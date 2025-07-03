import { AbstractControl, ValidationErrors } from "@angular/forms";

export class GuardianshipDetailsValidator {
    static phoneNumberExtPattern(control: AbstractControl): ValidationErrors | null {
        const NUMBER_PATTERN = /^[0-9]+$/;
        if (control.value && (control.value as string).trim().length > 0 && !NUMBER_PATTERN.test(control.value)) {
          return {
            validatePhoneExt: {
                actualValue: control.value,
                customFieldName: 'Ext'
            }
          };
          
        }
        return null;
      }

    static phoneNumberPattern(control: AbstractControl) {
        const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[0-9]{10}$/;
        if (control.value && !PHONE_NUMBER_PATTERN_TEN_DIGIT.test(control.value.trim().replace(/[^+\d]+/g, ''))) {
            return {
                phoneNumberPattern: {
                    customFieldName: 'Phone',
                    csliValidationMsg: true,
                    actualValue: control.value,
                }
            };
        }
        return null;
    }
}