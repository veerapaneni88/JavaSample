package us.tx.state.dfps.service.usersession.dao;

import us.tx.state.dfps.service.usersession.dto.UserSessionDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Dao
 * does insert and update operation Sep 8, 2017- 3:22:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface UserSessionDao {

	/**
	 * 
	 * Method Name:insertIntoUserSession Method Description: Method
	 * insertIntoUserSession inserts a UserSession record upon login.
	 * 
	 * @param userSessionDto
	 * @return long
	 */
	public long insertIntoUserSession(UserSessionDto userSessionDto);

	/**
	 * 
	 * Method Name: updateUserSession Method Description:Method
	 * updateUserSession updates the CD_LOGOUT_TYPE relating to how the session
	 * ended
	 * 
	 * @param userSessionDto
	 * @return long
	 */
	public long updateUserSession(UserSessionDto userSessionDto);

}