package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.GetPrimaryWorkerReq;
import us.tx.state.dfps.approval.dto.GetPrimaryWorkerRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 11,
 * 2018- 1:05:32 PM © 2018 Texas Department of Family and Protective Services
 */
public interface AprvlGetPrimaryWorkerDao {
	public GetPrimaryWorkerRes getPrimaryWorkerIdByStage(GetPrimaryWorkerReq getPrimaryWorkerReq);

}
