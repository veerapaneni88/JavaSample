package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.DeleteEventPersonLinkDao;
import us.tx.state.dfps.xmlstructs.inputstructs.EventPersonLinkDeleteDto;

@Repository
public class DeleteEventPersonLinkDaoImpl implements DeleteEventPersonLinkDao {
	@Autowired
	MessageSource messageSource;
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: deleteEventPersonLink Method Description: This DAM deletes
	 * all rows from the EVENT_PERSON_LINK table based on an ID-EVENT.
	 * 
	 * @param eventPersonLinkDeleteDto
	 * @return long @
	 */
	@Override
	public long deleteEventPersonLink(EventPersonLinkDeleteDto eventPersonLinkDeleteDto) {
		long Result = 0;
		if (ServiceConstants.REQ_FUNC_CD_DELETE
				.equals(eventPersonLinkDeleteDto.getArchInputStructDto().getCreqFuncCd())) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
			Event event = new Event();
			event.setIdEvent(Long.valueOf(eventPersonLinkDeleteDto.getUlIdEvent()));
			criteria.add(Restrictions.eq("event.idEvent", new Long(eventPersonLinkDeleteDto.getUlIdEvent())));
			List<EventPersonLink> eventPersonLinks = (List<EventPersonLink>) criteria.list();

			for (EventPersonLink eventPersonLink : eventPersonLinks) {
				sessionFactory.getCurrentSession().delete(eventPersonLink);
				Result++;
			}

		}

		return Result;
	}
}
