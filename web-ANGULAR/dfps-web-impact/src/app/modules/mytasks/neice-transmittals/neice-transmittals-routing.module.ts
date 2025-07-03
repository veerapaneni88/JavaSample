import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DfpsDirtyCheckComponent } from 'dfps-web-lib';
import { CanActivateNeiceTransmittalList } from './guard/neice-transmittal-list.guard';
import { CanActivateNeiceTransmittalSummary } from './guard/neice-transmittal-summary.guard';
import { NeiceTransmittalListComponent } from './neice-transmittal-list/neice-transmittal-list.component';
import { NeiceTransmittalSummaryComponent } from './neice-transmittal-summary/neice-transmittal-summary.component';

export const NeiceTransmittalRoutes: Routes = [
  {
    path: 'mytasks/neice-transmittals',
    component: NeiceTransmittalListComponent,
    canActivate: [CanActivateNeiceTransmittalList]

  },
  {
    path: 'mytasks/neice-transmittals/:idNeiceTransmittal/summary/:incomingOutgoing',
    component: NeiceTransmittalSummaryComponent,
    canActivate: [CanActivateNeiceTransmittalSummary],
    canDeactivate: [DfpsDirtyCheckComponent]
  }
];

@NgModule({
  imports: [RouterModule.forChild(NeiceTransmittalRoutes)],
  exports: [RouterModule]
})
export class NeiceTransmittalsRoutingModule { }
