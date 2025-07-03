import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class SafetyPlanValidators {

    static validateSafetyPlan() {
        return (group: FormGroup): { [key: string]: any } => {
            const effectiveDate = group.controls.effectiveDate;

            if (group.controls.operationNameAndNumber.value == null) {
                group.controls.operationNameAndNumber.setErrors({ 
                    safetyPlanRequired: { 
                        customFieldName: 'Operation Name and Number', 
                        safetyPlansafetyPlanRequired: { 
                            customFieldName: 'Operation Name and Number', 
                            safetyPlanRequired: true 
                        } 
                    } 
                });
            } else {
                group.controls.operationNameAndNumber.setErrors(null);
            }
            
            if (DfpsCommonValidators.validateDate(effectiveDate)) {
                group.controls.effectiveDate.setValidators(
                    SafetyPlanValidators.setErrorValidator({
                        validateDate: { actualValue: effectiveDate.value }
                    }));
            } else if (group.controls.effectiveDate.value === null || group.controls.effectiveDate.value === '') {
                group.controls.effectiveDate.setErrors({ 
                    safetyPlanRequired: { 
                        customFieldName: 'Effective Date', 
                        safetyPlanRequired: true 
                    } 
                });
            } else {
                group.controls.effectiveDate.setErrors(null);
            }

            if (group.controls.unsafeSituations.value == null || group.controls.unsafeSituations.value === '') {
                group.controls.unsafeSituations.setErrors({ 
                    required: { 
                        customFieldName: 'Each specific situation causing children to be unsafe', 
                        safetyPlanRequired: true 
                    } 
                });
            } else {
                group.controls.unsafeSituations.setErrors(null);
            }

            if (group.controls.immediateAction.value == null || group.controls.immediateAction.value === '') {
                group.controls.immediateAction.setErrors({ 
                    required: { 
                        customFieldName: 'Each action that will be taken immediately to keep children safe', 
                        safetyPlanRequired: true 
                    } 
                });
            } else {
                group.controls.immediateAction.setErrors(null);
            }

            if (group.controls.responsiblePersons.value == null || group.controls.responsiblePersons.value === '') {
                group.controls.responsiblePersons.setErrors({ 
                    required: { 
                        customFieldName: 'Each person responsible for ensuring that the operation takes these actions', 
                        safetyPlanRequired: true 
                    } 
                });
            } else {
                group.controls.responsiblePersons.setErrors(null);
            }

            if (group.controls.actionTimeFrame.value == null || group.controls.actionTimeFrame.value === '') {
                group.controls.actionTimeFrame.setErrors({ 
                    required: { 
                        customFieldName: 'Time frame for completing each action', 
                        safetyPlanRequired: true 
                    } 
                });
            } else {
                group.controls.actionTimeFrame.setErrors(null);
            }

            if (group.controls.safetyPlanStatus.value == null) {
                group.controls.safetyPlanStatus.setErrors({ 
                    safetyPlanRequired: { 
                        customFieldName: 'Status', 
                        safetyPlanRequired: true 
                    } 
                });
            } else if (group.controls.safetyPlanStatus.value === 'INP' && 
                        group.controls.clickedBtn.value === 'Save And Complete') {
                group.controls.safetyPlanStatus.setErrors({ 
                    clickSaveForInProcess: { 
                        customFieldName: 'SaveAndComplete', 
                        safetyPlanRequired: true 
                    } 
                });
            } else if (group.controls.safetyPlanStatus.value === 'INE' && 
                        group.controls.clickedBtn.value === 'Save') {
                group.controls.safetyPlanStatus.setErrors({ 
                    clickSaveAndCompleteForInEffect: { 
                        customFieldName: 'Save', 
                        safetyPlanRequired: true 
                    } 
                });
            } else if (group.controls.safetyPlanStatus.value === 'CLS' && 
                        group.controls.clickedBtn.value === 'Save') {
                group.controls.safetyPlanStatus.setErrors({ 
                    clickSaveAndCompleteForClosed: { 
                        customFieldName: 'Save', 
                        safetyPlanRequired: true 
                    } 
                });
            } else {
                group.controls.safetyPlanStatus.setErrors(null);
            }

            return null;
        };
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}
