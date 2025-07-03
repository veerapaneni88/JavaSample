package us.tx.state.dfps.service.apssna.service;

import org.springframework.stereotype.Service;
import us.tx.state.dfps.service.apssna.dto.ApsStrengthsAndNeedsAssessmentDto;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ApsSnaServicefor APSSNA Form Service.
 * July 14, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@Service
public interface ApsSnaFormService {

    /**
     * to get ApsSna details for ApsSnaForm Service
     * @param apsCommonReq
     * @return
     */
    public PreFillDataServiceDto getApsSnaDetails(ApsCommonReq apsCommonReq);

    /* to get getApsSna Details for Aps Case Review **/
    public List<ApsStrengthsAndNeedsAssessmentDto> getApsSnaDetailsforCaseReview(Long idCase);

}
