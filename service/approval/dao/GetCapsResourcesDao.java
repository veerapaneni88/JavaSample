package us.tx.state.dfps.service.approval.dao;

import java.util.List;

import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscRes;

public interface GetCapsResourcesDao {

	/**
	 * Method Name: getCapsRsc Method Description: This method will return the
	 * FA Home Status for a given Event Id. DAM NAME: CSES43D Service Name:
	 * CCMN35S
	 *
	 * @param SaveApprovalStatusGetCapsRscReq
	 * @return List<SaveApprovalStatusGetCapsRscRes>
	 */
	List<SaveApprovalStatusGetCapsRscRes> getCapsRsc(SaveApprovalStatusGetCapsRscReq saveApprovalStatusGetCapsRscReq);

}
