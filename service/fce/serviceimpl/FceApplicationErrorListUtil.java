package us.tx.state.dfps.service.fce.serviceimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FcePerson;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.fce.FceDepCareDeductDto;
import us.tx.state.dfps.service.fce.IncomeExpenditureDto;
import us.tx.state.dfps.service.fce.dao.DepCareDeductDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;
import us.tx.state.dfps.service.fce.dto.FcePersonDto;
import us.tx.state.dfps.service.fostercarereview.dao.FcePersonDao;
import us.tx.state.dfps.service.incomeexpenditures.dao.IncomeExpendituresDao;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FceApplicationErrorListUtil On the Domicile and Deprivation page
 * of the Foster Care Assistance Application, you can choose a Living
 * Arrangement of Living with One Legal or Biological parent when you have two
 * person with a Rel/Int of Parent (Birth) checked on the Persons Living in Home
 * at time of Removal. When you click the Submit Application button, you will
 * not get a message that your chosen Living Arrangment is inconsistent with the
 * Persons in the Home. Nov 29, 2017- 5:00 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Service
@Transactional
public class FceApplicationErrorListUtil {

	@Autowired
	FcePersonDao fcePersonDao;

	@Autowired
	IncomeExpendituresDao incomeExpendituresDao;

	@Autowired
	DepCareDeductDao depCareDeductionDao;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	PersonUtil personUtil;

	@Autowired
	StageDao stageDao;

	private static final int MAX_ELIGIBLE_AGE = 18;

	/**
	 * Method Name: checkApplicationErrors Method Description:
	 * 
	 * @param incomeExpenditureDto
	 * @param includeCalculateErrors
	 * 
	 */
	public List<ErrorDto> checkApplicationErrors(IncomeExpenditureDto incomeExpenditureDto,
			boolean includeCalculateErrors) {
		// to make caching pre-calculated values (e.g. the list of principles)
		// easier, create an
		// instance and work off that

		FceApplicationDto fceApplicationDto = incomeExpenditureDto.getFceApplicationDto();
		FceEligibilityDto fceEligibilityDto = incomeExpenditureDto.getFceEligibilityDto();
		boolean hasStepParent = intializeCheckErrors(incomeExpenditureDto, fceEligibilityDto);
		FcePerson child = fcePersonDao.getFcepersonById(incomeExpenditureDto.getFceEligibilityDto().getIdFcePerson());

		List<ErrorDto> errorDtos = new ArrayList<>();

		// Errors that are always checked
		checkAddrsOfRmvlReq(incomeExpenditureDto, errorDtos);
		checkChldAtHomeReq(child, errorDtos);
		checkManagConserQuestions(incomeExpenditureDto.getFceApplicationDto(), errorDtos);
		checkNumParentsLivingAtHome(incomeExpenditureDto, errorDtos);
		checkDobRequired(child, errorDtos);
		checkVerifDobReq(fceApplicationDto, errorDtos);
		checkCitizenRequired(fceEligibilityDto, errorDtos);
		checkVerifCitizenReq(fceEligibilityDto, errorDtos);
		checkEvalCnclRequired(fceApplicationDto, fceEligibilityDto, errorDtos);
		checkSpcfyLvgArrgmnt(fceApplicationDto, errorDtos);

		checkHealthInsRequired(fceApplicationDto, errorDtos);

		checkNotLivingAtHome(incomeExpenditureDto, errorDtos);
		if (!ObjectUtils.isEmpty(incomeExpenditureDto.getIsRelDateBefore())
				&& incomeExpenditureDto.getIsRelDateBefore()) {
			checkIncomeAssistQuestionReq(fceApplicationDto, errorDtos);
		}
		checkFamilyNoIncm(incomeExpenditureDto, errorDtos);
		checkNoCombinedIncomeReq(incomeExpenditureDto, errorDtos);
		checkIncmDtrmntnCmntReq(fceApplicationDto, errorDtos);
		checkEquityQuesReq(fceEligibilityDto, errorDtos);

		// only require questions if the child has stepparents
		if (hasStepParent) {
			checkStepparentChildrenReq(fceEligibilityDto, errorDtos);
			checkAlimonyPaymentsReq(fceEligibilityDto, errorDtos);
			checkOtherPaymentsReq(fceEligibilityDto, errorDtos);
			checkStepparentNotCertified(incomeExpenditureDto, errorDtos);
		}

		checkChildSupQuesReq(fceApplicationDto, errorDtos);

		checkDocChecklistRequired(fceApplicationDto, errorDtos);

		// Errors that are only checked when clicking Calculate
		if (includeCalculateErrors) {
			checkChldInCertGroup(child, errorDtos);
			checkCountableExemptIncome(incomeExpenditureDto, errorDtos);
			checkEvalApprovalReq(fceApplicationDto, fceEligibilityDto, errorDtos);
			checkDdConfirmation(fceEligibilityDto, errorDtos); 
			checkEquityInconsistent(incomeExpenditureDto, errorDtos);
		}

		checkEnteryLegalStatusPresent(incomeExpenditureDto, child, errorDtos);

		return errorDtos;

	}

