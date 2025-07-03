package us.tx.state.dfps.service.usersession.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.response.UserSessionRes;
import us.tx.state.dfps.service.usersession.dao.UserSessionDao;
import us.tx.state.dfps.service.usersession.dto.UserSessionDto;
import us.tx.state.dfps.service.usersession.service.UserSessionService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * does insert and update operation Sep 7, 2017- 3:15:11 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class UserSessionServiceImpl implements UserSessionService {

	@Autowired
	UserSessionDao userSessionDao;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: insertIntoUserSession Method Description:Method
	 * insertIntoUserSession inserts a UserSession record upon login.
	 * 
	 * @param userSessionDto
	 * @return UserSessionRes @
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public UserSessionRes insertIntoUserSession(UserSessionDto userSessionDto) {

		UserSessionRes userSessionRes = new UserSessionRes();
		Long idImpactUserSession = userSessionDao.insertIntoUserSession(userSessionDto);
		userSessionDto.setIdImpactUserSession(idImpactUserSession.longValue());
		userSessionRes.setUserSessionDto(userSessionDto);
		return userSessionRes;
	}

	/**
	 * Method Name: updateUserSession Method Description:This method updates the
	 * UserSession upon the session's destruction.
	 * 
	 * @param userSessionDto
	 * @return UserSessionRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public UserSessionRes updateUserSession(UserSessionDto userSessionDto) {

		UserSessionRes userSessionRes = new UserSessionRes();
		userSessionRes.setUpdateResult(userSessionDao.updateUserSession(userSessionDto));
		userSessionRes.setUserSessionDto(userSessionDto);
		return userSessionRes;
	}

}