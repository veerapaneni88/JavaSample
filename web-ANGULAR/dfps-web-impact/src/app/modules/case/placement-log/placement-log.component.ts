import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { PlacementLogRes } from '@case/model/placement';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormService,
  FormValue,
  Reports,
  NavigationService,
  ReportService
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { CaseService } from './../service/case.service';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'placement-log',
  templateUrl: './placement-log.component.html'
})
export class PlacementLogComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  placementLogForm: FormGroup;
  currentPageNumber = 0;
  tableBody: any[];
  tableColumn: any[];
  placementLogDataTable: DataTable;
  placementLogResponse: PlacementLogRes;
  reportSubscription: Subscription;
  formsSubscription: Subscription;
  reports: any;
  displayHomeInfoHeader: boolean;
  formValues: FormValue[];
  showForm: boolean;
  childId: string;

  constructor(
    private reportService: ReportService,
    private formService: FormService,
    private modalService: BsModalService,
    private formBuilder: FormBuilder,
    private caseService: CaseService,
    private router: Router,
    private navigationService: NavigationService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Placement Log');
    this.helpService.loadHelp('Case');
    this.placementLogForm = this.formBuilder.group({
    });
    this.caseService.gePlacementLog().subscribe(res => {
      this.placementLogResponse = res;
      this.loadDataTable();
      this.formValues = this.getForms();
      if (this.placementLogResponse.stageCode === 'KIN') {
        this.showForm = true;
      }
    });
    this.displayHomeInfoHeader = this.router.url.includes('home-information');

    this.reportSubscription = this.reportService.generateReportEvent.subscribe(data => {
      this.generateReport(data);
    });

    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });

    this.reports = this.getReports().map(report => {
      return { ...report, reportParams: JSON.stringify(report.reportParams) };
    });

  }

  loadDataTable() {
    this.tableColumn = [
      { field: 'name', header: 'Name', sortable: true, width: 170 },
      { field: 'personId', header: 'Person Id', sortable: false, width: 100 },
      { field: 'dob', header: 'DOB', width: 100 },
      { field: 'placementDate', header: 'Placement Date', sortable: true, width: 150, isDate: true },
      { field: 'endDate', header: 'End Date', sortable: true, width: 100, isDate: true },
      { field: 'removalReason', header: 'Removal Reason', sortable: true, width: 200 },
      { field: 'removalReasonSubType', header: 'Removal Reason Subtype', sortable: true, width: 200 },
      { field: 'livingArrangement', header: 'Living Arrangement', sortable: false, width: 200 },
      { field: 'adoptionConsummated', header: 'Ado Const', sortable: false, width: 75, isTick: true }
    ];


    if (this.placementLogResponse.stageCode === 'KIN') {
      this.placementLogDataTable = {
        tableBody: this.placementLogResponse.placementLogs,
        tableColumn: this.tableColumn,
        isSingleSelect: true,
        isPaginator: true,
      };
    }
    else {
      this.placementLogDataTable = {
        tableBody: this.placementLogResponse.placementLogs,
        tableColumn: this.tableColumn,
        isSingleSelect: false,
        isPaginator: true,
      };
    }
  }

  getSelectedPlacement(selectedRow) {
    if (selectedRow) {
      this.childId = selectedRow.personId;
    }
  }


  getReports(): Reports[] {
    return [
      {
        reportName: 'Facility Placement Report',
        reportParams: {
          reportName: 'cfa04o00',
          emailMessage: '',
          paramList: '',
        },
      },
    ];
  }

  generateReport(reportData: any) {
    if (reportData) {
      reportData = JSON.parse(reportData);
      const emailMessage = 'Facility Placement Report for: ' + 
      (this.placementLogResponse.homeName ? this.placementLogResponse.homeName : this.placementLogResponse.resourceName);
      reportData.emailMessage = emailMessage;
      const paramList = this.placementLogResponse.resourceId + '^' + '0';
      reportData.paramList = paramList;
    }
    this.reportService.callReportService(reportData);
  }

  launchForm(data: any) {
    if (this.childId && this.childId !== '') {
      if (data) {
        data = JSON.parse(data)
        data.idPerson = this.childId
      }
      this.formService.launchForm(JSON.stringify(data));
    }
    else {
      const initialState = {
        title: 'Placement Log',
        message:
          'Please select a Name to launch Form-0697',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        document.getElementById('formLaunch').focus();
      });

    }
  }

  getForms(): FormValue[] {
    return [{
      formName: 'Form-0697 Application for Reimbursement',
      formParams: {
        docType: 'kin04o00',
        docExists: 'false',
        protectDocument: 'false',
        checkForNewMode: 'false',
        idCase: this.placementLogResponse.caseId ?
          String(this.placementLogResponse.caseId) : '',
        idResource: this.placementLogResponse.resourceId ?
          String(this.placementLogResponse.resourceId) : '',
        idStage: this.placementLogResponse.stageId ?
          String(this.placementLogResponse.stageId) : '',
        idPerson: this.childId ?
          String(this.childId) : ''
      }
    }
    ];
  }

  ngOnDestroy() {
    this.reportSubscription.unsubscribe();
  }

}
