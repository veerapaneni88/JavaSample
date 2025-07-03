import { DropDown } from 'dfps-web-lib';
import { Resource } from './case';
import { CodesDto } from './codesDto';
import {Event} from './event'
export interface SearchStandardDisplayRes {
    category: DropDown[];
    caseName: string;
    stageId: number;
}

export interface StandardSearchReq {
    keyword: string;
    category: string;
}

export interface StandardSearchResult {
    id: number;
    type: string;
    category: string;
    number: string;
    sds: string;
    keyword: string;
    effectiveDate: string;
    endDate: string;
    versionNumber: number;
    nameUserCreated: string;
    createdDate: string;
    nameUserLastModified: string;
    lastDateModified: string;
    weightCode: string;
    weight: number;
    controlIndicator: string;
    wvRiskDecode: string;
}

export interface WaiverVarianceDetailRes {
    waiverVariance: WaiverVariance;
    event: Event;
    pageMode: string;
    cacheKey: string;
    caseName: string;
    stageId: string;
    resourceId: string;
    homeStatus: string;
    homeCategory: string;
    isNewUsing: string;
    newUsingParam: string;
    submittedForApproval: boolean;
    reviewRequestStatus: WavierVarianceReviewStatus[];
}

export interface WaiverVariance {
    id: number;
    eventId: number;
    stageId: number;
    resourceId: number;
    request: string;
    relative: string;
    fictiveKin: string;
    unrelated: string;
    description1: string;
    description2: string;
    description3: string;
    description4: string;
    description5: string;
    description6: string;
    description7: string;
    childrenInCare: string;
    ages: string;
    email: string;
    mail: string;
    fax: string;
    handDelivered: string;
    standardId: number;
    originalRequestDate: string;
    requestUntilDate: string;
    effectiveDate: string;
    endDate: string;
    status: string;
    denialReason: string;
    reviewStatus: string;
    reviewRequestDate: string;
    reviewDecisianDate: string;
    reviewResultDate: string;
    condition1: string;
    condition2: string;
    condition3: string;
    result: string;
    outcomeDate: string;
    verifiedHome: string;
    caseId: number;
    personId: number;
    isSaveAndSubmit: boolean;
    approvalMode: boolean;
    resourceName: string;
    resourceStatus: string;
    resourceCategory: string;
    resourceCategoryCode: string;
    resourceStatusCode: string;
    caseName: string;
    standardResult: Standard;
    eventResult: Event;
    lastUpdatedDate: string;
    resourceResult: ResourceResult;
    reqStatusDecisionAdmin: boolean;
    standard: Standard;
}

export interface Standard {
    id: number;
    type: string;
    category: string;
    number: string;
    sds: string;
    keyword: string;
    effectiveDate: string;
    endDate: string;
    versionNumber: number;
    nameUserCreated: string;
    createdDate: string;
    nameUserLastModified: string;
    lastDateModified: string;
    weightCode: string;
    weight: number;
    controlIndicator: string;
}

export interface ResourceResult {
    resourceId: number;
    resourceFAHomeEventId: number;
    resourceFAHomeStageId: number;
    lastUpdatedDate: string;
    caseId: string;
    addressStreetLine1: string;
    addressStreetLine2: string;
    city: string;
    state: string;
    zip: string;
    addressAttention: string;
    county: string;

    sourceInquiry: string;
    type: string;
    campusType: string;
    maintainer: string;
    schoolDistrict: string;
    ownership: string;
    facilityType: string;
    hub: string;
    certifiedBy: string;
    operatedBy: string;
    setting: string;
    payment: string;
    category: string;
    ethnicity: string;
    language: string;
    maritalStatus: string;

    region: string;
    religion: string;
    respite: string;
    faHomeStatus: string;
    faHomeType1: string;
    faHomeType2: string;
    faHomeType3: string;
    faHomeType4: string;
    faHomeType5: string;
    faHomeType6: string;
    faHomeType7: string;
    status: string;
    marriageDate: string;
    closeDate: string;
    certDate: string;
    writeToHistory: string;
    careProvided: string;
    emergencyPlacement: string;
    inactive: boolean;
    transport: boolean;
    individualStudy: boolean;
    nonPRSHome: boolean;
    lastUpdatedName: string;
    name: string;
    nameIndex: string;
    contactName: string;
    phone: string;
    phoneExt: string;
    facilityCapacity: string;
    facilityAcclaim: string;
    vendorId: string;
    campusNumber: number;
    maxNoChildren: number;
    maxAgeFemaleChild: number;
    minAgeFemaleChild: number;
    maxAgeMaleChild: number;
    minAgeMaleChild: number;
    annualIncome: number;
    maxAgeFemaleChildHome: number;
    minAgeFemaleChildHome: number;
    maxAgeMaleChildHome: number;
    minAgeMaleChildHome: number;
    openSlots: number;
    addressComments: string;
    comments: string;
    mhmrComp: string;
    cclUpdateDate: string;
    mhmrSite: string;
    contracted: boolean;
    legalName: string;
    certifyEntity: string;
    relativeCaregiver: boolean;
    ficitiveCaregiver: boolean;
    personCount: number;
    signedAgreement: boolean;
    incomeQualified: boolean;
    manualGiven: boolean;
    allKinEmployed: boolean;
    faHomeType8: string;
    childSpecific: string;
    servicesType: string;
    childSpecificSchedRate: boolean;
    subsidyOnly: boolean;
    investigJurisdiction: string;
    multiLanguage: boolean;
    upperCaseName: string;
    contactFirstName: string;
    contactLastName: string;
    contactTitle: string;
    inactiveReason: string;
    inactiveComments: string;
    contractedCare: boolean;
    specialContract: boolean;
    nonPRSPCA: boolean;
    unrelatedCaregiver: boolean;
    facilityCareType: string;
    purgedDate: string;
    ssccCatchment: string;
    prefContactMethod: string;
    closureReason: DropDown[];
    recommendReopen: DropDown[];
    involuntaryClosure: DropDown[];
    selectedHomeTypes: DropDown[];
    nonprsPca: string;
    nextStartDate: string;
    previousEndDate: string;
    updatedHomeTypes: CodesDto[];
}

export enum WaiverVarianceView {
    New = 'NEW',
    VIEW = 'VIEW',
    EDIT = 'EDIT',
}

export interface WavierVarianceReviewStatus {
    originalRequestDate: string;
    effectiveDate: string;
    endDate: string;
    status: string;
    denialReason: string;
}
