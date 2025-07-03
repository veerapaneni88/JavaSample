package us.tx.state.dfps.service.approval.dao;

import us.tx.state.dfps.approval.dto.AprvlStatusUpdateCapsResourceReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: July 8,
 * 2018- 11:16:32 AM © 2018 Texas Department of Family and Protective Services
 */
public interface ApprovalStatusUpdateCapsResourceDao {

	/**
	 * Method Name: updateCapsResource Method Description:This Method will
	 * Executes the query which updates the Resource table given the Id Event.
	 * DAM NAME: CAUD52D Service Name: CCMN35S
	 * 
	 * @param aprvlStatusUpdateCapsResourceReq
	 * @return
	 */
	public void updateCapsResource(AprvlStatusUpdateCapsResourceReq aprvlStatusUpdateCapsResourceReq);

}