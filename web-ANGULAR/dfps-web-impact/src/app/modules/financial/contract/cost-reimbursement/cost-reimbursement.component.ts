import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsCommonValidators,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { CostReimbursementDetails } from '../model/CostReimbursement';
import { ContractService } from '../service/contract.service';
import { CostReimbursementValidators } from './cost-reimbursement.validator';

@Component({
  selector: 'cost-reimbursement',
  templateUrl: './cost-reimbursement.component.html'
})
export class CostReimbursementComponent extends DfpsFormValidationDirective
  implements OnInit {
  costReimbursementForm: FormGroup;
  costReimbursementsDetailsResponse: CostReimbursementDetails;
  costReimbursementsDetails;
  totalVal: number;
  contractServiceId: any;
  contractId: any;
  contractNumber: any;
  period: string;
  version: string;
  saveCRMInfo: boolean;
  constructor(
    private formBuilder: FormBuilder,
    private navigationService: NavigationService,
    private costReimbursementService: ContractService, private route: ActivatedRoute, private router: Router,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Cost Reimbursement Detail Contract');
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.contractId = routeParams.get('contractId');
      this.contractNumber = routeParams.get('contractNumber');
      this.period = routeParams.get('period');
      this.version = routeParams.get('version');
      this.contractServiceId = routeParams.get('contractServiceId');
    }
    this.createForm();
    this.getcostReimbursementDetails(this.contractId, this.period, this.version, this.contractServiceId);
    this.formInputChanges();
  }

  getcostReimbursementDetails(contractId, period, version, contractServiceId) {
    this.costReimbursementService.getContractServiceByPaymentType(contractId, period, version, contractServiceId).subscribe(res => {
      this.costReimbursementsDetailsResponse = res;
      this.costReimbursementsDetails = this.costReimbursementsDetailsResponse.contractService;
      this.setFormValues();
      this.determinePageMode();
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
  }

  createForm() {
    this.costReimbursementForm = this.formBuilder.group({
      version: [''],
      lineItem: [''],
      salaryAmount: ['', [DfpsCommonValidators.validateCurrency(11)]],
      salaryUsedAmount: [''],
      fringeBenefitAmount: ['', [CostReimbursementValidators.validateFringeCurrency(11)]],
      fringeBenefitUsedAmount: [''],
      travelAmount: ['', [DfpsCommonValidators.validateCurrency(11)]],
      travelUsedAmount: [''],
      supplyAmount: ['', [DfpsCommonValidators.validateCurrency(11)]],
      supplyUsedAmount: [''],
      equipmentAmount: ['', [DfpsCommonValidators.validateCurrency(11)]],
      equipmentUsedAmount: [''],
      otherAmount: ['', [DfpsCommonValidators.validateCurrency(11)]],
      otherUsedAmount: [''],
      offsetItemUsedAmount: [''],
      adminAllUsedAmount: ['']
    }, {
      validators:
        [CostReimbursementValidators.compareAmountfields]
    });
  }

  setFormValues() {
    this.costReimbursementForm.setValue({
      version: this.costReimbursementsDetails.version,
      lineItem: this.costReimbursementsDetails.lineItem,
      salaryAmount: this.costReimbursementsDetails.salaryAmount
        ? Number(this.costReimbursementsDetails.salaryAmount).toFixed(2) : '0.00',
      salaryUsedAmount: this.costReimbursementsDetails.salaryUsedAmount
        ? this.costReimbursementsDetails.salaryUsedAmount : 0.00,
      fringeBenefitAmount: this.costReimbursementsDetails.fringeBenefitAmount
        ? Number(this.costReimbursementsDetails.fringeBenefitAmount).toFixed(2) : '0.00',
      fringeBenefitUsedAmount: this.costReimbursementsDetails.fringeBenefitUsedAmount
        ? this.costReimbursementsDetails.fringeBenefitUsedAmount : 0.00,
      travelAmount: this.costReimbursementsDetails.travelAmount
        ? Number(this.costReimbursementsDetails.travelAmount).toFixed(2) : '0.00',
      travelUsedAmount: this.costReimbursementsDetails.travelUsedAmount
        ? this.costReimbursementsDetails.travelUsedAmount : 0.00,
      supplyAmount: this.costReimbursementsDetails.supplyAmount
        ? Number(this.costReimbursementsDetails.supplyAmount).toFixed(2) : '0.00',
      supplyUsedAmount: this.costReimbursementsDetails.supplyUsedAmount
        ? this.costReimbursementsDetails.supplyUsedAmount : 0.00,
      equipmentAmount: this.costReimbursementsDetails.equipmentAmount
        ? Number(this.costReimbursementsDetails.equipmentAmount).toFixed(2) : '0.00',
      equipmentUsedAmount: this.costReimbursementsDetails.equipmentUsedAmount
        ? this.costReimbursementsDetails.equipmentUsedAmount : 0.00,
      otherAmount: this.costReimbursementsDetails.otherAmount
        ? Number(this.costReimbursementsDetails.otherAmount).toFixed(2) : '0.00',
      otherUsedAmount: this.costReimbursementsDetails.otherUsedAmount
        ? this.costReimbursementsDetails.otherUsedAmount : '0.00',
      offsetItemUsedAmount: this.costReimbursementsDetails.offsetItemUsedAmount
        ? this.costReimbursementsDetails.offsetItemUsedAmount : 0.00,
      adminAllUsedAmount: this.costReimbursementsDetails.adminAllUsedAmount
        ? this.costReimbursementsDetails.adminAllUsedAmount : 0.00
    });
  }

  formInputChanges() {
    this.costReimbursementForm.valueChanges.subscribe(value => {
      this.totalVal = Number(value.salaryAmount) + Number(value.fringeBenefitAmount) + Number(value.travelAmount)
        + Number(value.supplyAmount) + Number(value.equipmentAmount) + Number(value.otherAmount);
    });
  }

  determinePageMode() {
    if (this.costReimbursementsDetailsResponse.pageMode === 'VIEW') {
      this.costReimbursementForm.disable();
      this.saveCRMInfo = false;
    } else if (this.costReimbursementsDetailsResponse.pageMode === 'EDIT') {
      FormUtils.enableFormControlStatus(this.costReimbursementForm, ['salaryAmount', 'fringeBenefitAmount', 'travelAmount',
        'supplyAmount', 'equipmentAmount', 'otherAmount']);
      this.saveCRMInfo = true;
    }
  }

  saveCRMDetails() {

    if (this.validateFormGroup(this.costReimbursementForm)) {
      const updatedCostReimbursementFormValues = {
        ...this.costReimbursementForm.value,
        salaryAmount: +this.costReimbursementForm.value.salaryAmount,
        fringeBenefitAmount: +this.costReimbursementForm.value.fringeBenefitAmount,
        travelAmount: +this.costReimbursementForm.value.travelAmount,
        supplyAmount: +this.costReimbursementForm.value.supplyAmount,
        equipmentAmount: +this.costReimbursementForm.value.equipmentAmount,
        otherAmount: +this.costReimbursementForm.value.otherAmount
      };
      const formVals = {
        ...this.costReimbursementsDetails, ...updatedCostReimbursementFormValues
      };
      this.costReimbursementService.saveCRMDetails(this.contractServiceId, this.period, this.version,
        { ...this.costReimbursementsDetailsResponse, ...formVals })
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
