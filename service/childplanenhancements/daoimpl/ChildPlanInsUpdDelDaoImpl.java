package us.tx.state.dfps.service.childplanenhancements.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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

import us.tx.state.dfps.common.domain.ChildPlan;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ChildPlanParticipantDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAUDEvtDetailDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanInsUpdDelOutputDto;
import us.tx.state.dfps.service.childplanenhancements.dao.ChildPlanInsUpdDelDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateLikeExpression;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Updates
 * ChildPlan Nov 8, 2017- 5:38:02 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class ChildPlanInsUpdDelDaoImpl implements ChildPlanInsUpdDelDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ChildPlanInsUpdDelDaoImpl.getParticipants}")
	private String getParticipants;

	@Value("${ChildPlanInsUpdDelDaoImpl.updateDateCopyGiven}")
	private String updateDateCopyGivenSql;

	private static final Logger log = Logger.getLogger(ChildPlanInsUpdDelDaoImpl.class);

	/**
	 * Method Name: childPlanUpdateOrDelete Method Description: Updates
	 * ChildPlan
	 * 
	 * @param childPlanAUDDetailDto
	 * @return ChildPlanInsUpdDelOutputDto
	 */
	@Override
	public ChildPlanInsUpdDelOutputDto childPlanUpdateOrDelete(ChildPlanAUDEvtDetailDto childPlanAUDDetailDto) {
		log.debug("Entering method childPlanUpdateOrDelete in ChildPlanInsUpdDelDaoImpl");
		ServiceInputDto serviceInputDto = childPlanAUDDetailDto.getArchInputStruct();
		ChildPlanInsUpdDelOutputDto childPlanInsUpdDelOutputDto = null;
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(serviceInputDto.getCreqFuncCd())) {

			ChildPlan childPlan = new ChildPlan();
			childPlan.setIdChildPlanEvent(childPlanAUDDetailDto.getIdChildPlanEvent());
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					childPlanAUDDetailDto.getIdPerson());
			childPlan.setPerson(person);
			populateChildPlan(childPlan, childPlanAUDDetailDto);

			childPlan.setCdSsccPurpose(childPlanAUDDetailDto.getCdSsccPurpose());

			childPlanInsUpdDelOutputDto = (ChildPlanInsUpdDelOutputDto) sessionFactory.getCurrentSession()
					.save(childPlan);

		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(serviceInputDto.getCreqFuncCd())) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
			criteria.add(Restrictions.eq("idChildPlanEvent", childPlanAUDDetailDto.getIdChildPlanEvent()));
			ChildPlan childPlan = (ChildPlan) criteria.uniqueResult();

			// Update if only child plan is available

			if (!ObjectUtils.isEmpty(childPlan)
					&& childPlan.getDtLastUpdate().compareTo(childPlanAUDDetailDto.getDtLastUpdate()) == 0) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						childPlanAUDDetailDto.getIdPerson());
				childPlan.setPerson(person);
				populateChildPlan(childPlan, childPlanAUDDetailDto);
				childPlanInsUpdDelOutputDto = (ChildPlanInsUpdDelOutputDto) sessionFactory.getCurrentSession()
						.save(childPlan);
			}

		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(serviceInputDto.getCreqFuncCd())) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
			criteria.add(Restrictions.eq("idChildPlanEvent", childPlanAUDDetailDto.getIdChildPlanEvent()));
			criteria.add(new DateLikeExpression("dtLastUpdate",
					TypeConvUtil.formatDate(childPlanAUDDetailDto.getDtLastUpdate())));
			ChildPlan childPlan = (ChildPlan) criteria.uniqueResult();
			sessionFactory.getCurrentSession().delete(childPlan);

		}

		return childPlanInsUpdDelOutputDto;
	}

	public void populateChildPlan(ChildPlan childPlan, ChildPlanAUDEvtDetailDto childPlanAUDDetailDto) {
		childPlan.setCdCspPlanPermGoal(childPlanAUDDetailDto.getCdCspPlanPermGoal());
		childPlan.setCdCspPlanType(childPlanAUDDetailDto.getCdCspPlanType());
		childPlan.setDtCspPermGoalTarget(childPlanAUDDetailDto.getDtCspPermGoalTarget());
		childPlan.setDtCspNextReview(childPlanAUDDetailDto.getDtCspNextReview());
		childPlan.setTxtCspLengthOfStay(childPlanAUDDetailDto.getCspLengthOfStay());
		childPlan.setTxtCspLosDiscrepancy(childPlanAUDDetailDto.getCspDiscrepancy());
		childPlan.setTxtCspParticipComment(childPlanAUDDetailDto.getCspParticipComment());
		childPlan.setDtCspPlanCompleted(childPlanAUDDetailDto.getDtCspPlanCompleted());
		childPlan.setDtInitialTransitionPlan(childPlanAUDDetailDto.getDtInitlTransitionPlan());
		childPlan.setIndParentsParticipated(childPlanAUDDetailDto.getIndParentsParticipated());
		childPlan.setTxtInfoNotAvail(childPlanAUDDetailDto.getInfoNotAvail());
		childPlan.setTxtOtherAssmt(childPlanAUDDetailDto.getOtherAssmt());
		childPlan.setIndNoConGoal(childPlanAUDDetailDto.getIndNoConGoal());
		childPlan.setTxtNoConGoals(childPlanAUDDetailDto.getNoConGoal());
	}

	public List<ChildPlanParticipantDto> getChildParticipants(Long idEvent) {
		List<ChildPlanParticipantDto> childPlanParticipDtolist = new ArrayList<>();

		Query query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getParticipants)
				.setParameter("idEvent", idEvent)).addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("email", StandardBasicTypes.STRING)
						.addScalar("idChildPlanPart", StandardBasicTypes.LONG)
						// .addScalar("nmFosterParent",
						// StandardBasicTypes.STRING)
						.addScalar("cdDstrbutnMthd", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ChildPlanParticipantDto.class));
		childPlanParticipDtolist = (List<ChildPlanParticipantDto>) query.list();
		return childPlanParticipDtolist;

	}

	public Boolean updateDateCopyProvided(List<Long> participantIds) {

		Boolean updateSucces = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createQuery(updateDateCopyGivenSql);
		query.setDate("dateCopyProvided", new Date());
		query.setParameterList("participantIds", participantIds);
		int result = query.executeUpdate();
		if (result > 0) {
			updateSucces = Boolean.TRUE;
		}
		return updateSucces;

	}
}
