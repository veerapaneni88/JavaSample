import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import {
    SearchStandardDisplayRes,
    StandardSearchResult,
    WaiverVarianceDetailRes,
} from '@case/model/waiverVariance';
import { WaiverVarianceService } from '@case/service/waiver-variance.service';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
    DataTable,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    DropDown,
    NavigationService,
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { SearchStandardValidator } from './search-standard.validator';

@Component({
    selector: 'search-standard',
    templateUrl: './search-standard.component.html',
    styleUrls: ['./search-standard.component.css'],
})
export class SearchStandardComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    constructor(
        private navigationService: NavigationService,
        private formBuilder: FormBuilder,
        private waiverVarianceService: WaiverVarianceService,
        private searchService: SearchService,
        private cookieService: CookieService,
        private modalService: BsModalService,
        private router: Router,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }
    searchStandardForm: FormGroup;
    searchCategoryList: DropDown[];
    searchResultTable: DataTable;
    tableColumn: any[];
    displaySearchResponseObs: Observable<SearchStandardDisplayRes>;
    displaySearchResponse: SearchStandardDisplayRes;
    selectStandardRecord: StandardSearchResult;
    waiverVarianceDetailRes: WaiverVarianceDetailRes;
    displaySearchDataTable: boolean;

    ngOnInit() {
        this.navigationService.setTitle('Standard Search');
        this.createForm();
        this.displaySearchDataTable = false;
        this.createSearchResultsDisplayTable();
        this.displaySearchResponseObs = this.waiverVarianceService.displayStandardSearch();
        this.displaySearchResponseObs.subscribe((displayStandardSearch) => {
            if (displayStandardSearch) {
                this.displaySearchResponse = displayStandardSearch;
            }
        });
    }

    doSearch() {
        this.displaySearchDataTable = false;
        if (this.validateFormGroup(this.searchStandardForm)) {
            this.searchStandard();
        }
    }

    searchStandard() {
        const standardSearchReq = {
            keyword: this.searchStandardForm.value.keyword,
            category: this.searchStandardForm.value.category,
        };
        this.waiverVarianceService.searchStandard(standardSearchReq).subscribe((response) => {
            if (response) {
                this.displaySearchResults(response);
            }
        });
    }

    createSearchResultsDisplayTable() {
        this.tableColumn = [
            { field: 'number', header: 'Standard', width: 100 },
            { field: 'weightDecode', header: 'Weight', isHidden: false, width: 100 },
            { field: 'sds', header: 'Description', isHidden: false, width: 300 },
        ];
        this.searchResultTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: true,
            isPaginator: true,
        };
    }

    displaySearchResults(standardSearchResponse: StandardSearchResult[]) {
        this.displaySearchDataTable = true;
        this.searchResultTable.tableBody = standardSearchResponse;
    }

    createForm() {
        this.searchStandardForm = this.formBuilder.group(
            {
                keyword: [''],
                category: [''],
                msgErrDisplay: [''],
            },
            {
                validators: [SearchStandardValidator.validateForEmptySearchParam],
            }
        );
    }

    getSelectedStandardRecord(event) {
        this.selectStandardRecord = event;
    }

    doContinue() {
        if (this.selectStandardRecord) {
            this.waiverVarianceDetailRes = this.searchService.getFormContent();
            this.searchService.setselectedSearchStandard(this.selectStandardRecord);
            const url = this.searchService.getReturnUrl()
                ? this.searchService.getReturnUrl()
                : this.cookieService.get('search_return_url');
            if (!this.searchService.getFormData() && this.cookieService.get('form_data')) {
                this.searchService.setFormData(JSON.parse(this.cookieService.get('form_data')));
            }
            this.searchService.setSelectedOrg(null);
            this.cookieService.delete('search_return_url');
            this.cookieService.delete('form_data');

            if (url.includes('using')) {
                const urlArray: any[] = url.split('?')
                const urlParam: string = urlArray[1].split('using=')[1].trim();
                this.router.navigate([urlArray[0]], { queryParams: { using: urlParam } });
            } else {
                this.router.navigate([url]);
            }
        } else {
            this.showErrorModal();
        }
    }

    showErrorModal() {
        const initialState = {
            title: 'Search Standard',
            message: 'Please select atleast one row to perform this action.',
            showCancel: false,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
            class: 'modal-md modal-dialog-centered',
            initialState,
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
            if (result === true) {
                // do nothing;
            }
        });
    }
}
