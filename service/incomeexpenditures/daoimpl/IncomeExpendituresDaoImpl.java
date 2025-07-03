package us.tx.state.dfps.service.incomeexpenditures.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Lists;

import us.tx.state.dfps.common.domain.FceIncome;
import us.tx.state.dfps.common.domain.IncomeAndResources;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fce.IncomeExpenditureDto;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.incomeexpenditures.dao.IncomeExpendituresDao;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * extends BaseDao and implements IncomeExpendituresDao. This is used to
 * save,submit,calculate and submit income expenditure details in the database.
 * Nov 13, 2017- 5:53:04 PM © 2017 Texas Department of Family and Protective
 * Services
 */

@Repository
public class IncomeExpendituresDaoImpl implements IncomeExpendituresDao {

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	FceEligibilityDao fceEligibilityDao;

	@Autowired
	FceDao fceApplicationDao;

	@Autowired
	PersonDao personDao;

	@Value("${IncomeExpendituresDaoImpl.findPrimaryWorkerForStage}")
	private String findPrimaryWorkerForStageSql;

	@Value("${IncomeExpendituresDaoImpl.findFceIncome}")
	private String findFceIncomeSql;

	@Value("${IncomeExpendituresDaoImpl.findFceIncomeSourceClause}")
	private String findFceIncomeSourceClauseSql;

	@Value("${IncomeExpendituresDaoImpl.findFceResourceSourceClause}")
	private String findFceResourceSourceClauseSql;

	@Value("${IncomeExpendituresDaoImpl.findFceSourceChildClause}")
	private String findFceSourceChildClauseSql;

	@Value("${IncomeExpendituresDaoImpl.findFceSourceFamilyClause}")
	private String findFceSourceFamilyClauseSql;

	@Value("${IncomeExpendituresDaoImpl.updateFceEligibility}")
	private String updateFceEligibilitySql;

	@Value("${IncomeExpendituresDaoImpl.updateFceApplication}")
	private String updateFceApplicationSql;

	@Value("${PersonDaoImpl.getPersonById}")
	private String getPersonByIdSql;

	/**
	 * 
	 * Method Name: findPrimaryWorkerForStage Method Description:
	 * 
	 * @param stageID
	 ** @param incomeExpenditureDto
	 * @
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public PersonDto findPrimaryWorkerForStage(long stageID, IncomeExpenditureDto incomeExpenditureDto) {
		List<PersonDto> personDtos = new ArrayList<>();

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(findPrimaryWorkerForStageSql)
				.setParameter("stageId", stageID);
		((SQLQuery) query).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersonCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonZip", StandardBasicTypes.STRING)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("cdPersonState", StandardBasicTypes.STRING)
				.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT).addScalar("personPhone", StandardBasicTypes.STRING)
				.addScalar("personIdNumber", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("txtPersonOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("indAutoPersMerge", StandardBasicTypes.STRING)
				.addScalar("cdDisasterRlf", StandardBasicTypes.STRING)
				.addScalar("indEducationPortfolio", StandardBasicTypes.STRING)
				.addScalar("cdTribeEligible", StandardBasicTypes.STRING)
				.addScalar("cdOccupation", StandardBasicTypes.STRING)
				.addScalar("cdMannerDeath", StandardBasicTypes.STRING)
				.addScalar("cdDeathRsnCps", StandardBasicTypes.STRING)
				.addScalar("cdDeathCause", StandardBasicTypes.STRING)
				.addScalar("cdDeathAutpsyRslt", StandardBasicTypes.STRING)
				.addScalar("cdDeathFinding", StandardBasicTypes.STRING)
				.addScalar("fatalityDetails", StandardBasicTypes.STRING)
				.addScalar("indIrRpt", StandardBasicTypes.STRING)
				.addScalar("indAbuseNglctDeathInCare", StandardBasicTypes.STRING);

		List<Object[]> resultSet = query.list();
		if (resultSet.size() == 0) {

			throw new DataLayerException("no primary worker for stage: " + stageID);
		} else if (resultSet.size() > 1) {
			throw new DataLayerException("only expected 1 primary worker for stage :" + stageID);
		} else {
			personDtos = query.setResultTransformer(Transformers.aliasToBean(PersonDto.class)).list();

		}
		if (CollectionUtils.isNotEmpty(personDtos)) {
			return personDtos.get(0);
		}
		return null;
	}

	/**
	 * 
	 * Method Name: findFceIncomeOrResourceForChildOrFamily Method Description:
	 * 
	 * @param idFceEligibility
	 * @param incomeExpenditureDto
	 * @param incomeOrResource
	 * @param childOrFamily
	 * @
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public List<FceIncomeDto> findFceIncomeOrResourceForChildOrFamily(long idFceEligibility, String incomeOrResource,
			String childOrFamily) {
		String sql = findFceIncomeSql;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(sql);

		if (StringUtils.isNotBlank(childOrFamily) && childOrFamily.equalsIgnoreCase("Child")) {
			stringBuilder.append(findFceSourceChildClauseSql);
		} else {
			stringBuilder.append(findFceSourceFamilyClauseSql);
		}

		if (StringUtils.isNotBlank(incomeOrResource) && incomeOrResource.equalsIgnoreCase("Income")) {
			stringBuilder.append(findFceIncomeSourceClauseSql);
		} else {
			stringBuilder.append(findFceResourceSourceClauseSql);
		}

		sql = stringBuilder.toString();

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		query.setParameter("idfceEligibility", idFceEligibility);

		query.addScalar("idFceIncome", StandardBasicTypes.LONG).addScalar("idFceEligibility", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idIncRsrc", StandardBasicTypes.LONG)
				.addScalar("idFcePerson", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("amtIncome", StandardBasicTypes.DOUBLE).addScalar("cdType", StandardBasicTypes.STRING)
				.addScalar("indIncomeSource", StandardBasicTypes.STRING)
				.addScalar("indResourceSource", StandardBasicTypes.STRING)
				.addScalar("indCountable", StandardBasicTypes.STRING).addScalar("indEarned", StandardBasicTypes.STRING)
				.addScalar("indNotAccessible", StandardBasicTypes.STRING)
				.addScalar("indChild", StandardBasicTypes.STRING).addScalar("indFamily", StandardBasicTypes.STRING)
				.addScalar("indNone", StandardBasicTypes.STRING)
				.addScalar("txtVerificationMethod", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING).addScalar("txtSource", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("cdRelInt", StandardBasicTypes.STRING)
				.addScalar("dtBirth", StandardBasicTypes.DATE).addScalar("nbrAge", StandardBasicTypes.LONG)
				.addScalar("indCertifiedGroup", StandardBasicTypes.STRING)
				.addScalar("indPersonHmRemoval", StandardBasicTypes.STRING);

		List<FceIncomeDto> listIncomeExpenditureDtos = query
				.setResultTransformer(Transformers.aliasToBean(FceIncomeDto.class)).list();

		return listIncomeExpenditureDtos;
	}

	/**
	 * 
	 * Method Name: saveFceIncomeResource Method Description:
	 * 
	 * @param FceIncomeDto
	 * @
	 */

