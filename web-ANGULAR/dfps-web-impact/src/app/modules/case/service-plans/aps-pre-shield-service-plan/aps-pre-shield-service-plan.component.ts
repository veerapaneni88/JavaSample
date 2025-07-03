import { Component, OnInit, Inject } from '@angular/core';
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
import { HelpService } from 'app/common/impact-help.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, FormArray } from '@angular/forms';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-pre-aps-shield-service-plan',
  templateUrl: './aps-pre-shield-service-plan.component.html',
})
export class ApsPreShieldServicePlanComponent extends DfpsFormValidationDirective implements OnInit {
  displayApsPreShieldServicePlanDetails: any;
  apsPreShieldServicePlanForm: FormGroup;
  formValues: FormValue[];
  narrativeSubscription: Subscription;
  formsSubscription: Subscription;

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
  setUserData() {
    // set user data for 3rd level menu rendering
    const params = this.route.snapshot.paramMap.get('caseId');
    const params1 = this.route.snapshot.paramMap.get('stageId');
    this.navigationService.setUserDataValue('caseId', params);
    this.navigationService.setUserDataValue('stageId', params1);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('APS Service Plan');
    this.initializeScreen();
    this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
      this.generateNarrative();
    });
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
  }

  initializeScreen() {
    this.createForm();
    this.caseService.getPreShieldServicePlan().subscribe(res => {
      console.log(res);
      this.displayApsPreShieldServicePlanDetails = res;
      this.setForm();
      this.formValues = this.getForms();
      if (this.displayApsPreShieldServicePlanDetails.apsClientFactorsDto) {
        this.displayApsPreShieldServicePlanDetails.apsClientFactorsDto.forEach(item => {
          (this.apsPreShieldServicePlanForm.get('apsClientFactorsDto') as FormArray).push(
            this.apsClientFactors(item)
          );
        });
      }
      this.getPageMode();
    });
  }

  getPageMode() {
    this.apsPreShieldServicePlanForm.get('apsInvestigationDetailDto.dtApsInvstCltAssmt').disable();
    (this.apsPreShieldServicePlanForm.get('apsClientFactorsDto') as FormArray).controls.forEach((val) => {
      (val.get('apsClientFactorsCodesDto') as FormArray).controls.forEach((list) => {
        list.get('selectedFlag').disable();
        list.get('txtApsCltFactorCmnts').disable();
      })
    });
  }

  createForm() {
    this.apsPreShieldServicePlanForm = this.fb.group({
      apsClientFactorsDto: this.fb.array([]),
      apsInvestigationDetailDto: this.fb.group({
        dtApsInvstCmplt: [''],
        dtApsInvstCltAssmt: ['']
      }),
      displayClientAssessment: [''],
      narrativeExists: [''],
      pageMode: ['']
    })
  }

  get servicePlanSourcesList() {
    return (this.apsPreShieldServicePlanForm.get('apsClientFactorsDto') as FormArray).controls;
  }

  getapsClientCategoryDtoList(index) {
    return ((this.apsPreShieldServicePlanForm.get('apsClientFactorsDto') as FormArray).
      controls[index].get('apsClientFactorsCodesDto') as FormArray).controls;
  }

  private apsClientFactors(item: any): FormGroup {
    return this.fb.group({
      apsClientFactorsCodesDto: this.fb.array(this.apsClientCategoryDtoList(item.apsClientFactorsCodesDto)),
      cdApsClientFactor: item.cdApsClientFactor,
      codeDecode: item.codeDecode,
      idApsCltFactor: item.idApsCltFactor,
      dtLastUpdate: item.dtLastUpdate,
      idCase: item.idCase,
      idEvent: item.idEvent,
      idSituation: item.idSituation,
    })
  }

  apsClientCategoryDtoList(list: any) {
    return list.map(item => this.fb.group({
      cdApsCltFactorAns: item.cdApsCltFactorAns,
      cdApsCltFactorCateg: item.cdApsCltFactorCateg,
      code: item.code,
      codeType: item.codeType,
      decode: item.decode,
      dtApsCltFactorProblem: item.dtApsCltFactorProblem,
      dtPurged: item.dtPurged,
      selectedFlag: item.selectedFlag,
      txtApsCltFactorCmnts: item.txtApsCltFactorCmnts
    }));
  }

  setForm() {
    if (this.displayApsPreShieldServicePlanDetails) {
      this.apsPreShieldServicePlanForm.patchValue({
        apsClientFactorsDto: this.displayApsPreShieldServicePlanDetails.apsClientFactorsDto,
        apsInvestigationDetailDto: this.displayApsPreShieldServicePlanDetails.apsInvestigationDetailDto,
        displayClientAssessment: this.displayApsPreShieldServicePlanDetails.displayClientAssessment,
        narrativeExists: this.displayApsPreShieldServicePlanDetails.narrativeExists,
        pageMode: this.displayApsPreShieldServicePlanDetails.pageMode
      })
    }
  }

  launchForm(data: any) {
    if (data) {
    }
    this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
  }

  ngOnDestroy(): void {
    this.formsSubscription?.unsubscribe();
  }

  getForms(): FormValue[] {
    return [{
      formName: 'Identified Problems Summary',
      formParams: {
        docType: 'cfiv0700',
        docExists: 'false',
        protectDocument: 'true',
        checkForNewMode: 'true',
        postInSameWindow: 'false',
        promptSavePage: 'frmAPSServicePlan',
        pEvent: this.displayApsPreShieldServicePlanDetails.apsInvestigationDetailDto.idEvent ?
          String(this.displayApsPreShieldServicePlanDetails.apsInvestigationDetailDto.idEvent) : '',
        pStage: this.displayApsPreShieldServicePlanDetails.apsInvestigationDetailDto.idApsStage ?
          String(this.displayApsPreShieldServicePlanDetails.apsInvestigationDetailDto.idApsStage) : ''
      }
    }];
  }

  getNarrativeData() {
    return {
      docType: 'addcmnts',
      protectDocument: true,
      sEvent: this.displayApsPreShieldServicePlanDetails.apsInvestigationDetailDto?.idEvent
    };
  }

  generateNarrative() {
    const formDto: any = this.getNarrativeData();
    this.narrativeService.callNarrativeService(formDto);
  }

}
