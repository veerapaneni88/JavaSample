import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class RiskOfRecidivismAssessmentValidators {
    static validateReason(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
          if (!control.value) {
            return {
                reasonForOverride: {
                customFieldName: 'Reason for override (mandatory or discretionary)',
                reasonForOverride: true
              }
            };
          }
        };
      }
}