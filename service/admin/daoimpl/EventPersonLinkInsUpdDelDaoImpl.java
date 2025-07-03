package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkInsUpdDelInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:EventPersonLinkInsUpdDelDaoImpl Aug 7, 2017- 6:33:06 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class EventPersonLinkInsUpdDelDaoImpl implements EventPersonLinkInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventPersonLinkInsUpdDelDaoImpl.class);

	public EventPersonLinkInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * deleteEventPersonLink - This method is used to delete the
	 * EventPersonLink.
	 * 
	 * @param iCcmn68diDto
	 * @return void
	 */
	public void deleteEventPersonLink(EventPersonLinkInsUpdDelInDto iCcmn68diDto) {
		log.debug("Entering method EventPersonLinkInsUpdDelQUERYdam in EventPersonLinkInsUpdDelDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto)) {
			EventPersonLink eventPersonLink = new EventPersonLink();
			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getIdPerson())) {
				eventPersonLink.setPerson(
						(Person) sessionFactory.getCurrentSession().get(Person.class, iCcmn68diDto.getIdPerson()));
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getTsLastUpdate())) {
				eventPersonLink.setDtLastUpdate(iCcmn68diDto.getTsLastUpdate());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getIdEvent())) {
				eventPersonLink.setEvent(
						(Event) sessionFactory.getCurrentSession().get(Event.class, iCcmn68diDto.getIdEvent()));
			}
			sessionFactory.getCurrentSession().delete(eventPersonLink);
		}
		log.debug("Exiting method EventPersonLinkInsUpdDelQUERYdam in EventPersonLinkInsUpdDelDaoImpl");
	}

	/**
	 * 
	 * Method Name: saveEventPersonLink Method Description:This method will
	 * perform SAVE operation on EventPersonLink table.
	 * 
	 * @param iCcmn68diDto
	 */
	public void saveEventPersonLink(EventPersonLinkInsUpdDelInDto iCcmn68diDto) {
		log.debug("Entering method EventPersonLinkInsUpdDelQUERYdam in EventPersonLinkInsUpdDelDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto)) {
			EventPersonLink eventPersonLink = new EventPersonLink();
			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getTsLastUpdate())) {
				eventPersonLink.setDtLastUpdate(iCcmn68diDto.getTsLastUpdate());
			} else {
				eventPersonLink.setDtLastUpdate(new Date());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getIdPerson())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						iCcmn68diDto.getIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("eventpersonlink.person.not.found.personId", null, Locale.US));
				}
				eventPersonLink.setPerson(person);
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getIdEvent())) {
				Event Event = (Event) sessionFactory.getCurrentSession().get(Event.class, iCcmn68diDto.getIdEvent());
				if (TypeConvUtil.isNullOrEmpty(Event)) {
					throw new DataNotFoundException(
							messageSource.getMessage("eventpersonlink.event.not.found.eventId", null, Locale.US));
				}
				eventPersonLink.setEvent(Event);
			}

			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getCdNotice())) {
				eventPersonLink.setIndLanguage(iCcmn68diDto.getCdNotice());
			}
			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getDistMethod())) {
				eventPersonLink.setIndDstrbutnMthd(iCcmn68diDto.getDistMethod());
			}

			sessionFactory.getCurrentSession().save(eventPersonLink);
		}
		log.debug("Exiting method EventPersonLinkInsUpdDelQUERYdam in EventPersonLinkInsUpdDelDaoImpl");
	}

	/**
	 * 
	 * Method Name: updateEventPersonLink Method Description: This method will
	 * perform add, update and delete on Event Person Link table.
	 * 
	 * @param iCcmn68diDto
	 * @
	 */
	@Override
	public void updateEventPersonLink(EventPersonLinkInsUpdDelInDto iCcmn68diDto) {
		log.debug("Entering method EventPersonLinkInsUpdDelQUERYdam in EventPersonLinkInsUpdDelDaoImpl");
		switch (iCcmn68diDto.getCdReqFunction()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			saveEventPersonLink(iCcmn68diDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
			saveEventPersonLink(iCcmn68diDto);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteEventPersonLink(iCcmn68diDto);
			break;
		}
		log.debug("Exiting method EventPersonLinkInsUpdDelQUERYdam in EventPersonLinkInsUpdDelDaoImpl");
	}
}
