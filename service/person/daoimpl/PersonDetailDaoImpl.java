package us.tx.state.dfps.service.person.daoimpl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.mobile.PersonRaceMpsDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.AllegationVictimReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dao.PersonDetailDao;
import us.tx.state.dfps.service.person.dto.AfcarsDto;
import us.tx.state.dfps.service.person.dto.EmployeeDto;
import us.tx.state.dfps.service.person.dto.PersonBean;
import us.tx.state.dfps.service.person.dto.PersonFullNameDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 30, 2018- 5:43:30 PM © 2017 Texas Department of
 * Family and Protective Services
 * ****************  Change History *********************
 * 05/24/2021 nairl Artifact artf185349 : Security -Records check tab is displaying for merged DFPS employee with Person in Closed case Legacy IMPACT and IMPACT2.0
 */
@Repository
public class PersonDetailDaoImpl implements PersonDetailDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${PersonDaoImpl.getPersonIdandFullName}")
	private String getPersonIdandFullName;

	@Value("${PersonDaoImpl.getFullName}")
	private String getFullName;

	@Value("${PersonDaoImpl.hasSSN}")
	private String hasSSNSql;

	@Value("${PersonDaoImpl.isSSNVerifiedByInterface}")
	private String isSSNVerifiedByInterfaceSql;

	@Value("${PersonDaoImpl.getEmployeeTypeDetail}")
	private String getEmployeeTypeDetail;

	@Value("${PersonDaoImpl.getAllegationVictimList}")
	private String getAllegationVictimList;

	@Value("${PersonMergeSplit.getPersonMergeHierarchyList}")
	private String getPersonMergeHierarchyListSql;

	@Value("${PersonMergeSplitDaoImpl.getMergeList}")
	private String getMergeListSql;

	@Value("${PersonDaoImpl.getExtrnlEmpTypeDtl}")
	private String getExtrnlEmpTypeDtlSql;

	@Value("${PersonDaoImpl.getPersonRaceMps}")
	private String getPersonRaceMpsSql;

	/**
	 * Method Name: isPersonEmpOrFormerEmp Method Description:Checks if person
	 * is an employee or former employee
	 *
	 * @param idPerson
	 * @return List<PersonBean>
	 */

	@Override
	public List<PersonBean> isPersonEmpOrFormerEmp(Long idPerson) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonCategory.class);
		criteria.add(Restrictions.eq("idPerson", idPerson));
		List<PersonCategory> personList = criteria.list();
		List<PersonBean> personBeansList = new ArrayList();
		for (PersonCategory personCategory : personList) {
			PersonBean personBean = new PersonBean();
			personBean.setIdPersonCategory(personCategory.getIdPersonCategory());
			personBean.setPersonCategoryDateLastUpdate(personCategory.getDtLastUpdate());
			personBean.setPersonCategoryCode(personCategory.getCdPersonCategory());
			personBeansList.add(personBean);
		}

		if (TypeConvUtil.isNullOrEmpty(personBeansList)) {
			throw new DataNotFoundException(messageSource.getMessage("person.personlist.data", null, Locale.US));
		}

		return personBeansList;
	}

	/**
	 * Method Name: hasSSN Method Description: Check if the Person has a Non End
	 * Dated SSN
	 *
	 * @param idPerson
	 * @return boolean
	 */
	@Override
	public boolean hasSSN(Long idPerson) {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(hasSSNSql)
				.addScalar("count", StandardBasicTypes.LONG).setParameter("idPerson", idPerson);

		if ((Long) sqlQuery.uniqueResult() > 0) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * Method Name: getPersonIdAndFullName Method Description: get PersonId and
	 * full name of person
	 *
	 * @param szNbrPersonIdNbr
	 * @param ulIdPerson
	 * @return List<PersonFullNameDto>
	 */
	@Override
	public List<PersonFullNameDto> getPersonIdAndFullName(String szNbrPersonIdNbr, Long ulIdPerson) {
		List<PersonFullNameDto> personNameList = new ArrayList<>();
		Query getPersonName = null;
		if (ulIdPerson > 0) {
			getPersonName = sessionFactory.getCurrentSession().createSQLQuery(getPersonIdandFullName)
					.addScalar(ServiceConstants.ID_PERSON_CONST, StandardBasicTypes.LONG)
					.addScalar("fullName", StandardBasicTypes.STRING)
					.setParameter("idPerson", ulIdPerson)
					.setResultTransformer(Transformers.aliasToBean(PersonFullNameDto.class));
		} else {
			getPersonName = sessionFactory.getCurrentSession().createSQLQuery(getFullName)
					.addScalar(ServiceConstants.ID_PERSON_CONST, StandardBasicTypes.LONG)
					.addScalar("fullName", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(PersonFullNameDto.class));
		}
		getPersonName.setParameter("szNbrPersonIdNbr", szNbrPersonIdNbr)
				.setParameter("personIdType", ServiceConstants.PERS_CNUMTYPE_SSN)
				.setParameter("personStatusM", ServiceConstants.PERS_CPERSTAT_M)
				.setParameter("personStatusA", ServiceConstants.PERS_CPERSTAT_A)
				.setParameter("stringIndN", ServiceConstants.STRING_IND_N)
				.setParameter("stringIndY", ServiceConstants.STRING_IND_Y);
		personNameList = (List<PersonFullNameDto>) getPersonName.list();
		if (TypeConvUtil.isNullOrEmpty(personNameList)) {
			throw new DataNotFoundException(messageSource.getMessage("person.personlist.data", null, Locale.US));
		}

		return personNameList;
	}

	/**
	 * Method Name: isSSNVerifiedByInterface Method Description: Check if the
	 * SSN has been Verified by DHS Interface
	 *
	 * @param szNbrPersonIdNbr
	 * @return Boolean
	 */

	@Override
	public Boolean isSSNVerifiedByInterface(String szNbrPersonIdNbr) {
		Boolean bSSNVerified = false;

		Object count = sessionFactory.getCurrentSession().createSQLQuery(isSSNVerifiedByInterfaceSql)
				.setParameter("NbrPersonId", szNbrPersonIdNbr).uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(count)) {
			throw new DataNotFoundException(messageSource.getMessage("personMerge.data", null, Locale.US));
		}
		if(count instanceof Integer && ((Integer) count).intValue()>0){
			bSSNVerified = true;
		} else if (count instanceof BigDecimal && ((BigDecimal) count).longValue() > 0) {
			bSSNVerified = true;
		}

		return bSSNVerified;
	}

	/**
	 * Method Name: getPersonDetails Method Description: Retrieves the person
	 * details from the PERSON table based on the Person ID
	 *
	 * @param idForwardPerson
	 * @return Person
	 */
	@Override
	public Person getPersonDetails(Long idForwardPerson) {
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, idForwardPerson);
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(messageSource.getMessage("Person.NotFound", null, Locale.US));
		}
		return person;
	}

	/**
	 * Method Name: getForwardPersonInMerge Method Description:This method
	 * checks if the person is closed person in a merge
	 *
	 * @param idClosedPerson
	 * @return Long
	 */

	@Override
	public Long getForwardPersonInMerge(Long idPerson) {
		Long ulIdForwardPersonMerge = ServiceConstants.ZERO_VAL;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class);
		criteria.add(Restrictions.eq("personByIdPersMergeClosed.idPerson", idPerson));
		criteria.add(Restrictions.eq("indPersMergeInvalid", 'N'));
		criteria.add(Restrictions.neProperty("personByIdPersMergeForward", "personByIdPersMergeClosed"));
		List<PersonMerge> personMerges = criteria.list();

		if (!CollectionUtils.isEmpty(personMerges)) {

			ulIdForwardPersonMerge = personMerges.get(0).getPersonByIdPersMergeForward().getIdPerson();

		}
		return ulIdForwardPersonMerge;
	}

	/**
	 * Method Name: checkIfMergeListLegacy Method Description: This method
	 * checks if the merge list to be fetched in a legacy stye for a person
	 * forward
	 *
	 * @param fwdPersonId
	 * @return Boolean
	 */
	@Override
	public Boolean checkIfMergeListLegacy(Long fwdPersonId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class);
		criteria.add(Restrictions.isNull("dtPersMergeSplit"));
		criteria.add(Restrictions.eq("indPersMergeInvalid", ServiceConstants.N_CHAR));
		criteria.add(Restrictions.isNull("indDirectMerge"));
		criteria.add(Restrictions.eq("personByIdPersMergeForward.idPerson", fwdPersonId));
		criteria.setProjection(Projections.count("idPersonMerge"));
		Boolean mergeListLegacy = false;
		Long rowCount = (Long) criteria.uniqueResult();
		if (rowCount > 20) {
			mergeListLegacy = true;
		}
		return mergeListLegacy;
	}

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 *
	 * @param fwdPersonId
	 * @param mergeListLegacy
	 * @return List<PersonMergeSplitValueDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long fwdPersonId, Boolean mergeListLegacy) {
		ArrayList<PersonMergeSplitValueDto> personMergeSplitValueDtoList = new ArrayList<PersonMergeSplitValueDto>();
		boolean bLoopExists = false;
		if (!mergeListLegacy) {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getPersonMergeHierarchyListSql).addScalar("idPersonMerge", StandardBasicTypes.LONG)
					.addScalar("idForwardPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
					.addScalar("dtPersonMerge", StandardBasicTypes.DATE)
					.addScalar("idClosedPerson", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeWorker", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeSplitWorker", StandardBasicTypes.LONG)
					.addScalar("indPersonMergeInvalid", StandardBasicTypes.STRING)
					.addScalar("dtPersonMergeSplit", StandardBasicTypes.DATE)
					.addScalar("idMergeGroup", StandardBasicTypes.LONG)
					.addScalar("indDirectMerge", StandardBasicTypes.STRING)
					.addScalar("idGroupLink", StandardBasicTypes.LONG)
					.addScalar("nmForwardPerson", StandardBasicTypes.STRING)
					.addScalar("nmClosedPerson", StandardBasicTypes.STRING)
					.addScalar("nmPersonMergeWorker", StandardBasicTypes.STRING)
					//artf174475 : Changed the merge/split column name used in the query to the correct one and mapped that to the response.
					.addScalar("nmPersonMergeSplitWorker", StandardBasicTypes.STRING)
					.setParameter("ulIdPerson", fwdPersonId)
					.setResultTransformer(Transformers.aliasToBean(PersonMergeSplitValueDto.class));

			personMergeSplitValueDtoList = (ArrayList<PersonMergeSplitValueDto>) sqlQuery.list();
			if (CollectionUtils.isEmpty(personMergeSplitValueDtoList))
				bLoopExists = true;
		}
		if (bLoopExists || mergeListLegacy) {

			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getMergeListSql)
					.addScalar("idPersonMerge", StandardBasicTypes.LONG)
					.addScalar("idForwardPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
					.addScalar("dtPersonMerge", StandardBasicTypes.DATE)
					.addScalar("idClosedPerson", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeWorker", StandardBasicTypes.LONG)
					.addScalar("idPersonMergeSplitWorker", StandardBasicTypes.LONG)
					.addScalar("indPersonMergeInvalid", StandardBasicTypes.STRING)
					.addScalar("dtPersonMergeSplit", StandardBasicTypes.DATE)
					.addScalar("idMergeGroup", StandardBasicTypes.LONG)
					.addScalar("indDirectMerge", StandardBasicTypes.STRING)
					.addScalar("idGroupLink", StandardBasicTypes.LONG)
					.addScalar("nmForwardPerson", StandardBasicTypes.STRING)
					.addScalar("nmClosedPerson", StandardBasicTypes.STRING)
					.addScalar("nmPersonMergeWorker", StandardBasicTypes.STRING)
					.addScalar("nmPersonMergeSplitWorker", StandardBasicTypes.STRING)
					.setParameter("fwdPersonId", fwdPersonId)
					.setResultTransformer(Transformers.aliasToBean(PersonMergeSplitValueDto.class));

			personMergeSplitValueDtoList = (ArrayList<PersonMergeSplitValueDto>) sqlQuery.list();
		}

		Long maxId = 999999999l;
		for (PersonMergeSplitValueDto personMergeSplitValueDto : personMergeSplitValueDtoList) {
			if (fwdPersonId.equals(personMergeSplitValueDto.getIdForwardPerson())
					&& !ServiceConstants.Y.equals(personMergeSplitValueDto.getIndPersonMergeInvalid())
					&& !maxId.equals(personMergeSplitValueDto.getIdClosedPerson())
					&& (ObjectUtils.isEmpty(personMergeSplitValueDto.getIdPersonMergeSplitWorker())
					|| personMergeSplitValueDto.getIdPersonMergeSplitWorker().equals(0l))) {
				personMergeSplitValueDto.setIndDirectMerge(ServiceConstants.Y);
			} else {
				personMergeSplitValueDto.setIndDirectMerge(ServiceConstants.N);
			}

			if (bLoopExists || mergeListLegacy)
				personMergeSplitValueDto.setIndLegacy(ServiceConstants.Y);
		}
		return personMergeSplitValueDtoList;
	}

	/**
	 * Method Name: getEmployeeTypeDetail Method Description:retrieve employee
	 * type info given person id
	 *
	 * @param idPerson
	 * @return Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getEmployeeTypeDetail(Long idPerson) {

		Map resultMap = null;
		Query query = null;
		Employee employeeEntity = (Employee) sessionFactory.getCurrentSession().get(Employee.class, idPerson);
		if (!ObjectUtils.isEmpty(employeeEntity) && employeeEntity.getIdPerson() != ServiceConstants.NULL_VAL) {
			query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getEmployeeTypeDetail)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("dtEmpTermination", StandardBasicTypes.DATE)
					.addScalar("cdExternalType", StandardBasicTypes.STRING)
					.addScalar("cdSecurityClassName", StandardBasicTypes.STRING)
					.setParameter("cdPersonCat", ServiceConstants.CPSNDTCT_EMP)
					.setParameter("cdPersonCatFem", ServiceConstants.CPSNDTCT_FEM).setParameter("idPersonIp", idPerson)
					.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));
		} else {
			query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getExtrnlEmpTypeDtlSql)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("dtEmpTermination", StandardBasicTypes.DATE)
					.addScalar("cdExternalType", StandardBasicTypes.STRING)
					.addScalar("cdSecurityClassName", StandardBasicTypes.STRING)
					.setParameter("cdPersonCat", ServiceConstants.CPSNDTCT_EMP).setParameter("idPersonIp", idPerson)
					.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));
		}

		List<EmployeeDto> emplyeeDetail = query.list();
		if (TypeConvUtil.isNullOrEmpty(emplyeeDetail)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (EmployeeDto employeeDto : emplyeeDetail) {
			resultMap = new HashMap();
			resultMap.put(ServiceConstants.idPerson, idPerson + ServiceConstants.EMPTY_STRING);
			resultMap.put(ServiceConstants.DT_EMP_TERMINATION,
					!ObjectUtils.isEmpty(employeeDto.getDtEmpTermination())
							? DateUtils.dateStringInSlashFormat(employeeDto.getDtEmpTermination())
							: employeeDto.getDtEmpTermination());
			resultMap.put(ServiceConstants.CD_EXTERNAL_TYPE, employeeDto.getCdExternalType());
			resultMap.put(ServiceConstants.CD_SECURITY_CLASS_NAME, employeeDto.getCdSecurityClassName());
			break;
		}
		return resultMap;
	}
	/* Added to fix defect artf185349 */
	/**
	 * Method Name: getEmployeeTypeWithMerge
	 * Method Description:retrieve employee type info given person id
	 *
	 * @param idPerson
	 * @return Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getEmployeeTypeWithMerge(Long idPerson) {

		Map resultMap = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getEmployeeTypeDetail)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtEmpTermination", StandardBasicTypes.DATE)
				.addScalar("cdExternalType", StandardBasicTypes.STRING)
				.addScalar("cdSecurityClassName", StandardBasicTypes.STRING)
				.setParameter("cdPersonCat", ServiceConstants.CPSNDTCT_EMP)
				.setParameter("cdPersonCatFem", ServiceConstants.CPSNDTCT_FEM).setParameter("idPersonIp", idPerson)
				.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));
		List<EmployeeDto> employeeDetail = query.list();
		if (TypeConvUtil.isNullOrEmpty(employeeDetail)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (EmployeeDto employeeDto : employeeDetail) {
			resultMap = new HashMap();
			resultMap.put(ServiceConstants.idPerson, idPerson + ServiceConstants.EMPTY_STRING);
			resultMap.put(ServiceConstants.DT_EMP_TERMINATION,
					!ObjectUtils.isEmpty(employeeDto.getDtEmpTermination())
							? DateUtils.dateStringInSlashFormat(employeeDto.getDtEmpTermination())
							: employeeDto.getDtEmpTermination());
			resultMap.put(ServiceConstants.CD_EXTERNAL_TYPE, employeeDto.getCdExternalType());
			resultMap.put(ServiceConstants.CD_SECURITY_CLASS_NAME, employeeDto.getCdSecurityClassName());
			break;
		}
		return resultMap;
	}
	/* End of code changes for artifact artf185349 */


	/**
	 * Method Name: getAfcarsData Method Description:retrieves the one row with
	 * the latest end date from the AFCARS_RESPONSE table for the input Person
	 * ID, for the Person Characteristics page.
	 *
	 * @param idPerson
	 * @return AfcarsDto
	 */
	@Override
	public AfcarsDto getAfcarsData(Long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AfcarsResponse.class);
		criteria.add(Restrictions.eq("idPerson", idPerson));
		// add a restriction for future date for end date
		Date compareDate = DateUtils.getJavaDate(4712, 12, 31);
		criteria.add(Restrictions.eq("dtEnd", compareDate));
		List<AfcarsResponse> afcarsResponseList = criteria.list();
		AfcarsDto afcarsDto = new AfcarsDto();
		// if there is a recored exist
		if (!ObjectUtils.isEmpty(afcarsResponseList)) {
			for (AfcarsResponse afcarsResponses : afcarsResponseList) {
				afcarsDto.setCdResponse(afcarsResponses.getCdResponse());
				afcarsDto.setDtBegin(afcarsResponses.getDtBegin());
				afcarsDto.setDtEnd(afcarsResponses.getDtEnd());
				afcarsDto.setIdAfcarsResponse(afcarsResponses.getIdAfcarsResponse());
				afcarsDto.setIdPerson(afcarsResponses.getIdPerson());
				afcarsDto.setTsLastUpdate(afcarsResponses.getDtLastUpdate());
			}
		} else {
			// get record without end date condition
			Criteria criteriaAfcars = sessionFactory.getCurrentSession().createCriteria(AfcarsResponse.class);
			criteriaAfcars.add(Restrictions.eq("idPerson", idPerson));
			List<AfcarsResponse> afcarsList = criteriaAfcars.list();
			if (!ObjectUtils.isEmpty(afcarsList)) {
				afcarsDto.setIdPerson(idPerson);
				afcarsDto.setCdResponse(ServiceConstants.AFCARS_CD_RESPONSE);
			}

		}

		return afcarsDto;
	}

	/**
	 * Method Name: savePersonAudit Method Description: this method Calls the
	 * stored procedure. Input to the stored procedure are in the ArrayList.
	 *
	 * @param arrayList
	 * @return CommonStringRes
	 */
	@Override
	public CommonStringRes savePersonAudit(List<Object> arrayList) {
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		CommonStringRes response = new CommonStringRes();
		String procedure = "{call " + "INSERT_PERSON_AUDIT" + "(?,?,?,?,?)" + "}";
		try {
			callStatement = connection.prepareCall(procedure);
			callStatement = setCallableStatement(arrayList, callStatement);
			boolean result = callStatement.execute();
			response.setCommonRes(String.valueOf(result));
		} catch (SQLException e) {
			new DataLayerException(e.getMessage());
		} catch (ParseException e) {
			new DataLayerException(e.getMessage());
		}
		return response;
	}

	/**
	 * Method Name: savePersonAuditReasonDeath Method Description: this method
	 * Calls the stored procedure. Input to the stored procedure are in the
	 * ArrayList.
	 *
	 * @param arrayList
	 * @return CommonStringRes
	 */
	@Override
	public CommonStringRes savePersonAuditReasonDeath(List<Object> arrayList) {
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		CallableStatement callStatement = null;
		CommonStringRes response = new CommonStringRes();
		String procedure = "{call " + "INSERT_PERSON_AUDIT_RSN_DTH" + "(?,?,?,?,?)" + "}";
		try {
			callStatement = connection.prepareCall(procedure);
			callStatement = setCallableStatement(arrayList, callStatement);
			boolean result = callStatement.execute();
			response.setCommonRes(String.valueOf(result));
		} catch (SQLException e) {
			new DataLayerException(e.getMessage());
		} catch (ParseException e) {
			new DataLayerException(e.getMessage());
		}
		return response;
	}

	/**
	 * Method Name: getAllegationVictimList Method Description: retrive the Allegation Victim List information
	 *
	 * @param allegationVictimReq
	 * @return ArrayList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> getAllegationVictimList(AllegationVictimReq allegationVictimReq) {
		List<String> idVictimList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(allegationVictimReq.getId_allegation_stage())) {
			Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getAllegationVictimList)
					.addScalar("idVictim", StandardBasicTypes.STRING)
					.setParameter("idStage", allegationVictimReq.getId_allegation_stage());
			idVictimList = (List<String>)query.list();
		}

		return idVictimList;
	}

	/**
	 *
	 * Method Name: setCallableStatement Method Description: This is a private
	 * method to set all input parameters(of the stored procedure) in the
	 * CallableStatement
	 *
	 * @param ArrayList
	 *            - inputValues
	 * @param CallableStatement
	 *            - callStmt
	 */
	@SuppressWarnings("rawtypes")
	private CallableStatement setCallableStatement(List<Object> inputValues, CallableStatement callStmt)
			throws SQLException, ParseException {
		int i = 1;
		for (Object val : inputValues) {
			Class valClass = val.getClass();
			if (Integer.class == valClass) {
				callStmt.setInt(i, ((Integer) val).intValue());
			} else if (String.class == valClass) {
				if (isValidDate((String) val)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date dt = dateFormat.parse((String) val);
					java.sql.Date sqlDt = new java.sql.Date(dt.getTime());
					callStmt.setDate(i, sqlDt);
				} else {
					callStmt.setString(i, (String) val);
				}
			}
			i++;
		}

		return callStmt;

	}

	/**
	 *
	 * Method Name: isValidDate Method Description: This is a private method to
	 * set all input parameters(of the stored procedure) in the
	 * CallableStatement
	 *
	 * @param String
	 *            inDate
	 * @return boolean
	 */
	private boolean isValidDate(String inDate) {

		if (inDate == null)
			return false;

		// set the format to use as a constructor argument
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (inDate.trim().length() != dateFormat.toPattern().length())
			return false;

		dateFormat.setLenient(false);

		try {
			// parse the inDate parameter
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	@Override
	public IncomingPersonMpsDto getMPSPersonDetails(Long idIncomingPerson) throws InvocationTargetException, IllegalAccessException {
		IncomingPersonMps person = (IncomingPersonMps) sessionFactory.getCurrentSession().get(IncomingPersonMps.class, idIncomingPerson);

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getPersonRaceMpsSql)
				.addScalar("idPersonRaceMps", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdRace", StandardBasicTypes.STRING)
				.setParameter("idIncomingPPersonMPS", idIncomingPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonRaceMpsDto.class));
		List<PersonRaceMpsDto> personRaceMpsList = query.list();
		IncomingPersonMpsDto incomingPersonMpsDto = new IncomingPersonMpsDto();
		org.springframework.beans.BeanUtils.copyProperties(person, incomingPersonMpsDto);
		incomingPersonMpsDto.setNbrAge(person.getNbrAge());
		incomingPersonMpsDto.setPersonRaceMpsDtoSet(new HashSet<>(personRaceMpsList));
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(messageSource.getMessage("Person.NotFound", null, Locale.US));
		}
		return incomingPersonMpsDto;
	}
}
