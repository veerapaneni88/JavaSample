package us.tx.state.dfps.service.apscareformsnarrative.service;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Care Forms Narrative - civ35o00.
 * Jan 19th, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Repository
public interface ApsCareFormsNarrativeService {
    /**
     * Method Name: getApsCareFormsNarrative Method Description: Aps Care Forms Narrative - civ35o00.
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsCareFormsNarrativeData(ApsCommonReq apsCommonReq);
}
