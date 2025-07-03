package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.ApproversUpdInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for ccmn88 Aug 8, 2017- 10:47:29 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ApproversUpdDao {

	/**
	 * 
	 * Method Name: updateApproverStatus Method Description:
	 * 
	 * @param pInputDataRec
	 * @return int @
	 */
	public int updateApproverStatus(ApproversUpdInDto pInputDataRec);
}
