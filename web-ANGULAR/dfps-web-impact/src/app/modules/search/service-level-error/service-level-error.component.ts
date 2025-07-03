import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { DataTable, DfpsCommonValidators, DfpsConfirmComponent, DfpsFormValidationDirective,
  DirtyCheck, DropDown, NavigationService, Pagination, PaginationUtils } from 'dfps-web-lib';
import {
  DisplayServiceLevelError, ServiceLevelError, ServiceLevelErrorRequest,
  ServiceLevelErrorResponse, ServiceLevelErrorSearchRes } from '../model/ServiceLevelError';
import { ServiceLevelErrorService } from '../service/service-level-error.service';
import {ServiceLevelErrorsValidators} from './service-level-error.validator'
import { HelpService } from 'app/common/impact-help.service';
import { County } from '../model/HomeSearch';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { LazyLoadEvent } from 'primeng/api';


@Component({
  selector: 'service-level-error',
  templateUrl: './service-level-error.component.html'
})
export class ServiceLevelErrorComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  selectedForDeletion: any[] = [];
  modalSubscription: Subscription;
  DEFAULT_PAGE_SIZE = this.getPageSize();
  currentPageNumber = 0;
  serviceLevelErrorForm: FormGroup;
  displaySearchResults = false;
  serviceLevelErrorResponse: ServiceLevelErrorResponse;
  displayServiceLevelError: DisplayServiceLevelError;
  serviceLevelErrorRequest: ServiceLevelErrorRequest;
  dataTable: DataTable;
  counties: DropDown[] = [];
  tableBody: any[];
  tableColumn: any[];
  serviceLevelErrors:any;
  hideMarkForDeletion = false;
  sortOrder: number;
  sortField: string;
  totalElements: any;

  constructor(
    private serviceLevelErrorService: ServiceLevelErrorService,
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
    this.navigationService.setTitle('Service Level Error Search');
    this.helpService.loadHelp('Search');
    localStorage.removeItem('SvcLvlErr');
    this.createForm();
    this.intializeScreen();
  }

  createForm() {
    this.serviceLevelErrorForm = this.formBuilder.group(
      {
        markedForDeletion: [''],
        personId: ['', [Validators.maxLength(16)]],
        errorText: ['', [Validators.maxLength(50)]],
        fromDate: [''],
        toDate: [''],
        tprErrors: [''],
        fpsErrors: [''],
        region: ['']
      },
      {
        validators:
          [ServiceLevelErrorsValidators.serviceLevelValidation]
      }
    );
  }

  intializeScreen() {
    this.serviceLevelErrorService.displayServiceLevelError().subscribe((response) => {
      this.displayServiceLevelError = response;
      this.serviceLevelErrorForm.controls.markedForDeletion.setValue(null);
    });
    this.serviceLevelErrorRequest = this.serviceLevelErrorForm.value;
  }

  loadCounties(event) {
    const selectedRegion = event.target.value.split(': ')[1];
    this.regionCounty(selectedRegion);
  }

  regionCounty(selectedRegion: string) {
    this.serviceLevelErrorForm.controls.county.setValue('');
    if (selectedRegion === '98') {
      this.counties = this.displayServiceLevelError.regionCounty;
    } else if (selectedRegion === '99') {
      this.counties = [];
    } else {
      this.counties = this.displayServiceLevelError.regionCounty.filter(
        (regionCounty: County) => regionCounty.regionCode === selectedRegion
      );
    }
    this.counties.push({ code: '', decode: '' });
  }

  save() {
    if (this.selectedForDeletion?.length > 0) {
      this.serviceLevelErrorService.markForDeletion(this.selectedForDeletion.map(obj => obj.id)).subscribe(response => {
        this.search();
      });
    } else {
      const initialState = {
        message: 'Mark for Deletion: Please check at least one error record to proceed.',
        title: 'Service Level Error Search',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }

  search() {
    localStorage.removeItem('SvcLvlErr');
    this.displaySearchResults = false;
    this.currentPageNumber = 0;
    if (this.validateFormGroup(this.serviceLevelErrorForm)) {
      this.serviceLevelErrorRequest = this.serviceLevelErrorForm.value;
      this.serviceLevelErrorRequest.region = this.serviceLevelErrorForm.controls.region.value;
      this.serviceLevelErrorRequest.tprErrors = this.serviceLevelErrorForm.controls.tprErrors.value ? 'Y' : null;
      this.serviceLevelErrorRequest.fpsErrors = this.serviceLevelErrorForm.controls.fpsErrors.value ? 'Y' : null;
      this.markForDeletionChange();
      this.hideMarkForDeletion = this.serviceLevelErrorRequest.markedForDeletion === 'Y';
      this.loadDataLazy(null);
    }
  }

  loadDataLazy(event: LazyLoadEvent) {
    const serviceLevelPagination: Pagination = {
      pageNumber: PaginationUtils.getPageNumber(event),
      pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
      sortField: PaginationUtils.getSortField(event, this.serviceLevelErrorResponse ?
        this.serviceLevelErrorResponse.sortColumns : '', 'lastName'),
      sortDirection: PaginationUtils.getSortOrder(event)
    };

    const serviceLevelCopy: any = JSON.parse(localStorage.getItem('SvcLvlErr'));

    if (serviceLevelCopy) {
      if (event) {
        serviceLevelCopy.pageNumber = PaginationUtils.getPageNumber(event);
        serviceLevelCopy.pageSize = (serviceLevelCopy.pageSize === null)
          ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event);
        serviceLevelCopy.sortField = PaginationUtils.getSortField(event, this.serviceLevelErrorResponse ?
          this.serviceLevelErrorResponse.sortColumns : '', 'lastName');
        serviceLevelCopy.sortDirection = PaginationUtils.getSortOrder(event);
        this.searchServiceLevel(Object.assign(serviceLevelPagination, serviceLevelCopy));
      } else {
        this.DEFAULT_PAGE_SIZE = this.getPageSize();
        this.currentPageNumber = serviceLevelCopy.pageNumber * this.getPageSize();
        this.searchServiceLevel(serviceLevelCopy);
      }
    } else {
      this.searchServiceLevel(Object.assign(serviceLevelPagination, this.serviceLevelErrorRequest));
    }
  }

  getPageSize(): number {
    const pageSizeData = localStorage.getItem('SvcLvlErr_PageSize');
    return pageSizeData ? Number(pageSizeData) : 10 as number;
  }

  searchServiceLevel(serviceLevelPagination: any) {
    this.serviceLevelErrorService.search(serviceLevelPagination).subscribe(response => {
      this.serviceLevelErrors = response.serviceLevelErrors;
      this.totalElements = response.totalElements;
      this.displaySearchResults = true;
      this.serviceLevelErrorResponse = response;
      this.initializeDataTable();
    });

    localStorage.removeItem('SvcLvlErr');
    localStorage.setItem('SvcLvlErr', JSON.stringify(serviceLevelPagination));
    localStorage.setItem('SvcLvlErr_PageSize', serviceLevelPagination.pageSize);
  }

  initializeDataTable() {
      this.tableColumn = [
        // { field: 'id', header: 'Id', width: 60 },
        { field: 'region', header: 'Region', width: 60 },
        { field: 'lastName', header: 'Person Name', sortable: true, width: 150 },
        { field: 'personId', header: 'Person ID', width: 100 },
        { field: 'alocCode', header: 'ASL', sortable: true, width: 100 },
        { field: 'alocStartDate', header: 'Start Date', width: 100 },
        { field: 'alocEndDate', header: 'End Date', width: 100 },
        { field: 'alocProcessedDate', header: 'Proc Date', sortable: true, width: 150 },
        { field: 'revTypeCode', header: 'Review Type', width: 100 },
        { field: 'errorText', header: 'Error', width: 200 }
      ];
      
      this.dataTable = {
        tableBody: this.serviceLevelErrors,
        tableColumn: this.tableColumn,
        isPaginator: true,
        isMultiSelect: this.serviceLevelErrorRequest?.markedForDeletion === 'N',
        totalRows: this.totalElements,
        displaySelectAll:true
      }
    }

  markForDeletionChange() {
    this.serviceLevelErrorRequest.markedForDeletion = this.serviceLevelErrorForm.get('markedForDeletion').value ? 'Y' : 'N';
  }

  selectedRowEvent(event: any) {

    const seldRows = this.dataTable.selectedRows;
    if(seldRows) {
      if(seldRows.length == 0) {
        //select All unchecked event
        this.dataTable.tableBody.forEach( tbItem => {
          let ind = 0;
          this.selectedForDeletion.forEach(sfdItem => {
            if(sfdItem.id === tbItem.id) {
              this.selectedForDeletion.splice(ind,1);
            }
            ind++;
          });
        });
      } else {
        //checked/unchecked an item
        this.dataTable.tableBody.forEach( tbItem => {
          const seldItem = seldRows.find(sItem => sItem.id === tbItem.id);
          if (seldItem) {
            const sfdItem1 = this.selectedForDeletion.find(sfdItem => sfdItem.id === seldItem.id);
            if(!sfdItem1) {
              //checked a new item
              this.selectedForDeletion.push(seldItem);
            }
          } else {
            let ind = 0;
            this.selectedForDeletion.forEach(sfdItem => {
              if(sfdItem.id === tbItem.id) {
              //unchecked an item
              this.selectedForDeletion.splice(ind,1);
              }
              ind++;
            });
          }

        });
      }
    } else {
      //page change event, build the list of checked items for that page
      const selectedRows = [];
      this.dataTable.tableBody.forEach( tbItem => {
        const sfdItem1 = this.selectedForDeletion.find(sfdItem => sfdItem.id === tbItem.id);
        if(sfdItem1) {
          selectedRows.push(sfdItem1);
        }
      });
      this.dataTable.selectedRows = selectedRows;
    }

  }

}
