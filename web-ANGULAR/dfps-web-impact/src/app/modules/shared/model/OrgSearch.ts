export interface OrgSearchRequest {
    ein: string;
    otherName: string;
    legalName: string;
}

export interface OrgSearchResponse {
    ein: string;
    otherName: string;
    legalName: string;
    status: string;
    addressLn1: string;
    addressCity: string;
    county: string;
}

