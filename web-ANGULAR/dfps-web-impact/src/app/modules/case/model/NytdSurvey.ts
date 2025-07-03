import { DropDown } from 'dfps-web-lib';

export interface NytdSurveyHeader {
    nytdSurveyHeaderId?: number;
    personId?: number;
    personDOB?: any;
    nytdReportPeriodId?: number;
    cdPopulationType?: string;
    cdSurveyStatus?: string;
    dtSurveyDue?: any;
    lastUpdatedPersonId?: number;
}

export interface NytdSurveyDetail {
    nytdSurveyHeader?: NytdSurveyHeader
    nytdSurveyDetailId?: number;
    nytdSurveyHeaderId?: number;
    eventId?: number;
    indProxy?: string;
    nmProxy?: string;
    dtYouthResponded?: any;
    cdMethodReceived?: string;
    nbrInterviewMinutes?: number;
    indSpecAccomodation?: string;
    cdFullEmploy?: string;
    cdPartEmploy?: string;
    cdSkillsEmploy?: string;
    cdSocialSecurity?: string;
    cdEducAid?: string;
    cdAssistFin?: string;
    cdAssistFood?: string;
    cdAssistHousing?: string;
    cdOtherFinAid?: string;
    cdEducCert?: string;
    cdEducEnroll?: string;
    cdConnAdult?: string;
    cdHomeless?: string;
    cdSubstanceAbuse?: string;
    cdIncarceration?: string;
    cdChildren?: string;
    cdBirthMarriage?: string;
    cdMedicaid?: string;
    cdOtherHealthIns?: string;
    cdInsMedical?: string;
    cdInsMental?: string;
    cdInsPrescription?: string;
    isSubmit?: boolean;
    createdPersonId?: number;
    lastUpdatedPersonId?: number;
}

export interface NytdReportPeriod {
    nytdReportPeriodId?: number;
    nbrFederalYear?: number;
    cdFederalPeriod?: string;
    dtReportStart?: any;
    dtReportEnd?: any;

}

export interface Stage {
    stageId?: number;
    stageName?: string;
}

export interface NytdPopulationList {
    nytdPopulationListId?: number;
    personId?: number;
    nytdReportPeriodId?: number;
    nytdReportPeriod?: NytdReportPeriod;
    caseId?: number;
    stage?: Stage;
    newInd?: string;
    baselineInd?: string;
    followup19Ind?: string;
    followup21Ind?: string;
    servedInd?: string;
}

export interface NytdSurveyDetailRes {
    stageName?: string;
    caseId?: number;
    staffId?: number;
    nytdSurveyHeaderId?: number;
    nytdSurveyDetail?: NytdSurveyDetail;
    nytdPopulationList?: NytdPopulationList;
    contactMethod?: DropDown[];
    yesNoDeclined?: DropDown[];
    highestDegree?: DropDown[];
    yesNoDeclinedDontKnow?: DropDown[];
    pageMode?: string;
}