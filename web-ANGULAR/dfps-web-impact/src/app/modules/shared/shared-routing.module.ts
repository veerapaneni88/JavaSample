import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

export const SharedRoutes: Routes = [
 
];

@NgModule({
  imports: [RouterModule.forChild(SharedRoutes)],
  exports: [RouterModule],
  providers: [],
})

export class SharedRoutingModule { }