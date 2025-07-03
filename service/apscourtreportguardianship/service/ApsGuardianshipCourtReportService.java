package us.tx.state.dfps.service.apscourtreportguardianship.service;

import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Guardianship Court Report Service -- aps court report for guardianship CIV22O00.
 * Dec 28, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

public interface ApsGuardianshipCourtReportService {

    /**
     *
     * Method Name: getServiceAuthFormData Method Description: Aps Guardianship Court Report Service.
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsGuardianshipCourtReport(ApsCommonReq apsCommonReq);

    /**
     * Method Name: getApsGuardianshipDetailsReport Method Description: Request to get
     * the Service Auth Form Data in prefill data format.
     * @param apsGuardianshipDetailReportReq
     * @return
     */
    PreFillDataServiceDto getApsGuardianshipDetailsReport(ApsCommonReq apsGuardianshipDetailReportReq);

}
