import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
    DataTable,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    DfpsCommonValidators,
    DropDown,
    FormUtils,
    NavigationService,
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { County, FinancialAccountDisplayRes, FinancialAccountSearch, pageModes } from '../model/FinancialAccount';
import { FinancialAccountService } from '../service/financial-account.service';
import { Store } from '@ngrx/store';
import { FinancialAccountSearchValidators } from './financial-account-search.validator';

@Component({
    selector: 'financial-account-search',
    templateUrl: './financial-account-search.component.html',
})
export class FinancialAccountSearchComponent extends DfpsFormValidationDirective implements OnInit {
    financialAccountSearchForm: FormGroup;
    financialAcctSearchRes: FinancialAccountDisplayRes;
    financialAccountSearch: FinancialAccountSearch[];
    personIDTableColumn: any[];
    accountTableColumn: any[];
    financialAcctSearchTable: DataTable;
    isTableInfo = false;
    isSearchDisabled = false;
    isProgramAPS = false;
    isEditMode = false;
    selectedRegion = '';
    counties: DropDown[] = [];
    accountType: DropDown[] = [];
    readonly accountTypeProgram = ['CH', 'SV', 'TF', 'XX'];
    personId = 0;
    readonly DEFAULT_PAGE_SIZE = 50;
    searchButtonClicked = false;
    @ViewChild('results') resultsEle: ElementRef;

    constructor(
        private formBuilder: FormBuilder,
        private router: Router,
        private route: ActivatedRoute,
        private financialAccountService: FinancialAccountService,
        private navigationService: NavigationService,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.navigationService.setUserDataValue('firstLevelTab', 'Financial');
        this.navigationService.setUserDataValue('idFinancialAccount', '');
        this.navigationService.setUserDataValue('accountNumber', '');
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Financial Account Search');
        this.createForm();
        this.getInitialData();
    }

    getInitialData() {
        FormUtils.disableFormControlStatus(this.financialAccountSearchForm, ['county']);
        this.financialAccountService.displayFinancialAccountSearch().subscribe((res) => {
            this.financialAcctSearchRes = res;
            this.financialAccountSearchForm.controls.program.setValue(this.financialAcctSearchRes.program);
            if (this.financialAcctSearchRes.program === 'APS') {
                this.isProgramAPS = true;
                this.financialAccountSearchForm.controls.region.setValue(this.financialAcctSearchRes.region);
                FormUtils.enableFormControlStatus(this.financialAccountSearchForm, ['county', 'region']);
                this.accountType = this.financialAcctSearchRes.accountTypes;
            } else if (this.financialAcctSearchRes.program === 'CPS') {
                this.accountType = this.filterAccountTypeBasedCPSProgram();
            }
            this.counties = this.financialAcctSearchRes.counties;
            this.loadFinancialAccount();
        });
    }

    onProgramChange(event) {
        const value = event.target.value.split(': ')[1];
        this.isProgramAPS = false;
        FormUtils.disableFormControlStatus(this.financialAccountSearchForm, ['county', 'region']);
        this.accountType = this.financialAcctSearchRes.accountTypes;
        this.financialAccountSearchForm.controls.county.setValue('');

        if (value === 'CPS') {
            this.isSearchDisabled = false;
            this.financialAccountSearchForm.controls.region.setValue('');
            this.accountType = this.filterAccountTypeBasedCPSProgram();
        } else if (value === 'APS') {
            this.isProgramAPS = true;
            this.isSearchDisabled = false;
            FormUtils.enableFormControlStatus(this.financialAccountSearchForm, ['county', 'region']);
        } else {
            this.isSearchDisabled = true;
        }
        const accountTypeCtrl = this.financialAccountSearchForm.controls.accountType;
        if (this.accountType.filter((dropDown: DropDown) => dropDown.code === accountTypeCtrl.value).length === 0) {
            this.financialAccountSearchForm.controls.accountType.setValue('');
        }
    }

    filterAccountTypeBasedCPSProgram() {
        return (this.accountType = this.financialAcctSearchRes.accountTypes.filter((data) => {
            return this.accountTypeProgram.includes(data.code);
        }));
    }

    onRegionChange(event) {
        this.selectedRegion = event.target.value.split(': ')[1];
        this.regionCounty(this.selectedRegion);
    }

    regionCounty(selectedRegion: string) {
        if (this.isProgramAPS) {
            this.financialAccountSearchForm.controls.county.setValue('');
            this.counties = this.financialAcctSearchRes.regionCounties.filter(
                (county: County) => county.regionCode === selectedRegion
            );
            this.counties.push({ code: '', decode: '' });
        }
    }

