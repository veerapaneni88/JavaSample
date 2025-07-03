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
  DfpsMultiSelect,
  DfpsRadioButton,
  DfpsReportLaunch,
  DfpsSelect,
  DfpsAddressValidator,
  DfpsTextarea,
  DfpsFormLaunch
} from 'dfps-web-lib';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { HomeSearchComponent } from './home-search/home-search.component';
import { FadHomeComponent } from './home/fad/fad-home.component';
import { SearchRoutingModule } from './search-routing.module';
import { HomeService } from './service/home.service';
import { KinHomeComponent } from './home/kin/kin-home.component';
import { ServicePackageErrorsComponent } from './service-package-errors/service-package-errors.component';
import { ServicePackageErrorService } from './service/service-package-error.service';
import { CentralRegistryPersonSearchComponent } from './central-registry-person-search/central-registry-person-search.component';
import { CentralRegistryCheckRequestDetailComponent } from './central-registry-check-request-detail/central-registry-check-request-detail/central-registry-check-request-detail.component';
import { CentralRegistryRecordCheckDetailComponent } from './central-registry-record-check-detail/central-registry-record-check-detail.component';
import { ServiceLevelErrorComponent } from './service-level-error/service-level-error.component';
import { ServiceLevelErrorService } from './service/service-level-error.service';

@NgModule({
  declarations: [
    HomeSearchComponent,
    FadHomeComponent,
    KinHomeComponent,
    ServicePackageErrorsComponent,
    KinHomeComponent,
    CentralRegistryPersonSearchComponent,
    CentralRegistryCheckRequestDetailComponent,
    CentralRegistryRecordCheckDetailComponent,
    ServiceLevelErrorComponent
  ],
  imports: [
    SearchRoutingModule,
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    DfpsButton,
    DfpsCheckBox,
    DfpsRadioButton,
    DfpsInput,
    DfpsCollapsableSection,
    DfpsDatePicker,
    DfpsConfirm,
    DfpsSelect,
    DfpsMultiSelect,
    DfpsDataTableLazyload,
    DfpsDataTable,
    DfpsDirtyCheck,
    DfpsReportLaunch,
    DfpsFormValidation,
    DfpsAddressValidator,
    DfpsTextarea,
    DfpsFormLaunch
  ],
  exports: [],
  providers: [
    HomeService,
    ServicePackageErrorService,
    ServiceLevelErrorService
  ]
})
export class SearchModule { }
