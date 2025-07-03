import { DropDown } from 'dfps-web-lib';

export interface DisplayResourceSearchResponse {
  numberTypes: DropDown[];
  resourceTypes: DropDown[];
  investigationJurisdictions: DropDown[];
  contractStatuses: DropDown[];
  facilityTypes: DropDown[];
  facilityTypesOther: DropDown[];
  serviceTypes: DropDown[];
  serviceLevels: DropDown[];
  states: DropDown[];
  counties: DropDown[];
  services: ChildDropDown[];
  regions: DropDown[];
  categories: DropDown[];
  categoriesHotline: DropDown[];
  categoriesLawEnforcement: DropDown[];
  categoriesMhmr: DropDown[];
  categoriesOther: DropDown[];
  categoriesProvider: DropDown[];
  categoriesSscc: DropDown[];
  categoriesSchool: DropDown[];
  genders: DropDown[];
  languages: DropDown[];
  characteristics: DropDown[];
}

export interface ResourceSearchResponse {
  name: string;
  resourceId: string;
  status: string;
  contractedCare: string;
  type: string;
  investigJurisdiction: string;
  facilityType: string;
  addressStreetLine1: string;
  city: string;
  county: string;
  phone: string;
  phoneExt: string;
  vendorId?: string;
  isHomeAlreadyLinked?: boolean;
}

export class ChildDropDown extends DropDown {
  parentCode: string;
   constructor (decode: string, code: string, parentCode: string) {
     super(decode, code);
   }
  
}
