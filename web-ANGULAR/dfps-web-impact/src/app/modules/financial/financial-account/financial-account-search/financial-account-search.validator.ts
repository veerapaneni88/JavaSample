import { AbstractControl, FormGroup, FormControlDirective, ValidatorFn, FormControl } from '@angular/forms';

export class FinancialAccountSearchValidators {
    static validateAccountNumber(control: AbstractControl) {
        const ACCOUNT_NO_PATTERN = /^[A-Za-z0-9]+$/
        if (control.value && !ACCOUNT_NO_PATTERN.test(control.value)) {
            return {
                            resourceSearchAge: {
                               actualValue: control.value,
                               resourceSearchAge: true
                           }
                       };
        }
        return null;
    }
    }
