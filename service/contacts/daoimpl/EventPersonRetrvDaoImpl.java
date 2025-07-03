package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.EventPersonRetrvDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.EventPersonRetrvInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.EventPersonRetrvArrayOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.EventPersonRetrvOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.EventPersonRetrvRowOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:EventPersonRetrvDaoImpl Oct 31, 2017- 3:10:19 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EventPersonRetrvDaoImpl implements EventPersonRetrvDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getPersonIdsForStage Method Description:EVENT_PERSON_LINK
	 * retrieval.
	 * 
	 * @param eventPersonRetrvInDto
	 * @return EventPersonRetrvOutDto @
	 */
	@Override
	public EventPersonRetrvOutDto getPersonIdsForStage(EventPersonRetrvInDto eventPersonRetrvInDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);

		criteria.add(Restrictions.eq("event.idEvent", eventPersonRetrvInDto.getUlIdEvent()));

		List<EventPersonLink> eventPersonLinkList = criteria.list();

		if (TypeConvUtil.isNullOrEmpty(eventPersonLinkList)) {
			throw new DataNotFoundException(messageSource.getMessage("eventPersonLinkList.notFound", null, Locale.US));
		}
		EventPersonRetrvOutDto eventPersonRetrvOutDto = new EventPersonRetrvOutDto();
		EventPersonRetrvArrayOutDto eventPersonRetrvArrayOutDto = new EventPersonRetrvArrayOutDto();
		for (EventPersonLink eventPersonLink : eventPersonLinkList) {

			EventPersonRetrvRowOutDto eventPersonRetrvRowOutDto = new EventPersonRetrvRowOutDto();
			eventPersonRetrvRowOutDto.setTsLastUpdate(eventPersonLink.getDtLastUpdate());
			eventPersonRetrvRowOutDto.setUlIdPerson(eventPersonLink.getPerson().getIdPerson());
			eventPersonRetrvRowOutDto.setcIndPersRmvlNotified(eventPersonLink.getIndPersRmvlNotified());
			eventPersonRetrvRowOutDto.setcIndKinNotifChild(eventPersonLink.getIndKinNotifChild());
			eventPersonRetrvRowOutDto.setNotices(eventPersonLink.getIndLanguage());
			eventPersonRetrvRowOutDto.setDistributionMethod(eventPersonLink.getIndDstrbutnMthd());
			if (eventPersonRetrvArrayOutDto.getEventPersonRetrvRowOutDtoList().size() >= 100) {
				throw new DataNotFoundException(messageSource.getMessage("Index.Out.of.bound", null, Locale.US));
			}
			eventPersonRetrvArrayOutDto.getEventPersonRetrvRowOutDtoList().add(eventPersonRetrvRowOutDto);

		}

		eventPersonRetrvOutDto.setPersonRetrvArrayOutDto(eventPersonRetrvArrayOutDto);
		return eventPersonRetrvOutDto;
	}
}
