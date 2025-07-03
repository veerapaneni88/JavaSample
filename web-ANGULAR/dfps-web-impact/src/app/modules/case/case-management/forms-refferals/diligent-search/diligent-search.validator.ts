import { AbstractControl, FormArray, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsFormValidationDirective } from 'dfps-web-lib';

export class DiligentSearchValidator extends DfpsFormValidationDirective {
    static sections: string[] = ['children', 'mothers', 'fathers'];
    static sectionsToFullNameFields = new Map<string, string>();

    static phoneNumberExtPattern = (extControl: any, group: FormGroup, fieldName : string): { [key: string]: any } => {
        const extension = extControl.value;
        const NUMBER_PATTERN = /^[0-9]+$/;
        if (extension?.toString()?.trim()?.length > 0 && !NUMBER_PATTERN.test(extension)) {
            extControl.setValidators(
                DiligentSearchValidator.setErrorValidator({
                    MSG_PHONE_EXT: { customFieldName: fieldName, actualValue: extension },
                })
            );
        } else {
            extControl.clearValidators();
        }
        extControl.updateValueAndValidity({ onlySelf: true });
        return null;
    };

    static phoneNumberPattern = (phoneControl: any, group: FormGroup, fieldName : string): { [key: string]: any } => {
        const phoneNumber = phoneControl.value;
        if (phoneNumber) {
            const PHONE_NUMBER_PATTERN_TEN_DIGIT = /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/;
            if (!PHONE_NUMBER_PATTERN_TEN_DIGIT.test(phoneNumber.toString().trim())) {
                phoneControl.setValidators(
                    DiligentSearchValidator.setErrorValidator({
                        MSG_PHONE_NUMBER: { customFieldName: fieldName , actualValue: phoneNumber },
                    })
                );
            } else {
                phoneControl.clearValidators();
            }
            phoneControl.updateValueAndValidity({ onlySelf: true });
        }

        return null;
    };

    static validateDiligentSearchInfo() {
        return (group: FormGroup): { [key: string]: any } => {
        const buttonClicked = group.controls.buttonClicked.value;
        DiligentSearchValidator.sectionsToFullNameFields.set('children', 'fullName');
        DiligentSearchValidator.sectionsToFullNameFields.set('mothers', 'motherFullName');
        DiligentSearchValidator.sectionsToFullNameFields.set('fathers', 'fatherFullName');

        if (buttonClicked === 'complete') {
            DiligentSearchValidator.submitValidations(group);
        } else if (buttonClicked === 'save' || buttonClicked === 'saveAndStay') {
            DiligentSearchValidator.validateSubjects(group);
        }
        return null;
    };
}

    static validateSubjects = (group: FormGroup): { [key: string]: any } => {
        DiligentSearchValidator.sections.forEach((section) => {
            const nameArray = [];
            let subNameControl: AbstractControl;
            const subjectsControl = group.get(section) as FormArray;
            subjectsControl.controls.forEach((element, index) => {
                const formGroup = subjectsControl.at(index) as FormGroup;
                const nameControl = formGroup.get(DiligentSearchValidator.sectionsToFullNameFields.get(section));
                if (index === 0) {
                    subNameControl = nameControl;
                }
                if (nameControl.value !== '' && nameControl.value !== 0) nameArray.push(nameControl.value);
            });

            if (DiligentSearchValidator.hasDuplicates(nameArray)) {
                subNameControl.setValidators(
                    DiligentSearchValidator.setErrorValidator({
                        MSG_DUPLICATE_NAME: { hideFieldName: true },
                    })
                );
            } else {
                subNameControl.clearValidators();
            }
            subNameControl.updateValueAndValidity({ onlySelf: true });
        });
        return null;
    };

