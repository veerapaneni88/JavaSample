import { Component, Inject, OnDestroy, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
  Address, DfpsAddressValidatorComponent, DfpsConfirmComponent, DfpsFormValidationDirective, DirtyCheck,
  ENVIRONMENT_SETTINGS, NavigationService, SET, DfpsCommonValidators, FormUtils
} from 'dfps-web-lib';
import { saveAs } from 'file-saver';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { Subscription, Observable } from 'rxjs';
import { CaseService } from '../../service/case.service';
import { InvestigationLetter, InvestigationLetterRes, AllegationFinding, AllegationType } from '../../model/InvestigationLetter';
import { CciInvestigationFindingValidators } from './cci-investigation-finding.validator';
import { map, take } from 'rxjs/operators';
import { dfpsValidatorErrors } from 'messages/validator-errors';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'cci-investigation-finding',
  templateUrl: './cci-investigation-finding.component.html',
  styleUrls: ['./cci-investigation-finding.component.css']
})
export class CciInvestigationFindingComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('searchButton', { read: ElementRef}) searchButtonRef;

  modalSubscription: Subscription;
  investigationLetter: InvestigationLetter;
  investigationLetterForm: FormGroup;
  investigationLetterRes: InvestigationLetterRes;
  allegationFindings: AllegationFinding[];
  formOrgValue: string;

  hideAttentionMessage = false;
  hideDeleteButton = false;
  hideDownloadDraftButton = false;
  hideDownloadButton = false;
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
  disableDownloadDraftButton = false;
  investigatorFindings: any[];
  displayAllegationTable = false;
  displayOption1 = false;
  displayOption2 = false;
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
    this.navigationService.setTitle('CCI Investigation Findings to Operation');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    this.investigatorFindings = [
      { label: 'Option 1 - CCI Findings', value: 'investigatorFindings1' },
      { label: 'Option 2 - ARIF/SOAH Findings', value: 'investigatorFindings2' }
    ];
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
    this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    const selectedResourceData = this.searchService.getSelectedResource();
    let isFormChanged = false;
    this.caseService.getInvestigationLetter(investigationLetterId).pipe(take(1), map((response) => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;
      this.investigationLetter.letterType ='CIF';

      if(selectedResourceData){
       const storedInvestigationLetterForm = this.environmentSettings.environmentName === 'Local'
            ? this.cookieService.get('form_data') : sessionStorage.getItem('form_data');
        this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data','/') : sessionStorage.removeItem('form_data');

        this.investigationLetter = JSON.parse(storedInvestigationLetterForm);
        this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data','/') : sessionStorage.removeItem('form_data');
      }
      this.investigationLetter.cciAllegationFindings= response.investigationLetter?.cciAllegationFindings;
      if (this.investigationLetter.id === null || this.investigationLetter.id === '0') {
        if (response.operationAgencyHomeDto.classOperationName == null || response.operationAgencyHomeDto.classOperationName == '') {
          this.errorNoDataMessage = dfpsValidatorErrors.MSG_OPERATION_NAME_EMPTY;
        }
        this.investigationLetter.operationHomeName= response.operationAgencyHomeDto.classOperationName;
        this.investigationLetter.resourceId= response.operationAgencyHomeDto.operationImpactResourceId;
        this.investigationLetter.addressLine1 =response.operationAgencyHomeDto.addressLine1;
        this.investigationLetter.addressLine1 =response.operationAgencyHomeDto.addressLine1;
        this.investigationLetter.addressLine2 =response.operationAgencyHomeDto.addressLine2;
        this.investigationLetter.addressCity =response.operationAgencyHomeDto.addressCity;
        this.investigationLetter.addressZip = response.operationAgencyHomeDto.addressZip;
        this.investigationLetter.addressState = response.operationAgencyHomeDto.state;
        this.investigationLetter.administratorName = response.operationAgencyHomeDto.administratorName;
        this.investigationLetter.operationNumber = this.investigationLetterRes.operationAgencyHomeDto.classOperationNumber;
      }
      return response;

    }), map(() => {
      if(selectedResourceData){
        this.caseService.getOperationHomeByResourceId(selectedResourceData.resourceId).subscribe( resp => {
          if(resp){

          this.investigationLetter.operationHomeName= resp.operationAgencyHomeDto.classOperationName;
          this.investigationLetter.resourceId= resp.operationAgencyHomeDto.operationImpactResourceId;
          this.investigationLetter.addressLine1 =resp.operationAgencyHomeDto.addressLine1;
          this.investigationLetter.addressLine2 =resp.operationAgencyHomeDto.addressLine2;
          this.investigationLetter.addressCity =resp.operationAgencyHomeDto.addressCity;
          this.investigationLetter.addressZip = resp.operationAgencyHomeDto.addressZip;
          this.investigationLetter.addressState = resp.operationAgencyHomeDto.state;
          this.investigationLetter.administratorName = resp.operationAgencyHomeDto.administratorName;
          this.investigationLetter.letterBodyDear = resp.operationAgencyHomeDto.administratorName;
          this.investigationLetter.operationNumber = resp.operationAgencyHomeDto.classOperationNumber;

          setTimeout(() => {
            if (this.searchButtonRef && this.searchButtonRef.nativeElement) {
              this.searchButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
          }
         this.loadInvestigationLetterFormData();
         this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
         this.searchService.setSelectedResource(null);
         return resp;
        });
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
      return '';
    })
    ).subscribe( res => {
      if(!selectedResourceData){
        this.loadInvestigationLetterFormData();
      }
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));

      if (selectedResourceData || isFormChanged) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
      }
    });

    if (investigationLetterId === null || investigationLetterId === '0') {
      this.disableDeleteButton = true;
      this.disableDownloadDraftButton = true;
    }

    this.storeSub = this.store.select(state => state.dirtyCheck.isDirty).subscribe((res) => {
      this.isPageDirty = res;
    });

  }

  createForm() {
    this.investigationLetterForm = this.formBuilder.group(
      {
        id: [''],
        letterMethod: ['',[Validators.required]],
        letterType: ['CIF'],
        operationHomeName: ['',[Validators.required]],
        operationNumber: ['',[Validators.required]],
        administratorName: ['',[Validators.required]],
        addressLine1: ['',[Validators.required]],
        addressLine2: [''],
        addressCity: ['',[Validators.required]],
        addressState: ['',[Validators.required]],
        addressZip: ['', [Validators.required]],
        addressZipExt: [''],
        letterBodyDear: ['',[Validators.required]],
        investigatorFindings1: [''],
        investigatorFindings2: [''],
        tableData: [''],
        investigatorFindings: [''],
        investigatorName: ['',[Validators.required]],
        investigatorDesignation: ['',[Validators.required]],
        investigatorPhoneNumber: ['',[Validators.required]],
        investigatorEmail: ['',[Validators.required]],
        allegationSummary: [''],
        caseId: [''],
        eventId: [''],
        fecilityId: [''],
        investigatorPersonId: [''],
        letterSentFromPersonId: [''],
        letterSentToPersonId: [''],
        resourceId: [''],
        stageId: [''],
        recipientNumber: ['']
      }, {
      validators: [
       CciInvestigationFindingValidators.requiredFieldValidations,
       CciInvestigationFindingValidators.validateEmailPattern,
       CciInvestigationFindingValidators.findingsCheck,
       CciInvestigationFindingValidators.phoneNumberPattern,
       CciInvestigationFindingValidators.zipPattern
      ]
    }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'CIF' === value.code).decode;

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
        allegationSummary: this.investigationLetter.allegationSummary,
        letterMethod: this.investigationLetter.letterMethod,
        letterType: this.investigationLetter.letterType,
        caseId: this.investigationLetter.caseId,
        eventId: this.investigationLetter.eventId,
        fecilityId: this.investigationLetter.fecilityId,
        investigatorPersonId: this.investigationLetter.investigatorPersonId,
        letterSentFromPersonId: this.investigationLetter.letterSentFromPersonId,
        letterSentToPersonId: this.investigationLetter.letterSentToPersonId,
        resourceId: this.investigationLetter.resourceId,
        stageId: this.investigationLetter.stageId,
        investigatorFindings1: this.investigationLetter.investigatorFindings1,
        investigatorFindings2: this.investigationLetter.investigatorFindings2,
        investigatorDesignation: this.investigationLetter.investigatorDesignation,
        investigatorEmail: this.investigationLetter.investigatorEmail,
        investigatorName: this.formatName(this.investigationLetter.investigatorName),
        investigatorPhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.investigatorPhoneNumber),
        letterBodyDear: this.formatName(this.investigationLetter.letterBodyDear),
        recipientNumber: this.investigationLetter.recipientNumber,
        tableData: this.investigationLetter.cciAllegationFindings ? this.investigationLetter.cciAllegationFindings : '',
        operationHomeName: this.investigationLetter.operationHomeName,
        operationNumber: this.investigationLetter.operationNumber,
        investigatorFindings: (this.investigationLetter.investigatorFindings1) ? 'investigatorFindings1' :
              (this.investigationLetter.investigatorFindings2? 'investigatorFindings2': '')
      });
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
        ['letterMethod', 'recipientNumber', 'operationHomeName','operationNumber','investigatorFindings','administratorName', 'addressLine1', 'addressLine2',
          'addressCity', 'addressState', 'addressZip', 'addressZipExt', 'letterBodyDear', 'investigatorName', 'investigatorDesignation',
          'investigatorPhoneNumber', 'investigatorEmail']);
    }
    if (this.investigationLetterRes.pageMode === 'VIEW' || this.investigationLetter.event !== 'COMP') {
      this.hideDownloadButton = true;
    }
    this.displayAllegationFindings();

    this.showOrHideRecipientNumber(this.investigationLetter.letterMethod);
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));

    this.formOrgValue = this.getFormValue();
  }

  formatName(value: any) {
    if(value){
      const index = value.indexOf(',');
      const firstAndMiddleName = value.substring(index+1, value.length);
      const lastName = value.substring(0, index);
      return firstAndMiddleName.concat(" ").concat(lastName);
    } else {
        return "";
    }
  }


  searchResource() {
   
    this.environmentSettings.environmentName === 'Local' ?
    this.cookieService.set('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }),
      10000, '/', undefined, undefined, 'Lax') :
      sessionStorage.setItem('form_data', JSON.stringify({...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }));
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const returnUrl = 'case/letters/investigation-letters/CIF/' +
      (this.investigationLetter.id ? this.investigationLetter.id : '0');
    this.searchService.setSearchSource('ResourceSearch');
    this.searchService.setFormData(this.investigationLetterForm.value);
    this.searchService.setReturnUrl(returnUrl);
    this.router.navigate(
      ['/case/letters/investigation-letters/CIF/' + (this.investigationLetter.id ? this.investigationLetter.id : '0') + '/resourcesearch',
      { source: 'non-sscc' },],
      { skipLocationChange: false }
    )

  }


  delete() {
    const initialState = {
      message: 'Are you sure you want to delete this information?',
      title: 'CCI Investigation Findings to Operation',
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

  downloadDraft() {
    if(this.isFormDirty()) {
      const initialState = {
        message: 'Please save the page before downloading the draft Letter.',
        title: 'Letter Detail',
        showCancel: false
      };

      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });

    } else {
      if (this.validateFormGroup(this.investigationLetterForm)) {
        let letterName = this.investigationLetterRes.letterTypes.find(value => 'CIF' === value.code).decode;
        this.caseService.downloadInvestigationLetter(this.investigationLetter.id, true)
          .subscribe(blob => {
            saveAs(blob, 'Draft_'+letterName + '_' + this.investigationLetterRes.caseId + '.pdf');
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          });
      }
    }
  }

  download() {
   let letterName = this.investigationLetterRes.letterTypes.find(value => 'CIF' === value.code).decode;
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

  saveInvestigationLetter() {
    // This is for findings table data validation purpose
    this.investigationLetterForm.patchValue({tableData: this.investigationLetter.cciAllegationFindings});
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

  getCustomStyles() {
    return 'line-height: 3.5';
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

   displayAllegationFindings(){
      const value = this.investigationLetterForm.get('investigatorFindings').value;
      if(value && value === 'investigatorFindings1'){
        this.displayOption1 = true;
        this.displayAllegationTable = true;
        this.displayOption2 = false;
        this.investigationLetterForm.get('investigatorFindings1').setValue(true);
        this.investigationLetterForm.get('investigatorFindings2').setValue(false);
      }
      if(value && value === 'investigatorFindings2'){
        this.displayOption2 = true;
        this.displayAllegationTable = true;
        this.displayOption1 = false;
        this.investigationLetterForm.get('investigatorFindings2').setValue(true);
        this.investigationLetterForm.get('investigatorFindings1').setValue(false);
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
