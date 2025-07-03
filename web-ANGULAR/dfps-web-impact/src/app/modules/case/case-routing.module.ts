import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeHistoryDetailComponent } from './home-history-detail/home-history-detail.component';
import { HomeHistoryComponent } from './home-history/home-history.component';
import { FadAddressDetailComponent } from './home-information/fad/address-detail/address-detail.component';
import { KinAddressDetailComponent } from './home-information/kin/address-detail/address-detail.component';
import { FadHomeInformationComponent } from './home-information/fad/fad-home-information.component';
import { KinHomeInformationComponent } from './home-information/kin/kin-home-information.component';
import { PhoneDetailComponent } from './home-information/phone-detail/phone-detail.component';
import { PlacementLogComponent } from './placement-log/placement-log.component';
import { SearchStandardComponent } from './waiver-variance/search-standard/search-standard.component';
import { WaiverVarianceComponent } from './waiver-variance/waiver-variance.component';
import { ResourceSearchComponent } from '@shared/resource-search/resource-search.component';
import { PaymentInformationComponent } from './payment-information/payment-information.component';
import { HomeAssessmentAddendumComponent } from './home-assessment-addendum/home-assessment-addendum.component';
import { HomeAssessmentListComponent } from './home-assessment-list/home-assessment-list.component';
import { HomeAssessmentDetailComponent } from './home-assessment-detail/home-assessment-detail.component';
import { DfpsDirtyCheckComponent } from 'dfps-web-lib';
import { AllegationListComponent } from './allegation-list/allegation-list.component';
import { DangerIndicatorsComponent } from './danger-indicators/danger-indicators.component';
import { SafetyPlanListComponent } from './safety-plan-list/safety-plan-list.component';
import { SafetyPlanComponent } from './safety-plan/safety-plan.component';
import { InjuryDetailComponent } from './injury-detail/injury-detail.component';
import { InjuryListComponent } from './injury-list/injury-list.component';
import { StrengthNeedsAssessmentDetailComponent } from './strength-needs-assessment-detail/strength-needs-assessment-detail.component';
import { ApsCaseManagementComponent } from './aps-case-management/aps-case-management.component';
import { SafetyAssesmssmentDetailComponent } from './safety-assesmssment-detail/safety-assesmssment-detail.component';
import { OutcomeMatrixComponent } from './service-plans/outcome-matrix/outcome-matrix.component';
import { OutcomeMatrixDetailsComponent } from './service-plans/outcome-matrix-details/outcome-matrix-details.component';
import { CareDetailsComponent } from './service-plans/care-details/care-details.component';
import { StaffSearchComponent } from '../shared/staff-search/staff-search.component';
import { RiskOfRecidivismAssessmentComponent } from './risk-of-recidivism-assessment/risk-of-recidivism-assessment.component';
import { ApsShieldServicePlanComponent } from './service-plans/aps-shield-service-plan/aps-shield-service-plan.component';
import { GuardianshipDetailComponent } from './guardianship-detail/guardianship-detail.component';
import { ContactStandardDetailsComponent } from './contact-standard-details/contact-standard-details.component';
import { AddActionsToMultiPrblmsComponent } from './add-actions-to-multi-prblms/add-actions-to-multi-prblms.component';
import { ApsPreShieldServicePlanComponent } from './service-plans/aps-pre-shield-service-plan/aps-pre-shield-service-plan.component';
import { InvestigationLetterListComponent } from './investigation-letter-list/investigation-letter-list.component';
import { FpsLetterHeadComponent } from './investigation-letter/fps-letterhead/fps-letterhead.component';
import { VisitAcknowledgementComponent } from './investigation-letter/visit-acknowledgement/visit-acknowledgement.component'
import { ChildDeathReportComponent } from './investigation-letter/child-death-report/child-death-report.component'
import { ParentNotificationOfInterviewComponent } from './investigation-letter/parent-notification-of-interview/parent-notification-of-interview.component';
import { AnFindingsLetterToPerpComponent } from './investigation-letter/an-findings-letter-to-perp/an-findings-letter-to-perp.component';
import { ApRightsPriorToInterviewComponent } from './investigation-letter/ap-rights-prior-to-interview/ap-rights-prior-to-interview.component';
import {InvestigationInitialVictimParentComponent} from "@case/investigation-letter/investigation-intial-vicitim-parent/investigation-initial-victim-parent.component";
import {AnInvestigationParentsVictimComponent} from "@case/investigation-letter/an-investigation-parents-victim/an-investigation-parents-victim.component";
import { HhscCaseTransferComponent } from './investigation-letter/hhsc-case-transfer/hhsc-case-transfer.component';
import {CciInvestigationFindingComponent} from '@case/investigation-letter/cci-investigation-finding/cci-investigation-finding.component';
import {CciReporterLetterComponent} from './investigation-letter/cci-reporter-letter/cci-reporter-letter.component';
import { AdminReviewComponent } from './admin-review/admin-review.component';
import { ServicePackageListComponent } from './placement/service-package-list/service-package-list.component';
import { ServicePackageDetailComponent } from './placement/service-package-detail/service-package-detail.component';
import {NytdSurveyDetailComponent} from './nytd/nytd-survey-detail/nytd-survey-detail.component';
import { QuickFindComponent } from './case-management/forms-refferals/quick-find/quick-find.component';
import { DiligentSearchComponent } from './case-management/forms-refferals/diligent-search/diligent-search.component';
import { KinHomeAssessmentListComponent } from './placement/kinhome-assessment-list/kinhome-assessment-list.component';
import { KinHomeAssessmentDetail } from './placement/kinhome-assessment-detail/kinhome-assessment-detail.component';
import { AdoRecertificationAppComponent } from './ado-pad/ado-recertification-app/ado-recertification-app.component';
import { AdoAssistanceAppComponent } from './ado-pad/ado-assistance-app/ado-assistance-app.component';

