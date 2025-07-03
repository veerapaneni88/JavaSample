import { Component, Inject, OnDestroy, OnInit, forwardRef } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators, NG_VALUE_ACCESSOR } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  FormValue,
  NavigationService,
  SET,
  ENVIRONMENT_SETTINGS,
  FormService
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CaseService } from '../service/case.service';
import { DisplayApsSNADetails } from '../model/apsSNADetails';
import { SNAValidators } from './strength-needs-assessment-detail.validator';
import { HelpService } from '../../../common/impact-help.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-strength-needs-assessment-detail',
  templateUrl: './strength-needs-assessment-detail.component.html',
})
export class StrengthNeedsAssessmentDetailComponent extends DfpsFormValidationDirective implements OnInit {
  apsSNADetailsForm: FormGroup;
  displayApsSNADetails: DisplayApsSNADetails;
  disableCaretakerDropdown = false;
  eventId: any;
  responses: any = [];
  formValues: FormValue[];
  incompleteDomainsPC: [];
  incompleteDomainsCL: [];
  isEditMode: boolean;
  environment: string;
  isMPSEnvironment = false;
  formsSubscription: Subscription;

  constructor(private navigationService: NavigationService, private modalService: BsModalService,
    private fb: FormBuilder, private caseService: CaseService, private route: ActivatedRoute,
    private router: Router,
    private helpService: HelpService,
    private formService: FormService,
    public store: Store<{ dirtyCheck: DirtyCheck }>,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any) {
    super(store);
    this.setUserData();
  }

