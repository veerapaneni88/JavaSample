import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

export const MytaskRoutes: Routes = [
  
];

@NgModule({
  imports: [RouterModule.forChild(MytaskRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class MytasksRoutingModule { }