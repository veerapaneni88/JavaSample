package us.tx.state.dfps.service.event.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.cciinvReport.dto.CciInvLetterDto;
import us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.TaskDao;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EventUtilRes;
import us.tx.state.dfps.service.common.response.ListObjectRes;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.StagePersonDao;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.TaskDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 5, 2017 - 1:22:03 PM
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

	@Autowired
	StageDao stageDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	TaskDao taskDao;

	@Autowired
	ChildPlanBeanDao childPlanBeanDao;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	StagePersonDao stagePersonDao;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	TodoDao todoDao;

	private static final Logger log = Logger.getLogger(EventServiceImpl.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.tx.us.dfps.impact.event.service.EventService#postEvent(org.tx.us.dfps
	 * .impact.request.PostEventReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PostEventRes postEvent(PostEventReq postEventReq) {
		Event event = new Event();
		EventPersonLink eventPersonLink = new EventPersonLink();
		if (!ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(postEventReq.getReqFuncCd())) {
//			event.setIdEvent(postEventReq.getUlIdEvent());
			event = eventDao.getEventById(postEventReq.getUlIdEvent());
		}
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getUlIdStage())) {
			Stage stage = new Stage();
			stage.setIdStage(postEventReq.getUlIdStage());
			event.setStage(stage);
		}
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getUlIdCase()))
			event.setIdCase(postEventReq.getUlIdCase());
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getSzTxtEventDescr()))
			event.setTxtEventDescr(postEventReq.getSzTxtEventDescr());
		else
			event.setTxtEventDescr(postEventReq.getSzTxtEventDescr());
		//Defect #13883- Entered By column should display as the user for MED event type
		if(!TypeConvUtil.isNullOrEmpty(postEventReq.getUserId()) && ServiceConstants.CEVNTTYP_MED.equals(postEventReq.getSzCdEventType())) {
			Person person = new Person();
			person.setIdPerson(Long.valueOf(postEventReq.getUserId()));
			event.setPerson(person);
		} else if (!TypeConvUtil.isNullOrEmpty(postEventReq.getUlIdPerson())) {
			Person person = new Person();
			person.setIdPerson(postEventReq.getUlIdPerson());
			event.setPerson(person);
		}
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getPostEventPersonList())
				&& postEventReq.getPostEventPersonList().size() > 0 ) {
			Person eventPerson = new Person();
			if(ServiceConstants.SUB_CARE.equals(postEventReq.getCdStage()) && postEventReq.getPostEventPersonList().get(0).getIdPerson() == 0){
				Long idPerson = stagePersonDao.getPrimaryClientIdForStage(postEventReq.getUlIdStage());
				eventPerson.setIdPerson(idPerson);
			}else{
			eventPerson.setIdPerson(postEventReq.getPostEventPersonList().get(0).getIdPerson());
			}
			eventPersonLink.setPerson(eventPerson);
		}
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getSzCdTask()))
			event.setCdTask(postEventReq.getSzCdTask());
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getSzCdEventType()))
			event.setCdEventType(postEventReq.getSzCdEventType());
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getSzTxtEventDescr()))
			event.setTxtEventDescr(postEventReq.getSzTxtEventDescr());
		if (!TypeConvUtil.isNullOrEmpty(postEventReq.getSzCdEventStatus()))
			event.setCdEventStatus(postEventReq.getSzCdEventStatus());
		if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(postEventReq.getReqFuncCd())) {
			event.setDtEventCreated(new Date());
			event.setDtLastUpdate(new Date());
			event.setDtEventModified(new Date());
			event.setDtEventOccurred(new Date());
		} else {
			event.setDtLastUpdate(postEventReq.getTsLastUpdate());
			if (!TypeConvUtil.isNullOrEmpty(postEventReq.getTsLastUpdate()))
				event.setDtEventModified(postEventReq.getTsLastUpdate());
			if (!TypeConvUtil.isNullOrEmpty(postEventReq.getDtDtEventOccurred()))
				event.setDtEventOccurred(postEventReq.getDtDtEventOccurred());
			// Setting created date for merging event detail
//			event.setDtEventCreated(postEventReq.getDtDtEventOccurred());
		}
		Long idEvent = eventDao.updateEvent(event, postEventReq.getReqFuncCd());
		if (!ObjectUtils.isEmpty(postEventReq.getSzCdTask())
				&& !ServiceConstants.CONTACT_DETAIL_TASK_CODES.contains(postEventReq.getSzCdTask())
				&& !ObjectUtils.isEmpty(eventPersonLink.getPerson())) {
			eventPersonLink.setEvent(event);
			eventPersonLink.setDtLastUpdate(new Date());
			// artf128774 : For PPT event, update EventPersonLink table based on the action saved in PostEventPersonDto
			if (CodesConstant.CEVNTTYP_PPT.equalsIgnoreCase(postEventReq.getSzCdEventType())
					&& !CollectionUtils.isEmpty(postEventReq.getPostEventPersonList())) {
				eventDao.updateEventPersonLink(eventPersonLink, postEventReq.getPostEventPersonList().get(0).getCdScrDataAction());
			}else {
				eventDao.updateEventPersonLink(eventPersonLink, postEventReq.getReqFuncCd());
			}
		}
		PostEventRes postEventRes = new PostEventRes();
		postEventRes.setUlIdEvent(event.getIdEvent());
		postEventRes.setTsLastUpdate(postEventReq.getTsLastUpdate());
		postEventRes.setUlIdEvent(idEvent);
		postEventRes.setTransactionId(postEventReq.getTransactionId());
		log.info("TransactionId :" + postEventReq.getTransactionId());
		return postEventRes;
	}

	/**
	 * Method Description:This method will return the details of the event on
	 * the basis of an eventid.
	 * 
	 * @param event
	 *            id
	 * @return EventDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public EventDto getEvent(Long idEvent) {
		return eventDao.getEventByid(idEvent);
	}

	/**
	 * Method Description:This method will return an event and its details based
	 * on stage id and event type
	 * 
	 * @param Stage
	 *            id and Event type
	 * @return EventUtilRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public EventUtilRes getEventByStageAndEventType(Long ulIdStage, String szCdEventType) {
		return null;
	}

	/**
	 * Method Description:This method will return all the tasks for an Event
	 * which is in PENDING status
	 * 
	 * @param Stage
	 *            id
	 * @return EventDto list @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<EventDto> getPendingEventTasks(Long idStage) {
		return eventDao.getPendingEventTasks(idStage);
	}

	/**
	 * Method Description:This method will return the status of the Stage.
	 * 
	 * @param Stage
	 *            id, Stage type and program type
	 * @return Boolean -- It tells if the stage is in PEND status or not. @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonHelperRes isStageInPendStatus(Long idStage, String stageType, String stageProgType) {
		boolean isPendStatus = false;
		List<EventDto> eventDtoList = eventDao.getEventBystagenTask(idStage,
				stageDao.getTaskCode(stageType, stageProgType));
		for (EventDto eventDto : eventDtoList) {
			isPendStatus = (null != eventDto.getCdEventStatus()
					? ServiceConstants.EVENTSTATUS_PENDING.equals(eventDto.getCdEventStatus()) : false);
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIsStageInPendStatus(isPendStatus);
		return commonHelperRes;
	}

	/**
	 * Method Description:This method will return if the user has the Stage
	 * Access or not.
	 * 
	 * @param Stage
	 *            id, personid
	 * @return Boolean -- It tells if the personid has stage access or not. @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonHelperRes hasStageAccess(Long idStage, Long idPerson) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setHasStageAccess(stageDao.hasStageAccess(idStage, idPerson));
		return commonHelperRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonHelperRes hasApproverTask(Long idCase, Long idPerson) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(stageDao.hasApproverTask(idCase, idPerson));
		return commonHelperRes;
	}

	/**
	 * Method Description:This method will return the open Stages
	 * 
	 * @param idCase,
	 *            caseStatus
	 * @return SelectStageDto List @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<SelectStageDto> getOpenStages(Long idCase, String caseStatus) {
		return stageDao.getOpenStages(idCase, caseStatus);
	}

	/**
	 * Method Description: Returns the most recent event id and event status for
	 * the given stage and task code<br>
	 * Usage Example: see getEvent( int, String )
	 * 
	 * @param idCase,
	 *            caseStatus
	 * @return SelectStageDto List @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ListObjectRes getEventByStageIDAndTaskCode(CommonHelperReq commonHelperReq) {
		ListObjectRes listObjectRes = new ListObjectRes();
		List<Object> returnObjectList;
		returnObjectList = new ArrayList<Object>(
				eventDao.getEventByStageIDAndTaskCode(commonHelperReq.getIdStage(), commonHelperReq.getSzCdTask()));
		listObjectRes.setObjectList(returnObjectList);
		return listObjectRes;
	}

	/*
	 * Method-Description:Returns the status of the given event.
	 * 
	 * @param IdEvent The ID_EVENT for the event of interest
	 * 
	 * @return The status of the event
	 * 
	 */
	@Override
	@Transactional
	public CommonHelperRes getSzCdEventStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		EventDto eventDto = eventDao.getEventByid(commonHelperReq.getIdEvent());
		if (null != eventDto) {
			commonHelperRes.setCdEventStatus(eventDto.getCdEventStatus());
		}
		return commonHelperRes;
	}

	/*
	 * Method-Description:Returns true if the stage has a existing extension
	 * request.
	 * 
	 * @param stageID
	 * 
	 * @return Boolean true/False depending on id Stage has existing extension
	 * request.
	 * 
	 * @throws Service Exception, InvalidRequestException.
	 * 
	 */
	@Override
	@Transactional
	public CommonHelperRes hasExtnReq(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setHasExtnReq(eventDao.hasExtnReq(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	/*
	 * Method-Description:Returns true if the stage has child fatality present.
	 * 
	 * @param stageID
	 * 
	 * @return Boolean true/False depending on id Stage has child fatality
	 * present.
	 * 
	 * @throws Service Exception, InvalidRequestException.
	 * 
	 */
	@Override
	@Transactional
	public CommonHelperRes isChildFatalityPresent(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIsChildFatalityPresent(eventDao.isChildFatalityPresent(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	/**
	 * Method-Description:Check to see if the given event has an SSCC placement.
	 * 
	 * @param eventID
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	@Override
	public CommonHelperRes getIdResourceSSCC(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIdResourceSSCC(eventDao.getIdResourceSSCC(commonHelperReq.getIdEvent()));
		return commonHelperRes;
	}

	/**
	 * Method-Description:Returns the Living Arrangement from an event id
	 * 
	 * @param eventID
	 * @return String (Living arrangement)
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	@Override
	public CommonHelperRes getLivingArrangement(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setLivingArrangement(eventDao.getLivingArrangement(commonHelperReq.getIdEvent()));
		return commonHelperRes;
	}

	/**
	 * Method-Description:eturns the active child referral for idStage
	 * 
	 * @param stageID
	 * @return Long -- the child active referral
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	@Override
	public CommonHelperRes getChildActiveReferral(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setChildActiveReferral(eventDao.getChildActiveReferral(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	/**
	 * Method-Description:Check and retrieve if there is an intake for the given
	 * case in the contact
	 * 
	 * @param caseID
	 * @return List of all StageIds and PriorStageIds.
	 * @throws Service
	 *             Exception
	 * 
	 **/
	@Override
	@Transactional
	public CommonHelperRes queryIntakeStage(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes = eventDao.queryIntakeStage(commonHelperReq.getIdCase());
		return commonHelperRes;
	}

	/**
	 * Method-Description:Check if there is a contact for the given case id and
	 * stage id and Contact Type
	 * 
	 * @param caseID,
	 *            StageID, Contact Type
	 * @return Boolean True or false
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	@Override
	@Transactional
	public CommonHelperRes isContactInStage(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIsContactInStage(eventDao.isContactInStage(commonHelperReq.getIdCase(),
				commonHelperReq.getIdStage(), commonHelperReq.getIdContactType()));
		return commonHelperRes;
	}

	/**
	 * Method-Description:This method returns an boolean value based on whether
	 * or not any of the ADO stage is currently in PROC status.
	 * 
	 * @param stageId
	 *            --stageID
	 * @return Boolean -- true or false
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	@Override
	@Transactional
	public CommonHelperRes getAdoProcessStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setAdoProcessStatus(eventDao.getAdoProcessStatus(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	/**
	 * Method-Description: Returns true if the stage has a pending extension
	 * request
	 * 
	 * @param stageId
	 *            --stageID , idCurrentContactEvent (event id)
	 * @return Boolean -- true or false
	 * @throws Service
	 *             Exception.
	 * 
	 **/
	@Override
	@Transactional
	public CommonHelperRes hasPendingExtnReq(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setHasPendingExtnReq(
				eventDao.hasPendingExtnReq(commonHelperReq.getIdStage(), commonHelperReq.getIdEvent()));
		return commonHelperRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes updateEventStatus(CommonEventIdReq eventIdList) {
		CommonHelperRes updateEventRes = eventDao.updateEventStatus(eventIdList);
		return updateEventRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.event.service.EventService#updateEvent(java.lang
	 * .Long)
	 */
	@Override
	public CommonHelperRes updateEvent(Long idEvent, String taskCode) {
		CommonHelperRes updateEventRes = new CommonHelperRes();
		String desc = taskDao.getTaskDetails(taskCode).getTaskDecode();
		updateEventRes = eventDao.updateEvent(idEvent, taskCode, desc);
		return updateEventRes;
	}

	/**
	 * Method Description: Returns the most recent CPS event id and event status
	 * for the given stage and task code<br>
	 * Usage Example: see getEvent( int, String )
	 * 
	 * @param idCase,
	 *            caseStatus
	 * @return SelectStageDto List @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ListObjectRes getCpsEventByStageIdAndTaskCode(CommonHelperReq commonHelperReq) {
		ListObjectRes listObjectRes = new ListObjectRes();
		List<Object> returnObjectList;
		TaskDto taskDto = taskDao.getTaskDetails(commonHelperReq.getSzCdTask());
		if (taskDto != null) {
			returnObjectList = new ArrayList<Object>(eventDao
					.getCpsEventByStageIdAndTaskCode(commonHelperReq.getIdStage(), commonHelperReq.getSzCdTask()));
			listObjectRes.setObjectList(returnObjectList);
		}
		return listObjectRes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.event.service.EventService#
	 * getPendingStageClosureEvent(java.lang.Long)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getPendingStageClosureEvent(Long idStage) {
		return eventDao.getPendingStageClosureEvent(idStage);
	}

	@Override
	public void updateEventStatus(Long idEvent, String eventStatus) {
		updateEventStatus(idEvent, eventStatus, null);
	}

	@Override
	public void updateEventStatus(Long idEvent, String eventStatus, String eventDescription) {

		EventDto eventDto = eventDao.getEventByid(idEvent);
		if (ObjectUtils.isEmpty(eventDto) && ObjectUtils.isEmpty(eventDto.getCdEventStatus())) {
			throw new ServiceLayerException(messageSource.getMessage("Event.not.found.idEvent", null, Locale.US));
		}
		if (eventDto.getCdEventStatus().equals(eventStatus) && ObjectUtils.isEmpty(eventDescription)) {
			return;
		}
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		Date dtCurrent = new Date();
		postEventIPDto.setIdEvent(eventDto.getIdEvent());
		postEventIPDto.setCdEventStatus(eventStatus);
		postEventIPDto.setCdEventType(eventDto.getCdEventType());
		postEventIPDto.setIdStage(eventDto.getIdStage());
		postEventIPDto.setIdCase(eventDto.getIdCase());
		postEventIPDto.setIdPerson(eventDto.getIdPerson());
		postEventIPDto.setCdTask(eventDto.getCdTask());
		archInputDto.setReqFuncCd(ServiceConstants.UPDATE);
		postEventIPDto.setTsLastUpdate(dtCurrent);
		if (ObjectUtils.isEmpty(eventDescription))
			eventDescription = eventDto.getEventDescr();
		postEventIPDto.setEventDescr(eventDescription);
		postEventIPDto.setDtEventOccurred(eventDto.getDtEventOccurred());
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		log.info("End Method Execution updateEventStatus with updated event " + postEventOPDto.getIdEvent());
	}

	/**
	 * Method Name: getChildPlanServicePlanList. Method Description: This method
	 * will fetch the all child service plan created for a child by passing
	 * idStage and cdTask values
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@Override
	public List<EventDto> getChildServicePlanList(String cdTask, Long idStage) {
		List<EventDto> eventList=eventDao.getChildServicePlanList(cdTask, idStage);
		eventList.stream().forEach(eventDto-> {
			eventDto.setLegacyChildPlan(childPlanBeanDao.checkIfEventIsLegacy(eventDto.getIdEvent()));
		});
		return eventList;
	}

	/**
	 * Method Name: hasLegalStatus Method Description: Checks wheather the legal
	 * status is present in the DB or not.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean hasLegalStatus(Long idStage) {
		Boolean status = Boolean.FALSE;
		List<LegalStatusDto> legalStatusDto = eventDao.hasLegalStatus(idStage);
		if (!CollectionUtils.isEmpty(legalStatusDto)) {
			status = Boolean.TRUE;
		}
		return status;

	}
	
	/**
	 * Method Name: This Method will check if the FPR_SERVICE_AUTH_TASK_CODE is present in the list or not
	 * @param idEvent
	 * @return Boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Boolean getCdTasksByEventid(Long idEvent) {
		return eventDao.getCdTasksByEventid(idEvent);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CciInvLetterDto getLetterDetails(Long idEvent) {
		return eventDao.getLetterDetails(idEvent);
	}
	@Override
	public String getRoraEventStatus(Long stageId) {
		List<String> roraStatusList = eventDao.getRoraEventStatus(stageId);
		return ObjectUtils.isEmpty(roraStatusList) ? null : roraStatusList.get(0);
	}

	/**
	 * Method Name: This Method will check if the event is created from safety assessment or not
	 * @param idEvent
	 * @return Boolean
	 */
	@Override
	public Boolean isSafetyAssessmentCreatedContact(Long idEvent) {
		return eventDao.isSafetyAssessmentCreatedContact(idEvent);
	}

	/**
	 * Method Description: Returns event ids and event status for
	 * the given stage and task codes<br>
	 *
	 * @param idCase, idStage,
	 *            taskCodes
	 * @return Select EventDTO List @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<EventDto> getEventsByStageIDAndTaskCodes(CommonHelperReq commonHelperReq) {

		List<EventDto> eventList= eventDao.getEventsByStageIDAndTaskCodes(commonHelperReq.getIdCase(), commonHelperReq.getIdStage(), commonHelperReq.getTaskCodes());

		return eventList;
	}
	
	/**
	 * Method Description:This method will return the details of the event information given case id and event description for Foster Care Candidacy
	 * 
	 * @param caseId
	 * @param eventDesc
	 * @return EventDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public EventDto getLatestEventForFCCByCaseIdAndEventDesc(Long caseId, String eventDesc) {
		return eventDao.getLatestEventForFCCByCaseIdAndEventDesc(caseId,eventDesc);
	}

}
