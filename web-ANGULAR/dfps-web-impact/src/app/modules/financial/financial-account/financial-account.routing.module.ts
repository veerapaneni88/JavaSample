import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FinancialAccountReportComponent } from './financial-account-report/financial-account-report.component';
import { FinancialAccountSearchComponent } from './financial-account-search/financial-account-search.component';
import { DfpsDirtyCheckComponent } from 'dfps-web-lib';
import { FinancialAccountDetailComponent } from './financial-account-detail/financial-account-detail.component';
import { FinancialAcccountRegisterComponent } from './financial-account-register/financial-account-register.component';
import { RegionalAccountDetailComponent } from './regional-account-detail/regional-account-detail.component';
import { CanActivateFinancialAccountSearch } from './guard/financial-account-search.guard';
import { CanActivateFinancialAccount } from './guard/financial-account-details.guard';
import { FinancialAcccountRegisterDetailComponent } from './financial-account-register-detail/financial-account-register-detail.component';

export const FinancialAccountRoutes: Routes = [
  {
    path: 'financial/financial-account',
    redirectTo: 'financial/financial-account/search',
    pathMatch: 'full'
  },
  {
    path: 'financial/financial-account/search',
    component: FinancialAccountSearchComponent,
    canActivate: [CanActivateFinancialAccountSearch],
  },
  {
    path: 'financial/financial-account/report',
    component: FinancialAccountReportComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'financial/financial-account/header/:accountId',
    component: FinancialAccountDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent],
    canActivate : [CanActivateFinancialAccount],
  },
  {
    path: 'financial/financial-account/register/:accountId',
    component: FinancialAcccountRegisterComponent
  },
  {
    path: 'financial/financial-account/register/:accountId/:transactionId',
    component: FinancialAcccountRegisterDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'financial/financial-account/regional/:accountNumber/:status',
    component: RegionalAccountDetailComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(FinancialAccountRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class FinancialAccountRoutingModule { }
