import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    DropDown,
    FormUtils,
    NavigationService,
    SET,
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { combineLatest } from 'rxjs';
import { DeliveredServiceDetail } from '../model/DeliveredService';
import { InvoiceService } from '../service/invoice.service';
import { DeliveredServiceValidators } from './delivered-service.validator';

@Component({
    selector: 'delivered-service',
    templateUrl: './delivered-service.component.html',
    styleUrls: [],
})
export class DeliveredServiceComponent extends DfpsFormValidationDirective implements OnInit {
    deliveredServiceForm: FormGroup;
    deliveredService: DeliveredServiceDetail;
    currentDate = new Date();
    isUnitRateReadOnly = false;
    invoiceId: string;
    deliveredServiceId: string;
    previousItemTotal: string;

    hideSaveButton = true;
    hideserviceAuthDetailAndDates = true;
    isNewUsingMode = false;
    infoDivDisplay = false;
    informationalMsgs: string[] = [];

    constructor(
        private formBuilder: FormBuilder,
        private invoiceService: InvoiceService,
        private modalService: BsModalService,
        private route: ActivatedRoute,
        private router: Router,
        private navigationService: NavigationService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.setUserData();
    }

    setUserData() {
        // set user data for 3rd level menu rendering
        const params = this.route.snapshot.paramMap.get('invoiceId');
        this.navigationService.setUserDataValue('idInvoice', params);
    }

    createForm() {
        this.deliveredServiceForm = this.formBuilder.group({
            personId: ['', [Validators.required, DfpsCommonValidators.validateMaxId]],
            month: ['', [Validators.required, DfpsCommonValidators.validateMonth]],
            year: ['', [Validators.required, DfpsCommonValidators.validateYear]],
            service: ['', Validators.required],
            county: ['', Validators.required],
            unitType: ['', Validators.required],
            rate: [
                '0.00',
                [DeliveredServiceValidators.validatePositiveNumbers(7), DfpsCommonValidators.validateCurrency(7)],
            ],
            quantity: ['0.00', [DfpsCommonValidators.validateCurrency(8)]],
            feePaid: ['', DfpsCommonValidators.validateCurrency(11)],
            validate: [false],
            serviceAuthDetailId: [''],
            itemTotal: [''],
            id: [],
            lastUpdatedDate: [],
            saveErrors: [false],
            adjustmentInvoiceError: [false]
        });
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Delivered Service Detail');
        this.createForm();
        const routeParams = this.route.snapshot.paramMap;
        const routeQueryParams = this.route.snapshot.queryParamMap;
        const usingId = routeQueryParams.get('using');
        this.isNewUsingMode = !!usingId;
        if (this.isNewUsingMode) {
            this.invoiceId = routeParams.get('invoiceId');
            this.deliveredServiceId = usingId;
        } else if (routeParams) {
            this.invoiceId = routeParams.get('invoiceId');
            this.deliveredServiceId = routeParams.get('deliveredServiceId');
        }
        combineLatest([
            this.deliveredServiceForm.get('personId').valueChanges,
            this.deliveredServiceForm.get('month').valueChanges,
            this.deliveredServiceForm.get('year').valueChanges,
            this.deliveredServiceForm.get('service').valueChanges,
            this.deliveredServiceForm.get('county').valueChanges,
        ]).subscribe((data) => {
            this.deliveredServiceForm.get('validate').setValue(false);
        });

        combineLatest([
            this.deliveredServiceForm.get('rate').valueChanges,
            this.deliveredServiceForm.get('quantity').valueChanges,
            this.deliveredServiceForm.get('feePaid').valueChanges,
        ]).subscribe((data) => {
            this.calculateAndSetItemTotal();
        });
        this.intializeScreen();
    }

    intializeScreen() {
        this.invoiceService.getDeliveredService(this.invoiceId, this.deliveredServiceId).subscribe((response) => {
            this.deliveredService = response;
            this.loadService();
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        });
    }

    transformDropdown(data: DropDown[]) {
        return data.map((value) => {
            value.decode = value.code + ' ' + value.decode;
            return value;
        });
    }

