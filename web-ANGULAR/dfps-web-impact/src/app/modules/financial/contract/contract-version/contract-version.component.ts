import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Contract } from '../model/ContractHeader';
import { ContractPeriod } from '../model/ContractPeriod';
import { ContractVersion, ContractVersionRes } from '../model/ContractVersion';
import { ContractService } from '../service/contract.service';
import { ContractVersionValidators } from './contract-version.validator';

@Component({
  selector: 'contract-version',
  templateUrl: './contract-version.component.html',
  styleUrls: []
})
export class ContractVersionComponent extends DfpsFormValidationDirective implements OnInit {
  contractVersionForm: FormGroup;

  contractId: string;
  contractNumber: string;
  period: number;
  version: number;

  contractVersion: ContractVersion;
  contractVersionRes: ContractVersionRes;

  readonly twentyDaysInMillis = 20 * 24 * 60 * 60 * 1000;

  hideSaveButton = false;

  constructor(
    private formBuilder: FormBuilder,
    private navigationService: NavigationService,
    private route: ActivatedRoute,
    private router: Router,
    private contractService: ContractService,
    private modalService: BsModalService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
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
    this.contractVersionForm = this.formBuilder.group({
      effectiveDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      versionNumber: [],
      endDate: [],
      createDate: [],
      noShowPercent: [0, [ContractVersionValidators.validateNoShowPct]],
      locked: [false],
      comment: [''],
      lastUpdatedDate: ['']
    }, {
      validators: [
        ContractVersionValidators.validateEffectiveDate(),
      ]
    });
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Contract Version Detail');
    this.createForm();
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.contractId = routeParams.get('contractId');
      this.period = +routeParams.get('period');
      this.version = +routeParams.get('version');
    }
    this.getVersion(this.version);
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  getVersion(versionId: number, reload: boolean = false) {
    // check if the contract period is available in the injected service
    if (!reload && (versionId !== 0
      && this.contractService.getContractVersions().length > 0)) {
      const contractVersions = this.contractService.getContractVersions()
        .filter(version => version.versionNumber === versionId);
      let contractVersion = contractVersions[0];
      const contract: Contract = this.contractService.getContract();
      const contractPeriod: ContractPeriod = this.contractService.getCntrctPeriods()
        .filter(period => period.period === this.period)[0];
      contractVersion = {
        ...contractVersion,
        scorContractNumber: contract.scorContractNumber,
        region: contract.region,
        functionType: contract.functionType,
        periodClosureDate: contractPeriod.closureDate,
        periodSigned: contractPeriod.signed
      };
      this.contractVersionRes = {
        contractVersion,
        pageMode: this.contractService.getContractPageMode()
      };
      this.loadContractVersion(contractVersion);
    } else {
      // make the call to API to retreive
      this.contractService.getContractVersion(this.contractId, this.period, this.version).subscribe(
        res => {
          this.contractVersionRes = res;
          this.loadContractVersion(res.contractVersion);
        }
      );
    }
  }

  loadContractVersion(contractVersion: ContractVersion) {
    this.contractVersion = contractVersion;
    this.contractVersionForm.setValue({
      effectiveDate: contractVersion.effectiveDate,
      versionNumber: contractVersion.versionNumber,
      endDate: contractVersion.endDate,
      createDate: contractVersion.createDate,
      noShowPercent: contractVersion.noShowPercent || 0,
      locked: contractVersion.locked,
      comment: contractVersion.comment,
      lastUpdatedDate: contractVersion.lastUpdatedDate
    });
    this.determinePageMode();
    this.showHideButtons();
  }

  determinePageMode() {
    if (this.contractVersionRes.pageMode === 'VIEW' || this.contractVersionRes.pageMode === 'EDIT') {
      this.contractVersionForm.disable();
      if (this.contractVersionRes.pageMode === 'EDIT') {
        FormUtils.enableFormControlStatus(this.contractVersionForm, ['noShowPercent', 'comment']);
        if (!this.contractVersion.locked && this.contractVersion.periodSigned) {
          FormUtils.enableFormControlStatus(this.contractVersionForm, ['locked']);
        }
      }
    } else if (this.contractVersionRes.pageMode === 'NEW') {
      FormUtils.disableFormControlStatus(this.contractVersionForm, ['locked']);
      FormUtils.enableFormControlStatus(this.contractVersionForm, ['effectiveDate', 'comment']);
    }
  }

  showHideButtons() {
    if (this.contractVersionRes.pageMode === 'VIEW') {
      this.hideSaveButton = true;
    }
  }

  doSave() {
    if (this.validateFormGroup(this.contractVersionForm)) {
      if ((!this.contractVersionForm.get('locked').disabled && !this.contractVersionForm.get('locked').value) ||
            (!this.contractVersionForm.get('locked').disabled && this.contractVersionForm.get('locked').value)) {
        const initialState = {
          title: 'Contract Version',
          message: `Have you placed a check for each county applicable for each service in this contract?
         Click 'OK' if you have already checked the counties. Click 'Cancel' if you need to check the counties.`,
          showCancel: true
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md modal-dialog-centered',
          initialState
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
          if (result === true) {
            this.closureDateCheck();
          } else {
            // route to services
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['financial/contract/header/'
              + this.contractId + '/' + this.contractNumber
              + '/period/' + this.period
              + '/version/' + this.version
              + '/service/']);
          }
        });
      } else {
        this.closureDateCheck();
      }
    }
  }

  private closureDateCheck() {
    const closureDate = new Date(this.contractVersion.periodClosureDate);
    const effectiveDate = new Date(this.contractVersionForm.get('effectiveDate').value);
    if (this.contractVersionRes.pageMode === 'NEW' && effectiveDate < closureDate
      && ((closureDate.getTime() - effectiveDate.getTime()) < this.twentyDaysInMillis)) {
      const initialState = {
        title: 'Contract Version',
        message: 'Version becomes effective within 20 days of period close date. Continue?',
        showCancel: true
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered',
        initialState
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
        if (result === true) {
          this.saveContractVersions();
        }
      });
    } else {
      this.saveContractVersions();
    }

  }

  saveContractVersions() {
    const payload = Object.assign(this.contractVersion, this.contractVersionForm.value);
    const { period, contractId, contractWorkerId } = this.contractVersionRes.contractVersion;
    this.contractService.saveContractVersion({ period, contractId, contractWorkerId, ...payload }).subscribe(result => {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      const routeUrl = 'financial/contract/header/'
        + this.contractId
        + '/' + this.contractNumber;
      this.router.navigate([routeUrl]);
    });
  }
}

