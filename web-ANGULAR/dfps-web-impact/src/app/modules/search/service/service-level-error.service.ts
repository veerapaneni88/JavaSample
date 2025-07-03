import { Injectable } from '@angular/core';
import { ApiService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { DisplayServiceLevelError, ServiceLevelError, ServiceLevelErrorRequest, 
  ServiceLevelErrorResponse, ServiceLevelErrorSearchRes } from '../model/ServiceLevelError';

@Injectable()
export class ServiceLevelErrorService {

 readonly SERVICE_LEVEL_ERROR_URL = '/v1/service-level-error'; 

  constructor(private apiService: ApiService) { }

  displayServiceLevelError(): Observable<DisplayServiceLevelError> {
    return this.apiService.get(this.SERVICE_LEVEL_ERROR_URL + '/display');
  }

  search(serviceLevelErrorRequest: ServiceLevelErrorRequest): Observable<ServiceLevelErrorSearchRes> {
    return this.apiService.post(this.SERVICE_LEVEL_ERROR_URL + '/search', serviceLevelErrorRequest);
  }

  markForDeletion(serviceLevelError: ServiceLevelError[]): Observable<ServiceLevelErrorResponse> {
    return this.apiService.post(this.SERVICE_LEVEL_ERROR_URL, serviceLevelError);
  }

}


