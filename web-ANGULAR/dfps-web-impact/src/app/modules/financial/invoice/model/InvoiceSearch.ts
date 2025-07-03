import { DropDown } from 'dfps-web-lib';

export interface InvoiceSearch {
    invoiceId: number;
    type: string;
    agencyAccountId: number;
    Phase: string;
    invoiceYear: number;
    invoiceMonth: number;
    region: string;
    providerAuthorized: boolean;
    resourceId: number;
}


export interface InvoiceSearchDisplay {
    types: DropDown[];
    regions: DropDown[];
    phases: DropDown[];
    pageMode: string;
    addPrivilege: boolean;
    searchPrivilege: boolean;
    userType: string;
    accountIds: DropDown[];
}

export interface InvoiceSearchResponse {
    invoiceSearchResults: InvoiceSearchResult[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface InvoiceSearchResult {
    invoiceId: number;
    type: string;
    submittedDate: string;
    agencyAccountId: number;
    phase: string;
    validatedAmount: number;
    resourceName: string;
    resourceId: number;
    region: string;
}
