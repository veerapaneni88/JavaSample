import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CostReimbursementDetailValidator {
    static validateInputFields = (group: FormGroup) => {
        const salary = group.controls.salaries.value;
        const frgBenft = group.controls.benefits.value;
        const travel = group.controls.travel.value;
        const supply = group.controls.supplies.value;
        const equip = group.controls.equipment.value;
        const other = group.controls.other.value;
        const adminAll = group.controls.administrative.value;
        const offset = group.controls.offsetItemReimbursement.value;
        const qty = group.controls.lineItemServicesQuantity.value;

        let unitRate = 0;
        if (qty > 0) {
            unitRate = (salary + frgBenft + travel + supply + equip + other + adminAll + offset) / qty;
        }

        if (unitRate > 9999.99) {
            group.controls.saveError.setErrors({
                SSM_FIN_CR_UNIT_RATE_MAX: { SSM_FIN_CR_UNIT_RATE_MAX: true, hideFieldName: true },
            });
        }

        const fieldNames = ['salaries', 'benefits', 'travel', 'supplies', 'equipment', 'other', 'administrative'];
        group.controls.postiveNegativeEntriesError.clearValidators();
        group.controls.negativeEntriesError.clearValidators();

        fieldNames.some((fieldName) => {
            if (!group.controls[fieldName].errors) {
                if (CostReimbursementDetailValidator.validatePostiveNegative(group, fieldName)
                    && CostReimbursementDetailValidator.validateNegativeEntry(group, fieldName)) {
                    group.controls.postiveNegativeEntriesError.setValidators(CostReimbursementDetailValidator.setPositiveNegativeError());
                    group.controls.negativeEntriesError.setValidators(CostReimbursementDetailValidator.setNegativeError());
                } else if (CostReimbursementDetailValidator.validatePostiveNegative(group, fieldName)) {
                    group.controls.postiveNegativeEntriesError.setValidators(CostReimbursementDetailValidator.setPositiveNegativeError());
                } else if (CostReimbursementDetailValidator.validateNegativeEntry(group, fieldName)) {
                    group.controls.negativeEntriesError.setValidators(CostReimbursementDetailValidator.setNegativeError());
                }
            }
        });
        group.controls.saveError.updateValueAndValidity({ onlySelf: true });
        group.controls.postiveNegativeEntriesError.updateValueAndValidity({ onlySelf: true });
        group.controls.negativeEntriesError.updateValueAndValidity({ onlySelf: true });
        return null;
    };

    static validatePostiveNegative(group: FormGroup, controlName: string): boolean {
        const qty = group.controls.lineItemServicesQuantity.value;
        return ((qty > 0 && CostReimbursementDetailValidator.checkFieldValueLessThanZero(group, controlName))
            || (qty < 0 && CostReimbursementDetailValidator.checkFieldValueGreaterThanZero(group, controlName))
        )
    }

    static validateNegativeEntry(group: FormGroup, controlName: string): boolean {
        const adjustment = group.controls.adjustment.value;
        const lineItemType = group.controls.lineItemServicesQuantity.value  >= 0 ? 'A' : 'R';
        return ((adjustment === 'N' || lineItemType === 'A')
            && CostReimbursementDetailValidator.checkFieldValueLessThanZero(group, controlName));
    }

    static checkFieldValueLessThanZero(group: FormGroup, controlName: string): any {
        return group.controls[controlName].value < 0;
    }

    static checkFieldValueGreaterThanZero(group: FormGroup, controlName: string): any {
        return group.controls[controlName].value > 0;
    }

    static setPositiveNegativeError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                SSM_FIN_NO_POS_NEG_AMTS: {
                    hideFieldName: true,
                    SSM_FIN_NO_POS_NEG_AMTS: true
                }
            };
        };
    }

    static setNegativeError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                SSM_FIN_NO_NEGTVE_ENTRY: {
                    hideFieldName: true,
                    SSM_FIN_NO_NEGTVE_ENTRY: true
                }
            };
        };
    }
}
