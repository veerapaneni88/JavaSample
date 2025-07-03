import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
export const FinancialRoutes: Routes = [

];

@NgModule({
  imports: [RouterModule.forChild(FinancialRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class FinancialRoutingModule { }
