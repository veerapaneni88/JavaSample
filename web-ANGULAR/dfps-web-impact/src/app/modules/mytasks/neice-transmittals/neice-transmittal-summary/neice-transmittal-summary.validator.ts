import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export class NeiceTransmittalSummaryValidators {

    static validateTransmittalSummaryComment = (group: FormGroup): { [key: string]: any } => {
        if (group.controls) {
            const rejectHoldIndValue = group.controls.rejectHoldInd.value;
            const commentValue = group.controls.comment.value;
            if (rejectHoldIndValue && rejectHoldIndValue === 'R' && commentValue.trim() === '') {
                group.controls.comment.setValidators(NeiceTransmittalSummaryValidators.setErrorValidator({
                    transmittalSummaryRejectComment: true
                }));
            } else {
                group.controls.comment.clearValidators();
            }
            group.controls.comment.updateValueAndValidity({ onlySelf: true });
            return null;
        }
    }

    static validateAdditionalInfoComments = (group: FormGroup): { [key: string]: any } => {
        if (group.controls) {
            const additionalInformationValue = group.controls.additionalInformation.value;
            const commentsValue = group.controls.comments.value;
            if (additionalInformationValue && additionalInformationValue === '220' && commentsValue.trim() === '') {
                group.controls.comments.setValidators(NeiceTransmittalSummaryValidators.setErrorValidator({
                    addInfoCommentsReq: true
                }));
            } else {
                group.controls.comments.clearValidators();
            }
            group.controls.comments.updateValueAndValidity({ onlySelf: true });
            return null;
        }
    }

    static validateRejectInfo = (group: FormGroup): { [key: string]: any } => {
        if (group.controls) {
            const documentType = group.controls.documentType.value;
            const description = group.controls.description.value;
            const file = group.controls.file.value;
            
            if (documentType) {
                if (file.trim() === '') {
                    group.controls.file.setValidators(NeiceTransmittalSummaryValidators.setErrorValidator({
                        rejectFileReq: true
                    }));
                } else {
                    group.controls.file.clearValidators();
                }
                
                
                if (documentType === '75' && description.trim() === '') {
                    group.controls.description.setValidators(NeiceTransmittalSummaryValidators.setErrorValidator({
                        rejectDescriptionReq: true
                    }));
                } else {
                    group.controls.description.clearValidators();
                }
            }

            if (file && documentType.trim() === '') {
                group.controls.documentType.setValidators(NeiceTransmittalSummaryValidators.setErrorValidator({
                    rejectDocumentTypeReq: true
                }));
            } else {
                group.controls.documentType.clearValidators();
            }

            if (documentType.trim() === '' && file.trim() === '') {                
                group.controls.documentType.clearValidators();
                group.controls.description.clearValidators();
                group.controls.file.clearValidators();
            }
            group.controls.documentType.updateValueAndValidity({ onlySelf: true });
            group.controls.description.updateValueAndValidity({ onlySelf: true });
            group.controls.file.updateValueAndValidity({ onlySelf: true });
            return null;
        }
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}