import { Component, HostListener, OnDestroy, OnInit,  ViewChild, Input } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    Address,
    DfpsAddressValidatorComponent, 
    DfpsCommonValidators,
    DfpsFormValidationDirective,
    DfpsConfirmComponent, 
    DirtyCheck, 
    DropDown, 
    FormUtils, 
    NavigationService, 
    SET
} from 'dfps-web-lib';
import { DisplayAddressDetail } from '../../../model/addressDetail';
import { CaseService } from '../../../service/case.service';
import { AddressDetailValidators } from './address-detail.validator';
import { BsModalService } from 'ngx-bootstrap/modal';

@Component({
    selector: 'address-detail',
    templateUrl: '././address-detail.component.html'
})
export class KinAddressDetailComponent extends DfpsFormValidationDirective implements  OnInit, OnDestroy {

    @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;

    addressDetailForm: FormGroup;
    resourceId: number;
    resourceAddressId: number;
    displayAddressDetail: DisplayAddressDetail;
    schoolDistricts: DropDown[] = [];
    isAddressValidated: boolean;
    isAddressChanged: boolean;

    constructor(private navigationService: NavigationService,
        private formBuilder: FormBuilder,
        private caseService: CaseService,
        private route: ActivatedRoute,
        private modalService: BsModalService,
        private router: Router,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }

    createForm() {
        this.addressDetailForm = this.formBuilder.group(
            {
                addressType: ['', Validators.required],
                vendorId: [''],
                attention: ['',[AddressDetailValidators.attentionPattern('Attention')]],
                addressLine1: ['', Validators.required],
                addressLine2: [''],
                city: ['', [Validators.required, DfpsCommonValidators.cityPattern]],
                state: ['TX'],
                zip: ['', [Validators.required,DfpsCommonValidators.zipPattern]],
                zipExt: ['',[DfpsCommonValidators.zipExtensionPattern]],
                county: ['', Validators.required],
                comments: [''],
                schoolDistrict: [''],
                dtLastUpdate: [''],
                addressTypeBusiness: ['']
            }, {
            validators: [
                AddressDetailValidators.vendorIdValidation,
                AddressDetailValidators.poboxValidation
            ]
        }
        );
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Address Detail');
        const routeParams = this.route.snapshot.paramMap;
        if (routeParams) {
            this.resourceId = Number(routeParams.get('resourceId'));
            this.resourceAddressId = Number(routeParams.get('resourceAddressId'));
        }
        this.intializeScreen();
        this.createForm();
    }

    intializeScreen() {
        this.caseService.getAddressList(this.resourceId, this.resourceAddressId).subscribe((response) => {
            this.displayAddressDetail = response;
            this.loadAddressDetail();
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        });
    }

    loadAddressDetail() {
        if (this.displayAddressDetail && this.displayAddressDetail.resourceAddress) {
            const zip = this.displayAddressDetail.resourceAddress.zip;
            this.addressDetailForm.patchValue({
                addressType: this.displayAddressDetail.resourceAddress.addressType,
                vendorId: this.displayAddressDetail.resourceAddress.vendorId,
                attention: this.displayAddressDetail.resourceAddress.attention,
                addressLine1: this.displayAddressDetail.resourceAddress.addressLine1,
                addressLine2: this.displayAddressDetail.resourceAddress.addressLine2,
                city: this.displayAddressDetail.resourceAddress.city,
                state: this.displayAddressDetail.resourceAddress.state,
                zip: zip ? (zip.includes('-') ? zip.split('-')[0] : zip) : '',
                zipExt: zip ? (zip.includes('-') ? zip.split('-')[1] : '') : '',
                county: this.displayAddressDetail.resourceAddress.county,
                comments: this.displayAddressDetail.resourceAddress.comments,
                schoolDistrict: this.displayAddressDetail.resourceAddress.schoolDistrict,
                dtLastUpdate: this.displayAddressDetail.resourceAddress.dtLastUpdate
            });
            this.addressDetailForm.patchValue({ addressTypeBusiness: this.displayAddressDetail.resourceAddress.addressType });

            if (this.displayAddressDetail.resourceAddress.addressType === '01') {
                FormUtils.disableFormControlStatus(this.addressDetailForm, ['addressType']);
            } else {
                this.displayAddressDetail.addressType = this.displayAddressDetail.addressType.filter(e => e.code !== '01');
                FormUtils.disableFormControlStatus(this.addressDetailForm, ['schoolDistrict']);
            }
            this.loadSchoolDistricts();
        }
        if (this.displayAddressDetail && this.displayAddressDetail.removePrimaryAddressType && !this.displayAddressDetail.resourceAddress) {
            this.displayAddressDetail.addressType = this.displayAddressDetail.addressType.filter(e => e.code !== '01');
        }
    }

