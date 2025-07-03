export interface ApsCaseMgmtModel {
    caseId: number;
    stageId: number;
    stageName: string;
    overallDisposition: string;
    closureReasons: string;
    pageMode: string;
    pageTitle: string;
    preSingleStage: boolean;
    errorMessages: string;
    infoMessages: any;
    popupMessages: any;
    displaySendForReview: boolean;
    displaySaveAndSubmit: boolean;
    docExists: boolean;
    snaExists: boolean;
    priorStage: any;
    hasValidSelfNeglect: boolean;
    hasValidApAllegation: boolean;
    entityInformation: any;
    newEMRInvestigation: boolean;
    reportableConductExists: boolean;
    event: any;
    apsInvestigationDetailDto: any;
    programAdmins: any;
    selfNeglectAllegations: any;
    apAllegations: any;

}

export interface event {
    eventId: number;
    personId: number;
    stageId: number;
    lastUpdatedDate: string;
    type: string;
    caseId: number;
    taskCode: string;
    description: string;
    occurredDate: string;
    status: string;
    createdDate: string;
    modifiedDate: string;
    purgedDate: string;
}

export interface apsInvDetailDto {
    idEvent: number;
    dtLastUpdate: string;
    idCase: number;
    dtApsInvstBegun: string;
    cdApsInvstFinalPrty: any;
    dtApsInvstCmplt: string;
    cdApsInvstOvrallDisp: string;
    dtApsInvstCltAssmt: any;
    cdClosureType: string;
    indExtDoc: string;
    indLegalAction: string;
    indFamViolence: string;
    indEcs: string;
    indClient: string;
    txtClientOther: string;
    cdInterpreter: string;
    txtMethodComm: string;
    txtTrnsNameRlt: string;
    txtAltComm: string;
    dtClientAdvised: any;
    idProgramAdminPerson: any;
    idEntity: any;
    dtPurged: any;
    indSvcPlan: any;
    txtNotPrtcptSvcPln: any;
    txtAllegFinding: string;
    txtRootCause: string;
    txtDescAllg: string;
    txtAnlysEvidance: string;
    txtPrepondrncStmnt: string;
    indPcsSvcs: string;
    txtNotAdviCaseClosure: string;
    cdEmrAprvl: any;
}