import { AbstractControl, ValidationErrors } from '@angular/forms';
import { formatDate } from '@angular/common';

export class PaymentInfoValidators {
    static childName: any;

    static validateRequestExtn() {

    return (group: AbstractControl): ValidationErrors | null => {
        const goodCause = group.get('goodCause').value? group.get('goodCause').value :'';
        const comments =  group.get('comments').value?  group.get('comments').value: '';
        const startDate = new Date(group.get('startDate').value?  group.get('startDate').value: '');
        const childName = group.get('childName').value?  group.get('childName').value: '';
        const paymentEligibility = group.get('paymentEligibility').value?  group.get('paymentEligibility').value: '';
        
        const today = new Date();
        const firstDayOfThisMonth = new Date(today.getFullYear(), today.getMonth(), 1);

        group.get('startDate').clearValidators();
        
        if(goodCause !== 'G' && paymentEligibility !== 'Court-ordered' && paymentEligibility !== 'Eligible' ){
          group.get('paymentEligibilityValidated').setValidators(PaymentInfoValidators.setCaregiverEligibility());
        }
        else{
          group.get('paymentEligibilityValidated').clearValidators();
        }
        if(goodCause === 'H' && comments.length === 0){
            group.get('isValidated').setValidators(PaymentInfoValidators.setValidateRequestExtn());
        }
        else{
            group.get('isValidated').clearValidators();
        }
        if(startDate.getTime() === 0 || ( startDate > today ) && (startDate.getFullYear() === 3500 )){
            group.get('startDate').setValidators(PaymentInfoValidators.setRequiredValidations());
        }
        
        else if( startDate.getTime() !== 0){
            if(new Date(startDate) < firstDayOfThisMonth ) {
                group.get('startDate').setValidators(PaymentInfoValidators.setValidateBackDateExtnDate());
            }
        }
        else{
            group.get('startDate').clearValidators();
        }
        if(goodCause.length === 0){
            group.get('goodCause').setValidators(PaymentInfoValidators.setRequiredValidations());
        }
        else{
            group.get('goodCause').clearValidators();
        }
        
        if(childName.length === 0){
            group.get('childName').setValidators(PaymentInfoValidators.setRequiredValidations());
        }
        else{
            group.get('childName').clearValidators();
        }
        group.get('isValidated').updateValueAndValidity({ onlySelf: true });
        group.get('childName').updateValueAndValidity({ onlySelf: true });
        group.get('startDate').updateValueAndValidity({ onlySelf: true });
        group.get('goodCause').updateValueAndValidity({ onlySelf: true });
        group.get('paymentEligibilityValidated').updateValueAndValidity({ onlySelf: true });
        return null;
    };
  }

  static setValidateRequestExtn() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_COMNT_REQ: {
          hideFieldName: true,
          MSG_COMNT_REQ: true
        }
      };
    };
  }

  static setValidateBackDateExtnDate() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_KINSHIP_BACKDATE: {
          hideFieldName: false,
          MSG_KINSHIP_BACKDATE: true
        }
      };
    };
  }
  static setRequiredValidations() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        SSM_COMPLETE_REQUIRED: {
          hideFieldName: false,
          SSM_COMPLETE_REQUIRED: true
        }
      };
    };
  }
  static setOverlapValidations() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_KINSHIP_MONTHLY_EXTN_OVRLAP: {
          hideFieldName: false,
          MSG_KINSHIP_MONTHLY_EXTN_OVRLAP: true
        }
      };
    };
  }
  static setCaregiverEligibility() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_NO_KIN_PYMNT_ELIGIBILITY: {
          hideFieldName: true,
          MSG_NO_KIN_PYMNT_ELIGIBILITY: true
        }
      };
    };
  }
  static setOverlapWithRegularValidations() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_KINSHIP_RGLR_EXTN_OVERLAP: {
          hideFieldName: false,
          MSG_KINSHIP_RGLR_EXTN_OVERLAP: true
        }
      };
    };
  }

}
