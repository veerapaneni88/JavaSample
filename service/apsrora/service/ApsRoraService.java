package us.tx.state.dfps.service.apsrora.service;

import us.tx.state.dfps.service.apsrora.dto.ApsRoraDto;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import org.springframework.stereotype.Service;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Risk of recidivism Assessment -- apsrora.
 * Dec 29, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Service
public interface ApsRoraService {

    /**
     * Method Name: getApsRoraFormInformation Method Description: Aps Risk of recidivism Assessment -- apsrora.
     *
     * @param apsRoraReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsRoraFormInformation(ApsCommonReq apsRoraReq);

    ApsRoraDto getApsRoraFullDetails(ApsCommonReq apsRoraReportReq);
}
