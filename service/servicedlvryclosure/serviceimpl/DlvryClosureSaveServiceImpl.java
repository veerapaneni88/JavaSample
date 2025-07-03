package us.tx.state.dfps.service.servicedlvryclosure.serviceimpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ClosureNoticeListDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dao.StageInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.StageClosureRtrvDto;
import us.tx.state.dfps.service.admin.dto.StageInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casemanagement.service.CPSInvCnlsnService;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.service.PcspService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CpsInvNoticesClosureReq;
import us.tx.state.dfps.service.common.request.ServiceDlvryClosureReq;
import us.tx.state.dfps.service.common.response.CpsInvCnclsnRes;
import us.tx.state.dfps.service.common.response.DlvryClosurevalidationRes;
import us.tx.state.dfps.service.common.response.PCSPAssessmentRes;
import us.tx.state.dfps.service.common.response.ServiceDlvryClosureSaveSubmitRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.ContactPrincipalsCollateralsDto;
import us.tx.state.dfps.service.contacts.service.ContactDetailsService;
import us.tx.state.dfps.service.guardianshipdtl.service.GuardianshipDtlService;
import us.tx.state.dfps.service.person.dao.CriminalHistoryDao;
import us.tx.state.dfps.service.person.service.PersonMPSService;
import us.tx.state.dfps.service.personlistbystage.dao.PersonListByStageDao;
import us.tx.state.dfps.service.servicedlvryclosure.dao.DlvryClosureEventDao;
import us.tx.state.dfps.service.servicedlvryclosure.dao.ServiceDlvryClosureSaveDao;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureSaveService;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureService;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureSubmitService;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureValidationService;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureEventDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureSaveDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureValidationDto;
import us.tx.state.service.servicedlvryclosure.dto.ServiceDlvryClosureDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * Service IMpl save the CSVC18D,CSVC22D,CSVC36D,CINV43D Details Aug 23, 2017-
 * 5:03:32 PM © 2017 Texas Department of Family and Protective Services
 */

@Service
@Transactional
public class DlvryClosureSaveServiceImpl implements DlvryClosureSaveService {

	private static final Logger log = Logger.getLogger("ServiceBusiness-DlvryClosureSaveServiceImpl");
	private static final String SVC_CD_EVENT_TYPE_CLOSE = "STG";
	private static final String FPR_STAGE = "FPR";
	private static final String SVC_CD_EVENT_STATUS_COMPLETE = "COMP";
	private static final String SVC_CD_EVENT_STATUS_PENDING = "PEND";
	private static final String SVC_CD_STAGE_APS_SVC = "SVC";
	private static final String MSG_OPEN_PCSP_AT_STG_CLOSURE_STR = "MSG_OPEN_PCSP_AT_STG_CLOSURE";
	private static final String MSG_PCSP_VAL_ASMNT_COMP_OR_DEL_STR = "MSG_PCSP_VAL_ASMNT_COMP_OR_DEL";
	private static final int MSG_OPEN_PCSP_AT_STG_CLOSURE = 55683;
	private static final int MSG_PCSP_VAL_ASMNT_COMP_OR_DEL = 56731;
	private static final int MSG_CRML_HIST_CHECK_STAGE_APRVL = 55326;
	private static final int MSG_SDM_ASSESSMENT_STATUS = 56984;
	private static final int MSG_COMPLETE_SEX_VIC_QN = 57064;
	
	@Autowired
	StageEventStatusCommonService stageEventService;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	StageInsUpdDelDao stageInsUpdDelDao;

	@Autowired
	ServiceDlvryClosureSaveDao serviceDlvryClosureSaveDao;

	@Autowired
	DlvryClosureEventDao dlvryClosureEventDao;

	@Autowired
	TodoUpdDtTodoCompletedDao todoUpdDtTodoCompletedDao;

	@Autowired
	PostEventStageStatusService postEventStageStatusService;

	@Autowired
	PcspService pscpService;

	@Autowired
	DlvryClosureService dlvryClosureService;

	@Autowired
	DlvryClosureSubmitService dlvryClosureSubmitService;

	@Autowired
	PersonMPSService personMPSService;

	@Autowired
	ContactDetailsService contactDetailsService;

	@Autowired
	GuardianshipDtlService guardianshipDtlService;

