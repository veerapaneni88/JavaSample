import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export class PhoneDetailValidators {

    static phoneNumberPattern = (group: FormGroup): { [key: string]: any } => {
        const phoneNumber = group.controls.phoneNumber.value;
        if (phoneNumber) {
            const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[0-9]{10}$/;
            // replace '()' and '-' before checking for 10 digits in a phone number.
            if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim().replace(/[^+\d]+/g, ''))) {
                group.controls.phoneNumber.setErrors({ phoneNumberPattern: { actualValue: phoneNumber } });
            }
        }
        return null;
    }

    static phoneNumberExtPattern = (group: FormGroup): { [key: string]: any } => {
        const phoneExtension = group.controls.phoneExtension.value;
        const NUMBER_PATTERN = /^[0-9]+$/;
        if (phoneExtension && !NUMBER_PATTERN.test(phoneExtension)) {
            group.controls.phoneExtension.setValidators(
                PhoneDetailValidators.setErrorValidator({
                    validatePhoneExt: {
                        actualValue: phoneExtension,
                        customFieldName: 'Ext'
                    }
                }));
        } else { group.controls.phoneExtension.clearValidators(); }
        group.controls.phoneExtension.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}