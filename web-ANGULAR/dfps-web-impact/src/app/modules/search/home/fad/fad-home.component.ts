import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
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
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { DisplayHomeInformation, FadHomeRequest } from '../../model/home';
import { HomeService } from '../../service/home.service';
import { FadHomeComponentValidator } from './fad-home.validator';
import { AddressDetailValidators } from './../../../case/home-information/fad/address-detail/address-detail.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  templateUrl: './fad-home.component.html'
})
export class FadHomeComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild('validatePrimaryAddress') dfpsPrimaryAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('validateBusinessAddress') dfpsBusinessAddressValidator: DfpsAddressValidatorComponent;

  homeForm: FormGroup;
  displayHomeInformation: DisplayHomeInformation;

  isChildCharacteristics = false;
  isChildesEthnicity = false;
  isHomeDemographicsOpen = true;

  homeTypeFilteredValues = [];
  isAddressTypePrimaryMsg: string;

  selectedCharacteristicsData: any[];
  selectedEthnicityData: any[];

  skipPrimaryAddressValidation = true;
  skipBusinessAddressValidation = true;

  isPrimaryAddressChanged: boolean;
  isPrimaryAddressValidated: boolean;

  isBusinessAddressChanged: boolean;
  isBusinessAddressValidated: boolean;

  isAlert = false;

  constructor(
    private formBuilder: FormBuilder,
    private homeService: HomeService,
    private modalService: BsModalService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Add Home'); 
    this.helpService.loadHelp('Search');
    this.intializeScreen();
    this.createForm();
  }

  createForm() {
    this.homeForm = this.formBuilder.group(
      {
        homeName: ['', [Validators.required]],
        category: ['', [Validators.required]],
        status: ['Inquiry'],
        ethnicity: [''],
        language: [''],
        religion: [''],
        annualIncome: ['', [DfpsCommonValidators.validateCurrency(12)]],
        maritalStatus: [''],
        marriageDate: ['', [DfpsCommonValidators.validateDate]],
        sourceOfInquiry: [''],
        respite: [''],
        individualStudyProcess: [''],
        childesEthnicity: [''],
        noOfChildren: ['0', [Validators.required, FadHomeComponentValidator.numericValues]],
        willTransportChildren: [''],
        emergencyPlacements: [''],
        comments: [''],
        childCharacteristics: [''],
        maleAgeRangeInterestsMinYear: ['0'],
        maleAgeRangeInterestsMinMonth: ['0'],
        maleAgeRangeInterestsMaxYear: ['0'],
        maleAgeRangeInterestsMaxMonth: ['0'],
        femaleAgeRangeInterestsMinYear: ['0'],
        femaleAgeRangeInterestsMinMonth: ['0'],
        femaleAgeRangeInterestsMaxYear: ['0'],
        femaleAgeRangeInterestsMaxMonth: ['0'],

        typePrimaryAddress: ['01', [Validators.required]],
        vendorIdPrimaryAddress: [''],
        attentionPrimaryAddress: ['', [AddressDetailValidators.attentionPattern('Attention')]],
        addressLn1PrimaryAddress: ['', [Validators.required]],
        addressLn2PrimaryAddress: [''],
        cityPrimaryAddress: ['', Validators.required],
        statePrimaryAddress: ['TX', Validators.required],
        zipPrimaryAddress: ['', [Validators.required, FadHomeComponentValidator.zipPattern('Primary Address Zip Code')]],
        zipExtPrimaryAddress: ['', [FadHomeComponentValidator.zipExtensionPattern('Primary Address Zip Code Extension')]],
        countyPrimaryAddress: ['', Validators.required],
        commentsPrimaryAddress: [''],
        schoolDistrictPrimaryAddress: [''],
        lastUpdateDatePrimaryAddress: [''],
        validatedPrimaryAddress: [false],
        validatedDatePrimaryAddress: [''],

        typeBusinessAddress: ['02', Validators.required],
        vendorIdBusinessAddress: [''],
        attentionBusinessAddress: ['', [FadHomeComponentValidator.alphaNumeric('Business Address Attention')]],
        addressLn1BusinessAddress: [''],
        addressLn2BusinessAddress: [''],
        cityBusinessAddress: [''],
        stateBusinessAddress: ['TX'],
        zipBusinessAddress: ['', [FadHomeComponentValidator.zipPattern('Business Address Zip Code')]],
        zipExtBusinessAddress: ['', [FadHomeComponentValidator.zipExtensionPattern('Business Address Zip Code Extension')]],
        countyBusinessAddress: [''],
        commentsBusinessAddress: [''],
        schoolDistrictBusinessAddress: [''],
        lastUpdateDateBusinessAddress: [''],
        validatedBusinessAddress: [false],
        validatedDateBusinessAddress: [''],

        phoneTypePrimary: ['01'],
        phonePrimary: ['', [FadHomeComponentValidator.phoneNumberPattern]],
        phoneExtPrimary: ['', [FadHomeComponentValidator.numericValues]],
        phoneCommentsPrimary: ['']
      }, {
      validators: [
        FadHomeComponentValidator.poboxValidation
      ]
    }
    );

  }

  intializeScreen() {
    this.homeService.displayHome().subscribe((response) => {
      this.displayHomeInformation = response;
      if (this.displayHomeInformation) {
        const homeTypeValues = this.displayHomeInformation.homeType;
        if (homeTypeValues && homeTypeValues.length) {
          this.homeTypeFilteredValues = homeTypeValues.filter(value => {
            if (value && value.decode === 'Relative' || value.decode === 'Fictive Kin' || value.decode === 'Unrelated') {
              return value;
            }
          });
          this.displayHomeInformation.homeType = homeTypeValues.filter(value => {
            if (value && value.decode !== 'Relative' && value.decode !== 'Fictive Kin' && value.decode !== 'Unrelated') {
              return value;
            }
          });

        }
      }
      this.loadFadHomeInfo();
    });
  }

  loadFadHomeInfo() {
    FormUtils.disableFormControlStatus(this.homeForm, ['phoneTypePrimary']);
  }

  checkMaritalStatus() {
    if (!this.isAlert && (!this.homeForm.get('maritalStatus').value && this.homeForm.get('marriageDate').value) ||
      (this.homeForm.get('maritalStatus').value &&
        this.homeForm.get('maritalStatus').value !== '01' &&
        this.homeForm.get('marriageDate').value)) {
      this.isAlert = true;
      const initialState = {
        title: 'Add Home',
        message: 'Since Marital Status is no longer "Married," the Marriage Date will be cleared upon Save..',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered', initialState
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
        if (result === true) {
          this.homeForm.controls.marriageDate.setValue('');
        }
      });
    }
  }

  saveAndAssign() {
    this.isAlert = false;
    this.checkMaritalStatus();
    if (!this.isAlert && this.isPrimaryAddressChanged) {
      this.isAlert = true;
      const initialState = {
        title: 'Address Detail',
        message: 'Please validate the Primary address before you save.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    }

    if (!this.isAlert && this.isBusinessAddressChanged) {
      this.isAlert = true;
      const initialState = {
        title: 'Address Detail',
        message: 'Please validate the Business address before you save.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    }

    if (((this.isPrimaryAddressChanged && this.isPrimaryAddressValidated) || !this.isPrimaryAddressChanged) &&
      ((this.isBusinessAddressChanged && this.isBusinessAddressValidated) || !this.isBusinessAddressChanged)) {

      if (this.validateFormGroup(this.homeForm)) {
        if (!this.homeForm.get('addressLn1BusinessAddress').value) {
          this.homeForm.controls.validatedBusinessAddress.setValue(true);
        }
        if (this.homeForm.get('stateBusinessAddress').value !== 'TX') {
          this.homeForm.controls.validatedBusinessAddress.setValue(true);
        }
        if (this.homeForm.get('statePrimaryAddress').value !== 'TX') {
          this.homeForm.controls.validatedPrimaryAddress.setValue(true);
        }

        if ((this.skipPrimaryAddressValidation || this.homeForm.get('validatedPrimaryAddress').value) &&
          (this.skipBusinessAddressValidation || this.homeForm.get('validatedBusinessAddress').value)) {
          this.homeForm.patchValue({
            childesEthnicity: this.selectedEthnicityData
          });

          this.homeForm.patchValue({
            childCharacteristics: this.selectedCharacteristicsData
          });

          const formValues: FadHomeRequest = this.homeForm.getRawValue();
          if (!this.isAlert) {
            this.homeService.saveAndAssign(formValues).subscribe(
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
        } else {
          if (!this.homeForm.get('validatedPrimaryAddress').value) {
            document.getElementById('validatePrimaryAddressDiv').getElementsByTagName('button')[0].click();
          } else if (!this.homeForm.get('validatedBusinessAddress').value) {
            document.getElementById('validateBusinessAddressDiv').getElementsByTagName('button')[0].click();
          }
        }
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
    if (addressPrimary) {      
      if (addressPrimary.isAddressAccepted) {                
        this.skipPrimaryAddressValidation = false;
        this.homeForm.patchValue({
          validatedPrimaryAddress: true,
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
                this.skipPrimaryAddressValidation = true;
                this.homeForm.patchValue({
                  zipPrimaryAddress: '',
                  zipExtPrimaryAddress: ''
                }); 
          }

          if (this.homeForm.controls.zipExtPrimaryAddress.value && 
              this.homeForm.controls.zipExtPrimaryAddress.value.length !== 4) {
                this.skipPrimaryAddressValidation = true;
                this.homeForm.patchValue({ 
                  zipExtPrimaryAddress: ''
                }); 
          }
      }
    } else {
      this.homeForm.patchValue({
        validatedPrimaryAddress: false
      });
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
    this.isPrimaryAddressValidated = true;
    this.isPrimaryAddressChanged = false;
  }

  updateAdressFieldsBusiness(addressBusiness: Address) {
    if (addressBusiness) {
      if (addressBusiness.isAddressAccepted) {                
        this.skipBusinessAddressValidation = false;
        this.homeForm.patchValue({
          validatedBusinessAddress: true,
          addressLn1BusinessAddress: addressBusiness.street1,
          addressLn2BusinessAddress: addressBusiness.street2,
          cityBusinessAddress: addressBusiness.city,
          countyBusinessAddress: addressBusiness.countyCode,
          stateBusinessAddress: addressBusiness.state,
          zipBusinessAddress: addressBusiness.zip ? (addressBusiness.zip.includes('-') ? addressBusiness.zip.split('-')[0] : '') : '',
          zipExtBusinessAddress: addressBusiness.zip ? (addressBusiness.zip.includes('-') ? addressBusiness.zip.split('-')[1] : '') : '',
        });
      } else {
          if (this.homeForm.controls.zipBusinessAddress.value && 
              this.homeForm.controls.zipBusinessAddress.value.length !== 5) {
                this.skipBusinessAddressValidation = true;
                this.homeForm.patchValue({
                  zipBusinessAddress: '',
                  zipExtBusinessAddress: ''
                }); 
          }

          if (this.homeForm.controls.zipExtBusinessAddress.value && 
              this.homeForm.controls.zipExtBusinessAddress.value.length !== 4) {
                this.skipBusinessAddressValidation = true;
                this.homeForm.patchValue({ 
                  zipExtBusinessAddress: ''
                }); 
          }
      }
    } else {
      this.homeForm.patchValue({
        validatedBusinessAddress: false
      });
    }
  }

  validateAdressFieldsBusiness() {
    const addressBusiness: Address = {
      street1: this.homeForm.controls.addressLn1BusinessAddress.value,
      street2: this.homeForm.controls.addressLn2BusinessAddress.value,
      city: this.homeForm.controls.cityBusinessAddress.value,
      county: this.homeForm.controls.countyBusinessAddress.value,
      state: this.homeForm.controls.stateBusinessAddress.value,
      zip: this.homeForm.controls.zipBusinessAddress.value,
      extension: this.homeForm.controls.zipExtBusinessAddress.value
    };
    this.dfpsBusinessAddressValidator.validate(addressBusiness);
    this.isBusinessAddressValidated = true;
    this.isBusinessAddressChanged = false;
  }

  selectedChildEthnicityValues(event) {
    this.selectedEthnicityData = [...event];
    this.homeForm.patchValue({
      childesEthnicity: this.selectedEthnicityData
    });
  }

  selectedChildCharacteristicsValues(event) {
    this.selectedCharacteristicsData = [...event];
    this.homeForm.patchValue({
      childCharacteristics: this.selectedCharacteristicsData
    });
  }

  statePrimaryAddressChange() {
    this.primaryAddressValueChange();

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

  stateBusinessAddressChange() {
    this.businessAddressValueChange();

    if (this.homeForm.get('stateBusinessAddress').value === 'TX') {
      this.homeForm.patchValue({
        countyBusinessAddress: ''
      });
    } else {
      this.homeForm.patchValue({
        countyBusinessAddress: '999'
      });
    }
  }

  countyPrimaryAddressChange() {
    this.primaryAddressValueChange();

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

  countyBusinessAddressChange() {
    this.businessAddressValueChange();

    if (this.homeForm.get('countyBusinessAddress').value === '999') {
      this.homeForm.patchValue({
        stateBusinessAddress: ''
      });
    } else {
      this.homeForm.patchValue({
        stateBusinessAddress: 'TX'
      });
    }
  }

  primaryAddressValueChange() {
    this.homeForm.patchValue({
      validatedPrimaryAddress: false,
    });
    this.isPrimaryAddressChanged = true;
    this.isPrimaryAddressValidated = false;
  }

  businessAddressValueChange() {
    this.homeForm.patchValue({
      validatedBusinessAddress: false,
    });
    this.isBusinessAddressChanged = true;
    this.isBusinessAddressValidated = false;
  }

}