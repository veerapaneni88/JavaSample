import { DropDown } from 'dfps-web-lib';
import { Resource } from '@case/model/case';

export interface ResourceServiceRes {
  resourceId: number;
  resourceServiceId: number
  resourceName: string;
  resourceType: string;
  resourceService: ResourceSvc[];
  pageMode: string;
  resource: Resource;

}
export interface ResourceCharacterRes {
  resourceCharacter: ResourceCharacter[];

}

export interface MedicalConsenterRes {
  resource: Resource;
  medicalConsenter: MedicalConsenter;
}


export interface MedicalConsenter {
  resourceId: number;
  resourceName: string;
  resourceType: string;
  resourceCacthment: string;
  personId: number;
  name: string;
  comments: string;
  lastUpdatedDate: Date;
  lastUpdatedBy: string;
  mcremoved: boolean;
  pageMode: string;
  region: string;
  ssccPlacmentLinkId: number;
  memberResourceId: number;
}

export interface ResourceSvcByAreaDetailRes {
  resourceId: number;
  resourceName: string;
  resourceType: string;
  pageMode: string;
  resourceService: ResourceSvc;
  categories?: DropDown[];
  services?: DropDown[];
  states?: DropDown[];
  programs?: DropDown[];
  regions?: DropDown[];
  counties?: DropDown[];
  regionCounties: DropDown[];
  categoryServices: DropDown[];
}

export interface ResourceCharacterDetailRes {
  resourceId: number;
  resourceName: string;
  resourceType: string;
  pageMode: string;
  resourceCharacter: ResourceCharacter;
  month?: DropDown[];
  year?: DropDown[];
  character?: DropDown[];
}

export interface ResourceSvc {
  id: number;
  lastUpdatedDate: string;
  resourceId: number;
  showRow: string;
  incomeBased: string;
  category: string;
  county: string;
  program: string;
  region: string;
  service: string;
  state: string;
  countyPartial: string;
  kinshipTraining: string;
  kinshipHomeAssessment: string;
  kinshipIncome: string;
  kinshipAgreement: string;
  categoryName: string;
  serviceName: string;
  programName: string;
  regionName: string;
  countyName: string;
  stateName: string;
  contracted: boolean;
  allCounties: string;
}

export interface ResourceCharacter {
  id: number
  lastUpdatedDate: string;
  character: string;
  categoryResource: string;
  county: string;
  program: string;
  region: string;
  service: string;
  state: string;
  dateAdded: string;
  minMaleAge: number;
  maxMaleAge: number;
  minFemaleAge: number;
  maxFemaleAge: number;
  sex: string;
  purgedDate: string;
  // added for codedecode
  characterName: string;
  maleMinAge: string;
  maleMaxAge: string;
  femaleMinAge: string;
  femaleMaxAge: string;
  // request details from characterstic detail page
  resourceId: number;
  serviceId: number;
  maleMaxYear: number
  maleMinYear: number
  maleMaxMonth: number;
  maleMinMonth: number;
  femaleMaxYear: number;
  femaleMinYear: number;
  femaleMaxMonth: number;
  femaleMinMonth: number;
}

export interface County {
  decode: string;
  code: string;
  regionCode: string;
}

export interface FacilityLoc{
  flocId: number;
  level: string;
  active: boolean;
  hold: boolean;
  na: boolean;
  status:string;
}

export interface FacilityServiceLevelRes {
  flocId: number;
  resourceId: number;
  resourceName: string;
  resourceType: string;
  effectiveDate: string;
  endDate: string;
  facilityLocs: FacilityLoc[];
  pageMode: string;
}

export interface ServicePackageHistoryRes {
  flocId: number;
  resourceId: number;
  resourceName: string;
  resourceType: string;
  effectiveDate: string;
  endDate: string;
  pageMode: string;
  servicePackages: Map<string,DropDown[]>;
  servicePackageStatuses: DropDown;
  serviceActivePackageStatuses: DropDown;
  facilityServicePackageDetails: ServicePackageCredential;
  facilityType: string;
  facilityTypeValid: boolean;
  previousAgencyEffectiveDate: string;
}

export interface ServicePackageCredential {
  id?: number;
  resourceId?: number;
  effectiveDate?: string;
  endDate?: string;
  facilityGroup: string;
  childPlacingAgency: boolean;
  servicePackageCredentialDetails: ServicePackageCredentialDetails[];
  facilityType: string;
}

export interface ServicePackageCredentialDetails {
  id?: number;
  servicePackageId?: number;
  servicePackage?: string;
  status?: string;
}
