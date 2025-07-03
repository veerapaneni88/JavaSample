import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
    DfpsFormValidationDirective,
    NavigationService,
    DirtyCheck,
    FormUtils,
    DataTable,
    DropDown,
    DfpsConfirmComponent,
    DfpsCommonValidators
} from 'dfps-web-lib';
import { ActivatedRoute, Router } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Store } from '@ngrx/store';
import { PaymentGroupService } from '../service/payment-group.service';
import { PaymentGrpDetailValidators } from './payment-group-detail-validator';
import { ValidatePaymentGrpReq, SavePaymentGrpRequest } from '../model/PaymentGrpDetail';


@Component({
    templateUrl: './payment-group-detail.component.html'
})
export class PaymentGroupDetailComponent extends DfpsFormValidationDirective implements OnInit {

    paymentGroupForm: FormGroup;
    validatePaymentGrpReq: ValidatePaymentGrpReq;
    savePaymentGrpReq: SavePaymentGrpRequest;

    savePaymentGrpResponse: any;
    paymentGroupCategory: any;
    paymentModeList: any;
    paymentGrpDetails: any;
    newPaymentInfo: any;
    validateFormGrpRes: any;
    reqObj: any;
    acctNumList: DropDown[];
    dataTable: DataTable;
    tableColumn: any = [];
    acctNum: any;
    paymentGrpId: any;
    title: any;
    listOfTransaction: any = [];
    transactionsList: any = [];
    finTransactionTable: boolean;
    hideAddTransactionBtn: boolean;
    hideSaveBtn = true;
    showValidateBtn: boolean;
    showDeleteBtn: boolean;
    isSelected: boolean;
    constructor(private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private navigationService: NavigationService,
        public paymentGroupService: PaymentGroupService,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>) {
        super(store);
    }

    ngOnInit() {
        this.navigationService.setTitle('Payment Group Detail');
        if (this.paymentGroupService.getSelectedAccountNumber()) {
            this.acctNum = this.paymentGroupService.getSelectedAccountNumber();
        } else {
            this.acctNum = '';
        }
        const routeParams = this.route.snapshot.paramMap;
        if (routeParams) {
            this.paymentGrpId = +routeParams.get('paymentGrpId');
        }
        if (this.paymentGrpId === 0) {
            this.title = 'Add Payment Group';
        } else {
            this.title = 'Payment Group Detail';
        }
        this.createForm();
        this.paymentGroupCategory = [
            { label: 'Contract', value: 'contract' },
            { label: 'Person', value: 'person' },
            { label: 'One-Time Payment', value: 'oneTimePayment' },
        ];
        this.paymentModeList = [
            { label: 'Check', value: 'check' },
            { label: 'ACH', value: 'ach' },
        ];
        this.paymentGroupForm.controls.accountNumber.setValue(this.acctNum);
        this.setInitialValues();
        this.getPaymentGrpInfo(this.paymentGrpId);
        this.categoryCodeUpdates();
        this.onChangeAgencyPersonId();
    }

    setInitialValues() {
        this.paymentGroupForm.controls.categoryCode.setValue('contract');
        this.paymentGroupForm.controls.paymentMode.setValue('check');
    }
    createForm() {
        this.paymentGroupForm = this.formBuilder.group({
            categoryCode: [],
            paymentMode: [[Validators.required]],
            payeeName: ['', [Validators.maxLength(30)]],
            contractPersonId: ['', [PaymentGrpDetailValidators.validateAgencyAccountOrPersonId]],
            accountNumber: ['', [Validators.required]],
            paymentReceiptName: [''],
            receiptAddress1: [''],
            receiptAddress2: [''],
            receiptCity: [''],
            receiptState: [''],
            receiptZipCode: [''],
            checkMemo: ['', [Validators.maxLength(80)]],
            checkNumber: ['', [DfpsCommonValidators.validateMaxId]],
            noOfTransactions: [''],
            totalAmount: [''],
            issuedDate: [''],
            paymentGroupStatus: [''],
            financialPaymentGrpId: [''],
            categoryErr: ' ',
            isValidated: [false]
        }, {
            validators: [PaymentGrpDetailValidators.oneTimePaymentCatValidation(),
            PaymentGrpDetailValidators.paymentGrpCategoryValidation()]
        });
    }

