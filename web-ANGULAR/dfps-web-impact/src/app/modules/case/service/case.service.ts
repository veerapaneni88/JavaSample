import { Injectable, LOCALE_ID, Inject } from '@angular/core';
import { PlacementLogRes } from '@case/model/placement';
import { ResourceHistoryDetailRes, ResourceHistoryRes } from '@case/model/resourcehistory';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { tap, map } from 'rxjs/operators';
import { formatDate } from '@angular/common';
import {
    AdminReviewResponse,
    DisplayHomeInformation,
    HomeInformationResponse,
    MonthlyRequestExtension,
    PaymentInformationRes,
    ResourceAddress
} from '../model/case';
import { DisplayHomeAssessmentListAdd } from '../model/homeAssessment';
import { DisplayHomeAssessmentList } from '../model/homeAssessmentList';
import { DisplayHomeAssessmentDetail } from '../model/homeAssessmentDetail';
import { DisplayApsSafetyAssessmentDetails } from '@case/model/apsSafetyAssessmentList';
import { DisplayApsServicePlanDetails } from '@case/model/apsServicePlanDetailsList';
import { DangerIndicators, DangerIndicatorsRes } from '../model/DangerIndicators';
import { SafetyPlanListRes, SafetyPlanRes, SafetyPlan } from '../model/SafetyPlan';
import { InjuryDetail, InjuryDetailDisplayRes, InjuryListRes } from '../model/InjuryDetail';
import { AllegationDetailRes } from './../model/Allegation';
import { Router } from '@angular/router';
import { InvestigationLetterListRes, InvestigationLetterRes, Person } from './../model/InvestigationLetter';
import { ServicePackage, ServicePackageListRes, ServicePackageRes } from './../model/servicePackage';
import { NytdSurveyDetail, NytdSurveyDetailRes } from '../model/NytdSurvey';
import { EleKinAssessmentListRes, KinAssessmentRes, KinAssessmentDetail } from './../model/kinHomeAssessment';



