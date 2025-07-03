import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class AddressDetailValidators {

    static businessTypeValidation = (group: FormGroup): { [key: string]: any } => {
        const addressType = group.controls.addressType.value;
        const vendorId = group.controls.vendorId.value;
        const addressTypeBusiness = group.controls.addressTypeBusiness.value;
        if (addressTypeBusiness === '02' && addressType !== '02') {
            group.controls.addressTypeBusiness.setValidators(
                AddressDetailValidators.setErrorValidator({
                    addressTypeBusiness: {
                        actualValue: addressTypeBusiness, hideFieldName: true
                    }
                }));
        } else {
            group.controls.addressTypeBusiness.clearValidators();
        }

        group.controls.addressTypeBusiness.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static poboxValidation = (group: FormGroup): { [key: string]: any } => {

        const addressLine1 = group.controls.addressLine1.value;
        const addressType = group.controls.addressType.value;
        if (!addressLine1) {

            group.controls.addressLine1.setValidators(AddressDetailValidators.setErrorValidator({

                required: { customFieldName: 'Address Line 1' }

            }));

        }else if (addressLine1 && addressType === '01') {
            group.controls.addressLine1.setValidators(AddressDetailValidators.poboxPattern('Address Ln 1'));
        } else {
            group.controls.addressLine1.clearValidators();
        }
        group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }


    static attentionPattern(fieldName?: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value) {
                const ATTENTION_PATTERN = /^[a-zA-Z_\\\/.’'-]+(?: +[A-Za-z_\\\/.’'-]+)*$/;
                if (!ATTENTION_PATTERN.test(control.value)) {
                    return {
                        attentionPattern: {
                            actualValue: control.value,
                            customFieldName: fieldName,
                        },
                    };
                }
                return null;
            } else {
                return null;
            }
        };
    }

    static poboxPattern(fieldName?: string): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            const POBOX_PATTERN = /\b(?:p\.?\s*o\.?|post\s+office)\s+box\b/i;
            if (control.value && POBOX_PATTERN.test(control.value)) {
                return {
                    poboxPattern: {
                        actualValue: control.value,
                        customFieldName: fieldName,
                    },
                };
            } else {
                return null;
            }
        };
    }

}