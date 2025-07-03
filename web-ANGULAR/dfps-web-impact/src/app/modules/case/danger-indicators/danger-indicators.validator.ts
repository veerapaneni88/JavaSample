import { FormGroup } from '@angular/forms';

export class DangerIndicatorsValidators {

    static validateDangerIndicators() {
        return (group: FormGroup): { [key: string]: any } => {
            if (group.controls.indCgSerPhHarm.value == null) {
                group.controls.indCgSerPhHarm.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 1', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else if (group.controls.indCgSerPhHarm.value === 'Y' &&
                (group.controls.indCgSerPhHarmInj.value == null || group.controls.indCgSerPhHarmInj.value == false)&&
                (group.controls.indCgSerPhHarmThr.value == null || group.controls.indCgSerPhHarmThr.value == false) &&
                (group.controls.indCgSerPhHarmPhForce.value == null || group.controls.indCgSerPhHarmPhForce.value == false)) {
                group.controls.indCgSerPhHarmInj.setErrors({
                    dangerIndicatorCBRequired: {
                        customFieldName: 'Danger Indicator 1',
                        dangerIndicatorCBRequired: true
                    }
                });
            } else {
                group.controls.indCgSerPhHarm.setErrors(null);
                group.controls.indCgSerPhHarmInj.setErrors(null);
                group.controls.indCgSerPhHarmThr.setErrors(null);
                group.controls.indCgSerPhHarmPhForce.setErrors(null);
            }

            if (group.controls.indChSexAbSus.value == null) {
                group.controls.indChSexAbSus.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 2', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else if (group.controls.indChSexAbSus.value === 'Y' &&
              (group.controls.indChSexAbSusCg.value == null  || group.controls.indChSexAbSusCg.value == false)&&
              (group.controls.indChSexAbSusOh.value == null  || group.controls.indChSexAbSusOh.value == false) &&
              (group.controls.indChSexAbSusUnk.value == null || group.controls.indChSexAbSusUnk.value == false)) {
                group.controls.indChSexAbSusCg.setErrors({
                    dangerIndicatorCBRequired: {
                        customFieldName: 'Danger Indicator 2',
                        dangerIndicatorCBRequired: true
                    }
                });
            } else {
                group.controls.indChSexAbSus.setErrors(null);
                group.controls.indChSexAbSusCg.setErrors(null);
                group.controls.indChSexAbSusOh.setErrors(null);
                group.controls.indChSexAbSusUnk.setErrors(null);
            }

            if (group.controls.indCgAwPotHarm.value == null) {
                group.controls.indCgAwPotHarm.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 3', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgAwPotHarm.setErrors(null);
            }
            if (group.controls.indCgNoExpForInj.value == null) {
                group.controls.indCgNoExpForInj.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 4', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgNoExpForInj.setErrors(null);
            }
            if (group.controls.indCgDnMeetChNeedsFc.value == null) {
                group.controls.indCgDnMeetChNeedsFc.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 5', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgDnMeetChNeedsFc.setErrors(null);
            }
            if (group.controls.indCgDnMeetChNeedsMed.value == null) {
                group.controls.indCgDnMeetChNeedsMed.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 6', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgDnMeetChNeedsMed.setErrors(null);
            }
            if (group.controls.indBadLivConds.value == null) {
                group.controls.indBadLivConds.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 7', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indBadLivConds.setErrors(null);
            }
            if (group.controls.indCgSubAbCantSupCh.value == null) {
                group.controls.indCgSubAbCantSupCh.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 8', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgSubAbCantSupCh.setErrors(null);
            }
            if (group.controls.indDomVioDan.value == null) {
                group.controls.indDomVioDan.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 9', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indDomVioDan.setErrors(null);
            }
            if (group.controls.indCgDesChNeg.value == null) {
                group.controls.indCgDesChNeg.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 10', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgDesChNeg.setErrors(null);
            }
            if (group.controls.indCgDisCantSupCh.value == null) {
                group.controls.indCgDisCantSupCh.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 11', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgDisCantSupCh.setErrors(null);
            }
            if (group.controls.indCgRefAccChToInv.value == null) {
                group.controls.indCgRefAccChToInv.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 12', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgRefAccChToInv.setErrors(null);
            }
            if (group.controls.indCgPrMalTrtHist.value == null) {
                group.controls.indCgPrMalTrtHist.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 13', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else {
                group.controls.indCgPrMalTrtHist.setErrors(null);
            }
            if (group.controls.indOtherDangers.value == null) {
                group.controls.indOtherDangers.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Danger Indicator 14', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else if (group.controls.indOtherDangers.value ==='Y' && 
                        (group.controls.txtOtherDangers.value == null || 
                        group.controls.txtOtherDangers.value.trim().length === 0)) {
                group.controls.txtOtherDangers.setErrors({ 
                    dangerIndicatorTBRequired: { 
                        customFieldName: 'Danger Indicator 14', 
                        dangerIndicatorTBRequired: true 
                    } 
                });
            } else {
                group.controls.indOtherDangers.setErrors(null);
                group.controls.txtOtherDangers.setErrors(null);
            }

            if (group.controls.cdSftyDcsn.value == null) {
                group.controls.cdSftyDcsn.setErrors({ 
                    dangerIndicatorRequired: { 
                        customFieldName: 'Safety Decision', 
                        dangerIndicatorRequired: true 
                    } 
                });
            } else if (group.controls.cdSftyDcsn.value === 'USF' && 
                        (group.controls.txtComments.value == null || 
                        group.controls.txtComments.value.trim().length === 0)) {
                group.controls.txtComments.setErrors({ 
                    dangerIndicatorTBRequired: { 
                        customFieldName: 'Comments', 
                        dangerIndicatorTBRequired: true 
                    } 
                });
            } else {
                group.controls.cdSftyDcsn.setErrors(null);
                group.controls.txtComments.setErrors(null);
            }

            return null;
        };
    }
}
