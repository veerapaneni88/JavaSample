import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export class VisitAcknowledgementValidator {

    static validateLetterInformation = (group: FormGroup): { [key: string]: any } => {
        const letterMethod = group.controls.letterMethod;
        const operationHomeName = group.controls.operationHomeName;
		const recipientNumber = group.controls.recipientNumber;
		
		// Method        
        if (!letterMethod.value) {
            group.controls.letterMethod.setValidators(VisitAcknowledgementValidator.setErrorValidator({
                selectFieldRequired: { customFieldName: 'Method' }
            }));
        } else {
            group.controls.letterMethod.clearValidators();
        }
        group.controls.letterMethod.updateValueAndValidity({ onlySelf: true });

        if (["RCM", "RCE", "RCF", "REF"].includes(letterMethod.value) && (!recipientNumber.value || recipientNumber.value === 0)) {
          group.controls.recipientNumber.setValidators(VisitAcknowledgementValidator.setErrorValidator({
            required: { customFieldName: 'Regular/Certified Mail Return Receipt Number' }
          }));
        }else {
          group.controls.recipientNumber.clearValidators();
        }

      	if (!operationHomeName.value ) {
	        group.controls.operationHomeName.setValidators(VisitAcknowledgementValidator.setErrorValidator({
	          required: { customFieldName: 'Operation/Home/Branch Name' }
	        }));
	      }else {
	        group.controls.operationHomeName.clearValidators();
      	}

	      group.controls.recipientNumber.updateValueAndValidity({ onlySelf: true });
	      group.controls.operationHomeName.updateValueAndValidity({ onlySelf: true });

      	return null;
    }


	static setErrorValidator(errorObject: any): ValidatorFn {
	    return (control: AbstractControl): ValidationErrors | null => {
	      return errorObject;
	    };
	}
}
