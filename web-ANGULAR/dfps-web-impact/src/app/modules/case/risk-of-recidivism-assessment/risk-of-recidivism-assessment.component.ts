import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  ENVIRONMENT_SETTINGS,
  FormService,
  FormValue,
  NavigationService,
  SET } from 'dfps-web-lib';
import { CaseService } from '@case/service/case.service';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import { BsModalService } from 'ngx-bootstrap/modal';
import { HelpService } from '../../../common/impact-help.service';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { RiskOfRecidivismAssessmentValidators } from './risk-of-recidivism-assessment.validators';

@Component({
  selector: 'app-risk-of-recidivism-assessment',
  templateUrl: './risk-of-recidivism-assessment.component.html',
  styleUrls: ['./risk-of-recidivism-assessment.component.css']
})
export class RiskOfRecidivismAssessmentComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  cdTask: any;
  apsRoraData: any;
  historicalQuestions: any[] = [];
  currentInvestigationQuestions: any[] = [];
  clientQuestions: any[] = [];
  apsRoraForm: FormGroup;
  // hasSupervisorAccess
  isSystemSupervisor: any;
  saveAndCompleteEnabled: boolean;
  riskLevelOptions = [{
    label:'Increase risk by one level',
    value: 'I'
  },
  {
    label:'Decrease risk by one level',
    value: 'D'
  },
  {
    label:'No discretionary overrides apply',
    value: 'N'
  }];

  CLIENT_REFUSED_QUESTION = {
    questionId: 'R12',
    noAnwserId: 218,
    yesAnswerId: 219,
    yesAnswerId2: 220
  };

  PRIOR_INVESTIGATION_QUES = {
    questionId: 'R1',
    NoneAnswerId: 193
  };

  isFormDisabled = false;
  questionFormSub: Subscription;
  finalRiskVal:string;
  environment: string;
  formValues: FormValue[];
  dirtyCheckSub: Subscription;
  formsSubscription: Subscription;
  showFormLaunch = true;
  setFormLaunch = false;
  questionCaseIdsMap = {};
  indexMap = {};

  constructor(private navigationService: NavigationService,
              private modalService: BsModalService,
              private formService: FormService,
              private caseService: CaseService,
              private helpService: HelpService,
              private route: ActivatedRoute,
              public store: Store<{ dirtyCheck: DirtyCheck }>,
              @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
              private formBuilder: FormBuilder) {
                super(store);
               }

  ngOnInit(): void {
    this.navigationService.setTitle('APS Risk of Recidivism Assessment Page');
    this.helpService.loadHelp('Assess');
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.isSystemSupervisor = routeParams.get('isSystemSupervisor')
    }
    this.initializeScreen();
    this.environment = this.environmentSettings.environmentName === 'Local' ? '' : '/impact3';
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
  }

  launchForm(data: any) {
    if (data) {
      const { pageMode, eventId } = this.apsRoraData;
      if ((pageMode === 'NEW' && !eventId) || (pageMode === 'EDIT' && this.apsRoraForm.dirty)) {
        const initialState = {
          title: 'Risk of Recidivism Assessment',
          message: pageMode === 'EDIT' ?
          'Page data has changed.  Please save before producing document.' :
          'Please save page before producing document.',
          showCancel: false,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
        });
        return;
      }
    }
    this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
  }

  ngOnDestroy(): void {
    this.formsSubscription?.unsubscribe();
    this.dirtyCheckSub?.unsubscribe();
  }

  initializeScreen() {
    this.cdTask = 9724;
    this.caseService.getApsRora(this.cdTask, this.isSystemSupervisor).subscribe(res => {
      this.apsRoraData = res;
      this.setFormLaunch = this.apsRoraData.eventId || this.apsRoraData.pageMode === 'NEW';
      this.historicalQuestions = this.getQuestionsForSection('HI');
      this.currentInvestigationQuestions = this.getQuestionsForSection('CI');
      this.clientQuestions = this.getQuestionsForSection('CC');
      this.createForm();
      this.setHiddenAttributeForHistoricalQuestion(this.apsRoraForm.get(`questionForm.${this.PRIOR_INVESTIGATION_QUES.questionId}`).value);
      this.formValues = this.getForms();
      this.calculateFinalRiskVal();
      this.saveAndCompleteEnabled = this.apsRoraData?.preSingleStage ||
                                    ((!this.apsRoraData?.preSingleStage) &&
                                      this.apsRoraData?.validAllgExists &&
                                      this.apsRoraData?.sacompleted);
    });
  }

  calculateFinalRiskVal() {
    if (this.apsRoraData?.eventStatus === 'COMP' || this.apsRoraData?.eventStatus === 'PROC') {
      if (!this.apsRoraData?.indMandtryOverride && this.apsRoraData.problemCount > 0) {
        this.finalRiskVal = 'High';
      } else if ((this.apsRoraData.indMandtryOverride && this.apsRoraData.problemCount > 0 && this.apsRoraData.cdFinalRiskLevel == null) || (this.apsRoraData.indMandtryOverride && this.apsRoraData.problemCount > 0)) {
        this.finalRiskVal = 'High';
      } else if (this.apsRoraData.indMandtryOverride && this.apsRoraData.problemCount == 0) {
        this.finalRiskVal = '';
      } else {
        this.finalRiskVal = this.apsRoraData?.cdFinalRiskLevel
      }
    } else {
      this.finalRiskVal = this.apsRoraData?.cdFinalRiskLevel
    }
  }


  getQuestionsForSection(sectionCode: string, currentQuestions = []) {
    return [...this.apsRoraData.responses.filter((x) => x.cdSection === sectionCode).map((x) => ({...x,
      hidden: currentQuestions.find((y) => y.cdRoraQuestion === x.cdRoraQuestion)?.hidden || false,
      options: [
      ...x.apsRoraAnswerLookup.map((answer) => ({
        label: answer.txtAnswer,
        value: answer.idApsRoraAnswerLookup,
        snIndex: answer.snIndexValue,
        aneIndex: answer.aneIndexValue,
        order: answer.nbrOrder,
        subOptions: answer.apsRoraFollowupResponseDtoList && answer.apsRoraFollowupResponseDtoList.length > 0 ? [
          ...answer.apsRoraFollowupResponseDtoList.map((subOption) => ({
            label: subOption.followupTxtAnswer,
            value: `${x.cdRoraQuestion}$$${subOption.idApsRoraFollowupLookup}`
          }))
        ] : []
      }))
    ]}))];
  }

  getDefaultAnswerForNewMode(question) {
    switch(question.cdRoraQuestion) {
      case this.PRIOR_INVESTIGATION_QUES.questionId:
        return this.getDefaultAnswerBasedUponCaseAndOrder(question);
      case 'R2':
      case 'R5':
      case 'R6':
      case 'R9':
        return this.getDefaultAnswerBasedUponCaseAndOrder(question);
      case 'R13':
        return this.apsRoraData?.versionNumber === 2 ?
        this.getDefaultAnswerBasedUponCaseAndOrder(question, this.apsRoraData?.snInvestigationCount) : null;
      case 'R14':
        return this.apsRoraData?.versionNumber === 2 ?
        this.getDefaultAnswerBasedUponCaseAndOrder(question, this.apsRoraData?.aneInvestigationCount) : null;
      default:
        return question.idApsRoraAnswerLookup
    }
  }

  getDefaultAnswerBasedUponCaseAndOrder(question, caseIds = null) {
    if (!caseIds) {
      caseIds = this.getCaseIdForQuestion(question.cdRoraQuestion);
    }
    const hasOneOrTwoOption = ['R2', 'R5'].includes(question.cdRoraQuestion);
    const options = [...question.apsRoraAnswerLookup].sort((a, b) => a.nbrOrder > b.nbrOrder ?  1: -1);
    if (caseIds?.length > 0) {
    const optionIndex = hasOneOrTwoOption && caseIds.length === 2 ? (caseIds.length - 1) : caseIds.length;
    return options[optionIndex]?.idApsRoraAnswerLookup || options[(options.length -1)].idApsRoraAnswerLookup;
    }
    return options[0].idApsRoraAnswerLookup;
  }

  createForm() {
    const questionFormValues = {};
    this.isFormDisabled = this.apsRoraData.pageMode === 'VIEW';
    const isNewMode = this.apsRoraData.pageMode === 'NEW';
    this.apsRoraData.responses.forEach((question) => {
      if (question.hasCheckBoxAnswers) {
        if (question.apsRoraAnswerLookup && question.apsRoraAnswerLookup.length > 0) {
          question.apsRoraAnswerLookup.forEach((answer) => {
            const isAnswerSelected = question.apsRoraResponsesDtoList?.
              some((x) => x.idApsRoraAnswerLookup === answer.idApsRoraAnswerLookup);
            questionFormValues[`${question.cdRoraQuestion}$$${answer.idApsRoraAnswerLookup}`] = { value: isAnswerSelected ? true : false,
              disabled: (this.isFormDisabled || this.getDisabledValueForCheckboxType(question.cdRoraQuestion,
                answer.idApsRoraAnswerLookup, question.apsRoraResponsesDtoList?.map((x) => x.idApsRoraAnswerLookup)?.filter((x) => x))) }
          });
        }
      } else {
        const value = (isNewMode || this.isFormDisabled) && !this.apsRoraData.idApsRora && !question.idApsRoraAnswerLookup
        // const value = !question.idApsRoraAnswerLookup
          ? this.getDefaultAnswerForNewMode(question) : question.idApsRoraAnswerLookup;
        questionFormValues[question.cdRoraQuestion] = [{ value, disabled: this.isFormDisabled }];
      }
      if (question.apsRoraAnswerLookup && question.apsRoraAnswerLookup.length > 0) {
        question.apsRoraAnswerLookup.forEach((answer) => {
          if (answer.apsRoraFollowupResponseDtoList && answer.apsRoraFollowupResponseDtoList.length > 0) {
            answer.apsRoraFollowupResponseDtoList.forEach((followUp) => {
              questionFormValues[`${question.cdRoraQuestion}$$${followUp.idApsRoraFollowupLookup}`] =
                [{
                  value: followUp.indApsRoraFollowup === 'Y',
                  disabled: (this.isFormDisabled || (answer.idApsRoraAnswerLookup !== question.idApsRoraAnswerLookup))
                }];
            })
          }
        })
      }
    });
    const  { cdDiscretryOverride } =  this.apsRoraData;
    this.apsRoraForm = this.formBuilder.group({
      discretryOverride: { value: cdDiscretryOverride || 'N',
        disabled: (this.isFormDisabled || (this.apsRoraData?.problemCount && this.apsRoraData?.problemCount !== 0))
      },
      comments: { value:this.apsRoraData.txtComments, disabled: this.isFormDisabled },
      // reasonForOverride: { value: this.apsRoraData.txtRsnForOverride, disabled: this.isFormDisabled },
      reasonForOverride: [{ value: this.apsRoraData.txtRsnForOverride, disabled: this.isFormDisabled },
        cdDiscretryOverride === 'I' || cdDiscretryOverride === 'D' ? [RiskOfRecidivismAssessmentValidators.validateReason()] : []],
      questionForm: this.formBuilder.group({...questionFormValues})
    });
    this.subscribeEventChange();
    this.showPendingStatusMessageAlert();
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.setQuestionsMap();
  }

  setReasonForOverrideValidations(discretryOverride: string) {
    this.resetRiskLevelError();
    const reasonControl = this.apsRoraForm.get('reasonForOverride');
    reasonControl.clearValidators();
    if (discretryOverride === 'I' || discretryOverride === 'D') {
      reasonControl.setValidators([RiskOfRecidivismAssessmentValidators.validateReason()]);
    }
    reasonControl.updateValueAndValidity();
  }

  showPendingStatusMessageAlert() {
    // if (this.apsRoraData.approvalEvent?.status?.toUpperCase() === 'PEND' && this.apsRoraData.popUpMessages?.length > 0) {
    if (this.apsRoraData.popUpMessages?.length > 0) {
      const initialState = {
        message: this.apsRoraData.popUpMessages[0],
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {

      });
    }
  }

  getDisabledValueForCheckboxType(questionId: string, answerId: number, selectedAnswers: number[]) {
    if (selectedAnswers?.length > 0 && questionId === this.CLIENT_REFUSED_QUESTION.questionId) {
      if (answerId === this.CLIENT_REFUSED_QUESTION.noAnwserId) {
      return selectedAnswers.includes(answerId) ? false : true;
      } else {
        return !selectedAnswers.includes(this.CLIENT_REFUSED_QUESTION.noAnwserId) ? false : true;
      }
    }
    return false;
  }

  setHiddenAttributeForHistoricalQuestion(value: number) {
    const hidden = value === this.PRIOR_INVESTIGATION_QUES.NoneAnswerId;
    // const savedAnswer = this.apsRoraData.responses.find((x) => x.cdRoraQuestion === this.PRIOR_INVESTIGATION_QUES.questionId).idApsRoraAnswerLookup;
    const patchValue = {};
    const questionDefaultAnswerMapping = {
      R2: 196,
      R5: 203,
      R6: 206,
      R9: 212
    };
    const { pageMode, idApsRora } = this.apsRoraData;
    this.historicalQuestions = this.historicalQuestions.map((x) => {
      let isQuestionHidden = false;
      if (x.nbrOrder >=2  && x.nbrOrder <= 10) {
        // if (this.apsRoraData.pageMode === 'NEW') {
        //if (savedAnswer === this.PRIOR_INVESTIGATION_QUES.NoneAnswerId && value !== this.PRIOR_INVESTIGATION_QUES.NoneAnswerId) {
         if (pageMode === 'NEW' && !idApsRora
            && questionDefaultAnswerMapping[x.cdRoraQuestion] && !x.idApsRoraAnswerLookup &&
            !this.apsRoraForm.get(`questionForm.${x.cdRoraQuestion}`).value) {
            patchValue[x.cdRoraQuestion] = this.getDefaultAnswerBasedUponCaseAndOrder
              (this.apsRoraData.responses.find((resQues) => resQues.cdRoraQuestion === x.cdRoraQuestion));
          } 
          else if (pageMode === 'EDIT' && idApsRora && x.idApsRoraAnswerLookup && !this.apsRoraForm.get(`questionForm.${x.cdRoraQuestion}`).value) {
            patchValue[x.cdRoraQuestion] = x.idApsRoraAnswerLookup;
          }
        // }
        // }
        isQuestionHidden = hidden;
      }
      return {
        ...x,
        hidden: isQuestionHidden
      };
    });
    if (!onload) {
      this.apsRoraForm.patchValue({questionForm: patchValue}, { emitEvent: false});
    }
  }

  subscribeForDirtyCheck() {
    this.dirtyCheckSub?.unsubscribe();
    this.dirtyCheckSub = this.apsRoraForm.valueChanges.pipe(take(1)).subscribe(() => {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
    });
  }

  subscribeEventChange() {
    const eventOptions = { emitEvent: false};
    if (this.apsRoraData.responseListComplete && this.apsRoraForm.get('discretryOverride').value) {
      this.questionFormSub?.unsubscribe();
      this.questionFormSub = this.apsRoraForm.get('questionForm').valueChanges.subscribe(() => {
        const initialState = {
          message: 'Changing the response on an assessment item will reset the Discretionary Override and Final Risk Level.',
          showCancel: false
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md modal-dialog-centered', initialState,
          ignoreBackdropClick: true,
          keyboard: false,
          backdrop: true
        });
          (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
            this.apsRoraForm.patchValue({
              discretryOverride:'',
            }, { emitEvent: false});
            this.finalRiskVal = '';
           // this.apsRoraForm.get('discretryOverride').disable(eventOptions);
          });
        this.questionFormSub?.unsubscribe();
      });
    }
    this.apsRoraForm.get('discretryOverride').valueChanges.subscribe((value) => {
      this.setReasonForOverrideValidations(value);
    });
    this.subscribeForDirtyCheck();
    this.apsRoraForm.get(`questionForm.${this.PRIOR_INVESTIGATION_QUES.questionId}`).valueChanges.subscribe((value) => {
      this.setHiddenAttributeForHistoricalQuestion(value);
    });
    const noControlField = `${this.CLIENT_REFUSED_QUESTION.questionId}$$${this.CLIENT_REFUSED_QUESTION.noAnwserId}`;
    const yesControlField = `${this.CLIENT_REFUSED_QUESTION.questionId}$$${this.CLIENT_REFUSED_QUESTION.yesAnswerId}`;
    const yesControlField2 = `${this.CLIENT_REFUSED_QUESTION.questionId}$$${this.CLIENT_REFUSED_QUESTION.yesAnswerId2}`;
    this.apsRoraForm.get(`questionForm.${noControlField}`).valueChanges.subscribe((value) => {
        if (value) {
          this.apsRoraForm.get(`questionForm.${yesControlField}`).disable(eventOptions);
          this.apsRoraForm.get(`questionForm.${yesControlField2}`).disable(eventOptions);
        } else {
          this.apsRoraForm.get(`questionForm.${yesControlField}`).enable(eventOptions);
          this.apsRoraForm.get(`questionForm.${yesControlField2}`).enable(eventOptions);
        }
    });
    this.apsRoraForm.get(`questionForm.${yesControlField}`).valueChanges.subscribe((value) => {
      if (value || this.apsRoraForm.get(`questionForm.${yesControlField2}`).value) {
        this.apsRoraForm.get(`questionForm.${noControlField}`).disable(eventOptions);
      } else {
        this.apsRoraForm.get(`questionForm.${noControlField}`).enable(eventOptions);
      }
    });
    this.apsRoraForm.get(`questionForm.${yesControlField2}`).valueChanges.subscribe((value) => {
      if (value || this.apsRoraForm.get(`questionForm.${yesControlField}`).value) {
        this.apsRoraForm.get(`questionForm.${noControlField}`).disable(eventOptions);
      } else {
        this.apsRoraForm.get(`questionForm.${noControlField}`).enable(eventOptions);
      }
    });
  }

  setQuestionsMap() {
    this.questionCaseIdsMap = {};
    this.indexMap = {};
    this.apsRoraData.responses.forEach((question) => {
      const caseIds = this.getCaseIdForQuestion(question.cdRoraQuestion);
      const answer = question.idApsRoraAnswerLookup || this.apsRoraForm.get(`questionForm.${question.cdRoraQuestion}`)?.value;
      if (caseIds?.length > 0) {
        const selectedOption = question.apsRoraAnswerLookup.find((x) =>
        x.idApsRoraAnswerLookup === answer);
        if (selectedOption) {
          this.questionCaseIdsMap[`${question.cdRoraQuestion}${selectedOption.idApsRoraAnswerLookup}`] = caseIds;
        }
      }
      if (this.apsRoraData.hasSupervisorAccess && this.apsRoraData.idApsRora && answer) {
        const selectedOption = question.apsRoraAnswerLookup.find((x) =>
        x.idApsRoraAnswerLookup === answer);
        this.indexMap[`${question.cdRoraQuestion}${selectedOption.idApsRoraAnswerLookup}`] = {
          snIndex: selectedOption.snIndexValue,
          aneIndex: selectedOption.aneIndexValue
        };
      }
    });
  }

  getCaseIdForQuestion(questionId): number[] {
    const data =  this.apsRoraData;
    switch (questionId) {
      case 'R1':
        return data?.priorAPSInvest;
      case 'R2':
        return data?.priorApsSnAlleg;
      case 'R5':
        return data?.priorValidatedAPS;
      case 'R6':
        return data?.priorANEValidation;
      case 'R9':
        return data?.priorAneBySpOrParamour;
      default:
        return null;
    }
  }

  launchCaseReviewLinkOnKeyPress(event, caseId: number): void {
    if (event.code === 'Enter') {
      this.launchCaseReviewLink(caseId);
    }
  }

  openQuestionLinkOnKeyPress(event, link: string) {
    if (event.code === 'Enter') {
      this.openQuestionLink(event, link);
    }
  }

  openQuestionLink(event, link: string) {
    event.stopPropagation();
    window.open(`${this.environment}${link}`, '_blank');
  }

  launchCaseReviewLink(caseId: number): void {
    this.formService.launchForm(JSON.stringify({
      docType: 'cfiv2800',
      protectDocument: 'true',
      pCase: caseId.toString()
    }));
  }

  onAnswerChange(question) {
    const { options } = question;
    if (options?.length > 0) {
      options.forEach((option) => {
        const { subOptions } = option;
        if (subOptions?.length > 0) {
          const isDisabled = this.apsRoraForm.get(`questionForm.${question.cdRoraQuestion}`).value !== option.value;
          subOptions.forEach((subOption) => {
            const control = this.apsRoraForm.get(`questionForm.${subOption.value}`);
            if (isDisabled) {
              control.reset();
              control.disable();
            } else {
              control.enable()
            }
          })
        }
      });
    }
  }

  getSavePayload() {
    const responses = [...this.apsRoraData.responses.map((ques) => {
      const question = { ...ques};
      if (question.hasCheckBoxAnswers) {
        question.apsRoraAnswerLookup.forEach((answer, index) => {
          const ansControl = this.apsRoraForm.get(`questionForm.${question.cdRoraQuestion}$$${answer.idApsRoraAnswerLookup}`);
          if (question.apsRoraResponsesDtoList?.[index]) {
            question.apsRoraResponsesDtoList[index].idApsRoraAnswerLookup = ansControl.value ? answer.idApsRoraAnswerLookup : null;
          }
        });
      }
      else {
        const control = this.apsRoraForm.get(`questionForm.${question.cdRoraQuestion}`);
        if (control.dirty || control.value || (question.idApsRoraAnswerLookup && !control.value)) {
          if (question.nbrOrder >= 2 && question.nbrOrder <= 10 && this.apsRoraForm.get(`questionForm.${this.PRIOR_INVESTIGATION_QUES.questionId}`).value === this.PRIOR_INVESTIGATION_QUES.NoneAnswerId) {
            question.idApsRoraAnswerLookup =  null;
            control.patchValue('');
          } else {
            question.idApsRoraAnswerLookup = control.value ? control.value : null;
          }
        }
      }
      if (question.apsRoraAnswerLookup && question.apsRoraAnswerLookup.length > 0) {
        question.apsRoraAnswerLookup = question.apsRoraAnswerLookup.map((ans) => {
          const answer = {...ans};
          if (answer.apsRoraFollowupResponseDtoList && answer.apsRoraFollowupResponseDtoList.length > 0) {
            answer.apsRoraFollowupResponseDtoList = answer.apsRoraFollowupResponseDtoList.map((followUp) => {
              const followUpControl = this.apsRoraForm.get(`questionForm.${question.cdRoraQuestion}$$${followUp.idApsRoraFollowupLookup}`);
              return followUpControl.dirty ? {
                ...followUp,
                indApsRoraFollowup: followUpControl.value ? 'Y' : 'N'
              }: followUp;
            });
            this.setCdRoraAnswerForQuestion(question);
          }
          return answer;
        });
      }
      return question;
    })];
    return {
      ...this.apsRoraData,
      txtComments: this.apsRoraForm.get('comments').value,
      txtRsnForOverride: this.apsRoraForm.get('reasonForOverride').value,
      cdDiscretryOverride: this.apsRoraForm.get('discretryOverride').value,
      responses
    };
  }

  setCdRoraAnswerForQuestion(question) {
    question.cdRoraAnswer = question.apsRoraAnswerLookup.find((x) =>
    x.idApsRoraAnswerLookup === question.idApsRoraAnswerLookup)?.cdRoraAnswer || null;
  }

  save() {
    if (this.validateFormGroup(this.apsRoraForm)) {
      this.caseService.saveApsRora(this.getSavePayload()).subscribe((res) => {
        this.onSaveSuccess(res);
      });
    } else {
      this.validateRiskLevel();
    }
  }

  validateRiskLevel() {
    const value = this.apsRoraForm.get('discretryOverride').value;
    let riskLevelErrorKey = '';
    const finalRiskValue = this.finalRiskVal ? this.finalRiskVal.toLowerCase() : '';
    if (finalRiskValue === 'low' && value === 'D') {
      riskLevelErrorKey = 'riskLowError';
    } else if (finalRiskValue === 'high' && value === 'I') {
      riskLevelErrorKey = 'riskHighError';
    }
    if (riskLevelErrorKey) {
      this.resetRiskLevelError();
      this.validationErrors.push({
        fieldName: 'riskLevelError',
        hideFieldName: true,
        errors: {
          [`${riskLevelErrorKey}`]: true
        }
      });
    }
  }

  resetRiskLevelError() {
    if (this.validationErrors && this.validationErrors.length) {
      this.validationErrors = this.validationErrors.filter((x) => x.fieldName !== 'riskLevelError');
    }
  }

  saveAndCompleteAction() {
    const payload = this.getSavePayload();
    payload.saveAndCompleteButtonClicked = true;
    this.caseService.saveAndCompleteApsRora(payload).subscribe((res) => {
      this.onSaveSuccess(res);
    });
  }

  saveAndComplete() {
    if (this.apsRoraForm.dirty && this.apsRoraData.responseListComplete) {
      const initialState = {
        message: `Some answers have been changed. Please click 'Save' to review the
        'Risk Score/Level' section prior to completing the assessment.`,
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {

        });
      return false;
    }
    const discretryOverride = this.apsRoraForm.get('discretryOverride').value;
    if (discretryOverride === 'I' || discretryOverride === 'D') {
      const initialState = {
        message: `Discretionary Override is used only for merging cases. If the case is not being merged, please cancel.`,
        showCancel: true
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe((res) => {
          if (res) {
            this.validatePreSingleStage();
          }
        });
      return false;
    }
    this.validatePreSingleStage();
  }
  validatePreSingleStage() {
    if (this.apsRoraData.preSingleStage) {
      this.saveAndCompleteAction();
    } else {
      const initialState = {
        message: this.apsRoraData.cntctStdComplete ?
          `The contact standards have already been set and will not change based on any updates made to the RORA.` :
          `Once the 'Save and Complete' button is clicked, the contact standards will be set for the remainder of the case.
         Timeframe for completing the SNA starts once this button is clicked.`,
        showCancel: true
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((res) => {
        if (res) {
          this.saveAndCompleteAction();
        }
      });
    }
  }

  onSaveSuccess(res) {
    this.apsRoraData = res;
    this.historicalQuestions = this.getQuestionsForSection('HI', this.historicalQuestions);
    this.currentInvestigationQuestions = this.getQuestionsForSection('CI', this.currentInvestigationQuestions);
    this.clientQuestions = this.getQuestionsForSection('CC', this.clientQuestions);
    window.scrollTo(0, 0);
    this.apsRoraForm.patchValue({
      discretryOverride: this.apsRoraData.cdDiscretryOverride || 'N',
      comments: this.apsRoraData.txtComments,
      reasonForOverride: this.apsRoraData.txtRsnForOverride
    }, { emitEvent: false });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.subscribeForDirtyCheck();
    this.subscribeEventChange();
    this.setQuestionsMap();
    this.apsRoraForm.markAsPristine();
    this.setFormLaunch = this.apsRoraData.eventId == 0 || this.apsRoraData.pageMode === 'NEW';
    this.showFormLaunch = false;
    this.formValues = this.getForms();
    this.calculateFinalRiskVal();
    setTimeout(() => {
      this.showFormLaunch = true;
    }, 100);
  }

  getForms(): FormValue[] {
    return [{
      formName: 'RORA Form',
      formParams: {
        docType: 'APSRORA',
        docExists: 'false',
        protectDocument: 'true',
        displayOnly: 'true',
        checkForNewMode: 'false',
        pStage: this.apsRoraData?.idStage ? String(this.apsRoraData?.idStage) : '',
        pEvent: this.apsRoraData?.eventId ? String(this.apsRoraData.eventId) : ''
      }
    }
    ];
  }
}
