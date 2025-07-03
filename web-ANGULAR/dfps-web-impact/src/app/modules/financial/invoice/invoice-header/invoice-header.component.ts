import { InvoiceDetailRes } from './../model/InvoiceHeader';
import { saveAs } from 'file-saver';
import { DatePipe } from '@angular/common';
import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    DataTable,
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    DropDown,
    FormUtils,
    NavigationService,
    Pagination,
    PaginationUtils,
    Reports,
    ReportService,
    SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { LazyLoadEvent } from 'primeng/api';
import { Observable, Subscription } from 'rxjs';
import {
    CostReimbursementListRes,
    DeliveredServiceResList,
    DeliveredServiceResult,
    FosterCareListRes,
    InvoiceAdminDetailListRes,
    InvoiceHeaderDisplay,
    InvoiceResult,
    InvoiceTypeEnum,
    InvoiceView,
    UserTypeEnum,
    ValidateContractNumberResult
} from '../model/InvoiceHeader';
import { InvoiceService } from '../service/invoice.service';
import { InvoiceHeaderValidators } from './invoice-header.validator';

@Component({
    templateUrl: './invoice-header.component.html'
})
export class InvoiceHeaderComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    constructor(
        private datePipe: DatePipe,
        private formBuilder: FormBuilder,
        private invoiceService: InvoiceService,
        private modalService: BsModalService,
        private route: ActivatedRoute,
        private router: Router,
        private reportService: ReportService,
        private navigationService: NavigationService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.setUserData();
    }

    DEFAULT_PAGE_SIZE = 50;
    readonly ROW_OPTIONS: DropDown[] = [
        {
            code: '10',
            decode: '10'
        },
        {
            code: '25',
            decode: '25'
        },

        {
            code: '50',
            decode: '50'
        },
        {
            code: '100',
            decode: '100'
        },
        {
            code: '250',
            decode: '250'
        },
        {
            code: '500',
            decode: '500'
        }];

    invoiceHeaderForm: FormGroup;
    invoiceId = 0;
    invoicePhase = '';
    hideValidateButton = false;
    hideRecoupInformation = true;
    hideSaveButton = true;
    displayDataTable1 = true;
    displayDataTable2 = true;
    displayReports = true;
    displayInvoiceHeaderResponse: Observable<InvoiceHeaderDisplay>;
    invoiceDetailRes: InvoiceDetailRes;
    newUsing: number;
    isNewUsing = false;
    pageMode: string;
    tableBody1: any[];
    tableColumn1: any[];
    urlKey1: any[];
    urlPath1: string;
    tableBody2: any[];
    tableColumn2: any[];
    informationalMsgs: string[] = [];
    infoDivDisplay = false;
    urlKey2: any[];
    urlPath2: string;
    invoiceLineItemdataTable1: DataTable;
    invoiceLineItemdataTable2: DataTable;
    fosterCareResponse: FosterCareListRes;
    recoupInitDate = null;

    selectedRow: DeliveredServiceResult;
    invoiceType = null;
    dtTableHeaderTxtMap: Map<string, string> = new Map<string, string>();
    dataTable1HeaderTxt = 'Data Table';
    deliveredServiceResList: DeliveredServiceResList;
    isDisplayNewUsing = false;
    resourceName: string;
    resourceVendorId: string;
    resourceId: number;
    region: string;
    validateInvoiceContractResult: ValidateContractNumberResult;
    costReimbursementListRes: CostReimbursementListRes;
    reportSubscription: Subscription;
    reports: any;
    hideAddInvoiceLineItem = false;
    showAuthProvideSec = false;
    showBtnAuth = false;
    currentPageNumber = 0;
    sortOrder: number;
    sortField: string;
    displayExport = false;
    totalElements: number;

    ngOnInit() {
        this.navigationService.setTitle('Invoice');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.ADMINSTRATIVE, 'Administrative List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.FOSTER_CARE, 'Foster Care List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.DELIVERED_SVC_BOTH, 'Delivered Service List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.DELIVERED_SVC_CR, 'Delivered Service List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.DELIVERED_SVC_UR, 'Delivered Service List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.ADOPTION_SERVICE, 'Delivered Service List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.HEALTH_CARE_BENEFIT, 'Delivered Service List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.PERMANECY_CARE_ASSISTANCE, 'Delivered Service List');
        this.dtTableHeaderTxtMap.set(InvoiceTypeEnum.COST_REIMBURSEMENT, 'Cost Reimbursement List');
        this.pageMode = InvoiceView.VIEW;

        this.createForm();
        this.invoiceId = +this.route.snapshot.paramMap.get('invoiceId');
        this.route.queryParams.subscribe((params) => {
            this.newUsing = params.using;
        });
        if (!this.invoiceId || !this.newUsing) {
            let param = this.invoiceId; // this will be Editget
            if (param === 0) {
                this.pageMode = InvoiceView.New;
                this.isNewUsing = false;
            }
            if (this.newUsing) {
                // param will newUsin id && InvoiceID will be 0
                param = this.newUsing;
                this.pageMode = InvoiceView.New;
                this.isNewUsing = true;
            }

            this.intializeScreen(param);
            this.reportSubscription = this.reportService.generateReportEvent.subscribe((data) => {
                this.generateReport(data);
            });
        }
        this.invoiceHeaderForm.get('agencyAccountId').valueChanges.subscribe((value) => {
            this.invoiceHeaderForm.get('validate').setValue('false');
        });

        this.invoiceHeaderForm.get('isManualRecoup').valueChanges.subscribe((value) => {
            if (this.invoiceHeaderForm.get('isManualRecoup').value) {
                if (
                    this.invoiceDetailRes.invoiceResult.recoupInitiatedDate === null ||
                    this.invoiceDetailRes.invoiceResult.recoupInitiatedDate === ''
                ) {
                    this.invoiceDetailRes.invoiceResult.recoupInitiatedDate = this.datePipe.transform(
                        new Date(),
                        'MM/dd/yyyy'
                    );
                    this.recoupInitDate = this.datePipe.transform(new Date(), 'MM/dd/yyyy');
                }
            } else {
                this.invoiceDetailRes.invoiceResult.recoupInitiatedDate = '';
                this.recoupInitDate = null;
            }
        });
    }

    ngOnDestroy() {
        if (this.reportSubscription) {
            this.reportSubscription.unsubscribe();
        }
    }

    createForm() {
        this.invoiceHeaderForm = this.formBuilder.group(
            {
                agencyAccountId: ['', [Validators.required, DfpsCommonValidators.validateId]],
                invoiceAdjustment: [false],
                isReadyForValid: [false],
                type: ['', Validators.required],
                receivedDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
                month: ['', [Validators.required, DfpsCommonValidators.validateMonth]],
                year: ['', [Validators.required, DfpsCommonValidators.validateYear]],
                claimedAmount: ['', DfpsCommonValidators.validateCurrency(14)],
                isManualRecoup: [false],
                recoupCompletedDate: ['', [DfpsCommonValidators.validateDate]],
                providerAuth: [],
                validate: [false],
                monthValidate: [''],
                receivedYearValidate: [''],
            },
            {
                validators: [
                    InvoiceHeaderValidators.validateInvoiceHeaderContractId(),
                    InvoiceHeaderValidators.validateReceivedMonthAndYear(),
                ],
            }
        );
    }

    setUserData() {
        // set user data for 3rd level menu rendering
        const params = this.route.snapshot.paramMap.get('invoiceId');
        this.navigationService.setUserDataValue('idInvoice', params);
    }

    setNewUsingValues() {
        this.invoiceDetailRes.pageMode = InvoiceView.New;
        this.invoiceDetailRes.invoiceResult.type = '';
        this.invoiceDetailRes.invoiceResult.receivedDate = '';
        this.invoiceDetailRes.invoiceResult.invoiceAdjustment = false;
        this.invoiceDetailRes.invoiceResult.isReadyForValid = false;
        this.invoiceDetailRes.invoiceResult.claimedAmount = 0.0;
        this.invoiceDetailRes.invoiceResult.submitDate = '';
        this.invoiceDetailRes.invoiceResult.validAmount = 0.0;
        this.invoiceDetailRes.invoiceResult.warrantDate = '';
        this.invoiceDetailRes.invoiceResult.warrantNumber = '';
        this.invoiceDetailRes.invoiceResult.approved = '';
        this.invoiceDetailRes.invoiceResult.warrantAmount = 0.0;
    }

    intializeScreen(invoiceId: any, loadForm = true) {
        this.invoiceService.displayInvoiceHeaderDetail(invoiceId).subscribe((response) => {
            this.invoiceDetailRes = response;
            if (this.invoiceDetailRes.invoiceResult) {
                this.invoiceType = this.invoiceDetailRes.invoiceResult.type;
            }
            if (this.isNewUsing) {
                this.setNewUsingValues();
            }
            this.loadInvoiceHeaderDetail(loadForm);

            this.setEnableDisableFormFields(this.invoiceDetailRes.pageMode);
            this.showHideSections(this.pageMode);
            if (this.pageMode === InvoiceView.VIEW || this.pageMode === InvoiceView.EDIT) {
                this.displayExport = this.canDisplayExportButton();
                if (
                    this.invoiceDetailRes.invoiceResult &&
                    this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.FOSTER_CARE
                ) {
                    this.dataTable1HeaderTxt = this.dtTableHeaderTxtMap.get(InvoiceTypeEnum.FOSTER_CARE);
                    this.initializeFosterCareDataTable(this.invoiceDetailRes.fosterCareListRes);
                    this.displayDataTable1 = false;
                    this.fosterCareListDataTable(this.invoiceDetailRes.fosterCareListRes);
                    this.totalElements = this.invoiceDetailRes.fosterCareListRes.totalElements;
                    this.backFromFC();
                } else if (
                    this.invoiceDetailRes.invoiceResult &&
                    this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.ADMINSTRATIVE
                ) {
                    this.dataTable1HeaderTxt = this.dtTableHeaderTxtMap.get(InvoiceTypeEnum.ADMINSTRATIVE);
                    this.displayDataTable1 = false;
                    this.administrativeDataListTable(this.invoiceDetailRes.invoiceAdminDetailListRes);
                    this.totalElements = this.invoiceDetailRes.invoiceAdminDetailListRes.totalElements;
                } else if (
                    this.invoiceDetailRes.invoiceResult &&
                    this.isDeliveredService(this.invoiceDetailRes.invoiceResult.type)
                ) {
                    this.dataTable1HeaderTxt = this.dtTableHeaderTxtMap.get(InvoiceTypeEnum.DELIVERED_SVC_BOTH);
                    this.initializedeliveredSvcDataTable(this.invoiceDetailRes.deliveredServiceListRes);
                    this.displayDataTable1 = false;
                    this.totalElements = this.invoiceDetailRes.deliveredServiceListRes.totalElements;
                    this.deliveredSvcDataTable(this.invoiceDetailRes.deliveredServiceListRes);
                    this.backFromDSD();
                    if (
                        this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_CR ||
                        this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_BOTH
                    ) {
                        this.displayDataTable2 = false;
                        this.costReimbursementListDataTable(this.invoiceDetailRes.costReimbursementListRes);
                        this.setToLocalStorage(this.invoiceDetailRes.costReimbursementListRes.costReimbursement, invoiceId);
                        this.validateCRM(invoiceId);
                        this.backFromDSD();
                    }
                }
            }
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        });
    }

    backFromFC() {
        // To implement BACK for FC
        const localData = JSON.parse(localStorage.getItem('invoiceHeader'));
        const localDataFC = JSON.parse(localStorage.getItem('backFrom'));
        if (localData) {
            if (localDataFC) {
                this.DEFAULT_PAGE_SIZE = localData.fosterCare.pageSize;
                this.getInvoiceFosterCareData(localData.fosterCare);
            } else {
                localStorage.setItem('invoiceHeader', JSON.stringify({
                    fosterCare: {
                        pageNumber: 0,
                        pageSize: localData.fosterCare.pageSize,
                        sortDirection: localData.fosterCare.sortDirection,
                        sortField: localData.fosterCare.sortField
                    }
                }));
                const onloadLocalData = JSON.parse(localStorage.getItem('invoiceHeader'));
                this.DEFAULT_PAGE_SIZE = onloadLocalData.fosterCare.pageSize;
                this.getInvoiceFosterCareData(onloadLocalData.fosterCare);
            }
        }
    }

    backFromDSD() {
        // To implement BACK for DSD
        const localDataDelivered = JSON.parse(localStorage.getItem('invoiceHeaderDelivered'));
        const localDataDSD = JSON.parse(localStorage.getItem('backFrom'));
        if (localDataDelivered) {
            if (localDataDSD) {
                if (localDataDelivered.deliveredDetail && localDataDSD.naivgatedBackFrom) {
                    this.DEFAULT_PAGE_SIZE = localDataDelivered.deliveredDetail.pageSize;
                    this.getInvoiceDeliveredSvcBothList(localDataDelivered.deliveredDetail);
                }
            } else {
                localStorage.setItem('invoiceHeaderDelivered', JSON.stringify({
                    deliveredDetail: {
                        pageNumber: 0,
                        pageSize: localDataDelivered.deliveredDetail.pageSize,
                        sortDirection: localDataDelivered.deliveredDetail.sortDirection,
                        sortField: localDataDelivered.deliveredDetail.sortField
                    }
                }));
                const onloadDSLocalData = JSON.parse(localStorage.getItem('invoiceHeaderDelivered'));
                this.DEFAULT_PAGE_SIZE = onloadDSLocalData.deliveredDetail.pageSize;
                this.getInvoiceDeliveredSvcBothList(onloadDSLocalData.deliveredDetail);
            }
        }
    }

    generateUniqueId(data) {
        return data.service + data.lineItem + data.lineItemServicesQuantity;
    }

    generateUpdatedCRMData(crmResponse, localStorageData, invoiceId) {
        const crmData = crmResponse.map(data => {
            const filteredCrm = localStorageData[invoiceId].filter(localData => localData.uniqueId === this.generateUniqueId(data));
            if (filteredCrm.length) {
                data.isSaved = filteredCrm[0].isSaved;
            } else {
                data.isSaved = false;
            }
            return { isSaved: data.isSaved, uniqueId: this.generateUniqueId(data) };
        })
        return crmData;
    }

    setToLocalStorage(crmResponse: any[], invoiceId) {
        const localStorageData = localStorage.getItem('crmData') ? JSON.parse(localStorage.getItem('crmData')) : '';
        if (localStorageData && localStorageData[invoiceId]) {
            const crmData = this.generateUpdatedCRMData(crmResponse, localStorageData, invoiceId);
            localStorage.setItem('crmData', JSON.stringify({
                [invoiceId]: crmData
            }));
            return;
        }
        localStorage.setItem('crmData', JSON.stringify({
            [invoiceId]: crmResponse.map(data => ({ uniqueId: this.generateUniqueId(data), isSaved: false }))
        }));
    }

    isDeliveredService(type: any) {
        if (
            type === InvoiceTypeEnum.DELIVERED_SVC_BOTH ||
            type === InvoiceTypeEnum.DELIVERED_SVC_UR ||
            type === InvoiceTypeEnum.DELIVERED_SVC_CR ||
            type === InvoiceTypeEnum.ADOPTION_SERVICE ||
            type === InvoiceTypeEnum.HEALTH_CARE_BENEFIT ||
            type === InvoiceTypeEnum.PERMANECY_CARE_ASSISTANCE
        ) {
            return true;
        } else {
            return false;
        }
    }

    setEnableDisableFormFields(pageMode: string) {
        this.pageMode = pageMode;
        if (pageMode === InvoiceView.VIEW || this.isNewUsing) {
            this.invoiceHeaderForm.get('validate').setValue(true);
        }
        if (pageMode === InvoiceView.VIEW || pageMode === InvoiceView.EDIT) {
            this.invoiceHeaderForm.disable();
            this.invoiceHeaderForm.get('validate').setValue(true);
        }
        if (pageMode === InvoiceView.EDIT) {
            FormUtils.enableFormControlStatus(this.invoiceHeaderForm, ['receivedDate', 'claimedAmount']);
        } else if (this.isNewUsing) {
            this.invoiceHeaderForm.get('agencyAccountId').disable();
            this.invoiceHeaderForm.get('validate').setValue(true);
        }

        if (
            pageMode === InvoiceView.New ||
            (UserTypeEnum.INTERNAL === this.invoiceDetailRes.invoiceResult.userType &&
                this.invoiceDetailRes.invoiceResult.optIn &&
                !this.invoiceDetailRes.invoiceResult.providerAuth &&
                pageMode === InvoiceView.EDIT) ||
            UserTypeEnum.EXTERNAL === this.invoiceDetailRes.invoiceResult.userType
        ) {
            this.invoiceHeaderForm.get('isReadyForValid').disable();
        } else if (
            !this.invoiceDetailRes.invoiceResult.isReadyForValid &&
            this.hasInvoiceLineItems(this.invoiceDetailRes) && pageMode === InvoiceView.EDIT
        ) {
            FormUtils.enableFormControlStatus(this.invoiceHeaderForm, ['isReadyForValid']);
        }

        if (this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.EXTERNAL) {
            this.invoiceHeaderForm.get('receivedDate').disable();
            this.invoiceHeaderForm.get('claimedAmount').disable();
        } else if (
            this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.INTERNAL &&
            this.invoiceDetailRes.invoiceResult.optIn &&
            pageMode === InvoiceView.EDIT
        ) {
            if (
                !this.invoiceDetailRes.invoiceResult.isReadyForValid &&
                !this.invoiceDetailRes.invoiceResult.providerAuth
            ) {
                this.invoiceHeaderForm.get('receivedDate').disable();
            }
        } else if (this.invoiceDetailRes.invoiceResult.isManualRecoup) {
            this.invoiceHeaderForm.get('receivedDate').disable();
        }
    }

    hasInvoiceLineItems(invoiceDetailRes: InvoiceDetailRes) {
        let hasLineItems = false;

        if (invoiceDetailRes && invoiceDetailRes.invoiceResult) {
            if (
                invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.FOSTER_CARE &&
                invoiceDetailRes.fosterCareListRes.fosterCareResults.length > 0
            ) {
                hasLineItems = true;
            } else if (
                invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.ADMINSTRATIVE &&
                invoiceDetailRes.invoiceAdminDetailListRes.adminDetailResults.length > 0
            ) {
                hasLineItems = true;
            } else if (
                (invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.ADOPTION_SERVICE ||
                    invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_BOTH ||
                    invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_CR ||
                    invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_UR ||
                    invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.PERMANECY_CARE_ASSISTANCE ||
                    invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.HEALTH_CARE_BENEFIT) &&
                invoiceDetailRes.deliveredServiceListRes.deliveredServiceDetails.length > 0
            ) {
                hasLineItems = true;
            }
        }
        return hasLineItems;
    }

    loadInvoiceHeaderDetail(loadForm = true) {
        if (this.invoiceDetailRes.invoiceResult) {
            if (loadForm) {
                this.invoiceHeaderForm.setValue({
                    agencyAccountId: this.invoiceDetailRes.invoiceResult.contractId,
                    invoiceAdjustment: this.invoiceDetailRes.invoiceResult.invoiceAdjustment,
                    isReadyForValid: this.isNewUsing ? false : this.invoiceDetailRes.invoiceResult.isReadyForValid,
                    type: this.invoiceDetailRes.invoiceResult.type,
                    receivedDate: this.invoiceDetailRes.invoiceResult.receivedDate,
                    month: this.invoiceDetailRes.invoiceResult.month,
                    year: this.invoiceDetailRes.invoiceResult.year,
                    claimedAmount: this.invoiceDetailRes.invoiceResult.claimedAmount
                        ? Number(this.invoiceDetailRes.invoiceResult.claimedAmount).toFixed(2)
                        : (0).toFixed(2),
                    isManualRecoup: this.invoiceDetailRes.invoiceResult.isManualRecoup,
                    recoupCompletedDate: this.invoiceDetailRes.invoiceResult.recoupCompletedDate,
                    validate: this.isNewUsing ? true : false,
                    providerAuth: this.invoiceDetailRes.invoiceResult.providerAuth,
                    monthValidate: '',
                    receivedYearValidate: '',
                });
                this.setContractInformationInvoiceResult(this.invoiceDetailRes.invoiceResult);
            } else {
                this.invoiceHeaderForm.patchValue({
                    agencyAccountId: this.invoiceDetailRes.invoiceResult.contractId,
                    type: this.invoiceDetailRes.invoiceResult.type,
                    receivedDate: this.invoiceDetailRes.invoiceResult.receivedDate,
                    month: this.invoiceDetailRes.invoiceResult.month,
                    year: this.invoiceDetailRes.invoiceResult.year,
                    claimedAmount: this.invoiceDetailRes.invoiceResult.claimedAmount,
                    providerAuth: this.invoiceDetailRes.invoiceResult.providerAuth,

                    recoupCompletedDate: this.invoiceDetailRes.invoiceResult.recoupCompletedDate,
                });
            }

            if (this.isNewUsing || this.pageMode === InvoiceView.New) {
                this.invoiceDetailRes.invoiceResult.invoiceId = null;
                this.invoiceDetailRes.invoiceResult.phase = '';
            }

            if (this.pageMode === InvoiceView.EDIT && this.invoiceDetailRes.invoiceResult.approvalDate) {
                if (
                    this.invoiceDetailRes.invoiceResult.isManualRecoup &&
                    this.invoiceDetailRes.invoiceResult.recoupCompletedDate !== null
                ) {
                    FormUtils.enableFormControlStatus(this.invoiceHeaderForm, ['recoupCompletedDate']);
                }
            }
        }
    }
    validateContractId() {
        if (this.validateFormControl(this.invoiceHeaderForm.get('agencyAccountId'), true)) {
            this.invoiceService
                .getInvoiceValidateContractInfo(this.invoiceHeaderForm.get('agencyAccountId').value)
                .subscribe((response) => {
                    this.validateInvoiceContractResult = response;
                    if (this.validateInvoiceContractResult) {
                        this.invoiceHeaderForm.get('validate').setValue(true);
                        this.setContractInformation(this.validateInvoiceContractResult);
                    }
                });
        }
    }
    doInvoiceSave() {
        if (this.validateFormGroup(this.invoiceHeaderForm)) {
            this.invoiceReadyForValidation();
        }
    }

    invoiceReadyForValidation() {
        if (this.pageMode === InvoiceView.EDIT) {
            if (
                this.invoiceHeaderForm.controls.isReadyForValid.enabled &&
                !this.invoiceHeaderForm.get('isReadyForValid').value
            ) {
                const initialState = {
                    title: 'Invoice',
                    message: 'If Ready to Validate click OK, if not click Cancel. ',
                    showCancel: true,
                };
                const modal = this.modalService.show(DfpsConfirmComponent, {
                    class: 'modal-md modal-dialog-centered',
                    initialState,
                });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                    if (result === true) {
                        this.invoiceDetailRes.invoiceResult.isReadyForValid = true;
                        this.saveInvoiceHeader();
                    } else {
                        this.saveInvoiceHeader();
                    }
                });
            } else {
                this.saveInvoiceHeader();
            }
        } else {
            this.saveInvoiceHeader();
        }
    }

    saveInvoiceHeader() {
        const updateInvoiceResult = {
            invoiceId:
                this.pageMode === InvoiceView.New || this.isNewUsing 
                    ? null
                    : this.invoiceDetailRes.invoiceResult.invoiceId,
            contractId:
                this.pageMode === InvoiceView.EDIT || this.isNewUsing || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceDetailRes.invoiceResult.contractId
                    : this.invoiceHeaderForm.value.agencyAccountId,
            invoiceAdjustment:
                this.pageMode === InvoiceView.EDIT || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceHeaderForm.value.isAdjustment
                        ? this.invoiceHeaderForm.value.isAdjustment
                        : this.invoiceDetailRes.invoiceResult.invoiceAdjustment
                    : this.invoiceHeaderForm.value.invoiceAdjustment,
            type:
                this.pageMode === InvoiceView.EDIT || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceDetailRes.invoiceResult.type
                    : this.invoiceHeaderForm.value.type,
            isReadyForValid:
                this.pageMode === InvoiceView.EDIT || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceHeaderForm.value.isReadyForValid
                        ? this.invoiceHeaderForm.value.isReadyForValid
                        : this.invoiceDetailRes.invoiceResult.isReadyForValid
                    : this.invoiceHeaderForm.value.isReadyForValid,
            receivedDate: this.invoiceHeaderForm.value.receivedDate
                ? this.invoiceHeaderForm.value.receivedDate
                : this.invoiceDetailRes.invoiceResult.receivedDate,
            month:
                this.pageMode === InvoiceView.EDIT || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceDetailRes.invoiceResult.month
                    : this.invoiceHeaderForm.value.month,
            year:
                this.pageMode === InvoiceView.EDIT || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceDetailRes.invoiceResult.year
                    : this.invoiceHeaderForm.value.year,
            claimedAmount: this.invoiceHeaderForm.value.claimedAmount,
            isManualRecoup:
                this.pageMode === InvoiceView.EDIT || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceHeaderForm.value.isManualRecoup
                        ? this.invoiceHeaderForm.value.isManualRecoup
                        : this.invoiceDetailRes.invoiceResult.isManualRecoup
                    : this.invoiceHeaderForm.value.isManualRecoup,
            recoupInitiatedDate: this.invoiceDetailRes.invoiceResult.recoupInitiatedDate ?
             this.invoiceDetailRes.invoiceResult.recoupInitiatedDate:this.recoupInitDate,
            recoupCompletedDate:
                this.pageMode === InvoiceView.EDIT || this.pageMode === InvoiceView.VIEW
                    ? this.invoiceHeaderForm.value.recoupCompletedDate
                        ? this.invoiceHeaderForm.value.recoupCompletedDate
                        : this.invoiceDetailRes.invoiceResult.recoupCompletedDate
                    : this.invoiceHeaderForm.value.recoupCompletedDate,
            lastUpdatedDate:
                this.pageMode === InvoiceView.New ? '' : this.invoiceDetailRes.invoiceResult.lastUpdatedDate,
            indCostSaved: this.invoiceDetailRes.indCostSaved,
        };

        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.invoiceService.saveInvoiceHeaderDetail(updateInvoiceResult).subscribe((response) => {
            if (response) {
                this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
                    this.router.navigate(['/financial/invoice/header/' + response]);
                });
            }
        });
    }

    loadDataLazy(event: LazyLoadEvent) {
        if (this.invoiceType === InvoiceTypeEnum.FOSTER_CARE) {
            this.fosterCareResponse = this.invoiceDetailRes.fosterCareListRes;
            this.fosterCareloadDataLazy(event);
        } else if (this.invoiceType === InvoiceTypeEnum.ADMINSTRATIVE) {
            this.adminDetailLoadDataLazy(event);
        } else if (this.isDeliveredService(this.invoiceType)) {
            this.deliveredServiceResList = this.invoiceDetailRes.deliveredServiceListRes;
            this.deliveredSvcBothdDataLazy(event);
        }
    }

    fosterCareloadDataLazy(event: LazyLoadEvent) {
        const fosterCarePagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(
                event,
                this.fosterCareResponse ? this.fosterCareResponse.sortColumns : '',
                'serviceDetailPerson.fullName'
            ),
            sortDirection: PaginationUtils.getSortOrder(event),
        };
        const invoiceLocalStorge = localStorage.getItem('invoiceHeader');
        const localData = JSON.parse(invoiceLocalStorge);
        if (invoiceLocalStorge) {
            localData.fosterCare = fosterCarePagination;
            localStorage.setItem('invoiceHeader', JSON.stringify(localData));
        } else {
            localStorage.setItem('invoiceHeader', JSON.stringify({ fosterCare: fosterCarePagination }))
        }
        this.getInvoiceFosterCareData(fosterCarePagination);
    }

    getInvoiceFosterCareData(fosterCarePagination: any) {
        this.invoiceService
            .getInvoiceFosterCareList(fosterCarePagination, this.invoiceDetailRes.invoiceResult.invoiceId)
            .subscribe((response) => {
                localStorage.setItem('InvoiceHeader_FosterCare_SortFields', JSON.stringify(response.sortColumns));
                this.displayDataTable1 = false;
                this.fosterCareListDataTable(response);
                localStorage.removeItem('backFrom');
            });
    }

    costReimbursementDataLazy(event: LazyLoadEvent) {
        const crmPagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(
                event,
                this.invoiceDetailRes.costReimbursementListRes ? this.invoiceDetailRes.costReimbursementListRes.sortColumns : '',
                ''
            ),
            sortDirection: PaginationUtils.getSortOrder(event),
        };
        this.getInvoiceCRMList(crmPagination);
    }

    deliveredSvcBothdDataLazy(event: LazyLoadEvent) {
        const deliveredSvcPagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(
                event,
                this.deliveredServiceResList ? this.deliveredServiceResList.sortColumns : '',
                ''
            ),
            sortDirection: event === null ? 'DESC' : PaginationUtils.getSortOrder(event),
        };

        const invoiceLocalStorge = localStorage.getItem('invoiceHeaderDelivered');
        const localDataDelivered = JSON.parse(invoiceLocalStorge);
        if (invoiceLocalStorge) {
            localDataDelivered.deliveredDetail = deliveredSvcPagination;
            localStorage.setItem('invoiceHeaderDelivered', JSON.stringify(localDataDelivered));
        } else {
            localStorage.setItem('invoiceHeaderDelivered', JSON.stringify({ deliveredDetail: deliveredSvcPagination }))
        }

        this.getInvoiceDeliveredSvcBothList(deliveredSvcPagination);
    }

    adminDetailLoadDataLazy(event: LazyLoadEvent) {
        const admindDetailPagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(event, this.invoiceDetailRes.invoiceAdminDetailListRes
                ? this.invoiceDetailRes.invoiceAdminDetailListRes.sortColumns : '', ''),
            sortDirection: PaginationUtils.getSortOrder(event),
        };
        this.getInvoiceAdminDetailList(admindDetailPagination);
    }

    getInvoiceAdminDetailList(admindDetailPagination: any) {
        this.invoiceService
            .getInvoiceAdminDetailList(admindDetailPagination, this.invoiceDetailRes.invoiceResult.invoiceId)
            .subscribe((response) => {
                this.displayDataTable1 = false;
                this.administrativeDataListTable(response);
            });
    }

    getInvoiceCRMList(crmReq: any) {
        this.invoiceService
            .getInvoiceCRMList(crmReq, this.invoiceDetailRes.invoiceResult.invoiceId)
            .subscribe((response) => {
                this.displayDataTable2 = false;
                this.costReimbursementListDataTable(response);
            });
    }

    validateCRM(invoiceId) {
        const localStorageCRMData = JSON.parse(localStorage.getItem('crmData'));
        if (localStorageCRMData && localStorageCRMData[invoiceId]) {
            const isCrmNotSaved = localStorageCRMData[invoiceId].some(data => data.isSaved === false);
            if (isCrmNotSaved) {
                this.invoiceHeaderForm.setValidators([
                    InvoiceHeaderValidators.validateInvoiceHeaderContractId(),
                    InvoiceHeaderValidators.validateReceivedMonthAndYear(),
                    InvoiceHeaderValidators.validateCRM(isCrmNotSaved)]);
            }
        }

    }

    getInvoiceDeliveredSvcBothList(deliveredSvcPagination: any) {
        this.invoiceService
            .getInvoiceDeliveredSvcList(deliveredSvcPagination, this.invoiceDetailRes.invoiceResult.invoiceId)
            .subscribe((response) => {
                localStorage.setItem('InvoiceHeader_Delivered_SortFields', JSON.stringify(response.sortColumns));
                this.displayDataTable1 = false;
                this.deliveredSvcDataTable(response);
                localStorage.removeItem('backFrom');
            });
    }

    isServiceAuth() {
        if (
            this.invoiceType === InvoiceTypeEnum.DELIVERED_SVC_BOTH ||
            this.invoiceType === InvoiceTypeEnum.DELIVERED_SVC_UR ||
            this.invoiceType === InvoiceTypeEnum.DELIVERED_SVC_CR
        ) {
            return true;
        } else {
            return false;
        }
    }

    initializedeliveredSvcDataTable(deliveredServiceListRes: DeliveredServiceResList) {
        if (this.isServiceAuth()) {
            this.initializeSvcDelSvcTableColumn(deliveredServiceListRes.optIn);
        } else {
            this.initializeNonSvcDelSvcTableColumn(deliveredServiceListRes.optIn);
        }
        this.invoiceLineItemdataTable1 = {
            tableColumn: this.tableColumn1,
            isSingleSelect: false,
            isPaginator: true,
        };
    }
    deliveredSvcDataTable(deliveredServiceListRes: DeliveredServiceResList) {
        let singleSelect = false;
        if (this.pageMode === InvoiceView.EDIT) {
            if (
                (this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.INTERNAL &&
                    deliveredServiceListRes.deliveredServiceDetails.length > 0) ||
                (this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.EXTERNAL &&
                    this.invoiceDetailRes.invoiceResult.hasInvoiceModify &&
                    deliveredServiceListRes.deliveredServiceDetails.length > 0)
            ) {
                this.isDisplayNewUsing = true;
                singleSelect = true;
            } else {
                this.isDisplayNewUsing = false;
            }
        }

        this.invoiceLineItemdataTable1 = {
            tableBody: deliveredServiceListRes.deliveredServiceDetails,
            tableColumn: this.tableColumn1,
            isSingleSelect: singleSelect,
            totalRows: deliveredServiceListRes.totalElements,
            isPaginator: true,
        };


        if (localStorage.getItem('invoiceHeaderDelivered')) {
            const localDataDelivered = JSON.parse(localStorage.getItem('invoiceHeaderDelivered'));
            this.currentPageNumber = localDataDelivered.deliveredDetail.pageNumber * localDataDelivered.deliveredDetail.pageSize;
            this.sortOrder = localDataDelivered.deliveredDetail.sortDirection === 'ASC' ? 1 : -1;
            const pageTimeOut = (localDataDelivered.deliveredDetail.pageSize === 500) ? 3000 : 1000;
            if (localDataDelivered.deliveredDetail.sortField) {
                this.sortField = this.getDeliveredSortField(localDataDelivered.deliveredDetail.sortField);
                if (localDataDelivered.focusItem) {
                    setTimeout(() => {
                        const element: any = document.querySelector(`[href$='${localDataDelivered.focusItem}']`);
                        if (element) {
                            element.focus();
                            const copyLocalData = { ...localDataDelivered };
                            copyLocalData.focusItem = '';
                            localStorage.setItem('invoiceHeaderDelivered', JSON.stringify(copyLocalData));
                        }
                    }, pageTimeOut)
                }
            }
        }
        else {
            localStorage.setItem('invoiceHeaderDelivered', JSON.stringify({
                deliveredDetail: {
                    pageNumber: 0, pageSize: this.DEFAULT_PAGE_SIZE, sortDirection: 'ASC', sortField: 'p.fullName'
                }
            }))
        }

        if (
            this.pageMode === InvoiceView.EDIT &&
            this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.INTERNAL
        ) {
            // Display Add Button else hide the addButton
            this.hideAddInvoiceLineItem = true;
        } else {
            this.hideAddInvoiceLineItem = false;
        }
    }

    initializeSvcDelSvcTableColumn(optIn: boolean) {
        if (optIn) {
            this.tableColumn1 = [
                {
                    field: 'rejectedItem',
                    header: 'Rej',
                    isHidden: false,
                    isLink: true,
                    sortable: true,
                    width: 100,
                    url: '/financial/invoice/header/:invoiceId/rejection-reason/:id',
                    urlParams: ['invoiceId', 'id'],
                },
                {
                    field: 'externalViewIndicatorFlag',
                    header: 'EV',

                    width: 80,
                    isTick: true,
                },
                {
                    field: 'internalViewIndicatorFlag',
                    header: 'IV',

                    width: 80,
                    isTick: true,
                },
                { field: 'personId', header: 'Person ID', isHidden: false, width: 100 },
                {
                    field: 'fullName',
                    header: 'Name',
                    width: 170,
                    isLink: true,
                    sortable: true,
                    url: '/financial/invoice/header/:invoiceId/delivered-service/:id',
                    urlParams: ['invoiceId', 'id'],
                },
                { field: 'service', header: 'Svc', isHidden: false, width: 100, sortable: true },
                { field: 'serviceAuthDetailId', header: 'Svc Auth Dtl Id', isHidden: false, width: 100 },
                { field: 'svcAuthStartDate', header: 'Svc Auth Start', isHidden: false, width: 100 },
                { field: 'svcAuthTermDate', header: 'Svc Auth Term', isHidden: false, width: 100 },
                {
                    field: 'unitRate',
                    header: 'Rate',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                {
                    field: 'unitQuantity',
                    header: 'Qty',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                { field: 'itemTotal', header: 'Item Tot', isHidden: false, width: 100, isCurrency: true },
                { field: 'csli', header: 'CSLI', isHidden: false, width: 100 },
                { field: 'unitType', header: 'UT', isHidden: false, width: 100 },
                { field: 'county', header: 'Cnty', isHidden: false, width: 100 },
                { field: 'month', header: 'Mo', isHidden: false, width: 100, sortable: true },
                { field: 'year', header: 'Yr', isHidden: false, width: 100 },
                { field: 'feePaid', header: 'Fee Paid', isHidden: false, width: 100, isCurrency: true },
                { field: 'lineType', header: 'LI Type', isHidden: false, width: 100 },
                { field: 'paymentType', header: 'Payment Type', isHidden: false, width: 100 },
            ];
        } else {
            this.tableColumn1 = [
                {
                    field: 'rejectedItem',
                    header: 'Rej',
                    isHidden: false,
                    isLink: true,
                    sortable: true,
                    width: 100,
                    url: '/financial/invoice/header/:invoiceId/rejection-reason/:id',
                    urlParams: ['invoiceId', 'id'],
                },

                { field: 'personId', header: 'Person ID', isHidden: false, width: 100 },
                {
                    field: 'fullName',
                    header: 'Name',
                    width: 170,
                    isLink: true,
                    sortable: true,
                    url: '/financial/invoice/header/:invoiceId/delivered-service/:id',
                    urlParams: ['invoiceId', 'id'],
                },
                { field: 'service', header: 'Svc', isHidden: false, width: 100, sortable: true },
                { field: 'serviceAuthDetailId', header: 'Svc Auth Dtl Id', isHidden: false, width: 100 },
                { field: 'svcAuthStartDate', header: 'Svc Auth Start', isHidden: false, width: 100 },
                { field: 'svcAuthTermDate', header: 'Svc Auth Term', isHidden: false, width: 100 },
                {
                    field: 'unitRate',
                    header: 'Rate',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                {
                    field: 'unitQuantity',
                    header: 'Qty',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                { field: 'itemTotal', header: 'Item Tot', isHidden: false, width: 100, isCurrency: true },
                { field: 'csli', header: 'CSLI', isHidden: false, width: 100 },
                { field: 'unitType', header: 'UT', isHidden: false, width: 100 },
                { field: 'county', header: 'Cnty', isHidden: false, width: 100 },
                { field: 'month', header: 'Mo', isHidden: false, width: 100, sortable: true },
                { field: 'year', header: 'Yr', isHidden: false, width: 100 },
                { field: 'feePaid', header: 'Fee Paid', isHidden: false, width: 100, isCurrency: true },
                { field: 'lineType', header: 'LI Type', isHidden: false, width: 100 },
                { field: 'paymentType', header: 'Payment Type', isHidden: false, width: 100 },
            ];
        }
    }

    initializeNonSvcDelSvcTableColumn(optIn: boolean) {
        if (optIn) {
            this.tableColumn1 = [
                {
                    field: 'rejectedItem',
                    header: 'Rej',
                    isHidden: false,
                    isLink: true,
                    width: 100,
                    url: '/financial/invoice/header/:invoiceId/rejection-reason/:id',
                    urlParams: ['invoiceId', 'id'],
                },
                {
                    field: 'externalViewIndicatorFlagv',
                    header: 'EV',

                    width: 80,
                },
                {
                    field: 'internalViewIndicatorFlag',
                    header: 'IV',

                    width: 80,
                },
                { field: 'personId', header: 'Person ID', isHidden: false, width: 100 },
                {
                    field: 'fullName',
                    header: 'Name',
                    width: 170,
                    isLink: true,
                    sortable: true,
                    url: '/financial/invoice/header/:invoiceId/delivered-service/:id',
                    urlParams: ['invoiceId', 'id'],
                },
                { field: 'service', header: 'Svc', isHidden: false, width: 100, sortable: true },
                {
                    field: 'unitRate',
                    header: 'Rate',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                {
                    field: 'unitQuantity',
                    header: 'Qty',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                { field: 'itemTotal', header: 'Item Tot', isHidden: false, width: 100, isCurrency: true },
                { field: 'csli', header: 'CSLI', isHidden: false, width: 100 },
                { field: 'unitType', header: 'UT', isHidden: false, width: 100 },
                { field: 'county', header: 'Cnty', isHidden: false, width: 100 },
                { field: 'month', header: 'Mo', isHidden: false, width: 100, sortable: true },
                { field: 'year', header: 'Yr', isHidden: false, width: 100 },
                { field: 'feePaid', header: 'Fee Paid', isHidden: false, width: 100, isCurrency: true },
                { field: 'lineType', header: 'LI Type', isHidden: false, width: 100 },
                { field: 'paymentType', header: 'Payment Type', isHidden: false, width: 100 },
            ];
        } else {
            this.tableColumn1 = [
                {
                    field: 'rejectedItem',
                    header: 'Rej',
                    isHidden: false,
                    isLink: true,
                    width: 100,
                    url: '/financial/invoice/header/:invoiceId/rejection-reason/:id',
                    urlParams: ['invoiceId', 'id'],
                },

                { field: 'personId', header: 'Person ID', isHidden: false, width: 100 },
                {
                    field: 'fullName',
                    header: 'Name',
                    width: 170,
                    isLink: true,
                    sortable: true,
                    url: '/financial/invoice/header/:invoiceId/delivered-service/:id',
                    urlParams: ['invoiceId', 'id'],
                },
                { field: 'service', header: 'Svc', isHidden: false, width: 100, sortable: true },
                {
                    field: 'unitRate',
                    header: 'Rate',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                {
                    field: 'unitQuantity',
                    header: 'Qty',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                { field: 'itemTotal', header: 'Item Tot', isHidden: false, width: 100, isCurrency: true },
                { field: 'csli', header: 'CSLI', isHidden: false, width: 100 },
                { field: 'unitType', header: 'UT', isHidden: false, width: 100 },
                { field: 'county', header: 'Cnty', isHidden: false, width: 100 },
                { field: 'month', header: 'Mo', isHidden: false, width: 100, sortable: true },
                { field: 'year', header: 'Yr', isHidden: false, width: 100 },
                { field: 'feePaid', header: 'Fee Paid', isHidden: false, width: 100, isCurrency: true },
                { field: 'lineType', header: 'LI Type', isHidden: false, width: 100 },
                { field: 'paymentType', header: 'Payment Type', isHidden: false, width: 100 },
            ];
        }
    }

    initializeFosterCareDataTable(fosterCareResponse: FosterCareListRes) {
        if (fosterCareResponse.optIn) {
            this.tableColumn1 = [
                {
                    field: 'rejectedItem',
                    header: 'Rej',
                    isHidden: false,
                    sortable: true,
                    isLink: true,
                    width: 100,
                    url: '/financial/invoice/header/:invoiceId/rejection-reason/:fosterCareId',
                    urlParams: ['invoiceId', 'fosterCareId'],
                },
                {
                    field: 'isExternalViewed',
                    header: 'EV',
                    width: 80,
                    isTick: true,
                },
                {
                    field: 'isInternalViewed',
                    header: 'IV',
                    width: 80,
                    isTick: true,
                },
                { field: 'personId', header: 'Person ID', width: 100 },
                {
                    field: 'fullName',
                    header: 'Name',
                    width: 200,
                    sortable: true,
                    url: '/financial/invoice/header/:invoiceId/foster-care/:fosterCareId',
                    urlParams: ['invoiceId', 'fosterCareId'],
                    isLink: true
                },
                { field: 'resourceId', header: 'Resource ID ', sortable: true, width: 100 },
                { field: 'month', header: 'Month', isHidden: false, width: 70 },
                { field: 'year', header: 'Year', isHidden: false, width: 70 },
                { field: 'fromDay', header: 'From', isHidden: false, width: 100 },
                { field: 'toDay', header: 'To', isHidden: false, width: 100 },
                { field: 'service', header: 'Svc', isHidden: false, width: 100 },
                {
                    field: 'rate',
                    header: 'Rate',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                { field: 'income', header: 'Income', isHidden: false, isCurrency: true, width: 100 },
                { field: 'itemTotal', header: 'Item Total', isCurrency: true, isHidden: false, width: 100 },
                { field: 'facilityAcclaim', header: 'Facility Number', isHidden: false, width: 100 },
                { field: 'unitQty', header: 'Qty', isHidden: false, width: 100, isNumber: true, numberFormat: '1.2-5' },
            ];
        } else {
            this.tableColumn1 = [
                {
                    field: 'rejectedItem',
                    header: 'Rejection',
                    isHidden: false,
                    isLink: true,
                    sortable: true,
                    width: 100,
                    url: '/financial/invoice/header/:invoiceId/rejection-reason/:fosterCareId',
                    urlParams: ['invoiceId', 'fosterCareId'],
                },
                { field: 'personId', header: 'Person ID', width: 100 },
                {
                    field: 'fullName',
                    header: 'Name',
                    width: 170,
                    sortable: true,
                    url: '/financial/invoice/header/:invoiceId/foster-care/:fosterCareId',
                    urlParams: ['invoiceId', 'fosterCareId'],
                    isLink: true
                },
                { field: 'resourceId', header: 'Resource ID ', sortable: true, width: 100 },
                { field: 'month', header: 'Month', isHidden: false, width: 70 },
                { field: 'year', header: 'Year', isHidden: false, width: 70 },
                { field: 'fromDay', header: 'From', isHidden: false, width: 100 },
                { field: 'toDay', header: 'To', isHidden: false, width: 100 },
                { field: 'service', header: 'Svc', isHidden: false, width: 100 },
                {
                    field: 'rate',
                    header: 'Rate',
                    isHidden: false,
                    width: 100,
                    isNumber: true,
                    numberFormat: '1.2-5',
                },
                { field: 'income', header: 'Income', isHidden: false, isCurrency: true, width: 100 },
                { field: 'itemTotal', header: 'Item Total', isCurrency: true, isHidden: false, width: 100 },
                { field: 'facilityAcclaim', header: 'Facility Number', isHidden: false, width: 100 },
                { field: 'unitQty', header: 'Qty', isHidden: false, width: 100, isNumber: true, numberFormat: '1.2-5' },
            ];
        }

        this.invoiceLineItemdataTable1 = {
            tableColumn: this.tableColumn1,
            isSingleSelect: false,
            isPaginator: true,
        };
    }

    fosterCareListDataTable(fosterCareResponse: FosterCareListRes) {
        this.invoiceLineItemdataTable1 = {
            tableBody: fosterCareResponse.fosterCareResults,
            tableColumn: this.tableColumn1,
            isSingleSelect: false,
            totalRows: fosterCareResponse.totalElements,
            isPaginator: true,
        };

        if (localStorage.getItem('invoiceHeader')) {
            const localData = JSON.parse(localStorage.getItem('invoiceHeader'));
            this.currentPageNumber = localData.fosterCare.pageNumber * localData.fosterCare.pageSize;
            this.sortOrder = localData.fosterCare.sortDirection === 'ASC' ? 1 : -1;
            const pageTimeOut = (localData.fosterCare.pageSize === 500) ? 3000 : 1000;
            if (localData.fosterCare.sortField) {
                this.sortField = this.getFosterCareSortField(localData.fosterCare.sortField);
            }
            if (localData.focusItem) {
                setTimeout(() => {
                    const element: any = document.querySelector(`[href$='${localData.focusItem}']`);
                    if (element) {
                        element.focus();
                        const copyLocalData = { ...localData };
                        copyLocalData.focusItem = '';
                        localStorage.setItem('invoiceHeader', JSON.stringify(copyLocalData));
                    }
                }, pageTimeOut)
            }
        }
        else {
            localStorage.setItem('invoiceHeader', JSON.stringify({
                fosterCare: {
                    pageNumber: 0, pageSize: this.DEFAULT_PAGE_SIZE, sortDirection: 'ASC', sortField: 'serviceDetailPerson.fullName'
                }
            }))
        }

        if (!this.invoiceDetailRes.invoiceResult.isManualRecoup && this.pageMode === InvoiceView.EDIT) {
            this.hideAddInvoiceLineItem = true;
        } else {
            this.hideAddInvoiceLineItem = false;
        }
    }

    addInvoiceLineItem() {
        if (this.invoiceType === InvoiceTypeEnum.FOSTER_CARE) {
            this.router.navigate([InvoiceService.HEADER_FRONTEND_URL + '/' + this.invoiceId + '/foster-care/0']);
        } else if (this.invoiceType === InvoiceTypeEnum.ADMINSTRATIVE) {
            this.router.navigate([InvoiceService.HEADER_FRONTEND_URL + '/' + this.invoiceId + '/administrative/0']);
        } else if (this.isDeliveredService(this.invoiceType)) {
            this.router.navigate([InvoiceService.HEADER_FRONTEND_URL + '/' + this.invoiceId + '/delivered-service/0']);
        }
    }

    administrativeDataListTable(adminDetailListRes: InvoiceAdminDetailListRes) {
        this.tableColumn1 = [
            {
                field: 'rejection',
                header: 'Rejection',
                isHidden: false,
                isLink: true,
                sortable: true,
                width: 100,
                url: '/financial/invoice/header/:invoiceId/rejection-reason/:adminDetailId',
                urlParams: ['invoiceId', 'adminDetailId'],
            },

            {
                field: 'service',
                header: 'Service',
                isHidden: false,
                width: 100,
                url: '/financial/invoice/header/:invoiceId/administrative/:adminDetailId',
                urlParams: ['invoiceId', 'adminDetailId'],
                isLink: true,
            },
            { field: 'csli', header: 'CSLI ', isHidden: false, width: 100 },
            { field: 'month', header: 'Month', isHidden: false, width: 100 },
            { field: 'year', header: 'Year', isHidden: false, width: 100 },
            { field: 'salaries', header: 'Salaries', isCurrency: true, isHidden: false, width: 100 },
            { field: 'benefits', header: 'Frg Bnfts', isCurrency: true, isHidden: false, width: 100 },
            { field: 'travel', header: 'Travel', isCurrency: true, isHidden: false, width: 100 },
            { field: 'supplies', header: 'Supplies', isCurrency: true, isHidden: false, width: 100 },
            { field: 'equipment', header: 'Equip', isCurrency: true, isHidden: false, width: 100 },
            { field: 'other', header: 'Other', isCurrency: true, isHidden: false, width: 100 },
            { field: 'administrative', header: 'Adm Alloc', isCurrency: true, isHidden: false, width: 100 },
            { field: 'offset', header: 'Offset', isCurrency: true, isHidden: false, width: 100 },
        ];

        this.invoiceLineItemdataTable1 = {
            tableBody: adminDetailListRes.adminDetailResults,
            tableColumn: this.tableColumn1,
            isSingleSelect: false,
            totalRows: adminDetailListRes.totalElements,
            isPaginator: true,
        };
        if (this.pageMode === InvoiceView.EDIT) {
            // Display Add Button else hide the addButton
            this.hideAddInvoiceLineItem = true;
        } else {
            this.hideAddInvoiceLineItem = false;
        }
    }
    costReimbursementListDataTable(costReimbursementListRes: CostReimbursementListRes) {
        this.tableColumn2 = [
            {
                field: 'rejectedItem',
                header: 'Rejection',
                sortable: true,
                isLink: true,
                width: 100,
                url: '/financial/invoice/header/:invoiceId/rejection-reason/:crmId',
                urlParams: ['invoiceId', 'crmId'],
            },

            {
                field: 'service',
                header: 'Svc',
                isHidden: false,
                width: 100,
                isLink: true,
                url:
                    '/financial/invoice/header/:invoiceId/cost-reimbursement/:crmId/:service/:lineItem/:lineItemServicesQuantity',
                urlParams: ['invoiceId', 'crmId', 'service', 'lineItem', 'lineItemServicesQuantity'],
            },
            { field: 'lineItem', header: 'CSLI ', isHidden: false, width: 50 },
            {
                field: 'lineItemServicesQuantity',
                header: 'Qty',
                isHidden: false,
                isNumber: true,
                numberFormat: '1.2-5',
                width: 100,
            },
            {
                field: 'salaryExpenditureReimbursement',
                header: 'Salaries',
                isCurrency: true,
                isHidden: false,
                width: 100,
            },
            { field: 'fringeBenefitReimbursement', header: 'Frg Bnfts', isCurrency: true, isHidden: false, width: 100 },
            {
                field: 'travelExpenditureReimbursement',
                isCurrency: true,
                isHidden: false,
                width: 100,
                header: 'Travel',
            },
            {
                field: 'supplyExpenditureReimbursement',
                header: 'Supplies',
                isCurrency: true,
                isHidden: false,
                width: 100,
            },
            { field: 'equipmentExpenditures', header: 'Equip', isCurrency: true, isHidden: false, width: 100 },
            { field: 'otherExpenditureReimbursement', header: 'Other', isCurrency: true, isHidden: false, width: 100 },
            { field: 'subTotal', header: 'Subtotal', isCurrency: true, isHidden: false, width: 100 },
            { field: 'allocatedAdminCosts', header: 'Adm Alloc', isCurrency: true, isHidden: false, width: 100 },
            { field: 'offsetItemReimbursement', header: 'Offset', isCurrency: true, isHidden: false, width: 100 },
            {
                field: 'rate',
                header: 'Rate',
                isHidden: false,
                width: 100,
                isNumber: true,
                numberFormat: '1.2-5',
            }
        ];

        this.invoiceLineItemdataTable2 = {
            tableBody: costReimbursementListRes.costReimbursement,
            tableColumn: this.tableColumn2,
            isSingleSelect: false,
            totalRows: costReimbursementListRes.totalElements,
            isPaginator: true,
        };
    }

    newUsingDsvc() {
        // check if an invoice is selected
        if (!(this.selectedRow && this.selectedRow.invoiceId)) {
            const initialState = {
                title: 'Invoice Header',
                message: 'Please select at least one row to perform this action',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
        } else {
            const headerUrl = InvoiceService.HEADER_FRONTEND_URL + '/' + this.invoiceId + '/delivered-service/0';
            this.router.navigate([headerUrl], { queryParams: { using: this.selectedRow.id } });
        }
    }

    setContractInformation(validateInvoiceContractResult: ValidateContractNumberResult) {
        this.resourceVendorId = validateInvoiceContractResult.resourceVendorId;
        this.resourceId = validateInvoiceContractResult.resourceId;
        this.resourceName = validateInvoiceContractResult.resourceName;
        this.region = validateInvoiceContractResult.region;
    }

    setContractInformationInvoiceResult(invoiceResult: InvoiceResult) {
        this.resourceVendorId = invoiceResult.vidNumber;
        this.resourceId = invoiceResult.resourceId;
        this.resourceName = invoiceResult.resourceName;
        this.region = invoiceResult.region;
    }

    getReports(): Reports[] {
        if (
            (this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_BOTH ||
                this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_CR ||
                this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_UR) &&
            this.invoiceDetailRes.invoiceResult.phase !== 'NSB' &&
            this.invoiceDetailRes.invoiceResult.phase !== 'SBT' &&
            this.invoiceDetailRes.invoiceResult.phase !== 'PAD'
        ) {
            return [
                {
                    reportName: 'Singular Pre-Bill for Dlvrd Svc',
                    reportParams: {
                        reportName: 'cfn50o01',
                        emailMessage: '',
                        paramList: '',
                    },
                },
            ];
        } else if (
            this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.FOSTER_CARE &&
            !(
                this.invoiceDetailRes.invoiceResult.phase === 'NSB' ||
                this.invoiceDetailRes.invoiceResult.phase === 'SBT' ||
                this.invoiceDetailRes.invoiceResult.phase === 'PAD'
            )
        ) {
            return [
                {
                    reportName: 'Singular Pre-Bill for Foster Car',
                    reportParams: {
                        reportName: 'cfn51o01',
                        emailMessage: '',
                        paramList: '',
                    },
                },
            ];
        } else if (this.invoiceDetailRes.invoiceResult.phase === 'PAD') {
            return [
                {
                    reportName: 'Singular Provider Statement',
                    reportParams: {
                        reportName: 'cfn52o01',
                        emailMessage: '',
                        paramList: '',
                    },
                },
            ];
        }
    }
    generateReport(reportData: any) {
        if (reportData) {
            reportData = JSON.parse(reportData);
            const emailMessage = 'Invoice:' + this.invoiceId;
            reportData.emailMessage = emailMessage;
            const paramList = this.invoiceId;
            reportData.paramList = paramList;
        }

        this.reportService.callReportService(reportData);
    }

    callSaveInvoiceAuthorize() {
        const invoiceAuthorizeReq = {
            invoiceId: this.invoiceDetailRes.invoiceResult.invoiceId,
            providerAuth: this.invoiceHeaderForm.get('providerAuth').value,
        };
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.invoiceService.authorizeInvoiceHeader(invoiceAuthorizeReq).subscribe((response) => {
            this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
                this.router.navigate(['/financial/invoice/header/' + response]);
            });
        });
    }

    doInvoiceAuthorize() {
        if (
            this.invoiceHeaderForm.get('providerAuth').value === 'false' ||
            !this.invoiceHeaderForm.get('providerAuth').value
        ) {
            const initialState = {
                title: 'Invoice',
                message: 'You must select the Provider Authorization checkbox before you can Authorize this Invoice.',
                showCancel: true,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                if (result === true) {
                    // do nothing
                }
            });
        } else {
            if (this.isInvoiceLineItemsViewed()) {
                this.callSaveInvoiceAuthorize();
            } else {
                const initialState = {
                    title: 'Invoice',
                    message: 'You have not opened all line item detail pages. Are you sure you are ready to Authorize?',
                    showCancel: true,
                };
                const modal = this.modalService.show(DfpsConfirmComponent, {
                    class: 'modal-md modal-dialog-centered',
                    initialState,
                });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                    if (result === true) {
                        this.callSaveInvoiceAuthorize();
                    }
                });
            }
        }
    }

    isInvoiceLineItemsViewed() {
        if (
            (this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_BOTH ||
                this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_CR ||
                this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_UR) &&
            this.invoiceDetailRes.deliveredServiceListRes.deliveredServiceDetails.length ===
            this.invoiceDetailRes.deliveredServiceListRes.evCount
        ) {
            return true;
        } else if (
            this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.FOSTER_CARE &&
            this.invoiceDetailRes.fosterCareListRes.fosterCareResults.length ===
            this.invoiceDetailRes.fosterCareListRes.evCount
        ) {
            return true;
        } else {
            return false;
        }
    }

    showHideSections(pageMode: any) {
        if (pageMode === InvoiceView.VIEW || pageMode === InvoiceView.EDIT || this.isNewUsing) {
            this.hideValidateButton = true;
        }

        if (this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.INTERNAL) {
            if (pageMode === InvoiceView.New) {
                this.hideSaveButton = false;
            } else if (pageMode === InvoiceView.EDIT && !this.invoiceDetailRes.invoiceResult.recoupCompletedDate) {
                if (
                    this.invoiceDetailRes.invoiceResult.phase !== 'NSB' &&
                    this.invoiceDetailRes.invoiceResult.phase !== 'SBT' &&
                    this.invoiceDetailRes.invoiceResult.phase !== 'PAD'
                ) {
                    this.hideSaveButton = false;
                }
            }
        }

        if (pageMode === InvoiceView.EDIT || pageMode === InvoiceView.VIEW) {
            if (
                this.invoiceDetailRes.invoiceResult.optIn &&
                (this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.FOSTER_CARE ||
                    this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_BOTH ||
                    this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_CR ||
                    this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_UR)
            ) {
                this.showAuthProvideSec = true;
                if (
                    this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.EXTERNAL &&
                    !this.invoiceDetailRes.invoiceResult.providerAuth &&
                    (this.invoiceDetailRes.invoiceResult.hasInvoiceFCModify ||
                        this.invoiceDetailRes.invoiceResult.hasInvoiceModify)
                ) {
                    this.showBtnAuth = true;
                }

                if (
                    this.invoiceDetailRes.invoiceResult.userType === UserTypeEnum.INTERNAL ||
                    this.invoiceDetailRes.invoiceResult.providerAuth ||
                    (!this.invoiceDetailRes.invoiceResult.providerAuth && !this.showBtnAuth) ||
                    pageMode === InvoiceView.VIEW
                ) {
                    this.invoiceHeaderForm.get('providerAuth').disable();
                } else {
                    this.invoiceHeaderForm.get('providerAuth').enable();
                }
            }
        }

        // Manual Recoup Info
        if (
            this.invoiceType === InvoiceTypeEnum.FOSTER_CARE &&
            (pageMode === InvoiceView.EDIT || pageMode === InvoiceView.VIEW)
        ) {
            this.hideRecoupInformation = false;

            if (this.invoiceDetailRes.invoiceResult.approvalDate) {
                const today = new Date().getTime();
                const apprvDate = new Date(this.invoiceDetailRes.invoiceResult.approvalDate).getTime();
                const oneDay = 24 * 60 * 60 * 1000;
                const noOfDaysDiff = Math.round(Math.abs((today - apprvDate) / oneDay));
                const validAmount = this.invoiceDetailRes.invoiceResult.validAmount
                    ? Number(this.invoiceDetailRes.invoiceResult.validAmount)
                    : 0;
                if (
                    noOfDaysDiff > 89 &&
                    validAmount < 0 &&
                    !this.invoiceDetailRes.invoiceResult.isManualRecoup &&
                    !(
                        this.invoiceDetailRes.invoiceResult.phase === 'SBT' ||
                        this.invoiceDetailRes.invoiceResult.phase === 'PAD'
                    )
                ) {
                    FormUtils.enableFormControlStatus(this.invoiceHeaderForm, ['isManualRecoup']);
                    this.hideSaveButton = false;
                } else {
                    if (
                        this.invoiceDetailRes.invoiceResult.isManualRecoup &&
                        this.invoiceDetailRes.invoiceResult.recoupCompletedDate === null
                    ) {
                        FormUtils.enableFormControlStatus(this.invoiceHeaderForm, ['recoupCompletedDate']);
                        this.hideSaveButton = false;
                    }
                }
            }

        }

        if (pageMode === InvoiceView.VIEW || pageMode === InvoiceView.EDIT) {
            this.displayReports = false;
            if (this.getReports()) {
                this.reports = this.getReports().map((report) => {
                    return { ...report, reportParams: JSON.stringify(report.reportParams) };
                });
            }
        }

        if (pageMode === InvoiceView.VIEW || pageMode === InvoiceView.EDIT) {
            if (
                this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_BOTH ||
                this.invoiceDetailRes.invoiceResult.type === InvoiceTypeEnum.DELIVERED_SVC_CR
            ) {
                if (this.invoiceDetailRes.isUnitRateMax || this.invoiceDetailRes.isCostReiNavViolation) {
                    if (this.invoiceDetailRes.isUnitRateMax) {
                        this.informationalMsgs.push(
                            'Calculated Unit Rate exceeds 9999.99. Until corrected it will be displayed as 0.0'
                        );
                    }

                    if (this.invoiceDetailRes.isCostReiNavViolation) {
                        this.informationalMsgs.push(
                            // tslint:disable-next-line: max-line-length
                            'Unit Rate in Delivered Service is currently 0.00. If this is not correct, please navigate to Cost Reimbursement Detail and complete and save, or save again, for correct Unit Rate calculation. '
                        );
                    }
                    this.infoDivDisplay = true;
                }
            }
        }
    }

    download() {
        this.invoiceService.downloadDeliveredService(this.invoiceId, this.totalElements)
            .subscribe(blob => saveAs(blob, this.invoiceId + '_' + this.invoiceType + '.xlsx'));
    }

    canDisplayExportButton() {
        return this.invoiceType === 'FSC'
            || this.invoiceType === 'DCR'
            || this.invoiceType === 'DUR'
            || this.invoiceType === 'DSB'
            || this.invoiceType === 'PCA'
            || this.invoiceType === 'HCB'
            || this.invoiceType === 'ADS'
    }

    getFosterCareSortField(value): any {
        return this.getKeyByValue(JSON.parse(localStorage.getItem('InvoiceHeader_FosterCare_SortFields')), value)
    }

    getDeliveredSortField(value): any {
        return this.getKeyByValue(JSON.parse(localStorage.getItem('InvoiceHeader_Delivered_SortFields')), value)
    }

    getKeyByValue(object, value): any {
        return Object.keys(object).find(key => object[key] === value);
    }

    @HostListener('window:popstate' || 'window:hashchange', ['$event'])
    onPopState(event) {
        const paymentHistoryLocalData = JSON.parse(localStorage.getItem('PaymentHistory'));
        if (paymentHistoryLocalData) {
            paymentHistoryLocalData.focusItem = this.router.url;
            localStorage.setItem('PaymentHistory', JSON.stringify(paymentHistoryLocalData));
        }
        localStorage.setItem('backFrom', 'InvoiceHeader');
    }
}