    setFormValues() {
        this.paymentGroupForm.setValue({
            categoryCode: this.newPaymentInfo.categoryCode,
            paymentMode: this.newPaymentInfo.paymentMode ? this.newPaymentInfo.paymentMode.toLowerCase() : null,
            payeeName: this.newPaymentInfo.payeeName,
            contractPersonId: this.newPaymentInfo.contractPersonId,
            accountNumber: this.newPaymentInfo.accountNumber ? this.newPaymentInfo.accountNumber : this.acctNum,
            paymentReceiptName: this.newPaymentInfo.paymentReceiptName,
            receiptAddress1: this.newPaymentInfo.receiptAddress1,
            receiptAddress2: this.newPaymentInfo.receiptAddress2,
            receiptCity: this.newPaymentInfo.receiptCity,
            receiptState: this.newPaymentInfo.receiptState,
            receiptZipCode: this.newPaymentInfo.receiptZipCode,
            checkMemo: this.newPaymentInfo.paymentMemo,
            checkNumber: this.newPaymentInfo.checkNumber,
            noOfTransactions: this.newPaymentInfo.noOfTransactions ? this.newPaymentInfo.noOfTransactions : 0,
            totalAmount: this.newPaymentInfo.totalAmount ? this.newPaymentInfo.totalAmount : 0,
            issuedDate: this.newPaymentInfo.issuedDate,
            paymentGroupStatus: this.newPaymentInfo.paymentGroupStatus,
            financialPaymentGrpId: this.newPaymentInfo.financialPaymentGrpId,
            categoryErr: ' ',
            isValidated: false
        });
    }

    categoryCodeUpdates() {
        this.paymentGroupForm.get('categoryCode').valueChanges.subscribe(val => {
            if ((val === 'person' || val === 'contract') && this.paymentGrpDetails.pageMode === 'NEW') {
                this.showValidateBtn = true;
                this.paymentGroupForm.get('isValidated').setValue('false');
                FormUtils.disableFormControlStatus(this.paymentGroupForm, ['payeeName']);
                FormUtils.enableFormControlStatus(this.paymentGroupForm, ['contractPersonId']);
                this.paymentGroupForm.get('payeeName').setValue('');
            } else if (val === 'oneTimePayment') {
                this.showValidateBtn = false;
                this.paymentGroupForm.get('contractPersonId').setValue('');
                FormUtils.disableFormControlStatus(this.paymentGroupForm, ['contractPersonId']);
                FormUtils.enableFormControlStatus(this.paymentGroupForm, ['payeeName']);
            }
        });
    }

    onChangeAgencyPersonId() {
        this.paymentGroupForm.get('contractPersonId').valueChanges.subscribe(val => {
          this.paymentGroupForm.get('isValidated').setValue('false');
      });
    }

    transLinkEnabled(status) {
        return status !== 'Void';
    }

    getPaymentGrpInfo(paymentGrpId) {
        this.paymentGroupService.getPaymentGrpDetails(paymentGrpId).subscribe(res => {
            this.paymentGrpDetails = res;
            this.newPaymentInfo = this.paymentGrpDetails.finPaymentGroupRes;
            this.acctNumList = this.paymentGroupService.generateDropDownValues(this.paymentGrpDetails.accountNumbers);
            this.setFormValues();
            if (this.newPaymentInfo.financialPaymentGrpId === null) {
                this.setInitialValues();
            }
            this.determinePageModes();
            if (this.paymentGrpDetails.finTransactionRes !== null) {
                this.finTransactionTable = true;
                if (this.paymentGrpDetails.pageMode === 'EDIT') {
                    this.isSelected = true;
                    this.initializeDataTable(this.paymentGrpDetails.finTransactionRes,
                        this.isSelected, this.transLinkEnabled(this.paymentGrpDetails.finPaymentGroupRes.paymentGroupStatus));
                } else if (this.paymentGrpDetails.pageMode === 'VIEW') {
                    this.isSelected = false;
                    this.initializeDataTable(this.paymentGrpDetails.finTransactionRes,
                        this.isSelected, this.transLinkEnabled(this.paymentGrpDetails.finPaymentGroupRes.paymentGroupStatus));
                }
            } else {
                this.finTransactionTable = false;
            }
        })
    }

