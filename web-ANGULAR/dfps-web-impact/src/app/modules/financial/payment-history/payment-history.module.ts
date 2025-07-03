import { PaymentHistoryRoutingModule } from './payment-history.routing.module';
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
import { SharedModule } from '../../shared/shared.module';
import { PaymentHistorySearchComponent } from './payment-history-search/payment-history-search.component';

@NgModule({
  declarations: [PaymentHistorySearchComponent],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    PaymentHistoryRoutingModule,
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
  ]
})
export class PaymentHistoryModule { }
