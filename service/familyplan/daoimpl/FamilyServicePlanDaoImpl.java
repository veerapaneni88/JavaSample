package us.tx.state.dfps.service.familyplan.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.familyplan.dao.FamilyServicePlanDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes calls
 * to database and returns data to service May 2, 2018- 4:01:33 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FamilyServicePlanDaoImpl implements FamilyServicePlanDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FamilyServicePlanDaoImpl.getServicePlanItems}")
	private String getServicePlanItemsSql;

	@Value("${FamilyServicePlanDaoImpl.getServicePlanProblems}")
	private String getServicePlanProblemsSql;

	@Value("${FamilyServicePlanDaoImpl.getServicePlanGoals}")
	private String getServicePlanGoalsSql;

	@Value("${FamilyServicePlanDaoImpl.getPermanencyGoals}")
	private String getPermanencyGoalsSql;

	@Value("${FamilyServicePlanDaoImpl.getCompletionDate}")
	private String getCompletionDateSql;

	/**
	 * Method Name: getServicePlanItems Method Description: Gets all rows from
	 * Service plan table for given id (DAM: CSVC11D)
	 * 
	 * @param idSvcPlanEvent
	 * @param idSvcPlanItem
	 * @return List<ServPlanEvalRecDto>
	 */
	@Override
	public List<ServPlanEvalRecDto> getServicePlanItems(Long idSvcPlanEvent, Long idSvcPlanItem) {
		StringBuilder sb = new StringBuilder();
		sb.append(getServicePlanItemsSql);
		if (ObjectUtils.isEmpty(idSvcPlanEvent) && ServiceConstants.ZERO_VAL.equals(idSvcPlanEvent)) {
			sb.append("WHERE ID_SVC_PLN_ITEM = ");
			sb.append(idSvcPlanItem);
		} else {
			sb.append("WHERE ID_SVC_PLAN_EVENT = ");
			sb.append(idSvcPlanEvent);
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sb.toString())
				.addScalar("idSvcPlnItem", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdSvcPlanProblem", StandardBasicTypes.STRING)
				.addScalar("cdSvcPlanGoal", StandardBasicTypes.STRING)
				.addScalar("cdSvcPlanTask", StandardBasicTypes.STRING)
				.addScalar("cdSvcPlanSvc", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanProblem", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanGoal", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanTask", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanSvc", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanTaskFreq", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanMethodEval", StandardBasicTypes.STRING)
				.addScalar("indSvcPlnTaskCrtOrdr", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanSvcFreq", StandardBasicTypes.STRING)
				.addScalar("indSvcPlnSvcCrtOrdr", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ServPlanEvalRecDto.class));
		return (List<ServPlanEvalRecDto>) query.list();
	}

	/**
	 * Method Name: getServicePlanProblems Method Description: Gets problems
	 * with a certain plan event (DAM: CLSS27D)
	 * 
	 * @param idSvcPlanEvent
	 * @return List<ServPlanEvalRecDto>
	 */
	@Override
	public List<ServPlanEvalRecDto> getServicePlanProblems(Long idSvcPlanEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getServicePlanProblemsSql)
				.addScalar("txtSvcPlanProblem", StandardBasicTypes.STRING)
				.addScalar("idSvcPlanEvent", StandardBasicTypes.LONG).setParameter("idEvent", idSvcPlanEvent)
				.setResultTransformer(Transformers.aliasToBean(ServPlanEvalRecDto.class));
		return (List<ServPlanEvalRecDto>) query.list();
	}

	/**
	 * Method Name: getServicePlanGoals Method Description: Gets goals with a
	 * certain plan event (DAM: CLSS27D)
	 * 
	 * @param idSvcPlanEvent
	 * @return List<ServPlanEvalRecDto>
	 */
	@Override
	public List<ServPlanEvalRecDto> getServicePlanGoals(Long idSvcPlanEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getServicePlanGoalsSql)
				.addScalar("txtSvcPlanGoal", StandardBasicTypes.STRING)
				.addScalar("idSvcPlanEvent", StandardBasicTypes.LONG).setParameter("idEvent", idSvcPlanEvent)
				.setResultTransformer(Transformers.aliasToBean(ServPlanEvalRecDto.class));
		return (List<ServPlanEvalRecDto>) query.list();
	}

	/**
	 * Method Name: getPermanencyGoals Method Description: Gets child permanency
	 * goals for an event (DAM: CLSC55D)
	 * 
	 * @param idEvent
	 * @return List<PersonDto>
	 */
	@Override
	public List<PersonDto> getPermanencyGoals(Long idEvent) {
		List<PersonDto> personList = new ArrayList<PersonDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCompletionDateSql).setParameter("idEvent",
				idEvent);
		Date completionDate = (Date) query.uniqueResult();
		if (!ObjectUtils.isEmpty(completionDate)) {
			query = sessionFactory.getCurrentSession().createSQLQuery(getPermanencyGoalsSql)
					.addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
					.addScalar("dtChildPlanLastUpdate", StandardBasicTypes.DATE)
					.addScalar("idChPerson", StandardBasicTypes.LONG)
					.addScalar("cspPlanPermGoal", StandardBasicTypes.STRING)
					.addScalar("cdCspPlanType", StandardBasicTypes.STRING)
					.addScalar("dtcspPermGoalTarget", StandardBasicTypes.DATE)
					.addScalar("dtCspNextReview", StandardBasicTypes.DATE)
					.addScalar("txtCspLengthOfStay", StandardBasicTypes.STRING)
					.addScalar("txtCspLosDiscrepancy", StandardBasicTypes.STRING)
					.addScalar("txtCspParticpatnComment", StandardBasicTypes.STRING)
					.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
					.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
					.addScalar("nmPersonLast", StandardBasicTypes.STRING)
					.addScalar("cdPersonSuffix", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
					.setParameter("dtComplete", completionDate)
					.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
			personList = (List<PersonDto>) query.list();
		}
		return personList;
	}

}
