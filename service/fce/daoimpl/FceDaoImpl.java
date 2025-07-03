package us.tx.state.dfps.service.fce.daoimpl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.EligNarr;
import us.tx.state.dfps.common.domain.Eligibility;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.FceDomclPrsnWelf;
import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FceIncome;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.FceReasonNotEligible;
import us.tx.state.dfps.common.domain.FstrCareAstAppNarr;
import us.tx.state.dfps.common.domain.FstrCareAstRvwNarr;
import us.tx.state.dfps.common.domain.IncomeAndResources;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonDtl;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceDomicilePersonWelfDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;
import us.tx.state.dfps.service.fostercarereview.dao.FceIncomeDao;
import us.tx.state.dfps.service.fostercarereview.dao.FcePersonDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Repository
public class FceDaoImpl implements FceDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private FcePersonDao fcePersonDao;

	@Value("${FceDaoImpl.createBlankRecordsForFcePersons}")
	private String createBlankRecordsForFcePersonsSql;

	@Value("${FceDaoImpl.copyIncomeResources}")
	private String copyIncomeResourcesSql;

	@Value("${FceDaoImpl.getPersonListByStage}")
	private String getPersonListByStageSql;

	@Value("${FceDaoImpl.deleteNonPrinciples}")
	private String deleteNonPrinciplesSql;

	@Value("${FceDaoImpl.hasDOBChangedForCertPers}")
	private String hasDOBChangedForCertPers;

	@Value("${FceDaoImpl.getIndEvaluationConclusionSql}")
	private String getIndEvaluationConclusionSql;

	@Value("${FceDaoImpl.createPrinciplesSql}")
	private String createPrinciplesSql;

	@Value("${FceDaoImpl.findLatestEligForStage}")
	private String findLatestEligForStage;

	@Value("${FceDaoImpl.findSsi}")
	private String findSsiSql;

	@Value("${FceDaoImpl.isChildNewToSubcare}")
	private String isChildNewToSubcareSql;

	@Value("${FceDaoImpl.deleteFceDepCareDeductAdultPerson}")
	private String deleteFceDepCareDeductAdultPersonSql;

	@Value("${FceDaoImpl.deleteFceDepCareDeductDependentPerson}")
	private String deleteFceDepCareDeductDependentPersonSql;

	@Value("${FceDaoImpl.deleteFceApplication}")
	private String deleteFceApplicationSql;

	@Value("${FceDaoImpl.findEligByPersonId}")
	private String findEligByPersonIdSql;

	@Value("${FceDaoImpl.saveEligFirstValidation}")
	private String saveEligFirstValidationSql;

	@Value("${FceDaoImpl.saveEligSecondValidation}")
	private String saveEligSecondValidationSql;

	@Value("${FceDaoImpl.saveEligThirdValidation}")
	private String saveEligThirdValidationSql;

	@Value("${FceDaoImpl.saveEligFourthValidation}")
	private String saveEligFourthValidationSql;

	@Value("${FceDaoImpl.saveEligFifthValidation}")
	private String saveEligFifthValidationSql;

	@Value("${FceDaoImpl.determineEligWasCourtOrd}")
	private String determineEligWasCourtOrdSql;

	@Value("${FceDaoImpl.saveEligSeventhValidation}")
	private String saveEligSeventhValidationSql;

	@Value("${FceDaoImpl.findEligByidEligibilityEvent}")
	private String findEligByidEligibilityEventSql;

	@Value("${FceDaoImpl.updateEligFirstValidation}")
	private String updateEligFirstValidationSql;

	@Value("${FceDaoImpl.updateEligSecondValidation}")
	private String updateEligSecondValidationSql;

	@Value("${FceDaoImpl.updateEligThirdValidation}")
	private String updateEligThirdValidationSql;

	@Value("${FceDaoImpl.updateEligFourthValidation}")
	private String updateEligFourthValidationSql;

	@Value("${FceDaoImpl.findEligByidStage}")
	private String findEligByidStage;

	@Autowired
	private LookupDao lookupdao;

	@Autowired
	private FceIncomeDao fceIncomeDao;

	private static final Logger LOG = Logger.getLogger(FceDaoImpl.class);

	private static final char SQUARE_BRACKET_CLOSE = ')';
	private static final char SQUARE_BRACKET_OPEN = '(';

	@Autowired TodoDao todoDao;
	
	@Override
	public FceApplication save(FceApplication fceApplication) {
		sessionFactory.getCurrentSession().saveOrUpdate(fceApplication);

		return fceApplication;
	}

	@Override
	public String deleteFceApplication(Long idFceApplication) {
		FceApplication fceApplication = (FceApplication) sessionFactory.getCurrentSession().get(FceApplication.class,
				idFceApplication);
		sessionFactory.getCurrentSession().delete(fceApplication);
		sessionFactory.getCurrentSession().flush();
		return ServiceConstants.SUCCESS;
	}

	@Override
	public FceApplicationDto getFceApplicationById(Long idFceApplication) {

		FceApplication fceApplication = getFceApplication(idFceApplication);
		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		BeanUtils.copyProperties(fceApplication, fceApplicationDto);
		fceApplicationDto.setIdPerson(fceApplication.getPersonByIdPerson().getIdPerson());
		fceApplicationDto.setIdLastUpdatePerson(fceApplication.getPersonByIdLastUpdatePerson().getIdPerson());
		fceApplicationDto.setIdEvent(fceApplication.getEvent().getIdEvent());
		fceApplicationDto.setIdStage(fceApplication.getStage().getIdStage());
		fceApplicationDto.setIdFceApplication(idFceApplication);
		fceApplicationDto.setIdFceEligibility(fceApplication.getFceEligibility().getIdFceEligibility());
		return fceApplicationDto;

	}

	@Override
	public FceApplicationDto findApplicationByApplicationEvent(Long idEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceApplication.class);
		criteria.add(Restrictions.eq("event.idEvent", idEvent));
		FceApplication fceApplication = (FceApplication) criteria.uniqueResult();
		if (ObjectUtils.isEmpty(fceApplication)) {
			return null;
		}
		return transformFceApplication(fceApplication);
	}

	public FceApplicationDto transformFceApplication(FceApplication fceApplication) {
		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		BeanUtils.copyProperties(fceApplication, fceApplicationDto);

		if (!ObjectUtils.isEmpty(fceApplication.getPersonByIdPerson()))
			fceApplicationDto.setIdPerson(fceApplication.getPersonByIdPerson().getIdPerson());

		if (!ObjectUtils.isEmpty(fceApplication.getPersonByIdLastUpdatePerson()))
			fceApplicationDto.setIdLastUpdatePerson(fceApplication.getPersonByIdLastUpdatePerson().getIdPerson());

		if (!ObjectUtils.isEmpty(fceApplication.getEvent()))
			fceApplicationDto.setIdEvent(fceApplication.getEvent().getIdEvent());

		if (!ObjectUtils.isEmpty(fceApplication.getStage()))
			fceApplicationDto.setIdStage(fceApplication.getStage().getIdStage());

		if (!ObjectUtils.isEmpty(fceApplication.getNbrCourtMonth()))
			fceApplicationDto.setNbrCourtMonth(fceApplication.getNbrCourtMonth().longValue());

		if (!ObjectUtils.isEmpty(fceApplication.getNbrCourtYear()))
			fceApplicationDto.setNbrCourtYear(fceApplication.getNbrCourtYear().longValue());

		fceApplicationDto.setIdFceApplication(fceApplication.getIdFceApplication());

		if (!ObjectUtils.isEmpty(fceApplication.getFceEligibility()))
			fceApplicationDto.setIdFceEligibility(fceApplication.getFceEligibility().getIdFceEligibility());

		if (!ObjectUtils.isEmpty(fceApplication.getNbrLivingAtHome()))
			fceApplicationDto.setNbrLivingAtHome(fceApplication.getNbrLivingAtHome().longValue());

		fceApplicationDto.setNbrGcdLat(fceApplication.getNbrGcdLat());
		fceApplicationDto.setNbrGcdLong(fceApplication.getNbrGcdLong());
		fceApplicationDto.setTxtMailbltyScore(fceApplication.getTxtMailbltyScore());
		fceApplicationDto.setIndValdtd(fceApplication.getIndValdtd());
		fceApplicationDto.setDtValdtd(fceApplication.getDtValdtd());
		fceApplicationDto.setCdAddrRtrn(fceApplication.getCdAddrRtrn());
		fceApplicationDto.setCdGcdRtrn(fceApplication.getCdGcdRtrn());
		// Added this code to set the AFDC eligible month and year - Warranty defect 11610
		if (!ObjectUtils.isEmpty(fceApplication.getNbrAfdcEligMonth()))
			fceApplicationDto.setNbrAfdcEligMonth(fceApplication.getNbrAfdcEligMonth().longValue());
		if (!ObjectUtils.isEmpty(fceApplication.getNbrAfdcEligYear()))
			fceApplicationDto.setNbrAfdcEligYear(fceApplication.getNbrAfdcEligYear().longValue());

		return fceApplicationDto;
	}

	@Override
	public FceApplication getFceApplication(Long idFceApplication) {
		return (FceApplication) sessionFactory.getCurrentSession().get(FceApplication.class, idFceApplication);
	}

	@Override
	public Long createFcEligibility(Long idPerson, Long idCase, Long idLastUpdatePerson, Long idStage) {

		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdCase(idCase);

		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (ObjectUtils.isEmpty(stage)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.stageId", null, Locale.US));
		}

		fceEligibility.setStage(stage);

		fceEligibility.setDtLastUpdate(new Date());

		Person personByIdLastUpdatePerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
				idLastUpdatePerson);
		if (ObjectUtils.isEmpty(personByIdLastUpdatePerson)) {
			throw new DataNotFoundException(
					messageSource.getMessage("person.not.found.personByIdLastUpdatePerson", null, Locale.US));
		}

		fceEligibility.setIdPerson(idPerson);
		fceEligibility.setPerson(personByIdLastUpdatePerson);
		Long idFceEligibility = (Long) sessionFactory.getCurrentSession().save(fceEligibility);

		return idFceEligibility;
	}

	/**
	 * 
	 * Method Name: createApplication Method Description:
	 * 
	 * @param fceApplicationDto
	 * @return fceApplication @
	 */
	@Override
	public Long createFceApplication(FceApplicationDto fceApplicationDto) {

		FceApplication fceApplication = new FceApplication();

		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, fceApplicationDto.getIdEvent());
		if (ObjectUtils.isEmpty(event)) {
			throw new DataNotFoundException(messageSource.getMessage("event.not.found.eventId", null, Locale.US));
		}

		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
				fceApplicationDto.getIdFceEligibility());
		if (ObjectUtils.isEmpty(fceEligibility)) {
			throw new DataNotFoundException(
					messageSource.getMessage("fceEligibility.not.found.fceEligibilityId", null, Locale.US));
		}

		Person personByIdLastUpdatePerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
				fceApplicationDto.getIdLastUpdatePerson());
		if (ObjectUtils.isEmpty(personByIdLastUpdatePerson)) {
			throw new DataNotFoundException(
					messageSource.getMessage("person.not.found.personByIdLastUpdatePerson", null, Locale.US));
		}

		Person personByIdPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
				fceApplicationDto.getIdPerson());
		if (ObjectUtils.isEmpty(personByIdPerson)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.personId", null, Locale.US));
		}

		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, fceApplicationDto.getIdStage());
		if (ObjectUtils.isEmpty(stage)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.stageId", null, Locale.US));
		}

		fceApplication.setCdApplication(fceApplicationDto.getCdApplication());
		fceApplication.setIdCase(fceApplicationDto.getIdCase());
		fceApplication.setDtLastUpdate(new Date());
		fceApplication.setEvent(event);
		fceApplication.setFceEligibility(fceEligibility);
		fceApplication.setPersonByIdLastUpdatePerson(personByIdLastUpdatePerson);
		fceApplication.setPersonByIdPerson(personByIdPerson);
		fceApplication.setStage(stage);

		Long idFceApplication = (Long) sessionFactory.getCurrentSession().save(fceApplication);

		FcePerson fcePerson = new FcePerson();
		fcePerson.setDtLastUpdate(new Date());

		fcePerson.setPerson(personByIdPerson);
		fcePerson.setFceEligibility(fceEligibility);
		Long idFcePerson = (Long) sessionFactory.getCurrentSession().save(fcePerson);
		fcePerson.setIdFcePerson(idFcePerson);

		Set<FcePerson> fcePersonSet = new HashSet<>();
		fcePersonSet.add(fcePerson);
		fceEligibility.setIdFceEligibility(fceApplicationDto.getIdFceEligibility());
		fceEligibility.setIdFcePerson(idFcePerson);
		fceEligibility.setFcePersons(fcePersonSet);
		fceEligibility.setIdFceApplication(idFceApplication);
		fceEligibility.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(fceEligibility);

		return idFceApplication;
	}

	/**
	 * 
	 * Method Name: getCdPersonCitizenship Method Description:
	 * 
	 * @param idPerson
	 * @return CdPersonCitizenship
	 */
	@Override
	public String getCdPersonCitizenship(Long idPerson) {
		String cdPersonCitizenship = null;
		PersonDtl personDtl = (PersonDtl) sessionFactory.getCurrentSession().get(PersonDtl.class, idPerson);
		if (!ObjectUtils.isEmpty(personDtl)) {
			cdPersonCitizenship = personDtl.getCdPersonCitizenship();
		}
		return cdPersonCitizenship;
	}

	@Override
	public void createIncomeForFcePersons(Long idFceEligibility, Long childIdFcePerson, Long childIdPerson) {

		List<FcePerson> fcePersons = (List<FcePerson>) fcePersonDao.getFcePersonsbyEligibilityId(idFceEligibility);
		Map<Long, Long> personMap = new HashMap<>();
		for (FcePerson fcePerson : fcePersons) {
			personMap.put(fcePerson.getPerson().getIdPerson(), fcePerson.getIdFcePerson());

		}
		Map<String, Set<Long>> IncomeAndResource = new HashMap<>();

		updateIncomeResources(personMap, idFceEligibility, childIdFcePerson, IncomeAndResource);
		Set<Long> incomeSet = IncomeAndResource.get(ServiceConstants.CINCORES_INC);
		Set<Long> resourceSet = IncomeAndResource.get(CodesConstant.CCMACTYP_RES);
		List<FcePerson> incomeFcePersons = fcePersons.stream()
				.filter(o -> !incomeSet.contains(o.getPerson().getIdPerson())).collect(Collectors.toList());
		List<FcePerson> resourceFcePersons = fcePersons.stream()
				.filter(o -> !resourceSet.contains(o.getPerson().getIdPerson())).collect(Collectors.toList());
		createBlankRecordsForFcePersons(idFceEligibility, childIdFcePerson, childIdPerson, ServiceConstants.INCOME,
				incomeFcePersons);

		createBlankRecordsForFcePersons(idFceEligibility, childIdFcePerson, childIdPerson, ServiceConstants.RESOURCE,
				resourceFcePersons);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Long updateIncomeResources(Map<Long, Long> personMap, Long idFceEligibility, Long childIdFcePerson,
			Map<String, Set<Long>> IncomeAndResource) {
		Collection personSetString = personMap.keySet();
		/**
		 * delete records from fce_income that are no longer valid in
		 * income_and_resources
		 */
		SQLQuery query = sessionFactory.openSession().createSQLQuery(copyIncomeResourcesSql);

		query.setParameterList(ServiceConstants.ID_PERSON_LIST, personMap.keySet());
		query.setParameter("idEceEligibility", idFceEligibility);
		query.executeUpdate();

		List<FceIncome> fceIncomesList = fceIncomeDao.getFceIncomesByIdElig(idFceEligibility);
		Set<Long> incomeSet = fceIncomesList.stream().filter(o -> ServiceConstants.Y.equals(o.getIndIncomeSource()))
				.map(o -> o.getPerson().getIdPerson()).collect(Collectors.toSet());
		Set<Long> resourceSet = fceIncomesList.stream().filter(o -> ServiceConstants.Y.equals(o.getIndResourceSource()))
				.map(o -> o.getPerson().getIdPerson()).collect(Collectors.toSet());
		List<IncomeAndResources> incomeAndResourceList = sessionFactory.getCurrentSession()
				.createCriteria(IncomeAndResources.class)
				.add(Restrictions.in("personByIdPerson.idPerson", personSetString))
				.add(Restrictions.gt("dtIncRsrcTo", new Date())).list();
		// Set<Long> incomeSet= new HashSet<>();
		// Set<Long> resourceSet= new HashSet<>();
		for (IncomeAndResources incomeAndResources : incomeAndResourceList) {
			Long idIncRsrc = incomeAndResources.getIdIncRsrc();
			Long idPerson = incomeAndResources.getPersonByIdPerson().getIdPerson();
			Long idFcePerson = personMap.get(idPerson);
			FceIncome fceIncome = fceIncomesList.stream().filter(o -> !ObjectUtils.isEmpty(o.getIdIncRsrc())
					&& idIncRsrc.equals(o.getIdIncRsrc()) && idPerson.equals(o.getPerson().getIdPerson())).findFirst()
					.orElse(null);
			if (ServiceConstants.CINCORES_INC.equals(incomeAndResources.getCdIncRsrcIncome())) {
				incomeSet.add(idPerson);
			}
			if (CodesConstant.CCMACTYP_RES.equals(incomeAndResources.getCdIncRsrcIncome())) {
				resourceSet.add(idPerson);
			}
			if (ObjectUtils.isEmpty(fceIncome)) {
				// incoem record is not present before, create FCE income record
				fceIncome = new FceIncome();
				FceEligibility fceEligibility = new FceEligibility();
				fceEligibility.setIdFceEligibility(idFceEligibility);
				fceIncome.setFceEligibility(fceEligibility);
				FcePerson fecPerson = new FcePerson();
				fecPerson.setIdFcePerson(idFcePerson);
				fceIncome.setFcePerson(fecPerson);
				// Delete dummy income record when valid income record exists
				if (ServiceConstants.CINCORES_INC.equals(incomeAndResources.getCdIncRsrcIncome())) {
					FceIncome fceIncomeToDelete = fceIncomesList.stream()
							.filter(o -> idPerson.equals(o.getPerson().getIdPerson())
									&& ObjectUtils.isEmpty(o.getCdType())
									&& ServiceConstants.YES.equals(o.getIndIncomeSource()))
							.findFirst().orElse(null);
					if (!ObjectUtils.isEmpty(fceIncomeToDelete))
						fceIncomeDao.deleteFceIncome(fceIncomeToDelete);
				}
				// Delete dummy income record when valid income record exists
				if (CodesConstant.CCMACTYP_RES.equals(incomeAndResources.getCdIncRsrcIncome())) {
					FceIncome fceIncomeToDelete = fceIncomesList.stream()
							.filter(o -> idPerson.equals(o.getPerson().getIdPerson())
									&& ObjectUtils.isEmpty(o.getCdType())
									&& ServiceConstants.YES.equals(o.getIndResourceSource()))
							.findFirst().orElse(null);
					if (!ObjectUtils.isEmpty(fceIncomeToDelete))
						fceIncomeDao.deleteFceIncome(fceIncomeToDelete);
				}
			}
			fceIncome.setIdIncRsrc(idIncRsrc);
			if (!ObjectUtils.isEmpty(idPerson) && idPerson > 0) {
				Person person = new Person();
				person.setIdPerson(idPerson);
				fceIncome.setPerson(person);
				boolean isChild = (idFcePerson.equals(childIdFcePerson));
				if (isChild) {
					fceIncome.setIndChild(ServiceConstants.YES);
				} else {
					fceIncome.setIndFamily(ServiceConstants.YES);
				}

				if (idFcePerson.equals(childIdFcePerson)) {
					fceIncome.setIndChild(ServiceConstants.YES);
				} else {
					fceIncome.setIndFamily(ServiceConstants.YES);
				}

				String incomeOrResource = incomeAndResources.getCdIncRsrcIncome();
				if (ServiceConstants.CINCORES_INC.equals(incomeOrResource)) {
					fceIncome.setIndIncomeSource(ServiceConstants.YES);
					fceIncome.setIndResourceSource(ServiceConstants.NO);
				} else {
					fceIncome.setIndIncomeSource(ServiceConstants.NO);
					fceIncome.setIndResourceSource(ServiceConstants.YES);
				}

				Optional.ofNullable(incomeAndResources.getAmtIncRsrc()).map(s -> (s).doubleValue())
						.ifPresent(fceIncome::setAmtIncome);
				fceIncome.setCdType(incomeAndResources.getCdIncRsrcType());
				Optional.ofNullable(incomeAndResources.getIndIncRsrcNotAccess()).map(s -> (String.valueOf(s)))
						.ifPresent(fceIncome::setIndNotAccessible);
				fceIncome.setTxtSource(incomeAndResources.getSdsIncRsrcSource());
				fceIncome.setTxtVerificationMethod(incomeAndResources.getSdsIncRsrcVerfMethod());
				fceIncome.setTxtComments(incomeAndResources.getTxtIncRsrcDesc());
				fceIncome.setDtLastUpdate(new Date());

				// ALM ID : 131385 : During income for a child, earned and countable radio buttons should be selected automatically
				if(CodesConstant.CINC_GRS.equals(incomeAndResources.getCdIncRsrcType())){
					fceIncome.setIndCountable(ServiceConstants.YES);
					fceIncome.setIndEarned(ServiceConstants.YES);
				}
				sessionFactory.getCurrentSession().saveOrUpdate(fceIncome);
			}
		}
		IncomeAndResource.put(ServiceConstants.CINCORES_INC, incomeSet);
		IncomeAndResource.put(CodesConstant.CCMACTYP_RES, resourceSet);
		return childIdFcePerson;
	}

	/**
	 * 
	 * Method Name: deleteNonPrinciples Method Description:
	 * 
	 * @param idFceEligibility
	 * @param idStage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteNonPrinciples(Long idFceEligibility, Long idStage) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.openSession().createSQLQuery(deleteNonPrinciplesSql)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setLong(ServiceConstants.ID_FCE_ELIGIBLITY, idFceEligibility)
				.setLong(ServiceConstants.idStage, idStage);

		// Warranty Defect - 11424 - Incorrect Mapping of DataType
		List<Long> nonPrinciples = sqlQuery.list();

		if (ObjectUtils.isEmpty(nonPrinciples) || nonPrinciples.isEmpty()) {
			return;
		}

		/**
		 * delete fce_income if he's no longer a principal
		 */
		List<FcePerson> fcePersonList = (List<FcePerson>) fcePersonDao.getFcePersonsbyEligibilityId(idFceEligibility);
		for (FcePerson fcePerson : fcePersonList) {
			if (nonPrinciples.contains(fcePerson.getIdFcePerson())) {
				fcePerson.getFceIncomes();
				fcePerson.getFceDepCareDeductsForIdFceAdultPerson();
				fcePerson.getFceDepCareDeductsForIdFceDependentPerson();
				sessionFactory.getCurrentSession().delete(fcePerson);
			}
		}
	}

	/**
	 * 
	 * Method Name: createPrinciples Method Description:
	 * 
	 * @param idFceEligibility
	 * @param idStage
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void createPrinciples(Long idFceEligibility, Long idStage) {

		SQLQuery sqlQuery = (SQLQuery) ((SQLQuery) sessionFactory.openSession().createSQLQuery(createPrinciplesSql)
				.setParameter("idStage", idStage).setParameter("cdStagePersType", ServiceConstants.PRN_TYPE))
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
						.addScalar("nmPersonFull", StandardBasicTypes.STRING)
						.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
						.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
						.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
						.addScalar("nmPersonLast", StandardBasicTypes.STRING)
						.addScalar("addrPersonStLn1", StandardBasicTypes.STRING)
						.addScalar("addrPersonCity", StandardBasicTypes.STRING)
						.addScalar("addrPersonZip", StandardBasicTypes.STRING)
						.addScalar("cdPersonState", StandardBasicTypes.STRING)
						.addScalar("cdPersonCounty", StandardBasicTypes.STRING)
						.addScalar("cdRelInt", StandardBasicTypes.STRING)
						.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(PersonDto.class));

		List<PersonDto> personDtoList = sqlQuery.list();

		List<FcePerson> fcePersonList = (List<FcePerson>) fcePersonDao.getFcePersonsbyEligibilityId(idFceEligibility);
		for (PersonDto personDto : personDtoList) {
			Long idPerson = personDto.getIdPerson();
			FcePerson fcePerson = fcePersonList.stream()
					.filter(o -> o.getPerson().getIdPerson().equals(personDto.getIdPerson())).findFirst().orElse(null);
			if (ObjectUtils.isEmpty(fcePerson))
				fcePerson = fcePersonDao.save(idFceEligibility, idPerson);
			fcePerson.setCdRelInt(personDto.getCdRelInt());
			if (!ObjectUtils.isEmpty(personDto.getDtPersonBirth())) {
				fcePerson.setNbrAge((short) DateUtils.getAge(personDto.getDtPersonBirth()));
				fcePerson.setDtBirth(personDto.getDtPersonBirth());
			}
			fcePerson.setIndDobApprox(personDto.getIndPersonDobApprox());
			sessionFactory.getCurrentSession().saveOrUpdate(fcePerson);
		}
	}

	@Override
	public Long createBlankRecordsForFcePersons(Long idFceEligibility, Long childIdFcePerson, Long childIdPerson,
			boolean incomeOrResource, List<FcePerson> personList) {

		for (FcePerson fcePerson : personList) {
			createBlankFceIncome(idFceEligibility, fcePerson.getIdFcePerson(), fcePerson.getPerson().getIdPerson(),
					(fcePerson.getIdFcePerson() == childIdFcePerson.longValue()), incomeOrResource);
		}
		return childIdPerson;

	}

	private void createBlankFceIncome(Long idFceEligibility, Long idFcePerson, Long idPerson, boolean isChild,
			boolean incomeOrResource) {
		FceIncome fceIncome = new FceIncome();

		Person person = new Person();
		person.setIdPerson(idPerson);
		fceIncome.setPerson(person);
		FcePerson fcePerson = new FcePerson();
		fcePerson.setIdFcePerson(idFcePerson);
		fceIncome.setFcePerson(fcePerson);
		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdFceEligibility(idFceEligibility);
		fceIncome.setFceEligibility(fceEligibility);
		if (isChild) {
			fceIncome.setIndChild(ServiceConstants.YES);
		} else {
			fceIncome.setIndFamily(ServiceConstants.YES);
		}
		if (incomeOrResource) {
			fceIncome.setIndIncomeSource(ServiceConstants.YES);
			fceIncome.setIndResourceSource(ServiceConstants.NO);
		} else {
			fceIncome.setIndResourceSource(ServiceConstants.YES);
			fceIncome.setIndIncomeSource(ServiceConstants.NO);
		}
		fceIncome.setCdType(null);
		fceIncome.setDtLastUpdate(new Date());
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(fceIncome);
		session.flush();
	}

	/**
	 * 
	 * Method Name: getIndEvaluationConclusion Method Description:
	 * 
	 * @param idFceApplication
	 * @return ServiceConstants.N or ServiceConstants.Y
	 */
	@Override
	public String getIndEvaluationConclusion(Long idFceApplication) {

		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(getIndEvaluationConclusionSql);
		query.setLong("idFceApplication", idFceApplication);
		query.addScalar("idNarrativeEvent", StandardBasicTypes.LONG);

		Long idNarrativeEvent = (long) query.list().size();

		if (idNarrativeEvent != 0) {
			return ServiceConstants.Y;
		}
		return ServiceConstants.N;
	}

	/**
	 * Method Name: copyInto Method copies fceEligibility properties to
	 * fceEligibilityDto
	 * 
	 * @param fceEligibilityDto
	 * @param fceEligibility
	 * @return
	 */
	public void copyInto(FceEligibility fceEligibility, FceEligibilityDto fceEligibilityDto) {
		BeanUtils.copyProperties(fceEligibility, fceEligibilityDto);
		fceEligibilityDto.setIdLastUpdatePerson(fceEligibility.getPerson().getIdPerson());
		fceEligibilityDto.setIdStage(fceEligibility.getStage().getIdStage());
	}

	/**
	 * 
	 * Method Name: getFceEligibility Method Description: Method to get
	 * FceEligiblity
	 * 
	 * @param idFceEligibility
	 * @return FceEligibility
	 */
	public FceEligibilityDto getFceEligibility(Long idFceEligibility) {
		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().load(FceEligibility.class,
				idFceEligibility);

		if (ObjectUtils.isEmpty(fceEligibility)) {
			throw new DataNotFoundException(
					messageSource.getMessage("fceEligibility.stage.not.found.fceEligibilityId", null, Locale.US));
		}
		FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
		copyInto(fceEligibility, fceEligibilityDto);
		return fceEligibilityDto;
	}

	@Override
	public Long createSemaphore(Long idApplicationEvent, String fceApplicationTable, String fceApplicationColumn) {

		StringBuilder sqlQuery = new StringBuilder();
		sqlQuery.append(ServiceConstants.INSERT_INTO).append(ServiceConstants.CHAR_SPACE).append(fceApplicationTable)
				.append(ServiceConstants.CHAR_SPACE).append(SQUARE_BRACKET_OPEN).append(ServiceConstants.CHAR_SPACE)
				.append(fceApplicationColumn).append(ServiceConstants.CHAR_SPACE).append(SQUARE_BRACKET_CLOSE)
				.append(ServiceConstants.VALUES).append(SQUARE_BRACKET_OPEN).append(ServiceConstants.CHAR_SPACE)
				.append(ServiceConstants.idApplication).append(ServiceConstants.CHAR_SPACE)
				.append(SQUARE_BRACKET_CLOSE);
		SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString());
		query.setParameter(ServiceConstants.idApplications, idApplicationEvent);
		return (long) query.executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fceapplication.dao.FceApplicationDao#
	 * findFcePerson(java.lang.Long)
	 */
	@Override
	public FcePersonDto findFcePerson(Long idFcePerson) {
		FcePersonDto fcePersonDto = new FcePersonDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FcePerson.class);
		criteria.add(Restrictions.eq("idFcePerson", idFcePerson));

		FcePerson fcePerson = (FcePerson) criteria.uniqueResult();
		if (ObjectUtils.isEmpty(fcePerson)) {
			return null;
		}
		BeanUtils.copyProperties(fcePerson, fcePersonDto);
		if (!ObjectUtils.isEmpty(fcePerson.getPerson())) {
			fcePersonDto.setIdPerson(fcePerson.getPerson().getIdPerson());
		}
		if (!ObjectUtils.isEmpty(fcePerson.getFceEligibility())) {
			fcePersonDto.setIdFceEligibility(fcePerson.getFceEligibility().getIdFceEligibility());
		}
		return fcePersonDto;
	}

	/**
	 * 
	 * Method Name: hasDOBChangedForCertPers Method Description:
	 * 
	 * @param idFceEligibility
	 * @return hasDOBChanged
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasDOBChangedForCertPers(Long idFceEligibility) {
		boolean hasDOBChanged = false;

		Criteria fcePersonCriteria = sessionFactory.getCurrentSession().createCriteria(FcePerson.class);
		fcePersonCriteria.add(Restrictions.eq(ServiceConstants.FCE_ELIGIBILITY_ID_FCE_ELIGIBLITY, idFceEligibility));
		fcePersonCriteria.add(Restrictions.eq("indCertifiedGroup", ServiceConstants.Y));
		List<FcePerson> fcePersonList = fcePersonCriteria.list();

		if (!ObjectUtils.isEmpty(fcePersonList) && fcePersonList.size() > 0) {
			for (FcePerson fcePerson : fcePersonList) {
				if ((!ObjectUtils.isEmpty(fcePerson.getDtBirth())) && !ObjectUtils.isEmpty(fcePerson.getPerson())
						&& (fcePerson.getDtBirth().compareTo(fcePerson.getPerson().getDtPersonBirth()) != 0)) {
					hasDOBChanged = true;
					break;
				}
			}
		}
		return hasDOBChanged;
	}

	/**
	 * This method finds the latest foster care eligibility for a given
	 * eligibility event id
	 * 
	 * @param idEligibilityEvent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FceEligibilityDto findLatestEligibilityForEligibilityEvent(Long idEligibilityEvent) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceEligibility.class);
		criteria.add(Restrictions.eq("idEligibilityEvent", idEligibilityEvent));
		List<FceEligibility> fceEligibilitylist = criteria.list();
		FceEligibility fceEligibility = null;
		FceEligibilityDto fceEligibilityDto = null;
		if (!ObjectUtils.isEmpty(fceEligibilitylist) && !fceEligibilitylist.isEmpty()) {
			fceEligibility = (FceEligibility) fceEligibilitylist.get(0);
			fceEligibilityDto = new FceEligibilityDto();
			copyInto(fceEligibility, fceEligibilityDto);
		}
		return fceEligibilityDto;
	}

	/**
	 * 
	 * Method Name: countNewEvents Method Description: This method is used for
	 * counting the number of new events
	 * 
	 * @param taskCode
	 * @param idStage
	 * @return idEvent
	 */
	@Override
	public Long countNewEvents(String taskCode, Long idStage) {

		Long newEligibilityEvent = null;

		Criteria stageCriteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		stageCriteria.add(Restrictions.eq(ServiceConstants.idStage, idStage));

		Stage stage = (Stage) stageCriteria.uniqueResult();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("stage", stage)).add(Restrictions.eq("cdTask", taskCode))
				.add(Restrictions.eq("cdEventStatus", ServiceConstants.NEW_EVENT));
		criteria.addOrder(Order.asc("dtEventCreated"));

		criteria.setFirstResult(0);
		criteria.setMaxResults(1);
		Event event = (Event) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(event)) {
			newEligibilityEvent = event.getIdEvent();
		}
		return newEligibilityEvent;
	}

	/**
	 * 
	 * Method Name: deleteEvent Method Description: This method is used for
	 * deleting the New event link
	 * 
	 * @param idEvent
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteEvent(Long idEvent) {

		Criteria eventPersonLinkCriteria = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
		eventPersonLinkCriteria.add(Restrictions.eq("event.idEvent", idEvent));
		List<EventPersonLink> eventPersonLinkList = eventPersonLinkCriteria.list();
		if (!ObjectUtils.isEmpty(eventPersonLinkList) && !eventPersonLinkList.isEmpty()) {
			for (EventPersonLink eventPersonLink : eventPersonLinkList) {
				sessionFactory.getCurrentSession().delete(eventPersonLink);
			}
		}

		Criteria eligNarrriteria = sessionFactory.getCurrentSession().createCriteria(EligNarr.class);
		eligNarrriteria.add(Restrictions.eq("idEvent", idEvent));
		EligNarr eligNarr = (EligNarr) eligNarrriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(eligNarr)) {
			sessionFactory.getCurrentSession().delete(eligNarr);
		}

		Criteria fstrCareAstAppNarrCriteria = sessionFactory.getCurrentSession()
				.createCriteria(FstrCareAstAppNarr.class);
		fstrCareAstAppNarrCriteria.add(Restrictions.eq("idEvent", idEvent));
		FstrCareAstAppNarr fstrCareAstAppNarr = (FstrCareAstAppNarr) fstrCareAstAppNarrCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(fstrCareAstAppNarr)) {
			sessionFactory.getCurrentSession().delete(fstrCareAstAppNarr);
		}

		Criteria fstrCareAstRvwNarrCriteria = sessionFactory.getCurrentSession()
				.createCriteria(FstrCareAstRvwNarr.class);
		fstrCareAstRvwNarrCriteria.add(Restrictions.eq("idEvent", idEvent));
		FstrCareAstRvwNarr fstrCareAstRvwNarr = (FstrCareAstRvwNarr) fstrCareAstRvwNarrCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(fstrCareAstRvwNarr)) {
			sessionFactory.getCurrentSession().delete(fstrCareAstRvwNarr);
		}

		Criteria eventCriteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		eventCriteria.add(Restrictions.eq("idEvent", idEvent));
		Event event = (Event) eventCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(event)) {
			sessionFactory.getCurrentSession().delete(event);
		}
	}

	/**
	 * 
	 * Method Name: countIncompleteEvents Method Description: This method is
	 * used for to count the number of incomplete events
	 * 
	 * @param taskCode
	 * @param idStage
	 * @return count
	 */
	@Override
	public int countIncompleteEvents(String taskCode, Long idStage) {

		int count = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.eq("cdTask", taskCode));
		criteria.add(Restrictions.ne("cdEventStatus", ServiceConstants.COMPLETE_EVENT));
		Long eventCount = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult());

		if (eventCount > 0) {
			count = eventCount.intValue();
		}
		return count;
	}

	@Override
	public Long createFceEligibility(Long idCase, Long idLastUpdatePerson, Long idStage, Long idPerson) {

		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdCase(idCase);

		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (ObjectUtils.isEmpty(stage)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.stageId", null, Locale.US));
		}

		fceEligibility.setStage(stage);

		fceEligibility.setDtLastUpdate(new Date());

		Person personByIdLastUpdatePerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
				idLastUpdatePerson);
		if (ObjectUtils.isEmpty(personByIdLastUpdatePerson)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.personId", null, Locale.US));
		}
		fceEligibility.setPerson(personByIdLastUpdatePerson);

		fceEligibility.setIdPerson(idPerson);

		Long idFceEligibility = (Long) sessionFactory.getCurrentSession().save(fceEligibility);
		return idFceEligibility;
	}

	/**
	 * Description: This method returns in latest eligibility for that Stage it
	 * gets either the last eligibility associated with a closed review event or
	 * the last eligibility associated with an approved application event
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FceEligibilityDto findLatestEligibilityForStage(Long idStage) {

		FceEligibilityDto fceEligibilityDto = null;
		Long idFceEligibility = null;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(findLatestEligForStage)
				.addScalar("idFceEligibility", StandardBasicTypes.LONG).setParameter("idStage", idStage);

		List<Long> idFceEligibilityList = (List<Long>) query.list();
		if (!ObjectUtils.isEmpty(idFceEligibilityList) && idFceEligibilityList.size() > 0) {
			idFceEligibility = idFceEligibilityList.get(0);
			if (!ObjectUtils.isEmpty(idFceEligibility)) {
				fceEligibilityDto = getFceEligibility(idFceEligibility);
			}
		}
		return fceEligibilityDto;
	}

	@Override
	public FceEligibilityDto copyEligibility(FceEligibilityDto lastfceEligibilityDto, Long idLastUpdatePerson,
			boolean copyReasonsNotEligible) {

		FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
		Long idFceEligibility = null;

		idFceEligibility = createFceEligibility(lastfceEligibilityDto.getIdCase(), idLastUpdatePerson,
				lastfceEligibilityDto.getIdStage(), lastfceEligibilityDto.getIdPerson());

		fceEligibilityDto.setIdFceApplication(lastfceEligibilityDto.getIdFceApplication());
		fceEligibilityDto.setIdFceReview(lastfceEligibilityDto.getIdFceReview());
		fceEligibilityDto.setCdEligibilityActual(lastfceEligibilityDto.getCdEligibilityActual());
		fceEligibilityDto.setNbrCertifiedGroup(lastfceEligibilityDto.getNbrCertifiedGroup());
		fceEligibilityDto.setIndEligible(lastfceEligibilityDto.getIndEligible());
		fceEligibilityDto.setCdPersonCitizenship(lastfceEligibilityDto.getCdPersonCitizenship());
		fceEligibilityDto.setIdFceEligibility(idFceEligibility);
		fceEligibilityDto.setIdStage(lastfceEligibilityDto.getIdStage());
		fceEligibilityDto.setIdCase(lastfceEligibilityDto.getIdCase());
		fceEligibilityDto.setIdLastUpdatePerson(idLastUpdatePerson);
		fceEligibilityDto.setIdPerson(lastfceEligibilityDto.getIdPerson());
		fceEligibilityDto.setDtLastUpdate(new Date());
		if (copyReasonsNotEligible) {
			copyReasonsNotEligible(lastfceEligibilityDto.getIdFceEligibility(), idFceEligibility);
		}

		FcePersonDto lastFcePersonDto = null;
		if (!ObjectUtils.isEmpty(lastfceEligibilityDto.getIdFcePerson())) {
			lastFcePersonDto = findFcePerson(lastfceEligibilityDto.getIdFcePerson());
			if (!ObjectUtils.isEmpty(idFceEligibility) && !ObjectUtils.isEmpty(lastFcePersonDto)) {
				lastFcePersonDto.setIdFceEligibility(idFceEligibility);
			}
			if (!ObjectUtils.isEmpty(lastFcePersonDto.getIdPerson()) && !ObjectUtils.isEmpty(idFceEligibility)) {
				FcePerson fcePerson = fcePersonDao.save(idFceEligibility, lastFcePersonDto.getIdPerson());
				// associate child with eligibility
				fceEligibilityDto.setIdFcePerson(fcePerson.getIdFcePerson());
				fceEligibilityDto.setIdPerson(lastFcePersonDto.getIdPerson());
			}
		}
		return fceEligibilityDto;
	}

	public boolean hasLegacyEvents(Long idStage) {
		boolean hasLegacyEvents = false;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.in("cdTask", Arrays.asList(ServiceConstants.LEGACY_FCE_APPLICATION_TASK_CODE,
				ServiceConstants.LEGACY_FCE_REVIEW_TASK_CODE)));
		Long eventCount = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult());

		if (eventCount > 0) {
			hasLegacyEvents = true;
		}
		return hasLegacyEvents;
	}

	@Override
	public FceEligibilityDto createFceEligibility(Long idCase, Long idEvent, Long idLastUpdatePerson, Long idStage,
			Long idPerson, boolean forFceReview) {
		FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
		FcePersonDto fcePersonDto = new FcePersonDto();
		Long idFceEligibility = null;

		idFceEligibility = createFcEligibility(idPerson, idCase, idLastUpdatePerson, idStage);
		fcePersonDto.setCdRelInt(ServiceConstants.CRELVICT_SL);

		fceEligibilityDto.setIdFceEligibility(idFceEligibility);
		fceEligibilityDto.setIdPerson(idPerson);
		/**
		 * If the latest FceEligibility is not null and is NTSC - New To
		 * SubCare; this eligibility is New To SubCare is Impact Application;
		 * this eligibility is Impact Application is CAPS - this eligibility is
		 * CAPS (FYI. If you get here; latest FceEligibility is null) If there's
		 * an existing Eligibility but no FceEligibility OR there are legacy
		 * applications/reviews Then The child is from CAPs Else The child is
		 * NTSC new to subcare
		 */

		EligibilityDto eligibilityDto = findLatestLegacyEligibility(idCase, idPerson);

		String cdApplication = ServiceConstants.CAPS_FCE;
		if (!hasLegacyEvents(idStage)) {
			boolean childMarkedNewToSubcare = false;

			if (ObjectUtils.isEmpty(eligibilityDto)) {
				childMarkedNewToSubcare = true;
			}
			if (!childMarkedNewToSubcare) {
				/**
				 * while there is no complete application/review, there may
				 * still be an FceApplication object associated with an
				 * FceEligibility object created in Impact
				 */
				childMarkedNewToSubcare = isChildNewToSubcare(idPerson);
			}
			if (childMarkedNewToSubcare) {
				if (forFceReview) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(ServiceConstants.MSG_NO_APPLICATION_REVIEW_NOT_AVAILABLE_CODE);
					fceEligibilityDto.setErrorDto(errorDto);
					return fceEligibilityDto;
				}
			}

			cdApplication = ServiceConstants.NEW_TO_SUBCARE;
		}

		if (!ObjectUtils.isEmpty(eligibilityDto)
				&& ServiceConstants.AUTO_SYS_ID == eligibilityDto.getIdPersonUpdate()) {
			cdApplication = ServiceConstants.AUTO_MAO_ELIG;
		}

		if ((forFceReview) && ServiceConstants.CAPS.equals(cdApplication)) {
			if (eligibilityDto == null) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_NO_SUMMARY_REVIEW_NOT_AVAILABLE_CODE);
				fceEligibilityDto.setErrorDto(errorDto);
				return fceEligibilityDto;
			}
			fceEligibilityDto.setIndEligible(ServiceConstants.STRING_IND_N);
			if (ServiceConstants.CELIGIBI_010.equals(eligibilityDto.getCdEligActual())) {
				fceEligibilityDto.setIndEligible(ServiceConstants.STRING_IND_Y);
			}

			fceEligibilityDto.setIndEligible(fceEligibilityDto.getIndEligible());
		}
		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		fceApplicationDto.setCdApplication(cdApplication);
		fceApplicationDto.setIdCase(idCase);
		fceApplicationDto.setIdPerson(idPerson);
		fceApplicationDto.setIdEvent(idEvent);
		fceApplicationDto.setIdLastUpdatePerson(idLastUpdatePerson);
		fceApplicationDto.setIdStage(idStage);
		fceApplicationDto.setIdFceEligibility(idFceEligibility);

		Long idFceApplication = createFceApplication(fceApplicationDto);
		fceEligibilityDto = getFceEligibility(idFceEligibility);
		fceEligibilityDto.setIdFceApplication(idFceApplication);
		fceEligibilityDto.setIdPerson(idPerson);
		return fceEligibilityDto;
	}

	@SuppressWarnings("unchecked")
	public EligibilityDto findLatestLegacyEligibility(Long idCase, Long idPerson) {

		EligibilityDto eligibilityDto = null;
		List<EligibilityDto> eligibilityList = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Eligibility.class);
		Criterion conjuction = Restrictions.conjunction().add(Restrictions.eq("idCase", idCase))
				.add(Restrictions.eq("personByIdPerson.idPerson", idPerson)).add(Restrictions.isNotNull("cdEligActual"))
				.add(Restrictions.neProperty("dtEligStart", "dtEligEnd"));
		criteria.add(conjuction);
		eligibilityList = (List<EligibilityDto>) criteria.addOrder(Order.desc("dtEligStart"))
				.addOrder(Order.desc("dtEligEnd"))
				.setProjection(Projections.projectionList().add(Projections.property("idEligEvent").as("idEligEvent"))
						.add(Projections.property("personByIdPerson.idPerson").as("idPerson"))
						.add(Projections.property("personByIdPersonUpdate.idPerson").as("idPersonUpdate"))
						.add(Projections.property("cdEligActual").as("cdEligActual"))
						.add(Projections.property("cdEligCsupQuest1").as("cdEligCsupQuest1"))
						.add(Projections.property("cdEligCsupQuest2").as("cdEligCsupQuest2"))
						.add(Projections.property("cdEligCsupQuest3").as("cdEligCsupQuest3"))
						.add(Projections.property("cdEligCsupQuest4").as("cdEligCsupQuest4"))
						.add(Projections.property("cdEligCsupQuest5").as("cdEligCsupQuest5"))
						.add(Projections.property("cdEligCsupQuest6").as("cdEligCsupQuest6"))
						.add(Projections.property("cdEligCsupQuest7").as("cdEligCsupQuest7"))
						.add(Projections.property("cdEligMedEligGroup").as("cdEligMedEligGroup"))
						.add(Projections.property("cdEligSelected").as("cdEligSelected"))
						.add(Projections.property("dtEligCsupReferral").as("dtEligCsupReferral"))
						.add(Projections.property("dtEligEnd").as("dtEligEnd"))
						.add(Projections.property("dtEligReview").as("dtEligReview"))
						.add(Projections.property("dtEligStart").as("dtEligStart"))
						.add(Projections.property("indEligCsupSend").as("indEligCsupSend"))
						.add(Projections.property("indEligWriteHistory").as("indEligWriteHistory"))
						.add(Projections.property("txtEligComment").as("indEligWriteHistory")))
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class)).list();

		if (!ObjectUtils.isEmpty(eligibilityList)) {
			eligibilityDto = eligibilityList.get(0);
		}
		return eligibilityDto;
	}

	@Override
	public FceApplicationDto createFcApplication(String cdApplication, Long idCase, Long idEvent,
			Long idLastUpdatePerson, Long idStage, Long idPerson, String indEligible) {

		if (ObjectUtils.isEmpty(idPerson)) {
			throw new DataLayerException(ServiceConstants.primaryChild + idStage);
		}

		Long idFceEligibility = createFceEligibility(idCase, idLastUpdatePerson, idStage, idPerson);

		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		fceApplicationDto.setCdApplication(cdApplication);
		fceApplicationDto.setIdCase(idCase);
		fceApplicationDto.setIdEvent(idEvent);
		fceApplicationDto.setIdFceEligibility(idFceEligibility);
		fceApplicationDto.setIdLastUpdatePerson(idLastUpdatePerson);
		fceApplicationDto.setIdPerson(idPerson);
		fceApplicationDto.setIdStage(idStage);

		Long idFceApplication = createFceApplication(fceApplicationDto);
		fcePersonDao.save(idFceEligibility, idPerson);
		fceApplicationDto.setIdFceApplication(idFceApplication);
		return fceApplicationDto;
	}

	/**
	 * retrieves the legacy eligibility
	 * 
	 * @param idEligibilityEvent
	 * @return
	 */
	@Override
	public EligibilityDto findLegacyEligibility(Long idEligibilityEvent) {

		Criteria eventCriteria = sessionFactory.getCurrentSession().createCriteria(Eligibility.class);
		eventCriteria.add(Restrictions.eq("idEligEvent", idEligibilityEvent));
		Eligibility eligibility = (Eligibility) eventCriteria.uniqueResult();
		if (ObjectUtils.isEmpty(eligibility)) {
			return null;
		}
		EligibilityDto eligibilityDto = new EligibilityDto();
		BeanUtils.copyProperties(eligibility, eligibilityDto);

		if (!ObjectUtils.isEmpty(eligibility.getPersonByIdPerson())) {
			eligibilityDto.setIdPerson(eligibility.getPersonByIdPerson().getIdPerson());
		}
		if (!ObjectUtils.isEmpty(eligibility.getPersonByIdPersonUpdate())) {
			eligibilityDto.setIdPersonUpdate(eligibility.getPersonByIdPersonUpdate().getIdPerson());
		}
		return eligibilityDto;
	}

	@Override
	public Double findSsi(Long idPerson) {
		Double amtIncRsrc = (Double) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(findSsiSql)
				.setParameter("idPerson", idPerson).setParameter("cdIncRsrcType", CodesConstant.CINC_SSI))
						.addScalar("amt_inc_rsrc", StandardBasicTypes.DOUBLE).uniqueResult();

		return amtIncRsrc;
	}

	@SuppressWarnings("unchecked")
	public void copyReasonsNotEligible(Long oldIdFceEligibility, Long newIdFceEligibility) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceReasonNotEligible.class);
		criteria.add(Restrictions.eq("fceEligibility.idFceEligibility", oldIdFceEligibility));
		List<FceReasonNotEligible> fceReasonNotEligibleList = criteria.list();
		if (!ObjectUtils.isEmpty(fceReasonNotEligibleList) && !fceReasonNotEligibleList.isEmpty()) {
			List<String> reasonsNotEligibleList = new ArrayList<>();
			fceReasonNotEligibleList.stream().forEach(fceReasonNotEligible -> {
				if (!ObjectUtils.isEmpty(fceReasonNotEligible.getCdReasonNotEligible())) {
					reasonsNotEligibleList.add(fceReasonNotEligible.getCdReasonNotEligible());
				}
			});
			if (!ObjectUtils.isEmpty(reasonsNotEligibleList) && reasonsNotEligibleList.size() > 0) {
				createFceReasonsNotEligible(reasonsNotEligibleList, newIdFceEligibility);
			}
		}
	}

	@Override
	public void createFceReasonsNotEligible(List<String> reasonsNotEligibleList, Long idFceEligibility) {

		reasonsNotEligibleList.stream().forEach(cdReasonNotEligible -> {
			FceReasonNotEligible fceReasonNotEligible = new FceReasonNotEligible();
			FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession()
					.get(FceEligibility.class, idFceEligibility);
			if (ObjectUtils.isEmpty(fceEligibility)) {
				throw new DataLayerException(
						messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
			}
			fceEligibility.setIdFceEligibility(idFceEligibility);
			fceReasonNotEligible.setCdReasonNotEligible(cdReasonNotEligible);
			fceReasonNotEligible.setDtLastUpdate(new Date());
			fceReasonNotEligible.setFceEligibility(fceEligibility);
			sessionFactory.getCurrentSession().save(fceReasonNotEligible);
		});
	}

	@Override
	public void updateBirthday(FcePersonDto fcePersonDto, PersonDto personDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FcePerson.class);
		criteria.add(Restrictions.eq("idFcePerson", fcePersonDto.getIdFcePerson()));

		FcePerson fcePerson = (FcePerson) criteria.uniqueResult();
		if (ObjectUtils.isEmpty(fcePerson) || ObjectUtils.isEmpty(personDto)) {
			return;
		}
		fcePerson.setNbrAge(personDto.getNbrPersonAge());
		fcePerson.setDtBirth(personDto.getDtPersonBirth());
		fcePerson.setIndDobApprox(personDto.getIndPersonDobApprox());
		sessionFactory.getCurrentSession().update(fcePerson);
	}

	@SuppressWarnings("unchecked")
	public boolean isChildNewToSubcare(Long idPerson) {
		SQLQuery squery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isChildNewToSubcareSql)
				.setParameter("idPerson", idPerson);

		List<BigDecimal> idFceApplicationList = (List<BigDecimal>) squery.list();

		return (!ObjectUtils.isEmpty(idFceApplicationList));
	}

	/**
	 * Method Name: saveEligibility Method Description: This method saves the
	 * FceEligibility details in database
	 * 
	 * @param fceEligibilityDto
	 * @return long
	 */
	@Override
	public CommonHelperRes saveEligibility(FceEligibilityDto fceEligibilityDto, boolean isDeletefceReasonNotEligibles) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		FceEligibility fceEligibility = findByPrimaryKey(fceEligibilityDto.getIdFceEligibility());
		if (fceEligibility.getDtLastUpdate().compareTo(fceEligibilityDto.getDtLastUpdate()) != 0) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			commonHelperRes.setErrorDto(errorDto);
			return commonHelperRes;
		}
		BeanUtils.copyProperties(fceEligibilityDto, fceEligibility);
		if (isDeletefceReasonNotEligibles)
			fceEligibility.setFceReasonNotEligibles(null);
		Person personByIdLastUpdate = (Person) sessionFactory.getCurrentSession().get(Person.class,
				fceEligibilityDto.getIdLastUpdatePerson());
		fceEligibility.setPerson(personByIdLastUpdate);
		fceEligibility.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(fceEligibility);
		return commonHelperRes;
	}

	public FceEligibility findByPrimaryKey(Long idFceEligibility) {

		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
				idFceEligibility);
		if (ObjectUtils.isEmpty(fceEligibility)) {
			throw new DataLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
		return fceEligibility;
	}

	public FceEligibility findEligiblilityById(Long idFceEligibility) {

		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().load(FceEligibility.class,
				idFceEligibility);
		if (ObjectUtils.isEmpty(fceEligibility)) {
			throw new DataLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
		return fceEligibility;
	}

	public Long createEligibility(FceEligibility fceEligibility) {
		Long idFceEligibility = (Long) sessionFactory.getCurrentSession().save(fceEligibility);
		return idFceEligibility;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteFceReasonsNotEligible(Long idFceEligibility) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceReasonNotEligible.class);
		criteria.add(Restrictions.eq("fceEligibility.idFceEligibility", idFceEligibility));
		List<FceReasonNotEligible> fceReasonNotEligibleList = criteria.list();
		if (!ObjectUtils.isEmpty(fceReasonNotEligibleList) && fceReasonNotEligibleList.size() > 0) {
			for (FceReasonNotEligible fceReasonNotEligible : fceReasonNotEligibleList) {
				sessionFactory.getCurrentSession().delete(fceReasonNotEligible);
			}
		}
	}

	public List<String> getReasonsNotEligible(FceEligibility fceEligibility) {

		List<String> reasonsNotEligibleList = new ArrayList<>();

		if (ServiceConstants.N.equals(fceEligibility.getIndChildUnder18()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A01);

		if (ServiceConstants.N.equals(fceEligibility.getIndChildQualifiedCitizen()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A02);

		if (ServiceConstants.N.equals(fceEligibility.getIndParentalDeprivation()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A03);

		if (ServiceConstants.N.equals(fceEligibility.getIndChildLivingPrnt6Mnths()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A04);

		if (ObjectUtils.isEmpty(fceEligibility.getNbrStepCalc())) {
			if (ServiceConstants.N.equals(fceEligibility.getIndHomeIncomeAfdcElgblty()))
				reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A05);
		} else {
			// SIR 1010396 Added AFDC 185% and 100% reasons not eligible
			if (ServiceConstants.N.equals(fceEligibility.getIndIncome185AfdcElgblty()))
				reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A11);
			else if (ServiceConstants.N.equals(fceEligibility.getIndIncome100AfdcElgblty()))
				reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A12);
		}
		if (ServiceConstants.Y.equals(fceEligibility.getIndEquity()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A06);
		if (ServiceConstants.N.equals(fceEligibility.getIndRemovalChildOrdered()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A08);
		if (ServiceConstants.N.equals(fceEligibility.getIndRsnblEffortPrvtRemoval()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A09);
		if (ServiceConstants.N.equals(fceEligibility.getIndPrsManagingCvs()))
			reasonsNotEligibleList.add(ServiceConstants.CFCERNE_A10);

		return reasonsNotEligibleList;
	}

	@Override
	public void updateFceReasonNotEligibles(Long idFceEligibility) {

		FceEligibility fceEligibility = findEligiblilityById(idFceEligibility);
		List<String> fceReasonsNotEligibleList = getReasonsNotEligible(fceEligibility);
		fceEligibility.setDtLastUpdate(new Date());
		if (!ObjectUtils.isEmpty(fceReasonsNotEligibleList) && fceReasonsNotEligibleList.size() > 0) {

			Set<FceReasonNotEligible> fceReasonNotEligibleSet = new HashSet<>();
			fceReasonsNotEligibleList.stream().forEach(cdReasonNotEligible -> {
				if (!ObjectUtils.isEmpty(cdReasonNotEligible)) {
					FceReasonNotEligible fceReasonNotEligible = new FceReasonNotEligible();
					fceReasonNotEligible.setCdReasonNotEligible(cdReasonNotEligible);
					fceReasonNotEligible.setDtLastUpdate(new Date());
					fceReasonNotEligible.setFceEligibility(fceEligibility);
					fceReasonNotEligibleSet.add(fceReasonNotEligible);
				}
			});

			fceEligibility.setIndEligible(ServiceConstants.N);
			fceEligibility.setFceReasonNotEligibles(fceReasonNotEligibleSet);
			sessionFactory.getCurrentSession().update(fceEligibility);
		} else {
			fceEligibility.setIndEligible(ServiceConstants.Y);
			sessionFactory.getCurrentSession().update(fceEligibility);
		}
	}

	@Override
	public void updateFceApplication(FceApplicationDto fceApplicationDto) {
		FceApplication fceApplication = (FceApplication) sessionFactory.getCurrentSession().get(FceApplication.class,
				fceApplicationDto.getIdFceApplication());
		if (ObjectUtils.isEmpty(fceApplication)) {
			throw new DataLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
		fceApplication.setDtLastUpdate(new Date());
		fceApplication.setDtApplicationComplete(new Date());
		sessionFactory.getCurrentSession().update(fceApplication);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.fce.dao.FceDao#updateFceEligiblility(us.tx.state
	 * .dfps.service.fce.dto.FceEligibilityDto)
	 */
	@Override
	public void updateFceEligiblility(FceEligibilityDto fceEligibilityDto) {
		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().load(FceEligibility.class,
				fceEligibilityDto.getIdFceEligibility());
		if (ObjectUtils.isEmpty(fceEligibility)) {
			throw new DataLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
		BeanUtils.copyProperties(fceEligibilityDto, fceEligibility);
		fceEligibility.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(fceEligibility);
	}

	/**
	 * Method Name: findReasonsNotEligible Method Description:
	 * findReasonsNotEligible
	 * 
	 * @param idFceEligibility
	 * @return List<FceReasonNotEligibleDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FceReasonNotEligibleDto> findReasonsNotEligible(Long idFceEligibility) {
		List<FceReasonNotEligibleDto> fceReasonNotEligibleDtoList = new ArrayList<>();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceReasonNotEligible.class);

		criteria.add(Restrictions.eq("fceEligibility.idFceEligibility", idFceEligibility));
		criteria.addOrder(Order.asc("cdReasonNotEligible"));
		List<FceReasonNotEligible> fceReasonNotEligibleList = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(fceReasonNotEligibleList)) {
			for (FceReasonNotEligible fceReasonNotEligible : fceReasonNotEligibleList) {

				FceReasonNotEligibleDto fceReasonNotEligibleDto = new FceReasonNotEligibleDto();
				fceReasonNotEligibleDto.setCdReasonNotEligible(fceReasonNotEligible.getCdReasonNotEligible());
				fceReasonNotEligibleDto.setDtLastUpdate(fceReasonNotEligible.getDtLastUpdate());
				if (!TypeConvUtil.isNullOrEmpty(fceReasonNotEligible.getFceEligibility())) {
					fceReasonNotEligibleDto
							.setIdFceEligibility(fceReasonNotEligible.getFceEligibility().getIdFceEligibility());
				}
				fceReasonNotEligibleDto.setIdFceReasonNotEligible(fceReasonNotEligible.getIdFceReasonNotEligible());
				fceReasonNotEligibleDtoList.add(fceReasonNotEligibleDto);
			}
		}
		return fceReasonNotEligibleDtoList;
	}

	@Override
	public CommonHelperRes updateFcEligibilityAndApp(FceApplicationDto fceApplicationDto,
			FceEligibilityDto fceEligibilityDto) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
				fceEligibilityDto.getIdFceEligibility());
		if (ObjectUtils.isEmpty(fceEligibility)) {
			throw new DataNotFoundException(
					messageSource.getMessage("fceEligibility.not.found.fceEligibilityId", null, Locale.US));
		}
		FceApplication fceApplication = null;
		Optional<FceApplication> OpFceApplication = fceEligibility.getFceApplications().stream()
				.filter(dto -> dto.getIdFceApplication() == fceEligibilityDto.getIdFceApplication()).findAny();
		if (OpFceApplication.isPresent()) {
			fceApplication = OpFceApplication.get();
			if (fceApplication.getDtLastUpdate().compareTo(fceApplicationDto.getDtLastUpdate()) == 0
					&& fceEligibility.getDtLastUpdate().compareTo(fceEligibilityDto.getDtLastUpdate()) == 0) {
				BeanUtils.copyProperties(fceApplicationDto, fceApplication);
				BeanUtils.copyProperties(fceEligibilityDto, fceEligibility);
				
				/*
				 * if
				 * (!ObjectUtils.isEmpty(fceApplicationDto.getNbrCourtYear()))
				 * fceApplication.setNbrCourtYear(fceApplicationDto.
				 * getNbrCourtYear().intValue());
				 */
				fceApplication.setDtCourtPetFiled(fceApplicationDto.getDtCourtPetFiled());
				if (!ObjectUtils.isEmpty(fceApplicationDto.getNbrAfdcEligMonth())){
					fceApplication.setNbrAfdcEligMonth(fceApplicationDto.getNbrAfdcEligMonth().intValue());
					fceApplication.setNbrCourtMonth(fceApplicationDto.getNbrAfdcEligMonth().intValue());
				}
				if (!ObjectUtils.isEmpty(fceApplicationDto.getNbrCourtMonth())) {
					fceApplication.setNbrCourtMonth(fceApplicationDto.getNbrCourtMonth().intValue());
				}
				if (!ObjectUtils.isEmpty(fceApplicationDto.getNbrAfdcEligYear())) {
					fceApplication.setNbrAfdcEligYear(fceApplicationDto.getNbrAfdcEligYear().intValue());
					fceApplication.setNbrCourtYear(fceApplicationDto.getNbrAfdcEligYear().intValue());
				}
				if (!fceApplicationDto.getNbrCourtYear().equals(ServiceConstants.ZERO)) {
					fceApplication.setNbrCourtYear(fceApplicationDto.getNbrCourtYear().intValue());
				}
				fceEligibility.getFceApplications().add(fceApplication);
				fceEligibility.setDtLastUpdate(new Date());
				fceApplication.setDtLastUpdate(new Date());

				sessionFactory.getCurrentSession().update(fceEligibility);
			} else {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				commonHelperRes.setErrorDto(errorDto);
				return commonHelperRes;
			}
		}

		return commonHelperRes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FceDomicilePersonWelfDto> getFceDomicilePersonWelf(FceApplicationDto fceApplicationDto) {
		List<FceDomicilePersonWelfDto> fceDomicilePersonWelfDtoList = new ArrayList<>();

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceDomclPrsnWelf.class);

		criteria.add(Restrictions.eq("idFceApplication", fceApplicationDto.getIdFceApplication()));

		List<FceDomclPrsnWelf> fceDomclPrsnWelfList = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(fceDomclPrsnWelfList)) {
			for (FceDomclPrsnWelf fceDomclPrsnWelf : fceDomclPrsnWelfList) {
				FceDomicilePersonWelfDto fceDomicilePersonWelfDto = new FceDomicilePersonWelfDto();
				fceDomicilePersonWelfDto.setIdFceDomclPrsnWelf(fceDomclPrsnWelf.getIdFceDomclPrsnWelf());
				fceDomicilePersonWelfDto.setIdPerson(fceDomclPrsnWelf.getIdPerson());
				fceDomicilePersonWelfDto.setIdFceApplication(fceDomclPrsnWelf.getIdFceApplication());
				fceDomicilePersonWelfDto.setIdCreatedPerson(fceDomclPrsnWelf.getIdCreatedPerson());
				fceDomicilePersonWelfDto.setIdLastUpdatePerson(fceDomclPrsnWelf.getIdLastUpdatePerson());
				fceDomicilePersonWelfDto.setDtCreated(fceDomclPrsnWelf.getDtCreated());
				fceDomicilePersonWelfDto.setDtLastUpdate(fceDomclPrsnWelf.getDtLastUpdate());
				fceDomicilePersonWelfDtoList.add(fceDomicilePersonWelfDto);
			}
		}
		return fceDomicilePersonWelfDtoList;

	}

	/**
	 * 
	 * Method Name: updateFceDomicilePersonWelf Method Description:
	 * 
	 * @param fceDomicilePersonWelfDto
	 * @return
	 */
	@Override
	public void updateFceDomicilePersonWelf(FceDomicilePersonWelfDto fceDomicilePersonWelfDto) {

		FceDomclPrsnWelf fceDomclPrsnWelf = (FceDomclPrsnWelf) sessionFactory.getCurrentSession()
				.get(FceDomclPrsnWelf.class, fceDomicilePersonWelfDto.getIdPerson());
		if (ObjectUtils.isEmpty(fceDomclPrsnWelf)) {
			fceDomclPrsnWelf = new FceDomclPrsnWelf();
			fceDomclPrsnWelf.setIdPerson(fceDomicilePersonWelfDto.getIdPerson());
			fceDomclPrsnWelf.setIdFceApplication(fceDomicilePersonWelfDto.getIdFceApplication());
			fceDomclPrsnWelf.setIdCreatedPerson(fceDomicilePersonWelfDto.getIdCreatedPerson());
			fceDomclPrsnWelf.setIdLastUpdatePerson(fceDomicilePersonWelfDto.getIdLastUpdatePerson());
			fceDomclPrsnWelf.setDtCreated(new Date());
			fceDomclPrsnWelf.setDtLastUpdate(new Date());
		} else {

			BeanUtils.copyProperties(fceDomicilePersonWelfDto, fceDomclPrsnWelf);
			fceDomclPrsnWelf.setDtLastUpdate(new Date());
		}

		sessionFactory.getCurrentSession().saveOrUpdate(fceDomclPrsnWelf);

	}

	/**
	 * 
	 * Method Name: verifyOpenStage Method Description:
	 * 
	 * @param idStage
	 * @return IdStage
	 */
	@Override
	public Long verifyOpenStage(Long idStage) {
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (ObjectUtils.isEmpty(stage))
			throw new DataLayerException(messageSource.getMessage("Stage.notFound", null, Locale.US));
		if (ServiceConstants.Y.equals(stage.getIndStageClose())
				|| ServiceConstants.ONE.equals(stage.getIndStageClose())) {
			throw new DataLayerException(messageSource
					.getMessage(lookupdao.getMessageByNumber(ServiceConstants.MSG_SYS_STAGE_CLOSED), null, Locale.US));
		}
		return stage.getIdStage();
	}

	public void deleteFceDepCareDeductAdultPerson(Long idFceEligibility) {
		excuteDeleteQuery(deleteFceDepCareDeductAdultPersonSql, idFceEligibility);
	}

	public void deleteFceDepCareDeductDependentPerson(Long idFceEligibility) {
		excuteDeleteQuery(deleteFceDepCareDeductDependentPersonSql, idFceEligibility);
	}

	public void deleteFcePerson(Long idFceEligibility) {
		FcePerson fcePerson = (FcePerson) sessionFactory.getCurrentSession().get(FcePerson.class, idFceEligibility);
		if (!ObjectUtils.isEmpty(fcePerson))
			sessionFactory.getCurrentSession().delete(fcePerson);
		sessionFactory.getCurrentSession().flush();
	}

	public void deleteFceReasonNotEligible(Long idFceEligibility) {
		FceReasonNotEligible fceReasonNotEligible = (FceReasonNotEligible) sessionFactory.getCurrentSession()
				.get(FceReasonNotEligible.class, idFceEligibility);
		if (!ObjectUtils.isEmpty(fceReasonNotEligible))
			sessionFactory.getCurrentSession().delete(fceReasonNotEligible);
		sessionFactory.getCurrentSession().flush();
	}

	public void deleteFceApplicationForEligibility(Long idEligibilityEvent) {

		Query queryExecution = sessionFactory.getCurrentSession().createSQLQuery(deleteFceApplicationSql);
		queryExecution.setParameter("idEligibilityEvent", idEligibilityEvent);
		queryExecution.executeUpdate();

	}

	public void deleteFceEligibility(Long idFceEligibility) {

		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
				idFceEligibility);
		if (!ObjectUtils.isEmpty(fceEligibility))
			sessionFactory.getCurrentSession().delete(fceEligibility);
		sessionFactory.getCurrentSession().flush();
	}

	public void excuteDeleteQuery(String query, Long idFceEligibility) {

		Query queryExecution = sessionFactory.getCurrentSession().createSQLQuery(query);
		queryExecution.setParameter("idFceEligibility", idFceEligibility);
		queryExecution.executeUpdate();
	}

	@Override
	public FcePersonDto findFcePersonByPrimaryKey(long idFcePerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FcePerson.class);
		criteria.add(Restrictions.eq("idFcePerson", idFcePerson));
		FcePerson fcePerson = (FcePerson) criteria.uniqueResult();
		FcePersonDto fcePersonDto = new FcePersonDto();
		BeanUtils.copyProperties(fcePerson, fcePersonDto);
		BeanUtils.copyProperties(fcePerson.getPerson(), fcePersonDto);
		return fcePersonDto;
	}

	public Boolean isAutoEligibility(Long idEvent) {
		EventPersonLink eventPersonLink = (EventPersonLink) sessionFactory.getCurrentSession()
				.get(EventPersonLink.class, idEvent);
		if (!ObjectUtils.isEmpty(eventPersonLink) && !ServiceConstants.Y.equals(eventPersonLink.getIndAutoElig())) {
			return Boolean.FALSE;
		} else
			return Boolean.TRUE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> findEligEventIdByPersonId(Long idPerson) {
		List<Long> idEligEventList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(findEligByPersonIdSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).setLong("idPerson", idPerson);
		try {
			idEligEventList = (List<Long>) sqlQuery.list();
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return idEligEventList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> saveEligFirstValidation(Long idPerson, Date startDate, Date endDate) {
		List<Long> idEligEventList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(saveEligFirstValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).setLong("idPerson", idPerson)
				.setParameter("dtEligStart", startDate).setParameter("dtEligEnd", endDate);
		try {
			idEligEventList = (List<Long>) sqlQuery.list();
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return idEligEventList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> saveEligSecondValidation(Long idPerson, Date startDate, Date endDate) {
		List<Long> idEligEventList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(saveEligSecondValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).setLong("idPerson", idPerson)
				.setParameter("dtEligStart", startDate).setParameter("dtEligEnd", endDate);
		try {
			idEligEventList = (List<Long>) sqlQuery.list();
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return idEligEventList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> saveEligThirdValidation(Long idPerson, Date startDate, Date endDate) {
		List<Long> idEligEventList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(saveEligThirdValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).setLong("idPerson", idPerson)
				.setParameter("dtEligStart", startDate).setParameter("dtEligEnd", endDate);
		try {
			idEligEventList = (List<Long>) sqlQuery.list();
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return idEligEventList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EligibilityDto saveEligFourthValidation(Long idPerson, Date startDate, Date endDate) {
		EligibilityDto eligibilityDto = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(saveEligFourthValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).addScalar("dtEligEnd", StandardBasicTypes.DATE)
				.setLong("idPerson", idPerson).setParameter("dtEligStart", startDate)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class));
		try {
			List<EligibilityDto> eligibilityList = sqlQuery.list();
			if (!ObjectUtils.isEmpty(eligibilityList))
				eligibilityDto = eligibilityList.get(0);

		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return eligibilityDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EligibilityDto saveEligFifthValidation(Long idPerson, Date startDate, Date endDate) {
		EligibilityDto eligibilityDto = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.openSession().createSQLQuery(saveEligFifthValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).addScalar("dtEligStart", StandardBasicTypes.DATE)
				.setLong("idPerson", idPerson).setParameter("dtEligEnd", endDate)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class));
		try {
			List<EligibilityDto> eligibilityList = sqlQuery.list();
			if (!ObjectUtils.isEmpty(eligibilityList))
				eligibilityDto = eligibilityList.get(0);
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return eligibilityDto;
	}

	@Override
	public EligibilityDto determineEligWasCourtOrd(Long idPerson, Long idEligibilityEvent) {
		EligibilityDto eligibilityDto = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.openSession().createSQLQuery(determineEligWasCourtOrdSql)
				.addScalar("cdEligCsupQuest1", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest2", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest3", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest4", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest5", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest6", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest7", StandardBasicTypes.STRING).setLong("idPerson", idPerson)
				.setLong("idEligibilityEvent", idEligibilityEvent)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class));
		eligibilityDto = (EligibilityDto) sqlQuery.uniqueResult();
		return eligibilityDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> saveEligSeventhValidation(Long idPerson, Date startDate, Date endDate) {
		List<Long> idEligEventList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(saveEligSeventhValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).setLong("idPerson", idPerson)
				.setParameter("dtEligStart", startDate).setParameter("dtEligEnd", endDate);
		try {
			idEligEventList = (List<Long>) sqlQuery.list();
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return idEligEventList;
	}

	@Override
	public Long saveElig(EligibilityDto eligibilityDto) {
		Long idEligEvent = null;
		Eligibility eligibility = new Eligibility();
		BeanUtils.copyProperties(eligibilityDto, eligibility);
		eligibility.setDtLastUpdate(new Date());

		Person personByIdLastUpdate = (Person) sessionFactory.getCurrentSession().get(Person.class,
				eligibilityDto.getIdPersonUpdate());
		if (ObjectUtils.isEmpty(personByIdLastUpdate)) {
			throw new DataNotFoundException(
					messageSource.getMessage("person.not.found.personByIdLastUpdate", null, Locale.US));
		}

		Person personByIdPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
				eligibilityDto.getIdPerson());
		if (ObjectUtils.isEmpty(personByIdPerson)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.personId", null, Locale.US));
		}

		eligibility.setPersonByIdPerson(personByIdPerson);
		eligibility.setIdCreatedPerson(personByIdLastUpdate.getIdPerson());
		eligibility.setPersonByIdPersonUpdate(personByIdLastUpdate);
		if(ObjectUtils.isEmpty(eligibilityDto.getDtEligEnd()))
			eligibility.setDtEligEnd(ServiceConstants.MAX_DATE);
		else eligibility.setDtEligEnd(eligibilityDto.getDtEligEnd());
		sessionFactory.getCurrentSession().save(eligibility);
		return idEligEvent;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateElig(EligibilityDto eligibilityDto) {

		Eligibility eligibility = (Eligibility) sessionFactory.getCurrentSession().get(Eligibility.class,
				eligibilityDto.getIdEligEvent());
		if (ObjectUtils.isEmpty(eligibility)) {
			throw new DataNotFoundException(
					messageSource.getMessage("eligibility.not.found.idEligEvent", null, Locale.US));
		}
		BeanUtils.copyProperties(eligibilityDto, eligibility);
		Person personByIdLastUpdate = (Person) sessionFactory.getCurrentSession().get(Person.class,
				eligibilityDto.getIdPersonUpdate());
		if (ObjectUtils.isEmpty(personByIdLastUpdate)) {
			throw new DataNotFoundException(
					messageSource.getMessage("person.not.found.personByIdLastUpdate", null, Locale.US));
		}

		Person personByIdPerson = (Person) sessionFactory.getCurrentSession().get(Person.class,
				eligibilityDto.getIdPerson());
		if (ObjectUtils.isEmpty(personByIdPerson)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.personId", null, Locale.US));
		}
		
		if(ServiceConstants.NULL_VALUE.equals(eligibility.getIndEligCsupSend())) {
			eligibility.setIndEligCsupSend(null);
		}
		eligibility.setPersonByIdPerson(personByIdPerson);
		eligibility.setPersonByIdPersonUpdate(personByIdLastUpdate);
		eligibility.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().update(eligibility);
		
		Date dtEligEnd = eligibilityDto.getDtEligEnd();
		
		if (!ObjectUtils.isEmpty(dtEligEnd) && !(ServiceConstants.MAX_DATE.compareTo(dtEligEnd) == 0
				|| ServiceConstants.NULL_JAVA_DATE_DATE.compareTo(dtEligEnd) == 0)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class)
					.add(Restrictions.eq("capsCase.idCase", eligibilityDto.getIdCase()))
					.add(Restrictions.eq("stage.idStage", eligibilityDto.getIdStage()))
					.add(Restrictions.eq("cdTodoTask", ServiceConstants.FCE_ELIGIBILITY_TASK_CODE))
					.add(Restrictions.isNull("dtTodoCompleted"));
			List<Todo> todoList =  criteria.list();
			
			if (!CollectionUtils.isEmpty(todoList)) {
				todoList.stream().forEach(todo->{
					todo.setDtTodoCompleted(new Date());
					sessionFactory.getCurrentSession().update(todo);
				});
			}
		}
	}

	@Override
	public EligibilityDto findEligEventIdByIdEligEvent(Long idEligibilityEvent, Long idPerson, Date startDate,
			Date endDate, Date dtLastUpdate) {
		EligibilityDto eligibilityDto = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(findEligByidEligibilityEventSql).addScalar("idEligEvent", StandardBasicTypes.LONG)
				.addScalar("dtCurrentPlocStart", StandardBasicTypes.DATE)
				.addScalar("dtCurrentPlocEnd", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).setLong("idPerson", idPerson)
				.setLong("idEligibilityEvent", idEligibilityEvent)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class));
		eligibilityDto = (EligibilityDto) sqlQuery.uniqueResult();
		if (ObjectUtils.isEmpty(eligibilityDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("eligibility.not.found.idEligibilityEvent", null, Locale.US));
		}
		return eligibilityDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> updateEligFirstValidation(Long idEligibilityEvent, Long idPerson, Date startDate,
			Date dtCurrentPlocStart) {
		List<Long> idEligEventList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateEligFirstValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).setLong("idPerson", idPerson)
				.setLong("idEligibilityEvent", idEligibilityEvent).setParameter("dtEligStart", startDate)
				.setParameter("dtCurrentPlocStart", dtCurrentPlocStart);
		try {
			idEligEventList = (List<Long>) sqlQuery.list();
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idEligibilityEvent: " + idPerson);
		}
		return idEligEventList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> updateEligSecondValidation(Long idEligibilityEvent, Long idPerson, Date endDate,
			Date dtCurrentPlocEnd) {
		List<Long> idEligEventList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateEligSecondValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).setLong("idPerson", idPerson)
				.setLong("idEligibilityEvent", idEligibilityEvent).setParameter("dtEligEnd", endDate)
				.setParameter("dtCurrentPlocEnd", dtCurrentPlocEnd);
		try {
			idEligEventList = (List<Long>) sqlQuery.list();
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idEligibilityEvent: " + idPerson);
		}
		return idEligEventList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EligibilityDto updateEligThirdValidation(Long idPerson, Date startDate, Date dtCurrentPlocStart) {
		EligibilityDto eligibilityDto = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateEligThirdValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).addScalar("dtEligEnd", StandardBasicTypes.DATE)
				.setLong("idPerson", idPerson).setParameter("dtCurrentPlocStart", dtCurrentPlocStart)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class));
		try {
			List<EligibilityDto> eligibilityList = sqlQuery.list();
			if (!ObjectUtils.isEmpty(eligibilityList))
				eligibilityDto = eligibilityList.get(0);
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return eligibilityDto;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EligibilityDto updateEligFourthValidation(Long idPerson, Date endDate, Date dtCurrentPlocEnd) {
		EligibilityDto eligibilityDto = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateEligFourthValidationSql)
				.addScalar("idEligEvent", StandardBasicTypes.LONG).addScalar("dtEligStart", StandardBasicTypes.DATE)
				.setLong("idPerson", idPerson).setParameter("dtCurrentPlocEnd", dtCurrentPlocEnd)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class));
		try {
			List<EligibilityDto> eligibilityList = sqlQuery.list();
			if (!ObjectUtils.isEmpty(eligibilityList))
				eligibilityDto = eligibilityList.get(0);
		} catch (DataNotFoundException | DataLayerException e) {
			LOG.debug("Eligibility data not found for idPerson: " + idPerson);
		}
		return eligibilityDto;
	}

	@Override
	public FceEligibilityDto copyFceReviewEligibility(FceEligibilityDto lastfceEligibilityDto, Long idLastUpdatePerson,
			boolean copyReasonsNotEligible) {

		FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
		Long idFceEligibility = null;
		Session session = sessionFactory.getCurrentSession();
		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdCase(lastfceEligibilityDto.getIdCase());

		Stage stage = (Stage) session.get(Stage.class, lastfceEligibilityDto.getIdStage());
		if (ObjectUtils.isEmpty(stage)) {
			throw new DataNotFoundException(messageSource.getMessage("stage.not.found.stageId", null, Locale.US));
		}

		fceEligibility.setStage(stage);

		fceEligibility.setDtLastUpdate(new Date());

		Person personByIdLastUpdatePerson = (Person) session.get(Person.class, idLastUpdatePerson);
		if (ObjectUtils.isEmpty(personByIdLastUpdatePerson)) {
			throw new DataNotFoundException(messageSource.getMessage("person.not.found.personId", null, Locale.US));
		}
		fceEligibility.setPerson(personByIdLastUpdatePerson);
		fceEligibility.setIdFceApplication(lastfceEligibilityDto.getIdFceApplication());
		fceEligibility.setIdFceReview(lastfceEligibilityDto.getIdFceReview());
		fceEligibility.setCdEligibilityActual(lastfceEligibilityDto.getCdEligibilityActual());
		fceEligibility.setNbrCertifiedGroup(lastfceEligibilityDto.getNbrCertifiedGroup());
		fceEligibility.setIndEligible(lastfceEligibilityDto.getIndEligible());
		fceEligibility.setCdPersonCitizenship(lastfceEligibilityDto.getCdPersonCitizenship());
		fceEligibility.setIdCase(lastfceEligibilityDto.getIdCase());
		fceEligibility.setIdPerson(lastfceEligibilityDto.getIdPerson());
		fceEligibility.setDtLastUpdate(new Date());

		idFceEligibility = (Long) session.save(fceEligibility);
		session.flush();
		// after save need to call
		if (copyReasonsNotEligible) {
			copyReasonsNotEligible(lastfceEligibilityDto.getIdFceEligibility(), idFceEligibility);
		}
		FcePerson lastFcePerson = (FcePerson) session.get(FcePerson.class, lastfceEligibilityDto.getIdFcePerson());
		FcePerson fcePerson = new FcePerson();
		BeanUtils.copyProperties(lastFcePerson, fcePerson, "fceDepCareDeductsForIdFceAdultPerson",
				"fceDepCareDeductsForIdFceDependentPerson", "fceIncomes");
		FceEligibility FceEligibilityLoad = (FceEligibility) session.get(FceEligibility.class, idFceEligibility);
		fcePerson.setFceEligibility(FceEligibilityLoad);
		Long latestIdFcePerson = (Long) session.save(fcePerson);
		session.flush();
		FcePerson latestFcePerson = (FcePerson) sessionFactory.getCurrentSession().get(FcePerson.class,
				latestIdFcePerson);
		FceEligibilityLoad.setIdFcePerson(latestFcePerson.getIdFcePerson());
		FceEligibilityLoad.setIdPerson(latestFcePerson.getPerson().getIdPerson());
		// final save
		sessionFactory.getCurrentSession().saveOrUpdate(FceEligibilityLoad);
		session.flush();
		FceEligibility FceEligibilityLoadFinal = (FceEligibility) session.get(FceEligibility.class, idFceEligibility);
		BeanUtils.copyProperties(FceEligibilityLoadFinal, fceEligibilityDto);
		return fceEligibilityDto;
	}

	/**
	 * 
	 * Method Name: deleteFceDomicilePersonWelf Method Description:Delete the
	 * records from FCE_DOMCL_PRSN_WELF
	 * 
	 * @param FceDomicilePersonWelfDto
	 * @return fceDomclPrsnWelf
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteFceDomicilePersonWelf(FceDomicilePersonWelfDto fceDomicilePersonWelfDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FceDomclPrsnWelf.class);
		criteria.add(Restrictions.eq("idFceApplication", fceDomicilePersonWelfDto.getIdFceApplication()));
		criteria.add(Restrictions.eq("idPerson", fceDomicilePersonWelfDto.getIdPerson()));
		List<FceDomclPrsnWelf> FceDomclPrsnWelfList = criteria.list();
		if (!ObjectUtils.isEmpty(FceDomclPrsnWelfList) && FceDomclPrsnWelfList.size() > 0) {
			for (FceDomclPrsnWelf fceDomclPrsnWelf : FceDomclPrsnWelfList) {
				sessionFactory.getCurrentSession().delete(fceDomclPrsnWelf);
			}
		}

	}

	@Override
	public List<EligibilityDto> findEligByidStage(Long idStage) {
		List<EligibilityDto> eligibilityDtoList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(findEligByidStage).addScalar("idEligEvent", StandardBasicTypes.LONG)
				.addScalar("dtCurrentPlocStart", StandardBasicTypes.DATE)
				.addScalar("dtCurrentPlocEnd", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdEligSelected", StandardBasicTypes.STRING)
				.setLong("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(EligibilityDto.class));
		Object obj =  sqlQuery.list();
		if(ObjectUtils.isEmpty(obj)) {
			return Collections.emptyList();
		}
		eligibilityDtoList = (List<EligibilityDto>) obj;
		return eligibilityDtoList;
	}
}