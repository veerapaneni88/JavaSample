package us.tx.state.dfps.service.prt.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dao.PRTActPlanFollowUpDao;
import us.tx.state.dfps.service.prt.dao.PRTActionPlanDao;
import us.tx.state.dfps.service.prt.dao.PRTDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.service.PRTService;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * provides the service implementation for the common logic for PRT Action Plan
 * and PRT Follow-Up. April 6, 2018- 3:03:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PRTServiceImpl implements PRTService {

	@Autowired
	private PRTDao prtDao;

	@Autowired
	PRTActPlanFollowUpDao prtActPlanFollowUpDao;

	@Autowired
	PRTActionPlanDao prtActionPlanDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-BasePRTSessionServiceLog");

	/**
	 * Method Name: getActPlanEventId Method Description:This method is used to
	 * retrieve the id event for the particular action plan.
	 * 
	 * @param idActionPlan
	 *            - The id of the Action Plan.
	 * @return Long - The id event for the Action Plan.
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getActPlanEventId(long idActionPlan) {

		long idActionPlanEvent = ServiceConstants.Zero;
		try {
			if (idActionPlan != ServiceConstants.Zero) {
				List<PRTEventLinkDto> prtEventLinkValueDtoList = prtDao.selectPrtEventLink(idActionPlan,
						ServiceConstants.ActionPlanType.ACTION_PLAN);
				if (prtEventLinkValueDtoList.size() > ServiceConstants.Zero) {
					idActionPlanEvent = prtEventLinkValueDtoList.get(ServiceConstants.Zero).getIdEvent();
				}
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return idActionPlanEvent;
	}

	/**
	 * Method Name: saveChildPlanGoals Method Description:This method is used to
	 * save the child goals for the PRT Plan.
	 * 
	 * @param prtPersonLinkDto
	 *            - The dto will hold the child details in PRT Action Plan.
	 */
	@Override
	public void saveChildPlanGoals(PRTPersonLinkDto prtPersonLinkDto) {

		// Requirement is to get the latest goals every time user retrieves the
		// Action Plan. So the following code deletes all the Goals and
		// re-inserts them on every save.

		// Delete all the Goals for the Child.
		prtDao.deletePrtGoalsForChild(prtPersonLinkDto.getIdPrtPersonLink());
		// Child Plan Goals are already pulled on the retrive, so we need to
		// save
		// the goals that are in the actionPlan Object.
		List<PRTPermGoalDto> cpGoals = prtPersonLinkDto.getPrtPermGoalValueDtoList();
		for (PRTPermGoalDto goal : cpGoals) {
			// Insert Child Plan Goals into PRT Table.
			goal.setIdPrtPersonLink(prtPersonLinkDto.getIdPrtPersonLink());
			goal.setIdCreatedPerson(prtPersonLinkDto.getIdCreatedPerson());
			goal.setIdLastUpdatePerson(prtPersonLinkDto.getIdCreatedPerson());
			prtDao.insertPRTPermGoals(goal);
		}
	}

	/**
	 * Method Name: savePRTConnections Method Description:This method is used to
	 * save and delete the PRT connections.
	 * 
	 * @param prtPersonLinkDto
	 *            - The dto will hold the child details in PRT Action Plan.
	 */
	@Override
	public void savePRTConnections(PRTPersonLinkDto prtPersonLinkDto) {
		List<Long> deleteIdConnList = new ArrayList<Long>();
		List<PRTConnectionDto> addPRTConnectionsList = new ArrayList<PRTConnectionDto>();
		List<PRTConnectionDto> prtConnectionDtosList = prtPersonLinkDto.getPrtConnectionValueDtoList();
		if (!CollectionUtils.isEmpty(prtConnectionDtosList)) {
			// Getting th list of connections to be deleted and added newly.
			deleteIdConnList = prtConnectionDtosList.stream()
					.filter(conn -> (!conn.isSelectedByUser()
							&& (!ObjectUtils.isEmpty(conn.getIdPrtConnection()) && conn.getIdPrtConnection() > 0l)))
					.map(PRTConnectionDto::getIdPrtConnection).collect(Collectors.toList());

			addPRTConnectionsList = prtConnectionDtosList.stream()
					.filter(conn -> (conn.isSelectedByUser() && (ObjectUtils.isEmpty(conn.getIdPrtConnection())
							|| (!ObjectUtils.isEmpty(conn.getIdPrtConnection())
									&& conn.getIdPrtConnection().equals(0l)))))
					.collect(Collectors.toList());
		}

		// Iterate over the list of connectios to be added
		if (!CollectionUtils.isEmpty(addPRTConnectionsList)) {
			addPRTConnectionsList.forEach(prtConnectionDto -> {
				prtConnectionDto.setIdPrtPersonLink(prtPersonLinkDto.getIdPrtPersonLink());
				prtConnectionDto.setIdCreatedPerson(prtPersonLinkDto.getIdCreatedPerson());
				prtConnectionDto.setIdLastUpdatePerson(prtPersonLinkDto.getIdLastUpdatePerson());
				prtDao.insertPrtConnection(prtConnectionDto);
			});

		}
		// Iterate over the list of connections to be deleted
		if (!CollectionUtils.isEmpty(deleteIdConnList)) {
			prtDao.deletePrtConnections(deleteIdConnList);
		}
	}

	/**
	 * Method Name: deletePRTStrategy Method Description:This method is used to
	 * delete the PRT Strategy and Tasks related to the Strategy.This method
	 * calls the dao implementation to delete from
	 * PRT_STRATEGY,PRT_TASK_PERSON_LINK,PRT_TASK tables.
	 * 
	 * @param prtActplanFollowUpReq
	 *            - The dto holds the input parameter for the delete operation.
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void deletePRTStrategy(PRTActplanFollowUpReq prtActplanFollowUpReq) {
		// Calling the dao implementation to delete strategy.
		prtDao.deletePRTStrategy(prtActplanFollowUpReq.getIdStrategy());

	}

	/**
	 * Method Name: fetchStrategies Method Description:This method is used to
	 * fetch the strategies for a particular Action Plan/Follow-Up.
	 * 
	 * @param followUp
	 *            - The dto hold the input parameter values for retrieving
	 *            strategies as well as the PRT Follow-Up details.
	 * @param prevFollowup
	 *            - The boolean indicator to indicate if a previous follow-up
	 *            exits.
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public void fetchStrategies(PRTActPlanFollowUpDto followUp, boolean prevFollowup) {
		Long idPrtFollowup = followUp.getIdPrtActplnFollowup();
		List<PRTTaskDto> prtTasks = new ArrayList<>();
		// Fetch PRT Strategy.
		List<PRTStrategyDto> strategyList = prtDao.selectPrtStrategy(idPrtFollowup, ActionPlanType.ACTPLN_FOLLOWUP);
		// Fetch Tasks associated with Strategy.
		for (PRTStrategyDto strategy : strategyList) {
			if (prevFollowup) {
				prtTasks = prtDao.selectPrtTasks(strategy.getIdPrtStrategy(), false);
			} else {
				prtTasks = prtDao.selectPrtTasks(strategy.getIdPrtStrategy(), true);
			}

			strategy.setPrtTaskValueDtoList(prtTasks);
			List<Long> parentIds = new ArrayList<Long>();
			// Fetch Children Associated with Tasks.
			prtTasks.forEach(prtTask -> {
				List<PRTTaskPersonLinkDto> childrenForTask = prtDao.selectChildrenForTask(prtTask.getIdPrtTask());
				prtTask.setPrtTaskPersonLinkValueDtoList(childrenForTask);
				if (!ObjectUtils.isEmpty(prtTask.getIdPrtParentTask()) && prtTask.getIdPrtParentTask() != 0) {
					parentIds.add(prtTask.getIdPrtParentTask());
				}
			});

			// Get Parent Task date created
			if (!CollectionUtils.isEmpty(parentIds)) {
				List<PRTTaskDto> parentTaskDates = prtActPlanFollowUpDao.getPRTParentTaskInfo(parentIds);
				if (!CollectionUtils.isEmpty(parentTaskDates)) {

					for (PRTTaskDto prtTask : prtTasks) {
						if (!CollectionUtils.isEmpty(parentTaskDates)) {

							for (PRTTaskDto taskDt : parentTaskDates) {

								if (!ObjectUtils.isEmpty(prtTask.getIdPrtParentTask())
										&& prtTask.getIdPrtParentTask().equals(taskDt.getIdPrtTask())) {
									prtTask.setDtCreated(taskDt.getDtCreated());
									break;
								}

							}
						}
					}
				}
			}
		}
		// setting the strategies in the follow up dto.
		followUp.setStrategies(strategyList);
	}

	/**
	 * Method Name: getExitLegalStatusDate Method Description:This method is
	 * used to get the legal status exit date for a particular child.
	 * 
	 * @param idChild
	 *            - The id Person of the child.
	 * @return Date -The exit date for the child in legal status.
	 */
	@Override
	public Date getExitLegalStatusDate(Long idChild) {
		LegalStatusDto legalStatusDto = prtDao.fetchLatestLegalStatus(idChild);
		Date legalStatusDate = null;

		String legalStatus = legalStatusDto.getCdLegalStatStatus();
		if ((CodesConstant.CLEGSTAT_090.equals(legalStatus) || CodesConstant.CLEGSTAT_100.equals(legalStatus)
				|| CodesConstant.CLEGSTAT_120.equals(legalStatus))) {
			legalStatusDate = legalStatusDto.getDtLegalStatStatusDt();
		}

		return legalStatusDate;
	}

	/**
	 * Method Name: getPermStatus Method Description:This method is used to
	 * fetch the perm status lookup definitions.
	 * 
	 * @return List - The list of Perm Status Lookup definitions.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PRTPermStatusLookupDto> getPermStatus() {
		/*
		 * Calling the dao implementation to fetch the perm status lookup
		 * definition list.
		 */
		return prtActionPlanDao.selectPrtPermStatusLookup();
	}
}
