import { Component, OnDestroy, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    DataTable,
    DfpsCommonValidators,
    DfpsConfirmComponent, 
    DfpsFormValidationDirective,
    DirtyCheck,
    ERROR_RESET, 
    NavigationService,
    Pagination,
    PaginationUtils,
    SET,
    SUCCESS_RESET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { PaymentGroupService } from '../service/payment-group.service';
import { PaymentGroupSearchDisplayResponse, PaymentGroupSearchRequest } from './../model/PaymentGroupSearch';
import { PaymentGroupSearchValidators } from './payment-group-search-validators';

@Component({
    templateUrl: './payment-group-search.component.html',
})
export class PaymentGroupSearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    paymentGroupSearchForm: FormGroup;
    displayResponse: PaymentGroupSearchDisplayResponse;
    paymentGroupSearchRequest: PaymentGroupSearchRequest;
    searchPagination: Pagination;
    tableBody: any[];
    tableColumn: any[];
    paymentGroupSearchDataTable: DataTable;
    showPaymentGroupSearchTable = false;
    sortColumns: any;
    modalSubscription: Subscription;
    isSaveSearchRequest = false;
    showDeleteButton = false;
    searchButtonClicked = false;
    @ViewChild('results') resultsEle: ElementRef;

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private modalService: BsModalService,
        private navigationService: NavigationService,
        private paymentGroupService: PaymentGroupService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
        this.navigationService.setUserDataValue('', '');
    }

    ngOnInit() {
        this.navigationService.setTitle('Payment Group Search');
        this.clearServerMsg();
        this.createForm();
        this.initializeScreen();
        this.initializeDataTable();
        if (this.paymentGroupService.getPaymentGroupSearchRequest()) {
            this.paymentGroupSearchRequest = this.paymentGroupService.getPaymentGroupSearchRequest();
            this.paymentGroupSearchForm.patchValue(this.paymentGroupService.getPaymentGroupSearchRequest());
            this.searchPagination = {
                pageNumber: this.paymentGroupSearchRequest.pageNumber,
                pageSize: this.paymentGroupSearchRequest.pageSize,
                sortField: this.paymentGroupSearchRequest.sortField,
                sortDirection: this.paymentGroupSearchRequest.sortDirection,
            };
            this.loadTableData();
        }
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    }

    ngOnDestroy() {
        if (this.isSaveSearchRequest) {
            this.paymentGroupService.setPaymentGroupSearchRequest(
                Object.assign(this.searchPagination, this.paymentGroupSearchForm.value)
            );
        } else {
            this.paymentGroupService.setPaymentGroupSearchRequest(null);
        }
        this.clearServerMsg();
        if (this.modalSubscription) {
            this.modalSubscription.unsubscribe();
        }
    }

    createForm() {
        this.paymentGroupSearchForm = this.formBuilder.group(
            {
                accountNumber: ['', Validators.required],
                agencyAccountId: ['', PaymentGroupSearchValidators.paymentGroupSearchIdPattern],
                personId: ['', PaymentGroupSearchValidators.paymentGroupSearchIdPattern],
                paymentMode: [''],
                status: [''],
                fromDate: ['', [DfpsCommonValidators.validateDate, PaymentGroupSearchValidators.validateFutureDate]],
                toDate: ['', [DfpsCommonValidators.validateDate, PaymentGroupSearchValidators.validateFutureDate]],
            }
        );
    }

    initializeScreen() {
        this.paymentGroupService.displayPaymentGroupSearch().subscribe((response) => {
            this.displayResponse = response;
            if (response.accountNumbers) {
                this.displayResponse.accountNumbers = this.generateDropDownValues(response.accountNumbers);
            }
        });
    }

    generateDropDownValues(dropdownData) {
        return dropdownData.map((data) => ({ code: data, decode: data }));
    }

    searchPaymentGroup() {
        this.clearServerMsg();
        this.showPaymentGroupSearchTable = false;
        PaymentGroupSearchValidators.datesValidations(this.paymentGroupSearchForm);
        if (this.validateFormGroup(this.paymentGroupSearchForm)) {
            this.loadDataLazy();
        }
    }

    initializeDataTable() {
        this.tableColumn = [
            {
                field: 'paymentGroupId',
                header: 'Payment Group ID',
                sortable: true,
                width: 150,
                isLink: true,
                url: '/financial/payments/search/payment-group/:paymentGroupId',
                urlParams: ['paymentGroupId'],
            },
            { field: 'payeeName', header: 'Payee Name', sortable: true, width: 250 },
            { field: 'paymentMode', header: 'Payment Mode', width: 110 },
            { field: 'status', header: 'Status', width: 85 },
            { field: 'checkNumber', header: 'Check ACH No.', sortable: true, width: 140 },
            { field: 'amount', header: 'Amount', isCurrency: true, width: 100 },
            { field: 'agencyAccountId', header: 'Agency Account ID', sortable: true, width: 155 },
            { field: 'issuedDate', header: 'Date Issued', width: 100 },
        ];
        this.paymentGroupSearchDataTable = {
            tableColumn: this.tableColumn,
            isMultiSelect: true,
            isPaginator: true,
        };
    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
      }

    loadDataLazy(event?) {
        this.searchPagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: event ? PaginationUtils.getPageSize(event) : 25,
            sortField: PaginationUtils.getSortField(event, this.sortColumns ? this.sortColumns : '', 'payeeName'),
            sortDirection: PaginationUtils.getSortOrder(event),
        };
        this.loadTableData();
    }

    loadTableData() {
        this.searchButtonClicked = true;
        this.paymentGroupService
            .searchPaymentGroup(Object.assign(this.searchPagination, this.paymentGroupSearchForm.value))
            .subscribe((response: any) => {
                if (response) {
                    this.sortColumns = response.sortColumns;
                    this.showDeleteButton =
                        response.paymentGroups &&
                        Array.isArray(response.paymentGroups) &&
                        response.paymentGroups.length > 0;
                }
                this.paymentGroupSearchDataTable = {
                    tableBody: response ? (response.paymentGroups ? response.paymentGroups : []) : [],
                    tableColumn: this.tableColumn,
                    isPaginator: true,
                    isMultiSelect: true,
                    displaySelectAll: false,
                    selectedRows: [],
                    totalRows: response ? response.totalElements : 0,
                };
                this.showPaymentGroupSearchTable = true;
                if (this.searchButtonClicked) {
                    setTimeout(() => {
                      this.resultsEle.nativeElement.focus();
                    }, 0);
                  }
            });
    }

    accountNumberChanged() {
        this.paymentGroupService.setSelectedAccountNumber(this.paymentGroupSearchForm.controls.accountNumber.value);
    }

    delete() {
        const idList = [];
        this.paymentGroupSearchDataTable.selectedRows.forEach((el) => idList.push(el.paymentGroupId));
        if (idList.length > 0) {
            const initialState = {
                message: 'Are you sure want to delete this information?',
                title: 'Payment Group Search',
                showCancel: true,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
            this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                if (result) {
                    this.paymentGroupService.deletePaymentGroup(idList).subscribe((response: any) => {
                        this.loadTableData();
                    });
                    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                }
            });
        } else {
            const initialState = {
                message: 'Please select at least one row to perform this action.',
                title: 'Payment Group Search',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
            this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe();

        }
    }

    add() {
        this.isSaveSearchRequest = true;
        this.router.navigate(['/financial/payments/search/payment-group/0']);
    }

    clearServerMsg() {
        this.store.dispatch(ERROR_RESET(null));
        this.store.dispatch(SUCCESS_RESET(null));
    }

    clear() {
        window.location.reload();
    }
}
