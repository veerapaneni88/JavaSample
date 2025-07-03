import {
    AbstractControl,
    FormGroup,
    Validators
} from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';
import { Invoice } from '../model/DeliveredService';

export class DeliveredServiceValidators {
    static validateTwoDecimalNumber(control: AbstractControl) {
        const DECIMAL_PATTERN = /^[+-]?[0-9]*\.[0-9]{2}$/;
        if (control.value && !DECIMAL_PATTERN.test(control.value)) {
            return {
                decimalNumber: {
                    actualValue: control.value,
                },
            };
        }
        return null;
    }

    static validatePositiveNumbers(length) {
        return (control: AbstractControl) => {
            if (!DfpsCommonValidators.validateCurrency(length)(control)) {
                const DECIMAL_PATTERN = /^[+-]?[0-9]*\.[0-9]{2}$/;
                if (parseInt(control.value, 10) < 0 && DECIMAL_PATTERN.test(control.value)) {
                    return {
                        negativeNumber: true,
                    };
                }
            }
            return null;
        }
    }

    static validateMonthAndYear() {
        return (group: FormGroup): { [key: string]: any } => {
            const monthControl = group.controls.month;
            const quantityControl = group.controls.quantity;
            const yearControl = group.controls.year;
            const currentMonth = new Date().getMonth() + 1;
            const currentYear = new Date().getFullYear();
            const fiscalMonth = 9;
            const fiscalYear = currentMonth < 9 ? currentYear - 3 : currentYear - 2;
            const monthValue = parseInt(monthControl.value, 10);
            const yearValue = parseInt(yearControl.value, 10);
            if ((!DfpsCommonValidators.validateMonth(monthControl) && monthValue > currentMonth)
                && (yearValue === currentYear)) {
                monthControl.setErrors({ deliveredMonth: { deliveredMonth: true } });
            } else {
                if (monthControl.errors && monthControl.errors.deliveredMonth) {
                    monthControl.setErrors({});
                    monthControl.updateValueAndValidity();

                }
            }

            if (yearValue > currentYear) {
                yearControl.setErrors({ deliveredYear: { deliveredYear: true } });
            }
            if (
                quantityControl.value !== 0 && yearValue.toString().length === 4 &&
                (yearValue < fiscalYear || (yearValue === fiscalYear && monthValue < fiscalMonth))
            ) {
                yearControl.setErrors({ SSM_FIN_SVC_YR_GRTR_MAX: { SSM_FIN_SVC_YR_GRTR_MAX: true } });
            }
            return null;
        };
    }

    static validateRateAndPaymentType(paymentType) {
        return (group: FormGroup): { [key: string]: any } => {
            const unitRateControl = group.controls.rate;
            const serviceCode = group.controls.service.value;
            if ((serviceCode && serviceCode !== '68N') && (paymentType && paymentType !== 'CRM') && !unitRateControl.errors) {
                if (!unitRateControl.value || Number(unitRateControl.value) === 0) {
                    unitRateControl.setErrors({ deliveredServiceUnitRate: true });
                }
            }
            return null;
        };
    }

    static validateAmounts(invoice: Invoice) {
        return (group: FormGroup): { [key: string]: any } => {
            const quantityControl = group.controls.quantity;
            const feePaidControl = group.controls.feePaid;
            const unitRateControl = group.controls.rate;
            const saveErrorsControl = group.controls.saveErrors;
            const adjustmentInvoiceError = group.controls.adjustmentInvoiceError;
            const invoiceAdjustmentType = invoice.adjustment;

            if (!quantityControl.invalid && !feePaidControl.invalid && !unitRateControl.invalid) {
                const feePaidValue = parseFloat(feePaidControl.value);
                const subTotal = unitRateControl.value * quantityControl.value;
                if (invoiceAdjustmentType === 'N') {
                    if (quantityControl.value < 0 || feePaidValue < 0) {
                        adjustmentInvoiceError.setErrors({
                            feePaidNegative: {
                                hideFieldName: true,
                            }
                        });
                    } else {
                        adjustmentInvoiceError.setErrors({});
                    }
                    if (Number(unitRateControl.value) !== 0 && feePaidValue !== 0) {
                        if (feePaidValue > subTotal) {
                            saveErrorsControl.setErrors({
                                feePaid: {
                                    hideFieldName: true,
                                    actualValue: '$' + (Number(subTotal) - Number(feePaidValue.toFixed(2)))
                                }
                            });
                        } else {
                            saveErrorsControl.setErrors({});
                        }
                    }
                }

                if (invoiceAdjustmentType === 'I') {
                    if ((quantityControl.value >= 0 && feePaidValue < 0) ||
                        (quantityControl.value < 0 && feePaidValue > 0)) {
                        saveErrorsControl.setErrors({
                            feePaidLessThanZero:
                                { hideFieldName: true }
                        });
                    } else {
                        saveErrorsControl.setErrors({});
                    }
                    if (quantityControl.value >= 0 && (feePaidValue > subTotal && feePaidValue !== 0)) {
                        saveErrorsControl.setErrors({
                            feePaid: {
                                hideFieldName: true,
                                actualValue:
                                    '$' + parseFloat(quantityControl.value || 0)
                                    * parseFloat(unitRateControl.value || 0) * 100 / 100.0
                            }
                        });
                    }
                    else if (quantityControl.value < 0 && feePaidValue < subTotal && feePaidValue !== 0) {
                        saveErrorsControl.setErrors(
                            {
                                feePaid: {
                                    hideFieldName: true,
                                    actualValue: '$' + parseFloat(quantityControl.value || 0)
                                        * parseFloat(unitRateControl.value || 0) * 100 / 100.0
                                }
                            });
                    }
                }
            }
            return null;
        };
    }
}
