import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { CaretakerDetailRes, CaretakerRes } from '../model/caretakerDetail';
import {Resource} from '@case/model/case';
import {
  ResourceCharacterDetailRes,
  ResourceCharacter,
  ResourceServiceRes,
  ResourceSvc,
  ResourceSvcByAreaDetailRes,
  MedicalConsenterRes,
  MedicalConsenter,
  FacilityServiceLevelRes,
  ServicePackageCredential} from './../model/resource';

@Injectable()
export class ResourceService {
  readonly SERVICE_BY_AREA_URL: string = '/v1/resources/';
  readonly GET_CARETAKER_URL: string = '/v1/resources/';

  private resourceCharacters: ResourceCharacter[] = [];

  constructor(
    private apiService: ApiService,
    private globalMessageService: GlobalMessageServcie,
    @Inject(LOCALE_ID) private locale: string
  ) {
    this.globalMessageService.addSuccessPath('/v1/resources/service-package-history/save');

  }

  getServiceByAreaRes(resourceId: any): Observable<ResourceServiceRes> {
    return this.apiService.get(this.SERVICE_BY_AREA_URL
      + resourceId + '/service/');
  }

  getResourceCharacters(): ResourceCharacter[] {
    return this.resourceCharacters;
  }

  getCharacteristcisRes(resourceId: any, serviceId: any): Observable<ResourceCharacter[]> {
    return this.apiService.get(this.SERVICE_BY_AREA_URL
      + resourceId + '/service/' + serviceId + '/character').pipe(tap(res => {
        this.resourceCharacters = res;
      }));
  }

  getServicesByAreaDetailRes(resourceId: any, resourceServiceId: any): Observable<ResourceSvcByAreaDetailRes> {
    return this.apiService.get(this.SERVICE_BY_AREA_URL + resourceId + '/service/' + resourceServiceId);
  }

  saveServicesByAreaDetail(resourceId: any, resourceServiceDto: ResourceSvc) {
    const resourceServiceDtoClone = Object.assign({}, resourceServiceDto);
    if (resourceServiceDto.incomeBased) {
      resourceServiceDtoClone.incomeBased = 'Y';
    } else {
      resourceServiceDtoClone.incomeBased = 'N';
    }

    if (resourceServiceDto.countyPartial) {
      resourceServiceDtoClone.countyPartial = 'Y';
    } else {
      resourceServiceDtoClone.countyPartial = 'N';
    }

    resourceServiceDtoClone.showRow = 'Y';

    if (resourceServiceDto.kinshipAgreement) {
      resourceServiceDtoClone.kinshipAgreement = 'Y';
    } else {
      resourceServiceDtoClone.kinshipAgreement = 'N';
    }

    if (resourceServiceDto.kinshipHomeAssessment) {
      resourceServiceDtoClone.kinshipHomeAssessment = 'Y';
    } else {
      resourceServiceDtoClone.kinshipHomeAssessment = 'N';
    }

    if (resourceServiceDto.kinshipTraining) {
      resourceServiceDtoClone.kinshipTraining = 'Y';
    } else {
      resourceServiceDtoClone.kinshipTraining = 'N';
    }

    if (resourceServiceDto.kinshipIncome) {
      resourceServiceDtoClone.kinshipIncome = 'Y';
    } else {
      resourceServiceDtoClone.kinshipIncome = 'N';
    }

    if (':' === resourceServiceDto.service) {
      resourceServiceDtoClone.service = '';
    }

    if (':' === resourceServiceDto.region) {
      resourceServiceDtoClone.region = '';
    }

    if (':' === resourceServiceDto.county) {
      resourceServiceDtoClone.county = '';
    }

    return this.apiService.post(this.SERVICE_BY_AREA_URL + resourceId + '/service', resourceServiceDtoClone);
  }

  deleteServicesByAreaDetail(resourceId: any, resourceServiceId: any): Observable<any> {
    return this.apiService
      .delete(this.SERVICE_BY_AREA_URL + resourceId + '/service/' + resourceServiceId)
      .pipe(
        map((res) => {
          return { ...res, response: res == null ? { res: 'success' } : {} };
        })
      );
  }

