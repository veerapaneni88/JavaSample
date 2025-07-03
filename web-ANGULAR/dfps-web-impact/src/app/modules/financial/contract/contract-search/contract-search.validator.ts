import { FormGroup, Validators } from '@angular/forms';

export class ContractSearchValidators {
  static validateProgramType = (group: FormGroup): { [key: string]: any } => {
      const contractNumberValue = group.controls.contractNumber.value;
      const agencyAccountIdvalue = group.controls.agencyAccountId.value;
      const resourceIdvalue = group.controls.resourceId.value;
      if ([contractNumberValue, agencyAccountIdvalue, resourceIdvalue].join('')) {
        group.controls.programType.clearValidators();
      } else {
        group.controls.programType.setValidators([Validators.required]);
      }
      group.controls.programType.updateValueAndValidity({ onlySelf: true });
      return null;
    }
  }

