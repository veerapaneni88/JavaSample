import { AbstractControl, FormGroup, ValidationErrors } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class InvoiceHeaderValidators {
    static validateInvoiceHeaderContractId() {
        return (group: FormGroup): { [key: string]: any } => {
            const validateControl = group.controls.validate;
            const receivedDateControl = group.controls.receivedDate;

            const contractId = group.controls.agencyAccountId.value;

            if (contractId && validateControl.value === 'false') {
                validateControl.setErrors({ validate: { validate: true, hideFieldName: true } });
            } else {
                validateControl.setErrors({});
            }
            this.validateReceivedDate(receivedDateControl);

            const monthControl = group.controls.month;
            const yearControl = group.controls.year;
            const currentMonth = new Date().getMonth() + 1;
            const currentYear = new Date().getFullYear();
            const invoiceMonth = parseInt(monthControl.value, 10);
            const invoiceYear = parseInt(yearControl.value, 10);
            const monthValidate = group.controls.monthValidate;

            if (invoiceMonth && invoiceYear) {
                if (invoiceYear > currentYear && invoiceYear.toString().length >= 4) {
                    monthValidate.setErrors({ InvoiceYearMonth: { InvoiceYearMonth: true, hideFieldName: true } });
                } else if (invoiceYear === currentYear && invoiceMonth > currentMonth && invoiceMonth <= 12) {
                    monthValidate.setErrors({ InvoiceYearMonth: { InvoiceYearMonth: true, hideFieldName: true } });
                } else {
                    monthValidate.setErrors({});
                }
            }
            this.validatFiscalYear(yearControl, monthControl);
            this.validateClaimAmountNegative(group.controls.invoiceAdjustment, group.controls.claimedAmount);
            this.validateReceivedMonthAndYear();
            return null;
        };
    }

    static validateReceivedDate(receivedDateControl: AbstractControl) {
        if (!receivedDateControl.errors || receivedDateControl.errors === {}) {
            if (receivedDateControl.value && (new Date(receivedDateControl.value) > new Date())) {
                receivedDateControl.setErrors({ InvoiceReceivedDate: { InvoiceReceivedDate: true } });
            } else {
                receivedDateControl.setErrors({});
            }
        }
    }

    static validateClaimAmountNegative(adjustment: AbstractControl, claimAmount: AbstractControl) {
        const claimAmountValue = claimAmount.value === '' ? 0 : claimAmount.value;
        if (!adjustment.value && claimAmountValue < 0) {
            claimAmount.setErrors({ CLAIMED_AMOUNT_NEGATIVE: { CLAIMED_AMOUNT_NEGATIVE: true } });
        }
    }

    static validatFiscalYear(yearControl: AbstractControl, monthControl: AbstractControl) {
        const thisMonth = new Date().getMonth() + 1;
        const thisYear = new Date().getFullYear();
        const fiscalMonth = 9;
        const fiscalYear = thisMonth < 9 ? thisYear - 3 : thisYear - 2;
        const year = Number(yearControl.value);
        const month = Number(monthControl.value);

        if (year > 0) {
            // Invoice month and year must be w/in the last two fiscal years. SSM_FIN_SVC_YR_GRTR_MAX
            if (year < fiscalYear || (year === fiscalYear && month < fiscalMonth)) {
                yearControl.setErrors({ SSM_FIN_SVC_YR_GRTR_MAX: { SSM_FIN_SVC_YR_GRTR_MAX: true } });
            } else {
                yearControl.setErrors({});
            }
        }
    }

    static validateReceivedMonthAndYear() {
        return (group: FormGroup): { [key: string]: any } => {
            const monthControl = group.controls.month;
            const yearControl = group.controls.year;
            const receivedDateControl = group.controls.receivedDate;
            const receivedYearValidate = group.controls.receivedYearValidate;
            if (receivedDateControl.value && yearControl.value && monthControl.value) {
                const receivedMonthValue = new Date(receivedDateControl.value).getMonth() + 1;
                const receivedYearValue = new Date(receivedDateControl.value).getFullYear();
                const invoiceMonth = +monthControl.value;
                const invoiceYear = +yearControl.value;

                if (invoiceYear > receivedYearValue) {
                    receivedYearValidate.setErrors({
                        INVOICE_FIN_YR_GR: { INVOICE_FIN_YR_GR: true, hideFieldName: true },
                    });
                } else if ((invoiceYear === receivedYearValue) && invoiceMonth > receivedMonthValue) {
                    receivedYearValidate.setErrors({
                        INVOICE_FIN_YR_GR: { INVOICE_FIN_YR_GR: true, hideFieldName: true },
                    });
                } else {
                    receivedYearValidate.setErrors({});
                }
            }
            return null;
        };
    }

    static validateCRM(isCrmIdZero) {
        return (group: FormGroup): { [key: string]: any } => {
            const isReadyForValidCtrl = group.get('isReadyForValid');
            if (!isReadyForValidCtrl.disabled && isCrmIdZero && isReadyForValidCtrl.value) {
                isReadyForValidCtrl.setErrors({
                    CRMValidation: {
                        CRMValidation: true,
                        hideFieldName: true
                    }
                })
            }
            return null;
        };
    }
}
