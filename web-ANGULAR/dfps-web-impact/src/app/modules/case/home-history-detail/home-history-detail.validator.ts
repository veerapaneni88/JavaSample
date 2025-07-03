import { AbstractControl, FormGroup } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';
export class HomeHistoryDetailValidators {

    static startDateValidation(previousEndDate) {
        return (group: FormGroup): { [key: string]: any } => {
            const startDate = group.controls.startDate;
            if(DfpsCommonValidators.validateDate(startDate)){     
                    startDate.setErrors({validateDate:{actualValue : startDate.value}}) 
            }else if (previousEndDate && (new Date(startDate.value) < new Date(previousEndDate)) && 
                        (new Date(startDate.value) > new Date())) {
                startDate.setErrors({ startDateErrorMessage: { startDate: true } });
            } else if (previousEndDate && new Date(startDate.value) < new Date(previousEndDate)) {
                startDate.setErrors({ MSG_SUB_PERIOD_OVERLAP_1: { startDate: true } });
            } else if (new Date(startDate.value) > new Date()) {
                startDate.setErrors({ SSM_DATE_BEFORE_SAME_CURR: { startDate: true } });
            } else {
                startDate.setErrors({});
            }
            return null;
        }
    }

    static endDateValidation(nextStartDate) {
        return (group: FormGroup): { [key: string]: any } => {
            const startDate = group.controls.startDate;
            const endDate = group.controls.endDate;
            if(DfpsCommonValidators.validateDate(endDate)){     
                endDate.setErrors({validateDate:{actualValue : endDate.value}}) 
            }else if (new Date(endDate.value) > new Date(nextStartDate)) {
                endDate.setErrors({ MSG_SUB_PERIOD_OVERLAP_2: { endDate: true } });
            } else if (new Date(endDate.value) < new Date(startDate.value)) {
                if (new Date(startDate.value) > new Date()) {
                    startDate.setErrors({ startDateCurrentAndEndDateCOmparison: { startDate: true } });
                } else {
                    startDate.setErrors({ SSM_START_BEFORE_SAME_END: { startDate: true } });
                }
            } else if (new Date(endDate.value) > new Date()) {
                endDate.setErrors({ SSM_DATE_BEFORE_SAME_CURR: { endDate: true } });
            } else {
                endDate.setErrors({});
            }
            return null;
        }
    }

    static homeTypeValidation(nonFpsPca) {
        return (group: FormGroup): { [key: string]: any } => {
            const category = group.controls.category;
            const homeType = group.controls.selectedHomeTypeCodes;
            if (!(category.value === 'A' || category.value === 'O') && !homeType.value.length) {
                homeType.setErrors({ MSG_FAD_FOST_TYPE: { homeType: true, hideFieldName: true } });
            }
            else {
                homeType.setErrors({});
            }
            return null;
        }
    }

    static relationshipValidation(nonFpsPca) {
        return (group: FormGroup): { [key: string]: any } => {
            const category = group.controls.category;
            const status = group.controls.status;
            const familyRelationshipType = group.controls.selectedFamRelTypeCodes;
            let atLeastOneFamRelTypeChecked = false;
            if (familyRelationshipType.value.length > 0) {
                atLeastOneFamRelTypeChecked = true;
            }
            let isOneOfFosterCategory = false;
            if (category && (category.value === 'F' || category.value === 'G' || category.value === 'S' || category.value === 'V')) {
                isOneOfFosterCategory = true;
            }
            if (status && (!(status.value === '010' || status.value === '020'))) {
                if (!atLeastOneFamRelTypeChecked && isOneOfFosterCategory && !nonFpsPca) {
                    familyRelationshipType.setErrors({ MSG_FAD_ATLEAST_ONE_RELATIONSHIP_TYPE: { homeType: true, hideFieldName: true } });
                }
                else if (!familyRelationshipType.value.includes('K' || 'L') && isOneOfFosterCategory && nonFpsPca) {
                    familyRelationshipType.setErrors({ MSG_FAD_REL_OR_FIC_REQD: { homeType: true, hideFieldName: true } });
                } else {
                    familyRelationshipType.setErrors({});
                }
            } else {
                familyRelationshipType.setErrors({});
            }

            return null;
        }
    }

    static capacityValidation = (group: FormGroup): { [key: string]: any } => {
        const capacity = group.controls.capacity;
        const status = group.controls.status;
        if (capacity.value === '0' || capacity.value === '') {
            switch (status.value) {
                case '040':
                    capacity.setErrors({ MSG_ACTIVE_NO_HOME_CAPACITY: { capacity: true } });
                    break;
                case '050':
                    capacity.setErrors({ MSG_INACTIVE_NO_HOME_CAPACITY: { capacity: true } });
                    break;
                case '060':
                    capacity.setErrors({ MSG_PEND_NO_HOME_CAPACITY: { capacity: true } });
                    break;
                default: capacity.setErrors({});
                    break;
            }
            return null;
        }else{
            const pattern = /^[.\d]+$/;
             if(!pattern.test(capacity.value)){
                capacity.setErrors({validateNumber: {actualValue: capacity.value}})
             }
        }
        return null;
    }

