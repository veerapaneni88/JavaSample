import { formatDate } from '@angular/common';
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { ApiService } from 'dfps-web-lib';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { DisplayHomeInformation, FadHomeRequest, HomeResponse, SchoolDistrict, Resource, KinHomeRequest } from '../model/home';

@Injectable()
export class HomeService {

    readonly SESSION_URL = '/v1/session/';
    readonly HOME_URL = '/v1/home';
    readonly RESOURCE_URL: string = '/v1/resources';

    readonly URI_SCHOOL_DISTRICTS_COUNTY_CODE = '/v1/schooldistricts/countyCode/';

    constructor(
        private cookieService: CookieService,
        private apiService: ApiService,
        @Inject(LOCALE_ID) private locale: string
    ) {
    }

    getUserData(): Observable<any> {
        return this.apiService.get(this.SESSION_URL + this.cookieService.get('impactUserSession'));
    }

    displayHome(): Observable<DisplayHomeInformation> {
        return this.apiService.get(this.HOME_URL + '/display');
    }

    displayHomeKIN(): Observable<DisplayHomeInformation> {
        return this.apiService.get(this.HOME_URL + '/display-kin');
    }

    saveAndAssign(homeRequest: FadHomeRequest): Observable<HomeResponse> {
        return this.apiService.post(this.HOME_URL + '/fad/save', homeRequest);
    }

    saveAndAssignKIN(homeRequest: KinHomeRequest): Observable<HomeResponse> {
        return this.apiService.post(this.HOME_URL + '/kin/save', homeRequest);
    }

    getSchoolDistrictByCountyCode(countyCode: string): Observable<SchoolDistrict[]> {
        return this.apiService.get(this.URI_SCHOOL_DISTRICTS_COUNTY_CODE + countyCode);
    }

    getResource(resourceId: string): Observable<Resource> {
        return this.apiService.get(this.RESOURCE_URL + '/' + resourceId);
    }
}