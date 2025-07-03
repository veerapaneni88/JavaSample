package us.tx.state.dfps.service.SDM.daoimpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.AssessmentHouseholdLink;
import us.tx.state.dfps.common.domain.CpsSa;
import us.tx.state.dfps.common.domain.CpsSaAssmtLookup;
import us.tx.state.dfps.common.domain.CpsSaDeleteLog;
import us.tx.state.dfps.common.domain.CpsSaFollowupLookup;
import us.tx.state.dfps.common.domain.CpsSaFollowupResponse;
import us.tx.state.dfps.common.domain.CpsSaQstnLookup;
import us.tx.state.dfps.common.domain.CpsSaResponse;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentResponseDto;
import us.tx.state.dfps.service.person.dto.PersonInfoDto;

/**
 * Dao implementation for functions required for implementing SDM Safety
 * Assessment functionality
 *
 */
@Repository
public class SDMSafetyAssessmentDaoImpl implements SDMSafetyAssessmentDao {

	@Value("${SDMSafetyAssessmentDaoImpl.cpsSafetyAssmtDetails}")
	private transient String cpsSafetyAssmtDetailsSql;

	@Value("${SDMSafetyAssessmentDaoImpl.cpsSaResponse}")
	private transient String cpsSaResponseSql;

	@Value("${SDMSafetyAssessmentDaoImpl.cpsSaFollowupResponse}")
	private transient String cpsSaFollowupResponseSql;

	@Value("${SDMSafetyAssessmentDaoImpl.isSftAsmntInProcStatusAvail}")
	private transient String isSftAsmntInProcStatusAvail;

	@Value("${SDMSafetyAssessmentDaoImpl.isNewSftAsmntInProcStatusAvail}")
	private transient String isNewSftAsmntInProcStatusAvail;

	@Value("${SDMSafetyAssessmentDaoImpl.SDM_SAFETY_ASSESSMENT_FORM_LOOKUP_SQL}")
	private transient String safetyAsmtFormLookUpSql;

	@Value("${SDMSafetyAssessmentDaoImpl.SDM_FOLLOWUP_LOOKUP_SQL}")
	private transient String safetyAsmtFollowLookUpSql;

	@Value("${SDMSafetyAssessmentDaoImpl.SAFETY_ASSESSMENT_COUNT_SQL}")
	private transient String SAFETY_ASSESSMENT_COUNT_SQL;

	@Value("${SDMSafetyAssessmentDaoImpl.GET_APPRVD_AR_STAGE_SAFETY_COUNT_SQL}")
	private transient String GET_APPRVD_AR_STAGE_SAFETY_COUNT_SQL;

	@Value("${SDMSafetyAssessmentDaoImpl.GET_APPRVD_7_DAY_SAFETY_COUNT_SQL}")
	private transient String GET_APPRVD_7_DAY_SAFETY_COUNT_SQL;

	@Value("${SDMSafetyAssessmentDaoImpl.getHouseHoldName}")
	private transient String getHouseHoldNamesql;

	@Value("${SDMSafetyAssessmentDaoImpl.HOUSEHOLD_SAFETY_ASSESSMENT_COUNT_SQL}")
	private transient String HOUSEHOLD_SAFETY_ASSESSMENT_COUNT_SQL;
	
	@Value("${SDMSafetyAssessmentDaoImpl.HOUSEHOLD_SAFETY_ASSESSMENT_COUNT_SQL2}")
	private String HOUSEHOLD_SAFETY_ASSESSMENT_COUNT_SQL2;
	
	@Value("${SDMSafetyAssessmentDaoImpl.housholdSameAsPersonAssessed}")
	private transient String housholdSameAsPersonAssessed;
	
	@Value("${SDMSafetyAssessmentDaoImpl.fetchPersonAssessedForHouseHold}")
	private transient String fetchPersonAssessedForHouseHold;
	

	@Value("${SDMSafetyAssessmentDaoImpl.getSafetyAssmentList}")
	private transient String getSafetyAssmentList;

	@Value("${SDMSafetyAssessmentDaoImpl.getSafetyAssmentResp}")
	private transient String getSafetyAssmentResp;

	@Value("${SDMSafetyAssessmentDaoImpl.getSDMFollowUpQuestions}")
	private transient String getSDMFollowUpQuestions;

	@Value("${SDMSafetyAssessmentDaoImpl.getPersonAssessedInSDMSafety}")
	private transient String getPersonAssessedInSDMSafety;
	
	@Value("${SDMSafetyAssessmentDaoImpl.getLatestSafetyAssessmentEvent}")
	private transient String getLatestSafetyAssessmentEvent;
	
	@Value("${SDMSafetyAssessmentDaoImpl.latestSafetyDtForStage}")
	private transient String latestSafetyDtForStageHql;
	
	@Value("${SDMSafetyAssessmentDaoImpl.getChildrenCareGiverAssessedInSDMSafety}")
	private transient String getChildrenCareGiverAssessedInSDMSafety;
	
	
	@Value("${SDMSafetyAssessmentDaoImpl.HOUSEHOLD_CHECK_SAFETY_ASSESSMENT_SQL}")
	private transient String householdCheckSafetyAssessmentSql;

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final List<String> AR_AND_INV_STG_TYPE_LIST = Arrays.asList(ServiceConstants.CSTAGES_AR, ServiceConstants.CSTAGES_INV);

	public SDMSafetyAssessmentDaoImpl() {

	}

	@Override
	public long deleteSafetyAssmtDetails(SDMSafetyAssessmentDto safetyAssessmentDB) {
		CpsSaDeleteLog cpsSaDeleteLog = new CpsSaDeleteLog();
		cpsSaDeleteLog.setIdEvent(safetyAssessmentDB.getIdEvent());
		cpsSaDeleteLog.setIdStage(safetyAssessmentDB.getIdStage());
		cpsSaDeleteLog.setDtDeleted(new Date());
		cpsSaDeleteLog.setIdDeletedPerson(safetyAssessmentDB.getLoggedInUser());
		cpsSaDeleteLog.setIdCreatedPerson(safetyAssessmentDB.getIdCreatedPerson());
		cpsSaDeleteLog.setCdStatus(safetyAssessmentDB.getEventStatus());
		cpsSaDeleteLog.setCdAssmtType(safetyAssessmentDB.getAssessmentType());
		cpsSaDeleteLog.setCdSafetyDecision(safetyAssessmentDB.getCdSavedSafetyDecision());
		cpsSaDeleteLog.setDtLastUpdate(new Date());
		long updateResult = (long) sessionFactory.getCurrentSession().save(cpsSaDeleteLog);
		Event eventtoDelete = (Event) sessionFactory.getCurrentSession().load(Event.class,
				safetyAssessmentDB.getIdEvent());
		sessionFactory.getCurrentSession().delete(eventtoDelete);
		return updateResult;
	}

