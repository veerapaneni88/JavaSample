import {Component, Inject, LOCALE_ID, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  ENVIRONMENT_SETTINGS,
  NavigationService,
  SET
} from 'dfps-web-lib';
import {ResourceService} from '../../service/resource.service';
import {ActivatedRoute} from '@angular/router';
import {
  ServicePackageHistoryRes,
  ServicePackageCredential,
  ServicePackageCredentialDetails
} from '../../model/resource';
import {Store} from '@ngrx/store';
import {HelpService} from '../../../../common/impact-help.service';
import {BsModalService} from 'ngx-bootstrap/modal';

@Component({
  selector: 'service-package-credentials',
  templateUrl: './service-package-credentials.component.html'
})
export class ServicePackageCredentialsComponent extends DfpsFormValidationDirective implements OnInit {

  servicePackageCredentialsForm: FormGroup;
  resourceId: any;
  servicePackageHistoryId: any;
  effectiveDate: any;
  endDate: any;
  facilityServicePackageRes: ServicePackageHistoryRes;
  groCodeTypes: string[];
  ffcCodeTypes: string[];
  codeTypes: string[];
  facilityServicePackageDetails: ServicePackageCredential;
  servicePackageCredentialFromDB: ServicePackageCredentialDetails[];
  isEditMode = false;
  servicePackageDetails: any;
  attentionMsg: string;
  displayAttentionMsg = false;


  constructor(private formBuilder: FormBuilder,
              private activatedRoute: ActivatedRoute,
              private resourceService: ResourceService,
              private navigationService: NavigationService,
              private modalService: BsModalService,
              private helpService: HelpService,
              @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
              @Inject(LOCALE_ID) private locale: string,
              public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Service Package Credentials')
    this.helpService.loadHelp('Resource')
    this.groCodeTypes = ["CG1SSCSP", "CG1DFSSP", "CG2SSCSP", "CG2DFSSP"]
    this.ffcCodeTypes = ["CFCSSCSP", "CFFCDFSP"]
    this.createForm()
    this.resourceId = this.activatedRoute.snapshot.paramMap.get('resourceId')
    this.servicePackageHistoryId = this.activatedRoute.snapshot.paramMap.get('servicePackageHistoryId')
    this.effectiveDate = localStorage.getItem('serviceEffectiveDate')
    this.initializeScreen(false);
  }

  createForm() {
    this.servicePackageCredentialsForm = this.formBuilder.group({
      servicePackages: new FormArray([])
    });
  }

  initializeScreen(isSave: boolean) {
    this.resourceService.getFacilityServicePackageHistory(this.resourceId, this.servicePackageHistoryId, this.effectiveDate).subscribe(facilityServicePackageRes => {
      this.facilityServicePackageRes = facilityServicePackageRes
      this.endDate = facilityServicePackageRes.facilityServicePackageDetails?.endDate
      this.effectiveDate = !this.effectiveDate ? facilityServicePackageRes.facilityServicePackageDetails?.effectiveDate : this.effectiveDate
      this.facilityServicePackageDetails = facilityServicePackageRes.facilityServicePackageDetails
      this.servicePackageCredentialFromDB = []
      facilityServicePackageRes.facilityServicePackageDetails.servicePackageCredentialDetails.forEach((item)=> {
         this.servicePackageCredentialFromDB.push(item)
        })
      this.initializeServicePackages(isSave);
      this.getPageMode();
    });
  }


  initializeServicePackages(isSave: boolean) {
    this.codeTypes = this.facilityServicePackageRes.facilityServicePackageDetails.facilityGroup === 'FFC' ? this.ffcCodeTypes : this.groCodeTypes
    const servicePackagesControl = <FormArray>this.servicePackageCredentialsForm.get('servicePackages')
    if (!isSave) {
      this.codeTypes.forEach(codeType => {
        servicePackagesControl.push(this.createServicePackageListForm(codeType))
      })
    }
    if (this.facilityServicePackageRes?.facilityServicePackageDetails) {
      this.servicePackageCredentialsForm.setControl('servicePackages', this.setSelectedServicePackages(this.facilityServicePackageRes.facilityServicePackageDetails.servicePackageCredentialDetails));

    }
  }

  setSelectedServicePackages(servicePackageDetails: ServicePackageCredentialDetails[]): FormArray {
    const servicePackagesControl = <FormArray>this.servicePackageCredentialsForm.get('servicePackages')
    servicePackageDetails.forEach((servicePkgDtl) => {
      let formIndex = 0
      this.codeTypes.forEach((codeType: string) => {
        let codeIndex = this.getServicePackageCodeIndex(codeType, servicePkgDtl.servicePackage)
        if (codeIndex != -1) {
          servicePackagesControl['controls'][formIndex]['controls'].packageList['controls'][codeIndex].get('id').setValue(servicePkgDtl.id)
          servicePackagesControl['controls'][formIndex]['controls'].packageList['controls'][codeIndex].get('servicePackageId').setValue(servicePkgDtl.servicePackageId)
          servicePackagesControl['controls'][formIndex]['controls'].packageList['controls'][codeIndex].get('status').setValue(servicePkgDtl.status)
          if (servicePkgDtl.status === 'ACT' || servicePkgDtl.status === 'HLD') {
            servicePackagesControl['controls'][formIndex]['controls'].packageList['controls'][codeIndex].get('statusDropdown').setValue(this.facilityServicePackageRes.serviceActivePackageStatuses)
          }
        }
        formIndex++
      });
    });
    return servicePackagesControl
  }

