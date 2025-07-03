import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ServicePackageListRes } from '@case/model/servicePackage';
import { CaseService } from '@case/service/case.service';
import { DataTable, NavigationService, GlobalMessageServcie, ReportService, Reports, DfpsConfirmComponent } from 'dfps-web-lib';
import { HelpService } from 'app/common/impact-help.service';
import { Subscription } from 'rxjs';
import { BsModalService } from 'ngx-bootstrap/modal';

@Component({
  selector: 'service-package-list',
  templateUrl: './service-package-list.component.html'
})
export class ServicePackageListComponent implements OnInit {

  servicePackageListRes: ServicePackageListRes;
  dataTable: DataTable;
  tableBody: any[];
  tableColumn: any[];
  reports: any;
  reportSubscription: Subscription;

  constructor(private caseService: CaseService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private globalMessageService: GlobalMessageServcie,
    private reportService: ReportService,
    private modalService: BsModalService,
    private route: Router) { }

  ngOnInit(): void {
    this.globalMessageService.clearPaths();
    this.navigationService.setTitle('Service Package List');
    this.helpService.loadHelp('Placement');
    this.caseService.getServicePackageList().subscribe(
      response => {
        this.servicePackageListRes = response;
        this.intializeDataTable();
        this.reportSubscription = this.reportService.generateReportEvent.subscribe(data => {
          this.generateReport(data);
        });
        this.reports = this.getReports().map(report => {
          return { ...report, reportParams: JSON.stringify(report.reportParams) };
        });
      });
  }

  intializeDataTable() {
    this.tableColumn = [
      { field: 'createdDate', header: 'Date Entered', sortable: true, width: 150 },
      { field: 'eventStatus', header: 'Status', sortable: true, width: 120 },
      {
        field: 'eventType',
        header: 'Type',
        desc: null,
        width: 130,
        sortable: true,
        isLink: true,
        uniqueLinkKey: 'id',
        url: '/case/placement/service-packages/:id',
        urlParams: ['id']
      },
      { field: 'eventDescription', header: 'Description', sortable: true, width: 330 },
      { field: 'stageCode', header: 'Stage', sortable: true, width: 120 },
      { field: 'stageName', header: 'Stage Name', sortable: true, width: 150 },
      { field: 'caseId', header: 'Case ID', sortable: true, width: 150 },
      { field: 'personName', header: 'Person', sortable: true, width: 150 },
      { field: 'createdBy', header: 'Entered By', sortable: true, width: 150 },
      { field: 'eventId', header: 'Event ID', sortable: true, width: 150 },];

    this.dataTable = {
      tableBody: this.servicePackageListRes.servicePackages,
      tableColumn: this.tableColumn,
      isPaginator: true,
      isSingleSelect: false
    };
  }

  add() {
    this.route.navigate(['case/placement/service-packages/0']);
  }

  getReports(): Reports[] {
    return [
      {
        reportName: 'Service Package Confirmation',
        reportParams: {
          reportName: 'svcpkg00',
          emailMessage: 'Service Package Confirmation: ',
          paramList: '2'
        }
      }
    ];
  }

  generateReport(reportData: any) {
    if (!reportData) {
      const initialState = {
        title: 'Service Package Confirmation Report',
        message: 'Select a report from the list before attempting to launch a report.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });

    } else if (this.servicePackageListRes && this.servicePackageListRes.servicePackages && !this.servicePackageListRes.servicePackages.length) {
      const initialState = {
        title: 'Service Package Confirmation Report',
        message: 'Service Package should exist before attempting to launch the report.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    } else {
      if (reportData) {
        reportData = JSON.parse(reportData);
        reportData.emailMessage = reportData.emailMessage + this.servicePackageListRes.stageName;
        reportData.paramList = this.servicePackageListRes.stageId ;
      }
      this.reportService.callReportService(reportData);
    }
  }

  ngOnDestroy() {
    this.reportSubscription.unsubscribe();
  }

}
