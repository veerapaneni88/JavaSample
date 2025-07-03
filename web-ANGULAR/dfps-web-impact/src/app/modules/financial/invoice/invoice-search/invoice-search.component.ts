import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Observable, Subscription } from 'rxjs';
import {
    DataTable,
    FormValidationError,
    NavigationService,
    DirtyCheck,
    DfpsCommonValidators,
    Pagination,
    PaginationUtils,
    DfpsFormValidationDirective,
    AuthService,
    UserInfo,
    DfpsConfirmComponent,
    DropDown,
    FormUtils,
} from 'dfps-web-lib';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { LazyLoadEvent } from 'primeng/api/public_api';
import {
    InvoiceSearchDisplay,
    InvoiceSearch,
    InvoiceSearchResponse,
    InvoiceSearchResult,
} from '../model/InvoiceSearch';
import { InvoiceService } from '../service/invoice.service';
import { InvoiceSearchValidators } from './invoice-search.validator';
import { BsModalService } from 'ngx-bootstrap/modal';
import {InvoiceHeaderValidators} from "@financial/invoice/invoice-header/invoice-header.validator";

@Component({
    templateUrl: './invoice-search.component.html',
})
export class InvoiceSearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    invoiceSearchForm: FormGroup;
    invoiceSearchRequest: InvoiceSearch;
    displaySearchResponseObs: Observable<InvoiceSearchDisplay>;
    displaySearchResponse: InvoiceSearchDisplay;

    invoiceSearchResponse: InvoiceSearchResponse;
    selectedRow: InvoiceSearchResult;
    displaySearchResults = false;
    userInfo: UserInfo;
    userInfoSubscription: Subscription;

    tableBody: any[];
    tableColumn: any[];
    urlKey: any[];
    urlPath: string;
    dataTable: DataTable;
    @ViewChild('results') resultsEle: ElementRef;
    searchButtonClicked = false;
    validationErrors: FormValidationError[] = [];
    validationErrorsLength = 0;
    currentDate = new Date();
    agencyAccounts: DropDown[];
    isUserTypeExternal: boolean;
    private readonly DEFAULT_TYPE = 'ALL';
    private readonly DEFAULT_PHASE = 'ALL';

    DEFAULT_PAGE_SIZE = this.getPageSize();
    currentPageNumber = 0;
    isBackFromHeader = false;

    constructor(
        private formBuilder: FormBuilder,
        private invoiceService: InvoiceService,
        private route: Router,
        private navigationService: NavigationService,
        private authService: AuthService,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.setUserData();
    }

    setUserData() {
        // reset invoice ID, so the 3rd level menu is removed
        this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
        this.navigationService.setUserDataValue('idInvoice', '0');
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Invoice Search');
        localStorage.setItem('crmData', '');
        this.createForm();
        this.getUserInfo();
        this.navigationService.loadMenuSubject.next(true);
        this.displaySearchResponseObs = this.invoiceService.displayInvoiceSearch();
        this.displaySearchResponseObs.subscribe((displayInvoiceSearch) => {
            if (displayInvoiceSearch) {
                this.displaySearchResponse = displayInvoiceSearch;
                this.isUserTypeExternal = displayInvoiceSearch.userType === 'external';
                this.invoiceSearchForm.setValidators(
                    InvoiceSearchValidators.validateRequiredFields(this.isUserTypeExternal)
                );
                if (displayInvoiceSearch.accountIds) {
                    this.agencyAccounts = this.generateDropDownValues(displayInvoiceSearch.accountIds);
                    this.invoiceSearchForm.get('agencyAccountId').setValue(displayInvoiceSearch.accountIds[0]);
                }
                this.determineUserTypes();
                if (this.isUserTypeExternal) {
                    this.invoiceSearchForm.get('region').setValue('');
                    this.invoiceSearchForm.get('type').setValue('');
                }
            }
        });

        if (localStorage.getItem('backFrom') === 'InvoiceHeader') {
            this.doSearchForBack();
        } else {
            localStorage.removeItem('InvoicetSearch');
            localStorage.removeItem('invoiceSearchLink');
        }
    }

    getUserInfo() {
        this.userInfoSubscription = this.authService.getUserInfo().subscribe((userInfo) => {
            this.userInfo = userInfo;
            let userRegionStr = userInfo.userRegion;
            const userRegion = +userRegionStr;
            if (userRegion > 99) {
                userRegionStr = '99';
            } else if (userRegion < 10) {
                userRegionStr = '0' + userRegion.toString();
            } else if (userRegion === 10) {
                userRegionStr = '10';
            }
            this.invoiceSearchForm.get('region').setValue(userRegionStr);
        });
    }

    generateDropDownValues(dropdownData) {
        return dropdownData.map((data) => ({ code: data, decode: data }));
    }

    createForm() {
        let year = this.currentDate.getFullYear();
        let month = this.currentDate.getMonth();
        if (month === 0) {
            month = 12;
            year = year - 1;
        }
        this.invoiceSearchForm = this.formBuilder.group({
            invoiceId: ['', [DfpsCommonValidators.validateMaxId]],
            agencyAccountId: ['', [DfpsCommonValidators.validateMaxId]],
            resourceId: ['', [DfpsCommonValidators.validateMaxId]],
            type: [this.DEFAULT_TYPE],
            phase: [this.DEFAULT_PHASE],
            region: [''],
            invoiceMonth: [month, DfpsCommonValidators.validateMonth],
            invoiceYear: [year, DfpsCommonValidators.validateYear],
            regionMonthYearValid: [],
            providerAuthorized: [false],
           accountResourceIdValid: [],
        },
       );
    }

    determineUserTypes() {
        const agencyAccountIdControl = this.invoiceSearchForm.get('agencyAccountId');
        if (this.isUserTypeExternal) {
            FormUtils.disableFormControlStatus(this.invoiceSearchForm, ['resourceId', 'type', 'region']);
            agencyAccountIdControl.setValidators(Validators.required);
            this.invoiceSearchForm.get('region').clearValidators();
        } else {
            this.invoiceSearchForm.enable();
            agencyAccountIdControl.setValidators(DfpsCommonValidators.validateMaxId);
        }
    }

    doSearchForBack() {
        localStorage.removeItem('backFrom');
        this.isBackFromHeader = true;
        this.searchButtonClicked = true;
        this.displaySearchResults = false;
        this.invoiceSearchRequest = this.invoiceSearchForm.getRawValue();
        this.initializeDataTable();
        this.loadDataLazy(null);
        this.setFormValues();
    }

    doSearch() {
        this.isBackFromHeader = false;
        this.currentPageNumber = 0;
        localStorage.removeItem('backFrom');
        localStorage.removeItem('InvoiceSearch');
        this.searchButtonClicked = true;
        this.displaySearchResults = false;
        if (this.validateFormGroup(this.invoiceSearchForm) && this.invoiceSearchForm.value) {
            this.invoiceSearchRequest = this.invoiceSearchForm.getRawValue();
            this.initializeDataTable();
            this.loadDataLazy(null);
        }
    }

    loadDataLazy(event: LazyLoadEvent) {
        const invoiceSearchPagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
            sortField: null,
            sortDirection: null,
        };

        const invoiceSearchCopy: any = JSON.parse(localStorage.getItem('InvoiceSearch'));
        if (invoiceSearchCopy) {
            if (event) {
                invoiceSearchCopy.pageNumber = PaginationUtils.getPageNumber(event);
                invoiceSearchCopy.pageSize = (invoiceSearchCopy.pageSize === null)
                    ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event);
                invoiceSearchCopy.sortField = PaginationUtils.getSortField(
                    event,
                    this.invoiceSearchResponse ? this.invoiceSearchResponse.sortColumns : '',
                    ''
                );
                invoiceSearchCopy.sortDirection = PaginationUtils.getSortOrder(event);
                this.searchInvoice(invoiceSearchCopy);
            } else {
                this.DEFAULT_PAGE_SIZE = this.getPageSize();
                this.currentPageNumber = invoiceSearchCopy.pageNumber * this.getPageSize();
                this.searchInvoice(invoiceSearchCopy);
            }
        } else {
            this.searchInvoice(Object.assign(invoiceSearchPagination, this.invoiceSearchRequest));
        }

    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
    }

    searchInvoice(invoiceSearchPagniation: any) {
        this.invoiceService
            .searchInvoice(invoiceSearchPagniation)
            .subscribe((response: InvoiceSearchResponse) => {
                localStorage.setItem('InvoiceSearch_SortFields', JSON.stringify(response.sortColumns));
                // if the user has searched by valid invoice id, redirect to invoice header
                const invoiceId = this.invoiceSearchForm.get('invoiceId').value;
                if (response.invoiceSearchResults.length === 1 && invoiceId && !this.isBackFromHeader) {
                    const headerUrl =
                        InvoiceService.HEADER_FRONTEND_URL + '/' + response.invoiceSearchResults[0].invoiceId;
                    this.route.navigate([headerUrl]);
                } else {
                    this.invoiceSearchResponse = response;
                    this.displaySearchResults = true;
                    this.displayDataTable();
                    if (this.searchButtonClicked) {
                        setTimeout(() => {
                            this.resultsEle.nativeElement.focus();
                        }, 0);
                    }
                }
            });
        localStorage.removeItem('InvoiceSearch');
        localStorage.setItem('InvoiceSearch', JSON.stringify(invoiceSearchPagniation));
        localStorage.setItem('InvoiceSearch_PageSize', invoiceSearchPagniation.pageSize);
    }

    add() {
        const headerUrl = InvoiceService.HEADER_FRONTEND_URL + '/0';
        this.route.navigate([headerUrl]);
    }

    newUsing() {
        // check if an invoice is selected
        if (!(this.selectedRow && this.selectedRow.invoiceId)) {
            const initialState = {
                title: 'Invoice Search',
                message: 'Please select at least one row to perform this action',
                showCancel: false,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, {
                class: 'modal-md modal-dialog-centered',
                initialState,
            });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
        } else {
            const headerUrl = InvoiceService.HEADER_FRONTEND_URL + '/0';
            this.route.navigate([headerUrl], { queryParams: { using: this.selectedRow.invoiceId } });
        }
    }

    initializeDataTable() {
        this.tableColumn = [
            {
                field: 'invoiceId',
                header: 'Invoice ID',
                handleClick: true,
                width: 100,
                url: InvoiceService.HEADER_FRONTEND_URL + '/:invoiceId',
                urlParams: ['invoiceId'],
                uniqueLinkKey: 'invoiceId'
            },
            { field: 'type', header: 'Type', width: 210 },
            { field: 'submittedDate', header: 'Submitted Date', width: 120 },
            {
                field: 'agencyAccountId',
                header: 'Agency Account ID',
                width: 150,
            },
            { field: 'phase', header: 'Phase', width: 150 },
            {
                field: 'validatedAmount',
                header: 'Validated Amount',
                isCurrency: true,
                desc: null,
                width: 150,
            },
            { field: 'resourceName', header: 'Resource Name', width: 230 },
            { field: 'resourceId', header: 'Resource ID', width: 120 },
            { field: 'region', header: 'Region', width: 100 },
            { field: 'accessFlag', isHidden: true, width: 1 },
        ];
        this.dataTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: true,
            isPaginator: true
        };
    }

    displayDataTable() {
        this.dataTable = {
            tableBody: this.invoiceSearchResponse.invoiceSearchResults,
            tableColumn: this.tableColumn,
            isSingleSelect: true,
            isPaginator: true,
            totalRows: this.invoiceSearchResponse.totalElements,
        };
        const invoiceSearchLink = JSON.parse(localStorage.getItem('invoiceSearchLink'));
        if (invoiceSearchLink) {
            setTimeout(() => {
                const element: any = document.querySelector('#id' + invoiceSearchLink.invoiceId);
                if (element) {
                    element.focus();
                }
            }, 1000);
        }
    }

    handleInvoiceLinkClick(event: any) {
        if (event.tableRow.invoiceId) {
            if (event.tableRow.accessFlag === true) {
                this.route.navigate([event.link]);
                localStorage.setItem('invoiceSearchLink', JSON.stringify({
                    invoiceId: event.tableRow.invoiceId
                }));
            } else {
                this.showUnAuthorizedInvoiceMessage();
            }
        }
    }

    showUnAuthorizedInvoiceMessage() {
        const initialState = {
            title: 'Invoice Search',
            message: 'You are not authorized to view this invoice.',
            showCancel: false,
        };
        this.modalService.show(DfpsConfirmComponent, {
            class: 'modal-md modal-dialog-centered',
            initialState,
        });
    }

    clear() {
        localStorage.removeItem('InvoiceSearch');
        window.location.reload();
    }

    ngOnDestroy() {
        this.userInfoSubscription.unsubscribe();
    }

    getSortField(value): any {
        return this.getKeyByValue(JSON.parse(localStorage.getItem('InvoiceSearch_SortFields')), value)
    }

    getKeyByValue(object, value): any {
        return Object.keys(object).find(key => object[key] === value);
    }

    getPageSize(): any {
        const pageSizeData = localStorage.getItem('InvoiceSearch_PageSize');
        return pageSizeData ? Number(pageSizeData) : 50 as number;
    }

    setFormValues() {
        const invoiceSearchReq: any = JSON.parse(localStorage.getItem('InvoiceSearch'));
        Object.keys(invoiceSearchReq).forEach((key) => {
            if (this.invoiceSearchForm.get(key)) {
                this.invoiceSearchForm.get(key).setValue(invoiceSearchReq[key]);
            }
        })
    }
}
