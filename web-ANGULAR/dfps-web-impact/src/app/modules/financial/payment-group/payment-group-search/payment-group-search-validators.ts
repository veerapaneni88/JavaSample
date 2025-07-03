import { AbstractControl, ValidationErrors, FormGroup } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class PaymentGroupSearchValidators {
    static datesValidations(group: FormGroup) {
        if (
            !DfpsCommonValidators.validateDate(group.controls.toDate) &&
            !DfpsCommonValidators.validateDate(group.controls.fromDate) &&
            group.controls.toDate.value &&
            group.controls.toDate.value.toString().length > 0 &&
            !(new Date(group.controls.toDate.value) >= new Date(group.controls.fromDate.value))
        ) {
            group.controls.fromDate.setErrors({ fromDate: true });
        } else if (
            group.controls.toDate.value &&
            group.controls.toDate.value.length > 0 &&
            (!group.controls.fromDate.value || group.controls.fromDate.value.length === 0)
        ) {
            group.controls.fromDate.setErrors({ required: true });
        } else if (
            !DfpsCommonValidators.validateDate(group.controls.fromDate) &&
            !this.validateFutureDate(group.controls.fromDate)
        ) {
            group.controls.fromDate.setErrors(null);
        }

        if (
            group.controls.fromDate.value &&
            group.controls.fromDate.value.length > 0 &&
            (!group.controls.toDate.value || group.controls.toDate.value.length === 0)
        ) {
            group.controls.toDate.setErrors({ required: true });
        } else if (
            !DfpsCommonValidators.validateDate(group.controls.toDate) &&
            !this.validateFutureDate(group.controls.toDate)
        ) {
            group.controls.toDate.setErrors(null);
        }
        return null;
    }

    static paymentGroupSearchIdPattern(control: AbstractControl): ValidationErrors | null {
        const NUMBER_PATTERN = /^[0-9]{1,10}$/;
        if (control.value && (control.value as string).trim().length > 0 && !NUMBER_PATTERN.test(control.value)) {
            return {
                paymentGroupSearchIdPattern: {
                    actualValue: control.value,
                },
            };
        }
        return null;
    }

    static validateFutureDate(control: AbstractControl): ValidationErrors | null {
        if (!DfpsCommonValidators.validateDate(control) && new Date(control.value) > new Date()) {
            return {
                validateFutureDate: true,
            };
        } else {
            return {};
        }
    }
}
