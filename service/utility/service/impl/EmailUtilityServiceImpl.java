package us.tx.state.dfps.service.utility.service.impl;

import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.EmailLog;
import us.tx.state.dfps.service.common.request.EmailLogReq;
import us.tx.state.dfps.service.common.request.EmailSendReq;
import us.tx.state.dfps.service.common.response.EmailLogRes;
import us.tx.state.dfps.service.common.response.EmailSendRes;
import us.tx.state.dfps.service.common.response.EmailSendRes.EmailSentStatus;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.utility.dao.EmailUtilityDao;
import us.tx.state.dfps.service.utility.service.EmailUtilityService;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Service Name: EmailUtilityBean
 * Class Description: This is used to log after emails being sent in the
 * email_log table. Aug 5, 2017 - 3:50:30 PM
 */

@Service
@Transactional
public class EmailUtilityServiceImpl implements EmailUtilityService {

	@Autowired
	EmailUtilityDao emailLogDao;

	private static final Logger log = Logger.getLogger(EmailUtilityServiceImpl.class);

	/**
	 * 
	 * Method Description: EJB service name - EmailUtilityBean. This method
	 * calls the JavaMail API to send the email with attachments
	 * 
	 * @param emailLogReq
	 * @return EmailLogRes @
	 */
	public EmailSendRes sendEmail(EmailSendReq emailSendReq) {
		EmailSendRes emailSendRes = new EmailSendRes();
		try {
			// Create Mime Message Object.
			MimeMessage message = createMimeMessageObj(emailSendReq);
			// Add Mail Address to Mail MimeMessage.
			addAddrToMessage(emailSendReq, message);
			// Add header and message body.
			message.setSubject(emailSendReq.getTxtSubject());
			Multipart multipart = createMessageBody(emailSendReq);
			// Put all message parts in the message
			message.setContent(multipart);
			// Send The Email Message
			Transport.send(message);
		} catch (SendFailedException sfe) {
			log.debug("Exception in sending mail" + sfe.getMessage());
			emailSendRes.setEmailStatus(EmailSentStatus.COMM_FAILURE);
			emailSendRes.setEmailSent(false);
			ServiceLayerException serviceLayerException = new ServiceLayerException(sfe.getMessage());
			serviceLayerException.initCause(sfe);
			throw serviceLayerException;
		} catch (MessagingException me) {
			log.debug("Exception in sending mail" + me.getMessage());
			emailSendRes.setEmailStatus(EmailSentStatus.COMM_FAILURE);
			emailSendRes.setEmailSent(false);
			ServiceLayerException serviceLayerException = new ServiceLayerException(me.getMessage());
			serviceLayerException.initCause(me);
			throw serviceLayerException;
		} catch (Exception exception) {
			log.debug("Exception in sending mail" + exception.getMessage());
			emailSendRes.setEmailStatus(EmailSentStatus.COMM_FAILURE);
			emailSendRes.setEmailSent(false);
			ServiceLayerException serviceLayerException = new ServiceLayerException(exception.getMessage());
			serviceLayerException.initCause(exception);
			throw serviceLayerException;
		}
		emailSendRes.setEmailSent(true);

		return emailSendRes;
	}

	/**
	 * This method creates Message body from EamilvalueBean Content.
	 * 
	 * @param EmailSendReq
	 * 
	 * @return Multipart
	 */
	private Multipart createMessageBody(EmailSendReq emailSendReq) throws MessagingException {
		// Create a message part to represent the body text

		StringBuffer donNotReplyMessage = new StringBuffer(TXT_DEFAULT_ENDING_MESG);
		StringBuffer messageFooter = new StringBuffer();
		if (emailSendReq.getTxtContextType().equals("text/html")) {
			messageFooter.append("<br/><br/><br/><br/>");
		} else {
			messageFooter.append("\n\n\n\n");
		}
		messageFooter.append(emailSendReq.getTxtMessageFooter());
		Multipart multipart = new MimeMultipart("alternative");
		MimeBodyPart alternativeBodyPart = new MimeBodyPart();
		if (emailSendReq.getTxtContextType().equals("text/html")) {
			alternativeBodyPart.setContent(emailSendReq.getTxtMessage() + messageFooter,
					emailSendReq.getTxtContextType());
		} else {
			alternativeBodyPart.setText(emailSendReq.getTxtMessage() + messageFooter + donNotReplyMessage);
		}
		multipart.addBodyPart(alternativeBodyPart);

		return multipart;
	}

