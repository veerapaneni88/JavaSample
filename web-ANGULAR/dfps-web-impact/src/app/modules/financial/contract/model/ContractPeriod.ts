import { DropDown } from 'dfps-web-lib';

export interface ContractPeriodResponse {
    contractPeriod: ContractPeriod;
    contractResource: ContractResource;
    status: DropDown[];
    pageMode: string;
}

export interface ContractPeriod {
    contractId?: string;
    contractWorkerId?: number;
    period?: number;
    statusCode?: string;
    startDate?: string;
    termDate?: string;
    closureDate?: string;
    lastUpdatedDate?: string;
    renew?: boolean;
    signed?: boolean;
    legalIdentifier?: number;
    procurementNumber?: string;
    scorContractNumber?: string;
    resourceType?: string;
    scorLinked?: boolean;
    region?: string;
    functionType?: string;
    signDisabled?: boolean;
}

export interface ContractResource {
    resourceId: number;
    resourceType: string;
    resourceName: string;
    legalName: string;
    vendorId: number;
    addressLine1: string;
    functionType: string;
    procurementType: string;
    programType: string;
    region: string;
    budgetLimit: string;
    contractManagerId: string;
    resourceAddressId: string;
    personFullName: string;
}
