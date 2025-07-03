import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck, ENVIRONMENT_SETTINGS, NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ResourceService } from '../service/resource.service';
import { ResourceCharacterDetailRes, ResourceCharacter } from './../model/resource';
import { ClientCharactersticsDetailValidators } from './client-characterstics-detail.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  templateUrl: './client-characteristics-detail.component.html'
})
export class ClientCharacteristicsDetailComponent extends DfpsFormValidationDirective implements OnInit {
  clientCharacteristicsDetailForm: FormGroup;
  resourceCharacterDetailRes: ResourceCharacterDetailRes;
  resourceCharacter: ResourceCharacter;
  displayDeleteButton = false;
  displaySaveButton = false;
  resourceId: any;
  resourceServiceId: any;
  resourceCharacterId: any;

  constructor(
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    private activatedRoute: ActivatedRoute,
    private route: Router,
    private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private resourceService: ResourceService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
    this.resourceId = this.activatedRoute.snapshot.paramMap.get('resourceId');
    this.resourceServiceId = this.activatedRoute.snapshot.paramMap.get('resourceServiceId');
    this.resourceCharacterId = this.activatedRoute.snapshot.paramMap.get('resourceCharacterId');
  }
  ngOnInit(): void {
    this.navigationService.setTitle('Client Characteristics Detail');
    this.helpService.loadHelp('Resource');
    this.navigationService.setUserDataValue('firstLevelTab', 'Resource');
    this.navigationService.setUserDataValue('idResource', this.resourceId);
    this.createForm();
    this.intializeScreen();
  }
  createForm() {
    this.clientCharacteristicsDetailForm = this.formBuilder.group({
      character: ['', [Validators.required]],
      maleMaxYear: ['', [ClientCharactersticsDetailValidators.yearPattern('Male Max Year')]],
      maleMinYear: ['', [ClientCharactersticsDetailValidators.yearPattern('Male Min Year')]],
      maleMaxMonth: ['', [ClientCharactersticsDetailValidators.monthPattern('Male Max Month')]],
      maleMinMonth: ['', [ClientCharactersticsDetailValidators.monthPattern('Male Min Month')]],
      femaleMaxYear: ['', [ClientCharactersticsDetailValidators.yearPattern('Female Max Year')]],
      femaleMinYear: ['', [ClientCharactersticsDetailValidators.yearPattern('Female Min Year')]],
      femaleMaxMonth: ['', [ClientCharactersticsDetailValidators.monthPattern('Female Max Month')]],
      femaleMinMonth: ['', [ClientCharactersticsDetailValidators.monthPattern('Female Min Month')]]
    }, {
      validators: ClientCharactersticsDetailValidators.validateCharacteristics
    });
  }

  intializeScreen() {
    this.loadFormData();
  }
  loadFormData() {
    this.resourceService.getClientCharacteristicDetailRes(this.resourceId, this.resourceServiceId, this.resourceCharacterId)
      .subscribe(
        response => {
          this.resourceCharacterDetailRes = response;
          this.resourceCharacter = this.resourceCharacterDetailRes.resourceCharacter;
          if (response.resourceCharacter) {
            this.clientCharacteristicsDetailForm.patchValue({
              character: this.resourceCharacter.character,
              maleMaxYear: this.resourceCharacter.maleMaxYear,
              maleMinYear: this.resourceCharacter.maleMinYear,
              maleMaxMonth: this.resourceCharacter.maleMaxMonth,
              maleMinMonth: this.resourceCharacter.maleMinMonth,
              femaleMaxYear: this.resourceCharacter.femaleMaxYear,
              femaleMinYear: this.resourceCharacter.femaleMinYear,
              femaleMaxMonth: this.resourceCharacter.femaleMaxMonth,
              femaleMinMonth: this.resourceCharacter.femaleMinMonth,
              lastUpdatedDate: this.resourceCharacter.lastUpdatedDate,});
          }
          this.setPageModes();
        }
      )
  }
  
  save() {
    if (this.validateFormGroup(this.clientCharacteristicsDetailForm)) {
      const formValues: ResourceCharacter = this.clientCharacteristicsDetailForm.getRawValue();
      formValues.resourceId = this.resourceId;
      formValues.serviceId = this.resourceServiceId;
      formValues.id = this.resourceCharacterId;
      this.resourceService.saveClientCharactersiticsDetail(this.resourceId, this.resourceServiceId, formValues
      ).subscribe(
        res => {
          if (res) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.route.navigate(['/resource/services-by-area/' + this.resourceId]);
          }
        }
      );
    }
  }
  delete() {
    const initialState = {
      title: 'Client Characteristics Detail',
      message: 'Are you sure you want to delete this record?',
      showCancel: true};
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md', initialState});
    (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
      if (result === true) {
        this.resourceService.deleteCharactersticsDetail(this.resourceId, this.resourceServiceId, this.resourceCharacterId).subscribe(
          res => {
            if (res) {
              this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
              this.route.navigate(['/resource/services-by-area/' + this.resourceId]);
            }
          }
        );
      }
    });
  }

  setPageModes() {
    if (this.resourceCharacterDetailRes.pageMode === 'NEW') {
      this.displaySaveButton = true;
      this.displayDeleteButton = false;
    } else if (this.resourceCharacterDetailRes.pageMode === 'EDIT') {
      this.displaySaveButton = true;
      this.displayDeleteButton = true;
    } else if (this.resourceCharacterDetailRes.pageMode === 'VIEW') {
      this.clientCharacteristicsDetailForm.disable();
      this.displayDeleteButton = false;
      this.displaySaveButton = false;
    }
    if (this.resourceCharacterDetailRes.resourceCharacter 
      && this.resourceCharacterDetailRes.resourceCharacter.id < 1) {
      this.displayDeleteButton = false;
    }
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }
}