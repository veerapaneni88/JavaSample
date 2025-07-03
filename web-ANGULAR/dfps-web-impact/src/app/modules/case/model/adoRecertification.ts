export interface AdoptionRecertificationDisplayRes {
    personId: number;
    socialSecurityNumber: string;
    medicaidNumber: string;
    dateOfBirth: Date;
    fullName: string;
    currentAge: number;
    workerName: string;
    workerPhone: string;
     adoptionRecertificationDto: AdoptionRecertificationDto;
     eventDto: EventDto ;
    pageMode: string;
 }
 
 export interface AdoptionRecertificationDto {
    id: number;
    lastUpdateDate: Date;
    personId: number;
    age: number;
    completedDate: Date;
    documentReceived:boolean;
    documentSentDate: Date;
    documentReceivedDate: Date;
    highSchool:boolean;
    postSecondary:boolean;
    promoteEmployment:boolean;
    minimumHours:boolean;
    incapableOfMedicalCondition:boolean;
    createdPersonId: number;
    createdDate: Date;
    lastUpdatedPersonId:number;
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