	/**
	 * This method creates MimeMessage Object
	 * 
	 * @param EmailSendReq
	 * 
	 * @return MimeMessage
	 */
	private MimeMessage createMimeMessageObj(EmailSendReq emailSendReq) {
		// Setup mail server
		Properties props = System.getProperties();
		props.put(MAIL_SMTP_HOST, emailSendReq.getTxtMailHostName());
		int smtpPort = 25;
		props.put(MAIL_SMTP_PORT, "" + smtpPort);

		// Get a mail session
		Session mailSession = Session.getInstance(props, null);
		// Define a new mail message
		MimeMessage message = new MimeMessage(mailSession);
		return message;
	}

	/**
	 * This method adds From, To, CC and Bcc Addresses to MimeMessage.
	 * 
	 * @param EmailSendReq
	 * @param message
	 *            by reference
	 */
	private void addAddrToMessage(EmailSendReq emailSendReq, MimeMessage message)
			throws AddressException, MessagingException {
		// Get email Addresses.
		String fromAdress = emailSendReq.getTxtFromAddress();
		String toAddress = parseEmailAddress(emailSendReq.getTxtRecipientTo());
		// Add addresses.
		InternetAddress[] toAddressList = InternetAddress.parse(toAddress);
		message.addRecipients(Message.RecipientType.TO, toAddressList);
		if (StringUtils.isNotBlank(emailSendReq.getTxtRecipientCopy())) {
			String ccAddress = parseEmailAddress(emailSendReq.getTxtRecipientCopy());
			InternetAddress[] ccAddressList = InternetAddress.parse(ccAddress);
			message.addRecipients(Message.RecipientType.CC, ccAddressList);
		}
		if (StringUtils.isNotBlank(emailSendReq.getTxtRecipientBcc())) {
			String bccAddress = parseEmailAddress(emailSendReq.getTxtRecipientBcc());
			InternetAddress[] bccAddressList = InternetAddress.parse(bccAddress);
			message.addRecipients(Message.RecipientType.BCC, bccAddressList);
		}
		message.setFrom(new InternetAddress(fromAdress));
		message.setReplyTo(new javax.mail.Address[]{new InternetAddress(fromAdress)});
	}

	/**
	 * Strips the email address from the formated string of type "firstname,
	 * lastName(email@domain);" to comma separated address to
	 * "email@domain,email2@domain2".
	 * 
	 * @param emailAddress
	 * @return
	 */
	private String parseEmailAddress(String emailAddr) {
		String emailAddress = emailAddr.trim();
		StringBuilder stringBuffer = new StringBuilder();
		StringTokenizer stringToken = new StringTokenizer(emailAddress, ";");
		while (stringToken.hasMoreTokens()) {
			String tempString = stringToken.nextToken();
			if (StringUtils.isNotBlank(tempString)) {
				if (tempString.contains("(") && tempString.contains(")")) {
					int startIndex = tempString.indexOf('(');
					int endIndex = tempString.indexOf(')');
					stringBuffer.append(tempString.substring(startIndex + 1, endIndex).trim());

				} else {
					stringBuffer.append(tempString.trim());
				}
				stringBuffer.append(',');
			}
		}
		int lastIndexChar = stringBuffer.lastIndexOf(",");
		if (lastIndexChar == stringBuffer.length() - 1) {
			stringBuffer.deleteCharAt(lastIndexChar);
		}
		String finalEmailAddress = stringBuffer.toString();
		return finalEmailAddress;
	}

	/**
	 * 
	 * Method Description: EJB service name - EmailUtilityBean
	 * 
	 * @param emailLogReq
	 * @return EmailLogRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public EmailLogRes insertEmailLog(EmailLogReq emailLogReq) {
		EmailLogRes emailLogRes = new EmailLogRes();

		EmailLog emailLog = new EmailLog();
		emailLog.setCdEmailType(emailLogReq.getCdEmailType());
		emailLog.setIdEmailRecipientPerson(emailLogReq.getIdRecipientPerson());
		emailLog.setTxtEmailRecipient(emailLogReq.getTxtRecipientEmail());
		emailLog.setIdEmailSenderPerson(emailLogReq.getIdSenderPerson());
		emailLog.setTxtEmailSender(emailLogReq.getTxtSenderEmail());
		emailLog.setIdRecCheck(emailLogReq.getIdRecCheck());
		emailLog.setDtSent(new Date());
		emailLog.setTsLastUpdate(new Date());

		emailLogDao.insertEmailLog(emailLog);

		emailLogRes.setIdEmailLog(emailLog.getIdEmailLog());
		log.debug("emailLog.getIdEmailLog(): " + emailLog.getIdEmailLog());
		return emailLogRes;
	}

	private static final String TXT_DEFAULT_ENDING_MESG = "\n\n\nPlease do not respond to this email.";

	private static final String MAIL_SMTP_PORT = "mail.smtp.port";

	private static final String MAIL_SMTP_HOST = "mail.smtp.host";

}
