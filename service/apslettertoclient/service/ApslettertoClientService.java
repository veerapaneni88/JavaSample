package us.tx.state.dfps.service.apslettertoclient.service;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Letter to Client when cannot locate-- CIV37o00.
 * Dec 02, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Repository
public interface ApslettertoClientService {

    /**
     *
     * Method Name: getServiceAuthFormData Method Description: Inhome Service
     * Authorization form used by APS to refer clients for paid services under
     * PRS contracts.
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsLettertoClientData(ApsCommonReq apsCommonReq);

}
