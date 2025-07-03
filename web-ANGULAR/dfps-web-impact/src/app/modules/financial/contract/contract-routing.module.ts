import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrgSearchComponent } from '@shared/org-search/org-search.component';
import { ResourceSearchComponent } from '@shared/resource-search/resource-search.component';
import { StaffSearchComponent } from '@shared/staff-search/staff-search.component';
import { DfpsDirtyCheckComponent, DfpsUnderconstructionComponent } from 'dfps-web-lib';
import { BudgetTransferComponent } from './budget-transfer/budget-transfer.component';
import { ContractHeaderComponent } from './contract-header/contract-header.component';
import { ContractPeriodComponent } from './contract-period/contract-period.component';
import { ContractServiceListComponent } from './contract-service-list/contract-service-list.component';
import { ContractServiceComponent } from './contract-service/contract-service.component';
import { ContractVersionComponent } from './contract-version/contract-version.component';
import { CostReimbursementComponent } from './cost-reimbursement/cost-reimbursement.component';
import { CanActivateContractHeader } from './guard/contract-header.guard';
import { ProviderBillingStaffComponent } from './provider-billing-staff/provider-billing-staff.component';
import { UnitRateDetailComponent } from './unit-rate-detail/unit-rate-detail.component';
import { ContractSearchComponent } from './contract-search/contract-search.component';

export const ContractRoutes: Routes = [
  {
    path: 'financial/contract',
    component: ContractSearchComponent,
  },
  {
    path: 'financial/contract',
    children: [
      {
        path: 'authprovider/:contractId',
        component: ProviderBillingStaffComponent,
      },
      {
        path: 'authprovider/:contractId/staffsearch',
        component: StaffSearchComponent,
      },
      {
        path: 'header/:contractId/:contractNumber',
        component: ContractHeaderComponent,
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: 'header/:contractId/:contractNumber/staffsearch',
        component: StaffSearchComponent,
      },
      {
        path: 'header/:contractId/:contractNumber/orgsearch',
        component: OrgSearchComponent,
      },
      {
        path: 'header/:contractId/:contractNumber/resourcesearch',
        component: ResourceSearchComponent,
      },
      {
        path: 'header/:contractId/:contractNumber/period/:period',
        component: ContractPeriodComponent,
        canActivate: [CanActivateContractHeader],
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: 'header/:contractId/:contractNumber/period/:period/version/:version',
        component: ContractVersionComponent,
        canActivate: [CanActivateContractHeader],
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: 'header/:contractId/:contractNumber/period/:period/version/:version/budget-transfer',
        component: BudgetTransferComponent,
        canDeactivate: [DfpsDirtyCheckComponent],
        canActivate: [CanActivateContractHeader]
      },
      {
        path: 'header/:contractId/:contractNumber/period/:period/version/:version/service',
        component: ContractServiceListComponent,
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: 'header/:contractId/:contractNumber/period/:period/version/:version/service/:contractServiceId',
        component: ContractServiceComponent,
        canDeactivate: [DfpsDirtyCheckComponent],
        canActivate: [CanActivateContractHeader]
      },
      {
        path: 'header/:contractId/:contractNumber/period/:period/version/:version/service/:contractServiceId',
        children: [
          {
            path: 'CRM',
            component: CostReimbursementComponent,
            canDeactivate: [DfpsDirtyCheckComponent]
          },
          {
            path: 'VUR',
            redirectTo: 'URT'
          },
          {
            path: 'URT',
            component: UnitRateDetailComponent,
            canDeactivate: [DfpsDirtyCheckComponent]
          },
        ]
      },
      {
        path: '**',
        component: DfpsUnderconstructionComponent,
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(ContractRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class ContractRoutingModule { }
