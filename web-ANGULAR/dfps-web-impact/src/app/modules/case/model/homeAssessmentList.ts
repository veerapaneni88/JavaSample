export interface DisplayHomeAssessmentList {
    caseId: number;
    caseName: string;
    homeAssessmentsList: HomeAssessmentsList[];
    stageName: string;
    pageMode: string;
}

export interface HomeAssessmentsList {
    createdDate: string;
    description: string;
    fullName: string;
    id: number;
    stageId: number;
    status: string;
    type: string;
}