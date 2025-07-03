package us.tx.state.dfps.service.familytree.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonRelation;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.FTFamilyTreeUtil;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipSearchDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Data
 * Access Object contains functions related to Family Tree, Implement
 * FTRelationshipSearchDao Search functionality. Feb 12, 2018- 6:10:24 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class FTRelationshipSearchDaoImpl implements FTRelationshipSearchDao {

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${FTRelationshipSearchDaoImpl.selectAllDirectRelationships}")
	private String selectAllDirectRelationships;

	@Value("${FTRelationshipSearchDaoImpl.selectStagePersonListRelations}")
	private String selectStagePersonListRelations;

	@Value("${FTRelationshipSearchDaoImpl.relSearchResultsColumns}")
	private String relSearchResultsColumns;

	@Value("${FTRelationshipSearchDaoImpl.selectCasePersonListRelations}")
	private String selectCasePersonListRelations;

	@Value("${FTRelationshipSearchDaoImpl.casePersonListRelationsSelectStatement}")
	private String casePersonListRelationsSelectStatement;

	@Value("${FTRelationshipSearchDaoImpl.selectPersonrelInStageOrCaseSql1}")
	private String selectPersonrelInStageOrCaseSql1;

	@Value("${FTRelationshipSearchDaoImpl.selectPersonrelInStageOrCaseSql2}")
	private String selectPersonrelInStageOrCaseSql2;

	@Value("${FTRelationshipSearchDao.selectAllDirectRelForPersons}")
	private String selectAllDirectRelForPersons;

	@Value("${FTRelationshipSearchDaoImpl.selectRelAmongPersons}")
	private String selectRelAmongPersons;

	@Value("${FTRelationshipSearchDaoImpl.selectAllrelIncaseSqlSelect}")
	private String selectAllrelIncaseSqlSelect;

	@Value("${FTRelationshipSearchDaoImpl.selectAllrelIncaseSqlFrom}")
	private String selectAllrelIncaseSqlFrom;

	@Value("${FTRelationshipSearchDaoImpl.selectStagePersonlistSubqryStage}")
	private String selectStagePersonlistSubqryStage;

	@Value("${FTRelationshipSearchDaoImpl.selectAllrelIncaseSqlWhere2}")
	private String selectAllrelIncaseSqlWhere2;

	@Value("${FTRelationshipSearchDaoImpl.selectAllrelIncaseSqlAnd1}")
	private String selectAllrelIncaseSqlAnd1;

	@Value("${FTRelationshipSearchDaoImpl.selectAllrelIncaseSqlWhere1}")
	private String selectAllrelIncaseSqlWhere1;

	@Value("${FTRelationshipSearchDaoImpl.selectAllrelIncaseSqlAnd2}")
	private String selectAllrelIncaseSqlAnd2;

	@Value("${FTRelationshipSearchDaoImpl.selectStagePersonlistSubqryCase}")
	private String selectStagePersonlistSubqryCase;

	@Value("${FTRelationshipSearchDao.selectAllDirectRelPersonClosedCaseorStage}")
	private String selectAllDirectRelPersonClosedCaseorStage;

	@Value("${FTRelationshipSearchDaoImpl.selectAllDirectRelInClosedCaseOrStage}")
	private String selectAllDirectRelInClosedCaseOrStage;

	@Value("${FTRelationshipSearchDaoImpl.selectPersonRelWithinClosedCaseOrStage}")
	private String selectPersonRelWithinClosedCaseOrStage;

	@Value("${FTRelationshipSearchDaoImpl.selectAllDirectRelInClosedCaseOrStageSql}")
	private String selectAllDirectRelInClosedCaseOrStageSql;

	/**
	 * Method Name: selectAllDirectRelationships Method Description: This method
	 * fetches All Direct Relationships with the selected Context Person.
	 * 
	 * @param idPerson
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllDirectRelationships(long idPerson) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectAllDirectRelationships)
				.setLong("idPerson", idPerson).setLong("idRelatedPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class));
		createSQLForSearch(query);
		List<FTPersonRelationDto> personRelationList = query.list();
		personRelationList.forEach(ftPersonRelationDto -> {
			populatePersonDetails(ftPersonRelationDto);
		});

		return personRelationList;
	}

	/**
	 * 
	 * Method Name: selectAllRelationshipsWithinStage Method Description:This
	 * method fetches All Person Relationships with in a Stage It has to make
	 * sure both the Persons in the Relationship are with in the Stage.
	 * 
	 * @param idStage
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllRelationshipsWithinStage(Long idStage) {
		StringBuilder selectAllrelInstageSql = new StringBuilder();
		selectAllrelInstageSql.append(selectAllrelIncaseSqlSelect);
		selectAllrelInstageSql.append(" ");
		selectAllrelInstageSql.append(relSearchResultsColumns);
		selectAllrelInstageSql.append(" ");
		selectAllrelInstageSql.append(selectAllrelIncaseSqlFrom);
		selectAllrelInstageSql.append(" ");
		selectAllrelInstageSql.append(selectAllrelIncaseSqlWhere1);
		selectAllrelInstageSql.append(" (");
		selectAllrelInstageSql.append(selectStagePersonlistSubqryStage);
		selectAllrelInstageSql.append(selectAllrelIncaseSqlWhere2);
		selectAllrelInstageSql.append(" (");
		selectAllrelInstageSql.append(selectStagePersonlistSubqryStage);
		selectAllrelInstageSql.append(selectAllrelIncaseSqlAnd1);
		selectAllrelInstageSql.append(" ");
		selectAllrelInstageSql.append(selectAllrelIncaseSqlAnd2);
		selectAllrelInstageSql.append("))");

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectAllrelInstageSql.toString()).setLong("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class));
		createSQLForSearch(sQLQuery1);
		List<FTPersonRelationDto> personRelationList = sQLQuery1.list();
		personRelationList.forEach(ftPersonRelationDto -> {
			populatePersonDetails(ftPersonRelationDto);
		});

		return personRelationList;
	}

	/**
	 * 
	 * Method Name: selectAllRelWithinClosedCaseOrStage Method Description:This
	 * method fetches All Relationships with in a CLOSED Case or Stage It has to
	 * make sure both the Persons in the Relationship are with in the Stage.
	 * 
	 * @param zeroValue
	 * @param idStage
	 * @param dtClosed
	 * @return List<FTPersonRelationDto> @
	 * 
	 */
	@Override
	public List<FTPersonRelationDto> selectAllRelWithinClosedCaseOrStage(Long idCase, Long idStage, Date dtClosed) {
		Long idCaseOrStage = (!ObjectUtils.isEmpty(idCase) && idCase.equals(0)) ? idStage : idCase;
		String caseStageHolder = (idCase == 0) ? " ID_STAGE =" + idCaseOrStage : " ID_CASE =" + idCaseOrStage;
		String sqlQuery = selectAllDirectRelInClosedCaseOrStageSql;
		sqlQuery = sqlQuery.replaceAll(ServiceConstants.CASE_STAGE_HOLDER, caseStageHolder);

		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
		createSQLForSearch(query);
		query.setParameter("dtClose", dtClosed);
		List<FTPersonRelationDto> ftPersonRelationBeans = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeans) {
			populatePersonDetails(ftPersonRelationDto);
		}
		return ftPersonRelationBeans;
	}

	/**
	 * 
	 * Method Name: selectPersonRelationshipsWithinStage Method Description:This
	 * method fetches Person Relationships for the selected Person with in a
	 * Stage It has to make sure both the Persons in the Relationship are with
	 * in the Stage.
	 * 
	 * @param idStage
	 * @param idContextPerson
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectPersonRelationshipsWithinStage(Long idStage, Long idContextPerson) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(ServiceConstants.SELECT_DISTINCT + ServiceConstants.SPACE + relSearchResultsColumns
						+ ServiceConstants.SPACE + selectPersonrelInStageOrCaseSql1 + ServiceConstants.SPACE
						+ ServiceConstants.WHERE_ID_STAGE_FTR + idStage.toString() + ServiceConstants.SPACE
						+ selectPersonrelInStageOrCaseSql2)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
		createSQLForSearch(query);
		query.setParameter("idPerson", idContextPerson);

		List<FTPersonRelationDto> ftPersonRelationBeans = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeans) {
			populatePersonDetails(ftPersonRelationDto);
		}
		return ftPersonRelationBeans;
	}

	/**
	 * 
	 * Method Name: selectAllRelationshipsWithinCase Method Description:This
	 * method fetches All Person Relationships with in a Case It has to make
	 * sure both the Persons in the Relationship are with in the Case.
	 * 
	 * @param idCase
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllRelationshipsWithinCase(Long idCase) {

		StringBuilder selectAllrelIncaseSql = new StringBuilder();
		selectAllrelIncaseSql.append(selectAllrelIncaseSqlSelect);
		selectAllrelIncaseSql.append(" ");
		selectAllrelIncaseSql.append(relSearchResultsColumns);
		selectAllrelIncaseSql.append(" ");
		selectAllrelIncaseSql.append(selectAllrelIncaseSqlFrom);
		selectAllrelIncaseSql.append(" ");
		selectAllrelIncaseSql.append(selectAllrelIncaseSqlWhere1);
		selectAllrelIncaseSql.append(" (");
		selectAllrelIncaseSql.append(selectStagePersonlistSubqryCase);
		selectAllrelIncaseSql.append(selectAllrelIncaseSqlWhere2);
		selectAllrelIncaseSql.append(" (");
		selectAllrelIncaseSql.append(selectStagePersonlistSubqryCase);
		selectAllrelIncaseSql.append(selectAllrelIncaseSqlAnd1);
		selectAllrelIncaseSql.append(" ");
		selectAllrelIncaseSql.append(selectAllrelIncaseSqlAnd2);
		selectAllrelIncaseSql.append("))");

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectAllrelIncaseSql.toString()).setLong("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class));
		createSQLForSearch(sQLQuery1);
		List<FTPersonRelationDto> personRelationList = sQLQuery1.list();

		for (FTPersonRelationDto ftPersonRelationDto : personRelationList) {
			populatePersonDetails(ftPersonRelationDto);
		}
		return personRelationList;
	}

	/**
	 * 
	 * Method Name: selectPersonRelationshipsWithinCase Method Description: This
	 * method fetches Person Relationships for the selected Person with in a
	 * Case It has to make sure both the Persons in the Relationship are with in
	 * the Stage.
	 * 
	 * @param idCase
	 * @param idContextPerson
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectPersonRelationshipsWithinCase(Long idCase, Long idContextPerson) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(ServiceConstants.SELECT_DISTINCT + ServiceConstants.SPACE + relSearchResultsColumns
						+ ServiceConstants.SPACE + selectPersonrelInStageOrCaseSql1 + ServiceConstants.SPACE
						+ ServiceConstants.WHERE_ID_CASE_FTR + idCase.toString() + ServiceConstants.SPACE
						+ selectPersonrelInStageOrCaseSql2)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
		createSQLForSearch(query);
		query.setParameter("idPerson", idContextPerson);

		List<FTPersonRelationDto> ftPersonRelationBeans = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeans) {
			populatePersonDetails(ftPersonRelationDto);
		}
		return ftPersonRelationBeans;

	}

	/**
	 * Method Name: selectCasePersonListRelations Method Description: fetches
	 * PersonRelWithinClosedCaseOrStage details
	 * 
	 * @param idCase
	 * @param idStage
	 * @param idContextPerson
	 * @param dtClosed
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectPersonRelWithinClosedCaseOrStage(Long idCase, Long idStage,
			Long idContextPerson, Date dtClosed) {
		String selectQuery = selectPersonRelWithinClosedCaseOrStage;
		if (idCase == ServiceConstants.ZERO_VAL) {
			selectQuery = selectQuery.replaceAll(ServiceConstants.CASE_STAGE_HOLDER, " ID_STAGE = :idStage ");
		} else {
			selectQuery = selectQuery.replaceAll(ServiceConstants.CASE_STAGE_HOLDER, " ID_CASE = :idCase ");
		}
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectQuery)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
		createSQLForSearch(query);
		query.setParameter("idPerson", idContextPerson);
		query.setParameter("dtClose", dtClosed);
		if (idCase == ServiceConstants.ZERO_VAL) {
			query.setParameter("idStage", idStage);
		} else {
			query.setParameter("idCase", idCase);
		}
		List<FTPersonRelationDto> ftPersonRelationBeans = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeans) {
			populatePersonDetails(ftPersonRelationDto);
		}

		return ftPersonRelationBeans;
	}

	/**
	 * Method Name: selectCasePersonListRelations Method Description: This
	 * method fetches All Relationships of persons with in the Case. Persons in
	 * the Relationship could be in any Case or Stage
	 * 
	 * @param idCase
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectCasePersonListRelations(Long idCase) {

		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(casePersonListRelationsSelectStatement + ServiceConstants.SPACE
						+ relSearchResultsColumns + ServiceConstants.SPACE + selectCasePersonListRelations)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));

		createSQLForSearch(query);

		query.setParameter("idCase", idCase);

		List<FTPersonRelationDto> ftPersonRelationBeans = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeans) {
			populatePersonDetails(ftPersonRelationDto);
		}
		return ftPersonRelationBeans;

	}

	/**
	 * Method Name: selectAllDirectRelInClosedCaseOrStage Method
	 * Description:This method fetches All Direct Relationships in a CLOSED Case
	 * or Stage Related Persons can be in other Case/Stage.
	 * 
	 * @param idCase
	 * @param idStage
	 * @param dtCaseClosed
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllDirectRelInClosedCaseOrStage(Long idCase, Long idStage,
			Date dtCaseClosed) {

		String selectQuery = selectAllDirectRelInClosedCaseOrStageSql;
		if (idCase == ServiceConstants.ZERO_VAL) {
			selectQuery = selectQuery.replaceAll(ServiceConstants.CASE_STAGE_HOLDER, " ID_STAGE = :idStage ");
		} else {
			selectQuery = selectQuery.replaceAll(ServiceConstants.CASE_STAGE_HOLDER, " ID_CASE = :idCase ");
		}
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectQuery)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));

		createSQLForSearch(query);

		query.setParameter("dtClose", dtCaseClosed);
		if (idCase == ServiceConstants.ZERO_VAL) {
			query.setParameter("idStage", idStage);
		} else {
			query.setParameter("idCase", idCase);
		}
		List<FTPersonRelationDto> ftPersonRelationBeans = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeans) {
			populatePersonDetails(ftPersonRelationDto);
		}
		return ftPersonRelationBeans;
	}

	/**
	 * Method Name: selectStagePersonListRelations Method Description: This
	 * method fetches All Relationships of persons with in the stage. Persons in
	 * the Relationship could be in any Case or Stage
	 * 
	 * @param idStage
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectStagePersonListRelations(Long idStage) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectStagePersonListRelations)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
		createSQLForSearch(query);
		query.setParameter("idStage", idStage);
		List<FTPersonRelationDto> ftPersonRelationBeanList = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeanList) {
			populatePersonDetails(ftPersonRelationDto);
		}
		return ftPersonRelationBeanList;
	}

	/**
	 * Method Name: selectAllDirectRelPersonClosedCaseorStage Method
	 * Description:Fetches AllDirectRelPersonClosedCaseorStage
	 * 
	 * @param idContextPerson
	 * @param dtClosed
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllDirectRelPersonClosedCaseorStage(Long idContextPerson, Date dtClosed) {
		List<FTPersonRelationDto> ftPersonRelationBeanList = new ArrayList<>();

		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectAllDirectRelPersonClosedCaseorStage)
				.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
		createSQLForSearch(query);

		query.setParameter("idPerson", idContextPerson);
		query.setParameter("dtCreated", dtClosed);
		ftPersonRelationBeanList = query.list();
		for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeanList) {
			populatePersonDetails(ftPersonRelationDto);
		}

		return ftPersonRelationBeanList;
	}

	/**
	 * Method Name: selectRelationsAmongPersons Method Description:Fetches
	 * RelationsAmongPerson List
	 * 
	 * @param relPersonIdList
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectRelationsAmongPersons(List<Long> relPersonIdList) {

		List<FTPersonRelationDto> ftPersonRelationBeanList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(relPersonIdList)) {
			SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectRelAmongPersons)
					.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
			createSQLForSearch(query);
			query.setParameterList("relPersonIdList", relPersonIdList);
			ftPersonRelationBeanList = query.list();
			for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeanList) {
				populatePersonDetails(ftPersonRelationDto);
			}

		}
		return ftPersonRelationBeanList;
	}

	/**
	 * Method Name: selectAllDirectRelForPersons Method Description: This method
	 * gets All Direct Relations for Persons.
	 * 
	 * @param personIdList
	 * @return List<FTPersonRelationDto> @
	 */
	@Override
	public List<FTPersonRelationDto> selectAllDirectRelForPersons(List<Long> personIdList) {
		List<FTPersonRelationDto> ftPersonRelationBeanList;
		if (!personIdList.isEmpty()) {
			SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectAllDirectRelForPersons)
					.setResultTransformer(Transformers.aliasToBean(FTPersonRelationDto.class)));
			createSQLForSearch(query);
			query.setParameterList("idPersonList", personIdList);
			ftPersonRelationBeanList = query.list();
			for (FTPersonRelationDto ftPersonRelationDto : ftPersonRelationBeanList) {
				populatePersonDetails(ftPersonRelationDto);
			}
		} else {
			ftPersonRelationBeanList = new ArrayList<>();
		}
		return ftPersonRelationBeanList;
	}

	/**
	 * 
	 * Method Name: populatePersonDetails Method Description: set person age to
	 * FTPersonRelationDto
	 * 
	 * @param relValueBean
	 */
	public void populatePersonDetails(FTPersonRelationDto relValueBean) {
		relValueBean.setAgeContextPerson(FTFamilyTreeUtil.getAge(relValueBean.getDtBirthContextPerson(),
				relValueBean.getDtDeathContextPerson()));
		relValueBean.setAgeRelatedPerson(FTFamilyTreeUtil.getAge(relValueBean.getDtBirthRelatedPerson(),
				relValueBean.getDtDeathRelatedPerson()));
	}

	private void createSQLForSearch(SQLQuery query) {
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
		query.addScalar("cdContextPersonStatus", StandardBasicTypes.STRING);
		query.addScalar("nmRelatedPersonFull", StandardBasicTypes.STRING);
		query.addScalar("cdSexRelatedPerson", StandardBasicTypes.STRING);
		query.addScalar("dtBirthRelatedPerson", StandardBasicTypes.DATE);
		query.addScalar("dtDeathRelatedPerson", StandardBasicTypes.DATE);
		query.addScalar("indDOBApproxRelatedPerson", StandardBasicTypes.STRING);
		query.addScalar("cdSuffixRelatedPerson", StandardBasicTypes.STRING);
		query.addScalar("cdRelatedPersonStatus", StandardBasicTypes.STRING);
	}

	/**
	 * 
	 * Method Name: insertRelationship Method Description: Insert Person
	 * Relation details to Person_Relation table
	 * 
	 * @param ftPersonRelationDto
	 */
	@Override
	public void insertRelationship(FTPersonRelationDto ftPersonRelationDto) {
		PersonRelation personRelation = new PersonRelation();
		personRelation.setIdPersonRelation(ServiceConstants.ZERO);
		personRelation.setIdLastUpdatePerson(ftPersonRelationDto.getIdLastUpdatePerson());
		personRelation.setIdCreatedPerson(ftPersonRelationDto.getIdCreatedPerson());
		Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", ftPersonRelationDto.getIdPerson())).uniqueResult();
		Person relatedPerson = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", ftPersonRelationDto.getIdRelatedPerson())).uniqueResult();
		personRelation.setPersonByIdPerson(person);
		personRelation.setPersonByIdRelatedPerson(relatedPerson);
		personRelation.setCdOrigin(ftPersonRelationDto.getCdOrigin());
		personRelation.setCdType(ftPersonRelationDto.getCdType());
		personRelation.setCdRelation(ftPersonRelationDto.getCdRelation());
		personRelation.setCdLineage(ftPersonRelationDto.getCdLineage());
		personRelation.setCdSeparation(ftPersonRelationDto.getCdSeparation());
		personRelation.setDtEffective(ftPersonRelationDto.getDtEffective());
		personRelation.setDtDissolution(ftPersonRelationDto.getDtDissolution());
		personRelation.setDtEnded(ftPersonRelationDto.getDtEnded());
		personRelation.setDtInvalid(ftPersonRelationDto.getDtInvalid());
		personRelation.setTxtRelComments(ftPersonRelationDto.getTxtRelComments());
		personRelation.setCdEndReason(ftPersonRelationDto.getCdEndReason());
		/* ALM defect :15530 - Set values for dtCreated and dtLastUpdate */
		personRelation.setDtCreated(new Date());
		personRelation.setDtLastUpdate(new Date());
		/* End of ALM defect :15530 */
		sessionFactory.getCurrentSession().save(personRelation);
	}

	/**
	 * 
	 * Method Name: updateRelationship Method Description: Update Person
	 * Relation details to Person_Relation table
	 * 
	 * @param ftPersonRelationDto
	 */
	@Override
	public void updateRelationship(FTPersonRelationDto ftPersonRelationDto) {
		PersonRelation personRelation = (PersonRelation) sessionFactory.getCurrentSession()
				.createCriteria(PersonRelation.class)
				.add(Restrictions.eq("idPersonRelation", ftPersonRelationDto.getIdPersonRelation())).uniqueResult();
		personRelation.setIdLastUpdatePerson(ftPersonRelationDto.getIdLastUpdatePerson());
		personRelation.setIdCreatedPerson(ftPersonRelationDto.getIdCreatedPerson());
		Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", ftPersonRelationDto.getIdPerson())).uniqueResult();
		Person relatedPerson = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", ftPersonRelationDto.getIdRelatedPerson())).uniqueResult();
		personRelation.setPersonByIdPerson(person);
		personRelation.setPersonByIdRelatedPerson(relatedPerson);
		personRelation.setCdOrigin(ftPersonRelationDto.getCdOrigin());
		personRelation.setCdType(ftPersonRelationDto.getCdType());
		personRelation.setCdRelation(ftPersonRelationDto.getCdRelation());
		personRelation.setCdLineage(ftPersonRelationDto.getCdLineage());
		personRelation.setCdSeparation(ftPersonRelationDto.getCdSeparation());
		personRelation.setDtEffective(ftPersonRelationDto.getDtEffective());
		personRelation.setDtDissolution(ftPersonRelationDto.getDtDissolution());
		personRelation.setDtEnded(ftPersonRelationDto.getDtEnded());
		personRelation.setDtInvalid(ftPersonRelationDto.getDtInvalid());
		personRelation.setTxtRelComments(ftPersonRelationDto.getTxtRelComments());
		personRelation.setCdEndReason(ftPersonRelationDto.getCdEndReason());
		sessionFactory.getCurrentSession().saveOrUpdate(personRelation);
	}
}
