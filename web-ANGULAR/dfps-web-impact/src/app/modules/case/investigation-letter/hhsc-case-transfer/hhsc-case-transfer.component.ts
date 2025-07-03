import { Component, Inject, OnInit, OnDestroy, ViewChild } from '@angular/core';
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
  SET, FormUtils
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { InvestigationLetter, InvestigationLetterRes } from '../../model/InvestigationLetter';
import { CaseService } from '../../service/case.service';
import { HhscCaseTransferValidators } from './hhsc-case-transfer.validator';
import { saveAs } from 'file-saver';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'hhsc-case-transfer',
  templateUrl: './hhsc-case-transfer.component.html'
})
export class HhscCaseTransferComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;

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
  letterTypeDecode: string;
  errorNoDataMessage: any;
  disableDeleteButton = false;
  disableDownloadButton = false;
  disableDownloadDraftButton = false;

  constructor(
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

  ngOnInit(): void {
    this.navigationService.setTitle('HHSC Case Transfer');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    const formLetterType = 'HCT';
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ?
      this.route.snapshot.paramMap.get('investigationLetterId') : '0';

    const selectedPersonId = this.route.snapshot.queryParamMap.get('selectedPersonId');

    this.caseService.getInvestigationLetter(investigationLetterId).subscribe(response => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;

      this.investigationLetter.letterType = formLetterType;

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

        FormUtils.disableFormControlStatus(this.investigationLetterForm,
          ['letterMethod', 'recipientNumber', 'allegationSummary', 'dispositionSummary', 'monitoringConcerns',
            'investigatorName', 'investigatorDesignation', 'investigatorPhoneNumber', 'investigatorEmail']);
      }

      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));

      this.formOrgValue = this.getFormValue();
    });
  }

  createForm() {
    this.investigationLetterForm = this.formBuilder.group(
      {
        id: [''],
        letterMethod: [''],
        letterType: ['', [Validators.required]],
        allegationSummary: ['', [Validators.required]],
        dispositionSummary: ['', [Validators.required]],
        monitoringConcerns: ['', [Validators.required]],
        caseId: [''],
        eventId: [''],
        investigatorPersonId: [''],
        resourceId: [''],
        stageId: [''],
        investigatorDesignation: ['', [Validators.required]],
        investigatorEmail: ['', [Validators.required]],
        investigatorName: ['', [Validators.required]],
        investigatorPhoneNumber: ['', [Validators.required]],
        recipientNumber: ['']
      }, {
      validators: [
        HhscCaseTransferValidators.phoneNumberPattern,
        HhscCaseTransferValidators.requiredFieldValidations,
        HhscCaseTransferValidators.validateEmailPattern()
      ]
    }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'HCT' === value.code).decode;
    if (this.investigationLetter) {
      this.investigationLetterForm.setValue({
        id: this.investigationLetter.id,
        letterMethod: this.investigationLetter.letterMethod,
        letterType: this.investigationLetter.letterType,
        allegationSummary: this.investigationLetter.allegationSummary,
        dispositionSummary: this.investigationLetter.dispositionSummary,
        monitoringConcerns: this.investigationLetter.monitoringConcerns,
        caseId: this.investigationLetter.caseId,
        eventId: this.investigationLetter.eventId,
        investigatorPersonId: this.investigationLetter.investigatorPersonId,
        resourceId: this.investigationLetter.resourceId,
        stageId: this.investigationLetter.stageId,
        investigatorDesignation: this.investigationLetter.investigatorDesignation,
        investigatorEmail: this.investigationLetter.investigatorEmail,
        investigatorName: this.formatName(this.investigationLetter.investigatorName),
        investigatorPhoneNumber: FormUtils.formatPhoneNumber(this.investigationLetter.investigatorPhoneNumber),
        recipientNumber: this.investigationLetter.recipientNumber
      });

      this.showReceiptNumber();
    }
  }

  delete() {
    const initialState = {
      message: 'Are you sure you want to delete this information?',
      title: 'HHSC Case Transfer',
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
    }else{
      if (this.validateFormGroup(this.investigationLetterForm)) {
        if(this.investigationLetter.event === 'COMP'){
          this.caseService.downloadInvestigationLetter(this.investigationLetter.id, false)
          .subscribe(blob => saveAs(blob, 'HHSC Case Transfer' + '_' + this.investigationLetterRes.caseId + '.pdf'));
        } else {
          this.caseService.downloadInvestigationLetter(this.investigationLetter.id, true)
          .subscribe(blob => saveAs(blob, 'Draft_' + 'HHSC Case Transfer' + '_' + this.investigationLetterRes.caseId + '.pdf'));
        }
      }
    }
  }

  showReceiptNumber() {
    this.displayReceiptNumber = ['RCM', 'RCE', 'RCF', 'REF'].includes(this.investigationLetterForm.controls.letterMethod.value);
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

  fmtPhoneNumber(event: any) {
    this.investigationLetterForm.patchValue({
      investigatorPhoneNumber: FormUtils.formatPhoneNumber(event.target.value)
    });
  }

  getCustomStyles() {
    return 'height: 130px';
  }

  formatName(value: any) {
    if(value){
      const index = value.indexOf(',');
      const firstAndMiddleName = value.substring(index+1, value.length);
      const lastName = value.substring(0, index);
      return firstAndMiddleName.concat(' ').concat(lastName);
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
