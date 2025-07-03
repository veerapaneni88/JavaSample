import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MonthlyRequestExtension, PaymentInformationRes } from '@case/model/case';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsFormValidationDirective,
  DirtyCheck,
  ENVIRONMENT_SETTINGS,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import { CaseService } from './../service/case.service';
import { PaymentInfoValidators } from './payment-information.validator';

@Component({
  selector: 'payment-information',
  templateUrl: './payment-information.component.html'
})
export class PaymentInformationComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  paymentInfoForm: FormGroup;
  paymentInformationRes: PaymentInformationRes;
  monthlyExtension: MonthlyRequestExtension;
  caregiverPaymentDataTable: DataTable;
  monthlyPaymentDataTable: DataTable;
  monthlyExtensionDataTable: DataTable;
  annualReimbursementDataTable: DataTable;
  caregiverTableColumn: any[];
  monthlyExtensionTableColumn: any[];
  monthlyPaymentTableColumn: any[];
  annualReimbursementTableColumn: any[];
  serviceStartDate: any;
  selectedMonthlyExtension: MonthlyRequestExtension;
  bHideApproveButton = false;
  isRedirectedFromApproval = false;

  constructor(
    private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private caseService: CaseService,
    private route: ActivatedRoute,
    private cookieService: CookieService,
    public store: Store<{ dirtyCheck: DirtyCheck }>,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
  ) {
    super(store);
  }
  ngOnInit(): void {
    this.navigationService.setTitle('Payment Information');
    this.route.queryParams.subscribe((param) => {
      this.isRedirectedFromApproval = (param.action === 'approval');
      this.bHideApproveButton = !this.isRedirectedFromApproval;
    });
    this.createForm();
    this.initializeScreen();
    this.initializeCareGiverDataTable();
    this.initializeExtensionDataTable();
    this.initializeAnnualPaymentDataTable();
    this.initializeMonthlyPaymentDataTable();
  }
  createForm() {
    this.paymentInfoForm = this.formBuilder.group({
      startDate: [''],
      goodCause: [''],
      comments: [''],
      childName: [''],
      isValidated: [true],
      paymentEligibilityValidated: [true],
      paymentEligibility: [''],
      latestTermDate: [''],
      serviceEndDate: ['']
    },
      {
        validators: PaymentInfoValidators.validateRequestExtn()
      }
    );
  }

  doRequestExtension() {
    if (this.validateFormGroup(this.paymentInfoForm)) {
      this.paymentInformationRes.monthlyExtension = this.paymentInfoForm.value;
      this.paymentInformationRes.monthlyExtension.childId = this.paymentInformationRes.monthlyExtension.childName;
      this.caseService.requestPaymentExtension(this.paymentInformationRes.resourceId,
        this.paymentInformationRes.monthlyExtension).subscribe(
          response => {
            this.paymentInformationRes.monthlyExtension = response;
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));

            const cacheKey = this.environmentSettings.environmentName === 'Local'
              ? this.cookieService.get('cacheKey') : localStorage.getItem('cacheKey');
            sessionStorage.setItem('attentionMessages', JSON.stringify(this.paymentInformationRes.monthlyExtension.attentionMessages));
            window.location.href = this.environmentSettings.impactP2WebUrl +
              '/case/payment-information/displayToDoDetail?cacheKey=' + cacheKey;

          }
        );
    }
  }

  initializeScreen() {
    const resourceId = this.route.snapshot.paramMap.get('resourceId') ?
      this.route.snapshot.paramMap.get('resourceId') : '0';
      this.caseService.getKinPaymentInfo(resourceId).subscribe((response) => {
      this.paymentInformationRes = response;
      this.paymentInformationRes.resourceId = resourceId;
      this.bHideApproveButton = !this.paymentInformationRes.submittedForApproval;
      if (this.paymentInformationRes) {

        this.displayCareGiverDataTable();
        this.displayMonthlyExtensionsDataTable();
        this.displayMonthlyPaymentDataTable();
        this.displayAnnualPaymentDataTable();

        this.paymentInfoForm.patchValue({
          startDate: this.paymentInformationRes.monthlyExtension.startDate,
          goodCause: this.paymentInformationRes.monthlyExtension.goodCause,
          childName: this.paymentInformationRes.monthlyExtension.childId?.toString(),
          comments: this.paymentInformationRes.monthlyExtension.comments,
          paymentEligibility: this.paymentInformationRes.kinPaymentEligibility,

        });
      }
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
  }

  displayCareGiverDataTable() {
    this.caregiverPaymentDataTable = {
      tableBody: this.paymentInformationRes.careGiverPayments,
      tableColumn: this.caregiverTableColumn,
      isMultiSelect: false,
      isPaginator: true,
    };
  }

  displayMonthlyExtensionsDataTable() {
    this.monthlyExtensionDataTable = {
      tableBody: this.paymentInformationRes.monthlyExtensions,
      tableColumn: this.monthlyExtensionTableColumn,
      isMultiSelect: false,
      isPaginator: true,
    };
  }

  displayMonthlyPaymentDataTable() {
    this.monthlyPaymentDataTable = {
      tableBody: this.paymentInformationRes.kinMonthlyPayments,
      tableColumn: this.monthlyPaymentTableColumn,
      isMultiSelect: false,
      isPaginator: true,
    };
  }

  displayAnnualPaymentDataTable() {
    this.annualReimbursementDataTable = {
      tableBody: this.paymentInformationRes.annualReimbursements,
      tableColumn: this.annualReimbursementTableColumn,
      isMultiSelect: false,
      isPaginator: true,
    };
  }

  initializeCareGiverDataTable() {
    this.caregiverTableColumn = [
      { field: 'childName', header: 'Child Name', width: 150 },
      { field: 'totalAmountRequested', header: 'Total Amount Req', isCurrency: true, width: 200 },
      { field: 'totalAmountUsed', header: 'Total Amount Used', sortable: false, isCurrency: true, width: 150 },
      { field: 'totalUnitsRequested', header: 'Total Units Req', sortable: false, desc: null, width: 150 },
      { field: 'totalUnitsUsed', header: 'Total Units Used', sortable: false, desc: null, width: 150 },
      { field: 'totalUnitsRemaining', header: 'Total Units Remaining', sortable: false, desc: null, width: 150 },
      { field: 'totalAmountRemaining', header: 'Total Amount Remaining', sortable: false, isCurrency: true, width: 150 },
      { field: 'tanfStartDate', header: 'TANF Start', sortable: false, desc: null, width: 150 },
      { field: 'tanfTermDate', header: 'TANF Term', sortable: false, desc: null, width: 150 },
      { field: 'nonTanfStartDate', header: ' Non-TANF Start', sortable: false, desc: null, width: 150 },
      { field: 'nonTanfTermDate', header: 'Non-TANF Term', sortable: false, desc: null, width: 150 },
      { field: 'childId', isHidden: true },
    ];
    this.caregiverPaymentDataTable = {
      tableColumn: this.caregiverTableColumn,
    };
  }

  initializeMonthlyPaymentDataTable() {
    this.monthlyPaymentTableColumn = [
      { field: 'childName', header: 'Child Name', width: 150 },
      { field: 'careGiverName', header: 'Caregiver Name', width: 150 },
      { field: 'serviceMonth', header: 'Service Month', sortable: false, width: 120 },
      { field: 'serviceYear', header: 'Service Year', sortable: false, desc: null, width: 150 },
      { field: 'totalUnitsRequested', header: 'Units Requested', sortable: false, desc: null, width: 150 },
      { field: 'totalAmountRequested', header: 'Amount Requested', isCurrency: true, sortable: false, desc: null, width: 150 },
      { field: 'serviceCode', header: 'Payment Type', sortable: false, width: 150 },
      { field: 'paymentStatus', header: 'Payment Status', sortable: false, desc: null, width: 150 },
      { field: 'warrantDate', header: 'Warrant Date', sortable: false, desc: null, width: 150 },
      { field: 'invoiceId', header: 'Invoice Id', sortable: false, desc: null, width: 150 }
    ];
    this.monthlyPaymentDataTable = {
      tableColumn: this.monthlyPaymentTableColumn,
    };
  }
  initializeAnnualPaymentDataTable() {
    this.annualReimbursementTableColumn = [
      { field: 'careGiverName', header: 'Caregiver Name', width: 150 },
      { field: 'childName', header: 'Child Name', width: 150 },
      { field: 'serviceCode', header: 'Service Code', sortable: false, width: 120 },
      { field: 'totalAmountRequested', header: 'Amount Req', sortable: false, isCurrency: true, desc: null, width: 150 },
      { field: 'totalAmountUsed', header: 'Amount Used', sortable: false, isCurrency: true, desc: null, width: 150 },
      { field: 'startDate', header: 'Payment Start Date', sortable: false, width: 150 },
      { field: 'invoiceStatus', header: 'Invoice Status', sortable: false, desc: null, width: 150 }
    ];
    this.annualReimbursementDataTable = {
      tableColumn: this.annualReimbursementTableColumn,
    };
  }

  initializeExtensionDataTable() {
    this.monthlyExtensionTableColumn = [
      { field: 'childName', header: 'Child Name', width: 150 },
      { field: 'careGiverName', header: 'Caregiver Name', width: 150 },
      { field: 'startDate', header: 'Service Start Date', sortable: false, width: 120 },
      { field: 'endDate', header: 'Service End Date', sortable: false, desc: null, width: 150 },
      { field: 'paymentType', header: 'Payment Type', sortable: false, desc: null, width: 150 },
      { field: 'goodCause', header: 'Good Cause Reason', sortable: false, desc: null, width: 150 },
      { field: 'eventStatus', header: 'Status', sortable: false, width: 150 },
      { field: 'totalAmountRequested', header: 'Amount Req', sortable: false, isCurrency: true, desc: null, width: 150 },
      { field: 'totalAmountPaid', header: 'Amount Paid', sortable: false, isCurrency: true, desc: null, width: 150 },
      { field: 'totalUnitsRequested', header: 'Units Req', sortable: false, desc: null, width: 150 },
      { field: 'totalUnitsPaid', header: 'Units Paid', sortable: false, desc: null, width: 150 },
      { field: 'comments', header: 'Comments', sortable: false, desc: null, width: 700 },
    ];
    this.monthlyExtensionDataTable = {
      tableColumn: this.monthlyExtensionTableColumn,
    };
  }

  showApproval() {
    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/approval-status/display?cacheKey=' + sessionStorage.getItem('cacheKey');
  }
}
