import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CaseService } from '@case/service/case.service';
import {
  FormValue,
  NavigationService,
  Address,
  DfpsConfirmComponent,
  DfpsAddressValidatorComponent,
  ENVIRONMENT_SETTINGS,
  FormUtils,
  DfpsFormValidationDirective,
  DirtyCheck,
  ERROR_RESET,
  SUCCESS_RESET,
  DfpsCommonValidators,
  FormService} from 'dfps-web-lib';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { pairwise, startWith, tap, withLatestFrom } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { HelpService } from '../../../common/impact-help.service';
import { GuardianshipDetailsValidator } from './guardianship-details.validator';

@Component({
  selector: 'app-guardianship-detail',
  templateUrl: './guardianship-detail.component.html',
  styleUrls: ['guardianship-detail.component.css'],
})
export class GuardianshipDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
  @ViewChild(DfpsAddressValidatorComponent) dfpsAddressValidator: DfpsAddressValidatorComponent;

  apsGuardianshipDetails: any;
  eventId: any = 263775335;
  guardianType: string;
  guardianshipType: string;
  guardianTypeList: [];
  guardianshipTypeList: [];
  guardianName: string;
  initialCaseReviewResultsList: [];
  notifDadsAssessmentResultsList: [];
  jointStaffingResultsList: [];
  managementReviewResultsList: [];
  guardianshipReviewCommitteeResultsList: [];
  finalOutcomeList: [];
  isNewRecord = false;
  isEditMode: boolean;
  isAddressEditMode: boolean;
  pageMode: string;
  addressPageMode: string;
  statesList: [];
  countiesList: [];
  isAddrValidated = true;

  guardianshipDetailForm: FormGroup;
  showReferralActionSection = false;
  formValues: FormValue[];

  addressSub: Subscription;
  formsSubscription: Subscription;
  isAddressPhoneDetailsOpen = false;
  isReferralActionsOpen = false;
  showFormLaunch = true;
  isGuardianTypeChanged = false;

  constructor(private caseService: CaseService,
              private navigationService: NavigationService,
              private route: ActivatedRoute,
              private router: Router,
              private helpService: HelpService,
              private formBuilder: FormBuilder,
              private modalService: BsModalService,
              private cookieService: CookieService,
              private formService: FormService,
              @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
              public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Guardianship Detail Page');
    this.helpService.loadHelp('Case');
    this.route.params.pipe(
      withLatestFrom(this.route.queryParams),
      tap(([params, queryParams]) => {
        this.eventId = params.eventId;
        this.isNewRecord = +this.eventId === 0 ? true : false;
        this.guardianType = queryParams.guardianType;
        //this.guardianshipType = queryParams.guardianshipType;
      })
    ).subscribe(() => {
      this.initializeScreen();
    });
    this.formsSubscription = this.formService.formLaunchEvent.subscribe(data => {
      this.launchForm(data);
    });
  }

  launchForm(data: any) {
    if (data && this.apsGuardianshipDetails.pageMode === 'NEW' && !this.apsGuardianshipDetails.eventId) {
      const initialState = {
        title: 'Guardianship Detail',
        message: 'Please save page before producing document.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
      });
      return;
    }
    this.formService.launchForm(data ? JSON.stringify(JSON.parse(data)) : data);
  }

  subscribeGuardianType(guardianType: string) {
    this.showReferralActionSection = guardianType === 'DAD';
    this.guardianshipDetailForm.get('guardianType').valueChanges
      .pipe(startWith(guardianType || null), pairwise()).subscribe(([previousValue, currentValue]) => {
      if (this.isEditMode) {
        if (previousValue !== null) {
          this.showDataClearAlert(previousValue, currentValue);
        } else {
          this.onGuardianTypeChange(currentValue,previousValue);
        }
      }
    });
  }

  subscribeAssessmentResults() {
    this.guardianshipDetailForm.get('notifDadsAssessmentResults').valueChanges.subscribe((value) => {
      const depedentFields = {
        jointStaffing: '',
        jointStaffingResults: '',
        managementReview: '',
        managementReviewResults: '',
        guardianshipReviewCommittee: '',
        guardianshipReviewCommitteeResults: ''
      };
      if (value === 'N10') {
        this.guardianshipDetailForm.patchValue(depedentFields);
        FormUtils.disableFormControlStatus(this.guardianshipDetailForm, [...Object.keys(depedentFields)]);
      } else {
        FormUtils.enableFormControlStatus(this.guardianshipDetailForm, [...Object.keys(depedentFields)]);
      }
    })
  }

  setEditMode() {
    this.isEditMode = (this.pageMode === 'EDIT' || this.pageMode === 'NEW') ? true : false;
    this.isAddressEditMode = (this.addressPageMode === 'EDIT' || this.addressPageMode === 'NEW') ? true : false;
  }

  initializeScreen(): void {
    this.guardianshipDetailForm = null;
    this.showFormLaunch = true;
    this.caseService.getApsGuardianshipDetail(this.eventId,this.guardianType,this.guardianshipType).subscribe(res => {
      this.apsGuardianshipDetails = res;
      this.pageMode = res.pageMode;
      this.addressPageMode = res.addressPageMode;
      this.setEditMode();
      this.populateDropdownValues();
      this.createForm();
      this.formValues = this.getForms();
    });
  }

  createForm() {
    const details = this.apsGuardianshipDetails?.guardianshipResponseDto;
    const addressDisabled = this.apsGuardianshipDetails?.addressPageMode === 'VIEW';
    const isAOCStage = this.apsGuardianshipDetails?.cdStage === "AOC";
    const disableAssessmentsFields = details?.cdNotifAssessResult === 'N10';
    this.guardianshipDetailForm = this.formBuilder.group({
      guardianType: [isAOCStage ? 'DAD' : details?.cdGuardGuardianType, [Validators.required]],
      guardianshipType: [details?.cdGuardType, [Validators.required]],
      guardianName: [isAOCStage ? 'DADS' : details?.sdsGuardName],
      initialCaseReview: [details?.dtInitReview, [DfpsCommonValidators.validateDate]],
      initialCaseReviewResults: [details?.cdInitReviewResult],
      notificationOfDadsAssessmentReceived: [details?.dtNotifAssess, [DfpsCommonValidators.validateDate]],
      notifDadsAssessmentResults: [details?.cdNotifAssessResult],
      jointStaffing: [{ value: details?.dtJointStaff, disabled: disableAssessmentsFields }, [DfpsCommonValidators.validateDate]],
      jointStaffingResults: [{ value: details?.cdJointStaffResult, disabled: disableAssessmentsFields }],
      managementReview: [{ value: details?.dtMgmtReview, disabled: disableAssessmentsFields }, [DfpsCommonValidators.validateDate]],
      managementReviewResults: [{ value:details?.cdMgmtReviewResult, disabled: disableAssessmentsFields }],
      guardianshipReviewCommittee: [{ value:details?.dtCommittee, disabled: disableAssessmentsFields }, [DfpsCommonValidators.validateDate]],
      guardianshipReviewCommitteeResults: [{ value:details?.cdCommitteeResult, disabled: disableAssessmentsFields }],
      finalOutcome: [details?.cdFinalOutcome],
      // phone: [details?.nbrGuardPhone],
      phone : [FormUtils.formatPhoneNumber(details?.nbrGuardPhone) || '', [GuardianshipDetailsValidator.phoneNumberPattern]],
      ext: [details?.nbrGuardPhoneExt || '', [GuardianshipDetailsValidator.phoneNumberExtPattern]],
      address: this.formBuilder.group({
        address1: [{ value: details?.addrGuardLn1, disabled: addressDisabled }],
        address2: [{ value: details?.addrGuardLn2, disabled: addressDisabled }],
        state: [{value: details?.addrGuardSt, disabled: addressDisabled }],
        county: [{value: details?.addrGuardCnty, disabled: addressDisabled }],
        // zip: [details?.addrGuardZip],
        // zipExt: [details?.addrGuardZip],
        zip: [{ value: details?.addrGuardZip?.split('-')?.[0] || '', disabled: addressDisabled }, [DfpsCommonValidators.zipPattern]],
        zipExt: [{ value: details?.addrGuardZip?.split('-')?.[1] || '', disabled: addressDisabled },
          [DfpsCommonValidators.zipExtensionPattern]],
        city: [{ value: details?.addrGuardCity || '' , disabled: addressDisabled }, [DfpsCommonValidators.cityPattern]]
      }),
      comments: [details?.txtGuardAddrComments],
      agedOutOfCps: [details?.indGuardAgedOut === 'Y']
    });
    this.disableFields();
    this.subscribeGuardianType(details?.cdGuardGuardianType || null);
    this.subscribeAssessmentResults();
    // this.addressSub = this.guardianshipDetailForm.get('address').valueChanges.subscribe(() => {
    //   this.isAddrValidated = false;
    // });
  }

  disableFields() {
    const details = this.apsGuardianshipDetails?.guardianshipResponseDto;
    let fieldsToBeDisabled = [];
    const addressFields = ['address', 'phone', 'ext', 'comments'];
    if (!this.isEditMode) {
      fieldsToBeDisabled = [...Object.keys(this.guardianshipDetailForm.value).filter((x) => !addressFields.includes(x))];
      fieldsToBeDisabled.push('agedOutOfCps');
    } else if(this.isEditMode && this.eventId && details?.sdsGuardName != null) {
      fieldsToBeDisabled.push('guardianName');
    }
    if (!this.isAddressEditMode) {
      fieldsToBeDisabled = fieldsToBeDisabled.concat(...addressFields);;
    }
    if (this.apsGuardianshipDetails?.cdStage === "AOC") {
      fieldsToBeDisabled.push('guardianName');
      fieldsToBeDisabled.push('guardianType');
    }
    FormUtils.disableFormControlStatus(this.guardianshipDetailForm, fieldsToBeDisabled);
  }

  populateDropdownValues() {
    this.guardianTypeList = this.apsGuardianshipDetails?.guardianTypeList;
    this.guardianshipTypeList = this.apsGuardianshipDetails?.guardianshipTypeList;
    this.initialCaseReviewResultsList = this.apsGuardianshipDetails?.caseReviewResults;
    this.notifDadsAssessmentResultsList = this.apsGuardianshipDetails?.assessmentResults;
    this.jointStaffingResultsList = this.apsGuardianshipDetails?.jointStaffingResults;
    this.managementReviewResultsList = this.apsGuardianshipDetails?.managementReviewResults;
    this.guardianshipReviewCommitteeResultsList = this.apsGuardianshipDetails?.guardianshipReviewComResults;
    this.finalOutcomeList = this.apsGuardianshipDetails?.finalOutcomeList;
    this.statesList = this.apsGuardianshipDetails?.statesList;
    this.countiesList = this.apsGuardianshipDetails?.countyList;
  }

  save() {
   if (this.isGuardianTypeChanged) {
     this.guardianshipDetailForm.controls.guardianName.updateValueAndValidity();
    }
    const guardianTypeValue = this.guardianshipDetailForm.controls.guardianType.value
    this.handleDADSSectionFieldsOnGuardianTypeChange(guardianTypeValue);
    this.handleAddressPhoneFieldsOnGuardianTypeChangeOnSave(guardianTypeValue);
    this.clearServerMsg();
    let isValid = true;
    let validationErrors = [];
    if (!this.validateFormGroup(this.guardianshipDetailForm)) {
      isValid = false;
      validationErrors = this.validationErrors.map((x) => ({...x}));
    }
    if (!this.validateFormGroup(this.guardianshipDetailForm.get('address') as FormGroup)) {
      isValid = false;
    }
    if (isValid) {
      const payload = this.getPayload();
      if (!this.eventId) {
        this.formValues = this.getForms();
      }
      this.caseService.saveGuardianshipDetails(payload).subscribe((res) => {
        this.isNewRecord = false;
        this.showFormLaunch = false;
        this.router.navigate([`case/guardianship-detail/${res.eventId}`]);
        window.scrollTo(0, 0);
        setTimeout(() => {
          this.showFormLaunch = true;
        });
      });
    }
  }

  validateAdressFields() {
    const addressForm = this.guardianshipDetailForm.get('address').value;
    const address: Address = {
      street1: addressForm.address1,
      street2: addressForm.address2,
      city: addressForm.city,
      county: addressForm.county,
      state: addressForm.state,
      zip: addressForm.zip,
      extension: addressForm.zipExt
    };
    this.dfpsAddressValidator.validate(address);
  }

  updateAdressFields(address: Address) {
    if (address) {
      if (address.isAddressAccepted) {
        this.guardianshipDetailForm.get('address').patchValue({
          addressLine1: address.street1,
          addressLine2: address.street2,
          city: address.city,
          county: address.countyCode,
          state: address.state,
          zip: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[0] : '') : '',
          zipExt: address.zip ? (address.zip.includes('-') ? address.zip.split('-')[1] : '') : '',
        }, { emitEvent: false });
        this.isAddrValidated = true;
      } else {
        if (this.guardianshipDetailForm.get('address.zip').value &&
          this.guardianshipDetailForm.get('address.zip').value.length !== 5) {
          this.guardianshipDetailForm.get('address.zip').patchValue({
            zip: '',
            zipExt: ''
          });
        }

        if (this.guardianshipDetailForm.get('address.zipExt').value &&
          this.guardianshipDetailForm.get('address.zipExt').value.length !== 4) {
          this.guardianshipDetailForm.get('address').patchValue({
            zipExt: ''
          });
        }
      }
    }
  }

  selectPerson() {
    const guardianTypeSelect = this.guardianshipDetailForm.get('guardianType').value;
    const isGuardianTypeOtherPerson = guardianTypeSelect === 'OT1' ? true: false;
    setTimeout(() => {
      if (isGuardianTypeOtherPerson) {
        const eventId = this.eventId;
        const guardianType = this.guardianshipDetailForm.get('guardianType').value;
        const guardianshipType = this.guardianshipDetailForm.get('guardianshipType').value;
        const destinationUrl = '/case/guardianship-detail/' + eventId +
          '?guardianType=' + guardianType + '&guardianshipType=' + guardianshipType;
        const callingPage = 'Guardianship Detail List';
        const thirdLevelNav = 'guardianship';
        const activeSideNav = 'guardianshipDetail';
        const activeThirdLevelNav = 'guardianship';

        const cacheKey = this.environmentSettings.environmentName === 'Local'
          ? this.cookieService.get('cacheKey') : localStorage.getItem('cacheKey');
        window.location.href = this.environmentSettings.impactP2WebUrl +
          '/case/caseSummary/event/displayPersonList?cacheKey=' + cacheKey +
          '&callingPage=' + callingPage + '&destinationUrl=' + destinationUrl
          + '&thirdLevelNav=' + thirdLevelNav + '&activeSideNav=' + activeSideNav + '&activeThirdLevelNav=' + activeThirdLevelNav;
      } else {
        const initialState = {
          title: 'Guardianship Details',
          message: 'The Person push button cannot be used with the selected Guardian Type',
          showCancel: false,
        };
        this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      }
    });
  }

  selectResource() {

  }

  getForms(): FormValue[] {
    return [{
      formName: 'Guardianship Details',
      formParams: {
        docType: 'CSC04O00',
        docExists: 'false',
        protectDocument: 'true',
        checkForNewMode: 'false',
        pStage: this.apsGuardianshipDetails.stageId ? String(this.apsGuardianshipDetails.stageId) : '',
        pEvent: this.apsGuardianshipDetails.eventId ? String(this.apsGuardianshipDetails.eventId) : ''
      }
    }
    ];
  }

  ngOnDestroy(): void {
    this.addressSub?.unsubscribe();
    this.formsSubscription?.unsubscribe();
    this.clearServerMsg();
  }

  showDataClearAlert(prevGuardianType: string, guardianType: string) {
    const initialState = {
      title: 'Guardianship Details',
      message: 'Guardian Name and any related information will be cleared. Continue?',
      showCancel: true,
    };
    const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
    (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
      document.getElementById('guardianType').focus();
      if (result) {
        this.guardianshipDetailForm.patchValue({
          guardianName: ''
        }, { emitEvent: false })
        this.onGuardianTypeChange(guardianType, prevGuardianType);
      } else {
        this.guardianshipDetailForm.patchValue({
          guardianType: prevGuardianType
        }, { emitEvent: false });
      }
    });
  }

  onGuardianTypeChange(guardianType: string, prevGuardianType: string) {
    this.showReferralActionSection = guardianType === 'DAD';
    const guardianNameControl = this.guardianshipDetailForm.get('guardianName');
    guardianNameControl.clearValidators();
    FormUtils.enableFormControlStatus(this.guardianshipDetailForm, ['guardianName']);
    if (guardianType === 'DAD') {
      this.guardianshipDetailForm.patchValue({
        guardianName: 'DADS'
      }, { emitEvent: false })
      FormUtils.disableFormControlStatus(this.guardianshipDetailForm, ['guardianName']);
    } else {
      guardianNameControl.setValidators([Validators.required]);
    }
    // guardianNameControl.updateValueAndValidity();
    this.handleAddressPhoneFieldsOnGuardianTypeChange(guardianType, prevGuardianType);
    // this.handleDADSSectionFieldsOnGuardianTypeChange(guardianType);
    this.isGuardianTypeChanged = true;
    this.guardianshipDetailForm.updateValueAndValidity();
  }

  clearServerMsg() {
    this.store.dispatch(ERROR_RESET(null));
    this.store.dispatch(SUCCESS_RESET(null));
  }

  handleAddressPhoneFieldsOnGuardianTypeChange(guardianType: string, prevGuardianType: string) {
    if ( guardianType !== 'OT3' && guardianType !== 'OT2') {
      this.isAddressPhoneDetailsOpen = false;
    } else if (this.isAddressEditMode && prevGuardianType !== 'OT3' && prevGuardianType !== 'OT2') {
      if (!this.guardianshipDetailForm.get('address').get('state').value) {
        this.guardianshipDetailForm.patchValue({
          address: {
            state: 'TX'
          }
        });
      }
    }
  }

  handleAddressPhoneFieldsOnGuardianTypeChangeOnSave(guardianType: string) {
    if ( guardianType !== 'OT3' && guardianType !== 'OT2') {
      this.isAddressPhoneDetailsOpen = false;
      this.isAddrValidated = false;
      this.guardianshipDetailForm.patchValue({
        phone: '',
        ext: '',
        address: {
          address1: '',
          address2: '',
          state: '',
          county: '',
          zip: '',
          zipExt: '',
          city: ''
        },
        comments: ''
      });
    }
  }

  handleDADSSectionFieldsOnGuardianTypeChange(guardianType: string) {
    if (this.isGuardianTypeChanged && guardianType !== 'DAD') {
      this.isGuardianTypeChanged = false;
      this.guardianshipDetailForm.patchValue({
        initialCaseReview: '',
        initialCaseReviewResults: '',
        notificationOfDadsAssessmentReceived: '',
        notifDadsAssessmentResults: '',
        jointStaffing: '',
        jointStaffingResults: '',
        managementReview: '',
        managementReviewResults: '',
        guardianshipReviewCommittee: '',
        guardianshipReviewCommitteeResults: '',
        finalOutcome: ''

      });
    }
  }

  getPayload() {
    const { guardianshipResponseDto: details, stageId,  caseId } = this.apsGuardianshipDetails;
    const formValues = this.guardianshipDetailForm.value;
    const formControls = this.guardianshipDetailForm.controls;
    const addressForm: any = this.guardianshipDetailForm.controls.address;
    return {
      eventId: this.eventId || null,
      addressValidated: this.isAddrValidated,
      editConfig: null,
      stageId,
      caseId,
      reqFuncCd: null, // TODO check
      guardianshipResponseDto: {
        idGuardEvent: details?.idGuardEvent,
        dtLastUpdate: details?.dtLastUpdate,
        personId: details?.personId,
        resourceId: details?.resourceId,
        addrGuardCity: addressForm.controls.city.value,
        addrGuardCnty: addressForm.controls.county.value,
        addrGuardLn1: addressForm.controls.address1.value?.length>=30?addressForm.controls.address1.value.substring(0, 30):addressForm.controls.address1.value,
        addrGuardLn2: addressForm.controls.address2.value?.length>=30?addressForm.controls.address2.value.substring(0, 30):addressForm.controls.address2.value,
        addrGuardSt: addressForm.controls.state.value,
        addrGuardZip: addressForm.controls.zip.value+'-'+addressForm.controls.zipExt.value,
        cdGuardCloseReason: details?.cdGuardCloseReason,
        cdGuardGuardianType: formControls.guardianType.value,
        cdGuardType: formControls.guardianshipType.value,
        dtGuardCloseDate: details?.dtGuardCloseDate,
        dtGuardLetterIssued: details?.dtGuardLetterIssued,
        dtGuardOrdered: details?.dtGuardOrdered,
        indGuardAgedOut: details?.indGuardAgedOut,
        nbrGuardPhone: formControls.phone.value.replace(/[^\d]/g, ''),
        nbrGuardPhoneExt: formControls.ext.value,
        sdsGuardName: formControls.guardianName.value?.length>=30?formControls.guardianName.value.substring(0, 30):formControls.guardianName.value,
        txtGuardAddrComments: formControls.comments.value,
        txtGuardComments: details?.txtGuardComments,
        dtReferralSent: details?.dtReferralSent,
        dtInitReview: formControls.initialCaseReview.value,
        cdInitReviewResult: formControls.initialCaseReviewResults.value,
        dtAcceptance: details?.dtAcceptance,
        dtNotifAssess: formControls.notificationOfDadsAssessmentReceived.value,
        cdNotifAssessResult: formControls.notifDadsAssessmentResults.value,
        dtJointStaff: formControls.jointStaffing.value,
        cdJointStaffResult: formControls.jointStaffingResults.value,
        dtMgmtReview: formControls.managementReview.value,
        cdMgmtReviewResult: formControls.managementReviewResults.value,
        dtCommittee: formControls.guardianshipReviewCommittee.value,
        cdCommitteeResult: formControls.guardianshipReviewCommitteeResults.value,
        cdFinalOutcome: formControls.finalOutcome.value,
        resourceName: details?.resourceName,
        fullName: details?.fullName
      },
      entityInformation: {
        entityAddressDto: {},
        entityDto: {},
        entityEmailDto: {},
        entityNameDto: {},
        entityPhoneDto: {}
      }
    }
  }

  onAddressPhoneDetailOpenChange(event) {
    const guardianType = this.guardianshipDetailForm.get('guardianType').value;
    const isAddressPhoneEnabled = guardianType === 'OT3' || guardianType === 'OT2';
    setTimeout(() => {
      if (event && !isAddressPhoneEnabled) {
        this.isAddressPhoneDetailsOpen = false;
        const initialState = {
          title: 'Guardianship Details',
          message: 'The Address/Phone sub-module cannot be used with selected guardian type',
          showCancel: false,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(() => {
          document.getElementById('saveButtonId').focus();
        });
        
      }
    });
  }


}
