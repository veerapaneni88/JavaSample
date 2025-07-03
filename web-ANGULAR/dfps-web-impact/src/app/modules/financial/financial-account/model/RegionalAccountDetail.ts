import { DropDown } from 'dfps-web-lib';
export interface RegionalAccountDetails {
    id: number
    accountNumber: string,
    program: string,
    region: string,
    status: string,
    name: string,
    address1: string,
    address2: string,
    city: string,
    state: string,
    zip: string,
    zipExt: string,
    phoneNumber: string,
    ext: string,
    county: string,
    balance: number
    reconBalance: number
}

export interface RegionalAccountResponse {
    totalPages: number;
    totalElements: number;
    pageSize: number;
    sortColumns: [],
    regionalAccountDetails: RegionalAccountDetails
    financialAccountList: any[],
    stateList: DropDown[],
    countyList: DropDown[],
    pageMode: string
}

export interface RegionalAccountReq {
    regionalAccountId: number,
    accountStatus: string,
    sortField: string,
}