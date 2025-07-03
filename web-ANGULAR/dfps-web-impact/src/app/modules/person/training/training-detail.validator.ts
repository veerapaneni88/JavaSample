import { AbstractControl, ValidatorFn, ValidationErrors, Validators, FormGroup } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';
export class TrainingDetailValidator { 

    static validateSession(control: AbstractControl) {
        const NUMBER_PATTERN = /^[0-9]+$/;
        if (control.value && !NUMBER_PATTERN.test(control.value)) {
            return {
                csliValidationMsg: {
                    customFieldName: 'Session',
                    csliValidationMsg: true,
                    actualValue: control.value,
                }
            };
        }
        return null;
    }

    static validateHours(control: AbstractControl) {
        const NUMBER_PATTERN_TWO_DECIMAL_POSITIVE = /^\$?[0-9]*\.?[0-9]{0,2}$/;
        if (control.value && !NUMBER_PATTERN_TWO_DECIMAL_POSITIVE.test(control.value)) {
            return {
                validHoursMsg: {
                    customFieldName: 'Hours',
                    csliValidationMsg: true,
                    actualValue: control.value,
                }
            };
        }
        return null;
    }

    static validateDate = (group: FormGroup): { [key: string]: any } => {
        const trainingDate = group.controls.trainingDate;
        if (!trainingDate.value) {
            group.controls.trainingDate.setValidators(TrainingDetailValidator.setErrorValidator({
                required: { customFieldName: 'Date' }
            }));
        } else if (trainingDate && DfpsCommonValidators.validateDate(trainingDate)) {
            group.controls.trainingDate.setValidators(
                TrainingDetailValidator.setErrorValidator({
                    validateDate: { actualValue: trainingDate.value, customFieldName: 'Date' }
                }));
        } else {
            group.controls.trainingDate.clearValidators();
        }
        group.controls.trainingDate.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}