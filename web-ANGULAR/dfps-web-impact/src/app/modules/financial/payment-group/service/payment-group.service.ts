import { ApiService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { formatDate } from '@angular/common';
import { Inject, Injectable, LOCALE_ID } from '@angular/core';

import {
    PaymentProcessSearch,
    PaymentProcessResponse,
    PaymentProcessRequest,
    ProcessPrintAndVoidChecksRequest,
    VoidUnprocessedChecks,
    ProcessSelectedACHRequest
} from '../model/PaymentProcess';
import { AddPaymentGrpRes, SavePaymentGrpRequest, ValidatePaymentGrpReq } from '../model/PaymentGrpDetail';

import { PaymentGroupRes, TransactionSearchRes, TransactionSearchReq, PaymentTransaction } from '../model/PaymentGroup';
import {
    ReconcileDisplayResponse,
    ReconciliationSearchRequest,
    ReconciliationSearchRes,
    ReconciliationTransactionReq,
} from '../model/ReconcileTransaction';
import {
    PaymentGroupSearchDisplayResponse,
    PaymentGroupSearchRequest,
    PaymentGroupSearchResponse,
} from './../model/PaymentGroupSearch';
import { map } from 'rxjs/operators';

@Injectable()
export class PaymentGroupService {
    readonly RECONCILE_DISPLAY_URL: string = '/v1/reconciliation/display';
    readonly DISPLAY_SEARCH_URL: string = '/v1/payment-process/display-search';
    readonly PAYMENT_PROCESS_SEARCH_URL: string = '/v1/payment-process/search';
    readonly ASSIGN_CHECK_NUMBER: string = '/v1/payment-process/max-check-number';
    readonly PROCESS_PRINT_AND_VOID_CHECKS: string = '/v1/payment-process/process-print-checks';
    readonly VOID_UNPROCESSED_CHECKS: string = '/v1/payment-process/void-unprocessed-check';
    readonly PROCESS_SELECTED_ACH: string = '/v1/payment-process/process-ach-payments';
    readonly ACCOUNT_NUMBERS_LIST: string = '/v1/payment-groups/0';
    readonly VALIDATE_PAYMENT_GRP_DETAILS = '/v1/payment-groups/validate';
    readonly SAVE_PAYMENT_GRP_DETAILS = '/v1/payment-groups';
    readonly PAYMENT_GRP_DETAILS = '/v1/payment-groups/';
    readonly GET_PAYMENT_GRP_DETAIL = '/v1/payment-groups';
    readonly PAYMENT_GROUP_SEARCH_DISPLAY_URL: string = '/v1/payment-groups/display-search';
    readonly PAYMENT_GROUP_SEARCH: string = '/v1/payment-groups/search';
    readonly RECONCILE_TRANSACTION_SEARCH_URL: string = '/v1/reconciliation/search';
    readonly RECONCILE_TRANSACTION_URL: string = '/v1/reconciliation/reconcile';
    readonly PAYMENT_GROUP_TRAN_SEARCH = '/v1/payment-groups/transaction-search';
    readonly DELETE_TRANSACTION_FROM_GRP = '/v1/payment-groups/transactions/delete';
    readonly PAYMENT_GROUP_SEARCH_DELETE = '/v1/payment-groups/delete';

    private selectedAccountNumber: any;
    private paymentGroupSearchRequest: PaymentGroupSearchRequest;

    constructor(private apiService: ApiService, @Inject(LOCALE_ID) private locale: string) { }

    getSelectedAccountNumber(): any {
        return this.selectedAccountNumber;
    }

    setSelectedAccountNumber(accountNumber: any) {
        this.selectedAccountNumber = accountNumber;
    }

    getPaymentGroupSearchRequest(): any {
        return this.paymentGroupSearchRequest;
    }

    setPaymentGroupSearchRequest(searchRequest: any) {
        this.paymentGroupSearchRequest = searchRequest;
    }

    displayReconcileTransaction(): Observable<ReconcileDisplayResponse> {
        return this.apiService.get(this.RECONCILE_DISPLAY_URL);
    }

    displayPaymentProcessSearch(): Observable<PaymentProcessSearch> {
        return this.apiService.get(this.DISPLAY_SEARCH_URL);
    }

    searchPaymentProcess(paymentProcessSearch: PaymentProcessRequest): Observable<PaymentProcessResponse> {
        return this.apiService.post(this.PAYMENT_PROCESS_SEARCH_URL, paymentProcessSearch);
    }

    assignCheckNumber(accountNo: number): Observable<number> {
        return this.apiService.get(this.ASSIGN_CHECK_NUMBER + '/' + accountNo);
    }

    processPrintChecks(
        processPrintAndVoidChecksRequest: ProcessPrintAndVoidChecksRequest
    ): Observable<PaymentProcessResponse> {
        return this.apiService.post(this.PROCESS_PRINT_AND_VOID_CHECKS, processPrintAndVoidChecksRequest);
    }

    voidUnprocessedChecks(voidUnprocessedChecksRequest: VoidUnprocessedChecks): Observable<VoidUnprocessedChecks> {
        return this.apiService.post(this.VOID_UNPROCESSED_CHECKS, voidUnprocessedChecksRequest);
    }

    processSelectedACH(processSelectedACHRequest: ProcessSelectedACHRequest): Observable<PaymentProcessResponse> {
        return this.apiService.post(this.PROCESS_SELECTED_ACH, processSelectedACHRequest);
    }

    getListOfAcctNumbers(): Observable<AddPaymentGrpRes> {
        return this.apiService.get(this.ACCOUNT_NUMBERS_LIST);
    }

    validatePaymentGrpDetails(reqObj: ValidatePaymentGrpReq) {
        return this.apiService.post(this.VALIDATE_PAYMENT_GRP_DETAILS, reqObj);
    }

    generateDropDownValues(dropdownData) {
        return dropdownData.map((data) => ({ code: data, decode: data }));
    }

    savePaymentGrpDetail(savePaymentGrpRequest: SavePaymentGrpRequest) {
        return this.apiService.post(this.SAVE_PAYMENT_GRP_DETAILS, savePaymentGrpRequest);
    }

    getPaymentGrpDetails(paymentGrpId): Observable<AddPaymentGrpRes> {
        return this.apiService.get(this.PAYMENT_GRP_DETAILS + paymentGrpId).pipe(
            map((res: AddPaymentGrpRes) => {
                if (res.finTransactionRes) {
                    res.finTransactionRes.forEach((dto) => {
                        if (dto.transactionDate) {
                            dto.transactionDate = formatDate(dto.transactionDate.split(' ')[0], 'MM/dd/yyyy', this.locale);
                        }
                        if (dto.type === 'C') {
                            dto.type = 'CR';
                        } else {
                            dto.type = '';
                        }
                    });
                }
                return res;
            })
        );
    }

    displayPaymentGroupSearch(): Observable<PaymentGroupSearchDisplayResponse> {
        return this.apiService.get(this.PAYMENT_GROUP_SEARCH_DISPLAY_URL);
    }

    getPaymentGroupDetail(paymentGroupId: any): Observable<PaymentGroupRes> {
        return this.apiService.get(this.GET_PAYMENT_GRP_DETAIL + '/' + paymentGroupId);
    }

    searchReconcileTransactions(
        reconcilationSearchRequest: ReconciliationSearchRequest
    ): Observable<ReconciliationSearchRes> {
        return this.apiService.post(this.RECONCILE_TRANSACTION_SEARCH_URL, reconcilationSearchRequest);
    }

    searchPaymentGroup(searchRequest: PaymentGroupSearchRequest): Observable<PaymentGroupSearchResponse> {
        return this.apiService.post(this.PAYMENT_GROUP_SEARCH, searchRequest);
    }

    deletePaymentGroup(deleteReq: any[]): Observable<PaymentGroupSearchResponse> {
        return this.apiService.post(this.PAYMENT_GROUP_SEARCH_DELETE, deleteReq);
    }

    reconcileTransactions(reconcileTransactionReq: ReconciliationTransactionReq) {
        return this.apiService.post(this.RECONCILE_TRANSACTION_URL, reconcileTransactionReq).pipe(
            map((res) => {
                return { ...res, response: res == null ? { res: 'success' } : {} };
            })
        );
    }

    displayPaymentTranSearch(transactionSearchReq: TransactionSearchReq): Observable<TransactionSearchRes> {
        return this.apiService.post(this.PAYMENT_GROUP_TRAN_SEARCH, transactionSearchReq).pipe(
            map((res: TransactionSearchRes) => {
                res.transactionSearchResults.forEach((dto) => {
                    if (dto.transactionDate) {
                        dto.transactionDate = formatDate(dto.transactionDate.split(' ')[0], 'MM/dd/yyyy', this.locale);
                    }
                    if (dto.type === 'C') {
                        dto.type = 'CR';
                    } else {
                        dto.type = '';
                    }
                });
                return res;
            })
        );
    }

    addTransToPaymntGrp(paymntGrpId: number, Trans: PaymentTransaction[]): Observable<number> {
        return this.apiService.post(this.GET_PAYMENT_GRP_DETAIL + '/' + paymntGrpId + '/addFinTrans', Trans);
    }

    deleteTransactionFromGrp(paymntGrpId: number, body) {
        return this.apiService.post(this.PAYMENT_GRP_DETAILS + paymntGrpId + '/transactions/delete', body);
    }
}
