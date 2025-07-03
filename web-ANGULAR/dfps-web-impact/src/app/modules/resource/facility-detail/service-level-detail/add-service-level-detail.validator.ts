import { AbstractControl, ValidationErrors, Validators, FormGroup } from '@angular/forms';
import { formatDate } from '@angular/common';
export class AddServiceLevelDetailValidator {



  /* If the new effective date is more than 14 days after the current date, throw an error.
   The new effective date must be at least 2 days after the previous effective date, otherwise throw an error.
   */

  static validateEffectiveDate() {
    return (group: FormGroup): { [key: string]: any } => {
      const selDate = group.controls.effectiveDate.value ? new Date(group.controls.effectiveDate.value) : null;

      const latestEffectiveDate = group.controls.latestDate.value;
      let futureDate = null;
      if (latestEffectiveDate !== null) {
        const result = latestEffectiveDate.split(' ');
        const date = result[0].trim().split('-');
        const convertedDate = date[1] + '/' + date[2] + '/' + date[0];
        const correctedLatestEffectiveDateFormat = convertedDate + ' ' + result[1];
        const latestDate = new Date(formatDate(new Date(correctedLatestEffectiveDateFormat), 'MM/dd/yyyy', group.controls.locale.value));
        futureDate = new Date(latestDate.setDate(latestDate.getDate() + 2));
      }

      const currentDate = new Date(formatDate(new Date(), 'MM/dd/yyyy', group.controls.locale.value));
      const DateAfter14Days = new Date(currentDate.setDate(currentDate.getDate() + 14));

      if (group.controls.effectiveDate.value) {
        if (selDate > DateAfter14Days) {
          group.controls.effectiveDate.setValidators(AddServiceLevelDetailValidator.setValidateEffectiveDate());
        }

        else if (latestEffectiveDate !== null && selDate < futureDate) {
          group.controls.effectiveDate.setValidators(AddServiceLevelDetailValidator.setValidateLatestEffectiveDate());
        } else {
          group.controls.effectiveDate.clearValidators();
        }
      }
      else if (!group.controls.effectiveDate.value || group.controls.effectiveDate.invalid) {
        group.controls.effectiveDate.setValidators(AddServiceLevelDetailValidator.setEffectiveDateRequired());
      }
      else {
        group.controls.effectiveDate.clearValidators();
      }
      group.controls.effectiveDate.updateValueAndValidity({ onlySelf: true });
      return null;
    };
  }

  static setValidateEffectiveDate() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_RES_DATE_TOO_LATE: {
          hideFieldName: false,
          MSG_RES_DATE_TOO_LATE: true
        }
      };
    };
  }

  static setValidateLatestEffectiveDate() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_RES_DATE_TOO_EARLY: {
          hideFieldName: false,
          MSG_RES_DATE_TOO_EARLY: true
        }
      };
    };
  }

  static setEffectiveDateRequired() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_EFFECTIVE_DATE_REQ: {
          hideFieldName: false,
          MSG_EFFECTIVE_DATE_REQ: true
        }
      };
    };
  }

}