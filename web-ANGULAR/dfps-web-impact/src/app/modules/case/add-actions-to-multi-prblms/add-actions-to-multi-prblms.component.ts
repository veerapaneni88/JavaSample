import { Component, OnInit } from '@angular/core';
import { FormGroup, FormArray, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CaseService } from '../service/case.service';

@Component({
  selector: 'app-add-actions-to-multi-prblms',
  templateUrl: './add-actions-to-multi-prblms.component.html',
  styleUrls: ['./add-actions-to-multi-prblms.component.css']
})
export class AddActionsToMultiPrblmsComponent extends DfpsFormValidationDirective implements OnInit {

  constructor(private modalService: BsModalService,
    private caseService: CaseService, private fb: FormBuilder,
    private router: Router,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }
  apsRootFormDetails: any;
  childForm: FormGroup;

  ngOnInit(): void {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.apsRootFormDetails = this.caseService.getFormData();
    if (this.apsRootFormDetails) {
      this.getFormDetails();
    }
    this.createForm();
  }

  createForm() {
    this.childForm = this.fb.group({
      actionDescription: ['', Validators.required],
      actionCategoryCode: ['', Validators.required],
      actionResultsCode: ['']
    });
  }

  getFormDetails() {
    if (this.apsRootFormDetails.value.apsServicePlanDto) {
      if (this.apsRootFormDetails.value.apsServicePlanDto.immediateInterventionSources) {
        this.apsRootFormDetails.value.apsServicePlanDto.immediateInterventionSources.forEach(item => {
          (this.apsRootFormDetails.value.apsServicePlanDto.immediateInterventionSources as FormArray).push(
            this.servicePlanSourcesGrp(item)
          );
        });
      }
      if (this.apsRootFormDetails.value.apsServicePlanDto.allegData) {
        this.apsRootFormDetails.value.apsServicePlanDto.allegData.forEach(item => {
          (this.apsRootFormDetails.value.apsServicePlanDto.allegData as FormArray).push(
            this.servicePlanSourcesGrp(item)
          );
        });
      }
      if (this.apsRootFormDetails.value.apsServicePlanDto.snaIcsData) {
        this.apsRootFormDetails.value.apsServicePlanDto.snaIcsData.forEach(item => {
          (this.apsRootFormDetails.value.apsServicePlanDto.snaIcsData as FormArray).push(
            this.servicePlanSourcesGrp(item)
          );
        });
      }
    }
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

  get servicePlanSourcesList() {
    return (this.apsRootFormDetails.get('apsServicePlanDto.immediateInterventionSources') as FormArray).controls;
  }

  getServiceProblemsFor(index) {
    return ((this.apsRootFormDetails.get('apsServicePlanDto.immediateInterventionSources') as FormArray).
      controls[index].get('serviceProblems') as FormArray).controls;
  }

  getActionsFor(taskIndex, groupIndex) {
    return (((this.apsRootFormDetails.get('apsServicePlanDto.immediateInterventionSources') as FormArray).
      controls[taskIndex].get('serviceProblems') as FormArray).controls[groupIndex].get('actions') as FormArray).controls;
  }

  get snaServicePlanSourcesList() {
    return (this.apsRootFormDetails.get('apsServicePlanDto.snaIcsData') as FormArray).controls;
  }

  getServiceProblemsForSNAICSData(index) {
    return ((this.apsRootFormDetails.get('apsServicePlanDto.snaIcsData') as FormArray).
      controls[index].get('serviceProblems') as FormArray).controls;
  }

  getActionsForSNAICSData(taskIndex, groupIndex) {
    return (((this.apsRootFormDetails.get('apsServicePlanDto.snaIcsData') as FormArray).
      controls[taskIndex].get('serviceProblems') as FormArray).controls[groupIndex].get('actions') as FormArray).controls;
  }

  get allegationServicePlanSourcesList() {
    return (this.apsRootFormDetails.get('apsServicePlanDto.allegData') as FormArray).controls;
  }

  getServiceProblemsForAllegationData(index) {
    return ((this.apsRootFormDetails.get('apsServicePlanDto.allegData') as FormArray).
      controls[index].get('serviceProblems') as FormArray).controls;
  }

  getActionsForAllegationData(taskIndex, groupIndex) {
    return (((this.apsRootFormDetails.get('apsServicePlanDto.allegData') as FormArray).
      controls[taskIndex].get('serviceProblems') as FormArray).controls[groupIndex].get('actions') as FormArray).controls;
  }

  continue() {
    if (this.validateFormGroup(this.childForm)) {
      this.apsRootFormDetails.get('apsServicePlanDto.createActionsForMultiPrblm').patchValue({
        actionDescription: this.childForm.get('actionDescription').value,
        actionCategoryCode: this.childForm.get('actionCategoryCode').value,
        actionResultsCode: this.childForm.get('actionResultsCode').value
      });
      this.caseService.addActn(this.apsRootFormDetails.get('apsServicePlanDto').value).subscribe((res) => {
        if (res) {
          this.navigateBackToPreviousPage();
        }
      });
    }
  }

  cancel() {
    this.navigateBackToPreviousPage();
  }

  navigateBackToPreviousPage() {
    this.router.navigate([this.caseService.getReturnUrl()])
  }

}
