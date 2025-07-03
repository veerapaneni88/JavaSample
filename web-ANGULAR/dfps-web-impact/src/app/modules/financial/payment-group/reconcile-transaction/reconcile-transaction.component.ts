import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
    DfpsCommonValidators,
    DfpsFormValidationDirective,
    DirtyCheck,
    DfpsConfirmComponent,
    DropDown,
    DataTable,
    SET,
    NavigationService,
} from 'dfps-web-lib';
import { ReconcileTransactionValidators } from './reconcile-transaction-validator';
import { BsModalService } from 'ngx-bootstrap/modal';
import { combineLatest } from 'rxjs';
import {
    ReconcileDisplayResponse,
    ReconciliationSearchRes,
    ReconciliationSearchRequest,
    ReconciliationTransactionReq,
} from '../model/ReconcileTransaction';
import { PaymentGroupService } from '../service/payment-group.service';
import { Router } from '@angular/router';
import { startWith } from 'rxjs/operators';

@Component({
    selector: 'reconcile-transaction',
    templateUrl: './reconcile-transaction.component.html',
})
export class ReconcileTransactionComponent extends DfpsFormValidationDirective implements OnInit {
    reconcileTransactionForm: FormGroup;
    searchButtonClicked = false;
    readonly DEFAULT_PAGE_SIZE = 25;
    displayResponse: ReconcileDisplayResponse;
    accountNos: DropDown[];
    beginReconBalance = 0;
    runningReconBalance = 0;
    reconciliationSearchReq: ReconciliationSearchRequest;
    reconciliationSearchRes: ReconciliationSearchRes;
    displayTable = false;
    reconcileTransactionsTable: DataTable;
    tableBody: any[];
    tableColumn: any[];
    formRefresh = false;
    reconciliationTransactionReq: ReconciliationTransactionReq;
    transactionsSelected: any[] = [];
    infoMessageDisplay = false;
    reconciledTranSuccessMsg: string;
    searchButtonClickedForResults = false;
    @ViewChild('results') resultsEle: ElementRef;

    constructor(
        private formBuilder: FormBuilder,
        private navigationService: NavigationService,
        public store: Store<{ dirtyCheck: DirtyCheck }>,
        private modalService: BsModalService,
        private paymentGroupService: PaymentGroupService,
        private router: Router
    ) {
        super(store);
    }

    ngOnInit() {
        this.navigationService.setTitle('Reconcile Transactions'); 
        this.createForm();
        combineLatest([
            this.reconcileTransactionForm.get('checks').valueChanges.pipe(startWith(false)),
            this.reconcileTransactionForm.get('ach').valueChanges.pipe(startWith(false)),
            this.reconcileTransactionForm.get('deposits').valueChanges.pipe(startWith(false)),
            this.reconcileTransactionForm.get('transfers').valueChanges.pipe(startWith(false)),
        ]).subscribe((data) => {
            if (!this.formRefresh) {
                this.displayTable = false;
                this.refreshSearch();
            }
        });
        this.initializeScreen();
        this.initializeDataTable();
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    }

    createForm() {
        this.reconcileTransactionForm = this.formBuilder.group(
            {
                checks: [false],
                ach: [false],
                deposits: [false],
                transfers: [false],
                accountNo: ['', [Validators.required]],
                checkNo: ['', [Validators.maxLength(10)]],
                achNumber: ['', [Validators.maxLength(15)]],
                agencyAccountId: ['', [DfpsCommonValidators.validateMaxId]],
                beginReconciledBalance: [''],
                payeePersonId: ['', [DfpsCommonValidators.validateMaxId]],
                runningReconciledBalance: [''],
            },
            {
                validators: [ReconcileTransactionValidators.validateAccountNo()],
            }
        );
    }

    initializeScreen() {
        this.paymentGroupService.displayReconcileTransaction().subscribe((response) => {
            this.displayResponse = response;

            if (response.accountNos) {
                this.accountNos = this.generateDropDownValues(response.accountNos);
            }
        });
    }

    doSearch() {
        this.infoMessageDisplay = false;
        this.displayTable = false;
        const { checks, ach, deposits, transfers } = this.reconcileTransactionForm.getRawValue();
        if (!checks && !ach && !deposits && !transfers) {
            this.showModal('You must specify at least one transaction type.', false);
           
            return;
        }
        this.search();
    }

