import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { CentralRegistrySearchRequest, CentralRegistrySearchResponse } from '@shared/model/CentralRegistrySearch';
import { SearchService } from '@shared/service/search.service';
import { DataTable, DfpsCommonValidators, DfpsFormValidationDirective, DirtyCheck, NavigationService, Pagination, PaginationUtils } from 'dfps-web-lib';
import { LazyLoadEvent } from 'primeng/api';
import { CentralRegistryPersonSearchValidators } from './central-registry-person-search.validator';
import { HelpService } from '../../../common/impact-help.service';

@Component({
    selector: 'central-registry-person-search',
    templateUrl: './central-registry-person-search.component.html'
})
export class CentralRegistryPersonSearchComponent extends DfpsFormValidationDirective implements OnInit {
    centralRegistryPersonSearchForm: FormGroup;
    centralRegistrySearchResponse: CentralRegistrySearchResponse;
    centralRegistrySearchRequest: CentralRegistrySearchRequest;
    displaySearchResults = false;
    isSearch = false;

    searchType: any;
    tableBody: any[];
    tableColumn: any[];
    dataTable: DataTable;
    urlKey: any[];
    urlPath: string;
    currentPageNumber = 0;
    sortOrder: number;
    sortField: string;
    DEFAULT_PAGE_SIZE = this.getPageSize();

    maxDate = { year: new Date().getUTCFullYear(), month: new Date().getMonth() + 1, day: new Date().getDate() }
    minDate = { year: new Date().getUTCFullYear() - 120, month: 12, day: 31 }

    constructor(private navigationService: NavigationService,
        private formBuilder: FormBuilder,
        private searchService: SearchService,
        private route: Router,
        private helpService: HelpService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.navigationService.setUserDataValue('idCrpRequest', '0'); 
    }

    createForm() {
        this.centralRegistryPersonSearchForm = this.formBuilder.group(
            {
                firstName: [''],
                lastName: [''],
                dob: [''],
                ssn: [''],
                requestId: [''],
                searchType: ['1'],
                searchCriteria: ['']
            }, {
            validators: [CentralRegistryPersonSearchValidators.personSearchValidation,
            CentralRegistryPersonSearchValidators.ssnValidator]
        });
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Central Registry Person Search');
        this.helpService.loadHelp('Search');
        this.createForm();
        this.searchType = [
            { label: 'Phonetic Name', value: '1' },
            { label: 'Partial Name', value: '2' },
        ];
        
    }

    doSearch() {
        localStorage.removeItem('CentralRegistrySearch');
        this.sortOrder = 0;
        this.sortField = null;
        this.currentPageNumber = 0;

        this.displaySearchResults = false;
        this.centralRegistryPersonSearchForm.controls.dob.setValue(this.centralRegistryPersonSearchForm.controls.dob.value.replace(/\s/g, ''));
        if (this.validateFormGroup(this.centralRegistryPersonSearchForm) && this.centralRegistryPersonSearchForm.value) {
            this.centralRegistrySearchRequest = this.centralRegistryPersonSearchForm.value;
            this.initializeDataTable();
            this.loadDataLazy(null);
        }
    }

