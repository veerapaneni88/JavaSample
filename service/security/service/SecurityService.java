package us.tx.state.dfps.service.security.service;

import us.tx.state.dfps.service.security.dto.AppReqDto;
import us.tx.state.dfps.service.security.request.SSOTokenReq;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Class Description: This is Class has
 * methods pertaining to security related operations. Apr 12, 2017 - 1:32:43 PM
 */
public interface SecurityService {

	/**
	 * 
	 * Method Description:insertToken
	 * 
	 * @param ssoTokenReq
	 * @return appReqDto @
	 */
	public AppReqDto insertToken(SSOTokenReq ssoTokenReq);

}
