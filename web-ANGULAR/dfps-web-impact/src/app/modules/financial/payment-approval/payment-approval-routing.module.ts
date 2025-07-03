import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentApprovalComponent } from './payment-approval/payment-approval.component';
import { CanActivatePaymentApproval } from './guard/payment-approval.guard';

export const PaymentApprovalRoutes: Routes = [
  {
    path: 'financial/payment-approval',
    component: PaymentApprovalComponent,
    canActivate: [CanActivatePaymentApproval]
  },
];

@NgModule({
  imports: [RouterModule.forChild(PaymentApprovalRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class PaymentApprovalRoutingModule { }
