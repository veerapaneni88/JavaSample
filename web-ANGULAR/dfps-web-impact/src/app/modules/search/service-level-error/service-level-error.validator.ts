import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn, FormArray } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class ServiceLevelErrorsValidators {

    static serviceLevelValidation = (group: FormGroup): { [key: string]: any } => {
        const fromDate = group.controls.fromDate.value;
        const toDate = group.controls.toDate.value;

        // Errors From - conditional required check
        if (!fromDate && toDate) {
            group.controls.fromDate.setValidators(ServiceLevelErrorsValidators.setErrorValidator({
                SLE_START_AND_END_DATES_REQUIRED: { customFieldName: 'Errors From' }
            }));
        } else if (fromDate && DfpsCommonValidators.validateDate(group.controls.fromDate)) {
            group.controls.fromDate.setValidators(
                ServiceLevelErrorsValidators.setErrorValidator({
                    SLE_INVALID_DATE: { customFieldName: 'Errors From', actualValue: fromDate }
                }));
        } else if (fromDate && toDate && new Date(fromDate) > new Date(toDate)) {
            group.controls.fromDate.setValidators(
                ServiceLevelErrorsValidators.setErrorValidator({
                    SLE_START_BEFORE_SAME_END: { customFieldName: 'Errors From', actualValue: fromDate }
                }));
        } else {
            group.controls.fromDate.clearValidators();
        }

        // Errors To - conditional required check
        if (fromDate && !toDate) {
            group.controls.toDate.setValidators(ServiceLevelErrorsValidators.setErrorValidator({
                SLE_START_AND_END_DATES_REQUIRED: { customFieldName: 'Errors To' }
            }));
        } else if (toDate && DfpsCommonValidators.validateDate(group.controls.toDate)) {
            group.controls.toDate.setValidators(
                ServiceLevelErrorsValidators.setErrorValidator({
                    SLE_INVALID_DATE: { customFieldName: 'Errors To', actualValue: toDate }
                }));
        } else {
            group.controls.toDate.clearValidators();
        }

        group.controls.fromDate.updateValueAndValidity({ onlySelf: true });
        group.controls.toDate.updateValueAndValidity({ onlySelf: true });

        const personId = group.controls.personId.value;
        if (personId != null && personId != '' && personId != undefined) {
            if (!/^\d+$/.test(personId)) {
                group.controls.personId.setValidators(ServiceLevelErrorsValidators.setErrorValidator({
                    SLE_PERSON_ID_NOT_VALID: {
                        customFieldName: 'Person ID',
                        actualValue: personId
                    }
                }));
            } else {
                group.controls.personId.clearValidators();
            }
        } else {
            group.controls.personId.clearValidators();
        }
        group.controls.personId.updateValueAndValidity({ onlySelf: true });


        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}