@Injectable()
export class CaseService {
    private formData: any;
    private returnUrl: string;
    readonly HOME_INFORMATION_URL = '/v1/home-information';
    readonly HOME_INFORMATION_SAVE_URL = '/v1/home-information/save';
    readonly PLACEMENT_LOG_URL = '/v1/placement-log';
    readonly SESSION_URL = '/v1/session/';
    readonly RESOURCE_HISTORY_URL = '/v1/resource-history/';
    readonly RESOURCE_URL: string = '/v1/resource';
    readonly ADMIN_REVIEW_SAVE_URL = '/v1/admin-review/save';
    readonly ADMIN_REVIEW_SAVE_AND_CLOSE_URL = '/v1/admin-review/close';
    readonly HOME_INFORMATION_FAD_SAVE_URL = '/v1/home-information/save';
    readonly HOME_INFORMATION_FAD_SUBMIT_URL = '/v1/home-information/submit';
    readonly HOME_INFORMATION_FAD_ASSIGN_URL = '/v1/home-information/assign';
    readonly KIN_HOME_INFORMATION_URL = '/v1/kin-home-information';
    readonly KIN_PAYMENT_INFORMATION_URL = '/v1/resources';
    readonly HOME_INFORMATION_KIN_SAVE_URL = '/v1/kin-home-information/save';
    readonly HOME_INFORMATION_KIN_SUBMIT_URL = '/v1/kin-home-information/submit';
    readonly HOME_INFORMATION_KIN_ASSIGN_URL = '/v1/kin-home-information/assign';
    readonly HOME_ASSESSMENT_URL: string = '/v1/home-assessment';
    readonly HOME_ASSESSMENT_KIN_SAVE_URL = '/v1/home-assessment/save';
    readonly HOME_ASSESSMENT_KIN_SUBMIT_URL = '/v1/home-assessment/submit';
    readonly APS_SNA_DETAILS = '/v1/aps-sna';
    readonly APS_SNA_SAVE = '/v1/aps-sna/save';
    readonly APS_INV_CONCLUSION = '/v1/aps-case-management/display-investigation-conclusion';
    readonly DANGER_INDICATORS_URL: string = '/v1/danger-indicators';
    readonly SAFETY_PLAN_URL: string = '/v1/safety-plan';
    readonly INJURY_DETAILS_URL: string = '/v1/injury-details';
    readonly APS_SNA_SAVE_COMPLETE = '/v1/aps-sna/save-and-complete';
    readonly APS_INV_CONCLUSION_SAVE = '/v1/aps-case-management/save-investigation-conclusion';
    readonly APS_INV_CONCLUSION_SAVE_SUBMIT = '/v1/aps-case-management/save-submit-investigation-conclusion';
    readonly APS_INV_CONCLUSION_SEND_FOR_REVIEW = '/v1/aps-case-management/send-for-review-investigation-conclusion';
    readonly APS_SAFETY_ASSESSMENT_DETAILS = '/v1/aps-safety-assessment';
    readonly APS_SAFETY_ASSESSMENT_SAVE = '/v1/aps-safety-assessment/save';
    readonly OUTCOME_MATRIX = '/investigation/outcomeMatrix/displayOutcomeMatrix';
    readonly APS_CARE_DETAILS = '/v1/aps-care';
    readonly APS_RORA = '/v1/apsRora/displayApsRora';
    readonly APS_RORA_SAVE = '/v1/apsRora/save';
    readonly APS_RORA_SAVEANDCOMPLETE = '/v1/apsRora/saveAndComplete';
    readonly SP_LANDING_PAGE = '/v1/servicePlan/displayServicePlan';
    readonly APS_GUARDIANSHIP = '/v1/Guardianship-Detail';
    readonly SP_UPDATE_MULTIPLE_PROBLEMS = '/v1/servicePlan/problem/update';
    readonly SP_UPDATE_MULTIPLE_ACTIONS = '/v1/servicePlan/action/update';
    readonly APS_II_ADD_PRBLM = '/v1/servicePlan/problem/add';
    readonly APS_II_REMOVE_PRBLM = '/v1/servicePlan/problem/remove';
    readonly APS_II_REMOVE_ACTN = '/v1/servicePlan/action/remove';
    readonly APS_II_ADD_ACTN = '/v1/servicePlan/action/add';
    readonly SP_CONTACT_STANDARD_DETAILS = '/v1/servicePlan/displayContactStandardDetail';
    readonly APS_SP_SAVE = '/v1/servicePlan/saveShieldServicePlan';
    readonly APPROVAL_STATUS = '/v1/aps-case-management/approval-status';
    readonly APS_PRE_SHIELD_SERVICE_PLAN = '/v1/preServicePlans/getApsPreServicePlans';
    readonly APS_SP_CANCEL = '/v1/servicePlan/multiUpdateCancel';
    readonly INVESTIGATION_LETTER_URL = '/v1/investigation-letter';
    readonly PERSON_URL = '/v1/person';
    // Admin Review URLs
    readonly ADMIN_REVIEW_URL: string = '/v1/admin-review';

    readonly NYTD_SURVEY_DETAIL_URL: string = '/v1/nytd-survey-detail';

    // ADO-PAD stage URLs
    readonly ADO_APP_DISPLAY = '/v1/adoption-assistance-application';

    private addressListRes: ResourceAddress[] = [];

    constructor(
        private cookieService: CookieService,
        private apiService: ApiService,
        private router: Router,
        private globalMessageService: GlobalMessageServcie,
        @Inject(LOCALE_ID) private locale: string
    ) {}

    redirectToUrl(redirectUrl: string) {
        this.globalMessageService.clearPaths();
        this.router.navigate([redirectUrl]);
    }

    getUserData(): Observable<any> {
        return this.apiService.get(this.SESSION_URL + this.cookieService.get('impactUserSession'));
    }

    getAdminReviewData(): Observable<any> {
        return this.apiService.get(this.ADMIN_REVIEW_URL);
    }

    displayHomeInformation(resourceId: any): Observable<DisplayHomeInformation> {
        return this.apiService.get(this.HOME_INFORMATION_URL + '/' + resourceId).pipe(
            map((res: DisplayHomeInformation) => {
                if (res && res.resource) {
                    if (res.resource.marriageDate) {
                        res.resource.marriageDate = formatDate(
                            res.resource.marriageDate.split(' ')[0],
                            'MM/dd/yyyy',
                            this.locale
                        );
                    }
                }
                return res;
            })
        );
    }

