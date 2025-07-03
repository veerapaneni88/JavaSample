import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';
export class CostReimbursementValidators {
  static compareAmountfields = (group) => {
    const salaryAmount = group.get('salaryAmount').value;
    const salaryUsedAmount = group.get('salaryUsedAmount').value;
    const fringeBenefitAmount = group.get('fringeBenefitAmount').value;
    const fringeBenefitUsedAmount = group.get('fringeBenefitUsedAmount').value;
    const travelAmount = group.get('travelAmount').value;
    const travelUsedAmount = group.get('travelUsedAmount').value;
    const supplyAmount = group.get('supplyAmount').value;
    const supplyUsedAmount = group.get('supplyUsedAmount').value;
    const equipmentAmount = group.get('equipmentAmount').value;
    const equipmentUsedAmount = group.get('equipmentUsedAmount').value;
    const otherAmount = group.get('otherAmount').value;
    const otherUsedAmount = group.get('otherUsedAmount').value;


    if (DfpsCommonValidators.isValidCurrency(group.get('salaryAmount').value, 11) && salaryAmount < salaryUsedAmount) {
      group.get('salaryAmount').setValidators(CostReimbursementValidators.validateAmount('Salary Amount'));
    } else {
      group.controls.salaryAmount.setValidators([DfpsCommonValidators.validateCurrency(11)]);
    }

    if (CostReimbursementValidators.validateFringeCurrency(group.get('salaryAmount')) && fringeBenefitAmount < fringeBenefitUsedAmount) {
      group.get('fringeBenefitAmount').setValidators(CostReimbursementValidators.validateAmount('Fringe Amount'));
    } else {
      group.controls.fringeBenefitAmount.setValidators([CostReimbursementValidators.validateFringeCurrency(11)]);
    }
    if (DfpsCommonValidators.isValidCurrency(group.get('travelAmount').value, 11) && travelAmount < travelUsedAmount) {
      group.get('travelAmount').setValidators(CostReimbursementValidators.validateAmount('Travel Amount'));
    } else {
      group.controls.travelAmount.setValidators([DfpsCommonValidators.validateCurrency(11)]);
    }
    if (DfpsCommonValidators.isValidCurrency(group.get('supplyAmount').value, 11) && supplyAmount < supplyUsedAmount) {
      group.get('supplyAmount').setValidators(CostReimbursementValidators.validateAmount('Supply Amount'));
    } else {
      group.controls.supplyAmount.setValidators([DfpsCommonValidators.validateCurrency(11)]);
    }
    if (DfpsCommonValidators.isValidCurrency(group.get('equipmentAmount').value, 11) && equipmentAmount < equipmentUsedAmount) {
      group.get('equipmentAmount').setValidators(CostReimbursementValidators.validateAmount('Equipment Amount'));
    } else {
      group.controls.equipmentAmount.setValidators([DfpsCommonValidators.validateCurrency(11)]);
    }
    if (DfpsCommonValidators.isValidCurrency(group.get('otherAmount').value, 11) && Number(otherAmount) < otherUsedAmount) {
      group.get('otherAmount').setValidators(CostReimbursementValidators.validateAmount('Other Amount'));
    } else {
      group.controls.otherAmount.setValidators([DfpsCommonValidators.validateCurrency(11)]);
    }

    group.controls.salaryAmount.updateValueAndValidity({ onlySelf: true });
    group.controls.fringeBenefitAmount.updateValueAndValidity({ onlySelf: true });
    group.controls.travelAmount.updateValueAndValidity({ onlySelf: true });
    group.controls.supplyAmount.updateValueAndValidity({ onlySelf: true });
    group.controls.equipmentAmount.updateValueAndValidity({ onlySelf: true });
    group.controls.otherAmount.updateValueAndValidity({ onlySelf: true });


  }

  static validateFringeCurrency(fieldLength?: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!DfpsCommonValidators.isValidCurrency(control.value, fieldLength)) {
        return {
          invalidDollarAmount: {
            customFieldName: 'Fringe Amount',
            actualValue: '$' + control.value
          }
        };
      }
      return null;
    };
  }

  static validateAmount(fieldName): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        salaryAmount: {
          customFieldName: fieldName,
          salaryAmount: true
        }
      };

    };
  }
}