    createForm() {
        this.financialAccountSearchForm = this.formBuilder.group({
            personId: ['', [DfpsCommonValidators.validateMaxId]],
            accountNumber: ['', [FinancialAccountSearchValidators.validateAccountNumber]],
            accountType: ['XX'],
            program: ['', [Validators.required]],
            region: [{ value: '', disabled: true }],
            county: [{ value: '', disabled: true }],
            status: [''],
        });

        this.setupTable();
    }

    setupTable() {
        this.personIDTableColumn = [
            {
                field: 'personId',
                header: 'Person ID',
                isLink: true,
                url: '/financial/financial-account/header/:financialAccountId',
                urlParams: ['financialAccountId'],
                width: 125,
            },
            { field: 'personName', header: 'Name', width: 200 },
            { field: 'county', header: 'County', width: 100 },
            { field: 'accountType', header: 'Type', width: 150 },
            { field: 'status', header: 'Status', width: 100 },
            {
                field: 'accountNumber',
                header: 'Account Number',
                isLink: true,
                url: '/financial/financial-account/register/:financialAccountId',
                urlParams: ['financialAccountId'],
                width: 100,
            },
            { field: 'region', header: 'REG', width: 100 },
            { field: 'transactionCount', header: 'TC', width: 100 },
            { field: 'accountBalance', header: 'Balance', isCurrency: true, width: 100 },
            { field: 'asOf', header: 'As Of', width: 100 },
        ];
        this.accountTableColumn = [
            {
                field: 'accountNumber',
                header: 'Account Number',
                handleClick: true,
                url: '/financial/financial-account/regional/:accountNumber',
                urlParams: ['accountNumber'],
                width: 120,
            },
            { field: 'finInstitutionName', header: 'Name', width: 280 },
            { field: 'accountType', header: 'Type', width: 100 },
            { field: 'status', header: 'Status', width: 100 },
            { field: 'accountBalance', header: 'Balance', width: 120, isCurrency: true },
            { field: 'asOf', header: 'As Of', width: 80 },
            {
                field: 'reconBalance',
                header: 'Recon Bal',
                isCurrency: true,
                width: 120,
            },
        ];
    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
      }

    loadFinancialAccount() {
        let personId = 0;
        this.route.queryParams.subscribe((params) => {
            personId = params.using;
            this.financialAccountSearchForm.controls.personId.setValue(personId);
        });

        if (personId) {
            this.searchFinancialAccount();
        }
    }

    searchFinancialAccount() {
        const account = this.financialAccountSearchForm.get('accountNumber').value;
        const status = this.financialAccountSearchForm.get('status').value;
        const region = this.financialAccountSearchForm.get('region').value;
        const program = this.financialAccountSearchForm.get('program').value;
        const personid = this.financialAccountSearchForm.get('personId').value;

        if (program === 'CPS' && account && personid) {
            this.showErrorModal('Please Enter Person Id or Account number for this Search');
            return;
        }
        if (this.isProgramAPS && !region) {
            this.showErrorModal('You must select Region to perform this search');
            return;
        }
        if (account && program === 'CPS' && !status) {
            this.showErrorModal('You must select Status to perform this search');
            return;
        }

        const search = this.financialAccountSearchForm.getRawValue();
        if (this.validateFormGroup(this.financialAccountSearchForm)) {
            this.searchButtonClicked = true;
            this.financialAccountService.postFinancialAccountSearch(search).subscribe((res) => {
                this.financialAccountSearch = res.financialAccountSearch;
                this.setupResults(this.financialAccountSearch);
                this.isTableInfo = true;
                this.pageModeDisplay(res.pageMode);
                if (this.searchButtonClicked) {
                    setTimeout(() => {
                      this.resultsEle.nativeElement.focus();
                    }, 0);
                  }
            });
        }
    }

    setupResults(searchResponse) {
        this.financialAcctSearchTable = {
            tableColumn: this.accountTableColumn,
            isPaginator: true,
        };
        const displayPersonTable = searchResponse.filter((result) => result.personId > 0).length > 0;
        if (displayPersonTable) {
            this.financialAcctSearchTable.tableColumn = this.personIDTableColumn;
        }
        this.financialAcctSearchTable.tableBody = searchResponse;
    }

    pageModeDisplay(pageMode) {
        if (pageMode === pageModes.EDIT && !this.financialAccountSearchForm.get('accountNumber').value) {
            this.isEditMode = true;
        } else {
            this.isEditMode = false;
        }
    }

    showErrorModal(errorMessage: string) {
        const initialState = { message: errorMessage, title: 'Financial Account Search' };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
          document.getElementById('financialAccountSearch').focus();
        });
    }

    addFinancialAccount() {
        this.router.navigate(['/financial/financial-account/header' + '/0']);
    }

    handleRouting(event) {
        if (event) {
            const status: any = this.financialAccountSearchForm.get('status').value;
            this.router.navigate([event.link + '/' + status]);
        }
    }
    clear() {
        window.location.reload();
    }
}