    saveHomeInformationFad(
        fadHomeInfo: any,
        isSubmit: boolean,
        isAssign: boolean
    ): Observable<HomeInformationResponse> {
        let url = this.HOME_INFORMATION_FAD_SAVE_URL;
        if (isAssign) {
            url = this.HOME_INFORMATION_FAD_ASSIGN_URL;
        } else if (isSubmit) {
            url = this.HOME_INFORMATION_FAD_SUBMIT_URL;
        }
        this.globalMessageService.addSuccessPath(url);
        return this.apiService.post(url, fadHomeInfo);
    }

    saveAdminReview(adminReviewInfo: any, isSaveAndClose: boolean): Observable<any> {
        let url = '';
        if (isSaveAndClose) {
            url = this.ADMIN_REVIEW_SAVE_AND_CLOSE_URL;
        } else {
            url = this.ADMIN_REVIEW_SAVE_URL;
        }
        this.globalMessageService.addSuccessPath(url);
        return this.apiService.post(url, adminReviewInfo);
    }

    deleteAddressList(resourceId: number, addressId: number): Observable<any> {
        return this.apiService.delete(this.RESOURCE_URL + '/' + resourceId + '/address/' + addressId);
    }

    getAddressList(resourceId: number, addressId: number): Observable<any> {
        return this.apiService.get(this.RESOURCE_URL + '/' + resourceId + '/address/' + addressId + '/display').pipe(
            tap((res) => {
                this.addressListRes = res;
            })
        );
    }

    saveAddressDetail(resourceId: number, addressDetail: any): Observable<any> {
        return this.apiService.post(this.RESOURCE_URL + '/' + resourceId + '/address', addressDetail);
    }

    deletePhoneList(resourceId: number, resourcePhoneId: number): Observable<any> {
        return this.apiService.delete(this.RESOURCE_URL + '/' + resourceId + '/phone/' + resourcePhoneId);
    }

    getPhoneList(resourceId: number, resourcePhoneId: number): Observable<any> {
        return this.apiService.get(this.RESOURCE_URL + '/' + resourceId + '/phone/' + resourcePhoneId + '/display');
    }

    savePhoneDetail(resourceId: number, phoneDetail: any): Observable<any> {
        return this.apiService.post(this.RESOURCE_URL + '/' + resourceId + '/phone', phoneDetail);
    }

    getHomeInformation(): Observable<DisplayHomeInformation> {
        return this.apiService.get(this.HOME_INFORMATION_URL);
    }

    saveHomeInformation(): Observable<any> {
        this.globalMessageService.addSuccessPath(this.HOME_INFORMATION_SAVE_URL);
        return this.apiService.post(this.HOME_INFORMATION_SAVE_URL, null);
    }

    gePlacementLog(): Observable<PlacementLogRes> {
        return this.apiService.get(this.PLACEMENT_LOG_URL);
    }

    getResourceHistory(resourceId: string): Observable<ResourceHistoryRes> {
        return this.apiService.get('/v1/resource/' + resourceId + '/resource-history');
    }

    deleteResourceHistory(resourceId: string, resourceHistoryId: string): Observable<any> {
        return this.apiService.delete('/v1/resource/' + resourceId + '/resource-history/' + resourceHistoryId);
    }

    getResourceHistoryDetail(resourceId: string, resourceHistoryId): Observable<ResourceHistoryDetailRes> {
        return this.apiService.get('/v1/resource/' + resourceId + '/resource-history/' + resourceHistoryId);
    }

    saveResourceHistoryDetail(resourceId: string, resourceHistoryDto): Observable<any> {
        this.globalMessageService.addSuccessPath('/v1/resource/' + resourceId + '/resource-history/');
        return this.apiService.post('/v1/resource/' + resourceId + '/resource-history/', resourceHistoryDto);
    }

