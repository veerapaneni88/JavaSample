package us.tx.state.dfps.service.childplanrtrv.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildPlan;
import us.tx.state.dfps.common.domain.CpConcurrentGoal;
import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;
import us.tx.state.dfps.service.childplanrtrv.dao.FetchConcurrentGoalDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * implements the methods present in FetchConcurrentGoalDao Oct 11, 2017-
 * 5:38:15 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class FetchConcurrentGoalDaoImpl implements FetchConcurrentGoalDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.childplanrtrv.dao.FetchConcurrentGoalDao#
	 * getConcurrentGoals(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ConcurrentGoalDto> getConcurrentGoals(Long idChildPlanEvent) {

		ChildPlan childPlan = (ChildPlan) sessionFactory.getCurrentSession().load(ChildPlan.class, idChildPlanEvent);

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpConcurrentGoal.class);
		criteria.add(Restrictions.eq("childPlan", childPlan));

		List<CpConcurrentGoal> cpConcurrentGoalList = criteria.list();

		List<ConcurrentGoalDto> concurrentGoalDtoList = new ArrayList<ConcurrentGoalDto>();

		if (!ObjectUtils.isEmpty(cpConcurrentGoalList)) {
			cpConcurrentGoalList.forEach(concurrentGoal -> {
				ConcurrentGoalDto concurrentGoalDto = new ConcurrentGoalDto();
				concurrentGoalDto.setCdConcurrentGoal(concurrentGoal.getCdConcurrentGoal());
				concurrentGoalDto.setTsLastUpdate(concurrentGoal.getDtLastUpdate());
				concurrentGoalDto.setIdCase(concurrentGoal.getCapsCase().getIdCase());
				concurrentGoalDto.setIdStage(concurrentGoal.getStage().getIdStage());
				concurrentGoalDto.setIdCpConGoal(concurrentGoal.getIdCpConcurrentGoal());
				concurrentGoalDto.setIdChildPlanEvent(concurrentGoal.getChildPlan().getIdChildPlanEvent());

				concurrentGoalDtoList.add(concurrentGoalDto);

			});
		}

		return concurrentGoalDtoList;
	}

}
