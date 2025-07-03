package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.EventUpdateDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.EventUpdateInDto;

@Repository
public class EventUpdateDaoImpl implements EventUpdateDao {
	@Autowired
	MessageSource messageSource;
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: updateEvent Method Description:Updates event table.
	 * 
	 * @param eventUpdateInDto
	 * @return long @
	 */
	@Override
	public long updateEvent(EventUpdateInDto eventUpdateInDto) {
		List<Event> eventList = new ArrayList<Event>();
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(eventUpdateInDto.getArchInputStruct().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq("stage.idStage", eventUpdateInDto.getUlIdEventStage()));
			eventList = criteria.list();
			if (TypeConvUtil.isNullOrEmpty(eventList)) {
				throw new DataNotFoundException(messageSource.getMessage("ArcErrBadFuncCd", null, Locale.US));
			}
			for (Event event : eventList) {
				event.setDtEventOccurred(eventUpdateInDto.getDtDtEventOccurred());
				sessionFactory.getCurrentSession().saveOrUpdate(event);
			}
		}
		return eventList.size();
	}

}
