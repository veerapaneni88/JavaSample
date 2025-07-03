import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class KinHomeInformationValidators {

    static validateHomeInformationKin = (group: FormGroup): { [key: string]: any } => {
        const buttonClicked = group.controls.buttonClicked.value;
        if (buttonClicked === 'save') {
            KinHomeInformationValidators.saveValidations(group);
            KinHomeInformationValidators.commonValidations(group);
        } else if (buttonClicked === 'assign') {
            // KinHomeInformationValidators.assignValidations(group);
        } else if (buttonClicked === 'submit') {
            KinHomeInformationValidators.submitValidations(group);
            KinHomeInformationValidators.commonValidations(group);
        }
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static saveValidations = (group: FormGroup): { [key: string]: any } => {
        const status = group.controls.status.value;
        const closureReason = group.controls.closureReason.value;
        const recommendReopening = group.controls.recommendReopening.value;
        if (status === '091' && (closureReason || recommendReopening)) {
            group.controls.status.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_APRV_REQ_FOR_SAVE_CLS: { actualValue: status, hideFieldName: true }
                }));
        } else {
            group.controls.status.clearValidators();
        }
        if (status === '060' || status === '080' || status === '061') {
            group.controls.supervisorApprovalMsg.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_FAD_SAVE_SUBMIT: { hideFieldName: true }
                }));
        } else {
            group.controls.supervisorApprovalMsg.clearValidators();
        }

        group.controls.status.updateValueAndValidity({ onlySelf: true });
        group.controls.supervisorApprovalMsg.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static submitValidations = (group: FormGroup): { [key: string]: any } => {
        const status = group.controls.status.value;
        const signedAgreement = group.controls.signedAgreement.value;
        const maritalStatus = group.controls.maritalStatus.value;
        const closureReason = group.controls.closureReason.value;
        const recommendReopening = group.controls.recommendReopening.value;
        const saveAndSubmitStatusMsg = group.controls.saveAndSubmitStatusMsg.value;
        const incomeQualified = group.controls.incomeQualified.value;
        const manualGiven = group.controls.manualGiven.value;
        const isKinAssessment = group.controls.kinAssessment.value;
        const kinCaregiverEligStatusCode = group.controls.kinCaregiverEligibilityStatus.value;
        const isPaymentCourtOrdered = group.controls.isPaymentCourtOrdered.value;
        const agreementSignedDate = group.controls.agreementSignedDate.value;
        const hasBegunTraining = group.controls.hasBegunTraining.value;
        const vendorId = group.controls.vendorId.value;
        const isHouseholdMeetFPL = group.controls.isHouseholdMeetFPL.value;
        const showNewPage = ((status === '070' || status === '080') && !isKinAssessment) ? false : true;

        if (status !== '080' && (closureReason || recommendReopening)) {
            group.controls.pendingClosureSupervisorApprovalMsg.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_APRV_REQ_FOR_SAVE_CLS: { actualValue: closureReason, hideFieldName: true }
                }));
        } else {
            group.controls.pendingClosureSupervisorApprovalMsg.clearValidators();
        }
        group.controls.pendingClosureSupervisorApprovalMsg.updateValueAndValidity({ onlySelf: true });

        if (status !== '060' && status !== '080' && status !== '061') {
            group.controls.saveAndSubmitStatusMsg.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_KIN_PENDING_STATUS_ONLY: { actualValue: saveAndSubmitStatusMsg, hideFieldName: true }
                }));
        } else {
            group.controls.saveAndSubmitStatusMsg.clearValidators();
        }
        group.controls.saveAndSubmitStatusMsg.updateValueAndValidity({ onlySelf: true });

        if (!group.controls.saveAndSubmitStatusMsg.invalid) {
            if (status === '080') {
                if (!closureReason) {
                    group.controls.closureReason.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_PENDCLS__NO_CLO_REASON: { actualValue: closureReason }
                        }));
                } else {
                    group.controls.closureReason.clearValidators();
                }
            }
            else {
                if (closureReason && status !== '080' && status !== '070') {
                    group.controls.closureReason.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_NO_CLOSURE: { actualValue: closureReason }
                        }));
                } else {
                    group.controls.closureReason.clearValidators();
                }

                if (!maritalStatus) {
                    group.controls.maritalStatus.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_FAD_NO_MAR_STATUS: { actualValue: maritalStatus }
                        }));
                } else {
                    group.controls.maritalStatus.clearValidators();
                }

                if (signedAgreement === null || signedAgreement === undefined) {
                    group.controls.signedAgreement.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_CMN_NO_KIN_SIGN: { actualValue: signedAgreement }
                        }));
                } else {
                    group.controls.signedAgreement.clearValidators();
                }

                if (incomeQualified === null || incomeQualified === undefined && !showNewPage) {
                    group.controls.incomeQualified.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_CMN_NO_KIN_INCOME_QUAL: { actualValue: incomeQualified }
                        }));
                } else {
                    group.controls.incomeQualified.clearValidators();
                }

                if (manualGiven === null || manualGiven === undefined) {
                    group.controls.manualGiven.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_CMN_NO_KIN_MANUAL: { actualValue: manualGiven }
                        }));
                } else {
                    group.controls.manualGiven.clearValidators();
                }

                if (agreementSignedDate && showNewPage && 
                    (new Date(agreementSignedDate)).getTime() < (new Date('09/01/2017')).getTime()) {
                    group.controls.agreementSignedDate.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_KINSHIP_DATE: { actualValue: agreementSignedDate, 
                                customFieldName: 'Caregiver Agreement Signed Date' }
                        }));
                } else if ((signedAgreement && agreementSignedDate 
                    && (new Date(agreementSignedDate)).getTime() > (new Date()).getTime()) 
                    && status !== '080') {
                    group.controls.agreementSignedDate.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_SVC_NO_FUTURE_DATE: { actualValue: agreementSignedDate }
                        }));
                } else {
                    group.controls.agreementSignedDate.clearValidators();
                }

                if (!vendorId && showNewPage && (isHouseholdMeetFPL || isPaymentCourtOrdered)) {
                    group.controls.vendorIdErrorMsg.setValidators(
                        KinHomeInformationValidators.setErrorValidator({
                            MSG_VENDRID_REQ: { hideFieldName: true }
                        }));
                } else {
                    group.controls.vendorIdErrorMsg.clearValidators();
                }

                if ((kinCaregiverEligStatusCode && showNewPage) && status !== '080') {
                    if (hasBegunTraining == null || hasBegunTraining === undefined) {
                        group.controls.hasBegunTraining.setValidators(
                            KinHomeInformationValidators.setErrorValidator({
                                MSG_TRING_REQ: { actualValue: hasBegunTraining }
                            }));
                    } else {
                        group.controls.hasBegunTraining.clearValidators();
                    }

                    if ((isPaymentCourtOrdered == null || isPaymentCourtOrdered === undefined) && status !== '080') {
                        group.controls.isPaymentCourtOrdered.setValidators(
                            KinHomeInformationValidators.setErrorValidator({
                                MSG_CRTPAY_REQ: { actualValue: isPaymentCourtOrdered }
                            }));
                    } else {
                        group.controls.isPaymentCourtOrdered.clearValidators();
                    }
                }
            }
        } else {
            group.controls.closureReason.clearValidators();
            group.controls.maritalStatus.clearValidators();
            group.controls.signedAgreement.clearValidators();
            group.controls.incomeQualified.clearValidators();
            group.controls.manualGiven.clearValidators();
            group.controls.agreementSignedDate.clearValidators();
            group.controls.hasBegunTraining.clearValidators();
            group.controls.isPaymentCourtOrdered.clearValidators();
            group.controls.vendorIdErrorMsg.clearValidators();
        }
        group.controls.pendingClosureSupervisorApprovalMsg.updateValueAndValidity({ onlySelf: true });
        group.controls.saveAndSubmitStatusMsg.updateValueAndValidity({ onlySelf: true });
        group.controls.closureReason.updateValueAndValidity({ onlySelf: true });
        group.controls.maritalStatus.updateValueAndValidity({ onlySelf: true });
        group.controls.signedAgreement.updateValueAndValidity({ onlySelf: true });
        group.controls.incomeQualified.updateValueAndValidity({ onlySelf: true });
        group.controls.manualGiven.updateValueAndValidity({ onlySelf: true });
        group.controls.agreementSignedDate.updateValueAndValidity({ onlySelf: true });
        group.controls.hasBegunTraining.updateValueAndValidity({ onlySelf: true });
        group.controls.isPaymentCourtOrdered.updateValueAndValidity({ onlySelf: true });
        group.controls.vendorIdErrorMsg.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static commonValidations = (group: FormGroup): { [key: string]: any } => {
        const status = group.controls.status.value;
        const personCount = group.controls.personCount.value;
        const isKinAssessment = group.controls.kinAssessment.value;
        const isRelativeCareGiver = group.controls.relativeCaregiver.value;
        const isCaregiverMaternal = group.controls.isCaregiverMaternal.value;
        const isCaregiverPaternal = group.controls.isCaregiverPaternal.value;
        const isPlacementCourtOrdered = group.controls.isPlacementCourtOrdered.value;
        const showNewPage = ((status === '070' || status === '080') && !isKinAssessment) ? false : true;
        const annualIncome = group.controls.annualIncome.value;
        const buttonClicked = group.controls.buttonClicked.value;
        const NUMBER_PATTERN = /^([0]?[0-9]{1,2})$/;
        const INCOME_PATTERN = /^[+-]?[0-9]*\.[0-9]{2}$/;

        if (annualIncome) {
            group.controls.annualIncome.setValidators([
                DfpsCommonValidators.validateCurrency(11)
            ]);
        } else { group.controls.annualIncome.clearValidators(); }

        if (personCount === '') {
            group.controls.personCount.setValidators(KinHomeInformationValidators.setErrorValidator({
                required: { customFieldName: '# Persons in Household' }
            }));
        } else if ((!personCount || Number(personCount) === 0) && showNewPage
            && !group.controls.saveAndSubmitStatusMsg.invalid) {
            group.controls.personCount.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_CMN_NO_NUM_KIN_PERSON: { actualValue: personCount, customFieldName: '# Persons in Household' }
                }));
        } else if (!NUMBER_PATTERN.test(personCount)) {
            group.controls.personCount.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    personsInHousehold: { actualValue: personCount, customFieldName: '# Persons in Household' }
                }));
        } else {
            group.controls.personCount.clearValidators();
        }

        if (!group.controls.saveAndSubmitStatusMsg.invalid) {
            KinHomeInformationValidators.validateCourtOrderedPlacementDate(group, showNewPage);
            KinHomeInformationValidators.validateCourtOrderedPaymentDate(group, showNewPage);
            KinHomeInformationValidators.validateAgreementSignedDate(group, showNewPage);
            if ('080' !== group.controls.status.value &&
                (isPlacementCourtOrdered == null || isPlacementCourtOrdered === undefined && showNewPage)) {
                group.controls.isPlacementCourtOrdered.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_CRT_PLCMNT_REQ: { actualValue: isPlacementCourtOrdered, hideFieldName: true }
                    }));
            } else {
                group.controls.isPlacementCourtOrdered.clearValidators();
            }
            if (isRelativeCareGiver && showNewPage && (!isCaregiverMaternal && !isCaregiverPaternal)) {
                group.controls.relativeCareGiverMsg.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_PAT_MAT_REQ: { hideFieldName: true }
                    }));
            } else {
                group.controls.relativeCareGiverMsg.clearValidators();
            }

        } else {
            group.controls.isPlacementCourtOrdered.clearValidators();
            group.controls.relativeCareGiverMsg.clearValidators();
        }
        if ((buttonClicked === 'save' && showNewPage) || (!group.controls.saveAndSubmitStatusMsg.invalid)) {
            if (annualIncome === '') {
                group.controls.annualIncome.setValidators([Validators.required]);
            } else if ((!annualIncome || annualIncome === '0.00') && showNewPage) {
                group.controls.annualIncome.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_CMN_NO_ANNUAL_INCOME: { actualValue: annualIncome }
                    }));
            } else if (!INCOME_PATTERN.test(annualIncome)) {
                group.controls.annualIncome.setValidators(
                    KinHomeInformationValidators.setErrorValidator({ decimalNumber: { actualValue: annualIncome } }));
            } else {
                group.controls.annualIncome.clearValidators();
            }
        } else {
            group.controls.annualIncome.clearValidators();
        }
        group.controls.isPlacementCourtOrdered.updateValueAndValidity({ onlySelf: true });
        group.controls.relativeCareGiverMsg.updateValueAndValidity({ onlySelf: true });
        group.controls.annualIncome.updateValueAndValidity({ onlySelf: true });
        group.controls.personCount.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static validateCourtOrderedPlacementDate = (group: FormGroup, showNewPage: boolean): { [key: string]: any } => {
        const courtOrderedPlacementDate = group.controls.courtOrderedPlacementDate.value;
        const isPlacementCourtOrdered = group.controls.isPlacementCourtOrdered.value;
        if (isPlacementCourtOrdered && showNewPage && !courtOrderedPlacementDate) {
            group.controls.courtOrderedPlacementDate.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_PLCMNT_DATE_REQ: { actualValue: courtOrderedPlacementDate }
                }));
        } else if (isPlacementCourtOrdered && showNewPage && courtOrderedPlacementDate &&
            KinHomeInformationValidators.isFutureDate(courtOrderedPlacementDate)) {
            group.controls.courtOrderedPlacementDate.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_PLCMNT_DATE_REQ: { actualValue: courtOrderedPlacementDate }
                }));
        } else if (isPlacementCourtOrdered && showNewPage && courtOrderedPlacementDate &&
            new Date(courtOrderedPlacementDate) > new Date()) {
            group.controls.courtOrderedPlacementDate.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    MSG_SVC_NO_FUTURE_DATE: { actualValue: courtOrderedPlacementDate }
                }));
        } else if (isPlacementCourtOrdered && (courtOrderedPlacementDate || courtOrderedPlacementDate !== '')
            && DfpsCommonValidators.validateDate(group.controls.courtOrderedPlacementDate)) {
            group.controls.courtOrderedPlacementDate.setValidators(
                KinHomeInformationValidators.setErrorValidator({
                    validateDate: { actualValue: courtOrderedPlacementDate }
                }));
        } else {
            group.controls.courtOrderedPlacementDate.clearValidators();
        }
        group.controls.courtOrderedPlacementDate.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static validateCourtOrderedPaymentDate = (group: FormGroup, showNewPage: boolean): { [key: string]: any } => {
        const kinCaregiverEligStatusCode = group.controls.kinCaregiverEligibilityStatus.value;
        const courtOrderedPaymentDate = group.controls.courtOrderedPaymentDate.value;
        const isPaymentCourtOrdered = group.controls.isPaymentCourtOrdered.value;
        if (kinCaregiverEligStatusCode && showNewPage) {
            if (isPaymentCourtOrdered && !courtOrderedPaymentDate) {
                group.controls.courtOrderedPaymentDate.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_PYDATE_REQ: { actualValue: courtOrderedPaymentDate }
                    }));
            } else if (isPaymentCourtOrdered && courtOrderedPaymentDate &&
                KinHomeInformationValidators.isFutureDate(courtOrderedPaymentDate)) {
                group.controls.courtOrderedPaymentDate.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_PYDATE_REQ: { actualValue: courtOrderedPaymentDate }
                    })); 
            } else if (isPaymentCourtOrdered && (courtOrderedPaymentDate || courtOrderedPaymentDate !== '')
                && DfpsCommonValidators.validateDate(group.controls.courtOrderedPaymentDate)) {
                group.controls.courtOrderedPaymentDate.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        validateDate: { actualValue: courtOrderedPaymentDate }
                    }));
            } else {
                group.controls.courtOrderedPaymentDate.clearValidators();
            }
        }
        group.controls.courtOrderedPaymentDate.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static validateAgreementSignedDate = (group: FormGroup, showNewPage: boolean): { [key: string]: any } => {
        const kinCaregiverEligStatusCode = group.controls.kinCaregiverEligibilityStatus.value;
        const signedAgreement = group.controls.signedAgreement.value;
        const agreementSignedDate = group.controls.agreementSignedDate.value;
        if (kinCaregiverEligStatusCode && showNewPage) {
            if (signedAgreement && !agreementSignedDate) {
                group.controls.agreementSignedDate.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_DATAGRMNT_REG: { actualValue: agreementSignedDate, 
                            customFieldName: 'Caregiver Agreement Signed Date' }
                    }));
            } else if (signedAgreement && agreementSignedDate && 
                KinHomeInformationValidators.isFutureDate(agreementSignedDate)) {
                group.controls.agreementSignedDate.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_DATAGRMNT_REG: { actualValue: agreementSignedDate, 
                            customFieldName: 'Caregiver Agreement Signed Date' }
                    }));
            } else if (signedAgreement && agreementSignedDate 
                    && (new Date(agreementSignedDate)).getTime() > (new Date()).getTime()) {
                group.controls.agreementSignedDate.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        MSG_SVC_NO_FUTURE_DATE: { actualValue: agreementSignedDate, 
                            customFieldName: 'Caregiver Agreement Signed Date' }
                    }));
            } else if (signedAgreement && (agreementSignedDate || agreementSignedDate !== '')
                && DfpsCommonValidators.validateDate(group.controls.agreementSignedDate)) {
                group.controls.agreementSignedDate.setValidators(
                    KinHomeInformationValidators.setErrorValidator({
                        validateDate: { actualValue: agreementSignedDate, 
                            customFieldName: 'Caregiver Agreement Signed Date' }
                    }));
            } else {
                if (group.controls.agreementSignedDate.valid) {
                    group.controls.agreementSignedDate.clearValidators();
                }
            }
            group.controls.agreementSignedDate.updateValueAndValidity({ onlySelf: true });
            return null;
        }
    }

    static isFutureDate(dateValue) {
        const futureDate = new Date('12/31/3400');
        const inputDate = new Date(dateValue);
        if (inputDate > futureDate) {
            return true;
        } else {
            return false;
        }
    }
}