	@Override
	public void saveFceIncomeResource(FceIncomeDto fceIncomeDto) {

		// Need to implement EJB Validation

		FceIncome fceIncome = saveNoTimestampCheck(fceIncomeDto);
		sessionFactory.getCurrentSession().saveOrUpdate(fceIncome);

	}

	/**
	 * 
	 * Method Name: findFceIncomeOrResource Method Description:
	 * 
	 * @param idFceIncome
	 * @
	 */
	@Override
	public FceIncome findFceIncomeOrResource(long idFceIncome) {
		FceIncome fceIncome = (FceIncome) sessionFactory.getCurrentSession().get(FceIncome.class, idFceIncome);

		if (!ObjectUtils.isEmpty(fceIncome)) {
			return fceIncome;
		} else {
			throw new DataLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
	}

	/**
	 * 
	 * Method Name: saveNoTimestampCheck Method Description:
	 * 
	 * @
	 */
	public FceIncome saveNoTimestampCheck(FceIncomeDto fceIncomeDto) {
		FceIncome fceIncome = null;
		if (!ObjectUtils.isEmpty(fceIncomeDto.getIdFceIncome())) {
			fceIncome = (FceIncome) sessionFactory.getCurrentSession().get(FceIncome.class,
					fceIncomeDto.getIdFceIncome());
		}
		if (ObjectUtils.isEmpty(fceIncome)) {
			fceIncome = new FceIncome();
		}

		if (!ObjectUtils.isEmpty(fceIncomeDto.getAmtIncome()) && String.valueOf(fceIncomeDto.getAmtIncome()) != "") {
			fceIncome.setAmtIncome(fceIncomeDto.getAmtIncome());
		}
		if (!TypeConvUtil.isNullOrEmpty(fceIncomeDto.getCdType())) {
			fceIncome.setCdType(fceIncomeDto.getCdType());
		}

		if (!TypeConvUtil.isNullOrEmpty(fceIncomeDto.getIdIncRsrc())) {
			fceIncome.setIdIncRsrc(fceIncomeDto.getIdIncRsrc());
		}

		// can be changed in the page
		if (!TypeConvUtil.isNullOrEmpty(fceIncomeDto.getIndCountable())) {
			fceIncome.setIndCountable(fceIncomeDto.getIndCountable());
		}
		// can be changed in the page
		if (!TypeConvUtil.isNullOrEmpty(fceIncomeDto.getIndEarned())) {
			fceIncome.setIndEarned(fceIncomeDto.getIndEarned());
		}
		// can be changed in the page
		if (!TypeConvUtil.isNullOrEmpty(fceIncomeDto.getIndNone())) {
			fceIncome.setIndNone(fceIncomeDto.getIndNone());
		}

		if (!TypeConvUtil.isNullOrEmpty(fceIncomeDto.getDtLastUpdate())) {
			fceIncome.setDtLastUpdate(fceIncomeDto.getDtLastUpdate());
		}
		fceIncome.setDtLastUpdate(new Date());

		return fceIncome;

	}

	/**
	 * Method Name: syncFceApplicationStatus Method Description: Sync
	 * Application data
	 * 
	 * @param IncomeExpenditureDto
	 */
	public void syncFceApplicationStatus(FceEligibilityDto fceEligibilityDto) {
		long idFceApplication = fceEligibilityDto.getIdFceApplication().longValue();
		long idFceEligibility = fceEligibilityDto.getIdFceEligibility().longValue();
		long idFcePerson = fceEligibilityDto.getIdFcePerson().longValue();
		long idPerson = fceEligibilityDto.getIdPerson().longValue();

		// copy CD_PERSON_CITIZENSHIP from person_dtl to eligibility
		String cdPersonCitizenship = fceApplicationDao.getCdPersonCitizenship(idPerson);

		SQLQuery updateEligibilityQuery = sessionFactory.getCurrentSession().createSQLQuery(updateFceEligibilitySql);
		updateEligibilityQuery.setParameter("cdPersonCitizenship", cdPersonCitizenship);
		updateEligibilityQuery.setParameter("idFceEligibility", idFceEligibility);
		updateEligibilityQuery.setParameter("idFceApplication", idFceApplication);
		updateEligibilityQuery.executeUpdate();

		// IND_EVALUATION_CONCLUSION; for AgeCitizenship
		String indEvaluationConclusion = fceApplicationDao.getIndEvaluationConclusion(idFceApplication);

		SQLQuery updateFceApplicationQuery = sessionFactory.getCurrentSession().createSQLQuery(updateFceApplicationSql);
		updateFceApplicationQuery.setParameter("indEvalConclusion", indEvaluationConclusion);
		updateFceApplicationQuery.setParameter("idFceEligibility", idFceEligibility);
		updateFceApplicationQuery.setParameter("idFceApplication", idFceApplication);
		updateEligibilityQuery.executeUpdate();

		// copy person birthday to fce_person birthday (& calc age)
		FcePersonDto fcePersonLocal = fceApplicationDao.findFcePerson(idFcePerson);
		PersonDto personEntity = personDao.getPersonById(idPerson);

		PersonDto personDto = new PersonDto();
		personDto.setDtPersonBirth(personEntity.getDtPersonBirth());
		personDto.setIndPersonDobApprox(personEntity.getIndPersonDobApprox());

		// update person bdy dtl
		fceApplicationDao.updateBirthday(fcePersonLocal, personDto);

	}

	/**
	 * Method Name: getPersonIncomeResourceRequest Method Description; this
	 * method will fetch persons income and resources under the idPerson
	 * 
	 * @param PersonId
	 * @return IncomeAndResources
	 */
	public List<IncomeAndResources> getPersonIncomeForIdPerson(Long personId) {
		Person person = null;
		Query queryPerson = sessionFactory.getCurrentSession().createQuery(getPersonByIdSql);
		queryPerson.setParameter("idSearch", personId);

		person = (Person) queryPerson.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.attributes", null, Locale.US));
		}

		List<IncomeAndResources> list = Lists.newArrayList(person.getIncomeAndResourcesesForIdPerson());

		if (TypeConvUtil.isNullOrEmpty(list)) {
			throw new DataNotFoundException(
					messageSource.getMessage("incomeandresouce.not.found.attributes", null, Locale.US));
		}

		return list;
	}

	/**
	 * Method Name: fetchPersonCharacsWithDate Method Description: to save
	 * IncomeAndResources of a person with person id and effective date
	 * 
	 * @param incomeAndResources
	 */
	@Override
	public void saveIncomeAndResource(IncomeAndResources incomeAndResources) {
		sessionFactory.getCurrentSession().saveOrUpdate(incomeAndResources);
	}

	/**
	 * 
	 * Method Name: deleteIncomeAndResourceById Method Description: to delete
	 * income and resources of a person corresponding to the person id
	 * 
	 * @param id
	 */
	@Override
	public void deleteIncomeAndResourceById(Long id) {
		IncomeAndResources incomeAndResourcesObj = (IncomeAndResources) sessionFactory.getCurrentSession()
				.load(IncomeAndResources.class, id);
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().delete(incomeAndResourcesObj);
	}
}