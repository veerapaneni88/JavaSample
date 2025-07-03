package us.tx.state.dfps.service.apscontractLogNarrative.service;

import us.tx.state.dfps.service.common.request.ApsContactLogNarrativeReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for APSCLN-Log of Contact Narratives December 29 2021, 2021- 2:36:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ApsContactLogNarrativeService {

    /**
     * * Method Name: getapscontactlognarrative Method Description: Gets data for AAPSCLN-
     * Log of Contact Narratives and returns prefill data (form APSCLN)
     * @param apsContactLogNarrativeReq
     * @return PreFillDataServiceDto
     */
   public PreFillDataServiceDto getapscontactlognarrative(ApsContactLogNarrativeReq apsContactLogNarrativeReq);
}
