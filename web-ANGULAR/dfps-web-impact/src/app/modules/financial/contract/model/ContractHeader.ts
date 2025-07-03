import { DropDown } from 'dfps-web-lib';

export interface Contract {
    contractId?: string;
    contractWorkerId?: number;
    contractManagerId?: number;
    resourceAddressId?: number;
    resourceId?: number;
    contractManager?: string;
    functionType?: string;
    programType?: string;
    procurementType?: string;
    region?: string;
    budgetLimit?: boolean;
    resourceName?: string;
    resourceLegalName?: string;
    resourceVendorId?: string;
    addressLine1?: string;
    resourceType?: string;
    sponsorPersonId?: number;
    sponsorName?: string;
    sponsorEmail?: string;
    sponsorPhone?: string;
    organizationEIN?: number;
    organizationLegalName?: string;
    organizationId?: number;
    einBgcAccountLinkId?: number;
    backgroundCheck?: boolean;
    resourceAddress?: ResourceAddress[];
    createdDate?: string;
    lastUpdatedDate?: string;
    createdPersonId?: string;
    lastUpdatedPersonId?: string;
    registeredStatus?: boolean;
    scorStatus?: boolean;
    accountStatus?: boolean;
    versionLocked?: boolean;
    scorContractNumber?: string;
}

export interface Resource {
    resourceId: number;
    name: string;
    legalName: string;
    contractResourceAddress: ResourceAddress[];
}

export interface ResourceAddress {
    resourceAddressId: number;
    vendorId: string;
    addressLine1: string;
}

export interface ContractHeaderResponse {
    contract?: Contract;
    programTypes: DropDown[];
    regions: DropDown[];
    functionTypes: DropDown[];
    procurementType: DropDown[];
    pageMode: string;
}
