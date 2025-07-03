import { FormGroup, AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class ParentNotificationOfInterviewValidators {

    static validateLetterInformation() {
        return (group: FormGroup): { [key: string]: any } => {
            const emailRegEx = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;

            if (!emailRegEx.test(group.controls.investigatorEmail.value)) {
                group.controls.investigatorEmail.setErrors({
                    emailAddressIsInvalid: {
                        customFieldName: 'Email',
                        emailAddressIsInvalid: true
                    }
                });
            }
            return null;
        }
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
            group.controls.letterMethod.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                selectFieldRequired: { customFieldName: 'Method' }
            }));
        } else {
            group.controls.letterMethod.clearValidators();
        }
        group.controls.letterMethod.updateValueAndValidity({ onlySelf: true });

        // Address City
        const city = group.controls.addressCity.value;
        if (!city) {
            group.controls.addressCity.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                required: { customFieldName: 'City' }
            }));
        } else {
            group.controls.addressCity.clearValidators();
        }
        group.controls.addressCity.updateValueAndValidity({ onlySelf: true });

        // Address Zip
        const zip = group.controls.addressZip.value;
        if (!zip) {
            group.controls.addressZip.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Zip' }
            }));
        } else {
            group.controls.addressZip.clearValidators();
        }
        group.controls.addressZip.updateValueAndValidity({ onlySelf: true });

        //address State
        const state = group.controls.addressState.value;
        if (!state) {
            group.controls.addressState.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
              selectFieldRequired: { customFieldName: 'State' }
            }));
        } else {
            group.controls.addressState.clearValidators();
        }
        group.controls.addressState.updateValueAndValidity({ onlySelf: true });

        // Address 1
        const addressLine1 = group.controls.addressLine1.value;
        if (!addressLine1) {
            group.controls.addressLine1.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Address Line 1' }
            }));
        } else {
            group.controls.addressLine1.clearValidators();
        }
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });

        // letter Body Dear
        const bodyDear = group.controls.letterBodyDear.value;
        if (!bodyDear) {
            group.controls.letterBodyDear.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Dear' }
            }));
        } else {
            group.controls.letterBodyDear.clearValidators();
        }
        group.controls.letterBodyDear.updateValueAndValidity({ onlySelf: true });

        // Operation Name
        const bodyOperationName = group.controls.fecilityId.value;
        if (bodyOperationName === '') {
            group.controls.fecilityId.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                selectFieldRequired: { customFieldName: 'Operation Name' }
            }));
        } else {
            group.controls.fecilityId.clearValidators();
        }
        group.controls.fecilityId.updateValueAndValidity({ onlySelf: true });

        // Regular/Certified Mail Return Receipt Number
        if (['RCM', 'RCE', 'RCF', 'REF'].includes(letterMethod)) {
            const recipientNumber = group.controls.recipientNumber.value;
            if (!recipientNumber) {
                group.controls.recipientNumber.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                    required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
                }));
            } else {
                group.controls.recipientNumber.clearValidators();
            }
            group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        } else {
            group.controls.recipientNumber.clearValidators();
        }

        // Parent Name
        const parentName = group.controls.administratorName.value;
        if (parentName === null || parentName === '' || parentName == undefined) {
            group.controls.administratorName.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Parent Name' }
            }));
        } else {
            group.controls.administratorName.clearValidators();
        }
        group.controls.administratorName.updateValueAndValidity({ onlySelf: true });

        // Child Name
        const childName = group.controls.childsFirstName.value;
        if (childName === null || childName === '' || childName == undefined) {
            group.controls.childsFirstName.setValidators(ParentNotificationOfInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Child\'s First Name' }
            }));
        } else {
            group.controls.childsFirstName.clearValidators();
        }
        group.controls.childsFirstName.updateValueAndValidity({ onlySelf: true });

        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

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
            const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[(][0-9]{3}[)]\s[0-9]{3}-[0-9]{4}$/;
            if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim())) {
                group.controls.investigatorPhoneNumber.setErrors({ MSG_PHONE_NUMBER: { customFieldName: 'Phone Number', actualValue: phoneNumber } });
            }
        }
        return null;
    }

    static dateFormatValidation = (group: FormGroup): { [key: string]: any } => {
        const bodyInterviewDate = group.controls.interviewDate;
        if(DfpsCommonValidators.validateDate(bodyInterviewDate)){
            group.controls.interviewDate.setErrors({validateDate: { actualValue: bodyInterviewDate.value}});
        }
        return null;
    }

}
