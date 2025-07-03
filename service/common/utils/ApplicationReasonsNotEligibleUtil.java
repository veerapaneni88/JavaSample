package us.tx.state.dfps.service.common.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FceEligibility;
import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.fce.FceDepCareDeductDto;
import us.tx.state.dfps.service.fce.IncomeExpenditureDto;
import us.tx.state.dfps.service.fce.dao.DepCareDeductDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.incomeexpenditures.dao.IncomeExpendituresDao;
import us.tx.state.dfps.service.person.dao.FceEligibilityDao;

@Repository
public class ApplicationReasonsNotEligibleUtil {
	private static final Logger log = Logger.getLogger(ApplicationReasonsNotEligibleUtil.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	IncomeExpendituresDao incomeExpendituresDao;

	@Autowired
	DepCareDeductDao depCareDeductionDao;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	PersonUtil personUtil;

	@Autowired
	FceUtil fceUtil;

	@Autowired
	FceInitUtil fceInitUtil;

	@Autowired
	FceEligibilityDao fceEligibilityDao;
	
	@Autowired
	CodesDao codesDao;

	// used to calculate values that are not stored in the db for later lookup
	// private static int familySize = 0;

	public static boolean ineligibleDueToAnyReasonOtherThanCitizenshipRequirement(FceEligibilityDto fceEligibilityDto) {
		List<String> reasonsNotEligible = getReasonsNotEligible(fceEligibilityDto);

		reasonsNotEligible.remove(ServiceConstants.CFCERNE_A02);

		return (ServiceConstants.EMPTY_STR.equals(reasonsNotEligible));
	}

	public static List<String> getReasonsNotEligible(FceEligibilityDto fceEligibilityDB) {
		List<String> list = new ArrayList<>();

		if (ServiceConstants.N.equals(fceEligibilityDB.getIndChildUnder18())) {
			list.add(ServiceConstants.CFCERNE_A01);
		}
		if (ServiceConstants.N.equals(fceEligibilityDB.getIndChildQualifiedCitizen())) {
			list.add(ServiceConstants.CFCERNE_A02);
		}
		if (ServiceConstants.N.equals(fceEligibilityDB.getIndParentalDeprivation())) {
			list.add(ServiceConstants.CFCERNE_A03);
		}

		if (ServiceConstants.N.equals(fceEligibilityDB.getIndChildLivingPrnt6Mnths())) {
			list.add(ServiceConstants.CFCERNE_A04);
		}

		if (null == fceEligibilityDB.getNbrStepCalc()) {
			if (ServiceConstants.N.equals(fceEligibilityDB.getIndHomeIncomeAfdcElgblty()))

			{
				list.add(ServiceConstants.CFCERNE_A05);
			}
		} else {
			// Added AFDC 185% and 100% reasons not eligible
			if (ServiceConstants.N.equals(fceEligibilityDB.getIndIncome185AfdcElgblty()))

			{
				list.add(ServiceConstants.CFCERNE_A11);
			} else if (ServiceConstants.N.equals(fceEligibilityDB.getIndIncome100AfdcElgblty()))

			{
				list.add(ServiceConstants.CFCERNE_A12);
			}

		}
		if (ServiceConstants.Y.equals(fceEligibilityDB.getIndEquity())) {
			list.add(ServiceConstants.CFCERNE_A06);
		}

		if (ServiceConstants.N.equals(fceEligibilityDB.getIndRemovalChildOrdered()))

		{
			list.add(ServiceConstants.CFCERNE_A08);
		}
		if (ServiceConstants.N.equals(fceEligibilityDB.getIndRsnblEffortPrvtRemoval()))

		{
			list.add(ServiceConstants.CFCERNE_A09);
		}
		if (ServiceConstants.N.equals(fceEligibilityDB.getIndPrsManagingCvs()))

		{
			list.add(ServiceConstants.CFCERNE_A10);
		}

		return list;
	}

	public void calculateSystemDerivedParentalDeprivation(String cdLivingCondition, FceApplicationDto fceApplicationDto,
			FceEligibilityDto fceEligibilityDto) {
		FceEligibility fceEligibility = fceEligibilityDao
				.getFceEligibilitybyId(fceApplicationDto.getIdFceEligibility());
		fceEligibility.setIndMeetsDpOrNotSystem(ServiceConstants.Y);
		// if the applicaiton is creatd after R2 release date, set this same as System DD
		Date dtR2Release = codesDao.getAppRelDate(ServiceConstants.R2_REL_CODE);
		if (!ObjectUtils.isEmpty(fceApplicationDto.getEventDate()) && !fceApplicationDto.getEventDate().before(dtR2Release)){
			fceEligibility.setIndMeetsDpOrNotEs(ServiceConstants.Y);
		}

		boolean noneBothPrnt = false;
		boolean noneOnePrnt = false;
		// if None selected
		if (ServiceConstants.CFCELIV_N.equals(cdLivingCondition)
				&& !ObjectUtils.isEmpty(fceEligibilityDto.getIndChildLivingPrnt6Mnths())
				&& (ServiceConstants.Y.equals(fceEligibilityDto.getIndChildLivingPrnt6Mnths())
						&& !ObjectUtils.isEmpty(fceApplicationDto.getCdNotaMostRecent()))) {
			if (ServiceConstants.CFCELIV_B.equals(fceApplicationDto.getCdNotaMostRecent()))
				noneBothPrnt = true;

			else if (ServiceConstants.CFCELIV_O.equals(fceApplicationDto.getCdNotaMostRecent()))
				noneOnePrnt = true;
		}
		// child is living with both parents
		if (ServiceConstants.CFCELIV_B.equals(cdLivingCondition) || noneBothPrnt) {

			int nbrCertifiedGroup = 0;
			if (fceEligibilityDto.getNbrCertifiedGroup() != null) {
				nbrCertifiedGroup = fceEligibilityDto.getNbrCertifiedGroup().intValue();
			}

			if ((nbrCertifiedGroup == 0) && ((ServiceConstants.CAPS_FCE.equals(fceApplicationDto.getCdApplication()))
					|| (ServiceConstants.IMP.equals(fceApplicationDto.getCdApplication())))) {
				// Assume both parents and child are in the certified group for
				// the purpose of calculating underemployed when child is from
				// CAPS.
				nbrCertifiedGroup = 3;
			}

			Double fcePweUnderemployed = 0.0;
			if (nbrCertifiedGroup > 0) {
				fcePweUnderemployed = getFcePweUnderemployed(nbrCertifiedGroup);
			}

			boolean oneParentIsDisabled = ServiceConstants.Y.equals(fceEligibilityDto.getIndParentDisabled());
			boolean pweWorksIrregularlyUnder100HoursPerMonth = false;

			if (ServiceConstants.Y.equals(fceEligibilityDto.getCdPweIrregularUnder100())) {
				pweWorksIrregularlyUnder100HoursPerMonth = true;
			}

			boolean pweWorksRegularlyUnder100HoursPerMonth = false;
			if (ServiceConstants.Y.equals(fceEligibilityDto.getCdPweSteadyUnder100())) {
				pweWorksRegularlyUnder100HoursPerMonth = true;
			}

			boolean pweIsUnderEmployed = ((!ObjectUtils.isEmpty(fceEligibilityDto.getAmtPweIncome()))
					&& (fceEligibilityDto.getAmtPweIncome() <= fcePweUnderemployed));

			if (oneParentIsDisabled || pweWorksIrregularlyUnder100HoursPerMonth
					|| pweWorksRegularlyUnder100HoursPerMonth || pweIsUnderEmployed) {
				return;
			}
		} // added condition if one parent under None option do check
		else if (ServiceConstants.CFCELIV_O.equals(cdLivingCondition) || noneOnePrnt) {

			if (!ServiceConstants.Y.equals(fceEligibilityDto.getIndAbsentMilitaryWork())) {

				return;
			}
		}
		// this else indicates that the child was not living with at least one
		// parent
		else {

			return;
		}

		fceEligibility.setIndMeetsDpOrNotSystem(ServiceConstants.N);
		fceEligibility.setIndMeetsDpOrNotEs(ServiceConstants.N);

		sessionFactory.getCurrentSession().saveOrUpdate(fceEligibility);
	}

	/**
	 * Method Name: getFcePweUnderemployed Method Description: This method finds
	 * FcePwe Under Employer
	 * 
	 * @param nbrCertifiedGroup
	 * @return Double
	 *
	 */
	private Double getFcePweUnderemployed(int nbrCertifiedGroup) {
		if (nbrCertifiedGroup > ServiceConstants.MAX_NBR_CERTIFIED) {
			nbrCertifiedGroup = ServiceConstants.MAX_NBR_CERTIFIED;
		}
		return (double) (nbrCertifiedGroup + ServiceConstants.NUM_ONE);
	}

	// Income Expenditures page
	/**
	 * Method Name: calculateFceAppDetail Method Description: This method is
	 * used to calculate fce application data for further processing.
	 * 
	 * @param IncomeExpenditureDto
	 * @throws Exception
	 *
	 */

	public void calculateFceAppDetail(
			IncomeExpenditureDto incomeExpenditureDto) /* throws Exception */ {
		FceEligibility fceEligibility = fceEligibilityDao
				.getFceEligibilitybyId(incomeExpenditureDto.getFceApplicationDto().getIdFceEligibility());

		// clear this in case it was set by a previous calculate
		// !!! this should probably happen on save of every type except
		// Worksheet,
		// but that's a lot of places
		fceEligibility.setIndEligible(null);

		int familySize = updatePrinciplesInformation(fceEligibility);
		updateIncomeLimitsFromTableLookups(fceEligibility, familySize);
		updateIncomeInformation(fceEligibility);
		checkAge(fceEligibility);
		checkCitizenship(fceEligibility);
		// checkDomicile(fceEligibility);
		checkIncome(fceEligibility);
		checkDeprivation(fceEligibility);
		// end verified

		if(ServiceConstants.Y.equals(incomeExpenditureDto.getFceApplicationDto().getIndRefusalDocsPresent())) {
			fceEligibility.setIndHomeIncomeAfdcElgblty(ServiceConstants.N);
			fceEligibility.setIndIncome100AfdcElgblty(ServiceConstants.N);
		}
		fceEligibilityDao.save(fceEligibility);
	}

	protected int updatePrinciplesInformation(FceEligibility fceEligibility) {
		int familySize = 0;
		List<FcePerson> principles = fceEligibility.getFcePersons().stream().collect(Collectors.toList());

		long nbrCertifiedGroup = 0;
		long nbrParents = 0;
		for (FcePerson person : principles) {
			if (ServiceConstants.Y.equals(person.getIndCertifiedGroup())) {
				nbrCertifiedGroup++;
			}
			String cdRelInt = person.getCdRelInt();
			if ((ServiceConstants.Y.equals(person.getIndCertifiedGroup())) && (personUtil.isParent(cdRelInt))) {
				nbrParents++;
			}
			// we only want to add stepparents here
			if ((ServiceConstants.Y.equals(person.getIndPersonHmRemoval())) && (personUtil.isStepParent(cdRelInt))) {
				familySize++;
			}
		}

		// if have stepparents, count their children
		if (familySize > 0) {
			// This is where we add stepparent's children
			Long stepKids = fceEligibility.getNbrStepparentChildren();
			if (stepKids != null) {
				familySize += stepKids.intValue();
			}
		}

		fceEligibility.setNbrCertifiedGroup(new Long(nbrCertifiedGroup));
		fceEligibility.setNbrParentsHome(new Long(nbrParents));
		return familySize;
	}

	protected void updateIncomeLimitsFromTableLookups(FceEligibility fceEligibility, int familySize) {
		int nbrCertifiedGroup = fceEligibility.getNbrCertifiedGroup().intValue();
		int nbrParents = fceEligibility.getNbrParentsHome().intValue();

		Double amtIncomeLimit = fceInitUtil.getFceAfdcIncomeLimit(nbrParents, nbrCertifiedGroup);
		fceEligibility.setAmtIncomeLimit(new Double(amtIncomeLimit));

		// start SIR 1010395
		// get 100% AFDC Need standard for Family
		Double amtIncomeLimit100 = fceInitUtil.getFceAfdcIncomeLimit100(nbrParents, nbrCertifiedGroup);
		fceEligibility.setAmtIncomeLimit100(new Double(amtIncomeLimit100));
		// end SIR 1010395

		Double amtStepparentAllowance = fceInitUtil.getFceStepparentAllowance(familySize);
		fceEligibility.setAmtStepparentAllowance(new Double(amtStepparentAllowance));
	}

	private void updateIncomeInformation(FceEligibility fceEligibility) {
		long idFceEligibility = fceEligibility.getIdFceEligibility().longValue();
		List<FceIncomeDto> incomesForChild = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_INCOME, ServiceConstants.FCE_CHILD);

		List<FceIncomeDto> incomesForFamily = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_INCOME, ServiceConstants.FCE_FAMILY);
		// start SIR 1010395
		List<FceDepCareDeductDto> depCareDeductions = depCareDeductionDao.findFceDepCareDeduct(idFceEligibility,
				ServiceConstants.TRUE_VALUE);
		// end SIR 1010395

