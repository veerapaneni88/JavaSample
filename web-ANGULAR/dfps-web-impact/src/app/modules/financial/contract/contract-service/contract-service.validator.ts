import { AbstractControl, ValidatorFn, ValidationErrors, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';
export class ContractServiceValidators {
  static compareUnitRatefields = (group) => {

    const unitRate = group.get('unitRate').value;
    const federalMatch = group.get('federalMatch').value;
    const localMatch = group.get('localMatch').value;
    const paymentType = group.get('paymentType').value;
    const unitRateValue: number = Number(unitRate);
    const budgetLimit = group.get('budgetLimit').value;
    const NUMBER_PATTERN = /^[0-9]+$/;

    if (paymentType
      && (paymentType.indexOf('URT') !== -1 || paymentType.indexOf('VUR') !== -1)
      && (unitRateValue <= 0)) {
      group.get('unitRate').setValidators(ContractServiceValidators.validatePositiveNumbers(7));
    } else {
      group.controls.paymentType.setValidators([Validators.required]);
      group.controls.unitRate.setValidators([Validators.max(9999.99), DfpsCommonValidators.validateCurrency(7)]);
    }

    if (unitRate >= 10000) {
      group.get('unitRate').setValidators(ContractServiceValidators.validateAmount('Unit Rate'));
    } else if (unitRate && !(unitRateValue <= 0)) {
      group.controls.unitRate.setValidators([Validators.max(9999.99), DfpsCommonValidators.validateCurrency(7)]);
    }

    if (!budgetLimit && paymentType === 'CRM') {
      group.get('paymentType').setValidators(ContractServiceValidators.validateBudgetAmount('Payment Type'));
    } else if (paymentType) {
      group.controls.paymentType.setValidators([Validators.required]);
    }

    if (federalMatch && !NUMBER_PATTERN.test(federalMatch)) {
      group.get('federalMatch').setValidators(ContractServiceValidators.validateMatch('Federal Match', federalMatch));
    } else if (federalMatch >= 1000) {
      group.get('federalMatch').setValidators(ContractServiceValidators.validateFedLocAmountMatch('Federal Match'));
    } else {
      group.controls.federalMatch.setValidators([Validators.maxLength(3)]);
    }

    if (localMatch && !NUMBER_PATTERN.test(localMatch)) {
      group.get('localMatch').setValidators(ContractServiceValidators.validateMatch('Local Match', localMatch));
    } else if (localMatch >= 1000) {
      group.get('localMatch').setValidators(ContractServiceValidators.validateFedLocAmountMatch('Local Match'));
    } else {
      group.controls.localMatch.setValidators([Validators.maxLength(3)]);
    }

    group.controls.federalMatch.updateValueAndValidity({ onlySelf: true });
    group.controls.localMatch.updateValueAndValidity({ onlySelf: true });
    group.controls.unitRate.updateValueAndValidity({ onlySelf: true });
    group.controls.paymentType.updateValueAndValidity({ onlySelf: true });
    return null;
  }

  static validateAmount(fieldName): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        unitRate: {
          customFieldName: fieldName,
          unitRate: true
        }
      };
    };
  }

  static validateBudgetAmount(fieldName): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        budgetLimit: {
          customFieldName: fieldName,
          budgetLimit: true
        }
      };
    };
  }

  static validateFedLocAmountMatch(fieldName): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        unitRate: {
          customFieldName: fieldName,
          unitRate: true
        }
      };
    };
  }

  static validateMinimumAmount(fieldName): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        unitRatePaymentType: {
          customFieldName: fieldName,
          unitRatePaymentType: true
        }
      };
    };
  }

  static validateMatch(fieldName, value): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        federalMatch: {
          actualValue: value,
          customFieldName: fieldName,
          federalMatch: true
        }
      };
    };
  }

  static validatePositiveNumbers(length) {
    return (control: AbstractControl) => {
        
            const DECIMAL_PATTERN = /^[+-]?[0-9]*\.[0-9]{2}$/;
            if (parseInt(control.value, 10) < 0 ) {
                return {
                    negativeNumber: true,
                };
            }
        
            return null;
    }
}
}
