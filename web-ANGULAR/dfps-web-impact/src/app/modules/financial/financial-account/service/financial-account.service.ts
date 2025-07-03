import { formatDate } from '@angular/common';
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { ApiService, DropDown, GlobalMessageServcie } from 'dfps-web-lib';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import {
    FinancialAccount,
    FinancialAccountDisplayRes,
    FinancialAccountRes,
    FinancialAccountSearchRes
} from '../model/FinancialAccount';
import { FinancialTransaction, FinancialTransactionRes } from '../model/FinancialRegister';


@Injectable()
export class FinancialAccountService {
    readonly FINANCIAL_ACCOUNT_DETAIL_URL: string = '/v1/financial-accounts';
    readonly FINANCIAL_ACCOUNT_DETAIL_VALIDATE_URL: string = '/v1/financial-accounts/validate';
    private readonly DISPLAY_FINANCIAL_ACCOUNT_SEARCH = '/v1/financial-accounts/display-search';
    readonly REGIONAL_ACCOUNT_DETAILS = '/v1/regional-accounts/get';

    private countyMap = new Map();
    public get getCountyMap() {
        return this.countyMap;
    }

    private statusMap = new Map();
    public get getStatusMap() {
        return this.statusMap;
    }

    private typeMap = new Map();
    public get getTypeMap() {
        return this.typeMap;
    }

    constructor(private apiService: ApiService,
        private globalMessageService: GlobalMessageServcie,
        @Inject(LOCALE_ID) private locale: string) { }

    getFinancialAccountDetail(financialAccountId: number): Observable<FinancialAccountRes> {
        return this.apiService.get(this.FINANCIAL_ACCOUNT_DETAIL_URL + '/' + financialAccountId);
    }

    saveFinancialAccountDetail(financialAccount: FinancialAccount): Observable<FinancialAccount> {
        this.globalMessageService.addSuccessPath(this.FINANCIAL_ACCOUNT_DETAIL_URL);
        return this.apiService.post(this.FINANCIAL_ACCOUNT_DETAIL_URL, financialAccount);
    }

    validateFinancialAccountDetail(financialAccount: FinancialAccount): Observable<FinancialAccount> {
        return this.apiService.post(this.FINANCIAL_ACCOUNT_DETAIL_VALIDATE_URL, financialAccount);
    }

    getFinancialAccountTransactions(financialAccountId: any): Observable<FinancialTransactionRes> {
        return this.apiService.get(
            this.FINANCIAL_ACCOUNT_DETAIL_URL + '/' + financialAccountId + '/financial-account-transactions'
        ).pipe(
            map((res: FinancialTransactionRes) => {
                if (res.financialAccount) {
                    res.financialAccount.balance = res.financialAccount.balance || 0;
                    res.financialAccount.availableBalance = res.financialAccount.availableBalance || 0;
                }
                res.financialAccountTransactions.forEach((dto) => {
                    if (dto.transactionDate) {
                        dto.transactionDate = formatDate(dto.transactionDate.split(' ')[0],
                            'MM/dd/yyyy', this.locale);
                    }
                    if (dto.issuedDate) {
                        dto.issuedDate = formatDate(dto.issuedDate.split(' ')[0],
                            'MM/dd/yyyy', this.locale);
                    }

                });
                return res;
            })
        );
    }

    getFinancialAccountTransaction(financialAccountId: any, financialTransactionAccountId: any): Observable<FinancialTransactionRes> {
        return this.apiService.get(
            this.FINANCIAL_ACCOUNT_DETAIL_URL
            + '/' + financialAccountId
            + '/financial-account-transactions/' + financialTransactionAccountId
        ).pipe(
            map((res: FinancialTransactionRes) => {
                if (res.financialAccount) {
                    res.financialAccount.balance = res.financialAccount.balance || 0;
                    res.financialAccount.availableBalance = res.financialAccount.availableBalance || 0;
                    if (res.financialTransaction) {
                        if (res.financialTransaction.transactionDate) {
                            res.financialTransaction.transactionDate = formatDate(
                                res.financialTransaction.transactionDate.split(' ')[0], 'MM/dd/yyyy', this.locale);
                        }
                        if (res.financialTransaction.accountHoldDate) {
                            res.financialTransaction.accountHoldDate = formatDate(
                                res.financialTransaction.accountHoldDate.split(' ')[0], 'MM/dd/yyyy', this.locale);
                        }
                        if (res.financialTransaction.issuedDate) {
                            res.financialTransaction.issuedDate = formatDate(
                                res.financialTransaction.issuedDate.split(' ')[0], 'MM/dd/yyyy', this.locale);
                        }
                    }
                    if (res.financialAccount.program === 'CPS' && res.financialAccount.cpsChkAcct) {
                        if (res.financialTransaction.warrantNumber === null) {
                            res.financialTransaction.warrantNumber = res.financialTransaction.achNumber;
                        }
                    }
                }
                return res;
            })
        )
    }


