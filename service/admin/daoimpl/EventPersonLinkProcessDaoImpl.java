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
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkProcessDao;
import us.tx.state.dfps.service.admin.dto.EventLinkInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Ccmn68dDaoImpl Aug 7, 2017- 6:33:06 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class EventPersonLinkProcessDaoImpl implements EventPersonLinkProcessDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EventPersonLinkProcessDaoImpl.class);

	/**
	 * deleteEventPersonLink.
	 * 
	 * @param iCcmn68diDto
	 * @return void
	 */
	public void deleteEventPersonLink(EventLinkInDto iCcmn68diDto) {
		log.debug("Entering method deleteEventPersonLink in Ccmn68dDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto)) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
			criteria.add(Restrictions.eq("person.idPerson", iCcmn68diDto.getIdPerson()));
			criteria.add(Restrictions.eq("dtLastUpdate", iCcmn68diDto.getDtLastUpdate()));
			criteria.add(Restrictions.eq("event.idEvent", iCcmn68diDto.getIdEvent()));
			EventPersonLink eventPersonLink = (EventPersonLink) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(eventPersonLink)) {
				throw new DataNotFoundException(
						messageSource.getMessage("eventpersonlink.person.not.found.personId", null, Locale.US));
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
	public void saveEventPersonLink(EventLinkInDto iCcmn68diDto) {

		log.debug("Entering method saveEventPersonLink in Ccmn68dDaoImpl");
		if (!TypeConvUtil.isNullOrEmpty(iCcmn68diDto)) {

			EventPersonLink eventPersonLink = new EventPersonLink();

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
	public void updateEventPersonLink(EventLinkInDto iCcmn68diDto) {
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

	/**
	 * 
	 * Method Name: ccmn68dAUDdam Method Description:this method does
	 * insert,update and delete.
	 * 
	 * @param eventLinkInDto
	 * @return long
	 */
	@Override
	public long ccmn68dAUDdam(EventLinkInDto eventLinkInDto) {

		long updatedResult = ServiceConstants.Zero;
		// Defect 10923
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(eventLinkInDto.getArchInputStruct().getCreqFuncCd())
				|| ServiceConstants.REQ_FUNC_CD_UPDATE.equals(eventLinkInDto.getArchInputStruct().getCreqFuncCd())) {
			
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
			criteria.add(Restrictions.eq("event.idEvent", eventLinkInDto.getIdEvent()));
			criteria.add(Restrictions.eq("person.idPerson", eventLinkInDto.getIdPerson()));

			List<EventPersonLink> eventPersonLinkList = criteria.list();
			if (ObjectUtils.isEmpty(eventPersonLinkList)){
				EventPersonLink eventPersonLink = new EventPersonLink();
				Event event = new Event();
				event.setIdEvent(eventLinkInDto.getIdEvent());
				eventPersonLink.setEvent(event);
				Person person = new Person();
				person.setIdPerson(eventLinkInDto.getIdPerson());
				eventPersonLink.setPerson(person);
				if (!TypeConvUtil.isNullOrEmpty(eventLinkInDto.getIndPersRmvlNotified())) {
					eventPersonLink.setIndPersRmvlNotified(eventLinkInDto.getIndPersRmvlNotified());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventLinkInDto.getIndKinNotifChild())) {
					eventPersonLink.setIndKinNotifChild(eventLinkInDto.getIndKinNotifChild());
				}
				eventPersonLink.setDtLastUpdate(new Date());
				eventPersonLink.setIndLanguage(eventLinkInDto.getNotices());
				eventPersonLink.setIndDstrbutnMthd(eventLinkInDto.getDistributionMethod());
				sessionFactory.getCurrentSession().merge(eventPersonLink);
				updatedResult = ServiceConstants.One;
			}else{
				for (EventPersonLink personLink : eventPersonLinkList) {
					if (!TypeConvUtil.isNullOrEmpty(eventLinkInDto.getIndPersRmvlNotified())) {
						personLink.setIndPersRmvlNotified(eventLinkInDto.getIndPersRmvlNotified());
					}
					if (!TypeConvUtil.isNullOrEmpty(eventLinkInDto.getIndKinNotifChild())) {
						personLink.setIndKinNotifChild(eventLinkInDto.getIndKinNotifChild());
					}
					personLink.setIndLanguage(eventLinkInDto.getNotices());
					personLink.setIndDstrbutnMthd(eventLinkInDto.getDistributionMethod());
					personLink.setDtLastUpdate(new Date());
					sessionFactory.getCurrentSession().saveOrUpdate(personLink);
				}
				updatedResult = eventPersonLinkList.size();
			}
		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(eventLinkInDto.getArchInputStruct().getCreqFuncCd())) {
			deleteEventPersonLink(eventLinkInDto);
			updatedResult = ServiceConstants.One;
		} /*else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(eventLinkInDto.getArchInputStruct().getCreqFuncCd())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);

			criteria.add(Restrictions.eq("event.idEvent", eventLinkInDto.getIdEvent()));
			criteria.add(Restrictions.eq("person.idPerson", eventLinkInDto.getIdPerson()));

			List<EventPersonLink> eventPersonLinkList = criteria.list();
			for (EventPersonLink personLink : eventPersonLinkList) {
				if (!TypeConvUtil.isNullOrEmpty(eventLinkInDto.getIndPersRmvlNotified())) {
					personLink.setIndPersRmvlNotified(eventLinkInDto.getIndPersRmvlNotified());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventLinkInDto.getIndKinNotifChild())) {
					personLink.setIndKinNotifChild(eventLinkInDto.getIndKinNotifChild());
				}
				personLink.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().saveOrUpdate(personLink);
			}
			updatedResult = eventPersonLinkList.size();
		}*/
		return updatedResult;
	}
}
