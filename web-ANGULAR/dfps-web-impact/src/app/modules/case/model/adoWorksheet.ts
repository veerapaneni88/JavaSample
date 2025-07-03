import { DropDown } from 'dfps-web-lib';

export interface AdoptionEligibilityWorksheetDisplayRes {
    adoptionEligibilityApplicationDto: AdoptionEligibilityApplicationDto;
    adoptionEligibilityDeterminationDto: AdoptionEligibilityDeterminationDto;
    eventDto: EventDto;
    pageMode: string;
    withdrawalReason: DropDown[];
    placementEventDto: PlacementDto;
    messageList: any;
}

export interface AdoptionEligibilityApplicationDto {
    id: number;
    qualifiedSiblingPersonId: number;
    lastUpdatedDate: Date;
    personId: number;
    childQualifyAge: number;
    isInManagedCvs: boolean;
    inLicensedManagingConservatorShip: boolean;
    otherManagingConservatorShip: boolean;
    agencyName: string;
    agencyAddress: string;
    placementPlannedDate: Date;
    applicationSubmittedDate: Date;
    adoptedParentState: string;
    internationalPlacement: boolean;
    fairHearing: boolean;
    fairHearingDate: Date;
    adoptionHomeStudy: boolean;
    adpotionHomeStudyDate: Date;
    backGroundCheck: boolean;
    childAtLeastSixYears: boolean;
    childTwoMinority: boolean;
    withSibling: boolean;
    handicapCondition: boolean;
    adoptionRegularExchange: boolean;
    adoptionRegularExchangeName: string;
    emotionalBond: boolean;
    citizenship: boolean;
    permanentResident: boolean;
    qualifiedAlien: boolean;
    citizenshipUnknown: boolean;
    adoptedByCitizen: boolean;
    childEnteredUsDate: Date;
    howChildEnteredUs: string;
    documentBirthVerify: boolean;
    documentForm2250: boolean;
    medicalProfessionalDocument: boolean;
    terminationOrder: boolean;
    form2253ab: boolean;
    courtOrderDocument: boolean;
    homeStudy: boolean;
    birthVerifyForSibling: boolean;
    policyWaiver: boolean;
    fbiBackgroundCheck: boolean;
    terminationOrderSibling: boolean;
    doctorLetter: boolean;
    serviceLevelSheet: boolean;
    immigrationDoc: boolean;
    withdrawReason: string;
    withdrawReasonComments: string;
    ssaMedicalRequirement: boolean;
    ssaRequirement: boolean;
    metMedProfessionalRequirement: boolean;
    receivedDocumentFromSsa: boolean;
    locationAdoptiveFamily: boolean;
    locateEffortForAdoptiveFamily: string;
    resultedInNoAdoptivePlacement: boolean;
    resultedInNoAdoptivePlacementReason: string;
    legalStatus: string;
    parentalRightsTerminatedDate: Date;
    legalStatEventId: number;
    purgedDate: Date;
    // Fields related to sibling
    siblingFullName: string;
    siblingsAge: number;
    siblingsRelation: string;
}

export interface AdoptionEligibilityDeterminationDto {
    id: number;
    lastUpdateDate: Date;
    applicationId: number;
    childFederalFiscalYearAge: number;
    sixtyMonthsInCvs: boolean;
    applicableAge: boolean;
    withSibling: boolean;
    meetMedicalDisabilityForSsi: boolean;
    livingWithMinOrParent: boolean;
    priorAdoptionTitleIVe: boolean;
    afdcEligible: boolean;
    ssiEligible: boolean;
    livingWithMinorParentTitleIVe: boolean;
    placementStartDate: Date;
    placementAgreementDate: Date;
    agreementDate: Date;
    childAgeAtAgreement: number;
    specialNeedOutcome: boolean;
    citizenShipOutcome: boolean;
    reasonableEffortOutcome: boolean;
    dfpsManagingCvsOutcome: boolean;
    applicableChildOutcome: boolean;
    placementRequirementMet: boolean;
    adoptionAgreementOutcome: boolean;
    additionalIveRequirementMet: boolean;
    childQualify: boolean;
    assistanceDisqualified: boolean;
    placementEventId: number;
    initialDeterminationPersonId: number;
    initialDeterminationDate: Date;
    initialDetermination: string;
    finalDetermination: string;
    finalDeterminationPersonId: number;
    finalDeterminationDate: Date;
    initialDeterminationPersonName: string;
    finalDeterminationPersonName: string;
    siblingPersonId: number;
    adoptionConsummatedDate: Date;
    eligibilityDeterminationOutcome: number;
    determinationSiblingPersonFullName: string;
    determinationSiblingPersonAge: number;
    stageDeterminationSiblingPersonRelInt: string;
}

export interface EventDto {
    eventId: number;
    personId: number;
    stageId: number;
    lastUpdatedDate: Date;
    type: string;
    caseId: number;
    taskCode: string;
    description: string;
    occurredDate: Date;
    status: string;
    createdDate: Date;
    modifiedDate: Date;
    purgedDate: string;
}

export interface PlacementDto {
    placementEventId: number;
    lastUpdateDate: Date;
    childPersonId: number;
    endDate: Date;
    caseId: number;
    adultPersonId: number;
    resourceFacilityId: number;
    city: string;
    country: string;
    addressLine1: string;
    addressLine2: string;
    state: string;
    zip: string;
}
