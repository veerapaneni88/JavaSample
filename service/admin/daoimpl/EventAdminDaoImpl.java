package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dao.EventAdminDao;
import us.tx.state.dfps.service.admin.dto.EventDataInputDto;
import us.tx.state.dfps.service.admin.dto.EventDataOutputDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * deletes,saves and updates the events. Aug 7, 2017- 6:28:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EventAdminDaoImpl implements EventAdminDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventAdminDaoImpl.class);

	/**
	 * Description:Updates Event.
	 * 
	 * @param eventInput
	 * @return void
	 */
	public void updateEvent(EventDataInputDto eventDataInputDto) {

		if (eventDataInputDto != null) {
			Date minDate = new Date(eventDataInputDto.getEventLastUpdate().getTime());
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq("idEvent", eventDataInputDto.getIdEvent()));
			Event event = (Event) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(
						messageSource.getMessage("event.not.found.eventId.and.dtLastUpdate", null, Locale.US));
			}
			Date lastUpdate = new Date(event.getDtLastUpdate().getTime());
			if ((lastUpdate.equals(minDate) || lastUpdate.after(minDate)) && lastUpdate.before(maxDate)) {

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getCdEventStatus())) {
					event.setCdEventStatus(eventDataInputDto.getCdEventStatus());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getCdEventType())) {
					event.setCdEventType(eventDataInputDto.getCdEventType());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getIdPerson())) {
					event.getPerson().setIdPerson(eventDataInputDto.getIdPerson());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getIdStage())) {
					event.getStage().setIdStage(eventDataInputDto.getIdStage());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getTxtEventDescr())) {
					event.setTxtEventDescr(eventDataInputDto.getTxtEventDescr());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getCdTask())) {
					event.setCdTask(eventDataInputDto.getCdTask());
				}

				event.setDtEventModified(new Date());

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getIdEvent())) {
					event.setIdEvent(eventDataInputDto.getIdEvent());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getEventLastUpdate())) {
					event.setDtLastUpdate(eventDataInputDto.getEventLastUpdate());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventDataInputDto.getDtDtEventOccurred())) {
					String cdEventType = eventDataInputDto.getCdEventType();
					if (!(ServiceConstants.PLA_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.REF_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.LES_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.REMOVAL_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.LOC_EVENT.equalsIgnoreCase(cdEventType))) {
						event.setDtEventOccurred(eventDataInputDto.getDtDtEventOccurred());
					}
				}

				sessionFactory.getCurrentSession().saveOrUpdate(event);
			} else {
				throw new DataNotFoundException(
						messageSource.getMessage("event.not.found.eventId.and.dtLastUpdate", null, Locale.US));
			}
		}
	}

	/**
	 * Description:Deletes the Event.
	 * 
	 * @param eventInput
	 * @return void
	 */
	public void deleteEvent(EventDataInputDto eventInput) {

		if (!TypeConvUtil.isNullOrEmpty(eventInput)) {

			Event event = new Event();

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getIdEvent())) {
				event.setIdEvent(eventInput.getIdEvent());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getEventLastUpdate())) {
				event.setDtLastUpdate(eventInput.getEventLastUpdate());
			}
			sessionFactory.getCurrentSession().delete(event);
		}
	}

	/**
	 * Description: Saves Event.
	 * 
	 * @param eventInput
	 * @return
	 * @return void
	 */
	public Long saveEvent(EventDataInputDto eventInput) {
		Long eventID = ServiceConstants.ZERO_VAL;
		if (!TypeConvUtil.isNullOrEmpty(eventInput)) {

			Event event = new Event();

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getCdEventStatus())) {
				event.setCdEventStatus(eventInput.getCdEventStatus());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getCdEventType())) {
				event.setCdEventType(eventInput.getCdEventType());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getDtDtEventOccurred())) {
				event.setDtEventOccurred(eventInput.getDtDtEventOccurred());
			} else {
				event.setDtEventOccurred(new Date());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getIdPerson())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, eventInput.getIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("event.person.not.found.personId", null, Locale.US));
				}
				event.setPerson(person);
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getIdStage())) {
				Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, eventInput.getIdStage());
				if (TypeConvUtil.isNullOrEmpty(stage)) {
					throw new DataNotFoundException(
							messageSource.getMessage("event.stage.not.found.stageId", null, Locale.US));
				}
				event.setStage(stage);
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getTxtEventDescr())) {
				event.setTxtEventDescr(eventInput.getTxtEventDescr());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInput.getCdTask())) {
				event.setCdTask(eventInput.getCdTask());
			}
			if (eventInput.getDtDtEventOccurred() != null) {
				event.setDtEventCreated(eventInput.getDtDtEventOccurred());
			} else {
				event.setDtEventCreated(new Date());
			}
			event.setDtEventModified(new Date());
			event.setDtLastUpdate(new Date());

			sessionFactory.getCurrentSession().save(event);
			eventID = event.getIdEvent();
		}
		return eventID;

	}

	/**
	 * Description: Saves, updates,deletes event.
	 * 
	 * @param eventInput
	 * @return eventOutput
	 */
	@Override
	public EventDataOutputDto postEvent(EventDataInputDto eventDataInputDto) {
		log.debug("Entering method postEvent in EventAdminDaoImpl");
		EventDataOutputDto eventOutput = new EventDataOutputDto();
		switch (eventDataInputDto.getReqFunctionCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
		case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
			eventOutput.setIdEvent(saveEvent(eventDataInputDto));
			break;

		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			updateEvent(eventDataInputDto);
			break;

		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteEvent(eventDataInputDto);
			break;
		}
		eventOutput.setRowCount(0);
		log.debug("Exiting method postEvent in EventAdminDaoImpl");
		return eventOutput;
	}
}
