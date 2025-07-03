import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  NavigationService
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { FacilityType, SafetyPlanListRes } from '../model/SafetyPlan';
import { CaseService } from '../service/case.service';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'safety-plan-list',
  templateUrl: './safety-plan-list.component.html'
})
export class SafetyPlanListComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  safetyPlansResponse: SafetyPlanListRes;
  safetyPlansTable: DataTable;
  tableColumn: any[];
  selectSafetyPlans: any[]
  modalSubscription: Subscription;
  facType: FacilityType[];

  @ViewChild('errors') errorElement: ElementRef;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: BsModalService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private caseService: CaseService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Safety Plan List');
    this.helpService.loadHelp('Case');

    this.getSafetyPlans();
  }

  getSafetyPlans() {
    this.caseService.displaySafetyPlans().subscribe(
      response => {
        this.safetyPlansResponse = response;
        this.loadSafetyPlanDataTable();
      });
  }

  loadSafetyPlanDataTable() {
    this.tableColumn = [
      { field: 'createdDate', header: 'Date Entered', width: 120 },
      {
        field: 'id',
        header: 'Safety Plan Id',
        isLink: true,
        url: '/case/case-management/maintenance/safety-plans/:id',
        urlParams: ['id'],
        width: 100
      },
      { field: 'safetyPlanStatus', header: 'Status', width: 100 },
      { field: 'operationName', header: 'Operation Name', width: 150 },
      { field: 'operationNumber', header: 'Operation Number', width: 150 },
      { field: 'impactFacilityType', header: 'IMPACT Facility Type', width: 200 },
      { field: 'effectiveDate', header: 'Effective Date', width: 120 },
      { field: 'createdBy', header: 'Entered By', width: 100 },
      { field: 'lastUpdatedDate', header: 'Last Modified Date', width: 150 },
      { field: 'lastModifiedBy', header: 'Last Modified By', width: 150 }
    ];

    this.safetyPlansTable = {
      tableColumn: this.tableColumn,
      isPaginator: false,
      isMultiSelect: true,
      displaySelectAll: false,
    };

    this.safetyPlansTable.tableBody = this.safetyPlansResponse.safetyPlans;
    if (this.safetyPlansTable.tableBody && this.safetyPlansTable.tableBody.length) {
      
      this.safetyPlansTable.tableBody.forEach(tableData => {
        tableData.safetyPlanStatus = this.findStatusDecode(tableData.safetyPlanStatus);
      })
      this.safetyPlansTable.tableBody.forEach(tableData => {
        tableData.impactFacilityType = this.findImpactFacilityDecode(tableData.impactFacilityType, tableData.impactFacilityCode);
      })
    }

    if (this.safetyPlansResponse) {
      this.safetyPlansTable.tableBody = this.safetyPlansResponse.safetyPlans;
    }
  }

  findStatusDecode(status: string){
    return status ? this.safetyPlansResponse.safetyPlanStatuses.find(statusCode => statusCode.code === status).decode : '';
  }

  findImpactFacilityDecode(impactFacilityType: string, impactFacilityCode: string){
    return impactFacilityCode ? 
    this.safetyPlansResponse.facilityTypes.find(facilityCode => facilityCode.code === impactFacilityCode).decode : '';
  }

  delete() {
    const selectSafetyPlanIds: any[] = [];
    let selectedNonInprocessSafetyPlan = false;

    this.safetyPlansTable?.selectedRows?.forEach(safetyPlan => {
      if(safetyPlan?.safetyPlanStatus !== 'In Process') {
        selectedNonInprocessSafetyPlan = true;
      }
      if(!selectedNonInprocessSafetyPlan){
        selectSafetyPlanIds.push(safetyPlan.id);
      }
    });

    if (selectedNonInprocessSafetyPlan){      
      const initialState = {
        message: 'The Safety Plan(s) with Status \'In Effect\' and \'Closed\' cannot be deleted. Please select the Safety Plan(s) with Status \'In Process\' to continue.',
        title: 'Safety Plans',
        showCancel: false,
      };

      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        });

    } else {
      let initialState = {
        message: 'Are you sure you want to delete these Safety Plans?',
        title: 'Safety Plans',
        showCancel: true,
      };
      if (selectSafetyPlanIds.length > 0) {
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
          if (result) {
            this.caseService.deleteSafetyPlans(selectSafetyPlanIds).subscribe(() => {
              this.getSafetyPlans();
            });
          }
        });
      } else {
        initialState = {
          message: 'Please select at least one row to perform this action.',
          title: 'Safety Plan Details',
          showCancel: false,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
        this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe();
      }
    }
  }

  selectedSafetyPlans(event) {
    this.selectSafetyPlans = event;
  }

  add() {
    this.router.navigate(['case/case-management/maintenance/safety-plans/0']);
  }

}
