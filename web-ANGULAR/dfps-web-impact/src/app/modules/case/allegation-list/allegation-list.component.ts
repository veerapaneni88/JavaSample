import { Allegation } from './../model/Allegation';
import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { AllegationDetailRes } from '../model/Allegation';
import { InjuryDetail } from '../model/InjuryDetail';
import { CaseService } from '../service/case.service';

@Component({
  selector: 'allegation-list',
  templateUrl: './allegation-list.component.html'
})
export class AllegationListComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild('errors') errorElement: ElementRef;
  allegationDetailRes: AllegationDetailRes;

  allegationTable: DataTable;
  tableColumn: any[];
  selectedAllegations: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: BsModalService,
    private caseService: CaseService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnInit(): void {
    this.caseService.getAllegations().subscribe(
      response => {
        this.allegationDetailRes = response;
        this.loadAllegationDataTable();
      });
  }

  loadAllegationDataTable() {
    this.tableColumn = [
      { field: 'type', header: 'Alleg', width: 75 },
      { field: 'victimName', header: 'Alleged Victim', width: 150 },
      { field: 'allegedPerpetratorName', header: 'Alleged Perpetrator', width: 150 },
      { field: 'disposition', header: 'Disp', width: 60 },
      { field: 'severity', header: 'Severity', width: 60 },
      { field: 'stageCode', header: 'Stage', width: 60 },
      { field: 'id', header: 'Allegation ID', width: 100 },
      { field: 'victimId', header: 'VC ID', width: 100 },
      { field: 'allegedPerpetratorId', header: 'AP ID', width: 100 },
      { field: 'createdDate', header: 'Date Entered', width: 150 },
      { field: 'createdBy', header: 'Entered By', width: 200 },
      { field: 'lastUpdatedDate', header: 'Last Modified Date', width: 150 },
      { field: 'lastUpdatedBy', header: 'Last Modified By', width: 200 }
    ];

    this.allegationTable = {
      tableColumn: this.tableColumn,
      isPaginator: false,
      isSingleSelect: true
    };

    if (this.allegationDetailRes) {
      this.allegationTable.tableBody = this.allegationDetailRes?.allegations;
    }
  }

  selectAllegations(selectedRows) {
    localStorage.setItem('preSelectedAllegations', null) 
    this.selectedAllegations = selectedRows? selectedRows  : null;
  }

  setSelectedAllegation() {
    if (localStorage.getItem('preSelectedAllegations') !== 'null') {
      const allegation: any = JSON.parse(localStorage.getItem('preSelectedAllegations')).length
        > 0 ? JSON.parse(localStorage.getItem('preSelectedAllegations'))[0] : null;
      this.selectedAllegations = allegation;
      return allegation;
    }
    return;
  }

  continue() {
    if (!this.selectedAllegations) {
      this.showErrorModal();
    } else {
      const injuryId = this.route.snapshot.paramMap.get('injuryId') ? this.route.snapshot.paramMap.get('injuryId') : '0';
      const allegation: Allegation[] = [];
      allegation.push(this.selectedAllegations);
      localStorage.setItem('selectedAllegations', JSON.stringify(allegation));
      this.router.navigate(
        ['case/allegation/injury-details/' + injuryId], { queryParams: { 'select-allegation': 'true' } });
    }
  }

  cancel() {
    const injuryId = this.route.snapshot.paramMap.get('injuryId') ? this.route.snapshot.paramMap.get('injuryId') : '0';
    localStorage.removeItem('selectedAllegations');
    this.router.navigate(
      ['case/allegation/injury-details/' + injuryId], { queryParams: { 'select-allegation': 'true' } });
  }

  showErrorModal() {
    const initialState = {
      message: 'Please select an Allegation to Continue.',
      title: 'Allegation List',
    };
    this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
  }
}
