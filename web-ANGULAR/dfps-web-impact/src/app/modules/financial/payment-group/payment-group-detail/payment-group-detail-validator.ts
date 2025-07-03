import { AbstractControl, FormGroup } from '@angular/forms';
export class PaymentGrpDetailValidators {

    static oneTimePaymentCatValidation() {
        return (group: FormGroup): { [key: string]: any } => {
            const categoryCode = group.controls.categoryCode;
            const payeeName = group.controls.payeeName;
            const paymentMode = group.controls.paymentMode;
            const paymentGroupStatus = group.controls.paymentGroupStatus;
            const checkNumber = group.controls.checkNumber;
            if ((categoryCode.value === 'oneTimePayment' && (payeeName.value === '' || payeeName.value === null)) ||
                (paymentGroupStatus.value === 'Pending' && (payeeName.value === '' || payeeName.value === null))) {
                payeeName.setErrors({ MSG_FIN_PAYEE_NM_REQD: { MSG_FIN_PAYEE_NM_REQD: true } });
            } else if (paymentMode.value === 'check' && paymentGroupStatus.value === 'Processed') {
                if (checkNumber.value === '') {
                    checkNumber.setErrors({ required: { required: true } })
                }
            } else {
                payeeName.setErrors({});
                checkNumber.setErrors({});
            }
            return null;
        };
    }

    static validateAgencyAccountOrPersonId(control: AbstractControl) {
        const NUMBER_PATTERN = /^[.\d]+$/;
        if (control.value && !NUMBER_PATTERN.test(control.value)) {
            return {
                paymentGroupSearchIdPattern: {
                    customFieldName: 'Agency Account ID/Person ID',
                    actualValue: control.value,
                    paymentGroupSearchIdPattern: true
                }
            };
        }
        return null;
    }

    static paymentGrpCategoryValidation() {
        return (group: FormGroup): { [key: string]: any } => {
            const categoryCode = group.controls.categoryCode;
            const contractPersonId = group.controls.contractPersonId;
            const isValidated = group.controls.isValidated;
            if (((categoryCode.value === 'person')
                || (categoryCode.value === 'contract'))
                && group.controls.isValidated.value === 'false'
                && ((contractPersonId.value !== '')
                    || (contractPersonId.value !== null))) {
                isValidated.setErrors({ MSG_FIN_VALD_PYMT_GRP: { MSG_FIN_VALD_PYMT_GRP: true, hideFieldName: true } })
            } else {
                isValidated.setErrors({});
            }
            return null;
        };
    }
}