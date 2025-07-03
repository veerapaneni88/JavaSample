/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Oct 7, 2017- 6:24:14 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.childplanparticipant.daoimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ChildPlanParticip;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.SsccChildPlanParticip;
import us.tx.state.dfps.common.dto.ChildPlanParticipantDto;
import us.tx.state.dfps.service.childplanparticipant.dao.ChildPlanParticipantDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * services related to ChildPlan. Implementation for Clss20dDao.java Oct 7,
 * 2017- 6:24:14 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ChildPlanParticipantDaoImpl implements ChildPlanParticipantDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventDao eventDao;

	/**
	 * Method Name: fetchChildPlanParticipant Method Description:Queries the
	 * Child Plan participant table and retrieves a row corresponding to an
	 * Event Id
	 * 
	 * @param ChildPlanParticipantDODto
	 * @return ChildPlanParticipantDODto
	 */
	@Override
	public List<ChildPlanParticipantDto> fetchChildPlanParticipant(Long idChildPlanEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlanParticip.class);

		criteria.add(Restrictions.eq("event.idEvent", idChildPlanEvent));

		List<ChildPlanParticip> childPlanParticipList = criteria.list();

		List<ChildPlanParticipantDto> childPlanParticipDtoList = new ArrayList<ChildPlanParticipantDto>();

		if (!ObjectUtils.isEmpty(childPlanParticipList)) {
			childPlanParticipList.forEach(childPlanParticip -> {
				ChildPlanParticipantDto childPlanParticipDto = new ChildPlanParticipantDto();

				childPlanParticipDto.setIdChildPlanPart(childPlanParticip.getIdChildPlanPart());
				childPlanParticipDto.setDtLastUpdate(childPlanParticip.getDtLastUpdate());
				childPlanParticipDto.setIdPerson(childPlanParticip.getIdPerson());
				childPlanParticipDto.setIdEvent(childPlanParticip.getEvent().getIdEvent());
				childPlanParticipDto.setCdCspPartNotifType(childPlanParticip.getCdCspPartNotifType());
				childPlanParticipDto.setCdCspPartType(childPlanParticip.getCdCspPartType());
				childPlanParticipDto.setDtCspDateNotified(childPlanParticip.getDtCspDateNotified());
				childPlanParticipDto.setDtCspPartCopyGiven(childPlanParticip.getDtCspPartCopyGiven());
				childPlanParticipDto.setDtCspPartParticipate(childPlanParticip.getDtCspPartParticipate());
				childPlanParticipDto.setNmCspPartFull(childPlanParticip.getNmCspPartFull());
				childPlanParticipDto.setSdsCspPartRelationship(childPlanParticip.getSdsCspPartRelationship());
				childPlanParticipDto.setIdCase(childPlanParticip.getIdCase());
				childPlanParticipDtoList.add(childPlanParticipDto);

			});
		}

		return childPlanParticipDtoList;
	}
	
	@Override
	public ChildPlanParticipantDto fetchSsccChildPlanParticipant(Long idSsccChildPlanParticip) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlanParticip.class);

		criteria.add(Restrictions.eq("idSsccChildPlanParticip", idSsccChildPlanParticip));

		SsccChildPlanParticip ssccChildPlanParticip = (SsccChildPlanParticip) criteria.uniqueResult();

		ChildPlanParticipantDto childPlanParticipDto = new ChildPlanParticipantDto();

		childPlanParticipDto.setIdChildPlanPart(ssccChildPlanParticip.getIdSsccChildPlanParticip());
		childPlanParticipDto.setDtLastUpdate(ssccChildPlanParticip.getDtLastUpdate());
		childPlanParticipDto.setIdPerson(ssccChildPlanParticip.getIdPerson());
		childPlanParticipDto.setCdCspPartNotifType(ssccChildPlanParticip.getCdCspPartNotifType());
		childPlanParticipDto.setCdCspPartType(ssccChildPlanParticip.getCdCspPartType());
		childPlanParticipDto.setDtCspDateNotified(ssccChildPlanParticip.getDtCspDateNotified());
		childPlanParticipDto.setDtCspPartCopyGiven(ssccChildPlanParticip.getDtCspPartCopyGiven());
		childPlanParticipDto.setDtCspPartParticipate(ssccChildPlanParticip.getDtCspPartParticipate());
		childPlanParticipDto.setNmCspPartFull(ssccChildPlanParticip.getNmCspPartFulls());
		childPlanParticipDto.setSdsCspPartRelationship(ssccChildPlanParticip.getDsCspPartRelationship());

		return childPlanParticipDto;
	}

	@Override
	public Long saveOrUpdateChildPlanParticip(ChildPlanParticipantDto childPlanParticipDto) {

		Long idChildPlan = childPlanParticipDto.getIdChildPlanPart();
		ChildPlanParticip childPlanParticip = null;
		if (null != idChildPlan) {
			childPlanParticip = (ChildPlanParticip) sessionFactory.getCurrentSession().get(ChildPlanParticip.class,
					childPlanParticipDto.getIdChildPlanPart());
		} else {
			Event event = null;
			if (childPlanParticipDto.getIdEvent() != null)
				event = (Event) sessionFactory.getCurrentSession().get(Event.class, childPlanParticipDto.getIdEvent());
			childPlanParticip = new ChildPlanParticip();
			childPlanParticip.setIdPerson(childPlanParticipDto.getIdPerson());
			childPlanParticip.setEvent(event);

		}
		Calendar cal = Calendar.getInstance();

		if (null != idChildPlan)
			childPlanParticip.setIdChildPlanPart(childPlanParticipDto.getIdChildPlanPart());
		childPlanParticip.setIdCase(childPlanParticipDto.getIdCase());
		childPlanParticip.setCdCspPartNotifType(childPlanParticipDto.getCdCspPartNotifType());
		childPlanParticip.setCdCspPartType(childPlanParticipDto.getCdCspPartType());
		childPlanParticip.setDtCspDateNotified(childPlanParticipDto.getDtCspDateNotified());
		childPlanParticip.setDtCspPartCopyGiven(childPlanParticipDto.getDtCspPartCopyGiven());
		childPlanParticip.setDtCspPartParticipate(childPlanParticipDto.getDtCspPartParticipate());
		childPlanParticip.setNmCspPartFull(childPlanParticipDto.getNmCspPartFull());
		childPlanParticip.setSdsCspPartRelationship(childPlanParticipDto.getSdsCspPartRelationship());
		childPlanParticip.setDtLastUpdate(cal.getTime());

		if (null != idChildPlan) {

			sessionFactory.getCurrentSession().update(childPlanParticip);
		} else {
			idChildPlan = (Long) sessionFactory.getCurrentSession().save(childPlanParticip);
		}

		return idChildPlan;
	}

	@Override
	public String deleteChildPlanParticip(Long idChildPlanParticp) {
		ChildPlanParticip childPlanParticip = (ChildPlanParticip) sessionFactory.getCurrentSession()
				.get(ChildPlanParticip.class, idChildPlanParticp);
		sessionFactory.getCurrentSession().delete(childPlanParticip);
		return ServiceConstants.SUCCESS;
	}

}