    loadDataLazy(event: LazyLoadEvent) {
        const centralRegistrySearchPagination: Pagination = {
            pageNumber: PaginationUtils.getPageNumber(event),
            pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
            sortField: PaginationUtils.getSortField(event, this.centralRegistrySearchResponse ?
                this.centralRegistrySearchResponse.sortColumns : '', 'requestId'),
            sortDirection: PaginationUtils.getSortOrder(event)
        };
        const centralRegistrySearchCopy: any = JSON.parse(localStorage.getItem('CentralRegistrySearch'));

        if (centralRegistrySearchCopy) {
            if (event) {
                centralRegistrySearchCopy.pageNumber = PaginationUtils.getPageNumber(event);
                centralRegistrySearchCopy.pageSize = (centralRegistrySearchCopy.pageSize === null)
                    ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event);
                centralRegistrySearchCopy.sortField = PaginationUtils.getSortField(event, this.centralRegistrySearchResponse ?
                    this.centralRegistrySearchResponse.sortColumns : '', 'requestId');
                centralRegistrySearchCopy.sortDirection = PaginationUtils.getSortOrder(event);
                this.searchCentralRegistry(Object.assign(centralRegistrySearchPagination, centralRegistrySearchCopy));
            } else {
                this.DEFAULT_PAGE_SIZE = this.getPageSize();
                this.currentPageNumber = centralRegistrySearchCopy.pageNumber * this.getPageSize();
                this.sortField = this.getSortField(centralRegistrySearchCopy.sortField);
                this.sortOrder = centralRegistrySearchCopy.sortDirection === 'ASC' ? 1 : -1;
                this.searchCentralRegistry(centralRegistrySearchCopy);
            }
        } else {
            this.searchCentralRegistry(Object.assign(centralRegistrySearchPagination, this.centralRegistrySearchRequest));
        }
    }

    searchCentralRegistry(centralRegistrySearchPagination: any) {
        this.searchService.searchCentralRegistryPerson(centralRegistrySearchPagination).subscribe(
            response => {
                localStorage.setItem('centralRegistrySearch_SortFields', JSON.stringify(response.sortColumns));
                this.centralRegistrySearchResponse = response;
                this.displaySearchResults = true;
                this.displayDataTable();
                this.isSearch = true;
            }
        );
        localStorage.removeItem('CentralRegistrySearch');
        localStorage.setItem('CentralRegistrySearch', JSON.stringify(centralRegistrySearchPagination));
        localStorage.setItem('CentralRegistrySearch_PageSize', centralRegistrySearchPagination.pageSize);
    }

    initializeDataTable() {
        this.tableColumn = [
            {
                field: 'requestId', header: 'Request ID', width: 150, handleClick: true, sortable: true,
                uniqueLinkKey: 'primReqId',
                url: '/search/central-registry-person-search/centralRegistryRequestDetail/:primReqId',
                urlParams: ['primReqId'], isHidden: false
            },
            { field: 'dtSubmitted', header: 'Submit Date and Time', width: 200 },
            { field: 'matchName', header: 'Match Name', width: 150 },
            { field: 'dob', header: 'DOB', width: 100 },
            { field: 'age', header: 'Age', width: 100 },
            { field: 'gender', header: 'Gender', width: 100 },
            { field: 'city', header: 'City', width: 100 },
            { field: 'county', header: 'County', width: 100 },
            { field: 'addressLine1', header: 'Street', width: 200 },
            { field: 'ssn', header: 'SSN', width: 100 },
            { field: 'primaryName', header: 'Primary Name', width: 150 },
            { field: 'score', header: 'Score', width: 100 }
        ];
        this.dataTable = {
            tableColumn: this.tableColumn,
        };
    }

    displayDataTable() {
        this.dataTable = {
            tableBody: this.centralRegistrySearchResponse.centralRegSearchResults,
            tableColumn: this.tableColumn,
            urlKey: this.urlKey,
            urlPath: this.urlPath,
            isMultiSelect: false,
            isPaginator: true,
            totalRows: this.centralRegistrySearchResponse.totalElements
        };
    }

    handleRouting(event: any) {
        if (event.tableRow.primReqId) {
            const link = (event.link as string).replace('null', '0');
            this.navigationService.setUserDataValue('idCrpRequest', event.tableRow.primReqId);
            this.route.navigate([link]);
        }
    }

    getPageSize(): number {
        const pageSizeData = localStorage.getItem('CentralRegistrySearch_PageSize');
        return pageSizeData ? Number(pageSizeData) : 50 as number;
    }

    getSortField(value): any {
        return this.getKeyByValue(JSON.parse(localStorage.getItem('centralRegistrySearch_SortFields')), value)
    }

    getKeyByValue(object, value): any {
        return Object.keys(object).find(key => object[key] === value);
    }
}