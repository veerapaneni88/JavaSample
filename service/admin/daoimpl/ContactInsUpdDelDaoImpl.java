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

import us.tx.state.dfps.common.domain.AppAccessAuditLog;
import us.tx.state.dfps.common.domain.AppAccessAuditLogId;
import us.tx.state.dfps.common.domain.Contact;
import us.tx.state.dfps.common.domain.ContactNarrative;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dao.ContactInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.ContactInsUpdDelInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Csys07dDaoImpl - This DAM is an AUD for the CONTACT table. Aug
 * 10, 2017- 12:38:54 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class ContactInsUpdDelDaoImpl implements ContactInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(ContactInsUpdDelDaoImpl.class);

	public ContactInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updateContactAndContactNarrative Method Description: This
	 * method will perform UPDATE on CONTACT and NARRATIVE.
	 * 
	 * @param pInputDataRec
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateContactAndContactNarrative(ContactInsUpdDelInDto pInputDataRec) {
		log.debug("Entering method ContactInsUpdDelQUERYdam in ContactInsUpdDelDaoImpl");
		StringBuilder szTxtParameter = new StringBuilder();
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent() + "");
		szTxtParameter.append(pInputDataRec.getIdEvent());
		switch (pInputDataRec.getSysReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			saveContact(pInputDataRec);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			saveContact(pInputDataRec);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteContact(pInputDataRec);
			saveAccessAuditLog(pInputDataRec, szTxtParameter.toString());
			if (pInputDataRec.getStrBScrIndNarrExists().equalsIgnoreCase(ServiceConstants.INDICATOR_EXISTS)) {
				deleteContactNarrative(pInputDataRec);
			}
		}
		log.debug("Exiting method ContactInsUpdDelQUERYdam in ContactInsUpdDelDaoImpl");
	}

	/**
	 * 
	 * Method Name: deleteContact Method Description: This method used to delete
	 * the contact based on event id.
	 * 
	 * @param pInputDataRec
	 */
	public void deleteContact(ContactInsUpdDelInDto pInputDataRec) {
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec)) {
			Date minDate = pInputDataRec.getTsSysTsLastUpdate2();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);
			criteria.add(Restrictions.eq("idEvent", pInputDataRec.getIdEvent()));
			criteria.add(Restrictions.ge("dtLastUpdate", minDate));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			Contact contact = (Contact) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(contact)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Csys07dDaoImpl.contact.not.found", null, Locale.US));
			}
			sessionFactory.getCurrentSession().delete(contact);
		}
	}

	/**
	 * 
	 * Method Name: saveAccessAuditLog Method Description: This method used to
	 * insert the audit log.
	 * 
	 * @param pInputDataRec
	 */
	public void saveAccessAuditLog(ContactInsUpdDelInDto pInputDataRec, String szTxtParameter) {
		AppAccessAuditLog appAccessAuditLog = new AppAccessAuditLog();
		AppAccessAuditLogId appAccessAuditLogId = new AppAccessAuditLogId();
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdPersonUpdate())) {
			appAccessAuditLogId.setIdPerson(pInputDataRec.getIdPersonUpdate());
		}
		appAccessAuditLogId.setTxtSql(ServiceConstants.DELETE_CONTACT_SQL);
		appAccessAuditLogId.setTxtParameter(szTxtParameter);
		appAccessAuditLog.setId(appAccessAuditLogId);
		sessionFactory.getCurrentSession().save(appAccessAuditLog);
	}

	/**
	 * 
	 * Method Name: deleteContactNarrative Method Description: This method used
	 * to delete the contact narrative.
	 * 
	 * @param pInputDataRec
	 */
	public void deleteContactNarrative(ContactInsUpdDelInDto pInputDataRec) {
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec)) {
			Date minDate = pInputDataRec.getTsLastUpdate();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactNarrative.class);
			criteria.add(Restrictions.eq("idEvent", pInputDataRec.getIdEvent()));
			criteria.add(Restrictions.ge("dtLastUpdate", minDate));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			ContactNarrative contactnarrative = (ContactNarrative) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(contactnarrative)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Csys07dDaoImpl.contactnarrative.not.found", null, Locale.US));
			}
			sessionFactory.getCurrentSession().delete(contactnarrative);
		}
	}

	/**
	 * 
	 * Method Name: updateContact Method Description: This method used to update
	 * the contact based on event id and lastupdate.
	 * 
	 * @param pInputDataRec
	 */
	public void updateContact(ContactInsUpdDelInDto pInputDataRec) {
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec)) {
			Contact contact;
			Date minDate = pInputDataRec.getTsLastUpdate();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);
			criteria.add(Restrictions.eq("idEvent", pInputDataRec.getIdEvent()));
			criteria.add(Restrictions.ge("dtLastUpdate", minDate));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			contact = (Contact) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(contact)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Csys07dDaoImpl.contact.not.found", null, Locale.US));
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdPerson())) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						pInputDataRec.getIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(
							messageSource.getMessage("Csys07dDaoImpl.person.not.found", null, Locale.US));
				}
				contact.setPerson(person);
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtMonthlySummBegin())) {
				contact.setDtCntctMnthlySummBeg(pInputDataRec.getDtMonthlySummBegin());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtMonthlySummEnd())) {
				contact.setDtCntctMnthlySummEnd(pInputDataRec.getDtMonthlySummEnd());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactLocation())) {
				contact.setCdContactLocation(pInputDataRec.getCdContactLocation());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactMethod())) {
				contact.setCdContactMethod(pInputDataRec.getCdContactMethod());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactOthers())) {
				contact.setCdContactOthers(pInputDataRec.getCdContactOthers());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactPurpose())) {
				contact.setCdContactPurpose(pInputDataRec.getCdContactPurpose());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactType())) {
				contact.setCdContactType(pInputDataRec.getCdContactType());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtDTContactOccurred())) {
				contact.setDtContactOccurred(pInputDataRec.getDtDTContactOccurred());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndContactAttempted())) {
				contact.setIndContactAttempted(pInputDataRec.getIndContactAttempted());
			}
			contact.setDtLastEmpUpdate(new Date());
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdPersonUpdate())) {
				contact.setIdLastEmpUpdate(pInputDataRec.getIdPersonUpdate());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndEmergency())) {
				contact.setIndEmergency(pInputDataRec.getIndEmergency());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdReasonScreenOut())) {
				contact.setCdRsnScrout(pInputDataRec.getCdReasonScreenOut());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndKinRecmd())) {
				contact.setIndRecCons(pInputDataRec.getIndKinRecmd());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getNmKnCgvr())) {
				contact.setTxtKinCaregiver(pInputDataRec.getNmKnCgvr());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdRsnNotNeed())) {
				contact.setCdRsnAmtne(pInputDataRec.getCdRsnNotNeed());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getAmtNeeded())) {
				contact.setAmtNeeded(pInputDataRec.getAmtNeeded());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndSiblingVisit())) {
				contact.setIndSiblingVisit(pInputDataRec.getIndSiblingVisit());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdChildSafety())) {
				contact.setCdChildSafety(pInputDataRec.getCdChildSafety());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdPendLegalAction())) {
				contact.setCdPendLegalAction(pInputDataRec.getCdPendLegalAction());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndPrinInterview())) {
				contact.setIndPrincipalInterview(pInputDataRec.getIndPrinInterview());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdProfCollateral())) {
				contact.setCdProfCollateral(pInputDataRec.getCdProfCollateral());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdAdministrative())) {
				contact.setCdAdministrative(pInputDataRec.getCdAdministrative());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getComments())) {
				contact.setTxtComments(pInputDataRec.getComments());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndAnnounced())) {
				contact.setIndAnnounced(pInputDataRec.getIndAnnounced());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndSafPlnCompleted())) {
				contact.setIndSafPlanComp(pInputDataRec.getIndSafPlnCompleted());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndFamPlnCompleted())) {
				contact.setIndFamPlanComp(pInputDataRec.getIndFamPlnCompleted());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndSafConResolved())) {
				contact.setIndSafConResolv(pInputDataRec.getIndSafConResolved());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getHours())) {
				contact.setEstContactHours(pInputDataRec.getHours());
			}
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getMins())) {
				contact.setEstContactMins(pInputDataRec.getMins());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(contact);
		}
	}

	/**
	 * 
	 * Method Name: updateContact Method Description: This method used to insert
	 * the contact.
	 * 
	 * @param pInputDataRec
	 */
	public void saveContact(ContactInsUpdDelInDto pInputDataRec) {
		Contact contact = new Contact();
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdEvent())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, pInputDataRec.getIdEvent());
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.event", null, Locale.US));
			}
			contact.setEvent(event);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdStage())) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, pInputDataRec.getIdStage());
			if (TypeConvUtil.isNullOrEmpty(stage)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.stage", null, Locale.US));
			}
			contact.setStage(stage);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, pInputDataRec.getIdPerson());
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.person", null, Locale.US));
			}
			contact.setPerson(person);
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtMonthlySummBegin())) {
			contact.setDtCntctMnthlySummBeg(pInputDataRec.getDtMonthlySummBegin());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtMonthlySummEnd())) {
			contact.setDtCntctMnthlySummEnd(pInputDataRec.getDtMonthlySummEnd());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactLocation())) {
			contact.setCdContactLocation(pInputDataRec.getCdContactLocation());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactMethod())) {
			contact.setCdContactMethod(pInputDataRec.getCdContactMethod());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactOthers())) {
			contact.setCdContactOthers(pInputDataRec.getCdContactOthers());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactPurpose())) {
			contact.setCdContactPurpose(pInputDataRec.getCdContactPurpose());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdContactType())) {
			contact.setCdContactType(pInputDataRec.getCdContactType());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getDtDTContactOccurred())) {
			contact.setDtContactOccurred(pInputDataRec.getDtDTContactOccurred());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndContactAttempted())) {
			contact.setIndContactAttempted(pInputDataRec.getIndContactAttempted());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdPersonUpdate())) {
			contact.setIdLastEmpUpdate(pInputDataRec.getIdPersonUpdate());
		}
		contact.setDtLastEmpUpdate(new Date());
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndEmergency())) {
			contact.setIndEmergency(pInputDataRec.getIndEmergency());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdReasonScreenOut())) {
			contact.setCdRsnScrout(pInputDataRec.getCdReasonScreenOut());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndKinRecmd())) {
			contact.setIndRecCons(pInputDataRec.getIndKinRecmd());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getNmKnCgvr())) {
			contact.setTxtKinCaregiver(pInputDataRec.getNmKnCgvr());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdRsnNotNeed())) {
			contact.setCdRsnAmtne(pInputDataRec.getCdRsnNotNeed());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getAmtNeeded())) {
			contact.setAmtNeeded(pInputDataRec.getAmtNeeded());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndSiblingVisit())) {
			contact.setIndSiblingVisit(pInputDataRec.getIndSiblingVisit());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdChildSafety())) {
			contact.setCdChildSafety(pInputDataRec.getCdChildSafety());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdPendLegalAction())) {
			contact.setCdPendLegalAction(pInputDataRec.getCdPendLegalAction());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndPrinInterview())) {
			contact.setIndPrincipalInterview(pInputDataRec.getIndPrinInterview());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdProfCollateral())) {
			contact.setCdProfCollateral(pInputDataRec.getCdProfCollateral());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getCdAdministrative())) {
			contact.setCdAdministrative(pInputDataRec.getCdAdministrative());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getComments())) {
			contact.setTxtComments(pInputDataRec.getComments());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndAnnounced())) {
			contact.setIndAnnounced(pInputDataRec.getIndAnnounced());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndSafPlnCompleted())) {
			contact.setIndSafPlanComp(pInputDataRec.getIndSafPlnCompleted());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndFamPlnCompleted())) {
			contact.setIndFamPlanComp(pInputDataRec.getIndFamPlnCompleted());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIndSafConResolved())) {
			contact.setIndSafConResolv(pInputDataRec.getIndSafConResolved());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getHours())) {
			contact.setEstContactHours(pInputDataRec.getHours());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getMins())) {
			contact.setEstContactMins(pInputDataRec.getMins());
		}
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getTxtClosureDesc())) {
			contact.setTxtClosureDesc(pInputDataRec.getTxtClosureDesc());
		}
		contact.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(contact);
	}
}
