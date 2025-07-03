import { DropDown } from 'dfps-web-lib';

export interface FinancialAccount {
    id: number;
    personId: number;
    address1: string;
    address2: string;
    city: string;
    zip: string;
    state: string;
    balance: number;
    status: string;
    county: string;
    type: string;
    balanceDate: string;
    endDate: string;
    startDate: string;
    accountNumber: string;
    phone: string;
    ext: string;
    transactionCount: number;
    institutionName: string;
    comments: string;
    program: string;
    dedicated: boolean;
    availableBalance: number;
    reconBalance: number;
    personName: string;
    personProgram: string;
    personIdSsn: string;
    lastUpdatedDate: string;
    cpsChkAcct: boolean;
}

export interface FinancialAccountRes {
    financialAccount?: FinancialAccount;
    pageMode: string;
    apsTypes: DropDown[];
    cpsTypes: DropDown[];
    programs: DropDown[];
    status: DropDown[];
    states: DropDown[];
    counties: DropDown[];
}

export interface FinancialAccountDisplayRes {
    csvcRegions: DropDown[];
    programs: DropDown[];
    accountTypes: DropDown[];
    counties: DropDown[];
    status: DropDown[];
    regionCounties: DropDown[];
    regions: string;
    program: string;
    region: string;
    displayAccountNumber: boolean;
}

export interface County {
    decode: string;
    code: string;
    regionCode: string;
}

export interface FinancialAccountSearchRes {
    financialAccountSearch: FinancialAccountSearch[];
    pageMode: string;
}

export interface FinancialAccountSearch {
    personId: number;
    personName: string;
    finInstitutionName: string;
    county: string;
    accountType: string;
    status: string;
    accountNumber: string;
    region: string;
    transactionCount: number;
    accountBalance: number;
    reconBalance: number;
    asOf: string;
    financialAccountId: number;
    finInstituteAccountId: number;
    lastUpdatedDate: string;
}

export enum pageModes {
    NEW = 'NEW',
    EDIT = 'EDIT',
    VIEW = 'VIEW'
}

export const programs = {
    APS: 'APS',
    CPS: 'CPS'
};

export const status = {
    active: 'A',
    closed: 'C',
};

export const states = {
    texas: 'TX'
};
