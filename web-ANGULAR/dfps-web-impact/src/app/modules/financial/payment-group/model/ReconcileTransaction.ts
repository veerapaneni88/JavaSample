import { DropDown } from 'dfps-web-lib';

export interface ReconcileDisplayResponse {
    accountNos: DropDown[];
    pageMode: string;
    
}

export interface ReconciliationSearchRequest {
    accountNumber : string;
    checks : boolean;
    ach : boolean;
    deposits : boolean;
    transfers : boolean;
    checkNo : number;
    achNumber : string;
    agencyAccountId : number;
    payeePersonId : number;
}

export interface ReconciliationSearchRes {
    reconciliationSearchResults: ReconcileTransactionSearch[];
    reconBalance: number;
}

export interface ReconcileTransactionSearch{
    idReconTran : number;
    reconTransType : string;
    idPerson : number;
    personName : string;
    dtReconTran : string;
    amountReconTran : number;
    checkNumber : number;
    achNumber : string;
    payeeName : string;
    accountNumber : string;
    agencyAccountId : number;
    resourceId : number;
    acctTransType : string;
    paymentOrTransInd : string;
    currencySign : string;
    checkOrAchNumber : string;
    financialAccountId: number;

}

export interface ReconcileTransaction{
    idReconTran : number;
    paymentOrTransInd : string;

}

export interface ReconciliationTransactionReq{
    reconcileTransactions: ReconcileTransaction[];
}