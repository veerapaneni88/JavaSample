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
  Address, SET, DfpsCommonValidators
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { InvestigationLetter, InvestigationLetterRes } from '../../model/InvestigationLetter';
import { CaseService } from '../../service/case.service';
import { CookieService } from 'ngx-cookie-service';
import { AnInvestigationParentsVictimValidator } from './an-investigation-parents-victim.validator';
import { saveAs } from 'file-saver';
import { last } from 'rxjs/operators';
import {dfpsValidatorErrors} from "../../../../../messages/validator-errors";
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'an-investigation-parents-victim',
  templateUrl: './an-investigation-parents-victim.component.html',
  styleUrls: ['./an-investigation-parents-victim.component.css']
})
export class AnInvestigationParentsVictimComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('searchPersonButton', { read: ElementRef}) searchPersonButtonRef;

  modalSubscription: Subscription;
  investigationLetter: InvestigationLetter;
  investigationLetterForm: FormGroup;
  investigationLetterRes: InvestigationLetterRes;
  formOrgValue: string;

  hideRecipientNumber = true;
  hideAttentionMessage = false;
  hideDeleteButton = false;
  hideDownloadButton = false;
  hideDownloadDraftButton = false;
  hideSaveSubmitButton = false;
  hideSaveButton = false;
  hideValidateButton = false;
  hideSearchButton = false;
  letterType: string;
  letterTypeDecode: string;
  errorNoDataMessage: any;
  disableDeleteButton = false;
  disableDownloadButton = false;
  disableLetterMethod = false;
  disableCheckBox = false;
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
    this.navigationService.setTitle('AN Investigation Results to Parents of Victims - 2893');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    const formLetterType = "AIR";
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
    this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    const selectedPersonId = this.route.snapshot.queryParamMap.get('selectedPersonId');

    this.caseService.getInvestigationLetter(investigationLetterId).subscribe(response => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;

      if (this.investigationLetterRes.operationNames.length == 0) {
        this.errorNoDataMessage = dfpsValidatorErrors.MSG_OPERATION_NAME_EMPTY;
      }

      let isFormChanged = false;
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
        this.investigationLetter.letterBodyDear = this.formatFullName(selectedPerson);
        this.investigationLetter.addressLine1 = selectedPerson.personStreet1;
        this.investigationLetter.addressLine2 = selectedPerson.personStreet2;
        this.investigationLetter.addressCity = selectedPerson.personCity;
        this.investigationLetter.addressState = selectedPerson.personState ? selectedPerson.personState : 'TX';
        this.investigationLetter.addressZip = selectedPerson.personZip;

        setTimeout(() => {
          if (this.searchPersonButtonRef && this.searchPersonButtonRef.nativeElement) {
            this.searchPersonButtonRef.nativeElement.lastChild.focus();
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
            if (this.searchPersonButtonRef && this.searchPersonButtonRef.nativeElement) {
              this.searchPersonButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
        }

      }
      this.investigationLetter.letterType = formLetterType;

      this.loadInvestigationLetterFormData();
      this.showOrHideRecipientNumber(this.investigationLetter.letterMethod);
      if (this.investigationLetter.id === null || this.investigationLetter.id === '0') {
        this.disableDeleteButton = true;
        this.disableDownloadDraftButton = true;
      }

      if (this.investigationLetter.event === 'COMP' || this.investigationLetterRes.pageMode === 'VIEW') {

        this.hideDeleteButton = true;
        this.hideDownloadDraftButton = true;
        this.hideSaveSubmitButton = true;
        this.hideSaveButton = true;
        this.hideValidateButton = true;
        this.hideSearchButton = true;
        this.disableLetterMethod = true;
        this.disableCheckBox = true;
        this.disableForm()
      }

      if (this.investigationLetterRes.pageMode === 'VIEW' || this.investigationLetter.event !== 'COMP') {
        this.hideDownloadButton = true;
      }
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      if (selectedPersonId || isFormChanged) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
      }

      this.storeSub = this.store.select(state => state.dirtyCheck.isDirty).subscribe((res) => {
        this.isPageDirty = res;
      })
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
        letterMethod: [''],
        letterType: [''],
        recipientNumber: [''],
        addressLine1: [''],
        addressLine2: [''],
        addressCity: [''],
        addressState: ['TX'],
        addressZip: ['', [DfpsCommonValidators.zipPattern]],
        addressZipExt: [''],
        letterBodyDear: [''],
        bodyOperationName: [''],
        selectedChildsPersonId: [''],
        letterSentToPersonId: [''],
        administratorName: [''],
        investigatorName: [''],
        investigatorPersonId: [''],
        investigatorDesignation: [''],
        investigatorPhoneNumber: [''],
        investigatorEmail: [''],
        interviewStatus: [''],
        tableData: [''],

      }, {
        validators: [
          AnInvestigationParentsVictimValidator.requiredFieldValidations,
          AnInvestigationParentsVictimValidator.validateEmailPattern(),
          AnInvestigationParentsVictimValidator.phoneNumberPattern,
          AnInvestigationParentsVictimValidator.findingsCheck,
          AnInvestigationParentsVictimValidator.zipPattern
        ]
      }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'AIR' === value.code).decode;
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
        letterBodyDear: this.investigationLetter.letterBodyDear,
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
        investigatorDesignation: this.investigationLetter.investigatorDesignation,
        investigatorEmail: this.investigationLetter.investigatorEmail,
        investigatorName: this.investigationLetter.investigatorName,
        investigatorPhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.investigatorPhoneNumber),
        recipientNumber: this.investigationLetter.recipientNumber,
        tableData: this.investigationLetter.allegationFindingList || [],
        bodyOperationName: this.investigationLetterRes.investigationLetter?.fecilityId !=null ?
        this.investigationLetterRes.investigationLetter?.fecilityId.toString() : '',
        interviewStatus: this.investigationLetter.interviewStatus
      });
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
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

  disableForm(){
    FormUtils.disableFormControlStatus(this.investigationLetterForm, ['letterMethod', 'recipientNumber',
        'letterSentToPersonId', 'addressLine1', 'addressLine2', 'addressCity', 'addressState','administratorName',
        'addressZip', 'addressZipExt', 'selectedChildsPersonId', 'bodyOperationName','letterBodyDear',
        'interviewStatus', 'investigatorName', 'investigatorEmail', 'investigatorDesignation', 'investigatorPhoneNumber']);
  }

  letterMethodChange() {
    let letterMethodVal = ['RCM', 'RCE', 'RCF', 'REF'].includes(this.investigationLetterForm.controls.letterMethod.value);
    if(letterMethodVal){
      this.hideRecipientNumber = false;
    } else {
      this.hideRecipientNumber = true;
    }
  }

  showOrHideRecipientNumber(letterMethod){
    if (['RCM', 'REF', 'RCF', 'RCE'].includes(letterMethod)) {
      this.hideRecipientNumber = false;
    } else {
      this.hideRecipientNumber = true;
    }
  }

  delete() {
    if(this.investigationLetter.id){
      const initialState = {
        title: 'A/N Investigation Results to Parents of Victims - 2893',
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
        showCancel: false
      };

      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    } else if (this.validateFormGroup(this.investigationLetterForm)) {
      const letterName = 'AN Investigation Results to Parents of Victims - 2893';
      this.caseService.downloadInvestigationLetter(this.investigationLetter.id, true)
        .subscribe(blob => saveAs(blob, 'Draft_'+letterName + '_' + this.investigationLetterRes.caseId + '.pdf'));
    }
  }

  download() {
    let letterName = 'AN Investigation Results to Parents of Victims - 2893';
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

    const redirectUrl = 'case/letters/investigation-letters/AIR/' +
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

  saveInvestigationLetter() {
    this.investigationLetterForm.patchValue({tableData: this.investigationLetter.allegationFindingList});
    if (this.validateFormGroup(this.investigationLetterForm)) {
      const payload = Object.assign(this.investigationLetterRes.investigationLetter, this.investigationLetterForm.value);
      this.investigationLetterRes.investigationLetter = payload;
      this.investigationLetterRes.investigationLetter.fecilityId =
        (this.investigationLetterForm.controls.bodyOperationName.value === null ? 0 : this.investigationLetterForm.controls.bodyOperationName.value);
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

  updateIndex(isChecked: boolean, indexNumber: number){
    this.investigationLetter.allegationFindingList[indexNumber].checked = isChecked;
    this.investigationLetterForm.patchValue({tableData: this.investigationLetter.allegationFindingList});
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
    Object.keys(this.investigationLetter.allegationFindingList).forEach( key => {
      let val = this.investigationLetter.allegationFindingList[key].checked;
      formValue += key + '=[' + (val !== null ? val : '') + '],';
    });
    return formValue;
  }

}
