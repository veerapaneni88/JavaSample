import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
export class PaymentProcessValidators {
    static validateDate(achDateControl: AbstractControl) {
        if (!achDateControl.errors && new Date(achDateControl.value) > new Date()) {
            return {
                achDate: true,
            };
        }
        return null;
    }

    static validateACHNumber(achNumberControl) {
        if (!achNumberControl.value) {
            return {
                achNo: {
                    customFieldName: 'ACH No.',
                    achNo: true
                }
            };
        }
        return null;
    }


    static validateACHDate(voidACHDateControl) {
        if (!voidACHDateControl.value) {
            return {
                achDateRequired: {
                    customFieldName: 'ACH Date',
                    achDateRequired: true,
                }
            };
        }
        return null;
    }

    static validateCheckNumber(checkNumberControl) {
        if (!checkNumberControl.value) {
            return {
                checkNumber: true,
            };
        }
        return null;
    }

    static validatevoidDate(voidDateControl) {
        if (!voidDateControl.value) {
            return {
                voidDateRequired: true,
            };
        }
        return null;
    }

    static validateRowSelect(control: AbstractControl): ValidationErrors | null {
        if (control.value === 0) {
            return {
                minSelections: {
                    customFieldName: '',
                    minSelections: true,
                    hideFieldName: true,
                },
            };
        } else {
            return null;
        }
    }

    static validateMaxRowSelect(control: AbstractControl): ValidationErrors | null {
        if (control.value > 50) {
            return {
                maxSelections: {
                    actualValue: control.value,
                    customFieldName: '',
                    maxSelections: true,
                    hideFieldName: true,
                },
            };
        } else {
            return null;
        }
    }
}
