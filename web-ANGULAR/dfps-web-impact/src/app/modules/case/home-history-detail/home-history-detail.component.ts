import { Component, HostListener, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ResourceHistoryDetail, ResourceHistoryDetailRes } from '@case/model/resourcehistory';
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
import { CaseService } from './../service/case.service';
import { HomeHistoryDetailValidators } from './home-history-detail.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'home-history-detail',
  templateUrl: './home-history-detail.component.html'
})
export class HomeHistoryDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  resurceHistoryDetailRes: ResourceHistoryDetailRes;
  resourceHistoryDetail: ResourceHistoryDetail;
  resourceHistoryDetailForm: FormGroup;
  resourceHistoryId: any;
  resourceId: any;
  isEditMode: boolean;
  isDisplayScreen: boolean;
  famRelTypes = [];
  previousEndDate: string;
  nextStartDate: string;
  pageMode: string;
  endDate: string;
  selectedHomeTypeCodes = [];
  selectedFamRelTypeCodes = [];
  disbaleHomeTypes: boolean;
  faHomeTypePreSelectedValues = [];

  constructor(private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private caseService: CaseService,
    private modalService: BsModalService,
    private route: ActivatedRoute,
    private router: Router,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    if(this.router.url.includes('FAD')) {
      this.navigationService.setTitle('Home History Detail FAD');
    } 
    this.helpService.loadHelp('Case');
    this.getStorageData();
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.resourceHistoryId = routeParams.get('id');
      this.resourceId = routeParams.get('resourceId');
    }
    this.getPageMode();
    this.initializeScreen(this.resourceHistoryId);

  }

  // If page mode is edit and record contains end date all field are in edit mode
  // Else only start date can be edited.
  getPageMode() {
    if (this.pageMode === 'EDIT' && this.endDate) {
      this.isEditMode = true;
      this.disbaleHomeTypes = false;
    } else {
      this.isEditMode = false;
      this.disbaleHomeTypes = true;
    }
  }

  initializeScreen(resourceHistoryId) {
    this.caseService.getResourceHistoryDetail(this.resourceId, resourceHistoryId).subscribe((response) => {
      this.resurceHistoryDetailRes = response;
      if (this.resurceHistoryDetailRes) {
        this.resourceHistoryDetail = this.resurceHistoryDetailRes.resourceHistoryDetail;
        this.filterHomeTypes();
        this.filterCategory();
        this.createForm();
        if (this.isEditMode) {
          this.isDisplayScreen = true;
        } else {
          this.showWarningMessage();
        }
        this.faHomeTypePreSelectedValues = this.transformData(this.resourceHistoryDetail.selectedHomeTypes);
        if (this.resourceHistoryDetail && this.resourceHistoryDetail.category) {
          const category: any = this.resourceHistoryDetail.category;
          if (category === 'A' || category === 'O') {
            this.disbaleHomeTypes = true;
          }
        }
      }
      this.getFosterType();
      this.setFormValues();
    });
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  // If users opens a record which does not have end date, warning alert is appears before displaying the screen.
  showWarningMessage() {
    const initialState = {
      title: 'Resource History',
      message: 'The most current was selected. Only the start date may be modified.',
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered',
      initialState,
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { this.isDisplayScreen = true; });
  }

  createForm() {
    this.resourceHistoryDetailForm = this.formBuilder.group(
      {
        startDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
        endDate: ['', [DfpsCommonValidators.validateDate]],
        capacity: [''],
        category: [''],
        status: [''],
        minAgeMaleChildYear: [''],
        minAgeMaleChildMonth: [''],
        maxAgeMaleChildYear: [''],
        maxAgeMaleChildMonth: [''],
        minAgeFemaleChildYear: [''],
        minAgeFemaleChildMonth: [''],
        maxAgeFemaleChildYear: [''],
        maxAgeFemaleChildMonth: [''],
        closureReason: [''],
        recommendReopen: [''],
        involuntaryClosure: [''],
        homeType: [''],
        famRelTypes: [''],
        selectedHomeTypeCodes: [''],
        selectedFamRelTypeCodes: [''],
        ageRangeErrorMsg: ['']
      }
    );
    if (!this.isEditMode) {
      FormUtils.disableFormControlStatus(this.resourceHistoryDetailForm, ['endDate', 'capacity',
        'category', 'status', 'minAgeMaleChildYear', 'minAgeMaleChildMonth', 'maxAgeMaleChildYear',
        'maxAgeMaleChildMonth', 'minAgeFemaleChildYear', 'minAgeFemaleChildMonth', 'maxAgeFemaleChildYear',
        'maxAgeFemaleChildMonth', 'closureReason', 'recommendReopen', 'involuntaryClosure']);
    }
    this.setValidatorsDynamically();
  }

  // Adding the validators dynamically based on page mode
  setValidatorsDynamically() {
    if (this.isEditMode) {
      this.resourceHistoryDetailForm.get('endDate').setValidators([Validators.required, DfpsCommonValidators.validateDate]);
      this.resourceHistoryDetailForm.get('category').setValidators([Validators.required]);
      this.resourceHistoryDetailForm.get('status').setValidators([Validators.required]);
      this.resourceHistoryDetailForm.setValidators([
        HomeHistoryDetailValidators.startDateValidation(this.previousEndDate),
        HomeHistoryDetailValidators.endDateValidation(this.nextStartDate),
        HomeHistoryDetailValidators.homeTypeValidation(this.resourceHistoryDetail?.nonprsPca),
        HomeHistoryDetailValidators.relationshipValidation(this.resourceHistoryDetail?.nonprsPca),
        HomeHistoryDetailValidators.capacityValidation,
        HomeHistoryDetailValidators.closureValidation,
        HomeHistoryDetailValidators.ageRangeValidation
      ]);
    } else {
      this.resourceHistoryDetailForm.setValidators([
        HomeHistoryDetailValidators.startDateValidation(this.previousEndDate)
      ])
    }
  }

  setFormValues() {
    if (this.resurceHistoryDetailRes && this.resourceHistoryDetail) {
      this.resourceHistoryDetailForm.patchValue({
        capacity: String(this.resourceHistoryDetail.facilityCapacity),
        category: this.resourceHistoryDetail.category,
        startDate: this.resourceHistoryDetail.effectiveDate,
        endDate: this.resourceHistoryDetail.endDate,
        status: this.resourceHistoryDetail.faHomeStatus,
        minAgeMaleChildYear: String(this.resourceHistoryDetail.minAgeMaleChildYear),
        minAgeMaleChildMonth: String(this.resourceHistoryDetail.minAgeMaleChildMonth),
        maxAgeMaleChildYear: String(this.resourceHistoryDetail.maxAgeMaleChildYear),
        maxAgeMaleChildMonth: String(this.resourceHistoryDetail.maxAgeMaleChildMonth),
        minAgeFemaleChildYear: String(this.resourceHistoryDetail.minAgeFemaleChildYear),
        minAgeFemaleChildMonth: String(this.resourceHistoryDetail.minAgeFemaleChildMonth),
        maxAgeFemaleChildYear: String(this.resourceHistoryDetail.maxAgeFemaleChildYear),
        maxAgeFemaleChildMonth: String(this.resourceHistoryDetail.maxAgeFemaleChildMonth),
        closureReason: this.resourceHistoryDetail.closureReason,
        recommendReopen: this.resourceHistoryDetail.recommendReopen,
        involuntaryClosure: this.resourceHistoryDetail.involuntaryClosure,
        selectedHomeTypeCodes: this.selectedHomeTypeCodes,
        selectedFamRelTypeCodes: this.selectedFamRelTypeCodes
      });
    }
  }

  // Seperating the Foster Home Type, Foster Parent's Relationship to Children from response codesDto to
  // dispaly multiselect values
  filterHomeTypes() {
    const homeTypes = this.resurceHistoryDetailRes.typeList;
    if (homeTypes && homeTypes.length) {
      const filterRelTypes = ['Relative', 'Fictive Kin', 'Unrelated'];
      let filterHomeTypes = [...filterRelTypes, 'Kin Aunt/Uncle', 'Kin Other Family', 'Kin Grandparent'];
      this.famRelTypes = homeTypes.filter(value => {
        if (filterRelTypes.includes(value.decode)) {
          return value
        }
      });
      if (this.resurceHistoryDetailRes.resourceHistoryDetail && this.resurceHistoryDetailRes.resourceHistoryDetail.selectedHomeTypes) {
        const kinTypes = this.resurceHistoryDetailRes.resourceHistoryDetail.selectedHomeTypes.map(type => type.decode)
          .filter(type => (type === 'Kin Aunt/Uncle' || type === 'Kin Other Family' || type === 'Kin Grandparent'));
        filterHomeTypes = filterHomeTypes.filter((element) => !kinTypes.includes(element));
      }
      this.resurceHistoryDetailRes.homeType = homeTypes.filter(value => {
        if (!filterHomeTypes.includes(value.decode)) {
          return value
        }
      });
    }
  }

  // Shows ICPC or Kin Licensed in dropdown only if preselected value contains it. Else removed from dropdown.
  filterCategory() {
    const category: any = this.resurceHistoryDetailRes.categoryList;
    if (category && category.length) {
      const filteredCategory = category.filter(element => (element.code !== 'I' && element.code !== 'K'))
      const selectedCategory: any = this.resourceHistoryDetail ? this.resourceHistoryDetail.category : null;
      const icpcOrKin = category.filter(element =>
        element.code === selectedCategory && !filteredCategory.find(cat => cat.code === selectedCategory))
      this.resurceHistoryDetailRes.categoryList = [...filteredCategory, ...icpcOrKin];
    }
  }

  // Seperating the preselected Foster Home Type, Foster Parent's Relationship to Children from display response
  // dispaly preselected values
  getFosterType() {
    const familyRelationValues = ['Relative', 'Fictive Kin', 'Unrelated'];
    if (this.resourceHistoryDetail.selectedHomeTypes) {
      this.selectedHomeTypeCodes = this.resourceHistoryDetail.selectedHomeTypes.filter(value => {
        if (!familyRelationValues.includes(value.decode)) {
          return value;
        }
      }).map(value => value.code);
      this.selectedFamRelTypeCodes = this.resourceHistoryDetail.selectedHomeTypes.filter(value => {
        if (familyRelationValues.includes(value.decode)) {
          return value;
        }
      }).map(value => value.code);
    }
  }

  doSave() {
    if (this.validateFormGroup(this.resourceHistoryDetailForm)) {
      this.resourceHistoryDetail.previousEndDate = this.previousEndDate;
      this.resourceHistoryDetail.nextStartDate = this.nextStartDate;
      this.resourceHistoryDetail.facilityCapacity = this.resourceHistoryDetailForm.get('capacity').value;
      this.resourceHistoryDetail.category = this.resourceHistoryDetailForm.get('category').value;
      this.resourceHistoryDetail.effectiveDate = this.resourceHistoryDetailForm.get('startDate').value;
      this.resourceHistoryDetail.endDate = this.resourceHistoryDetailForm.get('endDate').value;
      this.resourceHistoryDetail.faHomeStatus = this.resourceHistoryDetailForm.get('status').value;
      this.saveResourceHistoryDetail();
    }
  }

  saveResourceHistoryDetail() {
    this.setHomeTypesDto();
    const payload = Object.assign(this.resourceHistoryDetail, this.resourceHistoryDetailForm.value);
    this.caseService.saveResourceHistoryDetail(this.resourceId, payload).subscribe(
      response => {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        const url= this.router.url;
        const redirectUrl= url.slice(0, url.lastIndexOf('/'));
        this.router.navigate([redirectUrl]);
      }
    );
  }

  deleteResourceHistory() {
    const initialState = {
      title: 'Resource History Detail',
      message: 'Are you sure you want to delete this information?',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
    
      if (result === true) {
        this.caseService
          .deleteResourceHistory(this.resourceId, this.resourceHistoryDetail.resourceHistoryId)
          .subscribe((res) => {
            const url= this.router.url;
            const redirectUrl= url.slice(0, url.lastIndexOf('/'));
            this.router.navigate([redirectUrl]);
          });
      }
    });
  }

  updateHomeTypes(event) {
    this.selectedHomeTypeCodes = [...event];
    this.resourceHistoryDetailForm.patchValue({ selectedHomeTypeCodes: this.selectedHomeTypeCodes });
  }

  updateFamilyRelationTypes(event) {
    this.selectedFamRelTypeCodes = [...event];
    this.resourceHistoryDetailForm.patchValue({ selectedFamRelTypeCodes: this.selectedFamRelTypeCodes });
  }

  getStorageData() {
    const data = JSON.parse(localStorage.getItem('resourceHistoryLink'));
    if (data) {
      this.previousEndDate = data.previousEndDate;
      this.nextStartDate = data.nextStartDate;
      this.pageMode = data.pageMode;
      this.endDate = data.endDate;
    }
  }

  // Creating the hometype dto with selected home types before making the save call.
  setHomeTypesDto() {
    const homeTypes = [... this.selectedHomeTypeCodes, ...this.selectedFamRelTypeCodes];
    homeTypes.forEach((element) => {
      this.resourceHistoryDetail.updatedHomeTypes.push(Object.assign({}, { code: element }))
    });
  }

  // If category selected is Adoptive/ ICPC Adoptive, Foster Home Type, Foster Parent's Relationship to Children
  // are be disabled and selected values are reset
  categoryTypeChanged(event) {
    const categoryValue = this.resourceHistoryDetailForm.get('category').value;
    if (categoryValue && (categoryValue === 'A' || categoryValue === 'O')) {
      this.disbaleHomeTypes = true;
      if (this.resourceHistoryDetail && (this.selectedHomeTypeCodes || this.selectedFamRelTypeCodes)) {
        this.faHomeTypePreSelectedValues = [];
        this.selectedHomeTypeCodes = [];
        this.selectedFamRelTypeCodes = [];
        this.resourceHistoryDetailForm.patchValue({ selectedHomeTypeCodes: [] });
        this.resourceHistoryDetailForm.patchValue({ selectedFamRelTypeCodes: [] });
      }
    } else {
      this.disbaleHomeTypes = false;
    }
  }

  @HostListener('window:popstate' || 'window:hashchange', ['$event'])
  onPopState(event) {
    localStorage.setItem('backFrom', 'ResourceHistoryDetail');
  }

  transformData(data) {
    return data && data.map((value) => {
      return value && value.code;
    });
  }

  helpIcon() {
    const initialState = {
      title: 'Home Information',
      messageList: [{
        content: `'Relative' means a person and a child in DFPS conservatorship are related by blood or 
      adoption (consanguinity) or marriage (affinity).  This term excludes the child's legal, birth, 
      or adoptive parent(s).`, url: ''
      },
      {
        content: `'Fictive kin' means a person and a child in DFPS conservatorship (or the child's family) 
      have a longstanding and significant relationship with each other.  This term excludes the child's legal, 
      birth, or adoptive parent(s).  Examples include a godparent or someone considered to be an aunt or uncle, 
      even though the person is not related to the child.`, url: ''
      },
      {
        content: `'Unrelated' means the relationship of a person and a child in DFPS conservatorship does not meet the criteria 
      of 'relative' or 'fictive kin'.  This term excludes the child's legal, birth, or adoptive parent(s).`, url: ''
      }],
      showCancel: false,
    };
    this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg modal-dialog-centered', initialState });
  }

}