		// sum countable incomes for child
		Double amtCntblEarnedCrtfdGrp = 0.0;
		Double amtCntblUnearnedCrtfdGrp = 0.0;
		// SIR 1010395
		Double amtTotalWorkIncome = 0.0;
		// Hash table to store each persons work related deduction
		Map<Long, Double> workIncomePerson = new HashMap<>();
		Double amtWorkIncome = null;

		for (FceIncomeDto fceIncomeDto : incomesForChild) {
			// child is always in the certified group, so no need to check that
			Double amtIncome = fceIncomeDto.getAmtIncome();

			if (ServiceConstants.Y.equals(fceIncomeDto.getIndCountable())) {
				if (ServiceConstants.Y.equals(fceIncomeDto.getIndEarned())) {
					amtCntblEarnedCrtfdGrp += amtIncome;
					// Total earned work related income for child
					amtTotalWorkIncome = 0.0;
					// check if income for person already on hash table
					amtWorkIncome = workIncomePerson.get(fceIncomeDto.getIdFcePerson());
					if (amtWorkIncome != null) {
						// set as part of total income
						amtTotalWorkIncome = amtWorkIncome;
					}
					// add to current income
					amtTotalWorkIncome += amtIncome;
					// set the total back onto the hash table
					amtWorkIncome = amtTotalWorkIncome;
					workIncomePerson.put(fceIncomeDto.getIdFcePerson(), amtWorkIncome);
					// end SIR 1010395
				} else {
					amtCntblUnearnedCrtfdGrp += amtIncome;
				}
			}
		}