    loadSchoolDistricts() {
        if (this.addressDetailForm.get('addressType').value === '01') {
            const selectedCounty = this.addressDetailForm.get('county').value;
            this.schoolDistricts = [];
            this.displayAddressDetail.schoolDistrict.forEach(sd => {
                if (sd.schoolDistrictTxCountyCode === selectedCounty) {
                    this.schoolDistricts.push({
                        code: sd.schoolDistrictCode,
                        decode: sd.schoolDistrictTxtName
                    })
                }
            });
        } else {
            FormUtils.disableFormControlStatus(this.addressDetailForm, ['schoolDistrict']);
        }
    }

    updateAdressFields(address: Address) {
        if (address) {
            if (address.isAddressAccepted) {                
                this.addressDetailForm.patchValue({
                    addressLine1: address.street1,
                    addressLine2: address.street2,
                    city: address.city,
                    county: address.countyCode,
                    state: address.state,
                    zip: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[0] : '') : '',
                    zipExt: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[1] : '') : '',
                });
            } else {
                if (this.addressDetailForm.controls.zip.value && 
                    this.addressDetailForm.controls.zip.value.length !== 5) {
                        this.addressDetailForm.patchValue({
                            zip: '',
                            zipExt: ''
                        }); 
                }

                if (this.addressDetailForm.controls.zipExt.value && 
                    this.addressDetailForm.controls.zipExt.value.length !== 4) {
                        this.addressDetailForm.patchValue({ 
                            zipExt: ''
                        }); 
                }
            }
        }
    }

    validateAdressFields() {
        const address: Address = {
            street1: this.addressDetailForm.controls.addressLine1.value,
            street2: this.addressDetailForm.controls.addressLine2.value,
            city: this.addressDetailForm.controls.city.value,
            county: this.addressDetailForm.controls.county.value,
            state: this.addressDetailForm.controls.state.value,
            zip: this.addressDetailForm.controls.zip.value,
            extension: this.addressDetailForm.controls.zipExt.value,
        };
        this.dfpsAddressValidator.validate(address);
        this.isAddressValidated = true;
        this.isAddressChanged = false;
    }

    doSave() {
        if (this.isAddressChanged) {
            const initialState = {
                title: 'Address Detail',
                message: 'Please validate the address before you save.',
                showCancel: false
              };
              const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
              (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
        }  
          if ((this.isAddressChanged && this.isAddressValidated) || !this.isAddressChanged) {
            if (this.validateFormGroup(this.addressDetailForm)) {
                this.saveAddressDetail();
            }
        }
    }

      
    onAddressChange(event: any) {            
        this.isAddressChanged =  true;
        this.isAddressValidated = false;         
    }


    stateChange(event: any) {
        this.isAddressChanged =  true;
        this.isAddressValidated = false; 
    
        if (this.addressDetailForm.controls.state.value === 'TX') {
          this.addressDetailForm.patchValue({
              county: ''
          });          
        } else{
            this.addressDetailForm.patchValue({
                county: '999'
            }); 
        }
      }

    saveAddressDetail() {    
        const payload = Object.assign(this.displayAddressDetail.resourceAddress || {}, this.addressDetailForm.value);
        payload.resourceId = this.resourceId;
        this.caseService.saveAddressDetail(this.resourceId, payload).subscribe(
            response => {
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                const currentUrl = this.router.url.split('/address')[0]
                this.router.navigate([currentUrl]);
                const addressDetail = true;
                localStorage.setItem('addressDetailSave', JSON.stringify(addressDetail));
            }
        );
    }

    @HostListener('window:popstate' || 'window:hashchange', ['$event'])
    onPopState(event) {
        localStorage.setItem('addressList', JSON.stringify({}));
        const addressLocalStorge = JSON.parse(localStorage.getItem('addressList'));
        addressLocalStorge.focusItem = this.router.url;
        localStorage.setItem('backFrom', JSON.stringify({ naivgatedBackFrom: 'addressDetail' }));
        localStorage.setItem('addressList', JSON.stringify(addressLocalStorge));
    }
}