import { CookieService } from 'ngx-cookie-service';
import { Component, OnDestroy, OnInit, ElementRef, ViewChild, Inject } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { OrgSearchRequest, OrgSearchResponse } from '@shared/model/OrgSearch';
import { SearchService } from '@shared/service/search.service';
import { 
  DataTable, 
  DfpsConfirmComponent, 
  DfpsFormValidationDirective, 
  DirtyCheck, 
  ENVIRONMENT_SETTINGS, 
  NavigationService 
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { OrgSearchValidators } from './org-search-validator';

@Component({
  selector: 'org-search',
  templateUrl: './org-search.component.html',
})
export class OrgSearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  orgSearchForm: FormGroup;

  OrgDataTable: DataTable;
  tableColumn: any[];
  isOrgInfo = false;
  helpFileName: string;

  searchPayload: OrgSearchRequest;
  selectedOrg: OrgSearchResponse;
  modalSubscription: Subscription;

  searchButtonClicked = false;
  @ViewChild('results') resultsEle: ElementRef;

  constructor(
    private cookieService: CookieService,
    private navigationService: NavigationService,
    private searchService: SearchService,
    private modalService: BsModalService,
    private router: Router, 
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Organization Search');
    const helpUrl = this.environmentSettings.helpUrl;
    this.helpFileName = helpUrl.replace('Page_Descriptions/Finance/', 'Impact_Phase_2_Help.htm');

    this.orgSearchForm = new FormGroup({
      ein: new FormControl('', [OrgSearchValidators.validateEIN]),
      otherName: new FormControl(''),
      legalName: new FormControl(''),
      atleastOneFieldValid: new FormControl('')
    },
    {
      validators: [
      OrgSearchValidators.validateAtleastOneFieldRequired()
    ]}
    );
  }

  ngOnDestroy(): void {
    if (this.modalSubscription) {
      this.modalSubscription.unsubscribe();
    }
  }

  search() {
    if (this.validateFormGroup(this.orgSearchForm)) {
      this.isOrgInfo = false;
      this.searchPayload = this.orgSearchForm.value;
      this.searchButtonClicked = true;
      this.searchService.searchOrganization(this.searchPayload).subscribe((orgRes: OrgSearchResponse[]) => {
        this.orgInfoDataTable(orgRes);
        if (this.searchButtonClicked) {
          setTimeout(() => {
            this.resultsEle.nativeElement.focus();
          }, 0);
        }
      });
    }

  }
  orgInfoDataTable(displayResponse) {
    this.tableColumn = [
      { field: 'einId', header: 'EIN', isHidden: false, width: 75 },
      { field: 'legalName', header: 'Legal Name', isHidden: false, width: 200 },
      { field: 'otherName', header: 'Other Name', isHidden: false, width: 100 },
      { field: 'status', header: 'Status', isHidden: false, width: 50 },
      { field: 'addressLn1', header: 'Business Address', isHidden: false, width: 100 },
      { field: 'addressCity', header: 'City', isHidden: false, width: 75 },
      { field: 'county', header: 'County', isHidden: false, width: 75 }
    ];
    this.OrgDataTable = {
      tableColumn: this.tableColumn,
      isSingleSelect: true,
      isPaginator: false
    };
    if (displayResponse) {
      this.isOrgInfo = true;
      this.OrgDataTable.tableBody = displayResponse;
    }
  }

  searchButtonResultOnBlur() {
    this.searchButtonClicked = false;
  }

  getSelectedOrgSearch(event) {
    this.selectedOrg = event;
  }

  continue() {
    if (this.selectedOrg) {
      if (this.selectedOrg.status === 'Active') {
        this.searchService.setSelectedOrg(this.selectedOrg);
        const url = this.searchService.getReturnUrl() ? this.searchService.getReturnUrl()
                : this.cookieService.get('search_return_url');
            if (!this.searchService.getFormData() && this.cookieService.get('form_data')) {
                this.searchService.setFormData(JSON.parse(this.cookieService.get('form_data')));
            }
            this.cookieService.delete('search_return_url');
            this.cookieService.delete('form_data');
            this.router.navigate([url]);
      } else {
        this.showInActiveModal();
      }
    } else {
      this.showErrorModal();
    }
  }

  cancel() {
    this.router.navigate([this.searchService.getReturnUrl()]);
  }

  clear() {
    this.cookieService.set('search_return_url', this.searchService.getReturnUrl(), 10000, '/', undefined, undefined, 'Lax');
    this.cookieService.set('form_data', JSON.stringify((this.searchService.getFormData()) as FormGroup),
      10000, '/', undefined, undefined, 'Lax');
    window.location.reload();
  }

  showErrorModal() {
    const initialState = {
      message: 'Please select at least one row to perform this action.',
      title: 'Organization Search',
    };
    this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
  }

  showInActiveModal() {
    const initialState = {
      message: 'This organization status is inactive. Do you want to proceed?',
      title: 'Organization Search',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.searchService.setSelectedOrg(this.selectedOrg);
        this.router.navigate([this.searchService.getReturnUrl()]);
      }
    });
  }


}
