import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  DataTable,
  DfpsConfirmComponent,
  DfpsFormValidationDirective,
  DirtyCheck, ENVIRONMENT_SETTINGS, NavigationService,
  SET
} from 'dfps-web-lib';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ResourceCharacterRes, ResourceServiceRes } from '../model/resource';
import { ResourceService } from '../service/resource.service';
import { HelpService } from 'app/common/impact-help.service';

@Component({
  templateUrl: './services-by-area.component.html'
})
export class ServicesByAreaComponent extends DfpsFormValidationDirective implements OnInit {

  serviceByAreaForm: FormGroup;
  resourceServiceRes: ResourceServiceRes;
  resourceCharacterRes: ResourceCharacterRes;
  resourceCharcterDto: any;
  tableColumn: any[];
  serviceByAreaDataTable: DataTable;
  chracteristicsDataTable: DataTable;

  displaySaveButton = false;
  resourceId: any;
  serviceId: any;
  selectedServicesByArea: any;
  selectedCharacterstics: any;

  hideServiceAddButton = false;
  hideCharAddButton = false;

  hideServiceDeleteButton = false;
  hideCharDeleteButton = false;

  confirmMessage: any;
  characteristicsTitle: any;
  chractersticsCheck: boolean;
  servicesCheck: boolean;

  constructor(
    private activatedRoute: ActivatedRoute,
    private route: Router,
    private navigationService: NavigationService,
    private formBuilder: FormBuilder,
    private modalService: BsModalService,
    private resourceService: ResourceService,
    private helpService: HelpService,
    public store: Store<{ dirtyCheck: DirtyCheck }>
  ) {
    super(store);
    this.resourceId = this.activatedRoute.snapshot.paramMap.get('resourceId');
    this.serviceId = this.activatedRoute.snapshot.paramMap.get('serviceId');
  }

  ngOnInit(): void {
    this.navigationService.setTitle('Services by Area');
    this.helpService.loadHelp('Resource');
    this.serviceByAreaForm = this.formBuilder.group({
    });
    localStorage.setItem('idResource', this.resourceId);
    this.navigationService.setUserDataValue('firstLevelTab', 'Resource');
    this.navigationService.setUserDataValue('idResource', this.resourceId);
    this.intializeScreen();
  }

  intializeScreen() {
    this.resourceService.getServiceByAreaRes(this.resourceId).subscribe(res => {
      this.resourceServiceRes = res;
      if (this.resourceServiceRes) {
        this.servicesCheck = this.resourceServiceRes.resourceService.length > 0 ? true : false;
      }
      this.loadServicesByAreaDataTable();
      this.showHideButtons();

      setTimeout(() => {
        const radio1 = document.getElementById('p-tableRadioButton-0');
        if (radio1) {
          radio1.click();
          this.setPageModes();
        }
      }, 1000);
    }
    )
  }

  showHideButtons() {
    if (this.resourceServiceRes.pageMode === 'VIEW') {
      this.hideServiceAddButton = true;
      this.hideCharAddButton = true;
      this.hideServiceDeleteButton = true;
      this.hideCharDeleteButton = true;
    }
  }

  loadServicesByAreaDataTable() {
    this.tableColumn = [
      { field: 'categoryName', header: 'Category', sortable: false, width: 150 },
      {
        field: 'serviceName',
        header: 'Service',
        desc: null,
        width: 150,
        isLink: true,
        url: '/resource/services-by-area/:resourceId/:id',
        urlParams: ['resourceId', 'id']
      },
      { field: 'contracted', header: 'C', sortable: false, width: 30, isTick: true },
      { field: 'programName', header: 'Program', sortable: false, width: 75 },
      { field: 'regionName', header: 'Region', sortable: false, width: 75 },
      { field: 'countyName', header: 'County', sortable: false, width: 75 },
      { field: 'countyPartial', header: 'Partial County', sortable: false, width: 75 },
      { field: 'incomeBased', header: 'Income Based', sortable: false, width: 75 },
      { field: 'stateName', header: 'State', sortable: false, width: 50 }
    ]

    this.serviceByAreaDataTable = {
      tableBody: this.resourceServiceRes.resourceService,
      tableColumn: this.tableColumn,
      isSingleSelect: true,
      selectedRows: [1],
      isPaginator: this.resourceServiceRes && this.resourceServiceRes.resourceService &&
        this.resourceServiceRes.resourceService.length > 10
    };
  }

