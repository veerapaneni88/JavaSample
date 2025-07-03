import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TrainingDetailComponent } from './training/training-detail.component';
import { DfpsDirtyCheckComponent } from 'dfps-web-lib';

export const PersonRoutes: Routes = [
  {
    path: 'case/person/:personId/training/:trainingId',
    component: TrainingDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  }
];

@NgModule({
  imports: [RouterModule.forChild(PersonRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class PersonRoutingModule { }
