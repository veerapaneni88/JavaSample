import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  DropDown,
  FormUtils,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ContractHeaderResponse } from '../model/ContractHeader';
import { ContractPeriodResponse } from '../model/ContractPeriod';
import { ContractService } from '../service/contract.service';
import { ContractPeriodValidators } from './contract-period.validator';
import { ValidateContractNumberComponent } from './validate-contract-number/validate-contract-number.component';

@Component({
  selector: 'contract-period',
  templateUrl: './contract-period.component.html',
  styleUrls: []
})
export class ContractPeriodComponent extends DfpsFormValidationDirective implements OnInit {
  contractPeriodForm: FormGroup;
  contractId: string;
  contractNumber: string;
  periodId: string;
  isNewPage = false;
  defaultStatus: DropDown[];
  contractPeriodResponse: ContractPeriodResponse = null;
  contractHeaderResponse: ContractHeaderResponse;
  hideSaveButton = false;
  hideValidateButton = false;
  status: DropDown[];
  displayContractNumberField: boolean;
  isContractNumberValid = true;
  showProcurementNumber: boolean;
  readonly functionTypesForContractNumber = ['APS', 'CPS', 'FAC'];
  readonly resourceTypesForContractNumber = ['01', '06'];
  readonly statusCodeForSignedStatus = ['CLS', 'CLT', 'PND'];

  constructor(
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private route: ActivatedRoute,
    private router: Router,
    private contractService: ContractService,
    private navigationService: NavigationService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    this.setUserData();
  }

  setUserData() {
    // set user data for 3rd level menu rendering
    this.contractId = this.route.snapshot.paramMap.get('contractId');
    this.contractNumber = this.route.snapshot.paramMap.get('contractNumber') === 'null' ? '0'
      : this.route.snapshot.paramMap.get('contractNumber');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idContract', this.contractId);
    this.navigationService.setUserDataValue('contractNumber', this.contractNumber);
  }

