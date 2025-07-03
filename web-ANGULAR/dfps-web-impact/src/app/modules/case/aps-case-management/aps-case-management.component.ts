import { Component, OnInit, Inject, ViewChild, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn,  Validators } from '@angular/forms';
import { CaseService } from '@case/service/case.service';
import {
  Address,
  DfpsAddressValidatorComponent, DfpsCommonValidators, DfpsConfirmComponent, DfpsFormValidationDirective, DirtyCheck,
  ENVIRONMENT_SETTINGS,
  FormValue,
  NarrativeService,
  NavigationService,
  SET,
  FormUtils
} from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import {HelpService} from '../../../common/impact-help.service';
import {Subscription} from 'rxjs';
import {Store} from '@ngrx/store';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ApsCaseManagementValidator } from './aps-case-management.validator';
import { take } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-aps-case-management',
  templateUrl: './aps-case-management.component.html',
  styleUrls: ['./aps-case-management.component.css'],
})
export class ApsCaseManagementComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;

  apsCaseManagementData: any; // TODO add the type for it
  victimInformedofCaseClosureData: { label: string, value: string }[] = [];
  victimInformedofStageClosureData: { label: string, value: string }[] = [];
  servicesProvidedData: { label: string, value: string }[] = [];
  clientCommunicationData: { label: string, value: string }[] = [];
  counties = [];
  states = [];
  yesNoOptions: { label: string, value: string }[] = [];
  checkList: string;
  closureReasons: [];
  closureReason: '';
  programAdmins: [];
  showLEPSection = false;

  caseManagementForm: FormGroup;
  svcCaseManagementform: FormGroup;
  formValues: FormValue[];
  narrativeSubscription: Subscription;
  addressSub: Subscription;
  dirtyCheckSub: Subscription;
  isAddrValidated = true;
  selfNeglectAccordionOpen : boolean;
  cjAccordionOpen : boolean;

  constructor(private caseService: CaseService,
    private formBuilder: FormBuilder,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private cookieService: CookieService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private modalService: BsModalService,
    private narrativeService: NarrativeService,
    public store: Store<{ dirtyCheck: DirtyCheck }>,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any) {
    super(store);
  }

  ngOnInit(): void {
    this.helpService.loadHelp('Case');
    this.initializeScreen();

    this.victimInformedofCaseClosureData = [
      { label: 'Alleged victim/client advised of case closure', value: 'Advised' },
      { label: 'Alleged victim/client not advised of case closure (Explain in Narrative)', value: 'Not Advised' },
    ];

    this.victimInformedofStageClosureData = [
      { label: 'Client advised of case closure', value: 'Advised' },
      { label: 'Client not advised of case closure (Explain in Narrative)', value: 'Not Advised' },
    ];

    this.servicesProvidedData = [
      { label: 'Client participated in service planning', value: 'Participated' },
      { label: 'Client did not participate in service planning (Explain in Narrative)', value: 'Not Participated' },
    ];

    this.clientCommunicationData = [
      {
        label: `APS specialist did not obtain the services of an interpreter because APS specialist is fluent in the principal's preferred
      language or other communication methods. APS specialist is able to fully communicate with client/alleged perpetrator.`, value: 'Translator Not Used'
      },
      {
        label: `APS specialist was unable to communicate with principals. APS specialist obtained the services of a translator or used
      another method to communicate with the client/alleged perpetrator.`, value: 'Translator Used'
      },
    ];

    this.yesNoOptions = [
      {
        label: 'Yes', value: 'yes'
      },
      {
        label: 'No', value: 'no'
      }
    ]
    this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
      this.generateNarrative();
    });
  }

  ngOnDestroy(): void {
    if (this.addressSub) {
      this.addressSub.unsubscribe();
    }
    if (this.dirtyCheckSub) {
      this.dirtyCheckSub.unsubscribe();
    }
  }

  initializeScreen() {
    this.caseService.getApsInvestigationConclusion().subscribe(res => {
      this.apsCaseManagementData = res;
      this.populateDropdownValues();
      this.setClosureReason();
      this.formValues = this.getForms();
      this.navigationService.setTitle('Case Closure');
      if (this.apsCaseManagementData?.stageCode === 'SVC') {
        this.createSvcForm();
      } else {
        this.createForm();
        this.toggleLEPSection();
      }
      this.showPendingStatusMessageAlert();
    });

  }

  setClosureReason() {
    let closureReasonCode = "";
    if(this.apsCaseManagementData?.stageCode === 'ARI'){
      closureReasonCode = this.apsCaseManagementData?.priorStage?.reasonClosed
    }else{
      closureReasonCode = this.apsCaseManagementData?.stage?.reasonClosed;
    }
    if (closureReasonCode && this.closureReasons) {
      this.closureReason = (this.closureReasons?.find((x: any) => x.code === closureReasonCode) as any)?.decode;
    }
  }

  populateDropdownValues() {
    this.closureReasons = this.apsCaseManagementData.closureReasons;
    this.counties = this.apsCaseManagementData.counties;
    this.states = this.apsCaseManagementData.states;
    this.programAdmins = this.apsCaseManagementData.programAdmins;
  }

  getServiceProvidedValue(closureTypeCode: string, indSvcPlan: string) {
    if(!this.apsCaseManagementData?.preSingleStage && this.apsCaseManagementData?.stageCode === 'INV') {
      return indSvcPlan === 'Y' ? 'Participated'
      : indSvcPlan === 'N' ? 'Not Participated'
        : null;
    } else {
      return closureTypeCode === 'CSP' ? 'Participated'
      : closureTypeCode === 'CNS' ? 'Not Participated'
        : null;
    }

  }

  getClientCommunication(value: string) {
    return value === 'CIP' ? 'Translator Used'
      : value === 'CNI' ? 'Translator Not Used'
        : null;
  }

  getVictimAdvised(value: string) {
    return value === 'CAC' ? 'Advised'
      : value === 'CNC' ? 'Not Advised'
        : null;
  }

  createSvcForm() {
    const data = this.apsCaseManagementData?.stage;
    this.svcCaseManagementform = this.formBuilder.group({
      reason: [data?.reasonClosed, [Validators.required]],
      date: [data?.lastUpdatedDate, [Validators.required]],
      alrernateResourceExploered: [data?.ecs === 'Y'],
      caseWorkerVerified: [data?.ecsVer === 'Y'],
      caseClosureAdvised: ['', [Validators.required]],
      comments: [data?.closureComments],
      advisedDate: [data?.clientAdvisedDate]
    });
  }

  createForm() {
    const { apsInvestigationDetailDto: data } = this.apsCaseManagementData;
    const entityInformation = this.apsCaseManagementData?.entityInformation;
    const address = entityInformation?.entityAddressDto;
    const agency = entityInformation?.entityDto;
    const phone = entityInformation?.entityPhoneDto;
    const name = entityInformation?.entityNameDto;
    const email = entityInformation?.entityEmailDto;

    const validatorsForEmrSection = this.apsCaseManagementData.reportableConductExists ? [Validators.required] : [];
    const validatorsForProgramAdminSection = this.apsCaseManagementData.preSingleStage && this.apsCaseManagementData.reportableConductExists ? [Validators.required] : [];
    const disabled = this.apsCaseManagementData.pageMode === 'VIEW';
    const rootCauseDisabled = disabled || !this.apsCaseManagementData?.hasValidSelfNeglect
    const idProgramAdmin = this.apsCaseManagementData?.apsInvestigationDetailDto?.idProgramAdminPerson ?
      String(this.apsCaseManagementData.apsInvestigationDetailDto.idProgramAdminPerson)
      :null;

    this.selfNeglectAccordionOpen = this.apsCaseManagementData?.selfNeglectSummaryExpanded;
    this.cjAccordionOpen = this.apsCaseManagementData?.conclusionJustificationExpanded

    this.caseManagementForm = this.formBuilder.group({
      closureReason: [{ value: this.apsCaseManagementData?.stageCode === 'ARI' ? this.apsCaseManagementData?.priorStage?.reasonClosed :this.apsCaseManagementData?.stage?.reasonClosed, disabled }],
      programAdmin: [{ value: idProgramAdmin, disabled },validatorsForProgramAdminSection],
      victimInformedofCaseClosure: [{ value: this.getVictimAdvised(data?.cdClosureType), disabled }],
      narrative: [{ value: data?.txtNotAdviCaseClosure, disabled }],
      clientAdvisedDate: [{ value: data?.dtClientAdvised, disabled }, [DfpsCommonValidators.validateDate, ApsCaseManagementValidator.validateFutureDate]],
      servicesProvided: [{ value: this.getServiceProvidedValue(data?.cdClosureType, data?.indSvcPlan), disabled }],
      servicesProvidedNarrative: [{ value: data?.txtNotPrtcptSvcPln, disabled }],
      descriptionOfTheAllegationsAndFinding: [{ value: data?.txtAllegFinding, disabled }],
      rootCause: [{ value: data?.txtRootCause, disabled }],
      descriptionOfTheAllegations: [{ value: data?.txtDescAllg, disabled }],
      analysisOfTheEvidence: [{ value: data?.txtAnlysEvidance, disabled }],
      preponderanceStatement: [{ value: data?.txtPrepondrncStmnt, disabled }],
      docExists: [{ value: data?.indExtDoc === 'Y', disabled }],
      legalActionsCompleted: [{ value: data?.indLegalAction === 'Y', disabled }],
      clientProvidedMaterial: [{ value: data?.indFamViolence === 'Y', disabled }],
      alernateResourceExplored: [{ value: data?.indEcs === 'Y', disabled }],
      verifiedClientGoods: [{ value: data?.indPcsSvcs === 'Y', disabled }],
      unableToCommunicateInEnglish: [{ value: ['CNI', 'CIP'].includes(data?.cdInterpreter), disabled }],
      client: [{ value: data?.indClient === 'Y', disabled }],
      otherName: [{ value: data?.txtClientOther, disabled }],
      clientCommunication: [{ value: this.getClientCommunication(data?.cdInterpreter), disabled }],
      clientMethodOfCommuincation: [{ value: data?.txtMethodComm, disabled }],
      translaterName: [{ value: data?.txtTrnsNameRlt, disabled }],
      alternateCommunicationMeans: [{ value: data?.txtAltComm, disabled }],
      nameOfAgency: [{ value: agency?.nmEntity, disabled }, validatorsForEmrSection],
      firstName: [{ value: name?.nmFirst, disabled }, validatorsForEmrSection],
      lastName: [{ value: name?.nmLast, disabled }, validatorsForEmrSection],
      middleName: [{ value: name?.nmMiddle, disabled }],
      phone: [{ value: FormUtils.formatPhoneNumber(phone?.nbrPhone.toString()) || '', disabled }, [...validatorsForEmrSection, ApsCaseManagementValidator.phoneNumberPattern]],
      email: [{ value: email?.email, disabled }, [ApsCaseManagementValidator.validateEmailAddress]],
      extension: [{ value: phone?.nbrPhoneExtension, disabled }, [ApsCaseManagementValidator.phoneNumberExtPattern]],
      address: this.formBuilder.group({
        addressLine0: [{ value: address?.addrStLn1, disabled }, validatorsForEmrSection],
        addressLine2: [{ value: address?.addrStLn2, disabled }],
        state: [{ value: address?.cdState || 'TX', disabled }, validatorsForEmrSection],
        county: [{ value: address?.cdCounty, disabled }, validatorsForEmrSection],
        zip: [{ value: address?.addrZip?.split('-')?.[0] || '', disabled }, [...validatorsForEmrSection, DfpsCommonValidators.zipPattern]],
        zipExt: [{ value: address?.addrZip?.split('-')?.[1] || '', disabled }, [DfpsCommonValidators.zipExtensionPattern]],
        city: [{ value: address?.addrCity, disabled }, [...validatorsForEmrSection, DfpsCommonValidators.cityPattern]]
      })
    });
    if(rootCauseDisabled){
      this.caseManagementForm?.controls?.rootCause.disable()
    }
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
     this.addressSub = this.caseManagementForm.get('address').valueChanges.subscribe(() => {
      this.isAddrValidated = false;
    });
    this.subscribeForDirtyCheck();
    this.subscribeFormEvents();
  }


  subscribeFormEvents() {
    this.caseManagementForm.get('victimInformedofCaseClosure').valueChanges.pipe(take(1)).subscribe(() => {
      this.clearCaseAndDeliveryError();
    });
    this.caseManagementForm.get('servicesProvided').valueChanges.pipe(take(1)).subscribe(() => {
      this.clearCaseAndDeliveryError();
    });
  }

  clearCaseAndDeliveryError() {
    if (this.validationErrors && this.validationErrors.length) {
      this.validationErrors =  this.validationErrors.filter((x) => x.fieldName !== 'MSG_APS_CASE_CLSR_DELIVERY_REQ');
    }
  }

  subscribeForDirtyCheck() {
    this.dirtyCheckSub?.unsubscribe();
    this.dirtyCheckSub = this.caseManagementForm.valueChanges.pipe(take(1)).subscribe(() => {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
    });
  }

  validateForm() {
    this.validateFormGroup(this.caseManagementForm);
    this.validateFormGroup(this.caseManagementForm.get('address') as FormGroup);
    this.clearCaseAndDeliveryError();
    return this.validationErrors.length > 0 ? false : true;
  }

  showEmrValidationModal() {
    const initialState = {
      title: 'Address Detail',
      message: 'Please Validate and Save the EMR information before sending it for review',
      showCancel: false
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(() => { });
  }

  saveAndSubmit() {
    this.updateDateClientAdvised();
    if (this.showSaveAndSubmitModal()) {
      return;
    }
    this.validateAndSubmit();
  }

  validateAndSubmit() {
    if (this.showClosureDropdown()) {
      this.addValidatorsToField('closureReason', [ApsCaseManagementValidator.requiredFieldonSaveandSubmit('closureReasonRequiredError')]);
    }
    this.addValidatorsToField('victimInformedofCaseClosure', [ApsCaseManagementValidator.requiredFieldWithHiddenFieldName('MSG_APS_CASE_CLSR_RSN_REQD')]);
    if (this.apsCaseManagementData?.hasValidSelfNeglect) {
      this.addValidatorsToField('rootCause', [ApsCaseManagementValidator.requiredFieldonSaveandSubmit('rootCauseRequiredError')]);
    }
    if (this.apsCaseManagementData?.selfNeglectAllegations?.length > 0) {
      this.addValidatorsToField('descriptionOfTheAllegationsAndFinding', [ApsCaseManagementValidator.requiredFieldonSaveandSubmit('allegationAndFindingError')]);
    }
    if (this.apsCaseManagementData?.apAllegations?.length > 0) {
      this.addValidatorsToField('descriptionOfTheAllegations', [ApsCaseManagementValidator.requiredFieldonSaveandSubmit('MSG_REQ_AP_ALLG')]);
      this.addValidatorsToField('analysisOfTheEvidence', [ApsCaseManagementValidator.requiredFieldonSaveandSubmit('MSG_REQ_AP_ALLG')]);
      this.addValidatorsToField('preponderanceStatement',[ApsCaseManagementValidator.requiredFieldonSaveandSubmit('MSG_REQ_AP_ALLG')]);
    }
    if (this.caseManagementForm.get('servicesProvided').value === 'Not Participated') {
      this.addValidatorsToField('servicesProvidedNarrative', [ApsCaseManagementValidator.requiredFieldWithHiddenFieldName('SERVICE_PROVIDED_NARRATOVE_REQ_MSG')]);
    }
    if (this.caseManagementForm.get('victimInformedofCaseClosure').value === 'Advised') {
      this.addValidatorsToField('clientAdvisedDate', [DfpsCommonValidators.validateDate, ApsCaseManagementValidator.validateFutureDate, ApsCaseManagementValidator.requiredFieldWithHiddenFieldName('DATE_ADVISED_REQ_MSG')]);
    }
    if (this.caseManagementForm.get('victimInformedofCaseClosure').value === 'Not Advised') {
      this.addValidatorsToField('narrative', [ApsCaseManagementValidator.requiredFieldWithHiddenFieldName('NARRATIVE_REQ_MSG')]);
    }
    if (this.validateForm()) {
      this.saveAndSubmitRequest();
    } else {
      this.clearCaseAndDeliveryError();
      if (!this.caseManagementForm.get('servicesProvided').value && !this.caseManagementForm.get('victimInformedofCaseClosure').value) {
        this.validationErrors.push({
          fieldName: 'MSG_APS_CASE_CLSR_DELIVERY_REQ',
          hideFieldName: true,
          errors: {
            'MSG_APS_CASE_CLSR_DELIVERY_REQ': true
          }
        });
      }
      this.clearValidators('victimInformedofCaseClosure');
      if (this.caseManagementForm.get('rootCause').errors || this.caseManagementForm.get('descriptionOfTheAllegationsAndFinding').errors) {
        this.selfNeglectAccordionOpen = true;
      }
      if (this.caseManagementForm.get('descriptionOfTheAllegations').errors ||
          this.caseManagementForm.get('analysisOfTheEvidence').errors ||
          this.caseManagementForm.get('preponderanceStatement').errors) {
            this.cjAccordionOpen = true;
          }
      this.clearValidators('closureReason');
      if (this.apsCaseManagementData.hasValidSelfNeglect) {
        this.clearValidators('rootCause');
      }
      if (this.apsCaseManagementData?.selfNeglectAllegations?.length > 0) {
        this.clearValidators('descriptionOfTheAllegationsAndFinding');
      }
      if (this.apsCaseManagementData?.apAllegations?.length > 0) {
        this.clearValidators('descriptionOfTheAllegations');
        this.clearValidators('analysisOfTheEvidence');
        this.clearValidators('preponderanceStatement');
      }
      this.clearValidators('narrative');
      this.clearValidators('servicesProvidedNarrative');
      this.addValidatorsToField('clientAdvisedDate', [DfpsCommonValidators.validateDate, ApsCaseManagementValidator.validateFutureDate]);
    }
  }

  addValidatorsToField(field: string, validators: ValidatorFn[]) {
    this.caseManagementForm.get(field).setValidators(validators);
    this.caseManagementForm.get(field).updateValueAndValidity({emitEvent: false});
  }

  clearValidators(field: string) {
    this.caseManagementForm.get(field).clearValidators();
    this.caseManagementForm.get(field).updateValueAndValidity({emitEvent: false});
  }

  saveAndSubmitRequest() {
    let payload = this.getSavePayload();
    payload = this.showLEPSection ? payload : this.clearELPSectionFormFields(payload);
    const buttonType = {saveAndSubmitClicked: true}
    payload = {...payload,...buttonType};
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.caseService.saveandSubmitApsInvestigationConclusion(payload).subscribe(res => {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      console.log('save and submit success', res);
      const eventTypeCode = 'TASK_APPROVAL';
      const eventId = this.apsCaseManagementData.apsInvestigationDetailDto.idEvent;
      const cacheKey = this.environmentSettings.environmentName === 'Local'
        ? this.cookieService.get('cacheKey') : localStorage.getItem('cacheKey');
      window.location.href = this.environmentSettings.impactP2WebUrl +
        '/case/caseManagement/displayToDoDetail?cacheKey=' + cacheKey + '&eventTypeCode=' + eventTypeCode
        + '&eventId=' + eventId;
    }, (err) => this.handleSaveError(err));
  }

  getUrlForError(errorCode: string): string {
    switch(errorCode) {
      case 'PERSON_LIST': {
        return `${this.environmentSettings.impactP2WebUrl}/case/personList?isMPSEnvironment=false&cacheKey=${this.getCacheKey()}`;
      }
      case 'EVENT_LIST': {
        return `${this.environmentSettings.impactP2WebUrl}/case/caseSummary/event/displayEventList/AUT?isMPSEnvironment=false&cacheKey=${this.getCacheKey()}`;
      }
      default:
        return '';
    }
  }

  getCacheKey() {
    return this.environmentSettings.environmentName === 'Local'
        ? this.cookieService.get('cacheKey') : localStorage.getItem('cacheKey');
  }

  handleSaveError(errResponse) {
    const errorsToShownInModal = [
      {
        error: 'Please enter the Reason for Death in the Person Detail.',
        code: 'PERSON_LIST'
      },
      {
        error: 'You must close all Service Authorizations before case can be closed.',
        code: 'EVENT_LIST'
      },
      {
        error: 'Person characteristics must be addressed for each principal.',
        code: 'PERSON_LIST'
      },
      {
        error: 'Each principal in the Investigation must have an entered or estimated DOB.',
        code: 'PERSON_LIST'
      },
      {
        error: 'A Person Search must be conducted on all MPS persons in the Stage',
        code: 'PERSON_LIST'
      }
    ];
    if (errResponse?.error?.errors?.length > 0 && errorsToShownInModal.some((x) => errResponse.error.errors.includes(x.error))) {
      // const messageList = errorsToShownInModal.filter((x) => errResponse.error.errors.includes(x.error)).map((x) => {
      //   return {
      //     content: x.error,
      //     url: this.getUrlForError(x.code)
      //   };
      // });
      const messageList = errResponse.error.errors.map((x) => {
        const errorDetails = errorsToShownInModal.find((error) => error.error === x);
        return {
          content: x,
          url: errorDetails ? this.getUrlForError(errorDetails.code) : ''
        };
      });
      if (messageList?.length > 0) {
        const initialState = {
          title: 'Error List',
          messageList,
          showCancel: false
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-lg modal-dialog-centered', initialState,
          ignoreBackdropClick: true,
          keyboard: false,
          backdrop: true
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(() => { });
      }
    }
  }

  save() {
    this.updateDateClientAdvised();
    let payload = this.getSavePayload();
    payload = this.showLEPSection ? payload : this.clearELPSectionFormFields(payload);
    const buttonType = {saveClicked: true}
    payload = {...payload,...buttonType};
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    if (this.validateForm()) {
      this.caseService.saveApsInvestigationConclusion(payload).subscribe(() => {
        window.scrollTo(0, 0);
        const params = this.activatedRoute.snapshot.queryParams;
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
          this.router.navigate(['/case/caseManagement/aps-case-management/case-conclusion'], {queryParams: params}
        ));
      });
    }
  }

  clearELPSectionFormFields(payload) {
    const apsInvestigationDetailDto = {
      ...payload?.apsInvestigationDetailDto, 
      ...{ 
        txtMethodComm: '', 
        txtTrnsNameRlt: '', 
        txtAltComm: '', 
        txtClientOther: '', 
        indClient: 'N', 
        cdInterpreter: ''
      }
    }

    return { 
      ...payload, 
      ...{ 
        apsInvestigationDetailDto
      }
    }
  }

  getSavePayload() {
    const formValue = this.caseManagementForm.value;
    const payload = {
      entityInformation: this.getEntitytInformationPayload(formValue),
      apsInvestigationDetailDto: this.getInvestigationDetailsDtoPayload(formValue),
      addressValidated: this.isAddrValidated,
      stageId: this.apsCaseManagementData?.stageId,
      caseId: this.apsCaseManagementData?.caseId,
      closureReasonCd: this.getCurrentClosureCode(formValue),
      preSingleStage:this.apsCaseManagementData?.preSingleStage,
      overallDispositionDecode:this.apsCaseManagementData?.overallDisposition?.decode,
      stageType:this.apsCaseManagementData?.stage?.type,
      missingDisposition:this.apsCaseManagementData?.missingDisposition,
      validApAllegation:this.apsCaseManagementData?.hasValidApAllegation,
      validSelfNeglect:this.apsCaseManagementData?.hasValidSelfNeglect,
      selfNeglectAllegations:this.apsCaseManagementData?.selfNeglectAllegations,
      apAllegations:this.apsCaseManagementData?.apAllegations,
      reportableConductExists:this.apsCaseManagementData?.reportableConductExists,
      cdServiceType:this.getServicesProvidedCode(formValue.servicesProvided),
      showLEPSection:this.showLEPSection,
      docExists:this.apsCaseManagementData?.docExists,
      allegPerpRoles:this.apsCaseManagementData?.allegPerpRoles,
      eventStatus: this.apsCaseManagementData?.event?.status,
      selfNeglectSummaryExpanded: this.selfNeglectAccordionOpen,
      conclusionJustificationExpanded: this.cjAccordionOpen
    }
    return payload;
  }

  sendForReview() {
    if (this.caseManagementForm.dirty) {
      this.showEmrValidationModal();
      return;
    }
    let payload = {...this.getSavePayload(), sendForReviewClicked: true, saveClicked: !this.caseManagementForm.dirty } ;
    if (this.validateForm()) {
      this.caseService.sendForReviewApsInvestigationConclusion(payload).subscribe(res => {
        console.log('send for review success', res);
        const eventTypeCode = 'EMR_APPROVAL';
        //const eventId = this.apsCaseManagementData.apsInvestigationDetailDto.idEvent;
        const eventId = res?.eventId;
        const cacheKey = this.environmentSettings.environmentName === 'Local'
          ? this.cookieService.get('cacheKey') : localStorage.getItem('cacheKey');
        window.location.href = this.environmentSettings.impactP2WebUrl +
          '/case/caseManagement/displayToDoDetail?cacheKey=' + cacheKey + '&eventTypeCode=' + eventTypeCode
          + '&eventId=' + eventId;

      });
    }
  }

  getInvestigationDetailsDtoPayload(formValue) {
    const apsInvestigationDto = this.apsCaseManagementData?.apsInvestigationDetailDto || {};
    // let clientAdvisedDate = formValue.clientAdvisedDate;
    // if (formValue.victimInformedofCaseClosure === 'Not Advised') {
    //   this.caseManagementForm.patchValue({
    //     clientAdvisedDate: ''
    //   }, { emitEvent: false});
    //   clientAdvisedDate = '';
    // }
    return {
      ...apsInvestigationDto,
      dtClientAdvised: formValue.clientAdvisedDate,
      txtNotAdviCaseClosure: formValue.narrative,
      indSvcPlan: formValue.servicesProvided === 'Participated' ? 'Y' : formValue.servicesProvided === 'Not Participated' ? 'N' : null,
      txtNotPrtcptSvcPln: formValue.servicesProvidedNarrative,
      txtAllegFinding: formValue.descriptionOfTheAllegationsAndFinding,
      txtRootCause: formValue.rootCause,
      txtDescAllg: formValue.descriptionOfTheAllegations,
      txtAnlysEvidance: formValue.analysisOfTheEvidence,
      txtPrepondrncStmnt: formValue.preponderanceStatement,
      indExtDoc: formValue.docExists === true ? 'Y' : 'N',
      indLegalAction: formValue.legalActionsCompleted === true ? 'Y' : 'N',
      indFamViolence: formValue.clientProvidedMaterial === true ? 'Y' : 'N',
      indEcs: formValue.alernateResourceExplored === true ? 'Y' : 'N',
      indClient: formValue.client === true ? 'Y' : 'N',
      indPcsSvcs: formValue.verifiedClientGoods === true ? 'Y' : 'N',
      txtClientOther: formValue.otherName,
      cdInterpreter: this.getClientCommunicationCode(formValue.clientCommunication),
      txtMethodComm: formValue.clientMethodOfCommuincation,
      txtTrnsNameRlt: formValue.translaterName,
      txtAltComm: formValue.alternateCommunicationMeans,
      cdClosureType:this.getVictimAdvisedCode(formValue.victimInformedofCaseClosure),
      idProgramAdminPerson:formValue.programAdmin
    };
  }

  getVictimAdvisedCode(value: string) {
    return value === 'Advised' ? 'CAC'
      : value === 'Not Advised' ? 'CNC'
        : '';
  }

  getServicesProvidedCode(value: string) {
    return value === 'Participated' ? 'CSP'
      : value === 'Not Participated' ? 'CNS'
        : '';
  }

  getClientCommunicationCode(value: string) {
    return value === 'Translator Used' ? 'CIP'
      : value === 'Translator Not Used' ? 'CNI'
        : '';
  }

  getEntitytInformationPayload(formValue) {
    const entityInformation = this.apsCaseManagementData?.entityInformation || {};
    const addressForm: any = this.caseManagementForm.controls.address;
    return {
      ...entityInformation,
      entityAddressDto: {
        ...entityInformation?.entityAddressDto || {},
        addrStLn1: addressForm.controls.addressLine0.value,
        addrStLn2: addressForm.controls.addressLine2.value,
        cdState: addressForm.controls.state.value,
        cdCounty: addressForm.controls.county.value,
        addrZip: `${addressForm.controls.zip.value}-${addressForm.controls.zipExt.value}`,
        addrCity: addressForm.controls.city.value
      },
      entityDto: {
        ...entityInformation?.entityDto || {},
        nmEntity: formValue.nameOfAgency
      },
      entityEmailDto: {
        ...entityInformation?.entityEmailDto || {},
        email: formValue.email
      },
      entityNameDto: {
        ...entityInformation?.entityNameDto || {},
        nmFirst: formValue.firstName,
        nmLast: formValue.lastName,
        nmMiddle: formValue.middleName
      },
      entityPhoneDto: {
        ...entityInformation?.entityPhoneDto || {},
        nbrPhone: Number.parseFloat((formValue.phone.replace(/[^\d]/g, ''))),
        nbrPhoneExtension: formValue.extension
      }
    };
  }

  getCurrentClosureCode(formValue) {
    return formValue.closureReason;
  }

  getCurrentProgramAdmin(formValue) {
    return formValue.programAdmin;
  }

  approvalStatus() {
    let payload = this.getSavePayload();
    const buttonType = {approvalClicked: true}
    payload = {...payload,...buttonType}
    this.caseService.approvalStatus(payload).subscribe(res => {
      console.log('send for review success', res);
      const cacheKey = this.environmentSettings.environmentName === 'Local'
        ? this.cookieService.get('cacheKey') : localStorage.getItem('cacheKey');
      window.location.href = this.environmentSettings.impactP2WebUrl +
        '/case/approval-status/display?cacheKey=' + cacheKey;
    })
  }

  toggleLEPSection() {
    const outputCompletedValue = this.caseManagementForm.get('unableToCommunicateInEnglish').value;
    this.showLEPSection = outputCompletedValue || this.apsCaseManagementData.pageMode === 'VIEW' ? true : false;
  }

  validateAdressFields() {
    const addressForm = this.caseManagementForm.get('address') as FormGroup;
    const address: Address = {
      street1: addressForm.controls.addressLine0.value,
      street2: addressForm.controls.addressLine2.value,
      city: addressForm.controls.city.value,
      county: addressForm.controls.county.value,
      state: addressForm.controls.state.value,
      zip: addressForm.controls.zip.value,
      extension: addressForm.controls.zipExt.value,
    };
    this.dfpsAddressValidator.validate(address);
  }

  updateAdressFields(address: Address) {
    if (address) {
      if (address.isAddressAccepted) {
        this.caseManagementForm.get('address').patchValue({
          addressLine0: address.street1,
          addressLine2: address.street2,
          city: address.city,
          county: address.countyCode,
          state: address.state,
          zip: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[0] : '') : '',
          zipExt: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[1] : '') : '',
        });
        this.isAddrValidated = true;
        // this.caseManagementForm.get('address').markAsPristine();
      } else {
        if (this.caseManagementForm.get('address.zip').value &&
          this.caseManagementForm.get('address.zip').value.length !== 5) {
          this.caseManagementForm.get('address').patchValue({
            zip: '',
            zipExt: ''
          });
        }

        if (this.caseManagementForm.get('address.zipExt').value &&
          this.caseManagementForm.get('address.zipExt').value.length !== 4) {
          this.caseManagementForm.get('address').patchValue({
            zipExt: ''
          });
        }
      }
    }
  }

  getForms(): FormValue[] {
    return [{
      formName: 'APS Case Closure',
      formParams: {
        docType: 'cfiv1200',
        docExists: 'false',
        protectDocument: 'true',
        displayOnly: 'true',
        checkForNewMode: 'false',
        idStage: this.apsCaseManagementData.formTagDto.pStage ?
          String(this.apsCaseManagementData.formTagDto.pStage) : '',

      }
    }
    ];
  }

  getNarrativeData() {
    return {
      docType: 'APSINVNARR',
      protectDocument: (this.apsCaseManagementData?.pageMode !== 'EDIT'),
      sEvent:this.apsCaseManagementData.apsInvestigationDetailDto.idEvent ,
      sCase:this.apsCaseManagementData.apsInvestigationDetailDto.idCase ,
      sTimestamp: this.apsCaseManagementData.apsInvestigationDetailDto?.dtLastUpdate ?
        String(this.apsCaseManagementData.apsInvestigationDetailDto.dtLastUpdate) : ''
    };
  }

  generateNarrative() {
      const formDto: any = this.getNarrativeData();
      this.narrativeService.callNarrativeService(formDto);
  }

  showPendingStatusMessageAlert() {
    const { popupMessages } = this.apsCaseManagementData;
    if (popupMessages?.length > 0) {
      const initialState = {
        messageList: popupMessages.map((x) => {
          return {
            content: x,
            url: ''
          }
        }),
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered', initialState,
        ignoreBackdropClick: true,
        keyboard: false,
        backdrop: true
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {

      });
    }
  }

  showClosureDropdown() {
    return this.apsCaseManagementData?.overallDisposition &&
      this.apsCaseManagementData.pageMode !== 'VIEW' ? true : false;
  }

  showAneServicesModal() {
    if (this.apsCaseManagementData?.snaExists) {
      const initialState = {
        message: `All required ANE services have been added for valid allegations.
                  Select OK to continue, or Cancel to add more services now.`,
        showCancel: true
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered', initialState,
        ignoreBackdropClick: true,
        keyboard: false,
        backdrop: true
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((res) => {
        if (res) {
          this.validateAndSubmit();
        }
      });
    } else {
      this.validateAndSubmit();
    }
  }

  showSaveAndSubmitModal() {
    if (this.caseManagementForm.get('closureReason').value === '99' || this.apsCaseManagementData?.snaExists) {
      this.showClosureReasonModal();
      return true;
    }
    return false;
  }

  showClosureReasonModal() {
    if (this.caseManagementForm.get('closureReason').value === '99') {
      const initialState = {
        message: `You must create a new call from Call ID ${this.apsCaseManagementData?.priorIntakeStage?.stageId}`,
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered', initialState,
        ignoreBackdropClick: true,
        keyboard: false,
        backdrop: true
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((res) => {
        this.showAneServicesModal();
      });
    }
    else {
      this.showAneServicesModal();
    }
  }

  updateStatusOfSelfNeglectAccordion(el) {
    this.selfNeglectAccordionOpen = el;
  }

  updateStatusOfConclusionJustificationAccordion(el) {
    this.cjAccordionOpen = el;
  }

  updateDateClientAdvised() {
    const formValue = this.caseManagementForm.value;
    if (formValue.victimInformedofCaseClosure === 'Not Advised') {
      this.caseManagementForm.patchValue({
        clientAdvisedDate: ''
      }, { emitEvent: false});
    }
  }
}
