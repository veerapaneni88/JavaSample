import { SchoolDistrict } from './../../search/model/home';
import { DropDown } from 'dfps-web-lib';

export interface DisplayAddressDetail {
    addressType: DropDown[];
    county: DropDown[];
    resourceAddress: ResourceAddress;
    removePrimaryAddressType: boolean;
    schoolDistrict: SchoolDistrict[];
    state: DropDown[];
}

export interface ResourceAddress {
    addressLine1: string;
    addressLine2: string;
    addressType: string;
    attention: string;
    city: string;
    comments: string;
    country: string;
    county: string;
    countyName: string;
    dtLastUpdate: string;
    mailabilityScore: string;
    resourceAddressId: number;
    resourceId: number;
    returnAddress: string;
    schoolDistrict: string;
    state: string;
    validated: string;
    validatedDate: string;
    vendorId: string;
    zip: string
}