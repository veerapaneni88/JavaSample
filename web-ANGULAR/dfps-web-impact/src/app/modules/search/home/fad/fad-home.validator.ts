import { AbstractControl, ValidatorFn, ValidationErrors, Validators, FormGroup } from '@angular/forms';
export class FadHomeComponentValidator {

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

    static poboxValidation = (group: FormGroup): { [key: string]: any } => {
        const phoneTypePrimary = group.controls.phoneTypePrimary.value;
        const addressLn1PrimaryAddress = group.controls.addressLn1PrimaryAddress.value;
        if (!addressLn1PrimaryAddress) {
            group.controls.addressLn1PrimaryAddress.setValidators(FadHomeComponentValidator.setErrorValidator({
                required: { customFieldName: 'Address Ln 1' }
            }));
        } else if (addressLn1PrimaryAddress && phoneTypePrimary === '01') {
            group.controls.addressLn1PrimaryAddress.setValidators(FadHomeComponentValidator.poboxPattern('Address Ln 1'));
        } else {
            group.controls.addressLn1PrimaryAddress.clearValidators();
        }
        group.controls.addressLn1PrimaryAddress.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static poboxPattern(fieldName?: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            const POBOX_PATTERN = /\b(?:p\.?\s*o\.?|post\s+office)\s+box\b/i;
            if (control.value && POBOX_PATTERN.test(control.value)) {
                return {
                    poboxPattern: {
                        actualValue: control.value,
                        customFieldName: fieldName,
                    },
                };
            } else {
                return null;
            }
        };
    }

}