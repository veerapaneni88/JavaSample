import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CaseService } from '../service/case.service';
import { DisplayHomeAssessmentDetail } from '../model/homeAssessmentDetail';
import {
  DataTable,
  DirtyCheck,
  SET,
  ENVIRONMENT_SETTINGS,
  FormUtils,
  DfpsCommonValidators,
  DfpsFormValidationDirective,
  DfpsConfirmComponent,
  FormParams,
  NavigationService,
  NarrativeService
} from 'dfps-web-lib';
import { Store } from '@ngrx/store';
import { HomeAssessmentDetailValidators } from './home-assessment-detail-validator';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'home-assessment-detail',
  templateUrl: './home-assessment-detail.component.html'
})
export class HomeAssessmentDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  homeAddendumDetailForm: FormGroup;
  displayHomeAssessmentDetail: DisplayHomeAssessmentDetail;
  tableColumn: any[];
  kinshipSafetyEvaluationDataTable: DataTable;
  enableCurrentHomeAssessment = false;
  narrativeSubscription: Subscription;
  formsDto: any;
  isAlert = false;

  constructor(
    private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private caseService: CaseService,
    private modalService: BsModalService,
    private cookieService: CookieService,
    private narrativeService: NarrativeService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
  ) {
    super(store);
  }

  createForm() {
    this.homeAddendumDetailForm = this.formBuilder.group(
      {
        outputCompletedChecked: [''],
        signatureDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
        statusCodes: [''],
        buttonClicked: [''],
        docExists: ['']
      },
      {
        validators: [
          HomeAssessmentDetailValidators.validateHomeAssessmentDetail
        ]
      }
    );
  }
  ngOnInit(): void {
    this.navigationService.setTitle('Home Assessment Addendum Detail');
    this.helpService.loadHelp('Case');
    this.createForm();
    this.intializeScreen();
  }

  intializeScreen() {
    const eventId = this.route.snapshot.paramMap.get('id') ? this.route.snapshot.paramMap.get('id') : '0';
    this.caseService.getKinHomeAssessmentDetail(eventId).subscribe((response) => {

      this.displayHomeAssessmentDetail = response;
      if (this.displayHomeAssessmentDetail) {
        this.loadKinHomeAssessmentDetail();
        this.getkinshipSafetyEvaluationDetails();
        this.enableDisableFields();
        this.showInvalidateApprovalPopup();
      }
      this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
        this.generateNarrative();
      });
      this.homeAddendumDetailForm.patchValue({ docExists: this.displayHomeAssessmentDetail.documentExists });
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
  }

  enableDisableFields() {
    if (!this.displayHomeAssessmentDetail.statusHidden &&
      (this.displayHomeAssessmentDetail.eventTypeCode === 'KCM' ||
        this.displayHomeAssessmentDetail.eventTypeCode === 'KAM')) {
      this.enableCurrentHomeAssessment = true;
    } else {
      this.enableCurrentHomeAssessment = false;
    }

    if (this.displayHomeAssessmentDetail.outputCompletedEnabled) {
      FormUtils.enableFormControlStatus(this.homeAddendumDetailForm, ['outputCompletedChecked']);
    } else {
      FormUtils.disableFormControlStatus(this.homeAddendumDetailForm, ['outputCompletedChecked']);
    }

    if (this.displayHomeAssessmentDetail.signatureFieldsEnabled) {
      FormUtils.enableFormControlStatus(this.homeAddendumDetailForm, ['signatureDate', 'statusCodes']);
    } else {
      FormUtils.disableFormControlStatus(this.homeAddendumDetailForm, ['signatureDate', 'statusCodes']);
    }
  }

  loadKinHomeAssessmentDetail() {
    if (this.displayHomeAssessmentDetail) {
      this.homeAddendumDetailForm.patchValue({
        signatureDate: this.displayHomeAssessmentDetail.signatureDate,
        statusCodes: this.displayHomeAssessmentDetail.assessmentStatus,
        outputCompletedChecked: this.displayHomeAssessmentDetail.outputCompletedChecked
      });
    }
  }

  getkinshipSafetyEvaluationDetails() {
    this.tableColumn = [
      { field: 'eventType', header: 'Type', isHidden: false, isLink: false, width: 70 },
      { field: 'statusCode', header: 'Status', isHidden: false, isLink: false, width: 50 },
      { field: 'statusChangedDate', header: 'Signature Date', isHidden: false, isLink: false, width: 50 },
      { field: 'fullName', header: 'Created By', isHidden: false, isLink: false, width: 50 },
      { field: 'lastUpdatedDate', header: 'Updated Date', isHidden: false, isLink: false, width: 50 },
    ];
    this.kinshipSafetyEvaluationDataTable = {
      tableBody: this.displayHomeAssessmentDetail.homeAssessmentHistory,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false,
    };

    if (this.kinshipSafetyEvaluationDataTable.tableBody && this.kinshipSafetyEvaluationDataTable.tableBody.length) {
      this.kinshipSafetyEvaluationDataTable.tableBody.forEach(data => {
        data.eventType = this.displayHomeAssessmentDetail.eventType;
        data.signatureDate = this.displayHomeAssessmentDetail.signatureDate;
      })
    }
  }

  checkOutputCompleted() {
    const outputCompletedValue = this.homeAddendumDetailForm.get('outputCompletedChecked').value;
    if (!this.isAlert && this.displayHomeAssessmentDetail.event.status === 'PROC' &&
      !outputCompletedValue) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Assessment',
        message: 'The output must be completed before submitting for approval.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }

  checkPendingCriminalHistory() {
    if (this.displayHomeAssessmentDetail.criminalHistoryPending
      && this.displayHomeAssessmentDetail.criminalHistoryPersonList.length === 0) {
      const initialState = {
        title: 'Home Assessment',
        message: `A principal on the person list has a pending Criminal History Check that has not yet returned.
        Criminal History Check Results must be returned and accepted or rejected before submitting this Stage for approval.`,
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe();
    } else if (this.displayHomeAssessmentDetail.criminalHistoryPending
      && this.displayHomeAssessmentDetail.criminalHistoryPersonList.length > 0) {
      const initialState = {
        title: 'Home Assessment',
        messageList: [
          {
            content: 'Criminal History Check Results page must be completed before submitting this page for approval.',
            url: this.environmentSettings.impactP2WebUrl + '/case/person/records-check-list?cacheKey=' + sessionStorage.getItem('cacheKey')
          },
          {
            content: `A principal on the person list has a pending Criminal History Check that has not yet returned.
            Criminal History Check Results must be returned and accepted or rejected before submitting this Stage for approval.`,
            url: ''
          }],
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }

  showInvalidateApprovalPopup() {
    if (this.displayHomeAssessmentDetail.showInvalidateApprovalPopup) {
      const initialState = {
        title: 'Home Assessment',
        message: 'Saving this page will invalidate pending approval. Click OK to continue.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {

        }
      });
    }
  }

  saveHomeAssessmentDetail(isSubmit: boolean) {
    if (this.validateFormGroup(this.homeAddendumDetailForm)) {
      const payload = this.displayHomeAssessmentDetail;
      const reqPayload = {
        eventType: payload.eventTypeCode,
        homeAssessmentId: payload.homeAssessmentId,
        eventId: payload.event.eventId,
        assessmentStatus: this.homeAddendumDetailForm.get('statusCodes').value,
        statusChangedDate: this.homeAddendumDetailForm.get('signatureDate').value,
        outputCompletedChecked: this.homeAddendumDetailForm.get('outputCompletedChecked').value
      }
      this.caseService.saveKinHomeAssessmentDetail(reqPayload, isSubmit).subscribe(
        response => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          if (isSubmit) {
            const eventTypeCode = this.displayHomeAssessmentDetail.eventTypeCode;
            const eventId = payload.event.eventId;
            const cacheKey = this.environmentSettings.environmentName === 'Local'
              ? this.cookieService.get('cacheKey') : localStorage.getItem('cacheKey');
            window.location.href = this.environmentSettings.impactP2WebUrl +
              '/case/home-assessment/displayToDoDetail?cacheKey=' + cacheKey + '&eventTypeCode=' + eventTypeCode
              + '&eventId=' + eventId;
          } else {
            this.router.navigate(['/case/home-assessment-addendum-list/' + this.displayHomeAssessmentDetail.event.eventId])
              .then(() => {
                setTimeout(() => {
                  window.location.reload();
                }, 3000);
              });
          }
        }
      );
    }
  }

  save() {
    const eventId = this.route.snapshot.paramMap.get('id') ? this.route.snapshot.paramMap.get('id') : '0';
    this.caseService.getKinHomeAssessmentDetail(eventId).subscribe((response) => {
      if (response) {
        this.displayHomeAssessmentDetail.documentExists = response.documentExists;
        this.homeAddendumDetailForm.patchValue({ docExists: this.displayHomeAssessmentDetail.documentExists });
      }

      this.homeAddendumDetailForm.patchValue({ buttonClicked: 'save' });
      this.isAlert = false;
      if (!this.displayHomeAssessmentDetail.documentExists) {
        FormUtils.enableFormControlStatus(this.homeAddendumDetailForm, ['outputCompletedChecked']);
      }
      if (!this.isAlert) {
        this.saveHomeAssessmentDetail(false);
      }
    });
  }

  submit() {
    const eventId = this.route.snapshot.paramMap.get('id') ? this.route.snapshot.paramMap.get('id') : '0';
    this.caseService.getKinHomeAssessmentDetail(eventId).subscribe((response) => {
      if (response) {
        this.displayHomeAssessmentDetail.documentExists = response.documentExists;
        this.homeAddendumDetailForm.patchValue({ docExists: this.displayHomeAssessmentDetail.documentExists });
      }

      this.homeAddendumDetailForm.patchValue({ buttonClicked: 'submit' });
      this.isAlert = false;
      this.checkOutputCompleted();
      this.checkPendingCriminalHistory();
      if (!this.displayHomeAssessmentDetail.documentExists) {
        FormUtils.enableFormControlStatus(this.homeAddendumDetailForm, ['outputCompletedChecked']);
      }
      if (!this.isAlert) {
        this.saveHomeAssessmentDetail(true);
      }
    });
  }

  deleteDetail() {
    this.caseService.deleteKinHomeAssessmentDetail(this.displayHomeAssessmentDetail.event.eventId).subscribe((res) => {
      this.router.navigate(['/case/home-assessment-addendum-list']);
    });
  }

  showApproval() {
    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/approval-status/display?cacheKey=' + sessionStorage.getItem('cacheKey');
  }

  generateNarrative() {
    const narrativeData: any = this.displayHomeAssessmentDetail.formTagDto;
    if (narrativeData) {
      this.narrativeService.callNarrativeService(narrativeData);
    }
  }

}
