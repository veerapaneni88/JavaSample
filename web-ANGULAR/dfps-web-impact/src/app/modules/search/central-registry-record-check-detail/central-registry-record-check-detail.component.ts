import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import { DataTable, DfpsConfirmComponent, DfpsFormValidationDirective, DirtyCheck, FormService, FormUtils, FormValue, NavigationService } from 'dfps-web-lib';
import { CrpDueProcessRequest, CrpHistoryDecisonRequest, CrpRecordCheckDetail, CrpRecordCommentsRequest } from '@shared/model/CrpRecordCheckDetail';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { HelpService } from '../../../common/impact-help.service';
import { saveAs } from 'file-saver';
import { CentralRegistryRecordCheckValidators } from './central-registry-record-check-detail.validator';

@Component({
    selector: 'central-registry-record-check-detail',
    templateUrl: './central-registry-record-check-detail.component.html'
})
export class CentralRegistryRecordCheckDetailComponent extends DfpsFormValidationDirective implements OnInit {

    centralRegistryRecordCheckDetailForm: FormGroup;
    isDisplayComment = false;
    displayRecordCheckDetail: CrpRecordCheckDetail;
    crpRecordCommentsRequest: CrpRecordCommentsRequest;
    primReqId: any;

    tableBody: any[];
    tableColumn: any[];
    commentsListTable: DataTable;
    crcHistoryDecisionTable: DataTable;
    potentialMatchesDataTable: DataTable;
    matchInformationDataTable: DataTable;
    notificationsDataTable: DataTable;
    documentsDataTable: DataTable;

    isDisableCrcHistorySave = false;
    isDisablePotentialButtons = false;
    isDisableRefresh = false;
    isDisableResendEmail = false;
    isDisplaySendClearedEmail = false;
    dateCleared: any;
    selectedNotificationEvent: any = [];
    relaunchNotificationEvent: any = [];
    selectedDocumentEvent: any = [];
    formValues: FormValue[];
    formsSubscription: Subscription;
    isDisplayTypeOfAbuse = false;
    readonly DEFAULT_PAGE_SIZE = 5;

    constructor(private navigationService: NavigationService,
        private formBuilder: FormBuilder,
        private searchService: SearchService,
        private modalService: BsModalService,
        private formService: FormService,
        private helpService: HelpService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.navigationService.setUserDataValue('idCrpRequest', JSON.parse(localStorage.getItem('primReqId')));
    }

