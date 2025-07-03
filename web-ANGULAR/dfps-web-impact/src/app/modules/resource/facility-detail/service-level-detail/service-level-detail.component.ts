import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { DataTable, DfpsFormValidationDirective, DirtyCheck, ENVIRONMENT_SETTINGS, NavigationService, SET, FormUtils } from 'dfps-web-lib';
import { FacilityServiceLevelRes, ResourceServiceRes, FacilityLoc } from '../../model/resource';
import { ResourceService } from '../../service/resource.service';
import { HelpService } from 'app/common/impact-help.service';

@Component({
    templateUrl: './service-level-detail.component.html'
})
export class ServiceLevelDetailComponent extends DfpsFormValidationDirective implements OnInit {
    serviceLevelDetailForm: FormGroup;
    resourceId: any;
    flocId: any;
    resourceServiceRes: ResourceServiceRes;
    facilityServiceLevelRes: FacilityServiceLevelRes;
    facilityLocsOriginal: FacilityLoc[] = [];
    effectiveDate: any;
    tableColumn: any[];
    getData: any;

    slDatatable: DataTable;
    constructor(
        private formBuilder: FormBuilder,
        private activatedRoute: ActivatedRoute,
        private resourceService: ResourceService,
        private navigationService: NavigationService,
        private helpService: HelpService,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.resourceId = this.activatedRoute.snapshot.paramMap.get('resourceId');
        this.flocId = this.activatedRoute.snapshot.paramMap.get('flocId');
    }

    ngOnInit(): void {
        this.navigationService.setTitle('Service Level Detail');
        this.helpService.loadHelp('Resource');
        this.createForm();
        localStorage.setItem('idResource', this.resourceId);
        this.effectiveDate = localStorage.getItem('serviceEffectiveDate');
        this.intializeScreen();
        localStorage.removeItem('serviceEffectiveDate');
    }

    intializeScreen() {
        this.resourceService.getFacilityServiceLevel(this.resourceId, this.flocId, this.effectiveDate).
            subscribe(facilityServiceLevelRes => {
                this.facilityServiceLevelRes = facilityServiceLevelRes;
                if (new Date(this.facilityServiceLevelRes.endDate) > new Date()) {
                    this.facilityServiceLevelRes.endDate = '';
                }
                if (this.effectiveDate) {
                    this.facilityServiceLevelRes.effectiveDate = this.effectiveDate;
                }
                this.facilityServiceLevelRes.facilityLocs.forEach(values => {
                    if (values.active) {
                        values.status = 'active';
                    } else if (values.hold) {
                        values.status = 'hold';
                    } else if (values.na) {
                        values.status = 'na';
                    }
                })
                this.facilityLocsOriginal = JSON.parse(JSON.stringify(this.facilityServiceLevelRes.facilityLocs));
            });

        this.getData = [
            { label: 'Active', value: 'active' },
            { label: 'Hold', value: 'hold' },
            { label: 'N/A', value: 'na' },
        ];
    }

    getDisableOption(data: any): any {
        // console.log('data', data);
        const facilityLoc = this.facilityLocsOriginal.find(fLoc => data.level === fLoc.level);
        return facilityLoc.status === 'active' || facilityLoc.status === 'hold'
            ? 'na' : facilityLoc.status === 'na' ? null : '';
    }

    doSave() {

        this.facilityServiceLevelRes.facilityLocs.forEach(values => {
            if (values.status === 'active') {
                values.active = true;
                values.hold = false;
                values.na = false;
            } else if (values.status === 'hold') {
                values.hold = true;
                values.active = false;
                values.na = false;
            } else if (values.status === 'na') {
                values.na = true;
                values.hold = false;
                values.active = false;
            }
        })

        if (this.effectiveDate) {
            this.facilityServiceLevelRes.effectiveDate = this.effectiveDate;
        }
        const payload = Object.assign(this.facilityServiceLevelRes);
        this.resourceService.saveFacilityServiceLevel(payload).subscribe(
            response => {
                if (response) {
                    this.facilityServiceLevelRes = response;
                    sessionStorage.setItem('cacheKey', response.cacheKey);
                    window.location.href =
                        this.environmentSettings.impactP2WebUrl +
                        '/resource/facilityDetail/display?idResource=' + this.resourceId;
                    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
                }
            }
        );
    }

    createForm() {
        this.serviceLevelDetailForm = this.formBuilder.group(
            {
                facilityLoc: []
            }

        );
    }



}
