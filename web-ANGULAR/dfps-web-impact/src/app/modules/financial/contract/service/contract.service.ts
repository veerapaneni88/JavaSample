import { Injectable } from '@angular/core';
import { ApiService, GlobalMessageServcie } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { Contract, ContractHeaderResponse, Resource } from '../model/ContractHeader';
import { ContractPeriod, ContractPeriodResponse } from '../model/ContractPeriod';
import { ContractSearchRequest, ContractSearchResponse, DisplayContractSearchResponse } from '../model/ContractSearch';
import { ContractSvc, CountyCodes, DisplayContractServicesResponse } from '../model/ContractService';
import { ContractVersion, ContractVersionRes } from '../model/ContractVersion';
import { CostReimbursementDetails } from '../model/CostReimbursement';

@Injectable()
export class ContractService {

    readonly DISPLAY_SEARCH_URL: string = '/v1/contracts/display-search';
    readonly CONTRACT_SEARCH_URL: string = '/v1/contracts/search';
    readonly CONTRACT_URL: string = '/v1/contracts';
    readonly RESOURCE_URL: string = '/v1/resources';
    readonly CONTRACT_PERIOD_URL = '/v1/contract-periods';
    readonly SAVE_CONTRACT_PERIOD_URL = '/v1/contract-periods/save';
    readonly VALIDATE_CONTRACT_URL = '/v1/contract-periods/validate-scor';
    readonly CONTRACT_VERSION_URL = '/v1/contract-versions';
    readonly SAVE_CONTRACT_VERSION_URL = '/v1/contract-versions/save';

    private contract: Contract = {};
    private contractVersions: ContractVersion[] = [];
    private contractPeriods: ContractPeriod[] = [];
    private contractPageMode = 'VIEW';

    private selectedContractVersion: ContractVersion;
    private selectedContractPeriod: ContractPeriod;

    getContract(): Contract {
        return this.contract;
    }

    getContractVersions(): ContractVersion[] {
        return this.contractVersions;
    }

    getContractPageMode(): string {
        return this.contractPageMode;
    }

    getCntrctPeriods(): ContractPeriod[] {
        return this.contractPeriods;
    }

    getSelectedContractVersion(): ContractVersion {
        return this.selectedContractVersion;
    }

    getSelectedContractPeriod(): ContractPeriod {
        return this.selectedContractPeriod;
    }

    setSelectedContractVersion(contractVersion: ContractVersion) {
        this.selectedContractVersion = contractVersion;
    }

    setSelectedContractPeriod(contractPeriod: ContractPeriod) {
        this.selectedContractPeriod = contractPeriod;
    }

    constructor(
        private apiService: ApiService,
        private globalMessageService: GlobalMessageServcie) {
        this.globalMessageService.addSuccessPath(this.CONTRACT_URL);
        this.globalMessageService.addSuccessPath(this.SAVE_CONTRACT_PERIOD_URL);
    }

    displayContractSearch(): Observable<DisplayContractSearchResponse> {
        return this.apiService.get(this.DISPLAY_SEARCH_URL);
    }

    searchContract(contractSearchRequest: ContractSearchRequest): Observable<ContractSearchResponse> {
        return this.apiService.post(this.CONTRACT_SEARCH_URL, contractSearchRequest);
    }

    getContractHeader(contractId: string): Observable<ContractHeaderResponse> {
        return this.apiService.get(this.CONTRACT_URL + '/' + contractId).pipe(map(res => {
            return { ...res, contract: res.contract ? res.contract : {} };
        }), tap(res => {
            this.contract = res.contract;
            this.contractPageMode = res.pageMode;
        }
        ));
    }

    getContractPeriod(contractId: string, periodNumber: string): Observable<ContractPeriodResponse> {
        return this.apiService.get(this.CONTRACT_PERIOD_URL + '/' + contractId + '/periods/' + periodNumber).pipe(map(res => {
            return { ...res, contract: res.contract ? res.contract : {} };
        }
        ));
    }

    saveContractPeriod(contractPeriodRequest: any): Observable<ContractPeriod> {
        return this.apiService.post(this.SAVE_CONTRACT_PERIOD_URL, contractPeriodRequest);
    }

    saveContract(contract: Contract): Observable<Contract> {
        const contractClone = Object.assign({}, contract);
        if (!contract.backgroundCheck) {
            contractClone.einBgcAccountLinkId = null;
            contractClone.sponsorPersonId = null;
        }
        return this.apiService.post(this.CONTRACT_URL, contractClone);
    }

