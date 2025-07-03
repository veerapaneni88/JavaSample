import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn, FormArray } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class ServicePackageErrorsValidators {

    static servicePackageValidation = (group: FormGroup): { [key: string]: any } => {

        const fromDate = group.controls.fromDate.value;
        const toDate = group.controls.toDate.value;

        // Errors From - conditional required check
        if (!fromDate && toDate) {
            group.controls.fromDate.setValidators(ServicePackageErrorsValidators.setErrorValidator({
                required: { customFieldName: 'Errors From' }
            }));
        } else if (fromDate && DfpsCommonValidators.validateDate(group.controls.fromDate)) {
            group.controls.fromDate.setValidators(
                ServicePackageErrorsValidators.setErrorValidator({
                    validateDate: { customFieldName: 'Errors From', actualValue: fromDate }
                }));
        } else if (fromDate && toDate && new Date(fromDate) > new Date(toDate)) {
            group.controls.fromDate.setValidators(
                ServicePackageErrorsValidators.setErrorValidator({
                    SPE_START_BEFORE_SAME_END: { actualValue: fromDate }
                }));
        } else {
            group.controls.fromDate.clearValidators();
        }

        // Errors To - conditional required check
        if (fromDate && !toDate) {
            group.controls.toDate.setValidators(ServicePackageErrorsValidators.setErrorValidator({
                required: { customFieldName: 'Errros To' }
            }));
        } else if (toDate && DfpsCommonValidators.validateDate(group.controls.toDate)) {
            group.controls.toDate.setValidators(
                ServicePackageErrorsValidators.setErrorValidator({
                    validateDate: { customFieldName: 'Errors To', actualValue: toDate }
                }));
        } else {
            group.controls.toDate.clearValidators();
        }

        group.controls.fromDate.updateValueAndValidity({ onlySelf: true });
        group.controls.toDate.updateValueAndValidity({ onlySelf: true });

        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}