    saveFinancialAccountTransaction(financialAccountId: any, financialTransaction: FinancialTransaction) {
        const finTransactionClone = Object.assign({}, financialTransaction);
        finTransactionClone.financialAccountId = financialAccountId;
        if (finTransactionClone.id == null) {
            finTransactionClone.id = '0';
        }
        if (financialTransaction.accountHoldDate) {
            finTransactionClone.accountHoldDate = formatDate(finTransactionClone.accountHoldDate.split(' ')[0],
                'yyyy-MM-dd HH:mm:ss', this.locale);
        }
        if (financialTransaction.issuedDate) {
            finTransactionClone.issuedDate = formatDate(finTransactionClone.issuedDate.split(' ')[0],
                'yyyy-MM-dd HH:mm:ss', this.locale);
        }
        if (financialTransaction.transactionDate) {
            finTransactionClone.transactionDate = formatDate(finTransactionClone.transactionDate.split(' ')[0],
                'yyyy-MM-dd HH:mm:ss', this.locale);
        }
        // this.globalMessageService.addSuccessPath(this.FINANCIAL_ACCOUNT_DETAIL_URL
        //     + '/' + financialAccountId + '/financial-account-transactions');
        return this.apiService
            .post(this.FINANCIAL_ACCOUNT_DETAIL_URL
                + '/' + financialAccountId + '/financial-account-transactions',
                finTransactionClone);
    }

    deleteFinancialAccountTransactions(financialAccountId: any, transactionIds: any[]): Observable<any> {
        return this.apiService
            .post(
                this.FINANCIAL_ACCOUNT_DETAIL_URL + '/' + financialAccountId + '/financial-account-transactions/delete',
                transactionIds
            )
            .pipe(
                map((res) => {
                    return { ...res, response: res == null ? { res: 'success' } : {} };
                })
            );
    }

    reconcileFinancialAccountTransactions(financialAccountId: string, transactionIds: any[]) {
        return this.apiService
            .post(
                this.FINANCIAL_ACCOUNT_DETAIL_URL +
                '/' +
                financialAccountId +
                '/financial-account-transactions/reconcile',
                transactionIds
            )
            .pipe(
                map((res) => {
                    return { ...res, response: res == null ? { res: 'success' } : {} };
                })
            );
    }

    displayFinancialAccountSearch(): Observable<FinancialAccountDisplayRes> {
        return this.apiService.get(this.DISPLAY_FINANCIAL_ACCOUNT_SEARCH).pipe(
            tap((res: FinancialAccountDisplayRes) => {
                res.counties.forEach((county: DropDown) => {
                    this.countyMap.set(county.code, county.decode);
                });
                res.accountTypes.forEach((county: DropDown) => {
                    this.typeMap.set(county.code, county.decode);
                });
                res.status.forEach((county: DropDown) => {
                    this.statusMap.set(county.code, county.decode);
                });
            })
        );
    }

    postFinancialAccountSearch(search): Observable<FinancialAccountSearchRes> {
        return this.apiService.post(this.FINANCIAL_ACCOUNT_DETAIL_URL + '/search', search).pipe(
            map((res: FinancialAccountSearchRes) => {
                res.financialAccountSearch.forEach((dto) => {
                    dto.status = this.statusMap.get(dto.status);
                    dto.county = this.countyMap.get(dto.county);
                    dto.accountType = this.typeMap.get(dto.accountType);
                    if (!dto.transactionCount) {
                        dto.transactionCount = 0;
                    }
                });
                return res;
            })
        );
    }

    getRegionalAccountDetails(obj) {
        return this.apiService.post(this.REGIONAL_ACCOUNT_DETAILS, obj);
    }
}