    createForm() {
        this.centralRegistryRecordCheckDetailForm = this.formBuilder.group(
            {
                comments: [''],
                determination: [''],
                certifiedMailTracking: ['', Validators.maxLength(25)],
                soahMailed: [''],
                dateResponseReceived: [''],
                soahHearingStatus: [''],
                soahDecisionDate: [''],
                typeOfAbuse: ['']
            },
            {
                validators: CentralRegistryRecordCheckValidators.dueProcessTrackingValidation
            });
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Central Registry Record Check Detail');
        this.helpService.loadHelp('Search');
        this.createForm();
        this.intializeScreen();
        this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
            this.launchForm(data);
        });
    }

    ngOnDestroy(): void {
        this.formsSubscription?.unsubscribe();
    }

    intializeScreen() {
        this.primReqId = JSON.parse(localStorage.getItem('primReqId'));
        this.searchService.getCrpRecordCheckDetail(this.primReqId).subscribe((response) => {
            this.displayRecordCheckDetail = response;
            this.getCommentsDetails();
            this.getCRCHistoryDecisionDetails();
            this.getPotentialMatchesDetails();
            this.getMatchInformationDetails();
            this.getDocumentDetails();
            this.getNotificationDetails();
            this.loadCrpRecordCheckDetail();
            this.formValues = this.displayRecordCheckDetail?.centralRegBasicInfoDto?.indSingleUser === 'N' ? this.getActionRequiredForm().concat(this.getForms()) : this.getForms();
        });
    }

    loadCrpRecordCheckDetail() {
        if (this.displayRecordCheckDetail) {
            this.centralRegistryRecordCheckDetailForm.patchValue({
                determination: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.cdRequestStatus,
                certifiedMailTracking: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idMailTracking,
                soahMailed: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.dtSoahEmailed,
                dateResponseReceived: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.dtResponseReceived,
                soahHearingStatus: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.cdSoahStatus,
                soahDecisionDate: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.dtSoahDecision,
            });
        }
        if (['CLCM'].includes(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.cdRequestStatus)) {
            this.centralRegistryRecordCheckDetailForm.disable();
            this.isDisableCrcHistorySave = true;
            this.isDisableCrcHistorySave = true;
            this.isDisablePotentialButtons = true;
            this.isDisableRefresh = true;
            this.isDisableResendEmail = true;
            this.isDisplaySendClearedEmail = true;
            this.potentialMatchesDataTable.tableColumn[14].handleClick = false;
            this.potentialMatchesDataTable.tableColumn[15].handleClick = false;
            FormUtils.enableFormControlStatus(this.centralRegistryRecordCheckDetailForm, ['comments']);
        } else if (['MTCM', 'SHCM'].includes(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.cdRequestStatus)) {
            FormUtils.disableFormControlStatus(this.centralRegistryRecordCheckDetailForm, ['determination']);
            this.isDisableCrcHistorySave = true;
        } else {
            this.centralRegistryRecordCheckDetailForm.enable();
            this.isDisableCrcHistorySave = false;
            this.isDisableCrcHistorySave = false;
            this.isDisableRefresh = false;
            this.isDisableResendEmail = false;
            this.isDisplaySendClearedEmail = false;
        }

        if (this.displayRecordCheckDetail.centralRegDueProcessInfoDto.indAutoClear === 'Y' ||
            this.displayRecordCheckDetail.centralRegDueProcessInfoDto.indClearedEmail === 'Y') {
            this.isDisplaySendClearedEmail = false;
        }

        if (['NRWD', 'SHDR', 'RRWD'].includes(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.cdSoahStatus)) {
            FormUtils.disableFormControlStatus(this.centralRegistryRecordCheckDetailForm,
                ['certifiedMailTracking', 'soahMailed', 'dateResponseReceived', 'soahHearingStatus', 'soahDecisionDate']);
        } else {
            FormUtils.enableFormControlStatus(this.centralRegistryRecordCheckDetailForm,
                ['certifiedMailTracking', 'soahMailed', 'dateResponseReceived', 'soahHearingStatus', 'soahDecisionDate']);
        }
    }

    getCommentsDetails() {
        this.tableColumn = [
            { field: 'dtEntered', header: 'Date/Time', isHidden: false, isLink: false, width: 50 },
            { field: 'txtComment', header: 'Comment', isHidden: false, isLink: false, width: 50 },
            { field: 'usersFullName', header: 'Entered By', isHidden: false, isLink: false, width: 50 },
        ];
        this.commentsListTable = {
            tableColumn: this.tableColumn,
            tableBody: this.displayRecordCheckDetail?.centralRegCommentsInfoDtoList,
            isPaginator: false,
        };
    }

    getCRCHistoryDecisionDetails() {
        this.tableColumn = [
            { field: 'dtDecision', header: 'Date and Time', isHidden: false, isLink: false, width: 50 },
            { field: 'determination', header: 'Determination', isHidden: false, isLink: false, width: 50 },
            { field: 'usersFullName', header: 'Entered By', isHidden: false, isLink: false, width: 50 },
        ];
        this.crcHistoryDecisionTable = {
            tableColumn: this.tableColumn,
            tableBody: this.displayRecordCheckDetail?.centralRegHistDecisionInfoDtoList,
            isPaginator: false,
        };
        if (this.crcHistoryDecisionTable.tableBody && this.crcHistoryDecisionTable.tableBody.length) {
            this.crcHistoryDecisionTable.tableBody.forEach(crcData => {
                this.displayRecordCheckDetail.determinationTypeList.forEach(determination => {
                    if (crcData.determination === determination.code) {
                        crcData.determination = determination.decode;
                    }
                })
            })
        }
    }

    addComment() {
        this.isDisplayComment = true;
    }

    getPotentialMatchesDetails() {
        this.tableColumn = [
            { field: 'pid', header: 'PID', isHidden: false, width: 100 },
            { field: 'caseId', header: 'Case ID', isHidden: false, width: 100 },
            { field: 'stageId', header: 'Stage ID', isHidden: false, width: 100 },
            { field: 'program', header: 'Program', isHidden: false, width: 100 },
            { field: 'firstName', header: 'First Name', isHidden: false, width: 150 },
            { field: 'middleName', header: 'Middle Name', isHidden: false, width: 150 },
            { field: 'lastName', header: 'Last Name', isHidden: false, width: 150 },
            { field: 'dob', header: 'DOB', isHidden: false, width: 100 },
            { field: 'ssn', header: 'SSN', isHidden: false, width: 150 },
            { field: 'approx', header: 'Approx', isHidden: false, width: 100 },
            { field: 'gender', header: 'Gender', isHidden: false, width: 100 },
            {
                field: 'typeOfAbuse',
                header: 'Type of Abuse',
                handleClick: true,
                url: '#',
                urlParams: [''],
                isHidden: false, width: 200
            },
            { field: 'invClosureDate', header: 'Investigation Closure Date', isHidden: false, width: 200 },
            { field: 'role', header: 'Role', isHidden: false, width: 100 },
            { field: 'accept', header: 'Accept', width: 100, handleClick: true, url: ':idPublicCentralRegistry', urlParams: ['idPublicCentralRegistry'], isHidden: false },
            { field: 'reject', header: 'Reject', width: 100, handleClick: true, url: ':idPublicCentralRegistry', urlParams: ['idPublicCentralRegistry'], isHidden: false },
            { field: 'determination', header: 'Determination', isHidden: false, width: 150 },
            { field: 'indMatchClear', isHidden: true, width: 1 },
            { field: 'cdAllegType', isHidden: true, width: 1 }
        ];
        this.potentialMatchesDataTable = {
            tableColumn: this.tableColumn,
            tableBody: this.displayRecordCheckDetail.centralRegPotentialMatchInfoDtoList,
            isPaginator: true
        };
        if (this.potentialMatchesDataTable.tableBody && this.potentialMatchesDataTable.tableBody.length) {
            this.potentialMatchesDataTable.tableBody.forEach(data => {
                data.accept = 'Accept';
                data.reject = 'Reject';

                if (data.indMatchClear === 'A') {
                    data.determination = 'Accepted';
                } else if (data.indMatchClear === 'R') {
                    data.determination = 'Rejected';
                } else {
                    data.determination = '';
                }

                if (data.indSavePost === 'Y') {
                    this.potentialMatchesDataTable.tableColumn[14].handleClick = false;
                    this.potentialMatchesDataTable.tableColumn[15].handleClick = false;
                    this.isDisablePotentialButtons = true;
                } else {
                    this.potentialMatchesDataTable.tableColumn[14].handleClick = true;
                    this.potentialMatchesDataTable.tableColumn[15].handleClick = true;
                    this.isDisablePotentialButtons = false;
                }

                if (data.typeOfAbuse === null) {
                    data.typeOfAbuse = 'Choose';
                }
            })
        }
    }

    handleRouting(event) {
        if (this.potentialMatchesDataTable.tableBody && this.potentialMatchesDataTable.tableBody.length) {
            this.potentialMatchesDataTable.tableBody.forEach(indMatch => {
                if (event.tableColumn.field === 'accept' && (Number(event.link) === indMatch.idPublicCentralRegistry)) {
                    indMatch.determination = 'Accepted';
                    indMatch.indMatchClear = 'A';
                } else if (event.tableColumn.field === 'reject' && (Number(event.link) === indMatch.idPublicCentralRegistry)) {
                    indMatch.determination = 'Rejected';
                    indMatch.indMatchClear = 'R';
                }
            })
        }

        if (event.tableColumn.field !== 'accept' && event.tableColumn.field !== 'reject') {
            if (event.tableRow.typeOfAbuse === 'Choose') {
                const initialState = {
                    title: 'Type of Abuse',
                    message: 'Please select Type of Abuse from the dropdown.',
                    showCancel: false,
                };
                const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                    if (result) {
                        this.isDisplayTypeOfAbuse = true;
                    }
                });
                localStorage.setItem('selectedRow', JSON.stringify(event.tableRow));
            } else {
                this.isDisplayTypeOfAbuse = false;
                const initialState = {
                    title: 'Type of Abuse',
                    message: 'Type of Abuse can not be modified.',
                    showCancel: false,
                };
                const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                });
            }
        }
        else {
            this.centralRegistryRecordCheckDetailForm.patchValue({ typeOfAbuse: '' });
            this.isDisplayTypeOfAbuse = false;
        }
    }

    addTypeOfAbuse() {
        const selectedRow = JSON.parse(localStorage.getItem('selectedRow'));
        const typeOfAbuse = this.centralRegistryRecordCheckDetailForm.get('typeOfAbuse').value;
        if (this.potentialMatchesDataTable.tableBody && this.potentialMatchesDataTable.tableBody.length) {
            this.potentialMatchesDataTable.tableBody.forEach(data => {
                if ((selectedRow.pid === data.pid) && (data.typeOfAbuse === 'Choose') && (selectedRow.caseId === data.caseId) && (selectedRow.stageId === data.stageId)) {
                    data.cdAllegType = typeOfAbuse;
                    this.displayRecordCheckDetail.allegationTypeList.forEach(allegation => {
                        if (allegation.code === typeOfAbuse) {
                            data.typeOfAbuse = allegation.decode;
                        }
                    });
                }
            })
        }
        this.centralRegistryRecordCheckDetailForm.patchValue({ typeOfAbuse: '' });
        this.isDisplayTypeOfAbuse = false;
    }

    rejectAllPotentialMatches() {
        if (this.potentialMatchesDataTable.tableBody && this.potentialMatchesDataTable.tableBody.length) {
            this.potentialMatchesDataTable.tableBody.forEach(data => {
                data.indMatchClear = 'R';
                data.determination = 'Rejected';
            })
        }
    }

    getMatchInformationDetails() {
        this.tableColumn = [
            { field: 'pid', header: 'PID', isHidden: false, isLink: false, width: 100 },
            { field: 'caseId', header: 'Case ID', isHidden: false, isLink: false, width: 100 },
            { field: 'firstName', header: 'First Name', isHidden: false, isLink: false, width: 150 },
            { field: 'middleName', header: 'Middle Name', isHidden: false, isLink: false, width: 150 },
            { field: 'lastName', header: 'Last Name', isHidden: false, isLink: false, width: 150 },
            { field: 'dob', header: 'DOB', isHidden: false, isLink: false, width: 100 },
            { field: 'ssn', header: 'SSN', isHidden: false, isLink: false, width: 150 },
            { field: 'approx', header: 'Approx', isHidden: false, isLink: false, width: 100 },
            { field: 'gender', header: 'Gender', isHidden: false, isLink: false, width: 100 },
            { field: 'typeOfAbuse', header: 'Type of Abuse', isHidden: false, isLink: false, width: 200 },
            { field: 'invClosureDate', header: 'Investigation Closure Date', isHidden: false, isLink: false, width: 200 },
            { field: 'role', header: 'Role', isHidden: false, isLink: false, width: 100 },
            { field: 'indMatchClear', isHidden: true, width: 1 },
            { field: 'indSavePost', isHidden: true, width: 1 },
            { field: 'idRequest', isHidden: true, width: 1 },
            { field: 'idPublicCentralRegistry', isHidden: true, width: 1 }

        ];
        this.matchInformationDataTable = {
            tableColumn: this.tableColumn,
            tableBody: this.displayRecordCheckDetail?.centralRegMatchInfoDtoList,
            isPaginator: false,
        };
    }

    getDocumentDetails() {
        this.tableColumn = [
            { field: 'documentName', header: 'Document Name', isHidden: false, isLink: false, width: 70 },
            { field: 'idCentralRegDocument', isHidden: true, width: 0 },
            { field: 'idDocRepository', isHidden: true, width: 0 }
        ];
        this.documentsDataTable = {
            tableColumn: this.tableColumn,
            tableBody: this.displayRecordCheckDetail?.centralRegDocumentInfoDtoList,
            isPaginator: false,
            isSingleSelect: true
        };
    }

    selectedDocument(event) {
        this.selectedDocumentEvent = event;
    }

    getNotificationDetails() {
        this.tableColumn = [
            { field: 'dtNotificationSent', header: 'Status Date', isHidden: false, isLink: false, width: 50 },
            { field: 'cdNotificationStat', header: 'Status', isHidden: false, isLink: false, width: 30 },
            { field: 'notificationType', header: 'Type', isHidden: false, width: 50, handleClick: true, url: ':idCrpRecordNotif', urlParams: ['idCrpRecordNotif'] },
            { field: 'txtRecipientEmail', header: 'Sent To', isHidden: false, isLink: false, width: 70 },
            { field: 'txtSenderEmail', header: 'Sent From', isHidden: false, isLink: false, width: 70 },
            { field: 'senderFullName', header: 'Updated By', isHidden: false, isLink: false, width: 50 },
            { field: 'idSenderPerson', header: 'Staff ID', isHidden: false, isLink: false, width: 50 },
            { field: 'idCrpRecordNotif', header: 'Notification ID', isHidden: false, isLink: false, width: 50 },
            { field: 'cdNotificationType', isHidden: true, width: 1 },
        ];
        this.notificationsDataTable = {
            tableColumn: this.tableColumn,
            tableBody: this.displayRecordCheckDetail?.centralRegNotificationInfoDtoList,
            isPaginator: false,
            isSingleSelect: true
        };
    }

    selectedNotification(event) {
        this.selectedNotificationEvent = event;
    }

    handleFormRelaunch(event) {
        if (this.notificationsDataTable.tableBody && this.notificationsDataTable.tableBody.length) {
            this.relaunchNotificationEvent = event.tableRow;
            this.relaunchEmail();
        }
    }

    saveCRCHistory() {
        const determination = this.centralRegistryRecordCheckDetailForm.get('determination').value;

        if (determination === null || determination === undefined) {
            const initialState = {
                title: 'Save Determination',
                message: 'Select appropriate value for Determination',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
                document.getElementById('determination').focus();
            });
            return;
        }
        else {
            if (determination === 'CLCM') {
                this.centralRegistryRecordCheckDetailForm.disable();
                this.isDisableCrcHistorySave = true;
                this.isDisableCrcHistorySave = true;
                this.isDisablePotentialButtons = true;
                this.isDisableRefresh = true;
                this.isDisableResendEmail = true;
                this.isDisplaySendClearedEmail = true;
                this.potentialMatchesDataTable.tableColumn[14].handleClick = false;
                this.potentialMatchesDataTable.tableColumn[15].handleClick = false;
                FormUtils.enableFormControlStatus(this.centralRegistryRecordCheckDetailForm, ['comments']);
            } else if (['MTCM', 'SHCM'].includes(determination)) {
                FormUtils.disableFormControlStatus(this.centralRegistryRecordCheckDetailForm, ['determination']);
                this.isDisableCrcHistorySave = true;
            } else {
                this.centralRegistryRecordCheckDetailForm.enable();
                this.isDisableCrcHistorySave = false;
                this.isDisableCrcHistorySave = false;
                this.isDisableRefresh = false;
                this.isDisableResendEmail = false;
                this.isDisplaySendClearedEmail = false;
                FormUtils.enableFormControlStatus(this.centralRegistryRecordCheckDetailForm, ['determination']);
            }
            const payload: CrpHistoryDecisonRequest = {
                cdRequestStatus: this.centralRegistryRecordCheckDetailForm.get('determination').value,
                dtSoahDecision: this.dateCleared ? this.dateCleared : null,
                idRequest: this.displayRecordCheckDetail.centralRegBasicInfoDto.reqId
            }
            this.searchService.saveHistoryDecision(payload).subscribe((response) => {
                if (response) {
                    this.refresh();
                }
            });
        }
    }

    soahHearingStatusChange() {
        const soahHearingStatus = this.centralRegistryRecordCheckDetailForm.get('soahHearingStatus').value;
        if (['NRWD', 'SHDR', 'RRWD'].includes(soahHearingStatus)) {
            FormUtils.disableFormControlStatus(this.centralRegistryRecordCheckDetailForm,
                ['certifiedMailTracking', 'soahMailed', 'dateResponseReceived', 'soahHearingStatus', 'soahDecisionDate']);
        } else {
            FormUtils.enableFormControlStatus(this.centralRegistryRecordCheckDetailForm,
                ['certifiedMailTracking', 'soahMailed', 'dateResponseReceived', 'soahHearingStatus', 'soahDecisionDate']);
        }
    }

    saveDueProcess() {

        if (this.validateFormGroup(this.centralRegistryRecordCheckDetailForm)) {
            this.soahHearingStatusChange();
            const payload: CrpDueProcessRequest = {
                idMailTracking: this.centralRegistryRecordCheckDetailForm.get('certifiedMailTracking').value,
                cdRequestStatus: this.centralRegistryRecordCheckDetailForm.get('determination').value,
                idRequest: this.displayRecordCheckDetail.centralRegBasicInfoDto.reqId,
                cdSoahStatus: this.centralRegistryRecordCheckDetailForm.get('soahHearingStatus').value,
                dtResponseReceived: this.centralRegistryRecordCheckDetailForm.get('dateResponseReceived').value,
                dtSoahEmailed: this.centralRegistryRecordCheckDetailForm.get('soahMailed').value,
                dtSoahDecision: this.centralRegistryRecordCheckDetailForm.get('soahDecisionDate').value,
            }
            this.searchService.saveDueProcess(payload).subscribe((response) => {
            });

        }
    }

    savePotentialMatches() {
        const indSave = 'N';
        const payload = this.getPayload(indSave);
        this.searchService.savePotentialMatches(payload).subscribe((response) => { });
    }

    saveAndPostPotentialMatches() {
        const indSave = 'Y';
        const payload = this.getPayload(indSave);
        this.searchService.savePotentialMatches(payload).subscribe((response) => {
            if (response) {
                window.location.reload();
            }
        });
    }

    getPayload(indSave) {
        const requestId = this.displayRecordCheckDetail.centralRegBasicInfoDto.reqId;
        const centralRegPotentialMatchInfoDtoList: any = this.displayRecordCheckDetail.centralRegPotentialMatchInfoDtoList;
        return {
            requestId: requestId,
            centralRegPotentialMatchInfoDtoList: centralRegPotentialMatchInfoDtoList,
            savePost: indSave
        }
    }

    saveRecordComments() {
        const payload: CrpRecordCommentsRequest = {
            idRequest: this.displayRecordCheckDetail.centralRegBasicInfoDto.reqId,
            idCrpRecordDetail: this.displayRecordCheckDetail.centralRegDueProcessInfoDto?.idCrpRecordDetail,
            txtComment: this.centralRegistryRecordCheckDetailForm.get("comments").value
        }
        this.searchService.saveRecordComments(payload).subscribe((response) => {
            if (response) {
                window.location.reload();
            }
        });
    }

    refresh() {
        window.location.reload();
    }

    relaunchEmail() {
        const data: any = this.reLaunchEmailNotif();
        this.formService.launchForm(data ? JSON.stringify(data) : data);
    }

    reLaunchEmailNotif() {
        return {
            docType: this.relaunchNotificationEvent.cdNotificationType,
            docExists: 'true',
            windowName: 'Central Registry Record Check Detail',

            protectDocument: (this.relaunchNotificationEvent?.cdNotificationStat == 'Sent') || (this.relaunchNotificationEvent?.cdNotificationStat == 'Re-sent' &&
                this.relaunchNotificationEvent?.dtNotificationSent != null) ? 'true' : 'false',
            bResend: 'false',
            pCrpCheck: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail ?
                String(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail) : '',
            pCrpRecordNotif: this.relaunchNotificationEvent?.idCrpRecordNotif ?
                String(this.relaunchNotificationEvent.idCrpRecordNotif) : '',
            sCrpRecordNotif: this.relaunchNotificationEvent?.idCrpRecordNotif ?
                String(this.relaunchNotificationEvent.idCrpRecordNotif) : '',
            idCreatedPerson: this.relaunchNotificationEvent?.idSenderPerson ?
                String(this.relaunchNotificationEvent.idSenderPerson) : '',
            idLastUpdatePerson: this.relaunchNotificationEvent?.idSenderPerson ?
                String(this.relaunchNotificationEvent.idSenderPerson) : '',
            sTimestamp: this.relaunchNotificationEvent?.dtNotificationSent ?
                String(this.relaunchNotificationEvent?.dtNotificationSent) : '',
            userId: this.displayRecordCheckDetail?.centralRegBasicInfoDto?.loggedInUserId ?
                String(this.displayRecordCheckDetail.centralRegBasicInfoDto.loggedInUserId) : ''
        }

    }

    resendEmail() {
        if (this.selectedNotificationEvent.length <= 0) {
            const initialState = {
                title: 'Notification',
                message: 'Please select at least one row to perform this action.',
                showCancel: false
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
        }
        else if (this.selectedNotificationEvent?.cdNotificationStat != 'Sent') {
            const initialState = {
                title: 'Notification',
                message: 'A notification in Sent status must be selected before the Resend Email button is clicked.',
                showCancel: false
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
        }
        else {
            const data: any = this.resendEmailNotif();
            this.formService.launchForm(data ? JSON.stringify(data) : data);
        }
    }

    resendEmailNotif() {
        return {
            docType: this.selectedNotificationEvent.cdNotificationType,
            docExists: 'true',
            windowName: 'Central Registry Record Check Detail',
            protectDocument: 'false',
            bResend: 'true',
            pCrpCheck: this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail ?
                String(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail) : '',
            pCrpRecordNotif: this.selectedNotificationEvent?.idCrpRecordNotif ?
                String(this.selectedNotificationEvent.idCrpRecordNotif) : '',
            sCrpRecordNotif: this.selectedNotificationEvent?.idCrpRecordNotif ?
                String(this.selectedNotificationEvent.idCrpRecordNotif) : '',
            idCreatedPerson: this.selectedNotificationEvent?.idSenderPerson ?
                String(this.selectedNotificationEvent.idSenderPerson) : '',
            idLastUpdatePerson: this.selectedNotificationEvent?.idSenderPerson ?
                String(this.selectedNotificationEvent.idSenderPerson) : '',
            sTimestamp: this.selectedNotificationEvent?.dtNotificationSent ?
                String(this.selectedNotificationEvent?.dtNotificationSent) : '',
            userId: this.displayRecordCheckDetail?.centralRegBasicInfoDto?.loggedInUserId ?
                String(this.displayRecordCheckDetail.centralRegBasicInfoDto.loggedInUserId) : ''
        }

    }


    getForms(): FormValue[] {
        return [
            {
                formName: 'ARIF Notification',
                formParams: {
                    docType: 'ARIFNOT',
                    docExists: 'false',
                    windowName: 'Central Registry Record Check Detail',
                    protectDocument: 'false',
                    pCrpCheck: this.displayRecordCheckDetail?.centralRegDueProcessInfoDto?.idCrpRecordDetail ?
                        String(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail) : '',
                    pCrpRecordNotif: '',
                    userId: this.displayRecordCheckDetail?.centralRegBasicInfoDto?.loggedInUserId ?
                        String(this.displayRecordCheckDetail.centralRegBasicInfoDto.loggedInUserId) : ''
                }
            },
            {
                formName: 'Match Notification',
                formParams: {
                    docType: 'MATCNOT',
                    docExists: 'false',
                    windowName: 'Central Registry Record Check Detail',
                    protectDocument: 'false',
                    pCrpCheck: this.displayRecordCheckDetail?.centralRegDueProcessInfoDto?.idCrpRecordDetail ?
                        String(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail) : '',
                    pCrpRecordNotif: '',
                    userId: this.displayRecordCheckDetail?.centralRegBasicInfoDto?.loggedInUserId ?
                        String(this.displayRecordCheckDetail.centralRegBasicInfoDto.loggedInUserId) : ''
                }
            },
            {
                formName: 'SOAH Offer Notification',
                formParams: {
                    docType: 'SOAHNOT',
                    docExists: 'false',
                    windowName: 'Central Registry Record Check Detail',
                    protectDocument: 'false',
                    pCrpCheck: this.displayRecordCheckDetail?.centralRegDueProcessInfoDto?.idCrpRecordDetail ?
                        String(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail) : '',
                    pCrpRecordNotif: '',
                    userId: this.displayRecordCheckDetail?.centralRegBasicInfoDto?.loggedInUserId ?
                        String(this.displayRecordCheckDetail.centralRegBasicInfoDto.loggedInUserId) : ''
                }
            },
            {
                formName: 'DFPS Letterhead Notification',
                formParams: {
                    docType: 'LTRHNOT',
                    docExists: 'false',
                    windowName: 'Central Registry Record Check Detail',
                    protectDocument: 'false',
                    pCrpCheck: this.displayRecordCheckDetail?.centralRegDueProcessInfoDto?.idCrpRecordDetail ?
                        String(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail) : '',
                    pCrpRecordNotif: '',
                    userId: this.displayRecordCheckDetail?.centralRegBasicInfoDto?.loggedInUserId ?
                        String(this.displayRecordCheckDetail.centralRegBasicInfoDto.loggedInUserId) : ''
                }
            }
        ];
    }

    getActionRequiredForm(): FormValue[] {
        return [{
            formName: 'Action Required Notification',
            formParams: {
                docType: 'ACRQNOT',
                docExists: 'false',
                windowName: 'Central Registry Record Check Detail',
                protectDocument: 'false',
                pCrpCheck: this.displayRecordCheckDetail?.centralRegDueProcessInfoDto?.idCrpRecordDetail ?
                    String(this.displayRecordCheckDetail.centralRegDueProcessInfoDto.idCrpRecordDetail) : '',
                pCrpRecordNotif: '',
                userId: this.displayRecordCheckDetail?.centralRegBasicInfoDto?.loggedInUserId ?
                    String(this.displayRecordCheckDetail.centralRegBasicInfoDto.loggedInUserId) : ''
            }
        }];
    }

    launchForm(data: any) {
        const dataModified = data ? JSON.parse(data) : data;
        if (dataModified && (dataModified.docType === 'MATCNOT' || dataModified.docType === 'ARIFNOT' || dataModified.docType === 'SOAHNOT') && this.matchInformationDataTable?.tableBody?.length === 0) {
            const initialState = {
                title: 'Form Launch',
                message: 'No records found.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
                document.getElementById('formLaunch').focus();
            });
            return;
        }
        this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
    }

    sendClearanceNotif() {
        this.searchService.sendClearanceNotif(this.primReqId).subscribe((response) => {
            window.location.reload();
        });
    }

    viewDocument() {
        if (this.selectedDocumentEvent.length <= 0) {
            const initialState = {
                title: 'Documents',
                message: 'Please select at least one row to perform this action.',
                showCancel: false
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
        }
        else {
            this.searchService.viewCrpDocument(this.selectedDocumentEvent?.idDocRepository)
                .subscribe(blob => saveAs(blob, this.selectedDocumentEvent.documentName));
        }
    }

    deleteDocument() {
        if (this.selectedDocumentEvent.length <= 0) {
            const initialState = {
                title: 'Documents',
                message: 'Please select at least one row to perform this action.',
                showCancel: false
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
        }
        else {

            this.searchService.deleteCrpDocument(this.selectedDocumentEvent?.idDocRepository).subscribe((response) => {
                if (response) {
                    window.location.reload();
                }
            });

        }
    }

}
