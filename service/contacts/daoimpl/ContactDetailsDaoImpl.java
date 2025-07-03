package us.tx.state.dfps.service.contacts.daoimpl;

import oracle.jdbc.OracleTypes;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.ContactPersonNarrValueDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.apscasereview.ApsCaseReviewContactDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.request.SaveContactReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.contact.dto.*;
import us.tx.state.dfps.service.contacts.dao.ChildFatality1050BDao;
import us.tx.state.dfps.service.contacts.dao.ContactDetailsDao;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.service.PersonDtlService;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageLinkDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactDetailsDaoImpl Jul 31, 2017- 1:04:37 PM Â© 2017 Texas
 * Department of Family and Protective Services
 */

@Repository
public class ContactDetailsDaoImpl implements ContactDetailsDao {

	private static final Logger log = Logger.getLogger(ContactDetailsDaoImpl.class);
	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	PersonDtlService personDtlService;

	@Autowired
	ChildFatality1050BDao childFatality1050BDao;

	@Value("${ContactDaoImpl.getContactEntityById}")
	private String getContactEntityByIdSql;

	@Value("${ContactDaoImpl.getContactById}")
	private String getContactByIdSql;

	@Value("${ContactDaoImpl.updateAppEventStatus}")
	private transient String updateAppEventStatusSql;

	@Value("${ContactDetailsDaoImpl.getCFTChildrenForStage}")
	private String getCFTChildrenForStage;

	@Value("${ContactDetailsDaoImpl.hasCF1050BRecord}")
	private String hasCF1050BRecord;

	@Value("${ContactDetailsDaoImpl.fetchContactPersonNarr}")
	private transient String fetchContactPersonNarr;

	@Value("${FormReferralsDaoImpl.getSupervisorId}")
	private String getSupervisorId;

	@Value("${contactDetails.deletingWorloadRec}")
	private String deleteWorkLoadRec;

	@Value("${workload.getCaseWorkers}")
	private String getCaseWorkers;

	@Value("${contactDetails.selectToDosForEvent}")
	private String selectToDosForEvent;
	@Value("${ContactDetailsDaoImpl.getCFTCurrHisInfo}")
	private String getCFTCurrHisInfo;

	@Value("${ContactDetailsDaoImpl.getCFTPriorHistory}")
	private String getCFTPriorHistory;
	@Value("${ContactDetailsDaoImpl.fetchFacilityType}")
	private String fetchFacilityType;
	@Value("${ContactDetailsDaoImpl.childFatility}")
	private String childFatility;
	@Value("${CriminalHistoryDaoImpl.checkCrimHistActionNonDPS}")
	private transient String checkCrimHistActionNonDPS;

	@Value("${CriminalHistoryDaoImpl.checkCrimHistActionDPSPreWSRelease}")
	private transient String checkCrimHistActionDPSPreWSRelease;

	@Value("${CriminalHistoryDaoImpl.checkCrimHistActionDPSPostWSRelease}")
	private transient String checkCrimHistActionDPSPostWSRelease;

	@Value("${contactDetailsDaoImpl.deleteFtRlsInfoRpt}")
	private transient String deleteFtRlsInfoRpt;

	@Value("${contactDetailsDaoImpl.deleteRlsInfoRptCPSByRptId}")
	private transient String deleteRlsInfoRptCPSByRptId;

	@Value("${contactDetailsDaoImpl.deleteRlsInfoAllegDispositionByRptId}")
	private transient String deleteRlsInfoAllegDispositionByRptId;

	@Value("${contactDetailsDaoImpl.deleteRlsInfoRptRsrcByRptId}")
	private transient String deleteRlsInfoRptRsrcByRptId;

	@Value("${contactDetailsDaoImpl.deleteRsrcViolationByRptId}")
	private transient String deleteRsrcViolationByRptId;

	@Value("${contactDetailsDaoImpl.deleteRlsInfoRptCPAByRptId}")
	private transient String deleteRlsInfoRptCPAByRptId;

	@Value("${contactDetailsDaoImpl.reasonRlngshmntCodes}")
	private String reasonRlngshmntCodes;

	@Value("${contactDetails.getAllPersonsRecords}")
	private String getAllPersonsRecordsSql;

	@Value("${contactDetails.getPersonsAddrDtls}")
	private String getPersonsAddrDtlsSql;

	@Value("${contactDetails.getRecentClosedIdStage}")
	private String getRecentClosedIdStageSql;

	@Value("${contactDetails.getContactActiveAddr}")
	private String getContactActiveAddrSql;

	@Value("${contactDetails.getDistinctAllgtnList}")
	private String getDistinctAllgtnListSql;
	
	@Value("${ContactDaoImpl.getRiskAssmtNarr}")
	private String getRiskAssmtNarrSql;
	
	@Value("${ContactDaoImpl.getRiskAssmtIarNarr}")
	private String getRiskAssmtIarNarrSql;


	@Value("${contactDetails.getAllPersonsRecordsForMPS}")
	private String getAllPersonsRecordsForMPSSql;

	@Value("${contactDetails.getDistinctAllgtnListForMPS}")
	private String getDistinctAllgtnListSqlForMPS;

	@Autowired
	TodoDao todoDao;

	@Autowired
	private LookupDao lookupDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	MobileUtil mobileUtil;