  createServicePackageListForm(codeType) {
    let packageListForm: FormGroup
    const packageListFormGroup = this.facilityServicePackageRes.servicePackages[codeType].map(obj => {
      return this.formBuilder.group({
        id: [0],
        servicePackageId: [0],
        code: [obj.code],
        decode: [obj.decode],
        status: ['NTA'],
        statusDropdown: [this.facilityServicePackageRes.servicePackageStatuses]
      })
    });
    packageListForm = this.formBuilder.group({
      packageType: [codeType],
      packageList: new FormArray([])
    });

    const packageListFormArray: FormArray = this.formBuilder.array(packageListFormGroup)
    packageListForm.setControl('packageList', packageListFormArray)
    return packageListForm
  }

  getServicePackages(servicePackageCredentialsForm) {
    return servicePackageCredentialsForm.controls.servicePackages.controls
  }


  getServicePackageList(servicePackageCredentialsForm, codeType) {
    const servicePackages = <FormArray>this.servicePackageCredentialsForm.get('servicePackages');
    for (let index = 0; index <= servicePackages.controls.length; index++) {
      if (servicePackageCredentialsForm.controls.servicePackages.controls[index]?.controls.packageType.value === codeType) {
        return servicePackageCredentialsForm.controls.servicePackages.controls[index].controls.packageList.controls
      }
    }
  }

  getServicePackageCodeIndex(codeType, packageCode) {
    return this.facilityServicePackageRes.servicePackages[codeType].findIndex(obj => obj.code === packageCode)
  }

  servicePackageSelectionChange(status, code, id) {
    let pkg = {id: id, servicePackageId: this.servicePackageHistoryId, servicePackage: code, status: status}
    let foundIndex = this.facilityServicePackageDetails?.servicePackageCredentialDetails.findIndex(x => x.servicePackage == code)
    if (foundIndex != -1) {
      this.facilityServicePackageDetails.servicePackageCredentialDetails.splice(foundIndex, 1)
    }
    this.facilityServicePackageDetails.servicePackageCredentialDetails.push(pkg)
  }

  showErrorModal(errorMessage) {
    const initialState = {
      message: errorMessage,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md modal-dialog-centered',
      initialState,
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
    });
  }

  save() {
    this.facilityServicePackageDetails.id = this.servicePackageHistoryId
    this.facilityServicePackageDetails.resourceId = this.resourceId
    this.facilityServicePackageDetails.effectiveDate = this.effectiveDate
    const payload: ServicePackageCredential = Object.assign(this.facilityServicePackageRes.facilityServicePackageDetails || {}, this.facilityServicePackageDetails);
    if (!Number(this.facilityServicePackageRes.facilityServicePackageDetails.id)) {
      payload.servicePackageCredentialDetails.map(x => x.id = 0);

    }
    payload.facilityType = this.facilityServicePackageRes.facilityType;
    const isValidRequest = payload.servicePackageCredentialDetails
      .some((servicePackage) => servicePackage.status !== 'NTA');

    if (!isValidRequest) {
      this.showErrorModal('You are attempting to save with no status changes. Before saving, you must\n' +
        'make a change');
      return;
    }
    else if ( !Number(this.facilityServicePackageRes.facilityServicePackageDetails.id)
      && this.facilityServicePackageDetails.servicePackageCredentialDetails
        .filter(item=>item.status != 'NTA' )
        .every(item=>
                this.servicePackageCredentialFromDB.some(x =>
                  x.servicePackage == item.servicePackage &&  x.status == item.status
                )))
    {
        this.showErrorModal(
          'You are attempting to save with no status changes. Before saving, you must'+
          ' make a change to the Service Package History or Navigate back to Facility'+
          ' Detail Page if you selected "Add" in error.');
        return;
    }

    this.resourceService.saveFacilityServicePackage(payload).subscribe(
      response => {
        if (response) {
          this.displayAttentionMsg = !response.isSsccDfpsCredentialsMatched;
          sessionStorage.setItem('cacheKey', response.cacheKey);
          if (this.displayAttentionMsg) {
            this.attentionMsg = 'Save Completed: Equivalent SSCC and DFPS service packages should be saved with the same credential status. Review before closing.';
          }
          this.store.dispatch(SET({dirtyCheck: {isDirty: false}}))
          this.resourceId = this.activatedRoute.snapshot.paramMap.get('resourceId');
          this.servicePackageHistoryId = response.id;
          this.initializeScreen(true);

        }
      }
    );
  }

  getPageMode() {
    if (this.facilityServicePackageRes.pageMode === 'EDIT') {
      this.isEditMode = true;
    } else {
      const servicePackages = <FormArray>this.servicePackageCredentialsForm.get('servicePackages');
      servicePackages.value.forEach((form, index) => {
        servicePackages.controls[index].disable();
      });
    }
  }
}
