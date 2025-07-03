import { DropDown } from 'dfps-web-lib';
import { CodesDto } from './codesDto';

export interface ResourceHistoryRes{
    caseName: string;
    resourceId: string;
    pageMode : string;
    stageId: string;
    stageName : string;
    stageCode : string;
    resourceHistory: ResourceHistory[];
}

export interface ResourceHistoryDetailRes{
    caseName: string;
    resourceId: string;
    categoryList : DropDown[];
    statusList: DropDown[];
    year: DropDown[];
    month: DropDown[];
    typeList: DropDown[];
    closureReasonList: DropDown[];
    recommendReOpeningList: DropDown[];
    involuntaryClosureList: DropDown[];
    homeType: DropDown[];
    resourceHistoryDetail: ResourceHistoryDetail;
    pageMode : string;
}

export interface ResourceHistory {
    effectiveDate: string;
    endDate: string;
    category: string;
    faHomeStatus: string;
    facilityCapacity: string;
    minAgeMaleChild: string;
    maxAgeMaleChild: string;
    minAgeFemaleChild:string;
    maxAgeFemaleChild:string;
    closureReason: string;
    recommendReopen: string;
    involuntaryClosure: string;
    ethnicity: string;
    language: string;
    religion: string;
    annualIncome: string;
    maritalStatus: string;
    marriageDate: string;
    sourceInquiry: string;
    respite: string;
    nonPRSPCA: string;
    nonPRSHome: string;
    certifyEntity: string;
    individualStudy: string;
    careProvided: string;
    homeType: string;
    disableSelectRow: string;
    resourceHistoryId: string;
    
}

export interface ResourceHistoryDetail{
    resourceHistoryId: string;
    effectiveDate: string;
    endDate: string;
    facilityCapacity: string;
    category: DropDown[];
    faHomeStatus: DropDown[];
    minAgeMaleChildYear: string;
    minAgeMaleChildMonth: string;        
    maxAgeMaleChildYear: string;
    maxAgeMaleChildMonth: string;            
    minAgeFemaleChildYear: string;
    minAgeFemaleChildMonth: string;
    maxAgeFemaleChildYear: string;
    maxAgeFemaleChildMonth: string;
    minAgeMaleChild: number;
    maxAgeMaleChild: number;
    minAgeFemaleChild: number;
    maxAgeFemaleChild: number;
    faHomeType1: string;
    faHomeType2: string;
    faHomeType3: string;
    faHomeType4: string;
    faHomeType5: string;
    faHomeType6: string;
    faHomeType7: string;
    faHomeType8: string;
    closureReason: DropDown[];
    recommendReopen: DropDown[];
    involuntaryClosure: DropDown[];
    selectedHomeTypes: DropDown[];
    nonprsPca: string;    
    nextStartDate: string;    
    previousEndDate: string;
    updatedHomeTypes: CodesDto[];
  }