    loadService() {
        const deliveredServiceDetail = this.deliveredService.deliveredServiceDetail;
        this.deliveredService.serviceCodes = this.transformDropdown(this.deliveredService.serviceCodes);
        this.deliveredService.countyCodes = this.transformDropdown(this.deliveredService.countyCodes);
        this.deliveredService.utCodes = this.transformDropdown(this.deliveredService.utCodes);
        this.deliveredServiceForm.setValue({
            personId: deliveredServiceDetail.personId,
            month: deliveredServiceDetail.month,
            year: deliveredServiceDetail.year,
            service: deliveredServiceDetail.service,
            county: deliveredServiceDetail.county,
            unitType: deliveredServiceDetail.unitType,
            rate: (deliveredServiceDetail.unitRate || 0).toFixed(2),
            quantity: (deliveredServiceDetail.unitQuantity || 0).toFixed(2),
            feePaid: (deliveredServiceDetail.feePaid || 0).toFixed(2),
            validate: false,
            serviceAuthDetailId: deliveredServiceDetail.serviceAuthDetailId,
            itemTotal: deliveredServiceDetail.itemTotal || 0,
            id: !this.isNewUsingMode ? deliveredServiceDetail.id : 0,
            lastUpdatedDate: !this.isNewUsingMode ? deliveredServiceDetail.lastUpdatedDate : null,
            saveErrors: false,
            adjustmentInvoiceError: false
        });
        this.infoDivDisplay = this.deliveredService.isErrorServiceCode;
        if(this.infoDivDisplay === true) {
          this.informationalMsgs
            .push('One or more Service Codes could not be decoded.Contact Help Desk.');
          if(this.deliveredService.pageMode === 'NEW') {
            this.deliveredService.serviceCodes = [];
          }
        }
        this.determinePageMode();
        this.showHideButtons();
        this.hideserviceAuthDetailAndDates = this.validateServiceAuthId(this.deliveredService.invoice.type);
        this.previousItemTotal = deliveredServiceDetail.itemTotal;
        this.calculateAndSetItemTotal();
        this.setValidatorsDynamically();
    }

    setValidatorsDynamically() {
        this.deliveredServiceForm.setValidators([
            DeliveredServiceValidators.validateMonthAndYear(),
            DeliveredServiceValidators.validateRateAndPaymentType(
                this.deliveredService.deliveredServiceDetail.paymentType
            ),
            DeliveredServiceValidators.validateAmounts(this.deliveredService.invoice),
        ]);
    }
    calculateAndSetItemTotal() {
        const quantityControl = this.deliveredServiceForm.controls.quantity;
        const feePaidControl = this.deliveredServiceForm.controls.feePaid;
        const unitRateControl = this.deliveredServiceForm.controls.rate;
        const saveErrorsControl = this.deliveredServiceForm.controls.saveErrors;
        const adjustmentInvoiceErrorControl = this.deliveredServiceForm.controls.adjustmentInvoiceError;


        if (!quantityControl.invalid && !feePaidControl.invalid && !unitRateControl.invalid
            && !saveErrorsControl.errors && !adjustmentInvoiceErrorControl.errors) {
            const { rate, quantity, feePaid } = this.deliveredServiceForm.getRawValue();
            const roundRate = Math.round(parseFloat(rate || 0) * 100) / 100.0;
            const subTotal = parseFloat(quantity || 0) * roundRate;
            const itemTotal = (subTotal - parseFloat(feePaid || 0)).toFixed(2);
            this.deliveredService.deliveredServiceDetail.itemTotal = itemTotal;
            this.deliveredServiceForm.get('itemTotal').setValue(itemTotal);
        }
    }

    validateServiceAuthId(invoiceType) {
        return ['DCR', 'DSB', 'DUR'].includes(invoiceType);
    }

    determinePageMode() {
        this.deliveredServiceForm.get('validate').setValue(false);
        if (this.deliveredService.pageMode === 'VIEW' || this.deliveredService.pageMode === 'EDIT') {
            this.deliveredServiceForm.disable();
            if (this.deliveredService.pageMode === 'EDIT' || this.isNewUsingMode) {
                FormUtils.enableFormControlStatus(this.deliveredServiceForm, [
                    'unitType',
                    'rate',
                    'quantity',
                    'feePaid',
                ]);
            }
        }
        if (this.deliveredService.deliveredServiceDetail.paymentType === 'CRM') {
            this.isUnitRateReadOnly = true;
        }
    }

    showHideButtons() {
        if (this.deliveredService.pageMode === 'VIEW') {
            this.hideSaveButton = true;
        } else if (this.deliveredService.pageMode === 'EDIT') {
            this.hideSaveButton = false;
        }
    }

