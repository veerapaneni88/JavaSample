import { AbstractControl, ValidationErrors, ValidatorFn, FormGroup } from '@angular/forms';

export class ResourceSearchValidators {
  static validateNumberType = (group: FormGroup): { [key: string]: any } => {
    const idNumberValue = group.controls.idNumberValue.value;
    const streetAddress = group.controls.locationStreetAddress.value;
    const city = group.controls.locationCity.value;
    const NUMBER_PATTERN = /^[0-9]+$/;
    if (group.controls.idNumberType.value && group.controls.idNumberType.value.length > 0) {
      switch (group.controls.idNumberType.value) {
        case 'MHM':
          if (idNumberValue.length > 5 || idNumberValue.length < 2) {
            group.controls.idNumberValue.setErrors( null );
            group.controls.idNumberValue.setErrors({ resourceSearchMHMRCode: {
              actualValue: idNumberValue,
              customFieldName: 'Number Type'
            } });
          } else {
            group.controls.idNumberValue.setErrors( null );
          }
          break;
        case 'CON':
          if (!NUMBER_PATTERN.test(idNumberValue) ||
          idNumberValue.length > 9 || idNumberValue.length < 2) {
            group.controls.idNumberValue.setErrors( null );
            group.controls.idNumberValue.setErrors({ resourceSearchAgencyAcctID: {
              actualValue: idNumberValue,
              customFieldName: 'Number Type'
            } });
          } else {
            group.controls.idNumberValue.setErrors( null );
          }
          break;
        case 'LIC':
          if (!NUMBER_PATTERN.test(idNumberValue) ||
          idNumberValue.length > 8 || idNumberValue.length < 2) {
            group.controls.idNumberValue.setErrors( null );
            group.controls.idNumberValue.setErrors({ resourceSearchFacilityNumber: {
              customFieldName: 'Number Type',
              actualValue: idNumberValue,
            }});
          } else {
            group.controls.idNumberValue.setErrors( null );
          }
          break;
        case 'PHN':
          if (!NUMBER_PATTERN.test(idNumberValue) ||
          idNumberValue.length !== 10) {
            group.controls.idNumberValue.setErrors( null );
            group.controls.idNumberValue.setErrors({ resourceSearchPhoneNumber: {
              customFieldName: 'Number Type',
              actualValue: idNumberValue,
            }});
          } else {
            group.controls.idNumberValue.setErrors( null );
          }
          break;
        case 'RSC':
          if (!NUMBER_PATTERN.test(idNumberValue) ||
          idNumberValue.length > 9 || idNumberValue.length < 2) {
            group.controls.idNumberValue.setErrors( null );
            group.controls.idNumberValue.setErrors({ resourceSearchResourceNumber: {
              customFieldName: 'Number Type',
              actualValue: idNumberValue,
            }});
          } else {
            group.controls.idNumberValue.setErrors( null );
          }
          break;
        case 'CNU':
          if (!NUMBER_PATTERN.test(idNumberValue) ||
          idNumberValue.length > 9 || idNumberValue.length < 2) {
            group.controls.idNumberValue.setErrors( null );
            group.controls.idNumberValue.setErrors({ resourceSearchContractNumber: {
              customFieldName: 'Number Type',
              actualValue: idNumberValue,
            }});
          } else {
            group.controls.idNumberValue.setErrors( null );
          }
          break;
      }
    } else if ((!group.controls.idNumberType.value || group.controls.idNumberType.value.length === 0) &&
    (idNumberValue && idNumberValue.length > 0) ) {
      group.controls.idNumberValue.setErrors( null );
      group.controls.idNumberType.setErrors({ resourceSearchNumberType: {
        customFieldName: 'Number Type',
        actualValue: idNumberValue,
      }});
     } else if ((!group.controls.idNumberType.value || group.controls.idNumberType.value.length === 0) &&
      (!idNumberValue || idNumberValue.length === 0)) {
      group.controls.idNumberValue.setErrors( null );
      group.controls.idNumberType.setErrors( null );
    }

    if ((streetAddress && streetAddress.trim().length > 0) &&
      (!city || city.trim().length === 0)) {
      group.controls.locationCity.setErrors({ resourceSearchCityRequired: {
        customFieldName: 'City'
      }});
    } else if (((streetAddress && streetAddress.trim().length > 0) &&
    (city && city.trim().length > 0)) || !streetAddress || streetAddress.trim().length === 0) {
      group.controls.locationCity.setErrors( null );
    }

    return null;
  }
  static resourceType(control: AbstractControl): ValidationErrors | null {
    if (!control.value || (control.value as string).trim().length === 0) {
      return {
        resourceType: {
          errorMessage:
            'You must enter at least one required search parameter before beginning a search.',
        },
      };
    }
    return null;
  }
  static resourceSearchAge(control: AbstractControl): ValidationErrors | null {
    const NUMBER_PATTERN = /^[0-9]+$/;
    if (control.value && (control.value as string).trim().length > 0 && !NUMBER_PATTERN.test(control.value)) {
      return {
        resourceSearchAge: {
          actualValue: control.value,
          customFieldName: 'Age'
        },
      };
    }
    return null;
  }

