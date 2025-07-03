import { DropDown } from "dfps-web-lib";

export interface CrpRecordCheckDetail {
    centralRegBasicInfoDto: CentralRegBasicInfoDto;
    centralRegDueProcessInfoDto: CentralRegDueProcessInfoDto;
    centralRegPotentialMatchInfoDtoList: CentralRegPotentialMatchInfoDtoList[];
    centralRegMatchInfoDtoList: CentralRegMatchInfoDtoList[];
    centralRegNotificationInfoDtoList: CentralRegNotificationInfoDtoList[];
    centralRegCommentsInfoDtoList: CentralRegCommentsInfoDtoList[];
    centralRegHistDecisionInfoDtoList: CentralRegHistDecisionInfoDtoList[];
    centralRegDocumentInfoDtoList: CentralRegDocumentInfoDtoList[];
    allegationTypeList: DropDown[];
    determinationTypeList: DropDown[];
    soahStatusTypeList: any[];
    notificationTypeList: DropDown[];
}

export interface CentralRegBasicInfoDto {
    reqId: number;
    searchType: string;
    requestedBy: string;
    emailId: string;
    dtRequested: string;
    dtCompleted: string;
    loggedInUserId: number;
    indSingleUser: string;
    headerInfo: string;
}

export interface CentralRegDueProcessInfoDto {
    idCrpRecordDetail: number;
    idMailTracking: string;
    cdRequestStatus: string;
    dtCompleted: string;
    idRequest: string;
    cdSoahStatus: string;
    dtResponseReceived: string;
    dtSoahEmailed: string;
    dtSoahDecision: string;
    indAutoClear: string;
    indClearedEmail: string;
}

export interface CentralRegPotentialMatchInfoDtoList {
    idRequest: number;
    idPublicCentralRegistry: number;
    caseId: number;
    stageId: number;
    firstName: string;
    middleName: string;
    lastName: string;
    dob: string;
    ssn: number;
    gender: string;
    role: string;
    typeOfAbuse: string;
    invClosureDate: string;
    approx: string;
    indCleared: string;
    indMatchClear: string;
    pid: string;
    program: string;
    indSavePost: string;
    cdAllegType: string;
}

export interface CentralRegMatchInfoDtoList {
    idRequest: number;
    idPublicCentralRegistry: number;
    caseId: number;
    stageId: number;
    firstName: string;
    middleName: string;
    lastName: string;
    dob: string;
    ssn: number;
    gender: string;
    role: string;
    typeOfAbuse: string;
    invClosureDate: string;
    approx: string;
    indCleared: string;
    indMatchClear: string;
    pid: string;
    indSavePost: string;
}

export interface CentralRegNotificationInfoDtoList {
    idCrpRecordNotif: number;
    cdNotificationType: string;
    notificationType: string;
    cdNotificationStat: string;
    dtNotificationSent: string;
    txtRecipientEmail: string;
    idSenderPerson: number;
    senderFullName: string;
    txtSenderEmail: string;
    indSecureNotification: string;
}

export interface CentralRegCommentsInfoDtoList {
    idCrpRecordComment: number;
    createdDate: string;
    dtEntered: string;
    txtComment: string;
    createdPersonId: number;
    usersFullName: string;
}

export interface CentralRegHistDecisionInfoDtoList {
    idCrpRecordHistDecsn: number;
    createdDate: string;
    dtDecision: string;
    determination: string;
    createdPersonId: number;
    usersFullName: string;
}

export interface CrpRecordCommentsRequest {
    idRequest: number;
    idCrpRecordDetail: number;
    txtComment: string;
}

export interface CrpHistoryDecisonRequest {
    cdRequestStatus: number;
    dtSoahDecision: string;
    idRequest: number;
}

export interface CrpDueProcessRequest {
    idMailTracking: string;
    cdRequestStatus: number,
    idRequest: number;
    cdSoahStatus: string;
    dtResponseReceived: string;
    dtSoahEmailed: string;
    dtSoahDecision: string;

}

export interface CentralRegDocumentInfoDtoList {
    idCentralRegDocument: number;
    idCentralRegistryCheck: number;
    idDocRepository: number;
    textDocDetails: string;
    extDocType: string;
    textDocLocation: string;
    documentName: string;
}

