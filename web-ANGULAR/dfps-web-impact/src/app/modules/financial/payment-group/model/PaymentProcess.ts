import { DropDown } from 'dfps-web-lib';

export interface PaymentProcessSearch {
    processPaymentType: string;
    accountIds: string[];
    searchPrivilege: boolean;
}

export interface PaymentProcessRequest {
    processPaymentType: string;
    accountNo: string;
    checkNo: number;
    agencyAccountId: number;
    payeePersonId: number;
    lastUpdatedDate: string;
}

export interface PaymentProcessResponse {
    availablePaymentGroupResults: AvailablePaymentGroupResults[];
    modifyPrivilege: boolean;
    pageSize: number;
    sortColumns: number;
    totalElements: number;
    totalPages: number;
}

export interface AvailablePaymentGroupResults {
    agencyAccountId: number;
    amount: number;
    checkNumber: number;
    dateIssued: string;
    payeeName: string;
    paymentGroupId: number;
    paymentMode: string;
    status: string;
    lastUpdatedDate : string;
}

export interface ProcessPrintAndVoidChecksRequest {
    processPaymentType: string;
    accountNumber: string;
    printCheckDtoList: PrintCheckDtoList[];
}

export interface PrintCheckDtoList {
    paymentGroupId: number;
    warrantNbr?: number;
    lastUpdatedDate?: string;
    status?: string;
}

export interface VoidUnprocessedChecks {
    processPaymentType: string;
    accountNumber: string;
    unprocessedCheckNo: number;
    voidDate: string;
}

export interface ProcessSelectedACHRequest {
    processPaymentType: string;
    accountNumber: string;
    availablePaymentGroupResults: AvailablePaymentGroupResults[];
    achNumber: string;
    achDate: string;

}
