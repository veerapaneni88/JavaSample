package us.tx.state.dfps.service.approval.dao;

import java.util.List;

import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyReq;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyRes;

public interface ApprovalStatusResourceHistyDao {

	/**
	 * Method Name: fetchResourceHisty Method Description: This dao call will
	 * retrieve a row from the RESOURCE HISTORY table where the effective Date
	 * is the most recent date and FA home status is <> 'PV' or 'SP'
	 * 
	 * @param ApprovalStatusResourceHistyReq
	 * @return ApprovalStatusResourceHistyRes
	 */
	public List<ApprovalStatusResourceHistyRes> fetchResourceHisty(
			ApprovalStatusResourceHistyReq approvalStatusResourceHistyReq);

}
