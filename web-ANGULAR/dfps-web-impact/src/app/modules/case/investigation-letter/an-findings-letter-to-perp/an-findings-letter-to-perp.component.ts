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
import { Allegation } from '../../model/Allegation';
import { InvestigationLetter, InvestigationLetterRes, AllegationFinding, AllegationType } from '../../model/InvestigationLetter';
import { AnFindingsLetterToPerpValidators } from './an-findings-letter-to-perp.validator';
import { last } from 'rxjs/operators';
import { dfpsValidatorErrors } from "../../../../../messages/validator-errors";
import { HelpService } from 'app/common/impact-help.service';
import { OfficeAddressService } from '@shared/service/office-address.service';
import { MailCode } from '@shared/model/MailCode';

@Component({
  selector: 'an-findings-letter-to-perp',
  templateUrl: './an-findings-letter-to-perp.component.html',
  styleUrls: ['./an-findings-letter-to-perp.component.css']
})
export class AnFindingsLetterToPerpComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;
  @ViewChild('searchApButton', { read: ElementRef }) searchApButtonRef;
  @ViewChild('searchStaffButton', { read: ElementRef }) searchStaffButtonRef;

  modalSubscription: Subscription;
  investigationLetter: InvestigationLetter;
  investigationLetterForm: FormGroup;
  investigationLetterRes: InvestigationLetterRes;
  allegationFindings: AllegationFinding[];
  formOrgValue: string;
  mailCode: MailCode;

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
  hideReceiptNumber = false;
  hideRecipientNumber = true;
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
    private searchService: SearchService,
    private officeAddressService: OfficeAddressService,
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
    this.navigationService.setTitle('AN Findings Letter to Perp - 2894');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
      this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    const selectedPersonId = this.route.snapshot.queryParamMap.get('selectedPersonId') ?
      this.route.snapshot.queryParamMap.get('selectedPersonId') : '0';

    this.caseService.getInvestigationLetterBySelectedPersonId(investigationLetterId, selectedPersonId).subscribe(response => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;
      this.investigationLetter.letterType = 'ANF';

      if (this.investigationLetterRes.operationNames.length == 0) {
        this.errorNoDataMessage = dfpsValidatorErrors.MSG_OPERATION_NAME_EMPTY;
      }
      var isFormChanged = false;
      if (selectedPersonId !== '0' && selectedPersonId) {
        const selectedPerson = this.environmentSettings.environmentName === 'Local'
          ? JSON.parse(this.cookieService.get('selectedPerson')) : JSON.parse(sessionStorage.getItem('selectedPerson'));

        const storedInvestigationLetterForm = this.environmentSettings.environmentName === 'Local'
          ? JSON.parse(this.cookieService.get('form_data')) : JSON.parse(sessionStorage.getItem('form_data'));

        this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('form_data', '/') : sessionStorage.removeItem('form_data');
        this.environmentSettings.environmentName === 'Local' ? this.cookieService.delete('selectedPerson', '/') : sessionStorage.removeItem('selectedPerson');
        this.investigationLetter = Object.assign(this.investigationLetter, storedInvestigationLetterForm);

        if (this.investigationLetter?.bodyOperationName !== null && this.investigationLetter?.bodyOperationName !== '') {
          this.investigationLetter.fecilityId = this.investigationLetter.bodyOperationName;
        }

        if (this.investigationLetter.lastPersonSearched === 'Alleged Perpetrator') {
          this.investigationLetter.letterSentToPersonId = selectedPerson.idPerson;
          this.investigationLetter.administratorName = this.formatFullName(selectedPerson);
          this.investigationLetter.letterBodyDear = this.formatFullName(selectedPerson);
          this.investigationLetter.addressLine1 = selectedPerson.personStreet1;
          this.investigationLetter.addressLine2 = selectedPerson.personStreet2;
          this.investigationLetter.addressCity = selectedPerson.personCity;
          this.investigationLetter.addressState = selectedPerson.personState ? selectedPerson.personState : 'TX';
          // zip value split is happening at loadInvestigationLetterFormData() method.
          this.investigationLetter.addressZip = selectedPerson.personZip;

          setTimeout(() => {
            if (this.searchApButtonRef && this.searchApButtonRef.nativeElement) {
              this.searchApButtonRef.nativeElement.lastChild.focus();
            }
          }, 100);
        }
      } else {
        let form_data = this.environmentSettings.environmentName === 'Local'
          ? this.cookieService.get('form_data') : sessionStorage.getItem('form_data');
        if (form_data) {
          const storedInvestigationLetterForm = JSON.parse(form_data);
          this.environmentSettings.environmentName === 'Local' ?
            this.cookieService.delete('form_data', '/') : sessionStorage.removeItem('form_data');
          if (storedInvestigationLetterForm) {
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
      if (this.searchService.getSelectedStaff() && this.searchService.getSearchSource() === 'AdminStaff') {
        this.investigationLetter = this.searchService.getFormData();
        this.investigationLetter.adminReviewStaffPersonId = this.searchService.getSelectedStaff().personId;
        let email = this.searchService.getSelectedStaff().email ? this.searchService.getSelectedStaff().email : '';
        let adminReviewStaffDetails = '';
        let fullName = '';

        // get office mail addres to display in Administrative Review Staff Information text area
        this.officeAddressService.getOfficeAddr(this.searchService.getSelectedStaff().personId)
          .subscribe((result) => {
            if (result) {
              this.mailCode = result;
              let fName = this.mailCode.firstName ? this.mailCode.firstName : '';
              let mName = this.mailCode.middleName ? this.mailCode.middleName : '';
              let lName = this.mailCode.lastName ? this.mailCode.lastName : '';

              let fullName: string = `${fName} ${mName} ${lName} \n`;
              let addressMailCode = this.mailCode.businessOfficeAddress;
              let fax = this.mailCode.number ? this.mailCode.number : '';
              adminReviewStaffDetails = fullName.replace('  ', ' ') +
                  addressMailCode + '\n' +
                  'Fax: ' + fax + '\n' +
                  'Email: ' + email + '\n';
            } else {
              adminReviewStaffDetails = fullName.replace('  ', ' ') +
              'Fax: ' + 'fax' + '\n' +
              'Email: ' + email + '\n';
            }
            this.investigationLetterForm.patchValue({
              adminReviewStaffDetails: adminReviewStaffDetails
            });
          });

        this.searchService.setSearchSource('');

        setTimeout(() => {
          if (this.searchStaffButtonRef && this.searchStaffButtonRef.nativeElement) {
            this.searchStaffButtonRef.nativeElement.lastChild.focus();
          }
        }, 100);
      }
      this.investigationLetter.allegedPerpetratorName = this.investigationLetter.administratorName;
      this.investigationLetter.letterType = 'ANF';
      this.loadInvestigationLetterFormData();
      this.investigationLetterForm.patchValue
      this.investigationLetter.apAllegationFindings = response.investigationLetter?.apAllegationFindings;
      if (selectedPersonId !== '0' && selectedPersonId) {
        this.investigationLetter.tableData = null;
        this.investigationLetter.apAllegationFindings = response.investigationLetter?.apAllegationFindings;
      }
      else if (null === this.investigationLetter.apAllegationFindings && this.investigationLetter.tableData !== null) {
        this.investigationLetter.apAllegationFindings = this.investigationLetter.tableData;
      }

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
          ['letterMethod', 'recipientNumber', 'administratorName', 'addressLine1', 'addressLine2',
            'addressCity', 'addressState', 'addressZip', 'addressZipExt', 'letterBodyDear', 'bodyOperationName',
            'adminReviewStaffDetails', 'letterBody', 'investigatorName', 'investigatorDesignation',
            'investigatorPhoneNumber', 'investigatorEmail']);
      }
      if (this.investigationLetter.event === 'COMP' && this.investigationLetterRes.pageMode === 'EDIT') {
        this.hideDownloadButton = false;
      }
      this.showOrHideRecipientNumber(this.investigationLetter.letterMethod);
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      if ((selectedPersonId !== '0' && selectedPersonId) || isFormChanged) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: true } }));
      }

      this.storeSub = this.store.select(state => state.dirtyCheck.isDirty).subscribe((res) => {
        this.isPageDirty = res;
      })
      if (this.searchService.getReturnUrl() && this.searchService.getReturnUrl().includes('staffsearch')) {
        setTimeout(() => {
          if (this.searchStaffButtonRef && this.searchStaffButtonRef.nativeElement) {
            this.searchStaffButtonRef.nativeElement.lastChild.focus();
          }
        }, 3000);
      }
      this.formOrgValue = this.getFormValue();
    });

  }

  createForm() {
    this.investigationLetterForm = this.formBuilder.group(
      {
        id: [''],
        letterMethod: [''],
        letterType: ['ANF'],
        recipientNumber: [''],
        allegedPerpetratorName: [''],
        administratorName: [''],
        addressLine1: [''],
        addressLine2: [''],
        addressCity: [''],
        addressState: ['TX'],
        addressZip: ['', [DfpsCommonValidators.zipPattern]],
        addressZipExt: [''],
        letterBodyDear: [''],
        letterBody: [''],
        allegationSummary: [''],
        incidentLocation: [''],
        interviewStatus: [''],
        dispositionSummary: [''],
        interviewDate: [''],
        allegedPerpetratorPhoneNumber: [''],
        caseId: [''],
        eventId: [''],
        fecilityId: [''],
        bodyOperationName: [''],
        operationHomeName: [''],
        investigatorFindings1: [''],
        investigatorFindings2: [''],
        letterAllegationLinkId: [''],
        adminReviewStaffPersonId: [''],
        adminReviewStaffDetails: [''],
        tableData: [''],
        selectedChildsPersonId: [''],
        investigatorPersonId: [''],
        childsPersonId: [''],
        letterSentFromPersonId: [''],
        letterSentToPersonId: [''],
        resourceId: [''],
        stageId: [''],
        incidentSummary: [''],
        investigatorName: [''],
        investigatorDesignation: [''],
        investigatorPhoneNumber: [''],
        investigatorEmail: [''],
        monitoringConcerns: [''],
        lastPersonSearched: [''],
        parentName: [''],
        childsFirstName: [''],
        seletedAllegationID: ['']
      }, {
      validators: [
        AnFindingsLetterToPerpValidators.requiredFieldValidations,
        AnFindingsLetterToPerpValidators.validateEmailPattern(),
        AnFindingsLetterToPerpValidators.phoneNumberPattern,
        AnFindingsLetterToPerpValidators.findingsCheck,
        AnFindingsLetterToPerpValidators.zipPattern
      ]
    }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'ANF' === value.code).decode;

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
            this.investigationLetter.addressZip.split('-')[1] : this.investigationLetter.addressZipExt) : this.investigationLetter.addressZipExt,
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
        seletedAllegationID: this.investigationLetter.letterSentToPersonId,
        resourceId: this.investigationLetter.resourceId,
        stageId: this.investigationLetter.stageId,
        incidentSummary: this.investigationLetter.incidentSummary,
        investigatorFindings1: this.investigationLetter.investigatorFindings1,
        investigatorFindings2: this.investigationLetter.investigatorFindings2,
        investigatorDesignation: this.investigationLetter.investigatorDesignation,
        investigatorEmail: this.investigationLetter.investigatorEmail,
        investigatorName: this.formatName(this.investigationLetter.investigatorName),
        investigatorPhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.investigatorPhoneNumber),
        letterBodyDear: this.investigationLetter.letterBodyDear,
        letterBody: this.investigationLetter.letterBody,
        monitoringConcerns: this.investigationLetter.monitoringConcerns,
        recipientNumber: this.investigationLetter.recipientNumber,
        lastPersonSearched: this.investigationLetter.lastPersonSearched,
        parentName: this.investigationLetter.parentName,
        childsFirstName: this.investigationLetter.childsFirstName,
        allegedPerpetratorName: this.investigationLetter.administratorName,
        adminReviewStaffDetails: this.investigationLetter.adminReviewStaffDetails,
        tableData: this.investigationLetter.apAllegationFindings ? this.investigationLetter.apAllegationFindings :
          this.investigationLetter.tableData ? this.investigationLetter.tableData : '',
        operationHomeName: this.investigationLetter.operationHomeName,
        bodyOperationName: this.investigationLetterRes.investigationLetter.fecilityId ?
          this.investigationLetterRes.investigationLetter.fecilityId.toString() :
          (this.investigationLetter.bodyOperationName ? this.investigationLetter.bodyOperationName : '')
      });
    }
  }

  formatFullName(value: any) {
    if (value) {
      let fullName = '';
      let firstName = value.nmPersonFirst ? value.nmPersonFirst : '';
      let middleName = value.nmPersonMiddle ? value.nmPersonMiddle : '';
      let lastName = value.nmpersonLast ? value.nmpersonLast : '';
      if (firstName.length != 0) { fullName = firstName.concat(' '); }
      if (middleName.length != 0) { fullName = fullName.concat(middleName).concat(' '); }
      if (lastName.length != 0) { fullName = fullName.concat(lastName); }
      if (fullName == null || fullName == '') {
        fullName = this.formatName(value.personFull);
      }
      return fullName.replace("~S~", "'");
    } else {
      return "";
    }
  }
  formatName(value: any) {
    if (value) {
      const index = value.indexOf(',');
      const firstAndMiddleName = value.substring(index + 1, value.length);
      const lastName = value.substring(0, index);
      return lastName != null && lastName != '' ? firstAndMiddleName.concat(' ').concat(lastName) : firstAndMiddleName;
    } else {
      return '';
    }
  }

  searchAllegedPerptrator() {
    this.investigationLetterForm.controls.lastPersonSearched.setValue('Alleged Perpetrator');
    this.searchPerson();
  }

  searchPerson() {
    this.environmentSettings.environmentName === 'Local' ?
      this.cookieService.set('form_data', JSON.stringify({ ...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }),
        10000, '/', undefined, undefined, 'Lax') :
      sessionStorage.setItem('form_data', JSON.stringify({ ...this.investigationLetterForm.value, isPageDirty: this.isPageDirty }));
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    const redirectUrl = 'case/letters/investigation-letters/ANF/' +
      (this.investigationLetter.id ? this.investigationLetter.id : '0');

    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/personList?personSearchFrom=investigationLetter&redirectToPageUrl=' + redirectUrl;
  }

  searchAdminReviewStaff() {
    this.helpService.loadHelp('Search');
    this.searchService.setSearchSource('AdminStaff');
    this.selectAdminReviewStaff();
  }

  selectAdminReviewStaff() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.environmentSettings.environmentName === 'Local' ?
      this.cookieService.set('form_data', JSON.stringify(this.investigationLetterForm.value),
        10000, '/', undefined, undefined, 'Lax') :
      sessionStorage.setItem('form_data', JSON.stringify(this.investigationLetterForm.value));
    const returnUrl = 'case/letters/investigation-letters/ANF/' +
      (this.investigationLetter.id ? this.investigationLetter.id : '0');
    this.searchService.setSearchSource('AdminStaff');
    this.searchService.setFormData(this.investigationLetterForm.value);
    this.searchService.setReturnUrl(returnUrl);
    this.cookieService.set('search_return_url', this.searchService.getReturnUrl(), 10000, '/', undefined, undefined, 'Lax');
    this.router.navigate(
      ['/case/letters/investigation-letters/ANF/' + (this.investigationLetter.id ? this.investigationLetter.id : '0') + '/staffsearch'],
      { skipLocationChange: false }
    );
  }

  delete() {
    const initialState = {
      message: 'Are you sure you want to delete this information?',
      title: 'A/N Findings Letter to Perp - 2894',
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
    if (this.isFormDirty()) {
      const initialState = {
        message: 'Please save the page before downloading the draft Letter.',
        title: 'Letter Detail',
        showCancel: false,
      };

      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });

    } else if (this.validateFormGroup(this.investigationLetterForm)) {
      const letterName = 'AN Findings Letter to Perp - 2894';
      if (this.investigationLetter.event === 'COMP') {
        this.caseService.downloadInvestigationLetter(this.investigationLetter.id, false)
          .subscribe(blob => {
            saveAs(blob, letterName + '_' + this.investigationLetterRes.caseId + '.pdf');
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          });
      } else {
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
    this.investigationLetterRes.investigationLetter.isSubmit = true;
    this.saveInvestigationLetter();
  }

  saveInvestigationLetter() {
    // This is for findings table data validation purpose
    this.investigationLetterForm.patchValue({ tableData: this.investigationLetter.apAllegationFindings });
    if (this.validateFormGroup(this.investigationLetterForm)) {
      const payload = Object.assign(this.investigationLetterRes.investigationLetter, this.investigationLetterForm.value);
      payload.apAllegationFindings = this.investigationLetter.apAllegationFindings;
      this.investigationLetterRes.investigationLetter = payload;
      this.investigationLetterRes.investigationLetter.fecilityId =
        (this.investigationLetterForm.controls.bodyOperationName.value === null ?
          0 : this.investigationLetterForm.controls.bodyOperationName.value);

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

    // this.investigationLetterForm.patchValue({tableData: this.investigationLetter.apAllegationFindings});
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
    return 'line-height: 1.4';
  }

  letterMethodChange() {
    const letterMethod = this.investigationLetterForm.get('letterMethod').value;
    this.showOrHideRecipientNumber(letterMethod);
    if (this.hideRecipientNumber) {
      this.investigationLetterForm.patchValue({ recipientNumber: '' });
    }
  }

  showOrHideRecipientNumber(letterMethod) {
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

  isFormDirty() {
    let formNewValue = this.getFormValue();
    return (formNewValue.localeCompare(this.formOrgValue) !== 0);
  }

  getFormValue() {
    let formValue = '';
    Object.keys(this.investigationLetterForm.controls).forEach(key => {
      let val = this.investigationLetterForm.controls[key].value;
      formValue += key + '=[' + (val !== null ? val : '') + '],';
    });
    return formValue;
  }

}
