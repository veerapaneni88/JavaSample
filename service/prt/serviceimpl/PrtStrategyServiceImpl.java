package us.tx.state.dfps.service.prt.serviceimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.baseprtsession.dto.PRTTaskPersonLinkValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.response.PrtStrategyRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.prt.dao.PRTActPlanFollowUpDao;
import us.tx.state.dfps.service.prt.dao.PRTDao;
import us.tx.state.dfps.service.prt.dao.PrtStrategyDao;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyValueDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskValueDto;
import us.tx.state.dfps.service.prt.service.PrtStrategyService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePRTSessionServiceImpl for BasePRTSession Oct 6, 2017- 3:03:42 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PrtStrategyServiceImpl implements PrtStrategyService {
	@Autowired
	private PrtStrategyDao prtStrategyDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-BasePRTSessionServiceLog");

	@Autowired
	PRTActPlanFollowUpDao prtActPlanFollowUpDao;

	@Autowired
	PRTDao prtDao;

	/**
	 * method name: insertPRTTask Description: This method saves PRT Task.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long insertPRTTask(List<PRTTaskValueDto> taskList) {
		Long result = ServiceConstants.ZERO;
		Long idPrtTask = 0l;
		try {
			for(PRTTaskValueDto prtTaskValueDto:taskList) {
			idPrtTask = prtStrategyDao.insertPrtTask(prtTaskValueDto);
			result = prtStrategyDao.deletePrtTaskPersonLink(idPrtTask);

			// if (newLink) {
			List<PRTTaskPersonLinkValueDto> prtTaskPersonLinkList = prtTaskValueDto.getPrtTaskPersonLinkValueDtoList();
			for (PRTTaskPersonLinkValueDto taskPersonLink : prtTaskPersonLinkList) {
				taskPersonLink.setIdPrtTask(idPrtTask);
				result += prtStrategyDao.insertTaskPersonLink(taskPersonLink);
			}
			}
			// }
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return idPrtTask;
	}

	/**
	 * method name: updatePRTTask Description: This method updates PRT Task.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updatePRTTask(PRTTaskValueDto prtTaskValueDto) {
		Long result = ServiceConstants.ZERO;
		try {
			result = prtStrategyDao.updatePrtTask(prtTaskValueDto);
			Long idPrtTask = prtTaskValueDto.getIdPrtTask();
			result += prtStrategyDao.deletePrtTaskPersonLink(idPrtTask);
			List<PRTTaskPersonLinkValueDto> prtTaskPersonLinkList = prtTaskValueDto.getPrtTaskPersonLinkValueDtoList();
			for (PRTTaskPersonLinkValueDto taskPersonLink : prtTaskPersonLinkList) {
				taskPersonLink.setIdPrtTask(idPrtTask);
				result += prtStrategyDao.insertTaskPersonLink(taskPersonLink);
			}
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return result;
	}

	/**
	 * method name: deletePRTTask Description: This method deletes PRT Task
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deletePRTTask(Long idPrtTask) {
		Long result = ServiceConstants.ZERO;
		try {
			result = prtStrategyDao.deletePrtTask(idPrtTask);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return result;
	}

	/**
	 * method name: insertPRTStrategy Description: This method saves new PRT
	 * Strategy.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long insertPRTStrategy(PRTStrategyValueDto prtStrategyValueDto) {
		Long result = ServiceConstants.ZERO;
		try {
			result = prtStrategyDao.insertPrtStrategy(prtStrategyValueDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return result;
	}

	/**
	 * method name: updatePRTStrategy Description: This method updates PRT
	 * Strategy.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updatePRTStrategy(PRTStrategyValueDto prtStrategyValueDto) {
		Long result = ServiceConstants.ZERO;
		try {
			result = prtStrategyDao.updatePrtStrategy(prtStrategyValueDto);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return result;
	}

	/**
	 * method name: deletePRTStrategy Description: This method deletes PRT
	 * Strategy and all its associated tasks.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deletePRTStrategy(Long idPrtStrategy) {
		Long result = ServiceConstants.ZERO;
		try {
			// result = prtStrategyDao.deletePrtStrategy(idPrtStrategy);
		} catch (DataNotFoundException e) {
			LOG.error(e.getMessage());
		}
		return result;
	}

	@Override
	public PrtStrategyRes fetchPRTStrategy(PRTStrategyValueDto prtStrategyValueDto) {

		PrtStrategyRes prtStrategyRes = new PrtStrategyRes();

		Long idPrtFollowup = prtStrategyValueDto.getIdPrtActplnFollowup();
		Long idPrtActionPlan= prtStrategyValueDto.getIdPrtActionPlan();
		List<PRTTaskDto> prtTasks = null;
		List<PRTStrategyDto> strategyList = new ArrayList<>();
		// Fetch PRT Strategy.
		if (prtStrategyValueDto.getIndActionPlan() == true) {
			strategyList = prtDao.selectPrtStrategy(idPrtActionPlan, ActionPlanType.ACTION_PLAN);
		} else {
			strategyList = prtDao.selectPrtStrategy(idPrtFollowup, ActionPlanType.ACTPLN_FOLLOWUP);
		}
		// Fetch Tasks associated with Strategy.
		for (PRTStrategyDto strategy : strategyList) {
			if (prtStrategyValueDto.isIndPrevFollowUp()) {
				prtTasks = prtStrategyDao.selectPrtTasks(strategy.getIdPrtStrategy(), false);
			} else {
				prtTasks = prtStrategyDao.selectPrtTasks(strategy.getIdPrtStrategy(), true);
			}

			strategy.setPrtTaskValueDtoList(prtTasks);
			List<Long> parentIds = new ArrayList<Long>();
			// Fetch Children Associated with Tasks.
			for (PRTTaskDto prtTask : prtTasks) {
				List<PRTTaskPersonLinkDto> childrenForTask = prtDao.selectChildrenForTask(prtTask.getIdPrtTask());
				prtTask.setPrtTaskPersonLinkValueDtoList(childrenForTask);
				if (null != prtTask.getIdPrtParentTask()) {
					parentIds.add(prtTask.getIdPrtParentTask());
				}
			}

			// Get Parent Task date created
			if (parentIds.size() > 0) {
				List parentTaskDates = prtActPlanFollowUpDao.getPRTParentTaskInfo(parentIds);
				for (PRTTaskDto prtTask : prtTasks) {
					if (parentTaskDates != null && parentTaskDates.size() != 0) {
						Iterator taskDtIt = parentTaskDates.iterator();
						while (taskDtIt.hasNext()) {
							PRTTaskDto taskDt = (PRTTaskDto) taskDtIt.next();
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

		prtStrategyRes.setStrategyList(strategyList);

		return prtStrategyRes;
	}

	/**
	 * method name: fetchPrtStrategyList description: This method updates Latest
	 * Child Plan Goals into PRT Tables.
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	public PrtStrategyRes fetchPRTStrategyList(PRTStrategyValueDto prtStrategyValueDto) {

		PrtStrategyRes prtStrategyRes = new PrtStrategyRes();

		Long idPrtFollowup = prtStrategyValueDto.getIdPrtActplnFollowup();
		// Fetch PRT Strategy.
		List<PRTStrategyDto> strategyList = prtDao.selectPrtStrategy(idPrtFollowup, ActionPlanType.ACTION_PLAN);
		prtStrategyRes.setStrategyList(strategyList);

		return prtStrategyRes;
	}

	/**
	 * method name: fetchChildren description: This method fetches children
	 * associated with the PRT
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 * @throws InvalidRequestException
	 * 
	 */
	@Override
	public PrtStrategyRes fetchInitialChildList(PRTStrategyValueDto prtStrategyValueDto) {

		PrtStrategyRes prtStrategyRes = new PrtStrategyRes();

		// Long idPrtFollowup = prtStrategyValueDto.getIdPrtActplnFollowup();
		List<PRTPersonLinkDto> personLinkList = new ArrayList<>();

		if (!TypeConvUtil.isNullOrEmpty(prtStrategyValueDto.getIdPrtActplnFollowup())) {
			personLinkList = prtActPlanFollowUpDao.selectPrtChildren(prtStrategyValueDto.getIdPrtActplnFollowup());
		} else if (!TypeConvUtil.isNullOrEmpty(prtStrategyValueDto.getIdPrtActionPlan())) {
			personLinkList = prtStrategyDao.selectPrtChildren(prtStrategyValueDto.getIdPrtActionPlan());
		}

		List<PRTTaskPersonLinkDto> taskPersonList = new ArrayList<>();

		for (PRTPersonLinkDto personLink : personLinkList) {

			PRTTaskPersonLinkDto taskPerson = new PRTTaskPersonLinkDto();

			taskPerson.setIdPrtPersonLink(personLink.getIdPrtPersonLink());
			taskPerson.setIdPerson(personLink.getIdPerson());
			taskPersonList.add(taskPerson);
		}
		prtStrategyRes.setTaskPersonList(taskPersonList);

		return prtStrategyRes;
	}

}
