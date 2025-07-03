package us.tx.state.dfps.service.utility.controller;

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
import us.tx.state.dfps.service.common.request.EmailLogReq;
import us.tx.state.dfps.service.common.request.EmailSendReq;
import us.tx.state.dfps.service.common.response.EmailLogRes;
import us.tx.state.dfps.service.common.response.EmailSendRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.utility.service.EmailUtilityService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Name: PrincipalCaseHistoryBean
 * Class Description: EmailUtilityController will have all operation which are
 * mapped to email sending and logging feature. Aug 5, 2017 - 3:18:29 PM
 */
@RestController
@RequestMapping("/emailUtility")
public class EmailUtilityController {

	@Autowired
	EmailUtilityService emailUtilityService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(EmailUtilityController.class);

	/**
	 * Method Description: Method to to perform insert operations. This method
	 * will insert email log information into email_log Table. EJB Name:
	 * EmailUtilityBean
	 * 
	 * @param emailLogReq
	 * @return EmailLogRes @
	 */
	@RequestMapping(value = "/insertEmailLog", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  EmailLogRes insertEmailLog(@RequestBody EmailLogReq emailLogReq) {

		if (TypeConvUtil.isNullOrEmpty(emailLogReq.getIdRecipientPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("emailutility.recipientid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(emailLogReq.getIdSenderPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("emailutility.senderid.mandatory", null, Locale.US));
		}

		EmailLogRes emailLogRes = emailUtilityService.insertEmailLog(emailLogReq);
		log.debug("emailLogRes: " + emailLogRes.getIdEmailLog());
		return emailLogRes;
	}

	/**
	 * Method Description: Method to send email. EJB Name: EmailUtilityBean
	 * 
	 * @param emailLogReq
	 * @return EmailLogRes @
	 */
	@RequestMapping(value = "/sendEmail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  EmailSendRes sendEmail(@RequestBody EmailSendReq emailSendReq) {

		EmailSendRes emailSendRes = emailUtilityService.sendEmail(emailSendReq);
		log.debug("emailSendRes: " + emailSendRes.isEmailSent());

		return emailSendRes;
	}
}