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
  DfpsMultiSelect,
  DfpsRadioButton
} from 'dfps-web-lib';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { SharedRoutingModule } from './shared-routing.module';
import { StaffSearchComponent } from './staff-search/staff-search.component';
import { ResourceSearchComponent } from './resource-search/resource-search.component';
import { OrgSearchComponent } from './org-search/org-search.component';
import { HomeService } from '../search/service/home.service';

@NgModule({
  declarations: [
    StaffSearchComponent,
    ResourceSearchComponent,
    OrgSearchComponent
  ],
  imports: [
    SharedRoutingModule,
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
    DfpsFormValidation
  ],
  exports: [
    StaffSearchComponent,
    ResourceSearchComponent  ],
  providers: [
    HomeService
  ]
})
export class SharedModule { }
