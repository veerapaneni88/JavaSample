import { AbstractControl, ValidatorFn, ValidationErrors, Validators, FormGroup } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';
export class HomeComponentValidator {

    static validateChildesEthnicity(control: AbstractControl) {
        if (!control.value) {
            return {
                MSG_CHILD_ETHNICITY_REQ: {
                    hideFieldName: true,
                    MSG_CHILD_ETHNICITY_REQ: true
                }
            };
        }
        return null;
    }

    static numericValues(control: AbstractControl): ValidationErrors | null {
        const NUMBER_PATTERN = /^[0-9]+$/;
        if (control.value && (control.value as string).trim().length > 0 && !NUMBER_PATTERN.test(control.value)) {
          return {
            numericValues: true
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

    static zipPattern(fieldName?: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value) {
                const ZIP_PATTERN = /[0-9]{5}/;
                if (!ZIP_PATTERN.test(control.value)) {
                    return {
                        zipPattern: {
                            actualValue: control.value,
                            customFieldName: fieldName,
                        },
                    };
                }
                return null;
            } else {
                return null;
            }
        };
    }

    static zipExtensionPattern(fieldName?: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value) {
                const ZIP_EXT_PATTERN = /[0-9]{4}/;
                if (!ZIP_EXT_PATTERN.test(control.value)) {
                    return {
                        zipExtensionPattern: {
                            actualValue: control.value,
                            customFieldName: fieldName,
                        },
                    };
                }
                return null;
            } else {
                return null;
            }
        };
    }

    
  static alphaNumeric(fieldName?: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        if (control.value) {
            const PATTERN = /^[a-zA-Z0-9]+$/;
            if (!PATTERN.test(control.value)) {
                return {
                    alphaNumeric: {
                        actualValue: control.value,
                        fieldName: 'Attention',
                    },
                };
            }
            return null;
        } else {
            return null;
        }
    };
  }
}