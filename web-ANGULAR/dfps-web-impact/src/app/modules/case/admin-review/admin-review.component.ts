import { Component, OnDestroy, OnInit, Inject, HostListener } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import {
  DataTable,
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  FormValue,
  FormParams,
  FormService,
  NavigationService,
  Reports,
  ReportService,
  SET, ENVIRONMENT_SETTINGS, NarrativeService
} from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import { DisplayAdminReviewInformation } from '../model/case';
import { BsModalService } from 'ngx-bootstrap/modal';
import { AdminReviewValidators } from './admin-review.validator';
import { dfpsValidatorErrors } from '../../../../messages/validator-errors';
import { HelpService } from 'app/common/impact-help.service';
import { CaseService } from '../service/case.service';
import { ARIA_LIVE_DELAY } from '@ng-bootstrap/ng-bootstrap/util/accessibility/live';
import { Subscription } from 'rxjs';

@Component({
  selector: 'admin-review',
  templateUrl: './admin-review.component.html',
  styleUrls: ['./admin-review.component.scss']
})
export class AdminReviewComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  adminReviewForm: FormGroup;
  attentionMessages: string[];
  authority = [];
  otherAuthority: string;
  event: any;
  eventStatus: string;
  hidesavesection: boolean = false;
  requestedBy = [];
  otherRequestedBy: string;
  result = [];
  delayedReason = [];
  otherDelayedReason: string;
  status = [];
  type = [];
  isAlert = false;
  adminReviewDetails: any;
  errorNoDataMessage;
  hideAttentionMessage = false;
  reports: any;
  formValues: FormValue[];
  narrativeSubscription: Subscription;
  formsSubscription: Subscription;
  formsArray: any[];
  selectedFormValue: any;
  showPersonList: boolean = false;
  personTableData: DataTable;
  tableColumn: any[];
  personId: string;

  constructor(
    private fb: FormBuilder,
    private modalService: BsModalService,
    private caseService: CaseService,
    private formService: FormService,
    private helpService: HelpService,
    private navigationService: NavigationService,
    private route: ActivatedRoute,
    private narrativeService: NarrativeService,
    private router: Router,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {

    super(store);
  }

  displayAdminReviewInformation: DisplayAdminReviewInformation;

  ngOnInit(): void {
    this.helpService.loadHelp('Case');
    this.navigationService.setTitle('Administrative Review and Appeal');
    this.intializeScreen();
    this.createForm();
  }

  intializeScreen() {
    this.caseService
      .getAdminReviewData()
      .subscribe((res) => {
        this.displayAdminReviewInformation = res;
        this.formValues = this.getForms();
        this.setFormValues();
        this.disablePageMode();
        this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
          this.generateNarrative(data);
        });
        this.loadPersonDataTable();
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        if (this.displayAdminReviewInformation?.adminReviewDto?.eventId <= 0 || this.displayAdminReviewInformation?.adminReviewDto?.eventId == null || this.displayAdminReviewInformation?.adminReviewDto?.eventId == undefined) {
          this.errorNoDataMessage = dfpsValidatorErrors.MSG_INT_NO_DATA_PASSED_IN;
        }
        this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
          this.launchForm(data);
        });
      }

      );
  }

  disablePageMode() {
    if (this.displayAdminReviewInformation.pageMode === 'VIEW') {
      this.disableAllFields();
      this.hidesavesection = true;
    }
    if (this.displayAdminReviewInformation.stageCode === 'ARF') {
      this.disableARFAllFields();
    }
  }

  generateNarrative(narrativeData: any) {
    if (this.displayAdminReviewInformation.pageMode === 'NEW') {
      const initialState = {
        title: 'Admin Review',
        message: 'Please save page before producing document.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    } else if (narrativeData) {
      this.narrativeService.callNarrativeService(narrativeData);
    }
  }

  createForm() {
    this.adminReviewForm = this.fb.group(
      {
        appealRequestDate: ['', [DfpsCommonValidators.validateDate]],
        authority: ['', [Validators.required]],
        otherAuthority: [''],
        caseId: [''],
        changeRoleToSp: [''],
        narrative: [''],
        dueDate: ['', [DfpsCommonValidators.validateDate]],
        emergencyRelease: [''],
        emergencyReleaseDate: ['', [DfpsCommonValidators.validateDate]],
        eventId: [''],
        hearingDate: ['', [DfpsCommonValidators.validateDate]],
        lastUpdateDate: [''],
        notificationDate: ['', [DfpsCommonValidators.validateDate]],
        personId: [''],
        personReviewed: [''],
        relatedStageId: [''],
        requestedBy: ['', Validators.required],
        otherRequestedBy: [''],
        requestedByName: ['', Validators.required],
        result: [''],
        delayedReason: [''],
        otherDelayedReason: [''],
        reviewDate: ['', [DfpsCommonValidators.validateDate]],
        stageId: [''],
        status: ['', [Validators.required]],
        type: ['', [Validators.required]],
        saveAndSubmitStatusMsg: [''],
        buttonClicked: [''],
        narrativeButton: [false],
        stageCode: [''],
        program: ['']
      }, {
      validators: [
        AdminReviewValidators.validateAdminReview
      ]
    });
  }

  setFormValues() {
    if (this.displayAdminReviewInformation) {
      this.adminReviewForm.patchValue({
        appealRequestDate: this.displayAdminReviewInformation.adminReviewDto.appealRequestDate ? this.displayAdminReviewInformation.adminReviewDto.appealRequestDate : '',
        authority: this.displayAdminReviewInformation.adminReviewDto.authority ? this.displayAdminReviewInformation.adminReviewDto.authority : '',
        otherAuthority: this.displayAdminReviewInformation.adminReviewDto.otherAuthority ? this.displayAdminReviewInformation.adminReviewDto.otherAuthority : '',
        caseId: this.displayAdminReviewInformation.adminReviewDto.caseId ? this.displayAdminReviewInformation.adminReviewDto.caseId : '',
        changeRoleToSp: this.displayAdminReviewInformation.adminReviewDto.changeRoleToSp,
        dueDate: this.displayAdminReviewInformation.adminReviewDto.dueDate ? this.displayAdminReviewInformation.adminReviewDto.dueDate : '',
        emergencyRelease: this.displayAdminReviewInformation.adminReviewDto.emergencyRelease ? this.displayAdminReviewInformation.adminReviewDto.emergencyRelease : '',
        emergencyReleaseDate: this.displayAdminReviewInformation.adminReviewDto.emergencyReleaseDate ? this.displayAdminReviewInformation.adminReviewDto.emergencyReleaseDate : '',
        eventId: this.displayAdminReviewInformation.adminReviewDto.eventId ? this.displayAdminReviewInformation.adminReviewDto.eventId : '',
        hearingDate: this.displayAdminReviewInformation.adminReviewDto.hearingDate ? this.displayAdminReviewInformation.adminReviewDto.hearingDate : '',
        lastUpdateDate: this.displayAdminReviewInformation.adminReviewDto.lastUpdateDate ? this.displayAdminReviewInformation.adminReviewDto.lastUpdateDate : '',
        notificationDate: this.displayAdminReviewInformation.adminReviewDto.notificationDate ? this.displayAdminReviewInformation.adminReviewDto.notificationDate : '',
        personId: this.displayAdminReviewInformation.adminReviewDto.personId ? this.displayAdminReviewInformation.adminReviewDto.personId : '',
        personReviewed: this.displayAdminReviewInformation.adminReviewDto.personReviewed ? this.displayAdminReviewInformation.adminReviewDto.personReviewed : '',
        relatedStageId: this.displayAdminReviewInformation.adminReviewDto.relatedStageId ? this.displayAdminReviewInformation.adminReviewDto.relatedStageId : '',
        requestedBy: this.displayAdminReviewInformation.adminReviewDto.requestedBy ? this.displayAdminReviewInformation.adminReviewDto.requestedBy : '',
        otherRequestedBy: this.displayAdminReviewInformation.adminReviewDto.otherRequestedBy ? this.displayAdminReviewInformation.adminReviewDto.otherRequestedBy : '',
        requestedByName: this.displayAdminReviewInformation.adminReviewDto.requestedByName ? this.displayAdminReviewInformation.adminReviewDto.requestedByName : '',
        result: this.displayAdminReviewInformation.adminReviewDto.result ? this.displayAdminReviewInformation.adminReviewDto.result : '',
        delayedReason: this.displayAdminReviewInformation.adminReviewDto.delayedReason ? this.displayAdminReviewInformation.adminReviewDto.delayedReason : '',
        otherDelayedReason: this.displayAdminReviewInformation.adminReviewDto.otherDelayedReason ? this.displayAdminReviewInformation.adminReviewDto.otherDelayedReason : '',
        reviewDate: this.displayAdminReviewInformation.adminReviewDto.reviewDate ? this.displayAdminReviewInformation.adminReviewDto.reviewDate : '',
        stageId: this.displayAdminReviewInformation.adminReviewDto.stageId ? this.displayAdminReviewInformation.adminReviewDto.stageId : '',
        stageCode: this.displayAdminReviewInformation.stageCode ? this.displayAdminReviewInformation.stageCode : '',
        status: this.displayAdminReviewInformation.adminReviewDto.status ? this.displayAdminReviewInformation.adminReviewDto.status : '',
        type: this.displayAdminReviewInformation.adminReviewDto.type ? this.displayAdminReviewInformation.adminReviewDto.type : '',
        narrativeButton: this.displayAdminReviewInformation.blobExists ? this.displayAdminReviewInformation.blobExists : false,
        program: this.displayAdminReviewInformation.program ? this.displayAdminReviewInformation.program : '',
      });
    }
  }

  disableAllFields() {
    FormUtils.disableFormControlStatus(this.adminReviewForm,
      ["appealRequestDate",
        "authority",
        "otherAuthority",
        "caseId",
        "changeRoleToSp",
        "narrativ",
        "dueDate",
        "emergencyRelease",
        "emergencyReleaseDate",
        "eventI",
        "hearingDate",
        "lastUpdateDate",
        "notificationDate",
        "personId",
        "personReviewed",
        "relatedStageId",
        "requestedBy",
        "otherRequestedBy",
        "requestedByName",
        "result",
        "delayedReason",
        "otherDelayedReason",
        "reviewDate",
        "stageId",
        "status",
        "type",
        "saveAndSubmitStatusMsg",
        "buttonClicked",
        "narrative"
      ]);
  }

  disableARFAllFields() {
    FormUtils.disableFormControlStatus(this.adminReviewForm,
      [
        "emergencyRelease",
        "emergencyReleaseDate",
        "hearingDate"
      ]);
  }

  updateConductDate() {
    const appealRequestDate = this.adminReviewForm.get('appealRequestDate').value;
    if (appealRequestDate) {
      let date = new Date(appealRequestDate);
      let year = date.getFullYear();
      if (year < 2000) {
        year += 1900;
      }
      if ((date.getMonth() >= 0 && date.getMonth() < 12) &&
        (date.getDate() > 0 && date.getDate() < 32) &&
        (year > 1970)) {
        date = new Date(date.valueOf() + 2592000000);
        year = date.getFullYear();
        if (year < 2000) {
          year += 1900;
        }
        const newDate = date.getMonth() + 1 + "/" + date.getDate() + "/" + year;
        this.adminReviewForm.patchValue({ dueDate: newDate });
      }
    }
  }

  validateResultSP() {
    const changeRoleToSp = this.adminReviewForm.get('changeRoleToSp').value;
    const result = this.adminReviewForm.get('result').value;
    if (changeRoleToSp) {
      if ((result === '020') || (result === '030')) {
        this.adminReviewForm.patchValue({ changeRoleToSp: false });
      }
    }
  }

  saveAdminReviewInformation(isSaveAndClose: boolean) {
    const requestPayload = {
      adminReview: this.adminReviewForm.value
    }
    this.caseService.saveAdminReview(requestPayload, isSaveAndClose).subscribe((response) => {
      if (response) {
        setTimeout(() => {
          window.location.reload();
        }, 1000);
      }
    });
  }

  launchForm(data: any) {
    if (data) {
      const formData = JSON.parse(data)
      if ((formData.docType === this.displayAdminReviewInformation.notificationToParentEnglish) ||
        (formData.docType === this.displayAdminReviewInformation.notificationToParentSpanish)) {
        if (this.personId) {
          formData.pPerson = this.personId.toString();
          this.formService.launchForm(JSON.stringify(formData));
          this.loadPersonDataTable();
        } else {
          const initialState = {
            title: 'Admin Review',
            message:
              'Please select at least on person before launching the form',
            showCancel: false,
          };
          const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
          (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
            document.getElementById('formLaunch').focus();
          });
        }
      } else {
        this.formService.launchForm(JSON.stringify(formData));
      }
    } else {
      this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
    }
  }

  getForms(): FormValue[] {
    this.formsArray = [];
    if (this.displayAdminReviewInformation.showFindings) {
      this.formsArray.push({
        formName: 'Findings of Admin Review',
        formParams: {
          displayName: "Findings of Admin Review",
          protectDocument: true,
          docType: 'ccf09o00',
          docExists: 'false',
          pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
          pEvent: this.displayAdminReviewInformation?.adminReviewDto?.eventId ? this.displayAdminReviewInformation?.adminReviewDto?.eventId.toString() : ''
        }
      });
    }
    if (this.displayAdminReviewInformation.showReqParent) {
      this.formsArray.push({
        formName: 'Cover Letter to Requester (English)', formParams: {
          displayName: "Cover Letter to Requester (English)",
          protectDocument: 'false',
          docType: this.displayAdminReviewInformation.coverLetterToRequesterEnglish,
          docExists: 'false',
          pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
          pFormName: this.displayAdminReviewInformation.coverLetterToRequesterEnglish
        }
      },
        {
          formName: 'Cover Letter to Requester (Spanish)', formParams: {
            displayName: "Cover Letter to Requester (Spanish)",
            protectDocument: 'false',
            docType: this.displayAdminReviewInformation.coverLetterToRequesterSpanish,
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pFormName: this.displayAdminReviewInformation.coverLetterToRequesterSpanish
          }
        },
        {
          formName: 'Notif to Parent/Prof Reporter (English)', formParams: {
            displayName: "Notif to Parent/Prof Reporter (English)",
            protectDocument: 'false',
            docType: this.displayAdminReviewInformation.notificationToParentEnglish,
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pPerson: this.personId ? String(this.personId) : '',

          }
        },
        {
          formName: 'Notif to Parent/Prof Reporter (Spanish)', formParams: {
            displayName: "Notif to Parent/Prof Reporter (Spanish)",
            protectDocument: 'false',
            docType: this.displayAdminReviewInformation.notificationToParentSpanish,
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pPerson: this.personId ? String(this.personId) : '',
          }
        });
    }
    if (this.displayAdminReviewInformation.showNotification) {
      this.formsArray.push({
        formName: 'Notif to Requester - AFC (English)', formParams: {
          displayName: "Notif to Requester - AFC (English)",
          protectDocument: 'false',
          docType: 'ccf15o00',
          docExists: 'false',
          pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
          pPerson: this.personId ? String(this.personId) : '',
        }
      },
        {
          formName: 'Notif to Requester - AFC (Spanish)', formParams: {
            displayName: "Notif to Requester - AFC (Spanish)",
            protectDocument: 'false',
            docType: 'ccf16o00',
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pPerson: this.personId ? String(this.personId) : '',
          }
        },
      );
    }
    if (this.displayAdminReviewInformation.showLicensing) {
      this.formsArray.push(
        {
          formName: 'Abuse/Neglect Upheld - LIC (English)', formParams: {
            displayName: "Abuse/Neglect Upheld - LIC (English)",
            protectDocument: 'false',
            docType: 'clf01o00',
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pFormName: 'clf01o00'
          }
        },
        {

          formName: 'Abuse/Neglect Overturned - LIC (English)', formParams: {
            displayName: "Abuse/Neglect Overturned - LIC (English)",
            protectDocument: 'false',
            docType: 'clf02o00',
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pFormName: 'clf02o00'
          }
        },
        {
          formName: 'Release Hearing - A/N Upheld - LIC (English)', formParams: {
            displayName: "Release Hearing - A/N Upheld - LIC (English)",
            protectDocument: 'false',
            docType: 'clf03o00',
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pFormName: 'clf03o00'
          }
        },
        {
          formName: 'Release Hearing - A/N Overturned - LIC (English)', formParams: {
            displayName: "Release Hearing - A/N Overturned - LIC (English)",
            protectDocument: 'false',
            docType: 'clf04o00',
            docExists: 'false',
            pStage: this.displayAdminReviewInformation?.adminReviewDto?.stageId ? this.displayAdminReviewInformation?.adminReviewDto?.stageId.toString() : '',
            pFormName: 'clf04o00'
          }
        });
    }
    return this.formsArray;
  }

  triggerAPICall(value: any) {
    if (this.validateFormGroup(this.adminReviewForm)) {
      if (value === 'save') {
        this.saveAdminReviewInformation(false);
      } else if (value === 'submit') {
        this.saveAdminReviewInformation(true);
      }
    }
  }

  checkForNewResult(value: string) {
    const selectedStatus = this.adminReviewForm.get('status').value;
    const originalStatus = this.displayAdminReviewInformation.adminReviewDto.status;
    if (this.displayAdminReviewInformation.stageCode == 'ARI') {
      if (selectedStatus == '030' || selectedStatus == '040' || originalStatus == '030' || originalStatus == '040') {
        const originalResult = this.displayAdminReviewInformation.adminReviewDto.result;
        if (this.adminReviewForm.get('result').value === '020') {
          this.isAlert = true;
          const initialState = {
            title: 'Admin Review',
            message: dfpsValidatorErrors.MSG_ADMIN_RVW_RESULT_CHG,
            showCancel: true,
          };
          const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
          (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
            this.isAlert = false;
            if (result === true) {
              this.triggerAPICall(value);
            } else {
              this.adminReviewForm.patchValue({ 'result': originalResult });
              return false;
            }
          });
        } else {
          this.triggerAPICall(value);
        }
      } else {
        this.triggerAPICall(value);
      }
    } else {
      this.triggerAPICall(value);
    }
  }

  save() {
    this.adminReviewForm.patchValue({ buttonClicked: 'save' });
    // do save the form
    this.checkForNewResult('save');
  }

  submit() {
    this.adminReviewForm.patchValue({ buttonClicked: 'saveandclose' });
    this.checkForNewResult('submit');
  }

  getNarrative(): FormParams {
    return {
      displayName: "Form Info",
      name: "frmDocumentTag",
      protectDocument: this.displayAdminReviewInformation.protectDocument ?
        String(this.displayAdminReviewInformation.protectDocument) : '',
      docType: 'adminrev',
      checkStage: this.displayAdminReviewInformation.adminReviewDto.stageId ?
        String(this.displayAdminReviewInformation.adminReviewDto.stageId) : '',
      docExists: this.displayAdminReviewInformation.blobExists ?
        String(this.displayAdminReviewInformation.blobExists) : '',
      sEvent: this.displayAdminReviewInformation.adminReviewDto.eventId ?
        String(this.displayAdminReviewInformation.adminReviewDto.eventId) : '',
      sCase: this.displayAdminReviewInformation.adminReviewDto.caseId ?
        String(this.displayAdminReviewInformation.adminReviewDto.caseId) : '',
      sTimestamp: this.displayAdminReviewInformation.narrativeLastUpdatedDate ?
        String(this.displayAdminReviewInformation.narrativeLastUpdatedDate) : ''
    };
  }

  getSelectedlaunchValue(event: any) {
    this.selectedFormValue = event?.target?.value ? JSON.parse(event.target.value) : '';
    if (this.selectedFormValue?.docType == this.displayAdminReviewInformation.notificationToParentEnglish || this.selectedFormValue?.docType == this.displayAdminReviewInformation.notificationToParentSpanish) {
      this.showPersonList = true;
    } else {
      this.showPersonList = false;
    }
  }

  loadPersonDataTable() {
    this.tableColumn = [
      { field: 'fullName', header: 'Name', width: 100 },
      { field: 'dobApprox', header: 'Aprx', width: 50 },
      { field: 'age', header: 'Age', width: 50 },
      { field: 'sex', header: 'Gender', width: 50 },
      { field: 'personType', header: 'Type', width: 50 },
      { field: 'role', header: 'Role', width: 50 },
      { field: 'relInt', header: 'Rel/Int', width: 50 },
      { field: 'personId', header: 'Person ID', width: 50 },
    ];

    this.personTableData = {
      tableColumn: this.tableColumn,
      isPaginator: false,
      isSingleSelect: true
    };

    if (this.displayAdminReviewInformation?.personDtoList) {
      this.personTableData.tableBody = this.displayAdminReviewInformation?.personDtoList;
    }

  }

  getSelectedPerson(selectedRow) {
    if (selectedRow) {
      this.personId = selectedRow.personId;
    }
  }

  continue() {
    this.launchForm(JSON.stringify(this.selectedFormValue));
  }

  authorityChange() {
      this.adminReviewForm.patchValue({ otherAuthority: '' });
  }

  requestedByChange() {
      this.adminReviewForm.patchValue({ otherRequestedBy: '' });
  }

  resultChange() {
    this.adminReviewForm.patchValue({delayedReason: ''});
  }

  delayedReasonByChange() {
    this.adminReviewForm.patchValue({otherDelayedReason: ''});
  }

}
