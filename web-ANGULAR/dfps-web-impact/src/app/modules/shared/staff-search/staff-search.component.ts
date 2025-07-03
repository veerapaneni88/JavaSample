import { Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DisplayStaffSearchResponse, StaffSearchRequest, StaffSearchResponse } from '@shared/model/StaffSearch';
import { ResourceSearchValidators } from '@shared/resource-search/resource-search-validator';
import { SearchService } from '@shared/service/search.service';
import {
  DataTable,
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  SET,
  DirtyCheck,
  DropDown,
  ENVIRONMENT_SETTINGS,
  ERROR_RESET,
  NavigationService,
  Pagination,
  PaginationUtils,
  SUCCESS_RESET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';


@Component({
  selector: 'staff-search',
  templateUrl: './staff-search.component.html',
  styleUrls: ['./staff-search.component.css'],
})
export class StaffSearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  currentURL: string;
  constructor(
    private cookieService: CookieService,
    private searchService: SearchService,
    private formBuilder: FormBuilder,
    private router: Router,
    private modalService: BsModalService,
    private navigationService: NavigationService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  tableBody: any[];
  tableColumn: any[];
  staffSearchDataTable: DataTable;

  searchTypeData: any;
  statusData: any;
  staffSearchForm: FormGroup;
  staffSearchBasicOtherIDTypeList: DropDown[];
  staffSearchAdvProgramList: DropDown[];
  staffSearchAdvRegDivList: DropDown[];
  staffSearchAdvCountyList: DropDown[];
  staffSearchAdvUnitSpecializationList: DropDown[];
  staffSearchAdvExternalStaffTypeList: DropDown[];
  displayStaffSearchResponse: DisplayStaffSearchResponse;
  staffSearchResponse: StaffSearchResponse[];
  staffSearchRequest: StaffSearchRequest[];
  selectedStaff: StaffSearchResponse;
  showStaffSearchTable = false;
  displayCancel: boolean = false;
  @ViewChild('searchButton') searchButtonEleRef: ElementRef;
  searchButtonClicked = false;
  @ViewChild('results') resultsEle: ElementRef;

  ngOnInit(): void {
    this.navigationService.setTitle('Staff Search');
    const helpUrl = this.environmentSettings.helpUrl;
    this.currentURL = this.router.url;
    this.environmentSettings.helpUrl = helpUrl.replace('Finance', 'Search');
    this.clearServerMsg();
    this.initializeDataTable();
    this.searchTypeData = [
      { label: 'Partial Name', value: '1' },
      { label: 'Phonetic Name', value: '2' },
    ];

    this.statusData = [
      { label: 'Active Staff Only', value: 'Y' },
      { label: 'All Staff', value: 'N' },
    ];

    this.searchService.searchStaffDisplay().subscribe((response) => {
      this.displayStaffSearchResponse = response;
      this.displayStaffSearchResponse.regions.sort((a, b) => (a.code > b.code) ? 1 : -1);
      this.staffSearchForm.controls.program.setValue(this.displayStaffSearchResponse.defaultUserProgram);
      this.staffSearchForm.controls.regDiv.setValue(this.displayStaffSearchResponse.defaultUserRegion);
    });
    this.createForm();
    this.staffSearchForm.controls.searchType.setValue('1');
    this.staffSearchForm.controls.status.setValue('Y');
    let pageUrl = this.searchService.getReturnUrl() ? this.searchService.getReturnUrl()
    : this.cookieService.get('search_return_url');
    if (pageUrl.includes('investigation-letters')) {
      this.displayCancel = true;
    }  
  }

  ngOnDestroy() {
    this.clearServerMsg();
    const helpUrl = this.environmentSettings.helpUrl;
    this.environmentSettings.helpUrl = helpUrl.replace('Search', 'Finance');
  }

  createForm() {
    this.staffSearchForm = this.formBuilder.group({
      searchType: [''],
      first: ['', Validators.maxLength(12)],
      middle: [{ value: '', disabled: false }],
      last: [{ value: '', disabled: false }],
      personId: [{ value: '', disabled: false }, ResourceSearchValidators.numericValues],
      ssn: [{ value: '', disabled: false }, DfpsCommonValidators.validateSsn],
      otherIdType: [{ value: '', disabled: false }],
      otherId: [{ value: '', disabled: false }],
      status: [{ value: '', disabled: false }],
      externalStaff: [{ value: false, disabled: false }],
      program: [{ value: '', disabled: false }],
      regDiv: [{ value: '', disabled: false }],
      unit: [{ value: '', disabled: false }, [Validators.maxLength(2), ResourceSearchValidators.alphaNumeric('Unit')]],
      county: [{ value: '', disabled: false }],
      officeCity: [{ value: '', disabled: false }, [Validators.maxLength(20), ResourceSearchValidators.alphaNumericHyphen('Office City')]],
      mailCode: [{ value: '', disabled: false }, [Validators.maxLength(4), ResourceSearchValidators.alphaNumericHyphen('Mail Code')]],
      unitSpecialization: [{ value: '', disabled: false }],
      externalStaffType: [{ value: '', disabled: false }],
      organizationName: [{ value: '', disabled: false }],
      organizationEin: [{ value: '', disabled: false }, [Validators.maxLength(9),
      ResourceSearchValidators.organizationEinPattern('Organization EIN')]],
    });
  }

  personIdSSNChanged(event) {
    if (this.staffSearchForm.controls.ssn.value.length > 0
      || this.staffSearchForm.controls.personId.value.length > 0) {
      this.disableForm();
    } else {
      this.enableForm();
    }
  }

  ssnTab() {
    if (this.staffSearchForm.controls.ssn.value.length > 0
      || this.staffSearchForm.controls.personId.value.length > 0) {
      setTimeout(() => {
        this.searchButtonEleRef.nativeElement.focus();
      }, 0);
    }
  }

  searchStaff() {
    this.clearServerMsg();
    if (this.validateFormGroup(this.staffSearchForm)) {
      this.loadDataLazy(null);
    }
  }

  initializeDataTable() {
    this.tableColumn = [
      { field: 'status', header: 'Status', isHidden: false, width: 80 },
      { field: 'name', header: 'Name', isHidden: false, width: 200 },
      { field: 'regDiv', header: 'Reg/Div', isHidden: false, width: 75 },
      { field: 'unit', header: 'Unit', isHidden: false, width: 50 },
      { field: 'eu', header: 'EU', isHidden: false, isTick: true, width: 50 },
      { field: 'office', header: 'Office', isHidden: false, width: 100 },
      { field: 'workPhone', header: 'Work Phone', isPhoneNumber: true, isHidden: false, width: 150 },
      { field: 'ext', header: 'Ext', isHidden: false, width: 75 },
      { field: 'jobClass', header: 'Job Class', isHidden: false, width: 250 },
      { field: 'supervisorName', header: 'Supervisor', isHidden: false, width: 150 },
      { field: 'personId', header: 'Person ID', isHidden: false, width: 100 },
      { field: 'mailCode', header: 'Mail Code', isHidden: false, width: 100 },
      { field: 'dob', header: 'DOB', isHidden: false, width: 150 },
      { field: 'matchName', header: 'Match Name', isHidden: false, width: 110 },
      { field: 'score', header: 'Score', isHidden: false, width: 100 }
    ];

    this.staffSearchDataTable = {
      tableColumn: this.tableColumn,
      isSingleSelect: true,
      isPaginator: true,
    };
  }

  searchButtonResultOnBlur() {
    this.searchButtonClicked = false;
  }

  loadDataLazy(event) {
    const staffSearchPagination: Pagination = {
      pageNumber: PaginationUtils.getPageNumber(event) + 1,
      pageSize: event === null ? 10 : PaginationUtils.getPageSize(event),
      sortField: PaginationUtils.getSortField(event, '', ''),
      sortDirection: PaginationUtils.getSortOrder(event),
    };
    this.loadTableData(staffSearchPagination);
  }

  loadTableData(staffSearchPagination) {
    this.searchButtonClicked = true;
    this.searchService
      .searchStaff(
        Object.assign(staffSearchPagination, this.staffSearchForm.value)
      )
      .subscribe((response: any) => {
        this.staffSearchDataTable = {
          tableBody: response.staffSearchResults ? response.staffSearchResults : [],
          tableColumn: this.tableColumn,
          isSingleSelect: true,
          totalRows: response.totalElements,
          isPaginator: true
        };
        this.showStaffSearchTable = true;
        if (this.searchButtonClicked) {
          setTimeout(() => {
            this.resultsEle.nativeElement.focus();
          }, 0);
        }
      });
  }

  disableForm() {
    Object.entries(this.staffSearchForm.controls).forEach((el) => {
      if (el[0] !== 'personId' && el[0] !== 'ssn') {
        el[1].disable();
      }
    });
  }
  enableForm() {
    Object.entries(this.staffSearchForm.controls).forEach((el) => {
      if (el[0] !== 'personId' && el[0] !== 'ssn') {
        el[1].enable();
      }
    });
  }

  getSelectedStaff(event) {
    this.selectedStaff = event;
  }

  continue() {
    if (this.selectedStaff && this.selectedStaff.name) {
      this.searchService.setSelectedStaff(this.selectedStaff);
      const url = this.searchService.getReturnUrl() ? this.searchService.getReturnUrl()
        : this.cookieService.get('search_return_url');
      if (url.includes('investigation-letters')) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      }  
      if (!this.searchService.getFormData() && this.cookieService.get('form_data')) {
        this.searchService.setFormData(JSON.parse(this.cookieService.get('form_data')));
      }
      this.searchService.setSelectedOrg(null);
      this.cookieService.delete('search_return_url');
      this.cookieService.delete('form_data');
      this.router.navigate([url]);
    } else {
      this.showErrorModal();
    }
  }

  showErrorModal() {
    const initialState = { message: 'Please select at least one row to perform this action.', title: 'Staff Search' };
    this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
  }

  clearServerMsg() {
    this.store.dispatch(ERROR_RESET(null));
    this.store.dispatch(SUCCESS_RESET(null));
  }


  clear() {
    this.cookieService.set('search_return_url', this.searchService.getReturnUrl(), 10000, '/', undefined, undefined, 'Lax');
    this.cookieService.set('form_data', JSON.stringify((this.searchService.getFormData()) as FormGroup),
      10000, '/', undefined, undefined, 'Lax');
    window.location.reload();
  }

  cancel() {
    const url = this.searchService.getReturnUrl() ? this.searchService.getReturnUrl()
      : this.cookieService.get('search_return_url');
      if (url.includes('investigation-letters')) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      } 
    this.router.navigate([url]);
    this.searchService.setReturnUrl(this.currentURL);
  }

}
