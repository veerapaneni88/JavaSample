import { AbstractControl, FormGroup } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class ContractPeriodValidators {
    static validateProcurementNumber(control: AbstractControl) {
        const PROCUREMENT_NUMBER_PATTERN = /^$|^[a-zA-Z0-9][a-zA-Z0-9-]{1,14}[a-zA-Z0-9]$/;
        if (control.value && !PROCUREMENT_NUMBER_PATTERN.test(control.value)) {
            return {
                procrementNumberPattern: {
                    actualValue: control.value,
                },
            };
        }
        return null;
    }

    static validateContractNumber(validateStatus, displayContract, scorLinked) {
        return (group: FormGroup): { [key: string]: any } => {
            const { scorContractNumber, statusCode } = group.value;
            const scorContractNumberControl = group.controls.scorContractNumber;
            if (scorContractNumber && !validateStatus) {
                scorContractNumberControl.setErrors({ scorContractNumber: { scorContractNumber: true } });
            }
            if (scorContractNumber && statusCode === 'ACT' && displayContract && !scorLinked) {
                scorContractNumberControl.setErrors({ contractNumber: { contractNumber: true } });
            } else {
                scorContractNumberControl.setErrors({});
            }
            return null;
        };
    }

    static validateEarlyTermDate() {
        return (group: FormGroup): { [key: string]: any } => {
            const earlyTerm = group.controls.earlyTerm;
            const functionType = group.controls.functionType;
            const statusCode = group.controls.statusCode;
            const termDate = group.controls.termDate;
            const startDate = group.controls.startDate;
            const validStartDate = startDate.value && !DfpsCommonValidators.validateDate(startDate);
            const validTermDate = termDate.value && !DfpsCommonValidators.validateDate(termDate);
            const validEarlyTermDate = earlyTerm.value && !DfpsCommonValidators.validateDate(earlyTerm);
            if (statusCode.value === 'PNT' && !(functionType.value === 'FAC')) {
                if (validStartDate && validTermDate && validEarlyTermDate) {
                    if (new Date(earlyTerm.value) < new Date()) {
                        earlyTerm.setErrors({ earlyTerm: { earlyTerm: true } });
                    }
                    if (new Date(earlyTerm.value) > new Date(termDate.value)) {
                        earlyTerm.setErrors({ earlyAndTermDates: { earlyAndTermDates: true } });
                    }
                    if (
                        new Date(startDate.value) > new Date(termDate.value) ||
                        new Date(startDate.value) > new Date(earlyTerm.value)
                    ) {
                        startDate.setErrors({ startDate: { startDate: true } });
                    }
                }
            } else {
                if (validStartDate && validTermDate) {
                    if (new Date(startDate.value) > new Date(termDate.value)) {
                        startDate.setErrors({ startDate: { startDate: true } });
                    } else {
                        startDate.setErrors({});
                    }
                }
            }
            return null;
        };
    }
}
