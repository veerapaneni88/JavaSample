package us.tx.state.dfps.service.prt.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PrtActionPlan;
import us.tx.state.dfps.common.domain.PrtActplnFollowup;
import us.tx.state.dfps.common.domain.PrtPersonLink;
import us.tx.state.dfps.common.domain.PrtStrategy;
import us.tx.state.dfps.common.domain.PrtTask;
import us.tx.state.dfps.common.domain.PrtTaskPersonLink;
import us.tx.state.dfps.service.baseprtsession.dto.PRTTaskPersonLinkValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.prt.dao.PrtStrategyDao;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyValueDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskValueDto;

@Repository
public class PrtStrategyDaoImpl implements PrtStrategyDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${BasePRTSessionDaoImpl.deletePrtTaskPersonLink}")
	private String deletePrtTaskPersonLinksql;

	@Value("${BasePRTSessionDaoImpl.deletePrtTask1}")
	private String deletePrtTask1sql;

	@Value("${BasePRTSessionDaoImpl.deletePrtTask2}")
	private String deletePrtTask2sql;

	@Value("${BasePRTSessionDaoImpl.deletePrtStrategy}")
	private String deletePrtStrategysql;

	@Value("${BasePRTSessionDaoImpl.deletePrtStrategy1}")
	private String deletePrtStrategysql1;

	@Value("${BasePRTSessionDaoImpl.deletePrtStrategy2}")
	private String deletePrtStrategysql2;

	@Value("${PRTActPlanFollowUpDaoImpl.selectPrtChildrenNoStageIdPlan}")
	private String selectPrtChildrenNoStageIdSql;

	@Value("${PRTActPlanFollowUpDaoImpl.selectPrtChildrenNoStageId2Plan}")
	private String selectPrtChildrenNoStageIdSql2;

	/**
	 * @param strategy
	 * @return Long
	 */
	@Override
	public Long insertPrtStrategy(PRTStrategyValueDto strategy) {
		PrtStrategy prtStrategy = new PrtStrategy();
		prtStrategy.setIdCreatedPerson(strategy.getIdCreatedPerson());
		prtStrategy.setIdLastUpdatePerson(strategy.getIdLastUpdatePerson());
		PrtActionPlan prtActionPlan = new PrtActionPlan();

		if (!TypeConvUtil.isNullOrEmpty(strategy.getIdPrtActionPlan())) {
			prtActionPlan.setIdPrtActionPlan(strategy.getIdPrtActionPlan());
			prtStrategy.setPrtActionPlan(prtActionPlan);
		}
		PrtActplnFollowup prtActplnFollowup = new PrtActplnFollowup();
		if (!TypeConvUtil.isNullOrEmpty(strategy.getIdPrtActplnFollowup())) {
			prtActplnFollowup.setIdPrtActplnFollowup(strategy.getIdPrtActplnFollowup());
			prtStrategy.setPrtActplnFollowup(prtActplnFollowup);
		}
		prtStrategy.setCdStrategy(strategy.getCdStrategy());

		if (!TypeConvUtil.isNullOrEmpty(strategy.getTxtOtherDesc()))
			prtStrategy.setTxtOtherDesc(strategy.getTxtOtherDesc());
		prtStrategy.setDtCreated(new Date());
		if (!TypeConvUtil.isNullOrEmpty(strategy.getIdSrcPrtStrategy()))
			prtStrategy.setIdSrcPrtStrategy(strategy.getIdSrcPrtStrategy());
		prtStrategy.setDtLastUpdate(new Date());
		Long result = (Long) sessionFactory.getCurrentSession().save(prtStrategy);
		return result;

	}

	/**
	 * method name: insertPRTTask Description: This method saves PRT Task.
	 * 
	 * @param PRTTaskValueDto
	 * @return IdPrtTask
	 *
	 */
	@Override
	public Long insertPrtTask(PRTTaskValueDto prtTaskValueDto) {

		PrtTask prtTask = new PrtTask();
		BeanUtils.copyProperties(prtTaskValueDto, prtTask);
		prtTask.setIdCreatedPerson(prtTaskValueDto.getIdCreatedPerson());
		prtTask.setIdLastUpdatePerson(prtTaskValueDto.getIdLastUpdatePerson());
		PrtStrategy prtStrategy = new PrtStrategy();
		prtStrategy.setIdPrtStrategy(prtTaskValueDto.getIdPrtStrategy());
		prtTask.setPrtStrategy(prtStrategy);
		if (null != prtTaskValueDto.getIdPersonAssigned()) {
			Person person = new Person();
			person.setIdPerson(prtTaskValueDto.getIdPersonAssigned());
			prtTask.setPerson(person);
		}
		if (null != prtTaskValueDto.getTxtEliminationDetails())
			prtTask.setTxtElmntnDetails(prtTaskValueDto.getTxtEliminationDetails());
		if (null != prtTaskValueDto.getCdEliminationReason())
			prtTask.setCdElimReason(prtTaskValueDto.getCdEliminationReason());
		if (null != prtTaskValueDto.getCdAssignedToType())
			prtTask.setCdAssignedType(prtTaskValueDto.getCdAssignedToType());
		if (null != prtTaskValueDto.getCdTaskStatus() && null == prtTaskValueDto.getDtCompOrElimnated()) {

			if ("20".equals(prtTaskValueDto.getCdTaskStatus()) || "30".equals(prtTaskValueDto.getCdTaskStatus())) {
				prtTask.setDtCompOrEliminated(new Date());
			}
		}
		prtTask.setDtTargetComplete(prtTaskValueDto.getDtTargetComplete());
		prtTask.setDtCreated(new Date());
		prtTask.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(prtTask);

		return prtTask.getIdPrtTask();
	}

	/**
	 * method name: insertTaskPersonLink Description: This method saves PRT Task
	 * Person Link.
	 * 
	 * @param PRTTaskPersonLinkValueDto
	 * @return IdPrtTaskPersonLink
	 *
	 */
	@Override
	public Long insertTaskPersonLink(PRTTaskPersonLinkValueDto taskPersonLink) {
		PrtTaskPersonLink prtTaskPersonLink = new PrtTaskPersonLink();
		BeanUtils.copyProperties(taskPersonLink, prtTaskPersonLink);
		prtTaskPersonLink.setIdCreatedPerson(taskPersonLink.getIdCreatedPerson());
		prtTaskPersonLink.setIdLastUpdatePerson(taskPersonLink.getIdLastUpdatePerson());
		PrtTask prtTask = new PrtTask();
		prtTask.setIdPrtTask(taskPersonLink.getIdPrtTask());
		prtTaskPersonLink.setPrtTask(prtTask);
		PrtPersonLink prtPersonLink = new PrtPersonLink();
		prtPersonLink.setIdPrtPersonLink(taskPersonLink.getIdPrtPersonLink());
		prtTaskPersonLink.setPrtPersonLink(prtPersonLink);
		prtTaskPersonLink.setDtCreated(new Date());
		prtTaskPersonLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(prtTaskPersonLink);
		return prtTaskPersonLink.getIdPrtTaskPersonLink();
	}

	/**
	 * method name: deletePrtTaskPersonLink Description: This method deleted PRT
	 * Task.
	 * 
	 * @param idPrtTask
	 * @return null
	 *
	 */
	@Override
	public Long deletePrtTaskPersonLink(Long idPrtTask) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(deletePrtTaskPersonLinksql)
				.setParameter("idPrtTask", idPrtTask);
		return (long) query.executeUpdate();
	}

	/**
	 * method name: updatePrtTask Description: This method updates PRT Task.
	 * 
	 * @param PRTTaskValueDto
	 * @return
	 *
	 */
	@Override
	public Long updatePrtTask(PRTTaskValueDto prtTaskValueDto) {
		Long result = ServiceConstants.ZERO;
		Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(PrtTask.class);

		criteria1.add(Restrictions.eq("idPrtTask", prtTaskValueDto.getIdPrtTask()));

		List<PrtTask> prtTaskList = criteria1.list();

		for (PrtTask prtTask : prtTaskList) {
			prtTask.setIdLastUpdatePerson(prtTaskValueDto.getIdLastUpdatePerson());
			PrtStrategy prtStrategy = new PrtStrategy();
			prtStrategy.setIdPrtStrategy(prtTaskValueDto.getIdPrtStrategy());
			prtTask.setPrtStrategy(prtStrategy);
			Person person = new Person();
			person.setIdPerson(prtTaskValueDto.getIdPersonAssigned());
			prtTask.setPerson(person);
			prtTask.setCdBarrier(prtTaskValueDto.getCdBarrier());
			prtTask.setTxtPlanOvrcmBrs(prtTaskValueDto.getTxtPlanOvrcmBrs());
			prtTask.setDtTargetComplete(prtTaskValueDto.getDtTargetComplete());
			prtTask.setDtCompOrEliminated(prtTaskValueDto.getDtCompOrElimnated());
			prtTask.setIdSrcPrtTask(prtTaskValueDto.getIdPrtSrcTask());
			prtTask.setTxtDesc(prtTaskValueDto.getTxtDesc());
			prtTask.setCdAssignedType(prtTaskValueDto.getCdAssignedToType());
			prtTask.setCdTaskStatus(prtTaskValueDto.getCdTaskStatus());
			prtTask.setTxtElmntnDetails(prtTaskValueDto.getTxtEliminationDetails());
			prtTask.setTxtComments(prtTaskValueDto.getTxtComments());
			prtTask.setCdElimReason(prtTaskValueDto.getCdEliminationReason());
			sessionFactory.getCurrentSession().saveOrUpdate(prtTask);
			result++;
		}
		return result;
	}

	/**
	 * method name: deletePrtTask Description: This method deleted PRT Task.
	 * 
	 * @param idPrtTask
	 * @return null
	 *
	 */
	@Override
	public Long deletePrtTask(Long idPrtTask) {
		Long result = ServiceConstants.ZERO;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(deletePrtTask1sql)
				.setParameter("idPrtTask", idPrtTask);
		result = (long) query.executeUpdate();

		Query query1 = (Query) sessionFactory.getCurrentSession().createSQLQuery(deletePrtTask2sql)
				.setParameter("idPrtTask", idPrtTask);
		result += query1.executeUpdate();

		return result;
	}

	/**
	 * method name: updatePrtStrategy Description: This method is used to update
	 * PRT strategy
	 * 
	 * @param PRTStrategyValueDto
	 * @return null
	 *
	 */
	@Override
	public Long updatePrtStrategy(PRTStrategyValueDto prtStrategyValueDto) {
		Long result = ServiceConstants.ZERO;
		Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(PrtStrategy.class);

		criteria1.add(Restrictions.eq("idPrtStrategy", prtStrategyValueDto.getIdPrtStrategy()));

		List<PrtStrategy> prtStrategyList = criteria1.list();

		for (PrtStrategy prtStrategy : prtStrategyList) {
			prtStrategy.setIdLastUpdatePerson(prtStrategyValueDto.getIdLastUpdatePerson());
			prtStrategy.setCdStrategy(prtStrategyValueDto.getCdStrategy());
			prtStrategy.setTxtOtherDesc(prtStrategyValueDto.getTxtOtherDesc());
			sessionFactory.getCurrentSession().saveOrUpdate(prtStrategy);
			result++;
		}
		return result;
	}

	/**
	 * This method fetches single row from PRT_STRATEGY table.
	 * 
	 * @param idPrtActPlnOrFollowup
	 * @param ActionPlanType
	 * 
	 * @return List PRTStrategyDto or null if record not found.
	 * 
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTStrategyDto> selectPrtStrategy(Long idPrtActionPlan, ActionPlanType actionPlan)
			throws DataNotFoundException {
		List<PRTStrategyDto> strategyList = new ArrayList<PRTStrategyDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtStrategy.class);
		criteria.add(Restrictions.eq("prtActionPlan.idPrtActionPlan", idPrtActionPlan));
		List<PrtStrategy> strategyValueDtos = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(strategyValueDtos)) {

			for (PrtStrategy prtStrategy : strategyValueDtos) {
				PRTStrategyDto strategy = new PRTStrategyDto();

				strategy.setIdPrtStrategy(prtStrategy.getIdPrtStrategy());
				strategy.setDtCreated(prtStrategy.getDtCreated());
				strategy.setIdCreatedPerson(prtStrategy.getIdCreatedPerson());
				strategy.setDtLastUpdate(prtStrategy.getDtLastUpdate());
				strategy.setIdLastUpdatePerson(prtStrategy.getIdLastUpdatePerson());
				if (!TypeConvUtil.isNullOrEmpty(prtStrategy.getPrtActionPlan()))
					strategy.setIdPrtActionPlan(prtStrategy.getPrtActionPlan().getIdPrtActionPlan());
				if (!TypeConvUtil.isNullOrEmpty(prtStrategy.getPrtActplnFollowup()))
					strategy.setIdPrtActplnFollowup(prtStrategy.getPrtActplnFollowup().getIdPrtActplnFollowup());
				strategy.setCdStrategy(prtStrategy.getCdStrategy());
				strategy.setTxtOtherDesc(prtStrategy.getTxtOtherDesc());
				if (!TypeConvUtil.isNullOrEmpty(prtStrategy.getIdSrcPrtStrategy()))
					strategy.setIdSrcPrtStrategy(prtStrategy.getIdSrcPrtStrategy());

				strategyList.add(strategy);
			}
		}

		return strategyList;
	}

	/**
	 * This method fetches records from PRT_TASK table for the given Strategy.
	 * 
	 * @param idPrtStrategy
	 * @param current
	 * 
	 * @return List <PRTTaskDto> or null if record not found.
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTTaskDto> selectPrtTasks(Long idPrtStrategy, boolean current) {
		List<PRTTaskDto> prtTaskValueDtoList = new ArrayList<PRTTaskDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtTask.class)
				.add(Restrictions.eq("prtStrategy.idPrtStrategy", idPrtStrategy));
		if (!current) {
			criteria.add(Restrictions.eqOrIsNull("dtCompOrEliminated", null));
		}
		List<PrtTask> PrtTaskList = criteria.list();
		for (PrtTask prtTask : PrtTaskList) {

			PRTTaskDto prtTaskValueDto = new PRTTaskDto();
			BeanUtils.copyProperties(prtTask, prtTaskValueDto);
			if (null != prtTask.getCdElimReason())
				prtTaskValueDto.setCdEliminationReason(prtTask.getCdElimReason());
			if (null != prtTask.getTxtElmntnDetails())
				prtTaskValueDto.setTxtEliminationDetails(prtTask.getTxtElmntnDetails());
			if (null != prtTask.getCdAssignedType())
				prtTaskValueDto.setCdAssignedToType(prtTask.getCdAssignedType());
			if (null != prtTask.getPerson()) {
				if (null != prtTask.getPerson().getIdPerson()) {
					prtTaskValueDto.setIdPersonAssigned(prtTask.getPerson().getIdPerson());
				}
			}
			prtTaskValueDtoList.add(prtTaskValueDto);

		}

		return prtTaskValueDtoList;
	}

	/**
	 * This method fetches records from PRT_TASK_PERSON_LINK table for the given
	 * Strategy.
	 * 
	 * @param idPrtTask
	 * 
	 * @return List <PRTTaskPersonLinkDto> or null if record not found.
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTTaskPersonLinkDto> selectChildrenForTask(Long idPrtTask) {

		List<PRTTaskPersonLinkDto> childrenForTask = new ArrayList<PRTTaskPersonLinkDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtTaskPersonLink.class);
		criteria.add(Restrictions.eq("prtTask.idPrtTask", idPrtTask));
		List<PrtTaskPersonLink> taskPersonLinkValueDtos = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(taskPersonLinkValueDtos)) {

			for (PrtTaskPersonLink taskPerson : taskPersonLinkValueDtos) {
				PRTTaskPersonLinkDto childForTask = new PRTTaskPersonLinkDto();

				childForTask.setIdPrtTaskPersonLink(taskPerson.getIdPrtTaskPersonLink());
				childForTask.setDtCreated(taskPerson.getDtCreated());
				childForTask.setIdCreatedPerson(taskPerson.getIdCreatedPerson());
				childForTask.setDtLastUpdate(taskPerson.getDtLastUpdate());
				childForTask.setIdLastUpdatePerson(taskPerson.getIdLastUpdatePerson());
				childForTask.setIdPrtTask(taskPerson.getPrtTask().getIdPrtTask());
				childForTask.setIdPrtPersonLink(taskPerson.getPrtPersonLink().getIdPrtPersonLink());

				childrenForTask.add(childForTask);
			}
		}

		return childrenForTask;
	}

	/**
	 * This method fetches records from PRT_TASK_PERSON_LINK table for the given
	 * Strategy.
	 * 
	 * @param idPrtTask
	 * 
	 * @return List <PRTTaskPersonLinkDto> or null if record not found.
	 * 
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPersonLinkDto> selectInitialChildrenForTask(Long idPrtTask) {

		List<PRTPersonLinkDto> childrenForTask = new ArrayList<PRTPersonLinkDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtPersonLink.class);
		criteria.add(Restrictions.eq("prtTask.idPrtTask", idPrtTask));
		List<PrtPersonLink> taskPersonLinkValueDtos = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(taskPersonLinkValueDtos)) {

			for (PrtPersonLink taskPerson : taskPersonLinkValueDtos) {
				PRTPersonLinkDto childForTask = new PRTPersonLinkDto();

				BeanUtils.copyProperties(taskPerson, childForTask);
				childForTask.setIdPrtPersonLink(taskPerson.getIdPrtPersonLink());
				childForTask.setDtCreated(taskPerson.getDtCreated());
				childForTask.setIdCreatedPerson(taskPerson.getIdCreatedPerson());
				childForTask.setDtLastUpdate(taskPerson.getDtLastUpdate());
				childForTask.setIdLastUpdatePerson(taskPerson.getIdLastUpdatePerson());
				childrenForTask.add(childForTask);
			}
		}

		return childrenForTask;
	}

	/**
	 * method name: selectPrtChildren description: This method fetches children
	 * associated with the PRT
	 * 
	 * @param basePRTSessionReq
	 * @return BasePRTSessionRes
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPersonLinkDto> selectPrtChildren(Long idPrtActplnFollowup) {
		List<PRTPersonLinkDto> prtPersonLinkValueDtoList = sessionFactory.getCurrentSession()
				.createSQLQuery(selectPrtChildrenNoStageIdSql).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtBirth", StandardBasicTypes.DATE).addScalar("idPrtPersonLink", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG)
				.addScalar("idPrtActplnFollowup", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("idContactEvent", StandardBasicTypes.LONG)
				.addScalar("idPrtPermStatusLookup", StandardBasicTypes.LONG).addScalar("cdLastPermDsc")
				.addScalar("dtPrtExit", StandardBasicTypes.DATE).addScalar("cdExitReason")
				.addScalar("cdRcmndPrimaryGoal").addScalar("cdRcmndConcurrentGoal").addScalar("indNoConGoal")
				.addScalar("idPrevPermStatus", StandardBasicTypes.LONG).setParameter("idPrtActPln", idPrtActplnFollowup)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class)).list();

		List<PRTPersonLinkDto> prtPersonLinkValueDtoList2 = sessionFactory.getCurrentSession()
				.createSQLQuery(selectPrtChildrenNoStageIdSql2).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idPrtActPln", idPrtActplnFollowup)
				.setResultTransformer(Transformers.aliasToBean(PRTPersonLinkDto.class)).list();
		for (PRTPersonLinkDto child : prtPersonLinkValueDtoList2) {
			for (PRTPersonLinkDto child2 : prtPersonLinkValueDtoList) {
				if (child2.getIdPerson().equals(child.getIdPerson())) {
					child2.setIdChildSUBStage(child.getIdStage());
					break;
				}
			}
		}

		return prtPersonLinkValueDtoList;
	}

}
