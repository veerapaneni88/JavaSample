import { FormGroup } from '@angular/forms';

export class InvoiceSearchValidators {
  static validateRequiredFields(isExternalUser) {
    return (group: FormGroup): { [key: string]: any } => {
      const region = group.controls.region;
      const month = group.controls.invoiceMonth;
      const year = group.controls.invoiceYear;
      const regionMonthYearValid = group.controls.regionMonthYearValid;
      const accountResourceIdValid = group.controls.accountResourceIdValid;
      const invoiceIdVal = group.controls.invoiceId.value;
      const agencyAccountId = group.controls.agencyAccountId.value;
      const resourceid = group.controls.resourceId.value;
      if (!invoiceIdVal && !isExternalUser) {
        if (!region.value || !month.value || !year.value) {
          regionMonthYearValid.setErrors({
            invoiceSearchRequiredFields: {
              hideFieldName: true,
              invoiceSearchRequiredFields: true
            }
          });
        } else {
          regionMonthYearValid.clearValidators();
          regionMonthYearValid.updateValueAndValidity({onlySelf: true});
        }
      }
      if (!invoiceIdVal && (

        agencyAccountId && resourceid) ){
          accountResourceIdValid.setErrors({
          agencyResourceIDFields: {
            hideFieldName: true,
            agencyResourceIDFields: true
          }
        });
      }
      return null;
    };
  }

}
