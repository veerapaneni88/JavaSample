import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class InjuryDetailValidators {

    static saveValidaion() {
        return (group: FormGroup): { [key: string]: any } => {
            if (group.controls.abuseNeglect.value === 'Y' && (group.controls.allegations.value == null ||
                 group.controls.allegations.value.length <= 0)) {
                group.controls.allegations.setValidators(InjuryDetailValidators.setError());
            } else {
                group.controls.allegations.setValidators(null);
            }
            group.controls.allegations.updateValueAndValidity({ onlySelf: true });
            if(group.controls.injuryCausedBy.value != 'OTH' && group.controls.injuryCausedBy.value !=''
                && group.controls.injuryCausedBy.value != undefined
                && (!group.controls.injuryRelatedTo.value || group.controls.injuryRelatedTo.value.length === 0))
            {
                group.controls.injuryRelatedTo.setValidators( InjuryDetailValidators.setErrorValidator({
                  required: { customFieldName: 'Injury Related To ' }}));
            }else {
              group.controls.injuryRelatedTo.clearValidators();
            }
            group.controls.injuryRelatedTo.updateValueAndValidity({ onlySelf: true });

          return null;
        };
    }

    static validateFutureDate(control: AbstractControl): ValidationErrors | null {
        if (!DfpsCommonValidators.validateDate(control) && new Date(control.value) > new Date()) {
            return {
                validateFutureDate: true,
            };
        } else {
            return {};
        }
    }

    static setError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                allegationRequired: {
                    hideFieldName: true,
                    allegationRequired: true
                }
            };
        };
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static customRequiredValidation = (group: FormGroup): { [key: string]: any } => {
        
        if (group.controls.injuryType.value.length === 0) {
            group.controls.injuryType.setValidators(InjuryDetailValidators.setErrorValidator({
                required: { customFieldName: 'Type of Injury' }
            }));
        } else {
            group.controls.injuryType.clearValidators();
        }
        group.controls.injuryType.updateValueAndValidity({ onlySelf: true });
        
        if (!group.controls.abuseNeglect.value) {
            group.controls.abuseNeglect.setValidators(InjuryDetailValidators.setErrorValidator({
                required: { customFieldName: 'Injury Related to Abuse/Neglect' }
            }));
        } else {
            group.controls.abuseNeglect.clearValidators();
        }
        group.controls.abuseNeglect.updateValueAndValidity({ onlySelf: true });

        return null;
    }

    static conditionallyRequiredValidation = (group: FormGroup): { [key: string]: any } => {

        const injuryType = group.controls.injuryType.value;
        const otherTypeInjury = group.controls.otherTypeInjury.value;
        const injuryCausedBy = group.controls.injuryCausedBy.value;
        const otherInjuryRelatedTo = group.controls.otherInjuryRelatedTo.value;

        if (injuryType.includes('OTH') && !otherTypeInjury) {
            group.controls.otherTypeInjury.setValidators(InjuryDetailValidators.setErrorValidator({
                required: { customFieldName: 'Other Type of Injury' }
            }));
        } else {
            group.controls.otherTypeInjury.clearValidators();
        }
        group.controls.otherTypeInjury.updateValueAndValidity({ onlySelf: true });

        if ((injuryCausedBy === 'OTH' || group.controls.injuryRelatedTo.value.includes('OTR')) && !otherInjuryRelatedTo) {
            group.controls.otherInjuryRelatedTo.setValidators(InjuryDetailValidators.setErrorValidator({
                required: { customFieldName: 'Other Injury Related To' }
            }));
        } else {
            group.controls.otherInjuryRelatedTo.clearValidators();
        }
        group.controls.otherInjuryRelatedTo.updateValueAndValidity({ onlySelf: true });

        if (group.controls.abuseNeglect.value === 'Y' && (group.controls.allegedPerpetrator.value === '' || group.controls.allegedPerpetrator.value === undefined)) {
            group.controls.allegedPerpetrator.setValidators(InjuryDetailValidators.setErrorValidator({
                required: { customFieldName: 'Alleged Perpetrator' }
            }));
        } else if (group.controls.victim.value === group.controls.allegedPerpetrator.value) {
            group.controls.allegedPerpetrator.setValidators(
                InjuryDetailValidators.setErrorValidator(
                    { MSG_ALLEGED_PERPETRATOR_SAME_AS_VICTIM: { hideFieldName: false }
            }));
        } else {
            group.controls.allegedPerpetrator.clearValidators();
        }
        group.controls.allegedPerpetrator.updateValueAndValidity({ onlySelf: true });

        return null;
    }
}
