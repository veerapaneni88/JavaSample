import { DropDown } from 'dfps-web-lib';

export interface PaymentGroupSearchDisplayResponse {
    accountNumbers?: DropDown[];
    status?: DropDown[];
    paymentModes?: DropDown[];
    modifyPrivilege?: boolean;
}

export interface PaymentGroupSearchRequest {
    pageNumber?;
    pageSize?;
    sortField?;
    sortDirection?;
    accountNumber?;
    agencyAccountId?;
    personId?;
    status?;
    paymentMode?;
    fromDate?;
    toDate?;
}
export interface PaymentGroupSearchResponse {
    paymentGroupId?;
    payeeName?;
    paymentMode?;
    status?;
    checkNumber?;
    amount?;
    agencyAccountId?;
    issuedDate?;
    displayCheckBox?
    sortColumns?;
}