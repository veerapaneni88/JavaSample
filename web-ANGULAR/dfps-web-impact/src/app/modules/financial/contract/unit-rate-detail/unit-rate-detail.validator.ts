import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
export class UnitRateDetailValidators {

  static compareAmountfields = (group: FormGroup) => {

    let unitRateAmount = group.get('unitRateAmount').value;
    const unitRateUsedAmount = group.get('unitRateUsedAmount').value;
    const totalUnits = group.get('totalUnits').value;
    const usedUnits = group.get('usedUnits').value;

    if (unitRateAmount.trim() === '') {
      unitRateAmount = '0.00';
    }
    // artf204309 - String comparison changed to number comparison
    if (group.get('unitRateAmount').valid && parseInt(unitRateAmount) < parseInt(unitRateUsedAmount)) {
      group.get('validateAmountError').setValidators(UnitRateDetailValidators.validateAmount());
    } else {
      group.controls.validateAmountError.clearValidators();
    }
    if (totalUnits > 65535 || usedUnits > 65535) {
      group.get('validateUnitsError').setValidators(UnitRateDetailValidators.validateUnits());
    } else {
      group.controls.validateUnitsError.clearValidators();
    }

    group.controls.validateUnitsError.updateValueAndValidity({ onlySelf: true });
    group.controls.validateAmountError.updateValueAndValidity({ onlySelf: true });

    return null;
  }

  static validateUnits(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        validUnits: {
          hideFieldName: true,
          validUnits: true
        }
      };
    };
  }

  static validateAmount(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        salaryAmount: {
          hideFieldName: true,
          salaryAmount: true
        }
      };

    };
  }
}
