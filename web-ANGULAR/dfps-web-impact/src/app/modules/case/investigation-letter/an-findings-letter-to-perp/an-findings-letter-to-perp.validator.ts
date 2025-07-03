import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class AnFindingsLetterToPerpValidators {

    static findingsCheck = (group: FormGroup): { [key: string]: any } => {
        const tableData = group.controls.tableData.value;
        if (!tableData || !tableData.length) {
            group.controls.tableData.setErrors({
                MSG_ALLEGATION_DETAIL: {
                    hideFieldName: true,
                    actualValue: tableData
                }
            });
        }
        if (tableData && tableData.some((x) => !x.finding)) {
            group.controls.tableData.setErrors({
                MSG_ALLEGATION_WITH_NO_FINDINGS: {
                    hideFieldName: true,
                    actualValue: tableData
                }
            });
        }
        return null;
    }

    static validateEmailPattern() {
        return (group: FormGroup): { [key: string]: any } => {
            const emailRegEx = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
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

    static requiredFieldValidations = (group: FormGroup): { [key: string]: any } => {

        // Method
        const letterMethod = group.controls.letterMethod.value;
        if (!letterMethod) {
            group.controls.letterMethod.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
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
                group.controls.recipientNumber.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                    required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
                }));
            } else {
                group.controls.recipientNumber.clearValidators();
            }
            group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        } else {
            group.controls.recipientNumber.clearValidators();
        }

        // alleged Perpetrator Name
        const allegedPerpetratorName = group.controls.allegedPerpetratorName.value;
        if (!allegedPerpetratorName) {
            group.controls.allegedPerpetratorName.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Alleged Perpetrator' }
            }));
        } else {
            group.controls.allegedPerpetratorName.clearValidators();
        }
        group.controls.allegedPerpetratorName.updateValueAndValidity({ onlySelf: true });

        // Address 1
        const addressLine1 = group.controls.addressLine1.value;
        if (!addressLine1) {
            group.controls.addressLine1.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Address Line 1' }
            }));
        } else {
            group.controls.addressLine1.clearValidators();
        }
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });

        // City
        const addressCity = group.controls.addressCity.value;
        if (!addressCity) {
            group.controls.addressCity.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'City' }
            }));
        } else {
            group.controls.addressCity.clearValidators();
        }
        group.controls.addressCity.updateValueAndValidity({ onlySelf: true });

        // State
        const addressState = group.controls.addressState.value;
        if (!addressState) {
            group.controls.addressState.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
              selectFieldRequired: { customFieldName: 'State' }
            }));
        } else {
            group.controls.addressState.clearValidators();
        }
        group.controls.addressState.updateValueAndValidity({ onlySelf: true });

        // Zip
        const addressZip = group.controls.addressZip.value;
        if (!addressZip) {
            group.controls.addressZip.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Zip' }
            }));
        } else {
            group.controls.addressZip.clearValidators();
        }
        group.controls.addressZip.updateValueAndValidity({ onlySelf: true });

        // Dear
        const letterBodyDear = group.controls.letterBodyDear.value;
        if (!letterBodyDear) {
            group.controls.letterBodyDear.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Dear' }
            }));
        } else {
            group.controls.letterBodyDear.clearValidators();
        }
        group.controls.letterBodyDear.updateValueAndValidity({ onlySelf: true });

        // Operation Name
        const bodyOperationName = group.controls.bodyOperationName.value;
        if (!bodyOperationName) {
            group.controls.bodyOperationName.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                selectFieldRequired: { customFieldName: 'Operation Name' }
            }));
        } else {
            group.controls.bodyOperationName.clearValidators();
        }
        group.controls.bodyOperationName.updateValueAndValidity({ onlySelf: true });

        // admin Review Staff Details
        const adminReviewStaffDetails = group.controls.adminReviewStaffDetails.value;
        if (!adminReviewStaffDetails) {
            group.controls.adminReviewStaffDetails.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Administrative Review Staff Information' }
            }));
        } else {
            group.controls.adminReviewStaffDetails.clearValidators();
        }
        group.controls.adminReviewStaffDetails.updateValueAndValidity({ onlySelf: true });

        // investigator Name
        const investigatorName = group.controls.investigatorName.value;
        if (!investigatorName) {
            group.controls.investigatorName.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Investigator Name' }
            }));
        } else {
            group.controls.investigatorName.clearValidators();
        }
        group.controls.investigatorName.updateValueAndValidity({ onlySelf: true });

        // Designation
        const investigatorDesignation = group.controls.investigatorDesignation.value;
        if (!investigatorDesignation) {
            group.controls.investigatorDesignation.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Designation' }
            }));
        } else {
            group.controls.investigatorDesignation.clearValidators();
        }
        group.controls.investigatorDesignation.updateValueAndValidity({ onlySelf: true });

        // investigator Phone Number
        const investigatorPhoneNumber = group.controls.investigatorPhoneNumber.value;
        if (!investigatorPhoneNumber) {
            group.controls.investigatorPhoneNumber.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
                required: { customFieldName: 'Phone Number' }
            }));
        } else {
            group.controls.investigatorPhoneNumber.clearValidators();
        }
        group.controls.investigatorPhoneNumber.updateValueAndValidity({ onlySelf: true });

        // investigator Email
        const investigatorEmail = group.controls.investigatorEmail.value;
        if (!investigatorEmail) {
            group.controls.investigatorEmail.setValidators(AnFindingsLetterToPerpValidators.setErrorValidator({
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
