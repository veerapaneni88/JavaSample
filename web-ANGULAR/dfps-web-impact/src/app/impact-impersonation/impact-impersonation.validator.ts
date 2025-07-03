import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn, FormArray } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class ImpersonationValidators {

    static impersonationValidation = (group: FormGroup): { [key: string]: any } => {

        const impersonationId = group.controls.impersonationId.value;
        const pattern = /^[.\d]+$/;

        if (!impersonationId) {
            group.controls.impersonationId.setValidators(ImpersonationValidators.setErrorValidator({
                required: { customFieldName: 'Logon As (UserID)' }
            }));
        } else if (impersonationId && !pattern.test(impersonationId)) {
            group.controls.impersonationId.setValidators(
                ImpersonationValidators.setErrorValidator({
                    validateNumber: { actualValue: impersonationId, customFieldName: "Logon As (UserID)" }
                }));
        } else { group.controls.impersonationId.clearValidators(); }

        group.controls.impersonationId.updateValueAndValidity({ onlySelf: true });

        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}