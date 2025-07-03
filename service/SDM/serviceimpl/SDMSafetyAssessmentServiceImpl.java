package us.tx.state.dfps.service.SDM.serviceimpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.AssessmentHouseholdLink;
import us.tx.state.dfps.common.domain.CpsArCnclsnDetail;
import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.SDM.dao.SDMSafetyAssessmentDao;
import us.tx.state.dfps.service.SDM.service.SDMSafetyAssessmentService;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.approval.service.ApprovalStatusService;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.PcspAssessmentDao;
import us.tx.state.dfps.service.casepackage.dto.PCSPPersonDto;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.SdmSafetyAssessmentPrefillData;
import us.tx.state.dfps.service.investigation.dao.CpsInvstDetailDao;
import us.tx.state.dfps.service.investigation.dao.PcspDao;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentResponseDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssmentDto;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * Service implementation for functions required for implementing SDM Safety
 * Assessment functionality
 *
 */
@Service
@Transactional
public class SDMSafetyAssessmentServiceImpl implements SDMSafetyAssessmentService {

	@Autowired
	SDMSafetyAssessmentDao sdmSafetyAssessmentDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PostEventService postEventService;

	@Autowired
	StageDao stageDao;

	@Autowired
	CommonService commonService;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	CpsInvstDetailDao cpsInvstDetailDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;

	@Autowired
	ArHelperDao arHelperDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	SdmSafetyAssessmentPrefillData prefilldata;

	@Autowired
	CaseUtils caseUtils;

	@Autowired
	PcspAssessmentDao pcspAssessmentDao;

	@Autowired
	PcspDao pcspDao;
	
	@Autowired
	CaseSummaryService caseSummaryService;
	
	@Autowired
	WorkLoadDao workLoadDao;
	
	@Autowired
	AlertService  alertService;

	@Autowired
	ApprovalStatusService approvalStatusService;

	private static final Logger logger = Logger.getLogger(SDMSafetyAssessmentServiceImpl.class);

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public SDMSafetyAssessmentDto saveAssessment(SDMSafetyAssessmentDto safetyAssessmentDto,
			UserProfileDto userProfileDB) {
		checkForInvalidateApproval(safetyAssessmentDto);
		if (!ObjectUtils.isEmpty(safetyAssessmentDto.getIdEvent()) && safetyAssessmentDto.getIdEvent() != 0) {
			updateSafetyAssessment(safetyAssessmentDto, userProfileDB); 
		} else {
			addSafetyAssessment(safetyAssessmentDto, userProfileDB, ServiceConstants.CEVTSTAT_PROC);
		}

		return safetyAssessmentDto;
	}

