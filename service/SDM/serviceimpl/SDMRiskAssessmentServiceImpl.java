package us.tx.state.dfps.service.SDM.serviceimpl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.SDM.dao.SDMRiskAssessmentDao;
import us.tx.state.dfps.service.SDM.service.SDMRiskAssessmentService;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.request.SDMRiskAssessmentReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.response.SDMRiskAssessmentRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentAnswerDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentQuestionDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssmtSecondaryFollowupDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.placement.service.OutputLaunchRtrvService;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * Service implementation for Risk Assessment Process
 *
 */
@Service
@Transactional
public class SDMRiskAssessmentServiceImpl implements SDMRiskAssessmentService {

	@Autowired
	SDMRiskAssessmentDao sDMRiskAssessmentDao;

	@Autowired
	EventService eventService;

	@Autowired
	PostEventService postEventService;

	@Autowired
	OutputLaunchRtrvService outputlaunchRtrvService;

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	ApprovalCommonService approvalService;
	
	@Autowired
	StageDao stageDao;
	
	@Autowired
	WorkLoadDao workLoadDao;
	
	@Autowired
	AlertService alertService;
	
	@Autowired
	PersonDao  personDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SDMRiskAssessmentServiceImpl.class);

	/**
	 * 
	 * @param SDMRiskAssessmentReq
	 * @return long updateResult @
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public long completeAssessment(SDMRiskAssessmentReq sDMRiskAssessmentReq) {

		SDMRiskAssessmentRes sdmRiskAssessmentRes = new SDMRiskAssessmentRes();

		sdmRiskAssessmentRes
				.setUpdateResult(sDMRiskAssessmentDao.updateEventStatus(sDMRiskAssessmentReq.getEventValueDto()));
		// artf156996 : DEV 64.8 Save and Complete the Assessment
		// is FPR open for this stage
		List<StageDto> stageDtoList = stageDao.getOpenStageByIdCase(sDMRiskAssessmentReq.getsDMRiskAssessmentdto().getCaseId());
		if(!ObjectUtils.isEmpty(stageDtoList)){
			Long stageIdFpr =   stageDtoList.stream().filter(stageDto->ServiceConstants.FAMILY_PRESERVATION_STG.equals(stageDto.getCdStage()))
					.findFirst()
					.map(StageDto::getIdStage)
					.orElse(null);
			SelectStageDto selectStageDtoCurrent = caseSummaryDao.getStage(sDMRiskAssessmentReq.getsDMRiskAssessmentdto().getStageId(), ServiceConstants.STAGE_CURRENT);
			if(!ObjectUtils.isEmpty(stageIdFpr) && Arrays.asList(ServiceConstants.STAGE_TYPE_INVESTIGATION,ServiceConstants.CSTAGES_AR).contains(selectStageDtoCurrent.getCdStage())){
				String longDesText = getLongDesText(sDMRiskAssessmentReq.getsDMRiskAssessmentdto());
				generateAlerts(stageIdFpr, sDMRiskAssessmentReq.getsDMRiskAssessmentdto().getIdUser(), sDMRiskAssessmentReq.getsDMRiskAssessmentdto().getCaseId(), longDesText);
			}
		}
		return sDMRiskAssessmentDao.addAssessmentCompletedDate(sDMRiskAssessmentReq.getsDMRiskAssessmentdto());
	}

	/**
	 * To save the SDMRiskAssessmentDB to the CPS_RA table
	 * 
	 * @param SDMRiskAssessmentDto
	 * @return SDMRiskAssessmentRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public SDMRiskAssessmentRes saveRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		long idEvent = 0l;
		SDMRiskAssessmentDto dto = new SDMRiskAssessmentDto();
		if (ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIdEvent()) || sDMRiskAssessmentdto.getIdEvent() == 0) {
			sDMRiskAssessmentdto.setIdEvent((long) (createAndReturneventId(sDMRiskAssessmentdto)));
			dto = sDMRiskAssessmentDao.addRiskAssessment(sDMRiskAssessmentdto);
		} else {
			// check and invalidate the pending approval if applicable.
			if (checkForInvalidateApproval(sDMRiskAssessmentdto)) {
				// if the pending approval is invalidated, change the current
				// risk event status to comp
				sDMRiskAssessmentdto.setEventStatus(ServiceConstants.COMPLETE_EVENT);
			}
			// Defect #14876 - Check the Event status if its complete
			if(ServiceConstants.COMPLETE_EVENT.equals(sDMRiskAssessmentdto.getEventStatus())
					&& (!isSection1Complete(sDMRiskAssessmentdto) || !isSection2Complete(sDMRiskAssessmentdto))){
				sDMRiskAssessmentdto.setEventStatus(ServiceConstants.PROCESS_EVENT_STATUS);
				// Artifact: artf158205 - Defect #15164 - Along with moving event to PROC, also blank out the dtAssessmentCompleted.
				sDMRiskAssessmentdto.setDateAssessmentCompleted(null);
			}
			// Update the Risk and score level.
			updateScoresAndRiskLevels(sDMRiskAssessmentdto);
			idEvent = sDMRiskAssessmentDao.updateRiskAsmnt(sDMRiskAssessmentdto);
			sDMRiskAssessmentdto.setIdEvent(idEvent);
			dto = sDMRiskAssessmentdto;
		}
		// update the primary and secondary careGiver indicator in the
		// stage_person_link table.
		if (!ObjectUtils.isEmpty(sDMRiskAssessmentdto.getIdSecondaryCaregiver())) {
			updatePrimarySecondaryCaretaker(sDMRiskAssessmentdto);
		}
		SDMRiskAssessmentRes res = new SDMRiskAssessmentRes();

		// dto.setIdEvent(idEvent);
		res.setsDMRiskAssessmentdto(dto);
		return res;

	}
	
	
	// artf156996 : DEV 64.8 Save and Complete the Assessment 
	private String getLongDesText(SDMRiskAssessmentDto sDMRiskAssessmentdto){
	     
		 StringBuilder longDesText = new StringBuilder();
	     
	     longDesText.append(ServiceConstants.SDM_RISK_ASSESSMENT_TEXT_DESC); 
	     longDesText.append(ServiceConstants.SDM_HOUSE_HOLD);
	     if(!ObjectUtils.isEmpty(sDMRiskAssessmentdto) && sDMRiskAssessmentdto.getIdHouseHoldPerson() != null){
		 	 PersonDto personDto = personDao.getPersonById(sDMRiskAssessmentdto.getIdHouseHoldPerson());
		     longDesText.append( personDto.getNmPersonFull());
	     }
	     longDesText.append(ServiceConstants.SDM_FINAL_RISK_LEVEL);
	     if(!ObjectUtils.isEmpty(sDMRiskAssessmentdto) && sDMRiskAssessmentdto.getFinalRiskLevelCode() != null){
		     longDesText.append(sDMRiskAssessmentdto.getFinalRiskLevelCode());
	     }
	     
	     return longDesText.toString();
	}


	// artf156996 : DEV 64.8 Save and Complete the Assessment 
	private void generateAlerts(Long stageId, Long userId, Long idCase, String longDesText){
		List<Long> caseWorkeFprPersonIdList  = workLoadDao.getAssignedWorkersForStage(stageId);
		caseWorkeFprPersonIdList.stream().forEach(caseWorkePersonId -> alertService.createFbssAlert(stageId,
				caseWorkePersonId, null, idCase, longDesText, ServiceConstants.SDM_RISK_ASSESSMENT_TEXT_DESC));
	}

	private boolean checkForInvalidateApproval(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
		boolean hasInvalidateApproval = false;
		if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto)
				&& !TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getApprovalEvent())
				&& !TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto.getApprovalEvent().getIdEvent())) {
			invalidateApproval(sDMRiskAssessmentdto);
			hasInvalidateApproval = true;
		}
		return hasInvalidateApproval;
	}

	private void invalidateApproval(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
		// Call the service to invalidate the pending approval.
		ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
		approvalCommonInDto.setIdEvent(sDMRiskAssessmentdto.getApprovalEvent().getIdEvent());
		approvalService.callCcmn05uService(approvalCommonInDto);		
	}

	/**
	 * To update primary and secondary caretaker indicator in stage_person_link
	 * table
	 * 
	 * @param sDMRiskAssessmentdto
	 * @return long Result
	 */
	private long updatePrimarySecondaryCaretaker(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		long Result = 0;

		List<StagePersonValueDto> stageValueBeanList = sDMRiskAssessmentdto.getPersonList();

		if (null != sDMRiskAssessmentdto.getPersonList() && sDMRiskAssessmentdto.getPersonList().size() > 0) {
			for (StagePersonValueDto stageValueBean : stageValueBeanList) {
				// if the person is selected as primary care giver, then sets
				// the
				// indicator as Y
				if (stageValueBean.getIdPerson().equals(sDMRiskAssessmentdto.getIdPrimaryCaregiver())) {
					stageValueBean.setIndPrimaryCaretaker(ServiceConstants.YES);
				}
				// else sets the indicator as N
				else {
					stageValueBean.setIndPrimaryCaretaker(ServiceConstants.NO);
				}
				// if the person is selected as secondary care giver, then sets
				// the indicator as Y
				if (stageValueBean.getIdPerson().equals(sDMRiskAssessmentdto.getIdSecondaryCaregiver())) {
					stageValueBean.setIndSecCaretaker(ServiceConstants.YES);
				}
				// else sets the indicator as N
				else {
					stageValueBean.setIndSecCaretaker(ServiceConstants.NO);
				}
				Result = sDMRiskAssessmentDao.updatePrimarySecondaryCaretaker(stageValueBean,
						sDMRiskAssessmentdto.getStageId());
			}

		}
		return Result;

	}

	/**
	 * Method to update the followup responses
	 * 
	 * @param sDMRiskAssessmentdto
	 * @param answerDB
	 * @return long Result
	 */
	private long updateFollowupResponses(SDMRiskAssessmentDto sDMRiskAssessmentdto,
			SDMRiskAssessmentAnswerDto answerDB) {
		long Result = 0;

		List<SDMRiskAssessmentFollowupDto> followupQuestions = answerDB.getFollowupQuestions();

		for (SDMRiskAssessmentFollowupDto followupDB : followupQuestions) {
			if (followupDB.isDirty()) {
				followupDB.setLoggedInUser(answerDB.getLoggedInUser());
				sDMRiskAssessmentDao.updateRiskFollowupResponse(followupDB);
			}
			if (ServiceConstants.ITEM_WITH_SECOND_FOLLOWUP
					.contains(Integer.valueOf(answerDB.getQuestionLookupId()).toString())) {

				List<SDMRiskAssmtSecondaryFollowupDto> secondaryFollowupQuestions = followupDB
						.getSecondaryFollowupQuestions();
				for (SDMRiskAssmtSecondaryFollowupDto secFollowupDB : secondaryFollowupQuestions) {
					if (secFollowupDB.isDirty()) {
						secFollowupDB.setLoggedInUser(answerDB.getLoggedInUser());
						Result = sDMRiskAssessmentDao.updateRiskSecondFollowupResponse(secFollowupDB);
					}

				}
			}
		}

		return Result;

	}

	/**
	 * update the scores and risk levels
	 * 
	 * @param sDMRiskAssessmentdto
	 * @return long result
	 */
	private long updateScoresAndRiskLevels(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		long Result = 0;

		String fnRiskLevel = CalculateFNScoreLvl(sDMRiskAssessmentdto);
		String faRiskLevel = CalculateFAScoreLvl(sDMRiskAssessmentdto);
		sDMRiskAssessmentdto.setFutureNeglectRiskLevelCode(fnRiskLevel);
		sDMRiskAssessmentdto.setFutureAbuseRiskLevelCode(faRiskLevel);
		sDMRiskAssessmentdto = CalculateScoredRiskLvl(sDMRiskAssessmentdto);

		String finalRiskLevel = CalculateFinalRiskLvl(sDMRiskAssessmentdto);
		sDMRiskAssessmentdto.setFinalRiskLevelCode(finalRiskLevel);

		if (!isSection1Complete(sDMRiskAssessmentdto)) {
			sDMRiskAssessmentdto.setFinalRiskLevelCode(null);
			sDMRiskAssessmentdto.setFutureAbuseRiskLevelCode(null);
			sDMRiskAssessmentdto.setFutureNeglectRiskLevelCode(null);
			sDMRiskAssessmentdto.setScoredRiskLevelCode(null);
			sDMRiskAssessmentdto.setOverrideCode(null);
			sDMRiskAssessmentdto.setOverrideReason(null);
		}

		Result = sDMRiskAssessmentDao.updateScoresAndRiskLevels(sDMRiskAssessmentdto);

		return Result;
	}

	/**
	 * to calculate the final risk level
	 * 
	 * @param SDMRiskAssessmentDto
	 * @return String finalRiskLevel
	 */
	private String CalculateFinalRiskLvl(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		String finalRiskLevel = new String();

		if (CodesConstant.CDOVRIDE_NOOVERRIDE.equalsIgnoreCase(sDMRiskAssessmentdto.getOverrideCode())) {
			finalRiskLevel = sDMRiskAssessmentdto.getScoredRiskLevelCode();
		} else if (CodesConstant.CDOVRIDE_POLICYOVERRIDE.equalsIgnoreCase(sDMRiskAssessmentdto.getOverrideCode())) {
			finalRiskLevel = CodesConstant.CSDMRLVL_VERYHIGH;
		} else if (CodesConstant.CDOVRIDE_DISCRETIONARYOVERRID
				.equalsIgnoreCase(sDMRiskAssessmentdto.getOverrideCode())) {
			if (CodesConstant.CSDMRLVL_LOW.equalsIgnoreCase(sDMRiskAssessmentdto.getScoredRiskLevelCode())) {
				finalRiskLevel = CodesConstant.CSDMRLVL_MOD;
			} else if (CodesConstant.CSDMRLVL_MOD.equalsIgnoreCase(sDMRiskAssessmentdto.getScoredRiskLevelCode())) {
				finalRiskLevel = CodesConstant.CSDMRLVL_HIGH;
			} else if (CodesConstant.CSDMRLVL_HIGH.equalsIgnoreCase(sDMRiskAssessmentdto.getScoredRiskLevelCode())) {
				finalRiskLevel = CodesConstant.CSDMRLVL_VERYHIGH;
			}
		}

		return finalRiskLevel;
	}

	/**
	 * Method to calculate scored risk level
	 * 
	 * @param SDMRiskAssessmentDto
	 * @return SDMRiskAssessmentDto
	 */
	private SDMRiskAssessmentDto CalculateScoredRiskLvl(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		String scoredRiskLevel = new String();

		if ((CodesConstant.CSDMRLVL_LOW).equalsIgnoreCase(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode())
				|| (CodesConstant.CSDMRLVL_LOW).equalsIgnoreCase(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode())) {
			scoredRiskLevel = CodesConstant.CSDMRLVL_LOW;
		}

		if ((CodesConstant.CSDMRLVL_MOD).equalsIgnoreCase(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode())
				|| (CodesConstant.CSDMRLVL_MOD).equalsIgnoreCase(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode())) {
			scoredRiskLevel = CodesConstant.CSDMRLVL_MOD;
		}

		if ((CodesConstant.CSDMRLVL_HIGH).equalsIgnoreCase(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode())
				|| (CodesConstant.CSDMRLVL_HIGH).equalsIgnoreCase(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode())) {
			scoredRiskLevel = CodesConstant.CSDMRLVL_HIGH;
		}

		if (CodesConstant.CSDMRLVL_VERYHIGH.equalsIgnoreCase(sDMRiskAssessmentdto.getScoredRiskLevelCode())) {
			sDMRiskAssessmentdto.setOverrideCode(null);
		}
		if ((CodesConstant.CSDMRLVL_VERYHIGH).equalsIgnoreCase(sDMRiskAssessmentdto.getFutureNeglectRiskLevelCode())
				|| (CodesConstant.CSDMRLVL_VERYHIGH)
						.equalsIgnoreCase(sDMRiskAssessmentdto.getFutureAbuseRiskLevelCode())) {
			scoredRiskLevel = CodesConstant.CSDMRLVL_VERYHIGH;
			sDMRiskAssessmentdto.setOverrideCode(CodesConstant.CDOVRIDE_NOOVERRIDE);
			sDMRiskAssessmentdto.setIndPOChildDeath(Boolean.valueOf(ServiceConstants.NO));
			sDMRiskAssessmentdto.setIndPOChildLessThanSixteenInjured(Boolean.valueOf(ServiceConstants.NO));
			sDMRiskAssessmentdto.setIndPOChildSexualAbuse(Boolean.valueOf(ServiceConstants.NO));
			sDMRiskAssessmentdto.setIndPOChildLessThanThreeInjured(Boolean.valueOf(ServiceConstants.NO));
			sDMRiskAssessmentdto.setOverrideReason(null);
		}
		sDMRiskAssessmentdto.setScoredRiskLevelCode(scoredRiskLevel);
		return sDMRiskAssessmentdto;
	}

	/**
	 * Method to calculate the final risk level
	 * 
	 * @param SDMRiskAssessmentDto
	 * @return String faRiskLevel
	 */
	private String CalculateFAScoreLvl(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		String faRiskLevel = new String();

		if ((sDMRiskAssessmentdto.getFutureAbuseScore() >= sDMRiskAssessmentdto.getFutureAbuseLowMin())
				&& (sDMRiskAssessmentdto.getFutureAbuseScore() <= sDMRiskAssessmentdto.getFutureAbuseLowMax())) {

			faRiskLevel = CodesConstant.CSDMRLVL_LOW;
		} else if ((sDMRiskAssessmentdto.getFutureAbuseScore() >= sDMRiskAssessmentdto.getFutureAbuseModerateMin())
				&& (sDMRiskAssessmentdto.getFutureAbuseScore() <= sDMRiskAssessmentdto.getFutureAbuseModerateMax())) {

			faRiskLevel = CodesConstant.CSDMRLVL_MOD;
		} else if ((sDMRiskAssessmentdto.getFutureAbuseScore() >= sDMRiskAssessmentdto.getFutureAbuseHighMin())
				&& (sDMRiskAssessmentdto.getFutureAbuseScore() <= sDMRiskAssessmentdto.getFutureAbuseHighMax())) {

			faRiskLevel = CodesConstant.CSDMRLVL_HIGH;
		} else if (sDMRiskAssessmentdto.getFutureAbuseScore() > sDMRiskAssessmentdto.getFutureAbuseHighMax()) {

			faRiskLevel = CodesConstant.CSDMRLVL_VERYHIGH;
		}
		return faRiskLevel;
	}

	/**
	 * Method to calculate the final risk level
	 * 
	 * @param SDMRiskAssessmentDto
	 * @return String fnRiskLevel
	 */
	private String CalculateFNScoreLvl(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		String fnRiskLevel = new String();

		if ((sDMRiskAssessmentdto.getFutureNeglectScore() >= sDMRiskAssessmentdto.getFutureNegelectLowMin())
				&& (sDMRiskAssessmentdto.getFutureNeglectScore() <= sDMRiskAssessmentdto.getFutureNegelectLowMax())) {
			fnRiskLevel = CodesConstant.CSDMRLVL_LOW;
		} else if ((sDMRiskAssessmentdto.getFutureNeglectScore() >= sDMRiskAssessmentdto.getFutureNegelectModerateMin())
				&& (sDMRiskAssessmentdto.getFutureNeglectScore() <= sDMRiskAssessmentdto
						.getFutureNegelectModerateMax())) {
			fnRiskLevel = CodesConstant.CSDMRLVL_MOD;
		} else if ((sDMRiskAssessmentdto.getFutureNeglectScore() >= sDMRiskAssessmentdto.getFutureNegelectHighMin())
				&& (sDMRiskAssessmentdto.getFutureNeglectScore() <= sDMRiskAssessmentdto.getFutureNegelectHighMax())) {
			fnRiskLevel = CodesConstant.CSDMRLVL_HIGH;
		} else if (sDMRiskAssessmentdto.getFutureNeglectScore() > sDMRiskAssessmentdto.getFutureNegelectHighMax()) {
			fnRiskLevel = CodesConstant.CSDMRLVL_VERYHIGH;
		}
		return fnRiskLevel;
	}

	/**
	 * method to check if Approval Request needs to be Invalidated
	 *
	 * @param SDMRiskAssessmentDto
	 *            SDM Risk Assessment DTO
	 * @return Returns true if invalidateApproval was run @
	 */
	/*
	 * private boolean checkForInvalidateApproval(SDMRiskAssessmentDto
	 * sDMRiskAssessmentdto) {
	 * 
	 * boolean hasInvalidatedApproval = false; if ((sDMRiskAssessmentdto !=
	 * null) && (sDMRiskAssessmentdto.getApprovalEvent() != null) &&
	 * (sDMRiskAssessmentdto.getApprovalEvent().getIdEvent() != 0)) {
	 * 
	 * Ccmn01uiDto ccmn01uiDto = new Ccmn01uiDto();
	 * 
	 * ccmn01uiDto.setUlIdEvent(sDMRiskAssessmentdto.getEventId());
	 * ccmn01uiDto.setUlIdPerson(sDMRiskAssessmentdto.getPersonList().get(0).
	 * idPerson); ccmn01uiDto.setUlIdStage(sDMRiskAssessmentdto.getStageId());
	 * ccmn01uiDto.setDtDtEventOccurred(sDMRiskAssessmentdto.
	 * getDateEventOccurred());
	 * ccmn01uiDto.setEventLastUpdate(sDMRiskAssessmentdto.
	 * getEventDateLastUpdate());
	 * ccmn01uiDto.setSzCdEventStatus(sDMRiskAssessmentdto.getEventStatus());
	 * 
	 * ccmn01uiDto.setSzCdTask(sDMRiskAssessmentdto.getTaskCode());
	 * 
	 * ccmn01uiDto.setSzUserId(new
	 * Integer(sDMRiskAssessmentdto.getLoggedInUser()).toString());
	 * 
	 * ccmn01uService.callCcmn01uService(ccmn01uiDto);
	 * 
	 * hasInvalidatedApproval = true; } return hasInvalidatedApproval; }
	 */

	/**
	 * Method to Create Event and Return Event Id of the new Event
	 * 
	 * @param SDMRiskAssessmentDto
	 *            SDM Risk Assessment DataBean
	 * @param userProfile
	 *            Logged in User
	 * @param eventStatus
	 *            Event Status of the to be created event
	 * @param assessmentType
	 *            Assessment Type from Codes tables
	 * @return long New Event Id that was created
	 */
	private int createAndReturneventId(SDMRiskAssessmentDto sDMRiskAssessmentdto) {

		PostEventReq postEventReq = getEventDetails(sDMRiskAssessmentdto);
		// PostEventReq postEventReq = new PostEventReq();

		/*
		 * PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
		 * postEventPersonDto.setIdPerson(sDMRiskAssessmentdto.getApprovalEvent(
		 * ).getIdPerson());
		 * 
		 * List<PostEventPersonDto> postEventPersonDtos = new
		 * ArrayList<PostEventPersonDto>();
		 * postEventPersonDtos.add(postEventPersonDto);
		 * 
		 * postEventReq.setPostEventPersonList(postEventPersonDtos);
		 * 
		 * postEventReq.setSzCdTask(sDMRiskAssessmentdto.getApprovalEvent().
		 * getCdTask());
		 * postEventReq.setTsLastUpdate(sDMRiskAssessmentdto.getApprovalEvent().
		 * getDtLastUpdate());
		 * postEventReq.setSzCdEventStatus(sDMRiskAssessmentdto.getApprovalEvent
		 * ().getCdEventStatus());
		 * postEventReq.setSzCdEventType(sDMRiskAssessmentdto.getApprovalEvent()
		 * .getCdEventType());
		 * postEventReq.setDtDtEventOccurred(sDMRiskAssessmentdto.
		 * getApprovalEvent().getDtEventOccurred());
		 * postEventReq.setUlIdEvent(sDMRiskAssessmentdto.getApprovalEvent().
		 * getIdEvent());
		 * postEventReq.setUlIdStage(sDMRiskAssessmentdto.getApprovalEvent().
		 * getIdStage());
		 * postEventReq.setUlIdPerson(sDMRiskAssessmentdto.getApprovalEvent().
		 * getIdPerson());
		 * postEventReq.setSzTxtEventDescr(sDMRiskAssessmentdto.getApprovalEvent
		 * ().getEventDescr());
		 * postEventReq.setUlIdCase(sDMRiskAssessmentdto.getApprovalEvent().
		 * getIdCase());
		 */

		PostEventRes postEventRes = eventService.postEvent(postEventReq);
		return postEventRes.getUlIdEvent().intValue();
	}

	private PostEventReq getEventDetails(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
		PostEventReq postEventReq = new PostEventReq();
		postEventReq.setSzCdEventType(ServiceConstants.CEVNTTYP_ASM);
		postEventReq.setSzCdTask(ServiceConstants.SDM_RISK_ASSMT_TASK);
		postEventReq.setSzTxtEventDescr(ServiceConstants.SDM_RISK_ASSESSMENT);
		postEventReq.setUlIdPerson(sDMRiskAssessmentdto.getIdUser());
		postEventReq.setUlIdStage(sDMRiskAssessmentdto.getIdStage());
		postEventReq.setDtDtEventOccurred(new Date());
		postEventReq.setIdUser(sDMRiskAssessmentdto.getIdUser());
		postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventReq.setTsLastUpdate(sDMRiskAssessmentdto.getDateLastUpdate());
		postEventReq.setSzCdEventStatus(ServiceConstants.PROCESS_EVENT);
		return postEventReq;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public SDMRiskAssessmentRes deleteRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentDto) {
		SDMRiskAssessmentRes sDMRiskAssessmentRes = new SDMRiskAssessmentRes();
		sDMRiskAssessmentRes.setMessage(sDMRiskAssessmentDao.deleteRiskAssessment(sDMRiskAssessmentDto));

		return sDMRiskAssessmentRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public SDMRiskAssessmentRes getHouseholdName(SDMRiskAssessmentDto sDMRiskAssessmentDto) {
		SDMRiskAssessmentRes sDMRiskAssessmentRes = new SDMRiskAssessmentRes();
		SDMRiskAssessmentDto sDMRiskAssessment = sDMRiskAssessmentDao.getHouseholdName(sDMRiskAssessmentDto);
		if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessment)) {
			sDMRiskAssessmentRes.setsDMRiskAssessmentdto(sDMRiskAssessment);
		}
		return sDMRiskAssessmentRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SDMRiskAssessmentRes saveHouseholdDtl(SDMRiskAssessmentDto sDMRiskAssessmentdto) {
		SDMRiskAssessmentRes sDMRiskAssessmentRes = new SDMRiskAssessmentRes();
		if (!TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentdto)) {
			sDMRiskAssessmentRes = sDMRiskAssessmentDao.saveHouseholdDtl(sDMRiskAssessmentdto);
		}

		return sDMRiskAssessmentRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SDMRiskAssessmentRes getExistingRAForHousehold(SDMRiskAssessmentDto retrieveHouseHoldForCaseReq) {
		SDMRiskAssessmentRes sDMRiskAssessmentRes = new SDMRiskAssessmentRes();
		if (!TypeConvUtil.isNullOrEmpty(retrieveHouseHoldForCaseReq)) {
			sDMRiskAssessmentRes = sDMRiskAssessmentDao.getExistingRAForHousehold(retrieveHouseHoldForCaseReq);
		}

		return sDMRiskAssessmentRes;
	}

	/**
	 * Method Name: getRiskAssesment. Method Description: This method gets the
	 * cpa_ra record for input idStage
	 * 
	 * @param sdmRiskAssessmentReq
	 * @return SDMRiskAssessmentRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public SDMRiskAssessmentRes getRiskAssesment(Long idStage) {
		SDMRiskAssessmentRes sdmRiskAssessmentRes = new SDMRiskAssessmentRes();
		sdmRiskAssessmentRes.setsDMRiskAssessmentdto(sDMRiskAssessmentDao.getRiskAssessment(idStage));
		return sdmRiskAssessmentRes;
	}

	/**
	 * Method Name: queryRiskAssessmentDtls. Method Description: This service is
	 * to retrieve if Risk Assessment details exists and the event status of
	 * Risk Assessment.
	 * 
	 * @param riskAssmtValueDto
	 * @return RiskAssmtValueDto
	 */
	public SDMRiskAssessmentRes queryRiskAssessmentExists(SDMRiskAssessmentDto sdmRiskAssessmentDto) {
		SDMRiskAssessmentRes sdmRiskAssessmentRes = new SDMRiskAssessmentRes();
		sdmRiskAssessmentRes
				.setsDMRiskAssessmentdto(sDMRiskAssessmentDao.queryRiskAssessmentExists(sdmRiskAssessmentDto));
		return sdmRiskAssessmentRes;
	}

	/**
	 * Method Name:getSDMRiskAssessment Method Description:To get a
	 * riskAssessment with eventId and stageId
	 * 
	 * @param eventId
	 * @param stageId
	 * @return SDMRiskAssessmentDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public SDMRiskAssessmentDto getSDMRiskAssessment(Long idStage, Long idEvent) {
		SDMRiskAssessmentDto sdmRiskAssessmentDto = sDMRiskAssessmentDao.queryRiskAssessment(idEvent, idStage);
		sDMRiskAssessmentDao.getPersonDetails(sdmRiskAssessmentDto, idStage);

		boolean isSection1Comp = isSection1Complete(sdmRiskAssessmentDto);
		boolean isSection2Comp = isSection2Complete(sdmRiskAssessmentDto);
		sdmRiskAssessmentDto.setSection1Compete(isSection1Comp);
		if (!TypeConvUtil.isNullOrEmpty(isSection1Comp) && isSection1Comp == true) {
			sdmRiskAssessmentDto.setIndSection1Complete(true);
		}
		if (!TypeConvUtil.isNullOrEmpty(isSection2Comp) && isSection2Comp == true) {
			sdmRiskAssessmentDto.setIndSection2Complete(true);
		}
		sdmRiskAssessmentDto.setSection2Compete(isSection2Comp);
		return sdmRiskAssessmentDto;
	}

	/**
	 * Method Name: queryPageData Method Description:To pull the questions,
	 * answers, followups ans secondary follow ups when the assessment is new
	 * 
	 * @param idStage
	 * @return SDMRiskAssessmentDBDto
	 */
	@Override
	public SDMRiskAssessmentDto queryPageData(Long idStage) {
		SDMRiskAssessmentDto sdmRiskAssessmentDto = sDMRiskAssessmentDao.queryPageData(idStage);
		sDMRiskAssessmentDao.getPersonDetails(sdmRiskAssessmentDto, idStage);

		return sdmRiskAssessmentDto;
	}

	/**
	 * Method Name: getPrimaryCreGivrHistoryCount Method Description:To get a
	 * Primary Caregiver History with care giver Id and stageId
	 * 
	 * @param idPrimaryCaregiver,idStage
	 * @return Long
	 */

	@Override
	public Long getPrimaryCreGivrHistoryCount(Long idPrimaryCaregiver, Long idStage) {
		long countPriCareGiver = 0l;
		countPriCareGiver = sDMRiskAssessmentDao.getPrimaryCreGivrHistoryCount(idPrimaryCaregiver, idStage).size();
		return countPriCareGiver;

	}

	/**
	 * Method Name: getSecondaryCreGivrHistoryCount Method Description:To get a
	 * Secondary Caregiver History with care giver Id and stageId
	 * 
	 * @param idSecondaryCaregiver,idStage
	 * @return Long
	 */

	@Override
	public Long getSecondaryCreGivrHistoryCount(Long idSecondaryCaregiver, Long idStage) {
		long countSecondaryCareGiver = 0l;
		countSecondaryCareGiver = sDMRiskAssessmentDao.getSecondaryCreGivrHistoryCount(idSecondaryCaregiver, idStage)
				.size();
		return countSecondaryCareGiver;

	}

	@Override
	public SafetyAssessmentRes retrieveSafetyAssmtData(SafetyAssessmentReq safetyAssessmentReq) {
		SafetyAssessmentRes safetyAssessmentRes = sDMRiskAssessmentDao.retrieveSafetyAssmtData(safetyAssessmentReq);
		return safetyAssessmentRes;
	}

	/**
	 * Method Name:getSubStageOpen Method Description:Returns if a Sub Stage is
	 * open, with a given StageId
	 * 
	 * @param safetyAssessmentDto
	 * @return boolean
	 */
	@Override
	public boolean getsubStageOpen(Long idCase) {
		boolean subStageOpen = sDMRiskAssessmentDao.getsubStageOpen(idCase);
		return subStageOpen;
	}

	@Override
	public String getCurrentEventStatus(Long idStage, Long idCase) {
		String eventStatus = sDMRiskAssessmentDao.getCurrentEventStatus(idStage, idCase);
		return eventStatus;
	}

	private boolean isSection2Complete(SDMRiskAssessmentDto sDMRiskAssessmentDto) {
		boolean isSection2Complete = false;
		if (ServiceConstants.CDOVRIDE_NOOVERRIDE.equalsIgnoreCase(sDMRiskAssessmentDto.getOverrideCode())) {
			isSection2Complete = true;
		}

		else if (ServiceConstants.CDOVRIDE_POLICYOVERRIDE.equalsIgnoreCase(sDMRiskAssessmentDto.getOverrideCode())) {
			if ((!ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildDeath())
					|| !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildLessThanThreeInjured())
					|| !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildLessThanSixteenInjured())
					|| !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getIsPOChildSexualAbuse()))
					&& StringUtil.isValid(sDMRiskAssessmentDto.getOverrideReason())) {
				isSection2Complete = true;
			}

		}
		if (ServiceConstants.CDOVRIDE_DISCRETIONARYOVERRID.equalsIgnoreCase(sDMRiskAssessmentDto.getOverrideCode())
				&& StringUtil.isValid(sDMRiskAssessmentDto.getOverrideReason())) {
			isSection2Complete = true;
		}

		return isSection2Complete;
	}

	private boolean isSection1Complete(SDMRiskAssessmentDto sDMRiskAssessmentDto) {

		boolean followupAnswered = true;

		Set answeredQuestionSet = new HashSet();
		Set notAnsweredFollowupSet = new HashSet();
		Set secNotAnsweredFollowupSet = new HashSet();
		boolean allAnswered = false;
		boolean allFollowupAnswered = true;
		boolean allSecondaryFollowupAnswered = true;
		List<SDMRiskAssessmentQuestionDto> questions = sDMRiskAssessmentDto.getQuestions();

		if (!ObjectUtils.isEmpty(questions)) {
			for (SDMRiskAssessmentQuestionDto questionDB : questions) {
				List<SDMRiskAssessmentAnswerDto> answers = questionDB.getAnswers();

				if (!ObjectUtils.isEmpty(answers)) {
					for (SDMRiskAssessmentAnswerDto answerDB : answers) {

						if (ServiceConstants.ANSWERCODE_WITH_FOLLOWUPS.contains(answerDB.getAnswerCode().toString())
								&& answerDB.getResponseCode() != null) {

							List<SDMRiskAssessmentFollowupDto> followupQuestions = answerDB.getFollowupQuestions();

							if (!ObjectUtils.isEmpty(followupQuestions)) {

								for (SDMRiskAssessmentFollowupDto followupDto : followupQuestions) {
									if (ServiceConstants.STRING_IND_Y
											.equalsIgnoreCase(followupDto.getIndRaFollowup())) {
										followupAnswered = true;
										break;
									}

									else {

										followupAnswered = false;

									}

									if ((ServiceConstants.FOLLOWUPID_WITH_SECFOLLOWUP
											.contains(new Integer(followupDto.getFollowupId())))
											&& (ServiceConstants.YES
													.equalsIgnoreCase(followupDto.getIndRaFollowup()))) {
										List<SDMRiskAssmtSecondaryFollowupDto> secondaryFollowupQuestions = followupDto
												.getSecondaryFollowupQuestions();

										if (!ObjectUtils.isEmpty(secondaryFollowupQuestions)) {

											for (SDMRiskAssmtSecondaryFollowupDto secFollowupDto : secondaryFollowupQuestions) {
												if (ServiceConstants.YES
														.equalsIgnoreCase(secFollowupDto.getIndSecFollowupLookup())) {
													break;
												} else {
													secNotAnsweredFollowupSet
															.add(new Integer(answerDB.getAnswerLookupId()));
												}
											}
										}

									}
								}
							}
							if (!followupAnswered) {

								notAnsweredFollowupSet.add(new String(answerDB.getAnswerCode()));
							}
						}

						if (!ObjectUtils.isEmpty(answerDB.getResponseCode())) {
							answeredQuestionSet.add(new Integer(answerDB.getQuestionLookupId()));
							break;
						}
					}
				}
			}
		}
		if (!ObjectUtils.isEmpty(sDMRiskAssessmentDto) && !ObjectUtils.isEmpty(sDMRiskAssessmentDto.getQuestions())
				&& answeredQuestionSet.size() == sDMRiskAssessmentDto.getQuestions().size()) {
			allAnswered = true;

		}
		if (notAnsweredFollowupSet.size() > 0) {

			allFollowupAnswered = false;

		}

		if (secNotAnsweredFollowupSet.size() > 0) {
			allSecondaryFollowupAnswered = false;

		}

		if (allSecondaryFollowupAnswered && allFollowupAnswered && allAnswered) {
			return true;
		}
		return false;
	}

}