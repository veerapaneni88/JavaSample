import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class AdminReviewValidators {

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static validateAdminReview = (group: FormGroup): { [key: string]: any } => {
        const program = group.controls.program.value;
        const type = group.controls.type.value;
        const status = group.controls.status.value;
        const authority = group.controls.authority.value;
        const requestedBy = group.controls.requestedBy.value;
        const requestedByName = group.controls.requestedByName.value;
        const otherAuthority = group.controls.otherAuthority.value;
        const result = group.controls.result.value;
        const delayedReason = group.controls.delayedReason.value;
        const otherDelayedReason = group.controls.otherDelayedReason.value;

        if (authority === "050" || authority === "060") {
            group.controls.authority.setValidators(AdminReviewValidators.setErrorValidator({
                    required: {actualValue: authority  }
                }));
        } else {
            group.controls.authority.clearValidators();
        }

        if (authority === "080" && (!otherAuthority || otherAuthority === '') ) {
            group.controls.otherAuthority.setValidators(AdminReviewValidators.setErrorValidator({
                    required: {actualValue: otherAuthority  }
                }));
        } else {
            group.controls.otherAuthority.clearValidators();
        }

        if (result === "090" && (delayedReason === "" || delayedReason === undefined)) {
            group.controls.delayedReason.setValidators(AdminReviewValidators.setErrorValidator({
                    required: {actualValue: delayedReason  }
                }));
        } else {
            group.controls.delayedReason.clearValidators();
        }

        if (result === "090" && delayedReason === "040" && otherDelayedReason === "") {
            group.controls.otherDelayedReason.setValidators(AdminReviewValidators.setErrorValidator({
                    required: {actualValue: otherDelayedReason  }
                }));
        } else {
            group.controls.otherDelayedReason.clearValidators();
        }

        if (program === 'AFC') {
            if (type && authority && status && requestedBy && requestedByName) {
                AdminReviewValidators.commonValidations(group);
            } else {
                group.controls.type.setValidators([Validators.required]);
                group.controls.authority.setValidators([Validators.required]);
                group.controls.status.setValidators([Validators.required]);
                group.controls.requestedBy.setValidators([Validators.required]);
                group.controls.requestedByName.setValidators([Validators.required]);
                group.controls.notificationDate.updateValueAndValidity({ onlySelf: true });
                group.controls.notificationDate.clearValidators();
                group.controls.appealRequestDate.updateValueAndValidity({ onlySelf: true });
                group.controls.appealRequestDate.clearValidators();
                group.controls.result.updateValueAndValidity({ onlySelf: true });
                group.controls.result.clearValidators();
                group.controls.reviewDate.updateValueAndValidity({ onlySelf: true });
                group.controls.reviewDate.clearValidators();
                group.controls.dueDate.updateValueAndValidity({ onlySelf: true });
                group.controls.dueDate.clearValidators();
            }
        } else {
            if (type && authority && status) {
                AdminReviewValidators.commonValidations(group);
            } else {
                group.controls.type.setValidators([Validators.required]);
                group.controls.authority.setValidators([Validators.required]);
                group.controls.status.setValidators([Validators.required]);
                group.controls.notificationDate.updateValueAndValidity({ onlySelf: true });
                group.controls.notificationDate.clearValidators();
                group.controls.appealRequestDate.updateValueAndValidity({ onlySelf: true });
                group.controls.appealRequestDate.clearValidators();
                group.controls.result.updateValueAndValidity({ onlySelf: true });
                group.controls.result.clearValidators();
                group.controls.reviewDate.updateValueAndValidity({ onlySelf: true });
                group.controls.reviewDate.clearValidators();
                group.controls.dueDate.updateValueAndValidity({ onlySelf: true });
                group.controls.dueDate.clearValidators();
            }
        }
        group.controls.otherAuthority.updateValueAndValidity({ onlySelf: true });
        group.controls.status.updateValueAndValidity({ onlySelf: true });
        group.controls.status.clearValidators();
        group.controls.authority.updateValueAndValidity({ onlySelf: true });
        group.controls.authority.clearValidators();
        group.controls.type.updateValueAndValidity({ onlySelf: true });
        group.controls.type.clearValidators();
        group.controls.requestedByName.updateValueAndValidity({ onlySelf: true });
        group.controls.requestedByName.clearValidators();
        group.controls.requestedBy.updateValueAndValidity({ onlySelf: true });
        group.controls.requestedBy.clearValidators();
        group.controls.delayedReason.updateValueAndValidity({ onlySelf: true });
        group.controls.delayedReason.clearValidators();
        group.controls.otherDelayedReason.updateValueAndValidity({ onlySelf: true });
        group.controls.otherDelayedReason.clearValidators();
        return null;
    }

    static commonValidations = (group: FormGroup): { [key: string]: any } => {
        const buttonClicked = group.controls.buttonClicked.value;
        const appealRequestDate = group.controls.appealRequestDate;
        const notificationDate = group.controls.notificationDate;
        const reviewDate = group.controls.reviewDate;
        const dueDate = group.controls.dueDate;
        const result = group.controls.result.value;
        const status = group.controls.status.value;
        const type = group.controls.type.value;
        const requestedBy = group.controls.requestedBy.value;
        const otherRequestedBy = group.controls.otherRequestedBy.value;

        if (requestedBy === "XXX" && !otherRequestedBy) {
            group.controls.otherRequestedBy.setValidators(
                AdminReviewValidators.setErrorValidator({
                    required: { hideFieldName: false }
                }));
        }

        if (notificationDate.value && (DfpsCommonValidators.validateDate(notificationDate))) {
            group.controls.notificationDate.setValidators(
                AdminReviewValidators.setErrorValidator({
                    validateDate: { actualValue: notificationDate.value, customFieldName: 'Reviewed Person Notifed On', }
                }));
        }

        if (appealRequestDate.value && (DfpsCommonValidators.validateDate(appealRequestDate))) {
            group.controls.appealRequestDate.setValidators(
                AdminReviewValidators.setErrorValidator({
                    validateDate: { actualValue: appealRequestDate.value, customFieldName: 'Request Received Date' }
                }));
        }

        if (reviewDate.value && (DfpsCommonValidators.validateDate(reviewDate))) {
            group.controls.reviewDate.setValidators(
                AdminReviewValidators.setErrorValidator({
                    validateDate: { actualValue: reviewDate.value }
                }));
        }

        if (dueDate.value && (DfpsCommonValidators.validateDate(dueDate))) {
            group.controls.dueDate.setValidators(
                AdminReviewValidators.setErrorValidator({
                    validateDate: { actualValue: dueDate.value, customFieldName: 'Conduct Review By' }
                }));
        }

        if ((status === '040' || status === '030')) {
            if (!result) {
                group.controls.result.setValidators(
                    AdminReviewValidators.setErrorValidator({
                        MSG_ADMIN_RVW_RESULT_REQ: { hideFieldName: false }
                    }));
            }
            if (type !== '040') {
                if (!notificationDate.value) {
                    group.controls.notificationDate.setValidators(
                        AdminReviewValidators.setErrorValidator({
                            MSG_PRSN_NTFD_REQ_NOT_RLSE: { customFieldName: 'Reviewed Person Notifed On', hideFieldName: false }
                        }));
                }
                if (!appealRequestDate.value) {
                    group.controls.appealRequestDate.setValidators(
                        AdminReviewValidators.setErrorValidator({
                            MSG_RQST_RCVD_DT_REQ: { customFieldName: 'Request Received Date', hideFieldName: false }
                        }));
                }

                if (!reviewDate.value) {
                    group.controls.reviewDate.setValidators(
                        AdminReviewValidators.setErrorValidator({
                            MSG_RVW_DT_REQ_NOT_RLSE: { hideFieldName: false }
                        }));
                }
            }
        }
        if (reviewDate.value && appealRequestDate.value) {
            if (new Date(reviewDate.value).getTime() < new Date(appealRequestDate.value).getTime()) {
                group.controls.reviewDate.setValidators(
                    AdminReviewValidators.setErrorValidator({
                        MSG_REVIEW_AFTER_REQUEST_DATE: { hideFieldName: false }
                    }));
            }
        }

        if (dueDate.value && !appealRequestDate.value) {
            group.controls.dueDate.setValidators(
                AdminReviewValidators.setErrorValidator({
                    MSG_RQST_RCVD_DT_REQ_CONDUCT: { customFieldName: 'Conduct Review By ', hideFieldName: false }
                }));
        }

        if (reviewDate.value && !appealRequestDate.value) {
            group.controls.reviewDate.setValidators(
                AdminReviewValidators.setErrorValidator({
                    MSG_RQST_RCVD_DT_REQ_RVW: { hideFieldName: false }
                }));
        }

        if (buttonClicked === 'save' && status === '040') {
            group.controls.status.setValidators(
                AdminReviewValidators.setErrorValidator({
                    MSG_AR_SAVE_CLOSE: { actualValue: status, hideFieldName: false }
                }));
        }

        if (buttonClicked === 'saveandclose' && status !== '040') {
            group.controls.status.setValidators(
                AdminReviewValidators.setErrorValidator({
                    MSG_APRV_REQ_BEFORE_SAVE_CLOSE: { hideFieldName: false }
                }));
        }

        group.controls.notificationDate.updateValueAndValidity({ onlySelf: true });
        group.controls.notificationDate.clearValidators();
        group.controls.status.updateValueAndValidity({ onlySelf: true });
        group.controls.status.clearValidators();
        group.controls.appealRequestDate.updateValueAndValidity({ onlySelf: true });
        group.controls.appealRequestDate.clearValidators();
        group.controls.result.updateValueAndValidity({ onlySelf: true });
        group.controls.result.clearValidators();
        group.controls.reviewDate.updateValueAndValidity({ onlySelf: true });
        group.controls.reviewDate.clearValidators();
        group.controls.dueDate.updateValueAndValidity({ onlySelf: true });
        group.controls.dueDate.clearValidators();
        group.controls.type.updateValueAndValidity({ onlySelf: true });
        group.controls.type.clearValidators();
        group.controls.otherAuthority.updateValueAndValidity({ onlySelf: true});
        group.controls.otherRequestedBy.updateValueAndValidity({ onlySelf: true});
        group.controls.otherRequestedBy.clearValidators();
        return null;
    }
}
