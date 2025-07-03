import { FormGroup, ValidatorFn, AbstractControl, ValidationErrors, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class FinancialAccountValidators {
    static validateDateFields = (group: FormGroup) => {
        const from = group.controls.from.value;
        const to = group.controls.to.value;
        if (from && to && new Date(from) > new Date(to)) {
            group.controls.from.setValidators(FinancialAccountValidators.setError());            
        } else {
            group.controls.from.setValidators([Validators.required, DfpsCommonValidators.validateDate]);
        }        
        group.controls.from.updateValueAndValidity({ onlySelf: true });
        group.controls.to.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                dateFrom: {
                    dateFrom: true
                }
            };
        };
    }
}

