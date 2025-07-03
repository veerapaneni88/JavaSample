import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { WaiverVarianceDetailRes, WaiverVariance, WaiverVarianceView } from '@case/model/waiverVariance';
import { Store } from '@ngrx/store';
import {
    DataTable,
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    NavigationService,
    ENVIRONMENT_SETTINGS,
    SET,
} from 'dfps-web-lib';
import { WaiverVarianceService } from '@case/service/waiver-variance.service';
import { SearchService } from '@shared/service/search.service';
import { ActivatedRoute, Router } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { WaiverVarianceValidator } from './waiver-variance.validator';
import { Inject, LOCALE_ID } from '@angular/core';
import { HelpService } from 'app/common/impact-help.service';

@Component({
    selector: 'waiver-variance',
    templateUrl: './waiver-variance.component.html',
})
export class WaiverVarianceComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    constructor(
        private navigationService: NavigationService,
        private formBuilder: FormBuilder,
        @Inject(LOCALE_ID) private locale: string,
        private waiverVarianceService: WaiverVarianceService,
        private router: Router,
        private route: ActivatedRoute,
        private modalService: BsModalService,
        private searchService: SearchService,
        private helpService: HelpService,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }
    waiverVarianceReqForm: FormGroup;
    requestType: any;
    childFosterParentsKin: any;
    childFosterParentsRelative: any;
    childFosterParentsUnRelated: any;
    emailDelvMethod: any;
    mailDelvMethod: any;
    faxDelvMethod: any;
    handDelvMethod: any;
    reqStatusTable: DataTable;
    tableColumn: any[];
    waiverVarianceDetailRes: WaiverVarianceDetailRes;
    waiverVariance: WaiverVariance;
    eventId = 0;
    usingEventId = 0;
    isNewUsing = false;
    hideSaveAndSubmit = false;
    displayRequestAdminReview = false;
    newUsingParam: any;
    hideApprovalStatus = true;
    taskStatusAction: any;
    informationalMsgs: string[] = [];
    infoDivDisplay = false;
    APRV = 'APRV';

    ngOnInit() {        
    this.navigationService.setTitle('Waiver Variance Request Detail');
    this.helpService.loadHelp('Case');
        this.requestType = [
            { label: 'Variance', value: 'V' },
            { label: 'Waiver', value: 'W' },
        ];
        this.createForm();
        this.eventId = +this.route.snapshot.paramMap.get('eventId');
        this.route.queryParams.subscribe((params) => {
            this.usingEventId = params.using ? params.using : 0;
            this.taskStatusAction = params.action;
        });
        if (!this.eventId || !this.usingEventId) {
            this.newUsingParam = this.eventId;
            if (this.newUsingParam === 0) {
                this.isNewUsing = false;
            }
            if (this.usingEventId) {
                this.newUsingParam = this.usingEventId;
                this.isNewUsing = true;
            }
            this.createRequestStatusTable();
            this.intializeScreen(this.eventId, this.usingEventId);
        }
    }

    intializeScreen(eventId, usingEventId) {
        this.waiverVarianceService.displayWaiverVarianceStandard(eventId, usingEventId).subscribe((response) => {
            this.waiverVarianceDetailRes = response;
            this.waiverVariance = this.waiverVarianceDetailRes.waiverVariance;
            this.loadWaiverVarianceForm(true, this.waiverVariance);
            this.populatePullBackData();
            this.disbleScreenElements(this.waiverVarianceDetailRes);
            this.disableSaveAndSaveSubmit(this.waiverVarianceDetailRes);
            this.hideSections();
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        });
    }

    loadWaiverVarianceForm(reload: any, waiverVarianceResult: WaiverVariance) {
        if (this.waiverVariance) {
            if (reload) {
                this.waiverVarianceReqForm.patchValue({
                    request: waiverVarianceResult.request,
                    relative: waiverVarianceResult.relative,
                    fictiveKin: waiverVarianceResult.fictiveKin,
                    unrelated: waiverVarianceResult.unrelated,
                    description1: waiverVarianceResult.description1,
                    description2: waiverVarianceResult.description2,
                    description3: waiverVarianceResult.description3,
                    description4: waiverVarianceResult.description4,
                    description5: waiverVarianceResult.description5,
                    description6: waiverVarianceResult.description6,
                    description7: waiverVarianceResult.description7,
                    ages: waiverVarianceResult.ages,
                    email: waiverVarianceResult.email,
                    mail: waiverVarianceResult.mail,
                    fax: waiverVarianceResult.fax,
                    childrenInCare: waiverVarianceResult.childrenInCare,
                    handDelivered: waiverVarianceResult.handDelivered,
                    requestUntilDate: waiverVarianceResult.requestUntilDate,
                    txtCondition1: waiverVarianceResult.condition1,
                    txtCondition2: waiverVarianceResult.condition2,
                    txtCondition3: waiverVarianceResult.condition3,
                    descriptionTxt: '',
                });
            } else {
                this.waiverVarianceReqForm.patchValue({
                    request: waiverVarianceResult.request,
                    description1: waiverVarianceResult.description1,
                    description2: waiverVarianceResult.description2,
                    description3: waiverVarianceResult.description3,
                    description4: waiverVarianceResult.description4,
                    description5: waiverVarianceResult.description5,
                    description6: waiverVarianceResult.description6,
                    description7: waiverVarianceResult.description7,
                    ages: waiverVarianceResult.ages,
                    childrenInCare: waiverVarianceResult.childrenInCare,
                    requestUntilDate: waiverVarianceResult.requestUntilDate,
                    txtCondition1: waiverVarianceResult.condition1,
                    txtCondition2: waiverVarianceResult.condition2,
                    txtCondition3: waiverVarianceResult.condition3,
                    descriptionTxt: '',
                });
            }
        }
    }

    createRequestStatusTable() {
        this.tableColumn = [
            { field: 'originalRequestDate', header: 'Original Received Date', width: 200 },
            { field: 'effectiveDate', header: 'Effective Date', isHidden: false, width: 200 },
            { field: 'endDate', header: 'Expiration Date', isHidden: false, width: 100 },
            { field: 'status', header: 'Status', isHidden: false, width: 100 },
            { field: 'denialReason', header: 'Reason', isHidden: false, width: 100 },
        ];
        this.reqStatusTable = {
            tableColumn: this.tableColumn,
            isSingleSelect: false,
            isPaginator: false,
        };
        this.reqStatusTable.tableBody = [];
    }
    createForm() {
        this.waiverVarianceReqForm = this.formBuilder.group(
            {
                request: ['', Validators.required],
                relative: [],
                fictiveKin: [],
                unrelated: [],
                description1: [''],
                description2: [''],
                description3: [''],
                description4: [''],
                description5: [''],
                description6: [''],
                description7: [''],
                descriptionTxt: [''],
                ages: ['', Validators.required],
                email: [],
                mail: [],
                fax: [],
                childrenInCare: [''],
                handDelivered: [],
                requestUntilDate: ['', [Validators.required, DfpsCommonValidators.validateDate]],
                txtCondition1: [],
                txtCondition2: [],
                txtCondition3: [],
                locale: [this.locale],
            },
            {
                validators: [
                    WaiverVarianceValidator.validateDescTextFields,
                    WaiverVarianceValidator.validateRequestUntilDate,
                    WaiverVarianceValidator.validateChildrenInCare,
                    WaiverVarianceValidator.validateRequestType
                ],
            }
        );
    }

    showApproval() {
        window.location.href =
            this.environmentSettings.impactP2WebUrl +
            '/case/approval-status/display?cacheKey=' +
            sessionStorage.getItem('cacheKey');
    }

    doSearchMinimumStandard() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let returnUrl;
        // TODO: URL
        if (this.isNewUsing && this.newUsingParam) {
            returnUrl = '/case/waiver-variance/' + this.eventId + '?using=' + this.usingEventId;
        } else {
            returnUrl = '/case/waiver-variance/' + this.eventId;
        }
        this.searchService.setSearchSource('searchStandard');
        this.searchService.setFormData(this.waiverVarianceReqForm.value);
        this.searchService.setFormContent(this.waiverVarianceDetailRes);
        this.searchService.setReturnUrl(returnUrl);
        this.router.navigate(['case/waiver-variance/' + this.eventId + '/search-standard'], { skipLocationChange: false });
    }

    populatePullBackData() {
        if (this.searchService.getFormData()) {
            this.waiverVarianceReqForm.patchValue(this.searchService.getFormData());
        }
        if (this.searchService.getFormContent()) {
            this.waiverVarianceDetailRes = this.searchService.getFormContent();
        }
        if (this.searchService.getselectedSearchStandard()) {
            this.waiverVariance.standard = this.searchService.getselectedSearchStandard();
            this.waiverVariance.standardId = this.searchService.getselectedSearchStandard().id;
        }
        this.searchService.setselectedSearchStandard(null);
        this.searchService.setFormData(null);
        this.searchService.setFormContent(null);
    }

    doSave() {
        if (this.validateFormGroup(this.waiverVarianceReqForm)) {
            this.saveWaiverVariance();
        }
    }

    saveWaiverVariance() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        const payLoad = Object.assign(this.waiverVariance, this.waiverVarianceReqForm.value);
        if (this.isNewUsing || !payLoad.id) {
            payLoad.id = 0;
            payLoad.eventId = 0;
            payLoad.verifiedHome = null;
        }
        this.waiverVarianceService.saveWaiverVariance(payLoad.eventId, payLoad).subscribe((response) => {
            if (response) {
                this.router.navigate([])
                    .then(() => {
                        setTimeout(() => {
                            window.location.href = this.environmentSettings.impactP2WebUrl +
                                '/case/caseSummary/event/displayEventList/VRN?cacheKey=' +
                                sessionStorage.getItem('cacheKey');
                        }, 3000);
                    });
            }
        });
    }

    doSaveAndSubmit() {
        if (this.validateFormGroup(this.waiverVarianceReqForm)) {
            this.submitWaiverVariance();
        }
    }

    submitWaiverVariance() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        const payLoad = Object.assign(this.waiverVariance, this.waiverVarianceReqForm.value);
        payLoad.isSaveAndSubmit = true;
        if (this.isNewUsing || !payLoad.id) {
            payLoad.id = 0;
            payLoad.eventId = 0;
        }
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.waiverVarianceService.submitWaiverVariance(payLoad.eventId, payLoad).subscribe((response) => {
            if (response) {
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                window.location.href =
                    this.environmentSettings.impactP2WebUrl +
                    '/case/waiver-variance/displayToDoDetail?cacheKey=' +
                    sessionStorage.getItem('cacheKey');
            }
        });
    }

    requestHelpIcon() {
        const initialState = {
            title: 'Home Information',
            message: `A waiver may be requested if the economic impact of compliance is great enough to make
            compliance impractical.


          A variance may be requested if there is good and just cause for the applicant to meet the purpose of the
          standard in a different way.`,

            showCancel: false,
        };
        this.modalService.show(DfpsConfirmComponent, { class: 'modal-lg modal-dialog-centered', initialState });
    }

    childRelHelpIcon() {
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
    disbleScreenElements(waiverVarianceDetailRes: WaiverVarianceDetailRes) {
        if (
            !this.isNewUsing &&
            (waiverVarianceDetailRes.pageMode === WaiverVarianceView.VIEW ||
                this.taskStatusAction === 'approval' ||
                (waiverVarianceDetailRes.event && waiverVarianceDetailRes.event.status === this.APRV))
        ) {
            this.waiverVarianceReqForm.get('request').disable();
            this.waiverVarianceReqForm.get('relative').disable();
            this.waiverVarianceReqForm.get('fictiveKin').disable();
            this.waiverVarianceReqForm.get('unrelated').disable();
            this.waiverVarianceReqForm.get('childrenInCare').disable();
            this.waiverVarianceReqForm.get('ages').disable();
            this.waiverVarianceReqForm.get('requestUntilDate').disable();
            this.waiverVarianceReqForm.get('email').disable();
            this.waiverVarianceReqForm.get('mail').disable();
            this.waiverVarianceReqForm.get('fax').disable();
            this.waiverVarianceReqForm.get('handDelivered').disable();
        }
    }

    disableSaveAndSaveSubmit(waiverVarianceDetailRes: WaiverVarianceDetailRes) {
        if (
            !this.isNewUsing &&
            (waiverVarianceDetailRes.pageMode === WaiverVarianceView.VIEW ||
                this.taskStatusAction === 'approval' ||
                (waiverVarianceDetailRes.event && waiverVarianceDetailRes.event.status === this.APRV))
        ) {
            this.hideSaveAndSubmit = true;
        }
    }

    hideSections() {
        const PEND = 'PEND';
        this.showDecisionAdminReqStatus(this.waiverVarianceDetailRes);
        this.hideApprovalStatus = !this.waiverVarianceDetailRes.submittedForApproval;

        if (!this.isNewUsing) {
            const eventObj = this.waiverVarianceDetailRes.event;
            if (PEND === eventObj.status && this.isNewUsing === false) {
                this.informationalMsgs.push(
                    'There is an outstanding approval request.  Saving this page will invalidate that approval. '
                );
                this.infoDivDisplay = true;
                const initialState = {
                    title: 'Waiver Variance',
                    message: 'Saving this page will invalidate pending approval. Click OK to continue.',
                    showCancel: false,
                };
                const modal = this.modalService.show(DfpsConfirmComponent, {
                    class: 'modal-md modal-dialog-centered',
                    initialState,
                });
                (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                    if (result === true) {
                        // do nothing;
                    }
                });
            }
        }
    }

    showDecisionAdminReqStatus(waiverVarianceDetailRes: WaiverVarianceDetailRes) {
        this.displayRequestAdminReview = waiverVarianceDetailRes?.reviewRequestStatus !== null;
        this.reqStatusTable.tableBody = this.waiverVarianceDetailRes.waiverVariance
            && this.waiverVarianceDetailRes.reviewRequestStatus;
    }
}
