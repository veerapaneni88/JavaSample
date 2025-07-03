import { DropDown } from 'dfps-web-lib';

export interface DisplayContractSearchResponse {
    programTypes: DropDown[];
    regions: DropDown[];
    functionTypes: DropDown[];
    procurementType: DropDown[];
    addPrivilege: boolean;
}

export interface ContractSearchRequest {
    contractId: string;
    resourceId: string;
    functionType: string;
    programType: string;
    region: string;
    budgetLimit: boolean;
    dateFrom: string;
    dateTo: string;
}

export interface ContractSearchResponse {
    contractSearchResults: ContractSearchResult[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface ContractSearchResult {
    resourceId: string;
    vendorId: string;
    resourceName: string;
    contractNumber: string;
    agencyAccountId: string;
    functionType: string;
    programType: string;
    region: string;
    budgetLimit: string;
    managerName: string;
}
