 export interface CostReimbursement {
    crmId: number;
    invoiceId: number;
    phase: string;
    service: string;
    lineItem: number;
    lineItemServicesQuantity: number;
    salaryExpenditureReimbursement: number;
    supplyExpenditureReimbursement: number;
    travelExpenditureReimbursement: number;
    fringeBenefitReimbursement: number;
    offsetItemReimbursement: number;
    equipmentExpenditures: number;
    allocatedAdminCosts: number;
    otherExpenditureReimbursement: number;
    lineItemType: string;
    adjustment: string;
    pageMode: string;
    lastUpdatedDate: string;
}

export enum PageMode {
    NEW = 'NEW',
    EDIT = 'EDIT',
    VIEW = 'VIEW'
}