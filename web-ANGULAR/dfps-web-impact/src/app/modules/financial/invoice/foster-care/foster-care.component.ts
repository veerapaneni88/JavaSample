import { Component, HostListener, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
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
import { FosterCareDetail, FosterCareRes } from '../model/FosterCare';
import { InvoiceService } from '../service/invoice.service';
import { FosterCareValidators } from './foster-care.validator';

@Component({
  selector: 'foster-care',
  templateUrl: './foster-care.component.html',
  styleUrls: []
})
export class FosterCareComponent extends DfpsFormValidationDirective implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private invoiceService: InvoiceService,
    private route: ActivatedRoute,
    private bsModalService: BsModalService,
    private router: Router,
    private navigationService: NavigationService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    this.setUserData();
  }

  fosterCareForm: FormGroup;
  fosterCare: FosterCareDetail;
  invoiceId = '0';
  fosterCareId = '0';
  itemTotal = '0';
  service: DropDown[] = [];

  hideValidateButton = false;
  hideSaveButton = true;
  disableReversal = false;
  isSaveClicked = false;
  fosterCareResponse: FosterCareRes;
  infoDivDisplay = false;
  informationalMsgs: string[] = [];

  setUserData() {
    const params = this.route.snapshot.paramMap.get('invoiceId');
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.navigationService.setUserDataValue('idInvoice', params);
  }

  createForm() {
    this.fosterCareForm = this.formBuilder.group({
      personId: ['', [Validators.required, DfpsCommonValidators.validateMaxId]],
      resourceId: ['', [Validators.required, DfpsCommonValidators.validateMaxId]],
      month: ['', [Validators.required, DfpsCommonValidators.validateMonth]],
      year: ['', [Validators.required, DfpsCommonValidators.validateYear]],
      fromDay: ['', [FosterCareValidators.validateDayRange]],
      toDay: ['', [FosterCareValidators.validateDayRange]],
      service: ['', [Validators.required]],
      rate: ['', [Validators.required, DfpsCommonValidators.validateCurrency(7)]],
      income: ['', [DfpsCommonValidators.validateCurrency(11)]],
      invoiceReceivedDate: [],
      isReversal: [false],
      isValidated: [false],
      monthYear: ['']
    }, {
      validators: [FosterCareValidators.validateDays,
      FosterCareValidators.validateFosterCareSave,
      FosterCareValidators.validateIncome,
      DfpsCommonValidators.futureYearMonthValidation]
    });
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Foster Care Detail');
    this.createForm();
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.invoiceId = routeParams.get('invoiceId');
      this.fosterCareId = routeParams.get('fosterCareId');
      this.invoiceService.getFosterCareDetail(this.invoiceId, this.fosterCareId).subscribe(
        res => {
          this.fosterCareResponse = res;
          this.service = res.service;
          if (res.pageMode === 'EDIT') {
            this.hideSaveButton = false;
          }
          if (res.pageMode === 'VIEW') {
            this.hideValidateButton = true;
            this.fosterCareForm.disable();
          }
          this.loadFosterCare();
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        }
      )
    }

    this.fosterCareForm.get('personId').valueChanges.subscribe((value) => {
      this.fosterCareForm.get('isValidated').setValue('false');
    });

    this.fosterCareForm.get('resourceId').valueChanges.subscribe((value) => {
      this.fosterCareForm.get('isValidated').setValue('false');
    });
  }

  loadFosterCare() {
    if (this.fosterCareResponse) {
      if (this.fosterCareResponse.pageMode === 'NEW') {
        this.fosterCareForm.patchValue(
          {
            rate: (0).toFixed(2),
            income: (0).toFixed(2),
          });
      }
      else {
        this.itemTotal = this.fosterCareResponse.fosterCare.itemTotal;
        this.fosterCareForm.patchValue(
          {
            personId: this.fosterCareResponse.fosterCare.personId,
            resourceId: this.fosterCareResponse.fosterCare.resourceId,
            month: this.fosterCareResponse.fosterCare.month,
            year: this.fosterCareResponse.fosterCare.year,
            fromDay: this.fosterCareResponse.fosterCare.fromDay,
            toDay: this.fosterCareResponse.fosterCare.toDay,
            service: this.fosterCareResponse.fosterCare.service,
            rate: Number(this.fosterCareResponse.fosterCare.rate || 0).toFixed(2),
            income: Number(this.fosterCareResponse.fosterCare.income || 0).toFixed(2),
            isReversal: this.fosterCareResponse.fosterCare.lineType === 'R',
            invoiceReceivedDate: null,
            isValidated: false
          }
        );
      }
    }
    this.infoDivDisplay = this.fosterCareResponse.isErrorServiceCode;
    if (this.infoDivDisplay === true) {
      this.informationalMsgs
        .push('One or more Service Codes could not be decoded.Contact Help Desk.');
      if (this.fosterCareResponse.pageMode === 'NEW') {
        this.fosterCareResponse.service = [];
      }
    }
    if (this.fosterCareResponse.invoice) {
      this.fosterCareForm.patchValue(
        {
          invoiceReceivedDate: this.fosterCareResponse.invoice.receivedDate
        }
      );
    }

    this.diableReversal();
  }

  validate() {
    const personId: string = this.fosterCareForm.get('personId').value;
    const resourceId: string = this.fosterCareForm.get('resourceId').value;
    if (personId === '' || resourceId === '') {
      const initialState = {
        title: 'Foster Care',
        message: 'Please enter Person ID and Resource ID to validate.',
        showCancel: false
      };
      const modal = this.bsModalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {

      });
    } else {
      const controls: AbstractControl[] = [];
      controls.push(this.fosterCareForm.get('personId'));
      controls.push(this.fosterCareForm.get('resourceId'));

      if (this.validateFormControls(controls)) {
        const req: any = {
          personId,
          resourceId
        };
        this.invoiceService.validateFosterCare(this.invoiceId, req).subscribe(res => {
          if (res) {
            const fosterCare: any = {};
            fosterCare.facilityAcclaim = res.facilityAcclaim;
            fosterCare.personId = res.personId;
            fosterCare.fullName = res.personFullName;
            fosterCare.resourceId = res.resourceId;
            this.fosterCareResponse.fosterCare = this.fosterCareResponse.fosterCare ?
              Object.assign(this.fosterCareResponse.fosterCare, fosterCare) : fosterCare;
            this.fosterCareForm.controls.isValidated.setValue(true);
            this.hideSaveButton = false;
          }
        });
      }
    }
  }

  diableReversal() {
    if (this.fosterCareResponse.invoice.adjustment !== 'I') {
      FormUtils.disableFormControlStatus(this.fosterCareForm, ['isReversal']);
    }
  }

  save() {
    this.isSaveClicked = true;
    if (this.validateFormGroup(this.fosterCareForm)) {
      if (this.fosterCareResponse.invoice.adjustment !== 'I') {
        this.fosterCareResponse.fosterCare.isReversal = false;
      }
      this.invoiceService.saveFosterCare(this.invoiceId, Object.assign(this.fosterCareResponse.fosterCare, this.fosterCareForm.value))
        .subscribe(res => {
          if (res) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['/financial/invoice/header/' + this.invoiceId]);
            const invoiceLocalStorge = localStorage.getItem('invoiceHeader');
            const invoiceHeaderData = JSON.parse(invoiceLocalStorge);
            invoiceHeaderData.focusItem = this.router.url;
            localStorage.setItem('backFrom', JSON.stringify({naivgatedBackFrom: 'fostercare'}));
            localStorage.setItem('invoiceHeader', JSON.stringify(invoiceHeaderData));
          }
        });
    }
  }

  validateMonthYear(event) {
    if (this.isSaveClicked) {
      DfpsCommonValidators.validateFutureYearMonth(this.fosterCareForm);
      this.validateFormControl(this.fosterCareForm.controls.monthYear);
    }
  }

  @HostListener('window:popstate' || 'window:hashchange', ['$event'])
  onPopState(event) {
    const invoiceLocalStorge = localStorage.getItem('invoiceHeader');
    const invoiceHeaderData = JSON.parse(invoiceLocalStorge);
    invoiceHeaderData.focusItem = this.router.url;
    localStorage.setItem('backFrom', JSON.stringify({naivgatedBackFrom: 'fostercare'}));
    localStorage.setItem('invoiceHeader', JSON.stringify(invoiceHeaderData));
  }
}
