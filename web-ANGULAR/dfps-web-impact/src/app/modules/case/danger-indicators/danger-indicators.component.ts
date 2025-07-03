import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  NavigationService
} from 'dfps-web-lib';
import { DangerIndicators, DangerIndicatorsRes } from './../model/DangerIndicators';
import { CaseService } from './../service/case.service';
import { DangerIndicatorsValidators } from './danger-indicators.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'danger-indicators',
  templateUrl: './danger-indicators.component.html'
})
export class DangerIndicatorsComponent extends DfpsFormValidationDirective implements OnInit {
  dangerIndicatorsForm: FormGroup;
  yesNo: any;
  caseId: any;
  dangerIndicatorsResponse: DangerIndicatorsRes;
  dangerIndicators?: DangerIndicators;
  isReadOnly = false;
  hideComments = true;


  @ViewChild('errors') errorElement: ElementRef;

  constructor(private formBuilder: FormBuilder,
    private router: Router,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private caseService: CaseService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnInit(): void {
    this.yesNo = [
      { label: 'Yes', value: 'Y' },
      { label: 'No', value: 'N' },
    ];
    this.navigationService.setTitle('Danger Indicators');
    this.helpService.loadHelp('Case');

    this.createForm();
    this.caseService.getDangerIndicators().subscribe((response) => {
      this.dangerIndicatorsResponse = response;
      this.isReadOnly = this.dangerIndicatorsResponse.pageMode === 'VIEW' ? true : false;

      if (this.dangerIndicatorsResponse.dangerIndicators) {
        const dngrIndctrs = this.dangerIndicatorsResponse.dangerIndicators;
        this.dangerIndicatorsForm.setValue({
          indCgSerPhHarm: dngrIndctrs.indCgSerPhHarm,
          indCgSerPhHarmInj: (dngrIndctrs.indCgSerPhHarmInj === 'Y' ? 'true' : null),
          indCgSerPhHarmThr: (dngrIndctrs.indCgSerPhHarmThr === 'Y' ? 'true' : null),
          indCgSerPhHarmPhForce: (dngrIndctrs.indCgSerPhHarmPhForce === 'Y' ? 'true' : null),
          indChSexAbSus: dngrIndctrs.indChSexAbSus,
          indChSexAbSusCg: (dngrIndctrs.indChSexAbSusCg === 'Y' ? 'true' : null),
          indChSexAbSusOh: (dngrIndctrs.indChSexAbSusOh === 'Y' ? 'true' : null),
          indChSexAbSusUnk: (dngrIndctrs.indChSexAbSusUnk === 'Y' ? 'true' : null),
          indCgAwPotHarm: dngrIndctrs.indCgAwPotHarm,
          indCgNoExpForInj: dngrIndctrs.indCgNoExpForInj,
          indCgDnMeetChNeedsFc: dngrIndctrs.indCgDnMeetChNeedsFc,
          indCgDnMeetChNeedsMed: dngrIndctrs.indCgDnMeetChNeedsMed,
          indBadLivConds: dngrIndctrs.indBadLivConds,
          indCgSubAbCantSupCh: dngrIndctrs.indCgSubAbCantSupCh,
          indDomVioDan: dngrIndctrs.indDomVioDan,
          indCgDesChNeg: dngrIndctrs.indCgDesChNeg,
          indCgDisCantSupCh: dngrIndctrs.indCgDisCantSupCh,
          indCgRefAccChToInv: dngrIndctrs.indCgRefAccChToInv,
          indCgPrMalTrtHist: dngrIndctrs.indCgPrMalTrtHist,
          indOtherDangers: dngrIndctrs.indOtherDangers,
          txtOtherDangers: dngrIndctrs.txtOtherDangers == null ? '' : dngrIndctrs.txtOtherDangers,
          cdSftyDcsn: dngrIndctrs.cdSftyDcsn,
          txtComments: dngrIndctrs.txtComments
        });
      }

      this.updateDisplayData();

      this.validatePageMode();
    });
  }

  updateDisplayData() {
    this.populateIndicator1Updated();
    this.populateIndicator2Updated();
    this.populateIndicator14Updated();
    this.safetyDecisionUpdated();
  }

  validatePageMode() {
    if (this.isReadOnly) {
      FormUtils.disableFormControlStatus(this.dangerIndicatorsForm,
        ['indCgSerPhHarmInj', 'indCgSerPhHarmThr', 'indCgSerPhHarmPhForce', 'indChSexAbSus', 'indChSexAbSusCg',
          'indChSexAbSusOh', 'indChSexAbSusUnk', 'indCgAwPotHarm', 'indCgNoExpForInj', 'indCgDnMeetChNeedsFc',
          'indCgDnMeetChNeedsMed', 'indBadLivConds', 'indCgSubAbCantSupCh', 'indDomVioDan', 'indCgDesChNeg',
          'indCgDisCantSupCh', 'indCgRefAccChToInv', 'indCgPrMalTrtHist', 'indOtherDangers', 'txtOtherDangers',
          'cdSftyDcsn', 'txtComments', 'indCgSerPhHarm', 'danIndSave']);
    }
  }

  createForm() {
    this.dangerIndicatorsForm = this.formBuilder.group({
      indCgSerPhHarm: [],
      indCgSerPhHarmInj: [],
      indCgSerPhHarmThr: [],
      indCgSerPhHarmPhForce: [],
      indChSexAbSus: [],
      indChSexAbSusCg: [],
      indChSexAbSusOh: [],
      indChSexAbSusUnk: [],
      indCgAwPotHarm: [],
      indCgNoExpForInj: [],
      indCgDnMeetChNeedsFc: [],
      indCgDnMeetChNeedsMed: [],
      indBadLivConds: [],
      indCgSubAbCantSupCh: [],
      indDomVioDan: [],
      indCgDesChNeg: [],
      indCgDisCantSupCh: [],
      indCgRefAccChToInv: [],
      indCgPrMalTrtHist: [],
      indOtherDangers: [],
      txtOtherDangers: [''],
      cdSftyDcsn: [],
      txtComments: []
    }, {
      validators: [
        DangerIndicatorsValidators.validateDangerIndicators(),
      ]
    });
  }

  save() {
    if (this.validateFormGroup(this.dangerIndicatorsForm)) {
      this.dangerIndicators = {}
      if (this.dangerIndicatorsResponse.dangerIndicators) {
        this.dangerIndicators.id = this.dangerIndicatorsResponse.dangerIndicators.id;
        this.dangerIndicators.caseId = this.dangerIndicatorsResponse.dangerIndicators.caseId;
      }
      this.dangerIndicators.indCgSerPhHarm = this.dangerIndicatorsForm.controls.indCgSerPhHarm.value;
      this.dangerIndicators.indCgSerPhHarmInj = this.dangerIndicatorsForm.controls.indCgSerPhHarmInj.value ? 'Y' : 'N';
      this.dangerIndicators.indCgSerPhHarmThr = this.dangerIndicatorsForm.controls.indCgSerPhHarmThr.value ? 'Y' : 'N';
      this.dangerIndicators.indCgSerPhHarmPhForce =
        this.dangerIndicatorsForm.controls.indCgSerPhHarmPhForce.value ? 'Y' : 'N';
      this.dangerIndicators.indChSexAbSus = this.dangerIndicatorsForm.controls.indChSexAbSus.value;
      this.dangerIndicators.indChSexAbSusCg =
        this.dangerIndicatorsForm.controls.indChSexAbSusCg.value ? 'Y' : 'N';
      this.dangerIndicators.indChSexAbSusOh =
        this.dangerIndicatorsForm.controls.indChSexAbSusOh.value ? 'Y' : 'N';
      this.dangerIndicators.indChSexAbSusUnk =
        this.dangerIndicatorsForm.controls.indChSexAbSusUnk.value ? 'Y' : 'N';
      this.dangerIndicators.indCgAwPotHarm = this.dangerIndicatorsForm.controls.indCgAwPotHarm.value;
      this.dangerIndicators.indCgNoExpForInj = this.dangerIndicatorsForm.controls.indCgNoExpForInj.value;
      this.dangerIndicators.indCgDnMeetChNeedsFc =
        this.dangerIndicatorsForm.controls.indCgDnMeetChNeedsFc.value;
      this.dangerIndicators.indCgDnMeetChNeedsMed =
        this.dangerIndicatorsForm.controls.indCgDnMeetChNeedsMed.value;
      this.dangerIndicators.indBadLivConds = this.dangerIndicatorsForm.controls.indBadLivConds.value;
      this.dangerIndicators.indCgSubAbCantSupCh =
        this.dangerIndicatorsForm.controls.indCgSubAbCantSupCh.value;
      this.dangerIndicators.indDomVioDan = this.dangerIndicatorsForm.controls.indDomVioDan.value;
      this.dangerIndicators.indCgDesChNeg = this.dangerIndicatorsForm.controls.indCgDesChNeg.value;
      this.dangerIndicators.indCgDisCantSupCh = this.dangerIndicatorsForm.controls.indCgDisCantSupCh.value;
      this.dangerIndicators.indCgRefAccChToInv = this.dangerIndicatorsForm.controls.indCgRefAccChToInv.value;
      this.dangerIndicators.indCgPrMalTrtHist = this.dangerIndicatorsForm.controls.indCgPrMalTrtHist.value;
      this.dangerIndicators.indOtherDangers = this.dangerIndicatorsForm.controls.indOtherDangers.value;
      this.dangerIndicators.txtOtherDangers = this.dangerIndicatorsForm.controls.txtOtherDangers.value;
      this.dangerIndicators.cdSftyDcsn = this.dangerIndicatorsForm.controls.cdSftyDcsn.value;
      this.dangerIndicators.txtComments = this.dangerIndicatorsForm.controls.txtComments.value;
      this.caseService.saveDangerIndicators(this.dangerIndicators)
        .subscribe((result) => {
          if (result) {
            setTimeout(() => {
              if (this.dangerIndicators.cdSftyDcsn === 'SWP') {
                this.caseService.redirectToUrl('case/case-management/maintenance/safety-plans');
              } else {
                window.location.reload();
              }
            }, 3000);
          }
        });
    }
  }

  indicator1Updated() {
    if (this.isReadOnly) {
      FormUtils.disableFormControlStatus(this.dangerIndicatorsForm, ['danIndSave']);
    } else {
      this.populateIndicator1Updated();
    }
  }

  populateIndicator1Updated() {
      if (this.dangerIndicatorsForm.get('indCgSerPhHarm').value === 'N') {
        this.dangerIndicatorsForm.controls.indCgSerPhHarmInj.setValue(null);
        this.dangerIndicatorsForm.controls.indCgSerPhHarmThr.setValue(null);
        this.dangerIndicatorsForm.controls.indCgSerPhHarmPhForce.setValue(null);
        FormUtils.disableFormControlStatus(this.dangerIndicatorsForm, ['indCgSerPhHarmInj', 'indCgSerPhHarmThr', 'indCgSerPhHarmPhForce']);
      } else {
        FormUtils.enableFormControlStatus(this.dangerIndicatorsForm, ['indCgSerPhHarmInj', 'indCgSerPhHarmThr', 'indCgSerPhHarmPhForce']);
      }
  }

  indicator2Updated() {
    if (!this.isReadOnly) {
      this.populateIndicator2Updated();
    }
  }


  populateIndicator2Updated() {
    if (this.dangerIndicatorsForm.get('indChSexAbSus').value === 'N') {
      this.dangerIndicatorsForm.controls.indChSexAbSusCg.setValue(null);
      this.dangerIndicatorsForm.controls.indChSexAbSusOh.setValue(null);
      this.dangerIndicatorsForm.controls.indChSexAbSusUnk.setValue(null);
      FormUtils.disableFormControlStatus(this.dangerIndicatorsForm, ['indChSexAbSusCg', 'indChSexAbSusOh', 'indChSexAbSusUnk']);
    } else {
      FormUtils.enableFormControlStatus(this.dangerIndicatorsForm, ['indChSexAbSusCg', 'indChSexAbSusOh', 'indChSexAbSusUnk']);
    }
  }

  indicator14Updated() {
    if (!this.isReadOnly) {
      this.populateIndicator14Updated();
    }
  }

  populateIndicator14Updated() {
    if (this.dangerIndicatorsForm.get('indOtherDangers').value === 'N') {
      this.dangerIndicatorsForm.controls.txtOtherDangers.setValue(null);
      FormUtils.disableFormControlStatus(this.dangerIndicatorsForm, ['txtOtherDangers']);
    } else {
      FormUtils.enableFormControlStatus(this.dangerIndicatorsForm, ['txtOtherDangers']);
    }
  }

  safetyDecisionUpdated() {
    if (this.dangerIndicatorsForm.controls.cdSftyDcsn.value !== 'USF') {
      this.dangerIndicatorsForm.controls.txtComments.setValue(null);
      this.hideComments = true;
    } else {
      this.hideComments = false;
    }
  }

}
