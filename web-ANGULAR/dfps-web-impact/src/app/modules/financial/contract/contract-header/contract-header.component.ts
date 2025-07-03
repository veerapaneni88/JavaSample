import { Component, ElementRef, HostListener, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
    DataTable,
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    ENVIRONMENT_SETTINGS,
    FormUtils,
    NavigationService,
    SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { ResourceSearchResponse } from '../../../shared/model/ResourceSearch';
import { ContractHeaderResponse, ResourceAddress } from '../model/ContractHeader';
import { ContractService } from '../service/contract.service';
import { ContractHeaderValidators } from './contract-header.validator';
import { dfpsValidatorErrors } from '../../../../../messages/validator-errors';

@Component({
    templateUrl: './contract-header.component.html',
})
export class ContractHeaderComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    @ViewChild('cvTable', { static: false }) cvTableElement: ElementRef;
    @ViewChild('cpTable', { static: false }) cpTableElement: ElementRef;
    @ViewChild('errors', { static: false }) errorElement: ElementRef;
    @ViewChild('resourceIdSpan') resourceIdSpanEl: ElementRef;

    contractHeaderForm: FormGroup;
    constractServiceForm: FormGroup;
    contractId = '0';
    contractNumber: string;
    resourceAddresses: ResourceAddress[];
    contractHeaderResponse: ContractHeaderResponse;
    selectedResourceAddress: string;
    selectContractPeriod: any;
    params: string;

    isBackgroundCheck = false;
    isBudgetLimit = false;

    isVendorInfo = false;
    isSponsorInfo = false;
    isCpInfo = false;
    isCvInfo = false;
    isResourceIdValidated = false;
    isContractPeriodSelected = false;
    isAnyContractPeriodClosed = false;

    hideResourceButton = false;
    hideResourceValidateButton = false;
    hideStaffButton = false;
    hideOrgButton = false;
    hideOrgStaffButton = false;
    hideSaveButton = false;
    disbaleVendorInfo = false;
    showServicesHyperLink = false;
    showTransferHyperLink = false;
    errorNoDataMessage;

    selectedResourceAddressId: string;
    selectContractVersion: any;

    tableColumn: any[];
    urlKey: any[];
    urlPath: string;
    vendorDataTable: DataTable;
    cpDataTable: DataTable;
    cvDataTable: DataTable;
    urlResponsePaths: string[];
    urlErrorPaths: string[];

    constructor(
        private formBuilder: FormBuilder,
        private cookieService: CookieService,
        private route: ActivatedRoute,
        private router: Router,
        private navigationService: NavigationService,
        private contractService: ContractService,
        private modalService: BsModalService,
        private searchService: SearchService,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.setUserData();
        if (this.navigationService.getPreviousUrl() && this.navigationService.getPreviousUrl().endsWith('/financial/contract')) {
            this.searchService.setFormData(null);
            this.searchService.setFormContent(null);
        }
    }

    setUserData() {
        this.contractId = this.route.snapshot.paramMap.get('contractId');
        this.contractNumber =
            this.route.snapshot.paramMap.get('contractNumber') === 'null'
                ? '0'
                : this.route.snapshot.paramMap.get('contractNumber');
        this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
        this.navigationService.setUserDataValue('idContract', this.contractId);
        this.navigationService.setUserDataValue('contractNumber', this.contractNumber);
    }

    createForm() {
        this.contractHeaderForm = this.formBuilder.group(
            {
                resourceId: ['', [DfpsCommonValidators.validateId, Validators.required, Validators.maxLength(10)]],
                functionType: ['', Validators.required],
                procurementType: ['', Validators.required],
                programType: ['', Validators.required],
                region: ['', Validators.required],
                contractManagerId: ['', ContractHeaderValidators.validateContractManager()],
                budgetLimit: [false],
                backgroundCheck: [false],
                organizationId: [''],
                einBgcAccountLinkId: [''],
                sponsorPersonId: [''],
                lastUpdatedDate: [],
                isValidated: [false],
                contractVersionLocked: [true, ContractHeaderValidators.validateContractVersionIsLocked()],
            },
            {
                validators: [ContractHeaderValidators.validateSponsorInfo]
            }
        );
    }

    ngOnInit() {
        this.navigationService.setTitle('Contract Header');
        this.createForm();
        this.intializeScreen();
        this.contractHeaderForm.get('backgroundCheck').valueChanges.subscribe((value) => {
            this.isSponsorInfo = value;
        });
        this.contractHeaderForm.get('resourceId').valueChanges.subscribe((value) => {
            this.contractHeaderForm.get('isValidated').setValue('false');
            this.isResourceIdValidated = false;
        });
    }

    intializeScreen(reload = false) {
        this.contractId = this.route.snapshot.paramMap.get('contractId');
        this.contractService.getContractHeader(this.contractId).subscribe((response) => {
            this.contractHeaderResponse = response;
            this.loadContract(reload);
            this.determinePageMode();
            this.showHideButtons();
            this.vendorInfoDataTable();
            this.initialFormValue = this.contractHeaderForm.value;
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.populatePullBackData();
        });
    }

    populatePullBackData() {
        if (this.searchService.getFormData()) {
            this.contractHeaderForm.patchValue(this.searchService.getFormData());
            if (this.contractHeaderForm.get('resourceId').value !== '') {
                this.validateResourceId();
            }
            if (this.searchService.getFormContent()) {
                this.contractHeaderResponse = this.searchService.getFormContent();
            }
            if (this.searchService.getSelectedOrg()) {
                this.contractHeaderResponse.contract.organizationEIN = this.searchService.getSelectedOrg().einId;
                this.contractHeaderResponse.contract.organizationLegalName = this.searchService.getSelectedOrg().legalName;
                this.contractHeaderResponse.contract.organizationId = this.searchService.getSelectedOrg().orgDtlId;
                this.contractHeaderForm.get('organizationId').setValue(this.searchService.getSelectedOrg().orgDtlId);
            }
            if (this.searchService.getSelectedResource()) {
                this.contractHeaderResponse.contract.resourceName = this.searchService.getSelectedResource().name;
                this.contractHeaderResponse.contract.resourceId = this.searchService.getSelectedResource().resourceId;
                this.contractHeaderForm.get('resourceId').setValue(this.searchService.getSelectedResource().resourceId);
                this.setResourceAddressesFromResourceSearch(this.searchService.getSelectedResource());
                this.validateResourceId();
                // this.vendorInfoDataTable();
            }
            if (this.searchService.getSelectedStaff() && this.searchService.getSearchSource() === 'ContractManager') {
                this.contractHeaderResponse.contract.contractManager = this.searchService.getSelectedStaff().name;
                this.contractHeaderForm
                    .get('contractManagerId')
                    .setValue(this.searchService.getSelectedStaff().personId);
                if (this.contractHeaderResponse.pageMode === 'NEW' && !this.contractHeaderResponse.contract.sponsorName) {
                    this.contractHeaderResponse.contract.sponsorName = this.searchService.getSelectedStaff().name;
                    this.contractHeaderResponse.contract.sponsorPersonId = this.searchService.getSelectedStaff().personId;
                    this.contractHeaderResponse.contract.sponsorPhone = this.searchService.getSelectedStaff().workPhone;
                    this.contractHeaderResponse.contract.sponsorEmail = this.searchService.getSelectedStaff().email;
                    this.contractHeaderForm.get('sponsorPersonId').setValue(this.searchService.getSelectedStaff().personId);
                }
            }
            if (this.searchService.getSelectedStaff() && this.searchService.getSearchSource() === 'Sponser') {
                this.contractHeaderResponse.contract.sponsorName = this.searchService.getSelectedStaff().name;
                this.contractHeaderResponse.contract.sponsorPersonId = this.searchService.getSelectedStaff().personId;
                this.contractHeaderResponse.contract.sponsorPhone = this.searchService.getSelectedStaff().workPhone;
                this.contractHeaderResponse.contract.sponsorEmail = this.searchService.getSelectedStaff().email;
                this.contractHeaderForm.get('sponsorPersonId').setValue(this.searchService.getSelectedStaff().personId);
            }

            this.searchService.setSelectedStaff(null);
            this.searchService.setFormData(null);
        }
    }

    setResourceAddressesFromResourceSearch(resourceSearchResponse: ResourceSearchResponse) {
        const resourceAddress: ResourceAddress = {
            resourceAddressId: 0,
            vendorId: resourceSearchResponse.vendorId,
            addressLine1: resourceSearchResponse.addressStreetLine1 ? resourceSearchResponse.addressStreetLine1 : '',
        };
        this.resourceAddresses = [resourceAddress];
    }

    loadContract(reload = false) {
        if (this.contractHeaderResponse.contract.resourceId) {
            this.resourceAddresses = this.contractHeaderResponse.contract.resourceAddress;
            if (!reload) {
                this.contractHeaderForm.setValue({
                    resourceId: this.contractHeaderResponse.contract.resourceId,
                    functionType: this.contractHeaderResponse.contract.functionType,
                    procurementType: this.contractHeaderResponse.contract.procurementType,
                    programType: this.contractHeaderResponse.contract.programType,
                    region: this.contractHeaderResponse.contract.region,
                    budgetLimit: this.contractHeaderResponse.contract.budgetLimit,
                    backgroundCheck: this.contractHeaderResponse.contract.backgroundCheck,
                    organizationId: this.contractHeaderResponse.contract.organizationId,
                    einBgcAccountLinkId: this.contractHeaderResponse.contract.einBgcAccountLinkId,
                    sponsorPersonId: this.contractHeaderResponse.contract.sponsorPersonId,
                    contractManagerId: this.contractHeaderResponse.contract.contractManagerId,
                    lastUpdatedDate: this.contractHeaderResponse.contract.lastUpdatedDate,
                    contractVersionLocked: false,
                    isValidated: false,
                });
            } else {
                this.contractHeaderForm.patchValue({
                    lastUpdatedDate: this.contractHeaderResponse.contract.lastUpdatedDate,
                    contractVersionLocked: false,
                });
            }
        }
    }

    determinePageMode() {
        if (this.contractHeaderResponse.pageMode === 'VIEW' || this.contractHeaderResponse.pageMode === 'EDIT') {
            this.contractHeaderForm.disable();
            if (this.contractHeaderResponse.pageMode === 'EDIT') {
                FormUtils.enableFormControlStatus(this.contractHeaderForm, [
                    'procurementType',
                    'programType',
                    'organizationId',
                    'contractManagerId',
                    'sponsorPersonId',
                ]);
                if (
                    !(
                        this.contractHeaderResponse.contract.registeredStatus ||
                        this.contractHeaderResponse.contract.accountStatus
                    )
                ) {
                    FormUtils.enableFormControlStatus(this.contractHeaderForm, [
                        'procurementType',
                        'programType',
                        'backgroundCheck',
                    ]);
                }
            }
        }
    }

    enableServicesHyperLink(contractPeriod: any) {
        if (contractPeriod && contractPeriod.length > 0) {
            this.showServicesHyperLink = true;
        }
    }

    enableTransferHyperLink() {
        if (this.contractHeaderResponse.pageMode === 'EDIT' && this.contractHeaderForm.get('budgetLimit').value) {
            this.showTransferHyperLink = true;
        }
    }

    showHideButtons() {
        if (this.contractHeaderResponse.pageMode === 'VIEW') {
            this.hideResourceButton = true;
            this.hideResourceValidateButton = true;
            this.hideStaffButton = true;
            this.hideOrgButton = true;
            this.hideOrgStaffButton = true;
            this.hideSaveButton = true;
            this.disbaleVendorInfo = true;
            this.isCpInfo = true;
        } else if (this.contractHeaderResponse.pageMode === 'EDIT') {
            this.hideResourceButton = true;
            this.hideResourceValidateButton = true;
            this.hideStaffButton = false;
            this.hideOrgButton = false;
            this.hideOrgStaffButton = false;
            this.hideSaveButton = false;
            this.disbaleVendorInfo = false;
            this.isCpInfo = true;
        }
    }

    doSave() {
        if (this.validateFormGroup(this.contractHeaderForm)) {
            if (this.contractHeaderResponse.pageMode === 'NEW') {
                const initialState = {
                    title: 'Contract Header',
                    message: 'Once saved the Budget Limit can not be changed. Continue?',
                    showCancel: true,
                    focusId: 'contractSave',
                };
                const modal = this.modalService.show(DfpsConfirmComponent, {
                    class: 'modal-md modal-dialog-centered',
                    initialState,
                });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                    if (result === true) {
                        this.saveContract();
                    }
                });
            } else {
                this.saveContract();
            }
        }
    }

    saveContract() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        const payLoad = Object.assign(this.contractHeaderResponse.contract, this.contractHeaderForm.value);
        this.clearSponsorInfo();
        this.contractService.saveContract(payLoad).subscribe((response) => {
            if (response) {
                const contractId = response.contractId;
                const scorNumber = response.scorContractNumber ? response.scorContractNumber : 0;
                this.router.navigate(['/financial/contract/header/'
                    + contractId + '/' + scorNumber])
                    .then(() => {
                        setTimeout(() => {
                            window.location.reload();
                        }, 3000);
                    });
            }
        });
    }

    validateResourceId() {
        this.errorNoDataMessage="";
        if (this.validateFormControl(this.contractHeaderForm.get('resourceId'))) {
            const resourceId = this.contractHeaderForm.get('resourceId').value;
            this.contractService.getResource(resourceId).subscribe((response) => {
                if (response) {
                    this.resourceAddresses = response.contractResourceAddress;
                    this.contractHeaderResponse.contract.resourceAddressId = this.getResourceAddressId(
                        response.contractResourceAddress
                    );
                    this.contractHeaderResponse.contract.resourceName = response.name;
                    this.contractHeaderResponse.contract.resourceLegalName = response.legalName;
                    this.vendorInfoDataTable();
                    this.contractHeaderForm.controls.isValidated.setValue(true);
                    this.isResourceIdValidated = true;
                    setTimeout(() => this.resourceIdSpanEl.nativeElement.focus(), 500);
                } else {
                    // artf253087 : PPM:84970 Contract Attempt Validity Resource with no VID
                    this.errorNoDataMessage = dfpsValidatorErrors.MSG_INT_NO_VID_RECORD; 
                }
            });
        } else {
            if (this.errorElement) {
                // for focussing errors while validating resource id.
                setTimeout(() => {
                    this.errorElement.nativeElement.querySelector('#dfpsFormValidationDispayDiv').focus();
                }, 100);
            }
        }
    }

    getResourceAddressId(resourceAddress: ResourceAddress[]) {
        if (resourceAddress) {
            return resourceAddress[0].resourceAddressId;
        }
        return null;
    }

    selectResource() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let returnUrl;
        if (this.contractId) {
            returnUrl = '/financial/contract/header/' + this.contractId + '/' + this.contractNumber;
        } else {
            returnUrl = '/financial/contract/header/0/0';
        }
        // this.searchService.setSearchSource('ResourceSearch');
        this.searchService.setFormData(this.contractHeaderForm.value);
        this.searchService.setFormContent(this.contractHeaderResponse);
        this.searchService.setReturnUrl(returnUrl);
        this.searchService.setInvokingPage('ContractHeader');
        this.router.navigate(
            [
                '/financial/contract/header/' + this.contractId + '/' + this.contractNumber + '/resourcesearch',
                { source: 'non-sscc' },
            ],
            { skipLocationChange: false }
        );
    }

    selectContractManager() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let returnUrl;
        if (this.contractId) {
            returnUrl = '/financial/contract/header/' + this.contractId + '/' + this.contractNumber;
        } else {
            returnUrl = '/financial/contract/header/0/0';
        }
        this.searchService.setSearchSource('ContractManager');
        this.searchService.setFormData(this.contractHeaderForm.value);
        this.searchService.setFormContent(this.contractHeaderResponse);
        this.searchService.setReturnUrl(returnUrl);
        this.router.navigate(
            ['/financial/contract/header/' + this.contractId + '/' + this.contractNumber + '/staffsearch'],
            { skipLocationChange: false }
        );
    }

    selectOrg() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let returnUrl;
        if (this.contractId) {
            returnUrl = '/financial/contract/header/' + this.contractId + '/' + this.contractNumber;
        } else {
            returnUrl = '/financial/contract/header/0/0';
        }
        this.searchService.setFormData(this.contractHeaderForm.value);
        this.searchService.setFormContent(this.contractHeaderResponse);
        this.searchService.setReturnUrl(returnUrl);
        this.router.navigate(
            ['/financial/contract/header/' + this.contractId + '/' + this.contractNumber + '/orgsearch'],
            { skipLocationChange: false }
        );
    }

    selectSponser() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let returnUrl;
        if (this.contractId) {
            returnUrl = '/financial/contract/header/' + this.contractId + '/' + this.contractNumber;
        } else {
            returnUrl = '/financial/contract/header/0/0';
        }
        this.searchService.setSearchSource('Sponser');
        this.searchService.setFormData(this.contractHeaderForm.value);
        this.searchService.setReturnUrl(returnUrl);
        this.router.navigate(
            ['/financial/contract/header/' + this.contractId + '/' + this.contractNumber + '/staffsearch'],
            { skipLocationChange: false }
        );
    }

    vendorInfoDataTable() {
        this.tableColumn = [
            { field: 'vendorId', header: 'Vendor ID', isHidden: false },
            { field: 'addressLine1', header: 'Address Line 1', isHidden: false },
        ];
        this.vendorDataTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: true,
            isPaginator: false,
        };
        if (this.resourceAddresses) {
            this.isVendorInfo = true;
            this.vendorDataTable.tableBody = this.resourceAddresses;
        }
    }

    displayContractPeriods(event) {
        if (this.isCpInfo && event) {
            this.contractService.getContractPeriods(this.contractId).subscribe((response) => {
                this.contractPeriodDatable(response);
            });
        }
    }

    contractPeriodDatable(contractPeriods: any) {
        this.isCpInfo = true;
        this.tableColumn = [
            {
                field: 'period',
                header: 'Period',
                isHidden: false,
                isLink: true,
                url: '/financial/contract/header/:contractId/:scorContractNumber/period/:period',
                urlParams: ['contractId', 'scorContractNumber', 'period'],
                width: 75,
            },
            { field: 'legalIdentifier', header: 'Legal Identifier', isHidden: false, width: 200 },
            { field: 'scorContractNumber', header: 'Contract Number', isHidden: false, width: 200 },
            { field: 'startDate', header: 'Start', isHidden: false, width: 150 },
            { field: 'termDate', header: 'Term', isHidden: false, width: 150 },
            { field: 'closureDate', header: 'Early Term', isHidden: false, width: 150 },
            { field: 'statusCode', header: 'Status', isHidden: false, width: 150 },
            { field: 'renew', header: 'Renew', isHidden: false, isTick: true, width: 100 },
            { field: 'signed', header: 'Signed', isHidden: false, isTick: true, width: 100 },
            { field: 'contractId', isHidden: true, width: 1 },
        ];
        this.cpDataTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: true,
            isPaginator: false,
        };
        if (contractPeriods) {
            this.cpDataTable.tableBody = contractPeriods;
            this.checkIsContractPeriodClosed(contractPeriods);
        }
    }

    checkIsContractPeriodClosed(contractPeriods: any[]) {
        this.isAnyContractPeriodClosed = contractPeriods.filter(this.checkStatusCode).length > 0;
    }

    checkStatusCode(element) {
        return element.statusCode === 'CLT';
    }

    getContractVersion(event) {
        if (event) {
            this.isContractPeriodSelected = true;
            this.selectContractPeriod = event;
            this.contractService.getContractVerions(this.contractId, event.period).subscribe((response) => {
                this.contractVersionDatable(response);
                this.enableServicesHyperLink(response);
                if (document.getElementById('cpDeleteButtonId')) {
                    document.getElementById('cpDeleteButtonId').focus();

                } else if (document.getElementById('cpAdddeButtonId')) {
                    document.getElementById('cpAdddeButtonId').focus();
                } else if (document.getElementById('cvTable')) {
                    document.getElementById('cvTable').focus();
                }
            });
            this.enableTransferHyperLink();
        }
    }

    setSelectedContractPeriod(): any {
        return this.selectContractPeriod;
    }

    getSelectedContractVersion(event: Event) {
        this.selectContractVersion = event;
    }

    contractVersionDatable(response?: any) {
        this.tableColumn = [
            {
                field: 'versionNumber',
                header: 'Version',
                isLink: true,
                width: 105,
                url: '/financial/contract/header/:contractId/:scorContractNumber/period/:period/version/:versionNumber',
                urlParams: ['contractId', 'scorContractNumber', 'period', 'versionNumber'],
            },
            { field: 'effectiveDate', header: 'Effective', width: 200 },
            { field: 'endDate', header: 'End', isHidden: false, width: 200 },
            { field: 'createDate', header: 'Create', isHidden: false, width: 100 },
            { field: 'noShowPercent', header: 'No Show %', isHidden: false, width: 100 },
            { field: 'locked', header: 'Locked', isHidden: false, isTick: true, width: 100 },
            { field: 'comment', header: 'Comments', isHidden: false, width: 300 },
            { field: 'period', isHidden: true, width: 1 },
            { field: 'contractId', isHidden: true, width: 1 },
        ];
        this.cvDataTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: true,
            isPaginator: false,
        };
        this.cvDataTable.tableBody = [];
        if (response) {
            this.isCvInfo = true;
            // setTimeout(() => {
            //     this.cvTableElement.nativeElement.focus();
            // });
            this.cvDataTable.tableBody = response;
        }
    }

    setSelectedResourceAddress(): ResourceAddress {
        if (this.resourceAddresses && this.contractHeaderResponse.contract) {
            for (const resourceAddress of this.resourceAddresses) {
                if (resourceAddress.resourceAddressId === this.contractHeaderResponse.contract.resourceAddressId) {
                    return resourceAddress;
                }
            }
        }
        return;
    }

    getSelectedResourceAddress(selectedRow) {
        if (selectedRow) {
            this.contractHeaderResponse.contract.resourceAddressId = selectedRow.resourceAddressId;
        }
    }

    clearSponsorInfo() {
        if (!this.contractHeaderResponse.contract.backgroundCheck) {
            this.contractHeaderResponse.contract.sponsorEmail = '';
            this.contractHeaderResponse.contract.sponsorName = '';
            this.contractHeaderResponse.contract.sponsorPersonId = null;
            this.contractHeaderResponse.contract.sponsorPhone = '';
            this.contractHeaderResponse.contract.organizationEIN = null;
            this.contractHeaderResponse.contract.organizationId = null;
            this.contractHeaderResponse.contract.organizationLegalName = null;
        }
    }

    deletePeriod() {
        if (this.selectContractPeriod.signed) {
            const initialState = {
                title: 'Contract Period',
                message: 'The Contract Period you are trying to delete has already been signed and cannot be deleted.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                document.getElementById('cpDeleteButtonId').focus();
            });
        } else if (this.selectContractPeriod.statusCode !== 'PND') {
            const initialState = {
                title: 'Contract Period',
                message:
                    'The Contract Period you are trying to delete does not have a status of Pending, and cannot be deleted.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                document.getElementById('cpDeleteButtonId').focus();
            });
        } else {
            if (this.isContractPeriodSelected) {
                const initialState = {
                    title: 'Contract Period',
                    message: 'Are you sure you want to delete the information?',
                    showCancel: true,
                };
                const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                    if (result === true) {
                        this.contractService
                            .deleteContractPeriod(this.contractId, this.selectContractPeriod.period)
                            .subscribe((res) => {
                                this.contractHeaderResponse = null;
                                this.intializeScreen();
                                this.displayContractPeriods(true);
                                this.contractVersionDatable(null);
                            });
                    }
                    document.getElementById('cpDeleteButtonId').focus();
                });
            }
        }
    }

    addPeriod() {
        if (!this.contractHeaderResponse.contract.versionLocked) {
            const initialState = {
                title: 'Contract Period',
                message: 'A contract period may not be created if a version is unlocked.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-lg',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                document.getElementById('cpAddButtonId').focus();
            });
        } else if (this.cpDataTable && this.cpDataTable.tableBody[0] && !this.cpDataTable.tableBody[0].signed) {
            const initialState = {
                title: 'Contract Period',
                message: 'You cannot add a new period while the most recent period is unsigned.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-lg',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                document.getElementById('cpAddButtonId').focus();
            });
        } else {
            this.router.navigate(
                ['financial/contract/header/' + this.contractId + '/' + this.contractNumber + '/period/0'],
                { skipLocationChange: true }
            );
        }
    }

    addVersion() {
        if (this.cvDataTable.tableBody.length > 0) {
            const versionData = this.cvDataTable.tableBody[0];
            if (versionData.period === this.selectContractPeriod.period && !versionData.locked) {
                const initialState = {
                    title: 'Contract Version',
                    message: 'You cannot add a new version while the most recent version is not locked.',
                    showCancel: false,
                };
                const modal = this.modalService.show(DfpsConfirmComponent, {
                    class: 'modal-lg',
                    initialState,
                });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                    document.getElementById('addVersion').focus();
                });
            } else {
                this.routeToVersionDetail();
            }
        } else {
            this.routeToVersionDetail();
        }
    }

    private routeToVersionDetail() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.router.navigate(
            [
                'financial/contract/header/' +
                this.contractId +
                '/' +
                this.contractNumber +
                '/period/' +
                this.selectContractPeriod.period +
                '/version/0',
            ],
            { skipLocationChange: false }
        );
    }

    navigateToServices() {
        if (this.selectContractVersion) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(
                [
                    'financial/contract/header/' +
                    this.contractId +
                    '/' +
                    this.contractNumber +
                    '/period/' +
                    this.selectContractPeriod.period +
                    '/version/' +
                    this.selectContractVersion.versionNumber +
                    '/service/',
                ],
                { skipLocationChange: false }
            );
        } else {
            const initialState = {
                title: 'Contract Service',
                message: 'Please select a contract version row to perform this action.',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
        }
    }

    navigateToTransfer() {
        FormUtils.enableFormControlStatus(this.contractHeaderForm, ['contractVersionLocked']);
        this.contractHeaderForm.get('contractVersionLocked').setValue(this.checkIsContractVersionIsLocked());
        if (this.validateFormControl(this.contractHeaderForm.get('contractVersionLocked'))) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(
                [
                    'financial/contract/header/' +
                    this.contractId +
                    '/' +
                    this.contractNumber +
                    '/period/' +
                    this.selectContractPeriod.period +
                    '/version/' +
                    this.cvDataTable.tableBody[0].versionNumber +
                    '/budget-transfer/',
                ],
                { skipLocationChange: false }
            );
        }
        FormUtils.disableFormControlStatus(this.contractHeaderForm, ['contractVersionLocked']);
    }

    checkIsContractVersionIsLocked() {
        return this.cvDataTable.tableBody && this.cvDataTable.tableBody[0].locked;
    }

    OnDestroy() {
        this.searchService.setSelectedOrg(null);
        this.searchService.setSelectedResource(null);
        this.searchService.setSelectedStaff(null);
        this.searchService.setFormContent(null);
        this.searchService.setFormData(null);
        this.cookieService.delete('search_return_url');
        this.cookieService.delete('form_data');
    }

    @HostListener('window:popstate' || 'window:hashchange', ['$event'])
    onPopState(event) {
        localStorage.setItem('backFrom', 'ContractHeader');
    }
}
