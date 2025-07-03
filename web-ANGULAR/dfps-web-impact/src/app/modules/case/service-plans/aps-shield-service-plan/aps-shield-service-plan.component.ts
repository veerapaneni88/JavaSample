import { Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { CaseService } from '@case/service/case.service';
import { Store } from '@ngrx/store';
import {
  DfpsConfirmComponent,
  DfpsCommonValidators,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  FormService,
  FormValue,
  NarrativeService,
  NavigationService,
  SET,
  DataTable, ENVIRONMENT_SETTINGS
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Subscription } from 'rxjs';
import { DisplayApsServicePlanDetails } from '@case/model/apsServicePlanDetailsList';
import { ActivatedRoute, Router } from '@angular/router';
import { HelpService } from '../../../../common/impact-help.service';

@Component({
  selector: 'app-aps-shield-service-plan',
  templateUrl: './aps-shield-service-plan.component.html',
  styleUrls: ['./aps-shield-service-plan.component.css']
})
export class ApsShieldServicePlanComponent extends DfpsFormValidationDirective implements OnInit {
  @ViewChild('servicePlanSectionRef')
  servicePlanSectionRef: ElementRef;
  tableColumn: any[];
  servicePlanLandingPageData: any;
  narrativeSubscription: Subscription;
  formValues: FormValue[];
  servicePlanDetailsForm: FormGroup;
  displayServicePlanDetails: DisplayApsServicePlanDetails;
  eventId: any;
  caseId: any;
  stageId: any;
  displaySNAICSDetails = false;
  contactStandardsDataTable: DataTable;
  isEditMode: boolean;
  isMPSEnvironment = false;
  formsSubscription: Subscription;
  listOfAllegationSrc: any = [];
  listOfSnaIcsSrc: any = [];

  constructor(private navigationService: NavigationService,
    private formService: FormService,
    private caseService: CaseService, private modalService: BsModalService,
    private narrativeService: NarrativeService,
    private helpService: HelpService,
    private route: ActivatedRoute,
    private fb: FormBuilder, private router: Router,
    public store: Store<{ dirtyCheck: DirtyCheck }>,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any) {
    super(store);
    this.setUserData();
  }
  actionBtnClickedFrom: string;

  setUserData() {
    // set user data for 3rd level menu rendering
    const params = this.route.snapshot.paramMap.get('caseId');
    const params1 = this.route.snapshot.paramMap.get('stageId');
    this.navigationService.setUserDataValue('caseId', params);
    this.navigationService.setUserDataValue('stageId', params1);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Service Plan');
    this.helpService.loadHelp('Assess');
    // this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.initializeScreen();
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
    this.isMPSEnvironment = 'MPS' === this.environmentSettings.environmentName.toUpperCase();
  }

  loadFadDataTable() {
    this.tableColumn = [
      {
        field: 'planSourceCodeDecode', header: 'Source', sortable: false, width: 200,
        isLink: true,
        url: '/case/displayContactStandardDetail/:id',
        urlParams: ['id']
      },
      { field: 'planStartDate', header: 'Start Date', sortable: false, width: 150 },
      { field: 'planEndDate', header: 'End Date', sortable: false, width: 100 },
      { field: 'totalNumberOfContacts', header: 'Total Contacts', sortable: false, width: 100 },
      { field: 'numberOfFaceToFaceContacts', header: 'Face to Face Contacts', sortable: false, width: 100 },
      { field: 'numberOfContactsReqd', header: 'Other Contacts', sortable: false, width: 100 },
      { field: 'activeInd', header: 'Active', sortable: false, width: 100 }
    ];
    this.contactStandardsDataTable = {
      tableBody: this.displayServicePlanDetails.apsServicePlanDto.monitoringPlanList,
      tableColumn: this.tableColumn,
      isPaginator: (this.displayServicePlanDetails && this.displayServicePlanDetails.apsServicePlanDto.monitoringPlanList &&
        this.displayServicePlanDetails.apsServicePlanDto.monitoringPlanList.length) > 10 ? true : false
    };
  }

  initializeScreen() {
    this.createForm();
    this.caseService.getApsServicePlanDetails().subscribe(res => {
      this.displayServicePlanDetails = res;
      if (this.displayServicePlanDetails.apsServicePlanDto) {
        if (this.displayServicePlanDetails.apsServicePlanDto.immediateInterventionSources) {
          this.displayServicePlanDetails.apsServicePlanDto.immediateInterventionSources.forEach(item => {
            (this.servicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).push(
              this.servicePlanSourcesGrp(item)
            );
          });
        }
        if (this.displayServicePlanDetails.apsServicePlanDto.allegData) {
          this.displayServicePlanDetails.apsServicePlanDto.allegData.forEach(item => {
            (this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).push(
              this.servicePlanSourcesGrp(item)
            );
          });
          (this.servicePlanDetailsForm.get('apsServicePlanDto.allegData')).value.forEach((val) => {
            this.listOfAllegationSrc.push(val);
          });
        }
        if (this.displayServicePlanDetails.apsServicePlanDto.snaIcsData) {
          this.displayServicePlanDetails.apsServicePlanDto.snaIcsData.forEach(item => {
            (this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).push(
              this.servicePlanSourcesGrp(item)
            );
          });
          (this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData')).value.forEach((val) => {
            this.listOfSnaIcsSrc.push(val)
          });
        }
      }
      if ((this.displayServicePlanDetails.stageCode === 'INV' && !(this.displayServicePlanDetails.preSingleStage)) ||
        this.displayServicePlanDetails.stageType === 'ICS'
      ) {
        this.displaySNAICSDetails = true;
      }
      else if ((this.displayServicePlanDetails.stageCode === 'INV' && this.displayServicePlanDetails.preSingleStage)
        || this.displayServicePlanDetails.stageType === 'MNT') {
        this.displaySNAICSDetails = false
      }
      if (this.displayServicePlanDetails.approvalEvent?.status === 'PEND') {
        this.CMN_INVLD_APRVL_POPUP();
      }
      this.loadFadDataTable();
      this.setForm();
      this.getPageMode();
      this.formValues = this.getForms();
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
  }

  createForm() {
    this.servicePlanDetailsForm = this.fb.group({
      pageTitle: [''],
      approvalEvent: [''],
      apsServicePlanDto: this.fb.group({
        actionOutcomeCodes: [''],
        actionCategoryCodes: [''],
        actionResultCodes: [''],
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
        updateMultiPrblmOrActnData: this.fb.group({
          actionDescription: [''],
          actionCategoryCode: [''],
          actionResultsCode: [''],
          outcomeCode: ['']
        }),
        createActionsForMultiPrblm: this.fb.group({
          actionDescription: [''],
          actionCategoryCode: [''],
          actionResultsCode: ['']
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
        currSelectedSourcesList: [''],
        eventId: [''],
        id: [''],
        immediateInterventionSources: this.fb.array([]),
        monitoringPlanList: [''],
        servicePlanSources: [''],
        snaIcsData: this.fb.array([]),
        servicePlanEventId: [''],
        caseId: [''],
        isUpdateProblemClicked: [false],
        isUpdateActionClicked: [false],
        actionBtnClickedFromSection: [''],
        spaneexpanded: [false],
        spcontactStandardExpanded: [false],
        spiiexpanded: [false],
        spmultipleProbActionExpanded: [false],
        spsnaorICSExpanded: [false]
      }),
      immedIntervenSectionComplete: [''],
      caseId: [''],
      caseName: [''],
      completedSAAvailable: [''],
      dateLastUpdate: [''],
      dirty: [''],
      eventId: [''],
      hasStageAccess: [''],
      id: [''],
      infoMessages: [''],
      loggedInUser: [''],
      mandatoryMessageBlack: [''],
      mandatoryMessageRed: [''],
      pageMode: [''],
      popUpMessages: [''],
      preSingleStage: [''],
      serviceSources: [''],
      stageCode: [''],
      stageId: [''],
      stageType: [''],
      isSaveClicked: [false],
      isUpdateProblemClicked: [false],
      isUpdateActionClicked: [false],
    });
  }

  setForm() {
    if (this.displayServicePlanDetails) {
      this.servicePlanDetailsForm.patchValue({
        pageTitle: 'Service Plan',
        apsServicePlanDto: this.displayServicePlanDetails.apsServicePlanDto ? this.displayServicePlanDetails.apsServicePlanDto : null,
        approvalEvent: this.displayServicePlanDetails.approvalEvent ? this.displayServicePlanDetails.approvalEvent : null,
        caseId: this.displayServicePlanDetails.caseId ? this.displayServicePlanDetails.caseId : '',
        caseName: this.displayServicePlanDetails.caseName ? this.displayServicePlanDetails.caseName : '',
        dateLastUpdate: this.displayServicePlanDetails.dateLastUpdate ? this.displayServicePlanDetails.dateLastUpdate : '',
        dirty: this.displayServicePlanDetails.dirty ? this.displayServicePlanDetails.dirty : '',
        eventId: this.displayServicePlanDetails.eventId ? this.displayServicePlanDetails.eventId : '',
        hasStageAccess: this.displayServicePlanDetails.hasStageAccess ? this.displayServicePlanDetails.hasStageAccess : '',
        id: this.displayServicePlanDetails.id ? this.displayServicePlanDetails.id : '',
        loggedInUser: this.displayServicePlanDetails.loggedInUser ? this.displayServicePlanDetails.loggedInUser : '',
        mandatoryMessageBlack: this.displayServicePlanDetails.mandatoryMessageBlack ?
          this.displayServicePlanDetails.mandatoryMessageBlack : '',
        mandatoryMessageRed: this.displayServicePlanDetails.mandatoryMessageRed ? this.displayServicePlanDetails.mandatoryMessageRed : '',
        pageMode: this.displayServicePlanDetails.pageMode ? this.displayServicePlanDetails.pageMode : '',
        popUpMessages: this.displayServicePlanDetails.popUpMessages ? this.displayServicePlanDetails.popUpMessages : '',
        preSingleStage: this.displayServicePlanDetails.preSingleStage ? this.displayServicePlanDetails.preSingleStage : '',
        serviceSources: this.displayServicePlanDetails.serviceSources ? this.displayServicePlanDetails.serviceSources : '',
        stageCode: this.displayServicePlanDetails.stageCode ? this.displayServicePlanDetails.stageCode : '',
        stageId: this.displayServicePlanDetails.stageId ? this.displayServicePlanDetails.stageId : '',
        stageType: this.displayServicePlanDetails.stageType ? this.displayServicePlanDetails.stageType : '',
        infoMessages: this.displayServicePlanDetails.infoMessages ? this.displayServicePlanDetails.infoMessages : [],
        completedSAAvailable: this.displayServicePlanDetails.completedSAAvailable ?
          this.displayServicePlanDetails.completedSAAvailable : null,
        spaneexpanded: this.displayServicePlanDetails.apsServicePlanDto.spaneexpanded,
        spcontactStandardExpanded: this.displayServicePlanDetails.apsServicePlanDto.spcontactStandardExpanded,
        spiiexpanded: this.displayServicePlanDetails.apsServicePlanDto.spiiexpanded,
        spmultipleProbActionExpanded: this.displayServicePlanDetails.apsServicePlanDto.spmultipleProbActionExpanded,
        spsnaorICSExpanded: this.displayServicePlanDetails.apsServicePlanDto.spsnaorICSExpanded
      })
    }
  }

  updateStatusOfIISection(el) {
    this.servicePlanDetailsForm.get('apsServicePlanDto').patchValue({
      spiiexpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  updateStatusOfANESection(el) {
    this.servicePlanDetailsForm.get('apsServicePlanDto').patchValue({
      spaneexpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  updateStatusOfSNAOrICSSection(el) {
    this.servicePlanDetailsForm.get('apsServicePlanDto').patchValue({
      spsnaorICSExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  updateStatusOfContactStandardSection(el) {
    this.servicePlanDetailsForm.get('apsServicePlanDto').patchValue({
      spcontactStandardExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  updateStatusOfMultipleProbActionSection(el) {
    this.servicePlanDetailsForm.get('apsServicePlanDto').patchValue({
      spmultipleProbActionExpanded: el
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
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
      sourceType: code.sourceType,
      allegedPerpetratorName: code.allegedPerpetratorName
    })
  }

  getServiceProblemsGrp(serviceProblems: any) {
    return serviceProblems.map(code => this.fb.group({
      id: code.id,
      selected: code.selected,
      problemDescription: code.problemDescription,
      actions: this.fb.array(this.getActionsGrp(code.actions)),
      apsSpServiceSrcId: code.apsSpServiceSrcId,
      outcomeCode: code.outcomeCode,
      lastUpdatedDate: code.lastUpdatedDate
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
      selected: code.selected,
      lastUpdatedDate: code.lastUpdatedDate
    }));
  }

  get snaServicePlanSourcesList() {
    return (this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).controls;
  }

  getServiceProblemsForSNAICSData(index) {
    return ((this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).
      controls[index].get('serviceProblems') as FormArray).controls;
  }

  getActionsForSNAICSData(taskIndex, groupIndex) {
    return (((this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).
      controls[taskIndex].get('serviceProblems') as FormArray).controls[groupIndex].get('actions') as FormArray).controls;
  }

  get servicePlanSourcesList() {
    return (this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).controls;
  }

  getServiceProblemsFor(index) {
    return ((this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).
      controls[index].get('serviceProblems') as FormArray).controls;
  }

  getActionsFor(taskIndex, groupIndex) {
    return (((this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).
      controls[taskIndex].get('serviceProblems') as FormArray).controls[groupIndex].get('actions') as FormArray).controls;
  }

  generateNarrative() {
    const narrativeData: any = this.servicePlanLandingPageData?.docExists;
    if (narrativeData) {
      this.narrativeService.callNarrativeService(narrativeData);
    }
  }

  launchForm(data: any) {
    if (data) {
      const { pageMode, eventId } = this.displayServicePlanDetails;
      if ((pageMode === 'NEW' && !eventId) || (pageMode === 'EDIT' && this.servicePlanDetailsForm.dirty)) {
        const initialState = {
          title: 'Service Plan',
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
      formName: 'Service Plan',
      formParams: {
        docType: 'APSSP',
        docExists: 'false',
        protectDocument: 'true',
        checkForNewMode: 'true',
        pCase: this.displayServicePlanDetails.caseId ?
          String(this.displayServicePlanDetails.caseId) : '',
        idStage: this.displayServicePlanDetails.stageId ?
          String(this.displayServicePlanDetails.stageId) : '',
        pStage: this.displayServicePlanDetails.stageId ?
          String(this.displayServicePlanDetails.stageId) : ''
      }
    }];
  }

  selectedAllegSrc(list, selectedIndex) {
    list.forEach((i, currentIndex) => {
      if (currentIndex === selectedIndex) {
        const currVal = (this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).at(currentIndex);
        currVal.patchValue({
          selected: true
        })
      } else {
        const currVal = (this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).at(currentIndex);
        currVal.patchValue({
          selected: false
        });
        (this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).controls.forEach((val) => {
          console.log(val);
          val.patchValue({
            selected: false
          });
        });
        (this.servicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).controls.forEach((val) => {
          console.log(val);
          val.patchValue({
            selected: false
          });
        });
      }
    });
  }

  selectedSrcVal(list, selectedIndex) {
    list.forEach((i, currentIndex) => {
      if (currentIndex === selectedIndex) {
        const currVal = (this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).at(currentIndex);
        currVal.patchValue({
          selected: true
        })
      } else {
        const currVal = (this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).at(currentIndex);
        currVal.patchValue({
          selected: false
        });
        (this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).controls.forEach((val) => {
          console.log(val);
          val.patchValue({
            selected: false
          })
        });
        (this.servicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).controls.forEach((val) => {
          console.log(val);
          val.patchValue({
            selected: false
          });
        });
      }
    });
  }

  getPageMode() {
    if (this.displayServicePlanDetails.pageMode === 'EDIT' || this.displayServicePlanDetails.pageMode === 'NEW') {
      this.isEditMode = true;
    } else if (this.displayServicePlanDetails.pageMode === 'VIEW') {
      this.isEditMode = false;
      (this.servicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).controls.forEach((val) => {
        val.get('selected').disable();
        (val.get('serviceProblems') as FormArray).controls.forEach((list) => {
          list.get('outcomeCode').disable();
          list.get('problemDescription').disable();
          list.get('selected').disable();
          (list.get('actions') as FormArray).controls.forEach((item) => {
            item.get('actionCategoryCode').disable();
            item.get('actionDescription').disable();
            item.get('actionResultsCode').disable();
            item.get('selected').disable();
          })
        })
      });
      (this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).controls.forEach((val) => {
        val.get('selected').disable();
        (val.get('serviceProblems') as FormArray).controls.forEach((list) => {
          list.get('outcomeCode').disable();
          list.get('problemDescription').disable();
          list.get('selected').disable();
          (list.get('actions') as FormArray).controls.forEach((item) => {
            item.get('actionCategoryCode').disable();
            item.get('actionDescription').disable();
            item.get('actionResultsCode').disable();
            item.get('selected').disable();
          })
        })
      });
      this.servicePlanDetailsForm.get('apsServicePlanDto.updateMultiPrblmOrActnData.actionDescription').disable();
      this.servicePlanDetailsForm.get('apsServicePlanDto.updateMultiPrblmOrActnData.actionCategoryCode').disable();
      this.servicePlanDetailsForm.get('apsServicePlanDto.updateMultiPrblmOrActnData.actionResultsCode').disable();
      this.servicePlanDetailsForm.get('apsServicePlanDto.updateMultiPrblmOrActnData.outcomeCode').disable();
    }
  }

  showErrorPopup(errorMessage: string) {
    const initialState = {
      title: 'Service Plan',
      message: errorMessage,
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

  addPrblm() {
    this.showErrorPopup('Select the problem origin to add a problem.')
  }

  removePrblm() {
    this.showErrorPopup('Select the problem(s) that need to be removed.')
  }

  addAction() {
    this.showErrorPopup('Select Problem(s) to add an Action"')
  }

  removeAction() {
    this.showErrorPopup('Select Action(s) to be removed')
  }

  CMN_INVLD_APRVL_POPUP() {
    this.showErrorPopup('Saving this page will invalidate pending approval. Click OK to continue.')
  }

  updateProblem() {
    const selectedSvcPrblmsInII = this.servicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources').value.some(item => {
      return item.serviceProblems.some((val) => val.selected === true);
    });
    const selectedSvcPrblmsInAllegData = this.servicePlanDetailsForm.get('apsServicePlanDto.allegData').value.some(item => {
      return item.serviceProblems.some((val) => val.selected === true);
    });
    const selectedSvcPrblmsInSNAICSData = this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData').value.some(item => {
      return item.serviceProblems.some((val) => val.selected === true);
    });
    if (selectedSvcPrblmsInII || selectedSvcPrblmsInAllegData || selectedSvcPrblmsInSNAICSData) {
      this.servicePlanDetailsForm.get('apsServicePlanDto').patchValue({
        isUpdateProblemClicked: true,
        isUpdateActionClicked: false,
      });
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      this.caseService.updateMultiProblrmsInServicePlanDto(this.servicePlanDetailsForm.get('apsServicePlanDto').value).subscribe((res) => {
        if (res) {
          this.reloadCurrentPage();
        }
      })
    } else {
      this.showErrorPopup('At least one problem must be selected to perform Update probelm function')
    }
  }

  updateAction() {
    const selectedActnsInII = this.servicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources').value.some(item => (
      item.serviceProblems.some((val) => (val.actions.some((el) => el.selected === true)))
    ));
    const selectedActnsInAllegData = this.servicePlanDetailsForm.get('apsServicePlanDto.allegData').value.some(item => (
      item.serviceProblems.some((val) => (val.actions.some((el) => el.selected === true)))
    ));
    const selectedActnsInSNAICSData = this.servicePlanDetailsForm.get('apsServicePlanDto.snaIcsData').value.some(item => (
      item.serviceProblems.some((val) => (val.actions.some((el) => el.selected === true)))
    ));
    if (selectedActnsInII || selectedActnsInAllegData || selectedActnsInSNAICSData) {
      this.servicePlanDetailsForm.get('apsServicePlanDto').patchValue({
        isUpdateActionClicked: true,
        isUpdateProblemClicked: false,
      });
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      this.caseService.updateMultiActionsInServicePlanDto(this.servicePlanDetailsForm.get('apsServicePlanDto').value).subscribe((res) => {
        if (res) {
          this.reloadCurrentPage();
        }
      })
    } else {
      this.showErrorPopup('At least one action must be selected to perform Update action function')
    }
  }

  cancel() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.caseService.cancelUpdatePrblmOrActn(this.servicePlanDetailsForm.get('apsServicePlanDto').value).subscribe(
      (res) => {
        if (res) {
          this.reloadCurrentPage();
        }
      })
  }

  reloadCurrentPage() {
    const currentUrl = 'case/shield-service-plan/displayServicePlan';
    this.router.navigateByUrl('/',
      { skipLocationChange: true }).then(() => {
        this.router.navigate([currentUrl]);
      });
  }

  backToTop() {
    this.servicePlanSectionRef.nativeElement.focus();
    window.scrollTo(0,0);
  }
}
