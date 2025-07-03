import { Component, Inject, LOCALE_ID, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DfpsFormValidationDirective, DirtyCheck, NavigationService, SET } from 'dfps-web-lib';
import { AddServicePackageCredentialsValidators } from './add-service-package-credentials.validator';
import { ResourceService } from '../../service/resource.service';
import { ServicePackageHistoryRes } from '../../model/resource';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'add-service-package-credentials',
  templateUrl: './add-service-package-credentials.component.html'
})
export class AddServicePackageCredentialsComponent extends DfpsFormValidationDirective implements OnInit {

  addServicePackageCredentialsForm: FormGroup;
  resourceId: any;
  effectiveDate: any;
  facilityServicePackageRes: ServicePackageHistoryRes;

  constructor(
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private helpService: HelpService,
    private router: Router,
    private navigationService: NavigationService,
    @Inject(LOCALE_ID) private locale: string,
    public store: Store<{ dirtyCheck: DirtyCheck }>,
    private resourceService: ResourceService
  ) {
    super(store);
    this.resourceId = this.activatedRoute.snapshot.paramMap.get('resourceId');
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Add Service Package Credentials');
    this.helpService.loadHelp('Resource');
    this.createForm();
    this.initializeResourceData();
  }

  createForm() {
    this.addServicePackageCredentialsForm = this.formBuilder.group(
      {
        effectiveDate: [''],
        locale: [this.locale],
        previousEffectiveDate: [''],
        facilityType: '',
        facilityTypeValid: false,
        cpaEffectiveDate: ''
      }, {
      validators: [
        AddServicePackageCredentialsValidators.validateEffectiveDate
      ]
    }
    );
  }

  initializeResourceData() {
    this.resourceService.getFacilityServicePackageHistory(this.resourceId, 0).
      subscribe(response => {
        this.facilityServicePackageRes = response;
        this.addServicePackageCredentialsForm.patchValue(
          { previousEffectiveDate: response?.facilityServicePackageDetails?.effectiveDate })
        this.addServicePackageCredentialsForm.patchValue(
          { facilityType: response?.facilityType })
        this.addServicePackageCredentialsForm.patchValue(
          { facilityTypeValid: response?.facilityTypeValid })
        this.addServicePackageCredentialsForm.patchValue(
          { cpaEffectiveDate: response?.recentAgencyServicePackageDetails?.cpaEffectiveDate })
      });
  }

  doContinue() {
    if (this.validateFormGroup(this.addServicePackageCredentialsForm)) {
      this.effectiveDate = this.addServicePackageCredentialsForm.controls.effectiveDate.value;
      localStorage.setItem('serviceEffectiveDate', this.effectiveDate);
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      this.router.navigate(['/resource/facility-detail/service-package-credentials/' + this.resourceId + '/0'],
        { queryParams: { effectiveDate: this.addServicePackageCredentialsForm.controls.effectiveDate.value } });
    }
  }

}