    showModal(text: string, isDisplay: boolean) {
        const initialState = {
            title: 'Reconciliation Transactions Search',
            message: text,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
            class: 'modal-md modal-dialog-centered',
            initialState,
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
            this.displayTable = isDisplay;
            if(!isDisplay){
                this.beginReconBalance = 0;
                this.runningReconBalance = 0;
                this.reconcileTransactionForm.get('checkNo').setValue('');
                this.reconcileTransactionForm.get('achNumber').setValue('');
                this.reconcileTransactionForm.get('agencyAccountId').setValue('');
                this.reconcileTransactionForm.get('payeePersonId').setValue('');
                this.searchButtonClicked = false;
            }
            
        });
    }

    search() {
        this.reconciliationSearchReq = this.reconcileTransactionForm.getRawValue();
        this.reconciliationSearchReq.accountNumber = this.reconcileTransactionForm.get('accountNo').value;
        if (this.validateFormGroup(this.reconcileTransactionForm) && this.reconciliationSearchReq) {
            this.searchButtonClickedForResults = true;
            this.paymentGroupService.searchReconcileTransactions(this.reconciliationSearchReq).subscribe((res) => {
                this.reconciliationSearchRes = res;
                this.beginReconBalance = this.reconciliationSearchRes.reconBalance;
                this.runningReconBalance = this.reconciliationSearchRes.reconBalance;
                this.displayDataTable();
                this.displayTable = true;
                this.searchButtonClicked = true;
                this.transactionsSelected = [];
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                if (this.searchButtonClickedForResults) {
                    setTimeout(() => {
                      this.resultsEle.nativeElement.focus();
                    }, 0);
                  }
            });
        }
    }

    generateDropDownValues(dropdownData) {
        return dropdownData.map((data) => ({ code: data, decode: data }));
    }

    initializeDataTable() {
        this.tableColumn = [
            {
                field: 'dtReconTran',
                header: 'Trans Date/Issue Date',
                handleClick: true,
                url: '/financial/financial-account/register/:financialAccountId/:idReconTran',
                urlParams: ['financialAccountId', 'idReconTran'],
                sortable: true,
                isDate: true,
                width: 100,
            },
            { field: 'reconTransType', header: 'Type', sortable: true, width: 80 },
            { field: 'amountReconTran', header: 'Amount', isCurrency: true, sortable: true, width: 100 },
            { field: 'currencySign', header: '', width: 40 },
            { field: 'payeeName', header: 'Payee Name', sortable: true, width: 200 },
            { field: 'checkOrAchNumber', header: 'Check/ACH No.', width: 100 },
            { field: 'personName', header: 'Account Person', sortable: true, width: 170 },
            { field: 'idPerson', header: 'Account Person ID', sortable: true, width: 100 },
            { field: 'resourceId', header: 'Resource ID', sortable: true, width: 100 },
            { field: 'agencyAccountId', header: 'Agency Account ID', width: 100 },
            { field: 'idReconTran', header: 'Id Recon Tran', isHidden: true, width: 1 },
            { field: 'paymentOrTransInd', header: '', isHidden: true, width: 1 },
            { field: 'financialAccountId', header: '', isHidden: true, width: 1 },
        ];
    }

    searchButtonResultOnBlur() {
        this.searchButtonClickedForResults = false;
      }

    displayDataTable() {
        this.reconcileTransactionsTable = {
            tableColumn: this.tableColumn,
            isPaginator: true,
            isMultiSelect: true,
            selectedRows: [],
        };
        this.reconcileTransactionsTable.tableBody = this.reconciliationSearchRes.reconciliationSearchResults;
    }

    refreshForm() {
        if (this.searchButtonClicked) {
            this.formRefresh = true;
            this.reconcileTransactionForm.get('checks').setValue(false);
            this.reconcileTransactionForm.get('ach').setValue(false);
            this.reconcileTransactionForm.get('deposits').setValue(false);
            this.reconcileTransactionForm.get('transfers').setValue(false);
            this.reconcileTransactionForm.get('checkNo').setValue('');
            this.reconcileTransactionForm.get('achNumber').setValue('');
            this.reconcileTransactionForm.get('agencyAccountId').setValue('');
            this.reconcileTransactionForm.get('payeePersonId').setValue('');
            this.displayTable = false;
            this.beginReconBalance = 0;
            this.runningReconBalance = 0;
            this.searchButtonClicked = false;
            this.formRefresh = false;
            this.reconcileTransactionsTable.tableBody = [];
            this.infoMessageDisplay = false;
        }
    }

    refreshSearch() {
        if (this.searchButtonClicked) {
            this.doSearch();
        }
    }

    handleRouting(event) {
        const showLinkTo = event.tableRow.paymentOrTransInd;
        if (showLinkTo === 'P') {
            const transactionId = event.tableRow.idReconTran;
            this.router.navigate(['/financial/payments/search/payment-group/' + transactionId]);
        } else {
            this.router.navigate([event.link]);
        }
    }

    reconcile() {
        if (this.transactionsSelected.length === 0) {
            this.showModal('Please select at least one row to perform this action.', true);
        } else {
            const reconciliationTransactionReq: ReconciliationTransactionReq = {
                reconcileTransactions: this.transactionsSelected,
            };
            this.paymentGroupService.reconcileTransactions(reconciliationTransactionReq).subscribe((res) => {
                if (res) {
                    this.transactionsSelected = [];
                    this.displayTable = false;
                    this.reconcileTransactionsTable.tableBody = [];
                    this.doSearch();
                    this.reconciledTranSuccessMsg = 'Selected transactions/payments have been reconciled.';
                    this.infoMessageDisplay = true;
                    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                }
            });
        }
    }

    setSelectedRow(event: any[]) {
        this.transactionsSelected = event;
        this.infoMessageDisplay = false;
        this.updateReconRunningBalance();
        this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
    }
    
    updateReconRunningBalance() {
        if (this.transactionsSelected.length === 0) {
            this.runningReconBalance = this.beginReconBalance;
        } else {
            this.runningReconBalance = this.beginReconBalance;
            this.transactionsSelected.forEach((row) => {
                if (row.currencySign === 'CR') {
                    this.runningReconBalance = this.runningReconBalance + row.amountReconTran;
                } else {
                    this.runningReconBalance = this.runningReconBalance - row.amountReconTran;
                }
            });
        }
    }

    clear() {
        window.location.reload();
    }
}
