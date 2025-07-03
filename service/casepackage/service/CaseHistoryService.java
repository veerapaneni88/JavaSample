/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 17, 2017- 10:35:08 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.common.request.RetrvCaseHistoryReq;
import us.tx.state.dfps.service.common.request.RetrvCaseHistoryRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 17, 2017- 10:35:08 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface CaseHistoryService {

	/**
	 * 
	 * Method Name: getCaseHistory Method Description: Retrives Case History
	 * details for the passed case id
	 * 
	 * @param idCase
	 * @return RetrvCaseHistoryRes @
	 */
	public RetrvCaseHistoryRes getCaseHistory(RetrvCaseHistoryReq retrvCaseHistoryReq);
}
