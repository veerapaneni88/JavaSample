import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {
  DataTable,
  DfpsConfirmComponent,
  NavigationService
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { InjuryListRes } from '../model/InjuryDetail';
import { CaseService } from '../service/case.service';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'injury-list',
  templateUrl: './injury-list.component.html'
})
export class InjuryListComponent implements OnInit {
  injuryDataTable: DataTable;
  tableColumn: any[];
  injuryListRes: InjuryListRes;
  selectInjuryDetails: any[]
  modalSubscription: Subscription;

  constructor(
    private caseService: CaseService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private modalService: BsModalService,
    private router: Router) {
  }
  
  ngOnInit(): void {
    this.navigationService.setTitle('Injury List');
    this.helpService.loadHelp('Case');

    this.displayInjuryDetails();
  }

  displayInjuryDetails() {
    return this.caseService.displayInjuryDetails()
      .subscribe(res => {
        this.injuryListRes = res;
        this.intializeDataTable();
      });
  }

  intializeDataTable() {
    this.tableColumn = [
      { field: 'createdDate', header: 'Date Entered', width: 100 },
      {
        field: 'id',
        header: 'Injury ID',
        isLink: true,
        url: '/case/allegation/injury-details/:id',
        urlParams: ['injuryId', 'id'],
        width: 65
      },
      { field: 'injuryDate', header: 'Injury Date', width: 100 },
      { field: 'victim', header: 'Alleged Victim', width: 150 },
      { field: 'abuseNeglectInjury', header: 'Injury Related to Abuse/Neglect', width: 150 },
      { field: 'allegedPerpetrator', header: 'Alleged Perpetrator', width: 150 },
      { field: 'createdBy', header: 'Entered By', width: 150 },
      { field: 'lastUpdatedDate', header: 'Last Modified Date', width: 150 },
      { field: 'lastModifiedBy', header: 'Last Modified By', width: 150 }
    ];
    this.injuryDataTable = {
      tableBody: this.injuryListRes.injuryDetails,
      tableColumn: this.tableColumn,
      isPaginator: true,
      isMultiSelect: this.injuryListRes.editMode,
      displaySelectAll: false,
    };
  }

  selectedInjuryDetais(event) {
    this.selectInjuryDetails = event;
  }

  add() {
    this.router.navigate(['case/allegation/injury-details/0']);
  }

  delete() {
    const selectInjuryIds: any[] = [];
    this.injuryDataTable?.selectedRows?.forEach(injury => selectInjuryIds.push(injury.id));
    let initialState = {
      message: 'Are you sure you want to delete this information?',
      title: 'Injury List',
      showCancel: true,
    };
    if (selectInjuryIds.length > 0) {
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result) {
          this.caseService.deleteInjuryDetails(selectInjuryIds).subscribe(res =>{
            this.displayInjuryDetails();
          });
        }
        document.getElementById('deleteButtonId').focus();
      });
    } else {
      initialState = {
        message: 'Injury List: Please select an Injury to Continue.',
        title: 'Injury List',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }

}