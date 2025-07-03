import { DropDown } from 'dfps-web-lib';

export interface DisplayServicePackageError {
    region: DropDown[];   
    county: DropDown[];   
    regionCounty: DropDown[];   
}

export interface ServicePackageErrorRequest {
    personId: string;
    errorText: string;
    fromDate: string;
    toDate: string;
    region: string;
    county: string[];
    markedForDeletion: string;
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface ServicePackageErrorSearchRes {
    servicePackageErrors: ServicePackageError[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface ServicePackageError {
    id:string,
    personId: string;
    county: string;
    region: string;
    numberMessage: string;
    errorText: string;
    personName: string;
    servicePackage: string;
    groAssessmentCompletedDate: string;
    groRecommended: string;
}

export interface ServicePackageErrorResponse {
    servicePackageErrors: ServicePackageError[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];  
}

  