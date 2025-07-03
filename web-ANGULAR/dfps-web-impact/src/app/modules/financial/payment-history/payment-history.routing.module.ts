import { PaymentHistorySearchComponent } from './payment-history-search/payment-history-search.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CanActivatePaymentHistory } from './guard/payment-history.guard';

export const PaymentHistoryRoutes: Routes = [
  {
    path: 'financial/payment-history',
    component: PaymentHistorySearchComponent,
    canActivate: [CanActivatePaymentHistory]
  },

];

@NgModule({
  imports: [RouterModule.forChild(PaymentHistoryRoutes)],
  exports: [RouterModule],
  providers: [CanActivatePaymentHistory],
})
export class PaymentHistoryRoutingModule { }