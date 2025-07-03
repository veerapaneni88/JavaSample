package us.tx.state.dfps.service.apsinvsummary.service;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Investigation Summary -- CFIV1200.
 * Dec 14, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Repository
public interface ApsInvSummaryService {

    /**
     * Method Name: getapsinvsummarydata Method Description: Aps Investigation Summary -- CFIV1200.
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getapsinvsummarydata(ApsCommonReq apsCommonReq);
}
