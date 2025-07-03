import { Component, OnDestroy, OnInit, Inject, HostListener } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  FormValue,
  NavigationService,
  SET, ENVIRONMENT_SETTINGS
} from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import { DisplayHomeInformation } from '../../model/case';
import { CaseService } from '../../service/case.service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { KinHomeInformationValidators } from './kin-home-information.validator';
import { dfpsValidatorErrors } from '../../../../../messages/validator-errors';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'kin-home-information',
  templateUrl: './kin-home-information.component.html'
})
export class KinHomeInformationComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  tableColumn: any[];
  careGiverEligibilityDataTable: DataTable;
  paymentEligibilityDataTable: DataTable;
  addressListDatatable: DataTable;
  phoneListDatatable: DataTable;
  disableStatusField = false;
  isShowDocument = true;
  hideSave = false;
  isPendClosure = false;
  placementCourtOrdered = false;
  isAddress = false;
  isPhone = false;
  requestType: any;
  selectedAddressData: any = [];
  selectedPhoneData: any = [];
  showNewPage = false;
  hidePlacementOrderedDate = false;
  hidePaymentOrderedDate = false;
  hideAgreementSignedDate = false;
  hidePMCCheckboxes = false;
  unfilteredStatusOptions = [];
  hideApproveButton = false;
  formValues: FormValue[];
  approvalRejected = false;
  hasBeenSubmitted = false;
  incomeQualified = null;
  disableValidation = false;
  hideSaveSubmit = false;
  hideAddressAndPhoneButtons = false;
  isRedirectedFromApproval = false;
  isAlert = false;
  venedorId = null;
  disableAddressAndPhoneSingleSelect = false;
  errorNoDataMessage;
  hideAttentionMessage = false;

  constructor(private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private caseService: CaseService,
    private modalService: BsModalService,
    private route: ActivatedRoute,
    private router: Router,
    private cookieService: CookieService,
    private helpService: HelpService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  displayHomeInformation: DisplayHomeInformation;

  homeInfoForm: FormGroup;

  ngOnInit(): void {
    this.navigationService.setTitle('Home Information');
    this.helpService.loadHelp('Case');
    this.createForm();
    this.route.queryParams.subscribe((param) => {
      this.isRedirectedFromApproval = (param.action === 'approval');
      this.hideApproveButton = !this.isRedirectedFromApproval;
    });
    this.intializeScreen();
  }

  createForm() {
    this.homeInfoForm = this.formBuilder.group(
      {
        category: ['', [Validators.required]],
        status: ['', [Validators.required]],
        ethnicity: [''],
        language: [''],
        religion: [''],
        annualIncome: ['', [Validators.required, DfpsCommonValidators.validateCurrency(11)]],
        maritalStatus: [''],
        sourceInquiry: [''],
        relativeCaregiver: [''],
        ficitiveCaregiver: [''],
        personCount: ['', [Validators.required]],
        isCaregiverMaternal: [''],
        isCaregiverPaternal: [''],
        manualGiven: [''],
        assessmentStatus: [''],
        assessmentStatusDecode: [''],
        assessmentApprovedDate: [''],
        isPlacementCourtOrdered: [''],
        kinCaregiverEligibilityStatus: [''],
        allKinEmployed: [''],
        signedAgreement: [''],
        agreementSignedDate: [''],
        isPaymentCourtOrdered: [''],
        hasBegunTraining: [''],
        incomeQualified: [''],
        fplAmount: [''],
        kinCaregiverPaymentEligibilityStatus: [''],
        paymentStartDate: [''],
        closureReason: [''],
        recommendReopening: [''],
        involuntaryClosure: [''],
        courtOrderedPaymentDate: [''],
        courtOrderedPlacementDate: [''],
        kinAssessment: [''],
        saveAndSubmitStatusMsg: [''],
        buttonClicked: [''],
        supervisorApprovalMsg: [''],
        relativeCareGiverMsg: [''],
        pendingClosureSupervisorApprovalMsg: [''],
        vendorIdErrorMsg: [''],
        vendorId: [''],
        isHouseholdMeetFPL: ['']
      }, {
      validators: [
        KinHomeInformationValidators.validateHomeInformationKin
      ]
    }
    );
  }

  intializeScreen() {
    this.requestType = [
      { label: 'Yes', value: true },
      { label: 'No', value: false },
    ];
    const resourceId = this.route.snapshot.paramMap.get('resourceId') ? this.route.snapshot.paramMap.get('resourceId') : '0';

    if (Number(resourceId) < 1) {
      this.errorNoDataMessage = dfpsValidatorErrors.MSG_INT_NO_DATA_PASSED_IN;
    } else {
      FormUtils.disableFormControlStatus(this.homeInfoForm,
        ['category', 'paymentStartDate', 'assessmentApprovedDate', 'involuntaryClosure']);
      this.caseService.displayKinHomeInformation(resourceId).subscribe((response) => {
        this.displayHomeInformation = response;
        this.unfilteredStatusOptions = this.displayHomeInformation.status;
        this.hideApproveButton = !this.displayHomeInformation.submittedForApproval;
        this.setFormValues();
        this.updateShowNewPage();
        this.disableMaternal();
        this.disablePaternal();
        this.enablePMCheckboxes();
        this.updateDisableFlags();
        this.filterStatusDropdown();
        this.hideOrShowDateFields();
        this.loadCareGiverEligibilityHistory();
        this.loadPaymentEligibilityHistory();
        this.loadAddressListDetails();
        this.loadPhoneListDetails();
        this.reloadReject();
        this.formValues = this.getForms();
        this.setVendorId();
        if (this.displayHomeInformation.pageMode === 'VIEW') {
          this.disableAllFieldsAndButtons();
        }
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      });
    }
  }

  setFormValues() {
    if (this.displayHomeInformation && this.displayHomeInformation.resource) {
      this.homeInfoForm.patchValue({
        category: this.displayHomeInformation.resource.category,
        status: this.displayHomeInformation.resource.faHomeStatus,
        ethnicity: this.displayHomeInformation.resource.ethnicity,
        language: this.displayHomeInformation.resource.language,
        religion: this.displayHomeInformation.resource.religion,
        annualIncome: (Math.round(this.displayHomeInformation.resource.annualIncome || 0.00)).toFixed(2),
        maritalStatus: this.displayHomeInformation.resource.maritalStatus,
        marriageDate: this.displayHomeInformation.resource.marriageDate,
        sourceInquiry: this.displayHomeInformation.resource.sourceInquiry,
        relativeCaregiver: this.displayHomeInformation.resource.relativeCaregiver,
        ficitiveCaregiver: this.displayHomeInformation.resource.ficitiveCaregiver,
        personCount: Number(this.displayHomeInformation.resource.personCount),
        isCaregiverMaternal: this.displayHomeInformation.resource.isCaregiverMaternal,
        isCaregiverPaternal: this.displayHomeInformation.resource.isCaregiverPaternal,
        manualGiven: this.displayHomeInformation.resource.manualGiven,
        assessmentStatus: this.displayHomeInformation.resource.assessmentStatus,
        assessmentStatusDecode: this.displayHomeInformation.resource.assessmentStatusDecode,
        assessmentApprovedDate: this.displayHomeInformation.resource.assessmentApprovedDate,
        isPlacementCourtOrdered: this.displayHomeInformation.resource.isPlacementCourtOrdered,
        kinCaregiverEligibilityStatus: this.displayHomeInformation.resource.kinCaregiverEligibilityStatus,
        allKinEmployed: this.displayHomeInformation.resource.allKinEmployed,
        signedAgreement: this.displayHomeInformation.resource.signedAgreement,
        agreementSignedDate: this.displayHomeInformation.resource.agreementSignedDate,
        isPaymentCourtOrdered: this.displayHomeInformation.resource.isPaymentCourtOrdered,
        hasBegunTraining: this.displayHomeInformation.resource.hasBegunTraining,
        incomeQualified: this.displayHomeInformation.resource.incomeQualified,
        fplAmount: this.displayHomeInformation.resource.fplAmount,
        kinCaregiverPaymentEligibilityStatus: this.displayHomeInformation.resource.kinCaregiverPaymentEligibilityStatus,
        paymentStartDate: this.displayHomeInformation.resource.paymentStartDate,
        closureReason: this.displayHomeInformation.resource.closureReason,
        recommendReopening: this.displayHomeInformation.resource.recommendReopen,
        involuntaryClosure: this.displayHomeInformation.resource.involuntaryClosure,
        courtOrderedPaymentDate: this.displayHomeInformation.resource.courtOrderedPaymentDate,
        courtOrderedPlacementDate: this.displayHomeInformation.resource.courtOrderedPlacementDate,
        kinAssessment: this.displayHomeInformation.kinAssessment,
        isHouseholdMeetFPL: this.displayHomeInformation.resource.isHouseholdMeetFPL
      });
    }
  }

  disableMaternal() {
    const isCaregiverPaternal = this.homeInfoForm.get('isCaregiverPaternal').value;
    if (isCaregiverPaternal) {
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['isCaregiverMaternal']);
    } else {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['isCaregiverMaternal']);
    }
  }

  disablePaternal() {
    const isCaregiverMaternal = this.homeInfoForm.get('isCaregiverMaternal').value;
    if (isCaregiverMaternal) {
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['isCaregiverPaternal']);
    } else {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['isCaregiverPaternal']);
    }
  }

  placementOrderedClicked() {
    const placementOrdered = this.homeInfoForm.get('isPlacementCourtOrdered').value;
    if (placementOrdered) {
      this.hidePlacementOrderedDate = false;
    } else {
      this.hidePlacementOrderedDate = true;
    }
  }

  signedAgreementClicked() {
    const kinAgreementSigned = this.homeInfoForm.get('signedAgreement').value;
    if (kinAgreementSigned) {
      this.hideAgreementSignedDate = false;
    } else {
      this.hideAgreementSignedDate = true;
    }
  }

  paymentOrderedClicked() {
    if (this.homeInfoForm.get('isPaymentCourtOrdered').value) {
      this.hidePaymentOrderedDate = false;
    } else {
      this.hidePaymentOrderedDate = true;
    }
  }

  enablePMCheckboxes() {
    const isRelativeCaregiver = this.homeInfoForm.get('relativeCaregiver').value;
    if (!isRelativeCaregiver) {
      this.hidePMCCheckboxes = true;
      this.hidePMCCheckboxes = true;
    } else {
      this.hidePMCCheckboxes = false;
      this.hidePMCCheckboxes = false;
    }
  }

  relativeCaregiverClicked() {
    const isRelativeCaregiver = this.homeInfoForm.get('relativeCaregiver').value;
    if (!isRelativeCaregiver) {
      this.homeInfoForm.patchValue({ isCaregiverPaternal: false });
      this.homeInfoForm.patchValue({ isCaregiverMaternal: false });
    }
    this.enablePMCheckboxes();
  }

  updateShowNewPage() {
    const status = this.homeInfoForm.get('status').value;
    const isPlacementCourtOrdered = this.homeInfoForm.get('isPlacementCourtOrdered').value;
    const isKinAssessment = this.displayHomeInformation.kinAssessment;
    if (status && (status === '080' || status === '070') && !isKinAssessment && isPlacementCourtOrdered === null) {
      this.showNewPage = false;
    }
    else {
      this.showNewPage = true;
    }
  }

  filterStatusDropdown() {
    const selectedStatus = this.displayHomeInformation.resource.faHomeStatus;
    this.displayHomeInformation.status = [];
    if (selectedStatus === '091') { // selectedStatus is  In Review
      this.displayHomeInformation.status.push(this.getStatus('091'));
      this.displayHomeInformation.status.push(this.getStatus('060'));
      this.displayHomeInformation.status.push(this.getStatus('061'));
      this.displayHomeInformation.status.push(this.getStatus('080'));
    } else if (selectedStatus === '080') { // selectedStatus is Pending Closure
      // if the home was rejected enable the status and also enable Save button
      if (this.displayHomeInformation.eventStatus === 'COMP' && this.displayHomeInformation.rejectClosure) {
        this.displayHomeInformation.status.push(this.getStatus('091'));
      } else {
        this.displayHomeInformation.status.push(this.getStatus('080'));
      }
    } else if (selectedStatus === '061') { // selectedStatus is Pend Court Ordered
      this.displayHomeInformation.status.push(this.getStatus('091'));
      this.displayHomeInformation.status.push(this.getStatus('060'));
      this.displayHomeInformation.status.push(this.getStatus('061'));
      this.displayHomeInformation.status.push(this.getStatus('080'));
    } else if (selectedStatus === '060') { // selectedStatus is Pending Approval
      this.displayHomeInformation.status.push(this.getStatus('091'));
      this.displayHomeInformation.status.push(this.getStatus('060'));
      this.displayHomeInformation.status.push(this.getStatus('061'));
      this.displayHomeInformation.status.push(this.getStatus('080'));
    } else if (selectedStatus === '070') { // selectedStatus is Closed
      this.displayHomeInformation.status.push(this.getStatus('091'));
      this.displayHomeInformation.status.push(this.getStatus('070'));
    } else if (selectedStatus === '062') { // selectedStatus is Court-Ordered
      this.displayHomeInformation.status.push(this.getStatus('060'));
      this.displayHomeInformation.status.push(this.getStatus('062'));
      this.displayHomeInformation.status.push(this.getStatus('061'));
      this.displayHomeInformation.status.push(this.getStatus('080'));
    } else if (selectedStatus === '092') { // selectedStatus is Approved
      this.displayHomeInformation.status.push(this.getStatus('092'));
      this.displayHomeInformation.status.push(this.getStatus('060'));
      this.displayHomeInformation.status.push(this.getStatus('061'));
      this.displayHomeInformation.status.push(this.getStatus('080'));
    } else {
      this.displayHomeInformation.status.push(this.getStatus('091'));
      this.displayHomeInformation.status.push(this.getStatus('060'));
      this.displayHomeInformation.status.push(this.getStatus('061'));
      this.displayHomeInformation.status.push(this.getStatus('080'));
    }

  }

  updateDisableFlags() {
    const selectedStatus = this.displayHomeInformation.resource.faHomeStatus;
    if (selectedStatus === '080') { // selectedStatus is Pending Closure
      this.disableFieldsOnPendingClosure();
    } else if (selectedStatus === '061') { // selectedStatus is Pend Court Ordered
      this.disableAllFields();
    } else if (selectedStatus === '060') { // selectedStatus is Pending Approval
      this.disableAllFields();
    } else if (selectedStatus === '070') { // selectedStatus is Closed
      this.disableAllFields();
      this.disableCloseHome();
      this.hideSave = true;
      this.hideSaveSubmit = true;
      this.isPendClosure = true;
    }
  }

  getStatus(value) {
    return this.unfilteredStatusOptions.find(stausValue => stausValue.code === value);
  }

  disableFieldsOnPendingClosure() {
    // artf266912 : if pending closure approval task is rejected then enable all fields for modification
    if (this.disableValidation || !this.displayHomeInformation.rejectClosure) {
      this.disableAllFields();
      this.disableCloseHome();
    }

    FormUtils.disableFormControlStatus(this.homeInfoForm, ['status']);

    this.isPendClosure = true;
    // if the home was rejected enable the status and also enable Save button
    if (this.displayHomeInformation.eventStatus === 'COMP' && this.displayHomeInformation.rejectClosure) {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['status']);
      this.hideSaveSubmit = true;
      this.homeInfoForm.patchValue({ closureReason: '' });
      this.homeInfoForm.patchValue({ involuntaryClosure: '' });
      this.homeInfoForm.patchValue({ recommendReopening: '' });
    } else {
      if (this.displayHomeInformation.eventStatus === 'COMP') {
        this.enableCloseHome();
      }
      else {
        this.hideSave = true;
        this.hideSaveSubmit = true;
      }
    }
  }

  disableAllFieldsAndButtons() {
    FormUtils.disableFormControlStatus(this.homeInfoForm, ['status']);
    this.disableAllFields();
    this.disableCloseHome();
    // this.hideApproveButton = true;
    this.hideSave = true;
    this.hideSaveSubmit = true;
    this.hideAddressAndPhoneButtons = true;
  }

  disableAllFields() {
    this.disableHomeDemographicsFields();
    this.disableKinshipInformation();
    this.disableCaregiverInformation();
    this.disablePaymentInformation();
    this.hideAddressAndPhoneButtons = true;
    this.disableAddressAndPhoneSingleSelect = true;

  }

  disableHomeDemographicsFields() {
    FormUtils.disableFormControlStatus(this.homeInfoForm,
      ['ethnicity', 'language', 'religion', 'annualIncome', 'maritalStatus', 'sourceInquiry']);
  }

  disableKinshipInformation() {
    FormUtils.disableFormControlStatus(this.homeInfoForm,
      ['relativeCaregiver', 'ficitiveCaregiver', 'personCount', 'isCaregiverPaternal', 'isCaregiverMaternal']);
  }

  disableCaregiverInformation() {
    FormUtils.disableFormControlStatus(this.homeInfoForm,
      ['signedAgreement', 'incomeQualified', 'manualGiven', 'isPlacementCourtOrdered', 'courtOrderedPlacementDate', 'allKinEmployed']);
  }

  disablePaymentInformation() {
    FormUtils.disableFormControlStatus(this.homeInfoForm,
      ['signedAgreement', 'agreementSignedDate', 'isPaymentCourtOrdered', 'courtOrderedPaymentDate', 'hasBegunTraining']);
  }

  disableCloseHome() {
    FormUtils.disableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening']);
  }

  enableCloseHome() {
    FormUtils.enableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening']);
  }

  hideOrShowDateFields() {
    if (!this.homeInfoForm.get('isPlacementCourtOrdered').value) {
      this.hidePlacementOrderedDate = true;
    }
    if (!this.homeInfoForm.get('isPaymentCourtOrdered').value) {
      this.hidePaymentOrderedDate = true;
    }
    if (!this.homeInfoForm.get('agreementSignedDate').value) {
      this.hideAgreementSignedDate = true;
    }
  }

  reloadReject() {
    if (this.displayHomeInformation.rejectClosure) {

      const initialState = {
        title: 'Home Information',
        message: 'Closure of home is not approved. Do you still wish to close the home?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result) {
          this.homeInfoForm.get('closureReason').setValue('');
          this.homeInfoForm.get('recommendReopening').setValue('');
          this.hideSaveSubmit = false;
          this.hideSave = false;
        } else {
          this.pendingClosure();
        }
      });
    }
  }

  pendingClosure() {
    const initialState = {
      title: 'Home Information',
      message: `Home is pending closure.
        If you no longer wish to close the home, select a new status for the home and save the page.`,
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      // artf266912 : this flag should be false in order to save the user updates
      this.disableValidation = false;
      this.disableFieldsOnPendingClosure();
    });
  }

  save() {
    this.hideAttentionMessage = true;
    this.homeInfoForm.patchValue({ buttonClicked: 'save' })
    if (!this.disableValidation) {
      this.saveHomeInformation(false, false);
    }
  }

  submit() {
    this.hideAttentionMessage = true;
    this.homeInfoForm.patchValue({ buttonClicked: 'submit' })

    this.criminalHistoryPendingAlert();

    if (this.homeInfoForm.get('status').value === '080') {
      this.isAlert = true;
      this.paidInvoiceAlert();
      this.closingSummaryAlert();
      if (this.displayHomeInformation && this.displayHomeInformation.hasOpenPlacements) {
        this.openPlacementsAlert();
      }
    }
    if (this.displayHomeInformation && this.displayHomeInformation.hasIndLegalCountyMatch === false) {
      this.isAlert = true;
      this.legalCountyMatchAlert();
    }
    if (!this.isAlert) {
      this.saveHomeInformation(true, false);
    }
  }

  saveAndAssign() {
    this.homeInfoForm.patchValue({ buttonClicked: 'assign' });
    this.saveHomeInformation(false, true);
  }

  saveHomeInformation(isSubmit: boolean, isAssign: boolean) {
    this.clearFormErrors();
    if (this.validateFormGroup(this.homeInfoForm)) {
      const payload = Object.assign(this.displayHomeInformation.resource, this.homeInfoForm.value);
      this.displayHomeInformation.resource = payload;
      this.displayHomeInformation.resource.annualIncome =
        Number(Math.round(this.homeInfoForm.controls.annualIncome.value || 0.00).toFixed(2));
      this.displayHomeInformation.resource.recommendReopen =  this.homeInfoForm.controls.recommendReopening.value;
      payload.faHomeStatus = this.homeInfoForm.controls.status.value;
      const request = {
        resource: { ...payload },
      }
      this.caseService.saveKinHomeInformation(request, isSubmit, isAssign).subscribe(
        response => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          this.isAlert = false;
          if (isSubmit) {
            window.location.href = this.environmentSettings.impactP2WebUrl +
              '/case/kin-home-information/displayToDoDetail?cacheKey=' + response.cacheKey + '&status=' +
              this.displayHomeInformation.resource.faHomeStatus;
          } else if (isAssign) {
            window.location.href = this.environmentSettings.impactP2WebUrl +
              '/home/assigment?cacheKey=' + response.cacheKey;
          } else {
            this.router.navigate(['/case/home-information/KIN/' + this.displayHomeInformation.resource.resourceId])
              .then(() => {
                window.location.reload();
              });
          }
        }
      );
    }
  }

  clearFormErrors() {
    Object.keys(this.homeInfoForm.controls).forEach((controlName: string) => {
      if (this.homeInfoForm.get(controlName).invalid) {
        this.homeInfoForm.get(controlName).clearValidators();
        this.homeInfoForm.get(controlName).updateValueAndValidity();
      }
    });

  }

  loadCareGiverEligibilityHistory() {
    this.tableColumn = [
      { field: 'createdDate', header: 'Created Date', sortable: false, width: 150 },
      { field: 'createdBy', header: 'Created By', sortable: false, width: 150 },
      { field: 'kinEligibilityDecode', header: 'Eligibility Status', sortable: false, width: 150 },
      { field: 'assessmentValid', header: 'Placement Approved', sortable: false, width: 175, isTick: true },
      { field: 'assessmentStatusDecode', header: 'Home Assessment Status', sortable: false, width: 150 },
      { field: 'assessmentApproveDate', header: 'Home Assessment Signature Date', sortable: false, width: 175 },
      { field: 'courtOrderedPlacementDate', header: 'Court Ordered Date', sortable: false, width: 110 },
    ];

    this.careGiverEligibilityDataTable = {
      tableBody: this.displayHomeInformation.careEligibilityHistories,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false
    };
  }

  loadPaymentEligibilityHistory() {
    this.tableColumn = [
      { field: 'caregiverName', header: 'Caregiver\'s Name', sortable: false, width: 150 },
      { field: 'childFullName', header: 'Child\'s Name', sortable: false, width: 200 },
      { field: 'childAge', header: 'Child\'s Age', sortable: false, width: 150 },
      { field: 'legalStatusDecode', header: 'Legal Status', sortable: false, width: 175 },
      { field: 'paymentEligibilityStatus', header: 'Qualifies For Payment', sortable: false, width: 100, isTick: true },
      { field: 'courtOrderedPaymentStartDate', header: 'Payment Start Date', sortable: false, width: 175 },
      { field: 'signedAgreement', header: 'Agreement Signed', sortable: false, width: 110, isTick: true },
      { field: 'paymentCourtOrdered', header: 'Payment Court-Ordered', sortable: false, width: 125, isTick: true },
      { field: 'incomeQualified', header: 'Income-Qualified', sortable: false, width: 150, isTick: true },
      { field: 'trainingBegun', header: 'Begun Training', sortable: false, width: 100, isTick: true },
      { field: 'lastUpdatedDate', header: 'Updated Date', sortable: false, width: 110 },
      { field: 'updatedBy', header: 'Updated By', sortable: false, width: 150 }
    ];

    this.paymentEligibilityDataTable = {
      tableBody: this.displayHomeInformation.paymentEligibilityHistories,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false
    };
  }

  loadAddressListDetails() {
    this.tableColumn = [
      {
        field: 'addressType',
        header: 'Type',
        width: 30,
        isLink: (this.displayHomeInformation.pageMode === 'EDIT' && !this.isPendClosure),
        url: '/case/home-information/KIN/:resourceId/address/:resourceAddressId',
        urlParams: ['resourceId', 'resourceAddressId'],
      },
      { field: 'vendorId', header: 'Vendor ID', isHidden: false, isLink: false, width: 50 },
      { field: 'attention', header: 'Attention', isHidden: false, isLink: false, width: 50 },
      { field: 'address', header: 'Address', isHidden: false, isLink: false, width: 100 },
      { field: 'countyName', header: 'County', isHidden: false, isLink: false, width: 50 },
      { field: 'comments', header: 'Comments', isHidden: false, isLink: false, isTick: true, width: 50 },
    ];
    this.addressListDatatable = {
      tableColumn: this.tableColumn,
      isSingleSelect: (this.displayHomeInformation.pageMode === 'EDIT' && !this.isPendClosure) &&
        !this.disableAddressAndPhoneSingleSelect,
      isPaginator: false,
    };
    if (this.displayHomeInformation && this.displayHomeInformation.resource.resourceAddress) {
      this.addressListDatatable.tableBody = this.displayHomeInformation.resource.resourceAddress;
      if (this.addressListDatatable.tableBody && this.addressListDatatable.tableBody.length) {
        this.addressListDatatable.tableBody.forEach(addressData => {
          addressData.address = `${addressData.addressLine1 || ''} ${addressData.addressLine2 || ''}
          ${addressData.city || ''} ${addressData.state || ''}, ${addressData.zip || ''}`
        })
      }
    }
    const localDataAddress = JSON.parse(localStorage.getItem('addressList'));
    if (localDataAddress && localDataAddress.focusItem) {
      this.isAddress = true;
      setTimeout(() => {
        const element: any = document.querySelector(`[href$='${localDataAddress.focusItem}']`);
        if (element) {
          element.focus();
          const copyLocalData = { ...localDataAddress };
          copyLocalData.focusItem = '';
          localStorage.setItem('addressList', JSON.stringify(copyLocalData));
        }
      }, 1000)
    }
    const addressDetail = JSON.parse(localStorage.getItem('addressDetailSave'));
    if (addressDetail) {
      this.isAddress = true;
    }
    localStorage.removeItem('addressDetailSave');
    localStorage.removeItem('addressList');
  }

  loadPhoneListDetails() {
    this.tableColumn = [
      {
        field: 'phoneType',
        header: 'Type',
        width: 50,
        isLink: (this.displayHomeInformation.pageMode === 'EDIT' && !this.isPendClosure),
        url: '/case/home-information/KIN/:resourceId/phone/:resourcePhoneId',
        urlParams: ['resourceId', 'resourcePhoneId'],
      },
      { field: 'phoneNumber', header: 'Phone', isHidden: false, isLink: false, width: 50 },
      { field: 'phoneExtension', header: 'Ext', isHidden: false, isLink: false, width: 50 },
      { field: 'comments', header: 'Comments', isHidden: false, isLink: false, isTick: true, width: 50 },
    ];
    this.phoneListDatatable = {
      tableColumn: this.tableColumn,
      isSingleSelect: (this.displayHomeInformation.pageMode === 'EDIT' && !this.isPendClosure) &&
        !this.disableAddressAndPhoneSingleSelect,
      isPaginator: false,
    };
    if (this.displayHomeInformation && this.displayHomeInformation.resource.resourcePhone) {
      this.phoneListDatatable.tableBody = this.displayHomeInformation.resource.resourcePhone;
    }

    if (this.displayHomeInformation && this.displayHomeInformation.resource.resourcePhone) {
      this.phoneListDatatable.tableBody = this.displayHomeInformation.resource.resourcePhone;
      if (this.phoneListDatatable.tableBody && this.phoneListDatatable.tableBody.length) {
        this.phoneListDatatable.tableBody.forEach(phoneData => {
          phoneData.phoneNumber = FormUtils.formatPhoneNumber(phoneData.phoneNumber);
        })
      }
    }

    const localDataPhone = JSON.parse(localStorage.getItem('phoneList'));
    if (localDataPhone && localDataPhone.focusItem) {
      this.isPhone = true;
      setTimeout(() => {
        const element: any = document.querySelector(`[href$='${localDataPhone.focusItem}']`);
        if (element) {
          element.focus();
          const copyLocalData = { ...localDataPhone };
          copyLocalData.focusItem = '';
          localStorage.setItem('phoneList', JSON.stringify(copyLocalData));
        }
      }, 1000)
    }
    const phoneDetial = JSON.parse(localStorage.getItem('phoneDetailSave'));
    if (phoneDetial) {
      this.isPhone = true;
    }
    localStorage.removeItem('phoneDetailSave');
    localStorage.removeItem('phoneList')
  }

  selectedAddress(event) {
    this.selectedAddressData = [];
    this.selectedAddressData = event;
  }

  deleteAddress() {
    if (this.selectedAddressData.length <= 0) {
      const initialState = {
        title: 'Address List',
        message: 'Please select at least one row to perform this action.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    } else if (this.selectedAddressData && this.selectedAddressData.addressType === 'Primary') {
      const initialState = {
        title: 'Address List',
        message: 'A primary address record may not be deleted.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    } else if (this.selectedAddressData && this.selectedAddressData.vendorId) {
      const initialState = {
        title: 'Address List',
        message: 'An address record with a Vendor ID may not be deleted.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    } else {
      const initialState = {
        title: 'Address List',
        message: 'Are you sure you want to delete the information?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.caseService
            .deleteAddressList(this.selectedAddressData.resourceId, this.selectedAddressData.resourceAddressId)
            .subscribe((res) => {
              this.intializeScreen();
              this.selectedAddressData = [];
            });
        }
      });
    }
  }

  selectedPhone(event) {
    this.selectedPhoneData = [];
    this.selectedPhoneData = event;
  }

  deletePhone() {
    if (this.selectedPhoneData.length <= 0) {
      const initialState = {
        title: 'Phone List',
        message: 'Please select at least one row to perform this action.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    } else if (this.selectedPhoneData && this.selectedPhoneData.phoneType === 'Primary') {
      const initialState = {
        title: 'Phone List',
        message: 'A primary phone record may not be deleted.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    } else {
      const initialState = {
        title: 'Phone List',
        message: 'Are you sure you want to delete the information?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.caseService
            .deletePhoneList(this.selectedPhoneData.resourceId, this.selectedPhoneData.resourcePhoneId)
            .subscribe((res) => {
              this.intializeScreen();
              this.selectedPhoneData = [];
            });
        }
      });
    }
  }

  addAddress() {
    const headerUrl = 'case/home-information/KIN/' + this.displayHomeInformation.resource.resourceId + '/address/0';
    this.router.navigate([headerUrl]);
  }

  addPhone() {
    const headerUrl = 'case/home-information/KIN/' + this.displayHomeInformation.resource.resourceId + '/phone/0';
    this.router.navigate([headerUrl]);
  }

  criminalHistoryPendingAlert() {
    if (this.displayHomeInformation.criminalHistoryPending) {
      if (this.displayHomeInformation.criminalHistoryPersonList.length === 0) {
        this.isAlert = true;
        const initialState = {
          title: 'Home Information',
          message: `A principal on the person list has a pending Criminal History Check that has not yet returned.
          Criminal History Check Results must be returned and accepted or rejected before submitting this Stage for approval.`,
          showCancel: false,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe();
      } else if (this.displayHomeInformation.criminalHistoryPersonList.length > 0) {
        this.isAlert = true;
        const initialState = {
          title: 'Home Information',
          messageList: [
            {
              content: 'Criminal History Check Results page must be completed before submitting this page for approval.',
              url: this.environmentSettings.impactP2WebUrl + '/case/person/records-check-list?cacheKey=' +
                sessionStorage.getItem('cacheKey')
            },
            {
              content: `A principal on the person list has a pending Criminal History Check that has not yet returned.
              Criminal History Check Results must be returned and accepted or rejected before submitting this Stage for approval.`,
              url: ''
            }],
          showCancel: false,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe();
      }
    } else if (this.displayHomeInformation.criminalHistoryPersonList.length > 0) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        messageList: [
          {
            content: 'Criminal History Check Results page must be completed before submitting this page for approval.',
            url: this.environmentSettings.impactP2WebUrl + '/case/person/records-check-list?cacheKey='
              + sessionStorage.getItem('cacheKey')
          }],
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }

  legalCountyMatchAlert() {
    const initialState = {
      title: 'Home Information',
      message: 'The Legal Region does not match the Contract Region, which may impact payment. Please correct before continuing.',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (this.homeInfoForm.get('status').value !== '080' && !this.displayHomeInformation.criminalHistoryPending) {
        this.saveHomeInformation(true, false);
      }
    });
  }

  openPlacementsAlert() {
    const initialState = {
      title: 'Home Information',
      message: 'You have children in placement with this Resource.  Are you sure you want to close the home?',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
  }

  closingSummaryAlert() {
    const initialState = {
      title: 'Home Information',
      message: 'Remember to create Closing Summary before closing the Home.',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
  }

  paidInvoiceAlert() {
    const initialState = {
      title: 'Home Information',
      message: `You have children in placement with this Resource.This may cause an adjustment to a paid invoice.
                Are you sure you want to close the home?`,
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (!this.displayHomeInformation.criminalHistoryPending) {
        this.saveHomeInformation(true, false);
      }
    });
  }

  setVendorId() {
    if (this.displayHomeInformation.resource && this.displayHomeInformation.resource.resourceAddress) {
      this.displayHomeInformation.resource.resourceAddress.forEach(address => {
        if (address.addressType === 'Primary') {
          this.homeInfoForm.patchValue({ vendorId: address.vendorId })
        }
      })
    }
  }

  getForms(): FormValue[] {
    return [{
      formName: 'Kinship Caregiver Contract Request',
      formParams: {
        docType: 'kin10o00',
        docExists: 'false',
        protectDocument: 'false',
        checkForNewMode: 'false',
        idStage: this.displayHomeInformation.resource.resourceFAHomeStageId ?
          String(this.displayHomeInformation.resource.resourceFAHomeStageId) : '',
        idCase: this.displayHomeInformation.resource.caseId ?
          String(this.displayHomeInformation.resource.caseId) : '',
        idResource: this.displayHomeInformation.resource.resourceId ?
          String(this.displayHomeInformation.resource.resourceId) : '',
        idPerson: this.displayHomeInformation.personId ?
          String(this.displayHomeInformation.personId) : ''
      }
    },
    {
      formName: 'Application for Integration Payment',
      formParams: {
        docType: 'kin05o00',
        docExists: 'false',
        protectDocument: 'false',
        checkForNewMode: 'false',
        idStage: this.displayHomeInformation.resource.resourceFAHomeStageId ?
          String(this.displayHomeInformation.resource.resourceFAHomeStageId) : '',
        idCase: this.displayHomeInformation.resource.caseId ?
          String(this.displayHomeInformation.resource.caseId) : '',
        idResource: this.displayHomeInformation.resource.resourceId ?
          String(this.displayHomeInformation.resource.resourceId) : '',
        idPerson: this.displayHomeInformation.personId ?
          String(this.displayHomeInformation.personId) : ''
      }
    },
    {
      formName: 'Disaster Plan',
      formParams: {
        docType: 'cfa19o00',
        docExists: this.displayHomeInformation.disasterPlanDocExists ?
          String(this.displayHomeInformation.disasterPlanDocExists) : '',
        protectDocument: this.displayHomeInformation.protectDocument ?
          String(this.displayHomeInformation.protectDocument) : '',
        postInSameWindow: 'false',
        idStage: this.displayHomeInformation.resource.resourceFAHomeStageId ?
          String(this.displayHomeInformation.resource.resourceFAHomeStageId) : '',
        idResource: this.displayHomeInformation.resource.resourceId ?
          String(this.displayHomeInformation.resource.resourceId) : '',
        idPerson: this.displayHomeInformation.personId ?
          String(this.displayHomeInformation.personId) : '',
        idEventTable: this.displayHomeInformation.disasterPlanEventId ?
          String(this.displayHomeInformation.disasterPlanEventId) : '',
        idResourceTable: this.displayHomeInformation.resource.resourceId ?
          String(this.displayHomeInformation.resource.resourceId) : '',
        idPersonTable: this.displayHomeInformation.disasterPlanPersonId ?
          String(this.displayHomeInformation.disasterPlanPersonId) : '',
        dtTimestamp: this.displayHomeInformation.disasterPlanLastUpdatedDate ?
          String(this.displayHomeInformation.disasterPlanLastUpdatedDate) : '',
        userId: this.displayHomeInformation.personId ?
          String(this.displayHomeInformation.personId) : '',
      }
    }
    ];
  }

  showApproval() {
    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/approval-status/display?cacheKey=' + sessionStorage.getItem('cacheKey');
  }

  @HostListener('window:popstate' || 'window:hashchange', ['$event'])
  onPopState(event) {
    localStorage.setItem('backFrom', 'KinHomeInformation');
  }

}