    getResource(resourceId: string): Observable<Resource> {
        return this.apiService.get(this.RESOURCE_URL + '/' + resourceId);
    }

    getContractPeriods(contractId: string): Observable<any> {
        return this.apiService.get(this.CONTRACT_URL + '/' + contractId + '/periods').pipe(tap(res => {
            this.contractPeriods = res;
        }));
    }

    getContractVerions(contractId: string, period: string): Observable<ContractVersion[]> {
        return this.apiService.get(this.CONTRACT_URL + '/' + contractId + '/periods/' + period + '/versions').pipe(tap(res => {
            this.contractVersions = res;
        }));
    }

    getContractVersion(contractId: string, period: number, version: number): Observable<ContractVersionRes> {
        return this.apiService.get(this.CONTRACT_VERSION_URL + '/' + contractId + '/periods/' + period + '/versions/' + version);
    }

    deleteContractPeriod(contractId: string, period: string): Observable<any> {
        return this.apiService.delete(this.CONTRACT_URL + '/' + contractId + '/periods/' + period);
    }

    saveContractVersion(contractVersionRequest: ContractVersion): Observable<ContractVersion> {
        return this.apiService.post(this.SAVE_CONTRACT_VERSION_URL, contractVersionRequest);
    }

    getContractServiceByPaymentType(contractId: number, period: number, version: number, contractServiceId: number) {
        return this.apiService.get(this.CONTRACT_URL + '/' + contractId
            + '/periods/' + period
            + '/versions/' + version
            + '/services/' + contractServiceId
            + '/paymentType');
    }

    saveCRMDetails(contractId: string, period: string, version: string, costReimbursementDetails: CostReimbursementDetails)
        : Observable<CostReimbursementDetails> {
        const SAVE_CONTRACT_SERVICE_PAYMENT_TYPE = this.CONTRACT_URL + '/'
            + contractId + '/periods/'
            + period + '/versions/'
            + version + '/services/';
        this.globalMessageService.addSuccessPath(SAVE_CONTRACT_SERVICE_PAYMENT_TYPE);
        return this.apiService.post(SAVE_CONTRACT_SERVICE_PAYMENT_TYPE, costReimbursementDetails);
    }

    getContractServices(contractId: string, period: string, version: string): Observable<any> {
        return this.apiService.get(this.CONTRACT_URL + '/'
            + contractId + '/periods/'
            + period + '/versions/'
            + version + '/services');
    }

    getContractServicesDetail(contractId: number, period: number, version: number, contractServiceId: number)
        : Observable<DisplayContractServicesResponse> {
        return this.apiService.get(this.CONTRACT_URL + '/' + contractId
            + '/periods/' + period
            + '/versions/' + version
            + '/services/' + contractServiceId);
    }

    getContractServicesCountyCode(contractId: number, period: number, version: number, contractServiceId: number, countyCode: string)
        : Observable<CountyCodes> {
        return this.apiService.get(this.CONTRACT_URL + '/' + contractId
            + '/periods/' + period
            + '/versions/' + version
            + '/services/' + contractServiceId
            + '/county-codes/' + countyCode);
    }

    saveContractServiceDetails(contractId: number, period: number, version: number, contractService: ContractSvc)
        : Observable<ContractSvc> {
        const SAVE_CONTRACT_SERVICE = this.CONTRACT_URL + '/'
            + contractId + '/periods/'
            + period + '/versions/'
            + version + '/services';
        this.globalMessageService.addSuccessPath(SAVE_CONTRACT_SERVICE);
        return this.apiService.post(SAVE_CONTRACT_SERVICE, contractService);
    }

    validateContractNumber(contractNumber: string): Observable<any> {
        return this.apiService.get(this.VALIDATE_CONTRACT_URL + '/' + contractNumber);
    }

    getProvidersInfo(contractId: string) {
        return this.apiService.get(this.CONTRACT_URL + '/' + contractId + '/authproviders');
    }

    deleteProviderInfo(contractId: string, personId: string) {
        return this.apiService.delete(this.CONTRACT_URL + '/' + contractId + '/authproviders/' + personId);
    }

    saveProviderInfo(contractId: string, personId) {
        return this.apiService.post(this.CONTRACT_URL + '/' + contractId + '/authproviders/', personId);
    }


}
