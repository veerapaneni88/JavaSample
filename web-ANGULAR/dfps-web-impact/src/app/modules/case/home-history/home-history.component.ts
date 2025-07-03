import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
  DfpsFormValidationDirective,
  DirtyCheck,
  NavigationService,
  DataTable,
  DfpsConfirmComponent,
  Reports,
  ReportService
} from 'dfps-web-lib';
import { ResourceHistoryRes } from '@case/model/resourcehistory';
import { CaseService } from './../service/case.service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Route } from '@angular/compiler/src/core';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'home-history',
  templateUrl: './home-history.component.html'
})
export class HomeHistoryComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  resourceHistoryForm: FormGroup;
  currentPageNumber = 0;
  tableBody: any[];
  tableColumn: any[];
  resourceHistoryDataTable: DataTable;
  resourceHistoryResponse: ResourceHistoryRes;
  selectedResourceHistory: any;
  reports: any;
  reportSubscription: Subscription;
  resourceId: string;
  isFadHomeHistory: boolean;
  showForm : boolean;

  constructor(
    private navigationService: NavigationService,
    private caseService: CaseService,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private reportService: ReportService,
    private router: Router,
    private route: ActivatedRoute,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    if(this.router.url.includes('FAD')) {
      this.navigationService.setTitle('Home History FAD');
    } else if(this.router.url.includes('KIN')) {
      this.navigationService.setTitle('Home History KIN');
    }
    this.helpService.loadHelp('Case');
    this.resourceHistoryForm = this.formBuilder.group({
    });
    this.initializeScreen();
    this.resourceId = this.route.snapshot.paramMap.get('resourceId');
    if (localStorage.getItem('backFrom') === 'ResourceHistoryDetail') {
      localStorage.removeItem('backFrom');
    } else {
      localStorage.removeItem('resourceHistoryLink');
    }
    
  }

  initializeScreen() {
    this.caseService.getResourceHistory(this.resourceId).subscribe(res => {
      this.resourceHistoryResponse = res;
      this.loadDataTable();  
     
      if(this.resourceHistoryResponse.stageCode === 'FAD'){
        this.showForm = true;
        }   
      this.reportSubscription = this.reportService.generateReportEvent.subscribe(data => {
        this.generateReport(data);
      });
      this.reports = this.getReports().map(report => {
        return { ...report, reportParams: JSON.stringify(report.reportParams) };
      });
    });
   
  }

  loadFadDataTable() {
    this.tableColumn = [
      {
        field: 'effectiveDate',
        header: 'Effective Date',
        desc: null,
        width: 120,
        handleClick: this.isEditMode(),
        uniqueLinkKey: 'resourceHistoryId',
        url: '/case/home-information/home-history/FAD/:resourceId/:resourceHistoryId',
        urlParams: ['resourceId','resourceHistoryId']
      },
      { field: 'endDate', header: 'End Date', sortable: false, width: 100 },
      { field: 'category', header: 'Category', sortable: false, width: 150 },
      { field: 'faHomeStatus', header: 'Status', sortable: false, width: 175 },
      { field: 'homeType', header: 'Foster Type', sortable: false, width: 300 },
      { field: 'facilityCapacity', header: 'Capacity', sortable: false, width: 75 },
      { field: 'minAgeMaleChild', header: 'M Min Year', sortable: false, width: 110 },
      { field: 'maxAgeMaleChild', header: 'M Max Year', sortable: false, width: 110 },
      { field: 'minAgeFemaleChild', header: 'F Min Year', sortable: false, width: 110 },
      { field: 'maxAgeFemaleChild', header: 'F Max Year', sortable: false, width: 110 },
      { field: 'closureReason', header: 'Closure Reason', sortable: false, width: 175 },
      { field: 'recommendReopen', header: 'Recommend Reopening', sortable: false, width: 250 },
      { field: 'involuntaryClosure', header: 'Vol/Invol Closure', sortable: false, width: 150 },
      { field: 'ethnicity', header: 'Race and Ethnicity', sortable: false, width: 200 },
      { field: 'language', header: 'Language', sortable: false, width: 100 },
      { field: 'religion', header: 'Religion', sortable: false, width: 125 },
      { field: 'annualIncome', header: 'Annual Income', sortable: false, width: 150, isCurrency: true },
      { field: 'maritalStatus', header: 'Marital Status', sortable: false, width: 200 },
      { field: 'marriageDate', header: 'Marriage Date', sortable: false, width: 150 },
      { field: 'sourceInquiry', header: 'Inquiry Source', sortable: false, width: 200 },
      { field: 'respite', header: 'Respite', sortable: false, width: 125 },
      { field: 'nonPRSHome', header: 'Non-FPS Home', sortable: false, width: 150 },
      { field: 'nonPRSPCA', header: 'Non-FPS Home PCA', sortable: false, width: 175 },
      { field: 'certifyEntity', header: 'Certify Entity', sortable: false, width: 175 },
      { field: 'individualStudy', header: 'Ind Study', sortable: false, width: 100 },
      { field: 'careProvided', header: 'Inhome Care', sortable: false, width: 125 },
      { field: 'resourceId', isHidden: true }
    ];

    this.resourceHistoryDataTable = {
      tableBody: this.resourceHistoryResponse.resourceHistory,
      tableColumn: this.tableColumn,
      isSingleSelect: this.isEditMode(),
      isPaginator: (this.resourceHistoryResponse && this.resourceHistoryResponse.resourceHistory &&
        this.resourceHistoryResponse.resourceHistory.length) > 10 ? true : false
    };

    const previousRecord = JSON.parse(localStorage.getItem('resourceHistoryLink'));
    if (previousRecord) {
      setTimeout(() => {
        const element: any = document.querySelector('#id' + previousRecord.resourceHistoryId);
        if (element) {
          element.focus();
        }
      },
        1000)
    }    
  }

  loadKinDataTable() {
    this.tableColumn = [
      {
        field: 'effectiveDate',
        header: 'Effective Date',
        width: 125
      },
      { field: 'endDate', header: 'End Date', sortable: false, width: 100 },
      { field: 'category', header: 'Category', sortable: false, width: 150},
      { field: 'faHomeStatus', header: 'Status', sortable: false, width: 100 },  
      { field: 'closureReason', header: 'Closure Reason', sortable: false, width: 175 },
      { field: 'recommendReopen', header: 'Recommend Reopening', sortable: false, width: 200 },   
      { field: 'ethnicity', header: 'Race and Ethnicity', sortable: false, width: 300 },
      { field: 'language', header: 'Language', sortable: false, width: 100 },
      { field: 'religion', header: 'Religion', sortable: false, width: 125 },
      { field: 'annualIncome', header: 'Annual Income', sortable: false, width: 150, isCurrency: true },
      { field: 'personCount', header: 'Number in Household', sortable: false, width: 200 },
      { field: 'maritalStatus', header: 'Marital Status', sortable: false, width: 150 },
      { field: 'sourceInquiry', header: 'Inquiry Source', sortable: false, width: 200 }     
    ];
    this.resourceHistoryDataTable = {
      tableBody: this.resourceHistoryResponse.resourceHistory,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: (this.resourceHistoryResponse && this.resourceHistoryResponse.resourceHistory &&
        this.resourceHistoryResponse.resourceHistory.length) > 10 ? true : false
    };    
  }

  getSelectedResourceHistory(event: Event) {
    this.selectedResourceHistory = event;
  }

  setSelectedResourceHistory(): any {
    return this.selectedResourceHistory;
  }

  isEditMode() {
    return this.router.url.includes('FAD') && this.resourceHistoryResponse && this.resourceHistoryResponse.pageMode === 'EDIT';
  }

  loadDataTable() {
    this.isFadHomeHistory = this.router.url.includes('FAD');
    if (this.router.url.includes('FAD')) {
      this.loadFadDataTable();
    } else if (this.router.url.includes('KIN')) {
      this.loadKinDataTable();
    }
  }

  handleRouting(event: any) {
    localStorage.removeItem('resourceHistoryLink');
    const link = (event.link as string).replace('null', '0');
    this.router.navigate([link]);
    let index = null;
    this.resourceHistoryResponse.resourceHistory.forEach((resource, i) => {
      if (resource.resourceHistoryId === event.tableRow.resourceHistoryId) {
        index = i;
      }
    })
    const previousEndDate = this.resourceHistoryResponse.resourceHistory[index + 1] ?
      this.resourceHistoryResponse.resourceHistory[index + 1].endDate : null;
    const nextStartDate = this.resourceHistoryResponse.resourceHistory[index - 1] ?
      this.resourceHistoryResponse.resourceHistory[index - 1].effectiveDate : null;
    localStorage.setItem('resourceHistoryLink', JSON.stringify({
      resourceHistoryId: event.tableRow.resourceHistoryId,
      endDate: event.tableRow.endDate,
      previousEndDate,
      nextStartDate,
      pageMode: this.resourceHistoryResponse.pageMode
    }));
  }

  deleteResourceHistory() {
    if (this.selectedResourceHistory) {
      const initialState = {
        title: 'Home History',
        message: 'Are you sure you want to delete this information?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.caseService
            .deleteResourceHistory(this.resourceId, this.selectedResourceHistory.resourceHistoryId)
            .subscribe((res) => {
              this.initializeScreen();
              document.getElementById('deleteButtonId').focus();
            });
        }
      });
    } else {
      const initialState = {
        title: 'Home History',
        message: 'Please select atleast one row to perform this action.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered',
        initialState,
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
    }
  }

  getReports(): Reports[] {
    return [
      {
        reportName: 'F/A Home History without Audit records ',
        reportParams: {
          reportName: 'cfa18o00',
          emailMessage: 'W/O Audit Recs, Home: ',
          paramList: '0'
        }
      }, {
        reportName: 'F/A Home History with Audit records ',
        reportParams: {
          reportName: 'cfa18o00',
          emailMessage: 'W/ Audit Recs, Home: ',
          paramList: '1'
        }
      }
    ];
  }

  generateReport(reportData: any) {
    if (reportData) {
      reportData = JSON.parse(reportData);
      reportData.emailMessage = reportData.emailMessage + this.resourceHistoryResponse.stageName;
      reportData.paramList = this.resourceHistoryResponse.stageId + '^' + reportData.paramList;
    }
    this.reportService.callReportService(reportData);
  }

  ngOnDestroy() {
    this.reportSubscription.unsubscribe();
  }
}
