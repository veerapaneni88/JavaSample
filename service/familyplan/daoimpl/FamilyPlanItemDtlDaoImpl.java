package us.tx.state.dfps.service.familyplan.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.FamilyPlanEvalItem;
import us.tx.state.dfps.common.domain.FamilyPlanItem;
import us.tx.state.dfps.common.domain.FamilyPlanTask;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanItemDtlDao;
import us.tx.state.dfps.service.familyplan.dto.FamilyPlanEvalItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanGoalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FamilyPlanDaoImpl will implemented all operation defined in
 * FamilyPlanDao Interface related FamilyPlan module. Mar 8, 2018- 2:02:21 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class FamilyPlanItemDtlDaoImpl implements FamilyPlanItemDtlDao {

	public static final String UPDATE_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN = "UPDATE_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN";
	public static final String UPDATE_WITHOUT_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN = "UPDATE_WITHOUT_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN";

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FamilyPlanItemDtlDaoImpl.familyPlanItemDtls}")
	private String familyPlanItemDtls;

	@Value("${FamilyPlanItemDtlDaoImpl.familyPlanItemDtlTasks}")
	private String familyPlanItemDtlTasks;

	@Value("${FamilyPlanItemDtlDaoImpl.completedFamilyPlanItemDtlTasks}")
	private String completedFamilyPlanItemDtlTasks;

	@Value("${FamilyPlanItemDtlDaoImpl.familyPlanEvaluationItems}")
	private String familyPlanEvaluationItems;

	@Value("${FamilyPlanItemDtlDaoImpl.getFamilyPlanItemDtls}")
	private String getFamilyPlanItemDtls;

	@Value("${FamilyPlanItemDtlDaoImpl.getFamilyPlanGoalsForItem}")
	private String getFamilyPlanGoalsForItem;

	@Value("${FamilyPlanItemDtlDaoImpl.getOldTaskGoals}")
	private String getOldTaskGoals;

	@Value("${FamilyPlanItemDtlDaoImpl.getNewTaskGoals}")
	private String getNewTaskGoals;

	/**
	 * Method Name: getFamilyPlanItemDtlLegacy
	 * 
	 * Method Description:This method is used to get the legacy family plan item
	 * detail for the version 1
	 * 
	 * @param familyPlanItemDto
	 * @return familyPlanItemDto
	 */
	@Override
	public FamilyPlanItemDto getFamilyPlanItemDtlLegacy(FamilyPlanItemDto familyPlanItemDto) {
		FamilyPlanItemDto familyPlanItemDtoResp = null;
		List<FamilyPlanTaskDto> finalFamilyPlanTaskDtoList = new ArrayList<>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(familyPlanItemDtls)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdAreaConcern", StandardBasicTypes.STRING)
				.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING)
				.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING)
				.addScalar("txtItemGoals", StandardBasicTypes.STRING)
				.addScalar("dtInitiallyAddressed", StandardBasicTypes.DATE)
				.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING)
				.addScalar("txtAreaOfConcern", StandardBasicTypes.STRING)
				.setParameter("idCase", familyPlanItemDto.getIdCase())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));
		familyPlanItemDtoResp = (FamilyPlanItemDto) query.uniqueResult();

		// query all non-completed tasks in chronological order by the date
		query = sessionFactory.getCurrentSession().createSQLQuery(familyPlanItemDtlTasks)
				.addScalar("idFamilyPlanTask", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("indCourtOrdered", StandardBasicTypes.STRING)
				.addScalar("txtTask", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.setParameter("idCase", familyPlanItemDto.getIdCase())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class));

		List<FamilyPlanTaskDto> familyPlanTaskDtoList = query.list();
		if (!ObjectUtils.isEmpty(familyPlanTaskDtoList)) {
			finalFamilyPlanTaskDtoList.addAll(familyPlanTaskDtoList);
		}

		// query all completed tasks in chronological order by the date created.

		query = sessionFactory.getCurrentSession().createSQLQuery(completedFamilyPlanItemDtlTasks)
				.addScalar("idFamilyPlanTask", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("indCourtOrdered", StandardBasicTypes.STRING)
				.addScalar("txtTask", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).setParameter("idCase", familyPlanItemDto.getIdCase())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class));

		List<FamilyPlanTaskDto> compFamilyPlanTaskDtoList = query.list();
		if (!ObjectUtils.isEmpty(compFamilyPlanTaskDtoList)) {
			finalFamilyPlanTaskDtoList.addAll(compFamilyPlanTaskDtoList);
		}
		if (!ObjectUtils.isEmpty(finalFamilyPlanTaskDtoList)) {
			familyPlanItemDtoResp.setFamilyPlanTaskDtoList(finalFamilyPlanTaskDtoList);
		}

		familyPlanItemDtoResp = queryFamilyPlanEvalItems(familyPlanItemDtoResp);
		return familyPlanItemDtoResp;

	}

	/**
	 * Method Name: queryFamilyPlanEvalItems Method Description:This method is
	 * used to get the family plan evaluation items
	 * 
	 * @param familyPlanItemDto
	 * @return FamilyPlanItemDto
	 */
	private FamilyPlanItemDto queryFamilyPlanEvalItems(FamilyPlanItemDto familyPlanItemDto) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(familyPlanEvaluationItems)
				.addScalar("idFamilyPlanEvalItem", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("txtItemEvaluation", StandardBasicTypes.STRING)
				.addScalar("txtNewConcerns", StandardBasicTypes.STRING)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).setParameter("idCase", familyPlanItemDto.getIdCase())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanEvalItemDto.class));
		List<FamilyPlanEvalItemDto> familyPlanEvalItemDtoList = query.list();

		if (!ObjectUtils.isEmpty(familyPlanEvalItemDtoList)) {
			familyPlanItemDto.setFamilyPlanEvalItemDtoList(familyPlanEvalItemDtoList);
		}

		return familyPlanItemDto;

	}

	/**
	 * Method Name: getFamilyPlanItemDtl Method Description:This method is used
	 * to get the legacy family plan item detail for the version 0
	 * 
	 * @param familyPlanItemDto
	 * @return familyPlanItemDto
	 */
	@Override
	public FamilyPlanItemDto getFamilyPlanItemDtl(FamilyPlanItemDto familyPlanItemDto) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanItemDtls)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdAreaConcern", StandardBasicTypes.STRING)
				.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING)
				.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING)
				.addScalar("cdPrevLevelConcern", StandardBasicTypes.STRING)
				.addScalar("txtInitialConcerns", StandardBasicTypes.STRING)
				.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING)
				.addScalar("dtInitiallyAddressed", StandardBasicTypes.DATE)
				.addScalar("indAddressedFamPlan", StandardBasicTypes.STRING)
				.addScalar("idAddressedEvent", StandardBasicTypes.LONG)
				.addScalar("txtAreaOfConcern", StandardBasicTypes.STRING)
				.setParameter("idCase", familyPlanItemDto.getIdCase())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));

		FamilyPlanItemDto resultfamilyPlanItemDto = (FamilyPlanItemDto) query.uniqueResult();
		if (!ObjectUtils.isEmpty(resultfamilyPlanItemDto)) {
			if (!ObjectUtils.isEmpty(resultfamilyPlanItemDto.getIdAddressedEvent())
					&& resultfamilyPlanItemDto.getIdAddressedEvent() > 0) {
				resultfamilyPlanItemDto.setIndIdAddressedEventColNull(ServiceConstants.N);
			} else {
				resultfamilyPlanItemDto.setIndIdAddressedEventColNull(ServiceConstants.Y);
			}
			if (!ObjectUtils.isEmpty(resultfamilyPlanItemDto.getIndAddressedFamPlan())) {
				resultfamilyPlanItemDto.setIndAddressedInFamilyPlanColNull(ServiceConstants.N);
			} else {
				resultfamilyPlanItemDto.setIndAddressedInFamilyPlanColNull(ServiceConstants.Y);
			}
		}
		// Query the eval items for this family plan item.
		resultfamilyPlanItemDto = queryFamilyPlanEvalItems(resultfamilyPlanItemDto);

		resultfamilyPlanItemDto = populateGoalsForItem(resultfamilyPlanItemDto);
		if (!ObjectUtils.isEmpty(familyPlanItemDto.getIdCurrentEvalEvent())
				&& familyPlanItemDto.getIdCurrentEvalEvent() > 0) {

			if (!ObjectUtils.isEmpty(resultfamilyPlanItemDto.getFamilyPlanGoalDtoList())) {
				resultfamilyPlanItemDto
						.setFamilyPlanTaskDtoList(getTasksForGoals(resultfamilyPlanItemDto.getFamilyPlanGoalDtoList(),
								resultfamilyPlanItemDto.getIdCase(), familyPlanItemDto.getIdCurrentEvalEvent()));

			}

		}
		return resultfamilyPlanItemDto;
	}

	/**
	 * Method Name: getTasksForGoals Method Description :This method is used to
	 * get the tasks for the goals
	 * 
	 * @param familyPlanGoalDtoList
	 * @param idCase
	 * @param idCurrentEvalEvent
	 * @return
	 */
	private List<FamilyPlanTaskDto> getTasksForGoals(List<FamilyPlanGoalDto> familyPlanGoalDtoList, Long idCase,
			Long idCurrentEvalEvent) {

		List<FamilyPlanTaskDto> resultTaskGoals = new ArrayList<>();
		List<Long> familyGoalIds = new ArrayList<>();

		if (!ObjectUtils.isEmpty(familyPlanGoalDtoList) && familyPlanGoalDtoList.size() > 0) {
			for (FamilyPlanGoalDto familyPlanGoalDto : familyPlanGoalDtoList) {
				familyGoalIds.add(familyPlanGoalDto.getIdFamilyPlanGoal());
			}
		}

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getOldTaskGoals)
				.addScalar("idFamilyPlanTask", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("txtTask", StandardBasicTypes.STRING).addScalar("indCpsAssigned", StandardBasicTypes.STRING)
				.addScalar("indFamilyAssigned", StandardBasicTypes.STRING)
				.addScalar("indParentsAssigned", StandardBasicTypes.STRING)
				.addScalar("indCpsOrdered", StandardBasicTypes.STRING)
				.addScalar("indFamilyOrdered", StandardBasicTypes.STRING)
				.addScalar("indParentsOrdered", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtNoLongerNeeded", StandardBasicTypes.DATE)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).addScalar("dtApproved", StandardBasicTypes.DATE)
				.setParameterList("goalIds", familyGoalIds).setParameter("idCase", idCase)
				.setParameter("idCompletedEvent", idCurrentEvalEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class));
		List<FamilyPlanTaskDto> oldTasksList = query.list();
		if (!ObjectUtils.isEmpty(oldTasksList)) {
			resultTaskGoals = oldTasksList;
		}

		query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getNewTaskGoals)
				.addScalar("idFamilyPlanTask", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("txtTask", StandardBasicTypes.STRING).addScalar("indCpsAssigned", StandardBasicTypes.STRING)
				.addScalar("indFamilyAssigned", StandardBasicTypes.STRING)
				.addScalar("indParentsAssigned", StandardBasicTypes.STRING)
				.addScalar("indCpsOrdered", StandardBasicTypes.STRING)
				.addScalar("indFamilyOrdered", StandardBasicTypes.STRING)
				.addScalar("indParentsOrdered", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtNoLongerNeeded", StandardBasicTypes.DATE)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).addScalar("dtApproved", StandardBasicTypes.DATE)
				.setParameterList("goalIds", familyGoalIds).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class));
		List<FamilyPlanTaskDto> newTasksList = query.list();
		if (!ObjectUtils.isEmpty(newTasksList)) {
			resultTaskGoals.addAll(newTasksList);
		}

		if (resultTaskGoals.size() > 0) {
			return resultTaskGoals;
		} else {
			return null;
		}
	}

	/**
	 * Method Name: populateGoalsForItem Method Description : This method is
	 * used to get the goals for the given family plan item
	 * 
	 * @param familyPlanItemDtoResp
	 * @return
	 */
	@Override
	public FamilyPlanItemDto populateGoalsForItem(FamilyPlanItemDto familyPlanItemDtoResp) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanGoalsForItem)
				.addScalar("txtGoal", StandardBasicTypes.STRING).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtApproved", StandardBasicTypes.DATE).addScalar("idFamilyPlanGoal", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", familyPlanItemDtoResp.getIdCase())
				.setParameter("idFamilyPlanItem", familyPlanItemDtoResp.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanGoalDto.class));
		List<FamilyPlanGoalDto> familyPlanGoalDtoList = query.list();

		if (!ObjectUtils.isEmpty(familyPlanGoalDtoList)) {
			familyPlanItemDtoResp.setFamilyPlanGoalDtoList(familyPlanGoalDtoList);
		}

		return familyPlanItemDtoResp;

	}

	/**
	 * 
	 * Method Name: updateFamilyPlanItem Method Description: This method updates
	 * the FAMILY_PLAN_ITEM table.
	 * 
	 * @param familyPlanItemDto
	 */
	@Override
	public void updateFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto, String reqFunc) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanItem.class);
		criteria.add(Restrictions.eq("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem()));
		criteria.add(Restrictions.eq("capsCase.idCase", familyPlanItemDto.getIdCase()));
		FamilyPlanItem familyPlanItem = (FamilyPlanItem) criteria.uniqueResult();
		familyPlanItem.setCdCurrentLevelConcern(familyPlanItemDto.getCdCurrentLevelConcern());
		if (!ObjectUtils.isEmpty(familyPlanItemDto.getDtInitiallyAddressed())) {
			familyPlanItem.setDtInitiallyAddressed(familyPlanItemDto.getDtInitiallyAddressed());
		} else {
			familyPlanItem.setDtInitiallyAddressed(new Date());
		}
		familyPlanItem.setTxtInitialConcerns(familyPlanItemDto.getTxtInitialConcerns());
		if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(reqFunc)) {
			familyPlanItem.setCdCurrentLevelConcern(null);
			familyPlanItem.setDtInitiallyAddressed(null);
			familyPlanItem.setTxtInitialConcerns(null);
			familyPlanItem.setIdAddressedEvent(null);
			familyPlanItem.setIndAddressedFamPlan(ServiceConstants.NULL_STRING);
		} else if (UPDATE_ID_ADDRESSED_EVENT_AND_IND_ADDRESSED_FAM_PLAN.equals(reqFunc)) {
			familyPlanItem.setIdAddressedEvent(familyPlanItemDto.getIdAddressedEvent());
			familyPlanItem.setIndAddressedFamPlan(familyPlanItemDto.getIndAddressedFamPlan());
		}
		sessionFactory.getCurrentSession().save(familyPlanItem);

	}

	/**
	 * 
	 * Method Name: deleteFamilyPlanEvalItem Method Description: deletes the
	 * record from FAMILY_PLAN_ITEM table
	 * 
	 * @param familyPlanItemDto
	 */
	@Override
	public void deleteFamilyPlanEvalItem(FamilyPlanEvalItemDto familyPlanEvalItemDto) {
		FamilyPlanEvalItem familyPlanEvalItem = (FamilyPlanEvalItem) sessionFactory.getCurrentSession()
				.load(FamilyPlanEvalItem.class, familyPlanEvalItemDto.getIdFamilyPlanEvalItem());
		sessionFactory.getCurrentSession().delete(familyPlanEvalItem);
	}

	/**
	 * 
	 * Method Name: updateFamilyPlanEvalItem Method Description: Updates the
	 * FAMILY_PLAN_EVAL_ITEM table
	 * 
	 * @param familyPlanItemDto
	 */
	@Override
	public void updateFamilyPlanEvalItem(FamilyPlanEvalItemDto familyPlanEvalItemDto) {
		FamilyPlanEvalItem familyPlanEvalItem = new FamilyPlanEvalItem();
		if (!ObjectUtils.isEmpty(familyPlanEvalItemDto.getIdFamilyPlanEvalItem())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanEvalItem.class);
			criteria.add(Restrictions.eq("idFamilyPlanEvalItem", familyPlanEvalItemDto.getIdFamilyPlanEvalItem()));
			criteria.add(Restrictions.eq("capsCase.idCase", familyPlanEvalItemDto.getIdCase()));
			familyPlanEvalItem = (FamilyPlanEvalItem) criteria.uniqueResult();
		} else {
			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class,
					familyPlanEvalItemDto.getIdCase());
			Event event = (Event) sessionFactory.getCurrentSession().load(Event.class,
					familyPlanEvalItemDto.getIdEvent());
			FamilyPlanItem familyPlanItem = (FamilyPlanItem) sessionFactory.getCurrentSession()
					.load(FamilyPlanItem.class, familyPlanEvalItemDto.getIdFamilyPlanItem());
			familyPlanEvalItem.setCapsCase(capsCase);
			familyPlanEvalItem.setEvent(event);
			familyPlanEvalItem.setFamilyPlanItem(familyPlanItem);
		}
		familyPlanEvalItem.setTxtItemEvaluation(familyPlanEvalItemDto.getTxtItemEvaluation());
		familyPlanEvalItem.setTxtNewConcerns(familyPlanEvalItemDto.getTxtNewConcerns());
		familyPlanEvalItem.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(familyPlanEvalItem);
	}

	/**
	 * 
	 * Method Name: updateFamilPlanTasks Method Description:Query the goals for
	 * a particular item from the database and updates the FAMILY_PLAN_TASK
	 * table
	 * 
	 * @param familyPlanTaskDto
	 * @param idEvalEvent
	 */
	@Override
	public void updateFamilPlanTasks(List<FamilyPlanTaskDto> familyPlanTaskDtoList, Long idEvalEvent) {
		familyPlanTaskDtoList.forEach(familyPlanTaskDto -> {
			FamilyPlanTask familyPlanTask = (FamilyPlanTask) sessionFactory.getCurrentSession()
					.load(FamilyPlanTask.class, familyPlanTaskDto.getIdFamilyPlanTask());
			if (ServiceConstants.Y.equals(familyPlanTaskDto.getIndNoLongerNeeded())) {
				familyPlanTask.setDtNoLongerNeeded(new Date());
			} else {
				familyPlanTask.setDtNoLongerNeeded(null);
			}
			if (ServiceConstants.Y.equals(familyPlanTaskDto.getIndTaskCompleted())) {
				familyPlanTask.setDtCompleted(new Date());
			} else {
				familyPlanTask.setDtCompleted(null);
			}
			if ((ObjectUtils.isEmpty(familyPlanTaskDto.getIndNoLongerNeeded())
					|| ServiceConstants.N.equals(familyPlanTaskDto.getIndNoLongerNeeded()))
					&& (ObjectUtils.isEmpty(familyPlanTaskDto.getIndTaskCompleted())
							|| ServiceConstants.N.equals(familyPlanTaskDto.getIndTaskCompleted()))) {
				familyPlanTask.setIdCompletedEvent(idEvalEvent);
			} else {
				familyPlanTask.setIdCompletedEvent(null);
			}
			sessionFactory.getCurrentSession().save(familyPlanTask);
		});

	}

	/**
	 * 
	 * Method Name: updateEventStatusWithoutTimestamp Method Description:Updates
	 * the event information without using a Timestamp check.
	 * 
	 * @param cdEventStatus
	 * @param idEvent
	 */
	@Override
	public void updateEventStatusWithoutTimestamp(String cdEventStatus, Long idEvent) {
		Event event = (Event) sessionFactory.getCurrentSession().load(Event.class, idEvent);
		event.setCdEventStatus(cdEventStatus);
		sessionFactory.getCurrentSession().save(event);
	}

	/**
	 * 
	 * Method Name: deleteFamilyPlanItem Method Description: This helps to
	 * delete the items section.
	 * 
	 * @param familyPlanEvalItemDto
	 */
	@Override
	public void deleteFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto) {
		deleteFamilyPlanEvalItems(familyPlanItemDto);
		updateFamilyPlanItem(familyPlanItemDto, ServiceConstants.REQ_FUNC_CD_DELETE);
	}

	/**
	 * 
	 * Method Name: deleteFamilyPlanEvalItems Method Description: deletes family
	 * plan eval items
	 * 
	 * @param familyPlanItemDto
	 */
	private void deleteFamilyPlanEvalItems(FamilyPlanItemDto familyPlanItemDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanEvalItem.class);
		criteria.add(
				Restrictions.eqOrIsNull("familyPlanItem.idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem()));
		List<FamilyPlanEvalItem> itemsList = criteria.list();
		itemsList.forEach(o -> {
			sessionFactory.getCurrentSession().delete(o);
		});
	}

	/**
	 * 
	 * Method Name: getFamilyPlanItemLastUpdateTime Method Description: This
	 * helps to get last update time of family plan item
	 * 
	 * @param idFamilyPlanItem
	 * @return
	 */
	@Override
	public Date getFamilyPlanItemLastUpdateTime(Long idFamilyPlanItem) {
		FamilyPlanItem familyPlanItem = (FamilyPlanItem) sessionFactory.getCurrentSession().load(FamilyPlanItem.class,
				idFamilyPlanItem);
		return familyPlanItem.getDtLastUpdate();
	}
}