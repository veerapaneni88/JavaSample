import { FormGroup, AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export class ContractHeaderValidators {
  static validateSponsorInfo = (group: FormGroup): { [key: string]: any } => {
    if (group.controls.backgroundCheck.value) {
      if (!group.controls.organizationId.value) {
        group.controls.organizationId.setValidators([ContractHeaderValidators.validateOrganizationAndLegal('EIN and Legal Name')]);
      }
      if (!group.controls.sponsorPersonId.value) {
        group.controls.sponsorPersonId.setValidators([ContractHeaderValidators.validateSponsorName('Sponsor Name')]);
      }
    } else {
      group.controls.organizationId.clearValidators();
      group.controls.sponsorPersonId.clearValidators();
    }

    const resourceId = group.controls.resourceId.value;

    if (resourceId && group.controls.isValidated.value === 'false') {
      group.controls.isValidated.setValidators(ContractHeaderValidators.setValidateBeforeSaveMessage());
    } else {
      group.controls.isValidated.clearValidators();
    }

    group.controls.isValidated.updateValueAndValidity({ onlySelf: true });
    group.controls.organizationId.updateValueAndValidity({ onlySelf: true });
    group.controls.sponsorPersonId.updateValueAndValidity({ onlySelf: true });
    return null;
  }

  static validateContractManager(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return {
          contractMgr: {
            customFieldName: 'Contract Manager',
            contractMgr: true
          }
        };
      }
    };
  }

  static validateContractVersionIsLocked(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return {
          contractVersionLocked: {
            customFieldName: 'Transfers',
            contractVersionLocked: true
          }
        };
      }
    };
  }

  static validateOrganizationAndLegal(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return {
          organizationEINAndLegalName: {
            customFieldName: fieldName,
            organizationEINAndLegalName: true
          }
        };
      }
    };
  }

  static validateSponsorName(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return {
          sponsorName: {
            customFieldName: fieldName,
            sponsorName: true
          }
        };
      }
    };
  }

  static validateContractPeriod() {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return {
          selectedContractPeriod: {
            customFieldName: 'Contract Period',
            selectedContractPeriod: true
          }
        };
      }
    };
  }

  static setValidateBeforeSaveMessage() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        SSM_CON_NOT_VALIDATED: {
          hideFieldName: true,
          SSM_CON_NOT_VALIDATED: true
        }
      };
    };
  }

}