	@Override
	public long updateSDMSafetyAssessment(SDMSafetyAssessmentDto safetyAssessmentDB) {
		CpsSa cpsSa = (CpsSa) sessionFactory.getCurrentSession().createCriteria(CpsSa.class)
				.add(Restrictions.eq("event.idEvent", safetyAssessmentDB.getIdEvent()))
				.add(Restrictions.eq("idCpsSa", Long.valueOf(safetyAssessmentDB.getId()))).uniqueResult();
		try {
			cpsSa.setDtAssessed(DateUtils.getTimestamp(safetyAssessmentDB.getDtSafetyAssessed(),
					safetyAssessmentDB.getTimeSafetyAssessed()));
		} catch (ParseException e) {
			DataLayerException dataLayerException = new DataLayerException(e.toString());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		// cpsSa.setDtAssessed(safetyAssessmentDB.getTsSafetyAssessed());
		boolean isDirty = false;
		if (!getNonNullString(cpsSa.getTxtAssmtDiscussion())
				.equals(getNonNullString(safetyAssessmentDB.getAssessmentDiscussion()))) {
			cpsSa.setTxtAssmtDiscussion(getNonNullString(safetyAssessmentDB.getAssessmentDiscussion()));
			isDirty = true;
		}
		if (!getNonNullString(cpsSa.getCdSafetyDecision())
				.equals(getNonNullString(safetyAssessmentDB.getCdCurrSafetyDecision()))) {
			cpsSa.setCdSafetyDecision(getNonNullString(safetyAssessmentDB.getCdCurrSafetyDecision()));
			isDirty = true;
		}
		if (!getNonNullString(cpsSa.getCdUnsafeDecisionAction())
				.equals(getNonNullString(safetyAssessmentDB.getUnsafeDecisionAction()))) {
			cpsSa.setCdUnsafeDecisionAction(getNonNullString(safetyAssessmentDB.getUnsafeDecisionAction()));
			isDirty = true;
		}
		if (!getNonNullString(cpsSa.getCdAssmtType())
				.equals(getNonNullString(safetyAssessmentDB.getAssessmentType()))) {
			cpsSa.setCdAssmtType(getNonNullString(safetyAssessmentDB.getAssessmentType()));
			isDirty = true;
		}
		if (ObjectUtils.isEmpty(cpsSa.getHouseHoldLink())
				&& !TypeConvUtil.isNullOrEmpty(safetyAssessmentDB.getIdHouseHoldPerson())) {
			AssessmentHouseholdLink assessmentHouseholdLink = new AssessmentHouseholdLink();
			assessmentHouseholdLink.setIdAsmntCpsSa(cpsSa);
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					safetyAssessmentDB.getIdHouseHoldPerson());
			assessmentHouseholdLink.setIdHshldPerson(person);
			assessmentHouseholdLink.setIdCreatedPerson(Long.valueOf(safetyAssessmentDB.getLoggedInUser()));
			assessmentHouseholdLink.setDtCreated(new Date());
			assessmentHouseholdLink.setDtLastUpdate(new Date());
			assessmentHouseholdLink.setIdLastUpdatePerson(new Long(safetyAssessmentDB.getLoggedInUser()));
			cpsSa.setHouseHoldLink(assessmentHouseholdLink);
			isDirty = true;
		}
		if (isDirty) {
			cpsSa.setIdLastUpdatePerson(safetyAssessmentDB.getLoggedInUser());
			cpsSa.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().update(cpsSa);
		}
		return cpsSa.getIdCpsSa();
	}

	@Override
	public long updateSDMSafetyAssessmentResponse(SDMSafetyAssessmentResponseDto safetyAssessDB) {
		CpsSaResponse cpsSaResponse = (CpsSaResponse) sessionFactory.getCurrentSession()
				.createCriteria(CpsSaResponse.class).add(Restrictions.eq("idCpsSaResponse", safetyAssessDB.getId()))
				.uniqueResult();
		boolean isDirty = false;
		if (!getNonNullString(cpsSaResponse.getCdSaAnswer())
				.equals(getNonNullString(safetyAssessDB.getCdQuestionResponse()))) {
			cpsSaResponse.setCdSaAnswer(getNonNullString(safetyAssessDB.getCdQuestionResponse()));
			isDirty = true;
		}
		if (!getNonNullString(cpsSaResponse.getTxtOtherDescription())
				.equals(getNonNullString(safetyAssessDB.getOtherDescriptionText()))) {
			cpsSaResponse.setTxtOtherDescription(getNonNullString(safetyAssessDB.getOtherDescriptionText()));
			isDirty = true;
		}
		if (isDirty) {
			cpsSaResponse.setIdLastUpdatePerson(safetyAssessDB.getLoggedInUser());
			cpsSaResponse.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().update(cpsSaResponse);
		}
		return cpsSaResponse.getIdCpsSaResponse();
	}

	@Override
	public long updateFollowupResponse(SDMSafetyAssessmentFollowupDto safetyAssessmentFollowupDB) {
		CpsSaFollowupResponse cpsSaFollowupResponse = (CpsSaFollowupResponse) sessionFactory.getCurrentSession()
				.createCriteria(CpsSaFollowupResponse.class)
				.add(Restrictions.eq("idCpsSaFollowupResponse", (long) safetyAssessmentFollowupDB.getId()))
				.uniqueResult();
		boolean isDirty = false;
		if (!getNonNullString(cpsSaFollowupResponse.getCdSaFollowupResponse())
				.equals(getNonNullString(safetyAssessmentFollowupDB.getCdFollowupResponse()))) {
			cpsSaFollowupResponse
					.setCdSaFollowupResponse(getNonNullString(safetyAssessmentFollowupDB.getCdFollowupResponse()));
			isDirty = true;
		}
		if (isDirty) {
			cpsSaFollowupResponse.setIdLastUpdatePerson(safetyAssessmentFollowupDB.getLoggedInUser());
			cpsSaFollowupResponse.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().update(cpsSaFollowupResponse);
		}
		return cpsSaFollowupResponse.getIdCpsSaFollowupResponse();
	}

