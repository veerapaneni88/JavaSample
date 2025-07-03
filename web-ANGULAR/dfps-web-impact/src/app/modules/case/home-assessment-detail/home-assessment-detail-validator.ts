import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class HomeAssessmentDetailValidators {
    static validateHomeAssessmentDetail = (group: FormGroup): { [key: string]: any } => {
        const buttonClicked = group.controls.buttonClicked.value;
        if (buttonClicked === 'save' || buttonClicked === 'submit') {
            HomeAssessmentDetailValidators.commonValidations(group);
        }
        return null;
    }

    static commonValidations = (group: FormGroup): { [key: string]: any } => {
        const signatureDate = group.controls.signatureDate;
        const statusCodes = group.controls.statusCodes;
        const buttonClicked = group.controls.buttonClicked.value;
        const docExists = group.controls.docExists.value;
        const outputCompletedValue = group.controls.outputCompletedChecked.value;

        if (DfpsCommonValidators.validateDate(signatureDate)) {
            group.controls.signatureDate.setValidators(
                HomeAssessmentDetailValidators.setErrorValidator({
                    validateDate: { actualValue: signatureDate.value }
                }));
        } else if (signatureDate && new Date(signatureDate.value) > new Date()) {
            group.controls.signatureDate.setValidators(
                HomeAssessmentDetailValidators.setErrorValidator({
                    MSG_SVC_NO_FUTURE_DATE: { actualValue: signatureDate.value }
                }));
        } else if (!signatureDate.value) {
            group.controls.signatureDate.setValidators(
                HomeAssessmentDetailValidators.setErrorValidator({
                    SSM_COMPLETE_REQUIRED: { actualValue: signatureDate.value }
                }));
        } else { group.controls.signatureDate.clearValidators(); }

        if (!statusCodes.value) {
            group.controls.statusCodes.setValidators(
                HomeAssessmentDetailValidators.setErrorValidator({
                    SSM_COMPLETE_REQUIRED: { customFieldName: 'Assessment Status' }
                }));
        } else { group.controls.statusCodes.clearValidators(); }

        if ((buttonClicked === 'save' || buttonClicked === 'submit') && !docExists && outputCompletedValue) {
            group.controls.docExists.setValidators(
                HomeAssessmentDetailValidators.setErrorValidator({
                    STR_MSG_COMP_NOT_SAVED: { hideFieldName: true }
                }));
        } else { group.controls.docExists.clearValidators(); }

        group.controls.signatureDate.updateValueAndValidity({ onlySelf: true });
        group.controls.statusCodes.updateValueAndValidity({ onlySelf: true });
        group.controls.docExists.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}