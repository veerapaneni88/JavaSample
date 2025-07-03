package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.UpdateFacilityLocReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 20,
 * 2018- 12:18:15 PM © 2018 Texas Department of Family and Protective Services
 */
public interface UpdateFacilityLocDao {

	/**
	 * Service Name: updateFacilityLoc Service Description: This service Used to
	 * end date a facility loc row. Dam Name: CAUDB4D Service Name: CCMN35S
	 * 
	 * @param @
	 */
	public void updateFacilityLoc(UpdateFacilityLocReq updateFacilityLocReq);
}
