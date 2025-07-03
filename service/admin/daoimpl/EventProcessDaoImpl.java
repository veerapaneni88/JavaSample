package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dao.EventProcessDao;
import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.EventOutputDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * deletes,saves and updates the events. Aug 7, 2017- 6:28:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EventProcessDaoImpl implements EventProcessDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	CodesDao codesDao;

	private static final Logger log = Logger.getLogger(EventProcessDaoImpl.class);

	/**
	 * 
	 * Method Name: updateEvent Method Description:this method updates event
	 * table
	 * 
	 * @param eventInputDto
	 * @
	 */
	public void updateEvent(EventInputDto eventInputDto) {

		if (eventInputDto != null) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, eventInputDto.getIdEvent());
			//Modified the code to check only event entity is not empty for warranty defect 11951
			if (!TypeConvUtil.isNullOrEmpty(event)) {
				if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getCdEventStatus())) {
					event.setCdEventStatus(eventInputDto.getCdEventStatus());
				}

				if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getCdEventType())) {
					event.setCdEventType(eventInputDto.getCdEventType());
				}

				// Removing the below piece of code for warranty defect 11020.
				// During update of event, the created person should not be
				// changed. The code can be removed when this file is changed
				// later for any other reason. Keeping the commented code for
				// now for reference
				/*
				 * if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getIdPerson()))
				 * { event.getPerson().setIdPerson(eventInputDto.getIdPerson());
				 * }
				 */

				if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getIdStage())) {
					event.getStage().setIdStage(eventInputDto.getIdStage());
				}
				// Added the code to compare the event occurred date is before R2
				// release date, if Yes then don't change the event description
				// for warranty defect 11954
				Date relDate = codesDao.getAppRelDate(ServiceConstants.R2_REL_CODE);
				if (!event.getDtEventOccurred().before(relDate)) {
					if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getTxtEventDescr())) {
						event.setTxtEventDescr(eventInputDto.getTxtEventDescr());
					}
				}

				if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getCdTask())) {
					event.setCdTask(eventInputDto.getCdTask());
				}

				event.setDtEventModified(new Date());

				if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getIdEvent())) {
					event.setIdEvent(eventInputDto.getIdEvent());
				}

				event.setDtLastUpdate(new Date());

				if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getDtDtEventOccurred())) {
					String cdEventType = eventInputDto.getCdEventType();
					if (!(ServiceConstants.PLA_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.REF_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.LES_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.REMOVAL_EVENT.equalsIgnoreCase(cdEventType)
							|| ServiceConstants.LOC_EVENT.equalsIgnoreCase(cdEventType))) {
						event.setDtEventOccurred(eventInputDto.getDtDtEventOccurred());
					}
				}

				sessionFactory.getCurrentSession().saveOrUpdate(event);
			}
		}
	}

	/**
	 * 
	 * Method Name: deleteEvent Method Description:this method deletes the event
	 * 
	 * @param eventInputDto
	 * @
	 */
	public void deleteEvent(EventInputDto eventInputDto) {

		if (!TypeConvUtil.isNullOrEmpty(eventInputDto)) {

			Event event = new Event();
			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getIdEvent())) {
				event = (Event) sessionFactory.getCurrentSession().get(Event.class, eventInputDto.getIdEvent());
			}
			if (!ObjectUtils.isEmpty(event))
				sessionFactory.getCurrentSession().delete(event);

		}
	}

	/**
	 * 
	 * Method Name: saveEvent Method Description:This method insert into event
	 * table.
	 * 
	 * @param eventInputDto
	 * @return @
	 */
	public Long saveEvent(EventInputDto eventInputDto) {
		Long eventID = ServiceConstants.ZERO_VAL;
		if (!TypeConvUtil.isNullOrEmpty(eventInputDto)) {

			Event event = new Event();

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getCdEventStatus())) {
				event.setCdEventStatus(eventInputDto.getCdEventStatus());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getCdEventType())) {
				event.setCdEventType(eventInputDto.getCdEventType());
			}

			// change made by Lubaba
			event.setDtLastUpdate((!TypeConvUtil.isNullOrEmpty(eventInputDto.getEventLastUpdate()))
					? eventInputDto.getEventLastUpdate() : new Date());

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getDtDtEventCreated())) {
				event.setDtEventCreated(eventInputDto.getDtDtEventCreated());
			} else {
				event.setDtEventCreated(new Date());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getIdPerson())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						eventInputDto.getIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("person.personlist.data", null, Locale.US));
				}
				event.setPerson(person);
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getIdStage())) {
				Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, eventInputDto.getIdStage());
				if (TypeConvUtil.isNullOrEmpty(stage)) {
					throw new DataNotFoundException(
							messageSource.getMessage("record.not.found.stage", null, Locale.US));
				}
				event.setStage(stage);
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getTxtEventDescr())) {
				event.setTxtEventDescr(eventInputDto.getTxtEventDescr());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getCdTask())) {
				event.setCdTask(eventInputDto.getCdTask());
			}

			if (!TypeConvUtil.isNullOrEmpty(eventInputDto.getDtDtEventOccurred())) {
				String cdEventType = eventInputDto.getCdEventType();
				if (!(ServiceConstants.PLA_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.REF_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.LES_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.REMOVAL_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.LOC_EVENT.equalsIgnoreCase(cdEventType))) {
					event.setDtEventOccurred(eventInputDto.getDtDtEventOccurred());
				}
			}

			event.setDtEventModified(new Date());

			// change made by Lubaba
			log.debug(event.getDtEventCreated());
			log.debug("inside save : dt " + event.getDtLastUpdate());
			sessionFactory.getCurrentSession().save(event);
			eventID = event.getIdEvent();
		}
		return eventID;

	}

	/**
	 * 
	 * Method Name: ccmn46dAUDdam Method Description:This method is
	 * update,delete and insert in event table.
	 * 
	 * @param eventInputDto
	 * @return EventOutputDto @
	 */
	@Override
	public EventOutputDto ccmn46dAUDdam(EventInputDto eventInputDto) {
		log.debug("Entering method ccmn46dAUDdam in Ccmn46dDaoImpl");

		EventOutputDto eventOutputDto = new EventOutputDto();
		switch (eventInputDto.getReqFunctionCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
		case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
			eventOutputDto.setIdEvent(saveEvent(eventInputDto));
			break;

		case ServiceConstants.REQ_FUNC_CD_UPDATE: {
			updateEvent(eventInputDto);
			eventOutputDto.setIdEvent(eventInputDto.getIdEvent());
			break;
		}

		case ServiceConstants.REQ_FUNC_CD_DELETE: {
			deleteEvent(eventInputDto);
			eventOutputDto.setIdEvent(eventInputDto.getIdEvent());
			break;
		}

		}
		eventOutputDto.setRowCount(ServiceConstants.Zero);
		log.debug("Exiting method ccmn46dAUDdam in Ccmn46dDaoImpl");
		return eventOutputDto;
	}
}
