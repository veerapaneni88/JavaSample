import { AbstractControl, ValidationErrors } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class NeiceTransmittalListValidator {

    static validateFromFutureDate(control: AbstractControl): ValidationErrors | null {
        if (!DfpsCommonValidators.validateDate(control) && new Date(control.value) > new Date()) {
            return { dateFromNEICE: true }
        }
        return null;
    }

    static validateToFutureDate(control: AbstractControl): ValidationErrors | null {
        if (!DfpsCommonValidators.validateDate(control) && new Date(control.value) > new Date()) {
            return { dateToNEICE: true }
        }
        return null;
    }

}
