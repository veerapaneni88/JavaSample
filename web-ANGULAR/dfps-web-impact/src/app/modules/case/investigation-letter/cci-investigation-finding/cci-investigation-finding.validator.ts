import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
export class CciInvestigationFindingValidators {



    static findingsCheck = (group: FormGroup): { [key: string]: any } => {
        const tableData = group.controls.tableData.value;
        if (!tableData || !tableData.length) {
            group.controls.tableData.setErrors({
                MSG_NO_ALLEGATION: {
                    hideFieldName: true,
                    actualValue: tableData
                }
            });
        }
        if (group.controls.investigatorFindings.value && 
            tableData && tableData.some((allegation) => allegation.allegationFinding?.some((x) => !x.finding))) {
            group.controls.tableData.setErrors({
                MSG_ALLEGATION_WITH_NO_FINDINGS: {
                    hideFieldName: true,
                    actualValue: tableData
                }
            });
        }
        return null;
    }


    static validateEmailPattern = (group: FormGroup): {[key: string]: any} =>{
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
            group.controls.letterMethod.setValidators(CciInvestigationFindingValidators.setErrorValidator({
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
                group.controls.recipientNumber.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                    required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
                }));
            } else {
                group.controls.recipientNumber.clearValidators();
            }
            group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });

        } else {
            group.controls.recipientNumber.clearValidators();
        }

        // Address 1
        const addressLine1 = group.controls.addressLine1.value;
        if (!addressLine1) {
            group.controls.addressLine1.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Address Line 1' }
            }));
        } else {
            group.controls.addressLine1.clearValidators();
        }
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });

        // City
        const addressCity = group.controls.addressCity.value;
        if (!addressCity) {
            group.controls.addressCity.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'City' }
            }));
        } else {
            group.controls.addressCity.clearValidators();
        }
        group.controls.addressCity.updateValueAndValidity({ onlySelf: true });

        // State
        const addressState = group.controls.addressState.value;
        if (!addressState) {
            group.controls.addressState.setValidators(CciInvestigationFindingValidators.setErrorValidator({
              selectFieldRequired: { customFieldName: 'State' }
            }));
        } else {
            group.controls.addressState.clearValidators();
        }
        group.controls.addressState.updateValueAndValidity({ onlySelf: true });

        // Zip
        const addressZip = group.controls.addressZip.value;
        if (!addressZip) {
            group.controls.addressZip.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Zip' }
            }));
        } else {
            group.controls.addressZip.clearValidators();
        }
        group.controls.addressZip.updateValueAndValidity({ onlySelf: true });

        // Dear
        const letterBodyDear = group.controls.letterBodyDear.value;
        if (!letterBodyDear) {
            group.controls.letterBodyDear.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Dear' }
            }));
        } else {
            group.controls.letterBodyDear.clearValidators();
        }
        group.controls.letterBodyDear.updateValueAndValidity({ onlySelf: true });

        // Operation Name
        const operationHomeName = group.controls.operationHomeName.value;
        if (!operationHomeName) {
            group.controls.operationHomeName.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Operation Name' }
            }));
        } else {
            group.controls.operationHomeName.clearValidators();
        }
        group.controls.operationHomeName.updateValueAndValidity({ onlySelf: true });

        // Operation Number
        const operationNumber = group.controls.operationNumber.value;
        if (!operationNumber) {
            group.controls.operationNumber.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Operation Number' }
            }));
        } else {
            group.controls.operationNumber.clearValidators();
        }
        group.controls.operationNumber.updateValueAndValidity({ onlySelf: true });


        //Options
        const option = group.controls.investigatorFindings.value;
        if(!option){
            group.controls.investigatorFindings.setErrors({
                MSG_INVESTIGATION_FINDINGS_CHECK: {
                    hideFieldName: true,
                    actualValue: option
                }
            });
        }else{
            group.controls.investigatorFindings.clearValidators();
        }
       // group.controls.investigatorFindings.updateValueAndValidity({ onlySelf: true });

        // investigator Name
        const investigatorName = group.controls.investigatorName.value;
        if (!investigatorName) {
            group.controls.investigatorName.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Investiagtor Name' }
            }));
        } else {
            group.controls.investigatorName.clearValidators();
        }
        group.controls.investigatorName.updateValueAndValidity({ onlySelf: true });

        // Designation
        const investigatorDesignation = group.controls.investigatorDesignation.value;
        if (!investigatorDesignation) {
            group.controls.investigatorDesignation.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Designation' }
            }));
        } else {
            group.controls.investigatorDesignation.clearValidators();
        }
        group.controls.investigatorDesignation.updateValueAndValidity({ onlySelf: true });

        // investigator Phone Number
        const investigatorPhoneNumber = group.controls.investigatorPhoneNumber.value;
        if (!investigatorPhoneNumber) {
            group.controls.investigatorPhoneNumber.setValidators(CciInvestigationFindingValidators.setErrorValidator({
                required: { customFieldName: 'Phone Number' }
            }));
        } else {
            group.controls.investigatorPhoneNumber.clearValidators();
        }
        group.controls.investigatorPhoneNumber.updateValueAndValidity({ onlySelf: true });

        // investigator Email
        const investigatorEmail = group.controls.investigatorEmail.value;
        if (!investigatorEmail) {
            group.controls.investigatorEmail.setValidators(CciInvestigationFindingValidators.setErrorValidator({
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
