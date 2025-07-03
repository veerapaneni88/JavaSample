/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jul 2, 2017- 5:14:41 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.childfatality.daoimpl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.childfatality.dao.ChildFatalityDao;
import us.tx.state.dfps.service.childfatality.dto.ChildFatalityDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChildFatalityDao Performs ChildFatality related retrieve into the
 * data base. Aug 17, 2017- 5:14:41 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class ChildFatalityDaoImpl implements ChildFatalityDao {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(ChildFatalityDaoImpl.class);

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ChildFatalityDaoImpl.selectWithoutStage}")
	private String selectWithoutStage;

	@Value("${ChildFatalityDaoImpl.selectWithoutStageB}")
	private String selectWithoutStageB;

	@Value("${ChildFatalityDaoImpl.selectWithStage}")
	private String selectWithStage;

	@Value("${ChildFatalityDaoImpl.selectWithStageB}")
	private String selectWithStageB;

	@Value("${ChildFatalityDaoImpl.whereRegionPart1}")
	private String whereRegionPart1;

	@Value("${ChildFatalityDaoImpl.whereRegionPart2}")
	private String whereRegionPart2;

	@Value("${ChildFatalityDaoImpl.whereDateOfDeathRange}")
	private String whereDateOfDeathRange;

	@Value("${ChildFatalityDaoImpl.whereDateOfInvClsrRange}")
	private String whereDateOfInvClsrRange;

	@Value("${ChildFatalityDaoImpl.wherePersonId}")
	private String wherePersonId;

	@Value("${ChildFatalityDaoImpl.whereCaseId}")
	private String whereCaseId;

	@Value("${ChildFatalityDaoImpl.whereStageProgram}")
	private String whereStageProgram;

	@Value("${ChildFatalityDaoImpl.whereCounty}")
	private String whereCounty;

	@Value("${ChildFatalityDaoImpl.whereGender}")
	private String whereGender;

	@Value("${ChildFatalityDaoImpl.whereDateOfBirthRange}")
	private String whereDateOfBirthRange;

	@Value("${ChildFatalityDaoImpl.whereOperationType}")
	private String whereOperationType;

	public ChildFatalityDaoImpl() {

	}

	/**
	 * This method searches person records with Child Fatality Search Criteria.
	 * The list is iterated and duplicate is removed.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @return List<Person> - results of person search records.
	 */
	public List<Person> searchChild(ChildFatalityDto childFatalityDto) {
		String sql = "";
		String sqlExt = "";
		String flag = "";
		if (StringUtils.isNotEmpty(childFatalityDto.getSearchReportType())
				&& childFatalityDto.getSearchReportType().equals("B")) {
			sqlExt = "_B";
			log.info("sqlExt: " + sqlExt);
		}
		if (StringUtils.isNotEmpty(childFatalityDto.getRsrcRegion())
				|| StringUtils.isNotEmpty(childFatalityDto.getProgram())
				|| "icd".equals(childFatalityDto.getSearchType())) {
			flag = "SQL_SELECT_WITH_STAGE" + sqlExt;
			sql = getSearchSQL(childFatalityDto, flag);
			log.info("sql 1: " + sql);
		} else {
			flag = "SQL_SELECT_WITHOUT_STAGE" + sqlExt;
			sql = getSearchSQL(childFatalityDto, flag);
			log.info("sql 2: " + sql);
		}
		log.info("sql 3: " + sql);
		Query searchChildQuery = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(Person.class));
		searchChildQuery = formQueryParameters(childFatalityDto, flag, searchChildQuery);
		log.info("searchChildQuery: " + searchChildQuery);
		List<Person> personList = searchChildQuery.list();
		return personList;
	}

	/**
	 * This method searches person records with Child Fatality Search Criteria.
	 * The list is iterated and duplicate is removed.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @return List<Person> - results of person search records.
	 */
	public List<Person> searchChildDOD(ChildFatalityDto childFatalityDto) {
		String sql = "";
		String sqlExt = "";
		String flag = "";
		if (StringUtils.isNotEmpty(childFatalityDto.getSearchReportType())
				&& childFatalityDto.getSearchReportType().equals("B")) {
			sqlExt = "_B";
		}
		// Not in the original code....
		if ("icd".equals(childFatalityDto.getSearchType())) {
			flag = "SQL_SELECT_WITH_STAGE" + sqlExt;
			sql = getSearchSQL(childFatalityDto, flag);
		} else {
			flag = "SQL_SELECT_WITHOUT_STAGE" + sqlExt;
			sql = getSearchSQL(childFatalityDto, flag);
		}
		// Commented this one...
		// flag = "SQL_SELECT_WITHOUT_STAGE" + sqlExt;
		sql = getSearchSQL(childFatalityDto, flag);
		log.info("sql: " + sql);
		Query searchChildDODQuery = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(Person.class));
		searchChildDODQuery = formQueryParameters(childFatalityDto, flag, searchChildDODQuery);
		log.info("searchChildQuery: " + searchChildDODQuery);
		List<Person> personList = searchChildDODQuery.list();
		return personList;
	}

	/**
	 * This method searches person records with Child Fatality Search Criteria -
	 * Only Person ID. The list is iterated and duplicate is removed.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @return List<Person> - results of person search records.
	 */
	public List<Person> searchChildID(ChildFatalityDto childFatalityDto) {
		String sql = "";
		String sqlExt = "";
		String flag = "";
		if (StringUtils.isNotEmpty(childFatalityDto.getSearchReportType())
				&& childFatalityDto.getSearchReportType().equals("B")) {
			sqlExt = "_B";
		}
		flag = "SQL_SELECT_WITHOUT_STAGE" + sqlExt;
		sql = getSearchSQL(childFatalityDto, flag);
		Query searchChildDODQuery = sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(Person.class));
		searchChildDODQuery = formQueryParameters(childFatalityDto, flag, searchChildDODQuery);
		log.info("searchChildDODQuery: " + searchChildDODQuery);
		List<Person> personList = searchChildDODQuery.list();
		return personList;
	}

	/**
	 * This method returns Resources matches to Addressline1, City, State and
	 * Zip Code.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @param flag
	 *            the flag
	 * @return String
	 */
	private String getSearchSQL(ChildFatalityDto childFatalityDto, String flag) {
		StringBuilder sql = new StringBuilder();
		boolean IS_DOD = false;
		boolean IS_INVDT = false;
		if ("dod".equals(childFatalityDto.getSearchType())) {
			if (childFatalityDto.getDtFrom() != null && childFatalityDto.getDtTo() != null) {
				IS_DOD = true;
			}
		} else if ("icd".equals(childFatalityDto.getSearchType())) {
			if (childFatalityDto.getDtFrom() != null && childFatalityDto.getDtTo() != null) {
				IS_INVDT = true;
			}
		}
		boolean IS_PID = (childFatalityDto.getIdPerson() != 0);
		boolean IS_CID = (childFatalityDto.getIdCase() != 0);
		boolean IS_PROGRAM = StringUtils.isNotBlank(childFatalityDto.getProgram());
		boolean IS_REGION = StringUtils.isNotBlank(childFatalityDto.getRsrcRegion());
		boolean IS_COUNTY = StringUtils.isNotBlank(childFatalityDto.getAddrCounty());
		boolean IS_GENDER = StringUtils.isNotBlank(childFatalityDto.getGender());
		boolean IS_DOB = (childFatalityDto.getMinDob() != null);
		if (flag.equals("SQL_SELECT_WITH_STAGE")) {
			sql.append(selectWithStage);
		} else if (flag.equals("SQL_SELECT_WITHOUT_STAGE")) {
			sql.append(selectWithoutStage);
		} else if (flag.equals("SQL_SELECT_WITH_STAGE_B")) {
			sql.append(selectWithStageB);
		} else if (flag.equals("SQL_SELECT_WITHOUT_STAGE_B")) {
			sql.append(selectWithoutStageB);
		}
		if (IS_DOD) {
			sql.append(whereDateOfDeathRange);
		}
		if (IS_INVDT) {
			sql.append(whereDateOfInvClsrRange);
		}
		if (IS_PID) {
			sql.append(wherePersonId);
		}
		if (IS_CID) {
			sql.append(whereCaseId);
		}
		if (IS_PROGRAM) {
			sql.append(whereStageProgram);
		}
		if (IS_REGION) {
			sql.replace(sql.indexOf("<"), sql.indexOf(">") + 1, ", ADDRESS_PERSON_LINK APL, PERSON_ADDRESS PA");
			if (IS_DOD) {
				sql.append(whereRegionPart1 + whereDateOfDeathRange + whereRegionPart2);
			} else {
				sql.append(whereRegionPart1 + whereRegionPart2);
			}
		}
		if (IS_COUNTY) {
			sql.append(whereCounty);
		}
		if (IS_GENDER) {
			sql.append(whereGender);
		}
		if (IS_DOB) {
			sql.append(whereDateOfBirthRange);
		}
		if (childFatalityDto.getSearchReportType().equals("B")) {
			boolean IS_OPERATION_TYPE = StringUtils.isNotEmpty(childFatalityDto.getOperationType());
			if (IS_OPERATION_TYPE) {
				sql.append(whereOperationType);
			}
		}
		if (!IS_REGION) {
			sql.replace(sql.indexOf("<"), sql.indexOf(">") + 1, " ");
		}
		return sql.toString();
	}

	/**
	 * Form query parameters.
	 *
	 * @param childFatalityDto
	 *            the child fatality dto
	 * @param flag
	 *            the flag
	 * @param querySQL
	 *            the query SQL
	 * @return Hibernate query
	 */
	private Query formQueryParameters(ChildFatalityDto childFatalityDto, String flag, Query querySQL) {
		boolean IS_DOD = false;
		boolean IS_INVDT = false;
		if ("dod".equals(childFatalityDto.getSearchType())) {
			if (childFatalityDto.getDtFrom() != null && childFatalityDto.getDtTo() != null) {
				IS_DOD = true;
			}
		} else if ("icd".equals(childFatalityDto.getSearchType())) {
			if (childFatalityDto.getDtFrom() != null && childFatalityDto.getDtTo() != null) {
				IS_INVDT = true;
			}
		}
		boolean IS_PID = (childFatalityDto.getIdPerson() != 0);
		boolean IS_CID = (childFatalityDto.getIdCase() != 0);
		boolean IS_PROGRAM = StringUtils.isNotBlank(childFatalityDto.getProgram());
		boolean IS_REGION = StringUtils.isNotBlank(childFatalityDto.getRsrcRegion());
		boolean IS_COUNTY = StringUtils.isNotBlank(childFatalityDto.getAddrCounty());
		boolean IS_GENDER = StringUtils.isNotBlank(childFatalityDto.getGender());
		boolean IS_DOB = (StringUtils.isNotBlank(childFatalityDto.getMinStrDob()));
		if (IS_DOD) {
			querySQL.setParameter("personDeathFromDate", childFatalityDto.getDtFrom());
			querySQL.setParameter("personDeathToDate", childFatalityDto.getDtTo());
		}
		if (IS_INVDT) {
			querySQL.setParameter("stageCloseFromDate", childFatalityDto.getDtFrom());
			querySQL.setParameter("stageCloseToDate", childFatalityDto.getDtTo());
		}
		if (IS_PID) {
			querySQL.setParameter("idPerson", childFatalityDto.getIdPerson());
		}
		if (IS_CID) {
			querySQL.setParameter("caseId", childFatalityDto.getIdCase());
		}
		if (IS_PROGRAM) {
			querySQL.setParameter("programStageCode", childFatalityDto.getProgram());
		}
		if (IS_REGION) {
			if (IS_DOD) {
				querySQL.setParameter("personDeathFromDate", childFatalityDto.getDtFrom());
				querySQL.setParameter("personDeathToDate", childFatalityDto.getDtTo());
				querySQL.setParameter("regionCountyRegCode", childFatalityDto.getRsrcRegion());
			} else {
				querySQL.setParameter("regionCountyRegCode", childFatalityDto.getRsrcRegion());
			}
		}
		if (IS_COUNTY) {
			querySQL.setParameter("personCountyCode", childFatalityDto.getAddrCounty());
		}
		if (IS_GENDER) {
			querySQL.setParameter("personSexCode", childFatalityDto.getGender());
		}
		if (IS_DOB) {
			querySQL.setParameter("minStrDob", childFatalityDto.getMinStrDob());
			querySQL.setParameter("maxStrDob", childFatalityDto.getMaxStrDob());
		}
		if (childFatalityDto.getSearchReportType().equals("B")) {
			boolean IS_OPERATION_TYPE = StringUtils.isNotEmpty(childFatalityDto.getOperationType());
			if (IS_OPERATION_TYPE) {
				querySQL.setParameter("reportCode", childFatalityDto.getOperationType());
			}
		}
		return querySQL;
	}
}
