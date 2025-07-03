import { DropDown } from 'dfps-web-lib';
import { CodesDto } from './codesDto';

export interface DisplayHomeInformation {
    category: DropDown[];
    certifyingEntity: DropDown[];
    childCharacteristics: string;
    childesEthnicity: any;
    closureReason: DropDown[];
    ethnicity: DropDown[];
    faHomeType: DropDown[];
    homeName: string;
    involuntaryClosure: DropDown[];
    language: DropDown[];
    maritalStatus: string;
    recommendReopening: DropDown[];
    resourceId: string;
    respite: string;
    sourceOfInquiry: DropDown[];
    status: DropDown[];
    religion: DropDown[];
    resource: Resource;
    month: DropDown[];
    year: DropDown[];
    annualIncome: string;
    homeType: DropDown[];
    pageMode: string;
    homeStudyDocExists: boolean;
    disasterPlanDocExists: boolean;
    disasterPlanEventId: string;
    disasterPlanLastUpdatedDate: string;
    linkedResourceName: string;
    linkedResourceId: number;
    protectDocument: string;
    personId: string;
    disasterPlanPersonId: string;
    homeStudyNarrativeLastUpdatedDate: string;
    eventStatus: string;
    approvalFlag: boolean;
    approver: boolean;
    criminalHistoryPending: boolean;
    criminalHistoryPersonList: any[];
    closingSummaryDocExists: boolean;
    rejectClosure: boolean;
    attentionMessages: string[];
    showInvalidateApprovalPopup: boolean;
    careEligibilityHistories: CareGiverEligibilityHistory[];
    paymentEligibilityHistories: PaymentEligibilityHistory[];
    kinAssessment: boolean;
    submittedForApproval: boolean;
    hasOpenPlacements: boolean;
    hasIndLegalCountyMatch: boolean;
    placementCount: any;
}

export interface HomeInformation {
    category: string;
    status: string;
    ethinicity: string;
    language: string;
    homeName: string;
    resourceId: string;
}

