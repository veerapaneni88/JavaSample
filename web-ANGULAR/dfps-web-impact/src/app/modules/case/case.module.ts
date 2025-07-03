import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  DfpsAddressValidator, DfpsButton,
  DfpsCheckBox,
  DfpsCollapsableSection,
  DfpsConfirm,
  DfpsDataTable,
  DfpsDataTable1,
  DfpsDataTableLazyload,
  DfpsDataTableLazyload1,
  DfpsDatePicker,
  DfpsDirtyCheck,
  DfpsFormLaunch,
  DfpsFormValidation,
  DfpsInput,
  DfpsMultiSelect,
  DfpsRadioButton,
  DfpsReportLaunch,
  DfpsNarrative,
  DfpsSelect,
  DfpsTextarea,
  DfpsTime
} from 'dfps-web-lib';
import { AccordionModule } from 'ngx-bootstrap/accordion';
import { SharedModule } from '../shared/shared.module';
import { CaseRoutingModule } from './case-routing.module';
import { HomeHistoryDetailComponent } from './home-history-detail/home-history-detail.component';
import { HomeHistoryComponent } from './home-history/home-history.component';
import { FadAddressDetailComponent } from './home-information/fad/address-detail/address-detail.component';
import { KinAddressDetailComponent } from './home-information/kin/address-detail/address-detail.component';
import { FadHomeInformationComponent } from './home-information/fad/fad-home-information.component';
import { KinHomeInformationComponent } from './home-information/kin/kin-home-information.component';
import { PhoneDetailComponent } from './home-information/phone-detail/phone-detail.component';
import { PaymentInformationComponent } from './payment-information/payment-information.component';
import { PlacementLogComponent } from './placement-log/placement-log.component';
import { SearchStandardComponent } from './waiver-variance/search-standard/search-standard.component';
import { CaseService } from './service/case.service';
import { WaiverVarianceService } from './service/waiver-variance.service';
import { WaiverVarianceComponent } from './waiver-variance/waiver-variance.component';
import { HomeAssessmentAddendumComponent } from './home-assessment-addendum/home-assessment-addendum.component';
import { HomeAssessmentListComponent } from './home-assessment-list/home-assessment-list.component';
import { HomeAssessmentDetailComponent } from './home-assessment-detail/home-assessment-detail.component';
import { StrengthNeedsAssessmentDetailComponent } from './strength-needs-assessment-detail/strength-needs-assessment-detail.component';
import { AllegationListComponent } from './allegation-list/allegation-list.component';
import { DangerIndicatorsComponent } from './danger-indicators/danger-indicators.component';
import { InjuryDetailComponent } from './injury-detail/injury-detail.component';
import { InjuryListComponent } from './injury-list/injury-list.component';
import { SafetyPlanListComponent } from './safety-plan-list/safety-plan-list.component';
import { SafetyPlanComponent } from './safety-plan/safety-plan.component';
import { ApsCaseManagementComponent } from './aps-case-management/aps-case-management.component';
import { CheckboxModule } from 'primeng/checkbox';
import { RadioButtonModule } from 'primeng/radiobutton';
import { SafetyAssesmssmentDetailComponent } from './safety-assesmssment-detail/safety-assesmssment-detail.component';
import { OutcomeMatrixComponent } from './service-plans/outcome-matrix/outcome-matrix.component';
import { OutcomeMatrixDetailsComponent } from './service-plans/outcome-matrix-details/outcome-matrix-details.component';
import { CareDetailsComponent } from './service-plans/care-details/care-details.component';
import { RiskOfRecidivismAssessmentComponent } from './risk-of-recidivism-assessment/risk-of-recidivism-assessment.component';
import { ApsShieldServicePlanComponent } from './service-plans/aps-shield-service-plan/aps-shield-service-plan.component';
import { ContactStandardDetailsComponent } from './contact-standard-details/contact-standard-details.component';
import { GuardianshipDetailComponent } from './guardianship-detail/guardianship-detail.component';
import { ImmediateInterventionsComponent } from './immediate-interventions/immediate-interventions.component';
import { ApsSpActionButtonsComponent } from './aps-sp-action-buttons/aps-sp-action-buttons.component';
import { AddActionsToMultiPrblmsComponent } from './add-actions-to-multi-prblms/add-actions-to-multi-prblms.component';
import { ApsPreShieldServicePlanComponent } from './service-plans/aps-pre-shield-service-plan/aps-pre-shield-service-plan.component';
import { AdminReviewComponent } from './admin-review/admin-review.component';


