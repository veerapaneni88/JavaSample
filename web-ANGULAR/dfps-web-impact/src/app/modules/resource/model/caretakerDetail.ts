import { Resource } from '@case/model/case';
import { DropDown } from 'dfps-web-lib';

export interface CaretakerRes {
    caretaker: CaretakerDetailRes;
    resource: Resource
    maritalStatus: DropDown[];
    sex: DropDown[];
    race: DropDown[];
    ethnicity: DropDown[];
    pageMode: string;
}

export interface CaretakerDetailRes {
    id: number;
    resourceId: number;
    number: string;
    firstName: string;
    middleName: string;
    lastName: string;
    sex: string;
    dob: string;
    ethnicity: string;
    race: string;
    personClassNumber: number;
    suffix: string;
    lastUpdatedDate: string;
    homeMaritalStatus: string;
}