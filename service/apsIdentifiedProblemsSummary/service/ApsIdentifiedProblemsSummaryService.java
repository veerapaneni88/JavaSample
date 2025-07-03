package us.tx.state.dfps.service.apsIdentifiedProblemsSummary.service;

import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import org.springframework.stereotype.Repository;

/**
 * Method Name: getIdentifiedProblemsSummaryDetails
 * Method Description: Retrieve case info and APS client factors based on event Id -- cfiv0700
 *
 * @param apsCommonReq
 * @return PreFillDataServiceDto
 */
@Repository
public interface ApsIdentifiedProblemsSummaryService {
    PreFillDataServiceDto getIdentifiedProblemsSummaryDetails(ApsCommonReq apsCommonReq);
}
