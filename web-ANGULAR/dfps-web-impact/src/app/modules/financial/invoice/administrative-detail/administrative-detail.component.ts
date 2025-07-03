import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsFormValidationDirective,
  DfpsCommonValidators,
  DirtyCheck,
  NavigationService,
  FormUtils,
  SET
} from 'dfps-web-lib';
import { ADMValidators } from '../administrative-detail/administrative-detail.validator';
import { InvoiceService } from '../service/invoice.service';
@Component({
  selector: 'administrative-detail',
  templateUrl: './administrative-detail.component.html',
  styleUrls: []
})

export class AdministrativeDetailComponent extends DfpsFormValidationDirective implements OnInit {
  administrativeDetailForm: FormGroup;
  adminDetailId: any;
  invoiceId: any;
  invoicePhase: any;
  administrativeFormDetails;
  adminFormDetails;
  saveAdminInfo: boolean;
  isSaveClicked = false;
  infoDivDisplay = false;
  informationalMsgs: string[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private navigationService: NavigationService,
    private invoiceService: InvoiceService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    this.setUserData();
  }

  setUserData() {
    // set user data for 3rd level menu rendering
    const params = this.route.snapshot.paramMap.get('invoiceId');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('invoiceId', params);
  }

  ngOnInit() {
    this.navigationService.setTitle('Administrative Detail');
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.invoiceId = routeParams.get('invoiceId');
      this.adminDetailId = routeParams.get('id');
    }
    this.createForm();
    this.getAdministrativeFormDetails(this.invoiceId, this.adminDetailId);
  }

  createForm() {
    this.administrativeDetailForm = this.fb.group({
      service: ['', [Validators.required]],
      csli: ['', [Validators.required, ADMValidators.validateCsli()]],
      month: ['', [Validators.required, DfpsCommonValidators.validateMonth]],
      year: ['', [Validators.required, DfpsCommonValidators.validateYear]],
      salaries: ['', DfpsCommonValidators.validateCurrency(11)],
      benefits: ['', DfpsCommonValidators.validateCurrency(11)],
      travel: ['', DfpsCommonValidators.validateCurrency(11)],
      supplies: ['', DfpsCommonValidators.validateCurrency(11)],
      equipment: ['', DfpsCommonValidators.validateCurrency(11)],
      other: ['', DfpsCommonValidators.validateCurrency(11)],
      administrative: ['', DfpsCommonValidators.validateCurrency(11)],
      offset: ['', DfpsCommonValidators.validateCurrency(11)],
      isReversal: [''],
      errorMsg: [''],
      negativeAmountError: [''],
      offsetAmountError: [''],
      lastUpdatedDate: [],
      monthYear: ['']
    }, {
      validators: [ADMValidators.validateOffsetSum(), 
        ADMValidators.fiscalYearMonthValidation(), DfpsCommonValidators.futureYearMonthValidation()]
    });
  }

  getAdministrativeFormDetails(invoiceId, adminDetailId) {
    this.invoiceService.getAdministrativeDetails(invoiceId, adminDetailId).subscribe(res => {
      this.adminFormDetails = res;
      this.administrativeFormDetails = this.adminFormDetails.adminDetailDto;
      this.setFormValues();
      this.infoDivDisplay = this.adminFormDetails.isErrorServiceCode;
      this.determinePageMode();
      if (this.infoDivDisplay === true) {
        this.informationalMsgs
          .push('One or more Service Codes could not be decoded.Contact Help Desk.');
      }
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    })
  }

  determinePageMode() {
    if (this.adminFormDetails.pageMode === 'VIEW') {
      this.administrativeDetailForm.disable();
      this.saveAdminInfo = false;
    } else if (this.adminFormDetails.pageMode === 'EDIT' || this.adminFormDetails.pageMode === 'NEW') {
      if (this.adminFormDetails.pageMode === 'NEW' && this.infoDivDisplay === true) {
        this.adminFormDetails.serviceList = [];
      }
      FormUtils.enableFormControlStatus(this.administrativeDetailForm, ['service', 'csli', 'month', 'year',
        'salaries', 'benefits', 'travel', 'supplies', 'equipment', 'other', 'administrative', 'offset']);
      this.saveAdminInfo = true;
    }
  }

  setFormValues() {
    this.administrativeDetailForm.patchValue({
      csli: this.administrativeFormDetails.csli,
      service: this.administrativeFormDetails.service,
      month: this.administrativeFormDetails.month,
      year: this.administrativeFormDetails.year,
      salaries: this.administrativeFormDetails.salaries
        ? Number(this.administrativeFormDetails.salaries).toFixed(2)
        : '0.00',
      benefits: this.administrativeFormDetails.benefits
        ? Number(this.administrativeFormDetails.benefits).toFixed(2)
        : '0.00',
      travel: this.administrativeFormDetails.travel
        ? Number(this.administrativeFormDetails.travel).toFixed(2)
        : '0.00',
      supplies: this.administrativeFormDetails.supplies
        ? Number(this.administrativeFormDetails.supplies).toFixed(2)
        : '0.00',
      equipment: this.administrativeFormDetails.equipment
        ? Number(this.administrativeFormDetails.equipment).toFixed(2)
        : '0.00',
      other: this.administrativeFormDetails.other
        ? Number(this.administrativeFormDetails.other).toFixed(2)
        : '0.00',
      administrative: this.administrativeFormDetails.administrative
        ? Number(this.administrativeFormDetails.administrative).toFixed(2)
        : '0.00',
      offset: this.administrativeFormDetails.offset
        ? Number(this.administrativeFormDetails.offset).toFixed(2)
        : '0.00',
      isReversal: this.administrativeFormDetails.isReversal,
      lastUpdatedDate: this.administrativeFormDetails.lastUpdatedDate,
      errorMsg: '',
      negativeAmountError: '',
      offsetAmountError: ''
    })
  }

  saveADMDetails() {
    this.isSaveClicked = true;
    if (this.validateFormGroup(this.administrativeDetailForm)) {
      const formValues = {
        adminDetailId: this.adminDetailId,
      }
      const ADMFormVals = {
        ...this.administrativeDetailForm.value, ...formValues
      }
      if (this.adminFormDetails.pageMode === 'EDIT') {
        this.invoiceService.updateADMDetails(this.invoiceId, ADMFormVals).subscribe(res => {
          if (res) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['/financial/invoice/header/' + this.invoiceId]);
          }
        })
      } else if (this.adminFormDetails.pageMode === 'NEW') {
        this.invoiceService.updateADMDetails(this.invoiceId, this.administrativeDetailForm.value).subscribe(res => {
          if (res) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['/financial/invoice/header/' + this.invoiceId]);
          }
        })
      }

    }
  }

  validateMonthYear(event) {
    if (this.isSaveClicked) {
      DfpsCommonValidators.validateFutureYearMonth(this.administrativeDetailForm);
      this.validateFormControl(this.administrativeDetailForm.controls.monthYear);
    }
  }

}