  static resourceSearchCityAlphaPattern(control: AbstractControl): ValidationErrors | null {
    const ALPHA_PATTERN = /^[a-zA-Z]+$/;
    if (control.value && (control.value as string).trim().length > 0 && !ALPHA_PATTERN.test(control.value)) {
      return {
        resourceSearchCityAlphaPattern: {
          actualValue: control.value,
          customFieldName: 'City'
        },
      };
    }
    return null;
  }

  static numericValues(control: AbstractControl): ValidationErrors | null {
    const NUMBER_PATTERN = /^[0-9]+$/;
    if (control.value && (control.value as string).trim().length > 0 && !NUMBER_PATTERN.test(control.value)) {
      return {
        numericValues: true
      };
    }
    return null;
  }

  static resourceSearchResourceName(control: AbstractControl): ValidationErrors | null {
    if (control.value && control.value.trim().length > 0 && control.value.trim().length < 2) {
      return {
        resourceSearchResourceName: {
          actualValue: control.value,
          customFieldName: 'Resource Name'
        }
      };
    }
    return null;
  }
  static required(fieldName?: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value || (control.value as string).trim().length === 0) {
        return {
          required: {
            customFieldName: fieldName,
          },
        };
      }
      return null;
    };
  }
  static alphaNumericHyphen(fieldName?: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const PATTERN = /^[-a-zA-Z0-9]+$/;
      if (control.value && !PATTERN.test(control.value)) {
        return {
          alphaNumericHyphen: {
            actualValue: control.value,
            customFieldName: fieldName,
          },
        };
      }
      return null;
    };
  }
  static alphaNumeric(fieldName?: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const PATTERN = /^[a-zA-Z0-9]+$/;
      if (control.value && !PATTERN.test(control.value)) {
        return {
          alphaNumeric: {
            actualValue: control.value,
            customFieldName: fieldName,
          },
        };
      }
      return null;
    };
  }

  static zipPattern(fieldName?: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value) {
        const ZIP_PATTERN = /[0-9]{5}/;
        if (!ZIP_PATTERN.test(control.value)) {
          return {
            zipPattern: {
              actualValue: control.value,
              customFieldName: fieldName,
            },
          };
        }
        return null;
      } else {
        return null;
      }
    };
  }

  static zipExtensionPattern(fieldName?: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value) {
        const ZIP_EXT_PATTERN = /[0-9]{4}/;
        if (!ZIP_EXT_PATTERN.test(control.value)) {
          return {
            zipExtensionPattern: {
              actualValue: control.value,
              customFieldName: fieldName,
            },
          };
        }
        return null;
      } else {
        return null;
      }
    };
  }

  static organizationEinPattern(fieldName?: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value) {
        const EIN_PATTERN = /^[0-9]{9}$/;
        if (!EIN_PATTERN.test(control.value)) {
          return {
            einPattern: {
              actualValue: control.value,
              customFieldName: fieldName,
            },
          };
        }
        return null;
      } 
    };
  }
}
