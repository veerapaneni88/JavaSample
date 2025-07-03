import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store'; 
import { PersonTrainingService } from '../service/persontraining.service';
import { PersonTrainingRes, PersonTraining } from './../model/persontraining';
import { CookieService } from 'ngx-cookie-service';
import {
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
import { TrainingDetailValidator } from './training-detail.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
    templateUrl: './training-detail.component.html'
})
export class TrainingDetailComponent extends DfpsFormValidationDirective implements OnInit {

  trainingDetailForm: FormGroup; 
  personTrainingRes: PersonTrainingRes;
  personTraining: PersonTraining; 
  
  displayDeleteButton = false;
  displaySaveButton = false;

  personId: any;
  trainingId: any;

  trainingTypeDropDown: DropDown[] = [];

  constructor(
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
      private route: ActivatedRoute,
      private router: Router,
      private navigationService: NavigationService,
      private formBuilder: FormBuilder, 
      private modalService: BsModalService,
      private personTrainingService: PersonTrainingService,
      private cookieService: CookieService,
      private helpService: HelpService,
      public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
    this.trainingId = this.route.snapshot.paramMap.get('trainingId');
    this.personId = this.route.snapshot.paramMap.get('personId');     
    
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Home Member Training Detail'); 
    this.helpService.loadHelp('Case');

    this.createForm();
    this.intializeScreen(); 
    this.cookieService.set('prePage', 'FAHomeTraining');
    sessionStorage.setItem('prePage', 'FAHomeTraining');    
  }

  createForm() {
    this.trainingDetailForm = this.formBuilder.group({      
      title: ['', [Validators.required]],
      type: ['', [Validators.required]],
      trainingDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
      evaluationComponent: [false],
      hours: ['0.0', [TrainingDetailValidator.validateHours]],
      trainingSession: ['', [TrainingDetailValidator.validateSession]], 
      kinTrainingCompleted: [false],
      lastUpdatedDate:['']
    }, {
      validators: TrainingDetailValidator.validateDate
    });
  }

  intializeScreen() {  
    this.loadFormData();
  }

  loadFormData() {       
    this.personTrainingService.getPersonTrainingRes(this.personId, this.trainingId)
      .subscribe(
        response => {
          this.personTrainingRes = response;
          this.personTraining = this.personTrainingRes.personTraining;  
          if (response.personTraining) {
            this.trainingDetailForm.patchValue({
              title: this.personTraining.title,
              type: this.personTraining.type,
              trainingDate: this.personTraining.trainingDate,
              evaluationComponent: this.personTraining?.evaluationComponent === 'Y' ? true : false,
              kinTrainingCompleted: this.personTraining?.kinTrainingCompleted === 'Y' ? true : false,
              hours: (this.personTraining.hours || 0).toFixed(1),
              trainingSession: this.personTraining.trainingSession,
              lastUpdatedDate: this.personTraining.lastUpdatedDate
            });
          }
          this.setPageModes();
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        }
      ) 
  }

  setPageModes() {
    if (this.personTrainingRes.pageMode === 'NEW') {
      this.displaySaveButton = true;
      this.displayDeleteButton = false; 
    }else if (this.personTrainingRes.pageMode === 'EDIT') {
      this.displaySaveButton = true;
      this.displayDeleteButton = true; 
    } else if (this.personTrainingRes.pageMode === 'VIEW') {
      this.trainingDetailForm.disable();
      this.displayDeleteButton = false;
      this.displaySaveButton = false;
    } 
  }

  save() {
    const formValue = this.trainingDetailForm.get('hours').value;
    const hoursValue = formValue.split('.')[0];
    if ((hoursValue && hoursValue.length > 1) && (formValue && formValue.includes('.'))) {
      this.trainingDetailForm.patchValue({ hours: hoursValue });
    }

    if (this.validateFormGroup(this.trainingDetailForm)) {    
     const formValues: PersonTraining = this.trainingDetailForm.getRawValue();
      formValues.personId = this.personId;
      formValues.id = this.trainingId;
      this.personTrainingService.savePersonTraining(this.personId, formValues
      ).subscribe(
        res => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          if (res) {
            window.location.href= this.environmentSettings.impactP2WebUrl + '/case/personDetail/' + this.personId+'?prePage=FAHomeTraining';
          }
        }
      );
    }
  }

  delete() {
    const initialState = {
      title: 'Home Member Training Detail',
      message: 'Are you sure you want to delete this information?',
      showCancel: true
    };
    const modal = this.modalService.show(DfpsConfirmComponent, {
      class: 'modal-md', initialState
    });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
      if (result === true) {
        this.personTrainingService.deletePersonTrainingRes(this.personId, this.trainingId).subscribe(
            res => {
              if (res) {
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                window.location.href= this.environmentSettings.impactP2WebUrl + '/case/personDetail/' + this.personId;
              }
            }
          );
      }
    });
  }  

} 