	private void checkForInvalidateApproval(SDMSafetyAssessmentDto safetyAssessmentDto) {
		if ((!ObjectUtils.isEmpty(safetyAssessmentDto.getApprovalEvent()))
				&& (safetyAssessmentDto.getApprovalEvent().getIdEvent() != 0)) {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			approvalCommonInDto.setIdEvent(safetyAssessmentDto.getApprovalEvent().getIdEvent());
			approvalService.callCcmn05uService(approvalCommonInDto);
		}
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public long deleteSafetyAssmtDetails(SDMSafetyAssessmentDto safetyAssessmentDB) {
		// Handle the household if this assesment is with Household.
		if (safetyAssessmentDB.isAssmntWithHouseHold()) {
			handleHouseHoldForConclusion(safetyAssessmentDB);
		}
		return sdmSafetyAssessmentDao.deleteSafetyAssmtDetails(safetyAssessmentDB);
	}

	/**
	 * method to Update Safety Assessment
	 *
	 * @param safetyDB
	 *            Safety Assessment DataBean Object
	 * @param userProfile
	 *            Logged in User Object
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public void updateSafetyAssessment(SDMSafetyAssessmentDto safetyDB, UserProfileDto userProfile) {
		determineSafetyDecision(safetyDB);
		sdmSafetyAssessmentDao.updateSDMSafetyAssessment(safetyDB);
		// Save Checked Person Assessed
		updatePersonAssessed(safetyDB);
		List<SDMSafetyAssessmentResponseDto> responseList = safetyDB.getSafetyResponseBySectionMap().entrySet().stream()
				.flatMap(o -> o.getValue().stream()).collect(Collectors.toList());
		for (SDMSafetyAssessmentResponseDto responseDB : responseList) {
			responseDB.setLoggedInUser(safetyDB.getLoggedInUser());
			sdmSafetyAssessmentDao.updateSDMSafetyAssessmentResponse(responseDB);
			List<SDMSafetyAssessmentFollowupDto> followupList = responseDB.getFollowupResponseList();
			if (null != followupList && followupList.size() > 0) {
				for (SDMSafetyAssessmentFollowupDto followupDB : followupList) {
					followupDB.setLoggedInUser(safetyDB.getLoggedInUser().intValue());
					sdmSafetyAssessmentDao.updateFollowupResponse(followupDB);
				}
			}
		}
	}

	/**
	 * method to add New Safety Assessment
	 *
	 * @param safetyDB
	 *            SDM Safety Assessment Databean.
	 * @param userProfile
	 *            User making the changes
	 * @param eventStatus
	 *            Status of the new Event to be created @
	 */
	private void addSafetyAssessment(SDMSafetyAssessmentDto safetyDB, UserProfileDto userProfile, String eventStatus) {
		if (ObjectUtils.isEmpty(safetyDB.getIdEvent()) || safetyDB.getIdEvent() == 0) {
			safetyDB.setIdEvent(
					createAndReturnEventId(safetyDB, userProfile, eventStatus, safetyDB.getAssessmentType()));
		}
		determineSafetyDecision(safetyDB);
		// Save Checked Person Assessed
		updatePersonAssessed(safetyDB);
		sdmSafetyAssessmentDao.addSafetyAssessment(safetyDB);
	}

	/**
	 * method to determine type of Safety Decision(Safe, Unsafe, Safe with Plan)
	 * Based on User responses to Current Danger Indicators in Section 2 and
	 * Safety Interventions in Section 4 of Safety Assessment
	 *
	 * @param safetyDB
	 */
	private void determineSafetyDecision(SDMSafetyAssessmentDto safetyDB) {
		int countDFNotAnswered = 0;
		int countDFNoSelectedAsResponse = 0;
		int countDFQuestions = 0;
		boolean isChildRemovalSelected = false;
		List<SDMSafetyAssessmentResponseDto> responseList = safetyDB.getSafetyResponseBySectionMap().entrySet().stream()
				.flatMap(o -> o.getValue().stream()).collect(Collectors.toList());
		for (SDMSafetyAssessmentResponseDto responseDB : responseList) {
			if (ServiceConstants.SECTION_2.equals(responseDB.getCdSection())) {
				if (null == responseDB.getCdQuestionResponse()
						|| ServiceConstants.EMPTY_STRING.equals(responseDB.getCdQuestionResponse())) {
					countDFNotAnswered++;
				}
				if (null != responseDB.getCdQuestionResponse()
						&& (ServiceConstants.STRING_IND_N.equals(responseDB.getCdQuestionResponse()))) {
					countDFNoSelectedAsResponse++;
				}
				countDFQuestions++;
			} else if (ServiceConstants.SECTION_4.equals(responseDB.getCdSection())
					&& ServiceConstants.SECTION_4_CSI_QUESTION_1.equals(responseDB.getCdQuestion())
					&& (ServiceConstants.STRING_IND_Y.equals(responseDB.getCdQuestionResponse()))) {
				isChildRemovalSelected = true;
			}
		}
		if ((countDFNoSelectedAsResponse == countDFQuestions)) {
			// All Section 2 questions answered No.
			safetyDB.setCdCurrSafetyDecision(ServiceConstants.CSDMDCSN_SAFE);
			safetyDB.setUnsafeDecisionAction(ServiceConstants.EMPTY_STRING);
		} else if ((countDFNotAnswered == 0) && (countDFQuestions > countDFNoSelectedAsResponse)) {
			if (isChildRemovalSelected) {
				safetyDB.setCdCurrSafetyDecision(ServiceConstants.CSDMDCSN_UNSAFE);
			} else {
				safetyDB.setCdCurrSafetyDecision(ServiceConstants.CSDMDCSN_SAFEWITHPLAN);
				safetyDB.setUnsafeDecisionAction(ServiceConstants.EMPTY_STRING);
			}
		} else {
			safetyDB.setCdCurrSafetyDecision(ServiceConstants.EMPTY_STRING);
			safetyDB.setUnsafeDecisionAction(ServiceConstants.EMPTY_STRING);
		}
		if (ServiceConstants.CSDMDCSN_SAFE.equals(safetyDB.getCdCurrSafetyDecision())
				&& (ServiceConstants.CSDMDCSN_SAFEWITHPLAN.equals(safetyDB.getCdSavedSafetyDecision())
						|| ServiceConstants.CSDMDCSN_UNSAFE.equals(safetyDB.getCdSavedSafetyDecision()))) {
			resetSection(ServiceConstants.SECTION_3, safetyDB);
			resetSection(ServiceConstants.SECTION_4, safetyDB);
		}
		return;
	}

	/**
	 * method to Reset User submitted data in a Section
	 *
	 * @param sectionCode
	 *            Section Code for Section that needs to be reset
	 * @param safetyDB
	 *            SDM Safety Assessment DataBean
	 * 
	 */
	private void resetSection(String sectionCode, SDMSafetyAssessmentDto safetyDB) {
		List<SDMSafetyAssessmentResponseDto> sectionList = safetyDB.getSafetyResponseBySectionMap().get(sectionCode);
		for (SDMSafetyAssessmentResponseDto responseDB : sectionList) {
			responseDB.setCdQuestionResponse(ServiceConstants.EMPTY_STRING);
			responseDB.setOtherDescriptionText(ServiceConstants.EMPTY_STRING);
			responseDB.setIndDirty(true);
		}
	}

	/**
	 * method to Update Event_Person_Link table in Database based on user
	 * selection on UI for Person Assessed
	 *
	 * @param safetyDB
	 *            SDM Safety Assessment DataBean @
	 * 
	 */
	private void updatePersonAssessed(SDMSafetyAssessmentDto safetyDB) {
		Set<Integer> tobeAddedPersonAssessed = null;
		if (!ObjectUtils.isEmpty(safetyDB.getSelectedPersonAssessed()))
			tobeAddedPersonAssessed = safetyDB.getSelectedPersonAssessed().stream().collect(Collectors.toSet());
		else
			tobeAddedPersonAssessed = new HashSet<Integer>();
		if (!ObjectUtils.isEmpty(safetyDB.getSavedPersonAssessed()))
			tobeAddedPersonAssessed.removeAll(safetyDB.getSavedPersonAssessed());
		Set<Integer> tobeDeletedPersonAssessed = null;
		if (!ObjectUtils.isEmpty(safetyDB.getSavedPersonAssessed()))
			tobeDeletedPersonAssessed = safetyDB.getSavedPersonAssessed().stream().collect(Collectors.toSet());
		else
			tobeDeletedPersonAssessed = new HashSet<Integer>();
		if (!ObjectUtils.isEmpty(safetyDB.getSelectedPersonAssessed()))
			tobeDeletedPersonAssessed.removeAll(safetyDB.getSelectedPersonAssessed());
		for (Integer tobeDeletedPersonId : tobeDeletedPersonAssessed) {
			sdmSafetyAssessmentDao.deleteEventPersonLink(tobeDeletedPersonId, safetyDB.getIdEvent().intValue());
		}
		for (Integer tobeAddedPersonId : tobeAddedPersonAssessed) {
			sdmSafetyAssessmentDao.addEventPersonLink(tobeAddedPersonId, safetyDB.getIdEvent().intValue(),
					safetyDB.getIdCase().intValue());
		}
	}

	/**
	 * method to Create Event and Return Event Id of the new Event
	 *
	 * @param safetyDB
	 *            SDM Safety Assessment DataBean
	 * @param userProfileDB
	 *            Logged in User
	 * @param eventStatus
	 *            Event Status of the to be created event
	 * @param assessmentType
	 *            Assessment Type from Codes tables
	 * @return int New Event Id that was created @
	 */
	private Long createAndReturnEventId(SDMSafetyAssessmentDto safetyDB, UserProfileDto userProfile, String eventStatus,
			String assessmentType) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
		if (ServiceConstants.CSDMASMT_INIT.equals(safetyDB.getAssessmentType()))
			postEventIPDto.setEventDescr(ServiceConstants.INITIAL_SAFETY_EVENT_DESCR);
		if (ServiceConstants.CSDMASMT_REAS.equals(safetyDB.getAssessmentType()))
			postEventIPDto.setEventDescr(ServiceConstants.REASSESS_SAFETY_EVENT_DESCR);
		if (ServiceConstants.CSDMASMT_CLOS.equals(safetyDB.getAssessmentType()))
			postEventIPDto.setEventDescr(ServiceConstants.INV_CLOSURE_SAFETY_EVENT_DESCR);
		postEventIPDto.setCdTask(safetyDB.getCdTask());
		postEventIPDto.setIdPerson(userProfile.getIdUser());
		postEventIPDto.setIdStage(safetyDB.getIdStage());
		postEventIPDto.setDtEventOccurred(new Date());
		postEventIPDto.setUserId(userProfile.getIdUserLogon());
		archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		postEventIPDto.setTsLastUpdate(new Date());
		postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_ASM);
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENT_PROC);
		postEventIPDto.setDtEventOccurred(new Date());
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
		return postEventOPDto.getIdEvent();
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public SafetyAssessmentRes getSDMSafetyAssessment(Long idEvent, Long idStage) {
		SafetyAssessmentRes res = new SafetyAssessmentRes();
		SDMSafetyAssessmentDto sdmSafetyDto = sdmSafetyAssessmentDao.getSDMSafetyAssessment(idEvent, idStage);
		StageDto stageDto = stageDao.getStageById(idStage);
		String eventStatus = eventDao.getEventStatus(idEvent);
		// Condition included for Defect 4857 To Show Household on SDM Safety
		// Assessment Done on Release Date and Still in PROC Status for A-R
		// Stages.
		if (ServiceConstants.PROCESS_EVENT_STATUS.equals(eventStatus)
				&& CodesConstant.CSTAGES_AR.equals(stageDto.getCdStage()) && stageDto.getDtStageStart()
						.getTime() == commonService.getAppRelDate(ServiceConstants.ReleaseCode).getTime()) {
			sdmSafetyDto.setAssmntWithHouseHold(true);
		}
		sdmSafetyDto.setDtStageStart(stageDto.getDtStageStart());
		res.setSafetyAssessmentDB(sdmSafetyDto);
		return res;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public SafetyAssessmentRes isSftAsmntInProcStatusAvail(Long idStage, String cdStage) {
		SafetyAssessmentRes res = new SafetyAssessmentRes();
		StageDto stageDto = stageDao.getStageById(idStage);
		if (stageDto.getDtStageStart().after(commonService.getAppRelDate(ServiceConstants.ReleaseCode)))
			res.setStatusMsg(false);
		else
			res.setStatusMsg(sdmSafetyAssessmentDao.isSftAsmntInProcStatusAvail(idStage, cdStage));
		return res;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = false)
	public SafetyAssessmentRes getQueryPageData(Long idStage) {
		SafetyAssessmentRes res = new SafetyAssessmentRes();
		StageDto stageDto = stageDao.getStageById(idStage);
		Boolean assmntWithHouseHold = Boolean.FALSE;
		// Including the Or Condition for Defect 4854,4897
		if ((stageDto.getDtStageStart().after(commonService.getAppRelDate(ServiceConstants.ReleaseCode))
				|| (CodesConstant.CSTAGES_AR.equals(stageDto.getCdStage()) && stageDto.getDtStageStart()
						.getTime() == commonService.getAppRelDate(ServiceConstants.ReleaseCode).getTime()))
				|| CodesConstant.CSTAGES_FPR.equals(stageDto.getCdStage())
				|| CodesConstant.CSTAGES_FRE.equals(stageDto.getCdStage())
				|| CodesConstant.CSTAGES_FSU.equals(stageDto.getCdStage())) {
			assmntWithHouseHold = Boolean.TRUE;
		}
		SDMSafetyAssessmentDto asmntDto = sdmSafetyAssessmentDao.getQueryPageData(idStage, assmntWithHouseHold);
		if (assmntWithHouseHold)
			asmntDto.setAssmntWithHouseHold(true);
		asmntDto.setDtStageStart(stageDto.getDtStageStart());
		res.setSafetyAssessmentDB(asmntDto);
		return res;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public CommonStringRes getAsmtTypHoHold(CommonHelperReq commonHelperReq) {
		CommonStringRes commonStringRes = null;
		commonStringRes = new CommonStringRes();
		if (sdmSafetyAssessmentDao.isNewSftAsmntInProcStatusAvail(commonHelperReq.getIdStage(),
				commonHelperReq.getIdPerson(), commonHelperReq.getCdStage(), commonHelperReq.getIdToDo())) {
			commonStringRes.setCommonRes("56886");
		} else {
			Long priorStageID = ServiceConstants.ZERO;
			if (ObjectUtils.isEmpty(commonHelperReq.getCdStage())){
				StageDto stageDto = stageDao.getStageById(commonHelperReq.getIdStage());
				commonHelperReq.setCdStage(stageDto.getCdStage());
			}
				
			// retrieve A-R stage that lead to this INV if exists
			if (ServiceConstants.CSTAGES_INV.equalsIgnoreCase(commonHelperReq.getCdStage())){
				SelectStageDto stageDto = caseSummaryService.getPriorStage(commonHelperReq);
				if (!ObjectUtils.isEmpty(stageDto) && !ObjectUtils.isEmpty(stageDto.getCdStage())
						&& ServiceConstants.A_R_STAGE.equals(stageDto.getCdStage())) {
					priorStageID = stageDto.getIdStage();
				}
			}
			String assmntType = ServiceConstants.EMPTY_STRING;
			if(!ObjectUtils.isEmpty(commonHelperReq.getCaseStatus()) && ServiceConstants.YES_TEXT.equalsIgnoreCase(commonHelperReq.getCaseStatus())){
				assmntType = sdmSafetyAssessmentDao.checkAsmtTypHoHold(commonHelperReq, priorStageID);
			}else{
				assmntType = sdmSafetyAssessmentDao.getAsmtTypHoHold(commonHelperReq,priorStageID);
			}
			commonStringRes.setCommonRes(assmntType);
		}
		commonStringRes.setIdPersonAssessedList(sdmSafetyAssessmentDao.getLatestAssessmentPersonAssessedList(commonHelperReq.getIdCase(), commonHelperReq.getIdPerson(), commonHelperReq.getIdEvent()));
		return commonStringRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SDMSafetyAssessmentDto completeAssessment(SDMSafetyAssessmentDto safetyAssessmentDB,
			UserProfileDto userProfileDB) {

		safetyAssessmentDB = sdmSafetyAssessmentDao.completeAssessment(safetyAssessmentDB, userProfileDB);
		safetyAssessmentDB.setEventStatus(ServiceConstants.CEVTSTAT_COMP);

		SelectStageDto currentStageDTO = caseSummaryDao.getStage(safetyAssessmentDB.getIdStage(), ServiceConstants.STAGE_CURRENT);

		if (!ObjectUtils.isEmpty(currentStageDTO)) {

			// artf156992  DEV 254.4 Alert INV and AR Staff on Completed Safety Assessments (When cdStage is FPR, it retrieves the INV/A-R stage id)
			// artf156992  FR 65.9.4 Alert FBSS Staff on Completed Safety Assessments (When cdStage is INV/A-R, it retrieves the FPR stage id)
			List<Long> openStageIds = approvalStatusService.fetchCpsPriorOrProgressedStage(currentStageDTO.getIdStage(),
					currentStageDTO.getCdStage());

			if (!CollectionUtils.isEmpty(openStageIds)) {

				String shortDesc = ServiceConstants.SDM_SAFETY_ASSESSMENT_TEXT_DESC;
				if (ServiceConstants.FAMILY_PRESERVATION_STG.equals(currentStageDTO.getCdStage())) {
					shortDesc = ServiceConstants.SDM_SAFETY_ASSESSMENT_FPR_TEXT_DESC;
				}

				Date dtAssessed = null;

				try {
					dtAssessed = DateUtils.getTimestamp(safetyAssessmentDB.getDtSafetyAssessed(),
							safetyAssessmentDB.getTimeSafetyAssessed());
				} catch (ParseException e) {
					logger.debug("Date convertion issue." + e.getMessage());
					dtAssessed = safetyAssessmentDB.getDtSafetyAssessed();
				}

				String longDesc = String.format(ServiceConstants.SDM_SA_LONG_TEXT_DESC, shortDesc,
						ServiceConstants.dateTimeFormat.format(dtAssessed), safetyAssessmentDB.getNmHouseHoldPerson(),
						safetyAssessmentDB.getCdCurrSafetyDecision());

				if (!StringUtils.isEmpty(longDesc)) {
					// Generates the alerts to Primary & Secondary staff for open stages
					for (Long stageId : openStageIds) {
						List<Long> caseWorkerPersonIdList = workLoadDao.getAssignedWorkersForStage(stageId);
						for (Long idPerson : caseWorkerPersonIdList) {
							alertService.createFbssAlert(stageId, idPerson, null, safetyAssessmentDB.getIdCase(),
									longDesc, shortDesc);
						}
					}
				}
			}
		}

		return safetyAssessmentDB;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SDMSafetyAssessmentDto undoCompleteAssessment(SDMSafetyAssessmentDto safetyAssessmentDB) {
		sdmSafetyAssessmentDao.undoCompleteAssessment(safetyAssessmentDB);
		// Handle the household if this assesment is with Household.
		if (safetyAssessmentDB.isAssmntWithHouseHold()) {
			handleHouseHoldForConclusion(safetyAssessmentDB);
		}
		safetyAssessmentDB.setEventStatus(ServiceConstants.CEVTSTAT_PROC);
		return safetyAssessmentDB;
	}

	/**
	 * Method Name: handleHouseHoldForConclusion Method Description: Method
	 * checks for Household selected in conclusion for the current stage, and
	 * clears the Assessment link if SDM safety assesment is deleted or
	 * converted to proc for selected Household.
	 * 
	 * @param safetyAssessmentDB
	 */
	private void handleHouseHoldForConclusion(SDMSafetyAssessmentDto safetyAssessmentDB) {
		// Check if the Assessment Added for the Stage A-R or INV.
		Stage stage = stageDao.getStageEntityById(safetyAssessmentDB.getIdStage());
		if (!ObjectUtils.isEmpty(stage) && ServiceConstants.INV_Stage.equals(stage.getCdStage())) {
			// Check the INV Stage has the Household link and clear if
			// applicable.
			CpsInvstDetail cpsInvstDetail = cpsInvstDetailDao.getCpsInvstDetailbyStageId(stage);
			if (!ObjectUtils.isEmpty(cpsInvstDetail)
					&& !ObjectUtils.isEmpty(cpsInvstDetail.getIdAssessmentHouseHoldLink())) {
				Person invSelectedHousehold = cpsInvstDetail.getIdAssessmentHouseHoldLink().getIdHshldPerson();
				if (safetyAssessmentDB.getIdHouseHoldPerson().equals(invSelectedHousehold.getIdPerson())) {
					cpsInvstDetail.setIdAssessmentHouseHoldLink(null);
					cpsInvstDetailDao.updtCpsInvstDetail(cpsInvstDetail, ServiceConstants.REQ_FUNC_CD_UPDATE);
				}
			}
		} else if (!ObjectUtils.isEmpty(stage) && ServiceConstants.A_R_STAGE.equals(stage.getCdStage())) {
			// Check the A-R Stage has the Household link and clear if
			// applicable.
			CpsArCnclsnDetail cpsArInvDetail = arHelperDao.getArinvCnclsnDetail(stage.getIdStage().longValue());
			if (!ObjectUtils.isEmpty(cpsArInvDetail)
					&& !StringUtils.isEmpty(cpsArInvDetail.getIdAssessmentHouseholdLink())) {
				AssessmentHouseholdLink assessmentHousehold = arHelperDao
						.getIdHouseholdFromAssessmentHousehold(cpsArInvDetail.getIdAssessmentHouseholdLink());
				if (safetyAssessmentDB.getIdHouseHoldPerson()
						.equals(assessmentHousehold.getIdHshldPerson().getIdPerson())) {
					String blankIdAssessmentHousehold = ServiceConstants.EMPTY_STRING;
					arHelperDao.updIdAssessmentHousehold(cpsArInvDetail.getIdEvent(), blankIdAssessmentHousehold);
				}
			}
		}
	}

	@Override
	public PreFillDataServiceDto displaySDMSafetyAssessmentForm(SafetyAssessmentReq safetyAssessReq) {
		SDMSafetyAssmentDto safetyAssment = new SDMSafetyAssmentDto();

		Map<String, List<SDMSafetyAssessmentFollowupDto>> followupMap = new HashMap<String, List<SDMSafetyAssessmentFollowupDto>>();
		List<SDMSafetyAssessmentResponseDto> getSafetyAssmentResp = new ArrayList<SDMSafetyAssessmentResponseDto>();
		List<SDMSafetyAssessmentFollowupDto> getSDMFollowUpQuestions = new ArrayList<SDMSafetyAssessmentFollowupDto>();

		// getSafetAssessmentList
		SDMSafetyAssessmentDto getSafetAssessmentDto = sdmSafetyAssessmentDao
				.getSafetyAssmentList(safetyAssessReq.getIdEvent());
		if (!ObjectUtils.isEmpty(getSafetAssessmentDto)) {
			safetyAssment.setSafetAssessmentList(getSafetAssessmentDto);
		}

		// getTimeSafetyAssessed
		SafetyAssessmentRes getSafetAssessmentDto2 = getSDMSafetyAssessment(safetyAssessReq.getIdEvent(),
				safetyAssessReq.getIdStage());
		safetyAssment.getSafetAssessmentList()
				.setTimeSafetyAssessed(getSafetAssessmentDto2.getSafetyAssessmentDB().getTimeSafetyAssessed());
		
		// Warranty Defect - 12431 - To set the HouseHold Person Id		
		safetyAssment.getSafetAssessmentList().setIdHouseHoldPerson(getSafetAssessmentDto2.getSafetyAssessmentDB().getIdHouseHoldPerson());

		// eventPersonLink
		List<Long> getEventPersonLink = sdmSafetyAssessmentDao.getEventPersonLink(safetyAssessReq.getIdEvent());
		if (!ObjectUtils.isEmpty(getEventPersonLink)) {
			safetyAssment.setEventPersonLink(getEventPersonLink);
		}

		// safetyAssmentType
		String getSafetyAssmentType = sdmSafetyAssessmentDao.getSafetyAssmentType(safetyAssessReq.getIdEvent());
		if (!ObjectUtils.isEmpty(getSafetyAssmentType)) {
			safetyAssment.getSafetAssessmentList().setAssessmentType(getSafetyAssmentType);
		}

		// safetyAssmentResp
		if (!ObjectUtils.isEmpty(getSafetAssessmentDto)) {
			if (!ObjectUtils.isEmpty(getSafetAssessmentDto.getId())) {
				getSafetyAssmentResp = sdmSafetyAssessmentDao.getSafetyAssmentResp(getSafetAssessmentDto.getId());
				safetyAssment.setSafetyResponses(getSafetyAssmentResp);
			}
		}

		// sdmFollowUpQuestions
		if (!ObjectUtils.isEmpty(getSafetAssessmentDto)) {
			if (!ObjectUtils.isEmpty(getSafetAssessmentDto.getId())) {
				getSDMFollowUpQuestions = sdmSafetyAssessmentDao.getSDMFollowUpQuestions(getSafetAssessmentDto.getId());
				for (SDMSafetyAssessmentFollowupDto followupResponse : getSDMFollowUpQuestions) {
					List<SDMSafetyAssessmentFollowupDto> followupList = followupMap
							.get(followupResponse.getCdQuestion());
					if (ObjectUtils.isEmpty(followupList)) {
						followupList = new ArrayList<SDMSafetyAssessmentFollowupDto>();
						followupMap.put(followupResponse.getCdQuestion(), followupList);
					}
					followupList.add(followupResponse);
				}
				if (!ObjectUtils.isEmpty(getSafetyAssmentResp) && getSafetyAssmentResp.size() > 0) {
					Map<String, List<SDMSafetyAssessmentResponseDto>> sectionMap = new HashMap<String, List<SDMSafetyAssessmentResponseDto>>();
					for (SDMSafetyAssessmentResponseDto responseDB : getSafetyAssmentResp) {
						List<SDMSafetyAssessmentFollowupDto> questionFollowupList = followupMap
								.get(responseDB.getCdQuestion());
						if (!ObjectUtils.isEmpty(questionFollowupList) && questionFollowupList.size() > 0) {
							responseDB.setFollowupResponseList(questionFollowupList);
						}
						List<SDMSafetyAssessmentResponseDto> sectionResponseList = sectionMap
								.get(responseDB.getCdSection());
						if (ObjectUtils.isEmpty(sectionResponseList)) {
							sectionResponseList = new ArrayList<SDMSafetyAssessmentResponseDto>();
							sectionMap.put(responseDB.getCdSection(), sectionResponseList);
						}
						sectionResponseList.add(responseDB);
					}
					safetyAssment.getSafetAssessmentList().setSafetyResponseBySectionMap(sectionMap);
				}
			}
		}

		if (!ObjectUtils.isEmpty(getSafetAssessmentDto.getIdCase())) {
			safetyAssment.getSafetAssessmentList().setCaseName(caseUtils.getNmCase(getSafetAssessmentDto.getIdCase()));
		}

		if (!ObjectUtils.isEmpty(getSafetAssessmentDto.getIdStage())) {
			safetyAssment.setNameList(populateNameList(pcspDao.getPersonDetails(getSafetAssessmentDto.getIdStage())));

		}

		return prefilldata.returnPrefillData(safetyAssment);
	}

	/**
	 * Method Name: populateNameList Method Description: Method to Populate the
	 * Name List for SDM Safety Assessment Form
	 * 
	 * @param List<PcspDto>
	 */

	private List<PCSPPersonDto> populateNameList(List<PcspDto> personDetails) {

		List<PCSPPersonDto> nameList = new ArrayList<PCSPPersonDto>();

		if (!ObjectUtils.isEmpty(personDetails)) {
			for (PcspDto pcspDto : personDetails) {
				PCSPPersonDto pCSPPersonDto = new PCSPPersonDto();
				pCSPPersonDto.setNmPersonFull(pcspDto.getNmPersonFull());
				pCSPPersonDto.setIdPerson(pcspDto.getIdPerson());
				pCSPPersonDto.setStgPrsnType(pcspDto.getCdPersType());
				pCSPPersonDto.setCdStagePersRelInt(pcspDto.getCdPersRelation());
				pCSPPersonDto.setCdStagePersRole(pcspDto.getCdPersRole());
				nameList.add(pCSPPersonDto);
			}

		}
		return nameList;

	}
	
	/**
	 * 
	 * Method Name: getLatestSafetyAssessmentEvent Method Description:Gets
	 * latest safety assessment event id
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
	public Long getLatestSafetyAssessmentEvent(Long idStage) {
		Long idEvent = sdmSafetyAssessmentDao.getLatestSafetyAssessmentEvent(idStage);
		return idEvent;
	}
	/**
	 * 
	 *Method Name:	getLatestSafetyAssessmentEvent
	 *Method Description:gets the Date of Latest Assessment in the Stage.
	 * latest safety assessment event id
	 *@param idStage
	 *@param idEvent
	 *@return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = true)
	public Date getLatestSafetyAssessmentEvent(Long idStage,Long idEvent) {
		Date dtAssessmentLatest = sdmSafetyAssessmentDao.getLatestAssessmentDateInStage(idStage, idEvent);
		return dtAssessmentLatest;
	}
	/**
	 * 
	 *Method Name:	getAsmtTypHouseHoldForAssessedPerson
	 *Method Description: This method is used to found if the person selected in assessed section is same as household selected in other assessment
	 *@param commonHelperReq
	 *@return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public String getAsmtTypHouseHoldForAssessedPerson(Long idStage, Long idCpsSa,List<Long> idPersonList) {
		return sdmSafetyAssessmentDao.getAsmtTypHouseHoldForAssessedPerson(idStage,idCpsSa,idPersonList);
	}
}