    determinePageModes() {
        if (this.paymentGrpDetails.pageMode === 'EDIT') {
            this.paymentGroupForm.controls.isValidated.setValue('true');
            this.showValidateBtn = false;
            if (this.paymentGrpDetails.finPaymentGroupRes.paymentGroupStatus === 'Pending') {
                FormUtils.disableFormControlStatus(this.paymentGroupForm,
                    ['categoryCode', 'contractPersonId', 'accountNumber',
                        'receiptAddress1', 'receiptAddress2', 'receiptCity', 'receiptState', 'receiptZipCode', 'checkNumber']);
                FormUtils.enableFormControlStatus(this.paymentGroupForm, ['paymentMode', 'payeeName']);
                this.paymentGroupForm.controls.payeeName.setValue(this.paymentGrpDetails.finPaymentGroupRes.payeeName);
                this.hideAddTransactionBtn = true;
                if (this.paymentGrpDetails.finTransactionRes.length > 0) {
                    this.showDeleteBtn = true;
                } else {
                    this.showDeleteBtn = false;
                }
            } else if (this.paymentGrpDetails.finPaymentGroupRes.paymentGroupStatus === 'Processed') {
                if (this.paymentGrpDetails.finPaymentGroupRes.paymentMode === 'CHECK') {
                    FormUtils.enableFormControlStatus(this.paymentGroupForm, ['checkNumber']);
                    FormUtils.disableFormControlStatus(this.paymentGroupForm,
                        ['categoryCode', 'contractPersonId', 'accountNumber',
                            'receiptAddress1', 'receiptAddress2', 'receiptCity', 'receiptState',
                            'receiptZipCode', 'paymentMode', 'payeeName', 'checkMemo']);
                    this.paymentGroupForm.controls.payeeName.setValue(this.paymentGrpDetails.finPaymentGroupRes.payeeName);
                } else {
                    FormUtils.disableFormControlStatus(this.paymentGroupForm,
                        ['categoryCode', 'contractPersonId', 'accountNumber',
                            'receiptAddress1', 'receiptAddress2', 'receiptCity', 'receiptState',
                            'receiptZipCode', 'paymentMode', 'payeeName', 'checkMemo', 'checkNumber']);
                    this.paymentGroupForm.controls.payeeName.setValue(this.paymentGrpDetails.finPaymentGroupRes.payeeName);
                }
            } else {
                FormUtils.disableFormControlStatus(this.paymentGroupForm,
                    ['categoryCode', 'paymentMode', 'contractPersonId', 'accountNumber',
                        'receiptAddress1', 'receiptAddress2', 'receiptCity', 'receiptState', 'receiptZipCode', 'checkNumber']);
            }

        } else if (this.paymentGrpDetails.pageMode === 'VIEW') {
            this.hideAddTransactionBtn = false;
            this.showValidateBtn = false;
            this.hideSaveBtn = false;
            FormUtils.disableFormControlStatus(this.paymentGroupForm,
                ['categoryCode', 'paymentMode', 'payeeName', 'contractPersonId',
                    'accountNumber', 'receiptAddress1', 'receiptAddress2', 'receiptCity',
                    'receiptState', 'receiptZipCode', 'checkNumber', 'checkMemo']);
            this.paymentGroupForm.controls.payeeName.setValue(this.paymentGrpDetails.finPaymentGroupRes.payeeName);
        } else if (this.paymentGrpDetails.pageMode === 'NEW') {
            this.hideAddTransactionBtn = false;
            this.showValidateBtn = true;
            FormUtils.disableFormControlStatus(this.paymentGroupForm,
                ['receiptAddress1', 'receiptAddress2', 'receiptCity', 'receiptState', 'receiptZipCode', 'checkNumber']);
        } else {
            this.hideAddTransactionBtn = false;
            this.showValidateBtn = true;
            FormUtils.disableFormControlStatus(this.paymentGroupForm,
                ['receiptAddress1', 'receiptAddress2', 'receiptCity', 'receiptState', 'receiptZipCode', 'checkNumber']);
        }
    }

