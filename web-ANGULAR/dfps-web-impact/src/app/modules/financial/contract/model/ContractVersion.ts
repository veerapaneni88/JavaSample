export interface ContractVersion {
    scorContractNumber?: string;
    versionNumber?: number;
    effectiveDate?: string;
    endDate?: string;
    createDate?: string;
    noShowPercent?: string;
    locked?: boolean;
    comment?: string;
    region?: string;
    functionType?: string;
    contractWorkerId?: number;
    contractId?: number;
    period?: number;
    periodClosureDate?: string;
    periodSigned?: boolean;
    lastUpdatedDate?: string;
}

export interface ContractVersionRes {
    contractVersion: ContractVersion;
    pageMode: string;
}
