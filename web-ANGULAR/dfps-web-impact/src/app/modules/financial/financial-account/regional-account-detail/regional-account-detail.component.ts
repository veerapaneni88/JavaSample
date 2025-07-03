import { Component, OnInit } from '@angular/core';
import {
  DataTable,
  DirtyCheck,
  NavigationService,
  FormUtils,
  SET,
  Pagination,
  PaginationUtils
} from 'dfps-web-lib';
import { LazyLoadEvent } from 'primeng/api';
import { ActivatedRoute } from '@angular/router';
import { FinancialAccountService } from '../service/financial-account.service';
import { RegionalAccountResponse } from '../model/RegionalAccountDetail';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';


@Component({
  selector: 'regional-account-detail',
  templateUrl: './regional-account-detail.component.html',
})
export class RegionalAccountDetailComponent implements OnInit {
  accountDetailsDataTable: DataTable;
  regionalAccountResponse: RegionalAccountResponse;
  accountNumber: number;
  accountStatus: string;
  tableBody: any[];
  tableColumn: any = [];
  urlKey: any[];
  urlPath: string;
  dataObj: any;
  regionalAccountInfo: any;
  regionalAccountDetailsForm: any;
  readonly DEFAULT_PAGE_SIZE = 50;
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private navigationService: NavigationService,
    private regionalAccountService: FinancialAccountService) {
    this.accountNumber = +this.route.snapshot.paramMap.get('accountNumber');
    this.accountStatus = this.route.snapshot.paramMap.get('status');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('accountStatus', this.accountStatus);
    this.navigationService.setUserDataValue('accountNumber', this.accountNumber.toString());
    this.navigationService.setUserDataValue('idFinancialAccount', '0');
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Regional Account Detail');
    this.createForm();
    this.loadDataLazy(null);
    this.initializeDataTable();
  }

  createForm() {
    this.regionalAccountDetailsForm = this.fb.group({
      phoneNumber: [''],
      ext: [''],
      address1: [''],
      address2: [''],
      city: [''],
      state: [''],
      zip: [''],
      county: [''],
      zipExt: ['']
    });
  }

  setFormValues() {
    this.regionalAccountDetailsForm.setValue({
      phoneNumber: FormUtils.formatPhoneNumber(this.regionalAccountInfo.phoneNumber),
      ext: this.regionalAccountInfo.ext,
      address1: this.regionalAccountInfo.address1,
      address2: this.regionalAccountInfo.address2,
      city: this.regionalAccountInfo.city,
      state: this.regionalAccountInfo.state,
      zip: this.regionalAccountInfo.zip,
      county: this.regionalAccountInfo.county,
      zipExt: this.regionalAccountInfo.zipExt
    })
  }

  log(e: boolean) {
    const btn = document.getElementsByClassName('btn btn-link');
    if (e) {
        btn[0].setAttribute('aria-expanded', 'true');
    } else {
        btn[0].setAttribute('aria-expanded', 'false');
    }
}


  regionalAccountDetails(financialAccountListPagination: any) {
    if (this.accountStatus === 'Active') {
      this.accountStatus = 'A';
    } else if (this.accountStatus === 'Closed') {
      this.accountStatus = 'C';
    }
    this.dataObj = {
      regionalAccountId: this.accountNumber,
      accountStatus: this.accountStatus
    }
    const obj = { ...this.dataObj, ...financialAccountListPagination }
    this.regionalAccountService.getRegionalAccountDetails(obj).subscribe(res => {
      if (res) {
        this.regionalAccountResponse = res;
        this.regionalAccountInfo = res.regionalAccountDetails;
        this.setFormValues();
        this.regionalAccountDetailsForm.disable();
        this.displayDataTable();
      }
    });
  }

  loadDataLazy(event: LazyLoadEvent) {
    const financialAccountListPagination: Pagination = {
      pageNumber: PaginationUtils.getPageNumber(event),
      pageSize: event === null ? this.DEFAULT_PAGE_SIZE : PaginationUtils.getPageSize(event),
      sortField: PaginationUtils.getSortField(event, this.regionalAccountResponse ?
        this.regionalAccountResponse.sortColumns : '', 'person.fullName'),
      sortDirection: PaginationUtils.getSortOrder(event)
    };
    this.regionalAccountDetails(financialAccountListPagination);
  }

  initializeDataTable() {
    this.tableColumn = [
      {
        field: 'personId', header: 'Person ID', isHidden: false, isLink: true,
        url: '/financial/financial-account/header/:id', urlParams: ['id'], width: 105
      },
      { field: 'personName', header: 'Name', isHidden: false, sortable: true, width: 150 },
      { field: 'personCounty', header: 'County', isHidden: false, sortable: true, width: 100 },
      { field: 'region', header: 'Reg', isHidden: false, width: 70 },
      { field: 'type', header: 'Type', isHidden: false, width: 70 },
      { field: 'status', header: 'Status', isHidden: false, width: 75 },
      {
        field: 'accountNumber', header: 'Account Number', isHidden: false, width: 150, isLink: true,
        url: '/financial/financial-account/register/:id', urlParams: ['id'],
      },
      { field: 'balance', header: 'Balance', isHidden: false, isCurrency: true, width: 100 },
      { field: 'balanceDate', header: 'As Of', isHidden: false, width: 100 },
      { field: 'reconBalance', header: 'Recon Bal', isCurrency: true, isHidden: false, width: 100 },
    ];
    this.accountDetailsDataTable = {
      tableColumn: this.tableColumn,
      isPaginator: true
    };
  }

  displayDataTable() {
    this.accountDetailsDataTable = {
      tableBody: this.regionalAccountResponse.financialAccountList,
      tableColumn: this.tableColumn,
      urlKey: this.urlKey,
      urlPath: this.urlPath,
      isPaginator: true,
      totalRows: this.regionalAccountResponse.totalElements
    };
  }

}
