import { AbstractControl, FormGroup, ValidationErrors } from '@angular/forms';

export class FinancialAccountDetailValidators {
    static validationConstraints = (group: FormGroup): { [key: string]: any } => {
        const NUMBER_PATTERN = /^[0-9]+$/;
        const NUMBER_PATTERN_ZEROS = /^[0]+$/;
        if (group.controls.formAction.value === 'VALIDATE') {
            if (!group.controls.personId.value || group.controls.personId.value.length === 0) {
                group.controls.personId.setErrors({ required: true });
            }
            if (
                group.controls.personId.value &&
                (NUMBER_PATTERN_ZEROS.test(group.controls.personId.value) ||
                    !NUMBER_PATTERN.test(group.controls.personId.value))
            ) {
                group.controls.personId.setErrors({
                    finanAcctValidateConstraintPersonId: {
                        actualValue: group.controls.personId.value,
                    },
                });
            }
            if (group.controls.program.value && group.controls.program.value === 'CPS') {
                if (!group.controls.personId.value || group.controls.personId.value.length === 0) {
                    group.controls.personId.setErrors({ cpsProgramConstraint: {actualValue: 'Person Id'} });
                }
                if (!group.controls.type.value || group.controls.type.value.length === 0) {
                    group.controls.type.setErrors({
                        cpsProgramConstraint: {
                            customFieldName: 'Account Type',
                            actualValue: 'Account Type'
                        },
                    });
                }
                if (!group.controls.accountNumber.value || group.controls.accountNumber.value.trim().length === 0) {
                    group.controls.accountNumber.setErrors({ cpsProgramConstraint: {actualValue: 'Account Number'} });
                }
                if (
                    group.controls.accountNumber.value &&
                    (NUMBER_PATTERN_ZEROS.test(group.controls.accountNumber.value) ||
                        !NUMBER_PATTERN.test(group.controls.accountNumber.value))
                ) {
                    group.controls.accountNumber.setErrors({
                        finanAcctSaveConstraintAcctNumber: {
                            actualValue: group.controls.accountNumber.value,
                        },
                    });
                }
            }
        }
        return null;
    };

    static saveConstraints(isNew)  {
        return(group: FormGroup): { [key: string]: any } =>{
        const NUMBER_PATTERN = /^[0-9]+$/;
        const NUMBER_PATTERN_ZEROS = /^[0]+$/;
        if (group.controls.formAction.value && group.controls.formAction.value === 'SAVE') {
            if(isNew && (!group.controls.validate.value)){
                group.controls.validate.setErrors({ MSG_VALIDATE_SAVE: true });
            }
            if (!group.controls.personId.value || group.controls.personId.value.length === 0) {
                group.controls.personId.setErrors({ required: true });
            }
            if (
                group.controls.personId.value &&
                (NUMBER_PATTERN_ZEROS.test(group.controls.personId.value) ||
                    !NUMBER_PATTERN.test(group.controls.personId.value))
            ) {
                group.controls.personId.setErrors({
                    finanAcctValidateConstraintPersonId: {
                        actualValue: group.controls.personId.value,
                    },
                });
            }
            if (!group.controls.type.value || group.controls.type.value.length === 0) {
                group.controls.type.setErrors({
                    required: {
                        customFieldName: 'Account Type',
                    },
                });
            }
            if (!group.controls.accountNumber.value || group.controls.accountNumber.value.trim().length === 0) {
                group.controls.accountNumber.setErrors({ required: true });
            }
            if (
                group.controls.accountNumber.value &&
                (NUMBER_PATTERN_ZEROS.test(group.controls.accountNumber.value) ||
                    !NUMBER_PATTERN.test(group.controls.accountNumber.value))
            ) {
                group.controls.accountNumber.setErrors({
                    finanAcctSaveConstraintAcctNumber: {
                        actualValue: group.controls.accountNumber.value,
                    },
                });
            }
            if (group.controls.institutionName.enabled && (!group.controls.institutionName.value ||
              group.controls.institutionName.value.trim().length === 0)) {
                group.controls.institutionName.setErrors({
                    required: {
                        customFieldName: 'Name',
                    }
                });
            }
            if (group.controls.zip.disabled) {
              group.controls.zip.setErrors(null);
            }
        }
        return null;
    };
}

    static phoneNumberPattern = (group: FormGroup): { [key: string]: any } => {
        const phoneNumber = group.controls.phone.value;
        if (phoneNumber) {
            const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[0-9]{10}$/;
            // replace '()' and '-' before checking for 10 digits in a phone number.
            if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim().replace(/[^+\d]+/g, ''))) {
                group.controls.phone.setErrors({phoneNumberPattern: {actualValue: phoneNumber}});
            }
        }
        return null;
    }

}
