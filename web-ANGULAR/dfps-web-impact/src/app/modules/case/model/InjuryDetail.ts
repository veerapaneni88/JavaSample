import { Time } from '@angular/common';
import { DropDown } from 'dfps-web-lib';
import { Allegation } from './Allegation';

export interface InjuryDetailDisplayRes {
    caseId?: string;
    stageName?: string;
    injuryDeterminations?: DropDown[];
    injuryTypes?: DropDown[];
    injuryCausedByDiscipline?: DropDown[];
    injuryCausedByWeapons?: DropDown[];
    injuryCausedByIndoorEquipments?: DropDown[];
    injuryCausedByOutdoorEquipments?: DropDown[];
    injuryCausedByRestraint?: DropDown[];
    injuryCausedBySelf?: DropDown[];
    injuryCausedTools?: DropDown[];
    injuryCausedVehicle?: DropDown[];
    injuryCausedWaterActivities?: DropDown[];
    injuryCausedWeather?: DropDown[];
    injuryRelatedToPersons?: DropDown[];
    injuryCausedByPersons?: DropDown[];
    injuryLocations?: DropDown[];
    victims?: DropDown[];
    allegedPerpetrators?: DropDown[];
    injuryDetailReq?: InjuryDetail;
    pageMode?: string;
}

export interface InjuryDetail {
    id?: string;
    abuseNeglect?: string;
    allegedPerpetrator?: string;
    approximateDate?: string;
    approximateTime?: string;
    injuryCausedBy?: string;
    injuryDate?: Date;
    injuryDescription?: string;
    injuryDetermination?: string;
    injuryLocation?: string;
    injuryRelatedTo?: any[];
    injuryTime?: Time;
    injuryType?: any[];
    otherInjuryRelatedTo?: string;
    otherTypeInjury?: string;
    standardViolation?: string;
    victim?: string;
    createdDate?: string;
    lastUpdatedDate?: string;
    createdPerson?: string;
    lastUpdatedPerson?: string;
    canDelete?: boolean;
    canAdd?: boolean;
    allegations?: Allegation[];
}

export interface InjuryListRes {
    injuryDetails: InjuryDetail[];
    editMode: boolean;
    caseId: string;
    stageName: string;
}