		// step parents will be encountered when we go through the family
		// incomes;
		Double amtStepparentCntblEarned = 0.0;
		Double amtStepparentCntblUnearned = 0.0;
		Map<Long, Double> stepparentIncomeMap = new HashMap();
		// sum countable incomes for family in the certified group with the
		// just-calculated countable incomes for the child
		
		Map<Long, FcePerson> personMap = new HashMap<Long, FcePerson>();
		
		int index = 0;
		if(!CollectionUtils.isEmpty(fceEligibility.getFcePersons())) {
			fceEligibility.getFcePersons().stream().forEach(fceperson->{
				log.debug(fceperson.getPerson().getIdPerson());
				if(!personMap.containsKey(fceperson.getPerson().getIdPerson())) {
					personMap.put(fceperson.getPerson().getIdPerson(), fceperson);
				}
			});
		}
		for (FceIncomeDto fceIncomeDto : incomesForFamily) {
			Double amtIncome = fceIncomeDto.getAmtIncome();
			String cdRelInt = personMap.get(fceIncomeDto.getIdPerson()).getCdRelInt();

			if ((ServiceConstants.Y.equals(fceIncomeDto.getIndCertifiedGroup())
					&& (PersonUtil.isStepParent(cdRelInt) == false))
					&& ServiceConstants.Y.equals(fceIncomeDto.getIndCountable())) {
				if (ServiceConstants.Y.equals(fceIncomeDto.getIndEarned())) {
					amtCntblEarnedCrtfdGrp += amtIncome;
					// Total earned work related income for family member
					amtTotalWorkIncome = 0.0;
					// check if income for person already on hash table
					amtWorkIncome = (Double) workIncomePerson.get(fceIncomeDto.getIdFcePerson());
					if (amtWorkIncome != null) {
						// set as part of total income
						amtTotalWorkIncome = amtWorkIncome;
					}
					// add to current income
					amtTotalWorkIncome += amtIncome;
					// set the total back onto the hash table
					amtWorkIncome = amtTotalWorkIncome;
					workIncomePerson.put(fceIncomeDto.getIdFcePerson(), amtWorkIncome);
				} else {
					amtCntblUnearnedCrtfdGrp += amtIncome;
				}
			}
			// stepparents are never in the certified group, sum income from
			// stepparents that are living in the home
			FcePerson child = personMap.get(fceIncomeDto.getIdPerson());
			if (PersonUtil.isStepParent(cdRelInt) && ServiceConstants.Y.equals(child.getIndPersonHmRemoval())
					&& ServiceConstants.Y.equals(fceIncomeDto.getIndCountable())) {
				if (ServiceConstants.Y.equals(fceIncomeDto.getIndEarned())) {
					amtStepparentCntblEarned += amtIncome;
					// need to store stepparent income for each
					// stepparent to calculate the appropriate deduction
					Long idFceStepparent = fceIncomeDto.getIdFcePerson();
					Double amtStepparentIncome = (Double) stepparentIncomeMap.get(idFceStepparent);

					amtStepparentIncome = amtStepparentIncome == null ? new Double(amtIncome)
							: new Double(amtStepparentIncome + amtIncome);

					stepparentIncomeMap.put(idFceStepparent, amtStepparentIncome);
				} else {
					amtStepparentCntblUnearned += amtIncome;
				}
			}
		}

