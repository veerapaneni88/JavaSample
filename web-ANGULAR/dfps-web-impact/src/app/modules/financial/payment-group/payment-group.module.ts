import { PaymentGroupSearchComponent } from './payment-group-search/payment-group-search.component';
import { PaymentGroupDetailComponent } from './payment-group-detail/payment-group-detail.component';
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
  DfpsRadioButton, 
  DfpsReportLaunch,
  DfpsSelect,
  DfpsTextarea
} from 'dfps-web-lib';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { SharedModule } from '../../shared/shared.module';
import { PaymentGroupRoutingModule } from './payment-group.routing.module';
import { PaymentProcessComponent } from './payment-process/payment-process.component';
import { ReconcileTransactionComponent } from './reconcile-transaction/reconcile-transaction.component';
import { PaymentGroupService } from './service/payment-group.service';
import { TransactionSearchComponent } from './transaction-search/transaction-search.component';
import { PaymentProcessConfirmationComponent } from './payment-process/payment-process-confirmation/payment-process-confirmation.component';

@NgModule({
  declarations: [
    PaymentGroupSearchComponent,
    PaymentGroupDetailComponent,
    PaymentProcessComponent,
    ReconcileTransactionComponent,
    TransactionSearchComponent,
    PaymentProcessConfirmationComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    PaymentGroupRoutingModule,
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
    PaymentGroupService
  ]
})
export class PaymentGroupModule { }
