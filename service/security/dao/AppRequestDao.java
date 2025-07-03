package us.tx.state.dfps.service.security.dao;

import us.tx.state.dfps.service.security.dto.AppReqDto;
import us.tx.state.dfps.service.security.request.SSOTokenReq;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Description: Interface to perform
 * operations on APP_REQUEST table
 */
public interface AppRequestDao {
	/**
	 * 
	 * Method Description:insertToken - This method inserts a row in APP_REQUEST
	 * and generates the SSO token.
	 * 
	 * @param ssoTokenReq
	 * @return appReqDto @
	 */
	public AppReqDto insertToken(SSOTokenReq ssoTokenReq);

	/**
	 * Method Description:This method gets the username from the ldap link table
	 * 
	 * @param idPerson
	 * @return
	 */
	public String getUserNameFromLogonAsId(Long idPerson);
}
