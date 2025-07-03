package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.ClosePlacementReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 11,
 * 2018- 11:51:32 AM © 2018 Texas Department of Family and Protective Services
 */
public interface ClosePlacementDao {

	/**
	 * Method Name: closePlacement Method Description : Close open DA/DD
	 * placement from PLACEMENT given ID_PLCMT_EVENT. Dam Name: CSUB87D Service
	 * Name: CCMN35S
	 * 
	 * @param ClosePlacementReq
	 * @return
	 */
	public void closePlacement(ClosePlacementReq closePlacementReq);

}
