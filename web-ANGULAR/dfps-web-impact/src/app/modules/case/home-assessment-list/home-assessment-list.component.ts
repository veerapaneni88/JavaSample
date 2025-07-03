import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { DataTable, Reports, ReportService, NavigationService} from 'dfps-web-lib';
import { CaseService } from '../service/case.service';
import { Router } from '@angular/router';
import { DisplayHomeAssessmentList } from '../model/homeAssessmentList';
import { Subscription } from 'rxjs';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'home-assessment-list',
  templateUrl: './home-assessment-list.component.html'
})
export class HomeAssessmentListComponent implements OnInit, OnDestroy {
  assessmentAddendumListForm: FormGroup;
  homeAssessmentListRes: DisplayHomeAssessmentList;
  tableBody: any[];
  tableColumn: any[];
  homeAssessmentListDataTable: DataTable;
  hideAddButton = false;
  reports: any;
  reportSubscription: Subscription;

  constructor(
    private navigationService: NavigationService,
    private helpService: HelpService,
    private formBuilder: FormBuilder,
    private caseService: CaseService,
    private router: Router,
    private reportService: ReportService
  ) { }

  ngOnInit(): void {        
    this.navigationService.setTitle('Home Assessment Addendum List');
    this.helpService.loadHelp('Case');
    this.assessmentAddendumListForm = this.formBuilder.group({
    });
    this.initializeScreen();
  }

  initializeScreen() {
    this.caseService.getKinHomeAssessmentList().subscribe(response => {
      this.homeAssessmentListRes = response;
      this.loadHomeAssessmentListDataTable();

      for (const homeAssessment of this.homeAssessmentListRes.homeAssessmentsList) {
        if (!['APRV'].includes(homeAssessment.status)) {
        this.hideAddButton = true;
          break;
        }
      }

      if (this.homeAssessmentListRes.homeAssessmentsList && this.homeAssessmentListRes.homeAssessmentsList.length === 0) {
        this.hideAddButton = false;
      }

      if (this.homeAssessmentListRes.pageMode === 'VIEW') {
        this.hideAddButton = true;
      }

      this.reportSubscription = this.reportService.generateReportEvent.subscribe(data => {
        this.generateReport(data);
      });
      this.reports = this.getReports().map(report => {
        return { ...report, reportParams: JSON.stringify(report.reportParams) };
      });
    });
  }

  loadHomeAssessmentListDataTable() {
    this.tableColumn = [
      { field: 'createdDate', header: 'Date Entered', sortable: false, width: 100 },
      { field: 'status', header: 'Status', sortable: false, width: 75 },
      {
        field: 'typeDecode',
        header: 'Type',
        width: 200,
        sortable: false,
        isLink: true,
        url: '/case/home-assessment-addendum-list/:id',
        urlParams: ['id'],
      },
      { field: 'description', header: 'Description', sortable: false, width: 200 },
      { field: 'stage', header: 'Stage', sortable: false, width: 75 },
      { field: 'stageName', header: 'Stage Name', sortable: false, width: 150 },
      { field: 'caseId', header: 'Case ID', sortable: false, width: 110 },
      { field: 'fullName', header: 'Entered By', sortable: false, width: 150 },
      { field: 'id', header: 'Event ID', sortable: false, width: 110 },
    ];

    this.homeAssessmentListDataTable = {
      tableBody: this.homeAssessmentListRes.homeAssessmentsList,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false,
    };

    if (this.homeAssessmentListDataTable.tableBody && this.homeAssessmentListDataTable.tableBody.length) {
      this.homeAssessmentListDataTable.tableBody.forEach(data => {
        data.stage = 'KIN';
        data.stageName = this.homeAssessmentListRes.stageName;
        data.caseId = this.homeAssessmentListRes.caseId;
      })
    }
  }

  add() {
    const headerUrl = 'case/home-assessment-addendum-list/add';
    this.router.navigate([headerUrl]);
  }

  getReports(): Reports[] {
    return [
      {
        reportName: 'Case Event List',
        reportParams: {
          reportName: 'ccm02o00',
          emailMessage: 'Case Name: ',
          paramList: ''
        }
      }
    ];
  }

  generateReport(reportData: any) {
    if (reportData) {
      reportData = JSON.parse(reportData);
      reportData.emailMessage = reportData.emailMessage + this.homeAssessmentListRes.caseName;
      reportData.paramList = this.homeAssessmentListRes.caseId;
    }
    this.reportService.callReportService(reportData);
  }

  ngOnDestroy() {
    this.reportSubscription.unsubscribe();
  }

}
