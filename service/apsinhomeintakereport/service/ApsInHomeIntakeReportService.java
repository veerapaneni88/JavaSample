package us.tx.state.dfps.service.apsinhomeintakereport.service;

import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for APS Inhome Intake Report Nov 02, 2021- 3:43:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ApsInHomeIntakeReportService {

    /**
     * Method Name: getIntakeReport Method Description: Gets data for APS Inhome Intake
     * Report and returns prefill data (form CFIN0300)
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto @
     */
    public PreFillDataServiceDto getIntakeReport(ApsCommonReq apsCommonReq);

    /**
     Method Name: getapsinhomereportnore Method Description: Gets data for AIntake Report Adult Protective Services (Minus
     * 	Reporter) and returns prefill data (from cfin0700)
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto @
     */

    public PreFillDataServiceDto getapsinhomereportnorep(ApsCommonReq apsCommonReq);

}
