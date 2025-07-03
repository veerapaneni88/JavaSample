import { DropDown } from 'dfps-web-lib';

export interface ContractSvc {
    adminAllUsedAmount?: number;
    budgetBalanceAmount?: number;
    budgetLimit?: boolean;
    contractId?: number;
    contractNumber?: number;
    contractServiceId?: number;
    contractWorkerId?: number;
    countyCodes?: CountyCodes[];
    equipmentAmount?: number;
    equipmentUsedAmount?: number;
    federalMatch: number;
    fringeBenefitAmount?: number;
    fringeBenefitUsedAmount?: number;
    isNewRow?: string;
    lastUpdatedDate?: string;
    lineItem?: number;
    localMatch: number;
    offsetItemUsedAmount?: number;
    otherAmount?: number;
    otherUsedAmount?: number;
    pageMode?: boolean;
    paymentType: string;
    period?: number;
    salaryAmount?: number;
    salaryUsedAmount?: number;
    serviceCode: string;
    supplyAmount?: number;
    supplyUsedAmount?: number;
    totalAmount?: number;
    totalUnits?: number;
    travelAmount?: number;
    travelUsedAmount?: number;
    unitRate: number;
    unitRateAmount?: number;
    unitRateUsedAmount?: number;
    unitType: string;
    usedUnits?: number;
    version?: number;
    versionLocked?: boolean;
}

export interface DisplayContractServicesResponse {
    contractService: ContractSvc;
    pageMode: string;
    services: DropDown[];
    programTypes: DropDown[];
    unitType: DropDown[];
    isErrorServiceCode: boolean;

}

export interface CountyCodes {
    checked: boolean;
    code: string;
    codeType: string;
    dateEnded: string;
    decode: string;
    nbrOrder: string;
}
