import { ContractHeaderComponent } from './modules/financial/contract/contract-header/contract-header.component';
import { InjectionToken, NgModule } from '@angular/core';
import { ActivatedRouteSnapshot, RouterModule, Routes } from '@angular/router';
import { DfpsUnauthorizedComponent } from 'dfps-web-lib';
import { BrowserUtils } from "@azure/msal-browser";
import { MsalGuard } from '@azure/msal-angular';
import { ImpactImpersonationComponent } from './impact-impersonation/impact-impersonation.component';

const externalUrlProvider = new InjectionToken('externalUrlRedirectResolver');

const APP_ROUTES: Routes = [
  // {
  //   path: 'financial',
  //   loadChildren: () => import('./modules/financial/financial.module').then(m => m.FinancialModule)
  // },
  // {
  //   path: 'case',
  //   loadChildren: () => import('./modules/case/case.module').then(m => m.CaseModule)
  // },
  {
    path: 'unauthorized',
    component: DfpsUnauthorizedComponent,
    canActivate: [externalUrlProvider]
  },
  {
    path: 'externalRedirect',
    component: ContractHeaderComponent,
    canActivate: [externalUrlProvider]
  },
  {
    path: 'impersonation',
    component: ImpactImpersonationComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(APP_ROUTES, {
    // Don't perform initial navigation in iframes or popups
    initialNavigation: !BrowserUtils.isInIframe() && !BrowserUtils.isInPopup() ? "enabledNonBlocking" :
      "disabled", // Set to enabledBlocking to use Angular Universal
  })],
  exports: [RouterModule],
  providers: [
    {
      provide: externalUrlProvider,
      useValue: (route: ActivatedRouteSnapshot) => {
        const externalUrl = route.paramMap.get('externalUrl');
        window.open(externalUrl, '_self');
      },
    }
  ],
})
export class AppRoutingModule { }
