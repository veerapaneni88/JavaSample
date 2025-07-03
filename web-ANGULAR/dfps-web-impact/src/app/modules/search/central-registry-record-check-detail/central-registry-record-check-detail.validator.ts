import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class CentralRegistryRecordCheckValidators {

    static dueProcessTrackingValidation = (group: FormGroup): { [key: string]: any } => {
        const certifiedMailTracking = group.controls.certifiedMailTracking.value;
        const soahMailed = group.controls.soahMailed.value;
        const soahDecisionDate = group.controls.soahDecisionDate.value;
        const dateResponseReceived = group.controls.dateResponseReceived.value;
        const pattern = /^[.\d ]+$/;

        if (!certifiedMailTracking) {
            group.controls.certifiedMailTracking.setValidators(
                CentralRegistryRecordCheckValidators.setErrorValidator({
                    required: { actualValue: certifiedMailTracking, customFieldName: 'Certified Mail Tracking #' }
                }));
        } 
        else if((certifiedMailTracking || certifiedMailTracking !== '') && !pattern.test(certifiedMailTracking)) {
            group.controls.certifiedMailTracking.setValidators(
                CentralRegistryRecordCheckValidators.setErrorValidator({
                    MSG_VALIDATE_NUMBER: { actualValue: certifiedMailTracking, customFieldName: 'Certified Mail Tracking #' }
                }));
            }
        else { group.controls.certifiedMailTracking.clearValidators(); }


        if (!soahMailed) {
            group.controls.soahMailed.setValidators(
                CentralRegistryRecordCheckValidators.setErrorValidator({
                    required: { actualValue: soahMailed, customFieldName: 'Date SOAH Offer Mailed' }
                }));
        } 
        else if ((soahMailed || soahMailed !== '') && DfpsCommonValidators.validateDate(group.controls.soahMailed)) {
            group.controls.soahMailed.setValidators(
                CentralRegistryRecordCheckValidators.setErrorValidator({
                    validateDate: { actualValue: soahMailed, customFieldName: 'Date SOAH Offer Mailed' }
                }));
        } else { group.controls.soahMailed.clearValidators(); }


        if ((soahDecisionDate || soahDecisionDate !== '') && DfpsCommonValidators.validateDate(group.controls.soahDecisionDate)) {
            group.controls.soahDecisionDate.setValidators(
                CentralRegistryRecordCheckValidators.setErrorValidator({
                    validateDate: { actualValue: soahDecisionDate, customFieldName: 'SOAH Decision Date' }
                }));
        } else { group.controls.soahDecisionDate.clearValidators(); }

        if ((dateResponseReceived || dateResponseReceived !== '') && DfpsCommonValidators.validateDate(group.controls.dateResponseReceived)) {
            group.controls.dateResponseReceived.setValidators(
                CentralRegistryRecordCheckValidators.setErrorValidator({
                    validateDate: { actualValue: dateResponseReceived, customFieldName: 'Date Response Received' }
                }));
        } else { group.controls.dateResponseReceived.clearValidators(); }


        group.controls.certifiedMailTracking.updateValueAndValidity({ onlySelf: true });
        group.controls.soahMailed.updateValueAndValidity({ onlySelf: true });
        group.controls.soahDecisionDate.updateValueAndValidity({ onlySelf: true });
        group.controls.dateResponseReceived.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}