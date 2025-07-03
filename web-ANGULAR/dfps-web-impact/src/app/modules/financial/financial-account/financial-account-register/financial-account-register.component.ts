import { Component, ElementRef, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { WindowInterruptSource } from '@ng-idle/core';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  NavigationService,
  Reports,
  ReportService
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { FinancialTransactionRes } from '../model/FinancialRegister';
import { FinancialAccountService } from '../service/financial-account.service';
import { FinancialAccountValidators } from './financial-account-register.validator';

@Component({
  templateUrl: './financial-account-register.component.html'
})
export class FinancialAcccountRegisterComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  financialAcccountRegisterForm: FormGroup;
  transactionDatatable: DataTable;

  financialAccountId: any;
  financialTransactionId: any;

  reportSubscription: Subscription;
  reports: any;
  hideReportSection = false;
  hideReconcileAndNewUsingButton = false;
  showAddAndDeleteButton = false;

  tableBody: any[];
  tableColumn: any[];
  financialTransactionRes: FinancialTransactionRes;
  selectedTransactions: any[] = [];

  personName = '';
  isReconciled = false;
  @ViewChild('reconciledIdSpan') reconciledSpanElement: ElementRef;
  isDeleted = false;
  @ViewChild('deletedIdSpan') deletedSpanElement: ElementRef;


  constructor(private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private navigationService: NavigationService,
    private modalService: BsModalService,
    private reportService: ReportService,
    private financialAccountService: FinancialAccountService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    this.financialAccountId = this.route.snapshot.paramMap.get('accountId');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idFinancialAccount', this.financialAccountId);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Financial Account Register');
    this.createForm();
    this.loadTransactionData();
    
  }

  loadTransactionData(){
     this.financialAccountService.getFinancialAccountTransactions(this.financialAccountId).subscribe(
      response => {
        this.financialTransactionRes = response;
        if (this.financialTransactionRes.financialAccount.cpsChkAcct) {
          this.transactionDataTable2();
        } else {
          this.hideReconcileAndNewUsingButton = true;
          this.transactionDataTable1();
        }
        if (this.financialTransactionRes.financialAccount.personProgram === 'APS') {
          this.hideReportSection = true;
        }
        if (this.financialTransactionRes.pageMode === 'EDIT') {
          this.showAddAndDeleteButton = true;
        }
        if (this.isReconciled) {
            setTimeout(() => {
                this.reconciledSpanElement.nativeElement.focus();
            }, 500);
        }
        if (this.isDeleted) {
          setTimeout(() => {
              this.deletedSpanElement.nativeElement.focus();
          }, 500);
      }
        
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
    this.financialAcccountRegisterForm = this.formBuilder.group({
      from: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      to: ['', [Validators.required, DfpsCommonValidators.validateDate]],
    }, {
      validators: [FinancialAccountValidators.validateDateFields]
    });
  }

  transactionDataTable1() {
    this.transactionDatatable = null;
    this.tableColumn = [
      {
        field: 'transactionDate',
        header: 'Transaction Date',
        isLink: true,
        url: '/financial/financial-account/register/:financialAccountId/:id',
        urlParams: ['financialAccountId', 'id'],
        sortable: true,
        width: 150
      },
      { field: 'warrantNumber', header: 'Check No', sortable: true, width: 100 },
      { field: 'reconciled', header: 'R', isTick: true, width: 50 },
      { field: 'category', header: 'Category', width: 200 },
      { field: 'debitAmount', header: 'Debit', isCurrency: true, width: 150 },
      { field: 'creditAmount', header: 'Credit', isCurrency: true, width: 150 },
      { field: 'payeeName', header: 'Payee', sortable: true, width: 200 },
      { field: 'description', header: 'Description', width: 600 },
      { field: 'subcategory', header: 'Sub Category', width: 150 },
      { field: 'workerName', header: 'Modified By', width: 150 }
    ];
    this.transactionDatatable = {
      tableColumn: this.tableColumn,
      isPaginator: true,
      isMultiSelect: true,
      displaySelectAll: false,
    };

    if (this.financialTransactionRes.financialAccountTransactions) {
      this.transactionDatatable.tableBody = this.financialTransactionRes?.financialAccountTransactions;
      this.personName = this.financialTransactionRes.financialAccount.personName;
    }
  }

  transactionDataTable2() {
    this.transactionDatatable = null;
    this.tableColumn = [
      {
        field: 'transactionDate',
        header: 'Transaction Date',
        isLink: true,
        sortable: true,
        isDate: true,
        url: '/financial/financial-account/register/:financialAccountId/:id',
        urlParams: ['financialAccountId', 'id'],
        width: 150
      },
      { field: 'warrantNumber', header: 'Check/ACH No', sortable: true, width: 150 },
      { field: 'category', header: 'Category', width: 200 },
      { field: 'type', header: 'Transaction Type', width: 150 },
      { field: 'amount', header: 'Amount', isCurrency: true, width: 100 },
      { field: 'payeeName', header: 'Payee', sortable: true, width: 200 },
      { field: 'description', header: 'Description', width: 600 },
      { field: 'status', header: 'Status', width: 150 },
      { field: 'workerName', header: 'Modified By', width: 150 },
    ];
    this.transactionDatatable = {
      tableColumn: this.tableColumn,
      isPaginator: true,
      isMultiSelect: true,
      displaySelectAll: false,
    };

    if (this.financialTransactionRes.financialAccountTransactions) {
      this.transactionDatatable.tableBody = this.financialTransactionRes?.financialAccountTransactions;
      this.personName = this.financialTransactionRes.financialAccount.personName;
    }
  }

  setSelectedTransactions(data){
    this.isDeleted = false;
    this.isReconciled = false;
    this.selectedTransactions = data;
  }

  reconciliation() {
    this.isDeleted = false;
    if (this.selectedTransactions.length === 0) {
      this.showModal('Please select at least one row to perform this action.', false, 'reconciliationButtonId');
    } else {
      
      const transactionIds: any[] = [];
      this.selectedTransactions.forEach(transaction => {
        transactionIds.push(transaction.id);
      });
      this.financialAccountService
          .reconcileFinancialAccountTransactions(this.financialAccountId, transactionIds)
          .subscribe((res) => {
              if (res) {
                this.financialTransactionRes = null;
                  this.router.navigate(['/financial/financial-account/register/' + this.financialAccountId], {
                      queryParams: { refresh: new Date().getTime() },
                  });
                  this.isReconciled = true;
                  this.loadTransactionData();
               }
          });
          document.getElementById('reconciliationButtonId').focus();
          this.selectedTransactions = [];
      }
     
  }

  newUsing() {
    this.isReconciled = false;
    this.isDeleted = false;
    if (this.selectedTransactions.length === 0) {
      this.showModal('Please select at least one row to perform this action.', false, 'newUsingButtonId');
    } else if (this.selectedTransactions.length > 1) {
      this.showModal('You must select only one row to perform a New Using.', false, 'newUsingButtonId');
    } else {
      this.router.navigate(['financial/financial-account/register/' + this.financialAccountId + '/0'],
        {
          queryParams: { using: this.selectedTransactions[0].id },
          skipLocationChange: true
        });
    }
  }

  add() {
    this.isReconciled = false;
    this.isDeleted = false;
    this.router.navigate(['financial/financial-account/register/' + this.financialAccountId + '/0']);
  }

  delete() {
    this.isReconciled = false;
    if (this.selectedTransactions.length === 0) {
      this.showModal('Please select at least one row to perform this action.', false, 'deleteButtonId');
    } else {
      const initialState = {
        title: 'Financial Account Register',
        message: 'Are you sure you want to delete?',
        showCancel: true,
        cancelFocusId: 'deleteButtonId'
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md', initialState
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
        if (result === true) {
          
          const transactionIds: any[] = [];
          this.selectedTransactions.forEach(transaction => {
            transactionIds.push(transaction.id);
          });
          this.financialAccountService.deleteFinancialAccountTransactions(this.financialAccountId, transactionIds).subscribe(
            res => {
              if (res) {
                this.financialTransactionRes = null;
                this.router.navigate(['/financial/financial-account/register/' + this.financialAccountId], {
                  queryParams: { refresh: new Date().getTime() }
                });
                this.isDeleted = true;
                this.loadTransactionData();
              }
            }
          );
        }
      });
    }
  }

  showModal(messageText: string, show: boolean, id: string) {
    let userResponse;
    const initialState = {
      title: 'Financial Account Register',
      message: messageText,
      showCancel: show,
      elementId: id
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md', initialState
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
      if (result === true) {
        userResponse = true;
      }
      document.getElementById(initialState.elementId).focus();
    });
    return userResponse;
  }

  getReports(): Reports[] {
    return [
      {
        reportName: 'Child\'s Income History Report',
        reportParams: {
          reportName: 'cfn12o00',
          emailMessage: this.personName,
          paramList: ''
        }
      }, {
        reportName: 'Child\'s Checking Transaction',
        reportParams: {
          reportName: 'cfn11x00',
          emailMessage: this.personName,
          paramList: ''
        }
      }, {
        reportName: 'Savings Transaction Report',
        reportParams: {
          reportName: 'cfn20o00',
          emailMessage: this.personName,
          paramList: ''
        }
      }, {
        reportName: 'Child\'s Invoice History',
        reportParams: {
          reportName: 'cfn17x00',
          emailMessage: this.personName,
          paramList: ''
        }
      },
      {
        reportName: 'Child\'s Placement History Report',
        reportParams: {
          reportName: 'cfn19o00',
          emailMessage: this.personName,
          paramList: ''
        }
      }
    ];
  }

  generateReport(reportData: any) {
    if (this.validateFormGroup(this.financialAcccountRegisterForm)) {
      const isValid: boolean =
        !this.validateDate(this.financialAcccountRegisterForm.get('from').value) &&
        !this.validateDate(this.financialAcccountRegisterForm.get('to').value);
      if (isValid && reportData) {
        reportData = JSON.parse(reportData);
        const reportDesc = 'Report for : ' + this.personName;
        const paramList = this.financialTransactionRes.financialAccount.personId
          + '^'
          + this.financialAcccountRegisterForm.get('from').value
          + '^'
          + this.financialAcccountRegisterForm.get('to').value;
        reportData.paramList = paramList;
        reportData.emailMessage = reportDesc;
      }
      this.reportService.callReportService(reportData);
    }
  }

  validateDate(date: any): boolean {
    const isNotValid: boolean = date && new Date(date) > new Date();
    if (isNotValid) {
      this.showErrorModal('Date must be before or the same as the current date and in the correct format MM/DD/YYYY');
    }
    return isNotValid;
  }

  showErrorModal(errorMessage: string) {
    const initialState = { message: errorMessage, title: 'Report Launch' };
    this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
  }
}
