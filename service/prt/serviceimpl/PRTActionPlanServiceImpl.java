/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 28, 2018- 3:31:05 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.prt.serviceimpl;

import java.rmi.RemoteException;
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
import us.tx.state.dfps.phoneticsearch.IIRHelper.StringHelper;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PRTActionPlanReq;
import us.tx.state.dfps.service.common.request.PRTParticipantReq;
import us.tx.state.dfps.service.common.response.PRTActionPlanRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.eventutility.service.EventUtilityService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PRTActionPlanPrefillData;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDao;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDataFetchDao;
import us.tx.state.dfps.service.prt.dao.PRTDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.service.PRTActionPlanService;
import us.tx.state.dfps.service.prt.utils.PRTActionPlanUtils;
import us.tx.state.dfps.service.subcare.dto.PRTActionPlanDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 28, 2018- 3:31:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PRTActionPlanServiceImpl implements PRTActionPlanService {

	private static final Logger log = Logger.getLogger("ServiceBusiness-PRTActionPlanServicelog");

	@Autowired
	private PRTActionPlanDao pRTActionPlanDao;

	@Autowired
	private CaseUtils caseUtils;

	@Autowired
	private PRTActionPlanDataFetchDao dataFetchPlanDao;

	@Autowired
	private PRTActionPlanUtils pRTActionPlanUtils;

	@Autowired
	private EventUtilityService eventUtilityService;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private PRTActionPlanPrefillData actionPlanPrefill;

	@Autowired
	PRTDao prtDao;

	@Autowired
	PostEventService postEventService;

	/**
	 * Method Name: fetchActionPlan Method Description:This method retrieves all
	 * PRT Action Plan Details.
	 *
	 * @param idActionPlanEvent
	 *            the id action plan event
	 * @param idStage
	 *            the id stage
	 * @param idCase
	 *            the id case
	 * @return PRTActionPlanDto @ the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PRTActionPlanDto fetchActionPlan(Long idActionPlanEvent, Long idStage, Long idCase) {
		PRTActionPlanDto prtActionPlanDto = new PRTActionPlanDto();
		// Action Plan Event.
		if (ServiceConstants.ZERO_VAL.equals(idActionPlanEvent)) {
			prtActionPlanDto = initiateNewActionPlan(idStage, idCase);
		} else {
			// Action Plan - Retrieve Action Plan using Event.
			prtActionPlanDto = pRTActionPlanDao.selectActionPlanUsingEventId(idActionPlanEvent);
			// Action Plan Event - Retrieve Action Plan Event.
			EventValueDto eventValueDto = eventUtilityService.fetchEventInfo(idActionPlanEvent);
			// eventValueDto = null;
			prtActionPlanDto.setEventValueDto(eventValueDto);
			Long idPrtActionPlan = prtActionPlanDto.getIdPrtActionPlan();
			String eventStatus = eventValueDto.getCdEventStatus();
			// Children - Fetch Children for the Action Plan.
			List<PRTPersonLinkDto> prtPersonList = pRTActionPlanDao.selectPrtChildren(idPrtActionPlan);
			prtActionPlanDto.setChildren(prtPersonList);
			// Populate Event Id associated with the Child.
			pRTActionPlanDao.populateEventIdForChild(prtPersonList, idPrtActionPlan, ActionPlanType.ACTION_PLAN);
			// Connections - Fetch PRT Connections for Children
			fetchConnections(prtActionPlanDto);
			for (PRTPersonLinkDto child : prtActionPlanDto.getChildren()) {
				List<PRTPermGoalDto> prtGoals = prtDao.selectPRTPermGoals(child.getIdPrtPersonLink());
				child.setPrtPermGoalValueDtoList(prtGoals);
			}
			// Strategies / Tasks - Fetch PRT Strategy.
			fetchStrategies(prtActionPlanDto);
			// Fetch Prt Event List - All the Events for the Children.
			List<PRTEventLinkDto> prtActPlnEventList = prtDao.selectPrtEventLink(idPrtActionPlan,
					ActionPlanType.ACTION_PLAN);
			prtActionPlanDto.setPrtEventLinkDto(prtActPlnEventList);
			// Participants - PRT Action Plan Participants.
			List<PRTParticipantDto> paricipantList = pRTActionPlanDao.selectPRTParticipants(idPrtActionPlan);
			prtActionPlanDto.setPrtParticipantDto(paricipantList);
			// If Event Status is PROC (COMP = PRT Action Plan Frozen),
			if (ServiceConstants.CEVTSTAT_PROC.equals(eventStatus)) {
				// Retrieve Latest Placement and Child Plan for each Child.
				refreshPlacementAndChildPlan(prtActionPlanDto);
				// Fetch Unit Number of the Primary Worker of the Stage.
				dataFetchPlanDao.fetchAndPopulatePRUnit(prtActionPlanDto, idStage);
			}
			// If Event Status is COMP.
			else if (ServiceConstants.CEVTSTAT_COMP.equals(eventStatus)) {
				// Retrieve Placement and Child Plan for each Child.
				dataFetchPlanDao.selectAndPopulatePlcmtUsingIdPlcmt(prtActionPlanDto.getChildren());

				// Fetch Unit Number of the Primary Worker of the Stage.
				if (!ObjectUtils.isEmpty(prtActionPlanDto.getIdUnitWorker())
						&& 0 != prtActionPlanDto.getIdUnitWorker()) {
					dataFetchPlanDao.fetchAndPopulatePRUnitUsingIdUnit(prtActionPlanDto);
				}
			}
		}
		return prtActionPlanDto;
	}

	/**
	 * This method rows from PRT_PARTICIPANT table.
	 * 
	 * @param idPrtActionPlan
	 * 
	 * @return paricipantList or null if record not found.
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<PRTParticipantDto> selectPRTParticipants(Long idPrtActionPlan) {
		// Participants - PRT Action Plan Participants.
		List<PRTParticipantDto> paricipantList = pRTActionPlanDao.selectPRTParticipants(idPrtActionPlan);
		return paricipantList;
	}

	/**
	 * This method will be called to initiate New Action Plan. It loads all the
	 * required information to setup New Action Plan like All the Open Sub
	 * Stages in the given Case. Latest Placements for each Child in all the
	 * Open Sub Stages. Latest Child Plan Goals for each Child in all the Open
	 * Sub Stages.
	 *
	 * @param idStage
	 *            the id stage
	 * @param idCase
	 *            the id case
	 * @return PRTActionPlanDto
	 */
	private PRTActionPlanDto initiateNewActionPlan(Long idStage, Long idCase) {
		PRTActionPlanDto prtActionPlanDto = new PRTActionPlanDto();
		// Get All the Open SUB stages in the current Case.
		List<StageDto> openSubStages = caseUtils.getOpenSUBStages(idCase);
		// Get the List of Stage Ids.
		List<Long> stageIdList = new ArrayList<Long>();
		for (StageDto StageDto : openSubStages) {
			stageIdList.add(StageDto.getIdStage());
		}
		// Get the Primary Child for all the Open Stages.
		List<PRTPersonLinkDto> linkValueDtos = dataFetchPlanDao.selectPCForStagesWithPMCLegalStatus(stageIdList);
		if (linkValueDtos.size() > ServiceConstants.ZERO_VAL) {
			// Get Latest Placements for the given stages.
			dataFetchPlanDao.selectAndPopulateLatestPlacement(linkValueDtos);
			// Get Latest Child Plan for the given stages and populate
			// Children.
			dataFetchPlanDao.fetchAndPopulateLatestChildPlans(stageIdList, linkValueDtos);
		}
		// Fetch Unit Number of the Primary Worker of the Stage.
		dataFetchPlanDao.fetchAndPopulatePRUnit(prtActionPlanDto, idStage);
		prtActionPlanDto.setChildren(linkValueDtos);
		return prtActionPlanDto;
	}

	/**
	 * This method retrieves all PRT Connections.
	 * 
	 * If Action Plan Event is in COMP status or If PRT Exit Date Exists for the
	 * Child. - Get Connections only from PRT_CONNECTIONS table. Else - First
	 * Get Connections from PRT_CONNECTIONS table. - Then Get People from Stage
	 * Person Link. - Merge both of the above. If the Record is coming from
	 * connections table, checkbox will be selected on the screen. End If
	 *
	 * @param actionPlan
	 *            the action plan
	 */
	private void fetchConnections(PRTActionPlanDto actionPlan) {

		String eventStatus = (!TypeConvUtil.isNullOrEmpty(actionPlan.getEventValueDto()))
				? actionPlan.getEventValueDto().getCdEventStatus() : "";
		// Get Person List.
		List<PRTPersonLinkDto> prtPersonList = actionPlan.getChildren();
		for (PRTPersonLinkDto child : prtPersonList) {
			// If PRT Exit Date Exists for the Child.
			if (ServiceConstants.CEVTSTAT_COMP.equals(eventStatus) || !DateUtils.isNull(child.getDtPrtExit())) {
				// Gets only from PRT_CONNECTIONS table.
				List<PRTConnectionDto> prtConnections = pRTActionPlanDao
						.selectPRTConnections(child.getIdPrtPersonLink(), child.getIdChildSUBStage());
				child.setPrtConnectionValueDtoList(prtConnections);
			} else {
				// First Gets from PRT_CONNECTIONS table.
				List<PRTConnectionDto> prtConnections = pRTActionPlanDao
						.selectPRTConnections(child.getIdPrtPersonLink(), child.getIdChildSUBStage());
				// Then Get People from Stage Person Link.
				List<PRTConnectionDto> connFromSpl = dataFetchPlanDao
						.fetchConnectionsForStage(child.getIdChildSUBStage());
				// Merge both of the above.
				for (PRTConnectionDto conn : connFromSpl) {
					if (!pRTActionPlanUtils.isPersonInConnList(prtConnections, conn.getIdPerson())) {
						prtConnections.add(conn);
					}
				}
				child.setPrtConnectionValueDtoList(prtConnections);
			}
		}

	}

	/**
	 * This method retrieves all PRT Strategies and Tasks.
	 *
	 * @param actionPlan
	 *            the action plan
	 */
	private void fetchStrategies(PRTActionPlanDto actionPlan) {

		Long idPrtActionPlan = actionPlan.getIdPrtActionPlan();
		// Fetch PRT Strategy.
		List<PRTStrategyDto> strategyList = prtDao.selectPrtStrategy(idPrtActionPlan, ActionPlanType.ACTION_PLAN);
		// Fetch Tasks associated with Strategy.
		for (PRTStrategyDto strategy : strategyList) {
			List<PRTTaskDto> prtTasks = prtDao.selectPrtTasks(strategy.getIdPrtStrategy(), true);
			// Fetch Children Associated with Tasks.
			for (PRTTaskDto prtTask : prtTasks) {
				List<PRTTaskPersonLinkDto> childrenForTask = prtDao.selectChildrenForTask(prtTask.getIdPrtTask());
				prtTask.setPrtTaskPersonLinkValueDtoList(childrenForTask);
			}
			strategy.setPrtTaskValueDtoList(prtTasks);
		}
		actionPlan.setPrtStrategiesDto(strategyList);
	}

	/**
	 * This method loads Latest Placement and Child Plan for all the kids in the
	 * PRT.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return PRTActionPlanValueDto
	 */
	private void refreshPlacementAndChildPlan(PRTActionPlanDto actionPlan) {

		// Get the List of Stage Ids.
		List<Long> stageIdList;
		stageIdList = getStageIdsForActPlan(actionPlan.getIdPrtActionPlan());
		if (stageIdList.size() > 0) {
			// Get the Children for the Action Plan
			List<PRTPersonLinkDto> children = actionPlan.getChildren();
			// Get Latest Placements for the given stages.
			dataFetchPlanDao.selectAndPopulateLatestPlacement(children);
			// Get Latest Child Plan for the given stages and populate
			// Children.
			dataFetchPlanDao.fetchAndPopulateLatestChildPlans(stageIdList, children);
		}
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<Long> getStageIdsForActPlan(Long idPrtActionPlan) {
		log.debug("Entering method getStageIdsForActPlan in PRTActionPlanService");
		{
			// Get All the Records from PRT Event Link Table.
			List<PRTEventLinkDto> prtActPlnEventList = pRTActionPlanDao.selectPrtEventLinkWithIdStage(idPrtActionPlan,
					ActionPlanType.ACTION_PLAN);

			// Get the List of Stage Ids.
			List<Long> stageIdList = new ArrayList<Long>();

			for (PRTEventLinkDto evtLink : prtActPlnEventList) {
				stageIdList.add(evtLink.getIdStage());
			}

			log.debug("Exiting method getStageIdsForActPlan in PRTActionPlanService");
			return stageIdList;
		}
	}

	/**
	 * Method Name: validateAddActionPlan Method Description:This method
	 * validates before creating new Action Plan.
	 *
	 * @param idStage
	 *            the id stage
	 * @param idCase
	 *            the id case
	 * @return List<Long> @ the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<Long> validateAddActionPlan(Long idStage, Long idCase) {

		List<Long> errors = new ArrayList<Long>();
		try {
			// Get All the Open SUB stages in the current Case.
			List<StageDto> openSubStages = caseUtils.getOpenSUBStages(idCase);
			List<PRTPersonLinkDto> children = null;
			List<Long> stageIdList = new ArrayList<Long>();
			for (StageDto stageDto : openSubStages) {
				stageIdList.add(stageDto.getIdStage());
			}

			// Get the Primary Child for all the Open Stages.
			if (!ObjectUtils.isEmpty(stageIdList)) {
				children = dataFetchPlanDao.selectPCForStagesWithPMCLegalStatus(stageIdList);
			}
			if (ObjectUtils.isEmpty(children) || children.size() == ServiceConstants.ZERO_VAL) {
				errors.add(ServiceConstants.MSG_PRT_LEGSTAT_NOTPMC);
			} else {
				// Check if the Primary Child of the Current Stage has PMC.
				boolean pcHasPMC = ServiceConstants.MOBILE_IMPACT;
				Long idPrimaryChild = caseUtils.getPrimaryClientIdForStage(idStage);
				for (PRTPersonLinkDto child : children) {
					if (child.getIdPerson().equals(idPrimaryChild)) {
						pcHasPMC = ServiceConstants.SERVER_IMPACT;
						break;
					}
				}
				if (ServiceConstants.MOBILE_IMPACT == pcHasPMC) {
					errors.add(ServiceConstants.MSG_PRT_LEGSTAT_NOTPMC);
					return errors;
				}
				// Get Latest Child Plan for the given stages and populate
				// Children.
				dataFetchPlanDao.fetchAndPopulateLatestChildPlans(stageIdList, children);
				for (PRTPersonLinkDto child : children) {
					// The child is in another "active" PRT:
					// (i.e. "active" means not in an Action Plan that is in
					// COMP status and
					// has an associated Action Plan Follow-Up with type =
					// Closing Summary)
					Long idOpenActionPlan = dataFetchPlanDao.fetchOpenActionPlan(child.getIdPerson());

					if (!ServiceConstants.ZERO_VAL.equals(idOpenActionPlan)) {
						errors.add(ServiceConstants.MSG_PRT_CHILD_IN_OPENPRT);
					}
					// Child can not be in any Action Plan with PROC status.
					Long idProcActionPlan = dataFetchPlanDao.fetchActionPlanforPerson(child.getIdPerson(),
							ServiceConstants.CEVTSTAT_PROC);
					if (!ServiceConstants.ZERO_VAL.equals(idProcActionPlan)) {
						errors.add(ServiceConstants.MSG_PRT_CHILD_IN_OPENPRT);
					}
					// The child has a CPOS in PROC, COMP, PEND or APRV status.
					// � The child's CPOS has a primary goal
					// � The child's CPOS has a concurrent goal
					// CPOS of the Adoption Type does not have an option for
					// Concurrent Goals or the
					// No Concurrent Goals checkbox.
					if (pRTActionPlanUtils.validateChildPlanGoals(child) == ServiceConstants.MOBILE_IMPACT) {
						errors.add(ServiceConstants.MSG_PRT_NO_CHILDPLAN_GOAL);
					}
				}
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return errors;

	}

	/**
	 * Method Name: fetchPrtPermStatusLookup Method Description:This method is
	 * used to get the list of Prem Status Definition List
	 * 
	 * @ the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<PRTPermStatusLookupDto> fetchPrtPermStatusLookup() {
		List<PRTPermStatusLookupDto> permStatusList = pRTActionPlanDao.selectPrtPermStatusLookup();
		log.debug("Entering method fetchPrtPermStatusLookup in PRTActionPlanService");
		return permStatusList;
	}

	/**
	 * Method Name: deleteActionPlan Method Description:This method is used to
	 * delete the PRT Action Plan
	 * 
	 * @ the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long deleteActionPlan(Long idEvent, Long idStage, Long idCase) {
		// Get Action Plan.
		PRTActionPlanDto actionPlan = new PRTActionPlanDto();
		actionPlan = fetchActionPlan(idEvent, idStage, idCase);

		if (!ObjectUtils.isEmpty(actionPlan) && !ObjectUtils.isEmpty(actionPlan.getIdPrtActionPlan())) {

			Long actionPlanId = actionPlan.getIdPrtActionPlan();
			// Delete Participants.
			pRTActionPlanDao.deleteParticipantsForActPlan(actionPlanId);

			// Delete Person Link and Related Tables.
			List<PRTPersonLinkDto> childrens = actionPlan.getChildren();
			if (!CollectionUtils.isEmpty(childrens)) {
				for (PRTPersonLinkDto child : childrens) {
					// Delete Goals.
					pRTActionPlanDao.deletePrtGoalsForChild(child.getIdPrtPersonLink());
					// Delete Connections.
					pRTActionPlanDao.deletePrtConnections(child.getIdPrtPersonLink());
				}
			}
			// Delete Strategies.
			List<PRTStrategyDto> strategyList = actionPlan.getPrtStrategiesDto();
			if (!CollectionUtils.isEmpty(strategyList)) {
				// For Each Strategy
				for (PRTStrategyDto strategy : strategyList) {
					pRTActionPlanDao.deletePrtStrategy(strategy.getIdPrtStrategy());
				}
			}

			// Delete Person Link
			pRTActionPlanDao.deletePersonLinkForActPlan(actionPlanId);

			// Delete PRT Event Link.
			pRTActionPlanDao.deletePrtEventLink(actionPlanId);

			// Delete PRT Event.
			List<PRTEventLinkDto> eventList = actionPlan.getPrtEventLinkDto();
			if (eventList != null) {
				for (PRTEventLinkDto evt : eventList) {
					pRTActionPlanDao.deletePrtEvent(evt.getIdEvent());
				}
			}

			// delete PRT Action Plan
			return pRTActionPlanDao.deleteActionPlan(actionPlanId);
		}
		return 0L;
	}

	/**
	 * This method saves (insert or update) PRT Action Plan into the database.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return idCurrentEvent @ the service exception
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long saveActionPlan(PRTActionPlanDto actionPlan) {
		Long idCurrentEvent = 0l;
		if (!ObjectUtils.isEmpty(actionPlan.getEventValueDto())) {
			idCurrentEvent = actionPlan.getEventValueDto().getIdEvent();
		}

		if (ObjectUtils.isEmpty(actionPlan.getIdPrtActionPlan())) // Insert
		{
			// Insert New Action Plan and Related Records.
			idCurrentEvent = createNewActionPlan(actionPlan);
		} else // Update
		{
			updateActionPlan(actionPlan);
		}

		return idCurrentEvent;
	}

	/**
	 * This method creates new PRT Action Plan along with the Details. Following
	 * Records will be inserts for PRT Action Plan - PRT Action Plan. - PRT
	 * Action Plan Event for each Child. - PRT Event Link entry for each Child
	 * to link PRT Action Plan to the new Event. - PRT Person Link entry for
	 * each Child in Action Plan. - All PRT Connections for each Child. - All
	 * PRT Goals (Child Plan Goals) for each Child. - All PRT Strategies
	 * associated with Action Plan. - Tasks associated with each strategy. -
	 * Task Person Link entry for all each child associated with the Task. - PRT
	 * Participants.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return idCurrentEvent @ the service exception
	 */
	private Long createNewActionPlan(PRTActionPlanDto actionPlan) {
		Long idCurrentEvent = 0l;

		// Create PRT Action Plan Record.
		long idPrtActionPlan = pRTActionPlanDao.insertActionPlan(actionPlan);

		// - PRT Action Plan Event - PRT Action Plan should be visible from the
		// SUB Stages of all the Children in the PRT.
		// Creating New Event for each Child in the Action Plan (For Each SUB
		// Stage).
		List<PRTPersonLinkDto> prtPersonList = actionPlan.getChildren();
		if (prtPersonList != null) {
			for (PRTPersonLinkDto prtChild : prtPersonList) {
				// Save the Child Record only if selected by User.
				if (prtChild.isCheckedByUser()) {
					Long idChildSUBStage = prtChild.getIdChildSUBStage();
					// Create PRT Event.
					Long idEvent = createPRTActionPlanEvent(actionPlan, prtChild, idChildSUBStage);
					// Set the Current Event associated with the Current Stage.
					if (idChildSUBStage.equals(actionPlan.getIdCurrentStage())) {
						idCurrentEvent = idEvent;
					}

					// Create entry into PRT_EVENT_LINK Table.
					PRTEventLinkDto prtEvtLink = new PRTEventLinkDto();
					prtEvtLink.setIdEvent(idEvent);
					prtEvtLink.setIdPrtActionPlan(idPrtActionPlan);
					prtEvtLink.setIdCreatedPerson(prtChild.getIdCreatedPerson());
					prtEvtLink.setIdLastUpdatePerson(prtChild.getIdCreatedPerson());

					// dtCreated
					prtEvtLink.setDtCreated(new Date());
					prtEvtLink.setDtLastUpdate(new Date());

					prtDao.insertPrtEventLink(prtEvtLink);

					// Create entry into PRT_PERSON_LINK table for each Child.
					prtChild.setIdPrtActionPlan(idPrtActionPlan);

					long idPrtPersonLink = prtDao.insertPRTPersonLink(prtChild);

					prtChild.setIdPrtPersonLink(idPrtPersonLink);

					// Create copy of Child Plan Perm Goals for each Child.
					// We need to make a copy of the Child Plan Goals here
					// because we need to
					// get the latest Child Plan Goals irrespective of Event
					// Status. So there is
					// a possibility that the Child Plan Goals could be modified
					// after the
					// Action Plan is frozen. Saving the goals here helps us to
					// freeze that
					// information at the time of Action Plan Completion.

					// Child Plan Goals are already pulled on the retrive, so we
					// need to save
					// the goals that are in the actionPlan Object.
					saveChildPlanGoals(prtChild);
				}
			}
		}
		return idCurrentEvent;
	}

	/**
	 * This method create PRT Action Plan Event.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @param prtChild
	 *            the prt child
	 * @param idChildSUBStage
	 *            the id child SUB stage
	 * @return idEvent @ the service exception
	 */
	private Long createPRTActionPlanEvent(PRTActionPlanDto actionPlan, PRTPersonLinkDto prtChild,
			long idChildSUBStage) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		PostEventDto postEventDto = new PostEventDto();
		postEventIPDto.setIdEvent(0l);
		postEventIPDto.setDtEventOccurred(new Date());
		postEventIPDto.setTsLastUpdate(new Date());
		postEventIPDto.setIdPerson(prtChild.getIdCreatedPerson());
		postEventIPDto.setIdCase(actionPlan.getIdCurrentCase());
		postEventIPDto.setIdStage(idChildSUBStage);
		postEventIPDto.setEventDescr(ServiceConstants.APPLICATION_PROC_EVENT_DESC);
		postEventIPDto.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
		postEventIPDto.setCdEventType(ServiceConstants.EVENT_TYPE_PAP);
		postEventIPDto.setCdTask(actionPlan.getTaskCode());
		ServiceReqHeaderDto serviceReqHeaderDto= new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		List<PostEventDto> postEventPersonList = new ArrayList<PostEventDto>();
		postEventDto.setIdPerson(prtChild.getIdPerson());
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
		postEventPersonList.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventPersonList);
		PostEventOPDto postEventOPDto = new PostEventOPDto();
		postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		Long idEvent = postEventOPDto.getIdEvent();
		return idEvent;
	}

	/**
	 * This method updates Latest Child Plan Goals into PRT Tables.
	 * 
	 * Create copy of Child Plan Perm Goals for each Child. We need to make a
	 * copy of the Child Plan Goals here because we need to get the latest Child
	 * Plan Goals irrespective of Event Status. So there is a possibility that
	 * the Child Plan Goals could be modified after the Action Plan is frozen.
	 * Saving the goals here helps us to freeze that information at the time of
	 * Action Plan Completion.
	 *
	 * @param prtChild
	 *            the prt child @ the service exception
	 */
	private void saveChildPlanGoals(PRTPersonLinkDto prtChild) {

		// Create copy of Child Plan Perm Goals for each Child.
		// We need to make a copy of the Child Plan Goals here because we need
		// to
		// get the latest Child Plan Goals irrespective of Event Status. So
		// there is
		// a possibility that the Child Plan Goals could be modified after the
		// Action Plan is frozen. Saving the goals here helps us to freeze that
		// information at the time of Action Plan Completion.

		// Requirement is to get the latest goals every time user retrieves the
		// Action Plan. So the following code deletes all the Goals and
		// re-inserts them on every save.

		// Delete all the Goals for the Child.
		prtDao.deletePrtGoalsForChild(prtChild.getIdPrtPersonLink());

		// Child Plan Goals are already pulled on the retrieve, so we need to
		// save
		// the goals that are in the actionPlan Object.
		List<PRTPermGoalDto> cpGoals = prtChild.getPrtPermGoalValueDtoList();
		if (!CollectionUtils.isEmpty(cpGoals)) {
			for (PRTPermGoalDto goal : cpGoals) {
				// Insert Child Plan Goals into PRT Table.
				goal.setIdPrtPersonLink(prtChild.getIdPrtPersonLink());
				goal.setIdCreatedPerson(prtChild.getIdCreatedPerson());
				goal.setIdLastUpdatePerson(prtChild.getIdCreatedPerson());
				prtDao.insertPRTPermGoals(goal);
				log.info("PRT Perm Goal Inserted");
			}
		}

	}

	/**
	 * This method updates PRT Action Plan along with the Details. - PRT Action
	 * Plan. - PRT Action Plan Event for each Child. - PRT Event Link entry for
	 * each Child to link PRT Action Plan to the new Event. - PRT Person Link
	 * entry for each Child in Action Plan. - All PRT Connections for each
	 * Child. - All PRT Goals (Child Plan Goals) for each Child. - All PRT
	 * Strategies associated with Action Plan. - Tasks associated with each
	 * strategy. - Task Person Link entry for all each child associated with the
	 * Task. - PRT Participants.
	 *
	 * @param actionPlan
	 *            the action plan
	 * @return idPrtActionPlan @ the service exception
	 */
	private void updateActionPlan(PRTActionPlanDto actionPlan) {

		// Update PRT Action Plan Record.
		if (actionPlan.isSaveNComplete()) {
			actionPlan.setDtComplete(new Date());
		}
		pRTActionPlanDao.updateActionPlan(actionPlan);

		// PRT Children.
		List<PRTPersonLinkDto> prtPersonList = actionPlan.getChildren();
		if (!CollectionUtils.isEmpty(prtPersonList)) {
			for (PRTPersonLinkDto prtChild : prtPersonList) {
				// If PRT Exit Date is present for a Child, that Child's records
				// are frozen.
				if (DateUtils.isNull(prtChild.getDtPrtExit())) {
					// update PRT_PERSON_LINK table for each Child.
					prtChild.setIdLastUpdatePerson(actionPlan.getIdLastUpdatePerson());

					// To Do PRTPersonLinkValueDto to PrtPersonLink
					prtDao.updatePRTPersonLink(prtChild);

					// Add / Delete PRT Connections.
					savePRTConnections(prtChild);

					// Update Child Plan Goals into PRT Tables.
					saveChildPlanGoals(prtChild);

					// Every time the Action Plan is updated, delete all the
					// goals
					// and insert them again.
					for (PRTParticipantDto participant : actionPlan.getPrtParticipantDto()) {
						pRTActionPlanDao.updatePRTParticipants(participant);
					}
				}
			}
		}

		// Strategies and Tasks are on new screen. So there is no need to update
		// them
		// here.

		// Add Staff and Delete Staff buttons will take care of Adding and
		// deleting the
		// participants.
		// We only need to update PRT Role and Action Plan Agreement for the
		// selected
		// participants here.

		// If the Action is Save and Complete, Change the Event Status to COMP
		// for all the event associated with all the children in different
		// SUB stages in the PRT.
		if (actionPlan.isSaveNComplete()) {
			EventDto event = null;
			String eventDesc = ServiceConstants.EVNTDESC_ACTPLN_COMP + new Date();
			for (PRTEventLinkDto evtLink : actionPlan.getPrtEventLinkDto()) {
				event = eventDao.getEventByid(evtLink.getIdEvent());
				updateEventStatus(event, actionPlan.getIdLastUpdatePerson(), CodesConstant.CEVTSTAT_COMP, eventDesc);
			}
		}
	}

	/**
	 * This method Adds / Deletes PRT Connections based on user selection.
	 *
	 * @param prtChild
	 *            the prt child @ the service exception
	 */
	private void savePRTConnections(PRTPersonLinkDto prtChild) {

		List<Long> deleteIdConnList = new ArrayList<Long>();

		for (PRTConnectionDto prtConn : prtChild.getPrtConnectionValueDtoList()) {
			// Selected by User and Not Already a connection.
			if (prtConn.isSelectedByUser() && ObjectUtils.isEmpty(prtConn.getIdPrtConnection())) {
				prtConn.setIdPrtPersonLink(prtChild.getIdPrtPersonLink());
				prtConn.setIdCreatedPerson(prtChild.getIdLastUpdatePerson());
				prtConn.setIdLastUpdatePerson(prtChild.getIdLastUpdatePerson());
				prtDao.insertPrtConnection(prtConn);

			} else if (!prtConn.isSelectedByUser() && !ObjectUtils.isEmpty(prtConn.getIdPrtConnection())) {
				deleteIdConnList.add(prtConn.getIdPrtConnection());
			}
		}

		// Delete and Insert PRT Connections for each Child.
		if (deleteIdConnList.size() > 0) {
			prtDao.deletePrtConnections(deleteIdConnList);
		}
	}

	/**
	 * This is helper function to update Event Status by calling PostEventBean.
	 *
	 * @param evt
	 *            the evt
	 * @param idUpdatePerson
	 *            the id update person
	 * @param evtStatus
	 *            the evt status
	 * @param eventDesc
	 *            the event desc @ the service exception
	 */
	private Long updateEventStatus(EventDto eventDto, Long idUpdatePerson, String evtStatus, String eventDesc) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		BeanUtils.copyProperties(eventDto, postEventIPDto);
		if (StringHelper.isValid(evtStatus)) {
			postEventIPDto.setCdEventStatus(evtStatus);
		}
		if (StringHelper.isValid(eventDesc)) {
			postEventIPDto.setEventDescr(eventDesc);
		}
		if (ObjectUtils.isEmpty(idUpdatePerson) && 0 != idUpdatePerson) {
			postEventIPDto.setIdPerson(idUpdatePerson);
		}
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		PostEventOPDto response = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		return response.getIdEvent();

	}

	/**
	 * Method Name: updatePRTContactToPersLink Method Description: This method
	 * update PRTContactToPersLink.
	 *
	 * @param pRTActionPlanReq
	 *            the RT action plan req
	 * @return the long @ the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long updatePRTContactToPersLink(PRTActionPlanReq pRTActionPlanReq) {
		// Update PRT Action Plan ContactEvent to PRT_PERSON_LINK

		Long updateCount = pRTActionPlanDao.updatePRTContactToPersLink(pRTActionPlanReq.getpRTPersonLinkValueDto());

		return updateCount;
	}

	/**
	 * This method retrieves all PRT Action Plan Details.
	 * 
	 * @param idActionPlanEvent
	 * @param idStage
	 * @param idCase
	 * 
	 * @return PRTActionPlanValueBean
	 * 
	 * @throws RemoteException
	 */
	@Override
	public PRTActionPlanDto fetchActionPlan(Long idPrtActionPlan) {
		PRTActionPlanDto actionPlan = new PRTActionPlanDto();

		// Action Plan - Retrieve Action Plan using Action Plan ID.
		actionPlan = pRTActionPlanDao.selectActionPlan(idPrtActionPlan);

		// Children - Fetch Children for the Action Plan.
		List<PRTPersonLinkDto> prtPersonList = pRTActionPlanDao.selectPrtChildren(idPrtActionPlan);
		actionPlan.setChildren(prtPersonList);

		// Populate Event Id associated with the Child.
		pRTActionPlanDao.populateEventIdForChild(prtPersonList, idPrtActionPlan, ActionPlanType.ACTION_PLAN);

		// Connections - Fetch PRT Connections for Children
		fetchConnections(actionPlan);

		// Goals - Fetch PRT Goals.
		for (PRTPersonLinkDto child : actionPlan.getChildren()) {
			List<PRTPermGoalDto> prtGoals = prtDao.selectPRTPermGoals(child.getIdPrtPersonLink());
			child.setPrtPermGoalValueDtoList(prtGoals);
		}

		// Strategies / Tasks - Fetch PRT Strategy.
		fetchStrategies(actionPlan);

		// Fetch Prt Event List - All the Events for the Children.
		List<PRTEventLinkDto> prtActPlnEventList = prtDao.selectPrtEventLink(idPrtActionPlan,
				ActionPlanType.ACTION_PLAN);
		actionPlan.setPrtEventLinkDto(prtActPlnEventList);

		// Participants - PRT Action Plan Participants.
		List<PRTParticipantDto> paricipantList = pRTActionPlanDao.selectPRTParticipants(idPrtActionPlan);
		actionPlan.setPrtParticipantDto(paricipantList);

		// Fetch Unit Number of the Primary Worker of the Stage.
		if (!ObjectUtils.isEmpty(actionPlan.getIdUnitWorker()) && actionPlan.getIdUnitWorker() != 0) {
			dataFetchPlanDao.fetchAndPopulatePRUnitUsingIdUnit(actionPlan);
		}

		return actionPlan;
	}

	/**
	 * This method used to insert the PRTParticipant into DB.
	 * 
	 * @param participantReq
	 * @return count @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long insertPRTParticipant(PRTParticipantReq participantReq) {

		log.debug("Entering method insertPRTParticipant in PRTActionPlanService");
		Long count = null;
		try {
			count = pRTActionPlanDao.insertPRTParticipant(participantReq.getPrtParticipantDto());
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		log.debug("Exiting method insertPRTParticipant in PRTActionPlanService");
		return count;
	}

	/**
	 * This method used to delete the PRTParticipant into DB.
	 * 
	 * @param participantReq
	 * @return count @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long deletePRTParticipant(Long idPrtParticipant) {
		log.debug("Entering method deletePRTParticipant in PRTActionPlanService");
		{
			log.debug("Exiting method deletePRTParticipant in PRTActionPlanService");
			return pRTActionPlanDao.deletePrtParticipant(idPrtParticipant);

		}
	}

	/**
	 * This method used to fetch the list of PRTPersonLinkDto for given stages.
	 * 
	 * @param stageIdList
	 * @param children
	 * @return count
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<PRTPersonLinkDto> fetchAndPopulateLatestChildPlans(List<Long> stageIdList,
			List<PRTPersonLinkDto> children) {
		log.debug("Exiting method fetchAndPopulateLatestChildPlans in PRTActionPlanService");
		return dataFetchPlanDao.fetchAndPopulateLatestChildPlans(stageIdList, children);

	}

	/**
	 * This method used to fetch the list of PRTPersonLinkDto for given stages.
	 * 
	 * @param idPerson
	 * @param children
	 * @return legalStatusDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public LegalStatusDto fetchLatestLegalStatus(Long idPerson) {
		log.debug("Entering method fetchLatestLegalStatus in PRTActionPlanService");
		LegalStatusDto legalStatusDto = dataFetchPlanDao.fetchLatestLegalStatus(idPerson);
		// This service method requires only the "cdLegalStatStatus" field.
		// Since DAO returns dtLegalStatStatusDt as well, we remove it from the
		// DTO
		// before returning to controller
		legalStatusDto.setDtLegalStatStatusDt(null);
		log.debug("Exiting method fetchLatestLegalStatus in PRTActionPlanService");
		return legalStatusDto;
	}

	/**
	 * This method used to fetch the list of PRTPersonLinkDto for given stages.
	 * 
	 * @param idPerson
	 * @return pRTActionPlanRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PRTActionPlanRes fetchOpenActionPlan(Long idPerson) {
		log.debug("Entering method fetchOpenActionPlan in PRTActionPlanService");
		Long idActionPlan = 0l;
		PRTActionPlanRes pRTActionPlanRes = new PRTActionPlanRes();
		idActionPlan = dataFetchPlanDao.fetchOpenActionPlan(idPerson);
		pRTActionPlanRes.setIdPrtActionPlan(idActionPlan);
		log.debug("Exiting method fetchOpenActionPlan in PRTActionPlanService");

		return pRTActionPlanRes;
	}

	/**
	 * Method Name: fetchActionPlan Method Description:This method retrieves all
	 * PRT Action Plan Details.
	 *
	 * @param idActionPlanEvent
	 *            the id action plan event
	 * @param idStage
	 *            the id stage
	 * @param idCase
	 *            the id case
	 * @return PRTActionPlanDto @ the service exception
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto displayActionPlanForm(Long idActionPlanEvent, Long idStage, Long idCase) {
		PRTActionPlanDto prtActionPlanDto = new PRTActionPlanDto();
		try {
			prtActionPlanDto = fetchActionPlan(idActionPlanEvent, idStage, idCase);
			prtActionPlanDto.setIdCurrentCase(idCase);
			prtActionPlanDto.setIdCurrentEvent(idActionPlanEvent);
			prtActionPlanDto.setIdCurrentStage(idStage);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return actionPlanPrefill.returnPrefillData(prtActionPlanDto);
	}

	/**
	 * Method Name: deletePRTStrategy Method Description:This method is used to
	 * delete the PRT Strategy and Tasks related to the Strategy.This method
	 * calls the dao implementation to delete from
	 * PRT_STRATEGY,PRT_TASK_PERSON_LINK,PRT_TASK tables.
	 * 
	 * @param prtActplanFollowUpReq
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void deletePRTStrategy(PRTActionPlanReq pRTActionPlanReq) {
		prtDao.deletePRTStrategy(pRTActionPlanReq.getIdStrategy());

	}

	@Override
	public Date getTimeStamp(CommonHelperReq commonHelperReq) {
		Date dtLastUpdate = pRTActionPlanDao.getTimeStamp(commonHelperReq);
		return dtLastUpdate;
	}
}
