
export interface DisplayContactStandardDetails {
    caseId: number;
    caseName: string;
    apsServicePlanMonitoringDto: apsServicePlanMonitoringDto;
}

export interface apsServicePlanMonitoringDto {
    apsSpMonitoringPlanId: [],
    apsSpId: string,
    apsSaId: string,
    planSourceCode: string ,
    planDescription: string,
    planStartDate: Date,
    planEndDate:Date ,
    numberOfFaceToFaceContacts: number,
    numberOfContactsReqd:number,
    createdPersonId: string,
    lastUpdatedPersonId: string,
    totalNumberOfContacts: number,
    planSourceCodeDecode: string,
    activeInd: boolean
}




