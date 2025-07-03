package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkAdminDao;
import us.tx.state.dfps.service.admin.dto.EventPersonAdminLinkDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Ccmn68dDaoImpl Aug 7, 2017- 6:33:06 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class EventPersonLinkAdminDaoImpl implements EventPersonLinkAdminDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventPersonLinkAdminDaoImpl.class);

	/**
	 * deleteEventPersonLink.
	 * 
	 * @param iCcmn68diDto
	 * @return void
	 */
	public void deleteEventPersonLink(EventPersonAdminLinkDto iCcmn68diDto) {
		log.debug("Entering method deleteEventPersonLink in Ccmn68dDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto)) {

			EventPersonLink eventPersonLink = new EventPersonLink();

			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getUlIdPerson())) {
				eventPersonLink.setPerson(
						(Person) sessionFactory.getCurrentSession().get(Person.class, iCcmn68diDto.getUlIdPerson()));
			}

			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getTsLastUpdate())) {
				eventPersonLink.setDtLastUpdate(iCcmn68diDto.getTsLastUpdate());
			}

			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getUlIdEvent())) {
				eventPersonLink.setEvent(
						(Event) sessionFactory.getCurrentSession().get(Event.class, iCcmn68diDto.getUlIdEvent()));
			}

			sessionFactory.getCurrentSession().delete(eventPersonLink);
		}
		log.debug("Exiting method deleteEventPersonLink in Ccmn68dDaoImpl");
	}

	/**
	 * saveEventPersonLink.
	 * 
	 * @param iCcmn68diDto
	 * @return void
	 */
	public void saveEventPersonLink(EventPersonAdminLinkDto iCcmn68diDto) {

		log.debug("Entering method saveEventPersonLink in Ccmn68dDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto)) {

			EventPersonLink eventPersonLink = new EventPersonLink();

			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getUlIdPerson())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						iCcmn68diDto.getUlIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("eventpersonlink.person.not.found.personId", null, Locale.US));
				}
				eventPersonLink.setPerson(person);
			}

			if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto.getUlIdEvent())) {

				Event Event = (Event) sessionFactory.getCurrentSession().get(Event.class, iCcmn68diDto.getUlIdEvent());

				if (TypeConvUtil.isNullOrEmpty(Event)) {
					throw new DataNotFoundException(
							messageSource.getMessage("eventpersonlink.event.not.found.eventId", null, Locale.US));
				}
				eventPersonLink.setEvent(Event);
			}
			eventPersonLink.setDtLastUpdate(new Date());

			sessionFactory.getCurrentSession().save(eventPersonLink);
		}

		log.debug("Exiting method saveEventPersonLink in Ccmn68dDaoImpl");
	}

	/**
	 * updateEventPersonLink.
	 * 
	 * @param iCcmn68diDto
	 * @return void
	 */
	@Override
	public void updateEventPersonLink(EventPersonAdminLinkDto iCcmn68diDto) {
		log.debug("Entering method ccmn68dAUDdam in Ccmn68dDaoImpl");

		switch (iCcmn68diDto.getReqFunctionCd()) {
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

		log.debug("Exiting method ccmn68dAUDdam in Ccmn68dDaoImpl");
	}

	@Override
	public long modifyEventPersonLink(EventPersonAdminLinkDto ccmn68di) {
		// TODO Auto-generated method stub

		long updatedResult = 0;
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(ccmn68di.getArchInputStruct().getCreqFuncCd())) {
			EventPersonLink eventPersonLink = new EventPersonLink();
			Event event = new Event();
			event.setIdEvent(ccmn68di.getUlIdEvent());
			Person person = new Person();
			person.setIdPerson(ccmn68di.getUlIdPerson());
			eventPersonLink.setPerson(person);
			if (ccmn68di.getcIndPersRmvlNotified() != null) {
				eventPersonLink.setIndPersRmvlNotified(ccmn68di.getcIndPersRmvlNotified());
			}
			if (ccmn68di.getcIndKinNotifChild() != null) {
				eventPersonLink.setIndKinNotifChild(ccmn68di.getcIndKinNotifChild());
			}
			updatedResult = (long) sessionFactory.getCurrentSession().save(eventPersonLink);
		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(ccmn68di.getArchInputStruct().getCreqFuncCd())) {
			deleteEventPersonLink(ccmn68di);
			updatedResult = 1;
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(ccmn68di.getArchInputStruct().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);

			criteria.add(Restrictions.eq("event.idEvent", ccmn68di.getUlIdEvent()));
			criteria.add(Restrictions.eq("person.idPerson", ccmn68di.getUlIdPerson()));

			List<EventPersonLink> eventPersonLinkList = criteria.list();
			for (EventPersonLink personLink : eventPersonLinkList) {
				personLink.setIndPersRmvlNotified(ccmn68di.getcIndPersRmvlNotified());
				personLink.setIndKinNotifChild(ccmn68di.getcIndKinNotifChild());
				personLink.setDtLastUpdate(ccmn68di.getTsLastUpdate());
				sessionFactory.getCurrentSession().saveOrUpdate(personLink);
			}
			updatedResult = eventPersonLinkList.size();
		}
		return updatedResult;
	}
}
