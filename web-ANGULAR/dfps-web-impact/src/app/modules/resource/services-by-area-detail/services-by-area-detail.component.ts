import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck, DropDown, ENVIRONMENT_SETTINGS, FormUtils, NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ResourceService, } from '../service/resource.service';
import { County, ResourceSvc, ResourceSvcByAreaDetailRes } from './../model/resource';
import {ServicesByAreaDetailValidator} from './services-by-area-detail.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  templateUrl: './services-by-area-detail.component.html'
})
export class ServicesByAreaDetailComponent extends DfpsFormValidationDirective implements OnInit {

  servicesByAreaDetailForm: FormGroup;
  resourceSvcByAreaDetailRes: ResourceSvcByAreaDetailRes;
  resourceSvc: ResourceSvc;

  displayDeleteButton = false;
  displaySaveButton = false;

  resourceId: any;
  resourceServiceId: any;

  filteredRegions: DropDown[] = [];
  filteredCounties: DropDown[] = [];
  confirmMessage: any;

  filteredServices: DropDown[] = [];

  constructor(
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    private route: ActivatedRoute,
    private router: Router,
    private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private resourceService: ResourceService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
    this.resourceId = this.route.snapshot.paramMap.get('resourceId');
    this.resourceServiceId = this.route.snapshot.paramMap.get('id');
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Services by Area Detail');
    this.helpService.loadHelp('Resource');
    this.navigationService.setUserDataValue('firstLevelTab', 'Resource');
    this.navigationService.setUserDataValue('idResource', this.resourceId);
    this.createForm();
    this.intializeScreen();
  }

  createForm() {
    this.servicesByAreaDetailForm = this.formBuilder.group({
      category: ['', [Validators.required]],
      service: ['', [Validators.required]],
      state: ['TX', [Validators.required]],
      program: ['', [Validators.required]],
      region: ['', [Validators.required]],
      county: [''],
      incomeBased: [''],
      countyPartial: [''],
      allCounties: [''],
      kinshipTraining: [''],
      kinshipHomeAssessment: [''],
      kinshipIncome: [''],
      kinshipAgreement:[''],
      buttonClicked: ['']
    }, {
      validators: [
        ServicesByAreaDetailValidator.saveValidations
      ]
    });
  }

  intializeScreen() {
    this.loadFormData();
  }