    displayKinHomeInformation(resourceId: any): Observable<DisplayHomeInformation> {
        return this.apiService.get(this.KIN_HOME_INFORMATION_URL + '/' + resourceId).pipe(
            map((res: DisplayHomeInformation) => {
                if (res && res.resource) {
                    if (
                        res.resource.agreementSignedDate &&
                        (res.resource.agreementSignedDate === '12/31/3500' ||
                            res.resource.agreementSignedDate === '12/31/4712')
                    ) {
                        res.resource.agreementSignedDate = null;
                    }
                    if (
                        (res.resource.courtOrderedPaymentDate &&
                            res.resource.courtOrderedPaymentDate === '12/31/3500') ||
                        res.resource.courtOrderedPaymentDate === '12/31/4712'
                    ) {
                        res.resource.courtOrderedPaymentDate = null;
                    }
                    if (
                        (res.resource.courtOrderedPlacementDate &&
                            res.resource.courtOrderedPlacementDate === '12/31/3500') ||
                        res.resource.courtOrderedPlacementDate === '12/31/4712'
                    ) {
                        res.resource.courtOrderedPlacementDate = null;
                    }
                }
                if (res && res.careEligibilityHistories) {
                    res.careEligibilityHistories.forEach((history) => {
                        if (
                            (history.assessmentApproveDate && history.assessmentApproveDate === '12/31/3500') ||
                            history.assessmentApproveDate === '12/31/4712'
                        ) {
                            history.assessmentApproveDate = null;
                        }
                        if (
                            (history.courtOrderedPlacementDate && history.courtOrderedPlacementDate === '12/31/3500') ||
                            history.courtOrderedPlacementDate === '12/31/4712'
                        ) {
                            history.courtOrderedPlacementDate = null;
                        }
                    });
                }
                if (res && res.paymentEligibilityHistories) {
                    res.paymentEligibilityHistories.forEach((payment) => {
                        if (payment.paymentEligibilityStatusCode && payment.paymentEligibilityStatusCode === 'Y') {
                            payment.paymentEligibilityStatus = true;
                        }
                    });
                }
                return res;
            })
        );
    }

    getKinPaymentInfo(resourceId: any): Observable<PaymentInformationRes> {
        return this.apiService.get(this.KIN_PAYMENT_INFORMATION_URL + '/' + resourceId + '/display');
    }

    requestPaymentExtension(resourceId: any, monthlyExtension: any): Observable<MonthlyRequestExtension> {
        return this.apiService.post(
            this.KIN_PAYMENT_INFORMATION_URL + '/' + resourceId + '/request-extension',
            monthlyExtension
        );
    }

    saveKinHomeInformation(
        kinHomeInfo: any,
        isSubmit: boolean,
        isAssign: boolean
    ): Observable<HomeInformationResponse> {
        let url = this.HOME_INFORMATION_KIN_SAVE_URL;
        if (isAssign) {
            url = this.HOME_INFORMATION_KIN_ASSIGN_URL;
        } else if (isSubmit) {
            url = this.HOME_INFORMATION_KIN_SUBMIT_URL;
        }
        this.globalMessageService.addSuccessPath(url);
        return this.apiService.post(url, kinHomeInfo);
    }

    getKinHomeAssessmentListAdd(): Observable<DisplayHomeAssessmentListAdd> {
        return this.apiService.get(this.HOME_ASSESSMENT_URL + '/narratives');
    }

    getKinHomeAssessmentList(): Observable<DisplayHomeAssessmentList> {
        return this.apiService.get(this.HOME_ASSESSMENT_URL + '/event-list');
    }

    saveKinHomeAssessmentEvent(homeAssessmentEvent): Observable<any> {
        return this.apiService.post(this.HOME_ASSESSMENT_URL + '/create-event', homeAssessmentEvent);
    }

    getKinHomeAssessmentDetail(eventId: any): Observable<DisplayHomeAssessmentDetail> {
        return this.apiService.get(this.HOME_ASSESSMENT_URL + '?eventId=' + eventId);
    }

    saveKinHomeAssessmentDetail(kinHomeAssessment: any, isSubmit: boolean): Observable<DisplayHomeAssessmentDetail> {
        let url = this.HOME_ASSESSMENT_KIN_SAVE_URL;
        if (isSubmit) {
            url = this.HOME_ASSESSMENT_KIN_SUBMIT_URL;
        }
        this.globalMessageService.addSuccessPath(url);
        return this.apiService.post(url, kinHomeAssessment);
    }

    deleteKinHomeAssessmentDetail(eventId: number): Observable<DisplayHomeAssessmentDetail> {
        return this.apiService.delete(this.HOME_ASSESSMENT_URL + '/' + eventId);
    }

    getDangerIndicators(): Observable<DangerIndicatorsRes> {
        return this.apiService.get(this.DANGER_INDICATORS_URL);
    }

    saveDangerIndicators(dangerIndicators: DangerIndicators): Observable<any> {
        this.globalMessageService.addSuccessPath(this.DANGER_INDICATORS_URL);
        return this.apiService.post(this.DANGER_INDICATORS_URL, dangerIndicators);
    }

    displayInjuryDetails(): Observable<InjuryListRes> {
        return this.apiService.get('/v1/injury-details/').pipe();
    }

