import { Injectable } from '@angular/core';
import { ApiService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { DisplayServicePackageError, ServicePackageError, ServicePackageErrorRequest, 
  ServicePackageErrorResponse, ServicePackageErrorSearchRes } from '../model/ServicePackageError';

@Injectable()
export class ServicePackageErrorService {

 readonly SERVICE_PACKAGE_ERROR_URL = '/v1/service-package-error'; 

  constructor(private apiService: ApiService) { }

  displayServicePackageError(): Observable<DisplayServicePackageError> {
    return this.apiService.get(this.SERVICE_PACKAGE_ERROR_URL + '/display');
  }

  search(servicePackageErrorRequest: ServicePackageErrorRequest): Observable<ServicePackageErrorSearchRes> {
    return this.apiService.post(this.SERVICE_PACKAGE_ERROR_URL + '/search', servicePackageErrorRequest);
  }

  markForDeletion(servicePackageError: ServicePackageError[]): Observable<ServicePackageErrorResponse> {
    return this.apiService.post(this.SERVICE_PACKAGE_ERROR_URL, servicePackageError);
  }

}


