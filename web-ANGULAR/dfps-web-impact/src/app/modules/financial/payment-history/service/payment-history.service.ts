import { Injectable } from '@angular/core';
import { ApiService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { PaymentHistorySearchReq, PaymentHistorySearchRes } from '../model/PaymentHistorySearch';

@Injectable({
    providedIn: 'root',
})
export class PaymentHistoryService {
    constructor(private apiService: ApiService) {}
    readonly PAYMENT_HISTORY_SEARCH_URL = '/v1/payment-history/search';
    readonly PAYMENT_HISTORY_TOTAL_PAYMENTS_URL = '/v1/payment-history/total-payments';

    getPaymentSearchHistory(paymentHistorySearchReq: PaymentHistorySearchReq): Observable<PaymentHistorySearchRes> {
        return this.apiService.post(this.PAYMENT_HISTORY_SEARCH_URL, paymentHistorySearchReq);
    }

    getTotalPayments(paymentHistorySearchReq: PaymentHistorySearchReq): Observable<number> {
        return this.apiService.post(this.PAYMENT_HISTORY_TOTAL_PAYMENTS_URL, paymentHistorySearchReq);
    }
}