		// The countable values for incomes from the certified group
		fceEligibility.setAmtGrossEarnedCrtfdGrp(new Double(amtCntblEarnedCrtfdGrp));
		fceEligibility.setAmtGrossUnearnedCrtfdGrp(new Double(amtCntblUnearnedCrtfdGrp));

		// The countable values for the stepparent
		fceEligibility.setAmtStepparentGrossEarned(new Double(amtStepparentCntblEarned));
		fceEligibility.setAmtStepparentCntblUnearned(new Double(amtStepparentCntblUnearned));

		// The sum of the work related expenses deduction for each stepparent
		Double amtStepparentWorkDeduct = 0.0;

		for (Map.Entry<Long, Double> stepPrentIncomeentry : stepparentIncomeMap.entrySet()) {
			Double amtStepparentIncomeObject = stepPrentIncomeentry.getValue();
			Double amtStepparentIncome = amtStepparentIncomeObject != null ? amtStepparentIncomeObject : 0.0;

			amtStepparentWorkDeduct += (amtStepparentIncome > ServiceConstants.WORK_RELATED_EXPENSES_DEDUCTION
					? ServiceConstants.WORK_RELATED_EXPENSES_DEDUCTION : amtStepparentIncome);
		}
		fceEligibility.setAmtStepparentWorkDeduct(new Double(amtStepparentWorkDeduct));

