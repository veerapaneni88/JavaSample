export interface CentralRegistrySearchRequest {
    firstName: string;
    lastName: string;
    requestId: number;
    ssn: number;
    dob: string;
    searchType: number;
}

export interface CentralRegistrySearchResponse {
    centralRegSearchResults: CentralRegistrySearchResult[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface CentralRegistrySearchResult {
    status: string;
    requestId: number;
    primReqId: number;
    primaryName: string;
    matchName: string;
    dob: string;
    age: number;
    ssn: string;
    gender: string;
    addressLine1: string;
    addressLine2: string;
    city: string;
    zip: string;
    county: string;
    score: number;
    createdDate: string;
    dtSubmitted: string;
}