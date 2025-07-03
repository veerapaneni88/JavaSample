import { SafetyPlan } from './../model/SafetyPlan';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsCommonValidators, DfpsConfirmComponent, DfpsFormValidationDirective, DirtyCheck, DropDown,
  FormUtils,
  NavigationService
} from 'dfps-web-lib';
import { saveAs } from 'file-saver';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { SafetyPlanRes } from '../model/SafetyPlan';
import { CaseService } from '../service/case.service';
import { SafetyPlanValidators } from './safety-plan.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'safety-plan',
  templateUrl: './safety-plan.component.html'
})
export class SafetyPlanComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  safetyPlanForm: FormGroup;
  safetyPlanId: any;
  eventId: any;

  @ViewChild('errors') errorElement: ElementRef;
  safetyPlanResponse: SafetyPlanRes;
  modalSubscription: Subscription;
  disableDeleteButton = false;
  disableDownloadButton = false;
  hideDeleteButton = false;
  hideDownloadButton = false;
  hideSaveAndCompleteButton = false;
  hideSaveButton = false;
  spStatus = '';
  isReadOnly = false;
  hasStageAccess = false;

  constructor(private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: BsModalService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private caseService: CaseService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
    super(store);
    this.navigationService.setTitle('Safety Plan Detail');
    this.helpService.loadHelp('Case');

    if(this.router.url.includes('event')) {
      this.eventId = this.route.snapshot.paramMap.get('eventId');
    } else {
      this.eventId = null;
      this.safetyPlanId = this.route.snapshot.paramMap.get('safetyPlanId');
    }
  }

  ngOnInit(): void {
    this.createForm();
    if(this.eventId != null) {
      this.caseService.getSafetyPlanDetailFromEvent(this.eventId).subscribe(
        response => {
          this.initializeForm(response);
        });
    } else {
      this.caseService.getSafetyPlanDetail(this.safetyPlanId).subscribe(
        response => {
          this.initializeForm(response);
        });
    }
  }

  initializeForm(resp) {
    this.safetyPlanResponse = resp;

    this.isReadOnly = this.safetyPlanResponse.pageMode === 'VIEW' ? true : false;
    this.hasStageAccess = this.safetyPlanResponse.hasStageAccess;

    if (this.safetyPlanResponse.safetyPlan) {
      this.safetyPlanForm.setValue({
        operationNameAndNumber: this.safetyPlanResponse.safetyPlan?.facilityId.toString(),
        effectiveDate: this.safetyPlanResponse.safetyPlan.effectiveDate,
        unsafeSituations: this.safetyPlanResponse.safetyPlan.unsafeSituations,
        immediateAction: this.safetyPlanResponse.safetyPlan.immediateAction,
        responsiblePersons: this.safetyPlanResponse.safetyPlan.responsiblePersons,
        actionTimeFrame: this.safetyPlanResponse.safetyPlan.actionTimeFrame,
        safetyPlanStatus: this.safetyPlanResponse.safetyPlan.safetyPlanStatus,
        id: this.safetyPlanResponse.safetyPlan.id,
        clickedBtn: ''
      });
      this.spStatus = this.safetyPlanResponse.safetyPlan.safetyPlanStatus;
    }
    this.updateFormDisplay();
  }

  createForm() {
    this.safetyPlanForm = this.formBuilder.group({
      operationNameAndNumber: [],
      effectiveDate: ['', DfpsCommonValidators.validateDate],
      unsafeSituations: [],
      immediateAction: [],
      responsiblePersons: [],
      actionTimeFrame: [],
      safetyPlanStatus: [],
      clickedBtn: [''],
      id: []
    }, {
      validators: [
        SafetyPlanValidators.validateSafetyPlan(),
      ]

    });
  }

  updateFormDisplay() {
    this.disableDeleteButton = false;
    this.disableDownloadButton = false;
    this.hideDeleteButton = false;
    this.hideDownloadButton = false;
    this.hideSaveAndCompleteButton = false;
    this.hideSaveButton = false;
    this.filterSafetyPlanStatuses();

    if (this.safetyPlanResponse?.safetyPlan?.id == null ||
      this.safetyPlanResponse?.safetyPlan?.id === 0) {
        this.disableDeleteButton = true;
        this.disableDownloadButton = true;
    } else if (!this.hasStageAccess) {
      this.hideDownloadButton = true;
    } else if (this.safetyPlanForm.controls.safetyPlanStatus.value === 'INE') {
      this.hideDeleteButton = true;
      this.hideSaveButton = true;
    }else if (this.safetyPlanForm.controls.safetyPlanStatus.value === 'CLS') {
      this.hideDeleteButton = true;
      this.hideSaveButton = true;
      this.hideSaveAndCompleteButton = true;
    }
    if (this.safetyPlanForm.controls.safetyPlanStatus.value === 'INE' ||
      this.safetyPlanForm.controls.safetyPlanStatus.value === 'CLS') {
      FormUtils.disableFormControlStatus(this.safetyPlanForm, ['effectiveDate']);
      if (this.safetyPlanForm.controls.safetyPlanStatus.value === 'CLS') {
        FormUtils.disableFormControlStatus(this.safetyPlanForm, ['safetyPlanStatus']);
      }
    } else {
      FormUtils.enableFormControlStatus(this.safetyPlanForm, ['effectiveDate']);
    }

    if(this.isReadOnly) {
      FormUtils.disableFormControlStatus(this.safetyPlanForm,
        ['operationNameAndNumber','effectiveDate','unsafeSituations','immediateAction',
        'responsiblePersons', 'actionTimeFrame', 'safetyPlanStatus']);
      this.hideDeleteButton = true;
      if (this.safetyPlanForm.controls.safetyPlanStatus.value === 'INE') {
        this.hideDownloadButton = false;
      }
      this.hideSaveAndCompleteButton = true;
      this.hideSaveButton = true;
    }
  }

  filterSafetyPlanStatuses() {
    const statuses: any = this.safetyPlanResponse?.safetyPlanStatuses;
    if (statuses && statuses.length) {
      const filteredStauses = statuses.filter(element => {
        if(this.safetyPlanForm.controls.safetyPlanStatus.value === 'INE'){
          if(element.code !== 'INP'){
            return element;
          }
        } else{
          return element;
        }
      });
      this.safetyPlanResponse.safetyPlanStatuses = [...filteredStauses];
    }
  }

  download() {
    if (this.validateFormGroup(this.safetyPlanForm)) {
      this.caseService.downloadSafetyPlan(this.safetyPlanResponse.safetyPlan.id)
      .subscribe(blob => saveAs(blob, 'Safety_Plan_' +
        this.safetyPlanResponse.safetyPlan.caseId + '_' +
        this.safetyPlanResponse.safetyPlan.id + '.pdf'));
    }
  }

  delete() {
    const selectSafetyPlanIds: any[] = [this.safetyPlanResponse.safetyPlan.id];
    const initialState = {
      message: 'Are you sure you want to delete this Safety Plan?',
      title: 'Safety Plan Details',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
          this.caseService.deleteSafetyPlans(selectSafetyPlanIds).subscribe(res => {
            this.router.navigate(['/case/case-management/maintenance/safety-plans']);
        });
      }
    });
  }

  saveAndComplete() {
    this.safetyPlanForm.controls.clickedBtn.setValue('Save And Complete');
    if (this.validateFormGroup(this.safetyPlanForm)) {
      if(this.safetyPlanForm.controls.safetyPlanStatus.value === 'INE'){
        this.saveSafetyPlan();
      } else {
        const initialState = {
          message: 'Once the Safety Plan\'s Status is changed to \'Closed\', it cannot be changed to \'In Effect\'. ' +
                   'Would you like to continue?',
          title: 'Save and Complete Safety Plan',
          showCancel: true,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
          if (result) {
            this.saveSafetyPlan();
          }
        });
      }
    }
  }

  save() {
    this.safetyPlanForm.controls.clickedBtn.setValue('Save');
    this.safetyPlanForm.controls.effectiveDate.updateValueAndValidity();
    if (this.validateFormGroup(this.safetyPlanForm)) {
      this.saveSafetyPlan();
    }
  }

  saveSafetyPlan() {
    const safetyPlan: SafetyPlan = {};
    if(!this.safetyPlanResponse.safetyPlan) {
      this.safetyPlanResponse.safetyPlan = {};
    }
    this.safetyPlanResponse.safetyPlan.facilityId =
      (this.safetyPlanForm.controls.operationNameAndNumber.value === null ? 0 : this.safetyPlanForm.controls.operationNameAndNumber.value);
    this.safetyPlanResponse.safetyPlan.effectiveDate = this.safetyPlanForm.controls.effectiveDate.value;
    this.safetyPlanResponse.safetyPlan.unsafeSituations = this.safetyPlanForm.controls.unsafeSituations.value;
    this.safetyPlanResponse.safetyPlan.immediateAction = this.safetyPlanForm.controls.immediateAction.value;
    this.safetyPlanResponse.safetyPlan.responsiblePersons = this.safetyPlanForm.controls.responsiblePersons.value;
    this.safetyPlanResponse.safetyPlan.actionTimeFrame = this.safetyPlanForm.controls.actionTimeFrame.value;
    this.safetyPlanResponse.safetyPlan.safetyPlanStatus = this.safetyPlanForm.controls.safetyPlanStatus.value;
    this.safetyPlanResponse.safetyPlan.id = this.safetyPlanResponse?.safetyPlan.id ? this.safetyPlanResponse?.safetyPlan.id : 0;
    this.caseService
      .saveSafetyPlan( this.safetyPlanResponse.safetyPlan)
      .subscribe((result) => {
        if (result) {
          this.caseService.redirectToUrl('case/case-management/maintenance/safety-plans');
        }
      });
  }
}
