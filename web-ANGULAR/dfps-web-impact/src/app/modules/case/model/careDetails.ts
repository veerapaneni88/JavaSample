export interface DisplayCareDetails {
    action: string;
    bshowSevereColumn: boolean;
    caseId: number;
    caseName: string;
    comment: string;
    dateEventOccurred: Date;
    dateLastUpdate: Date;
    dirty: boolean;
    domains: Domains[];
    eventDateLastUpdate: Date;
    eventId: number;
    eventStatus: string;
    lifeThreateningCode: string;
    loginUserId: number;
    stageId: number;
    formTagDto: FormTagDto;
    pageMode: string;
    docExists: boolean;
    docType: string;
}

export interface FormTagDto {
    docExists: boolean;
    docType: string;
    sCase: number;
    sEvent: number;
    protectDocument:boolean;
}

export interface Domains {
    apsCareCategoryDtoList: ApsCareCategoryDtoList[];
    careDomainId: number;
    caseId: number;
    cdAllegationFocus: string;
    cdDomain: string;
    descComment: string;
    dtLastUpdate: Date;
    eventId: number;
    nbrDomainOrder: number;
    stageId: number;
    txtDomain: string;
    isComplete:boolean;
    reasonBelieveFlag:boolean;
    noneFlag:boolean;
}

export interface ApsCareCategoryDtoList {
    apsCareFactorDtoList: ApsCareFactorDtoList[];
    careCategoryId: number;
    careDomainId: number;
    caseId: number;
    cdCareCategory: string;
    cdDomain: string;
    cdReasonBelieve: string;
    dtLastUpdate: Date;
    eventId: number;
    nbrCategoryOrder: number;
    stageId: number;
    txtCategory: string;
}

export interface ApsCareFactorDtoList {
    careCategoryId: number;
    careDomainId: number;
    careFactorId: number;
    caseId: number;
    cdCareFactorResponse: string;
    cdCategory: string;
    cdFactor: string;
    dtLastUpdate: Date
    eventId: number;
    indFactorHigh: string;
    indFactorLow: string;
    indFactorMed: string;
    indFactorNa: string;
    indFactorUtd: string;
    nbrFactorOrder: number;
    stageId: number;
    txtFactor: string;
}
