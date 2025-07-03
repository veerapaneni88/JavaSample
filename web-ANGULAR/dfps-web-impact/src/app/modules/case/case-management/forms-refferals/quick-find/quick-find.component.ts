import { ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    DfpsCommonValidators,
    DfpsFormValidationDirective,
    DirtyCheck,
    DropDown,
    SET,
    ENVIRONMENT_SETTINGS,
    FormUtils,
} from 'dfps-web-lib';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { HelpService } from '../../../../../common/impact-help.service';
import { QuickFindService } from '../../../service/forms-referral.service';
import { QuickFindValidators } from './quick-find.validator';
import { SearchService } from '@shared/service/search.service';
import { CaseService } from '@case/service/case.service';
import { QuickFindPersonsDto } from '@case/model/formsReferrals';

@Component({
    selector: 'quick-find',
    templateUrl: './quick-find.component.html',
})
export class QuickFindComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    @ViewChild('errors') errorElement: ElementRef;
    quickFindForm: FormGroup;
    quickFindData: any;
    isReadOnly = false;
    regions: [];
    counties: [];
    isAlert = false;
    hideAttentionMessage = false;
    quickFindId: any;
    isEditMode = false;
    personList: DropDown[];
    subjectsControlCount = 0;
    isChecked: boolean[] = [false, false, false];
    isNewUsing = false;
    disableDeleteButton = true;
    disableSearchButton = false;
    personFields: QuickFindPersonsDto[];


    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private helpService: HelpService,
        private quickFindService: QuickFindService,
        private searchService: SearchService,
        private caseService: CaseService,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }

    ngOnInit(): void {
        this.helpService.loadHelp('Case');
        this.initializeScreen();
        this.createForm();
    }

    createForm() {
        this.quickFindForm = this.formBuilder.group(
            {
                requestersName: [''],
                requestersPhone: ['', [DfpsCommonValidators.validateMaxId, Validators.maxLength(30)]],
                requestersPhoneExtension: ['', [DfpsCommonValidators.validateMaxId, Validators.maxLength(8)]],
                caseId: [''],
                requestersRegion: [''],
                requestersCounty: [''],
                cps: [''],
                cpsNytd: [''],
                accounting: [''],
                legal: [''],
                aps: [''],
                emr: [''],
                fullName: [''],
                pdfFullName: [''],
                addressLine1: [''],
                addressLine2: [''],
                city: [''],
                state: [''],
                zip: [''],
                phone: [''],
                dateOfBirth: [''],
                approximateDateOfBirth: [false],
                ssn: [''],
                ethnicity: [''],
                personId: [''],
                comments: [''],
                subjects: new FormArray([]),
                buttonClicked: [''],
                requestersPersonId: [''],
            },
            {
                validators: [QuickFindValidators.validateQuickFindInfo],
            }
        );
    }

    buildSubjects() {
        const subjectsControl = this.quickFindForm.get('subjects') as FormArray;
        subjectsControl.push(this.createSubjectsForm());
    }

    createSubjectsForm() {
        return new FormGroup({
            fullName: new FormControl(''),
            pdfFullName: new FormControl(''),
            addressLine1: new FormControl(''),
            addressLine2: new FormControl(''),
            city: new FormControl(''),
            state: new FormControl(''),
            county: new FormControl(''),
            zip: new FormControl(''),
            phone: new FormControl(''),
            dateOfBirth: new FormControl(''),
            approximateDateOfBirth: new FormControl(false),
            ssn: new FormControl(''),
            ethnicity: new FormControl(''),
            personId: new FormControl(''),
            comments: new FormControl(''),
            quickFindPersonsId: new FormControl(''),
        });
    }

    get subjects(): FormArray {
        return this.quickFindForm.get('subjects') as FormArray;
    }

    addSubject() {
        const subjectsControl = this.quickFindForm.get('subjects') as FormArray;
        if (subjectsControl.controls.length < 3) {
            this.buildSubjects();
        } else {
            this.quickFindForm.get('Add').disable();
        }
        this.subjectsControlCount = subjectsControl.controls.length;
    }

    initializeScreen() {
            const fullPath = this.route.snapshot.url.map(segment => segment.path).join('/');
            const quickFindId = this.route.snapshot.paramMap.get('id')
            ? this.route.snapshot.paramMap.get('id')
            : '0';
            let temp = this.route.snapshot.queryParamMap.get('isNewUsing')
            this.isNewUsing = this.route.snapshot.queryParamMap.get('isNewUsing')
            ? (this.route.snapshot.queryParamMap.get('isNewUsing') === 'true')
            : false;
            this.quickFindId = this.isNewUsing ? '0' : quickFindId;
            this.quickFindService.getQuickFind(quickFindId).subscribe((res) => {
                if (res.quickFind && res.quickFind.subjects) {
                    if (this.isNewUsing) {
                        res.quickFind.quickFindId = '0';
                        res.quickFind.formsReferralsId = '0';
                        res.quickFind.eventStatus = 'PROC';
                        res.quickFind.createdDate = null;
                        res.quickFind.lastUpdatePersonId = '0';
                        res.quickFind.lastUpdatedDate = null;
                        res.quickFind.createdPersonId = '0';
                        res.quickFind.subjects.forEach(subject => subject.quickFindPersonsId = '0');
                    }
                    this.personList = res.searchSubjects.map((obj) => new DropDown(obj.fullName, obj.personId));
                }
                this.quickFindData = res;
                this.pupulateFormData();
                this.populatePullBackData();
                this.getPageMode();
            });

    }

    pupulateFormData() {
        if (this.quickFindId === '0' && !this.isNewUsing) {
            this.quickFindForm.patchValue({ caseId: this.quickFindData.caseId });
            this.quickFindForm.patchValue({ requestersName: this.quickFindData.requester.fullName });
            this.quickFindForm.patchValue({ requestersPersonId: this.quickFindData.quickFind.requestersPersonId });
            this.quickFindForm.patchValue({ requestersPhone: this.quickFindData.requester.requestersPhone });
            this.quickFindForm.patchValue({ requestersRegion: this.quickFindData.stageRegion });
            this.quickFindForm.patchValue({ requestersCounty: this.quickFindData.stageCounty });
            this.quickFindForm.patchValue({ requestersPhone: this.quickFindData.quickFind.requestersPhone});
            this.buildSubjects();
        } else {
            this.quickFindForm.patchValue({ caseId: this.quickFindData.quickFind.caseId });
            this.quickFindForm.patchValue({ requestersName: this.quickFindData.quickFind.requestersName });
            this.quickFindForm.patchValue({ requestersPhone: this.quickFindData.quickFind.requestersPhone });
            this.quickFindForm.patchValue({ requestersPhoneExtension: this.quickFindData.quickFind.requestersPhoneExtension });
            this.quickFindForm.patchValue({ requestersPersonId: this.quickFindData.quickFind.requestersPersonId });
            this.quickFindForm.patchValue({ requestersRegion: this.quickFindData.quickFind.requestersRegion });
            this.quickFindForm.patchValue({ requestersCounty: this.quickFindData.quickFind.requestersCounty });
            this.quickFindForm.patchValue({ cps: this.quickFindData.quickFind.cps });
            this.quickFindForm.patchValue({ cpsNytd: this.quickFindData.quickFind.cpsNytd });
            this.quickFindForm.patchValue({ accounting: this.quickFindData.quickFind.accounting });
            this.quickFindForm.patchValue({ legal: this.quickFindData.quickFind.legal });
            this.quickFindForm.patchValue({ aps: this.quickFindData.quickFind.aps });
            this.quickFindForm.patchValue({ emr: this.quickFindData.quickFind.emr });
            this.populateSubDetails(this.quickFindData.quickFind.subjects);
            this.disableDeleteButton = this.quickFindData.quickFind.eventStatus !== 'PROC';
            this.disableSearchButton = this.quickFindData.quickFind.eventStatus === 'COMP';
        }
        this.populateDropdownValues();
    }

    populateDropdownValues() {
        this.regions = this.quickFindData.region;
        this.counties = this.quickFindData.county;
    }

    populateSubDetails(list: any) {
        list.forEach((i, currentIndex) => {
            this.addSubject();
            const subjectControls = this.quickFindForm.get('subjects') as FormArray;
            const val = subjectControls.controls[currentIndex];
            const person = list[currentIndex];
            subjectControls.controls[currentIndex].setValue(person);
            subjectControls.controls[currentIndex].get('fullName').setValue(person.personId);
        });
    }

    getSubjects(quickFindForm): any {
        return quickFindForm.get('subjects').controls;
    }

    complete() {
        this.quickFindForm.patchValue({ buttonClicked: 'complete' });
        this.clearValidators();
        if (this.validateFormGroup(this.quickFindForm)) {
            this.saveQuickFind(true, false);
        }
    }

    save() {
        this.quickFindForm.patchValue({ buttonClicked: 'save' });
        this.clearValidators();
        if (this.validateFormGroup(this.quickFindForm)) {
            this.saveQuickFind(false, false);
        }
    }

    saveAndStay() {
        this.quickFindForm.patchValue({ buttonClicked: 'saveAndStay' });
        this.clearValidators();
        if (this.validateFormGroup(this.quickFindForm)) {
            this.saveQuickFind(false, true);
        }
    }

    saveQuickFind(isComplete: boolean, isStay: boolean) {
        this.updateNames();
        const payload = Object.assign(this.quickFindData.quickFind || {}, this.quickFindForm.value);
        payload.quickFindId = this.quickFindId;
        if (isComplete) payload.actionType = 'COMP';
        else payload.actionType = 'PROC';
        this.quickFindService.saveQuickFind(payload).subscribe((res) => {
            if (isStay) {
                const url = 'case/case-management/forms-referral/quick-find/' + res.quickFindId;
                this.quickFindId = res.quickFindId;
                window.location.href = url;
            } else {
                window.location.href =
                    this.environmentSettings.impactP2WebUrl + '/case/caseManagement/formsReferralsList.get';
            }
        });
        return null;
    }
    updateNames() {
        const subjectControls = this.quickFindForm.get('subjects') as FormArray;
        subjectControls.controls.forEach((element) => {
            const subNameControl = element.get('fullName');
            const name = this.findNameById(this.personList, subNameControl.value);
            subNameControl.setValue(name);
        });
    }

    findNameById(dropDownArray: DropDown[], value: any) {
        for (const element of dropDownArray) {
            if(element.code === value)
                return element.decode;
        }
    }



    delete() {
        this.quickFindForm.patchValue({ buttonClicked: 'delete' });
        this.quickFindService.deleteQuickFind(this.quickFindId).subscribe((res) => {
                window.location.href =
                    this.environmentSettings.impactP2WebUrl + '/case/caseManagement/formsReferralsList.get';
        });
        return null;
    }
    getCustomStyles() {
        return 'height: 130px';
    }

    getTextAreaId(index) {
        return 'comments' + index;
    }

    selectStaff() {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let returnUrl;
        if (this.quickFindId) {
            returnUrl = 'case/case-management/forms-referral/quick-find/' + this.quickFindId;
        } else {
            returnUrl = 'case/case-management/forms-referral/quick-find/0';
        }
        this.searchService.setSearchSource('QuickFind');
        this.searchService.setFormData(this.quickFindForm.value);
        this.searchService.setFormContent(this.quickFindData);
        this.searchService.setReturnUrl(returnUrl);
        this.router.navigate(['case/case-management/forms-referral/quick-find/' + this.quickFindId + '/staffsearch'], {
            skipLocationChange: false,
        });
    }

    populatePullBackData() {
        if (this.searchService.getFormData()) {
            this.quickFindForm.patchValue(this.searchService.getFormData());
        }
        if (this.searchService.getSelectedStaff()) {
            this.quickFindForm.patchValue({ requestersName: this.searchService.getSelectedStaff().name });
            this.quickFindForm.patchValue({ requestersPersonId: this.searchService.getSelectedStaff().personId });
            this.quickFindForm.patchValue({ requestersPhone: this.searchService.getSelectedStaff().workPhone });
            this.quickFindForm.patchValue({ requestersPhoneExtension : this.searchService.getSelectedStaff().ext });
        }
    }

    getPageMode() {
        if (this.quickFindData.pageMode === 'NEW' || this.isNewUsing) {
            this.isEditMode = true;
        } else if (this.quickFindData.pageMode === 'EDIT') {
            this.isEditMode = true;
        } else if (this.quickFindData.pageMode === 'VIEW') {
            this.isEditMode = false;
            this.disableAllFields();
            (this.quickFindForm.get('subjects') as FormArray).controls.forEach((val: FormGroup) => {
                const fields = Object.keys(val.controls);
                fields.forEach((field) => {
                    val.get(field).disable();
                });
            });
        }
    }

    disableAllFields() {
        FormUtils.disableFormControlStatus(this.quickFindForm, [
            'requestersName',
            'requestersPhone',
            'requestersPhoneExtension',
            'requestersRegion',
            'requestersCounty',
            'cps',
            'cpsNytd',
            'accounting',
            'legal',
            'aps',
            'emr',
            'approximateDateOfBirth',
        ]);
    }

    updateSubDetails(selectedIndex: any) {
        const list = this.quickFindData.searchSubjects;
        const subjectControls = this.quickFindForm.get('subjects') as FormArray;
        const val = subjectControls.controls[selectedIndex];
        const selectedPerson = val.get('fullName').value;
        if (!selectedPerson) {
            val.reset();
        } else {
            list.forEach((i, currentIndex) => {
                if (list[currentIndex].personId === selectedPerson) {
                    const person = list[currentIndex];
                    const comments = subjectControls.controls[selectedIndex].get('comments').value;
                    subjectControls.controls[selectedIndex].setValue(person);
                    subjectControls.controls[selectedIndex].get('comments').setValue(comments);
                    subjectControls.controls[selectedIndex].get('fullName').setValue(person.personId);
                    subjectControls.controls[selectedIndex]
                        .get('approximateDateOfBirth')
                        .setValue(person.approximateDateOfBirth ? 'true' : '');
                }
            });
        }
    }

    deleteSub(index: any) {
        const subjectsControl = this.quickFindForm.get('subjects') as FormArray;
        subjectsControl.controls.splice(index, 1);
        this.subjectsControlCount = subjectsControl.controls.length;
    }

    clearValidators() {
        Object.keys(this.quickFindForm.controls).forEach((controlName) => {
            const control = this.quickFindForm.get(controlName);
            control?.clearValidators();
            control?.updateValueAndValidity();
        });
        const subjectControls = this.quickFindForm.get('subjects') as FormArray;
        subjectControls.controls.forEach((element) => {
            const subNameControl = element.get('fullName');
            subNameControl.clearValidators();
            subNameControl.updateValueAndValidity();
        });
    }
}
