import { DropDown } from 'dfps-web-lib';

export interface ResourcePhoneRes {
    phoneType: DropDown[];
    resourcePhone: ResourcePhone;
}

export interface ResourcePhone {
    comments: string;
    lastUpdateDate: string;
    phoneExtension: null
    phoneNumber: string;
    phoneType: string;
    purgedDate: string;
    resourceId: number;
    resourcePhoneId: number;
}