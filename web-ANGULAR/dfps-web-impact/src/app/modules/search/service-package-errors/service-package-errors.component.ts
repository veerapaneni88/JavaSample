import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { DataTable, DfpsCommonValidators, DfpsConfirmComponent, DfpsFormValidationDirective,
  DirtyCheck, DropDown, NavigationService, Pagination, PaginationUtils } from 'dfps-web-lib';
import {
  DisplayServicePackageError, ServicePackageError, ServicePackageErrorRequest,
  ServicePackageErrorResponse, ServicePackageErrorSearchRes
} from '../model/ServicePackageError';
import { ServicePackageErrorService } from '../service/service-package-error.service';
import { ServicePackageErrorsValidators } from './service-package-errors.validator';
import { HelpService } from 'app/common/impact-help.service';
import { County } from '../model/HomeSearch';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { LazyLoadEvent } from 'primeng/api';


@Component({
  selector: 'service-package-errors',
  templateUrl: './service-package-errors.component.html'
})
export class ServicePackageErrorsComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  selectedForDeletion: any[] = [];
  modalSubscription: Subscription;
  DEFAULT_PAGE_SIZE = this.getPageSize();
  currentPageNumber = 0;
  servicePackageErrorForm: FormGroup;
  displaySearchResults = false;
  servicePackageErrorResponse: ServicePackageErrorResponse;
  displayServicePackageError: DisplayServicePackageError;
  servicePackageErrorRequest: ServicePackageErrorRequest;
  dataTable: DataTable;
  preSelectedValues = null;
  counties: DropDown[] = [];
  tableBody: any[];
  tableColumn: any[];
  servicePackageErrors:any;
  hideMarkForDeletion = false;
  urlKey: any[];
  urlPath: string;
  sortOrder: number;
  sortField: string;
  totalElements: any;

  constructor(
    private servicePackageErrorService: ServicePackageErrorService,
    private navigationService: NavigationService,
    private router: Router,
    private helpService: HelpService,
    private modalService: BsModalService,
    private formBuilder: FormBuilder,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Service Package Error Search');
    this.helpService.loadHelp('Search');
    this.createForm();
    this.intializeScreen();
    localStorage.removeItem('ServicePackage');
  }

  createForm() {
    this.servicePackageErrorForm = this.formBuilder.group(
      {
        markedForDeletion: [''],
        personId: ['', [Validators.maxLength(16), DfpsCommonValidators.validateId]],
        errorText: ['', [Validators.maxLength(30)]],
        fromDate: [''],
        toDate: [''],
        region: [''],
        county: ['']
      },
      {
        validators:
          [ServicePackageErrorsValidators.servicePackageValidation]
      }
    );
  }

  intializeScreen() {
    this.servicePackageErrorService.displayServicePackageError().subscribe((response) => {
      this.displayServicePackageError = response;
    });
  }

  loadCounties(event) {
    const selectedRegion = event.target.value.split(': ')[1];
    this.regionCounty(selectedRegion);
  }

  regionCounty(selectedRegion: string) {
    this.servicePackageErrorForm.controls.county.setValue('');
    if (selectedRegion === '98') {
      this.counties = this.displayServicePackageError.regionCounty;
    } else if (selectedRegion === '99') {
      this.counties = [];
    } else {
      this.counties = this.displayServicePackageError.regionCounty.filter(
        (regionCounty: County) => regionCounty.regionCode === selectedRegion
      );
    }
    this.counties.push({ code: '', decode: '' });
  }

  save() {
    if (this.selectedForDeletion?.length > 0) {
      this.servicePackageErrorService.markForDeletion(this.selectedForDeletion.map(obj => obj.id)).subscribe(response => {
        this.markForDeletionChange();
        this.search();
      });
    } else {
      const initialState = {
        message: 'Please select at least one row to perform this action.',
        title: 'Service Package Error Search',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }

  search() {
    localStorage.removeItem('ServicePackage');
    this.displaySearchResults = false;
    this.currentPageNumber = 0;
    if (this.validateFormGroup(this.servicePackageErrorForm)) {
      this.servicePackageErrorRequest = this.servicePackageErrorForm.value;
      this.markForDeletionChange();
      this.hideMarkForDeletion = this.servicePackageErrorRequest.markedForDeletion === 'Y';
      this.loadDataLazy(null);
    }
  }

  loadDataLazy(event: LazyLoadEvent) {
    const servicePackagePagination: Pagination = {
      pageNumber: PaginationUtils.getPageNumber(event),
      pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
      sortField: '',
      sortDirection: PaginationUtils.getSortOrder(event)
    };

    const servicePackageCopy: any = JSON.parse(localStorage.getItem('ServicePackage'));

    if (servicePackageCopy) {
      if (event) {
        servicePackageCopy.pageNumber = PaginationUtils.getPageNumber(event);
        servicePackageCopy.pageSize = (servicePackageCopy.pageSize === null)
          ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event);
        servicePackageCopy.sortField = PaginationUtils.getSortField(event, this.servicePackageErrorResponse ?
          this.servicePackageErrorResponse.sortColumns : '', 'lastName');
        servicePackageCopy.sortDirection = PaginationUtils.getSortOrder(event);
        this.searchServicePackage(Object.assign(servicePackagePagination, servicePackageCopy));
      } else {
        this.DEFAULT_PAGE_SIZE = this.getPageSize();
        this.currentPageNumber = servicePackageCopy.pageNumber * this.getPageSize();
        this.searchServicePackage(servicePackageCopy);
      }
    } else {
      this.searchServicePackage(Object.assign(servicePackagePagination, this.servicePackageErrorRequest));
    }
  }

  searchServicePackage(servicePackagePagination: any) {
    this.servicePackageErrorService.search(servicePackagePagination).subscribe(response => {
      this.servicePackageErrors = response.servicePackageErrors;
      this.totalElements = response.totalElements;
      this.displaySearchResults = true;
      this.servicePackageErrorResponse = response;
      this.initializeDataTable();
    }
    );

    localStorage.removeItem('ServicePackage');
    localStorage.setItem('ServicePackage', JSON.stringify(servicePackagePagination));
    localStorage.setItem('ServicePackage_PageSize', servicePackagePagination.pageSize);
  }

  getPageSize(): number {
    const pageSizeData = localStorage.getItem('ServicePackage_PageSize');
    return pageSizeData ? Number(pageSizeData) : 10 as number;
  }

  markForDeletion(event: any) {
    if(event.id) {
       this.selectedForDeletion.push(event.id);
    }
  }

  initializeDataTable() {
    this.tableColumn = [
      { field: 'region', header: 'Reg', width: 80 },
      { field: 'county', header: 'County', width: 100 },
      { field: 'lastName', header: 'Person Name', sortable: true, width: 120 },
      { field: 'personId', header: 'Person ID', width: 100 },
      { field: 'groAssessmentCompletedDate', header: 'TPR Review Date', width: 100 },
      { field: 'groRecommended', header: 'TPR Decision', width: 100 },
      { field: 'errorText', header: 'Error', width: 200 },
    ];

    this.dataTable = {
      tableBody: this.servicePackageErrors,
      tableColumn: this.tableColumn,
      isPaginator: true,
      isMultiSelect: this.servicePackageErrorRequest?.markedForDeletion === 'N',
      totalRows: this.totalElements,
      displaySelectAll:true
    }
  }

  markForDeletionChange() {
    this.servicePackageErrorRequest.markedForDeletion = this.servicePackageErrorForm.get('markedForDeletion').value ? 'Y' : 'N';
  }

}
