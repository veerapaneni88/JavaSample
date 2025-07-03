import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
  DfpsConfirmComponent,
  DropDown,
  Reports,
  ReportService,
  SET,
  DirtyCheck,
  ApiService,
  NavigationService,
  DfpsFormValidationDirective,
  FormValidationErrorEvent,
  DfpsCommonValidators,
  FormValidationError
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { FinancialAccountValidators } from './financial-account-report.validator';

@Component({
  selector: 'financial-account-report',
  templateUrl: './financial-account-report.component.html',
})
export class FinancialAccountReportComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  financialAcctReportForm: FormGroup;
  regionDropDown: DropDown[];
  reportSubscription: Subscription;
  reports: any;
  validationErrors: FormValidationError[] = [];
  validationErrorsLength = 0;
  constructor(private formBuilder: FormBuilder,
    private apiService: ApiService,
    private navigationService: NavigationService,
    private modalService: BsModalService,
    private reportService: ReportService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idFinancialAccount', '0');
    this.navigationService.setUserDataValue('accountNumber', '0');
  }

  emitValidationErrorEvent(dfpsFormValidationErrorEvent: FormValidationErrorEvent) {
  }

  ngOnInit() {
    this.navigationService.setTitle('Financial Account Report');
    this.createForm();
    this.apiService.get('/v1/financial-accounts/display').subscribe(
      response => {
        this.regionDropDown = response.regions;
        this.financialAcctReportForm.controls.region.setValue(response.region);
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      }
    );
    this.reportSubscription = this.reportService.generateReportEvent.subscribe(data => {
      this.generateReport(data);
    });
    this.reports = this.getReports().map(report => {
      return { ...report, reportParams: JSON.stringify(report.reportParams) };
    });
  }

  ngOnDestroy() {
    this.reportSubscription.unsubscribe();
  }

  createForm() {
    this.financialAcctReportForm = this.formBuilder.group({
      from: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      to: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      region: ['', Validators.required]
    }, {
      validators: [FinancialAccountValidators.validateDateFields]
    });
  }

  getReports(): Reports[] {
    return [
      {
        reportName: 'Monthly Income and Placement',
        reportParams: {
          reportName: 'cfn13x00',
          emailMessage: '',
          paramList: ''
        }
      }, {
        reportName: 'Regional CIA Transaction Report',
        reportParams: {
          reportName: 'cfn21o00',
          emailMessage: '',
          paramList: ''
        }
      }, {
        reportName: 'Termination Listing',
        reportParams: {
          reportName: 'cfn16x00',
          emailMessage: '',
          paramList: ''
        }
      }, {
        reportName: 'Regional CIA Reconciliation',
        reportParams: {
          reportName: 'cfn15o00',
          emailMessage: '',
          paramList: ''
        }
      }, {
        reportName: 'CIA Balance Report',
        reportParams: {
          reportName: 'cfn26o00',
          emailMessage: '',
          paramList: ''
        }
      },
      {
        reportName: 'CIA Adjusting Entries',
        reportParams: {
          reportName: 'cfn93o00',
          emailMessage: '',
          paramList: ''
        }
      }
    ];
  }

  generateReport(reportData: any) {
    if (this.validateFormGroup(this.financialAcctReportForm)) {
      if (this.financialAcctReportForm.get('region').value === '00'
        || this.financialAcctReportForm.get('region').value === '99') {
        const initialState = {
          title: 'Financial Account Report',
          message: 'The selected region cannot be used for the selected report.',
          showCancel: false
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
      } else {
        if (reportData) {
          reportData = JSON.parse(reportData);
          const reportDesc = 'Report for Region: ' + this.financialAcctReportForm.get('region').value;
          const paramList = this.financialAcctReportForm.get('region').value + '^'
            + this.financialAcctReportForm.get('from').value + '^'
            + this.financialAcctReportForm.get('to').value;
          reportData.paramList = paramList;
          reportData.emailMessage = reportDesc;
        }
        this.reportService.callReportService(reportData);
      }
    }
  }
}
