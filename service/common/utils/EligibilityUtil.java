package us.tx.state.dfps.service.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Eligibility;
import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.facility.dto.EligibilityDBDto;
import us.tx.state.dfps.service.fce.EligibilityDto;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fostercarereview.dao.FcePersonDao;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;

@Repository
public class EligibilityUtil {

	private static final Logger log = Logger.getLogger(EligibilityUtil.class);

	private static final String METHOD_LOC = "Catch for findEligibilityByIdFceApplication method: ";

	@Autowired
	static MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private FceDao fceApplicationDao;

	@Autowired
	private FcePersonDao fcePersonDao;

	@Autowired
	private FceDao fceDao;

	@Value("${EligibilityUtil.findEligibilityByIdFceApplication}")
	private static String findEligibilityByIdFceApplicationSql;

	@Value("{FceEligibilityUtils.findLatestEligForStage}")
	private String findLatestEligForStage;

	@Value("EligibilityUtil.findLatestLegacyEligibility")
	private String findLatestLegacyEligibility;

	@Value("EligibilityUtil.isChildNewToSubcare")
	private String isChildNewToSubcare;

	/**
	 * Description: This method is used to fetch the Eligibility of an
	 * Application using the IdFceApplication.
	 * 
	 * @param idFceApplication
	 * @return FceEligibilityDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public FceEligibilityDto findEligibilityByIdFceApplication(long idFceApplication) {

		log.info("executing findEligibilityByIdFceApplication");
		FceEligibilityDto fceEligibilityDto = null;
		FceApplication fceAppData = (FceApplication) sessionFactory.getCurrentSession()
				.createCriteria(FceApplication.class).add(Restrictions.eq("idFceApplication", idFceApplication))
				.uniqueResult();
		if (fceAppData != null) {
			try {
				log.info("executing try clause");
				long idFceEligibility = fceAppData.getFceEligibility().getIdFceEligibility();
				fceEligibilityDto = fceApplicationDao.getFceEligibility(idFceEligibility);
			} catch (ServiceLayerException e) {
				log.error(METHOD_LOC + e.getMessage());
			}
		}
		return fceEligibilityDto;
	}

	public FceEligibility createEligibility(Long idCase, Long idLastUpdatePerson, Long idStage) {
		FceEligibility fceEligibility = new FceEligibility();
		fceEligibility.setIdCase(idCase);
		Stage stage = new Stage();
		stage.setIdStage(idStage);
		fceEligibility.setStage(stage);

		fceEligibility.setDtLastUpdate(new Date());
		Person person = new Person();
		person.setIdPerson(idLastUpdatePerson);
		fceEligibility.setPerson(person);
		return null;// ageCitizenshipDao.createEligibility(fceEligibility);
	}

	@SuppressWarnings("unchecked")
	public EligibilityDBDto findLatestLegacyEligibility(Long idCase, Long idPerson) {

		List<EligibilityDBDto> eligList = null;

		/*
		 * eligList = (List<EligibilityDBDto>) ((SQLQuery)
		 * sessionFactory.getCurrentSession()
		 * .createSQLQuery(findLatestLegacyEligibility).addScalar(
		 * "cdEligActual", StandardBasicTypes.STRING)
		 * .addScalar("cdEligMedEligGroup", StandardBasicTypes.STRING)
		 * .addScalar("cdEligSelected", StandardBasicTypes.STRING)
		 * .addScalar("dtEligCsupReferral", StandardBasicTypes.DATE)
		 * .addScalar("dtEligEnd", StandardBasicTypes.DATE)
		 * .addScalar("dtEligReview", StandardBasicTypes.DATE)
		 * .addScalar("dtEligStart", StandardBasicTypes.DATE)
		 * .addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
		 * .addScalar("idCase",
		 * StandardBasicTypes.LONG).addScalar("idEligEvent",
		 * StandardBasicTypes.LONG) .addScalar("idPerson",
		 * StandardBasicTypes.LONG) .addScalar("idPersonUpdate",
		 * StandardBasicTypes.LONG) .addScalar("indEligCsupSend",
		 * StandardBasicTypes.BOOLEAN) .addScalar("indEligWriteHistory",
		 * StandardBasicTypes.BOOLEAN) .addScalar("txtEligComment",
		 * StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
		 * .setParameter("idCase", idCase))
		 * .setResultTransformer(Transformers.aliasToBean(EligibilityDBDto.class
		 * )).list();
		 */

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Eligibility.class)
				.setProjection(Projections.projectionList().add(Projections.property("cdEligActual"))
						.add(Projections.property("cdEligMedEligGroup")).add(Projections.property("dtEligCsupReferral"))
						.add(Projections.property("dtEligEnd")).add(Projections.property("dtEligReview"))
						.add(Projections.property("dtEligStart")).add(Projections.property("dtLastUpdate"))
						.add(Projections.property("idCase"))
						.add(Projections.property("personByIdPerson.idPerson"), "idPerson")
						.add(Projections.property("idEligEvent"))
						.add(Projections.property("personByIdPersonUpdate.idPerson"), "idPersonUpdate")
						.add(Projections.property("indEligCsupSend")).add(Projections.property("indEligWriteHistory"))
						.add(Projections.property("txtEligComment")));

		Criterion conjuction = Restrictions.conjunction().add(Restrictions.eq("idCase", idCase))
				.add(Restrictions.eq("personByIdPerson.idPerson", idPerson)).add(Restrictions.isNotNull("cdEligActual"))
				.add(Restrictions.neProperty("dtEligStart", "dtEligEnd"));
		criteria.add(conjuction);
		eligList = (List<EligibilityDBDto>) criteria.addOrder(Order.desc("dtEligStart"))
				.addOrder(Order.desc("dtEligEnd"))
				.setResultTransformer(Transformers.aliasToBean(EligibilityDBDto.class)).list();

		if (eligList.isEmpty()) {
			return null;
		}

		if (eligList.size() > 1) {
			// throw new IllegalStateException("expected only 1 result (got " +
			// eligList.size() + ")");
		}

		return eligList.get(0);
	}

	public boolean ineligibleDueToAnyReasonOtherThanCitizenshipRequirement(FceEligibility fceEligForDB) {

		List<String> reasonsNotEligible = getReasonsNotEligible(fceEligForDB);

		reasonsNotEligible.remove(ServiceConstants.CFCERNE_A02);

		if (reasonsNotEligible.isEmpty()) {
			// thanks you return true/ reasonsNotEligible.isEmpty() == false
			return true;
		}

		return false;
	}

	private List<String> getReasonsNotEligible(FceEligibility fceEligForDB) {

		List<String> resultList = new ArrayList<>();

		if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndChildUnder18())) {
			resultList.add(ServiceConstants.CFCERNE_A01);
		}

		if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndChildQualifiedCitizen())) {
			resultList.add(ServiceConstants.CFCERNE_A02);
		}
		if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndParentalDeprivation())) {
			resultList.add(ServiceConstants.CFCERNE_A03);
		}

		if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndChildLivingPrnt6Mnths())) {
			resultList.add(ServiceConstants.CFCERNE_A04);
		}

		if (null == fceEligForDB.getNbrStepCalc()) {
			if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndHomeIncomeAfdcElgblty())) {
				resultList.add(ServiceConstants.CFCERNE_A05);
			}
		} else {
			if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndIncome185AfdcElgblty())) {
				resultList.add(ServiceConstants.CFCERNE_A11);
			}

			else if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndIncome100AfdcElgblty())) {
				resultList.add(ServiceConstants.CFCERNE_A12);
			}
		}

		if (ServiceConstants.Y.equalsIgnoreCase(fceEligForDB.getIndEquity())) {
			resultList.add(ServiceConstants.CFCERNE_A06);

		}
		if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndRemovalChildOrdered())) {
			resultList.add(ServiceConstants.CFCERNE_A08);
		}

		if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndRsnblEffortPrvtRemoval())) {
			resultList.add(ServiceConstants.CFCERNE_A09);
		}

		if (ServiceConstants.N.equalsIgnoreCase(fceEligForDB.getIndPrsManagingCvs())) {
			resultList.add(ServiceConstants.CFCERNE_A10);
		}

		return resultList;
	}

	/**
	 * Method Name: isLatestLegacyEligibilityOpenPrsPaid Method Description:To
	 * find the latest legacy eligiblity
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return boolean
	 */
	public boolean isLatestLegacyEligibilityOpenPrsPaid(Long idCase, Long idPerson) {

		boolean resultEligi = false;
		EligibilityDto eligibilityDto = fceDao.findLatestLegacyEligibility(idCase, idPerson);

		if (TypeConvUtil.isNullOrEmpty(eligibilityDto)) {
			return false;
		}

		if (!TypeConvUtil.isNullOrEmpty(eligibilityDto.getDtEligEnd())
				&& (ServiceConstants.GENERIC_END_DATE.getTime() != eligibilityDto.getDtEligEnd().getTime())) {
			return false;
		}

		String actualEligibility = eligibilityDto.getCdEligActual();

		resultEligi = (ServiceConstants.CELIGIBI_010.equalsIgnoreCase(actualEligibility)
				|| ServiceConstants.CELIGIBI_020.equalsIgnoreCase(actualEligibility)
				|| ServiceConstants.CELIGIBI_030.equalsIgnoreCase(actualEligibility));

		return resultEligi;
	}

	/**
	 * Method Name: saveEligibility Method Description:
	 * 
	 * @param fceEligibilityDB
	 */
	public void saveEligibility(FosterCareReviewDto fosterCareReviewDto) {
		Long idFceEligibility = fosterCareReviewDto.getIdFceEligibility();
		if (ObjectUtils.isEmpty(idFceEligibility) || idFceEligibility == 0l) {
			return;
		}
		FceEligibility fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
				idFceEligibility);
		if (fceEligibility == null) {
			return;
		}

		// fceEligibility =
		// fosterCareReviewDto.getFceEligibilityDto(fceEligibility);
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtCountableIncome())
				&& fosterCareReviewDto.getAmtCountableIncome() > 0) {
			fceEligibility.setAmtCountableIncome(fosterCareReviewDto.getAmtCountableIncome());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtGrossEarnedCrtfdGrp())
				&& fosterCareReviewDto.getAmtGrossEarnedCrtfdGrp() > 0) {
			fceEligibility.setAmtGrossEarnedCrtfdGrp(fosterCareReviewDto.getAmtGrossEarnedCrtfdGrp());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtGrossUnearnedCrtfdGrp())) {
			fceEligibility.setAmtGrossUnearnedCrtfdGrp(fosterCareReviewDto.getAmtGrossUnearnedCrtfdGrp());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtIncomeLimit())
				&& fosterCareReviewDto.getAmtIncomeLimit() > 0) {
			fceEligibility.setAmtIncomeLimit(fosterCareReviewDto.getAmtIncomeLimit());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtPweIncome()) && fosterCareReviewDto.getAmtPweIncome() > 0) {
			fceEligibility.setAmtPweIncome(fosterCareReviewDto.getAmtPweIncome());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtSsi()) && fosterCareReviewDto.getAmtSsi() > 0) {
			fceEligibility.setAmtSsi(fosterCareReviewDto.getAmtSsi());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentAlimony())
				&& fosterCareReviewDto.getAmtStepparentAlimony() > 0) {
			fceEligibility.setAmtStepparentAlimony(fosterCareReviewDto.getAmtStepparentAlimony());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentAllowance())
				&& fosterCareReviewDto.getAmtStepparentAllowance() > 0) {
			fceEligibility.setAmtStepparentAllowance(fosterCareReviewDto.getAmtStepparentAllowance());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentAppliedIncome())
				&& fosterCareReviewDto.getAmtStepparentAppliedIncome() > 0) {
			fceEligibility.setAmtStepparentAppliedIncome(fosterCareReviewDto.getAmtStepparentAppliedIncome());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentCntblUnearned())
				&& fosterCareReviewDto.getAmtStepparentCntblUnearned() > 0) {
			fceEligibility.setAmtStepparentCntblUnearned(fosterCareReviewDto.getAmtStepparentCntblUnearned());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentGrossEarned())
				&& fosterCareReviewDto.getAmtStepparentGrossEarned() > 0) {
			fceEligibility.setAmtStepparentGrossEarned(fosterCareReviewDto.getAmtStepparentGrossEarned());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentOutsidePmnt())
				&& fosterCareReviewDto.getAmtStepparentOutsidePmnt() > 0) {
			fceEligibility.setAmtStepparentOutsidePmnt(fosterCareReviewDto.getAmtStepparentOutsidePmnt());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentTotalCntbl())
				&& fosterCareReviewDto.getAmtStepparentTotalCntbl() > 0) {
			fceEligibility.setAmtStepparentTotalCntbl(fosterCareReviewDto.getAmtStepparentTotalCntbl());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getAmtStepparentWorkDeduct())
				&& fosterCareReviewDto.getAmtStepparentWorkDeduct() > 0) {
			fceEligibility.setAmtStepparentWorkDeduct(fosterCareReviewDto.getAmtStepparentWorkDeduct());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdBlocChild())) {
			fceEligibility.setCdBlocChild(fosterCareReviewDto.getCdBlocChild());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdEligibilityActual())) {
			fceEligibility.setCdEligibilityActual(fosterCareReviewDto.getCdEligibilityActual());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdEligibilitySelected())) {
			fceEligibility.setCdEligibilitySelected(fosterCareReviewDto.getCdEligibilitySelected());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdMedicaidEligibilityType())) {
			fceEligibility.setCdMedicaidEligibilityType(fosterCareReviewDto.getCdMedicaidEligibilityType());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdPweIrregularUnder100())) {
			fceEligibility.setCdPweIrregularUnder100(fosterCareReviewDto.getCdPweIrregularUnder100());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getCdPweSteadyUnder100())) {
			fceEligibility.setCdPweSteadyUnder100(fosterCareReviewDto.getCdPweSteadyUnder100());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtEligDtrmntnStart())) {
			fceEligibility.setDtEligDtrmntnStart(fosterCareReviewDto.getDtEligDtrmntnStart());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtEligibilityEnd())) {
			fceEligibility.setDtEligibilityEnd(fosterCareReviewDto.getDtEligibilityEnd());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtRemovalChildOrdered())) {
			fceEligibility.setDtRemovalChildOrdered(fosterCareReviewDto.getDtRemovalChildOrdered());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtReviewDate())) {
			fceEligibility.setDtReviewDate(fosterCareReviewDto.getDtReviewDate());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getDtRsnblEffortPreventRem())) {
			fceEligibility.setDtRsnblEffortPreventRem(fosterCareReviewDto.getDtRsnblEffortPreventRem());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getFceEligibilityCdPersonCitizenship())) {
			fceEligibility.setCdPersonCitizenship(fosterCareReviewDto.getFceEligibilityCdPersonCitizenship());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getFceEligibilityDtLastUpdate())) {
			fceEligibility.setDtLastUpdate(fosterCareReviewDto.getFceEligibilityDtLastUpdate());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdCase())) {
			fceEligibility.setIdCase(fosterCareReviewDto.getIdCase());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdEligibilityEvent())) {
			fceEligibility.setIdEligibilityEvent(fosterCareReviewDto.getIdEligibilityEvent());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdFceApplication())) {
			fceEligibility.setIdFceApplication(fosterCareReviewDto.getIdFceApplication());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdFceEligibility())) {
			fceEligibility.setIdFceEligibility(fosterCareReviewDto.getIdFceEligibility());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdFcePerson())) {
			fceEligibility.setIdFcePerson(fosterCareReviewDto.getIdFcePerson());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdFceReview())) {
			fceEligibility.setIdFceReview(fosterCareReviewDto.getIdFceReview());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdPerson())) {
			fceEligibility.setIdPerson(fosterCareReviewDto.getIdPerson());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentAltrntCustody())) {
			fceEligibility.setIndAbsentAltrntCustody(fosterCareReviewDto.getIndAbsentAltrntCustody());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentDeported())) {
			fceEligibility.setIndAbsentDeported(fosterCareReviewDto.getIndAbsentDeported());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentDeserted())) {
			fceEligibility.setIndAbsentDeserted(fosterCareReviewDto.getIndAbsentDeserted());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentDied())) {
			fceEligibility.setIndAbsentDied(fosterCareReviewDto.getIndAbsentDied());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentDivorced())) {
			fceEligibility.setIndAbsentDivorced(fosterCareReviewDto.getIndAbsentDivorced());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentFather())) {
			fceEligibility.setIndAbsentFather(fosterCareReviewDto.getIndAbsentFather());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentHospitalized())) {
			fceEligibility.setIndAbsentHospitalized(fosterCareReviewDto.getIndAbsentHospitalized());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentIncarcerated())) {
			fceEligibility.setIndAbsentIncarcerated(fosterCareReviewDto.getIndAbsentIncarcerated());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentMilitaryWork())) {
			fceEligibility.setIndAbsentMilitaryWork(fosterCareReviewDto.getIndAbsentMilitaryWork());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentMother())) {
			fceEligibility.setIndAbsentMother(fosterCareReviewDto.getIndAbsentMother());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentSeparated())) {
			fceEligibility.setIndAbsentSeparated(fosterCareReviewDto.getIndAbsentSeparated());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentWorkRelated())) {
			fceEligibility.setIndAbsentWorkRelated(fosterCareReviewDto.getIndAbsentWorkRelated());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndEquity())) {
			fceEligibility.setIndEquity(fosterCareReviewDto.getIndEquity());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildLivingPrnt6Mnths())) {
			fceEligibility.setIndChildLivingPrnt6Mnths(fosterCareReviewDto.getIndChildLivingPrnt6Mnths());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildQualifiedCitizen())) {
			fceEligibility.setIndChildQualifiedCitizen(fosterCareReviewDto.getIndChildQualifiedCitizen());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildSupportOrdered())) {
			fceEligibility.setIndChildSupportOrdered(fosterCareReviewDto.getIndChildSupportOrdered());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndChildUnder18())) {
			fceEligibility.setIndChildUnder18(fosterCareReviewDto.getIndChildUnder18());
		}
		fceEligibility
				.setIndCtznshpAttorneyReview(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpAttorneyReview());

		fceEligibility.setIndCtznshpBaptismalCrtfct(
				fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpBaptismalCrtfct());

		fceEligibility
				.setIndCtznshpBirthCrtfctFor(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpBirthCrtfctFor());

		fceEligibility
				.setIndCtznshpBirthCrtfctUs(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpBirthCrtfctUs());

		fceEligibility.setIndCtznshpChldFound(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpChldFound());

		fceEligibility
				.setIndCtznshpCitizenCrtfct(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpCitizenCrtfct());

		fceEligibility.setIndCtznshpDhsOther(fosterCareReviewDto.getIndCtznshpDhsOther());

		fceEligibility.setIndCtznshpDhsUs(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpDhsUs());

		fceEligibility.setIndCtznshpEvaluation(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpEvaluation());

		fceEligibility.setIndCtznshpForDocumentation(
				fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpForDocumentation());

		fceEligibility
				.setIndCtznshpHospitalCrtfct(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpHospitalCrtfct());

		fceEligibility.setIndCtznshpNoDocumentation(
				fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpNoDocumentation());

		fceEligibility
				.setIndCtznshpNtrlztnCrtfct(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpNtrlztnCrtfct());

		fceEligibility.setIndCtznshpPassport(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpPassport());

		fceEligibility
				.setIndCtznshpResidentCard(fosterCareReviewDto.getFceEligibilityDto().getIndCtznshpResidentCard());

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndEligibilityCourtMonth())) {
			fceEligibility.setIndEligibilityCourtMonth(fosterCareReviewDto.getIndEligibilityCourtMonth());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndEligible())) {
			fceEligibility.setIndEligible(fosterCareReviewDto.getIndEligible());
		}

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndFatherPwe())) {
			fceEligibility.setIndFatherPwe(fosterCareReviewDto.getIndFatherPwe());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndHomeIncomeAfdcElgblty())) {
			fceEligibility.setIndHomeIncomeAfdcElgblty(fosterCareReviewDto.getIndHomeIncomeAfdcElgblty());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndMeetsDpOrNotEs())) {
			fceEligibility.setIndMeetsDpOrNotEs(fosterCareReviewDto.getIndMeetsDpOrNotEs());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndMeetsDpOrNotSystem())) {
			fceEligibility.setIndMeetsDpOrNotSystem(fosterCareReviewDto.getIndMeetsDpOrNotSystem());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndMotherPwe())) {
			fceEligibility.setIndMotherPwe(fosterCareReviewDto.getIndMotherPwe());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndNarrativeApproved())) {
			fceEligibility.setIndNarrativeApproved(fosterCareReviewDto.getIndNarrativeApproved());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndOtherVerification())) {
			fceEligibility.setIndOtherVerification(fosterCareReviewDto.getIndOtherVerification());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndParentDisabled())) {
			fceEligibility.setIndParentDisabled(fosterCareReviewDto.getIndParentDisabled());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndParentalDeprivation())) {
			fceEligibility.setIndParentalDeprivation(fosterCareReviewDto.getIndParentalDeprivation());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndPrsManagingCvs())) {
			fceEligibility.setIndPrsManagingCvs(fosterCareReviewDto.getIndPrsManagingCvs());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndRemovalChildOrdered())) {
			fceEligibility.setIndRemovalChildOrdered(fosterCareReviewDto.getIndRemovalChildOrdered());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndRsdiVerification())) {
			fceEligibility.setIndRsdiVerification(fosterCareReviewDto.getIndRsdiVerification());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndRsnblEffortPrvtRemoval())) {
			fceEligibility.setIndRsnblEffortPrvtRemoval(fosterCareReviewDto.getIndRsnblEffortPrvtRemoval());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndSsiVerification())) {
			fceEligibility.setIndSsiVerification(fosterCareReviewDto.getIndSsiVerification());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getNbrCertifiedGroup())) {
			fceEligibility.setNbrCertifiedGroup(fosterCareReviewDto.getNbrCertifiedGroup());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getNbrParentsHome())) {
			fceEligibility.setNbrParentsHome(fosterCareReviewDto.getNbrParentsHome());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getTxtDeterminationComments())) {
			fceEligibility.setTxtDeterminationComments(fosterCareReviewDto.getTxtDeterminationComments());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIndAbsentNeverCohabitated())) {
			fceEligibility.setIndAbsentNeverCohabitated(fosterCareReviewDto.getIndAbsentNeverCohabitated());
		}
		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getNbrStepparentChildren())) {
			fceEligibility.setNbrStepparentChildren(fosterCareReviewDto.getNbrStepparentChildren());
		}

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdLastUpdatePerson())
				&& fosterCareReviewDto.getIdLastUpdatePerson() > 0l) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					fosterCareReviewDto.getIdLastUpdatePerson());
			fceEligibility.setPerson(person);

		}

		if (!ObjectUtils.isEmpty(fosterCareReviewDto.getIdStage()) && fosterCareReviewDto.getIdStage() > 0l) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, fosterCareReviewDto.getIdStage());
			fceEligibility.setStage(stage);
		}

		sessionFactory.getCurrentSession().saveOrUpdate(fceEligibility);
	}

	/**
	 * Method Name : save Eligibility
	 * 
	 * @param fceEligibilityDB
	 * @return void
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void saveEligibility(FceEligibilityDto fceEligibilityDB)
			throws IllegalAccessException, InvocationTargetException {

		if (fceEligibilityDB.getIdFceEligibility() == 0) {
			return;
		}
		FceEligibility fceEligibility = new FceEligibility();

		if (!TypeConvUtil.isNullOrEmpty(fceEligibilityDB.getIdFceEligibility())) {
			fceEligibilityDB.setDtLastUpdate(new Date());

			ConvertUtils.register(new DateConverter(null), Date.class);
			BeanUtils.copyProperties(fceEligibility, fceEligibilityDB);

			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					fceEligibilityDB.getIdPerson());
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, fceEligibilityDB.getIdStage());
			fceEligibility.setPerson(person);
			fceEligibility.setStage(stage);
			sessionFactory.getCurrentSession().saveOrUpdate(fceEligibility);
		}
	}

	/**
	 * Method Name: findEligibility Method Description:
	 * 
	 * @param idFceEligibility
	 * @return FceEligibilityLocal
	 */
	public FceEligibility findEligibility(Long idFceEligibility) {
		FceEligibility fceEligibility = new FceEligibility();
		try {
			fceEligibility = (FceEligibility) sessionFactory.getCurrentSession().get(FceEligibility.class,
					idFceEligibility);
			if (fceEligibility != null) {
				return fceEligibility;
			} else {
				throw new ServiceLayerException(
						messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));

			}
		} catch (ServiceLayerException e) {
			log.info("Exception in findeligibility " + e.getMessage());
		}

		return fceEligibility;
	}

	/**
	 * retrieves the legacy eligibility
	 * 
	 * @param idEligibilityEvent
	 * @return
	 */

	public Eligibility findLegacyEligibility(Long idEligibilityEvent) {
		log.info("findLegacyEligibility executing");
		Eligibility eligibility = new Eligibility();
		String legacyElgForEventSql = "select e from Eligibility e where e.idEligEvent = :idEligibilityEvent";

		Query query = sessionFactory.getCurrentSession().createQuery(legacyElgForEventSql);
		query.setParameter("idEligibilityEvent", idEligibilityEvent);
		List eligibilitylist = query.list();
		if (eligibilitylist.size() == 0) {
			return null;
		}
		if ((eligibilitylist.size() > 1)) {
			throw new IllegalStateException("expected only 1 result (got " + eligibilitylist.size() + ")");
		}

		eligibility = (Eligibility) eligibilitylist.get(0);
		return eligibility;
	}

	public void setChildSupportFromLegacy(Eligibility legacyEligibility) {

		// CodesTables.CELIGCSU_030
		if ((legacyEligibility != null) && (EligibilityUtil.isQuestionAnswered("030", legacyEligibility))) {
			// fceEligibilityDto.setIndChildSupportOrdered("Y");
		}
	}

	public static boolean isQuestionAnswered(String question, Eligibility eligibility) {
		return ((question.equals(eligibility.getCdEligCsupQuest1()))
				|| (question.equals(eligibility.getCdEligCsupQuest2()))
				|| (question.equals(eligibility.getCdEligCsupQuest3()))
				|| (question.equals(eligibility.getCdEligCsupQuest4())));
	}

	public FceEligibilityDto findEligibilityFordetermineEligibility(Long idFceEligibility) {
		FceEligibilityDto dto = new FceEligibilityDto();
		FceEligibility entity = findEligibility(idFceEligibility);
		try {
			ConvertUtils.register(new DateConverter(null), Date.class);
			BeanUtils.copyProperties(dto, entity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("Exception " + e.getMessage());
		}
		dto.setIdLastUpdatePerson(entity.getPerson().getIdPerson());
		dto.setIdStage(entity.getStage().getIdStage());
		return dto;
	}

	public FceEligibilityDto copyEligibility(FceEligibilityDto lastfceEligibilityDto, Long idLastUpdatePerson,
			boolean copyReasonsNotEligible) {

		if (TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto)) {
			return null;
		}
		FceEligibilityDto fceEligibilityDto = new FceEligibilityDto();
		FceEligibility fceEligibility;

		try {
			fceEligibility = createEligibility(lastfceEligibilityDto.getIdCase(), idLastUpdatePerson,
					lastfceEligibilityDto.getIdStage());

			if (!TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto.getIdFceApplication())) {
				fceEligibilityDto.setIdFceApplication(lastfceEligibilityDto.getIdFceApplication());
			}

			if (!TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto.getIdFceReview())) {
				fceEligibilityDto.setIdFceReview(lastfceEligibilityDto.getIdFceReview());
			}

			if (!TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto.getCdEligibilityActual())) {

				fceEligibilityDto.setCdEligibilityActual(lastfceEligibilityDto.getCdEligibilityActual());
			}

			if (!TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto.getNbrCertifiedGroup())) {
				fceEligibilityDto.setNbrCertifiedGroup(lastfceEligibilityDto.getNbrCertifiedGroup());
			}

			if (!TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto.getIndEligible())) {
				fceEligibilityDto.setIndEligible(lastfceEligibilityDto.getIndEligible());
			}

			if (!TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto.getCdPersonCitizenship())) {
				fceEligibilityDto.setCdPersonCitizenship(lastfceEligibilityDto.getCdPersonCitizenship());
			}

			// since it is false, hence not implementing
			// ReasonNotEligibleHelper.copyReasonsNotEligible() for now.
			if (copyReasonsNotEligible) {
				/*
				 * ReasonNotEligibleHelper.copyReasonsNotEligible(connection,
				 * lastIdFceEligibility, idFceEligibility);
				 */
			}
			// copy fce_person

			FcePersonDto fcePersonlast;
			if (!TypeConvUtil.isNullOrEmpty(lastfceEligibilityDto.getIdFcePerson())) {

				fcePersonlast = fceApplicationDao.findFcePerson(lastfceEligibilityDto.getIdFcePerson());

				if (!TypeConvUtil.isNullOrEmpty(fceEligibility.getIdFceEligibility())
						&& !TypeConvUtil.isNullOrEmpty(fcePersonlast.getIdFceEligibility())) {
					fcePersonlast.setIdFceEligibility(fceEligibility.getIdFceEligibility());
				}

				FcePerson fcePerson;
				if (!TypeConvUtil.isNullOrEmpty(fcePersonlast.getIdPerson())
						&& !TypeConvUtil.isNullOrEmpty(fceEligibility.getIdFceEligibility())) {

					fcePerson = fcePersonDao.save(fceEligibility.getIdFceEligibility(), fcePersonlast.getIdPerson());

					// associate child with eligibility
					if (!TypeConvUtil.isNullOrEmpty(fcePerson.getIdFcePerson())) {

						fceEligibilityDto.setIdFcePerson(fcePerson.getIdFcePerson());
					}
					// if
					// (!TypeConvUtil.isNullOrEmpty(fcePerson.getPerson().getIdPerson()))
					// {
					// fceEligibilityDto.setIdPerson(fcePerson.getPerson().getIdPerson());
					// }
				}
			}

		} catch (ServiceLayerException e) {
			log.error(e.getMessage());
		}

		return fceEligibilityDto;
	}

	public boolean isChildNewToSubcare(Long idPerson) {
		Long idFceApplication = 0l;

		Query query = sessionFactory.getCurrentSession().createQuery(isChildNewToSubcare);
		query.setParameter("idPerson", idPerson);
		List eligibilitylist = query.list();
		if (!eligibilitylist.isEmpty()) {
			FceApplication entity = (FceApplication) eligibilitylist.get(0);
			idFceApplication = entity.getIdFceApplication();
		}
		return (idFceApplication != 0l);
	}

}