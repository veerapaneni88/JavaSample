import { Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
  County,
  DisplayHomeSearchResponse,
  HomeSearchRequest,
  HomeSearchResponse
} from 'app/modules/search/model/HomeSearch';
import {
  DataTable,
  DfpsCommonValidators, DfpsFormValidationDirective,
  DirtyCheck,
  DropDown,
  ENVIRONMENT_SETTINGS, FormUtils,
  NavigationService
} from 'dfps-web-lib';
import { HomeSearchResult } from '../model/HomeSearch';
import { UserData } from './../../shared/model/UserData';
import { HomeSearchValidators } from './home-search.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'home-search',
  templateUrl: './home-search.component.html'
})
export class HomeSearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  urlPath: string;
  urlKey: string[];
  displayHomeInformation: any;

  constructor(
    private searchService: SearchService,
    private router: Router,
    private formBuilder: FormBuilder,
    private navigationService: NavigationService,
    private helpService: HelpService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  homeSearchForm: FormGroup;
  displaySearchResults = false;
  displaySearchResponse: DisplayHomeSearchResponse;
  homeSearchRequest: HomeSearchRequest;
  homeSearchResponse: HomeSearchResult[];
  dataTable: DataTable;
  preSelectedValues = null;
  counties: DropDown[] = [];
  tableBody: any[];
  tableColumn: any[];
  homeSearchDataTable: DataTable;
  showHomeSearchTable = false;
  selectedHome: HomeSearchResponse;
  isSearchCriteriaWarning = false;
  @ViewChild('results') resultsEle: ElementRef;
  searchButtonClicked = false;
  @ViewChild('searchResults') searchResultsEle: ElementRef;
  selectedCharacteristicsData: any[];
  DEFAULT_PAGE_SIZE = this.getPageSize();
  currentPageNumber = 0;
  resourceLink = false;
  displaySearchResponseCopy: DisplayHomeSearchResponse;

  ngOnInit(): void {
    this.navigationService.setTitle('Home Search');
    this.helpService.loadHelp('Search');
    this.createForm();
    this.intializeScreen();
    if (['FadHomeInformation', 'KinHomeInformation'].includes(localStorage.getItem('backFrom'))) {
      this.doSearchForBack();
    } else {
      localStorage.removeItem('counties');
      localStorage.removeItem('homeSearchLink');
      localStorage.removeItem('HomeSearch');
    }
  }

  determinePageMode() {
    if (this.displaySearchResponse.pageMode === 'VIEW') {
      FormUtils.disableFormControlStatus(this.homeSearchForm,
        ['type', 'homeName', 'resourceId', 'status', 'region', 'county', 'category']);
    }
  }

  intializeScreen() {
    this.searchService.displayHomeSearch().subscribe((response) => {
      this.displaySearchResponse = response;
      this.displaySearchResponseCopy = this.displaySearchResponse;
    });
  }

  transformData(data) {
    return data && data.map((value) => {
      return value && value.code;
    });
  }

  onCategoryChange(event) {
    const value = event.target.value.split(': ')[1];
    if (value === 'A' || value === 'N') {
      this.homeSearchForm.controls.type.setValue('');
      FormUtils.disableFormControlStatus(this.homeSearchForm, ['type']);
    }
    else {
      FormUtils.enableFormControlStatus(this.homeSearchForm, ['type']);
    }
  }

  doSearch() {
    const resourceId = this.homeSearchForm.get('resourceId').value;
    if (resourceId) {
      this.preSelectedValues = [];
      this.homeSearchForm.patchValue({
        homeName: null, status: null, language: null, region: null, county: null,
        category: null, city: null, type: null, gender: null, minYear: null,
        maxYear: null, minMonth: null, maxMonth: null, openSlots: null
      });
    }
    this.searchButtonClicked = true;
    this.displaySearchResults = false;
    if (this.validateFormGroup(this.homeSearchForm)) {
      this.homeSearchRequest = this.homeSearchForm.value;
      this.homeSearchRequest.childCharacteristics = this.selectedCharacteristicsData;
      if (this.homeSearchRequest.city) {
        this.homeSearchRequest.city = this.homeSearchRequest.city.toUpperCase();
        this.homeSearchForm.controls.city.setValue(this.homeSearchRequest.city.toUpperCase());
      }
      this.homeSearchForm.controls.homeName.setValue(this.doCamelCase(this.homeSearchRequest.homeName));
      this.searchHome(this.homeSearchRequest);
    }
  }

  doCamelCase(str) {
    const splitStr = str && str.toLowerCase().split(' ');
    if (splitStr) {
      for (let i = 0; i < splitStr.length; i++) {
        splitStr[i] = splitStr[i].charAt(0).toUpperCase() + splitStr[i].substring(1);
      }
      return splitStr.join(' ');
    }
  }

  doSearchForBack() {
    this.searchButtonClicked = true;
    this.displaySearchResults = false;
    this.initializeDataTable();
    this.setFormValue();
    this.doSearch();
  }

  setFormValue() {
    const homeSearchReq: HomeSearchRequest = JSON.parse(localStorage.getItem('HomeSearch'));
    this.counties = JSON.parse(localStorage.getItem('counties'));
    Object.keys(homeSearchReq).forEach((key) => {
      if (this.homeSearchForm.get(key)) {
        this.homeSearchForm.get(key).setValue(homeSearchReq[key]);
      }
    })
    this.preSelectedValues = homeSearchReq.childCharacteristics;
    this.selectedCharacteristicsData = homeSearchReq.childCharacteristics;
  }

  initializeDataTable() {
    this.tableColumn = [
      {
        field: 'resourceName', header: 'Resource Name', width: 225,
        handleClick: this.resourceLink,
        uniqueLinkKey: 'resourceId',
        url: '/case/home-information/:stageCode/:resourceId',
        urlParams: ['stageCode', 'resourceId'], isHidden: false
      },
      { field: 'category', header: 'Category', width: 100 },
      {
        field: 'status', header: 'Status', sortable: false, width: 150
      },
      { field: 'ethnicity', header: 'Ethnicity', sortable: false, width: 150 },
      { field: 'nonFPS', header: 'Non-FPS', sortable: false, width: 150 },
      { field: 'city', header: 'City', sortable: false, width: 150 },
      { field: 'workerName', header: 'Worker Name', sortable: false, width: 150 },
      { field: 'workerPhone', header: 'Worker Phone', sortable: false, width: 150 },
      { field: 'extension', header: 'Ext', sortable: false, width: 150 },
      { field: 'idCase', hidden: true },
      { field: 'idStage', hidden: true }
    ];
    this.dataTable = {
      tableBody: this.homeSearchResponse,
      tableColumn: this.tableColumn,
      isPaginator: (this.homeSearchResponse &&
        this.homeSearchResponse.length > 10)
    };
  }

  searchHome(homeSearchRequest: HomeSearchRequest) {
    localStorage.removeItem('HomeSearch');
    localStorage.setItem('HomeSearch', JSON.stringify(homeSearchRequest));
    this.homeSearchRequest.childCharacteristics = !this.homeSearchRequest.childCharacteristics
      || this.homeSearchRequest.childCharacteristics.length === 0 ? null
      : this.homeSearchRequest.childCharacteristics;
    this.searchService.searchHome(homeSearchRequest).subscribe(
      response => {
        this.homeSearchResponse = response;
        this.displaySearchResults = true;
        this.displayDataTable();

        if ((localStorage.getItem('backFrom') === 'FadHomeInformation')
          || (localStorage.getItem('backFrom') === 'KinHomeInformation')) {
          this.displaySearchResponseCopy = JSON.parse(localStorage.getItem('displaySearchResponse'));
        }

        if (this.homeSearchResponse && this.displaySearchResponseCopy) {
          this.homeSearchResponse.forEach(resourceData => {
            if (resourceData.resourceId && ((resourceData.stageCode === 'KIN' &&
              (this.displaySearchResponseCopy.kinHomeAccess || this.displaySearchResponseCopy.fadHomeAccess)) ||
              (resourceData.stageCode === 'FAD' && this.displaySearchResponseCopy.fadHomeAccess))) {
              this.resourceLink = true;
            } else {
              this.resourceLink = false;
            }
          })
        }
        this.initializeDataTable();
        localStorage.removeItem('backFrom');
        localStorage.removeItem('displaySearchResponse');
      }
    );
  }

  displayDataTable() {
    this.dataTable = {
      tableBody: this.homeSearchResponse,
      tableColumn: this.tableColumn,
      urlKey: this.urlKey,
      urlPath: this.urlPath,
      isMultiSelect: false,
      isPaginator: true,
    };

    const homeSearchUrl = JSON.parse(localStorage.getItem('homeSearchLink'));
    if (homeSearchUrl) {
      setTimeout(() => {
        const element: any = document.querySelector('#id' + homeSearchUrl.resourceId);
        if (element) {
          element.focus();
        }
      }, 1000)
    }
  }

  createForm() {
    this.homeSearchForm = this.formBuilder.group(
      {
        homeName: [''],
        resourceId: ['', [DfpsCommonValidators.validateMaxId, Validators.maxLength(10)]],
        status: [''],
        language: [''],
        region: [''],
        county: [''],
        category: [''],
        city: [''],
        type: [''],
        childCharacteristics: [''],
        gender: [''],
        minYear: [''],
        maxYear: [''],
        minMonth: [''],
        maxMonth: [''],
        openSlots: ['', [DfpsCommonValidators.validateId, Validators.maxLength(5)]],
        isValidated: [true],
        hasAllRequiredParam: [true],
        atleastOneRequiredParam: [true],
        selectedCharacteristics: []
      },
      {
        validators:
          [HomeSearchValidators.validateSearchCriteria(),
          HomeSearchValidators.homeSearchCityPattern]
      }
    );
  }

  loadCounties(event) {
    const selectedRegion = event.target.value.split(': ')[1];
    this.regionCounty(selectedRegion);
  }

  regionCounty(selectedRegion: string) {
    this.homeSearchForm.controls.county.setValue('');
    if (selectedRegion === '98') {
      this.counties = this.displaySearchResponse.regionCounties;
    } else if (selectedRegion === '99') {
      this.counties = [];
    } else {
      this.counties = this.displaySearchResponse.regionCounties.filter(
        (county: County) => county.regionCode === selectedRegion
      );
    }
    this.counties.push({ code: '', decode: '' });
  }

  addFadHome() {
    this.router.navigate(['search/home-search/add-fad']);
  }

  addKinHome() {
    this.searchService.setInvokingPage('');
    this.router.navigate(['search/home-search/add-kin']);
  }

  selectedChildCharacteristicsValues(event) {
    this.selectedCharacteristicsData = [...event];
    this.homeSearchForm.controls.selectedCharacteristics.setValue(this.selectedCharacteristicsData);
  }

  handleRouting(event: any) {
    if (this.resourceLink) {
      const userData: UserData = { idResource: event.tableRow.resourceId }
      this.searchService.updateUserContext(userData).subscribe(
        response => {
          const link = (event.link as string).replace('null', '0');
          this.router.navigate([link]);
          localStorage.setItem('homeSearchLink', JSON.stringify({
            resourceName: event.tableRow.resourceName,
            resourceId: event.tableRow.resourceId
          }));
          localStorage.setItem('counties', JSON.stringify(this.counties));
          localStorage.setItem('displaySearchResponse', JSON.stringify(this.displaySearchResponse));
        }
      );
    }
  }

  getPageSize(): number {
    const pageSizeData = localStorage.getItem('HomeSearch_PageSize');
    return pageSizeData ? Number(pageSizeData) : 10 as number;
  }
}

