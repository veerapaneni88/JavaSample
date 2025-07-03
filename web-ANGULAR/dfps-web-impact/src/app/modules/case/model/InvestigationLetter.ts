import { Time } from '@angular/common';
import { DropDown } from 'dfps-web-lib';
import { Allegation } from './Allegation';

export interface InvestigationLetterListRes {
    caseId?: string;
    stageId?: string;
    stageName?: string;
    pageMode?: string;
    investigationLetters?: InvestigationLetter[];
    letterTypes?: DropDown[];
}

export interface InvestigationLetter {
    id?: string;
    addressCity?: string;
    addressState?: string;
    addressLine1?: string;
    addressLine2?: string;
    addressZip?: string;
    addressZipExt?: string;
    administratorName?: string;
    allegationSummary?: string;
    incidentLocation?: string;
    interviewStatus?: string;
    letterMethod?: string;
    letterType?: string;
    dispositionSummary?: string;
    interviewDate?: Time;
    allegedPerpetratorPhoneNumber?: string;
    caseId?: string;
    eventId?: string;
    fecilityId?: string;
    letterAllegationLinkId?: string;
    adminReviewStaffPersonId?: string;
    selectedChildsPersonId?: string;
    investigatorPersonId?: string;
    childsPersonId?: string;
    letterSentFromPersonId?: string;
    letterSentToPersonId?: string;
    resourceId?: string;
    stageId?: string;
    incidentSummary?: string;
    investigatorFindings1?: boolean;
    investigatorFindings2?: boolean;
    investigatorDesignation?: string;
    investigatorEmail?: string;
    investigatorName?: string;
    investigatorPhoneNumber?: string;
    letterBodyDear?: string;
    letterBody?: string;
    monitoringConcerns?: string;
    operationHomeName?: string;
    bodyOperationName?: string;
    recipientNumber?: string;
    createdPersonId?: string;
    createdDate?: string;
    lastUpdatedDate?: string;
    lastUpdatedPersonId?: string; 
    createdBy?: string;
    lastModifiedBy?: string;
    event?: string;
    isSubmit: boolean;
    canDelete?: boolean;
    canAdd?: boolean;

    lastPersonSearched?: string;
    parentName?: string;
    childsFirstName?: string;

    allegedPerpetratorName?: string;
    apAllegationFindings: AllegationFinding[];
    adminReviewStaffDetails?: string;
    initialLetterAllegations?: InitialLetterAllegations[];
    allegedVictimDtoList?: AllegedVictimDtoList[];
    allegationFindingList?: AllegationFindingList[];
    tableData: AllegationFinding[];

    apFirstName?: string;
    apMiddleName?: string;
    apLastName?: string;
    apHomePhoneNumber?: string;
    apHomeStreetAddress?: string;
    county?: string;
    operationAgencyHomeDto?: OperationAgencyHome;
    cciAllegationFindings?: AllegationFindingList[];
    operationNumber?: string;

}

export interface AllegationType {
    codeType?: string;
    code?: string;
    decode?: string;
}

export interface DispositionType {
    codeType?: string;
    code?: string;
    decode?: string;
}

export interface AllegationFinding {
    allegationType?: string;
    victimName?: string;
    finding?: string;
}

export interface AllegedVictimDtoList{
    victimName?: string;
    allegations?: string;
}

export interface InitialLetterAllegations{
    allegationType?: string;
    victimName?: string;
    victimId?: string;
    checked?: boolean;
}

export interface AllegationFindingList{
    allegationFinding?: AllegationFinding[];
    victimName?: string;
    victimId?: string;
    checked?: boolean;
    allegedPerpetratorName?: string;
}

export interface InvestigationLetterRes {
    caseId: string;
    stageId: string;
    stageName: string;
    pageMode: string;
    attentionMessages: string[];
    investigationLetter: InvestigationLetter;
    letterTypes: DropDown[]; 
    letterMethods: DropDown[];
    operationNames?: DropDown[];
    states: DropDown[]; 
    principalPersons: DropDown[];
    incidentLocation: DropDown[];
    allegationTypes: AllegationType[];
    dispositionTypes: DispositionType[];
    interviewStatuses: DropDown[];
    county: DropDown[];
    operationAgencyHomeDto:OperationAgencyHome;
}

export interface Person {
    personId?: string;
    dtLastUpDate?: Time;
    sex?: string;
    addrPersonStLn1?: string;
    addrPersonCity?: string;
    addrPersonZip?: string;
    DateOfDeath?: Time;
    DateOfBirth?: Time;
    religion?: string;
    characteristic?: string;
    livingArrangement?: string;
    guardCnsrv?: string;
    status?: string;
    deathCode?: string;
    maritalStatus?: string;
    language?: string;
    ethnicGroup?: string;
    state?: string;
    county?: string;
    dobApprox?: string;
    cancelHistory?: string;
    age?: string;
    phone?: string;
    idNumber?: string;
    firstName?: string;
    middleName?: string;
    lastName?: string;
    fullName?: string;
    occupation?: string;
    suffix?: string;
    autoPersonMerge?: string;
    disasterRelief?: string;
    dducationPortfolio?: string;
    tribeEligible?: string;
    occupationCode?: string;
    mannerDeath?: string;
    deathReasonCps?: string;
    deathCause?: string;
    deathAutopsyResult?: string;
    deathFinding?: string;
    fatalityDetails?: string;
    irReport?: string;
    abuseNeglectDeathInCare?: string;
    employee?: string;
    latestPhone?: string;
    latestEmail?: string;
}
 export interface OperationAgencyHome{
    classOperationName?: string;
    classOperationNumber?: string;
    operationImpactFacilityType?: string;
    operationImpactResourceId ?: string;
    agencyHomeName?: string;
    agencyHomeOperationNumber?: string;
    agencyHomeImpactFacilityType?: string;
    showAgencyHomeInfo?:boolean;
    addressLine1?: string;
    addressLine2?: string;
    addressCity?: string;
    addressZip?: string;
    county?: string;
    state?: string;
    administratorName?: string;
 }