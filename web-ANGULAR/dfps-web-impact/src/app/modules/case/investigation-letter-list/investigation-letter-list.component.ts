import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DfpsFormValidationDirective,
  DfpsConfirmComponent,
  DataTable,
  DirtyCheck,
  NavigationService
} from 'dfps-web-lib';
import { Subscription } from 'rxjs';
import { CaseService } from '../service/case.service';
import { InvestigationLetterListRes } from '../model/InvestigationLetter';
import { saveAs } from 'file-saver';
import { BsModalService } from 'ngx-bootstrap/modal';
import { InvestigationLetterListValidator } from './investigation-letter-list.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'investigation-letter-list',
  templateUrl: './investigation-letter-list.component.html'
})
export class InvestigationLetterListComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  @ViewChild('downloadButton', { read: ElementRef }) downloadButton;

  investigationLetterListForm: FormGroup;
  investigationLetterListDataTable: DataTable;
  investigationLetterListRes: InvestigationLetterListRes;
  modalSubscription: Subscription;
  tableBody: any[];
  tableColumn: any[];
  hideAddButton = false;
  hideDownloadButton = false;
  selectedInvLetter: any;
  disableAddLetterType = false;


  constructor(
    private navigationService: NavigationService,
    private helpService: HelpService,
    private formBuilder: FormBuilder,
    private router: Router,
    private caseService: CaseService,
    private modalService: BsModalService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnDestroy() {
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Letter List');
    this.investigationLetterListForm = this.formBuilder.group({
    });
    this.helpService.loadHelp('Letters');
    this.initializeScreen();
  }

  initializeScreen() {
    this.createForm();
    this.caseService.getInvestigationLetterList().subscribe(response => {
      this.investigationLetterListRes = response;
      this.investigationLetterListRes.investigationLetters.map(letter => {
        if(letter.event === "" || letter.event === null) {
          letter.event = "PROC";
        }
      });
      this.loadInvestigationLetterListDataTable();
      this.hideAddButton = this.investigationLetterListRes.pageMode === 'VIEW';
      this.hideDownloadButton = this.investigationLetterListRes.pageMode === 'VIEW';
      this.disableAddLetterType = this.investigationLetterListRes.pageMode === 'VIEW';

    });
  }

  createForm() {
    this.investigationLetterListForm = this.formBuilder.group(
      {
        addLetterType: ['']
      }, {
        validators: [
          InvestigationLetterListValidator.requiredFieldValidations
        ]
      }
    );
  }

  loadInvestigationLetterListDataTable() {
    this.tableColumn = [
      { field: 'createdDate', header: 'Date Entered', sortable: false, width: 80 },
      { field: 'event', header: 'Status', sortable: false, width: 45 },
      {
        field: 'letterTypeDecode',
        header: 'Letter Type',
        width: 200,
        sortable: false,
        handleClick: true,
        url: '/case/letters/investigation-letters/:letterType/:id',
        urlParams: ['id', 'letterType'],
      },
      { field: 'administratorName', header: 'Letter To', sortable: false, width: 120 },
      { field: 'letterMethodDecode', header: 'Method', sortable: false, width: 120 },
      { field: 'createdBy', header: 'Entered By', sortable: false, width: 130 },
      { field: 'lastUpdatedDate', header: 'Last Modified Date', sortable: false, width: 95 },
      { field: 'lastModifiedBy', header: 'Last Modified By', sortable: false, width: 120 },
      { field: 'id', header: 'Letter ID', sortable: false, width: 65 },
    ];

    this.investigationLetterListDataTable = {
      tableBody: this.investigationLetterListRes.investigationLetters,
      tableColumn: this.tableColumn,
      isSingleSelect: this.investigationLetterListRes.pageMode !== 'VIEW',
      isPaginator: (this.investigationLetterListRes && this.investigationLetterListRes.investigationLetters &&
        this.investigationLetterListRes.investigationLetters.length) > 10
    };

    if (this.investigationLetterListDataTable.tableBody && this.investigationLetterListDataTable.tableBody.length) {
      this.investigationLetterListDataTable.tableBody.forEach(data => {
        data.stageName = this.investigationLetterListRes.stageName;
        data.caseId = this.investigationLetterListRes.caseId;
        data.stageId = this.investigationLetterListRes.stageId;
      })
    }
  }

  handleRouting(event: any) {
      this.router.navigate([event.link]);
  }

  setSelectedInvLetter(event: Event) {
    this.selectedInvLetter = event;
  }

  add() {
    if (this.validateFormGroup(this.investigationLetterListForm)) {
      if ('' !== this.investigationLetterListForm.get('addLetterType').value) {
        const headerUrl = 'case/letters/investigation-letters/' + this.investigationLetterListForm.get('addLetterType').value + '/0';
        this.router.navigate([headerUrl]);
      }
    }
  }

  download() {
    if (this.selectedInvLetter && this.selectedInvLetter.event === 'COMP') {
      let letterName = this.investigationLetterListRes.letterTypes.find(letterType =>
        letterType.code === this.selectedInvLetter.letterType).decode;
      if(this.selectedInvLetter.letterType === 'ANF'){
        letterName = 'AN Findings Letter to Perp - 2894';
      } else if(this.selectedInvLetter.letterType === 'AIR'){
        letterName = 'AN Investigation Results to Parents of Victims - 2893';
      }
      this.caseService.downloadInvestigationLetter(this.selectedInvLetter.id, false)
        .subscribe(blob => saveAs(blob, letterName + '_' + this.investigationLetterListRes.caseId + '.pdf'));
    } else {
      const initialState = {
        title: 'Investigation Letters',
        message: 'Please select a completed Letter before clicking the Download Button.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered',
        initialState,
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
        this.downloadButton.nativeElement.lastChild.focus();
       });
    }
  }

}
