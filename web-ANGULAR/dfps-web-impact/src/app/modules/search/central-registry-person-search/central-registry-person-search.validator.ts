import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class CentralRegistryPersonSearchValidators {

    static personSearchValidation = (group: FormGroup): { [key: string]: any } => {
        const firstName = group.controls.firstName.value;
        const lastName = group.controls.lastName.value;
        const dob = group.controls.dob.value;
        const ssn = group.controls.ssn.value;
        const requestId = group.controls.requestId.value;
        const searchCriteria = group.controls.searchCriteria.value;
        const requestPattern = /^[\d]+$/;
        const PATTERN = /^[a-z A-Z]+$/;
        const searchType = group.controls.searchType.value;

        if (firstName && !lastName) {
            group.controls.lastName.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    required: { actualValue: lastName }
                }));
        } else if (lastName && !PATTERN.test(lastName) && searchType === '1') {
            group.controls.lastName.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    NUMERIC_NOT_VALID: { actualValue: lastName }
                }));
        } else { group.controls.lastName.clearValidators(); }

        if (!firstName && lastName) {
            group.controls.firstName.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    required: { actualValue: firstName }
                }));
        } else if (firstName && !PATTERN.test(firstName) && searchType === '1') {
            group.controls.firstName.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    NUMERIC_NOT_VALID: { actualValue: firstName }
                }));
        } else { group.controls.firstName.clearValidators(); }

        if (!firstName && !lastName && !dob && !ssn && !requestId) {
            group.controls.searchCriteria.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    SEARCH_CRITERIA: { actualValue: searchCriteria, hideFieldName: true }
                }));
        } else { group.controls.searchCriteria.clearValidators(); }

        if ((dob || dob !== '') && DfpsCommonValidators.validateDate(group.controls.dob)) {
            group.controls.dob.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    validateDate: { actualValue: dob, customFieldName: 'DOB' }
                }));
        } else { group.controls.dob.clearValidators(); }

        if ((requestId || requestId !== '') && !requestPattern.test(requestId)) {
            group.controls.searchCriteria.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    MSG_VALIDATE_NUMBER: { actualValue: requestId, customFieldName: 'Request ID' }
                }));
            }else { group.controls.dob.clearValidators(); }

        group.controls.lastName.updateValueAndValidity({ onlySelf: true });
        group.controls.firstName.updateValueAndValidity({ onlySelf: true });
        group.controls.searchCriteria.updateValueAndValidity({ onlySelf: true });
        group.controls.dob.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static ssnValidator(group: FormGroup) {
        const SSN_PATTERN = /^\d{3}-\d{2}-\d{4}$/;
        const ssn = group.controls.ssn.value;
        if (ssn && !SSN_PATTERN.test(ssn)) {
            group.controls.ssn.setValidators(
                CentralRegistryPersonSearchValidators.setErrorValidator({
                    ssnPattern: { actualValue: ssn, customFieldName: 'SSN' }
                }));
        } else {
            group.controls.ssn.clearValidators();
        }
        group.controls.ssn.updateValueAndValidity({ onlySelf: true });
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}