export const CaseRoutes: Routes = [
  {
    path: 'case/home-information/FAD/:resourceId',
    component: FadHomeInformationComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-information/FAD',
    component: FadHomeInformationComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-information/FAD/:resourceId/resourcesearch',
    component: ResourceSearchComponent,
  },
  {
    path: 'case/home-information/KIN/:resourceId',
    component: KinHomeInformationComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'search/home-search/add-kin/resource-search',
    component: ResourceSearchComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-information/placement-log',
    component: PlacementLogComponent,
  },
  {
    path: 'case/home-information/home-history/FAD/:resourceId',
    component: HomeHistoryComponent,
  },
  {
    path: 'case/home-information/home-history/KIN/:resourceId',
    component: HomeHistoryComponent,
  },
  {
    path: 'case/home-information/home-history/FAD/:resourceId/:id',
    component: HomeHistoryDetailComponent,
  },
  {
    path: 'case/waiver-variance/:eventId',
    component: WaiverVarianceComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/waiver-variance/:eventId/search-standard',
    component: SearchStandardComponent,
  },
  {
    path: 'case/home-information/FAD/:resourceId/address/:resourceAddressId',
    component: FadAddressDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-information/FAD/:resourceId/phone/:resourcePhoneId',
    component: PhoneDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-information/KIN/:resourceId/address/:resourceAddressId',
    component: KinAddressDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-information/KIN/:resourceId/phone/:resourcePhoneId',
    component: PhoneDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-information/payment-information/:resourceId',
    component: PaymentInformationComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-assessment-addendum-list/add',
    component: HomeAssessmentAddendumComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/home-assessment-addendum-list',
    component: HomeAssessmentListComponent,
  },
  {
    path: 'case/home-assessment-addendum-list/:id',
    component: HomeAssessmentDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/case-management/maintenance/danger-indicators',
    component: DangerIndicatorsComponent,
  },
  {
    path: 'case/case-management/maintenance/safety-plans',
    component: SafetyPlanListComponent,
  },
  {
    path: 'case/case-management/maintenance/safety-plans/:safetyPlanId',
    component: SafetyPlanComponent,
  },
  {
    path: 'case/safety-plan/event/:eventId',
    component: SafetyPlanComponent,
  },
  {
    path: 'case/allegation/injury-details',
    component: InjuryListComponent,
  },
  {
    path: 'case/allegation/injury-details/:injuryId',
    component: InjuryDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/allegation/injury-details/:injuryId/allegation-list',
    component: AllegationListComponent,
  },
  {
    path: 'case/allegation/injury-detail/event/:eventId',
    component: InjuryDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/strength-needs-assessment-detail/:eventId',
    component: StrengthNeedsAssessmentDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/safety-assessment-detail/:eventId',
    component: SafetyAssesmssmentDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/caseManagement/aps-case-management/case-conclusion',
    component: ApsCaseManagementComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/servicePlans/outcomeMatrix',
    component: OutcomeMatrixComponent
  },
  {
    path: 'case/servicePlans/outcomeMatrix/details/:id',
    component: OutcomeMatrixDetailsComponent
  },
  {
    path: 'case/servicePlans/care',
    component: CareDetailsComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/safety-assessment-detail/:eventId/staffsearch',
    component: StaffSearchComponent,
  },
  {
    path: 'case/rora/:isSystemSupervisor',
    component: RiskOfRecidivismAssessmentComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/shield-service-plan/displayServicePlan',
    component: ApsShieldServicePlanComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/guardianship-detail/:eventId',
    component: GuardianshipDetailComponent,
  },
  {
    path: 'case/displayContactStandardDetail/:apsSpMonitoringPlanId',
    component: ContactStandardDetailsComponent,
  },
  {
    path: 'case/add-actions-to-multiProblems',
    component: AddActionsToMultiPrblmsComponent
  },
  {
    path: 'case/servicePlans/preshieldApsServicePlan',
    component: ApsPreShieldServicePlanComponent
  },
  {
    path: 'case/letters/investigation-letters',
    component: InvestigationLetterListComponent,
  },
  {
    path: 'case/letters/investigation-letters/FPS/:investigationLetterId',
    component: FpsLetterHeadComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/VAF/:investigationLetterId',
    component: VisitAcknowledgementComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/CDR/:investigationLetterId',
    component: ChildDeathReportComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/PRN/:investigationLetterId',
    component: ParentNotificationOfInterviewComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/ANF/:investigationLetterId/staffsearch',
    component: StaffSearchComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/ANF/:investigationLetterId',
    component: AnFindingsLetterToPerpComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/ARP/:investigationLetterId',
    component: ApRightsPriorToInterviewComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/ARS/:investigationLetterId',
    component: ApRightsPriorToInterviewComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/IIL/:investigationLetterId',
    component: InvestigationInitialVictimParentComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/AIR/:investigationLetterId',
    component: AnInvestigationParentsVictimComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/HCT/:investigationLetterId',
    component: HhscCaseTransferComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/CIF/:investigationLetterId',
    component: CciInvestigationFindingComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/CIF/:investigationLetterId/resourcesearch',
    component: ResourceSearchComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/CRL/:investigationLetterId',
    component: CciReporterLetterComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/letters/investigation-letters/CRS/:investigationLetterId',
    component: CciReporterLetterComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/admin-review',
    component: AdminReviewComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/placement/service-packages',
    component: ServicePackageListComponent
  },
  {
    path: 'case/placement/service-packages/:servicePackageId',
    component: ServicePackageDetailComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/nytd/nytdSurvey/NytdSurvey/displaySurveyForYouth/:idStaff/:idSurvey',
    component: NytdSurveyDetailComponent

  },
  {
    path: 'case/case-management/forms-referral/quick-find/:id',
    component: QuickFindComponent,
  },
  {
    path: 'case/caseManagement/aps-case-management/forms-referral/quick-find/:id',
    component: QuickFindComponent,
  },
  {
    path: 'case/case-management/forms-referral/diligent-search/:id',
    component: DiligentSearchComponent,
  },
  {
    path: 'case/case-management/forms-referral/quick-find/:id/staffsearch',
    component: StaffSearchComponent,
  },
  {
    path: 'case/case-management/forms-referral/diligent-search/:id/staffsearch',
    component: StaffSearchComponent,
  },
  {
       path: 'case/placement/kinhome-assessments',
       component: KinHomeAssessmentListComponent
  },
  {
    path: 'case/placement/kinhome-assessments/:kinAssessmentId',
    component: KinHomeAssessmentDetail,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/adoptionAssistance/ado-assistance-application/:eventId',
    component: AdoAssistanceAppComponent,
    canDeactivate: [DfpsDirtyCheckComponent]
  },
  {
    path: 'case/adoptionAssistance/ado-assistance-recertification/:eventId',
    component: AdoRecertificationAppComponent,
  }
];

@NgModule({
  imports: [RouterModule.forChild(CaseRoutes)],
  exports: [RouterModule],
  providers: [],
})
export class CaseRoutingModule { }
