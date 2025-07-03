package us.tx.state.dfps.service.familytree.daoimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.OnlineParameters;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonRelation;
import us.tx.state.dfps.common.domain.PersonRelationHistory;
import us.tx.state.dfps.common.domain.PersonRelationRelintDef;
import us.tx.state.dfps.common.domain.PersonRelationSugDef;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.phoneticsearch.IIRHelper.StringHelper;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FamilyTreeRelationsReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FTFamilyTreeUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationBean;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familyTree.bean.PersonRelIntDto;
import us.tx.state.dfps.service.familyTree.bean.PersonRelSuggestionDto;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<implement
 * class for FTRelationshipDao> Jul 23, 2017- 11:23:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FTRelationshipDaoImpl implements FTRelationshipDao {

	@Value("${FTRelationshipDaoImpl.relationAmongTwoPerson}")
	private String relationAmongTwoPerson;

	@Value("${FTRelationshipDaoImpl.fetchContextPersons}")
	private String fetchContextPersons;

	@Value("${FTRelationshipDaoImpl.sqlFetchContextPersonsClosedcaseorstage}")
	private String sqlFetchContextPersonsClosedcaseorstage;

	@Value("${FTRelationshipDaoImpl.selectPeopleClosedAtGivenDate}")
	private String selectPeopleClosedAtGivenDate;

	@Value("${FTRelSuggestionsDaoImpl.selectPersonDetails}")
	private String selectPersonDetails;

	@Value("${FTRelSuggestionsDaoImpl.selectPersonListFromStage}")
	private String selectPersonListFromStage;

	@Value("${FTRelSuggestionsDaoImpl.selectRelationshipsAmongPersonList}")
	private String selectRelationshipsAmongPersonList;

	@Value("${FTRelationshipDaoImpl.selectRelatedOpenCases}")
	private String selectRelatedOpenCases;

	@Value("${FTRelationshipDaoImpl.selectAllDirectRelationships}")
	private String selectAllDirectRelationships;

	@Value("${FTRelationshipDaoImpl.selectSensitveCasePersons}")
	private String selectSensitveCasePersons;

	@Value("${FTRelationshipDaoImpl.selectStaffRecordsForSensitiveFitlering}")
	private String selectStaffRecordsForSensitiveFitlering;

	@Value("${FTRelationsDetailDaoImpl.fetchAllDirectRelationship}")
	private String fetchAllDirectRelationship;

	@Value("${FTRelationsDetailDaoImpl.existingRelationClause}")
	private String existingRelationClause;

	@Value("${FTRelationshipSearchDaoImpl.selectRelAmongPersons}")
	private String selectRelAmongPersons;

	@Value("${FTRelationshipSearchDao.selectAllDirectRelForPersons}")
	private String selectAllDirectRelForPersons;

	@Value("${FTRelationshipDaoImpl.selectPCSPAmongPerons}")
	private String selectPCSPAmongPerons;

	@Value("${FTRelationsDetailDaoImpl.fetchAllDirectPersonRelationship}")
	private String fetchAllDirectPersonRelationship;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	public FTRelationshipDaoImpl() {

	}

	@Override
	public ArrayList<FTPersonRelationBean> selectRelationshipsWith2Persons(Long idPerson1, Long idPerson2) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(relationAmongTwoPerson)
				.addScalar("idPersonRelation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idRelatedPerson", StandardBasicTypes.LONG).addScalar("cdOrigin", StandardBasicTypes.STRING)
				.addScalar("cdType", StandardBasicTypes.STRING).addScalar("cdRelation", StandardBasicTypes.STRING)
				.addScalar("cdLineage", StandardBasicTypes.STRING).addScalar("cdSeparation", StandardBasicTypes.STRING)
				.addScalar("dtEffective", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtDissolution", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtEnded", StandardBasicTypes.TIMESTAMP).addScalar("dtInvalid", StandardBasicTypes.TIMESTAMP)
				.addScalar("relComments", StandardBasicTypes.STRING).addScalar("cdEndReason", StandardBasicTypes.STRING)
				.addScalar("nmLastUpdatePerson", StandardBasicTypes.STRING).setParameter(0, idPerson1)
				.setParameter(1, idPerson2).setParameter(2, idPerson2).setParameter(3, idPerson1)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationBean.class));
		return (ArrayList<FTPersonRelationBean>) query.list();
	}

	/**
	 * Method Name: selectPersonDetails Method Description: This method
	 * retrieves Person Details for the given Person List
	 * 
	 * @param personIdList
	 * @return Map<Long,PersonValueDto> @
	 */
	@Override
	public Map<Long, PersonValueDto> selectPersonDetails(List<Long> personIdList) {
		Map<Long, PersonValueDto> personDetailsMap = new HashMap<Long, PersonValueDto>();
		StringBuilder inClauseStr = new StringBuilder();
		inClauseStr.append(ServiceConstants.SQUARE_BRACKET_OPEN);
		for (int index = ServiceConstants.Zero; index < personIdList.size(); index++) {
			Long idPerson = personIdList.get(index);
			inClauseStr.append(idPerson);
			if (index != personIdList.size() - ServiceConstants.One) {
				inClauseStr.append(ServiceConstants.COMMA_SEQ);
			}
		}
		inClauseStr.append(ServiceConstants.SQUARE_BRACKET_CLOSE);
		String sqlQuery = selectPersonDetails + inClauseStr.toString();
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("sex", StandardBasicTypes.STRING)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE).addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("activeInactiveMergedStatusCode", StandardBasicTypes.STRING)
				.addScalar("ethnicGroupCode", StandardBasicTypes.STRING)
				.addScalar("firstName", StandardBasicTypes.STRING).addScalar("middleName", StandardBasicTypes.STRING)
				.addScalar("lastName", StandardBasicTypes.STRING).addScalar("fullName", StandardBasicTypes.STRING)
				.addScalar("nameSuffixCode", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));
		;
		List<PersonValueDto> personValueDtoList = sql.list();

		for (PersonValueDto personValueDto : personValueDtoList) {
			if (!ObjectUtils.isEmpty(personValueDto.getDateOfBirth())) {
				int age = DateUtils.getAge(personValueDto.getDateOfBirth(), personValueDto.getDateOfDeath());
				personValueDto.setAge(age);
			}
			personDetailsMap.put(personValueDto.getPersonId(), personValueDto);
		}
		return personDetailsMap;
	}

	/**
	 * Method Name: selectRelationshipHistory Method Description:This method
	 * fetches single row from PERSON_RELATION_HISTORY table using
	 * idPersonRelationHistory
	 * 
	 * @param idPersonRelationHistory
	 * @return ftPersonRelationBean
	 */
	@Override
	public FTPersonRelationBean selectRelationshipHistory(Long idPersonRelationHistory) {
		PersonRelationHistory personRelationHistory = (PersonRelationHistory) sessionFactory.getCurrentSession()
				.get(PersonRelationHistory.class, idPersonRelationHistory);
		FTPersonRelationBean ftPersonRelationBean = new FTPersonRelationBean();
		BeanUtils.copyProperties(personRelationHistory, ftPersonRelationBean);
		return ftPersonRelationBean;
	}

	/**
	 * Method Name: selectRelationship Method Description: This method fetches
	 * single row from PERSON_RELATION table using idRelationship
	 * 
	 * @param idPersonRelation-
	 *            The unique identifier to get the record from the
	 *            PERSON_RELATION table.
	 * @return ftPersonRelationBean - The dto contains values from a single row
	 *         from the PERSON_RELATION table for the input passed.
	 */
	@Override
	public FTPersonRelationBean selectRelationship(Long idPersonRelation) {
		PersonRelation personRelation = (PersonRelation) sessionFactory.getCurrentSession().get(PersonRelation.class,
				idPersonRelation);
		FTPersonRelationBean ftPersonRelationBean = new FTPersonRelationBean();
		BeanUtils.copyProperties(personRelation, ftPersonRelationBean);
		if (!ObjectUtils.isEmpty(personRelation.getDtInvalid())) {
			ftPersonRelationBean.setInvalidIndicator(ServiceConstants.Y);
		} else {
			ftPersonRelationBean.setInvalidIndicator(ServiceConstants.N);
		}
		if (!ObjectUtils.isEmpty(personRelation.getTxtRelComments())) {
			ftPersonRelationBean.setRelComments(personRelation.getTxtRelComments());
		}
		ftPersonRelationBean.setIdPerson(personRelation.getPersonByIdPerson().getIdPerson());
		ftPersonRelationBean.setIdRelatedPerson(personRelation.getPersonByIdRelatedPerson().getIdPerson());
		return ftPersonRelationBean;
	}

	/**
	 * Method Name: findRelatedOpenCases Method Description:This method finds
	 * all the open cases where the relationship is being used. Meaning where
	 * both idPerson and idRealtedPerson are in Stage Person Link.
	 * 
	 * @param idPerson
	 *            - The context person in the relationship
	 * @param idRelatedPerson
	 *            - The associated person in the relatioship.
	 * @return List<Long> - The list of case ids in which the relationship
	 *         between the primary person and the secondary person exists.
	 */
	@Override
	public List<Long> findRelatedOpenCases(Long idPerson, Long idRelatedPerson) {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectRelatedOpenCases);
		query.setParameter("idPerson", idPerson);
		query.setParameter("idRelatedPerson", idRelatedPerson);
		return query.list();
	}

	/**
	 * Method Name: selectContextPersons Method Description:This method
	 * retrieves all the persons from STAGE_PERSON_LINK table related to a Stage
	 * or a Case.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return List<PersonValueDto>
	 */
	@Override
	public List<PersonValueDto> selectContextPersons(Long idCase, Long idStage) {
		List<PersonValueDto> personList = new ArrayList<>();
		String sql = fetchContextPersons;
		String caseStageHolder = (ObjectUtils.isEmpty(idStage) || (!ObjectUtils.isEmpty(idStage) && idStage == 0))
				? " SPL.ID_CASE = " : " SPL.ID_STAGE = ";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(sql);
		stringBuilder.append(caseStageHolder);
		stringBuilder.append(":id ORDER BY P.NM_PERSON_FULL");
		sql = stringBuilder.toString();

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		Long id = (ObjectUtils.isEmpty(idStage) || (!ObjectUtils.isEmpty(idStage) && idStage == 0)) ? idCase : idStage;
		query.setParameter("id", id);
		query.addScalar("personId", StandardBasicTypes.LONG).addScalar("roleInStageCode", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indNmStage", StandardBasicTypes.STRING).addScalar("sex", StandardBasicTypes.STRING)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE).addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("activeInactiveMergedStatusCode", StandardBasicTypes.STRING)
				.addScalar("ethnicGroupCode", StandardBasicTypes.STRING)
				.addScalar("firstName", StandardBasicTypes.STRING).addScalar("middleName", StandardBasicTypes.STRING)
				.addScalar("lastName", StandardBasicTypes.STRING).addScalar("fullName", StandardBasicTypes.STRING)
				.addScalar("nameSuffixCode", StandardBasicTypes.STRING).addScalar("ageLong", StandardBasicTypes.LONG)
				.addScalar("maritalStatusCode", StandardBasicTypes.STRING);
		List<PersonValueDto> personValueDtos = query
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class)).list();
		Long idPerson = 0l;
		// Add to the person list only if the person is not already present in
		// the list.
		for (PersonValueDto person : personValueDtos) {

			if (idPerson != person.getPersonId()) {
				personList.add(person);
			}
			idPerson = person.getPersonId();
		}
		;
		/*
		 * Iterating over the person list to set the full name as full name
		 * followed by suffix.
		 */
		personList.forEach(personValueDto -> {
			int age = FTFamilyTreeUtil.getAge(personValueDto.getDateOfBirth(), personValueDto.getDateOfDeath());
			personValueDto.setAge(age);
			personValueDto.setFullName(formatPersonName(personValueDto.getFullName(),
					personValueDto.getNameSuffixCode(), personValueDto.getPersonId().intValue()));

		});

		return personList;
	}

	/**
	 * This creates Person Full Name String to be displayed on Family Tree
	 * Pages, Graphs and Reports.
	 * 
	 * @param fullName
	 * @param suffix
	 * @param idPerson
	 * 
	 * @return Person Name
	 */
	private String formatPersonName(String fullName, String suffix, int idPerson) {
		String personName = fullName;
		// If fullName is Null, replace it with Unknown (pid)
		if ((!StringHelper.isValid(fullName)) || "(reporter)".equals(fullName)) {
			personName = "Unknown (" + idPerson + ")";
		} else if (StringHelper.isValid(suffix)) {
			personName += " " + suffix;
		}

		return personName;
	}

	/**
	 * 
	 * Method Name: selectContextPersons Method Description:This method
	 * retrieves all the persons from STAGE_PERSON_LINK table related to a Stage
	 * or a Case when the Case/Stage is Closed.
	 * 
	 * @param idCase
	 * @param idStage
	 * @param dtClosed
	 * @return List<PersonValueDto> @
	 */
	@Override
	public List<PersonValueDto> selectContextPersons(Long idCase, Long idStage, Date dtClosed) {

		String sqlQry = ServiceConstants.EMPTY_STRING;
		String caseStageHolder = (ObjectUtils.isEmpty(idStage) || (!ObjectUtils.isEmpty(idStage) && idStage == 0))
				? " SPL.ID_CASE = " : " SPL.ID_STAGE = ";
		Long id = (ObjectUtils.isEmpty(idStage) || (!ObjectUtils.isEmpty(idStage) && idStage == 0)) ? idCase : idStage;
		caseStageHolder += id.toString();

		sqlQry = sqlFetchContextPersonsClosedcaseorstage.replaceAll(ServiceConstants.CASE_STAGE_HOLDER,
				caseStageHolder);

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlQry)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("sex", StandardBasicTypes.STRING)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE).addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("activeInactiveMergedStatusCode", StandardBasicTypes.STRING)
				.addScalar("ethnicGroupCode", StandardBasicTypes.STRING).addScalar("age", StandardBasicTypes.INTEGER)
				.addScalar("fullName", StandardBasicTypes.STRING)
				.addScalar("maritalStatusCode", StandardBasicTypes.STRING)
				.addScalar("roleInStageCode", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indNmStage", StandardBasicTypes.STRING).setParameter("dtClosed", dtClosed)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));

		return sQLQuery1.list();

	}

	/**
	 * 
	 * Method Name: selectOnlineParameterValue Method Description:This method
	 * returns the value for the the given Key Name from ONLINE_PARAMETERS
	 * table.
	 * 
	 * @param familyTreeGraph
	 * @return String @
	 */
	@Override
	public String selectOnlineParameterValue(String familyTreeGraph) {
		String txtValue = ServiceConstants.EMPTY_STRING;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OnlineParameters.class);
		criteria.add(Restrictions.eq("txtName", familyTreeGraph));
		List<OnlineParameters> onlineParametersList = criteria.list();
		for (OnlineParameters onlineParameters : onlineParametersList) {
			txtValue = onlineParameters.getTxtValue();
		}

		return txtValue;
	}

	/**
	 * 
	 * Method Name: selectCasePersonList Method Description:This method
	 * retrieves all the person Ids in the Case Person List.
	 * 
	 * @param idCase
	 * @return List<Long> @
	 */
	@Override
	public List<Long> selectCasePersonList(Long idCase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idCase", idCase));
		criteria.setProjection(Projections.distinct(Projections.property("idPerson")));
		return criteria.list();
	}

	/**
	 * 
	 * Method Name: selectStaffRecordsAmongPersonList Method Description:This
	 * method retrieves list of staff records among persons in the given person
	 * list.
	 * 
	 * @param personIdList
	 * @return List<Long> @
	 */
	@Override
	public List<Long> selectStaffRecordsAmongPersonList(List<Long> personIdList) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Employee.class);
		criteria.add(Restrictions.in("idPerson", personIdList));
		criteria.setProjection(Projections.distinct(Projections.property("idPerson")));
		return criteria.list();
	}

	/**
	 * Method Name: selectStaffRecordsForSensitiveFitlering Method
	 * Description:This method retrieves list of staff records among persons in
	 * the given person list.
	 * 
	 * @param personIdList
	 * @return List<Long> @
	 */
	@Override
	public List<Long> selectStaffRecordsForSensitiveFitlering(List<Long> personIdList) {
		List<Long> idPersonList = new ArrayList<>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectStaffRecordsForSensitiveFitlering).setParameterList("idPersonList", personIdList);
		List<BigDecimal> personList = (List<BigDecimal>) sqlQuery.list();
		for (BigDecimal bigDecimal : personList) {
			idPersonList.add(bigDecimal.longValue());
		}
		return idPersonList;
	}

	/**
	 * 
	 * Method Name: selectSensitveCasePersons Method Description:This method
	 * returns the list of persons with sensitive case access.
	 * 
	 * @param personIdList
	 * @return List<Long> @
	 */
	@Override
	public List<Long> selectSensitveCasePersons(List<Long> personIdList) {
		List<Long> sensitiveCasePersonList = new ArrayList<>();
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectSensitveCasePersons)
				.setParameterList("idPersonList", personIdList);
		List<BigDecimal> personList = new ArrayList<>();
		personList = (List<BigDecimal>) sQLQuery1.list();
		for (BigDecimal bigDecimal : personList) {
			sensitiveCasePersonList.add(bigDecimal.longValue());
		}
		return sensitiveCasePersonList;
	}

	/**
	 * Method Name: selectStagePersonList Method Description: This method
	 * retrieves all the person Ids in the Stage Person List.
	 * 
	 * @param idStage
	 * @return List<PersonValueDto>
	 */
	@Override
	public List<PersonValueDto> selectStagePersonList(Long idStage) {
		List<PersonValueDto> personValueDtoList = new ArrayList<PersonValueDto>();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.setProjection(Projections.distinct(Projections.property("idPerson")));

		List<Long> stagePersonLinkList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(stagePersonLinkList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (Long idPerson : stagePersonLinkList) {
			PersonValueDto personValueDto = new PersonValueDto();
			personValueDto.setPersonId(idPerson);
			personValueDtoList.add(personValueDto);
		}
		return personValueDtoList;
	}

	/**
	 * Method Name: selectPeopleClosedAtGivenDate Method Description:This method
	 * returns the list of persons that are closed at the time of Case/Stage
	 * Closure.
	 * 
	 * @param personIdList
	 * @param dtClosed
	 * @return List<Integer> @
	 */
	@Override
	public List<Long> selectPeopleClosedAtGivenDate(List<Long> personIdList, Date dtClosed) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPeopleClosedAtGivenDate)
				.setParameterList("idPersonList", personIdList).setParameter("dtClosed", dtClosed);
		List<BigDecimal> idPersonList = query.list();
		if (TypeConvUtil.isNullOrEmpty(idPersonList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		List<Long> listPersonId = new ArrayList<>();
		for (BigDecimal bigDecimal : idPersonList) {
			listPersonId.add(bigDecimal.longValue());
		}

		return listPersonId;
	}

	/**
	 * Method Name: selectRelationshipSugMappingData Method Description: This
	 * method loads the Person Relationship Suggestion Def Mapping Data.
	 * 
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectRelationshipSugMappingData() {
		List<FTPersonRelationDto> ftPersonRelationDtoList = new ArrayList<FTPersonRelationDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonRelationSugDef.class);
		List<PersonRelationSugDef> personRelationSugDefList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(personRelationSugDefList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("FTRelationMappingDaoImpl.selectRelationshipSugMappingData.NotFound", null, Locale.US));
		}
		for (PersonRelationSugDef personRelationSugDef : personRelationSugDefList) {
			FTPersonRelationDto ftPersonRelationDto = new FTPersonRelationDto();
			ftPersonRelationDto.setIdPersonRelationRelintDef(personRelationSugDef.getIdPersonRelationSugDef());
			ftPersonRelationDto.setDtLastUpdate(personRelationSugDef.getDtLastUpdate());
			ftPersonRelationDto.setDtCreated(personRelationSugDef.getDtCreated());
			ftPersonRelationDto.setCdNewRelation(personRelationSugDef.getCdNewRelation());
			ftPersonRelationDto.setCdExistRelation(personRelationSugDef.getCdExistRelation());
			ftPersonRelationDto.setCdResultRelation(personRelationSugDef.getCdResultRelation());
			ftPersonRelationDto.setCdResultSeparation(personRelationSugDef.getCdResultSeparation());
			ftPersonRelationDto.setIndLineageUsed(personRelationSugDef.getIndLineageUsed());
			ftPersonRelationDtoList.add(ftPersonRelationDto);
		}
		return ftPersonRelationDtoList;
	}

	/**
	 * Method Name: selectRelintRelMappingData Method Description: This method
	 * loads the Person Relationship / Relint Mapping data.
	 * 
	 * @return Map<String,FTPersonRelationDto> @
	 */
	@Override
	public Map<String, FTPersonRelationDto> selectRelintRelMappingData() {
		Map<String, FTPersonRelationDto> relRelintDefMap = new HashMap<String, FTPersonRelationDto>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonRelationRelintDef.class);
		List<PersonRelationRelintDef> personRelationRelintDefList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(personRelationRelintDefList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("FTRelationMappingDaoImpl.selectRelintRelMappingData.NotFound", null, Locale.US));
		}
		for (PersonRelationRelintDef personRelationRelintDef : personRelationRelintDefList) {
			FTPersonRelationDto ftPersonRelationDto = new FTPersonRelationDto();
			ftPersonRelationDto.setIdPersonRelationRelintDef(personRelationRelintDef.getIdPersonRelationRelintDef());
			ftPersonRelationDto.setDtLastUpdate(personRelationRelintDef.getDtLastUpdate());
			ftPersonRelationDto.setDtCreated(personRelationRelintDef.getDtCreated());
			ftPersonRelationDto.setCdRelint(personRelationRelintDef.getCdRelint());
			ftPersonRelationDto.setCdType(personRelationRelintDef.getCdType());
			ftPersonRelationDto.setCdRelation(personRelationRelintDef.getCdRelation());
			ftPersonRelationDto.setCdLineage(personRelationRelintDef.getCdLineage());
			ftPersonRelationDto.setCdSeparation(personRelationRelintDef.getCdSeparation());
			relRelintDefMap.put(ftPersonRelationDto.getCdRelint(), ftPersonRelationDto);
		}
		return relRelintDefMap;
	}

	/**
	 * Method Name: selectPersonListFromStage Method Description:This method
	 * retrieves all the Persons with in the Stage. It uses the same query as
	 * Person List Window.
	 * 
	 * @param idStage
	 * @return List<PersonValueDto>
	 */
	@Override
	public List<PersonValueDto> selectPersonListFromStage(Long idStage) {
		List<PersonValueDto> personEthnicityDtos = (List<PersonValueDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(selectPersonListFromStage).addScalar("personId", StandardBasicTypes.LONG)
				.addScalar("sex", StandardBasicTypes.STRING).addScalar("dateOfDeath", StandardBasicTypes.DATE)
				.addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("activeInactiveMergedStatusCode", StandardBasicTypes.STRING)
				.addScalar("ethnicGroupCode", StandardBasicTypes.STRING)
				.addScalar("firstName", StandardBasicTypes.STRING).addScalar("middleName", StandardBasicTypes.STRING)
				.addScalar("lastName", StandardBasicTypes.STRING).addScalar("nmSuffix", StandardBasicTypes.STRING)
				.addScalar("fullName", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("roleInStageCode", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("cdStage", ServiceConstants.STAFF_TYPE)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class)).list();
		if (TypeConvUtil.isNullOrEmpty(personEthnicityDtos)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (PersonValueDto personValueDto : personEthnicityDtos) {
			int age = FTFamilyTreeUtil.getAge(personValueDto.getDateOfBirth(), personValueDto.getDateOfDeath());
			personValueDto.setAge(age);
			String fullName = FTFamilyTreeUtil.formatPersonName(personValueDto.getFullName(),
					personValueDto.getNmSuffix(), personValueDto.getPersonId().intValue());
			personValueDto.setFullName(fullName);
		}
		return personEthnicityDtos;
	}

	/**
	 * Method Name: selectRelationshipsAmongPersonList Method Description:This
	 * method retrieves all the relationships among persons in the given person
	 * list.
	 * 
	 * @param personIdList
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectRelationshipsAmongPersonList(List<Long> personIdList) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectRelationshipsAmongPersonList)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
		query.addScalar("idPersonRelation", StandardBasicTypes.LONG);
		query.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		query.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG);
		query.addScalar("dtCreated", StandardBasicTypes.DATE);
		query.addScalar("idCreatedPerson", StandardBasicTypes.LONG);
		query.addScalar("idPerson", StandardBasicTypes.LONG);
		query.addScalar("idRelatedPerson", StandardBasicTypes.LONG);
		query.addScalar("cdOrigin", StandardBasicTypes.STRING);
		query.addScalar("cdType", StandardBasicTypes.STRING);
		query.addScalar("cdRelation", StandardBasicTypes.STRING);
		query.addScalar("cdLineage", StandardBasicTypes.STRING);
		query.addScalar("cdSeparation", StandardBasicTypes.STRING);
		query.addScalar("cdEndReason", StandardBasicTypes.STRING);
		query.addScalar("dtEffective", StandardBasicTypes.DATE);
		query.addScalar("dtDissolution", StandardBasicTypes.DATE);
		query.addScalar("dtEnded", StandardBasicTypes.DATE);
		query.addScalar("dtInvalid", StandardBasicTypes.DATE);
		query.addScalar("txtRelComments", StandardBasicTypes.STRING);
		query.setParameterList("personIdList", personIdList);
		return query.list();
	}

	/**
	 * Method Name: insertRelationship Method Description: This method inserts
	 * record into RELATIONSHIP table.
	 * 
	 * @param ftPersonRelationBean
	 * @return long @
	 */
	@Override
	public long insertRelationship(FTPersonRelationDto ftPersonRelationBean) {
		PersonRelation personRelation = new PersonRelation();
		personRelation.setIdLastUpdatePerson(ftPersonRelationBean.getIdLastUpdatePerson()); // ID_LAST_UPDATE_PERSON
		personRelation.setIdCreatedPerson(ftPersonRelationBean.getIdCreatedPerson()); // ID_CREATED_PERSON
		Person person = new Person();
		person.setIdPerson((long) ftPersonRelationBean.getIdPerson()); // ID_PERSON
		personRelation.setPersonByIdPerson(person);
		person = new Person();
		person.setIdPerson((long) ftPersonRelationBean.getIdRelatedPerson()); // ID_PERSON_RELATION
		personRelation.setPersonByIdRelatedPerson(person);
		personRelation.setCdOrigin(ftPersonRelationBean.getCdOrigin()); // CD_ORIGIN
		personRelation.setCdType(ftPersonRelationBean.getCdType()); // CD_TYPE
		personRelation.setCdRelation(ftPersonRelationBean.getCdRelation()); // CD_RELATION
		personRelation.setCdLineage(ftPersonRelationBean.getCdLineage()); // CD_LINEAGE
		personRelation.setCdSeparation(ftPersonRelationBean.getCdSeparation()); // CD_SEPARATION
		personRelation.setDtEffective(ftPersonRelationBean.getDtEffective()); // DT_EFFECTIVE
		personRelation.setDtDissolution(ftPersonRelationBean.getDtDissolution()); // DT_DISSOLUTION
		personRelation.setDtEnded(ftPersonRelationBean.getDtEnded()); // DT_ENDED
		personRelation.setDtInvalid(ftPersonRelationBean.getDtInvalid()); // DT_INVALID
		personRelation.setTxtRelComments(ftPersonRelationBean.getTxtRelComments()); // TXT_REL_COMMENTS
		personRelation.setCdEndReason(ftPersonRelationBean.getCdEndReason()); // CD_END_REASON
		personRelation.setDtCreated(new Date());
		personRelation.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(personRelation);
		// sessionFactory.getCurrentSession().flush();
		return 1;
	}

	/**
	 * 
	 * Method Name: selectAllDirectRelationships Method Description:This method
	 * fetches All Direct Relationships with the selected Context Person It
	 * fetches the relationships from snapshot table (SS_PERSON_RELATION) so
	 * that relationships can be displayed as they existed before person merge
	 * 
	 * @param idPerson
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllDirectRelationships(Long idPerson, Long idReferenceData,
			String cdActionType, String cdSnapshotType) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(selectAllDirectRelationships)
				.addScalar("idPersonRelation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idRelatedPerson", StandardBasicTypes.LONG)
				.addScalar("cdOrigin", StandardBasicTypes.STRING).addScalar("cdType", StandardBasicTypes.STRING)
				.addScalar("cdRelation", StandardBasicTypes.STRING).addScalar("cdLineage", StandardBasicTypes.STRING)
				.addScalar("cdSeparation", StandardBasicTypes.STRING).addScalar("dtEffective", StandardBasicTypes.DATE)
				.addScalar("dtDissolution", StandardBasicTypes.DATE).addScalar("dtEnded", StandardBasicTypes.DATE)
				.addScalar("dtInvalid", StandardBasicTypes.DATE).addScalar("txtRelComments", StandardBasicTypes.STRING)
				.addScalar("cdEndReason", StandardBasicTypes.STRING).setParameter("idReferenceData", idReferenceData)
				.setParameter("cdSnapshotType", cdSnapshotType).setParameter("cdActionType", cdActionType)
				.setParameter("idObject", idPerson).setParameter("idPerson", idPerson)
				.setParameter("idRelatedPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class));
		;
		return query.list();
	}

	/**
	 * Method Name: fetchAllDirectRelationships Method Description: This method
	 * is used to find all the relationship between two persons which are not
	 * ended or invalidated and excluding the current record. The values are
	 * fetched from the PERSON_RELATION, PERSON tables.
	 * 
	 * @param ftPersonRelationBean
	 * @return List<FTPersonRelationBean>
	 */
	@Override
	public List<FTPersonRelationBean> fetchAllDirectRelationships(FTPersonRelationBean ftPersonRelationBean) {
		List<FTPersonRelationBean> existingRelationsList = new ArrayList<>();
		String sqlQuery = fetchAllDirectRelationship;
		if (!ObjectUtils.isEmpty(ftPersonRelationBean.getIdPersonRelation())
				&& 0l != ftPersonRelationBean.getIdPersonRelation()) {
			sqlQuery = sqlQuery + " " + existingRelationClause;
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
				.addScalar("idPersonRelation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idRelatedPerson", StandardBasicTypes.LONG).addScalar("cdOrigin", StandardBasicTypes.STRING)
				.addScalar("cdType", StandardBasicTypes.STRING).addScalar("cdRelation", StandardBasicTypes.STRING)
				.addScalar("cdLineage", StandardBasicTypes.STRING).addScalar("cdSeparation", StandardBasicTypes.STRING)
				.addScalar("dtEffective", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtDissolution", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtEnded", StandardBasicTypes.TIMESTAMP).addScalar("dtInvalid", StandardBasicTypes.TIMESTAMP)
				.addScalar("relComments", StandardBasicTypes.STRING).addScalar("cdEndReason", StandardBasicTypes.STRING)
				.addScalar("cdSexContextPerson", StandardBasicTypes.STRING)
				.addScalar("dtBirthContextPerson", StandardBasicTypes.DATE)
				.addScalar("dtDeathContextPerson", StandardBasicTypes.DATE)
				.addScalar("indDOBApproxContextPerson", StandardBasicTypes.STRING)
				.addScalar("cdContextPersonStatus", StandardBasicTypes.STRING)
				.addScalar("nmContextPerson", StandardBasicTypes.STRING)
				.addScalar("cdSexRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("dtBirthRelatedPerson", StandardBasicTypes.DATE)
				.addScalar("dtDeathRelatedPerson", StandardBasicTypes.DATE)
				.addScalar("indDOBApproxRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("cdRelatedPersonStatus", StandardBasicTypes.STRING)
				.addScalar("nmRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("nmContextSuffix", StandardBasicTypes.STRING)
				.addScalar("nmRelatedSuffix", StandardBasicTypes.STRING)
				.addScalar("nmCreatedPerson", StandardBasicTypes.STRING)
				.addScalar("nmLastUpdatePerson", StandardBasicTypes.STRING)
				.setParameter("idPerson", ftPersonRelationBean.getIdPerson())
				.setParameter("idRelatedPerson", ftPersonRelationBean.getIdRelatedPerson())
				.setParameter("cdOrigin", ftPersonRelationBean.getCdOrigin())
				.setParameter("personStatus", ftPersonRelationBean.getCdContextPersonStatus());
		/*
		 * If it is a existing relationship , then adding clause to exclude the
		 * current relation in retrieving the existing relationship list.
		 */
		if (!ObjectUtils.isEmpty(ftPersonRelationBean.getIdPersonRelation())
				&& 0l != ftPersonRelationBean.getIdPersonRelation()) {
			query.setParameter("idPersonRelation", ftPersonRelationBean.getIdPersonRelation());
		}
		query.setResultTransformer(Transformers.aliasToBean(FTPersonRelationBean.class));
		existingRelationsList = query.list();
		return existingRelationsList;
	}

	/**
	 * Method Name: insertRelationship Method Description: This method inserts
	 * record into PERSON_RELATION table.
	 * 
	 * @param ftPersonRelationBean
	 *            - The dto with the relation details to be inserted in the
	 *            table.
	 * @return Long - The unique id representing the idPersonRelation for the
	 *         newly created person.
	 */
	@Override
	public Long insertRelationship(FTPersonRelationBean ftPersonRelationBean) {
		PersonRelation personRelation = new PersonRelation();
		personRelation.setIdLastUpdatePerson(ftPersonRelationBean.getIdLastUpdatePerson());
		personRelation.setIdCreatedPerson(ftPersonRelationBean.getIdLastUpdatePerson());
		Person person = new Person();
		person.setIdPerson(ftPersonRelationBean.getIdPerson());
		personRelation.setPersonByIdPerson(person);
		person = new Person();
		person.setIdPerson(ftPersonRelationBean.getIdRelatedPerson());
		personRelation.setPersonByIdRelatedPerson(person);
		personRelation.setCdOrigin(ftPersonRelationBean.getCdOrigin());
		personRelation.setCdType(ftPersonRelationBean.getCdType());
		personRelation.setCdRelation(ftPersonRelationBean.getCdRelation());
		personRelation.setCdLineage(ftPersonRelationBean.getCdLineage());
		personRelation.setCdSeparation(ftPersonRelationBean.getCdSeparation());
		personRelation.setDtEffective(ftPersonRelationBean.getDtEffective());
		personRelation.setDtDissolution(ftPersonRelationBean.getDtDissolution());
		personRelation.setDtEnded(ftPersonRelationBean.getDtEnded());
		personRelation.setDtInvalid(ftPersonRelationBean.getDtInvalid());
		personRelation.setTxtRelComments(ftPersonRelationBean.getRelComments());
		personRelation.setCdEndReason(ftPersonRelationBean.getCdEndReason());
		personRelation.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		personRelation.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		return (long) sessionFactory.getCurrentSession().save(personRelation);
	}

	/**
	 * Method Name: updateRelationship Method Description: This method updates
	 * PERSON_RELATION table using FTPersonRelationValueBean.
	 * 
	 * @param ftPersonRelationBean-The
	 *            dto with the relation details to be updated in the table.
	 * @return Long -The unique id representing the idPersonRelation for the
	 *         updated person.
	 */
	@Override
	public Long updateRelationship(FTPersonRelationBean ftPersonRelationBean) {
		PersonRelation personRelation = (PersonRelation) sessionFactory.getCurrentSession().get(PersonRelation.class,
				ftPersonRelationBean.getIdPersonRelation());

		personRelation.setIdLastUpdatePerson(ftPersonRelationBean.getIdLastUpdatePerson());
		Person person = new Person();
		person.setIdPerson(ftPersonRelationBean.getIdPerson());
		personRelation.setPersonByIdPerson(person);
		person = new Person();
		person.setIdPerson(ftPersonRelationBean.getIdRelatedPerson());
		personRelation.setPersonByIdRelatedPerson(person);
		personRelation.setCdOrigin(ftPersonRelationBean.getCdOrigin());
		personRelation.setCdType(ftPersonRelationBean.getCdType());
		personRelation.setCdRelation(ftPersonRelationBean.getCdRelation());
		personRelation.setCdLineage(ftPersonRelationBean.getCdLineage());
		personRelation.setCdSeparation(ftPersonRelationBean.getCdSeparation());
		personRelation.setDtEffective(ftPersonRelationBean.getDtEffective());
		personRelation.setDtDissolution(ftPersonRelationBean.getDtDissolution());
		personRelation.setDtEnded(ftPersonRelationBean.getDtEnded());
		personRelation.setDtInvalid(ftPersonRelationBean.getDtInvalid());
		personRelation.setTxtRelComments(ftPersonRelationBean.getRelComments());
		personRelation.setCdEndReason(ftPersonRelationBean.getCdEndReason());

		sessionFactory.getCurrentSession().saveOrUpdate(personRelation);

		return ftPersonRelationBean.getIdPersonRelation();
	}

	/**
	 * Method Name: isRelIntMatches Method Description:The method is used to
	 * fetch from the PERSON_RELATION_RELINT_DEF table based on the column
	 * CD_RELINT.
	 * 
	 * @param ftPersonRelationBean
	 *            - The dto with the input value for retrieving the records from
	 *            the db.
	 */
	@Override
	public List<PersonRelIntDto> isRelIntMatches(FTPersonRelationBean ftPersonRelationBean) {
		List<PersonRelIntDto> personRelIntDtos = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonRelationRelintDef.class);
		criteria.add(Restrictions.eq("cdRelint", ftPersonRelationBean.getRelInt()));
		List<PersonRelationRelintDef> personRelationRelintDefs = criteria.list();
		for (PersonRelationRelintDef personRelationRelintDef : personRelationRelintDefs) {
			PersonRelIntDto personRelIntDto = new PersonRelIntDto();
			BeanUtils.copyProperties(personRelationRelintDef, personRelIntDto);
			personRelIntDtos.add(personRelIntDto);
		}
		return personRelIntDtos;
	}

	/**
	 * Method Name: getPossibleRelation Method Description:This method is used
	 * to fetch data from the PERSON_RELATION_SUG_DEF table based on the
	 * CD_NEW_RELATION and CD_EXISTING_RELATION column.
	 * 
	 * @param familyTreeRelationsReq
	 *            - The dto with the input values for the CD_NEW_RELATION and
	 *            CD_EXISTING_RELATION column.
	 * @return PersonRelSuggestionDto - The dto with details of the possible
	 *         relation match for the input parameters.
	 */
	@Override
	public PersonRelSuggestionDto getPossibleRelation(FamilyTreeRelationsReq familyTreeRelationsReq) {
		PersonRelSuggestionDto personRelSuggestionDto = new PersonRelSuggestionDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonRelationSugDef.class);
		criteria.add(Restrictions.eq("cdNewRelation", familyTreeRelationsReq.getNewCdRelation()));
		criteria.add(Restrictions.eq("cdExistRelation", familyTreeRelationsReq.getExistingCdRelation()));
		PersonRelationSugDef personRelationSugDef = (PersonRelationSugDef) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(personRelationSugDef)) {
			BeanUtils.copyProperties(personRelationSugDef, personRelSuggestionDto);
		}

		return personRelSuggestionDto;
	}

	/**
	 * Method Name: invalidateRelation Method Description:
	 * 
	 * @param ftPersonRelationBean-The
	 *            dto with the relation details to be updated in the table.
	 */
	@Override
	public void invalidateRelation(FTPersonRelationBean ftPersonRelationBean) {
		PersonRelation personRelation = (PersonRelation) sessionFactory.getCurrentSession().get(PersonRelation.class,
				ftPersonRelationBean.getIdPersonRelation());

		personRelation.setIdLastUpdatePerson(ftPersonRelationBean.getIdLastUpdatePerson());
		if (!ObjectUtils.isEmpty(ftPersonRelationBean.getDtEnded())) {
			personRelation.setDtEnded(ftPersonRelationBean.getDtEnded());
		}
		if (!ObjectUtils.isEmpty(ftPersonRelationBean.getDtInvalid())) {
			personRelation.setDtInvalid(ftPersonRelationBean.getDtInvalid());
		}
		sessionFactory.getCurrentSession().saveOrUpdate(personRelation);

	}

	/**
	 * Method Name: selectRelationsAmongPersons Method Description:Fetches
	 * RelationsAmongPerson List
	 * 
	 * @param relPersonIdList
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectRelationsAmongPersons(List<Integer> relPersonIdList) {

		List<FTPersonRelationDto> ftPersonRelationDtoList = new ArrayList<>();

		if (!TypeConvUtil.isNullOrEmpty(relPersonIdList)) {
			SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectRelAmongPersons)
					.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));

			query.addScalar("idPersonRelation", StandardBasicTypes.LONG);
			query.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
			query.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG);
			query.addScalar("dtCreated", StandardBasicTypes.DATE);
			query.addScalar("idCreatedPerson", StandardBasicTypes.LONG);
			query.addScalar("idPerson", StandardBasicTypes.LONG);
			query.addScalar("idRelatedPerson", StandardBasicTypes.LONG);
			query.addScalar("cdOrigin", StandardBasicTypes.STRING);
			query.addScalar("cdType", StandardBasicTypes.STRING);
			query.addScalar("cdRelation", StandardBasicTypes.STRING);
			query.addScalar("cdLineage", StandardBasicTypes.STRING);
			query.addScalar("cdSeparation", StandardBasicTypes.STRING);
			query.addScalar("cdEndReason", StandardBasicTypes.STRING);
			query.addScalar("dtEffective", StandardBasicTypes.DATE);
			query.addScalar("dtDissolution", StandardBasicTypes.DATE);
			query.addScalar("dtEnded", StandardBasicTypes.DATE);
			query.addScalar("dtInvalid", StandardBasicTypes.DATE);
			query.addScalar("txtRelComments", StandardBasicTypes.STRING);
			query.addScalar("nmContextPersonFull", StandardBasicTypes.STRING);
			query.addScalar("cdSexContextPerson", StandardBasicTypes.STRING);
			query.addScalar("dtBirthContextPerson", StandardBasicTypes.DATE);
			query.addScalar("dtDeathContextPerson", StandardBasicTypes.DATE);
			query.addScalar("indDOBApproxContextPerson", StandardBasicTypes.STRING);
			query.addScalar("cdSuffixContextPerson", StandardBasicTypes.STRING);
			query.addScalar("cdContextPersonStatus", StandardBasicTypes.LONG);
			query.addScalar("nmRelatedPersonFull", StandardBasicTypes.STRING);
			query.addScalar("cdSexRelatedPerson", StandardBasicTypes.STRING);
			query.addScalar("dtBirthRelatedPerson", StandardBasicTypes.DATE);
			query.addScalar("dtDeathRelatedPerson", StandardBasicTypes.DATE);
			query.addScalar("indDOBApproxRelatedPerson", StandardBasicTypes.STRING);
			query.addScalar("cdSuffixRelatedPerson", StandardBasicTypes.STRING);
			query.addScalar("cdRelatedPersonStatus", StandardBasicTypes.STRING);

			query.setParameterList("relPersonIdList", relPersonIdList);
			ftPersonRelationDtoList = query.list();
			if (!TypeConvUtil.isNullOrEmpty(ftPersonRelationDtoList)) {
				throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));

			}
		}
		return ftPersonRelationDtoList;
	}

	/**
	 * Method Name: selectAllDirectRelForPersons Method Description: This method
	 * gets All Direct Relations for Persons.
	 * 
	 * @param personIdList
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllDirectRelForPersons(List<Integer> personIdList) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectAllDirectRelForPersons)
				.addScalar("idPersonRelation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idRelatedPerson", StandardBasicTypes.LONG)
				.addScalar("cdOrigin", StandardBasicTypes.STRING).addScalar("cdType", StandardBasicTypes.STRING)
				.addScalar("cdRelation", StandardBasicTypes.STRING).addScalar("cdLineage", StandardBasicTypes.STRING)
				.addScalar("cdSeparation", StandardBasicTypes.STRING)
				.addScalar("cdEndReason", StandardBasicTypes.STRING).addScalar("dtEffective", StandardBasicTypes.DATE)
				.addScalar("dtDissolution", StandardBasicTypes.DATE).addScalar("dtEnded", StandardBasicTypes.DATE)
				.addScalar("dtInvalid", StandardBasicTypes.DATE).addScalar("txtRelComments", StandardBasicTypes.STRING)
				.addScalar("nmContextPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdSexContextPerson", StandardBasicTypes.STRING)
				.addScalar("dtBirthContextPerson", StandardBasicTypes.DATE)
				.addScalar("dtDeathContextPerson", StandardBasicTypes.DATE)
				.addScalar("indDOBApproxContextPerson", StandardBasicTypes.STRING)
				.addScalar("cdSuffixContextPerson", StandardBasicTypes.STRING)
				.addScalar("cdContextPersonStatus", StandardBasicTypes.STRING)
				.addScalar("nmRelatedPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdSexRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("dtBirthRelatedPerson", StandardBasicTypes.DATE)
				.addScalar("dtDeathRelatedPerson", StandardBasicTypes.DATE)
				.addScalar("indDOBApproxRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("cdSuffixRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("cdRelatedPersonStatus", StandardBasicTypes.STRING)
				.setParameterList("idPersonList", personIdList)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class));
		List<FTPersonRelationDto> ftPersonRelationDtoList = query.list();
		return ftPersonRelationDtoList;
	}

	/**
	 * Method Name: selectPCSPAmongPerons Method Description:fetches
	 * PCSPAmongPerons list
	 * 
	 * @param personIdList
	 * @param idCase
	 * @return List<PcspValueDto> @
	 */
	@Override
	public List<PcspDto> selectPCSPAmongPerons(List<Long> personIdList, Long idCase) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectPCSPAmongPerons)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCaregvrPerson", StandardBasicTypes.LONG)
				.setParameterList("idPersonList", personIdList).setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(PcspDto.class));

		List<PcspDto> pcspValueDtoList = query.list();
		return pcspValueDtoList;
	}

	/**
	 * Method Name: fetchAllDirectRelationships Method Description: This method
	 * is used to find all the relationship between two persons which are not
	 * ended or invalidated and excluding the current record. The values are
	 * fetched from the PERSON_RELATION, PERSON tables.
	 * 
	 * @param ftPersonRelationBean
	 * @return List<FTPersonRelationBean>
	 */
	@Override
	public List<FTPersonRelationBean> fetchAllDirectPersonRelationShip(FTPersonRelationBean ftPersonRelationBean) {

		List<FTPersonRelationBean> existingRelationsList = new ArrayList<>();
		String sqlQuery = fetchAllDirectPersonRelationship;

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
				.addScalar("idPersonRelation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idRelatedPerson", StandardBasicTypes.LONG).addScalar("cdOrigin", StandardBasicTypes.STRING)
				.addScalar("cdType", StandardBasicTypes.STRING).addScalar("cdRelation", StandardBasicTypes.STRING)
				.addScalar("cdLineage", StandardBasicTypes.STRING).addScalar("cdSeparation", StandardBasicTypes.STRING)
				.addScalar("dtEffective", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtDissolution", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtEnded", StandardBasicTypes.TIMESTAMP).addScalar("dtInvalid", StandardBasicTypes.TIMESTAMP)
				.addScalar("relComments", StandardBasicTypes.STRING).addScalar("cdEndReason", StandardBasicTypes.STRING)
				.addScalar("cdSexContextPerson", StandardBasicTypes.STRING)
				.addScalar("dtBirthContextPerson", StandardBasicTypes.DATE)
				.addScalar("dtDeathContextPerson", StandardBasicTypes.DATE)
				.addScalar("indDOBApproxContextPerson", StandardBasicTypes.STRING)
				.addScalar("cdContextPersonStatus", StandardBasicTypes.STRING)
				.addScalar("nmContextPerson", StandardBasicTypes.STRING)
				.addScalar("cdSexRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("dtBirthRelatedPerson", StandardBasicTypes.DATE)
				.addScalar("dtDeathRelatedPerson", StandardBasicTypes.DATE)
				.addScalar("indDOBApproxRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("cdRelatedPersonStatus", StandardBasicTypes.STRING)
				.addScalar("nmRelatedPerson", StandardBasicTypes.STRING)
				.addScalar("nmContextSuffix", StandardBasicTypes.STRING)
				.addScalar("nmRelatedSuffix", StandardBasicTypes.STRING)
				.addScalar("nmCreatedPerson", StandardBasicTypes.STRING)
				.addScalar("nmLastUpdatePerson", StandardBasicTypes.STRING)
				.setParameter("idPerson", ftPersonRelationBean.getIdPerson())
				.setParameter("cdOrigin", ftPersonRelationBean.getCdOrigin())
				.setParameter("personStatus", ftPersonRelationBean.getCdContextPersonStatus());

		query.setResultTransformer(Transformers.aliasToBean(FTPersonRelationBean.class));
		existingRelationsList = query.list();
		return existingRelationsList;

	}

	/**
	 * Method Name: isStaff Method Description:query if a given person id, there
	 * is a record in the employee table, if found record, return true else
	 * false
	 * 
	 * @param idPerson
	 * @return Boolean
	 */

	@Override
	public Boolean isStaff(Long idPerson) {
		Boolean isStaff = ServiceConstants.FALSEVAL;
		Employee employee = (Employee) sessionFactory.getCurrentSession().get(Employee.class, idPerson);
		if (!TypeConvUtil.isNullOrEmpty(employee)) {
			isStaff = ServiceConstants.TRUEVAL;
		}
		return isStaff;
	}

}