import { InvestigationLetterListComponent } from './investigation-letter-list/investigation-letter-list.component';
import { FpsLetterHeadComponent } from './investigation-letter/fps-letterhead/fps-letterhead.component';
import { VisitAcknowledgementComponent } from './investigation-letter/visit-acknowledgement/visit-acknowledgement.component';
import { ParentNotificationOfInterviewComponent } from './investigation-letter/parent-notification-of-interview/parent-notification-of-interview.component';
import { ChildDeathReportComponent } from './investigation-letter/child-death-report/child-death-report.component';
import { AnFindingsLetterToPerpComponent } from './investigation-letter/an-findings-letter-to-perp/an-findings-letter-to-perp.component';
import { ApRightsPriorToInterviewComponent } from './investigation-letter/ap-rights-prior-to-interview/ap-rights-prior-to-interview.component';
import { InvestigationInitialVictimParentComponent } from './investigation-letter/investigation-intial-vicitim-parent/investigation-initial-victim-parent.component';
import { AnInvestigationParentsVictimComponent } from './investigation-letter/an-investigation-parents-victim/an-investigation-parents-victim.component';
import { HhscCaseTransferComponent } from './investigation-letter/hhsc-case-transfer/hhsc-case-transfer.component';
import { CciInvestigationFindingComponent } from './investigation-letter/cci-investigation-finding/cci-investigation-finding.component';
import {CciReporterLetterComponent} from './investigation-letter/cci-reporter-letter/cci-reporter-letter.component';
import { ServicePackageListComponent } from './placement/service-package-list/service-package-list.component';
import { ServicePackageDetailComponent } from './placement/service-package-detail/service-package-detail.component';
import { NytdSurveyDetailComponent } from '../case/nytd/nytd-survey-detail/nytd-survey-detail.component';
import { DiligentSearchComponent } from './case-management/forms-refferals/diligent-search/diligent-search.component';
import { QuickFindComponent } from './case-management/forms-refferals/quick-find/quick-find.component';
import { QuickFindService, DiligentSearchService } from './service/forms-referral.service';
import { KinHomeAssessmentListComponent } from './placement/kinhome-assessment-list/kinhome-assessment-list.component';
import { KinHomeAssessmentDetail } from './placement/kinhome-assessment-detail/kinhome-assessment-detail.component';
import { AdoAssistanceAppComponent } from './ado-pad/ado-assistance-app/ado-assistance-app.component';
import { AdoRecertificationAppComponent } from './ado-pad/ado-recertification-app/ado-recertification-app.component';

@NgModule({
  declarations: [
    FadHomeInformationComponent,
    FadAddressDetailComponent,
    KinAddressDetailComponent,
    PhoneDetailComponent,
    KinHomeInformationComponent,
    PlacementLogComponent,
    HomeHistoryComponent,
    HomeHistoryDetailComponent,
    WaiverVarianceComponent,
    SearchStandardComponent,
    PaymentInformationComponent,
    HomeAssessmentAddendumComponent,
    HomeAssessmentListComponent,
    HomeAssessmentDetailComponent,
    StrengthNeedsAssessmentDetailComponent,
    DangerIndicatorsComponent,
    SafetyPlanListComponent,
    SafetyPlanComponent,
    InjuryDetailComponent,
    InjuryListComponent,
    AllegationListComponent,
    ApsCaseManagementComponent,
    SafetyAssesmssmentDetailComponent,
    OutcomeMatrixComponent,
    OutcomeMatrixDetailsComponent,
    CareDetailsComponent,
    RiskOfRecidivismAssessmentComponent,
    CareDetailsComponent,
    ApsShieldServicePlanComponent,
    ContactStandardDetailsComponent,
    GuardianshipDetailComponent,
    ImmediateInterventionsComponent,
    ApsSpActionButtonsComponent,
    AddActionsToMultiPrblmsComponent,
    ApsPreShieldServicePlanComponent,
    InvestigationLetterListComponent,
    FpsLetterHeadComponent,
    VisitAcknowledgementComponent,
    ParentNotificationOfInterviewComponent,
    ChildDeathReportComponent,
    ParentNotificationOfInterviewComponent,
    AnFindingsLetterToPerpComponent,
    ApRightsPriorToInterviewComponent,
    InvestigationInitialVictimParentComponent,
    AnInvestigationParentsVictimComponent,
    HhscCaseTransferComponent,
    CciInvestigationFindingComponent,
    CciReporterLetterComponent,
    ServicePackageListComponent,
    ServicePackageDetailComponent,
    AdminReviewComponent,
    NytdSurveyDetailComponent,
    DiligentSearchComponent,
    QuickFindComponent,
    NytdSurveyDetailComponent,
    KinHomeAssessmentListComponent,
    KinHomeAssessmentDetail,
    AdoAssistanceAppComponent,
    AdoRecertificationAppComponent
  ],
  imports: [
    RadioButtonModule,
    CheckboxModule,
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    FormsModule,
    AccordionModule,
    CaseRoutingModule,
    DfpsButton,
    DfpsCheckBox,
    DfpsInput,
    DfpsCollapsableSection,
    DfpsDatePicker,
    DfpsConfirm,
    DfpsSelect,
    DfpsMultiSelect,
    DfpsDataTableLazyload,
    DfpsDataTable,
    DfpsDataTable1,
    DfpsDataTableLazyload1,
    DfpsDirtyCheck,
    DfpsReportLaunch,
    DfpsFormValidation,
    DfpsTextarea,
    DfpsRadioButton,
    DfpsFormLaunch,
    DfpsAddressValidator,
    DfpsNarrative,
    DfpsTime
  ],
  exports: [
  ],
  providers: [
    CaseService, WaiverVarianceService, QuickFindService, DiligentSearchService
  ]
})
export class CaseModule { }
