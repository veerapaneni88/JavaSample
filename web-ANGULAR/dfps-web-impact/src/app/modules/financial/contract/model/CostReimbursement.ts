export interface CostReimbursementDetails {
    contractService: {
        adminAllUsedAmount: number;
        budgetBalanceAmount: number;
        contractId: number;
        contractNumber: string;
        contractServiceId: number;
        contractWorkerId: number;
        countyCodes: []
        equipmentAmount: number;
        equipmentUsedAmount: number;
        federalMatch: number;
        fringeBenefitAmount: number;
        fringeBenefitUsedAmount: number;
        isNewRow: string;
        lastUpdatedDate: string;
        lineItem: number;
        localMatch: number;
        offsetItemUsedAmount: number;
        otherAmount: number;
        otherUsedAmount: number;
        paymentType: string;
        period: number;
        salaryAmount: number;
        salaryUsedAmount: number;
        serviceCode: string;
        supplyAmount: number;
        supplyUsedAmount: number;
        totalAmount: number;
        totalUnits: number;
        travelAmount: number;
        travelUsedAmount: number;
        unitRate: number;
        unitRateAmount: number;
        unitRateUsedAmount: number;
        unitType: string;
        usedUnits: number;
        version: number;
    };
    pageMode: string;
    paymentTypes: [];
    services: [];
    unitTypes: [];
}

