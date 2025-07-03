import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { formatDate } from '@angular/common';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class HhscCaseTransferValidators {

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
                    group.controls.investigatorEmail.setErrors({MSG_EMAIL_ADDRESS_INVALID: {customFieldName: 'Email', emailAddressIsInvalid: true, actualValue: email}});
                }
            }
            return null;
        }
    }

    static requiredFieldValidations = (group: FormGroup): { [key: string]: any } => {

        // Method
        const letterMethod = group.controls.letterMethod.value;

        if (!letterMethod) {
            group.controls.letterMethod.setValidators(HhscCaseTransferValidators.setErrorValidator({
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
                group.controls.recipientNumber.setValidators(HhscCaseTransferValidators.setErrorValidator({
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

        return null;
    }

}
