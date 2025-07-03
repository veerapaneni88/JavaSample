import { Injectable } from '@angular/core';
import {  DiligentSearchDto, DiligentSearchResponse, QuickFindDto, QuickFindResponse } from '@case/model/formsReferrals';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { Observable } from 'rxjs';

@Injectable()
export class QuickFindService {
    readonly SESSION_URL = '/v1/session/';
    readonly QUICK_FIND_URL = '/v1/quick-find/';

    constructor(private apiService: ApiService, private globalMessageService: GlobalMessageServcie) {}

    getQuickFind(quickFindId: any): Observable<QuickFindResponse> {
        return this.apiService.get(this.QUICK_FIND_URL + quickFindId);
    }

    saveQuickFind(quickFind: QuickFindDto): Observable<QuickFindDto> {
        return this.apiService.post(this.QUICK_FIND_URL, quickFind);
    }

    deleteQuickFind(quickFindId: any): Observable<QuickFindResponse> {
        return this.apiService.delete(this.QUICK_FIND_URL + quickFindId);
    }
}

@Injectable()
export class DiligentSearchService {
    readonly SESSION_URL = '/v1/session/';
    readonly DILIGENT_SEARCH_URL = '/v1/diligent-search/';

    constructor(private apiService: ApiService, private globalMessageService: GlobalMessageServcie) {}

    getDiligentSearch(diligentSearchId: any): Observable<DiligentSearchResponse> {
        return this.apiService.get(this.DILIGENT_SEARCH_URL + diligentSearchId);
    }

    saveDiligentSearch(diligentSearchDto: DiligentSearchDto): Observable<DiligentSearchDto> {
        return this.apiService.post(this.DILIGENT_SEARCH_URL, diligentSearchDto);
    }

    deleteDiligentSearch(diligentSearchId: any): Observable<DiligentSearchResponse> {
        return this.apiService.delete(this.DILIGENT_SEARCH_URL + diligentSearchId);
    }
}