	@Autowired
	DlvryClosureValidationService dlvryClosureValidationService;

	@Autowired
	CPSInvCnlsnService cPSInvCnlsnService;

	@Autowired
	StageDao stageDao;
	
	@Autowired
	CriminalHistoryDao criminalHistoryDao;
	
	@Autowired
	private PersonListByStageDao personListByStageDao;


	/**
	 * Method Name: saveDlvryClosureService Method Description:This method Saves
	 * the CSVC18D,CSVC22D,CSVC36D,CINV43D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 * @return DlvryClosureEventDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DlvryClosureEventDto saveDlvryClosureService(DlvryClosureSaveDto dlvryClosureSaveDto) {
		log.debug("Entering method callServDelCloseSaveService in ServDelCloseSaveServiceImpl");
		DlvryClosureEventDto dlvryClosureEventDto = new DlvryClosureEventDto();
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		ApprovalCommonInDto pInvdInput = new ApprovalCommonInDto();
		ApprovalCommonOutDto pInvdOutput = new ApprovalCommonOutDto();

		Stage stageDto = stageDao.getStageEntityById(dlvryClosureSaveDto.getRgStageDto().getIdStage());
		Timestamp existingLastUpdatedTime = new Timestamp(stageDto.getDtLastUpdate().getTime());
		SimpleDateFormat hourSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String hourDate = hourSDF.format(dlvryClosureSaveDto.getRgStageDto().getLastUpdate().getTime());
		Timestamp newLastUpdatedTime = Timestamp.valueOf(hourDate);
		if (existingLastUpdatedTime.after(newLastUpdatedTime)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			dlvryClosureEventDto.setErrorDto(errorDto);
			return dlvryClosureEventDto;
		}
		stageTaskInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		stageTaskInDto.setIdStage(dlvryClosureSaveDto.getRgPostEventDto().getIdStage());
		stageTaskInDto.setCdTask(dlvryClosureSaveDto.getRgPostEventDto().getCdTask());
		String eventStatus = stageEventService.checkStageEventStatus(stageTaskInDto);
		if (ServiceConstants.MSG_SYS_MULT_INST.equals(eventStatus)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(Messages.MSG_SYS_MULT_INST);
			dlvryClosureEventDto.setErrorDto(errorDto);
			return dlvryClosureEventDto;
		} else if (ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH.equals(eventStatus)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(Messages.MSG_SYS_EVENT_STS_MSMTCH);
			dlvryClosureEventDto.setErrorDto(errorDto);
			return dlvryClosureEventDto;
		} else if (ServiceConstants.MSG_SYS_STAGE_CLOSED.equals(eventStatus)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(Messages.MSG_SYS_STAGE_CLOSED);
			dlvryClosureEventDto.setErrorDto(errorDto);
			return dlvryClosureEventDto;
		}
		// to check the event status is sucess
		if (ServiceConstants.ARC_SUCCESS.equals(eventStatus)) {
			if ((!dlvryClosureSaveDto.getSysNbrReserved1())
					&& (SVC_CD_EVENT_STATUS_PENDING.equalsIgnoreCase(dlvryClosureSaveDto.getEventStatus()))) {
				pInvdInput.setIdEvent(dlvryClosureSaveDto.getRgPostEventDto().getIdEvent());
				approvalCommonService.InvalidateAprvl(pInvdInput, pInvdOutput);
				dlvryClosureSaveDto.getRgPostEventDto().setCdEventStatus(SVC_CD_EVENT_STATUS_COMPLETE);
			}
			// for event is new event
			if (TypeConvUtil.isNullOrEmpty(dlvryClosureSaveDto.getRgPostEventDto().getIdEvent())) {
				reteriveDlvryClosureEvent(dlvryClosureSaveDto, dlvryClosureEventDto);
			}

			postEvent(dlvryClosureSaveDto);
			dlvryClosureEventDto.setIdEvent(dlvryClosureSaveDto.getIdEvent());
			saveStageDetails(dlvryClosureSaveDto);
			// if stage is svc update the svc table
			if (SVC_CD_STAGE_APS_SVC.equalsIgnoreCase(dlvryClosureSaveDto.getCdStage())) {
				saveDlvryClosureDetails(dlvryClosureSaveDto);

			}
			updateTodoDetails(dlvryClosureSaveDto);
		}

		Map<String, Integer> attentionMsgMap = new HashMap<>();
		boolean vOpenPCSP = false;
		if (CodesConstant.CSTAGES_FPR.equals(dlvryClosureSaveDto.getRgStageDto().getCdStage())
				&& CodesConstant.CPGRMS_CPS.equals(dlvryClosureSaveDto.getRgStageDto().getCdStageProgram())) {

			List<PcspDto> pcspList = dlvryClosureSaveDto.getPcspList();
			if (!ObjectUtils.isEmpty(pcspList)) {
				for (PcspDto pcspDto : pcspList) {
					String status = pcspDto.getCdStatus();
					if (ObjectUtils.isEmpty(pcspDto.getDtEnd())
							|| ServiceConstants.MAX_DATE.equals(pcspDto.getDtEnd())) {
						if (ObjectUtils.isEmpty(status) || CodesConstant.CPCSPSTA_PEDS.equals(status)
								|| CodesConstant.CPCSPSTA_DTPE.equals(status)
								|| CodesConstant.CPCSPSTA_CIPC.equals(status)) {
							vOpenPCSP = true;

						}

					}
				}
			}
			if (vOpenPCSP) {
				attentionMsgMap.put(MSG_OPEN_PCSP_AT_STG_CLOSURE_STR, MSG_OPEN_PCSP_AT_STG_CLOSURE);
			}

			CommonHelperReq req = new CommonHelperReq();
			req.setIdCase(dlvryClosureSaveDto.getRgStageDto().getIdCase());
			req.setCdStageReasonClosed(dlvryClosureSaveDto.getRgStageDto().getCdStageReasonClosed());
			req.setIdStage(dlvryClosureSaveDto.getRgStageDto().getIdStage());
			req.setCdStage(dlvryClosureSaveDto.getRgStageDto().getCdStage());

			PCSPAssessmentRes pcspAssessmentRes = pscpService.valPCSPPlcmt(req);
			if (pcspAssessmentRes != null && pcspAssessmentRes.getValPCSPPlcmtErr() > 0) {
				int errorMsgCode = pcspAssessmentRes.getValPCSPPlcmtErr();
				attentionMsgMap.put("ErrorMsgCode", errorMsgCode);
			}
			CpsInvCnclsnRes cpsInvCnclsnRes = pscpService.hasOpenPCSPAsmntForStage(req);
			if (!ObjectUtils.isEmpty(cpsInvCnclsnRes) && cpsInvCnclsnRes.getHasOpenPCSPAssessment()) {
				attentionMsgMap.put(MSG_PCSP_VAL_ASMNT_COMP_OR_DEL_STR, MSG_PCSP_VAL_ASMNT_COMP_OR_DEL);
			}
		}
		// ADS Changes
		if(!ObjectUtils.isEmpty(dlvryClosureSaveDto.getClosureNoticeList())) {
			List<ClosureNoticeListDto> closureNotfcnList = dlvryClosureSaveDto.getClosureNoticeList();
			for(ClosureNoticeListDto lst : closureNotfcnList) {
				if(!ObjectUtils.isEmpty(lst.getCdNoticeSelected()) && lst.getCdNoticeSelected().contains(",")) {
					lst.setCdNoticeSelected(lst.getCdNoticeSelected().replaceAll(",", ""));
					
				}
				lst.setIdEvent(dlvryClosureSaveDto.getIdEvent());
				lst.setDtLastUpdated(new Date());
				if(ObjectUtils.isEmpty(lst.getDtCreated())) {
					lst.setDtCreated(new Date());
					lst.setIdCreatedPerson(dlvryClosureSaveDto.getIdUser());
					lst.setReqFuncAction(ServiceConstants.ADD);
				}else {
					lst.setReqFuncAction(ServiceConstants.UPDATE);
				}
				lst.setIdLastUpdatePerson(dlvryClosureSaveDto.getIdUser());
			}
			CpsInvNoticesClosureReq cpsInvNoticesClosureReq = new CpsInvNoticesClosureReq();
			cpsInvNoticesClosureReq.setIdLogin(dlvryClosureSaveDto.getIdUser());
			cpsInvNoticesClosureReq.setParentCaregiverNoticeDto(closureNotfcnList);
			cPSInvCnlsnService.saveClosureNotices(cpsInvNoticesClosureReq);
		}
		dlvryClosureEventDto.setvOpenPCSP(vOpenPCSP);
		dlvryClosureEventDto.setAttentionMsgMap(attentionMsgMap);

		log.debug("Exiting method callServDelCloseSaveService in ServDelCloseSaveServiceImpl");
		return dlvryClosureEventDto;
	}

	/**
	 * Method Name: saveStageDetails Method Description:This method Saves the
	 * CSVC18D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	@Override
	public void saveStageDetails(DlvryClosureSaveDto dlvryClosureSaveDto) {
		log.debug("Entering method saveStageDetails in ServDelCloseSaveServiceImpl");
		StageInsUpdDelInDto stageInsUpdDelInDto = new StageInsUpdDelInDto();
		stageInsUpdDelInDto.setIdStage(dlvryClosureSaveDto.getRgStageDto().getIdStage());
		stageInsUpdDelInDto.setTmSysTmStageClose(dlvryClosureSaveDto.getRgStageDto().getSysTmStageClose());
		stageInsUpdDelInDto.setTmSysTmStageStart(dlvryClosureSaveDto.getRgStageDto().getSysTmStageStart());
		stageInsUpdDelInDto.setIdUnit(dlvryClosureSaveDto.getRgStageDto().getIdUnit());
		stageInsUpdDelInDto.setIndStageClose(dlvryClosureSaveDto.getRgStageDto().getIndStageClose());
		stageInsUpdDelInDto.setNmStage(dlvryClosureSaveDto.getRgStageDto().getNmStage());
		stageInsUpdDelInDto.setCdStage(dlvryClosureSaveDto.getRgStageDto().getCdStage());
		stageInsUpdDelInDto.setCdStageClassification(dlvryClosureSaveDto.getRgStageDto().getCdStageClassification());
		stageInsUpdDelInDto.setCdStageCnty(dlvryClosureSaveDto.getRgStageDto().getCdStageCnty());
		stageInsUpdDelInDto.setCdStageCurrPriority(dlvryClosureSaveDto.getRgStageDto().getCdStageCurrPriority());
		stageInsUpdDelInDto.setCdStageInitialPriority(dlvryClosureSaveDto.getRgStageDto().getCdStageInitialPriority());
		stageInsUpdDelInDto.setCdStageProgram(dlvryClosureSaveDto.getRgStageDto().getCdStageProgram());
		stageInsUpdDelInDto.setCdStageReasonClosed(dlvryClosureSaveDto.getRgStageDto().getCdStageReasonClosed());
		stageInsUpdDelInDto.setDtStageClose(dlvryClosureSaveDto.getRgStageDto().getDtStageClose());
		stageInsUpdDelInDto.setDtStageStart(dlvryClosureSaveDto.getRgStageDto().getDtStartDate());
		stageInsUpdDelInDto.setIdCase(dlvryClosureSaveDto.getRgStageDto().getIdCase());
		stageInsUpdDelInDto.setIdSituation(dlvryClosureSaveDto.getRgStageDto().getIdSituation());
		stageInsUpdDelInDto.setCdClientAdvised(dlvryClosureSaveDto.getRgStageDto().getCdClientAdvised());
		stageInsUpdDelInDto.setIndEcs(dlvryClosureSaveDto.getRgStageDto().getIndEcs());
		stageInsUpdDelInDto.setIndEcsVer(dlvryClosureSaveDto.getRgStageDto().getIndEcsVer());
		stageInsUpdDelInDto.setStageClosureCmnts(dlvryClosureSaveDto.getRgStageDto().getStageClosureCmnts());
		stageInsUpdDelInDto.setStagePriorityCmnts(dlvryClosureSaveDto.getRgStageDto().getStagePriorityCmnts());
		stageInsUpdDelInDto.setCdStageRegion(dlvryClosureSaveDto.getRgStageDto().getCdStageRegion());
		stageInsUpdDelInDto.setCdStageRsnPriorityChgd(dlvryClosureSaveDto.getRgStageDto().getCdStageRsnPriorityChgd());
		stageInsUpdDelInDto.setCdStageType(dlvryClosureSaveDto.getRgStageDto().getCdStageType());
		stageInsUpdDelInDto.setDtClientAdvised(dlvryClosureSaveDto.getRgStageDto().getDtClientAdvised());
		stageInsUpdDelInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		stageInsUpdDelDao.updateStageDetail(stageInsUpdDelInDto);
		log.debug("Exiting method saveStageDetails in ServDelCloseSaveServiceImpl");
	}

	/**
	 * Method Name: saveDlvryClosureDetails Method Description:This method Saves
	 * the CSVC22D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	@Override
	public void saveDlvryClosureDetails(DlvryClosureSaveDto dlvryClosureSaveDto) {
		log.debug("Entering method saveDlvryClosureDetails in ServDelCloseSaveServiceImpl");
		serviceDlvryClosureSaveDao.saveOrUpdatevcDelvDecision(dlvryClosureSaveDto);
		log.debug("Exiting method saveDlvryClosureDetails in ServDelCloseSaveServiceImpl");
	}

	/**
	 * Method Name: updateTodoDetails Method Description:This method fetch the
	 * CSVC36D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	@Override
	public void reteriveDlvryClosureEvent(DlvryClosureSaveDto dlvryClosureSaveDto,
			DlvryClosureEventDto dlvryClosureEventDto) {
		log.debug("Entering method reteriveDlvryClosureEvent in ServDelCloseSaveServiceImpl");
		long idStage = dlvryClosureSaveDto.getRgStageDto().getIdStage();
		dlvryClosureEventDto = dlvryClosureEventDao.retrvEvent(idStage, SVC_CD_EVENT_TYPE_CLOSE);
		if (!ObjectUtils.isEmpty(dlvryClosureEventDto)) {
			dlvryClosureSaveDto.getRgPostEventDto().setIdEvent(dlvryClosureEventDto.getIdEvent());
			dlvryClosureSaveDto.setIdEvent(dlvryClosureEventDto.getIdEvent());
			dlvryClosureSaveDto.getRgPostEventDto().setIdEvent(dlvryClosureEventDto.getIdEvent());
			dlvryClosureSaveDto.getRgPostEventDto().setLastUpdate(dlvryClosureEventDto.getLastUpdate());

		}

		log.debug("Exiting method reteriveDlvryClosureEvent in ServDelCloseSaveServiceImpl");
	}

	/**
	 * Method Name: updateTodoDetails Method Description:This method Saves the
	 * CINV43D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	@Override
	public void updateTodoDetails(DlvryClosureSaveDto dlvryClosureSaveDto) {
		log.debug("Entering method updateTodoDetails in ServDelCloseSaveServiceImpl");
		TodoUpdDtTodoCompletedInDto todoUpdDtTodoCompletedInDto = new TodoUpdDtTodoCompletedInDto();
		todoUpdDtTodoCompletedInDto.setIdEvent(dlvryClosureSaveDto.getIdEvent());
		todoUpdDtTodoCompletedDao.updateTODOEvent(todoUpdDtTodoCompletedInDto);
		log.debug("Exiting method updateTodoDetails in ServDelCloseSaveServiceImpl");
	}

	/**
	 * Method Name: postEvent Method Description:This method Saves the event
	 * Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	private void postEvent(DlvryClosureSaveDto dlvryClosureSaveDto) {
		log.debug("Entering method CallPostEvent in ServDelCloseSaveServiceImpl");
		PostEventStageStatusInDto postEventStageStatusInDto = new PostEventStageStatusInDto();
		if (dlvryClosureSaveDto.getRgPostEventDto().getIdEvent() != 0) {
			postEventStageStatusInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			postEventStageStatusInDto.setIdEvent(dlvryClosureSaveDto.getRgPostEventDto().getIdEvent());
			postEventStageStatusInDto.setIdStage(dlvryClosureSaveDto.getRgPostEventDto().getIdStage());
			postEventStageStatusInDto.setIdPerson(dlvryClosureSaveDto.getRgPostEventDto().getIdPerson());
			postEventStageStatusInDto.setCdTask(dlvryClosureSaveDto.getRgPostEventDto().getCdTask());
			postEventStageStatusInDto.setCdEventType(dlvryClosureSaveDto.getRgPostEventDto().getCdEventType());
			postEventStageStatusInDto.setDtEventOccurred(dlvryClosureSaveDto.getRgPostEventDto().getDtEventOccurred());
			postEventStageStatusInDto.setEventDescr(dlvryClosureSaveDto.getRgPostEventDto().getEventDescr());
			postEventStageStatusInDto.setCdEventStatus(dlvryClosureSaveDto.getRgPostEventDto().getCdEventStatus());
			postEventStageStatusInDto.setDtEventLastUpdate(dlvryClosureSaveDto.getRgPostEventDto().getLastUpdate());
			postEventStageStatusService.callPostEventStageStatusService(postEventStageStatusInDto);
		}

	}

	@SuppressWarnings("unused")
	@Override
	public ServiceDlvryClosureSaveSubmitRes saveAndSubmitDlvryClosure(DlvryClosureSaveDto dlvryClosureSaveDto,
			ServiceDlvryClosureReq serviceDlvryClosureReq, DlvryClosureValidationDto dlvryClosureValidationDto) {

		List<Integer> errorCodeList = new ArrayList<>();
		ServiceDlvryClosureSaveSubmitRes serviceDlvryClosureSaveSubmitRes = new ServiceDlvryClosureSaveSubmitRes();
		DlvryClosureEventDto dlvryClosureEventDto = saveDlvryClosureService(dlvryClosureSaveDto);
		if (!ObjectUtils.isEmpty(dlvryClosureEventDto.getErrorDto())) {
			serviceDlvryClosureSaveSubmitRes.setErrorDto(dlvryClosureEventDto.getErrorDto());
			return serviceDlvryClosureSaveSubmitRes;
		}
		HashMap<String, String> hashMapFDTC = new HashMap<String, String>();
		String szPersonFullName = null;
		boolean bStartsFDTCFlag = false;
		boolean isOpenPCSP = false;
		boolean isPCSPTransferred = false;
		String szmostRecentFDTCSubType = null;
		Long idFDTCStage = null;
		HashMap<String, String> personInFDTCEdit = new HashMap<>();
		ServiceDlvryClosureDto serviceDlvryClosureDto = dlvryClosureService.dlvryClosureService(serviceDlvryClosureReq);
		serviceDlvryClosureSaveSubmitRes.setLastUpdate(serviceDlvryClosureDto.getRgStageDto().getLastUpdate());
		serviceDlvryClosureSaveSubmitRes.setClosureNoticeList(serviceDlvryClosureDto.getClosureNotificationlist());
		if (!ObjectUtils.isEmpty(serviceDlvryClosureDto.getChrPersonIdList())) {
			errorCodeList.add(MSG_CRML_HIST_CHECK_STAGE_APRVL);
		}
		
		boolean validationResult = validationForSexualVctmizationQues(serviceDlvryClosureReq);
		if(!validationResult) {
			errorCodeList.add(MSG_COMPLETE_SEX_VIC_QN);
		}
		
		if (CodesConstant.CSTAGES_FPR.equalsIgnoreCase(dlvryClosureSaveDto.getCdStage())) {
			HashMap<Integer, String> personFDTCMap = dlvryClosureSubmitService
					.getPersonIdInFDTC(serviceDlvryClosureReq.getIdCase());
			Set<Map.Entry<Integer, String>> personFDTCMapEntrySet = personFDTCMap.entrySet();
			for (Map.Entry<Integer, String> personFDTCMapEntry : personFDTCMapEntrySet) {
				Long personId = new Long(personFDTCMapEntry.getKey());
				hashMapFDTC = dlvryClosureSubmitService.getMostRecentFDTCSubtype(personId);
				szPersonFullName = personFDTCMapEntry.getValue();
				if (!ObjectUtils.isEmpty(hashMapFDTC)) {
					szmostRecentFDTCSubType = (String) hashMapFDTC.get("CD_LEGAL_ACT_ACTN_SUBTYPE");
					idFDTCStage = Long.parseLong((String) hashMapFDTC.get("ID_EVENT_STAGE"));
				}
				if (CodesConstant.CFDT_010.equalsIgnoreCase(szmostRecentFDTCSubType)) {
					bStartsFDTCFlag = true;
					personInFDTCEdit.put(personId.toString(), szPersonFullName);
				} else if (CodesConstant.CFDT_030.equalsIgnoreCase(szmostRecentFDTCSubType)
						&& !serviceDlvryClosureDto.getRgStageDto().getIdStage().equals(idFDTCStage)) {
					bStartsFDTCFlag = true;
					personInFDTCEdit.put(personId.toString(), szPersonFullName);
				}
			}
		}
		if (CodesConstant.CSTAGES_FPR.equals(serviceDlvryClosureDto.getRgStageDto().getCdStage())
				&& CodesConstant.CPGRMS_CPS.equals(serviceDlvryClosureDto.getRgStageDto().getCdStageProgram())) {
			List<PcspDto> pcspList = serviceDlvryClosureDto.getPcspListDto();
			if (!ObjectUtils.isEmpty(pcspList)) {

				for (PcspDto openPCSPRow : pcspList) {
					String pcspStatus = openPCSPRow.getCdStatus();
					if (openPCSPRow.getDtEnd() == null || ServiceConstants.MAX_DATE.equals(openPCSPRow.getDtEnd())) {
						if (pcspStatus == null || CodesConstant.CPCSPSTA_PEDS.equals(pcspStatus)
								|| CodesConstant.CPCSPSTA_DTPE.equals(pcspStatus)
								|| CodesConstant.CPCSPSTA_CIPC.equals(pcspStatus)) {
							isOpenPCSP = true;
						} else if (CodesConstant.CPCSPSTA_CPCT.equals(pcspStatus)) {
							isPCSPTransferred = true;
						}
					}
				}
			}
		}

		boolean bpersMPSSearchReq = false;
		bpersMPSSearchReq = personMPSService.isStagePersReltd(serviceDlvryClosureDto.getRgStageDto().getIdStage(),
				ServiceConstants.Y);

		boolean bGuardDetOutcomeNull = false;

		String stage = serviceDlvryClosureDto.getRgStageDto().getCdStage();

		if (stage.equals(SVC_CD_STAGE_APS_SVC)) {
			bGuardDetOutcomeNull = validateGuardianDetail(serviceDlvryClosureDto.getRgStageDto().getIdCase());
		}

		boolean crimHistPending = false;

		if (contactDetailsService.isCrimHistCheckPending(serviceDlvryClosureDto.getRgStageDto().getIdStage())) {
			crimHistPending = true;
		}
		if ((serviceDlvryClosureDto != null && !ObjectUtils.isEmpty(serviceDlvryClosureDto.getChrPersonIdList()))
				|| bpersMPSSearchReq || bGuardDetOutcomeNull || isOpenPCSP || isPCSPTransferred || crimHistPending
				|| !(ObjectUtils.isEmpty(dlvryClosureEventDto.getAttentionMsgMap()))) {
			if (!ObjectUtils.isEmpty(serviceDlvryClosureDto.getRgPostEventDto().getIdPerson())) {
				HashMap personDetail = new HashMap();
				personDetail = criminalHistoryDao
						.checkCrimHistAction(serviceDlvryClosureDto.getRgStageDto().getIdStage());
				if (!ObjectUtils.isEmpty(personDetail)) {
					String nmCase = (String) personDetail.get(ServiceConstants.NAMEPERSON);
					serviceDlvryClosureSaveSubmitRes.setPersonName(nmCase);
					//Added for warranty defect 11009
					serviceDlvryClosureSaveSubmitRes.setPersonId((Long)personDetail.get(ServiceConstants.IDPERSON));
				}
			}
			if (bpersMPSSearchReq) {
				errorCodeList.add(Messages.MSG_MPS_PERSON_NOT_SEARCHED);
			}
			if (bGuardDetOutcomeNull) {
				errorCodeList.add(Messages.MSG_GUARD_FINAL_OUTCOME_REQ);
			}
			if (bStartsFDTCFlag) {
				errorCodeList.add(Messages.MSG_ERR_FDTC_CLOSURE);
			}
			if (isOpenPCSP) {
				errorCodeList.add(Messages.MSG_OPEN_PCSP_AT_STG_CLOSURE);
			}
			if (isPCSPTransferred) {
				errorCodeList.add(Messages.MSG_PCSP_TRANSFER_TO_FBSS);
			}
			if (crimHistPending) {
				errorCodeList.add(Messages.MSG_CRML_HIST_CHECK_STAGE_PEND);
			}

			if (dlvryClosureEventDto.getAttentionMsgMap().size() > 0) {
				Set<Entry<String, Integer>> pcspErrors = dlvryClosureEventDto.getAttentionMsgMap().entrySet();
				for (Entry<String, Integer> entry : pcspErrors) {
					errorCodeList.add(entry.getValue());
				}
			}
		} else {
			if (bStartsFDTCFlag) {
				errorCodeList.add(Messages.MSG_ERROR_FDTC_CLOSURE);
			} else {
				DlvryClosurevalidationRes dlvryClosurevalidationRes = dlvryClosureValidationService
						.callDlvryClosureValidation(dlvryClosureValidationDto);
				serviceDlvryClosureSaveSubmitRes.setEventIdList(dlvryClosurevalidationRes.getIdEvent());
				if (!ObjectUtils.isEmpty(dlvryClosurevalidationRes.getErrorList())) {
					for (ErrorDto errorDto : dlvryClosurevalidationRes.getErrorList()) {
						int code = Integer.parseInt(errorDto.getErrorMsg());
						errorCodeList.add(code);
					}
				}
			}
		}

		if ((ServiceConstants.CCFPCLOS_05.equals(dlvryClosureSaveDto.getRgStageDto().getCdStageReasonClosed()))
				|| (ServiceConstants.CCFPCLOS_15.equals(dlvryClosureSaveDto.getRgStageDto().getCdStageReasonClosed()))
				|| (ServiceConstants.CCFPCLOS_20.equals(dlvryClosureSaveDto.getRgStageDto().getCdStageReasonClosed()))
				|| (ServiceConstants.CCFPCLOS_26.equals(dlvryClosureSaveDto.getRgStageDto().getCdStageReasonClosed()))
				|| (ServiceConstants.CCFPCLOS_46
						.equals(dlvryClosureSaveDto.getRgStageDto().getCdStageReasonClosed()))) {
			boolean sDMAssessment = dlvryClosureSubmitService
					.getSDMAssessmentStatus(dlvryClosureSaveDto.getRgStageDto().getIdStage());
			if (!sDMAssessment) {
				errorCodeList.add(MSG_SDM_ASSESSMENT_STATUS);
			}
		}
		serviceDlvryClosureSaveSubmitRes.setDlvryClosureEventId(dlvryClosureEventDto.getIdEvent());
		serviceDlvryClosureSaveSubmitRes.setErrorList(errorCodeList);
		return serviceDlvryClosureSaveSubmitRes;
	}

	/**
	 * This method queries to find if there are guardianship records of type DAD
	 * with null final outcome.
	 *
	 * @param ulIdCase
	 * @return bIsGuardianDtlNull
	 */
	private boolean validateGuardianDetail(Long idCase) {

		return guardianshipDtlService.isDADFinalOutcomeDocumented(idCase);
	}

