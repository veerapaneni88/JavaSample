import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import {
    DataTable,
    DfpsFormValidationDirective,
    DirtyCheck,
    DropDown,
    NavigationService,
    Pagination,
    PaginationUtils,
} from 'dfps-web-lib';
import { LazyLoadEvent } from 'primeng/api/public_api';
import { PaymentApprovalValidators } from './payment-approval.validator';
import {
    PaymentApprovalRes,
    ApprovalStatus,
    PaymentApprovalSaveReq,
} from '@financial/payment-approval/model/PaymentApproval';
import { PaymentService } from '../service/payment.service';

@Component({
    templateUrl: './payment-approval.component.html'
})
export class PaymentApprovalComponent extends DfpsFormValidationDirective implements OnInit {
    DEFAULT_PAGE_SIZE = this.getPageSize();
    currentPageNumber = 0;
    tableBody: any[];
    tableColumn: any[];
    dataTable: DataTable;
    paymentApprovalRes: PaymentApprovalRes;
    paymentApprovalPagination: Pagination;
    sortField: any;
    sortOrder: any;
    paymentApprovalForm: FormGroup;

    constructor(
        private formBuilder: FormBuilder,
        private paymentService: PaymentService,
        private navigationService: NavigationService,
        private route: Router,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Payment Approval');
        const backFrom = localStorage.getItem('backFrom');
        this.createForm();
        if (backFrom === 'InvoiceHeader') {
            this.initializeDataTable();
            this.loadDataLazy(null);
            localStorage.removeItem('backFrom');
        } else {
            this.currentPageNumber = 0;
            localStorage.removeItem('backFrom');
            localStorage.removeItem('checkedItems');
            localStorage.removeItem('PaymentApprovalObj');
            localStorage.removeItem('paymentApprovalLink');
            this.initializeDataTable();
            this.loadDataLazy(null);
        }
    }

    createForm() {
        this.paymentApprovalForm = this.formBuilder.group({
            paymentApprovals: [[], PaymentApprovalValidators.validateRowSelect()],
        });
    }

    initializeDataTable() {
        this.tableColumn = [
            { field: 'approved', header: 'A', sortable: true, width: 50 },
            { field: 'resourceName', header: 'Resource Name', sortable: true, width: 200 },
            { field: 'contractId', header: 'Agency Account ID', sortable: true, width: 120 },
            {
                field: 'invoiceId',
                header: 'Invoice ID',
                desc: null,
                width: 120,
                handleClick: true,
                url: '/financial/invoice/header/:invoiceId',
                urlParams: ['invoiceId'],
                uniqueLinkKey: 'invoiceId'
            },
            { field: 'phase', header: 'Phase', width: 80 },
            { field: 'receivedDate', header: 'Received Date', desc: null, width: 100 },
            { field: 'approvalDate', header: 'Approval Date', desc: null, width: 100 },
            { field: 'validAmount', header: 'Validated Amount', isCurrency: true, desc: null, width: 120 },
            { field: 'personId', header: 'Person ID', desc: null, width: 100 },
            { field: 'lastUpdatedDate', header: 'Last Updated Date', isHidden: true, width: 1 },
        ];
    }

    displayDataTable() {
        const checkedItems = JSON.parse(localStorage.getItem('checkedItems'));
        this.dataTable = {
            tableBody: this.paymentApprovalRes.paymentApprovals,
            tableColumn: this.tableColumn,
            isMultiSelect: true,
            isPaginator: true,
            displaySelectAll: false,
            totalRows: this.paymentApprovalRes.totalElements,
            selectedRows: checkedItems
        };
        const paymentApprovalLink = JSON.parse(localStorage.getItem('paymentApprovalLink'));
        const pageSizeData = JSON.parse(localStorage.getItem('PaymentApprovalObj_Pagesize'));
        const pageTimeOut = (pageSizeData === 500) ? 3000 : 1000;
        if (paymentApprovalLink) {
            setTimeout(() => {
                const element: any = document.querySelector('#id' + paymentApprovalLink.invoiceId);
                if (element) {
                    element.focus();
                    const copyLocalData = { ...paymentApprovalLink };
                    copyLocalData.invoiceId = '';
                    localStorage.setItem('paymentApprovalLink', JSON.stringify(copyLocalData));
                }
            }, pageTimeOut)
        }
    }

