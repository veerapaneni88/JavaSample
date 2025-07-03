import { Component, OnDestroy, OnInit, HostListener } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import {
    DfpsFormValidationDirective,
    NavigationService,
    DirtyCheck,
    SET,
    FormUtils, 
    DfpsConfirmComponent
} from 'dfps-web-lib';
import { CaseService } from '../../service/case.service';
import { ResourcePhoneRes } from '../../model/phoneDetail';
import { PhoneDetailValidators } from './phone-detail.validator';
import { BsModalService } from 'ngx-bootstrap/modal';


@Component({
    selector: 'phone-detail',
    templateUrl: './phone-detail.component.html'
})

export class PhoneDetailComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

    phoneDetailForm: FormGroup;
    displayPhoneDetail: ResourcePhoneRes;
    resourceId: number;
    resourcePhoneId: number;
    phoneDeleteButtonHide = true;

    constructor(private navigationService: NavigationService,
        private formBuilder: FormBuilder,
        private caseService: CaseService,
        private route: ActivatedRoute,
        private router: Router,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
    }

    createForm() {
        this.phoneDetailForm = this.formBuilder.group(
            {
                phoneType: ['', Validators.required],
                phoneNumber: ['', [Validators.required]],
                phoneExtension: [''],
                comments: [''],
                lastUpdateDate: ['']
            }, {
            validators: [
                PhoneDetailValidators.phoneNumberPattern,
                PhoneDetailValidators.phoneNumberExtPattern
            ]
        }
        );
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Phone Detail');
        const routeParams = this.route.snapshot.paramMap;
        if (routeParams) {
            this.resourceId = Number(routeParams.get('resourceId'));
            this.resourcePhoneId = Number(routeParams.get('resourcePhoneId'));
        }
        this.createForm();
        this.intializeScreen();
    }

    intializeScreen() {
        this.caseService.getPhoneList(this.resourceId, this.resourcePhoneId).subscribe((response) => {
            this.displayPhoneDetail = response;
            this.loadPhoneDetail();
            this.hideDeleteButton();
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        });
    }

    loadPhoneDetail() {
        if (this.displayPhoneDetail && this.displayPhoneDetail.resourcePhone) {
            this.phoneDetailForm.patchValue({
                phoneType: this.displayPhoneDetail.resourcePhone.phoneType,
                phoneNumber: this.displayPhoneDetail.resourcePhone.phoneNumber,
                phoneExtension: this.displayPhoneDetail.resourcePhone.phoneExtension,
                comments: this.displayPhoneDetail.resourcePhone.comments,
                lastUpdateDate: this.displayPhoneDetail.resourcePhone.lastUpdateDate
            });

            
            if (this.displayPhoneDetail.resourcePhone.phoneType === '01') {
                FormUtils.disableFormControlStatus(this.phoneDetailForm, ['phoneType']);
            }
        }
    }

    hideDeleteButton() {
        if (this.resourcePhoneId !== 0 && (this.displayPhoneDetail.resourcePhone &&
            this.displayPhoneDetail.resourcePhone.phoneType !== '01')) {
            this.phoneDeleteButtonHide = false;
        }
    }

    delete() {
        if (this.displayPhoneDetail.resourcePhone && this.displayPhoneDetail.resourcePhone.phoneType === '01') {
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
                        .deletePhoneList(this.displayPhoneDetail.resourcePhone.resourceId,
                            this.displayPhoneDetail.resourcePhone.resourcePhoneId)
                        .subscribe(res => {
                            this.router.navigate(['/case/home-information/FAD/' + this.resourceId]);
                        });
                }
            });
        }
    }

    doSave() {
        if (this.validateFormGroup(this.phoneDetailForm)) {
            this.savePhoneDetail();
        }
    }

    savePhoneDetail() {
        const payload = Object.assign(this.displayPhoneDetail.resourcePhone || {}, this.phoneDetailForm.value);
        payload.resourceId = this.resourceId;
        payload.resourcePhoneId = this.resourcePhoneId ? this.resourcePhoneId : 0;
        this.caseService.savePhoneDetail(this.resourceId, payload).subscribe(
            response => {
                this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                const currentUrl = this.router.url.split('/phone')[0]
                this.router.navigate([currentUrl]);
                const phoneDetail = true;
                localStorage.setItem('phoneDetailSave', JSON.stringify(phoneDetail));
            }
        );
    }

    @HostListener('window:popstate' || 'window:hashchange', ['$event'])
    onPopState(event) {
        localStorage.setItem('phoneList', JSON.stringify({}));
        const phoneLocalStorge = JSON.parse(localStorage.getItem('phoneList'));
        phoneLocalStorge.focusItem = this.router.url;
        localStorage.setItem('backFrom', JSON.stringify({ naivgatedBackFrom: 'phoneDetail' }));
        localStorage.setItem('phoneList', JSON.stringify(phoneLocalStorge));
    }

}