		// Total countable stepparent income is the total earned, minus the
		// allowable deduction, plus the amount unearned
		Double amtStepparentTotalCntbl = amtStepparentCntblEarned - amtStepparentWorkDeduct
				+ amtStepparentCntblUnearned;

		fceEligibility.setAmtStepparentTotalCntbl(new Double(amtStepparentTotalCntbl));

		// -- Alimony, Child Support, and Other Monthly Payments to Dependents
		// outside the home for stepparents
		Double amtStepparentOutsidePayment = 0.;
		if (fceEligibility.getAmtStepparentOutsidePmnt() != null) {
			amtStepparentOutsidePayment = fceEligibility.getAmtStepparentOutsidePmnt();
		}

		Double amtStepparentAlimonyPayment = 0.;
		if (fceEligibility.getAmtStepparentAlimony() != null) {
			amtStepparentAlimonyPayment = fceEligibility.getAmtStepparentAlimony();
		}

		Double amtTotalOutsidePayments = amtStepparentOutsidePayment + amtStepparentAlimonyPayment;

		// !!! should we store amtTotalOutsidePayments somewhere?

		// -- Applied Income of Stepparent
		Double amtStepparentAppliedIncome = amtStepparentTotalCntbl - amtTotalOutsidePayments
				- fceEligibility.getAmtStepparentAllowance();

		if (amtStepparentAppliedIncome < 0) {
			amtStepparentAppliedIncome = 0.0;
		}

		fceEligibility.setAmtStepparentAppliedIncome(new Double(amtStepparentAppliedIncome));

		// -- Total Countable Income
		Double amtCountableIncome = amtCntblEarnedCrtfdGrp + amtCntblUnearnedCrtfdGrp + amtStepparentAppliedIncome;
		fceEligibility.setAmtCountableIncome(new Double(amtCountableIncome));

