import { DropDown } from 'dfps-web-lib';

export interface AdoptionAssistanceDisplayRes {
    payeeDto: PayeeDto;
    adoptionSubsidyDto: AdoptionSubsidyDto;
    eventDto: EventDto;
    type: DropDown[];
    closureReason: DropDown[];
    withdrawalReason: DropDown[];
    stageName: string;
    countyName: string;
    recentOverrideSubType: string;
    pageMode: string;
    disableSaveButton: boolean;
    ageAtPcaAgreement: number;
    childQualify: string;
    assistanceDisqualified: string;
    fairHearing: string;
    placementStartDate: Date;
    placementEventId: number;
    isDisableDates: boolean; // set B
    isDisableEndDateAndClosureReason: boolean; //set E
    isDisableWithdrawalProcess: boolean; //SET E
    isEligibilitySpecialist: boolean;
    informationalMessages: any;
    loginUserId: number;
    showEnhancedPca: boolean;
    isSuperUser: boolean;
  }

  export interface AdoptionSubsidyDto {
     id : number;
     personId: number;
     payeeId: number;
     placementEventId: number;
     subsidyAmount: number;
     closureReason: string;
     determination: string;
     agreementRetentionDate: Date;
     agreementSentDate: Date;
     applicationReturnedDate: Date;
     applicationSentDate: Date;
     approvedDate: Date;
     effectiveDate: Date;
     endDate: Date;
     lastInvoiceDate: Date;
     thirdParty: boolean;
     process: boolean;
     reason: string;
     applicationId: string;
     recertificationId: string;
     nextRecertificationDate: Date;
     withdrawReason: string;
     withdrawalDate: Date;
     withdrawalComments: string;
     eligibilityOverridden:boolean;
 
 }

 export interface PayeeDto {
    addressStreetLine1: string;
    addressStreetLine2: string;
    city: string;
    county: string;
    lastUpdatedDate: Date;
    name: string;
    resourceId: number;
    state: string;
    vendorId: string;
    zip: string;
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