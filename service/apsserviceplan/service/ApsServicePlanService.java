package us.tx.state.dfps.service.apsserviceplan.service;

import org.springframework.stereotype.Repository;
import us.tx.state.dfps.service.apsserviceplan.dto.ApsServicePlanCompleteDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

@Repository
public interface ApsServicePlanService {

    /**
     * Method Name: getApsServicePlanReport Method Description: Aps Service Plan  -- APSSP.
     *
     * @param commonHelperReq
     * @return PreFillDataServiceDto
     */
    PreFillDataServiceDto getApsServicePlanReport(CommonHelperReq commonHelperReq);

    ApsServicePlanCompleteDto getServicePlanCompleteDetails(Long idStage);


    Long getPcsActionCategoryCount(Long idStage);
}
