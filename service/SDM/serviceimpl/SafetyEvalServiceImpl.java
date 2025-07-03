package us.tx.state.dfps.service.SDM.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafetyFactorEvalDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafteyEvalResDto;
import us.tx.state.dfps.service.SDM.dao.SafetyEvalDao;
import us.tx.state.dfps.service.SDM.service.SafetyEvalService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.OutputLaunchRtrvReq;
import us.tx.state.dfps.service.common.request.RiskAssessmentReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.OutputLaunchRtrvoRes;
import us.tx.state.dfps.service.common.response.RiskAssessmentRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.ChildSafetyEvalFormDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ChildSafetyEvaluationPrefill;
import us.tx.state.dfps.service.investigation.dto.RiskAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.placement.service.OutputLaunchRtrvService;
import us.tx.state.dfps.service.riskassesment.dto.RiskAssmtDtlDto;
import us.tx.state.dfps.service.riskassessment.dao.RiskAssessmentDao;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyAssessmentDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service for
 * the Child Safety Eval Form on Safety and Risk Assessment Page. May 9, 2018-
 * 10:30:27 AM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class SafetyEvalServiceImpl implements SafetyEvalService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	SafetyEvalDao safetyEvalDao;

	@Autowired
	private RiskAssessmentDao riskAssessmentDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	ChildSafetyEvaluationPrefill childSafetyEvaluationPrefill;

	@Autowired
	OutputLaunchRtrvService outputLaunchRtrvService;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getSafetyEvalDetails(CommonHelperReq commonHelperReq) {

		ChildSafetyEvalFormDto childSafetyEvalFormDto = new ChildSafetyEvalFormDto();

		// CallCSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(commonHelperReq.getIdStage());
		childSafetyEvalFormDto.setGenericCaseInfoDto(genericCaseInfoDto);

		// CallCINV01D
		List<SafetyFactorEvalDto> safetyFactorEvalDtoList = safetyEvalDao
				.getSafetyEvalFactors(commonHelperReq.getIdEvent());

		childSafetyEvalFormDto.setSafetyFactorEvalDtoList(safetyFactorEvalDtoList);

		// CallCINV00D
		SafteyEvalResDto safteyEvalResDto = safetyEvalDao.getSafetyEval(commonHelperReq.getIdEvent());

		childSafetyEvalFormDto.setSafteyEvalResDto(safteyEvalResDto);

		return childSafetyEvaluationPrefill.returnPrefillData(childSafetyEvalFormDto);
	}

	/**
	 * Method Name: queryRiskAssmt Method Description:Retrieve the Risk
	 * Assessment details and the data needed to build the Risk Assessment page.
	 *
	 * @param CommonHelperReq
	 * @return RiskAssessmentRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskAssessmentRes queryRiskAssmt(RiskAssessmentReq riskAssessmentReq) {
		RiskAssessmentRes riskAssessmentRes = new RiskAssessmentRes();

		List<RiskAssmtDtlDto> riskAssmtDtlDtoList = riskAssessmentDao.queryRiskAssmt(riskAssessmentReq.getIdStage(),
				riskAssessmentReq.getIdCase(), riskAssessmentReq.getIdEvent(), riskAssessmentReq.getNbrVersion());

		if (!ObjectUtils.isEmpty(riskAssmtDtlDtoList)) {
			RiskAssmtDtlDto riskAssmtDtlDto = riskAssmtDtlDtoList.get(ServiceConstants.Zero);
			// Response Values
			riskAssessmentRes.setAbuseNeglCircumstance(riskAssmtDtlDto.getTxtCircumstanceAbuseNgel());
			riskAssessmentRes.setAbuseNeglExtent(riskAssmtDtlDto.getTxtExtentAbuseNegl());
			riskAssessmentRes.setAbuseNeglHistEffect(riskAssmtDtlDto.getTxtAbuseNeglHstryEffect());
			riskAssessmentRes.setNeglectSummary(riskAssmtDtlDto.getTxtAbuseNeglSummary());
			riskAssessmentRes.setEventStatus(riskAssmtDtlDto.getCdEventStatus());
			riskAssessmentRes.setCaseId(riskAssmtDtlDto.getIdCase().intValue());
			riskAssessmentRes.setStageId(riskAssmtDtlDto.getIdStage().intValue());
			riskAssessmentRes.setEventId(riskAssmtDtlDto.getIdEvent().intValue());
			riskAssessmentRes.setPurpose(riskAssmtDtlDto.getCdRiskAssmtPurpose());
			riskAssessmentRes.setFinding(riskAssmtDtlDto.getCdRiskAssmtRiskFind());
			riskAssessmentRes.setLastUpdateDate(riskAssmtDtlDto.getDtAssmtLastUpdate());
			riskAssessmentRes.setLastUpdateEventDate(riskAssmtDtlDto.getDtEventLastUpdate());
			riskAssessmentRes.setVersionNBR(riskAssmtDtlDto.getNbrVersion().intValue());
			riskAssessmentRes.setNeglectSummary(riskAssmtDtlDto.getTxtAbuseNeglSummary());
			riskAssessmentRes.setIndAbuseNeglComp(riskAssmtDtlDto.getIndAbuseNeglSearchComp());
			riskAssessmentRes.setIndAbuseNeglFound(riskAssmtDtlDto.getIndAbuseNeglHistryFound());
			riskAssessmentRes.setFindingRationale(riskAssmtDtlDto.getTxtFindingRationale());
			riskAssessmentRes.setFactorsControlled(riskAssmtDtlDto.getTxtFactorsControlled());
			riskAssessmentRes.setIndAbuseNeglPrevINV(riskAssmtDtlDto.getIndAbuseNeglPrevInv());
			riskAssessmentRes.setRiskAssmtEventId(riskAssmtDtlDto.getIdEvent().intValue());
			riskAssessmentRes.setCriminalHistEffect(riskAssmtDtlDto.getTxtCriminalHstryEffect());
			riskAssessmentRes.setChildFunction(riskAssmtDtlDto.getTxtChildFunction());
			riskAssessmentRes.setParentDailyFunction(riskAssmtDtlDto.getTxtParentDailyFunction());
			riskAssessmentRes.setParentPractices(riskAssmtDtlDto.getTxtParentPractices());
			riskAssessmentRes.setParentDiscipline(riskAssmtDtlDto.getTxtParentDiscipline());
			riskAssessmentRes.setRiskAssmtEventId(riskAssmtDtlDto.getIdEvent().intValue());

			List<RiskAssessmentFactorDto> riskAssmntList = new ArrayList<RiskAssessmentFactorDto>();

			for (RiskAssmtDtlDto factor : riskAssmtDtlDtoList) {

				RiskAssessmentFactorDto riskAssessmentFactorDto = new RiskAssessmentFactorDto();
				riskAssessmentFactorDto.setCdArea(factor.getCdRiskArea());
				riskAssessmentFactorDto.setAreaDateLastUpdate(factor.getDtAreaLastUpdate());
				riskAssessmentFactorDto.setIdCase(factor.getIdCase().intValue());
				riskAssessmentFactorDto.setIdStage(factor.getIdStage().intValue());
				riskAssessmentFactorDto.setIdArea(factor.getIdRiskArea().intValue());
				riskAssessmentFactorDto.setAreaText(factor.getTxtArea());
				riskAssessmentFactorDto.setAreaScaleOfConcern(factor.getCdRiskAreaConcernScale());
				riskAssessmentFactorDto.setAreaTxtScaleConcern(factor.getTxtConcernScale());
				riskAssessmentFactorDto.setIdCategory(factor.getIdRiskCategory().intValue());
				riskAssessmentFactorDto.setCdCategory(factor.getCdRiskCateg());
				riskAssessmentFactorDto.setCategoryText(factor.getTxtCategory());
				riskAssessmentFactorDto.setCategoryScaleOfConcern(factor.getCdRiskCategConcernScale());
				riskAssessmentFactorDto.setDtCategoryLastUpdate(factor.getDtCategLastUpdate());
				riskAssessmentFactorDto.setCdFactor(factor.getCdRiskFactor());
				riskAssessmentFactorDto.setIdFactor(factor.getIdRiskFactor().intValue());
				riskAssessmentFactorDto.setFactorText(factor.getTxtFactor());
				riskAssessmentFactorDto.setFactorResponse(factor.getCdRiskFactorResponse());
				riskAssessmentFactorDto.setFactorComment(factor.getTxtRiskFactorComment());
				riskAssessmentFactorDto.setFactorDateLastUpdate(factor.getDtFactorLastUpdate());
				riskAssessmentFactorDto.setIdEvent(factor.getIdEvent().intValue());
				riskAssessmentFactorDto.setAreaSortOrderIndex(factor.getNbrArea().toString());
				riskAssessmentFactorDto.setCategorySortOrderIndex(factor.getNbrCategoryOrder().toString());
				riskAssessmentFactorDto.setFactorSortOrderIndex(factor.getNbrFactorOrder().toString());
				riskAssmntList.add(riskAssessmentFactorDto);
				riskAssessmentRes.setFactors(riskAssmntList);
			}
		}

		checkRiskAssmtForCompletion(riskAssessmentRes);

		Event invCnclsnEvent = eventDao.getEventByEventType(riskAssessmentReq.getIdCase(),
				riskAssessmentReq.getIdStage(), ServiceConstants.CONCLUSION_EVENT);

		if (!ObjectUtils.isEmpty(invCnclsnEvent)) {
			RiskAssessmentDto riskAssessmentDto = new RiskAssessmentDto();
			riskAssessmentDto.setEventStatus(invCnclsnEvent.getCdEventStatus());
			riskAssessmentDto.setIdCase(invCnclsnEvent.getIdCase().intValue());
			riskAssessmentDto.setEventDescription(invCnclsnEvent.getTxtEventDescr());
			riskAssessmentDto.setIdEvent(invCnclsnEvent.getIdEvent().intValue());
			riskAssessmentDto.setDtEventOccurred(invCnclsnEvent.getDtEventOccurred());
			riskAssessmentDto.setDtLastUpdate(invCnclsnEvent.getDtLastUpdate());
			riskAssessmentDto.setIdPerson(invCnclsnEvent.getPerson().getIdPerson().intValue());
			riskAssessmentDto.setIdStage(invCnclsnEvent.getStage().getIdStage().intValue());
			riskAssessmentRes.setInvestigationConclusionEvent(riskAssessmentDto);
		}

		OutputLaunchRtrvReq outputLaunchRtrvReq = new OutputLaunchRtrvReq();
		outputLaunchRtrvReq.setIdEvent(riskAssessmentReq.getIdEvent());
		outputLaunchRtrvReq.setIdStage(riskAssessmentReq.getIdStage());
		outputLaunchRtrvReq.setCdTask(ServiceConstants.RISK_ASSESSMENT_TASK_CODE);
		outputLaunchRtrvReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_SEARCH);
		OutputLaunchRtrvoRes outputLaunchRtrvoRes = outputLaunchRtrvService
				.callOutputLaunchRtrvService(outputLaunchRtrvReq);
		if (!ObjectUtils.isEmpty(outputLaunchRtrvoRes)) {
			riskAssessmentRes.setStructuredNarr(
					!ObjectUtils.isEmpty(outputLaunchRtrvoRes.getOutputLaunchRtrvoDto().getDtEventLastUpdate())
							? ServiceConstants.TRUEVAL : ServiceConstants.FALSEVAL);
		}

		return riskAssessmentRes;

	}

	public void checkRiskAssmtForCompletion(RiskAssessmentRes riskAssmtBeanToCheck) {

		RiskAssessmentFactorDto currentRiskFactor = null;
		String areaCodeBeingChecked = null;
		Boolean currentAreaIsComplete = Boolean.TRUE;
		Map<String, Boolean> currentAreaHashtable = new HashMap<String, Boolean>();
		boolean riskAssmtIsComplete = true;
		// SIR 25105- completion check flag for abuse neglect history section
		boolean abuseNeglectHistIsComplete = true;
		// SIR 25105- completion check flag for conclusion section
		boolean conclusionIsComplete = true;
		// Butts SIR 24696- Create a flag to be used later to check if the text
		// area under Abuse neglect history
		// were filled or not and set status based on it.
		boolean riskAssmtTextAreasFilled = true;
		// SIR 1002876 - add completion check for criminal history and knowing
		// the family sections.
		boolean criminalHistIsComplete = true;
		boolean knowingTheFamilyIsComplete = true;

		// SIR 25607 - Although purpose is never null, Just as a safe practice,
		// checking if somehow it does get null
		// set all the three main sections of a risk assessment as incomplete
		// until purpose is not null.

		if ((riskAssmtBeanToCheck.getPurpose() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getPurpose()))) {
			abuseNeglectHistIsComplete = false;
			riskAssmtIsComplete = false;
			conclusionIsComplete = false;
		}

		// SIR 25105- Added condition to cater for RA finding =null.
		if ((riskAssmtBeanToCheck.getFinding() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getFinding())))

		{ // SIR 25105 - Checking if abuse neglect history section is complete.
			abuseNeglectHistIsComplete = false;
			riskAssmtIsComplete = false;
			conclusionIsComplete = false;
		} else if (CodesConstant.CCRSKFND_01.equals(riskAssmtBeanToCheck.getFinding())
				|| CodesConstant.CCRSKFND_02.equals(riskAssmtBeanToCheck.getFinding())
				|| CodesConstant.CCRSKFND_03.equals(riskAssmtBeanToCheck.getFinding())) {
			// SIR 25129
			// if risk finding is "Risk Indicated", "No Sign factors" and
			// "Factors Controlled"
			// abuse history is required. If question 3 is Yes, then both text
			// boxes are required
			if ((riskAssmtBeanToCheck.getIndAbuseNeglFound() == null)
					|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getIndAbuseNeglFound()))
					|| (riskAssmtBeanToCheck.getIndAbuseNeglPrevINV() == null)
					|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getIndAbuseNeglPrevINV()))) {
				abuseNeglectHistIsComplete = false;
			}

			if (ServiceConstants.Y.equalsIgnoreCase(riskAssmtBeanToCheck.getIndAbuseNeglPrevINV())) {
				if ((riskAssmtBeanToCheck.getNeglectSummary() == null)
						|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getNeglectSummary()))
						|| (riskAssmtBeanToCheck.getAbuseNeglHistEffect() == null)
						|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getAbuseNeglHistEffect()))) {
					abuseNeglectHistIsComplete = false;
				}
			}
		}
		// SIR 25129 - If risk finding is N/A then only required first text box
		// under abuse neglect history
		// section if first question is selected to Y.
		// SIR 1026202 - Set Assessment Completion requirements for Risk Finding
		// "Completed External Risk Assessment" same as Risk Assessment NA.
		else if (CodesConstant.CCRSKFND_04.equals(riskAssmtBeanToCheck.getFinding())
				|| CodesConstant.CCRSKFND_05.equals(riskAssmtBeanToCheck.getFinding())) {
			// SIR 25129
			// if risk finding is "Risk N/A"
			// abuse history is required. If question 3 is Yes, then both text
			// boxes are required
			if ((riskAssmtBeanToCheck.getIndAbuseNeglFound() == null)
					|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getIndAbuseNeglFound()))
					|| (riskAssmtBeanToCheck.getIndAbuseNeglPrevINV() == null)
					|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getIndAbuseNeglPrevINV()))) {
				abuseNeglectHistIsComplete = false;
			}

			if (ServiceConstants.Y.equalsIgnoreCase(riskAssmtBeanToCheck.getIndAbuseNeglPrevINV())) {
				if ((riskAssmtBeanToCheck.getNeglectSummary() == null)
						|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getNeglectSummary()))) {
					abuseNeglectHistIsComplete = false;
				}
			}
		}

		// Iterate through the factor beans in the order they appear in the
		// array.
		// The factors were grouped by Area and Category when retrieved from the
		// database, so just iterate through the list and check the properties
		// of
		// the factor beans until the Area changes. Then start over for each new
		// Area.
		List<RiskAssessmentFactorDto> riskAssessmentFactorDtoList = riskAssmtBeanToCheck.getFactors();

		int factorSize = riskAssessmentFactorDtoList.size();

		if (!ObjectUtils.isEmpty(riskAssessmentFactorDtoList)) {
			for (RiskAssessmentFactorDto riskAssessmentFactorDto : riskAssessmentFactorDtoList) {
				int Iterator = 0;

				currentRiskFactor = riskAssessmentFactorDto;

				// Initialize the current Area variable if this is the first
				// iteration.
				if (areaCodeBeingChecked == null) {
					areaCodeBeingChecked = currentRiskFactor.getAreaText();
				}

				// If the Area of the current factor bean is the same as the
				// Area that we
				// are currently checking for completion, then check that the
				// Area Scale
				// of Concern, Category Scale of Concern, and Factor each have a
				// response.
				// If any of these properties is null or empty, then the Area is
				// incomplete.
				// Also, the overall Risk Assessment is incomplete.
				if (currentRiskFactor.getAreaText().equals(areaCodeBeingChecked)) {
					// SIR 24696 since we dont update category of concern for
					// new version so
					// for completion check for version 2 removed check for
					// CategoryScaleOfConcern

					if (riskAssmtBeanToCheck.getVersionNBR() == 2) {
						// SIR 25129 - Checking the areas for Risk Finding "Risk
						// Indicated", "Factors Controlled"
						// and "No significant factors".
						if (CodesConstant.CCRSKFND_01.equals(riskAssmtBeanToCheck.getFinding())
								|| CodesConstant.CCRSKFND_02.equals(riskAssmtBeanToCheck.getFinding())
								|| CodesConstant.CCRSKFND_03.equals(riskAssmtBeanToCheck.getFinding())) {
							if (((currentRiskFactor.getFactorResponse() == null) || (ServiceConstants.NULL_STRING
									.equalsIgnoreCase(currentRiskFactor.getFactorResponse())))) {
								currentAreaIsComplete = Boolean.FALSE;
								riskAssmtIsComplete = false;
							}

							// if scale of concern is selected
							if (!((currentRiskFactor.getAreaScaleOfConcern() == null)
									|| ("".equals(currentRiskFactor.getAreaScaleOfConcern())))) {
								// SIR 25129
								// if scale of concern is Somewhat, Considerable
								// and Extreme
								// ensure that the text boxes are filled
								if (CodesConstant.CRISKSOC_3.equalsIgnoreCase(currentRiskFactor.getAreaScaleOfConcern())
										|| CodesConstant.CRISKSOC_4
												.equalsIgnoreCase(currentRiskFactor.getAreaScaleOfConcern())
										|| CodesConstant.CRISKSOC_5
												.equalsIgnoreCase(currentRiskFactor.getAreaScaleOfConcern())) {
									if ((currentRiskFactor.getAreaTxtScaleConcern() == null)
											|| (ServiceConstants.NULL_STRING
													.equals(currentRiskFactor.getAreaTxtScaleConcern()))) {

										/*
										 * SIR 25803 - Added Check if scale of
										 * concern is Somewhat, Considerable and
										 * Extreme and the text area is filled
										 * as part of the risk area completion
										 * check, before scale of concern
										 * section was checked separately from
										 * the area completion.
										 */

										currentAreaIsComplete = Boolean.FALSE;
										riskAssmtIsComplete = false;

									}
								}
							} else {
								// if risk finding is "Risk Indicated", "No Sign
								// factors" and "Factors Controlled"
								// and no scale of concern specified then it is
								// an error
								currentAreaIsComplete = Boolean.FALSE;
								riskAssmtIsComplete = false;
							}

						}
						// If this is the very last factor bean to be checked,
						// then we are
						// finished. Add the completion check information to the
						// hashtable.
						// THE WHILE LOOP WILL END NOW.
						if (Iterator == factorSize) {
							currentAreaHashtable.put(areaCodeBeingChecked, currentAreaIsComplete);

						}
					} else if (riskAssmtBeanToCheck.getVersionNBR() != 2) {
						if (currentRiskFactor.getAreaScaleOfConcern() == null
								|| (ServiceConstants.NULL_STRING).equals(currentRiskFactor.getAreaScaleOfConcern())
								|| currentRiskFactor.getCategoryScaleOfConcern() == null
								|| (ServiceConstants.NULL_STRING).equals(currentRiskFactor.getCategoryScaleOfConcern())
								|| currentRiskFactor.getFactorResponse() == null
								|| (ServiceConstants.NULL_STRING).equals(currentRiskFactor.getFactorResponse())) {
							// SIR 25803
							currentAreaIsComplete = Boolean.FALSE;
							riskAssmtIsComplete = false;

							// If the Risk Finding is "Risk Assesssment N/A"
							// (Code "04"), then
							// the Risk Assessment is still considered complete.
							// SIR 1026202 - Set Assessment Completion
							// requirements for Risk Finding "Completed External
							// Risk Assessment" same as Risk Assessment NA.
							if (riskAssmtBeanToCheck.getFinding() != null && (CodesConstant.CCRSKFND_04
									.equalsIgnoreCase(riskAssmtBeanToCheck.getFinding())
									|| CodesConstant.CCRSKFND_05.equalsIgnoreCase(riskAssmtBeanToCheck.getFinding()))) {
								currentAreaIsComplete = Boolean.TRUE;
								riskAssmtIsComplete = true;
							}
						}

						// If this is the very last factor bean to be checked,
						// then we are
						// finished. Add the completion check information to the
						// hashtable.
						// THE WHILE LOOP WILL END NOW.
						if (Iterator == factorSize) {
							currentAreaHashtable.put(areaCodeBeingChecked, currentAreaIsComplete);

						}
					} // end else if for version 1

				}
				// Since the Area of the current factor bean is different from
				// the Area
				// that we are currently checking, then we are finished checking
				// the Area.
				// Add the completion check information to the hashtable, and
				// reset
				// the completion check variables. Then check the current factor
				// bean
				// for completion and set the "is complete" variable accordingly
				// for the
				// new Area.
				// SIR 25187 - If it's version 2 check if text area scale of
				// concern is empty and "somewhat", "considerable"
				// or extreme are selected.
				else {
					if (riskAssmtBeanToCheck.getVersionNBR() == 2) {
						currentAreaHashtable.put(areaCodeBeingChecked, currentAreaIsComplete);
						currentAreaIsComplete = Boolean.TRUE;
						areaCodeBeingChecked = currentRiskFactor.getAreaText();
						if ((currentRiskFactor.getAreaScaleOfConcern() == null
								|| (ServiceConstants.NULL_STRING).equals(currentRiskFactor.getAreaScaleOfConcern()))
								|| (currentRiskFactor.getFactorResponse() == null || (ServiceConstants.NULL_STRING)
										.equals(currentRiskFactor.getFactorResponse()))) {
							currentAreaIsComplete = Boolean.FALSE;
							riskAssmtIsComplete = false;

							// If the Risk Finding is "Risk Assesssment N/A"
							// (Code "04"), then
							// the Risk Assessment is still considered complete.
							// SIR 1026202 - Set Assessment Completion
							// requirements for Risk Finding "Completed External
							// Risk Assessment" same as Risk Assessment NA.
							if ((riskAssmtBeanToCheck.getFinding() != null && (CodesConstant.CCRSKFND_04
									.equalsIgnoreCase(riskAssmtBeanToCheck.getFinding())
									|| CodesConstant.CCRSKFND_05.equalsIgnoreCase(riskAssmtBeanToCheck.getFinding())))
									&& (riskAssmtTextAreasFilled)) {
								currentAreaIsComplete = Boolean.TRUE;
								riskAssmtIsComplete = true;
							}
						}
						if ((currentRiskFactor.getAreaTxtScaleConcern() == null
								|| currentRiskFactor.getAreaTxtScaleConcern().equals(ServiceConstants.NULL_STRING))
								&& (((CodesConstant.CRISKSOC_3)
										.equalsIgnoreCase(currentRiskFactor.getAreaScaleOfConcern()))
										|| ((CodesConstant.CRISKSOC_4)
												.equalsIgnoreCase(currentRiskFactor.getAreaScaleOfConcern()))
										|| ((CodesConstant.CRISKSOC_5)
												.equalsIgnoreCase(currentRiskFactor.getAreaScaleOfConcern())))) {
							// SIR 25607
							currentAreaIsComplete = Boolean.FALSE;
							riskAssmtIsComplete = false;

						}
					} // end of if of version 2

					// If RA is not version 2 then simply check the area for
					// factor response, category scale of concern and area scale
					// of concern.
					else {
						currentAreaHashtable.put(areaCodeBeingChecked, currentAreaIsComplete);
						currentAreaIsComplete = Boolean.TRUE;
						areaCodeBeingChecked = currentRiskFactor.getAreaText();
						if (currentRiskFactor.getAreaScaleOfConcern() == null
								|| currentRiskFactor.getAreaScaleOfConcern().equals(ServiceConstants.NULL_STRING)
								|| currentRiskFactor.getCategoryScaleOfConcern() == null
								|| currentRiskFactor.getCategoryScaleOfConcern().equals(ServiceConstants.NULL_STRING)
								|| currentRiskFactor.getFactorResponse() == null
								|| currentRiskFactor.getFactorResponse().equals(ServiceConstants.NULL_STRING)) {
							currentAreaIsComplete = Boolean.FALSE;
							riskAssmtIsComplete = false;

							// If the Risk Finding is "Risk Assesssment N/A"
							// (Code "04"), then
							// the Risk Assessment is still considered complete.
							// SIR 1026202 - Set Assessment Completion
							// requirements for Risk Finding "Completed External
							// Risk Assessment" same as Risk Assessment NA.
							if (riskAssmtBeanToCheck.getFinding() != null
									&& (riskAssmtBeanToCheck.getFinding().equals(CodesConstant.CCRSKFND_04)
											|| riskAssmtBeanToCheck.getFinding().equals(CodesConstant.CCRSKFND_05))) {
								currentAreaIsComplete = Boolean.TRUE;
								riskAssmtIsComplete = true;
							}
						}
					} // end of else of if not version 2

				} // end else

				Iterator++;

			}
		}

		// SIR 25129 - Checking for the conclusion section for completion. If
		// risk finding is "No significant factors"
		// and first text box is empty set the conclusion section completion
		// flag to false.
		if ((((riskAssmtBeanToCheck.getFindingRationale() == null)
				|| ((ServiceConstants.NULL_STRING).equals(riskAssmtBeanToCheck.getFindingRationale())))
				&& ((CodesConstant.CCRSKFND_03).equals(riskAssmtBeanToCheck.getFinding())))) {
			conclusionIsComplete = false;
		}
		// SIR 25105 - for risk finding Risk Indicated(1) and Factors Controlled
		// (2)
		// if both the text boxes are mandatory
		else if (((riskAssmtBeanToCheck.getFindingRationale() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getFindingRationale()))
				|| (riskAssmtBeanToCheck.getFactorsControlled() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getFactorsControlled())))
				&& (CodesConstant.CCRSKFND_01.equals(riskAssmtBeanToCheck.getFinding())
						|| CodesConstant.CCRSKFND_02.equals(riskAssmtBeanToCheck.getFinding()))) {
			conclusionIsComplete = false;
		}

		// SIR 1002876
		if ((riskAssmtBeanToCheck.getCriminalHistEffect() == null)
				|| ((ServiceConstants.NULL_STRING).equals(riskAssmtBeanToCheck.getCriminalHistEffect()))) {
			criminalHistIsComplete = false;
		}

		if ((riskAssmtBeanToCheck.getAbuseNeglExtent() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getAbuseNeglExtent()))) {
			knowingTheFamilyIsComplete = false;
		}

		if ((riskAssmtBeanToCheck.getAbuseNeglCircumstance() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getAbuseNeglCircumstance()))) {
			knowingTheFamilyIsComplete = false;
		}
		if ((riskAssmtBeanToCheck.getChildFunction() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getChildFunction()))) {
			knowingTheFamilyIsComplete = false;
		}
		if ((riskAssmtBeanToCheck.getParentDailyFunction() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getParentDailyFunction()))) {
			knowingTheFamilyIsComplete = false;
		}
		if ((riskAssmtBeanToCheck.getParentPractices() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getParentPractices()))) {
			knowingTheFamilyIsComplete = false;
		}
		if ((riskAssmtBeanToCheck.getParentDiscipline() == null)
				|| (ServiceConstants.NULL_STRING.equals(riskAssmtBeanToCheck.getParentDiscipline()))) {
			knowingTheFamilyIsComplete = false;
		}

		riskAssmtBeanToCheck.setAreaCompletionStatus(currentAreaHashtable);
		// SIR 25105 - Setting flag for abuse neglect history section
		// completion.
		riskAssmtBeanToCheck.setAbuseNeglectHistComplete(abuseNeglectHistIsComplete);
		riskAssmtBeanToCheck.setComplete(riskAssmtIsComplete);
		// SIR 25105 - Setting flag for conclusion section completion.
		riskAssmtBeanToCheck.setConclusionComplete(conclusionIsComplete);
		// SIR 1002876
		riskAssmtBeanToCheck.setCriminalHistComplete(criminalHistIsComplete);
		riskAssmtBeanToCheck.setIsknowingTheFamilyComplete(knowingTheFamilyIsComplete);

	}

	/**
	 * Method Name: queryPageData Method Description:Query the data needed to
	 * create the Risk Assessment page.
	 *
	 * @param riskAssessmentReq
	 * @return RiskAssessmentRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskAssessmentRes queryPageData(RiskAssessmentReq riskAssessmentReq) {
		RiskAssessmentRes riskAssessmentRes = new RiskAssessmentRes();

		if (riskAssessmentReq.getNbrVersion().equals(ServiceConstants.ZERO)) {
			riskAssessmentReq.setNbrVersion((long) ServiceConstants.NBR_VERSION_2);
		}

		List<RiskAssmtDtlDto> riskAssmtDtlDtoList = riskAssessmentDao.queryPageData(riskAssessmentReq.getNbrVersion());

		if (!ObjectUtils.isEmpty(riskAssmtDtlDtoList)) {
			List<RiskAssessmentFactorDto> riskAssmntList = new ArrayList<RiskAssessmentFactorDto>();

			for (RiskAssmtDtlDto factor : riskAssmtDtlDtoList) {

				RiskAssessmentFactorDto riskAssessmentFactorDto = new RiskAssessmentFactorDto();
				riskAssessmentFactorDto.setCdArea(factor.getCdRiskArea());
				riskAssessmentFactorDto.setAreaText(factor.getTxtArea());
				riskAssessmentFactorDto.setAreaSortOrderIndex(factor.getNbrArea().toString());
				riskAssessmentFactorDto.setCdCategory(factor.getCdRiskCateg());
				riskAssessmentFactorDto.setCategoryText(factor.getTxtCategory());
				riskAssessmentFactorDto.setCategorySortOrderIndex(factor.getNbrCategoryOrder().toString());
				riskAssessmentFactorDto.setCdFactor(factor.getCdRiskFactor());
				riskAssessmentFactorDto.setFactorText(factor.getTxtFactor());
				riskAssessmentFactorDto.setFactorSortOrderIndex(factor.getNbrFactorOrder().toString());
				riskAssmntList.add(riskAssessmentFactorDto);
				riskAssessmentRes.setFactors(riskAssmntList);
			}
		}

		riskAssessmentRes.setCaseId(riskAssessmentReq.getIdCase().intValue());
		riskAssessmentRes.setStageId(riskAssessmentReq.getIdStage().intValue());
		riskAssessmentRes.setEventId(riskAssessmentReq.getIdEvent().intValue());

		return riskAssessmentRes;
	}

	/**
	 * Method Name: getCurrentEventId Method Description:Query the data needed
	 * to create the Risk Assessment page.
	 *
	 * @param idStage,idCase
	 * @return SafetyAssessmentRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SafetyAssessmentRes getCurrentEventId(Long idStage, Long idCase) {
		Long idEvent = safetyEvalDao.getCurrentEventId(idStage, idCase);
		SafetyAssessmentRes safetyAssessment = new SafetyAssessmentRes();
		safetyAssessment.setIdEvent(idEvent);
		return safetyAssessment;
	}

	/**
	 * Method Name: retrieveSafetyAssmtData Method Description:This method
	 * fetches the safety assessment data
	 *
	 * @param idStage,idEvent,idCase
	 * @return SafetyAssessmentRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SafetyAssessmentRes retrieveSafetyAssmtData(Long idStage, Long idEvent, Long idCase) {
		SafetyAssessmentDto safetyAssessmentDto = safetyEvalDao.retrieveSafetyAssessmentData(idStage, idEvent, idCase);
		SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
		safetyAssessmentRes.setSafetyAssessmentDto(safetyAssessmentDto);
		return safetyAssessmentRes;
	}

	/**
	 * Method Name: getCurrentEventStatus Method Description:This method fetches
	 * the current Event Status
	 * 
	 * @param idCase,idStage
	 * @return SafetyAssessmentRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SafetyAssessmentRes getCurrentEventStatus(Long idStage, Long idCase) {
		String eventStatus = safetyEvalDao.getCurrentEventStatus(idStage, idCase);
		SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
		safetyAssessmentRes.setEventStatus(eventStatus);
		return safetyAssessmentRes;
	}

	/**
	 * Method Name: getSubStageOpen Method Description:This method checks if the
	 * Sub Stage is Open or Close
	 *
	 * @param idCase
	 * @return SafetyAssessmentRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SafetyAssessmentRes getSubStageOpen(Long idCase) {
		boolean subStageOpen = safetyEvalDao.getSubStageOpen(idCase);
		SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
		safetyAssessmentRes.setSubStageOpen(subStageOpen);
		return safetyAssessmentRes;
	}

	/**
	 * Method Name: getQueryPgData Method Description:Retrieve the data needed
	 * to build the Safety Assessment page.
	 * 
	 * @param SafetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SafetyAssessmentRes getQueryPgData(SafetyAssessmentReq safetyAssessmentReq) {
		SafetyAssessmentRes safetyAssessmentRes = safetyEvalDao.getQueryPgData(safetyAssessmentReq);
		return safetyAssessmentRes;
	}
}
