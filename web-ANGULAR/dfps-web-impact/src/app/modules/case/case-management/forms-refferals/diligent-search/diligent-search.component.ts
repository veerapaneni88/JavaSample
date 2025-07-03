import { Component, ElementRef, OnDestroy, OnInit, ViewChild, Inject } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DiligentSearchService } from '@case/service/forms-referral.service';
import { Store } from '@ngrx/store';
import { SearchService } from '@shared/service/search.service';
import { HelpService } from 'app/common/impact-help.service';
import {
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    DropDown,
    ENVIRONMENT_SETTINGS,
    FormUtils,
    SET,
} from 'dfps-web-lib';
import { DiligentSearchValidator } from './diligent-search.validator';
import { DiligentSearchDto, DiligentSearchChild } from '@case/model/formsReferrals';
import { element } from 'protractor';
import { BsModalService } from 'ngx-bootstrap/modal';

@Component({
    selector: 'diligent-search',
    templateUrl: './diligent-search.component.html',
})
export class DiligentSearchComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    @ViewChild('errors') errorElement: ElementRef;
    diligentSearchForm: FormGroup;
    diligentSearchData: any;
    isReadOnly = false;
    isEditMode = false;
    personList: DropDown[];
    originalPersonList: DropDown[];
    motherPersonList: DropDown[];
    fatherPersonList: DropDown[];
    childrenControlCount = 0;
    disableDeleteButton = true;
    disableSearchButton = false;
    diligentSearchId: any;
    motherControlCount = 0;
    fatherControlCount = 0;
    previousValue: Map<string, string> = new Map();
    caseWorkerOptions: any;
    motherLocateOptions: any;
    sections: string[] = ['children', 'mothers', 'fathers'];
    totalCount = 0;
    isNewUsing = false;
    fatherOfChildList: DropDown[];
    fatherOfChildControlCount = 0;

    constructor(
        private formBuilder: FormBuilder,
        private route: ActivatedRoute,
        private router: Router,
        private searchService: SearchService,
        private helpService: HelpService,
        private diligentSearchService: DiligentSearchService,
        private modalService: BsModalService,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }

    ngOnInit(): void {
        this.caseWorkerOptions = [
            { label: 'Yes. Complete name, email, and phone for supervisor.', value: 'Y' },
            { label: 'No. Complete name, email, and phone for caseworker and supervisor.', value: 'N' },
        ];
        this.motherLocateOptions =[
            {label: 'I do not know this person’s location, and I need FINDRS to conduct a diligent search to locate the subject and possible relatives.', value: 'F'},
            {label: 'I know this person’s location and only need to locate relatives of this person.', value: 'L'},
            {label: 'This parent’s information is unknown.', value: 'U'}
        ];
        this.helpService.loadHelp('Case');
        this.createForm();
        this.initializeScreen();
    }

    createForm() {
        this.diligentSearchForm = this.formBuilder.group(
            {
                requestersName: [''],
                requestersPhone: ['', [DfpsCommonValidators.validateMaxId, Validators.maxLength(30)]],
                requestersPhoneExtension: ['', [DfpsCommonValidators.validateMaxId, Validators.maxLength(4)]],
                unitRole: [''],
                email: [''],
                caseId: [''],
                requestersRegion: [''],
                requestersCounty: [''],
                caseworker: [''],
                childWithoutPlacement: [''],
                locateRelatives: [''],
                caseWorkerName: [''],
                caseWorkerPhone: [''],
                caseWorkerExtension: [''],
                caseWorkerEmail: [''],
                supervisorName: [''],
                supervisorPhone: [''],
                supervisorExtension: [''],
                supervisorEmail: [''],
                children: new FormArray([]),
                mothers: new FormArray([]),
                fathers: new FormArray([]),
                buttonClicked: [''],
                requestersPersonId: [''],
                supervisorPersonId: [''],
                caseWorkerPersonId: [''],
                causeNumber: [''],
                formsReferralsId: [''],
            },
            {
                validators: [DiligentSearchValidator.validateDiligentSearchInfo()],
            }
        );
    }

    initializeScreen() {
        this.isNewUsing = this.route.snapshot.queryParamMap.get('isNewUsing')
            ? this.route.snapshot.queryParamMap.get('isNewUsing') === 'true'
            : false;
        const diligentSearchId = this.route.snapshot.paramMap.get('id') ? this.route.snapshot.paramMap.get('id') : '0';
        this.diligentSearchId = this.isNewUsing ? '0' : diligentSearchId;
        this.diligentSearchService.getDiligentSearch(diligentSearchId).subscribe((res) => {
            this.diligentSearchData = res;
            if (res.subjects) {
                if (this.isNewUsing) {
                    res.diligentSearch.diligentSearchId = '0';
                    res.diligentSearch.formsReferralsId = '0';
                    res.diligentSearch.eventStatus = 'PROC';
                    res.diligentSearch.mothers.forEach((subject) => (subject.diligentSearchPersonId = '0'));
                    res.diligentSearch.fathers.forEach((subject) => (subject.diligentSearchPersonId = '0'));
                    res.diligentSearch.children.forEach((subject) => (subject.diligentSearchPersonId = '0'));
                }
                this.originalPersonList = res.subjects.map((obj) => new DropDown(obj.fullName, obj.personId));
                this.personList = Array.from(this.originalPersonList);
                this.motherPersonList = Array.from(this.originalPersonList);
                this.fatherPersonList = Array.from(this.originalPersonList);
                this.personList.sort((a, b) => a.decode.localeCompare(b.decode));
                this.motherPersonList.sort((a, b) => a.decode.localeCompare(b.decode));
                this.fatherPersonList.sort((a, b) => a.decode.localeCompare(b.decode));
                this.totalCount = this.originalPersonList.length;
                this.fatherOfChildList = new Array();
            }
            this.pupulateFormData();
            this.populatePullBackData();
            this.getPageMode();
        });

        this.isEditMode = true;
    }
    pupulateFormData() {
        this.diligentSearchForm.patchValue({ caseId: this.diligentSearchData.diligentSearch.caseId });
        this.diligentSearchForm.patchValue({ requestersName: this.diligentSearchData.diligentSearch.requestersName });
        this.diligentSearchForm.patchValue({ requestersPhone: this.diligentSearchData.diligentSearch.requestersPhone });
        this.diligentSearchForm.patchValue({
            requestersPersonId: this.diligentSearchData.diligentSearch.requestersPersonId,
        });
        this.diligentSearchForm.patchValue({
            formsReferralsId: this.diligentSearchData.diligentSearch.formsReferralsId,
        });
        this.diligentSearchForm.patchValue({
            requestersPhoneExtension: this.diligentSearchData.diligentSearch.requestersPhoneExtension,
        });
        this.diligentSearchForm.patchValue({ unitRole: this.diligentSearchData.diligentSearch.unitRole });
        this.diligentSearchForm.patchValue({ email: this.diligentSearchData.diligentSearch.email });

        this.diligentSearchForm.patchValue({
            requestersRegion: this.diligentSearchData.diligentSearch.requestersRegion,
        });
        this.diligentSearchForm.patchValue({
            requestersCounty: this.diligentSearchData.diligentSearch.requestersCounty,
        });

        if (
            this.diligentSearchId === '0' &&
            !this.isNewUsing &&
            (this.searchService.getFormData() === undefined || this.searchService.getFormData() === null)
        ) {
            this.diligentSearchForm.patchValue({
                caseworker: 'Y',
            });
            this.updateCaseWorkerDetails();
            this.buildSubjects();
        } else {
            // Case Worker
            this.diligentSearchForm.patchValue({ caseworker: this.diligentSearchData.diligentSearch.caseworker });
            this.diligentSearchForm.patchValue({
                caseWorkerName: this.diligentSearchData.diligentSearch.caseWorkerName,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerPhone: this.diligentSearchData.diligentSearch.caseWorkerPhone,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerExtension: this.diligentSearchData.diligentSearch.caseWorkerExtension,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerEmail: this.diligentSearchData.diligentSearch.caseWorkerEmail,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerPersonId: this.diligentSearchData.diligentSearch.caseWorkerPersonId,
            });
            // Supervisor
            this.diligentSearchForm.patchValue({
                supervisorName: this.diligentSearchData.diligentSearch.supervisorName,
            });
            this.diligentSearchForm.patchValue({
                supervisorPhone: this.diligentSearchData.diligentSearch.supervisorPhone,
            });
            this.diligentSearchForm.patchValue({
                supervisorExtension: this.diligentSearchData.diligentSearch.supervisorExtension,
            });
            this.diligentSearchForm.patchValue({
                supervisorEmail: this.diligentSearchData.diligentSearch.supervisorEmail,
            });
            this.diligentSearchForm.patchValue({
                supervisorPersonId: this.diligentSearchData.diligentSearch.supervisorPersonId,
            });
            this.diligentSearchForm.patchValue({ causeNumber: this.diligentSearchData.diligentSearch.causeNumber });

            // Purpose of Request
            this.diligentSearchForm.patchValue({
                locateRelatives: this.diligentSearchData.diligentSearch.locateRelatives,
            });
            this.diligentSearchForm.patchValue({
                childWithoutPlacement: this.diligentSearchData.diligentSearch.childWithoutPlacement,
            });

            if (this.searchService.getFormData() !== undefined && this.searchService.getFormData() !== null) {
                this.populateSubDetails(this.searchService.getFormData());
            } else {
                this.populateSubDetails(this.diligentSearchData.diligentSearch);
            }
            this.disableDeleteButton = this.diligentSearchData.diligentSearch.eventStatus !== 'PROC';
            this.disableSearchButton = this.diligentSearchData.diligentSearch.eventStatus === 'COMP';
            this.sections.forEach((section) => {
                const subjectControls = this.diligentSearchForm.get(section) as FormArray;
                subjectControls.controls.forEach((element) => {
                    let fullNameId = 'fullName';
                    if (section === 'mothers') fullNameId = 'motherFullName';
                    else if (section === 'fathers') fullNameId = 'fatherFullName';
                    const selPersonId = element.get(fullNameId).value;
                    this.filterNames(section, selPersonId);
                });
            });
            this.updateFatherOfChildList();
        }
    }

    buildSubjects() {
        const chldrenControl = this.diligentSearchForm.get('children') as FormArray;
        chldrenControl.push(this.createChildrenForm());
        const mothersControl = this.diligentSearchForm.get('mothers') as FormArray;
        mothersControl.push(this.createParentForm('mothers'));
        mothersControl.controls[0].get('motherLocOption').setValue('L');
        const fathersControl = this.diligentSearchForm.get('fathers') as FormArray;
        fathersControl.push(this.createParentForm('fathers'));
        fathersControl.controls[0].get('fatherLocOption').setValue('L');
        const childControl = fathersControl.controls[0].get('fatherOfChildren') as FormArray;
        childControl.push(this.createFatherOfChildForm());
    }
    createFatherOfChildForm() {
        return this.formBuilder.group({
            childPersonId: [''],
            diligentSearchChildId: [''],
        });
    }

    populateSubDetails(diligentSearch: DiligentSearchDto) {
        if (diligentSearch.children !== null && diligentSearch.children.length > 0) {
            const list = diligentSearch.children;
            list.forEach((i, currentIndex) => {
                this.addchildren();
                const subjectControls = this.diligentSearchForm.get('children') as FormArray;
                const val = subjectControls.controls[currentIndex];
                const person = list[currentIndex];
                this.populateSectionDetails('children', val, person, true);
            });
        }

        if (diligentSearch.mothers !== null && diligentSearch.mothers.length > 0) {
            const list = diligentSearch.mothers;
            list.forEach((i, currentIndex) => {
                this.addParent('mothers');
                const subjectControls = this.diligentSearchForm.get('mothers') as FormArray;
                const val = subjectControls.controls[currentIndex];
                const person = list[currentIndex];
                this.populateSectionDetails('mothers', val, person, true);
            });
        }

        if (diligentSearch.fathers !== null && diligentSearch.fathers.length > 0) {
            const list = diligentSearch.fathers;
            list.forEach((i, currentIndex) => {
                this.addParent('fathers');
                const subjectControls = this.diligentSearchForm.get('fathers') as FormArray;
                const val = subjectControls.controls[currentIndex];
                const person = list[currentIndex];
                this.populateSectionDetails('fathers', val, person, true);
            });
        }
    }

    selectStaff(section: string) {
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        let returnUrl;
        if (this.diligentSearchId) {
            returnUrl = 'case/case-management/forms-referral/diligent-search/' + this.diligentSearchId;
        } else {
            returnUrl = 'case/case-management/forms-referral/diligent-search/0';
        }
        this.searchService.setSearchSource(section);
        this.searchService.setInvokingPage('DiligentSearch');
        this.searchService.setFormData(this.diligentSearchForm.value);
        this.searchService.setFormContent(this.diligentSearchData);
        this.searchService.setReturnUrl(returnUrl);
        this.router.navigate(
            ['case/case-management/forms-referral/diligent-search/' + this.diligentSearchId + '/staffsearch'],
            {
                skipLocationChange: false,
            }
        );
    }

    populatePullBackData() {
        if (this.searchService.getFormData()) {
            this.diligentSearchForm.patchValue(this.searchService.getFormData());
        }
        if (this.searchService.getSelectedStaff()) {
            if (this.searchService.getSearchSource() === 'caseWorker') {
                this.diligentSearchForm.patchValue({ caseWorkerName: this.searchService.getSelectedStaff().name });
                this.diligentSearchForm.patchValue({
                    caseWorkerPersonId: this.searchService.getSelectedStaff().personId,
                });
                this.diligentSearchForm.patchValue({ caseWorkerEmail: this.searchService.getSelectedStaff().email });
                this.diligentSearchForm.patchValue({
                    caseWorkerPhone: this.searchService.getSelectedStaff().workPhone,
                });
                this.diligentSearchForm.patchValue({ caseWorkerExtension: this.searchService.getSelectedStaff().ext });
            } else if (this.searchService.getSearchSource() === 'supervisor') {
                this.diligentSearchForm.patchValue({ supervisorName: this.searchService.getSelectedStaff().name });
                this.diligentSearchForm.patchValue({
                    supervisorPersonId: this.searchService.getSelectedStaff().personId,
                });
                this.diligentSearchForm.patchValue({ supervisorEmail: this.searchService.getSelectedStaff().email });
                this.diligentSearchForm.patchValue({
                    supervisorPhone: this.searchService.getSelectedStaff().workPhone,
                });
                this.diligentSearchForm.patchValue({ supervisorExtension: this.searchService.getSelectedStaff().ext });
            } else {
                this.diligentSearchForm.patchValue({ requestersName: this.searchService.getSelectedStaff().name });
                this.diligentSearchForm.patchValue({
                    requestersPersonId: this.searchService.getSelectedStaff().personId,
                });
                this.diligentSearchForm.patchValue({ email: this.searchService.getSelectedStaff().email });
                this.diligentSearchForm.patchValue({
                    requestersPhone: this.searchService.getSelectedStaff().workPhone,
                });
                this.diligentSearchForm.patchValue({
                    requestersPhoneExtension: this.searchService.getSelectedStaff().ext,
                });
            }
        }
    }

    getChildren(diligentSearchForm): any {
        return diligentSearchForm.get('children').controls;
    }

    addchildren() {
        const childrenControl = this.diligentSearchForm.get('children') as FormArray;
        if (childrenControl.controls.length < this.totalCount) {
            this.buildChildren();
        } else {
            this.diligentSearchForm.get('Add').disable();
        }
        this.childrenControlCount = childrenControl.controls.length;
    }

    buildChildren() {
        const childrenControl = this.diligentSearchForm.get('children') as FormArray;
        childrenControl.push(this.createChildrenForm());
    }

    createChildrenForm() {
        return new FormGroup({
            fullName: new FormControl(''),
            prevSelPerson: new FormControl(''),
            city: new FormControl(''),
            state: new FormControl(''),
            county: new FormControl(''),
            dateOfBirth: new FormControl(''),
            approximateDateOfBirth: new FormControl(false),
            ssn: new FormControl(''),
            ethnicity: new FormControl(''),
            gender: new FormControl(''),
            personId: new FormControl(''),
            diligentSearchPersonId: new FormControl(''),
        });
    }

    createParentForm(section: string) {
        if (section === 'mothers') {
            return new FormGroup({
                motherDlgntSearchPersonId: new FormControl(''),
                motherFullName: new FormControl(''),
                motherPrevSelPerson: new FormControl(''),
                motherAddressLine1: new FormControl(''),
                motherAddressLine2: new FormControl(''),
                motherCity: new FormControl(''),
                motherState: new FormControl(''),
                motherCounty: new FormControl(''),
                motherZip: new FormControl(''),
                motherPhone: new FormControl(''),
                motherDateOfBirth: new FormControl(''),
                motherApproxDateOfBirth: new FormControl(false),
                motherSsn: new FormControl(''),
                motherEthnicity: new FormControl(''),
                motherPersonId: new FormControl(''),
                motherLocUnkown: new FormControl(false),
                motherLocKnown: new FormControl(false),
                motherInfoUnknown: new FormControl(false),
                motherComments: new FormControl(''),
                motherPersonAddressId: new FormControl(''),
                motherLocOption: new FormControl('')
            });
        } else
            return new FormGroup({
                fatherDlgntSearchPersonId: new FormControl(''),
                fatherFullName: new FormControl(''),
                fatherPrevSelPerson: new FormControl(''),
                fatherAddressLine1: new FormControl(''),
                fatherAddressLine2: new FormControl(''),
                fatherCity: new FormControl(''),
                fatherState: new FormControl(''),
                fatherCounty: new FormControl(''),
                fatherZip: new FormControl(''),
                fatherPhone: new FormControl(''),
                fatherDateOfBirth: new FormControl(''),
                fatherApproxDateOfBirth: new FormControl(false),
                fatherSsn: new FormControl(''),
                fatherEthnicity: new FormControl(''),
                fatherPersonId: new FormControl(''),
                fatherLocUnkown: new FormControl(false),
                fatherLocKnown: new FormControl(false),
                fatherInfoUnknown: new FormControl(false),
                fatherComments: new FormControl(''),
                fatherPersonAddressId: new FormControl(''),
                fatherOfChildren: new FormArray([]),
                fatherLocOption: new FormControl('')
            });
    }

    updateSubDetails(selectedIndex: any, section: string) {
        const subjects = this.diligentSearchData.subjects;
        const subjectControls = this.diligentSearchForm.get(section) as FormArray;
        const val = subjectControls.controls[selectedIndex];
        let fullNameId = 'fullName';
        if (section === 'mothers') fullNameId = 'motherFullName';
        else if (section === 'fathers') fullNameId = 'fatherFullName';
        const selectedPerson = val.get(fullNameId).value;
        let previousSelValue = '';

        if (section === 'children')
            previousSelValue = subjectControls.controls[selectedIndex].get('prevSelPerson').value;
        if (section === 'mothers')
            previousSelValue = subjectControls.controls[selectedIndex].get('motherPrevSelPerson').value;
        if (section === 'fathers')
            previousSelValue = subjectControls.controls[selectedIndex].get('fatherPrevSelPerson').value;

        if (!selectedPerson) {
            
            let selLocOption ;
            if (section === 'mothers'){
                selLocOption = subjectControls.controls[selectedIndex].get('motherLocOption').value;                
            } else if (section === 'fathers'){
                selLocOption = subjectControls.controls[selectedIndex].get('fatherLocOption').value;                
            }
            val.reset();
            if (section === 'mothers'){
               subjectControls.controls[selectedIndex].get('motherLocOption').setValue(selLocOption);
            }else if (section === 'fathers'){                
                subjectControls.controls[selectedIndex].get('fatherLocOption').setValue(selLocOption);
            }
        } else {
            if (section === 'children') {
                subjects.forEach((i, currentIndex) => {
                    if (subjects[currentIndex].personId === selectedPerson) {
                        const person = subjects[currentIndex];
                        subjectControls.controls[selectedIndex].get('ethnicity').setValue(person.ethnicity);
                        subjectControls.controls[selectedIndex].get('gender').setValue(person.gender);
                        subjectControls.controls[selectedIndex].get('city').setValue(person.birtCity);
                        subjectControls.controls[selectedIndex].get('state').setValue(person.birthState);
                        subjectControls.controls[selectedIndex].get('county').setValue(person.birthCounty);
                        subjectControls.controls[selectedIndex].get('dateOfBirth').setValue(person.dateOfBirth);
                        subjectControls.controls[selectedIndex].get('ssn').setValue(person.ssn);
                        subjectControls.controls[selectedIndex].get('personId').setValue(person.personId);
                        subjectControls.controls[selectedIndex].get('fullName').setValue(person.personId);
                        subjectControls.controls[selectedIndex].get('prevSelPerson').setValue(person.personId);
                        subjectControls.controls[selectedIndex]
                            .get('approximateDateOfBirth')
                            .setValue(person.approximateDateOfBirth ? 'true' : '');
                    }
                });
                this.updateFatherOfChildList();
            } else {
                subjects.forEach((i, currentIndex) => {
                    if (subjects[currentIndex].personId === selectedPerson) {
                        const person = subjects[currentIndex];
                        this.populateSectionDetails(section, subjectControls.controls[selectedIndex], person, false);
                    }
                });
            }
        }
        this.updateNamesDropDown(selectedPerson, section, previousSelValue);
        this.previousValue.set(section, selectedPerson);
    }

    updateFatherOfChildList() {
        this.fatherOfChildList = new Array();
        const sectionControl = this.diligentSearchForm.get('children') as FormArray;
        sectionControl.controls.forEach((element) => {
            const childPersonId = element.get('fullName').value;
            if (childPersonId !== '' && childPersonId !== null && childPersonId !== undefined) {
                const previousDropDown = this.originalPersonList.find((item) => item.code === childPersonId);
                this.fatherOfChildList.push(previousDropDown);
            }
        });
    }

    updateNamesDropDown(selectedPerson: any, section: any, previousSelValue: any) {
        const previousDropDown = this.originalPersonList.find((item) => item.code === previousSelValue);

        if (previousDropDown !== undefined) {
            const selExistsInSection = this.sectionHasDropDown(section, previousDropDown);
            if (selExistsInSection === false) {
                if (section === 'children') {
                    this.motherPersonList.push(previousDropDown);
                    this.fatherPersonList.push(previousDropDown);
                } else if (section === 'mothers') {
                    this.personList.push(previousDropDown);
                    this.fatherPersonList.push(previousDropDown);
                } else if (section === 'fathers') {
                    this.motherPersonList.push(previousDropDown);
                    this.personList.push(previousDropDown);
                }
                this.personList.sort((a, b) => a.decode.localeCompare(b.decode));
                this.motherPersonList.sort((a, b) => a.decode.localeCompare(b.decode));
                this.fatherPersonList.sort((a, b) => a.decode.localeCompare(b.decode));
            }
        }
        if (selectedPerson) {
            this.filterNames(section, selectedPerson);
        }
    }

    updateNamesDropDownForDelete(selectedPerson: any, section: any) {
        const previousDropDown = this.originalPersonList.find((item) => item.code === selectedPerson);

        if (previousDropDown !== undefined) {
            const selExistsInSection = this.sectionHasDropDown(section, previousDropDown);
            if (selExistsInSection === false) {
                if (section === 'children') {
                    this.motherPersonList.push(previousDropDown);
                    this.fatherPersonList.push(previousDropDown);
                } else if (section === 'mothers') {
                    this.personList.push(previousDropDown);
                    this.fatherPersonList.push(previousDropDown);
                } else if (section === 'fathers') {
                    this.motherPersonList.push(previousDropDown);
                    this.personList.push(previousDropDown);
                }
                this.personList.sort((a, b) => a.decode.localeCompare(b.decode));
                this.motherPersonList.sort((a, b) => a.decode.localeCompare(b.decode));
                this.fatherPersonList.sort((a, b) => a.decode.localeCompare(b.decode));
            }
        }
    }

    filterNames(section: any, selectedPerson: any) {
        if (section === 'children') {
            this.motherPersonList = Array.from(this.motherPersonList.filter((item) => item.code !== selectedPerson));
            this.fatherPersonList = Array.from(this.fatherPersonList.filter((item) => item.code !== selectedPerson));
        } else if (section === 'mothers') {
            this.personList = Array.from(this.personList.filter((item) => item.code !== selectedPerson));
            this.fatherPersonList = Array.from(this.fatherPersonList.filter((item) => item.code !== selectedPerson));
        } else if (section === 'fathers') {
            this.motherPersonList = Array.from(this.motherPersonList.filter((item) => item.code !== selectedPerson));
            this.personList = Array.from(this.personList.filter((item) => item.code !== selectedPerson));
        }
    }

    sectionHasDropDown(section: any, previousDropDown: DropDown): boolean {
        let result = false;
        const subjectControls = this.diligentSearchForm.get(section) as FormArray;
        subjectControls.controls.forEach((element) => {
            let fullNameId = 'fullName';
            if (section === 'mothers') {
                fullNameId = 'motherFullName';
            } else if (section === 'fathers') {
                fullNameId = 'fatherFullName';
            }

            const subNameControl = element.get(fullNameId);
            if (subNameControl.value === previousDropDown.code) result = true;
        });
        return result;
    }

    deleteSub(index: any, section: string) {
        let fullNameId = 'fullName';
        if (section === 'mothers') {
            fullNameId = 'motherFullName';
        } else if (section === 'fathers') {
            fullNameId = 'fatherFullName';
        }
        const sectionControl = this.diligentSearchForm.get(section) as FormArray;
        const selectedPerson = sectionControl.controls[index].get(fullNameId).value;
        const nameControl = sectionControl.controls[index].get(fullNameId);
        if (nameControl.hasError('required')) {
            delete nameControl.errors['required'];
        }
        nameControl.updateValueAndValidity({onlySelf: true});
        sectionControl.controls.splice(index, 1);
        if (section === 'children') this.childrenControlCount = sectionControl.controls.length;
        else if (section === 'mothers') this.motherControlCount = sectionControl.controls.length;
        else this.fatherControlCount = sectionControl.controls.length;
        this.updateNamesDropDownForDelete(selectedPerson, section);
        this.updateFatherOfChildList();
    }

    getCustomStyles() {
        return 'height: 130px';
    }

    getParentDetails(diligentSearchForm: any, section: string) {
        return diligentSearchForm.get(section).controls;
    }
    getFatherOfChildDetails(diligentSearchForm: any, section: string, index: any) {
        return diligentSearchForm.get('fathers').controls[index].get(section).controls;
    }
    addParent(parent: string) {
        const parentControl = this.diligentSearchForm.get(parent) as FormArray;
        this.buildParent(parent);
        if (parent === 'mothers') this.motherControlCount = parentControl.controls.length;
        else this.fatherControlCount = parentControl.controls.length;
    }

    buildParent(parent: string) {
        const parentControl = this.diligentSearchForm.get(parent) as FormArray;
        parentControl.push(this.createParentForm(parent));

        if (parent === 'fathers') {
            const childControl = parentControl.controls[parentControl.length - 1].get('fatherOfChildren') as FormArray;
            childControl.push(this.createFatherOfChildForm());
            this.fatherOfChildControlCount = childControl.length;
            parentControl.controls[parentControl.length - 1].get('fatherLocOption').setValue('L');
        } else {
            parentControl.controls[parentControl.length - 1].get('motherLocOption').setValue('L');
        }
    }

    addFatherOfChild(fatherIndex: any, index: any) {
        const fathersControl = this.diligentSearchForm.get('fathers') as FormArray;
        const childControl = fathersControl.controls[fatherIndex].get('fatherOfChildren') as FormArray;
        childControl.push(this.createFatherOfChildForm());
        this.fatherOfChildControlCount = childControl.length;
    }

    delete() {
        this.diligentSearchForm.patchValue({ buttonClicked: 'delete' });
        this.diligentSearchService.deleteDiligentSearch(this.diligentSearchId).subscribe((res) => {
            window.location.href =
                this.environmentSettings.impactP2WebUrl + '/case/caseManagement/formsReferralsList.get';
        });
    }

    complete() {
        this.diligentSearchForm.patchValue({ buttonClicked: 'complete' });
        this.clearValidators();
        const subjectsControl = this.diligentSearchForm.get('fathers') as FormArray;
        let error = false;

        if (this.validateFormGroup(this.diligentSearchForm)) {
            subjectsControl.controls.forEach((element) => {
                const fatherOfChildren = element.get('fatherOfChildren') as FormArray;
                const child = fatherOfChildren.controls[0].get('childPersonId');
                if (child.value === '' || child.value === null || child.value === undefined) {
                    error = true;
                    this.showModal('Please select Father of Child in Father\'s Information.', false);
                    return;
                }
            });
            if (error) return;
            this.saveDiligentSearch(true, false);
        }
    }
    save() {
        this.diligentSearchForm.patchValue({ buttonClicked: 'save' });
        this.clearValidators();
        if (this.validateFormGroup(this.diligentSearchForm)) {
            this.saveDiligentSearch(false, false);
        }
    }
    saveAndStay() {
        this.diligentSearchForm.patchValue({ buttonClicked: 'saveAndStay' });
        this.clearValidators();
        if (this.validateFormGroup(this.diligentSearchForm)) {
            this.saveDiligentSearch(false, true);
        }
    }

    saveDiligentSearch(isComplete: boolean, isStay: boolean) {
        this.updateNames();
        const payload = Object.assign(this.diligentSearchData.diligentSearchId || {}, this.diligentSearchForm.value);
        payload.diligentSearchId = this.diligentSearchId;
        if (payload.caseworker === '' || payload.caseworker === undefined) {
            payload.caseworker = null;
        }
        if (isComplete) payload.actionType = 'COMP';
        else payload.actionType = 'PROC';
        this.populateDtosFromFields(payload);
        this.diligentSearchService.saveDiligentSearch(payload).subscribe((res) => {
            if (isStay) {
                const url = 'case/case-management/forms-referral/diligent-search/' + res.diligentSearchId;
                this.diligentSearchId = res.diligentSearchId;
                window.location.href = url;
            } else {
                window.location.href =
                    this.environmentSettings.impactP2WebUrl + '/case/caseManagement/formsReferralsList.get';
            }
        });
        return null;
    }

    showModal(text: string, isDisplay: boolean) {
        const initialState = {
            title: 'Diligent Search Form',
            message: text,
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
            class: 'modal-md modal-dialog-centered',
            initialState,
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {});
    }

    updateNames() {
        this.sections.forEach((section) => {
            const subjectControls = this.diligentSearchForm.get(section) as FormArray;
            if (section === 'children') {
                subjectControls.controls.forEach((element) => {
                    const subNameControl = element.get('fullName');
                    const name = this.findNameById(this.personList, subNameControl.value);
                    subNameControl.setValue(name);
                });
            }
        });
    }

    populateDtosFromFields(payload: any) {
        this.sections.forEach((section) => {
            const subjectControls = this.diligentSearchForm.get(section) as FormArray;
            if (section === 'mothers') {
                subjectControls.controls.forEach((element, index) => {
                    payload.mothers[index].personId = element.get('motherPersonId').value;
                    payload.mothers[index].personAddressId = element.get('motherPersonAddressId').value;
                    let name = this.findNameById(this.motherPersonList, element.get('motherFullName').value);
                    if (
                        name === undefined &&
                        (element.get('motherLocUnkown').value ||
                            element.get('motherLocKnown').value ||
                            element.get('motherInfoUnknown').value)
                    ) {
                        name = '0';
                        payload.mothers[index].personId = '0';
                        payload.mothers[index].personAddressId = '0';
                    }
                    payload.mothers[index].fullName = name;
                    payload.mothers[index].addressLine1 = element.get('motherAddressLine1').value;
                    payload.mothers[index].addressLine2 = element.get('motherAddressLine2').value;
                    payload.mothers[index].city = element.get('motherCity').value;
                    payload.mothers[index].state = element.get('motherState').value;
                    payload.mothers[index].county = element.get('motherCounty').value;
                    payload.mothers[index].zip = element.get('motherZip').value;
                    payload.mothers[index].phone = element.get('motherPhone').value;
                    payload.mothers[index].dateOfBirth = element.get('motherDateOfBirth').value;
                    payload.mothers[index].approximateDateOfBirth = element.get('motherApproxDateOfBirth').value;
                    payload.mothers[index].ssn = element.get('motherSsn').value;
                    payload.mothers[index].ethnicity = element.get('motherEthnicity').value;
                    if(element.get('motherLocOption').value === 'F')
                        payload.mothers[index].locatePersonOrRelative = true;
                    else if(element.get('motherLocOption').value === 'L')
                        payload.mothers[index].locateRelative = true;
                    else if(element.get('motherLocOption').value === 'U')
                        payload.mothers[index].personInfoUnknown = true;

                    payload.mothers[index].comments = element.get('motherComments').value;
                    payload.mothers[index].diligentSearchPersonId = element.get('motherDlgntSearchPersonId').value;
                });
            } else if (section === 'fathers') {
                subjectControls.controls.forEach((element, index) => {
                    payload.fathers[index].personId = element.get('fatherPersonId').value;
                    payload.fathers[index].personAddressId = element.get('fatherPersonAddressId').value;
                    let name = this.findNameById(this.fatherPersonList, element.get('fatherFullName').value);
                    if (
                        name === undefined &&
                        (element.get('fatherLocUnkown').value ||
                            element.get('fatherLocKnown').value ||
                            element.get('fatherInfoUnknown').value)
                    ) {
                        name = '0';
                        payload.fathers[index].personId = '0';
                        payload.fathers[index].personAddressId = '0';
                    }
                    payload.fathers[index].fullName = name;
                    payload.fathers[index].addressLine1 = element.get('fatherAddressLine1').value;
                    payload.fathers[index].addressLine2 = element.get('fatherAddressLine2').value;
                    payload.fathers[index].city = element.get('fatherCity').value;
                    payload.fathers[index].state = element.get('fatherState').value;
                    payload.fathers[index].county = element.get('fatherCounty').value;
                    payload.fathers[index].zip = element.get('fatherZip').value;
                    payload.fathers[index].phone = element.get('fatherPhone').value;
                    payload.fathers[index].dateOfBirth = element.get('fatherDateOfBirth').value;
                    payload.fathers[index].approximateDateOfBirth = element.get('fatherApproxDateOfBirth').value;
                    payload.fathers[index].ssn = element.get('fatherSsn').value;
                    payload.fathers[index].ethnicity = element.get('fatherEthnicity').value;
                    if(element.get('fatherLocOption').value === 'F')
                        payload.fathers[index].locatePersonOrRelative = true;
                    else if(element.get('fatherLocOption').value === 'L')
                        payload.fathers[index].locateRelative = true;
                    else if(element.get('fatherLocOption').value === 'U')
                        payload.fathers[index].personInfoUnknown = true;

                    payload.fathers[index].comments = element.get('fatherComments').value;
                    payload.fathers[index].diligentSearchPersonId = element.get('fatherDlgntSearchPersonId').value;
                });
            }
        });
    }

    findNameById(dropDownArray: DropDown[], value: any) {
        for (const element of dropDownArray) {
            if (element.code === value) return element.decode;
        }
    }

    getPageMode() {
        if (this.diligentSearchData.pageMode === 'NEW' || this.isNewUsing) {
            this.isEditMode = true;
        } else if (this.diligentSearchData.pageMode === 'EDIT') {
            this.isEditMode = true;
        } else if (this.diligentSearchData.pageMode === 'VIEW') {
            this.isEditMode = false;
            this.disableAllFields();

            this.sections.forEach((section) => {
                (this.diligentSearchForm.get(section) as FormArray).controls.forEach((val: FormGroup) => {
                    const fields = Object.keys(val.controls);
                    fields.forEach((field) => {
                        val.get(field).disable();
                    });
                });
            });
        }
    }

    disableAllFields() {
        FormUtils.disableFormControlStatus(this.diligentSearchForm, [
            'requestersName',
            'requestersPhone',
            'requestersPhoneExtension',
            'supervisorName',
            'supervisorPhone',
            'supervisorExtension',
            'caseWorkerName',
            'caseWorkerPhone',
            'caseWorkerExtension',
            'requestersRegion',
            'requestersCounty',
            'causeNumber',
            'locateRelatives',
            'childWithoutPlacement',
            'cps',
            'cpsNytd',
            'accounting',
            'legal',
            'aps',
            'emr',
            'approximateDateOfBirth',
            'caseworker',
        ]);
    }

    clearValidators() {
        Object.keys(this.diligentSearchForm.controls).forEach((controlName) => {
            const control = this.diligentSearchForm.get(controlName);
            control?.clearValidators();
            control?.updateValueAndValidity();
        });
        this.sections.forEach((section) => {
            let locUnkownId: any;
            let locKnownId: any;
            let infoUnknownId: any;
            if (section === 'mothers') {
                locUnkownId = 'motherLocUnkown';
                locKnownId = 'motherLocKnown';
                infoUnknownId = 'motherInfoUnknown';
            } else if (section === 'fathers') {
                locUnkownId = 'fatherLocUnkown';
                locKnownId = 'fatherLocKnown';
                infoUnknownId = 'fatherInfoUnknown';
            }
            const subjectControls = this.diligentSearchForm.get(section) as FormArray;
            let fullNameId = 'fullName';
            if (section === 'mothers') fullNameId = 'motherFullName';
            else if (section === 'fathers') fullNameId = 'fatherFullName';
            subjectControls.controls.forEach((element) => {
                const subNameControl = element.get(fullNameId);
                subNameControl.clearValidators();
                subNameControl.updateValueAndValidity();
                if (section === 'fathers' || section === 'mothers') {
                    element.get(locUnkownId).clearValidators();
                    element.get(locUnkownId).updateValueAndValidity();
                }
            });
        });
    }

    updateCaseWorkerDetails() {
        if (this.diligentSearchForm.controls['caseworker'].value === 'Y') {
            this.diligentSearchForm.patchValue({
                caseWorkerName: this.diligentSearchForm.get('requestersName').value,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerEmail: this.diligentSearchForm.get('email').value,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerPhone: this.diligentSearchForm.get('requestersPhone').value,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerExtension: this.diligentSearchForm.get('requestersPhoneExtension').value,
            });
            this.diligentSearchForm.patchValue({
                caseWorkerPersonId: this.diligentSearchForm.get('requestersPersonId').value,
            });
        } else {
            this.diligentSearchForm.patchValue({ caseWorkerName: '' });
            this.diligentSearchForm.patchValue({ caseWorkerEmail: '' });
            this.diligentSearchForm.patchValue({ caseWorkerPhone: '' });
            this.diligentSearchForm.patchValue({ caseWorkerExtension: '' });
            this.diligentSearchForm.patchValue({ caseWorkerPersonId: '' });
        }
    }

    populateSectionDetails(section: string, control: AbstractControl, person: any, updateChkBoxOptions: boolean) {
        if (section === 'mothers') {
            control.get('motherFullName').setValue(person.personId);
            control.get('motherPrevSelPerson').setValue(person.personId);
            control.get('motherAddressLine1').setValue(person.addressLine1);
            control.get('motherAddressLine2').setValue(person.addressLine2);
            control.get('motherCity').setValue(person.city);
            control.get('motherState').setValue(person.state);
            control.get('motherCounty').setValue(person.county);
            control.get('motherZip').setValue(person.zip);
            control.get('motherPhone').setValue(person.phone);
            control.get('motherDateOfBirth').setValue(person.dateOfBirth);
            control.get('motherApproxDateOfBirth').setValue(person.approximateDateOfBirth ? 'true' : '');
            control.get('motherSsn').setValue(person.ssn);
            control.get('motherEthnicity').setValue(person.ethnicity);
            if(person.personId>0)
                control.get('motherPersonId').setValue(person.personId);
            if (updateChkBoxOptions) {
                if(person.locatePersonOrRelative === true)
                    control.get('motherLocOption').setValue('F');
                else if(person.locateRelative === true)
                    control.get('motherLocOption').setValue('L');
                else if(person.personInfoUnknown === true)
                    control.get('motherLocOption').setValue('U');

                control.get('motherDlgntSearchPersonId').setValue(person.diligentSearchPersonId);
                control.get('motherComments').setValue(person.comments);
            }
            control.get('motherPersonAddressId').setValue(person.personAddressId);
        } else if (section === 'fathers') {
            control.get('fatherFullName').setValue(person.personId);
            control.get('fatherPrevSelPerson').setValue(person.personId);
            control.get('fatherAddressLine1').setValue(person.addressLine1);
            control.get('fatherAddressLine2').setValue(person.addressLine2);
            control.get('fatherCity').setValue(person.city);
            control.get('fatherState').setValue(person.state);
            control.get('fatherCounty').setValue(person.county);
            control.get('fatherZip').setValue(person.zip);
            control.get('fatherPhone').setValue(person.phone);
            control.get('fatherDateOfBirth').setValue(person.dateOfBirth);
            control.get('fatherApproxDateOfBirth').setValue(person.approximateDateOfBirth ? 'true' : '');
            control.get('fatherSsn').setValue(person.ssn);
            control.get('fatherEthnicity').setValue(person.ethnicity);
            if(person.personId>0)
                control.get('fatherPersonId').setValue(person.personId);
            if (updateChkBoxOptions) {
                if(person.locatePersonOrRelative === true)
                    control.get('fatherLocOption').setValue('F');
                else if(person.locateRelative === true)
                    control.get('fatherLocOption').setValue('L');
                else if(person.personInfoUnknown === true)
                    control.get('fatherLocOption').setValue('U');
                control.get('fatherDlgntSearchPersonId').setValue(person.diligentSearchPersonId);
                control.get('fatherComments').setValue(person.comments);
            }
            control.get('fatherPersonAddressId').setValue(person.personAddressId);


            if (person.fatherOfChildren !== null && person.fatherOfChildren.length > 0) {
                person.fatherOfChildren.forEach((child, index) => {
                    const childControl = control.get('fatherOfChildren') as FormArray;
                    if (childControl.length - 1 < index) childControl.push(this.createFatherOfChildForm());
                    childControl.controls[index].get('childPersonId').setValue(child.childPersonId);
                    childControl.controls[index].get('diligentSearchChildId').setValue(child.diligentSearchChildId);
                    this.fatherOfChildControlCount = childControl.length;
                });
            }
        } else {
            control.get('fullName').setValue(person.personId);
            control.get('city').setValue(person.birtCity);
            control.get('state').setValue(person.birthState);
            control.get('county').setValue(person.birthCounty);
            control.get('dateOfBirth').setValue(person.dateOfBirth);
            control.get('approximateDateOfBirth').setValue(person.approximateDateOfBirth ? 'true' : '');
            control.get('personId').setValue(person.personId);
            control.get('ssn').setValue(person.ssn);
            control.get('ethnicity').setValue(person.ethnicity);
            control.get('gender').setValue(person.gender);
            control.get('diligentSearchPersonId').setValue(person.diligentSearchPersonId);
            control.get('prevSelPerson').setValue(person.personId);
        }
    }
}
