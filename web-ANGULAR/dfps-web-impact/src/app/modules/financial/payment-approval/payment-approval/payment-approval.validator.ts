import { FormGroup, AbstractControl, ValidatorFn, ValidationErrors } from '@angular/forms';

export class PaymentApprovalValidators {
  static validateRowSelect(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value: any[] = control.value;
      if (value.length === 0) {
        return {
          paymentApprovals: {
            customFieldName: '',
            paymentApprovals: true
          }
        };
      }
    };
  }

}


