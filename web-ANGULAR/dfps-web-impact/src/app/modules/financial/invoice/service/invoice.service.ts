import { InvoiceTypeEnum } from './../model/InvoiceHeader';
import { Injectable, LOCALE_ID, Inject } from '@angular/core';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { CostReimbursement } from '../model/CostReimbursement';
import { DeliveredServiceDetail, DeliveredServiceDetailRes } from '../model/DeliveredService';
import { FosterCareDetail, FosterCareRes, FosterCareValidateReq, FosterCareValidateRes } from '../model/FosterCare';
import {
    DeliveredServiceResList,
    FosterCareListRes,
    InvoiceDetailRes,
    InvoiceHeaderDisplay,
    ValidateContractNumberResult,
    CostReimbursementListRes,
    InvoiceAdminDetailListRes,
    InvoiceAuthorizeReq,
} from '../model/InvoiceHeader';
import { InvoiceSearch, InvoiceSearchDisplay, InvoiceSearchResponse } from '../model/InvoiceSearch';
import { RejectionReason } from '../model/RejectionReason';
import { map } from 'rxjs/operators';
import { formatDate } from '@angular/common';

@Injectable()
export class InvoiceService {
    static readonly HEADER_FRONTEND_URL = '/financial/invoice/header';

    readonly DISPLAY_SEARCH_URL: string = '/v1/invoices/display-search';
    readonly INVOICE_SEARCH_URL: string = '/v1/invoices/search';
    readonly VALIDATE_DETAILED_SERVICE_URL = '/v1/invoices/';
    readonly DISPLAY_HEADER_URL: string = '/v1/invoices/types';
    readonly SAVE_INVOICE_HEADER_URL = '/v1/invoices/save';
    readonly INVOICE_URL: string = '/v1/invoices/';
    readonly SAVE_DELIVERED_SERVICE_URL = '/v1/invoices/';

    constructor(
        private apiService: ApiService,
        private globalMessageService: GlobalMessageServcie,
        @Inject(LOCALE_ID) private locale: string
    ) {}

    displayInvoiceSearch(): Observable<InvoiceSearchDisplay> {
        return this.apiService.get(this.DISPLAY_SEARCH_URL);
    }

    searchInvoice(invoiceSearch: InvoiceSearch): Observable<InvoiceSearchResponse> {
        return this.apiService.post(this.INVOICE_SEARCH_URL, invoiceSearch);
    }

    getAdministrativeDetails(invoiceId, adminDetailId) {
        return this.apiService.get(this.INVOICE_URL + invoiceId + '/admin-details/' + adminDetailId);
    }

    updateADMDetails(invoiceId, updatedDetails) {
        return this.apiService.post(this.INVOICE_URL + invoiceId + '/admin-details', updatedDetails);
    }

    getDeliveredService(invoiceId, deliveredServiceId): Observable<DeliveredServiceDetail> {
        return this.apiService.get(this.INVOICE_URL + invoiceId + '/delivered-services/' + deliveredServiceId);
    }

    saveDeliveredService(invoiceId, deliveredServiceRequest: any): Observable<DeliveredServiceDetailRes> {
        return this.apiService.post(
            this.SAVE_DELIVERED_SERVICE_URL + invoiceId + '/delivered-services/save',
            deliveredServiceRequest
        );
    }

    validateDeliveredService(invoiceId, deliveredService: any): Observable<any> {
        return this.apiService.post(
            this.VALIDATE_DETAILED_SERVICE_URL + invoiceId + '/delivered-services/validate',
            deliveredService
        );
    }

    getRejectionReason(invoiceId: any, rejectedItemId: any): Observable<RejectionReason[]> {
        return this.apiService.get(this.INVOICE_URL + invoiceId + '/rejection-reasons/' + rejectedItemId);
    }

    displayInvoiceHeader(): Observable<InvoiceHeaderDisplay> {
        return this.apiService.get(this.DISPLAY_HEADER_URL);
    }

    displayInvoiceHeaderDetail(invoiceID: number): Observable<InvoiceDetailRes> {
        return this.apiService.get(this.INVOICE_URL + invoiceID).pipe(
            map((result: InvoiceDetailRes) => {
                if (result && result.invoiceResult) {
                    if (result.invoiceResult.recoupInitiatedDate) {
                        result.invoiceResult.recoupInitiatedDate = formatDate(
                            result.invoiceResult.recoupInitiatedDate.split(' ')[0],
                            'MM/dd/yyyy',
                            this.locale
                        );
                    }
                }
                return result;
            })
        );
    }

