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
  FormUtils,
  Address, SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { InvestigationLetter, InvestigationLetterRes } from '../../model/InvestigationLetter';
import { CaseService } from '../../service/case.service';
import { CookieService } from 'ngx-cookie-service';
import { ChildDeathReportValidator } from './child-death-report.validator';
import { saveAs } from 'file-saver';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'child-death-report',
  templateUrl: './child-death-report.component.html'
})
export class ChildDeathReportComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('searchButton', { read: ElementRef}) searchButtonRef;

  modalSubscription: Subscription;
  investigationLetter: InvestigationLetter;
  investigationLetterForm: FormGroup;
  investigationLetterRes: InvestigationLetterRes;
  formOrgValue: string;

  displayReceiptNumber = false;
  hideAttentionMessage = false;
  hideDeleteButton = false;
  hideDownloadButton = true;
  hideDownloadDraftButton = false;
  hideSaveSubmitButton = false;
  hideSaveButton = false;
  hideValidateButton = false;
  hideSearchButton = false;
  letterTypeDecode: string;
  errorNoDataMessage: any;
  disableDeleteButton = false;
  disableDownloadDraftButton = false;

  disableDownloadButton = false;
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
    this.navigationService.setTitle('Child Death Report - 2899e');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  getCustomStyles() {
    return 'line-height: 1.4';
  }

  initializeScreen() {
    const formLetterType = "CDR";
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
    this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    const selectedPersonId = this.route.snapshot.queryParamMap.get('selectedPersonId');
    var isFormChanged = false;
    this.caseService.getInvestigationLetter(investigationLetterId).subscribe(response => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;

      if(selectedPersonId){
        const selectedPerson = this.environmentSettings.environmentName === 'Local'
            ? JSON.parse(this.cookieService.get('selectedPerson')) : JSON.parse(sessionStorage.getItem('selectedPerson'));

        const storedInvestigationLetterForm = this.environmentSettings.environmentName === 'Local'
                  ? JSON.parse(this.cookieService.get('form_data')) : JSON.parse(sessionStorage.getItem('form_data'));


        this.investigationLetter = Object.assign(this.investigationLetter, storedInvestigationLetterForm);
        this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data','/') :
          sessionStorage.removeItem('form_data');

        this.investigationLetter.letterSentToPersonId = selectedPersonId;
        this.investigationLetter.administratorName = this.formatFullName(selectedPerson);
        this.investigationLetter.addressLine1 = selectedPerson.personStreet1;
        this.investigationLetter.addressLine2 = selectedPerson.personStreet2;
        this.investigationLetter.addressCity = selectedPerson.personCity;
        this.investigationLetter.addressState = selectedPerson.personState ? selectedPerson.personState : 'TX';
        // zip value split is happening at loadInvestigationLetterFormData() method.
        this.investigationLetter.addressZip = selectedPerson.personZip ;

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

      this.loadInvestigationLetterFormData();
      this.showReceiptNumber();
      if (this.investigationLetter.event === 'COMP' || this.investigationLetterRes.pageMode === 'VIEW') {
        this.hideSaveSubmitButton = true;
        this.hideSaveButton = true;
        this.hideValidateButton = true;
        this.hideSearchButton = true;
        this.hideDeleteButton = true;
        this.hideDownloadDraftButton = true;
        this.disableForm();
      }
      if (this.investigationLetter.id === null || this.investigationLetter.id === '0') {
        this.disableDeleteButton = true;
        this.disableDownloadDraftButton = true;
        this.disableDownloadButton = true;
      }
      if (this.investigationLetter.event === 'COMP' && this.investigationLetterRes.pageMode === 'EDIT') {
        this.hideDownloadButton = false;
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
        caseId: [''],
        eventId: [''],
        stageId: [''],
        fecilityId: [''],
        resourceId: [''],
        letterMethod: ['', [Validators.required]],
        letterType: ['', [Validators.required]],
        recipientNumber: [''],
        administratorName: [''],
        addressLine1: ['', [Validators.required]],
        addressLine2: [''],
        addressCity: ['', [Validators.required]],
        addressState: ['TX'],
        addressZip: ['', [Validators.required]],
        addressZipExt: [''],
        selectedChildsPersonId: ['', [Validators.required]],
        incidentLocation: ['', [Validators.required]],
        incidentSummary: ['', [Validators.required]],
        letterSentToPersonId: [''],
        investigatorPersonId: [''],
        investigatorName: ['', [Validators.required]],
        investigatorDesignation: ['', [Validators.required]],
        investigatorPhoneNumber: ['', [Validators.required]],
        investigatorEmail: ['', [Validators.required]],
      }, {
        validators: [
          ChildDeathReportValidator.validateLetterInformation,
          ChildDeathReportValidator.validateEmailPattern(),
          ChildDeathReportValidator.phoneNumberPattern,
          ChildDeathReportValidator.zipPattern

        ]
      }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'CDR' === value.code).decode;
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
        incidentLocation: this.investigationLetter.incidentLocation,
        letterMethod: this.investigationLetter.letterMethod,
        letterType: this.investigationLetter.letterType,
        caseId: this.investigationLetter.caseId,
        eventId: this.investigationLetter.eventId,
        fecilityId: this.investigationLetter.fecilityId,
        selectedChildsPersonId: String(this.investigationLetter.selectedChildsPersonId),
        investigatorPersonId: this.investigationLetter.investigatorPersonId,
        letterSentToPersonId: this.investigationLetter.letterSentToPersonId,
        resourceId: this.investigationLetter.resourceId,
        stageId: this.investigationLetter.stageId,
        incidentSummary: this.investigationLetter.incidentSummary,
        investigatorDesignation: this.investigationLetter.investigatorDesignation,
        investigatorEmail: this.investigationLetter.investigatorEmail,
        investigatorName: this.investigationLetter.investigatorName,
        investigatorPhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.investigatorPhoneNumber),
        recipientNumber: this.investigationLetter.recipientNumber
      });
    }
  }

  disableForm(){
    FormUtils.disableFormControlStatus(this.investigationLetterForm, ['letterMethod', 'recipientNumber','administratorName',
        'letterSentToPersonId', 'addressLine1', 'addressLine2', 'addressCity', 'addressState',
        'addressZip', 'addressZipExt', 'selectedChildsPersonId', 'incidentLocation',
        'incidentSummary', 'investigatorName', 'investigatorEmail', 'investigatorDesignation', 'investigatorPhoneNumber']);
  }

  showReceiptNumber() {
    this.displayReceiptNumber = ['RCM', 'RCE', 'RCF', 'REF'].includes(
      this.investigationLetterForm.controls.letterMethod.value);
    if (!this.displayReceiptNumber) {
      this.investigationLetterForm.controls.recipientNumber.setValue('');
    }
  }

  delete() {
    if(this.investigationLetter.id){
      const initialState = {
        title: 'Child Death Report - 2899e',
        message: 'Are you sure you want to delete this information?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.caseService.deleteInvestigationLetter(this.investigationLetterForm.get('id').value)
            .subscribe((res) => {
              this.router.navigate(['/case/letters/investigation-letters']);
            });
        }
      });
    } else{
      this.router.navigate(['/case/letters/investigation-letters']);
    }
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
    }else {
      if (this.validateFormGroup(this.investigationLetterForm)) {
        this.caseService.downloadInvestigationLetter(this.investigationLetter.id, true)
          .subscribe(blob => saveAs(blob, 'Draft_Child Death Report - 2899e' + '_' + this.investigationLetterRes.caseId + '.pdf'));
      }
    }
  }

  download() {
    let letterName = this.investigationLetterRes.letterTypes.find(value => 'CDR' === value.code).decode;
    this.caseService.downloadInvestigationLetter(this.investigationLetter.id, false)
      .subscribe(blob => {
        saveAs(blob, letterName + '_' + this.investigationLetterRes.caseId + '.pdf');
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      });

  }

  searchPerson() {
    this.environmentSettings.environmentName === 'Local' ?
      this.cookieService.set('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }),
        10000, '/', undefined, undefined, 'Lax') :
      sessionStorage.setItem('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }));

    const redirectUrl = 'case/letters/investigation-letters/CDR/' +
      (this.investigationLetter.id ? this.investigationLetter.id : '0');

    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/personList?personSearchFrom=investigationLetter&redirectToPageUrl=' + redirectUrl;
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

      this.caseService.saveInvestigationLetter(payload).subscribe(
        response => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          this.router.navigate(['/case/letters/investigation-letters']);
        }
      );
    }
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
  fmtPhoneNumber(event: any) {
    this.investigationLetterForm.patchValue({
      investigatorPhoneNumber: FormUtils.formatPhoneNumber(event.target.value)
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
