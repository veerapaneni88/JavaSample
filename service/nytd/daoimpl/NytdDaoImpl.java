package us.tx.state.dfps.service.nytd.daoimpl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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

import us.tx.state.dfps.common.domain.NytdSurveyHeader;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.service.casepackage.dto.DesignatedContactDto;
import us.tx.state.dfps.service.casepackage.dto.SurveyHistoryDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.nytd.dao.NytdDao;
import us.tx.state.dfps.service.nytd.dto.NytdReportPeriodDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchResultDto;
import us.tx.state.dfps.service.nytd.dto.NytdSearchValueDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthContactInfoDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthHistoryDto;
import us.tx.state.dfps.service.nytd.dto.NytdYouthHistoryResultDto;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.PersonEmailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * is used to call the fetch, update and insert data into NYTD related tables.
 * Apr 6, 2018- 1:06:05 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class NytdDaoImpl implements NytdDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NytdDaoImpl.queryNytdReportPeriod}")
	private transient String nytdReportPeriodSql;

	@Value("${NytdDaoImpl.setNewPersonView}")
	private transient String nytdSetNewPersonViewSql;

	@Value("${NytdDaoImpl.saveSurveyAppAuthInfo}")
	private transient String nytdSaveSurveyAppAuthInfo;

	@Value("${NytdDaoImpl.retrieveNytdYouthHistory}")
	private transient String nytdRetrieveNytdYouthHistory;

	@Value("${NytdDaoImpl.getDesignatedContacts}")
	private transient String nytdGetDesignatedContacts;

	@Value("${NytdDaoImpl.searchNytdPopulation}")
	private transient String searchNytdPopulation;

	@Value("${NytdDaoImpl.retrieveNytdYouthContact}")
	private transient String retrieveNytdYouthContact;

	@Value("${NytdDaoImpl.youthCurrentEmailList}")
	private transient String youthCurrentEmailList;

	@Value("${NytdDaoImpl.youthCurrentPhoneList}")
	private transient String youthCurrentPhoneList;

	@Value("${NytdDaoImpl.youthCurrentAddressList}")
	private transient String youthCurrentAddressList;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getCurrentReportingPeriod Method Description: Gets NYTD
	 * Reporting Period for input date
	 * 
	 * @param dtReportingDate
	 * @return NytdReportPeriodDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public NytdReportPeriodDto getNytdReportingPeriod(Date dtReportingDate) {
		NytdReportPeriodDto nytdReportPeriodDto = new NytdReportPeriodDto();
		nytdReportPeriodDto = (NytdReportPeriodDto) sessionFactory.getCurrentSession()
				.createSQLQuery(nytdReportPeriodSql).addScalar("idNytdReportPeriod", StandardBasicTypes.LONG)
				.addScalar("nbrFederalYear", StandardBasicTypes.LONG)
				.addScalar("cdFederalPeriod", StandardBasicTypes.STRING)
				.addScalar("dtReportStart", StandardBasicTypes.DATE).addScalar("dtReportEnd", StandardBasicTypes.DATE)
				.addScalar("indBaseline", StandardBasicTypes.CHARACTER)
				.addScalar("indFollowup19", StandardBasicTypes.CHARACTER)
				.addScalar("indFollowup21", StandardBasicTypes.CHARACTER)
				.addScalar("indServed", StandardBasicTypes.CHARACTER)
				.addScalar("indFollowupDetermined", StandardBasicTypes.CHARACTER)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("dtPrevReportStart", StandardBasicTypes.DATE)
				.addScalar("dtPrevReportEnd", StandardBasicTypes.DATE)
				.addScalar("idPrevReportPeriod", StandardBasicTypes.LONG)
				.setParameter("date", DateUtils.toString(dtReportingDate, DateUtils.slashFormat))
				.setResultTransformer(Transformers.aliasToBean(NytdReportPeriodDto.class)).uniqueResult();

		// Set up calculated fields for display
		if (!ObjectUtils.isEmpty(nytdReportPeriodDto)) {
			Date startDate = nytdReportPeriodDto.getDtReportStart();
			Date endDate = nytdReportPeriodDto.getDtReportEnd();
			nytdReportPeriodDto.setReportPeriod(DateUtils.getStandardNytdFormattedPeriod(startDate, endDate));
			Date prevStartDate = nytdReportPeriodDto.getPrevReportStartDate();
			Date prevEndDate = nytdReportPeriodDto.getPrevReportEndDate();
			nytdReportPeriodDto
					.setPrevReportPeriod(DateUtils.getStandardNytdFormattedPeriod(prevStartDate, prevEndDate));
		} else {
			nytdReportPeriodDto = new NytdReportPeriodDto();
		}
		return nytdReportPeriodDto;

	}

	/**
	 * Method Name: setNewPersonView Method Description: Set the # in the record
	 * that identifies this record as not viewed.
	 * 
	 * @param idNytd
	 * @param idAssignedPerson
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void setNewPersonView(Long idNytd, Long idAssignedPerson) {
		Query stmtSetNewPerson = (Query) sessionFactory.getCurrentSession().createSQLQuery(nytdSetNewPersonViewSql)
				.setParameter("idNytd", idNytd).setParameter("idAssignedPerson", idAssignedPerson);
		stmtSetNewPerson.executeUpdate();
	}

	/**
	 * Method Name: saveNytdYouthOutcomeReportingStatus Method Description: Save
	 * the Reporting Status for this youth record.
	 * 
	 * @param idNytd
	 * @param reportingStatus
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void saveNytdYouthOutcomeReportingStatus(Long idNytd, String reportingStatus) {
		// Get record to save
		NytdSurveyHeader recordToSave = (NytdSurveyHeader) sessionFactory.getCurrentSession()
				.get(NytdSurveyHeader.class, idNytd);
		// Update reporting status and save to Entity
		recordToSave.setCdOutcomeReportingStatus(reportingStatus);
		recordToSave.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(recordToSave);
	}

	/**
	 * Method Name: saveSurveyAppAuthInfo Method Description: This method saves
	 * Authentication Info record into impact_rqst_nytd@nytd table.
	 * 
	 * @param idRegPersonNytd
	 * @param idStaff
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void saveSurveyAppAuthInfo(Long idStaff) {
		// Input token is in Full ISO format
		String txtToken = DateUtils.fullISODateTimeFormat(new Date());
		Query stmtSurveyAppAuthInfo = (Query) sessionFactory.getCurrentSession()
				.createSQLQuery(nytdSaveSurveyAppAuthInfo).setParameter("idStaff", idStaff)
				.setParameter("token", txtToken);
		stmtSurveyAppAuthInfo.executeUpdate();
	}

	/**
	 * Method Name: retrieveNytdYouthHistory Method Description: Get the List of
	 * NYTD Youth History records.
	 * 
	 * @param idStage
	 * @return NytdYouthValueDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public NytdYouthHistoryDto retrieveNytdYouthHistory(Long idStage) {
		NytdYouthHistoryDto nytdYouthValueDto = new NytdYouthHistoryDto();
		// Query Result that will be mapped to response DTO
		List<NytdYouthHistoryResultDto> queryResult = null;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(nytdRetrieveNytdYouthHistory)
				.addScalar("surveyHeaderId", StandardBasicTypes.LONG).addScalar("personId", StandardBasicTypes.LONG)
				.addScalar("stageNm", StandardBasicTypes.STRING).addScalar("stageId", StandardBasicTypes.STRING)
				.addScalar("indFollowupDetermined", StandardBasicTypes.STRING)
				.addScalar("type", StandardBasicTypes.STRING).addScalar("status", StandardBasicTypes.STRING)
				.addScalar("surveyDueDt", StandardBasicTypes.DATE)
				.addScalar("surveyCompleteDt", StandardBasicTypes.DATE)
				.addScalar("participation", StandardBasicTypes.STRING)
				.addScalar("surveyStatusCd", StandardBasicTypes.STRING)
				.addScalar("outcomeReportStatusCd", StandardBasicTypes.STRING)
				.addScalar("reportStartDt", StandardBasicTypes.DATE).addScalar("reportEndDt", StandardBasicTypes.DATE)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(NytdYouthHistoryResultDto.class));
		queryResult = (List<NytdYouthHistoryResultDto>) query.list();

		if (!ObjectUtils.isEmpty(queryResult) && 0 < queryResult.size()) {

			// Map result values to response DTo
			nytdYouthValueDto.setStageID(queryResult.get(0).getStageId());
			nytdYouthValueDto.setStageName(queryResult.get(0).getStageNm());

			// Populate Survey History List
			for (NytdYouthHistoryResultDto currentItem : queryResult) {
				SurveyHistoryDto surveyHistoryDto = new SurveyHistoryDto();
				surveyHistoryDto.setIdNytdHeader(currentItem.getSurveyHeaderId());
				surveyHistoryDto.setSurveyType(currentItem.getType());
				surveyHistoryDto.setSurveyStatus(currentItem.getStatus());
				surveyHistoryDto.setCdSurveyStatus(currentItem.getSurveyStatusCd());
				surveyHistoryDto.setDtSurveyDue(currentItem.getSurveyDueDt());
				surveyHistoryDto.setDtSurveyCompletion(currentItem.getSurveyCompleteDt());
				surveyHistoryDto.setDtPeriodStart(currentItem.getReportStartDt());
				surveyHistoryDto.setDtPeriodEnd(currentItem.getReportEndDt());
				surveyHistoryDto.setOutcomeReportingStatus(currentItem.getOutcomeReportStatusCd());
				surveyHistoryDto.setOutcomeReportingStatusStr(currentItem.getParticipation());

				// Based on query result, assign the surveys dates accordingly
				if (!ObjectUtils.isEmpty(surveyHistoryDto)) {
					Date baseCompletionDate = surveyHistoryDto.getDtSurveyCompletion();
					if (!ObjectUtils.isEmpty(baseCompletionDate)) {
						surveyHistoryDto.setSurveyCompletionDate(getStandardNytdFormattedDate(baseCompletionDate));
					}
					Date baseDueDate = surveyHistoryDto.getDtSurveyDue();
					if (!ObjectUtils.isEmpty(baseDueDate)) {
						surveyHistoryDto.setSurveyDueDate(getStandardNytdFormattedDate(baseDueDate));
					}

					Date baseStartPeriodDate = surveyHistoryDto.getDtPeriodStart();
					Date baseEndPeriodDate = surveyHistoryDto.getDtPeriodEnd();
					if (!ObjectUtils.isEmpty(baseStartPeriodDate) && !ObjectUtils.isEmpty(baseEndPeriodDate)) {
						surveyHistoryDto.setNytdReportingPeriod(
								DateUtils.getStandardNytdFormattedPeriod(baseStartPeriodDate, baseEndPeriodDate));
					}
				}
				String surveyType = surveyHistoryDto.getSurveyType();
				// Set the survey history DTO into correct survey type bucket
				if (ServiceConstants.NYTD_YOUTH_BASE.equalsIgnoreCase(surveyType)) {
					nytdYouthValueDto.setBase(surveyHistoryDto);
					nytdYouthValueDto.setFollowUpDetermined(currentItem.getIndFollowupDetermined());
				} else if (ServiceConstants.NYTD_YOUTH_19.equalsIgnoreCase(surveyType)) {
					nytdYouthValueDto.setFollowup19(surveyHistoryDto);
				} else if (ServiceConstants.NYTD_YOUTH_21.equalsIgnoreCase(surveyType)) {
					nytdYouthValueDto.setFollowup21(surveyHistoryDto);
				}
			}
			// Get designated contact information
			List<DesignatedContactDto> contactsList = getDesignatedContactForStage(idStage);
			if (!ObjectUtils.isEmpty(contactsList)) {
				nytdYouthValueDto.setDesignatedContacts(contactsList);
			}
		}
		return nytdYouthValueDto;
	}

	/**
	 * Method Name: retrieveNytdYouthContactInfo Method Description:Get
	 * YouthContactInfoValueBean containing the info related to NYTD youth and
	 * youth designated contact primary and other current information.
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return YouthContactInfoValueDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NytdYouthContactInfoDto retrieveNytdYouthContactInfo(Long idStage, Long idPerson) {

		NytdYouthContactInfoDto youthContactInfoValueDto = new NytdYouthContactInfoDto();
		// Get Youth Name and DoB from person details
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class)
				.setProjection(Projections.projectionList().add(Projections.property("nmPersonFull"), "nmPersonFull")
						.add(Projections.property("dtPersonBirth"), "dtPersonBirth"));
		criteria.add(Restrictions.eq("idPerson", idPerson))
				.setResultTransformer(Transformers.aliasToBean(Person.class));
		Person person = (Person) criteria.uniqueResult();

		// Assign to response DTO
		if (!ObjectUtils.isEmpty(person)) {
			youthContactInfoValueDto.setYouthName(person.getNmPersonFull());
			youthContactInfoValueDto.setYouthDOB(getStandardNytdFormattedDate(person.getDtPersonBirth()));
		}
		// Get Designated primary contact from DB
		NytdYouthContactInfoDto desigContact = new NytdYouthContactInfoDto();
		Query desigContactInfoQuery = (Query) sessionFactory.getCurrentSession()
				.createSQLQuery(retrieveNytdYouthContact).addScalar("nytdDesigContactName", StandardBasicTypes.STRING)
				.addScalar("nytdDesigContactEmail", StandardBasicTypes.STRING)
				.addScalar("nytdDesigContactPhone", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(NytdYouthContactInfoDto.class));
		desigContact = (NytdYouthContactInfoDto) desigContactInfoQuery.uniqueResult();

		// Assign Designated Contact info to response DTO
		if (!ObjectUtils.isEmpty(desigContact)) {
			if (!ObjectUtils.isEmpty(desigContact.getNytdDesigContactName())) {
				youthContactInfoValueDto.setNytdDesigContactName(desigContact.getNytdDesigContactName());
			}
			if (!ObjectUtils.isEmpty(desigContact.getNytdDesigContactEmail())) {
				youthContactInfoValueDto.setNytdDesigContactEmail(desigContact.getNytdDesigContactEmail());
			}
			if (!ObjectUtils.isEmpty(desigContact.getNytdDesigContactPhone())) {
				youthContactInfoValueDto.setNytdDesigContactPhone(desigContact.getNytdDesigContactPhone());
			}
		}
		// Retrieve all e-mails
		Query personEmailQuery = (Query) sessionFactory.getCurrentSession().createSQLQuery(youthCurrentEmailList)
				.addScalar("txtEmail", StandardBasicTypes.STRING).addScalar("cdType", StandardBasicTypes.STRING)
				.addScalar("indPrimary", StandardBasicTypes.STRING).addScalar("dtStart", StandardBasicTypes.DATE)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonEmailDto.class));

		List<PersonEmailDto> currentEmailList = (List<PersonEmailDto>) personEmailQuery.list();

		if (!ObjectUtils.isEmpty(currentEmailList)) {
			youthContactInfoValueDto.setCurrentEmailList(currentEmailList);
		}
		// Phone number list
		Query phoneQuery = (Query) sessionFactory.getCurrentSession().createSQLQuery(youthCurrentPhoneList)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.addScalar("dtPersonPhoneStart", StandardBasicTypes.DATE)
				.addScalar("cdPersonPhoneType", StandardBasicTypes.STRING)
				.addScalar("indPersonPhonePrimary", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PhoneInfoDto.class));

		List<PhoneInfoDto> phoneInfoDtoList = phoneQuery.list();
		if (!ObjectUtils.isEmpty(phoneInfoDtoList)) {
			youthContactInfoValueDto.setCurrentPhoneList(phoneInfoDtoList);
		}
		// Address list
		Query addressQuery = (Query) sessionFactory.getCurrentSession().createSQLQuery(youthCurrentAddressList)
				.addScalar("streetLn1", StandardBasicTypes.STRING).addScalar("streetLn2", StandardBasicTypes.STRING)
				.addScalar("city", StandardBasicTypes.STRING).addScalar("stateName", StandardBasicTypes.STRING)
				.addScalar("zip", StandardBasicTypes.STRING).addScalar("startDate", StandardBasicTypes.DATE)
				.addScalar("isPrimary", StandardBasicTypes.STRING).addScalar("addressType", StandardBasicTypes.STRING)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(AddressValueDto.class));
		List<AddressValueDto> addressValueDtos = addressQuery.list();
		if (!ObjectUtils.isEmpty(addressValueDtos)) {
			youthContactInfoValueDto.setCurrentAddressList(addressValueDtos);
		}
		return youthContactInfoValueDto;
	}

	/**
	 * Method Name: searchNytdPopulation Method Description: Get the NYTD
	 * Population based on Input Criteria
	 * 
	 * @param nytdSearchValueDto
	 * @return nytdSearchReturnList
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<NytdSearchResultDto> searchNytdPopulation(NytdSearchValueDto nytdSearchValueDto) {
		// Call method to get search query to execute
		String sql = getSearchSQL(nytdSearchValueDto);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("nytdId", StandardBasicTypes.STRING).addScalar("personId", StandardBasicTypes.STRING)
				.addScalar("reportPeriod", StandardBasicTypes.STRING).addScalar("caseId", StandardBasicTypes.STRING)
				.addScalar("stageId", StandardBasicTypes.STRING).addScalar("stageName", StandardBasicTypes.STRING)
				.addScalar("newIndicator", StandardBasicTypes.STRING)
				.addScalar("baselineIndicator", StandardBasicTypes.STRING)
				.addScalar("followup19Indicator", StandardBasicTypes.STRING)
				.addScalar("followup21Indicator", StandardBasicTypes.STRING)
				.addScalar("servedIndicator", StandardBasicTypes.STRING)
				.addScalar("surveyDueDate", StandardBasicTypes.STRING)
				.addScalar("missingDOB", StandardBasicTypes.STRING).addScalar("missingSex", StandardBasicTypes.STRING)
				.addScalar("unknownSex", StandardBasicTypes.STRING).addScalar("missingRace", StandardBasicTypes.STRING)
				.addScalar("missingEthnicity", StandardBasicTypes.STRING)
				.addScalar("oldEducation", StandardBasicTypes.STRING)
				.addScalar("missingSpecialEducation", StandardBasicTypes.STRING)
				.addScalar("missingEducation", StandardBasicTypes.STRING)
				.addScalar("missingContact", StandardBasicTypes.STRING)
				.addScalar("missingPhone", StandardBasicTypes.STRING)
				.addScalar("missingAddress", StandardBasicTypes.STRING)
				.addScalar("missingEmail", StandardBasicTypes.STRING)
				.addScalar("missingSurveyOutcome", StandardBasicTypes.STRING)
				.addScalar("lastNytdReportableService", StandardBasicTypes.STRING)
				.addScalar("lastServiceDate", StandardBasicTypes.STRING)
				.addScalar("lastActivity", StandardBasicTypes.STRING)
				.addScalar("legalCounty", StandardBasicTypes.STRING)
				.addScalar("assignedStaffName", StandardBasicTypes.STRING)
				.addScalar("assignedStaffRegion", StandardBasicTypes.STRING)
				.addScalar("assignedStaffUnit", StandardBasicTypes.STRING)
				.addScalar("newPersonIndicator", StandardBasicTypes.STRING)
				.addScalar("createdOn", StandardBasicTypes.TIMESTAMP).addScalar("createdBy", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdateCr", StandardBasicTypes.DATE).addScalar("updatedBy", StandardBasicTypes.STRING)
				.addScalar("surveyStatus", StandardBasicTypes.STRING)
				.addScalar("surveyDueDate", StandardBasicTypes.STRING)
				.addScalar("surveyCompletionDate", StandardBasicTypes.STRING)
				.addScalar("nytdHeaderId", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(NytdSearchResultDto.class));

		List<NytdSearchResultDto> nytdSearchResultList = query.list();
		if (!ObjectUtils.isEmpty(nytdSearchResultList)) {
			for (NytdSearchResultDto currentResult : nytdSearchResultList) {
				// Populate fields from Request
				currentResult.setIncludeServedPopulation(nytdSearchValueDto.isIncludeServedPopulation());
				currentResult.setIncludeSurveyPopulation(nytdSearchValueDto.isIncludeSurveyPopulation());

				// For each result construct calculated fields
				if (nytdSearchValueDto.isIncludeServedPopulation() && nytdSearchValueDto.isIncludeSurveyPopulation()) {
					currentResult.setIncludeBoth(true);
				}
				// Now, call private method to populate additional calculated
				// properties
				setResultsetValues(currentResult);
			}
		}

		return nytdSearchResultList;
	}

	/**
	 * Method Name: getDesignatedContactForStage Method Description:Get List of
	 * DesignatedContactDto containing the info related designated contact
	 * primary and other current information.
	 * 
	 * @param idStage
	 * @return List of DesignatedContactDto
	 */
	@SuppressWarnings("unchecked")
	private List<DesignatedContactDto> getDesignatedContactForStage(long idStage) {
		List<DesignatedContactDto> contactList = null;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(nytdGetDesignatedContacts)
				.addScalar("idContactPerson", StandardBasicTypes.LONG)
				.addScalar("contactFullName", StandardBasicTypes.STRING)
				.addScalar("primaryContactIndicator", StandardBasicTypes.STRING)
				.addScalar("contactPhone", StandardBasicTypes.STRING)
				.addScalar("contactEmail", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(DesignatedContactDto.class));
		contactList = (List<DesignatedContactDto>) query.list();
		return contactList;
	}

	/**
	 * This is a helper method used to get the standard format of a full date
	 * string for all Nytd use. The format is MM/dd/yyyy
	 * 
	 * @param Date
	 *            - The date to format into the standarad format.
	 * @return String - The formatted String value.
	 */
	private String getStandardNytdFormattedDate(Date date) {
		String formattedDate = ServiceConstants.EMPTY_STRING;
		if (!ObjectUtils.isEmpty(date)) {
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			formattedDate = formatter.format(date);
		}
		return formattedDate;
	}

	/**
	 * This is a helper method used to get the date format population search
	 * result Format is dd/MM/yyyy
	 * 
	 * @param Date
	 *            - The date to format into the standard format.
	 * @return String - The formatted String value.
	 */
	private String getNytdSearchResultFormattedDate(String date) {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		String formattedDate = ServiceConstants.EMPTY_STRING;
		if (!ObjectUtils.isEmpty(date)) {
			formattedDate = formatter.format(DateUtils.stringDate(date));
		}
		return formattedDate;
	}

	/**
	 * This method returns the SQL for retrieving the list of NYTD Population
	 * 
	 * @param resourceSearchDB
	 * @param isAddResource
	 * 
	 * @return String
	 */
	private String getSearchSQL(NytdSearchValueDto nytdSearchDB) {
		// Build query string for Population Search
		StringBuilder sql = new StringBuilder();
		StringBuilder SELECT_FOR_NYTD_LIST = new StringBuilder(searchNytdPopulation);
		// Check for the population flag if served population included append
		// below query
		if (nytdSearchDB.isIncludeServedPopulation()) {
			SELECT_FOR_NYTD_LIST.append(ServiceConstants.PRE_SELECT_FOR_NYTD_LIST_EDU_SQL);
		} else {
			SELECT_FOR_NYTD_LIST.append(ServiceConstants.PRE_SELECT_FOR_NYTD_LIST_NO_EDU_SQL);
		}
		// Check for the population flag if survey population included append
		// below query
		if (nytdSearchDB.isIncludeSurveyPopulation()) {
			SELECT_FOR_NYTD_LIST.append(ServiceConstants.PRE_SELECT_FOR_NYTD_LIST_OUTCOME_SQL);
		} else {
			SELECT_FOR_NYTD_LIST.append(ServiceConstants.PRE_SELECT_FOR_NYTD_LIST_NO_OUTCOME_SQL);
		}
		sql.append(SELECT_FOR_NYTD_LIST.toString());
		// Check for if the query has to be in specific order.
		if (!ObjectUtils.isEmpty(nytdSearchDB.getResultDetails())
				&& !ObjectUtils.isEmpty(nytdSearchDB.getResultDetails().getOrderBy())
				&& ServiceConstants.SORT_BY_DATA.equals(nytdSearchDB.getResultDetails().getOrderBy())) {
			sql.append(ServiceConstants.ORDER_BY_DATA_CLAUSE_FOR_SELECT);
		} else if (!ObjectUtils.isEmpty(nytdSearchDB.getResultDetails())
				&& !ObjectUtils.isEmpty(nytdSearchDB.getResultDetails().getOrderBy())
				&& ServiceConstants.SORT_BY_INFO.equals(nytdSearchDB.getResultDetails().getOrderBy())) {
			sql.append(ServiceConstants.ORDER_BY_INFO_CLAUSE_FOR_SELECT);
		}
		boolean stateWide = ServiceConstants.CREGIONS_99.equals(nytdSearchDB.getServiceRegion());
		// Check the Service Region if it is state wide then append the below
		// query else go with the another region query
		if (stateWide) {
			sql.append(ServiceConstants.TABLE_FROM_NYTD_LIST_SQL);

		} else {
			sql.append(ServiceConstants.TABLE_FROM_NYTD_LIST_REGION_SQL);
		}
		// Add the join with the NYTD_HEADER table.
		sql.append(ServiceConstants.NYTD_HEADER_TABLE);
		sql.append(nytdSearchDB.getIdReportPeriod());
		if (stateWide) {
			sql.append(ServiceConstants.NYTD_HEADER);
		} else {
			sql.append(ServiceConstants.NYTD_HEADER1);
			sql.append(new Integer(nytdSearchDB.getServiceRegion()));
			sql.append(ServiceConstants.NYTD_HEADER2);
		}
		boolean assignedToMe = nytdSearchDB.isIncludeAssignedToMe();
		boolean legalStatus = nytdSearchDB.isIncludeLegalStatus();
		boolean both = nytdSearchDB.isIncludeBoth();
		// Check if the user selected both or not
		if (!both) {
			if (assignedToMe) {
				sql.append(ServiceConstants.NPL_ID_PERSON);
				sql.append(nytdSearchDB.getPersonId());
				sql.append(ServiceConstants.CFP_CLOSE_BRACE);
			} else if (legalStatus && !stateWide) {
				sql.append(ServiceConstants.NPL_ID_PERSON1);
			} else if (legalStatus && stateWide) {
				sql.append(ServiceConstants.NPL_ID_PERSON2);
			}
		}
		// Check if the survey population indicator is true
		if (nytdSearchDB.isIncludeSurveyPopulation()) {
			sql.append(ServiceConstants.SURVEY1);
		}
		// Check if the served population indicator is true
		if (nytdSearchDB.isIncludeServedPopulation()) {
			sql.append(ServiceConstants.SURVEY2);
		}
		// Check if the urgent is included
		if (nytdSearchDB.isIncludeUrgent()) {
			sql.append(ServiceConstants.AND_OPENBRACKET);
			sql.append(ServiceConstants.INDICATORS_MISSING);
			if (nytdSearchDB.isIncludeSurveyPopulation()) {
				sql.append(ServiceConstants.YOUTH_SURVEY);
			}
			if (nytdSearchDB.isIncludeServedPopulation()) {
				sql.append(ServiceConstants.SERVED_POPULATION);
			}
			sql.append(ServiceConstants.CFP_CLOSE_BRACE);
		}
		// Check if the result set is not null to check the order by
		if (!ObjectUtils.isEmpty(nytdSearchDB.getResultDetails())
				&& nytdSearchDB.getResultDetails().getOrderBy() != null) {
			sql.append(ServiceConstants.ORDER_BY1);
			if (nytdSearchDB.getResultDetails().getOrderBy() != null) {
				String orderByString = nytdSearchDB.getResultDetails().getOrderBy();
				if (ServiceConstants.SORT_BY_INFO.equals(orderByString)
						|| ServiceConstants.SORT_BY_DATA.equals(orderByString)) {
					sql.append(ServiceConstants.MISSED_DATA);

				} else {
					sql.append(orderByString);
				}
			}
		} else {
			sql.append(ServiceConstants.ORDER_BY_NPL);

		}
		return sql.toString();
	}

	/**
	 * This Helper method updates search result
	 * 
	 * @param nytdSearchResultDto
	 * @param result
	 **/
	private void setResultsetValues(NytdSearchResultDto nytdSearchResultDto) {
		// Set Type based on IncludeBoth result from query
		if (nytdSearchResultDto.isIncludeBoth()) {
			nytdSearchResultDto.setType(ServiceConstants.TYPE_BOTH);
		} else if (nytdSearchResultDto.isIncludeServedPopulation()) {
			nytdSearchResultDto.setType(ServiceConstants.TYPE_SERVED);
		}

		if (nytdSearchResultDto.isIncludeBoth()) {
			nytdSearchResultDto.setType(ServiceConstants.TYPE_BOTH);
		} else if (nytdSearchResultDto.isIncludeServedPopulation()) {
			nytdSearchResultDto.setType(ServiceConstants.TYPE_SERVED);
		} else if (nytdSearchResultDto.isIncludeSurveyPopulation()) {
			nytdSearchResultDto.setServedIndicator(null);
		}

		// Format Survey Date string for Display when it is survey population
		String formattedDate;
		if (isSurveyPopulation(nytdSearchResultDto)) {
			formattedDate = getNytdSearchResultFormattedDate(nytdSearchResultDto.getSurveyDueDate());
			if (!ObjectUtils.isEmpty(formattedDate)) {
				nytdSearchResultDto.setSurveyDueDate(formattedDate);
			}
		} else {
			if (!ObjectUtils.isEmpty(nytdSearchResultDto.getSurveyDueDate())) {
				formattedDate = getNytdSearchResultFormattedDate(nytdSearchResultDto.getSurveyDueDate());
				if (!ObjectUtils.isEmpty(formattedDate)) {
					nytdSearchResultDto.setSurveyDueDate(formattedDate);
				}
			}
		}

		// Format the due date string
		if (!ObjectUtils.isEmpty(nytdSearchResultDto.getSurveyDueDate())) {
			formattedDate = getNytdSearchResultFormattedDate(nytdSearchResultDto.getSurveyCompletionDate());
			if (!ObjectUtils.isEmpty(formattedDate)) {
				nytdSearchResultDto.setSurveyCompletionDate(formattedDate);
			}
		}

		// Format Last Service Date string
		if (!ObjectUtils.isEmpty(nytdSearchResultDto.getLastServiceDate())) {
			String lastServiceDt = getNytdSearchResultFormattedDate(nytdSearchResultDto.getLastServiceDate());
			if (!ObjectUtils.isEmpty(lastServiceDt)) {
				nytdSearchResultDto.setLastServiceDate(lastServiceDt);
			}
		}

		//
		if (!ObjectUtils.isEmpty(nytdSearchResultDto.getLastActivity())) {
			String lastActivityDt = getNytdSearchResultFormattedDate(nytdSearchResultDto.getLastActivity());
			Date dtActivityDt = DateUtils.stringDate(nytdSearchResultDto.getLastActivity());
			if (!ObjectUtils.isEmpty(lastActivityDt)) {
				nytdSearchResultDto.setLastActivity(lastActivityDt);
				double days = DateUtils.daysDifference(new Date(), dtActivityDt);
				if (days > ServiceConstants.NYTD_ACTIVITY_DATE_THRESHOLD) {
					nytdSearchResultDto.setBoldLastActivityDate(ServiceConstants.Y);
				}
			}
		}
	}

	/**
	 * This Helper method checks if survey needs to be populated
	 * 
	 * @param nytdSearchResultDto
	 * 
	 * @return boolean
	 **/
	private boolean isSurveyPopulation(NytdSearchResultDto nytdSearchResultDto) {
		boolean indPopulateSurvey = false;
		if (ServiceConstants.Y.equals(nytdSearchResultDto.getBaselineIndicator())
				|| ServiceConstants.Y.equals(nytdSearchResultDto.getFollowup19Indicator())
				|| ServiceConstants.Y.equals(nytdSearchResultDto.getFollowup21Indicator())) {
			indPopulateSurvey = true;
		}
		return indPopulateSurvey;
	}
}
