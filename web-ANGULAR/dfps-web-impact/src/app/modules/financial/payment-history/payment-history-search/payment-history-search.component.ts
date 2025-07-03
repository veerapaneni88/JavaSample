import { Component, OnDestroy, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
    DataTable,
    DfpsCommonValidators,
    DfpsFormValidationDirective,
    DirtyCheck,
    FormValidationError,
    Pagination,
    PaginationUtils,
    SET,
    ERROR_RESET,
    SUCCESS_RESET,
    NavigationService,
} from 'dfps-web-lib';
import { LazyLoadEvent } from 'primeng/api';
import { PaymentHistoryService } from '../service/payment-history.service';
import { InvoiceService } from './../../invoice/service/invoice.service';
import { PaymentHistorySearchRes } from '../model/PaymentHistorySearch';

@Component({
    selector: 'payment-history-search',
    templateUrl: './payment-history-search.component.html',
    styleUrls: ['./payment-history-search.component.css'],
})
export class PaymentHistorySearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    paymentHistorySearchForm: FormGroup;
    validationErrors: FormValidationError[] = [];
    validationErrorsLength = 0;
    searchForData: any;
    DEFAULT_PAGE_SIZE = this.getPageSize();
    searchButtonClicked = false;
    @ViewChild('results') resultsEle: ElementRef;
    constructor(
        private formBuilder: FormBuilder,
        private paymentHistoryService: PaymentHistoryService,
        private navigationService: NavigationService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }

    tableBody: any[];
    tableColumn: any[];
    clientTableColumn: any[];
    paymentHistoryDataTable: DataTable;
    isPaymentHistoryTable = false;
    totalPayments: number;
    currentPageNumber = 0;

    ngOnInit() {
        this.navigationService.setTitle('Payment History Search');
        this.clearServerMsg();
        this.searchForData = [
            { label: 'Client', value: 'Client' },
            { label: 'Resource', value: 'Resource' },
            { label: 'Agency Account ID', value: 'AccountId' },
        ];
        this.createForm();
        if (localStorage.getItem('backFrom') === 'InvoiceHeader') {
            this.doSearchForBack();
        } else {
            localStorage.removeItem('PaymentHistory');
            this.paymentHistorySearchForm.controls.searchFor.setValue('Client');
        }
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    }

    createForm() {
        this.paymentHistorySearchForm = this.formBuilder.group(
            {
                fromDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
                toDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
                searchFor: ['', Validators.required],
                searchId: ['', [Validators.required, DfpsCommonValidators.validateMaxId]],
            },
            {
                validators: [DfpsCommonValidators.compareDates('fromDate', 'toDate')],
            }
        );
    }

    initializeDataTable() {
        this.tableColumn = [
            { field: 'warrantDate', header: 'Warrant Date', width: 100 },
            {
                field: 'invoiceId',
                header: 'Invoice ID',
                isLink: true,
                width: 100,
                urlParams: ['invoiceId'],
                url: InvoiceService.HEADER_FRONTEND_URL + '/:invoiceId',
            },
            { field: 'resourceName', header: 'Resource Name', width: 90 },
            { field: 'amount', header: 'Amount', isCurrency: true, width: 60 },
        ];

        this.clientTableColumn = [
            { field: 'warrantDate', header: 'Warrant Date', width: 100 },
            {
                field: 'invoiceId',
                header: 'Invoice ID',
                isLink: true,
                width: 100,
                urlParams: ['invoiceId'],
                url: InvoiceService.HEADER_FRONTEND_URL + '/:invoiceId',
            },
            { field: 'resourceName', header: 'Resource Name', width: 150 },
            { field: 'service', header: 'Svc', width: 50 },
            { field: 'quantity', header: 'Qty', isNumber: true, numberFormat: '1.2-5', width: 50 },
            { field: 'unitType', header: 'UT', width: 50 },
            { field: 'amount', header: 'Amount', isCurrency: true, width: 100 },
            { field: 'rate', header: 'Rate', isCurrency: true, width: 80 },
            { field: 'income', header: 'Income', isCurrency: true, width: 80 },
            { field: 'feePaid', header: 'Fee Paid', isNumber: true, numberFormat: '.1', width: 80 },
            { field: 'from', header: 'From', isNumber: true, width: 80 },
            { field: 'to', header: 'To', isNumber: true, width: 80 },
        ];
    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
    }

    doSearchForBack() {
        this.initializeDataTable();
        localStorage.removeItem('backFrom');
        this.setFormValues(JSON.parse(localStorage.getItem('PaymentHistory')));
        this.clearServerMsg();
        this.isPaymentHistoryTable = false;
        this.loadDataLazy(null);
        this.getTotalPayments();
    }

    searchPaymentHistory() {
        localStorage.removeItem('PaymentHistory')
        this.currentPageNumber = 0;
        this.clearServerMsg();
        this.isPaymentHistoryTable = false;
        if (this.paymentHistorySearchForm.controls.fromDate.value.toString().length === 0) {
            this.paymentHistorySearchForm.controls.fromDate.setErrors({
                required: true
            });
        }
        if (this.validateFormGroup(this.paymentHistorySearchForm)) {
            this.initializeDataTable();
            this.loadDataLazy(null);
            this.getTotalPayments();
        }
    }

    loadDataLazy(event: LazyLoadEvent) {
        const paginationObj: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(event, '', 'warrantDate'),
            sortDirection: PaginationUtils.getSortOrder(event),
        };

        const paymentHistoryCopy: any = JSON.parse(localStorage.getItem('PaymentHistory'));
        if (paymentHistoryCopy) {
            if (event) {
                this.loadTableData(Object.assign(paginationObj, this.paymentHistorySearchForm.value));
            } else {
                this.currentPageNumber = paymentHistoryCopy.pageNumber * this.getPageSize();
                this.loadTableData(paymentHistoryCopy);
            }
        } else {
            this.loadTableData(Object.assign(paginationObj, this.paymentHistorySearchForm.value));
        }
    }

    loadTableData(paymentSearchObj) {
        this.searchButtonClicked = true;
        this.paymentHistoryService
            .getPaymentSearchHistory(paymentSearchObj)
            .subscribe((response: PaymentHistorySearchRes) => {
                localStorage.setItem('PaymentHistory_SortFields', JSON.stringify(response.sortColumns));
                this.paymentHistoryDataTable = {
                    tableBody: response.paymentHistorySearchResults,
                    tableColumn:
                        this.paymentHistorySearchForm.controls.searchFor.value === 'Client'
                            ? this.clientTableColumn
                            : this.tableColumn,
                    isPaginator: true,
                    totalRows: response.totalElements
                };
                this.isPaymentHistoryTable = true;
                if (this.searchButtonClicked) {
                    setTimeout(() => {
                        this.resultsEle.nativeElement.focus();
                    }, 0);
                }
                const paymentHisUrl = JSON.parse(localStorage.getItem('PaymentHistory'));
                if (paymentHisUrl.focusItem) {
                    setTimeout(() => {
                        const element: any = document.querySelector(`[href$='${paymentHisUrl.focusItem}']`);
                        if (element) {
                            element.focus();
                        }
                    }, 1000)
                }
            });
        localStorage.removeItem('PaymentHistory');
        localStorage.setItem('PaymentHistory', JSON.stringify(paymentSearchObj));
        localStorage.setItem('PaymentHistory_PageSize', JSON.parse(paymentSearchObj.pageSize));
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    }

    getTotalPayments() {
        this.totalPayments = 0;
        this.paymentHistoryService
            .getTotalPayments(this.paymentHistorySearchForm.value)
            .subscribe((totalPayments: number) => {
                this.totalPayments = totalPayments;
            });
    }

    clearServerMsg() {
        this.store.dispatch(ERROR_RESET(null));
        this.store.dispatch(SUCCESS_RESET(null));
    }

    ngOnDestroy() {
        this.clearServerMsg();
    }

    clear() {
        localStorage.removeItem('PaymentHistory');
        window.location.reload();
    }

    getSortField(value): any {
        return this.getKeyByValue(JSON.parse(localStorage.getItem('PaymentHistory_SortFields')), value)
    }

    getKeyByValue(object, value): any {
        return Object.keys(object).find(key => object[key] === value);
    }

    getPageSize(): number {
        return localStorage.getItem('PaymentHistory_PageSize')
            ? localStorage.getItem('PaymentHistory_PageSize') as unknown as number : 50 as number;
    }

    getPageNumber(): number {
        const paymentSearchCopy: any = JSON.parse(localStorage.getItem('PaymentHistory'));
        return paymentSearchCopy ? paymentSearchCopy.pageNumber : 0;
    }

    setFormValues(paymentHistory: any) {
        this.paymentHistorySearchForm.controls.searchFor.setValue(paymentHistory.searchFor);
        this.paymentHistorySearchForm.controls.searchId.setValue(paymentHistory.searchId);
        this.paymentHistorySearchForm.controls.fromDate.setValue(paymentHistory.fromDate);
        this.paymentHistorySearchForm.controls.toDate.setValue(paymentHistory.toDate);
    }
}
