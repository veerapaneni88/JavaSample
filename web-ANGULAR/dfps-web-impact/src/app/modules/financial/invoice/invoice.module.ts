import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  DfpsButton,
  DfpsCheckBox,
  DfpsCollapsableSection,
  DfpsConfirm,
  DfpsDataTable,
  DfpsDataTableLazyload,
  DfpsDatePicker,
  DfpsDirtyCheck,
  DfpsFormValidation,
  DfpsInput,
  DfpsReportLaunch,
  DfpsSelect,
  DfpsTextarea,
  DfpsRadioButton
} from 'dfps-web-lib';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { InvoiceSearchComponent } from './invoice-search/invoice-search.component';
import { InvoiceService } from './service/invoice.service';
import { InvoiceRoutingModule } from './invoice-routing.module';
import { SharedModule } from '@shared/shared.module';
import { DeliveredServiceComponent } from './delivered-service/delivered-service.component';
import { FosterCareComponent } from './foster-care/foster-care.component';
import { AdministrativeDetailComponent } from './administrative-detail/administrative-detail.component';
import { InvoiceHeaderComponent } from './invoice-header/invoice-header.component';
import { CostReimbursementDetailComponent } from './cost-reimbursement-detail/cost-reimbursement-detail.component';
import { RejectionReasonComponent } from './rejection-reason/rejection-reason.component';
import { CanActivateInvoiceSearch } from './guard/invoice-search.guard';
import {CanActivateInvoiceHeader} from '@financial/invoice/guard/invoice-header.guard';

@NgModule({
  declarations: [
    InvoiceSearchComponent,
    DeliveredServiceComponent,
    InvoiceSearchComponent,
    FosterCareComponent,
    AdministrativeDetailComponent,
    InvoiceHeaderComponent,
    RejectionReasonComponent,
    CostReimbursementDetailComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    InvoiceRoutingModule,
    DfpsButton,
    DfpsCheckBox,
    DfpsInput,
    DfpsCollapsableSection,
    DfpsDatePicker,
    DfpsConfirm,
    DfpsSelect,
    DfpsDataTableLazyload,
    DfpsDataTable,
    DfpsDirtyCheck,
    DfpsReportLaunch,
    DfpsFormValidation,
    DfpsTextarea,
    DfpsRadioButton
  ],
  exports: [
  ],
  providers: [
    InvoiceService,
    CanActivateInvoiceSearch,
    CanActivateInvoiceHeader
  ]
})
export class InvoiceModule { }
