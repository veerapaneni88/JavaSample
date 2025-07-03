import { Injectable } from '@angular/core';
import { Resource } from '@case/model/case';
import { CentralRegistrySearchRequest, CentralRegistrySearchResponse } from '@shared/model/CentralRegistrySearch';
import { OrgSearchRequest, OrgSearchResponse } from '@shared/model/OrgSearch';
import { ResourceSearchResponse } from '@shared/model/ResourceSearch';
import { StaffSearchResponse } from '@shared/model/StaffSearch';
import { UserData } from '@shared/model/UserData';
import { DisplayHomeSearchResponse, HomeSearchRequest } from 'app/modules/search/model/HomeSearch';
import { ApiService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { HomeSearchResult } from '../../search/model/HomeSearch';
import { CrpRequestDetailsDto } from '@shared/model/CentralRegistryRequestDetail';
import { CrpDueProcessRequest, CrpHistoryDecisonRequest, CrpRecordCheckDetail, CrpRecordCommentsRequest } from '@shared/model/CrpRecordCheckDetail';

@Injectable({ providedIn: 'root' })
export class SearchService {

    readonly ORG_SEARCH_URL: string = '/v1/organization/search';
    readonly STAFF_SEARCH_URL: string = '/v1/staff/search';
    readonly STAFF_SEARCH_DISPLAY_URL: string = '/v1/staff/display-search';
    readonly RESOURCE_SEARCH_URL: string = '/v1/resource/search';
    readonly RESOURCE_SEARCH_DISPLAY_URL: string = '/v1/resource/display-search';
    readonly HOME_SEARCH_DISPLAY_URL: string = '/v1/home/display-search';
    readonly HOME_SEARCH_URL: string = '/v1/home/search';
    readonly P3_RESOURCE_URL: string = '/v1/resources';
    readonly P3_UPDATE_USER_CONTEXT: string = '/v1/user-data';
    readonly CENTRAL_REGISTRY_SEARCH_URL: string = '/v1/central-registry/search';
    readonly CENTRAL_REGISTRY_URL: string = '/v1/central-registry';

    private selectedOrg: any;
    private selectedStaff: any;
    private selectedResource: any;
    private formData: any;
    private formContent: any;
    private returnUrl: string;
    private searchSource: string;
    private invokingPage: string;
    private selectedHome: any;
    private selectedSearchStandard: any;

    constructor(private apiService: ApiService) {
    }

    getInvokingPage(): string {
        return this.invokingPage;
    }

    setInvokingPage(value: string) {
        this.invokingPage = value;
    }

    getSelectedOrg(): any {
        return this.selectedOrg;
    }

    setSelectedOrg(selectedOrg: any) {
        this.selectedOrg = selectedOrg;
    }

    getSelectedStaff(): any {
        return this.selectedStaff;
    }

    setSelectedStaff(staff: any) {
        this.selectedStaff = staff;
    }

    getSelectedResource(): any {
        return this.selectedResource;
    }
    getselectedSearchStandard(): any {
        return this.selectedSearchStandard;
    }

    setselectedSearchStandard(searchStandardRec: any) {
        this.selectedSearchStandard = searchStandardRec;
    }

    setSelectedResource(resource: any) {
        this.selectedResource = resource;
    }

    getFormContent() {
        return this.formContent;
    }

    setFormContent(formContent: any) {
        this.formContent = formContent;
    }

    getFormData() {
        return this.formData;
    }

    setFormData(formData: any) {
        this.formData = formData;
    }

    getReturnUrl() {
        return this.returnUrl;
    }

    setReturnUrl(returnUrl: any) {
        this.returnUrl = returnUrl;
    }

    getSearchSource() {
        return this.searchSource;
    }

    setSearchSource(searchSource: string) {
        this.searchSource = searchSource;
    }

    getSelectedHome(): any {
        return this.selectedHome;
    }

    setSelectedHome(home: any) {
        this.selectedHome = home;
    }
    searchOrganization(orgSearchRequest: OrgSearchRequest): Observable<OrgSearchResponse[]> {
        return this.apiService.post(this.ORG_SEARCH_URL, orgSearchRequest);
    }

    searchStaffDisplay() {
        return this.apiService.get(this.STAFF_SEARCH_DISPLAY_URL);
    }

    searchResourceDisplay() {
        return this.apiService.get(this.RESOURCE_SEARCH_DISPLAY_URL);
    }

    searchStaff(staffSearchReq): Observable<StaffSearchResponse[]> {
        return this.apiService.post(this.STAFF_SEARCH_URL, staffSearchReq);
    }

    searchResource(resourceSearchReq): Observable<ResourceSearchResponse[]> {
        return this.apiService.post(this.RESOURCE_SEARCH_URL, resourceSearchReq);
    }

    searchHome(homeSearchRequest: HomeSearchRequest): Observable<HomeSearchResult[]> {
        return this.apiService.post(this.HOME_SEARCH_URL, homeSearchRequest);
    }

    displayHomeSearch(): Observable<DisplayHomeSearchResponse> {
        return this.apiService.get(this.HOME_SEARCH_DISPLAY_URL);
    }

    getResource(resourceId: any): Observable<Resource> {
        return this.apiService.get(this.P3_RESOURCE_URL + '/' + resourceId);
    }

    isHomeAlreadyLinked(resourceId: any): Observable<boolean> {
        return this.apiService.get(this.P3_RESOURCE_URL + '/' + resourceId + '/resource-linked');
    }

    isResourceLinkedToKIN(resourceId: any): Observable<string> {
        return this.apiService.get(this.P3_RESOURCE_URL + '/' + resourceId + '/isresourcetypeofkin');
    }

    updateUserContext(homeSearchRequest: UserData): Observable<HomeSearchResult[]> {
        return this.apiService.post(this.P3_UPDATE_USER_CONTEXT, homeSearchRequest);
    }

    searchCentralRegistryPerson(centralRegistrySearchRequest: CentralRegistrySearchRequest): Observable<CentralRegistrySearchResponse> {
        return this.apiService.post(this.CENTRAL_REGISTRY_SEARCH_URL, centralRegistrySearchRequest);
    }

    getCrpCheckDetail(requestId: string): Observable<CrpRequestDetailsDto> {
        return this.apiService.get(this.CENTRAL_REGISTRY_URL + '/' + requestId);
    }

    getCrpRecordCheckDetail(requestId: string): Observable<CrpRecordCheckDetail> {
        return this.apiService.get(this.CENTRAL_REGISTRY_URL + '/record-check-details/' + requestId);
    }

    savePotentialMatches(payload: any): Observable<any> {
        return this.apiService.post(`${this.CENTRAL_REGISTRY_URL}/potential-match`, payload);
    }

    saveRecordComments(payload: CrpRecordCommentsRequest): Observable<CrpRecordCommentsRequest> {
        return this.apiService.post(`${this.CENTRAL_REGISTRY_URL}/comments`, payload);
    }

    saveHistoryDecision(payload: CrpHistoryDecisonRequest): Observable<CrpHistoryDecisonRequest> {
        return this.apiService.post(`${this.CENTRAL_REGISTRY_URL}/history-decision`, payload);
    }

    saveDueProcess(payload: CrpDueProcessRequest): Observable<CrpDueProcessRequest> {
        return this.apiService.post(`${this.CENTRAL_REGISTRY_URL}/due-process`, payload);
    }

    refreshNotif(requestId: string): Observable<string> {
        return this.apiService.get(this.CENTRAL_REGISTRY_URL + '/' + requestId + '/record-check/notifications');
    }

    sendClearanceNotif(requestId: string): Observable<string> {
        return this.apiService.post(this.CENTRAL_REGISTRY_URL + '/' + requestId + '/cleared', null);
    }

    viewCrpDocument(idDocRepository: number): Observable<CrpRecordCheckDetail> {
        return this.apiService.download(`${this.CENTRAL_REGISTRY_URL}/record-check/openDocument/${idDocRepository}`);
    }

    deleteCrpDocument(idDocRepository: number): Observable<any> {
        return this.apiService.delete(this.CENTRAL_REGISTRY_URL + '/record-check/deleteUploadedDocument/'+ idDocRepository);
    }


}
