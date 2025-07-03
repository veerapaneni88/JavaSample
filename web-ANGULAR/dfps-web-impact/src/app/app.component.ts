import {
  AfterViewInit,
  Component,
  ContentChild,
  ElementRef,
  HostListener,
  OnInit,
  QueryList,
  ViewChild,
  Inject,
} from '@angular/core';
import { Router } from '@angular/router';
import { NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { Idle } from '@ng-idle/core';
import { AuthService, NavigationService, DfpsSessionTimeoutComponent, ApiService } from 'dfps-web-lib';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { environment } from '../environments/environment';
import { MsalBroadcastService, MsalService, MSAL_GUARD_CONFIG, MsalGuardConfiguration } from '@azure/msal-angular';
import {
  EventMessage,
  EventType,
  InteractionStatus,
  RedirectRequest,
} from '@azure/msal-browser';
import { Subject, filter, takeUntil } from 'rxjs';
import { FormBuilder, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { ADAuthService } from './auth-service';

@Component({
  selector: 'dfps-app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit, AfterViewInit {
  @ContentChild(DfpsSessionTimeoutComponent) dfpsSessionTimeoutPopupComponent: QueryList<any>;
  @ViewChild('mainContent') mainContentRef: ElementRef;
  bsModalRef: BsModalRef;
  ngbModalOptions: NgbModalOptions = { size: 'sm' };

  idleState = 'Not started.';
  timedOut = false;
  lastPing?: Date = null;
  title = 'angular-idle-timeout';
  timer = environment.idleSessionDuration;
  disableTimeout = !environment.isMPSEnvironment;
  onLoadUrl: string;

  isIframe = false;
  loginDisplay = false;
  loginStatus: boolean = false;
  private readonly _destroying$ = new Subject<void>();
  userName;
  onPremId;

  userForm: any;

  impersonationDispFlag = false;

  interval;
  @HostListener('document:click', ['$event'])
  documentClick(event: any): void {
    if (this.disableTimeout) {
      clearInterval(this.interval);
      this.timerInterval(this.timer);
      this.reset();
    }
  }

  constructor(
    private modalService: BsModalService,
    private idle: Idle,
    private authService: AuthService,
    private adAuthService: ADAuthService,
    private msalAuthService: MsalService,
    private navigaitonService: NavigationService,
    private broadcastService: MsalBroadcastService,
    @Inject(MSAL_GUARD_CONFIG) private msalGuardConfig: MsalGuardConfiguration,
    private router: Router,
    private formBuilder: FormBuilder,
    private apiService: ApiService,
    private http: HttpClient,
    private cookieService: CookieService,
  ) {
    this.userForm = this.formBuilder.group({
      name: [''],
      impersonateId: ['', [Validators.required]],
    });
  }

  ngOnInit() {
    const swiLogoutUrl = this.cookieService.get('swiLogoutUrl');
    // this.cookieService.set("swiLogoutUrl",undefined, 0, "/", window.location.hostname, false, 'Lax');
    if (swiLogoutUrl) {
      // this.cookieService.deleteAll("/", window.location.hostname);
      clearListCookies();
      localStorage.clear();
      sessionStorage.clear();

      window.location.href = swiLogoutUrl;
      return;
    }
    this.isIframe = window !== window.parent && !window.opener; // Remove this line to use Angular Universal
    const refreshToke = this.authService.getRefreshToken();

    if (refreshToke) {
      this.loginStatus = true;
    }
    this.subscribeMsalEvents();
    if (this.disableTimeout && refreshToke) {
      this.navigaitonService.retriveUserData();
      this.timerInterval(this.timer);

      // sets an idle timeout of configured seconds.
      this.idle.setIdle(this.timer - 300);

      // sets a timeout period of 300 seconds(5 minutes). 300 seconds(5 minutes) of inactivity, the user will be considered timed out.
      this.idle.setTimeout(300);

      // sets the default interrupts, in this case, things like clicks, scrolls, touches to the document
      // this.idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

      this.idle.onIdleEnd.subscribe(() => {
        this.idleState = 'No longer idle.';
        this.reset();
      });

      this.idle.onTimeout.subscribe(() => {
        this.idleState = 'Timed out!';
        this.timedOut = true;
        this.authService.logoutAuthFlow('TIMED_OUT');
      });

      this.idle.onIdleStart.subscribe(() => {
        this.idleState = "You've gone idle!";
        this.modalService.show(DfpsSessionTimeoutComponent, { class: 'modal-sm modal-dialog-centered' });
      });

      this.idle.onTimeoutWarning.subscribe((countdown) => {
        this.idleState = 'You will time out in ' + countdown + ' seconds!';
      });

      this.reset();
    }
  }

  ngOnDestroy(): void {
    this._destroying$.next(undefined);
    this._destroying$.complete();
  }

  subscribeMsalEvents() {
    if (!environment.environmentName.startsWith('BSL')) {
      this.broadcastService.msalSubject$
        .pipe(
          filter((msg: EventMessage) => msg.eventType === EventType.ACQUIRE_TOKEN_FAILURE)
        )
        .subscribe((result: EventMessage) => {
          this.adAuthService.login();
        });

      this.broadcastService.inProgress$
        .pipe(
          filter((status: InteractionStatus) => status === InteractionStatus.None),
          takeUntil(this._destroying$)
        )
        .subscribe(() => {
          this.checkAndSetActiveAccount();
          this.setLoginDisplay();
        });
    }
  }

  checkAndSetActiveAccount() {
    let activeAccount = this.msalAuthService.instance.getActiveAccount();
    if (!activeAccount && this.msalAuthService.instance.getAllAccounts().length > 0) {
      let accounts = this.msalAuthService.instance.getAllAccounts();
      this.msalAuthService.instance.setActiveAccount(accounts[0]);
    }
  }

  setLoginDisplay() {
    const bearerToken = this.authService.getBearerToken();
    const ssoStatus = this.msalAuthService.instance.getAllAccounts().length > 0;
    if (ssoStatus) {
      if (!bearerToken) {
        const profile_data = this.msalAuthService.instance.getAllAccounts()[0];
        // Get token from dfps-security-auth server and update cookies.
        this.onPremId = profile_data.idTokenClaims?.onPremisesSamAccountName;
        this.userName = profile_data.idTokenClaims ? profile_data.idTokenClaims['name'] : null;
        this.cookieService.set('userName', profile_data.username, 10000, '/', undefined, undefined, 'Lax');
        if (environment.enableImpersonation) {
          this.router.navigate(['/impersonation']);
        } else {
          // get dfps oauth2 access token
          this.adAuthService.generateDfpsOauth2Token(this.onPremId);
        }
      } else {
        this.loginStatus = true;
      }
    } else {
      this.adAuthService.login();
    }
  }

  reset() {
    this.idle.watch();
    this.idleState = 'Started.';
    this.timedOut = false;
  }

  two(x) {
    return (x > 9 ? '' : '0') + x;
  }

  time(ms) {
    let t = '';
    let timeToShow = localStorage.getItem('timeToShow');
    if (timeToShow !== undefined && timeToShow === '') {
      timeToShow = '';
    }
    let sec = Math.floor(ms / 1000);
    let min = Math.floor(sec / 60);
    sec = sec % 60;
    t = this.two(sec);

    min = min % 60;
    t = this.two(min) + ':' + t;
    localStorage.setItem('timeToShow', t);
    return 'Your Session Will Time Out In ' + t;
  }

  timerInterval(defaultTime) {
    let time = defaultTime * 1000;
    this.interval = setInterval(() => {
      time -= 1000;
      window.status = this.time(time);
      if (time === 0) {
        window.status = 'Your session has been disconnected';
        clearInterval(this.interval);
      }
    }, 1000);
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.onLoadUrl = this.router.url;
    }, 0);
  }

  logout(){
    this.adAuthService.logout();
  }

  scrollToElement($element): void {
    $element.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' });
    this.mainContentRef.nativeElement.focus();
  }
}

function clearListCookies() {
  let cookies = document.cookie.split(";");
  for (let i = 0; i < cookies.length; i++) {
    let spcook = cookies[i].split("=");
    deleteCookie(spcook[0]);
  }
  function deleteCookie(cookiename) {
    let d = new Date();
    d.setDate(d.getDate() - 1);
    let expires = ";expires=" + d;
    let name = cookiename;
    //alert(name);
    let value = "";
    document.cookie = name + "=" + value + expires + "; path=/";
  }
}