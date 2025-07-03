
export interface Allegation {
    allegedPerpetrator?: Person;
    victim?: Person;
    caseId?: string;
    stageId?: string;
    stageCode?: string;
    duration?: string;
    type?: string;
    disposition?: string;
    severity?: string;    
}

export interface Person {
    personId?: string;
    fullName?: string;
}

export interface AllegationDetailRes {
    allegations?: Allegation[];
    caseId?: string;
    stageName?: string;
}
