import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import { HelpService } from 'app/common/impact-help.service';
import {
  DataTable,
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormValidationError,
  NavigationService,
  Pagination,
  PaginationUtils
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { LazyLoadEvent } from 'primeng/api';
import { Observable } from 'rxjs';
import { ContractSearchResponse, DisplayContractSearchResponse } from '../model/ContractSearch';
import { ContractService } from '../service/contract.service';
import { ContractSearchRequest } from './../model/ContractSearch';
import { ContractSearchValidators } from './contract-search.validator';

@Component({
  templateUrl: './contract-search.component.html'
})
export class ContractSearchComponent extends DfpsFormValidationDirective implements OnInit {
  contractSerachForm: FormGroup;
  contractSearchRequest: ContractSearchRequest;
  displaySearchResponse: Observable<DisplayContractSearchResponse>;
  contractSearchResponse: ContractSearchResponse;
  displaySearchResults = false;

  @ViewChild('errors') errorElement: ElementRef;

  tableBody: any[];
  tableColumn: any[];
  urlKey: any[];
  urlPath: string;
  dataTable: DataTable;
  @ViewChild('results') resultsEle: ElementRef;
  searchButtonClicked = false;
  validationErrors: FormValidationError[] = [];
  validationErrorsLength = 0;
  DEFAULT_PAGE_SIZE = this.getPageSize();
  currentPageNumber = 0;
  sortOrder: number;
  sortField: string;

  constructor(private formBuilder: FormBuilder,
    private cookieService: CookieService,
    private searchService: SearchService,
    private contractSearchService: ContractService,
    private route: Router,
    private modalService: BsModalService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    this.setUserData();
  }

  setUserData() {
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserData({ null: null });
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Contract Search');
    this.helpService.loadHelp('Finance');
    this.createForm();
    this.navigationService.loadMenuSubject.next(true);
    this.displaySearchResponse = this.contractSearchService.displayContractSearch();
    this.searchService.setSelectedOrg(null);
    this.searchService.setSelectedResource(null);
    this.searchService.setSelectedStaff(null);
    this.searchService.setFormContent(null);
    this.searchService.setFormData(null);
    this.cookieService.delete('search_return_url');
    this.cookieService.delete('form_data');
    if (localStorage.getItem('backFrom') === 'ContractHeader') {
      this.doSearchForBack();
    } else {
      localStorage.removeItem('ContractSearch');
      localStorage.removeItem('contractSearchLink');
    }
  }

  createForm() {
    this.contractSerachForm = this.formBuilder.group({
      contractNumber: ['', [DfpsCommonValidators.cannotContainSpace, Validators.maxLength(15)]],
      programType: ['', [Validators.required]],
      region: [''],
      agencyAccountId: ['', [DfpsCommonValidators.validateId, Validators.maxLength(10)]],
      resourceId: ['', [DfpsCommonValidators.validateId, Validators.maxLength(10)]],
      functionType: [''],
      dateFrom: ['', [DfpsCommonValidators.validateDate]],
      dateTo: ['', [DfpsCommonValidators.validateDate]],
      budgetLimit: ['']
    }, {
      validators: [
        DfpsCommonValidators.compareDates('dateFrom', 'dateTo'),
        ContractSearchValidators.validateProgramType
      ]
    });
  }

  doSearch() {
    localStorage.removeItem('backFrom');
    localStorage.removeItem('ContractSearch');
    localStorage.removeItem('contractSearchLink');
    this.sortOrder = 0;
    this.sortField = null;
    this.currentPageNumber = 0;

    this.searchButtonClicked = true;
    this.displaySearchResults = false;
    if (this.validateFormGroup(this.contractSerachForm) && this.contractSerachForm.value) {
      this.contractSearchRequest = this.contractSerachForm.value;
      this.initializeDataTable();
      this.loadDataLazy(null);
    }
  }

  doSearchForBack() {
    this.searchButtonClicked = true;
    this.displaySearchResults = false;
    this.initializeDataTable();
    this.loadDataLazy(null);
    localStorage.removeItem('backFrom');
    this.setFormValue();
  }

  loadDataLazy(event: LazyLoadEvent) {
    const contractSearchPagination: Pagination = {
      pageNumber: PaginationUtils.getPageNumber(event),
      pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
      sortField: PaginationUtils.getSortField(event, this.contractSearchResponse ?
        this.contractSearchResponse.sortColumns : '', 'CR.name,contractId'),
      sortDirection: PaginationUtils.getSortOrder(event)
    };
    const contractSearchCopy: any = JSON.parse(localStorage.getItem('ContractSearch'));

    if (contractSearchCopy) {
      if (event) {
        contractSearchCopy.pageNumber = PaginationUtils.getPageNumber(event);
        contractSearchCopy.pageSize = (contractSearchCopy.pageSize === null)
          ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event);
        contractSearchCopy.sortField = PaginationUtils.getSortField(event, this.contractSearchResponse ?
          this.contractSearchResponse.sortColumns : '', 'CR.name,contractId');
        contractSearchCopy.sortDirection = PaginationUtils.getSortOrder(event);
        this.searchContract(Object.assign(contractSearchPagination, contractSearchCopy));
      } else {
        this.DEFAULT_PAGE_SIZE = this.getPageSize();
        this.currentPageNumber = contractSearchCopy.pageNumber * this.getPageSize();
        this.sortField = this.getSortField(contractSearchCopy.sortField);
        this.sortOrder = contractSearchCopy.sortDirection === 'ASC' ? 1 : -1;
        this.searchContract(contractSearchCopy);
      }
    } else {
      this.searchContract(Object.assign(contractSearchPagination, this.contractSearchRequest));
    }
  }

  searchButtonResultOnBlur() {
    this.searchButtonClicked = false;
  }

  searchContract(contractSearchPagination: any) {
    this.contractSearchService.searchContract(contractSearchPagination).subscribe(
      response => {
        localStorage.setItem('ContractSearch_SortFields', JSON.stringify(response.sortColumns));
        this.contractSearchResponse = response;
        this.displaySearchResults = true;
        this.displayDataTable();
        if (this.searchButtonClicked) {
          setTimeout(() => {
            this.resultsEle.nativeElement.focus();
          }, 0);
        }
      }
    );
    localStorage.removeItem('ContractSearch');
    localStorage.setItem('ContractSearch', JSON.stringify(contractSearchPagination));
    localStorage.setItem('ContractSearch_PageSize', contractSearchPagination.pageSize);
  }

  add() {
    this.route.navigate(['financial/contract/header/0/0']);
  }

  initializeDataTable() {
    this.tableColumn = [
      {
        field: 'resourceName', header: 'Resource Name', width: 225, handleClick: true,
        uniqueLinkKey: 'agencyAccountId',
        url: '/financial/contract/header/:agencyAccountId/:contractNumber',
        urlParams: ['agencyAccountId', 'contractNumber'], isHidden: false
      },
      { field: 'contractNumber', header: 'Contract Number', width: 150 },
      {
        field: 'agencyAccountId', header: 'Agency Account ID', sortable: true, width: 170
      },
      { field: 'vendorId', header: 'Vendor ID', sortable: true, desc: null, width: 150 },
      { field: 'managerName', header: 'Manager', sortable: true, desc: null, width: 150 },
      { field: 'region', header: 'Region', sortable: true, desc: null, width: 150 },
      { field: 'functionType', header: 'Function Type', sortable: true, width: 150 },
      { field: 'programType', header: 'Program Type', sortable: true, desc: null, width: 150 },
      { field: 'budgetLimit', header: 'Budget Limit', sortable: true, desc: null, width: 150 },
      { field: 'resourceId', header: 'Resource ID', sortable: true, width: 150 }
    ];
    this.dataTable = {
      tableColumn: this.tableColumn,
    };
  }

  displayDataTable() {
    this.dataTable = {
      tableBody: this.contractSearchResponse.contractSearchResults,
      tableColumn: this.tableColumn,
      urlKey: this.urlKey,
      urlPath: this.urlPath,
      isMultiSelect: false,
      isPaginator: true,
      totalRows: this.contractSearchResponse.totalElements
    };
    const contractSearchUrl = JSON.parse(localStorage.getItem('contractSearchLink'));
    if (contractSearchUrl) {
      setTimeout(() => {
        const element: any = document.querySelector('#id' + contractSearchUrl.agencyAccountId);
        if (element) {
          element.focus();
        }
      }, 1000)
    }
  }

  handleRouting(event: any) {
    if (event.tableRow.vendorId) {
      const link = (event.link as string).replace('null', '0');
      this.route.navigate([link]);
      localStorage.setItem('contractSearchLink', JSON.stringify({
        resourceName: event.tableRow.resourceName,
        agencyAccountId: event.tableRow.agencyAccountId
      }));
    } else {
      const initialState = {
        title: 'Contract Search',
        message: 'No Vendor IDs are maintained for this resource.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });

    }
  }

  clear() {
    localStorage.removeItem('ContractSearch');
    window.location.reload();
  }

  getSortField(value): any {
    return this.getKeyByValue(JSON.parse(localStorage.getItem('ContractSearch_SortFields')), value)
  }

  getKeyByValue(object, value): any {
    return Object.keys(object).find(key => object[key] === value);
  }

  getPageSize(): number {
    const pageSizeData = localStorage.getItem('ContractSearch_PageSize');
    return pageSizeData ? Number(pageSizeData) : 50 as number;
  }

  setFormValue() {
    const contractSearchReq: any = JSON.parse(localStorage.getItem('ContractSearch'));
    Object.keys(contractSearchReq).forEach((key) => {
      if (this.contractSerachForm.get(key)) {
        this.contractSerachForm.get(key).setValue(contractSearchReq[key]);
      }
    })
  }

}
function loadHelp() {
  throw new Error('Function not implemented.');
}

