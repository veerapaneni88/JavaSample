import { FinancialAcccountRegisterDetailComponent } from './financial-account-register-detail/financial-account-register-detail.component';
import { ModalModule } from 'ngx-bootstrap/modal';
import { FinancialAccountService } from './service/financial-account.service';
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
  DfpsAddressValidator,
  DfpsRadioButton
} from 'dfps-web-lib';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { SharedModule } from '../../shared/shared.module';
import { FinancialAccountReportComponent } from './financial-account-report/financial-account-report.component';
import { FinancialAccountSearchComponent } from './financial-account-search/financial-account-search.component';
import { FinancialAccountRoutingModule } from './financial-account.routing.module';
import { FinancialAccountDetailComponent } from './financial-account-detail/financial-account-detail.component';
import { FinancialAcccountRegisterComponent } from './financial-account-register/financial-account-register.component';
import { RegionalAccountDetailComponent } from './regional-account-detail/regional-account-detail.component';
import { CanActivateFinancialAccountSearch } from './guard/financial-account-search.guard';
import { CanActivateFinancialAccount } from './guard/financial-account-details.guard';

@NgModule({
  declarations: [
    FinancialAccountReportComponent,
    FinancialAccountDetailComponent,
    FinancialAcccountRegisterComponent,
    FinancialAcccountRegisterDetailComponent,
    FinancialAccountSearchComponent,
    RegionalAccountDetailComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    ModalModule.forRoot(),
    AccordionModule.forRoot(),
    FinancialAccountRoutingModule,
    DfpsAddressValidator,
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
    FinancialAccountService,
    CanActivateFinancialAccountSearch,
    CanActivateFinancialAccount
  ]
})
export class FinancialAccountsModule { }
