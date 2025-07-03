import { FormGroup, AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DfpsCommonValidators } from 'dfps-web-lib';

export class NytdSurveyValidators {

    static validateNytdSurvey() {
        return (group: FormGroup): { [key: string]: any } => {
            
            let countQsWAns = 0;
            const dtYouthResponded = group.controls.dtYouthResponded.value;
            const surveyStartDate = group.controls.surveyStartDate.value;
            const surveyEndDate = group.controls.surveyEndDate.value;
            if (dtYouthResponded == null) {
                group.controls.dtYouthResponded.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        required: { customFieldName: 'Date Youth Responded' }
                    }));
            } else if (DfpsCommonValidators.validateDate(group.controls.dtYouthResponded)) {
                group.controls.dtYouthResponded.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        NYTD_INVALID_DATE: { 
                            customFieldName: 'Date Youth Responded', 
                            actualValue: dtYouthResponded}
                    }));
            } else if ((new Date(dtYouthResponded)) > (new Date(surveyEndDate))) {
                group.controls.dtYouthResponded.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        YOUTH_RESPONDED_DATE_AFTER_DUE_DATE: { 
                            customFieldName: 'Date Youth Responded', 
                            actualValue: dtYouthResponded}
                    }));
            } else if ((new Date(dtYouthResponded)) < (new Date(surveyStartDate))) {
                group.controls.dtYouthResponded.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        YOUTH_RESPONDED_DATE_OUT_OF_ELIGIBLE_DATES: { 
                            customFieldName: 'Date Youth Responded', 
                            actualValue: dtYouthResponded}
                    }));
            } else { group.controls.dtYouthResponded.clearValidators(); }
            group.controls.dtYouthResponded.updateValueAndValidity({ onlySelf: true });

            const cdMethodReceived = group.controls.cdMethodReceived.value;
            if (cdMethodReceived === null || cdMethodReceived === '' || cdMethodReceived == undefined){
                group.controls.cdMethodReceived.setValidators(NytdSurveyValidators.setErrorValidator({
                    selectFieldRequired: {customFieldName: 'Method' }
                }));
            } else {
                group.controls.cdMethodReceived.clearValidators();
            }
            group.controls.cdMethodReceived.updateValueAndValidity({ onlySelf: true });

            let directMethods = ['50','60','80'];
            let nbrInterviewMinutesInvFormat = false;
            const method = group.controls.cdMethodReceived.value;
            if (directMethods.includes(method)) {
                const nbrInterviewMinutes = group.controls.nbrInterviewMinutes.value;
                if (nbrInterviewMinutes === null || nbrInterviewMinutes === '' || nbrInterviewMinutes == undefined) {
                    group.controls.nbrInterviewMinutes.setValidators(NytdSurveyValidators.setErrorValidator({
                        required: {customFieldName: 'Interview Length (minutes)' }
                    }));
                } else if (!/^\d+$/.test(nbrInterviewMinutes)) {
                    group.controls.nbrInterviewMinutes.setValidators(NytdSurveyValidators.setErrorValidator({
                        INTERVIEW_LENGTH_NOT_VALID: {
                            customFieldName: 'Interview Length (minutes)',
                            actualValue: nbrInterviewMinutes
                        }
                    }));
                    nbrInterviewMinutesInvFormat = true;
                } else {
                    group.controls.nbrInterviewMinutes.clearValidators();
                }
            }
            group.controls.nbrInterviewMinutes.updateValueAndValidity({ onlySelf: true });
        
            const cdFullEmploy = group.controls.cdFullEmploy.value;
            if (cdFullEmploy == null) {
                group.controls.cdFullEmploy.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { customFieldName: 'Question 1. Currently are you employed full-time?' }
                    }));
            } else { group.controls.cdFullEmploy.clearValidators(); countQsWAns++;}
            group.controls.cdFullEmploy.updateValueAndValidity({ onlySelf: true });

    
            const cdPartEmploy = group.controls.cdPartEmploy.value;
            if (cdPartEmploy == null) {
                group.controls.cdPartEmploy.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { customFieldName: 'Question 2. Currently are you employed part-time?' }
                    }));
            } else { group.controls.cdPartEmploy.clearValidators(); countQsWAns++;}
            group.controls.cdPartEmploy.updateValueAndValidity({ onlySelf: true });

            const cdSkillsEmploy = group.controls.cdSkillsEmploy.value;
            if (cdSkillsEmploy == null) {
                group.controls.cdSkillsEmploy.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 3. In the past year, did you complete an apprenticeship, internship, or ' +
                            'other on-the-job training, either paid or unpaid?' 
                        }
                    }));
            } else { group.controls.cdSkillsEmploy.clearValidators();  countQsWAns++;}
            group.controls.cdSkillsEmploy.updateValueAndValidity({ onlySelf: true });

            const cdSocialSecurity = group.controls.cdSocialSecurity.value;
            if (cdSocialSecurity == null) {
                group.controls.cdSocialSecurity.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 4. Currently are you receiving social security payments ' +
                            '(Supplemental Security Income (SSI), Social ' +
                            'Security Disability Insurance (SSDI), or dependent\'s payments)?' 
                        }
                    }));
            } else { group.controls.cdSocialSecurity.clearValidators();  countQsWAns++;}
            group.controls.cdSocialSecurity.updateValueAndValidity({ onlySelf: true });

            const cdEducAid = group.controls.cdEducAid.value;
            if (cdEducAid == null) {
                group.controls.cdEducAid.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 5. Currently are you using a ' +
                            'scholarship, grant, stipend, student loan, voucher, or other type of ' + 
                            'educational financial aid to cover any educational expenses?' 
                        }
                    }));
            } else { group.controls.cdEducAid.clearValidators();  countQsWAns++;}
            group.controls.cdEducAid.updateValueAndValidity({ onlySelf: true });

            const isBaselineSurvey = group.controls.isBaselineSurvey.value;

            const cdAssistFin = group.controls.cdAssistFin.value;
            if (isBaselineSurvey === 'false' && cdAssistFin == null) {
                group.controls.cdAssistFin.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 5A. Currently are you receiving ongoing welfare ' + 
                                            'payments from the government to support your ' +
                                            'basic needs?' 
                        }
                    }));
            } else { group.controls.cdAssistFin.clearValidators(); }
            group.controls.cdAssistFin.updateValueAndValidity({ onlySelf: true });

            const cdAssistFood = group.controls.cdAssistFood.value;
            if (isBaselineSurvey === 'false' && cdAssistFood == null) {
                group.controls.cdAssistFood.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 5B. Currently are you receiving public food assistance?' 
                        }
                    }));
            } else { group.controls.cdAssistFood.clearValidators(); }
            group.controls.cdAssistFood.updateValueAndValidity({ onlySelf: true });

            const cdAssistHousing = group.controls.cdAssistHousing.value;
            if (isBaselineSurvey === 'false' && cdAssistHousing == null) {
                group.controls.cdAssistHousing.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 5C. Currently are you receiving any sort of housing ' + 
                                    'assistance from the government, such as living in public housing ' + 
                                    'or receiving a housing voucher?' 
                        }
                    }));
            } else { group.controls.cdAssistHousing.clearValidators(); }
            group.controls.cdAssistHousing.updateValueAndValidity({ onlySelf: true });

            const cdOtherFinAid = group.controls.cdOtherFinAid.value;
            if (cdOtherFinAid == null) {
                group.controls.cdOtherFinAid.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 6. Currently are you receiving ' + 
                            'any periodic and/or significant financial resources or support from ' + 
                            'another source not previously indicated and excluding paid employment?' 
                        }
                    }));
            } else { group.controls.cdOtherFinAid.clearValidators();  countQsWAns++;}
            group.controls.cdOtherFinAid.updateValueAndValidity({ onlySelf: true });

            const cdEducCert = group.controls.cdEducCert.value;
            if (cdEducCert == null) {
                group.controls.cdEducCert.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 7. What is the highest educational degree or certification that you have received?' 
                        }
                    }));
            } else { group.controls.cdEducCert.clearValidators();  countQsWAns++;}
            group.controls.cdEducCert.updateValueAndValidity({ onlySelf: true });

            const cdEducEnroll = group.controls.cdEducEnroll.value;
            if (cdEducEnroll == null) {
                group.controls.cdEducEnroll.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 8. Currently are you enrolled in ' + 
                            'and attending high school, GED classes, post-high school ' +
                            'vocational training, or college?' 
                        }
                    }));
            } else { group.controls.cdEducEnroll.clearValidators();  countQsWAns++;}
            group.controls.cdEducEnroll.updateValueAndValidity({ onlySelf: true });

            const cdConnAdult = group.controls.cdConnAdult.value;
            if (cdConnAdult == null) {
                group.controls.cdConnAdult.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 9. Currently is there at least one adult in your life, ' + 
                            'other than your caseworker, to whom you can go ' + 
                            'for advice or emotional support?' }
                    }));
            } else { group.controls.cdConnAdult.clearValidators();  countQsWAns++;}
            group.controls.cdConnAdult.updateValueAndValidity({ onlySelf: true });

            let q10Label = 'Question 10. Have you ever been homeless?';
            if(isBaselineSurvey == 'false') {
                q10Label = 'Question 10. In the past two years, were you homeless at any time?';
            }
            const cdHomeless = group.controls.cdHomeless.value;
            if (cdHomeless == null) {
                group.controls.cdHomeless.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: q10Label 
                        }
                    }));
            } else { group.controls.cdHomeless.clearValidators();  countQsWAns++;}
            group.controls.cdHomeless.updateValueAndValidity({ onlySelf: true });

            let q11Label = 'Question 11. Have you ever referred yourself or has someone else referred you ' +
                           'for an alcohol or drug abuse assessment or counseling?';
            if(isBaselineSurvey == 'false') {
                q11Label = 'Question 11. In the past two years, did you ' + 
                           'refer yourself or had someone else referred you for an alcohol or ' + 
                           'drug abuse assessment or counseling?';
            }
            const cdSubstanceAbuse = group.controls.cdSubstanceAbuse.value;
            if (cdSubstanceAbuse == null) {
                group.controls.cdSubstanceAbuse.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: q11Label 
                        }
                    }));
            } else { group.controls.cdSubstanceAbuse.clearValidators();  countQsWAns++;}
            group.controls.cdSubstanceAbuse.updateValueAndValidity({ onlySelf: true });

            let q12Label = 'Question 12. Have you ever been confined in a jail, prison, or correctional facility, ' +
                            'or juvenile or community detention facility, in connection with ' +
                            'allegedly committing a crime?';
            if(isBaselineSurvey == 'false') {
                q12Label = 'Question 12. In the past two years, were you confined in a jail, ' + 
                           'prison, correctional facility, or juvenile or community detention facility, ' + 
                           'in connection with allegedly committing a crime?';
            }
            const cdIncarceration = group.controls.cdIncarceration.value;
            if (cdIncarceration == null) {
                group.controls.cdIncarceration.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: q12Label 
                        }
                    }));
            } else { group.controls.cdIncarceration.clearValidators();  countQsWAns++;}
            group.controls.cdIncarceration.updateValueAndValidity({ onlySelf: true });

            let q13Label = 'Question 13. Have you ever given birth or fathered any children that were born?';
            if(isBaselineSurvey == 'false') {
                q13Label = 'Question 13. In the past two years, did you give birth ' + 
                           'to or father any children that were born?';
            }
            const cdChildren = group.controls.cdChildren.value;
            if (cdChildren == null) {
                group.controls.cdChildren.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: q13Label 
                        }
                    }));
            } else { group.controls.cdChildren.clearValidators();  countQsWAns++;}
            group.controls.cdChildren.updateValueAndValidity({ onlySelf: true });

            const cdBirthMarriage = group.controls.cdBirthMarriage.value;
            if (cdChildren != null && cdChildren === '10' && cdBirthMarriage == null) {
                group.controls.cdBirthMarriage.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 13A. If you responded yes to the previous question, ' + 
                            'were you married to the child\'s other parent at the time each child was born?' 
                        }
                    }));
            } else { group.controls.cdBirthMarriage.clearValidators(); }
            group.controls.cdBirthMarriage.updateValueAndValidity({ onlySelf: true });

            const cdMedicaid = group.controls.cdMedicaid.value;
            if (cdMedicaid == null) {
                group.controls.cdMedicaid.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 14. Currently are you on Medicaid?' 
                        }
                    }));
            } else { group.controls.cdMedicaid.clearValidators();  countQsWAns++;}
            group.controls.cdMedicaid.updateValueAndValidity({ onlySelf: true });

            const cdOtherHealthIns = group.controls.cdOtherHealthIns.value;
            if (cdOtherHealthIns == null) {
                group.controls.cdOtherHealthIns.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 15. Currently do you have health insurance, other than Medicaid?' 
                        }
                    }));
            } else { group.controls.cdOtherHealthIns.clearValidators();  countQsWAns++;}
            group.controls.cdOtherHealthIns.updateValueAndValidity({ onlySelf: true });

            const cdInsMedical = group.controls.cdInsMedical.value;
            if (cdOtherHealthIns != null && cdOtherHealthIns === '10' && cdInsMedical == null) {
                group.controls.cdInsMedical.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 15A. Does your health insurance include coverage for medical services?' 
                        }
                    }));
            } else { group.controls.cdInsMedical.clearValidators(); }
            group.controls.cdInsMedical.updateValueAndValidity({ onlySelf: true });

            const cdInsMental = group.controls.cdInsMental.value;
            if (cdOtherHealthIns != null && cdOtherHealthIns === '10' && cdInsMental == null) {
                group.controls.cdInsMental.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 15B. Does your health insurance include coverage for mental health services?' 
                        }
                    }));
            } else { group.controls.cdInsMental.clearValidators(); }
            group.controls.cdInsMental.updateValueAndValidity({ onlySelf: true });

            const cdInsPrescription = group.controls.cdInsPrescription.value;
            if (cdOtherHealthIns != null && cdOtherHealthIns === '10' && cdInsPrescription == null) {
                group.controls.cdInsPrescription.setValidators(
                    NytdSurveyValidators.setErrorValidator({
                        selectFieldRequired: { 
                            customFieldName: 'Question 15C. Does your health insurance include coverage for prescription drugs?' 
                        }
                    }));
            } else { group.controls.cdInsPrescription.clearValidators(); }
            group.controls.cdInsPrescription.updateValueAndValidity({ onlySelf: true });

            const clickedButton = group.controls.clickedButton.value;

            if (clickedButton === 'Save and return') {
                group.controls.cdMethodReceived.clearValidators();
                group.controls.cdMethodReceived.updateValueAndValidity({ onlySelf: true });
                if(!nbrInterviewMinutesInvFormat) {
                    group.controls.nbrInterviewMinutes.clearValidators();
                    group.controls.nbrInterviewMinutes.updateValueAndValidity({ onlySelf: true });
                }
                group.controls.cdFullEmploy.clearValidators();
                group.controls.cdFullEmploy.updateValueAndValidity({ onlySelf: true });
                group.controls.cdPartEmploy.clearValidators();
                group.controls.cdPartEmploy.updateValueAndValidity({ onlySelf: true });
                group.controls.cdSkillsEmploy.clearValidators();	
                group.controls.cdSkillsEmploy.updateValueAndValidity({ onlySelf: true });
                group.controls.cdSocialSecurity.clearValidators();	
                group.controls.cdSocialSecurity.updateValueAndValidity({ onlySelf: true });
                group.controls.cdEducAid.clearValidators();	
                group.controls.cdEducAid.updateValueAndValidity({ onlySelf: true });
                group.controls.cdAssistFin.clearValidators();	
                group.controls.cdAssistFin.updateValueAndValidity({ onlySelf: true });
                group.controls.cdAssistFood.clearValidators();	
                group.controls.cdAssistFood.updateValueAndValidity({ onlySelf: true });
                group.controls.cdAssistHousing.clearValidators();	
                group.controls.cdAssistHousing.updateValueAndValidity({ onlySelf: true });
                group.controls.cdOtherFinAid.clearValidators();	
                group.controls.cdOtherFinAid.updateValueAndValidity({ onlySelf: true });
                group.controls.cdEducCert.clearValidators();	
                group.controls.cdEducCert.updateValueAndValidity({ onlySelf: true });
                group.controls.cdEducEnroll.clearValidators();	
                group.controls.cdEducEnroll.updateValueAndValidity({ onlySelf: true });
                group.controls.cdConnAdult.clearValidators();	
                group.controls.cdConnAdult.updateValueAndValidity({ onlySelf: true });
                group.controls.cdHomeless.clearValidators();	
                group.controls.cdHomeless.updateValueAndValidity({ onlySelf: true });
                group.controls.cdSubstanceAbuse.clearValidators();	
                group.controls.cdSubstanceAbuse.updateValueAndValidity({ onlySelf: true });
                group.controls.cdIncarceration.clearValidators();	
                group.controls.cdIncarceration.updateValueAndValidity({ onlySelf: true });
                group.controls.cdChildren.clearValidators();	
                group.controls.cdChildren.updateValueAndValidity({ onlySelf: true });
                group.controls.cdBirthMarriage.clearValidators();	
                group.controls.cdBirthMarriage.updateValueAndValidity({ onlySelf: true });
                group.controls.cdMedicaid.clearValidators();	
                group.controls.cdMedicaid.updateValueAndValidity({ onlySelf: true });
                group.controls.cdOtherHealthIns.clearValidators();	
                group.controls.cdOtherHealthIns.updateValueAndValidity({ onlySelf: true });
                group.controls.cdInsMedical.clearValidators();	
                group.controls.cdInsMedical.updateValueAndValidity({ onlySelf: true });
                group.controls.cdInsMental.clearValidators();	
                group.controls.cdInsMental.updateValueAndValidity({ onlySelf: true });
                group.controls.cdInsPrescription.clearValidators();	
                group.controls.cdInsPrescription.updateValueAndValidity({ onlySelf: true });

                if (countQsWAns == 0) {
                    const noAnswerMessage = group.controls.noAnswerMessage.value;
                    group.controls.noAnswerMessage.setValidators(
                        NytdSurveyValidators.setErrorValidator({
                            AT_LEAST_ONE_QUES_NEEDS_ANS: { actualValue: noAnswerMessage, hideFieldName: true }
                        }));
                } else { group.controls.noAnswerMessage.clearValidators(); }
                group.controls.noAnswerMessage.updateValueAndValidity({ onlySelf: true });
    
            }

            return null;
        };
    }

    static setErrorValidator(errorObject: any): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            return errorObject;
        };
    }

}
