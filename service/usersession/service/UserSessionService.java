package us.tx.state.dfps.service.usersession.service;

import us.tx.state.dfps.service.common.response.UserSessionRes;
import us.tx.state.dfps.service.usersession.dto.UserSessionDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Interface does insert and update operation Sep 7, 2017- 3:15:21 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface UserSessionService {

	/**
	 * Method Name:insertIntoUserSession Method Description:Method
	 * insertIntoUserSession inserts a UserSession record upon login.
	 * 
	 * @param userSessionDto
	 * @return UserSessionRes @
	 */
	public UserSessionRes insertIntoUserSession(UserSessionDto userSessionDto);

	/**
	 * Method Name:updateUserSession Method Description:This method updates the
	 * UserSession upon the session's destruction.
	 * 
	 * @param userSessionDto
	 * @return UserSessionRes @
	 */
	public UserSessionRes updateUserSession(UserSessionDto userSessionDto);
}