    doValidate() {
        const controls: AbstractControl[] = [];
        controls.push(this.deliveredServiceForm.get('personId'));
        controls.push(this.deliveredServiceForm.get('month'));
        controls.push(this.deliveredServiceForm.get('year'));
        controls.push(this.deliveredServiceForm.get('service'));
        controls.push(this.deliveredServiceForm.get('county'));
        if (this.validateFormControls(controls)) {
            const { personId, service, month, county, year } = this.deliveredServiceForm.getRawValue();
            if (!personId || !service || !month || !county || !year) {
                const initialState = {
                    message: 'Please enter a Person ID, Month, Year, Svc and County to validate.',
                    showCancel: true,
                };
                this.modalService.show(DfpsConfirmComponent, {
                    class: 'modal-md modal-dialog-centered',
                    initialState,
                });
            } else {
                this.hideSaveButton = true;
                this.invoiceService
                    .validateDeliveredService(this.invoiceId, {
                        personId,
                        service,
                        month,
                        county,
                        year,
                        invoiceId: this.invoiceId,
                    })
                    .subscribe((response) => {
                        if (response) {
                            const { unitType, unitRate } = response;
                            this.deliveredService.deliveredServiceDetail.serviceAuthDetails = response.serviceAuthDetails.map(
                                (data) => {
                                    data.code = data.serviceAuthDetailId;
                                    data.decode = data.serviceAuthDetailId;
                                    return data;
                                }
                            );
                            if (this.deliveredService.deliveredServiceDetail.serviceAuthDetails.length > 0) {
                                this.deliveredService.deliveredServiceDetail = {
                                    ...this.deliveredService.deliveredServiceDetail,
                                    ...this.deliveredService.deliveredServiceDetail.serviceAuthDetails[0],
                                };
                                this.deliveredServiceForm
                                    .get('serviceAuthDetailId')
                                    .setValue(this.deliveredService.deliveredServiceDetail.serviceAuthDetailId);
                            }

                            this.deliveredServiceForm.get('validate').setValue(true);
                            this.deliveredServiceForm.get('unitType').setValue(unitType);
                            this.hideSaveButton = false;
                            if (unitType !== 'NOS' && this.deliveredServiceForm.value.unitType !== 'NOS') {
                                this.deliveredServiceForm.patchValue({ unitType });
                                if (unitRate) {
                                    this.deliveredServiceForm.patchValue({ rate: unitRate.toFixed(2) });
                                }
                            }
                            this.deliveredService.deliveredServiceDetail = {
                                ...this.deliveredService.deliveredServiceDetail,
                                ...response,
                            };

                            this.hideserviceAuthDetailAndDates = this.validateServiceAuthId(
                                this.deliveredService.deliveredServiceDetail.invoiceType
                            );
                            if (this.deliveredService.deliveredServiceDetail.paymentType === 'CRM') {
                                this.isUnitRateReadOnly = true;
                            }
                            this.calculateAndSetItemTotal();
                        }
                    });
            }
        }
    }

    doSave() {
        if (this.validateFormGroup(this.deliveredServiceForm)) {
            this.saveDeliveredService();
        }
    }

    saveDeliveredService() {
        const {
            id,
            csli,
            invoiceMonth,
            invoiceType,
            invoiceYear,
            paymentType,
            rejectedItem,
            resourceId,
            serviceAuthDetailId,
            lastUpdatedDate,
            unitRate
        } = this.deliveredService.deliveredServiceDetail;
        const payload = Object.assign(this.deliveredServiceForm.getRawValue(), {
            id,
            csli,
            invoiceId: this.invoiceId,
            invoiceMonth,
            invoiceType,
            invoiceYear,
            paymentType,
            rejectedItem,
            resourceId,
            serviceAuthDetailId,
            lastUpdatedDate,
            unitRate
        });
        payload.invoiceAdjustment = this.deliveredService.invoice.invoiceAdjustment;
        payload.serviceAuthDetailId = this.deliveredServiceForm.get('serviceAuthDetailId').value;
        // Model has property name as unitRate
        payload.unitRate = payload.rate;
        payload.unitQuantity = payload.quantity;
        if (this.isNewUsingMode) {
            payload.lastUpdatedDate = null;
            payload.id = null;
        }
        if(!payload.unitQuantity) {
            payload.unitQuantity = 0.00;
        }
        if(!payload.feePaid){
            payload.feePaid = 0.00
        }
        this.invoiceService.saveDeliveredService(this.invoiceId, payload).subscribe((response) => {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            if (response) {
                this.router.navigate(['financial/invoice/header/' + this.invoiceId]);                
                const invoiceLocalStorge = localStorage.getItem('invoiceHeaderDelivered');
                const invoiceHeaderData = JSON.parse(invoiceLocalStorge);
                invoiceHeaderData.focusItem = this.router.url;
                localStorage.setItem('backFrom', JSON.stringify({naivgatedBackFrom: 'deliveredService'}));
                localStorage.setItem('invoiceHeaderDelivered', JSON.stringify(invoiceHeaderData));            }
        });
    }

    @HostListener('window:popstate' || 'window:hashchange', ['$event'])
    onPopState(event) {
      const invoiceLocalStorge = localStorage.getItem('invoiceHeaderDelivered');
      const invoiceHeaderData = JSON.parse(invoiceLocalStorge);
      invoiceHeaderData.focusItem = this.router.url;
      localStorage.setItem('backFrom', JSON.stringify({naivgatedBackFrom: 'deliveredService'}));
      localStorage.setItem('invoiceHeaderDelivered', JSON.stringify(invoiceHeaderData));
    }

}
