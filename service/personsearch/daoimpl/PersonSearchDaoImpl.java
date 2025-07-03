package us.tx.state.dfps.service.personsearch.daoimpl;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;

import us.tx.state.dfps.common.domain.PersonId;
import us.tx.state.dfps.common.domain.PersonMerge;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.DatabaseResultDetailsDto;
import us.tx.state.dfps.service.kin.dto.PaginationResultDto;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.PersonAddressDto;
import us.tx.state.dfps.service.person.dto.PersonRelationDto;
import us.tx.state.dfps.service.person.dto.PersonSearchCountDto;
import us.tx.state.dfps.service.person.dto.PersonSearchDetailDto;
import us.tx.state.dfps.service.person.dto.PersonSearchInRecDto;
import us.tx.state.dfps.service.person.dto.PersonSearchNameDto;
import us.tx.state.dfps.service.person.dto.PersonSearchOutRecDto;
import us.tx.state.dfps.service.person.dto.PrsnSearchInRecDto;
import us.tx.state.dfps.service.person.dto.PrsnSearchOutRecArrayDto;
import us.tx.state.dfps.service.person.dto.PrsnSearchRecOutDto;
import us.tx.state.dfps.service.person.dto.PrsnSrchListpInitArrayDto;
import us.tx.state.dfps.service.person.dto.PrsnSrchListpInitDto;
import us.tx.state.dfps.service.personsearch.dao.PersonSearchDao;


