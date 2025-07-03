import { Injectable } from '@angular/core';
import { ApiService } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BudgetTransferReq, BudgetTransferRes } from '../model/BudgetTransfer';

@Injectable()
export class BudgetTransferService {
    readonly BUDGET_TRANSFER_URL = '/v1/contracts';
    readonly BUDGET_TRANSFER_SAVE = '/v1/contracts/budget-transfer';

    constructor(
        private apiService: ApiService) {
    }

    getBudgetTransfer(contractId: number, periodNumber: number, version: number): Observable<BudgetTransferRes> {
        return this.apiService.get(this.BUDGET_TRANSFER_URL + '/' + contractId + '/' + periodNumber + '/' +
            version + '/budget-transfer').pipe(map(res => {
                return { ...res, contract: res.contract ? res.contract : {} };
            }
            ));
    }

    saveBudgetTransfer(budgetTransferReq: BudgetTransferReq): Observable<any> {
        return this.apiService.post(this.BUDGET_TRANSFER_SAVE, budgetTransferReq);
    }

}
