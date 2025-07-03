import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentGroupSearchComponent } from './payment-group-search/payment-group-search.component';
import { PaymentProcessComponent } from './payment-process/payment-process.component';
import { ReconcileTransactionComponent } from './reconcile-transaction/reconcile-transaction.component';
import { PaymentGroupDetailComponent } from './payment-group-detail/payment-group-detail.component';
import { TransactionSearchComponent } from './transaction-search/transaction-search.component';
import { DfpsDirtyCheckComponent } from 'dfps-web-lib';

export const PaymentGroupRoutes: Routes = [
    {
        path: 'financial/payments',
        redirectTo: 'financial/payments/search',
        pathMatch: 'full',
    },
    {
        path: 'financial/payments/search',
        component: PaymentGroupSearchComponent,
    },
    {
        path: 'financial/payments/process',
        component: PaymentProcessComponent,
    },
    {
        path: 'financial/payments/search/payment-group/:paymentGrpId',
        component: PaymentGroupDetailComponent,
    },
    {
        path: 'financial/payments/reconciliation',
        component: ReconcileTransactionComponent,
        canDeactivate: [DfpsDirtyCheckComponent],
    },
    {
        path: 'financial/payments/search/payment-group/:paymentGrpId/transaction-search',
        component: TransactionSearchComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(PaymentGroupRoutes)],
    exports: [RouterModule],
    providers: [],
})
export class PaymentGroupRoutingModule {}
