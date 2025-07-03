import { Component, Inject, OnDestroy, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  Address, DfpsAddressValidatorComponent, DfpsConfirmComponent, DfpsFormValidationDirective, DirtyCheck,
  ENVIRONMENT_SETTINGS, NavigationService, SET, FormUtils
} from 'dfps-web-lib';
import { saveAs } from 'file-saver';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { Subscription } from 'rxjs';
import { CaseService } from '../../service/case.service';
import { InvestigationLetter, InvestigationLetterRes } from '../../model/InvestigationLetter';
import { ParentNotificationOfInterviewValidators } from './parent-notification-of-interview.validator';
import { dfpsValidatorErrors } from 'messages/validator-errors';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'parent-notification-of-interview',
  templateUrl: './parent-notification-of-interview.component.html'
})
export class ParentNotificationOfInterviewComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('searchParentButton', { read: ElementRef}) searchParentButtonRef;
  @ViewChild('searchChildButton', { read: ElementRef}) searchChildButtonRef;

  modalSubscription: Subscription;
  investigationLetter: InvestigationLetter;
  investigationLetterForm: FormGroup;
  investigationLetterRes: InvestigationLetterRes;
  formOrgValue: string;

  hideAttentionMessage = false;
  hideDeleteButton = false;
  hideDownloadButton = true;
  hideDownloadDraftButton = false;
  hideSaveSubmitButton = false;
  hideSaveButton = false;
  hideValidateButton = false;
  hideSearchButton = false;
  letterTypeDecode: string;
  isAddressValidated: boolean;
  isAddressChanged: boolean;
  errorNoDataMessage: any;
  hideReceiptNumber = true;
  hideRecipientNumber = false;
  disableLetterMethod = false;
  disableDeleteButton = false;
  disableDownloadButton = false;
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
    private caseService: CaseService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnDestroy() {
    if (this.storeSub) {
      this.storeSub.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Parent Notification of Interview - 2867');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
    this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    const selectedPersonId = this.route.snapshot.queryParamMap.get('selectedPersonId');

    var isFormChanged = false;
    this.caseService.getInvestigationLetter(investigationLetterId).subscribe(response => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;
      this.investigationLetter.letterType = 'PRN';

      if (this.investigationLetterRes.operationNames.length == 0) {
        this.errorNoDataMessage = dfpsValidatorErrors.MSG_OPERATION_NAME_EMPTY;
      }

      if(selectedPersonId){
        const selectedPerson = this.environmentSettings.environmentName === 'Local'
        ? JSON.parse(this.cookieService.get('selectedPerson')) : JSON.parse(sessionStorage.getItem('selectedPerson'));

        const storedInvestigationLetterForm = this.environmentSettings.environmentName === 'Local'
                  ? JSON.parse(this.cookieService.get('form_data')) : JSON.parse(sessionStorage.getItem('form_data'));

        this.investigationLetter = Object.assign(this.investigationLetter, storedInvestigationLetterForm);
        this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data','/') : sessionStorage.removeItem('form_data');
        if(this.investigationLetter != null && this.investigationLetter.addressZip != null  && this.investigationLetter.addressZipExt != null){
          this.investigationLetter.addressZip = this.investigationLetter.addressZip.concat("-").concat(this.investigationLetter.addressZipExt);
        }

        if(this.investigationLetter.lastPersonSearched === 'Parent') {
          this.investigationLetter.letterSentToPersonId = selectedPersonId;
          this.investigationLetter.administratorName = this.formatFullName(selectedPerson);
          this.investigationLetter.letterBodyDear = this.formatFullName(selectedPerson);
          this.investigationLetter.addressLine1 = selectedPerson.personStreet1;
          this.investigationLetter.addressLine2 = selectedPerson.personStreet2;
          this.investigationLetter.addressCity = selectedPerson.personCity;
          this.investigationLetter.addressState = selectedPerson.personState ? selectedPerson.personState : 'TX';
          this.investigationLetter.addressZip = selectedPerson.personZip;
          setTimeout(() => {
            if (this.searchParentButtonRef && this.searchParentButtonRef.nativeElement) {
              this.searchParentButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);

        } else if(this.investigationLetter.lastPersonSearched === 'Child') {
          this.investigationLetter.childsFirstName = selectedPerson.nmPersonFirst!= null ? selectedPerson.nmPersonFirst.replace("~S~","'") : "";

          setTimeout(() => {
            if (this.searchChildButtonRef && this.searchChildButtonRef.nativeElement) {
              this.searchChildButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
        }
      } else {
        let form_data = this.environmentSettings.environmentName === 'Local'
                        ? this.cookieService.get('form_data') : sessionStorage.getItem('form_data');
        if(form_data) {
          const storedInvestigationLetterForm = JSON.parse(form_data);
          this.environmentSettings.environmentName === 'Local' ? 
                      this.cookieService.delete('form_data','/') : sessionStorage.removeItem('form_data');
          if(storedInvestigationLetterForm) {
            isFormChanged = storedInvestigationLetterForm.isPageDirty;
            this.investigationLetter = Object.assign(this.investigationLetter, storedInvestigationLetterForm);
          }
        }
        if(this.investigationLetter.lastPersonSearched === 'Parent') {
          setTimeout(() => {
            if (this.searchParentButtonRef && this.searchParentButtonRef.nativeElement) {
              this.searchParentButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
        } else if(this.investigationLetter.lastPersonSearched === 'Child') {
          setTimeout(() => {
            if (this.searchChildButtonRef && this.searchChildButtonRef.nativeElement) {
              this.searchChildButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
        }
        
      }

      this.investigationLetter.letterType = 'PRN';

      this.loadInvestigationLetterFormData();

      if (this.investigationLetter.id === null || this.investigationLetter.id === '0') {
        this.disableDeleteButton = true;
        this.disableDownloadButton = true;
        this.disableDownloadDraftButton = true;
      }

      if (this.investigationLetterRes.pageMode === 'VIEW' || this.investigationLetter.event === 'COMP') {
        this.hideDeleteButton = true;
        this.hideDownloadDraftButton = true;
        this.hideSaveSubmitButton = true;
        this.hideSaveButton = true;
        this.hideValidateButton = true;
        this.hideSearchButton = true;
        this.disableLetterMethod = true;
		FormUtils.disableFormControlStatus(this.investigationLetterForm,
		['letterMethod', 'recipientNumber', 'addressLine1', 'addressLine2',
		  'addressCity', 'addressState', 'addressZip', 'addressZipExt', 'letterBodyDear',
		  'interviewDate', 'investigatorName', 'investigatorDesignation', 'fecilityId',
		  'investigatorPhoneNumber', 'investigatorEmail','administratorName', 'childsFirstName']);
    }

      if (this.investigationLetter.event === 'COMP' && this.investigationLetterRes.pageMode === 'EDIT') {
        this.hideDownloadButton = false;
      }
      this.showOrHideRecipientNumber(this.investigationLetter.letterMethod);
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));

      if (selectedPersonId || isFormChanged) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
      }
      this.formOrgValue = this.getFormValue();
    });

    this.storeSub = this.store.select(state => state.dirtyCheck.isDirty).subscribe((res) => {
      this.isPageDirty = res;
    });

  }

  createForm() {
    this.investigationLetterForm = this.formBuilder.group(
      {
        id: [''],
        letterMethod: [''],
        recipientNumber: [''],
        addressLine1: [''],
        addressLine2: [''],
        addressCity: [''],
        addressState: ['TX'],
        addressZip: [''],
        addressZipExt: [''],
        letterBodyDear: [''],
        administratorName: [''],
        childsFirstName: [''],
        letterBody: [''],
        childsPersonId: [''],
        allegationSummary: [''],
        incidentLocation: [''],
        interviewStatus: [''],
        letterType: ['PRN'],
        dispositionSummary: [''],
        fecilityId: [''],
        interviewDate: ['', [Validators.required]],
        allegedPerpetratorPhoneNumber: [''],
        caseId: [''],
        eventId: [''],
        letterAllegationLinkId: [''],
        adminReviewStaffPersonId: [''],
        investigatorPersonId: [''],
        letterSentFromPersonId: [''],
        letterSentToPersonId: [''],
        resourceId: [''],
        stageId: [''],
        incidentSummary: [''],
        investigatorFindings1: [''],
        investigatorFindings2: [''],
        investigatorName: ['', [Validators.required]],
        investigatorDesignation: ['', [Validators.required]],
        investigatorPhoneNumber: ['', [Validators.required]],
        investigatorEmail: ['', [Validators.required]],
        monitoringConcerns: [''],
        operationHomeName: [''],
        lastPersonSearched: ['']
      }, {
        validators: [
          ParentNotificationOfInterviewValidators.validateLetterInformation(),
          ParentNotificationOfInterviewValidators.requiredFieldValidations,
          ParentNotificationOfInterviewValidators.validateEmailPattern(),
          ParentNotificationOfInterviewValidators.phoneNumberPattern,
          ParentNotificationOfInterviewValidators.dateFormatValidation,
          ParentNotificationOfInterviewValidators.zipPattern
        ]
      }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'PRN' === value.code).decode;

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
        fecilityId: this.investigationLetter.fecilityId !== null ? this.investigationLetter.fecilityId+'' : '',
        letterType: this.investigationLetter.letterType,
        dispositionSummary: this.investigationLetter.dispositionSummary,
        interviewDate: this.investigationLetter.interviewDate,
        allegedPerpetratorPhoneNumber: this.investigationLetter.allegedPerpetratorPhoneNumber,
        caseId: this.investigationLetter.caseId,
        eventId: this.investigationLetter.eventId,
        letterAllegationLinkId: this.investigationLetter.letterAllegationLinkId,
        adminReviewStaffPersonId: this.investigationLetter.adminReviewStaffPersonId,
        investigatorPersonId: this.investigationLetter.investigatorPersonId,
        childsPersonId: this.investigationLetter.childsPersonId,
        letterSentFromPersonId: this.investigationLetter.letterSentFromPersonId,
        letterSentToPersonId: this.investigationLetter.letterSentToPersonId,
        resourceId: this.investigationLetter.resourceId,
        stageId: this.investigationLetter.stageId,
        incidentSummary: this.investigationLetter.incidentSummary,
        investigatorFindings1: this.investigationLetter.investigatorFindings1,
        investigatorFindings2: this.investigationLetter.investigatorFindings2,
        investigatorDesignation: this.investigationLetter.investigatorDesignation,
        investigatorEmail: this.investigationLetter.investigatorEmail,
        investigatorName: this.investigationLetter.investigatorName,
        investigatorPhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.investigatorPhoneNumber),
        letterBodyDear: this.investigationLetter.letterBodyDear,
        letterBody: this.investigationLetter.letterBody,
        monitoringConcerns: this.investigationLetter.monitoringConcerns,
        operationHomeName: this.investigationLetter.operationHomeName,
        recipientNumber: this.investigationLetter.recipientNumber,
        lastPersonSearched: this.investigationLetter.lastPersonSearched,
        childsFirstName: this.investigationLetter.childsFirstName
      });
    }
  }

  searchParent() {
    const isPageDirty = this.isPageDirty;
    this.investigationLetterForm.controls.lastPersonSearched.setValue('Parent');
    this.searchPerson(isPageDirty);
  }

  searchChild() {
    const isPageDirty = this.isPageDirty;
    this.investigationLetterForm.controls.lastPersonSearched.setValue('Child');
    this.searchPerson(isPageDirty);
  }

  searchPerson(isPageDirty: boolean) {
    this.environmentSettings.environmentName === 'Local' ?
    this.cookieService.set('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: isPageDirty }),
      10000, '/', undefined, undefined, 'Lax') :
      sessionStorage.setItem('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: isPageDirty }));
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const redirectUrl = 'case/letters/investigation-letters/PRN/' +
      (this.investigationLetter.id ? this.investigationLetter.id : '0');

    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/personList?personSearchFrom=investigationLetter&redirectToPageUrl=' + redirectUrl;
  }

  delete() {
    const initialState = {
      message: 'Are you sure you want to delete this information?',
      title: 'Parent Notification of Interview - 2867',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.caseService.deleteInvestigationLetter(this.investigationLetterForm.get('id').value).subscribe((res) => {
        this.router.navigate(['/case/letters/investigation-letters']);
        });
      }
    });
  }

  download() {
    let letterName = this.investigationLetterRes.letterTypes.find(value => 'PRN' === value.code).decode;
    this.caseService.downloadInvestigationLetter(this.investigationLetter.id, false)
      .subscribe(blob => {
        saveAs(blob, letterName + '_' + this.investigationLetterRes.caseId + '.pdf');
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
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
    } else {
      if (this.validateFormGroup(this.investigationLetterForm)) {
        const letterName = this.investigationLetterRes.letterTypes.find(value => 'PRN' === value.code).decode;
        this.caseService.downloadInvestigationLetter(this.investigationLetter.id, true)
          .subscribe(blob => {
            saveAs(blob, 'Draft_' + letterName + '_' + this.investigationLetterRes.caseId + '.pdf');
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          });
      }
    }
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
  letterMethodChange() {
    const letterMethod = this.investigationLetterForm.get('letterMethod').value;
    this.showOrHideRecipientNumber(letterMethod);
    if(this.hideRecipientNumber){
      this.investigationLetterForm.patchValue({ recipientNumber: '' });
    }
  }

  showOrHideRecipientNumber(letterMethod){
    if (['RCM', 'REF', 'RCF', 'RCE'].includes(letterMethod)) {
      this.hideRecipientNumber = false;
    } else {
      this.hideRecipientNumber = true;
    }
  }

  fmtPhoneNumber(event: any) {
    this.investigationLetterForm.patchValue({
      investigatorPhoneNumber: FormUtils.formatPhoneNumber(event.target.value)
    });
  }

  formatFullName(value: any) {
    if(value){
      let fullName = '';
      let firstName = value.nmPersonFirst ? value.nmPersonFirst : '';
      let middleName = value.nmPersonMiddle ? value.nmPersonMiddle : '';
      let lastName = value.nmpersonLast ? value.nmpersonLast : '';
      if(firstName.length != 0) { fullName = firstName.concat(' '); }
      if(middleName.length != 0) { fullName = fullName.concat(middleName).concat(' '); } 
      if(lastName.length != 0) { fullName = fullName.concat(lastName); }
      if(fullName == null || fullName == ''){
        fullName = this.formatName(value.personFull);
      }
      return fullName.replace("~S~","'");
    } else {
      return "";
    }
  }
  formatName(value: any) {
    if(value){
      const index = value.indexOf(',');
      const firstAndMiddleName = value.substring(index+1, value.length);
      const lastName = value.substring(0, index);
      return lastName != null && lastName!='' ? firstAndMiddleName.concat(' ').concat(lastName) : firstAndMiddleName;
    } else {
      return '';
    }
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
