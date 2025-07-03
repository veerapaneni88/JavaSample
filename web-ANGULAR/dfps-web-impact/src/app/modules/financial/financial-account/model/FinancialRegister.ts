import { DropDown } from 'dfps-web-lib';
import { FinancialAccount, FinancialAccountSearch } from './FinancialAccount';

export interface FinancialTransactionRes {
    financialAccount?: FinancialAccount;
    financialAccountTransactions?: FinancialTransaction[];
    financialTransaction?: FinancialTransaction;
    pageMode: string;
    debitCategory?: DropDown[];
    creditCategory?: DropDown[];
    subCategory?: DropDown[];
    account?: DropDown[];
    status?: DropDown[];
    accountNumbers?: FinancialAccountSearch[];
    editAcctRegTranMode?: any;
    disableStatus:boolean;
    disableDescription:boolean;
}

export interface FinancialTransaction {
    transferFinancialAccountId: string,
    id: string;
    serviceDetailId: string;
    invoiceId: string;
    financialAccountId: string;
    financialPaymentGroupId: string;
    amount: string;
    balance: string;
    category: string;
    type: string;
    transactionDate: string;
    transactionCount: string;
    warrantNumber: string;
    description: string;
    subcategory: string;
    payeeName: string;
    reconciled: boolean;
    employeeLogonId: string;
    ach: boolean;
    accountHoldDate: string;
    workerName: string;
    status: string;
    issuedDate: string;
    achNumber: string;
    lastUpdatedDate: string;
}
