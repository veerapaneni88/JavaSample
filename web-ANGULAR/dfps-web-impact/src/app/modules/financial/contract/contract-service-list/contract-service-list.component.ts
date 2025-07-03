import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataTable, DfpsConfirmComponent, NavigationService } from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ContractServicesList } from '../model/ContractServiceList';
import { ContractService } from '../service/contract.service';

@Component({
  selector: 'contract-service-list',
  templateUrl: './contract-service-list.component.html',
})
export class ContractServiceListComponent implements OnInit {

  contractServiceListTable: DataTable;
  tableColumn: any[];
  isServiceList = false;
  selectedService: any[];
  urlPath: string;
  urlKey: string[];
  isAddDisplay = false;
  contractServiceResponse: ContractServicesList[];

  contractId = '0';
  contractNumber: any;
  period: any;
  version: any;
  contractServiceId = 1231;
  maxCSLICount = 99;

  constructor(private contractService: ContractService,
    private route: ActivatedRoute,
    private navigationService: NavigationService,
    private router: Router,
    private modalService: BsModalService) {
    this.setUserData();
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Contract Service List');
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.contractId = routeParams.get('contractId');
      this.version = routeParams.get('version');
      this.period = routeParams.get('period');
    }
    this.contractService.getContractServices(this.contractId, this.period, this.version).subscribe(data => {
      this.contractServiceResponse = data.contractServices;
      this.isAddDisplay = data.addPrivilege;
      this.contractNumber = data.scorContractNumber;
      this.serviceList(data.contractServices);
    });
  }

  setUserData() {
    // set user data for 3rd level menu rendering
    this.contractId = this.route.snapshot.paramMap.get('contractId');
    this.contractNumber = this.route.snapshot.paramMap.get('contractNumber');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idContract', this.contractId);
    this.navigationService.setUserDataValue('contractNumber', this.contractNumber);
  }

  add() {
    const currentNoOfRows = this.contractServiceResponse.length;
    if (currentNoOfRows === this.maxCSLICount) {
      this.showModal();
      return;
    }
    this.contractServiceId = 0;
    const params = this.contractId + '/' +this.contractNumber + '/period/' + this.period
      + '/version/' + this.version + '/service/' + this.contractServiceId;
    this.router.navigate(['/financial/contract/header/' + params]);
  }

  serviceList(event) {
    this.tableColumn = [
      {
        field: 'lineItem', header: 'CSLI', isLink: true, isHidden: false,
        url: '/financial/contract/header/:contractId/:contractNumber/period/:period/'
          + 'version/:version/service/:contractServiceId',
        urlParams: ['contractId', 'contractNumber', 'period', 'version', 'contractServiceId'],
      },
      { field: 'serviceCode', header: 'Service', isHidden: false },
      {
        field: 'paymentTypeCode', header: 'Payment Type', isLink: true,
        url: '/financial/contract/header/:contractId/:contractNumber/period/:period/'
          + 'version/:version/service/:contractServiceId/:paymentType',
        urlParams: ['contractId', 'contractNumber', 'period', 'version', 'contractServiceId', 'paymentType'],
        isHidden: false
      },
      { field: 'unitType', header: 'Unit Type', isHidden: false },
      { field: 'unitRate', header: 'Unit Rate', isHidden: false, isCurrency: true },
      { field: 'federalMatch', header: 'Federal Match', isHidden: false },
      { field: 'localMatch', header: 'Local Match', isHidden: false },
      { field: 'totalAmount', header: 'Total Amount', isHidden: false, isCurrency: true },
      { field: 'budgetBalanceAmount', header: 'Budget Balance', isHidden: false, isCurrency: true },
      { field: 'contractServiceId', isHidden: true, width: 1 },
      { field: 'contractId', isHidden: true, width: 1 },
      { field: 'contractNumber', isHidden: true, width: 1 },
      { field: 'version', isHidden: true, width: 1 },
      { field: 'period', isHidden: true, width: 1 },
      { field: 'paymentTypeCode', isHidden: true, width: 1 }
    ];
    this.contractServiceListTable = {
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: true
    };
    if (this.contractServiceResponse) {
      this.isServiceList = true;
      this.contractServiceListTable.tableBody = event;
    }
  }

  showModal() {
    let userResponse;
    const initialState = {
      title: 'Contract Service List',
      message: 'The CSLI cannot exceed 99.  No more rows may be added.'
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md', initialState
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
      if (result === true) {
        userResponse = true;
      }
    });
    return userResponse;
  }

}
