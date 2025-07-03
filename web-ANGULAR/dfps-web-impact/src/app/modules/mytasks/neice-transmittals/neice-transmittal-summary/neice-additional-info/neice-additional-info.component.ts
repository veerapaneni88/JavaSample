import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { DfpsFormValidationDirective, DirtyCheck } from 'dfps-web-lib';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Dropdown } from 'primeng/dropdown';
import { AdditionalInformation } from '../../model/NeiceTransmittalSummary';
import { NeiceTransmittalSummaryValidators } from '../neice-transmittal-summary.validator';

@Component({
  selector: 'app-neice-additional-info',
  templateUrl: './neice-additional-info.component.html',
  styleUrls: ['./neice-additional-info.component.css']
})
export class NeiceAdditionalInfoComponent extends DfpsFormValidationDirective implements OnInit {

  neiceAdditionalInfoForm: FormGroup;
  additionalInformationRequest: AdditionalInformation;

  constructor(private modalRef: BsModalRef, private formBuilder: FormBuilder, public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  @Input() additionalInformations: Dropdown[];

  @Output() additionalInformationEvent: EventEmitter<any> = new EventEmitter();

  sendRequest() {
    if (this.validateFormGroup(this.neiceAdditionalInfoForm) && this.neiceAdditionalInfoForm.value) {
      this.closeModal();
      this.additionalInformationRequest = this.neiceAdditionalInfoForm.getRawValue();
      this.additionalInformationEvent.emit(this.additionalInformationRequest);
    }
  }

  ngOnInit() {
    this.createForm();
  }

  createForm() {
    this.neiceAdditionalInfoForm = this.formBuilder.group(
      {
        additionalInformation: ['', [Validators.required]],
        comments: ['']
      }, {
      validators: [NeiceTransmittalSummaryValidators.validateAdditionalInfoComments]
    }
    );
  }

  closeModal() {
    this.modalRef.hide();
    document.getElementById('btnRequestAdditionalInfo').focus();
  }

  preventTabbing(event: KeyboardEvent) {
    if ((event.shiftKey && event.key === 'Tab') && (event.target as Element).id === 'closeButton') {
      document.getElementById('sendButton').focus();
      event.preventDefault();
    } else if (!event.shiftKey && event.key === 'Tab' && (event.target as Element).id === 'cancelButton') {
      document.getElementById('sendButton').focus();
      event.preventDefault();
    } else if (!event.shiftKey && event.key === 'Tab' && (event.target as Element).id === 'sendButton') {
      document.getElementById('closeButton').focus();
      event.preventDefault();
    } else if (event.shiftKey && event.key === 'Tab' && (event.target as Element).id === 'sendButton') {
      document.getElementById('cancelButton').focus();
      event.preventDefault();
    } else if ((event.key === 'Tab') && !(((event.target as Element).id === 'sendButton')
      || ((event.target as Element).id === 'cancelButton')
      || ((event.target as Element).id === 'closeButton'))) {
      event.preventDefault();
    }
  }

}