package us.tx.state.dfps.service.common.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.IncomingPersonMps;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonId;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this method
 * implements PersonUtil Oct 9, 2017- 4:13:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class PersonUtil {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersonDao personDao;

	@Value("${RecertificationUtil.findPrimaryChildForStageSql}")
	private String findPrimaryChildForStageSql;

	@Value("${PersonUtils.findPersonId}")
	private String findPersonId;

	@Value("${PersonUtil.getLegalStatusDate}")
	private String getLegalStatusDate;

	@Value("${PersonUtil.getLegalStatusDateOrder}")
	private String getLegalStatusDateOrder;

	@Value("${PersonAuthDaoImpl.selectSql}")
	private String getPersonName;

	@Value("${PersonUtil.hasDOBChangedForCertPers}")
	private String hasDOBChangedForCertPersSql;

	@Value("${PersonUtils.selectPersonDetail}")
	private String selectPersonDetail;

	@Value("${PersonUtil.getPersonFullName}")
	private String getNmPersonFullSql;

	/**
	 * Method Name: findPrimaryChildForStage Method Description:this method
	 * retrieves the idPerson
	 * 
	 * @param idStage
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long findPrimaryChildForStage(Long idStage) throws DataNotFoundException {
		Long stageId = ServiceConstants.NULL_VAL;
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(findPrimaryChildForStageSql);
		query.setParameter("idStage", idStage);

		List<BigDecimal> stageList = (List<BigDecimal>) query.list();

		if (!TypeConvUtil.isNullOrEmpty(stageList)) {
			stageId = stageList.get(ServiceConstants.Zero).longValue();
		}

		return stageId;
	}

	/**
	 * Method Name: findSsn Method Description: this method retrieves the
	 * PersonId
	 * 
	 * @param idPerson
	 * @return String
	 * @throws DataNotFoundException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public String findSsn(Long idPerson) throws DataNotFoundException {
		return findPersonId(idPerson, ServiceConstants.CNUMTYPE_SSN);
	}

	/**
	 * Method Name: findPersonId Method Description:
	 * 
	 * @param idPerson
	 * @param cnumtypeSsn
	 * @return String
	 */
	private String findPersonId(Long idPerson, String personIdType) throws DataNotFoundException {
		String personId = "";
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(findPersonId);
		query.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING);
		query.setParameter("idPerson", idPerson);
		query.setParameter("cdPersonIdType", personIdType);
		query.setResultTransformer(Transformers.aliasToBean(PersonId.class));
		List<PersonId> list = (List<PersonId>) query.list();
		// commented for reusability. Please throw the exception in service
		// based on personId value.
		/*
		 * if (TypeConvUtil.isNullOrEmpty(list)) { throw new
		 * DataNotFoundException(messageSource.getMessage(
		 * "Common.noRecordFound", null, Locale.US)); }
		 */
		if (!list.isEmpty()) {
			personId = list.get(ServiceConstants.Zero).getNbrPersonIdNumber();
		}

		return personId;
	}

	/**
	 * Method Name: findMedicaid Method Description:this method retrieves the
	 * Medicaid
	 * 
	 * @param idPerson
	 * @return String
	 * @throws DataNotFoundException
	 */
	public String findMedicaid(Long idPerson) throws DataNotFoundException {
		return findPersonId(idPerson, ServiceConstants.CNUMTYPE_MEDICAID_NUMBER);
	}

	/**
	 * Method Name: getLegalStatusDate Method Description: Get Legal Status Date
	 * of the Child, Legal Status is dependent on the type of Stage
	 *
	 * @param idPerson
	 * @param idCase
	 * @param stageType
	 * @return Date
	 * @throws DataNotFoundException
	 */
	public Date getLegalStatusDate(Long idPerson, Long idCase, String stageType) throws DataNotFoundException {

		Date dtLegalStatStatusDate = null;
		String cdLegalStatus = "";

		StringBuilder dynamicSql = new StringBuilder();
		dynamicSql.append(getLegalStatusDate);

		if (ServiceConstants.CSTGTYPE_CJPC.equals(stageType) || ServiceConstants.CSTGTYPE_CTYC.equals(stageType)) {
			cdLegalStatus = "'" + ServiceConstants.CLEGSTAT_010 + "'";
		} else {
			cdLegalStatus = "'" + ServiceConstants.CLEGSTAT_010 + "', " + "'" + ServiceConstants.CLEGSTAT_020 + "', "
					+ "'" + ServiceConstants.CLEGSTAT_030 + "', " + "'" + ServiceConstants.CLEGSTAT_040 + "', " + "'"
					+ ServiceConstants.CLEGSTAT_050 + "', " + "'" + ServiceConstants.CLEGSTAT_070 + "', " + "'"
					+ ServiceConstants.CLEGSTAT_080 + "', " + "'" + ServiceConstants.CLEGSTAT_130 + "'";
		}

		dynamicSql.append(cdLegalStatus);
		dynamicSql.append(getLegalStatusDateOrder);

		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(dynamicSql.toString());
		sqlQuery.setParameter("idPerson", idPerson);
		sqlQuery.setParameter("idCase", idCase);

		List<Date> dateList = sqlQuery.list();

		if (TypeConvUtil.isNullOrEmpty(dateList)) {
			throw new DataNotFoundException(messageSource.getMessage("dateList.not.found.attributes", null, Locale.US));
		}

		if (CollectionUtils.isNotEmpty(dateList)) {
			dtLegalStatStatusDate = dateList.get(0);
		}

		return dtLegalStatStatusDate;
	}

	public String getPersonNameFromNameTbl(long idName, long idPerson) throws DataNotFoundException {
		String personName = "";
		String firstName = "";
		String middleName = "";
		String lastName = "";
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getPersonName);
		query.addScalar("nmNameFirst", StandardBasicTypes.STRING);
		query.addScalar("nmNameMiddle", StandardBasicTypes.STRING);
		query.addScalar("nmNameLast", StandardBasicTypes.STRING);
		query.setParameter("idPerson", idPerson);
		query.setParameter("idName", idName);
		personName = formatPersonName(firstName, middleName, lastName);
		return personName;
	}

	/**
	 * 
	 * This creates Person Full Name String from first, last and middle names.
	 * 
	 * @param fullName
	 * @param suffix
	 * @param idPerson
	 * 
	 * @return Person Name
	 */
	public static String formatPersonName(String firstName, String middleName, String lastName) {
		String personName = lastName;
		personName += ", " + firstName;
		personName += " " + middleName;
		return personName;
	}

	/**
	 * 
	 * Method Name: formatStreetAddress Method Description:This function formats
	 * Street Address fields by adding Street Address1 and Street Address 2.
	 * 
	 * @param addressLine1
	 * @param addressLine2
	 * @return
	 */
	public static String formatStreetAddress(String addressLine1, String addressLine2) {
		String streetAddress = (isValid(addressLine1)) ? addressLine1 : "";
		streetAddress += (isValid(addressLine2)) ? " " + addressLine2 : "";
		return streetAddress;
	}

	/**
	 * Method Name: isValid Method Description:Checks to see if a given string
	 * is valid. This includes checking that the string is not null or empty.
	 * 
	 * @param value
	 * @return boolean
	 */
	public static boolean isValid(String value) {
		if (value == null)
			return ServiceConstants.FALSEVAL;
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

	/**
	 * Method Name: isStepParent Method Description:
	 * 
	 * @param cdRelInt
	 * @return Boolean
	 */
	public static Boolean isStepParent(String cdRelInt) {
		return ServiceConstants.CRELPRN2_ST.equals(cdRelInt);
	}

	/**
	 * Method Name: hasDOBChangedForCertPers Method Description:
	 * hasDOBChangedForCertPers
	 * 
	 * @param idFceEligibility
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean hasDOBChangedForCertPers(Long idFceEligibility) throws DataNotFoundException {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(hasDOBChangedForCertPersSql)
				.addScalar("dtBirth", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.setParameter("idFceEligibility", idFceEligibility)
				.setResultTransformer(Transformers.aliasToBean(FcePersonDto.class));
		Boolean hasDOBChanged = Boolean.FALSE;
		List<FcePersonDto> fcePersonDtoList = query.list();
		for (FcePersonDto fcePersonDto : fcePersonDtoList) {
			if ((null != fcePersonDto.getDtBirth()) && (null != fcePersonDto.getDtPersonBirth())
					&& (!fcePersonDto.getDtBirth().equals(fcePersonDto.getDtPersonBirth()))) {
				hasDOBChanged = Boolean.TRUE;
				break;
			} else
				continue;
		}
		return hasDOBChanged;
	}

	/**
	 * Method Name: getPersonDetail Method Description: This method retrieves
	 * Person Info for a given Person within IMPACT
	 * 
	 * @param idMedConsenterPerson
	 * @return PersonInfoDto
	 */
	public PersonInfoDto getPersonDetail(Long idMedConsenterPerson) throws DataNotFoundException {
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(selectPersonDetail);
		query.setParameter("idPerson", idMedConsenterPerson);
		query.addScalar("name", StandardBasicTypes.STRING).addScalar("dob", StandardBasicTypes.DATE)
				.addScalar("age", StandardBasicTypes.LONG).addScalar("dobApprox", StandardBasicTypes.STRING)
				.addScalar("gender", StandardBasicTypes.STRING).addScalar("ssn", StandardBasicTypes.STRING);
		query.setResultTransformer(Transformers.aliasToBean(PersonInfoDto.class));
		List<PersonInfoDto> personInfoDtos = query.list();
		PersonInfoDto personInfoDto = new PersonInfoDto();
		if (TypeConvUtil.isNullOrEmpty(personInfoDtos)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		} else {
			List<String> nameList = new ArrayList<>();
			for (PersonInfoDto personInfo : personInfoDtos) {

				nameList.add(personInfo.getNmPersonFull());
				personInfoDto.setGender(personInfo.getGender());
				personInfoDto.setSsn(personInfo.getSsn());

				if (StringUtil.isValid(personInfo.getAge().toString()) && personInfo.isApprxAge()
						&& Integer.parseInt(personInfo.getAge().toString()) > 0) {
					personInfoDto.setDob(DateUtils.getJavaDateFromAge(personInfo.getAge().intValue()).toString());
					personInfoDto.setApprxAge(true);
				} else if (StringUtil.isValid(personInfo.getDob().toString())) {
					personInfoDto.setDob(personInfo.getDob());
					personInfoDto.setApprxAge(false);
				}

			}

		}
		return personInfoDto;
	}

	public Person findPerson(long personId) {
		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, personId);
		if (person != null) {
			return person;
		} else {
			throw new ServiceLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
	}

	public String findBloc(Long idPerson) {

		return personDao.findBloc(idPerson);
	}

	public static boolean isParent(String cdRelInt) {
		return ((CodesConstant.CRELVICT_PA.equals(cdRelInt)) || (CodesConstant.CRELPRN2_PB.equals(cdRelInt))
				|| (CodesConstant.CRELPRN2_PD.equals(cdRelInt)) || (CodesConstant.CRELPRN2_AP.equals(cdRelInt)));
	}

	public String getPersonFullName(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createQuery(getNmPersonFullSql);
		query.setParameter(0, idPerson);
		return (String) query.uniqueResult();
	}


}