export interface Resource {
    addressAttention: string;
    addressComments: string;
    addressStreetLine1: string;
    addressStreetLine2: string;
    allKinEmployed: boolean;
    annualIncome: number;
    campusNumber: number;
    campusType: string;
    careProvided: boolean;
    caseId: number;
    category: string;
    cclUpdateDate: string;
    certDate: string;
    certifiedBy: string;
    certifyEntity: string;
    childSpecific: boolean;
    childSpecificSchedRate: boolean;
    city: string;
    closeDate: string;
    closureReason: string;
    comments: string;
    contactFirstName: string;
    contactLastName: string;
    contactName: string;
    contactTitle: string;
    contracted: boolean;
    contractedCare: boolean;
    county: string;
    emergencyPlacement: boolean;
    ethnicity: string;
    faHomeStatus: string
    faHomeType1: string;
    faHomeType2: string;
    faHomeType3: string;
    faHomeType4: string;
    faHomeType5: string;
    faHomeType6: string;
    faHomeType7: string;
    faHomeType8: string;
    facilityAcclaim: string;
    facilityCapacity: number;
    facilityCareType: string;
    facilityType: string;
    ficitiveCaregiver: boolean;
    hub: string;
    inactive: boolean;
    inactiveComments: string;
    inactiveReason: string;
    incomeQualified: boolean;
    individualStudy: boolean;
    investigJurisdiction: string;
    involuntaryClosure: string;
    isAssociate: string;
    language: string;
    lastUpdatedDate: string;
    lastUpdatedName: string;
    legalName: string;
    maintainer: string;
    manualGiven: boolean;
    maritalStatus: string;
    marriageDate: string;
    maxAgeFemaleChild: number;
    maxAgeFemaleChildHome: number;
    maxAgeMaleChild: number;
    maxAgeMaleChildHome: number;
    maxNoChildren: number;
    mhmrComp: string;
    mhmrSite: string;
    minAgeFemaleChild: number;
    minAgeFemaleChildHome: number;
    minAgeMaleChild: number;
    minAgeMaleChildHome: number;
    multiLanguage: boolean;
    name: string;
    nameIndex: string;
    nonPRSHome: boolean;
    nonPRSPCA: boolean;
    openSlots: number;
    placementCount: number;
    operatedBy: string;
    ownership: string;
    parentResourceId: number;
    parentResourceType: string;
    payment: number;
    personCount: number;
    phone: string;
    phoneExt: string;
    prefContactMethod: string;
    purgedDate: string;
    recommendReopen: string;
    region: string;
    relativeCaregiver: boolean;
    religion: string;
    resourceAddress: any;
    resourcePhone: DropDown[];
    resourceFAHomeEventId: number;
    resourceFAHomeStageId: number
    resourceId: number;
    resourceScore: number;
    respite: string;
    schoolDistrict: string;
    servicesType: string;
    setting: string;
    signedAgreement: string;
    sourceInquiry: string;
    specialContract: boolean;
    ssccCatchment: string;
    state: string;
    status: string;
    subsidyOnly: boolean;
    transport: boolean;
    type: string;
    unrelatedCaregiver: boolean;
    upperCaseName: string;
    vendorId: number;
    writeToHistory: boolean;
    zip: string;
    attention: string;
    address: string;
    selectedCharacteristics: string;
    onHold: boolean;
    selectedMaleMinYearHomeInterest: number;
    selectedMaleMinMonthHomeInterest: number;
    selectedMaleMaxYearHomeInterest: number;
    selectedMaleMaxMonthHomeInterest: number;
    selectedFemaleMinYearHomeInterest: number;
    selectedFemaleMinMonthHomeInterest: number;
    selectedFemaleMaxYearHomeInterest: number;
    selectedFemaleMaxMonthHomeInterest: number;
    selectedMaleMinYear: number;
    selectedMaleMinMonth: number;
    selectedMaleMaxYear: number;
    selectedMaleMaxMonth: number;
    selectedFemaleMinYear: number;
    selectedFemaleMinMonth: number;
    selectedFemaleMaxYear: number;
    selectedFemaleMaxMonth: number;
    selectedHomeTypes: DropDown[];
    selectedChildEthnicities: string;
    isCaregiverMaternal: boolean,
    isCaregiverPaternal: boolean,
    assessmentStatus: string;
    assessmentStatusDecode: string;
    assessmentApprovedDate: string;
    isPlacementCourtOrdered: boolean;
    kinCaregiverEligibilityStatus: string;
    agreementSignedDate: string;
    isPaymentCourtOrdered: boolean;
    hasBegunTraining: boolean;
    resourceLinks: any[];
    placementDetails: any[];
    fplAmount: number;
    kinCaregiverPaymentEligibilityStatus: string;
    paymentStartDate: string;
    courtOrderedPaymentDate: string;
    courtOrderedPlacementDate: string;
    isHouseholdMeetFPL: boolean;
    hasIndLegalCountyMatch: boolean;
}

export interface ResourceAddress {
    addressLine1: string;
    addressLine2: string;
    addressType: string;
    attention: string;
    city: string;
    comments: string;
    country: string;
    county: string;
    countyName: string;
    dtLastUpdate: string;
    latitude: number;
    longitude: number;
    mailabilityScore: string;
    resourceAddressId: number;
    resourceId: number;
    returnAddress: string;
    returnGcd: string;
    schoolDistrict: string;
    state: string;
    validated: string;
    validatedDate: string;
    vendorId: number;
    zip: string;
    zipExt: string;
}

export interface HomeInformationResponse {
    cacheKey: string;
}

export interface AdminReviewResponse {
    cacheKey: string;
}

export interface CareGiverEligibilityHistory {
    id: number;
    createdDate: string;
    createdBy: string;
    assessmentStatus: string;
    assessmentStatusDecode: string;
    assessmentApproveDate: string;
    courtOrderedPlacementDate: string;
    placementCourtOrdered: string;
    kinEligibilityCode: string;
    kinEligibilityDecode: string;
    resourceId: number;
    assessmentValid: boolean;
}

