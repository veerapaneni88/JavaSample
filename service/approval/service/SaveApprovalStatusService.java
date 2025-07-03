package us.tx.state.dfps.service.approval.service;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SaveApprovalStatusReq;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.common.response.ChildPlanParticipantEmailRes;

public interface SaveApprovalStatusService {
	/**
	 * Method Name: SaveApvlStsService Method Description: Saves information
	 * change on Approval Window (CCMN65W). Updates the status of all related
	 * events to the Approval. Sends out appropriate To-Do notifications.
	 * 
	 * @param pInputMsg
	 * @return
	 * 
	 */
	public ApprovalStatusRes saveApprovalStatus(SaveApprovalStatusReq saveApprovalStatusReq);

	ChildPlanParticipantEmailRes getParticipants(SaveApprovalStatusReq saveApprovalStatusReq);

	ChildPlanParticipantEmailRes updateDateCopyProvided(CommonHelperReq commHelperReq);
}
