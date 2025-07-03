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
  DfpsDataTable1,
  DfpsDataTableLazyload1,
  DfpsDatePicker,
  DfpsDirtyCheck,
  DfpsFormValidation,
  DfpsInput,
  DfpsRadioButton,
  DfpsReportLaunch,
  DfpsSelect,
  DfpsTextarea
} from 'dfps-web-lib';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { SharedModule } from '../shared/shared.module';
import { InvoiceModule } from './invoice/invoice.module';
import { ContractModule } from './contract/contract.module';
import { PaymentApprovalModule } from './payment-approval/payment-approval.module';
import { FinancialAccountsModule } from './financial-account/financial-accounts.module';
import { PaymentHistoryModule } from './payment-history/payment-history.module';
import { PaymentGroupModule } from './payment-group/payment-group.module';
import { FinancialRoutingModule } from './financial-routing.module';
import { HomeComponent } from './home/home.component';

@NgModule({
  declarations: [
  HomeComponent],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    InvoiceModule,
    ContractModule,
    PaymentApprovalModule,
    FinancialAccountsModule,
    PaymentHistoryModule,
    PaymentGroupModule,
    FinancialRoutingModule,
    DfpsButton,
    DfpsCheckBox,
    DfpsInput,
    DfpsCollapsableSection,
    DfpsDatePicker,
    DfpsConfirm,
    DfpsSelect,
    DfpsDataTableLazyload,
    DfpsDataTable,
    DfpsDataTable1,
    DfpsDataTableLazyload1,
    DfpsDirtyCheck,
    DfpsReportLaunch,
    DfpsFormValidation,
    DfpsTextarea,
    DfpsRadioButton
  ],
  exports: [
  ],
  providers: [
  ]
})
export class FinancialModule { }
