import { DropDown } from 'dfps-web-lib';

export interface DeliveredServiceDetail {
    pageMode: string;
    serviceCodes: DropDown[];
    countyCodes: DropDown[];
    utCodes: DropDown[];
    deliveredServiceDetail: DeliveredServiceDetailRes;
    invoice: Invoice;
    isErrorServiceCode: boolean;
}

export interface DeliveredServiceDetailRes {
    id: number;
    lastUpdatedDate: string;
    invoiceId: number;
    invoiceType: string;
    invoiceMonth: number;
    invoiceYear: number;
    resourceId: number;
    personId: number;
    contractId: number;
    county: string;
    feePaid: number;
    income: number;
    unitRate: number;
    disposition: string;
    lineType: string;
    service: string;
    serviceAuthDetails?: ServiceAuthDetails[];
    unitType: string;
    rejectedItem: string;
    month: number;
    csli: number;
    fromDay: number;
    toDay: number;
    unitQuantity: number;
    year: number;
    paymentType: string;
    externalViewIndicator: string;
    internalViewIndicator: string;
    itemTotal: string;
    serviceAuthDetailId: number;
    svcAuthStartDate: string;
    svcAuthTermDate: string;
    invoiceAdjustment:boolean;
}

export interface Invoice {
    phase: string;
    type: string;
    invoiceAdjustment: boolean;
    adjustment: string;
}

export interface ServiceAuthDetails {
    fullName: string;
    serviceAuthDetailId: number;
    svcAuthStartDate: string;
    svcAuthTermDate: string;
    code: number;
    decode: number;
}
