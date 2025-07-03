import { DropDown } from 'dfps-web-lib';
import { Stage } from './stage';

export interface ServicePackageListRes {
    servicePackages?: ServicePackage[];
    caseName?: string;
    stageName?: string;
    caseId?: number;
    stageId?: number;
    editMode?: boolean;
}

export interface ServicePackage {
    id?: string;
    caseId?: number;
    stageId?: number;
    eventId?: number;
    stageName?: string;
    servicePackageType?: string;
    servicePackage?: string;
    overrideReason?: string;
    overrideReasonComments?: string;
    endReason?: string;
    endReasonComments?: string;
    startDate?: string;
    endDate?: string;
    cansCompletedIndicator?: string;
    cansNotCompletedReason?: string;
    cansNotCompletedComments?: string;
    cansAssessmentCompletedDate?: string;
    cansRecommendationComments?: string;
    lastUpdatedDate?: string;
    lastModifiedBy?: string;
    event?: Event;
    stage?: Stage;
    addonServicePackages: AddonServicePackage[];
    creleaseDate: string;
    servicePackageCaseType?: string;
    thirdPartyReviewerComments: string;
    groRecommendedIndicator: string;
    groAssessmentCompletedDate: string;
    cansAssessorName?: string;
}

export interface ServicePackageRes {
    addonServicePackages: Map<string,DropDown[]>;
    servicePackage?: ServicePackage;
    recommendedServicePackage: ServicePackage;
    servicePackageTypes?: DropDown[];
    servicePackages?: DropDown[];
    servicePackageEndDates?: DropDown[];
    overrideReasons?: DropDown;
    selectedPrimaryEndReasons?: DropDown[];
    recommendedPrimaryEndReasons?: DropDown[];
    cansAssessmentNotCompletedReasons?: DropDown[];
    addonServiceEndReasons?: DropDown[];
    caseId?: number;
    stageId?: number;
    stageType?: string;
    pageMode?:string
    personAge?: number;
    eventId: number;
    pregnantOrYouthParent: boolean;
    cansAssessor: boolean;
    caseWorker: boolean;
    placementFixer: boolean;
    fosterCareEligibilitySpecialist: boolean;
    creleaseDate: string;
    servicePackageCaseTypes?: DropDown[];
    dfpsServicePackages?: DropDown[];
    ssccServicePackages?: DropDown[];
    dfpsGroServicePackages?: DropDown[];
    ssccGroServicePackages?: DropDown[];
}

export interface AddonServicePackage {
    id?: number;
    servicePackageId?: number;
    servicePackageCode?: string;
    endReason?: string;
    endReasonComments?: string;
    startDate?: string;
    endDate?: string;
}