/**
 *
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao
 * Interface for functions required for implementing PersonSearch functionality
 * Oct 30, 2017- 6:37:45 PM Â© 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class PersonSearchDaoImpl implements PersonSearchDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonSearchDaoImpl.sqlPersonAddressAddrPersLinkPersAddr}")
	private transient String sqlPersonAddressAddrPersLinkPersAddr;

	@Value("${PersonSearchDaoImpl.sqlPartialSearchNameOnly}")
	private transient String sqlPartialSearchNameOnly;

	@Value("${PersonSearchDaoImpl.sqlParitalSearchPersonUpper}")
	private transient String sqlParitalSearchPersonUpper;

	@Value("${PersonSearchDaoImpl.getPersonDetailAll}")
	private transient String getPersonDetailAll;

	@Value("${PersonSearchDaoImpl.sqlPersonDupExist}")
	private transient String sqlPersonDupExist;

	@Value("${PersonSearchDaoImpl.sqlViewPersonInfo}")
	private transient String sqlViewPersonInfo;

	@Value("${PersonSearchDaoImpl.sqlPersonAndAddressInfo}")
	private transient String sqlPersonAndAddressInfo;

	@Value("${PersonSearchDaoImpl.sqlPersonRelationSelect}")
	private transient String sqlPersonRelationSelect;

	@Value("${PersonSearchDaoImpl.sqlPersonMerge}")
	private transient String sqlPersonMerge;

	@Value("${PersonSearchDaoImpl.sqlPersonViewMerged}")
	private transient String sqlPersonViewMerged;

	@Value("${PersonSearchDaoImpl.sqlSelectPersonViewPerson}")
	private transient String sqlSelectPersonViewPerson;

	@Value("${PersonSearchDaoImpl.sqlPersonAddressAndApproxInd}")
	private transient String sqlPersonAddressAndApproxInd;

	@Value("${PersonSearchDaoImpl.sqlPersonViewPersonId}")
	private transient String sqlPersonViewPersonId;

	@Value("${PersonSearchDaoImpl.sqlPersonViewPersonIdTdhs}")
	private transient String sqlPersonViewPersonIdTdhs;

	@Value("${PersonSearchDaoImpl.sqlPersonViewPersonIdCase}")
	private transient String sqlPersonViewPersonIdCase;

	@Value("${PersonSearchDaoImpl.sqlSelectPersonIdPhoneNbr}")
	private transient String sqlSelectPersonIdPhoneNbr;

	@Value("${PersonSearchDaoImpl.selectPersonDobonlySql}")
	private transient String selectPersonDobonlySql;

	@Value("${PersonSearchDaoImpl.sqlFetchPersonPartialInfo}")
	private transient String sqlFetchPersonPartialInfo;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PersonSearchDaoLog");

	public PersonSearchDaoImpl() {
		// DefaultConstructor
	}

	/**
	 *
	 * Method Name: getPersonIdentifier Method Description: This method gets the
	 * person identifier number for a given person identifier type
	 *
	 * @param idPerson
	 * @param idType
	 * @return String
	 */
	@Override
	public String getPersonIdentifier(Long idPerson, String idType) {

		Date date = new Date(2812, 11, 31);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonId.class)
				.setProjection(Projections.distinct(Projections.property("nbrPersonIdNumber")));
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		criteria.add(Restrictions.eq("cdPersonIdType", idType));
		criteria.add(Restrictions.eq("dtPersonIdEnd", date));
		List<String> nbrPersonIdNumberList = criteria.list();
		if (CollectionUtils.isEmpty(nbrPersonIdNumberList)) {
			return null;
		} else {
			return nbrPersonIdNumberList.get(ServiceConstants.Zero);
		}
	}

	/**
	 *
	 * Method Name: getIdPersonAddress Method Description:This method get person
	 * address details for a person
	 *
	 * @param idPerson
	 * @return AddressValueDto
	 */
	@Override
	public AddressValueDto getIdPersonAddress(Long idPerson) {
		log.debug("Entering method getIdPersonAddress in PersonSearchDaoImpl");
		AddressValueDto addressValue = new AddressValueDto();
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(sqlPersonAddressAddrPersLinkPersAddr).addScalar("personId", StandardBasicTypes.INTEGER)
				.addScalar("streetLn1", StandardBasicTypes.STRING).addScalar("streetLn2", StandardBasicTypes.STRING)
				.addScalar("city", StandardBasicTypes.STRING).addScalar("state", StandardBasicTypes.STRING)
				.addScalar("county", StandardBasicTypes.STRING).addScalar("zip", StandardBasicTypes.STRING)
				.setParameter("PersonId", idPerson)
				.setResultTransformer(Transformers.aliasToBean(AddressValueDto.class));
		List<AddressValueDto> addressValueDtoList = sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(addressValueDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		if (CollectionUtils.isEmpty(addressValueDtoList)) {
			return addressValue;
		}
		if (TypeConvUtil.isNullOrEmpty(addressValueDtoList.get(ServiceConstants.Zero).getStreetLn2()))
			addressValueDtoList.get(ServiceConstants.Zero).setStreetLn2(ServiceConstants.EMPTY_STRING);
		log.debug("Exiting method getIdPersonAddress in PersonSearchDaoImpl");
		return addressValueDtoList.get(ServiceConstants.Zero);
	}

	/**
	 * Method Name: getPersonSearchView Method Description: Returns result of
	 * person view search
	 *
	 * @param personSearchReq
	 * @return PersonSearchOutRecDto
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PersonSearchOutRecDto getPersonSearchView(HashMap personIdentifierValueNumber,
			PaginationResultDto paginationResultDto) {
		log.debug("Entering method getPersonSearchView in PersonSearchDaoImpl");
		List<PrsnSearchRecOutDto> personList = new ArrayList();
		Set personSet = new HashSet();
		PersonSearchOutRecDto personSearchOutRecDto = new PersonSearchOutRecDto();
		HashMap identifierMap = new HashMap();
		ArrayList keys = new ArrayList();

		PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto = new PrsnSearchOutRecArrayDto();

		long nCount = ServiceConstants.Zero;

		String szIdentifierType = ServiceConstants.NULL_CASTOR_DATE;
		String szValue = ServiceConstants.NULL_CASTOR_DATE;

		if (personIdentifierValueNumber.containsKey(ServiceConstants.idPerson)) {
			szIdentifierType = ServiceConstants.idPerson;
			szValue = (String) personIdentifierValueNumber.get(szIdentifierType);
			Long szValueNum = new Long(szValue).longValue();
			PrsnSearchRecOutDto prsnSearchOutRecDto = new PrsnSearchRecOutDto();
			if (isPersonMerged(szValueNum)) {
				prsnSearchOutRecDto.setUlIdPerson(szValueNum);
				prsnSearchOutRecDto.setCwcdIndMerge(ServiceConstants.YES);
				prsnSearchOutRecDto = getPersonDetailMerged(prsnSearchOutRecDto);
			} else {
				prsnSearchOutRecDto = getPersonDetail(szValueNum);
			}
			if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto)
					&& prsnSearchOutRecDto.getUlIdPerson() > ServiceConstants.ZERO_VAL) {
				prsnSearchOutRecDto = populatePersonAddrAndDobApprox(prsnSearchOutRecDto);
				String strSsn = getPersonIdentifier(szValueNum, ServiceConstants.CNUMTYPE_SSN);
				prsnSearchOutRecDto.setSzNbrPersonIdSsn(strSsn);
				prsnSearchOutRecDto.setUsScrIndScore(ServiceConstants.MAX_SCORE);
				if (isPersonInfoViewable(szValueNum)) {
					prsnSearchOutRecDto.setBsysIndViewPersonInfo(ServiceConstants.YES);
				} else {
					prsnSearchOutRecDto.setBsysIndViewPersonInfo(ServiceConstants.NO);
				}
				if (!personSet.contains(prsnSearchOutRecDto.getUlIdPerson())) {
					personSet.add(prsnSearchOutRecDto.getUlIdPerson());
					personList.add(prsnSearchOutRecDto);
				}
			}
			personIdentifierValueNumber.remove(szIdentifierType);
		}
		if (personIdentifierValueNumber.containsKey(ServiceConstants.CNUMTYPE_SSN)) {
			szIdentifierType = ServiceConstants.CNUMTYPE_SSN;
			szValue = (String) personIdentifierValueNumber.get(szIdentifierType);
			identifierMap.put(szIdentifierType, szValue);
			keys.add(szIdentifierType);
			personIdentifierValueNumber.remove(szIdentifierType);
		}
		if (personIdentifierValueNumber.containsKey(ServiceConstants.CNUMTYPE_TDHS_CLIENT_NUMBER)) {
			szIdentifierType = ServiceConstants.CNUMTYPE_TDHS_CLIENT_NUMBER;
			szValue = (String) personIdentifierValueNumber.get(szIdentifierType);
			identifierMap.put(szIdentifierType, szValue);
			keys.add(szIdentifierType);
			personIdentifierValueNumber.remove(szIdentifierType);
		}
		if (!personIdentifierValueNumber.isEmpty()) {
			Iterator iter = personIdentifierValueNumber.keySet().iterator();
			boolean bFlag = false;
			while (iter.hasNext() && !bFlag) {
				szIdentifierType = (String) iter.next();
				if (!ServiceConstants.NBR_PERSON_PHONE_STR.equals(szIdentifierType)) {
					bFlag = true;
					szValue = (String) personIdentifierValueNumber.get(szIdentifierType);
					identifierMap.put(szIdentifierType, szValue);
					keys.add(szIdentifierType);
					personIdentifierValueNumber.remove(szIdentifierType);
				}
			}
		}
		if (!identifierMap.isEmpty()) {
			List<PrsnSearchRecOutDto> prsnList = getPersonDetail(identifierMap, keys);
			Iterator it = prsnList.iterator();
			while (it.hasNext()) {
				PrsnSearchRecOutDto prsnSearchOutRecDto = (PrsnSearchRecOutDto) it.next();
				prsnSearchOutRecDto.setUsScrIndScore(ServiceConstants.MAX_SCORE);
				if (isPersonMerged(prsnSearchOutRecDto.getUlIdPerson())) {
					prsnSearchOutRecDto.setCwcdIndMerge(ServiceConstants.YES);
					prsnSearchOutRecDto = getPersonDetailMerged(prsnSearchOutRecDto);
				}
				if (!personSet.contains(prsnSearchOutRecDto.getUlIdPerson())) {
					personSet.add(prsnSearchOutRecDto.getUlIdPerson());
					personList.add(prsnSearchOutRecDto);
				}
			}
		}
		if (personIdentifierValueNumber.containsKey(ServiceConstants.NBR_PERSON_PHONE_STR)) {
			szIdentifierType = ServiceConstants.NBR_PERSON_PHONE_STR;
			szValue = (String) personIdentifierValueNumber.get(szIdentifierType);
			List<PrsnSearchRecOutDto> prsnList = getPersonDetailByPhoneNumber(szIdentifierType, szValue);
			Iterator it = prsnList.iterator();
			while (it.hasNext()) {
				PrsnSearchRecOutDto prsnSearchOutRecDto = (PrsnSearchRecOutDto) it.next();
				prsnSearchOutRecDto.setUsScrIndScore(ServiceConstants.MAX_SCORE);
				if (isPersonMerged(prsnSearchOutRecDto.getUlIdPerson())) {
					prsnSearchOutRecDto.setCwcdIndMerge(ServiceConstants.YES);
					prsnSearchOutRecDto = getPersonDetailMerged(prsnSearchOutRecDto);
				}
				//artf189542  - This is a closed person with matching phone number, remove this record from personList
				if (!personSet.contains(prsnSearchOutRecDto.getUlIdPerson())
						&& (ObjectUtils.isEmpty(prsnSearchOutRecDto.getMergedUlIdPerson()) ||
						prsnSearchOutRecDto.getUlIdPerson().equals(prsnSearchOutRecDto.getMergedUlIdPerson()))) {
					personSet.add(prsnSearchOutRecDto.getUlIdPerson());
					personList.add(prsnSearchOutRecDto);
				}
			}
			personIdentifierValueNumber.remove(szIdentifierType);
		}
		int listSize = ServiceConstants.AGED_PERSON_AGE;
		if (!TypeConvUtil.isNullOrEmpty(paginationResultDto)) {
			prsnSearchOutRecArrayDto = populatePersonResultsPaginateOutRec(paginationResultDto, personList,
					ServiceConstants.TRUEVAL);
			listSize = prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList().size();
		}
		nCount = listSize;
		if (nCount == 0)
			nCount++;
		prsnSearchOutRecArrayDto.setUlRowQty(nCount);
		personSearchOutRecDto.setPrsnSearchOutRecArrayDto(prsnSearchOutRecArrayDto);

		personSearchOutRecDto = populatePotentialDupIndicator(personSearchOutRecDto);
		populateFamilyTreeRelIndicator(prsnSearchOutRecArrayDto);

		log.debug("Exiting method getPersonSearchView in PersonSearchDaoImpl");
		return personSearchOutRecDto;
	}

	/**
	 *
	 * Method Name: populatePotentialDupIndicator Method Description: populating
	 * the required values in the object
	 *
	 * @param personSearchOutRecDto
	 * @return PersonSearchOutRecDto
	 */
	private PersonSearchOutRecDto populatePotentialDupIndicator(PersonSearchOutRecDto personSearchOutRecDto) {
		log.debug("Entering method populatePotentialDupIndicator in PersonSearchDaoImpl");

		PrsnSearchRecOutDto prsnSearchOutRecDto = new PrsnSearchRecOutDto();
		HashMap<Integer, PrsnSearchRecOutDto> personMap = new HashMap<Integer, PrsnSearchRecOutDto>();
		if(!ObjectUtils.isEmpty(personSearchOutRecDto.getPrsnSearchOutRecArrayDto()
				.getPrsnSearchOutRecDtoList())){
			List<PersonSearchCountDto> personSearchCountDtoList = new ArrayList<>();
			if(ServiceConstants.MAX_SEARCH_ROWS > personSearchOutRecDto.getPrsnSearchOutRecArrayDto()
					.getPrsnSearchOutRecDtoList().size()){
				StringBuffer sb = new StringBuffer();
				HashMap<String, Integer> bindVariablesVector = new HashMap<String, Integer>();
				for (PrsnSearchRecOutDto elem : personSearchOutRecDto.getPrsnSearchOutRecArrayDto()
						.getPrsnSearchOutRecDtoList()) {
					prsnSearchOutRecDto = elem;
					personMap.put(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue(), prsnSearchOutRecDto);
					bindVariablesVector.put("idPerson" + bindVariablesVector.size(),
							new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue());
				}
				Iterator<String> iter = bindVariablesVector.keySet().iterator();
				while (iter.hasNext()) {
					sb.append(":" + iter.next() + ",");
				}
				String placeholder = (sb.toString().length() > ServiceConstants.Zero)
						? sb.toString().substring(0, sb.toString().length() - 1) : ServiceConstants.EMPTY_STR;
				if (placeholder == ServiceConstants.EMPTY_STR) {
					return personSearchOutRecDto;
				}
				String sql = sqlPersonDupExist.replace(":personId", placeholder);
				iter = bindVariablesVector.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					placeholder.replace(key, key + "Dup");
				}
				if (placeholder == ServiceConstants.EMPTY_STR) {
					return personSearchOutRecDto;
				}
				sql = sql.replace(":personDupId", placeholder);
				SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPersonCount", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PersonSearchCountDto.class));
				iter = bindVariablesVector.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					int val = bindVariablesVector.get(key);
					sqlQuery.setParameter(key, val).setParameter(key, val);
				}
				personSearchCountDtoList = sqlQuery.list();
			}else{
				for (int i = 0; i*ServiceConstants.MAX_SEARCH_ROWS < personSearchOutRecDto.getPrsnSearchOutRecArrayDto()
						.getPrsnSearchOutRecDtoList().size(); i++) {
					StringBuffer sb = new StringBuffer();
					HashMap<String, Integer> bindVariablesVector = new HashMap<String, Integer>();
	        		int startIndex = i * ServiceConstants.MAX_SEARCH_ROWS.intValue();
					int maxIndex = ((personSearchOutRecDto.getPrsnSearchOutRecArrayDto()
							.getPrsnSearchOutRecDtoList().size()
							- startIndex) > ServiceConstants.MAX_SEARCH_ROWS)
									? startIndex + ServiceConstants.MAX_SEARCH_ROWS.intValue()
									: personSearchOutRecDto.getPrsnSearchOutRecArrayDto()
									.getPrsnSearchOutRecDtoList().size();
					for (PrsnSearchRecOutDto elem : personSearchOutRecDto.getPrsnSearchOutRecArrayDto()
							.getPrsnSearchOutRecDtoList().subList(startIndex, maxIndex - 1)) {
						prsnSearchOutRecDto = elem;
						personMap.put(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue(), prsnSearchOutRecDto);
						bindVariablesVector.put("idPerson" + bindVariablesVector.size(),
								new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue());
					}
					Iterator<String> iter = bindVariablesVector.keySet().iterator();
					while (iter.hasNext()) {
						sb.append(":" + iter.next() + ",");
					}
					String placeholder = (sb.toString().length() > ServiceConstants.Zero)
							? sb.toString().substring(0, sb.toString().length() - 1) : ServiceConstants.EMPTY_STR;
					if (placeholder == ServiceConstants.EMPTY_STR) {
						return personSearchOutRecDto;
					}
					String sql = sqlPersonDupExist.replace(":personId", placeholder);
					iter = bindVariablesVector.keySet().iterator();
					while (iter.hasNext()) {
						String key = iter.next();
						placeholder.replace(key, key + "Dup");
					}
					if (placeholder == ServiceConstants.EMPTY_STR) {
						return personSearchOutRecDto;
					}
					sql = sql.replace(":personDupId", placeholder);
					SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
							.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPersonCount", StandardBasicTypes.LONG)
							.setResultTransformer(Transformers.aliasToBean(PersonSearchCountDto.class));
					iter = bindVariablesVector.keySet().iterator();
					while (iter.hasNext()) {
						String key = iter.next();
						int val = bindVariablesVector.get(key);
						sqlQuery.setParameter(key, val).setParameter(key, val);
					}
					personSearchCountDtoList.addAll(sqlQuery.list());
				}
			}
			long dupRecCount = ServiceConstants.Zero;
			for (PersonSearchCountDto result : personSearchCountDtoList) {
				prsnSearchOutRecDto = personMap.get(new Long(result.getIdPerson()).intValue());
				dupRecCount = result.getIdPersonCount();
				if (dupRecCount > ServiceConstants.Zero) {
					prsnSearchOutRecDto.setBindPotentialDup(ServiceConstants.Y);
				} else {
					prsnSearchOutRecDto.setBindPotentialDup(ServiceConstants.N);
				}
			}
		}
		log.debug("Exiting method populatePotentialDupIndicator in PersonSearchDaoImpl");
		return personSearchOutRecDto;
	}

	/**
	 * Method Name: getPersonDetailByPhoneNumber Method Description: Returns
	 * details by phone number
	 *
	 * @param szIdentifierType
	 * @param szValue
	 * @return List<PrsnSearchOutRecDto>
	 */
	private List<PrsnSearchRecOutDto> getPersonDetailByPhoneNumber(String szIdentifierType, String szValue) {
		log.debug("Entering method getPersonDetailByPhoneNumber in PersonSearchDaoImpl");
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlSelectPersonIdPhoneNbr)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG).addScalar("ccdPersonSex", StandardBasicTypes.STRING)
				.addScalar("dtDtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("lnbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("szNmPersonFull", StandardBasicTypes.STRING)
				.addScalar("szNbrPersonIdSsn", StandardBasicTypes.STRING)
				.addScalar("bindActiveStatus", StandardBasicTypes.STRING).setParameter("szValue", szValue)
				.setResultTransformer(Transformers.aliasToBean(PrsnSearchRecOutDto.class));
		List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = sqlQuery.list();
		log.debug("Exiting method getPersonDetailByPhoneNumber in PersonSearchDaoImpl");
		return prsnSearchOutRecDtoList;
	}

	/**
	 * Method Name: getPersonDetail Method Description: Returns list of person
	 * details with list of keys
	 *
	 * @param typeValueMap
	 * @param keys
	 * @return List<PrsnSearchOutRecDto>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<PrsnSearchRecOutDto> getPersonDetail(HashMap typeValueMap, List keys) {
		log.debug("Entering method getPersonDetail in PersonSearchDaoImpl");
		StringBuffer sqlStringBuffer = new StringBuffer();
		Iterator keyItr = keys.iterator();
		String szIdentifierType = ServiceConstants.NULL_CASTOR_DATE;
		String szValue = ServiceConstants.NULL_CASTOR_DATE;
		int sqlCnt = ServiceConstants.Zero;
		HashMap<String, String> params = new HashMap<String, String>();
		while (keyItr.hasNext()) {
			szIdentifierType = keyItr.next().toString();
			szValue = typeValueMap.get(szIdentifierType).toString();
			if (sqlCnt > ServiceConstants.Zero) {
				sqlStringBuffer.append(ServiceConstants.SQL_UNION_COMMAND);
			}
			sqlStringBuffer.append(ServiceConstants.SINGLE_WHITESPACE);
			sqlStringBuffer.append(ServiceConstants.PRE_SELECT_FOR_RESOURCE_SERVICE);
			sqlStringBuffer.append(ServiceConstants.SELECT_DISTINCT + sqlCnt);
			sqlStringBuffer.append(ServiceConstants.SINGLE_WHITESPACE);
			sqlStringBuffer.append(ServiceConstants.COMMA);
			sqlStringBuffer.append(ServiceConstants.SINGLE_WHITESPACE);
			if (ServiceConstants.CNUMTYPE_SSN.equalsIgnoreCase(szIdentifierType)) {
				sqlStringBuffer.append(sqlPersonViewPersonId);
				params.put("szIdentifierTypeId", szIdentifierType);
				params.put("szValueId", szValue);
			} else if (ServiceConstants.CNUMTYPE_TDHS_CLIENT_NUMBER.equalsIgnoreCase(szIdentifierType)) {
				sqlStringBuffer.append(sqlPersonViewPersonIdTdhs);
				params.put("szIdentifierTypeTdhs", szIdentifierType);
				params.put("szValueTdhs", szValue);
			} else {
				sqlStringBuffer.append(sqlPersonViewPersonIdCase);
				params.put("szIdentifierTypeCase", szIdentifierType);
				params.put("szValueCase", szValue);
			}
			sqlStringBuffer.append(ServiceConstants.SQL_CLOSE_PAREN_STATEMENT);
			sqlCnt++;
		}
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlStringBuffer.toString())
				.addScalar("ulIdPerson", StandardBasicTypes.LONG).addScalar("ccdPersonSex", StandardBasicTypes.STRING)
				.addScalar("dtDtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("lnbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("szNmPersonFull", StandardBasicTypes.STRING)
				.addScalar("szNbrPersonIdSsn", StandardBasicTypes.STRING)
				.addScalar("bindActiveStatus", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PrsnSearchRecOutDto.class));
		Iterator<String> iter = params.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String val = params.get(key);
			sqlQuery.setParameter(key, val);
		}

		List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = sqlQuery.list();
		log.debug("Exiting method getPersonDetail in PersonSearchDaoImpl");
		return prsnSearchOutRecDtoList;
	}

	/**
	 * Method Name: populatePersonAddrAndDobApprox Method Description: Returns
	 * person's address and DOB
	 *
	 * @param prsnSearchOutRecDto
	 * @return PrsnSearchOutRecDto
	 */
	@SuppressWarnings("unchecked")
	private PrsnSearchRecOutDto populatePersonAddrAndDobApprox(PrsnSearchRecOutDto prsnSearchOutRecDto) {
		log.debug("Entering method populatePersonAddrAndDobApprox in PersonSearchDaoImpl");
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonAddressAndApproxInd)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG)
				.addScalar("szAddrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("szAddrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("szAddrCity", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonState", StandardBasicTypes.STRING)
				.addScalar("szCdCounty", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonZip", StandardBasicTypes.STRING)
				.addScalar("bindPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonCity", StandardBasicTypes.STRING)
				.addScalar("szCdPersonCounty", StandardBasicTypes.STRING)
				.setParameter("idPerson", prsnSearchOutRecDto.getUlIdPerson())
				.setResultTransformer(Transformers.aliasToBean(PrsnSearchRecOutDto.class));
		List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = sqlQuery.list();
		log.debug("Exiting method populatePersonAddrAndDobApprox in PersonSearchDaoImpl");
		PrsnSearchRecOutDto prsnSearchOutRecDtoOut = prsnSearchOutRecDtoList.get(0);
		if (TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzAddrPersAddrStLn1())
				&& TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzAddrPersAddrStLn2())
				&& TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzAddrCity())
				&& TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzCdCounty())) {
			prsnSearchOutRecDto.setSzAddrPersAddrStLn1(prsnSearchOutRecDtoOut.getSzAddrPersonStLn1());
			prsnSearchOutRecDto.setSzAddrCity(prsnSearchOutRecDtoOut.getSzAddrPersonCity());
			prsnSearchOutRecDto.setSzCdCounty(prsnSearchOutRecDtoOut.getSzCdPersonCounty());
		}
		prsnSearchOutRecDto.setSzAddrPersonState(prsnSearchOutRecDtoOut.getSzAddrPersonState());
		prsnSearchOutRecDto.setSzAddrPersonZip(prsnSearchOutRecDtoOut.getSzAddrPersonZip());
		return prsnSearchOutRecDto;
	}

	/**
	 * Method Name: getPersonDetail Method Description: Returns details of
	 * person
	 *
	 * @param idPerson
	 * @return PrsnSearchOutRecDto
	 */
	private PrsnSearchRecOutDto getPersonDetail(long idPerson) {
		log.debug("Entering method getPersonDetail in PersonSearchDaoImpl");
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlSelectPersonViewPerson)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG).addScalar("ccdPersonSex", StandardBasicTypes.STRING)
				.addScalar("dtDtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("lnbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("szNmNameFirst", StandardBasicTypes.STRING)
				.addScalar("szNmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("szNmNameLast", StandardBasicTypes.STRING)
				.addScalar("szNmPersonFull", StandardBasicTypes.STRING)
				.addScalar("bindActiveStatus", StandardBasicTypes.STRING)
				.addScalar("szNbrPersonIdSsn", StandardBasicTypes.STRING)
				.addScalar("bindPersonDobApprox", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PrsnSearchRecOutDto.class));
		List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = sqlQuery.list();
		log.debug("Exiting method getPersonDetail in PersonSearchDaoImpl");
		PrsnSearchRecOutDto prsnSearchOutRecDto = !CollectionUtils.isEmpty(prsnSearchOutRecDtoList)
				? prsnSearchOutRecDtoList.get(ServiceConstants.Zero) : null;
		if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto)
				&& !TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getDtDtPersonBirth())) {
			prsnSearchOutRecDto.setLnbrPersonAge((long) DateUtils.getAge(prsnSearchOutRecDto.getDtDtPersonBirth()));
		}
		return prsnSearchOutRecDto;
	}

	/**
	 * Method Name: getPersonDetailMerged Method Description: Returns merged
	 * person details
	 *
	 * @param prsnSearchOutRecDto
	 * @return PrsnSearchOutRecDto
	 */
	private PrsnSearchRecOutDto getPersonDetailMerged(PrsnSearchRecOutDto prsnSearchOutRecDto) {
		log.debug("Entering method getPersonDetailMerged in PersonSearchDaoImpl");
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonViewMerged)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG).addScalar("ccdPersonSex", StandardBasicTypes.STRING)
				.addScalar("dtDtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("dtDtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("szNmPersonFull", StandardBasicTypes.STRING)
				.addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("bindPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("szNbrPersonIdSsn", StandardBasicTypes.STRING)
				.addScalar("szAddrCity", StandardBasicTypes.STRING)
				.addScalar("szAddrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("szAddrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("szCdCounty", StandardBasicTypes.STRING)
				.addScalar("szNmNameFirst", StandardBasicTypes.STRING)
				.addScalar("szNmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("szNmNameLast", StandardBasicTypes.STRING)
				.addScalar("lnbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("szAddrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonCity", StandardBasicTypes.STRING)
				.addScalar("szCdPersonCounty", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonZip", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonState", StandardBasicTypes.STRING)
				.setParameter("idPerson", prsnSearchOutRecDto.getUlIdPerson())
				.setResultTransformer(Transformers.aliasToBean(PrsnSearchRecOutDto.class));
		List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = sqlQuery.list();
		log.debug("Exiting method getPersonDetailMerged in PersonSearchDaoImpl");
		PrsnSearchRecOutDto prsnSearchOutRecDtoOut = prsnSearchOutRecDtoList.get(0);
		if (TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzAddrPersAddrStLn1())
				&& TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzAddrPersAddrStLn2())
				&& TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzAddrCity())
				&& TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzCdCounty())) {
			prsnSearchOutRecDto.setSzAddrPersAddrStLn1(prsnSearchOutRecDtoOut.getSzAddrPersonStLn1());
			prsnSearchOutRecDto.setSzAddrCity(prsnSearchOutRecDtoOut.getSzAddrPersonCity());
			prsnSearchOutRecDto.setSzCdCounty(prsnSearchOutRecDtoOut.getSzCdPersonCounty());
		}
		prsnSearchOutRecDto.setSzAddrPersonState(prsnSearchOutRecDtoOut.getSzAddrPersonState());
		prsnSearchOutRecDto.setSzAddrPersonZip(prsnSearchOutRecDtoOut.getSzAddrPersonZip());
		prsnSearchOutRecDto.setSzNmNameFirst(prsnSearchOutRecDtoOut.getSzNmNameFirst());
		prsnSearchOutRecDto.setSzNmNameMiddle(prsnSearchOutRecDtoOut.getSzNmNameMiddle());
		prsnSearchOutRecDto.setSzNmNameLast(prsnSearchOutRecDtoOut.getSzNmNameLast());
		prsnSearchOutRecDto.setSzNmIncmgPersFull(formatFullName(prsnSearchOutRecDtoOut.getSzNmNameFirst(),
				prsnSearchOutRecDtoOut.getSzNmNameMiddle(), prsnSearchOutRecDtoOut.getSzNmNameLast()));
		if (prsnSearchOutRecDto.getSzNmIncmgPersFull().equalsIgnoreCase(prsnSearchOutRecDtoOut.getSzNmPersonFull())) {
			prsnSearchOutRecDto.setSzScrCdPersonSearchHit(ServiceConstants.NO);
		} else {
			prsnSearchOutRecDto.setSzScrCdPersonSearchHit(ServiceConstants.YES);
		}
		//artf189542  - This is a closed person with matching phone number, remove this record from personList
		prsnSearchOutRecDto.setMergedUlIdPerson(prsnSearchOutRecDtoOut.getUlIdPerson());
		return prsnSearchOutRecDto;
	}

	/**
	 * Method Name: isPersonMerged Method Description: Checks if person is
	 * merged
	 *
	 * @param idPerson
	 * @return boolean
	 */
	private boolean isPersonMerged(long idPerson) {
		log.debug("Entering method isPersonMerged in PersonSearchDaoImpl");
		boolean bPersonMerged = ServiceConstants.FALSEVAL;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonMerge)
				.setParameter("idPerson", idPerson);
		// .setResultTransformer(Transformers.aliasToBean(Long.class));
		BigDecimal counter = (BigDecimal) sqlQuery.uniqueResult();
		if (counter.intValue() > ServiceConstants.Zero) {
			bPersonMerged = true;
		}
		log.debug("Exiting method isPersonMerged in PersonSearchDaoImpl");
		return bPersonMerged;
	}

	/**
	 * Method Name: performDOBSearch Method Description: Returns result of DOB
	 * search
	 *
	 * @param personSearchReq
	 * @return PersonSearchOutRecDto
	 */
	@Override
	public PersonSearchOutRecDto performDOBSearch(PersonSearchInRecDto personSearchInRecDto,
			PaginationResultDto paginationResultDto) {
		log.debug("Entering method performDOBSearch in PersonSearchDaoImpl");
		PersonSearchOutRecDto personSearchOutRecDto = new PersonSearchOutRecDto();
		PrsnSearchInRecDto prsnSearchInRecDto = personSearchInRecDto.getPrsnSearchInRec();
		StringBuilder sqlBuilder = new StringBuilder(selectPersonDobonlySql);
		HashMap<String, String> params = new HashMap<String, String>();
		if (!TypeConvUtil.isNullOrEmpty(prsnSearchInRecDto.getDtDtPersonBirth())) {
			sqlBuilder.append(ServiceConstants.DOB_PERSON_BIRTH);
			sqlBuilder.append(ServiceConstants.WHERE_CC_DT_CNCNTY_EFFECTIVE_DATE);
			String[] parts = DateUtils.dateString(prsnSearchInRecDto.getDtDtPersonBirth())
					.split(ServiceConstants.ZIP_CODE);
			String formattedDate = parts[ServiceConstants.INDEX_1] + ServiceConstants.ZIP_CODE
					+ parts[ServiceConstants.INDEX_2] + ServiceConstants.ZIP_CODE + parts[ServiceConstants.Zero];
			params.put("dtPersonBirth", formattedDate);
		}
		if (!TypeConvUtil.isNullOrEmpty(prsnSearchInRecDto.getCcdPersonSex())) {
			sqlBuilder.append(ServiceConstants.DOB_PERSON_SEX);
			params.put("cdPersonSex", prsnSearchInRecDto.getCcdPersonSex());
		}
		if (ServiceConstants.Y.equalsIgnoreCase(prsnSearchInRecDto.getBscrAddressChk())) {
			if (!TypeConvUtil.isNullOrEmpty(prsnSearchInRecDto.getSzAddrCity())) {
				sqlBuilder.append(ServiceConstants.DOB_PERSON_ADDR_CITY);
				params.put("addrPersonCity", prsnSearchInRecDto.getSzAddrCity());
			}
			if (!TypeConvUtil.isNullOrEmpty(prsnSearchInRecDto.getLaddrZip())) {
				sqlBuilder.append(ServiceConstants.DOB_PERSON_ADDR_ZIP);
				params.put("addrPersonZip", prsnSearchInRecDto.getLaddrZip() + ServiceConstants.PERCENT);
			}
			if (!TypeConvUtil.isNullOrEmpty(prsnSearchInRecDto.getSzCdAddrCounty())) {
				sqlBuilder.append(ServiceConstants.DOB_PERSON_COUNTY);
				params.put("cdPersonCounty", prsnSearchInRecDto.getSzCdAddrCounty());
			}
			if (!TypeConvUtil.isNullOrEmpty(prsnSearchInRecDto.getSzCdAddrState())) {
				sqlBuilder.append(ServiceConstants.DOB_PERSON_STATE);
				params.put("cdPersonState", prsnSearchInRecDto.getSzCdAddrState());
			}
		}
		sqlBuilder.append(ServiceConstants.DOB_ROWNUM_ORDER);
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString())
				.addScalar("szNmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtDtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("dtDtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("ccdPersonSex", StandardBasicTypes.STRING)
				.addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG)
				.addScalar("bindActiveStatus", StandardBasicTypes.STRING)
				.addScalar("szNbrPersonIdSsn", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonCity", StandardBasicTypes.STRING)
				.addScalar("szCdPersonCounty", StandardBasicTypes.STRING)
				.addScalar("szAddrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("szNmNameFirst", StandardBasicTypes.STRING)
				.addScalar("szNmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("szNmNameLast", StandardBasicTypes.STRING)
				.addScalar("bindNamePrimary", StandardBasicTypes.STRING)
				.addScalar("dtDtNameEndDate", StandardBasicTypes.DATE)
				.addScalar("lnbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("bindPersonDobApprox", StandardBasicTypes.STRING);

		Set<String> keys = params.keySet();
		Iterator<String> iter = keys.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			String val = params.get(key);
			sqlQuery.setParameter(key, val);
		}
		sqlQuery.setResultTransformer(Transformers.aliasToBean(PrsnSearchRecOutDto.class));
		List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = sqlQuery.list();
		PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto = populatePersonResultsPaginateOutRec(paginationResultDto,
				prsnSearchOutRecDtoList, ServiceConstants.TRUEVAL);
		personSearchOutRecDto.setPrsnSearchOutRecArrayDto(prsnSearchOutRecArrayDto);
		log.debug("Exiting method performDOBSearch in PersonSearchDaoImpl");
		return personSearchOutRecDto;
	}

	/**
	 * Method Name: populateAddtnlInfoIntakeSearch Method Description: Populates
	 * additional info from intake search
	 *
	 * @param prsnSrchListpInitArrayDto
	 * @return PrsnSrchListpInitArrayDto
	 */
	@Override
	public PrsnSrchListpInitArrayDto populateAddtnlInfoIntakeSearch(
			PrsnSrchListpInitArrayDto prsnSrchListpInitArrayDto) {
		log.debug("Entering method populateAddtnlInfoIntakeSearch in PersonSearchDaoImpl");

		HashMap<Integer, PrsnSrchListpInitDto> personMap = new HashMap<Integer, PrsnSrchListpInitDto>();
		HashMap<Integer, PrsnSearchRecOutDto> mergedPersonMap = new HashMap<Integer, PrsnSearchRecOutDto>();
		HashMap<Integer, Integer> closedPersonMap = new HashMap<Integer, Integer>();
		StringBuffer sb = new StringBuffer();

		HashMap<String, Integer> bindVariablesVector = new HashMap<String, Integer>();
		List<Integer> bindVariablesVectorMerged = new ArrayList<Integer>();

		PrsnSrchListpInitDto prsnSrchListpInitDto = new PrsnSrchListpInitDto();
		ArrayList<PrsnSrchListpInitDto> recRemovalList = new ArrayList<PrsnSrchListpInitDto>();
		long ulIdForwardPersonMerge = ServiceConstants.LongZero;
		PrsnSearchRecOutDto mergedPersonRec = new PrsnSearchRecOutDto();
		SQLQuery sqlQuery;

		for (PrsnSrchListpInitDto elem : prsnSrchListpInitArrayDto.getPrsnSrchListpInitDto()) {
			prsnSrchListpInitDto = elem;
			ulIdForwardPersonMerge = getForwardPersonInMerge(prsnSrchListpInitDto.getUlIdPerson());
			if (TypeConvUtil.isNullOrEmpty(ulIdForwardPersonMerge)) {
				continue;
			} else {
				bindVariablesVectorMerged.add(new Long(ulIdForwardPersonMerge).intValue());
				closedPersonMap.put(new Long(prsnSrchListpInitDto.getUlIdPerson()).intValue(),
						new Long(ulIdForwardPersonMerge).intValue());
			}
		}
		if (bindVariablesVectorMerged.size() > ServiceConstants.Zero) {
			mergedPersonMap = getPersonDetailAll(bindVariablesVectorMerged);
		}

		for (PrsnSrchListpInitDto elem : prsnSrchListpInitArrayDto.getPrsnSrchListpInitDto()) {
			prsnSrchListpInitDto = elem;
			if (closedPersonMap.containsKey(new Long(prsnSrchListpInitDto.getUlIdPerson()).intValue())
					&& mergedPersonMap.containsKey(
							closedPersonMap.get(new Long(prsnSrchListpInitDto.getUlIdPerson()).intValue()))) {
				String setSzNmPersonFull = prsnSrchListpInitDto.getSzNmPersonFull();
				long mergedPersonId = prsnSrchListpInitDto.getUlIdPerson();
				prsnSrchListpInitDto.setcWcdIndMerge(ServiceConstants.Y);
				mergedPersonRec = mergedPersonMap
						.get(closedPersonMap.get(new Long(prsnSrchListpInitDto.getUlIdPerson()).intValue()));
				prsnSrchListpInitDto.setUlIdPerson(mergedPersonRec.getUlIdPerson());
				prsnSrchListpInitDto.setcCdPersonSex(mergedPersonRec.getCcdPersonSex());
				prsnSrchListpInitDto.setDtDtPersonBirth(mergedPersonRec.getDtDtPersonBirth());
				prsnSrchListpInitDto.setSzCdPersonEthnicGroup(mergedPersonRec.getSzCdPersonEthnicGroup());
				prsnSrchListpInitDto.setSzNbrPersonIdSsn(mergedPersonRec.getSzNbrPersonIdSsn());
				prsnSrchListpInitDto.setSzAddrCity(mergedPersonRec.getSzAddrCity());
				prsnSrchListpInitDto.setSzAddrPersAddrStLn1(mergedPersonRec.getSzAddrPersAddrStLn1());
				prsnSrchListpInitDto.setSzCdCounty(mergedPersonRec.getSzCdCounty());
				prsnSrchListpInitDto.setSzNmIncmgPersFull(mergedPersonRec.getSzNmIncmgPersFull());
				prsnSrchListpInitDto.setSzNmPersonFull(mergedPersonRec.getSzNmPersonFull());
				prsnSrchListpInitDto.setSzScrCdPersonSearchHit(mergedPersonRec.getSzScrCdPersonSearchHit());
				prsnSrchListpInitDto.setbIndActiveStatus(mergedPersonRec.getBindActiveStatus());
				if (!TypeConvUtil.isNullOrEmpty(prsnSrchListpInitDto.getDtDtPersonBirth())) {
					prsnSrchListpInitDto.setlNbrPersonAge(mergedPersonRec.getLnbrPersonAge());
				}
				prsnSrchListpInitDto.setbIndPersonDobApprox(mergedPersonRec.getBindPersonDobApprox());
				if (ServiceConstants.Y.equalsIgnoreCase(prsnSrchListpInitDto.getbIndNameMatch())) {
					prsnSrchListpInitDto.setSzNmPersonFull(setSzNmPersonFull);
				}
				if ((ServiceConstants.VARIABLE.equals(prsnSrchListpInitDto.getSzNmPersonFull())
						|| TypeConvUtil.isNullOrEmpty(prsnSrchListpInitDto.getSzNmPersonFull()))) {
					bindVariablesVectorMerged.clear();
					sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlFetchPersonPartialInfo)
							.addScalar("ulIdPerson", StandardBasicTypes.LONG)
							.addScalar("szNmPersonFull", StandardBasicTypes.STRING)
							.addScalar("szNbrPersonIdSsn", StandardBasicTypes.STRING)
							.addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
							.addScalar("bIndPersonDobApprox", StandardBasicTypes.STRING)
							.setParameter("idPerson", mergedPersonId)
							.setResultTransformer(Transformers.aliasToBean(PrsnSrchListpInitDto.class));
					List<PrsnSrchListpInitDto> prsnSrchListpInitDtoList = sqlQuery.list();
					if(!CollectionUtils.isEmpty(prsnSrchListpInitDtoList)){
						PrsnSrchListpInitDto result = prsnSrchListpInitDtoList.get(ServiceConstants.Zero);
						if (!TypeConvUtil.isNullOrEmpty(result.getSzNmPersonFull())) {
							prsnSrchListpInitDto.setSzNmPersonFull(result.getSzNmPersonFull());
						} else {
							prsnSrchListpInitDto.setSzNmPersonFull(ServiceConstants.COMMA);
						}
					}
				}
				sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonDupExist)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("idPersonCount", StandardBasicTypes.LONG)
						.setParameter("personId", prsnSrchListpInitDto.getUlIdPerson())
						.setParameter("personDupId", prsnSrchListpInitDto.getUlIdPerson())
						.setResultTransformer(Transformers.aliasToBean(PersonSearchCountDto.class));
				List<PersonSearchCountDto> personSearchCountDtoList = sqlQuery.list();
				long dupRecCount = ServiceConstants.Zero;
				for (PersonSearchCountDto result : personSearchCountDtoList) {
					dupRecCount = result.getIdPersonCount();
					if (dupRecCount > ServiceConstants.Zero) {
						prsnSrchListpInitDto.setbIndPotentialDup(ServiceConstants.Y);
					} else {
						prsnSrchListpInitDto.setbIndPotentialDup(ServiceConstants.N);
					}
				}
			}
			if (isPersonInfoViewable(prsnSrchListpInitDto.getUlIdPerson())) {
				prsnSrchListpInitDto.setbSysIndViewPersonInfo(ServiceConstants.Y);
			} else {
				prsnSrchListpInitDto.setbSysIndViewPersonInfo(ServiceConstants.N);
			}
			if (!personMap.containsKey(new Long(prsnSrchListpInitDto.getUlIdPerson()).intValue())) {
				personMap.put(new Long(prsnSrchListpInitDto.getUlIdPerson()).intValue(), prsnSrchListpInitDto);
				if (!ServiceConstants.PERS_CPERSTAT_M.equalsIgnoreCase(prsnSrchListpInitDto.getbIndActiveStatus())) {
					bindVariablesVector.put("idPerson" + bindVariablesVector.size(),
							new Long(prsnSrchListpInitDto.getUlIdPerson()).intValue());
				}
			} else {
				recRemovalList.add(prsnSrchListpInitDto);
			}
		}
		for (PrsnSrchListpInitDto toRemove : recRemovalList) {
			prsnSrchListpInitArrayDto.getPrsnSrchListpInitDto().remove(toRemove);
		}
		if (bindVariablesVector.size() == ServiceConstants.Zero) {
			return null;
		}
		sb = new StringBuffer();
		Iterator<String> iter = bindVariablesVector.keySet().iterator();
		while (iter.hasNext()) {
			sb.append(":" + iter.next() + ",");
		}
		String sql = sqlPersonAndAddressInfo.replace(":personId",
				sb.toString().substring(0, sb.toString().length() - 1));
		sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonAddressDto.class));
		iter = bindVariablesVector.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			int val = bindVariablesVector.get(key);
			sqlQuery.setParameter(key, val);
		}
		List<PersonAddressDto> personAddressDtoList = sqlQuery.list();

		for (PersonAddressDto result : personAddressDtoList) {
			prsnSrchListpInitDto = personMap.get(new Long(result.getIdPerson()).intValue());
			prsnSrchListpInitDto.setSzCdPersonEthnicGroup(result.getCdPersonEthnicGroup());
			prsnSrchListpInitDto.setSzNbrPersonIdSsn(result.getNbrPersonIdNumber());
			prsnSrchListpInitDto.setbIndPersonDobApprox(result.getIndPersonDobApprox());
			if (ServiceConstants.VARIABLE.equals(prsnSrchListpInitDto.getSzNmPersonFull())) {
				if (!TypeConvUtil.isNullOrEmpty(result.getNmPersonFull())) {
					prsnSrchListpInitDto.setSzNmPersonFull(result.getNmPersonFull());
					prsnSrchListpInitDto.setSzNmIncmgPersFull(result.getNmPersonFull());
				} else {
					prsnSrchListpInitDto.setSzNmPersonFull(ServiceConstants.COMMA);
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersAddrStLn1())) {
				prsnSrchListpInitDto.setSzAddrPersAddrStLn1(
						PersonUtil.formatStreetAddress(result.getAddrPersAddrStLn1(), result.getAddrPersAddrStLn2()));
			}
			if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersAddrStLn2())) {
				prsnSrchListpInitDto.setSzAddrPersAddrStLn2(result.getAddrPersAddrStLn2());
			}
			if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersonAddrCity())) {
				prsnSrchListpInitDto.setSzAddrCity(result.getAddrPersonAddrCity());
			}
			if (!TypeConvUtil.isNullOrEmpty(result.getCdPersonAddrCounty())) {
				prsnSrchListpInitDto.setSzCdCounty(result.getCdPersonAddrCounty());
			}
			if (TypeConvUtil.isNullOrEmpty(result.getAddrPersAddrStLn1())
					&& TypeConvUtil.isNullOrEmpty(result.getAddrPersonAddrCity())
					&& TypeConvUtil.isNullOrEmpty(result.getCdPersonAddrCounty())) {
				if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersonCity())) {
					prsnSrchListpInitDto.setSzAddrCity(result.getAddrPersonCity());
				}
				if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersonStLn1())) {
					prsnSrchListpInitDto.setSzAddrPersAddrStLn1(result.getAddrPersonStLn1());
				}
				if (!TypeConvUtil.isNullOrEmpty(result.getCdPersonCounty())) {
					prsnSrchListpInitDto.setSzCdCounty(result.getCdPersonCounty());
				}
			}
		}
		String placeholder = (sb.toString().length() > ServiceConstants.Zero)
				? sb.toString().substring(0, sb.toString().length() - 1) : ServiceConstants.EMPTY_STR;
		sql = sqlPersonDupExist.replace(":personId", placeholder);
		iter = bindVariablesVector.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			placeholder = placeholder.replace(key, key + "Dup");
		}
		sql = sql.replace(":personDupId", placeholder);
		sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPersonCount", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PersonSearchCountDto.class));
		iter = bindVariablesVector.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			int val = bindVariablesVector.get(key);
			sqlQuery.setParameter(key, val).setParameter(key + "Dup", val);
		}
		List<PersonSearchCountDto> personSearchCountDtoList = sqlQuery.list();
		long dupRecCount = ServiceConstants.Zero;
		for (PersonSearchCountDto result : personSearchCountDtoList) {
			prsnSrchListpInitDto = personMap.get(new Long(result.getIdPerson()).intValue());
			dupRecCount = result.getIdPersonCount();
			if (dupRecCount > ServiceConstants.Zero) {
				prsnSrchListpInitDto.setbIndPotentialDup(ServiceConstants.Y);
			} else {
				prsnSrchListpInitDto.setbIndPotentialDup(ServiceConstants.N);
			}
		}
		populateFamilyTreeRelIndicator(prsnSrchListpInitArrayDto);

		log.debug("Exiting method populateAddtnlInfoIntakeSearch in PersonSearchDaoImpl");
		return prsnSrchListpInitArrayDto;
	}

	/**
	 * Method Name: populateAddtnlInfoRegularSearch Method Description:
	 * Populates additional info from regular search
	 *
	 * @param prsnSearchOutRecArrayDto
	 * @return PrsnSearchOutRecArrayDto
	 */
	@Override
	public PrsnSearchOutRecArrayDto populateAddtnlInfoRegularSearch(PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto,
			boolean flag) {
		log.debug("Entering method populateAddtnlInfoRegularSearch in PersonSearchDaoImpl");

		HashMap<Integer, PrsnSearchRecOutDto> personMap = new HashMap<Integer, PrsnSearchRecOutDto>();
		HashMap<Integer, PrsnSearchRecOutDto> mergedPersonMap = new HashMap<Integer, PrsnSearchRecOutDto>();
		HashMap<Integer, Integer> closedPersonMap = new HashMap<Integer, Integer>();

		HashMap<String, Integer> bindVariablesVector = new HashMap<String, Integer>();
		List<Integer> bindVariablesVectorMerged = new ArrayList<Integer>();

		PrsnSearchRecOutDto prsnSearchOutRecDto = new PrsnSearchRecOutDto();
		ArrayList<PrsnSearchRecOutDto> recRemovalList = new ArrayList<PrsnSearchRecOutDto>();
		long ulIdForwardPersonMerge = ServiceConstants.LongZero;
		PrsnSearchRecOutDto mergedPersonRec = new PrsnSearchRecOutDto();
		SQLQuery sqlQuery;
		String sql = ServiceConstants.EMPTY_STR;

		for (PrsnSearchRecOutDto elem : prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList()) {
			prsnSearchOutRecDto = elem;
			ulIdForwardPersonMerge = getForwardPersonInMerge(prsnSearchOutRecDto.getUlIdPerson());
			if (TypeConvUtil.isNullOrEmpty(ulIdForwardPersonMerge)) {
				continue;
			} else {
				bindVariablesVectorMerged.add(new Long(ulIdForwardPersonMerge).intValue());
				closedPersonMap.put(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue(),
						new Long(ulIdForwardPersonMerge).intValue());
			}
		}

		if (bindVariablesVectorMerged.size() > ServiceConstants.Zero) {
			mergedPersonMap = getPersonDetailAll(bindVariablesVectorMerged);
		}

		for (PrsnSearchRecOutDto elem : prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList()) {
			prsnSearchOutRecDto = elem;
			if (closedPersonMap.containsKey(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue()) && mergedPersonMap
					.containsKey(closedPersonMap.get(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue()))) {
				String setSzNmPersonFull = prsnSearchOutRecDto.getSzNmPersonFull();
				long mergedPersonId = prsnSearchOutRecDto.getUlIdPerson();
				prsnSearchOutRecDto.setCwcdIndMerge(ServiceConstants.Y);
				mergedPersonRec = mergedPersonMap
						.get(closedPersonMap.get(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue()));
				prsnSearchOutRecDto.setUlIdPerson(mergedPersonRec.getUlIdPerson());
				prsnSearchOutRecDto.setCcdPersonSex(mergedPersonRec.getCcdPersonSex());
				prsnSearchOutRecDto.setDtDtPersonBirth(mergedPersonRec.getDtDtPersonBirth());
				prsnSearchOutRecDto.setSzCdPersonEthnicGroup(mergedPersonRec.getSzCdPersonEthnicGroup());
				prsnSearchOutRecDto.setSzNbrPersonIdSsn(mergedPersonRec.getSzNbrPersonIdSsn());
				prsnSearchOutRecDto.setSzAddrCity(mergedPersonRec.getSzAddrCity());
				prsnSearchOutRecDto.setSzAddrPersAddrStLn1(mergedPersonRec.getSzAddrPersAddrStLn1());
				prsnSearchOutRecDto.setSzAddrPersAddrStLn2(mergedPersonRec.getSzAddrPersAddrStLn2());
				prsnSearchOutRecDto.setSzCdCounty(mergedPersonRec.getSzCdCounty());
				prsnSearchOutRecDto.setSzNmIncmgPersFull(mergedPersonRec.getSzNmIncmgPersFull());
				prsnSearchOutRecDto.setSzNmPersonFull(mergedPersonRec.getSzNmPersonFull());
				prsnSearchOutRecDto.setSzScrCdPersonSearchHit(mergedPersonRec.getSzScrCdPersonSearchHit());
				prsnSearchOutRecDto.setBindActiveStatus(mergedPersonRec.getBindActiveStatus());
				if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getDtDtPersonBirth())) {
					prsnSearchOutRecDto.setLnbrPersonAge(mergedPersonRec.getLnbrPersonAge());
				}
				prsnSearchOutRecDto.setBindPersonDobApprox(mergedPersonRec.getBindPersonDobApprox());
				if (ServiceConstants.Y.equalsIgnoreCase(prsnSearchOutRecDto.getBindNameMatch())) {
					prsnSearchOutRecDto.setSzNmPersonFull(setSzNmPersonFull);
				}
				if (ServiceConstants.VARIABLE.equals(prsnSearchOutRecDto.getSzNmPersonFull())
						|| TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getSzNmPersonFull())) {
					bindVariablesVectorMerged.clear();
					sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlFetchPersonPartialInfo)
							.addScalar("ulIdPerson", StandardBasicTypes.LONG)
							.addScalar("szNmPersonFull", StandardBasicTypes.STRING)
							.addScalar("szNbrPersonIdSsn", StandardBasicTypes.STRING)
							.addScalar("szCdPersonEthnicGroup", StandardBasicTypes.STRING)
							.addScalar("bindPersonDobApprox", StandardBasicTypes.STRING)
							.setParameter("idPerson", mergedPersonId)
							.setResultTransformer(Transformers.aliasToBean(PrsnSearchRecOutDto.class));
					List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = sqlQuery.list();
					PrsnSearchRecOutDto result = prsnSearchOutRecDtoList.get(ServiceConstants.Zero);
					if (!TypeConvUtil.isNullOrEmpty(result.getSzNmPersonFull())) {
						prsnSearchOutRecDto.setSzNmPersonFull(result.getSzNmPersonFull());
					} else {
						prsnSearchOutRecDto.setSzNmPersonFull(ServiceConstants.COMMA);
					}
				}
				sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonDupExist)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("idPersonCount", StandardBasicTypes.LONG)
						.setParameter("personId", prsnSearchOutRecDto.getUlIdPerson())
						.setParameter("personDupId", prsnSearchOutRecDto.getUlIdPerson())
						.setResultTransformer(Transformers.aliasToBean(PersonSearchCountDto.class));
				List<PersonSearchCountDto> personSearchCountDtoList = sqlQuery.list();
				long dupRecCount = ServiceConstants.Zero;
				for (PersonSearchCountDto result : personSearchCountDtoList) {
					dupRecCount = result.getIdPersonCount();
					if (dupRecCount > ServiceConstants.Zero) {
						prsnSearchOutRecDto.setBindPotentialDup(ServiceConstants.Y);
					} else {
						prsnSearchOutRecDto.setBindPotentialDup(ServiceConstants.N);
					}
				}
			}
			if (isPersonInfoViewable(prsnSearchOutRecDto.getUlIdPerson())) {
				prsnSearchOutRecDto.setBsysIndViewPersonInfo(ServiceConstants.Y);
			} else {
				prsnSearchOutRecDto.setBsysIndViewPersonInfo(ServiceConstants.N);
			}
			if (!personMap.containsKey(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue())) {
				personMap.put(new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue(), prsnSearchOutRecDto);
				if (!ServiceConstants.PERS_CPERSTAT_M.equalsIgnoreCase(prsnSearchOutRecDto.getBindActiveStatus())) {
					bindVariablesVector.put("idPerson" + bindVariablesVector.size(),
							new Long(prsnSearchOutRecDto.getUlIdPerson()).intValue());
				}
			} else {
				recRemovalList.add(prsnSearchOutRecDto);
			}
		}
		for (PrsnSearchRecOutDto toRemove : recRemovalList) {
			prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList().remove(toRemove);
		}
		if (bindVariablesVector.size() == ServiceConstants.Zero) {
			return null;
		}
		if (flag) {
			sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonAndAddressInfo)
					.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
					.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
					.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
					.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
					.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
					.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
					.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
					.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
					.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
					.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
					.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
					.addScalar("addrPersonCity", StandardBasicTypes.STRING)
					.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(PersonAddressDto.class));
			List<PersonAddressDto> personAddressDtoList = new ArrayList<>();
            // Defect#14251: Check the size of bindVariablesVector, if it is > 1000 (which is
            // maximum acceptable no. for an IN query) partition
            // the parameter and execute the query for each subset of size 1000. Else execute in
            // single query.
			if(ServiceConstants.MAX_SEARCH_ROWS < bindVariablesVector.size()) {
				final SQLQuery innerQuery = sqlQuery;
				List<PersonAddressDto> finalPersonAddressDtoList = personAddressDtoList;
				Lists.partition(new ArrayList<>(bindVariablesVector.values()), ServiceConstants.MAX_SEARCH_ROWS.intValue()).forEach(v -> {
					innerQuery.setParameterList("personId", v);
					finalPersonAddressDtoList.addAll(innerQuery.list());
				});
			}else {
				sqlQuery.setParameterList("personId", bindVariablesVector.values());
				personAddressDtoList = sqlQuery.list();
			}

			for (PersonAddressDto result : personAddressDtoList) {
				prsnSearchOutRecDto = personMap.get(new Long(result.getIdPerson()).intValue());
				prsnSearchOutRecDto.setSzCdPersonEthnicGroup(result.getCdPersonEthnicGroup());
				prsnSearchOutRecDto.setSzNbrPersonIdSsn(result.getNbrPersonIdNumber());
				prsnSearchOutRecDto.setBindPersonDobApprox(result.getIndPersonDobApprox());
				if (ServiceConstants.VARIABLE.equals(prsnSearchOutRecDto.getSzNmPersonFull())) {
					if (!TypeConvUtil.isNullOrEmpty(result.getNmPersonFull())) {
						prsnSearchOutRecDto.setSzNmPersonFull(result.getNmPersonFull());
						prsnSearchOutRecDto.setSzNmIncmgPersFull(result.getNmPersonFull());
					} else {
						prsnSearchOutRecDto.setSzNmPersonFull(ServiceConstants.COMMA);
					}
				}
				if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersAddrStLn1())) {
					prsnSearchOutRecDto.setSzAddrPersAddrStLn1(PersonUtil
							.formatStreetAddress(result.getAddrPersAddrStLn1(), result.getAddrPersAddrStLn2()));
				}
				if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersAddrStLn2())) {
					prsnSearchOutRecDto.setSzAddrPersAddrStLn2(result.getAddrPersAddrStLn2());
				}
				if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersonAddrCity())) {
					prsnSearchOutRecDto.setSzAddrCity(result.getAddrPersonAddrCity());
				}
				if (!TypeConvUtil.isNullOrEmpty(result.getCdPersonAddrCounty())) {
					prsnSearchOutRecDto.setSzCdCounty(result.getCdPersonAddrCounty());
				}
				if (TypeConvUtil.isNullOrEmpty(result.getAddrPersAddrStLn1())
						&& TypeConvUtil.isNullOrEmpty(result.getAddrPersonAddrCity())
						&& TypeConvUtil.isNullOrEmpty(result.getCdPersonAddrCounty())) {
					if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersonCity())) {
						prsnSearchOutRecDto.setSzAddrCity(result.getAddrPersonCity());
					}
					if (!TypeConvUtil.isNullOrEmpty(result.getAddrPersonStLn1())) {
						prsnSearchOutRecDto.setSzAddrPersAddrStLn1(result.getAddrPersonStLn1());
					}
					if (!TypeConvUtil.isNullOrEmpty(result.getCdPersonCounty())) {
						prsnSearchOutRecDto.setSzCdCounty(result.getCdPersonCounty());
					}
				}
			}
		}

        // Defect#14251: Modified the code to directly pass the personIds in the query using
        // hibernate's parameterList.
		sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonDupExist)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPersonCount", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PersonSearchCountDto.class));
		List<PersonSearchCountDto> personSearchCountDtoList = new ArrayList<>();
        // Defect#14251: Check the size of bindVariablesVector, if it is > 1000 (which is maximum
        // acceptable no. for an IN query) partition
        // the parameter and execute the query for each subset of size 1000. Else execute in single
        // query.
		if(ServiceConstants.MAX_SEARCH_ROWS < bindVariablesVector.size()) {
			final SQLQuery innerQuery = sqlQuery;
			List<PersonSearchCountDto> finalpersonSearchCountDtoList = personSearchCountDtoList;
			Lists.partition(new ArrayList<>(bindVariablesVector.values()), ServiceConstants.MAX_SEARCH_ROWS.intValue()).forEach(personId -> {
				innerQuery.setParameterList("personId", personId).setParameterList("personDupId",personId);
				finalpersonSearchCountDtoList.addAll(innerQuery.list());
			});
		} else{
			sqlQuery.setParameterList("personId",bindVariablesVector.values()).setParameterList("personDupId",bindVariablesVector.values());
			personSearchCountDtoList = sqlQuery.list();
		}

		long dupRecCount = ServiceConstants.Zero;
		for (PersonSearchCountDto result : personSearchCountDtoList) {
			prsnSearchOutRecDto = personMap.get(new Long(result.getIdPerson()).intValue());
			dupRecCount = result.getIdPersonCount();
			if (dupRecCount > ServiceConstants.Zero) {
				prsnSearchOutRecDto.setBindPotentialDup(ServiceConstants.Y);
			} else {
				prsnSearchOutRecDto.setBindPotentialDup(ServiceConstants.N);
			}
		}
		populateFamilyTreeRelIndicator(prsnSearchOutRecArrayDto);

		log.debug("Exiting method populateAddtnlInfoRegularSearch in PersonSearchDaoImpl");
		return prsnSearchOutRecArrayDto;
	}

	/**
	 *
	 * Method Name: performPartialSearch Method Description: This method returns
	 * result of Partial Search
	 *
	 * @param personSearchInRecDto
	 * @param paginationResultDto
	 * @return PersonSearchOutRecDto
	 */
	@Override
	public PersonSearchOutRecDto performPartialSearch(PersonSearchInRecDto personSearchInRecDto,
			PaginationResultDto paginationResultDto) {

		log.debug("Entering method performPartialSearch in PersonSearchDaoImpl");
		PersonSearchOutRecDto personSearchOutRecDto = new PersonSearchOutRecDto();
		PrsnSearchInRecDto prsnSearchInRecDto = personSearchInRecDto.getPrsnSearchInRec();
		String partialFName = new String();
		String partialLName = new String();
		boolean nameOnlySearch = ServiceConstants.TRUEVAL;
		boolean flagAND = ServiceConstants.FALSEVAL;
		StringBuilder partialSql = new StringBuilder(sqlParitalSearchPersonUpper);
		partialSql.append(ServiceConstants.WHERE);
		partialSql.append(ServiceConstants.SPACE);
		if (ServiceConstants.Y.equals(prsnSearchInRecDto.getScrPartlNameChk())
				&& isValid(prsnSearchInRecDto.getCcdPersonSex())) {
			partialSql.append(ServiceConstants.CD_PERSON_SEX);
			if (ServiceConstants.UNKNOWN1.equals(prsnSearchInRecDto.getCcdPersonSex())) {
				partialSql.append(ServiceConstants.EMPTY_CHAR + ServiceConstants.UNKNOWN1 + ServiceConstants.EMPTY_CHAR
						+ ServiceConstants.COMMA);
				partialSql.append(ServiceConstants.EMPTY_CHAR + ServiceConstants.MERGE + ServiceConstants.EMPTY_CHAR
						+ ServiceConstants.COMMA);
				partialSql.append(
						ServiceConstants.EMPTY_CHAR + ServiceConstants.FULL_UNITS_VIEW + ServiceConstants.EMPTY_CHAR);
			} else {
				partialSql.append(ServiceConstants.EMPTY_CHAR + ServiceConstants.UNKNOWN1 + ServiceConstants.EMPTY_CHAR
						+ ServiceConstants.COMMA);
				partialSql.append(ServiceConstants.EMPTY_CHAR + prsnSearchInRecDto.getCcdPersonSex()
						+ ServiceConstants.EMPTY_CHAR);
			}
			partialSql.append(ServiceConstants.BRACKET);
			nameOnlySearch = ServiceConstants.FALSEVAL;
			flagAND = ServiceConstants.TRUEVAL;
		}

		if (ServiceConstants.Y.equals(prsnSearchInRecDto.getScrPartlNameChk())
				&& !TypeConvUtil.isNullOrEmpty(prsnSearchInRecDto.getDtDtPersonBirth())) {

			partialSql = flagAND ? partialSql.append(ServiceConstants.SPACE + ServiceConstants.AND) : partialSql;
			partialSql.append(ServiceConstants.DT_PERSON_BIRTH1 + DateUtils.getDateFormat().replaceAll("\\?",
					ServiceConstants.EMPTY_CHAR
							+ DateUtils.dateStringInSlashFormat(prsnSearchInRecDto.getDtDtPersonBirth())
							+ ServiceConstants.EMPTY_CHAR));
			nameOnlySearch = ServiceConstants.FALSEVAL;
			flagAND = ServiceConstants.TRUEVAL;
		} else if (ServiceConstants.Y.equals(prsnSearchInRecDto.getScrPartlNameChk())
				&& prsnSearchInRecDto.getLnbrPersonAge() > ServiceConstants.LongZero) {
			Date currentDate = new Date();
			Date approximateDOB = DateUtils.addToDate(currentDate, -prsnSearchInRecDto.getLnbrPersonAge().intValue(),
					ServiceConstants.Zero, ServiceConstants.Zero);
			Date startDate = null;
			Date endDate = null;
			int nbrOfYearsRange = ServiceConstants.Zero;
			if (prsnSearchInRecDto.getLnbrPersonAge() >= ServiceConstants.EIGHTEEN) {
				nbrOfYearsRange = ServiceConstants.TEN;
			} else {
				nbrOfYearsRange = ServiceConstants.Three;
			}

			startDate = DateUtils.addToDate(approximateDOB, -(nbrOfYearsRange + ServiceConstants.One),
					ServiceConstants.Zero, ServiceConstants.Zero);
			endDate = DateUtils.addToDate(approximateDOB, nbrOfYearsRange, ServiceConstants.Zero,
					ServiceConstants.Zero);
			partialSql = flagAND ? partialSql.append(ServiceConstants.SPACE + ServiceConstants.AND) : partialSql;
			partialSql.append(ServiceConstants.DT_PERSON_BIRTH2);

			partialSql.append(DateUtils.getDateFormat().replaceAll("?", startDate.toString()));

			partialSql.append(ServiceConstants.DT_PERSON_BIRTH3);
			partialSql.append(DateUtils.getDateFormat().replaceAll("?", endDate.toString()));

			nameOnlySearch = ServiceConstants.FALSEVAL;
			flagAND = ServiceConstants.TRUEVAL;
		}

		if (ServiceConstants.Y.equalsIgnoreCase(prsnSearchInRecDto.getBscrAddressChk())) {
			if (isValid(prsnSearchInRecDto.getSzAddrPersAddrStLn1())) {
				partialSql = flagAND ? partialSql.append(ServiceConstants.SPACE + ServiceConstants.AND) : partialSql;
				partialSql.append(ServiceConstants.ADDR_PERSON_ST_LN_1);
				partialSql.append(ServiceConstants.EMPTY_CHAR
						+ prsnSearchInRecDto.getSzAddrPersAddrStLn1().toUpperCase() + ServiceConstants.EMPTY_CHAR);
				flagAND = ServiceConstants.TRUEVAL;
			}

			if (isValid(prsnSearchInRecDto.getSzAddrCity())) {
				partialSql = flagAND ? partialSql.append(ServiceConstants.SPACE + ServiceConstants.AND) : partialSql;
				partialSql.append(ServiceConstants.ADDR_PERSON_CITY);
				partialSql.append(ServiceConstants.EMPTY_CHAR + prsnSearchInRecDto.getSzAddrCity().toUpperCase()
						+ ServiceConstants.EMPTY_CHAR);
				flagAND = ServiceConstants.TRUEVAL;
			}

			if (isValid(prsnSearchInRecDto.getLaddrZip())) {
				partialSql = flagAND ? partialSql.append(ServiceConstants.SPACE + ServiceConstants.AND) : partialSql;
				String addrZip = ServiceConstants.EMPTY_STRING;
				partialSql.append(ServiceConstants.ADDR_PERSON_ZIP);

				if (prsnSearchInRecDto.getLaddrZip().length() > 5) {
					addrZip = ServiceConstants.EMPTY_CHAR
							+ prsnSearchInRecDto.getLaddrZip().substring(ServiceConstants.Zero, 5)
							+ ServiceConstants.PERCENT + ServiceConstants.EMPTY_CHAR;
				} else {
					addrZip = prsnSearchInRecDto.getLaddrZip()
							.startsWith(ServiceConstants.SPACE + ServiceConstants.ZIP_CODE)
									? (ServiceConstants.SPACE + ServiceConstants.EMPTY_CHAR + ServiceConstants.PERCENT)
											+ prsnSearchInRecDto.getLaddrZip() + ServiceConstants.EMPTY_CHAR
									: ServiceConstants.EMPTY_CHAR + prsnSearchInRecDto.getLaddrZip()
											+ ServiceConstants.EMPTY_CHAR;
				}
				partialSql.append(addrZip);
				flagAND = ServiceConstants.TRUEVAL;
			}

			if (isValid(prsnSearchInRecDto.getSzCdAddrCounty())) {

				partialSql = flagAND ? partialSql.append(ServiceConstants.SPACE + ServiceConstants.AND) : partialSql;
				partialSql.append(ServiceConstants.CD_PERSON_COUNTY);
				partialSql.append(ServiceConstants.EMPTY_CHAR + prsnSearchInRecDto.getSzCdAddrCounty()
						+ ServiceConstants.EMPTY_CHAR);
				flagAND = ServiceConstants.TRUEVAL;
			}

			if (isValid(prsnSearchInRecDto.getSzCdAddrState())) {
				partialSql = flagAND ? partialSql.append(ServiceConstants.SPACE + ServiceConstants.AND) : partialSql;
				partialSql.append(ServiceConstants.CD_PERSON_STATE);
				partialSql.append(ServiceConstants.EMPTY_CHAR + prsnSearchInRecDto.getSzCdAddrState()
						+ ServiceConstants.EMPTY_CHAR);
				flagAND = ServiceConstants.TRUEVAL;
			}

			nameOnlySearch = ServiceConstants.FALSEVAL;
		}

		partialSql.append(ServiceConstants.SQL_PARITAL_SEARCH_PERSON_LOWER);
		if (ServiceConstants.Y.equals(prsnSearchInRecDto.getScrPartlNameChk())
				&& isValid(prsnSearchInRecDto.getSzNmNameLast())) {
			partialLName = ServiceConstants.EMPTY_STRING;
			partialSql.append(ServiceConstants.NM_NAME_LAST);
			partialLName = prsnSearchInRecDto.getSzNmNameLast() + ServiceConstants.PERCENT;
		}

		if (ServiceConstants.Y.equals(prsnSearchInRecDto.getScrPartlNameChk())
				&& isValid(prsnSearchInRecDto.getSzNmNameFirst())) {
			partialFName = ServiceConstants.EMPTY_CHAR;
			partialSql.append(ServiceConstants.NM_NAME_FIRST);
			partialFName = prsnSearchInRecDto.getSzNmNameFirst() + ServiceConstants.PERCENT;

		}

		partialSql.append(ServiceConstants.AND_ROWNUM);

		if (nameOnlySearch) {
			partialSql.delete(ServiceConstants.Zero, partialSql.length());
			partialSql.append(sqlPartialSearchNameOnly);
		}
		List<PersonSearchNameDto> personSearchNameDtoList = null;
		if (!StringUtils.isEmpty(partialLName) && !StringUtils.isEmpty(partialFName)) {
			SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(partialSql.toString())
					.addScalar("noofrow", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
					.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
					.addScalar("cdPersonSex", StandardBasicTypes.STRING)
					.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
					.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
					.addScalar("addrPersonCity", StandardBasicTypes.STRING)
					.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
					.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
					.addScalar("addrPersonZip", StandardBasicTypes.STRING)
					.addScalar("cdPersonState", StandardBasicTypes.STRING)
					.addScalar("nbrPersonAge", StandardBasicTypes.LONG)
					.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
					.addScalar("nmNameFirst", StandardBasicTypes.STRING)
					.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
					.addScalar("nmNameLast", StandardBasicTypes.STRING).setParameter("nmNameLast", partialLName)
					.setParameter("nmNameFirst", partialFName)
					.setResultTransformer(Transformers.aliasToBean(PersonSearchNameDto.class));

			personSearchNameDtoList = sQLQuery1.list();
		} else {
			SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(partialSql.toString())
					.addScalar("noofrow", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
					.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
					.addScalar("cdPersonSex", StandardBasicTypes.STRING)
					.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
					.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
					.addScalar("addrPersonCity", StandardBasicTypes.STRING)
					.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
					.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
					.addScalar("addrPersonZip", StandardBasicTypes.STRING)
					.addScalar("cdPersonState", StandardBasicTypes.STRING)
					.addScalar("nbrPersonAge", StandardBasicTypes.LONG)
					.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
					.addScalar("nmNameFirst", StandardBasicTypes.STRING)
					.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
					.addScalar("nmNameLast", StandardBasicTypes.STRING)
					.setResultTransformer(Transformers.aliasToBean(PersonSearchNameDto.class));

			personSearchNameDtoList = sQLQuery1.list();
		}

		if (TypeConvUtil.isNullOrEmpty(personSearchNameDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		log.debug("Exiting method performPartialSearch in PersonSearchDaoImpl");

		PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto = populatePersonResultsPaginate(paginationResultDto,
				personSearchNameDtoList, ServiceConstants.TRUEVAL);
		personSearchOutRecDto.setPrsnSearchOutRecArrayDto(prsnSearchOutRecArrayDto);

		return personSearchOutRecDto;

	}

	/**
	 *
	 * Method Name: populatePersonResultsPaginate Method Description:populating
	 * person results for pagination.
	 *
	 * @param paginationResultDto
	 * @param personSearchNameDtoList
	 * @param flag
	 * @return PrsnSearchOutRecArrayDto
	 */
	private PrsnSearchOutRecArrayDto populatePersonResultsPaginate(PaginationResultDto paginationResultDto,
			List<PersonSearchNameDto> personSearchNameDtoList, boolean flag) {
		log.debug("Entering method populatePersonResultsPaginate in PersonSearchDaoImpl");
		PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto = new PrsnSearchOutRecArrayDto();
		Long resultCount = ServiceConstants.LongZero;
		Long lastResult = ServiceConstants.LongZero;
		DatabaseResultDetailsDto databaseResultDetailsDto = paginationResultDto.getResultDetails();
		databaseResultDetailsDto.setNumberOfResults(personSearchNameDtoList.size());

		int resultPages = (int) (databaseResultDetailsDto.getNumberOfResults()
				/ databaseResultDetailsDto.getResultsPerPage());
		int remainder = (int) (databaseResultDetailsDto.getNumberOfResults()
				% databaseResultDetailsDto.getResultsPerPage());
		if (remainder > ServiceConstants.Zero) {
			resultPages++;
		}
		if (paginationResultDto.getResultDetails().getRequestedPage() > resultPages) {

			paginationResultDto.getResultDetails().setRequestedPage(resultPages);
			databaseResultDetailsDto.setNumberOfResults(personSearchNameDtoList.size());

			int tempLastResult = (int) (databaseResultDetailsDto.getRequestedPage()
					* databaseResultDetailsDto.getResultsPerPage());
			lastResult = Math.min(tempLastResult, databaseResultDetailsDto.getNumberOfResults());

		} else {
			int tempLastResult = (int) (databaseResultDetailsDto.getRequestedPage()
					* databaseResultDetailsDto.getResultsPerPage());
			lastResult = Math.min(tempLastResult, databaseResultDetailsDto.getNumberOfResults());
		}
		ArrayList<PrsnSearchRecOutDto> prsnSearchOutRecDtoList = new ArrayList<>();
		if (!TypeConvUtil.isNullOrEmpty(personSearchNameDtoList) && lastResult > ServiceConstants.Zero) {
			for (PersonSearchNameDto personSearchNameDto : personSearchNameDtoList) {
				PrsnSearchRecOutDto prsnSearchOutRecDto = new PrsnSearchRecOutDto();

				prsnSearchOutRecDto.setUlIdPerson(personSearchNameDto.getIdPerson());
				prsnSearchOutRecDto.setCcdPersonSex(personSearchNameDto.getCdPersonSex());
				prsnSearchOutRecDto.setDtDtPersonBirth(personSearchNameDto.getDtPersonBirth());
				prsnSearchOutRecDto.setSzCdPersonEthnicGroup(personSearchNameDto.getCdPersonEthnicGroup());
				prsnSearchOutRecDto.setLnbrPersonAge(personSearchNameDto.getNbrPersonAge());
				prsnSearchOutRecDto.setSzNmNameFirst(personSearchNameDto.getNmNameFirst());
				prsnSearchOutRecDto.setSzNmNameMiddle(personSearchNameDto.getNmNameMiddle());
				prsnSearchOutRecDto.setSzNmNameLast(personSearchNameDto.getNmNameLast());

				if (flag) {
					prsnSearchOutRecDto.setSzNmPersonFull(formatFullName(personSearchNameDto.getNmNameFirst(),
							personSearchNameDto.getNmNameMiddle(), personSearchNameDto.getNmNameLast()));
					prsnSearchOutRecDto.setSzNmIncmgPersFull(personSearchNameDto.getNmPersonFull());

					if (prsnSearchOutRecDto.getSzNmIncmgPersFull()
							.equalsIgnoreCase(prsnSearchOutRecDto.getSzNmPersonFull())) {
						prsnSearchOutRecDto.setSzScrCdPersonSearchHit(ServiceConstants.N);
					} else {
						prsnSearchOutRecDto.setSzScrCdPersonSearchHit(ServiceConstants.Y);
					}

					prsnSearchOutRecDto.setBindNameMatch(ServiceConstants.Y);

					prsnSearchOutRecDto.setSzAddrPersAddrStLn1(personSearchNameDto.getAddrPersonStLn1());
					prsnSearchOutRecDto.setSzAddrCity(personSearchNameDto.getAddrPersonCity());
					prsnSearchOutRecDto.setSzCdCounty(personSearchNameDto.getCdPersonCounty());
					prsnSearchOutRecDto.setSzAddrPersonState(personSearchNameDto.getCdPersonState());
					prsnSearchOutRecDto.setSzAddrPersonZip(personSearchNameDto.getAddrPersonZip());

				} else {
					prsnSearchOutRecDto.setSzNmPersonFull(personSearchNameDto.getNmPersonFull());
					prsnSearchOutRecDto.setSzNmIncmgPersFull(personSearchNameDto.getNmPersonFull());
				}

				prsnSearchOutRecDto.setBindActiveStatus(personSearchNameDto.getCdPersonStatus());

				prsnSearchOutRecDto.setBindPersonDobApprox(personSearchNameDto.getIndPersonDobApprox());

				if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getDtDtPersonBirth())) {
					prsnSearchOutRecDto
							.setLnbrPersonAge((long) DateUtils.getAge(prsnSearchOutRecDto.getDtDtPersonBirth()));
				}
				prsnSearchOutRecDtoList.add(prsnSearchOutRecDto);
			}
			prsnSearchOutRecArrayDto.setPrsnSearchOutRecDtoList(prsnSearchOutRecDtoList);

		}

		prsnSearchOutRecArrayDto.setUlRowQty(resultCount);
		populateAddtnlInfoRegularSearch(prsnSearchOutRecArrayDto, flag);
		log.debug("Exiting method populatePersonResultsPaginate in PersonSearchDaoImpl");
		return prsnSearchOutRecArrayDto;
	}

	/**
	 *
	 * Method Name: populatePersonResultsPaginate Method Description:populating
	 * person results for pagination.
	 *
	 * @param paginationResultDto
	 * @param prsnSearchOutRecDtoList
	 * @param flag
	 * @return PrsnSearchOutRecArrayDto
	 */
	private PrsnSearchOutRecArrayDto populatePersonResultsPaginateOutRec(PaginationResultDto paginationResultDto,
			List<PrsnSearchRecOutDto> prsnSearchOutRecDtoList, boolean flag) {
		log.debug("Entering method populatePersonResultsPaginate in PersonSearchDaoImpl");
		PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto = new PrsnSearchOutRecArrayDto();
		Long resultCount = ServiceConstants.LongZero;
		Long lastResult = ServiceConstants.LongZero;
		DatabaseResultDetailsDto databaseResultDetailsDto = paginationResultDto.getResultDetails();
		databaseResultDetailsDto.setNumberOfResults(prsnSearchOutRecDtoList.size());

		int resultPages = (int) (databaseResultDetailsDto.getNumberOfResults()
				/ databaseResultDetailsDto.getResultsPerPage());
		int remainder = (int) (databaseResultDetailsDto.getNumberOfResults()
				% databaseResultDetailsDto.getResultsPerPage());
		if (remainder > ServiceConstants.Zero) {
			resultPages++;
		}
		if (paginationResultDto.getResultDetails().getRequestedPage() > resultPages) {

			paginationResultDto.getResultDetails().setRequestedPage(resultPages);
			databaseResultDetailsDto.setNumberOfResults(prsnSearchOutRecDtoList.size());

			int tempLastResult = (int) (databaseResultDetailsDto.getRequestedPage()
					* databaseResultDetailsDto.getResultsPerPage());
			lastResult = Math.min(tempLastResult, databaseResultDetailsDto.getNumberOfResults());

		} else {
			int tempLastResult = (int) (databaseResultDetailsDto.getRequestedPage()
					* databaseResultDetailsDto.getResultsPerPage());
			lastResult = Math.min(tempLastResult, databaseResultDetailsDto.getNumberOfResults());
		}
		if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDtoList) && lastResult >= ServiceConstants.Zero) {
			List<PrsnSearchRecOutDto> searchOutRecDtoList = new ArrayList<>();
			for (PrsnSearchRecOutDto prsnSearchOutRecDto : prsnSearchOutRecDtoList) {
				PrsnSearchRecOutDto searchOutRecDto = new PrsnSearchRecOutDto();

				searchOutRecDto.setUlIdPerson(prsnSearchOutRecDto.getUlIdPerson());
				searchOutRecDto.setCcdPersonSex(prsnSearchOutRecDto.getCcdPersonSex());
				searchOutRecDto.setDtDtPersonBirth(prsnSearchOutRecDto.getDtDtPersonBirth());
				searchOutRecDto.setSzCdPersonEthnicGroup(prsnSearchOutRecDto.getSzCdPersonEthnicGroup());
				searchOutRecDto.setLnbrPersonAge(prsnSearchOutRecDto.getLnbrPersonAge());
				searchOutRecDto.setUsScrIndScore(prsnSearchOutRecDto.getUsScrIndScore());
				searchOutRecDto.setSzNmNameFirst(prsnSearchOutRecDto.getSzNmNameFirst());
				searchOutRecDto.setSzNmNameMiddle(prsnSearchOutRecDto.getSzNmNameMiddle());
				searchOutRecDto.setSzNmNameLast(prsnSearchOutRecDto.getSzNmNameLast());

				if (flag) {
					if (StringUtils.isNotEmpty(prsnSearchOutRecDto.getSzNmPersonFull())) {
						searchOutRecDto.setSzNmPersonFull(prsnSearchOutRecDto.getSzNmPersonFull());
					} else {
						searchOutRecDto.setSzNmPersonFull(formatFullName(prsnSearchOutRecDto.getSzNmNameFirst(),
								prsnSearchOutRecDto.getSzNmNameMiddle(), prsnSearchOutRecDto.getSzNmNameLast()));
					}
					searchOutRecDto.setSzNmIncmgPersFull(prsnSearchOutRecDto.getSzNmPersonFull());

					if (StringUtils.isNotEmpty(searchOutRecDto.getSzNmIncmgPersFull())
							&& StringUtils.isNotEmpty(searchOutRecDto.getSzNmPersonFull()) && searchOutRecDto
									.getSzNmIncmgPersFull().equalsIgnoreCase(searchOutRecDto.getSzNmPersonFull())) {
						searchOutRecDto.setSzScrCdPersonSearchHit(ServiceConstants.N);
					} else {
						searchOutRecDto.setSzScrCdPersonSearchHit(ServiceConstants.Y);
					}

					searchOutRecDto.setBindNameMatch(ServiceConstants.Y);

					searchOutRecDto.setSzAddrPersAddrStLn1(prsnSearchOutRecDto.getSzAddrPersonStLn1());
					searchOutRecDto.setSzAddrCity(prsnSearchOutRecDto.getSzAddrPersonCity());
					searchOutRecDto.setSzCdCounty(prsnSearchOutRecDto.getSzCdPersonCounty());
					searchOutRecDto.setSzAddrPersonZip(prsnSearchOutRecDto.getSzAddrPersonZip());
					searchOutRecDto.setSzAddrPersonState(prsnSearchOutRecDto.getSzAddrPersonState());
					searchOutRecDto.setSzNbrPersonIdSsn(prsnSearchOutRecDto.getSzNbrPersonIdSsn());

				} else {
					searchOutRecDto.setSzNmPersonFull(prsnSearchOutRecDto.getSzNmPersonFull());
					searchOutRecDto.setSzNmIncmgPersFull(prsnSearchOutRecDto.getSzNmPersonFull());
				}

				searchOutRecDto.setBindActiveStatus(prsnSearchOutRecDto.getBindActiveStatus());

				searchOutRecDto.setBindPersonDobApprox(prsnSearchOutRecDto.getBindPersonDobApprox());

				if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto.getDtDtPersonBirth())) {
					searchOutRecDto.setLnbrPersonAge((long) DateUtils.getAge(prsnSearchOutRecDto.getDtDtPersonBirth()));
				}
				int count = resultCount.intValue();
				searchOutRecDtoList.add(count++, searchOutRecDto);
			}
			if(!ObjectUtils.isEmpty(searchOutRecDtoList)){
				searchOutRecDtoList
						.sort(comparing(PrsnSearchRecOutDto::getSzNmIncmgPersFull, nullsLast(naturalOrder())));
			}
			prsnSearchOutRecArrayDto.setPrsnSearchOutRecDtoList(searchOutRecDtoList);

		}

		prsnSearchOutRecArrayDto.setUlRowQty(resultCount);
		populateAddtnlInfoRegularSearch(prsnSearchOutRecArrayDto, flag);
		log.debug("Exiting method populatePersonResultsPaginate in PersonSearchDaoImpl");
		return prsnSearchOutRecArrayDto;
	}

	/**
	 *
	 * Method Name: formatFullName Method Description: Combines up to the first
	 * 14 characters of the last name, a comma, the first 8 of the first name, a
	 * space, and the first letter of the middle name to create a full name
	 * string that is 25 characters or less
	 *
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @return String
	 */
	private static String formatFullName(String firstName, String middleName, String lastName) {
		StringBuffer fullName = new StringBuffer("");

		if ((lastName != null) && (!"".equals(lastName))) {
			if (lastName.length() > ServiceConstants.LENGTH_LAST_NAME) {
				fullName.append(lastName.substring(ServiceConstants.Zero, ServiceConstants.LENGTH_LAST_NAME));
			} else {
				fullName.append(lastName);
			}
			fullName.append(",");
		}

		if ((firstName != null) && (!"".equals(firstName))) {
			if (firstName.length() > ServiceConstants.LENGTH_FIRST_NAME) {
				fullName.append(firstName.substring(ServiceConstants.Zero, ServiceConstants.LENGTH_FIRST_NAME));
			} else {
				fullName.append(firstName);
			}
		}

		if ((middleName != null) && (!"".equals(middleName))) {
			fullName.append(" ");
			if (middleName.length() > ServiceConstants.LENGTH_MIDDLE_NAME) {
				fullName.append(middleName.substring(ServiceConstants.Zero, ServiceConstants.LENGTH_MIDDLE_NAME));
			} else {
				fullName.append(middleName);
			}
		}

		return fullName.toString();
	}

	/**
	 *
	 * Method Name: populateFamilyTreeRelIndicator Method Description:This
	 * function will be called after performing all person searches. It marks
	 * all the persons that has Family Tree Relationships.
	 *
	 * @param prsnSearchOutRecArrayDto
	 */
	private void populateFamilyTreeRelIndicator(PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto) {
		HashMap<Integer, PrsnSearchRecOutDto> personMap = new HashMap();
		String sqlFinalQuery = "";
		PrsnSearchRecOutDto prsnSearchOutRecDto = new PrsnSearchRecOutDto();
		if(!ObjectUtils.isEmpty(prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList())){
			List<PersonRelationDto> personRelationDtoList = new ArrayList<>();
			 if(prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList().size() < ServiceConstants.MAX_ROW_TYPE){
				StringBuffer idPerson = new StringBuffer();
				 for (PrsnSearchRecOutDto prsnSearchOutRec : prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList()) {
						personMap.put(new Integer(prsnSearchOutRec.getUlIdPerson().intValue()), prsnSearchOutRec);
						idPerson.append(prsnSearchOutRec.getUlIdPerson() + ServiceConstants.COMMA);
					}
					String sqlParam = !StringUtils.isEmpty(idPerson.toString())
							? idPerson.toString().substring(0, idPerson.toString().length() - 1) : "0";
					sqlFinalQuery = sqlPersonRelationSelect.replaceAll(":personId", sqlParam);
					SQLQuery sQLQuery3 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlFinalQuery)
							.addScalar("idPersonRelation", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
							.addScalar("idRelatedPerson", StandardBasicTypes.LONG)
							.setResultTransformer(Transformers.aliasToBean(PersonRelationDto.class));
					personRelationDtoList = sQLQuery3.list();
			} else {
				for (int i = 0; i * ServiceConstants.MAX_SEARCH_ROWS < prsnSearchOutRecArrayDto
						.getPrsnSearchOutRecDtoList().size(); i++) {
					StringBuffer idPerson = new StringBuffer();
					int startIndex = i * ServiceConstants.MAX_SEARCH_ROWS.intValue();
					int maxIndex = ((prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList().size()
							- startIndex) > ServiceConstants.MAX_SEARCH_ROWS)
									? startIndex + ServiceConstants.MAX_SEARCH_ROWS.intValue()
									: prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList().size();

					for (PrsnSearchRecOutDto prsnSearchOutRec : prsnSearchOutRecArrayDto.getPrsnSearchOutRecDtoList()
							.subList(startIndex, maxIndex - 1)) {
						personMap.put(new Integer(prsnSearchOutRec.getUlIdPerson().intValue()), prsnSearchOutRec);
						idPerson.append(prsnSearchOutRec.getUlIdPerson() + ServiceConstants.COMMA);
					}
					String sqlParam = !StringUtils.isEmpty(idPerson.toString())
							? idPerson.toString().substring(0, idPerson.toString().length() - 1) : "0";
					sqlFinalQuery = sqlPersonRelationSelect.replaceAll(":personId", sqlParam);
					SQLQuery sQLQuery3 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlFinalQuery)
							.addScalar("idPersonRelation", StandardBasicTypes.LONG)
							.addScalar("idPerson", StandardBasicTypes.LONG)
							.addScalar("idRelatedPerson", StandardBasicTypes.LONG)
							.setResultTransformer(Transformers.aliasToBean(PersonRelationDto.class));
					personRelationDtoList.addAll(sQLQuery3.list());
				}
			}
			if (!ObjectUtils.isEmpty(personRelationDtoList)) {
				for (PersonRelationDto personRelationDto : personRelationDtoList) {
					Integer idPerson1 = new Integer(personRelationDto.getIdPerson().intValue());
					Integer idRelatedPerson = new Integer(personRelationDto.getIdRelatedPerson().intValue());

					prsnSearchOutRecDto = (PrsnSearchRecOutDto) personMap.get(idPerson1);
					if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto)) {
						prsnSearchOutRecDto.setBindFamRelation(ServiceConstants.Y);
					}

					prsnSearchOutRecDto = (PrsnSearchRecOutDto) personMap.get(idRelatedPerson);
					if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRecDto)) {
						prsnSearchOutRecDto.setBindFamRelation(ServiceConstants.Y);
					}
				}
			}
		}
	}

	/**
	 *
	 * Method Name: populateFamilyTreeRelIndicator Method Description:This
	 * function will be called after performing all person searches. It marks
	 * all the persons that has Family Tree Relationships.
	 *
	 * @param prsnSrchListpInitArrayDto
	 */
	private void populateFamilyTreeRelIndicator(PrsnSrchListpInitArrayDto prsnSrchListpInitArrayDto) {
		HashMap<Integer, PrsnSrchListpInitDto> personMap = new HashMap();
		Long idPerson = ServiceConstants.LongZero;
		PrsnSrchListpInitDto prsnSrchListpInitDto = new PrsnSrchListpInitDto();
		for (PrsnSrchListpInitDto prsnSearchOutRec : prsnSrchListpInitArrayDto.getPrsnSrchListpInitDto()) {
			personMap.put(new Long(prsnSearchOutRec.getUlIdPerson()).intValue(), prsnSearchOutRec);
			idPerson = prsnSearchOutRec.getUlIdPerson();
		}

		SQLQuery sQLQuery3 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlPersonRelationSelect)
				.addScalar("idPersonRelation", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idRelatedPerson", StandardBasicTypes.LONG).setParameter("personId", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonRelationDto.class));
		List<PersonRelationDto> personRelationDtoList = sQLQuery3.list();
		for (PersonRelationDto personRelationDto : personRelationDtoList) {
			Integer idPerson1 = new Integer(personRelationDto.getIdPerson().intValue());
			Integer idRelatedPerson = new Integer(personRelationDto.getIdRelatedPerson().intValue());

			prsnSrchListpInitDto = (PrsnSrchListpInitDto) personMap.get(idPerson1);
			if (!TypeConvUtil.isNullOrEmpty(prsnSrchListpInitDto)) {
				prsnSrchListpInitDto.setbIndFamRelation(ServiceConstants.Y);
			}

			prsnSrchListpInitDto = (PrsnSrchListpInitDto) personMap.get(idRelatedPerson);
			if (!TypeConvUtil.isNullOrEmpty(prsnSrchListpInitDto)) {
				prsnSrchListpInitDto.setbIndFamRelation(ServiceConstants.Y);
			}

		}

	}

	/**
	 *
	 * Method Name: isPersonInfoViewable Method Description:Check if the
	 * Person's Information is Viewable or Not
	 *
	 * @param idPerson
	 * @return boolean
	 */
	public boolean isPersonInfoViewable(Long idPerson) {
		boolean bPersonInfoViewable = ServiceConstants.FALSEVAL;
		SQLQuery sQLQuery3 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlViewPersonInfo)
				.addScalar("idPersonCount", StandardBasicTypes.LONG).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(PersonSearchCountDto.class));
		PersonSearchCountDto personSearchCountDto = (PersonSearchCountDto) sQLQuery3.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(personSearchCountDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		if (personSearchCountDto.getIdPersonCount() == ServiceConstants.LongZero)
			bPersonInfoViewable = ServiceConstants.TRUEVAL;
		return bPersonInfoViewable;
	}

	/**
	 *
	 * Method Name: getPersonDetailAll Method Description:This method get the
	 * person detail information for a list of persons. This could any list of
	 * Person Ids
	 *
	 * @param bindVariablesVectorMerged
	 * @return HashMap
	 */
	public HashMap<Integer, PrsnSearchRecOutDto> getPersonDetailAll(List<Integer> bindVariablesVectorMerged) {

		HashMap<Integer, PrsnSearchRecOutDto> persMap = new HashMap();
		log.debug("Entering method getPersonDetailAll in PersonSearchDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonDetailAll)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE).addScalar("dtPersonDeath", StandardBasicTypes.DATE)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.LONG)
				.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonSearchDetailDto.class));
		// Defect#14251: Check the size of bindVariablesVector, if it is > 1000 (which is
		// maximum acceptable no. for an IN query) partition
		// the parameter and execute the query for each subset of size 1000. Else execute in
		// single query.
		List<PersonSearchDetailDto> personSearchDetailDtoList = new ArrayList<>();
		if(ServiceConstants.MAX_SEARCH_ROWS < bindVariablesVectorMerged.size()){
			Lists.partition(bindVariablesVectorMerged, ServiceConstants.MAX_SEARCH_ROWS.intValue()).forEach(idPersons -> {
				SQLQuery subQuery = (SQLQuery) sQLQuery1.setParameterList("PersonId", idPersons);
				personSearchDetailDtoList.addAll(subQuery.list());
			});
		}else{
			personSearchDetailDtoList.addAll(sQLQuery1.setParameterList("PersonId",bindVariablesVectorMerged).list());
		}



		if (TypeConvUtil.isNullOrEmpty(personSearchDetailDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}
		for (PersonSearchDetailDto personSearchDetailDto : personSearchDetailDtoList) {
			PrsnSearchRecOutDto prsnSearchOutRec = new PrsnSearchRecOutDto();
			prsnSearchOutRec.setUlIdPerson(personSearchDetailDto.getIdPerson());
			prsnSearchOutRec.setCcdPersonSex(personSearchDetailDto.getCdPersonSex());
			prsnSearchOutRec.setDtDtPersonBirth(personSearchDetailDto.getDtPersonBirth());
			prsnSearchOutRec.setSzCdPersonEthnicGroup(personSearchDetailDto.getCdPersonEthnicGroup());

			prsnSearchOutRec.setSzNbrPersonIdSsn(personSearchDetailDto.getNbrPersonIdNumber());
			prsnSearchOutRec.setSzAddrCity(personSearchDetailDto.getAddrPersonAddrCity());

			if (TypeConvUtil.isNullOrEmpty(personSearchDetailDto.getAddrPersAddrStLn2()))
				personSearchDetailDto.setAddrPersAddrStLn2(ServiceConstants.EMPTY_STRING);

			prsnSearchOutRec.setSzAddrPersAddrStLn1(PersonUtil.formatStreetAddress(
					personSearchDetailDto.getAddrPersAddrStLn1(), personSearchDetailDto.getAddrPersAddrStLn2()));
			prsnSearchOutRec.setSzAddrPersAddrStLn2(personSearchDetailDto.getAddrPersAddrStLn2());
			prsnSearchOutRec.setSzCdCounty(personSearchDetailDto.getCdPersonAddrCounty());
			if (!isValid(personSearchDetailDto.getAddrPersAddrStLn1())
					&& !isValid(personSearchDetailDto.getAddrPersonAddrCity())
					&& !isValid(personSearchDetailDto.getCdPersonAddrCounty())) {
				if (isValid(personSearchDetailDto.getAddrPersonStLn1()))
					prsnSearchOutRec.setSzAddrPersAddrStLn1(personSearchDetailDto.getAddrPersonStLn1());

				if (isValid(personSearchDetailDto.getAddrPersonCity()))
					prsnSearchOutRec.setSzAddrCity(personSearchDetailDto.getAddrPersonCity());

				if (isValid(personSearchDetailDto.getCdPersonCounty()))
					prsnSearchOutRec.setSzCdCounty(personSearchDetailDto.getCdPersonCounty());
			}
			if (TypeConvUtil.isNullOrEmpty(personSearchDetailDto.getNmNameFirst()))
				personSearchDetailDto.setNmNameFirst(ServiceConstants.EMPTY_STRING);

			if (TypeConvUtil.isNullOrEmpty(personSearchDetailDto.getNmNameMiddle()))
				personSearchDetailDto.setNmNameMiddle(ServiceConstants.EMPTY_STRING);
			if (TypeConvUtil.isNullOrEmpty(personSearchDetailDto.getNmNameLast()))
				personSearchDetailDto.setNmNameLast(ServiceConstants.EMPTY_STRING);

			String fullName = formatFullName(personSearchDetailDto.getNmNameFirst(),
					personSearchDetailDto.getNmNameMiddle(), personSearchDetailDto.getNmNameLast());

			prsnSearchOutRec.setSzNmIncmgPersFull(fullName);
			prsnSearchOutRec.setSzNmPersonFull(personSearchDetailDto.getNmPersonFull());

			if (prsnSearchOutRec.getSzNmIncmgPersFull().equalsIgnoreCase(prsnSearchOutRec.getSzNmPersonFull())) {
				prsnSearchOutRec.setSzScrCdPersonSearchHit(ServiceConstants.N);
			} else {
				prsnSearchOutRec.setSzScrCdPersonSearchHit(ServiceConstants.Y);
			}

			prsnSearchOutRec.setBindActiveStatus(ServiceConstants.PERS_CPERSTAT_M);

			if (!TypeConvUtil.isNullOrEmpty(prsnSearchOutRec.getDtDtPersonBirth())) {
				prsnSearchOutRec.setLnbrPersonAge((long) DateUtils.getAge(prsnSearchOutRec.getDtDtPersonBirth()));
			}

			prsnSearchOutRec.setBindPersonDobApprox(personSearchDetailDto.getIndPersonDobApprox());

			persMap.put(new Integer(prsnSearchOutRec.getUlIdPerson().intValue()), prsnSearchOutRec);
		}

		log.debug("Exiting method getPersonDetailAll in PersonSearchDaoImpl");
		return persMap;
	}

	/**
	 *
	 * Method Name: getForwardPersonInMerge Method Description: This method
	 * returns the forward person Id for a person
	 *
	 * @param idPerson
	 * @return Long
	 */
	@Override
	public Long getForwardPersonInMerge(Long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonMerge.class);
		criteria.add(Restrictions.eq("indPersMergeInvalid", ServiceConstants.N_CHAR));
		criteria.add(Restrictions.eq("personByIdPersMergeClosed.idPerson", idPerson));
		List<PersonMerge> PersonMergeList = criteria.list();
		if (CollectionUtils.isEmpty(PersonMergeList)) {
			return ServiceConstants.ZERO_VAL;
			// throw new
			// DataNotFoundException(messageSource.getMessage("Common.noRecordFound",
			// null, Locale.US));
		} else {
			return PersonMergeList.get(ServiceConstants.Zero).getPersonByIdPersMergeForward().getIdPerson();
		}
	}

	/**
	 * Checks to see if a given string is valid. This includes checking that the
	 * string is not null or empty.
	 *
	 * @param value
	 *            - the string that is being evaluated
	 * @return boolean - whether the string is valid
	 */
	public boolean isValid(String value) {
		if (value == null)
			return ServiceConstants.FALSEVAL;
		String trimmedString = value.trim();
		return (trimmedString.length() > ServiceConstants.Zero);
	}

}