  setUserData() {
    // set user data for 3rd level menu rendering
    const params = this.route.snapshot.paramMap.get('eventId');
    this.navigationService.setUserDataValue('eventId', params);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('APS Strengths and Needs Assessment Page');
    this.helpService.loadHelp('Assess');
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.eventId = routeParams.get('eventId');
    }
    this.initializeScreen();
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
    this.isMPSEnvironment = 'MPS' === this.environmentSettings.environmentName.toUpperCase();
  }

  createForm() {
    this.apsSNADetailsForm = this.fb.group({
      responses: this.fb.array([]),
      caretakerId: [''],
      indPrimaryCaretaker: [''],
      textDesCL: [''],
      textDesPC: [''],
      isSaveClicked: [false],
      isSaveAndCompleteClicked: [false],
      clientSectionExpanded: [false],
      pcsectionExpanded: [false],
      rootCausesSectionExpanded: [false]
    }, {
      validators: [
        SNAValidators.validateSNAFormInfo
      ]
    });
  }

  private apsSNADetailsFormGrp(code: any): FormGroup {
    return this.fb.group({
      id: code.id,
      createdPersonId: code.createdPersonId,
      domainCode: code.domainCode,
      answers: code.answers,
      domainText: code.domainText,
      responseCode: code.responseCode,
      apsSnaDomainLookupId: code.apsSnaDomainLookupId,
      apsSnaAnswerLookupId: code.apsSnaAnswerLookupId,
      txtOtherDescription: code.txtOtherDescription,
      sectionCode: code.sectionCode,
      definitionName: code.definitionName,
      indIncludeServicePlan: code.indIncludeServicePlan,
      includeInServicePlan: code.includeInServicePlan,
      answerText: code.answerText,
      servicePlanDomainId: code.servicePlanDomainId
    });
  }

  initializeScreen() {
    this.createForm();
    this.caseService.getApsSNADetails(this.eventId).subscribe(res => {
      this.displayApsSNADetails = res;
      this.displayApsSNADetails.responses.forEach(code => {
        (this.apsSNADetailsForm.get('responses') as FormArray).push(
          this.apsSNADetailsFormGrp(code)
        );
      });
      this.setForm();
      this.isPageChanged();
      this.determinePageMode();
      this.formValues = this.getForms();
      this.environment = this.environmentSettings.environmentName === 'Local' ? '' : '/impact3';
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
  }

  setForm() {
    if (this.displayApsSNADetails) {
      this.apsSNADetailsForm.patchValue({
        caretakerId: this.displayApsSNADetails.caretakerId,
        indPrimaryCaretaker: this.displayApsSNADetails.indPrimaryCaretaker,
        responses: this.displayApsSNADetails.responses,
        clientSectionExpanded: this.displayApsSNADetails.clientSectionExpanded,
        pcsectionExpanded: this.displayApsSNADetails.pcsectionExpanded,
        rootCausesSectionExpanded: this.displayApsSNADetails.rootCausesSectionExpanded
      })
      if (this.displayApsSNADetails.indPrimaryCaretaker) {
        this.disableCaretakerDropdown = true;
      }
    }
  }

  updateClientSectionSection(el) {
    this.apsSNADetailsForm.patchValue({
      clientSectionExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  updatePCSectionSection(el) {
    this.apsSNADetailsForm.patchValue({
      pcsectionExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  updateRootCausesSectionSection(el) {
    this.apsSNADetailsForm.patchValue({
      rootCausesSectionExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  determinePageMode() {
    if (this.displayApsSNADetails.pageMode === 'EDIT' || this.displayApsSNADetails.pageMode === 'NEW') {
      this.isEditMode = true;
    } else {
      FormUtils.disableFormControlStatus(this.apsSNADetailsForm, ['responses', 'caretakerId', 'indPrimaryCaretaker']);
    }
  }
  launchForm(data: any) {
    if (data) {
      const { pageMode, eventId } = this.displayApsSNADetails;
      if ((pageMode === 'NEW' && !eventId) || (pageMode === 'EDIT' && this.apsSNADetailsForm.dirty)) {
        const initialState = {
          title: 'Safety Assessment',
          message: pageMode === 'EDIT' ?
            'Page data has changed.  Please save before producing document.' :
            'Please save page before producing document.',
          showCancel: false
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md', initialState,
          ignoreBackdropClick: true,
          keyboard: false,
          backdrop: true
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
        });
        return;
      }
    }
    this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
  }

  ngOnDestroy(): void {
    this.formsSubscription?.unsubscribe();
  }

  getForms(): FormValue[] {
    return [{
      formName: 'SNA Form',
      formParams: {
        docType: 'APSSNA',
        docExists: 'false',
        protectDocument: 'true',
        checkForNewMode: 'false',
        pStage: this.displayApsSNADetails.stageId ?
          String(this.displayApsSNADetails.stageId) : '',
        pEvent: this.displayApsSNADetails.eventId ? String(this.displayApsSNADetails.eventId) : ''
      }
    }
    ];
  }

  selectedPCAnswers() {
    return this.apsSNADetailsForm.controls.responses.value.some(el => el.sectionCode === 'PC' && el.apsSnaAnswerLookupId != null)
  }

  caretakerSelected() {
    if (this.apsSNADetailsForm.get('indPrimaryCaretaker').value) {
      if (this.apsSNADetailsForm.get('caretakerId').value || this.selectedPCAnswers()) {
        this.showInvalidateApprovalPopup();
      } else {
        this.disableCaretakerDropdown = true;
      }
    } else {
      this.disableCaretakerDropdown = false;
    }
  }

  saveCompleteValidatePopupOne() {
    const initialState = {
      title: this.displayApsSNADetails.pageTitle,
      message:
        'Some answers have been changed.'
        + ' Please click "Save" to review the "Consideration of Root Causes" section prior to completing the assessment.',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
    });
  }

  reviewCRCPopup() {
    const initialState = {
      title: this.displayApsSNADetails.pageTitle,
      message:
        'All required domains have been answered.'
        + 'Please click "Save" to review the "Consideration of Root Causes" section prior to completing the assessment.',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
    });
  }

  showInvalidateApprovalPopup() {
    const initialState = {
      title: this.displayApsSNADetails.pageTitle,
      message: 'Clicking this checkbox will clear the Primary Caretaker answers. Click OK to continue.',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result === true) {
        this.disableCaretakerDropdown = true;
        this.apsSNADetailsForm.get('caretakerId').disable();
        this.apsSNADetailsForm.patchValue({
          caretakerId: '',
        });
        this.displayApsSNADetails.responses.forEach((code, index) => {
          if (code.sectionCode === 'PC') {
            code.apsSnaAnswerLookupId = null;
            code.txtOtherDescription = null;
            (this.apsSNADetailsForm.get('responses') as FormArray).at(index).patchValue(code)
          }
        });
      } else {
        this.apsSNADetailsForm.patchValue({
          indPrimaryCaretaker: false
        })
      }
    });
  }

  save() {
    this.apsSNADetailsForm.patchValue({
      isSaveClicked: true,
      isSaveAndCompleteClicked: false,
    });
    if (this.validateFormGroup(this.apsSNADetailsForm)) {
      this.submit()
    }
  }

  submit() {
    const reqObj = Object.assign(this.displayApsSNADetails, this.apsSNADetailsForm.getRawValue());
    this.caseService.saveApsSNADetails(reqObj).subscribe(res => {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      if (res) {
        this.eventId = res.eventId;
        this.router.navigateByUrl('/',
          { skipLocationChange: true }).then(() => {
            this.router.navigate(['case/strength-needs-assessment-detail/' + res.eventId]);
          });
      }
    })
  }

  isPageChanged() {
    const defaultValue = this.apsSNADetailsForm.value;
    this.apsSNADetailsForm.valueChanges
      .subscribe(value => {
        if (JSON.stringify(defaultValue) === JSON.stringify(value)) {
          this.apsSNADetailsForm.markAsPristine();
        }
      });
  }

  saveCompelte() {
    this.apsSNADetailsForm.patchValue({
      isSaveAndCompleteClicked: true,
      isSaveClicked: false,
    });
    if (this.validateFormGroup(this.apsSNADetailsForm)) {
      if (this.displayApsSNADetails.sectionCompleteCL && this.displayApsSNADetails.sectionCompletePC) {
        if (!this.apsSNADetailsForm.pristine) {
          this.saveCompleteValidatePopupOne();
        } else if (this.apsSNADetailsForm.pristine) {
          this.eventFreezePopup();
        }
      } else if (!this.checkNullValues() && !this.apsSNADetailsForm.pristine) {
        this.reviewCRCPopup();
      } else {
        this.submitSaveAndCompelte();
      }
    }
  }

  // To check the null values, in the responses based on section code.
  checkNullValues() {
    if (this.apsSNADetailsForm.get('indPrimaryCaretaker').value) {
      return this.apsSNADetailsForm.controls.responses.value.some(el => el.sectionCode === 'CL' && el.apsSnaAnswerLookupId === null);
    } else {
      return this.apsSNADetailsForm.controls.responses.value.some(el => el.apsSnaAnswerLookupId === null);
    }
  }

  eventFreezePopup() {
    const initialState = {
      title: this.displayApsSNADetails.pageTitle,
      message: 'Completion will freeze this Event. Press OK to Complete and freeze this Event, or press cancel to continue working.',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.submitSaveAndCompelte()
      }
    });
  }

  submitSaveAndCompelte() {
    const reqObj = Object.assign(this.displayApsSNADetails, this.apsSNADetailsForm.getRawValue());
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.caseService.saveAndSubmitApsSNADetails(reqObj).subscribe(res => {
      if (res) {
        this.router.navigateByUrl('/',
          { skipLocationChange: true }).then(() => {
            this.router.navigate(['case/strength-needs-assessment-detail/' + res.eventId]);
          });
      }
    })
  }
}
