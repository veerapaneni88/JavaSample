import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class ApRightsPriorToInterviewValidators {


    static requiredFieldValidations = (group: FormGroup): { [key: string]: any } => {

        // Method
        const letterMethod = group.controls.letterMethod.value;
        if (!letterMethod) {
            group.controls.letterMethod.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
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
                group.controls.recipientNumber.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
                    required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
                }));
            } else {
                group.controls.recipientNumber.clearValidators();
            }
            group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        } else {
            group.controls.recipientNumber.clearValidators();
        }

        // First Name
        const apFirstName = group.controls.apFirstName.value;
        if (!apFirstName) {
            group.controls.apFirstName.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
                required: { customFieldName: 'First Name' }
            }));
        } else {
            group.controls.apFirstName.clearValidators();
        }
        group.controls.apFirstName.updateValueAndValidity({ onlySelf: true });

        // Last Name
        const apLastName = group.controls.apLastName.value;
        if (!apLastName) {
            group.controls.apLastName.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Last Name' }
            }));
        } else {
            group.controls.apLastName.clearValidators();
        }
        group.controls.apLastName.updateValueAndValidity({ onlySelf: true });

        // Phone Number
        const apHomePhoneNumber = group.controls.apHomePhoneNumber.value;
        if (!apHomePhoneNumber) {
            group.controls.apHomePhoneNumber.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Home Phone Number' }
            }));
        } else {
            group.controls.apHomePhoneNumber.clearValidators();
        }
        group.controls.apHomePhoneNumber.updateValueAndValidity({ onlySelf: true });

        // Zip
        const addressZip = group.controls.addressZip.value;
        if (!addressZip) {
            group.controls.addressZip.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Zip' }
            }));
        } else {
            group.controls.addressZip.clearValidators();
        }
        group.controls.addressZip.updateValueAndValidity({ onlySelf: true });

        // Home Street Address
        const apHomeStreetAddress = group.controls.addressLine1.value;
        if (!apHomeStreetAddress) {
            group.controls.addressLine1.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
                required: { customFieldName: 'Home Street Address Line 1' }
            }));
        } else {
            group.controls.addressLine1.clearValidators();
        }
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });

        // State
        const addressState = group.controls.addressState.value;
        if (!addressState) {
            group.controls.addressState.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
              selectFieldRequired: { customFieldName: 'State' }
            }));
        } else {
            group.controls.addressState.clearValidators();
        }
        group.controls.addressState.updateValueAndValidity({ onlySelf: true });

        // City
        const addressCity = group.controls.addressCity.value;
        if (!addressCity) {
            group.controls.addressCity.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
                required: { customFieldName: 'City' }
            }));
        } else {
            group.controls.addressCity.clearValidators();
        }
        group.controls.addressCity.updateValueAndValidity({ onlySelf: true });


        // County
        const county = group.controls.county.value;
        if (!county) {
            group.controls.county.setValidators(ApRightsPriorToInterviewValidators.setErrorValidator({
              selectFieldRequired: { customFieldName: 'County' }
            }));
        } else {
            group.controls.county.clearValidators();
        }
        group.controls.county.updateValueAndValidity({ onlySelf: true });



        return null;
    }

    static phoneNumberPattern = (group: FormGroup): { [key: string]: any } => {
        const phoneNumber = group.controls.apHomePhoneNumber.value;
        if (phoneNumber) {
            const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[(][0-9]{3}[)]\s[0-9]{3}-[0-9]{4}$/;
            if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim())) {
                group.controls.apHomePhoneNumber.setErrors({ MSG_PHONE_NUMBER: { actualValue: phoneNumber } });
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

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}
