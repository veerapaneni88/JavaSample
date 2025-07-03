import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { 
  DfpsFormValidationDirective, 
  DfpsConfirmComponent,
  DirtyCheck, 
  NavigationService, 
  FormUtils,
  SET } from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { InvestigationLetter, InvestigationLetterRes } from '../../model/InvestigationLetter';
import { CaseService } from '../../service/case.service';
import { VisitAcknowledgementValidator } from './visit-acknowledgement.validator';
import { saveAs } from 'file-saver';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'visit-acknowledgement',
  templateUrl: './visit-acknowledgement.component.html'
})
export class VisitAcknowledgementComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

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
  letterTypeDecode: string;
  errorNoDataMessage: any;
  disableDeleteButton = false;
  disableDownloadButton = false;

  disableDownloadDraftButton = false;

  constructor(
    private navigationService: NavigationService,
    private helpService: HelpService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: BsModalService,
    private caseService: CaseService, 
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnDestroy() {
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Visit Acknowledgement Form - 3014');
    this.investigationLetterForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    const formLetterType = "VAF";
    this.createForm();
    const investigationLetterId = this.route.snapshot.paramMap.get('investigationLetterId') ? 
    this.route.snapshot.paramMap.get('investigationLetterId') : '0';
    this.caseService.getInvestigationLetter(investigationLetterId).subscribe(response => {
      this.investigationLetterRes = response;
      this.investigationLetter = response.investigationLetter;
      this.investigationLetter.letterType = formLetterType;
      this.loadInvestigationLetterFormData();
      this.showReceiptNumber();
      if (this.investigationLetterRes.pageMode === 'VIEW' || this.investigationLetter.event === 'COMP') {
        this.hideDeleteButton = true;
        this.hideDownloadDraftButton = true;
        this.hideSaveButton = true;
        this.hideSaveSubmitButton = true;
        FormUtils.disableFormControlStatus(this.investigationLetterForm,
                            ['letterMethod', 'recipientNumber', 'operationHomeName']);
      }
      if (this.investigationLetter.id === null || this.investigationLetter.id === '0') {
        this.disableDeleteButton = true;
        this.disableDownloadButton = true;
        this.disableDownloadDraftButton = true;
      }
      if (this.investigationLetter.event === 'COMP' && this.investigationLetterRes.pageMode === 'EDIT') {
        this.hideDownloadButton = false;
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
        letterType: [''],
        caseId: [''],
        stageId: [''],
        eventId: [''],
        operationHomeName: [''],
        recipientNumber: ['']
      }, {
        validators: [
          VisitAcknowledgementValidator.validateLetterInformation
        ]
      }
    );
  }

  loadInvestigationLetterFormData() {
    this.letterTypeDecode = this.investigationLetterRes.letterTypes.find(value => 'VAF' === value.code).decode;
    if (this.investigationLetter) {
      this.investigationLetterForm.setValue({
        id: this.investigationLetter.id,
        letterMethod: this.investigationLetter.letterMethod,
        letterType: this.investigationLetter.letterType,
        caseId: this.investigationLetter.caseId,
        eventId: this.investigationLetter.eventId,
        stageId: this.investigationLetter.stageId,
        operationHomeName: this.investigationLetter.operationHomeName,
        recipientNumber: this.investigationLetter.recipientNumber 
      });
    }
  }

  showReceiptNumber(){
    this.displayReceiptNumber = ['RCM', 'RCE', 'RCF', 'REF'].includes(
      this.investigationLetterForm.controls.letterMethod.value);
    if (!this.displayReceiptNumber) {
      this.investigationLetterForm.controls.recipientNumber.setValue('');
    }
  }

  delete() {
    const initialState = {
      title: 'Visit Acknowledgement Form - 3014',
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
          .subscribe(blob => saveAs(blob, 'Draft_Visit Acknowledgement Form - 3014' + '_' + this.investigationLetterRes.caseId + '.pdf'));
      }
    }
  }

  download() {
    let letterName = this.investigationLetterRes.letterTypes.find(value => 'VAF' === value.code).decode;
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
        this.caseService.saveInvestigationLetter(payload).subscribe(
          response => {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['/case/letters/investigation-letters']);
          }
        );
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