export interface PaymentEligibilityHistory {
    id: number;
    courtOrderedPayment: boolean;
    courtOrderedPaymentStartDate: string;
    incomeQualified: boolean;
    paymentEligibilityStatusCode: string;
    paymentEligibilityStatus: boolean;
    resourceId: number;
    childAge: number;
    signedAgreement: boolean;
    trainingBegun: boolean;
    signedAgreementDate: string;
    paymentEligibilityEndDate: string;
    courtOrderedPaymentDate: string;
    placementEventId: string;
    legalStatusCode: string;
    legalStatusDecode: string;
    childFullName: string;
    updatedBy: string;
}

export interface PaymentInformationRes {
    caseName: string;
    kinPaymentEligibility: string;
    submittedForApproval:boolean;
    resourceId: string;
    monthlyExtensions: MonthlyRequestExtension[],
    careGiverPayments: CareGiverPayment[],
    kinMonthlyPayments: MonthlyKinshipPayment[],
    annualReimbursements: PostPMCAnnualReimbursement[],
    childNames: DropDown[];
    goodCause: DropDown[];
    monthlyExtension: MonthlyRequestExtension;
    showMultipleSadMsg: boolean;
}

export interface MonthlyRequestExtension {

    childId: string;
    childName: string;
    careGiverName: string;
    eventStatus: string;
    paymentType: string;
    goodCause: string;
    comments: string;
    totalAmountRequested: number;
    totalAmountPaid: number;
    totalUnitsRequested: number;
    totalUnitsPaid: number;
    startDate: Date;
    endDate: Date;
    lastUpdateDate: Date;
    termDate: Date;
    serviceDate: Date;
    attentionMessages: string[];

}

export interface CareGiverPayment {
    childId: number;
    childName: string;
    statusDate: Date;
    totalUnitsUsed: number;
    totalUnitsRequested: number;
    totalUnitsRemaining: number;
    totalAmountUsed: number;
    totalAmountRequested: number;
    totalAmountRemaining: number;
    tanfStartDate: Date;
    tanfTermDate: Date;
    nonTanfStartDate: Date;
    nonTanfTermDate: Date;
    legalStatusDate: Date;
    multipleServiceAuthDetails: boolean
}

export interface MonthlyKinshipPayment {
    childName: string;
    careGiverName: string;
    serviceCode: string;
    totalUnitsRequested: number;
    totalAmountRequested: number;
    serviceMonth: number;
    serviceYear: number;
    paymentStatus: string;
    warrantDate: Date;
    invoiceId: number;
}

export interface PostPMCAnnualReimbursement {
    childName: string;
    careGiverName: string;
    serviceCode: string;
    totalAmountRequested: number;
    totalAmountUsed: number;
    startDate: Date;
    invoiceStatus: string;
}
export interface AdminReviewDto {
    appealRequestDate: Date;
    authority: string;
    otherAuthority: string;
    caseId: number;
    caseName:string;
    changeRoleToSp: boolean;
    dueDate: Date;
    emergencyRelease: boolean;
    emergencyReleaseDate: Date;
    eventId: number;
    hearingDate: Date;
    lastUpdateDate: Date;
    notificationDate: Date;
    personId: number;
    personReviewed: string;
    relatedStageId: number;
    requestedBy: string;
    otherRequestedBy: string;
    requestedByName: string;
    result: string;
    delayedReason: string;
    otherDelayedReason: string;
    reviewDate: Date;
    stageId: number;
    stageName: string;
    status: string;
    type: string;
}


export interface DisplayAdminReviewInformation {
    adminReviewDto: AdminReviewDto;
    attentionMessages: string[];
    authority: DropDown[];
    otherAuthority: string;
    blobExists: boolean;
    coverLetterToRequesterEnglish: string,
    coverLetterToRequesterSpanish: string,
    disableChangeSecurity: boolean;
    event: any;
    eventApproved: boolean;
    eventStatus: string;
    narrativeLastUpdatedDate: string,
    notificationToParentEnglish: string,
    notificationToParentSpanish: string,
    pageMode: string;
    personDtoList: [];
    program: string,
    protectDocument: boolean,
    requestedBy: DropDown[];
    otherRequestedBy: string;
    result: DropDown[];
    delayedReason: DropDown[];
    showFindings: boolean;
    showLicensing: boolean;
    showNotification: boolean;
    showReqParent: boolean;
    stageType: string,
    stageCode: string,
    status: DropDown[];
    type: DropDown[];   
}
