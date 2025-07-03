import { Injectable, Inject } from '@angular/core';
import { ApiService, ENVIRONMENT_SETTINGS } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { NeiceTransmittalList, NeiceSearchDisplay, NeiceTransmittalResponse } from '../model/NeiceTransmittalList';
import { AdditionalInformation, NeiceTransmittalSummary } from '../model/NeiceTransmittalSummary';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable()
export class NeiceTransmittalsService {
    static readonly NEICE_TRANSMITTAL_SUMMARY_URL = '/mytasks/neice-transmittals/:idNeiceTransmittal/summary/:incomingOutgoing';
    readonly DISPLAY_SEARCH_URL: string = '/v1/mytasks/neice-transmittals/display-search';
    readonly TRANSMITTALSLIST_SEARCH_URL: string = '/v1/mytasks/neice-transmittals/search';
    readonly NEICE_TRANSMITTAL_URL: string = '/v1/mytasks/neice-transmittals/';
    readonly NEICE_ADDITIONAL_INFO_URL: string = '/v1/mytasks/neice-transmittals/additional-info';
    readonly NEICE_DOCUMENT_TYPE_URL: string = '/v1/mytasks/neice-transmittals/document-type';

    constructor(private apiService: ApiService, private http: HttpClient, 
        @Inject(ENVIRONMENT_SETTINGS) private enviromentSettings: any) { }

    displayTransmittalListSearch(): Observable<NeiceSearchDisplay> {
        return this.apiService.get(this.DISPLAY_SEARCH_URL);
    }

    searchNeiceTransmittalList(neiceTransmittalList: NeiceTransmittalList): Observable<NeiceTransmittalResponse> {
        return this.apiService.post(this.TRANSMITTALSLIST_SEARCH_URL, neiceTransmittalList);
    }

    displayTransmittalSummary(idNeiceTransmittal, inboundOutboundType): Observable<NeiceTransmittalSummary> {
        return this.apiService.get(`${this.NEICE_TRANSMITTAL_URL}${idNeiceTransmittal}/summary/${inboundOutboundType}`);
    }

    saveTransmittalSummary(idNeiceTransmittal, payload): Observable<any> {
        return this.apiService.post(`${this.NEICE_TRANSMITTAL_URL}${idNeiceTransmittal}/summary`, payload);
    }

    displayAdditionalInformation(): Observable<any> {
        return this.apiService.get(this.NEICE_ADDITIONAL_INFO_URL);
    }

    displayDocumentType(): Observable<any> {
        return this.apiService.get(this.NEICE_DOCUMENT_TYPE_URL);
    }

    sendAdditionalInformation(idNeiceTransmittal, payload: AdditionalInformation): Observable<any> {
        return this.apiService.post(`${this.NEICE_TRANSMITTAL_URL}${idNeiceTransmittal}/additional-info`, payload);

    }

    rejectTransmittalSummary(idNeiceTransmittal, payload): Observable<any> {

        const formData: FormData = new FormData();
        formData.append('comments', payload.comments);
        formData.append('documentType', payload.documentType);
        formData.append('description', payload.description);
        formData.append('file', payload.file);

        return this.apiService.upload(`${this.NEICE_TRANSMITTAL_URL}${idNeiceTransmittal}/reject-info`, formData);
    }

    processTransmittalSummary(idNeiceTransmittal, payload): Observable<any> {
        return this.apiService.post(`${this.NEICE_TRANSMITTAL_URL}${idNeiceTransmittal}/process`, payload);

    }

    downloadDocument(inboundOutboundType: any, neiceAttachementId: any): Observable<Blob> {
        return this.apiService.download(`${this.NEICE_TRANSMITTAL_URL}${inboundOutboundType}/download/${neiceAttachementId}`);
    }

}
