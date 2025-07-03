package us.tx.state.dfps.service.common.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.common.domain.Situation;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.Workload;
import us.tx.state.dfps.common.dto.CaseCheckDto;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.xmlstructs.inputstructs.EventIdArrayDto;
import us.tx.state.dfps.xmlstructs.inputstructs.EventIdStructDto;

/**
 * service-common- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Sep 20, 2017- 11:29:30 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CaseUtils {
	@Value("${SSCCRefDaoImpl.selectStage}")
	private String selectStageSql;

	@Value("${CaseUtils.getIncomingCallDate}")
	private String getIncomingCallDate;

	@Value("${CaseUtils.getStagesByType}")
	private String getStagesByType;

	@Value("${CaseUtils.hasStageAccess}")
	private String hasStageAccess;

	@Value("${CaseUtils.getStageDetails}")
	private String getStageDetails;

	@Value("${CaseUtils.getCaseCheckoutStatus}")
	private String getCaseCheckoutStatusSql;

	@Value("${CaseUtils.getPriorStage}")
	private String getPriorStageSql;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	MobileUtil mobileUtil;

	public List getEvents(EventIdArrayDto inputEventIdStruct_array) {

		int eventCount1 = getEventIdStructCount(inputEventIdStruct_array.getEventIdStructList());

		Integer[] eventCount = new Integer[eventCount1];

		if (eventCount.length == 0) {

			throw new IllegalArgumentException("At least 1 event is required.");
		}

		String[] eventsListObjects = new String[eventCount.length];

		for (int index = 0; index < eventCount.length; index++) {
			int eventId = getEventIdStruct(index, inputEventIdStruct_array.getEventIdStructList()).getIdEvent()
					.intValue();
			eventsListObjects[index] = Integer.valueOf(eventId).toString();
		}

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class)
				.setProjection(Projections.projectionList().add(Projections.property("idEvent"))
						.add(Projections.property("cdEventStatus")).add(Projections.property("cdTask"))
						.add(Projections.property("dtLastUpdate")))
				.add(Restrictions.in("idEvent", eventsListObjects))
				.setResultTransformer(Transformers.aliasToBean(EventDto.class));

		List<EventDto> EventDtoList = criteria.list();

		return EventDtoList;
	}

	/**
	 * Method Name: getEventIdStruct Method Description:
	 * 
	 * @param index
	 * @param arrayList
	 * @return
	 */
	private EventIdStructDto getEventIdStruct(int index, ArrayList arrayList) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > arrayList.size())) {
			throw new IndexOutOfBoundsException();
		}
		return (EventIdStructDto) arrayList.get(index);

	}

	/**
	 * Method Name: getEventIdStructCount Method Description:
	 * 
	 * @param arrayList
	 * @return
	 */
	private int getEventIdStructCount(ArrayList arrayList) {
		return arrayList.size();
	}

	/**
	 * Returns the ulIdPerson of the Primary Worker of the stage. i;e the Person
	 * with role as PRIMARY ("PR").
	 * 
	 * @param ulIdStage
	 * @return
	 */

	public long getPrimaryWorkerIdForStage(Long ulIdStage) {

		long ulIdPersonPrimaryWorker = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Workload.class);
		criteria.add(Restrictions.eq("id.idWkldStage", ulIdStage));
		criteria.add(Restrictions.eq("id.cdWkldStagePersRole", "PR"));

		criteria.setProjection(Projections.property("id.idWkldPerson"));
		if (!ObjectUtils.isEmpty(criteria.uniqueResult())){
			ulIdPersonPrimaryWorker = (long) criteria.uniqueResult();
		}
		return ulIdPersonPrimaryWorker;
	}

	public Stage getStage(long idStage) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		criteria.add(Restrictions.eq("idStage", idStage));

		Stage stage = (Stage) criteria.uniqueResult();

		return stage;

	}

	public boolean isCPSSDMInvSafetyAssmt(Date DtStageStart) throws ParseException {
		boolean isSDMSafetyAssmt = false;
		Date march15RelDt = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMDDYYYY).parse("03/29/2015");
		if (null != DtStageStart && DateUtils.isAfter(DtStageStart, march15RelDt)) {
			isSDMSafetyAssmt = true;
		}
		return isSDMSafetyAssmt;
	}

	public Date getIncomingCallDateForIntake(long stageId) {
		Date incomingCallDate = null;
		IncomingDetail incomingDetail = (IncomingDetail) sessionFactory.getCurrentSession().get(IncomingDetail.class,
				stageId);
		if (incomingDetail != null) {
			incomingCallDate = incomingDetail.getDtIncomingCall();
		}
		return incomingCallDate;
	}

	/**
	 * Method Name: isCVSStage Method Description: This method returns an
	 * boolean value based on whether or not the is stage CVS user stage (FRE ||
	 * FSU || SUB)
	 * 
	 * @param szCdStage
	 * @return isCVSStage
	 */
	public boolean isCVSStage(String szCdStage) {

		boolean isCVSStage = false;
		if (ServiceConstants.CSTAGES_FSU.equals(szCdStage) || ServiceConstants.CSTAGES_FRE.equals(szCdStage)
				|| ServiceConstants.CSTAGES_SUB.equals(szCdStage) || ServiceConstants.CSTAGES_FPR.equals(szCdStage)) {
			isCVSStage = true;
		}

		return isCVSStage;
	}

	/**
	 * Method Name: getOpenSUBStages Method Description: This method returns all
	 * open SUB stages in the case
	 * 
	 * @param ulIdCase
	 * @return List<StageDto>
	 */
	public List<StageDto> getOpenSUBStages(Long ulIdCase) {
		return getStagesByType(String.valueOf(ulIdCase), ServiceConstants.OPEN_STAGES, ServiceConstants.CSTAGES_SUB);
	}

	/**
	 * Method Name: getStagesByType Method Description:This method will fetch
	 * all stages based on specific stage and indActive (open or closed)
	 * 
	 * @param idCase
	 * @param openStages
	 * @param cdStage
	 * @return List<StageDto>
	 */
	private List<StageDto> getStagesByType(String idCase, String openStages, String cdStage) {
		List<StageDto> results = new ArrayList<StageDto>();

		StringBuilder buf = new StringBuilder();

		buf.append(getStagesByType).append(ServiceConstants.CASE_UTIL_STAGE_);
		buf.append(idCase);

		switch (openStages) {
		case ServiceConstants.CLOSED_STAGES:
			buf.append(ServiceConstants.CASE_UTIL_CLOSED_STAGES);
			break;
		case ServiceConstants.OPEN_STAGES:
			buf.append(ServiceConstants.CASE_UTIL_OPEN_STAGES);
			break;
		case ServiceConstants.ALL_STAGES:
			// add no and clause
			break;
		default:
			throw new IllegalArgumentException("Illegal Stage Type.");
		}

		buf.append(ServiceConstants.CASE_UTIL_CD_STAGE);
		buf.append(cdStage);
		buf.append(ServiceConstants.CASE_UTIL_ORDER);

		Query query = sessionFactory.getCurrentSession().createSQLQuery(buf.toString())
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idSituation", StandardBasicTypes.LONG).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("cdStageType", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.DATE)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("idUnit", StandardBasicTypes.LONG).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(StageDto.class));

		results = query.list();

		return results;
	}

	public boolean hasStageAccess(Long ulIdStage, UserProfileDto user) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasStageAccess);
		query.setParameter("idStage", ulIdStage);
		query.setParameter("idPerson", user.getIdUser());

		List<Long> results = query.list();
		if (results.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Method Name: getOpenFSUStages Method Description: This method returns all
	 * open FSU stages in the case
	 * 
	 * @param ulIdCase
	 * @return List<StageDto>
	 */
	public List<StageDto> getOpenFSUStages(Long ulIdCase) {
		return getStagesByType(String.valueOf(ulIdCase), ServiceConstants.OPEN_STAGES, ServiceConstants.CSTAGES_FSU);
	}

	/**
	 * Method Name: getOpenFREStages Method Description:This method returns all
	 * open FSU stages in the case
	 * 
	 * @param ulIdCase
	 * @return List<StageDto>
	 */
	public List<StageDto> getOpenFREStages(Long ulIdCase) {
		return getStagesByType(String.valueOf(ulIdCase), ServiceConstants.OPEN_STAGES, ServiceConstants.CSTAGES_FRE);
	}

	/**
	 * Method Name: getAllSUBStages Method Description:This method returns all
	 * SUB stages in the case
	 * 
	 * @param ulIdCase
	 * @return List<StageDto>
	 */
	public List<StageDto> getAllSUBStages(Long ulIdCase) {
		return getStagesByType(String.valueOf(ulIdCase), ServiceConstants.ALL_STAGES, ServiceConstants.CSTAGES_SUB);
	}

	/**
	 * Method Name: getCaseCheckoutStatus Method Description: Select Query to
	 * get Stage Checked Out Person.
	 * 
	 * @param spStageId
	 * @return boolean
	 */
	public boolean getCaseCheckoutStatus(Long spStageId) {
		Boolean status = Boolean.FALSE;
		Long idCheckOutPerson;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCaseCheckoutStatusSql)
				.addScalar("idWkldPerson", StandardBasicTypes.LONG)
				.addScalar("cdMobileStatus", StandardBasicTypes.STRING)
				.addScalar("cdWkldStagePersRole", StandardBasicTypes.STRING).setParameter("idWkldStage", spStageId)
				.setResultTransformer(Transformers.aliasToBean(CaseCheckDto.class));

		idCheckOutPerson = (Long) query.uniqueResult();
		status = (ObjectUtils.isEmpty(idCheckOutPerson) || idCheckOutPerson == 0) ? Boolean.FALSE : Boolean.TRUE;
		return status;
	}

	/**
	 * Method Name: getEvent Method Description:Returns details for the
	 * requested event id.
	 * 
	 * @param eventId
	 * @return Event
	 */
	public Event getEvent(Long eventId) throws DataNotFoundException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("idEvent", eventId));
		criteria.addOrder(Order.desc("idEvent"));
		Event event = (Event) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(messageSource.getMessage("event.not.found.attributes", null, Locale.US));
		}
		return event;
	}

	/**
	 * Method Name: getEvent Method Description:Returns the most recent event id
	 * and event status for the given stage and task code
	 * 
	 * @param ulIdStage
	 * @param szCdTask
	 * @return Event
	 */
	public Event getEvent(Long ulIdStage, String szCdTask) {
		return getEvent(Long.toString(ulIdStage), szCdTask);
	}

	/**
	 * Method Name: getEvent Method Description:Returns the most recent event id
	 * and event status for the given stage and task code
	 * 
	 * @param ulIdStage
	 * @param szCdTask
	 * @return Event
	 */
	public Event getEvent(String ulIdStage, String szCdTask) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("stage.idStage", Long.valueOf(ulIdStage)));
		criteria.add(Restrictions.eq("cdTask", szCdTask));
		criteria.addOrder(Order.desc("idEvent"));
		List<Event> event = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(messageSource.getMessage("event.not.found.attributes", null, Locale.US));
		}
		return event.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: getEventByStageAndEventType Method Description: Returns the
	 * most recent event id, event status, task code and timestamp for the given
	 * stage and event type
	 * 
	 * @param ulIdStage
	 * @param szCdEventType
	 * @return Event
	 */
	public Event getEventByStageAndEventType(Long ulIdStage, String szCdEventType) {
		return getEventByStageAndEventType(Long.toString(ulIdStage), szCdEventType);
	}

	/**
	 * Method Name: getEventByStageAndEventType Method Description: Returns the
	 * most recent event id, event status, task code and timestamp for the given
	 * stage and event type
	 * 
	 * @param ulIdStage
	 * @param szCdEventType
	 * @return Event
	 */
	private Event getEventByStageAndEventType(String ulIdStage, String szCdEventType) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("stage.idStage", Long.valueOf(ulIdStage)));
		criteria.add(Restrictions.eq("cdEventType", szCdEventType));
		criteria.addOrder(Order.desc("idEvent"));

		List<Event> eventList = (List<Event>) criteria.list();

		return ObjectUtils.isEmpty(eventList) ? new Event() : eventList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: getStageDetails Method Description: Returns information
	 * about a stage.
	 * 
	 * @param idStage
	 * @return Stage
	 */
	public Stage getStageDetails(Long idStage) throws DataNotFoundException {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageDetails)
				.setResultTransformer(Transformers.aliasToBean(StageDto.class));

		sqlQuery.addScalar("idStage", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdStageType", StandardBasicTypes.STRING);
		sqlQuery.addScalar("idUnit", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idSituation", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtStageClose", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageClassification", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING);
		sqlQuery.addScalar("indStageClose", StandardBasicTypes.STRING);
		sqlQuery.addScalar("nmStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtStageStart", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cdStageProgram", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdStage", StandardBasicTypes.STRING);
		sqlQuery.addScalar("nmCase", StandardBasicTypes.STRING);

		sqlQuery.setParameter("idStage", idStage);

		List<StageDto> liStage = sqlQuery.list();

		if (TypeConvUtil.isNullOrEmpty(liStage)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
		}

		StageDto stageDto = liStage.get(ServiceConstants.Zero);

		Stage stage = new Stage();
		stage.setIdStage(stageDto.getIdStage());
		stage.setCdStageType(stageDto.getCdStageType());
		stage.setIdUnit(stageDto.getIdUnit());

		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, stageDto.getIdCase());
		stage.setCapsCase(capsCase);

		if(!mobileUtil.isMPSEnvironment()) {
			Situation situation = (Situation) sessionFactory.getCurrentSession().get(Situation.class,
					stageDto.getIdSituation());
			stage.setSituation(situation);
		}

		stage.setDtStageClose(stageDto.getDtStageClose());
		stage.setCdStageClassification(stageDto.getCdStageClassification());
		stage.setCdStageReasonClosed(stageDto.getCdStageReasonClosed());

		stage.setIndStageClose(stageDto.getIndStageClose());
		stage.setNmStage(stageDto.getNmStage());
		stage.setDtStageStart(stageDto.getDtStageStart());
		stage.setCdStageProgram(stageDto.getCdStageProgram());
		stage.setCdStage(stageDto.getCdStage());

		capsCase.setNmCase(stageDto.getNmCase());
		stage.setCapsCase(capsCase);

		return stage;

	}

	/**
	 * Method Name: getPrimaryClientIdForStage Method Description:Returns the
	 * ulIdPerson of the Primary Worker of the stage. i;e the Person with role
	 * as PRIMARY ("PR").
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getPrimaryClientIdForStage(Long idStage) {
		Long ulIdPersonPrimaryWorker = ServiceConstants.ZERO_VAL;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("cdStagePersRole", "PC"));

		criteria.setProjection(Projections.property("idPerson"));

		ulIdPersonPrimaryWorker = (long) criteria.uniqueResult();
		return ulIdPersonPrimaryWorker;
	}

	/**
	 * Method Name: getNmCase Method Description: Returns the case name from its
	 * id.
	 * 
	 * @param ulIdCase
	 * @return String.
	 */
	public String getNmCase(Long ulIdCase) {
		String nmCase = new String();

		if (!TypeConvUtil.isNullOrEmpty(ulIdCase)) {

			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, ulIdCase);

			if (!TypeConvUtil.isNullOrEmpty(capsCase)) {
				nmCase = capsCase.getNmCase();
			} else {
				throw new DataNotFoundException(
						messageSource.getMessage("CaseUtils.getNmCase.data.not.found", null, Locale.US));
			}

		}

		return nmCase;
	}

	public Long fetchPriorStage(Long idStage) throws DataNotFoundException {

		BigDecimal idPriorStage;

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPriorStageSql);

		sqlQuery.setParameter("idStage", idStage);

		idPriorStage = (BigDecimal) sqlQuery.uniqueResult();

		return idPriorStage.longValue();

	}
}
