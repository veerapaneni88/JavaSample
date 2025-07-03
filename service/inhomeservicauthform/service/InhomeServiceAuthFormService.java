package us.tx.state.dfps.service.inhomeservicauthform.service;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Inhome Service
 * Authorization form--CCN02O00 used by CPS to refer clients for paid services under PRS
 * contracts. Nov 5, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Repository
public interface InhomeServiceAuthFormService {

    /**
     *
     * Method Name: getServiceAuthFormData Method Description: Inhome Service
     * Authorization form used by APS to refer clients for paid services under
     * PRS contracts.
     *
     * @param commonHelperReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getInhomeServiceAuthFormData(ApsCommonReq apsCommonReq);

}
