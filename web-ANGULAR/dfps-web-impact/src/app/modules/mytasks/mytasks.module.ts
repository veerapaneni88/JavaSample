import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MytasksRoutingModule } from './mytasks-routing.module';
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
import { SharedModule } from '../shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { MytasksService } from './service/mytasks.service';
import { NeiceTransmittalsModule } from './neice-transmittals/neice-transmittals.module';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    MytasksRoutingModule,
    NeiceTransmittalsModule,    
    DfpsButton,
    DfpsCheckBox,
    DfpsInput,
    DfpsCollapsableSection,
    DfpsDatePicker,
    DfpsConfirm,
    DfpsSelect,
    DfpsMultiSelect,
    DfpsDataTableLazyload,
    DfpsDataTable,
    DfpsDataTable1,
    DfpsDataTableLazyload1,
    DfpsDirtyCheck,
    DfpsReportLaunch,
    DfpsFormValidation,
    DfpsTextarea,
    DfpsRadioButton,
    DfpsFormLaunch,
    DfpsAddressValidator,
    DfpsNarrative
  ],
  exports: [
  ],
  providers: [
    MytasksService
  ]
})
export class MytasksModule { }
