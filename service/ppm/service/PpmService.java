package us.tx.state.dfps.service.ppm.service;

import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.forms.dto.PpmDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: interface
 * for Permanency Planning Meeting Feb 2, 2018- 6:36:36 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PpmService {

	public PpmDto getPpm(PpmReq ppmReq);
}
