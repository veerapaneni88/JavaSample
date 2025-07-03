import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MSAL_GUARD_CONFIG, MsalBroadcastService, MsalGuardConfiguration, MsalService } from '@azure/msal-angular';
import {
    AccountInfo,
    AuthenticationResult,
    CacheLookupPolicy,
    InteractionRequiredAuthError,
    InteractionStatus,
    RedirectRequest,
    SilentRequest,
} from '@azure/msal-browser';
import { BehaviorSubject, Subject, from, lastValueFrom, of } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { catchError, filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { CookieService } from 'ngx-cookie-service';
import { environment } from '../environments/environment';
import { ApiService, AuthService } from 'dfps-web-lib';

@Injectable({
    providedIn: 'root',
})
export class ADAuthService implements OnDestroy {
    private readonly _destroy$ = new Subject<void>();
    private loggedIn = new BehaviorSubject<boolean>(false);

    constructor(
        private msalService: MsalService,
        private msalBroadCastService: MsalBroadcastService,
        private router: Router,
        private http: HttpClient,
        @Inject(MSAL_GUARD_CONFIG)
        private msalGuardConfig: MsalGuardConfiguration,
        private apiService: ApiService,
        private authService: AuthService,
        private msalAuthService: MsalService,
        private cookieService: CookieService
    ) {
        // Listen for authentication state changes
        if (!environment.environmentName.startsWith('BSL')) {
            this.msalBroadCastService.inProgress$
                .pipe(
                    filter((status: InteractionStatus) => status === InteractionStatus.None),
                    takeUntil(this._destroy$)
                )
                .subscribe(() => {
                    this.checkAndSetActiveAccount();
                });
        }
    }

    login(): void {
        if (this.msalGuardConfig.authRequest) {
            this.msalService.loginRedirect({
                ...this.msalGuardConfig.authRequest,
            } as RedirectRequest);
        } else this.msalService.loginRedirect();
    }

    logout(): void {
        this.authService.logoutAuthFlow();
    }

    isLoggedIn(): Observable<boolean> {
        return this.loggedIn.asObservable();
    }

    private checkAndSetActiveAccount(): void {
        const accounts = this.msalService.instance.getAllAccounts();
        if (accounts.length > 0) {
            this.loggedIn.next(true);
        } else {
            this.loggedIn.next(false);
        }
    }

    silentTokenLogin(): Observable<boolean> {
        return this.acquireSilentToken().pipe(
            switchMap((result: AuthenticationResult | null) => {
                if (result) {
                    this.handleSuccessfulLogin(result.account);
                    return of(true);
                } else {
                    return this.signInWithRedirect();
                }
            }),
            catchError((error) => {
                if (error instanceof InteractionRequiredAuthError) {
                    return this.signInWithRedirect();
                }
                return of(false);
            })
        );
    }

    private signInWithRedirect(): Observable<boolean> {
        const redirectRequest: RedirectRequest = {
            scopes: ['user.read'],
        };

        return from(this.msalService.instance.acquireTokenRedirect(redirectRequest)).pipe(
            map(() => true),
            catchError(() => {
                return of(false);
            })
        );
    }

    private handleSuccessfulLogin(account: AccountInfo | null) {
        if (account) {
            this.loggedIn.next(true);
        }
    }

    async getIdToken(): Promise<string | null> {
        try {
            const accounts = this.msalService.instance.getAllAccounts();
            if (accounts.length === 0) {
                // No accounts found, initiate login
                await this.msalService.loginPopup();
            }
            const request = {
                scopes: ['openid', 'profile', 'email', 'offline_access'], // Adjust scopes as needed
                account: accounts[0], // Use the first account found
                forceRefresh: true,
                cacheLookupPolicy: CacheLookupPolicy.Default,
            };
            const response = await lastValueFrom(this.msalService.acquireTokenSilent(request));
            return response.idToken;
        } catch (error) {
            console.error('Error acquiring ID token:', error);
            return null;
        }
    }

    private acquireSilentToken(): Observable<AuthenticationResult | null> {
        const accounts = this.msalService.instance.getAllAccounts();

        if (accounts.length > 0) {
            const silentRequest: SilentRequest = {
                scopes: ['user.read'],
                account: accounts[0],
                forceRefresh: true,
                cacheLookupPolicy: CacheLookupPolicy.Default,
            };

            return from(this.msalService.instance.acquireTokenSilent(silentRequest)).pipe(
                catchError((error) => {
                    if (error instanceof InteractionRequiredAuthError) {
                        return of(null);
                    }

                    return of(null);
                })
            );
        }

        return of(null);
    }

    ngOnDestroy(): void {
        console.log('Service is destroyed');
        this._destroy$.next(undefined);
        this._destroy$.complete();
    }

    generateDfpsOauth2Token(_onPremId: any, _imperId?: string) {
        const profile_data = this.msalAuthService.instance.getAllAccounts()[0];
        const onPremID: any = profile_data.idTokenClaims?.onPremisesSamAccountName;
        this.getIdToken().then((token) => {
            const clientId = 'AZURE_IMPACT_CLIENT';
            const clientSecret = 'AZURE_IMPACT_SECRET';
            const headers = new HttpHeaders({
                'Content-Type': 'application/x-www-form-urlencoded',
                Authorization: 'Basic QVpVUkVfSU1QQUNUX0NMSUVOVDpBWlVSRV9JTVBBQ1RfU0VDUkVU',
            });
            const body = new URLSearchParams();
            body.set('client_id', clientId);
            body.set('client_secret', clientSecret);
            body.set('scope', 'IMPACT FORMS');
            body.set('username', onPremID ? onPremID : '');
            body.set('password', token);
            if (_imperId) {
                body.set('impersonation_id', _imperId);
            }
            body.set('grant_type', 'password');
            this.http.post(environment.jwtTokenEndpoint, body.toString(), { headers }).pipe(
                tap(
                    response => {
                        this.authService.storeTokens(response);
                        let ph2RedirectUrl = '';
                        if (environment.environmentName === 'MPS') {
                            ph2RedirectUrl =  environment.impactP2WebUrl + '/mobile/authenticateUser';
                        } else {
                            ph2RedirectUrl = environment.impactP2WebUrl  + '/mytasks/mytask';
                        }
                        window.location.href = ph2RedirectUrl;
                    }
                )
            ).subscribe();
        })
    }
}