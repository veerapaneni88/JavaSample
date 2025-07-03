import { Component, HostListener, Inject, OnDestroy, OnInit, forwardRef } from '@angular/core';
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
  NarrativeService,
  SET,
  DataTable,
  ENVIRONMENT_SETTINGS
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CaseService } from '../../service/case.service';
import { SearchService } from '@shared/service/search.service';
import { Subscription } from 'rxjs';
import { DisplayCareDetails } from '@case/model/careDetails';
import { HelpService } from '../../../../common/impact-help.service';


@Component({
  selector: 'app-care-details',
  templateUrl: './care-details.component.html',
})
export class CareDetailsComponent extends DfpsFormValidationDirective implements OnInit {
  eventId: any;
  displayCareDetails: DisplayCareDetails;
  apsCareDetailsForm: FormGroup;
  narrativeSubscription: Subscription;
  domains: any = [];
  getData: any = [];
  options: any = [];
  formValues: FormValue[];
  threateningOptions: any = [];
  disableRadioBtns = false;
  summaryAssmntOptions: any = [];
  riskReviewDomainList: any = [];
  customStyle = 'col-md-2';
  customStyle1 = 'col-md-1';
  customStyleLabel = "sr-only";
  exists = false;
  noExists = false;
  displayData: boolean;
  impactP2WebUrl: string;
  optionsWithoutSeverePrblm: { label: string; value: string; }[];
  optionsWithSeverePrblm: { label: string; value: string; }[];
  constructor(private navigationService: NavigationService, private modalService: BsModalService, private searchService: SearchService,
    private fb: FormBuilder, private caseService: CaseService, private route: ActivatedRoute, private narrativeService: NarrativeService,
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
    this.navigationService.setTitle('Client Assessment and Risk Evaluation (CARE) Page');
    this.helpService.loadHelp('Case');
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.eventId = routeParams.get('eventId');
    }
    this.initializeScreen();
    this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
      this.generateNarrative();
    });
  }

  createForm() {
    this.apsCareDetailsForm = this.fb.group({
      comment: [''],
      action: [''],
      lifeThreateningCode: [''],
      domains: this.fb.array([])
    });
  }

  setForm() {
    if (this.displayCareDetails) {
      this.apsCareDetailsForm.patchValue({
        domians: this.displayCareDetails.domains ? this.displayCareDetails.domains : [],
        comment: this.displayCareDetails.comment,
        action: this.displayCareDetails.action,
        lifeThreateningCode: this.displayCareDetails.lifeThreateningCode,
      })
    }
  }

  initializeScreen() {
    this.createForm();
    this.impactP2WebUrl = this.environmentSettings.impactP2WebUrl;
    this.caseService.getApsCareDetails().subscribe(res => {
      this.displayCareDetails = res;
      if (this.displayCareDetails.bshowSevereColumn) {
        this.options = this.optionsWithSeverePrblm;
      } else {
        this.options = this.optionsWithoutSeverePrblm;
      }
      this.setForm();
      this.displayCareDetails.formTagDto = {
        docExists: this.displayCareDetails.docExists,
        docType: this.displayCareDetails.docType,
        sCase: this.displayCareDetails.caseId,
        sEvent: this.displayCareDetails.eventId,
        protectDocument: true
      }
      if (this.displayCareDetails.pageMode === 'VIEW') {
        this.disableRadioBtns = true;
        FormUtils.disableFormControlStatus(this.apsCareDetailsForm,
          ['lifeThreateningCode', 'domains', 'comment', 'action', 'cdAllegationFocus']);
      }
      if ((this.displayCareDetails.lifeThreateningCode) && (this.displayCareDetails.lifeThreateningCode === 'Y')) {
        this.exists = true;
      } else if ((this.displayCareDetails.lifeThreateningCode) && (this.displayCareDetails.lifeThreateningCode === 'N')) {
        this.noExists = true;
      }
      if (this.displayCareDetails.domains) {
        this.displayCareDetails.domains.forEach(code => {
          (this.apsCareDetailsForm.get('domains') as FormArray).push(
            this.domiansListGrp(code)
          );
        });
      }

      this.displayCareData();
      this.formValues = this.getForms();
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });

    this.getData = [
      { label: 'Yes', value: 'Y' },
      { label: 'No', value: 'N' },
    ];

    this.optionsWithSeverePrblm = [
      { label: 'No Problem ', value: 'L' },
      {
        label: 'Managed Risk', value: 'G'
      }, {
        label: 'Problem', value: 'M'
      }, {
        label: 'Severe Problem ', value: 'H'
      }, {
        label: 'NA', value: 'N'
      }, {
        label: 'UTD', value: 'U'
      }
    ];
    this.optionsWithoutSeverePrblm = [
      { label: 'No Problem ', value: 'L' },
      {
        label: 'Managed Risk', value: 'G'
      }, {
        label: 'Problem', value: 'M'
      }, {
        label: 'NA', value: 'N'
      }, {
        label: 'UTD', value: 'U'
      }
    ];

    this.summaryAssmntOptions = [
      { label: 'Yes', value: 'Y' },
      { label: 'No', value: 'N' },
      { label: 'Inadequate Information', value: 'I' }];

  }

  displayCareData() {
    if (this.exists || this.noExists) {
      this.displayData = true;
    }
  }

  getForms(): FormValue[] {
    return [{
      formName: 'CARE',
      formParams: {
        docType: 'civ35o00',
        docExists: 'false',
        protectDocument: 'true',
        checkForNewMode: 'true',
        postInSameWindow: 'false',
        promptSavePage: 'frmCare',
        pEvent: this.displayCareDetails.eventId ? String(this.displayCareDetails.eventId) : '',
        pCase: this.displayCareDetails.caseId ? String(this.displayCareDetails.caseId) : '',
        pPerson: this.displayCareDetails.loginUserId ? String(this.displayCareDetails.loginUserId) : ''
      }
    }
    ];
  }

  narrativePopup() {
    const initialState = {
      title: 'Client Assessment and Risk Evaluation ',
      message:
        'There is not a document to display',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
    });
  }

  generateNarrative() {
    const narrativeData: any = this.displayCareDetails.formTagDto;
    if (!this.displayCareDetails.formTagDto.docExists) {
      this.narrativePopup();
    } else {
      this.narrativeService.callNarrativeService(narrativeData);
    }
  }

  get domainsList() {
    return (this.apsCareDetailsForm.get('domains') as FormArray).controls;
  }

  getCareCategoryList(index) {
    return ((this.apsCareDetailsForm.get('domains') as FormArray).
      controls[index].get('apsCareCategoryDtoList') as FormArray).controls;
  }

  getCareFactorList(taskIndex, groupIndex) {
    return (((this.apsCareDetailsForm.get('domains') as FormArray).
      controls[taskIndex].get('apsCareCategoryDtoList') as FormArray).
      controls[groupIndex].get('apsCareFactorDtoList') as FormArray).controls;
  }

  private domiansListGrp(code: any): FormGroup {
    return this.fb.group({
      careDomainId: code.careDomainId,
      noneFlag: code.noneFlag,
      caseId: code.caseId,
      cdAllegationFocus: code.cdAllegationFocus,
      cdDomain: code.cdDomain,
      descComment: code.descComment,
      dtLastUpdate: code.dtLastUpdate,
      eventId: code.eventId,
      nbrDomainOrder: code.nbrDomainOrder,
      stageId: code.stageId,
      txtDomain: code.txtDomain,
      isComplete: code.isComplete,
      reasonBelieveFlag: code.reasonBelieveFlag,
      apsCareCategoryDtoList: this.fb.array(this.getApsCareCategoryDtoListGrp(code.apsCareCategoryDtoList)),
    })
  }

  getApsCareCategoryDtoListGrp(apsCareCategoryDtoList: any) {
    return apsCareCategoryDtoList.map(code => this.fb.group({
      careCategoryId: code.careCategoryId,
      careDomainId: code.careDomainId,
      caseId: code.caseId,
      cdCareCategory: code.cdCareCategory,
      cdDomain: code.cdDomain,
      dtLastUpdate: code.dtLastUpdate,
      eventId: code.eventId,
      nbrCategoryOrder: code.nbrCategoryOrder,
      stageId: code.stageId,
      cdReasonBelieve: code.cdReasonBelieve,
      txtCategory: code.txtCategory,
      apsCareFactorDtoList: this.fb.array(this.getApsCareFactorDtoListGrp(code.apsCareFactorDtoList))
    }));
  }

  getApsCareFactorDtoListGrp(apsCareFactorDtoList: any) {
    return apsCareFactorDtoList.map(code => this.fb.group({
      txtFactor: code.txtFactor,
      cdCareFactorResponse: code.cdCareFactorResponse,
      cdCategory: code.cdCategory,
      careCategoryId: code.careCategoryId,
      careDomainId: code.careDomainId,
      careFactorId: code.careFactorId,
      caseId: code.caseId,
      cdFactor: code.cdFactor,
      dtLastUpdate: code.dtLastUpdate,
      eventId: code.eventId,
      indFactorHigh: code.indFactorHigh,
      indFactorLow: code.indFactorLow,
      indFactorMed: code.indFactorMed,
      indFactorNa: code.indFactorNa,
      indFactorUtd: code.indFactorUtd,
      nbrFactorOrder: code.nbrFactorOrder,
      stageId: code.stageId
    }));
  }

}