    displayInjuryDetail(injuryId: any): Observable<InjuryDetailDisplayRes> {
        return this.apiService.get('/v1/injury-details/' + injuryId).pipe();
    }

    displayInjuryDetailForEvent(eventId: number): Observable<InjuryDetailDisplayRes> {
        return this.apiService.get('/v1/injury-details/event/' + eventId);
    }

    saveInjuryDetail(injuryDetail: InjuryDetail): Observable<any> {
        this.globalMessageService.addSuccessPath('/v1/injury-details/');
        return this.apiService.post('/v1/injury-details/', injuryDetail);
    }

    getAllegations(): Observable<AllegationDetailRes> {
        return this.apiService.get('/v1/allegations').pipe();
    }

    displaySafetyPlans(): Observable<SafetyPlanListRes> {
        return this.apiService.get(this.SAFETY_PLAN_URL);
    }

    getSafetyPlanDetail(safetyPlanId: number): Observable<SafetyPlanRes> {
        return this.apiService.get(this.SAFETY_PLAN_URL + '/' + safetyPlanId);
    }

    getSafetyPlanDetailFromEvent(eventId: number): Observable<SafetyPlanRes> {
        return this.apiService.get(this.SAFETY_PLAN_URL + '/event/' + eventId);
    }

    saveSafetyPlan(safetyPlan: SafetyPlan): Observable<any> {
        this.globalMessageService.addSuccessPath(this.SAFETY_PLAN_URL);
        return this.apiService.post(this.SAFETY_PLAN_URL, safetyPlan);
    }

    deleteSafetyPlans(selectSafetyPlanIds: any[]) {
        return this.apiService.post(this.SAFETY_PLAN_URL + '/delete', selectSafetyPlanIds);
    }

    deleteInjuryDetails(selectInjuryIds: any[]) {
        return this.apiService.post('/v1/injury-details/delete', selectInjuryIds);
    }

    downloadSafetyPlan(safetyPlanId: any): Observable<Blob> {
        return this.apiService.download(this.SAFETY_PLAN_URL + '/' + safetyPlanId + '/download');
    }

