/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: interface to retrieve change stage type
 *Apr 18, 2018- 11:20:41 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.service;

import java.util.List;

import us.tx.state.dfps.service.casemanagement.dto.ChgStgTypeRtrvoDto;
import us.tx.state.dfps.service.common.request.ChgStgTypeRtrvReq;

/**
 * The Interface ChgStgTypeRtrvService.
 */
public interface ChgStgTypeRtrvService {

	/**
	 * Call chg stg type rtrv service.
	 *
	 * @param pInputMsg
	 *            the input msg
	 * @return the list
	 */
	List<ChgStgTypeRtrvoDto> callChgStgTypeRtrvService(ChgStgTypeRtrvReq pInputMsg);
}
