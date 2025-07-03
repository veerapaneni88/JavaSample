import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DfpsCommonValidators, DfpsFormValidationDirective, DirtyCheck, FormUtils, NavigationService, SET } from 'dfps-web-lib';
import { CostReimbursementDetails } from '../model/CostReimbursement';
import { ContractService } from '../service/contract.service';
import { UnitRateDetailValidators } from './unit-rate-detail.validator';
@Component({
  selector: 'unit-rate-detail',
  templateUrl: './unit-rate-detail.component.html'
})
export class UnitRateDetailComponent extends DfpsFormValidationDirective
  implements OnInit {
  unitRateDetailsForm: FormGroup;
  unitRateDetailResponse: CostReimbursementDetails;
  unitRateDetails;
  contractServiceId: any;
  contractId: any;
  contractNumber: string;
  totalVal: number;
  period: string;
  version: string;
  paymentType: string;
  saveURTInfo: boolean;
  constructor(
    private formBuilder: FormBuilder,
    private contractService: ContractService,
    private route: ActivatedRoute,
    private router: Router,
    private navigationService: NavigationService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
    const params = this.route.snapshot.paramMap.get('contractId');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idContract', params);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Unit Rate Detail');
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.contractId = routeParams.get('contractId');
      this.period = routeParams.get('period');
      this.version = routeParams.get('version');
      this.contractServiceId = routeParams.get('contractServiceId');
      this.paymentType = routeParams.get('paymentType');
    }
    this.createForm();
    this.getUnitRateDetails(this.contractId, this.period, this.version, this.contractServiceId);
  }

  createForm() {
    this.unitRateDetailsForm = this.formBuilder.group({
      unitRateAmount: ['', DfpsCommonValidators.validateCurrency(12)],
      unitRateUsedAmount: [''],
      totalUnits: [''],
      usedUnits: [''],
      validateAmountError: [''],
      validateUnitsError: ['']
    }, {
      validators: [UnitRateDetailValidators.compareAmountfields]
    });
  }

  setFormValues() {
    this.unitRateDetailsForm.setValue({
      unitRateAmount: this.unitRateDetails.unitRateAmount ? Number(this.unitRateDetails.unitRateAmount).toFixed(2) : '0.00',
      unitRateUsedAmount: this.unitRateDetails.unitRateUsedAmount ? Number(this.unitRateDetails.unitRateUsedAmount).toFixed(2) : '0.00',
      totalUnits: this.unitRateDetails.totalUnits ? Number(this.unitRateDetails.totalUnits).toFixed(2) : '0.00',
      usedUnits: this.unitRateDetails.usedUnits ? Number(this.unitRateDetails.usedUnits).toFixed(2) : '0.00',
      validateAmountError: '',
      validateUnitsError: ''
    });
  }

  formInputUpdated(e) {
    const updatedVal = Number(e.target.value) / this.unitRateDetails.unitRate;
    this.totalVal = Math.round(Math.floor(updatedVal));
    this.unitRateDetailsForm.get('totalUnits').setValue(this.totalVal);
  }

  getUnitRateDetails(contractId, period, version, contractServiceId) {
    this.contractService.getContractServiceByPaymentType(contractId, period, version, contractServiceId).subscribe(res => {
      this.unitRateDetailResponse = res;
      this.unitRateDetails = this.unitRateDetailResponse.contractService;
      this.contractNumber = this.unitRateDetailResponse.contractService.contractNumber;
      this.setFormValues();
      this.determinePageMode();
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
  }

  determinePageMode() {
    if (this.unitRateDetailResponse.pageMode === 'VIEW') {
      this.unitRateDetailsForm.disable();
      this.saveURTInfo = false;
    } else if (this.unitRateDetailResponse.pageMode === 'EDIT') {
      FormUtils.enableFormControlStatus(this.unitRateDetailsForm, ['unitRateAmount']);
      this.saveURTInfo = true;
    }
  }
  saveCRMDetails() {

    if (this.validateFormGroup(this.unitRateDetailsForm)) {
      const updatedUnitRateFormValues = {
        ...this.unitRateDetailsForm.value,
        unitRateAmount: +this.unitRateDetailsForm.value.unitRateAmount,
      };
      const formVals = {
        ...this.unitRateDetails, ...updatedUnitRateFormValues
      };
      this.contractService.saveCRMDetails(this.contractId, this.period, this.version,
        { ...this.unitRateDetailResponse, ...formVals })
        .subscribe(res => {
          if (res) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['/financial/contract/header/' + this.contractId
              + '/' + this.contractNumber
              + '/period/' + this.period
              + '/version/' + this.version
              + '/service/']);
          }
        });
    }
  }
}