    static closureValidation = (group: FormGroup): { [key: string]: any } => {
        const closureReason = group.controls.closureReason;
        const status = group.controls.status;
        if (!closureReason.value) {
            if (status.value === '080') {
                closureReason.setErrors({ MSG_PENDCLS_NO_CLO_REASON: { closureReason: true } });
            }
            else if (status.value === '070') {
                closureReason.setErrors({ MSG_CLS_NO_CLO_REASON: { closureReason: true } });
            }else{
                closureReason.setErrors({});
            }
        } else {
            if (status.value !== '080' && status.value !== '070') {
                closureReason.setErrors({ MSG_NO_CLOSURE: { closureReason: true } });
            }else{
                closureReason.setErrors({})
            }
        }
        return null;
    }

    static ageRangeValidation = (group: FormGroup): { [key: string]: any } => {
        const status = group.controls.status;
        const maleMinAgeYears = group.controls.minAgeMaleChildYear.value ? Number(group.controls.minAgeMaleChildYear.value) : 0;
        const maleMinAgeMonths = group.controls.minAgeMaleChildMonth.value ? Number(group.controls.minAgeMaleChildMonth.value) : 0;
        const maleMinAgeInMonths = (maleMinAgeYears * 12) + maleMinAgeMonths;
        const maleMaxAgeYears = group.controls.maxAgeMaleChildYear.value ? Number(group.controls.maxAgeMaleChildYear.value) : 0;
        const maleMaxAgeMonths = group.controls.maxAgeMaleChildMonth.value ? Number(group.controls.maxAgeMaleChildMonth.value) : 0;
        const maleMaxAgeInMonths = (maleMaxAgeYears * 12) + maleMaxAgeMonths;
        const femaleMinAgeYears = group.controls.minAgeFemaleChildYear.value ? Number(group.controls.minAgeFemaleChildYear.value) : 0;
        const femaleMinAgeMonths = group.controls.minAgeFemaleChildMonth.value ? Number(group.controls.minAgeFemaleChildMonth.value) : 0;
        const femaleMinAgeInMonths = (femaleMinAgeYears * 12) + femaleMinAgeMonths;
        const femaleMaxAgeYears = group.controls.maxAgeFemaleChildYear.value ? Number(group.controls.maxAgeFemaleChildYear.value) : 0;
        const femaleMaxAgeMonths = group.controls.maxAgeFemaleChildMonth.value ? Number(group.controls.maxAgeFemaleChildMonth.value) : 0;
        const femaleMaxAgeInMonths = (femaleMaxAgeYears * 12) + femaleMaxAgeMonths;
        if (status.value === '040' || status.value === '050' || status.value === '060') {
            if (maleMinAgeInMonths === 0 && maleMaxAgeInMonths === 0 && 
                femaleMinAgeInMonths === 0 && femaleMaxAgeInMonths === 0) {
                group.controls.ageRangeErrorMsg.setErrors({ MSG_FAD_AGE_REQ: { hideFieldName: true } });
            }else{
                group.controls.ageRangeErrorMsg.setErrors({});
            }
        }

        if (maleMinAgeInMonths !== 0 && maleMaxAgeInMonths === 0) {
            group.controls.maxAgeMaleChildYear.setErrors({ MSG_MIN_MALE_RANGE: {'Max Year' : true  } });
        } else if (maleMinAgeInMonths === 0 && maleMaxAgeInMonths !== 0) {
            group.controls.minAgeMaleChildYear.setErrors({ MSG_MAX_MALE_RANGE: { 'Min Year': true } });
        } else if (maleMinAgeInMonths > maleMaxAgeInMonths) {
            group.controls.minAgeMaleChildYear.setErrors({ SSM_FAD_MIN_LESS_MAX: { 'Min Year': true } });
        }else{
            group.controls.maxAgeMaleChildYear.setErrors({});
            group.controls.minAgeMaleChildYear.setErrors({});
        }

        if (femaleMinAgeInMonths !== 0 && femaleMaxAgeInMonths === 0) {
            group.controls.maxAgeFemaleChildYear.setErrors({ MSG_MIN_FEMALE_RANGE: { 'Max Year': true } });
        } else if (femaleMinAgeInMonths === 0 && femaleMaxAgeInMonths !== 0) {
            group.controls.minAgeFemaleChildYear.setErrors({ MSG_MAX_FEMALE_RANGE: { 'Min Year': true } });
        } else if (femaleMinAgeInMonths > femaleMaxAgeInMonths) {
            group.controls.minAgeFemaleChildYear.setErrors({ SSM_FAD_MIN_LESS_MAX: { 'Min Year': true } });
        }else{
            group.controls.maxAgeFemaleChildYear.setErrors({});
            group.controls.minAgeFemaleChildYear.setErrors({})
        }
        return null;
    }

    static categoryTypeValidation = (group: FormGroup): { [key: string]: any } => {
        const category = group.controls.category;
        const familyRelationshipType = group.controls.selectedFamRelTypeCodes;
        let counter = 0;
        for (const type of familyRelationshipType.value) {
            if (familyRelationshipType.value.includes('X') || familyRelationshipType.value.includes('Y') || 
                    familyRelationshipType.value.includes('Z')) {
                counter++;
            }
        }
        if (category.value === 'K') {
            if (counter === 0 || counter > 1) {
                familyRelationshipType.setErrors({ MSG_FAD_KIN_FOST_TYPE: { familyRelationshipType: true } });
            }
        } else {
            if (counter > 0) {
                familyRelationshipType.setErrors({ MSG_FAD_NO_KIN_FOST_TYPE: { familyRelationshipType: true } });
            }
        }
        return null;
    }
}