import { DropDown } from 'dfps-web-lib';

export interface EleKinAssessmentListRes {
    kinAssessments?: KinAssessment[];
    caseName?: string;
    stageName?: string;
    caseId?: number;
    stageId?: number;
    editMode?: boolean;
}

export interface KinAssessment {
    id?: number;
    caseId?: number;
    stageId?: number;
    eventId?: number;
    personId?: number;
    eventType?: string;
    eventStatus?: string;
    eventDescription?: string;
    stageCode?: string;
    stageName?: string;
    personName?: string;
    createdBy?: string;
    createdDate?: string;
}

export interface KinAssessmentRes {
    kinAssessmentDetail?: KinAssessmentDetail;
    caseId?: number;
    stageId?: number;
    eventId?: number;
    approvalReasonList: DropDown[];
    fixer:  boolean;
    pageMode : string;
}

export interface KinAssessmentDetail {
    kinPlacementInfo?: KinPlacementInfo[];
    kinCaregiverInfo?: KinCaregiverInfo[];
    id?: number;
    caseId?: number;
    stageId?: number;
    eventId?: number;
    eventStatus?: string;
    idSvcAuth?: number;
    caregiverName?: string;
    caregiverId?: number;
    svcAuthDate: string;
    homeAssmtSubmittedDate: string;
    approvalCd: string;
    dtOfApproval: string;
    dtOfDenial: string;
    indAutoPopulate: string;
    indSaveComplete: string;
    indCriminalHistory: string;
    indAbuseNeglectHistory: string;
    indOtherReason: string;
    txtComments?: string;
    chKinSafety?: string;
    chAddendum?: string;
    abnKinSafety?: string;
    abnAddendum?: string;
    denialReasonComments?: string;
    dnCommentEnteredBy?: string;
    dtDnCommentEntered?: string;
    dnCommentModifiedBy?: string;
    dtDnCommentModified?: string;
    fixedEdited?: string;
}


export interface KinPlacementInfo {
    id?: number;
    childId?: number;
    childName?: string;
    dtOfPlacement: string;
}

export interface KinCaregiverInfo {
    code?: number;
    decode?: string;
}

