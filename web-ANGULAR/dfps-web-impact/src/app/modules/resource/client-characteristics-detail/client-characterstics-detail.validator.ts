import { AbstractControl, FormGroup, ValidationErrors, FormControlDirective, ValidatorFn, FormControl } from '@angular/forms';

export class ClientCharactersticsDetailValidators {   

    static yearPattern(fieldName?: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value) {
                const YEAR_PATTERN = /^0*(?:[0-9][0-9]?|120)$/
                if (!YEAR_PATTERN.test(control.value)) {
                    return {
                        yearRange: {
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

  

    static monthPattern(fieldName?: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value) {
                const MONTH_PATTERN = /^([0-9]|1[011])$/
                if (!MONTH_PATTERN.test(control.value)) {
                    return {
                        monthRange: {
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

    static validateCharacteristics = (group: FormGroup): { [key: string]: any } => {
        const characteristics = group.controls.character.value;
        if (!characteristics) {
            group.controls.character.setValidators(ClientCharactersticsDetailValidators.setErrorValidator({
                required: { customFieldName: 'Characteristics' }
            }));
        } else {
            group.controls.character.clearValidators();
        }
        group.controls.character.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}