import { DropDown } from 'dfps-web-lib';

export interface DisplayStaffSearchResponse {
  otherIdTypes: DropDown[];
  programs: DropDown[];
  regions: DropDown[];
  counties: DropDown[];
  unitSpecializations: DropDown[];
  externalStaffTypes: DropDown[];
  defaultUserRegion: string;
  defaultUserProgram: string;
}

export interface StaffSearchRequest {
  last: string;
  first: string;
  middle: string;
  personId: string;
  ssn: string;
  otherIdType: string;
  otherId: string;
  status: string;
  externalStaff: string;
  regDiv: string;
  unit: string;
  unitSpecialization: string;
  program: string;
  officeCity: string;
  county: string;
  mailCode: string;
  externalStaffType: string;
  organizationName: string;
  organizationEin: string;
}

export interface StaffSearchResponse {
  status: string;
  name: string;
  firstName: string;
  middleName: string;
  lastName: string;
  businessOfficeAddress: string;
  businessFaxNumber: string;
  personId: string;
  regDiv: string;
  unit: string;
  dob: string;
  eu: string;
  office: string;
  workPhone: string;
  ext: string;
  jobClass: string;
  supervisorName: string;
  supervisorId: string;
  mailCode: string;
  matchName: string;
  score: string;
  email: string;
}
