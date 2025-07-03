import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class FosterCareValidators {
    static validateDayRange(control: AbstractControl) {
        const value = control.value;
        const NUM_PATTERN = /^([1-9]|[12][0-9]|3[01])$/;

        if (value && value !== '') {
            if (!NUM_PATTERN.test(value) || value < 1 || value > 31) {
                return {
                    dayPattern: {
                        actualValue: control.value,
                    },
                };
            }
        }
    }

    static validateDays() {
        return (group) => {
            const month = group.controls.month.value ? Number(group.controls.month.value) : 0;
            const year = group.controls.year.value ? Number(group.controls.year.value) : 0;
            const fromDay = group.controls.fromDay.value !== '' ? Number(group.controls.fromDay.value) : 0;
            const toDay = group.controls.toDay.value !== '' ? Number(group.controls.toDay.value) : 0;
            const isLeapYear: boolean = year ? year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0) : false;

            if (month !== 0 && year !== 0 && fromDay !== 0) {
                if (
                    ((month === 4 || month === 6 || month === 9 || month === 11) && fromDay === 31) ||
                    (month === 2 && ((isLeapYear && fromDay > 29) || (!isLeapYear && fromDay > 28)))
                ) {
                    group.controls.fromDay.setValidators(FosterCareValidators.setInvalidMonthMessage());
                } else if (fromDay > toDay) {
                    group.controls.fromDay.setValidators(FosterCareValidators.setDayRangeMessage());
                } else {
                    if (group.controls.fromDay.getError) {
                        group.controls.fromDay.setValidators([FosterCareValidators.validateDayRange]);
                    }
                }
            }
            if (month !== 0 && year !== 0 && toDay !== 0) {
                if (
                    ((month === 4 || month === 6 || month === 9 || month === 11) && toDay === 31) ||
                    (month === 2 && ((isLeapYear && toDay > 29) || (!isLeapYear && toDay > 28)))
                ) {
                    group.controls.toDay.setValidators(FosterCareValidators.setInvalidMonthMessage());
                } else {
                    if (group.controls.toDay.getError) {
                        group.controls.toDay.setValidators([FosterCareValidators.validateDayRange]);
                    }
                }
            }
            group.controls.fromDay.updateValueAndValidity({ onlySelf: true });
            group.controls.toDay.updateValueAndValidity({ onlySelf: true });
            return null;
        };
    }

    static validateFosterCareSave() {
        return (group: FormGroup): { [key: string]: any } => {
            const thisMonth = new Date().getMonth() + 1;
            const thisYear = new Date().getFullYear();
            const fiscalMonth = 9;
            const fiscalYear = thisMonth < 9 ? thisYear - 3 : thisYear - 2;
            const receivedMonth = new Date(group.controls.invoiceReceivedDate.value).getMonth() + 1;
            const receivedYear = new Date(group.controls.invoiceReceivedDate.value).getFullYear();

            const year = Number(group.controls.year.value);
            const month = Number(group.controls.month.value);

            const fromDay = group.controls.fromDay.value !== '' ? Number(group.controls.fromDay.value) : 0;
            const toDay = group.controls.toDay.value !== '' ? Number(group.controls.toDay.value) : 0;

            if (
                year > 0 &&
                !DfpsCommonValidators.validateYear(group.controls.year) &&
                !DfpsCommonValidators.validateMonth(group.controls.month)
            ) {
                // Invoice month and year must be w/in the last two fiscal years.
                if (
                    (year < fiscalYear && toDay !== 0 && fromDay !== 0) ||
                    (year === fiscalYear && month < fiscalMonth && toDay !== 0 && fromDay !== 0)
                ) {
                    group.controls.year.setValidators(FosterCareValidators.setInvalidFiscalYear());
                } else if (year > receivedYear) {
                    // year entered cannot be greater than the received year of the invoice
                    group.controls.year.setValidators(FosterCareValidators.setInvoiceReceiveDateMessage());
                } else if (year === receivedYear && month > receivedMonth) {
                    group.controls.month.setValidators(FosterCareValidators.setInvoiceReceiveDateMessage());
                } else {
                    if (group.controls.year.getError) {
                        group.controls.year.setValidators([Validators.required, DfpsCommonValidators.validateYear]);
                    }
                    if (group.controls.month.getError) {
                        group.controls.month.setValidators([Validators.required, DfpsCommonValidators.validateMonth]);
                    }
                }
            } else {
                group.controls.year.setValidators([Validators.required, DfpsCommonValidators.validateYear]);
                group.controls.month.setValidators([Validators.required, DfpsCommonValidators.validateMonth]);
            }

            // The unit rate can not be negative
            const rate = group.controls.rate.value;
            if (rate < 0) {
                group.controls.rate.setValidators([
                    FosterCareValidators.setrateNegativeMessage(),
                    DfpsCommonValidators.validateCurrency(7),
                ]);
            } else {
                group.controls.rate.setValidators([Validators.required, DfpsCommonValidators.validateCurrency(7)]);
            }

            // Validate before save
            const personId = group.controls.personId.value;
            const resourceId = group.controls.resourceId.value;

            if ((personId || resourceId) && group.controls.isValidated.value === 'false') {
                group.controls.isValidated.setValidators(FosterCareValidators.setValidateBeforeSaveMessage());
            } else {
                group.controls.isValidated.clearValidators();
            }

            const income = parseFloat(group.controls.income.value);
            const isReversal = group.controls.isReversal.value;
            const unitQty: number = toDay === 0 && fromDay === 0 ? 0 : toDay - fromDay + 1;
            const subTotal = this.roundingNumber(unitQty > 0 ? unitQty * rate : 0, 2);

            if (isReversal) {
                if (!DfpsCommonValidators.validateCurrency(11)(group.controls.income) && income > 0) {
                    group.controls.income.setValidators([FosterCareValidators.setPositiveIncomeErrorMessage()]);
                } else if (
                    !DfpsCommonValidators.validateCurrency(11)(group.controls.income) &&
                    income < subTotal * -1
                ) {
                    group.controls.income.setValidators([
                        FosterCareValidators.setInvoiceGreaterThanIncomeMessage(subTotal * -1),
                        DfpsCommonValidators.validateCurrency(11),
                    ]);
                } else {
                    group.controls.income.setValidators([DfpsCommonValidators.validateCurrency(11)]);
                }
            } else {
                if (!DfpsCommonValidators.validateCurrency(11)(group.controls.income) && income < 0) {
                    group.controls.income.setValidators([FosterCareValidators.setNegativeIncomeErrorMessage()]);
                } else if (!DfpsCommonValidators.validateCurrency(11)(group.controls.income) && income > subTotal) {
                    group.controls.income.setValidators([
                        FosterCareValidators.setInvoiceGreaterThanIncomeMessage(subTotal),
                    ]);
                } else {
                    group.controls.income.setValidators([DfpsCommonValidators.validateCurrency(11)]);
                }
            }

            group.controls.income.updateValueAndValidity({ onlySelf: true });
            group.controls.rate.updateValueAndValidity({ onlySelf: true });
            group.controls.isValidated.updateValueAndValidity({ onlySelf: true });
            group.controls.year.updateValueAndValidity({ onlySelf: true });
            group.controls.month.updateValueAndValidity({ onlySelf: true });
            return null;
        };
    }

    static roundingNumber(numberVal, decimalPlaces) {
        const factorOfTen = Math.pow(10, decimalPlaces)
        return Math.round(numberVal * factorOfTen) / factorOfTen
      }

    static validateIncome() {
        return (group: FormGroup): { [key: string]: any } => {
            return null;
        };
    }

    static setDayRangeMessage(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                dayRange: {
                    dayRange: true,
                },
            };
        };
    }

    static setInvalidMonthMessage(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                invalidMonthDay: {
                    invalidMonthDay: true,
                },
            };
        };
    }

    static setInvalidFiscalYear(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                SSM_FIN_SVC_YR_GRTR_MAX: {
                    SSM_FIN_SVC_YR_GRTR_MAX: true,
                },
            };
        };
    }

    static setInvoiceReceiveDateMessage() {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                MSG_MO_YR_GRTR_REC: {
                    MSG_MO_YR_GRTR_REC: true,
                },
            };
        };
    }

    static setrateNegativeMessage() {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!DfpsCommonValidators.validateCurrency(7)(control)) {
                return {
                    SSM_NO_NEG_UNIT_RATE: {
                        SSM_NO_NEG_UNIT_RATE: true,
                    },
                };
            }
        };
    }

    static setValidateBeforeSaveMessage() {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                MSG_VAL_SAVE: {
                    hideFieldName: true,
                    MSG_VAL_SAVE: true,
                },
            };
        };
    }

    // SSM_FIN_INC_GRTR_THN_ITM
    static setInvoiceGreaterThanIncomeMessage(value: any) {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!DfpsCommonValidators.validateCurrency(11)(control)) {
                return {
                    SSM_FIN_INC_GRTR_THN_ITM: {
                        SSM_FIN_INC_GRTR_THN_ITM: true,
                        actualValue: '$' + value.toFixed(2),
                    },
                };
            }
        };
    }

    // SSM_INC_POS_REVERSAL
    static setPositiveIncomeErrorMessage() {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                SSM_INC_POS_REVERSAL: {
                    SSM_INC_POS_REVERSAL: true,
                },
            };
        };
    }

    // SSM_FIN_INC_NEG
    static setNegativeIncomeErrorMessage() {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!DfpsCommonValidators.validateCurrency(11)(control)) {
                return {
                    SSM_FIN_INC_NEG: {
                        SSM_FIN_INC_NEG: true,
                    },
                };
            }
        };
    }
}
