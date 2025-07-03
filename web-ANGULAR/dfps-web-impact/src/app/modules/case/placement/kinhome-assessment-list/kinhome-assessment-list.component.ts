import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EleKinAssessmentListRes } from '@case/model/kinHomeAssessment';
import { CaseService } from '@case/service/case.service';
import { DataTable, NavigationService, GlobalMessageServcie, ReportService, Reports, DfpsConfirmComponent } from 'dfps-web-lib';
import { HelpService } from 'app/common/impact-help.service';
import { Subscription } from 'rxjs';
import { BsModalService } from 'ngx-bootstrap/modal';

@Component({
  selector: 'kinhome-assessment-list',
  templateUrl: './kinhome-assessment-list.component.html'
})
export class KinHomeAssessmentListComponent implements OnInit {

  eleKinAssessmentListRes: EleKinAssessmentListRes;
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
    this.navigationService.setTitle('Kinship Home Assessment List');
    this.helpService.loadHelp('Placement');
    this.caseService.getEleKinHomeAssessmentList().subscribe(
      response => {
        this.eleKinAssessmentListRes = response;
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
        width: 170,
        sortable: true,
        isLink: true,
        uniqueLinkKey: 'eventId',
        url: ':eventId',
        urlParams: ['eventId']
      },
      { field: 'eventDescription', header: 'Description', sortable: true, width: 375 },
      { field: 'stageCode', header: 'Stage', sortable: true, width: 120 },
      { field: 'stageName', header: 'Stage Name', sortable: true, width: 150 },
      { field: 'caseId', header: 'Case ID', sortable: true, width: 150 },
      { field: 'personName', header: 'Person', sortable: true, width: 150 },
      { field: 'createdBy', header: 'Entered By', sortable: true, width: 150 },
      { field: 'eventId', header: 'Event ID', sortable: true, width: 150 },];

    this.dataTable = {
      tableBody: this.eleKinAssessmentListRes.kinAssessments,
      tableColumn: this.tableColumn,
      isPaginator: true,
      isSingleSelect: false
    };
  }

  add() {
    const initialState = {
      title: 'KIN Home Assessment',
      message: 'Before proceeding, please verify this is not a duplicate home assessment entry',
      showCancel: false
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { this.route.navigate(['case/placement/kinhome-assessments/0']); });


  }

  getReports(): Reports[] {
    return [
      {
        reportName: 'Kinship Home Assessment Report',
        reportParams: {
          reportName: 'ccm02o02',
          emailMessage: 'Kinship Home Assessment Report: ',
          paramList: '3'
        }
      }
    ];
  }

  generateReport(reportData: any) {
    if (!reportData) {
      const initialState = {
        title: 'Kinship Home Assessment Report',
        message: 'Select a report from the list before attempting to launch a report.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });

    } else if (this.eleKinAssessmentListRes && this.eleKinAssessmentListRes.kinAssessments && !this.eleKinAssessmentListRes.kinAssessments.length) {
      const initialState = {
        title: 'Kinship Home Assessment Report',
        message: 'Kin Home Assessment Events should exist before attempting to launch the report.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    } else {
      if (reportData) {
        reportData = JSON.parse(reportData);
        reportData.emailMessage = reportData.emailMessage + this.eleKinAssessmentListRes.stageName;
        reportData.paramList = this.eleKinAssessmentListRes.caseId+ '^' + 'KAM' + '^' + this.eleKinAssessmentListRes.stageId;
      }
      this.reportService.callReportService(reportData);
    }
  }

  ngOnDestroy() {
    this.reportSubscription.unsubscribe();
  }

}
