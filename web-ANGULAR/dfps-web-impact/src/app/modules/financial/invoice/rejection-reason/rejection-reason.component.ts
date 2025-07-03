import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DataTable, NavigationService } from 'dfps-web-lib';
import { InvoiceService } from '../service/invoice.service';

@Component({
  selector: 'rejection-reason',
  templateUrl: './rejection-reason.component.html',
  styleUrls: []
})
export class RejectionReasonComponent implements OnInit {

  rejectionReasons: any;
  rejectionReasonDataTable: DataTable;
  tableColumn: any[];

  constructor(private invoiceService: InvoiceService,
    private navigationService: NavigationService,
    private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Rejection Reason');
    const invoiceId = this.route.snapshot.paramMap.get('invoiceId');
    const rejectItemId = this.route.snapshot.paramMap.get('rejectItemId');

    this.invoiceService.getRejectionReason(invoiceId, rejectItemId).subscribe(response => {
      this.rejectionReasons = response;
      this.vendorInfoDataTable();
    })
  }

  vendorInfoDataTable() {
    this.tableColumn = [
      { field: 'rejectionReasonCode', header: 'RR', isHidden: false },
      { field: 'rejectionReasonDecode', header: 'Rejection Reason', isHidden: false },
    ];
    this.rejectionReasonDataTable = {
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false
    };
    this.rejectionReasonDataTable.tableBody = [];

    if (this.rejectionReasons) {
      this.rejectionReasonDataTable.tableBody = this.rejectionReasons;
    }
  }

}
