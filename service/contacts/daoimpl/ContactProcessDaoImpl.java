package us.tx.state.dfps.service.contacts.daoimpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;


import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.admin.dto.ContactDetailSaveDiDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.ContactProcessDao;
import us.tx.state.dfps.service.contacts.dao.InrSafetyDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactAUDDto;
import us.tx.state.dfps.xmlstructs.outputstructs.SimpleEventStageDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao Impl for
 * Contact detail AUD Jul 27, 2018- 12:05:01 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ContactProcessDaoImpl implements ContactProcessDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContactProcessDaoImpl.getNextInrGroupNumSql}")
	private String getNextInrGroupNum;

	@Value("${ContactProcessDaoImpl.getInrContactsByGroupNumSql}")
	private String getInrContactsByGroupNum;

	private static final Logger log = Logger.getLogger(ContactProcessDaoImpl.class);

    @Autowired
    private InrSafetyDao inrSafetyDao;

	@Autowired
	private ContactProcessDao contactProcessDao;

	private static final String ID_INR_GROUP = "idInrGroup";
	private static final String ID_INR_EVENT = "idInrEvent";

	/**
	 * Method Name:updateContactAndContactNarrative Method Description:
	 * updateContactAndContactNarrative.
	 * 
	 * @param pInputDataRec
	 * @return pOutputDataRec
	 */
	@Override
	public void updateContactAndContactNarrative(ContactDetailSaveDiDto pInputDataRec) {

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

		switch (pInputDataRec.getCdReqFunc()) {
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
		log.debug("Exiting method csys07dAUDdam in Csys07dDaoImpl");
	}

	/**
	 * 
	 * Method Name: deleteContact Method Description: delete Contact
	 * 
	 * @param pInputDataRec
	 */
	public void deleteContact(ContactDetailSaveDiDto pInputDataRec) {

		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);
			criteria.add(Restrictions.eq("idEvent", pInputDataRec.getIdEvent()));
			if (pInputDataRec.getDtLastUpdate2() != null) {
				Date minDate = pInputDataRec.getDtLastUpdate2();
				Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
				criteria.add(Restrictions.ge("dtLastUpdate", minDate));
				criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			}

			Contact contact = (Contact) criteria.uniqueResult();
			if (!TypeConvUtil.isNullOrEmpty(contact)) {
				sessionFactory.getCurrentSession().delete(contact);
			}
		}
	}

	/**
	 * Method Name:saveAccessAuditLog Method Description: save AccessAuditLog.
	 * 
	 * @param pInputDataRec
	 * @param szTxtParameter
	 */
	public void saveAccessAuditLog(ContactDetailSaveDiDto pInputDataRec, String szTxtParameter) {
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
	 * MethodName: deleteContactNarrative MethodDescription: delete
	 * ContactNarrative
	 * 
	 * @param pInputDataRec
	 */
	public void deleteContactNarrative(ContactDetailSaveDiDto pInputDataRec) {
		if (!TypeConvUtil.isNullOrEmpty(pInputDataRec)) {
			Date minDate = pInputDataRec.getDtLastUpdate();
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
	 * Method Name: updateContact Method Description:update contact table
	 * 
	 * @param contactDetailSaveDiDto
	 */
	public void updateContact(ContactDetailSaveDiDto contactDetailSaveDiDto) {
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto)) {
			Contact contact;
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);
			criteria.add(Restrictions.eq("idEvent", contactDetailSaveDiDto.getIdEvent()));
			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtLastUpdate())) {
				Date minDate = contactDetailSaveDiDto.getDtLastUpdate();
				Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
				criteria.add(Restrictions.ge("dtLastUpdate", minDate));
				criteria.add(Restrictions.lt("dtLastUpdate", maxDate));

			}
			contact = (Contact) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(contact)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Csys07dDaoImpl.contact.not.found", null, Locale.US));
			}
			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdPerson())) {

				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						contactDetailSaveDiDto.getIdPerson());
				if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
							messageSource.getMessage("Csys07dDaoImpl.person.not.found", null, Locale.US));
				}
				contact.setPerson(person);
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtMonthlySummBegin())) {
				contact.setDtCntctMnthlySummBeg(contactDetailSaveDiDto.getDtMonthlySummBegin());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtMonthlySummEnd())) {
				contact.setDtCntctMnthlySummEnd(contactDetailSaveDiDto.getDtMonthlySummEnd());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactLocation())) {
				contact.setCdContactLocation(contactDetailSaveDiDto.getCdContactLocation());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactMethod())) {
				contact.setCdContactMethod(contactDetailSaveDiDto.getCdContactMethod());
			}
			
			//Defect 13366- Fix for R2-CVS Unable to Remove Other selection in Faceplate 
			contact.setCdContactOthers(contactDetailSaveDiDto.getCdContactOthers());
			
			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactPurpose())) {
				contact.setCdContactPurpose(contactDetailSaveDiDto.getCdContactPurpose());
			}		

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactType())) {
				contact.setCdContactType(contactDetailSaveDiDto.getCdContactType());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtContactOccurred())) {
				contact.setDtContactOccurred(contactDetailSaveDiDto.getDtContactOccurred());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndContactAttempted())) {
				contact.setIndContactAttempted(contactDetailSaveDiDto.getIndContactAttempted());
			}

			contact.setDtLastEmpUpdate(new Date());

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdLastEmpUpdate())) {
				contact.setIdLastEmpUpdate(contactDetailSaveDiDto.getIdLastEmpUpdate());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndEmergency())) {
				contact.setIndEmergency(contactDetailSaveDiDto.getIndEmergency());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdReasonScreenOut())) {
				contact.setCdRsnScrout(contactDetailSaveDiDto.getCdReasonScreenOut());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndKinRecmd())) {
				contact.setIndRecCons(contactDetailSaveDiDto.getIndKinRecmd());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getNmKnCgvr())) {
				contact.setTxtKinCaregiver(contactDetailSaveDiDto.getNmKnCgvr());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdRsnNotNeed())) {
				contact.setCdRsnAmtne(contactDetailSaveDiDto.getCdRsnNotNeed());
			}else {
				contact.setCdRsnAmtne("");
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getAmtNeeded())) {
				contact.setAmtNeeded(contactDetailSaveDiDto.getAmtNeeded().intValue());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndSiblingVisit())) {
				contact.setIndSiblingVisit(contactDetailSaveDiDto.getIndSiblingVisit());
			}
			//Modified the code to check value not be null - Warranty defect 11608 
			if (null != contactDetailSaveDiDto.getCdChildSafety()) {
				contact.setCdChildSafety(contactDetailSaveDiDto.getCdChildSafety());
			}

			if (null != contactDetailSaveDiDto.getCdPendLegalAction()) {
				contact.setCdPendLegalAction(contactDetailSaveDiDto.getCdPendLegalAction());
			}

			if (null != contactDetailSaveDiDto.getIndPrinInterview()) {
				contact.setIndPrincipalInterview(contactDetailSaveDiDto.getIndPrinInterview());
			}

			if (null != contactDetailSaveDiDto.getCdProfCollateral()) {
				contact.setCdProfCollateral(contactDetailSaveDiDto.getCdProfCollateral());
			}

			if (null != contactDetailSaveDiDto.getCdAdministrative()) {
				contact.setCdAdministrative(contactDetailSaveDiDto.getCdAdministrative());
			}

			if (null != contactDetailSaveDiDto.getComments()) {
				contact.setTxtComments(contactDetailSaveDiDto.getComments());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndAnnounced())) {
				contact.setIndAnnounced(contactDetailSaveDiDto.getIndAnnounced());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndSafPlnCompleted())) {
				contact.setIndSafPlanComp(contactDetailSaveDiDto.getIndSafPlnCompleted());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndFamPlnCompleted())) {
				contact.setIndFamPlanComp(contactDetailSaveDiDto.getIndFamPlnCompleted());
			}
			
			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndCourtOrdrSvcs())) {
				contact.setIndCourtOrdrSvcs(contactDetailSaveDiDto.getIndCourtOrdrSvcs());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndSafConResolved())) {
				contact.setIndSafConResolv(contactDetailSaveDiDto.getIndSafConResolved());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getNbrHours())) {
				contact.setEstContactHours(contactDetailSaveDiDto.getNbrHours());
			}

			if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getNbrMins())) {
				contact.setEstContactMins(contactDetailSaveDiDto.getNbrMins());
			}

			/* artf128844 - Changes for FCL: ORDER #9 - START */
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getCdFtfExceptionRsn())) {
				contact.setCdFtfExceptionRsn(contactDetailSaveDiDto.getCdFtfExceptionRsn());
			}
			/* artf128844 - Changes for FCL: ORDER #9 - END */
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getCdReqextReason())) {
				contact.setCdReqextReason(contactDetailSaveDiDto.getCdReqextReason());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getTxtClosureDesc())) {
				contact.setTxtClosureDesc(contactDetailSaveDiDto.getTxtClosureDesc());
			}

			// CANIRSP-71 I&R Notification Staffing: Intake Details
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getDtNotification())) {
				contact.setDtNotification(contactDetailSaveDiDto.getDtNotification());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getTxtSummDiscuss())) {
				contact.setTxtSummDiscuss(contactDetailSaveDiDto.getTxtSummDiscuss());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getTxtIdentfdSafetyConc())) {
				contact.setTxtIdentfdSafetyConc(contactDetailSaveDiDto.getTxtIdentfdSafetyConc());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getTxtPlansFutureActions())) {
				contact.setTxtPlansFutureActions(contactDetailSaveDiDto.getTxtPlansFutureActions());
			}

			// CANIRSP-68 I&R Notification Staffing: Staffing Participant and Alleged Victims
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getIdCaseworker())) {
				contact.setIdCaseworker(contactDetailSaveDiDto.getIdCaseworker());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getCdJobCaseworker())) {
				contact.setCdJobCaseworker(contactDetailSaveDiDto.getCdJobCaseworker());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getIdSupervisor())) {
				contact.setIdSupervisor(contactDetailSaveDiDto.getIdSupervisor());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getCdJobSupervisor())) {
				contact.setCdJobSupervisor(contactDetailSaveDiDto.getCdJobSupervisor());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getIdDirector())) {
				contact.setIdDirector(contactDetailSaveDiDto.getIdDirector());
			}
			if (!StringUtils.isEmpty(contactDetailSaveDiDto.getCdJobDirector())) {
				contact.setCdJobDirector(contactDetailSaveDiDto.getCdJobDirector());
			}
            sessionFactory.getCurrentSession().saveOrUpdate(contact);
		}
	}

	/**
	 * 
	 * Method Name: saveContact Method Description:save Contact Table
	 * 
	 * @param contactDetailSaveDiDto
	 */
	public void saveContact(ContactDetailSaveDiDto contactDetailSaveDiDto) {

		Contact contact = new Contact();

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdEvent())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
					contactDetailSaveDiDto.getIdEvent());
			if (TypeConvUtil.isNullOrEmpty(event)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Csys07dDaoImpl.event.not.found", null, Locale.US));
			}
			contact.setEvent(event);
		}
		contact.setDtLastUpdate(new Date());
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdStage())) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class,
					contactDetailSaveDiDto.getIdStage());
			if (TypeConvUtil.isNullOrEmpty(stage)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.stage", null, Locale.US));
			}
			contact.setStage(stage);
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdPerson())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					contactDetailSaveDiDto.getIdPerson());
			if (TypeConvUtil.isNullOrEmpty(person)) {
				throw new DataNotFoundException(
						messageSource.getMessage("record.not.found.person", null, Locale.US));
			}
			contact.setPerson(person);
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtMonthlySummBegin())) {
			contact.setDtCntctMnthlySummBeg(contactDetailSaveDiDto.getDtMonthlySummBegin());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtMonthlySummEnd())) {
			contact.setDtCntctMnthlySummEnd(contactDetailSaveDiDto.getDtMonthlySummEnd());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactLocation())) {
			contact.setCdContactLocation(contactDetailSaveDiDto.getCdContactLocation());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactMethod())) {
			contact.setCdContactMethod(contactDetailSaveDiDto.getCdContactMethod());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactOthers())) {
			contact.setCdContactOthers(contactDetailSaveDiDto.getCdContactOthers());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactPurpose())) {
			contact.setCdContactPurpose(contactDetailSaveDiDto.getCdContactPurpose());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdContactType())) {
			contact.setCdContactType(contactDetailSaveDiDto.getCdContactType());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtContactOccurred())) {
			contact.setDtContactOccurred(contactDetailSaveDiDto.getDtContactOccurred());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndContactAttempted())) {
			contact.setIndContactAttempted(contactDetailSaveDiDto.getIndContactAttempted());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdLastEmpUpdate())) {
			contact.setIdLastEmpUpdate(contactDetailSaveDiDto.getIdLastEmpUpdate());
		}

		contact.setDtLastEmpUpdate(new Date());

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndEmergency())) {
			contact.setIndEmergency(contactDetailSaveDiDto.getIndEmergency());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdReasonScreenOut())) {
			contact.setCdRsnScrout(contactDetailSaveDiDto.getCdReasonScreenOut());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndKinRecmd())) {
			contact.setIndRecCons(contactDetailSaveDiDto.getIndKinRecmd());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getNmKnCgvr())) {
			contact.setTxtKinCaregiver(contactDetailSaveDiDto.getNmKnCgvr());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdRsnNotNeed())) {
			contact.setCdRsnAmtne(contactDetailSaveDiDto.getCdRsnNotNeed());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getAmtNeeded())) {
			contact.setAmtNeeded(contactDetailSaveDiDto.getAmtNeeded().intValue());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndSiblingVisit())) {
			contact.setIndSiblingVisit(contactDetailSaveDiDto.getIndSiblingVisit());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdChildSafety())) {
			contact.setCdChildSafety(contactDetailSaveDiDto.getCdChildSafety());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdPendLegalAction())) {
			contact.setCdPendLegalAction(contactDetailSaveDiDto.getCdPendLegalAction());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndPrinInterview())) {
			contact.setIndPrincipalInterview(contactDetailSaveDiDto.getIndPrinInterview());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdProfCollateral())) {
			contact.setCdProfCollateral(contactDetailSaveDiDto.getCdProfCollateral());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdAdministrative())) {
			contact.setCdAdministrative(contactDetailSaveDiDto.getCdAdministrative());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getComments())) {
			contact.setTxtComments(contactDetailSaveDiDto.getComments());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndAnnounced())) {
			contact.setIndAnnounced(contactDetailSaveDiDto.getIndAnnounced());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndSafPlnCompleted())) {
			contact.setIndSafPlanComp(contactDetailSaveDiDto.getIndSafPlnCompleted());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndFamPlnCompleted())) {
			contact.setIndFamPlanComp(contactDetailSaveDiDto.getIndFamPlnCompleted());
		}
		
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndCourtOrdrSvcs())) {
			contact.setIndCourtOrdrSvcs(contactDetailSaveDiDto.getIndCourtOrdrSvcs());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIndSafConResolved())) {
			contact.setIndSafConResolv(contactDetailSaveDiDto.getIndSafConResolved());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getNbrHours())) {
			contact.setEstContactHours(contactDetailSaveDiDto.getNbrHours());
		}

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getNbrMins())) {
			contact.setEstContactMins(contactDetailSaveDiDto.getNbrMins());
		}
		/* artf128844 - Changes for FCL: ORDER #9 - START */
		if (!StringUtils.isEmpty(contactDetailSaveDiDto.getCdFtfExceptionRsn())) {
			contact.setCdFtfExceptionRsn(contactDetailSaveDiDto.getCdFtfExceptionRsn());
		}
		/* artf128844 - Changes for FCL: ORDER #9 - END */
		if (!StringUtils.isEmpty(contactDetailSaveDiDto.getCdReqextReason())) {
			contact.setCdReqextReason(contactDetailSaveDiDto.getCdReqextReason());
		}

		if (!StringUtils.isEmpty(contactDetailSaveDiDto.getTxtClosureDesc())) {
			contact.setTxtClosureDesc(contactDetailSaveDiDto.getTxtClosureDesc());
		}

		// CANIRSP-8 I&R Staffing
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtNotification())) {
			contact.setDtNotification(contactDetailSaveDiDto.getDtNotification());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getTxtSummDiscuss())) {
			contact.setTxtSummDiscuss(contactDetailSaveDiDto.getTxtSummDiscuss());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getTxtIdentfdSafetyConc())) {
			contact.setTxtIdentfdSafetyConc(contactDetailSaveDiDto.getTxtIdentfdSafetyConc());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getTxtPlansFutureActions())) {
			contact.setTxtPlansFutureActions(contactDetailSaveDiDto.getTxtPlansFutureActions());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdCaseworker())) {
			contact.setIdCaseworker(contactDetailSaveDiDto.getIdCaseworker());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdJobCaseworker())) {
			contact.setCdJobCaseworker(contactDetailSaveDiDto.getCdJobCaseworker());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdSupervisor())) {
			contact.setIdSupervisor(contactDetailSaveDiDto.getIdSupervisor());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdJobSupervisor())) {
			contact.setCdJobSupervisor(contactDetailSaveDiDto.getCdJobSupervisor());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getIdDirector())) {
			contact.setIdDirector(contactDetailSaveDiDto.getIdDirector());
		}
		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getCdJobDirector())) {
			contact.setCdJobDirector(contactDetailSaveDiDto.getCdJobDirector());
		}
        sessionFactory.getCurrentSession().save(contact);
	}

	/**
	 * 
	 * Method Name: audContact Method Description:This DAO is an AUD for the
	 * CONTACT table.
	 * 
	 * @param contactDetailSaveDiDto
	 * @return long
	 */
	public long audContact(ContactDetailSaveDiDto contactDetailSaveDiDto) {
		String temp = new String();
		Date tempDate = new Date();

		if (!TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getDtContactOccurred())) {
			temp = ServiceConstants.dateFormat_MMddyyyy.format(contactDetailSaveDiDto.getDtContactOccurred());

			if (TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getTmScrTmCntct())) {
				temp = temp + ServiceConstants.SPACE + ServiceConstants.temp_time_null;
			} else {
				temp = temp + ServiceConstants.SPACE + contactDetailSaveDiDto.getTmScrTmCntct();
			}
			try {
				tempDate = ServiceConstants.dateTimeFormat.parse(temp);
			} catch (ParseException e) {
				log.debug("Exception occured while parsing the date format" + e.getMessage());
				/**Defect# 15387 - Contact Date for existing CSS Review gets updated on Save***/
				tempDate = contactDetailSaveDiDto.getDtContactOccurred();
			}
		}
		long loginId = 0;

		if (TypeConvUtil.isNullOrEmpty(contactDetailSaveDiDto.getServiceInputDto().getSzUserId())) {
			loginId = contactDetailSaveDiDto.getIdPerson();
		} else {
			loginId = Long.valueOf(contactDetailSaveDiDto.getServiceInputDto().getSzUserId());
		}
		contactDetailSaveDiDto.setIdLastEmpUpdate(loginId);
		contactDetailSaveDiDto.setDtLastEmpUpdate(new Date());
		contactDetailSaveDiDto.setDtContactOccurred(tempDate);

		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(contactDetailSaveDiDto.getServiceInputDto().getCreqFuncCd())) {
			saveContact(contactDetailSaveDiDto);
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE
				.equals(contactDetailSaveDiDto.getServiceInputDto().getCreqFuncCd())) {
			updateContact(contactDetailSaveDiDto);
        } else if (ServiceConstants.REQ_FUNC_CD_DELETE
				.equals(contactDetailSaveDiDto.getServiceInputDto().getCreqFuncCd())) {
			deleteContact(contactDetailSaveDiDto);
		}

		return ServiceConstants.Zero;
	}

	/**
	 * @deprecated This was used before the table INR_DUPLICATE_GROUPING existed, use createInrGroup() instead.
	 */
	@Deprecated
	@Override
	public Long getNextInrGroupNum() {
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getNextInrGroupNum));
		return ((BigDecimal)query.uniqueResult()).longValue();
	}

	@Override
	public Long createInrGroup(String cdInrProviderRegType, String txtNarrativeRpt, Long idPerson) {
		InrDuplicateGrouping newInrGroup = new InrDuplicateGrouping();
		newInrGroup.setIdCreatedPerson(idPerson);
		newInrGroup.setIdLastUpdatePerson(idPerson);
		newInrGroup.setCdInrProviderRegType(cdInrProviderRegType);
		newInrGroup.setTxtNarrativeRpt(txtNarrativeRpt);
		sessionFactory.getCurrentSession().persist(newInrGroup);
		return newInrGroup.getIdInrDuplicateGrouping();
	}

	@Override
	public boolean updateInrGroup(Long idGroup, String txtNarrative, String cdNarrative, Long idPerson) {
		boolean dirty = false;
		InrDuplicateGrouping grouping = (InrDuplicateGrouping) sessionFactory.getCurrentSession().get(InrDuplicateGrouping.class,
				idGroup);
		if (!ObjectUtils.nullSafeEquals(txtNarrative, grouping.getTxtNarrativeRpt())) {
			grouping.setTxtNarrativeRpt(txtNarrative);
			dirty = true;
		}
		if (!ObjectUtils.nullSafeEquals(cdNarrative, grouping.getCdInrProviderRegType())) {
			grouping.setCdInrProviderRegType(cdNarrative);
			dirty = true;
		}
		if (dirty) {
			grouping.setDtLastUpdate(new Date());
			grouping.setIdLastUpdatePerson(idPerson);
			sessionFactory.getCurrentSession().save(grouping);
		}
		return dirty;
	}

	@Override
	public Long getInrGroupNum(Long eventId) {
		Long retVal = null;
		Criteria linkCriteria = sessionFactory.getCurrentSession().createCriteria(InrDuplicateGroupingLink.class);
		linkCriteria.add(Restrictions.eq(ID_INR_EVENT, eventId));
		List<InrDuplicateGroupingLink> existingEventLinks = linkCriteria.list();
		if (!ObjectUtils.isEmpty(existingEventLinks)) {
			retVal = existingEventLinks.get(0).getIdInrGroup();
		}
		return retVal;
	}

	@Override
	public void audInrGrouping(ContactAUDDto contactAUDDto) {
		String methodInvoke = contactAUDDto.getServiceInputDto().getCreqFuncCd();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(methodInvoke)) {
			InrDuplicateGroupingLink newLink = new InrDuplicateGroupingLink();
			newLink.setIdInrGroup(contactAUDDto.getInrGroupId());
			newLink.setIdInrEvent(contactAUDDto.getIdEvent());
			newLink.setIdCreatedPerson(contactAUDDto.getIdPersonUpdate().longValue());
			newLink.setIdLastUpdatePerson(contactAUDDto.getIdPersonUpdate().longValue());
			sessionFactory.getCurrentSession().save(newLink);
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(methodInvoke)) {
			// update doesn't make sense, there is no non-key data.
		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(methodInvoke)) {
			// delete event to group links
			Criteria linkCriteria = sessionFactory.getCurrentSession().createCriteria(InrDuplicateGroupingLink.class);
			linkCriteria.add(Restrictions.eq(ID_INR_EVENT, contactAUDDto.getIdEvent()));
			InrDuplicateGroupingLink existingEventLink = (InrDuplicateGroupingLink) linkCriteria.uniqueResult();
			if (existingEventLink != null) {
				sessionFactory.getCurrentSession().delete(existingEventLink);
			}
		}
	}

	@Override
	public Map<Long, SimpleEventStageDto> getInrPersonIdToDataMap(Long nbrGroup) {
		Map<Long, SimpleEventStageDto> retVal = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getInrContactsByGroupNum)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("nbrGroup", nbrGroup)
				.setResultTransformer(Transformers.aliasToBean(SimpleEventStageDto.class));

		List<SimpleEventStageDto> resultList = query.list();

		if (!ObjectUtils.isEmpty(resultList)) {
			retVal = new HashMap<>();
			for (SimpleEventStageDto currResult : resultList) {
				retVal.put(currResult.getIdPerson(), currResult);
			}
		}

		return retVal;
	}

	@Override
	public void mergeGroupLinkToStages(String cdRegTyp, String txtNarr, List<Long> intakeStageIds, Long idInrGroup, Long idPerson, boolean inrGroupIsNew) {
		// set the value in INR_DUPLICATE_GROUPING. updateInrGroup handles setting or clearing values so it's safe to run for every case.
		if (!inrGroupIsNew) {
			contactProcessDao.updateInrGroup(idInrGroup, txtNarr, cdRegTyp, idPerson);
		}
		if (cdRegTyp == null) {
			handleLinkToStages(intakeStageIds, idInrGroup, idPerson);
		} else {
			handleIntakeStageException(idInrGroup);
		}
	}

	private void handleLinkToStages(List<Long> intakeStageIds, Long idInrGroup, Long idPerson) {
		// handle the list of stage ids
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InrGroupToStage.class);
		criteria.add(Restrictions.eq(ID_INR_GROUP, idInrGroup));
		List<InrGroupToStage> existingLinks = criteria.list();
		Set<Long> currentlyLinkedStages = existingLinks.stream().map(InrGroupToStage::getIdIntStage).collect(Collectors.toSet());

		// process any adds
		if (!ObjectUtils.isEmpty(intakeStageIds)) {
			for (Long currentStageId : intakeStageIds) {
				// skip updates.
				if (!currentlyLinkedStages.contains(currentStageId)) {
					// process adds
					InrGroupToStage newLink = new InrGroupToStage();
					newLink.setIdInrGroup(idInrGroup);
					newLink.setIdIntStage(currentStageId);
					newLink.setIdCreatedPerson(idPerson);
					newLink.setIdLastUpdatePerson(idPerson);
					sessionFactory.getCurrentSession().save(newLink);
				}
			}
		}

		// process any deletes
		for (InrGroupToStage currExistingLinkObj : existingLinks) {
			if (!intakeStageIds.contains(currExistingLinkObj.getIdIntStage())) {
				sessionFactory.getCurrentSession().delete(currExistingLinkObj);
			}
		}
	}

	private void handleIntakeStageException(Long idInrGroup) {
		// see if there are any values in INR_GROUP_TO_STAGE that need to be removed.
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InrGroupToStage.class);
		criteria.add(Restrictions.eq(ID_INR_GROUP, idInrGroup));
		List<InrGroupToStage> existingLinks = criteria.list();
		for (InrGroupToStage currExistingLinkObj : existingLinks) {
			sessionFactory.getCurrentSession().delete(currExistingLinkObj);
		}
	}

	@Override
	public String getIntakeStageListByEventId(Long eventId) {
		Criteria linkCriteria = sessionFactory.getCurrentSession().createCriteria(InrDuplicateGroupingLink.class);
		linkCriteria.add(Restrictions.eq(ID_INR_EVENT, eventId));
		List<InrDuplicateGroupingLink> existingEventLinks = linkCriteria.list();
		StringBuilder retVal = new StringBuilder();

		if (!ObjectUtils.isEmpty(existingEventLinks)) {
			Long groupId = existingEventLinks.get(0).getIdInrGroup();
			Criteria stageCriteria = sessionFactory.getCurrentSession().createCriteria(InrGroupToStage.class);
			stageCriteria.add(Restrictions.eq(ID_INR_GROUP, groupId));
			List<InrGroupToStage> existingStageLinks = stageCriteria.list();
			if (!ObjectUtils.isEmpty(existingStageLinks)) {
				for (InrGroupToStage currStageLink : existingStageLinks) {
					if (retVal.length() > 0) {
						retVal.append(",");
					}
					retVal.append(currStageLink.getIdIntStage());
				}
			}
		}
		return retVal.toString();
	}

	@Override
	public List<Long> getIntakeStageListByGroupId(Long groupId) {
		List<Long> retVal = new LinkedList<>();
			Criteria stageCriteria = sessionFactory.getCurrentSession().createCriteria(InrGroupToStage.class);
			stageCriteria.add(Restrictions.eq(ID_INR_GROUP, groupId));
			List<InrGroupToStage> existingStageLinks = stageCriteria.list();
			if (!ObjectUtils.isEmpty(existingStageLinks)) {
				for (InrGroupToStage currStageLink : existingStageLinks) {
					retVal.add(currStageLink.getIdIntStage());
				}
			}
		return retVal;
	}

	public void deleteInstakeStageListForGroup(Long nbrGroup) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InrGroupToStage.class);
		criteria.add(Restrictions.eq(ID_INR_GROUP, nbrGroup));
		List<InrGroupToStage> inrIntakeStageList = criteria.list();

		if(!CollectionUtils.isEmpty(inrIntakeStageList)) {
			for (InrGroupToStage inrSafetyFollowup : inrIntakeStageList) {
				sessionFactory.getCurrentSession().delete(inrSafetyFollowup);
			}
		}
	}

	@Override
	public void deleteInrGroup(Long idGroup) {
		// delete group
		Criteria groupCriteria = sessionFactory.getCurrentSession().createCriteria(InrDuplicateGrouping.class);
		groupCriteria.add(Restrictions.eq("idInrDuplicateGrouping", idGroup));
		InrDuplicateGrouping existingGroup = (InrDuplicateGrouping) groupCriteria.uniqueResult();
		if (existingGroup != null) {
			sessionFactory.getCurrentSession().delete(existingGroup);
		}
	}

}
