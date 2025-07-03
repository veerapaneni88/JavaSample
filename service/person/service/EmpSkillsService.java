package us.tx.state.dfps.service.person.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.RtrvSubPersIdsReq;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN48S Class
 * Description: This class is use for retrieving unit id Apr 9, 2017 - 4:19:51
 * PM
 */
public interface EmpSkillsService {

	/**
	 * 
	 * Method Description: This Method will retrieve a sub-set of the
	 * ID_PERSON's passed into this service who have the SKILL(S) which are
	 * passed into this method Service Name: CCMN48S
	 * 
	 * @param rtrvSubPersIdsReq
	 * @return List<Long> @
	 */

	public List<Long> getPersonIds(RtrvSubPersIdsReq rtrvSubPersIdsReq);

}
