import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentGroupService } from '../service/payment-group.service';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import {
    DfpsFormValidationDirective,
    DirtyCheck,
    NavigationService,
    DropDown,
    DataTable,
    PaginationUtils,
    DfpsConfirmComponent,
    DfpsCommonValidators,
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import {
    PaymentProcessSearch,
    AvailablePaymentGroupResults,
    PaymentProcessResponse,
    PaymentProcessRequest,
    ProcessPrintAndVoidChecksRequest,
    PrintCheckDtoList,
} from '../model/PaymentProcess';
import { PaymentProcessValidators } from './payment-process.validators';
import { PaymentProcessConfirmationComponent } from './payment-process-confirmation/payment-process-confirmation.component';

@Component({
    templateUrl: './payment-process.component.html',
})
export class PaymentProcessComponent extends DfpsFormValidationDirective implements OnInit {
    paymentProcessForm: FormGroup;
    processAchForm: FormGroup;
    voidChecksForm: FormGroup;
    paymentProcessTypes: any;
    accountNums: DropDown[];
    displayPaymentProcessSearchResponse: Observable<PaymentProcessSearch>;
    processACHTotalAmounts = 0;

    tableBody: AvailablePaymentGroupResults[];
    tableColumn: any[];
    urlKey: any[];
    urlPath: string;
    dataTable: DataTable;
    displaySearchResults = false;
    paymentSearchResponse: PaymentProcessResponse;
    paymentSearchRequest: PaymentProcessRequest;
    isPrintCheck: boolean;
    isProcessAch: boolean;
    isRowSelectionDisabled = false;
    isVoidCheck: boolean;
    filteredPaymentSearchResponse: AvailablePaymentGroupResults[];
    selectedPaymentSearchResults: AvailablePaymentGroupResults[] = [];
    showPrintChecksBtn = false;
    infoDivDisplay = false;
    informationalMsgs: string[] = [];
    tableColumn2: any[];
    modifyPrivilege = false;

    DEFAULT_PAGE_SIZE = 25;
    selectionForm: FormGroup;

    searchButtonClicked = false;
    @ViewChild('results') resultsEle: ElementRef;
    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private navigationService: NavigationService,
        private paymentGroupService: PaymentGroupService,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }

    ngOnInit() {
        this.navigationService.setTitle('Search Available Payment Groups');
        this.createForm();
        this.createProcessAchForm();
        this.createVoidChecksForm();
        this.selectionForm = this.formBuilder.group({
            minSelections: [, PaymentProcessValidators.validateRowSelect],
            maxSelections: [, PaymentProcessValidators.validateMaxRowSelect],
        });
        this.paymentGroupService.displayPaymentProcessSearch().subscribe((paymentProcessSearch) => {
            this.accountNums = paymentProcessSearch.accountIds.map((account) => ({ code: account, decode: account }));
        });

        this.paymentProcessTypes = [
            { label: 'Print Check', value: '1' },
            { label: 'Process ACH', value: '2' },
            { label: 'Void Check', value: '3' },
        ];

        this.paymentProcessChange();
        this.paymentProcessForm.get('processPaymentType').setValue('1');
    }

    createForm() {
        this.paymentProcessForm = this.formBuilder.group({
            processPaymentType: ['', [Validators.required]],
            accountNo: ['', [Validators.required]],
            checkNo: ['', [DfpsCommonValidators.validateMaxId]],
            agencyAccountId: ['', [DfpsCommonValidators.validateMaxId]],
            payeePersonId: ['', [DfpsCommonValidators.validateMaxId]],
        });
    }

    clearSelectionErrors() {
        this.selectionForm.get('minSelections').setErrors({});
        this.selectionForm.get('maxSelections').setErrors({});
        this.processAchForm.get('achNumber').setErrors({});
        this.processAchForm.get('achDate').setErrors({});
        this.voidChecksForm.get('unprocessedCheckNo').setErrors({});
        this.voidChecksForm.get('voidDate').setErrors({});
        this.validateFormControl(this.selectionForm.get('minSelections'));
        this.validateFormControl(this.selectionForm.get('maxSelections'));
        this.validateFormGroup(this.processAchForm);
        this.validateFormGroup(this.voidChecksForm);
    }

    paymentProcessChange() {
        const checkNumberControl = this.paymentProcessForm.get('checkNo');
        this.paymentProcessForm.get('processPaymentType').valueChanges.subscribe((data) => {
            this.clearSelectionErrors();
            this.DEFAULT_PAGE_SIZE = 25;
            this.displaySearchResults = false;
            this.infoDivDisplay = false;
            this.selectedPaymentSearchResults = [];
            if (this.dataTable) {
                this.dataTable.selectedRows = [];
            }
            this.isPrintCheck = data === '1';
            this.isProcessAch = data === '2';
            this.isVoidCheck = data === '3';
            if (data === '1' || data === '2') {
                checkNumberControl.setValue('');
                checkNumberControl.disable();
            } else {
                checkNumberControl.enable();
            }
        });
    }

    doSearch() {
        this.clearSelectionErrors();
        this.displaySearchResults = false;
        this.infoDivDisplay = false;
        if (this.validateFormGroup(this.paymentProcessForm)) {
            this.isRowSelectionDisabled = false;
            this.showPrintChecksBtn = false;
            this.paymentSearchRequest = this.paymentProcessForm.getRawValue();
            this.initializeDataTable();
            this.loadDataLazy(null);
            this.dataTable.isPaginator = true;
            this.DEFAULT_PAGE_SIZE = 25;
        }
    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
    }

    initializeDataTable() {
        this.tableColumn = [
            {
                field: 'paymentGroupId',
                header: 'Payment Group ID',
                isLink: true,
                sortable: true,
                width: 170,
                url: '' + '/financial/payments/search/payment-group/:paymentGroupId',
                urlParams: ['paymentGroupId'],
            },
            { field: 'payeeName', header: 'Payee Name', sortable: true, width: 250 },
            {
                field: 'paymentMode',
                header: 'Payment Mode',
                width: 150,
            },
            { field: 'status', header: 'Status', width: 100 },
            { field: 'amount', header: 'Amount', isCurrency: true, width: 100 },
            {
                field: 'agencyAccountId',
                header: 'Agency Account ID',
                sortable: true,
                width: 170,
            },
            { field: 'dateIssued', header: 'Date Issued', width: 150 },
        ];
        const checkNumber = { field: 'checkNumber', header: 'Check#', isEditable: false, width: 100 };
        this.tableColumn2 = this.tableColumn.slice();
        this.tableColumn2.splice(3, 0, checkNumber);

        this.dataTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: false,
            isPaginator: true,
        };
    }

    displayDataTable() {
        this.dataTable = {
            tableBody: this.paymentSearchResponse.availablePaymentGroupResults,
            tableColumn: this.tableColumn,
            isPaginator: true,
            isMultiSelect: true,
            totalRows: this.paymentSearchResponse.totalElements,
            selectedRows: this.selectedPaymentSearchResults,
        };
    }

    selectedTableData(data) {
        this.processACHTotalAmounts = 0;
        this.selectedPaymentSearchResults = data;
        this.selectedPaymentSearchResults.forEach((selectedData) => {
            this.processACHTotalAmounts += selectedData.amount;
        });

        const selections = this.selectedPaymentSearchResults.length;
        this.selectionForm.setValue({
            minSelections: selections,
            maxSelections: selections,
        });
        this.dataTable.selectedRows = this.selectedPaymentSearchResults;
        if (data.length) {
            setTimeout(() => {
                this.validateFormControl(this.selectionForm.get('minSelections'));
                if (this.isPrintCheck) {
                    this.validateFormControl(this.selectionForm.get('maxSelections'));
                }
            }, 0);
        }
    }

    loadDataLazy(event) {
        const paymentSearchPagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
            sortField: event ? event.sortField : '',
            sortDirection: PaginationUtils.getSortOrder(event),
        };
        if (this.paymentSearchRequest && !this.showPrintChecksBtn) {
            this.searchPayment(paymentSearchPagination);
        }
    }

    searchPayment(paymentSearchPagination) {
        this.searchButtonClicked = true;
        this.paymentGroupService
            .searchPaymentProcess(Object.assign(paymentSearchPagination, this.paymentSearchRequest))
            .subscribe((response: PaymentProcessResponse) => {
                this.paymentSearchResponse = response;
                this.modifyPrivilege = response.modifyPrivilege;

                this.displaySearchResults = true;
                this.displayDataTable();
                this.dataTable.tableColumn = this.isVoidCheck ? this.tableColumn2 : this.tableColumn;

                // Toggle Process ACH and Void Check form containers based on search results
                const { processPaymentType } = this.paymentProcessForm.value;
                if (!response.availablePaymentGroupResults.length) {
                    this.dataTable.isPaginator = false;
                    if (processPaymentType === '2') {
                        this.isProcessAch = false;
                    }
                    if (processPaymentType === '3') {
                        this.isVoidCheck = false;
                    }
                } else {
                    this.dataTable.isPaginator = true;
                    if (processPaymentType === '2') {
                        this.isProcessAch = true;
                    }
                    if (processPaymentType === '3') {
                        this.isVoidCheck = true;
                    }
                }
                if (this.searchButtonClicked) {
                    setTimeout(() => {
                        this.resultsEle.nativeElement.focus();
                    }, 0);
                }
            });
    }

    createProcessAchForm() {
        this.processAchForm = this.formBuilder.group({
            achNumber: ['', PaymentProcessValidators.validateACHNumber],
            achDate: ['', [DfpsCommonValidators.validateDate,
                PaymentProcessValidators.validateDate,
                PaymentProcessValidators.validateACHDate]],
        });
    }

    createVoidChecksForm() {
        this.voidChecksForm = this.formBuilder.group({
            unprocessedCheckNo: ['', [PaymentProcessValidators.validateCheckNumber, DfpsCommonValidators.validateMaxId]],
            voidDate: ['', [DfpsCommonValidators.validateDate,
            PaymentProcessValidators.validateDate,
            PaymentProcessValidators.validatevoidDate]],
        });
    }

    assignCheckNumber() {
        this.infoDivDisplay = false;
        const minSelectionsControl = this.selectionForm.get('minSelections');
        const maxSelectionsControl = this.selectionForm.get('maxSelections');
        if (this.validateFormControl(minSelectionsControl) && this.validateFormControl(maxSelectionsControl)) {
            this.paymentGroupService
                .assignCheckNumber(this.paymentProcessForm.get('accountNo').value)
                .subscribe((data) => {
                    if (data) {
                        this.dataTable.tableBody = this.selectedPaymentSearchResults.map((payment, index) => {
                            payment.checkNumber = data + index;
                            return payment;
                        });
                        this.DEFAULT_PAGE_SIZE = 50;
                        this.dataTable.totalRows = this.selectedPaymentSearchResults.length;
                        this.dataTable.selectedRows = this.dataTable.tableBody;
                        this.isRowSelectionDisabled = true;
                        this.dataTable.isPaginator = false;
                        this.dataTable.tableColumn = this.tableColumn2.map((column) => {
                            if (column.field === 'checkNumber') {
                                column.isEditable = true;
                                column.width = 200;
                                column.maxLength = 10;
                            }
                            return column;
                        });
                        this.showPrintChecksBtn = true;
                    }
                });
        }
    }

    generatePrintChecksAndVoidChecksReq() {
        const printCheckDtoList: PrintCheckDtoList[] = this.selectedPaymentSearchResults.map(
            (data): PrintCheckDtoList => {
                return {
                    paymentGroupId: data.paymentGroupId, warrantNbr: data.checkNumber,
                    lastUpdatedDate: data.lastUpdatedDate, status: data.status
                };
            }
        );
        const printChecksRequest: ProcessPrintAndVoidChecksRequest = {
            accountNumber: this.paymentProcessForm.value.accountNo,
            processPaymentType: this.paymentProcessForm.value.processPaymentType,
            printCheckDtoList,
        };
        return printChecksRequest;
    }

    printChecks() {
        this.infoDivDisplay = false;
        this.informationalMsgs = [];
        const initialState = {
            title: 'Print Checks Confirmation',
            message: 'Do you want to process selected Check(s)?',
            showCancel: true,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
            if (result) {
                const printChecksRequest = this.generatePrintChecksAndVoidChecksReq();
                this.paymentGroupService.processPrintChecks(printChecksRequest).subscribe((response) => {
                    this.displaySearchResults = false;
                    this.paymentSearchResponse = response;
                    this.isRowSelectionDisabled = false;
                    this.tableColumn = this.tableColumn.map((column) => {
                        if (column.field === 'checkNumber') {
                            column.isHidden = true;
                            column.isEditable = false;
                            column.width = 100;
                        }
                        return column;
                    });
                    this.displaySearchResults = true;
                    this.displayDataTable();
                    this.infoDivDisplay = true;
                    this.showPrintChecksBtn = false;
                    this.informationalMsgs.push('Selected check(s) have been processed.');
                });
            }
        });
    }

    processSelectedACH() {
        this.infoDivDisplay = false;
        this.informationalMsgs = [];
        const minSelectionsControl = this.selectionForm.get('minSelections');
        if (this.validateFormGroup(this.processAchForm) && this.validateFormControl(minSelectionsControl)) {
            const initialState = {
                title: 'Process Selected ACH',
                message: `Do you want to process selected ACH(s)?`,
                showCancel: true,
                selectedValues: this.selectedPaymentSearchResults,
                totalAmount: this.processACHTotalAmounts,
            };
            const modal = this.modalService.show(PaymentProcessConfirmationComponent, {
                class: 'modal-md',
                initialState,
            });
            (modal.content as PaymentProcessConfirmationComponent).isClose.subscribe((result) => {
                if (result) {
                    const { processPaymentType, accountNo } = this.paymentProcessForm.value;
                    const processACHRequest = {
                        processPaymentType,
                        accountNumber: accountNo,
                        ...this.processAchForm.value,
                    };
                    processACHRequest.availablePaymentGroupResults = this.selectedPaymentSearchResults;
                    this.paymentGroupService.processSelectedACH(processACHRequest).subscribe((response) => {
                        this.displaySearchResults = false;
                        this.paymentSearchResponse = response;
                        this.infoDivDisplay = true;
                        this.informationalMsgs.push('ACH reference stored for selected payment groups.');
                        this.createProcessAchForm();
                        setTimeout(() => {
                            this.displaySearchResults = true;
                            this.displayDataTable();
                        }, 0);
                    });
                }
            });
        }
    }

    voidSelectedChecks() {
        this.infoDivDisplay = false;
        this.informationalMsgs = [];
        const minSelectionsControl = this.selectionForm.get('minSelections');
        if (this.validateFormControl(minSelectionsControl)) {
            const initialState = {
                title: 'Void Selected Checks',
                message: `Are you sure you want to VOID selected checks(s)?`,
                showCancel: true,
                selectedValues: this.selectedPaymentSearchResults,
                totalAmount: this.processACHTotalAmounts,
                isVoidCheck: true,
            };
            const modal = this.modalService.show(PaymentProcessConfirmationComponent, {
                class: 'modal-md',
                initialState,
            });
            (modal.content as PaymentProcessConfirmationComponent).isClose.subscribe((result) => {
                if (result) {
                    const voidChecksRequest = this.generatePrintChecksAndVoidChecksReq();
                    this.paymentGroupService.processPrintChecks(voidChecksRequest).subscribe((response) => {
                        this.displaySearchResults = false;
                        this.paymentSearchResponse = response;
                        this.infoDivDisplay = true;
                        this.informationalMsgs.push('Selected Checks have been voided.');
                        setTimeout(() => {
                            this.displaySearchResults = true;
                            this.displayDataTable();
                        }, 0);
                    });
                }
            });
        }
    }

    voidUnprocessedCheck() {
        this.infoDivDisplay = false;
        this.informationalMsgs = [];
        if (this.validateFormGroup(this.voidChecksForm)) {
            const { processPaymentType, accountNo } = this.paymentProcessForm.value;
            const voidChecksRequest = { processPaymentType, accountNumber: accountNo, ...this.voidChecksForm.value };
            this.paymentGroupService.voidUnprocessedChecks(voidChecksRequest).subscribe((response) => {
                this.displaySearchResults = false;
                this.infoDivDisplay = true;
                this.informationalMsgs.push('The requested unprocessed Check has been voided.');
                this.createVoidChecksForm();
                setTimeout(() => {
                    this.displaySearchResults = true;
                    this.displayDataTable();
                }, 0);
            });
        }
    }
    clear() {
        window.location.reload();
    }
}