	/**
	 * Method Name: intializeCheckErrors Method Description:
	 * 
	 * @param incomeExpenditureDto
	 * @param fceEligibilityDto
	 * @return
	 */
	private boolean intializeCheckErrors(IncomeExpenditureDto incomeExpenditureDto,
			FceEligibilityDto fceEligibilityDto) {
		long idFceEligibility = fceEligibilityDto.getIdFceEligibility().longValue();

		boolean hasStepParent = false;

		Map<Long, FcePersonDto> peresonMap = fcePersonDao.getFcePersonDtosbyEligibilityId(idFceEligibility);
		List<FcePersonDto> principles = peresonMap.values().stream().collect(Collectors.toList());

		for (FcePersonDto fcePersonDto : principles) {
			String cdRelInt = fcePersonDto.getCdRelInt();
			if (PersonUtil.isStepParent(cdRelInt)) {
				hasStepParent = true;
				break;
			}
		}

		List<FceIncomeDto> incomesForChild = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_INCOME, ServiceConstants.FCE_CHILD);
		List<FceIncomeDto> incomesForFamily = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_INCOME, ServiceConstants.FCE_FAMILY);
		List<FceIncomeDto> resourcesForChild = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_RESOURCE, ServiceConstants.FCE_CHILD);
		List<FceIncomeDto> resourcesForFamily = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_RESOURCE, ServiceConstants.FCE_FAMILY);

		incomeExpenditureDto.setIncomeForChild(incomesForChild);
		incomeExpenditureDto.setIncomeForFamily(incomesForFamily);
		incomeExpenditureDto.setResourcesForFamily(resourcesForFamily);
		incomeExpenditureDto.setResourcesForChild(resourcesForChild);
		incomeExpenditureDto.setPrinciples(principles);
		return hasStepParent;
	}

	/**
	 * Method Name: checkDepCareDeductionErrors Method Description: This method
	 * is used to check department care deduction errors.
	 * 
	 * @param incomeExpenditureDto
	 */
	// new method to check if every valid dependent care deduction adult has
	// earned Income
	public ErrorDto checkDepCareDeductionErrors(IncomeExpenditureDto incomeExpenditureDto) {
		// to make caching pre-calculated values (e.g. the list of principles)
		// easier, create an
		// instance and work off that

		FceEligibilityDto fceEligibilityDto = incomeExpenditureDto.getFceEligibilityDto();
		long idFceEligibility = fceEligibilityDto.getIdFceEligibility().longValue();

		List<FceDepCareDeductDto> validDepCareDeductions = depCareDeductionDao.findFceDepCareDeduct(idFceEligibility,
				true);
		List<FceIncomeDto> incomesForFamily = incomeExpendituresDao.findFceIncomeOrResourceForChildOrFamily(
				idFceEligibility, ServiceConstants.FCE_INCOME, ServiceConstants.FCE_FAMILY);

		return checkIncomeForAdultDepCare(incomesForFamily, validDepCareDeductions);
	}

	/**
	 * Method Name: checkEnteryLegalStatusPresent Method Description: This
	 * method is used to Check if an Entry Legal Status is present only if child
	 * is 18 and over.
	 */
	private void checkEnteryLegalStatusPresent(IncomeExpenditureDto incomeExpenditureDto, FcePerson child,
			List<ErrorDto> errorDtos) {
		/*
		 * Check if person/child age is 18 and above, if Yes, check if
		 * respective Legal Status is present, throw error accordingly
		 */
		FceApplicationDto fceApplicationDto = incomeExpenditureDto.getFceApplicationDto();
		if (DateUtils.getAge(child.getDtBirth()) >= MAX_ELIGIBLE_AGE) {
			StageDto stage = stageDao.getStageById(fceApplicationDto.getIdStage());
			Long idCase = stage.getIdCase();
			String cdStageType = stage.getCdStageType();
			if (ObjectUtils
					.isEmpty(personUtil.getLegalStatusDate(fceApplicationDto.getIdPerson(), idCase, cdStageType))) {
				if (CodesConstant.CSTGTYPE_CJPC.equalsIgnoreCase(cdStageType)
						|| CodesConstant.CSTGTYPE_CTYC.equalsIgnoreCase(cdStageType)) {
					addError(ServiceConstants.MSG_TYC_JPC_LEGAL_STATUS_FCA, errorDtos);
				} else {
					addError(ServiceConstants.MSG_SUB_REG_LEGAL_STATUS_FCA, errorDtos);
				}
			}
		}
	}

	/**
	 * Method Name: checkAddrsOfRmvlReq Method Description: This method is used
	 * to Check address of removal request.
	 */

	private void checkAddrsOfRmvlReq(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		FceApplicationDto fceAppDto = incomeExpenditureDto.getFceApplicationDto();
		if (!(StringUtil.isValid(fceAppDto.getAddrRemovalStLn1()) && StringUtil.isValid(fceAppDto.getAddrRemovalCity())
				&& StringUtil.isValid(fceAppDto.getAddrRemovalAddrZip())
				&& StringUtil.isValid(fceAppDto.getCdRemovalAddrCounty())
				&& StringUtil.isValid(fceAppDto.getCdRemovalAddrState()))) {
			addError(ServiceConstants.MSG_ADDRS_OF_RMVL_REQ, errorDtos);

		}
	}

	/**
	 * Method Name: checkChldAtHomeReq Method Description: This method is used
	 * to Check child at home request.
	 */
	private void checkChldAtHomeReq(FcePerson child, List<ErrorDto> errorDtos) {
		if (ObjectUtils.isEmpty(child.getIndPersonHmRemoval())
				|| ServiceConstants.N.equals(child.getIndPersonHmRemoval())) {
			addError(ServiceConstants.MSG_CHLD_AT_HOME_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkChldInCertGroup Method Description: This method is used
	 * to Check child certificate group
	 */
	private void checkChldInCertGroup(FcePerson child, List<ErrorDto> errorDtos) {
		if (StringUtils.isBlank(child.getIndCertifiedGroup())
				|| ServiceConstants.N.equals(child.getIndCertifiedGroup())) {
			addError(ServiceConstants.MSG_CHLD_IN_CERT_GROUP_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkManagConserQuestions Method Description: This method is
	 * used to Check qestion details.
	 */

	private void checkManagConserQuestions(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		if (!(StringUtil.isValid(fceApplicationDto.getIndMinorParent())
				&& StringUtil.isValid(fceApplicationDto.getIndHospital())
				&& StringUtil.isValid(fceApplicationDto.getIndManagingCvs()))) {
			addError(ServiceConstants.MSG_MANAG_CONSER_QUESIONS, errorDtos);
		}
	}

	/**
	 * Method Name: checkDobRequired Method Description: This method is used to
	 * Check if DOB requires.
	 */

	private void checkDobRequired(FcePerson child, List<ErrorDto> errorDtos) {
		if (ObjectUtils.isEmpty(child.getDtBirth())) // should we check for the
														// null date? legacy
														// also has same quetion

		{
			addError(ServiceConstants.MSG_DOB_REQUIRED, errorDtos);
		}
	}

	/**
	 * Method Name: checkVerifDobReq Method Description: This method is used to
	 * verified DOB details
	 */

	private void checkVerifDobReq(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		if (!(StringUtils.isNotBlank(fceApplicationDto.getIndAgeJustifiedEval())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdBaptismCert())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdDhs())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdForeignCert())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdHospitalCert())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdNtrlztnCert())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdPassport())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdResidentCard())
				|| StringUtils.isNotBlank(fceApplicationDto.getIndAgeVrfdUsBirthCert()))) {
			addError(ServiceConstants.MSG_VERIF_DOB_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkCitizenRequired Method Description: This method is used
	 * to Check if citizenship missing.
	 */
	private void checkCitizenRequired(FceEligibilityDto fceEligibilityDto, List<ErrorDto> errorDtos) {
		if (!StringUtil.isValid(fceEligibilityDto.getCdPersonCitizenship())) {
			addError(ServiceConstants.MSG_CITIZEN_REQUIRED, errorDtos);
		}
	}

	/**
	 * Method Name: checkVerifCitizenReq Method Description: This method is used
	 * to Check citizenship details.
	 */

	private void checkVerifCitizenReq(FceEligibilityDto fceEligibilityDto, List<ErrorDto> errorDtos) {
		if (!(StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpAttorneyReview())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpBaptismalCrtfct())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpBirthCrtfctFor())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpBirthCrtfctUs())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpChldFound())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpCitizenCrtfct())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpDhsOther())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpDhsUs())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpEvaluation())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpForDocumentation())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpHospitalCrtfct())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpNoDocumentation())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpNtrlztnCrtfct())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpPassport())
				|| StringUtils.isNotBlank(fceEligibilityDto.getIndCtznshpResidentCard()))) {
			addError(ServiceConstants.MSG_VERIF_CITIZEN_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkEvalCnclRequired Method Description: This method is
	 * used to Check if conclusion required.
	 */

	private void checkEvalCnclRequired(FceApplicationDto fceApplicationDto, FceEligibilityDto fceEligibilityDto,
			List<ErrorDto> errorDtos) {
		if ((StringUtils.isNotBlank(fceApplicationDto.getIndAgeJustifiedEval())
				|| (!ObjectUtils.isEmpty(fceEligibilityDto.getIndCtznshpEvaluation())))
						&& StringUtils.isBlank(fceApplicationDto.getIndEvaluationConclusion())) {
			addError(ServiceConstants.MSG_EVAL_CNCL_REQUIRED, errorDtos);
		}
	}

	/**
	 * Method Name: checkEvalApprovalReq Method Description: This method is used
	 * to Check eval approval req.
	 */

	private void checkEvalApprovalReq(FceApplicationDto fceApplicationDto, FceEligibilityDto fceEligibilityDto,
			List<ErrorDto> errorDtos) {
		// Base this message just on what the user has checked, not on
		// whether a narrative exists or not. Narrative can remain and
		// eligibility
		// calculated if user changes verification methods to non-evaluative.
		/*if (StringUtils.isNotBlank(fceApplicationDto.getIndAgeJustifiedEval())
				|| (!ObjectUtils.isEmpty(fceEligibilityDto.getIndCtznshpEvaluation()))
						&& (!ObjectUtils.isEmpty(fceEligibilityDto.getIndNarrativeApproved()))) {*/
		if ((ServiceConstants.Y.equalsIgnoreCase(fceApplicationDto.getIndAgeJustifiedEval())
				|| ServiceConstants.Y.equalsIgnoreCase(fceEligibilityDto.getIndCtznshpEvaluation()))
						&& (!ServiceConstants.Y.equalsIgnoreCase(fceEligibilityDto.getIndNarrativeApproved()))) {
			addError(ServiceConstants.MSG_EVAL_APPROVAL_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkSpcfyLvgArrgmnt Method Description: This method is used
	 * to Check specified living arrangment.
	 */

	private void checkSpcfyLvgArrgmnt(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		if (!StringUtil.isValid(fceApplicationDto.getCdLivingMonthRemoval())) {
			addError(ServiceConstants.MSG_SPCFY_LVG_ARRGMNT, errorDtos);
		}
	}

	/**
	 * Method Name: checkHealthInsRequired Method Description: This method is
	 * used to Check if health info required.
	 */

	private void checkHealthInsRequired(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		if (StringUtils.isBlank(fceApplicationDto.getIndOtherHealthInsurance())) {
			addError(ServiceConstants.MSG_HEALTH_INS_REQUIRED, errorDtos);
		}
	}

	/**
	 * Method Name: checkNotLivingAtHome Method Description: This method is used
	 * to Check if family not living at home.
	 */

	private void checkNotLivingAtHome(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		// This check is only relevant if the child is marked as living with
		// "Other Relative," so
		// only run the check logic
		// if that is true.
		FceApplicationDto fceApplicationDto = incomeExpenditureDto.getFceApplicationDto();
		List<FcePersonDto> principles = incomeExpenditureDto.getPrinciples();
		if (CodesConstant.CFCELIV_R.equals(fceApplicationDto.getCdLivingMonthRemoval())) {
			Long idOtherRelativePersonObject = fceApplicationDto.getIdOtherRelativePerson();
			// Default to setting the error; only clear it if the relative with
			// whom the child is
			// living is marked as living
			// in the home of removal.
			boolean notLivingAtHome = true;
			// This should never happen, but just in case, do the null check; if
			// it is null, the
			// error gets set.
			if (idOtherRelativePersonObject != null) {
				// Get the long primitive value for the idOtherRelativePerson
				long idOtherRelativePerson = fceApplicationDto.getIdOtherRelativePerson().longValue();
				// Loop through the principles to find the principle with this
				// idFcePerson
				for (FcePersonDto fcePersonDto : principles) {

					// if the person who is selected as the relative with
					// whom the child was
					// living is in the home of
					// removal, then there is no error; otherwise, there is.
					if ((idOtherRelativePerson == fcePersonDto.getIdFcePerson())
							&& StringUtils.isNotBlank(fcePersonDto.getIndPersonHmRemoval())
							&& ServiceConstants.Y.equals(fcePersonDto.getIndPersonHmRemoval())) {
						notLivingAtHome = false;
						break;
					}
				}
			}
			if (notLivingAtHome) {
				addError(ServiceConstants.MSG_NOT_LIVING_AT_HOME, errorDtos);
			}
		}
	}

	/**
	 * Method Name: checkNumParentsLivingAtHome Method Description: This method
	 * is used to Check number of parents living at home.
	 */

	private void checkNumParentsLivingAtHome(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		int parentCount = 0;
		List<FcePersonDto> principles = incomeExpenditureDto.getPrinciples();
		FceApplicationDto fceApplicationDto = incomeExpenditureDto.getFceApplicationDto();
		for (FcePersonDto fcePersonDto : principles) {
			if (PersonUtil.isParent(fcePersonDto.getCdRelInt())) {
				if (StringUtils.isNotBlank(fcePersonDto.getIndPersonHmRemoval())
						&& ServiceConstants.Y.equals(fcePersonDto.getIndPersonHmRemoval())) {
					parentCount++;
				}
			}
		}
		// 19949, removed check on Other Relative, None of the Above,
		// which said the child could not be marked as living in the home of
		// removal
		// with any parents.
		if (((CodesConstant.CFCELIV_O.equals(fceApplicationDto.getCdLivingMonthRemoval())) && (parentCount < 1)) ||

				((CodesConstant.CFCELIV_B.equals(fceApplicationDto.getCdLivingMonthRemoval())) && (parentCount < 2))) {
			addError(ServiceConstants.MSG_LIV_ARR_NOT_MATCH_LIVING_AT_HOME, errorDtos);
		}

		// On the Domicile and Deprivation page of the Foster Care Assistance
		// Application,you can choose a Living Arrangement of Living with One
		// Legal or Biological parent when you have
		// two person with a Rel/Int of Parent (Birth) checked on the Persons
		// Living in Home at time of Removal. When you click the Submit
		// Application button, you will not get a message that
		// your chosen Living Arrangment is inconsistent with the Persons in the
		// Home.
		if ((CodesConstant.CFCELIV6_O.equals(fceApplicationDto.getCdLivingMonthRemoval())) && (parentCount > 1)) {
			addError(ServiceConstants.MSG_LIV_ARR_NOT_MATCH_LIVING_AT_HOME, errorDtos);
		}
	}

	/**
	 * Method Name: checkDdConfirmation Method Description: This method is used
	 * to Check Dd confirmation.
	 */

	private void checkDdConfirmation(FceEligibilityDto fceEligibilityDto, List<ErrorDto> errorDtos) {
		if (!StringUtil.isValid(fceEligibilityDto.getIndMeetsDpOrNotEs())) {
			addError(ServiceConstants.MSG_DD_CONFIRMATION, errorDtos);
		}
	}

	/**
	 * Method Name: checkIncomeAssistQuestionReq Method Description: This method
	 * is used to Check child certificate group
	 */
	private void checkIncomeAssistQuestionReq(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		if (!(StringUtil.isValid(fceApplicationDto.getIndIncomeAssistance())
				&& StringUtil.isValid(fceApplicationDto.getIndNotifiedDhsWorker()))) {
			addError(ServiceConstants.MSG_INCOME_ASSIST_QUESTION_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkFamilyNoIncm Method Description: This method is used to
	 * Check family no income request.
	 */

	private void checkFamilyNoIncm(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		// mdm 5/15/2003:
		// These checks used to verify person was in certified group;
		// that does not matter according to design doc

		for (FceIncomeDto dto : incomeExpenditureDto.getIncomeForChild()) {
			if ((dto.getAmtIncome() == 0)
					&& (ObjectUtils.isEmpty(dto.getIndNone()) || ServiceConstants.N.equals(dto.getIndNone()))) {
				addError(ServiceConstants.MSG_FAMILY_NO_INCM, errorDtos);
				return;
			}

		}

		for (FceIncomeDto dto : incomeExpenditureDto.getIncomeForFamily()) {

			if ((dto.getAmtIncome() == 0)
					&& (ObjectUtils.isEmpty(dto.getIndNone()) || ServiceConstants.N.equals(dto.getIndNone()))) {
				addError(ServiceConstants.MSG_FAMILY_NO_INCM, errorDtos);
				return;
			}

		}
	}

	/**
	 * Method Name: checkCountableExemptIncome Method Description: This method
	 * is used to Check countable exempt details
	 */

	private void checkCountableExemptIncome(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		checkCountableExempt(incomeExpenditureDto.getIncomeForChild(), ServiceConstants.MSG_COUNTABLE_EXEMPT,
				errorDtos);

		checkCountableExempt(incomeExpenditureDto.getResourcesForChild(), ServiceConstants.MSG_COUNTABLE_EXEMPT_RSRC,
				errorDtos);

		checkCountableExempt(incomeExpenditureDto.getIncomeForFamily(), ServiceConstants.MSG_FAMILY_COUNTABLE_EXEMPT,
				errorDtos);

		checkCountableExempt(incomeExpenditureDto.getResourcesForFamily(),
				ServiceConstants.MSG_FAMILY_COUNTABLE_EXEMPT_RSRC, errorDtos);

		checkInaccessibleExempt(incomeExpenditureDto.getResourcesForChild(),
				ServiceConstants.MSG_MARK_INACCESSIBLE_RESOURCE_EXEMPT, errorDtos);

		checkInaccessibleExempt(incomeExpenditureDto.getResourcesForFamily(),
				ServiceConstants.MSG_MARK_INACCESSIBLE_RESOURCE_EXEMPT, errorDtos);

		checkEarnedUnearned(incomeExpenditureDto.getIncomeForChild(), ServiceConstants.MSG_EARNED_UNEARNED, errorDtos);

		checkEarnedUnearned(incomeExpenditureDto.getIncomeForFamily(), ServiceConstants.MSG_FAMILY_EARNED_UNEARNED,
				errorDtos);
	}

	/**
	 * Method Name: checkCountableExempt Method Description: This method is used
	 * to Check countable exempt details
	 */

	private void checkCountableExempt(List<FceIncomeDto> incomeDtos, int message, List<ErrorDto> errorDtos) {

		for (FceIncomeDto dto : incomeDtos) {

			double amountIncome = dto.getAmtIncome();

			if ((amountIncome > 0.0) && (ObjectUtils.isEmpty(dto.getIndCountable()))) {
				addError(message, errorDtos);
				return;
			}

		}

	}

	/**
	 * Method Name: checkInaccessibleExempt Method Description: This method is
	 * used to Check inaccessible exempt request.
	 */

	private void checkInaccessibleExempt(List<FceIncomeDto> incomeDtos, int message, List<ErrorDto> errorDtos) {

		for (FceIncomeDto fceIncomeDto : incomeDtos) {

			if ((ServiceConstants.Y.equals(fceIncomeDto.getIndNotAccessible()))
					&& ((StringUtils.isBlank(fceIncomeDto.getIndCountable()))
							|| ServiceConstants.Y.equals(fceIncomeDto.getIndCountable()))) {
				addError(message, errorDtos);
				return;
			}

		}
	}

	/**
	 * Method Name: checkEarnedUnearned Method Description: This method is used
	 * to Check earned unearned details
	 */

	private void checkEarnedUnearned(List<FceIncomeDto> incomeDtos, int message, List<ErrorDto> errorDtos) {

		for (FceIncomeDto fceIncomeDto : incomeDtos) {
			double amountIncome = fceIncomeDto.getAmtIncome();

			if ((amountIncome > 0.0) && (ObjectUtils.isEmpty(fceIncomeDto.getIndEarned()))) {
				addError(message, errorDtos);
				return;
			}

		}
	}

	/**
	 * Method Name: checkEquityInconsistent Method Description: This method is
	 * used to Check quity incosistent.
	 */

	private void checkEquityInconsistent(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		double total = 0;

		for (FceIncomeDto fceIncomeDto : incomeExpenditureDto.getResourcesForChild()) {

			if (ServiceConstants.Y.equals(fceIncomeDto.getIndCountable())) {
				total += fceIncomeDto.getAmtIncome();
			}
		}

		for (FceIncomeDto fceIncomeDto : incomeExpenditureDto.getResourcesForFamily()) {

			String cdRelInt = fceIncomeDto.getCdRelInt();

			if ((ServiceConstants.Y.equals(fceIncomeDto.getIndCertifiedGroup()))
					&& (ServiceConstants.Y.equals(fceIncomeDto.getIndCountable()))
					&& (!PersonUtil.isStepParent(cdRelInt))) {
				total += fceIncomeDto.getAmtIncome();
			}
		}
		boolean indEquity = (StringUtils.isNotBlank(incomeExpenditureDto.getFceEligibilityDto().getIndEquity())
				&& ServiceConstants.Y.equals(incomeExpenditureDto.getFceEligibilityDto().getIndEquity()));
		if ((total >= ServiceConstants.RESOURCE_LIMIT) != indEquity) {
			addError(ServiceConstants.MSG_EQUITY_INCONSISTENT, errorDtos);
		}
	}

	/**
	 * Method Name: checkNoCombinedIncomeReq Method Description: This method is
	 * used to Check no combined income request
	 */

	private void checkNoCombinedIncomeReq(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		if (!StringUtil.isValid(incomeExpenditureDto.getFceApplicationDto().getTxtNoIncomeExplanation())) {
			double total = 0.0;

			for (FceIncomeDto fceIncomeDto : incomeExpenditureDto.getIncomeForChild()) {
				total += fceIncomeDto.getAmtIncome();
			}

			for (FceIncomeDto fceIncomeDto : incomeExpenditureDto.getIncomeForFamily()) {
				total += fceIncomeDto.getAmtIncome();
			}
			if (0 == total
					&& StringUtils.isBlank(incomeExpenditureDto.getFceApplicationDto().getTxtNoIncomeExplanation())) {
				addError(ServiceConstants.MSG_NO_COMBINED_INCOME_REQ, errorDtos);
			}
		}
	}

	/**
	 * Method Name: checkIncmDtrmntnCmntReq Method Description: This method is
	 * used to Check income detail request.
	 */

	private void checkIncmDtrmntnCmntReq(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		if (!StringUtil.isValid(fceApplicationDto.getTxtIncomeDtrmntnComments())) {
			addError(ServiceConstants.MSG_INCM_DTRMNTN_CMNT_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkEquityQuesReq Method Description: This method is used
	 * to Check equity request.
	 */

	private void checkEquityQuesReq(FceEligibilityDto fceEligibilityDto, List<ErrorDto> errorDtos) {
		if (!StringUtil.isValid(fceEligibilityDto.getIndEquity())) {
			addError(ServiceConstants.MSG_EQUITY_QUES_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkStepparentChildrenReq Method Description: This method
	 * is used to Check step parent child request.
	 */

	private void checkStepparentChildrenReq(FceEligibilityDto fceEligibilityDto, List<ErrorDto> errorDtos) {
		if (ObjectUtils.isEmpty(fceEligibilityDto.getNbrStepparentChildren())) {
			addError(ServiceConstants.MSG_STEPPARENT_CHILDREN_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkAlimonyPaymentsReq Method Description: This method is
	 * used to Check money request.
	 */

	private void checkAlimonyPaymentsReq(FceEligibilityDto fceEligibilityDto, List<ErrorDto> errorDtos) {
		if (ObjectUtils.isEmpty(fceEligibilityDto.getAmtStepparentAlimony())) {
			addError(ServiceConstants.MSG_ALIMONY_PAYMENTS_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkOtherPaymentsReq Method Description: This method is
	 * used to Check other payment request
	 */

	private void checkOtherPaymentsReq(FceEligibilityDto fceEligibilityDto, List<ErrorDto> errorDtos) {
		if (ObjectUtils.isEmpty(fceEligibilityDto.getAmtStepparentOutsidePmnt())) {
			addError(ServiceConstants.MSG_OTHER_PAYMENTS_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkChildSupQuesReq Method Description: This method is used
	 * to Check child request.
	 */

	private void checkChildSupQuesReq(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		// todo: figure out when to use ChildSupportOrder and when to use
		// ChildSupportOrdered
		if (!StringUtil.isValid(fceApplicationDto.getIndChildSupportOrder())) {
			addError(ServiceConstants.MSG_CHILD_SUP_QUES_REQ, errorDtos);
		}
	}

	/**
	 * Method Name: checkDocChecklistRequired Method Description: This method is
	 * used to Check if doc checck list required.
	 */

	private void checkDocChecklistRequired(FceApplicationDto fceApplicationDto, List<ErrorDto> errorDtos) {
		if ((ObjectUtils.isEmpty(fceApplicationDto.getIndProofAgeSentEs()))
				|| (ObjectUtils.isEmpty(fceApplicationDto.getIndProofCitizenshipSentEs()))
				|| (ObjectUtils.isEmpty(fceApplicationDto.getIndLegalDocsSentEs()))) {
			addError(ServiceConstants.MSG_DOC_CHECKLIST_REQUIRED, errorDtos);
		}
	}

	/**
	 * Method Name: checkStepparentNotCertified Method Description: This method
	 * is used to Check if step parent are not certified.
	 */

	private void checkStepparentNotCertified(IncomeExpenditureDto incomeExpenditureDto, List<ErrorDto> errorDtos) {
		for (FcePersonDto fcePersonDto : incomeExpenditureDto.getPrinciples()) {
			String cdRelInt = fcePersonDto.getCdRelInt();
			boolean certGroup = StringUtils.isNotBlank(fcePersonDto.getIndCertifiedGroup())
					&& ServiceConstants.Y.equals(fcePersonDto.getIndCertifiedGroup());

			if (PersonUtil.isStepParent(cdRelInt) && certGroup) {
				addError(ServiceConstants.MSG_STPPRNT_NOT_CERTIFIED, errorDtos);
				break;
			}
		}
	}

	/**
	 * Method Name: checkIncomeForAdultDepCare Method Description: This method
	 * is used to check if every valid dependent care deduction adult has raened
	 * income.
	 */

	private ErrorDto checkIncomeForAdultDepCare(List<FceIncomeDto> incomesForFamily,
			List<FceDepCareDeductDto> validDepCareDeductions) {

		// SIR 1012211 if no deductions then no errors
		if (ObjectUtils.isEmpty(validDepCareDeductions))
			return null;

		HashSet<Long> familyWithEarnedIncome = new HashSet<>();

		if (!ObjectUtils.isEmpty(incomesForFamily)) {

			// create an hash set with person ids that have earned income
			for (FceIncomeDto fceIncomeDto : incomesForFamily) {

				double amountIncome = fceIncomeDto.getAmtIncome();

				// if income earned or unearned is selected then add
				// it for evaluation
				if ((amountIncome > 0.0) && (fceIncomeDto.getIndEarned().equalsIgnoreCase("Y"))) {
					familyWithEarnedIncome.add(fceIncomeDto.getIdFcePerson());
				}
			}
		}

		// check adults in dependent care deduction has earned income using
		// above hashset
		for (FceDepCareDeductDto fceDepCareDeductDto : validDepCareDeductions) {

			if (!familyWithEarnedIncome.contains(fceDepCareDeductDto.getIdFceAdultPerson())) {
				// if no income set error message and return
				ErrorDto error = new ErrorDto();
				error.setErrorCode(ServiceConstants.MSG_FCEA_ADULT_NO_EARNED_INCOME);
				return error;

			}
		}

		return null;
	}

	/**
	 * 
	 * Method Name: addError Method Description: helper method for adding the
	 * error to list
	 * 
	 * @param errorCode
	 * @param errorDtos
	 */
	private void addError(int errorCode, List<ErrorDto> errorDtos) {
		ErrorDto error = new ErrorDto();
		error.setErrorCode(errorCode);
		errorDtos.add(error);
	}

}
