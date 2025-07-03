import { DropDown } from 'dfps-web-lib';

export interface NeiceTransmittalList {
    dateFrom: string;
    dateTo: string;
    transmittalType: string;
    transmittalStatus: string;
    neiceHomeStudyId: string;
    priority: string;
    homeStudyType: string;
    incomingOutgoing: string;
    transmittingType: string;
    oldestChildName: string;
    assignedTo: string;
    rejectHold: string;
    jurisdictionState: string;
}

export interface NeiceSearchDisplay {
    transmittalTypes: DropDown[];
    transmittalStatusList: DropDown[];
    priorities: DropDown[];
    homeStudyTypes: DropDown[];
    incomingOutgoingList: DropDown[];
    transmittingStates: DropDown[];
    assignedToList: DropDown[];
    rejectHoldList: DropDown[];
    jurisdictionStates: DropDown[];
}

export interface NeiceTransmittalResponse {
    neiceTransmittalResults: NeiceTransmittalResult[];
    totalPages: string;
    totalElements: number;
    pageSize: string;
    sortColumns: any[];
}

export interface NeiceTransmittalResult {
    transmitState: string;
    jurisdiction: string;
    dateReceived: string;
    idNeiceTransmittal: number;
    incomingOutgoing: string;
    transmittalType: string;
    otherTransmittals: string;
    priority: string;
    oldestChildName: string;
    assignedTo: number;
    assignedToFullName: string;
    transmittalStatus: string;
    transmittalError: string;
    rejectHold: string;
    rejectHoldDisplay: string;
    typeOfCare: string;
    homeStudyType: string;
    neiceHomeStudyId: string;
    purpose: string;
    comments: string;
    shortComments: string;
}
