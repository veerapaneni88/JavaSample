
export interface AddPaymentGrpRes {
    finPaymentGroupRes: PaymentGrpDetails,
    pageMode: string,
    accountNumbers: [],
    categories: string,
    paymentModes: string,
    finTransactionRes: TransactionList[]

}

export interface ValidatePaymentGrpReq {
    categoryCode: string;
    contractPersonId: string;
}

export interface SavePaymentGrpRequest {
    categoryCode: string;
    contractPersonId: string;
    paymentReceiptName: string,
    noOfTransactions: number,
    totalAmount: number,
    paymentMode: string,
    paymentMemo: string,
    payeeName: string,
    accountNumber: number,
    financialPaymentGrpId: number,
    issuedDate: string,
    checkNumber: number,
    paymentGroupStatus: string,
    lastUpdatedDate: string
}

export interface TransactionList {
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

export interface PaymentGrpDetails {
    financialPaymentGrpId: number,
    lastUpdatedDate: string,
    contractId: number,
    personId: number,
    categoryCode: string,
    paymentGroupStatus: string,
    noOfTransactions: number,
    createdDate: string,
    issuedDate: string,
    totalAmount: number,
    paymentMode: string,
    paymentMemo: string,
    checkNumber: number
    payeeName: string,
    accountNumber: string,
    achNumber: string,
    employeeIdCreated: string,
    employeeIdLastUpdated: string,
    paymentReceiptName: string,
    paymentProcessType: string,
    resourceId: number,
    fromDate: string,
    toDate: string,
    receiptAddress1: string,
    receiptAddress2: string,
    receiptCity: string,
    receiptZipCode: string,
    receiptState: string
}