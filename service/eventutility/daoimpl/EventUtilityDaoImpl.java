package us.tx.state.dfps.service.eventutility.daoimpl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.eventutility.dao.EventUtilityDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Used to
 * update the status of events. Sep 7, 2017- 11:42:10 AM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class EventUtilityDaoImpl implements EventUtilityDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventUtilityDaoImpl.class);

	@Autowired
	@Value("${EventUtilityDaoImpl.eventExists}")
	private transient String eventExistsQuery;

	@Autowired
	@Value("${EventUtilityDaoImpl.getINVConclusionStatus}")
	private transient String getINVConclusionStatus;

	/**
	 * This method is used to update CD_EVENT_STATUS with provided events
	 * 
	 * @param eventsList
	 * @return long @
	 */

	@Override
	public long updateEventStatus(List<Event> eventsList) {
		log.debug(ServiceConstants.ENTERING_METHOD_UPDATE_EVENT_STATUS_IN_EVENTUTILITYDAOIMPL);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);

		for (Event event : eventsList) {

			criteria.add(Restrictions.eq(ServiceConstants.IDEVENT, event.getIdEvent()));

			Event status = (Event) criteria.uniqueResult();

			status.setCdEventStatus(event.getCdEventStatus());

			sessionFactory.getCurrentSession().saveOrUpdate(status);

		}

		return criteria.list().size();

	}

	/**
	 * This method is used to update CD_EVENT_STATUS with provided events and
	 * status
	 * 
	 * @param eventsList,status
	 * @return long @
	 */

	@Override
	public long updateEventStatus(List<Event> eventsList, String status) {
		log.debug(ServiceConstants.ENTERING_METHOD_UPDATE_EVENT_STATUS_IN_EVENTUTILITYDAOIMPL);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);

		for (Event event : eventsList) {

			criteria.add(Restrictions.eq(ServiceConstants.IDEVENT, event.getIdEvent()));

			Event stat = (Event) criteria.uniqueResult();

			stat.setCdEventStatus(status);
			sessionFactory.getCurrentSession().saveOrUpdate(stat);

		}
		return criteria.list().size();

	}

	/**
	 * This method is used to update CD_EVENT_STATUS with provided idEvent and
	 * cdEventStatus status
	 * 
	 * @param idevent,cdEventStatus
	 * @return long @
	 */

	@Override
	public long updateEventStatus(Long idEvent, String cdEventStatus) {
		log.debug(ServiceConstants.ENTERING_METHOD_UPDATE_EVENT_STATUS_IN_EVENTUTILITYDAOIMPL);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);

		criteria.add(Restrictions.eq(ServiceConstants.IDEVENT, idEvent));

		Event stat = (Event) criteria.uniqueResult();

		stat.setCdEventStatus(cdEventStatus);

		sessionFactory.getCurrentSession().saveOrUpdate(stat);

		return criteria.list().size();

	}

	/**
	 * This method is used to check if the event exists for the provided Event
	 * bean
	 * 
	 * @param eventBean
	 * @return boolean @
	 */

	@Override
	public boolean eventExists(EventValueDto eventBean) {
		log.debug("Entering method invalidatePendingStageClosure in EventUtilityDaoImpl");
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(eventExistsQuery)
				.setParameter("h_IdEventStage", eventBean.getIdStage())
				.setParameter("h_CdEventType", eventBean.getCdEventType())
				.setParameter("h_CdTask", eventBean.getCdEventTask())
				.setParameter("h_CdEventStatus", eventBean.getCdEventStatus());

		BigDecimal resultCount = (BigDecimal) query.uniqueResult();

		if (resultCount.compareTo(BigDecimal.ZERO) > 0) {
			return true;
		}

		else {
			return false;
		}

	}

	/**
	 * Method Name: iNVConclusionStatus Method Description: This method Retrieve
	 * the Status of the Investigation Conclusion Event
	 * 
	 * @param idCase
	 * @return String @
	 */
	@Override
	public String getINVConclusionStatus(long idCase) {
		String status = ServiceConstants.EMPTY_STRING;
		List<String> statusList = (List<String>) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getINVConclusionStatus)
				.setParameter("idCase", idCase)).addScalar("cdEventStatus", StandardBasicTypes.STRING).list();
		if(!ObjectUtils.isEmpty(statusList) && 0 < statusList.size()){
			status = statusList.get(0);
		}
		return status;
	}
}