    validatePaymentGrpDetails() {
        if (this.paymentGroupForm.value.categoryCode === 'person') {
            if (this.paymentGroupForm.value.contractPersonId === '' || this.paymentGroupForm.value.contractPersonId === null) {
                const initialState = {
                    title: 'Add Payment Group',
                    message: 'You must enter a valid Person ID.',
                    showCancel: false
                };
                const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
                (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
            } else {
                this.paymentGroupForm.controls.isValidated.setValue('true');
                this.reqObj = {
                    categoryCode: this.paymentGroupForm.value.categoryCode,
                    contractPersonId: + this.paymentGroupForm.value.contractPersonId
                }
                this.validatePaymentFormGrp();
            }
        } else if (this.paymentGroupForm.value.categoryCode === 'contract') {
            if (this.paymentGroupForm.value.contractPersonId === '' || this.paymentGroupForm.value.contractPersonId === null) {
                const initialState = {
                    title: 'Add Payment Group',
                    message: 'You must enter a valid Agency Account  ID.',
                    showCancel: false
                };
                const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
                (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
            } else {
                this.paymentGroupForm.controls.isValidated.setValue('true');
                this.reqObj = {
                    categoryCode: this.paymentGroupForm.value.categoryCode,
                    contractPersonId: this.paymentGroupForm.value.contractPersonId
                }
                this.validatePaymentFormGrp();
            }
        }

    }

    validatePaymentFormGrp() {
        if (this.validateFormGroup(this.paymentGroupForm)) {
            this.paymentGroupForm.controls.isValidated.setValue('false');
            this.paymentGroupService.validatePaymentGrpDetails(this.reqObj).subscribe(res => {
                this.newPaymentInfo.paymentReceiptName = res.paymentReceiptName;
                this.newPaymentInfo.payeeName = res.paymentReceiptName;
                this.paymentGroupForm.controls.isValidated.setValue('true');
                this.paymentGroupForm.patchValue({
                    paymentReceiptName: res.paymentReceiptName,
                    payeeName: res.paymentReceiptName,
                    receiptAddress1: res.receiptAddress1,
                    receiptAddress2: res.receiptAddress2,
                    receiptCity: res.receiptCity,
                    receiptState: res.receiptState,
                    receiptZipCode: res.receiptZipCode
                })
            })
        }
    }

    savePaymentGrpDetails() {
        if (this.paymentGrpDetails.pageMode === 'EDIT') {
            this.paymentGroupForm.controls.isValidated.setValue('true');
        }
        const reqBody = {
            financialPaymentGrpId: this.paymentGroupForm.controls.financialPaymentGrpId.value,
            checkNumber: this.paymentGroupForm.controls.checkNumber.value,
            contractPersonId: this.paymentGroupForm.controls.contractPersonId.value,
            categoryCode: this.paymentGroupForm.controls.categoryCode.value,
            paymentReceiptName: this.paymentGroupForm.controls.paymentReceiptName.value,
            noOfTransactions: this.paymentGroupForm.controls.noOfTransactions.value,
            totalAmount: this.paymentGroupForm.controls.totalAmount.value,
            paymentMode: this.paymentGroupForm.controls.paymentMode.value.toUpperCase(),
            paymentMemo: this.paymentGroupForm.controls.checkMemo.value,
            payeeName: this.paymentGroupForm.controls.payeeName.value,
            accountNumber: this.paymentGroupForm.controls.accountNumber.value,
            issuedDate: this.paymentGroupForm.controls.issuedDate.value,
            paymentGroupStatus: this.paymentGroupForm.controls.paymentGroupStatus.value,
            lastUpdatedDate: this.paymentGrpDetails.finPaymentGroupRes.lastUpdatedDate,
            createdDate: this.paymentGrpDetails.finPaymentGroupRes.createdDate
        }
        if (this.validateFormGroup(this.paymentGroupForm)) {
            this.paymentGroupService.savePaymentGrpDetail(reqBody).subscribe(res => {
                this.savePaymentGrpResponse = res;
                if (this.paymentGrpDetails.pageMode === 'NEW') {
                    this.router.navigate(['financial/payments/search/payment-group/'
                        + this.savePaymentGrpResponse.financialPaymentGrpId + '/transaction-search'])
                } else {
                    this.paymentGroupForm.patchValue({
                        financialPaymentGrpId: this.savePaymentGrpResponse.financialPaymentGrpId,
                        checkNumber: this.savePaymentGrpResponse.checkNumber,
                        contractPersonId: this.savePaymentGrpResponse.contractPersonId,
                        categoryCode: this.savePaymentGrpResponse.categoryCode,
                        paymentReceiptName: this.savePaymentGrpResponse.paymentReceiptName,
                        noOfTransactions: this.savePaymentGrpResponse.noOfTransactions,
                        totalAmount: this.savePaymentGrpResponse.totalAmount,
                        paymentMode: this.savePaymentGrpResponse.paymentMode.toLowerCase(),
                        checkMemo: this.savePaymentGrpResponse.paymentMemo,
                        payeeName: this.savePaymentGrpResponse.payeeName,
                        accountNumber: this.savePaymentGrpResponse.accountNumber,
                        issuedDate: this.savePaymentGrpResponse.issuedDate,
                        paymentGroupStatus: this.savePaymentGrpResponse.paymentGroupStatus,
                    });
                    FormUtils.disableFormControlStatus(this.paymentGroupForm,
                        ['contractPersonId']);
                    FormUtils.enableFormControlStatus(this.paymentGroupForm,
                        ['payeeName']);
                    window.location.reload();
                }

            })
        }
    }

    initializeDataTable(list, isSelected, linkEnabled) {
        this.tableColumn = [
            {
                field: 'invoiceId',
                header: 'Invoice ID',
                sortable: true,
                width: 100,
            },
            { field: 'personId', header: 'Person ID', width: 100, sortable: true },
            { field: 'fullName', header: 'Account Name', width: 200, sortable: true },
            {
                field: 'transactionDate',
                header: 'Trans. Date',
                isLink: linkEnabled,
                url: '/financial/financial-account/register/:financialAccountId/:finAcctTransactionId',
                urlParams: ['financialAccountId', 'finAcctTransactionId'],
                width: 100,
                sortable: true
            },
            {
                field: 'amount',
                header: 'Amount',
                width: 100,
                isCurrency: true,
            },
            { field: 'type', header: '', width: 70 },
            { field: 'description', header: 'Description', width: 600 },
        ];
        this.dataTable = {
            tableColumn: this.tableColumn,
            isMultiSelect: isSelected,
            selectedRows: []
        };
        this.dataTable.tableBody = list;
    }

    setSelectedRow(data) {
        this.listOfTransaction = [];
        this.transactionsList = [];
        this.listOfTransaction = data;
    }

    addTransactionToGrp() {
        this.router.navigate(['financial/payments/search/payment-group/'
            + this.newPaymentInfo.financialPaymentGrpId + '/transaction-search'])
    }

    deleteTransaction() {
        if (this.listOfTransaction.length <= 0) {
            const initialState = {
                title: 'Payment Group Detail',
                message: 'Please select at least one row to perform this action.',
                showCancel: false
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
        } else if (this.listOfTransaction.length > 0) {
            this.listOfTransaction.forEach(e => {
                this.transactionsList.push(e.finAcctTransactionId)
            })
            this.paymentGroupService.deleteTransactionFromGrp(this.paymentGrpId, this.transactionsList).subscribe(res => {
                this.listOfTransaction = [];
                this.transactionsList = [];
                this.finTransactionTable = false;
                this.getPaymentGrpInfo(this.paymentGrpId);
            })
        }

    }

}