    formatPhoneNumber(phoneNumberString: string): string {
        const cleaned = ('' + phoneNumberString).replace(/\D/g, '');
        const match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);
        if (match) {
            return '(' + match[1] + ') ' + match[2] + '-' + match[3];
        }
        return null;
    }

    getApsSNADetails(eventId: any) {
        return this.apiService.get(this.APS_SNA_DETAILS + '?eventId=' + eventId);
    }

    getApsSafetyAssmntDetails(eventId: number): Observable<DisplayApsSafetyAssessmentDetails> {
        return this.apiService.get(this.APS_SAFETY_ASSESSMENT_DETAILS + '?eventId=' + eventId);
    }

    getApsCareDetails() {
        return this.apiService.get(this.APS_CARE_DETAILS);
    }

    saveSafetyAssmntDetails(reqObj): Observable<any> {
        this.globalMessageService.addSuccessPath(this.APS_SAFETY_ASSESSMENT_SAVE);
        return this.apiService.post(this.APS_SAFETY_ASSESSMENT_SAVE, reqObj);
    }

    getApsServicePlanDetails(): Observable<DisplayApsServicePlanDetails> {
        return this.apiService.get(this.SP_LANDING_PAGE);
    }

    saveApsSNADetails(reqObj): Observable<DisplayApsServicePlanDetails> {
        this.globalMessageService.addSuccessPath(this.APS_SNA_SAVE);
        return this.apiService.post(this.APS_SNA_SAVE, reqObj);
    }

    getApsInvestigationConclusion(): Observable<any> {
        return this.apiService.get(this.APS_INV_CONCLUSION);
    }

    saveAndSubmitApsSNADetails(reqObj) {
        this.globalMessageService.addSuccessPath(this.APS_SNA_SAVE_COMPLETE);
        return this.apiService.post(this.APS_SNA_SAVE_COMPLETE, reqObj);
    }

    saveApsInvestigationConclusion(reqObj) {
        return this.apiService.post(this.APS_INV_CONCLUSION_SAVE, reqObj);
    }

    saveandSubmitApsInvestigationConclusion(reqObj) {
        return this.apiService.post(this.APS_INV_CONCLUSION_SAVE_SUBMIT, reqObj);
    }

    sendForReviewApsInvestigationConclusion(reqObj) {
        return this.apiService.post(this.APS_INV_CONCLUSION_SEND_FOR_REVIEW, reqObj);
    }

    getOutcomeMatrix(): Observable<any> {
        return this.apiService.get(this.OUTCOME_MATRIX);
    }

    getOutcomeMatrixDetails(outcomeMatrixId: string): Observable<any> {
        return this.apiService.get(`${this.OUTCOME_MATRIX}?outcomeMatrixId=${outcomeMatrixId}`);
    }

    getApsRora(cdTask: any, isSystemSupervisor: any): Observable<any> {
        return this.apiService.get(`${this.APS_RORA}?cdTask=${cdTask}&isSystemSupervisor=${isSystemSupervisor}`);
    }

    saveApsRora(payload: any): Observable<any> {
        return this.apiService.post(`${this.APS_RORA_SAVE}`, payload);
    }

    saveAndCompleteApsRora(payload: any): Observable<any> {
        return this.apiService.post(`${this.APS_RORA_SAVEANDCOMPLETE}`, payload);
    }

    getSPLanding(): Observable<any> {
        return this.apiService.get(this.SP_LANDING_PAGE);
    }

    getApsGuardianshipDetail(eventId: any, guardianType: any, guardianshipType: any): Observable<any> {
        return this.apiService.get(
            `${this.APS_GUARDIANSHIP}?eventId=${eventId}&guardianType=${guardianType}&guardianshipType=${guardianshipType}`
        );
    }

    saveGuardianshipDetails(payload: any): Observable<any> {
        return this.apiService.post(`${this.APS_GUARDIANSHIP}/save`, payload);
    }

    getSPContactStandardDetails(apsSpMonitoringPlanId: any): Observable<any> {
        return this.apiService.get(
            `${this.SP_CONTACT_STANDARD_DETAILS}?apsSpMonitoringPlanId=${apsSpMonitoringPlanId}`
        );
    }

    addPrblm(reqPayload: any): Observable<any> {
        this.globalMessageService.addSuccessPath(this.APS_II_ADD_PRBLM);
        return this.apiService.post(this.APS_II_ADD_PRBLM, reqPayload);
    }

    removePrblm(reqPayload: any): Observable<any> {
        this.globalMessageService.addSuccessPath(this.APS_II_REMOVE_PRBLM);
        return this.apiService.post(this.APS_II_REMOVE_PRBLM, reqPayload);
    }

    addActn(reqPayload: any): Observable<any> {
        return this.apiService.post(this.APS_II_ADD_ACTN, reqPayload);
    }

    removeActn(reqPayload: any): Observable<any> {
        this.globalMessageService.addSuccessPath(this.APS_II_REMOVE_ACTN);
        return this.apiService.post(this.APS_II_REMOVE_ACTN, reqPayload);
    }

    updateMultiProblrmsInServicePlanDto(reqPayload: any): Observable<any> {
        this.globalMessageService.addSuccessPath(this.SP_UPDATE_MULTIPLE_PROBLEMS);
        return this.apiService.post(this.SP_UPDATE_MULTIPLE_PROBLEMS, reqPayload);
    }

    updateMultiActionsInServicePlanDto(reqPayload: any): Observable<any> {
        this.globalMessageService.addSuccessPath(this.SP_UPDATE_MULTIPLE_ACTIONS);
        return this.apiService.post(this.SP_UPDATE_MULTIPLE_ACTIONS, reqPayload);
    }

    saveAPSServiceplanDetails(reqPayload: any): Observable<any> {
        this.globalMessageService.addSuccessPath(this.APS_SP_SAVE);
        return this.apiService.post(this.APS_SP_SAVE, reqPayload);
    }

    getPreShieldServicePlan() {
        return this.apiService.get(this.APS_PRE_SHIELD_SERVICE_PLAN);
    }

    getFormData() {
        return this.formData;
    }

    setFormData(formData: any) {
        this.formData = formData;
    }

    getReturnUrl() {
        return this.returnUrl;
    }

    setReturnUrl(returnUrl: any) {
        this.returnUrl = returnUrl;
    }

    approvalStatus(reqObj) {
        return this.apiService.post(this.APPROVAL_STATUS, reqObj);
    }

    cancelUpdatePrblmOrActn(reqPayload: any): Observable<any> {
        return this.apiService.post(this.APS_SP_CANCEL, reqPayload);
    }
    getInvestigationLetterList(): Observable<InvestigationLetterListRes> {
        return this.apiService.get(this.INVESTIGATION_LETTER_URL);
    }

    deleteInvestigationLetter(investigationLetterId: any) {
        return this.apiService.post(this.INVESTIGATION_LETTER_URL + '/delete', investigationLetterId);
    }

    getInvestigationLetter(investigationLetterId: any): Observable<InvestigationLetterRes> {
        return this.apiService.get(this.INVESTIGATION_LETTER_URL + '/' + investigationLetterId).pipe();
    }

    getInvestigationLetterBySelectedPersonId(
        investigationLetterId: any,
        selectedPersonId: any
    ): Observable<InvestigationLetterRes> {
        return this.apiService
            .get(this.INVESTIGATION_LETTER_URL + '/' + investigationLetterId + '/' + selectedPersonId)
            .pipe();
    }

    saveInvestigationLetter(investigationLetter: any): Observable<any> {
        let url = this.INVESTIGATION_LETTER_URL + '/save';
        if (investigationLetter.isSubmit) {
            investigationLetter.event = 'COMP';
            url = this.INVESTIGATION_LETTER_URL + '/submit';
        }
        this.globalMessageService.addSuccessPath(url);
        return this.apiService.post(url, investigationLetter);
    }

    downloadInvestigationLetter(investigationLetterId, isDraft) {
        return this.apiService.download(
            this.INVESTIGATION_LETTER_URL + '/' + investigationLetterId + '/download?isDraft=' + isDraft
        );
    }

    getPersonDetail(personId: any): Observable<Person> {
        return this.apiService.get(this.PERSON_URL + '/' + personId);
    }
    getOperationHomeByResourceId(resourceId: any): Observable<InvestigationLetterRes> {
        return this.apiService.get(this.INVESTIGATION_LETTER_URL + '/operationHomeDetails' + '/' + resourceId).pipe();
    }

    getServicePackageList(): Observable<ServicePackageListRes> {
        return this.apiService.get('/v1/service-packages');
    }

    getServicePackageDetail(eventId: number): Observable<ServicePackageRes> {
        return this.apiService.get('/v1/service-packages/' + eventId);
    }

    saveServicePackageDetail(servicePackage: ServicePackage): Observable<ServicePackageRes> {
        this.globalMessageService.addSuccessPath('/v1/service-packages');
        return this.apiService.post('/v1/service-packages', servicePackage);
    }

    getNytdSurveyDetail(nytdSurveyHeaderId: any): Observable<NytdSurveyDetailRes> {
        return this.apiService.get(this.NYTD_SURVEY_DETAIL_URL + '/' + nytdSurveyHeaderId);
    }

    saveNytdSurveyDetail(nytdSurveyDetail: NytdSurveyDetail): Observable<any> {
        this.globalMessageService.addSuccessPath(this.NYTD_SURVEY_DETAIL_URL);
        return this.apiService.post(this.NYTD_SURVEY_DETAIL_URL, nytdSurveyDetail);
    }
    getEleKinHomeAssessmentList(): Observable<EleKinAssessmentListRes> {
        return this.apiService.get('/v1/kinhome-assessments');
    }

    getEleKinHomeAssessmentDetail(eventId: number): Observable<KinAssessmentRes> {
        return this.apiService.get('/v1/kinhome-assessments/' + eventId);
    }

    saveEleKinHomeAssessmentDetail(kinAssessmentDetail: KinAssessmentDetail): Observable<KinAssessmentRes> {
        this.globalMessageService.addSuccessPath('/v1/kinhome-assessments');
        return this.apiService.post('/v1/kinhome-assessments', kinAssessmentDetail);
    }

    getChildPlacementDetail(careGiverId: number): Observable<KinAssessmentRes> {
        return this.apiService.get('/v1/kinhome-assessments/careGiverId/' + careGiverId);
    }

    updateKinHomeAssessmentEvent(kinAssessmentRes: KinAssessmentRes): Observable<KinAssessmentRes> {
        this.globalMessageService.addSuccessPath('/v1/kinhome-assessments');
        return this.apiService.post('/v1/kinhome-assessments/update-event', kinAssessmentRes);
    }

    displayAdoptionAssistanceApp(eventId: any): Observable<any> {
        return this.apiService.get(this.ADO_APP_DISPLAY + '/' +  eventId);
    }
}
