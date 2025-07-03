package us.tx.state.dfps.service.usersession.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.UserSessionReq;
import us.tx.state.dfps.service.common.response.UserSessionRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.usersession.service.UserSessionService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:UserSessionController class will have all operation which are
 * related to case and relevant page to case. Sep 8, 2017- 3:14:15 PM © 2017
 * Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/userSession")
public class UserSessionController {

	@Autowired
	UserSessionService userSessionService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(UserSessionController.class);

	/**
	 * 
	 * Method Name: getInsertIntoUserSession Method
	 * Description:insertIntoUserSession inserts a UserSession record upon
	 * login.
	 * 
	 * @param userSessionReq
	 * @return UserSessionRes
	 */
	@RequestMapping(value = "/insertIntoUserSession", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  UserSessionRes getInsertIntoUserSession(@RequestBody UserSessionReq userSessionReq) {

		if (TypeConvUtil.isNullOrEmpty(userSessionReq.getUserSessionDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("userSession.userSessionDto.mandatory", null, Locale.US));
		}

		return userSessionService.insertIntoUserSession(userSessionReq.getUserSessionDto());

	}

	/**
	 * Method Name: getUpdateUserSession Method Description:This Method updates
	 * a UserSession record upon login. which are related to case and relevant
	 * page to case.
	 * 
	 * @param userSessionReq
	 * @return UserSessionRes
	 */
	@RequestMapping(value = "/updateUserSession", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  UserSessionRes getUpdateUserSession(@RequestBody UserSessionReq userSessionReq) {

		if (TypeConvUtil.isNullOrEmpty(userSessionReq.getUserSessionDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("userSession.userSessionDto.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + userSessionReq.getTransactionId());
		return userSessionService.updateUserSession(userSessionReq.getUserSessionDto());

	}
}
