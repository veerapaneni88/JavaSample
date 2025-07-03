import { Component, Inject, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsFormValidationDirective,
  DfpsConfirmComponent,
  DirtyCheck,
  ENVIRONMENT_SETTINGS,
  NavigationService,
  DfpsAddressValidatorComponent,
  Address, SET, FormUtils
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { InvestigationLetter, InvestigationLetterRes } from '../../model/InvestigationLetter';
import { CaseService } from '../../service/case.service';
import { CookieService } from 'ngx-cookie-service';
import { FpsLetterheadValidators } from './fps-letterhead.validator';
import { saveAs } from 'file-saver';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'fps-letterhead',
  templateUrl: './fps-letterhead.component.html'
})
export class FpsLetterHeadComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('searchButton', { read: ElementRef}) searchButtonRef;

  modalSubscription: Subscription;
  investigationLetter: InvestigationLetter;
  investigationLetterForm: FormGroup;
  investigationLetterRes: InvestigationLetterRes;
  displayReceiptNumber = false;
  formOrgValue: string;

  hideAttentionMessage = false;
  hideDeleteButton = false;
  hideDownloadDraftButton = false;
  hideDownloadButton = true;
  hideSaveSubmitButton = false;
  hideSaveButton = false;
  hideValidateButton = false;
  hideSearchButton = false;
  letterTypeDecode: string;
  isAddressValidated: boolean;
  isAddressChanged: boolean;
  errorNoDataMessage: any;
  selectedAdministratorName: string;
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
    this.navigationService.setTitle('FPS Letterhead - 2834a');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    const formLetterType = 'FPS';
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
      this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    const selectedPersonId = this.route.snapshot.queryParamMap.get('selectedPersonId');

    var isFormChanged = false;
    this.caseService.getInvestigationLetter(investigationLetterId).subscribe(response => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;

      if (selectedPersonId) {
        const selectedPerson = this.environmentSettings.environmentName === 'Local'
          ? JSON.parse(this.cookieService.get('selectedPerson')) : JSON.parse(sessionStorage.getItem('selectedPerson'));

        const storedInvestigationLetterForm = this.environmentSettings.environmentName === 'Local'
          ? JSON.parse(this.cookieService.get('form_data')) : JSON.parse(sessionStorage.getItem('form_data'));

        this.investigationLetter = Object.assign(this.investigationLetter, storedInvestigationLetterForm);
        this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data','/') :
        sessionStorage.removeItem('form_data');

        this.investigationLetter.letterSentToPersonId = selectedPersonId;
        this.investigationLetter.administratorName = this.formatFullName(selectedPerson);
        this.investigationLetter.letterBodyDear = this.formatFullName(selectedPerson);
        this.investigationLetter.addressLine1 = selectedPerson.personStreet1;
        this.investigationLetter.addressLine2 = selectedPerson.personStreet2;
        this.investigationLetter.addressCity = selectedPerson.personCity;
        this.investigationLetter.addressState = selectedPerson.personState ? selectedPerson.personState : 'TX';
        this.investigationLetter.addressZip = selectedPerson.personZip;
        setTimeout(() => {
          if (this.searchButtonRef && this.searchButtonRef.nativeElement) {
            this.searchButtonRef.nativeElement.lastChild.focus();
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
            isFormChanged = storedInvestigationLetterForm.isPageDirty;
            this.investigationLetter = Object.assign(this.investigationLetter, storedInvestigationLetterForm);
          }
          setTimeout(() => {
            if (this.searchButtonRef && this.searchButtonRef.nativeElement) {
              this.searchButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
        }

      }
      this.investigationLetter.letterType = formLetterType;
      this.selectedAdministratorName = this.investigationLetter.administratorName;

      this.loadInvestigationLetterFormData();

      if (this.investigationLetter.id === null || this.investigationLetter.id === '0') {
          this.disableDeleteButton = true;
          this.disableDownloadDraftButton = true;
      }

      if (this.investigationLetter.event === 'COMP' && this.investigationLetterRes.pageMode === 'EDIT') {
        this.hideDownloadButton = false;
        this.disableDownloadButton = false;
      } else {
        this.disableDownloadButton = true;
        this.hideDownloadButton = true;
      }

      if (this.investigationLetter.event === 'COMP' || this.investigationLetterRes.pageMode === 'VIEW') {
        this.hideDeleteButton = true;
        this.hideDownloadDraftButton = true;

        this.hideSaveSubmitButton = true;
        this.hideSaveButton = true;
        this.hideValidateButton = true;
        this.hideSearchButton = true;

        FormUtils.disableFormControlStatus(this.investigationLetterForm,
          ['letterMethod', 'recipientNumber', 'administratorName', 'addressLine1', 'addressLine2',
            'addressCity', 'addressState', 'addressZip', 'addressZipExt', 'letterBodyDear',
            'letterBody', 'investigatorName', 'investigatorDesignation', 'investigatorPhoneNumber', 'investigatorEmail']);
      }

      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));

      if (selectedPersonId || isFormChanged) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
      }

      this.storeSub = this.store.select(state => state.dirtyCheck.isDirty).subscribe((res) => {
        this.isPageDirty = res;
      });

      this.formOrgValue = this.getFormValue();

    });
  }

  createForm() {
    this.investigationLetterForm = this.formBuilder.group(
      {
        id: [''],
        letterMethod: [''],
        letterType: ['', [Validators.required]],
        administratorName: [''],
        addressLine1: [''],
        addressLine2: [''],
        addressCity: [''],
        addressState: ['TX'],
        addressZip: [''],
        addressZipExt: [''],
        incidentLocation: [''],
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
        letterBodyDear: [''],
        letterBody: [''],
        investigatorName: ['', [Validators.required]],
        investigatorDesignation: [''],
        investigatorPhoneNumber: [''],
        investigatorEmail: [''],
        monitoringConcerns: [''],
        operationHomeName: [''],
        recipientNumber: ['']
      }, {
      validators: [
        FpsLetterheadValidators.phoneNumberPattern,
        FpsLetterheadValidators.requiredFieldValidations,
        FpsLetterheadValidators.validateEmailPattern(),
        FpsLetterheadValidators.zipPattern
      ]
    }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'FPS' === value.code).decode;
    if (this.investigationLetter) {
      this.investigationLetterForm.setValue({
        id: this.investigationLetter.id,
        addressCity: this.investigationLetter.addressCity,
        addressState: this.investigationLetter.addressState ? this.investigationLetter.addressState : 'TX',
        addressLine1: this.investigationLetter.addressLine1,
        addressLine2: this.investigationLetter.addressLine2,
        addressZip: this.investigationLetter.addressZip ?
          (this.investigationLetter.addressZip.includes('-') ?
            this.investigationLetter.addressZip.split('-')[0] : this.investigationLetter.addressZip) : '',
        addressZipExt: this.investigationLetter.addressZip ?
          (this.investigationLetter.addressZip.includes('-') ?
            this.investigationLetter.addressZip.split('-')[1] : '') : '',
        administratorName: this.investigationLetter.administratorName,
        incidentLocation: this.investigationLetter.incidentLocation,
        letterMethod: this.investigationLetter.letterMethod,
        letterType: this.investigationLetter.letterType,
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
        investigatorName:this.investigationLetter.investigatorName,
        investigatorPhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.investigatorPhoneNumber),
        letterBodyDear: this.investigationLetter.letterBodyDear,
        letterBody: this.investigationLetter.letterBody,
        monitoringConcerns: this.investigationLetter.monitoringConcerns,
        operationHomeName: this.investigationLetter.operationHomeName,
        recipientNumber: this.investigationLetter.recipientNumber
      });

      this.showReceiptNumber();
    }
  }

  delete() {
    const initialState = {
      title: 'FPS Letterhead - 2834a',
      message: 'Are you sure you want to delete this information?',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.caseService.deleteInvestigationLetter(this.investigationLetterForm.get('id').value).subscribe((res) => {
          this.router.navigate(['/case/letters/investigation-letters']);
        });
      }
    });
  }

  download() {
    if(this.isFormDirty()) {
      const initialState = {
        message: 'Please save the page before downloading the draft Letter.',
        title: 'Letter Detail',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    }else if (this.validateFormGroup(this.investigationLetterForm) &&
        ((this.isAddressChanged && this.isAddressValidated) || !this.isAddressChanged)) {
          if(this.investigationLetter.event === 'COMP'){
            this.caseService.downloadInvestigationLetter(this.investigationLetter.id, false)
              .subscribe(blob => saveAs(blob, 'FPS Letterhead - 2834a' + '_' + this.investigationLetterRes.caseId + '.pdf'));
          } else {
            this.caseService.downloadInvestigationLetter(this.investigationLetter.id, true)
              .subscribe(blob => saveAs(blob, 'Draft_' + 'FPS Letterhead - 2834a' + '_' + this.investigationLetterRes.caseId + '.pdf'));
          }
      }
  }

  searchPerson() {

    this.environmentSettings.environmentName === 'Local' ?
    this.cookieService.set('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }),
      10000, '/', undefined, undefined, 'Lax') :
      sessionStorage.setItem('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }));

    const redirectUrl = 'case/letters/investigation-letters/FPS/' +
      (this.investigationLetter.id ? this.investigationLetter.id : '0');

    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/personList?personSearchFrom=investigationLetter&redirectToPageUrl=' + redirectUrl;
  }

  showReceiptNumber() {
    this.displayReceiptNumber = ['RCM', 'RCE', 'RCF', 'REF'].includes(
      this.investigationLetterForm.controls.letterMethod.value);
    if (!this.displayReceiptNumber) {
      this.investigationLetterForm.controls.recipientNumber.setValue('');
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

  saveInvestigationLetter() {
    if (this.investigationLetter.administratorName !== this.selectedAdministratorName) {
      this.investigationLetter.letterSentToPersonId = '';
      this.investigationLetterForm.patchValue({ letterSentToPersonId: '' });
    }
      if (this.validateFormGroup(this.investigationLetterForm)) {
        const payload = Object.assign(this.investigationLetterRes.investigationLetter, this.investigationLetterForm.value);
        this.caseService.saveInvestigationLetter(payload).subscribe(
          response => {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['/case/letters/investigation-letters']);
          }
        );
      }
  }
  onAddressChange(event: any) {
    this.isAddressChanged = true;
    this.isAddressValidated = false;
  }

  stateChange(event: any) {
    this.isAddressChanged = true;
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

  getCustomStyles() {
    return 'line-height: 1.4';
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
