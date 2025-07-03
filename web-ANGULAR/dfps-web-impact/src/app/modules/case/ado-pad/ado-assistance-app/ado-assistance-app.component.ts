import { Component, OnInit, Inject, OnDestroy } from '@angular/core';
import {
    DataTable,
    DfpsCommonValidators,
    DfpsConfirmComponent,
    DfpsFormValidationDirective,
    DirtyCheck,
    FormUtils,
    FormValue,
    FormParams,
    FormService,
    NavigationService,
    Reports,
    ReportService,
    SET,
    ENVIRONMENT_SETTINGS,
    NarrativeService,
} from 'dfps-web-lib';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { select, Store } from '@ngrx/store';
import { CookieService } from 'ngx-cookie-service';
import { ActivatedRoute, Router } from '@angular/router';
import { CaseService } from '../../service/case.service';

@Component({
    selector: 'app-ado-assistance-app',
    templateUrl: './ado-assistance-app.component.html',
    styleUrls: ['./ado-assistance-app.component.css'],
})
export class AdoAssistanceAppComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    adoptionAssistanceAppForm: FormGroup;
    adoptionAssistanceAppDisplayRes: any;
    yesNo: any;
    eventId: any;
    constructor(
        private navigationService: NavigationService,
        private fb: FormBuilder,
        private caseService: CaseService,
        private router: Router,
        private route: ActivatedRoute,
        private cookieService: CookieService,
        public store: Store<{ dirtyCheck: DirtyCheck }>,
        @Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any
    ) {
        super(store);
    }
    ngOnInit(): void {
        this.navigationService.setTitle('Adoption Assistance Application Page');
        this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
        this.eventId = this.route.snapshot.paramMap.get('eventId') ? this.route.snapshot.paramMap.get('eventId') : 0;
        this.yesNo = [
            { label: 'Yes', value: 'Y' },
            { label: 'No', value: 'N' },
        ];
        this.intializeScreen();
    }

    intializeScreen() {
        this.createForm();
        this.caseService.displayAdoptionAssistanceApp(this.eventId).subscribe((res) => {
            this.adoptionAssistanceAppDisplayRes = res;
        });
    }

    createForm() {
        this.adoptionAssistanceAppForm = this.fb.group({
            isInManagedCvs: [''],
            inLicensedManagingConservatorShip: [''],
            otherManagingConservatorShip: [''],
            agencyName: [''],
            agencyAddress: [''],
        });
    }

    personDetail() {
        const url = `${this.environmentSettings.impactP2WebUrl}/case/personDetail/${this.adoptionAssistanceAppDisplayRes.idPerson}?isMPSEnvironment=false`;
        window.location.href = url;
    }
}
