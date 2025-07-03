import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  DfpsAddressValidator, DfpsButton,
  DfpsCheckBox,
  DfpsCollapsableSection,
  DfpsConfirm,
  DfpsDataTable,
  DfpsDataTable1,
  DfpsDataTableLazyload,
  DfpsDataTableLazyload1,
  DfpsDatePicker,
  DfpsDirtyCheck,
  DfpsFormLaunch,
  DfpsFormValidation,
  DfpsInput,
  DfpsMultiSelect,
  DfpsRadioButton,
  DfpsReportLaunch,
  DfpsSelect, DfpsTextarea,
  DfpsNarrative
} from 'dfps-web-lib';
import { SharedModule } from '../../shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { NeiceTransmittalsRoutingModule } from './neice-transmittals-routing.module';
import { NeiceTransmittalListComponent } from './neice-transmittal-list/neice-transmittal-list.component';
import { NeiceTransmittalsService } from './service/neice-transmittals.service';
import {CanActivateNeiceTransmittalSummary} from './guard/neice-transmittal-summary.guard';
import { NeiceTransmittalSummaryComponent } from './neice-transmittal-summary/neice-transmittal-summary.component';
import { NeiceAdditionalInfoComponent } from './neice-transmittal-summary/neice-additional-info/neice-additional-info.component';
import { CanActivateNeiceTransmittalList } from './guard/neice-transmittal-list.guard';
import { NeiceRejectInfoComponent } from './neice-transmittal-summary/neice-reject-info/neice-reject-info.component';


@NgModule({
  declarations: [NeiceTransmittalListComponent, NeiceTransmittalSummaryComponent, NeiceAdditionalInfoComponent, NeiceRejectInfoComponent],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    NeiceTransmittalsRoutingModule,
    DfpsButton,
    DfpsCheckBox,
    DfpsInput,
    DfpsCollapsableSection,
    DfpsDatePicker,
    DfpsConfirm,
    DfpsSelect,
    DfpsDataTableLazyload,
    DfpsDataTableLazyload1,
    DfpsDataTable,
    DfpsDataTable1,
    DfpsDirtyCheck,
    DfpsReportLaunch,
    DfpsFormValidation,
    DfpsTextarea,
    DfpsRadioButton
  ],
  exports: [

  ],
  providers:[
    NeiceTransmittalsService,
    CanActivateNeiceTransmittalSummary,
    CanActivateNeiceTransmittalList
  ]
})
export class NeiceTransmittalsModule { }
