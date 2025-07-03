import { DropDown } from 'dfps-web-lib';


export interface DisplayHomeSearchResponse {
  status: DropDown[];
  region: DropDown[];
  counties: DropDown[];
  category: DropDown[];
  homeType: DropDown[];
  language: DropDown[];
  gender: DropDown[];
  year: DropDown[];
  month: DropDown[];
  childCharacteristics: DropDown[];
  selectedCharacteristics: string;
  regionCounties: DropDown[];
  fadHomeAccess : string;
  kinHomeAccess : string;
  pageMode: string;

}

export interface HomeSearchRequest {
  homeName: string;
  resourceId: string;
  status: string;
  county: string;
  region: string;
  category: boolean;
  city: string;
  type: string;
  language: string;
  childCharacteristics: string[];
  gender: string;
  minYear: number;
  minMonth: number;
  maxYear: number;
  maxMonth: number;
  openSlots: number;
}



export interface HomeSearchResponse {
  resourceName: string;
  category: string;
  status: string;
  ethnicity: string;
  nonFPS: string;
  city: string;
  workerName: string;
  addresworkerPhone: string;
  ext: string;
}

export interface HomeSearchResult {
  resourceName: string;
  category: string;
  status: string;
  ethnicity: string;
  nonFPS: string;
  city: string;
  workerName: string;
  workerPhone: string;
  extension: string;
  resourceId: number;
  stageCode: string;
}

export interface HomeSearchResponse {
  homeSearchResults: HomeSearchResult[];
  totalPages: string;
  totalElements: number;
  pageSize: string;
  sortColumns: any[];

}
export interface County {
  decode: string;
  code: string;
  regionCode: string;
}