import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class FinancialAccountValidators {
    static saveValidations = (group: FormGroup) => {
        const transactionDate = group.controls.transactionDate.value;
        const currentBalance = group.controls.currentBalance.value;
        const amount = group.controls.amount.value;
        const type = group.controls.type.value;
        const category = group.controls.category.value;
        const subcategory = group.controls.subcategory.value;
        const program = group.controls.program.value;
        const isCpsCheckAccount = group.controls.isCpsCheckAccount.value;

        if (transactionDate && new Date(transactionDate) > new Date()) {
            group.controls.transactionDate.setValidators(FinancialAccountValidators.setDateError());
        } else {
            group.controls.transactionDate.setValidators([Validators.required, DfpsCommonValidators.validateDate]);
        }

        if (type && category) {
            if (
                (category === 'AA' ||
                    category === 'EM' ||
                    category === 'HE' ||
                    category === 'ME' ||
                    category === 'PE' ||
                    category === 'PE') &&
                !subcategory &&
                program === 'APS'
            ) {
                group.controls.subcategory.setValidators(FinancialAccountValidators.setSubCategoyErrorMessage());
            } else {
                group.controls.subcategory.clearValidators();
            }
        } else {
            group.controls.subcategory.clearValidators();
        }

        if (type === 'Debit') {
            if (!group.controls.description.value || group.controls.description.value === '') {
                group.controls.description.setValidators(FinancialAccountValidators.setDescriptionError());
            } else {
                group.controls.description.clearValidators();
            }
            if (
                program === 'CPS' &&
                !isCpsCheckAccount &&
                (!group.controls.warrantNumber.value || group.controls.warrantNumber.value === 0) &&
                !group.controls.ach.value &&
                category !== 'TC' &&
                category !== 'TS' &&
                category !== 'TT'
            ) {
                group.controls.warrantNumber.setValidators(FinancialAccountValidators.setCheckAchError());
            } else if (
                program !== 'CPS' &&
                (!group.controls.warrantNumber.value || group.controls.warrantNumber.value === 0) &&
                !group.controls.ach.value
            ) {
                group.controls.warrantNumber.setValidators(FinancialAccountValidators.setCheckNumberError());
            } else {
                group.controls.warrantNumber.clearValidators();
            }
        } else {
            group.controls.description.clearValidators();
            group.controls.warrantNumber.clearValidators();
        }

        if (
            program === 'CPS' &&
            isCpsCheckAccount &&
            (type === 'Debit' || type === 'Hold') &&
            amount > currentBalance &&
            category !== 'DD' &&
            category !== 'NS' &&
            category !== 'BE'
        ) {
            group.controls.amount.setValidators([
                FinancialAccountValidators.setAmountNegativeError(),
                DfpsCommonValidators.validateCurrency(12),
            ]);
        } else if (amount !== null && (amount === '' || amount <= 0)) {
            group.controls.amount.setValidators([
                FinancialAccountValidators.setAmountZeroError(),
                DfpsCommonValidators.validateCurrency(12),
            ]);
        } else {
            group.controls.amount.setValidators([Validators.required, DfpsCommonValidators.validateCurrency(12)]);
        }

        if (program === 'CPS' && !isCpsCheckAccount && (type === 'Debit' || type === 'Hold')) {
            if (amount > currentBalance) {
                group.controls.amount.setValidators([
                    FinancialAccountValidators.setAmountError(),
                    DfpsCommonValidators.validateCurrency(12),
                ]);
            } else {
                group.controls.amount.setValidators([Validators.required, DfpsCommonValidators.validateCurrency(12)]);
            }
        }

        group.controls.transactionDate.updateValueAndValidity({ onlySelf: true });
        group.controls.description.updateValueAndValidity({ onlySelf: true });
        group.controls.amount.updateValueAndValidity({ onlySelf: true });
        group.controls.warrantNumber.updateValueAndValidity({ onlySelf: true });
        group.controls.subcategory.updateValueAndValidity({ onlySelf: true });
        return null;
    };

    static setDateError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                InvoiceReceivedDate: {
                    InvoiceReceivedDate: true,
                },
            };
        };
    }

    static setDescriptionError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                MSG_DEBIT_DESC: {
                    MSG_DEBIT_DESC: true,
                },
            };
        };
    }

    static setAmountError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                MSG_FIN_TRAN_AMOUNT: {
                    MSG_FIN_TRAN_AMOUNT: true,
                },
            };
        };
    }

    static setAmountZeroError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!DfpsCommonValidators.validateCurrency(12)(control)) {
                return {
                    MSG_FIN_TRAN_AMOUNT_REQ: {
                        MSG_FIN_TRAN_AMOUNT_REQ: true,
                    },
                };
            }
        };
    }

    static setAmountNegativeError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!DfpsCommonValidators.validateCurrency(12)(control)) {
                return {
                    MSG_FIN_ACCTBAL_NEG_ON_ADD: {
                        MSG_FIN_ACCTBAL_NEG_ON_ADD: true,
                    },
                };
            }
        };
    }

    static setCheckNumberError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                MSG_DEBIT_CHECKNBR: {
                    customFieldName: 'Check Number',
                    MSG_DEBIT_CHECKNBR: true,
                },
            };
        };
    }

    static setSubCategoyErrorMessage(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                MSG_FIN_SUBCATEGORY_REQ: {
                    MSG_FIN_SUBCATEGORY_REQ: true,
                },
            };
        };
    }

    static setCheckAchError(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return {
                MSG_DEBIT_CKNBR: {
                    customFieldName: 'Check Number',
                    MSG_DEBIT_CKNBR: true,
                },
            };
        };
    }
}
