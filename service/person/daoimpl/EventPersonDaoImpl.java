package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.person.dao.EventPersonDao;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.workload.dto.EventDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Implement class for EventPerson May 31, 2018- 11:17:17 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EventPersonDaoImpl implements EventPersonDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventPersonDaoImpl.getOpenStageEventsForAPerson}")
	private String getOpenStageEventsForAPersonSql;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * Method Name: getOpenStageEventsForAPerson Method Description: Get open
	 * stage event for person
	 * 
	 * @param idClosedPerson
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDto> getOpenStageEventsForAPerson(Long idClosedPerson) {
		List<EventDto> eventDtos = (List<EventDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getOpenStageEventsForAPersonSql).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("cdTask", StandardBasicTypes.STRING).addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idPerson", idClosedPerson)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class)).list();

		return eventDtos;

	}

	/**
	 * 
	 * Method Name: insertIntoEventPersonLinks Method Description: This method
	 * batch inserts into Event_person_link table using EventPersonValueBean
	 * list
	 * 
	 * @param spLinkBeans
	 * @return List<Long>
	 */
	@Override
	public List<Long> insertIntoEventPersonLinks(List<EventPersonDto> spLinkBeans) {
		List<Long> resultList = new ArrayList<>();
		for (EventPersonDto eventPersonDto : spLinkBeans) {
			EventPersonLink eventPersonLink = new EventPersonLink();
			Person person = new Person();
			person.setIdPerson(eventPersonDto.getIdpersonId());
			eventPersonLink.setPerson(person);
			Event event = new Event();
			event.setIdEvent(eventPersonDto.getIdEvent());
			eventPersonLink.setEvent(event);
			eventPersonLink.setIdCase(eventPersonDto.getIdCase());

			Long count = (Long) sessionFactory.getCurrentSession().save(eventPersonLink);
			resultList.add(count);
		}

		return resultList;
	}

}
