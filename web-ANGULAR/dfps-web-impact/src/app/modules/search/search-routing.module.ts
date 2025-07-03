import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeSearchComponent } from './home-search/home-search.component';
import { FadHomeComponent } from './home/fad/fad-home.component';
import { KinHomeComponent } from './home/kin/kin-home.component';
import { ResourceSearchComponent } from '../shared/resource-search/resource-search.component';
import { ServicePackageErrorsComponent } from './service-package-errors/service-package-errors.component';
import { CentralRegistryPersonSearchComponent } from './central-registry-person-search/central-registry-person-search.component';
import { CentralRegistryCheckRequestDetailComponent } from './central-registry-check-request-detail/central-registry-check-request-detail/central-registry-check-request-detail.component';
import { CentralRegistryRecordCheckDetailComponent } from './central-registry-record-check-detail/central-registry-record-check-detail.component';
import { ServiceLevelErrorComponent } from './service-level-error/service-level-error.component';

export const SearchRoutes: Routes = [
  {
    path: 'search/home-search',
    component: HomeSearchComponent,
  },
  {
    path: 'search/home-search/add-fad',
    component: FadHomeComponent,
  },
  {
    path: 'search/home-search/add-kin',
    component: KinHomeComponent,
  },
  {
    path: 'resource/displayResourceSearch',
    component: ResourceSearchComponent,
  },
  {
    path: 'search/service-package-errors',
    component: ServicePackageErrorsComponent,
  },
  {
    path: 'search/central-registry-person-search',
    component: CentralRegistryPersonSearchComponent,
  },
  {
    path: 'search/central-registry-person-search',
    children: [
      {
        path: 'centralRegistryRequestDetail',
        component: CentralRegistryCheckRequestDetailComponent,
      },
      {
        path: 'centralRegistryRequestDetail/:primReqId',
        component: CentralRegistryCheckRequestDetailComponent,
      },
      {
        path: 'centralRegistryRecordCheckDetail',
        component: CentralRegistryRecordCheckDetailComponent,
      }
    ]
  },
  {
    path: 'search/service-level-error',
    component: ServiceLevelErrorComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(SearchRoutes)],
  exports: [RouterModule],
  providers: [],
})

export class SearchRoutingModule { }
