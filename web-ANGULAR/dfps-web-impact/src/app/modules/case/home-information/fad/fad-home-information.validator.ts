import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { formatDate } from '@angular/common';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class FadHomeInformationValidators {

    static validateHomeInformation = (group: FormGroup): { [key: string]: any } => {
        const buttonClicked = group.controls.buttonClicked.value;
        if (buttonClicked === 'assign') {
            FadHomeInformationValidators.assignValidations(group);
        } else if (buttonClicked === 'save') {
            FadHomeInformationValidators.commonValidations(group);
            FadHomeInformationValidators.saveValidations(group);
        } else if (buttonClicked === 'submit') {
            FadHomeInformationValidators.submitValidations(group);
            FadHomeInformationValidators.commonValidations(group);
        }
        return null;
    }

    static saveValidations = (group: FormGroup): { [key: string]: any } => {
        const nonPRSPCA = group.controls.nonPRSPCA.value;
        const nonPRSHome = group.controls.nonPRSHome.value;
        const status = group.controls.status.value;
        const childesEthnicity = group.controls.childesEthnicity.value;
        const ethnicityMessageForSave = group.controls.ethnicityMessageForSave.value;
        const relationship = group.controls.selectedHomeTypeRelationship.value;
        const businessAddressExists = FadHomeInformationValidators.checkBusinessAddressExists(group);
        const businessAddressVendorIdExists = FadHomeInformationValidators.checkVendorIdExistsForBusinessAddress(group);
        const approver = group.controls.approver.value;
        const isHomeLicenseValuesChanged = group.controls.isHomeLicenseValuesChanged.value;

        const isClosure = (status === '070' || status === '080');

        FadHomeInformationValidators.supervisorApprovalValidations(group);

        if (!approver && nonPRSHome && childesEthnicity.length === 0) {
            group.controls.ethnicityMessageForSave.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_FAD_ETHNIC_REQ: {
                        actualValue: ethnicityMessageForSave, hideFieldName: true
                    }
                }));
        } else {
            group.controls.ethnicityMessageForSave.clearValidators();
        }
        if (
            (!isClosure && nonPRSHome && (!businessAddressExists || !businessAddressVendorIdExists)) ||
            (nonPRSPCA && (relationship && (relationship.includes('L') || relationship.includes('K'))
                && (!businessAddressExists || !businessAddressVendorIdExists)))) {
            group.controls.businessAddressMessage.setValidators(
                FadHomeInformationValidators.setErrorValidator(
                    { MSG_VENDOR_ID_NUMBER: { hideFieldName: true } }));
        } else {
            group.controls.businessAddressMessage.clearValidators();
        }
        group.controls.ethnicityMessageForSave.updateValueAndValidity({ onlySelf: true });
        group.controls.businessAddressMessage.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static submitValidations = (group: FormGroup): { [key: string]: any } => {
        const nonPRSPCA = group.controls.nonPRSPCA.value;
        const status = group.controls.status.value;
        const childesEthnicity = group.controls.childesEthnicity.value;
        const maritalStatus = group.controls.maritalStatus.value;
        const closureReason = group.controls.closureReason.value;
        const errorMessageForSaveSubmit = group.controls.errorMessageForSaveSubmit.value;
        const statusMessage = group.controls.statusMessage.value;
        const ethnicityMessage = group.controls.ethnicityMessage.value;
        const resourceAddress = group.controls.resourceAddress.value;
        const businessAddressVendorIdExists = FadHomeInformationValidators.checkVendorIdExistsForBusinessAddress(group);
        const isClosure = (status === '070' || status === '080');

        if (status !== '060' && status !== '080') {
            group.controls.statusMessagePend.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_FAD_PENDING_STATUS_ONLY: {
                        hideFieldName: true
                    }
                }));
            group.controls.statusMessagePend.updateValueAndValidity({ onlySelf: true });
        } else {
            group.controls.statusMessagePend.clearValidators();
            group.controls.statusMessagePend.updateValueAndValidity({ onlySelf: true });
        }

        if (!group.controls.statusMessagePend.invalid) {
            if (!isClosure && !nonPRSPCA && childesEthnicity.length === 0) {
                group.controls.ethnicityMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_ETHNIC_REQ: {
                            actualValue: ethnicityMessage, hideFieldName: true
                        }
                    }));
            } else {
                group.controls.ethnicityMessage.clearValidators();
            }

            if (!isClosure && !businessAddressVendorIdExists) {
                group.controls.resourceAddress.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_VENDOR_ID_NUMBER:
                            { actualValue: resourceAddress, hideFieldName: true }
                    }));
            } else {
                group.controls.resourceAddress.clearValidators();
            }

            if (!isClosure && !closureReason && status !== '060' && errorMessageForSaveSubmit) {
                group.controls.statusMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_CHANGE_TO_PEND: {
                            actualValue: statusMessage, hideFieldName: true
                        }
                    }));
            } else {
                group.controls.statusMessage.clearValidators();
            }
        } else {
            group.controls.ethnicityMessage.clearValidators();
            group.controls.resourceAddress.clearValidators();
        }

        group.controls.statusMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.statusMessagePend.updateValueAndValidity({ onlySelf: true });
        group.controls.ethnicityMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.resourceAddress.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static assignValidations = (group: FormGroup): { [key: string]: any } => {
        const status = group.controls.status.value;
        const previousStatus = group.controls.previousStatus.value;

        const statusMessage = group.controls.statusMessage.value;
        const locale = group.controls.locale.value;
        const closeDate = new Date(group.controls.closeDate.value);
        const currentDate = new Date(formatDate(new Date(), 'MM/dd/yyyy', locale));
        FadHomeInformationValidators.supervisorApprovalValidations(group);

        if (previousStatus === '070' && status !== '070'
            && (closeDate.getDate() === currentDate.getDate())
            && (closeDate.getFullYear() === currentDate.getFullYear())
            && (closeDate.getMonth() === currentDate.getMonth())) {
            group.controls.closeDate.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_HOME_REOPEN_DAY_WAIT: {
                        hideFieldName: true
                    }
                }));
        } else {
            group.controls.closeDate.clearValidators();
        }

        if (status === '070') {
            group.controls.statusMessage.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_CMN_STAGE_CLOSED: {
                        actualValue: statusMessage, hideFieldName: true
                    }
                }));
        } else {
            group.controls.statusMessage.clearValidators();
        }
        group.controls.closeDate.updateValueAndValidity({ onlySelf: true })
        group.controls.statusMessage.updateValueAndValidity({ onlySelf: true })
        return null;
    }

    static commonValidations = (group: FormGroup): { [key: string]: any } => {
        const facilityCapacity = group.controls.facilityCapacity.value;
        const nonPRSPCA = group.controls.nonPRSPCA.value;
        const nonPRSHome = group.controls.nonPRSHome.value;
        const certifyEntity = group.controls.certifyEntity.value;
        const status = group.controls.status.value;
        const maxNoChildren = group.controls.maxNoChildren.value;
        const maritalStatus = group.controls.maritalStatus.value;
        const closureReason = group.controls.closureReason.value;
        const NUMBER_PATTERN = /^[0-9]+$/;
        const certifyingEntityMessage = group.controls.certifyingEntityMessage.value;
        const maxNoChildrenMessage = group.controls.maxNoChildrenMessage.value;
        const resourcePhone = group.controls.resourcePhone.value;
        const homeType = group.controls.selectedHomeTypeData.value;
        const relationship = group.controls.selectedHomeTypeRelationship.value;
        const category = group.controls.category.value;
        const placements = group.controls.placements.value;
        const relationshipMessage = group.controls.relationshipMessage.value;
        const placementsMessage = group.controls.placementsMessage.value;
        const interestAgeMessage = group.controls.interestAgeMessage.value;
        const primaryPhoneExists = FadHomeInformationValidators.checkPrimaryPhoneExists(group);
        const annualIncome = group.controls.annualIncome.value;
        const maritalStatusMessage = group.controls.maritalStatusMessage.value;
        const buttonClicked = group.controls.buttonClicked.value;

        const atLeastOneFamRelTypeChecked = (relationship && relationship.length > 0);
        const isOneOfFosterCategory = (category === 'F' || category === 'G' || category === 'S' || category === 'V');

        // Licensing Age Ranges
        const selectedMaleMinYear = group.controls.selectedMaleMinYear.value ?
            Number(group.controls.selectedMaleMinYear.value) : 0;
        const selectedMaleMinMonth = group.controls.selectedMaleMinMonth.value ?
            Number(group.controls.selectedMaleMinMonth.value) : 0;
        const maleMinAgeInMonths = (selectedMaleMinYear * 12) + selectedMaleMinMonth;
        const selectedMaleMaxYear = group.controls.selectedMaleMaxYear.value ?
            Number(group.controls.selectedMaleMaxYear.value) : 0;
        const selectedMaleMaxMonth = group.controls.selectedMaleMaxMonth.value ?
            Number(group.controls.selectedMaleMaxMonth.value) : 0;
        const maleMaxAgeInMonths = (selectedMaleMaxYear * 12) + selectedMaleMaxMonth;
        const selectedFemaleMinYear = group.controls.selectedFemaleMinYear.value ?
            Number(group.controls.selectedFemaleMinYear.value) : 0;
        const selectedFemaleMinMonth = group.controls.selectedFemaleMinMonth.value ?
            Number(group.controls.selectedFemaleMinMonth.value) : 0;
        const femaleMinAgeInMonths = (selectedFemaleMinYear * 12) + selectedFemaleMinMonth;
        const selectedFemaleMaxYear = group.controls.selectedFemaleMaxYear.value ?
            Number(group.controls.selectedFemaleMaxYear.value) : 0;
        const selectedFemaleMaxMonth = group.controls.selectedFemaleMaxMonth.value ?
            Number(group.controls.selectedFemaleMaxMonth.value) : 0;
        const femaleMaxAgeInMonths = (selectedFemaleMaxYear * 12) + selectedFemaleMaxMonth;

        // Home Interest Age Ranges
        const selectedMaleMinYearHomeInterest = group.controls.selectedMaleMinYearHomeInterest.value ?
            Number(group.controls.selectedMaleMinYearHomeInterest.value) : 0;
        const selectedMaleMinMonthHomeInterest = group.controls.selectedMaleMinMonthHomeInterest.value ?
            Number(group.controls.selectedMaleMinMonthHomeInterest.value) : 0;
        const maleMinAgeInMonthInts = (selectedMaleMinYearHomeInterest * 12) + selectedMaleMinMonthHomeInterest;
        const selectedMaleMaxYearHomeInterest = group.controls.selectedMaleMaxYearHomeInterest.value ?
            Number(group.controls.selectedMaleMaxYearHomeInterest.value) : 0;
        const selectedMaleMaxMonthHomeInterest = group.controls.selectedMaleMaxMonthHomeInterest.value ?
            Number(group.controls.selectedMaleMaxMonthHomeInterest.value) : 0;
        const maleMaxAgeInMonthInts = (selectedMaleMaxYearHomeInterest * 12) + selectedMaleMaxMonthHomeInterest;
        const selectedFemaleMinYearHomeInterest = group.controls.selectedFemaleMinYearHomeInterest.value ?
            Number(group.controls.selectedFemaleMinYearHomeInterest.value) : 0;
        const selectedFemaleMinMonthHomeInterest = group.controls.selectedFemaleMinMonthHomeInterest.value ?
            Number(group.controls.selectedFemaleMinMonthHomeInterest.value) : 0;
        const femaleMinAgeInMonthInts = (selectedFemaleMinYearHomeInterest * 12) + selectedFemaleMinMonthHomeInterest;
        const selectedFemaleMaxYearHomeInterest = group.controls.selectedFemaleMaxYearHomeInterest.value ?
            Number(group.controls.selectedFemaleMaxYearHomeInterest.value) : 0;
        const selectedFemaleMaxMonthHomeInterest = group.controls.selectedFemaleMaxMonthHomeInterest.value ?
            Number(group.controls.selectedFemaleMaxMonthHomeInterest.value) : 0;
        const femaleMaxAgeInMonthInts = (selectedFemaleMaxYearHomeInterest * 12) + selectedFemaleMaxMonthHomeInterest;

        const interestAgesExist = (maleMaxAgeInMonthInts > 0 || maleMinAgeInMonthInts > 0 ||
            femaleMaxAgeInMonthInts > 0 || femaleMinAgeInMonthInts > 0);

        const licenseAgesExist = (maleMaxAgeInMonths > 0 || maleMinAgeInMonths > 0 ||
            femaleMaxAgeInMonths > 0 || femaleMinAgeInMonths > 0);

        const isClosure = (status === '070' || status === '080');

        let checkAges = true;

        FadHomeInformationValidators.validateMarriageDate(group);

        if (annualIncome) {
            group.controls.annualIncome.setValidators([
                DfpsCommonValidators.validateCurrency(12)
            ]);
        } else { group.controls.annualIncome.clearValidators(); }

        if (facilityCapacity && !NUMBER_PATTERN.test(facilityCapacity)) {
            group.controls.facilityCapacity.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    validateNumber: { actualValue: facilityCapacity, customFieldName: 'Capacity' }
                }));
        } else {
            group.controls.facilityCapacity.clearValidators();
        }

        if (maxNoChildren && !NUMBER_PATTERN.test(maxNoChildren)) {
            group.controls.maxNoChildren.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    validateNumber: { actualValue: maxNoChildren, customFieldName: 'Number of Children' }
                }));
        } else if (!isClosure && (maxNoChildren === '0' || !maxNoChildren)
            && buttonClicked === 'submit' && !group.controls.statusMessagePend.invalid) {
            group.controls.maxNoChildren.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_FAD_NO_INTERESTS: {
                        actualValue: maxNoChildren, customFieldName: 'Number of Children'
                    }
                }));
        } else { group.controls.maxNoChildren.clearValidators(); }

        group.controls.facilityCapacity.updateValueAndValidity({ onlySelf: true });
        group.controls.maxNoChildren.updateValueAndValidity({ onlySelf: true });

        if (!group.controls.statusMessagePend.invalid) {

            if (buttonClicked === 'submit' && !isClosure && !maritalStatus) {
                group.controls.maritalStatusMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_NO_MAR_STATUS: {
                            actualValue: maritalStatusMessage, hideFieldName: true
                        }
                    }));
            } else if (!isClosure && !maritalStatus && nonPRSHome) {
                group.controls.maritalStatusMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_NO_MAR_STATUS: {
                            actualValue: maritalStatusMessage, hideFieldName: true
                        }
                    }));
            } else {
                group.controls.maritalStatusMessage.clearValidators();
            }

            if ((facilityCapacity === '0' || !facilityCapacity) && !isClosure && !nonPRSPCA) {
                if (status === '040') {
                    group.controls.facilityCapacity.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            MSG_ACTIVE_NO_HOME_CAPACITY: { actualValue: facilityCapacity, customFieldName: 'Capacity' }
                        }));
                } else if (status === '050') {
                    group.controls.facilityCapacity.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            MSG_INACTIVE_NO_HOME_CAPACITY: { actualValue: facilityCapacity, customFieldName: 'Capacity' }
                        }));
                } else if (status === '060') {
                    group.controls.facilityCapacity.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            MSG_PEND_NO_HOME_CAPACITY: { actualValue: facilityCapacity, customFieldName: 'Capacity' }
                        }));
                }
            }

            if (!isClosure && nonPRSHome && !certifyEntity) {
                group.controls.certifyingEntityMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_CERTIFY_ENTITY_REQ: {
                            actualValue: certifyingEntityMessage, hideFieldName: true
                        }
                    }));
            } else { group.controls.certifyingEntityMessage.clearValidators(); }

            if (!isClosure && (nonPRSHome && !maxNoChildren)) {
                group.controls.maxNoChildrenMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_REQ_NUM_CHILDREN: {
                            actualValue: maxNoChildrenMessage, hideFieldName: true
                        }
                    }));
            } else {
                group.controls.maxNoChildrenMessage.clearValidators();
            }

            if (buttonClicked === 'submit' && !isClosure && status === '060' && interestAgesExist && !licenseAgesExist) {
                checkAges = false;
                group.controls.interestAgeMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        SSM_FAD_INTEREST_AGE: {
                            actualValue: interestAgeMessage, hideFieldName: true
                        }
                    }));
            } else {
                group.controls.interestAgeMessage.clearValidators();
            }

            if (!isClosure && checkAges && !nonPRSPCA) {
                FadHomeInformationValidators.validateHomeLicenseAgeFields(group);
            }

            const validateHomeInterestAndLicenseAgeFields: boolean = FadHomeInformationValidators.validateHomeInterestAgeFields(group);

            if (!isClosure && !nonPRSPCA && licenseAgesExist && interestAgesExist
                && !['010', '020', '030', '070'].includes(status)
                && validateHomeInterestAndLicenseAgeFields) {
                if (maleMaxAgeInMonths > 0) {
                    if (maleMaxAgeInMonthInts > maleMaxAgeInMonths) {
                        group.controls.selectedMaleMaxYearHomeInterest.setValidators(
                            FadHomeInformationValidators.setErrorValidator({
                                SSM_FAD_MAX_GTR_APVD:
                                {
                                    customFieldName: 'Max Year - Male',
                                }
                            }));
                    } else { group.controls.selectedMaleMaxYearHomeInterest.clearValidators(); }
                }

                if (maleMinAgeInMonths !== 0 && (maleMinAgeInMonthInts < maleMinAgeInMonths)) {
                    group.controls.selectedMaleMinYearHomeInterest.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            SSM_FAD_MIN_LESS_APVD:
                            {
                                customFieldName: 'Min Year - Male',
                            }
                        }));
                } else if (maleMaxAgeInMonths > 0 && (maleMinAgeInMonthInts > maleMaxAgeInMonths)) {
                    group.controls.selectedMaleMinYearHomeInterest.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            SSM_FAD_MIN_GTR_APVD:
                            {
                                customFieldName: 'Min Year - Male',
                            }
                        }));
                } else if (maleMaxAgeInMonthInts !== 0 && (maleMaxAgeInMonthInts < maleMinAgeInMonths)) {
                    group.controls.selectedMaleMinYearHomeInterest.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            SSM_FAD_MAX_LESS_APVD:
                            {
                                customFieldName: 'Min Year - Male',
                            }
                        }));
                } else { group.controls.selectedMaleMinYearHomeInterest.clearValidators(); }

                if (femaleMaxAgeInMonths !== 0) {
                    if (femaleMaxAgeInMonthInts > femaleMaxAgeInMonths) {
                        group.controls.selectedFemaleMaxYearHomeInterest.setValidators(
                            FadHomeInformationValidators.setErrorValidator({
                                SSM_FAD_MAX_GTR_APVD:
                                {
                                    customFieldName: 'Max Year - Female',
                                }
                            }));
                    } else { group.controls.selectedFemaleMaxYearHomeInterest.clearValidators(); }
                }

                if (femaleMinAgeInMonthInts !== 0 && (femaleMinAgeInMonthInts < femaleMinAgeInMonths)) {
                    group.controls.selectedFemaleMinYearHomeInterest.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            SSM_FAD_MIN_LESS_APVD:
                            {
                                customFieldName: 'Min Year - Female',
                            }
                        }));
                } else if (femaleMaxAgeInMonths > 0 && (femaleMinAgeInMonthInts > femaleMaxAgeInMonths)) {
                    group.controls.selectedFemaleMinYearHomeInterest.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            SSM_FAD_MIN_GTR_APVD:
                            {
                                customFieldName: 'Min Year - Female',
                            }
                        }));
                } else if (femaleMaxAgeInMonthInts !== 0 && (femaleMaxAgeInMonthInts < femaleMinAgeInMonths)) {
                    group.controls.selectedFemaleMinYearHomeInterest.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            SSM_FAD_MAX_LESS_APVD:
                            {
                                customFieldName: 'Min Year - Female',
                            }
                        }));
                } else { group.controls.selectedFemaleMinYearHomeInterest.clearValidators(); }
            }

            if (!isClosure && status !== '010' && status !== '070') {
                if (status && (status !== '010' && status !== '020')) {
                    if (!atLeastOneFamRelTypeChecked &&
                        isOneOfFosterCategory && !nonPRSPCA) {
                        group.controls.relationshipMessage.setValidators(
                            FadHomeInformationValidators.setErrorValidator({
                                MSG_FAD_ATLEAST_ONE_RELATIONSHIP_TYPE: {
                                    actualValue: relationshipMessage, hideFieldName: true
                                }
                            }));
                    } else if (!relationship || (relationship && !relationship.includes('K') && !relationship.includes('L')) &&
                        isOneOfFosterCategory && nonPRSPCA) {
                        group.controls.relationshipMessage.setValidators(
                            FadHomeInformationValidators.setErrorValidator({
                                MSG_FAD_REL_OR_FIC_REQD: {
                                    actualValue: relationshipMessage, hideFieldName: true
                                }
                            }));
                    } else { group.controls.relationshipMessage.clearValidators(); }
                } else { group.controls.relationshipMessage.clearValidators(); }

                if ((category !== 'A' && category !== 'O') && !nonPRSPCA && !(homeType && homeType.length)) {
                    group.controls.homeType.setValidators(
                        FadHomeInformationValidators.setErrorValidator({
                            MSG_FAD_FOST_TYPE: { hideFieldName: true }
                        }));
                } else { group.controls.homeType.clearValidators(); }
            } else {
                group.controls.relationshipMessage.clearValidators();
                group.controls.homeType.clearValidators();
            }

            if (!closureReason && status === '080') {
                group.controls.closureReason.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_PENDCLS__NO_CLO_REASON: { actualValue: closureReason }
                    }));
            } else if (!closureReason && status === '070') {
                group.controls.closureReason.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_CLS_NO_CLO_REASON: { actualValue: closureReason }
                    }));
            } else if (closureReason && (status !== '070' && status !== '080')) {
                group.controls.closureReason.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_NO_CLOSURE: { actualValue: closureReason }
                    }));
            } else { group.controls.closureReason.clearValidators(); }

            if (!primaryPhoneExists && !['010', '070', '080'].includes(status)) {
                group.controls.resourcePhone.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_REQ_PRIMARY_PHONE:
                            { actualValue: resourcePhone, hideFieldName: true }
                    }));
            } else { group.controls.resourcePhone.clearValidators(); }

            if (status === '080' && placements > 0) {
                group.controls.placementsMessage.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_PLCMT_CLS: { actualValue: placementsMessage, hideFieldName: true }
                    }));
            } else { group.controls.placementsMessage.clearValidators(); }
        } else {
            group.controls.maritalStatusMessage.clearValidators();
            if (!group.controls.facilityCapacity.invalid) {
                group.controls.facilityCapacity.clearValidators();
            }
            group.controls.certifyingEntityMessage.clearValidators();
            group.controls.maxNoChildrenMessage.clearValidators();
            group.controls.statusMessage.clearValidators();
            group.controls.closureReason.clearValidators();
            group.controls.selectedMaleMaxYear.clearValidators();
            group.controls.selectedMaleMinYear.clearValidators();
            group.controls.selectedFemaleMaxYear.clearValidators();
            group.controls.selectedFemaleMinYear.clearValidators();
            group.controls.interestAgeMessage.clearValidators();
            group.controls.selectedMaleMaxYearHomeInterest.clearValidators();
            group.controls.selectedFemaleMaxYearHomeInterest.clearValidators();
            group.controls.selectedMaleMinYearHomeInterest.clearValidators();
            group.controls.selectedFemaleMinYearHomeInterest.clearValidators();
            group.controls.resourcePhone.clearValidators();
            group.controls.relationshipMessage.clearValidators();
            group.controls.homeType.clearValidators();
            group.controls.placementsMessage.clearValidators();
        }

        group.controls.annualIncome.updateValueAndValidity({ onlySelf: true });
        group.controls.maritalStatusMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.facilityCapacity.updateValueAndValidity({ onlySelf: true });
        group.controls.certifyingEntityMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.maxNoChildrenMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.statusMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.closureReason.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedMaleMaxYear.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedMaleMinYear.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMaxYear.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMinYear.updateValueAndValidity({ onlySelf: true });
        group.controls.interestAgeMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedMaleMaxYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMaxYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedMaleMinYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMinYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        group.controls.resourcePhone.updateValueAndValidity({ onlySelf: true });
        group.controls.relationshipMessage.updateValueAndValidity({ onlySelf: true });
        group.controls.homeType.updateValueAndValidity({ onlySelf: true });
        group.controls.placementsMessage.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static checkBusinessAddressExists(group: FormGroup): boolean {
        const resourceAddress = group.controls.resourceAddress.value;
        if (resourceAddress && resourceAddress.length) {
            return resourceAddress.some(resource => {
                return resource.addressType && resource.addressType === 'Business'
                    && resource.addressLine1;
            })
        }
        return false;
    }

    static checkVendorIdExistsForBusinessAddress(group: FormGroup): boolean {
        const resourceAddress = group.controls.resourceAddress.value;
        if (resourceAddress && resourceAddress.length) {
            return resourceAddress.some(resource => {
                return resource.addressType && resource.addressType === 'Business'
                    && resource.vendorId;
            })
        }
        return false;
    }

    static checkPrimaryPhoneExists(group: FormGroup): boolean {
        const resourcePhone = group.controls.resourcePhone.value;
        if (resourcePhone && resourcePhone.length) {
            return resourcePhone.some(resourse => {
                return resourse.phoneType && resourse.phoneType === 'Primary'
            });
        }
        return false;
    }

    static validateMarriageDate(group: FormGroup) {
        const marriageDate = group.controls.marriageDate.value;
        const maritalStatus = group.controls.maritalStatus.value;
        const status = group.controls.status.value;

        const isClosure = (status === '070' || status === '080');
        const locale = group.controls.locale.value;
        const futureDate = new Date(formatDate(new Date(), 'MM/dd/yyyy', locale));

        if (maritalStatus === '01' && (!marriageDate || marriageDate === '') && !isClosure) {
            group.controls.marriageDate.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_FAD_NO_MARRIAGE: {
                        actualValue: marriageDate, hideFieldName: true
                    }
                }));
        } else if (maritalStatus === '01' && (marriageDate || marriageDate !== '')
            && DfpsCommonValidators.validateDate(group.controls.marriageDate)) {
            group.controls.marriageDate.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    validateDate: { actualValue: marriageDate }
                }));
        } else if (maritalStatus === '01' && marriageDate && marriageDate !== ''
            && (new Date(marriageDate) > futureDate)) {
            group.controls.marriageDate.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    SSM_FAD_MARRIAGE_DATE: {
                        actualValue: marriageDate
                    }
                }));
        } else { group.controls.marriageDate.clearValidators(); }
        group.controls.marriageDate.updateValueAndValidity({ onlySelf: true });
    }

    static validateHomeInterestAgeFields(group: FormGroup): boolean {
        let validationPassed = true;
        const selectedMaleMinYearHomeInterest = group.controls.selectedMaleMinYearHomeInterest.value ?
            Number(group.controls.selectedMaleMinYearHomeInterest.value) : 0;
        const selectedMaleMinMonthHomeInterest = group.controls.selectedMaleMinMonthHomeInterest.value ?
            Number(group.controls.selectedMaleMinMonthHomeInterest.value) : 0;
        const maleMinAgeInMonthInts = (selectedMaleMinYearHomeInterest * 12) + selectedMaleMinMonthHomeInterest;
        const selectedMaleMaxYearHomeInterest = group.controls.selectedMaleMaxYearHomeInterest.value ?
            Number(group.controls.selectedMaleMaxYearHomeInterest.value) : 0;
        const selectedMaleMaxMonthHomeInterest = group.controls.selectedMaleMaxMonthHomeInterest.value ?
            Number(group.controls.selectedMaleMaxMonthHomeInterest.value) : 0;
        const maleMaxAgeInMonthInts = (selectedMaleMaxYearHomeInterest * 12) + selectedMaleMaxMonthHomeInterest;
        const selectedFemaleMinYearHomeInterest = group.controls.selectedFemaleMinYearHomeInterest.value ?
            Number(group.controls.selectedFemaleMinYearHomeInterest.value) : 0;
        const selectedFemaleMinMonthHomeInterest = group.controls.selectedFemaleMinMonthHomeInterest.value ?
            Number(group.controls.selectedFemaleMinMonthHomeInterest.value) : 0;
        const femaleMinAgeInMonthInts = (selectedFemaleMinYearHomeInterest * 12) + selectedFemaleMinMonthHomeInterest;
        const selectedFemaleMaxYearHomeInterest = group.controls.selectedFemaleMaxYearHomeInterest.value ?
            Number(group.controls.selectedFemaleMaxYearHomeInterest.value) : 0;
        const selectedFemaleMaxMonthHomeInterest = group.controls.selectedFemaleMaxMonthHomeInterest.value ?
            Number(group.controls.selectedFemaleMaxMonthHomeInterest.value) : 0;
        const femaleMaxAgeInMonthInts = (selectedFemaleMaxYearHomeInterest * 12) + selectedFemaleMaxMonthHomeInterest;

        const noInterests = (maleMaxAgeInMonthInts === 0 && maleMinAgeInMonthInts === 0 &&
            femaleMaxAgeInMonthInts === 0 && femaleMinAgeInMonthInts === 0);
        const isClosure = (status === '070' || status === '080');
        const nonPRSPCA = group.controls.nonPRSPCA.value;
        const nonPRSHome = group.controls.nonPRSHome.value;
        const buttonClicked = group.controls.buttonClicked.value;

        // check for ange range(home interest and license section)
        if (maleMaxAgeInMonthInts <= 0 && maleMinAgeInMonthInts > 0) {
            group.controls.selectedMaleMaxYearHomeInterest.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_MIN_MALE_RANGE_INTEREST:
                    {
                        customFieldName: 'Max Year - Male',
                    }
                }));
            validationPassed = false;
        } else {
            group.controls.selectedMaleMaxYearHomeInterest.clearValidators();
        }
        if (buttonClicked === 'submit' && !isClosure && !nonPRSHome && !nonPRSPCA && noInterests) {
            group.controls.selectedMaleMinYearHomeInterest.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_FAD_NO_INTERESTS:
                    {
                        customFieldName: 'Min Year - Male',
                    }
                }));
            validationPassed = false;
        } else if (maleMaxAgeInMonthInts > 0 && maleMinAgeInMonthInts <= 0) {
            group.controls.selectedMaleMinYearHomeInterest.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_MAX_MALE_RANGE_INTEREST:
                    {
                        customFieldName: 'Min Year - Male',
                    }
                }));
            validationPassed = false;
        } else if (maleMaxAgeInMonthInts !== 0 && maleMinAgeInMonthInts > maleMaxAgeInMonthInts) {
            group.controls.selectedMaleMinYearHomeInterest.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    SSM_FAD_MIN_LESS_MAX:
                    {
                        customFieldName: 'Min Year - Male',
                    }
                }));
            validationPassed = false;
        } else {
            group.controls.selectedMaleMinYearHomeInterest.clearValidators();
        }

        if (femaleMaxAgeInMonthInts <= 0 && femaleMinAgeInMonthInts > 0) {
            group.controls.selectedFemaleMaxYearHomeInterest.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_MIN_FEMALE_RANGE_INTEREST:
                    {
                        customFieldName: 'Max Year - Female',
                    }
                }));
            validationPassed = false;
        } else {
            group.controls.selectedFemaleMaxYearHomeInterest.clearValidators();
        }

        if (femaleMaxAgeInMonthInts > 0 && femaleMinAgeInMonthInts <= 0) {
            group.controls.selectedFemaleMinYearHomeInterest.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_MAX_FEMALE_RANGE_INTEREST:
                    {
                        customFieldName: 'Min Year - Female',
                    }
                }));
            validationPassed = false;
        } else if (femaleMaxAgeInMonthInts !== 0 && femaleMinAgeInMonthInts > femaleMaxAgeInMonthInts) {
            group.controls.selectedFemaleMinYearHomeInterest.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    SSM_FAD_MIN_LESS_MAX:
                    {
                        customFieldName: 'Min Year - Female',
                    }
                }));
            validationPassed = false;
        } else {
            group.controls.selectedFemaleMinYearHomeInterest.clearValidators();
        }

        group.controls.selectedMaleMaxYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMaxYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedMaleMinYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMinYearHomeInterest.updateValueAndValidity({ onlySelf: true });
        return validationPassed;
    }

    static validateHomeLicenseAgeFields(group: FormGroup): boolean {
        let validationPassed = true;
        const selectedMaleMinYear = group.controls.selectedMaleMinYear.value ?
            Number(group.controls.selectedMaleMinYear.value) : 0;
        const selectedMaleMinMonth = group.controls.selectedMaleMinMonth.value ?
            Number(group.controls.selectedMaleMinMonth.value) : 0;
        const maleMinAgeInMonths = (selectedMaleMinYear * 12) + selectedMaleMinMonth;
        const selectedMaleMaxYear = group.controls.selectedMaleMaxYear.value ?
            Number(group.controls.selectedMaleMaxYear.value) : 0;
        const selectedMaleMaxMonth = group.controls.selectedMaleMaxMonth.value ?
            Number(group.controls.selectedMaleMaxMonth.value) : 0;
        const maleMaxAgeInMonths = (selectedMaleMaxYear * 12) + selectedMaleMaxMonth;
        const selectedFemaleMinYear = group.controls.selectedFemaleMinYear.value ?
            Number(group.controls.selectedFemaleMinYear.value) : 0;
        const selectedFemaleMinMonth = group.controls.selectedFemaleMinMonth.value ?
            Number(group.controls.selectedFemaleMinMonth.value) : 0;
        const femaleMinAgeInMonths = (selectedFemaleMinYear * 12) + selectedFemaleMinMonth;
        const selectedFemaleMaxYear = group.controls.selectedFemaleMaxYear.value ?
            Number(group.controls.selectedFemaleMaxYear.value) : 0;
        const selectedFemaleMaxMonth = group.controls.selectedFemaleMaxMonth.value ?
            Number(group.controls.selectedFemaleMaxMonth.value) : 0;
        const femaleMaxAgeInMonths = (selectedFemaleMaxYear * 12) + selectedFemaleMaxMonth;
        const nonPRSHome = group.controls.nonPRSHome.value;
        const previousCapacity = group.controls.previousCapacity.value;
        const facilityCapacity = group.controls.facilityCapacity.value;
        const status = group.controls.status.value;

        if ((status === '040' || status === '050' || status === '060')
            && (!nonPRSHome || (Number(previousCapacity) !== Number(facilityCapacity)))
            && (maleMinAgeInMonths === 0 && maleMaxAgeInMonths === 0
                && femaleMinAgeInMonths === 0 && femaleMaxAgeInMonths === 0)) {
            group.controls.selectedMaleMaxYear.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_FAD_AGE_REQ: {
                        actualValue: selectedMaleMaxYear, customFieldName: 'Max Year - Male'
                    }
                }));
        } else { group.controls.selectedMaleMaxYear.clearValidators(); }

        if (!(status === '040' || status === '050' || status === '060' || status === '010')) {

            if (maleMinAgeInMonths !== 0 && maleMaxAgeInMonths === 0) {
                group.controls.selectedMaleMaxYear.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_MIN_MALE_RANGE:
                        {
                            customFieldName: 'Max Year - Male',
                        }
                    }));
                validationPassed = false;
            } else {
                group.controls.selectedMaleMaxYear.clearValidators();
            }

            if (maleMinAgeInMonths === 0 && maleMaxAgeInMonths !== 0) {
                group.controls.selectedMaleMinYear.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_MAX_MALE_RANGE:
                        {
                            customFieldName: 'Min Year - Male',
                        }
                    }));
                validationPassed = false;
            } else if (maleMaxAgeInMonths !== 0 && maleMinAgeInMonths > maleMaxAgeInMonths) {
                group.controls.selectedMaleMinYear.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        SSM_FAD_MIN_LESS_MAX:
                        {
                            customFieldName: 'Min Year - Male',
                        }
                    }));
                validationPassed = false;
            }
            else {
                group.controls.selectedMaleMinYear.clearValidators();
            }

            if (femaleMinAgeInMonths !== 0 && femaleMaxAgeInMonths === 0) {
                group.controls.selectedFemaleMaxYear.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_MIN_FEMALE_RANGE:
                        {
                            customFieldName: 'Max Year - Female',
                        }
                    }));
                validationPassed = false;
            } else {
                group.controls.selectedFemaleMaxYear.clearValidators();
            }

            if (femaleMinAgeInMonths === 0 && femaleMaxAgeInMonths !== 0) {
                group.controls.selectedFemaleMinYear.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_MAX_FEMALE_RANGE:
                        {
                            customFieldName: 'Min Year - Female',
                        }
                    }));
                validationPassed = false;
            } else if (femaleMaxAgeInMonths !== 0 && femaleMinAgeInMonths > femaleMaxAgeInMonths) {
                group.controls.selectedFemaleMinYear.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        SSM_FAD_MIN_LESS_MAX:
                        {
                            customFieldName: 'Min Year - Female',
                        }
                    }));
                validationPassed = false;
            } else {
                group.controls.selectedFemaleMinYear.clearValidators();
            }
        }

        group.controls.selectedMaleMaxYear.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMaxYear.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedMaleMinYear.updateValueAndValidity({ onlySelf: true });
        group.controls.selectedFemaleMinYear.updateValueAndValidity({ onlySelf: true });
        return validationPassed;
    }

    static supervisorApprovalValidations(group: FormGroup) {
        const buttonClicked = group.controls.buttonClicked.value;
        const approver = group.controls.approver.value;
        const status = group.controls.status.value;
        const previousStatus = group.controls.previousStatus.value;
        const nonPRSPCA = group.controls.nonPRSPCA.value;
        const nonPRSHome = group.controls.nonPRSHome.value;
        let isHomeLicenseValuesChanged = group.controls.isHomeLicenseValuesChanged.value;
        const previousCategory = group.controls.previousCategory.value;
        const category = group.controls.category.value;

        const approvalNeeded = (status === '070' && previousStatus === '010');
        const statusPrecedesPendingApproval = (status === '010' || status === '020' || status === '030');
        const activeToInactiveChange = (previousStatus === '040' && status === '050');
        isHomeLicenseValuesChanged = ((String(previousStatus) !== String(status))
            && !activeToInactiveChange && (String(previousCategory) !== String(category)));

        if (buttonClicked === 'save' && !approver && !approvalNeeded && (nonPRSHome || nonPRSPCA) && status === '060') {
            group.controls.errorMessageForSaveSubmit.setValidators(
                FadHomeInformationValidators.setErrorValidator({
                    MSG_FAD_SAVE_SUBMIT: {
                        actualValue: status, hideFieldName: true
                    }
                }));
        } else if (!approver && !approvalNeeded && (!nonPRSHome && !nonPRSPCA)) {
            if (isHomeLicenseValuesChanged && ['040', '050'].includes(status)) {
                group.controls.errorMessageForSaveSubmit.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_CHANGE_TO_PEND: {
                            hideFieldName: true
                        }
                    }));
            } else if (['060', '080'].includes(status)) {
                group.controls.errorMessageForSaveSubmit.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_SAVE_SUBMIT: {
                            actualValue: status, hideFieldName: true
                        }
                    }));

            } else if (!statusPrecedesPendingApproval && isHomeLicenseValuesChanged) {
                group.controls.errorMessageForSaveSubmit.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_SAVE_SUBMIT: {
                            actualValue: status, hideFieldName: true
                        }
                    }));
            } else if (isHomeLicenseValuesChanged && !statusPrecedesPendingApproval && status !== '050') {
                group.controls.errorMessageForSaveSubmit.setValidators(
                    FadHomeInformationValidators.setErrorValidator({
                        MSG_FAD_SAVE_SUBMIT: {
                            actualValue: status, hideFieldName: true
                        }
                    }));
            }
        } else {
            group.controls.errorMessageForSaveSubmit.clearValidators();
        }
        group.controls.errorMessageForSaveSubmit.updateValueAndValidity({ onlySelf: true });
        return null;
    }

}
