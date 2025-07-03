/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: interface to save /update Change stage type
 *Apr 18, 2018- 11:20:41 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.common.request.ChgStgTypeAudReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 9, 2018- 12:19:34 PM © 2017 Texas Department of
 * Family and Protective Services.
 */
public interface ChgStgTypeAudService {

	/**
	 * Call change Stage type update service.
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @return the common string res
	 */
	CommonStringRes callChgStgTypeAudService(ChgStgTypeAudReq pInputMsg);

	/**
	 * Call CSESC 9 D.
	 *
	 * @param pInputMsg
	 *            the input message
	 * @return the int
	 */
	int callCSESC9D(ChgStgTypeAudReq pInputMsg);
}
