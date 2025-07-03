import { ResourcePhone } from './../../case/model/phoneDetail';
import { ResourceAddress } from '@case/model/case';
import { DropDown } from 'dfps-web-lib';

export interface DisplayHomeInformation {
    category: DropDown[];
    certifyingEntity: DropDown[];
    childCharacteristics: string;
    childesEthnicity: any;
    closureReason: DropDown[];
    ethnicity: DropDown[];
    faHomeType: DropDown[];
    involuntaryClosure: DropDown[];
    language: DropDown[];
    maritalStatus: string;
    recommendReopening: DropDown[];
    resourceId: string;
    respite: string;
    sourceOfInquiry: DropDown[];
    status: DropDown[];
    religion: DropDown[];
    month: DropDown[];
    year: DropDown[];
    homeType: DropDown[];
    counties: DropDown[];
    state: DropDown[];
    schoolDistrictPrimary: DropDown[];
    phoneType: DropDown[];
    statusInquiry: string;
}

export interface FadHomeRequest {
    homeName: string;
    category: string;
    status: string;
    ethnicity: string;
    language: string;
    region: string;
    annualIncome: number;
    maritalStatus: string;
    marriageDate: string;
    sourceOfInquiry: string;
    respite: string;
    individualStudyProcess: boolean;
    childesEthnicity: string[];
    noOfChildren: number;
    willTransportChildren: boolean;
    emergencyPlacements: boolean;
    comments: string;
    childCharacteristics: string[];
    maleAgeRangeInterestsMinYear: number;
    maleAgeRangeInterestsMinMonth: number;
    maleAgeRangeInterestsMaxYear: number;
    maleAgeRangeInterestsMaxMonth: number;
    femaleAgeRangeInterestsMinYear: number;
    femaleAgeRangeInterestsMinMonth: number;
    femaleAgeRangeInterestsMaxYear: number;
    femaleAgeRangeInterestsMaxMonth: number;

    typePrimaryAddress: string;
    vendorIdPrimaryAddress: string;
    attentionPrimaryAddress: string;
    addressLn1PrimaryAddress: string;
    addressLn2PrimaryAddress: string;
    cityPrimaryAddress: string;
    statePrimaryAddress: string;
    zipPrimaryAddress: string;
    zipExtPrimaryAddress: string;
    countyPrimaryAddress: string;
    commentsPrimaryAddress: string;
    schoolDistrictPrimaryAddress: string;
    lastUpdateDatePrimaryAddress: string;
    validatedPrimaryAddress: boolean;
    validatedDatePrimaryAddress: string;

    typeBusinessAddress: string;
    vendorIdBusinessAddress: string;
    attentionBusinessAddress: string;
    addressLn1BusinessAddress: string;
    addressLn2BusinessAddress: string;
    cityBusinessAddress: string;
    stateBusinessAddress: string;
    zipBusinessAddress: string;
    zipExtBusinessAddress: string;
    countyBusinessAddress: string;
    commentsBusinessAddress: string;
    schoolDistrictBusinessAddress: string;
    lastUpdateDateBusinessAddress: string;
    validatedBusinessAddress: boolean;
    validatedDateBusinessAddress: string;

    phoneTypePrimary: string;
    phonePrimary: string;
    phoneExtPrimary: string;
    phoneCommentsPrimary: string;
}


export interface KinHomeRequest {
    homeName: string;
    category: string;
    status: string;
    ethnicity: string;
    language: string;
    region: string;
    annualIncome: number;
    maritalStatus: string;
    marriageDate: string;
    sourceOfInquiry: string;
    respite: string;
    individualStudyProcess: boolean;
    childesEthnicity: string[];
    noOfChildren: number;
    willTransportChildren: boolean;
    emergencyPlacements: boolean;
    comments: string;
    childCharacteristics: string[];
    maleAgeRangeInterestsMinYear: number;
    maleAgeRangeInterestsMinMonth: number;
    maleAgeRangeInterestsMaxYear: number;
    maleAgeRangeInterestsMaxMonth: number;
    femaleAgeRangeInterestsMinYear: number;
    femaleAgeRangeInterestsMinMonth: number;
    femaleAgeRangeInterestsMaxYear: number;
    femaleAgeRangeInterestsMaxMonth: number;

    typePrimaryAddress: string;
    vendorIdPrimaryAddress: string;
    attentionPrimaryAddress: string;
    addressLn1PrimaryAddress: string;
    addressLn2PrimaryAddress: string;
    cityPrimaryAddress: string;
    statePrimaryAddress: string;
    zipPrimaryAddress: string;
    zipExtPrimaryAddress: string;
    countyPrimaryAddress: string;
    commentsPrimaryAddress: string;
    schoolDistrictPrimaryAddress: string;
    lastUpdateDatePrimaryAddress: string;
    validatedPrimaryAddress: boolean;
    validatedDatePrimaryAddress: string;
    resourceAddressId: string;

    phoneTypePrimary: string;
    phonePrimary: string;
    phoneExtPrimary: string;
    phoneCommentsPrimary: string;
    resourcePhoneId: string;

    addressModified: boolean;
    phoneModified: boolean;
}

export interface SchoolDistrict {
    schoolDistrictCode: string;
    schoolDistrictTxtName: string;
    schoolDistrictTxCountyCode: string;
}

export interface HomeResponse {
    cacheKey: string;
}

export interface Resource {
    resourceId: number;
    name: string;
    addressStreetLine1: string;
    addressStreetLine2: string;
    city: string;
    county: string;
    state: string;
    zip: string;
    phone: string;
    phoneExt: string;
    resourceAddress: ResourceAddress[];
    resourcePhone: ResourcePhone[];
}

export interface HomeDefaults {
    category: string;
    status: string;
}