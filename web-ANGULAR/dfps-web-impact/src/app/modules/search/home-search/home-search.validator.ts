import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export class HomeSearchValidators {


  static validateSearchCriteria() {

    return (group: AbstractControl): ValidationErrors | null => {
      const homeName = group.get('homeName').value ? group.get('homeName').value : '';
      const resourceId = group.get('resourceId').value ? group.get('resourceId').value : '';
      const region = group.get('region').value ? group.get('region').value : '';
      const county = group.get('county').value ? group.get('county').value : '';
      const city = group.get('city').value ? group.get('city').value : '';
      const category = group.get('category').value ? group.get('category').value : '';
      const type = group.get('type').value ? group.get('type').value : '';
      const status = group.get('status').value ? group.get('status').value : '';
      const language = group.get('language').value ? group.get('language').value : '';
      const gender = group.get('gender').value ? group.get('gender').value : '';
      const openSlots = group.get('openSlots').value ? group.get('openSlots').value : '';
      const minYear = group.get('minYear').value ? group.get('minYear').value : 0;
      const minMonth = group.get('minMonth').value ? group.get('minMonth').value : 0;
      const maxMonth = group.get('maxMonth').value ? group.get('maxMonth').value : 0;
      const maxYear = group.get('maxYear').value ? group.get('maxYear').value : 0;
      const minAge: number = +(minYear * 12) + +minMonth;
      const maxAge: number = +(maxYear * 12) + +maxMonth;
      const selectedCharacteristics = group.get('selectedCharacteristics').value ? group.get('selectedCharacteristics').value : '';
      group.get('atleastOneRequiredParam').clearValidators();
      if (selectedCharacteristics && selectedCharacteristics.length > 5) {
        group.get('selectedCharacteristics').setValidators(HomeSearchValidators.setValidateMaxCharacteristics());
      }
      else {
        group.get('selectedCharacteristics').clearValidators();
      }
      const NAME_PATTERN = /^\d/;
      const NUMERIC_PATTERN = /^\d+$/;
      let isValidForSearch = true;
      let noExtraParams = true;
      if (homeName.length > 0 || city.length > 0 || gender.length > 0
        || status.length > 0 || county.length > 0 || type.length > 0
        || language.length > 0 || openSlots.length > 0 || minAge !== 0
        || maxAge !== 0 || selectedCharacteristics.length > 0) {
        noExtraParams = false;
      }

      let onlyCategoryRegionValid = false;
      let isCategoryValid = false;
      let isRegionValid = false;
      if (category.length > 0) {
        isCategoryValid = true;
      }
      if (region.length > 0) {
        isRegionValid = true;
      }
      if ((isCategoryValid && !isRegionValid) || (!isCategoryValid && isRegionValid)) {
        onlyCategoryRegionValid = true;
      }


      if (resourceId) {
        isValidForSearch = true;
      }

      if (!resourceId && region === '98' && !homeName && status.length === 0
        && county.length === 0 && !isCategoryValid && city.length === 0) {
        isValidForSearch = false;
        group.get('isValidated').setValidators(HomeSearchValidators.setValidateSearchCriteria());
      }
      else {
        group.get('isValidated').clearValidators();
      }

      if (!resourceId && homeName && region === '98') {
        isValidForSearch = false;
        group.get('homeName').setValidators(HomeSearchValidators.setValidateRegionMisMatch());
      }
      else if (!resourceId && homeName && NAME_PATTERN.test(homeName)) {
        isValidForSearch = false;
        group.get('homeName').setValidators(HomeSearchValidators.setValidateHomeName());
      }
      else {
        group.get('homeName').clearValidators();
      }

      if (!resourceId && city && NAME_PATTERN.test(city)) {
        isValidForSearch = false;
        group.get('city').setValidators(HomeSearchValidators.setValidateHomeName());
      }
      else {
        group.get('city').clearValidators();
      }

      if (!resourceId && openSlots && !NUMERIC_PATTERN.test(openSlots)) {
        isValidForSearch = false;
        group.get('openSlots').setValidators(HomeSearchValidators.setValidateOpenSlots());
      }
      else {
        group.get('openSlots').clearValidators();
      }

      // to search one of these field is required : resource id, resource name, category or region
      if (resourceId.length === 0 && homeName.length === 0 && !isRegionValid && !isCategoryValid) {
        isValidForSearch = false;
        group.get('atleastOneRequiredParam').setValidators(HomeSearchValidators.setValidateResourceMismatch());
      }
      else {
        group.get('atleastOneRequiredParam').clearValidators();
      }

      if (gender && (minAge === 0 || maxAge === 0)) {
        group.get('minYear').setValidators(HomeSearchValidators.setValidateGender());
      }
      else if (gender && (+minAge > +maxAge)) {
        group.get('minYear').setValidators(HomeSearchValidators.setValidateAge());
      }
      else {
        group.get('minYear').clearValidators();
      }

      if (!gender && (minAge !== 0 || maxAge !== 0)) {
        isValidForSearch = false;
        group.get('gender').setValidators(HomeSearchValidators.setValidateGenderRequired());
      }
      else {
        group.get('gender').clearValidators();
      }

      if (!homeName && isValidForSearch && noExtraParams && onlyCategoryRegionValid) {
        isValidForSearch = false;
        group.get('hasAllRequiredParam').setValidators(HomeSearchValidators.setValidateSearchCriteria());
      }
      else {
        group.get('hasAllRequiredParam').clearValidators();
      }

      group.get('isValidated').updateValueAndValidity({ onlySelf: true });
      group.get('category').updateValueAndValidity({ onlySelf: true });
      group.get('homeName').updateValueAndValidity({ onlySelf: true });
      group.get('region').updateValueAndValidity({ onlySelf: true });
      group.get('minYear').updateValueAndValidity({ onlySelf: true });
      group.get('gender').updateValueAndValidity({ onlySelf: true });
      group.get('city').updateValueAndValidity({ onlySelf: true });
      group.get('atleastOneRequiredParam').updateValueAndValidity({ onlySelf: true });
      group.get('hasAllRequiredParam').updateValueAndValidity({ onlySelf: true });
      group.get('selectedCharacteristics').updateValueAndValidity({ onlySelf: true });
      group.get('openSlots').updateValueAndValidity({ onlySelf: true });
      return null;
    };
  }

  static setValidateSearchCriteria() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        SSM_ENTER_CRITERIA: {
          hideFieldName: true,
          SSM_ENTER_CRITERIA: true
        }
      };
    };
  }

  static setValidateMaxCharacteristics() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_FAD_NO_MORE_CHAR: {
          hideFieldName: true,
          MSG_FAD_NO_MORE_CHAR: true
        }
      };
    };
  }

  static setValidateRegionMisMatch() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        SSM_FAD_REGION_MISMATCH: {
          hideFieldName: false,
          SSM_FAD_REGION_MISMATCH: true
        }
      };
    };
  }
  static setValidateHomeName() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        SSM_FAD_INVALID_HOME_NAME: {
          hideFieldName: false,
          SSM_FAD_INVALID_HOME_NAME: true
        }
      };
    };
  }
  static setValidateOpenSlots() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_NUMERIC_NOT_VALID: {
          hideFieldName: false,
          MSG_NUMERIC_NOT_VALID: true,
          actualValue: control.value
        }
      };
    };
  }

  static setValidateResourceMismatch() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_RSRC_ENT_SEARCH_PARAM: {
          hideFieldName: true,
          MSG_RSRC_ENT_SEARCH_PARAM: true
        }
      };
    };
  }

  static setValidateGender() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_FAD_AGE_REQ: {
          hideFieldName: false,
          MSG_FAD_AGE_REQ: true
        }
      };
    };
  }

  static setValidateAge() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        SSM_MIN_LESS_MAX: {
          hideFieldName: true,
          SSM_MIN_LESS_MAX: true
        }
      };
    };
  }

  static setValidateGenderRequired() {
    return (control: AbstractControl): ValidationErrors | null => {
      return {
        MSG_GENDER_REQ_AGE_RNGE: {
          hideFieldName: false,
          MSG_GENDER_REQ_AGE_RNGE: true
        }
      };
    };
  }

  static homeSearchCityPattern = (group: FormGroup): { [key: string]: any } => {
    const city = group.controls.city.value;
    const CITY_PATTERN = /^[A-Z,a-z,\s\-]+$/;
    if (city && !CITY_PATTERN.test(city)) {
      group.controls.city.setValidators(
        HomeSearchValidators.setErrorValidator({
          resourceSearchCityAlphaPattern: { actualValue: city }
        }))
    } else {
      group.controls.city.clearValidators();
    }
    group.controls.city.updateValueAndValidity({ onlySelf: true });
    return null;
  }


  static setErrorValidator(errorObject: any): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      return errorObject;
    };
  }


}