  loadFormData() {
    this.resourceService.getServicesByAreaDetailRes(this.resourceId, this.resourceServiceId)
      .subscribe(
        response => {
          this.resourceSvcByAreaDetailRes = response;
          this.resourceSvc = this.resourceSvcByAreaDetailRes.resourceService;

          if (response.resourceService) {
            this.servicesByAreaDetailForm.patchValue({
              lastUpdatedDate: this.resourceSvc.lastUpdatedDate,
              category: this.resourceSvc.category,
              service: this.resourceSvc.service,
              state: this.resourceSvc.state ? this.resourceSvc.state : 'TX',
              program: this.resourceSvc.program,
              region: this.resourceSvc.region,
              county: this.resourceSvc.county,
              incomeBased: this.resourceSvc.incomeBased === 'Y' ? true : false,
              countyPartial: this.resourceSvc.countyPartial === 'Y' ? true : false,
              kinshipTraining: this.resourceSvc.kinshipTraining === 'Y' ? true : false,
              kinshipHomeAssessment: this.resourceSvc.kinshipHomeAssessment === 'Y' ? true : false,
              kinshipIncome: this.resourceSvc.kinshipIncome === 'Y' ? true : false,
              kinshipAgreement: this.resourceSvc.kinshipAgreement === 'Y' ? true : false
            });
          }

          this.filterDropDowns();

          if (this.resourceService) {
            if (this.resourceSvc.region) {
              this.populateCountiesForRegion(this.resourceSvc.region);
            }

            if (this.resourceSvc.category) {
              this.populateServicesForCategory(this.resourceSvc.category);
            }
          }

          if (this.servicesByAreaDetailForm.get('service').value) {
            FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['service']);
          } else {
            FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['service']);
          }

          if (this.servicesByAreaDetailForm.get('state').value &&
            this.servicesByAreaDetailForm.get('state').value !== 'TX') {
            this.servicesByAreaDetailForm.patchValue({
              region: '99',
              county: '999'
            });
            FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
            FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
          } else if (this.servicesByAreaDetailForm.get('state').value === 'TX') {
            FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
            if (!this.servicesByAreaDetailForm.get('region').value ||
              this.servicesByAreaDetailForm.get('region').value === '99') {
              if(this.servicesByAreaDetailForm.get('region').value === '99'){
                this.servicesByAreaDetailForm.controls.county.setValue('999');
              }
              FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
            } else {
              FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
            }
          } else {
            FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
            FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
          }

          if (!this.servicesByAreaDetailForm.get('region').value ||
            this.servicesByAreaDetailForm.get('region').value === '99') {
            FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
          } else {
            FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
          }

          if (this.servicesByAreaDetailForm.get('county').value) {
            this.servicesByAreaDetailForm.controls.allCounties.setValue('');
          }

          this.setPageModes();
          this.setPageElementModes();
          this.populateCheckBoxes();
        }
      )
  }

  populateCheckBoxes() {
    if (this.servicesByAreaDetailForm.get('state').value !== 'TX') {
      this.servicesByAreaDetailForm.patchValue({
        allCounties: '',
        countyPartial: ''
      });

      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
    } else {
      if (!this.servicesByAreaDetailForm.get('region').value  ||
        '99' === this.servicesByAreaDetailForm.get('region').value || '00' === this.servicesByAreaDetailForm.get('region').value) {
        this.servicesByAreaDetailForm.patchValue({
          allCounties: '',
          countyPartial: ''
        });

        FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
        FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
      } else {
        if (!this.servicesByAreaDetailForm.get('county').value) {
          this.servicesByAreaDetailForm.patchValue({
            allCounties: 'true',
            countyPartial: ''
          });

          FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
          FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);

        } else if ('999' === this.servicesByAreaDetailForm.get('county').value) {
          this.servicesByAreaDetailForm.patchValue({
            allCounties: '',
            countyPartial: ''
          });

          FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
          FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);

        }
      }
    }
  }

  setPageElementModes() {
    const bInActiveContract = false;
    const bIsRegionWide = false;
    let sDisabledIncomeAndProgram = true;
    let sDisabledPartialCounties = true;
    let sDisabledCounties = true;
    let sDisabledOtherFields = true;
    // SIR 24058 - Kinship Caretaker Eligibility variables
    const sDisabledKnshpTraining = true;
    const sDisabledKnshpHomeAssmnt = true;
    const sDisabledKnshpIncome = true;
    const sDisabledKnshpAgreement = true;

    // If we got called in view mode, we shouldn't be able to do anything.
    if (this.resourceSvcByAreaDetailRes.pageMode !== 'VIEW') {
      if (this.resourceSvcByAreaDetailRes.pageMode === 'NEW') {
        sDisabledIncomeAndProgram = false;
        sDisabledPartialCounties = false;
        sDisabledOtherFields = false;
        sDisabledCounties = false;
        // SIR 24058 - initialize Kinship Eligibility indicator disabled variables.
        // sDisabledKnshpTraining = false;
        // sDisabledKnshpHomeAssmnt = false;
        // sDisabledKnshpIncome = false;
        // sDisabledKnshpAgreement = false;
      } else {
        if (!this.resourceSvcByAreaDetailRes.resourceService.contracted) {
          this.displayDeleteButton = true;
          this.displaySaveButton = true;
          //  1) If service is in active contract, it is read only.
          //  2) If service is not active, but is region-wide, you can change program and income based.
          sDisabledIncomeAndProgram = false;
          sDisabledCounties = false;

          if (!bIsRegionWide) {
            //  3) If service is a particular county, you can also change partial counties.
            sDisabledPartialCounties = false;
          }
        }
      }
    }

    if (sDisabledOtherFields) {
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['category']);
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['service']);
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['state']);
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
    } else {
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['category']);
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['service']);
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['state']);
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
    }

    if (sDisabledIncomeAndProgram) {
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['program']);
    } else {
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['program']);
    }

    if (sDisabledPartialCounties) {
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
    } else {
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
    }

    if (sDisabledCounties) {
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
    } else {
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
    }

    if (sDisabledIncomeAndProgram) {
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['incomeBased']);
    } else {
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['incomeBased']);
    }
  }

  setPageModes() {
    if (this.resourceSvcByAreaDetailRes.pageMode === 'NEW') {
      this.displaySaveButton = true;
      this.displayDeleteButton = false;
    } else if (this.resourceSvcByAreaDetailRes.pageMode === 'EDIT') {
      this.displaySaveButton = true;
      this.displayDeleteButton = true;
    } else if (this.resourceSvcByAreaDetailRes.pageMode === 'VIEW') {
      this.servicesByAreaDetailForm.disable();
      this.displayDeleteButton = false;
      this.displaySaveButton = false;
    }

    if (this.resourceSvcByAreaDetailRes.resourceService &&
      this.resourceSvcByAreaDetailRes.resourceService.id < 1) {
      this.displayDeleteButton = false;
    }

    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  save() {
    this.servicesByAreaDetailForm.patchValue({ buttonClicked: 'save' });
    if (this.validateFormGroup(this.servicesByAreaDetailForm)) {
      const formValues: ResourceSvc = this.servicesByAreaDetailForm.getRawValue();
      formValues.resourceId = this.resourceId;
      formValues.id = this.resourceSvc.id;


      if (this.servicesByAreaDetailForm.get('region').value === '98') {
        const initialState = {
          title: 'Services By Area Detail',
          message: 'You are about to add a statewide row. This will delete '
            + 'any already existing similar regionwide '
            + 'rows, then add new ones. This will also '
            + 'delete the client characterisitics for '
            + 'those pre-existing rows. Are you sure you '
            + 'want to do this?',
          showCancel: true
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md', initialState
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
          if (result === true) {
            this.resourceService.saveServicesByAreaDetail(this.resourceId, formValues
            ).subscribe(
              res => {
                if (res) {
                  this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                  this.router.navigate(['/resource/services-by-area/' + this.resourceId]);
                }
              }
            );
          }
        });
      } else {
        this.resourceService.saveServicesByAreaDetail(this.resourceId, formValues
        ).subscribe(
          res => {
            if (res) {
              this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
              this.router.navigate(['/resource/services-by-area/' + this.resourceId]);
            }
          }
        );
      }

    }
  }

  delete() {
    if ((this.servicesByAreaDetailForm.get('state').value === 'TX' &&
      this.servicesByAreaDetailForm.get('region').value === '98') ||
      (this.servicesByAreaDetailForm.get('state').value !== 'TX')) {
      this.confirmMessage = 'This will delete the Client Characteristics for the service within the state. Delete?';
    } else {
      this.confirmMessage = 'This will delete the Client Characteristics for the service within the region. Delete?';
    }

    const initialState = {
      title: 'Services By Area Detail',
      message: this.confirmMessage,
      showCancel: true
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md', initialState
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
      if (result === true) {
        this.resourceService.deleteServicesByAreaDetail(this.resourceId, this.resourceServiceId).subscribe(
          res => {
            if (res) {
              this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
              this.router.navigate(['/resource/services-by-area/' + this.resourceId]);
            }
          }
        );
      }
    });
  }

  resourceSvcStateChange() {
    this.filterRegionForStateChange();
    if (this.servicesByAreaDetailForm.get('state').value) {
      if (this.servicesByAreaDetailForm.get('state').value === 'TX') {
        this.servicesByAreaDetailForm.patchValue({
          region: '',
          county: ''
        });
        FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
        FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
      } else {
        this.servicesByAreaDetailForm.patchValue({
          region: '99',
          county: '999'
        });

        if(this.servicesByAreaDetailForm.get('region').value === '99'){
          this.filteredCounties = this.resourceSvcByAreaDetailRes.counties.filter(
            (county: County) => county.code === '999'
          );
          this.servicesByAreaDetailForm.controls.county.setValue('999');
        }
        FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
        FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
      }
    } else {
      this.servicesByAreaDetailForm.patchValue({
        region: '',
        county: ''
      });
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['region']);
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
    }
    this.populateCheckBoxes();
  }

  displayedOnAreaServedChange() {
    if (this.servicesByAreaDetailForm.get('allCounties').value) {
      this.servicesByAreaDetailForm.patchValue({
        county: '',
        countyPartial: ''
      });

      if (this.resourceSvcByAreaDetailRes.pageMode === 'EDIT' && this.servicesByAreaDetailForm.get('allCounties').enabled){
        FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
        FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
      }else{
        FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
        FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
      }
    } else {
      if (this.resourceSvcByAreaDetailRes.pageMode === 'EDIT'){
        this.servicesByAreaDetailForm.patchValue({
          county: this.resourceSvc.county,
          countyPartial: this.resourceSvc.countyPartial === 'Y' ? true : false
        });
      }
      if(this.servicesByAreaDetailForm.get('allCounties').enabled) {
        FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
        FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
      }
    }
  }

  resetKinshipCheckboxes() {
      this.servicesByAreaDetailForm.patchValue({
        kinshipTraining: false,
        kinshipHomeAssessment:false,
        kinshipIncome: false,
        kinshipAgreement:false,
        buttonClicked:''
      });
  }

  loadCountyChange(event) {
    if (this.servicesByAreaDetailForm.get('county').value) {
      this.servicesByAreaDetailForm.patchValue({
        allCounties: false,
        countyPartial:false
      });
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
    }else{
      this.servicesByAreaDetailForm.patchValue({
        allCounties: 'true',
        countyPartial:false
      });
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['allCounties']);
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['countyPartial']);
    }

  }

  loadCountiesForRegionChange(event) {
    this.servicesByAreaDetailForm.controls.county.setValue('');
    if (!this.servicesByAreaDetailForm.get('region').value ||
      this.servicesByAreaDetailForm.get('region').value === '00' ||
      this.servicesByAreaDetailForm.get('region').value === '98' ||
      this.servicesByAreaDetailForm.get('region').value === '99') {
      if(this.servicesByAreaDetailForm.get('region').value === '99'){
        this.servicesByAreaDetailForm.controls.county.setValue('999');
      }
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
    } else {
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['county']);
    }
    const selectedRegion = event.target.value.split(': ')[1];
    this.populateCountiesForRegion(selectedRegion);
    this.populateCheckBoxes();
  }

  loadServicesForCategoryChange(event) {
    this.servicesByAreaDetailForm.controls.service.setValue('');
    if (!this.servicesByAreaDetailForm.get('category').value) {
      FormUtils.disableFormControlStatus(this.servicesByAreaDetailForm, ['service']);
    } else {
      FormUtils.enableFormControlStatus(this.servicesByAreaDetailForm, ['service']);
    }
    const selectedCategory = event.target.value.split(': ')[1];
    this.populateServicesForCategory(selectedCategory);
  }

  populateServicesForCategory(selectedCategory: string) {

    if (selectedCategory) {
      const filteredCategoryServices = this.resourceSvcByAreaDetailRes.categoryServices.filter(
        element => (element.decode === selectedCategory)
      );

      if (filteredCategoryServices && filteredCategoryServices.length > 0) {
        this.filteredServices = this.resourceSvcByAreaDetailRes.services.filter(
          element => filteredCategoryServices.find(catservice => catservice.code === element.code)
        );
      }
    } else {
      this.servicesByAreaDetailForm.controls.service.setValue('');
    }
  }

  populateCountiesForRegion(selectedRegion: string) {

    if (selectedRegion) {
      this.filteredCounties = this.resourceSvcByAreaDetailRes.regionCounties.filter(
        (county: County) => county.regionCode === selectedRegion
      );
      if(selectedRegion === '99'){
        this.filteredCounties = this.resourceSvcByAreaDetailRes.counties.filter(
          (county: County) => county.code === '999'
        );
        this.servicesByAreaDetailForm.controls.county.setValue('999');
      }
    } else {
      this.servicesByAreaDetailForm.controls.county.setValue('');
    }
  }

  filterDropDowns() {
    this.filterRegionForStateChange();
    this.filterServicesForCategoryChange();
  }

  filterRegionForStateChange() {
    this.filteredRegions = this.resourceSvcByAreaDetailRes.regions.filter(value => {
      if (this.servicesByAreaDetailForm.get('state').value === 'TX') {
        if (value.code !== '99') {
          return value;
        }
      }else {
        if (value.code === '99') {
          return value;
        }
      }
    });

    if(this.servicesByAreaDetailForm.get('region').value === '99'){
      this.filteredCounties = this.resourceSvcByAreaDetailRes.counties.filter(
        (county: County) => county.code === '999'
      );
      this.servicesByAreaDetailForm.controls.county.setValue('999');
    }
  }

  filterServicesForCategoryChange() {
    const filteredCategoryServices = this.resourceSvcByAreaDetailRes.categoryServices.filter(
      element => (element.decode === this.servicesByAreaDetailForm.get('category').value)
    );

    if (filteredCategoryServices && filteredCategoryServices.length > 0) {
      this.filteredServices = this.resourceSvcByAreaDetailRes.services.filter(
        element => filteredCategoryServices.find(catservice => catservice.code === element.code)
      );
    }
  }

}
