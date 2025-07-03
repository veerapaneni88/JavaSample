import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn, FormArray } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class ServicePackageDetailValidators {

    static servicePackageValidation = (group: FormGroup): { [key: string]: any } => {
        const servicePackageCaseType = group.controls.servicePackageCaseType.value;
        const servicePackageType = group.controls.servicePackageType.value;
        const servicePackage = group.controls.servicePackage.value;
        const overrideReasonRequired = group.controls.overrideReasonRequired.value;
        const overrideReason = group.controls.overrideReason.value;
        const overrideReasonComments = group.controls.overrideReasonComments.value;
        const startDate = group.controls.startDate.value;
        const endDate = group.controls.endDate.value;
        const endReason = group.controls.endReason.value;
        const endReasonComments = group.controls.endReasonComments.value;
        const endDateRequired = group.controls.endDateRequired.value;
        const endReasonRequired = group.controls.endReasonRequired.value;
        const cansCompletedIndicator = group.controls.cansCompletedIndicator.value;
        const cansNotCompletedReason = group.controls.cansNotCompletedReason.value;
        const cansNotCompletedComments = group.controls.cansNotCompletedComments.value;
        const endDateModfied = group.controls.endDateModfied.value;
        const cansAssessmentCompletedDate = group.controls.cansAssessmentCompletedDate.value;
	    const cReleaseDate = group.controls.cReleaseDate.value;
        const autopopulatedEndDate = group.controls.autopopulatedEndDate.value;
        const cansRecommendationComments = group.controls.cansRecommendationComments.value;
        const cansAssessor = group.controls.cansAssessor.value;
        const groAssessmentCompletedDate = group.controls.groAssessmentCompletedDate.value;
        const groRecommendedIndicator = group.controls.groRecommendedIndicator.value;
        const stageType = group.controls.stageType.value;
        const cansAssessorName = group.controls.cansAssessorName.value;

        let today = new Date();
        today.setHours(0,0,0,0);

        if (!servicePackageCaseType) {
          group.controls.servicePackageCaseType.setValidators(ServicePackageDetailValidators.setErrorValidator({
            required: { customFieldName: 'Is this child being served under DFPS or SSCC?' }
          }));
        } else { group.controls.servicePackageCaseType.clearValidators(); }

        if (!servicePackageType) {
            group.controls.servicePackageType.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: servicePackageType }
            }));
        } else if (servicePackage && !servicePackageType) {
            group.controls.servicePackageType.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: servicePackageType }
            }));
        } else { group.controls.servicePackageType.clearValidators(); }

        if (!servicePackage) {
            group.controls.servicePackage.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { customFieldName: 'Service Package', }
            }));
        } else { group.controls.servicePackage.clearValidators(); }

        if (overrideReasonRequired && !overrideReason) {
            group.controls.overrideReason.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: overrideReason }
            }));
        } else {
            group.controls.overrideReason.clearValidators();
        }

        if ((overrideReason === '80' || overrideReason === '90') && !overrideReasonComments) {
            group.controls.overrideReasonComments.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: overrideReasonComments }
            }));
        } else { group.controls.overrideReasonComments.clearValidators(); }

        if (servicePackageType && !startDate) {
            group.controls.startDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: startDate }
            }));
        } else if (startDate && DfpsCommonValidators.validateDate(group.controls.startDate)) {
            group.controls.startDate.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                    validateDate: { actualValue: startDate }
                }));
        } else if (startDate && new Date(startDate) < new Date(cReleaseDate)) {
            group.controls.startDate.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                    SP_START_BEFORE_RELEASE_DATE: { actualValue: startDate }
                }));
        } else if (startDate && servicePackageType === 'RMD' && new Date(startDate) > today) {
                      group.controls.startDate.setValidators(
                          ServicePackageDetailValidators.setErrorValidator({
                              SP_START_DATE_FUTURE_DATE: { actualValue: today.toLocaleDateString() }
                          }));
        } else if (startDate && servicePackageType === 'SEL' && new Date(startDate) > today) {
            group.controls.startDate.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                    SP_START_DATE_FUTURE_DATE: { actualValue: today.toLocaleDateString() }
                }));
        } else { group.controls.startDate.clearValidators(); }

        if (stageType !== 'C-PB' && !endDate) {
            group.controls.endDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: endDate }
            }));
        }
        else if (endDate && DfpsCommonValidators.validateDate(group.controls.endDate)) {
            group.controls.endDate.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                    validateDate: { actualValue: endDate }
                }));
        } else if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
            group.controls.endDate.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                  SP_END_AFTER_SAME_START: { actualValue: endDate }
                }));
        } else if (endDateModfied) {
            group.controls.endDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                 MSG_SPG_END_DATE: { actualValue: autopopulatedEndDate }
             }));
         } else { group.controls.endDate.clearValidators(); }

        if (endReasonRequired && !endReason) {
            group.controls.endReason.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: endDate }
            }));
        } else { group.controls.endReason.clearValidators(); }

        if(servicePackageType === 'RMD' && !cansCompletedIndicator){
            group.controls.cansCompletedIndicator.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: cansCompletedIndicator, customFieldName: 'Was CANS Assessment Completed to Inform Recommendation?' }
            }));
        } else { group.controls.cansCompletedIndicator.clearValidators(); }

        if(cansCompletedIndicator && cansCompletedIndicator === 'N' && !cansNotCompletedReason){
            group.controls.cansNotCompletedReason.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: cansNotCompletedReason, customFieldName: 'CANS Assessment Not Completed Reason' }
            }));
        } else { group.controls.cansNotCompletedReason.clearValidators(); }

        if(cansNotCompletedReason && cansNotCompletedReason === '40' && !cansNotCompletedComments){
            group.controls.cansNotCompletedComments.setValidators(ServicePackageDetailValidators.setErrorValidator({
                required: { actualValue: cansNotCompletedComments, customFieldName: 'Comments' }
            }));
        } else { group.controls.cansNotCompletedComments.clearValidators(); }

        if(cansAssessor && cansCompletedIndicator === 'Y' && (!cansRecommendationComments || !cansAssessmentCompletedDate || !cansAssessorName)){
            group.controls.cansAssessmentSection.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                    MSG_CANS_ASSESSOR_SECTION_INCOMPLETE:{hideFieldName: true}
            }));
        } else if(!cansAssessor && cansCompletedIndicator === 'Y' && (!cansRecommendationComments || !cansAssessmentCompletedDate || !!cansAssessorName)){
            group.controls.cansAssessmentSection.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                    MSG_NON_CANS_ASSESSOR_SECTION_INCOMPLETE:{hideFieldName: true}
            }));
        } else { group.controls.cansAssessmentSection.clearValidators(); }

        if (cansAssessmentCompletedDate && new Date(cansAssessmentCompletedDate) > today) {
            group.controls.cansAssessmentCompletedDate.setValidators(
                ServicePackageDetailValidators.setErrorValidator({
                    MSG_CANS_COMPLETED_DATE: { customFieldName: 'CANS Assessment Completed Date', actualValue: cansAssessmentCompletedDate }
                }));
        } else if (DfpsCommonValidators.validateDate(group.controls.cansAssessmentCompletedDate)) {
            group.controls.cansAssessmentCompletedDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                validateDate: { customFieldName: 'CANS Assessment Completed Date', actualValue: cansAssessmentCompletedDate }
            }))
        } else { group.controls.cansAssessmentCompletedDate.clearValidators(); }

      if(groRecommendedIndicator && !groAssessmentCompletedDate){
        group.controls.groAssessmentCompletedDate.setValidators(
          ServicePackageDetailValidators.setErrorValidator({
            MSG_GRO_ASSESSMENT_DATE_DOES_NOT_EXIST:{customFieldName: "Assessment Completion Date"}
          }));
      } else { group.controls.groAssessmentCompletedDate.clearValidators(); }

      if(!groRecommendedIndicator && groAssessmentCompletedDate){
        group.controls.groRecommendedIndicator.setValidators(
          ServicePackageDetailValidators.setErrorValidator({
            MSG_GRO_INDICATOR_DOES_NOT_EXIST:{customFieldName: "GRO II Recommended"}
          }));
      } else { group.controls.groRecommendedIndicator.clearValidators(); }

        ServicePackageDetailValidators.addonServicePackageValidations(group);

        group.controls.servicePackageCaseType.updateValueAndValidity({ onlySelf: true });
        group.controls.servicePackageType.updateValueAndValidity({ onlySelf: true });
        group.controls.overrideReason.updateValueAndValidity({ onlySelf: true });
        group.controls.overrideReasonComments.updateValueAndValidity({ onlySelf: true });
        group.controls.startDate.updateValueAndValidity({ onlySelf: true });
        group.controls.endDate.updateValueAndValidity({ onlySelf: true });
        group.controls.endReason.updateValueAndValidity({ onlySelf: true });
        group.controls.endReasonComments.updateValueAndValidity({ onlySelf: true });
        group.controls.servicePackage.updateValueAndValidity({ onlySelf: true });
        group.controls.cansNotCompletedReason.updateValueAndValidity({ onlySelf: true });
        group.controls.cansCompletedIndicator.updateValueAndValidity({ onlySelf: true });
        group.controls.cansNotCompletedComments.updateValueAndValidity({ onlySelf: true });
        group.controls.cansAssessmentCompletedDate.updateValueAndValidity({ onlySelf: true });
        group.controls.cansAssessmentSection.updateValueAndValidity({ onlySelf: true });
        group.controls.groRecommendedIndicator.updateValueAndValidity({ onlySelf: true });
        group.controls.groAssessmentCompletedDate.updateValueAndValidity({ onlySelf: true });

        return null;
    }

    static addonServicePackageValidations = (group: FormGroup): { [key: string]: any } => {
        const servicePackageType = group.controls.servicePackageType.value;
        const addonServicePackages = group.get('addonServicePackages') as FormArray;
        if (servicePackageType && servicePackageType === 'SEL') {
            addonServicePackages.value.forEach((element, index) => {
                if (addonServicePackages.at(index)) {
                    const formGroup = addonServicePackages.at(index) as FormGroup;
                    const startDate = formGroup.get('startDate');
                    const primaryStartDate = group.get('startDate').value;
                    const endDate = formGroup.get('endDate');
                    const primaryEndDate = group.get('endDate').value;
                    const endReason = formGroup.get('endReason');
                    const endReasonComments = formGroup.get('endReasonComments');
                    const servicePackageDecode = formGroup.get('servicePackageDecode').value;
                    const existingEndDate = formGroup.get('existingEndDate').value;
                    const endReasonRequired = formGroup.get('endReasonRequired').value;
                    const isPackageSelected = formGroup.get('isPackageSelected').value;

                    if (isPackageSelected) {
                        if (!startDate.value) {
                            startDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                                required: { customFieldName: servicePackageDecode + ' - Start Date' }
                            }))
                        } else if (DfpsCommonValidators.validateDate(startDate)) {
                            startDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                                validateDate: { customFieldName: servicePackageDecode + ' - Start Date', actualValue: startDate.value }
                            }))
                        } else if (startDate && primaryStartDate && new Date(startDate.value) < new Date(primaryStartDate)) {
                            startDate.setValidators(
                                ServicePackageDetailValidators.setErrorValidator({
                                    MSG_ADDON_START_DATE: { customFieldName: servicePackageDecode + ' - Start Date', actualValue: startDate.value }
                                }));
                        } else {
                            startDate.clearValidators();
                        }

                        if (!endDate.value) {
                            endDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                                required: { customFieldName: servicePackageDecode + ' - End Date' }
                            }))
                        } else if (existingEndDate && !endDate.value) {
                            endDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                                required: { customFieldName: servicePackageDecode + ' - End Date' }
                            }))
                        } else if (endDate.value && DfpsCommonValidators.validateDate(endDate)) {
                            endDate.setValidators(ServicePackageDetailValidators.setErrorValidator({
                                validateDate: { customFieldName: servicePackageDecode + ' - End Date', actualValue: endDate.value }
                            }));
                        } else if (startDate.value && endDate.value && new Date(startDate.value) > new Date(endDate.value)) {
                            endDate.setValidators(
                                ServicePackageDetailValidators.setErrorValidator({
                                    SP_END_AFTER_SAME_START: { customFieldName: servicePackageDecode + ' - End Date', actualValue: endDate.value }
                                }));
                        } else if (endDate.value && primaryEndDate && new Date(endDate.value) > new Date(primaryEndDate)) {
                            endDate.setValidators(
                                ServicePackageDetailValidators.setErrorValidator({
                                    MSG_ADDON_END_DATE: { customFieldName: servicePackageDecode + ' - End Date', actualValue: endDate.value }
                                }));
                        } else {
                            endDate.clearValidators();
                        }

                        if (endReasonRequired && !endReason.value) {
                            endReason.setValidators(
                                ServicePackageDetailValidators.setErrorValidator({
                                    required: { customFieldName: servicePackageDecode + ' - End Reason', actualValue: endReason.value }
                                })
                            )
                        } else {
                            endReason.clearValidators();
                        }

                        if (endReason.value && endReason.value === '40' && !endReasonComments.value) {
                            endReasonComments.setValidators(
                                ServicePackageDetailValidators.setErrorValidator({
                                    required: { customFieldName: servicePackageDecode + ' - End Reason Comments', actualValue: endReasonComments.value }
                                })
                            )
                        } else {
                            endReasonComments.clearValidators();
                        }
                    } else {
                        startDate.clearValidators();
                        endDate.clearValidators();
                        endReason.clearValidators();
                        endReasonComments.clearValidators();
                    }
                    startDate.updateValueAndValidity({ onlySelf: true });
                    endDate.updateValueAndValidity({ onlySelf: true });
                    endReason.updateValueAndValidity({ onlySelf: true });
                    endReasonComments.updateValueAndValidity({ onlySelf: true });
                }
            });
        }
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }
}
