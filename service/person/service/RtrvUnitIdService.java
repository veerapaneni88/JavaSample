package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.service.common.request.RtrvUnitIdReq;
import us.tx.state.dfps.service.common.response.RtrvUnitIdRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN47S Class
 * Description: This class is use for retrieving unit id Mar 24, 2017 - 3:19:51
 * PM
 */
public interface RtrvUnitIdService {

	/**
	 * 
	 * Method Description: This Method will retrieve ID UNIT for a Parent Unit,
	 * given CD UNIT PROGRAM, CD UNIT REGION, and NBR UNIT. Service Name:
	 * CCMN47S
	 * 
	 * @param rtrvUnitIdReq
	 * @return RtrvUnitIdRes @
	 */

	public RtrvUnitIdRes getUnitId(RtrvUnitIdReq rtrvUnitIdReq);

	public RtrvUnitIdRes getUnitCandidateId(RtrvUnitIdReq rtrvUnitIdReq);


}
