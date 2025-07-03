export interface Event {
    eventId: number;
    personId: number;
    stageId: number;
    lastUpdatedDate: string;
    type: string;
    caseId: number;
    taskCode: string;
    description: string;
    occurredDate: string;
    status: string;
    createdDate: string;
    modifiedDate: string;
    purgedDate: string;
}