import { DatePipe } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ErrorHandler, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CaseModule } from '@case/case.module';
import { FinancialModule } from '@financial/financial.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgIdleKeepaliveModule } from '@ng-idle/keepalive';
import { SharedModule } from '@shared/shared.module';
import { MytasksModule } from './modules/mytasks/mytasks.module';
import {
  ApiService,
  AuthService,
  DfpsAddress,
  DfpsAddressValidation,
  DfpsButton,
  DfpsDataTable,
  DfpsDataTable1, 
  DfpsDataTableLazyload,
  DfpsDataTableLazyload1,
  DfpsFooter,
  DfpsFormLaunch,
  DfpsFormValidation,
  DfpsGlobalMessage,
  DfpsHeader,
  DfpsInput,
  DfpsPrimaryNavigation,
  DfpsProgressBar,
  DfpsSecondaryNavigation,
  DfpsSelect,
  DfpsMultiSelect,
  DfpsSessionTimeout,
  DfpsTernaryNavigation,
  DfpsTextarea,
  DfpsUnauthorized,
  DfpsUnderconstruction,
  ENVIRONMENT_SETTINGS,
  GlobalErrorHandler,
  GlobalMessageServcie,
  HttpSuccessResponseInterceptor,
  LoaderService,
  LogService,
  NavigationService,
  RedirectService,
  ReportService,
  FormService,
  VALIDATOR_ERRORS,
  NarrativeService
} from 'dfps-web-lib';
import { environment } from 'environments/environment';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { BsModalRef, ModalModule } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { dfpsValidatorErrors } from '../messages/validator-errors';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HelpService } from './common/impact-help.service';
import { PersonModule } from './modules/person/person.module';
import { ResourceModule } from './modules/resource/resource.module';
import { SearchModule } from './modules/search/search.module';
import { MsalConfiguration } from './auth-config';
import { ADAuthService } from './auth-service'

import { HttpRequestInterceptor } from './common/interceptors/http-request-interceptor'

import {
  MsalModule, MsalRedirectComponent, MsalInterceptor, MsalGuard, MSAL_GUARD_CONFIG, MSAL_INSTANCE, MSAL_INTERCEPTOR_CONFIG, MsalBroadcastService, MsalService,
  MsalGuardConfiguration, MsalInterceptorConfiguration
} from '@azure/msal-angular'; // MsalGuard added to imports
import { PublicClientApplication, InteractionType, IPublicClientApplication } from '@azure/msal-browser'; // InteractionType added to imports
import { HttpErrorResponseInterceptor } from './common/interceptors/http-error-interceptor';
import { ImpactImpersonationComponent } from './impact-impersonation/impact-impersonation.component';

export function MSALInstanceFactory(msalConfig : MsalConfiguration): IPublicClientApplication {
  const config = msalConfig.getConfig();
  if(!config){
    return new PublicClientApplication({
      auth: {
        clientId: 'bsl-Fclient-id',
        authority: 'https://login.microsoft.com',
        redirectUri: '/'
      }
    });
  }
  return new PublicClientApplication(config);
}
// MSAL Interceptor is required to request access tokens in order to access the protected resource (Graph)
export function MSALInterceptorConfigFactory(): MsalInterceptorConfiguration {
  const protectedResourceMap = new Map<string, Array<string>>();
  protectedResourceMap.set('https://graph.microsoft.com/v1.0/me', ['user.read']);

  return {
    interactionType: InteractionType.Redirect,
    protectedResourceMap
  };
}

// MSAL Guard is required to protect routes and require authentication before accessing protected routes
export function MSALGuardConfigFactory(): MsalGuardConfiguration {
  return {
    interactionType: InteractionType.Redirect,
    authRequest: {
      scopes: ['user.read']
    }
  };
}

@NgModule({
  declarations: [
    AppComponent,
    ImpactImpersonationComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    SharedModule,
    AppRoutingModule,
    NgbModule,
    FinancialModule,
    CaseModule,
    PersonModule,
    SearchModule,
    ResourceModule,
    SharedModule,
    MytasksModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    ModalModule.forRoot(),
    AccordionModule.forRoot(),
    NgIdleKeepaliveModule.forRoot(),
    DfpsProgressBar,
    DfpsSessionTimeout,
    DfpsUnauthorized,
    DfpsUnderconstruction,
    DfpsSelect,
    DfpsMultiSelect,
    DfpsFormLaunch,
    DfpsButton,
    DfpsDataTableLazyload,
    DfpsDataTable,
    DfpsDataTableLazyload1,
    DfpsDataTable1,
    DfpsFooter,
    DfpsHeader,
    DfpsPrimaryNavigation,
    DfpsSecondaryNavigation,
    DfpsTernaryNavigation,
    DfpsGlobalMessage,
    DfpsFormValidation,
    DfpsAddress,
    DfpsAddressValidation,
    DfpsTextarea,
    DfpsInput,
    MsalModule
  ],
  providers: [
    MsalConfiguration,
    // { provide: HTTP_INTERCEPTORS, useClass: MsalInterceptor, multi: true },
    { provide: MSAL_INSTANCE, useFactory: MSALInstanceFactory, deps: [MsalConfiguration] },
    { provide: MSAL_GUARD_CONFIG, useFactory: MSALGuardConfigFactory },
    { provide: MSAL_INTERCEPTOR_CONFIG, useFactory: MSALInterceptorConfigFactory },
    MsalService,
    MsalGuard,
    MsalBroadcastService,
    { provide: HTTP_INTERCEPTORS, useClass: HttpRequestInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: HttpSuccessResponseInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorResponseInterceptor, multi: true },
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    { provide: ENVIRONMENT_SETTINGS, useValue: environment },
    { provide: VALIDATOR_ERRORS, useValue: dfpsValidatorErrors },
    { provide: DatePipe },
    LogService,
    AuthService,
    NavigationService,
    CookieService,
    BsModalRef,
    LoaderService,
    RedirectService,
    GlobalMessageServcie,
    ApiService,
    RedirectService,
    ReportService,
    FormService,
    LoaderService,
    NarrativeService,
    HelpService,
    ADAuthService
  ],
  entryComponents: [
    DfpsSessionTimeout
  ],
  bootstrap: [AppComponent, MsalRedirectComponent]
})
export class AppModule {
}