  loadCharactersticsDataLoad(resourceCharacters: any) {
    this.tableColumn = [
      {
        field: 'characterName',
        header: 'Characteristics',
        desc: null,
        width: 120,
        isLink: true,
        url: '/resource/services-by-area/:resourceId/:resourceServiceId/client-characteristics/:id',
        urlParams: ['resourceId', 'resourceServiceId', 'id']
      },
      { field: 'maleMinAge', header: 'Male Min Age', sortable: false, width: 100 },
      { field: 'maleMaxAge', header: 'Male Max Age', sortable: false, width: 100 },
      { field: 'femaleMinAge', header: 'Female Min Age', sortable: false, width: 100 },
      { field: 'femaleMaxAge', header: 'Female Max Age', sortable: false, width: 100 },
    ]

    this.chracteristicsDataTable = {
      tableBody: resourceCharacters,
      tableColumn: this.tableColumn,
      isSingleSelect: true,
      isPaginator: resourceCharacters && resourceCharacters.length > 20 ? true : false
    };
  }

  displayCharacterstics(event) {
    this.setPageModes();
    this.selectedCharacterstics = null;
    if (event) {
      this.hideCharDeleteButton = true;
      this.chracteristicsDataTable = null;
      this.selectedServicesByArea = event;
      this.characteristicsTitle = event.serviceName;
      this.resourceService.getCharacteristcisRes(this.resourceId, event.id).subscribe((response) => {
        if (response) {
          this.chractersticsCheck = response.length > 0 ? true : false;
        }
        this.loadCharactersticsDataLoad(response);
      });
    }
  }

  getSelectedCharacterstics(event) {
    if (event) {
      this.hideCharDeleteButton = false;
      this.selectedCharacterstics = event;
    }
  }

  setPageModes() {
    if (this.resourceServiceRes.pageMode === 'NEW') {
      this.displaySaveButton = true;
      this.hideServiceDeleteButton = false;
      this.hideCharDeleteButton = false;
    } else if (this.resourceServiceRes.pageMode === 'EDIT') {
      this.displaySaveButton = true;
      this.hideServiceAddButton = false;
      this.hideCharAddButton = false;
      this.hideServiceDeleteButton = false;
      this.hideCharDeleteButton = false;
    } else if (this.resourceServiceRes.pageMode === 'VIEW') {
      this.serviceByAreaForm.disable();
      this.displaySaveButton = false;
      this.hideServiceAddButton = true;
      this.hideCharAddButton = true;
      this.hideServiceDeleteButton = true;
      this.hideCharDeleteButton = true;
    }
    this.store.dispatch(SET({ dirtyCheck: { isDirty: false } }));
  }

  setSelectedServicesByArea(): any {
    return this.selectedServicesByArea;
  }

  setSelectedCharacterstics(): any {
    return this.selectedCharacterstics;
  }

  deleteServicesByAreaDetail() {
    if ((this.selectedServicesByArea.stateName === 'Texas' && this.selectedServicesByArea.resourceSvcRegion === '98')
      || this.selectedServicesByArea.stateName !== 'Texas') {
      this.confirmMessage = 'This will delete the Client Characteristics for the service within the state. Delete?';
    } else {
      this.confirmMessage = 'This will delete the Client Characteristics for the service within the region. Delete?';
    }

    if (this.selectedServicesByArea) {
      const initialState = {
        title: 'Services By Area',
        message: this.confirmMessage,
        showCancel: true,
      };

      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.resourceService.deleteServicesByAreaDetail(this.selectedServicesByArea.resourceId,
            this.selectedServicesByArea.id)
            .subscribe((res) => {
              window.location.reload();
            });
        }
      });
    } else {
      const initialState = {
        title: 'Services By Area',
        message: 'Please select atleast one row to perform this action.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered',
        initialState,
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
    }
  }

  addServicesByAreaDetail() {
    this.route.navigate(['/resource/services-by-area/'
      + this.resourceServiceRes.resourceId + '/0']);
  }

  addCharactersticsDetail() {
    this.route.navigate(['/resource/services-by-area/'
      + this.resourceServiceRes.resourceId + '/'
      + this.selectedServicesByArea.id
      + '/client-characteristics/0']);
  }

  deleteCharactersticsDetail() {
    if (this.selectedCharacterstics) {
      const initialState = {
        title: 'Client Characterstics',
        message: 'Are you sure you want to delete this record?',
        showCancel: true,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, { class: 'modal-md modal-dialog-centered', initialState });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => {
        if (result === true) {
          this.resourceService.deleteCharactersticsDetail(this.selectedCharacterstics.resourceId,
            this.selectedCharacterstics.resourceServiceId, this.selectedCharacterstics.id)
            .subscribe((res) => {
              this.intializeScreen();
              document.getElementById('deleteCharactersticsButtonId').focus();
            });
        }
      });
    } else {
      const initialState = {
        title: 'Client Characterstics',
        message: 'Please select atleast one row to perform this action.',
        showCancel: false,
      };
      const modal = this.modalService.show(DfpsConfirmComponent, {
        class: 'modal-md modal-dialog-centered',
        initialState,
      });
      (modal.content as DfpsConfirmComponent).onClose.subscribe((result) => { });
    }
  }

}


