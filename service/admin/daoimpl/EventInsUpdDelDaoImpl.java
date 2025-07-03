package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dao.EventInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:EventInsUpdDelDaoImpl Aug 7, 2017- 6:28:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EventInsUpdDelDaoImpl implements EventInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventInsUpdDelDaoImpl.class);

	public EventInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * updateEvent - This method used to update the event based on event id and
	 * last updated date.
	 * 
	 * @param iCcmn46diDto
	 * @return void
	 */
	public void updateEvent(EventInsUpdDelInDto iCcmn46diDto) throws DataNotFoundException {
		if (iCcmn46diDto != null) {
			/*
			 * Date minDate = iCcmn46diDto.getDtEventLastUpdate(); Date maxDate
			 * = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			 */
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq("idEvent", iCcmn46diDto.getIdEvent()));
			Event event = (Event) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(
						messageSource.getMessage("event.not.found.eventId.and.dtLastUpdate", null, Locale.US));
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getCdEventStatus())) {
				event.setCdEventStatus(iCcmn46diDto.getCdEventStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getCdEventType())) {
				event.setCdEventType(iCcmn46diDto.getCdEventType());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getIdPerson())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						iCcmn46diDto.getIdPerson());
				event.setPerson(person);
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getIdStage())) {
				event.getStage().setIdStage(iCcmn46diDto.getIdStage());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getEventDescr())) {
				event.setTxtEventDescr(iCcmn46diDto.getEventDescr());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getCdTask())) {
				event.setCdTask(iCcmn46diDto.getCdTask());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getDtEventLastUpdate())) {
				if (!ObjectUtils.isEmpty(event.getDtLastUpdate())
						&& event.getDtLastUpdate().getTime() != iCcmn46diDto.getDtEventLastUpdate().getTime())
					throw new DataNotFoundException(
							messageSource.getMessage("event.not.found.eventId.and.dtLastUpdate", null, Locale.US));
				event.setDtLastUpdate(iCcmn46diDto.getDtEventLastUpdate());
			}
			event.setDtEventModified(new Date());
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getDtEventOccurred())) {
				String cdEventType = iCcmn46diDto.getCdEventType();
				if (!(ServiceConstants.PLA_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.REF_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.LES_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.REMOVAL_EVENT.equalsIgnoreCase(cdEventType)
						|| ServiceConstants.LOC_EVENT.equalsIgnoreCase(cdEventType))) {
					event.setDtEventOccurred(iCcmn46diDto.getDtEventOccurred());
				}
			}
			sessionFactory.getCurrentSession().saveOrUpdate(event);
		}
	}

	/**
	 * 
	 * Method Name: deleteEvent Method Description:This method performs DELETE
	 * operation on EVENT table.
	 * 
	 * @param iCcmn46diDto
	 */
	public void deleteEvent(EventInsUpdDelInDto iCcmn46diDto) {
		if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto)) {
			Event event = new Event();
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getIdEvent())) {
				event.setIdEvent(iCcmn46diDto.getIdEvent());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getDtEventLastUpdate())) {
				event.setDtLastUpdate(iCcmn46diDto.getDtEventLastUpdate());
			}
			sessionFactory.getCurrentSession().delete(event);
		}
	}

	/**
	 * 
	 * Method Name: saveEvent Method Description:This method performs SAVE
	 * operation on EVENT table.
	 * 
	 * @param iCcmn46diDto
	 */
	public Long saveEvent(EventInsUpdDelInDto iCcmn46diDto) {
		if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto)) {
			Event event = new Event();
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getCdEventStatus())) {
				event.setCdEventStatus(iCcmn46diDto.getCdEventStatus());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getCdEventType())) {
				event.setCdEventType(iCcmn46diDto.getCdEventType());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getDtEventOccurred())) {
				event.setDtEventOccurred(iCcmn46diDto.getDtEventOccurred());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getDtEventLastUpdate())) {
				event.setDtLastUpdate(iCcmn46diDto.getDtEventLastUpdate());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getIdPerson())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						iCcmn46diDto.getIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("event.person.not.found.personId", null, Locale.US));
				}
				event.setPerson(person);
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getIdStage())) {
				Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, iCcmn46diDto.getIdStage());
				if (TypeConvUtil.isNullOrEmpty(stage)) {
					throw new DataNotFoundException(
							messageSource.getMessage("event.stage.not.found.stageId", null, Locale.US));
				}
				event.setStage(stage);
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getEventDescr())) {
				event.setTxtEventDescr(iCcmn46diDto.getEventDescr());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getCdTask())) {
				event.setCdTask(iCcmn46diDto.getCdTask());
			}
			// Defect Fix for 4560
			// event.setDtEventCreated(new Date());
			event.setDtLastUpdate(new Date());
			event.setDtEventCreated(new Date());
			sessionFactory.getCurrentSession().save(event);
			return event.getIdEvent();
		}
		return 0L;
	}

	/**
	 * 
	 * Method Name: ccmn46dAUDdam Method Description: This method will perform
	 * Add, Update and Delete on Event. Service Name : CCMN35S
	 * 
	 * @param iCcmn46diDto
	 * @return EventInsUpdDelOutDto
	 */
	@Override
	public EventInsUpdDelOutDto ccmn46dAUDdam(EventInsUpdDelInDto iCcmn46diDto) {
		log.debug("Entering method EventInsUpdDelQUERYdam in EventInsUpdDelDaoImpl");
		EventInsUpdDelOutDto oCcmn46doDto = new EventInsUpdDelOutDto();
		try {
			switch (iCcmn46diDto.getCdReqFunction()) {
			case ServiceConstants.REQ_FUNC_CD_ADD:
				oCcmn46doDto.setIdEvent(saveEvent(iCcmn46diDto));
				oCcmn46doDto.setRowCount(1);
				break;
			case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
				saveEvent(iCcmn46diDto);
				break;
			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				updateEvent(iCcmn46diDto);
				break;
			case ServiceConstants.REQ_FUNC_CD_DELETE:
				deleteEvent(iCcmn46diDto);
				break;
			}
		} catch (DataNotFoundException e) {
			return null;
		}

		log.debug("Exiting method EventInsUpdDelQUERYdam in EventInsUpdDelDaoImpl");
		return oCcmn46doDto;
	}

	@Override
	public Event checkEventByLastUpdate(EventInsUpdDelInDto iCcmn46diDto) {
		Event event = null;
		if (iCcmn46diDto != null) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria.add(Restrictions.eq("idEvent", iCcmn46diDto.getIdEvent()));
			// Below 2 lines are commented for date value issue in the dto.
			/*
			 * criteria.add(Restrictions.ge("dtLastUpdate",
			 * iCcmn46diDto.getEventLastUpdate()));
			 * criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			 */
			event = (Event) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(event)) {
				return null;
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn46diDto.getDtEventLastUpdate())) {
				if (!ObjectUtils.isEmpty(event.getDtLastUpdate())
						&& event.getDtLastUpdate().getTime() != iCcmn46diDto.getDtEventLastUpdate().getTime())
					return null;
			}
		}
		return event;
	}
}
