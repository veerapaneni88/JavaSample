import { AbstractControl, FormGroup, FormControlDirective, ValidatorFn, FormControl } from '@angular/forms';

export class ContractVersionValidators {
    static validateNoShowPct(control: AbstractControl) {
        const NO_SHOW_PATTERN = /^100$|^\d?\d$/;
        if (control.value && !NO_SHOW_PATTERN.test(control.value)) {
            return {
                percentRange: true
            };
        }
        return null;
    }

    static validateEffectiveDate() {
        return (group: FormGroup): { [key: string]: any } => {
            const effectiveDate = group.controls.effectiveDate;
            const endDate = group.controls.endDate;
            if (new Date(effectiveDate.value) > new Date(endDate.value)) {
                effectiveDate.setErrors({ effectiveDate: { effectiveDate: true } });
            }
            return null;
        };
    }
}