    saveInvoiceHeaderDetail(updateInvoiceResult: any): Observable<number> {
        if(updateInvoiceResult.recoupInitiatedDate){
            updateInvoiceResult.recoupInitiatedDate =    formatDate(
                updateInvoiceResult.recoupInitiatedDate.split(' ')[0],
                'yyyy-MM-dd HH:mm:ss',
                this.locale
            );
        }
        this.globalMessageService.addSuccessPath(this.SAVE_INVOICE_HEADER_URL);
        return this.apiService.post(this.SAVE_INVOICE_HEADER_URL, updateInvoiceResult);
    }
    authorizeInvoiceHeader(invoiceAuthorizeReq: InvoiceAuthorizeReq): Observable<number> {
        // this.globalMessageService.addSuccessPath(this.INVOICE_URL +'authorize');
        return this.apiService.post(this.INVOICE_URL + 'authorize', invoiceAuthorizeReq);
    }

    getInvoiceFosterCareList(fosterCarePagination: any, invoiceId: number): Observable<FosterCareListRes> {
        return this.apiService.post(this.INVOICE_URL + invoiceId + '/foster-care-details', fosterCarePagination);
    }

    getInvoiceAdminDetailList(admindDetailPagination: any, invoiceId: number): Observable<InvoiceAdminDetailListRes> {
        return this.apiService.post(
            this.INVOICE_URL + invoiceId + '/administrative-details-list',
            admindDetailPagination
        );
    }

    getInvoiceDeliveredSvcList(deliveredSvcPagination: any, invoiceId: number): Observable<DeliveredServiceResList> {
        return this.apiService.post(this.INVOICE_URL + invoiceId + '/delivered-services', deliveredSvcPagination);
    }

    getFosterCareDetail(invoiceId: any, fosterCareId: any): Observable<FosterCareRes> {
        return this.apiService.get(this.INVOICE_URL + invoiceId + '/foster-care-details/' + fosterCareId);
    }

    getCostReimbursementDetail(invoiceId: number, costReimbursementId: number, service: string): Observable<CostReimbursement> {
        return this.apiService.get(this.INVOICE_URL + invoiceId + '/cost-reimbursements/'
         + costReimbursementId + '/service-code/' + service);
    }

    getInvoiceCRMList(crmPagination: any, invoiceId: number): Observable<CostReimbursementListRes> {
        return this.apiService.post(this.INVOICE_URL + invoiceId + '/cost-reimbursements/list', crmPagination);
    }

    saveCostReimbursement(costReimbursement: CostReimbursement): Observable<number> {
        return this.apiService.post(
            this.INVOICE_URL + costReimbursement.invoiceId + '/cost-reimbursements',
            costReimbursement
        );
    }

    saveFosterCare(invoiceId: any, fosterCare: FosterCareDetail) {
        const SAVE_FOSTERCARE_URL = this.INVOICE_URL + invoiceId + '/foster-care-details/save';
        this.globalMessageService.addSuccessPath(SAVE_FOSTERCARE_URL);
        return this.apiService.post(SAVE_FOSTERCARE_URL, fosterCare);
    }

    validateFosterCare(
        invoiceId: any,
        fosterCareValidateReq: FosterCareValidateReq
    ): Observable<FosterCareValidateRes> {
        const VALIDATE_FOSTERCARE_URL = this.INVOICE_URL + invoiceId + '/foster-care-details/validate';
        this.globalMessageService.addSuccessPath(VALIDATE_FOSTERCARE_URL);
        return this.apiService.post(VALIDATE_FOSTERCARE_URL, fosterCareValidateReq);
    }

    getInvoiceValidateContractInfo(contractId: any): Observable<ValidateContractNumberResult> {
        return this.apiService.get(this.INVOICE_URL + 'validate/' + contractId);
    }

    downloadDeliveredService(invoiceId: any, totalElements: any) : Observable<Blob>{
        return this.apiService.download(this.INVOICE_URL + invoiceId + '/download/' + totalElements);
     }
}
