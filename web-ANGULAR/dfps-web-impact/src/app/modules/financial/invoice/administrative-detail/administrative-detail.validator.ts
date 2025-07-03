import { DfpsCommonValidators } from 'dfps-web-lib';
import { AbstractControl, ValidatorFn, ValidationErrors, FormGroup } from '@angular/forms';
export class ADMValidators {

    static validateCsli(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (control.value) {
                if (!(/^\d+$/.test(control.value))) {
                    return {
                        csliValidationMsg: {
                            customFieldName: 'CSLI',
                            csliValidationMsg: true,
                            actualValue: control.value,
                        }
                    };
                }
            }

        };
    }


    static validateOffsetSum() {
        return (group: FormGroup): { [key: string]: any } => {
            const negativeAmountError = group.controls.negativeAmountError;
            const offsetAmountError = group.controls.offsetAmountError;
            const errorMsg = group.controls.errorMsg;
            const reversal = group.controls.isReversal;
            const salaryAmount = group.controls.salaries;
            const benefitsAmount = group.controls.benefits;
            const travelAmount = group.controls.travel;
            const suppliesAmount = group.controls.supplies;
            const equipmentAmount = group.controls.equipment;
            const otherAmount = group.controls.other;
            const administrativeAmount = group.controls.administrative;
            const offsetAmount = group.controls.offset;
            // If this is not a reversal, none of the fields can be less than 0
            if (reversal.value != null && !(reversal.value) &&
                (Number(salaryAmount.value) < 0 || Number(benefitsAmount.value) < 0 ||
                    Number(travelAmount.value) < 0 || Number(suppliesAmount.value) < 0 ||
                    Number(equipmentAmount.value) < 0 || Number(otherAmount.value) < 0 ||
                    Number(administrativeAmount.value) < 0 || Number(offsetAmount.value) < 0)) {
                negativeAmountError.setErrors({ negativeAmount: { negativeAmount: true, hideFieldName: true } });
            } else {
                negativeAmountError.setErrors({});
            }
            // Postive and Negative amounts cannot be entered on the same admin detail
            if ((Number(salaryAmount.value) < 0 || Number(benefitsAmount.value) < 0 ||
                Number(travelAmount.value) < 0 || Number(suppliesAmount.value) < 0 ||
                Number(equipmentAmount.value) < 0 || Number(otherAmount.value) < 0 ||
                Number(administrativeAmount.value) < 0 || Number(offsetAmount.value) < 0) &&
                (Number(salaryAmount.value) > 0 || Number(benefitsAmount.value) > 0 ||
                    Number(travelAmount.value) > 0 || Number(suppliesAmount.value) > 0 ||
                    Number(equipmentAmount.value) > 0 || Number(otherAmount.value) > 0 ||
                    Number(administrativeAmount.value) > 0 || Number(offsetAmount.value) > 0)) {
                errorMsg.setErrors({ SSM_FIN_NO_POS_NEG_AMTS: { SSM_FIN_NO_POS_NEG_AMTS: true, hideFieldName: true } });
            } else {
                errorMsg.setErrors({});
            }
            if (reversal.value != null && !(reversal.value)) {
                // If it is not a reversal, Make sure that the categories
                // do not total greater than Item total.
                if (Number(offsetAmount.value) >
                    (Number(salaryAmount.value) + Number(benefitsAmount.value) + Number(travelAmount.value) + Number(suppliesAmount.value) +
                        Number(equipmentAmount.value) + Number(otherAmount.value) + Number(administrativeAmount.value))) {
                    offsetAmountError.setErrors({ validateOffsetSum: { validateOffsetSum: true, hideFieldName: true } });
                } else {
                    offsetAmountError.setErrors({});
                }
            } else if (reversal.value != null && reversal.value) {
                // If this invoice is a reversal and
                // If all of the items are less than or equal to 0 make sure that offset
                // Item is not less than the total of the categories
                if (Number(salaryAmount.value) <= 0 && Number(benefitsAmount.value) <= 0 &&
                    Number(travelAmount.value) <= 0 && Number(suppliesAmount.value) <= 0 &&
                    Number(equipmentAmount.value) <= 0 && Number(otherAmount.value) <= 0 &&
                    Number(administrativeAmount.value) <= 0 && Number(offsetAmount.value) <= 0) {
                    if (Number(offsetAmount.value) <
                        (Number(salaryAmount.value) + Number(benefitsAmount.value) +
                            Number(travelAmount.value) + Number(suppliesAmount.value) +
                            Number(equipmentAmount.value) + Number(otherAmount.value) + Number(administrativeAmount.value))) {
                        offsetAmountError.setErrors({ validateOffsetSum: { validateOffsetSum: true, hideFieldName: true } });
                    }
                }
            } else {
                errorMsg.setErrors({});
                offsetAmountError.setErrors({});
                negativeAmountError.setErrors({});
            }
            return null;
        };
    }

    static fiscalYearMonthValidation() {
        return (group: FormGroup): { [key: string]: any } => {
            const month = group.controls.month;
            const year = group.controls.year;
            const fiscalMonth = 9;
            let fiscalYear = 0;
            const currentMonth = new Date().getMonth();
            const currentYear = new Date().getFullYear();
            if ((currentMonth + 1) < 9) {
                fiscalYear = currentYear - 3;
            } else {
                fiscalYear = currentYear - 2;
            }
            if (year.value) {
                 if (Number(year.value) < fiscalYear) {
                    year.setErrors({ SSM_FIN_SVC_YR_GRTR_MAX: { SSM_FIN_SVC_YR_GRTR_MAX: true } });
                } else if (Number(year.value) === fiscalYear) {
                    if (Number(month.value)) {
                        if (month.value < fiscalMonth) {
                            month.setErrors({ SSM_FIN_SVC_YR_GRTR_MAX: { SSM_FIN_SVC_YR_GRTR_MAX: true } });
                        } else {
                            month.setErrors({});
                        }
                    }
                } else {
                    year.setErrors({});
                    month.setErrors({});
                }
            }
            return null;
        };
    }


}