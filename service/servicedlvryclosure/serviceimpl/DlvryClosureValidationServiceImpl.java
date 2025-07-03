package us.tx.state.dfps.service.servicedlvryclosure.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.EventStageTypeStatusDao;
import us.tx.state.dfps.service.admin.dao.EventStageTypeTaskDao;
import us.tx.state.dfps.service.admin.dao.StageCdDao;
import us.tx.state.dfps.service.admin.dao.StageSituationDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthDetailNameDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusOutDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskOutDto;
import us.tx.state.dfps.service.admin.dto.StageCdInDto;
import us.tx.state.dfps.service.admin.dto.StageCdOutDto;
import us.tx.state.dfps.service.admin.dto.StageSituationInDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.UpdtFacilAllegMultiDtlReq;
import us.tx.state.dfps.service.common.response.DlvryClosurevalidationRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.investigation.dao.FacilAllgDtlDao;
import us.tx.state.dfps.service.investigation.dto.FacilAllegDetailDto;
import us.tx.state.dfps.service.servicedlvryclosure.dao.ServiceDlvryClosureDao;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureValidationService;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.GuardianshipDto;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureApproversDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureValidationDto;
import us.tx.state.service.servicedlvryclosure.dto.OutcomeMatrixDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: csvc16s
 * tuxedo service converted to rest for service closure validation May 14, 2018-
 * 9:44:17 AM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class DlvryClosureValidationServiceImpl implements DlvryClosureValidationService {

	private static final String PEND = "PEND";
	private static final String DCR = "DCR";
	private static final String S = "S";
	private static final String DAD = "DAD";
	private static final String CON = "CON";
	private static final String GUA = "GUA";
	private static final String APS = "APS";
	private static final String INV = "INV";
	private static final String BCLS = "BCLS";
	private static final String ARC_SUCCESS = "ARC_SUCCESS";
	private static final int MSG_OPEN_INV_STAGE_FOUND = 55066;
	private static final int MSG_SVC_PROB_ACTION_OUTCOME = 5044;
	private static final int MSG_SVC_CLOSE_SUMMARY = 5041;
	private static final int MSG_SVC_RECORD_DEATH = 5043;
	private static final int MSG_DAY_CARE_REQ_PEND = 56136;
	private static final int MSG_SVC_EVENT_PENDING = 5071;
	private static final String CVS_REMOVAL_EVENT_TYPE = "REM";
	private static final int MSG_SVA_OPN_AUTHS = 8335;
	private static final int MSG_SVA_CRSR_OPN_AUTHS = 55192;
	private static final int MSG_INV_SVC_RFRL_CHKLST_WARNING = 4133;
	private static final int MSG_NO_ROWS_RETURNED = 9005;
	public static final int MSG_OPEN_SUBCARE_STAGE = 8226;

	@Autowired
	StageEventStatusCommonService stageEventCommonService;

	@Autowired
	ServiceDlvryClosureDao dlvryClosureValidationDao;

	@Autowired
	StageSituationDao stageSituationDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	SvcAuthEventLinkDao serviceAuthEventLinkDao;

	@Autowired
	EventStageTypeStatusDao eventStageTypeStatusDao;

	@Autowired
	StageCdDao stageCdDao;

	@Autowired
	FacilAllgDtlDao facilAllgDtlDao;

	@Autowired
	EventStageTypeTaskDao eventStageTypeTaskDao;

	@Autowired
	CloseStageCaseService closeStageCaseService;

	@Autowired
	StageDao stageDao;

	@Autowired
	SvcAuthDetailNameDao svcAuthDetailNameDao;

	@Autowired
	EventStagePersonLinkInsUpdDao objCcmn87dDao;

	/**
	 * Method Name: callDlvryClosureValidation Method Description: method for
	 * csvc16s tuxedo service validations for cps program and FRS stage
	 * 
	 * @param closureValidationDto
	 * @return DlvryClosurevalidationRes
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DlvryClosurevalidationRes callDlvryClosureValidation(DlvryClosureValidationDto closureValidationDto) {
		DlvryClosurevalidationRes dlvryClosurevalidationRes = new DlvryClosurevalidationRes();
		short returnValue = 0;
		Boolean isAPS = true;
		Boolean isCPS = true;
		StageTaskInDto stageTaskInDto = new StageTaskInDto();
		List<ErrorDto> errorDtoList = new ArrayList<>();
		stageTaskInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		stageTaskInDto.setIdStage(closureValidationDto.getIdStage());
		stageTaskInDto.setCdTask(closureValidationDto.getRgPostEventDto().getCdTask());
		String eventStatus = stageEventCommonService.checkStageEventStatus(stageTaskInDto);

		EventStageTypeStatusInDto eventStageTypeStatusInDto = new EventStageTypeStatusInDto();
		eventStageTypeStatusInDto.setIdStage(closureValidationDto.getRgStageDto().getIdStage());
		eventStageTypeStatusInDto.setCdEventStatus(closureValidationDto.getRgPostEventDto().getCdEventStatus());
		eventStageTypeStatusInDto.setCdEventType(closureValidationDto.getRgPostEventDto().getCdEventType());

		List<EventDto> eventList = null;
		if (ARC_SUCCESS.equalsIgnoreCase(eventStatus)) {
			returnValue = ServiceConstants.FIND_SUCCESS;
		}
		if (ServiceConstants.FIND_SUCCESS == returnValue) {
			/*
			 * SVC_CD_STAGE_CPS_SVC = "FPR". If the stage being closed is FPR,
			 * then we are dealing with CPS program.
			 */
			if (ServiceConstants.SVC_CD_STAGE_CPS_SVC.equals(closureValidationDto.getCdStage())) {
				isAPS = ServiceConstants.FALSEVAL;
			} else {
				isCPS = ServiceConstants.FALSEVAL;
			}
			if (ServiceConstants.SVC_CD_STAGE_TYPE_CGUA.equals(closureValidationDto.getRgStageDto().getCdStageType())) {
				// CallCSVC49D
				dlvryClosureValidationDao.getGuardianshipStatus(closureValidationDto.getRgStageDto().getIdCase());
				// need to throw service error
			}
			if ((ServiceConstants.SVC_CD_STAGE_TYPE_CONTRACTED
					.equals(closureValidationDto.getRgStageDto().getCdStageType()))
					|| (ServiceConstants.SVC_CD_STAGE_TYPE_CON_INTENSIVE
							.equals(closureValidationDto.getRgStageDto().getCdStageType()))
					|| (ServiceConstants.SVC_CD_STAGE_TYPE_CON_MODERATE
							.equals(closureValidationDto.getRgStageDto().getCdStageType()))) {
				if (ServiceConstants.SVC_CLOSE_CLIENT_DIED
						.equals(closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
					int cnt = dlvryClosureValidationDao
							.getPersonCount(closureValidationDto.getRgStageDto().getIdStage());
					if (cnt != 0) {
						createErrorDtoList(errorDtoList, String.valueOf(MSG_SVC_RECORD_DEATH));
					}
					// poutmsg
				}
			} else if ((!(!ObjectUtils.isEmpty(closureValidationDto.getRgStageDto().getCdStageType())
					&& ServiceConstants.SVC_CD_STAGE_TYPE_CRSR
							.equals(closureValidationDto.getRgStageDto().getCdStageType().substring(0, 2))))
					&& !ServiceConstants.SVC_CLOSE_WKLD_CONSTRAINTS
							.equals(closureValidationDto.getRgStageDto().getCdStageReasonClosed())
					&& !ServiceConstants.SVC_CLOSE_ADMIN_CLOSURE_CPS
							.equals(closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
				if (isCPS) {
					// 19d
					String eventSts = dlvryClosureValidationDao.getEventStatus(closureValidationDto, BCLS);
					if ((StringUtils.isEmpty(eventSts)) && (!ServiceConstants.SVC_CD_STAGE_TYPE_CREG
							.equals(closureValidationDto.getRgStageDto().getCdStageType())
							&& !ServiceConstants.SVC_CD_STAGE_TYPE_CGUA
									.equals(closureValidationDto.getRgStageDto().getCdStageType()))) {
						createErrorDtoList(errorDtoList, String.valueOf(MSG_SVC_CLOSE_SUMMARY));
					}

					String recDeath = Character.toString(
							closureValidationDto.getCdEditProcess().charAt(ServiceConstants.SVC_EDIT_RECORD_DEATH));
					if (ServiceConstants.Y.equals(recDeath)) {
						// 20d
						int cnt = dlvryClosureValidationDao
								.getPersonCount(closureValidationDto.getRgStageDto().getIdStage());
						if (cnt != 0) {
							createErrorDtoList(errorDtoList, String.valueOf(MSG_SVC_RECORD_DEATH));
						}
					}
				}
				if (isAPS) {
					/*
					 * Verify that there are no open INV stages, which can
					 * happen with Case Merge. If there are, the worker cannot
					 * close the SVC stage before closing the INV stage.
					 */
					StageSituationInDto stageSituationInDto = closureValidationDto.getStageSituationInDto();
					if(stageSituationInDto !=null){
						List<StageSituationOutDto> stageSituationDaoList = stageSituationDao
								.getStageDetails(stageSituationInDto);
						Calendar calendar = Calendar.getInstance();
						boolean bOpenInvStageFound = true;
						for (StageSituationOutDto dto : stageSituationDaoList) {
							calendar.setTime(dto.getDtStageClose());
							if ((ObjectUtils.isEmpty(dto.getDtStageClose())
									|| ServiceConstants.ARC_MAX_YEAR.equals(calendar.get(Calendar.YEAR)))
									&& dto.getCdStage().equals(INV)
									&& !dto.getIdStage().equals(closureValidationDto.getIdStage()) && bOpenInvStageFound) {
								bOpenInvStageFound = ServiceConstants.TRUE_VALUE;
								createErrorDtoList(errorDtoList, String.valueOf(MSG_OPEN_INV_STAGE_FOUND));
							}
						}
					}
					boolean indGUAFound = ServiceConstants.FALSEVAL;
					// CLSCB6D
					List<GuardianshipDto> guardianshipList = dlvryClosureValidationDao.guardianshipList(GUA,
							closureValidationDto.getIdStage());
					for (GuardianshipDto Guardianship : guardianshipList) {
						if (Guardianship.getCdGuardType().equals(APS) || Guardianship.getCdGuardType().equals(CON)
								|| Guardianship.getCdGuardType().equals(DAD)) {
							indGUAFound = ServiceConstants.TRUE_VALUE;
						}
					}

					String aomOut = Character.toString(closureValidationDto.getCdEditProcess()
							.charAt(ServiceConstants.SVC_EDIT_AOM_ACTION_OUTCOME));
					String aomAction = Character.toString(
							closureValidationDto.getCdEditProcess().charAt(ServiceConstants.SVC_EDIT_AOM_ACTION));
					String clientDied = Character.toString(
							closureValidationDto.getCdEditProcess().charAt(ServiceConstants.SVC_EDIT_CLIENT_DIED));
					String noResources = Character.toString(
							closureValidationDto.getCdEditProcess().charAt(ServiceConstants.SVC_EDIT_NO_RESOURCES));
					String agenctRefrl = Character.toString(
							closureValidationDto.getCdEditProcess().charAt(ServiceConstants.SVC_EDIT_AGENCY_REFERRAL));
					String unableLoc = Character.toString(
							closureValidationDto.getCdEditProcess().charAt(ServiceConstants.SVC_EDIT_UNABLE_LOCATE));
					// 87d
					if (!indGUAFound && (ServiceConstants.Y.equals(aomOut)) || (ServiceConstants.Y.equals(aomAction))
							|| (ServiceConstants.Y.equals(clientDied)) || (ServiceConstants.Y.equals(noResources))
							|| (ServiceConstants.Y.equals(agenctRefrl)) || (ServiceConstants.Y.equals(unableLoc))) {
						// 87d
						EventStagePersonLinkInsUpdInDto eventReq = new EventStagePersonLinkInsUpdInDto();
						eventReq.setCdEventType(ServiceConstants.SVC_CD_EVENT_TYPE_AOM);
						List<EventStagePersonLinkInsUpdOutDto> eventDtls = callCCMN87D(eventReq, closureValidationDto);

						Long idEvent = 0l;
						if (!CollectionUtils.isEmpty(eventDtls))
							idEvent = eventDtls.get(0).getIdEvent();
						/*
						 ** Look for Outcome Matrix action and outcome in Serv
						 * Delivery Stage and perform edit checks according to
						 * the reason for closure. SIR 23530 - use CINV96D
						 * instead of CSVC09D
						 */
						List<OutcomeMatrixDto> outComeMatrixDtos = dlvryClosureValidationDao
								.getOutcomeMatrixDetails(idEvent, S);

						String val = Character.toString(closureValidationDto.getCdEditProcess()
								.charAt(ServiceConstants.SVC_EDIT_AOM_ACTION_OUTCOME));
						if (ServiceConstants.Y.equals(val)) {
							for (OutcomeMatrixDto matrixDto : outComeMatrixDtos) {
								if (!closureValidationDto.getRgStageDto().getCdStageType()
										.equals(ServiceConstants.SVC_CD_STAGE_TYPE_CREG)
										|| !closureValidationDto.getRgStageDto().getCdStageType()
												.equals(ServiceConstants.SVC_CD_STAGE_TYPE_CGUA)) {

									if (ObjectUtils.isEmpty(matrixDto.getCdApsOutcomeResult())) {
										createErrorDtoList(errorDtoList, String.valueOf(MSG_SVC_PROB_ACTION_OUTCOME));
									}
								}
							}

						}

					}
				} // end of aps
			} // end else

			/*
			 * If dam returns at a row then set error message it indicates that
			 * there is a pending Day Care Request for this stage. Hence, set
			 * error message to prevent the user from submitting the stage for
			 * closure.
			 */
			List<EventStageTypeStatusOutDto> eventStageList = eventStageTypeStatusDao
					.getEventStatusForDayCare(eventStageTypeStatusInDto, DCR, PEND);
			if (!ObjectUtils.isEmpty(eventStageList)) {
				for (EventStageTypeStatusOutDto eventStageDto : eventStageList) {
					if (!ObjectUtils.isEmpty(eventStageDto.getCdEventStatus())) {
						createErrorDtoList(errorDtoList, String.valueOf(MSG_DAY_CARE_REQ_PEND));
					}
				}
			}
			serviceAuth(closureValidationDto, errorDtoList);
			String removalEdit = Character
					.toString(closureValidationDto.getCdEditProcess().charAt(ServiceConstants.CVS_REMOVAL_EDIT));
			if (ServiceConstants.Y.equals(removalEdit)) {
				// 87d
				EventStagePersonLinkInsUpdInDto eventReq = new EventStagePersonLinkInsUpdInDto();
				eventReq.setCdEventType(CVS_REMOVAL_EVENT_TYPE);
				List<EventStagePersonLinkInsUpdOutDto> eventDtls = callCCMN87D(eventReq, closureValidationDto);
				if (ObjectUtils.isEmpty(eventDtls)) {
					createErrorDtoList(errorDtoList, String.valueOf(MSG_OPEN_SUBCARE_STAGE));
				}
			}
			/*
			 ** If no warnings posted, prepare to close Service Delivery or
			 * submit for approval
			 */
			if (ObjectUtils.isEmpty(errorDtoList)) {
				if (ServiceConstants.ACTION_CODE_SUBMIT.equals(closureValidationDto.getCdReqFunction())) {
					eventList = callCSVC28D(closureValidationDto, ServiceConstants.SVC_CD_EVENT_STATUS_PENDING,
							dlvryClosurevalidationRes);

					if (!CollectionUtils.isEmpty(eventList)) {
						createErrorDtoList(errorDtoList, String.valueOf(MSG_SVC_EVENT_PENDING));
					} else if (isAPS) {
						if(closureValidationDto.getOutcomeMatrixDto() == null){
							closureValidationDto.setOutcomeMatrixDto(new OutcomeMatrixDto());
						}
						if ((ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_CLIENT_DIED,
								closureValidationDto.getRgStageDto().getCdStageReasonClosed()))
								|| (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_UNABLE_TO_LOCATE,
										closureValidationDto.getRgStageDto().getCdStageReasonClosed()))
								|| (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_CLIENT_WITHDREW,
										closureValidationDto.getRgStageDto().getCdStageReasonClosed()))
								|| (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_ADMIN_CLOSURE,
										closureValidationDto.getRgStageDto().getCdStageReasonClosed()))) {
							if (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_CLIENT_DIED,
									closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
								closureValidationDto.getOutcomeMatrixDto()
										.setCdApsOutcomeResult(ServiceConstants.SVC_CLOSE_CLIENT_DIED);
							}
							if (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_UNABLE_TO_LOCATE,
									closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
								closureValidationDto.getOutcomeMatrixDto()
										.setCdApsOutcomeResult(ServiceConstants.SVC_CLOSE_UNABLE_TO_LOCATE);
							}
							if (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_CLIENT_WITHDREW,
									closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
								closureValidationDto.getOutcomeMatrixDto()
										.setCdApsOutcomeResult(ServiceConstants.SVC_CLOSE_CLIENT_WITHDREW);
							}
							if (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_ADMIN_CLOSURE,
									closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
								closureValidationDto.getOutcomeMatrixDto()
										.setCdApsOutcomeResult(ServiceConstants.SVC_CLOSE_ADMIN_CLOSURE);
							}
							closureValidationDto.getOutcomeMatrixDto().setDtApsOutcomeRecord(null);
							// 26D
							boolean indMatrixUpdated = dlvryClosureValidationDao
									.updateOutcomeMatrix(closureValidationDto, ServiceConstants.REQ_FUNC_CD_UPDATE);

							if (!indMatrixUpdated) {
								ErrorDto errorDto = new ErrorDto();
								errorDto.setErrorCode(Messages.MSG_CMN_UPDATE_FAILED);
								dlvryClosurevalidationRes.setErrorDto(errorDto);
								return dlvryClosurevalidationRes;
							}
							if ((ArrayUtils.isEquals(ServiceConstants.SVC_CD_STAGE_TYPE_CREG,
									closureValidationDto.getRgStageDto().getCdStageType()))
									&& (ArrayUtils.isEquals(ServiceConstants.SVC_CD_STAGE_TYPE_CGUA,
											closureValidationDto.getRgStageDto().getCdStageType()))) {
								// 27D
								closureValidationDto.getRgPostEventDto()
										.setCdEventType(ServiceConstants.SVC_CD_EVENT_TYPE_AOM);
								boolean indEventUpdated = dlvryClosureValidationDao.updateEvent(closureValidationDto,
										ServiceConstants.REQ_FUNC_CD_UPDATE);
								if (!indEventUpdated) {
									ErrorDto errorDto = new ErrorDto();
									errorDto.setErrorCode(Messages.MSG_CMN_UPDATE_FAILED);
									dlvryClosurevalidationRes.setErrorDto(errorDto);
									return dlvryClosurevalidationRes;
								}
							}
						}
					} // end if APS
					/*
					 ** Closing an FPR stage. Call the DAM to make sure at least
					 * one Contact Monthly Summary has an event status of
					 * Approved.
					 */
					if (isCPS) {
						/*
						 * Verify that the worker has completed the Services and
						 * Referrals Checklist before allowing them to close the
						 * FPR stage.
						 */
						EventStageTypeTaskInDto eventStageTypeTaskInDto = new EventStageTypeTaskInDto();
						eventStageTypeTaskInDto.setIdStage(closureValidationDto.getRgStageDto().getIdStage());
						eventStageTypeTaskInDto.setCdEventType(ServiceConstants.SVC_REF_CHKLST_EVENT_TYPE);
						eventStageTypeTaskInDto.setCdTask(ServiceConstants.SVC_REF_CHKLST_FPR_TASK);
						// CallCSESA3D
						List<EventStageTypeTaskOutDto> eventTaskList = eventStageTypeTaskDao
								.getEventDtls(eventStageTypeTaskInDto);
						/*
						 ** If the status of the event is not COMP, the user must
						 * complete the Services and Referrals Checklist before
						 * saving. Post an edit message.
						 */
						if (ObjectUtils.isEmpty(eventTaskList)) {
							/*
							 ** The event does not exist, so post an edit message
							 * informing the user they must complete the
							 * Services and Referrals Checklist before closing.
							 */
							createErrorDtoList(errorDtoList, String.valueOf(MSG_INV_SVC_RFRL_CHKLST_WARNING));
						}
						for (EventStageTypeTaskOutDto eventTaskDto : eventTaskList) {
							if ((!ArrayUtils.isEquals(eventTaskDto.getCdEventStatus(), ServiceConstants.COMPLETE))
									&& (!ArrayUtils.isEquals(eventTaskDto.getCdEventStatus(),
											ServiceConstants.PENDING)
											&& (!ServiceConstants.APPROVED.equalsIgnoreCase(eventTaskDto.getCdEventStatus())))) {
								createErrorDtoList(errorDtoList, String.valueOf(MSG_INV_SVC_RFRL_CHKLST_WARNING));

							}
						}
					} // END: ISCPS
					/* If no errors found, continue with save and submit */
					if (CollectionUtils.isEmpty(errorDtoList)) {
						/*
						 ** If there are no events set to Pending, retrieve all
						 * events with a status of Complete to be submitted for
						 * approval
						 */
						eventList = callCSVC28D(closureValidationDto, ServiceConstants.SVC_CD_EVENT_STATUS_COMPLETE,
								dlvryClosurevalidationRes);
						if (CollectionUtils.isEmpty(eventList)) {
							createErrorDtoList(errorDtoList, String.valueOf(MSG_NO_ROWS_RETURNED));
						}
						callCSVC28D(closureValidationDto, ServiceConstants.SVC_CD_EVENT_STATUS_PROCESS,
								dlvryClosurevalidationRes);
					}
				} // end of save and submit
				if (ServiceConstants.ACTION_CODE_CLOSE.equals(closureValidationDto.getCdReqFunction())) {
					/* Check for specific reason codes for Closure */
					if ((ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_CLIENT_DIED,
							closureValidationDto.getRgStageDto().getCdStageReasonClosed()))
							|| (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_UNABLE_TO_LOCATE,
									closureValidationDto.getRgStageDto().getCdStageReasonClosed()))
							|| (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_CLIENT_WITHDREW,
									closureValidationDto.getRgStageDto().getCdStageReasonClosed()))
							|| (ArrayUtils.isEquals(ServiceConstants.SVC_CLOSE_ADMIN_CLOSURE,
									closureValidationDto.getRgStageDto().getCdStageReasonClosed()))) {
						/*
						 * Update outcomes of AOM based on the reason code given
						 */
						// CallCSVC26D
						boolean indMatrixUpdated = callCSVC26D(closureValidationDto);
						if (!indMatrixUpdated) {
							ErrorDto errorDto = new ErrorDto();
							errorDto.setErrorCode(Messages.MSG_CMN_UPDATE_FAILED);
							dlvryClosurevalidationRes.setErrorDto(errorDto);
							return dlvryClosurevalidationRes;
						}
						if ((ArrayUtils.isEquals(ServiceConstants.SVC_CD_STAGE_TYPE_CREG,
								closureValidationDto.getRgStageDto().getCdStageType()))
								&& (ArrayUtils.isEquals(ServiceConstants.SVC_CD_STAGE_TYPE_CGUA,
										closureValidationDto.getRgStageDto().getCdStageType()))) {
							boolean indEventUpdated = callCSVC27D(closureValidationDto);
							if (!indEventUpdated) {
								ErrorDto errorDto = new ErrorDto();
								errorDto.setErrorCode(Messages.MSG_CMN_UPDATE_FAILED);
								dlvryClosurevalidationRes.setErrorDto(errorDto);
								return dlvryClosurevalidationRes;
							}
						}
					} // end if reason codes

					/*
					 ** Added a call to the CallCSVC28D function to retrieve all
					 ** events with a status of COMPlete to be marked APRV
					 * (approved).
					 */
					eventList = callCSVC28D(closureValidationDto, ServiceConstants.SVC_CD_EVENT_STATUS_COMPLETE,
							dlvryClosurevalidationRes);
					if (CollectionUtils.isEmpty(eventList)) {
						ErrorDto errorDto = new ErrorDto();
						errorDto.setErrorCode(Messages.MSG_CMN_UPDATE_FAILED);
						dlvryClosurevalidationRes.setErrorDto(errorDto);
						return dlvryClosurevalidationRes;
					}
					/*
					 ** When Closing a stage or Submitting a Closure for
					 * Approval, all the IN PROGRESS "PROC" events should also
					 * be updated to approved "APRV" for closing and pending
					 * "PEND" for submitting. Added a second call to DAM CSVC28D
					 * pasing event status of IN PROGRESS instead of COMPLETE.
					 * Added row counter and Addition statement, to append the
					 * PROC event list to the COMP event list within the
					 * CALLCSVC28D function.
					 *****************************************************************/
					eventList = callCSVC28D(closureValidationDto, ServiceConstants.SVC_CD_EVENT_STATUS_PROCESS,
							dlvryClosurevalidationRes);
					if (CollectionUtils.isEmpty(eventList)) {
						ErrorDto errorDto = new ErrorDto();
						errorDto.setErrorCode(Messages.MSG_CMN_UPDATE_FAILED);
						dlvryClosurevalidationRes.setErrorDto(errorDto);
						return dlvryClosurevalidationRes;
					}
					/*
					 ** Close the Stage if "Save and Close" selected on client
					 */
					closeStage(closureValidationDto);
				}
			}
		}
		dlvryClosurevalidationRes.setErrorList(errorDtoList);
		return dlvryClosurevalidationRes;
	}

	/**
	 * Method Name: createErrorDtoList Method Description: to create error dto
	 * list
	 * 
	 * @param errorDtoList
	 * @param errorMsg
	 */
	private void createErrorDtoList(List<ErrorDto> errorDtoList, String errorMsg) {
		ErrorDto errorDto;
		errorDto = new ErrorDto();
		errorDto.setErrorMsg(errorMsg);
		errorDtoList.add(errorDto);
	}

	/**
	 * Method Name: callCSVC27D Method Description: call to update event status
	 * 
	 * @param closureValidationDto
	 * @return boolean
	 */
	private boolean callCSVC27D(DlvryClosureValidationDto closureValidationDto) {

		closureValidationDto.getRgPostEventDto().setCdEventStatus(ServiceConstants.SVC_CD_EVENT_STATUS_COMPLETE);
		closureValidationDto.getRgPostEventDto().setCdEventType(ServiceConstants.SVC_CD_EVENT_TYPE_AOM);
		return dlvryClosureValidationDao.updateEvent(closureValidationDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
	}

	/**
	 * Method Name: callCSVC26D Method Description: call to update the outcomme
	 * matrix dao
	 * 
	 * @param closureValidationDto
	 * @return boolean
	 */
	private boolean callCSVC26D(DlvryClosureValidationDto closureValidationDto) {

		/* Change numeric codes to alphanumeric ones to match the AOM codes */
		if (ServiceConstants.SVC_CLOSE_CLIENT_DIED
				.equals(closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
			closureValidationDto.getOutcomeMatrixDto().setCdApsOutcomeResult(ServiceConstants.SVC_CD_CLIENT_DIED);
		}
		if (ServiceConstants.SVC_CLOSE_UNABLE_TO_LOCATE
				.equals(closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
			closureValidationDto.getOutcomeMatrixDto().setCdApsOutcomeResult(ServiceConstants.SVC_CD_UNABLE_TO_LOCATE);
		}
		if (ServiceConstants.SVC_CLOSE_CLIENT_WITHDREW
				.equals(closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
			closureValidationDto.getOutcomeMatrixDto().setCdApsOutcomeResult(ServiceConstants.SVC_CD_CLIENT_WITHDREW);
		}
		if (ServiceConstants.SVC_CLOSE_ADMIN_CLOSURE
				.equals(closureValidationDto.getRgStageDto().getCdStageReasonClosed())) {
			closureValidationDto.getOutcomeMatrixDto().setCdApsOutcomeResult(ServiceConstants.SVC_CD_ADMIN_CLOSURE);
		}
		return dlvryClosureValidationDao.updateOutcomeMatrix(closureValidationDto, ServiceConstants.REQ_FUNC_CD_UPDATE);
	}

	/**
	 * Method Name: closeStage Method Description: To close stage
	 * 
	 * @param dlvryValidationDto
	 */
	public void closeStage(DlvryClosureValidationDto dlvryValidationDto) {
		/*
		 ** Update the status of all Service Delivery Events to Approved
		 */
		eventDao.getEventDetailsUpdate(dlvryValidationDto.getServiceReqHeaderDto(), dlvryValidationDto.getEventDto());
		/*
		 * Call CloseStageCase to Close both the Service Delivery Stage and the
		 * Case
		 */
		CloseStageCaseInputDto closeStageCaseInput = new CloseStageCaseInputDto();
		closeStageCaseInput.setCdStage(dlvryValidationDto.getCdStage());
		closeStageCaseInput.setCdStageProgram(dlvryValidationDto.getRgStageDto().getCdStageProgram());
		closeStageCaseInput.setCdStageReasonClosed(dlvryValidationDto.getRgStageDto().getCdStageReasonClosed());
		closeStageCaseInput.setIdStage(dlvryValidationDto.getRgStageDto().getIdStage());
		closeStageCaseInput.setEventDescr(dlvryValidationDto.getEventDto().getEventDescr());
		closeStageCaseService.closeStageCase(closeStageCaseInput);
	}

	/**
	 * Method Name: callCCMN87D Method Description: call for CCMN87D
	 * 
	 * @param pCCMN87DInputRec
	 * @param dlvryValDto
	 * @return
	 */
	public List<EventStagePersonLinkInsUpdOutDto> callCCMN87D(EventStagePersonLinkInsUpdInDto pCCMN87DInputRec,
			DlvryClosureValidationDto dlvryValDto) {
		String cdStage = dlvryValDto.getRgStageDto().getCdStage();
		Long idStage = dlvryValDto.getRgStageDto().getIdStage();
		if (CVS_REMOVAL_EVENT_TYPE.equals(pCCMN87DInputRec.getCdEventType())
				|| (ServiceConstants.SVC_AUTH_EVENT_TYPE.equals(pCCMN87DInputRec.getCdEventType())
						&& !cdStage.equals(ServiceConstants.SVC_CD_STAGE_CPS_SVC))) {
			// CallCCMNB6D
			UpdtFacilAllegMultiDtlReq updtFacilAllegMultiDtlReq = new UpdtFacilAllegMultiDtlReq();
			FacilAllegDetailDto facilAllegDetailDtoReq = new FacilAllegDetailDto();
			facilAllegDetailDtoReq.setIdStage(idStage);
			updtFacilAllegMultiDtlReq.setFacilAllegDetailDto(facilAllegDetailDtoReq);
			FacilAllegDetailDto facilAllegDetailDto = facilAllgDtlDao.retriveIdCase(updtFacilAllegMultiDtlReq);

			/* Retrieve Case Id for the current stage */
			// CallCCMNB6D
			if (!ObjectUtils.isEmpty(facilAllegDetailDto.getIdCase())) {
				pCCMN87DInputRec.setIdStage(facilAllegDetailDto.getIdStage());
				pCCMN87DInputRec.setIdCase(facilAllegDetailDto.getIdCase());
			}
		} else {
			pCCMN87DInputRec.setIdStage(idStage);
		}
		pCCMN87DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		pCCMN87DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);

		List<EventStagePersonLinkInsUpdOutDto> eventDtls = objCcmn87dDao.getEventAndStatusDtls(pCCMN87DInputRec);

		return eventDtls;
	}

	/**
	 * Method Name: serviceAuth Method Description: This method looks removes
	 * the service auth events (comp,pend,approval) from the eventlist
	 * 
	 * @param dlvryValidationDto
	 * @param errorDtoList
	 */
	public void serviceAuth(DlvryClosureValidationDto dlvryValidationDto, List<ErrorDto> errorDtoList) {
		boolean bLastStage = false;
		boolean bPassedSvcAuthEdit = true;
		boolean bSvcAuthsToProgress = false;
		boolean bEligibleStageFound = false;
		// CallCCMNB6D
		UpdtFacilAllegMultiDtlReq updtFacilAllegMultiDtlReq = new UpdtFacilAllegMultiDtlReq();

		FacilAllegDetailDto facilAllegDetailDtoReq = new FacilAllegDetailDto();
		facilAllegDetailDtoReq.setIdStage(dlvryValidationDto.getRgStageDto().getIdStage());
		updtFacilAllegMultiDtlReq.setFacilAllegDetailDto(facilAllegDetailDtoReq);

		FacilAllegDetailDto facilAllegDetailDto = facilAllgDtlDao.retriveIdCase(updtFacilAllegMultiDtlReq);

		if (!ObjectUtils.isEmpty(facilAllegDetailDto.getIdCase())) {
			// CallCCMNF6D
			List<StageCdOutDto> stageDtoList = callCCMNF6D(facilAllegDetailDto);
			if (!CollectionUtils.isEmpty(stageDtoList) && stageDtoList.size() == 1) {
				bLastStage = true;
			}
			// CallCCMN87D
			EventStagePersonLinkInsUpdInDto eventReq = new EventStagePersonLinkInsUpdInDto();
			eventReq.setCdEventType(ServiceConstants.SVC_AUTH_EVENT_TYPE);
			List<EventStagePersonLinkInsUpdOutDto> eventDtls = callCCMN87D(eventReq, dlvryValidationDto);

			List<Long> eventIdList = new ArrayList<>();
			if (!bLastStage) {
				for (EventStagePersonLinkInsUpdOutDto eventDto : eventDtls) {
					eventIdList.add(eventDto.getIdEvent());
					if (eventDto.getCdEventStatus().equals(ServiceConstants.COMPLETE)
							|| (eventDto.getCdEventStatus().equals(ServiceConstants.PENDING))
							|| (eventDto.getCdEventStatus().equals(ServiceConstants.APPROVAL))) {
						if (!bPassedSvcAuthEdit) {
							break;
						}
						/*
						 ** for the Service Auth event, search the
						 * SVC_AUTH_EVENT_LINK table to get the corresponding
						 * Service Auth Id.
						 */
						// CallCSES24D
						SvcAuthEventLinkInDto svcAuthEventLinkInDto = new SvcAuthEventLinkInDto();
						svcAuthEventLinkInDto.setIdSvcAuthEvent(eventDto.getIdEvent());
						List<SvcAuthEventLinkOutDto> svcAuthDetailNameList = serviceAuthEventLinkDao
								.getAuthEventLink(svcAuthEventLinkInDto);
						if (!CollectionUtils.isEmpty(svcAuthDetailNameList)) {
							for (SvcAuthEventLinkOutDto svcAuthEventLinkOutDto : svcAuthDetailNameList) {
								/*
								 ** search SVC_AUTH_DETAIL table to retrieve the
								 * SvcAuthDtlTermDate
								 */
								// CallCLSS24D
								SvcAuthDetailNameInDto svcAuthDetailNameInDto = new SvcAuthDetailNameInDto();
								svcAuthDetailNameInDto.setIdSvcAuth(svcAuthEventLinkOutDto.getIdSvcAuth());
								List<SvcAuthDetailNameOutDto> svcAuthList = svcAuthDetailNameDao
										.getServiceAuthentication(svcAuthDetailNameInDto);

								for (SvcAuthDetailNameOutDto svcAuthDto : svcAuthList) {
									if (DateUtils.isAfterToday(svcAuthDto.getDtSvcAuthDtlTerm())) {
										if (!ServiceConstants.APPROVED.equals(eventDto.getCdEventStatus())) {  //Defect# 12690 - Modified the object to pass the correct event status from the event dto
											bPassedSvcAuthEdit = ServiceConstants.FALSEVAL;
										} else {
											/*
											 * APRV'D Former CPS Day Care
											 * service auths can remain open
											 * whether or not an eligible stage
											 * exists to which we can progress
											 * them. Do not set the
											 * bSvcAuthsToProgress flag to TRUE
											 * for Former CPS Day Care service
											 * auth.
											 */
											if (!ArrayUtils.isEquals(ServiceConstants.FORMER_DAY_CARE,
													svcAuthDto.getCdSvcAuthDtlSvc())) {
												bSvcAuthsToProgress = ServiceConstants.TRUE_VALUE;
											}
										}
									}
								} // end of for
							}
						}
					} /* end if COMP or PEND */
				} // for loop
			} // end Last Stage
			else {
				/*
				 * Open SVC Auth with end date in future. Variable
				 * Ccmn87do.ArchOutputStruc.ulRowQty is being replace with a
				 * local variable ulRowQtyEvent in for loop where
				 * bPassedSvcAuthEdit == TRUE.
				 */

				for (EventStagePersonLinkInsUpdOutDto eventDto : eventDtls) {
					eventIdList.add(eventDto.getIdEvent());
					/*
					 ** When this is not the last stage in the case, we don't
					 * want any SvcAuths that are above COMPlete to have a term
					 * date greater than today (unless the Service Code is 40M)
					 * Otherwise, the SvcAuths are NEW or PROC and we don't give
					 * edit checks on these
					 */
					if (eventDto.getCdEventStatus().equals(ServiceConstants.COMPLETE)
							|| (eventDto.getCdEventStatus().equals(ServiceConstants.PENDING))
							|| (eventDto.getCdEventStatus().equals(ServiceConstants.APPROVAL))) {
						if (!bPassedSvcAuthEdit) {
							break;
						}
						// CallCSES24D
						SvcAuthEventLinkInDto svcAuthEventLinkInDto = new SvcAuthEventLinkInDto();
						svcAuthEventLinkInDto.setIdSvcAuthEvent(eventDto.getIdEvent());
						List<SvcAuthEventLinkOutDto> svcAuthDetailNameList = serviceAuthEventLinkDao
								.getAuthEventLink(svcAuthEventLinkInDto);
						for (SvcAuthEventLinkOutDto svcAuthEventLinkOutDto : svcAuthDetailNameList) {
							/*
							 ** search SVC_AUTH_DETAIL table to retrieve the
							 * SvcAuthDtlTermDate
							 */
							// CallCLSS24D
							SvcAuthDetailNameInDto svcAuthDetailNameInDto = new SvcAuthDetailNameInDto();
							svcAuthDetailNameInDto.setIdSvcAuth(svcAuthEventLinkOutDto.getIdSvcAuth());
							List<SvcAuthDetailNameOutDto> svcAuthList = svcAuthDetailNameDao
									.getServiceAuthentication(svcAuthDetailNameInDto);
							for (SvcAuthDetailNameOutDto svcAuthDto : svcAuthList) {
								if (!ObjectUtils.isEmpty(svcAuthDto.getDtSvcAuthDtlTerm()) // Defect 11803 - Add null check
										&& DateUtils.isAfterToday(svcAuthDto.getDtSvcAuthDtlTerm())) {
									if (!ServiceConstants.APPROVED.equals(dlvryValidationDto.getEventStatus())
											|| !ServiceConstants.FORMER_DAY_CARE
													.equals(svcAuthDto.getCdSvcAuthDtlSvc())) {
										bPassedSvcAuthEdit = ServiceConstants.FALSEVAL;
										break;
									}
								}
							} // end of for
						} // end of for
					}
				}
			} /* end if bLastStage is TRUE */
			if (bSvcAuthsToProgress && bPassedSvcAuthEdit) {
				StageSituationInDto stageSituationIn = new StageSituationInDto();
				stageSituationIn.setIdCase(dlvryValidationDto.getRgStageDto().getIdCase());
				// CallCLSS30D  ////Defect# 12690 - Modified the parameter to pass the correct object stageSituationIn
				List<StageSituationOutDto> stageSituationDaoList = stageSituationDao
						.getStageDetails(stageSituationIn);
				for (StageSituationOutDto dto : stageSituationDaoList) {
					//Defect# 12690 - Added condition to check if any one of the stage is eligible to progress the service auth then break the loop based on this flag
					if(bEligibleStageFound)
						break;
					////Defect# 12690 -Removed the date default year check, since the date stage close will not have default date
					if (ObjectUtils.isEmpty(dto.getDtStageClose())
							&& !dto.getIdStage().equals(dlvryValidationDto.getRgStageDto().getIdStage())) {
						if (!ObjectUtils.isEmpty(dto.getDtStageStart())
								&& (!ServiceConstants.PREP_ADULT.equals(dto.getCdStage())
										&& !ServiceConstants.ARI_STAGE.equals(dto.getCdStage())
										&& !ServiceConstants.SUBCARE.equals(dto.getCdStage())
										&& !ServiceConstants.INVESTIGATION.equals(dto.getCdStage())
										&& !ServiceConstants.ADOPTION.equals(dto.getCdStage())
										&& !ServiceConstants.A_R_STAGE.equals(dto.getCdStage()))) {
							bEligibleStageFound = true;

						} else if (ObjectUtils.isEmpty(dto.getDtStageStart())
								&& (!ServiceConstants.PREP_ADULT.equals(dto.getCdStage())
										&& !ServiceConstants.ARI_STAGE.equals(dto.getCdStage())
										&& !ServiceConstants.SUBCARE.equals(dto.getCdStage())
										&& !ServiceConstants.ADOPTION.equals(dto.getCdStage()))) ////Defect# 12690 - Removed the investigation stage check as per the Tuxedo service csvc16s
						{
							for (StageSituationOutDto situationDto : stageSituationDaoList) {
								if (ObjectUtils.isEmpty(situationDto.getDtStageClose())) { ////Defect# 12690 -Removed the date default year check, since the date stage close will not have default date
									/*
									 ** We are trying to determine which stage we
									 * should use for SvcAuths and this will be
									 * used later. We store the idStage for each
									 ** stage type we find that is open and will
									 * base which stage to progress the Service
									 * Auths to later based on which temp
									 ** variable in the hierarchy has an idStage
									 * in it
									 **
									 ** Removed ADO stage
									 */
									if (ServiceConstants.FPR_PROGRAM.equals(situationDto.getCdStage())) {
										bEligibleStageFound = true;
									} else if (ServiceConstants.FSU_PROGRAM.equals(situationDto.getCdStage())) {
										bEligibleStageFound = true;
									} else if (ServiceConstants.FRE_PROGRAM.equals(situationDto.getCdStage())) {
										bEligibleStageFound = true;
									}
								} /* end if Stage not closed */
							} /* end for i loop */
						} /*
							 * end if RC = 0 and not the SUB, PAL, or ADO stages
							 */
					} /* end if StageClose Null or Max */
				} // end for loop
				/*
				 ** If a stage suitable for receiving the open service auths is
				 * not found, set bPassedSvcAuthEdit to FALSE so that an error
				 * message informing the user that
				 * "Open services auths were found" so they can close them
				 * first.
				 */
				if (!bEligibleStageFound) {
					bPassedSvcAuthEdit = false;
				}
			} /* end if */
			/*
			 ** Changed message passed back to error list array to be
			 * consistent with other Conclusion edits
			 */
			/*
			 * Added check for CRSR stage types and perform display
			 * of error page hyper link accordingly
			 */

			if (!bPassedSvcAuthEdit) {
				if (ServiceConstants.SVC_CD_STAGE_TYPE_CREG.equals(dlvryValidationDto.getCdStage())
						|| ServiceConstants.SVC_CD_STAGE_TYPE_CGUA.equals(dlvryValidationDto.getCdStage())) {
					createErrorDtoList(errorDtoList, String.valueOf(MSG_SVA_CRSR_OPN_AUTHS));
				} else {
					createErrorDtoList(errorDtoList, String.valueOf(MSG_SVA_OPN_AUTHS));
				}
			}
		}
	}

	/**
	 * Method Name: callCCMNF6D Method Description: service call to ccmnf6D
	 * 
	 * @param facilAllegDetailDto
	 * @return List<StageCdOutDto>
	 */
	private List<StageCdOutDto> callCCMNF6D(FacilAllegDetailDto facilAllegDetailDto) {
		StageCdInDto stageCdInDto = new StageCdInDto();
		stageCdInDto.setIdCase(facilAllegDetailDto.getIdCase());
		return stageCdDao.getStageDtlsByDate(stageCdInDto);
	}

	// CallCSVC28D
	/**
	 * Method Name: callCSVC28D Method Description: service call to csvc28D
	 * 
	 * @param closureValidationDto
	 * @param eventStatus
	 * @param dlvryClosurevalidationRes
	 * @return List<EventDto>
	 */
	private List<EventDto> callCSVC28D(DlvryClosureValidationDto closureValidationDto, String eventStatus,
			DlvryClosurevalidationRes dlvryClosurevalidationRes) {
		List<EventDto> finaleventList = new ArrayList<>();
		boolean bFound = false;
		List<Long> eventIdList = new ArrayList<>();
		List<EventDto> eventList = eventDao.getEventInfo(closureValidationDto.getRgStageDto().getIdStage(),
				eventStatus);
		/*
		 ** The only completed Approval events for CPS that we want to return are
		 * rejected or invalid events, so first we much check for all CPS
		 * completed Approval events and continue appropriately.
		 */
		for (EventDto eventDto : eventList) {
			if (ServiceConstants.SVC_CD_STAGE_CPS_SVC.equals(closureValidationDto.getCdStage())
					&& ServiceConstants.EVENT_TYPE_APPROVAL.equals(eventDto.getCdEventType())
					&& ServiceConstants.SVC_CD_EVENT_STATUS_COMPLETE.equals(eventStatus)) {
				/*
				 ** The event is a completed CPS approval event, so call DAM
				 * CCMN56D to determine the approval status
				 */
				// CallCCMN56D
				List<DlvryClosureApproversDto> approversList = callCCMN56D(eventDto);

				for (DlvryClosureApproversDto DlvryClosureApproversDto : approversList) {
					if (!bFound && !ObjectUtils.isEmpty(DlvryClosureApproversDto.getApproversStatus())
							&& (ServiceConstants.APRV_REJECT.equals(DlvryClosureApproversDto.getApproversStatus())
									|| ServiceConstants.APRV_INVALID
											.equals(DlvryClosureApproversDto.getApproversStatus()))) {
						bFound = true;
					}
				}
				if (bFound) {
					finaleventList.add(eventDto);
				}

			} else {
				/*
				 ** Added if statement to check the event type of the current
				 * event. Do not return the id_event if it is a PROC, NEW, or
				 * COMP Service Auth. Do not return the id_event if it is a
				 * Service Auth Approval.
				 */
				if (!(((ServiceConstants.SERVICE_AUTH.equals(eventDto.getCdEventType()))
						&& ((ServiceConstants.PROGRESS.equals(eventDto.getCdEventStatus()))
								|| (ServiceConstants.SVC_CD_EVENT_STATUS_NEW.equals(eventDto.getCdEventStatus()))
								|| (ServiceConstants.COMPLETE.equals(eventDto.getCdEventStatus()))))
						|| (ServiceConstants.SVC_AUTH_APPROVAL_TASK.equals(eventDto.getCdTask())))) {
					finaleventList.add(eventDto);
				}
			}
		}
		finaleventList.stream().forEach(eventDto -> {

			if (!eventIdList.contains(eventDto.getIdEvent())) {
				eventIdList.add(eventDto.getIdEvent());
			}
		});
		dlvryClosurevalidationRes.getIdEvent().addAll(eventIdList);
		return finaleventList;
	}

	/**
	 * Method Name: callCCMN56D Method Description: Service call to ccmn56D
	 * 
	 * @param eventDto
	 * @return List<DlvryClosureApproversDto>
	 */
	private List<DlvryClosureApproversDto> callCCMN56D(EventDto eventDto) {
		List<DlvryClosureApproversDto> approversList = dlvryClosureValidationDao
				.getApproversList(eventDto.getIdEvent());

		return approversList;

	}

}