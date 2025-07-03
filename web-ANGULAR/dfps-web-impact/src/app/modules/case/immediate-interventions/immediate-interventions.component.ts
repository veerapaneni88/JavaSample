import { Component, OnInit, Input, Inject, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormArray, FormGroupDirective } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
  DfpsFormValidationDirective,
  DirtyCheck,
  SET,
  ENVIRONMENT_SETTINGS
} from 'dfps-web-lib';

@Component({
  selector: 'immediate-interventions',
  templateUrl: './immediate-interventions.component.html',
  styleUrls: ['./immediate-interventions.component.css']
})
export class ImmediateInterventionsComponent extends DfpsFormValidationDirective implements OnInit {

  apsServicePlanDetailsForm: FormGroup;
  showImmediateInterventionSourcesMsg = true;
  hideAllBtns = true;
  @Output() backToTop = new EventEmitter<void>();
  constructor(private rootFormGroup: FormGroupDirective,
    public store: Store<{ dirtyCheck: DirtyCheck }>,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any) {
    super(store);
  }
  enableII: boolean;
  actionBtnClickedFrom: string;
  environment: string;
  listOfSrc: any = [];
  ngOnInit(): void {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.apsServicePlanDetailsForm = this.rootFormGroup.control;
    this.environment = this.environmentSettings.environmentName === 'Local' ? '' : '/impact3';
    (this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources')).value.forEach((val) => {
      this.listOfSrc.push(val)
    });
    if (this.apsServicePlanDetailsForm.get('pageTitle').value === 'Service Plan') {
      this.enableII = true;
      (this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).controls.forEach((val) => {
        val.get('selected').disable();
      });
    } else {
      this.enableII = false;
      (this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).controls.forEach((val) => {
        val.get('selected').enable();
      });
    }
    if ((this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources').value.length > 0)) {
      this.showImmediateInterventionSourcesMsg = false;
    }
    this.pageMode();
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  selectedSrc(list, selectedIndex) {
    list.forEach((i, currentIndex) => {
      if (currentIndex === selectedIndex) {
        const currVal = (this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).at(currentIndex);
        currVal.patchValue({
          selected: true
        })
      } else {
        const currVal = (this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).at(currentIndex);
        currVal.patchValue({
          selected: false
        });
        (this.apsServicePlanDetailsForm.get('apsServicePlanDto.snaIcsData') as FormArray).controls.forEach((val) => {
          val.patchValue({
            selected: false
          });
        });
        (this.apsServicePlanDetailsForm.get('apsServicePlanDto.allegData') as FormArray).controls.forEach((val) => {
          val.patchValue({
            selected: false
          })
        });
      }
    });
  }

  pageMode() {
    if ((this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources').value.length <= 0)
      || this.apsServicePlanDetailsForm.get('pageMode').value === 'VIEW') {
      this.hideAllBtns = false;
      (this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).controls.forEach((val) => {
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
    }
  }

  get servicePlanSourcesList() {
    return (this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).controls;
  }

  getServiceProblemsFor(index) {
    return ((this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).
      controls[index].get('serviceProblems') as FormArray).controls;
  }

  getActionsFor(taskIndex, groupIndex) {
    return (((this.apsServicePlanDetailsForm.get('apsServicePlanDto.immediateInterventionSources') as FormArray).
      controls[taskIndex].get('serviceProblems') as FormArray).controls[groupIndex].get('actions') as FormArray).controls;
  }

  backToTopClick() {
    this.backToTop.emit();
  }

}
