import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import {
    DataTable, FormValidationError, NavigationService, DirtyCheck, DfpsCommonValidators,
    DfpsFormValidationDirective, DfpsConfirmComponent, ModalMessage
} from 'dfps-web-lib';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { NeiceSearchDisplay, NeiceTransmittalList, NeiceTransmittalResponse } from '../model/NeiceTransmittalList';
import { NeiceTransmittalsService } from '../service/neice-transmittals.service';
import { NeiceTransmittalListValidator } from './neice-transmittal-list.validator';
import { BsModalService } from 'ngx-bootstrap/modal';

@Component({
    templateUrl: './neice-transmittal-list.component.html'
})

export class NeiceTransmittalListComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {
    neiceTransmittalListForm: FormGroup;
    neiceTransmittalListRequest: NeiceTransmittalList;
    displaySearchResponseObs: Observable<NeiceSearchDisplay>;
    displaySearchResponse: NeiceSearchDisplay;
    neiceTransmittalResponse: NeiceTransmittalResponse;
    displaySearchResults = false;
    //userInfo: UserInfo;
    //userInfoSubscription: Subscription;
    tableBody: any[];
    tableColumn: any[];
    urlKey: any[];
    urlPath: string;
    dataTable: DataTable;
    @ViewChild('results') resultsEle: ElementRef;
    searchButtonClicked = false;
    validationErrors: FormValidationError[] = [];
    validationErrorsLength = 0;
    currentDate = new Date();
    readonly DEFAULT_PAGE_SIZE = 10;

    constructor(
        private formBuilder: FormBuilder,
        private neiceTransmittalsService: NeiceTransmittalsService,
        private route: Router,
        private navigationService: NavigationService,
        //private authService: AuthService,
        private modalService: BsModalService,
        public store: Store<{ dirtyCheck: DirtyCheck }>
    ) {
        super(store);
        this.setUserData();
    }

    setUserData() {
        this.navigationService.setUserData({ null: null });
    }

    ngOnInit(): void {
        this.navigationService.setTitle('NEICE Transmittal List');
        this.createForm();
        //this.getUserInfo();
        this.navigationService.loadMenuSubject.next(true);
        this.displaySearchResponseObs = this.neiceTransmittalsService.displayTransmittalListSearch();
        this.displaySearchResponseObs.subscribe((displayNeiceTramittalList) => {
            if (displayNeiceTramittalList) {
                this.displaySearchResponse = displayNeiceTramittalList;
            }
        });

        if (localStorage.getItem('backFrom') === 'NeiceTransmittalSummary') {
            this.doSearchForBack();
        } else {
            localStorage.removeItem('NeiceTransmittalListRequest');
            //this.doSearch();
        }
    }

    /*getUserInfo() {
        this.userInfoSubscription = this.authService.getUserInfo().subscribe((userInfo) => {
            this.userInfo = userInfo;
            const loginUserId = userInfo.loginUserId;
            this.neiceTransmittalListForm.get('assignedTo').setValue(loginUserId);
        });
    }*/

    createForm() {
        this.neiceTransmittalListForm = this.formBuilder.group({
            dateFrom: ['', [DfpsCommonValidators.validateDate, NeiceTransmittalListValidator.validateFromFutureDate]],
            dateTo: ['', [DfpsCommonValidators.validateDate, NeiceTransmittalListValidator.validateToFutureDate]],
            transmittalType: [''],
            //transmittalStatus: ['10'],
            transmittalStatus: [''],
            neiceHomeStudyId: [''],
            priority: [''],
            homeStudyType: [''],
            //incomingOutgoing: ['I'],
            incomingOutgoing: [''],
            transmittingState: [''],
            oldestChildName: [''],
            assignedTo: [''],
            rejectHold: [''],
            jurisdictionState: [''],
        }, {
            validators: [DfpsCommonValidators.compareDates('dateFrom', 'dateTo')]
        });
    }

    doSearchForBack() {
        localStorage.removeItem('backFrom');
        this.searchButtonClicked = true;
        this.displaySearchResults = false;
        this.neiceTransmittalListRequest = this.neiceTransmittalListForm.getRawValue();
        this.initializeDataTable();
        this.loadData();
        this.setFormValues();
    }

    doSearch() {
        localStorage.removeItem('backFrom');
        localStorage.removeItem('NeiceTransmittalListRequest');
        this.searchButtonClicked = true;
        this.displaySearchResults = false;
        if (this.validateFormGroup(this.neiceTransmittalListForm) && this.neiceTransmittalListForm.value) {
            this.neiceTransmittalListRequest = this.neiceTransmittalListForm.getRawValue();
            this.initializeDataTable();
            this.loadData();
        }
    }

    loadData() {
        const neiceTransmittalListCopy: any = JSON.parse(localStorage.getItem('NeiceTransmittalListRequest'));
        if (neiceTransmittalListCopy) {
            this.searchTransmittalList(neiceTransmittalListCopy);
        }
        else {
            this.searchTransmittalList(this.neiceTransmittalListRequest);
        }
    }

