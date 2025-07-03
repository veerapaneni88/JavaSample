import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class InvestigationLetterListValidator {

    static requiredFieldValidations = (group: FormGroup): { [key: string]: any } => {

        // Letter Type
        const letterType = group.controls.addLetterType.value;
        if (!letterType) {
            group.controls.addLetterType.setValidators(InvestigationLetterListValidator.setErrorValidator({
                selectFieldRequired: { customFieldName: 'Letter Type' }
            }));
        } else {
            group.controls.addLetterType.clearValidators();
        }
        group.controls.addLetterType.updateValueAndValidity({ onlySelf: true });        

        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}