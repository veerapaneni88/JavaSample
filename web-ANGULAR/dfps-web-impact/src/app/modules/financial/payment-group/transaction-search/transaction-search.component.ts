import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
    DfpsFormValidationDirective,
    NavigationService,
    DirtyCheck,
    FormUtils,
    DropDown,
    DfpsConfirmComponent,
    DataTable,
    Pagination,
    PaginationUtils,
    DfpsCommonValidators,
    SET,
} from 'dfps-web-lib';
import { ActivatedRoute, Router } from '@angular/router';

import { BsModalService } from 'ngx-bootstrap/modal';
import { Store } from '@ngrx/store';
import { PaymentGroupService } from '../service/payment-group.service';
import { LazyLoadEvent } from 'primeng/api';
import { PaymentGroupRes, TransactionSearchRes, TransactionSearchReq, PaymentTransaction } from '../model/PaymentGroup';

@Component({
    templateUrl: './transaction-search.component.html',
})
export class TransactionSearchComponent extends DfpsFormValidationDirective implements OnInit {
    tranSearchForm: FormGroup;
    userName = 'Test';
    paymentReceiptName = 'Name';
    paymentGroupCategory: any;
    paymentModeList: any;
    acctNumList: DropDown[];
    tableBody: any[];
    tableColumn: any[];
    urlKey: any[];
    urlPath: string;
    dataTable: DataTable;
    tranSearchResponse: TransactionSearchRes;
    transactionSearchReq: TransactionSearchReq;
    selectedRow: any;
    paymentGroupRes: PaymentGroupRes;
    paymentGroupId: any;
    VIEW = 'VIEW';
    EDIT = 'EDIT';
    isEdit = false;
    readonly DEFAULT_PAGE_SIZE = 10;
    searchButtonClicked = false;
    @ViewChild('results') resultsEle: ElementRef;
    displaySearchResults = false;
    selectedTrans: PaymentTransaction[];
    OTP = 'oneTimePayment';

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private navigationService: NavigationService,
        public paymentGroupService: PaymentGroupService,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }
    ngOnInit() {
        this.navigationService.setTitle('Transaction Search');
        this.createForm();
        this.paymentGroupCategory = [
            { label: 'Contract', value: 'contract' },
            { label: 'Person', value: 'person' },
            { label: 'One-Time Payment', value: 'oneTimePayment' },
        ];
        this.paymentModeList = [
            { label: 'Check', value: 'CHECK' },
            { label: 'ACH', value: 'ACH' },
        ];
        this.navigationService.setTitle('Transaction Search');
        const routeParams = this.route.snapshot.paramMap;
        if (routeParams) {
            this.paymentGroupId = routeParams.get('paymentGrpId');
        }
        this.intializeScreen(this.paymentGroupId);
    }

    createForm() {
        this.tranSearchForm = this.formBuilder.group({
            categoryCode: [{ value: '', disabled: false }],
            paymentMode: [{ value: '', disabled: false }],
            payeeName: [''],
            contractpersonId: [''],
            accountPersonId: ['', [DfpsCommonValidators.validateMaxId]],
            accountNumber: [''],
            paymentMemo: [''],
        });
    }
    intializeScreen(paymentGroupId: any) {
        this.paymentGroupService.getPaymentGroupDetail(paymentGroupId).subscribe((res) => {
            this.paymentGroupRes = res;
            this.acctNumList = this.paymentGroupService.generateDropDownValues(this.paymentGroupRes.accountNumbers);
            this.setFormValues(this.paymentGroupRes);
            this.setPageModeAttributes(this.paymentGroupRes);
            this.initializeDataTable();
            this.loadDataLazy(null);
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        });
    }

    setPageModeAttributes(paymentGroupRes: PaymentGroupRes) {
        this.tranSearchForm.disable();

        if (paymentGroupRes.pageMode === this.EDIT) {
            this.isEdit = true;
            FormUtils.enableFormControlStatus(this.tranSearchForm, ['accountPersonId']);
        }
    }

    setFormValues(paymentGroupRes: PaymentGroupRes) {
        this.tranSearchForm.setValue({
            categoryCode: paymentGroupRes.finPaymentGroupRes.categoryCode,
            paymentMode: paymentGroupRes.finPaymentGroupRes.paymentMode,
            payeeName: paymentGroupRes.finPaymentGroupRes.payeeName,
            accountNumber: paymentGroupRes.finPaymentGroupRes.accountNumber,
            paymentMemo: paymentGroupRes.finPaymentGroupRes.paymentMemo,
            contractpersonId: paymentGroupRes.finPaymentGroupRes.contractPersonId,
            accountPersonId: '',
        });
    }
    doTranSearch() {
        this.searchButtonClicked = true;
        this.displaySearchResults = false;
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        if (this.validateFormGroup(this.tranSearchForm) && this.tranSearchForm.value) {
            this.initializeDataTable();
            this.loadDataLazy(null);
        }
    }
    loadDataLazy(event: LazyLoadEvent) {
        const tranSearchPagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(
                event,
                this.tranSearchResponse ? this.tranSearchResponse.sortColumns : '',
                ''
            ),
            sortDirection: event === null ? 'ASC' : PaginationUtils.getSortOrder(event),
        };
        this.fetchPaymentTranSearch(tranSearchPagination);
    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
    }

    fetchPaymentTranSearch(tranSearchPagination: any) {
        const SearchReq = {
            accountId: this.paymentGroupRes.finPaymentGroupRes.accountNumber,
            contractId:
                this.paymentGroupRes.finPaymentGroupRes.categoryCode === 'contract'
                    ? this.paymentGroupRes.finPaymentGroupRes.contractPersonId
                    : null,
            personId: this.tranSearchForm.value.accountPersonId ? this.tranSearchForm.value.accountPersonId : null,
        };

        this.paymentGroupService
            .displayPaymentTranSearch(Object.assign(tranSearchPagination, SearchReq))
            .subscribe((res) => {
                this.tranSearchResponse = res;
                this.displayDataTable();
                this.displaySearchResults = true;
                if (this.searchButtonClicked) {
                    setTimeout(() => {
                        this.resultsEle.nativeElement.focus();
                    }, 0);
                }
            });
    }

    initializeDataTable() {
        this.tableColumn = [
            {
                field: 'invoiceId',
                header: 'Invoice ID',
                sortable: true,
                width: 100,
            },
            { field: 'personId', header: 'Person Id', width: 100, sortable: true },
            { field: 'fullName', header: 'Account Name', width: 200, sortable: true },
            {
                field: 'transactionDate',
                header: 'Trans Date',
                width: 100,
                sortable: true,
                isLink: true,
                url: '/financial/financial-account/register/:financialAccountId/:finAcctTransactionId',
                urlParams: ['financialAccountId', 'finAcctTransactionId'],
            },
            { field: 'amount', header: 'Amount', width: 100, isCurrency: true },
            { field: 'type', header: '', width: 50 },
            { field: 'description', header: 'Description', width: 300 },
        ];
        this.dataTable = {
            tableColumn: this.tableColumn,
            isMultiSelect: true,
        };
    }
    displayDataTable() {
        this.selectedTrans = this.dataTable.selectedRows ? this.dataTable.selectedRows : [];
        this.dataTable = {
            tableBody: this.tranSearchResponse.transactionSearchResults,
            tableColumn: this.tableColumn,
            isMultiSelect: true,
            displaySelectAll: false,
            totalRows: this.tranSearchResponse.totalElements,
            selectedRows: [],
            isPaginator: true,
        };
        this.dataTable.selectedRows = this.selectedTrans;
    }
    doCancel() {
        this.router.navigate([
            'financial/payments/search/payment-group/' + this.paymentGroupRes.finPaymentGroupRes.financialPaymentGrpId,
        ]);
    }
    doAddTransToPaymntGrp() {
        this.selectedTrans = this.dataTable.selectedRows;

        // if no trans selected then display message
        if (this.selectedTrans.length === 0) {
            const initialState = {
                title: 'Add Selected To Group',
                message: 'Please select at least one row to perform this action.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                if (result === true) {
                    // Do nothing
                }
            });
            // for OTP, PaymentGroup cannot have more than 1 tran
        } else if (
            this.paymentGroupRes.finPaymentGroupRes.categoryCode === this.OTP &&
            this.selectedTrans.length + this.paymentGroupRes.finPaymentGroupRes.noOfTransactions > 1
        ) {
            const initialState = {
                title: 'Add Selected To Group',
                message: 'You cannot add more than one transaction to this payment group.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                if (result === true) {
                    // Do nothing
                }
            });
            // for non OTP, PaymentGroup cannot have more than 16 tran
        } else if (
            !(this.paymentGroupRes.finPaymentGroupRes.categoryCode === this.OTP) &&
            this.selectedTrans.length + this.paymentGroupRes.finPaymentGroupRes.noOfTransactions > 16
        ) {
            const initialState = {
                title: 'Add Selected To Group',
                message: 'You cannot add more than 16 transactions to this payment group.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                if (result === true) {
                    // Do nothing
                }
            });
        } else {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.addSelectedTransToPaymntGrp(this.selectedTrans);
        }
    }

    addSelectedTransToPaymntGrp(trans: PaymentTransaction[]) {
        this.paymentGroupService
            .addTransToPaymntGrp(this.paymentGroupRes.finPaymentGroupRes.financialPaymentGrpId, trans)
            .subscribe((res) => {
                // financial/payments/search/payment-group/this.paymentGroupRes.finPaymentGroupRes.financialPaymentGrpId'
                this.router.navigate(['financial/payments/search/payment-group/' + res]);
            });
    }
}
