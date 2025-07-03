package us.tx.state.dfps.service.childplan.daoimpl;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
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

import pkware.DCL.InvalidDictionarySizeException;
import pkware.DCL.InvalidModeException;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEventDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegacyDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDtlDto;
import us.tx.state.dfps.service.childplan.dto.CurrentlySelectedPlanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.common.util.DateLikeExpression;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.person.daoimpl.Base64;
import us.tx.state.dfps.service.person.daoimpl.CompressionHelper;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.subcare.dto.ChildPlanGuideTopicDto;
import us.tx.state.dfps.service.subcare.dto.StaffSearchResultDto;
import us.tx.state.dfps.service.workload.dto.EventPlanLinkDto;

@Repository
public class ChildPlanBeanDaoImpl implements ChildPlanBeanDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ChildPlanDaoImpl.selectPersonsInStage}")
	private String selectPersonsInStage;

	@Value("${ChildPlanDaoImpl.sqlExistingPattern}")
	public String sqlExistingPattern;

	@Value("${ChildPlanDaoImpl.sqlNewPattern}")
	public String sqlNewPattern;

	@Value("${ChildPlanDaoImpl.selectPlanTypeCode}")
	public String selectPlanTypeCode;

	@Value("${ChildPlanDaoImpl.selectPlanTypeCodeByEvent}")
	public String selectPlanTypeCodeByEvent;


	@Value("${ChildPlanDaoImpl.checkForUnapprovedChildPlanCreatedInIMPACT}")
	public String checkForUnapprovedChildPlanCreatedInIMPACT;

	@Value("${ChildPlanDaoImpl.checkIfEventIsLegacy}")
	public String checkIfEventIsLegacy;

	@Value("${ChildPlanDaoImpl.checkCrimHist}")
	public String checkCrimHist;

	@Value("${ChildPlanDaoImpl.sqlSelectPriorAdoptionInfo}")
	public String sqlSelectPriorAdoptionInfo;

	@Value("${ChildPlanDaoImpl.sqlDtLastUpdatePrimaryChild}")
	public String sqlDtLastUpdatePrimaryChild;

	@Value("${ChildPlanDaoImpl.sqlSelectLegalStatus}")
	public String sqlSelectLegalStatus;

	@Value("${ChildPlanDaoImpl.sqlSelectPrimaryChildBirthDate}")
	public String sqlSelectPrimaryChildBirthDate;

	@Value("${ChildPlanDaoImpl.selectChildPlanSqlByEvent}")
	public String selectChildPlanSqlByEvent;

	@Value("${ChildPlanDaoImpl.sqlSelectStaff}")
	public String sqlSelectStaff;

	@Value("${ChildPlanDaoImpl.sqlStaffTop}")
	public String sqlStaffTop;

	@Value("${ChildPlanDaoImpl.saveChildPriorAdoption}")
	public String saveChildPriorAdoption;

	@Value("${ChildPlanDaoImpl.selectChildPlanSqlByPid}")
	public String selectChildPlanSqlByPid;

	@Value("${ChildPlanDaoImpl.sqlSelectTopicDtLastUpdate}")
	public String sqlSelectTopicDtLastUpdate;

	@Value("${ChildPlanDaoImpl.sqlUpdatePattern}")
	public String sqlUpdatePattern;

	@Value("${ChildPlanDaoImpl.sqlMpsUpdatePattern}")
	public String sqlMpsUpdatePattern;

	@Value("${ChildPlanDaoImpl.sqlInsertPattern}")
	public String sqlInsertPattern;

	@Value("${ChildPlanDaoImpl.getChildPlanEventsSql}")
	public String getChildPlanEventsSql;

	@Value("${ChildPlanDaoImpl.deleteChildPlan}")
	public String deleteChildPlan;

	private static final Logger log = Logger.getLogger(ChildPlanBeanDaoImpl.class);

	@Override
	public ChildPlanGuideTopicDto selectGuideTopic(ChildPlanGuideTopicDto guideTopicValueDto,
			ChildPlanReq childPlanReq) {
		log.debug("Entering method selectGuideTopic in ChildPlanDaoImpl");
		if (!ObjectUtils.isEmpty(guideTopicValueDto.getIdEvent())) {
			guideTopicValueDto = executeQueryExisting(guideTopicValueDto, childPlanReq);
		} else {

			guideTopicValueDto = executeQueryNew(guideTopicValueDto, childPlanReq);
		}

		log.debug("Exiting method selectGuideTopic in ChildPlanDaoImpl");
		return guideTopicValueDto;
	}

	@Override
	public String selectPlanTypeCode(Long caseId, Long eventId) {
		log.debug("Entering method selectPlanTypeCode in ChildPlanDaoImpl");
		StringBuilder sql = new StringBuilder(selectPlanTypeCode);

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("cdCspPlanType").setParameter("idCase", caseId).setParameter("idEvent", eventId)
				.setResultTransformer(Transformers.aliasToBean(ChildPlan.class));

		ChildPlan childPlan = (ChildPlan) sQLQuery1.uniqueResult();

		log.debug("Exiting method selectPlanTypeCode in ChildPlanDaoImpl");
		return childPlan.getCdCspPlanType();
	}
	@Override
	public String selectPlanTypeCode( Long eventId) {
		log.debug("Entering method selectPlanTypeCode in ChildPlanDaoImpl");
		StringBuilder sql = new StringBuilder(selectPlanTypeCodeByEvent);

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("cdCspPlanType").setParameter("idEvent", eventId)
				.setResultTransformer(Transformers.aliasToBean(ChildPlan.class));

		ChildPlan childPlan = (ChildPlan) sQLQuery1.uniqueResult();

		log.debug("Exiting method selectPlanTypeCode in ChildPlanDaoImpl");
		return childPlan.getCdCspPlanType();
	}

	@Override
	public List<PersonValueDto> selectPersonsInStage(ChildPlanReq childPlanReq) {
		log.debug("Entering method selectPersonsInStage in ChildPlanDaoImpl");
		String sql = selectPersonsInStage;

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("fullName", StandardBasicTypes.STRING).addScalar("nameSuffixCode", StandardBasicTypes.STRING)
				.addScalar("age", StandardBasicTypes.INTEGER).addScalar("sex", StandardBasicTypes.STRING)
				.addScalar("personId", StandardBasicTypes.LONG)
				.addScalar("isApproxDateOfBirth", StandardBasicTypes.STRING)
				.addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("livingArrangementCode", StandardBasicTypes.STRING)
				.addScalar("personCharacteristicsCode", StandardBasicTypes.STRING)
				.addScalar("maritalStatusCode", StandardBasicTypes.STRING)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE).addScalar("firstName", StandardBasicTypes.STRING)
				.addScalar("lastName", StandardBasicTypes.STRING)
				.addScalar("indStagePersonSearch", StandardBasicTypes.STRING)
				.addScalar("personType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("roleInStageCode", StandardBasicTypes.STRING)
				.addScalar("isReporter", StandardBasicTypes.STRING).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)

				.setParameter("idStage", childPlanReq.getIdStage())
				.setParameter("perStageType", ServiceConstants.PERSON_TYPE_STAFF)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));

		List<PersonValueDto> liPersonValueDto = new ArrayList<PersonValueDto>();

		liPersonValueDto = (List<PersonValueDto>) sQLQuery1.list();

		log.debug("Exiting method selectPersonsInStage in ChildPlanDaoImpl");
		return liPersonValueDto;
	}

	/**
	 * 
	 * Method Name: checkForUnapprovedChildPlanCreatedInIMPACT Method
	 * Description:Checks to see if the given stage has any unapproved child
	 * plans that were created in IMPACT
	 * 
	 * @param caseId
	 * @param stageId
	 * @return Boolean @
	 */
	@Override
	public Boolean checkForUnapprovedChildPlanCreatedInIMPACT(Long caseId, Long stageId) {
		log.debug("Entering method checkForUnapprovedChildPlanCreatedInIMPACT in ChildPlanDaoImpl");
		String sql = checkForUnapprovedChildPlanCreatedInIMPACT;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", caseId)
				.setParameter("idStage", stageId).setParameter("indImpact", ServiceConstants.YES)
				.setResultTransformer(Transformers.aliasToBean(Event.class));
		Event event = (Event) sQLQuery1.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(event)) {
			return ServiceConstants.TRUEVAL;
		}
		log.debug("Exiting method checkForUnapprovedChildPlanCreatedInIMPACT in ChildPlanDaoImpl");
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * 
	 * Method Name: saveGuideTopic Method Description: Saves the guide topic
	 * data to the database.
	 * 
	 * @param guideTopicValueDto
	 * @return ChildPlanGuideTopicValueBeanDto @
	 */
	@Override
	public Long saveGuideTopic(ChildPlanGuideTopicDto guideTopicValueDto) {
		log.debug("Entering method saveGuideTopic in ChildPlanDaoImpl");
		Long rowCount = ServiceConstants.ZERO_VAL;
		if (!ObjectUtils.isEmpty(guideTopicValueDto.getDtLastUpdate())) {
			rowCount = executeUpdate(guideTopicValueDto);
		} else {
			guideTopicValueDto.setDtLastUpdate(new Date());
			rowCount = executeInsert(guideTopicValueDto);
		}

		log.debug("Exiting method saveGuideTopic in ChildPlanDaoImpl");
		return rowCount;
	}

	/**
	 * 
	 * Method Name: getTopicDtLastUpdate Method Description: Saves the guide
	 * topic data to the database.
	 * 
	 * @param valueDto
	 * @return ChildPlanGuideTopicValueBeanDto @
	 */
	@Override
	public ChildPlanGuideTopicDto getTopicDtLastUpdate(ChildPlanGuideTopicDto valueDto) {
		log.debug("Entering method getTopicDtLastUpdate in ChildPlanDaoImpl");
		valueDto.setDtLastUpdate(null);
		String sql = sqlSelectTopicDtLastUpdate;
		
		sql = sql.toString().replaceAll(":tableName", valueDto.getTableName());
		
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.setParameter("idEvent", valueDto.getIdEvent());
		Date dtLastUpdate = (Date) sQLQuery1.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(dtLastUpdate)) {
			log.debug(messageSource.getMessage("Cses56d.not.found.ulIdCase", null, Locale.US));
		}
		valueDto.setDtLastUpdate(dtLastUpdate);
		log.debug("Exiting method getTopicDtLastUpdate in ChildPlanDaoImpl");
		return valueDto;
	}

	/**
	 * 
	 * Method Name: checkIfEventIsLegacy Method Description: Queries a row from
	 * the EVENT_PLAN_LINK for the given event id to determine whether or not
	 * the event is a legacy event--one created before the initial launch of
	 * IMPACT.
	 * 
	 * @param eventId
	 * @return Boolean @
	 */

	@Override
	public Boolean checkIfEventIsLegacy(Long eventId) {
		log.debug("Entering method checkIfEventIsLegacy in ChildPlanDaoImpl");
		String sql = checkIfEventIsLegacy;

		SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idEventFamilyPlanLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("indImpactCreated", StandardBasicTypes.STRING).setParameter("idEvent", eventId)
				.setResultTransformer(Transformers.aliasToBean(EventPlanLinkDto.class));

		List<EventPlanLinkDto> eventPlanLinkList = new ArrayList<>();
		eventPlanLinkList = (List<EventPlanLinkDto>) sQLQuery.list();
		Boolean isLegacyEvent = Boolean.TRUE;

		if (!ObjectUtils.isEmpty(eventPlanLinkList)) {
			for (EventPlanLinkDto eventPlanLink : eventPlanLinkList) {
				if (ServiceConstants.YES.equals(eventPlanLink.getIndImpactCreated())) {
					isLegacyEvent = Boolean.FALSE;
				}
			}
		}

		log.debug("Exiting method checkIfEventIsLegacy in ChildPlanDaoImpl");
		return isLegacyEvent;
	}

	/**
	 * 
	 * Method Name: addEventPlanLinkRow Method Description: Inserts a new row
	 * into the EVENT_PLAN_LINK table to indicate that the child plan was
	 * created using IMPACT.
	 * 
	 * @param eventId
	 * @return Long @
	 */
	@Override
	public Long addEventPlanLinkRow(Long eventId) {
		log.debug("Entering method addEventPlanLinkRow in ChildPlanDaoImpl");

		Event evnt = (Event) sessionFactory.getCurrentSession().get(Event.class, eventId);

		if (TypeConvUtil.isNullOrEmpty(evnt)) {
			throw new DataNotFoundException(messageSource.getMessage("Cses56d.not.found.ulIdCase", null, Locale.US));
		}
		EventPlanLink eventPlanLink = new EventPlanLink();
		eventPlanLink.setEvent(evnt);
		eventPlanLink.setIndImpactCreated(ServiceConstants.Y);

		sessionFactory.getCurrentSession().save(eventPlanLink);
		Long idEventFamilyPlanLink = eventPlanLink.getIdEventFamilyPlanLink();

		log.debug("Exiting method addEventPlanLinkRow in ChildPlanDaoImpl");
		return idEventFamilyPlanLink;
	}

	/**
	 * 
	 * Method Name: checkCrimHist Method Description:This method gets idPerson,
	 * if the Criminal History Action is null for the given Id_Stage.
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	public Long checkCrimHist(Long idStage) {
		log.debug("Entering method checkCrimHist in ChildPlanDaoImpl");
		Long idRecCheck = ServiceConstants.ZERO_VAL;
		String sql = checkCrimHist;

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idRecCheck", StandardBasicTypes.LONG).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(RecordsCheck.class));
		List<RecordsCheck> recordsCheckList = new ArrayList<>();
		recordsCheckList = (List<RecordsCheck>) sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(recordsCheckList)) {
			throw new DataNotFoundException(messageSource.getMessage("Cses56d.not.found.ulIdCase", null, Locale.US));
		}

		for (RecordsCheck recordsCheck : recordsCheckList) {
			idRecCheck = recordsCheck.getIdRecCheck();
		}
		log.debug("Exiting method checkCrimHist in ChildPlanDaoImpl");
		return idRecCheck;
	}

	/**
	 * 
	 * Method Name: updateChildPlanPriorAdopInfo Method Description: This method
	 * updates the ChildPlanBean with Prior Adoption Information
	 * 
	 * @param primaryChildPersonId
	 * @return ChildPlanRes
	 */
	@Override
	public void updateChildPlanPriorAdopInfo(Long primaryChildPersonId, ChildPlanLegacyDto childPlanLegacyDto) {
		log.debug("Entering method updateChildPlanPriorAdopInfo in ChildPlanDaoImpl");

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlSelectPriorAdoptionInfo)
				.addScalar("cdEverAdopted", StandardBasicTypes.STRING)
				.addScalar("dtMostRecentAdoption", StandardBasicTypes.DATE)
				.addScalar("cdAgencyAdoption", StandardBasicTypes.STRING)
				.addScalar("cdEverAdoptInternatl", StandardBasicTypes.STRING)
				.addScalar("indAdoptDateUnknown", StandardBasicTypes.STRING)
				.setParameter("idPerson", primaryChildPersonId)
				.setResultTransformer(Transformers.aliasToBean(PersonDtl.class));

		PersonDtl personDtl = new PersonDtl();
		personDtl = (PersonDtl) sQLQuery1.uniqueResult();

		if (!ObjectUtils.isEmpty(personDtl)) {
			childPlanLegacyDto.setCdEverAdopted(personDtl.getCdEverAdopted());
			childPlanLegacyDto.setDtMostRecentAdoption(personDtl.getDtMostRecentAdoption());
			childPlanLegacyDto.setCdAgnAdopted(personDtl.getCdAgencyAdoption());
			childPlanLegacyDto.setIndRecentAdopDtUnk(personDtl.getIndAdoptDateUnknown());
			childPlanLegacyDto.setCdIntlAdopted(personDtl.getCdEverAdoptInternatl());
		}
		log.debug("Exiting method updateChildPlanPriorAdopInfo in ChildPlanDaoImpl");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao#
	 * getDtLastUpdatePrimaryChild(java.lang.Long)
	 */
	@Override
	public Date getDtLastUpdatePrimaryChild(Long primaryChildPersonId) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlDtLastUpdatePrimaryChild)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setParameter("idPerson", primaryChildPersonId);
		Date dtLastUpdate = (Date) sqlQuery.uniqueResult();

		return dtLastUpdate;
	}

	/**
	 * 
	 * Method Name: getLegalStatusInformation Method Description: This method
	 * retrieves the Legal status info of the Primary child
	 * 
	 * @param primaryChildPersonId
	 * @return List<LegalStatusDto> @
	 */
	@Override
	public List<LegalStatusDto> getLegalStatusInformation(Long primaryChildPersonId) {
		log.debug("Entering method getLegalStatusInformation in ChildPlanDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlSelectLegalStatus)
				.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING)
				.addScalar("dtLegalStatStatusDt", StandardBasicTypes.DATE)
				.setParameter("idPerson", primaryChildPersonId)
				.setParameter("cdLegalStatStatus", ServiceConstants.ADOPTION_CONSUMMATED)
				.setResultTransformer(Transformers.aliasToBean(LegalStatusDto.class));
		List<LegalStatusDto> liLegalStatus = new ArrayList<>();

		liLegalStatus = (List<LegalStatusDto>) sQLQuery1.list();

		log.debug("Exiting method getLegalStatusInformation in ChildPlanDaoImpl");
		return liLegalStatus;
	}

	/**
	 * 
	 * Method Name: saveChildPriorAdoption Method Description: This method save
	 * the Prior Adoption information
	 * 
	 * @param childPlanLegacyDto
	 * @param primaryChildPersonId
	 * @param dtLastUpdatePrimaryChild
	 * @ @return Long
	 */
	@Override
	public Long saveChildPriorAdoption(ChildPlanLegacyDto childPlanLegacyDto) {
		log.debug("Entering method saveChildPriorAdoption in ChildPlanDaoImpl");
		Long idPerson = ServiceConstants.ZERO_VAL;
		// String dtLastUpdate =
		// frmat.format(childPlanLegacyDto.getDtLastUpdatePrimaryChild());
		if (!ObjectUtils.isEmpty(childPlanLegacyDto.getDtLastUpdatePrimaryChild())) {
			PersonDtl personDtl = (PersonDtl) sessionFactory.getCurrentSession().get(PersonDtl.class,
					childPlanLegacyDto.getPrimaryChildPersonId());
			if (personDtl.getDtLastUpdate().compareTo(childPlanLegacyDto.getDtLastUpdatePrimaryChild()) == 0) {
				if (!ObjectUtils.isEmpty(childPlanLegacyDto.getDtMostRecentAdoption())) {
					personDtl.setDtMostRecentAdoption(childPlanLegacyDto.getDtMostRecentAdoption());
				}

				if (!ObjectUtils.isEmpty(childPlanLegacyDto.getIndRecentAdopDtUnk())) {
					personDtl.setIndAdoptDateUnknown(childPlanLegacyDto.getIndRecentAdopDtUnk());
				}
			}
			personDtl.setCdEverAdopted(childPlanLegacyDto.getCdEverAdopted());
			personDtl.setCdAgencyAdoption(childPlanLegacyDto.getCdAgnAdopted());
			personDtl.setCdEverAdoptInternatl(childPlanLegacyDto.getCdIntlAdopted());

		} else {
			PersonDtl personDtl = new PersonDtl();
			personDtl.setCdEverAdopted(childPlanLegacyDto.getCdEverAdopted());
			personDtl.setDtMostRecentAdoption(childPlanLegacyDto.getDtMostRecentAdoption());
			personDtl.setCdAgencyAdoption(childPlanLegacyDto.getCdAgnAdopted());
			personDtl.setCdEverAdoptInternatl(childPlanLegacyDto.getCdEverAdopted());
			personDtl.setIndAdoptDateUnknown(childPlanLegacyDto.getIndRecentAdopDtUnk());
			sessionFactory.getCurrentSession().save(personDtl);
			idPerson = personDtl.getIdPerson();
		}
		log.debug("Exiting method saveChildPriorAdoption in ChildPlanDaoImpl");
		return idPerson;
	}

	/**
	 * 
	 * Method Name: getBirthDate Method Description: This method returns the
	 * Birth date of the Primary Child
	 * 
	 * @param primaryChildPersonId
	 * @return Date
	 */
	@Override
	public Date getBirthDate(Long primaryChildPersonId) {
		log.debug("Entering method getBirthDate in ChildPlanDaoImpl");
		Date dtBirthDate = null;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlSelectPrimaryChildBirthDate).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.setParameter("idPerson", primaryChildPersonId)
				.setResultTransformer(Transformers.aliasToBean(Person.class));
		List<Person> liPerson = new ArrayList<>();
		liPerson = (List<Person>) sQLQuery1.list();

		for (Person person : liPerson) {
			dtBirthDate = person.getDtPersonBirth();
		}

		if (TypeConvUtil.isNullOrEmpty(liPerson)) {
			throw new DataNotFoundException(messageSource.getMessage("Cses56d.not.found.ulIdCase", null, Locale.US));
		}
		log.debug("Exiting method getBirthDate in ChildPlanDaoImpl");
		return dtBirthDate;
	}

	/**
	 * 
	 * Method Name: getStaffSearchResultInformation Method Description: Returns
	 * the list of Primary and Secondary case workers
	 * 
	 * @param stageId
	 * @return List<StaffSearchResultDto> @
	 */
	@Override
	public List<StaffSearchResultDto> getStaffSearchResultInformation(Long stageId) {
		log.debug("Entering method getStaffSearchResultInformation in ChildPlanDaoImpl");
		List<StaffSearchResultDto> staffSearchResList = new ArrayList<>();
		List<Long> personIdList = new ArrayList<>();
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlSelectStaff)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idStage", stageId).setResultTransformer(Transformers.aliasToBean(Person.class));
		List<Person> liPerson = new ArrayList<>();
		liPerson = (List<Person>) sQLQuery1.list();

		for (Person person : liPerson) {
			StaffSearchResultDto staffSearchResultDto = new StaffSearchResultDto();
			staffSearchResultDto.setIdPerson(person.getIdPerson());
			staffSearchResultDto.setSzNmPersonFull(person.getNmPersonFull());
			staffSearchResList.add(staffSearchResultDto);
			personIdList.add(person.getIdPerson());
		}

		if (TypeConvUtil.isNullOrEmpty(liPerson)) {
			throw new DataNotFoundException(messageSource.getMessage("Cses56d.not.found.ulIdCase", null, Locale.US));
		}
		StringBuilder sql = new StringBuilder(sqlStaffTop);
		Integer len = staffSearchResList.size();
		for (int i = 0; i < len; i++) {
			if (i == len - ServiceConstants.INT_ONE) {
				sql.append(staffSearchResList.get(i).getIdPerson());
				sql.append(ServiceConstants.SQL_STAFF_BOTTOM);
			} else {
				sql.append(staffSearchResList.get(i).getIdPerson());
				sql.append(ServiceConstants.COMMA);
			}
		}
		sessionFactory.getCurrentSession().createSQLQuery(sql.toString()).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(Person.class));
		List<Person> personList = new ArrayList<>();
		personList = (List<Person>) sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(personList)) {
			throw new DataNotFoundException(messageSource.getMessage("Cses56d.not.found.ulIdCase", null, Locale.US));
		}

		for (Person personL : personList) {
			if (!personIdList.contains(personL.getIdPerson())) {
				StaffSearchResultDto staffSearchResultDto = new StaffSearchResultDto();
				staffSearchResultDto.setIdPerson(personL.getIdPerson());
				staffSearchResultDto.setSzNmPersonFull(personL.getNmPersonFull());
				staffSearchResList.add(staffSearchResultDto);
			}

		}

		log.debug("Exiting method getStaffSearchResultInformation in ChildPlanDaoImpl");
		return staffSearchResList;
	}

	/**
	 * 
	 * Method Name: isChildPlanExistForEvent Method Description: to check for
	 * Child Plan By Id Event
	 * 
	 * @param idEvent
	 * @return Boolean @
	 */
	@Override
	public Boolean isChildPlanExistForEvent(Long idEvent) {
		log.debug("Entering method isChildPlanExistForEvent in ChildPlanDaoImpl");
		Boolean toReturn = Boolean.FALSE;
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectChildPlanSqlByEvent)
				.addScalar("idChildPlanEvent", StandardBasicTypes.LONG).setParameter("idChildPlanEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ChildPlan.class));
		List<ChildPlan> liChildPlan = new ArrayList<>();
		liChildPlan = (List<ChildPlan>) sQLQuery1.list();

		if (!TypeConvUtil.isNullOrEmpty(liChildPlan)) {
			toReturn = ServiceConstants.TRUEVAL;
		}
		log.debug("Exiting method isChildPlanExistForEvent in ChildPlanDaoImpl");
		return toReturn;
	}

	/**
	 * 
	 * Method Name: selectChildPlansForPID Method Description: idChildPlanEvent
	 * for Child Plan By Id Person
	 * 
	 * @param idPerson
	 * @return List<Long> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> selectChildPlansForPID(Long idPerson) {
		log.debug("Entering method selectChildPlansForPID in ChildPlanDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectChildPlanSqlByPid)
				// .addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.setParameter("idPerson", idPerson);
		// .setResultTransformer(Transformers.aliasToBean(ChildPlan.class));

		List<Long> liChildPlan = new ArrayList<>();
		liChildPlan = (List<Long>) sQLQuery1.list();

		log.debug("Exiting method selectChildPlansForPID in ChildPlanDaoImpl");
		return liChildPlan;
	}

	/**
	 *
	 * @param guideTopicValueDto
	 * @param childPlanReq
	 * @return ChildPlanGuideTopicValueBeanDto @
	 */
	private ChildPlanGuideTopicDto executeQueryExisting(ChildPlanGuideTopicDto guideTopicValueDto,
			ChildPlanReq childPlanReq) {
		log.debug("Entering method executeQueryExisting in ChildPlanDaoImpl");
		StringBuilder sql = new StringBuilder(sqlExistingPattern);
		if (ServiceConstants.SERVER_IMPACT)
			sql.append(ServiceConstants.SQL_EXISTING_PATTERN_ADDN);
		else
			sql.append(ServiceConstants.SQL_EXISTING_PATTERN_ADDN_MPS);

		String query = sql.toString().replaceAll(":tableName", guideTopicValueDto.getTableName());

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query)
				.addScalar("instructions", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("topic", StandardBasicTypes.STRING)
				.addScalar("dtNewUsed", StandardBasicTypes.DATE).addScalar("narrative", StandardBasicTypes.BLOB)
				.setParameter("cdCpTopic", guideTopicValueDto.getCode())
				.setParameter("idEvent", guideTopicValueDto.getIdEvent());

		if (ServiceConstants.SERVER_IMPACT) {
			ChildPlanLegacyDto childPlanLegacyDto = childPlanReq.getChildPlanLegacyDto();
			Date eventOccouredDate = null; 
			if (!ObjectUtils.isEmpty(childPlanLegacyDto) && !ObjectUtils.isEmpty(childPlanLegacyDto.getEventDto())) {
				eventOccouredDate = childPlanLegacyDto.getEventDto().getDtEventOccurred();				
			}
			if (ObjectUtils.isEmpty(eventOccouredDate)) {
				eventOccouredDate = new Date();
			} 
			String formattedDate = DateUtils.formatDatetoString(eventOccouredDate);
			sQLQuery1.setParameter("date1", formattedDate).setParameter("date2", formattedDate);
		}
		sQLQuery1.setResultTransformer(Transformers.aliasToBean(ChildPlanGuideTopicDto.class));

		ChildPlanGuideTopicDto childPlanGuideDto = (ChildPlanGuideTopicDto) sQLQuery1.uniqueResult();

		if (!ObjectUtils.isEmpty(childPlanGuideDto)) {
			guideTopicValueDto.setInstructions(childPlanGuideDto.getInstructions());
			guideTopicValueDto.setTopic(childPlanGuideDto.getTopic());
			guideTopicValueDto.setDtLastUpdate(childPlanGuideDto.getDtLastUpdate());
			guideTopicValueDto.setDtNewUsed(childPlanGuideDto.getDtNewUsed());

			Blob narrative = childPlanGuideDto.getNarrative();
			if (!ObjectUtils.isEmpty(narrative)) {
				byte[] bytes = null;
				try {
					bytes = narrative.getBytes(1, (int) narrative.length());
				} catch (SQLException e) {
					throw new DataLayerException(e.getMessage());
				}
				guideTopicValueDto.setBlob(unwrapBlob(bytes));
			}

		}

		if (ObjectUtils.isEmpty(childPlanGuideDto)) {
			executeQueryNew(guideTopicValueDto, childPlanReq);
		}

		log.debug("Exiting method executeQueryExisting in ChildPlanDaoImpl");

		return guideTopicValueDto;
	}

	@Override
	public String unwrapBlob(byte[] blobData) {
		String data = null;
		try {
			data = CompressionHelper.decompressData(blobData).toString(ServiceConstants.CHARACTER_ENCODING);
			int start = data.indexOf("<fieldValue>");
			if (start < 0) {
				return null;
			}
			start += "<fieldValue>".length();

			int end = data.lastIndexOf("</fieldValue>");
			data = data.substring(start, end);
			data = new String(Base64.decode(data), ServiceConstants.CHARACTER_ENCODING);
			data = data.replaceAll("<br>", "").replaceAll("\n", "");

		} catch (Exception e) {
			DataLayerException dataException = new DataLayerException(e.getMessage());
			dataException.initCause(e);
			throw dataException;
		}
		return data;
	}

	private byte[] wrapBlob(String text) {
		byte[] data;
		try {
			data = null;
			text = Base64.encode(text.getBytes(ServiceConstants.CHARACTER_ENCODING));
			text = ServiceConstants.XML_HEADER
					+ "<data><userEdits><userEdit><fieldName>txtBlankNarrative</fieldName><fieldValue>" + text
					+ "</fieldValue></userEdit></userEdits></data>";
			data = CompressionHelper.compressData(text.getBytes(ServiceConstants.CHARACTER_ENCODING)).toByteArray();
		} catch (InvalidModeException | InvalidDictionarySizeException | IOException | InterruptedException e) {
			DataLayerException dataException = new DataLayerException(e.getMessage());
			dataException.initCause(e);
			throw dataException;
		}
		return data;
	}

	/**
	 *
	 * @param guideTopicValueDto
	 * @param childPlanReq
	 * @return ChildPlanGuideTopicValueBeanDto @
	 */
	private ChildPlanGuideTopicDto executeQueryNew(ChildPlanGuideTopicDto guideTopicValueDto,
			ChildPlanReq childPlanReq) {
		log.debug("Entering method executeQueryNew in ChildPlanDaoImpl");
		StringBuilder sql = new StringBuilder(sqlNewPattern);
		if (ServiceConstants.SERVER_IMPACT)
			sql.append(ServiceConstants.SQL_NEW_PATTERN_ADDN);
		else
			sql.append(ServiceConstants.SQL_NEW_PATTERN_ADDN_MPS);

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
				.addScalar("instructions", StandardBasicTypes.STRING).addScalar("topic", StandardBasicTypes.STRING)
				.setParameter("cdCpTopic", guideTopicValueDto.getCode());

		if (ServiceConstants.SERVER_IMPACT) {
			ChildPlanLegacyDto childPlanLegacyDto = childPlanReq.getChildPlanLegacyDto();
			Date eventOccouredDate = null; 
			if (!ObjectUtils.isEmpty(childPlanLegacyDto) && !ObjectUtils.isEmpty(childPlanLegacyDto.getEventDto())) {
				eventOccouredDate = childPlanLegacyDto.getEventDto().getDtEventOccurred();				
			}
			if (ObjectUtils.isEmpty(eventOccouredDate)) {
				eventOccouredDate = new Date();
			} 
			String formattedDate = DateUtils.formatDatetoString(eventOccouredDate);
			sQLQuery1.setParameter("date1", formattedDate).setParameter("date2", formattedDate);
		}
		sQLQuery1.setResultTransformer(Transformers.aliasToBean(ChildPlanGuideTopicDto.class));

		ChildPlanGuideTopicDto childPlanGuideDto = (ChildPlanGuideTopicDto) sQLQuery1.uniqueResult();

		if (!ObjectUtils.isEmpty(childPlanGuideDto)) {
			guideTopicValueDto.setInstructions(childPlanGuideDto.getInstructions());
			guideTopicValueDto.setTopic(childPlanGuideDto.getTopic());

		}
		log.debug("Exiting method executeQueryNew in ChildPlanDaoImpl");
		return guideTopicValueDto;
	}

	/**
	 * Saves the updated guide topic data to the database.
	 */
	private Long executeUpdate(ChildPlanGuideTopicDto guideTopicValueDto) {
		log.debug("Entering method executeUpdate in ChildPlanDaoImpl");
		String sql = ServiceConstants.EMPTY_STRING;
		String date = ServiceConstants.EMPTY_STRING;
		SimpleDateFormat frmt = new SimpleDateFormat(ServiceConstants.SQL_DATE_FORMAT);
		if (ServiceConstants.SERVER_IMPACT) {
			sql = sqlUpdatePattern;
			date = frmt.format(guideTopicValueDto.getDtLastUpdate()).toUpperCase();
		} else {
			sql = sqlMpsUpdatePattern;
			date = frmt.format(guideTopicValueDto.getDtLastUpdate()).toUpperCase();
		}

		sql = sql.toString().replaceAll(":tableName", guideTopicValueDto.getTableName());

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.setParameter("idEvent", guideTopicValueDto.getIdEvent())
				.setParameter("narrative", wrapBlob(guideTopicValueDto.getBlob())).setParameter("dtLastUpdate", date);

		Integer updateCount = sQLQuery1.executeUpdate();

		log.debug("Exiting method executeUpdate in ChildPlanDaoImpl");
		return updateCount.longValue();
	}

	private Long executeInsert(ChildPlanGuideTopicDto guideTopicValueDto) {
		log.debug("Entering method executeInsert in ChildPlanDaoImpl");
		String sql = sqlInsertPattern;
		SimpleDateFormat frmt = new SimpleDateFormat(ServiceConstants.SIMPLE_DATE_FORMAT);
		Date dtLastUpdate = null == guideTopicValueDto.getDtLastUpdate() ? new Date()
				: guideTopicValueDto.getDtLastUpdate();
		String date = frmt.format(dtLastUpdate).toUpperCase();

		sql = sql.toString().replaceAll(":tableName", guideTopicValueDto.getTableName());

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.setParameter("idEvent", guideTopicValueDto.getIdEvent())
				.setParameter("narrative", wrapBlob(guideTopicValueDto.getBlob())).setParameter("dtLastUpdate", date)
				.setParameter("documentTemplate", 29)
				.setResultTransformer(Transformers.aliasToBean(ChildPlanGuideTopicDto.class));

		Integer updateCount = sQLQuery1.executeUpdate();

		log.debug("Exiting method executeInsert in ChildPlanDaoImpl");
		return updateCount.longValue();
	}

	/**
	 * 
	 * Method Name: getChildPlanEvents Method Description:This method to get
	 * child plans with Conservatorship removal date
	 * 
	 * @param idStage
	 * @param idCase
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ChildPlanOfServiceDtlDto> getChildPlanEvents(Long idStage, Long idCase) {
		List<ChildPlanOfServiceDtlDto> childPlans = (List<ChildPlanOfServiceDtlDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getChildPlanEventsSql).addScalar("idChildPlanEvent", StandardBasicTypes.LONG)
				.addScalar("dtCVSRemoval", StandardBasicTypes.DATE).addScalar("cdCspPlanType")
				.addScalar("cdEventStatus").addScalar("dtCspNextReview", StandardBasicTypes.DATE)
				.setParameter("IDSTAGE", idStage)
				.setParameter("IDCASE", !StringUtils.isEmpty(idCase) ? idCase : ServiceConstants.ZERO)
				.setResultTransformer(Transformers.aliasToBean(ChildPlanOfServiceDtlDto.class)).list();
		return childPlans;
	}

	/**
	 * Method Name: deleteTopicTable Method Description:This Method is used to
	 * delete the child plan based The event id of the child plan.
	 * 
	 * @param idEvent
	 * @param List<CurrentlySelectedPlanDto>
	 */
	@Override
	public void deleteTopicTable(Long idEvent, List<CurrentlySelectedPlanDto> childPlanList) {
		log.debug("Entering method deleteChildPlan in ChildPlanDaoImpl");
		StringBuilder sql = new StringBuilder(deleteChildPlan);
		for (CurrentlySelectedPlanDto childPlan : childPlanList) {
			String query = sql.toString().replaceAll(":tableName", childPlan.getCpTopicTable());
			SQLQuery sQLQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query)
					.setParameter("idEvent", idEvent);
			sQLQuery.executeUpdate();
		}
	}

	/**
	 * Method Name: deleteChildPlanParticip Method Description:This Method is
	 * used to delete Child Plan Particip based The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	@Override
	public void deleteChildPlanParticip(Long idEvent) {
		Criteria fetchChildPlanParticip = sessionFactory.getCurrentSession().createCriteria(ChildPlanParticip.class);
		fetchChildPlanParticip.add(Restrictions.eq("event.idEvent", idEvent));
		List<ChildPlanParticip> childPlanParticipList = (List<ChildPlanParticip>) fetchChildPlanParticip.list();
		if (!CollectionUtils.isEmpty(childPlanParticipList)) {
			childPlanParticipList.forEach(childPlanParticip -> {
				sessionFactory.getCurrentSession().delete(childPlanParticip);
			});
		}
	}

	/**
	 * Method Name: deleteChildPlanItem Method Description:This Method is used
	 * to delete Child Plan Item based The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	@Override
	public void deleteChildPlanItem(Long idEvent) {
		Criteria fetchChildPlanItem = sessionFactory.getCurrentSession().createCriteria(ChildPlanItem.class);
		fetchChildPlanItem.add(Restrictions.eq("event.idEvent", idEvent));
		List<ChildPlanItem> childPlanItemList = (List<ChildPlanItem>) fetchChildPlanItem.list();
		if (!CollectionUtils.isEmpty(childPlanItemList)) {
			childPlanItemList.forEach(childPlanItem -> {
				sessionFactory.getCurrentSession().delete(childPlanItem);
			});
		}
	}

	/**
	 * Method Name: deleteTodo Method Description:This Method is used to delete
	 * Todo based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	@Override
	public void deleteTodo(Long idEvent) {
		Criteria fetchTodo = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		fetchTodo.add(Restrictions.eq("event.idEvent", idEvent));
		List<Todo> todoList = (List<Todo>) fetchTodo.list();
		if (!CollectionUtils.isEmpty(todoList)) {
			todoList.forEach(todo -> {
				sessionFactory.getCurrentSession().delete(todo);
			});
		}
	}

	/**
	 * Method Name: deleteChildPlan Method Description:This Method is used to
	 * delete Child Plan based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteChildPlan(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
		criteria.add(Restrictions.eq("idChildPlanEvent", idEvent));
		ChildPlan childPlan = (ChildPlan) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(childPlan)) {
			sessionFactory.getCurrentSession().delete(childPlan);
			
			//deleting sscc childplan 
			criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);
			criteria.add(Restrictions.eq("idChildPlanEvent", idEvent));
			List<SsccChildPlan> ssccChildPlans = criteria.list();

			if (!CollectionUtils.isEmpty(ssccChildPlans)) {
				ssccChildPlans.forEach(ssccChildPlan -> {
					sessionFactory.getCurrentSession().delete(ssccChildPlan);
				});
			}

		}
	}

	/**
	 * Method Name: deleteCpConcurrentGoal Method Description:This Method is
	 * used to delete Cp Concurrent Goal based on The event id of the child
	 * plan.
	 * 
	 * @param idEvent
	 */
	@Override
	public void deleteCpConcurrentGoal(Long idEvent) {
		Criteria fetchCpConcurrentGoal = sessionFactory.getCurrentSession().createCriteria(CpConcurrentGoal.class);
		fetchCpConcurrentGoal.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));
		List<CpConcurrentGoal> cpConcurrentGoalList = (List<CpConcurrentGoal>) fetchCpConcurrentGoal.list();
		if (!CollectionUtils.isEmpty(cpConcurrentGoalList)) {
			cpConcurrentGoalList.forEach(cpConcurrentGoal -> {
				sessionFactory.getCurrentSession().delete(cpConcurrentGoal);
			});
		}
	}

	/**
	 * Method Name: deleteCpConcurrentGoal Method Description:This Method is
	 * used to delete Event Plan Link based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	@Override
	public void deleteEventPlanLink(Long idEvent) {
		Criteria fetchEventPlanLink = sessionFactory.getCurrentSession().createCriteria(EventPlanLink.class);
		fetchEventPlanLink.add(Restrictions.eq("event.idEvent", idEvent));
		List<EventPlanLink> eventPlanLinkList = (List<EventPlanLink>) fetchEventPlanLink.list();
		if (!CollectionUtils.isEmpty(eventPlanLinkList)) {
			eventPlanLinkList.forEach(eventPlanLink -> {
				sessionFactory.getCurrentSession().delete(eventPlanLink);
			});
		}
	}

	/**
	 * Method Name: deleteEventPersonlink Method Description:This Method is used
	 * to delete Event person Link based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	@Override
	public void deleteEventPersonlink(Long idEvent) {
		Criteria fetchEventPersonLink = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
		fetchEventPersonLink.add(Restrictions.eq("event.idEvent", idEvent));
		List<EventPersonLink> eventPersonLinkList = (List<EventPersonLink>) fetchEventPersonLink.list();
		if (!CollectionUtils.isEmpty(eventPersonLinkList)) {
			eventPersonLinkList.forEach(eventPersonLink -> {
				sessionFactory.getCurrentSession().delete(eventPersonLink);
			});
		}
	}

	@Override
	public void childPlanAud(ChildPlanEventDto childPlanEventDto, String cdReqFunc) {
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(cdReqFunc)) {

			ChildPlan childPlan = new ChildPlan();
			childPlan.setIdChildPlanEvent(childPlanEventDto.getIdChildPlanEvent());

			populateChildPlan(childPlan, childPlanEventDto);

			sessionFactory.getCurrentSession().save(childPlan);

		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(cdReqFunc)) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
			criteria.add(Restrictions.eq("idChildPlanEvent", childPlanEventDto.getIdChildPlanEvent()));
			ChildPlan childPlan = (ChildPlan) criteria.uniqueResult();

			if (!ObjectUtils.isEmpty(childPlan)
					&& childPlan.getDtLastUpdate().compareTo(childPlanEventDto.getDtLastUpdate()) == 0) {
				populateChildPlan(childPlan, childPlanEventDto);
				sessionFactory.getCurrentSession().update(childPlan);
			}
			else {
				throw new DataLayerException("", Long.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH), null);
			}

		} else if (ServiceConstants.REQ_FUNC_CD_DELETE.equals(cdReqFunc)) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
			criteria.add(Restrictions.eq("idChildPlanEvent", childPlanEventDto.getIdChildPlanEvent()));
			criteria.add(new DateLikeExpression("dtLastUpdate",
					TypeConvUtil.formatDate(childPlanEventDto.getDtLastUpdate())));
			ChildPlan childPlan = (ChildPlan) criteria.uniqueResult();
			sessionFactory.getCurrentSession().delete(childPlan);

		}

	}

	public void populateChildPlan(ChildPlan childPlan, ChildPlanEventDto childPlanEventDto) {
		childPlan.setCdCspPlanPermGoal(childPlanEventDto.getCdCspPlanPermGoal());
		childPlan.setCdCspPlanType(childPlanEventDto.getCdCspPlanType());
		childPlan.setDtCspPermGoalTarget(childPlanEventDto.getDtCspPermGoalTarget());
		childPlan.setDtCspNextReview(childPlanEventDto.getDtCspNextReview());
		childPlan.setTxtCspLengthOfStay(childPlanEventDto.getCspLengthOfStay());
		childPlan.setTxtCspLosDiscrepancy(childPlanEventDto.getCspDiscrepancy());
		childPlan.setTxtCspParticipComment(childPlanEventDto.getCspParticipComment());
		childPlan.setDtCspPlanCompleted(childPlanEventDto.getDtCspPlanCompleted());
		childPlan.setDtInitialTransitionPlan(childPlanEventDto.getDtInitTransitionPlan());
		childPlan.setIndParentsParticipated(childPlanEventDto.getIndParentsParticipated());
		childPlan.setTxtInfoNotAvail(childPlanEventDto.getInfoNotAvail());
		childPlan.setTxtOtherAssmt(childPlanEventDto.getOtherAssmt());
		childPlan.setIndNoConGoal(childPlanEventDto.getIndNoConGoal());
		childPlan.setTxtNoConGoals(childPlanEventDto.getNoConGoal());

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, childPlanEventDto.getIdPerson());
		childPlan.setPerson(person);

		childPlan.setCdSsccPurpose(childPlanEventDto.getCdSsccPlanPurpose());

		childPlan.setDtLastUpdate(new Date());
	}

	@Override
	public void addCpConcurrentGoal(String cdConcurrentGoal, ChildPlanEventDto childPlanEventDto, Long idCase,
			Long idStage) {

		CpConcurrentGoal cpConcurrentGoal = new CpConcurrentGoal();

		ChildPlan childPlan = (ChildPlan) sessionFactory.getCurrentSession().load(ChildPlan.class,
				childPlanEventDto.getIdChildPlanEvent());
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class, idCase);
		Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, idStage);

		cpConcurrentGoal.setChildPlan(childPlan);
		cpConcurrentGoal.setCapsCase(capsCase);
		cpConcurrentGoal.setStage(stage);
		cpConcurrentGoal.setCdConcurrentGoal(cdConcurrentGoal);
		cpConcurrentGoal.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(cpConcurrentGoal);
		// sessionFactory.getCurrentSession().flush();

	}

	@Override
	public void deleteCpConcurrentGoalById(Long idCpConGoal) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpConcurrentGoal.class);
		criteria.add(Restrictions.eq("idCpConcurrentGoal", idCpConGoal));

		CpConcurrentGoal cpConcurrentGoal = (CpConcurrentGoal) criteria.uniqueResult();
		sessionFactory.getCurrentSession().delete(cpConcurrentGoal);

	}

	@Override
	public void deleteSuperVisionDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpSprvsnDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpSprvsnDtl> list = (List<CpSprvsnDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteCpTranstnAdultBlwDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpTranstnAdultBlwDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpTranstnAdultBlwDtl> list = (List<CpTranstnAdultBlwDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteSocialRecreationalDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpSoclRecrtnalDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpSoclRecrtnalDtl> list = (List<CpSoclRecrtnalDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteChildFamilyTeamDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpChildFmlyTeamDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpChildFmlyTeamDtl> list = (List<CpChildFmlyTeamDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteBehaviouralDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpBhvrMgmt.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpBhvrMgmt> list = (List<CpBhvrMgmt>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteYouthPregntDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpYouthPregntPrntg.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpYouthPregntPrntg> list = (List<CpYouthPregntPrntg>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteIntlCtlDevelopment(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpIntlctlDvlpmntl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpIntlctlDvlpmntl> list = (List<CpIntlctlDvlpmntl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteTreatementServiceDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpTrtmntSrvcDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpTrtmntSrvcDtl> list = (List<CpTrtmntSrvcDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteAdultAboveDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpTranstnAdultAbvDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpTranstnAdultAbvDtl> list = (List<CpTranstnAdultAbvDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteHighRiskBehavourDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpSvcsHghRiskBhvr.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpSvcsHghRiskBhvr> list = (List<CpSvcsHghRiskBhvr>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteCPInformation(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpInformation.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpInformation> list = (List<CpInformation>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteCPEducationDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpEductnDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpEductnDtl> list = (List<CpEductnDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteCPEmtnlThrptcDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpEmtnlThrptcDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpEmtnlThrptcDtl> list = (List<CpEmtnlThrptcDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteMedCtnDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpHlthCareSumm.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));
		List<CpHlthCareSumm> listOuter = (List<CpHlthCareSumm>) criteria.list();
		if (!CollectionUtils.isEmpty(listOuter)) {
			listOuter.forEach(obj -> {
				Criteria criteriaMed = sessionFactory.getCurrentSession().createCriteria(CpPsychMedctnDtl.class);
				criteriaMed.add(Restrictions.eq("cpHlthCareSumm.idCpHlthCareSumm", obj.getIdCpHlthCareSumm()));

				List<CpPsychMedctnDtl> list = (List<CpPsychMedctnDtl>) criteriaMed.list();
				if (!CollectionUtils.isEmpty(list)) {
					list.forEach(objMed -> {
						sessionFactory.getCurrentSession().delete(objMed);
					});
				}
			});
		}
	}

	@Override
	public void deleteCPHealthCareSumm(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpHlthCareSumm.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpHlthCareSumm> list = (List<CpHlthCareSumm>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteSSCCChildPlan(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);
		criteria.add(Restrictions.eq("idChildPlanEvent", idEvent));

		List<SsccChildPlan> list = (List<SsccChildPlan>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteVisitCntFmly(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpVisitCntctFmly.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpVisitCntctFmly> list = (List<CpVisitCntctFmly>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteQrtpPtm(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpQrtpPrmnTmMtng.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpQrtpPrmnTmMtng> list = criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				Long idCpQrtpPtm = obj.getIdCpQrtpPtm();
                deleteCpQrtpParticipants(idCpQrtpPtm);
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	private void deleteCpQrtpParticipants(Long idCpQrtpPtm) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpQrtpPtmParticipant.class);
		criteria.add(Restrictions.eq("cpQrtpPrmnTmMtng.idCpQrtpPtm", idCpQrtpPtm));
		List<CpQrtpPtmParticipant> list = criteria.list();

		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj ->
				sessionFactory.getCurrentSession().delete(obj)
			);
		}
	}

	@Override
	public void deleteAdtnlSctnDtls(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpAdtnlSctnDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpAdtnlSctnDtl> list = (List<CpAdtnlSctnDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteCpLstGoals(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpLstGoal.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpLstGoal> list = (List<CpLstGoal>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteCpAdoptnDtl(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpAdoptnDtl.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpAdoptnDtl> list = (List<CpAdoptnDtl>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}

	@Override
	public void deleteLegalGardianShip(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpLglGrdnshp.class);
		criteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));

		List<CpLglGrdnshp> list = (List<CpLglGrdnshp>) criteria.list();
		if (!CollectionUtils.isEmpty(list)) {
			list.forEach(obj -> {
				sessionFactory.getCurrentSession().delete(obj);
			});
		}
	}


}
