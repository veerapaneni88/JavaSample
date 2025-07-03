import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { formatDate } from '@angular/common';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class CciReporterLetterValidators {

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static validateLetterInformation = (group: FormGroup): { [key: string]: any } => {
        const buttonClicked = group.controls.buttonClicked.value;

        if  (buttonClicked === 'save') {
        } else if (buttonClicked === 'submit') {
        }
        return null;
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

    static validateEmailPattern() {
        return (group: FormGroup): { [key: string]: any } => {
            const email = group.controls.investigatorEmail.value;
            if (email) {
                const emailRegEx = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
                if (!emailRegEx.test(email)) {
                    group.controls.investigatorEmail.setErrors({
                      MSG_EMAIL_ADDRESS_INVALID: {
                        customFieldName: 'Email',
                        emailAddressIsInvalid: true,
                        actualValue: email
                      }
                    });
                }
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
          group.controls.letterMethod.setValidators(CciReporterLetterValidators.setErrorValidator({
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
                group.controls.recipientNumber.setValidators(CciReporterLetterValidators.setErrorValidator({
                    required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
                }));
            } else {
                group.controls.recipientNumber.clearValidators();
            }
            group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        } else {
            group.controls.recipientNumber.clearValidators();
        }
        group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        // Reporter Name
        const reporterName = group.controls.administratorName.value;
        if (!reporterName) {
            group.controls.administratorName.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Reporter Name' }
            }));
        } else {
            group.controls.administratorName.clearValidators();
        }
        group.controls.administratorName.updateValueAndValidity({ onlySelf: true });

        // City
        const addrLine1 = group.controls.addressLine1.value;
        if (!addrLine1) {
            group.controls.addressLine1.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Address Line 1' }
            }));
        } else {
            group.controls.addressLine1.clearValidators();
        }
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });


        // City
        const city = group.controls.addressCity.value;
        if (!city) {
            group.controls.addressCity.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'City' }
            }));
        } else {
            group.controls.addressCity.clearValidators();
        }
        group.controls.addressCity.updateValueAndValidity({ onlySelf: true });

        // State
        const addressState = group.controls.addressState.value;
        if (!addressState) {
            group.controls.addressState.setValidators(CciReporterLetterValidators.setErrorValidator({
              selectFieldRequired: { customFieldName: 'State' }
            }));
        } else {
            group.controls.addressState.clearValidators();
        }
        group.controls.addressState.updateValueAndValidity({ onlySelf: true });

        // City
        const zip = group.controls.addressZip.value;
        if (!zip) {
            group.controls.addressZip.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Zip' }
            }));
        } else {
            group.controls.addressZip.clearValidators();
        }
        group.controls.addressZip.updateValueAndValidity({ onlySelf: true });

        // Dear
        const dear = group.controls.letterBodyDear.value;
        if (!dear) {
            group.controls.letterBodyDear.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Dear' }
            }));
        } else {
            group.controls.letterBodyDear.clearValidators();
        }
        group.controls.letterBodyDear.updateValueAndValidity({ onlySelf: true });

        // Operation Name
        const operationName = group.controls.bodyOperationName.value;
        if (!operationName) {
            group.controls.bodyOperationName.setValidators(CciReporterLetterValidators.setErrorValidator({
                selectFieldRequired: { customFieldName: 'Operation Name' }
            }));
        } else {
            group.controls.bodyOperationName.clearValidators();
        }
        group.controls.bodyOperationName.updateValueAndValidity({ onlySelf: true });

        //investigator Name
        const investigatorName = group.controls.investigatorName.value;
        if (!investigatorName) {
            group.controls.investigatorName.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Investigator Name' }
            }));
        } else {
            group.controls.investigatorName.clearValidators();
        }
        group.controls.investigatorName.updateValueAndValidity({ onlySelf: true });


        //investigator Designation
        const designation = group.controls.investigatorDesignation.value;
        if (!designation) {
            group.controls.investigatorDesignation.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Designation' }
            }));
        } else {
            group.controls.investigatorDesignation.clearValidators();
        }
        group.controls.investigatorDesignation.updateValueAndValidity({ onlySelf: true });


        //Investigator Phone Number
        const phoneNumber = group.controls.investigatorPhoneNumber.value;
        if (!phoneNumber) {
            group.controls.investigatorPhoneNumber.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Phone Number' }
            }));
        } else {
            group.controls.investigatorPhoneNumber.clearValidators();
        }
        group.controls.investigatorPhoneNumber.updateValueAndValidity({ onlySelf: true });


        // Investigator Email
        const email = group.controls.investigatorEmail.value;
        if (!email) {
            group.controls.investigatorEmail.setValidators(CciReporterLetterValidators.setErrorValidator({
                required: { customFieldName: 'Email Address' }
            }));
        } else {
            group.controls.investigatorEmail.clearValidators();
        }
        group.controls.investigatorEmail.updateValueAndValidity({ onlySelf: true });

        return null;
    }

}
