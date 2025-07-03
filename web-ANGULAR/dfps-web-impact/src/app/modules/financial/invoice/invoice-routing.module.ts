import { CanActivateContractHeader } from './../contract/guard/contract-header.guard';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DfpsUnderconstructionComponent, DfpsDirtyCheckComponent } from 'dfps-web-lib';
import { InvoiceSearchComponent } from './invoice-search/invoice-search.component';
import { DeliveredServiceComponent } from './delivered-service/delivered-service.component';
import { FosterCareComponent } from './foster-care/foster-care.component';
import { AdministrativeDetailComponent } from './administrative-detail/administrative-detail.component';
import { InvoiceHeaderComponent } from './invoice-header/invoice-header.component';
import { RejectionReasonComponent } from './rejection-reason/rejection-reason.component';
import { CanActivateInvoiceSearch } from './guard/invoice-search.guard';
import { CostReimbursementDetailComponent } from './cost-reimbursement-detail/cost-reimbursement-detail.component';
import {CanActivateInvoiceHeader} from '@financial/invoice/guard/invoice-header.guard';

export const InvoiceRoutes: Routes = [
  {
    path: 'financial/invoice',
    children: [
      {
        path: '',
        component: InvoiceSearchComponent,
        canActivate : [CanActivateInvoiceSearch],
      },
      {
        path: 'header/:invoiceId/delivered-service/:deliveredServiceId',
        component: DeliveredServiceComponent,
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: 'header/:invoiceId/rejection-reason/:rejectItemId',
        component: RejectionReasonComponent,
      },
      {
        path: 'header/:invoiceId/foster-care/:fosterCareId',
        component: FosterCareComponent,
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: 'header/:invoiceId/administrative/:id',
        component: AdministrativeDetailComponent,
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: 'header/:invoiceId',
        component: InvoiceHeaderComponent,
        canDeactivate: [DfpsDirtyCheckComponent],
        // canActivate: [CanActivateInvoiceHeader]
      },
      {
        path: 'header/:invoiceId/cost-reimbursement/:id/:service/:lineItem/:lineItemServicesQuantity',
        component: CostReimbursementDetailComponent,
        canDeactivate: [DfpsDirtyCheckComponent]
      },
      {
        path: '**',
        component: DfpsUnderconstructionComponent,
      }
    ]
  },
];

@NgModule({
    imports: [RouterModule.forChild(InvoiceRoutes)],
    exports: [RouterModule],
    providers: [],
})
export class InvoiceRoutingModule {}
