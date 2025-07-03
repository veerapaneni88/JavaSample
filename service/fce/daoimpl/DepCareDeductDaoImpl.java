package us.tx.state.dfps.service.fce.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FceDepCareDeduct;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.DependentCareReadReq;
import us.tx.state.dfps.service.common.response.DependentCareReadRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.FceDepCareDeductDto;
import us.tx.state.dfps.service.fce.dao.DepCareDeductDao;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;

@Repository
public class DepCareDeductDaoImpl implements DepCareDeductDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${DepCareDeductionDaoImpl.getValidPersonsAsAdults}")
	private String readFceInfoSql;

	@Value("${DepCareDeductionDaoImpl.getValidPersonsAsDependents}")
	private String readFceDependentsInfoSql;

	@Value("${DepCareDeductionDaoImpl.getFceDepCareDeduct}")
	private String readFceDepCareDeductSql;

	@Value("${DepCareDeductionDaoImpl.getFcePrincipalsAge}")
	private String getFcePrincipalsAgeSql;

	@Value("${DepCareDeductionDaoImpl.getDepDeductionSum}")
	private String getDepDeductionSumSql;

	@Value("${DepCareDeductionDaoImpl.getValidAdultDependent}")
	private String validAdultDependentSql;

	@Value("${DepCareDeductionDaoImpl.findFceDepCareDeduct}")
	private String findFceDepCareDeductSql;

	@Value("${DepCareDeductionDaoImpl.findFceDepValidDeductions}")
	private String findFceDepValidDeductionsSql;

	@Value("${DepCareDeductionDaoImpl.findFceDepCareDeductOrderBy}")
	private String findFceDepCareDeductOrderBySql;

	@Value("${DepCareDeductionDaoImpl.isAdultDependentDup}")
	private String isAdultDependentDupSql;

	@Value("${DepCareDeductionDaoImpl.isAdultInDependentColumn}")
	private String isAdultInDependentColumnSql;

	@Value("${DepCareDeductionDaoImpl.isDependentInAdultColumn}")
	private String isDependentInAdultColumnSql;

	@Value("${DepCareDeductionDaoImpl.updateInvalidDependentInfo}")
	private String updateInvalidDependentInfoSql;

	@Value("${DepCareDeductionDaoImpl.getValidDependents}")
	private String getValidDependentsSql;

	private static final Logger log = Logger.getLogger(DepCareDeductDaoImpl.class);

	/**
	 * Method Name:read Method Description:read Dependent Care Detail record
	 * 
	 * @param dependentCareReadReq
	 * @return List<FcePersonDto>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FcePersonDto> getValidPersonsAsAdults(DependentCareReadReq dependentCareReadReq) {
		List<FcePersonDto> fcePersonDtoList = new ArrayList<FcePersonDto>();
		String readFceInfo = readFceInfoSql;
		if (dependentCareReadReq.getEligSpecMode()) {
			readFceInfo = readFceInfo
					.concat(" and UPPER(fp.IND_PERSON_HM_REMOVAL) = 'Y' and UPPER(fp.IND_CERTIFIED_GROUP) = 'Y'");
		} else {
			readFceInfo = readFceInfo.concat(" and UPPER(fp.IND_PERSON_HM_REMOVAL) = 'Y'");
		}

		fcePersonDtoList = (List<FcePersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(readFceInfo).setParameter("idFceEligibility", dependentCareReadReq.getIdFceEligiblity())
				.setParameter("idFceApplication", dependentCareReadReq.getIdFceApplication()))
						.addScalar("idFcePerson", StandardBasicTypes.LONG).addScalar("nbrAge", StandardBasicTypes.SHORT)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FcePersonDto.class)).list();

		log.info("TransactionId :" + dependentCareReadReq.getTransactionId());
		return fcePersonDtoList;
	}

	/**
	 * Method Name: getValidPersonsAsDependents Method Description: Get
	 * Dependents' information
	 * 
	 * @param dependentCareReadReq
	 * @return List<fcePersonDto>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FcePersonDto> getValidPersonsAsDependents(DependentCareReadReq dependentCareReadReq) {
		List<FcePersonDto> fcePersonDtoList = new ArrayList<FcePersonDto>();

		String readFceDependentsInfo = readFceDependentsInfoSql;
		if (dependentCareReadReq.getEligSpecMode()) {
			readFceDependentsInfo = readFceDependentsInfo
					.concat(" and UPPER(fp.IND_PERSON_HM_REMOVAL) = 'Y' and UPPER(fp.IND_CERTIFIED_GROUP) = 'Y'");
		} else {
			readFceDependentsInfo = readFceDependentsInfo.concat(" and UPPER(fp.IND_PERSON_HM_REMOVAL) = 'Y'");
		}
		fcePersonDtoList = (List<FcePersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(readFceDependentsInfo)
				.setParameter("idFceEligibility", dependentCareReadReq.getIdFceEligiblity())
				.setParameter("idFceApplication", dependentCareReadReq.getIdFceApplication()))
						.addScalar("idFcePerson", StandardBasicTypes.LONG).addScalar("nbrAge", StandardBasicTypes.SHORT)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FcePersonDto.class)).list();

		log.info("TransactionId :" + dependentCareReadReq.getTransactionId());
		return fcePersonDtoList;
	}

	/**
	 * Method Name: getFceDepCareDeduct Method Description: Read Dependent Care
	 * Cost Deduction
	 * 
	 * @param dependentCareReadReq
	 * @return fceDepCareDeductDto
	 * @throws Exception
	 */
	public FceDepCareDeductDto getFceDepCareDeduct(DependentCareReadReq dependentCareReadReq) {
		FceDepCareDeductDto fceDepCareDeductDto = new FceDepCareDeductDto();
		Long idFceDepCareDeduct = dependentCareReadReq.getIdFceDepCareDeduct();
		fceDepCareDeductDto = (FceDepCareDeductDto) sessionFactory.getCurrentSession()
				.createCriteria(FceDepCareDeduct.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idFceDepCareDeduct"), "idFceDepCareDeduct")
						.add(Projections.property("fcePersonByIdFceAdultPerson.idFcePerson"), "idFceAdultPerson")
						.add(Projections.property("fcePersonByIdFceDependentPerson.idFcePerson"),
								"idFceDependentPerson")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
						.add(Projections.property("dtCreated"), "dtCreated")
						.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("nbrDependentAge"), "nbrDependentAge")
						.add(Projections.property("indInvalid"), "strInvalid")
						.add(Projections.property("amtActCost"), "amtActCost")
						.add(Projections.property("amtDeduction"), "amtDeduction"))
				.add(Restrictions.eq("idFceDepCareDeduct", idFceDepCareDeduct))
				.setResultTransformer(Transformers.aliasToBean(FceDepCareDeductDto.class)).uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(fceDepCareDeductDto)
				&& !TypeConvUtil.isNullOrEmpty(fceDepCareDeductDto.getStrInvalid())) {
			fceDepCareDeductDto.setIndInvalid(ServiceConstants.Y.equals(fceDepCareDeductDto.getStrInvalid()));
		}
		return fceDepCareDeductDto;
	}

	/**
	 * Method Name: getFcePrincipalsAge Method Description: Get Principal Age
	 * for Deduction for Dependent Care Cost Detail
	 * 
	 * @param dependentCareReadReq
	 * @return List<FcePersonDto>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FcePersonDto> getFcePrincipalsAge(DependentCareReadReq dependentCareReadReq) {

		String getFcePrincipalsAge = getFcePrincipalsAgeSql;
		if (dependentCareReadReq.getEligSpecMode()) {
			getFcePrincipalsAge = getFcePrincipalsAge
					.concat(" and UPPER(fp.IND_PERSON_HM_REMOVAL) = 'Y' and UPPER(fp.IND_CERTIFIED_GROUP) = 'Y'");
		} else {
			getFcePrincipalsAge = getFcePrincipalsAge.concat(" and UPPER(fp.IND_PERSON_HM_REMOVAL) = 'Y'");
		}

		List<FcePersonDto> fcePersonDtoList = new ArrayList<FcePersonDto>();
		fcePersonDtoList = (List<FcePersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getFcePrincipalsAge)
				.setParameter("idFceEligibility", dependentCareReadReq.getIdFceEligiblity())
				.setParameter("idFceApplication", dependentCareReadReq.getIdFceApplication()))
						.addScalar("idFcePerson", StandardBasicTypes.LONG).addScalar("nbrAge", StandardBasicTypes.SHORT)
						.setResultTransformer(Transformers.aliasToBean(FcePersonDto.class)).list();

		log.info("TransactionId :" + dependentCareReadReq.getTransactionId());

		return fcePersonDtoList;
	}

	/**
	 * Method Name: getDepDeductionSum Method Description: Get Dependent Care
	 * Deduction Sum for Deduction for Dependent Care Cost Detail
	 * 
	 * @param dependentCareReadReq
	 * @return double
	 * @throws Exception
	 */
	@Override
	public Double getDepDeductionSum(DependentCareReadReq dependentCareReadReq) {
		BigDecimal sum = (BigDecimal) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getDepDeductionSumSql)
				.setParameter("idFceEligibility", dependentCareReadReq.getIdFceEligiblity())
				.setParameter("idFceDependentPerson", dependentCareReadReq.getIdFceDependentPerson()))
						.addScalar("amtDeduction", StandardBasicTypes.BIG_DECIMAL).uniqueResult();

		return null == sum ? 0.0 : sum.doubleValue();
	}

	/**
	 * Method Name: insertFceDepCareDeduct Method Description: Add
	 * FceDepCareDeduct record for Deduction for Dependent Care Cost Detail
	 * 
	 * @param fceDepCareDeductDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public void insertFceDepCareDeduct(FceDepCareDeductDto fceDepCareDeductDto) {

		FceDepCareDeduct fceDepCareDeduct = new FceDepCareDeduct();
		if (!TypeConvUtil.isNullOrEmpty(fceDepCareDeductDto)) {

			FcePerson fceAdult = (FcePerson) sessionFactory.getCurrentSession().get(FcePerson.class,
					fceDepCareDeductDto.getIdFceAdultPerson());
			FcePerson fceDependent = (FcePerson) sessionFactory.getCurrentSession().get(FcePerson.class,
					fceDepCareDeductDto.getIdFceDependentPerson());

			fceDepCareDeduct.setFcePersonByIdFceAdultPerson(fceAdult);
			fceDepCareDeduct.setFcePersonByIdFceDependentPerson(fceDependent);
			fceDepCareDeduct.setDtLastUpdate(new Date());
			fceDepCareDeduct.setIdLastUpdatePerson(fceDepCareDeductDto.getIdLastUpdatePerson());
			Long idCreatedPerson = fceDepCareDeductDto.getIdCreatedPerson();
			fceDepCareDeduct.setIdCreatedPerson(
					null != idCreatedPerson ? idCreatedPerson : fceDepCareDeductDto.getIdLastUpdatePerson());
			fceDepCareDeduct.setNbrDependentAge(fceDepCareDeductDto.getNbrDependentAge());
			fceDepCareDeduct.setAmtActCost(fceDepCareDeductDto.getAmtActCost());
			fceDepCareDeduct.setAmtDeduction(fceDepCareDeductDto.getAmtDeduction());
			fceDepCareDeduct.setIndInvalid(toCharIndicator(fceDepCareDeductDto.getIndInvalid()));
			fceDepCareDeduct.setDtCreated(new Date());
			sessionFactory.getCurrentSession().save(fceDepCareDeduct);
		}

	}

	/**
	 * Method Name: updateFceDepCareDeductInvalid Method Description: Update
	 * FceDepCareDeduct record for Invalid Adult and Dependent Person
	 * 
	 * @param dependentCareReadReq
	 * @return
	 * @throws Exception
	 */
	@Override
	public void updateFceDepCareDeductInvalid(FceDepCareDeductDto fceDepCareDeductDto) {
		FceDepCareDeduct fceDepCareDeduct = (FceDepCareDeduct) sessionFactory.getCurrentSession()
				.get(FceDepCareDeduct.class, fceDepCareDeductDto.getIdFceDepCareDeduct());
		fceDepCareDeduct.setIndInvalid(toCharIndicator(fceDepCareDeductDto.getIndInvalid()));
		fceDepCareDeduct.setIdLastUpdatePerson(fceDepCareDeductDto.getIdLastUpdatePerson());
		sessionFactory.getCurrentSession().saveOrUpdate(fceDepCareDeduct);
		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * Method Name:getValidAdultDependent Method Description:get
	 * FceDepCareDeduct record for valid Adult and Dependent Person
	 * 
	 * @param dependentCareReadReq
	 * @return List<FceDepCareDeductDto>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FceDepCareDeductDto> getValidAdultDependent(DependentCareReadReq dependentCareReadReq) {

		String validAdultDependent = validAdultDependentSql;
		List<FceDepCareDeductDto> fceDepCareDeductDtoList = new ArrayList<FceDepCareDeductDto>();

		fceDepCareDeductDtoList = (List<FceDepCareDeductDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(validAdultDependent)
				.setParameter("id_fce_adult_person", dependentCareReadReq.getIdFceAdultPerson())
				.setParameter("id_fce_dependent_person", dependentCareReadReq.getIdFceDependentPerson()))
						.addScalar("idFceDepCareDeduct", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("idFceAdultPerson", StandardBasicTypes.LONG)
						.addScalar("idFceDependentPerson", StandardBasicTypes.LONG)
						.addScalar("nbrDependentAge", StandardBasicTypes.SHORT)
						.addScalar("amtActCost", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("amtDeduction", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("indInvalid", StandardBasicTypes.BOOLEAN)
						.addScalar("nmAdultPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmAdultPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("nmAdultPersonLast", StandardBasicTypes.STRING)
						.addScalar("nmAdultPersonFull", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonLast", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonFull", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(FceDepCareDeductDto.class)).list();

		return fceDepCareDeductDtoList;

	}

	/**
	 * Method Name: updateInvalidDependentInfo Method Description: Update
	 * Invalid Dependent in database
	 * 
	 * @param fceDepCareDeductDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public void updateInvalidDependentInfo(FceDepCareDeductDto fceDepCareDeductDto) {

		FceDepCareDeduct fceDepCareDeduct = (FceDepCareDeduct) sessionFactory.getCurrentSession()
				.get(FceDepCareDeduct.class, fceDepCareDeductDto.getIdFceDepCareDeduct());
		fceDepCareDeduct.setIndInvalid(toCharIndicator(fceDepCareDeductDto.getIndInvalid()));
		if (fceDepCareDeductDto.getNbrAge() != null) {
			fceDepCareDeduct.setNbrDependentAge(fceDepCareDeductDto.getNbrDependentAge());
		}
		if (fceDepCareDeductDto.getAmtDeduction() != null) {
			fceDepCareDeduct.setAmtDeduction(fceDepCareDeductDto.getAmtDeduction());
		}
		if (fceDepCareDeductDto.getIdLastUpdatePerson() != null) {
			fceDepCareDeduct.setIdLastUpdatePerson(fceDepCareDeductDto.getIdLastUpdatePerson());
		}
		sessionFactory.getCurrentSession().update(fceDepCareDeduct);
	}

	/**
	 * Method Name: isAdultDependentDup Method Description: Get valid Dependents
	 * 
	 * @param idFceEligibility
	 * @param idFceDependentPerson
	 * @return dependentCareReadRes
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DependentCareReadRes getValidDependents(Long idFceEligibility, Long idFceDependentPerson) {

		DependentCareReadRes dependentCareReadRes = new DependentCareReadRes();
		List<FceDepCareDeductDto> fceDepCareDeductDtoList = new ArrayList<FceDepCareDeductDto>();
		String query = getValidDependentsSql;

		fceDepCareDeductDtoList = (List<FceDepCareDeductDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(query).setParameter("id_fce_eligibility", idFceEligibility)
				.setParameter("id_fce_dependent_person", idFceDependentPerson))
						.addScalar("idFceDepCareDeduct", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("idFceAdultPerson", StandardBasicTypes.LONG)
						.addScalar("idFceDependentPerson", StandardBasicTypes.LONG)
						.addScalar("nbrDependentAge", StandardBasicTypes.SHORT)
						.addScalar("amtActCost", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("amtDeduction", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("indInvalid", StandardBasicTypes.BOOLEAN)
						.setResultTransformer(Transformers.aliasToBean(FceDepCareDeductDto.class)).list();

		dependentCareReadRes.setFceDepCrDeductDto(fceDepCareDeductDtoList);
		return dependentCareReadRes;
	}

	/**
	 * Method Name: findFceDepCareDeduct Method Description: Find out Dependent
	 * Care Deduction
	 *
	 * @param idFceEligibility
	 * @param valid
	 * @return List<FceDepCareDeductDto>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FceDepCareDeductDto> findFceDepCareDeduct(Long idFceEligibility, Boolean valid) {

		String query = findFceDepCareDeductSql;
		if (valid) {
			query += findFceDepValidDeductionsSql;
		}
		query += findFceDepCareDeductOrderBySql;

		List<FceDepCareDeductDto> fceDepCareDeductDtoList = new ArrayList<FceDepCareDeductDto>();
		fceDepCareDeductDtoList = (List<FceDepCareDeductDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(query).setParameter("id_fce_eligibility", idFceEligibility))
						.addScalar("idFceDepCareDeduct", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("idFceAdultPerson", StandardBasicTypes.LONG)
						.addScalar("idFceDependentPerson", StandardBasicTypes.LONG)
						.addScalar("nbrDependentAge", StandardBasicTypes.SHORT)
						.addScalar("amtActCost", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("amtDeduction", StandardBasicTypes.BIG_DECIMAL)
						.addScalar("indInvalid", StandardBasicTypes.BOOLEAN)
						.addScalar("nmAdultPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmAdultPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("nmAdultPersonLast", StandardBasicTypes.STRING)
						.addScalar("nmAdultPersonFull", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonMiddle", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonLast", StandardBasicTypes.STRING)
						.addScalar("nmDependentPersonFull", StandardBasicTypes.STRING)
						.addScalar("dtBirth", StandardBasicTypes.DATE)
						.setResultTransformer(Transformers.aliasToBean(FceDepCareDeductDto.class)).list();

		return fceDepCareDeductDtoList;
	}

	/**
	 * 
	 * Method Name: isAdultDependentDup Method Description: Check whether or not
	 * Adult/Dependent is duplicate
	 * 
	 * @param idFceEligibility
	 * @param idFceAdultPerson
	 * @param idFceDependentPerson
	 * @return boolean
	 * @throws Exception
	 */
	public Boolean isAdultDependentDup(Long idFceEligibility, Long idFceAdultPerson, Long idFceDependentPerson) {

		Boolean result = Boolean.FALSE;
		Integer count = 0;
		String query = isAdultDependentDupSql;

		count = (Integer) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query)
				.setParameter("id_fce_eligibility", idFceEligibility)
				.setParameter("id_fce_adult_person", idFceAdultPerson)
				.setParameter("id_fce_dependent_person", idFceDependentPerson))
						.addScalar("count", StandardBasicTypes.INTEGER).uniqueResult();

		if (count > 1) {
			result = Boolean.TRUE;
		}
		return result;
	}

	/**
	 * 
	 * Method Name: isAdultInDependentColumn Method Description: check the Adult
	 * whether or not it is in dependent Column
	 * 
	 * @param idFceEligibility
	 * @param idFceAdultPerson
	 * @return boolean
	 * @throws Exception
	 */
	public Boolean isAdultInDependentColumn(Long idFceEligibility, Long idFceAdultPerson) {

		Boolean result = Boolean.FALSE;
		Integer count = 0;
		String query = isAdultInDependentColumnSql;

		count = (Integer) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query)
				.setParameter("id_fce_eligibility", idFceEligibility)
				.setParameter("id_fce_dependent_person", idFceAdultPerson))
						.addScalar("count", StandardBasicTypes.INTEGER).uniqueResult();

		if (count > 0) {
			result = Boolean.TRUE;
		}
		return result;
	}

	/**
	 * 
	 * Method Name: isDependentInAdultColumn Method Description: check the
	 * dependent whether or not it is in Adult Column
	 * 
	 * @param idFceEligibility
	 * @param idFceDependentPerson
	 * @return boolean
	 * @throws Exception
	 */
	public Boolean isDependentInAdultColumn(Long idFceEligibility, Long idFceDependentPerson) {

		Boolean result = Boolean.FALSE;
		Integer count = 0;
		String query = isDependentInAdultColumnSql;

		count = (Integer) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query)
				.setParameter("id_fce_eligibility", idFceEligibility)
				.setParameter("id_fce_adult_person", idFceDependentPerson))
						.addScalar("count", StandardBasicTypes.INTEGER).uniqueResult();

		if (count > 0) {
			result = Boolean.TRUE;
		}
		return result;
	}

	/**
	 * 
	 * Method Name: toCharIndicator Method Description: Conversion from Boolean
	 * to String
	 * 
	 * @param value
	 * @return boolean
	 */
	private static String toCharIndicator(Boolean value) {
		if (value == null) {
			return null;
		}
		if (value.booleanValue()) {
			return "Y";
		}
		return "N";
	}

}
