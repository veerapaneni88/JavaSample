import { DatePipe } from '@angular/common';
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
import { FinancialAccountService } from '../service/financial-account.service';
import { FinancialAccountSearch } from './../model/FinancialAccount';
import { FinancialTransaction, FinancialTransactionRes } from './../model/FinancialRegister';
import { FinancialAccountValidators } from './financial-account-register.validator';

@Component({
  templateUrl: './financial-account-register-detail.component.html'
})
export class FinancialAcccountRegisterDetailComponent extends DfpsFormValidationDirective implements OnInit {

  financialAcctRegisterDetailForm: FormGroup;
  financialTransactionRes: FinancialTransactionRes;

  financialAccountId: any;
  financialTransactionId: any;
  usingfinancialTransactionId = null;

  types: any;
  program: any;
  personName = '';

  categoryDropDown: DropDown[] = [];
  subCategoryDropDown: DropDown[] = [];
  accountNumbers = [];

  isCpsCheckAccount = false;
  displayDeleteButton = true;
  displaySaveButton = true;

  constructor(private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private datePipe: DatePipe,
    private navigationService: NavigationService,
    private modalService: BsModalService,
    private financialAccountService: FinancialAccountService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
    this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
    this.financialAccountId = this.route.snapshot.paramMap.get('accountId');
    this.financialTransactionId = this.route.snapshot.paramMap.get('transactionId');
    this.navigationService.setUserDataValue('idFinancialAccount', this.financialAccountId);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Financial Account Register Detail');
    this.financialAccountId = this.route.snapshot.paramMap.get('accountId');
    this.financialTransactionId = this.route.snapshot.paramMap.get('transactionId');
    this.usingfinancialTransactionId = this.route.snapshot.queryParamMap.get('using');
    this.createForm();
    this.intializeScreen();
  }

  createForm() {
    this.financialAcctRegisterDetailForm = this.formBuilder.group({
      type: [],
      category: ['', [Validators.required]],
      accountNo: [''],
      ach: [''],
      warrantNumber: [],
      subcategory: [],
      accountHoldDate: [],
      transactionDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      amount: ['', [Validators.required, DfpsCommonValidators.validateCurrency(12)]],
      payeeName: [],
      description: [],
      invoiceId: [],
      status: [''],
      currentBalance: [],
      program: [],
      isCpsCheckAccount: []
    }, {
      validators: FinancialAccountValidators.saveValidations
    });
  }

