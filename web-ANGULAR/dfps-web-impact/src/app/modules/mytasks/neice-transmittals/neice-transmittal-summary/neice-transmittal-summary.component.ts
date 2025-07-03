import { Component, HostListener, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { DataTable, DfpsFormValidationDirective, DirtyCheck, ENVIRONMENT_SETTINGS, FormUtils, FormValue, NavigationService, SET } from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { IncomingOutgoingTransmittals, TransmittalTypeEnum } from '../enum/neice-transmittal-type';
import { NeiceTransmittalSummary } from '../model/NeiceTransmittalSummary';
import { NeiceTransmittalsService } from '../service/neice-transmittals.service';
import { NeiceAdditionalInfoComponent } from './neice-additional-info/neice-additional-info.component';
import { NeiceTransmittalSummaryValidators } from './neice-transmittal-summary.validator';
import { saveAs } from 'file-saver';
import { NeiceRejectInfoComponent } from './neice-reject-info/neice-reject-info.component';

@Component({
  selector: 'app-neice-transmittal-summary',
  templateUrl: './neice-transmittal-summary.component.html',
  styleUrls: ['./neice-transmittal-summary.component.css']
})
export class NeiceTransmittalSummaryComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  neiceTransmittalSummary: NeiceTransmittalSummary;
  neiceTransmittalSummaryForm: FormGroup;
  idNeiceTransmittal: string;
  inboundOutboundType: string;
  subs: Subscription[] = [];
  documentsDataTable: DataTable;
  tableColumn: any[];
  tableBody: any[];
  selectedDocumentData: any[];
  types: any[];
  dispPublicPrivate: boolean;
  dispConcurrence: boolean;
  dispAdditionalInfo: boolean;
  dispGenderOtherGender: boolean;
  dispICWATitleIVEICPCOtherLegalTypeOfCare: boolean;
  dispDecisionDTDenialReasonRemarks: boolean;
  dispPlacementInfoSection: boolean;
  dispSaveBtn: boolean;
  dispReqAddInfoBtn: boolean;
  dispProcessTransmittalBtn: boolean;
  disablepProcessTransmittalBtn: boolean;
  dispRejectBtn: boolean;
  dispDownloadToDesktop: boolean;
  readonly DEFAULT_PAGE_SIZE = 20;

  formValues: FormValue[];
  dispTermination: boolean;
  dispPlacementDtType: boolean;

  constructor(private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private modalService: BsModalService,
    private neiceTransmittalService: NeiceTransmittalsService,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  createForm() {
    this.neiceTransmittalSummaryForm = this.formBuilder.group(
      {
        priority: [''],
        rejectHoldInd: [''],
        comment: [''],
        assignedTo: [''],
        specificType: ['']
      }, {
      validators: [NeiceTransmittalSummaryValidators.validateTransmittalSummaryComment]
    }
    );
  }

  ngOnInit(): void {
    this.navigationService.setTitle('NEICE Transmittal Summary');
    this.intializeScreen();
    localStorage.removeItem('backFrom');
  }

  intializeScreen() {
    this.createForm();
    this.idNeiceTransmittal =
      this.route.snapshot.paramMap.get('idNeiceTransmittal') ? this.route.snapshot.paramMap.get('idNeiceTransmittal') : '0';
    this.inboundOutboundType = this.route.snapshot.paramMap.get('incomingOutgoing') ? this.route.snapshot.paramMap.get('incomingOutgoing') : '';
    this.neiceTransmittalService.displayTransmittalSummary(this.idNeiceTransmittal, this.inboundOutboundType).subscribe((response) => {
      this.neiceTransmittalSummary = response;
      if (this.neiceTransmittalSummary) {
        this.loadTransmittarySummaryInfo();
        this.enableDisableFeilds();
      }
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    });

    this.types = [
      { label: 'Case Specific', value: 'CS' },
      { label: '100 A Specific', value: '100AS' },
    ];
  }

  loadTransmittarySummaryInfo() {
    if (this.neiceTransmittalSummary && this.neiceTransmittalSummary.transmittalActivity) {
      this.neiceTransmittalSummaryForm.patchValue({
        priority: this.neiceTransmittalSummary.transmittalActivity.priority,
        rejectHoldInd: this.neiceTransmittalSummary.transmittalActivity.rejectHoldInd,
        comment: this.neiceTransmittalSummary.transmittalActivity.comment ? this.neiceTransmittalSummary.transmittalActivity.comment : '',
        assignedTo: this.neiceTransmittalSummary.transmittalActivity.assignedTo,
      });
      this.getDocumentListDetails();
    }
  }

  enableDisableFeilds() {
    switch (this.inboundOutboundType) {
      case IncomingOutgoingTransmittals.Incoming_Transmittals:
        FormUtils.enableFormControlStatus(this.neiceTransmittalSummaryForm, ['priority', 'rejectHoldInd', 'comment', 'assignedTo']);
        this.dispGenderOtherGender = true;
        this.dispSaveBtn = true;
        this.dispReqAddInfoBtn = true;
        this.dispRejectBtn = true;
        this.dispProcessTransmittalBtn = true;
        this.disablepProcessTransmittalBtn = (this.neiceTransmittalSummary.transmittalDetail.transmittalStatusCode !== '10');
        switch (this.neiceTransmittalSummary.transmittalDetail.transmittalTypeCode) {
          case TransmittalTypeEnum.HOME_STUDY_REQUEST:
            this.dispProcessTransmittalBtn = false;
            break;
          default:
            break;
        }
        break;
      default:
        FormUtils.disableFormControlStatus(this.neiceTransmittalSummaryForm, ['priority', 'rejectHoldInd', 'assignedTo', 'comment']);
        break;
    }

    switch (this.neiceTransmittalSummary.transmittalDetail.transmittalTypeCode) {
      case TransmittalTypeEnum.HOME_STUDY_REQUEST:
        this.dispPublicPrivate = true;
        this.dispICWATitleIVEICPCOtherLegalTypeOfCare = true;
        this.dispPlacementInfoSection = true;
        this.dispPlacementDtType = (this.neiceTransmittalSummary.transmittalDetail.homeStudyTypeCode === '10');
        break;
      case TransmittalTypeEnum.HOME_STUDY_UPDATE:
        this.dispPublicPrivate = true;
        this.dispICWATitleIVEICPCOtherLegalTypeOfCare = true;
        this.dispPlacementInfoSection = true;
        break;
      case TransmittalTypeEnum.HOME_STUDY_RESPONSE:
        this.dispPublicPrivate = true;
        this.dispICWATitleIVEICPCOtherLegalTypeOfCare = true;
        this.dispDecisionDTDenialReasonRemarks = true;
        this.dispPlacementInfoSection = true;
        break;
      case TransmittalTypeEnum.CONCURRENCE_RESPONSE:
        this.dispConcurrence = true;
        break;
      case TransmittalTypeEnum.PLACEMENT_DECISION:
        this.dispICWATitleIVEICPCOtherLegalTypeOfCare = true;
        this.dispPlacementInfoSection = true;
        this.dispTermination = true;
        this.dispPlacementDtType = true;
        break;
      case TransmittalTypeEnum.ADDITIONALINFORMATION:
      case TransmittalTypeEnum.STATUS_REPORT_RESPONSE:
      case TransmittalTypeEnum.PROGRESS_REPORT_RESPONSE:
        this.dispAdditionalInfo = true;
        break;
      default:
        this.dispPublicPrivate = false;
        break;
    }
  }

  getDocumentListDetails() {
    this.tableColumn = [
      { field: 'creationDate', header: 'Creation Date', sortable: true, width: 50 },
      {
        field: 'documentName', header: 'Document Name', width: 150, handleClick: true, url: '#', urlParams: ['']
      },
      { field: 'documentType', header: 'Document Type', sortable: true, width: 50 },
      { field: 'description', header: 'Document Description', width: 200 },
    ];
    this.documentsDataTable = { tableColumn: this.tableColumn, isMultiSelect: true, displaySelectAll: true, isPaginator: true };
    if (this.neiceTransmittalSummary && this.neiceTransmittalSummary.documentsToAttach) {
      this.documentsDataTable.tableBody = this.neiceTransmittalSummary.documentsToAttach;
    }
  }

  selectedDocument(event) {
    if (event.length > 0) {
      this.dispDownloadToDesktop = true;
      this.selectedDocumentData = [];
      this.selectedDocumentData = event;
    } else {
      this.dispDownloadToDesktop = false;
      this.selectedDocumentData = [];
    }
  }

  handleTransmittalSummaryDocumentClick(event: any) {
    this.neiceTransmittalService.downloadDocument(this.inboundOutboundType, event.tableRow.neiceAttachementId)
      .subscribe(blob => saveAs(blob, event.tableRow.documentName));
  }

  showNeiceAdditionalInfoPopup() {
    this.neiceTransmittalService.displayAdditionalInformation().subscribe(res => {
      const modalFieldValues = res;
      const modal = this.modalService.show(NeiceAdditionalInfoComponent, {
        class: 'modal-lg modal-dialog-centered', initialState: modalFieldValues
      });
      (modal.content as NeiceAdditionalInfoComponent).additionalInformationEvent.subscribe(result => {
        this.neiceTransmittalService.sendAdditionalInformation(this.idNeiceTransmittal, result).subscribe();
      });
    });
  }

  showNeiceRejectnfoPopup() {
    this.neiceTransmittalService.displayDocumentType().subscribe(res => {
      const initialState = { 
        documentTypes: res.documentTypes, 
        comment: this.neiceTransmittalSummaryForm.value.comment 
      };

      const modal = this.modalService.show(NeiceRejectInfoComponent, {
        class: 'modal-lg modal-dialog-centered', initialState: initialState
      });
      (modal.content as NeiceRejectInfoComponent).rejectInformationEvent.subscribe(result => {
        this.neiceTransmittalService.rejectTransmittalSummary(this.idNeiceTransmittal, result)
		.subscribe(response => {
          window.location.reload();
        });
      });
    });
  }

  save() {
    if (this.validateFormGroup(this.neiceTransmittalSummaryForm)) {
      this.neiceTransmittalService.saveTransmittalSummary(this.idNeiceTransmittal, this.neiceTransmittalSummaryForm.value)
        .subscribe(response => {
          window.location.reload();
        });
    }
  }

  reject() {
    if (this.validateFormGroup(this.neiceTransmittalSummaryForm)) {
      this.neiceTransmittalService.rejectTransmittalSummary(this.idNeiceTransmittal, this.neiceTransmittalSummaryForm.value)
        .subscribe(response => {
          window.location.reload();
        });
    }
  }

  process() {
    const docsSelected = { docsSelected: this.selectedDocumentData }
    const payload = { ...this.neiceTransmittalSummaryForm.value, ...docsSelected };
    this.neiceTransmittalService.processTransmittalSummary(this.idNeiceTransmittal, payload).subscribe(response => {
      switch (this.neiceTransmittalSummary.transmittalDetail.transmittalTypeCode) {
        case TransmittalTypeEnum.PLACEMENT_DECISION:
          window.location.href =  `${this.environmentSettings.impactP2WebUrl}/case/icpc/event/displayDetail/?cacheKey=${sessionStorage.getItem('cacheKey')}`
          break;
        default:
          window.location.reload();
          break;
      }
    });
  }

  downloadToDesktop() {
    if (this.selectedDocumentData.length > 0) {
      this.selectedDocumentData.forEach(doc => {
        this.neiceTransmittalService.downloadDocument(this.inboundOutboundType, doc.neiceAttachementId)
          .subscribe(blob => saveAs(blob, doc.documentName));
      });
    }
  }

  neiceHomeStudyIdIMPACTUrl(): string {
    return `${this.environmentSettings.impactP2WebUrl}/case/icpc/event/displayDetail/?cacheKey=${sessionStorage.getItem('cacheKey')}`
  }

  @HostListener('window:popstate' || 'window:hashchange', ['$event'])
  onPopState(event) {
    localStorage.setItem('backFrom', 'NeiceTransmittalSummary');
  }

  ngOnDestroy() {
    this.subs.forEach((sub) => sub.unsubscribe());
  }
}
