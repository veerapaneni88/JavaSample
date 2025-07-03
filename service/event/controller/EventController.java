package us.tx.state.dfps.service.event.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.request.RtrvPersonIdReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.workload.dto.EventDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 5, 2017 - 1:17:59 PM
 */

@Api(tags = { "identity" ,"childplan"})
@RestController
@RequestMapping(value = "/event")
public class EventController {

	@Autowired
	EventService eventService;

	@Autowired
	CommonService commonService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(EventController.class);

	/**
	 * This common function is called to update the event table and the Event
	 * Person link table. Rows can be added, updated or deleted from the event
	 * table, while the Event Person Link table, you can only add and delete.
	 * Method Description: legacy service name - CCMN01U
	 * 
	 * @param postEventReq
	 * @return
	 * @
	 */
	@RequestMapping(value = "/postevent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PostEventRes postEvent(@RequestBody PostEventReq postEventReq) {

		if (!ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(postEventReq.getReqFuncCd())) {
			if (TypeConvUtil.isNullOrEmpty(postEventReq.getUlIdEvent())) {
				throw new InvalidRequestException(
						messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
			}
		}
		if (TypeConvUtil.isNullOrEmpty(postEventReq.getUlIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(postEventReq.getUlIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + postEventReq.getTransactionId());
		return eventService.postEvent(postEventReq);

	}

	/**
	 * Method Description: Returns details for the requested event id. Looks at
	 * the case to determine if the given user has access to a given stage.<br>
	 * Use this version of the method if you want to test access for the current
	 * user.<br>
	 * The following items are checked: <br>
	 * <li>primary worker assigned to stage</li>
	 * <li>one of the four secondary workers assigned to the stage</li>
	 * <li>the supervisor of any of the above</li>
	 * <li>the designee of any of the above supervisors</li> Case Utility
	 * Service @param commonHelperReq Common Request Object for Util
	 * Services(idStage, stageType)
	 *
	 * @return EventDto Event Details.
	 */
	@ApiOperation(value = "Get event details", tags = { "placements","childplan" })
	@RequestMapping(value = "/getEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  EventDto getEvent(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return eventService.getEvent(commonHelperReq.getIdEvent());

	}
	
	/**
	 * Method Description: Returns the most recent event information for given case id  and event description for Foster Care Candidacy
	 * 
	 * @param commonHelperReq - Common Request Object for Util Services
	 *
	 * @return EventDto - EventDto Objects 
	 *
	 **/
	@RequestMapping(value = "/getLatestEventForFCCByCaseIdAndEventDesc", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  EventDto getLatestEventForFCCByCaseIdAndEventDesc(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}else if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getEventDesc())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventDesc.mandatory", null, Locale.US));
		}
		return eventService.getLatestEventForFCCByCaseIdAndEventDesc(commonHelperReq.getIdCase(),commonHelperReq.getEventDesc());

	} 

	/**
	 * 
	 * Method Description: Returns the most recent event id and event status for
	 * the given stage and task code<br>
	 * Usage Example: see getEvent( int, String )
	 * 
	 * @param commonHelperReq
	 *            Common Request Object for Util Services
	 *
	 * @return ListObjectRes - List of EventDto Objects 
	 */
	@RequestMapping(value = "/getEventByStageIDAndTaskCode", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventDto getEventByStageIDAndTaskCode(@RequestBody CommonHelperReq commonHelperReq) {

		ListObjectRes listObjectRes = new ListObjectRes();
		EventDto eventDto = new EventDto();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getSzCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}

		try {
			listObjectRes = eventService.getEventByStageIDAndTaskCode(commonHelperReq);
			if (null != listObjectRes.getObjectList() && listObjectRes.getObjectList().size() > 0) {
				eventDto = (EventDto) listObjectRes.getObjectList().get(0);

			} else {
				eventDto = null;
			}
		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("common.data.emptyset", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		} catch (ServiceLayerException ex) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(ex.toString());
			serviceLayerException.initCause(ex);
			throw serviceLayerException;
		}
		return eventDto;
	}

	/**
	 * 
	 * Method Description: This method returns an boolean value based on whether
	 * or not a stage is in PEND status.Case Utility Service
	 * 
	 * @param commonHelperReq
	 *            Common Request Object for Util Services
	 *
	 * @return Boolean It tells if the stage is in PEND status or not.
	 */
	@RequestMapping(value = "/isStageInPendStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes isStageInPendStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getStageType())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageType.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getStageProgType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageProgramType.mandatory", null, Locale.US));
		}

		return eventService.isStageInPendStatus(commonHelperReq.getIdStage(), commonHelperReq.getStageType(),
				commonHelperReq.getStageProgType());
	}

	/**
	 * 
	 * Method Description: Looks at the case to determine if the given user has
	 * access to a given stage.<br>
	 * Use this version of the method if you want to test access for the current
	 * user.<br>
	 * The following items are checked: <br>
	 * <li>primary worker assigned to stage</li>
	 * <li>one of the four secondary workers assigned to the stage</li>
	 * <li>the supervisor of any of the above</li>
	 * <li>the designee of any of the above supervisors</li> <code>
	 * Usage Example:<br>
	 * int ulIdStage = GlobalData.getUlIdStage( request );<br>
	 * boolean bStageAccess = CaseUtility.hasStageAccess( ulIdStage, UserProfileHelper.getUserProfile( request ) );<br>
	 * </code>
	 * 
	 * @param commonHelperReq
	 *            Common Request Object for Util Services
	 *
	 * @return Boolean whether or not the user has access.
	 */
	@ApiOperation(value = "Check user access to stage", tags = { "identity" })
	@RequestMapping(value = "/hasStageAccess", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes hasStageAccess(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		return eventService.hasStageAccess(commonHelperReq.getIdStage(), commonHelperReq.getIdPerson());
	}

	/**
	 * 
	 * Method Description: Returns a list of open stages/closed and all stages
	 * for a given case; they are sorted by stage code, then stage name. Case
	 * Utility Service
	 * 
	 * @param idCase,caseStatus
	 * 			@return List<SelectStageDto>
	 */

	@RequestMapping(value = "/getOpenStages", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ListSelectStageRes getOpenStages(@RequestBody CommonHelperReq commonHelperReq) {

		ListSelectStageRes listSelectStageRes = new ListSelectStageRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCaseStatus())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseStaus.mandatory", null, Locale.US));
		}

		listSelectStageRes.setSelectStageDto(
				eventService.getOpenStages(commonHelperReq.getIdCase(), commonHelperReq.getCaseStatus()));
		return listSelectStageRes;
	}

	/*
	 * Method-Description:Returns the status of the given event.
	 * 
	 * @param IdEvent The ID_EVENT for the event of interest
	 * 
	 * @return The status of the event
	 * 
	 */
	@RequestMapping(value = "/getSzCdEventStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getSzCdEventStatus(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			return new CommonHelperRes();
		}

		return eventService.getSzCdEventStatus(commonHelperReq);
	}

	/**
	 * Method-Description:Returns true if the stage has a existing extension
	 * request.
	 * 
	 * @param stageID
	 * @return Boolean true/False depending on id Stage has existing extension
	 *         request.
	 * 
	 **/
	@RequestMapping(value = "/hasExtnReq", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes hasExtnReq(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return eventService.hasExtnReq(commonHelperReq);
	}

	/**
	 * Method-Description:Returns true if the stage has child fatality present
	 * 
	 * @param stageID
	 * 
	 **/
	@RequestMapping(value = "/isChildFatalityPresent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes isChildFatalityPresent(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return eventService.isChildFatalityPresent(commonHelperReq);
	}

	/**
	 * Method-Description:Check to see if the given event has an SSCC placement.
	 * 
	 * @param eventID
	 * 
	 **/
	@RequestMapping(value = "/getIdResourceSSCC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getIdResourceSSCC(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return eventService.getIdResourceSSCC(commonHelperReq);
	}

	/**
	 * Method-Description:Returns the Living Arrangement from an event id
	 * 
	 * @param eventID
	 * @return String (Living arrangement)
	 * 
	 **/
	@RequestMapping(value = "/getLivingArrangement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getLivingArrangement(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return eventService.getLivingArrangement(commonHelperReq);
	}

	/**
	 * Method-Description:Returns the active child referral for idStage
	 * 
	 * @param stageID
	 * @return Long -- the child active referral
	 * 
	 **/
	@RequestMapping(value = "/getChildActiveReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getChildActiveReferral(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return eventService.getChildActiveReferral(commonHelperReq);
	}

	/**
	 * Method-Description:Check and retrieve if there is an intake for the given
	 * case in the contact
	 * 
	 * @param caseID
	 * @return List of all StageIds and PriorStageIds.
	 * 
	 **/
	@RequestMapping(value = "/queryIntakeStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes queryIntakeStage(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return eventService.queryIntakeStage(commonHelperReq);
	}

	/**
	 * Method-Description:Check if there is a contact for the given case id and
	 * stage id and Contact Type
	 * 
	 * @param caseID,
	 *            StageID, Contact Type
	 * @return Boolean True or false
	 * 
	 **/
	@RequestMapping(value = "/isContactInStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes isContactInStage(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdContactType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.contactType.mandatory", null, Locale.US));
		}
		return eventService.isContactInStage(commonHelperReq);
	}

	/**
	 * Method-Description:This method returns an boolean value based on whether
	 * or not any of the ADO stage is currently in PROC status.
	 * 
	 * @param stageId
	 *            --stageID
	 * @return Boolean -- true or false
	 * 
	 **/
	@RequestMapping(value = "/getAdoProcessStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getAdoProcessStatus(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return eventService.getAdoProcessStatus(commonHelperReq);
	}

	/**
	 * Method-Description: Returns true if the stage has a pending extension
	 * request
	 * 
	 * @param stageId
	 *            --stageID , idCurrentContactEvent (event id)
	 * @return Boolean -- true or false
	 * 
	 **/
	@RequestMapping(value = "/hasPendingExtnReq", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes hasPendingExtnReq(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return eventService.hasPendingExtnReq(commonHelperReq);
	}

	/**
	 * Method Description: This method will retrieve id person for given event
	 * id.
	 * 
	 * @param rtrvPersonIdReq
	 * @return Long @
	 */
	@RequestMapping(value = "/rtrvPersonId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  Long rtrvIdPerson(@RequestBody RtrvPersonIdReq rtrvPersonIdReq) {

		if (TypeConvUtil.isNullOrEmpty(rtrvPersonIdReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		log.info("TransactionId :" + rtrvPersonIdReq.getTransactionId());
		return commonService.getPersonId(rtrvPersonIdReq);
	}

	/**
	 * Method Description:Update the event status for a list of
	 * CaseUtility.Event objects. Service Name: CaseUtility - Update Event
	 * Status
	 * 
	 * @param eventIdList
	 * @return updateEventsRes @
	 */

	// Update Event Status
	@RequestMapping(value = "/updateEventStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes updateEventStatus(@RequestBody CommonEventIdReq eventIdList) {

		if ((TypeConvUtil.isNullOrEmpty(eventIdList.getIdEvents()) || 0 == eventIdList.getIdEvents().size())
				&& (TypeConvUtil.isNullOrEmpty(eventIdList.getCdEventStatus()))) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CommonHelperRes updateEventsRes = eventService.updateEventStatus(eventIdList);

		log.info("TransactionId :" + eventIdList.getTransactionId());
		return updateEventsRes;
	}

	@RequestMapping(value = "/updateEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes updateEvent(@RequestBody CommonEventIdReq request) {

		if ((TypeConvUtil.isNullOrEmpty(request.getIdEvent()) || 0 == request.getIdEvent())
				&& (TypeConvUtil.isNullOrEmpty(request.getTaskCode()))) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CommonHelperRes updateEventsRes = new CommonHelperRes();

		updateEventsRes = eventService.updateEvent(request.getIdEvent(), request.getTaskCode());

		log.info("TransactionId :" + request.getTransactionId());
		return updateEventsRes;
	}

	/**
	 * 
	 * Method Description: Returns the cps event id and event status for the
	 * given stage and task code<br>
	 * Usage Example: see getEvent( int, String )
	 * 
	 * @param commonHelperReq
	 *            Common Request Object for Util Services
	 *
	 * @return ListObjectRes - List of EventDto Objects 
	 */
	@RequestMapping(value = "/getCpsEventByStageIdAndTaskCode", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventDto getCpsEventByStageIdAndTaskCode(@RequestBody CommonHelperReq commonHelperReq) {

		ListObjectRes listObjectRes = new ListObjectRes();
		EventDto eventDto = new EventDto();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getSzCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}

		try {
			listObjectRes = eventService.getCpsEventByStageIdAndTaskCode(commonHelperReq);
			if (null != listObjectRes.getObjectList() && listObjectRes.getObjectList().size() > 0) {
				eventDto = (EventDto) listObjectRes.getObjectList().get(0);

			} else {
				eventDto = null;
			}
		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("common.data.emptyset", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		} catch (ServiceLayerException ex) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(ex.toString());
			serviceLayerException.initCause(ex);
			throw serviceLayerException;
		}
		return eventDto;
	}

	@RequestMapping(value = "/getPendingStageClosureEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getPendingStageClosureEvent(@RequestBody CommonHelperReq commonHelperReq) {

		CommonHelperRes response = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		Long idEvent = eventService.getPendingStageClosureEvent(commonHelperReq.getIdStage());
		response.setIdEvent(idEvent);

		return response;
	}

	/**
	 * Method Name: getChildPlanServicePlanList. Method Description: This method
	 * will fetch the all child service plan created for a child by passing
	 * idStage and cdTask values
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@ApiOperation(value = "Get Child Plan details", tags = { "childplan" })
	@RequestMapping(value = "/getChildPlanServicePlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getChildPlanServicePlanList(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes childPlanResp = new CommonHelperRes();
		List<EventDto> eventDtoList = eventService.getChildServicePlanList(commonHelperReq.getSzCdTask(),
				commonHelperReq.getIdStage());
		childPlanResp.setEventDtls(eventDtoList);
		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return childPlanResp;
	}

	/**
	 * Method Name: hasLegalStatus Method Description: Checks whether the legal
	 * status is present in the DB or not.
	 * 
	 * @param commonHelperReq
	 * @return Boolean
	 */
	@RequestMapping(value = "/hasLegalStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  Boolean hasLegalStatus(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return eventService.hasLegalStatus(commonHelperReq.getIdStage());

	}
	
	@RequestMapping(value = "/getCdTasksByEventid", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getCdTasksByEventid(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		 commonHelperRes.setResult(eventService.getCdTasksByEventid(commonHelperReq.getIdEvent()));
		 return  commonHelperRes;
	}

	@GetMapping(value = "/getLetterDetails/{idEvent}")
	public InvLetterResponse getLetterDetailsFromEvent(@PathVariable(value = "idEvent") Long idEvent) {
		log.debug("Entering method getLetterDetailFromEvent in EventController");
		InvLetterResponse invLetterResponse = new InvLetterResponse();
		invLetterResponse.setCciInvLetterDto(eventService.getLetterDetails(idEvent));
		return invLetterResponse;
	}

	@RequestMapping(value = "/isRoraEventComplete", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Boolean isRoraEventComplete(@RequestBody EventDto eventDto) {
		if (TypeConvUtil.isNullOrEmpty(eventDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		String eventStatus = eventService.getRoraEventStatus(eventDto.getIdStage());
		return !ObjectUtils.isEmpty(eventStatus) && eventStatus.equals(ServiceConstants.CEVTSTAT_COMP);
	}

	@RequestMapping(value = "/isSafetyAssessmentCreatedContact", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Boolean isSafetyAssessmentCreatedContact(@RequestBody EventDto eventDto) {
		if (TypeConvUtil.isNullOrEmpty(eventDto.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return eventService.isSafetyAssessmentCreatedContact(eventDto.getIdEvent());
	}

	/**
	 *
	 * Method Description: Returns the most recent event id and event status for
	 * the given stage and task code<br>
	 * Usage Example: see getEvent( int, String )
	 *
	 * @param commonHelperReq
	 *            Common Request Object for Util Services
	 *
	 * @return ListObjectRes - List of EventDto Objects
	 */
	@RequestMapping(value = "/getEventsByStageIDAndTaskCodes", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getEventsByStageIDAndTaskCodes(@RequestBody CommonHelperReq commonHelperReq) {

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		ListObjectRes listObjectRes = new ListObjectRes();
		List<EventDto> eventDtoList = new ArrayList<>();
		EventDto eventDto = new EventDto();

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		try {
		eventDtoList = eventService.getEventsByStageIDAndTaskCodes(commonHelperReq);

		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("common.data.emptyset", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		} catch (ServiceLayerException ex) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(ex.toString());
			serviceLayerException.initCause(ex);
			throw serviceLayerException;
		}
		commonHelperRes.setEventDtls(eventDtoList);
		commonHelperRes.setIdEvents(eventDtoList.stream().map(e -> e.getIdEvent()).collect(Collectors.toList()));
		return commonHelperRes;
	}

}
