package us.tx.state.dfps.service.event.controller;

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
import us.tx.state.dfps.service.common.request.EventUtilityReq;
import us.tx.state.dfps.service.common.request.FetchEventReq;
import us.tx.state.dfps.service.common.response.EventUtilityRes;
import us.tx.state.dfps.service.common.response.FetchEventRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;

/**
 * 
 * service-business - IMPACT PHASE 2 EJB MODERNIZATION EJB Bean EventUtilityBean
 * EJB implementation for functions required for implementing Event Utility
 * functionality
 * 
 * EJB Name : EventUtilityBean.java
 * 
 */
@RestController
@RequestMapping("/eventutil")
public class EventUtilityController {

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(EventUtilityController.class);

	/*
	 * Method Description: This method is used to update the cdEventStatus in
	 * Event table/Entity provided input as List events
	 * 
	 * @param eventreq
	 * 
	 * @return EventUtilityRes
	 */

	@RequestMapping(value = "/updateStatusEvents", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  EventUtilityRes updateStatusEvents(@RequestBody EventUtilityReq eventUtilityReq) {

		// Creating object for EventUtilityResponse class

		EventUtilityRes eventUtilityRes = new EventUtilityRes();

		/*
		 * Getting the update from EventUtilityRequest class and assigning it to
		 * updateResult
		 */

		long updateResult = eventUtilityService.updateEventStatus(eventUtilityReq.getEvents());

		// Sending the updateResult value to the EventUtilityResponse class

		eventUtilityRes.setUpdateResult(updateResult);

		log.info("TransactionId :" + eventUtilityReq.getTransactionId());

		// Returning the EventUtilityResponse object

		return eventUtilityRes;

	}

	/*
	 * Method Description: This method is used to update the cdEventStatus in
	 * Event table/Entity provided input as List events, String status
	 * 
	 * 
	 * @param eventreq
	 * 
	 * @return EventUtilityRes
	 */

	@RequestMapping(value = "/updateCdStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  EventUtilityRes updateCdStatus(@RequestBody EventUtilityReq eventUtilityReq) {

		// Creating object for EventUtilityResponse class

		EventUtilityRes eventUtilityRes = new EventUtilityRes();

		/*
		 * Getting the update from EventUtilityRequest class and assigning it to
		 * updateResult
		 */

		long updateResult = eventUtilityService.updateEventStatus(eventUtilityReq.getEvents(),
				eventUtilityReq.getStatus());

		// Sending the updateResult value to the EventUtilityResponse class

		eventUtilityRes.setUpdateResult(updateResult);

		log.info("TransactionId :" + eventUtilityReq.getTransactionId());

		// Returning the EventUtilityResponse object

		return eventUtilityRes;

	}

	/*
	 * Method Description: This method is used to update the cdEventStatus in
	 * Event table/Entity provided input as int idEvent & String cdEventstatus
	 * 
	 * @param eventreq
	 * 
	 * @return EventUtilityRes
	 */

	@RequestMapping(value = "/updateStatusIdEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventUtilityRes updateStatusIdEvent(@RequestBody EventUtilityReq eventUtilityReq) {

		// Creating object for EventUtilityResponse class

		EventUtilityRes eventUtilityRes = new EventUtilityRes();

		/*
		 * Getting the update from EventUtilityRequest class and assigning it to
		 * updateResult
		 */

		long updateResult = eventUtilityService.updateEventStatus(eventUtilityReq.getIdEvent(),
				eventUtilityReq.getCdEventStatus());

		// Sending the updateResult value to the EventUtilityResponse class

		eventUtilityRes.setUpdateResult(updateResult);

		log.info("TransactionId :" + eventUtilityReq.getTransactionId());

		// Returning the EventUtilityResponse object

		return eventUtilityRes;

	}

	/*
	 * Method Description: This method is used to update the status to COMP
	 * provided input as stageClosureEventId
	 *
	 * @param eventReq
	 * 
	 * @return EventUtilityRes
	 * 
	 */

	@RequestMapping(value = "/invalidatePendingStageClosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventUtilityRes invalidatePendingStageClosure(@RequestBody EventUtilityReq eventReq) {

		// Creating object for EventUtilityResponse class
		EventUtilityRes eventUtilityRes = new EventUtilityRes();
		/*
		 * Getting the update from EventUtilityRequest class and assigning it to
		 * updateResult
		 */
		Long updateResult = eventUtilityService.invalidatePendingStageClosure(eventReq.getIdEvent());
		// Sending the updateResult value to the EventUtilityResponse class
		eventUtilityRes.setUpdateResult(updateResult);

		log.info("TransactionId :" + eventReq.getTransactionId());
		// Returning the EventUtilityResponse object
		return eventUtilityRes;

	}

	/**
	 * Fetches the event info for the IdEvent.
	 * 
	 * @param fetchEventReq
	 * @return FetchEventRes
	 */
	@RequestMapping(value = "/fetchEventInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FetchEventRes fetchEventInfo(@RequestBody FetchEventReq fetchEventReq) {
		FetchEventRes fetchEventRes = new FetchEventRes();
		log.info("TransactionId :" + fetchEventReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(fetchEventReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("contacts.idEvent.mandatory", null, Locale.US));
		}
		fetchEventRes.setEventValueDto(eventUtilityService.fetchEventInfo(fetchEventReq.getIdEvent()));
		return fetchEventRes;

	}

}
