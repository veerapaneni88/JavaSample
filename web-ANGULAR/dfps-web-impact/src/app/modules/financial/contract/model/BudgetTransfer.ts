export interface BudgetTransferRes {
    contractId?: number;
    period?: number;
    version?: number;
    budgetTransfers: BudgetTransfer[];
    pageMode?: string;
    scorContractNumber: string;
}

export interface BudgetTransfer {
    lineItem?: number;
    serviceCode?: string;
    category?: string;
    totalAmount?: number;
    budgetBalance?: number;
    lastUpdatedDate: string;
}

export interface BudgetTransferReq {
    contractId?: number;
    period?: number;
    version?: number;
    budgetTransfers: BudgetTransfer[];
}
