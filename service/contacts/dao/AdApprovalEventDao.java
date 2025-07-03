package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ApproverEventDto;

public interface AdApprovalEventDao {
	/**
	 * 
	 * Method Name: adApprovalEventLink Method Description:This method delete
	 * and insert the record in APPROVAL_EVENT_LINK
	 * 
	 * @param approverEventDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long adApprovalEventLink(ApproverEventDto approverEventDto);

}