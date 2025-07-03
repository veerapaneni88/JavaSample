import { DropDown } from 'dfps-web-lib';
import { InvoiceResult } from './InvoiceHeader';

export interface FosterCareRes {
    fosterCare: FosterCareDetail;
    invoice: InvoiceResult;
    pageMode: string;
    service: DropDown[];
    isErrorServiceCode: boolean;
}

export interface FosterCareDetail {
    id: string;
    lastUpd: string;
    invoiceId: string;
    resourceId: string;
    personId: string;
    serviceAuthDetailId: string;
    facilityAcclaim: string;
    county: string;
    feePaid: string;
    income: string;
    rate: string;
    disposition: string;
    lineType: string;
    service: string;
    unitType: string;
    rejectedItem: string;
    month: string;
    csli: string;
    fromDay: string;
    toDay: string;
    unitQuantity: string;
    year: string;
    fullName: string;
    paymentType: string;
    externalViewIndicator: string;
    internalViewIndicator: string;
    itemTotal: string;
    isReversal: boolean;
}

export interface FosterCareDetailRes {
    fosterCareDetail: FosterCareDetail;
    pageMode: string;
    service: DropDown[];
}

export interface FosterCareValidateReq {
    resourceId: string;
    idPerson: string;
}

export interface FosterCareValidateRes {
    facilityAcclaim: string
    personFullName: string;
    resourceId: string;
    personId: string;
}
