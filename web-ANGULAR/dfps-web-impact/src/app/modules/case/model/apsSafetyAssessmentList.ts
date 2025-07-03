
export interface DisplayApsSafetyAssessmentDetails {
    actionCategoryCodes: [];
    actionOutcomeCodes: [];
    actionResultCodes: [];
    apsSaId: number;
    apsSafetyAssessmentDto: ApsSafetyAssessmentDto;
    apsSafetyContacts: ApsSafetyContacts[];
    apsServicePlanDto: ApsservicePlanDto;
    assessmentType: [];
    caretakerList: CaretakerList[];
    caseId: number;
    caseName: string;
    contactStandardsCodeDtos: [];
    eventId: number;
    eventStatus: string;
    informationalMessages: any[];
    preSingleStage: boolean;
    nbrOfFaceToFaceContacts: number;
    nbrOfOtherContacts: number;
    nbrOfSafetyContacts: number;
    latestApsSafetyContacts: [];
    pageMode: string;
    pageTitle: string;
    priorityCodes: [];
    safetyResponses: SafetyResponses[];
    stageId: number;
    isDFSectionComplete: boolean;
    caseInitiationSectionComplete: boolean;
    face2FaceSectionComplete: boolean;
    immedIntervenSectionComplete: boolean;
    safetyDecisionSectionComplete: boolean;
    stageCode: string;
    apsSafetyAssessmentNarrativeDto: ApsSafetyAssessmentNarrativeDto;
    contactNarrativeDto: ContactNarrativeDto;
    roraRecompleteMsgValid: boolean;
    supervisorConsultRequired: boolean;
    incompleteSectionMessages: [];
    iisectionExpanded: boolean;
    safetyDecisionExpanded: boolean;
    errorMessagesInRed: string[];
}

export interface ApsservicePlanDto {
    actionOutcomeCodes: [];
    actionCategoryCodes: [];
    actionResultCodes: [];
    activeMonitoringPlan: any;
    allegData: any;
    assessmentMonitoringPlan: any;
    caseId: number;
    currSelectedSourcesList: [];
    eventId: number;
    id: number;
    servicePlanSources: any;
    monitoringPlanList: any;
    servicePlanEventId: number;
    selectedIISrc: [''],
    immediateInterventionSources: ImmediateInterventionSources[];
    snaIcsData: any;
}

export interface ImmediateInterventionSources {
    allegationId: any;
    apsSaId: number;
    apsSaResponseId: number;
    apsServicePlanSourceLabelDtoList: []
    apsSnaId: any;
    apsSnaResponseId: any;
    apsSpId: any;
    createdPersonId: any;
    id: number;
    lastUpdatedPersonId: any;
    selected: any;
    serviceProblems: [];
    sourceCode: string;
    sourceCodeDecode: string;
    sourceText: string;
    sourceType: string;
}

export interface ApsSafetyAssessmentDto {
    apsSaAssmtLookupId: number;
    assmtCompletedDate: any;
    assmtTypeCode: string;
    createdDate: Date;
    createdPersonId: number;
    createdPersonName: string
    currentPriority: any;
    id: number;
    indCaretakerNotApplicable: boolean;
    indCiCompleted: boolean;
    indCiImmediateIntervention: boolean;
    indInterventionsInPlace: boolean;
    indReferralRequired: boolean;
    initialPriority: any;
    lastUpdatedDate: any;
    lastUpdatedPersonId: number;
    priorityComments: any;
    safetyDecisionCode: string
    eventStatus: any;
    updatedPersonName: string
    versionNumber: number
}

export interface SafetyResponses {
    answerCode: any;
    apsSaQuestionLookupId: number;
    apsSaResponseId: number;
    isResponseYes: boolean;
    nmDefinition: string;
    questionCode: string;
    questionText: string;
    responseCode: any;
    sectionCode: string;
}

export interface ApsSafetyContacts {
    contactEventDto: any;
    contactMethodCode: any;
    contactOccurredDate: Date
    contactOccurredTime: any;
    contactType: string;
    contactTypeCode: string;
    contactWorkerFullName: string;
    contactWorkerId: number;
    eventId: number;
    indContactAttempted: string;
    contactAttempted: boolean;
}

export interface CaretakerList {
    caseId: number;
    fullName: string;
    isCaretakerSelected: any;
    personId: number;
    personType: string;
    relInt: string;
    role: string;
}

export interface ApsSafetyAssessmentNarrativeDto {
    eventId: number;
    createdPersonId: number;
    updatedPersonId: number;
    lastCreatedDate: any;
    lastUpdatedDate: any;
    documentTemplateId: number;

}

export interface ContactNarrativeDto {
    eventId: number;
    lastUpdatedDate: any;
    caseId: number;
    documentTemplateId: number;
}
