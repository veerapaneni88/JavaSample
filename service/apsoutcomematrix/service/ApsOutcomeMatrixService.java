package us.tx.state.dfps.service.apsoutcomematrix.service;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Outcome Matrix Form/Narrative - civ34o00.
 * Jan 28th, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public interface ApsOutcomeMatrixService {

    /**
     * Method Name: getApsOutcomeMatrixData Method Description: Outcome Matrix Form/Narrative - civ34o00.
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsOutcomeMatrixData(ApsCommonReq apsCommonReq);
}
