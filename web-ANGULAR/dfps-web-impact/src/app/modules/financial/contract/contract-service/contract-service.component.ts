import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DataTable, DfpsFormValidationDirective, DirtyCheck, FormUtils, NavigationService, SET, DfpsCommonValidators } from 'dfps-web-lib';
import { ContractSvc, CountyCodes, DisplayContractServicesResponse } from '../model/ContractService';
import { ContractService } from '../service/contract.service';
import { ContractServiceValidators } from './contract-service.validator';

@Component({
  selector: 'contract-service',
  templateUrl: './contract-service.component.html'
})
export class ContractServiceComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  periodNumber = 0;
  contractId = 0;
  contractNumber = 0;
  versionNumber = 0;
  csliNumber = 1;

  contractServiceId: number;
  contractServiceTable: DataTable;
  tableColumn: any[];
  disabledInfoSelect = false;
  isSaveInfo = false;

  isCheckboxSelect = false;
  isNewMode = false;
  selectedCounties: any[];
  modifiedCounties: any[];
  mySubscription: any;

  contractServiceResponse: DisplayContractServicesResponse;
  contractServiceRes: ContractSvc;
  contractServiceForm: FormGroup;
  infoDivDisplay = false;
  informationalMsgs: string[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private contractService: ContractService,
    private router: Router,
    private navigationService: NavigationService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    const contractId = this.route.snapshot.paramMap.get('contractId');
    const contractNumber = this.route.snapshot.paramMap.get('contractNumber');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idContract', contractId);
    this.navigationService.setUserDataValue('contractNumber', contractNumber);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Contract Service Detail');
    this.createForm();
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.contractId = Number(routeParams.get('contractId'));
      this.contractServiceId = Number(routeParams.get('contractServiceId'));
      this.versionNumber = Number(routeParams.get('version'));
      this.periodNumber = Number(routeParams.get('period'));
      this.getContractServiceDetail();
    }
  }

  ngOnDestroy() {
    if (this.mySubscription) {
      this.mySubscription.unsubscribe();
    }
  }

  getContractServiceDetail() {
    this.contractService.getContractServicesDetail(
      this.contractId, this.periodNumber, this.versionNumber, this.contractServiceId).subscribe(contractServiceResponse => {
        this.contractServiceResponse = contractServiceResponse;
        this.infoDivDisplay = this.contractServiceResponse.isErrorServiceCode;
        if (this.infoDivDisplay === true) {
          this.informationalMsgs
            .push('One or more Service Codes could not be decoded.Contact Help Desk.');
          if (this.contractServiceResponse.pageMode === 'NEW') {
            this.contractServiceResponse.services = [];
          }
        }
        this.determinePageMode();
        if (contractServiceResponse.contractService) {
          this.csliNumber = contractServiceResponse.contractService.lineItem;
          this.contractNumber = contractServiceResponse.contractService.contractNumber;
          this.loadContract(contractServiceResponse.contractService);
          if (contractServiceResponse.pageMode !== 'NEW') {
            this.serviceList(contractServiceResponse.contractService.countyCodes);
          }
        }
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      });
  }

  createForm() {
    this.contractServiceForm = this.formBuilder.group({
      service: ['', [Validators.required]],
      paymentType: ['', [Validators.required]],
      unitType: ['', [Validators.required]],
      unitRate: ['0.00', [DfpsCommonValidators.validateCurrency(7), ContractServiceValidators.validatePositiveNumbers(7)]],
      federalMatch: ['', [Validators.maxLength(3)]],
      localMatch: ['', [Validators.maxLength(3)]],
      lastUpdatedDate: [''],
      budgetLimit: ['']
    }, {
      Validators: 
      [ContractServiceValidators.compareUnitRatefields]
    });
  }

  serviceList(data) {
    this.disabledInfoSelect = false;
    if (data) {
      this.disabledInfoSelect = true;
      this.tableColumn = [
        { field: 'code', header: 'Code', isHidden: false },
        { field: 'decode', header: 'County', isHidden: false }
      ];
      this.contractServiceTable = {
        tableColumn: this.tableColumn,
        isPaginator: true,
        isMultiSelect: true,
        displaySelectAll: this.contractServiceResponse.pageMode !== 'VIEW',
        selectedRows: [],
      };

      this.contractServiceTable.tableBody = data;
      this.contractServiceTable.selectedRows = data.filter(value => value.checked);
    }
  }

  loadContract(contractServiceRes: ContractSvc) {
    if (contractServiceRes) {
      this.selectedCounties = contractServiceRes.countyCodes.filter(code => code.checked);
      this.contractServiceForm.setValue({
        service: contractServiceRes.serviceCode,
        paymentType: contractServiceRes.paymentType,
        unitType: contractServiceRes.unitType,
        unitRate: Number(contractServiceRes.unitRate).toFixed(2) || 0.00,
        federalMatch: contractServiceRes.federalMatch,
        localMatch: contractServiceRes.localMatch,
        lastUpdatedDate: contractServiceRes.lastUpdatedDate,
        budgetLimit: contractServiceRes.budgetLimit
      });
      this.contractServiceRes = contractServiceRes;
      if (contractServiceRes.versionLocked) {
        FormUtils.disableFormControlStatus(this.contractServiceForm, ['unitRate', 'federalMatch', 'localMatch']);
        this.isCheckboxSelect = true;
        this.isSaveInfo = false;
      }
      if (this.contractServiceRes.paymentType === 'CRM') {
        FormUtils.disableFormControlStatus(this.contractServiceForm, ['unitRate']);
      }
    }
  }

  determinePageMode() {
    const pageMode = this.contractServiceResponse.pageMode;
    if (pageMode === 'VIEW') {
      this.contractServiceForm.disable();
    } else if (pageMode === 'EDIT') {
      FormUtils.disableFormControlStatus(this.contractServiceForm,
        [
          'serviceCode',
          'paymentType',
          'unitType'
        ]);
      this.isSaveInfo = true;
    } else if (pageMode === 'NEW') {
      this.isSaveInfo = true;
    }
  }

  saveContractServiceDetails() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    if (this.validateFormGroup(this.contractServiceForm)) {
      const updatedContractService = {
        ...this.contractServiceRes,
        serviceCode: this.contractServiceForm.getRawValue().service,
        paymentType: this.contractServiceForm.getRawValue().paymentType,
        unitType: this.contractServiceForm.getRawValue().unitType,
        unitRate: this.contractServiceForm.getRawValue().unitRate,
        federalMatch: this.contractServiceForm.getRawValue().federalMatch,
        localMatch: this.contractServiceForm.getRawValue().localMatch,
        lastUpdatedDate: this.contractServiceForm.getRawValue().lastUpdatedDate,
        isNewRow: this.contractServiceId === 0 ? 'Y' : 'N',
        contractNumber: this.contractNumber,
        period: this.periodNumber,
        version: this.versionNumber,
        contractServiceId: this.contractServiceId,
        contractId: this.contractId
      };
      if (this.getModifiedCountyCodes()) {
        updatedContractService.countyCodes = null;
        updatedContractService.countyCodes = this.getModifiedCountyCodes();
      }
      this.contractService.saveContractServiceDetails(
        this.contractId, this.periodNumber, this.versionNumber, updatedContractService)
        .subscribe(res => {
          if (res) {
            this.router.navigateByUrl('/financial/contract/header/', { skipLocationChange: true }).then(() => {
              this.router.navigate(['/financial/contract/header/' + this.contractId
                + '/' + this.contractNumber
                + '/period/' + this.periodNumber
                + '/version/' + this.versionNumber
                + '/service']);
            });
          }
        });
    }
  }

  onServiceCodeChange(event) {
    const serviceCode = event.target.value.split(': ');
    this.modifiedCounties = null;
    if (serviceCode[1]) {
      this.contractService.getContractServicesCountyCode(
        this.contractId, this.periodNumber, this.versionNumber, this.contractServiceId, serviceCode[1])
        .subscribe(res => {
          this.serviceList(res);
        });
    } else {
      this.disabledInfoSelect = false;
    }
  }

  onPaymentTypeChange(event) {
    if (event.target.value.indexOf('CRM') !== -1) {
      FormUtils.disableFormControlStatus(this.contractServiceForm, ['unitRate']);
      FormUtils.enableFormControlStatus(this.contractServiceForm, ['unitType']);
    } else if (event.target.value.indexOf('VUR') !== -1) {
      this.contractServiceForm.controls.unitType.setValue('ONE');
      FormUtils.disableFormControlStatus(this.contractServiceForm, ['unitType']);
      if (!this.contractServiceRes.versionLocked) {
        FormUtils.enableFormControlStatus(this.contractServiceForm, ['unitRate']);
      }
    } else if (!this.contractServiceRes.versionLocked) {
      FormUtils.enableFormControlStatus(this.contractServiceForm, ['unitRate', 'unitType']);
    } else {
      FormUtils.enableFormControlStatus(this.contractServiceForm, ['unitType']);
    }
  }

  getModifiedCountyCodes(): CountyCodes[] {
    if (this.modifiedCounties) {
      this.modifiedCounties.forEach(data => {
        data.checked = true;
      });
    }
    return this.modifiedCounties ? this.modifiedCounties : null;
  }

  isCountyChecked(countyCode: CountyCodes): boolean {
    const value = this.modifiedCounties.find(modifiedCounty =>
      modifiedCounty.code === countyCode.code
    );
    return (value !== undefined);
  }
}
