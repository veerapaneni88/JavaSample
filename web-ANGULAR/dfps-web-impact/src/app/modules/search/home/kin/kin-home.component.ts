import { BsModalService } from 'ngx-bootstrap/modal';
import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ResourcePhone } from '@case/model/phoneDetail';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
  Address,
  DfpsAddressValidatorComponent,
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  DropDown,
  ENVIRONMENT_SETTINGS,
  FormUtils,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { DisplayHomeInformation, KinHomeRequest } from '../../model/home';
import { HomeService } from '../../service/home.service';
import { ResourceAddress } from './../../../case/model/case';
import { HomeComponentValidator } from './kin-home.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  templateUrl: './kin-home.component.html'
})
export class KinHomeComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild('validatePrimaryAddress') dfpsPrimaryAddressValidator: DfpsAddressValidatorComponent;

  homeForm: FormGroup;
  displayHomeInformation: DisplayHomeInformation;

  isChildCharacteristics = false;
  isChildesEthnicity = false;
  isHomeDemographicsOpen = true;
  addressModified = false;
  addressValidated = true;
  phoneModified = false;

  homeTypeFilteredValues = [];
  address: ResourceAddress;
  phone: ResourcePhone;

  selectedCharacteristicsData: any[];
  selectedEthnicityData: any[];


  isEditMode: boolean;

  constructor(
    private router: Router,
    private modalService: BsModalService,
    private formBuilder: FormBuilder,
    private homeService: HomeService,
    private navigationService: NavigationService,
    private searchService: SearchService,
    private helpService: HelpService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Add Home');
    this.helpService.loadHelp('Search');
    this.getPageMode();
    this.createForm();
    this.intializeScreen();
  }

  getPageMode() {
    this.isEditMode = this.searchService.getInvokingPage() === 'AddHomeKIN';
  }

  createForm() {
    this.homeForm = this.formBuilder.group(
      {
        resourceId: [''],
        homeName: ['', [Validators.required]],
        category: ['N', [Validators.required]],
        status: ['In Review'],
        ethnicity: [''],
        language: [''],
        religion: [''],
        annualIncome: ['', [DfpsCommonValidators.validateCurrency(12)]],
        maritalStatus: [''],
        marriageDate: ['', [DfpsCommonValidators.validateDate]],
        sourceOfInquiry: [''],
        respite: [''],
        individualStudyProcess: [''],
        comments: [''],
        resourceAddressId: [''],
        typePrimaryAddress: ['01', [Validators.required]],
        vendorIdPrimaryAddress: [''],
        attentionPrimaryAddress: ['', [HomeComponentValidator.alphaNumeric('Primary Address Attention')]],
        addressLn1PrimaryAddress: ['', [Validators.required]],
        addressLn2PrimaryAddress: [''],
        cityPrimaryAddress: ['', Validators.required],
        statePrimaryAddress: ['TX', Validators.required],
        zipPrimaryAddress: ['', [Validators.required, HomeComponentValidator.zipPattern('Primary Address Zip Code')]],
        zipExtPrimaryAddress: ['', [HomeComponentValidator.zipExtensionPattern('Primary Address Zip Code Extension')]],
        countyPrimaryAddress: ['', Validators.required],
        commentsPrimaryAddress: [''],
        schoolDistrictPrimaryAddress: [''],
        resourcePhoneId: [''],
        phoneTypePrimary: ['01'],
        phonePrimary: ['', [HomeComponentValidator.phoneNumberPattern]],
        phoneExtPrimary: ['', [HomeComponentValidator.numericValues]],
        phoneCommentsPrimary: ['']
      }
    );
    if (!this.isEditMode) {
      this.homeForm.disable();
    }
  }

  intializeScreen() {
    this.homeService.displayHomeKIN().subscribe((response) => {
      this.displayHomeInformation = response;
      if (this.displayHomeInformation) {
        this.homeForm.patchValue({ category: 'N' });
      }
      this.displayHomeInformation.statusInquiry = 'In Review';
      FormUtils.disableFormControlStatus(this.homeForm, ['phoneTypePrimary']);
    });

    if (this.searchService.getSelectedResource()) {
      this.homeService.getResource(this.searchService.getSelectedResource().resourceId).subscribe((response) => {
        if (response) {
          response.resourceAddress.forEach(address => {
            if (address.addressType === '01') {
              this.homeForm.patchValue({                
                resourceAddressId: address.resourceAddressId,
                addressLn1PrimaryAddress: address.addressLine1,
                addressLn2PrimaryAddress: address.addressLine2,
                cityPrimaryAddress: address.city,
                countyPrimaryAddress: address.county,
                statePrimaryAddress: address.state,
                zipPrimaryAddress: (address.zip.includes('-') ? address.zip.split('-')[0] : address.zip),
                zipExtPrimaryAddress: (address.zip.includes('-') ? address.zip.split('-')[1] : ''),
                vendorIdPrimaryAddress: address.vendorId,
                commentsPrimaryAddress: address.comments
              })
            }
          })

          response.resourcePhone.forEach(phone => {
            if (phone.phoneType === '01') {
              this.homeForm.patchValue({              
                resourcePhoneId: phone.resourcePhoneId,
                phonePrimary: phone.phoneNumber,
                phoneExtPrimary: phone.phoneExtension,
                phoneCommentsPrimary: phone.comments
              })
            }
          })

          this.homeForm.patchValue({
            resourceId: response.resourceId,
            homeName: response.name ? response.name : '',
          });
          FormUtils.disableFormControlStatus(this.homeForm,
            ['homeName', 'category', 'marriageDate', 'respite', 'individualStudyProcess']);
        }
      });
    }
  }

  saveAndAssign() {
    const formValues: KinHomeRequest = this.homeForm.getRawValue();
    if (this.validateFormGroup(this.homeForm)) {
      if (this.addressModified && !this.addressValidated) {
        const initialState = {
          title: 'Address Detail',
          message: 'Please validate the Primary address before you save.',
          showCancel: false
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
        });
      } else {
        formValues.addressModified = this.addressModified || this.addressValidated;
        formValues.phoneModified = this.phoneModified;
        this.homeService.saveAndAssignKIN(formValues).subscribe(
          res => {
            if (res) {
              sessionStorage.setItem('cacheKey', res.cacheKey);
              this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
              window.location.href = this.environmentSettings.impactP2WebUrl +
                '/home/assigment?cacheKey=' + res.cacheKey;
            }
          }
        );
      }
    }
  }

  maritalStatusChange() {
    const maritalStatus = this.homeForm.get('maritalStatus').value;
    if (maritalStatus) {
      if (maritalStatus !== '01') {
        this.homeForm.patchValue({
          marriageDate: ''
        });
        FormUtils.disableFormControlStatus(this.homeForm, ['marriageDate']);
      } else {
        FormUtils.enableFormControlStatus(this.homeForm, ['marriageDate']);
      }
    } else {
      this.homeForm.patchValue({
        marriageDate: ''
      });
      FormUtils.enableFormControlStatus(this.homeForm, ['marriageDate']);
    }
  }

  updateAdressFieldsPrimary(addressPrimary: Address) {
    this.addressModified = false;
    this.addressValidated = true;
    if (addressPrimary) {      
      if (addressPrimary.isAddressAccepted) {                
        this.homeForm.patchValue({
          addressLn1PrimaryAddress: addressPrimary.street1,
          addressLn2PrimaryAddress: addressPrimary.street2,
          cityPrimaryAddress: addressPrimary.city,
          countyPrimaryAddress: addressPrimary.countyCode,
          statePrimaryAddress: addressPrimary.state,
          zipPrimaryAddress: addressPrimary.zip ? (addressPrimary.zip.includes('-') ? addressPrimary.zip.split('-')[0] : '') : '',
          zipExtPrimaryAddress: addressPrimary.zip ? (addressPrimary.zip.includes('-') ? addressPrimary.zip.split('-')[1] : '') : '',
        });
      } else {
          if (this.homeForm.controls.zipPrimaryAddress.value && 
            this.homeForm.controls.zipPrimaryAddress.value.length !== 5) {
              this.addressModified = true;
              this.addressValidated = false;
              this.homeForm.patchValue({
                zipPrimaryAddress: '',
                zipExtPrimaryAddress: ''
              }); 
          }

          if (this.homeForm.controls.zipExtPrimaryAddress.value && 
            this.homeForm.controls.zipExtPrimaryAddress.value.length !== 4) {
              this.addressModified = true;
              this.addressValidated = false;
              this.homeForm.patchValue({ 
                zipExtPrimaryAddress: ''
              }); 
          }
      }
    }
  }

  validateAdressFieldsPrimary() {
    const addressPrimary: Address = {
      street1: this.homeForm.controls.addressLn1PrimaryAddress.value,
      street2: this.homeForm.controls.addressLn2PrimaryAddress.value,
      city: this.homeForm.controls.cityPrimaryAddress.value,
      county: this.homeForm.controls.countyPrimaryAddress.value,
      state: this.homeForm.controls.statePrimaryAddress.value,
      zip: this.homeForm.controls.zipPrimaryAddress.value,
      extension: this.homeForm.controls.zipExtPrimaryAddress.value
    };
    this.dfpsPrimaryAddressValidator.validate(addressPrimary);
    this.addressValidated = true;

  }

  statePrimaryAddressChange() {
    this.addressChanged();
    if (this.homeForm.get('statePrimaryAddress').value === 'TX') {
      this.homeForm.patchValue({
        schoolDistrictPrimaryAddress: '',
        countyPrimaryAddress: ''
      });
      FormUtils.enableFormControlStatus(this.homeForm, ['schoolDistrictPrimaryAddress']);
    } else {
      this.homeForm.patchValue({
        schoolDistrictPrimaryAddress: '',
        countyPrimaryAddress: '999'
      });
      FormUtils.disableFormControlStatus(this.homeForm, ['schoolDistrictPrimaryAddress']);
    }
  }

  countyPrimaryAddressChange() {
    this.addressChanged();
    if (this.homeForm.get('countyPrimaryAddress').value === '999') {
      this.homeForm.patchValue({
        schoolDistrictPrimaryAddress: '',
        statePrimaryAddress: ''
      });
      FormUtils.disableFormControlStatus(this.homeForm, ['schoolDistrictPrimaryAddress']);
    } else {
      this.homeForm.patchValue({
        schoolDistrictPrimaryAddress: '',
        statePrimaryAddress: 'TX'
      });
      FormUtils.enableFormControlStatus(this.homeForm, ['schoolDistrictPrimaryAddress']);

      this.homeService.getSchoolDistrictByCountyCode(this.homeForm.get('countyPrimaryAddress').value)
        .subscribe((response) => {
          this.displayHomeInformation.schoolDistrictPrimary = response.map(
            sd => ({ decode: sd.schoolDistrictTxtName, code: sd.schoolDistrictCode })) as DropDown[];
        });
    }
  }

  addressChanged() {
    this.addressModified = true;
    this.addressValidated = false;
  }

  phoneChanged() {
    this.phoneModified = true;
  }

  selectResource() {
    let returnUrl: string;
    returnUrl = '/search/home-search/add-kin';
    this.searchService.setFormData(this.homeForm.value);
    this.searchService.setFormContent(this.displayHomeInformation);
    this.searchService.setReturnUrl(returnUrl);
    this.searchService.setInvokingPage('AddHomeKIN');
    this.router.navigate(
      [
        '/search/home-search/add-kin/resource-search',
        { source: 'non-sscc' },
      ],
      { skipLocationChange: false }
    );
  }

}
