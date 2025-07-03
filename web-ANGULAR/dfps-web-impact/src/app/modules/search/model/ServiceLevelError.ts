import { DropDown } from 'dfps-web-lib';

export interface DisplayServiceLevelError {
    region: DropDown[];   
    county: DropDown[];   
    regionCounty: DropDown[];   
}

export interface ServiceLevelErrorRequest {
    personId: string;
    errorText: string;
    fromDate: string;
    toDate: string;
    region: string;
    county: string[];
    tprErrors: string;
    fpsErrors: string;
    markedForDeletion: string;
}

export interface ServiceLevelErrorSearchRes {
    serviceLevelErrors: ServiceLevelError[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface ServiceLevelError {
    id:string,
    region: string;
    personName: string;
    personId: string;
    asl: string;
    startDate: any;
    endDate: any;
    procDate: any;
    reviewType: string;
    errorText: string;
}

export interface ServiceLevelErrorResponse {
    serviceLevelErrors: ServiceLevelError[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];  
}

  