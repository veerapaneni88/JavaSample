import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { CookieService } from 'ngx-cookie-service';
import { ResourceService } from '../service/resource.service';
import {
  DfpsFormValidationDirective,
  DirtyCheck,
  ENVIRONMENT_SETTINGS,
  DataTable,
  DfpsConfirmComponent,
  FormUtils
} from 'dfps-web-lib';
import { ResourceServiceRes } from '../model/resource';
import { BsModalService } from 'ngx-bootstrap/modal';;
@Component({
  templateUrl: './resource-list.component.html'
})
export class ResourceListComponent extends DfpsFormValidationDirective implements OnInit, OnDestroy {

  existingResources = null;
  formData = null;
  tableColumn: any[];
  resourcesDataTable: DataTable;
  resourceServicesDataTable: DataTable;
  selectedResource = null;
  contact = null;
  maintainer = null;
  contactPhone = null;
  facilityType = null;
  facilityNumber = null;
  mhmrCode = null;
  resourceServiceResponse: ResourceServiceRes;

  constructor(@Inject(ENVIRONMENT_SETTINGS) private environmentSettings: any,
    private cookieService: CookieService,
    private resourceService: ResourceService,
    private modalService: BsModalService,
    public store: Store<{ dirtyCheck: DirtyCheck }>) {
    super(store);
  }

  ngOnInit(): void {
    this.loadResourceData();
    this.loadExistingResources();
  }

  loadResourceData() {
    const resourceList = this.environmentSettings.environmentName === 'Local'
      ? this.cookieService.get('existingResource') : sessionStorage.getItem('existingResource');
    this.formData = this.environmentSettings.environmentName === 'Local'
      ? this.cookieService.get('formData') : sessionStorage.getItem('formData');
    if (resourceList) {
      this.existingResources = JSON.parse(resourceList);
      if (this.existingResources.resourceSearchResultsDtoList) {
        this.existingResources.resourceSearchResultsDtoList.map(resource => {
          resource.resourceName = resource.resourceName.replaceAll("~S~", "\'");
          resource.resourcePhoneNumber = FormUtils.formatPhoneNumber(resource.resourcePhoneNumber);
          if (resource.resourceStatus === '01') {
            resource.resourceStatus = 'Active';
          } else {
            resource.resourceStatus = 'InActive';
          }

        }
        );
      }
    }
  }

  getSelectedResource(event: Event) {
    this.selectedResource = event;
    if (this.selectedResource) {
      this.getResourceServices(this.selectedResource.idResource);
    }
  }

  getResourceServices(idResource) {
    this.resourceService.getResourceService(idResource).subscribe(res => {
      this.resourceServiceResponse = res;
      if (this.resourceServiceResponse && this.resourceServiceResponse.resourceService) {
        this.loadResourceServices();
      }
      if (this.resourceServiceResponse && this.resourceServiceResponse.resource) {
        const resource = this.resourceServiceResponse.resource;
        this.facilityType = resource.facilityType;
        this.maintainer = resource.maintainer;
        if (resource.phone) {
          this.contactPhone = FormUtils.formatPhoneNumber(resource.phone);
        }
        this.facilityNumber = resource.facilityAcclaim;
        this.mhmrCode = resource.mhmrComp;
        this.contact = resource.contactName;
      }
    });
  }

  loadExistingResources() {
    this.tableColumn = [
      { field: 'resourceName', header: 'Resource Name', sortable: true, width: 150 },
      { field: 'idResource', header: 'Resource ID', sortable: false, width: 150 },
      { field: 'resourceStatus', header: 'Status', sortable: false, width: 100 },
      { field: 'resourceContractedStatus', header: 'C', sortable: true, width: 50, isTick: true },
      { field: 'txtResourceType', header: 'Resource Type', sortable: true, width: 150 },
      { field: 'txtInvJurisdiction', header: 'Investigation Jurisdiction', sortable: true, width: 175 },
      { field: 'txtFacilityType', header: 'Facility Type', sortable: false, width: 150 },
      { field: 'resourceStreetAddr', header: 'Address', sortable: false, width: 200 },
      { field: 'resourceCity', header: 'City', sortable: true, width: 110 },
      { field: 'txtCounty', header: 'County', sortable: true, width: 110 },
      { field: 'resourcePhoneNumber', header: 'Phone', sortable: false, width: 140 },
      { field: 'resourcePhoneExtn', header: 'Ext.', sortable: false, width: 110 },
    ];

    this.resourcesDataTable = {
      tableBody: this.existingResources.resourceSearchResultsDtoList,
      tableColumn: this.tableColumn,
      isSingleSelect: true,
      isPaginator: true
    };
  }

  loadResourceServices() {
    this.tableColumn = [
      { field: 'categoryName', header: 'Category', sortable: false, width: 150 },
      { field: 'serviceName', header: 'Service', sortable: false, width: 250 },
      { field: 'contracted', header: 'C', sortable: false, width: 50, isTick: true },
      { field: 'programName', header: 'Program', sortable: false, width: 100 },
      { field: 'regionName', header: 'Region', sortable: false, width: 100 },
      { field: 'countyName', header: 'County', sortable: false, width: 150 },
      { field: 'servedRsrcSvcCntyPartially', header: 'Partial County', sortable: false, width: 150 },
      { field: 'isServiceIncomeBased', header: 'Income Based', sortable: false, width: 150 },
      { field: 'stateName', header: 'State', sortable: false, width: 150 }
    ];

    this.resourceServicesDataTable = {
      tableBody: this.resourceServiceResponse.resourceService,
      tableColumn: this.tableColumn,
      isSingleSelect: false,
      isPaginator: false
    };
  }

  resourceSelected() {
    if (this.selectedResource) {
      window.location.href = this.environmentSettings.impactP2WebUrl + '/resource/' + this.selectedResource.idResource;
    } else {
      const initialState = {
        title: 'Home History',
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

  addResource() {
    if(this.formData){
      this.formData = this.formData.replaceAll("~S~", "\'");
    }
    const request = JSON.parse(this.formData);
    request.dtLastUpdated = null;
    this.resourceService.addResource(request).subscribe(res => {
      if (res) {
        sessionStorage.removeItem('formData');
        this.cookieService.delete('formData');
        window.location.href = this.environmentSettings.impactP2WebUrl + '/resource/' + res.resourceId;
      }
    })
  }

  cancel() {
    window.location.href = this.environmentSettings.impactP2WebUrl + '/resource/search';
  }

}
