package us.tx.state.dfps.service.approval.service;

import us.tx.state.dfps.approval.dto.ApprovalSecondaryCommentsDto;
import us.tx.state.dfps.service.common.request.ApprovalFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Approval Form Service Mar 14, 2018- 10:39:08 AM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface ApprovalFormService {

	/**
	 * Method Name: getApprovalFormData Method Description: Retrieves data for
	 * Approval Form from DB
	 * 
	 * @param approvalFormReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getApprovalFormData(ApprovalFormReq approvalFormReq);

	/**
	 * Method Name: getApprovalSecondaryComments Method Description: to get
	 * approval comments
	 * 
	 * @param approvalFormReq
	 * @return ApprovalSecondaryCommentsDto
	 */
	ApprovalSecondaryCommentsDto getApprovalSecondaryComments(ApprovalFormReq approvalFormReq);
}
