package us.tx.state.dfps.service.childplan.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.dto.ChildPlanParticipantDto;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.EventProcessDao;
import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dao.UpdateToDoDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao;
import us.tx.state.dfps.service.childplan.dto.ChildEventStageDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEventDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanGuideTopicDisplayDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegacyDto;
import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;
import us.tx.state.dfps.service.childplan.dto.CurrentlySelectedPlanDto;
import us.tx.state.dfps.service.childplan.dto.SSCCChildPlanDto;
import us.tx.state.dfps.service.childplan.service.ChildPlanService;
import us.tx.state.dfps.service.childplanparticipant.dao.ChildPlanParticipantDao;
import us.tx.state.dfps.service.childplanparticipant.service.ChildPlanParticipantService;
import us.tx.state.dfps.service.childplanrtrv.dao.ChildPlanNarrativeDao;
import us.tx.state.dfps.service.childplanrtrv.dao.CurrentPlanDao;
import us.tx.state.dfps.service.childplanrtrv.dao.FetchConcurrentGoalDao;
import us.tx.state.dfps.service.childplanrtrv.dao.FetchPlanDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.common.response.ChildPlanParticipRes;
import us.tx.state.dfps.service.common.response.ChildPlanRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.person.service.CriminalHistoryService;
import us.tx.state.dfps.service.sscc.dao.SSCCListDao;
import us.tx.state.dfps.service.sscc.util.SSCCRefUtil;
import us.tx.state.dfps.service.ssccchildplan.dao.SSCCChildPlanDao;
import us.tx.state.dfps.service.ssccchildplan.service.SSCCChildPlanService;
import us.tx.state.dfps.service.ssccchildplan.utility.SSCCChildPlanUtility;
import us.tx.state.dfps.service.subcare.dto.ChildPlanGuideTopicDto;
import us.tx.state.dfps.service.subcare.dto.StaffSearchResultDto;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.web.todo.bean.ToDoDetailBean;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * DescriptionImplementation for ChildPlanService Nov 9, 2017- 10:44:41 AM ©
 * 2017 Texas Department of Family and Protective Services.
 */
@Service
@Transactional
public class ChildPlanServiceImpl implements ChildPlanService {

	@Autowired
	private ChildPlanBeanDao childPlanDao;

	@Autowired
	ChildPlanParticipantService childPlanParticipantService;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	FetchConcurrentGoalDao fetchConcurrentGoalDao;

	@Autowired
	FetchPlanDao fetchPlanDao;

	@Autowired
	CurrentPlanDao currentPlanDao;

	@Autowired
	EventProcessDao eventProcessDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	UpdateToDoDao updateToDoDao;

	@Autowired
	ChildPlanNarrativeDao childPlanNarrativeDao;

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	@Autowired
	StageDao stageDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	TodoDao todoDao;

	@Autowired
	EventUpdEventStatusDao updateEventStatusDao;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	private SSCCChildPlanDao ssccChildPlanDao;

	@Autowired
	SSCCListDao ssccListDao;

	@Autowired
	SSCCRefUtil ssccRefUtil;

	@Autowired
	SSCCChildPlanUtility ssccChildPlanUtility;

	@Autowired
	SSCCChildPlanService ssccChildPlanService;

	@Autowired
	CriminalHistoryService criminalHistoryService;

	@Autowired
	ChildPlanParticipantDao childPlanParticipantDao;

	private static final Logger Log = Logger.getLogger(ChildPlanServiceImpl.class);

