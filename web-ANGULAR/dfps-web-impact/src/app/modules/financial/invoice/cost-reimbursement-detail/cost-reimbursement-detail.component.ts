import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    DfpsCommonValidators,
    DfpsFormValidationDirective,
    DirtyCheck,
    NavigationService,
    SET
} from 'dfps-web-lib';
import { CostReimbursement, PageMode } from '../model/CostReimbursement';
import { InvoiceService } from '../service/invoice.service';
import { CostReimbursementDetailValidator } from './cost-reimbursement-detail.validator';

@Component({
    templateUrl: './cost-reimbursement-detail.component.html',
})
export class CostReimbursementDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    costReimbursementDetailForm: FormGroup;
    costReimbursement: CostReimbursement;
    invoiceId: number;
    id: number;
    service: string;
    lineItem: any;
    lineItemServicesQuantity: any;

    constructor(
        private formBuilder: FormBuilder,
        private navigationService: NavigationService,
        private route: ActivatedRoute,
        private router: Router,
        private invoiceService: InvoiceService,
        public store: Store<{ dirtyCheck: DirtyCheck }>,
    ) {
        super(store);
        this.setUserData();
    }

    setUserData() {
        // set user data for 3rd level menu rendering
        this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
        const params = this.route.snapshot.paramMap.get('invoiceId');
        this.navigationService.setUserDataValue('idInvoice', params);
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Cost Reimbursement Detail Invoice');
        this.createForm();
        const routeParams = this.route.snapshot.paramMap;
        if (routeParams) {
            this.invoiceId = +routeParams.get('invoiceId');
            this.id = +routeParams.get('id');
            this.service = routeParams.get('service');
            this.lineItem = routeParams.get('lineItem');
            this.lineItemServicesQuantity = routeParams.get('lineItemServicesQuantity');
        }
        this.getDetail(this.invoiceId, this.id, this.service);
    }

    ngOnDestroy() {
    }

    getDetail(invoiceId: number, costReimbursementId: number, service: string) {
        this.invoiceService.getCostReimbursementDetail(invoiceId, costReimbursementId, service).subscribe((res) => {
            this.costReimbursement = res;
            if (this.id === 0) {
                this.costReimbursement.invoiceId = invoiceId;
            }
            this.loadForm(res);
        });
    }

    saveDetail() {
        if (this.validateFormGroup(this.costReimbursementDetailForm)) {
            const payload = this.costReimbursement;
            payload.salaryExpenditureReimbursement = this.costReimbursementDetailForm.controls.salaries.value;
            payload.supplyExpenditureReimbursement = this.costReimbursementDetailForm.controls.supplies.value;
            payload.travelExpenditureReimbursement = this.costReimbursementDetailForm.controls.travel.value;
            payload.fringeBenefitReimbursement = this.costReimbursementDetailForm.controls.benefits.value;
            payload.equipmentExpenditures = this.costReimbursementDetailForm.controls.equipment.value;
            payload.allocatedAdminCosts = this.costReimbursementDetailForm.controls.administrative.value;
            payload.otherExpenditureReimbursement = this.costReimbursementDetailForm.controls.other.value;
            payload.lineItemServicesQuantity = this.lineItemServicesQuantity;
            payload.lineItemType = this.costReimbursementDetailForm.controls.lineItemType.value;
            this.invoiceService.saveCostReimbursement(payload).subscribe((result) => {
                const crmLocalStorage = JSON.parse(localStorage.getItem('crmData'));
                const dataToSet = crmLocalStorage[this.invoiceId].map((data) => {
                    if (data.uniqueId === this.service + this.lineItem + this.lineItemServicesQuantity) {
                        data.isSaved = true;
                    }
                    return data;
                });
                localStorage.setItem('crmData', JSON.stringify({ [this.invoiceId]: dataToSet }));
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                this.router.navigate([InvoiceService.HEADER_FRONTEND_URL + '/' + this.invoiceId]);
            });
        }
    }

    loadForm(costReimbursement: CostReimbursement) {
        this.costReimbursementDetailForm.setValue({
            salaries: costReimbursement.salaryExpenditureReimbursement
                ? Number(costReimbursement.salaryExpenditureReimbursement).toFixed(2)
                : (0).toFixed(2),
            benefits: costReimbursement.fringeBenefitReimbursement
                ? (costReimbursement.fringeBenefitReimbursement).toFixed(2)
                : (0).toFixed(2),
            travel: costReimbursement.travelExpenditureReimbursement
                ? Number(costReimbursement.travelExpenditureReimbursement).toFixed(2)
                : (0).toFixed(2),
            supplies: costReimbursement.supplyExpenditureReimbursement
                ? Number(costReimbursement.supplyExpenditureReimbursement).toFixed(2)
                : (0).toFixed(2),
            equipment: costReimbursement.equipmentExpenditures
                ? Number(costReimbursement.equipmentExpenditures).toFixed(2)
                : (0).toFixed(2),
            other: costReimbursement.otherExpenditureReimbursement
                ? Number(costReimbursement.otherExpenditureReimbursement).toFixed(2)
                : (0).toFixed(2),
            administrative: costReimbursement.allocatedAdminCosts
                ? Number(costReimbursement.allocatedAdminCosts).toFixed(2)
                : (0).toFixed(2),
            lineItemServicesQuantity: this.lineItemServicesQuantity,
            offsetItemReimbursement: costReimbursement.offsetItemReimbursement,
            lineItemType: this.lineItemServicesQuantity < 0 ? 'R' : costReimbursement.lineItemType,
            adjustment: costReimbursement.adjustment,
            negativeEntriesError: '',
            postiveNegativeEntriesError: '',
            saveError: '',
        });
        this.setPageMode();
        this.costReimbursementDetailForm.setValidators([CostReimbursementDetailValidator.validateInputFields]);
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    }

    setPageMode() {
        if (!this.costReimbursement.pageMode || this.costReimbursement.pageMode === PageMode.VIEW) {
            this.costReimbursementDetailForm.disable();
        }
    }

    createForm() {
        this.costReimbursementDetailForm = this.formBuilder.group({
            salaries: ['', DfpsCommonValidators.validateCurrency(11)],
            benefits: ['', DfpsCommonValidators.validateCurrency(11)],
            travel: ['', DfpsCommonValidators.validateCurrency(11)],
            supplies: ['', DfpsCommonValidators.validateCurrency(11)],
            equipment: ['', DfpsCommonValidators.validateCurrency(11)],
            other: ['', DfpsCommonValidators.validateCurrency(11)],
            administrative: ['', DfpsCommonValidators.validateCurrency(11)],
            lineItemServicesQuantity: [''],
            offsetItemReimbursement: [''],
            adjustment: [''],
            lineItemType: [''],
            negativeEntriesError: [''],
            postiveNegativeEntriesError: [''],
            saveError: []
        },
            {
                validators: [CostReimbursementDetailValidator.validateInputFields]
            }
        );
    }
}
