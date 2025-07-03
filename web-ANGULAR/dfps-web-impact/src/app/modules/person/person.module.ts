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
import { PersonRoutingModule } from './person-routing.module';
import { TrainingDetailComponent } from './training/training-detail.component';
import { PersonTrainingService } from './service/persontraining.service';

@NgModule({
  declarations: [
    TrainingDetailComponent],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    PersonRoutingModule,
    FormsModule,
    AccordionModule,
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
    PersonTrainingService
  ]
})
export class PersonModule { }
