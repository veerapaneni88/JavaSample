import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    DataTable,
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    NavigationService,
    SET,
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BudgetTransferValidator } from './budget-transfer.validator';
import { BudgetTransferRes, BudgetTransferReq } from '../model/BudgetTransfer';
import { BudgetTransferService } from '../service/budget-transfer.service';

@Component({
    templateUrl: './budget-transfer.component.html',
    styleUrls: ['./budget-transfer.component.css'],
})
export class BudgetTransferComponent extends DfpsFormValidationDirective implements OnInit {
    budgetTransferForm: FormGroup;
    contractId: any;
    contractNumber: any;
    period: any;
    version: any;
    tableColumn: any[];
    urlKey: any[];
    urlPath: string;
    btDatatable: DataTable;
    urlResponsePaths: string[];
    urlErrorPaths: string[];
    fromId: any;
    toId: any;

    budgetTransferResponse: BudgetTransferRes;
    budgetTransferReq: BudgetTransferReq;

    constructor(
        private formBuilder: FormBuilder,
        private budgetTransferService: BudgetTransferService,
        private route: ActivatedRoute,
        private navigationService: NavigationService,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.contractId = this.route.snapshot.paramMap.get('contractId');
        this.contractNumber = this.route.snapshot.paramMap.get('contractNumber');
        this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
        this.navigationService.setUserDataValue('idContract', this.contractId);
        this.navigationService.setUserDataValue('contractNumber', this.contractNumber);
    }

    createForm() {
        this.budgetTransferForm = this.formBuilder.group(
            {
                amount: ['', [Validators.required, DfpsCommonValidators.validateCurrency(50)]],
                from: [''],
                to: [''],
            },
            {
                validators: [
                    BudgetTransferValidator.validateFromField(),
                    BudgetTransferValidator.validateToField(),
                    BudgetTransferValidator.validateFromToField(),
                    BudgetTransferValidator.validateAmountInvalid(),
                ],
            }
        );
    }

    budgetTransferDataTable(budgetTransferResponse: any) {
        this.tableColumn = [
            { field: 'from', header: 'From', isHidden: false, isLink: false, isRadio: true, emitColumn: 1, width: 50 },
            { field: 'to', header: 'To', isHidden: false, isLink: false, isRadio: true, emitColumn: 2, width: 50 },
            { field: 'lineItem', header: 'CSLI', width: 50 },
            { field: 'serviceCode', header: 'Service', width: 50 },
            { field: 'category', header: 'Category', width: 50 },
            { field: 'totalAmount', header: 'Total Amount', isHidden: false, isCurrency: true, width: 50 },
            { field: 'budgetBalance', header: 'Budget Balance', isHidden: false, isCurrency: true, width: 50 },
        ];
        this.btDatatable = {
            tableColumn: this.tableColumn,
            isSingleSelect: false,
            isPaginator: false,
        };
        if (budgetTransferResponse) {
            this.btDatatable.tableBody = budgetTransferResponse.budgetTransfers;
        }
    }

    ngOnInit() {
        this.navigationService.setTitle('Budget Transfer');
        const routeParams = this.route.snapshot.paramMap;
        if (routeParams) {
            this.contractId = routeParams.get('contractId');
            this.contractNumber = routeParams.get('contractNumber');
            this.version = routeParams.get('version');
            this.period = routeParams.get('period');
        }
        this.createForm();
        this.intializeScreen();
    }

    intializeScreen() {
        this.fromId = null;
        this.toId = null;
        this.budgetTransferForm.get('amount').setValue('');
        this.budgetTransferService
            .getBudgetTransfer(this.contractId, this.period, this.version)
            .subscribe((response) => {
                this.budgetTransferResponse = response;
                this.budgetTransferDataTable(this.budgetTransferResponse);
                this.contractNumber = this.budgetTransferResponse.scorContractNumber;
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            });
    }

    doSaveBudgetTransfer() {
        this.fromId.totalAmount = this.fromId.totalAmount - Number(this.budgetTransferForm.value.amount);
        this.toId.totalAmount = this.toId.totalAmount + Number(this.budgetTransferForm.value.amount);
        this.budgetTransferReq = {
            contractId: this.contractId,
            period: this.period,
            version: this.version,
            budgetTransfers: [this.fromId, this.toId],
        };
        this.budgetTransferService.saveBudgetTransfer(this.budgetTransferReq).subscribe((response) => {
            this.createForm();
            this.intializeScreen();
        });
    }

    doTransfer() {
        const amountValue = (document.getElementById('amount') as HTMLInputElement).value;
        this.budgetTransferForm.setValue({
            from: this.fromId,
            to: this.toId,
            amount: amountValue,
        });
        if (this.validateFormGroup(this.budgetTransferForm)) {
            const initialState = {
                title: 'Budget Transfer',
                message: 'Modified budgets may require a contract amendment. Continue? ',
                showCancel: true,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                if (result === true) {
                    this.doSaveBudgetTransfer();
                } else {
                    this.createForm();
                    this.intializeScreen();
                }
            });
        }
    }
}
