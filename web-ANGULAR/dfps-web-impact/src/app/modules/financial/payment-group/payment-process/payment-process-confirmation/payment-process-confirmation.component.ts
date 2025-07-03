import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { DataTable } from 'dfps-web-lib';

@Component({
  selector: 'payment-process-confirmation',
  templateUrl: './payment-process-confirmation.component.html',
  styleUrls: ['./payment-process-confirmation.component.css']
})
export class PaymentProcessConfirmationComponent implements OnInit {
  @Output() isClose = new EventEmitter();
  @Input() message;
  @Input() selectedValues;
  @Input() totalAmount;
  @Input() title;
  @Input() isVoidCheck = false;
  tableColumn: any[];
  processSelectedACHDataTable: DataTable;
  voidSelectedChecksDataTable: DataTable;

  constructor(private modalRef: BsModalRef) { }

  ngOnInit(): void {
    this.loadProcessSelectedACH();
    this.loadVoidSelectedChecks();
  }

  loadProcessSelectedACH() {
    this.tableColumn = [
      { field: 'paymentGroupId', header: 'Payment Group Id', sortable: false, width: 100 },
      { field: 'payeeName', header: 'Payee Name', sortable: false, width: 100 },
    ];
    this.processSelectedACHDataTable = {
      tableBody: this.selectedValues,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false
    };
  }

  loadVoidSelectedChecks() {
    this.tableColumn = [
      { field: 'paymentGroupId', header: 'Payment Group Id', sortable: false, width: 100 },
      { field: 'payeeName', header: 'Payee Name', sortable: false, width: 100 },
      { field: 'amount', header: 'Total Amount', sortable: false, width: 100 }
    ];
    this.voidSelectedChecksDataTable = {
      tableBody: this.selectedValues,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false
    };
  }

  closeModal() {
    this.isClose.emit(false);
    this.modalRef.hide();
  }

  confirm() {
    this.isClose.emit(true);
    this.modalRef.hide();
  }
}
