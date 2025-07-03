import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import {
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    ENVIRONMENT_SETTINGS,
    NavigationService,
    SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CaretakerRes } from '../../model/caretakerDetail';
import { ResourceService } from '../../service/resource.service';
import { HelpService } from 'app/common/impact-help.service';

@Component({
    templateUrl: './caretaker-detail.component.html',
})
export class CareTakerDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    constructor(
        private formBuilder: FormBuilder,
        private modalService: BsModalService,
        private route: ActivatedRoute,
        private resourceService: ResourceService,
        private navigationService: NavigationService,
        private helpService: HelpService,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }
    careTakerDetailReqForm: FormGroup;
    resourceId: any;
    caretakerId: any;
    caretakerRes: CaretakerRes;
    hideSaveButton = false;
    hideDeleteButton = false;

    ngOnInit() {
        this.navigationService.setTitle('Caretaker Detail');
        this.helpService.loadHelp('Resource');
        this.createForm();
        const routeParams = this.route.snapshot.paramMap;
        if (routeParams) {
            this.resourceId = routeParams.get('resourceId');
            this.caretakerId = routeParams.get('caretakerId');
        }
        this.intializeScreen();
    }

    createForm() {
        this.careTakerDetailReqForm = this.formBuilder.group({
            homeMaritalStatus: ['', Validators.required],
            firstName: ['', Validators.required],
            middleName: [''],
            lastName: ['', Validators.required],
            dateOfBirth: ['', [Validators.required, DfpsCommonValidators.validateDate]],
            gender: ['', Validators.required],
            race: ['', Validators.required],
            ethnicity: ['', Validators.required],
        });
    }

    intializeScreen() {
        this.resourceService.getCareTakerDetail(this.resourceId, this.caretakerId).subscribe((response) => {
            this.caretakerRes = response;
            this.loadService(true, this.caretakerRes);
            this.determinePageMode(this.caretakerRes);
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        });
    }

    determinePageMode(caretakerRes: CaretakerRes) {
        if (caretakerRes.pageMode === 'VIEW') {
            this.hideDeleteButton = true;
            this.hideSaveButton = true;
            this.careTakerDetailReqForm.disable();

        } else if (caretakerRes.pageMode === 'NEW') {
            this.hideDeleteButton = true;
        }
    }

    loadService(reload: boolean, caretakerRes: CaretakerRes) {
        if (caretakerRes) {
            if (reload) {
                this.careTakerDetailReqForm.setValue({
                    homeMaritalStatus: caretakerRes.resource.maritalStatus,
                    firstName: caretakerRes.caretaker.firstName,
                    middleName: caretakerRes.caretaker.middleName,
                    lastName: caretakerRes.caretaker.lastName,
                    dateOfBirth: caretakerRes.caretaker.dob,
                    gender: caretakerRes.caretaker.sex,
                    race: caretakerRes.caretaker.race,
                    ethnicity: caretakerRes.caretaker.ethnicity
                });
            } else {
                this.careTakerDetailReqForm.patchValue({
                    homeMaritalStatus: caretakerRes.resource.maritalStatus,
                    firstName: caretakerRes.caretaker.firstName,
                    middleName: caretakerRes.caretaker.middleName,
                    lastName: caretakerRes.caretaker.lastName,
                    dateOfBirth: caretakerRes.caretaker.dob,
                    gender: caretakerRes.caretaker.sex,
                    race: caretakerRes.caretaker.race,
                    ethnicity: caretakerRes.caretaker.ethnicity
                });
            }
        }
    }

    doSave() {
        if (this.careTakerDetailReqForm.value.homeMaritalStatus !== this.caretakerRes.resource.maritalStatus) {
            const initialState = {
                title: 'Care Taker Detail',
                message: 'Updating the home marital status applies to both caretakers',
                showCancel: true,
            };
            const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md', initialState });
            (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
                if (result === true) {
                    this.saveCaretakerDetail();
                }
            });
        } else {
            this.saveCaretakerDetail();
        }
    }

    saveCaretakerDetail() {
        if (this.validateFormGroup(this.careTakerDetailReqForm)) {
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            const updatecaretakerDetail = {
                homeMaritalStatus: this.careTakerDetailReqForm.value.homeMaritalStatus,
                firstName: this.careTakerDetailReqForm.value.firstName,
                middleName: this.careTakerDetailReqForm.value.middleName,
                lastName: this.careTakerDetailReqForm.value.lastName,
                dob: this.careTakerDetailReqForm.value.dateOfBirth,
                sex: this.careTakerDetailReqForm.value.gender,
                race: this.careTakerDetailReqForm.value.race,
                ethnicity: this.careTakerDetailReqForm.value.ethnicity
            }
            const payLoad = Object.assign(this.caretakerRes.caretaker, updatecaretakerDetail);
            this.resourceService.saveCaretakerDetail(this.resourceId, payLoad).subscribe((response) => {
                if (response) {
                    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                    window.location.href =
                        this.environmentSettings.impactP2WebUrl
                        + '/resource/caretaker/caretakerinfo?idResource='
                        + this.resourceId;
                }
            });
        }
    }

    doDelete() {
        const maritalStatus = this.careTakerDetailReqForm.value.homeMaritalStatus;
        const initialState = {
            title: 'Caretaker Detail',
            message: 'Are you sure you want to delete this information?',
            showCancel: true
        };
        const modal = this.modalService.show(DfpsConfirmComponent, {
            class: 'modal-md', initialState
        });
        (modal.content as DfpsConfirmComponent).onClose.subscribe(result => {
            if (result === true) {
                this.resourceService.deleteCaretakerDetail(this.resourceId, this.caretakerId, maritalStatus).subscribe(
                    res => {
                        if (res) {
                            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                            window.location.href =
                                this.environmentSettings.impactP2WebUrl +
                                '/resource/caretaker/caretakerinfo?idResource=' + this.resourceId;
                        }
                    }
                );
            }
        });
    }
}
