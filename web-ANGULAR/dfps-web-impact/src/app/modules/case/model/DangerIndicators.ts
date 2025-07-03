import { DropDown } from 'dfps-web-lib';

export interface DangerIndicators {
    id?: number;
    caseId?: number;
    indCgSerPhHarm?: string;
    indCgSerPhHarmInj?: string;
    indCgSerPhHarmThr?: string;
    indCgSerPhHarmPhForce?: string;
    indChSexAbSus?: string;
    indChSexAbSusCg?: string;
    indChSexAbSusOh?: string;
    indChSexAbSusUnk?: string;
    indCgAwPotHarm?: string;
    indCgNoExpForInj?: string;
    indCgDnMeetChNeedsFc?: string;
    indCgDnMeetChNeedsMed?: string;
    indBadLivConds?: string;
    indCgSubAbCantSupCh?: string;
    indDomVioDan?: string;
    indCgDesChNeg?: string;
    indCgDisCantSupCh?: string;
    indCgRefAccChToInv?: string;
    indCgPrMalTrtHist?: string;
    indOtherDangers?: string;
    txtOtherDangers?: string;
    cdSftyDcsn?: string;
    txtComments?: string;
    createdDate?: string;
    lastUpdatedDate?: string;
    createdPerson?: string;
    lastUpdatedPerson?: string;
}


export interface DangerIndicatorsRes {
    caseId?: number;
    stageName?: string;
    dangerIndicators?: DangerIndicators;
    safetyDecisions?: DropDown[];
    pageMode?: string;
}