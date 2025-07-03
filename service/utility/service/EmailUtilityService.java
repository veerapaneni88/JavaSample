package us.tx.state.dfps.service.utility.service;

import us.tx.state.dfps.service.common.request.EmailLogReq;
import us.tx.state.dfps.service.common.request.EmailSendReq;
import us.tx.state.dfps.service.common.response.EmailLogRes;
import us.tx.state.dfps.service.common.response.EmailSendRes;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Service Name: EmailUtilityBean
 * Class Description: This is used to log after emails being sent in the
 * email_log table. Aug 5, 2017 - 3:50:30 PM
 */
public interface EmailUtilityService {

	/**
	 * 
	 * Method Description: EJB service name - EmailUtilityBean. This method
	 * calls the JavaMail API to send the email with attachments
	 * 
	 * @param emailLogReq
	 * @return EmailLogRes @
	 */
	public EmailSendRes sendEmail(EmailSendReq emailSendReq);

	/**
	 * 
	 * Method Description: EJB service name - EmailUtilityBean
	 * 
	 * @param emailLogReq
	 * @return EmailLogRes @
	 */
	public EmailLogRes insertEmailLog(EmailLogReq emailLogReq);

}
