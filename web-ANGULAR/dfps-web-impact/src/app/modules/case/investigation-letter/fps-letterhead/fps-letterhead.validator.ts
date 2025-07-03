import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { formatDate } from '@angular/common';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class FpsLetterheadValidators {

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
            const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[0-9]{10}$/;
            // replace '()' and '-' before checking for 10 digits in a phone number.
            if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim().replace(/[^+\d]+/g, ''))) {
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
                    group.controls.investigatorEmail.setErrors({MSG_EMAIL_ADDRESS_INVALID: {customFieldName: 'Email', actualValue: email}});
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
          group.controls.letterMethod.setValidators(FpsLetterheadValidators.setErrorValidator({
            selectFieldRequired: { customFieldName: 'Method' }
          }));
        }else {
          group.controls.letterMethod.clearValidators();
        }
 
        // Regular/Certified Mail Return Receipt Number
        if (['RCM', 'RCE', 'RCF', 'REF'].includes(letterMethod)) {
            const recipientNumber = group.controls.recipientNumber.value;
            if (!recipientNumber) {
                group.controls.recipientNumber.setValidators(FpsLetterheadValidators.setErrorValidator({
                    required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
                }));
            } else {
                group.controls.recipientNumber.clearValidators();
            }
            group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        } else {
            group.controls.recipientNumber.clearValidators();
        }

        if (!group.controls.administratorName.value ) {
          group.controls.administratorName.setValidators(FpsLetterheadValidators.setErrorValidator({
            required: { customFieldName: 'Name' }
          }));
        }else {
          group.controls.administratorName.clearValidators();
        }

        if (!group.controls.addressState.value ) {
          group.controls.addressState.setValidators(FpsLetterheadValidators.setErrorValidator({
            selectFieldRequired: { customFieldName: 'State' }
          }));
        }else {
          group.controls.addressState.clearValidators();
        }

        if (!group.controls.addressLine1.value ) {
            group.controls.addressLine1.setValidators(FpsLetterheadValidators.setErrorValidator({
              required: { customFieldName: 'Address Line 1' }
            }));
          }else {
            group.controls.addressLine1.clearValidators();
        }
        if (!group.controls.addressCity.value ) {
          group.controls.addressCity.setValidators(FpsLetterheadValidators.setErrorValidator({
            required: { customFieldName: 'City' }
          }));
        }else {
          group.controls.addressCity.clearValidators();
        }
        if (!group.controls.addressZip.value ) {
          group.controls.addressZip.setValidators(FpsLetterheadValidators.setErrorValidator({
            required: { customFieldName: 'Zip' }
          }));
        }else {
          group.controls.addressZip.clearValidators();
        }

        // Dear
        const dear = group.controls.letterBodyDear.value;
        if (!dear) {
            group.controls.letterBodyDear.setValidators(FpsLetterheadValidators.setErrorValidator({
                required: { customFieldName: 'Dear' }
            }));
        } else {
            group.controls.letterBodyDear.clearValidators();
        }
        group.controls.letterBodyDear.updateValueAndValidity({ onlySelf: true });

        // Body
        const body = group.controls.letterBody.value;
        if (!body) {
            group.controls.letterBody.setValidators(FpsLetterheadValidators.setErrorValidator({
                required: { customFieldName: 'Body' }
            }));
        } else {
            group.controls.letterBody.clearValidators();
        }
        group.controls.letterBody.updateValueAndValidity({ onlySelf: true });

        //investigator Designation
        const designation = group.controls.investigatorDesignation.value;
        if (!designation) {
            group.controls.investigatorDesignation.setValidators(FpsLetterheadValidators.setErrorValidator({
                required: { customFieldName: 'Designation' }
            }));
        } else {
            group.controls.investigatorDesignation.clearValidators();
        }

        //Investigator Phone Number
        const phoneNumber = group.controls.investigatorPhoneNumber.value;
        if (!phoneNumber) {
            group.controls.investigatorPhoneNumber.setValidators(FpsLetterheadValidators.setErrorValidator({
                required: { customFieldName: 'Phone Number' }
            }));
        } else {
            group.controls.investigatorPhoneNumber.clearValidators();
        }

        // Investigator Email
        const email = group.controls.investigatorEmail.value;
        if (!email) {
            group.controls.investigatorEmail.setValidators(FpsLetterheadValidators.setErrorValidator({
                required: { customFieldName: 'Email Address' }
            }));
        } else {
            group.controls.investigatorEmail.clearValidators();
        }
        
        group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });
        group.controls.letterMethod.updateValueAndValidity({ onlySelf: true });
        group.controls.administratorName.updateValueAndValidity({ onlySelf: true });
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });
        group.controls.addressCity.updateValueAndValidity({ onlySelf: true });
        group.controls.addressState.updateValueAndValidity({ onlySelf: true });
        group.controls.addressZip.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedChildsPersonId.updateValueAndValidity({ onlySelf: true });
        group.controls.investigatorDesignation.updateValueAndValidity({ onlySelf: true });
        group.controls.investigatorPhoneNumber.updateValueAndValidity({ onlySelf: true });
        group.controls.investigatorEmail.updateValueAndValidity({ onlySelf: true });

        return null;
    }

}
