package us.tx.state.dfps.service.childplanrtrv.daoimpl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildPlan;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEventDto;
import us.tx.state.dfps.service.childplanrtrv.dao.FetchPlanDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * implements the methods declared in FetchPlanDao May 8, 2018- 1:20:31 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class FetchPlanDaoImpl implements FetchPlanDao {

	@Autowired
	private SessionFactory sessionFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.childplanrtrv.dao.FetchPlanDao#getChildPlanEvent
	 * (java.lang.Long)
	 */
	@Override
	public ChildPlanEventDto getChildPlanEvent(Long idChildPlanEvent) {

		/*Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
		criteria.add(Restrictions.eq("idChildPlanEvent", idChildPlanEvent));
		ChildPlan childPlan = (ChildPlan) criteria.uniqueResult();*/
		
		ChildPlan childPlan = (ChildPlan) sessionFactory.getCurrentSession().get(ChildPlan.class, idChildPlanEvent);

		ChildPlanEventDto childPlanEventDto = new ChildPlanEventDto();

		if (!ObjectUtils.isEmpty(childPlan)) {
			childPlanEventDto.setIdChildPlanEvent(childPlan.getIdChildPlanEvent());
			childPlanEventDto.setDtLastUpdate(childPlan.getDtLastUpdate());
			childPlanEventDto.setIdPerson(childPlan.getPerson().getIdPerson());
			childPlanEventDto.setCdCspPlanPermGoal(childPlan.getCdCspPlanPermGoal());
			childPlanEventDto.setCdCspPlanType(childPlan.getCdCspPlanType());
			childPlanEventDto.setCdSsccPlanPurpose(childPlan.getCdSsccPurpose());
			childPlanEventDto.setDtCspPermGoalTarget(childPlan.getDtCspPermGoalTarget());
			childPlanEventDto.setDtCspNextReview(childPlan.getDtCspNextReview());
			childPlanEventDto.setCspLengthOfStay(childPlan.getTxtCspLengthOfStay());
			childPlanEventDto.setCspDiscrepancy(childPlan.getTxtCspLosDiscrepancy());
			childPlanEventDto.setCspParticipComment(childPlan.getTxtCspParticipComment());
			childPlanEventDto.setDtCspPlanCompleted(childPlan.getDtCspPlanCompleted());
			childPlanEventDto.setDtInitTransitionPlan(childPlan.getDtInitialTransitionPlan());
			childPlanEventDto.setIndParentsParticipated(childPlan.getIndParentsParticipated());
			childPlanEventDto.setInfoNotAvail(childPlan.getTxtInfoNotAvail());
			childPlanEventDto.setOtherAssmt(childPlan.getTxtOtherAssmt());
			childPlanEventDto.setIndNoConGoal(childPlan.getIndNoConGoal());
			childPlanEventDto.setNoConGoal(childPlan.getTxtNoConGoals());

		}
		return childPlanEventDto;
	}

}
