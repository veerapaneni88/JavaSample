import { Component, ElementRef, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsConfirmComponent,
  DfpsCommonValidators,
  DfpsFormValidationDirective,
  DirtyCheck,
  FormUtils,
  ENVIRONMENT_SETTINGS,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { InjuryDetail, InjuryDetailDisplayRes } from '../model/InjuryDetail';
import { CaseService } from '../service/case.service';
import { InjuryDetailValidators } from './injury-detail.validator';
import { BsModalService } from 'ngx-bootstrap/modal';
import { Subscription } from 'rxjs';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'injury-detail',
  templateUrl: './injury-detail.component.html'
})

export class InjuryDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  injuryDetailForm: FormGroup;
  injuryDetailDisplayRes: InjuryDetailDisplayRes;
  injuryDetail: InjuryDetail = {};
  allegationTable: DataTable;
  injuryRelatedTo: any[];
  selectedInjuryRelatedTo: any[];
  abuseNeglect: any[];
  allegations: any[];
  tableColumn: any[];
  selectedInjuryTypes: any[];
  displayAllegationTable = false;
  displayAllegedPerpetrator = false;
  displayOtherTypeOfInjury = false;
  displayOtherInjuryRelatedTo = false;
  displayInjuryRelatedTo = false;
  isReadOnly = false;
  showDeleteButton = false;
  modalSubscription: Subscription;
  eventid: any;
  injuryId: any;

  @ViewChild('errors') errorElement: ElementRef;

  constructor(
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private caseService: CaseService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private route: ActivatedRoute,
    private router: Router,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);

  }

  ngOnInit(): void {
    this.navigationService.setTitle('Injury Detail');
    this.helpService.loadHelp('Case');
    this.abuseNeglect = [
      { label: 'Yes', value: 'Y' },
      { label: 'No', value: 'N' }
    ];
    this.createForm();

    if (this.router.url.includes('event')) {
      this.eventid = this.route.snapshot.paramMap.get('eventId');
      this.caseService.displayInjuryDetailForEvent(this.eventid).subscribe(
        response => {
          this.displayInjuryDetails(response);
        });
    } else {
      this.injuryId = this.route.snapshot.paramMap.get('injuryId') ? this.route.snapshot.paramMap.get('injuryId') : '0';
      this.caseService.displayInjuryDetail(this.injuryId)
        .subscribe(response => {
          this.displayInjuryDetails(response);
        });
    }

  }

  createForm() {
    this.injuryDetailForm = this.formBuilder.group({
      victim: ['', [Validators.required]],
      injuryType: [''],
      otherTypeInjury: [''],
      injuryCausedBy: ['', [Validators.required]],
      injuryRelatedTo: [''],
      otherInjuryRelatedTo: [''],
      injuryDate: ['', [Validators.required, DfpsCommonValidators.validateDate, InjuryDetailValidators.validateFutureDate]],
      approximateDate: [''],
      injuryTime: ['', [Validators.required]],
      approximateTime: [''],
      injuryLocation: ['', [Validators.required]],
      injuryDetermination: ['', [Validators.required]],
      abuseNeglect: [''],
      allegedPerpetrator: [''],
      injuryDescription: [''],
      allegations: ['']
    }, {
      validators: [
        InjuryDetailValidators.saveValidaion(),
        InjuryDetailValidators.customRequiredValidation,
        InjuryDetailValidators.conditionallyRequiredValidation
            ]
    });
  }

  populateInjuryRelatedTo(event) {
    const injuryCausedBy = this.injuryDetailForm.controls.injuryCausedBy.value;
    this.displayOtherInjuryRelatedTo = this.injuryDetail?.injuryRelatedTo?.includes('OTR') || injuryCausedBy === 'OTH';

    if (event) {
      this.selectedInjuryRelatedTo = [];
      this.injuryDetailForm.controls.injuryRelatedTo.setValue('');
      this.injuryDetailForm.patchValue({ otherInjuryRelatedTo: '' });
    }
    switch (injuryCausedBy) {
      case 'DIS':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedByDiscipline;
        break;

      case 'FAW':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedByWeapons;
        break;

      case 'IES':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedByIndoorEquipments;
        break;

      case 'OES':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedByOutdoorEquipments;
        break;

      case 'RAS':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedByRestraint;
        break;

      case 'SLI':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedBySelf;
        break;

      case 'TLE':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedTools;
        break;

      case 'VHL':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedVehicle;
        break;

      case 'WTA':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedWaterActivities;
        break;

      case 'WTR':
        this.injuryRelatedTo = this.injuryDetailDisplayRes.injuryCausedWeather;
        break;

      case 'OTH' || undefined:
        this.injuryRelatedTo = [];
        this.injuryDetailForm.patchValue({ injuryRelatedTo: [] });
        break;

      default:
        this.injuryRelatedTo = [];
        break;
    }

  }

  updateSelectedInjuryType(event: any[]) {
    this.displayOtherTypeOfInjury = event?.length > 0 && event.find(s => { return s === 'OTH' });
    this.injuryDetailForm.patchValue({ injuryType: [...event] });
    if (!(this.injuryDetailForm.get('injuryType').value.includes('OTH'))) {
      this.injuryDetailForm.patchValue({ otherTypeInjury: '' });
    }
  }

  updateSelectedInjuryRelatedTo(event) {
    this.displayOtherInjuryRelatedTo = event?.length > 0 && event.includes('OTR');
    if (!this.displayOtherInjuryRelatedTo) {
      this.injuryDetailForm.patchValue({ otherInjuryRelatedTo: '' });
    }
    this.injuryDetailForm.patchValue({ injuryRelatedTo: [...event] });
  }

  allegationDataTable() {
    this.allegations = localStorage.getItem('selectedAllegations') ?
      JSON.parse(localStorage.getItem('selectedAllegations')) : this.allegations;
    this.tableColumn = [
      { 
        field: 'type', 
        header: 'Alleg',                 
        handleClick: this.displayAllegationLink(),
        url: this.environmentSettings.impactP2WebUrl +'/case/allegationIdDetail/:id',
        urlParams: ['id'],
        width: 50 
      },
      { field: 'victimName', header: 'Alleged Victim', width: 100 },
      { field: 'allegedPerpetratorName', header: 'Alleged Perpetrator', width: 100 },
      { field: 'disposition', header: 'Disp', width: 50 },
      { field: 'severity', header: 'Severity', width: 50 },
      { field: 'stageCode', header: 'Stage', width: 50 },
      { field: 'id', header: 'Allegation ID', width: 75 },
      { field: 'victimId', header: 'VC ID', width: 75 },
      { field: 'allegedPerpetratorId', header: 'AP ID', width: 75 },
      { field: 'createdDate', header: 'Date Entered', width: 75 },
      { field: 'createdBy', header: 'Entered By', width: 100 },
      { field: 'lastUpdatedDate', header: 'Last Modified Date', width: 75 },
      { field: 'lastUpdatedBy', header: 'Last Modified By', width: 100 }
    ];
    this.allegationTable = {
      tableBody: this.allegations,
      tableColumn: this.tableColumn,      
    };
  }

  displayInjuryDetails(res: any) {
    if (res) {
      this.injuryDetailDisplayRes = res;
      this.injuryDetail = res.injuryDetailReq;
      this.isReadOnly = this.injuryDetailDisplayRes.pageMode === 'VIEW';
      this.populateFormData();
      this.showDeleteButton = this.injuryId !== '0';
      if (this.isReadOnly) {
        FormUtils.disableFormControlStatus(this.injuryDetailForm, ['victim', 'otherTypeInjury',
          'injuryCausedBy', 'otherInjuryRelatedTo', 'injuryDate', 'approximateDate',
          'injuryTime', 'approximateTime', 'injuryLocation', 'injuryDetermination',
          'abuseNeglect', 'allegedPerpetrator', 'injuryDescription']);
      }
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    }
  }

  populateFormData() {
    if (this.route.snapshot.queryParamMap.get('select-allegation')) {
      this.injuryDetail = JSON.parse(localStorage.getItem('injuryDetail'));
    } else {
      localStorage.removeItem('selectedAllegations');
    }
    if (this.injuryDetail) {
      this.injuryDetailForm.setValue({
        victim: this.injuryDetail?.victim ? this.injuryDetail?.victim.toString() : '',
        injuryType: this.injuryDetail?.injuryType,
        otherTypeInjury: this.injuryDetail?.otherTypeInjury,
        injuryCausedBy: this.injuryDetail?.injuryCausedBy,
        injuryRelatedTo: this.injuryDetail?.injuryRelatedTo,
        otherInjuryRelatedTo: this.injuryDetail?.otherInjuryRelatedTo,
        injuryDate: this.injuryDetail?.injuryDate,
        approximateDate: this.injuryDetail?.approximateDate,
        injuryTime: this.injuryDetail?.injuryTime,
        approximateTime: this.injuryDetail?.approximateTime,
        injuryLocation: this.injuryDetail?.injuryLocation,
        injuryDetermination: this.injuryDetail?.injuryDetermination,
        abuseNeglect: this.injuryDetail?.abuseNeglect,
        allegedPerpetrator: this.injuryDetail?.allegedPerpetrator ? this.injuryDetail?.allegedPerpetrator?.toString() : '',
        injuryDescription: this.injuryDetail?.injuryDescription,
        allegations: this.injuryDetail?.allegations
      });
    }
    this.allegations = this.injuryDetail?.allegations;
    this.displayOtherTypeOfInjury = this.injuryDetail?.injuryType?.find(s => { return s === 'OTH' });
    this.selectedInjuryTypes = this.injuryDetail?.injuryType;
    this.selectedInjuryRelatedTo = this.injuryDetail?.injuryRelatedTo;
    this.populateInjuryRelatedTo(null);
    this.displaySelectAllegation();
    this.allegationDataTable();
  }

  selectAllegations() {
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    localStorage.setItem('preSelectedAllegations', this.allegations ? JSON.stringify(this.allegations) : null);
    this.injuryDetail = this.injuryDetail == null ? {} : this.injuryDetail;
    localStorage.setItem('injuryDetail', JSON.stringify(Object.assign(this.injuryDetail, this.injuryDetailForm.value)));
    const injuryId = this.route.snapshot.paramMap.get('injuryId') ? this.route.snapshot.paramMap.get('injuryId') : '0';
    this.router.navigate(['case/allegation/injury-details/' + injuryId + '/allegation-list']);
  }

  displaySelectAllegation() {
    if (!this.isReadOnly) {
      this.displayAllegationTable = this.injuryDetailForm.controls.abuseNeglect.value === 'Y';
      this.displayAllegedPerpetrator = this.injuryDetailForm.controls.abuseNeglect.value === 'Y';
      if (this.injuryDetailForm.controls.abuseNeglect.value !== 'Y') {
        this.allegations = null;
        this.injuryDetailForm.controls.allegations.setValue(null);
        localStorage.setItem('preSelectedAllegations', null);
        localStorage.setItem('selectedAllegations', null);
        this.injuryDetailForm.controls.allegedPerpetrator.setValue(null);
      }
      this.allegationDataTable();
    }
  }

  save() {
    this.injuryDetailForm.controls.allegations.setValue(this.allegations);
    if (this.validateFormGroup(this.injuryDetailForm)) {
      this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      this.injuryDetail = this.injuryDetail ? this.injuryDetail : {};
      this.caseService.saveInjuryDetail(Object.assign(this.injuryDetail, this.injuryDetailForm.value)).subscribe(res => {
        if (res) {
          setTimeout(() => {
            this.caseService.redirectToUrl('case/allegation/injury-details');
          }, 3000);
        }
      });
    }
  }

  delete() {
    const selectInjuryIds: any[] = [];
    selectInjuryIds.push(this.injuryDetail.id);

    const initialState = {
      message: 'Are you sure you want to delete this information?',
      title: 'Injury Details',
      showCancel: true,
    };

    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    this.modalSubscription = (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.caseService.deleteInjuryDetails(selectInjuryIds).subscribe(res => {
          this.router.navigate(['/case/allegation/injury-details']);
        });
      }
      document.getElementById('deleteButtonId').focus();
    });
  }
  
  handleAllegationTableLinkRouting(event: any) {
    const link = (event.link as string);
    if(link){
      window.location.href=link;
    }
  }

  displayAllegationLink() {
    return true;
  }

}