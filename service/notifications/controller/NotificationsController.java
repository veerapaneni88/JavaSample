package us.tx.state.dfps.service.notifications.controller;

import java.util.Arrays;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NotificationsReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.NotificationsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.notifications.service.NotifToReporterService;
import us.tx.state.dfps.service.notifications.service.NotificationsService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:(CINT33S)
 * NotificationsController Aug 11, 2017- 12:03:44 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/notifications")
public class NotificationsController {

	@Autowired
	NotificationsService notificationsService;

	@Autowired
	NotifToReporterService notifToReporterService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(NotificationsController.class);

	/**
	 * 
	 * Method Name: rtvNotifications Method Description:This service will update
	 * the contact table and return a contact if one occurred before.
	 * 
	 * @param notificationsReq
	 * @return NotificationsRes
	 */
	@RequestMapping(value = "/rtvnotifications", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public NotificationsRes rtvNotifications(@RequestBody NotificationsReq notificationsReq) {
		log.debug("Entering method rtvNotifications in NotificationsController");
		try {

			if (ObjectUtils.isEmpty(notificationsReq.getIdCase())) {
				throw new InvalidRequestException(
						messageSource.getMessage("Notifications.UlIdCase.mandatory", null, Locale.US));
			}
			if (ObjectUtils.isEmpty(notificationsReq.getIdStage())) {
				throw new InvalidRequestException(
						messageSource.getMessage("Notifications.UlIdStage.mandatory", null, Locale.US));
			}
			if (ObjectUtils.isEmpty(notificationsReq.getIdEvent())) {
				throw new InvalidRequestException(
						messageSource.getMessage("Notifications.UlIdEvent.mandatory", null, Locale.US));
			}

			if (ObjectUtils.isEmpty(notificationsReq.getIdPerson())) {
				throw new InvalidRequestException(
						messageSource.getMessage("Notifications.UlIdPerson.mandatory", null, Locale.US));
			}

			if ((!ObjectUtils.isEmpty(notificationsReq.getCdTask())
					&& ServiceConstants.TODO_LE_NOTIF_TASK.equals(notificationsReq.getCdTask()))
					&& ObjectUtils.isEmpty(notificationsReq.getIdTodo())) {
				throw new InvalidRequestException(
						messageSource.getMessage("Notifications.LdIdTodo.mandatory", null, Locale.US));
			}

		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("NotificationsController.data.not.found", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		}
		NotificationsRes notificationsRes = notificationsService.callNotificationsService(notificationsReq);
		log.debug("Exiting method NotificationsController in NotificationsController");
		return notificationsRes;
	}

	/**
	 * TUX service: CINT32S/cfin0900 Method Description: This service will
	 * return the data needed for the form letter to reporter
	 *
	 * @param notificationsReq
	 * 
	 * @return the letter to reporter form
	 */
	@RequestMapping(value = "/getnotifreporterform", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getNotifReporter(@RequestBody NotificationsReq notificationsReq) {
		/*
		if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("notificationsReq.idPerson.mandatory", null, Locale.US));
		}
		*/
		if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("notificationsReq.idCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(notificationsReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("notificationsReq.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(notificationsReq.getCdEventType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("notificationsReq.cdEventType.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(notifToReporterService.getNotificationsService(notificationsReq)));
		log.info("TransactionId :" + notificationsReq.getTransactionId());
		return commonFormRes;
	}



	/**
	 *
	 * Method Name: ariNotifications Method Description:This service will insert
	 * event, event_person_link, contact tables.
	 *
	 * @param notificationsReq
	 * @return NotificationsRes
	 */
	@RequestMapping(value = "/arinotifications", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public NotificationsRes ariNotifications(@RequestBody NotificationsReq notificationsReq) {
		log.debug("Entering method rtvNotifications in NotificationsController");
		try {


			if (ObjectUtils.isEmpty(notificationsReq.getIdStage())) {
				throw new InvalidRequestException(
						messageSource.getMessage("Notifications.UlIdStage.mandatory", null, Locale.US));
			}

			if(!Arrays.asList(ServiceConstants.FORM_CLF03O00,ServiceConstants.FORM_CLF02O00,ServiceConstants.FORM_CLF01O00,
							ServiceConstants.FORM_CCF23O00, ServiceConstants.FORM_CCF13O00,ServiceConstants.FORM_CCF09O00,
							ServiceConstants.FORM_CCF11O00,ServiceConstants.FORM_CCF21O00,ServiceConstants.FORM_CLF04O00,
					ServiceConstants.FORM_CCF15O00,ServiceConstants.FORM_CCF16O00)
					.contains(notificationsReq.getFormName())) {
				if (ObjectUtils.isEmpty(notificationsReq.getIdPerson())) {
					throw new InvalidRequestException(
							messageSource.getMessage("Notifications.UlIdPerson.mandatory", null, Locale.US));
				}
			}

			if(!Arrays.asList(ServiceConstants.FORM_CCF11O00,ServiceConstants.FORM_CCF20O00
					,ServiceConstants.FORM_CCF09O00,ServiceConstants.FORM_CCF14O00,ServiceConstants.FORM_CCF11O00,
					ServiceConstants.FORM_CCF15O00,ServiceConstants.FORM_CCF16O00,ServiceConstants.FORM_CCF24O00
					,ServiceConstants.FORM_CCF10O00).contains(notificationsReq.getFormName())) {
				if (ObjectUtils.isEmpty(notificationsReq.getFormName())) {
					throw new InvalidRequestException(
							messageSource.getMessage("Notifications.FormName.mandatory", null, Locale.US));
				}
			}

		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("NotificationsController.data.not.found", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		}
		NotificationsRes notificationsRes = notificationsService.callNotificationsService(notificationsReq);
		log.debug("Exiting method NotificationsController in NotificationsController");
		return notificationsRes;
	}

}
