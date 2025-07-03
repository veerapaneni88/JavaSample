package us.tx.state.dfps.service.disasterplan.service;

import us.tx.state.dfps.service.common.request.DisasterPlanReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * DisasterPlanService will have all operation which are mapped to DisasterPlan
 * module. Feb 9, 2018- 2:01:02 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface DisasterPlanService {

	public PreFillDataServiceDto getDisasterPlan(DisasterPlanReq disasterPlanReq);

}
