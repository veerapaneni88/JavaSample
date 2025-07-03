import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class KinHomeAssessmentDetailValidators {

    static checkboxValidation = (group: FormGroup): { [key: string]: any } => {

        const caregiverName = group.controls.caregiverName.value;
        const homeAssmtSubmittedDate = group.controls.homeAssmtSubmittedDate.value;
        const criminalHistory = group.controls.criminalHistory.value;
        const chKinSafety = group.controls.chKinSafety.value;
        const chAddendum = group.controls.chAddendum.value;

        const approvalCd = group.controls.approvalCd.value;
        const dtOfApproval = group.controls.dtOfApproval.value;
        const dtOfDenial = group.controls.dtOfDenial.value;

        const abuseNeglectHistory = group.controls.abuseNeglectHistory.value;
        const abnKinSafety = group.controls.abnKinSafety.value;
        const abnAddendum = group.controls.abnAddendum.value;

        const otherReason = group.controls.otherReason.value;
        const denialReasonComments = group.controls.denialReasonComments.value;
        const indAutoPopulate= group.controls.indAutoPopulate.value;

        if (!caregiverName) {
            group.controls.caregiverName.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    required: { actualValue: caregiverName, customFieldName: 'Name of Kinship Caregiver' }
                }));
        } else { group.controls.caregiverName.clearValidators(); }

        if (homeAssmtSubmittedDate == null && indAutoPopulate =='N')  {
            group.controls.homeAssmtSubmittedDate.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    required: { actualValue: homeAssmtSubmittedDate, customFieldName: 'Date Home Assessment Submitted' }
                }));
        } else if (homeAssmtSubmittedDate && new Date(homeAssmtSubmittedDate) > new Date()) {
            group.controls.homeAssmtSubmittedDate.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    FUTURE_DATE: { actualValue: homeAssmtSubmittedDate }
                }));
        } else if (homeAssmtSubmittedDate && DfpsCommonValidators.validateDate(group.controls.homeAssmtSubmittedDate)) {
            group.controls.homeAssmtSubmittedDate.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    validateDate: { actualValue: homeAssmtSubmittedDate, customFieldName: 'Date Home Assessment Submitted' }
                }));
        } else { group.controls.homeAssmtSubmittedDate.clearValidators(); }

        if (!approvalCd) {
            group.controls.approvalCd.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    required: { actualValue: approvalCd, customFieldName: 'Approved' }
                }));
        }
        else { group.controls.approvalCd.clearValidators(); }

        if ((approvalCd === 'Y' || approvalCd === 'APAD' || approvalCd === 'APKS')  && !dtOfApproval) {
            group.controls.dtOfApproval.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    required: {
                        actualValue: dtOfApproval, customFieldName: 'Date of Approval'
                    }
                }));
        } else if (dtOfApproval && !DfpsCommonValidators.validateDate(group.controls.dtOfApproval) && new Date(dtOfApproval) > new Date()) {
            group.controls.dtOfApproval.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    FUTURE_DATE: { actualValue: dtOfApproval, customFieldName: 'Date of Approval' }
                }));
        } else if (dtOfApproval && DfpsCommonValidators.validateDate(group.controls.dtOfApproval)) {
            group.controls.dtOfApproval.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    validateDate: { actualValue: dtOfApproval, customFieldName: 'Date of Approval' }
                }));
        } else {
            group.controls.dtOfApproval.clearValidators();
        }

        if (approvalCd === 'N' && !dtOfDenial) {
            group.controls.dtOfDenial.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    required: {
                        actualValue: dtOfDenial, customFieldName: 'Date of Denial'
                    }
                }));
        } else if (dtOfDenial && !DfpsCommonValidators.validateDate(group.controls.dtOfDenial) && new Date(dtOfDenial) > new Date()) {
            group.controls.dtOfDenial.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    FUTURE_DATE: { actualValue: dtOfDenial, customFieldName: 'Date of Denial' }
                }));
        } else if (dtOfDenial && DfpsCommonValidators.validateDate(group.controls.dtOfDenial)) {
            group.controls.dtOfDenial.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    validateDate: { actualValue: dtOfDenial, customFieldName: 'Date of Denial' }
                }));
        } else {
            group.controls.dtOfDenial.clearValidators();
        }

        if (approvalCd === 'N' && !criminalHistory && !abuseNeglectHistory && !otherReason) {
            group.controls.criminalHistory.setValidators(
                KinHomeAssessmentDetailValidators.setErrorValidator({
                    required: {
                        actualValue: criminalHistory, customFieldName: 'Reason for Denial'
                    }
                }));
        } else {
            group.controls.criminalHistory.clearValidators();
        }

        if (criminalHistory) {
            group.controls.criminalHistory.clearValidators();
            if (!chKinSafety) {
                group.controls.chKinSafety.setValidators(
                    KinHomeAssessmentDetailValidators.setErrorValidator({
                        required: {
                            actualValue: chKinSafety, customFieldName: 'Is a Kinship Safety Evaluation required to address Criminal History?'
                        }
                    }));
            } else { group.controls.chKinSafety.clearValidators(); }

            if (!chAddendum) {
                group.controls.chAddendum.setValidators(
                    KinHomeAssessmentDetailValidators.setErrorValidator({
                        required: {
                            actualValue: chAddendum, customFieldName: 'Is an Addendum required to address Criminal History?'
                        }
                    }));
            } else { group.controls.chAddendum.clearValidators(); }
        } else {
            group.controls.chKinSafety.clearValidators();
            group.controls.chAddendum.clearValidators();
        }

        if (abuseNeglectHistory) {
            group.controls.criminalHistory.clearValidators();
            if (!abnKinSafety) {
                group.controls.abnKinSafety.setValidators(
                    KinHomeAssessmentDetailValidators.setErrorValidator({
                        required: {
                            actualValue: abnKinSafety, customFieldName: 'Is a Kinship Safety Evaluation required to address DFPS Abuse/Neglect History?'
                        }
                    }));
            } else { group.controls.abnKinSafety.clearValidators(); }

            if (!abnAddendum) {
                group.controls.abnAddendum.setValidators(
                    KinHomeAssessmentDetailValidators.setErrorValidator({
                        required: {
                            actualValue: abnAddendum, customFieldName: 'Is an Addendum required to address DFPS Abuse/Neglect History?'
                        }
                    }));
            } else { group.controls.abnAddendum.clearValidators(); }
        } else {
            group.controls.abnKinSafety.clearValidators();
            group.controls.abnAddendum.clearValidators();
        }

        if (otherReason) {
            group.controls.criminalHistory.clearValidators();
            if (!denialReasonComments) {
                group.controls.denialReasonComments.setValidators(
                    KinHomeAssessmentDetailValidators.setErrorValidator({
                        required: {
                            actualValue: denialReasonComments, customFieldName: 'Reason for Denial Comments'
                        }
                    }));
            } else { group.controls.denialReasonComments.clearValidators(); }

        } else {
            group.controls.denialReasonComments.clearValidators();
        }


        group.controls.caregiverName.updateValueAndValidity({ onlySelf: true });
        group.controls.homeAssmtSubmittedDate.updateValueAndValidity({ onlySelf: true });
        group.controls.approvalCd.updateValueAndValidity({ onlySelf: true });
        group.controls.chKinSafety.updateValueAndValidity({ onlySelf: true });
        group.controls.chAddendum.updateValueAndValidity({ onlySelf: true });
        group.controls.abnKinSafety.updateValueAndValidity({ onlySelf: true });
        group.controls.abnAddendum.updateValueAndValidity({ onlySelf: true });
        group.controls.dtOfApproval.updateValueAndValidity({ onlySelf: true });
        group.controls.dtOfDenial.updateValueAndValidity({ onlySelf: true });
        group.controls.denialReasonComments.updateValueAndValidity({ onlySelf: true });
        group.controls.criminalHistory.updateValueAndValidity({ onlySelf: true });


        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}