	public ContactDetailsDaoImpl() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public String completeCFInfoTasks(CommonEventIdReq commonEventIdReq) {
		Long eventId = commonEventIdReq.getIdEvent();

		List<TodoDto> todoList = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(selectToDosForEvent)
				.addScalar("cdTodoInfo", StandardBasicTypes.STRING).addScalar("idTodo", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.addScalar("idTodoCase", StandardBasicTypes.LONG).addScalar("idTodoEvent", StandardBasicTypes.LONG)
				.addScalar("idTodoPersCreator", StandardBasicTypes.LONG)
				.addScalar("idTodoStage", StandardBasicTypes.LONG)
				.addScalar("idTodoPersWorker", StandardBasicTypes.LONG)
				.addScalar("dtTodoDue", StandardBasicTypes.TIMESTAMP).addScalar("cdTodoTask", StandardBasicTypes.STRING)
				.addScalar("todoDesc", StandardBasicTypes.STRING).addScalar("cdTodoType", StandardBasicTypes.STRING)
				.addScalar("todoLongDesc", StandardBasicTypes.STRING)
				.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoTaskDue", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtTodoCompleted", StandardBasicTypes.TIMESTAMP)
				.addScalar("nmTodoCreatorInit", StandardBasicTypes.STRING)
				.addScalar("idTodoInfo", StandardBasicTypes.LONG).setParameter("idEvent", eventId)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class));

		todoList = (List<TodoDto>) query.list();

		for (TodoDto todoDto : todoList) {
			Todo todoEntity = todoDao.getTodoDetailById(todoDto.getIdTodo());
			todoEntity.setDtTodoCompleted(new Date());
			todoEntity.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(todoEntity);
		}

		return ServiceConstants.CONTACT_SUCCESS;
	}

	/**
	 * Method Name: reviewComplete Method Description:This method allows to
	 * complete review when type CSS Review is selected in contact details page.
	 * 
	 * @param saveContactReq
	 * @return String @
	 */
	@SuppressWarnings("unchecked")
	public String reviewComplete(SaveContactReq saveContactReq) {
		try {

			SQLQuery query1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCaseWorkers)
					.setParameter("idworkloadCase", saveContactReq.getIdCase());

			List<BigDecimal> caseWorkerIDs = query1.list();
			for (BigDecimal id : caseWorkerIDs) {
				if (!saveContactReq.getIdPerson().equals(id.longValue()))
					prepareAndSaveTodoEntity(saveContactReq, id.longValue());
				Long supervisorId = getSupervisorId(id.longValue());
				if (supervisorId != 0)
					prepareAndSaveTodoEntity(saveContactReq, supervisorId);
			}
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
					.add(Restrictions.eq("idStage", saveContactReq.getIdStage()))
					.add(Restrictions.eq("idCase", saveContactReq.getIdCase()))
					.add(Restrictions.eq("idPerson", saveContactReq.getIdPerson()));
			StagePersonLink stagePerLinkEntity = (StagePersonLink) cr.uniqueResult();
			// Defect 4335 - added check to invoke delete only when entity
			// returned is not null
			if (!ObjectUtils.isEmpty(stagePerLinkEntity)) {
				sessionFactory.getCurrentSession().delete(stagePerLinkEntity);
			}
		} catch (HibernateException e) {
			return ServiceConstants.CONTACT_FAILURE;
		}
		return ServiceConstants.CONTACT_SUCCESS;
	}

	/**
	 * 
	 * Method Name: getSupervisorId Method Description: This method will
	 * retrieve ID Person for the Supervisor.
	 * 
	 * @param idPerson
	 * @return Long @
	 */
	public Long getSupervisorId(Long idPerson) {
		Long idSupr = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSupervisorId).setParameter("idPerson",
				idPerson);
		BigDecimal bigDecimal = ((BigDecimal) query.uniqueResult());
		if (!TypeConvUtil.isNullOrEmpty(bigDecimal)) {
			idSupr = Long.valueOf(bigDecimal.longValue());
		}

		return idSupr;
	}

	public void prepareAndSaveTodoEntity(SaveContactReq saveContactReq, long caseWorkerId) {
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		Todo todoEntity = new Todo();
		todoEntity.setCdTodoType(ServiceConstants.ALERT_TODO);
		todoEntity.setCdTodoTask(ServiceConstants.CPS_CONTACT_TASK);
		todoEntity.setDtTodoDue(currentDate);
		todoEntity.setDtLastUpdate(currentDate);
		todoEntity.setDtTodoCreated(currentDate);
		todoEntity.setTxtTodoDesc(ServiceConstants.TODO_DESC_CONTACT);
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, saveContactReq.getIdStage());
		if (stage != null)
			todoEntity.setStage(stage);

		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, saveContactReq.getIdEvent());
		if (event != null)
			todoEntity.setEvent(event);

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, caseWorkerId);
		if (person != null)
			todoEntity.setPersonByIdTodoPersAssigned(person);
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
				saveContactReq.getIdCase());
		if (capsCase != null)
			todoEntity.setCapsCase(capsCase);
		sessionFactory.getCurrentSession().saveOrUpdate(todoEntity);
	}

	/**
	 * * This method checks returns all the deceased Children in the given
	 * Stage. Checks for following Conditions. 1. Allegation Record present for
	 * the Child with IND_FATALITY = 'Y'. 2. Same Child should have Date of
	 * Death in Person Table.
	 * 
	 * @param idStage
	 * @return List<Long> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getCFTChildrenForStage(Long idStage) {
		List<Long> cftChildrenList = new ArrayList<>();

		List<Person> person = (List<Person>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCFTChildrenForStage).setParameter("ID_ALLEGATION_STAGE", idStage))
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(Person.class)).list();

		for (Person persn : person)
			cftChildrenList.add(persn.getIdPerson());

		return cftChildrenList;
	}

	/**
	 * This method retrieves Contact Record for the given Contact Event Id.
	 * 
	 * @param idEvent
	 * @return @
	 */

	public ContactDto selectContact(long idEvent) {
		ContactDto contactDto = new ContactDto();
		Contact contact = (Contact) sessionFactory.getCurrentSession().get(Contact.class, idEvent);
		if (contact != null) {
			contactDto.setAmtNeeded(contact.getAmtNeeded());
			contactDto.setCdAdministrative(contact.getCdAdministrative());
			contactDto.setCdChildSafety(contact.getCdChildSafety());
			contactDto.setCdContactLocation(contact.getCdContactLocation());
			contactDto.setCdContactMethod(contact.getCdContactMethod());
			contactDto.setCdContactOthers(contact.getCdContactOthers());
			contactDto.setCdContactPurpose(contact.getCdContactPurpose());
			contactDto.setCdContactType(contact.getCdContactType());
			contactDto.setCdPendLegalAction(contact.getCdPendLegalAction());
			contactDto.setCdProfCollateral(contact.getCdProfCollateral());
			contactDto.setCdRsnAmtne(contact.getCdRsnAmtne());
			contactDto.setCdRsnScrout(contact.getCdRsnScrout());
			contactDto.setDtCntctMnthlySummBeg(contact.getDtCntctMnthlySummBeg());
			contactDto.setDtCntctMnthlySummEnd(contact.getDtCntctMnthlySummEnd());
			contactDto.setDtCntctNextSummDue(contact.getDtCntctNextSummDue());
			contactDto.setDtContactApprv(contact.getDtContactApprv());
			contactDto.setDtContactOccurred(contact.getDtContactOccurred());
			contactDto.setDtLastUpdate(contact.getDtLastUpdate());
			contactDto.setDtLastEmpUpdate(contact.getDtLastEmpUpdate());
			contactDto.setEstContactHours(contact.getEstContactHours());
			contactDto.setEstContactMins(contact.getEstContactMins());
			contactDto.setIdCase(contact.getIdCase());
			contactDto.setIdContactWorker(contact.getPerson().getIdPerson());
			contactDto.setIdEvent(contact.getIdEvent());
			contactDto.setIdLastEmpUpdate(contact.getIdLastEmpUpdate());
			contactDto.setIndAnnounced(contact.getIndAnnounced());
			contactDto.setIndContactAttempted(contact.getIndContactAttempted());
			contactDto.setIndEmergency(contact.getIndEmergency());
			contactDto.setIndFamPlanComp(contact.getIndFamPlanComp());
			contactDto.setIndPrincipalInterview(contact.getIndPrincipalInterview());
			contactDto.setIndRecCons(contact.getIndRecCons());
			contactDto.setIndSafConResolv(contact.getIndSafConResolv());
			contactDto.setIndSafPlanComp(contact.getIndSafPlanComp());
			contactDto.setIndSiblingVisit(contact.getIndSiblingVisit());
			contactDto.setKinCaregiver(contact.getTxtKinCaregiver());
			contactDto.setTxtComments(contact.getTxtComments());
			if (!ObjectUtils.isEmpty(contact.getPerson())) {
				contactDto.setNmPersonFull(contact.getPerson().getNmPersonFull());
				contactDto.setIdPerson(contact.getPerson().getIdPerson());
			}
			if (!ObjectUtils.isEmpty(contact.getStage())) {
				contactDto.setIdContactStage(contact.getStage().getIdStage());
			}
		}
		return contactDto;
	}

	/**
	 * MethodName: updateApproversStatus MethodDescription: Updates the
	 * Approvers Status Code to Invalid
	 * 
	 * @param idEvent
	 * @return long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long updateApproversStatus(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApprovalEventLink.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));
		ProjectionList reqColumns = Projections.projectionList();
		reqColumns.add(Projections.property("idApproval"));
		criteria.setProjection(reqColumns);
		long approvalId = (long) criteria.uniqueResult();

		if (approvalId != 0) {
			Criteria criteria1 = sessionFactory.getCurrentSession().createCriteria(Event.class);
			criteria1.add(Restrictions.eq("idEvent", idEvent));
			List<String> inList = new ArrayList<>();
			inList.add(ServiceConstants.CD_TASK_CPS_INV_CON);
			inList.add(ServiceConstants.CD_TASK_RCL_INV_CON);
			inList.add(ServiceConstants.CD_TASK_CPS_INV_CON_APRVL);
			inList.add(ServiceConstants.CD_TASK_RCL_INV_CON_APRVL);
			criteria1.add(Restrictions.in("cdTask", inList));
			List<Event> eventList = criteria1.list();
			for (Event event : eventList) {
				event.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
				sessionFactory.getCurrentSession().saveOrUpdate(event);
			}

			criteria1 = sessionFactory.getCurrentSession().createCriteria(Approvers.class);
			criteria1.add(Restrictions.eq("idApproval", approvalId));
			List<Approvers> approverList = criteria1.list();
			for (Approvers approvers : approverList) {
				approvers.setCdApproversStatus(ServiceConstants.INVALID_APPROVAL_STATUS);
				sessionFactory.getCurrentSession().saveOrUpdate(approvers);
			}
			return 1;
		}
		return 0;
	}

	/**
	 * MethodName: updateAppEventStatus MethodDescription: Update Approval event
	 * status.
	 * 
	 * @param idEvent
	 * @param eventStatus
	 * @return long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long updateAppEventStatus(Long idEvent, String eventStatus) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ApprovalEventLink.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));
		ProjectionList reqColumns = Projections.projectionList();
		reqColumns.add(Projections.property("idApproval"));
		criteria.setProjection(reqColumns);
		List<long[]> idApprovalList = criteria.list();

		criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.in("idEvent", idApprovalList));
		criteria.add(Restrictions.eq("cdEventType", "APP"));
		List<Event> events = criteria.list();
		for (Event event : events) {
			event.setCdEventStatus(eventStatus);
			sessionFactory.getCurrentSession().saveOrUpdate(event);
		}
		return events.size();
	}

	@Override
	public boolean hasCF1050BRecord(Long idStage, Long idPerson) {
		boolean recordExists = false;
		BigDecimal count = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(hasCF1050BRecord)
				.setParameter("ID_CONTACT_STAGE", idStage).setParameter("ID_PERSON", idPerson).uniqueResult();
		if (count.intValue() > 0)
			recordExists = true;
		return recordExists;
	}

	/**
	 * Method Name: fetchContactPersonNarr Method Description: Method to fetch
	 * Contact Guide Narrative for Caregiver/Collateral contacted.
	 * 
	 * @param contactPersonNarrValueDto
	 * @return ContactPersonNarrValueDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContactPersonNarrValueDto fetchContactPersonNarr(ContactPersonNarrValueDto contactPersonNarrValueDto) {
		List<ContactPersonNarrValueDto> contactPersonNarrValueDto2 = new ArrayList<ContactPersonNarrValueDto>();
		Query queryContact = sessionFactory.getCurrentSession().createSQLQuery(fetchContactPersonNarr)
				.addScalar("idContactPersonNarr", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("narrative", StandardBasicTypes.BINARY)
				.addScalar("cdRsnNotNofified", StandardBasicTypes.STRING)
				.setParameter("idEvent", contactPersonNarrValueDto.getIdEvent())
				.setParameter("idPerson", contactPersonNarrValueDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(ContactPersonNarrValueDto.class));

		contactPersonNarrValueDto2 = (List<ContactPersonNarrValueDto>) queryContact.list();
		if (TypeConvUtil.isNullOrEmpty(contactPersonNarrValueDto2)) {
			throw new DataNotFoundException(
					messageSource.getMessage("ContactDetailsDaoImpl.notFound", null, Locale.US));
		}
		ContactPersonNarrValueDto perNarValueDto = new ContactPersonNarrValueDto();
		if (!CollectionUtils.isEmpty(contactPersonNarrValueDto2)) {
			perNarValueDto = contactPersonNarrValueDto2.get(0);
		}
		return perNarValueDto;
	}

	/**
	 * Method Name: getCFTCurrHisInfo Method Description: This method current
	 * info for the given Stage.
	 * 
	 * @param cftRlsInfoRptCPSValueDto
	 * @return CFTRlsInfoRptCPSValueDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CFTRlsInfoRptCPSValueModBean getCFTCurrHisInfo(CFTRlsInfoRptCPSValueModBean currBean) {
		int errorCode = 0;
		String errorMessage = null;
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		try {
			callStatement = connection.prepareCall(getCFTCurrHisInfo);
			callStatement.setInt(1, currBean.getIdStage().intValue());
			callStatement.setInt(2, currBean.getIdCase().intValue());
			callStatement.registerOutParameter(3, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(6, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(7, java.sql.Types.ARRAY, "TYPE_CFT_SAFETY_INFO_ARR");
			callStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(9, java.sql.Types.BIGINT);
			callStatement.registerOutParameter(10, java.sql.Types.VARCHAR);
			callStatement.executeUpdate();
			currBean.setSafetyRiskAssmnt(callStatement.getString(3));
			currBean.setRiskAssmntMsg(callStatement.getString(4));
			currBean.setServicesReferrals(callStatement.getString(5));
			currBean.setCaseAction(callStatement.getString(6));
			currBean.setSafetyAssessmentInfoDBList(
					getCFTSafetyAssessmentInfoDBList((Array) callStatement.getObject(7)));
			currBean.setSdmRaFinalRiskLevel(callStatement.getString(8));
			errorCode = callStatement.getInt(9);
			errorMessage = callStatement.getString(10);
		} catch (SQLException e) {
			log.error(e.getMessage());
			DataLayerException dataException = new DataLayerException(errorCode + errorMessage + e.getMessage());
			dataException.initCause(e);
			throw dataException;
		} finally {
			try {
				callStatement.close();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
		}
		return currBean;
	}

	/**
	 * 
	 * Method Name: getCFTSafetyAssessmentInfoDBList Method Description:
	 * 
	 * @param safetyAssessmentInfoArray
	 * @return
	 */
	private List<CFTSafetyAssessmentInfoDto> getCFTSafetyAssessmentInfoDBList(Array safetyAssessmentInfoArray) {
		List<CFTSafetyAssessmentInfoDto> safetyAssessmentInfoDBList = new ArrayList<CFTSafetyAssessmentInfoDto>();
		try {
			Object[] safetyAssessmentDetails = (Object[]) safetyAssessmentInfoArray.getArray();
			CFTSafetyAssessmentInfoDto safetyAssessmentInfo = null;

			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (int i = 0; i < safetyAssessmentDetails.length; i++) {
				safetyAssessmentInfo = new CFTSafetyAssessmentInfoDto();
				java.sql.Struct safetyAssessmentRec = (java.sql.Struct) safetyAssessmentDetails[i];
				Object safetyAssessmentAtrr[] = safetyAssessmentRec.getAttributes();
				safetyAssessmentInfo.setIdEvent(Long.valueOf(safetyAssessmentAtrr[0].toString()));
				safetyAssessmentInfo.setDtSafetyAssessed(
						new Timestamp(dateFormatter.parse(safetyAssessmentAtrr[1].toString()).getTime()));
				safetyAssessmentInfo.setEventDescription(safetyAssessmentAtrr[2].toString());
				safetyAssessmentInfo.setSafetyDecision(safetyAssessmentAtrr[3].toString());

				safetyAssessmentInfoDBList.add(safetyAssessmentInfo);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			DataLayerException dataException = new DataLayerException(e.getMessage());
			dataException.initCause(e);
			throw dataException;
		}

		return safetyAssessmentInfoDBList;
	}

	/**
	 * Method Name: getCFTPriorHistory Method Description:This method return
	 * prior history for the given Stage.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return List<CFTRlsInfoRptCPSValueDto>
	 */
	@Override
	public List<CFTRlsInfoRptCPSValueModBean> getCFTPriorHistory(Long idStage, Long idPerson) {
		List<CFTRlsInfoRptCPSValueModBean> priorHistryList = new ArrayList<CFTRlsInfoRptCPSValueModBean>();
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		try {
			callStatement = connection.prepareCall(getCFTPriorHistory);
			callStatement.setLong(1, idStage);
			callStatement.setLong(2, idPerson);
			callStatement.registerOutParameter(3, java.sql.Types.ARRAY, "TYPE_PRIOR_HISTORY_ARR");
			callStatement.registerOutParameter(4, java.sql.Types.BIGINT);
			callStatement.registerOutParameter(5, java.sql.Types.VARCHAR);
			callStatement.executeQuery();
			Array priorHisArry = (Array) callStatement.getObject(3);
			if (!ObjectUtils.isEmpty(priorHisArry)) {
				Object[] priorHistry = (Object[]) priorHisArry.getArray();
				CFTRlsInfoRptCPSValueModBean priorHisBean = null;
				for (int i = 0; i < priorHistry.length; i++) {
					priorHisBean = new CFTRlsInfoRptCPSValueModBean();
					java.sql.Struct priorHistryRec = (java.sql.Struct) priorHistry[i];
					Object priorHistryAtrr[] = priorHistryRec.getAttributes();
					priorHisBean.setIdCase(Long.valueOf(priorHistryAtrr[0].toString()));
					priorHisBean.setIdStage(Long.valueOf(priorHistryAtrr[1].toString()));
					priorHisBean.setIdPerson(Long.valueOf(priorHistryAtrr[2].toString()));
					if (priorHistryAtrr[3] != null && priorHistryAtrr[3].toString().length() > 0)
						priorHisBean.setSafetyRiskAssmnt(priorHistryAtrr[3].toString());
					if (priorHistryAtrr[4] != null && priorHistryAtrr[4].toString().length() > 0)
						priorHisBean.setServicesReferrals(priorHistryAtrr[4].toString());
					if (priorHistryAtrr[5] != null && priorHistryAtrr[5].toString().length() > 0)
						priorHisBean.setCaseAction(priorHistryAtrr[5].toString());
					if (priorHistryAtrr[6] != null && priorHistryAtrr[6].toString().length() > 0)
						priorHisBean.setRiskAssmntMsg(priorHistryAtrr[6].toString());
					priorHisBean.setSafetyAssessmentInfoDBList(
							getCFTSafetyAssessmentInfoDBList(((Array) priorHistryAtrr[7])));
					PersonDtlReq personDtlReq = new PersonDtlReq();
					personDtlReq.setIdPerson(Long.valueOf(priorHistryAtrr[2].toString()));
					priorHisBean.setNmPersonFull(personDtlService.getPersonFullName(personDtlReq));
					priorHisBean.setCdHistoryType(ServiceConstants.HS);
					if (priorHistryAtrr[8] != null && priorHistryAtrr[8].toString().length() > 0)
						priorHisBean.setSdmRaFinalRiskLevel(priorHistryAtrr[8].toString());
					priorHistryList.add(priorHisBean);
				}
			}
			callStatement.close();
		} catch (SQLException e) {
			log.error(e.getMessage());
			DataLayerException dataException = new DataLayerException(e.getMessage());
			dataException.initCause(e);
			throw dataException;
		}
		return priorHistryList;
	}

	/**
	 * Method Name: fetchChildFatalityInfo Method Description:One of two
	 * different retrieval methods is invoked to fetch data for child fatality
	 * (RCL screen) based on if the contact page for current person and stage is
	 * NEW or existing - This method retrieves child fatality info for the given
	 * Stage and is used by continueType_xa() method used when the contact type
	 * is new <no prior data> exist for current person ID & stage (data
	 * retrieved from existing tables IMPACT & CLASS tables, not from
	 * FT_RLS_INFO_* tables >> which is retrieved from for update of contact
	 * info via displayContact_xa()).
	 * 
	 * @param personId
	 * @param stageId
	 * @param userId
	 * @return CFT1050BReportDto
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ContactDetailCFReportBean fetchChildFatalityInfo(long personId, long stageId, long userId) {
		ContactDetailCFReportBean currCFT1050BReportDB = new ContactDetailCFReportBean();
		SessionImplementor sessionFactoryImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		CallableStatement callStatement = null;
		try {
			Connection connection = sessionFactoryImplementor.getJdbcConnectionAccess().obtainConnection();
			callStatement = connection.prepareCall(childFatility);
			int errorCode = 0;
			String errorMessage = null;
			ResultSet resultSet = null;
			String operType = fetchFacilityType(stageId);
			if (operType.equals("UNKNOWN")) {
				// no need to fetch facility info since the facility type is
				// unknown
				// (not either CPA/AH/GRO)
				// this should be reported to the user as an error
				CFTRlsInfoRptResourceValueBean currCFTRlsInfoRptResourceValueBean = new CFTRlsInfoRptResourceValueBean();
				currCFTRlsInfoRptResourceValueBean.setOperationType(operType);
				currCFT1050BReportDB.setRlsInfoRptRsrc(currCFTRlsInfoRptResourceValueBean);
			} else {

				callStatement.setLong(1, stageId);
				callStatement.setLong(2, personId);
				callStatement.setString(3, operType);

				callStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
				callStatement.registerOutParameter(5, java.sql.Types.BIGINT);
				callStatement.registerOutParameter(6, OracleTypes.CURSOR);
				callStatement.registerOutParameter(7, OracleTypes.CURSOR);
				callStatement.registerOutParameter(8, OracleTypes.CURSOR);
				callStatement.registerOutParameter(9, OracleTypes.CURSOR);
				callStatement.registerOutParameter(10, OracleTypes.CURSOR);
				callStatement.registerOutParameter(11, OracleTypes.CURSOR);

				callStatement.registerOutParameter(12, java.sql.Types.DATE);
				callStatement.registerOutParameter(13, java.sql.Types.BIGINT);
				callStatement.registerOutParameter(14, java.sql.Types.VARCHAR);

				callStatement.executeQuery();
				String nameOfHome = callStatement.getString(4);
				int nbrRsrc = callStatement.getInt(5);

				Date dateOperationLicensed = callStatement.getDate(12);
				errorCode = callStatement.getInt(13);
				errorMessage = callStatement.getString(14);
				if (errorCode < 0) {
					throw new SQLException(errorMessage);
				}
				CFTRlsInfoRptValueModBean currCFTRlsInfoRptValueBean = new CFTRlsInfoRptValueModBean();
				CFTRlsInfoRptResourceValueBean currCFTRlsInfoRptResourceValueBean = new CFTRlsInfoRptResourceValueBean();
				List currCFTRlsInfoRptCPAValueBeanList = new ArrayList();
				List currCFTRlsInfoRptAllegDispValueBeanList = new ArrayList();
				List currCFTRlsInfoRptRsrcVoltnsValueBeanList = new ArrayList();

				currCFTRlsInfoRptValueBean.setIdPerson((int) personId);
				currCFTRlsInfoRptValueBean.setDtCreated(new Date());
				currCFTRlsInfoRptValueBean.setIdCreatedPerson((int) userId);
				currCFTRlsInfoRptValueBean.setIdLastUpdatePerson((int) userId);
				currCFTRlsInfoRptValueBean.setDtLastUpdate(new Date());

				currCFTRlsInfoRptResourceValueBean.setNmRsrc(nameOfHome);
				currCFTRlsInfoRptResourceValueBean.setOperationType(operType);
				currCFTRlsInfoRptResourceValueBean.setRsrc((long) nbrRsrc);
				currCFTRlsInfoRptResourceValueBean.setDtCreated(new Date());
				currCFTRlsInfoRptResourceValueBean.setIdCreatedPerson(userId);
				currCFTRlsInfoRptResourceValueBean.setIdLastUpdatePerson(userId);
				currCFTRlsInfoRptResourceValueBean.setDtLastUpdate(new Date());
				currCFTRlsInfoRptResourceValueBean.setDtDateOperationLicensed(dateOperationLicensed);
				if (operType.equals(lookupDao.simpleDecodeSafe("CRCLFACD", ServiceConstants.CRCLFACD_AFH))) {
					try {
						resultSet = (ResultSet) callStatement.getObject(6);
						while (resultSet.next()) {
							CFTRlsInfoRptCPAValueModBean currCFTRlsInfoRptCPAValueBean = new CFTRlsInfoRptCPAValueModBean();
							currCFTRlsInfoRptCPAValueBean
									.setNmCpa(resultSet.getString(1) == null ? "" : resultSet.getString(1));
							currCFTRlsInfoRptCPAValueBean
									.setDtCpaVerified(resultSet.getDate(2) == null ? null : resultSet.getDate(2));
							currCFTRlsInfoRptCPAValueBean
									.setDtCpaLicensed(resultSet.getDate(3) == null ? null : resultSet.getDate(3));
							currCFTRlsInfoRptCPAValueBean.setDtAHVerifRelinquished(
									resultSet.getDate(4) == null ? null : resultSet.getDate(4));
							currCFTRlsInfoRptCPAValueBean.setReasonAHVerifRelinquished(
									resultSet.getString(5) == null ? "" : resultSet.getString(5));
							currCFTRlsInfoRptCPAValueBean.setDtCreated(new Date());
							currCFTRlsInfoRptCPAValueBean.setDtLastUpdate(new Date());

							currCFTRlsInfoRptCPAValueBeanList.add(currCFTRlsInfoRptCPAValueBean);
						}
					} catch (Exception e) {
						log.error(e.getMessage());
						DataLayerException dataException = new DataLayerException(e.getMessage());
						dataException.initCause(e);
						throw dataException;
					}
					try {
						resultSet = (ResultSet) callStatement.getObject(7);
						while (resultSet.next()) {
							CFTRlsInfoRptCPAValueModBean currCFTRlsInfoRptCPAValueBean = new CFTRlsInfoRptCPAValueModBean();
							currCFTRlsInfoRptCPAValueBean
									.setNmCpa(resultSet.getString(1) == null ? "" : resultSet.getString(1));
							currCFTRlsInfoRptCPAValueBean
									.setDtCpaVerified(resultSet.getDate(2) == null ? null : resultSet.getDate(2));
							currCFTRlsInfoRptCPAValueBean
									.setDtCpaLicensed(resultSet.getDate(3) == null ? null : resultSet.getDate(3));
							currCFTRlsInfoRptCPAValueBean.setDtAHVerifRelinquished(
									resultSet.getDate(4) == null ? null : resultSet.getDate(4));
							currCFTRlsInfoRptCPAValueBean.setReasonAHVerifRelinquished(
									resultSet.getString(5) == null ? "" : resultSet.getString(5));
							currCFTRlsInfoRptCPAValueBean.setDtCreated(new Date());
							currCFTRlsInfoRptCPAValueBean.setDtLastUpdate(new Date());
							// don't add same CPA entry if it's been already
							// present
							// (added by p_cur_agency_history_ct)
							if (!currCFTRlsInfoRptCPAValueBeanList.contains(currCFTRlsInfoRptCPAValueBean)) {
								currCFTRlsInfoRptCPAValueBeanList.add(currCFTRlsInfoRptCPAValueBean);
							}
						}
					} catch (Exception e) {
						log.error("p_cur_agency_history_cl:" + e.getMessage());
						DataLayerException dataException = new DataLayerException(e.getMessage());
						dataException.initCause(e);
						throw dataException;
					}
					try {
						resultSet = (ResultSet) callStatement.getObject(8);
						while (resultSet.next()) {
							CFTRlsInfoRptCPAValueModBean currCFTRlsInfoRptCPAValueBean = new CFTRlsInfoRptCPAValueModBean();
							currCFTRlsInfoRptCPAValueBean
									.setNmCpa(resultSet.getString(1) == null ? "" : resultSet.getString(1));
							currCFTRlsInfoRptCPAValueBean
									.setDtCpaVerified(resultSet.getDate(2) == null ? null : resultSet.getDate(2));
							currCFTRlsInfoRptCPAValueBean
									.setDtCpaLicensed(resultSet.getDate(3) == null ? null : resultSet.getDate(3));
							currCFTRlsInfoRptCPAValueBean.setDtAHVerifRelinquished(
									resultSet.getDate(4) == null ? null : resultSet.getDate(4));
							currCFTRlsInfoRptCPAValueBean.setReasonAHVerifRelinquished(
									resultSet.getString(5) == null ? "" : resultSet.getString(5));
							currCFTRlsInfoRptCPAValueBean.setDtCreated(new Date());
							currCFTRlsInfoRptCPAValueBean.setDtLastUpdate(new Date());
							// don't add same CPA entry if it's been already
							// present
							// (added by p_cur_agency_history_cl)
							if (!currCFTRlsInfoRptCPAValueBeanList.contains(currCFTRlsInfoRptCPAValueBean)) {
								currCFTRlsInfoRptCPAValueBeanList.add(currCFTRlsInfoRptCPAValueBean);
							}
						}
					} catch (Exception e) {
						log.error("p_cur_agency_history_inv:" + e.getMessage());
						DataLayerException dataException = new DataLayerException(e.getMessage());
						dataException.initCause(e);
						throw dataException;
					}
				}
				// parse Allegation list - child is the deceased victim
				try {
					resultSet = (ResultSet) callStatement.getObject(9);
					while (resultSet.next()) {
						CFTRlsInfoRptAllegDispValueBean currCFTRlsInfoRptAllegDispValueBean = new CFTRlsInfoRptAllegDispValueBean();
						currCFTRlsInfoRptAllegDispValueBean.setDtInvStart((Date) resultSet.getDate(1));
						currCFTRlsInfoRptAllegDispValueBean.setAllegType(resultSet.getString(2));
						currCFTRlsInfoRptAllegDispValueBean.setAllegDisposition(resultSet.getString(3));
						currCFTRlsInfoRptAllegDispValueBean
								.setIndPendingAppeal(resultSet.getString(4) == null ? "N" : resultSet.getString(4));
						currCFTRlsInfoRptAllegDispValueBean.setIndDeceasedAllegedVictim(resultSet.getString(5));
						currCFTRlsInfoRptAllegDispValueBean.setDtCreated(new Date());
						currCFTRlsInfoRptAllegDispValueBean.setDtLastUpdate(new Date());

						currCFTRlsInfoRptAllegDispValueBeanList.add(currCFTRlsInfoRptAllegDispValueBean);
					}
				} catch (Exception e) {
					log.error("Error handled for Y victims alleg" + e.getMessage());
					DataLayerException dataException = new DataLayerException(e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}

				// parse Allegation list - child is NOT the deceased victim
				try {
					resultSet = (ResultSet) callStatement.getObject(10);
					while (resultSet.next()) {
						CFTRlsInfoRptAllegDispValueBean currCFTRlsInfoRptAllegDispValueBean = new CFTRlsInfoRptAllegDispValueBean();
						currCFTRlsInfoRptAllegDispValueBean.setDtInvStart((Date) resultSet.getDate(1));
						currCFTRlsInfoRptAllegDispValueBean.setAllegType(resultSet.getString(2));
						currCFTRlsInfoRptAllegDispValueBean.setAllegDisposition(resultSet.getString(3));
						currCFTRlsInfoRptAllegDispValueBean
								.setIndPendingAppeal(resultSet.getString(4) == null ? "N" : resultSet.getString(4));
						currCFTRlsInfoRptAllegDispValueBean.setIndDeceasedAllegedVictim(resultSet.getString(5));
						currCFTRlsInfoRptAllegDispValueBean.setDtCreated(new Date());
						currCFTRlsInfoRptAllegDispValueBean.setDtLastUpdate(new Date());

						currCFTRlsInfoRptAllegDispValueBeanList.add(currCFTRlsInfoRptAllegDispValueBean);
					}
				} catch (Exception e) {
					log.error("Error handled for N victims alleg:" + e.getMessage());
					DataLayerException dataException = new DataLayerException(e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}

				// parse standard violation list IFFH 43311691
				try {
					resultSet = (ResultSet) callStatement.getObject(11);
					while (resultSet.next()) {

						CFTRlsInfoRptRsrcVoltnsValueBean currCFTRlsInfoRptRsrcVoltnsValueBean = new CFTRlsInfoRptRsrcVoltnsValueBean();
						currCFTRlsInfoRptRsrcVoltnsValueBean.setDtViolation((Date) resultSet.getDate(1));
						currCFTRlsInfoRptRsrcVoltnsValueBean.setTac(resultSet.getString(2));
						currCFTRlsInfoRptRsrcVoltnsValueBean.setTacDesc(resultSet.getString(3));
						currCFTRlsInfoRptRsrcVoltnsValueBean.setDtCreated(new Date());
						currCFTRlsInfoRptRsrcVoltnsValueBean.setDtLastUpdate(new Date());

						currCFTRlsInfoRptRsrcVoltnsValueBeanList.add(currCFTRlsInfoRptRsrcVoltnsValueBean);
					}
				} catch (Exception e) {
					log.error("Error handled for std violations retrieval:" + e.getMessage());
					DataLayerException dataException = new DataLayerException(e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}

				currCFT1050BReportDB.setCftRlsInfoRpt(currCFTRlsInfoRptValueBean);
				currCFT1050BReportDB.setRlsInfoRptRsrc(currCFTRlsInfoRptResourceValueBean);
				currCFT1050BReportDB.setRlsInfoRptCPAList(currCFTRlsInfoRptCPAValueBeanList);
				currCFT1050BReportDB.setRlsInfoAllegDispositions(currCFTRlsInfoRptAllegDispValueBeanList);
				currCFT1050BReportDB.setRlsInfoRptRsrcVoltns(currCFTRlsInfoRptRsrcVoltnsValueBeanList);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			DataLayerException dataException = new DataLayerException(e.getMessage());
			dataException.initCause(e);
			throw dataException;
		} finally {
			if (!ObjectUtils.isEmpty(callStatement)) {
				try {
					callStatement.close();
				} catch (SQLException e) {
					log.error(e.getMessage());
					DataLayerException dataException = new DataLayerException(e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}
			}
		}
		return currCFT1050BReportDB;

	}

	/**
	 * Method Name: getContactType Method Description:This method retrieves
	 * Contact Type for the given Contact Event Id.
	 * 
	 * @param idEvent
	 * @return String
	 */
	@Override
	public String getContactType(long idEvent) {
		String contactType = ServiceConstants.EMPTY_STR;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contact.class);
		criteria.add(Restrictions.eq("idEvent", idEvent));

		Contact contact = (Contact) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(contact)) {
			contactType = contact.getCdContactType();
		}
		return contactType;
	}

	/**
	 * Function and related back end CALL to get operType based on stage.
	 * 
	 * @param idStage
	 * @return String
	 */

	@Override
	public String fetchFacilityType(Long idStage) {
		String facilityType = "";
		int errorCode = 0;
		String errorMessage = null;
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();

		ErrorDto errorDto = new ErrorDto();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		try {
			callStatement = connection.prepareCall(fetchFacilityType);
			callStatement.setLong(2, idStage);
			callStatement.registerOutParameter(1, java.sql.Types.VARCHAR);
			callStatement.registerOutParameter(3, java.sql.Types.BIGINT);
			callStatement.registerOutParameter(4, java.sql.Types.VARCHAR);
			callStatement.executeUpdate();
			errorCode = callStatement.getInt(3);
			errorMessage = callStatement.getString(4);
			if (errorCode < 0) {
				throw new SQLException();
			}
			facilityType = (String) callStatement.getString(1);
			return facilityType;
		} catch (Exception e) {
			errorDto.setErrorMsg(
					"Validating Name failed. Please contact the CSC and provide them with the following information: Common Application database is down. "
							+ errorMessage);
		} finally {
			if (!ObjectUtils.isEmpty(callStatement)) {
				try {
					callStatement.close();
				} catch (SQLException e) {
					log.error(e.getMessage());
					DataLayerException dataException = new DataLayerException(
							errorCode + errorMessage + e.getMessage());
					dataException.initCause(e);
					throw dataException;
				}
			}
		}
		return facilityType;
	}

	/**
	 * Method Name: saveContact Description: This method save contact
	 * 
	 * @param Contact
	 */

	@Override
	public void saveContact(Contact contact) {
		sessionFactory.getCurrentSession().save(contact);
	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contact
	 * @
	 */
	@Override
	public void updateContact(Contact contact) {
		sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(contact));
	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contact
	 * @
	 */
	@Override
	public void deleteContact(Contact contact) {

		sessionFactory.getCurrentSession().delete(contact);

	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contact
	 * @
	 */
	@Override
	public Contact getContactEntityById(Long idContact) {
		Contact contact = new Contact();
		Query queryContact = sessionFactory.getCurrentSession().createQuery(getContactEntityByIdSql);
		queryContact.setParameter("idEvent", idContact);
		return contact;
	}

	/**
	 * Method Name: getContact Description: This method returns contact entity
	 * for a giving contact id
	 * 
	 * @param idContact
	 * @return Contact
	 */
	@Override
	public Contact getContact(Long idContact) {
		return (Contact) sessionFactory.getCurrentSession().get(Contact.class, idContact);
	}

	/**
	 * This DAM is an AUD for the CONTACT table. Inputs: pSQLCA -- SQL
	 * communication area for Oracle return data. pInputDataRec -- Record
	 * containing a stage id. pOutputDataRec -- Record containing a stage table
	 * row
	 * 
	 * Outputs: Service return code.
	 * 
	 * NOTE: This DAM contains non-GENDAM generated code which would need to be
	 * copied if this DAM is re-GENDAM'd.
	 * 
	 * 
	 * Tuxedo Service Name: CCMN04U, DAM Name: CSYS07D
	 * 
	 * @param contact
	 * @
	 */

	@Override
	public ContactDto getContactById(Long idContact) {
		Query queryContact = sessionFactory.getCurrentSession().createSQLQuery(getContactByIdSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idContactStage", StandardBasicTypes.LONG)
				.addScalar("dtCntctMnthlySummBeg", StandardBasicTypes.DATE)
				.addScalar("dtCntctMnthlySummEnd", StandardBasicTypes.DATE)
				.addScalar("dtContactApprv", StandardBasicTypes.DATE)
				.addScalar("cdContactLocation", StandardBasicTypes.STRING)
				.addScalar("cdContactMethod", StandardBasicTypes.STRING)
				.addScalar("cdContactOthers", StandardBasicTypes.STRING)
				.addScalar("cdContactPurpose", StandardBasicTypes.STRING)
				.addScalar("cdContactType", StandardBasicTypes.STRING)
				.addScalar("dtContactOccurred", StandardBasicTypes.DATE)
				.addScalar("dtCntctNextSummDue", StandardBasicTypes.DATE)
				.addScalar("indContactAttempted", StandardBasicTypes.STRING)
				.addScalar("idLastEmpUpdate", StandardBasicTypes.LONG)
				.addScalar("dtLastEmpUpdate", StandardBasicTypes.DATE)
				.addScalar("indEmergency", StandardBasicTypes.STRING)
				.addScalar("cdRsnScrout", StandardBasicTypes.STRING).addScalar("indRecCons", StandardBasicTypes.STRING)
				.addScalar("txtKinCaregiver", StandardBasicTypes.STRING)
				.addScalar("cdRsnAmtne", StandardBasicTypes.STRING).addScalar("amtNeeded", StandardBasicTypes.STRING)
				.addScalar("indSiblingVisit", StandardBasicTypes.STRING)
				.addScalar("cdChildSafety", StandardBasicTypes.STRING)
				.addScalar("cdPendLegalAction", StandardBasicTypes.STRING)
				.addScalar("indPrincipalInterview", StandardBasicTypes.STRING)
				.addScalar("cdProfCollateral", StandardBasicTypes.STRING)
				.addScalar("cdAdministrative", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING)
				.addScalar("indAnnounced", StandardBasicTypes.STRING)
				.addScalar("indSafPlanComp", StandardBasicTypes.STRING)
				.addScalar("indFamPlanComp", StandardBasicTypes.STRING)
				.addScalar("indSafConResolv", StandardBasicTypes.STRING)
				.addScalar("estContactHours", StandardBasicTypes.STRING)
				.addScalar("estContactMins", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ContactDto.class));
		queryContact.setParameter("idEvent", idContact);
		return (ContactDto) queryContact.uniqueResult();
	}

	/**
	 * Method Name: saveContact Method Description:Method to save the Contact
	 * person narrative into the Contact_person_narr table.
	 * 
	 * @param personNarrDto
	 * @return Long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int addContactPersonNarr(ContactPersonNarrValueDto personNarrDto) {

		Criteria creteria = sessionFactory.getCurrentSession().createCriteria(DocumentTemplate.class);
		creteria.add(Restrictions.eq("documentTemplateType.idDocumentTemplateType", (long) 29));

		List<DocumentTemplate> results = new ArrayList<DocumentTemplate>();
		results = (ArrayList<DocumentTemplate>) creteria.list();

		long idDocumentTemplate = 0l;
		if (!ObjectUtils.isEmpty(results))
			idDocumentTemplate = results.get(0).getIdDocumentTemplate();

		ContactPersonNarr contactPersonNarr = new ContactPersonNarr();

		Event event = new Event();

		contactPersonNarr.setDtLastUpdate(new Date());

		event.setIdEvent(personNarrDto.getIdEvent());
		contactPersonNarr.setEvent(event);

		contactPersonNarr.setCdRsnNotNotified(personNarrDto.getCdRsnNotNofified());

		if (personNarrDto.getIdPerson() != 0 && personNarrDto.getIdPerson() > 0) {
			Person person = new Person();
			person.setIdPerson(personNarrDto.getIdPerson());
			contactPersonNarr.setPerson(person);
		}
		contactPersonNarr.setNarrative(personNarrDto.getNarrative());
		contactPersonNarr.setIdDocumentTemplate(idDocumentTemplate);
		contactPersonNarr.setIdCreatedPerson(personNarrDto.getIdCreatedPerson());
		contactPersonNarr.setIdLastUpdatePerson(personNarrDto.getIdLastUpdatePerosn());
		contactPersonNarr.setDtLastUpdate(new Date());
		contactPersonNarr.setDtCreated(new Date());

		long result = (long) sessionFactory.getCurrentSession().save(contactPersonNarr);

		if (result != 0)
			return 1;
		return 0;

	}

	/**
	 * Method to delete the Contact Guide Narrative
	 * 
	 * @param personNarrBean
	 * @return @
	 */
	@Override
	public long deleteContactPersonNarr(ContactPersonNarrValueDto personNarrDto) {
		ContactPersonNarr contactPersonNarr1 = (ContactPersonNarr) sessionFactory.getCurrentSession()
				.get(ContactPersonNarr.class, personNarrDto.getIdContactPersonNarr());
		long deletedRowCount = ServiceConstants.ZERO_VAL;
		if (!ObjectUtils.isEmpty(contactPersonNarr1)) {
			sessionFactory.getCurrentSession().delete(contactPersonNarr1);
			deletedRowCount++;
		}
		return deletedRowCount;
	}

	/**
	 * Method to update the Contact Guide Narrative
	 * 
	 * @param personNarrDto
	 * @return int
	 */
	@Override
	public int updateContactPersonNarr(ContactPersonNarrValueDto personNarrDto) {

		ContactPersonNarr contactPersonNarr = null;

		if (ServiceConstants.SERVER_IMPACT) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContactPersonNarr.class);
			criteria.add(
					Restrictions.and(Restrictions.eq("idContactPersonNarr", personNarrDto.getIdContactPersonNarr())));
			contactPersonNarr = (ContactPersonNarr) criteria.uniqueResult();
			if (ObjectUtils.isEmpty(contactPersonNarr)) {
				throw new DataLayerException("No record available to update");
			}
			contactPersonNarr.setCdRsnNotNotified(personNarrDto.getCdRsnNotNofified());

			contactPersonNarr.setNarrative(personNarrDto.getNarrative());
			sessionFactory.getCurrentSession().saveOrUpdate(contactPersonNarr);

		}
		return 0;

	}

	@SuppressWarnings({ "unchecked" })
	public Long updateApproversStatus(int idEvent) {
		Criteria creteria = sessionFactory.getCurrentSession().createCriteria(ApprovalEventLink.class);
		creteria.add(Restrictions.eq("idEvent", (long) idEvent));

		ApprovalEventLink results = (ApprovalEventLink) creteria.uniqueResult();

		long idApproval = results.getIdApproval();

		if (idApproval != 0) {
			Criteria creteriaEventUpdate = sessionFactory.getCurrentSession().createCriteria(Event.class);

			String[] cdTaskList = new String[4];

			cdTaskList[0] = ServiceConstants.CD_TASK_CPS_INV_CON;
			cdTaskList[1] = ServiceConstants.CD_TASK_RCL_INV_CON;
			cdTaskList[2] = ServiceConstants.CD_TASK_CPS_INV_CON_APRVL;
			cdTaskList[3] = ServiceConstants.CD_TASK_RCL_INV_CON_APRVL;

			creteriaEventUpdate.add(Restrictions.and(Restrictions.eq("idEvent", (long) idEvent),
					Restrictions.in("cdTask", cdTaskList)));

			Event event = (Event) creteriaEventUpdate.uniqueResult();
			if (ObjectUtils.isEmpty(event)) {
				throw new DataLayerException("TimestampMismatch");
			}
			event.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);

			sessionFactory.getCurrentSession().saveOrUpdate(event);

			sessionFactory.getCurrentSession().createCriteria(Approvers.class);

			creteria.add(Restrictions.eq("idApproval", idApproval));

			List<Approvers> approversList = creteria.list();

			if (approversList != null && !approversList.isEmpty()) {
				for (Approvers approvers : approversList) {
					approvers.setCdApproversStatus(ServiceConstants.INVALID_APPROVAL_STATUS);
					sessionFactory.getCurrentSession().saveOrUpdate(approvers);
				}
			}

			if (approversList == null) {
				throw new DataLayerException("TimestampMismatch");
			} else
				return 1L;

		}

		// end of if for id approval
		else {
			throw new DataLayerException(lookupDao.getMessageByNumber(ServiceConstants.SQL_NOT_FOUND));
		}

	}

	public Long updateAppEventStatus(int idEvent, String eventStatus) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateAppEventStatusSql);

		query.setParameter("eventStatus", eventStatus);

		query.setParameter("idEvent", idEvent);

		long noOfUpdates = (long) query.executeUpdate();

		return noOfUpdates;

	}

	/**
	 * Method Name: isCrimHistCheckPending Method Description: Check if any DPS
	 * Criminal History check is pending
	 * 
	 * @param idStage
	 * @return boolean @
	 */
	@Override
	public boolean isCrimHistCheckPending(long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RecordsCheck.class);
		criteria.add(Restrictions.eq("cdRecCheckCheckType", CodesConstant.CCHKTYPE_10));
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.isNull("dtRecCheckCompleted"));

		int personCount = criteria.list().size();
		if (personCount > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Method Name: deleteCFTRlsInfoRpt Method Description: This method deletes
	 * record from FtRlsInfoRpt table.
	 * 
	 * @param idEvent
	 * @
	 */

	public void deleteCFTRlsInfoRpt(Long idEvent) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteFtRlsInfoRpt)
				.setParameter("idEvent", idEvent);
		query.executeUpdate();
	}

	/**
	 * Method Name: deleteRlsInfoRptCPSByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptCps table.
	 * 
	 * @param idEvent
	 * @
	 */
	public void deleteRlsInfoRptCPSByRptId(Long rptId) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteRlsInfoRptCPSByRptId)
				.setParameter("rptId", rptId);
		query.executeUpdate();
	}

	/**
	 * Method Name: deleteRlsInfoAllegDispositionByRptId Method Description:
	 * This method deletes record from FtRlsInfoRptAllegDisp table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRlsInfoAllegDispositionByRptId(Long rptId) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(deleteRlsInfoAllegDispositionByRptId).setLong("rptId", rptId);
		query.executeUpdate();
	}

	/**
	 * Method Name: deleteRlsInfoRptRsrcByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptRsrc table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRlsInfoRptRsrcByRptId(Long rptId) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteRlsInfoRptRsrcByRptId)
				.setLong("rptId", rptId);
		query.executeUpdate();
	}

	/**
	 * Method Name: deleteRsrcViolationByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptRsrcVoltns table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRsrcViolationByRptId(Long rptId) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteRsrcViolationByRptId)
				.setParameter("rptId", rptId);
		query.executeUpdate();
	}

	/**
	 * Method Name: deleteRlsInfoRptCPAByRptId Method Description: This method
	 * deletes record from FtRlsInfoRptCpa table.
	 * 
	 * @param rptId
	 * @
	 */
	public void deleteRlsInfoRptCPAByRptId(Long rptId) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteRlsInfoRptCPAByRptId)
				.setParameter("rptId", rptId);
		query.executeUpdate();
	}

	/**
	 * Method Name: getReasonRlngshmntCodes Method Description: This method is
	 * to get ReasonRlngshmntCodes record from C_CODE_TABLE_ROW table.
	 * 
	 * @return Map<String, String>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, String> getReasonRlngshmntCodes() {
		SQLQuery queryContact = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(reasonRlngshmntCodes)
				.addScalar("code", StandardBasicTypes.STRING).addScalar("decode", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CodeAttributes.class));
		queryContact.setParameter("nmtableString", "ReasonRlngshmnt");
		List<CodeAttributes> codeList = (List<CodeAttributes>) queryContact.list();
		Map<String, String> codeMap = new LinkedHashMap();
		codeList.stream().forEach(e -> codeMap.put(e.getCode(), e.getDecode()));
		return codeMap;
	}

	/**
	 * Method Name: getAllPersonRecords Method Description: This method receives
	 * ID STAGE from the service and returns one or more rows from the
	 * STAGE_PERSON_LINK, NAME, PERSON, ADDRESS_PERSON_LINK, PERSON_ADDRESS,
	 * PERSON_PHONE tables. (CLSC0DD)
	 * 
	 * @param idStage
	 * @return List<PersonDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> getAllPersonRecords(Long idStage) {
		List<PersonDto> personList = null;
		String strQuery = "";
		if(mobileUtil.isMPSEnvironment()){
			strQuery = getAllPersonsRecordsForMPSSql;
		} else {
			strQuery = getAllPersonsRecordsSql;
		}
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(strQuery)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
				.addScalar("cdPersonState", StandardBasicTypes.STRING)
				.addScalar("personPhone", StandardBasicTypes.STRING)
				.addScalar("phoneExtnsn", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("dob", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdEthniCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonCitizenShip", StandardBasicTypes.STRING)
				.addScalar("personEthnicGroup", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));

		personList = (List<PersonDto>) query.list();
		return personList;
	}

	/**
	 * Method Name: getPersonsAddrDtls Method Description:This method receives
	 * ID_PERSON and returns one or more rows from the ADDRESS_PERSON_LINK,
	 * PERSON_ADDRESS tables for non-invalid non-endated addresses. (CLSC1BD)
	 * 
	 * @param idPerson
	 * @return List<PersonDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> getPersonsAddrDtls(Long idPerson) {
		List<PersonDto> personList = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getPersonsAddrDtlsSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("addrPersonFull", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonAttn", StandardBasicTypes.STRING)
				.addScalar("addrPersonZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
				.addScalar("cdPersonState", StandardBasicTypes.STRING)
				.addScalar("addrPersonLink", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		personList = (List<PersonDto>) query.list();
		return personList;
	}

	/**
	 * Method Name: getRecentClosedIdStage Method Description: This method Gets
	 * the most recently closed previous ID STAGE for a given ID STAGE.
	 * (ccmnb5d)
	 * 
	 * @param idStage
	 * @return StageLinkDto
	 */
	@Override
	public StageLinkDto getRecentClosedIdStage(Long idStage) {
		StageLinkDto stageLinkDto = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getRecentClosedIdStageSql)
				.addScalar("idPriorStage", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(StageLinkDto.class));
		stageLinkDto = (StageLinkDto) query.uniqueResult();
		return stageLinkDto;
	}

	/**
	 * Method Name: getContactActiveAddr Method Description: This method
	 * Retrieves active primary address, phone number, and name for an employee
	 * (CSEC01D)
	 * 
	 * @param idPerson
	 * @return ContactActiveAddrDto
	 */
	@Override
	public ContactActiveAddrDto getContactActiveAddr(Long idPerson) {
		ContactActiveAddrDto contactActiveAddrDto = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getContactActiveAddrSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nbrEmpActivePct", StandardBasicTypes.STRING)
				.addScalar("dtEmpHire", StandardBasicTypes.TIMESTAMP)
				.addScalar("idEmpJobHistory", StandardBasicTypes.LONG)
				.addScalar("idEmployeeLogon", StandardBasicTypes.LONG)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("txtEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("cdEmpSecurityClassNm", StandardBasicTypes.STRING)
				.addScalar("idEmpOffice", StandardBasicTypes.LONG)
				.addScalar("dtEmpLastAssigned", StandardBasicTypes.TIMESTAMP)
				.addScalar("indEmpConfirmedHrmis", StandardBasicTypes.STRING)
				.addScalar("indEmpPendingHrmis", StandardBasicTypes.STRING)
				.addScalar("indEmpActiveStatus", StandardBasicTypes.STRING)
				.addScalar("dtEmpTermination", StandardBasicTypes.TIMESTAMP)
				.addScalar("idJobPersSupv", StandardBasicTypes.LONG).addScalar("cdJobClass", StandardBasicTypes.STRING)
				.addScalar("nbrEmpActivePct", StandardBasicTypes.STRING)
				.addScalar("dtEmpHire", StandardBasicTypes.TIMESTAMP)
				.addScalar("idEmpJobHistory", StandardBasicTypes.LONG)
				.addScalar("idEmployeeLogon", StandardBasicTypes.LONG)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("txtEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("cdEmpSecurityClassNm", StandardBasicTypes.STRING)
				.addScalar("idEmpOffice", StandardBasicTypes.LONG)
				.addScalar("dtEmpLastAssigned", StandardBasicTypes.TIMESTAMP)
				.addScalar("indEmpConfirmedHrmis", StandardBasicTypes.STRING)
				.addScalar("indEmpPendingHrmis", StandardBasicTypes.STRING)
				.addScalar("indEmpActiveStatus", StandardBasicTypes.STRING)
				.addScalar("dtEmpTermination", StandardBasicTypes.TIMESTAMP)
				.addScalar("idJobPersSupv", StandardBasicTypes.LONG).addScalar("cdJobClass", StandardBasicTypes.STRING)
				.addScalar("txtJobDescr", StandardBasicTypes.STRING)
				.addScalar("indJobAssignable", StandardBasicTypes.STRING)
				.addScalar("cdJobFunction", StandardBasicTypes.STRING).addScalar("cdJobBjn", StandardBasicTypes.STRING)
				.addScalar("dtJobEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtJobStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdOfficeMail", StandardBasicTypes.STRING)
				.addScalar("cdOfficeProgram", StandardBasicTypes.STRING)
				.addScalar("cdOfficeRegion", StandardBasicTypes.STRING)
				.addScalar("nmOfficeName", StandardBasicTypes.STRING)
				.addScalar("nbrMailCodePhone", StandardBasicTypes.STRING)
				.addScalar("nbrMailCodePhoneExt", StandardBasicTypes.STRING)
				.addScalar("addrMailCodeStLnOne", StandardBasicTypes.STRING)
				.addScalar("addrMailCodeStLnTwo", StandardBasicTypes.STRING)
				.addScalar("addrMailCodeCity", StandardBasicTypes.STRING)
				.addScalar("addrMailCodeZip", StandardBasicTypes.STRING)
				.addScalar("addrMailCodeCounty", StandardBasicTypes.STRING)
				.addScalar("indMailCodeInvalid", StandardBasicTypes.STRING)
				.addScalar("idPersonPhone", StandardBasicTypes.LONG)
				.addScalar("txtPersonPhoneComments", StandardBasicTypes.LONG)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("dtPersonPhoneStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonPhoneEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("indPersonPhoneInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersonPhonePrimary", StandardBasicTypes.STRING)
				.addScalar("cdPersonPhoneType", StandardBasicTypes.STRING).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtNameStartDate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtNameEndDate", StandardBasicTypes.TIMESTAMP).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(ContactActiveAddrDto.class));
		contactActiveAddrDto = (ContactActiveAddrDto) query.uniqueResult();
		return contactActiveAddrDto;
	}

	/**
	 * Method Name: getDistinctAllgtnList Method Description: Get distinct list
	 * of allegation records for a given ID_ALLEGATION_STAGE. If the perpetrator
	 * = victim, return SELF_NEGLECT in szCdDecodeAllegType. (CLSSABD)
	 * 
	 * @param idStage
	 * @return List<AllegationDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationDto> getDistinctAllgtnList(Long idStage) {
		List<AllegationDto> allegationDto = null;
		String strQuery = getDistinctAllgtnListSql;
		if(mobileUtil.isMPSEnvironment()){
			strQuery = getDistinctAllgtnListSqlForMPS;
		}
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(strQuery)
				.addScalar("indSelfNeglect", StandardBasicTypes.STRING)
				.addScalar("cdAllegType", StandardBasicTypes.STRING)
				.addScalar("cdAllegDisposition", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(AllegationDto.class));
		allegationDto = (List<AllegationDto>) query.list();
		return allegationDto;
	}
	
	
	/**
	 * Method Name: getRiskNarrExists Method Description: This method gets the Narrative
	 * Count from RISK_ASSMT_IRA_NARR and RISK_ASSMT_NARR which helps to set the DocType
	 * for Structured Narrative
	 * 
	 * @param idCase
	 * @param idEvent
	 * @return String
	 */
	//Warranty Defect - 11243 - To display both Versions of Structured Narrative RISKSF and CIV33O00
	@Override
	public String getRiskNarrExists(Long idCase,Long idEvent) {
		
		String defaultDocType="RISKSF";
		
		// To check whether the Narrative Exists in RISK_ASSMT_NARR
		Query isRiskAssmtNarrExistsquery = (Query) sessionFactory.getCurrentSession().createSQLQuery(getRiskAssmtNarrSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", idCase)
				.setParameter("idEvent", idEvent);
		Long isRiskAssmtNarrExists = (Long) isRiskAssmtNarrExistsquery.uniqueResult();	
		
		
		// To check whether the Narrative Exists in RISK_ASSMT_IRA_NARR
		Query isRiskAssmtIarNarrExistsquery = (Query) sessionFactory.getCurrentSession().createSQLQuery(getRiskAssmtIarNarrSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idCase", idCase)
				.setParameter("idEvent", idEvent);
		Long isRiskIarAssmtNarrExists = (Long) isRiskAssmtIarNarrExistsquery.uniqueResult();	
		
		if(!ObjectUtils.isEmpty(isRiskAssmtNarrExists))
		{
			defaultDocType="RISKSF";			
		} 
		else if(!ObjectUtils.isEmpty(isRiskIarAssmtNarrExists))
		{
			defaultDocType="CIV33O00";
		}
		
		return defaultDocType;
	}

}