  getClientCharacteristicDetailRes(resourceId: any,
    resourceServiceId: any,
    resourceCharacterId: any): Observable<ResourceCharacterDetailRes> {
    return this.apiService.get(this.SERVICE_BY_AREA_URL + resourceId + '/service/'
      + resourceServiceId + '/character/' + resourceCharacterId);
  }

  saveClientCharactersiticsDetail(resourceId: any, resourceServiceId: any, resourceCharacterDto: ResourceCharacter) {
    return this.apiService.post(this.SERVICE_BY_AREA_URL + resourceId + '/service/'
      + resourceServiceId + '/character', resourceCharacterDto);
  }

  deleteCharactersticsDetail(resourceId: any, resourceServiceId: any, clientCharactersticId: any): Observable<any> {
    return this.apiService
      .delete(this.SERVICE_BY_AREA_URL + resourceId + '/service/'
        + resourceServiceId + '/character/' + clientCharactersticId)
      .pipe(
        map((res) => {
          return { ...res, response: res == null ? { res: 'success' } : {} };
        })
      );
  }

  getMedicalConsenterDetailsRes(resourceId: number, id: number):
    Observable<MedicalConsenterRes> {
    return this.apiService
      .get(this.SERVICE_BY_AREA_URL + resourceId + '/medical-consenter/'
        + id);
  }

  saveMedicalConsenterDetails(medicalConsenter: MedicalConsenter) {
    return this.apiService.post(this.SERVICE_BY_AREA_URL + 'medical-consenter', medicalConsenter);
  }

  getCareTakerDetail(resourceId: any, caretakerId: any): Observable<CaretakerRes> {
    return this.apiService.get(this.GET_CARETAKER_URL + resourceId + '/caretaker-details/' + caretakerId);
  }

  saveCaretakerDetail(resourceId: any, caretakerDetailRes: CaretakerDetailRes) {
    this.globalMessageService.addSuccessPath(this.GET_CARETAKER_URL + resourceId + '/caretaker-details');
    return this.apiService.post(this.GET_CARETAKER_URL + resourceId + '/caretaker-details', caretakerDetailRes);
  }

  deleteCaretakerDetail(resourceId: any, caretakerId: any, maritalStatus: any): Observable<any> {
    return this.apiService
      .delete(this.GET_CARETAKER_URL + resourceId + '/caretaker-details/' + caretakerId + '?maritalStatus=' + maritalStatus)
      .pipe(
        map((res) => {
          return { ...res, response: res == null ? { res: 'success' } : {} };
        })
      );
  }

  getFacilityServiceLevel(resourceId: any, flocId: any, effectiveDate: any): Observable<any> {
    return this.apiService.get('/v1/resources/' + resourceId + '/facility-loc/' + flocId + '?effectiveDate=' + effectiveDate);
  }

  getFacilityServicePackageHistory(resourceId: any, servicePackageHistoryId: any, effectiveDate?: any): Observable<any> {
    let path = !!effectiveDate ? '/v1/resources/' + resourceId + '/service-package-history/' + servicePackageHistoryId + '?effectiveDate=' + effectiveDate
      : '/v1/resources/' + resourceId + '/service-package-history/' + servicePackageHistoryId;
    return this.apiService.get(path);
  }

  getResourceService(resourceId: any): Observable<any> {
    return this.apiService.get('/v1/resources/' + resourceId + '/service');
  }

  addResource(resource:any): Observable<Resource> {
    return this.apiService.post('/v1/resources', resource);
  }

  saveFacilityServiceLevel(facilityServiceLevelRes: FacilityServiceLevelRes) {
    return this.apiService.post('/v1/resources/facility-loc/save', facilityServiceLevelRes);
  }

  saveFacilityServicePackage(facilityServicePackage: ServicePackageCredential) {
    return this.apiService.post('/v1/resources/service-package-history/save', facilityServicePackage);
  }
}
