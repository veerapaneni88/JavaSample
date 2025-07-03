package us.tx.state.dfps.service.prt.daoimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.PrtActionPlan;
import us.tx.state.dfps.common.domain.PrtActplnFollowup;
import us.tx.state.dfps.common.domain.PrtConnection;
import us.tx.state.dfps.common.domain.PrtEventLink;
import us.tx.state.dfps.common.domain.PrtPermGoal;
import us.tx.state.dfps.common.domain.PrtPermStatusLookup;
import us.tx.state.dfps.common.domain.PrtPersonLink;
import us.tx.state.dfps.common.domain.PrtStrategy;
import us.tx.state.dfps.common.domain.PrtTask;
import us.tx.state.dfps.common.domain.PrtTaskPersonLink;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.ServiceConstants.ActionPlanType;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dao.PRTDao;
import us.tx.state.dfps.service.prt.dto.PRTConnectionDto;
import us.tx.state.dfps.service.prt.dto.PRTEventLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTPermGoalDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.dto.PRTStrategyDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskDto;
import us.tx.state.dfps.service.prt.dto.PRTTaskPersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * BasePRTSessionDaoImpl for BasePRTSession Oct 6, 2017- 3:03:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class PRTDaoImpl implements PRTDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${PRTActionPlanDataFetchDaoImpl.deletePRTPermGoals}")
	private String deletePRTPermGoalsSQL;

	@Value("${PRTActionPlanDaoImpl.deletePrtConnSql}")
	private String deletePrtConnSql;

	@Value("${PRTActionPlanDaoImpl.selectIdPrtPersonLink1}")
	private String selectIdPrtPersonLinkSql1;

	@Value("${PRTActionPlanDaoImpl.selectIdPrtPersonLink2}")
	private String selectIdPrtPersonLinkSql2;

	@Value("${PRTActionPlanDataFetchDaoImpl.deletePRTStrategy}")
	private String deletePRTStrategySQL;

	@Value("${PRTActionPlanDataFetchDaoImpl.deletePRTTasks}")
	private String deletePRTTasksSQL;

	@Value("${PRTActPlanFollowUpDaoImpl.fetchLatestLegalStatus}")
	private String fetchLatestLegalStatusSql;

	/**
	 * Method Name: selectPrtEventLink Method Description:This method is used to
	 * select prt event link data
	 * 
	 * @param idActionPlan
	 * @param actionPlan
	 * @return prtEventLinkValueDtoList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTEventLinkDto> selectPrtEventLink(long idActionPlan, ActionPlanType actionPlan) {
		String selectQry = getSelectQry(ServiceConstants.PRT_EVENT_LINK, actionPlan);
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectQry)
				.addScalar("idPrtEventLink", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idPrtActionPlan", StandardBasicTypes.LONG)
				.addScalar("idPrtActplnFollowup", StandardBasicTypes.LONG).setParameter("idPrtActionPlan", idActionPlan)
				.setResultTransformer(Transformers.aliasToBean(PRTEventLinkDto.class));
		List<PRTEventLinkDto> prtEventLinkValueDtoList = sqlQuery.list();
		if (TypeConvUtil.isNullOrEmpty(prtEventLinkValueDtoList)) {
			throw new DataNotFoundException(
					messageSource.getMessage("person.CheckedOutStagesForPerson.empty", null, Locale.US));
		}
		return prtEventLinkValueDtoList;
	}

	/**
	 * Method Name: getSelectQry Method Description:This method is used to get
	 * the select query
	 * 
	 * @param prtEventLink
	 * @param actionPlan
	 * @return selectQry.toString()
	 */
	private String getSelectQry(String prtEventLink, ActionPlanType actionPlan) {
		StringBuilder selectQry = new StringBuilder();
		String keyColumn = (actionPlan == ServiceConstants.ActionPlanType.ACTION_PLAN)
				? ServiceConstants.ID_PRT_ACTION_PLAN : ServiceConstants.ID_PRT_ACTPLN_FOLLOWUP;
		selectQry.append(ServiceConstants.selecting).append(prtEventLink).append(ServiceConstants.condition)
				.append(keyColumn).append(ServiceConstants.mark);
		return selectQry.toString();
	}

	/**
	 * Method Name: insertPrtEventLink Method Description:This method is used to
	 * insert a record in the PRT_EVENT_LINK table
	 * 
	 * @param prtPersonLinkDto
	 */
	@Override
	public void insertPrtEventLink(PRTEventLinkDto prtEventLinkDto) {
		PrtEventLink prtEventLink = new PrtEventLink();
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, prtEventLinkDto.getIdEvent());
		prtEventLink.setEvent(event);
		prtEventLink.setIdCreatedPerson(prtEventLinkDto.getIdCreatedPerson());
		prtEventLink.setIdLastUpdatePerson(prtEventLinkDto.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(prtEventLinkDto.getIdPrtActplnFollowup())) {
			PrtActplnFollowup prtActplnFollowup = (PrtActplnFollowup) sessionFactory.getCurrentSession()
					.get(PrtActplnFollowup.class, prtEventLinkDto.getIdPrtActplnFollowup());
			prtEventLink.setPrtActplnFollowup(prtActplnFollowup);
		}
		if (!ObjectUtils.isEmpty(prtEventLinkDto.getIdPrtActionPlan())) {
			PrtActionPlan prtActionPlan = (PrtActionPlan) sessionFactory.getCurrentSession().get(PrtActionPlan.class,
					prtEventLinkDto.getIdPrtActionPlan());
			prtEventLink.setPrtActionPlan(prtActionPlan);
		}
		prtEventLink.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtEventLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		sessionFactory.getCurrentSession().save(prtEventLink);
	}

	/**
	 * Method Name: insertPRTPersonLink Method Description:This method is used
	 * to insert the PRT person link details into the DB
	 * 
	 * @param idPrtPersonLink
	 */
	@Override
	public Long insertPRTPersonLink(PRTPersonLinkDto prtPersonLinkDto) {
		PrtPersonLink prtPersonLink = new PrtPersonLink();
		prtPersonLink.setIdCreatedPerson(prtPersonLinkDto.getIdCreatedPerson());
		prtPersonLink.setIdLastUpdatePerson(prtPersonLinkDto.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdPrtActionPlan())) {
			PrtActionPlan prtActionPlan = (PrtActionPlan) sessionFactory.getCurrentSession().get(PrtActionPlan.class,
					prtPersonLinkDto.getIdPrtActionPlan());
			prtPersonLink.setPrtActionPlan(prtActionPlan);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdPrtActplnFollowup())) {
			PrtActplnFollowup prtActplnFollowup = (PrtActplnFollowup) sessionFactory.getCurrentSession()
					.get(PrtActplnFollowup.class, prtPersonLinkDto.getIdPrtActplnFollowup());
			prtPersonLink.setPrtActplnFollowup(prtActplnFollowup);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					prtPersonLinkDto.getIdPerson());
			prtPersonLink.setPerson(person);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdPlcmtEvent())) {
			Placement placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
					prtPersonLinkDto.getIdPlcmtEvent());
			prtPersonLink.setPlacement(placement);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdChildEventId())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
					prtPersonLinkDto.getIdChildEventId());
			prtPersonLink.setEvent(event);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdPrtPermStatusLookup())) {
			PrtPermStatusLookup prtPermStatusLookup = (PrtPermStatusLookup) sessionFactory.getCurrentSession()
					.get(PrtPermStatusLookup.class, prtPersonLinkDto.getIdPrtPermStatusLookup());
			prtPersonLink.setPrtPermStatusLookup(prtPermStatusLookup);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getCdLastPermDsc())) {
			prtPersonLink.setCdLastPermDsc(prtPersonLinkDto.getCdLastPermDsc());
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getDtPrtExit())) {
			prtPersonLink.setDtPrtExit(prtPersonLinkDto.getDtPrtExit());
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getCdExitReason())) {
			prtPersonLink.setCdExitReason(prtPersonLinkDto.getCdExitReason());
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getCdRcmndPrimaryGoal())) {
			prtPersonLink.setCdRcmndPrimaryGoal(prtPersonLinkDto.getCdRcmndPrimaryGoal());
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getCdRcmndConcurrentGoal())) {
			prtPersonLink.setCdRcmndConcurrentGoal(prtPersonLinkDto.getCdRcmndConcurrentGoal());
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIndNoConGoal())) {
			prtPersonLink.setIndNoConGoal(prtPersonLinkDto.getIndNoConGoal());
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkDto.getIdPrevPermStatus())) {
			prtPersonLink.setIdPrevPermStatusLkp(prtPersonLinkDto.getIdPrevPermStatus());
		}
		prtPersonLink.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtPersonLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		return (Long) sessionFactory.getCurrentSession().save(prtPersonLink);
	}

	/**
	 * Method Name: deletePrtGoalsForChild Method Description:This method is
	 * used to delete the goals for a child from the PRT_PERM_GOAL table.
	 * 
	 * @param idPrtPersonLink
	 */
	@Override
	public void deletePrtGoalsForChild(Long idPrtPersonLink) {
		// Delete goals for the child.
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(deletePRTPermGoalsSQL);
		query.setParameter("idPrtPersonLink", idPrtPersonLink);
		query.executeUpdate();
	}

	/**
	 * Method Name: deletePrtConnections Method Description:This method is used
	 * to delete the data for PRT connection.
	 * 
	 * @param deleteIdConnList
	 */
	@Override
	public void deletePrtConnections(List<Long> deleteIdConnList) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(deletePrtConnSql);
		query.setParameterList("connectionList", deleteIdConnList);
		query.executeUpdate();
	}

	/**
	 * This method inserts record into PRT_CONNECTION table.
	 *
	 * @param prtConnection
	 *            the prt connection
	 * @return the prt connection
	 * @returns idPrtConnection - newly created Id.
	 */
	@Override
	public PrtConnection insertPrtConnection(PRTConnectionDto prtConn) {
		PrtConnection prtConnection = new PrtConnection();
		if (!ObjectUtils.isEmpty(prtConn.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, prtConn.getIdPerson());
			prtConnection.setPerson(person);
		}
		prtConnection.setIdCreatedPerson(prtConn.getIdCreatedPerson());
		prtConnection.setIdLastUpdatePerson(prtConn.getIdLastUpdatePerson());
		prtConnection.setIdPrtPersonLink(prtConn.getIdPrtPersonLink());
		prtConnection.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtConnection.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		sessionFactory.getCurrentSession().save(prtConnection);
		return prtConnection;
	}

	/**
	 * Method Name: insertPRTPermGoals Method Description:This method is used to
	 * insert record into PRT_PERM_GOAL table.
	 * 
	 * @param goal
	 */

	@Override
	public Long insertPRTPermGoals(PRTPermGoalDto goal) {
		PrtPermGoal prtPermGoal = new PrtPermGoal();
		if (!ObjectUtils.isEmpty(goal.getIdPrtPersonLink())) {
			PrtPersonLink prtPersonLink = (PrtPersonLink) sessionFactory.getCurrentSession().get(PrtPersonLink.class,
					goal.getIdPrtPersonLink());
			prtPermGoal.setPrtPersonLink(prtPersonLink);
		}
		prtPermGoal.setIdCreatedPerson(goal.getIdCreatedPerson());
		prtPermGoal.setIdLastUpdatePerson(goal.getIdLastUpdatePerson());
		prtPermGoal.setCdGoal(goal.getCdGoal());
		prtPermGoal.setCdType(goal.getCdType());
		prtPermGoal.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtPermGoal.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		Long idGoal = ((BigDecimal) sessionFactory.getCurrentSession().save(prtPermGoal)).longValue();
		return idGoal;
	}

	/**
	 * Method Name: insertPrtStrategy Method Description:This method is used to
	 * insert record into the PRT_STRATEGY table.
	 * 
	 * @param prtStrategyDto
	 */
	@Override
	public Long insertPrtStrategy(PRTStrategyDto prtStrategyDto) {
		PrtStrategy prtStrategy = new PrtStrategy();
		if (0l != prtStrategyDto.getIdPrtActionPlan()) {
			PrtActionPlan prtActionPlan = (PrtActionPlan) sessionFactory.getCurrentSession().get(PrtActionPlan.class,
					prtStrategyDto.getIdPrtActionPlan());
			prtStrategy.setPrtActionPlan(prtActionPlan);
		}
		if (0l != prtStrategyDto.getIdPrtActplnFollowup()) {
			PrtActplnFollowup prtActplnFollowup = (PrtActplnFollowup) sessionFactory.getCurrentSession()
					.get(PrtActplnFollowup.class, prtStrategyDto.getIdPrtActplnFollowup());
			prtStrategy.setPrtActplnFollowup(prtActplnFollowup);
		}
		prtStrategy.setCdStrategy(prtStrategyDto.getCdStrategy());
		prtStrategy.setIdCreatedPerson(prtStrategyDto.getIdCreatedPerson());
		prtStrategy.setIdLastUpdatePerson(prtStrategyDto.getIdLastUpdatePerson());
		prtStrategy.setTxtOtherDesc(prtStrategyDto.getTxtOtherDesc());
		prtStrategy.setIdSrcPrtStrategy(prtStrategyDto.getIdSrcPrtStrategy());
		prtStrategy.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtStrategy.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		Long idStrategy = (Long) sessionFactory.getCurrentSession().save(prtStrategy);
		return idStrategy;
	}

	/**
	 * Method Name: insertPrtTask Method Description:This method is used to
	 * create a record in the PRT_TASK table.
	 * 
	 * @param task
	 * @return Long - The unique identifier generated after creating the record.
	 */
	@Override
	public Long insertPrtTask(PRTTaskDto task) {
		PrtTask prtTask = new PrtTask();

		if (!ObjectUtils.isEmpty(task.getIdCreatedPerson())) {
			prtTask.setIdCreatedPerson(task.getIdCreatedPerson());
		}
		if (!ObjectUtils.isEmpty(task.getIdLastUpdatePerson())) {
			prtTask.setIdLastUpdatePerson(task.getIdLastUpdatePerson());
		}
		if (!ObjectUtils.isEmpty(task.getIdPrtStrategy())) {
			PrtStrategy prtStrategy = (PrtStrategy) sessionFactory.getCurrentSession().get(PrtStrategy.class,
					task.getIdPrtStrategy());
			prtTask.setPrtStrategy(prtStrategy);
		}
		if (!ObjectUtils.isEmpty(task.getIdPersonAssigned())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, task.getIdPersonAssigned());
			prtTask.setPerson(person);
		}
		if (!ObjectUtils.isEmpty(task.getCdBarrier())) {
			prtTask.setCdBarrier(task.getCdBarrier());
		}
		if (!ObjectUtils.isEmpty(task.getTxtPlanOvrcmBrs())) {
			prtTask.setTxtPlanOvrcmBrs(task.getTxtPlanOvrcmBrs());
		}
		if (!ObjectUtils.isEmpty(task.getDtTargetComplete())) {
			prtTask.setDtTargetComplete(task.getDtTargetComplete());
		}
		// If task status is completed or eliminated, populate the date
		// completed or
		// date eliminated using system date.
		// Else if user either modified from completed/eliminated to in-process
		// then
		// blank away the date completed/eliminated date.
		if (!CodesConstant.CPRTSKST_10.equals(task.getCdTaskStatus())) {
			task.setDtCompOrElimnated(new Date());
		}
		if (!ObjectUtils.isEmpty(task.getDtCompOrElimnated())) {
			prtTask.setDtCompOrEliminated(task.getDtCompOrElimnated());
		}
		if (!ObjectUtils.isEmpty(task.getIdPrtSrcTask())) {
			prtTask.setDtCompOrEliminated(task.getDtCompOrElimnated());
		}
		if (!ObjectUtils.isEmpty(task.getTxtDesc())) {
			prtTask.setTxtDesc(task.getTxtDesc());
		}
		if (!ObjectUtils.isEmpty(task.getCdAssignedToType())) {
			prtTask.setCdAssignedType(task.getCdAssignedToType());
		}
		if (!ObjectUtils.isEmpty(task.getCdTaskStatus())) {
			prtTask.setCdTaskStatus(task.getCdTaskStatus());
		}
		if (!ObjectUtils.isEmpty(task.getTxtEliminationDetails())) {
			prtTask.setTxtElmntnDetails(task.getTxtEliminationDetails());
		}
		if (!ObjectUtils.isEmpty(task.getTxtComments())) {
			prtTask.setTxtComments(task.getTxtComments());
		}
		if (!ObjectUtils.isEmpty(task.getCdEliminationReason())) {
			prtTask.setCdElimReason(task.getCdEliminationReason());
		}
		if (!ObjectUtils.isEmpty(task.getIdPrtParentTask())) {
			prtTask.setIdParentTask(task.getIdPrtParentTask());
		}
		prtTask.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtTask.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		Long idTask = (Long) sessionFactory.getCurrentSession().save(prtTask);
		return idTask;
	}

	/**
	 * Method Name: getNewIDPRTPersonLink Method Description: This method
	 * returns new PRT person link for a child.
	 * 
	 * @param idPrtActionPlan
	 * @param idSrcPrtActplnFollowup
	 * @param idPrtActplnFollowup
	 * @param idPrtPersonLink
	 * @return
	 */
	@Override
	public Long getNewIDPRTPersonLink(Long idPrtActionPlan, Long idSrcPrtActplnFollowup, Long idPrtActplnFollowup,
			Long idPrtPersonLink) {
		Long newPRTPersonLink = 0l;
		Long idActPlanOrFollowUp = 0l;
		StringBuilder NEW_PRT_PERSON_LINK = new StringBuilder(selectIdPrtPersonLinkSql1);
		if (ObjectUtils.isEmpty(idSrcPrtActplnFollowup)
				|| (!ObjectUtils.isEmpty(idSrcPrtActplnFollowup) && idSrcPrtActplnFollowup.equals(0l))) {
			idActPlanOrFollowUp = idPrtActionPlan;
			NEW_PRT_PERSON_LINK.append(" prtActionPlan.idPrtActionPlan = :idActPlanOrFollowUp ");
		} else {
			idActPlanOrFollowUp = idSrcPrtActplnFollowup;
			NEW_PRT_PERSON_LINK.append("prtActplnFollowup.idPrtActplnFollowup = :idActPlanOrFollowUp ");
		}
		NEW_PRT_PERSON_LINK.append(selectIdPrtPersonLinkSql2);
		Query query = sessionFactory.getCurrentSession().createQuery(NEW_PRT_PERSON_LINK.toString())
				.setParameter("idActPlanOrFollowUp", idActPlanOrFollowUp)
				.setParameter("idPrtPersonLink", idPrtPersonLink)
				.setParameter("idPrtActplnFollowup", idPrtActplnFollowup);
		newPRTPersonLink = (Long) query.uniqueResult();

		return newPRTPersonLink;
	}

	/**
	 * Method Name: insertTaskPersonLink Method Description:This method is used
	 * to insert a record in the PRT_PERSON_LINK table.
	 * 
	 * @param taskPersonLink
	 */
	@Override
	public void insertTaskPersonLink(PRTTaskPersonLinkDto taskPersonLink) {
		PrtTaskPersonLink prtTaskPersonLink = new PrtTaskPersonLink();
		prtTaskPersonLink.setIdCreatedPerson(taskPersonLink.getIdCreatedPerson());
		prtTaskPersonLink.setIdLastUpdatePerson(taskPersonLink.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(taskPersonLink.getIdPrtTask())) {
			PrtTask prtTask = (PrtTask) sessionFactory.getCurrentSession().get(PrtTask.class,
					taskPersonLink.getIdPrtTask());
			prtTaskPersonLink.setPrtTask(prtTask);
		}

		if (!ObjectUtils.isEmpty(taskPersonLink.getIdPrtPersonLink())) {
			PrtPersonLink prtPersonLink = (PrtPersonLink) sessionFactory.getCurrentSession().get(PrtPersonLink.class,
					taskPersonLink.getIdPrtPersonLink());
			prtTaskPersonLink.setPrtPersonLink(prtPersonLink);
		}
		prtTaskPersonLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		prtTaskPersonLink.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		sessionFactory.getCurrentSession().save(prtTaskPersonLink);
	}

	/**
	 * This method updates PRT_PERSON_LINK table using PRTPersonLinkValueBean.
	 * 
	 * @param PRTPersonLinkValueBean
	 */
	@Override
	public void updatePRTPersonLink(PRTPersonLinkDto prtPersonLinkValueDto) {
		PrtPersonLink personLink = (PrtPersonLink) sessionFactory.getCurrentSession().get(PrtPersonLink.class,
				prtPersonLinkValueDto.getIdPrtPersonLink());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getIdPlcmtEvent())) {
			Placement placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
					prtPersonLinkValueDto.getIdPlcmtEvent());

			personLink.setPlacement(placement);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					prtPersonLinkValueDto.getIdPerson());

			personLink.setPerson(person);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getIdPrtPermStatusLookup())) {
			PrtPermStatusLookup prtPermStatusLookup = (PrtPermStatusLookup) sessionFactory.getCurrentSession()
					.get(PrtPermStatusLookup.class, prtPersonLinkValueDto.getIdPrtPermStatusLookup());
			personLink.setPrtPermStatusLookup(prtPermStatusLookup);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getIdChildEventId())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
					prtPersonLinkValueDto.getIdChildEventId());
			personLink.setEvent(event);
		}
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getIdLastUpdatePerson()))
			personLink.setIdLastUpdatePerson(prtPersonLinkValueDto.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getCdLastPermDsc()))
			personLink.setCdLastPermDsc(prtPersonLinkValueDto.getCdLastPermDsc());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getDtPrtExit()))
			personLink.setDtPrtExit(prtPersonLinkValueDto.getDtPrtExit());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getCdExitReason()))
			personLink.setCdExitReason(prtPersonLinkValueDto.getCdExitReason());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getCdRcmndPrimaryGoal()))
			personLink.setCdRcmndPrimaryGoal(prtPersonLinkValueDto.getCdRcmndPrimaryGoal());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getCdRcmndConcurrentGoal()))
			personLink.setCdRcmndConcurrentGoal(prtPersonLinkValueDto.getCdRcmndConcurrentGoal());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getIndNoConGoal()))
			personLink.setIndNoConGoal(prtPersonLinkValueDto.getIndNoConGoal());
		if (!ObjectUtils.isEmpty(prtPersonLinkValueDto.getIdPrevPermStatus()))
			personLink.setIdPrevPermStatusLkp(prtPersonLinkValueDto.getIdPrevPermStatus());
		personLink.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		sessionFactory.getCurrentSession().saveOrUpdate(personLink);
	}

	/**
	 * Method Name: deletePRTStrategy Method Description:This method is used to
	 * delete the strategy and tasks from the following tables
	 * -PRT_STRATEGY,PRT_TASK_PERSON_LINK,PRT_TASK
	 * 
	 * @param prtActplanFollowUpReq
	 */
	@Override
	public void deletePRTStrategy(Long idStrategy) {
		// Delete child related to the tasks.
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deletePRTStrategySQL);
		query.setParameter("idStrategy", idStrategy);
		query.executeUpdate();
		// Delete all PRT_TASKS, related to the strategy
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(deletePRTTasksSQL);
		query1.setParameter("idStrategy", idStrategy);
		query1.executeUpdate();
		// Delete prt_strategy
		PrtStrategy prtStrategy = (PrtStrategy) sessionFactory.getCurrentSession().get(PrtStrategy.class, idStrategy);
		sessionFactory.getCurrentSession().delete(prtStrategy);
	}

	/**
	 * Method Name: selectPRTPermGoals Method Description:This method is used to
	 * select the PRT perm goals
	 * 
	 * @param idPrtPersonLink
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTPermGoalDto> selectPRTPermGoals(Long idPrtPersonLink) {
		List<PRTPermGoalDto> prtPermGoalDtoList = new ArrayList<PRTPermGoalDto>();
		List<PrtPermGoal> prtPermGoalList = sessionFactory.getCurrentSession().createCriteria(PrtPermGoal.class)
				.add(Restrictions.eq("prtPersonLink.idPrtPersonLink", idPrtPersonLink)).list();
		if (!CollectionUtils.isEmpty(prtPermGoalList)) {
			prtPermGoalList.forEach(prtPermGoal -> {
				PRTPermGoalDto prtPermGoalValueDto = new PRTPermGoalDto();
				BeanUtils.copyProperties(prtPermGoal, prtPermGoalValueDto);
				if (!ObjectUtils.isEmpty(prtPermGoal.getPrtPersonLink())
						&& !ObjectUtils.isEmpty(prtPermGoal.getPrtPersonLink().getIdPrtPersonLink())) {
					prtPermGoalValueDto.setIdPrtPersonLink(prtPermGoal.getPrtPersonLink().getIdPrtPersonLink());
				}
				prtPermGoalDtoList.add(prtPermGoalValueDto);
			});
		}
		return prtPermGoalDtoList;
	}

	/**
	 * Method Name: fetchLatestLegalStatus Method Description:This method is
	 * used to fetch the legal Status for the person
	 * 
	 * @param idPerson
	 */
	@Override
	public LegalStatusDto fetchLatestLegalStatus(Long idPerson) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchLatestLegalStatusSql)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatusDt", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDto.class));
		return (LegalStatusDto) query.uniqueResult();

	}

	/**
	 * Method Name: fetchLatestLegalStatus Method Description:This method is
	 * used to select PRT Strategy
	 * 
	 * @param idPrtActPlnOrFollowup
	 * @param planType
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTStrategyDto> selectPrtStrategy(Long idPrtActPlnOrFollowup, ActionPlanType planType) {
		List<PRTStrategyDto> prtStrategyValueDtoList = new ArrayList<PRTStrategyDto>();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtStrategy.class);
		if (planType == ActionPlanType.ACTION_PLAN)
			criteria.add(Restrictions.eq("prtActionPlan.idPrtActionPlan", idPrtActPlnOrFollowup));
		else
			criteria.add(Restrictions.eq("prtActplnFollowup.idPrtActplnFollowup", idPrtActPlnOrFollowup));
		List<PrtStrategy> prtStrategList = criteria.list();
		for (PrtStrategy prtStrategy : prtStrategList) {
			PRTStrategyDto prtStrategyValueDto = new PRTStrategyDto();
			BeanUtils.copyProperties(prtStrategy, prtStrategyValueDto);

			if (!ObjectUtils.isEmpty(prtStrategy.getPrtActionPlan())
					&& !ObjectUtils.isEmpty(prtStrategy.getPrtActionPlan().getIdPrtActionPlan())) {
				prtStrategyValueDto.setIdPrtActionPlan(prtStrategy.getPrtActionPlan().getIdPrtActionPlan());
			}
			if (!ObjectUtils.isEmpty(prtStrategy.getPrtActplnFollowup())
					&& !ObjectUtils.isEmpty(prtStrategy.getPrtActplnFollowup().getIdPrtActplnFollowup())) {
				prtStrategyValueDto.setIdPrtActplnFollowup(prtStrategy.getPrtActplnFollowup().getIdPrtActplnFollowup());
			}
			prtStrategyValueDtoList.add(prtStrategyValueDto);
		}
		return prtStrategyValueDtoList;
	}

	/**
	 * Method Name: fetchLatestLegalStatus Method Description:This method is
	 * used to select PRT Strategy
	 * 
	 * @param idPrtActPlnOrFollowup
	 * @param planType
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
		List<PrtTask> prtTaskList = criteria.list();
		if (!CollectionUtils.isEmpty(prtTaskList)) {

			prtTaskList.forEach(prtTask -> {
				PRTTaskDto prtTaskValueDto = new PRTTaskDto();
				BeanUtils.copyProperties(prtTask, prtTaskValueDto);
				prtTaskValueDto.setIdPrtStrategy(prtTask.getPrtStrategy().getIdPrtStrategy());

				if (!ObjectUtils.isEmpty(prtTask.getPerson())
						&& !ObjectUtils.isEmpty(prtTask.getPerson().getIdPerson())) {
					prtTaskValueDto.setIdPersonAssigned(prtTask.getPerson().getIdPerson());
				}
				prtTaskValueDto.setTxtEliminationDetails(prtTask.getTxtElmntnDetails());
				prtTaskValueDto.setIdPrtSrcTask(prtTask.getIdPrtTask());
				prtTaskValueDto.setCdAssignedToType(prtTask.getCdAssignedType());
				if (!ObjectUtils.isEmpty(prtTask.getIdParentTask()))
					prtTaskValueDto.setIdPrtParentTask(prtTask.getIdParentTask());
				prtTaskValueDtoList.add(prtTaskValueDto);
			});

		}
		return prtTaskValueDtoList;
	}

	/**
	 * Method Name: selectChildrenForTask Method Description:This method is used
	 * to Select children for the task
	 * 
	 * @param idPrtTask
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<PRTTaskPersonLinkDto> selectChildrenForTask(Long idPrtTask) {
		List<PRTTaskPersonLinkDto> prtTaskPersonLinkValueList = new ArrayList<PRTTaskPersonLinkDto>();
		List<PrtTaskPersonLink> prtTaskPersonLinkList = (List<PrtTaskPersonLink>) sessionFactory.getCurrentSession()
				.createCriteria(PrtTaskPersonLink.class).add(Restrictions.eq("prtTask.idPrtTask", idPrtTask)).list();
		if (!CollectionUtils.isEmpty(prtTaskPersonLinkList)) {
			prtTaskPersonLinkList.forEach(prtTaskPersonLink -> {
				PRTTaskPersonLinkDto prtTaskPersonLinkValueDto = new PRTTaskPersonLinkDto();
				BeanUtils.copyProperties(prtTaskPersonLink, prtTaskPersonLinkValueDto);
				if (!ObjectUtils.isEmpty(prtTaskPersonLink.getPrtPersonLink())
						&& !ObjectUtils.isEmpty(prtTaskPersonLink.getPrtPersonLink().getIdPrtPersonLink())) {
					prtTaskPersonLinkValueDto
							.setIdPrtPersonLink(prtTaskPersonLink.getPrtPersonLink().getIdPrtPersonLink());
				}
				if (!ObjectUtils.isEmpty(prtTaskPersonLink.getPrtTask())
						&& !ObjectUtils.isEmpty(prtTaskPersonLink.getPrtTask().getIdPrtTask())) {
					prtTaskPersonLinkValueDto.setIdPrtTask(prtTaskPersonLink.getPrtTask().getIdPrtTask());
				}
				prtTaskPersonLinkValueList.add(prtTaskPersonLinkValueDto);
			});

		}
		return prtTaskPersonLinkValueList;
	}

	/**
	 * Method Name: selectPrtEventLink Method Description:This method is used to
	 * Select PRT Event Link
	 * 
	 * @param idPrtActPlnOrFollowup
	 * @param planType
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PRTEventLinkDto> selectPrtEventLink(Long idPrtActPlnOrFollowup, ActionPlanType planType) {
		List<PRTEventLinkDto> prtTaskPersonLinkValueList = new ArrayList<PRTEventLinkDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PrtEventLink.class);
		if (planType == ActionPlanType.ACTION_PLAN)
			criteria.add(Restrictions.eq("prtActionPlan.idPrtActionPlan", idPrtActPlnOrFollowup));
		else
			criteria.add(Restrictions.eq("prtActplnFollowup.idPrtActplnFollowup", idPrtActPlnOrFollowup));

		List<PrtEventLink> PrtEventLinkList = criteria.list();
		for (PrtEventLink prtEventLink : PrtEventLinkList) {
			PRTEventLinkDto prtEventLinkValueDto = new PRTEventLinkDto();
			prtEventLinkValueDto.setIdPrtEventLink(prtEventLink.getIdPrtEventLink());
			prtEventLinkValueDto.setDtCreated(prtEventLink.getDtCreated());
			prtEventLinkValueDto.setIdCreatedPerson(prtEventLink.getIdCreatedPerson());
			prtEventLinkValueDto.setDtLastUpdate(prtEventLink.getDtLastUpdate());
			prtEventLinkValueDto.setIdLastUpdatePerson(prtEventLink.getIdLastUpdatePerson());
			if (!ObjectUtils.isEmpty(prtEventLink.getEvent())
					&& !ObjectUtils.isEmpty(prtEventLink.getEvent().getIdEvent())) {
				prtEventLinkValueDto.setIdEvent(prtEventLink.getEvent().getIdEvent());
			}
			if (!ObjectUtils.isEmpty(prtEventLink.getPrtActionPlan())
					&& !ObjectUtils.isEmpty(prtEventLink.getPrtActionPlan().getIdPrtActionPlan())) {
				prtEventLinkValueDto.setIdPrtActionPlan(prtEventLink.getPrtActionPlan().getIdPrtActionPlan());
			}
			if (!ObjectUtils.isEmpty(prtEventLink.getPrtActplnFollowup())
					&& !ObjectUtils.isEmpty(prtEventLink.getPrtActplnFollowup().getIdSrcPrtActplnFollowup())) {
				prtEventLinkValueDto
						.setIdPrtActplnFollowup(prtEventLink.getPrtActplnFollowup().getIdSrcPrtActplnFollowup());
			}
			prtTaskPersonLinkValueList.add(prtEventLinkValueDto);
		}
		return prtTaskPersonLinkValueList;
	}
}
