import { AbstractControl, FormGroup, FormArray, ValidationErrors, ValidatorFn } from '@angular/forms';
import { formatDate } from '@angular/common';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class SNAValidators {
    static validateSNAFormInfo = (group: FormGroup): { [key: string]: any } => {
        const caretakerId = group.controls.caretakerId.value;
        const resList = group.controls.responses.value;
        const isSaveClicked = group.controls.isSaveClicked.value;
        const isSaveAndCompleteClicked = group.controls.isSaveAndCompleteClicked.value;
        const checkboxVal = group.controls.indPrimaryCaretaker.value;
        const textDesCL = group.controls.textDesCL.value;
        if (!caretakerId && !checkboxVal && isSaveAndCompleteClicked) {
            group.controls.caretakerId.setValidators(
                SNAValidators.setErrorValidator({
                    primaryCaretaker:
                    {
                        hideFieldName: true,
                    }
                }));
        } else {
            group.controls.caretakerId.clearValidators();
        }
        const validateReslistCL = () => {
            return resList.some(item => {
                return ((item.domainCode === 'CL12') &&
                    (item.apsSnaAnswerLookupId && item.apsSnaAnswerLookupId !== 159) && (!item.txtOtherDescription));
            });
        };
        const validateReslistPC = () => {
            return resList.some(item => {
                return ((item.domainCode === 'PC7') &&
                    (item.apsSnaAnswerLookupId && item.apsSnaAnswerLookupId !== 181) && (!item.txtOtherDescription));
            });
        };
        if (validateReslistCL()) {
            group.controls.textDesCL.setValidators(
                SNAValidators.setErrorValidator({
                    DESC_CL:
                    {
                        customFieldName: 'Description'
                    }
                }));
        } else {
            group.controls.textDesCL.clearValidators();
        }
        if (validateReslistPC()) {
            group.controls.textDesPC.setValidators(
                SNAValidators.setErrorValidator({
                    DESC_PC:
                    {
                        customFieldName: 'Description'
                    }
                }));
        } else {
            group.controls.textDesPC.clearValidators();
        }
        group.controls.caretakerId.updateValueAndValidity({ onlySelf: true });
        group.controls.textDesCL.updateValueAndValidity({ onlySelf: true });
        group.controls.textDesPC.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}