	public static final String ADO_CHILD_PLAN_TASK_CODE = "8660";
	public static final String APPROVE_ADO_CHILD_PLAN_TASK_CODE = "8860";
	public static final String SUB_CHILD_PLAN_TASK_CODE = "3160";
	public static final String APPROVE_SUB_CHILD_PLAN_TASK_CODE = "3370";
	public static final int CRIM_HIST_PENDING = 55325;
	public static final int MSG_CRML_HIST_CHECK_STAGE_PEND = 56384;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.childplan.service.ChildPlanService#getChildPlan(
	 * us.tx.state.dfps.service.common.request.ChildPlanReq)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ChildPlanRes getChildPlan(ChildPlanReq childPlanReq) {

		Log.debug("Entering method getChildPlan in ChildPlanService");

		ChildPlanRes childPlanRes = new ChildPlanRes();

		ChildEventStageDto childEventStageDto = populateChildEventStageDto(childPlanReq);
		ChildPlanLegacyDto childPlanLegacyDto = new ChildPlanLegacyDto();

		childPlanLegacyDto = getChildPlanLegacyDto(childEventStageDto);

		childPlanParticipantService.fetchChildPlanParticipants(childEventStageDto.getIdEvent(), childPlanLegacyDto);
		childPlanReq.setChildPlanLegacyDto(childPlanLegacyDto);

		// Retrieve persons in the stage.
		List<PersonValueDto> personValueDtoList = childPlanDao.selectPersonsInStage(childPlanReq);

		childPlanLegacyDto.setPersonsInStage(personValueDtoList);

		Long primaryChildPersonId = null;

		if (!ObjectUtils.isEmpty(personValueDtoList)) {
			Optional<PersonValueDto> personValueDtoOptional = personValueDtoList.stream()
					.filter(person -> CodesConstant.CROLEALL_PC.equals(person.getRoleInStageCode())).findFirst();
			if (personValueDtoOptional.isPresent()) {
				primaryChildPersonId = personValueDtoOptional.get().getPersonId();
				childPlanLegacyDto.setPrimaryChildPersonId(primaryChildPersonId);
				childPlanLegacyDto.setDtLastUpdatePrimaryChild(personValueDtoOptional.get().getDtLastUpdate());
				childPlanLegacyDto.setDtPrimaryChildBirthDate(personValueDtoOptional.get().getDateOfBirth());
			}
		}
		// Create the guide topic display list.
		createGuideTopicDisplayList(childPlanReq, childPlanLegacyDto);

		childPlanDao.updateChildPlanPriorAdopInfo(primaryChildPersonId, childPlanLegacyDto);

		if (ObjectUtils.isEmpty(childPlanLegacyDto.getCdEverAdopted())) {
			List<LegalStatusDto> legalStatusDtoList = childPlanDao.getLegalStatusInformation(primaryChildPersonId);
			if (!ObjectUtils.isEmpty(legalStatusDtoList) && legalStatusDtoList.size() > 0) {
				childPlanLegacyDto.setCdEverAdopted(ServiceConstants.ONE);
				childPlanLegacyDto.setDtMostRecentAdoption(legalStatusDtoList.get(0).getDtLegalStatStatusDt());
			}
			childPlanLegacyDto.setLegalStatusDtoList(legalStatusDtoList);
		}
		
		List<EventDto> eventList = eventDao.getEventByStageIDAndTaskCode(childPlanReq.getIdStage(), ServiceConstants.CLOSE_SUB_STAGE);

		EventDto stageClosureEvent = null;
		
		if (!ObjectUtils.isEmpty(eventList)) {
			stageClosureEvent = eventList.stream()
					.filter(x -> ServiceConstants.EVENT_STATUS_PEND.equals(x.getCdEventStatus()))
					.findAny().orElse(null);
		}
		
		if (!ObjectUtils.isEmpty(stageClosureEvent)) {
			childPlanLegacyDto.setIndStageClosurePending(ServiceConstants.YES);
		}
		
		Log.debug("Exiting method getChildPlan in ChildPlanService");

		childPlanRes.setChildPlanLegacyDto(childPlanLegacyDto);

		return childPlanRes;

	}

	/**
	 * Gets the child plan legacy dto.
	 *
	 * @param childEventStageDto
	 *            the child event stage dto
	 * @return the child plan legacy dto
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildPlanLegacyDto getChildPlanLegacyDto(ChildEventStageDto childEventStageDto) {
		ChildPlanLegacyDto childPlanLegacyDto = new ChildPlanLegacyDto();

		Long idChildPlanEvent = childEventStageDto.getIdEvent();

		if (!ObjectUtils.isEmpty(idChildPlanEvent) && idChildPlanEvent > 0) {
			EventDto eventDto = eventDao.getEventByid(childEventStageDto.getIdEvent());
			childPlanLegacyDto.setEventDto(eventDto);
		}

		setChildPlanEventDto(idChildPlanEvent, childPlanLegacyDto);
		setConcurrentGoals(idChildPlanEvent, childPlanLegacyDto);
		setCurrentlySelectedPlans(childEventStageDto, childPlanLegacyDto);

		return childPlanLegacyDto;
	}

	/**
	 * Sets the currently selected plans.
	 *
	 * @param childEventStageDto
	 *            the child event stage dto
	 * @param childPlanLegacyDto
	 *            the child plan legacy dto
	 */
	private void setCurrentlySelectedPlans(ChildEventStageDto childEventStageDto,
			ChildPlanLegacyDto childPlanLegacyDto) {
		Long idChildPlanEvent = childEventStageDto.getIdEvent();
		String cdCspPlanType = childPlanLegacyDto.getChildPlanEventDto().getCdCspPlanType();

		currentPlanDao.getCurrentlySelectedPlans(idChildPlanEvent, cdCspPlanType, childPlanLegacyDto);

	}

	/**
	 * Sets the child plan event dto.
	 *
	 * @param idChildPlanEvent
	 *            the id child plan event
	 * @param childPlanLegacyDto
	 *            the child plan legacy dto
	 * @return the child plan event dto
	 */
	private ChildPlanEventDto setChildPlanEventDto(Long idChildPlanEvent, ChildPlanLegacyDto childPlanLegacyDto) {
		ChildPlanEventDto childPlanEventDto = new ChildPlanEventDto();

		childPlanEventDto = fetchPlanDao.getChildPlanEvent(idChildPlanEvent);

		childPlanLegacyDto.setChildPlanEventDto(childPlanEventDto);

		return childPlanEventDto;
	}

	/**
	 * Sets the concurrent goals.
	 *
	 * @param idChildPlanEvent
	 *            the id child plan event
	 * @param childPlanLegacyDto
	 *            the child plan legacy dto
	 */
	private void setConcurrentGoals(Long idChildPlanEvent, ChildPlanLegacyDto childPlanLegacyDto) {
		List<String> selectedConcurrentGoals = new ArrayList<String>();

		List<ConcurrentGoalDto> concurrentGoalDtoList = fetchConcurrentGoalDao.getConcurrentGoals(idChildPlanEvent);
		childPlanLegacyDto.setConcurrentGoalDtoList(concurrentGoalDtoList);

		if (!ObjectUtils.isEmpty(concurrentGoalDtoList)) {
			concurrentGoalDtoList.forEach(concurrentGoal -> {
				selectedConcurrentGoals.add(concurrentGoal.getCdConcurrentGoal());
			});
			childPlanLegacyDto.setSelectedConcurrentGoals(selectedConcurrentGoals);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.childplan.service.ChildPlanService#
	 * getPlanTypeCode(java.lang.Long, java.lang.Long)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public String getPlanTypeCode(Long caseId, Long eventId) {
		Log.debug("Entering method getPlanTypeCode in ChildPlanService");

		String planTypeCode = ServiceConstants.EMPTY_STR;
		planTypeCode = childPlanDao.selectPlanTypeCode(caseId, eventId);

		Log.debug("Exiting method getPlanTypeCode in ChildPlanService");
		return planTypeCode;

	}

	/**
	 * Method Name: checkIfEventIsLegacy Method Description: Queries a row from
	 * the EVENT_PLAN_LINK for the given event id to determine whether or not
	 * the event is a legacy event--one created before the initial launch of
	 * IMPACT.
	 *
	 * @param eventId
	 *            the event id
	 * @return Boolean @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean checkIfEventIsLegacy(Long eventId) {
		Log.debug("Entering method checkIfEventIsLegacy in ChildPlanService");

		Boolean isLegacyEvent = ServiceConstants.FALSEVAL;
		isLegacyEvent = childPlanDao.checkIfEventIsLegacy(eventId);

		Log.debug("Exiting method checkIfEventIsLegacy in ChildPlanService");
		return isLegacyEvent;
	}

	/**
	 * Populate the ChildEventStageDto input object for retrieving child plan
	 * info.
	 *
	 * @param childPlanReq
	 *            the child plan req
	 * @return ChildEventStageDto The service input object.
	 */
	private ChildEventStageDto populateChildEventStageDto(ChildPlanReq childPlanReq) {
		ChildEventStageDto childEventStageDto = new ChildEventStageDto();

		childEventStageDto.setIdStage(childPlanReq.getIdStage());
		childEventStageDto.setIdEvent(childPlanReq.getIdEvent());
		childEventStageDto
				.setCdWinMode(childPlanReq.getIdEvent() == 0 ? ServiceConstants.NEW : ServiceConstants.MODIFY);

		return childEventStageDto;
	}

	/**
	 * Gets the guide topic.
	 *
	 * @param topicCode
	 *            the topic code
	 * @param eventId
	 *            the event id
	 * @param childPlanReq
	 *            the child plan req
	 * @return the guide topic
	 */
	private ChildPlanGuideTopicDto getGuideTopic(String topicCode, Long eventId, ChildPlanReq childPlanReq) {
		// Get the table name for the topic, and query the topic data.
		String tableName = lookupDao.decode(ServiceConstants.CCPTPTBL, topicCode);
		ChildPlanGuideTopicDto guideTopicValueBeanDto = new ChildPlanGuideTopicDto();
		guideTopicValueBeanDto.setCode(topicCode);
		guideTopicValueBeanDto.setIdEvent(eventId);
		guideTopicValueBeanDto.setTableName(tableName);
		guideTopicValueBeanDto = childPlanDao.selectGuideTopic(guideTopicValueBeanDto, childPlanReq);
		return guideTopicValueBeanDto;
	}

	/**
	 * Creates the guide topic display list.
	 *
	 * @param childPlanReq
	 *            the child plan req
	 * @param childPlanLegacyDto
	 *            the child plan legacy dto
	 */
	private void createGuideTopicDisplayList(ChildPlanReq childPlanReq, ChildPlanLegacyDto childPlanLegacyDto) {
		String pageMode = childPlanReq.getPageMode();

		List<ChildPlanGuideTopicDisplayDto> displayBeanList = new ArrayList<ChildPlanGuideTopicDisplayDto>();
		Long eventId = childPlanReq.getIdEvent();

		List<CurrentlySelectedPlanDto> currentlySelectedPlanDtoList = childPlanLegacyDto
				.getCurrentlySelectedPlanDtoList();
		boolean flagForContinue = false;
		if (!ObjectUtils.isEmpty(currentlySelectedPlanDtoList)) {

			for (CurrentlySelectedPlanDto currentlySelectedPlanDto : currentlySelectedPlanDtoList) {

				if (flagForContinue) {
					flagForContinue = false;
					continue;
				}
				String topicCode = currentlySelectedPlanDto.getCdCpTopic();
				// Determine whether or not the guide topic has a corresponding
				// "plans to
				// address" guide topic pair. If it does, move the enmerator to
				// the next
				// element since the guide topic pair will be next in the array.
				Boolean hasPair = ServiceConstants.FALSEVAL;
				String topicPairCode = lookupDao.simpleDecodeSafe(ServiceConstants.CCPPAIR1, topicCode);

				if (!ObjectUtils.isEmpty(topicPairCode) && !ServiceConstants.CCPTOPCS_FAM.equals(topicCode)) {
					hasPair = ServiceConstants.TRUEVAL;
					flagForContinue = true;
				}
				// Put the guide topic and its pair, if it has one, into a Guide
				// Topic
				// Display Bean. (Query the guide topic narratives using the EJB
				// so that
				// the blob will be decompressed correctly.) Then put the
				// Display Bean
				// into an ArrayList.

				ChildPlanGuideTopicDisplayDto displayBean = new ChildPlanGuideTopicDisplayDto();
				if (hasPair) {
					ChildPlanGuideTopicDto bean1 = getGuideTopic(topicCode, eventId, childPlanReq);
					ChildPlanGuideTopicDto bean2 = getGuideTopic(topicPairCode, eventId, childPlanReq);

					String guideTopicGroupHeading = bean1.getTopic();
					// guide topic. 'Permanency Efforts' and 'Visitation' are
					// special.
					if (topicCode.equals(ServiceConstants.CCPTOPCS_PER)
							|| topicCode.equals(ServiceConstants.CCPTOPCS_PRA)) {
						guideTopicGroupHeading = ServiceConstants.PERMANENCY_EFFORTS_HEADING;
					} else if (topicCode.equals(ServiceConstants.CCPTOPCS_REI)) {
						guideTopicGroupHeading = ServiceConstants.REI_HEADING;
					} else if (topicCode.equals(ServiceConstants.CCPTOPCS_PAL)) {
						guideTopicGroupHeading = ServiceConstants.PAL_HEADING;
					} else if (topicCode.equals(ServiceConstants.CCPTOPCS_PDO)) {
						guideTopicGroupHeading = ServiceConstants.PDO_HEADING;
					} else if (topicCode.equals(ServiceConstants.CCPTOPCS_PSY)) {
						guideTopicGroupHeading = ServiceConstants.PSY_HEADING;
					} else if (topicCode.equals(ServiceConstants.CCPTOPCS_VIS)) {
						guideTopicGroupHeading = ServiceConstants.VIS_HEADING;
					} else if (topicCode.equals(ServiceConstants.CCPTOPCS_DIP)) {
						guideTopicGroupHeading = ServiceConstants.DIP_HEADING;
					}
					displayBean.setTitle(guideTopicGroupHeading);
					displayBean.setBean1(bean1);
					displayBean.setBean2(bean2);
				} else {
					ChildPlanGuideTopicDto bean1 = getGuideTopic(topicCode, eventId, childPlanReq);
					String guideTopicGroupHeading = bean1.getTopic();
					displayBean.setTitle(guideTopicGroupHeading);
					displayBean.setBean1(bean1);
					displayBean.setBean2(null);

				}
				boolean initBeforeFCR2Rollout = ServiceConstants.FALSEVAL;
				boolean planTypeExcludeICHISH = ServiceConstants.FALSEVAL;

				String cdPlanType = childPlanReq.getChildPlanLegacyDto().getChildPlanEventDto().getCdCspPlanType();
				if (cdPlanType.equals(ServiceConstants.CHILD_PLAN_ADP)
						|| cdPlanType.equals(ServiceConstants.CHILD_PLAN_FRP)
						|| cdPlanType.equals(ServiceConstants.CHILD_PLAN_FRV)
						|| cdPlanType.equals(ServiceConstants.CHILD_PLAN_RVL)
						|| cdPlanType.equals(ServiceConstants.CHILD_PLAN_RVP)
						|| cdPlanType.equals(ServiceConstants.CHILD_PLAN_RVT)
						|| cdPlanType.equals(ServiceConstants.CHILD_PLAN_RVW)) {
					planTypeExcludeICHISH = ServiceConstants.TRUE_VALUE;
				}
				Date FCR2RolloutDt = DateUtils.stringDate(ServiceConstants.CP_ROLL_OUT_DT);				
				ChildPlanLegacyDto childPlanLegacy = childPlanReq.getChildPlanLegacyDto();
				Date eventOccouredDate = null; 
				EventDto eventDto = null;
				if (!ObjectUtils.isEmpty(childPlanLegacy)) {
					eventDto = childPlanLegacy.getEventDto();
					if (!ObjectUtils.isEmpty(eventDto)){
						eventOccouredDate = eventDto.getDtEventOccurred();
					}									
				}
				if (ObjectUtils.isEmpty(eventOccouredDate)) {
					eventOccouredDate = new Date();
				} 
				if (DateUtils.isBefore(eventOccouredDate, FCR2RolloutDt)) {
					initBeforeFCR2Rollout = ServiceConstants.TRUE_VALUE;
				}

				boolean aprvBeforeNovRollout = ServiceConstants.FALSEVAL;
				Date novRolloutDt = DateUtils.stringDate(ServiceConstants.CP_NOV_ROLL_OUT_DT);

				if (!ObjectUtils.isEmpty(eventDto) && ServiceConstants.APPROVAL.equals(eventDto.getCdEventStatus())
						&& DateUtils.isBefore(eventDto.getDtEventOccurred(), novRolloutDt)) {
					aprvBeforeNovRollout = ServiceConstants.TRUE_VALUE;
				}
				if ((ServiceConstants.CCPTOPCS_ICH.equals(displayBean.getBean1().getCode())
						|| ServiceConstants.CCPTOPCS_ISH.equals(displayBean.getBean1().getCode()))
						&& initBeforeFCR2Rollout == ServiceConstants.TRUE_VALUE
						&& planTypeExcludeICHISH == ServiceConstants.TRUE_VALUE) {
					// don't add to list
				} else if ((ServiceConstants.CCPTOPCS_ICH.equals(displayBean.getBean1().getCode())
						|| ServiceConstants.CCPTOPCS_ISH.equals(displayBean.getBean1().getCode()))
						&& !ObjectUtils.isEmpty(pageMode) && pageMode.equals(ServiceConstants.NEW_USING)
						&& !ObjectUtils.isEmpty(displayBean.getBean1().getBlob())) {
					ChildPlanGuideTopicDto bean1 = displayBean.getBean1();
					List<Long> idEvents = getChildPlanEvents(childPlanReq);
					for (Long idEvent : idEvents) {
						if (ObjectUtils.isEmpty(bean1.getBlob())) {
							if (idEvent.equals(eventId)) {
								continue;
							}
							bean1 = getGuideTopic(topicCode, idEvent, childPlanReq);
						}
					}
					bean1.setIdEvent(eventId);
					Long msg = childPlanReq.getMessage();
					if (!TypeConvUtil.isNullOrEmpty(bean1.getBlob())) {
						bean1.setIdEvent(eventId);
						bean1.setDtLastUpdate(null);
						bean1 = childPlanDao.getTopicDtLastUpdate(bean1);
						childPlanDao.saveGuideTopic(bean1);
						bean1 = childPlanDao.getTopicDtLastUpdate(bean1);
						if (msg != ServiceConstants.MSG_HISTORY_NOT_FOUND)
							msg = ServiceConstants.MSG_HISTORY_FOUND_COPIED_FWD;
					} else {
						msg = ServiceConstants.MSG_HISTORY_NOT_FOUND;
					}
					childPlanReq.setMessage(msg);
					displayBean.setBean1(bean1);
					displayBeanList.add(displayBean);
				} else if (aprvBeforeNovRollout) {
					displayBeanList.add(displayBean);
				} else {
					if (!ServiceConstants.CCPTOPCS_FAM.equals(displayBean.getBean1().getCode())
							&& !ServiceConstants.CCPTOPCS_FAN.equals(displayBean.getBean1().getCode())
							&& !ServiceConstants.CCPTOPCS_CPL.equals(displayBean.getBean1().getCode())
							&& !ServiceConstants.CCPTOPCS_FMP.equals(displayBean.getBean1().getCode())) {
						displayBeanList.add(displayBean);
					}
				}
			}
		}
		childPlanLegacyDto.setGuideTopicDisplayList(displayBeanList);

	}

	/**
	 * Gets the child plan events.
	 *
	 * @param childPlanBean
	 *            the child plan bean
	 * @return the child plan events
	 */
	private List<Long> getChildPlanEvents(ChildPlanReq childPlanBean) {
		List<Long> idEvents = childPlanDao
				.selectChildPlansForPID(childPlanBean.getChildPlanLegacyDto().getChildPlanEventDto().getIdPerson());
		return idEvents;
	}

	/**
	 * Method Name: deleteChildPlan Method Description:This Method is used to
	 * delete the child plan based The event id of the child plan.
	 * 
	 * @param idEvent
	 * @param ChildPlanLegacyDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteChildPlan(ChildPlanReq childPlanReq) {
		Long idEvent = childPlanReq.getIdEvent();
		ChildPlanLegacyDto childPlanLegacyDto = childPlanReq.getChildPlanLegacyDto();

		if (!ObjectUtils.isEmpty(idEvent) && idEvent != 0l) {
			setCurrentlySelectedPlans(populateChildEventStageDto(childPlanReq), childPlanLegacyDto);

			childPlanDao.deleteTopicTable(idEvent, childPlanLegacyDto.getCurrentlySelectedPlanDtoList());
			// Delete from EVENT_PLAN_LINK table
			childPlanDao.deleteEventPlanLink(idEvent);
			// Delete from CP_CONCURRENT_GOAL table
			childPlanDao.deleteCpConcurrentGoal(idEvent);
			// Delete from CHILD_PLAN table
			childPlanDao.deleteChildPlan(idEvent);
			// Delete from TO DO table
			childPlanDao.deleteTodo(idEvent);
			// Delete from CHILD_PLAN_PARTICIP table
			childPlanDao.deleteChildPlanParticip(idEvent);
			// Delete from EVENT_PERSON_LINK table
			childPlanDao.deleteEventPersonlink(idEvent);
			// Delete from CHILD_PLAN_ITEM table
			childPlanDao.deleteChildPlanItem(idEvent);
			// Delete the event
			EventInputDto eventInputDto = new EventInputDto();
			eventInputDto.setIdEvent(idEvent);
			eventProcessDao.deleteEvent(eventInputDto);
		}
	}
	/**
	 * Method Name: deleteChildPlanByEvent Method Description:This Method is used to
	 * delete the child plan based The event id of the child plan.
	 *
	 * @param childPlanReq
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteChildPlanByEvent(ChildPlanReq childPlanReq) {
		Long idEvent = childPlanReq.getIdEvent();
		ChildPlanLegacyDto childPlanLegacyDto = childPlanReq.getChildPlanLegacyDto();
		//Find child plan type from a child plan record
		String planTypeCode =childPlanDao.selectPlanTypeCode(idEvent);
		childPlanLegacyDto.getChildPlanEventDto().setCdCspPlanType(planTypeCode);
		/*
				artf132558-PD 58718 : CPOS delete option only deletes CPOS
				When an event is deleted , records from the following tables will also be deleted.
				Below association is by the Event Id.
				All the table records returned from the CODES_TABLES by plan type code
				Following are the associated child plan tables. Association is by the EVENT ID.
				CP_SPRVSN_DTL,CP_TRANSTN_ADULT_BLW_DTL,	CP_SOCL_RECRTNAL_DTL,CP_CHILD_FMLY_TEAM_DTL,CP_BHVR_MGMT
				CP_YOUTH_PREGNT_PRNTG,CP_INTLCTL_DVLPMNTL,CP_TRTMNT_SRVC_DTL,CP_TRANSTN_ADULT_ABV_DTL,CP_SVCS_HGH_RISK_BHVR
				CP_INFORMATION,	CP_EDUCTN_DTL,	CP_EMTNL_THRPTC_DTL,CP_HLTH_CARE_SUMM,CP_PSYCH_MEDCTN_DTL,SSCC_CHILD_PLAN
				CP_VISIT_CNTCT_FMLY,CP_ADTNL_SCTN_DTLS,	CP_LST_GOALS,CHILD_PLAN,EVENT_PLAN_LINK,CP_CONCURRENT_GOAL,	CHILD_PLAN_PARTICIP
				EVENT_PERSON_LINK,CHILD_PLAN_ITEM,	CP_ADOPTN_DTL,	CP_LGL_GRDNSHP,TABLETODO
		*/
		if (!ObjectUtils.isEmpty(idEvent) && idEvent != 0l) {
			setCurrentlySelectedPlans(populateChildEventStageDto(childPlanReq), childPlanLegacyDto);
			childPlanDao.deleteTopicTable(idEvent, childPlanLegacyDto.getCurrentlySelectedPlanDtoList());
			// Delete from CP_SPRVSN_DTL table
			childPlanDao.deleteSuperVisionDtl(idEvent);
			// Delete from CP_TRANSTN_ADULT_BLW_DTL table
			childPlanDao.deleteCpTranstnAdultBlwDtl(idEvent);
			// Delete from CP_SOCL_RECRTNAL_DTL table
			childPlanDao.deleteSocialRecreationalDtl(idEvent);
			// Delete from CP_CHILD_FMLY_TEAM_DTL table
			childPlanDao.deleteChildFamilyTeamDtl(idEvent);
			// Delete from CP_BHVR_MGMT table
			childPlanDao.deleteBehaviouralDtl(idEvent);
			// Delete from CP_YOUTH_PREGNT_PRNTG table
			childPlanDao.deleteYouthPregntDtl(idEvent);
			// Delete from CP_INTLCTL_DVLPMNTL table
			childPlanDao.deleteIntlCtlDevelopment(idEvent);
			// Delete from CP_TRTMNT_SRVC_DTL table
			childPlanDao.deleteTreatementServiceDtl(idEvent);
			// Delete from CP_TRANSTN_ADULT_ABV_DTL table
			childPlanDao.deleteAdultAboveDtl(idEvent);
			// Delete from CP_SVCS_HGH_RISK_BHVR table
			childPlanDao.deleteHighRiskBehavourDtl(idEvent);
			// Delete from CP_INFORMATION table
			childPlanDao.deleteCPInformation(idEvent);
			// Delete from CP_EDUCTN_DTL table
			childPlanDao.deleteCPEducationDtl(idEvent);
			// Delete from CP_EMTNL_THRPTC_DTL table
			childPlanDao.deleteCPEmtnlThrptcDtl(idEvent);
			// Delete from CP_PSYCH_MEDCTN_DTL table
			childPlanDao.deleteMedCtnDtl(idEvent);
			// Delete from CP_HLTH_CARE_SUMM table
			childPlanDao.deleteCPHealthCareSumm(idEvent);
			// Delete from SSCC_CHILD_PLAN table
			childPlanDao.deleteSSCCChildPlan(idEvent);
			// Delete from CP_VISIT_CNTCT_FMLY table
			childPlanDao.deleteVisitCntFmly(idEvent);
			// Delete from CP_QRTP_PTM table
			childPlanDao.deleteQrtpPtm(idEvent);
			// Delete from CP_ADTNL_SCTN_DTLS table
			childPlanDao.deleteAdtnlSctnDtls(idEvent);
			// Delete from CP_LST_GOALS table
			childPlanDao.deleteCpLstGoals(idEvent);
			// Delete from EVENT_PLAN_LINK table
			childPlanDao.deleteEventPlanLink(idEvent);
			// Delete from CP_CONCURRENT_GOAL table
			childPlanDao.deleteCpConcurrentGoal(idEvent);
			// Delete from CHILD_PLAN table
			childPlanDao.deleteChildPlan(idEvent);
			// Delete from TO DO table
			childPlanDao.deleteTodo(idEvent);
			// Delete from CHILD_PLAN_PARTICIP table
			childPlanDao.deleteChildPlanParticip(idEvent);
			// Delete from EVENT_PERSON_LINK table
			childPlanDao.deleteEventPersonlink(idEvent);
			// Delete from CHILD_PLAN_ITEM table
			childPlanDao.deleteChildPlanItem(idEvent);
			// Delete from CP_ADOPTN_DTL table
			childPlanDao.deleteCpAdoptnDtl(idEvent);
			// Delete from CP_LGL_GRDNSHP table
			childPlanDao.deleteLegalGardianShip(idEvent);
			// Delete the event
			EventInputDto eventInputDto = new EventInputDto();
			eventInputDto.setIdEvent(idEvent);
			eventProcessDao.deleteEvent(eventInputDto);
		}
	}
	/**
	 * Method Name: childPlanNewUsingSave Method Description: This method is
	 * used to save Child Plan after New Using
	 * 
	 * @param childPlanReq
	 * @return ChildPlanRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ChildPlanRes childPlanNewUsingSave(ChildPlanReq childPlanReq) {
		ChildPlanRes childPlanRes = new ChildPlanRes();
		ChildPlanLegacyDto childPlanLegacyDto = childPlanReq.getChildPlanLegacyDto();
		ChildPlanEventDto childPlanEventDto = childPlanLegacyDto.getChildPlanEventDto();
		EventDto eventDto = childPlanLegacyDto.getEventDto();

		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		String reqFuncCd = childPlanReq.getReqFuncCd();

		inCheckStageEventStatusDto.setCdReqFunction(reqFuncCd);
		inCheckStageEventStatusDto.setIdStage(childPlanReq.getIdStage());
		inCheckStageEventStatusDto.setCdTask(childPlanReq.getCdTask());
		Boolean checkStageStatus = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);

		if (!Boolean.TRUE.equals(checkStageStatus)) {
			throw new ServiceLayerException(ServiceConstants.MSG_SYS_STAGE_CLOSED);
		}
		StageDto stageDto = stageDao.getStageById(childPlanReq.getIdStage());

		childPlanRes.setIdCase(stageDto.getIdCase());

		Long idPerson = stagePersonLinkDao.getPersonIdByRole(childPlanReq.getIdStage(), CodesConstant.CROLES_PC);

		eventDto.setIdPerson(idPerson);

		PostEventIPDto postEventInput = new PostEventIPDto();

		postEventInput.setCdTask(eventDto.getCdTask());
		postEventInput.setCdEventStatus(eventDto.getCdEventStatus());
		postEventInput.setCdEventType(eventDto.getCdEventType());
		postEventInput.setIdStage(eventDto.getIdStage());
		postEventInput.setDtEventOccurred(eventDto.getDtEventOccurred());
		postEventInput.setIdPerson(eventDto.getIdPerson());
		postEventInput.setEventDescr(eventDto.getEventDescr());
		postEventInput.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

		List<PostEventDto> postEventDtos = new ArrayList<>();
		PostEventDto postEventDto = new PostEventDto();
		postEventDto.setIdPerson(idPerson);
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventDtos.add(postEventDto);

		postEventInput.setPostEventDto(postEventDtos);

		PostEventOPDto postEventOutput = postEventService.checkPostEventStatus(postEventInput, serviceReqHeaderDto);

		childPlanRes.setIdEvent(postEventOutput.getIdEvent());
		childPlanEventDto.setIdChildPlanEvent(postEventOutput.getIdEvent());

		todoDao.updateToDoEvent(eventDto.getCdTask(), eventDto.getIdStage(), childPlanRes.getIdEvent());

		if (CodesConstant.CEVTSTAT_COMP.equals(eventDto.getCdEventStatus())) {
			updateToDoDao.completeTodoEvent(childPlanRes.getIdEvent());
		}

		childPlanDao.childPlanAud(childPlanEventDto, ServiceConstants.REQ_FUNC_CD_ADD);

		List<String> selectedConcurrentGoals = childPlanLegacyDto.getSelectedConcurrentGoals();

		if (!ObjectUtils.isEmpty(selectedConcurrentGoals)) {
			for (String concurrentGoal : selectedConcurrentGoals) {
				childPlanDao.addCpConcurrentGoal(concurrentGoal, childPlanEventDto, childPlanRes.getIdCase(),
						childPlanReq.getIdStage());
			}
		}

		childPlanEventDto = fetchPlanDao.getChildPlanEvent(childPlanEventDto.getIdChildPlanEvent());

		String cdCspPlanTypeOld = childPlanReq.getCdPlanTypeOld();
		String cdCspPlanTypeNew = childPlanEventDto.getCdCspPlanType();
		Long idChildPlanEventOld = childPlanReq.getIdEvent();
		Long idChildPlanEventNew = childPlanEventDto.getIdChildPlanEvent();

		childPlanNarrativeDao.insertChildPlanNarrative(cdCspPlanTypeOld, idChildPlanEventOld, idChildPlanEventNew);

		if (!ObjectUtils.isEmpty(cdCspPlanTypeOld) && !cdCspPlanTypeOld.equalsIgnoreCase(cdCspPlanTypeNew)) {
			childPlanNarrativeDao.deleteChildPlanNarrative(cdCspPlanTypeOld, cdCspPlanTypeNew, idChildPlanEventNew);
		}

		currentPlanDao.getCurrentlySelectedPlans(idChildPlanEventNew, cdCspPlanTypeNew, childPlanLegacyDto);

		childPlanLegacyDto.setChildPlanEventDto(childPlanEventDto);
		childPlanRes.setChildPlanLegacyDto(childPlanLegacyDto);
		return childPlanRes;
	}

	/**
	 * Method Name: childPlanSave Method Description: This method is used to
	 * Save child Plan
	 * 
	 * @param childPlanReq
	 * @return ChildPlanRes
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public ChildPlanRes childPlanSave(ChildPlanReq childPlanReq) {

		ChildPlanRes childPlanRes = new ChildPlanRes();
		ChildPlanLegacyDto childPlanLegacyDto = childPlanReq.getChildPlanLegacyDto();
		ChildPlanEventDto childPlanEventDto = childPlanLegacyDto.getChildPlanEventDto();
		EventDto eventDto = childPlanLegacyDto.getEventDto();

		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		String reqFuncCd = childPlanReq.getReqFuncCd();

		if (!ObjectUtils.isEmpty(childPlanEventDto.getIdChildPlanEvent())
				&& childPlanEventDto.getIdChildPlanEvent() > 0) {
			reqFuncCd = ServiceConstants.REQ_FUNC_CD_UPD;
		} else {
			reqFuncCd = ServiceConstants.REQ_FUNC_CD_ADD;
		}

		inCheckStageEventStatusDto.setCdReqFunction(reqFuncCd);
		inCheckStageEventStatusDto.setIdStage(childPlanReq.getIdStage());
		inCheckStageEventStatusDto.setCdTask(childPlanReq.getCdTask());
		Boolean checkStageStatus = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);

		if (!Boolean.TRUE.equals(checkStageStatus)) {
			throw new ServiceLayerException(ServiceConstants.MSG_SYS_STAGE_CLOSED);
		}

		StageDto stageDto = stageDao.getStageById(childPlanReq.getIdStage());

		childPlanRes.setIdCase(stageDto.getIdCase());

		Event event = eventDao.getEventById(childPlanEventDto.getIdChildPlanEvent());

		if (ServiceConstants.EVENT_STATUS_PEND.equals(event.getCdEventStatus()) && !childPlanReq.isApprovalMode()) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
			approvalCommonInDto.setIdEvent(childPlanEventDto.getIdChildPlanEvent());
			// Call Service to invalidate approval
			approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);
		}

		Long idPerson = stagePersonLinkDao.getPersonIdByRole(childPlanReq.getIdStage(), CodesConstant.CROLES_PC);

		eventDto.setIdPerson(idPerson);

		List<EventDto> eventList = eventDao.getEventByStageIDAndTaskCode(childPlanReq.getIdStage(), ServiceConstants.CLOSE_SUB_STAGE);

		EventDto stageClosureEvent = null;
		
		if (!ObjectUtils.isEmpty(eventList)) {
			stageClosureEvent = eventList.stream()
					.filter(x -> ServiceConstants.EVENT_STATUS_PEND.equals(x.getCdEventStatus()))
					.findAny().orElse(null);
		}
		Long idEvent = ServiceConstants.ZERO;
		
		String stageClosureEventStatus = ServiceConstants.EMPTY_STRING;
		
		if (!ObjectUtils.isEmpty(stageClosureEvent)) {
			stageClosureEventStatus = stageClosureEvent.getCdEventStatus();
		}
		if (ServiceConstants.EVENT_STATUS_PEND.equals(stageClosureEventStatus)
				&& !ServiceConstants.REQ_FUNC_CD_ADD.equals(reqFuncCd) && !childPlanReq.isApprovalMode()) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
			approvalCommonInDto.setIdEvent(stageClosureEvent.getIdEvent());
			// Call Service to invalidate approval
			approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);

			EventUpdEventStatusInDto eventUpdEventStatusInDto = new EventUpdEventStatusInDto();
			eventUpdEventStatusInDto.setIdEvent(stageClosureEvent.getIdEvent());
			eventUpdEventStatusInDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
			eventUpdEventStatusInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
			updateEventStatusDao.updateEvent(eventUpdEventStatusInDto);

			eventUpdEventStatusInDto.setIdEvent(eventDto.getIdEvent());
			eventUpdEventStatusInDto.setCdEventStatus(eventDto.getCdEventStatus());
			eventUpdEventStatusInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
			updateEventStatusDao.updateEvent(eventUpdEventStatusInDto);

		} else {
			PostEventIPDto postEventInput = new PostEventIPDto();
			ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();

			List<PostEventDto> postEventDtos = new ArrayList<>();
			PostEventDto postEventDto = new PostEventDto();

			if (!ObjectUtils.isEmpty(eventDto.getIdEvent()) && eventDto.getIdEvent() > 0) {
				postEventInput.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPD);
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPD);
				postEventInput.setIdEvent(eventDto.getIdEvent());
				postEventInput.setTsLastUpdate(eventDto.getDtLastUpdate());
			} else {
				postEventInput.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				// Modified the code to update the ID_EVENT_PERSON filed with
				// logged in User ID in EVENT table when saving the child plan
				// for Warranty defect 11026
				postEventDto.setIdPerson(childPlanReq.getIdPerson());
				postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
				postEventDtos.add(postEventDto);

				postEventInput.setPostEventDto(postEventDtos);
			}

			postEventInput.setCdTask(eventDto.getCdTask());
			postEventInput.setCdEventStatus(eventDto.getCdEventStatus());
			postEventInput.setCdEventType(eventDto.getCdEventType());
			postEventInput.setIdStage(eventDto.getIdStage());
			postEventInput.setDtEventOccurred(eventDto.getDtEventOccurred());
			// Modified the code to update the ID_EVENT_PERSON filed with
			// logged in User ID in EVENT table when saving the child plan
			// for Warranty defect 11026
			postEventInput.setIdPerson(childPlanReq.getIdPerson());
			postEventInput.setEventDescr(eventDto.getEventDescr());

			PostEventOPDto postEventOutput = postEventService.checkPostEventStatus(postEventInput, serviceReqHeaderDto);

			idEvent = postEventOutput.getIdEvent();

			if (ServiceConstants.EVENT_STATUS_PEND.equals(stageClosureEventStatus) && !childPlanReq.isApprovalMode()) {
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
				approvalCommonInDto.setIdEvent(stageClosureEvent.getIdEvent());
				// Call Service to invalidate approval
				approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);

				EventUpdEventStatusInDto eventUpdEventStatusInDto = new EventUpdEventStatusInDto();
				eventUpdEventStatusInDto.setIdEvent(stageClosureEvent.getIdEvent());
				eventUpdEventStatusInDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
				eventUpdEventStatusInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
				updateEventStatusDao.updateEvent(eventUpdEventStatusInDto);
			}

		}

		if (ObjectUtils.isEmpty(eventDto.getIdEvent()) || eventDto.getIdEvent() == 0) {
			childPlanRes.setIdChildPlanEvent(idEvent);
			todoDao.updateToDoEvent(eventDto.getCdTask(), eventDto.getIdStage(), idEvent);
		} else {
			childPlanRes.setIdChildPlanEvent(childPlanEventDto.getIdChildPlanEvent());
		}

		if (CodesConstant.CEVTSTAT_COMP.equals(eventDto.getCdEventStatus())) {
			updateToDoDao.completeTodoEvent(eventDto.getIdEvent());
		}

		childPlanEventDto.setIdChildPlanEvent(childPlanRes.getIdChildPlanEvent());

		childPlanDao.childPlanAud(childPlanEventDto, reqFuncCd);

		List<ConcurrentGoalDto> concurrentGoalDtoList = fetchConcurrentGoalDao
				.getConcurrentGoals(childPlanRes.getIdChildPlanEvent());

		List<String> previouslySelectedConcurrentGoals = new ArrayList<String>();
		if (!ObjectUtils.isEmpty(concurrentGoalDtoList)) {
			concurrentGoalDtoList.forEach(concurrentGoal -> {
				previouslySelectedConcurrentGoals.add(concurrentGoal.getCdConcurrentGoal());
			});
		}

		List<String> selectedConcurrentGoals = childPlanLegacyDto.getSelectedConcurrentGoals();

		if (!ObjectUtils.isEmpty(selectedConcurrentGoals)) {
			for (String concurrentGoal : selectedConcurrentGoals) {
				if (!previouslySelectedConcurrentGoals.contains(concurrentGoal)) {
					childPlanDao.addCpConcurrentGoal(concurrentGoal, childPlanEventDto, childPlanRes.getIdCase(),
							childPlanReq.getIdStage());
				}
			}
		}

		if (!ObjectUtils.isEmpty(previouslySelectedConcurrentGoals)) {
			for (ConcurrentGoalDto concurrentGoal : concurrentGoalDtoList) {
				if (!selectedConcurrentGoals.contains(concurrentGoal.getCdConcurrentGoal())) {
					childPlanDao.deleteCpConcurrentGoalById(concurrentGoal.getIdCpConGoal());
				}
			}
		}

		childPlanEventDto = fetchPlanDao.getChildPlanEvent(childPlanEventDto.getIdChildPlanEvent());

		String cdCspPlanTypeOld = childPlanReq.getCdPlanTypeOld();
		String cdCspPlanTypeNew = childPlanEventDto.getCdCspPlanType();

		if (!ObjectUtils.isEmpty(cdCspPlanTypeOld) && !cdCspPlanTypeOld.equalsIgnoreCase(cdCspPlanTypeNew)) {
			childPlanNarrativeDao.deleteChildPlanNarrative(cdCspPlanTypeOld, cdCspPlanTypeNew,
					childPlanEventDto.getIdChildPlanEvent());
		}

		// currentPlanDao.getCurrentlySelectedPlans(childPlanEventDto.getIdChildPlanEvent(),
		// cdCspPlanTypeNew, childPlanLegacyDto);

		childPlanLegacyDto.setChildPlanEventDto(childPlanEventDto);
		childPlanRes.setChildPlanLegacyDto(childPlanLegacyDto);
		return childPlanRes;
	}

	@Override
	public ChildPlanParticipRes saveChildPlanParticip(ChildPlanParticipantDto childPlanParticipDto) {
		Long idChildPlan;
		ChildPlanParticipRes childPlanParticipRes = new ChildPlanParticipRes();
		idChildPlan = childPlanParticipantDao.saveOrUpdateChildPlanParticip(childPlanParticipDto);
		childPlanParticipDto.setIdChildPlanPart(idChildPlan);
		childPlanParticipRes.setChildPlanParticipDto(childPlanParticipDto);
		return childPlanParticipRes;
	}

	@Override
	public String deleteChildPlanParticip(ChildPlanParticipantDto childPlanParticipDto) {

		String success;
		success = childPlanParticipantDao.deleteChildPlanParticip(childPlanParticipDto.getIdChildPlanPart());

		return success;
	}
	
	@Override
	public ChildPlanParticipantDto fetchSsccChildPlanParticipant(Long idSsccChildPlanParticip) {
		return childPlanParticipantDao.fetchSsccChildPlanParticipant(idSsccChildPlanParticip);
	}

	@Override
	public ChildPlanParticipRes fetchChildPlanParticipant(Long idEvent, Long idChildPlanPart) {
		ChildPlanParticipRes childPlanParticipRes = new ChildPlanParticipRes();
		List<ChildPlanParticipantDto> childPlanParticipantDtoList = new ArrayList<>();
		ChildPlanParticipantDto childPlanParticip = new ChildPlanParticipantDto();
		childPlanParticipantDtoList = childPlanParticipantDao.fetchChildPlanParticipant(idEvent);
		
		if(!ObjectUtils.isEmpty(childPlanParticipantDtoList)) {
			childPlanParticip = childPlanParticipantDtoList.stream().filter(participant-> idChildPlanPart.equals(participant.getIdChildPlanPart())).findAny().get();
		}

		childPlanParticipRes.setChildPlanParticipDto(childPlanParticip);
		return childPlanParticipRes;
	}

	@Override
	public ChildPlanParticipRes staffSearchResultInformation(Long idStage) {

		List<StaffSearchResultDto> staffSearchResultDto;
		ChildPlanParticipRes childPlanParticipRes = new ChildPlanParticipRes();

		staffSearchResultDto = childPlanDao.getStaffSearchResultInformation(idStage);
		if (!ObjectUtils.isEmpty(staffSearchResultDto)) {
			childPlanParticipRes.setStaffSearchResultDto(staffSearchResultDto);
		}
		return childPlanParticipRes;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ChildPlanRes saveChildPlan(ChildPlanReq childPlanReq) {
		ChildPlanRes childPlanRes = new ChildPlanRes();

		ChildPlanLegacyDto childPlanLegacyDto = childPlanReq.getChildPlanLegacyDto();

		Date dtLastUpdatePrimaryChild = childPlanDao
				.getDtLastUpdatePrimaryChild(childPlanLegacyDto.getPrimaryChildPersonId());
		childPlanLegacyDto.setDtLastUpdatePrimaryChild(dtLastUpdatePrimaryChild);

		String pageMode = childPlanReq.getPageMode();
		List<ChildPlanGuideTopicDisplayDto> guideTopicDisplayList = childPlanLegacyDto.getGuideTopicDisplayList();

		if (ServiceConstants.NEW_USING.equals(pageMode)) {
			childPlanRes = childPlanNewUsingSave(childPlanReq);
		} else {
			if (!ObjectUtils.isEmpty(childPlanReq.getIdEvent()) && childPlanReq.getIdEvent() > 0
					&& !ObjectUtils.isEmpty(guideTopicDisplayList)) {
				for (ChildPlanGuideTopicDisplayDto childPlanGuideTopicDisplayDto : guideTopicDisplayList) {
					String changedIndicator = childPlanGuideTopicDisplayDto.getBean1().getChangedIndicator();
					if (!ObjectUtils.isEmpty(changedIndicator) && ServiceConstants.Y.equals(changedIndicator)) {
						childPlanDao.saveGuideTopic(childPlanGuideTopicDisplayDto.getBean1());
					}
					if (!ObjectUtils.isEmpty(childPlanGuideTopicDisplayDto.getBean2())) {
						String pairChangedIndicator = childPlanGuideTopicDisplayDto.getBean2()
								.getPairChangedIndicator();
						if (!ObjectUtils.isEmpty(pairChangedIndicator)
								&& ServiceConstants.Y.equals(pairChangedIndicator)) {
							childPlanDao.saveGuideTopic(childPlanGuideTopicDisplayDto.getBean2());
						}
					}
				}
			}

			childPlanRes = childPlanSave(childPlanReq);

			childPlanDao.saveChildPriorAdoption(childPlanLegacyDto);

			ChildPlanEventDto childPlanEventDto = childPlanLegacyDto.getChildPlanEventDto();

			String indsaveAndSubmit = childPlanReq.getIndSaveAndSubmit();

			if (ServiceConstants.Y.equals(indsaveAndSubmit)) {
				HashMap personDetail = new HashMap();

				if (CodesConstant.CSTAGES_ADO.equalsIgnoreCase(childPlanReq.getCdStage())
						|| CodesConstant.CSTAGES_SUB.equalsIgnoreCase(childPlanReq.getCdStage())) {
					personDetail = criminalHistoryService.checkCrimHistAction(childPlanReq.getIdStage());
				}
				boolean crimHistPending = false;

				crimHistPending = criminalHistoryService.isCrimHistPending(childPlanReq.getIdStage());

				String cdTask = childPlanReq.getCdTask();

				if (!crimHistPending && (ObjectUtils.isEmpty(personDetail))
						&& ((!ADO_CHILD_PLAN_TASK_CODE.equals(cdTask) || (!SUB_CHILD_PLAN_TASK_CODE.equals(cdTask))))) {
					String cdApprovalTask = ServiceConstants.EMPTY_STRING;

					if (ADO_CHILD_PLAN_TASK_CODE.equals(cdTask)) {
						cdApprovalTask = APPROVE_ADO_CHILD_PLAN_TASK_CODE;
					} else if (SUB_CHILD_PLAN_TASK_CODE.equals(cdTask)) {
						cdApprovalTask = APPROVE_SUB_CHILD_PLAN_TASK_CODE;
					}

					ToDoDetailBean toDoDetailBean = new ToDoDetailBean();
					toDoDetailBean.setIdEvent(childPlanReq.getIdEvent());
					toDoDetailBean.setIdCase(childPlanReq.getIdCase());
					toDoDetailBean.setIdStage(childPlanReq.getIdStage());
					toDoDetailBean.setCdTask(cdApprovalTask);
					childPlanRes.setToDoDetailBean(toDoDetailBean);
				} else {
					HashMap<Integer, String> errorMap = new HashMap<Integer, String>();
					boolean listAdded = false;

					if (!ObjectUtils.isEmpty(personDetail) && personDetail.size() != 0) {
						Long idPerson = (Long) personDetail.get(ServiceConstants.ID_PERSON_CONST);
						String nmPerson = (String) personDetail.get(ServiceConstants.NAMEPERSON);
						childPlanRes.setIdPerson(idPerson);
						childPlanRes.setNmPerson(nmPerson);
						errorMap.put(CRIM_HIST_PENDING, lookupDao.getMessage(CRIM_HIST_PENDING));
						listAdded = true;
					}

					if (crimHistPending && listAdded) {
						errorMap.put(MSG_CRML_HIST_CHECK_STAGE_PEND,
								lookupDao.getMessage(MSG_CRML_HIST_CHECK_STAGE_PEND));
					} else if (crimHistPending && !listAdded) {
						errorMap.put(MSG_CRML_HIST_CHECK_STAGE_PEND,
								lookupDao.getMessage(MSG_CRML_HIST_CHECK_STAGE_PEND));
					}
					childPlanRes.setErrorMap(errorMap);
				}

			}
			Long idSSCCReferral = ssccChildPlanDao.getActiveSSCCReferral(childPlanReq.getIdStage());

			if (!ObjectUtils.isEmpty(idSSCCReferral) && idSSCCReferral > 0 && ObjectUtils.isEmpty(childPlanRes.getErrorMap())) {

				SSCCChildPlanDto ssccChildPlanDto = new SSCCChildPlanDto();
				ssccChildPlanDto.setIdSSCCReferral(idSSCCReferral);
				SSCCRefDto ssccRefDto = ssccRefUtil.readSSCCRefByPK(idSSCCReferral);

				ssccChildPlanDto.setIdEvent(childPlanEventDto.getIdChildPlanEvent());
				ssccChildPlanDto = ssccChildPlanDao.querySSCCChildPlan(ssccChildPlanDto);

				if (!ObjectUtils.isEmpty(ssccChildPlanDto)) {
					if (ServiceConstants.Y.equals(childPlanReq.getIndSaveAndSubmit())) {
						ssccChildPlanUtility.createTimelineRecord(ssccChildPlanDto, ServiceConstants.CSSCCTBL_40,
								"Child Plan Submitted for Approval", childPlanReq.getIdPerson());
					} else {
						Long idRsrc = ssccRefDto.getIdRsrcSSCC();
						String cdPlanType = childPlanLegacyDto.getChildPlanEventDto().getCdCspPlanType();

						List<String> ssccAssignedTopics = ssccChildPlanDao.queryAssignedTopics(idRsrc, cdPlanType);
						// skipping new using

						if (ServiceConstants.MODIFY.equals(pageMode)) {
							String cdCspPlanTypeOld = childPlanReq.getCdPlanTypeOld();
							String cdCspPlanTypeNew = childPlanEventDto.getCdCspPlanType();

							if (!ObjectUtils.isEmpty(cdCspPlanTypeOld)
									&& !cdCspPlanTypeOld.equalsIgnoreCase(cdCspPlanTypeNew)) {

								ssccChildPlanUtility.createTimelineRecord(ssccChildPlanDto,
										ServiceConstants.CSSCCTBL_40, "Child Plan Type Updated",
										childPlanReq.getIdPerson());

								List<String> old = ssccChildPlanDao.queryAssignedTopics(idRsrc, cdPlanType);
								Set<String> delete = new HashSet<String>(old);
								delete.removeAll(ssccAssignedTopics);
								if (!delete.isEmpty()) {
									ssccChildPlanService.deleteSSCCTopicsForPlan(ssccChildPlanDto,
											new ArrayList<String>(delete));
								}
								Set<String> add = new HashSet<String>(ssccAssignedTopics);
								add.removeAll(old);
								if (!add.isEmpty()) {
									ssccChildPlanService.saveSSCCChildPlanTopics(ssccChildPlanDto,
											new ArrayList<String>(add));
								}
							}
						}
					}
				}
			}
		}

		return childPlanRes;
	}
}
