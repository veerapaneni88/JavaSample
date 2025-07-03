export interface PaymentHistorySearchReq {
    fromDate: string;
    toDate: string;
    searchIndicator: number;
    searchId: number;
}
export interface PaymentHistorySearchResults {
    warrantDate: string;
    invoiceId: string;
    resourceName: string;
    amount: number;
    service: string;
    quantity: number;
    unitType: string;
    rate: number;
    income: number;
    feePaid: number;
    from: number;
    to: number;
}

export interface PaymentHistorySearchRes {
    paymentHistorySearchResults: PaymentHistorySearchResults[];
    totalPayments: number;
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}
