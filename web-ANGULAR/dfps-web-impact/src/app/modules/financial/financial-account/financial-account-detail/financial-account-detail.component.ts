import { FinancialAccountDetailValidators } from './financial-account-detail-validator';
import { Component, OnInit, ViewChild, ContentChild, OnDestroy, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    Address,
    DfpsFormValidationDirective,
    DfpsAddressValidatorComponent,
    DirtyCheck,
    FormValidationError,
    NavigationService,
    FormUtils,
    SET,
    ERROR_RESET,
    SUCCESS_RESET,
    DfpsCommonValidators,
} from 'dfps-web-lib';
import { FinancialAccountRes, pageModes, programs, status, states } from './../model/FinancialAccount';
import { FinancialAccountService } from './../service/financial-account.service';
import { AccordionComponent } from 'ngx-bootstrap/accordion';

@Component({
    selector: 'financial-account-detail',
    templateUrl: './financial-account-detail.component.html',
    styleUrls: ['./financial-account-detail.component.css'],
})
export class FinancialAccountDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    financialAcctDetailForm: FormGroup;
    validationErrors: FormValidationError[] = [];
    validationErrorsLength = 0;
    searchIndicatorData: any;
    displayFinancialAcctResponse: FinancialAccountRes;
    accountId;
    isView = true;
    isNew = false;
    isShowValidate = false;
    isValidated = false;
    showDedicated = false;
    isPersonIdValidated: boolean;
    @ViewChild('personIdSpan') personIdSpanEl: ElementRef;

    @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
    @ContentChild(AccordionComponent) accordion: AccordionComponent;

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private navigationService: NavigationService,
        private financialAccountService: FinancialAccountService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.accountId = this.route.snapshot.paramMap.get('accountId');
        this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
        this.navigationService.setUserDataValue('idFinancialAccount', this.accountId);
    }

    ngOnInit(): void {
        this.clearServerMsg();
        this.createForm();
        this.navigationService.setTitle('Financial Account Detail');
        this.financialAccountService.getFinancialAccountDetail(this.accountId).subscribe((response) => {
            this.displayFinancialAcctResponse = response;
            if (this.displayFinancialAcctResponse.financialAccount) {
                const zip = this.displayFinancialAcctResponse.financialAccount.zip;
                this.financialAcctDetailForm.patchValue({
                    personId: this.displayFinancialAcctResponse.financialAccount.personId,
                    personName: this.displayFinancialAcctResponse.financialAccount.personName,
                    dedicated: this.displayFinancialAcctResponse.financialAccount.dedicated,
                    program: this.displayFinancialAcctResponse.financialAccount.program,
                    type: this.displayFinancialAcctResponse.financialAccount.type,
                    status: this.displayFinancialAcctResponse.financialAccount.status,
                    accountNumber: this.displayFinancialAcctResponse.financialAccount.accountNumber,
                    institutionName: this.displayFinancialAcctResponse.financialAccount.institutionName,
                    phone: FormUtils.formatPhoneNumber(this.displayFinancialAcctResponse.financialAccount.phone),
                    ext: this.displayFinancialAcctResponse.financialAccount.ext,
                    address1: this.displayFinancialAcctResponse.financialAccount.address1,
                    address2: this.displayFinancialAcctResponse.financialAccount.address2,
                    city: this.displayFinancialAcctResponse.financialAccount.city,
                    zip: zip ? (zip.includes('-') ? zip.split('-')[0] : '') : '',
                    zipExt: zip ? (zip.includes('-') ? zip.split('-')[1] : '') : '',
                    state: this.displayFinancialAcctResponse.financialAccount.state,
                    county: this.displayFinancialAcctResponse.financialAccount.county,
                    comments: this.displayFinancialAcctResponse.financialAccount.comments,
                });
            }
            this.determinePageMode();
            this.financialAcctDetailForm.setValidators([FinancialAccountDetailValidators.validationConstraints,
            FinancialAccountDetailValidators.saveConstraints(this.isNew),
            FinancialAccountDetailValidators.phoneNumberPattern,]);
            this.programChanged();
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
            this.navigationService.setUserDataValue('idFinancialAccount', this.accountId);

        });
    }

    log(e: boolean) {
        const btn = document.getElementsByClassName('btn btn-link');
        if (e) {
            btn[0].setAttribute('aria-expanded', 'true');
        } else {
            btn[0].setAttribute('aria-expanded', 'false');
        }
    }

    createForm() {
        this.financialAcctDetailForm = this.formBuilder.group(
            {
                personId: [{ value: '', disabled: true }],
                personName: [{ value: '', disabled: true }],
                dedicated: [{ value: false, disabled: true }],
                program: [{ value: '', disabled: true }],
                type: [{ value: '', disabled: true }],
                status: [{ value: '', disabled: true }],
                accountNumber: [{ value: '', disabled: true }],
                institutionName: [{ value: '', disabled: true }],
                phone: [{ value: '', disabled: true }],
                ext: [{ value: '', disabled: true }],
                address1: [{ value: '', disabled: true }],
                address2: [{ value: '', disabled: true }],
                city: [{ value: '', disabled: true }, DfpsCommonValidators.cityPattern],
                zip: [{ value: '', disabled: true }, DfpsCommonValidators.zipPattern],
                zipExt: [{ value: '', disabled: true }, DfpsCommonValidators.zipExtensionPattern],
                state: [{ value: '', disabled: true }],
                county: [{ value: '', disabled: true }],
                comments: [{ value: '', disabled: true }],
                formAction: [''],
                validate: [false]
            }
        );
    }

    validate() {
        this.financialAcctDetailForm.get('validate').setValue('true');
        this.resetValidationError();
        this.financialAcctDetailForm.controls.formAction.setValue('VALIDATE');
        this.clearServerMsg();
        const enableDisableFormCtrNames = ['personName', 'address1', 'address2', 'phone', 'ext',
            'city', 'zip', 'zipExt', 'county', 'institutionName', 'state'];
        if (this.validateFormGroup(this.financialAcctDetailForm)) {
            this.setRequestObj();
            this.displayFinancialAcctResponse.financialAccount.personName = '';
            this.financialAccountService
                .validateFinancialAccountDetail(this.displayFinancialAcctResponse.financialAccount)
                .subscribe((response) => {
                    this.financialAcctDetailForm.controls.personName.setValue(response.personName);
                    this.financialAcctDetailForm.controls.address1.setValue(response.address1);
                    this.financialAcctDetailForm.controls.address2.setValue(response.address2);
                    this.financialAcctDetailForm.controls.phone.setValue(response.phone);
                    this.financialAcctDetailForm.controls.ext.setValue(response.ext);
                    this.financialAcctDetailForm.controls.city.setValue(response.city);
                    this.financialAcctDetailForm.controls.zip.setValue(response.zip.slice(0, 5));
                    this.financialAcctDetailForm.controls.zipExt.setValue(response.zip.slice(6));
                    this.financialAcctDetailForm.controls.state.setValue(response.state);
                    this.financialAcctDetailForm.controls.county.setValue(response.county);
                    this.financialAcctDetailForm.controls.institutionName.setValue(response.institutionName);
                    this.isPersonIdValidated = true;
                    setTimeout(() => this.personIdSpanEl.nativeElement.focus(), 500);
                    if (this.displayFinancialAcctResponse.financialAccount.program === programs.CPS) {
                        this.showDedicated = true;
                    }
                    if (this.financialAcctDetailForm.controls.program.value === programs.CPS &&
                        this.financialAcctDetailForm.controls.type.value === 'CH') {
                        FormUtils.disableFormControlStatus(this.financialAcctDetailForm, enableDisableFormCtrNames);
                    } else {
                        FormUtils.enableFormControlStatus(this.financialAcctDetailForm, enableDisableFormCtrNames);
                    }

                });
        }
    }

    save() {
        this.clearServerMsg();
        this.financialAcctDetailForm.controls.formAction.setValue('SAVE');
        if (this.validateFormGroup(this.financialAcctDetailForm)) {
            switch (this.displayFinancialAcctResponse.pageMode) {
                case pageModes.EDIT:
                    this.displayFinancialAcctResponse.financialAccount.status = this.financialAcctDetailForm.controls.status.value;
                    this.displayFinancialAcctResponse.financialAccount.comments = this.financialAcctDetailForm.controls.comments.value;
                    this.displayFinancialAcctResponse.financialAccount.type = this.financialAcctDetailForm.controls.type.value;
                    this.displayFinancialAcctResponse.financialAccount.dedicated = this.financialAcctDetailForm.controls.dedicated.value;
                    this.financialAccountService
                        .saveFinancialAccountDetail(this.displayFinancialAcctResponse.financialAccount)
                        .subscribe((result) => {
                            if (result) {
                                this.doRoute();
                            }
                        });
                    break;
                case pageModes.NEW:
                    this.setRequestObj();
                    this.financialAccountService
                        .saveFinancialAccountDetail(this.displayFinancialAcctResponse.financialAccount)
                        .subscribe((result) => {
                            if (result) {
                                this.doRoute();
                            }
                        });
                    break;
            }
        }
    }

    determinePageMode() {
        const pageMode = this.displayFinancialAcctResponse.pageMode;
        if (pageMode === pageModes.VIEW || pageMode === pageModes.EDIT) {
            this.financialAcctDetailForm.disable();
            this.showDedicated = true;
            this.isView = true;
            if (pageMode === pageModes.EDIT) {
                this.isView = false;
                if (this.financialAcctDetailForm.controls.program.value === programs.CPS &&
                    this.financialAcctDetailForm.controls.type.value === 'CH') {
                    FormUtils.enableFormControlStatus(this.financialAcctDetailForm, ['status', 'comments', 'dedicated']);
                } else {
                    FormUtils.enableFormControlStatus(this.financialAcctDetailForm, [
                        'type',
                        'accountNumber',
                        'status',
                        'comments',
                        'dedicated',
                        'institutionName',
                        'phone',
                        'ext',
                        'address1',
                        'address2',
                        'city',
                        'zip',
                        'zipExt',
                        'state',
                        'county'
                    ]);
                    this.isShowValidate = true;
                }

            }
        } else if (pageMode === pageModes.NEW) {
            this.financialAcctDetailForm.enable();
            if (this.displayFinancialAcctResponse.programs.length) {
                this.financialAcctDetailForm.controls.program.setValue(
                    this.displayFinancialAcctResponse.programs[0].code
                );
                FormUtils.enableFormControlStatus(this.financialAcctDetailForm, ['program']);
                this.displayFinancialAcctResponse.financialAccount.program = this.displayFinancialAcctResponse.programs[0].code;
            }
            this.financialAcctDetailForm.controls.status.setValue(status.active);
            this.financialAcctDetailForm.controls.state.setValue(states.texas);
            this.financialAcctDetailForm.controls.dedicated.setValue(false);
            this.isView = false;
            this.isNew = true;
            this.isShowValidate = true;
        }
    }

    doRoute() {
        setTimeout(() => {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            const routeUrl = 'financial/financial-account/search';
            this.router.navigate([routeUrl], {
                queryParams: { using: this.financialAcctDetailForm.controls.personId.value },
            });
        }, 3000);
    }

    setRequestObj() {
        this.displayFinancialAcctResponse.financialAccount = this.financialAcctDetailForm.getRawValue();
        if (this.displayFinancialAcctResponse.programs.length === 1) {
            this.displayFinancialAcctResponse.financialAccount.program = this.displayFinancialAcctResponse.programs[0].code;
        }
        if (
            this.financialAcctDetailForm.controls.zipExt.value &&
            this.financialAcctDetailForm.controls.zipExt.value.trim().length > 0
        ) {
            this.displayFinancialAcctResponse.financialAccount.zip =
                this.displayFinancialAcctResponse.financialAccount.zip +
                '-' +
                this.financialAcctDetailForm.controls.zipExt.value.trim();
        }
    }

    resetValidationError() {
        this.financialAcctDetailForm.controls.personId.setErrors(null);
        this.financialAcctDetailForm.controls.type.setErrors(null);
        this.financialAcctDetailForm.controls.accountNumber.setErrors(null);
        this.financialAcctDetailForm.controls.institutionName.setErrors(null);
    }

    programChanged() {
        const currentType = this.financialAcctDetailForm.controls.type.value;
        if (this.financialAcctDetailForm.controls.program.value === programs.CPS) {
            this.financialAcctDetailForm.controls.type.setValue(this.displayFinancialAcctResponse.cpsTypes);
            this.financialAcctDetailForm.controls.type.setValue(currentType);
            if (this.isNew && !this.isValidated) {
                this.showDedicated = false;
            } else {
                this.showDedicated = true;
            }
        } else if (this.financialAcctDetailForm.controls.program.value === programs.APS) {
            this.resetValidationError();
            this.validateFormGroup(this.financialAcctDetailForm);
            this.financialAcctDetailForm.controls.type.setValue(this.displayFinancialAcctResponse.apsTypes);
            this.financialAcctDetailForm.controls.type.setValue(currentType);
            this.showDedicated = false;
        } else {
            this.showDedicated = false;
        }
    }

    updateAdressFields(address: Address) {
        if (address && address.isAddressAccepted) {
            this.financialAcctDetailForm.patchValue({
                address1: address.street1,
                address2: address.street2,
                city: address.city,
                county: address.countyCode,
                state: address.state,
                zip: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[0] : '') : '',
                zipExt: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[1] : '') : '',
            });
        }
    }

    validateAdressFields() {
        const address: Address = {
            street1: this.financialAcctDetailForm.controls.address1.value,
            street2: this.financialAcctDetailForm.controls.address2.value,
            city: this.financialAcctDetailForm.controls.city.value,
            county: this.financialAcctDetailForm.controls.county.value,
            state: this.financialAcctDetailForm.controls.state.value,
            zip: this.financialAcctDetailForm.controls.zip.value,
            extension: this.financialAcctDetailForm.controls.zipExt.value,
        };
        this.dfpsAddressValidator.validate(address);
    }

    clearServerMsg() {
        this.store.dispatch(ERROR_RESET(null));
        this.store.dispatch(SUCCESS_RESET(null));
    }

    ngOnDestroy() {
        this.clearServerMsg();
    }
}
