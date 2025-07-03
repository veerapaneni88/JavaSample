import { DropDown } from 'dfps-web-lib';

export interface QuickFindResponse {
    requesterName: string;
    phone: string;
    caseId: string;
    region: DropDown[];
    counties: DropDown[];
    cpsInjury: string;
    cpsNytdInjury: string;
    accountingInjury: string;
    legalInjury: string;
    apsInjury: string;
    emrInjury: string;
    pageMode: string;
    quickFind: QuickFindDto;
    searchSubjects: QuickFindPersonsDto[];
    isChecked: boolean[];
}

export interface QuickFindDto {
    quickFindId: string;
    formsReferralsId: string;
    requestersName: string;
    requestersPersonId: string;
    requestersPhone: string;
    caseId: string;
    requestersRegion: string;
    requestersCounty: string;
    cps: string;
    cpsNytd: string;
    accounting: string;
    legal: string;
    aps: string;
    emr: string;
    subjects: QuickFindPersonsDto[];
    eventStatus: string;
    lastUpdatedDate: string;
    createdDate: string;
    lastUpdatePersonId: string;
    createdPersonId: string;
}

export interface QuickFindPersonsDto {
    personId: string;
    fullName: string;
    addressLine1: string;
    addressLine2: string;
    city: string;
    state: string;
    zip: string;
    dob: string;
    approximateDateOfBirth: boolean;
    ssn: string;
    ethnicity: string;
    phone: string;
    comments: string;
    quickFindPersonsId: string;
}

export interface DiligentSearchResponse {
    phone: string;
    stageId: string;
    region: DropDown[];
    county: DropDown[];
    causeNumber: string;
    pageMode: string;
    diligentSearch: DiligentSearchDto;
    subjects: DiligentSearchPerson[];
}
export interface DiligentSearchDto {
    diligentSearchId: string;
    formsReferralsId: string;
    requestersName: string;
    requestersPersonId: string;
    requestersPhone: string;
    requestersPhoneExtension: string;
    unitRole: string;
    email: string;
    caseworker: string;
    caseWorkerName: string;
    caseWorkerPersonId: string;
    caseWorkerEmail: string;
    caseWorkerPhone: string;
    caseWorkerExtension: string;
    supervisorName: string;
    supervisorPersonId: string;
    supervisorEmail: string;
    supervisorPhone: string;
    supervisorExtension: string;
    requestersRegion: string;
    requestersCounty: string;
    causeNumber: string;
    caseId: number;
    locateRelatives: boolean;
    childWithoutPlacement: boolean;
    children: DiligentSearchPerson[];
    mothers: DiligentSearchPerson[];
    fathers: DiligentSearchPerson[];
    eventStatus: string;
}

export interface DiligentSearchPerson{
   diligentSearchPersonId: string;
   locatePersonOrRelative: boolean;
   locateRelative: boolean;
   personInfoUnknown: boolean;
   fullName: string;
   addressLine1: string;
   addressLine2: string;
   city: string;
   state: string;
   zip: string;
   county: string;
   phone: string;
   dateOfBirth: string;
   ethnicity: string;
   gender: string;
   approximateDateOfBirth: boolean;
   ssn: string;
   personId: string;
   comments: string;
   personAddressId: string;
   birtCity: string;
   birthCounty: string;
   birthState: string;
   children: DiligentSearchChild[];
}

export interface DiligentSearchChild{
    diligentSearchChildId : number;
    childPersonId : string;
    childFullName : string;

}