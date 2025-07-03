import { FormGroup, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { isError } from 'util';

export class BudgetTransferValidator {
    static validateFromField() {
        return (group: FormGroup): { [key: string]: any } => {
            const from = group.controls.from;
            if (!group.controls.from.value) {
                group.controls.from.setErrors({ from: { from: true, hideFieldName: true } });
            }

            return null;
        };
    }

    static validateToField() {
        return (group: FormGroup): { [key: string]: any } => {
            const from = group.controls.from;
            if (!group.controls.to.value) {
                group.controls.to.setErrors({ to: { to: true , hideFieldName: true,} });
            }

            return null;
        };
    }

    static validateFromToField() {
        return (group: FormGroup): { [key: string]: any } => {
            const from = group.controls.from;
            if (group.controls.from.value && group.controls.to.value) {
                if (
                    group.controls.from.value.idCNSVC === group.controls.to.value.idCNSVC &&
                    group.controls.from.value.category === group.controls.to.value.category
                ) {
                    group.controls.from.setErrors({ fromToEq: { fromToEq: true, hideFieldName: true, } });
                }
            }

            return null;
        };
    }

    static validateAmountInvalid() {
        return (group: FormGroup): { [key: string]: any } => {
            const from = group.controls.from;
            const amount = group.controls.amount;
            if (group.controls.from.value && group.controls.to.value && group.controls.amount.value) {
                const amtValue = +group.controls.amount.value.replace(/,/g, '');
                const fromBudBal = Number(group.controls.from.value.budgetBalance);
                if (amtValue < 0) {
                    group.controls.amount.setErrors({ invalidDollarAmount: { invalidDollarAmount: true } });
                } else if (amtValue > fromBudBal) {
                    group.controls.from.setErrors({ invalidAmt: { invalidAmt: true, hideFieldName: true, } });
                }
            }

            return null;
        };
    }
}
