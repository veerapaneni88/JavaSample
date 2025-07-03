import { Injectable } from '@angular/core';
import {
    SearchStandardDisplayRes,
    StandardSearchReq,
    StandardSearchResult,
    WaiverVarianceDetailRes,
} from '@case/model/waiverVariance';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { Observable } from 'rxjs';

@Injectable()
export class WaiverVarianceService {
    readonly SESSION_URL = '/v1/session/';
    readonly DISPLAY_STANDARD_URL = '/v1/standard/display';
    readonly SEARCH_STANDARD_URL = '/v1/standard/search';
    readonly GET_STANDARD_URL = '/v1/standard/search';

    readonly DISPLAY_WAIVERVARIANCE_URL = '/v1/waiver-variance/display';
    readonly SAVE_WAIVERVARIANCE_URL = '/v1/waiver-variance/save';
    constructor(
        private apiService: ApiService,
        private globalMessageService: GlobalMessageServcie
    ) { }

    displayStandardSearch(): Observable<SearchStandardDisplayRes> {
        return this.apiService.get(this.DISPLAY_STANDARD_URL);
    }

    searchStandard(standardSearchReq: StandardSearchReq): Observable<StandardSearchResult[]> {
        return this.apiService.post(this.SEARCH_STANDARD_URL, standardSearchReq);
    }

    displayWaiverVarianceStandard(eventId: any, usingEventId: any): Observable<WaiverVarianceDetailRes> {
        return this.apiService.get('/v1/event/' + eventId + '/waiver-variance?usingEventId=' + usingEventId);
    }

    saveWaiverVariance(eventId: any, waiverVarianceResult: any): Observable<number> {
        this.globalMessageService.addSuccessPath('/v1/event/' + eventId + '/waiver-variance/save');
        return this.apiService.post('/v1/event/' + eventId + '/waiver-variance/save', waiverVarianceResult);
    }

    submitWaiverVariance(eventId: any, waiverVarianceResult: any): Observable<number> {
        this.globalMessageService.addSuccessPath('/v1/event/' + eventId + '/waiver-variance/submit');
        return this.apiService.post('/v1/event/' + eventId + '/waiver-variance/submit', waiverVarianceResult);
    }
}
