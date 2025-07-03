import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  DfpsButton,
  DfpsCheckBox,
  DfpsCollapsableSection,
  DfpsConfirm,
  DfpsDataTable, DfpsDataTable1, DfpsDataTableLazyload, DfpsDataTableLazyload1,
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
import { ClientCharacteristicsDetailComponent } from './client-characteristics-detail/client-characteristics-detail.component';
import { CareTakerDetailComponent } from './facility-detail/caretaker-detail/caretaker-detail.component';
import { AddServiceLevelDetailComponent } from './facility-detail/service-level-detail/add-service-level-detail.component';
import { ServiceLevelDetailComponent } from './facility-detail/service-level-detail/service-level-detail.component';
import { AddServicePackageCredentialsComponent } from './facility-detail/service-package-credentials/add-service-package-credentials.component';
import { ServicePackageCredentialsComponent } from './facility-detail/service-package-credentials/service-package-credentials.component';
import { MedicalConsenterComponent } from './placement-network/medical-consenter/medical-consenter.component';
import { ResourceListComponent } from './resource-list/resource-list.component';
import { ResourceRoutingModule } from './resource-routing.module';
import { ResourceService } from './service/resource.service';
import { ServicesByAreaDetailComponent } from './services-by-area-detail/services-by-area-detail.component';
import { ServicesByAreaComponent } from './services-by-area/services-by-area.component';

@NgModule({
  declarations: [
    ServicesByAreaComponent, ServicesByAreaDetailComponent, ClientCharacteristicsDetailComponent, MedicalConsenterComponent,
    AddServiceLevelDetailComponent, CareTakerDetailComponent, ServiceLevelDetailComponent, ResourceListComponent, AddServicePackageCredentialsComponent, ServicePackageCredentialsComponent],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    ResourceRoutingModule,
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
    ResourceService
  ]
})
export class ResourceModule { }
