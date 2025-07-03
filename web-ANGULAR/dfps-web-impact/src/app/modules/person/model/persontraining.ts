import { DropDown } from 'dfps-web-lib';

export interface PersonTrainingRes {    
    personId: string; 
    name: string;
    personTraining: PersonTraining; 
    trainingType?: DropDown[]; 
    pageMode: string;    
    kinStage: boolean;
}

export interface PersonTraining {
    id: string;
    personId: string; 
    lastUpdatedDate: string;
    title: string;
    type: string;
    trainingDate: string;
    evaluationComponent: string;
    hours: number;
    trainingSession: string;
    kinTrainingCompleted: string;
    purgedDate: string;
}

export interface CodeDto {
    codeType: string;
    code: string;
    decode: string;
    nbrOrder: string;
    dateEnded: string; 
}