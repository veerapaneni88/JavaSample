import { ServiceLevelDetailComponent } from './facility-detail/service-level-detail/service-level-detail.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlacementLogComponent } from './../case/placement-log/placement-log.component';
import { ClientCharacteristicsDetailComponent } from './client-characteristics-detail/client-characteristics-detail.component';
import { CareTakerDetailComponent } from './facility-detail/caretaker-detail/caretaker-detail.component';
import { AddServiceLevelDetailComponent } from './facility-detail/service-level-detail/add-service-level-detail.component';
import { MedicalConsenterComponent } from './placement-network/medical-consenter/medical-consenter.component';
import { ServicesByAreaDetailComponent } from './services-by-area-detail/services-by-area-detail.component';
import { ServicesByAreaComponent } from './services-by-area/services-by-area.component';
import { ResourceListComponent } from './resource-list/resource-list.component';
import { AddServicePackageCredentialsComponent } from './facility-detail/service-package-credentials/add-service-package-credentials.component';
import { ServicePackageCredentialsComponent } from './facility-detail/service-package-credentials/service-package-credentials.component';

export const ResourceRoutes: Routes = [
  {
    path: 'resource/services-by-area/:resourceId',
    component: ServicesByAreaComponent,
  },
  {
    path: 'resource/services-by-area/:resourceId/:id',
    component: ServicesByAreaDetailComponent,
  },
  {
    path: 'resource/services-by-area/:resourceId/:resourceServiceId/client-characteristics/:resourceCharacterId',
    component: ClientCharacteristicsDetailComponent,
  },
  {
    path: 'resource/placement-network/medical-consenter/:ssccResourceId/:memberResourceId/:ssccPlacmentLinkId',
    component: MedicalConsenterComponent,
  },
  {
    path: 'resource/facility-detail/add-service-level-detail/:resourceId',
    component: AddServiceLevelDetailComponent,
  },
  {
    path: 'resource/facility-detail/add-service-package-credential/:resourceId',
    component: AddServicePackageCredentialsComponent,
  },
  {
    path: 'resource/facility-detail/caretaker-detail/:resourceId/:caretakerId',
    component: CareTakerDetailComponent,
  },
  {
    path: 'resource/facility-detail/placement-log/:resourceId',
    component: PlacementLogComponent,
  },
  {
    path: 'resource/facility-detail/service-level-detail/:resourceId/:flocId',
    component: ServiceLevelDetailComponent,
  },
  {
    path: 'resource/facility-detail/service-package-credentials/:resourceId/:servicePackageHistoryId',
    component: ServicePackageCredentialsComponent,
  },
  {
    path: 'resource/resource-list',
    component: ResourceListComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(ResourceRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class ResourceRoutingModule { }