	@Override
	public long addSafetyAssessment(SDMSafetyAssessmentDto safetyAssessmentDB) {
		CpsSa cpsSa = new CpsSa();
		Event event = new Event();
		event.setIdEvent(safetyAssessmentDB.getIdEvent());
		CpsSaAssmtLookup cpsSaAssmtLookup = new CpsSaAssmtLookup();
		cpsSaAssmtLookup.setIdCpsSaAssmtLookup(safetyAssessmentDB.getFormVersionNumber());
		cpsSa.setEvent(event);
		cpsSa.setIdStage(safetyAssessmentDB.getIdStage());
		cpsSa.setCpsSaAssmtLookup(cpsSaAssmtLookup);
		cpsSa.setCdAssmtType(safetyAssessmentDB.getAssessmentType());
		cpsSa.setCdSafetyDecision(getNonNullString(safetyAssessmentDB.getCdCurrSafetyDecision()));
		cpsSa.setCdUnsafeDecisionAction(getNonNullString(safetyAssessmentDB.getUnsafeDecisionAction()));
		try {
			cpsSa.setDtAssessed(DateUtils.getTimestamp(safetyAssessmentDB.getDtSafetyAssessed(),
					safetyAssessmentDB.getTimeSafetyAssessed()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			DataLayerException dataLayerException = new DataLayerException(e.toString());
			dataLayerException.initCause(e);
			throw dataLayerException;
		}
		cpsSa.setTxtAssmtDiscussion(getNonNullString(safetyAssessmentDB.getAssessmentDiscussion()));
		cpsSa.setDtCreated(new Date());
		cpsSa.setIdCreatedPerson(safetyAssessmentDB.getLoggedInUser());
		cpsSa.setIdLastUpdatePerson(safetyAssessmentDB.getLoggedInUser());
		cpsSa.setDtLastUpdate(new Date());
		Set<CpsSaResponse> cpsSaResponses = new HashSet<CpsSaResponse>();
		List<SDMSafetyAssessmentResponseDto> responseList = safetyAssessmentDB.getSafetyResponseBySectionMap()
				.entrySet().stream().flatMap(o -> o.getValue().stream()).collect(Collectors.toList());
		for (SDMSafetyAssessmentResponseDto responseDB : responseList) {
			responseDB.setLoggedInUser(safetyAssessmentDB.getLoggedInUser());
			CpsSaResponse cpsSaResponse = getTransiantResponse(responseDB, safetyAssessmentDB.getLoggedInUser());
			cpsSaResponse.setCpsSa(cpsSa);
			cpsSaResponses.add(cpsSaResponse);
			List<SDMSafetyAssessmentFollowupDto> followupList = responseDB.getFollowupResponseList();
			if (null != followupList && followupList.size() > 0) {
				Set<CpsSaFollowupResponse> cpsSaFollowupResponses = new HashSet<CpsSaFollowupResponse>(0);
				for (SDMSafetyAssessmentFollowupDto followupDB : followupList) {
					CpsSaFollowupResponse cpsSaFollowupResponse = getTransiantFollowupResponse(followupDB,
							safetyAssessmentDB.getLoggedInUser());
					cpsSaFollowupResponse.setCpsSaResponse(cpsSaResponse);
					cpsSaFollowupResponses.add(cpsSaFollowupResponse);
				}
				cpsSaResponse.setCpsSaFollowupResponses(cpsSaFollowupResponses);
			}
		}
		cpsSa.setCpsSaResponses(cpsSaResponses);
		if (!TypeConvUtil.isNullOrEmpty(safetyAssessmentDB.getIdHouseHoldPerson())) {
			AssessmentHouseholdLink assessmentHouseholdLink = new AssessmentHouseholdLink();
			assessmentHouseholdLink.setIdAsmntCpsSa(cpsSa);
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					safetyAssessmentDB.getIdHouseHoldPerson());
			assessmentHouseholdLink.setIdHshldPerson(person);
			assessmentHouseholdLink.setIdCreatedPerson(safetyAssessmentDB.getLoggedInUser());
			assessmentHouseholdLink.setDtCreated(new Date());
			assessmentHouseholdLink.setDtLastUpdate(new Date());
			assessmentHouseholdLink.setIdLastUpdatePerson(Long.valueOf(safetyAssessmentDB.getLoggedInUser()));
			cpsSa.setHouseHoldLink(assessmentHouseholdLink);
		}
		sessionFactory.getCurrentSession().save(cpsSa);
		safetyAssessmentDB.setId((int) cpsSa.getIdCpsSa());
		return cpsSa.getIdCpsSa();
	}

	private CpsSaResponse getTransiantResponse(SDMSafetyAssessmentResponseDto safetyAssessDB, long loggedInUser) {
		CpsSaResponse cpsSaResponse = new CpsSaResponse();
		if (!ObjectUtils.isEmpty(safetyAssessDB.getCdQuestionResponse()))
			cpsSaResponse.setCdSaAnswer(safetyAssessDB.getCdQuestionResponse());
		CpsSaQstnLookup cpsSaQstnLookup = (CpsSaQstnLookup) sessionFactory.getCurrentSession()
				.load(CpsSaQstnLookup.class, safetyAssessDB.getIdQuestionLookup());
		cpsSaResponse.setCpsSaQstnLookup(cpsSaQstnLookup);
		cpsSaResponse.setTxtOtherDescription(getNonNullString(safetyAssessDB.getOtherDescriptionText()));
		cpsSaResponse.setDtCreated(new Date());
		cpsSaResponse.setIdCreatedPerson(loggedInUser);
		cpsSaResponse.setIdLastUpdatePerson(loggedInUser);
		cpsSaResponse.setDtLastUpdate(new Date());
		return cpsSaResponse;
	}

	private CpsSaFollowupResponse getTransiantFollowupResponse(SDMSafetyAssessmentFollowupDto safetyAssessFollowupDB,
			long loggedInUser) {
		CpsSaFollowupResponse cpsSaFollowupResponse = new CpsSaFollowupResponse();
		CpsSaFollowupLookup cpsSaFollowupLookup = new CpsSaFollowupLookup();
		cpsSaFollowupLookup.setIdCpsSaFollowupLookup(safetyAssessFollowupDB.getIdFollowupLookup());
		cpsSaFollowupResponse.setCpsSaFollowupLookup(cpsSaFollowupLookup);
		if (!ObjectUtils.isEmpty(safetyAssessFollowupDB.getCdFollowupResponse()))
			cpsSaFollowupResponse.setCdSaFollowupResponse(safetyAssessFollowupDB.getCdFollowupResponse());
		cpsSaFollowupResponse.setDtCreated(new Date());
		cpsSaFollowupResponse.setIdCreatedPerson(loggedInUser);
		cpsSaFollowupResponse.setIdLastUpdatePerson(loggedInUser);
		cpsSaFollowupResponse.setDtLastUpdate(new Date());
		return cpsSaFollowupResponse;
	}

	@Override
	public long deleteEventPersonLink(Integer tobeDeletedPersonId, int eventId) {
		Criteria criteriaEventPersonLink = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
		criteriaEventPersonLink.add(Restrictions.eq("event.idEvent", Long.valueOf(eventId)));
		criteriaEventPersonLink.add(Restrictions.eq("person.idPerson", Long.valueOf(tobeDeletedPersonId)));
		EventPersonLink eventPersonLink = (EventPersonLink) criteriaEventPersonLink.uniqueResult();
		if(eventPersonLink !=null){
			sessionFactory.getCurrentSession().delete(eventPersonLink);
		}
		return Integer.valueOf(criteriaEventPersonLink.list().size());
	}

	@Override
	public long addEventPersonLink(Integer eventPersonLinkId, int eventId, int caseId) {
		EventPersonLink eventPersonLink = new EventPersonLink();
		Person person = new Person();
		person.setIdPerson((long) eventPersonLinkId);
		eventPersonLink.setPerson(person);
		Event event = new Event();
		event.setIdEvent((long) eventId);
		eventPersonLink.setEvent(event);
		eventPersonLink.setIdCase((long) caseId);
		eventPersonLink.setDtLastUpdate(Calendar.getInstance().getTime());
		return (long) sessionFactory.getCurrentSession().save(eventPersonLink);
	}

	/**
	 * This method is used to make sure a string is not null. If a non-null
	 * value is received, it is returned as is. If a null value value is
	 * received, a blank ("") String is returned.
	 *
	 * @param value
	 *            - the string that is being evaluated
	 * @return String - either valid value (not null) or blank ("")
	 */
	private String getNonNullString(String value) {
		if ((value == null) || (value.equals(ServiceConstants.EMPTY_STRING))) {
			return ServiceConstants.EMPTY_STRING;
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public SDMSafetyAssessmentDto getSDMSafetyAssessment(Long idEvent, Long idStage) {
		Set<Integer> personSet = new HashSet<>();
		new HashMap<String, List<SDMSafetyAssessmentFollowupDto>>();
		SDMSafetyAssessmentDto sdmSafetyAssessmnt = (SDMSafetyAssessmentDto) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(cpsSafetyAssmtDetailsSql).setParameter("idEvent", idEvent))
						.addScalar("id", StandardBasicTypes.INTEGER).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("createdOn", StandardBasicTypes.TIMESTAMP)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("createdBy", StandardBasicTypes.STRING)
						.addScalar("idHouseHoldPerson", StandardBasicTypes.LONG)
						.addScalar("nmHouseHoldPerson", StandardBasicTypes.STRING)
						.addScalar("updatedBy", StandardBasicTypes.STRING)
						.addScalar("formVersionNumber", StandardBasicTypes.INTEGER)
						.addScalar("idFormVersion", StandardBasicTypes.INTEGER)
						.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("eventStatus", StandardBasicTypes.STRING)
						.addScalar("dtEventLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("cdTask")
						.addScalar("caseName", StandardBasicTypes.STRING)
						.addScalar("dtEventOccurred", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtSafetyAssessed", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtAssessmentCompleted", StandardBasicTypes.TIMESTAMP)
						.addScalar("cdSavedSafetyDecision", StandardBasicTypes.STRING)
						.addScalar("unsafeDecisionAction", StandardBasicTypes.STRING)
						.addScalar("assessmentDiscussion", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentDto.class)).uniqueResult();
		if (ObjectUtils.isEmpty(sdmSafetyAssessmnt)) {
			throw new DataLayerException("No SDM SafetyAssessment Data found for event: " + idEvent);
		}
		//
		sdmSafetyAssessmnt.setTimeSafetyAssessed(DateUtils.getTime(sdmSafetyAssessmnt.getDtSafetyAssessed()));
		Criteria criteriaEventPersonLink = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
		criteriaEventPersonLink.add(Restrictions.eq("event.idEvent", idEvent));
		criteriaEventPersonLink.setProjection(Projections.property("person.idPerson"));
		List l = criteriaEventPersonLink.list();
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Long personId = (Long) it.next();
			personSet.add(personId.intValue());
		}
		Criteria criteriaCpssa = sessionFactory.getCurrentSession().createCriteria(CpsSa.class);
		criteriaCpssa.add(Restrictions.eq("event.idEvent", idEvent));
		criteriaCpssa.setProjection(Projections
				.distinct(Projections.projectionList().add(Projections.property("cdAssmtType"), "cdAssmtType")));
		List cpssaList = criteriaCpssa.list();
		Iterator cpsaItr = cpssaList.iterator();
		while (cpsaItr.hasNext()) {
			sdmSafetyAssessmnt.setAssessmentType((String) cpsaItr.next());
		}
		List<SDMSafetyAssessmentResponseDto> responses = new ArrayList<SDMSafetyAssessmentResponseDto>();
		responses = (List<SDMSafetyAssessmentResponseDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(cpsSaResponseSql).setParameter("id", sdmSafetyAssessmnt.getId()))
						.addScalar("idSafetyAssessment", StandardBasicTypes.LONG)
						.addScalar("id", StandardBasicTypes.LONG).addScalar("idQuestionLookup", StandardBasicTypes.LONG)
						.addScalar("cdQuestionResponse", StandardBasicTypes.STRING)
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.addScalar("otherDescriptionText", StandardBasicTypes.STRING)
						.addScalar("cdQuestion", StandardBasicTypes.STRING)
						.addScalar("questionText", StandardBasicTypes.STRING)
						.addScalar("cdSection", StandardBasicTypes.STRING)
						.addScalar("questionOrder", StandardBasicTypes.INTEGER)
						.addScalar("questionDefinition", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentResponseDto.class)).list();
		List<SDMSafetyAssessmentFollowupDto> followupResponseDB = new ArrayList<SDMSafetyAssessmentFollowupDto>();
		followupResponseDB = (List<SDMSafetyAssessmentFollowupDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(cpsSaFollowupResponseSql).setParameter("id", sdmSafetyAssessmnt.getId()))
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.addScalar("id", StandardBasicTypes.INTEGER)
						.addScalar("idFollowupLookup", StandardBasicTypes.INTEGER)
						.addScalar("followupQuestionText", StandardBasicTypes.STRING)
						.addScalar("followupQuestionOrder", StandardBasicTypes.INTEGER)
						.addScalar("followupDefinition", StandardBasicTypes.STRING)
						.addScalar("cdQuestion", StandardBasicTypes.STRING)
						.addScalar("idSafetyResponse", StandardBasicTypes.INTEGER)
						.addScalar("cdFollowupResponse", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentFollowupDto.class)).list();
		followupResponseDB.iterator();
		PersonInfoDto personInfoDto = (PersonInfoDto) sessionFactory.getCurrentSession()
				.createSQLQuery(getHouseHoldNamesql).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idAsmntCpssa", Long.valueOf(sdmSafetyAssessmnt.getId()))
				.setResultTransformer(Transformers.aliasToBean(PersonInfoDto.class)).uniqueResult();
		if (!ObjectUtils.isEmpty(personInfoDto)) {
			sdmSafetyAssessmnt.setIdHouseHoldPerson(personInfoDto.getIdPerson());
			sdmSafetyAssessmnt.setNmHouseHoldPerson(personInfoDto.getNmPersonFull());
			sdmSafetyAssessmnt.setAssmntWithHouseHold(true);
		}
		sdmSafetyAssessmnt.setSavedPersonAssessed(personSet);
		setSafetyResponseBySectionMap(responses, followupResponseDB, sdmSafetyAssessmnt);
		setDangerIndicatorYesAvailable(sdmSafetyAssessmnt);
		return sdmSafetyAssessmnt;
	}

	public void setSafetyResponseBySectionMap(List<SDMSafetyAssessmentResponseDto> safetyResponses,
			List<SDMSafetyAssessmentFollowupDto> followupList, SDMSafetyAssessmentDto sdmSafetyAssessmnt) {
		Set<String> questioncodes = followupList.stream().map(o -> o.getCdQuestion()).collect((Collectors.toSet()));
		Map<String, List<SDMSafetyAssessmentFollowupDto>> followupMap = new HashMap<String, List<SDMSafetyAssessmentFollowupDto>>();
		questioncodes.forEach(questionCode -> {
			followupMap.put(questionCode, followupList.stream().filter(o -> questionCode.equals(o.getCdQuestion()))
					.collect((Collectors.toList())));
		});
		for (Entry e : followupMap.entrySet()) {
			List<SDMSafetyAssessmentResponseDto> respList = safetyResponses.stream()
					.filter(p -> p.getIdQuestionLookup().toString().equals(e.getKey())).collect((Collectors.toList()));
			if (!ObjectUtils.isEmpty(respList) && respList.size() > 0)
				respList.get(0).setFollowupResponseList((List<SDMSafetyAssessmentFollowupDto>) e.getValue());
		}
		Set<String> sectionList = safetyResponses.stream().map(o -> o.getCdSection()).collect((Collectors.toSet()));
		Map<String, List<SDMSafetyAssessmentResponseDto>> sectionMap = new HashMap<String, List<SDMSafetyAssessmentResponseDto>>();
		sectionList.forEach(section -> {
			sectionMap.put(section, safetyResponses.stream().filter(o -> section.equals(o.getCdSection()))
					.collect((Collectors.toList())));
		});
		sdmSafetyAssessmnt.setSafetyResponseBySectionMap(sectionMap);
	}

	public Boolean isSftAsmntInProcStatusAvail(Long idStage, String cdStage) {
		String cdTask = null;

		switch (cdStage) {
		case ServiceConstants.CSTAGES_INV:
			cdTask = ServiceConstants.CD_TASK_SA;
			break;
		case ServiceConstants.CSTAGES_AR:
			cdTask = ServiceConstants.CD_TASK_SA_AR;
			break;
		case ServiceConstants.CSTAGES_FSU:
			cdTask = ServiceConstants.CD_TASK_SA_FSU;
			break;
		case ServiceConstants.CSTAGES_FRE:
			cdTask = ServiceConstants.CD_TASK_SA_FRE;
			break;
		case ServiceConstants.CSTAGES_FPR:
			cdTask = ServiceConstants.CD_TASK_SA_FPR;
			break;
		default:
			break;
		}

		Query query = sessionFactory.getCurrentSession().createSQLQuery(isSftAsmntInProcStatusAvail)
				.setParameter("idStage", idStage).setParameter("cdTask", cdTask);
		List<Object> countObj = query.list();
		BigDecimal cnt = (BigDecimal) countObj.get(0);
		return cnt.intValue() > 0 ? true : false;
	}

	public Boolean isNewSftAsmntInProcStatusAvail(Long idStage, Long idPerson, String cdStage, Long idCpsSA) {
		List<String> cdTasks = Arrays.asList(ServiceConstants.CD_TASK_SA, ServiceConstants.CD_TASK_SA_AR,
				ServiceConstants.CD_TASK_SA_FSU, ServiceConstants.CD_TASK_SA_FRE, ServiceConstants.CD_TASK_SA_FPR);

		if (ObjectUtils.isEmpty(idCpsSA)) {
			idCpsSA = 0l;
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(isNewSftAsmntInProcStatusAvail)
				.setParameter("idStage", idStage).setParameter("idPerson", idPerson).setParameterList("cdTask", cdTasks)
				.setParameter("idCpsSA", idCpsSA);
		List<Object> countObj = query.list();
		BigDecimal cnt = (BigDecimal) countObj.get(0);
		return cnt.intValue() > 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	public SDMSafetyAssessmentDto getQueryPageData(Long idStage, Boolean assmntWithHouseHold) {
		List<SDMSafetyAssessmentDto> sdmSafetyAssessmentList = new ArrayList<SDMSafetyAssessmentDto>();
		sdmSafetyAssessmentList = (List<SDMSafetyAssessmentDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(safetyAsmtFormLookUpSql).setParameter("idStage", idStage))
						.addScalar("idCpsSaAssmtLookup", StandardBasicTypes.LONG)
						.addScalar("formVersionNumber", StandardBasicTypes.INTEGER)
						.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentDto.class)).list();
		if (sdmSafetyAssessmentList.isEmpty()) {
			throw new DataLayerException("No SDM SafetyAssessment Form Data found in " + " queryPageData");
		}
		SDMSafetyAssessmentDto sdmSafetyAssessmentDto = sdmSafetyAssessmentList.get(0);
		long assmtLookupId = sdmSafetyAssessmentDto.getIdCpsSaAssmtLookup();
		List<SDMSafetyAssessmentFollowupDto> followupResponseDB = new ArrayList<SDMSafetyAssessmentFollowupDto>();
		followupResponseDB = (List<SDMSafetyAssessmentFollowupDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(safetyAsmtFollowLookUpSql).setParameter("assmtLookupId", assmtLookupId))
						.addScalar("idFollowupLookup", StandardBasicTypes.INTEGER)
						.addScalar("cdQuestion", StandardBasicTypes.STRING)
						.addScalar("followupQuestionText", StandardBasicTypes.STRING)
						.addScalar("followupQuestionOrder", StandardBasicTypes.INTEGER)
						.addScalar("followupDefinition", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentFollowupDto.class)).list();
		List<SDMSafetyAssessmentResponseDto> sdmsafetyAsmntRespList = new ArrayList<SDMSafetyAssessmentResponseDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpsSaQstnLookup.class)
				.add(Restrictions.eq("idCpsSaAssmtLookup", assmtLookupId)).addOrder(Order.asc("cdSection"))
				.addOrder(Order.asc("nbrOrder"));
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("cdSaQuestion"), "cdQuestion")
				.add(Projections.property("txtQuestion"), "questionText")
				.add(Projections.property("cdSection"), "cdSection")
				.add(Projections.property("nbrOrder"), "questionOrder")
				.add(Projections.property("idCpsSaQstnLookup"), "idQuestionLookup")
				.add(Projections.property("nmDefinition"), "questionDefinition");
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentResponseDto.class));
		sdmsafetyAsmntRespList = (List<SDMSafetyAssessmentResponseDto>) criteria.list();
		setSafetyResponseBySectionMap(sdmsafetyAsmntRespList, followupResponseDB, sdmSafetyAssessmentDto);
		setDangerIndicatorYesAvailable(sdmSafetyAssessmentDto);
		if (!assmntWithHouseHold) {
			sdmSafetyAssessmentDto.setAssessmentType(getAssessmentType(idStage));
			if (ServiceConstants.CSDMASMT_INIT.equals(sdmSafetyAssessmentDto.getAssessmentType())) {
				sdmSafetyAssessmentDto.setReAssmtTypeDisabled(true);
				sdmSafetyAssessmentDto.setCaseClosureAssmtTypeDisabled(true);
			} else if (ServiceConstants.CSDMASMT_REAS.equals(sdmSafetyAssessmentDto.getAssessmentType()))
				sdmSafetyAssessmentDto.setInitialAssmtTypeDisabled(true);
		}
		return sdmSafetyAssessmentDto;
	}

	public void setDangerIndicatorYesAvailable(SDMSafetyAssessmentDto sdmSafetyAssessmentDto) {
		sdmSafetyAssessmentDto.setDangerIndicatorYesAvailable(false);
		List<SDMSafetyAssessmentResponseDto> section2List = sdmSafetyAssessmentDto.getSafetyResponseBySectionMap()
				.get("SA2");
		for (SDMSafetyAssessmentResponseDto responseDB : section2List) {
			// Check if any response has YES selected
			if ("Y".equals(responseDB.getCdQuestionResponse())) {
				sdmSafetyAssessmentDto.setDangerIndicatorYesAvailable(true);
				break;
			}
		}
	}

	private String getAssessmentType(Long stageId) {
		// Default to Initial Assessment
		String strAssessmentType = ServiceConstants.CSDMASMT_INIT;
		if (getRecordCountUsingStageID(SAFETY_ASSESSMENT_COUNT_SQL, stageId) > 0
				|| getRecordCountUsingStageID(GET_APPRVD_AR_STAGE_SAFETY_COUNT_SQL, stageId) > 0
				|| getRecordCountUsingStageID(GET_APPRVD_7_DAY_SAFETY_COUNT_SQL, stageId) > 0) {
			// Set the Assessment Type to Re-Assessment
			strAssessmentType = ServiceConstants.CSDMASMT_REAS;
		}
		return strAssessmentType;
	}

	private int getRecordCountUsingStageID(String query, Long stageId) {
		return (int) sessionFactory.getCurrentSession().createSQLQuery(query)
				.addScalar("COUNT", StandardBasicTypes.INTEGER).setParameter("idStage", stageId).uniqueResult();
	}

	@Override
	public String getAsmtTypHoHold(CommonHelperReq commonHelperReq, Long priorStageID) {
		long idCpsSa = !ObjectUtils.isEmpty(commonHelperReq.getIdToDo())?commonHelperReq.getIdToDo():0l;
		int count = 0;
		// Defect 11503 - INV will check only for current and AR stage
		if (AR_AND_INV_STG_TYPE_LIST.contains(commonHelperReq.getCdStage())){
			 count = (int) sessionFactory.getCurrentSession().createSQLQuery(HOUSEHOLD_SAFETY_ASSESSMENT_COUNT_SQL)
					.addScalar("COUNT", StandardBasicTypes.INTEGER).setParameter("idStage", commonHelperReq.getIdStage())
					.setParameter("idCpsSA", idCpsSa).setParameter("idPerson", commonHelperReq.getIdPerson())
					.setParameter("idPriorStage", priorStageID).uniqueResult();
		}else{ // CVS stages will go across the case
			count = (int) sessionFactory.getCurrentSession().createSQLQuery(HOUSEHOLD_SAFETY_ASSESSMENT_COUNT_SQL2)
					.addScalar("COUNT", StandardBasicTypes.INTEGER).setParameter("idCase", commonHelperReq.getIdCase())
					.setParameter("idCpsSA", idCpsSa).setParameter("idPerson", commonHelperReq.getIdPerson())
					.uniqueResult();
		}
		// Default to Initial Assessment
		String strAssessmentType = ServiceConstants.CSDMASMT_INIT;
		if (count > 0) {
			// Set the Assessment Type to Re-Assessment
			strAssessmentType = ServiceConstants.CSDMASMT_REAS;
		}
		return strAssessmentType;
	}
	
	
	@Override
	public String checkAsmtTypHoHold(CommonHelperReq commonHelperReq, Long priorStageID) {
		
		String strAssessmentType = ServiceConstants.CSDMASMT_INIT;
		if(!ObjectUtils.isEmpty(commonHelperReq.getIdToDo())){
			Long idCpsSaGiven = (Long)sessionFactory.getCurrentSession().createSQLQuery(householdCheckSafetyAssessmentSql)
					.addScalar("idCps", StandardBasicTypes.LONG)
					.setParameter("idCpsSA", commonHelperReq.getIdToDo()).setParameter("idPerson", commonHelperReq.getIdPerson()).uniqueResult();
			if(ObjectUtils.isEmpty(idCpsSaGiven)){
				strAssessmentType = getAsmtTypHoHold( commonHelperReq, priorStageID);
			}
		}
		return strAssessmentType;
	}

	@Override
	public SDMSafetyAssessmentDto completeAssessment(SDMSafetyAssessmentDto safetyAssessmentDB,
			UserProfileDto userProfileDB) {
		updateEvent(safetyAssessmentDB, ServiceConstants.CEVTSTAT_COMP);
		CpsSa cpsSa = (CpsSa) sessionFactory.getCurrentSession().createCriteria(CpsSa.class)
				.add(Restrictions.eq("event.idEvent", safetyAssessmentDB.getIdEvent()))
				.add(Restrictions.eq("idCpsSa", Long.valueOf(safetyAssessmentDB.getId()))).uniqueResult();
		cpsSa.setIdCpsSa(safetyAssessmentDB.getId());
		safetyAssessmentDB.setDtAssessmentCompleted(new Date());
		cpsSa.setDtAssmtCompleted(safetyAssessmentDB.getDtAssessmentCompleted());
		cpsSa.setCdAssmtType(safetyAssessmentDB.getAssessmentType());
		cpsSa.setIdLastUpdatePerson(safetyAssessmentDB.getLoggedInUser());
		cpsSa.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(cpsSa);
		return safetyAssessmentDB;
	}

	@Override
	public void undoCompleteAssessment(SDMSafetyAssessmentDto safetyAssessmentDB) {
		updateEvent(safetyAssessmentDB, ServiceConstants.CEVTSTAT_PROC);
		CpsSa cpsSa = (CpsSa) sessionFactory.getCurrentSession().createCriteria(CpsSa.class)
				.add(Restrictions.eq("event.idEvent", safetyAssessmentDB.getIdEvent()))
				.add(Restrictions.eq("idCpsSa", Long.valueOf(safetyAssessmentDB.getId()))).uniqueResult();
		cpsSa.setIdCpsSa(safetyAssessmentDB.getId());
		cpsSa.setDtAssmtCompleted(null);
		cpsSa.setCdAssmtType(safetyAssessmentDB.getAssessmentType());
		cpsSa.setIdLastUpdatePerson(safetyAssessmentDB.getLoggedInUser());
		cpsSa.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(cpsSa);
	}

	private Long updateEvent(SDMSafetyAssessmentDto safetyAssessmentDB, String eventStatus) {
		Event updateEvent = (Event) sessionFactory.getCurrentSession().get(Event.class,
				safetyAssessmentDB.getIdEvent());
		updateEvent.setDtLastUpdate(new Date());
		Person person = new Person();
		person.setIdPerson(safetyAssessmentDB.getLoggedInUser());
		updateEvent.setPerson(person);
		updateEvent.setCdEventStatus(eventStatus);
		sessionFactory.getCurrentSession().update(sessionFactory.getCurrentSession().merge(updateEvent));
		return updateEvent.getIdEvent();
	}

	/**
	 * 
	 * Method Name: getSafetyAssmentList Method Description: Get Safety
	 * Assessment Details
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public SDMSafetyAssessmentDto getSafetyAssmentList(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyAssmentList)
				.addScalar("id", StandardBasicTypes.INTEGER).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("createdBy", StandardBasicTypes.STRING).addScalar("updatedBy", StandardBasicTypes.STRING)
				.addScalar("formVersionNumber", StandardBasicTypes.INTEGER)
				.addScalar("idCpsSaAssmtLookup", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("eventStatus", StandardBasicTypes.STRING)
				.addScalar("dtEventLastUpdate", StandardBasicTypes.DATE).addScalar("cdTask", StandardBasicTypes.STRING)
				.addScalar("caseName", StandardBasicTypes.STRING).addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("dtSafetyAssessed", StandardBasicTypes.DATE)
				.addScalar("dtAssessmentCompleted", StandardBasicTypes.DATE)
				.addScalar("cdCurrSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("unsafeDecisionAction", StandardBasicTypes.STRING)
				.addScalar("dtStageClosure",StandardBasicTypes.DATE)
				.addScalar("txtAssmtDiscussion", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentDto.class));
		return (SDMSafetyAssessmentDto) query.uniqueResult();
	}

	/**
	 * 
	 * Method Name: geteventPersonLink Method Description: Get the event person
	 * link for DB
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getEventPersonLink(Long idEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class)
				.add(Restrictions.eq("event.idEvent", idEvent))
				.setProjection(Projections.projectionList().add(Projections.property("person.idPerson"), "idPerson"));
		List<Long> personList = cr.list();
		return personList;
	}

	/**
	 * 
	 * Method Name: getSafetyAssmentResp Method Description: This method is used
	 * to retrieve the Safety Assessment Response by passing idCpsSa as input
	 * request.
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public String getSafetyAssmentType(Long idEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CpsSa.class)
				.add(Restrictions.eq("event.idEvent", idEvent)).setProjection(Projections.distinct(
						Projections.projectionList().add(Projections.property("cdAssmtType"), "cdAssmtType")));
		return (String) cr.uniqueResult();
	}

	/**
	 * 
	 * Method Name: getSDMFollowUpQuestions Method Description: This method is
	 * used to retrieve the SDM FollowUp Questions by passing idCpsSa as input
	 * request.
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SDMSafetyAssessmentResponseDto> getSafetyAssmentResp(int idCpsSa) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSafetyAssmentResp)
				.addScalar("id", StandardBasicTypes.LONG).addScalar("idSafetyAssessment", StandardBasicTypes.LONG)
				.addScalar("idQuestionLookup", StandardBasicTypes.LONG)
				.addScalar("cdQuestion", StandardBasicTypes.STRING)
				.addScalar("cdQuestionResponse", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("otherDescriptionText", StandardBasicTypes.STRING)
				.addScalar("questionText", StandardBasicTypes.STRING).addScalar("cdSection", StandardBasicTypes.STRING)
				.addScalar("questionOrder", StandardBasicTypes.INTEGER)
				.addScalar("questionDefinition", StandardBasicTypes.STRING).setParameter("idCpsSa", idCpsSa)
				.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentResponseDto.class));
		return query.list();
	}

	/**
	 * 
	 * Method Name: getSDMFollowUpQuestions Method Description: This method is
	 * used to retrieve the SDM FollowUp Questions by passing idCpsSa as input
	 * request.
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SDMSafetyAssessmentFollowupDto> getSDMFollowUpQuestions(int idCpsSa) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSDMFollowUpQuestions)
				.addScalar("id", StandardBasicTypes.INTEGER).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFollowupLookup", StandardBasicTypes.INTEGER)
				.addScalar("followupQuestionText", StandardBasicTypes.STRING)
				.addScalar("followupQuestionOrder", StandardBasicTypes.INTEGER)
				.addScalar("followupDefinition", StandardBasicTypes.STRING)
				.addScalar("cdQuestion", StandardBasicTypes.STRING)
				.addScalar("idSafetyResponse", StandardBasicTypes.INTEGER)
				.addScalar("cdFollowupResponse", StandardBasicTypes.STRING).setParameter("idCpsSa", idCpsSa)
				.setResultTransformer(Transformers.aliasToBean(SDMSafetyAssessmentFollowupDto.class));
		return query.list();
	}

	/**
	 * Method Name: getPersonAssessedInSDMSafety Method Description: The method
	 * fetches the list of Persons assessed in All Aproved SDM Safety Assessment
	 * for the Passed INV Stage.
	 * 
	 * @param idStage
	 * @return idPersonAssessedList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getPersonAssessedInSDMSafety(Long idStage) {
		List<Long> idPersonAssessedList = new ArrayList<>();
		idPersonAssessedList = sessionFactory.getCurrentSession().createSQLQuery(getPersonAssessedInSDMSafety)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idStage", idStage).list();
		return idPersonAssessedList;
	}
	
	/**
	 * 
	 * Method Name: getLatestSafetyAssessmentEvent Method Description:Gets
	 * latest safety assessment event id
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getLatestSafetyAssessmentEvent(Long idStage) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getLatestSafetyAssessmentEvent));
		query.setParameter("idStage", idStage);
		BigDecimal idEvent = (BigDecimal) query.setMaxResults(1).uniqueResult();
		return idEvent.longValue();
	}
	
	
	/**
	 * Method Name: getLatestAssessmentDateInStage Method Description: The
	 * method fetches the latest date of assessment for all available
	 * assessments in the stage.
	 * 
	 * @param idStage
	 * @return dtlatestAsmnt
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getLatestAssessmentDateInStage(Long idStage, Long idEvent) {
		Date dtlatestAsmnt = null;
		List<Date> asmntLst = (List<Date>) sessionFactory.getCurrentSession().createQuery(latestSafetyDtForStageHql)
				.setParameter("idStage", idStage).setParameter("idEvent", idEvent).list();
		if (!ObjectUtils.isEmpty(asmntLst)) {
			dtlatestAsmnt = asmntLst.get(0);
		}
		return dtlatestAsmnt;
	}
	
	/**
	 * Method Name: getLatestAssessmentDateInStage Method Description: The
	 * method fetches the person list for the latest assessment for the given household
	 * @param idStage
	 * @return dtlatestAsmnt
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getLatestAssessmentPersonAssessedList(Long idCase, Long idPerson,Long idEvent) {		
		idEvent = !ObjectUtils.isEmpty(idEvent)?idEvent:0l;
		SQLQuery sqlQuery =  (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchPersonAssessedForHouseHold)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idCase", idCase)
				.setParameter("idEvent", idEvent).setParameter("idHouseHoldPerson", idPerson);
		List<Long> idPersonList = (List<Long>)sqlQuery.list();
		return idPersonList;
	}
	
	/**
	 * 
	 *Method Name:	getAsmtTypHouseHoldForAssessedPerson
	 *Method Description:This method is used to check if some household is selected as a person assessed.
	 *@param idStage
	 *@param idCpsSa
	 *@param idPersonList
	 *@return
	 */
	@Override
	public String getAsmtTypHouseHoldForAssessedPerson(Long idStage, Long idCpsSa,List<Long> idPersonList) {
		idCpsSa = !ObjectUtils.isEmpty(idCpsSa)?idCpsSa:0l;
		int count = (int) sessionFactory.getCurrentSession().createSQLQuery(housholdSameAsPersonAssessed)
				.addScalar("COUNT", StandardBasicTypes.INTEGER).setParameter("idStage", idStage)
				.setParameter("idCpsSA", idCpsSa).setParameterList("idPerson", idPersonList)
				.uniqueResult();
		// Default to Initial Assessment
		String strAssessmentExist = ServiceConstants.NO_TEXT;
		if (count > 0) {
			// Set the Assessment Type to Re-Assessment
			strAssessmentExist = ServiceConstants.YES_TEXT;
		}
		return strAssessmentExist;
	}
	
	/**
	 * Method Name: getChildrenCareGiverAssessedInSDMSafety Method Description: The method
	 * fetches the list of Persons assessed in SDM Safety Assessment
	 * for the Passed INV Stage.
	 * 
	 * @param idStage
	 * @return idPersonAssessedList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getChildrenCareGiverAssessedInSDMSafety(Long idStage) {
		List<Long> idPersonAssessedList = new ArrayList<>();
		idPersonAssessedList = sessionFactory.getCurrentSession().createSQLQuery(getChildrenCareGiverAssessedInSDMSafety)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idStage", idStage).list();
		return idPersonAssessedList;
	}
	
}
