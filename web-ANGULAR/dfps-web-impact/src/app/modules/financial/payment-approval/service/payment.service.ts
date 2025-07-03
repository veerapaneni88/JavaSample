import { ApiService, GlobalMessageServcie, Pagination } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { PaymentApprovalRes, PaymentApprovalSaveReq } from '../model/PaymentApproval';

@Injectable()
export class PaymentService {

    readonly PAYMENT_APPROVAL_URL: string = '/v1/payment-approvals/display';
    readonly PAYMENT_APPROVAL_SAVE_URL: string = '/v1/payment-approvals';


    constructor(
        private apiService: ApiService) {
    }


    loadPaymentApprovals(paymentApprovalReq: Pagination): Observable<PaymentApprovalRes> {
        return this.apiService.post(this.PAYMENT_APPROVAL_URL, paymentApprovalReq);
    }

    savePaymentApprovals(paymentApprovalSaveReq: PaymentApprovalSaveReq): Observable<any> {
        return this.apiService.post(this.PAYMENT_APPROVAL_SAVE_URL, paymentApprovalSaveReq);
    }

}
