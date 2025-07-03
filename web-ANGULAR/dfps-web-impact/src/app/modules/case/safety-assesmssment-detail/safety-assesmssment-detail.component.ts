import { Component, HostListener, Inject, OnDestroy, OnInit, forwardRef } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators, NG_VALUE_ACCESSOR, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsConfirmComponent,
  DfpsCommonValidators,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormService,
  FormValue,
  NavigationService,
  SET,
  DataTable,
  NarrativeService,
  ENVIRONMENT_SETTINGS
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CaseService } from '../service/case.service';
import { Subscription } from 'rxjs';
import { SearchService } from '@shared/service/search.service';
import { DisplayApsSafetyAssessmentDetails } from '@case/model/apsSafetyAssessmentList';
import { environment } from "../../../../environments/environment";
import { SafetyAssessmentValidators } from './safety-assesmssment-detail.validator';
import { HelpService } from '../../../common/impact-help.service';

@Component({
  selector: 'app-safety-assesmssment-detail',
  templateUrl: './safety-assesmssment-detail.component.html',
  styleUrls: ['./safety-assesmssment-detail.component.css']
})
export class SafetyAssesmssmentDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  eventId: any;
  tableBody: any[];
  formValues: FormValue[];
  tableColumn: any[];
  safetyResponses: any = [];
  immediateInterventionSources: any = [];
  incompleteSectionMessages: any = [];
  disbaleCaretakersInfo = false;
  displayContactStandardsSection = true;
  getData: any = [];
  safetyDecision: any[];
  isCaretakerSelected: boolean;
  timeTypeList: any = [];
  selectedSrcId: any;
  selectedPrblmId: any;
  isF2FExpanded = false;
  isCIExpanded = false;
  safetyAssmntDetailsForm: FormGroup;
  displaySafetyAssmntDetails: DisplayApsSafetyAssessmentDetails;
  caretakerListDataTable: DataTable;
  narrativeSubscription: Subscription;
  formsSubscription: Subscription;
  environment: string;
  isEditMode = false;
  updatedNumberOfFaceToFaceContacts: any;
  updatedNumberOfContactsReqd: any;
  isSDExpanded = false;
  hideSelectStaffBtn: boolean;
  isMPSEnvironment = false;
  notApplicableSelected: any = [];
  decreasePrioritySelected: any = [];
  increasePrioritySelected: any = [];

  constructor(private navigationService: NavigationService, private modalService: BsModalService, private searchService: SearchService,
    private narrativeService: NarrativeService,
    private formService: FormService,
    private fb: FormBuilder, private caseService: CaseService, private route: ActivatedRoute,
    private router: Router,
    private helpService: HelpService,
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
    this.navigationService.setTitle('APS Safety Assessment Page');
    this.helpService.loadHelp('Assess');
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.eventId = routeParams.get('eventId');
    }
    this.initializeScreen();
    this.safetyAssmntDetailsForm.get('apsServicePlanDto.assessmentMonitoringPlan.numberOfFaceToFaceContacts')
      .valueChanges.subscribe((val) => {
        this.updatedNumberOfFaceToFaceContacts = val;
      });
    this.safetyAssmntDetailsForm.get('apsServicePlanDto.assessmentMonitoringPlan.numberOfContactsReqd').valueChanges.subscribe((val) => {
      this.updatedNumberOfContactsReqd = val;
    });
    this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
      this.generateNarrative();
      this.generateContactNarrative();
    });
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
    this.isMPSEnvironment = 'MPS' === this.environmentSettings.environmentName.toUpperCase();
  }

  loadFadDataTable() {
    this.tableColumn = [
      { field: 'fullName', header: 'Name', sortable: false, width: 100 },
      { field: 'personType', header: 'Type', sortable: false, width: 150 },
      { field: 'role', header: 'Role', sortable: false, width: 100 },
      { field: 'relInt', header: 'Relation/Interest', sortable: false, width: 100 }
    ];
    this.caretakerListDataTable = {
      tableBody: this.displaySafetyAssmntDetails.caretakerList,
      tableColumn: this.tableColumn,
      isMultiSelect: true,
      selectedRows: [],
      isPaginator: (this.displaySafetyAssmntDetails && this.displaySafetyAssmntDetails.caretakerList &&
        this.displaySafetyAssmntDetails.caretakerList.length) > 10 ? true : false
    };
    this.caretakerListDataTable.selectedRows = this.displaySafetyAssmntDetails.caretakerList.filter(value => value.isCaretakerSelected);
    if (this.displaySafetyAssmntDetails.pageMode === 'EDIT') {
      if (this.caretakerListDataTable.selectedRows?.length > 0) {
        this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCaretakerNotApplicable').disable();
      }
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCaretakerNotApplicable) {
        this.disbaleCaretakersInfo = true;
      }
    }
  }

  createForm() {
    this.safetyAssmntDetailsForm = this.fb.group({
      apsSafetyAssessmentDto: this.fb.group({
        apsSaAssmtLookupId: [''],
        assmtCompletedDate: [''],
        assmtTypeCode: [''],
        createdDate: [''],
        createdPersonId: [''],
        createdPersonName: [''],
        indReferralRequired: [''],
        indInterventionsInPlace: [''],
        indCiImmediateIntervention: [''],
        indCaretakerNotApplicable: [''],
        currentPriority: [''],
        indCiCompleted: [''],
        priorityComments: [''],
        lastUpdatedDate: [''],
        lastUpdatedPersonId: [''],
        safetyDecisionCode: [''],
        eventStatus: [''],
        updatedPersonName: [''],
        versionNumber: [''],
        initialPriority: [''],
        id: ['']
      }),
      safetyResponses: this.fb.array([]),
      apsSafetyContacts: this.fb.array([]),
      pageTitle: [''],
      isSaveClicked: [false],
      section3Dirty: [false],
      saveAndCompleteClicked: [false],
      saveAndContinueClicked: [false],
      selectedCaretakerList: [],
      apsServicePlanDto: this.fb.group({
        createActionsForMultiPrblm: this.fb.group({
          actionDescription: [''],
          actionCategoryCode: [''],
          actionResultsCode: ['']
        }),
        actionOutcomeCodes: this.fb.array([]),
        actionCategoryCodes: this.fb.array([]),
        actionResultCodes: this.fb.array([]),
        activeMonitoringPlan: this.fb.group({
          apsSaId: [''],
          apsSpId: [''],
          createdPersonId: [''],
          id: [''],
          lastUpdatedPersonId: [''],
          numberOfContactsReqd: [''],
          numberOfFaceToFaceContacts: [''],
          planDescription: [''],
          planEndDate: [''],
          planSourceCode: [''],
          planStartDate: ['']
        }),
        allegData: this.fb.array([]),
        assessmentMonitoringPlan: this.fb.group({
          apsSaId: [''],
          apsSpId: [''],
          createdPersonId: [''],
          id: [''],
          lastUpdatedPersonId: [''],
          numberOfContactsReqd: [''],
          numberOfFaceToFaceContacts: [''],
          planDescription: [''],
          planEndDate: [''],
          planSourceCode: [''],
          planStartDate: ['']
        }),
        eventId: [''],
        id: [''],
        immediateInterventionSources: this.fb.array([]),
        selectedIISrc: [''],
        monitoringPlanList: this.fb.array([]),
        servicePlanSources: this.fb.array([]),
        snaIcsData: this.fb.array([]),
        servicePlanEventId: [''],
        caseId: [''],
        actionBtnClickedFromSection: ['']
      }),
      immedIntervenSectionComplete: [''],
      eventId: [''],
      pageMode: [''],
      caseName: [''],
      caseId: [''],
      iisectionExpanded: [false],
      safetyDecisionExpanded: [false],
      buttonClicked: [''],
      isSafetyInProcExists: [false]      
      
    }, {
      validators: [
        SafetyAssessmentValidators.checkIfSafetyAsmProcExists
      ]
    });
  }

  private actionCategoryCodesGrp(item: any): FormGroup {
    return this.fb.group({
      code: item.code,
      codeType: item.codeType,
      dateEnded: item.dateEnded,
      decode: item.decode,
      nbrOrder: item.nbrOrder
    })
  }

  private actionOutcomeCodesGrp(item: any): FormGroup {
    return this.fb.group({
      code: item.code,
      codeType: item.codeType,
      dateEnded: item.dateEnded,
      decode: item.decode,
      nbrOrder: item.nbrOrder
    })
  }

  private actionResultCodesGrp(item: any): FormGroup {
    return this.fb.group({
      code: item.code,
      codeType: item.codeType,
      dateEnded: item.dateEnded,
      decode: item.decode,
      nbrOrder: item.nbrOrder
    })
  }

  private servicePlanSourcesGrp(code: any): FormGroup {
    return this.fb.group({
      allegationId: code.allegationId,
      apsSaId: code.apsSaId,
      apsSaResponseId: code.apsSaResponseId,
      apsServicePlanSourceLabelDtoList: code.apsServicePlanSourceLabelDtoList,
      apsSnaId: code.apsSnaId,
      apsSnaResponseId: code.apsSnaResponseId,
      apsSpId: code.apsSpId,
      createdPersonId: code.createdPersonId,
      id: code.id,
      lastUpdatedPersonId: code.lastUpdatedPersonId,
      selected: code.selected,
      serviceProblems: this.fb.array(this.getServiceProblemsGrp(code.serviceProblems)),
      sourceCode: code.sourceCode,
      sourceCodeDecode: code.sourceCodeDecode,
      sourceText: code.sourceText,
      sourceType: code.sourceType
    })
  }

  getServiceProblemsGrp(serviceProblems: any) {
    return serviceProblems.map(code => this.fb.group({
      id: code.id,
      selected: code.selected,
      problemDescription: code.problemDescription,
      actions: this.fb.array(this.getActionsGrp(code.actions)),
      apsSpServiceSrcId: code.apsSpServiceSrcId,
      outcomeCode: code.outcomeCode
    }));
  }

  getActionsGrp(actions: any) {
    return actions.map(code => this.fb.group({
      actionCategoryCode: code.actionCategoryCode,
      actionDescription: code.actionDescription,
      actionResultsCode: code.actionResultsCode,
      apsSpActionId: code.apsSpActionId,
      apsSpId: code.apsSpId,
      apsSpProblemActionLinkId: code.apsSpProblemActionLinkId,
      selected: code.selected
    }));
  }

  private apsSafetyContactsGrp(code: any, index: any): FormGroup {
    return this.fb.group({
      eventId: code.eventId,
      contactOccurredDate: [code.contactOccurredDate],
      contactOccurredTime: [code.contactOccurredTime],
      indContactAttempted: code.indContactAttempted,
      contactAttempted: code.contactAttempted,
      contactTypeCode: code.contactTypeCode,
      contactType: code.contactType,
      contactWorkerFullName: code.contactWorkerFullName,
      contactWorkerId: code.contactWorkerId,
      contactEventDto: code.contactEventDto,
      contactMethodCode: code.contactMethodCode
    })
  }

  private safetyDetailsFormGrp(code: any): FormGroup {
    return this.fb.group({
      apsSaResponseId: code.apsSaResponseId,
      nmDefinition: code.nmDefinition,
      questionCode: code.questionCode,
      questionText: code.questionText,
      sectionCode: code.sectionCode,
      answerCode: code.answerCode,
      responseCode: code.responseCode,
      apsSaQuestionLookupId: code.apsSaQuestionLookupId
    });
  }



  launchForm(data: any) {
    if (data) {
      const { pageMode, eventId } = this.displaySafetyAssmntDetails;
      if ((pageMode === 'NEW' && !eventId) || (pageMode === 'EDIT' && this.safetyAssmntDetailsForm.dirty)) {
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

  initializeScreen() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.createForm();
    this.environment = this.environmentSettings.environmentName === 'Local' ? '' : '/impact3';
    this.caseService.getApsSafetyAssmntDetails(this.eventId).subscribe(res => {
      this.displaySafetyAssmntDetails = res;
      if (((this.displaySafetyAssmntDetails.incompleteSectionMessages && (this.displaySafetyAssmntDetails.incompleteSectionMessages.length > 0))
        || (this.displaySafetyAssmntDetails.informationalMessages && this.displaySafetyAssmntDetails.informationalMessages.length > 0))) {
        this.shiftFocusToValidationErrors();
      }
      this.displayContactStandards();
      this.updatedNumberOfFaceToFaceContacts = this.safetyAssmntDetailsForm.
        get('apsServicePlanDto.assessmentMonitoringPlan.numberOfFaceToFaceContacts').value;
      this.updatedNumberOfContactsReqd = this.safetyAssmntDetailsForm.
        get('apsServicePlanDto.assessmentMonitoringPlan.numberOfContactsReqd').value;
      if (this.displaySafetyAssmntDetails.safetyResponses) {
        this.displaySafetyAssmntDetails.safetyResponses.forEach((code) => {
          (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).push(
            this.safetyDetailsFormGrp(code)
          );
        });
      }

      if (this.displaySafetyAssmntDetails.apsSafetyContacts) {
        this.displaySafetyAssmntDetails.apsSafetyContacts.forEach((code, index) => {
          (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray).push(
            this.apsSafetyContactsGrp(code, index)
          );
          const contactOccurredDate = (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
            .at(index).get('contactOccurredDate');
          const contactOccurredTime = (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
            .at(index).get('contactOccurredTime');
          if (code.contactType === 'CI') {
            contactOccurredDate.setValidators(
              [Validators.required, DfpsCommonValidators.validateDate, SafetyAssessmentValidators.validateFutureDate]);
            contactOccurredTime.setValidators(
              [Validators.required, SafetyAssessmentValidators.validateTimeFormat]);
            contactOccurredDate.updateValueAndValidity();
            contactOccurredTime.updateValueAndValidity();
          } else if (code.contactType === 'F2F' && !code.contactAttempted) {
            if (code.contactOccurredDate) {
              contactOccurredDate.setValidators(
                [DfpsCommonValidators.validateDate, SafetyAssessmentValidators.validateFutureDate]);
              contactOccurredTime.setValidators(
                [Validators.required, SafetyAssessmentValidators.validateTimeFormat]);
              (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
                .at(index).get('contactOccurredDate').updateValueAndValidity();
              (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
                .at(index).get('contactOccurredTime').updateValueAndValidity();
            } else {
              (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
                .at(index).get('contactOccurredTime').clearValidators();
              (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
                .at(index).get('contactOccurredTime').updateValueAndValidity();
            }
          } else if (code.contactType === 'F2F' && code.contactAttempted) {
            if (code.contactOccurredDate) {
              contactOccurredTime.setValidators(
                [Validators.required, SafetyAssessmentValidators.validateTimeFormat]);
              contactOccurredDate.setValidators(
                [DfpsCommonValidators.validateDate, SafetyAssessmentValidators.validateFutureDate]);
              contactOccurredDate.updateValueAndValidity();
              contactOccurredTime.updateValueAndValidity();
            }
          }
        });
      }

      if (this.displaySafetyAssmntDetails.apsServicePlanDto) {
        this.displaySafetyAssmntDetails.apsServicePlanDto.actionResultCodes.forEach((code, index) => {
          (this.safetyAssmntDetailsForm.get('apsServicePlanDto.actionResultCodes') as FormArray).push(
            this.actionResultCodesGrp(code)
          );
        })
      }

      if (this.displaySafetyAssmntDetails.apsServicePlanDto) {
        this.displaySafetyAssmntDetails.apsServicePlanDto.actionCategoryCodes.forEach((code, index) => {
          (this.safetyAssmntDetailsForm.get('apsServicePlanDto.actionCategoryCodes') as FormArray).push(
            this.actionCategoryCodesGrp(code)
          );
        })
      }

      if (this.displaySafetyAssmntDetails.apsServicePlanDto) {
        this.displaySafetyAssmntDetails.apsServicePlanDto.actionOutcomeCodes.forEach((code, index) => {
          (this.safetyAssmntDetailsForm.get('apsServicePlanDto.actionOutcomeCodes') as FormArray).push(
            this.actionOutcomeCodesGrp(code)
          );
        })
      }

      if (this.displaySafetyAssmntDetails.apsServicePlanDto &&
        this.displaySafetyAssmntDetails.apsServicePlanDto.immediateInterventionSources) {
        this.displaySafetyAssmntDetails.apsServicePlanDto.immediateInterventionSources.forEach(item => {
          (this.safetyAssmntDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).push(
            this.servicePlanSourcesGrp(item)
          );
        });
      }
      this.loadFadDataTable();
      this.setForm();
      this.populatePullBackData();
      this.isPageChanged();
      this.disableCaseInitiationSection();
      this.chkIntakePrtyBoundaryValue();
      this.hideSections();
      this.chkPrtySelection();
      this.hideSelectStaff();
      this.getPageMode();
      this.formValues = this.getForms();
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
    this.getData = [
      { label: 'Yes', value: true },
      { label: 'No', value: false },
    ];
    this.safetyDecision = [{ label: 'Yes', value: true },
    { label: 'No', value: false },]
    this.timeTypeList = [
      { code: 'AM', decode: 'AM' }, { code: 'PM', decode: 'PM' }
    ]
  }

  getPageMode() {
    if (this.displaySafetyAssmntDetails.pageMode === 'NEW') {
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCaretakerNotApplicable').enable();
      this.disbaleCaretakersInfo = false;
      this.isEditMode = true;
    }
    else if (this.displaySafetyAssmntDetails.pageMode === 'EDIT') {
      this.isEditMode = true;
    } else if (this.displaySafetyAssmntDetails.pageMode === 'VIEW') {
      this.isEditMode = false;
      this.disbaleCaretakersInfo = true;
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCaretakerNotApplicable').disable();
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.currentPriority').disable();
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.priorityComments').disable();
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCiImmediateIntervention').disable();
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indInterventionsInPlace').disable();
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indReferralRequired').disable();
      this.safetyAssmntDetailsForm.get('apsServicePlanDto.assessmentMonitoringPlan.numberOfFaceToFaceContacts').disable();
      this.safetyAssmntDetailsForm.get('apsServicePlanDto.assessmentMonitoringPlan.numberOfContactsReqd').disable();
      this.safetyAssmntDetailsForm.get('apsServicePlanDto.assessmentMonitoringPlan.planDescription').disable();
      (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray).controls.forEach((val) => {
        val.get('contactOccurredDate').disable();
        val.get('contactOccurredTime').disable();
        val.get('contactAttempted').disable();
      });
      (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
        val.get('responseCode').disable();
      });
    }
  }

  setForm() {
    if (this.displaySafetyAssmntDetails) {
      this.safetyAssmntDetailsForm.patchValue({
        eventId: this.displaySafetyAssmntDetails.eventId ? this.displaySafetyAssmntDetails.eventId : '',
        apsSafetyAssessmentDto: this.displaySafetyAssmntDetails.apsSafetyAssessmentDto,
        pageTitle: this.displaySafetyAssmntDetails.pageTitle ? this.displaySafetyAssmntDetails.pageTitle : '',
        apsServicePlanDto: this.displaySafetyAssmntDetails.apsServicePlanDto ?
          this.displaySafetyAssmntDetails.apsServicePlanDto : null,
        safetyResponses: this.displaySafetyAssmntDetails.safetyResponses ?
          this.displaySafetyAssmntDetails.safetyResponses : [],
        immediateInterventionSources: this.displaySafetyAssmntDetails.apsServicePlanDto ?
          this.displaySafetyAssmntDetails.apsServicePlanDto.immediateInterventionSources : null,
        immedIntervenSectionComplete: this.displaySafetyAssmntDetails.immedIntervenSectionComplete,
        pageMode: this.displaySafetyAssmntDetails.pageMode,
        caseName: this.displaySafetyAssmntDetails.caseName ? this.displaySafetyAssmntDetails.caseName : '',
        caseId: this.displaySafetyAssmntDetails.caseId ? this.displaySafetyAssmntDetails.caseId : '',
        selectedCaretakerList: this.caretakerListDataTable?.selectedRows ? this.caretakerListDataTable.selectedRows : [],
        safetyDecisionExpanded: this.displaySafetyAssmntDetails.safetyDecisionExpanded,
        iisectionExpanded: this.displaySafetyAssmntDetails.iisectionExpanded
      })
    }
  };

  updateStatusOfSDSection(el) {
    this.safetyAssmntDetailsForm.patchValue({
      safetyDecisionExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  updateStatusOfIISection(el) {
    this.safetyAssmntDetailsForm.patchValue({
      iisectionExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  // staff search navighation
  staffSearchNavPath() {
    const returnUrl = 'case/safety-assessment-detail/' + this.eventId;
    this.searchService.setReturnUrl(returnUrl);
    this.router.navigate(['case/safety-assessment-detail/' + this.eventId + '/staffsearch'],
      { skipLocationChange: false });
  }

  // populate the staff search data
  populatePullBackData() {
    if (this.searchService.getSelectedStaff()) {
      this.safetyAssmntDetailsForm.get('apsSafetyContacts').value.forEach((code, index) => {
        if (code.contactType === 'CI') {
          (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray).at(index).patchValue({
            contactWorkerFullName: this.searchService.getSelectedStaff().name,
            contactWorkerId: this.searchService.getSelectedStaff().personId
          })
        }
      });
    }
    this.searchService.setSelectedStaff(null);
  }

  // Caretaker List enable/disable
  noCaretakerSelected() {
    if (this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCaretakerNotApplicable').value) {
      this.caretakerListDataTable.selectedRows = [];
      this.disbaleCaretakersInfo = true;
    } else {
      this.disbaleCaretakersInfo = false;
    }
  }

  getSelectedCaretaker(event) {
    this.caretakerListDataTable.selectedRows.forEach((e) => {
      e.isCaretakerSelected = true
    })
    this.safetyAssmntDetailsForm.patchValue({
      selectedCaretakerList: this.caretakerListDataTable.selectedRows
    })
    if (this.caretakerListDataTable.selectedRows.length > 0) {
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCaretakerNotApplicable').disable();
    } else {
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCaretakerNotApplicable').enable();
    }
  }

  displayContactStandards() {
    if (this.displaySafetyAssmntDetails.preSingleStage) {
      this.displayContactStandardsSection = this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.safetyDecisionCode === 'CONDSAFE' ||
        this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.safetyDecisionCode === 'UNSAFE';
    } else {
      if ((this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.safetyDecisionCode == null) ||
        (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.safetyDecisionCode === '')) {
        this.displayContactStandardsSection = false;
      }
    }
  }

  hideSelectStaff() {
    if ((this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted === null ||
      !this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted) && !this.isMPSEnvironment) {
      this.hideSelectStaffBtn = true;
    } else if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted) {
      this.hideSelectStaffBtn = false
    }
  }

  hideSections() {
    if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.assmtTypeCode === 'INIT') {
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.eventStatus === null ||
        this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.eventStatus === 'PROC') {
        if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted === null ||
          !this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted) {
          this.isCIExpanded = true;
        } else if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted != null &&
          this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted) {
          this.isCIExpanded = false;
          this.isF2FExpanded = true;
        }
      } else if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.eventStatus === 'COMP') {
        this.isF2FExpanded = true;
        this.isCIExpanded = false;
        this.isSDExpanded = false;
      }
    } else if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.assmtTypeCode === 'REAS') {
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.eventStatus === null ||
        this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.eventStatus === 'PROC' ||
        this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.eventStatus === 'COMP') {
        this.isF2FExpanded = true;
        this.isCIExpanded = false;
        this.isSDExpanded = false;
      }
    }
  }

  // Case Intitation section methods
  disableCaseInitiationSection() {
    if (this.displaySafetyAssmntDetails.caseInitiationSectionComplete) {
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.currentPriority').disable();
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.priorityComments').disable();
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.indCiImmediateIntervention').disable();
      (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
        if ((val.get('sectionCode').value === 'SA1A') || (val.get('sectionCode').value === 'SA1B') ||
          (val.get('sectionCode').value === 'SA1C')) {
          val.get('responseCode').disable();
        }
      });
      (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray).controls.forEach((val) => {
        if (val.get('contactType').value === 'CI') {
          val.get('contactOccurredDate').disable();
          val.get('contactOccurredTime').disable();
          val.get('contactAttempted').disable();
        }
      });
    }
  }

  chkIntakePrtyBoundaryValue() {
    if (!this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.indCiCompleted) {
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.initialPriority === '1') {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1A')) {
            val.get('responseCode').disable();
          }
        });
      } else if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.initialPriority === '4') {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1B')) {
            val.get('responseCode').disable();
          }
        });
      }
    }
  }

  chkPrtySelection() {
    this.displaySafetyAssmntDetails.safetyResponses.forEach(code => {
      if (code.sectionCode === 'SA1A' && (code.responseCode)) {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1B') ||
            (val.get('sectionCode').value === 'SA1C')) {
            val.get('responseCode').disable();
          }
        });
      } else if (code.sectionCode === 'SA1B' && (code.responseCode)) {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1A') ||
            (val.get('sectionCode').value === 'SA1C')) {
            val.get('responseCode').disable();
          }
        });
      } else if (code.sectionCode === 'SA1C' && (code.responseCode)) {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1B') ||
            (val.get('sectionCode').value === 'SA1A')) {
            val.get('responseCode').disable();
          }
        });
      }
    });
  }

  clearDecreasePrtySelectionVal() {
    this.displaySafetyAssmntDetails.safetyResponses.forEach(code => {
      if (code.sectionCode === 'SA1B') {
        code.responseCode = null
      }
    })
  }

  clearIncreasePrtySelectionVal() {
    this.displaySafetyAssmntDetails.safetyResponses.forEach(code => {
      if (code.sectionCode === 'SA1A') {
        code.responseCode = null
      }
    })
  }

  clearChangeNotApplicablePrtySelection() {
    this.displaySafetyAssmntDetails.safetyResponses.forEach(code => {
      if (code.sectionCode === 'SA1C') {
        code.responseCode = null
      }
    })
  }

  changeIncreasePrtySelection(e) {
    const listOfSA1A = [];
    this.safetyAssmntDetailsForm.get('safetyResponses').value.forEach(code => {
      if (code.sectionCode === 'SA1A' && (code.responseCode)) {
        listOfSA1A.push(code);
      }
    });
    if (listOfSA1A && listOfSA1A.length > 0) {
      this.clearDecreasePrtySelectionVal();
      this.clearChangeNotApplicablePrtySelection();
      (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
        if ((val.get('sectionCode').value === 'SA1B') ||
          (val.get('sectionCode').value === 'SA1C')) {
          val.get('responseCode').disable();
        }
      });
    } else {
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.initialPriority != '4') {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1B')) {
            val.get('responseCode').enable();
          }
        });
      }
      (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
        if ((val.get('sectionCode').value === 'SA1C')) {
          val.get('responseCode').enable();
        }
      });
    }
  }

  changeDecreasePrtySelection(e) {
    const listOfSA1B = [];
    this.safetyAssmntDetailsForm.get('safetyResponses').value.forEach(code => {
      if (code.sectionCode === 'SA1B' && (code.responseCode)) {
        listOfSA1B.push(code);
      }
    });
    if (listOfSA1B && listOfSA1B.length > 0) {
      this.clearIncreasePrtySelectionVal();
      this.clearChangeNotApplicablePrtySelection();
      (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
        if ((val.get('sectionCode').value === 'SA1A') || (val.get('sectionCode').value === 'SA1C')) {
          val.get('responseCode').disable();
        }
      });
    } else {
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.initialPriority != '1') {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1A')) {
            val.get('responseCode').enable();
          }
        });
      }
      (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
        if ((val.get('sectionCode').value === 'SA1C')) {
          val.get('responseCode').enable();
        }
      });
    }

  }

  changeNotApplicablePrtySelection(e) {
    const listOfSA1C = [];
    this.safetyAssmntDetailsForm.get('safetyResponses').value.forEach(code => {
      if (code.sectionCode === 'SA1C' && (code.responseCode)) {
        listOfSA1C.push(code);
      }
    });
    if (listOfSA1C && listOfSA1C.length > 0) {
      this.clearIncreasePrtySelectionVal();
      this.clearDecreasePrtySelectionVal();
      (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
        if ((val.get('sectionCode').value === 'SA1A') || (val.get('sectionCode').value === 'SA1B')) {
          val.get('responseCode').disable();
        }
      });
    } else {
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.initialPriority != '1') {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1A')) {
            val.get('responseCode').enable();
          }
        });
      }
      if (this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.initialPriority != '4') {
        (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).controls.forEach((val) => {
          if ((val.get('sectionCode').value === 'SA1B')) {
            val.get('responseCode').enable();
          }
        });
      }
    }
  }

  isNotApplicableSelected() {
    this.increasePrioritySelected = [];
    this.decreasePrioritySelected = [];
    this.notApplicableSelected = [];
    this.safetyAssmntDetailsForm.get('safetyResponses').value.forEach(code => {
      if (code.sectionCode === 'SA1A' && (code.responseCode)) {
        this.increasePrioritySelected.push(code)
      }
      else if (code.sectionCode === 'SA1B' && (code.responseCode)) {
        this.decreasePrioritySelected.push(code)
      }
      else if (code.sectionCode === 'SA1C' && (code.responseCode)) {
        this.notApplicableSelected.push(code)
      }
    });
  }

  priorityChanged(event) {
    this.isNotApplicableSelected();
    const priorityVal = this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.currentPriority').value;
    if (this.increasePrioritySelected && this.increasePrioritySelected.length > 0) {
      if (priorityVal >= this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.initialPriority').value) {
        this.isFinalPriorityChangedPopup('The Final Priority must be increased above the Assigned Priority from Intake.');
      }

    }
    else if (this.decreasePrioritySelected && this.decreasePrioritySelected.length > 0) {
      if (priorityVal <= this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.initialPriority').value) {
        this.isFinalPriorityChangedPopup('The Final Priority must be decreased below the Assigned Priority from Intake.');
      }
    }
    else if (this.notApplicableSelected && this.notApplicableSelected.length > 0) {
      if (priorityVal !== this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto.initialPriority').value) {
        this.isFinalPriorityChangedPopup('The Final Priority must equal Assigned Priority when Not Applicable is selected.');
      }
    }
  }

  onInputChange(preVal, currVal, index) {
    if (preVal === 'Y' && !currVal.value) {
      this.safetyAssmntDetailsForm.patchValue({
        section3Dirty: true,
      });
      this.valueChangePopupInCDF(index);
    } else if (preVal === 'N' && currVal.value) {
      this.safetyAssmntDetailsForm.patchValue({
        section3Dirty: true,
      });
    }
  }

  valueChangePopupInCDF(index) {
    const initialState = {
      title: this.displaySafetyAssmntDetails.pageTitle,
      message: 'This action will remove the problem origin from the Immediate Interventions section. Click Ok to continue',
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
        const updateRadioBtnVal = (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).at(index);
        updateRadioBtnVal.patchValue({
          answerCode: 'N'
        })
      } else {
        const updateRadioBtnVal = (this.safetyAssmntDetailsForm.get('safetyResponses') as FormArray).at(index);
        updateRadioBtnVal.patchValue({
          responseCode: true,
          answerCode: 'Y'
        })
      }
    });
  }

  saveRequiredPopup() {
    const initialState = {
      title: this.displaySafetyAssmntDetails.pageTitle,
      message: 'You must select "Save" to save your answers before the Safety Assessment can be completed.',
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

  roraRecompletePopup() {
    const initialState = {
      title: this.displaySafetyAssmntDetails.pageTitle,
      message:
        'Adding Immediate Interventions will reset the RORA Overrides, Final Risk Level and Event Status. Please recomplete the RORA.',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.eventFreezePopup();
      }
    });
  }

  supervisorConsultPopup() {
    const initialState = {
      title: this.displaySafetyAssmntDetails.pageTitle,
      message: 'Supervisor consult required to complete the Safety Assessment with less than two Attempted Face-to-Face Dates and Times.',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.eventFreezePopup();
      }
    });
  }

  eventFreezePopup() {
    const initialState = {
      title: this.displaySafetyAssmntDetails.pageTitle,
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
        this.submit();
      }
    });
  }

  isFinalPriorityChangedPopup(msg) {
    const initialState = {
      title: this.displaySafetyAssmntDetails.pageTitle,
      message: msg,
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered', initialState,
      ignoreBackdropClick: true,
      keyboard: false,
      backdrop: true
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      this.safetyAssmntDetailsForm.get('apsSafetyAssessmentDto').patchValue({
        currentPriority: this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.currentPriority
      })
    });
  }

  isPageChanged() {
    const defaultValue = this.safetyAssmntDetailsForm.value;
    this.safetyAssmntDetailsForm.valueChanges
      .subscribe(value => {
        if (JSON.stringify(defaultValue) === JSON.stringify(value)) {
          this.safetyAssmntDetailsForm.markAsPristine();
        }
      });
  }

  // save and continue in the Case Initiation section
  saveContinue() {
    this.safetyAssmntDetailsForm.patchValue({
      saveAndContinueClicked: true,
      isSaveClicked: false,
      saveAndCompleteClicked: false,
    });
    this.submit();
  }

  save() {
    // On save validate for future date and time field
    // On save and complete F2F Date and Time are required from backend
    this.safetyAssmntDetailsForm.get('apsSafetyContacts').value.forEach((code, index) => {
      const contactOccurredDate = (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray).at(index).get('contactOccurredDate');
      const contactOccurredTime = (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray).at(index).get('contactOccurredTime');
      if (code.contactType === 'F2F' && !code.contactAttempted) {
        if (code.contactOccurredDate) {
          contactOccurredDate.setValidators(
            [DfpsCommonValidators.validateDate, SafetyAssessmentValidators.validateFutureDate]);
          contactOccurredTime.setValidators(
            [Validators.required, SafetyAssessmentValidators.validateTimeFormat]);
          (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
            .at(index).get('contactOccurredDate').updateValueAndValidity();
          (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
            .at(index).get('contactOccurredTime').updateValueAndValidity();
        } else {
          (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
            .at(index).get('contactOccurredTime').clearValidators();
          (this.safetyAssmntDetailsForm.get('apsSafetyContacts') as FormArray)
            .at(index).get('contactOccurredTime').updateValueAndValidity();
        }
      } else if (code.contactType === 'F2F' && code.contactAttempted) {
        if (code.contactOccurredDate) {
          contactOccurredTime.setValidators(
            [Validators.required, SafetyAssessmentValidators.validateTimeFormat]);
          contactOccurredDate.setValidators(
            [DfpsCommonValidators.validateDate, SafetyAssessmentValidators.validateFutureDate]);
          contactOccurredDate.updateValueAndValidity();
          contactOccurredTime.updateValueAndValidity();
        }
      }
    })
    this.safetyAssmntDetailsForm.patchValue({
      isSaveClicked: true,
      saveAndCompleteClicked: false,
      saveAndContinueClicked: false
    });
    this.submit();
  }

  saveAndComplete() {
    this.safetyAssmntDetailsForm.patchValue({
      isSaveClicked: false,
      saveAndCompleteClicked: true,
      saveAndContinueClicked: false,
    });
    if (!this.safetyAssmntDetailsForm.pristine) {
      this.saveRequiredPopup();
    }
    // Check if all Sections have been completely answered, Saved and Passed Validation
    else {
      if (!this.displaySafetyAssmntDetails.caseInitiationSectionComplete ||
        !this.displaySafetyAssmntDetails.face2FaceSectionComplete
        || !this.displaySafetyAssmntDetails.immedIntervenSectionComplete ||
        !this.displaySafetyAssmntDetails.safetyDecisionSectionComplete) {
        this.submit();
      }
      else {
        if (this.displaySafetyAssmntDetails.preSingleStage && this.displaySafetyAssmntDetails.roraRecompleteMsgValid) {
          this.roraRecompletePopup();
        }
        if (this.displaySafetyAssmntDetails.supervisorConsultRequired) {
          this.supervisorConsultPopup();
        }
        else if (!this.displaySafetyAssmntDetails.supervisorConsultRequired && !(this.displaySafetyAssmntDetails.preSingleStage && this.displaySafetyAssmntDetails.roraRecompleteMsgValid)) {
          this.eventFreezePopup();
        }
      }
    }

  }

  submit() {
    const payload = Object.assign(this.displaySafetyAssmntDetails, this.safetyAssmntDetailsForm.getRawValue());
    this.safetyAssmntDetailsForm.patchValue({ buttonClicked: 'submit' });
    if (this.validateFormGroup(this.safetyAssmntDetailsForm)) {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      this.caseService.saveSafetyAssmntDetails(payload).subscribe(res => {
        this.safetyAssmntDetailsForm.patchValue({isSafetyInProcExists: res.safetyInProcExists});
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        if (!res.safetyInProcExists) {
          this.reloadCurrentPage(res.eventId);
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        }
      });
    }
  }

  reloadCurrentPage(eventId) {
    const currentUrl = 'case/safety-assessment-detail/' + eventId;
    this.router.navigateByUrl('/',
      { skipLocationChange: true }).then(() => {
        this.router.navigate([currentUrl]);
      });
  }

  // Forms and Narrative methods
  getForms(): FormValue[] {
    return [{
      formName: 'APS Safety Assessment Form',
      formParams: {
        docType: 'APSSA',
        docExists: 'false',
        windowName: this.displaySafetyAssmntDetails?.pageTitle,
        displayOnly: 'true',
        protectDocument: 'true',
        pStage: this.displaySafetyAssmntDetails.stageId ?
          String(this.displaySafetyAssmntDetails.stageId) : '',
        pEvent: this.displaySafetyAssmntDetails.eventId ? String(this.displaySafetyAssmntDetails.eventId) : ''
      }
    }
    ];
  }

  getNarrativeData() {
    return {
      docType: 'SHIELDCFTF',
      protectDocument: (this.displaySafetyAssmntDetails?.pageMode !== 'EDIT'),
      sEvent: this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto?.eventId ?
        String(this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto.eventId) : String(this.displaySafetyAssmntDetails.eventId),
      sTimestamp: this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto?.lastUpdatedDate ?
        String(this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto.lastUpdatedDate) : '',
      sIdLastUpdatePerson: this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto?.updatedPersonId ?
        String(this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto?.updatedPersonId) : '',
      sIdCreatedPerson: this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto?.createdPersonId ?
        String(this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto.createdPersonId) : ''
    };
  }

  generateNarrative() {
    const narrativeData: any = this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto;
    if (this.displaySafetyAssmntDetails.eventId == 0) {
      this.narrativeSavePopup();
    } else {
      const formDto: any = this.getNarrativeData();
      this.narrativeService.callNarrativeService(formDto);
    }
  }

  getContactNarrativeData() {
    return {
      docType: 'C24H',
      protectDocument: this.displaySafetyAssmntDetails?.caseInitiationSectionComplete,
      sEvent: this.displaySafetyAssmntDetails.contactNarrativeDto?.eventId ?
        String(this.displaySafetyAssmntDetails.contactNarrativeDto.eventId) : String(this.displaySafetyAssmntDetails.eventId),
      sTimestamp: this.displaySafetyAssmntDetails.contactNarrativeDto?.lastUpdatedDate ?
        String(this.displaySafetyAssmntDetails.apsSafetyAssessmentNarrativeDto?.lastUpdatedDate) : '',
      sCase: this.displaySafetyAssmntDetails.caseId ? String(this.displaySafetyAssmntDetails.caseId) : '',
      userId: this.displaySafetyAssmntDetails.apsSafetyAssessmentDto?.createdPersonId

    };
  }

  generateContactNarrative() {
    const narrativeData: any = this.displaySafetyAssmntDetails.contactNarrativeDto;
    if ((this.displaySafetyAssmntDetails?.pageMode === 'NEW' || this.displaySafetyAssmntDetails?.pageMode === 'NEW_USING')
      || ((this.displaySafetyAssmntDetails?.contactNarrativeDto === null
        || this.displaySafetyAssmntDetails?.contactNarrativeDto.eventId === 0)
        && this.displaySafetyAssmntDetails.apsSafetyAssessmentDto.assmtTypeCode === 'INIT')) {
      this.narrativeSavePopup();
    } else if (narrativeData?.eventId === null && this.displaySafetyAssmntDetails?.caseInitiationSectionComplete) {
      this.narrativePopup();
    } else {
      const formDto: any = this.getContactNarrativeData();
      this.narrativeService.callNarrativeService(formDto);
    }
  }

  narrativeSavePopup() {
    const initialState = {
      title: 'Safety Assessment',
      message:
        'Please save page before producing document.',
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

  narrativePopup() {
    const initialState = {
      title: 'Safety Assessment',
      message:
        'There is not a document to display.',
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

  shiftFocusToValidationErrors() {
    setTimeout(() => {
      document.getElementById('attentionMsgs').focus();
      document.getElementById('attentionMsgs').scrollIntoView();
    }, 500);
  }

}
