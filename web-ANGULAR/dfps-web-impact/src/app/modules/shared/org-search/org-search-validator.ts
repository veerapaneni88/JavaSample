import { AbstractControl, FormGroup } from '@angular/forms';

export class OrgSearchValidators {
    static validateEIN(control: AbstractControl) {
        const NUMBER_PATTERN = /^[.\d]+$/;
        if (control.value && !NUMBER_PATTERN.test(control.value)) {
            return {
              einPattern: {
                    actualValue: control.value,
                    einPattern: true
                }
            };
        }
        return null;
    }

    static validateAtleastOneFieldRequired() {
        return (group: FormGroup): { [key: string]: any } => {
            const ein = group.controls.ein;
            const otherName = group.controls.otherName;
            const legalName = group.controls.legalName;
            const atleastOneFieldValid = group.controls.atleastOneFieldValid;           
            if (ein.value.trim() === '' && otherName.value.trim() === '' && legalName.value.trim() === '') {
                atleastOneFieldValid.setErrors({
                    atleastOneFieldRequired: {
                        hideFieldName: true,
                        atleastOneFieldRequired: true
                    }
                });
            } else {
                atleastOneFieldValid.clearValidators();
                atleastOneFieldValid.updateValueAndValidity({ onlySelf: true });
            }
            return null;
        };
    }
}