package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.CreateStageProgressionEventReq;
import us.tx.state.dfps.approval.dto.CreateStageProgressionEventRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 20,
 * 2018- 4:47:32 PM © 2018 Texas Department of Family and Protective Services
 */
public interface CreateStageProgressionEventDao {

	public CreateStageProgressionEventRes createStageProgressionEvent(
			CreateStageProgressionEventReq createStageProgressionEventReq);
}
