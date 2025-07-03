import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export class ChildDeathReportValidator {

  static validateEmailPattern() {
    return (group: FormGroup): { [key: string]: any } => {
      let emailRegEx = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
      const email = group.controls.investigatorEmail.value;
      if (email &&  !emailRegEx.test(email)) {
        group.controls.investigatorEmail.setErrors({
          MSG_EMAIL_ADDRESS_INVALID: {
            customFieldName: 'Email',
            emailAddressIsInvalid: true,
            actualValue: email
          }
        });
      }
      return null;
    }
  }

  static phoneNumberPattern = (group: FormGroup): { [key: string]: any } => {
    const phoneNumber = group.controls.investigatorPhoneNumber.value;
    if (phoneNumber) {
      const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[0-9]{10}$/;
      if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim().replace(/[^+\d]+/g, ''))) {
        group.controls.investigatorPhoneNumber.setErrors({ MSG_PHONE_NUMBER: { customFieldName: 'Phone Number', actualValue: phoneNumber } });
      }
    }
    return null;
  }
  static zipPattern = (group: FormGroup): { [key: string]: any } => {
    const addressZip = group.controls.addressZip.value;
    if (addressZip) {
      const ZIP_PATTERN =  /[0-9]{5}/;
      if (!ZIP_PATTERN.test(addressZip)) {
        group.controls.addressZip.setErrors({ addressZipPattern: { 
          customFieldName: 'Zip',
          actualValue: addressZip } });
      }
    }
    return null;
  }
    static validateLetterInformation = (group: FormGroup): { [key: string]: any } => {
      const letterMethod = group.controls.letterMethod;
      const recipientNumber = group.controls.recipientNumber;
      if (["RCM", "RCE", "RCF", "REF"].includes(letterMethod.value) && (!recipientNumber.value || recipientNumber.value === 0)) {
        group.controls.recipientNumber.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
        }));
      }else {
        group.controls.recipientNumber.clearValidators();
      }

      if (!letterMethod.value ) {
        group.controls.letterMethod.setValidators(ChildDeathReportValidator.setErrorValidator({
          selectFieldRequired: { customFieldName: 'Method' }
        }));
      }else {
        group.controls.letterMethod.clearValidators();
      }

      if (!group.controls.administratorName.value ) {
        group.controls.administratorName.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Name' }
        }));
      }else {
        group.controls.administratorName.clearValidators();
      }

      if (!group.controls.addressLine1.value ) {
        group.controls.addressLine1.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Address Line 1' }
        }));
      }else {
        group.controls.addressLine1.clearValidators();
      }

      if (!group.controls.addressCity.value ) {
        group.controls.addressCity.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'City' }
        }));
      }else {
        group.controls.addressCity.clearValidators();
      }

      if (!group.controls.addressState.value ) {
        group.controls.addressState.setValidators(ChildDeathReportValidator.setErrorValidator({
          selectFieldRequired: { customFieldName: 'State' }
        }));
      }else {
        group.controls.addressState.clearValidators();
      }
      if (!group.controls.addressZip.value ) {
        group.controls.addressZip.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Zip' }
        }));
      }else {
        group.controls.addressZip.clearValidators();
      }

      if (!group.controls.selectedChildsPersonId.value || group.controls.selectedChildsPersonId.value == "null") {
        group.controls.selectedChildsPersonId.setValidators(ChildDeathReportValidator.setErrorValidator({
          selectFieldRequired: { customFieldName: 'Child Name' }
        }));
      }else {
        group.controls.selectedChildsPersonId.clearValidators();
      }
      if (!group.controls.incidentLocation.value ) {
        group.controls.incidentLocation.setValidators(ChildDeathReportValidator.setErrorValidator({
          selectFieldRequired: { customFieldName: 'Location of Incident' }
        }));
      }else {
        group.controls.incidentLocation.clearValidators();
      }
      if (!group.controls.incidentSummary.value ) {
        group.controls.incidentSummary.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Brief Incident Summary' }
        }));
      }else {
        group.controls.incidentSummary.clearValidators();
      }

      if (!group.controls.investigatorDesignation.value ) {
        group.controls.investigatorDesignation.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Designation' }
        }));
      }else {
        group.controls.investigatorDesignation.clearValidators();
      }

      if (!group.controls.investigatorPhoneNumber.value ) {
        group.controls.investigatorPhoneNumber.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Phone Number' }
        }));
      }else {
        group.controls.investigatorPhoneNumber.clearValidators();
      }

      if (!group.controls.investigatorEmail.value ) {
        group.controls.investigatorEmail.setValidators(ChildDeathReportValidator.setErrorValidator({
          required: { customFieldName: 'Email Address' }
        }));
      }else {
        group.controls.investigatorEmail.clearValidators();
      }


      group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });
      group.controls.letterMethod.updateValueAndValidity({ onlySelf: true });
      group.controls.administratorName.updateValueAndValidity({ onlySelf: true });
      group.controls.addressLine1.updateValueAndValidity({ onlySelf: true });
      group.controls.addressCity.updateValueAndValidity({ onlySelf: true });
      group.controls.addressState.updateValueAndValidity({ onlySelf: true });
      group.controls.addressZip.updateValueAndValidity({ onlySelf: true });
      group.controls.selectedChildsPersonId.updateValueAndValidity({ onlySelf: true });
      group.controls.incidentLocation.updateValueAndValidity({ onlySelf: true });
      group.controls.incidentSummary.updateValueAndValidity({ onlySelf: true });
      group.controls.investigatorDesignation.updateValueAndValidity({ onlySelf: true });
      group.controls.investigatorPhoneNumber.updateValueAndValidity({ onlySelf: true });
      group.controls.investigatorEmail.updateValueAndValidity({ onlySelf: true });

      return null;
    }

  static setErrorValidator(errorObject: any): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return errorObject;
    };
  }
}
