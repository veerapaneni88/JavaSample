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
import { SharedModule } from 'primeng/api';
import { BudgetTransferComponent } from './budget-transfer/budget-transfer.component';
import { ContractHeaderComponent } from './contract-header/contract-header.component';
import { ContractPeriodComponent } from './contract-period/contract-period.component';
import { ValidateContractNumberComponent } from './contract-period/validate-contract-number/validate-contract-number.component';
import { ContractRoutingModule } from './contract-routing.module';
import { ContractSearchComponent } from './contract-search/contract-search.component';
import { ContractServiceListComponent } from './contract-service-list/contract-service-list.component';
import { ContractServiceComponent } from './contract-service/contract-service.component';
import { ContractVersionComponent } from './contract-version/contract-version.component';
import { CostReimbursementComponent } from './cost-reimbursement/cost-reimbursement.component';
import { CanActivateContractHeader } from './guard/contract-header.guard';
import { CanActivatePaymentApproval } from '../payment-approval/guard/payment-approval.guard';
import { ProviderBillingStaffComponent } from './provider-billing-staff/provider-billing-staff.component';
import { BudgetTransferService } from './service/budget-transfer.service';
import { ContractService } from './service/contract.service';
import { UnitRateDetailComponent } from './unit-rate-detail/unit-rate-detail.component';


@NgModule({
  declarations: [
    ContractSearchComponent,
    ContractHeaderComponent,
    ContractVersionComponent,
    ContractPeriodComponent,
    CostReimbursementComponent,
    ContractServiceComponent,
    ContractServiceListComponent,
    ValidateContractNumberComponent,
    BudgetTransferComponent,
    UnitRateDetailComponent,
    ProviderBillingStaffComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    ContractRoutingModule,
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
    ContractService,
    CanActivateContractHeader,
    CanActivatePaymentApproval,
    BudgetTransferService
  ]
})
export class ContractModule { }
