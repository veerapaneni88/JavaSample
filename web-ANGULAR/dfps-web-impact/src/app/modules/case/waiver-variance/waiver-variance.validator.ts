import { formatDate } from '@angular/common';
import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export class WaiverVarianceValidator {

    static validateChildrenInCare = (group: FormGroup): { [key: string]: any } => {
        const childrenInCare = group.controls.childrenInCare.value;
        if (childrenInCare) {
            if (!/^\d+$/.test(childrenInCare) || childrenInCare < 0) {
                group.controls.childrenInCare.setErrors({
                    validateNumber: {
                        customFieldName: 'Number of children in care or to be placed',
                        actualValue: childrenInCare,
                        hideFieldName: false
                    },
                });
            }
        } else {
            group.controls.childrenInCare.setErrors({
                required: {
                    customFieldName: 'Number of children in care or to be placed',
                    hideFieldName: false
                },
            });
        }
        return null;
    };

    static validateRequestUntilDate = (group: FormGroup): { [key: string]: any } => {
        const selDate = new Date(group.controls.requestUntilDate.value);
        const locale = group.controls.locale.value
        const toDate = new Date(formatDate(new Date(), 'MM/dd/yyyy', locale));

        if (!group.controls.requestUntilDate.errors || group.controls.requestUntilDate.errors === {}) {
            if (group.controls.requestUntilDate.value && (selDate < toDate)) {
                group.controls.requestUntilDate.setErrors({ SSM_DATE_SAME_OR_AFTER_CURR: { SSM_DATE_SAME_OR_AFTER_CURR: true } });
            } else {
                group.controls.requestUntilDate.setErrors({});
            }
        }
        return null;
    };

    static validateDescTextFields = (group: FormGroup): { [key: string]: any } => {
        const desc1 = group.controls.description1.value;
        const desc2 = group.controls.description2.value;
        const desc3 = group.controls.description3.value;
        const desc4 = group.controls.description4.value;
        const desc5 = group.controls.description5.value;
        const desc6 = group.controls.description6.value;
        const desc7 = group.controls.description7.value;

        if (!desc1 && !desc2 && !desc3 && !desc4 && !desc5 && !desc6 && !desc7) {
            group.controls.descriptionTxt.setErrors({
                MSG_FAH_WV_TEXT_ATLEASTONE: { MSG_FAH_WV_TEXT_ATLEASTONE: true, hideFieldName: true },
            });
        } else {
            group.controls.descriptionTxt.setErrors({});
        }

        return null;
    };

    static validateRequestType = (group: FormGroup): { [key: string]: any } => {
        const requestType = group.controls.request.value;
        if (!requestType) {
            group.controls.request.setValidators(WaiverVarianceValidator.setErrorValidator({
                required: { customFieldName: 'Request Type' }
            }));
        } else {
            group.controls.request.clearValidators();
        }
        group.controls.request.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}
