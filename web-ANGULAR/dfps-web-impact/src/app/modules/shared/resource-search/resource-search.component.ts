import { Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { ChildDropDown, DisplayResourceSearchResponse, ResourceSearchResponse } from '@shared/model/ResourceSearch';
import { SearchService } from '@shared/service/search.service';
import { HelpService } from 'app/common/impact-help.service';
import {
    AuthService, DataTable,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    DropDown, ENVIRONMENT_SETTINGS,
    ERROR_RESET,
    NavigationService,
    SET,
    SUCCESS_RESET, UserInfo
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { Subscription } from 'rxjs';
import { ResourceSearchValidators } from './resource-search-validator';

@Component({
    selector: 'resource-search',
    templateUrl: './resource-search.component.html',
    styleUrls: ['./resource-search.component.css'],
})
export class ResourceSearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    constructor(
        private cookieService: CookieService,
        private searchService: SearchService,
        private route: ActivatedRoute,
        private router: Router,
        private formBuilder: FormBuilder,
        private modalService: BsModalService,
        private navigationService: NavigationService,
        private authService: AuthService,
        private helpService: HelpService,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }
    facilityTypes: DropDown[];
    facilityTypeList: DropDown[];
    facilityTypeOtherList: DropDown[];
    areaCategoryList: DropDown[];
    areaServiceList: ChildDropDown[];
    displayResponse: DisplayResourceSearchResponse;
    resourceSearchForm: FormGroup;
    resourceStatusData: any;
    tableBody: any[];
    tableColumn: any[];
    resourceSearchDataTable: DataTable;
    showResourceSearchTable = false;
    selectedResource: ResourceSearchResponse;
    searchClicked = false;
    source = '';
    isSearchCriteriaWarning = false;
    @ViewChild('results') resultsEle: ElementRef;
    searchButtonClicked = false;
    @ViewChild('searchResults') searchResultsEle: ElementRef;
    userInfo: UserInfo;
    userInfoSubscription: Subscription;
    showCancelBtn = false;

    ngOnInit(): void {
        this.navigationService.setTitle('Resource Search');
        this.helpService.loadHelp('Search');
        this.clearServerMsg();
        this.source = this.route.snapshot.paramMap.get('source');
        this.initializeDataTable();
        this.searchService.searchResourceDisplay().subscribe((response) => {
            this.displayResponse = response;
            this.facilityTypeList = response.facilityTypes;
            this.facilityTypeOtherList = response.facilityTypesOther;
            this.areaCategoryList = response.categories;
            this.areaServiceList = response.services;
            if (this.source !== 'non-sscc') {
                this.displayResponse.numberTypes = this.displayResponse.numberTypes.filter((el) => {
                    return el.code === 'PHN' || el.code === 'RSC';
                });
                this.displayResponse.resourceTypes = this.displayResponse.resourceTypes.filter((el) => {
                    return el.code === '06';
                });
            }
            if (this.searchService.getReturnUrl() === '/search/home-search/add-kin') {
                this.filterKinResourceTypes();
                this.filterKinServiceTypes();
                this.setKinHomeSearchDefaults();
            }
            
            //const lettersUrl:string = 'case/letters/investigation-letters';
            const letterUrl = this.searchService.getReturnUrl() ? this.searchService.getReturnUrl()
                : this.cookieService.get('search_return_url');            
            if(letterUrl.includes('investigation-letters')){
                this.showCancelBtn = true;
            }
        });
        this.createForm();
        this.resourceStatusData = [
            { label: 'Active', value: 1 },
            { label: 'Inactive', value: 2 },
            { label: 'All', value: null },
        ];
        this.resourceSearchForm.controls.resourceStatus.setValue(1);
        this.resourceSearchForm.controls.locationState.setValue('TX');
        this.resourceSearchForm.controls.areaState.setValue('TX');
        if (this.source !== 'non-sscc') {
            this.resourceSearchForm.controls.resourceType.setValue('06');
        }
    }

    ngOnDestroy() {
        this.clearServerMsg();
    }

    numberTypeChanged() {
        this.idNumberValue.setErrors(null);
        this.isSearchCriteriaWarning = false;
        if (this.searchClicked) {
            this.validateFormControl(this.idNumberValue);
            this.checkMinReqField();
        }
        if (!this.idNumberType.value || this.idNumberType.value.length === 0) {
            this.enableForm();
            this.resourceSearchForm.get('idNumberValue').setValue('');
        } else {
            this.disableForm();
        }
    }

    resourceTypeChanged() {
        this.isSearchCriteriaWarning = false;
        this.disableFacilityInformation();
        this.displayResponse.categories = this.areaCategoryList;
        if (this.resourceType && this.resourceType.value.length > 0) {
            switch (this.resourceType.value) {
                case '01':
                    this.displayResponse.categories = this.displayResponse.categoriesProvider;
                    this.disableFacilityInformation();
                    break;
                case '02':
                    this.displayResponse.categories = this.displayResponse.categoriesLawEnforcement;
                    this.disableFacilityInformation();
                    break;
                case '03':
                    this.displayResponse.categories = this.displayResponse.categoriesHotline;
                    this.disableFacilityInformation();
                    break;
                case '04':
                    this.displayResponse.categories = this.displayResponse.categoriesSchool;
                    this.disableFacilityInformation();
                    break;
                case '05':
                    this.enableFacilityInformation();
                    this.facilityTypes = this.facilityTypeList;
                    this.displayResponse.categories = this.displayResponse.categoriesMhmr;
                    break;
                case '06':
                    this.enableFacilityInformation();
                    this.facilityTypes = this.facilityTypeOtherList;
                    this.displayResponse.categories = this.displayResponse.categoriesOther;
                    break;
                case '07':
                    this.disableFacilityInformation();
                    this.displayResponse.categories = this.displayResponse.categoriesSscc;
                    break;
            }
            this.resourceType.setErrors(null);
            this.validateFormControl(this.resourceType);
        }

        this.resetServicesByAreaClientCategoryAndService();
        if (this.searchClicked) {
            this.checkMinReqField();
        }
    }

    areaServiceChanged() {
        this.isSearchCriteriaWarning = false;
        if (this.areaService.value && this.areaService.value.length > 0) {
            this.resourceType.setErrors(null);
            this.validateFormControl(this.resourceType);
        }
        if (this.searchClicked) {
            this.checkMinReqField();
        }
    }

    facilityTypeChanged() {
        if (this.resourceType.value === '06' && this.facilityType.value && this.facilityType.value.length > 0) {
            this.facilityServiceLevel.setValue('');
            this.facilityServiceType.setValue('');
            this.facilityServiceLevel.enable();
            if (this.facilityType.value === '80') {
                this.facilityServiceType.enable();
            } else {
                this.facilityServiceType.disable();
            }
        } else {
            this.disableFacilityServiceType();
            this.disableFacilityServiceLevel();
        }
    }

    locationResourceNameChanged() {
        this.isSearchCriteriaWarning = false;
        if (this.locationResourceName.value && this.locationResourceName.value.length > 0) {
            this.resourceType.setErrors(null);
            this.validateFormControl(this.resourceType);
        }
        if (this.searchClicked) {
            this.checkMinReqField();
        }
    }

    checkMinReqField() {
        if (
            (!this.resourceType.value || this.resourceType.value.length === 0) &&
            (!this.locationResourceName.value || this.locationResourceName.value.length === 0) &&
            (!this.idNumberType.value || this.idNumberType.value.length === 0) &&
            (!this.areaService.value || this.areaService.value.length === 0)
        ) {
            this.resourceType.setErrors({ resourceType: true });
            this.validateFormControl(this.resourceType);
            this.idNumberValue.setErrors(null);
            this.validateFormControl(this.idNumberValue);
        }
        if (
            (this.resourceType.value && this.resourceType.value.length > 0) ||
            (this.locationResourceName.value && this.locationResourceName.value.length > 0) ||
            (this.areaService.value && this.areaService.value.length > 0)
        ) {
            this.idNumberValue.setErrors(null);
            this.validateFormControl(this.idNumberValue);
        }
        if (
            (!this.resourceType.value || this.resourceType.value.length === 0) &&
            (!this.locationResourceName.value || this.locationResourceName.value.length === 0) &&
            (!this.areaService.value || this.areaService.value.length === 0) &&
            this.idNumberType.value &&
            this.idNumberType.value.length > 0 &&
            (!this.idNumberValue.value || this.idNumberType.value.length === 0)
        ) {
            this.resourceType.setErrors(null);
            this.validateFormControl(this.resourceType);
            this.idNumberValue.setErrors({
                required: {
                    customFieldName: 'Number Type Value',
                },
            });
            this.validateFormControl(this.idNumberValue);
        }
    }

    categoryChanged() {
        const selctedCategory = this.areaCategory.value;
        this.areaServiceList = this.displayResponse.services.filter(serv => serv.parentCode.split(',').includes(selctedCategory));
    }

    createForm() {
        this.resourceSearchForm = this.formBuilder.group(
            {
                idNumberType: [''],
                idNumberValue: [''],
                resourceType: [{ value: '', disabled: false }],
                resourceStatus: [{ label: 'Active', value: 1 }],
                resourceInvJurisdiction: [{ value: '', disabled: false }],
                resourceContractStatus: [{ value: '', disabled: false }],
                facilityType: [{ value: '', disabled: true }],
                facilityServiceType: [{ value: '', disabled: true }],
                facilityServiceLevel: [{ value: '', disabled: true }],
                locationResourceName: [
                    { value: '', disabled: false },
                    ResourceSearchValidators.resourceSearchResourceName,
                ],
                locationStreetAddress: [{ value: '', disabled: false }],
                locationCity: [{ value: '', disabled: false }, ResourceSearchValidators.resourceSearchCityAlphaPattern],
                locationState: [{ value: '', disabled: false }],
                locationZipCode: [{ value: '', disabled: false }, ResourceSearchValidators.zipPattern('Zip Code')],
                locationZipCodeExt: [
                    { value: '', disabled: false },
                    ResourceSearchValidators.zipExtensionPattern('Zip Code Extension'),
                ],
                locationCounty: [{ value: '', disabled: false }],
                areaCategory: [{ value: '', disabled: false }],
                areaService: [{ value: '', disabled: false }],
                areaState: [{ value: '', disabled: false }],
                areaRegion: [{ value: '', disabled: false }],
                areaCounty: [{ value: '', disabled: false }],
                areaAge: [{ value: '', disabled: false }, ResourceSearchValidators.resourceSearchAge],
                areaCharacteristics: [{ value: '', disabled: false }],
                areaGender: [{ value: '', disabled: false }],
                languageSpoken: [{ value: '', disabled: false }],
            },
            {
                validators: [ResourceSearchValidators.validateNumberType],
            }
        );
    }
    get idNumberType(): any {
        return this.resourceSearchForm.get('idNumberType');
    }
    get idNumberValue(): any {
        return this.resourceSearchForm.get('idNumberValue');
    }
    get resourceType(): any {
        return this.resourceSearchForm.get('resourceType');
    }
    get resourceStatus(): any {
        return this.resourceSearchForm.get('resourceStatus');
    }
    get resourceInvJurisdiction(): any {
        return this.resourceSearchForm.get('resourceInvJurisdiction');
    }
    get resourceContractStatus(): any {
        return this.resourceSearchForm.get('resourceContractStatus');
    }
    get facilityType(): any {
        return this.resourceSearchForm.get('facilityType');
    }
    get facilityServiceType(): any {
        return this.resourceSearchForm.get('facilityServiceType');
    }
    get facilityServiceLevel(): any {
        return this.resourceSearchForm.get('facilityServiceLevel');
    }
    get locationResourceName(): any {
        return this.resourceSearchForm.get('locationResourceName');
    }
    get locationStreetAddress(): any {
        return this.resourceSearchForm.get('locationStreetAddress');
    }
    get locationCity(): any {
        return this.resourceSearchForm.get('locationCity');
    }
    get locationState(): any {
        return this.resourceSearchForm.get('locationState');
    }
    get locationZipCode(): any {
        return this.resourceSearchForm.get('locationZipCode');
    }
    get locationZipCodeExt(): any {
        return this.resourceSearchForm.get('locationZipCodeExt');
    }
    get locationCounty(): any {
        return this.resourceSearchForm.get('locationCounty');
    }
    get areaCategory(): any {
        return this.resourceSearchForm.get('areaCategory');
    }
    get areaService(): any {
        return this.resourceSearchForm.get('areaService');
    }
    get areaState(): any {
        return this.resourceSearchForm.get('areaState');
    }
    get areaRegion(): any {
        return this.resourceSearchForm.get('areaRegion');
    }
    get areaCounty(): any {
        return this.resourceSearchForm.get('areaCounty');
    }
    get areaAge(): any {
        return this.resourceSearchForm.get('areaAge');
    }
    get areaCharacteristics(): any {
        return this.resourceSearchForm.get('areaCharacteristics');
    }
    get areaGender(): any {
        return this.resourceSearchForm.get('areaGender');
    }
    get languageSpoken(): any {
        return this.resourceSearchForm.get('languageSpoken');
    }
    get resourceSearchButtonFromControl(): any {
        return this.resourceSearchForm.get('resourceSearchButtonFromControl');
    }
    set resourceSearchButtonFromControl(val) {
        this.resourceSearchButtonFromControl.setValue(val);
    }
    resetResourceTypeStatus() {
        this.resourceType.setValue('');
        this.resourceSearchForm.controls.resourceInvJurisdiction.setValue('');
        this.resourceSearchForm.controls.resourceContractStatus.setValue('');
    }

    resetServicesByAreaClientCategoryAndService() {
        this.areaCategory.setValue('');
        this.areaService.setValue('');
    }

    disableFacilityInformation() {
        this.disableFacilityType();
        this.disableFacilityServiceType();
        this.disableFacilityServiceLevel();
    }

    enableFacilityInformation() {
        this.enableFacilityType();
        this.disableFacilityServiceLevel();
        this.disableFacilityServiceType();
    }

    enableFacilityType() {
        this.facilityType.setValue('');
        this.facilityType.enable();
    }

    disableFacilityType() {
        this.facilityType.setValue('');
        this.facilityType.disable();
    }

    disableFacilityServiceType() {
        this.facilityServiceType.setValue('');
        this.facilityServiceType.disable();
    }

    disableFacilityServiceLevel() {
        this.facilityServiceLevel.setValue('');
        this.facilityServiceLevel.disable();
    }

    enableForm() {
        Object.entries(this.resourceSearchForm.controls).forEach((el) => {
            if (el[0] !== 'facilityType' && el[0] !== 'facilityServiceType' && el[0] !== 'facilityServiceLevel') {
                el[1].enable();
            }
        });
    }

    disableForm() {
        Object.entries(this.resourceSearchForm.controls).forEach((el) => {
            if (el[0] !== 'idNumberType' && el[0] !== 'idNumberValue') {
                el[1].disable();
                if (el[0] !== 'resourceStatus') {
                    el[1].setValue('');
                }
            }
        });
    }

    searchResource() {
        this.clearServerMsg();
        this.showResourceSearchTable = false;
        this.isSearchCriteriaWarning = false;
        this.searchClicked = true;
        this.checkMinReqField();
        if (this.validateFormGroup(this.resourceSearchForm)) {
            this.loadTableData();
        }
    }

    initializeDataTable() {
        this.tableColumn = [
            { field: 'name', header: 'Resource Name', sortable: true, isHidden: false, width: 225 },
            { field: 'resourceId', header: 'Resource ID', sortable: true, isHidden: false, width: 125 },
            { field: 'status', header: 'Status', isHidden: false, width: 100 },
            { field: 'contractedCare', header: 'C', isTick: true, isHidden: false, width: 75 },
            { field: 'type', header: 'Resource Type', isHidden: false, width: 150 },
            { field: 'investigJurisdiction', header: 'Investigation Jurisdiction', isHidden: false, width: 110 },
            { field: 'facilityType', header: 'Facility Type', isHidden: false, width: 75 },
            { field: 'addressStreetLine1', header: 'Address', isHidden: false, width: 250 },
            { field: 'city', header: 'City', isHidden: false, width: 200 },
            { field: 'county', header: 'County', isHidden: false, width: 100 },
            { field: 'phone', header: 'Phone', isPhoneNumber: true, isHidden: false, width: 120 },
            { field: 'phoneExt', header: 'Ext', isHidden: false, width: 75 },
        ];
        this.resourceSearchDataTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: true,
            isPaginator: false,
        };
    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
    }

    loadTableData() {
        this.searchButtonClicked = true;
        this.searchService.searchResource(this.resourceSearchForm.value).subscribe((response: any) => {
            if (response.totalElements === 1000) {
                this.isSearchCriteriaWarning = true;
                setTimeout(() => {
                    this.resultsEle.nativeElement.focus();
                }, 500);
            }
            this.resourceSearchDataTable = {
                tableBody: response.resourceSearchResults,
                tableColumn: this.tableColumn,
                isSingleSelect: true,
                isPaginator: true,
                totalRows: response.totalElements,
            };
            this.showResourceSearchTable = true;
            if (this.searchButtonClicked) {
                setTimeout(() => {
                    this.searchResultsEle.nativeElement.focus();
                }, 0);
            }
        });
    }

    setSelectedResource(event) {
        this.selectedResource = event;

    }

    continue() {
        if (this.selectedResource) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            const url = this.searchService.getReturnUrl() ? this.searchService.getReturnUrl()
                : this.cookieService.get('search_return_url');
            this.searchService.setSelectedResource(this.selectedResource);
            this.checkLinkResoruce(url);
            this.checkResourceAlreadyLinkedToKIN(url);
            const selectedResource = true;
            localStorage.setItem('resourceSelection', JSON.stringify(selectedResource));
        } else {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          if (this.searchService.getInvokingPage() === 'AddHomeKIN') {
            const url = this.searchService.getReturnUrl() ? this.searchService.getReturnUrl()
              : this.cookieService.get('search_return_url');
            this.continueNavigation(url);
          }else {
            this.showErrorModal();
          }
        }
    }

    continueNavigation(url: string) {
        if (!this.searchService.getFormData() && this.cookieService.get('form_data')) {
            this.searchService.setFormData(JSON.parse(this.cookieService.get('form_data')));
        }
        this.cookieService.delete('search_return_url');
        this.cookieService.delete('form_data');
        this.router.navigate([url]);
    }

    // To check wether resource has any links(Home information)
    checkLinkResoruce(url: string) {
        if (url && url.includes('/case/home-information/FAD/')) {
            this.searchService.isHomeAlreadyLinked(this.selectedResource.resourceId).subscribe(res => {
                this.selectedResource.isHomeAlreadyLinked = res;
            });
        }
    }

    checkResourceAlreadyLinkedToKIN(url: string) {
        if (url && url.includes('/search/home-search/add-kin')) {
            this.searchService.isResourceLinkedToKIN(this.selectedResource.resourceId).subscribe(res => {
                if (res.toString() === 'true') {
                    const initialState = {
                        title: 'Add Home KIN',
                        message: 'This Resource ID is already linked to a KIN stage.',
                        showCancel: false,
                    };
                    const modal = this.modalService.show(DfpsConfirmComponent, {
                        class: 'modal-md modal-dialog-centered',
                        initialState,
                    });
                    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
                    this.searchService.setSelectedResource(null);

                } else { this.continueNavigation(url); }
            });

        } else { this.continueNavigation(url); }
    }

    cancel() {
        this.router.navigate([this.searchService.getReturnUrl()]);
    }

    showErrorModal() {
        const initialState = {
            message: 'Please select at least one row to perform this action.',
            title: 'Resource Search',
        };
        this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    }

    clearServerMsg() {
        this.store.dispatch(ERROR_RESET(null));
        this.store.dispatch(SUCCESS_RESET(null));
    }

    clear() {
        this.cookieService.set('search_return_url', this.searchService.getReturnUrl(), 10000, '/', undefined, undefined, 'Lax');
        this.cookieService.set('form_data', JSON.stringify((this.searchService.getFormData()) as FormGroup),
            10000, '/', undefined, undefined, 'Lax');
        window.location.reload();
    }

    filterKinResourceTypes() {
        const filterList = ['01', '07'];
        const allResourceTypes: any = this.displayResponse.resourceTypes;
        if (allResourceTypes && allResourceTypes.length) {
            const filteredResourceTypes = allResourceTypes.filter(element => {
                if (filterList.includes(element.code)) {
                    return element
                }
            });
            this.displayResponse.resourceTypes = filteredResourceTypes;
        }
    }

    filterKinServiceTypes() {
        const serviceFilterList = ['68B', '68C', '68D', '68E', '68F', '68G', '68H', '68I',
            '68J', '68K', '68L', '68M', '68N', '68O', '68P', '68Q', '68R'];
        const allServiceTypes: any = this.areaServiceList;
        if (allServiceTypes && allServiceTypes.length) {
            const filteredServiceTypes = allServiceTypes.filter(element => {
                if (serviceFilterList.includes(element.code)) {
                    return element
                }
            });
            this.areaServiceList = filteredServiceTypes;
        }
    }

    setKinHomeSearchDefaults() {
        this.resourceSearchForm.patchValue({ areaCategory: '24' });
        this.resourceSearchForm.patchValue({ resourceType: '01' });
        this.resourceSearchForm.patchValue({ areaService: '68B' });
        // this.setDefaultRegionFromUserInfo();
    }

    setDefaultRegionFromUserInfo() {
        this.userInfoSubscription = this.authService.getUserInfo().subscribe((userInfo) => {
            this.userInfo = userInfo;
            let userRegionStr = userInfo.userRegion;
            const userRegion = +userRegionStr;
            if (userRegion > 99) {
                userRegionStr = '99';
            } else if (userRegion < 10) {
                userRegionStr = '0' + userRegion.toString();
            } else if (userRegion === 10) {
                userRegionStr = '10';
            }
            this.resourceSearchForm.patchValue({ areaRegion: userRegionStr })
        });
    }
}
