import { AbstractControl, FormArray, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators, DfpsFormValidationDirective } from 'dfps-web-lib';

export class QuickFindValidators extends DfpsFormValidationDirective {

    static phoneNumberExtPattern = (group: FormGroup): { [key: string]: any } => {
        const extension = group.controls.requestersPhoneExtension.value;
        const NUMBER_PATTERN = /^[0-9]+$/;
        if (extension?.toString()?.trim()?.length > 0 && !NUMBER_PATTERN.test(extension)) {
            group.controls.requestersPhoneExtension.setValidators(
                QuickFindValidators.setErrorValidator({
                    MSG_PHONE_EXT: { customFieldName: 'Extension', actualValue: extension },
                })
            );
        } else {
            group.controls.requestersPhoneExtension.clearValidators();
        }
        group.controls.requestersPhoneExtension.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static phoneNumberPattern = (group: FormGroup): { [key: string]: any } => {
        const phoneNumber = group.controls.requestersPhone.value;
        if (phoneNumber) {
            const PHONE_NUMBER_PATTERN_TEN_DIGIT =  /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/;
            if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim())) {
                group.controls.requestersPhone.setValidators(
                    QuickFindValidators.setErrorValidator({
                        MSG_PHONE_NUMBER: { customFieldName: 'Requester\'s Phone Number', actualValue: phoneNumber },
                    })
                );
            } else {
                group.controls.requestersPhone.clearValidators();
            }
        }
        group.controls.requestersPhone.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static validateQuickFindInfo = (group: FormGroup): { [key: string]: any } => {
        const buttonClicked = group.controls.buttonClicked.value;
        if (buttonClicked === 'complete') {
            QuickFindValidators.submitValidations(group);
        } else if (buttonClicked === 'save' || buttonClicked === 'saveAndStay') {
            QuickFindValidators.validateSubjects(group);
        }
        return null;
    };

    static submitValidations = (group: FormGroup): { [key: string]: any } => {
        const phone = group.controls.requestersPhone.value;
        const subjectsControl = group.get('subjects') as FormArray;
        let isError = false;

        if (phone === null || phone === undefined || phone.length === 0) {
            group.controls.requestersPhone.setValidators(
                QuickFindValidators.setErrorValidator({
                    required: { customFieldName: 'Requester\'s Phone Number' },
                })
            );
            isError = true;
        } else {
            group.controls.requestersPhone.clearValidators();
        }


        if(!group.controls.cps.value &&
            !group.controls.cpsNytd.value &&
            !group.controls.accounting.value &&
            !group.controls.legal.value &&
            !group.controls.aps.value &&
            !group.controls.emr.value ){
                group.controls.cps.addValidators(
                    QuickFindValidators.setErrorValidator({
                        required: { customFieldName: 'Select Program' },
                    })
                );
                isError = true;
        } else {
            group.controls.cps.clearValidators();
        }
        group.controls.cps.updateValueAndValidity({ onlySelf: true });


        QuickFindValidators.phoneNumberPattern(group);
        QuickFindValidators.phoneNumberExtPattern(group);

        if (subjectsControl.length > 0) {
            for (let index = 0; index < subjectsControl.length; index++) {
                const formGroup = subjectsControl.at(index) as FormGroup;
                const subNameControl = formGroup.get('fullName');

                if (
                    subNameControl.value === null ||
                    subNameControl.value === undefined ||
                    subNameControl.value === 0 ||
                    subNameControl.value.length === 0
                ) {
                    subNameControl.setValidators(
                        QuickFindValidators.setErrorValidator({
                            required: { customFieldName: 'Name' },
                        })
                    );
                    isError = true;
                } else {
                    subNameControl.clearValidators();
                }
                subNameControl.updateValueAndValidity({ onlySelf: true });
            }

            if (isError === false) {
                QuickFindValidators.validateSubjects(group);
            }
        }

        group.controls.requestersPhone.updateValueAndValidity({ onlySelf: true });

        return null;
    };

    static validateSubjects(group: FormGroup) {
        const subjects = group.get('subjects') as FormArray;
        const nameArray = [];
        let subNameControl;
        subjects.controls.forEach((element, index) => {
            const nameControl = element.get('fullName');
            if (index === 0) {
                subNameControl = nameControl;
            }
            if (nameControl.value !== '') nameArray.push(nameControl.value);
        });
        if (QuickFindValidators.hasDuplicates(nameArray)) {
            subNameControl.addValidators(
                QuickFindValidators.setErrorValidator({
                    MSG_DUPLICATE_SUBJECTS: { hideFieldName: true },
                })
            );
        } else {
            subNameControl.clearValidators();
        }

        subNameControl.updateValueAndValidity({ onlySelf: true });
        return null;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static hasDuplicates<T>(arr: T[]): boolean {
        return new Set(arr).size < arr.length;
    }
}
