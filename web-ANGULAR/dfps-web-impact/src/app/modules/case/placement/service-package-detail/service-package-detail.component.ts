import { formatDate } from '@angular/common';
import { Component, Inject, LOCALE_ID, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AddonServicePackage, ServicePackage, ServicePackageRes } from '@case/model/servicePackage';
import { CaseService } from '@case/service/case.service';
import { Store } from '@ngrx/store';
import {
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  DropDown,
  ENVIRONMENT_SETTINGS, FormService,
  FormUtils, FormValue,
  NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ServicePackageDetailValidators } from './service-package-detail.validator';
import { Subscription } from 'rxjs';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'service-package-detail',
  templateUrl: './service-package-detail.component.html'
})
export class ServicePackageDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  servicePackageDetailForm: FormGroup;
  servicePackageRes: ServicePackageRes;
  addOnEndReasons: any;
  modifiedEndDate = false;
  servicePackages: DropDown[];
  endReasons: DropDown[];
  endDate: any;
  hideOverrideReason = true;
  hideOverrideReasonComments = true;
  hideEndReasonAndComments = true;
  servicePackageId: number;
  addonServiceCheckboxFields: any[];
  displayAddonSectionCode: any[] = [];
  hideAddOnEndReasonComments: any[] = [];
  showAddOnServices = false;
  addonServiceForm: FormGroup;
  isEditMode = false;
  cansAssessmentRadio: any;
  showCansAssessmentSection = false;
  maxDate = { year: new Date().getUTCFullYear(), month: new Date().getMonth() + 1, day: new Date().getDate() }
  showCansAssessmentQuestion = true;
  primaryStartDate: any = {};
  formValues: FormValue[];
  formsSubscription: Subscription;
  servicePackageAssessmentForm: FormGroup;
  isMPSEnvironment = false;
  showThirdPartyReviewerSection = false;

  constructor(
    private formBuilder: FormBuilder,
    private formService: FormService,
    private caseService: CaseService,
    private modalService: BsModalService,
    private navigationService: NavigationService,
    private helpService: HelpService,
    private route: ActivatedRoute,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    @Inject(LOCALE_ID) private locale: string,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Service Package');
    this.helpService.loadHelp('Placement');
    this.createForm();
    const routeParams = this.route.snapshot.paramMap;
    if (routeParams) {
      this.servicePackageId = Number(routeParams.get('servicePackageId'));
    }
    this.caseService.getServicePackageDetail(this.servicePackageId).subscribe(
      response => {
        this.servicePackageRes = response;
        this.loadServicePackageDetail();
        this.initializeServicePackageType();
        this.initializeServicePackageCaseType();
        this.displayEndReasons();
        this.displayOverrideReasonOnload();
        this.initializeServicePackage();
        this.initializeAddonServicesOnload();
        this.getPageMode();
        this.displayCansAssessmentQuestion();
        this.displayCansSection();
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.displayThirdPartyReviewerSection();
      });
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
    this.isMPSEnvironment = 'MPS' === this.environmentSettings.environmentName.toUpperCase();
  }

  ngOnDestroy(): void {
    if (this.formsSubscription) {
      this.formsSubscription.unsubscribe();
    }
  }
  launchForm(data: any) {
    if (data) {
      const { pageMode, eventId } = this.servicePackageRes;
      if ((pageMode === 'NEW' && !eventId) || (pageMode === 'EDIT' && this.servicePackageDetailForm.dirty)) {
        const initialState = {
          title: 'Service Package Assessment',
          message:
            'You must save this page and click the hyperlink to return to the page\n' +
            'before launching the Assessment Request Form.',
          showCancel: false
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md', initialState,
          ignoreBackdropClick: true,
          keyboard: false,
          backdrop: true
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
        });
        return;
      } else if (this.servicePackageRes.servicePackage.servicePackageType === 'RMD' && (pageMode === 'EDIT' || pageMode === 'VIEW')) {
        if (this.servicePackageRes.servicePackage.cansAssessmentCompletedDate == null
          && (this.servicePackageRes.servicePackage.cansCompletedIndicator == null
            || this.servicePackageRes.servicePackage.cansCompletedIndicator === 'N')) {
          const initialState = {
            title: 'Service Package Assessment',
            message:
              'The CANS Assessment must be completed before Third Party Assessment.',
            showCancel: false
          };
          const modal = this.modalService.show(DfpsConfirmComponent, {
            class: 'modal-md', initialState,
            ignoreBackdropClick: true,
            keyboard: false,
            backdrop: true
          });
          (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
          });
          return;
        }
        else {
          this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
        }
      }
    } else {
      const initialState = {
        title: 'Service Package Assessment',
        message:
          'A document must be selected before the Launch button is clicked.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md', initialState,
        ignoreBackdropClick: true,
        keyboard: false,
        backdrop: true
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
      });
      return;
    }
  }

  createForm() {
    this.servicePackageDetailForm = this.formBuilder.group({
      servicePackageCaseType: [''],
      servicePackageType: [''],
      servicePackage: [''],
      overrideReason: [''],
      overrideReasonComments: [''],
      startDate: [''],
      endDate: [''],
      endReason: [''],
      endReasonComments: [''],
      endDateRequired: [''],
      endReasonRequired: [''],
      addonServicePackages: new FormArray([]),
      overrideReasonRequired: [''],
      endDateModifed: [''],
      pageMode: [''],
      existingEndDate: [''],
      endDateModfied: [''],
      cansCompletedIndicator: [''],
      cansNotCompletedReason: [''],
      cansNotCompletedComments: [''],
      cansAssessmentCompletedDate: [''],
      cansRecommendationComments: [''],
      cReleaseDate: [''],
      autopopulatedEndDate: [''],
      cansAssessmentSection: [''],
      thirdPartyReviewerComments: [''],
      groRecommendedIndicator: [''],
      groAssessmentCompletedDate: ['', DfpsCommonValidators.validateDate],
      cansAssessor: [''],
      stageType: [''],
      cansAssessorName: ['']
    }, {
      validators: [
        ServicePackageDetailValidators.servicePackageValidation,
      ]
    });
  }

  loadServicePackageDetail() {
    if (this.servicePackageRes && this.servicePackageRes.servicePackage) {
      this.servicePackageDetailForm.patchValue({
        servicePackageCaseType: this.servicePackageRes?.servicePackage?.servicePackageCaseType,
        servicePackageType: this.servicePackageRes?.servicePackage?.servicePackageType,
        servicePackage: this.servicePackageRes?.servicePackage?.servicePackage,
        overrideReason: this.servicePackageRes?.servicePackage?.overrideReason,
        overrideReasonComments: this.servicePackageRes?.servicePackage?.overrideReasonComments,
        startDate: this.servicePackageRes?.servicePackage?.startDate,
        endDate: this.servicePackageRes?.servicePackage?.endDate,
        endReason: this.servicePackageRes?.servicePackage?.endReason,
        endReasonComments: this.servicePackageRes?.servicePackage?.endReasonComments,
        lastUpdatedDate: this.servicePackageRes?.servicePackage?.lastUpdatedDate,
        lastModifiedBy: this.servicePackageRes?.servicePackage?.lastModifiedBy,
        cansCompletedIndicator: this.servicePackageRes?.servicePackage?.cansCompletedIndicator,
        cansNotCompletedReason: this.servicePackageRes?.servicePackage?.cansNotCompletedReason,
        cansNotCompletedComments: this.servicePackageRes?.servicePackage?.cansNotCompletedComments,
        cansAssessmentCompletedDate: this.servicePackageRes?.servicePackage?.cansAssessmentCompletedDate,
        cansRecommendationComments: this.servicePackageRes?.servicePackage?.cansRecommendationComments,
        existingEndDate: this.servicePackageRes?.servicePackage?.endDate,
        thirdPartyReviewerComments: this.servicePackageRes?.servicePackage?.thirdPartyReviewerComments,
        groRecommendedIndicator: this.servicePackageRes?.servicePackage?.groRecommendedIndicator,
        groAssessmentCompletedDate: this.servicePackageRes?.servicePackage?.groAssessmentCompletedDate,
        cansAssessorName: this.servicePackageRes?.servicePackage?.cansAssessorName,
      })
      this.hideEndReasonAndComments = this.servicePackageRes?.servicePackage?.endReason == null;
      this.endDate = this.servicePackageRes?.servicePackage?.endDate;
      this.servicePackageDetailForm.patchValue({ endDate: this.endDate, endDateRequired: !!this.endDate });
    }
    this.servicePackageDetailForm.patchValue({ cReleaseDate: this.servicePackageRes?.creleaseDate,
        stageType: this.servicePackageRes?.stageType, cansAssessor: this.servicePackageRes?.cansAssessor });
    const releasePartitionDate = this.servicePackageRes?.creleaseDate?.split('/');
    if (releasePartitionDate.length === 3) {
      this.primaryStartDate = {
        year: Number.parseInt(releasePartitionDate[2], 10),
        month: Number.parseInt(releasePartitionDate[1], 10),
        day: Number.parseInt(releasePartitionDate[0], 10),
      };
    }
    this.formValues = this.getForms();
  }

  getCustomStyles() {
    return 'height: 140px';
  }

  initializeServicePackageType() {
    if (this.servicePackageId === 0) {
      this.servicePackageDetailForm.patchValue({ servicePackageType: this.servicePackageRes.stageType === 'C-PB' ? 'SEL' : 'RMD' });
    }
  }

  initializeServicePackage() {
    if (this.servicePackageId === 0 && this.servicePackageRes.recommendedServicePackage) {
      const servicePackageCaseType = this.servicePackageRes?.recommendedServicePackage?.servicePackageCaseType
      this.populateServicePackage(servicePackageCaseType)
      this.servicePackageDetailForm.patchValue({
        servicePackage: this.servicePackageRes?.recommendedServicePackage?.servicePackage,
        servicePackageCaseType: this.servicePackageRes?.recommendedServicePackage?.servicePackageCaseType
      });
    } else {
      this.populateServicePackage(this.servicePackageDetailForm.get('servicePackageCaseType').value);
    }
    this.cansAssessmentRadio = [
      { label: 'Yes', value: 'Y' },
      { label: 'No', value: 'N' },
    ];
  }

  servicePackageTypeChange() {
    const servicePackageTypeValue = this.servicePackageDetailForm.get('servicePackageType').value;
    if (!servicePackageTypeValue) {
      this.servicePackages = [];
      this.endReasons = [];
      this.servicePackageDetailForm.patchValue({ servicePackage: '' });
    } else {
      this.servicePackages = this.servicePackageRes.servicePackages;
      this.initializeServicePackage();
    }
    this.servicePackageDetailForm.patchValue({ overrideReason: '', startDate: '', endDate: '', endReasonComments: '' });
    this.displayOverrideReason();
    this.populateAddonServices();
    this.displayEndReasons();
    this.displayCansAssessmentQuestion();
    this.displayThirdPartyReviewerSection();
    this.resetCansAssessmentSection();
    this.resetThirdPartyReviewerSection();
  }

  servicePackageChange() {
    this.populateEndDate();
    this.displayOverrideReason();
    this.populateAddonServices();
    this.displayThirdPartyReviewerSection();
    this.resetThirdPartyReviewerSection();
    this.updateAddonServices();
  }

  endDateChange() {
    const servicePackage = this.servicePackageDetailForm.get('servicePackage').value;
    if (this.servicePackageRes.servicePackageEndDates && servicePackage) {
      const endDateCode = this.servicePackageRes.servicePackageEndDates.find(data => data.code === servicePackage);
      this.servicePackageDetailForm.patchValue({ endDateRequired: endDateCode ? true : false });
    }
  }

  displayOverrideReasonOnload() {
    this.hideOverrideReason = this.servicePackageRes?.servicePackage?.overrideReason ? false : true;
    this.hideOverrideReasonComments = !(this.servicePackageRes?.servicePackage?.overrideReason === '80' ||
      this.servicePackageRes?.servicePackage?.overrideReason === '90');
  }

  displayCansAssessmentQuestion() {
    const servicePackageType = this.servicePackageDetailForm.get('servicePackageType').value;
    if (servicePackageType === 'RMD') {
      this.showCansAssessmentQuestion = true;
    } else {
      this.showCansAssessmentQuestion = false;
      this.showCansAssessmentSection = false;
    }
  }

  displayCansSection() {
    const cansCompletedIndicator = this.servicePackageDetailForm.get('cansCompletedIndicator').value;
    if (cansCompletedIndicator === 'Y') {
      this.showCansAssessmentSection = true;
    } else {
      this.showCansAssessmentSection = false;
    }
  }

  displayThirdPartyReviewerSection() {
    const servicePackageType = this.servicePackageDetailForm.get('servicePackageType').value;
    const servicePackage = this.servicePackageDetailForm.get('servicePackage').value;
    if (servicePackageType === 'RMD' && (this.servicePackageRes.dfpsGroServicePackages.some(obj => obj.code === servicePackage) ||
      this.servicePackageRes.ssccGroServicePackages.some(obj => obj.code === servicePackage))) {
      this.showThirdPartyReviewerSection = true;
    } else {
      this.showThirdPartyReviewerSection = false;
    }
  }

  resetCansAssessmentSection() {
    if (!this.showCansAssessmentSection) {
      this.servicePackageDetailForm.patchValue({ cansCompletedIndicator: '', cansNotCompletedReason: '', cansNotCompletedComments: '', cansAssessorName: '' });
    }
  }

  resetThirdPartyReviewerSection() {
    if (!this.showThirdPartyReviewerSection) {
      this.servicePackageDetailForm.patchValue({ thirdPartyReviewerComments: '', qrtpRecommendedIndicator: '', qrtpAssessmentCompletedDate: '' });
    }
  }

  displayOverrideReason() {
    if (this.servicePackageRes.stageType !== 'C-PB') {
      const servicePackageType = this.servicePackageDetailForm.get('servicePackageType').value;
      const servicePackage = this.servicePackageDetailForm.get('servicePackage').value;
      if (!servicePackageType || servicePackageType === 'RMD') {
        this.hideOverrideReason = true;
        this.servicePackageDetailForm.patchValue({ overrideReason: '', overrideReasonRequired: false });
      } else if (servicePackageType === 'SEL' && (this.servicePackageRes?.recommendedServicePackage
        && (!servicePackage || servicePackage === this.servicePackageRes.recommendedServicePackage?.servicePackage))) {
        this.hideOverrideReason = true;
        this.servicePackageDetailForm.patchValue({ overrideReason: '', overrideReasonRequired: false });
      } else {
        this.hideOverrideReason = false;
        this.servicePackageDetailForm.patchValue({ overrideReason: '', overrideReasonRequired: true });
      }
      this.overrideReasonChange();
    }
  }

  overrideReasonChange() {
    const overrideReason = this.servicePackageDetailForm.get('overrideReason').value;
    if (overrideReason && (overrideReason === '80' || overrideReason === '90')) {
      this.hideOverrideReasonComments = false;
    } else {
      this.hideOverrideReasonComments = true;
      this.servicePackageDetailForm.patchValue({ overrideReasonComments: '' });
    }
  }

  displayEndReasons() {
    const servicePackageType = this.servicePackageDetailForm.get('servicePackageType').value;
    if (servicePackageType === 'RMD') {
      this.endReasons = this.servicePackageRes.recommendedPrimaryEndReasons;
    } else if (servicePackageType === 'SEL') {
      this.endReasons = this.servicePackageRes.selectedPrimaryEndReasons;
    }
  }

  cansCompletedIndicatorChange() {
    const cansCompletedIndicator = this.servicePackageDetailForm.get('cansCompletedIndicator').value;
    if (cansCompletedIndicator === 'Y') {
      this.showCansAssessmentSection = true;
      this.servicePackageDetailForm.patchValue({ cansNotCompletedReason: '' });
      this.toggleCansAssessmentSection();
    } else {
      this.showCansAssessmentSection = false;
      this.servicePackageDetailForm.patchValue({ cansAssessmentCompletedDate: '', cansRecommendationComments: '', cansAssessorName: '' });
    }
  }

  cansNotCompletedReasonChange() {
    this.servicePackageDetailForm.patchValue({ cansNotCompletedComments: '' });
  }

  initializeAddonServicesOnload() {
    this.populateAddonServices();
    if (this.servicePackageRes?.servicePackage?.addonServicePackages) {
      this.servicePackageDetailForm.setControl('addonServicePackages',
        this.setExistingAddonPackages(this.servicePackageRes.servicePackage.addonServicePackages));
    }
  }

  populateAddonServices() {
    const servicePackage = this.servicePackageDetailForm.get('servicePackage').value;
    const servicePackageType = this.servicePackageDetailForm.get('servicePackageType').value;
    if (servicePackageType === 'SEL' && servicePackage) {
      this.addonServiceCheckboxFields = this.servicePackageRes.addonServicePackages[servicePackage];
      this.initializeAddonServices();
    } else {
      this.addonServiceCheckboxFields = [];
      this.showAddOnServices = false;
      this.removeAddonServices();
    }
  }

  initializeAddonServices(){
    if (this.addonServiceCheckboxFields) {
      const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
      this.removeAddonServices();
      this.showAddOnServices = true;
      this.addonServiceCheckboxFields.forEach(addOnService => {
        addOnServicesControl.push(this.createAddonServiceForm());
      });
    } else {
      this.showAddOnServices = false;
      this.removeAddonServices();
    }
  }

  updateAddonServices() {
    const servicePackage = this.servicePackageDetailForm.get('servicePackage').value;
    if (servicePackage && this.servicePackageRes?.servicePackage?.addonServicePackages) {
      this.servicePackageDetailForm.setControl('addonServicePackages',
        this.setExistingAddonPackages(this.servicePackageRes.servicePackage.addonServicePackages));
      const addOnServicesControls = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
      const endDate = this.servicePackageDetailForm.get('endDate').value;
      for (const addOnServicesControl of addOnServicesControls.controls) {
        addOnServicesControl.get('isPackageSelected').value ? addOnServicesControl.get('isPackageSelected').disable() : 
                                                                addOnServicesControl.get('isPackageSelected').enable();
        addOnServicesControl.get('endDate').setValue(endDate);
      }
    }
  }

  createAddonServiceForm() {
    return new FormGroup({
      id: new FormControl(0),
      servicePackageCode: new FormControl(''),
      servicePackageDecode: new FormControl(''),
      startDate: new FormControl(''),
      endDate: new FormControl(''),
      endReason: new FormControl(''),
      endReasonComments: new FormControl(''),
      isPackageSelected: new FormControl(false),
      existingEndDate: new FormControl(''),
      endReasonRequired: new FormControl(false),
    })
  }

  // Forms and Narrative methods
  getForms(): FormValue[] {
    return [{
      formName: 'Assessment Request',
      formParams: {
        docType: 'SRVPAR',
        docExists: 'false',
        displayOnly: 'true',
        pStage: this.servicePackageRes.stageId ? String(this.servicePackageRes.stageId) : '',
        pCase: this.servicePackageRes.caseId ? String(this.servicePackageRes.caseId) : '',
        id: this.servicePackageId ? String(this.servicePackageId) : ''
      }
    }
    ];
  }

  getAddonServices(servicePackageDetailForm) {
    return servicePackageDetailForm.controls.addonServicePackages.controls;
  }

  removeAddonServices() {
    const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    addOnServicesControl.controls = [];
  }

  resetAddonServiceFields(index) {
    const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    if (!addOnServicesControl.controls[index].get('isPackageSelected').value) {
      addOnServicesControl.controls[index].reset();
    }
  }

  addOnServiceEndReasonChange(index) {
    const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    const endReason = addOnServicesControl.controls[index].get('endReason').value;
    if (endReason && endReason !== '50') {
      addOnServicesControl.controls[index].get('endReasonComments').setValue('');
    }
  }

  setExistingAddonPackages(addonServices: AddonServicePackage[]): FormArray {
    const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    addonServices.forEach((addonService) => {
      if (this.addonServiceCheckboxFields) {
        const index = this.addonServiceCheckboxFields.findIndex(
          (addonServiceCode => addonServiceCode.code === addonService.servicePackageCode));
        if (index !== -1) {
          addOnServicesControl.controls[index].get('isPackageSelected').setValue(true);
          addOnServicesControl.controls[index].get('id').setValue(addonService.id);
          addOnServicesControl.controls[index].get('servicePackageCode').setValue(addonService.servicePackageCode);
          addOnServicesControl.controls[index].get('servicePackageDecode').setValue(this.addonServiceCheckboxFields[index].decode);
          addOnServicesControl.controls[index].get('startDate').setValue(addonService.startDate);
          addOnServicesControl.controls[index].get('endDate').setValue(addonService.endDate);
          addOnServicesControl.controls[index].get('endReason').setValue(addonService.endReason);
          addOnServicesControl.controls[index].get('endReasonComments').setValue(addonService.endReasonComments);
          addOnServicesControl.controls[index].get('existingEndDate').setValue(addonService.endDate);
          addOnServicesControl.controls[index].get('endReasonRequired').setValue(addonService.endReason ? true : false);
        }
      }
    })
    return addOnServicesControl;
  }

  updateAddonServicePackageCode(index) {
    const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    if (addOnServicesControl.controls[index].get('isPackageSelected').value) {
      addOnServicesControl.controls[index].get('servicePackageCode').setValue(this.addonServiceCheckboxFields[index].code);
      addOnServicesControl.controls[index].get('servicePackageDecode').setValue(this.addonServiceCheckboxFields[index].decode);
    } else {
      addOnServicesControl.controls[index].get('servicePackageCode').setValue('');
      addOnServicesControl.controls[index].get('servicePackageDecode').setValue('');
    }
  }

  autoPopulateAddOnEndDate(index) {
    const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    if (index !== null) {
      this.setAddonEndate(addOnServicesControl.controls[index]);
    } else if (addOnServicesControl.controls && addOnServicesControl.controls.length) {
      addOnServicesControl.controls.forEach((control) => {
        this.setAddonEndate(control)
      });
    }
  }

  setAddonEndate(control: AbstractControl) {
    control.get('startDate').value && DfpsCommonValidators.validateDate(this.servicePackageDetailForm.get('endDate')) == null ?
      control.get('endDate').setValue(this.servicePackageDetailForm.get('endDate').value) : control.get('endDate').setValue('');
  }

  populateEndDate() {
    if (this.servicePackageRes.stageType !== 'C-PB') {
      const servicePackage = this.servicePackageDetailForm.get('servicePackage').value;
      let startDate = this.servicePackageDetailForm.get('startDate').value;
      if (this.servicePackageRes.servicePackageEndDates && (servicePackage && startDate) &&
        DfpsCommonValidators.validateDate(this.servicePackageDetailForm.get('startDate')) == null) {
        startDate = new Date(startDate);
        if (this.servicePackageRes.servicePackageEndDates.find(data => data.code === servicePackage)) {
          this.endDate = formatDate(String(this.calculateEndDate(startDate, servicePackage)), 'MM/dd/yyyy', this.locale);
          this.servicePackageDetailForm.patchValue({ endDate: this.endDate, endReason: '', endDateRequired: true });
        } else {
          this.servicePackageDetailForm.patchValue({ endDate: '', endReason: '', endDateRequired: false });
        }
      } else {
        this.servicePackageDetailForm.patchValue({ endDate: '', endReason: '', endDateRequired: false });
      }
    }
  }

  calculateEndDate(startDate, servicePackage) {
    const endDateCode = this.servicePackageRes.servicePackageEndDates.find(data => data.code === servicePackage);
    if (endDateCode) {
      const firstDay = new Date(startDate.getFullYear(), startDate.getMonth(), 1);
      const tempEndDate = new Date(firstDay.setMonth(firstDay.getMonth() + Number(endDateCode.decode)));
      const calculatedEndDate = new Date(tempEndDate.getFullYear(), tempEndDate.getMonth() + 1, 0)
      return calculatedEndDate.setDate(calculatedEndDate.getDate() + 1);
    }
  }

  isModifiedEndDate(event: Event) {
    if (this.servicePackageRes.stageType !== 'C-PB') {
      const endDateValue = this.servicePackageDetailForm.get('endDate').value;
      const endDateRequired = this.servicePackageDetailForm.get('endDateRequired').value;
      if (this.endDate && event && DfpsCommonValidators.validateDate(this.servicePackageDetailForm.get('endDate')) == null
        && endDateValue !== this.endDate && endDateRequired) {
        this.hideEndReasonAndComments = false;
        this.servicePackageDetailForm.patchValue({ endReasonRequired: true });
      } else {
        this.hideEndReasonAndComments = true;
        this.servicePackageDetailForm.patchValue({ endReasonRequired: false });
      }
      this.displayEndReasons();
      this.validateAutoPopulatedEndDate();
    }
  }

  validateAutoPopulatedEndDate() {
    const endDate = this.servicePackageDetailForm.get('endDate').value;
    if (endDate) {
      const autopopulatedEndDate = this.calculateEndDate(new Date(this.servicePackageDetailForm.get('startDate').value),
        this.servicePackageDetailForm.get('servicePackage').value);
      this.servicePackageDetailForm.patchValue({
        autopopulatedEndDate: formatDate(String(autopopulatedEndDate),
          'MM/dd/yyyy', this.locale)
      });
      if (new Date(endDate) > new Date(autopopulatedEndDate)) {
        this.servicePackageDetailForm.patchValue({ endDateModfied: true });
      } else {
        this.servicePackageDetailForm.patchValue({ endDateModfied: false });
      }
    }
  }

  checkSelectedServicePackageEndDates() {
    const endDateValue = this.servicePackageDetailForm.get('endDate').value ?
      new Date(this.servicePackageDetailForm.get('endDate').value).getTime() : null;
    const existingEndDate = this.servicePackageDetailForm.get('existingEndDate').value ?
      new Date(this.servicePackageDetailForm.get('existingEndDate').value).getTime() : null;
    let isEndDateModified = endDateValue && existingEndDate && endDateValue !== existingEndDate;
    const addOnServicesControls: any = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    addOnServicesControls.getRawValue().forEach((addonService) => {
      if (addonService.isPackageSelected && !isEndDateModified) {
        const endDate = addonService.endDate ? new Date(addonService.endDate).getTime() : '';
        const existingAddonEndDate = addonService.existingEndDate ? new Date(addonService.existingEndDate).getTime() : '';
        isEndDateModified = existingAddonEndDate && endDate !== existingAddonEndDate;
      }
    })
    return isEndDateModified;
  }

  validateSelectedServicePackageEndDate() {
    if (this.checkSelectedServicePackageEndDates()) {
      this.handleModalOnSaveSelectedServicePkg('This may cause an adjustment to a paid invoice. Continue?');
    } else if (this.servicePackageRes.pageMode === 'NEW'  && (!this.servicePackageDetailForm.get('endDate').value ||
      this.servicePackageDetailForm.get('startDate').value !== this.servicePackageDetailForm.get('endDate').value)) {
      this.handleModalOnSaveSelectedServicePkg('Saving this Selected Service Package will end any existing Level of Care designations. Do you want to continue?');
    } else {
      this.saveServicePackage();
    }
  }

  isAddonEndDateModified(event: Event, index) {
    const addOnServicesControl = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
    if (event && DfpsCommonValidators.validateDate(addOnServicesControl.controls[index].get('endDate')) === null &&
      addOnServicesControl.controls[index].get('endDate').value !== this.servicePackageDetailForm.get('endDate').value) {
      addOnServicesControl.controls[index].get('endReasonRequired').setValue(true);
    } else {
      addOnServicesControl.controls[index].get('endReason').setValue('');
      addOnServicesControl.controls[index].get('endReasonRequired').setValue(false);
    }
  }

  handleModalOnSaveSelectedServicePkg(displayMessage) {
    const initialState = {
      title: 'Service Package Detail',
      message: displayMessage,
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      if (result) {
        this.saveServicePackage();
      }
    });
  }

  save() {
    if (this.validateFormGroup(this.servicePackageDetailForm)) {
      const servicePackageType = this.servicePackageDetailForm.get('servicePackageType').value;
      if (servicePackageType === 'RMD') {
        this.saveServicePackage();
      } else {
        this.validateSelectedServicePackageEndDate();
      }
    }
  }

  saveServicePackage() {
    const payload: ServicePackage = Object.assign(this.servicePackageRes.servicePackage || {},
      this.servicePackageDetailForm.getRawValue());
    payload.id = payload.id === undefined || null ? '0' : payload.id;
    payload.creleaseDate = this.servicePackageRes?.creleaseDate;
    this.updateAddonPackages(payload);

    if (payload.servicePackageType === 'SEL' &&
      this.hasPreganentAndParentingAddon(payload) && !this.servicePackageRes.pregnantOrYouthParent) {
      this.showPreganentAndParentingPopUp(payload);
    } else if (payload.servicePackageType === 'SEL' &&
      this.hasPreganentAndParentingTreatmentServices(payload) && !this.servicePackageRes.pregnantOrYouthParent) {
      this.showPregnantAndParentingTreatmentServicesPopUp(payload);
    } else if (payload.servicePackageType === 'SEL' && (this.hasTransitionSupportAddon(payload) &&
      (!this.servicePackageRes.personAge ||
        (this.servicePackageRes.personAge < 14 || this.servicePackageRes.personAge > 22)))) {
      this.showPersonDetailPopUp(payload);
    } else {
      this.caseService.saveServicePackageDetail(payload).subscribe(
        response => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          setTimeout(() => {
            this.caseService.redirectToUrl('case/placement/service-packages');
          }, 2000);
        });
    }
  }

  showPreganentAndParentingPopUp(payload: ServicePackage) {
    const initialState = {
      title: 'Service Package',
      messageList: [
        {
          content: 'Person Detail Page: Person Characteristics of Pregnant-current (Suspected or Diagnosed) or Youth Parent (Suspected) must be selected on Person Detail page, Person Characteristics section before selecting Pregnant and Parenting Add-On Services.',
          url: this.environmentSettings.impactP2WebUrl +
            '/case/personDetail/characteristics?tabValue=&personAge=' +
            this.servicePackageRes.personAge + '&isMPSEnvironment=false'
        }],
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe();
  }

  showPregnantAndParentingTreatmentServicesPopUp(payload: ServicePackage) {
    const initialState = {
      title: 'Service Package',
      messageList: [
        {
          content: 'Person Detail Page: Person Characteristics of Pregnant-current (Suspected or Diagnosed) or Youth Parent (Suspected) must be selected on Person Detail page, Person Characteristics section before selecting Youth & Young Adults Who Are Pregnant and Parenting Service Package.',
          url: this.environmentSettings.impactP2WebUrl +
            '/case/personDetail/characteristics?tabValue=&personAge=' +
            this.servicePackageRes.personAge + '&isMPSEnvironment=false'
        }],
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe();
  }

  showPersonDetailPopUp(payload: ServicePackage) {
    const initialState = {
      title: 'Service Package',
      messageList: [
        {
          content: 'Person Detail Page: Age of the child should be between 14 and 22.',
          url: this.environmentSettings.impactP2WebUrl + '/case/personDetail/detail?isMPSEnvironment=false'
        }],
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe();
  }

  hasPreganentAndParentingAddon(servicePackage: ServicePackage) {
    return (servicePackage.addonServicePackages.filter(sp => sp.servicePackageCode === 'TAF' || sp.servicePackageCode === 'TAC').length > 0);
  }

  hasPreganentAndParentingTreatmentServices(servicePackage: ServicePackage) {
    return (servicePackage.servicePackage === 'T12' || servicePackage.servicePackage === 'T36');
  }

  hasTransitionSupportAddon(servicePackage: ServicePackage) {
    return servicePackage.addonServicePackages.filter(sp => sp.servicePackageCode === 'TAA' ||
      sp.servicePackageCode === 'TAD').length > 0;
  }

  updateAddonPackages(payload) {
    if (payload && payload.addonServicePackages) {
      const selectedAddonServices = [];
      payload.addonServicePackages.forEach((addonPackage) => {
        if (addonPackage.isPackageSelected) {
          selectedAddonServices.push(addonPackage);
        }
      });
      payload.addonServicePackages = selectedAddonServices;
    }
  }

  getPageMode() {
    if (this.servicePackageRes.pageMode === 'NEW') {
      this.isEditMode = true;
      this.toggleCansAssessmentSection();
      this.toggleThirdpartyReviewSection();
    } else if (this.servicePackageRes.pageMode === 'EDIT') {
      this.isEditMode = true;
      this.updatePageInEditMode();
    } else {
      FormUtils.disableFormControlStatus(this.servicePackageDetailForm,
        ['servicePackageCaseType', 'servicePackageType', 'servicePackage', 'overrideReason', 'overrideReasonComments', 'startDate',
          'endDate', 'endReason', 'endReasonComments', 'overrideReasonRequired', 'endDateModifed',
          'cansAssessmentCompletedDate', 'cansRecommendationComments', 'cansCompletedIndicator', 'cansNotCompletedReason', 'cansNotCompletedComments',
          'thirdPartyReviewerComments', 'groRecommendedIndicator', 'groAssessmentCompletedDate', 'cansAssessorName']);
      if (this.showAddOnServices) {
        const addOnServicesControls = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
        for (const addOnServicesControl of addOnServicesControls.controls) {
          addOnServicesControl.disable();
        }
      }
      this.isEditMode = false;
    }
  }

  updatePageInEditMode() {
    FormUtils.disableFormControlStatus(this.servicePackageDetailForm, [ 'servicePackageType', 'cansAssessmentCompletedDate', 'cansRecommendationComments', 'cansCompletedIndicator', 'cansNotCompletedReason', 'cansNotCompletedComments', 'cansAssessorName']);
    if (!this.servicePackageRes.placementFixer && !this.servicePackageRes.fosterCareEligibilitySpecialist) {
      FormUtils.disableFormControlStatus(this.servicePackageDetailForm, ['servicePackageCaseType','servicePackage', 'startDate', 'overrideReason', 'overrideReasonComments', 'thirdPartyReviewerComments', 'groRecommendedIndicator', 'groAssessmentCompletedDate']);
    }
    if (this.servicePackageRes.servicePackage.servicePackageType === 'SEL' && this.servicePackageRes.servicePackage.addonServicePackages) {
      const addOnServicesControls = this.servicePackageDetailForm.get('addonServicePackages') as FormArray;
      for (const addOnServicesControl of addOnServicesControls.controls) {
        if (!this.servicePackageRes.placementFixer && !this.servicePackageRes.fosterCareEligibilitySpecialist) {
          addOnServicesControl.get('startDate').disable();
          addOnServicesControl.get('isPackageSelected').disable();
        } else {
          addOnServicesControl.get('isPackageSelected').value ? addOnServicesControl.get('isPackageSelected').disable() :
          addOnServicesControl.get('isPackageSelected').enable();
        }
      }
    }
  }

  toggleCansAssessmentSection(){
    if(this.servicePackageRes.cansAssessor && this.servicePackageId === 0){
      FormUtils.enableFormControlStatus(this.servicePackageDetailForm, ['cansAssessmentCompletedDate', 'cansRecommendationComments', 'cansAssessorName']);
    } else {
      FormUtils.disableFormControlStatus(this.servicePackageDetailForm, ['cansAssessmentCompletedDate', 'cansRecommendationComments', 'cansAssessorName']);
    }
  }

  toggleThirdpartyReviewSection() {
    if (!this.servicePackageRes?.placementFixer && !this.servicePackageRes.fosterCareEligibilitySpecialist) {
      FormUtils.disableFormControlStatus(this.servicePackageDetailForm, ['thirdPartyReviewerComments', 'groRecommendedIndicator', 'groAssessmentCompletedDate']);
    }
  }

  initializeServicePackageCaseType() {
    const servicePackageCaseType = this.servicePackageDetailForm.get('servicePackageCaseType').value
    this.populateServicePackage(servicePackageCaseType)
  }

  servicePackageCaseTypeChange() {
    const servicePackageCaseType = this.servicePackageDetailForm.get('servicePackageCaseType').value;
    const existingServicePackageCaseType = this.servicePackageRes?.servicePackage?.servicePackageCaseType;
    this.populateServicePackage(servicePackageCaseType)
    this.showAddOnServices = false
    this.removeAddonServices()
    this.updateServicePackage(servicePackageCaseType);
    this.displayThirdPartyReviewerSection();
    this.populateAddonServices();
    if (existingServicePackageCaseType === servicePackageCaseType && this.servicePackageRes.stageType !== 'C-PB') {
      this.servicePackageDetailForm.setControl('addonServicePackages',
        this.setExistingAddonPackages(this.servicePackageRes.servicePackage.addonServicePackages));
    }
  }

  populateServicePackage(servicePackageCaseTypeValue) {
    if (servicePackageCaseTypeValue === 'FPS') {
      this.servicePackages = this.servicePackageRes.dfpsServicePackages
    } else if (servicePackageCaseTypeValue === 'CBC') {
      this.servicePackages = this.servicePackageRes.ssccServicePackages
    } else {
      this.servicePackages = []
    }
  }

  updateServicePackage(servicePackageCaseType) {
    if (servicePackageCaseType === this.servicePackageRes?.recommendedServicePackage?.servicePackageCaseType) {
      this.servicePackageDetailForm.patchValue({
        servicePackage: this.servicePackageRes?.recommendedServicePackage?.servicePackage,
        startDate: '', endDate: ''
      });
    } else {
      this.servicePackageDetailForm.patchValue({ servicePackage: '', startDate: '', endDate: '' })
    }
  }
}