		// start SIR 1010395
		// The sum of Dependent Care Deductions
		Double amtTotalDepCareDeductions = 0.0;
		// Hash table to store each dependents deduction
		Map<Long, Double> dependentDeductions = new HashMap<>();
		Double amtDeduction = null;
		Double totalDeduction = 0.0;

		for (FceDepCareDeductDto fceDepCareDeductDto : depCareDeductions) {
			// if dependent already on hash table
			if (dependentDeductions.containsKey(fceDepCareDeductDto.getIdFceDependentPerson())) {
				// get amt for deduction already on the hash table
				amtDeduction = (Double) dependentDeductions.get(fceDepCareDeductDto.getIdFceDependentPerson());

				// sum with current deduction we are iterating through
				totalDeduction = amtDeduction.doubleValue() + fceDepCareDeductDto.getAmtDeduction().doubleValue();

				// if less than 2 yrs and total deduction > 175 , set dependents
				// deduction to 175 on hash table
				if ((fceDepCareDeductDto.getNbrDependentAge() < ServiceConstants.DEDUCTION_AGE_LIMIT)
						&& totalDeduction > ServiceConstants.MAX_DEDUCTION_AMT_UNDER_AGE_LIMIT)
					totalDeduction = ServiceConstants.MAX_DEDUCTION_AMT_UNDER_AGE_LIMIT;

				// if less than 2 yrs and total deduction > 200 , set dependents
				// deduction to 200 on hash table
				if ((fceDepCareDeductDto.getNbrDependentAge() >= ServiceConstants.DEDUCTION_AGE_LIMIT)
						&& totalDeduction > ServiceConstants.MAX_DEDUCTION_AMT_ABOVE_AGE_LIMIT)
					totalDeduction = ServiceConstants.MAX_DEDUCTION_AMT_ABOVE_AGE_LIMIT;

				dependentDeductions.put(fceDepCareDeductDto.getIdFceDependentPerson(), new Double(totalDeduction));
			} else {
				dependentDeductions.put(fceDepCareDeductDto.getIdFceDependentPerson(),
						fceDepCareDeductDto.getAmtDeduction().doubleValue());
			}
		}

		amtTotalDepCareDeductions = dependentDeductions.values().stream().mapToDouble(o -> o).sum();

		// total the work related deductions for each person
		Double amtTotalWorkIncDeduct = 0.0;
		amtTotalWorkIncome = 0.0;
		for (Map.Entry<Long, Double> e : workIncomePerson.entrySet()) {
			// get total work related income for each person
			amtTotalWorkIncome = e.getValue();
			// if income greater than $90 then only allow $90 deduction else add
			// income as part of deduction
			if (amtTotalWorkIncome > ServiceConstants.WORK_RELATED_EXPENSES_DEDUCTION)
				amtTotalWorkIncDeduct += ServiceConstants.WORK_RELATED_EXPENSES_DEDUCTION;
			else
				amtTotalWorkIncDeduct += amtTotalWorkIncome;
		}

		// Calculate Countable Earned Income
		Double amtCntblIncEarned = amtCntblEarnedCrtfdGrp - (amtTotalWorkIncDeduct + amtTotalDepCareDeductions);

		if (amtCntblIncEarned < 0) {
			amtCntblIncEarned = 0.0;
		}

		fceEligibility.setAmtWorkIncDeduct(new Double(amtTotalWorkIncDeduct));
		fceEligibility.setAmtDepCareDeduct(new Double(amtTotalDepCareDeductions));
		fceEligibility.setAmtCntblIncEarned(new Double(amtCntblIncEarned));

		// Total Countable Income for 100% AFDC Income
		Double amtCntblIncome100 = amtCntblIncEarned + amtCntblUnearnedCrtfdGrp + amtStepparentAppliedIncome;

		fceEligibility.setAmtCntblIncome100(new Double(amtCntblIncome100));

