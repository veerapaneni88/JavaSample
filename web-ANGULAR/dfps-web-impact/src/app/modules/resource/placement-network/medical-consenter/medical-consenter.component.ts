import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DfpsFormValidationDirective, DirtyCheck, ENVIRONMENT_SETTINGS, NavigationService, SET } from 'dfps-web-lib'; 
import { CookieService } from 'ngx-cookie-service';
import { MedicalConsenter, MedicalConsenterRes } from '../../model/resource';
import { ResourceService } from '../../service/resource.service';
import { HelpService } from 'app/common/impact-help.service';


@Component({
  templateUrl: './medical-consenter.component.html'
})
export class MedicalConsenterComponent extends DfpsFormValidationDirective implements OnInit {

  medicalConsenterForm: FormGroup;
  medicalConsenterRes: MedicalConsenterRes;
  ismcremoved = false;
  disableComments = false;
  displaySaveButton = true;
  displayCancelButton = true;
  displayPersonSEarchButton = true;
  ssccResourceId: any;
  hideCancelButton = false;
  medicalConsenterName: any;
  personName: string;
  memberResourceId: any; 
  ssccPlacmentLinkId: any;
  personId: any;

  constructor(
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    private activatedRoute: ActivatedRoute,
    private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private resourceService: ResourceService,
    private cookieService: CookieService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
    this.ssccResourceId = this.activatedRoute.snapshot.paramMap.get('ssccResourceId');
    this.memberResourceId = this.activatedRoute.snapshot.paramMap.get('memberResourceId');
    this.ssccPlacmentLinkId = this.activatedRoute.snapshot.paramMap.get('ssccPlacmentLinkId');
  }

  ngOnInit(): void {
    this.medicalConsenterForm = this.formBuilder.group({
    });
    this.navigationService.setTitle('Medical Consenter');
    this.helpService.loadHelp('Medical');
    this.createForm();
    this.intializeScreen();
    this.loadPersonDetails();
    if (this.ssccPlacmentLinkId > 0) {
      this.displayPersonSEarchButton = false;
    }
  }

  createForm() {
    this.medicalConsenterForm = this.formBuilder.group(
      {
        comments: ['', Validators.required]
      }
    );
  }

  intializeScreen() {
    this.loadFormData();
  }

  loadFormData() {
    this.resourceService.getMedicalConsenterDetailsRes(this.ssccResourceId, this.ssccPlacmentLinkId)
      .subscribe(
        response => {
          this.medicalConsenterRes = response;
          if (this.medicalConsenterRes.medicalConsenter) {
            this.ismcremoved = this.medicalConsenterRes.medicalConsenter.mcremoved;
            this.disableComments = this.medicalConsenterRes.medicalConsenter.mcremoved;
            this.displaySaveButton = !this.ismcremoved;
            this.personName = this.medicalConsenterRes.medicalConsenter.name;
            this.personId = this.medicalConsenterRes.medicalConsenter.personId;
            this.medicalConsenterForm.setValue({
              comments: this.medicalConsenterRes.medicalConsenter?.comments
            })
          }
        }
      );
  }

  loadPersonDetails() {
    const localPersonId: any = this.environmentSettings.environmentName === 'Local'
      ? this.cookieService.get('personId') : sessionStorage.getItem('personId');
    const localPersonName: any = this.environmentSettings.environmentName === 'Local'
      ? this.cookieService.get('personName') : sessionStorage.getItem('personName');
    const localmemberResourceId: any = this.environmentSettings.environmentName === 'Local'
      ? this.cookieService.get('memberResourceId') : sessionStorage.getItem('memberResourceId');
    this.personName = localPersonName;
    this.personId = localPersonId;
    this.memberResourceId = localmemberResourceId;
    this.cookieService.delete('personId');
    sessionStorage.removeItem('personId');
    this.cookieService.delete('personName');
    sessionStorage.removeItem('personName');
  }

  save() {
    if (this.validateFormGroup(this.medicalConsenterForm)) {
      const formValues: MedicalConsenter = this.medicalConsenterForm.getRawValue();
      formValues.ssccPlacmentLinkId = this.ssccPlacmentLinkId;
      formValues.name = this.personName;
      formValues.personId = this.personId;
      formValues.resourceId = this.ssccResourceId;
      formValues.memberResourceId = this.memberResourceId;
      this.resourceService.saveMedicalConsenterDetails(formValues
      ).subscribe(
        res => {
          if (res) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            window.location.href = this.environmentSettings.impactP2WebUrl + '/resource/placementNetwork/display/' + this.memberResourceId;
          }
        }
      );
    }
  }

  cancel() {
    window.location.href = this.environmentSettings.impactP2WebUrl + '/resource/placementNetwork/display/' + this.memberResourceId;
  }

  searchPerson() {
    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/person/search';
  }

}