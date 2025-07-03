import { DropDown } from 'dfps-web-lib';

export interface InvoiceHeaderDisplay {
    types: DropDown[];
    pageMode: string;
}

export interface InvoiceDetailRes {
    invoiceResult: InvoiceResult;
    invoiceTypes: DropDown[];
    pageMode: string;
    fosterCareListRes: FosterCareListRes;
    invoiceAdminDetailListRes: InvoiceAdminDetailListRes;
    deliveredServiceListRes: DeliveredServiceResList;
    costReimbursementListRes: CostReimbursementListRes;
    isUnitRateMax: boolean;
    isCostReiNavViolation: boolean;
    indCostSaved: boolean;
}

export interface InvoiceResult {
    invoiceId: number;
    phase: string;
    invoiceAdjustment: boolean;
    isReadyForValid: boolean;
    type: string;
    receivedDate: string;
    month: number;
    year: number;
    claimedAmount: number;
    submitDate: string;
    validAmount: number;
    warrantDate: string;
    warrantAmount: number;
    warrantNumber: string;
    approved: string;
    contractId: number;
    resourceName: string;
    resourceId: number;
    region: string;
    resourceVendorId: string;
    isManualRecoup: boolean;
    recoupInitiatedDate: string;
    recoupCompletedDate: string;
    vidNumber: string;
    userType: string;
    optIn: boolean;
    providerAuth: boolean;
    approvalDate: string;
    adjustment: string;
    lastUpdatedDate: string;
    hasInvoiceModify: boolean;
    hasInvoiceFCModify: boolean;
}

export enum InvoiceView {
    New = 'NEW',
    VIEW = 'VIEW',
    EDIT = 'EDIT',
}
export enum UserTypeEnum {
    INTERNAL = 'internal',
    EXTERNAL = 'external',
}

export enum InvoiceTypeEnum {
    FOSTER_CARE = 'FSC',
    ADMINSTRATIVE = 'ADM',
    HEALTH_CARE_BENEFIT = 'HCB',
    PERMANECY_CARE_ASSISTANCE = 'PCA',
    DELIVERED_SVC_UR = 'DUR',
    DELIVERED_SVC_BOTH = 'DSB',
    DELIVERED_SVC_CR = 'DCR',
    ADOPTION_SERVICE = 'ADS',
    COST_REIMBURSEMENT = 'CRM',
}

export interface InvoiceSaveReq {
    invoiceId: number;
    contractId: number;
    invoiceAdjustment: boolean;
    type: string;
    isReadyForValid: boolean;
    receivedDate: string;
    month: number;
    year: number;
    claimedAmount: number;
    lastUpdatedDate: string;
    indCostSaved: boolean;
}

export interface FosterCareListRes {
    fosterCareResults: FosterCareResult[];
    optIn: boolean;
    evCount: number;
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface FosterCareResult {
    invoiceId: number;
    rejectedItem: string;
    personId: number;
    fullName: string;
    resourceId: string;
    month: string;
    year: string;
    fromDay: number;
    toDay: number;
    service: string;
    unitRate: string;
    income: number;
    facilityAcclaim: number;
    unitQty: number;
    phase: string;
    fosterCareId: number;
    itemTotal: number;

    isExternalViewed: boolean;
    // Derived Boolean value from iv
    isInternalViewed: boolean;
}

export interface InvoiceAdminDetailListRes {
    adminDetailResults: AdminDetailRes[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface AdminDetailRes {
    adminDetailId: number;
    rejection: string;
    service: string;
    csli: number;
    month: number;
    year: number;
    salaries: number;
    benefits: number;
    travel: number;
    supplies: number;
    equipment: number;
    other: number;
    administrative: number;
    offset: number;
    invoiceId: number;
}

export interface DeliveredServiceResList {
    deliveredServiceDetails: DeliveredServiceResult[];
    optIn: boolean;
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
    // has_SEC_EP_MOD_POS_INV: boolean;
    userType: string;
    evCount: number;
}

export interface DeliveredServiceResult {
    id: number;
    invoiceId: number;
    resourceId: number;
    personId: number;
    contractId: number;
    invoiceMonth: number;
    invoiceYear: number;
    serviceAuthDetailId: number;
    invoiceType: string;
    invoiceAdjustment: string;
    county: string;
    feePaid: number;
    income: number;
    unitRate: number;
    disposition: string;
    lineType: string;
    service: string;
    unitType: string;
    rejectedItem: string;
    month: number;
    csli: number;
    fromDay: number;
    toDay: number;
    unitQuantity: number;
    year: number;
    fullName: string;
    paymentType: string;
    svcAuthStartDate: string;
    svcAuthTermDate: string;
    externalViewIndicatorFlag: boolean;
    internalViewIndicatorFlag: boolean;
}

export interface ValidateContractNumberResult {
    contractId: number;
    resourceName: string;
    resourceId: number;
    contractValidated: string;
    resourceVendorId: string;
    region: string;
    invoiceId: number;
}

export interface CostReimbursementListRes {
    costReimbursement: CostReimbursementResult[];

    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
    pageMode: string;
}

export interface CostReimbursementResult {
    crmId: number;
    invoiceId: number;
    phase: string;
    service: string;
    lineItem: number;
    lineItemType: string;
    lineItemServicesQuantity: number;
    adjustment: string;
    adjustmentFlag: boolean;
    rejectedItem: string;
    csli: string;
    salaryExpenditureReimbursement: number;
    supplyExpenditureReimbursement: number;
    travelExpenditureReimbursement: number;
    fringeBenefitReimbursement: number;
    offsetItemReimbursement: number;
    equipmentExpenditures: number;
    allocatedAdminCosts: number;
    otherExpenditureReimbursement: number;
    rate: number;
    subTotal: number;
}

export interface InvoiceAuthorizeReq {
    invoiceId: number;
    providerAuth: boolean;
}
