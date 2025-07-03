package us.tx.state.dfps.service.prt.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.phoneticsearch.IIRHelper.StringHelper;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.contacts.dao.StagePersonDao;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PRTFollowUpPrefillData;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dao.PRTActPlanFollowUpDao;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDao;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDataFetchDao;
import us.tx.state.dfps.service.prt.dao.PRTDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.service.PRTActPlanFollowUpService;
import us.tx.state.dfps.service.prt.service.PRTActionPlanService;
import us.tx.state.dfps.service.prt.service.PRTService;
import us.tx.state.dfps.service.prt.utils.PRTActPlanFollowUpUtil;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to handle the service implementation for PRT Follow-Up. Mar 28, 2018-
 * 3:27:10 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PRTActPlanFollowUpServiceImpl implements PRTActPlanFollowUpService {

	private static final Logger log = Logger.getLogger("ServiceBusiness-PRTFollowUpServicelog");

	@Autowired
	EventUtilityService eventUtilityService;

	@Autowired
	PRTActPlanFollowUpDao prtActPlanFollowUpDao;

	@Autowired
	PRTActPlanFollowUpUtil prtActPlanFollowUpUtil;

	@Autowired
	PRTActionPlanDataFetchDao prtActionPlanDataFetchDao;

	@Autowired
	PRTActionPlanService prtActionPlanService;

	@Autowired
	StagePersonDao stagePersonDao;

	@Autowired
	PRTActionPlanDao prtActionPlanDao;

	@Autowired
	PRTFollowUpPrefillData followUpPrefill;

	@Autowired
	PRTDao prtDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	EventDao eventDao;

	@Autowired
	PRTService prtService;

	public static final String EVNT_ACTPLN_FOLLOWUP_COMP = "PRT Follow-Up Completed on ";

	public static final String EVNT_ACTPLN_FOLLOWUP_INPROGRESS = "PRT Follow-Up is in progress.";

	/**
	 * Method Name: fetchPrevFollowUp Method Description:This method is used to
	 * fetch the details of the follow-up.
	 * 
	 * @param idFollowUp
	 *            - The id of the event of the follow-up.
	 * @param idStage
	 *            - The stage id in which the follow-up was created.
	 * @param idCase
	 *            - The case id in which the follow-up was created.
	 * @return PRTActPlanFollowUpDto - The dto with the follow-up details.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PRTActPlanFollowUpDto fetchActPlanFollowUp(Long idActplnFollowupEvent, Long idStage, Long idCase) {
		PRTActPlanFollowUpDto followUp = new PRTActPlanFollowUpDto();
		// Action Plan Event.
		if ((!ObjectUtils.isEmpty(idActplnFollowupEvent) && idActplnFollowupEvent.equals(0l))
				|| ObjectUtils.isEmpty(idActplnFollowupEvent)) {
			// Get the Primary Child for Current Stage.
			Long idPrimaryChild = stagePersonDao.getPrimaryClientIdForStage(idStage);
			// An Action Plan Follow-Up cannot be added because the PRT is
			// closed.
			Long idActionPlan = prtActionPlanDataFetchDao.fetchOpenActionPlan(idPrimaryChild);
			// Fetch Action Plan Details.
			long idActionPlanEvent = prtService.getActPlanEventId(idActionPlan);
			PRTActionPlanDto actionPlan = prtActionPlanService.fetchActionPlan(idActionPlanEvent, idStage, idCase);
			followUp = initiateNewFollowUp(actionPlan, idStage, idCase);
			// Fetch Unit Number of the Primary Worker of the Stage.
			prtActPlanFollowUpUtil.populateFollowUpUnitInfo(actionPlan, followUp);
		} else {
			// Action Plan Follow Up - Retrieve Action Plan Follow Up using
			// Event.
			followUp = prtActPlanFollowUpDao.selectFollowUpUsingEventId(idActplnFollowupEvent);
			Long idPrtActplnFollowup = followUp.getIdPrtActplnFollowup();

			// Get Action Plan associated with the follow-up.sav
			Long idActionPlan = followUp.getIdPrtActionPlan();

			// Fetch Action Plan Details.
			Long idActionPlanEvent = prtService.getActPlanEventId(idActionPlan);
			PRTActionPlanDto actionPlan = prtActionPlanService.fetchActionPlan(idActionPlanEvent, idStage, idCase);

			// Action Plan Follow Up - Retrieve Action Plan Follow Up Events.
			List<PRTEventLinkDto> followUpEvtList = prtDao.selectPrtEventLink(idPrtActplnFollowup,
					ActionPlanType.ACTPLN_FOLLOWUP);
			followUp.setFollowUpEvtLinks(followUpEvtList);
			// Action Plan Follow Up Event - Retrieve Action Plan Follow Up
			// Event.
			EventValueDto followUpEvent = eventUtilityService.fetchEventInfo(idActplnFollowupEvent);
			followUp.setFollowUpEvent(followUpEvent);
			String eventStatus = followUpEvent.getCdEventStatus();
			// Children - Fetch Children for the Action Plan.
			List<PRTPersonLinkDto> prtPersonList = prtActPlanFollowUpDao.selectPrtChildren(idPrtActplnFollowup);
			followUp.setChildren(prtPersonList);

			// Populate Event Id associated with the Child.
			prtActionPlanDao.populateEventIdForChild(prtPersonList, followUp.getIdPrtActplnFollowup(),
					ActionPlanType.ACTPLN_FOLLOWUP);
			// If Event Status is PROC (COMP = PRT Action Plan Frozen),
			if (CodesConstant.CEVTSTAT_PROC.equals(eventStatus)) {
				// refresh the Follow Up data while fetching the Follow UP data
				// in PROC status.
				refreshDataForFollowUpFetchInPROC(followUp, actionPlan, idStage, idCase);

				// Connections - Fetch PRT Connections for Children
				prtActPlanFollowUpUtil.populateFollowUpConnectionsInPROC(followUp);
			}
			// If Event Status is COMP.
			else if (CodesConstant.CEVTSTAT_COMP.equals(eventStatus)) {
				// refresh the Follow Up data while fetching the Follow UP data
				// in COMP status.
				refreshDataForFollowUpFetchInCOMP(followUp, actionPlan, idStage, idCase);

				// Connections - Fetch PRT Connections for Children
				prtActPlanFollowUpUtil.populateFollowUpConnectionsInCOMP(followUp);
				PRTActplanFollowUpReq prtActplanFollowUpReq = new PRTActplanFollowUpReq();
				prtActplanFollowUpReq.setIdStage(idStage);
				prtActplanFollowUpReq.setIdCase(idCase);
				prtActplanFollowUpReq.setIdEvent(idActplnFollowupEvent);
				getConnCount(prtActplanFollowUpReq, followUp);

			}

			// Get strategies and tasks from PRT Tables.
			prtService.fetchStrategies(followUp, false);

			// Fetch Unit Number of the Primary Worker of the Stage.
			prtActPlanFollowUpUtil.populateFollowUpUnitInfo(actionPlan, followUp);
		}
		// Calculate Follow up Month
		prtActPlanFollowUpUtil.calculateFollowUpMonth(followUp);

		// populate the perm status and the follow-up type map
		followUp.setPermStatusList(prtService.getPermStatus());

		return followUp;
	}

	/**
	 * This method retrieves all PRT Action Plan FollowUp Details.
	 * 
	 * @param idActplnFollowupEvent
	 * @param idStage
	 * @param idCase
	 * 
	 * @return PRTActPlanFollowUpDto
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto displayActPlanFollowUpFrom(Long idActplnFollowupEvent, Long idStage, Long idCase) {
		PRTActPlanFollowUpDto followUp = new PRTActPlanFollowUpDto();
		try {
			followUp = fetchActPlanFollowUp(idActplnFollowupEvent, idStage, idCase);
			followUp.setIdCurrentCase(idCase);
			followUp.setIdCurrentEvent(idActplnFollowupEvent);
			followUp.setIdCurrentStage(idStage);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return followUpPrefill.returnPrefillData(followUp);
	}

	/**
	 * Method Name: initiateNewFollowUp Method Description:This method is used
	 * to create the data for a new PRT Follow-Up.
	 * 
	 * @param actionPlan
	 * @param idStage
	 *            - The current stage id.
	 * @param idCase
	 *            - The current case id.
	 * @return followUp - The dto which will hold the PRT Follow-Up details.
	 */
	private PRTActPlanFollowUpDto initiateNewFollowUp(PRTActionPlanDto actionPlan, Long idStage, Long idCase) {
		PRTActPlanFollowUpDto followUp = new PRTActPlanFollowUpDto();
		// PRT Action Plan associated with the Follow Up
		Long idActionPlan = actionPlan.getIdPrtActionPlan();
		followUp.setIdPrtActionPlan(idActionPlan);
		// Latest Action Plan Follow Up
		Long idLatestFollowUp = prtActPlanFollowUpDao.selectLatestFollowUp(idActionPlan, CodesConstant.CEVTSTAT_COMP);
		PRTActPlanFollowUpDto latestFollowUp = new PRTActPlanFollowUpDto();
		if (idLatestFollowUp != 0) {
			latestFollowUp = fetchPrevFollowUp(idLatestFollowUp, idStage, idCase);
			followUp.setIdSrcPrtActplnFollowup(latestFollowUp.getIdPrtActplnFollowup());
		}

		// ACTION PLAN FOLLOW-UP DATA PREFILL RULES
		// Get the most recent follow Up Children with Prev Perm Status that is
		// not null.
		List<PRTPersonLinkDto> followUpChildrenWithPermstatus = prtActPlanFollowUpUtil
				.getChildrenForMostRecentFollowUpWithPermStatus(idActionPlan);

		// Populate Event Id associated with the Child.
		prtActPlanFollowUpDao.populateEventIdForChild(actionPlan.getChildren(), idActionPlan,
				ActionPlanType.ACTPLN_FOLLOWUP);

		// Child Information.
		List<PRTPersonLinkDto> children = new ArrayList<PRTPersonLinkDto>();
		for (PRTPersonLinkDto actPlnChild : actionPlan.getChildren()) {
			// New FollowUp Child
			PRTPersonLinkDto newChild = new PRTPersonLinkDto();
			// Person Information - from Action Plan.
			// newChild.setIdPrtActionPlan( idActionPlan );
			newChild.setIdPerson(actPlnChild.getIdPerson());
			newChild.setNmPersonFull(actPlnChild.getNmPersonFull());
			newChild.setDtBirth(actPlnChild.getDtBirth());
			newChild.setIdChildSUBStage(actPlnChild.getIdChildSUBStage());
			newChild.setIdChildEventId(actPlnChild.getIdChildEventId());
			// Get the Child Record for Most Recent FollowUp (Only if there is
			// followUp)
			PRTPersonLinkDto latestFollowUpChild = prtActPlanFollowUpUtil.getFollowUpChildRec(latestFollowUp,
					actPlnChild.getIdPerson());

			// Populate Child's Follow Up Status
			prtActPlanFollowUpUtil.populateChildFollowUpStatus(idLatestFollowUp, latestFollowUpChild, newChild,
					followUp);

			// Follow-Up after a child exits the PRT
			if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
				newChild.setIdPlcmtEvent(latestFollowUpChild.getIdPlcmtEvent());
				// Display: must always be the same data as the Follow-Up for
				// the
				// month the child exited the PRT)
				newChild.setDtPrtExit(latestFollowUpChild.getDtPrtExit());
				newChild.setCdExitReason(latestFollowUpChild.getCdExitReason());
			}
			// Follow-Up in which a child exits the PRT
			else if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT) {
				newChild.setIdPlcmtEvent(latestFollowUpChild.getIdPlcmtEvent());
				Date legStatDate = prtService.getExitLegalStatusDate(newChild.getIdPerson());
				newChild.setDtPrtExit(ObjectUtils.isEmpty(legStatDate) ? new Date() : legStatDate);
			}
			// Subsequent Follow-Ups.
			else if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU) {
				newChild.setIdPlcmtEvent(latestFollowUpChild.getIdPlcmtEvent());
			}

			// First Follow Up
			else if (newChild.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.FIRST_APFU) {
				newChild.setIdPlcmtEvent(actPlnChild.getIdPlcmtEvent());
			}

			// Previous Permanency Status.
			Long permStatus = prtActPlanFollowUpUtil.getPrevPermStatus(followUpChildrenWithPermstatus, actPlnChild,
					newChild);
			newChild.setIdPrevPermStatus(permStatus);

			// Connection Name and Rel/Int.
			List<PRTConnectionDto> prtConnections = prtActPlanFollowUpUtil.initiateNewFollowUpConnections(newChild,
					latestFollowUpChild, actPlnChild);
			newChild.setPrtConnectionValueDtoList(prtConnections);
			children.add(newChild);
		}

		// Get Latest Placement Info and Goals.
		prtActPlanFollowUpUtil.refreshPlacementAndChildPlan(idActionPlan, children);

		// Get strategies
		if (idLatestFollowUp == 0l) {
			followUp.setStrategies(actionPlan.getPrtStrategiesDto());
		} else {
			followUp.setStrategies(latestFollowUp.getStrategies());
		}

		// Add Children to Follow Up.
		followUp.setChildren(children);

		return followUp;
	}

	/**
	 * Method Name: refreshDataForFollowUpFetchInPROC Method Description:This
	 * method is used to populate the child status , placement details for each
	 * child.
	 * 
	 * @param followUp
	 *            - The dto will hold the follow-up details.
	 * @param actionPlan
	 *            - The dto will hold the action plan details.
	 * @param idStage
	 *            - The current stage id.
	 * @param idCase
	 *            - The current case id.
	 */
	private void refreshDataForFollowUpFetchInPROC(PRTActPlanFollowUpDto followUp, PRTActionPlanDto actionPlan,
			Long idStage, Long idCase) {
		Long idActionPlan = actionPlan.getIdPrtActionPlan();
		// Source (Latest) Action Plan Follow Up
		Long idLatestFollowUp = followUp.getIdSrcPrtActplnFollowup();
		PRTActPlanFollowUpDto latestFollowUp = new PRTActPlanFollowUpDto();
		if (!ObjectUtils.isEmpty(idLatestFollowUp) && idLatestFollowUp != ServiceConstants.ZERO_VAL) {
			latestFollowUp = fetchPrevFollowUp(idLatestFollowUp, idStage, idCase);
		}
		// ACTION PLAN FOLLOW-UP DATA PREFILL RULES.
		List<PRTPersonLinkDto> children = followUp.getChildren();
		for (PRTPersonLinkDto child : children) {
			// Get the Child Record for Most Recent FollowUp (Only if there is
			// followUp)
			PRTPersonLinkDto latestFollowUpChild = prtActPlanFollowUpUtil.getFollowUpChildRec(latestFollowUp,
					child.getIdPerson());

			// Populate Child's Follow Up Status
			prtActPlanFollowUpUtil.populateChildFollowUpStatus(idLatestFollowUp, latestFollowUpChild, child, followUp);

			// Follow-Up in which a child exits the PRT
			if (child.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT) {
				Date legStatDate = prtService.getExitLegalStatusDate(child.getIdPerson());
				child.setDtPrtExit(ObjectUtils.isEmpty(legStatDate) ? new Date() : legStatDate);
			}
			// Legal status is Adoption Consummated while creating the record,
			// then
			// changed when the record is in PROC, We need to reset the values
			// as per
			// the rules of Subsequent followup.
			else if (child.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU
					|| child.getFollowUpStatus() == PRTPersonLinkDto.ChildFollowUpStatus.FIRST_APFU) {
				child.setDtPrtExit(null);
				child.setCdExitReason("");
				// If the follow up is not Bi-Annual or Closing summary, reset
				// Current Perm Status to Zero.
				if (!CodesConstant.CPRTFLTY_30.equals(followUp.getCdType())
						&& !CodesConstant.CPRTFLTY_40.equals(followUp.getCdType())) {
					child.setIdPrtPermStatusLookup(ServiceConstants.ZERO_VAL);
				}
			}
		}
		// Get Latest Placement Info and Goals.
		refreshPlacementAndChildPlan(idActionPlan, children);

	}

	/**
	 * Method Name: refreshDataForFollowUpFetchInCOMP Method Description:This
	 * method is used to populate the child status , goals,placement details for
	 * each child.
	 * 
	 * @param followUp
	 *            - The dto will hold the follow-up details.
	 * @param actionPlan
	 *            - The dto will hold the action plan details.
	 * @param idStage
	 *            - The current stage id.
	 * @param idCase
	 *            - The current case id.
	 */
	private void refreshDataForFollowUpFetchInCOMP(PRTActPlanFollowUpDto followUp, PRTActionPlanDto actionPlan,
			Long idStage, Long idCase) {
		// --- Populate Follow Up Status
		// Source (Latest) Action Plan Follow Up
		Long idLatestFollowUp = followUp.getIdSrcPrtActplnFollowup();
		PRTActPlanFollowUpDto latestFollowUp = new PRTActPlanFollowUpDto();
		if (idLatestFollowUp != ServiceConstants.ZERO_VAL && idLatestFollowUp != null) {
			latestFollowUp = fetchPrevFollowUp(idLatestFollowUp, idStage, idCase);
		}

		List<PRTPersonLinkDto> children = followUp.getChildren();
		for (PRTPersonLinkDto child : children) {
			// Get the Child Record for Most Recent FollowUp (Only if there is
			// followUp)
			PRTPersonLinkDto latestFollowUpChild = prtActPlanFollowUpUtil.getFollowUpChildRec(latestFollowUp,
					child.getIdPerson());

			// Populate Child's Follow Up Status
			prtActPlanFollowUpUtil.populateChildFollowUpStatusInCOMP(idLatestFollowUp, latestFollowUpChild, child,
					followUp);
		}

		// --- Populate Goals, Placement etc...

		List<PRTPersonLinkDto> followUpChildren = followUp.getChildren();
		// Retrieve Placement and Child Plan for each Child.
		prtActionPlanDataFetchDao.selectAndPopulatePlcmtUsingIdPlcmt(followUpChildren);

		// Goals - Fetch PRT Goals.
		for (PRTPersonLinkDto child : followUpChildren) {
			List<PRTPermGoalDto> prtGoals = prtDao.selectPRTPermGoals(child.getIdPrtPersonLink());
			child.setPrtPermGoalValueDtoList(prtGoals);
		}

	}

	/**
	 * Method Name: fetchPrevFollowUp Method Description:This method is used to
	 * fetch the previous follow-up.
	 * 
	 * @param idFollowUp
	 *            - The id of the previous follow-up.
	 * @param idStage
	 *            - The stage id in which the follow-up was created.
	 * @param idCase
	 *            - The case id in which the follow-up was created.
	 * @return PRTActPlanFollowUpDto - The dto with the follow-up details.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PRTActPlanFollowUpDto fetchPrevFollowUp(Long idFollowUp, Long idStage, Long idCase) {
		PRTActPlanFollowUpDto prtActPlanFollowUpValueDto = prtActPlanFollowUpDao.selectActionPlanFollowUp(idFollowUp);
		// Get Follow Up Event.
		Long idPrtActplnFollowup = prtActPlanFollowUpValueDto.getIdPrtActplnFollowup();
		// Children - Fetch Children for the Action Plan.
		List<PRTPersonLinkDto> prtPersonLinkValueDtoList = prtActPlanFollowUpDao.selectPrtChildren(idPrtActplnFollowup);
		// Goals - Fetch PRT Goals.
		for (PRTPersonLinkDto child : prtPersonLinkValueDtoList) {
			// Goals - Fetch PRT Goals.
			List<PRTPermGoalDto> prtGoals = prtDao.selectPRTPermGoals(child.getIdPrtPersonLink());
			child.setPrtPermGoalValueDtoList(prtGoals);

			// Connections - Fetch PRT Connections
			List<PRTConnectionDto> prtConnections = prtActPlanFollowUpDao
					.selectPRTConnections(child.getIdPrtPersonLink(), child.getIdChildSUBStage());
			child.setPrtConnectionValueDtoList(prtConnections);
		}

		prtActPlanFollowUpValueDto.setChildren(prtPersonLinkValueDtoList);

		// Strategies and Tasks - Fecth PRT Strategies and Tasks.
		// Get strategies and tasks from PRT Tables.
		prtService.fetchStrategies(prtActPlanFollowUpValueDto, true);
		return prtActPlanFollowUpValueDto;
	}

	/**
	 * Method Name: isChildExitingPRT Method Description:This method checks if
	 * the Child is exiting PRT. if the child's legal status changed to one of
	 * the following values : Adoption Consummated Child Emancipated FPS Resp.
	 * terminated
	 * 
	 * @param idChild
	 *            - The person id of the child.
	 * @return boolean - The boolean value of true or false if the child is
	 *         exiting PRT.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isChildExitingPRT(Long idChild) {
		LegalStatusDto legalStatusDto = prtDao.fetchLatestLegalStatus(idChild);
		boolean childExitingPRT = false;
		String legalStatus = legalStatusDto.getCdLegalStatStatus();
		if (!ObjectUtils.isEmpty(legalStatus) && (CodesConstant.CLEGSTAT_090.equals(legalStatus)
				|| CodesConstant.CLEGSTAT_100.equals(legalStatus) || CodesConstant.CLEGSTAT_120.equals(legalStatus))) {
			childExitingPRT = true;
		}

		return childExitingPRT;
	}

	/**
	 * Method Name: validateAddNewFollowUp Method Description:This method is
	 * used to validate if a new PRT Follow-Up can be created.
	 * 
	 * @param prtActPlanFollowUpReq
	 *            - The dto holds the input paramter value - idCase, idStage,
	 *            idEvent
	 * @return List<String> - The dto holds the list of errors if present.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<Integer> validateAddNewFollowUp(PRTActplanFollowUpReq prtActPlanFollowUpReq) {
		List<Integer> errorList = new ArrayList<>();
		Long idStage = prtActPlanFollowUpReq.getIdStage();
		Long idCase = prtActPlanFollowUpReq.getIdCase();
		Long idPrimaryChild = null;
		Long idOpenActionPlan = null;
		Long idPrtActplnFollowup = null;
		boolean indValidateFurther = true;
		// Fetch the action plan in 'COMP' status for the current stage.
		Long idActionPlan = prtActPlanFollowUpDao.fetchActionPlanInCOMP(idStage);
		/*
		 * If there is no action plan in complete status then show error message
		 * on the screen.
		 */
		if (idActionPlan == 0) {
			errorList.add(Messages.MSG_PRT_FLU_NO_ACTPLN);
			indValidateFurther = false;
		}
		if (indValidateFurther) {
			idPrimaryChild = stagePersonDao.getPrimaryClientIdForStage(idStage);
			// An Action Plan Follow-Up cannot be added if PRT is closed.
			idOpenActionPlan = prtActionPlanDataFetchDao.fetchOpenActionPlan(idPrimaryChild);
			if (idOpenActionPlan == 0) {
				errorList.add(Messages.MSG_PRT_FLU_NOACTV_ACTPLN);
				indValidateFurther = false;
			}
		}

		if (indValidateFurther) {
			// Fetching the latest follow-up for the open action plan for a
			// child in 'PROC'
			// status.
			idPrtActplnFollowup = prtActPlanFollowUpDao.selectLatestFollowUp(idOpenActionPlan,
					CodesConstant.CEVTSTAT_PROC);
			/*
			 * If a follow-up exists in 'PROC' status for the child , then show
			 * error message on the screen.
			 */
			if (0L != idPrtActplnFollowup) {
				errorList.add(Messages.MSG_PRT_FLU_DUPLICATE);
				indValidateFurther = false;
			}
		}
		/*
		 * If the Child already Exited PRT, and the User is trying to Add
		 * FollowUP from that Child's SUB Stage display ERROR.
		 */ if (indValidateFurther) {

			Long idLatestFollowupInCOMP = prtActPlanFollowUpDao.selectLatestFollowUpForStage(idOpenActionPlan, idStage,
					CodesConstant.CEVTSTAT_COMP);
			if (indValidateFurther && 0 != idLatestFollowupInCOMP) {
				PRTActPlanFollowUpDto prtActPlanFollowUpDto = fetchPrevFollowUp(idLatestFollowupInCOMP, idStage,
						idCase);
				for (PRTPersonLinkDto child : prtActPlanFollowUpDto.getChildren()) {
					if (child.getIdPerson().equals(idPrimaryChild) && !ObjectUtils.isEmpty(child.getDtPrtExit())) {
						errorList.add(Messages.MSG_APFU_EXITEDPRT);
						break;
					}
				}
			}
			/*
			 * If the any goal is not valid for a child then show error message
			 * on the child.
			 */
			List<PRTPersonLinkDto> children = prtActionPlanDao.selectPrtChildren(idOpenActionPlan);
			refreshPlacementAndChildPlan(idActionPlan, children);
			boolean result = children.stream()
					.anyMatch(child -> (ObjectUtils.isEmpty(child.getDtPrtExit()) && !validateChildPlanGoals(child)));
			if (result) {
				errorList.add(Messages.MSG_PRT_NO_CHILDPLAN_GOAL);
			}
		}
		return errorList;
	}

	/**
	 * Method Name: validateChildPlanGoals Method Description:This method is
	 * used to validate the goals for each child in the PRT Action Plan.
	 * 
	 * @param child
	 *            - This dto holds the child information.
	 * @return isValid - The boolean value to indicate if the child has valid
	 *         goals or not.
	 */
	private boolean validateChildPlanGoals(PRTPersonLinkDto child) {
		boolean isValid = true;

		String cpPrimaryGoal = getPrimaryGoalForChild(child);
		String cpConcurrentGoals = getConcurrentGoalsForChild(child);
		/*
		 * If the primary or seconday goal is not valid then show error on the
		 * screen.
		 */
		if (ObjectUtils.isEmpty(cpPrimaryGoal)
				|| ((ObjectUtils.isEmpty(cpConcurrentGoals) && !ServiceConstants.Y.equals(child.getIndNoConGoal()))
						&& !CodesConstant.CCPPLNTP_ADP.equals(child.getChildPlanType()))) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Method Name: getConcurrentGoalsForChild Method Description:This method is
	 * used to get the concurrent goals for a child.
	 * 
	 * @param prtPersonLinkDto
	 *            - The dto with the child details.
	 * @return concurrentGoals - The string with the concurrent goals for a
	 *         child
	 */
	private String getConcurrentGoalsForChild(PRTPersonLinkDto child) {
		StringBuilder concurrentGoals = new StringBuilder();
		// Goals.
		for (PRTPermGoalDto goal : child.getPrtPermGoalValueDtoList()) {
			/*
			 * Iterating over the list of goals for a child and concatenating
			 * the concurrent goals.
			 */
			if (CodesConstant.CPRMGLTY_20.equals(goal.getCdType())) {
				String goalDecode = goal.getCdGoal();
				concurrentGoals.append(goalDecode).append(",");
			}
		}
		// Remove Last comma..
		if (concurrentGoals.length() > 0) {
			concurrentGoals.setLength(concurrentGoals.length() - 1);
		}
		// returning the concurrent goal string.
		return concurrentGoals.toString();
	}

	/**
	 * Method Name: getPrimaryGoalForChild Method Description:This method is
	 * used to get the primary goal for a child in PRT Action Plan.
	 * 
	 * @param prtPersonLinkDto
	 *            - The dto with the child details.
	 * @return primaryGoal - The primary goal for the child.
	 */
	private String getPrimaryGoalForChild(PRTPersonLinkDto child) {
		String primaryGoal = getPrimaryGoalForChildCode(child);

		return primaryGoal;
	}

	/**
	 * Method Name: getPrimaryGoalForChildCode Method Description:This method is
	 * used to get the primary goal code for a child.
	 * 
	 * @param prtPersonLinkDto
	 *            - The dto with the child details.
	 * @return primaryGoal - The code of primary goal for the child.
	 */
	private String getPrimaryGoalForChildCode(PRTPersonLinkDto child) {
		String primaryGoal = ServiceConstants.EMPTY_STRING;
		List<PRTPermGoalDto> currentChildPlanGoals = child.getPrtPermGoalValueDtoList();
		if (!CollectionUtils.isEmpty(currentChildPlanGoals)) {
			// Get the primary goal for a child
			primaryGoal = currentChildPlanGoals.stream()
					.filter(goal -> CodesConstant.CPRMGLTY_10.equals(goal.getCdType())).findFirst().get().getCdGoal();
		}
		// returning the primary goal.
		return primaryGoal;

	}

	/**
	 * Method Name: refreshPlacementAndChildPlan Method Description:This method
	 * is used to populate the latest placement and goals.
	 * 
	 * @param idActionPlan
	 *            - The id for the action plan.
	 * @param children
	 *            - The list of children in the PRT Action Plan.
	 */
	private void refreshPlacementAndChildPlan(Long idActionPlan, List<PRTPersonLinkDto> children) {
		// First Get the Stage Ids for Action Plan.
		List<Long> stageIdList = prtActionPlanDao.getStageIdsForActPlan(idActionPlan);

		/*
		 * Calling the dao implementation to populate the placement information
		 * and the goals for each child.
		 */
		prtActionPlanDataFetchDao.selectAndPopulateLatestPlacement(children);
		prtActionPlanDataFetchDao.fetchAndPopulateLatestChildPlans(stageIdList, children);
	}

	/**
	 * Method Name: getConnCount Method Description:This method is used to
	 * calculate the number of connections.
	 * 
	 * @param prtActplanFollowUpReq
	 *            - The dto will hold the input parameter values for calculating
	 *            the number of connections.
	 * @param followUp
	 *            - The dto will hold the PRT Follow-Up details.
	 */

	private void getConnCount(PRTActplanFollowUpReq prtActplanFollowUpReq, PRTActPlanFollowUpDto followUp) {

		Long idCurrentEvent = prtActplanFollowUpReq.getIdEvent();
		Long idActPlan = followUp.getIdPrtActionPlan();
		Long cntActPlnConn = 0l;
		Long cntActPlnFollowupConn = 0l;
		Long connAddToActPln = 0l;
		boolean isAddConn = false;
		List<PRTPersonLinkDto> children = followUp.getChildren();
		// Iterating over the list of children
		for (PRTPersonLinkDto child : children) {
			Long idPerson = child.getIdPerson();
			// Getting the idEvent for the Action Plan.
			Long idActPlanEvent = prtActPlanFollowUpDao.getActPlanEventId(idActPlan);
			PRTActplanFollowUpReq request = new PRTActplanFollowUpReq();
			request.setIdEvent(idActPlanEvent);
			request.setIdActionPlan(idActPlan);
			request.setIdPerson(idPerson);
			// Getting the count of connections in completed Action Plan.
			cntActPlnConn = prtActPlanFollowUpDao.getPRTConnInCompActPln(request);
			request.setIdEvent(idCurrentEvent);
			request.setIdFollowUp(followUp.getIdPrtActplnFollowup());
			// Getting the count of connections in completed Follow-Up
			cntActPlnFollowupConn = prtActPlanFollowUpDao.getPRTConnInCompActPlnFollowup(request);
			if (cntActPlnFollowupConn == 0) {
				isAddConn = false;
			} else if (cntActPlnFollowupConn > 0 && cntActPlnFollowupConn > cntActPlnConn) {
				isAddConn = true;
			}
			// Calculating the additional connections from Action Plan to
			// Follow-Up.
			connAddToActPln = cntActPlnFollowupConn - cntActPlnConn;
			// Setting the number of additional connections.
			child.setNbrOfConnAdd(connAddToActPln);

		}
		followUp.setIndAddConn(isAddConn);
	}

	/**
	 * Method Name: getFollowUpType Method Description:This method is used to
	 * create the follow-Up type list.
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - This dto is used to hold the input paramter values for
	 *            creating the follow-up type list.
	 * @return List - The list of options to be excluded in the follow-up type
	 *         drop-down.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<String> getFollowUpTypeExcludeOptions(PRTActPlanFollowUpDto prtActPlanFollowUpDto) {
		/*
		 * Calling the method to check if all the children in the Action Plan
		 * are exiting.
		 */
		boolean allChildrenExitingCare = allChildrenExitingPRT(prtActPlanFollowUpDto);
		List<String> followUpTypeExcludeOptions = new ArrayList<>();
		if (!CollectionUtils.isEmpty(prtActPlanFollowUpDto.getChildren())) {
			for (PRTPersonLinkDto prtPersonLinkDto : prtActPlanFollowUpDto.getChildren()) {
				// Checking if the child is exiting PRT.
				boolean childExitingPRT = isChildExitingPRT(prtPersonLinkDto.getIdPerson());
				if (prtPersonLinkDto
						.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT
						&& !childExitingPRT) {
					allChildrenExitingCare = false;
					break;
				}
			}
			/*
			 * If all children have exited PRT , then remove all the follow-up
			 * type options except 'Closing Summary'
			 */
			if (allChildrenExitingCare) {
				followUpTypeExcludeOptions.add(CodesConstant.CPRTFLTY_10);
				followUpTypeExcludeOptions.add(CodesConstant.CPRTFLTY_20);
				followUpTypeExcludeOptions.add(CodesConstant.CPRTFLTY_30);
				if (!ObjectUtils.isEmpty(prtActPlanFollowUpDto.getCdType())) {
					followUpTypeExcludeOptions.remove(prtActPlanFollowUpDto.getCdType());
				}
			}
		}
		return followUpTypeExcludeOptions;
	}

	/**
	 * Method Name: allChildrenExitingPRT Method Description:This method is used
	 * to check if all the children in the PRT Action Plan are exiting.
	 * 
	 * @param prtActPlanFollowUpDto
	 *            -The dto will hold the PRT Follow-Up details.
	 * @return allChildrenExitingCare - boolean value to indicate if all the
	 *         children are exiting the PRT.
	 */
	private boolean allChildrenExitingPRT(PRTActPlanFollowUpDto prtActPlanFollowUpDto) {
		boolean allChildrenExitingCare = true;

		// If All the children in the PRT is either exiting the care or already
		// exited the care, then show only Closing Summary as follow up type.
		if (!CollectionUtils.isEmpty(prtActPlanFollowUpDto.getChildren())) {
			/*
			 * Filtering the child list to find a child who does not have child
			 * status as 'APFU_AFTER_CHILDEXITEDPRT' or
			 * 'APFU_INWHICH_CHILDEXITEDPRT'
			 */
			boolean childNotExiting = prtActPlanFollowUpDto.getChildren().stream()
					.filter(child -> (child
							.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT
							&& child.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT))
					.findAny().isPresent();
			if (childNotExiting) {
				allChildrenExitingCare = false;
			}

		}
		// returning the indicator.
		return allChildrenExitingCare;
	}

	/**
	 * Method Name: savePRTFollowUp Method Description:This method is used to
	 * save the PRT Follow-Up details.
	 * 
	 * @param PRTActPlanFollowUpDto
	 *            -The dto will hold the PRT Follow-Up details to be
	 *            saved/updated.
	 * @return Long - The id of the record saved/updated PRT Follow-Up details.
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long savePRTFollowUp(PRTActPlanFollowUpDto prtActPlanFollowUpDto) {

		Long idEvent = prtActPlanFollowUpDto.getFollowUpEvent().getIdEvent();

		// New Event
		if (ObjectUtils.isEmpty(idEvent) || (!ObjectUtils.isEmpty(idEvent) && idEvent.equals(0l))) {
			// Insert New Action Plan and Related Records.
			idEvent = createNewFollowUp(prtActPlanFollowUpDto);
		}
		// Existing event - update
		else {
			idEvent = updateActPlanFollowUp(prtActPlanFollowUpDto);
		}
		// return the saved/updated id event.
		return idEvent;
	}

	/**
	 * Method Name: updateActPlanFollowUp Method Description:This method is used
	 * to update the PRT Follow-Up details.
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - The dto will hold the PRT Follow-Up details.
	 * @return idEvent - The idEvent of the PRT Follow-Up.
	 */
	private Long updateActPlanFollowUp(PRTActPlanFollowUpDto prtActPlanFollowUpDto) {
		// Update PRT Action Plan Follow-Up Record.
		if (prtActPlanFollowUpDto.isSaveNComplete()) {
			prtActPlanFollowUpDto.setDtComplete(new Date());
		}
		prtActPlanFollowUpDao.updateActPlanFollowUp(prtActPlanFollowUpDto);

		// PRT Children.
		List<PRTPersonLinkDto> prtPersonList = prtActPlanFollowUpDto.getChildren();
		for (PRTPersonLinkDto prtChild : prtPersonList) {
			// If Child already Exited PRT, that Child's records are frozen.
			if (prtChild.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
				// update PRT_PERSON_LINK table for each Child.
				prtChild.setIdLastUpdatePerson(prtActPlanFollowUpDto.getIdLastUpdatePerson());
				prtDao.updatePRTPersonLink(prtChild);

				// Add / Delete PRT Connections.
				prtService.savePRTConnections(prtChild);

				// Update Child Plan Goals into PRT Tables.
				prtService.saveChildPlanGoals(prtChild);
			}
		}
		// If the Save And Complete is clicked then update the event .
		if (prtActPlanFollowUpDto.isSaveNComplete()) {
			String eventDesc = EVNT_ACTPLN_FOLLOWUP_COMP + FormattingHelper.formatDate(new Date());
			for (PRTEventLinkDto evtLink : prtActPlanFollowUpDto.getFollowUpEvtLinks()) {
				EventDto eventDto = eventDao.getEventByid(evtLink.getIdEvent());

				updateEventStatus(eventDto, prtActPlanFollowUpDto.getIdLastUpdatePerson(), CodesConstant.CEVTSTAT_COMP,
						eventDesc);
			}
		}
		// return the saved/updated idEvent
		return prtActPlanFollowUpDto.getFollowUpEvent().getIdEvent();
	}

	/**
	 * Method Name: updateEventStatus Method Description:This method is used to
	 * update the event in the EVENT , EVENT_PERSON_LINK table
	 * 
	 * @param eventDto
	 *            - The dto will the event details of the PRT Follow-Up.
	 * @param idLastUpdatePerson
	 * @param cevtstatComp
	 *            - The event status which will be updated in the Event table.
	 * @param eventDesc
	 *            - The description which will be updated in the Event table.
	 * @return idEvent - The id of the PAF event.
	 */
	private Long updateEventStatus(EventDto eventDto, Long idLastUpdatePerson, String cevtstatComp, String eventDesc) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		BeanUtils.copyProperties(eventDto, postEventIPDto);
		// Setting the values in the dto.
		if (StringHelper.isValid(cevtstatComp)) {
			postEventIPDto.setCdEventStatus(cevtstatComp);
		}
		if (StringHelper.isValid(eventDesc)) {
			postEventIPDto.setEventDescr(eventDesc);
		}
		if (ObjectUtils.isEmpty(idLastUpdatePerson) && 0 != idLastUpdatePerson) {
			postEventIPDto.setIdPerson(idLastUpdatePerson);
		}
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		// Call PostEvent Service CCMN01U
		// PostEventRes response = eventService.postEvent(postEventReq);

		PostEventOPDto response = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);

		return response.getIdEvent();
	}

	/**
	 * Method Name: createNewFollowUp Method Description:This method is used to
	 * call the dao implementation which will insert data into the respective
	 * tables for creating the PRT Follow-Up.
	 * 
	 * @param prtActPlanFollowUpDto
	 * @return
	 */
	private Long createNewFollowUp(PRTActPlanFollowUpDto prtActPlanFollowUpDto) {
		Long idCurrentEvent = 0l;

		// For Save and Complete, set Date Complete.
		if (prtActPlanFollowUpDto.isSaveNComplete()) {
			prtActPlanFollowUpDto.setDtComplete(new Date());
		}
		// Create PRT Action Plan Follow Up Record.
		Long idPrtActplnFollowup = prtActPlanFollowUpDao.insertActPlanFollowUp(prtActPlanFollowUpDto);

		// - PRT Action Plan Follow Up Event - PRT Action Plan Follow Up should
		// be
		// visible from the
		// SUB Stages of all the Children in the PRT.
		// Creating New Event for each Child in the Action Plan Follow Up (For
		// Each SUB
		// Stage).
		List<PRTPersonLinkDto> prtPersonList = prtActPlanFollowUpDto.getChildren();
		if (!CollectionUtils.isEmpty(prtPersonList)) {
			for (PRTPersonLinkDto prtPersonLinkDto : prtPersonList) {
				// If Child Arealdy Exited the PRT, follow up should not be
				// visible from
				// that Chid's SUB stage, which means we should not creatae
				// Event and
				// Event Link for that Child's SUB Stage.
				if (prtPersonLinkDto
						.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
					// Create PRT Follow Up Event.
					Long idEvent = createFollowUpEvent(prtActPlanFollowUpDto, prtPersonLinkDto);
					// Set the Current Event associated with the Current Stage.
					if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdChildSUBStage())
							&& !ObjectUtils.isEmpty(prtActPlanFollowUpDto.getIdCurrentStage()) && prtPersonLinkDto
									.getIdChildSUBStage().equals(prtActPlanFollowUpDto.getIdCurrentStage())) {
						idCurrentEvent = idEvent;
					}

					// Create entry into PRT_EVENT_LINK Table.
					PRTEventLinkDto prtEventLinkDto = new PRTEventLinkDto();
					prtEventLinkDto.setIdEvent(idEvent);
					prtEventLinkDto.setIdPrtActplnFollowup(idPrtActplnFollowup);
					prtEventLinkDto.setIdCreatedPerson(prtPersonLinkDto.getIdCreatedPerson());
					prtEventLinkDto.setIdLastUpdatePerson(prtPersonLinkDto.getIdCreatedPerson());
					prtDao.insertPrtEventLink(prtEventLinkDto);

					// Create entry into PRT_PERSON_LINK table for each Child.
					prtPersonLinkDto.setIdPrtActplnFollowup(idPrtActplnFollowup);
					Long idPrtPersonLink = prtDao.insertPRTPersonLink(prtPersonLinkDto);
					prtPersonLinkDto.setIdPrtPersonLink(idPrtPersonLink);

					// Add / Delete PRT Connections.
					prtService.savePRTConnections(prtPersonLinkDto);

					// Create copy of Child Plan Perm Goals for each Child.
					// We need to make a copy of the Child Plan Goals here
					// because we need to
					// get latest Child Plan Goals irrespective of Event Status.
					// So there is
					// a possibility that the Child Plan Goals could be modified
					// after the
					// Action Plan Follow Up is frozen. Saving the goals here
					// helps us to
					// freeze that information at the time of Action Plan
					// Completion.

					// Child Plan Goals are already pulled on the retrive, so we
					// need to save
					// the goals that are in the actionPlan Object.
					prtService.saveChildPlanGoals(prtPersonLinkDto);
				}
			}
			// Save new strategies and tasks for the follow up
			prtActPlanFollowUpDto.setIdPrtActplnFollowup(idPrtActplnFollowup);
			savePRTStrategiesAndTasks(prtActPlanFollowUpDto);
		}
		// returning the idEvent of the PAF event for the current stage.
		return idCurrentEvent;
	}

	/**
	 * Method Name: createFollowUpEvent Method Description:This method is used
	 * to create a new Event. This method is used to call service implementation
	 * to create a new Event , Event Person Link
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - The dto will hold the input paramter values.
	 * @param prtPersonLinkDto
	 *            - The dto will hold the child details.
	 * @return Long - The id of the event newly created.
	 */
	private Long createFollowUpEvent(PRTActPlanFollowUpDto prtActPlanFollowUpDto, PRTPersonLinkDto prtPersonLinkDto) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		PostEventDto postEventDto = new PostEventDto();
		postEventIPDto.setIdPerson(prtPersonLinkDto.getIdCreatedPerson());
		postEventIPDto.setIdCase(prtActPlanFollowUpDto.getIdCurrentCase());
		postEventIPDto.setIdStage(prtPersonLinkDto.getIdChildSUBStage());
		postEventIPDto.setCdEventType(CodesConstant.CEVNTTYP_PAF);
		postEventIPDto.setCdTask(prtActPlanFollowUpDto.getTaskCode());
		// If the Action is Save and Complete, Event Status will be COMP
		// for all the event associated with all the children in different
		// SUB stages in the PRT.
		String eventStatus = CodesConstant.CEVTSTAT_PROC;
		String eventDesc = EVNT_ACTPLN_FOLLOWUP_INPROGRESS;
		if (prtActPlanFollowUpDto.isSaveNComplete()) {
			eventStatus = CodesConstant.CEVTSTAT_COMP;
			eventDesc = EVNT_ACTPLN_FOLLOWUP_COMP + FormattingHelper.formatDate(new Date());
		}
		postEventIPDto.setEventDescr(eventDesc);
		postEventIPDto.setCdEventStatus(eventStatus);

		List<PostEventDto> postEventPersonList = new ArrayList<PostEventDto>();
		postEventDto.setIdPerson(prtPersonLinkDto.getIdPerson());
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
		postEventPersonList.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventPersonList);
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		// Call PostEvent Service CCMN01U
		// PostEventRes response = eventService.postEvent(postEventReq);

		PostEventOPDto response = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		// returning the idEvent of the PAF event.
		return response.getIdEvent();
	}

	/**
	 * Method Name: getFollowUpDetails Method Description:This method is used to
	 * determine the values for a particular Follow-Up type.
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - This dto will hold the input paramter values for fetching
	 *            the PRT Follow-Up details for a particular PRT Follow-Up Type.
	 * @return PRTActPlanFollowUpDto - This dto will hold the PRT Follow-Up
	 *         details specific to Follow-Up Type.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PRTActPlanFollowUpDto getFollowUpDetails(PRTActPlanFollowUpDto prtActPlanFollowUpDto) {
		// Reset Exit Date and Exit Status based on the user selection of Follow
		// Up
		// Type.
		String cdType = prtActPlanFollowUpDto.getCdType();
		if (!CollectionUtils.isEmpty(prtActPlanFollowUpDto.getChildren())) {
			for (PRTPersonLinkDto prtPersonLinkDto : prtActPlanFollowUpDto.getChildren()) {
				prtPersonLinkDto.setDtPrtExit(null);
				if (prtPersonLinkDto
						.getFollowUpStatus() != PRTPersonLinkDto.ChildFollowUpStatus.APFU_AFTER_CHILDEXITEDPRT) {
					if (CodesConstant.CPRTFLTY_40.equals(cdType)) {
						// All the Children Exiting the Care.
						prtPersonLinkDto
								.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT);
						Date legStatDate = prtService.getExitLegalStatusDate(prtPersonLinkDto.getIdPerson());
						prtPersonLinkDto.setDtPrtExit(ObjectUtils.isEmpty(legStatDate) ? new Date() : legStatDate);
					} else {
						boolean childExitingPRT = isChildExitingPRT(prtPersonLinkDto.getIdPerson());
						// Get Latest Follow Up for Action Plan
						Long idLatestFollowUp = prtActPlanFollowUpDao.selectLatestFollowUp(
								prtActPlanFollowUpDto.getIdPrtActionPlan(), CodesConstant.CEVTSTAT_COMP);
						if (childExitingPRT) {
							prtPersonLinkDto.setFollowUpStatus(
									PRTPersonLinkDto.ChildFollowUpStatus.APFU_INWHICH_CHILDEXITEDPRT);
						} else if (idLatestFollowUp != 0) {
							// Subsequent Follow-Ups.
							prtPersonLinkDto.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.SUBSEQUENT_APFU);
							prtPersonLinkDto.setCdExitReason(ServiceConstants.EMPTY_STRING);
							prtPersonLinkDto.setIdPrtPermStatusLookup(0l);
						} else {
							// First Follow Up
							prtPersonLinkDto.setFollowUpStatus(PRTPersonLinkDto.ChildFollowUpStatus.FIRST_APFU);
							prtPersonLinkDto.setCdExitReason(ServiceConstants.EMPTY_STRING);
							prtPersonLinkDto.setIdPrtPermStatusLookup(0l);
						}
					}
				}
			}
		}
		// If Not BiAnnual Summary, set Did the PD participate in the bi-annual
		// Follow-Up? to Null.
		if (!CodesConstant.CPRTFLTY_30.equals(cdType)) {
			prtActPlanFollowUpDto.setIndPdParticipBiannual(ServiceConstants.EMPTY_STRING);
		}
		return prtActPlanFollowUpDto;
	}

	/**
	 * Method Name: savePRTStrategiesAndTasks Method Description:This method is
	 * used to save the strategies and the tasks related to the strategies. The
	 * service implementations calls the dao implementation which will insert
	 * data into PRT_STRATEGY,PRT_TASK,PRT_TASK_PERSON_LINK tables.
	 * 
	 * @param prtActPlanFollowUpDto
	 *            - The dto will hold the follow-up ,strategy and task related
	 *            information.
	 */
	@Override
	public void savePRTStrategiesAndTasks(PRTActPlanFollowUpDto prtActPlanFollowUpDto) {
		List<PRTTaskDto> taskList = null;
		List<PRTTaskPersonLinkDto> taskPersonLinkList = null;
		Long idPrtTask = 0l;
		List<PRTStrategyDto> strategyList = prtActPlanFollowUpDto.getStrategies();
		if (!CollectionUtils.isEmpty(strategyList)) {
			for (PRTStrategyDto prtStrategyDto : strategyList) {
				prtStrategyDto.setIdPrtActplnFollowup(prtActPlanFollowUpDto.getIdPrtActplnFollowup());
				prtStrategyDto.setIdSrcPrtStrategy(prtStrategyDto.getIdPrtStrategy());
				prtStrategyDto.setIdPrtStrategy(0l);

				prtStrategyDto.setIdCreatedPerson(prtActPlanFollowUpDto.getIdCreatedPerson());
				prtStrategyDto.setIdLastUpdatePerson(prtActPlanFollowUpDto.getIdLastUpdatePerson());

				// Nullify ID action plan
				prtStrategyDto.setIdPrtActionPlan(0l);
				// Inserting the strategy.
				Long idStrategy = prtDao.insertPrtStrategy(prtStrategyDto);

				// Get NON completed previous tasks from previous strategy
				taskList = prtStrategyDto.getPrtTaskValueDtoList();
				if (!CollectionUtils.isEmpty(taskList)) {
					for (PRTTaskDto task : taskList) {
						task.setIdPrtStrategy(idStrategy);
						task.setIdCreatedPerson(prtStrategyDto.getIdCreatedPerson());
						task.setIdLastUpdatePerson(prtStrategyDto.getIdLastUpdatePerson());
						task.setIdPrtSrcTask(task.getIdPrtTask());

						if (!ObjectUtils.isEmpty(task.getIdPrtTask()) && 0l != task.getIdPrtTask()) {
							task.setIdPrtParentTask(
									(!ObjectUtils.isEmpty(task.getIdPrtParentTask()) && 0l != task.getIdPrtParentTask())
											? task.getIdPrtParentTask() : task.getIdPrtTask());
						}
						task.setIdPrtTask(0l);

						taskPersonLinkList = task.getPrtTaskPersonLinkValueDtoList();
						// Insert the task
						idPrtTask = prtDao.insertPrtTask(task);
						Long newPrtPersonLink = 0l;
						for (PRTTaskPersonLinkDto taskPersonLink : taskPersonLinkList) {
							taskPersonLink.setIdCreatedPerson(prtStrategyDto.getIdCreatedPerson());
							taskPersonLink.setIdLastUpdatePerson(prtStrategyDto.getIdLastUpdatePerson());
							taskPersonLink.setIdPrtTask(idPrtTask);

							newPrtPersonLink = prtDao.getNewIDPRTPersonLink(prtActPlanFollowUpDto.getIdPrtActionPlan(),
									prtActPlanFollowUpDto.getIdSrcPrtActplnFollowup(),
									prtActPlanFollowUpDto.getIdPrtActplnFollowup(),
									taskPersonLink.getIdPrtPersonLink());
							taskPersonLink.setIdPrtPersonLink(newPrtPersonLink);
							// Inserting the TaskPersonLink
							prtDao.insertTaskPersonLink(taskPersonLink);
						}
					}
				}
			}
		}
	}

	@Override
	public Date getTimeStamp(CommonHelperReq commonHelperReq) {
		Date dtLastUpdate = prtActionPlanDao.getTimeStamp(commonHelperReq);
		return dtLastUpdate;
	}

}
