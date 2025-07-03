export interface PaymentApprovalRes {
    paymentApprovals: PaymentApproval[];
    pageMode: string;
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface PaymentApproval {
    invoiceId: number;
    approved: string;
    phase: string;
    receivedDate: string;
    approvalDate: string;
    validAmount: number;
    resourceName: string;
    contractId: number;
    personId: number;
    resourceId: number;
    lastUpdatedDate: string;
    disableRowSelect: boolean;
}

export interface PaymentApprovalSaveReq {
    paymentApprovals?: PaymentApproval[];
    status: string;
}


export enum ApprovalStatus {
    Approved = 'APV',
    Disapproved = 'DPV',
    Pending = 'PAP',
    Rejected = 'RJA'
}
