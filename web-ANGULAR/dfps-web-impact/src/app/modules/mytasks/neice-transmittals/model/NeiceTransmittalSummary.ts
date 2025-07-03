import { Dropdown } from 'primeng/dropdown';

export interface NeiceTransmittalSummary {
    priorities: Dropdown[];
    rejectHoldList: Dropdown[];
    assignedToPersons: Dropdown[];
    transmittalActivity: TransmittalActivity;
    transmittalDetail: TransmittalDetail;
    childInformations: [PersonInformation];
    placementInformation: PlacementInformation;
    documentsToAttach: [DocumentsToAttach];
}

export interface TransmittalActivity {
    priority: string;
    rejectHoldInd: string;
    comment: string;
    assignedTo: string;
    lastUpdatedBy: string;
}

export interface TransmittalDetail {
    dateReceived: string;
    neiceHomeStudyId: string;
    transmittalType: string;
    transmittalTypeCode: string;
    transmittalStatusCode: string;
    transmittalStatus: string;
    transmittalPurpose: string;
    rejectHoldInd: string;
    jurisdictionState: string;
    homeStudyDueDate: string;
    sendingState: string;
    sendingAgency: string;
    transmittalCreator: string;
    compactAdministrator: string;
    receivingState: string;
    receivingAgency: string;
    homeStudyType: string;
    homeStudyTypeCode: string;
    urgentRequestInd: string;
    urgentRequestReason: string;
    otherUrgentRequestReason: string;
    summaryText: string;
    comments: string;
    attention: string;
    publicPrivateInd: string;
    concurrenceToDismiss: string;
    additionalInformation: string;
    otherInformation: string;
    taskCode: number;
    stageId: number;
    eventId: number;
    caseId: number;
}

export interface PlacementInformation {
    address: string;
    county: string;
    phoneNumber: string;
    displayFacility: boolean;
    relationship: string;
    personInfos: [PersonInformation];
    facilityName: string;
    facilityId: string;
    contactName: string
}

export interface PersonInformation {
    name: string;
    neicePid: string;
    impactPid: string;
    race: string;
    sex: string;
    dateOfBirth: string;
    gender: string;
    otherGender: string;
    hispanicOrigin: string;
    ssn: string;
    icwaEligible: string;
    titleIVEEligible: string;
    currentLegalStatus: string;
    otherLegalStatus: string;
    typeOfCare: string;
    otherTypeOfCare: string;
    resourceCode: string;
    decisionDate: string;
    decision: string;
    reasonDenial: string;
    otherReasonDenial: string;
    placementType: string;
    placementTypeCode: string;
    placementDate: string;
    compactTerminateReason: string;
    otherTerminationReason: string;
    placementTerminationDate: string;
    placementRemarks: string;
    terminationNotes: string;
}

export interface DocumentsToAttach {
    creationDate: string;
    neiceAttachementId: number;
    documentName: string;
    documentType: string;
    description: string;
}

export interface AdditionalInformation {
    additionalInformation: string;
    comments: string;
}

export interface RejectInformation {
    comments: string;
    documentType: string;
    file: any;
}