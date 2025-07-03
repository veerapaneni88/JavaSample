import { Component, Inject, LOCALE_ID, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DfpsFormValidationDirective, DirtyCheck, ENVIRONMENT_SETTINGS, NavigationService, SET } from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import { ResourceServiceRes } from '../../model/resource';
import { ResourceService } from '../../service/resource.service';
import { AddServiceLevelDetailValidator } from './add-service-level-detail.validator';
import { HelpService } from 'app/common/impact-help.service';

@Component({
    templateUrl: './add-service-level-detail.component.html'
})

export class AddServiceLevelDetailComponent extends DfpsFormValidationDirective implements OnInit {
    addServiceLevelDetailForm: FormGroup;
    resourceId: any;
    resourceServiceRes: ResourceServiceRes;
    latestEffectiveDate = null;
    effectiveDate: any;

    constructor(
        private formBuilder: FormBuilder,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private resourceService: ResourceService,
        private navigationService: NavigationService,
        private helpService: HelpService,
        @Inject(LOCALE_ID) private locale: string,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        private cookieService: CookieService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.resourceId = this.activatedRoute.snapshot.paramMap.get('resourceId');
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Add Service Level Detail');
        this.helpService.loadHelp('Resource');
        this.createForm();
        this.intializeScreen();
    }

    intializeScreen() {
        this.resourceService.getServiceByAreaRes(this.resourceId).subscribe(res => {
            this.resourceServiceRes = res;
        });
    }

    private getLatestEffectiveDate() {
        if (this.cookieService.get('facilityLocList') || sessionStorage.getItem('facilityLocList')) {
            const facilityLocList: any[] = this.environmentSettings.environmentName === 'Local'
                ? JSON.parse(this.cookieService.get('facilityLocList')) :
                JSON.parse(sessionStorage.getItem('facilityLocList'));
            const effectiveDates: any = [];
            if (facilityLocList) {
                (facilityLocList).forEach(e => {
                    effectiveDates.push(e.dtFlocEffect);
                });
                this.latestEffectiveDate = effectiveDates.reduce((a, b) => (a > b ? a : b)).toString();
            }
            this.cookieService.delete('facilityLocList');
            sessionStorage.removeItem('facilityLocList');
        }
    }

    doContinue() {
        if (this.validateFormGroup(this.addServiceLevelDetailForm)) {
            this.effectiveDate = this.addServiceLevelDetailForm.controls.effectiveDate.value;
            localStorage.setItem('serviceEffectiveDate', this.effectiveDate);
            this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
            this.router.navigate(['/resource/facility-detail/service-level-detail/' + this.resourceId + '/0'],
                { queryParams: { effectiveDate: this.effectiveDate } });
        }
    }

    createForm() {
        this.getLatestEffectiveDate();
        this.addServiceLevelDetailForm = this.formBuilder.group(
            {
                effectiveDate: [''],
                locale: [this.locale],
                latestDate: [this.latestEffectiveDate]
            },
            {
                validators: [
                    AddServiceLevelDetailValidator.validateEffectiveDate()
                ],
            }
        );
    }

}
