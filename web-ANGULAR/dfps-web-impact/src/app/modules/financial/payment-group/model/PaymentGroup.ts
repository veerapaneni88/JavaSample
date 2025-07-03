export interface PaymentGroupRes {
    finPaymentGroupRes: PaymentGroupDetail;
    pageMode: string;
    accountNumbers: string[];
}

export interface PaymentGroupDetail {
    financialPaymentGrpId: number;
    lastUpdatedDate: string;
    contractPersonId: number;
    categoryCode: string;
    paymentGroupStatus: string;
    noOfTransactions: number;
    createdDate: string;
    issuedDate: string;
    totalAmount: number;
    paymentMode: string;
    paymentMemo: string;
    checkNumber: number;
    payeeName: string;
    accountNumber: string;
    achNumber: string;
    employeeIdCreated: number;
    employeeIdLastUpdated: number;
    paymentReceiptName: string;
    paymentProcessType: string;
    resourceId: number;
    userName: string;
}

export interface TransactionSearchReq {
    accountId: number;
    personId: number;
    contractId: number;
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface TransactionSearchRes {
    transactionSearchResults: PaymentTransaction[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface PaymentTransaction {
    finAcctTransactionId: number;
    serviceDetailId: number;
    invoiceId: number;
    financialAccountId: number;
    financialPaymentGroupId: number;
    amount: number;
    balance: number;
    category: string;
    type: string;
    transactionDate: string;
    transactionCount: number;
    warrantNumber: number;
    description: string;
    subcategory: string;
    payeeName: string;
    isReconciled: string;
    employeeLogonId: string;
    isAch: string;
    accountHoldDate: string;
    workerName: string;
    status: string;
    issuedDate: string;
    achNumber: string;
    fullName: string;
    personId: string;
    contractId: string;
}
