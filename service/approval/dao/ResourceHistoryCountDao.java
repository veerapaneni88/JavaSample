package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.ResourceHistoryCountReq;
import us.tx.state.dfps.approval.dto.ResourceHistoryCountRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 15,
 * 2018- 05:25:32 PM © 2018 Texas Department of Family and Protective Services
 */
public interface ResourceHistoryCountDao {

	/**
	 * Method Name:resourceHistoryCount Method Description: This method will
	 * count the resource history based on the ID_RSHS_FA_HOME_STAGE and
	 * CD_RSHS_FA_HOME_STATUS
	 * 
	 * Dam Name: CMSC46D Service Name: CCMN35S
	 * 
	 * @param resourceHistoryCountReq
	 * @return ResourceHistoryCountRes
	 */
	public ResourceHistoryCountRes resourceHistoryCount(ResourceHistoryCountReq resourceHistoryCountReq);

}