  intializeScreen() {
    this.types = [
      { label: 'Debit', value: 'Debit' },
      { label: 'Credit', value: 'Credit' },
      { label: 'Hold', value: 'Hold' },
    ];


    this.financialAcctRegisterDetailForm.get('type').valueChanges.subscribe((value) => {
      this.subCategoryDropDown = [];
      if (value && value === 'Debit') {
        this.categoryDropDown = this.financialTransactionRes.debitCategory;
        if (this.financialTransactionRes.pageMode === 'NEW') {
          this.financialAcctRegisterDetailForm.get('category').setValue('');

        }
      } else if (value === 'Credit' || value === 'Hold') {
        this.categoryDropDown = this.financialTransactionRes.creditCategory;
        if (this.financialTransactionRes.pageMode === 'NEW') {
          this.financialAcctRegisterDetailForm.get('category').setValue('');
        }
      }

      if (value && value === 'Hold' && this.financialTransactionRes.pageMode === 'NEW') {
        this.financialAcctRegisterDetailForm.get('category').setValue('OH');
        this.subCategoryDropDown = [];
        this.financialAcctRegisterDetailForm.get('subcategory').setValue('');
        const currentDate = this.datePipe.transform(new Date().toString(), 'MM/dd/yyyy', 'es-ES');
        this.financialAcctRegisterDetailForm.get('accountHoldDate').setValue(currentDate);
      }

      this.financialAcctRegisterDetailForm.get('category').valueChanges.subscribe((val) => {
        this.getSubCategory(val);
        this.populateAccountNumber();
      })
    });

    this.financialAcctRegisterDetailForm.get('ach').valueChanges.subscribe((value) => {
      if (value) {
        this.financialAcctRegisterDetailForm.get('warrantNumber').setValue('');
        FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm, ['warrantNumber']);
      } else {
        FormUtils.enableFormControlStatus(this.financialAcctRegisterDetailForm, ['warrantNumber']);
      }
    });
    this.loadFormData();
  }

  loadFormData() {
    this.financialAccountService.getFinancialAccountTransaction(this.financialAccountId,
      this.usingfinancialTransactionId !== null ? this.usingfinancialTransactionId : this.financialTransactionId)
      .subscribe(
        response => {
          this.financialTransactionRes = response;
          this.isCpsCheckAccount = this.financialTransactionRes.financialAccount.cpsChkAcct;
          this.program = this.financialTransactionRes.financialAccount.program;
          if (response.financialTransaction) {
            this.financialAcctRegisterDetailForm.patchValue({
              type: this.getType(response.financialTransaction.type),
              category: response.financialTransaction.category,
              ach: response.financialTransaction.ach,
              warrantNumber: response.financialTransaction.warrantNumber,
              subcategory: response.financialTransaction.subcategory,
              accountHoldDate: response.financialTransaction.accountHoldDate,
              transactionDate: response.financialTransaction.transactionDate,
              amount: Number(response.financialTransaction.amount).toFixed(2),
              payeeName: response.financialTransaction.payeeName,
              description: response.financialTransaction.description,
              invoiceId: response.financialTransaction.invoiceId,
              status: response.financialTransaction.status
            });
          }
          if (response.financialAccount) {
            this.financialAcctRegisterDetailForm.patchValue({
              currentBalance: response.financialAccount.availableBalance,
              program: response.financialAccount.program,
              isCpsCheckAccount: response.financialAccount.cpsChkAcct
            });
          }
          if (this.financialTransactionRes.financialAccount.program === 'APS') {
            this.types.splice(2);
          }
          this.financialAcctRegisterDetailForm.setValidators([FinancialAccountValidators.saveValidations]);
          this.setPageModes();
        }
      )
    this.financialAcctRegisterDetailForm.controls.type.enable({ emitEvent: true });
  }

  setPageModes() {
    FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm, ['accountHoldDate']);
    if (this.financialTransactionRes.disableStatus) {
      FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm, ['status']);
    }
    if (this.financialTransactionRes.disableDescription) {
      FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm, ['description']);
    }
    if (this.usingfinancialTransactionId !== null) {
      this.financialTransactionRes.pageMode = 'NEWUSING';
      this.displayDeleteButton = false;
    }
    if (this.financialTransactionRes.pageMode === 'NEW') {
      this.displayDeleteButton = false;
      this.financialAcctRegisterDetailForm.get('type').setValue('Debit');
      if (!this.isCpsCheckAccount) {
        this.financialAcctRegisterDetailForm.get('warrantNumber').setValue(0);
      }
      this.selected();

      if (this.isCpsCheckAccount) {
        FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm, ['ach']);
      }
    }

    if (this.financialTransactionRes.pageMode === 'EDIT') {
      this.displaySaveButton = true;
      if (this.program === 'CPS') {
        if (this.isCpsCheckAccount) {
          FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm,
            [
              'type',
              'category',
              'subcategory',
              'ach',
              'warrantNumber',
              'accountNo',
              'amount'
            ])
        } else {
          if (this.isTransferCategory()) {
            this.displayDeleteButton = false;
          }
          FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm,
            [
              'type',
              'amount'
            ]);
        }
      } else if (this.program === 'APS') {
        FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm, ['amount']);
      }
    } else if (this.financialTransactionRes.pageMode === 'VIEW') {
      this.financialAcctRegisterDetailForm.disable();
      this.displayDeleteButton = false;
      this.displaySaveButton = false;
    }
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  getType(value: string): any {
    let selectedValue = null;
    this.types.forEach(e => {
      if (e.value === value) {
        selectedValue = e.value;
      }
    });
    return selectedValue;
  }

  getSubCategory(value) {
    this.subCategoryDropDown = [];
    if (value) {
      this.financialTransactionRes.subCategory.forEach(e => {
        if (e.code.substr(0, 2) === value) {
          this.subCategoryDropDown.push(e);
        }
      });
    }
  }

  save() {
    if (this.financialTransactionRes.financialAccount.program === 'CPS') {
      const selectedCateogry = this.financialAcctRegisterDetailForm.controls.category.value;
      const accountNumber = this.financialAcctRegisterDetailForm.controls.accountNo.value;
      if ((selectedCateogry === 'TC' || selectedCateogry === 'TS' || selectedCateogry === 'TT') && accountNumber === '') {
        this.showModal('Please select Account number before saving');
        return;
      }

    }
    if (this.validateFormGroup(this.financialAcctRegisterDetailForm)) {
      if (this.financialTransactionRes.pageMode === 'NEWUSING') {
        this.financialTransactionRes.financialTransaction.id = '0';
        this.financialTransactionRes.financialTransaction.lastUpdatedDate = '';
      }

      const formValues: FinancialTransaction = this.financialAcctRegisterDetailForm.getRawValue();
      formValues.transferFinancialAccountId = this.financialAcctRegisterDetailForm.controls.accountNo.value;
      formValues.id = this.financialTransactionId;

      this.financialAccountService.saveFinancialAccountTransaction(
        this.financialTransactionRes.financialAccount.id, formValues
      ).subscribe(
        res => {
          if (res) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['financial/financial-account/register/' + this.financialAccountId]);
          }
        }
      );
    }
  }

  delete() {
    const initialState = {
      title: 'Financial Account Register',
      message: 'Are you sure you want to delete this information?',
      showCancel: true
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md', initialState
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
      if (result === true) {
        this.financialAccountService.deleteFinancialAccountTransactions(
          this.financialTransactionRes.financialAccount.id, [this.financialTransactionRes.financialTransaction.id]).subscribe(
            res => {
              if (res) {
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                this.router.navigate(['financial/financial-account/register/' + this.financialAccountId]);
              }
            }
          );
      }
    });
  }

  populateAccountNumber() {
    if (this.financialTransactionRes.pageMode !== 'EDIT' && this.financialTransactionRes.financialAccount.program === 'CPS') {
      this.accountNumbers = [];
      if (this.financialTransactionRes.accountNumbers) {
        const accountObjects: FinancialAccountSearch[] = this.financialTransactionRes.accountNumbers;
        const selectedCateogry = this.financialAcctRegisterDetailForm.controls.category.value;
        accountObjects.forEach(account => {
          if ((selectedCateogry === 'TC' && account.accountType === 'CH') ||
            (selectedCateogry === 'TS' && account.accountType === 'SV') ||
            (selectedCateogry === 'TT' && account.accountType === 'TF')) {
            if (account.accountNumber !== this.financialTransactionRes.financialAccount.accountNumber) {
              this.accountNumbers.push({ code: account.financialAccountId, decode: account.accountNumber });
            }
          }
        });
        if (this.accountNumbers.length === 0) {
          this.accountNumbers.push({ code: '', decode: 'No Account #' })
        }
      } else {
        this.accountNumbers.push({ code: '', decode: 'No Account #' })
      }
    }
    this.financialAcctRegisterDetailForm.controls.accountNo.setValue(this.accountNumbers[0].code);
  }

  isTransferCategory() {
    const selectedCateogry = this.financialAcctRegisterDetailForm.controls.category.value;
    return (selectedCateogry === 'TC' || selectedCateogry === 'TU' ||
      selectedCateogry === 'TV' || selectedCateogry === 'TW' ||
      selectedCateogry === 'TS' || selectedCateogry === 'TT');
  }

  disableAch(event) {
    if (!this.isCpsCheckAccount && event.target.value) {
      FormUtils.disableFormControlStatus(this.financialAcctRegisterDetailForm, ['ach']);
    } else {
      FormUtils.enableFormControlStatus(this.financialAcctRegisterDetailForm, ['ach']);
    }
  }

  selected() {
    this.financialAcctRegisterDetailForm.get('type').valueChanges.subscribe((value) => {
      this.subCategoryDropDown = [];
      if (value && value === 'Debit') {
        this.categoryDropDown = this.financialTransactionRes.debitCategory;
        if (this.financialTransactionRes.pageMode === 'NEW') {
          this.financialAcctRegisterDetailForm.get('category').setValue('');
        }
      } else if (value === 'Credit' || value === 'Hold') {
        this.categoryDropDown = this.financialTransactionRes.creditCategory;
        if (this.financialTransactionRes.pageMode === 'NEW') {
          this.financialAcctRegisterDetailForm.get('category').setValue('');
        }
      }

      if (value && value === 'Hold') {
        this.financialAcctRegisterDetailForm.get('category').setValue('OH');
        this.subCategoryDropDown = [];
        this.financialAcctRegisterDetailForm.get('subcategory').setValue('');
        const currentDate = this.datePipe.transform(new Date().toString(), 'MM/dd/yyyy', 'es-ES');
        this.financialAcctRegisterDetailForm.get('accountHoldDate').setValue(currentDate);
      } else {
        this.financialAcctRegisterDetailForm.get('accountHoldDate').setValue('');
      }
    });
  }

  setHoldDate() {
    if (this.financialAcctRegisterDetailForm.get('type').value === 'Hold') {
      this.financialAcctRegisterDetailForm.get('category').setValue('OH');
      this.subCategoryDropDown = [];
      this.financialAcctRegisterDetailForm.get('subcategory').setValue('');
    }

    if (this.financialAcctRegisterDetailForm.get('category').value === 'OH') {
      const currentDate = this.datePipe.transform(new Date().toString(), 'MM/dd/yyyy', 'es-ES');
      this.financialAcctRegisterDetailForm.get('accountHoldDate').setValue(currentDate);
    } else {
      this.financialAcctRegisterDetailForm.get('accountHoldDate').setValue('');
    }
  }

  amountUpdated() {
    const currentBalance = this.financialAcctRegisterDetailForm.get('currentBalance').value;
    const amount = this.financialAcctRegisterDetailForm.get('amount').value;
    const type = this.financialAcctRegisterDetailForm.get('type').value;
    const program = this.financialAcctRegisterDetailForm.get('program').value;
    if (program === 'CPS' && (type === 'Debit' || type === 'Hold') && amount > currentBalance) {
      this.showModal('Entered amount cannot be more than the Current Available Balance.');
    }
  }

  showModal(text: string) {
    const initialState = {
      title: 'Financial Account Register',
      message: text,
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered',
      initialState,
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
  }

}
