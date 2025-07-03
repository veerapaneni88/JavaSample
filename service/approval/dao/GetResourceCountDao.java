package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.GetResourceHstryReq;
import us.tx.state.dfps.approval.dto.GetResourceHstryRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 8,
 * 2018- 3:38:32 PM © 2018 Texas Department of Family and Protective Services
 */
public interface GetResourceCountDao {
	public GetResourceHstryRes getResourceCount(GetResourceHstryReq getResourceHstryReq);

}
