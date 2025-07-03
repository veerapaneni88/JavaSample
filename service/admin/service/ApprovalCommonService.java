package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * interface for CCMN05 Aug 8, 2017- 11:16:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ApprovalCommonService {

	/**
	 * 
	 * Method Name: callCcmn05uService Method Description: This library / common
	 * function will the approval related to a given ID EVENT. Functional Events
	 * are uniquely identified by this input. This function will find the the ID
	 * APPROVAL (ID EVENT of the approval event) from the Approval Event List
	 * Table. This key information will allow the function to complete the
	 * following: Update the Approval Event, Get any other functional events
	 ** related to the same Approval and demote them, set any pending related
	 * approvers to invalid status. Ccmn05u
	 * 
	 * @param approvalCommonInDto
	 * @return ApprovalCommonOutDto
	 */
	public ApprovalCommonOutDto callCcmn05uService(ApprovalCommonInDto approvalCommonInDto);

	ApprovalCommonOutDto InvalidateAprvl(ApprovalCommonInDto pInputMsg, ApprovalCommonOutDto pOutputMsg);
}
