import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';

@Component({
  selector: 'validate-contract-number',
  templateUrl: './validate-contract-number.component.html',
  styleUrls: ['./validate-contract-number.component.css']
})
export class ValidateContractNumberComponent implements OnInit {

  constructor(private modalRef: BsModalRef) { }

  @Input() contractNumber: string;
  @Input() contractManager: string;
  @Input() legalEntityName: string;
  @Input() tin: string;
  @Input() address: string;
  @Input() procurement: string;
  @Input() contractEndDate: string;
  @Input() contractValidated: boolean;

  @Output() validationEvent: EventEmitter<any> = new EventEmitter();

  validationAcceptance(val: boolean) {
    this.closeModal();
    this.validationEvent.emit(val);
  }

  ngOnInit() { }

  closeModal() {
    this.modalRef.hide();
    document.getElementById('validateResource').focus();
  }

  preventTabbing(event: KeyboardEvent) {
    if (this.contractValidated) {
      if ((event.shiftKey && event.key === 'Tab') && (event.target as Element).id === 'cancelButton') {
        document.getElementById('noButton').focus();
        event.preventDefault();
      } else if (!event.shiftKey && event.key === 'Tab' && (event.target as Element).id === 'cancelButton') {
        document.getElementById('yesButton').focus();
        event.preventDefault();
      } else if (!event.shiftKey && event.key === 'Tab' && (event.target as Element).id === 'noButton') {
        document.getElementById('cancelButton').focus();
        event.preventDefault();
      } else if (event.shiftKey && event.key === 'Tab' && (event.target as Element).id === 'noButton') {
        document.getElementById('yesButton').focus();
        event.preventDefault();
      } else if ((event.key === 'Tab') && !(((event.target as Element).id === 'yesButton')
        || ((event.target as Element).id === 'noButton')
        || ((event.target as Element).id === 'cancelButton'))) {
        // document.getElementById('yesButton').focus();
        event.preventDefault();
      }
    } else {
      if ((event.key === 'Tab') && (event.target as Element).id === 'closeButton') {
        document.getElementById('cancelButton').focus();
        event.preventDefault();
      } else if (event.key === 'Tab' && (event.target as Element).id === 'cancelButton') {
        document.getElementById('closeButton').focus();
        event.preventDefault();
      } else if ((event.key === 'Tab') && !(((event.target as Element).id === 'closeButton') ||
        ((event.target as Element).id === 'cancelButton'))) {
        document.getElementById('cancelButton').focus();
        event.preventDefault();
      }
    }
  }


}
