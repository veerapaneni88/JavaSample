import { Component, Inject, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  ENVIRONMENT_SETTINGS,
  NavigationService,
  DfpsAddressValidatorComponent,
} from 'dfps-web-lib';
import { Subscription } from 'rxjs';
import { CaseService } from '../../service/case.service';
import { NytdSurveyDetail, NytdSurveyDetailRes } from '../../model/NytdSurvey';
import { NytdSurveyValidators } from './nytd-survey-detail.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'nytd-survey-detail',
  templateUrl: './nytd-survey-detail.component.html',
  styleUrls: ['./nytd-survey-detail.component.css']
})
export class NytdSurveyDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;

  modalSubscription: Subscription;
  nytdSurveyDetailForm: FormGroup;
  yesNoDeclined: any;
  yesNoDeclinedDontKnow: any;
  highestDegree: any;
  nytdSurveyHeaderId: any;
  staffId: any;
  nytdSurveyDetailResponse: NytdSurveyDetailRes;
  nytdSurveyDetail?: NytdSurveyDetail;
  isReadOnly = false;
  showWellfareSubQues = false;
  showChildBirthSubQues = false;
  showHealthInsSubQues = false;
  isBaseline = false;
  isFollup19or21 = false;
  q10Label = '';
  q11Label = '';
  q12Label = '';
  q13Label = '';

  minDate: any;
  maxDate: any;

  constructor(
    private navigationService: NavigationService,
    private caseService: CaseService,
    private formBuilder: FormBuilder,
    private helpService: HelpService,
    private route: ActivatedRoute,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnDestroy() {
  }

  ngOnInit(): void {
    this.navigationService.setTitle('NYTD Survey');
    this.createForm();

    this.helpService.loadHelp('NYTD');

    this.staffId = this.route.snapshot.paramMap.get('idStaff');
    this.nytdSurveyHeaderId = this.route.snapshot.paramMap.get('idSurvey');

    this.caseService.getNytdSurveyDetail(this.nytdSurveyHeaderId).subscribe((response) => {
      this.nytdSurveyDetailResponse = response;
      this.nytdSurveyDetailResponse.staffId = this.staffId;
      this.nytdSurveyDetailResponse.nytdSurveyHeaderId = this.nytdSurveyHeaderId;

      this.setupSurveyOptions();

      this.isReadOnly = this.nytdSurveyDetailResponse.pageMode === 'VIEW' ? true : false;

      if (this.nytdSurveyDetailResponse.nytdSurveyDetail) {
        const nytdSurvey = this.nytdSurveyDetailResponse.nytdSurveyDetail;
        this.nytdSurveyDetailForm.setValue({
          dtYouthResponded:	nytdSurvey.dtYouthResponded,
          cdMethodReceived:	nytdSurvey.cdMethodReceived,
          nbrInterviewMinutes:	nytdSurvey.nbrInterviewMinutes,
          indSpecAccomodation: (nytdSurvey.indSpecAccomodation === 'Y' ? 'true' : null),
          cdFullEmploy:	nytdSurvey.cdFullEmploy,
          cdPartEmploy:	nytdSurvey.cdPartEmploy,
          cdSkillsEmploy:	nytdSurvey.cdSkillsEmploy,
          cdSocialSecurity:	nytdSurvey.cdSocialSecurity,
          cdEducAid:	nytdSurvey.cdEducAid,
          cdAssistFin:	nytdSurvey.cdAssistFin,
          cdAssistFood:	nytdSurvey.cdAssistFood,
          cdAssistHousing:	nytdSurvey.cdAssistHousing,
          cdOtherFinAid:	nytdSurvey.cdOtherFinAid,
          cdEducCert:	nytdSurvey.cdEducCert,
          cdEducEnroll:	nytdSurvey.cdEducEnroll,
          cdConnAdult:	nytdSurvey.cdConnAdult,
          cdHomeless:	nytdSurvey.cdHomeless,
          cdSubstanceAbuse:	nytdSurvey.cdSubstanceAbuse,
          cdIncarceration:	nytdSurvey.cdIncarceration,
          cdChildren:	nytdSurvey.cdChildren,
          cdBirthMarriage:	nytdSurvey.cdBirthMarriage,
          cdMedicaid:	nytdSurvey.cdMedicaid,
          cdOtherHealthIns:	nytdSurvey.cdOtherHealthIns,
          cdInsMedical:	nytdSurvey.cdInsMedical,
          cdInsMental:	nytdSurvey.cdInsMental,
          cdInsPrescription:	nytdSurvey.cdInsPrescription,
          noAnswerMessage: '',
          isBaselineSurvey: '',
          surveyStartDate: '',
          surveyEndDate: '',
          clickedButton: ''
        });

        if(this.nytdSurveyDetailResponse.nytdPopulationList.baselineInd === 'Y') {
          this.isBaseline = true;
          this.q10Label = '10. Have you ever been homeless?';
          this.q11Label = '11. Have you ever referred yourself or has someone else referred you ' +
                            'for an alcohol or drug abuse assessment or counseling?';
          this.q12Label = '12. Have you ever been confined in a jail, prison, or correctional facility, ' +
                            'or juvenile or community detention facility, in connection with ' +
                            'allegedly committing a crime?';
          this.q13Label = '13. Have you ever given birth or fathered any children that were born?';
          this.nytdSurveyDetailForm.controls.isBaselineSurvey.setValue('true');
          this.navigationService.setTitle('NYTD Survey for 17 years of youth - Staff');
        }

        if(this.nytdSurveyDetailResponse.nytdPopulationList.followup19Ind === 'Y' ||
           this.nytdSurveyDetailResponse.nytdPopulationList.followup21Ind === 'Y') {
          this.isFollup19or21 = true;
          this.q10Label = '10. In the past two years, were you homeless at any time?';
          this.q11Label = '11. In the past two years, did you refer yourself or had someone else referred you ' +
                            'for an alcohol or drug abuse assessment or counseling?';
          this.q12Label = '12. In the past two years, were you confined in a jail, prison, correctional facility, ' +
                            'or juvenile or community detention facility, in connection with ' +
                            'allegedly committing a crime?';
          this.q13Label = '13. In the past two years, did you give birth to or father any children that were born?';
          this.nytdSurveyDetailForm.controls.isBaselineSurvey.setValue('false');
          if(this.nytdSurveyDetailResponse.nytdPopulationList.followup19Ind === 'Y') {
            this.navigationService.setTitle('NYTD Survey for 19 years of youth - Staff');
          } else {
            this.navigationService.setTitle('NYTD Survey for 21 years of youth - Staff');
          }
        }

        if (this.isFollup19or21 == true) {
          this.showWellfareSubQues = true;
        } else {
          this.showWellfareSubQues = false;
        }
        if (nytdSurvey.cdChildren === '10') {
          this.showChildBirthSubQues = true;
        } else {
          this.showChildBirthSubQues = false;
        }
        if (nytdSurvey.cdOtherHealthIns === '10') {
          this.showHealthInsSubQues = true;
        } else {
          this.showHealthInsSubQues = false;
        }

        this.methodUpdated();
        this.setupSurveyDates();

        if(this.isReadOnly) {
          FormUtils.disableFormControlStatus(this.nytdSurveyDetailForm, ['dtYouthResponded','cdMethodReceived','nbrInterviewMinutes',
            'indSpecAccomodation','cdFullEmploy','cdPartEmploy','cdSkillsEmploy','cdSocialSecurity','cdEducAid','cdAssistFin',
            'cdAssistFood','cdAssistHousing','cdOtherFinAid','cdEducCert','cdEducEnroll','cdConnAdult','cdHomeless','cdSubstanceAbuse',
            'cdIncarceration','cdChildren','cdBirthMarriage','cdMedicaid','cdOtherHealthIns','cdInsMedical','cdInsMental','cdInsPrescription']);
        }

      }

    });

  }


createForm() {
  this.nytdSurveyDetailForm = this.formBuilder.group({
    dtYouthResponded: [''],
    cdMethodReceived: [''],
    nbrInterviewMinutes: [''],
    indSpecAccomodation: [''],
    cdFullEmploy: [''],
    cdPartEmploy: [''],
    cdSkillsEmploy: [''],
    cdSocialSecurity: [''],
    cdEducAid: [''],
    cdAssistFin: [''],
    cdAssistFood: [''],
    cdAssistHousing: [''],
    cdOtherFinAid: [''],
    cdEducCert: [''],
    cdEducEnroll: [''],
    cdConnAdult: [''],
    cdHomeless: [''],
    cdSubstanceAbuse: [''],
    cdIncarceration: [''],
    cdChildren: [''],
    cdBirthMarriage: [''],
    cdMedicaid: [''],
    cdOtherHealthIns: [''],
    cdInsMedical: [''],
    cdInsMental: [''],
    cdInsPrescription: [''],
    noAnswerMessage: [''],
    isBaselineSurvey: [''],
    surveyStartDate: [''],
    surveyEndDate: [''],
    clickedButton: ['']
  }, {
    validators: [
        NytdSurveyValidators.validateNytdSurvey()
      ]
    });
  }


  setupSurveyOptions() {
    this.yesNoDeclined = [];
    this.highestDegree = [];
    this.yesNoDeclinedDontKnow = [];
    if(this.nytdSurveyDetailResponse.yesNoDeclined) {
      this.nytdSurveyDetailResponse.yesNoDeclined.forEach(surveyOption => {
        this.yesNoDeclined.push({ label: surveyOption.decode, value: surveyOption.code })
      });
    }

    if(this.nytdSurveyDetailResponse.highestDegree) {
      this.nytdSurveyDetailResponse.highestDegree.forEach(surveyOption => {
        this.highestDegree.push({ label: surveyOption.decode, value: surveyOption.code })
      });
    }

    if(this.nytdSurveyDetailResponse.yesNoDeclinedDontKnow) {
      this.nytdSurveyDetailResponse.yesNoDeclinedDontKnow.forEach(surveyOption => {
        this.yesNoDeclinedDontKnow.push({ label: surveyOption.decode, value: surveyOption.code })
      });
    }
  }

  setupSurveyDates() {
    
    let sDt = new Date();
    let eDt = new Date();

    if(this.nytdSurveyDetailResponse.nytdPopulationList.baselineInd === 'Y') {
      if(this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.personDOB) {
        sDt = new Date(this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.personDOB);
        eDt = new Date(this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.personDOB);
      } else {
        sDt = new Date();
        eDt = new Date();
      }
      sDt.setFullYear(sDt.getFullYear() + 17);
      sDt.setDate(sDt.getDate()+1);
      eDt.setFullYear(eDt.getFullYear() + 17);
      eDt.setDate(eDt.getDate()+45);
    } else {
      if(this.nytdSurveyDetailResponse.nytdPopulationList.nytdReportPeriod.dtReportStart) {
        sDt = new Date(this.nytdSurveyDetailResponse.nytdPopulationList.nytdReportPeriod.dtReportStart);
      } else {
        sDt = new Date();
      }
      if(this.nytdSurveyDetailResponse.nytdPopulationList.nytdReportPeriod.dtReportEnd) {
        eDt = new Date(this.nytdSurveyDetailResponse.nytdPopulationList.nytdReportPeriod.dtReportEnd);
      } else {
        eDt = new Date();
      }

      if(sDt > eDt) {
        eDt = sDt;
      }
    }
    this.minDate = { year: sDt.getUTCFullYear(), month: sDt.getMonth() + 1, day: sDt.getDate() };
    this.maxDate = { year: eDt.getUTCFullYear(), month: eDt.getMonth() + 1, day: eDt.getDate() };
    this.nytdSurveyDetailForm.controls.surveyStartDate.setValue(sDt);
    this.nytdSurveyDetailForm.controls.surveyEndDate.setValue(eDt);

  }

  updateChildBirthQues() {
    if (this.nytdSurveyDetailForm.get('cdChildren').value === '10') {
      this.showChildBirthSubQues = true;
      this.nytdSurveyDetailForm.controls.cdBirthMarriage.setValue(null);
    } else {
      this.showChildBirthSubQues = false;
    }
  }

  updateHealthInsQues() {
    if (this.nytdSurveyDetailForm.get('cdOtherHealthIns').value === '10') {
      this.showHealthInsSubQues = true;
      this.nytdSurveyDetailForm.controls.cdInsMedical.setValue(null);
      this.nytdSurveyDetailForm.controls.cdInsMental.setValue(null);
      this.nytdSurveyDetailForm.controls.cdInsPrescription.setValue(null);
    } else {
      this.showHealthInsSubQues = false;
    }
  }

  submitCompletedSurvey() {
    this.nytdSurveyDetailForm.controls.clickedButton.setValue('Submit');
    this.save(true);
  }

  saveAndReturnLater() {
    this.nytdSurveyDetailForm.controls.clickedButton.setValue('Save and return');
    this.save(false);
  }

  save(isSubmit?: any) {
    if (this.validateFormGroup(this.nytdSurveyDetailForm)) {
      this.nytdSurveyDetail = {}
      if (this.nytdSurveyDetailResponse.nytdSurveyDetail) {
        this.nytdSurveyDetail.nytdSurveyHeaderId =
                  this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.nytdSurveyHeaderId;
        this.nytdSurveyDetail.nytdSurveyDetailId =
                  this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyDetailId;

        this.nytdSurveyDetail.isSubmit = isSubmit;
        this.nytdSurveyDetail.dtYouthResponded	=	this.nytdSurveyDetailForm.controls.dtYouthResponded.value;
        this.nytdSurveyDetail.cdMethodReceived	=	this.nytdSurveyDetailForm.controls.cdMethodReceived.value;
        this.nytdSurveyDetail.nbrInterviewMinutes	=	this.nytdSurveyDetailForm.controls.nbrInterviewMinutes.value;
        this.nytdSurveyDetail.indSpecAccomodation =
                  this.nytdSurveyDetailForm.controls.indSpecAccomodation.value ? 'Y' : null;
        this.nytdSurveyDetail.cdFullEmploy	=	this.nytdSurveyDetailForm.controls.cdFullEmploy.value;
        this.nytdSurveyDetail.cdPartEmploy	=	this.nytdSurveyDetailForm.controls.cdPartEmploy.value;
        this.nytdSurveyDetail.cdSkillsEmploy	=	this.nytdSurveyDetailForm.controls.cdSkillsEmploy.value;
        this.nytdSurveyDetail.cdSocialSecurity	=	this.nytdSurveyDetailForm.controls.cdSocialSecurity.value;
        this.nytdSurveyDetail.cdEducAid	=	this.nytdSurveyDetailForm.controls.cdEducAid.value;
        this.nytdSurveyDetail.cdAssistFin	=	this.nytdSurveyDetailForm.controls.cdAssistFin.value;
        this.nytdSurveyDetail.cdAssistFood	=	this.nytdSurveyDetailForm.controls.cdAssistFood.value;
        this.nytdSurveyDetail.cdAssistHousing	=	this.nytdSurveyDetailForm.controls.cdAssistHousing.value;
        this.nytdSurveyDetail.cdOtherFinAid	=	this.nytdSurveyDetailForm.controls.cdOtherFinAid.value;
        this.nytdSurveyDetail.cdEducCert	=	this.nytdSurveyDetailForm.controls.cdEducCert.value;
        this.nytdSurveyDetail.cdEducEnroll	=	this.nytdSurveyDetailForm.controls.cdEducEnroll.value;
        this.nytdSurveyDetail.cdConnAdult	=	this.nytdSurveyDetailForm.controls.cdConnAdult.value;
        this.nytdSurveyDetail.cdHomeless	=	this.nytdSurveyDetailForm.controls.cdHomeless.value;
        this.nytdSurveyDetail.cdSubstanceAbuse	=	this.nytdSurveyDetailForm.controls.cdSubstanceAbuse.value;
        this.nytdSurveyDetail.cdIncarceration	=	this.nytdSurveyDetailForm.controls.cdIncarceration.value;
        this.nytdSurveyDetail.cdChildren	=	this.nytdSurveyDetailForm.controls.cdChildren.value;
        this.nytdSurveyDetail.cdBirthMarriage	=	this.nytdSurveyDetailForm.controls.cdBirthMarriage.value;
        this.nytdSurveyDetail.cdMedicaid	=	this.nytdSurveyDetailForm.controls.cdMedicaid.value;
        this.nytdSurveyDetail.cdOtherHealthIns	=	this.nytdSurveyDetailForm.controls.cdOtherHealthIns.value;
        this.nytdSurveyDetail.cdInsMedical	=	this.nytdSurveyDetailForm.controls.cdInsMedical.value;
        this.nytdSurveyDetail.cdInsMental	=	this.nytdSurveyDetailForm.controls.cdInsMental.value;
        this.nytdSurveyDetail.cdInsPrescription	=	this.nytdSurveyDetailForm.controls.cdInsPrescription.value;

        this.nytdSurveyDetail.nytdSurveyHeader = {};
        this.nytdSurveyDetail.nytdSurveyHeader.nytdSurveyHeaderId = this.nytdSurveyDetail.nytdSurveyHeaderId;
        this.nytdSurveyDetail.nytdSurveyHeader.personId =
                    this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.personId;
        this.nytdSurveyDetail.nytdSurveyHeader.nytdReportPeriodId =
                    this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.nytdReportPeriodId;
        this.nytdSurveyDetail.nytdSurveyHeader.cdPopulationType =
                    this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.cdPopulationType;
        this.nytdSurveyDetail.nytdSurveyHeader.dtSurveyDue =
                    this.nytdSurveyDetailResponse.nytdSurveyDetail.nytdSurveyHeader.dtSurveyDue;
        if(isSubmit === true) {
          this.nytdSurveyDetail.nytdSurveyHeader.cdSurveyStatus = '50';
        } else {
          this.nytdSurveyDetail.nytdSurveyHeader.cdSurveyStatus = '40';
        }

        this.caseService.saveNytdSurveyDetail(this.nytdSurveyDetail)
        .subscribe((result) => {
          if (result) {
            setTimeout(() => {
               window.location.href = this.environmentSettings.impactP2WebUrl +
                   '/case/nytd/nytdInformation?isMPSEnvironment=false';
            }, 3000);
          }
        });
      }
    }
  }

  methodUpdated() {
    let directMethods = ['Face-to-Face','Phone','Combination'];
    let methodLabel = '';
    const method = this.nytdSurveyDetailResponse.contactMethod.find(option => option.code === this.nytdSurveyDetailForm.get('cdMethodReceived').value);
    methodLabel =  method ? method.decode : '';
    if (directMethods.includes(methodLabel)) {
      FormUtils.enableFormControlStatus(this.nytdSurveyDetailForm, ['nbrInterviewMinutes', 'indSpecAccomodation']);
    } else {
      this.nytdSurveyDetailForm.controls.nbrInterviewMinutes.setValue(null);
      this.nytdSurveyDetailForm.controls.indSpecAccomodation.setValue(null);
      FormUtils.disableFormControlStatus(this.nytdSurveyDetailForm, ['nbrInterviewMinutes', 'indSpecAccomodation']);
    }
  }

  setAriaValues(event) {
    if(event) {
      event.currentTarget.setAttribute('aria-expanded', 'true');
      const tipId = event.currentTarget.getAttribute('aria-labelledby');
      const ttip = document.getElementById(tipId);
      if(ttip) {
        ttip.style.visibility = 'visible';
      }
    }
  }

  unsetAriaValues(event) {
    if(event) {
      event.currentTarget.setAttribute('aria-expanded', 'false');
      const tipId = event.currentTarget.getAttribute('aria-labelledby');
      const ttip = document.getElementById(tipId);
      if(ttip) {
        ttip.style.visibility = 'hidden';
      }
    }
  }

}