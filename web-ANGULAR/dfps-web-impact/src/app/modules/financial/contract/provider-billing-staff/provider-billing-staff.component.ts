import { Component, ElementRef, Inject, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
  DataTable,
  DfpsConfirmComponent,
  DirtyCheck,
  ENVIRONMENT_SETTINGS,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ContractService } from '../service/contract.service';

@Component({
  selector: 'provider-billing-staff',
  templateUrl: './provider-billing-staff.component.html',
  styleUrls: ['./provider-billing-staff.component.css']
})
export class ProviderBillingStaffComponent implements OnInit {
  tableColumn: any = [];
  pBillingDataTable: DataTable;
  addEmployeeDataTable: DataTable;
  authprovidersList;
  isEmployeeSelected = false;
  selectEmployee: any;
  isProviderSelected: boolean;
  selectProvider: any;
  actionButtons: boolean;
  hideEmployeeTable: boolean;
  tableData = [];
  enableEmployeeInfo = true;
  disableEmployeeInfo: boolean;
  contractId: any;
  pBillingTableData: boolean;
  isEmployeeDeleted = false;
  @ViewChild('employeeIdSpan') employeeIdSpanEl: ElementRef;
  helpFileName: string;

  constructor(private contractService: ContractService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: BsModalService,
    private searchService: SearchService,
    private navigationService: NavigationService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    this.setUserData();
  }

  setUserData() {
    // set user data for 3rd level menu rendering
    const params = this.route.snapshot.paramMap.get('contractId');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idContract', params);
  }

  ngOnInit() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.navigationService.setTitle('Provider Billing Staff');
    const helpUrl = this.environmentSettings.helpUrl;
    this.helpFileName = helpUrl.replace('Page_Descriptions/Finance/', 'Impact_Phase_2_Help.htm');
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.contractId = routeParams.get('contractId');
    }
    this.getProviderBillingInfo(this.contractId);
    this.populatePullBackData();
  }

  getProviderBillingInfo(contractId) {
    this.contractService.getProvidersInfo(contractId).subscribe(res => {
      this.authprovidersList = res.authproviders;
      this.providerBillingTable(this.authprovidersList);
      this.determinePageMode(res);
    });
  }

  showButtons() {
    if (this.authprovidersList.length === 0) {
      this.pBillingTableData = true;
    } else {
      this.pBillingTableData = false;
    }
  }

  providerBillingTable(providersList: any) {
    this.showButtons();
    if (providersList) {
      this.tableColumn = [
        { field: 'fullName', header: 'Employee Name', isHidden: false },
      ];
      this.pBillingDataTable = {
        tableColumn: this.tableColumn,
        isSingleSelect: true,
        isPaginator: false,
      };
      if (providersList) {
        this.pBillingDataTable.tableBody = providersList;
      }
    }
  }

  listOfEMployeesTable(selectedStaffData: any) {
    if (selectedStaffData) {
      this.tableData.push(selectedStaffData);
      this.tableColumn = [
        { field: 'name', header: 'Employee Name', isHidden: false },
      ];
      this.addEmployeeDataTable = {
        tableColumn: this.tableColumn,
        isSingleSelect: true,
        isPaginator: false,
      };
      if (this.tableData) {
        this.addEmployeeDataTable.tableBody = this.tableData;
        this.hideEmployeeTable = true;
        this.enableEmployeeInfo = true;
      }
    }
  }

  getEmployeeInfo(event) {
    if (event) {
      this.isEmployeeSelected = true;
      this.selectEmployee = event;
    }
  }

  deleteEmployee() {
    if (this.isEmployeeSelected) {
      const initialState = {
        title: 'Provider Billing Staff',
        message: 'Are you sure you want to delete the information?',
        showCancel: true
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
        if (result === true) {
          this.contractService.deleteProviderInfo(this.selectEmployee.contractId, this.selectEmployee.personId).subscribe(res => {
            this.getProviderBillingInfo(this.selectEmployee.contractId);
            this.isEmployeeSelected = false;
            this.isEmployeeDeleted = true;
            setTimeout(() => this.employeeIdSpanEl.nativeElement.focus(), 500);
          });
        }

        document.getElementById('deleteEmployee').focus();

      });
    } else {
      const initialState = {
        title: 'Provider Billing Staff',
        message: 'Please select at least one row to perform this action',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { document.getElementById('deleteEmployee').focus(); });

    }

  }

  addEmployee() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    let returnUrl;
    if (this.contractId) {
      returnUrl = 'financial/contract/authprovider/' + this.contractId;
    }
    this.searchService.setReturnUrl(returnUrl);
    this.router.navigate(['financial/contract/authprovider/' + this.contractId + '/staffsearch']);
  }

  populatePullBackData() {
    if (this.searchService.getSelectedStaff()) {
      this.listOfEMployeesTable(this.searchService.getSelectedStaff());
      this.disableEmployeeInfo = true;
      this.searchService.setSelectedStaff(null);
      this.isEmployeeDeleted = false;
    }
  }

  getProviderInfo(event) {
    if (event) {
      this.isProviderSelected = true;
      this.selectProvider = event;
    }
  }

  setSelectedProviderInfo() {
    if (this.tableData) {
      return this.tableData;
    }
  }

  saveEmployeeDetails() {
    if (this.setSelectedProviderInfo()) {
      const saveProviderInfo = {
        personId: this.setSelectedProviderInfo()[0].personId
      };
      this.contractService.saveProviderInfo(this.contractId, saveProviderInfo).subscribe(res => {
        if (res) {
          this.addEmployeeDataTable.tableBody = [];
          this.hideEmployeeTable = false;
          this.getProviderBillingInfo(this.contractId);
          this.disableEmployeeInfo = false;
          this.isEmployeeDeleted = false;
        }
      });
    }
  }

  delete() {
    this.searchService.setSelectedStaff(null)
    this.addEmployeeDataTable.tableBody = [];
    this.hideEmployeeTable = false;
    this.disableEmployeeInfo = false;
    this.isEmployeeDeleted = true;
    setTimeout(() => this.employeeIdSpanEl.nativeElement.focus(), 500);
    document.getElementById('addEmpDiv').focus();
  }

  determinePageMode(res) {
    if (res.pageMode === 'EDIT') {
      this.actionButtons = true;
    } else if (res.pageMode === 'VIEW') {
      this.actionButtons = false;
      this.disableEmployeeInfo = true;
    }
  }

}
