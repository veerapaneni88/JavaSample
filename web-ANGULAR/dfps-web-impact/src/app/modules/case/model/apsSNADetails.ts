export interface DisplayApsSNADetails {
    apsSnaAssessmentLookupId: number;
    apsSnaId: number;
    assessmentTypeCode: string;
    caretakerId: number;
    caretakerList: CaretakerList[];
    caseId: number;
    caseName: string;
    createdUserName: string;
    dateCreated: Date;
    dateLastUpdate: Date;
    eventId: number;
    eventStatus: string;
    indPrimaryCaretaker: boolean;
    informationalMessages: [];
    sectionCompleteCL: boolean;
    sectionCompletePC: boolean;
    lastUpdatedUserName: string;
    needsAssessedC: [];
    pageMode: string;
    pageTitle: string;
    responses: Responses[];
    stageId: number;
    strengthsAssessedA: [];
    versionNumber: number;
    incompleteDomainsCL: [];
    incompleteDomainsPC: [];
    clientSectionExpanded: boolean;
    pcsectionExpanded: boolean;
    rootCausesSectionExpanded: boolean;
}

export interface Responses {
    answers: Answers[];
    id: number;
    apsSnaDomainLookupId: number;
    includeInServicePlan: string;
    createdPersonId: number;
    txtOtherDescription: string;
    domainCode: number;
    domainText: string;
    sectionCode: string;
    definitionName: string;
    apsSnaAnswerLookupId: number;
    servicePlanDomainId: number;
    responseCode: string;
    indIncludeServicePlan: boolean;
    answerText: string;
}

export interface Answers {
    id: number;
    apsSnaDomainLookupId: number;
    answerCode: string;
    answerText: string;
    codeValue: string;
    orderNumber: number;
    label: string;
    value: number;
}

export interface CaretakerList {
    code: number;
    decode: string;
}


