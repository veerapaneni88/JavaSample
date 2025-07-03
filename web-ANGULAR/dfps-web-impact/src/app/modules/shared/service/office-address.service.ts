import { Injectable } from '@angular/core';
import { ApiService } from 'dfps-web-lib';

@Injectable({
  providedIn: 'root'
})
export class OfficeAddressService {

  readonly P3_PER_OFF_ADDRESS_SEARCH_URL: string = '/v1/employee';

  constructor(private apiService: ApiService) { }

  getOfficeAddr(personId: any) {
    return this.apiService.get(this.P3_PER_OFF_ADDRESS_SEARCH_URL + '/' + personId + '/office');
  }
}