		// set flag to indicate we are doing a 2 Step process , prior to this
		// release the field will be null
		fceEligibility.setNbrStepCalc(Long.valueOf(ServiceConstants.NBR_STEPS_IN_AFDC_INCOME_DETERMINATION));
		// end SIR 1010395

	}

	/**
	 * SIR 23585 Added connection parameter, Determine Age difference between
	 * the LegalStatusDate and DateOfBirth as supposed to taking the Age from
	 * FCE_PERSON.NBR_AGE column
	 * 
	 * @throws Exception
	 */
	protected void checkAge(FceEligibility fceEligibility) {

		fceEligibility.setIndChildUnder18(ServiceConstants.Y);

		long idFcePerson = fceEligibility.getIdFcePerson().longValue();
		Map<Long, FcePerson> personMap = fceEligibility.getFcePersons().stream()
				.collect(Collectors.toMap(o -> o.getIdFcePerson(), o -> o));
		FcePerson child = personMap.get(idFcePerson);

		if (DateUtils.getAge(child.getDtBirth()) >= ServiceConstants.MAX_ELIGIBLE_AGE) {
			/*
			 * SIR 23585 Check the age when the child entered DFPS care. Age
			 * determination is done between DateOfBirth and the
			 * LegalStatusDate[the date the child entered DFPS care]
			 */
			Long idCase = fceEligibility.getIdCase();
			String cdStageType = fceEligibility.getStage().getCdStageType();

			Date dtEnteredCare = personUtil.getLegalStatusDate(fceEligibility.getIdPerson(), idCase, cdStageType);
			if (DateUtils.getAge(dtEnteredCare) >= ServiceConstants.MAX_ELIGIBLE_AGE) {
				fceEligibility.setIndChildUnder18(ServiceConstants.N);
			}
		}
	}

	protected static void checkCitizenship(FceEligibility fceEligibility) {
		fceEligibility.setIndChildQualifiedCitizen(ServiceConstants.Y);

		String cdPersonCitizenship = fceEligibility.getCdPersonCitizenship();
		if (CodesConstant.CCTZNSTA_TMR.equals(cdPersonCitizenship)) {
			fceEligibility.setIndChildQualifiedCitizen(ServiceConstants.N);
		}
	}

	// // !!! not clear code in legacy EJB, hence commented
	// protected static void checkDomicile(FceEligibility fceEligibility) {
	// if
	// (CodesConstant.CFCELIV_N.equalsIgnoreCase(fceEligibility.getFceApplicationDto().getCdLivingMonthRemoval())
	// &&
	// !(ServiceConstants.Y.equals(fceEligibilityDto.getIndChildLivingPrnt6Mnths()
	// ))) {
	// // !!! do I need another indicator to keep this case?
	// }
	// }

	protected static void checkIncome(FceEligibility fceEligibility) {
		// Initialize Indicators to Y
		// fceEligibilityDto.setIndHomeIncomeAfdcElgblty(ServiceConstants.Y);

		fceEligibility.setIndHomeIncomeAfdcElgblty(ServiceConstants.Y);
		fceEligibility.setIndIncome185AfdcElgblty(ServiceConstants.Y);
		fceEligibility.setIndIncome100AfdcElgblty(ServiceConstants.Y);

		// SIR 23305 - Modified to make Income Eligibility not valid when
		// Amount Countable Income is greater than or equal to(>=) AmountIncome
		// Limit (BEN).
		// jacobj above SIR 23305 comment is no more valid

		// if countable income is less than 185% Income Limit set to N
		if (fceEligibility.getAmtCountableIncome() > fceEligibility.getAmtIncomeLimit()) {
			fceEligibility.setIndIncome185AfdcElgblty(ServiceConstants.N);
		}

		// if countable income is less than 100% Income Limit set to N
		if (fceEligibility.getAmtCntblIncome100() > fceEligibility.getAmtIncomeLimit100()) {
			fceEligibility.setIndIncome100AfdcElgblty(ServiceConstants.N);
		}

		// if either indicators are N Set HomeIncomeAfdcElgblty to N
		if (fceEligibility.getIndIncome185AfdcElgblty().equals(ServiceConstants.N)
				|| fceEligibility.getIndIncome100AfdcElgblty().equals(ServiceConstants.N)) {
			fceEligibility.setIndHomeIncomeAfdcElgblty(ServiceConstants.N);
		}
	}

	protected static void checkDeprivation(FceEligibility fceEligibility) {
		// !!! there are 3 indicators for Parental Deprivation; I'm not sure
		// that we need all 3
		fceEligibility.setIndParentalDeprivation(fceEligibility.getIndMeetsDpOrNotEs());
	}

}
