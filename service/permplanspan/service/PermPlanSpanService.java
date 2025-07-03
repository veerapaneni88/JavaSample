package us.tx.state.dfps.service.permplanspan.service;

import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Permanency Planning Meeting service Feb 21, 2018- 2:48:24 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PermPlanSpanService {

	public PreFillDataServiceDto getPermPlan(PpmReq ppmReq);
}