    searchButtonResultOnBlur() {
        this.searchButtonClicked = false;
    }

    searchTransmittalList(neiceTransmittalListCopy: any) {
        this.neiceTransmittalsService
            .searchNeiceTransmittalList(neiceTransmittalListCopy)
            .subscribe((response: NeiceTransmittalResponse) => {
                this.neiceTransmittalResponse = response;
                this.displaySearchResults = true;
                this.dataTable = {
                    tableColumn: this.tableColumn,
                    tableBody: this.neiceTransmittalResponse ? this.neiceTransmittalResponse.neiceTransmittalResults : [],
                    isPaginator: true,
                    isFilterable: true
                };
                if (this.searchButtonClicked) {
                    setTimeout(() => {
                        this.resultsEle.nativeElement.focus();
                    }, 0);
                }
            });
        localStorage.removeItem('NeiceTransmittalListRequest');
        localStorage.setItem('NeiceTransmittalListRequest', JSON.stringify(neiceTransmittalListCopy));
    }

    initializeDataTable() {
        this.tableColumn = [
            { field: 'transmitState', header: 'Transmit State', sortable: true, width: 160, isFilterable: true },
            { field: 'jurisdiction', header: 'Jurisdiction', sortable: true, width: 160, isFilterable: true },
            { field: 'dateReceived', header: 'Date Received', isDate: true, sortable: true, width: 190, isFilterable: true },
            {
                field: 'transmittalType', header: 'Transmittal Type', sortable: true, isFilterable: true,
                handleClick: true,
                width: 250,
                url: `${NeiceTransmittalsService.NEICE_TRANSMITTAL_SUMMARY_URL}`,
                urlParams: ['idNeiceTransmittal', 'incomingOutgoing'],
                uniqueLinkKey: 'idNeiceTransmittal'
            },
            { field: 'oldestChildName', header: 'Oldest Child Name', sortable: true, width: 300, isFilterable: true },
            { field: 'assignedToFullName', header: 'Assigned To', sortable: true, width: 300, isFilterable: true },
            {
                field: 'transmittalStatus', header: 'Transmittal Status', sortable: true, width: 190, isFilterable: true,
                handleClick: true,
                url: '#',
                urlParams: ['']
            },
            { field: 'typeOfCare', header: 'Type of Care', sortable: true, width: 200, isFilterable: true },
            { field: 'homeStudyType', header: 'Home Study Type', sortable: true, width: 280, isFilterable: true },
            { field: 'neiceHomeStudyId', header: 'Neice Home Study ID', sortable: true, width: 220, isFilterable: true },
            { field: 'purpose', header: 'Purpose', sortable: true, width: 300, isFilterable: true },
            {
                field: 'shortComments', header: 'Comments', sortable: true, width: 300, isFilterable: true,
                handleClick: true,
                url: '#',
                urlParams: ['']
            },
            { field: 'rejectHoldDisplay', header: 'Reject/ Hold', sortable: true, width: 170, isFilterable: true },
            { field: 'otherTransmittals', header: 'Other Transmittals', sortable: true, width: 250, isFilterable: true },
            { field: 'priority', header: 'Priority', sortable: true, width: 150, isFilterable: true }
        ];
        this.dataTable = {
            tableColumn: this.tableColumn,
            isPaginator: true,
            isFilterable: true
        };
    }

    handleTransmittalListLinkClick(event: any) {
        switch (event.tableColumn.field) {
            case 'transmittalStatus':
                this.showTransmittalListMessage('Transmittal Status', `Status: ${event.tableRow.transmittalStatus}`, [{ content: `Message: ${event.tableRow.transmittalError}` }]);
                break;
            case 'shortComments':
                this.showTransmittalListMessage('Comments', event.tableRow.comments);
                break;
            case 'transmittalType':
                if (event.tableRow.idNeiceTransmittal) {
                    this.route.navigate([event.link]);
                }
                break;
        }
    }

    showTransmittalListMessage(fieldName: string, msg: string, msgList?: ModalMessage[]) {
        const initialState = {
            title: fieldName,
            message: msg,
            showCancel: false,
            messageList: msgList
        };
        this.modalService.show(DfpsConfirmComponent, {
            class: 'modal-md modal-dialog-centered',
            initialState,
        });
    }

    clear() {
        localStorage.removeItem('NeiceTransmittalListRequest');
        window.location.reload();
    }

    ngOnDestroy() {
        //this.userInfoSubscription.unsubscribe();
    }

    setFormValues() {
        const neiceTransmittalListReq: any = JSON.parse(localStorage.getItem('NeiceTransmittalListRequest'));
        Object.keys(neiceTransmittalListReq).forEach((key) => {
            if (this.neiceTransmittalListForm.get(key)) {
                this.neiceTransmittalListForm.get(key).setValue(neiceTransmittalListReq[key]);
            }
        })
    }
}
