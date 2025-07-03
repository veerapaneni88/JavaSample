package us.tx.state.dfps.service.kinapproval.service;

import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * KinApprovalService May 14, 2018- 8:52:24 AM © 2018 Texas Department of Family
 * and Protective Services
 */
public interface KinApprovalService {

	/**
	 * Method Name: doKinshipResourceApproval Method Description: This method is
	 * used to process kinship resource approval.
	 * 
	 * @param stageId
	 * @return Long
	 * 
	 */
	public Long doKinshipResourceApproval(KinHomeInfoDto kinHomeInfoDto);
}