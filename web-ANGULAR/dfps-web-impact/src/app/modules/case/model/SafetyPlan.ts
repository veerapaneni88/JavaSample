import { DropDown } from 'dfps-web-lib';

export interface FacilityInfo {
    classOperationName?: string;
    classOperationNumber?: string;
    impactOperationFacilType?: string;
    resourceId?: string;

    classAgencyHomeName?: string;
    classAgencyHomeNumber?: string;
    impactAgencyHomeFacilType?: string;
    facilityId?: string;

    nbrFclty?: number;
    nbrAgncy?: number;
    nbrBranch?: number;
}

export interface Facility {
    idFclty?: number;
    cdFcltyType?: string;
    nbrFclty?: number;
    nbrAgncy?: number;
    nbrBranch?: number;
    nmFclty?: string;
    operationNameAndNumber?: string;
}

export interface FacilityType {
    codeType?: string;
    code?: string;
    decode?: string;
}

export interface SafetyPlan {
    id?: number;
    facilityId?: number;
    selectedFacility?: Facility;
    caseId?: string;
    eventId?: number;
    safetyPlanStatus?: string;
    effectiveDate?: Date;
    unsafeSituations?: string;
    immediateAction?: string;
    responsiblePersons?: string;
    actionTimeFrame?: string;
    createdPersonId?: number;
    createdDate?: Date;
    lastUpdatedPersonId?: number;
    lastUpdatedDate?: Date;
}

export interface SafetyPlanListRes {
    caseId?: string;
    stageName?: string;
    safetyPlans?: SafetyPlan[];
    facilityTypes?: FacilityType[];
    showDeleteButton: boolean;
    showAddButton: boolean;
    safetyPlanStatuses?: DropDown[];
}

export interface SafetyPlanRes {
    caseId?: string;
    stageId?: string;
    stageName?: string;
    pageMode?: string;

    classOperationName?: string;
    classOperationNumber?: string;
    operationImpactFacilityType?: string;

    agencyHomeName?: string;
    agencyHomeOperationNumber?: string;
    agencyHomeImpactFacilityType?: string;

    safetyPlan?: SafetyPlan;
    operationNameAndNumber?: DropDown[];
    safetyPlanStatuses?: DropDown[];
    showAgencyHomeInfo?: boolean;
    stageClosed?: boolean;
    roleCanEdit?: boolean;
    hasStageAccess?: boolean;
}
