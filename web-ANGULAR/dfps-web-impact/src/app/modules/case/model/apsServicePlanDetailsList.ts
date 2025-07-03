export interface DisplayApsServicePlanDetails {
    apsSaId: number;
    approvalEvent: ApprovalEvent;
    apsServicePlanDto: ApsServicePlanDto;
    caseId: number;
    caseName: string;
    dateLastUpdate: Date;
    dirty: boolean;
    eventId: number;
    hasStageAccess: boolean;
    id: number;
    infoMessages: [];
    loggedInUser: number;
    mandatoryMessageBlack: [];
    mandatoryMessageRed: [];
    pageMode: string;
    popUpMessages: [];
    preSingleStage: boolean,
    serviceSources: {};
    stageCode: string,
    stageId: number,
    stageType: string,
    completedSAAvailable: boolean
}

export interface ApprovalEvent {
    caseId: number;
    createdDate: Date;
    description: string;
    eventId: number;
    lastUpdatedDate: Date;
    modifiedDate: Date;
    occurredDate: Date;
    personId: number;
    purgedDate: Date;
    stageId: number;
    status: string;
    taskCode: string;
    type: string;
}

export interface ApsServicePlanDto {
    caseId: number;
    servicePlanEventId: number;
    eventId: number;
    id: number;
    servicePlanSources: CommonModel[];
    immediateInterventionSources: CommonModel[];
    activeMonitoringPlan: ActiveMonitoringPlan;
    assessmentMonitoringPlan: ActiveMonitoringPlan;
    actionCategoryCodes: [];
    actionOutcomeCodes: [];
    actionResultCodes: [];
    currSelectedSourcesList: CommonModel[];
    monitoringPlanList: ActiveMonitoringPlan[];
    snaIcsData: CommonModel[];
    allegData: CommonModel[];
    updateMultiPrblmOrActnData: UpdateMultiPrblmOrActnData;
    lastUpdatedDate: Date;
    spaneexpanded: boolean,
    spcontactStandardExpanded: boolean,
    spiiexpanded: boolean,
    spmultipleProbActionExpanded: boolean,
    spsnaorICSExpanded: boolean
}

export interface CommonModel {
    id: number;
    apsSpId: number;
    serviceProblems: ServiceProblems;
    sourceType: string;
    sourceCode: string;
    sourceText: string;
    apsSaId: number;
    apsSnaId: number;
    allegationId: number;
    apsSaResponseId: number;
    apsSnaResponseId: number;
    selected: boolean;
    apsServicePlanSourceLabelDtoList: [];
    sourceCodeDecode: string;
    createdPersonId: number;
    lastUpdatedPersonId: number;
}

export interface ServiceProblems{
     id: number;
     apsSpServiceSrcId: number;
     problemDescription: number;
     outcomeCode: number;
     actions: Actions;
     selected: boolean;
     lastUpdatedDate: Date;
}

export interface Actions {
     apsSpActionId: number;
     actionCategoryCode: string;
     actionResultsCode: string;
     actionDescription: string;
     ApsSpProblemActionLinkId:number;
     apsSpId: number;
     selected: boolean;
     lastUpdatedDate: Date;
}

export interface ActiveMonitoringPlan {
    apsSpMonitoringPlanId: number;
    apsSpId: number;
    apsSaId: number;
    planSourceCode: string;
    planDescription: string;
    planStartDate: Date;
    planEndDate: Date;
    numberOfFaceToFaceContacts: number;
    numberOfContactsReqd: number;
    createdPersonId: number;
    lastUpdatedPersonId: number;
    totalNumberOfContacts: number;
    planSourceCodeDecode: string;
    activeInd: string;
}

export interface UpdateMultiPrblmOrActnData {
    actionDescription: string;
    actionCategoryCode: string;
    actionResultsCode: string;
    outcomeCode: string;
}
