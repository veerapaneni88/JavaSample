
import { AbstractControl, FormGroup, FormArray, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class SafetyAssessmentValidators {
    static validateFutureDate(control: AbstractControl): ValidationErrors | null {
        if (!DfpsCommonValidators.validateDate(control) && new Date(control.value) > new Date()) {
            return {
                validateFutureDate: true,
            };
        } else {
            return {};
        }
    }

    static validateTimeFormat(control: AbstractControl): ValidationErrors | null {
        if (control.value) {
            if (!(/\b((1[0-2]|0?[1-9]):([0-5][0-9]) ([AP][M]))/.test(control.value)))  {
                return {
                    validateTimeFormat: true,
                };
            }
        } else {
            return {};
        }
    }

    static checkIfSafetyAsmProcExists = (group: FormGroup): { [key: string]: any } => {

        const buttonClicked = group.controls.buttonClicked.value;

        const isSafetyInProcExists = group.controls.isSafetyInProcExists.value;
        if (buttonClicked && isSafetyInProcExists) {
            group.controls.isSafetyInProcExists.setValidators(SafetyAssessmentValidators.setErrorValidator({
                MSG_SYS_MULT_INST: { hideFieldName: true }
            }));
        } else {
            group.controls.isSafetyInProcExists.clearValidators();
        }
        group.controls.isSafetyInProcExists.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}