  createForm() {
    this.contractPeriodForm = this.formBuilder.group({
      startDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      contractNumber: [''],
      termDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      legalIdentifier: ['', [DfpsCommonValidators.validateId]],
      statusCode: ['', Validators.required],
      earlyTerm: ['', [DfpsCommonValidators.validateDate]],
      procurementNumber: ['', [ContractPeriodValidators.validateProcurementNumber]],
      renew: [false],
      signed: [false],
      contractId: [],
      period: [],
      lastUpdatedDate: [],
      scorContractNumber: [],
      resourceType: [],
      scorLinked: [],
      region: [],
      functionType: []
    }, {
      validators: [
        ContractPeriodValidators.validateEarlyTermDate()
      ]
    });
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Contract Period Detail');
    this.createForm();
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.contractId = routeParams.get('contractId');
      this.periodId = routeParams.get('period');
    }
    this.defaultStatus = [{ code: 'PND', decode: 'Pending' }];
    if (this.contractId && this.periodId) {
      this.initializeScreen();
    } else {
      this.isNewPage = true;
    }
  }

  initializeScreen() {
    this.contractService.getContractPeriod(this.contractId, this.periodId).subscribe((response) => {
      this.contractPeriodResponse = response;
      this.loadContractPeriod();
      this.determinePageMode();
      this.showHideButtons();
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });
  }

  loadContractPeriod() {
    this.contractPeriodForm.setValue(
      {
        startDate: this.contractPeriodResponse.contractPeriod.startDate,
        contractNumber: this.contractPeriodResponse.contractPeriod.contractId,
        termDate: this.contractPeriodResponse.contractPeriod.termDate,
        legalIdentifier: this.contractPeriodResponse.contractPeriod.legalIdentifier,
        statusCode: this.contractPeriodResponse.contractPeriod.statusCode,
        earlyTerm: this.contractPeriodResponse.contractPeriod.closureDate,
        procurementNumber: this.contractPeriodResponse.contractPeriod.procurementNumber,
        renew: this.contractPeriodResponse.contractPeriod.renew,
        signed: this.contractPeriodResponse.contractPeriod.signed,
        contractId: this.contractPeriodResponse.contractPeriod.contractId,
        period: this.contractPeriodResponse.contractPeriod.period,
        lastUpdatedDate: this.contractPeriodResponse.contractPeriod.lastUpdatedDate,
        scorContractNumber: this.contractPeriodResponse.contractPeriod.scorContractNumber,
        resourceType: this.contractPeriodResponse.contractResource.resourceType,
        region: this.contractPeriodResponse.contractResource.region,
        functionType: this.contractPeriodResponse.contractResource.functionType,
        scorLinked: this.contractPeriodResponse.contractPeriod.scorLinked
      }
    );
    this.status = [...this.contractPeriodResponse.status];
    const contractResource = this.contractPeriodResponse.contractResource;
    this.displayContractNumberField = this.resourceTypesForContractNumber.includes(contractResource.resourceType) &&
      this.functionTypesForContractNumber.includes(contractResource.functionType);
    this.setValidatorsDynamically();
  }

  determinePageMode() {
    const contractPeriod = this.contractPeriodResponse.contractPeriod;
    const contractResource = this.contractPeriodResponse.contractResource;

    if (this.contractPeriodResponse.pageMode === 'VIEW') {
      const isFaHome = contractResource.functionType === 'FAD';
      this.contractPeriodForm.disable();
      if (isFaHome) {
        FormUtils.enableFormControlStatus(this.contractPeriodForm, ['startDate']);
        this.showProcurementNumber = false;
      } else {
        this.showProcurementNumber = true;
      }
    }
    if (this.contractPeriodResponse.pageMode === 'EDIT') {
      const isFaHome = contractResource.functionType === 'FAD';
      FormUtils.disableFormControlStatus(this.contractPeriodForm, ['startDate', 'termDate', 'earlyTerm']);
      this.setFilteredStatusList(contractPeriod.signed);
      FormUtils.enableFormControlStatus(this.contractPeriodForm,
        ['scorContractNumber', 'legalIdentifier', 'statusCode', 'renew']);
      if (contractPeriod.procurementNumber) {
        FormUtils.disableFormControlStatus(this.contractPeriodForm, ['procurementNumber']);
      }
      if (contractPeriod.scorContractNumber) {
        FormUtils.disableFormControlStatus(this.contractPeriodForm, ['scorContractNumber']);
      }
      if (contractPeriod.signDisabled) { FormUtils.disableFormControlStatus(this.contractPeriodForm, ['signed']); }
      if (isFaHome || contractPeriod.legalIdentifier) {
        FormUtils.disableFormControlStatus(this.contractPeriodForm, ['legalIdentifier']);
      }
      if (isFaHome) {
        FormUtils.enableFormControlStatus(this.contractPeriodForm, ['startDate']);
        this.showProcurementNumber = false;
      } else {
        this.showProcurementNumber = true;
      }
      if (contractPeriod.statusCode === 'CLS') {
        this.status.push({ code: 'CLS', decode: 'Closed' });
        FormUtils.disableFormControlStatus(this.contractPeriodForm, ['statusCode']);
      }
      if (contractPeriod.statusCode === 'PNT') {
        FormUtils.enableFormControlStatus(this.contractPeriodForm, ['earlyTerm']);
      }
      this.signedSubscription();
    } else if (this.contractPeriodResponse.pageMode === 'NEW') {
      const isFaHome = contractResource.functionType === 'FAD';
      if (!isFaHome) {
        this.showProcurementNumber = true;
      }
      this.status = this.defaultStatus;
      FormUtils.disableFormControlStatus(this.contractPeriodForm, ['signed', 'earlyTerm']);
    }

  }

  filterStatusBasedOnSignedStatus() {
    this.status = this.contractPeriodResponse.status.filter(data => {
      return !this.statusCodeForSignedStatus.includes(data.code);
    });
  }

  signedSubscription() {
    this.contractPeriodForm.get('signed').valueChanges.subscribe((data) => {
      this.setFilteredStatusList(data);
      this.clearStatusCode();
    });
  }

  setFilteredStatusList(data) {
    if (data) {
      this.filterStatusBasedOnSignedStatus();
    } else {
      this.status = this.defaultStatus;
    }
  }

  clearStatusCode() {
    this.contractPeriodForm.controls.statusCode.setValue('');
  }

  showHideButtons() {
    if (this.contractPeriodResponse.pageMode === 'VIEW') {
      this.hideSaveButton = true;
      this.hideValidateButton = true;
    }
  }

  doSave() {
    if (this.validateFormGroup(this.contractPeriodForm)) {

      if (!this.contractPeriodResponse.contractPeriod.scorLinked && this.displayContractNumberField) {
        const initialState = {
          title: 'Contract Period',
          message: 'The Contract period will be saved with no Contract Number linked. Do you want to continue?',
          showCancel: true
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md modal-dialog-centered',
          initialState
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
          if (result === true) {
            this.saveContractPeriod();
          }
        });
      } else {
        this.saveContractPeriod();
      }
    }
  }

  saveContractPeriod() {
    const payload = Object.assign(this.contractPeriodResponse.contractPeriod, this.contractPeriodForm.getRawValue());
    payload.closureDate = payload.earlyTerm;
    this.contractService.saveContractPeriod(payload).subscribe(
      response => {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        const routeUrl = 'financial/contract/header/' + this.contractId + '/' + this.contractNumber;
        this.router.navigate([routeUrl]);
      }
    );
  }

  doValidate() {
    const contractNumber = this.contractPeriodForm.get('scorContractNumber').value;
    if (contractNumber) {
      this.contractService.validateContractNumber(contractNumber).subscribe(res => {
        const modalFieldValues = res;
        this.isContractNumberValid = res.contractValidated;
        const modal = this.modalService.show(ValidateContractNumberComponent, {
          class: 'modal-lg modal-dialog-centered', initialState: modalFieldValues
        });
        (modal.content as ValidateContractNumberComponent).validationEvent.subscribe(result => {
          this.contractPeriodResponse.contractPeriod.scorLinked = result;
          this.setValidatorsDynamically();
          this.contractPeriodForm.get('scorLinked').setValue(result);
          if (result === true) {
            this.contractPeriodForm.get('procurementNumber').setValue(res.procurement);
          }
        });
      });
    } else {
      const initialState = {
        title:'Contract Period Detail',
        message: 'Please enter a Contract Number to validate.',
        showCancel: true
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered', initialState
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
        document.getElementById('validateResource').focus();
      });
    }
  }

  statusCodeChange(event) {
    const earlyTermDateControl = this.contractPeriodForm.get('earlyTerm');
    if (event.target.value.includes('PNT')) {
      earlyTermDateControl.setValue('');
      earlyTermDateControl.setValidators([Validators.required, DfpsCommonValidators.validateDate]);
      FormUtils.enableFormControlStatus(this.contractPeriodForm, ['earlyTerm']);
    } else {
      FormUtils.disableFormControlStatus(this.contractPeriodForm, ['earlyTerm']);
      earlyTermDateControl.setValue(this.contractPeriodForm.getRawValue().termDate);
      earlyTermDateControl.setValidators([DfpsCommonValidators.validateDate]);
    }
  }

  setValidatorsDynamically() {
    this.contractPeriodForm.setValidators([
      // ContractPeriodValidators.compareStartAndTermDates(),
      ContractPeriodValidators.validateEarlyTermDate(),
      ContractPeriodValidators.validateContractNumber(
        this.isContractNumberValid,
        this.displayContractNumberField,
        this.contractPeriodResponse.contractPeriod.scorLinked)
    ]
    );
  }
}
