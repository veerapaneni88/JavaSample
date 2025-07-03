import { formatDate } from '@angular/common';
import { Component, Inject, LOCALE_ID, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { KinAssessmentRes, KinAssessmentDetail, KinPlacementInfo } from '@case/model/kinHomeAssessment';
import { pairwise, startWith, tap, withLatestFrom } from 'rxjs/operators';
import { CaseService } from '@case/service/case.service';
import { Store } from '@ngrx/store';
import {
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  DropDown,
  ENVIRONMENT_SETTINGS, FormService,
  FormUtils, FormValue,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { HelpService } from 'app/common/impact-help.service';
import { KinHomeAssessmentDetailValidators } from './kinhome-assessment-detail.validator';

@Component({
  selector: 'kinhome-assessment-detail',
  templateUrl: './kinhome-assessment-detail.html'
})
export class KinHomeAssessmentDetail extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  kinAssessmentDetailForm: FormGroup;
  kinAssessmentRes: KinAssessmentRes;
  kinAssessmentId: any = 0;
  isEditMode = false;
  isNewRecord = false;
  isFixer = false;
  isDisableSvcAuth = false;
  isDisableAll = false;
  isFixedEdited = false;
  isApproved = false;
  isDenied = false;
  indCriminalHistory = false;
  indAbuseNeglectHistory = false;
  indOtherReason = false;
  hideSaveSubmitButton = false;
  hideSaveButton = false;
  formValues: FormValue[];
  maxDate = { year: new Date().getUTCFullYear(), month: new Date().getMonth() + 1, day: new Date().getDate() };
  formsSubscription: Subscription;
  yesNo: any;
  caregiverPID: number;

  constructor(private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: BsModalService,
    private caseService: CaseService,
    private formService: FormService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.yesNo = [
      { decode: 'Yes', code: 'Y' },
      { decode: 'No', code: 'N' },
    ];
    this.navigationService.setTitle('Kinship Home Assessment');
    this.helpService.loadHelp('Placement');
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.kinAssessmentId = Number(routeParams.get('kinAssessmentId'));
      this.isNewRecord = this.kinAssessmentId === 0 ? true : false;
    }
    this.initializeScreen();
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
  }

  ngOnDestroy(): void {
    this.formsSubscription?.unsubscribe();
  }

  initializeScreen() {
    this.caseService.getEleKinHomeAssessmentDetail(this.kinAssessmentId).subscribe((response) => {
      this.kinAssessmentRes = response;
      this.isFixer = this.kinAssessmentRes?.fixer;
      this.createForm();
      this.loadKinHomeAssessmentDetail();
      this.initializeKinPlacementInfo();

      this.formValues = this.getForms();
    });
  }
  initializeKinPlacementInfo() {
    if (this.kinAssessmentRes?.kinAssessmentDetail?.kinPlacementInfo) {
      this.kinAssessmentRes.kinAssessmentDetail.kinPlacementInfo.forEach((code) => {
        (this.kinAssessmentDetailForm.get('kinPlacementInfo') as FormArray).push(
          this.kinPlacementsFormGrp(code)
        );
      });
    }
  }
  private kinPlacementsFormGrp(code: any): FormGroup {
    return this.formBuilder.group({
      id: code.id,
      childId: code.childId,
      childName: code.childName,
      dtOfPlacement: [{ value: code.dtOfPlacement, disabled: true }]
    });
  }

  removePlacementGroup() {
    const placemntControl = this.kinAssessmentDetailForm.get('kinPlacementInfo') as FormArray;
    placemntControl.controls = [];
  }

  getForms(): FormValue[] {
    return [
      {
        formName: 'Kinship Home Assessment',
        formParams: {
          docType: 'cvskinhomeassessment',
          docExists: 'false',
          windowName: 'Kinship Home Assessment',
          protectDocument: 'true',
          checkForNewMode: 'false',
          pEvent: this.kinAssessmentId !== null && this.kinAssessmentId !== undefined ? this.kinAssessmentId : this.kinAssessmentRes?.kinAssessmentDetail?.eventId
        }
      }

    ];
  }

  createForm() {
    const kinAssessmentRes = this.kinAssessmentRes;
    const details = this.kinAssessmentRes?.kinAssessmentDetail;
    this.kinAssessmentDetailForm = this.formBuilder.group({
      caregiverName: [details?.caregiverName],
      caregiverId: [details?.caregiverId],
      caseId: [this.kinAssessmentRes?.caseId],
      stageId: [this.kinAssessmentRes?.stageId],
      eventId: [this.kinAssessmentRes?.eventId],
      svcAuthDate: [details?.svcAuthDate],
      homeAssmtSubmittedDate: [details?.homeAssmtSubmittedDate],
      indAutoPopulate: [details?.indAutoPopulate],
      approvalCd: [details?.approvalCd],
      dtOfApproval: [details?.dtOfApproval],
      dtOfDenial: [details?.dtOfDenial],
      criminalHistory: [details?.indCriminalHistory === 'Y'],
      abuseNeglectHistory: [details?.indAbuseNeglectHistory === 'Y'],
      otherReason: [details?.indOtherReason === 'Y'],
      txtComments: [details?.txtComments],
      chKinSafety: [details?.chKinSafety === 'Y'],
      chAddendum: [details?.chAddendum === 'Y'],
      abnKinSafety: [details?.abnKinSafety === 'Y'],
      abnAddendum: [details?.abnAddendum === 'Y'],
      denialReasonComments: [details?.denialReasonComments],
      dnCommentEnteredBy: [details?.dnCommentEnteredBy],
      dtDnCommentEntered: [details?.dtDnCommentEntered],
      dnCommentModifiedBy: [details?.dtDnCommentEntered !== details?.dtDnCommentModified ? details?.dnCommentModifiedBy : ''],
      dtDnCommentModified: [details?.dtDnCommentEntered !== details?.dtDnCommentModified ? details?.dtDnCommentModified : ''],
      fixedEdited: [details?.fixedEdited],
      kinPlacementInfo: new FormArray([]),
    }, {
      validators: [
        KinHomeAssessmentDetailValidators.checkboxValidation,
      ]
    });
    this.subscribeAprovalCd(details?.approvalCd || null);
    this.disableFields();
    this.formValues = this.getForms();
  }

  getCustomStyles() {
    return 'line-height: 2.5';
  }

  subscribeAprovalCd(approvalCd: string) {
    if (['APAD', 'APKS', 'Y'].includes(approvalCd)) {
      this.isApproved = true;
    }

    this.isDenied = approvalCd === 'N';

    this.kinAssessmentDetailForm.get('approvalCd').valueChanges.subscribe((value) => {
      if (['APAD', 'APKS', 'Y'].includes(value)) {
        this.isApproved = true;
      }
      else {
        this.isApproved = false;
      }
      this.isDenied = value === 'N' ? true : false;
    });
  }

  disableFields() {
    const details = this.kinAssessmentRes?.kinAssessmentDetail;
    this.isDisableSvcAuth = details?.indAutoPopulate === 'Y';
    this.isDisableAll = this.kinAssessmentRes?.pageMode === 'VIEW';
    this.isEditMode = this.kinAssessmentRes?.pageMode === 'EDIT';
    this.isFixedEdited = (this.isEditMode && details?.indSaveComplete === 'Y') || (details?.fixedEdited === 'Y');
    let fieldsToBeDisabled = ['caregiverName', 'caregiverId', 'svcAuthDate'];
    let careGiverfields = ['caregiverName', 'caregiverId'];

    if (this.isDisableAll) {
      this.kinAssessmentDetailForm.disable();
      this.hideSaveSubmitButton = true;
      this.hideSaveButton = true;
    }
    if (this.isDisableSvcAuth) {
      FormUtils.disableFormControlStatus(this.kinAssessmentDetailForm, fieldsToBeDisabled);
    }
    if (this.isFixedEdited) {
      FormUtils.disableFormControlStatus(this.kinAssessmentDetailForm, careGiverfields);
      this.kinAssessmentDetailForm.patchValue({ fixedEdited: 'Y' });
    }

  }

  launchForm(data: any) {
    if (data && this.isEditMode) {
      const initialState = {
        title: 'Kinship Home Assessment',
        message: 'Form can be generated only after you Save and Complete the page',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
      });
      return;
    }
    this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
  }

  loadKinHomeAssessmentDetail() {
    if (this.kinAssessmentRes && this.kinAssessmentRes?.kinAssessmentDetail) {
      const kinAssessmentRes = this.kinAssessmentRes;
      const details = this.kinAssessmentRes?.kinAssessmentDetail;
      const isDisableSvcAuth = details?.indAutoPopulate === 'Y';
      this.indCriminalHistory = details?.indCriminalHistory === 'Y';
      this.indAbuseNeglectHistory = details?.indAbuseNeglectHistory === 'Y';
      this.indOtherReason = details?.indOtherReason === 'Y';
      this.caregiverPID = details?.caregiverId;
      this.kinAssessmentDetailForm?.patchValue({
        caregiverName: isDisableSvcAuth ? details?.caregiverName : details?.caregiverId,
        caregiverId: details?.caregiverId,
        caseId: this.kinAssessmentRes?.caseId,
        stageId: this.kinAssessmentRes?.stageId,
        eventId: this.kinAssessmentRes?.eventId,
        svcAuthDate: details?.svcAuthDate,
        homeAssmtSubmittedDate: details?.homeAssmtSubmittedDate,
        approvalCd: details?.approvalCd,
        dtOfApproval: details?.dtOfApproval,
        dtOfDenial: details?.dtOfDenial,
        indAutoPopulate: details?.indAutoPopulate,
        indSaveComplete: details?.indSaveComplete,
        criminalHistory: details?.indCriminalHistory === 'Y',
        abuseNeglectHistory: details?.indAbuseNeglectHistory === 'Y',
        otherReason: details?.indOtherReason === 'Y',
        txtComments: details?.txtComments,
        denialReasonComments: details?.denialReasonComments,
        chKinSafety: details?.chKinSafety,
        chAddendum: details?.chAddendum,
        abnKinSafety: details?.abnKinSafety,
        abnAddendum: details?.abnAddendum,
        dnCommentEnteredBy: details?.dnCommentEnteredBy,
        dtDnCommentEntered: details?.dtDnCommentEntered,
        dnCommentModifiedBy: details?.dtDnCommentEntered !== details?.dtDnCommentModified ? details?.dnCommentModifiedBy : '',
        dtDnCommentModified: details?.dtDnCommentEntered !== details?.dtDnCommentModified ? details?.dtDnCommentModified : '',
        fixedEdited: details?.fixedEdited,
        kinPlacementInfo: details?.kinPlacementInfo

      });
      this.disableFields();
    }
    this.formValues = this.getForms();
  }

  approvalCdChange() {
    const approvalCd = this.kinAssessmentDetailForm.get('approvalCd').value;
    if (['APAD', 'APKS', 'Y'].includes(approvalCd)) {
      this.isApproved = true;
      this.isDenied = false;
      this.resetDenialSection();
    }
    else if (approvalCd === 'N') {
      this.isApproved = false;
      this.isDenied = true;
      this.resetApprovalSection();
    }
    else {
      this.isApproved = false;
      this.isDenied = false;
      this.resetApprovalSection();
      this.resetDenialSection();
    }
  }
  resetApprovalSection() {
    this.kinAssessmentDetailForm.patchValue({ dtOfApproval: '' });
  }
  resetDenialSection() {
    this.kinAssessmentDetailForm.patchValue({
      dtOfDenial: '',
      criminalHistory: false, chKinSafety: '', chAddendum: '', abuseNeglectHistory: false
      , abnKinSafety: '', abnAddendum: '', otherReason: false, denialReasonComments: '',
      dnCommentEnteredBy: '', dtDnCommentEntered: '', dnCommentModifiedBy: '',
      dtDnCommentModified: ''
    });
    this.indCriminalHistory = false;
    this.indAbuseNeglectHistory = false;
    this.indOtherReason = false;
  }

  criminalHistoryClicked() {
    this.indCriminalHistory = this.kinAssessmentDetailForm?.get('criminalHistory').value;
    if (!this.indCriminalHistory) {
      this.kinAssessmentDetailForm.patchValue({ chKinSafety: '', chAddendum: '' });
    }
  }
  abnHistoryClicked() {
    this.indAbuseNeglectHistory = this.kinAssessmentDetailForm?.get('abuseNeglectHistory').value;
    if (!this.indAbuseNeglectHistory) {
      this.kinAssessmentDetailForm.patchValue({ abnKinSafety: '', abnAddendum: '' });
    }
  }
  otherReasonClicked() {
    this.indOtherReason = this.kinAssessmentDetailForm?.get('otherReason').value;
    if (!this.indOtherReason) {
      this.kinAssessmentDetailForm.patchValue({
        denialReasonComments: '', dnCommentEnteredBy: '',
        dtDnCommentEntered: '', dnCommentModifiedBy: '', dtDnCommentModified: ''
      });
    }
  }

  onCaregiverNameChange() {
    this.caregiverPID = this.kinAssessmentDetailForm.get('caregiverName').value;
    const caregiverId = this.caregiverPID !== null && this.caregiverPID !== undefined ? this.caregiverPID : 0;
    this.caseService.getChildPlacementDetail(caregiverId).subscribe((response) => {
      if (response) {
        this.kinAssessmentRes.kinAssessmentDetail.kinPlacementInfo = response.kinAssessmentDetail.kinPlacementInfo;
        this.removePlacementGroup();
        this.initializeKinPlacementInfo();
      }
    });
  }
  save() {
    const details = this.kinAssessmentRes?.kinAssessmentDetail;
    const caregiverId = details?.indAutoPopulate === 'Y' ? details?.caregiverId : this.caregiverPID;
    if (caregiverId === null || caregiverId === undefined) {
      const initialState = {
        title: 'Name of Kinship Caregiver',
        message: 'Name of Kinship Caregiver is a required field. Please enter a value',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    }
    else {
      const indSaveComplete = 'N';
      const payload = this.getPayload(indSaveComplete);
      this.caseService.saveEleKinHomeAssessmentDetail(payload).subscribe(
        response => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          this.kinAssessmentId = response?.kinAssessmentDetail?.eventId;
          this.router.navigate(['case/placement/kinhome-assessments/' + this.kinAssessmentId])
            .then(() => {
              window.location.reload();
            });
        });
    }
  }
  saveComplete() {
    const details = this.kinAssessmentRes?.kinAssessmentDetail;
    let isValid = true;
    let validationErrors = [];
    this.kinAssessmentDetailForm.patchValue({
      indAutoPopulate:
        details?.indAutoPopulate !== null && details?.indAutoPopulate !== undefined ? details?.indAutoPopulate : 'N'
    });
    if (!this.validateFormGroup(this.kinAssessmentDetailForm)) {
      isValid = false;
      validationErrors = this.validationErrors.map((x) => ({ ...x }));
    }
    if (isValid) {
      const indSaveComplete = 'Y';
      const payload = this.getPayload(indSaveComplete);
      this.caseService.saveEleKinHomeAssessmentDetail(payload).subscribe(
        response => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          setTimeout(() => {
            this.caseService.redirectToUrl('case/placement/kinhome-assessments');
          }, 2000);
        });
    }

  }
  edit() {
    this.caseService.updateKinHomeAssessmentEvent(this.kinAssessmentRes).subscribe(
      response => {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let fieldsToBeEnabled = ['svcAuthDate', 'homeAssmtSubmittedDate', 'approvalCd', 'dtOfApproval',
            'dtOfDenial', 'criminalHistory', 'chKinSafety', 'chAddendum', 'abuseNeglectHistory', 
            'abnKinSafety', 'abnAddendum', 'otherReason', 'denialReasonComments', 'txtComments'
          ];
          FormUtils.enableFormControlStatus(this.kinAssessmentDetailForm, fieldsToBeEnabled);
          this.isEditMode = response?.pageMode === 'EDIT';
          this.hideSaveSubmitButton = false;
          this.hideSaveButton = false;
          this.kinAssessmentDetailForm.patchValue({ fixedEdited: 'Y' });
          this.isFixedEdited = true;
      });
  }

  getPayload(indSaveComplete) {
    const kinAssessmentRes = this.kinAssessmentRes;
    const details = this.kinAssessmentRes?.kinAssessmentDetail;
    const isSvcAuthAutoPopulated = details?.indAutoPopulate === 'Y';
    const kinPlacementsControl = this.kinAssessmentDetailForm.get('kinPlacementInfo') as FormArray;
    return {
      id: details?.id,
      caseId: kinAssessmentRes?.caseId,
      stageId: kinAssessmentRes?.stageId,
      eventId: this.kinAssessmentId != null && this.kinAssessmentId != undefined ? this.kinAssessmentId : details?.eventId,
      idSvcAuth: details?.idSvcAuth,
      caregiverName: isSvcAuthAutoPopulated ? details?.caregiverName : "",
      caregiverId: isSvcAuthAutoPopulated ? details?.caregiverId : this.caregiverPID,
      svcAuthDate: isSvcAuthAutoPopulated ? details?.svcAuthDate : this.kinAssessmentDetailForm.get('svcAuthDate').value,
      homeAssmtSubmittedDate: !isSvcAuthAutoPopulated ? this.kinAssessmentDetailForm.get('homeAssmtSubmittedDate').value : "",
      approvalCd: this.kinAssessmentDetailForm.get('approvalCd')?.value,
      dtOfApproval: this.kinAssessmentDetailForm.get('dtOfApproval')?.value,
      dtOfDenial: this.kinAssessmentDetailForm.get('dtOfDenial')?.value,
      indAutoPopulate: details?.indAutoPopulate,
      indSaveComplete: indSaveComplete,
      indCriminalHistory: this.kinAssessmentDetailForm.get('criminalHistory').value ? 'Y' : 'N',
      indAbuseNeglectHistory: this.kinAssessmentDetailForm.get('abuseNeglectHistory').value ? 'Y' : 'N',
      indOtherReason: this.kinAssessmentDetailForm.get('otherReason')?.value ? 'Y' : 'N',
      txtComments: this.kinAssessmentDetailForm.get('txtComments')?.value,
      chKinSafety: this.kinAssessmentDetailForm.get('chKinSafety')?.value,
      chAddendum: this.kinAssessmentDetailForm.get('chAddendum')?.value,
      abnKinSafety: this.kinAssessmentDetailForm.get('abnKinSafety')?.value,
      abnAddendum: this.kinAssessmentDetailForm.get('abnAddendum')?.value,
      denialReasonComments: this.kinAssessmentDetailForm.get('denialReasonComments')?.value,
      dnCommentEnteredBy: details?.dnCommentEnteredBy,
      dtDnCommentEntered: details?.dtDnCommentEntered,
      dnCommentModifiedBy: details?.dnCommentModifiedBy,
      dtDnCommentModified: details?.dtDnCommentModified,
      fixedEdited: this.kinAssessmentDetailForm.get('fixedEdited')?.value,
      kinPlacementInfo: kinPlacementsControl.getRawValue(),
      kinCaregiverInfo: details?.kinCaregiverInfo

    }
  }

}
