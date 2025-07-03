import { Component, Inject, OnDestroy, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
  Address, DfpsAddressValidatorComponent, DfpsConfirmComponent, DfpsFormValidationDirective, DirtyCheck,
  ENVIRONMENT_SETTINGS, NavigationService, SET, FormUtils
} from 'dfps-web-lib';
import { saveAs } from 'file-saver';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { Subscription } from 'rxjs';
import { CaseService } from '../../service/case.service';
import { InvestigationLetter, InvestigationLetterRes, Person } from '../../model/InvestigationLetter';
import { ApRightsPriorToInterviewValidators } from './ap-rights-prior-to-interview.validator';
import { map, take } from 'rxjs/operators';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'ap-rights-prior-to-interview',
  templateUrl: './ap-rights-prior-to-interview.component.html'
})
export class ApRightsPriorToInterviewComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('searchApButton', { read: ElementRef}) searchApButtonRef;

  modalSubscription: Subscription;
  investigationLetter: InvestigationLetter;
  investigationLetterForm: FormGroup;
  investigationLetterRes: InvestigationLetterRes;
  selectedAP: Person;
  formOrgValue: string;

  hideAttentionMessage = false;
  hideDeleteButton = false;
  hideDownloadDraftButton = false;
  hideDownloadButton = false;
  hideSaveSubmitButton = false;
  hideSaveButton = false;
  hideValidateButton = false;
  hideSearchButton = false;
  letterType: string;
  letterTypeDecode: string;
  isAddressValidated: boolean;
  isAddressChanged: boolean;
  errorNoDataMessage: any;
  hideReceiptNumber = true;
  disableDeleteButton = false;
  disableDownloadDraftButton = false;
  isPageDirty = false;
  storeSub: Subscription;


  constructor(
    private cookieService: CookieService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private modalService: BsModalService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private searchService: SearchService,
    private caseService: CaseService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
      super(store);
      if (this.navigationService.getPreviousUrl() && this.navigationService.getPreviousUrl().endsWith('/letters/investigation-letters')) {
        this.searchService.setFormData(null);
        this.searchService.setFormContent(null);
      }
  }

  ngOnDestroy() {
    if (this.storeSub) {
      this.storeSub.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
    this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    if(this.router.url.includes('/ARS')) {
      this.letterType = 'ARS';
      this.navigationService.setTitle('APs Rights Prior to Interview (Spanish) - 2617s');
    } else {
      this.letterType = 'ARP';
      this.navigationService.setTitle('APs Rights Prior to Interview - 2617');
    }

    const selectedPersonId = this.route.snapshot.queryParamMap.get('selectedPersonId');

    var isFormChanged = false;
    this.caseService.getInvestigationLetter(investigationLetterId).pipe(take(1), map((response) => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;
      this.investigationLetter.apHomePhoneNumber = this.investigationLetter.allegedPerpetratorPhoneNumber;
      this.investigationLetter.letterType = this.letterType;

      if(selectedPersonId){
        const selectedPerson = this.environmentSettings.environmentName === 'Local'
          ? JSON.parse(this.cookieService.get('selectedPerson')) : JSON.parse(sessionStorage.getItem('selectedPerson'));

        const storedInvestigationLetterForm = this.environmentSettings.environmentName === 'Local'
            ? this.cookieService.get('form_data') : sessionStorage.getItem('form_data');
            this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data','/') : sessionStorage.removeItem('form_data');

          this.investigationLetter = JSON.parse(storedInvestigationLetterForm);
          this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data','/') : sessionStorage.removeItem('form_data');

        this.investigationLetter.addressLine1 = selectedPerson.personStreet1;
        this.investigationLetter.addressLine2 = selectedPerson.personStreet2;
        this.investigationLetter.addressCity = selectedPerson.personCity;
        this.investigationLetter.addressState = selectedPerson.personState ? selectedPerson.personState : 'TX';
        // zip value split is happening at loadInvestigationLetterFormData() method.
        this.investigationLetter.addressZip = selectedPerson.personZip ;
        this.investigationLetter.county = selectedPerson.personCounty ? selectedPerson.personCounty : '';

        setTimeout(() => {
          if (this.searchApButtonRef && this.searchApButtonRef.nativeElement) {
            this.searchApButtonRef.nativeElement.lastChild.focus();
          }
        }, 100);
      } else {
        let form_data = this.environmentSettings.environmentName === 'Local'
                        ? this.cookieService.get('form_data') : sessionStorage.getItem('form_data');
        if(form_data) {
          const storedInvestigationLetterForm = JSON.parse(form_data);
          this.environmentSettings.environmentName === 'Local' ?
                      this.cookieService.delete('form_data','/') : sessionStorage.removeItem('form_data');
          if(storedInvestigationLetterForm) {
            this.investigationLetter = Object.assign(this.investigationLetter, storedInvestigationLetterForm);
            isFormChanged = storedInvestigationLetterForm.isPageDirty;
          }
          setTimeout(() => {
            if (this.searchApButtonRef && this.searchApButtonRef.nativeElement) {
              this.searchApButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
        }
        
      }
      return response;
    }), map(() => {
      if(selectedPersonId){
        this.caseService.getPersonDetail(selectedPersonId).subscribe( resp => {
          this.selectedAP = resp;
          if(this.investigationLetter.lastPersonSearched === 'Alleged Perpetrator') {
            this.investigationLetter.letterSentToPersonId = selectedPersonId;
            this.investigationLetter.apFirstName = this.selectedAP.firstName;
            this.investigationLetter.apMiddleName = this.selectedAP.middleName;
            this.investigationLetter.apLastName = this.selectedAP.lastName;
            this.investigationLetter.letterBodyDear = this.selectedAP.firstName;
            this.investigationLetter.apHomePhoneNumber = this.selectedAP.phone;
         }
         this.loadInvestigationLetterFormData();
         this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
         return resp;
        });
      }
      return '';
    })
    ).subscribe( res => {
      if(!selectedPersonId){
        this.loadInvestigationLetterFormData();
      }
      if (isFormChanged) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
      }
  
    });
    this.storeSub = this.store.select(state => state.dirtyCheck.isDirty).subscribe((res) => {
      this.isPageDirty = res;
    })

    if (investigationLetterId === null || investigationLetterId === '0') {
      this.disableDeleteButton = true;
      this.disableDownloadDraftButton = true;
    }
  }

  createForm() {
    this.investigationLetterForm = this.formBuilder.group(
      {
        id: [''],
        letterMethod: ['', [Validators.required]],
        letterType: [this.letterType],
        apFirstName: ['', [Validators.required]],
        apMiddleName: [''],
        apLastName: ['', [Validators.required]],
        apHomePhoneNumber: ['', [Validators.required]],
        addressCity: ['', [Validators.required]],
        addressState: ['TX'],
        addressZip: ['', [Validators.required]],
        county: ['', [Validators.required]],
        addressLine1: ['', [Validators.required]],
        addressLine2: [''],
        addressZipExt: [''],
        administratorName: [''],
        allegationSummary: [''],
        incidentLocation: [''],
        interviewStatus: [''],
        dispositionSummary: [''],
        interviewDate: [''],
        allegedPerpetratorPhoneNumber: [''],
        caseId: [''],
        eventId: [''],
        fecilityId: [''],
        letterAllegationLinkId: [''],
        adminReviewStaffPersonId: [''],
        selectedChildsPersonId: [''],
        investigatorPersonId: [''],
        childsPersonId: [''],
        letterSentFromPersonId: [''],
        letterSentToPersonId: [''],
        resourceId: [''],
        stageId: [''],
        investigatorDesignation: [''],
        investigatorEmail: [''],
        investigatorName: [''],
        investigatorPhoneNumber: [''],
        letterBodyDear: [''],
        letterBody: [''],
        monitoringConcerns: [''],
        operationHomeName: [''],
        recipientNumber: [''],
        lastPersonSearched: [''],
        parentName: [''],
        childsFirstName: [''],
        allegedPerpetratorName: [''],
        adminReviewStaffDetails: ['']
      }, {
        validators: [
            ApRightsPriorToInterviewValidators.requiredFieldValidations,
            ApRightsPriorToInterviewValidators.phoneNumberPattern,
            ApRightsPriorToInterviewValidators.zipPattern
        ]
      }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.
                        find(value => this.investigationLetter.letterType === value.code).decode;

    if (this.investigationLetter) {
      this.investigationLetterForm.setValue({
        id: this.investigationLetter.id,
        addressCity: this.investigationLetter.addressCity,
        addressState: this.investigationLetter.addressState? this.investigationLetter.addressState : 'TX',
        addressLine1: this.investigationLetter.addressLine1,
        addressLine2: this.investigationLetter.addressLine2,
        addressZip: this.investigationLetter.addressZip ?
          (this.investigationLetter.addressZip.includes('-') ?
          this.investigationLetter.addressZip.split('-')[0] : this.investigationLetter.addressZip) : '',
        addressZipExt: this.investigationLetter.addressZip ?
        (this.investigationLetter.addressZip.includes('-') ?
          this.investigationLetter.addressZip.split('-')[1] : '') : '',
        administratorName: this.investigationLetter.administratorName,
        allegationSummary: this.investigationLetter.allegationSummary,
        incidentLocation: this.investigationLetter.incidentLocation,
        interviewStatus: this.investigationLetter.interviewStatus,
        letterMethod: this.investigationLetter.letterMethod,
        letterType: this.investigationLetter.letterType,
        dispositionSummary: this.investigationLetter.dispositionSummary,
        interviewDate: this.investigationLetter.interviewDate,
        allegedPerpetratorPhoneNumber: this.investigationLetter.allegedPerpetratorPhoneNumber,
        caseId: this.investigationLetter.caseId,
        eventId: this.investigationLetter.eventId,
        fecilityId: this.investigationLetter.fecilityId,
        letterAllegationLinkId: this.investigationLetter.letterAllegationLinkId,
        adminReviewStaffPersonId: this.investigationLetter.adminReviewStaffPersonId,
        selectedChildsPersonId: this.investigationLetter.selectedChildsPersonId,
        investigatorPersonId: this.investigationLetter.investigatorPersonId,
        childsPersonId: this.investigationLetter.childsPersonId,
        letterSentFromPersonId: this.investigationLetter.letterSentFromPersonId,
        letterSentToPersonId: this.investigationLetter.letterSentToPersonId,
        resourceId: this.investigationLetter.resourceId,
        stageId: this.investigationLetter.stageId,
        investigatorDesignation: this.investigationLetter.investigatorDesignation,
        investigatorEmail: this.investigationLetter.investigatorEmail,
        investigatorName: this.investigationLetter.investigatorName,
        investigatorPhoneNumber: this.investigationLetter.investigatorPhoneNumber,
        letterBodyDear: this.investigationLetter.letterBodyDear,
        letterBody: this.investigationLetter.letterBody,
        monitoringConcerns: this.investigationLetter.monitoringConcerns,
        operationHomeName: this.investigationLetter.operationHomeName,
        recipientNumber: this.investigationLetter.recipientNumber,
        lastPersonSearched: this.investigationLetter.lastPersonSearched,
        parentName: this.investigationLetter.parentName,
        childsFirstName: this.investigationLetter.childsFirstName,
        allegedPerpetratorName: this.investigationLetter.allegedPerpetratorName,
        adminReviewStaffDetails: this.investigationLetter.adminReviewStaffDetails,
        apFirstName: this.investigationLetter.apFirstName,
        apMiddleName: this.investigationLetter.apMiddleName,
        apLastName: this.investigationLetter.apLastName,
        apHomePhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.apHomePhoneNumber),
        county: this.investigationLetter.county
      });
    }
    if (this.investigationLetterRes.pageMode === 'VIEW' || this.investigationLetter.event === 'COMP') {
      FormUtils.disableFormControlStatus(this.investigationLetterForm, ['letterMethod','recipientNumber',
            'apFirstName','apMiddleName','apLastName', 'apHomePhoneNumber',
            'addressLine1','addressLine2','addressCity','addressState',
            'addressZip','addressZipExt','county']);
      this.hideDeleteButton = true;
      this.hideDownloadDraftButton = true;
      this.hideSaveSubmitButton = true;
      this.hideSaveButton = true;
      this.hideValidateButton = true;
      this.hideSearchButton = true;
    }

    if (this.investigationLetterRes.pageMode === 'VIEW' || this.investigationLetter.event !== 'COMP') {
      this.hideDownloadButton = true;
    }
    this.setHideReceiptNumber(this.investigationLetter.letterMethod);
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));

    this.formOrgValue = this.getFormValue();
  }

  searchAllegedPerptrator() {
    this.investigationLetterForm.controls.lastPersonSearched.setValue('Alleged Perpetrator');
    this.searchPerson();
  }

  searchPerson() {
    this.environmentSettings.environmentName === 'Local'?
          this.cookieService.set('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }),
            10000, '/', undefined, undefined, 'Lax') :
            sessionStorage.setItem('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }));

    const redirectUrl = 'case/letters/investigation-letters/' +
      this.letterType +'/' +
      (this.investigationLetter.id ? this.investigationLetter.id : '0');

    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/personList?personSearchFrom=investigationLetter&redirectToPageUrl=' + redirectUrl;
  }

  searchAdminReviewStaff()  {
    this.searchService.setSearchSource('AdminStaff');
    this.selectAdminReviewStaff();
  }

  selectAdminReviewStaff() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const returnUrl = 'case/letters/investigation-letters/' +
                  this.letterType + '/'  +
                  (this.investigationLetter.id ? this.investigationLetter.id : '0');

    this.searchService.setSearchSource('AdminStaff');
    this.searchService.setFormData(this.investigationLetterForm.value);
    this.searchService.setReturnUrl(returnUrl);
    this.router.navigate(
        ['/case/letters/investigation-letters/' +
        this.letterType +'/' +
        (this.investigationLetter.id ? this.investigationLetter.id : '0') + '/staffsearch'],
        { skipLocationChange: false }
    );
  }

  delete() {
    const initialState = {
      title: this.letterType === 'ARS' ? 'AP\'s Rights Prior to Interview (Spanish) - 2617s' : 'AP\'s Rights Prior to Interview - 2617',
      message: 'Are you sure you want to delete this information?',
      showCancel: true
    };

    const modal = this.modalService.show(DfpsConfirmComponent, {class: 'modal-md modal-dialog-centered', initialState});
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) =>{
      if(result === true){
        this.caseService.deleteInvestigationLetter(this.investigationLetterForm.get('id').value).subscribe((res) => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          this.router.navigate(['/case/letters/investigation-letters']);
        });
      }
    });
  }

  downloadDraft() {
    if(this.isFormDirty()) {
      const initialState = {
        message: 'Please save the page before downloading the draft Letter.',
        title: 'Letter Detail',
        showCancel: false,
      };

      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });

    } else if (this.validateFormGroup(this.investigationLetterForm)) {
        const letterName = this.investigationLetterRes.letterTypes.find(value => this.letterType === value.code).decode;

        this.caseService.downloadInvestigationLetter(this.investigationLetter.id, true)
          .subscribe(blob => saveAs(blob, 'Draft_'+letterName + '_' + this.investigationLetterRes.caseId + '.pdf'));
    }
  }

  download() {
    let letterName = this.investigationLetterRes.letterTypes.find(value =>  this.letterType === value.code).decode;
     this.caseService.downloadInvestigationLetter(this.investigationLetter.id, false)
         .subscribe(blob => {
         saveAs(blob, letterName + '_' + this.investigationLetterRes.caseId + '.pdf');
         this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
     });
   }

  save() {
    this.investigationLetter.isSubmit = false;
    this.saveInvestigationLetter();
  }

  submit() {
    this.investigationLetter.isSubmit = true;
    this.saveInvestigationLetter();
  }

  saveInvestigationLetter(){
    if (this.validateFormGroup(this.investigationLetterForm)) {
        const payload = Object.assign(this.investigationLetterRes.investigationLetter, this.investigationLetterForm.value);
        this.investigationLetterRes.investigationLetter = payload;
        if(this.investigationLetter.isSubmit) {
          this.investigationLetterRes.investigationLetter.isSubmit = true;
        }

        this.caseService
        .saveInvestigationLetter(this.investigationLetterRes.investigationLetter)
        .subscribe((result) => {
          if (result) {
            setTimeout(() => {
              this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
              this.router.navigate(['/case/letters/investigation-letters']);
              }, 3000);
          }
        });
      }
  }

  onAddressChange(event: any) {
    this.isAddressChanged =  true;
    this.isAddressValidated = false;
  }

  stateChange(event: any) {
    this.isAddressChanged =  true;
    this.isAddressValidated = false;
  }

  validateAdressFields() {
    const address: Address = {
        street1: this.investigationLetterForm.controls.addressLine1.value,
        street2: this.investigationLetterForm.controls.addressLine2.value,
        city: this.investigationLetterForm.controls.addressCity.value,
        state: this.investigationLetterForm.controls.addressState.value,
        zip: this.investigationLetterForm.controls.addressZip.value,
        extension: this.investigationLetterForm.controls.addressZipExt.value,
    };
    this.dfpsAddressValidator.validate(address);
    this.isAddressValidated = true;
    this.isAddressChanged = false;
  }

  updateAdressFields(address: Address) {
    if (address) {
        if (address.isAddressAccepted) {
            this.investigationLetterForm.patchValue({
                addressLine1: address.street1,
                addressLine2: address.street2,
                addressCity: address.city,
                addressState: address.state,
                addressZip: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[0] : '') : '',
                addressZipExt: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[1] : '') : '',
                county: address.countyCode
            });
        } else {
            if (this.investigationLetterForm.controls.addressZip.value &&
                this.investigationLetterForm.controls.addressZip.value.length !== 5) {
                    this.investigationLetterForm.patchValue({
                      addressZip: '',
                      addressZipExt: ''
                    });
            }

            if (this.investigationLetterForm.controls.addressZipExt.value &&
                this.investigationLetterForm.controls.addressZipExt.value.length !== 4) {
                    this.investigationLetterForm.patchValue({
                      addressZipExt: ''
                    });
            }
        }
    }
  }

  letterMethodChange(event: any){
    const method = this.investigationLetterForm.controls.letterMethod.value;
    this.setHideReceiptNumber(method);
    if(this.hideReceiptNumber){
      this.investigationLetterForm.patchValue({ recipientNumber: '' });
    }
  }

  setHideReceiptNumber(letterMethod){
    if (['RCM', 'REF', 'RCF', 'RCE'].includes(letterMethod)) {
      this.hideReceiptNumber = false;
    } else {
      this.hideReceiptNumber = true;
    }
  }

  fmtPhoneNumber(event: any) {
    this.investigationLetterForm.patchValue({
      apHomePhoneNumber: FormUtils.formatPhoneNumber(event.target.value)
    });
  }

  isFormDirty() {
    let formNewValue = this.getFormValue();
    return (formNewValue.localeCompare(this.formOrgValue) !== 0);
  }

  getFormValue() {
    let formValue = '';
    Object.keys(this.investigationLetterForm.controls).forEach( key => {
      let val = this.investigationLetterForm.controls[key].value;
      formValue += key + '=[' + (val !== null ? val : '') + '],';
    });
    return formValue;
  }

}
