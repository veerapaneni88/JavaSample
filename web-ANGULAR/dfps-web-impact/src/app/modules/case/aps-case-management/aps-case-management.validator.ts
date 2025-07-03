import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class ApsCaseManagementValidator {
    static phoneNumberExtPattern(control: AbstractControl): ValidationErrors | null {
        const NUMBER_PATTERN = /^[0-9]+$/;
        if (control.value && control.value?.toString()?.trim()?.length > 0 && !NUMBER_PATTERN.test(control.value)) {
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
        if (control.value && control.value?.toString()?.trim()?.length > 0 && !PHONE_NUMBER_PATTERN_TEN_DIGIT.test(control.value.toString().trim().replace(/[^+\d]+/g, ''))) {
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

    static validateFutureDate(control: AbstractControl): ValidationErrors | null {
        if (!DfpsCommonValidators.validateDate(control) && new Date(control.value) > new Date()) {
            return {
                validateFutureClientAdvisedDate: true,
            };
        } else {
            return null;
        }
    }

    static validateEmailAddress(control: AbstractControl): ValidationErrors | null {
        const EMAIL_PATTERN = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
        if(control.value && !EMAIL_PATTERN.test(control.value)) {
            return {
                validateEmail: {
                    actualValue: control.value, 
                }
            };
        } else {
                return null;
        }
    }

    static requiredFieldonSaveandSubmit(errorKey: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) {
                return {
                    [`${errorKey}`]: true
                }
            }
            return null;
        };
    }
    static requiredFieldWithHiddenFieldName(errorKey: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.value) {
                return {
                    [`${errorKey}`]: {
                        hideFieldName: true
                    }
                }
            }
            return null;
        };
    }
}
