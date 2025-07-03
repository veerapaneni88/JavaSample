export interface PlacementLogRes {
    homeName: string;
    resourceId: string;
    resourceName: string;
    resourceType: string;
    placementLogs: PlacementLog[];
    caseId: string;
    stageId: string;
    stageCode: string;
}

export interface PlacementLog {
    name: string;
    personId: string;
    dob: string;
    placementDate: string;
    endDate: string;
    removalReason: string;
    livingArrangement: string;
    removalReasonSubType: string;
    adoptionConsummated: string;
}