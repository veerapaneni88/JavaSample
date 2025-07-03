import {AbstractControl, FormGroup, ValidationErrors, ValidatorFn} from '@angular/forms';

export class ServicesByAreaDetailValidator{

  static saveValidations = (group: FormGroup): { [key: string]: any } => {
    const buttonClicked = group.controls.buttonClicked.value;
    const service = group.controls.service.value;
    const kinshipHomeAssessment = group.controls.kinshipHomeAssessment.value;
    const  kinshipIncome = group.controls.kinshipIncome.value;
    const  kinshipAgreement = group.controls.kinshipAgreement.value;

    if(buttonClicked === 'save' && ['68B', '68C', '68D','68E','68F', '68G', '68H','68I','68J','68K', '68L', '68M','68N'].includes(service)){
      if (!kinshipHomeAssessment) {
        group.controls.kinshipHomeAssessment.setValidators(
          ServicesByAreaDetailValidator.setErrorValidator({
            MSG_RSRC_HOME_ASSMNT_IND_REQ: {
              customFieldName: 'Home Assessment'
            }
          }));
      } else { group.controls.kinshipHomeAssessment.clearValidators(); }
       if (!kinshipIncome) {
        group.controls.kinshipIncome.setValidators(
          ServicesByAreaDetailValidator.setErrorValidator({
            MSG_RSRC_INCOME_QUAL_IND_REQ: {
              customFieldName: 'Income Qualification'
            }
          }));
      } else { group.controls.kinshipIncome.clearValidators(); }
       if (!kinshipAgreement) {
        group.controls.kinshipAgreement.setValidators(
          ServicesByAreaDetailValidator.setErrorValidator({
            MSG_RSRC_KNSHP_AGREEMENT_IND_REQ: {
              customFieldName: 'Kinship Agreement Form'
            }
          }));
      } else { group.controls.kinshipAgreement.clearValidators(); }
    }else{
      group.controls.kinshipHomeAssessment.clearValidators();
      group.controls.kinshipIncome.clearValidators();
      group.controls.kinshipAgreement.clearValidators();
    }
    group.controls.kinshipHomeAssessment.updateValueAndValidity({ onlySelf: true });
    group.controls.kinshipIncome.updateValueAndValidity({ onlySelf: true });
    group.controls.kinshipAgreement.updateValueAndValidity({ onlySelf: true });
    return null;
  }

  static setErrorValidator(errorObject: any): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return errorObject;
    };
  }

}
