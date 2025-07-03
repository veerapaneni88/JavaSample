import { Component, HostListener, Inject, LOCALE_ID, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import {
  DataTable,
  DfpsCommonValidators,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck,
  ENVIRONMENT_SETTINGS,
  FormParams,
  FormUtils,
  FormValue,
  NarrativeService,
  NavigationService,
  Reports,
  ReportService, SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieService } from 'ngx-cookie-service';
import { Subscription } from 'rxjs';
import { DisplayHomeInformation } from '../../model/case';
import { CaseService } from '../../service/case.service';
import { FadHomeInformationValidators } from './fad-home-information.validator';
import { dfpsValidatorErrors } from '../../../../../messages/validator-errors';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  selector: 'fad-home-information',
  templateUrl: './fad-home-information.component.html'
})
export class FadHomeInformationComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  displayHomeInformation: DisplayHomeInformation;
  homeInfoForm: FormGroup;
  addressListDatatable: DataTable;
  phoneListDatatable: DataTable;
  addressList = [];
  phoneList = [];
  addressDataTableColumns: any[];
  phoneDataTableColumns: any[];
  tableColumn: any[];
  tableBody: any[];
  selectedAddressData: any = [];
  selectedPhoneData: any = [];
  disableChildCharacteristics = false;
  disableChildesEthnicity = false;
  disableHomeTypes = false;
  disableRelationship = false;
  relationship = [];
  isAddressTypePrimaryMsg: string;
  selectedCharacteristicsData: any[];
  selectedEthinicityData: any[];
  selectedHomeTypeData: any[];
  selectedHomeTypeRelationship: any[];
  isAddress = false;
  isPhone = false;
  isHomeDemo = false;
  closureReasonFilteredData: any;
  isHomeLicenseChange = false;
  reports: any;
  reportSubscription: Subscription;
  formValues: FormValue[];
  childesEthnicity = [];
  childCharacteristics = [];
  bHideSaveButton = false;
  bHideAssignButton = true;
  bHideSaveSubmitButton = false;
  bHideApproveButton = true;
  bDisableOnHold = true;
  bPendingExists = false;
  bHideDocumentButton = false;
  bHideSelectResourceButton = false;
  hideAttentionMessage = false;
  isClosureFieldManadatory = false;
  unFilteredStatusValues: any;
  narrativeSubscription: Subscription;

  infoMessageDisplay = false;
  isEditMode: boolean;
  isCategoryReq = true;
  currentHomeStatus = '010';
  originalHomeStatus: string;
  isRedirectedFromApproval = false;
  isAlert = false;
  disableValidation = false;
  homeStatusUnfiltered: any[];
  bHideAddressAndPhoneButtons = false;
  errorNoDataMessage;

  constructor(private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private caseService: CaseService,
    private searchService: SearchService,
    private modalService: BsModalService,
    private cookieService: CookieService,
    private reportService: ReportService,
    private narrativeService: NarrativeService,
    private helpService: HelpService,
    @Inject(LOCALE_ID) private locale: string,
    @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
  }

  createForm() {
    this.homeInfoForm = this.formBuilder.group(
      {
        category: ['', [Validators.required]],
        status: [''],
        ethnicity: [''],
        language: [''],
        religion: [''],
        annualIncome: ['', [DfpsCommonValidators.validateCurrency(12)]],
        maritalStatus: [''],
        marriageDate: ['', [DfpsCommonValidators.validateDate]],
        sourceOfInquiry: [''],
        respite: [''],
        nonPRSHome: [''],
        nonPRSPCA: [''],
        individualStudy: [''],
        certifyEntity: [''],
        closureReason: [''],
        recommendReopening: [''],
        involuntaryClosure: [''],
        onHold: [''],
        childesEthnicity: [''],
        homeType: [''],
        relationship: [''],
        maxNoChildren: [''],
        transport: [''],
        emergencyPlacement: [''],
        comments: ['', [Validators.maxLength(300)]],
        facilityCapacity: [''],
        childCharacteristics: [''],
        selectedMaleMinYearHomeInterest: [''],
        selectedMaleMinMonthHomeInterest: [''],
        selectedMaleMaxYearHomeInterest: [''],
        selectedMaleMaxMonthHomeInterest: [''],
        selectedFemaleMinYearHomeInterest: [''],
        selectedFemaleMinMonthHomeInterest: [''],
        selectedFemaleMaxYearHomeInterest: [''],
        selectedFemaleMaxMonthHomeInterest: [''],
        selectedMaleMinYear: [''],
        selectedMaleMinMonth: [''],
        selectedMaleMaxYear: [''],
        selectedMaleMaxMonth: [''],
        selectedFemaleMinYear: [''],
        selectedFemaleMinMonth: [''],
        selectedFemaleMaxYear: [''],
        selectedFemaleMaxMonth: [''],
        buttonClicked: [''],
        resourcePhone: [''],
        resourceAddress: [''],
        placements: [''],
        homeStudyDocExists: [false],
        errorMessageForSaveSubmit: [false],
        statusMessage: [''],
        statusMessagePend: [''],
        certifyingEntityMessage: [''],
        ethnicityMessage: [''],
        ethnicityMessageForSave: [''],
        maritalStatusMessage: [''],
        interestAgeMessage: [''],
        relationshipMessage: [''],
        placementsMessage: [''],
        maxNoChildrenMessage: [''],
        closeDate: [''],
        businessAddressMessage: [''],
        approver: [''],
        previousStatus: [''],
        previousCapacity: [''],
        previousCategory: [''],
        locale: [this.locale],
        selectedHomeTypeData: [''],
        selectedHomeTypeRelationship: [''],
        isHomeLicenseValuesChanged: [false],
        licensingMessageForSave: ['']
      }, {
      validators: [
        FadHomeInformationValidators.validateHomeInformation
      ]
    }
    );
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Home Information');
    this.helpService.loadHelp('Case');
    this.route.queryParams.subscribe((param) => {
      this.isRedirectedFromApproval = (param && param.action && 'approval' === param.action);
      this.bHideApproveButton = !this.isRedirectedFromApproval;
    });
    this.intializeScreen();
    localStorage.removeItem('backFrom');
  }

  intializeScreen() {
    this.createForm();
    const resourceId = this.route.snapshot.paramMap.get('resourceId') ? this.route.snapshot.paramMap.get('resourceId') : '0';
    if (Number(resourceId) < 1) {
      this.errorNoDataMessage = dfpsValidatorErrors.MSG_INT_NO_DATA_PASSED_IN;
    } else {
      this.caseService.displayHomeInformation(resourceId).subscribe((response) => {
        this.displayHomeInformation = response;
        if (this.displayHomeInformation) {
          this.getFosterType();
          this.filterHomeTypes();
          this.homeStatusUnfiltered = this.displayHomeInformation.status;
          this.childesEthnicity = this.transformData(this.displayHomeInformation.resource.selectedChildEthnicities);
          this.childCharacteristics = this.transformData(this.displayHomeInformation.resource.selectedCharacteristics);
          this.unFilteredStatusValues = this.displayHomeInformation.status;
          this.closureReasonFilteredData = this.displayHomeInformation.closureReason;
          this.loadFadHomeInfo();
          this.filterClosureReason();
          this.filterStatus();
          this.updateStatusDropDown();
          this.updateDisableFlags();
          this.showInvalidateApprovalPopup();
          this.setOpenSlots();
          this.maritalStatusChange();
        }

        this.populatePullBackData();
        this.reloadReject();

        this.isEditMode = this.displayHomeInformation.pageMode === 'EDIT';
        this.reportSubscription = this.reportService.generateReportEvent.subscribe(data => {
          this.generateReport(data);
        });
        this.reports = this.getReports().map(report => {
          return { ...report, reportParams: JSON.stringify(report.reportParams) };
        });
        this.formValues = this.getForms();
        this.narrativeSubscription = this.narrativeService.generateNarrativeEvent.subscribe(data => {
          this.generateNarrative(data);
        });

        if (this.homeInfoForm.get('status').value !== '040') {
          FormUtils.disableFormControlStatus(this.homeInfoForm, ['onHold']);
        }

        if (this.homeInfoForm.get('onHold').value) {
          this.disableHomeLicenseFields();
          this.disableHomeTypes = true;
          this.disableRelationship = true;
        }

        if (this.isRedirectedFromApproval) {
          this.bHideApproveButton = false;
        } else if (this.displayHomeInformation.pageMode === 'VIEW') {
          this.disableAllFieldsAndButtons();
          this.disableFieldsForClosedFromCaseSummary();
          this.enableViewModeApproveButton();
        }

        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
      });
    }
  }

  showInvalidateApprovalPopup() {
    if (this.displayHomeInformation.showInvalidateApprovalPopup) {
      const initialState = {
        title: 'Home Information',
        message: 'Saving this page will invalidate pending approval. Click OK to continue.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {

        }
      });
    }
  }

  isHomeTypeModified(originalHomeTypes, userSelectedHomeTypes) {
    if (originalHomeTypes && userSelectedHomeTypes) {
      if (originalHomeTypes.length !== userSelectedHomeTypes.length) {
        return false;
      } else {
        originalHomeTypes.forEach(element => {
          if (!userSelectedHomeTypes.includes(element.code)) {
            return false;
          }
        });
      }
    } else {
      return false;
    }
    return true;
  }

  isHomeLicenseValuesChanged(): boolean {
    const resourceInfo = this.displayHomeInformation.resource;
    const formValue = this.homeInfoForm.getRawValue();
    const combinedFAHomeType = [...this.selectedHomeTypeData || [], ...this.selectedHomeTypeRelationship || []];
    const isUserModified = !this.isHomeTypeModified(resourceInfo.selectedHomeTypes, combinedFAHomeType);
    const licenceChange = (resourceInfo &&
      (Number(resourceInfo.facilityCapacity) !== Number(formValue.facilityCapacity) ||
        resourceInfo.selectedMaleMinYear && String(resourceInfo.selectedMaleMinYear) !== String(formValue.selectedMaleMinYear) ||
        resourceInfo.selectedMaleMinMonth && String(resourceInfo.selectedMaleMinMonth) !== String(formValue.selectedMaleMinMonth) ||
        resourceInfo.selectedMaleMaxYear && String(resourceInfo.selectedMaleMaxYear) !== String(formValue.selectedMaleMaxYear) ||
        resourceInfo.selectedMaleMaxMonth && String(resourceInfo.selectedMaleMaxMonth) !== String(formValue.selectedMaleMaxMonth) ||
        resourceInfo.selectedFemaleMinYear && String(resourceInfo.selectedFemaleMinYear) !== String(formValue.selectedFemaleMinYear) ||
        resourceInfo.selectedFemaleMinMonth && String(resourceInfo.selectedFemaleMinMonth) !== String(formValue.selectedFemaleMinMonth) ||
        resourceInfo.selectedFemaleMaxYear && String(resourceInfo.selectedFemaleMaxYear) !== String(formValue.selectedFemaleMaxYear) ||
        resourceInfo.selectedFemaleMaxMonth && String(resourceInfo.selectedFemaleMaxMonth) !== String(formValue.selectedFemaleMaxMonth) ||
        isUserModified));
    this.homeInfoForm.patchValue({ isHomeLicenseValuesChanged: licenceChange });
    return licenceChange;
  }

  transformData(data) {
    return data && data.map((value) => {
      return value && value.code;
    });
  }

  // Resource Search pull back data
  populatePullBackData() {
    const formData = this.searchService.getFormData();
    if (formData) {
      this.homeInfoForm.patchValue(formData);
      this.disableChildesEthnicity = formData.nonPRSPCA;
      this.disableChildCharacteristics = formData.nonPRSPCA;
      this.disableHomeTypes = formData.nonPRSPCA;
      if (formData.nonPRSPCA) {
        this.selectedHomeTypeRelationship = formData.selectedHomeTypeRelationship;
      } else {
        this.childesEthnicity = formData.childesEthnicity;
        this.childCharacteristics = formData.childCharacteristics;
        this.selectedHomeTypeData = formData.selectedHomeTypeData;
      }

      if (this.searchService.getFormContent()) {
        this.displayHomeInformation = this.searchService.getFormContent();
      }
      if (this.searchService.getSelectedResource()) {
        const selectedResourceData = this.searchService.getSelectedResource();
        if (selectedResourceData && !(selectedResourceData.ficitiveCaregiver || selectedResourceData.relativeCaregiver)) {
          const initialState = {
            title: 'Home Information',
            message: 'The selected Resource does not have a designation of Relative or Fictive Kin. Please select a different Resource.',
            showCancel: false,
          };
          const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
          (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
          });
        }
        else if (selectedResourceData && selectedResourceData.isHomeAlreadyLinked) {
          const initialState = {
            title: 'Home Information',
            message: 'The selected Agency Home is already linked to a FAD Home.  Please select another Agency Home.',
            showCancel: false,
          };
          const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
          (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
          });
        } else {
          this.displayHomeInformation.linkedResourceName = selectedResourceData.name;
          this.displayHomeInformation.linkedResourceId = selectedResourceData.resourceId;
        }

        const selectedResource = JSON.parse(localStorage.getItem('resourceSelection'));
        if (selectedResource) {
          this.isHomeDemo = true;
        }
        localStorage.removeItem('resourceSelection');
      }
      this.searchService.setFormData(null);
    }
    this.disableFosterHomeTypes();
  }

  // Initial filter to display checkboxes for HomeType and Foster Parent's Relationship
  filterHomeTypes() {
    const homeTypes = this.displayHomeInformation.homeType;
    if (homeTypes && homeTypes.length) {
      const filterRelTypes = ['Relative', 'Fictive Kin', 'Unrelated'];
      this.relationship = homeTypes.filter(value => {
        if (filterRelTypes.includes(value.decode)) {
          return value
        }
      });
      this.displayHomeInformation.homeType = homeTypes.filter(value => {
        if (!filterRelTypes.includes(value.decode)) {
          return value
        }
      });
    }
  }

  // filter for pre selected values to display checkboxes for HomeType and Foster Parent's Relationship
  getFosterType() {
    const familyRelationValues = ['Relative', 'Fictive Kin', 'Unrelated'];
    if (this.displayHomeInformation.resource.selectedHomeTypes &&
      this.displayHomeInformation.resource.selectedHomeTypes.length) {
      this.selectedHomeTypeData = this.displayHomeInformation.resource.selectedHomeTypes.filter(value => {
        if (value && value.decode) {
          if (!familyRelationValues.includes(value.decode)) {
            return value;
          }
        }
      }).map(value => value.code);
      this.selectedHomeTypeRelationship = this.displayHomeInformation.resource.selectedHomeTypes.filter(value => {
        if (value && value.decode) {
          if (familyRelationValues.includes(value.decode)) {
            return value
          }
        }
      }).map(value => value.code);
    }
  }

  filterStatus() {
    const status = this.homeInfoForm.get('status').value;
    const nonPRSPCA = this.homeInfoForm.get('nonPRSPCA').value;
    const nonPRSHome = this.homeInfoForm.get('nonPRSHome').value;
    let statusFilteredValues: any;
    if (this.displayHomeInformation.status && this.displayHomeInformation.status.length) {
      // If homeStatus is Blank
      if (!status) {
        this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
          if (statusValue.code === '010' || statusValue.code === '020' || statusValue.code === '030') {
            return statusValue;
          }
        })
      } else {
        // If homeStatus is Inquiry
        if (status === '010') {
          this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
            if (statusValue.code === '010' || statusValue.code === '020' || statusValue.code === '030') {
              return statusValue;
            }
          })
        }
        // If homeStatus is Recruit
        else if (status === '020') {
          this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
            if (statusValue.code === '020' || statusValue.code === '030') {
              return statusValue;
            }
          })
        }
        // If homeStatus is Applicant or Pending Approval
        else if (status === '030' || status === '060') {
          this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
            if (statusValue.code === '030' || statusValue.code === '060') {
              return statusValue;
            }
          })
        }
        // If homeStatus is Approved-Active
        else if (status === '040') {
          this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
            if (statusValue.code === '060' || statusValue.code === '040' || statusValue.code === '050') {
              return statusValue;
            }
          })
        }
        // If homeStatus is Approved-Inactive
        else if (status === '050') {
          this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
            if (statusValue.code === '060' || statusValue.code === '050') {
              return statusValue;
            }
          })
          if (nonPRSHome || nonPRSPCA) {
            const statusActive = this.unFilteredStatusValues.find(stausValue => stausValue.code === '040');
            this.displayHomeInformation.status.push(statusActive);
          }
        }
        // If homeStatus is Pending Closure
        else if (status === '080') {
          this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
            if (statusValue.code === '010' || statusValue.code === '020') {
              return statusValue;
            }
          })
        }
        // If homeStatus is Closed
        else if (status === '070') {
          if (nonPRSHome || nonPRSPCA) {
            this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
              if (statusValue.code === '040') {
                return statusValue;
              }
            })
          } else {
            this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
              if (statusValue.code === '010' || statusValue.code === '020' || statusValue.code === '030') {
                return statusValue;
              }
            })
          }
        }
        // If homeStatus is Closed Duplicate
        else if (status === '090') {
          this.displayHomeInformation.status = this.displayHomeInformation.status.filter(statusValue => {
            if (statusValue.code === '090') {
              return statusValue;
            }
          })
        }
        // If home status is Inquiry or Closed or Non Prs or Non Prs PCA AND NOT Pending Approval
        if (status === '010' || status === '070' || nonPRSHome || nonPRSPCA) {
          if (status !== '060') {
            statusFilteredValues = this.unFilteredStatusValues.filter(statusValue => {
              if (statusValue.code === '070') {
                return statusValue;
              }
            })
          }
        }
        // If homeStatus is not Closed Duplicate
        else if (status !== '090') {
          statusFilteredValues = this.unFilteredStatusValues.filter(statusValue => {
            if (statusValue.code === '080') {
              return statusValue;
            }
          })
        }
        if (statusFilteredValues && statusFilteredValues.length > 0) {
          this.displayHomeInformation.status.push(statusFilteredValues[0]);
        }
      }
    }
  }

  updatePendingClosureStatus() {
    const statusPendingClosure = this.displayHomeInformation.status.find(stausValue => stausValue.code === '080');
    if (!statusPendingClosure && this.displayHomeInformation.resource.faHomeStatus === '080') {
      this.homeInfoForm.patchValue({ status: '020' });
      this.disableAllFieldsAndButtons();
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
    }
  }

  loadFadHomeInfo() {
    if (this.displayHomeInformation && this.displayHomeInformation.resource) {
      this.currentHomeStatus = this.displayHomeInformation.resource.faHomeStatus;
      this.originalHomeStatus = this.displayHomeInformation.resource.faHomeStatus;
      this.homeInfoForm.patchValue({ resourcePhone: this.displayHomeInformation.resource.resourcePhone });
      this.homeInfoForm.patchValue({ resourceAddress: this.displayHomeInformation.resource.resourceAddress });
      this.homeInfoForm.patchValue({ placements: this.displayHomeInformation.placementCount });
      this.homeInfoForm.patchValue({ homeStudyDocExists: this.displayHomeInformation.homeStudyDocExists });
      this.homeInfoForm.patchValue({ closeDate: this.displayHomeInformation.resource.closeDate });
      this.homeInfoForm.patchValue({ approver: this.displayHomeInformation.approver });
      this.homeInfoForm.patchValue({ status: this.displayHomeInformation.resource.faHomeStatus });
      this.homeInfoForm.patchValue({ previousStatus: this.displayHomeInformation.resource.faHomeStatus });
      this.homeInfoForm.patchValue({ previousCapacity: this.displayHomeInformation.resource.facilityCapacity });
      this.homeInfoForm.patchValue({ previousCategory: this.displayHomeInformation.resource.category });

      this.homeInfoForm.patchValue({
        category: this.displayHomeInformation.resource.category,
        status: this.displayHomeInformation.resource.faHomeStatus,
        ethnicity: this.displayHomeInformation.resource.ethnicity,
        language: this.displayHomeInformation.resource.language,
        religion: this.displayHomeInformation.resource.religion,
        annualIncome: this.displayHomeInformation.resource.annualIncome
          ? Number(Math.round(this.displayHomeInformation.resource.annualIncome)).toFixed(2) : '0.00',
        maritalStatus: this.displayHomeInformation.resource.maritalStatus,
        marriageDate: this.displayHomeInformation.resource.marriageDate,
        sourceOfInquiry: this.displayHomeInformation.resource.sourceInquiry,
        respite: this.displayHomeInformation.resource.respite,
        nonPRSHome: this.displayHomeInformation.resource.nonPRSHome,
        nonPRSPCA: this.displayHomeInformation.resource.nonPRSPCA,
        individualStudy: this.displayHomeInformation.resource.individualStudy,
        certifyEntity: this.displayHomeInformation.resource.certifyEntity,
        closureReason: this.displayHomeInformation.resource.closureReason,
        recommendReopening: this.displayHomeInformation.resource.recommendReopen,
        involuntaryClosure: this.displayHomeInformation.resource.involuntaryClosure,
        onHold: this.displayHomeInformation.resource.onHold,
        childesEthnicity: this.displayHomeInformation.resource.selectedChildEthnicities,
        selectedHomeTypeData: this.selectedHomeTypeData,
        selectedHomeTypeRelationship: this.selectedHomeTypeRelationship,
        maxNoChildren: this.displayHomeInformation.resource.maxNoChildren
          ? this.displayHomeInformation.resource.maxNoChildren : 0,
        transport: this.displayHomeInformation.resource.transport,
        emergencyPlacement: this.displayHomeInformation.resource.emergencyPlacement,
        comments: this.displayHomeInformation.resource.comments,
        facilityCapacity: this.displayHomeInformation.resource.facilityCapacity
          ? this.displayHomeInformation.resource.facilityCapacity : 0,
        childCharacteristics: this.displayHomeInformation.resource.selectedCharacteristics,
        selectedMaleMinYearHomeInterest: String(this.displayHomeInformation.resource.selectedMaleMinYearHomeInterest),
        selectedMaleMinMonthHomeInterest: String(this.displayHomeInformation.resource.selectedMaleMinMonthHomeInterest),
        selectedMaleMaxYearHomeInterest: String(this.displayHomeInformation.resource.selectedMaleMaxYearHomeInterest),
        selectedMaleMaxMonthHomeInterest: String(this.displayHomeInformation.resource.selectedMaleMaxMonthHomeInterest),
        selectedFemaleMinYearHomeInterest: String(this.displayHomeInformation.resource.selectedFemaleMinYearHomeInterest),
        selectedFemaleMinMonthHomeInterest: String(this.displayHomeInformation.resource.selectedFemaleMinMonthHomeInterest),
        selectedFemaleMaxYearHomeInterest: String(this.displayHomeInformation.resource.selectedFemaleMaxYearHomeInterest),
        selectedFemaleMaxMonthHomeInterest: String(this.displayHomeInformation.resource.selectedFemaleMaxMonthHomeInterest),
        selectedMaleMinYear: String(this.displayHomeInformation.resource.selectedMaleMinYear),
        selectedMaleMinMonth: String(this.displayHomeInformation.resource.selectedMaleMinMonth),
        selectedMaleMaxYear: String(this.displayHomeInformation.resource.selectedMaleMaxYear),
        selectedMaleMaxMonth: String(this.displayHomeInformation.resource.selectedMaleMaxMonth),
        selectedFemaleMinYear: String(this.displayHomeInformation.resource.selectedFemaleMinYear),
        selectedFemaleMinMonth: String(this.displayHomeInformation.resource.selectedFemaleMinMonth),
        selectedFemaleMaxYear: String(this.displayHomeInformation.resource.selectedFemaleMaxYear),
        selectedFemaleMaxMonth: String(this.displayHomeInformation.resource.selectedFemaleMaxMonth),
      });
      this.getAddressListDetails();
      this.getPhoneListDetails();
    }
    if (this.displayHomeInformation.resource.faHomeStatus === '040' && this.displayHomeInformation.resource.status === '02') {
      this.homeInfoForm.patchValue({ onHold: true });
    }
    if (this.displayHomeInformation.resource.nonPRSHome || this.displayHomeInformation.resource.nonPRSPCA) {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
    }
    if ((this.displayHomeInformation.resource.nonPRSHome
      || this.displayHomeInformation.resource.nonPRSPCA)) {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
    } else {
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
    }
  }

  maritalStatusChange() {
    const maritalStatus = this.homeInfoForm.get('maritalStatus').value;
    const status = this.homeInfoForm.get('status').value;
    if (maritalStatus) {
      if (maritalStatus !== '01') {
        this.homeInfoForm.patchValue({
          marriageDate: ''
        });
        FormUtils.disableFormControlStatus(this.homeInfoForm, ['marriageDate']);
      } else {
        FormUtils.enableFormControlStatus(this.homeInfoForm, ['marriageDate']);
      }
    } else {
      this.homeInfoForm.patchValue({
        marriageDate: ''
      });
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['marriageDate']);
    }
    if (maritalStatus !== '01' && status === '060') {
      this.homeInfoForm.patchValue({ marriageDate: '' });
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['marriageDate']);
    }
    if(this.currentHomeStatus === '080' && this.displayHomeInformation.eventStatus !== 'PEND' 
    && maritalStatus !== '01'){
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['marriageDate']);
    }
  }

  updateStatusFilterDropdown(requiredValue) {
    const filteredValue = this.unFilteredStatusValues.filter(statusValue => {
      if (statusValue.code === requiredValue) {
        return statusValue;
      }
    })
    this.displayHomeInformation.status = filteredValue;
  }

  nonFpsAdoptiveHomeChange() {
    const nonPRSHomeFormValue = this.homeInfoForm.get('nonPRSHome').value;
    const nonPRSPCAFormValue = this.homeInfoForm.get('nonPRSPCA').value;
    const status = this.homeInfoForm.get('status').value;
    if (this.displayHomeInformation.resource.nonPRSHome) {
      if (!nonPRSHomeFormValue) {
        const initialState = {
          title: 'Home Information',
          message: 'If you save the Home it will become a FPS Home. Continue?',
          showCancel: true
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md modal-dialog-centered',
          initialState
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
          if (result) {
            this.updateStatusFilterDropdown('010');
            this.homeInfoForm.patchValue({ status: '010' });
            this.homeInfoForm.patchValue({ category: this.displayHomeInformation.resource.category });
            this.homeInfoForm.get('certifyEntity').setValue(null);
            FormUtils.disableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
            this.bHideSaveSubmitButton = true;
            this.disableFosterHomeTypes();
          } else {
            this.homeInfoForm.removeControl('nonPRSHome');
            this.homeInfoForm.addControl('nonPRSHome', new FormControl(true));
          }
        });
      } else {
        this.updateStatusFilterDropdown('040');
        this.homeInfoForm.patchValue({ status: '040' });
        this.homeInfoForm.patchValue({ category: 'A' });
        this.bHideSaveSubmitButton = false;
        FormUtils.enableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
        if (nonPRSPCAFormValue) {
         this.homeInfoForm.patchValue({nonPRSPCA: false});
      }
      }
    } else {
      if (nonPRSHomeFormValue) {
        this.updateStatusFilterDropdown('040');
        this.homeInfoForm.patchValue({ status: '040' });
        this.homeInfoForm.patchValue({ category: 'A' });
        this.bHideSaveSubmitButton = false;
        this.selectedHomeTypeData = [];
        this.selectedHomeTypeRelationship = [];
        this.homeInfoForm.patchValue({ selectedHomeTypeData: [] });
        this.homeInfoForm.patchValue({ selectedHomeTypeRelationship: [] });
        this.disableHomeTypes = true;
        this.disableRelationship = true;
        FormUtils.enableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
        if (this.homeInfoForm.get('status').value === '040'
          && this.disableChildesEthnicity && this.disableChildCharacteristics) {
          this.enableHomeInterestFields();
          this.childesEthnicity = [];
          this.homeInfoForm.patchValue({ childesEthnicity: this.childesEthnicity });
          this.childCharacteristics = [];
          this.homeInfoForm.patchValue({ childCharacteristics: this.childCharacteristics });
          this.enableHomeLicenseFields();
          FormUtils.enableFormControlStatus(this.homeInfoForm, ['onHold']);
          this.disableFosterHomeTypes();
        }
        if (nonPRSPCAFormValue) {
            this.homeInfoForm.patchValue({nonPRSPCA: false});
        }
      } else {
        this.updateStatusFilterDropdown('010');
        this.homeInfoForm.patchValue({ status: '010' });
        this.homeInfoForm.patchValue({ category: this.displayHomeInformation.resource.category });
        this.homeInfoForm.get('certifyEntity').setValue(null);
        FormUtils.disableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
        this.bHideSaveSubmitButton = true;
        this.disableHomeTypes = false;
        this.disableRelationship = false;
      }
    }
  }

  nonFpsPcaHomeChange() {
    const nonPRSPCAFormValue = this.homeInfoForm.get('nonPRSPCA').value;
    const nonPRSHomeValue = this.homeInfoForm.get('nonPRSHome').value;
    if (nonPRSPCAFormValue) {
      this.homeInfoForm.patchValue({ nonPRSHome: false });
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
      this.updateStatusFilterDropdown('040');
      this.homeInfoForm.patchValue({ status: '040' });
      this.disableHomeInterestFields();
      this.disableHomeLicenseFields();
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['onHold']);
    }
    else {
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
      this.homeInfoForm.get('certifyEntity').setValue(null);
      this.enableHomeInterestFields();
      this.enableHomeLicenseFields();
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['onHold']);
      if (!nonPRSPCAFormValue && this.displayHomeInformation.resource.nonPRSPCA && !nonPRSHomeValue) {
        const initialState = {
          title: 'Home Information',
          message: 'If you save the Home it will become a FPS Home. Continue?',
          showCancel: true
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md modal-dialog-centered',
          initialState
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
          if (result) {
            this.updateStatusFilterDropdown('010');
            this.homeInfoForm.patchValue({ status: '010' });
            this.homeInfoForm.patchValue({ category: this.displayHomeInformation.resource.category });
          } else {
               this.homeInfoForm.patchValue({nonPRSPCA: false});
          }
        });
      }
      else {
        if (!nonPRSHomeValue) {
          this.updateStatusFilterDropdown('010');
          this.homeInfoForm.patchValue({ status: '010' });
          this.homeInfoForm.patchValue({ category: this.displayHomeInformation.resource.category });
        }
      }
    }
    this.disableFosterHomeTypes();
  }

  onHoldChange() {
    if (!this.homeInfoForm.get('onHold').disabled) {
      const onHoldValue = this.homeInfoForm.get('onHold').value;
      const category = this.homeInfoForm.get('category').value;
      if (this.isHomeLicenseValuesChanged()) {
        const initialState = {
          title: 'Home Information',
          message: 'Get approval for licensing changes before  performing this action.',
          showCancel: false
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
          class: 'modal-md modal-dialog-centered',
          initialState
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
          if (result) {
            this.homeInfoForm.patchValue({ onHold: false });
          }
        });
      }
      // Todo need to verify further 
      // else {
      //   if (value && !this.isHomeLicenseValuesChanged()) {
      //     const initialState = {
      //       title: 'Home Information',
      //       message: 'You must uncheck the On Hold Box to make licensing changes.',
      //       showCancel: false
      //     };
      //     const modal = this.modalService.show(DfpsConfirmComponent, {
      //       class: 'modal-md modal-dialog-centered',
      //       initialState
      //     });
      //     (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
      //       if (result) {
      //         this.disableHomeLicenseFields();
      //       }
      //     });
      //   } else {
      //     if (!value) {
      //       this.enableHomeLicenseFields();
      //     }
      //   }
      // }

      if (onHoldValue) {
        this.disableHomeLicenseFields();
      } else {
        this.enableHomeLicenseFields();
        if (category === 'A' || category === 'O') {
          this.disableHomeTypes = true;
          this.disableRelationship = true;
        }
      }
    }
  }

  setOpenSlots() {
    if (this.displayHomeInformation && this.displayHomeInformation.resource) {
      const facilityCapacity = this.homeInfoForm.get('facilityCapacity').value || 0;
      const placement = this.displayHomeInformation.placementCount || 0;
      let openSlotsCount = (facilityCapacity - placement);
      if (openSlotsCount < 0) {
        openSlotsCount = 0;
      }
      this.displayHomeInformation.resource.openSlots = openSlotsCount;
    }
  }

  getAddressListDetails() {
    this.tableColumn = [
      {
        field: 'addressType',
        header: 'Type',
        width: 30,
        isLink: true,
        url: '/case/home-information/FAD/:resourceId/address/:resourceAddressId',
        urlParams: ['resourceId', 'resourceAddressId'],
      },
      { field: 'vendorId', header: 'Vendor ID', isHidden: false, isLink: false, width: 50 },
      { field: 'attention', header: 'Attention', isHidden: false, isLink: false, width: 50 },
      { field: 'address', header: 'Address', isHidden: false, isLink: false, width: 100 },
      { field: 'countyName', header: 'County', isHidden: false, isLink: false, width: 50 },
      { field: 'comments', header: 'Comments', isHidden: false, isLink: false, isTick: true, width: 50 },
    ];
    this.addressListDatatable = {
      tableColumn: this.tableColumn,
      isSingleSelect: this.isRedirectedFromApproval
        || !(this.currentHomeStatus === '080'
          || this.currentHomeStatus === '070'
          || this.displayHomeInformation.pageMode === 'VIEW')
        || (this.currentHomeStatus === '080' && this.displayHomeInformation.rejectClosure),
      isPaginator: false,
    };
    if (this.displayHomeInformation && this.displayHomeInformation.resource.resourceAddress) {
      this.addressListDatatable.tableBody = this.displayHomeInformation.resource.resourceAddress;
      if (this.addressListDatatable.tableBody && this.addressListDatatable.tableBody.length) {
        this.addressListDatatable.tableBody.forEach(addressData => {
          addressData.address = `${addressData.addressLine1 || ''} ${addressData.addressLine2 || ''}
          ${addressData.city || ''} ${addressData.state || ''}, ${addressData.zip || ''}`
        })
      }
    }
    const localDataAddress = JSON.parse(localStorage.getItem('addressList'));
    if (localDataAddress && localDataAddress.focusItem) {
      this.isAddress = true;
      setTimeout(() => {
        const element: any = document.querySelector(`[href$='${localDataAddress.focusItem}']`);
        if (element) {
          element.focus();
          const copyLocalData = { ...localDataAddress };
          copyLocalData.focusItem = '';
          localStorage.setItem('addressList', JSON.stringify(copyLocalData));
        }
      }, 1000)
    }
    const addressDetail = JSON.parse(localStorage.getItem('addressDetailSave'));
    if (addressDetail) {
      this.isAddress = true;
    }
    localStorage.removeItem('addressDetailSave');
  }

  getPhoneListDetails() {
    this.tableColumn = [
      {
        field: 'phoneType',
        header: 'Type',
        width: 50,
        isLink: true,
        url: '/case/home-information/FAD/:resourceId/phone/:resourcePhoneId',
        urlParams: ['resourceId', 'resourcePhoneId'],
      },
      { field: 'phoneNumber', header: 'Phone', isHidden: false, isLink: false, width: 50 },
      { field: 'phoneExtension', header: 'Ext', isHidden: false, isLink: false, width: 50 },
      { field: 'comments', header: 'Comments', isHidden: false, isLink: false, isTick: true, width: 50 },
    ];
    this.phoneListDatatable = {
      tableColumn: this.tableColumn,
      isSingleSelect: this.isRedirectedFromApproval
        || !(this.currentHomeStatus === '080'
          || this.currentHomeStatus === '070'
          || this.displayHomeInformation.pageMode === 'VIEW')
        || (this.currentHomeStatus === '080' && this.displayHomeInformation.rejectClosure),
      isPaginator: false,
    };
    if (this.displayHomeInformation && this.displayHomeInformation.resource.resourcePhone) {
      this.phoneListDatatable.tableBody = this.displayHomeInformation.resource.resourcePhone;
    }

    if (this.displayHomeInformation && this.displayHomeInformation.resource.resourcePhone) {
      this.phoneListDatatable.tableBody = this.displayHomeInformation.resource.resourcePhone;
      if (this.phoneListDatatable.tableBody && this.phoneListDatatable.tableBody.length) {
        this.phoneListDatatable.tableBody.forEach(phoneData => {
          phoneData.phoneNumber = FormUtils.formatPhoneNumber(phoneData.phoneNumber);
        })
      }
    }

    const localDataPhone = JSON.parse(localStorage.getItem('phoneList'));
    if (localDataPhone && localDataPhone.focusItem) {
      this.isPhone = true;
      setTimeout(() => {
        const element: any = document.querySelector(`[href$='${localDataPhone.focusItem}']`);
        if (element) {
          element.focus();
          const copyLocalData = { ...localDataPhone };
          copyLocalData.focusItem = '';
          localStorage.setItem('phoneList', JSON.stringify(copyLocalData));
        }
      }, 1000)
    }
    const phoneDetial = JSON.parse(localStorage.getItem('phoneDetailSave'));
    if (phoneDetial) {
      this.isPhone = true;
    }
    localStorage.removeItem('phoneDetailSave');
  }

  disableHomeDemographicsFields() {
    const nonPRSHome = 'nonPRSHome';
    const nonPRSPCA = 'nonPRSPCA';
    FormUtils.disableFormControlStatus(this.homeInfoForm,
      ['ethnicity', 'language', 'religion', 'annualIncome', 'maritalStatus', 'marriageDate',
        'sourceOfInquiry', 'respite', 'individualStudy']);
    this.homeInfoForm.controls[nonPRSHome].disable({ onlySelf: true, emitEvent: false });
    this.homeInfoForm.controls[nonPRSPCA].disable({ onlySelf: true, emitEvent: false });
  }

  enableHomeDemographicsFields() {
    const nonPRSHome = 'nonPRSHome';
    const nonPRSPCA = 'nonPRSPCA';
    FormUtils.enableFormControlStatus(this.homeInfoForm,
      ['ethnicity', 'language', 'religion', 'annualIncome', 'maritalStatus', 'marriageDate',
        'sourceOfInquiry', 'respite', 'individualStudy']);
    this.homeInfoForm.controls[nonPRSHome].enable({ onlySelf: true, emitEvent: false });
    this.homeInfoForm.controls[nonPRSPCA].enable({ onlySelf: true, emitEvent: false });
  }

  disableHomeInterestFields() {
    FormUtils.disableFormControlStatus(this.homeInfoForm,
      ['selectedMaleMinYearHomeInterest', 'selectedMaleMinMonthHomeInterest', 'selectedMaleMaxYearHomeInterest',
        'selectedMaleMaxMonthHomeInterest', 'selectedFemaleMinYearHomeInterest', 'selectedFemaleMinMonthHomeInterest',
        'selectedFemaleMaxYearHomeInterest', 'selectedFemaleMaxMonthHomeInterest', 'maxNoChildren',
        'transport', 'emergencyPlacement', 'comments'
      ]);
    this.disableChildCharacteristics = true;
    this.disableChildesEthnicity = true;
  }

  enableHomeInterestFields() {
    FormUtils.enableFormControlStatus(this.homeInfoForm,
      ['selectedMaleMinYearHomeInterest', 'selectedMaleMinMonthHomeInterest', 'selectedMaleMaxYearHomeInterest',
        'selectedMaleMaxMonthHomeInterest', 'selectedFemaleMinYearHomeInterest', 'selectedFemaleMinMonthHomeInterest',
        'selectedFemaleMaxYearHomeInterest', 'selectedFemaleMaxMonthHomeInterest', 'maxNoChildren',
        'transport', 'emergencyPlacement', 'comments'
      ]);
    this.disableChildCharacteristics = false;
    this.disableChildesEthnicity = false;
  }

  disableHomeLicenseFields() {
    FormUtils.disableFormControlStatus(this.homeInfoForm,
      ['facilityCapacity', 'selectedMaleMinYear', 'selectedMaleMinMonth', 'selectedMaleMaxYear',
        'selectedMaleMaxMonth', 'selectedFemaleMinYear', 'selectedFemaleMinMonth', 'selectedFemaleMaxYear',
        'selectedFemaleMaxMonth'
      ]);
    this.disableHomeTypes = true;
    this.disableRelationship = true;
  }

  enableHomeLicenseFields() {
    FormUtils.enableFormControlStatus(this.homeInfoForm,
      ['facilityCapacity', 'selectedMaleMinYear', 'selectedMaleMinMonth', 'selectedMaleMaxYear',
        'selectedMaleMaxMonth', 'selectedFemaleMinYear', 'selectedFemaleMinMonth', 'selectedFemaleMaxYear',
        'selectedFemaleMaxMonth'
      ]);
    this.disableHomeTypes = false;
    this.disableRelationship = false;
  }

  disableAllFieldsAndButtons() {
    this.isCategoryReq = false;
    FormUtils.disableFormControlStatus(this.homeInfoForm, ['category', 'status']);
    this.disableHomeDemographicsFields();
    this.disableAddressAndPhoneLinks();
    this.disableHomeInterestFields();
    this.disableHomeLicenseFields();
    FormUtils.disableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening',
      'involuntaryClosure', 'certifyEntity']);
    this.bHideApproveButton = true;
    this.bHideAssignButton = true;
    this.bHideSaveButton = true;
    this.bHideSaveSubmitButton = true;
    this.bHideAddressAndPhoneButtons = true;
  }

  enableAllFieldsAndButtons() {
    this.isCategoryReq = true;
    FormUtils.enableFormControlStatus(this.homeInfoForm, ['category', 'status']);
    this.enableHomeDemographicsFields();
    this.enableHomeInterestFields();
    this.enableHomeLicenseFields();
    FormUtils.enableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening', 'involuntaryClosure']);
    this.bHideApproveButton = false;
    this.bHideAssignButton = false;
    this.bHideSaveButton = false;
    this.bHideSaveSubmitButton = false;
    this.bHideAddressAndPhoneButtons = false;
    this.bDisableOnHold = false;
    this.bHideDocumentButton = false;
  }

  updateFosterHomeTypes() {
    if (this.disableHomeTypes) {
      this.displayHomeInformation.resource.faHomeType1 = null;
      this.displayHomeInformation.resource.faHomeType2 = null;
      this.displayHomeInformation.resource.faHomeType3 = null;
      this.displayHomeInformation.resource.faHomeType4 = null;
      this.displayHomeInformation.resource.faHomeType5 = null;
      this.displayHomeInformation.resource.faHomeType6 = null;
      this.displayHomeInformation.resource.faHomeType7 = null;
      this.displayHomeInformation.resource.faHomeType8 = null;
    }
  }

  disableFosterHomeTypes() {
    const category = this.homeInfoForm.get('category').value;
    const nonPRSPCA = this.homeInfoForm.get('nonPRSPCA').value;

    if (category === 'A' || category === 'O') {
      this.disableHomeTypes = true;
      this.disableRelationship = true;
      this.selectedHomeTypeData = [];
      this.selectedHomeTypeRelationship = [];
      this.homeInfoForm.patchValue({ selectedHomeTypeData: [] });
      this.homeInfoForm.patchValue({ selectedHomeTypeRelationship: [] });
    } else {
      this.disableHomeTypes = false;
      this.disableRelationship = false;
    }

    if (nonPRSPCA && !['A', 'O'].includes(category)) {
      this.disableHomeTypes = true;
      this.disableRelationship = false;
      this.selectedHomeTypeData = [];
      this.homeInfoForm.patchValue({ selectedHomeTypeData: this.selectedHomeTypeData });
    }
  }

  disableHomeInterestAndLicense() {
    if (this.displayHomeInformation.resource.nonPRSPCA) {
      this.disableHomeInterestFields();
      this.disableHomeLicenseFields();
      this.disableRelationship = false;
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['onHold']);
    }
  }

  // filter closure reason based on home status
  filterClosureReason() {
    let filterdClosureData;
    const status = this.homeInfoForm.get('status').value;
    if (status && status === '010' || status === '020' || status === '030') {
      if (this.closureReasonFilteredData) {
        filterdClosureData = this.closureReasonFilteredData
          .filter((data: any) => {
            return data.codeType === 'CVRFCLOS';
          })
      }
    } else if (status && status === '070' || status === '090' || status === '080') {
      if (this.closureReasonFilteredData) {
        filterdClosureData = this.closureReasonFilteredData
          .filter((data: any) => {
            return data.codeType === 'CHMCLOSE' && !['004', '016', '064', '076'].includes(data.code);
          })
      }
    } else {
      if (this.closureReasonFilteredData) {
        filterdClosureData = this.closureReasonFilteredData
          .filter((data: any) => {
            return data.codeType === 'CFACLOSE' && !['089'].includes(data.code);
          })
      }
    }
    this.displayHomeInformation.closureReason = filterdClosureData
      .filter(data => {
        return !['080', '081', '082', '083', '084', '085', '087', '088', '090', '091', '092'].includes(data.code);
      });
  }

  statusChange() {
    this.updateStatusDropDown();
    const onHoldValue = this.homeInfoForm.get('onHold').value;
    if (onHoldValue) {
      const initialState = {
        title: 'Home Information',
        message: 'Status cannot be changed while \"On Hold\" is checked in Home License.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.homeInfoForm.patchValue({ status: this.displayHomeInformation.resource.faHomeStatus })
        }
      });
    } else {
      const status = this.homeInfoForm.get('status').value;
      if (status && status === '040') {
        FormUtils.enableFormControlStatus(this.homeInfoForm, ['onHold']);
      } else {
        FormUtils.disableFormControlStatus(this.homeInfoForm, ['onHold'])
      }
    }
  }

  selectedAddress(event) {
    this.selectedAddressData = [];
    this.selectedAddressData = event;
  }

  selectedPhone(event) {
    this.selectedPhoneData = [];
    this.selectedPhoneData = event;
  }

  selectedChildEthnicityValues(event) {
    this.selectedEthinicityData = [...event];
    this.homeInfoForm.patchValue({ childesEthnicity: this.selectedEthinicityData });
  }

  selectedChildCharacteristicsValues(event) {
    this.selectedCharacteristicsData = [...event];
    this.homeInfoForm.patchValue({ childCharacteristics: this.selectedCharacteristicsData });
  }

  selectedHomeTypeValues(event) {
    this.selectedHomeTypeData = [...event];
    this.homeInfoForm.patchValue({ selectedHomeTypeData: this.selectedHomeTypeData })
  }

  selectedHomeTypeRelationshipValues(event) {
    this.selectedHomeTypeRelationship = [...event];
    this.homeInfoForm.patchValue({ selectedHomeTypeRelationship: this.selectedHomeTypeRelationship });
  }

  deleteAddress() {
    if (this.selectedAddressData.length <= 0) {
      const initialState = {
        title: 'Address List',
        message: 'Please select at least one row to perform this action.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    } else if (this.selectedAddressData && this.selectedAddressData.addressType === 'Primary') {
      const initialState = {
        title: 'Address List',
        message: 'A primary address record may not be deleted.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    } else if (this.selectedAddressData && this.selectedAddressData.vendorId) {
      const initialState = {
        title: 'Address List',
        message: 'An address record with a Vendor ID may not be deleted.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    } else {
      const initialState = {
        title: 'Address List',
        message: 'Are you sure you want to delete the information?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.caseService
            .deleteAddressList(this.selectedAddressData.resourceId, this.selectedAddressData.resourceAddressId)
            .subscribe((res) => {
              this.intializeScreen();
              this.selectedAddressData = [];
            });
        }
      });
    }
  }

  deletePhone() {
    if (this.selectedPhoneData.length <= 0) {
      const initialState = {
        title: 'Phone List',
        message: 'Please select at least one row to perform this action.',
        showCancel: false
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(result => { });
    } else if (this.selectedPhoneData && this.selectedPhoneData.phoneType === 'Primary') {
      const initialState = {
        title: 'Phone List',
        message: 'A primary phone record may not be deleted.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    } else {
      const initialState = {
        title: 'Phone List',
        message: 'Are you sure you want to delete the information?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.caseService
            .deletePhoneList(this.selectedPhoneData.resourceId, this.selectedPhoneData.resourcePhoneId)
            .subscribe((res) => {
              this.intializeScreen();
              this.selectedPhoneData = [];
            });
        }
      });
    }
  }

  addAddress() {
    const headerUrl = 'case/home-information/FAD/' + this.displayHomeInformation.resource.resourceId + '/address/0';
    this.router.navigate([headerUrl]);
  }

  addPhone() {
    const headerUrl = 'case/home-information/FAD/' + this.displayHomeInformation.resource.resourceId + '/phone/0';
    this.router.navigate([headerUrl]);
  }

  categoryChange() {
    const onHoldValue = this.homeInfoForm.get('onHold').value;
    if (onHoldValue) {
      const initialState = {
        title: 'Home Information',
        message: 'Category cannot be changed while \"On Hold\" is checked in Home License.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.homeInfoForm.patchValue({ category: this.displayHomeInformation.resource.category });
        }
      });
    }
    this.disableFosterHomeTypes();
  }

  helpIcon() {
    const initialState = {
      title: 'Home Information',
      messageList: [{
        content: `'Relative' means a person and a child in DFPS conservatorship are related by blood or
      adoption (consanguinity) or marriage (affinity).  This term excludes the child's legal, birth,
      or adoptive parent(s).`, url: ''
      },
      {
        content: `'Fictive kin' means a person and a child in DFPS conservatorship (or the child's family)
      have a longstanding and significant relationship with each other.  This term excludes the child's legal,
      birth, or adoptive parent(s).  Examples include a godparent or someone considered to be an aunt or uncle,
      even though the person is not related to the child.`, url: ''
      },
      {
        content: `'Unrelated' means the relationship of a person and a child in DFPS conservatorship does not meet the criteria
      of 'relative' or 'fictive kin'.  This term excludes the child's legal, birth, or adoptive parent(s).`, url: ''
      }],
      showCancel: false,
    };
    this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg modal-dialog-centered', initialState });
  }

  selectResource() {
    let returnUrl: string;
    returnUrl = '/case/home-information/FAD/' + this.displayHomeInformation.resource.resourceId;
    this.homeInfoForm.patchValue({ childesEthnicity: this.selectedEthinicityData });
    this.homeInfoForm.patchValue({ childCharacteristics: this.selectedCharacteristicsData });
    this.homeInfoForm.patchValue({ selectedHomeTypeData: this.selectedHomeTypeData });
    this.homeInfoForm.patchValue({ selectedHomeTypeRelationship: this.selectedHomeTypeRelationship });
    this.searchService.setFormData(this.homeInfoForm.value);
    this.searchService.setFormContent(this.displayHomeInformation);
    this.searchService.setReturnUrl(returnUrl);
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
    this.router.navigate(
      [
        '/case/home-information/FAD/' + this.displayHomeInformation.resource.resourceId + '/resourcesearch',
        { source: 'non-sscc' },
      ],
      { skipLocationChange: false }
    );
  }

  checkMaritalStatus() {
    const maritalStatus = this.homeInfoForm.get('maritalStatus').value;
    const marriageDate = this.homeInfoForm.get('marriageDate').value;
    if (!this.isAlert && maritalStatus !== '01' && marriageDate) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        message: 'Since Marital Status is no longer \'Married\', the Marriage Date will be cleared upon Save.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.homeInfoForm.patchValue({ marriageDate: null });
          this.saveHomeInformation(false, false);
        }
      });
    }
  }

  reloadReject() {
    if (this.displayHomeInformation.rejectClosure) {
      this.homeInfoForm.get('closureReason').setValue('');
      this.homeInfoForm.get('recommendReopening').setValue('');
      this.homeInfoForm.get('involuntaryClosure').setValue('');
      const initialState = {
        title: 'Home Information',
        message: 'Closure of home is not approved. Do you still wish to close the home?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result) {
          FormUtils.enableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening', 'involuntaryClosure']);
          this.bHideSaveSubmitButton = false;
          this.bHideSaveButton = false;
        } else {
          this.pendingClosure();
        }
      });
    }
  }

  pendingClosure() {
    const initialState = {
      title: 'Home Information',
      message: `Home is pending closure.
        If you no longer wish to close the home, select a new status for the home and save the page.`,
      showCancel: false,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['status']);
      this.homeInfoForm.get('closureReason').setValue('');
      this.displayHomeInformation.status = [{
        code: '020',
        decode: 'Recruit'
      }, {
        code: '030',
        decode: 'Applicant'
      }];
      this.bHideSaveButton = false;
      this.bHideSaveSubmitButton = true;
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening', 'involuntaryClosure']);
      //artf238305: 80007 - Enabling fields when status is in pending closure and rejected.
      this.disableValidation = false;
      this.bHideDocumentButton = true;
      this.homeInfoForm.patchValue({ status: '020' });
      this.homeInfoForm.patchValue({ closureReason: '' });
      this.displayHomeInformation.resource.faHomeStatus = '020';
      this.displayHomeInformation.resource.closureReason = null;
    });
  }

  updateStatusDropDown() {
    const statusValue = this.homeInfoForm.get('status').value;
    if (statusValue) {
      this.currentHomeStatus = statusValue;
    } else {
      this.currentHomeStatus = this.displayHomeInformation.resource ? this.displayHomeInformation.resource.faHomeStatus : '';
    }

    if (statusValue === '010' && this.currentHomeStatus !== '010') {
      const statusInquiry = this.displayHomeInformation.status.find(stausValue => stausValue.code === '010');
      this.displayHomeInformation.status.push(statusInquiry);
    }

    this.displayHomeInformation.status.forEach(status => {
      if (status.code === '060' || status.code === '080') {
        this.bPendingExists = true;
      }
    })
    this.bHideSaveSubmitButton = !this.bPendingExists;
  }

  updateDisableFlags() {
    this.bHideDocumentButton = !this.displayHomeInformation.homeStudyDocExists;
    // Enable/Disable fields for different home status
    this.disbaleFieldsForClosed();
    this.disableFieldsForClosedDuplicate();
    this.disableFieldsForPendingClosure();
    this.disbaleFieldsForPendingApproval();
    this.disableFormFieldsForApprovedActive();
    this.disableFieldsForApprovedInactiveAndApproved();
    this.diableFieldsFromApprovalPage();
    this.disableFosterHomeTypes();
    this.disableHomeInterestAndLicense();
    this.disableFieldsForClosedFromCaseSummary();
    this.updatePendingClosureStatus();
  }

  diableFieldsFromApprovalPage() {
    if (this.isRedirectedFromApproval && this.displayHomeInformation.approvalFlag) {
      this.enableAllFieldsAndButtons();
      this.bHideAssignButton = true;
      this.bHideSaveSubmitButton = true;
      this.enableAddressAndPhoneLinks();
      if (this.currentHomeStatus === '080') {
        FormUtils.disableFormControlStatus(this.homeInfoForm, ['status']);
      }
    }
  }

  disableFieldsForPendingClosure() {
 	//artf238305: 80007 - Enabling fields when status is in pending closure and rejected.
    if (this.currentHomeStatus === '080') {
      if (!this.homeInfoForm.invalid && !this.displayHomeInformation.rejectClosure) {
        this.disableAllFieldsAndButtons();
      }
      this.bHideSaveButton = false;
      this.bHideSaveSubmitButton = false;
      this.bHideApproveButton = !this.displayHomeInformation.submittedForApproval;

      FormUtils.enableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening', 'involuntaryClosure']);
    }
  }

  disbaleFieldsForPendingApproval() {
    if (this.currentHomeStatus === '060') {
      this.enableAllFieldsAndButtons();
      this.bHideApproveButton = !this.displayHomeInformation.submittedForApproval;
      this.bHideAssignButton = true;
      this.bDisableOnHold = true;
      if (this.displayHomeInformation.eventStatus === 'PEND') {
        this.disableAllFieldsAndButtons();
        FormUtils.enableFormControlStatus(this.homeInfoForm, ['category', 'status', 'involuntaryClosure']);
        FormUtils.disableFormControlStatus(this.homeInfoForm, ['certifyEntity']);
        this.isCategoryReq = true;
        this.enableHomeLicenseFields();
        FormUtils.enableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening', 'involuntaryClosure']);
        this.bHideSaveButton = false;
        this.bHideSaveSubmitButton = false;
        this.bHideApproveButton = false;
      }
    }
  }

  disableFormFieldsForApprovedActive() {
    if (this.currentHomeStatus === '040') {
      this.bHideAssignButton = true;
      this.bHideApproveButton = !this.displayHomeInformation.submittedForApproval;
      this.bHideDocumentButton = false;
    }
  }

  disableFieldsForApprovedInactiveAndApproved() {
    if (this.currentHomeStatus === '092') {
      this.bHideApproveButton = true;
      this.bHideAssignButton = true;
      this.bHideSaveSubmitButton = true;
    }

    if (this.currentHomeStatus === '010') {
      this.bHideApproveButton = true;
      this.bHideAssignButton = true;
      this.bHideSaveSubmitButton = true;
      this.bHideDocumentButton = false;
    }

    if (this.currentHomeStatus === '050'
      || this.currentHomeStatus === '020'
      || this.currentHomeStatus === '030') {
      this.enableAllFieldsAndButtons();
      this.bHideApproveButton = true;
      this.bHideAssignButton = true;
    }

  }

  disbaleFieldsForClosed() {
    const closureReason = this.homeInfoForm.get('closureReason').value;
    if (this.currentHomeStatus === '070' && closureReason) {
      this.disableAllFieldsAndButtons();
    }
  }

  disableFieldsForClosedFromCaseSummary() {
    if (this.currentHomeStatus === '070' && this.displayHomeInformation.resource.closureReason) {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['status']);
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['closureReason', 'recommendReopening', 'involuntaryClosure']);
      this.bHideAssignButton = false;
    }
  }

  enableViewModeApproveButton() {
    if (['040', '080'].includes(this.currentHomeStatus)) {
      this.bHideApproveButton = false;
    }
  }

  disableFieldsForClosedDuplicate() {
    if (this.currentHomeStatus === '090') {
      this.disableAllFieldsAndButtons();
    }
  }

  disableAddressAndPhoneLinks() {
    this.addressListDatatable.tableColumn[0].isLink = false;
    this.phoneListDatatable.tableColumn[0].isLink = false;
  }

  enableAddressAndPhoneLinks() {
    this.addressListDatatable.tableColumn[0].isLink = true;
    this.phoneListDatatable.tableColumn[0].isLink = true;
  }

  enableDisableHoldField() {
    if (this.bDisableOnHold) {
      FormUtils.disableFormControlStatus(this.homeInfoForm, ['onHold']);
    } else {
      FormUtils.enableFormControlStatus(this.homeInfoForm, ['onHold']);
    }
  }

  saveHomeInformation(isSubmit: boolean, isAssign: boolean) {
    this.clearFormErrors();
    this.updateFosterHomeTypes();
    if (this.validateFormGroup(this.homeInfoForm)) {
      const payload = Object.assign(this.displayHomeInformation.resource, this.homeInfoForm.value);
      this.displayHomeInformation.resource = payload;
      this.displayHomeInformation.resource.recommendReopen =  this.homeInfoForm.controls.recommendReopening.value;
      this.displayHomeInformation.resource.annualIncome =
        Number(Math.round(this.homeInfoForm.controls.annualIncome.value || 0.00).toFixed(2));
      payload.parentResourceId = this.displayHomeInformation.linkedResourceId;
      payload.faHomeStatus = this.homeInfoForm.controls.status.value;
      payload.category = this.homeInfoForm.controls.category.value;
      payload.marriageDate = this.homeInfoForm.controls.marriageDate.value;
      payload.sourceInquiry = this.homeInfoForm.controls.sourceOfInquiry.value;
      payload.certifyEntity = this.homeInfoForm.controls.certifyEntity.value;
      const request = {
        resource: { ...payload },
        homeType: payload.selectedHomeTypeData,
        relationship: payload.selectedHomeTypeRelationship,
        characteristics: this.selectedCharacteristicsData ? this.selectedCharacteristicsData : this.childCharacteristics,
        ethnicity: this.selectedEthinicityData ? this.selectedEthinicityData : this.childesEthnicity

      }
      this.caseService.saveHomeInformationFad(request, isSubmit, isAssign).subscribe(
        response => {
          this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
          if (isSubmit) {
            window.location.href = this.environmentSettings.impactP2WebUrl +
              '/case/home-information/displayToDoDetail?cacheKey=' + sessionStorage.getItem('cacheKey') + '&status=' +
              this.displayHomeInformation.resource.faHomeStatus;
          } else if (isAssign) {
            window.location.href = this.environmentSettings.impactP2WebUrl +
              '/home/assigment?cacheKey=' + sessionStorage.getItem('cacheKey');
          } else {
            this.router.navigate(['/case/home-information/FAD/' + this.displayHomeInformation.resource.resourceId])
              .then(() => {
                setTimeout(() => {
                  window.location.reload();
                }, 3000);
              });
          }
        }
      );
    }
  }



  save() {
    this.homeInfoForm.patchValue({ buttonClicked: 'save' });
    this.isAlert = false;
    this.hideAttentionMessage = true;

    if (this.currentHomeStatus === '080') {
      this.disableFieldsForPendingClosure();
    }
    this.isHomeLicenseChange = false;
    const categoryValue = this.homeInfoForm.get('category').value;
    const nonFpsAdoptiveHomeValue = this.homeInfoForm.get('nonPRSHome').value;
    const nonFpsPcaHomeValue = this.homeInfoForm.get('nonPRSPCA').value;
    const bICPCCategory = (categoryValue === 'S' || categoryValue === 'O' || categoryValue === 'V');
    const certifyEntity = this.homeInfoForm.get('certifyEntity').value;
    const statusValueClosed = this.homeInfoForm.get('status').value;
    if ((!nonFpsAdoptiveHomeValue && !nonFpsPcaHomeValue) && !(statusValueClosed === '070' && statusValueClosed === '010')) {
      if (statusValueClosed === '040' || statusValueClosed === '050') {
        this.isHomeLicenseValuesChanged();
      }
    }
    if (!this.isAlert && categoryValue !== 'A' && nonFpsAdoptiveHomeValue) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        message: 'Category selected not valid for Non-FPS Adoptive Home.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    }
    if (!this.isAlert && nonFpsPcaHomeValue && (categoryValue === 'A' || categoryValue === 'L' || categoryValue === 'O')) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        message: 'Category selected not valid for Non-FPS PCA Home.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    }
    if (this.displayHomeInformation.status && this.displayHomeInformation.status.length) {
      const isPendingApproval = this.displayHomeInformation.status.some(statusValue => {
        return statusValue.code && statusValue.code === '060';
      })
      this.homeInfoForm.patchValue({ errorMessageForSaveSubmit: isPendingApproval });
    }
    if (!this.isAlert && nonFpsPcaHomeValue && !bICPCCategory && certifyEntity !== '40'
      && (!this.displayHomeInformation.linkedResourceId)) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        message: 'You must select the Non-FPS Foster Home Resource ID, which is linked to the CLASS facility number, in order to save.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    }
    this.checkMaritalStatus();
    this.checkCategorySelectedForKin();
    if (this.displayHomeInformation.resource.faHomeStatus !== '070' && statusValueClosed === '070') {
      this.checkHomeLicensingChange();
    }
    if (!this.disableValidation && !this.isAlert) {
      this.saveHomeInformation(false, false);
    }

  }

  submit() {
    this.isAlert = false;
    this.hideAttentionMessage = true;
    this.homeInfoForm.patchValue({ buttonClicked: 'submit' });
    this.checkPendingCriminalHistory();
    this.checkCategorySelectedForKin();
    this.checkPendingClosureStatus();
    if (!this.isAlert) {
      this.saveHomeInformation(true, false);
    }
  }

  clearFormErrors() {
    Object.keys(this.homeInfoForm.controls).forEach((controlName: string) => {
      if (this.homeInfoForm.get(controlName).invalid) {
        this.homeInfoForm.get(controlName).clearValidators();
        this.homeInfoForm.get(controlName).updateValueAndValidity();
      }
    });
  }

  checkHomeLicensingChange() {
    if (!this.isAlert && this.isHomeLicenseValuesChanged()) {
      this.isAlert = true;
      const initialState = {
        title: 'Home License',
        message: 'Please save your licensing changes before closing the home.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      });
    }
  }

  checkPendingClosureStatus() {
    const statusPendingClosure = this.homeInfoForm.get('status').value;
    if (!this.isAlert && statusPendingClosure === '080' && !this.displayHomeInformation.closingSummaryDocExists) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        message: 'Remember to create Closing Summary before closing the Home.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        this.disableFieldsForPendingClosure();
        this.saveHomeInformation(true, false);
      });
    }
  }

  saveAndAssign() {
    this.homeInfoForm.patchValue({ buttonClicked: 'assign' });
    this.saveHomeInformation(false, true);
  }

  getReports(): Reports[] {
    return [
      {
        reportName: 'F/A Home Information Report',
        reportParams: {
          reportName: 'cfa09o00',
          emailMessage: 'FA Home Information Sheet for ',
          paramList: ''
        }
      }
    ];
  }

  getForms(): FormValue[] {
    return [{
      formName: 'Disaster Plan',
      formParams: {
        docType: 'CFA19O00',
        docExists: this.displayHomeInformation.disasterPlanDocExists ?
          String(this.displayHomeInformation.disasterPlanDocExists) : '',
        protectDocument: this.displayHomeInformation.protectDocument ?
          String(this.displayHomeInformation.protectDocument) : '',
        idStage: this.displayHomeInformation.resource.resourceFAHomeStageId ?
          String(this.displayHomeInformation.resource.resourceFAHomeStageId) : '',
        idResource: this.displayHomeInformation.resource.resourceId ?
          String(this.displayHomeInformation.resource.resourceId) : '',
        idPerson: this.displayHomeInformation.personId ?
          String(this.displayHomeInformation.personId) : '',
        idEventTable: this.displayHomeInformation.disasterPlanEventId ?
          String(this.displayHomeInformation.disasterPlanEventId) : '',
        idResourceTable: this.displayHomeInformation.resource.resourceId ?
          String(this.displayHomeInformation.resource.resourceId) : '',
        idPersonTable: this.displayHomeInformation.disasterPlanPersonId ?
          String(this.displayHomeInformation.disasterPlanPersonId) : '',
        dtTimestamp: this.displayHomeInformation.disasterPlanLastUpdatedDate ?
          String(this.displayHomeInformation.disasterPlanLastUpdatedDate) : '',
        userId: this.displayHomeInformation.personId ?
          String(this.displayHomeInformation.personId) : '',
      }
    }
    ];
  }

  generateReport(reportData: any) {
    if (reportData) {
      reportData = JSON.parse(reportData);
      reportData.emailMessage = reportData.emailMessage + this.displayHomeInformation.resource.name;
      reportData.paramList = this.displayHomeInformation.resource.resourceFAHomeStageId;
    }
    this.reportService.callReportService(reportData);
  }

  getNarrative(): FormParams {
    return {
      docType: 'cfa03o00',
      docExists: this.displayHomeInformation.homeStudyDocExists ?
        String(this.displayHomeInformation.homeStudyDocExists) : '',
      idStage: this.displayHomeInformation.resource.resourceFAHomeStageId ?
        String(this.displayHomeInformation.resource.resourceFAHomeStageId) : '',
      userId: this.displayHomeInformation.personId ?
        String(this.displayHomeInformation.personId) : '',
      checkStage: this.displayHomeInformation.resource.resourceFAHomeStageId ?
        String(this.displayHomeInformation.resource.resourceFAHomeStageId) : '',
      idCase: this.displayHomeInformation.resource.caseId ?
        String(this.displayHomeInformation.resource.caseId) : '',
      dtTimestamp: this.displayHomeInformation.homeStudyNarrativeLastUpdatedDate ?
        String(this.displayHomeInformation.homeStudyNarrativeLastUpdatedDate) : ''
    };
  }

  generateNarrative(narrativeData: any) {
    const maritalStatus = this.homeInfoForm.get('maritalStatus').value;
    const capacity = this.homeInfoForm.get('facilityCapacity').value;
    const annualIncome = this.homeInfoForm.get('annualIncome').value;
    const dateMarried = this.homeInfoForm.get('marriageDate').value;
    if ((!maritalStatus) || (!capacity || capacity === '0') ||
      (!annualIncome || annualIncome === '0.00')) {
      const initialState = {
        title: 'Home Information',
        message: `Please save the following before launching Home Study:
                    Annual Income, Marital Status, Home Capacity`,
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered',
        initialState,
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
    } else if ((maritalStatus && maritalStatus === '01') && !dateMarried) {
      const initialState = {
        title: 'Home Information',
        message: `Marriage Date is required if the Marital Status is Married`,
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered',
        initialState,
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
    }
    else {
      if (narrativeData) {
        this.narrativeService.callNarrativeService(narrativeData);
      }
    }
  }

  checkCategorySelectedForKin() {
    const selectedCategory = this.homeInfoForm.get('category').value;
    if (!this.isAlert && selectedCategory === 'K' || selectedCategory === 'I') {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        message: 'The selected category is no longer a valid category.  Please select a different category in order to save this FAD Home.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }

  checkPendingCriminalHistory() {
    if (!this.isAlert && this.displayHomeInformation.criminalHistoryPending
      && this.displayHomeInformation.criminalHistoryPersonList.length === 0) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        message: `A principal on the person list has a pending Criminal History Check that has not yet returned.
        Criminal History Check Results must be returned and accepted or rejected before submitting this Stage for approval.`,
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe();
    } else if (!this.isAlert && this.displayHomeInformation.criminalHistoryPending
      && this.displayHomeInformation.criminalHistoryPersonList.length > 0) {
      this.isAlert = true;
      const initialState = {
        title: 'Home Information',
        messageList: [
          {
            content: 'Criminal History Check Results page must be completed before submitting this page for approval. Check the Person List for the indicator to determine which person(s) need to have the results accepted/rejected',
            url: this.environmentSettings.impactP2WebUrl + '/case/person/records-check-list?cacheKey=' + sessionStorage.getItem('cacheKey')
          },
          {
            content: `A principal on the person list has a pending Criminal History Check that has not yet returned.
            Criminal History Check Results must be returned and accepted or rejected before submitting this Stage for approval.`,
            url: ''
          }],
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe();
    }
  }


  @HostListener('window:popstate' || 'window:hashchange', ['$event'])
  onPopState(event) {
    localStorage.setItem('backFrom', 'FadHomeInformation');
  }

  showApproval() {
    window.location.href = this.environmentSettings.impactP2WebUrl +
      '/case/approval-status/display?cacheKey=' + sessionStorage.getItem('cacheKey');
  }

  ngOnDestroy() {
    this.reportSubscription.unsubscribe();
  }
}
