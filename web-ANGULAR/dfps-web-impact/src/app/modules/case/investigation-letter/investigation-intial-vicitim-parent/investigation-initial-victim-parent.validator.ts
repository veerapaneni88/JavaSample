import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export class InvestigationInitialVictimParentValidator {

    static validateEmailPattern() {
        return (group: FormGroup): { [key: string]: any } => {
            let emailRegEx = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
            const email = group.controls.investigatorEmail.value;
            if (email && !emailRegEx.test(email)) {
                group.controls.investigatorEmail.setErrors({
                    MSG_EMAIL_ADDRESS_INVALID: {
                        customFieldName: 'Email',
                        emailAddressIsInvalid: true,
                        actualValue: email
                    }
                });
            }
            return null;
        }
    }

    static phoneNumberPattern = (group: FormGroup): { [key: string]: any } => {
        const phoneNumber = group.controls.investigatorPhoneNumber.value;
        if (phoneNumber) {
          const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[0-9]{10}$/;
          if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim().replace(/[^+\d]+/g, ''))) {
            group.controls.investigatorPhoneNumber.setErrors({ MSG_PHONE_NUMBER: { customFieldName: 'Phone Number', actualValue: phoneNumber } });
          }
        }
        return null;
      }

      static zipPattern = (group: FormGroup): { [key: string]: any } => {
        const addressZip = group.controls.addressZip.value;
        if (addressZip) {
          const ZIP_PATTERN =  /[0-9]{5}/;
          if (!ZIP_PATTERN.test(addressZip)) {
            group.controls.addressZip.setErrors({ addressZipPattern: { 
                customFieldName: 'Zip',
                actualValue: addressZip } });
          }
        }
        return null;
      }


    static requiredFieldValidations = (group: FormGroup): { [key: string]: any } => {

        // Method
        const letterMethod = group.controls.letterMethod.value;
        if (!letterMethod) {
            group.controls.letterMethod.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
              selectFieldRequired: { customFieldName: 'Method' }
            }));
        } else {
            group.controls.letterMethod.clearValidators();
        }
        group.controls.letterMethod.updateValueAndValidity({ onlySelf: true });

        // Regular/Certified Mail Return Receipt Number
        if (['RCM', 'RCE', 'RCF', 'REF'].includes(letterMethod)) {
            const recipientNumber = group.controls.recipientNumber.value;
            if (!recipientNumber) {
                group.controls.recipientNumber.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                  required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
                }));
            } else {
                group.controls.recipientNumber.clearValidators();
            }
            //group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        } else {
            group.controls.recipientNumber.clearValidators();
        }
        group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        // Parent Name
        const administratorName = group.controls.administratorName.value;
        if (!administratorName) {
            group.controls.administratorName.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Parent Name' }
            }));
        } else {
            group.controls.administratorName.clearValidators();
        }
        group.controls.administratorName.updateValueAndValidity({ onlySelf: true });

        // Address 1
        const addressLine1 = group.controls.addressLine1.value;
        if (!addressLine1) {
            group.controls.addressLine1.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Address Line 1' }
            }));
        } else {
            group.controls.addressLine1.clearValidators();
        }
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });

        // City
        const addressCity = group.controls.addressCity.value;
        if (!addressCity) {
            group.controls.addressCity.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'City' }
            }));
        } else {
            group.controls.addressCity.clearValidators();
        }
        group.controls.addressCity.updateValueAndValidity({ onlySelf: true });

        // State
        const addressState = group.controls.addressState.value;
        if (!addressState) {
            group.controls.addressState.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
              selectFieldRequired: { customFieldName: 'State' }
            }));
        } else {
            group.controls.addressState.clearValidators();
        }
        group.controls.addressState.updateValueAndValidity({ onlySelf: true });

        // Zip
        const addressZip = group.controls.addressZip.value;
        if (!addressZip) {
            group.controls.addressZip.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Zip' }
            }));
        } else {
            group.controls.addressZip.clearValidators();
        }
        group.controls.addressZip.updateValueAndValidity({ onlySelf: true });

        // Dear
        const letterBodyDear = group.controls.letterBodyDear.value;
        if (!letterBodyDear) {
            group.controls.letterBodyDear.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Dear' }
            }));
        } else {
            group.controls.letterBodyDear.clearValidators();
        }
        group.controls.letterBodyDear.updateValueAndValidity({ onlySelf: true });

        // Operation Name
        const bodyOperationName = group.controls.bodyOperationName.value;
        if (!bodyOperationName) {
            group.controls.bodyOperationName.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
              selectFieldRequired: { customFieldName: 'Operation Name' }
            }));
        } else {
            group.controls.bodyOperationName.clearValidators();
        }
        group.controls.bodyOperationName.updateValueAndValidity({ onlySelf: true });

         // interview status
         const interviewStatus = group.controls.interviewStatus.value;
         if (!interviewStatus) {
             group.controls.interviewStatus.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
               selectFieldRequired: { customFieldName: 'Interview Status' }
             }));
         } else {
             group.controls.interviewStatus.clearValidators();
         }
         group.controls.interviewStatus.updateValueAndValidity({ onlySelf: true });

        // investigator Name
        const investigatorName = group.controls.investigatorName.value;
        if (!investigatorName) {
            group.controls.investigatorName.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Investigator Name' }
            }));
        } else {
            group.controls.investigatorName.clearValidators();
        }
        group.controls.investigatorName.updateValueAndValidity({ onlySelf: true });

        // Designation
        const investigatorDesignation = group.controls.investigatorDesignation.value;
        if (!investigatorDesignation) {
            group.controls.investigatorDesignation.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Designation' }
            }));
        } else {
            group.controls.investigatorDesignation.clearValidators();
        }
        group.controls.investigatorDesignation.updateValueAndValidity({ onlySelf: true });

        // investigator Phone Number
        const investigatorPhoneNumber = group.controls.investigatorPhoneNumber.value;
        if (!investigatorPhoneNumber) {
            group.controls.investigatorPhoneNumber.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Phone Number' }
            }));
        } else {
            group.controls.investigatorPhoneNumber.clearValidators();
        }
        group.controls.investigatorPhoneNumber.updateValueAndValidity({ onlySelf: true });

        // investigator Email
        const investigatorEmail = group.controls.investigatorEmail.value;
        if (!investigatorEmail) {
            group.controls.investigatorEmail.setValidators(InvestigationInitialVictimParentValidator.setErrorValidator({
                required: { customFieldName: 'Email Address' }
            }));
        } else {
            group.controls.investigatorEmail.clearValidators();
        }
        group.controls.investigatorEmail.updateValueAndValidity({ onlySelf: true });

        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}