    loadDataLazy(event: LazyLoadEvent) {
        const paymentApprovalPagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(
                event,
                this.paymentApprovalRes ? this.paymentApprovalRes.sortColumns : '',
                'approved'
            ),
            sortDirection: event === null ? 'DESC' : PaginationUtils.getSortOrder(event),
        };
        const paymentApprovalCopy: any = JSON.parse(localStorage.getItem('PaymentApprovalObj'));
        if (paymentApprovalCopy) {
            if (event) {
                paymentApprovalCopy.pageNumber = PaginationUtils.getPageNumber(event);
                paymentApprovalCopy.pageSize = (paymentApprovalCopy.pageSize === null)
                    ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event);
                paymentApprovalCopy.sortField = PaginationUtils.getSortField(
                    event,
                    this.paymentApprovalRes ? this.paymentApprovalRes.sortColumns : '',
                    'approved'
                );
                paymentApprovalCopy.sortDirection =  event === null ? 'DESC' : PaginationUtils.getSortOrder(event),
                this.paymentApprovalPagination = paymentApprovalCopy;
                this.loadPaymentApproval(paymentApprovalCopy);
            } else {
                this.DEFAULT_PAGE_SIZE = this.getPageSize();
                this.currentPageNumber = paymentApprovalCopy.pageNumber * this.getPageSize();
                this.sortField = this.getSortField(paymentApprovalCopy.sortField);
                this.sortOrder = paymentApprovalCopy.sortDirection === 'ASC' ? 1 : -1;
                this.paymentApprovalPagination = paymentApprovalCopy;
                this.loadPaymentApproval(paymentApprovalCopy);
            }
        } else {
            this.paymentApprovalPagination = paymentApprovalPagination;
            this.loadPaymentApproval(paymentApprovalPagination);
        }
        if (this.dataTable) {
            localStorage.setItem('checkedItems', JSON.stringify(this.dataTable.selectedRows));
        }
    }

    loadPaymentApproval(paymentApprovalPagination: any) {
        this.paymentService.loadPaymentApprovals(paymentApprovalPagination).subscribe((response) => {
            localStorage.setItem('PaymentApproval_SortFields', JSON.stringify(response.sortColumns));
            this.paymentApprovalRes = response;
            this.displayDataTable();
        });
        localStorage.removeItem('PaymentApprovalObj');
        localStorage.setItem('PaymentApprovalObj', JSON.stringify(paymentApprovalPagination));
        localStorage.setItem('PaymentApprovalObj_Pagesize', paymentApprovalPagination.pageSize);
    }

    handleInvoiceLinkClick(event) {
        this.route.navigate([event.link]);
        localStorage.setItem('checkedItems', JSON.stringify(this.dataTable.selectedRows));
        localStorage.setItem('paymentApprovalLink', JSON.stringify({
            invoiceId: event.tableRow.invoiceId,
          }));
    }
    selectedRowEvent(event) {
        localStorage.setItem('checkedItems', JSON.stringify(this.dataTable.selectedRows));
    }
    approve() {
        this.savePaymentApproval(ApprovalStatus.Approved);

    }

    disapprove() {
        this.savePaymentApproval(ApprovalStatus.Disapproved);

    }

    reset() {
        this.savePaymentApproval(ApprovalStatus.Pending);
    }

    savePaymentApproval(status: string) {
        this.paymentApprovalForm.setValue({
            paymentApprovals: this.dataTable.selectedRows,
        });
        if (this.validateFormControl(this.paymentApprovalForm.get('paymentApprovals'))) {
            const paymentApprovalSaveReq: PaymentApprovalSaveReq = {
                status,
                paymentApprovals: this.dataTable.selectedRows,
            };
            this.paymentService.savePaymentApprovals(paymentApprovalSaveReq).subscribe((res) => {
                this.loadPaymentApproval(this.paymentApprovalPagination);
                localStorage.removeItem('checkedItems');
            });
        }
        this.validateFormGroup(this.paymentApprovalForm);
    }

    getSortField(value): any {
        return this.getKeyByValue(JSON.parse(localStorage.getItem('PaymentApproval_SortFields')), value)
    }

    getKeyByValue(object, value): any {
        return Object.keys(object).find(key => object[key] === value);
    }

    getPageSize(): any {
        const pageSizeData = localStorage.getItem('PaymentApprovalObj_Pagesize');
        return pageSizeData ? Number(pageSizeData) : 50 as number;
    }
}