	/**
	 * 
	 * Method Name: validationForSexualVctmizationQues Method Description: this method check
	 * if all the PRN less then 18 have answered Sexual Vctmization Question
	 * 
	 * @param ServiceDlvryClosureReq
	 * @return boolean
	 */
	
	private boolean validationForSexualVctmizationQues(ServiceDlvryClosureReq serviceDlvryClosureReq) {
		int personage = ServiceConstants.Zero_INT;
		String stage = serviceDlvryClosureReq.getCdStage();
		Date dtDtSystemDate = new Date();
		if(stage.equals(FPR_STAGE) ) {
			long stageid = serviceDlvryClosureReq.getIdStage(); 
			List<ContactPrincipalsCollateralsDto> listPpl = personListByStageDao.getPRNPersonDetailsForStage(stageid, ServiceConstants.PRINCIPAL);

			if(!ObjectUtils.isEmpty(listPpl)) {

				for(ContactPrincipalsCollateralsDto dto:listPpl) {
					ContactPrincipalsCollateralsDto person = dto;
					 personage = DateUtils.calculatePersonsAgeInYears(dto.getDtPersonBirth(),dtDtSystemDate);
					if( personage  < ServiceConstants.MIN_PG_AGE) {
						boolean answered = personListByStageDao.getIndChildSxVctmzinHistory(person.getIdPerson());
						if(!answered) {
							return false;
						}
					}	
				}

			}
			
		
		}
		return true;
	}
}
