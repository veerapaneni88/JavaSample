import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { DfpsFormValidationDirective, DirtyCheck } from 'dfps-web-lib';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Dropdown } from 'primeng/dropdown';
import { saveAs } from 'file-saver';
import { RejectInformation } from '../../model/NeiceTransmittalSummary';
import { NeiceTransmittalSummaryValidators } from '../neice-transmittal-summary.validator';

@Component({
  selector: 'app-neice-reject-info',
  templateUrl: './neice-reject-info.component.html',
  styleUrls: ['./neice-reject-info.component.css']
})
export class NeiceRejectInfoComponent extends DfpsFormValidationDirective implements OnInit {

  neiceRejectInfoForm: FormGroup;
  rejectInformationRequest: RejectInformation;

  constructor(private modalRef: BsModalRef, 
    private formBuilder: FormBuilder, 
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  @Input() documentTypes: Dropdown[];
  @Input() comment: string;

  @Output() rejectInformationEvent: EventEmitter<any> = new EventEmitter<any>();

  files  = [];
  fileSizeError: boolean;

  @ViewChild("fileUpload", {static: false}) fileUpload: ElementRef;

  sendRequest() {
    if (this.validateFormGroup(this.neiceRejectInfoForm) && this.neiceRejectInfoForm.value) {
      this.closeModal();
      this.rejectInformationRequest = this.neiceRejectInfoForm.getRawValue();
      if (this.files.length) {
        this.rejectInformationRequest.file = this.files[0].data;
      }    
      this.rejectInformationEvent.emit(this.rejectInformationRequest);
    }
  }

  ngOnInit() {
    this.createForm();
  }

  createForm() {
    this.neiceRejectInfoForm = this.formBuilder.group(
      {
        comments: [this.comment, [Validators.required]],
        documentType: [''],
        description: [''],
        file: ['']
      }, {
      validators: [NeiceTransmittalSummaryValidators.validateRejectInfo]
    }
    );
  }

  closeModal() {
    this.modalRef.hide();
    document.getElementById('btnRequestAdditionalInfo').focus();
  }

  onUploadClick() {
    const fileUpload = this.fileUpload.nativeElement; 
    fileUpload.onchange = () => {
      if (fileUpload.files.length) {
        this.files = [];
      }
      for (let index = 0; index < fileUpload.files.length; index++) {
        const file = fileUpload.files[index];
        console.log((file.size / 1048576).toFixed(1) + 'MB');
        if (file.size / 1048576 > 10) {
          this.fileSizeError = true;
        } else {
          this.fileSizeError = false;
          this.files.push({ data: file, inProgress: false, progress: 0 });
        }
      }
    };
    fileUpload.click();
  }

  /*downloadFile(file) {
    const blob = new Blob([file.data], { type: 'pdf' });
    alert(1);
    //saveAs(blob, file.data.name);
  }*/

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
