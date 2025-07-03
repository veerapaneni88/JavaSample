import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { formatDate } from '@angular/common';
import { DfpsCommonValidators } from 'dfps-web-lib';
import { moment } from 'ngx-bootstrap/chronos/test/chain';

export class AddServicePackageCredentialsValidators {

  static validateEffectiveDate = (group: FormGroup): { [key: string]: any } => {
    const effectiveDate = group.controls.effectiveDate.value;
    const selDate = group.controls.effectiveDate.value ? new Date(group.controls.effectiveDate.value) : null;

    const currentDate = new Date(formatDate(new Date(), 'MM/dd/yyyy', group.controls.locale.value));
    const DateAfter14Days = new Date(currentDate.setDate(currentDate.getDate() + 14));
    const previousFacilityEffectiveDate = new Date(group.controls.previousEffectiveDate.value);
    const cpaEffectiveDate = group.controls.cpaEffectiveDate.value;
    const facilityTypeValid = group.controls.facilityTypeValid.value;


    if (!effectiveDate) {
      group.controls.effectiveDate.setValidators(
        AddServicePackageCredentialsValidators.setErrorValidator({
          MSG_EFFECTIVE_DATE_REQ: {
            actualValue: effectiveDate
          }
        }));
    } else if (effectiveDate && effectiveDate !== '' && DfpsCommonValidators.validateDate(group.controls.effectiveDate)) {
      group.controls.effectiveDate.setValidators(
        AddServicePackageCredentialsValidators.setErrorValidator({
          validateDate: { actualValue: effectiveDate }
        }));
    } else if (effectiveDate && (selDate > DateAfter14Days)) {
      group.controls.effectiveDate.setValidators(
        AddServicePackageCredentialsValidators.setErrorValidator({
          MSG_RES_PACK_DATE_TOO_LATE: { actualValue: effectiveDate }
        }));
    } else if (effectiveDate && (selDate < previousFacilityEffectiveDate) ||
      (Math.floor((Date.UTC(selDate.getFullYear(), selDate.getMonth(), selDate.getDate()) -
        Date.UTC(previousFacilityEffectiveDate.getFullYear(), previousFacilityEffectiveDate.getMonth(), previousFacilityEffectiveDate.getDate())) / (1000 * 60 * 60 * 24)) <= 2)) {
      group.controls.effectiveDate.setValidators(
        AddServicePackageCredentialsValidators.setErrorValidator({
          MSG_EFFECTIVE_DATE_PAST_CHECK: { hideFieldName: true }
        }));
    } else if(facilityTypeValid && effectiveDate && !cpaEffectiveDate){
      group.controls.effectiveDate.setValidators(
        AddServicePackageCredentialsValidators.setErrorValidator({
          MSG_CPA_CREDENTIALS_DOES_NOT_EXIST: { actualValue: effectiveDate }
        }));
    } else if(facilityTypeValid && effectiveDate && cpaEffectiveDate && new Date(effectiveDate) < new Date(cpaEffectiveDate)){
      group.controls.effectiveDate.setValidators(
        AddServicePackageCredentialsValidators.setErrorValidator({
          MSG_CPA_EFFECTIVE_DATE_PAST_CHECK: { actualValue: effectiveDate }
        }));
    }
    else { group.controls.effectiveDate.clearValidators(); }

    group.controls.effectiveDate.updateValueAndValidity({ onlySelf: true });
    return null;
  }

  static setErrorValidator(errorObject: any): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return errorObject;
    };
  }
}