    static submitValidations = (group: FormGroup): { [key: string]: any } => {
        let isError = false;
        DiligentSearchValidator.validatePhoneNumber(group.controls.requestersPhone, group, 'Requester\'s Phone Number', isError);
        DiligentSearchValidator.phoneNumberExtPattern(group.controls.requestersPhoneExtension, group, 'Extension');
        DiligentSearchValidator.validatePhoneNumber(group.controls.caseWorkerPhone, group, 'Caseworker\'s Phone Number' ,isError);
        DiligentSearchValidator.phoneNumberExtPattern(group.controls.caseWorkerExtension, group,'Caseworker\'s Extension');
        DiligentSearchValidator.validatePhoneNumber(group.controls.supervisorPhone, group, 'Supervisor\'s Phone Number', isError);
        DiligentSearchValidator.phoneNumberExtPattern(group.controls.supervisorExtension, group, 'Supervisor\'s Extension');

        const caseworker = group.controls.caseworker.value;
        if (caseworker === '' || caseworker === undefined) {

            group.controls.caseworker.setValidators(
                DiligentSearchValidator.setErrorValidator({
                    required: { customFieldName: 'Is the requester also the caseworker?' },
                })
            );
            isError = true;
        } else {
            group.controls.caseworker.clearValidators();
        }
        group.controls.caseworker.updateValueAndValidity({ onlySelf: true });

        const purposeLocate = group.controls.locateRelatives.value;
        const purposeCWOP = group.controls.childWithoutPlacement.value;

        if ((purposeCWOP === '' || !purposeCWOP) && (purposeLocate === '' || !purposeLocate)) {
            group.controls.locateRelatives.setValidators(
                DiligentSearchValidator.setErrorValidator({
                    required: { customFieldName: 'Purpose of Request' },
                })
            );
            isError = true;
        } else {
            group.controls.locateRelatives.clearValidators();
        }
        group.controls.locateRelatives.updateValueAndValidity({ onlySelf: true });

        const supervisorName  = group.controls.supervisorName.value;
        if (!DiligentSearchValidator.isValidString(supervisorName)) {
            group.controls.supervisorName.setValidators(
                DiligentSearchValidator.setErrorValidator({
                    required: { customFieldName: 'Supervisor\'s Name' },
                })
            );
            isError = true;
        } else {
            group.controls.supervisorName.clearValidators();
        }
        group.controls.supervisorName.updateValueAndValidity({ onlySelf: true });

        const caseworkerName  = group.controls.caseWorkerName.value;
        if (!caseworkerName) {
            group.controls.caseWorkerName.setValidators(
                DiligentSearchValidator.setErrorValidator({
                    required: { customFieldName: 'Caseworker\'s Name' },
                })
            );
            isError = true;
        } else {
            group.controls.caseWorkerName.clearValidators();
        }
        group.controls.caseWorkerName.updateValueAndValidity({ onlySelf: true });

        const requestersName  = group.controls.requestersName.value;
        if (!DiligentSearchValidator.isValidString(requestersName

        )) {
            group.controls.requestersName.setValidators(
                DiligentSearchValidator.setErrorValidator({
                    required: { customFieldName: 'Requester\'s Name' },
                })
            );
            isError = true;
        } else {
            group.controls.requestersName.clearValidators();
        }
        group.controls.requestersName.updateValueAndValidity({ onlySelf: true });

        // Validate if the Name dropdown is not selected in any section.
        DiligentSearchValidator.sections.forEach((section) => {
            let locUnkownId : any;
            let locKnownId : any;
            let infoUnknownId : any;
            let locOptionsId : any;
            if (section === 'mothers') {
                locUnkownId = 'motherLocUnkown';
                locKnownId = 'motherLocKnown';
                infoUnknownId = 'motherInfoUnknown';
                locOptionsId = 'motherLocOption';

            } else if (section === 'fathers') {
                locUnkownId = 'fatherLocUnkown';
                locKnownId = 'fatherLocKnown';
                infoUnknownId = 'fatherInfoUnknown';
                locOptionsId = 'fatherLocOption';
            }

            let sectionName = '';
            const subjectsControl = group.get(section) as FormArray;
            subjectsControl.controls.forEach((element, index) => {
                const formGroup = subjectsControl.at(index) as FormGroup;
                if(index===0){
                    const subNameControl = formGroup.get(DiligentSearchValidator.sectionsToFullNameFields.get(section));
                    const locOptions = formGroup.get(locOptionsId);
                    
                    if (
                        subNameControl.value === null ||
                        subNameControl.value === '' ||
                        subNameControl.value === undefined ||
                        subNameControl.value === undefined ||
                        subNameControl.value === 0 ||
                        subNameControl.value.length === 0
                    ) {
                        if(section === 'children' || ((section==='mothers' || section==='fathers') && 
                            (locOptions.value === 'L' || locOptions.value === 'F'))){
                            if(section === 'children')
                                sectionName = 'Child';
                            else if(section === 'mothers')
                                sectionName = 'Mother';
                            else if(section === 'fathers')
                                sectionName = 'Father';
                            subNameControl.setValidators(
                                DiligentSearchValidator.setErrorValidator({
                                    required: { customFieldName: sectionName +' Name' },
                                })
                            );
                            isError = true;
                        }
                    } else {
                        subNameControl.clearValidators();
                    }
                    subNameControl.updateValueAndValidity({ onlySelf: true });

                } else { // index > 0

                    if (section !== 'children') {
                        const subNameControl = formGroup.get(DiligentSearchValidator.sectionsToFullNameFields.get(section));
                        const locOptions = formGroup.get(locOptionsId);
                      
                        if(section === 'children')
                            sectionName = 'Child';
                        else if(section === 'mothers')
                            sectionName = 'Mother';
                        else if(section === 'fathers')
                            sectionName = 'Father';
                        
                        if ((locOptions.value === 'L' || locOptions.value === 'F')  &&
                            (subNameControl.value === '' || subNameControl.value === null || subNameControl.value === 0) )  {
                                subNameControl.setValidators(
                                    DiligentSearchValidator.setErrorValidator({
                                        required: { customFieldName: sectionName+ ' Name' },
                                    })
                                );
                                isError = true;
                        } else if (((section === 'fathers'  && formGroup.get('fatherComments').value !=='')
                            || (section === 'mothers'  && formGroup.get('motherComments').value !=='') ) 
                            && (locOptions.value === 'L' || locOptions.value === 'F')
                            && (subNameControl.value === '' || subNameControl.value === null || subNameControl.value === 0) )  {
                                subNameControl.setValidators(
                                    DiligentSearchValidator.setErrorValidator({
                                        required: { customFieldName: sectionName + ' Name' },
                                    })
                                );
                                isError = true;
                        }  else {
                            subNameControl.clearValidators();
                        }
                        if (section === 'fathers'){
                            const fatherOfChildren = formGroup.get('fatherOfChildren') as FormArray
                            if(fatherOfChildren!=null && fatherOfChildren.controls.length>0){
                                if((fatherOfChildren.controls[0].get('childPersonId').value !==''
                                    && (locOptions.value === 'L' || locOptions.value === 'F'))
                                    && (subNameControl.value === '' || subNameControl.value === null || subNameControl.value === 0) )  {
                                    subNameControl.setValidators(
                                        DiligentSearchValidator.setErrorValidator({
                                            required: { customFieldName: sectionName + ' Name' },
                                        })
                                    );
                                    isError = true;
                                }
                            }else {
                                subNameControl.clearValidators();
                            }
                        }

                        subNameControl.updateValueAndValidity({ onlySelf: true });


                    }

                }
            });
        });

        if (isError === false) {
            DiligentSearchValidator.validateSubjects(group);
        }
        group.controls.requestersPhone.updateValueAndValidity({ onlySelf: true });
        group.controls.requestersPhoneExtension.updateValueAndValidity({ onlySelf: true });
        group.controls.caseWorkerPhone.updateValueAndValidity({ onlySelf: true });
        group.controls.caseWorkerExtension.updateValueAndValidity({ onlySelf: true });
        group.controls.supervisorPhone.updateValueAndValidity({ onlySelf: true });
        group.controls.supervisorExtension.updateValueAndValidity({ onlySelf: true });
        return null;
    };

    private static validatePhoneNumber(phoneControl: any, group: FormGroup, fieldName : string, isError: boolean) {
        const phone = phoneControl.value;
        if (!phone) {
                phoneControl.setValidators(
                DiligentSearchValidator.setErrorValidator({
                    required: { customFieldName: fieldName},
                })
            );
            isError = true;
        } else {
            phoneControl.clearValidators();
        }
        DiligentSearchValidator.phoneNumberPattern(phoneControl, group, fieldName);
        return isError;
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

    static hasDuplicates<T>(arr: T[]): boolean {
        return new Set(arr).size < arr.length;
    }

    static isValidString(value: any): boolean {
        return typeof value === 'string' && value.trim() !== '';
    }
}


