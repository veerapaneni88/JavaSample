package us.tx.state.dfps.service.investigation.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.InCheckStageEventStatusDto;
import us.tx.state.dfps.common.dto.ServiceResHeaderDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.baseriskassmt.dto.MPSStatsValueDto;
import us.tx.state.dfps.service.casepackage.dao.CaseMergeCustomDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CaseMergeDetailDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.contacts.dao.MPSStatsDao;
import us.tx.state.dfps.service.exception.AllegationBusinessException;
import us.tx.state.dfps.service.investigation.dao.*;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.investigation.dto.CalPerpVictmRoleDto;
import us.tx.state.dfps.service.investigation.dto.PerpVictmDto;
import us.tx.state.dfps.service.investigation.service.AllegtnService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageIdDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 10:24:29 AM
 */
@Service
@Transactional
public class AllegtnServiceImpl implements AllegtnService {

	@Autowired
	AllegtnDao allegtnDao;

	@Autowired
	ApsInvstDetailDao apsInvstDetailDao;

	@Autowired
	CpsInvstDetailDao cpsInvstDetailDao;

	@Autowired
	FacilityInvstDtlDao facilityInvstDtlDao;

	@Autowired
	LicensingInvstDtlDao licensingInvstDtlDao;

	@Autowired
	CaseMergeCustomDao caseMergeCustomDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	ApprovalCommonService approvalService;

	@Autowired
	EventDao eventDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	FacilAllgDtlDao facilAllgDtlDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	MobileUtil mobileUtil;

	@Autowired
	private MPSStatsDao mpsStatsDao;

	private static final Logger log = Logger.getLogger(AllegtnServiceImpl.class);

	@Autowired
	CheckStageEventStatusService checkStageEventStatusService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tx.us.dfps.impact.investigation.service.AllegtnService#getAllegations
	 * (org.tx.us.dfps.impact.request.InvAllegListReq)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public InvAllegListRes getAllegations(InvAllegListReq invAllegListReq) {
		List<AllegationDetailDto> allegationList = null;
		if (invAllegListReq.getSzCdAllegIncidentStage().equals(ServiceConstants.CSTAGES_INV)
				|| invAllegListReq.getSzCdAllegIncidentStage().equals(ServiceConstants.CSTAGES_ARI)) {
			allegationList = allegtnDao.getInvAdminStageAllegn(invAllegListReq.getUlIdStage());
		} else if (invAllegListReq.getSzCdAllegIncidentStage().equals(ServiceConstants.CSTAGES_SVC)) {
			allegationList = allegtnDao.getDeliveryStageAllegn(invAllegListReq.getUlIdStage());
		} else if (invAllegListReq.getSzCdAllegIncidentStage().equals(ServiceConstants.CSTAGES_INT)) {
			allegationList = allegtnDao.getIntakeStageAllegn(invAllegListReq.getUlIdStage());
		}
		InvAllegListRes invAllegListRes = new InvAllegListRes();
		invAllegListRes.setAllegationList(allegationList);
		if (invAllegListReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_APS)) {
			List<ApsInvstDetail> apsInvstDetailList = new ArrayList<>();
			apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(invAllegListReq.getUlIdStage());
			if (apsInvstDetailList.size() > 0) {
				invAllegListRes.setUlIdEvent(apsInvstDetailList.get(0).getIdEvent());
				invAllegListRes.setCdAllegDisposition(apsInvstDetailList.get(0).getCdApsInvstOvrallDisp());
			}
		}
		if (invAllegListReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CPS)) {
			List<CpsInvstDetail> cpsInvstDetailList = new ArrayList<>();
			cpsInvstDetailList = cpsInvstDetailDao.getCpsInvstDetailbyParentId(invAllegListReq.getUlIdStage());
			if (cpsInvstDetailList.size() > 0) {
				invAllegListRes.setUlIdEvent(cpsInvstDetailList.get(0).getIdEvent());
				invAllegListRes.setCdAllegDisposition(cpsInvstDetailList.get(0).getCdCpsInvstDtlOvrllDisptn());
			}
		}
		if (invAllegListReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_AFC)) {
			FacilityInvstDtl facilityInvstDtl = null;
			facilityInvstDtl = facilityInvstDtlDao.getFacilityInvstDetailbyParentId(invAllegListReq.getUlIdStage());
			if (!TypeConvUtil.isNullOrEmpty(facilityInvstDtl)) {
				invAllegListRes.setUlIdEvent(facilityInvstDtl.getIdEvent());
				invAllegListRes.setCdAllegDisposition(facilityInvstDtl.getCdFacilInvstOvrallDis());
			}
		}
		if (!mobileUtil.isMPSEnvironment() && (!invAllegListReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CCL)
				|| !invAllegListReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_RCL))) {
			List<LicensingInvstDtl> licensingInvstDtlList = new ArrayList<>();
			licensingInvstDtlList = licensingInvstDtlDao
					.getLicensingInvstDtlDaobyParentId(invAllegListReq.getUlIdStage());
			if (licensingInvstDtlList.size() > 0) {
				invAllegListRes.setUlIdEvent(licensingInvstDtlList.get(0).getIdEvent());
				invAllegListRes.setCdAllegDisposition(licensingInvstDtlList.get(0).getCdLicngInvstOvrallDisp());
			}
		}
		invAllegListRes.setTransactionId(invAllegListReq.getTransactionId());
		log.info(ServiceConstants.TRANSACTION_ID + invAllegListReq.getTransactionId());
		return invAllegListRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveAllgtnMultiRes saveAllgtnMulti(SaveAllgtnMultiReq saveAllgtnMultiReq) {
		if (!(saveAllgtnMultiReq.getSzCdStage().equals(ServiceConstants.CD_STAGE_ADMIN))) {
			InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
			inCheckStageEventStatusDto.setCdTask(saveAllgtnMultiReq.getSzCdTask());
			inCheckStageEventStatusDto.setIdStage(saveAllgtnMultiReq.getIdStage());
			inCheckStageEventStatusDto.setCdReqFunction(saveAllgtnMultiReq.getReqFuncCd());
			if (checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto))
				;
		}
		if (ServiceConstants.CASE_MERGED_IN_ERROR.equals(saveAllgtnMultiReq.getDisposition())) {
			Stage stage = stageDao.getStageEntityById(saveAllgtnMultiReq.getIdStage());
			List<CaseMergeDetailDto> caseMergeDetailDtoList = caseMergeCustomDao
					.getCaseMergeByIdCaseMergeTo(stage.getCapsCase().getIdCase());
			/*
			 * If cIndCaseMergeInv is not FND_YES, set the return code to
			 * MSG_INV_DISP_INVALID and break out of the loop.
			 */
			if (ObjectUtils.isEmpty(caseMergeDetailDtoList) || caseMergeDetailDtoList.stream()
					.filter(casedetail -> !ServiceConstants.STRING_IND_Y.equals(casedetail.getIndCaseMergeInv()))
					.collect(Collectors.toList()).size() > 0) {
				throw new AllegationBusinessException(4107l);
			}
		}//Production issue - artf256308  moving braces to call the disposition updates
			if (ServiceConstants.CPGRMS_CPS.equals(saveAllgtnMultiReq.getSzCdStageProgram())) {
				CallBlankOverallDispositionCPS(saveAllgtnMultiReq.getIdStage());
			}
			if (ServiceConstants.CPGRMS_CCL.equals(saveAllgtnMultiReq.getSzCdStageProgram())
					|| ServiceConstants.CPGRMS_RCL.equals(saveAllgtnMultiReq.getSzCdStageProgram())) {
				CallBlankOverallDispositionLIC(saveAllgtnMultiReq.getIdStage());
			}
			if (ServiceConstants.CPGRMS_APS.equals(saveAllgtnMultiReq.getSzCdStageProgram())) {
				CallBlankOverallDispositionAPSMulti(saveAllgtnMultiReq.getIdStage());
			}

			if (/*
				 * An ID EVENT has been passed in, implying the status is PEND
				 */
			!TypeConvUtil.isNullOrEmpty(saveAllgtnMultiReq.getUlIdEvent())) {
				// invalidate approval
				EventDto eventDto = eventDao.getEventByid(saveAllgtnMultiReq.getUlIdEvent());
				if (ServiceConstants.EVENTSTATUS_PENDING.equalsIgnoreCase(eventDto.getCdEventStatus())) {
					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					approvalCommonInDto.setIdEvent(saveAllgtnMultiReq.getUlIdEvent());
					approvalService.callCcmn05uService(approvalCommonInDto);
				}
			}

		for (Long idAllegation : saveAllgtnMultiReq.getAllegationIdList()) {
			Allegation allegation = allegtnDao.getAllegationById(idAllegation);
			allegation.setCdAllegDisposition(saveAllgtnMultiReq.getDisposition());
			allegation.setTxtDispstnSeverity(saveAllgtnMultiReq.getTxtDispstnSeverity());
			allegation.setCdAllegSeverity(saveAllgtnMultiReq.getSeverity());
			allegation.setIndRelinquishCstdy(null);
			allegation.setDtLastUpdate(new Date());
			allegation.setDtAllegedIncident(saveAllgtnMultiReq.getDtAllegedIncident());
			allegation.setIndApproxAllegedDate(saveAllgtnMultiReq.getIndApproxAllegedDate());
			allegtnDao.updateAllegation(allegation, ServiceConstants.REQ_FUNC_CD_UPDATE, false);
			if(mobileUtil.isMPSEnvironment()) {
				AllegationAUDReq allgtnAUDReq = new AllegationAUDReq();
				AllegationDetailDto allegationDetail = new AllegationDetailDto();
				allegationDetail.setIdStage(allegation.getStage().getIdStage());
				allegationDetail.setIdAllegation(allegation.getIdAllegation());
				allgtnAUDReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				allgtnAUDReq.setAllegationDetail(allegationDetail);
				callMPSStatsHelper(allgtnAUDReq);
			}

		}
		SaveAllgtnMultiRes saveAllgtnMultiRes = new SaveAllgtnMultiRes();
		saveAllgtnMultiRes.setStatus(ServiceConstants.SUCCESS);
		log.info(ServiceConstants.TRANSACTION_ID + saveAllgtnMultiReq.getTransactionId());
		CalOverallDispReq calOverallDispReq = new CalOverallDispReq();
		calOverallDispReq.setUlIdStage(saveAllgtnMultiReq.getIdStage());
		calOverallDispReq.setSzCdTask(saveAllgtnMultiReq.getSzCdTask());
		calOverallDispReq.setReqFuncCd(saveAllgtnMultiReq.getReqFuncCd());
		calOverallDispReq.setSzCdStageProgram(saveAllgtnMultiReq.getSzCdStageProgram());
		calOverallDispReq.setSzCdOverallDisp("");
		calOverallDisp(calOverallDispReq);
		return saveAllgtnMultiRes;
	}

	@Override
	@Transactional
	public DisplayAllegDtlRes diaplyAllegtnDetail(DisplayAllegDtlReq displayAllegDtlReq) {
		DisplayAllegDtlRes displayAllegDtlRes = null;
		if (TypeConvUtil.isNullOrEmpty(displayAllegDtlReq.getUlIdAllegation())) {
			displayAllegDtlRes = new DisplayAllegDtlRes();
			List<AllegtnPrsnDto> allegtnPrsnList = allegtnDao.getAllegtnDtlByIdStage(displayAllegDtlReq);
			displayAllegDtlRes.setAllegtnPrsnDtoList(allegtnPrsnList);
		} else {
			displayAllegDtlRes = allegtnDao.getAllegtnDtlByIdAlegtn(displayAllegDtlReq);
			List<AllegtnPrsnDto> allegtnPrsnList = allegtnDao.getAllegtnDtlByIdStage(displayAllegDtlReq);
			displayAllegDtlRes.setAllegtnPrsnDtoList(allegtnPrsnList);
			/*
			 * Added code for creating History table on Allegation Detail page
			 * for CCL program by fetching records from Allegation History table
			 * and forming a history record by loop through ith element and
			 * i+1th element.
			 * 
			 * 
			 */
			if (!ObjectUtils.isEmpty(displayAllegDtlReq.getCdStageProgram())
					&& displayAllegDtlReq.getCdStageProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_CCL)) {
				// Database call to fetch allegation history list
				List<PerpVictmDto> allegationDtlHistoryList = allegtnDao
						.getAllegationDetailHistoryList(displayAllegDtlReq);

				// forming allegation history AV/AP change using for loop of the
				// list by calling private Method:createAllegationHistoryList
				if (!CollectionUtils.isEmpty(allegationDtlHistoryList)) {
					IntStream.range(0, allegationDtlHistoryList.size() - 1).forEach(i -> {
						createAllegationHistoryList(allegationDtlHistoryList.get(i),
								allegationDtlHistoryList.get(i + 1));
					});
				}
				displayAllegDtlRes.setAllegationDtlHistoryList(allegationDtlHistoryList);
			}
		}
		log.info(ServiceConstants.TRANSACTION_ID + displayAllegDtlReq.getTransactionId());
		return displayAllegDtlRes;
	}

	/**
	 * Method Name:createAllegationHistoryList Method Desc: This Method is
	 * called to form a history list for CCL program on Allegation Detail Page
	 * 
	 * @param perpVictmDto
	 * @param perpVictmDto2
	 */
	private void createAllegationHistoryList(PerpVictmDto perpVictmDto, PerpVictmDto perpVictmDto2) {
		// Checking for null values and checking for change in either of Alleged
		// Victim or Alleged Prep
		if ((!perpVictmDto.getNmPersonFullVictim().equalsIgnoreCase(perpVictmDto2.getNmPersonFullVictim())
				&& (ObjectUtils.isEmpty(perpVictmDto.getNmPersonFullAllegedPerp())
						|| ObjectUtils.isEmpty(perpVictmDto2.getNmPersonFullAllegedPerp())))
				|| (!perpVictmDto.getNmPersonFullVictim().equalsIgnoreCase(perpVictmDto2.getNmPersonFullVictim())
						|| ((!ObjectUtils.isEmpty(perpVictmDto.getNmPersonFullAllegedPerp())
								&& (!perpVictmDto.getNmPersonFullAllegedPerp()
										.equalsIgnoreCase(perpVictmDto2.getNmPersonFullAllegedPerp())))
								|| (!ObjectUtils.isEmpty(perpVictmDto2.getNmPersonFullAllegedPerp())
										&& (!perpVictmDto2.getNmPersonFullAllegedPerp()
												.equalsIgnoreCase(perpVictmDto.getNmPersonFullAllegedPerp())))))) {
			perpVictmDto.setNmPersonFullVictimFrom(perpVictmDto.getNmPersonFullVictim());
			perpVictmDto.setNmPersonFullAllegedPerpFrom(perpVictmDto.getNmPersonFullAllegedPerp());
			perpVictmDto.setNmPersonFullVictimTo(perpVictmDto2.getNmPersonFullVictim());
			perpVictmDto.setNmPersonFullAllegedPerpTo(perpVictmDto2.getNmPersonFullAllegedPerp());
			perpVictmDto.setDtUpdate(perpVictmDto2.getDtLastUpdate());
			perpVictmDto.setNmPersonUpdated(perpVictmDto2.getNmPersonFullWorker());
		}
	}

	@Override
	@Transactional
	public AllegationAUDRes allegationAUD(AllegationAUDReq allegationAUDReq) {
		AllegationDetailDto allegationDetail = allegationAUDReq.getAllegationDetail();
		boolean isVRCDeleted = false;
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setCdTask(allegationDetail.getCdTask());
		inCheckStageEventStatusDto.setIdStage(allegationDetail.getIdStage());
		inCheckStageEventStatusDto.setCdReqFunction(allegationAUDReq.getReqFuncCd());
		if (!TypeConvUtil.isNullOrEmpty(allegationAUDReq.getLdIdTodo())) {
			inCheckStageEventStatusDto.setIdStage(allegationAUDReq.getLdIdTodo());
		}
		checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		if (ServiceConstants.CASE_MERGED_IN_ERROR.equals(allegationDetail.getCdAllegDisposition())) {
			Stage stageDto = stageDao.getStageEntityById(allegationDetail.getIdStage());
			List<CaseMergeDetailDto> caseMergeDetailList = caseMergeCustomDao
					.getCaseMergeByIdCaseMergeTo(stageDto.getCapsCase().getIdCase());
			/*
			 * If cIndCaseMergeInv is not FND_YES, set the return code to
			 * MSG_INV_DISP_INVALID and break out of the loop.
			 */
			if (ObjectUtils.isEmpty(caseMergeDetailList) || caseMergeDetailList.stream()
					.filter(casedetail -> !ServiceConstants.STRING_IND_Y.equals(casedetail.getIndCaseMergeInv()))
					.collect(Collectors.toList()).size() > 0) {
				throw new AllegationBusinessException(4107l);
			}
		}
		AllegationAUDRes allegationAUDRes = new AllegationAUDRes();
		allegationAUDRes.setUlIdVictim(allegationDetail.getIdVictim());
		allegationAUDRes.setUlIdAllegedPerpetrator(allegationDetail.getIdAllegedPerpetrator());
		/*
		 * If DELETE, perform deletion based on stage of service and existence
		 * of duplicates. Allow the allegation to be deleted if: 1) Stage is not
		 * INTAKE 2) Stage is INTAKE and more than one row exists for the given
		 * "who did what to whom"
		 */
		if (allegationAUDReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			/* if stage is not INTAKE; delete the allegation */
			if (!(ServiceConstants.CSTAGES_INT.equals(allegationDetail.getCdAllegIncidentStage()))) {
				if ((ServiceConstants.CPGRMS_CCL.equals(allegationAUDReq.getSzCdStageProgram())
						|| ServiceConstants.CPGRMS_RCL.equals(allegationAUDReq.getSzCdStageProgram())) && allegtnDao.getInjuryAllegationCount(allegationDetail.getIdAllegation()) > 0) {
					throw new AllegationBusinessException(57333l);
				} else {
					handleDeletion(allegationAUDReq);
					allegationAUDRes.setUlIdVictim(allegationDetail.getIdVictim());
					allegationAUDRes.setUlIdAllegedPerpetrator(allegationDetail.getIdAllegedPerpetrator());
				}
			} else {
				// duplicate exists; allow deletion
				if (allegtnDao.findDuplicates(allegationDetail, allegationAUDReq.getReqFuncCd())) {
					handleDeletion(allegationAUDReq);
					allegationAUDRes.setUlIdVictim(allegationDetail.getIdVictim());
					allegationAUDRes.setUlIdAllegedPerpetrator(allegationDetail.getIdAllegedPerpetrator());
				} else
					throw new AllegationBusinessException(4060l);
			}
		} else {
			// if the operation is ADD or UPDATE
			if (allegtnDao.findDuplicates(allegationDetail, allegationAUDReq.getReqFuncCd()))
				throw new AllegationBusinessException(9011l);
			else {
				if (!TypeConvUtil.isNullOrEmpty(allegationDetail.getCdAllegDisposition())
						|| ServiceConstants.NULL_STRING.equals(allegationDetail.getCdAllegDisposition())) {
					if (ServiceConstants.CPGRMS_CPS.equals(allegationAUDReq.getSzCdStageProgram())) {
						CallBlankOverallDispositionCPS(allegationDetail.getIdStage());
					}
					if (ServiceConstants.CPGRMS_CCL.equals(allegationAUDReq.getSzCdStageProgram())
							|| ServiceConstants.CPGRMS_RCL.equals(allegationAUDReq.getSzCdStageProgram())) {
						CallBlankOverallDispositionLIC(allegationDetail.getIdStage());
					}
					Stage stageDto = stageDao.getStageEntityById(allegationDetail.getIdStage());
						if (ServiceConstants.CPGRMS_APS.equals(allegationAUDReq.getSzCdStageProgram())
								&& stageDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_ARI)) {
							allegationDetail.setIdStage(allegationAUDReq.getIdStage());
							CallBlankOverallDispositionAPS(allegationDetail.getIdStage());
						} else if (ServiceConstants.CPGRMS_APS.equals(allegationAUDReq.getSzCdStageProgram())) {
							CallBlankOverallDispositionAPS(allegationDetail.getIdStage());
					}

				}
				Stage stageDto = stageDao.getStageEntityById(allegationDetail.getIdStage());
				if(stageDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_ARI)) {
					allegationDetail.setIdStage(allegationAUDReq.getIdStage());
					stagePersonLinkDao.updateStagePersonLink(allegationDetail);
				}else {
					stagePersonLinkDao.updateStagePersonLink(allegationDetail);
				}

				// ALM ID : 176946 : Now the system will update the ROLE of the PID on the PERSON LIST, if he/she is no longer an ALLEGED PERPETRATOR or VICTIM on the stage.
				if(allegationAUDReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)){
					Allegation allegation = allegtnDao.getAllegationById(allegationDetail.getIdAllegation());
					AllegationDetailDto oldAllegationDetailDto = new AllegationDetailDto();
					oldAllegationDetailDto.setIdAllegation(allegation.getIdAllegation());
					oldAllegationDetailDto.setIdStage(allegationDetail.getIdStage());
					oldAllegationDetailDto.setIdVictim(allegation.getPersonByIdVictim().getIdPerson());
					//Defect 16913,To fix the issue when the perpetrator is null
					if(!ObjectUtils.isEmpty(allegation.getPersonByIdAllegedPerpetrator())){
						oldAllegationDetailDto.setIdAllegedPerpetrator(allegation.getPersonByIdAllegedPerpetrator().getIdPerson());
					}
					updtVictimPerpRoles(oldAllegationDetailDto);
				}

				//FCL-CCI R2
				if ((ServiceConstants.CPGRMS_CCL.equals(allegationAUDReq.getSzCdStageProgram()) ||
				    ServiceConstants.CPGRMS_RCL.equals(allegationAUDReq.getSzCdStageProgram())) &&
					(allegationAUDReq.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD))) {
						//Create an event when a new Allegation is added
						allegationEvent(allegationAUDReq, ServiceConstants.REQ_FUNC_CD_ADD);
				}
				if (ServiceConstants.CPGRMS_APS.equals(allegationAUDReq.getSzCdStageProgram()) && !ObjectUtils.isEmpty(allegationDetail.getIdAllegation())) {
					Allegation oldAllegation = new Allegation();
					oldAllegation = allegtnDao.getAllegationById(allegationDetail.getIdAllegation());
                if (oldAllegation != null && ServiceConstants.CAPSALDP_VRC.equals(oldAllegation.getCdAllegDisposition()))
				 {
					isVRCDeleted = true;
                 }
				}

				Long idAllegation = allegtnDao.updateAllegation(pupulateAllegation(allegationDetail),
						allegationAUDReq.getReqFuncCd(), true);
				allegationAUDRes.setUlIdAllegation(idAllegation);
				allegationDetail.setIdAllegation(0l);
				updtVictimPerpRoles(allegationDetail);
				CalOverallDispReq calOverallDispReq = new CalOverallDispReq();
				calOverallDispReq.setUlIdStage(allegationDetail.getIdStage());
				calOverallDispReq.setSzCdTask(allegationDetail.getCdTask());
				calOverallDispReq.setReqFuncCd(allegationAUDReq.getReqFuncCd());
				calOverallDispReq.setSzCdStageProgram(allegationAUDReq.getSzCdStageProgram());
				calOverallDispReq.setSzCdOverallDisp("");
				calOverallDisp(calOverallDispReq);
			}
		}

		if (mobileUtil.isMPSEnvironment())
		{
			callMPSStatsHelper(allegationAUDReq);
		}

		if /* An ID EVENT has been passed in, and the status is PEND */
		(!TypeConvUtil.isNullOrEmpty(allegationAUDReq.getUlIdEvent())) {
			// invalidate approval
			EventDto eventDto = eventDao.getEventByid(allegationAUDReq.getUlIdEvent());
			if (ServiceConstants.EVENTSTATUS_PENDING.equalsIgnoreCase(eventDto.getCdEventStatus())) {
				ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
				approvalCommonInDto.setIdEvent(allegationAUDReq.getUlIdEvent());
				approvalService.callCcmn05uService(approvalCommonInDto);
			}
		}

		// defect fix for artf246199 : When EMR is invalidated, EMR Notification event is still in PEND status
		// PPM54242. If the Disposition is changed from VRC to something else, then we have to invalidate the approval and
		// update the Notification event related to EMR to Complete.
		if (ServiceConstants.CPGRMS_APS.equals(allegationAUDReq.getSzCdStageProgram())
		    && isVRCDeleted && !TypeConvUtil.isNullOrEmpty(allegationDetail.getIdStage()))
		{
			// invalidate approval for EMR events
			//get list of EMR events in Pend status for given stage id, case
			List<EventDto> eventList = getEventIds(allegationDetail.getIdStage(), ServiceConstants.APRV_APS_INV_EMR);
			if(!CollectionUtils.isEmpty(eventList)){
				eventList.stream().forEach(eventDto -> {
					if(CodesConstant.CEVNTTYP_NOT.equals(eventDto.getCdEventType())){
						ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
						approvalCommonInDto.setIdEvent(eventDto.getIdEvent());
						approvalService.callCcmn05uService(approvalCommonInDto);
					}
				});
			}
		}

		log.info(ServiceConstants.TRANSACTION_ID + allegationAUDReq.getTransactionId());
		return allegationAUDRes;
	}

	private void callMPSStatsHelper(AllegationAUDReq allegationAUDReq)
	{
		AllegationDetailDto allegationDetail = allegationAUDReq.getAllegationDetail();
		MPSStatsValueDto mpsStatsValueDto =  new MPSStatsValueDto();

		if( ServiceConstants.REQ_FUNC_CD_ADD.equals( allegationAUDReq.getReqFuncCd()) )
		{
			mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_ADD);
		}
		else if(ServiceConstants.REQ_FUNC_CD_UPDATE.equals( allegationAUDReq.getReqFuncCd()))
		{
			mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_UPDATE);
		}
		else
		{
			mpsStatsValueDto.setIdEvent(!TypeConvUtil.isNullOrEmpty(allegationDetail)
					? allegationDetail.getIdEvent()
					: Long.valueOf(ServiceConstants.Zero));
			mpsStatsValueDto.setCdDmlType(ServiceConstants.REQ_FUNC_CD_DELETE);
		}
		mpsStatsValueDto.setIdReference(allegationDetail.getIdAllegation());
		mpsStatsValueDto.setIdEvent(allegationDetail.getIdEvent());
		mpsStatsValueDto.setIdCase(allegationDetail.getIdCase());
		mpsStatsValueDto.setIdStage(allegationDetail.getIdStage());
		mpsStatsValueDto.setCdReference(CodesConstant.CMPSSTAT_017);
		mpsStatsDao.logStatsToDB( mpsStatsValueDto );

	}

	//
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CalOverallDispRes calOverallDisp(CalOverallDispReq calOverallDispReq) {
		List<CpsInvstDetail> cpsInvstDetailList;
		List<ApsInvstDetail> apsInvstDetailList;
		List<LicensingInvstDtl> licInvstDetailList;
		Date tsOverallDisposition1 = null;
		String cdOverallDisposition = null;
		CalOverallDispRes calOverallDispRes = new CalOverallDispRes();
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setCdTask(calOverallDispReq.getSzCdTask());
		inCheckStageEventStatusDto.setIdStage(calOverallDispReq.getUlIdStage());
		inCheckStageEventStatusDto.setCdReqFunction(calOverallDispReq.getReqFuncCd());
		checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		/*
		 * Retrieve timestamps for overall roles and overall disposition. These
		 * will be used later in this service to ascertain that no intervening
		 * updates to STAGE_PERSON_LINK (roles) or the appropriate Investigation
		 * Detail table (disposition) have been made.
		 */
		List<String> dispositionsList = allegtnDao.getDispositionsList(calOverallDispReq.getUlIdStage());
		if (!dispositionsList.contains(null)) {
			cdOverallDisposition = calcOverallDisposition(calOverallDispReq, dispositionsList);
			/*
			 * * Update the appropriate Investigation Detail conclusion table
			 * with the overall disposition, based on Program.
			 */
			if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CPS)) {
				cpsInvstDetailList = cpsInvstDetailDao.getCpsInvstDetailbyParentId(calOverallDispReq.getUlIdStage());
				tsOverallDisposition1 = cpsInvstDetailList.get(0).getDtLastUpdate();
				// Retrieve full row from CPS_INVST_DETAIL and .
				List<CpsInvstDetail> CpsInvstDetailList = cpsInvstDetailDao
						.getCpsInvstDetailbyParentId(calOverallDispReq.getUlIdStage());
				for (CpsInvstDetail O : CpsInvstDetailList) {
					O.setCdCpsInvstDtlOvrllDisptn(cdOverallDisposition);
					O.setDtLastUpdate(tsOverallDisposition1);
					cpsInvstDetailDao.updtCpsInvstDetail(O, ServiceConstants.REQ_FUNC_CD_UPDATE);
				}
			}
			if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CCL)
					|| calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_RCL)) {
				licInvstDetailList = licensingInvstDtlDao
						.getLicensingInvstDtlDaobyParentId(calOverallDispReq.getUlIdStage());
				// Retrieve full row from LICENSING_INVST_DETAIL and .
				for (LicensingInvstDtl l : licInvstDetailList) {
					l.setCdLicngInvstOvrallDisp(cdOverallDisposition);
					licensingInvstDtlDao.saveLicensingInvstDtl(l);
				}
			}
			if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_AFC)) {
				facilAllgDtlDao.updateOverallDispositionFAC(calOverallDispReq.getUlIdStage(), cdOverallDisposition);
			}

			if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_APS)) {
				apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(calOverallDispReq.getUlIdStage());
				if (null != apsInvstDetailList && apsInvstDetailList.size() > 0) {
					tsOverallDisposition1 = apsInvstDetailList.get(0).getDtLastUpdate();
					// Retrieve full row from APS_INVST_DETAIL and .
					List<ApsInvstDetail> ApsInvstDetailList = apsInvstDetailDao
							.getApsInvstDetailbyParentId(calOverallDispReq.getUlIdStage());
					for (ApsInvstDetail O : ApsInvstDetailList) {
						O.setCdApsInvstOvrallDisp(cdOverallDisposition);
						O.setDtLastUpdate(tsOverallDisposition1);
						apsInvstDetailDao.updateApsInvstDetail(O);
					}
				}
			}

			if (ServiceConstants.RULED_OUT.equals(cdOverallDisposition)
					|| ServiceConstants.ADMIN_CLOSURE.equals(cdOverallDisposition))
				calOverallDispRes.setcIndRuloutOrAdm(ServiceConstants.INDICATOR_YES);
			if (null != cdOverallDisposition) {
				if ((!(cdOverallDisposition.equals(calOverallDispReq.getSzCdOverallDisp())))
				//		&& (ServiceConstants.STRING_IND_N).equals(calOverallDispReq.getbIndStageClose())
				)
				{
					Stage stageupdate = stageDao.getStageEntityById(calOverallDispReq.getUlIdStage());
					if (null == stageupdate.getDtStageClose()) {
						stageupdate.setCdStageReasonClosed(null);
						stageupdate.setDtLastUpdate(new Date());
						stageDao.updtStage(stageupdate, ServiceConstants.REQ_FUNC_CD_UPDATE);
					}
				}
			}
			// end if
			/*
			 * Retrieve and Calculate Victim Roles
			 */
			List<AllegtnPrsnDto> prsnRoleList = allegtnDao.getVictimUnKnownPerp(calOverallDispReq.getUlIdStage());
			// Calculate Victim Only Roles
			clalulatePersonRoles(prsnRoleList, ServiceConstants.PERSON_TYPE_VICTIM,
					calOverallDispReq.getSzCdStageProgram());
			List<AllegtnPrsnDto> VictimNonPerpList = allegtnDao.getVictimNonPerp(calOverallDispReq.getUlIdStage());
			clalulatePersonRoles(VictimNonPerpList, ServiceConstants.PERSON_TYPE_VICTIM,
					calOverallDispReq.getSzCdStageProgram());
			List<AllegtnPrsnDto> perpNonVictimList = allegtnDao.getPerpNonVictim(calOverallDispReq.getUlIdStage());
			// Calculate Alleged Perpetrator Roles
			clalulatePersonRoles(perpNonVictimList, ServiceConstants.PERSON_TYPE_PERP,
					calOverallDispReq.getSzCdStageProgram());
			// Retrieve and Victim/Perp Roles
			List<PerpVictmDto> perpAndVictimList = allegtnDao.getPerpAndVictim(calOverallDispReq.getUlIdStage());
			List<AllegtnPrsnDto> perpAndVictimList1 = CalcVictimPerpRole(calOverallDispReq, perpAndVictimList);
			List<AllegtnPrsnDto> allroles = new ArrayList<>(prsnRoleList);
			allroles.addAll(VictimNonPerpList);
			allroles.addAll(perpNonVictimList);
			allroles.addAll(perpAndVictimList1);
			List<Long> uniquePidList = new ArrayList<>(
					allroles.stream().map(AllegtnPrsnDto::getIdPerson).collect(Collectors.toSet()));
			for (Long uidPersonCurrent : uniquePidList) {
				List<String> persnRolesList = new ArrayList<>(
						allroles.stream().filter(o -> o.getIdPerson().equals(uidPersonCurrent))
								.map(AllegtnPrsnDto::getCdStagePersRole).collect(Collectors.toSet()));
				// save stage person link
				String rolePersonCurrent = null;
				rolePersonCurrent = CalcCurrentPersonRole(persnRolesList);
				StagePersonLink stagePersonLink = stagePersonLinkDao
						.getStagePersonLink(calOverallDispReq.getUlIdStage(), uidPersonCurrent);
				if (!ObjectUtils.isEmpty(stagePersonLink)){ // Defect 11997 - check for presence of entity
					stagePersonLink.setCdStagePersRole(rolePersonCurrent);
					stagePersonLink.setDtLastUpdate(new Date());
					stagePersonLinkDao.updateStagePersonLinkDetails(stagePersonLink);
				}
			}
		}
		else
		{
			if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_APS)) {
				apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(calOverallDispReq.getUlIdStage());
				if (null != apsInvstDetailList && apsInvstDetailList.size() > 0) {
					tsOverallDisposition1 = apsInvstDetailList.get(0).getDtLastUpdate();
					// Retrieve full row from APS_INVST_DETAIL and .
					List<ApsInvstDetail> ApsInvstDetailList = apsInvstDetailDao
							.getApsInvstDetailbyParentId(calOverallDispReq.getUlIdStage());
					for (ApsInvstDetail O : ApsInvstDetailList) {
						O.setCdApsInvstOvrallDisp("");
						O.setDtLastUpdate(tsOverallDisposition1);
						apsInvstDetailDao.updateApsInvstDetail(O);
					}
				}
			}
		}
		log.info(ServiceConstants.TRANSACTION_ID + calOverallDispReq.getTransactionId());
		return calOverallDispRes;
	}

	// This method nullifies any answered Child Sex/Labor Trafficking question
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceResHeaderDto updateChildSexLaborTrafficking(CommonCaseIdReq commonCaseIdReq) {
		List<StageIdDto> uidStageList = stageDao.searchStageByCaseId(commonCaseIdReq.getUlIdCase());
		for (StageIdDto uidStageDto : uidStageList) {
			List<CpsInvstDetail> CpsInvstDetailList = cpsInvstDetailDao
					.getCpsInvstDetailbyParentId(uidStageDto.getIdStage());
			for (CpsInvstDetail O : CpsInvstDetailList) {
				O.setIndChildSexTraffic(ServiceConstants.EMPTY_STRING);
				cpsInvstDetailDao.updtCpsInvstDetail(O, ServiceConstants.REQ_FUNC_CD_UPDATE);
			}
		}
		log.info(ServiceConstants.TRANSACTION_ID + commonCaseIdReq.getTransactionId());
		return new ServiceResHeaderDto();
	}

	private String CalcCurrentPersonRole(List<String> persnRolesList) {
		String pOverallRole = new String();
		/*
		 * Overall Roles are calculated based on a hierarchy of roles. Hierarchy
		 * is: DB Designated Victim/Perpetrator DP Designated Perpetrator DV
		 * Designated Victim VP Victim/Perpetrator VC Victim CL Client AP
		 * Alleged Perpetrator AV Alleged Victim UD Unknown (when Overall
		 * Disposition = Unable To Determine) UM Unknown (when Overall
		 * Disposition = Family Moved) UK Unknown (APS only) NO No Role
		 */
		if (persnRolesList.contains(ServiceConstants.DESG_VICT_PERP))
			pOverallRole = ServiceConstants.DESG_VICT_PERP;
		else if (persnRolesList.contains(ServiceConstants.DESIGNATED_PERP))
			pOverallRole = ServiceConstants.DESIGNATED_PERP;
		else if (persnRolesList.contains(ServiceConstants.DESIGNATED_VICTIM))
			pOverallRole = ServiceConstants.DESIGNATED_VICTIM;
		else if (persnRolesList.contains(ServiceConstants.AV_PERP))
			pOverallRole = ServiceConstants.AV_PERP;
		else if (persnRolesList.contains(ServiceConstants.ALLEG_VICTIM))
			pOverallRole = ServiceConstants.ALLEG_VICTIM;
		else if (persnRolesList.contains(ServiceConstants.STAGE_PERS_ROLE_CL))
			pOverallRole = ServiceConstants.STAGE_PERS_ROLE_CL;
		else if (persnRolesList.contains(ServiceConstants.ALLEG_PERP))
			pOverallRole = ServiceConstants.ALLEG_PERP;
		else if (persnRolesList.contains(ServiceConstants.ALLEGED_VICTIM))
			pOverallRole = ServiceConstants.ALLEGED_VICTIM;
		else if (persnRolesList.contains(ServiceConstants.UTD_INVST))
			pOverallRole = ServiceConstants.UTD_INVST;
		else if (persnRolesList.contains(ServiceConstants.UTD_MOVED))
			pOverallRole = ServiceConstants.UTD_MOVED;
		else if (persnRolesList.contains(ServiceConstants.UTD_UTC))
			pOverallRole = ServiceConstants.UTD_UTC;
		else if (persnRolesList.contains(ServiceConstants.UNKNOWN))
			pOverallRole = ServiceConstants.UNKNOWN;
		else if (persnRolesList.contains(ServiceConstants.NO_ROLE))
			pOverallRole = ServiceConstants.NO_ROLE;
		return pOverallRole;
	}

	private List<AllegtnPrsnDto> CalcVictimPerpRole(CalOverallDispReq calOverallDispReq,
			List<PerpVictmDto> perpAndVictimList) {
		List<AllegtnPrsnDto> perpAndVictimList1 = new ArrayList<>();
		if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CPS)
				|| calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CCL)
				|| calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_RCL)) {
			List<Long> uniquePidList = new ArrayList<>(
					perpAndVictimList.stream().map(PerpVictmDto::getIdVictim).collect(Collectors.toSet()));
			for (Long uidPerson : uniquePidList) {
				List<String> victimDispList = new ArrayList<>(
						perpAndVictimList.stream().filter(o -> o.getIdVictim().equals(uidPerson))
								.map(PerpVictmDto::getCdAllegDispositionVict).collect(Collectors.toSet()));
				String victimDispositon = calcOverallDisposition(calOverallDispReq, victimDispList);
				List<String> perpDispList = new ArrayList<>(
						perpAndVictimList.stream().filter(o -> o.getIdVictim().equals(uidPerson))
								.map(PerpVictmDto::getCdAllegDispositionPerp).collect(Collectors.toSet()));
				String perpDispositon = calcOverallDisposition(calOverallDispReq, perpDispList);
				AllegtnPrsnDto allegtnPrsnDto = new AllegtnPrsnDto();
				allegtnPrsnDto.setIdPerson(uidPerson);
				String personRole = ServiceConstants.NO_ROLE;
				PerpVictmDto perpVictmDto = CalPerpVictmRoleDto.getCalPerpVictmRoleDto().stream()
						.filter(o -> o.getCdAllegDispositionPerp().equals(perpDispositon)
								&& o.getCdAllegDispositionVict().equals(victimDispositon))
						.findFirst().orElse(null);
				if (!ObjectUtils.isEmpty(perpVictmDto))
					personRole = perpVictmDto.getCdStagePersRole();
				allegtnPrsnDto.setCdStagePersRole(personRole);
				perpAndVictimList1.add(allegtnPrsnDto);
			}
		}
		if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_AFC)) {
			List<Long> uniquePidList = new ArrayList<>(
					perpAndVictimList.stream().map(PerpVictmDto::getIdVictim).collect(Collectors.toSet()));
			for (Long uidPerson : uniquePidList) {
				List<String> victimDispList = new ArrayList<>(
						perpAndVictimList.stream().filter(o -> o.getIdVictim().equals(uidPerson))
								.map(PerpVictmDto::getCdAllegDispositionVict).collect(Collectors.toSet()));
				String victimDispositon = calcOverallDisposition(calOverallDispReq, victimDispList);
				List<String> perpDispList = new ArrayList<>(
						perpAndVictimList.stream().filter(o -> o.getIdVictim().equals(uidPerson))
								.map(PerpVictmDto::getCdAllegDispositionPerp).collect(Collectors.toSet()));
				String perpDispositon = calcOverallDisposition(calOverallDispReq, perpDispList);
				AllegtnPrsnDto allegtnPrsnDto = new AllegtnPrsnDto();
				allegtnPrsnDto.setIdPerson(uidPerson);
				String personRole = ServiceConstants.NO_ROLE;

				if (CodesConstant.CCONFIRM_CON.equals(victimDispositon)
						|| CodesConstant.CCONFIRM_CON.equals(perpDispositon))
					personRole = CodesConstant.CINVROLE_DP;
				if (CodesConstant.CCONFIRM_CRC.equals(victimDispositon)
						|| CodesConstant.CCONFIRM_CRC.equals(perpDispositon))
					personRole = CodesConstant.CINVROLE_DP;
				if (CodesConstant.CCONFIRM_COU.equals(victimDispositon)
						|| CodesConstant.CCONFIRM_COU.equals(perpDispositon))
					personRole = CodesConstant.CINVROLE_NO;
				if (CodesConstant.CCONFIRM_INC.equals(victimDispositon)
						|| CodesConstant.CCONFIRM_INC.equals(perpDispositon))
					personRole = CodesConstant.CINVROLE_UK;
				if (CodesConstant.CCONFIRM_UNF.equals(victimDispositon)
						|| CodesConstant.CCONFIRM_UNF.equals(perpDispositon))
					personRole = CodesConstant.CINVROLE_NO;
				if (CodesConstant.CCONFIRM_OTH.equals(victimDispositon)
						|| CodesConstant.CCONFIRM_OTH.equals(perpDispositon))
					personRole = CodesConstant.CINVROLE_NO;
				if (CodesConstant.CCONFIRM_ZZZ.equals(victimDispositon)
						|| CodesConstant.CCONFIRM_ZZZ.equals(perpDispositon))
					personRole = CodesConstant.CINVROLE_NO;
				allegtnPrsnDto.setCdStagePersRole(personRole);
				perpAndVictimList1.add(allegtnPrsnDto);
			}
		}
		if (ServiceConstants.CPGRMS_APS.equals(calOverallDispReq.getSzCdStageProgram()) ) {
			List<Long> uniquePidList = new ArrayList<>(
					perpAndVictimList.stream().map(PerpVictmDto::getIdVictim).collect(Collectors.toSet()));
			for (Long uidPerson : uniquePidList) {
				List<String> victimDispList = new ArrayList<>(
						perpAndVictimList.stream().filter(o -> o.getIdVictim().equals(uidPerson))
								.map(PerpVictmDto::getCdAllegDispositionVict).collect(Collectors.toSet()));
				String victimDispositon = calcOverallDisposition(calOverallDispReq, victimDispList);
				AllegtnPrsnDto allegtnPrsnDto = new AllegtnPrsnDto();
				allegtnPrsnDto.setIdPerson(uidPerson);
				String personRole = ServiceConstants.PERSON_ROLE_CLIENT;
				if (ServiceConstants.CASE_MERGED_IN_ERROR.equals(victimDispositon)) {
					personRole = ServiceConstants.NO_ROLE;
				}

				allegtnPrsnDto.setCdStagePersRole(personRole);
				perpAndVictimList1.add(allegtnPrsnDto);
          }
		}
		return perpAndVictimList1;
	}

	private String calcOverallDisposition(CalOverallDispReq calOverallDispReq, List<String> dispositionsList) {
		String pOverallDisposition = null;
		//artf250394 : dispositionsList contains distinct values. throw exception when dispositionsList contains only ONE disposition which is ZZZ.
		if (dispositionsList.size() == 1 && dispositionsList.get(0).equals(ServiceConstants.CASE_MERGED_IN_ERROR)) {
			throw new AllegationBusinessException(4106l);
		} // end if All allegations are ZZZ (Merged in Error)

		/*
		 * * Overall Dispositions are calculated based on a hierarchy of *
		 * dispositions by program. Example: Program is CPS. The dispositions
		 * for the allegations in this stage are {UTD, MOV, UTD, R/O, MOV, and
		 * UTD}. The Overall Disposition is UTD, since UTD is highest in the CPS
		 * hierarchy.
		 */

		/*
		 * * CPS - One or more allegations is: Reason to Believe Unable To
		 * Determine Family Moved Ruled Out Administrative Closure Exceptions:
		 * 1) If none of the dispositions are RTB, UTD, or MOV, then the
		 * dispositions must all be R/O or all be ADM. If the dispositions are a
		 * mix of R/O and ADM, no Overall Disposition can be calculated. A
		 * message will be returned to this effect. 2) If any of the
		 * dispositions are RTB, UTD, or MOV, none of the remaining dispositions
		 * can be ADM. If at least one is ADM, no Overall Disposition can be
		 * calculated. A message will be returned to this effect.
		 * 
		 * Both of these are functional errors and should not shut down the
		 * server. Anything else causing a failure of the Overall Disposition to
		 * be calculated is a technical error (bad data, etc.) and WILL shut
		 * down the server as well as log the error to Session Transcript.
		 */
		if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CPS)) {
			if (dispositionsList.contains(ServiceConstants.REASON_TO_BELIEVE))
				pOverallDisposition = ServiceConstants.REASON_TO_BELIEVE;
			else if (dispositionsList.contains(ServiceConstants.UNABLE_TO_DETRMN_DISP))
				pOverallDisposition = ServiceConstants.UNABLE_TO_DETRMN_DISP;
			else if (dispositionsList.contains(ServiceConstants.FAMILY_MOVED)
					|| dispositionsList.contains(ServiceConstants.UNABLE_TO_COMPLETE))
				pOverallDisposition = ServiceConstants.UNABLE_TO_COMPLETE;
			else if (dispositionsList.contains(ServiceConstants.RULED_OUT))
				pOverallDisposition = ServiceConstants.RULED_OUT;
			else if (dispositionsList.contains(ServiceConstants.ADMIN_CLOSURE))
				pOverallDisposition = ServiceConstants.ADMIN_CLOSURE;
		} // end if CPS
		if (calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_CCL)
				|| calOverallDispReq.getSzCdStageProgram().equals(ServiceConstants.CPGRMS_RCL)) {
			if (dispositionsList.contains(ServiceConstants.REASON_TO_BELIEVE))
				pOverallDisposition = ServiceConstants.REASON_TO_BELIEVE;
			else if (dispositionsList.contains(ServiceConstants.UNABLE_TO_DETRMN_DISP))
				pOverallDisposition = ServiceConstants.UNABLE_TO_DETRMN_DISP;
			else if (dispositionsList.contains(ServiceConstants.RULED_OUT))
				pOverallDisposition = ServiceConstants.RULED_OUT;
			else if (dispositionsList.contains(ServiceConstants.ADMIN_CLOSURE))
				pOverallDisposition = ServiceConstants.ADMIN_CLOSURE;
		} // end if CCL/RCL
		if (calOverallDispReq.getSzCdStageProgram().equals(CodesConstant.CPGRMS_AFC)) {
			if (dispositionsList.contains(CodesConstant.CCONFIRM_CON))
				pOverallDisposition = CodesConstant.CCONFIRM_CON;
			else if (dispositionsList.contains(CodesConstant.CCONFIRM_CRC))
				pOverallDisposition = CodesConstant.CCONFIRM_CRC;
			else if (dispositionsList.contains(CodesConstant.CCONFIRM_COU))
				pOverallDisposition = CodesConstant.CCONFIRM_COU;
			else if (dispositionsList.contains(CodesConstant.CCONFIRM_INC))
				pOverallDisposition = CodesConstant.CCONFIRM_INC;
			else if (dispositionsList.contains(CodesConstant.CCONFIRM_UNF))
				pOverallDisposition = CodesConstant.CCONFIRM_UNF;
			else if (dispositionsList.contains(CodesConstant.CCONFIRM_OTH))
				pOverallDisposition = CodesConstant.CCONFIRM_OTH;

		} // end if CPS
		if (calOverallDispReq.getSzCdStageProgram().equals(CodesConstant.CPGRMS_APS)) {
			if (dispositionsList.contains(CodesConstant.CDISPSTN_VAL))
				pOverallDisposition = CodesConstant.CDISPSTN_VAL;
			else if (dispositionsList.contains(CodesConstant.CDISPSTN_VRC))
				pOverallDisposition = CodesConstant.CDISPSTN_VRC;
			else if (dispositionsList.contains(CodesConstant.CDISPSTN_VNF))
				pOverallDisposition = CodesConstant.CDISPSTN_VNF;
			else if (dispositionsList.contains(CodesConstant.CDISPSTN_NVL))
				pOverallDisposition = CodesConstant.CDISPSTN_NVL;
			else if (dispositionsList.contains(CodesConstant.CDISPSTN_UTD))
				pOverallDisposition = CodesConstant.CDISPSTN_UTD;
			else if (dispositionsList.contains(CodesConstant.CDISPSTN_XXX))
				pOverallDisposition = CodesConstant.CDISPSTN_XXX;
		}
		return pOverallDisposition;
	}

	private void CallBlankOverallDispositionCPS(Long uidStage) {
		List<CpsInvstDetail> CpsInvstDetailList = cpsInvstDetailDao.getCpsInvstDetailbyParentId(uidStage);
		for (CpsInvstDetail O : CpsInvstDetailList) {
			O.setCdCpsInvstDtlOvrllDisptn(ServiceConstants.EMPTY_STRING);
			cpsInvstDetailDao.updtCpsInvstDetail(O, ServiceConstants.REQ_FUNC_CD_UPDATE);
		}
	}

	private void CallBlankOverallDispositionAPS(Long uidStage) {
		List<ApsInvstDetail> apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(uidStage);
		for (ApsInvstDetail O : apsInvstDetailList) {
			O.setCdApsInvstOvrallDisp(ServiceConstants.EMPTY_STRING);
			apsInvstDetailDao.updateApsInvstDetail(O);
		}
	}

	private void CallUpdateOverallDispositionAPS(Long uidStage,String cdAllegDisposition) {
		List<ApsInvstDetail> apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(uidStage);
		for (ApsInvstDetail O : apsInvstDetailList) {
			O.setCdApsInvstOvrallDisp(cdAllegDisposition);
			O.setCdApsInvstOvrallDisp(ServiceConstants.EMPTY_STRING);
			apsInvstDetailDao.updateApsInvstDetail(O);
		}
	}

	private void CallBlankOverallDispositionAPSMulti(Long uidStage) {
		List<ApsInvstDetail> apsInvstDetailList = apsInvstDetailDao.getApsInvstDetailbyParentId(uidStage);
		for (ApsInvstDetail O : apsInvstDetailList) {
			O.setCdApsInvstOvrallDisp(ServiceConstants.EMPTY_STRING);
			apsInvstDetailDao.updateApsInvstDetail(O);
		}
	}


	private void CallBlankOverallDispositionLIC(Long idStage) {
		List<LicensingInvstDtl> licInvstDetailList = licensingInvstDtlDao.getLicensingInvstDtlDaobyParentId(idStage);
		for (LicensingInvstDtl l : licInvstDetailList) {
			l.setCdLicngInvstOvrallDisp(ServiceConstants.EMPTY_STRING);
			licensingInvstDtlDao.saveLicensingInvstDtl(l);
		}
	}

	private Allegation pupulateAllegation(AllegationDetailDto allegationDetail) {
		Allegation allegation = null;
		if (!ObjectUtils.isEmpty(allegationDetail.getIdAllegation())) {
			allegation = allegtnDao.getAllegationById(allegationDetail.getIdAllegation());
		} else {
			allegation = new Allegation();
			allegation.setIdAllegation(allegationDetail.getIdAllegation());
		}
		Stage stage = new Stage();
		stage.setIdStage(allegationDetail.getIdStage());
		allegation.setStage(stage);
		allegation.setCdAllegDisposition(allegationDetail.getCdAllegDisposition());
		allegation.setCdAllegSeverity(allegationDetail.getCdAllegSeverity());
		allegation.setTxtAllegDuration(allegationDetail.getAllegDuration());
		allegation.setTxtDispstnSeverity(allegationDetail.getDisptnSeverity());
		allegation.setIndCoSlpgChildDth(allegationDetail.getIndCoSlpgChildDth());
		allegation.setIndCoSlpgSubstance(allegationDetail.getIndCoSlpgSubstance());
		allegation.setDtLastUpdate(new Date());
		allegation.setCdAllegType(allegationDetail.getCdAllegType());
		allegation.setIndRelinquishCstdy(allegationDetail.getIndRelinquishCstdy());
		allegation.setCdAllegIncidentStage(allegationDetail.getCdAllegIncidentStage());
		allegation.setIndFatality(allegationDetail.getIndFatalAlleg());
		allegation.setIndNearFatal(allegationDetail.getIndNearFatal());
		allegation.setTxtAvApChngCmnt(allegationDetail.getTxtChangeComments());
		allegation.setDtLastUpdate(new Date());
		allegation.setIdLastUpdatePerson(allegationDetail.getIdLastUpdatePerson());
		// PPM 85809 - CPS Tracking Maltreatment of Children in CVS
		allegation.setDtAllegedIncident(allegationDetail.getDtAllegedIncident());
		allegation.setIndApproxAllegedDate(allegationDetail.getIndApproxAllegedDate());
		if (!ObjectUtils.isEmpty(allegationDetail.getIdVictim())) {
			Person victim = new Person();
			victim.setIdPerson(allegationDetail.getIdVictim());
			allegation.setPersonByIdVictim(victim);
		}
		if (!ObjectUtils.isEmpty(allegationDetail.getIdAllegedPerpetrator())) {
			Person perp = new Person();
			perp.setIdPerson(allegationDetail.getIdAllegedPerpetrator());
			allegation.setPersonByIdAllegedPerpetrator(perp);
		} else {
			allegation.setPersonByIdAllegedPerpetrator(null);
		}

		//FCL-CCI R2
		allegation.setIdEvent(allegationDetail.getIdEvent());
		if(ObjectUtils.isEmpty(allegationDetail.getIdAllegation())){
			allegation.setDtCreated(new Date());
			allegation.setIdCreatedPerson(allegationDetail.getIdCreatedPerson());
		}
		allegation.setDtIncdnt(allegationDetail.getDtIncdnt());
		allegation.setIndDtInjury(allegationDetail.getIndDtInjury());
		allegation.setTmIncdnt(allegationDetail.getTmIncdnt());
		allegation.setIndTmInjury(allegationDetail.getIndTmInjury());
		allegation.setCdIncdntLctn(allegationDetail.getCdIncdntLctn());
		allegation.setTxtDescAllg(allegationDetail.getTxtDescAllg());

		return allegation;
	}

	public void handleDeletion(AllegationAUDReq allegationAUDReq) {
		AllegationDetailDto allegationDetail = allegationAUDReq.getAllegationDetail();
		allegtnDao.updateAllegation(pupulateAllegation(allegationDetail), ServiceConstants.REQ_FUNC_CD_DELETE, false);
		if (ServiceConstants.CPGRMS_CPS.equals(allegationAUDReq.getSzCdStageProgram())) {
			CallBlankOverallDispositionCPS(allegationDetail.getIdStage());
		}
		if (ServiceConstants.CPGRMS_CCL.equals(allegationAUDReq.getSzCdStageProgram())
				|| ServiceConstants.CPGRMS_RCL.equals(allegationAUDReq.getSzCdStageProgram())) {
			CallBlankOverallDispositionLIC(allegationDetail.getIdStage());
			allegationEvent(allegationAUDReq, ServiceConstants.REQ_FUNC_CD_DELETE);
		}
		if (ServiceConstants.CPGRMS_AFC.equals(allegationAUDReq.getSzCdStageProgram())) {
			facilAllgDtlDao.callBlankOverallDispositionFAC(allegationDetail.getIdStage());
		}
		if (ServiceConstants.CPGRMS_APS.equals(allegationAUDReq.getSzCdStageProgram())) {
			CallBlankOverallDispositionAPS(allegationDetail.getIdStage());
		}
		updtVictimPerpRoles(allegationDetail);
	}

	private void clalulatePersonRoles(List<AllegtnPrsnDto> perpList, String personType, String cdProgram) {
		if (ServiceConstants.CPGRMS_CPS.equals(cdProgram) || ServiceConstants.CPGRMS_CCL.equals(cdProgram)
				|| ServiceConstants.CPGRMS_RCL.equals(cdProgram)) {
			perpList.forEach(prsnRoleDto -> {
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.REASON_TO_BELIEVE)) {
					if (personType.equals(ServiceConstants.PERSON_TYPE_VICTIM))
						prsnRoleDto.setCdStagePersRole(ServiceConstants.DESIGNATED_VICTIM);
					if (personType.equals(ServiceConstants.PERSON_TYPE_PERP))
						prsnRoleDto.setCdStagePersRole(ServiceConstants.DESIGNATED_PERP);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.UNABLE_TO_DETRMN_DISP)) {
					prsnRoleDto.setCdStagePersRole(ServiceConstants.UNABLE_TO_DETRMN_ROLE);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.FAMILY_MOVED)) {
					prsnRoleDto.setCdStagePersRole(ServiceConstants.UNKNOWN_OR_UTC);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.UNABLE_TO_COMPLETE)) {
					prsnRoleDto.setCdStagePersRole(ServiceConstants.UNKNOWN_OR_UTC);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.RULED_OUT)) {
					prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.ADMIN_CLOSURE)) {
					prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.CASE_MERGED_IN_ERROR)) {
					prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
				}
			});
		}
		if (ServiceConstants.CPGRMS_AFC.equals(cdProgram)) {
			perpList.forEach(prsnRoleDto -> {
				if (prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CCONFIRM_CON)
						|| prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CCONFIRM_CRC)) {
					if (personType.equals(ServiceConstants.PERSON_TYPE_VICTIM))
						prsnRoleDto.setCdStagePersRole(ServiceConstants.DESIGNATED_VICTIM);
					if (personType.equals(ServiceConstants.PERSON_TYPE_PERP))
						prsnRoleDto.setCdStagePersRole(ServiceConstants.DESIGNATED_PERP);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CCONFIRM_COU)) {
					prsnRoleDto.setCdStagePersRole(CodesConstant.CINTROLE_NO);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CCONFIRM_INC)) {
					prsnRoleDto.setCdStagePersRole(CodesConstant.CINTROLE_UK);
				}
				if (prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CCONFIRM_UNF)
						|| prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CCONFIRM_OTH)) {
					prsnRoleDto.setCdStagePersRole(CodesConstant.CINTROLE_NO);
				}
			});
		}
		if (ServiceConstants.CPGRMS_APS.equals(cdProgram) ) {
			perpList.forEach(prsnRoleDto -> {
				if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.CASE_MERGED_IN_ERROR)) {
					prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
				}
				else if(personType.equals(ServiceConstants.PERSON_TYPE_VICTIM))
				{
					prsnRoleDto.setCdStagePersRole(ServiceConstants.PERSON_ROLE_CLIENT);
				}
				else if(personType.equals(ServiceConstants.PERSON_TYPE_PERP))
				{
					if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.CAPSALDP_VAL)) {
						prsnRoleDto.setCdStagePersRole(ServiceConstants.DESIGNATED_PERP);
					}
					if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.CAPSALDP_VRC)) {
						prsnRoleDto.setCdStagePersRole(ServiceConstants.DESIGNATED_PERP);
					}
					if (prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CAPSALDP_VNF)) {
						prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
					}
					if (prsnRoleDto.getCdAllegDisposition().equals(CodesConstant.CDISPSTN_NVL)) {
						prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
					}
					if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.UNABLE_TO_DETRMN_DISP)) {
						prsnRoleDto.setCdStagePersRole(ServiceConstants.UNKNOWN);
					}
					if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.OTHERXX)) {
						prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
					}
					if (prsnRoleDto.getCdAllegDisposition().equals(ServiceConstants.OTHERXXX)) {
						prsnRoleDto.setCdStagePersRole(ServiceConstants.NO_ROLE);
					}
				}

			});
		}
	}

	@Override
	public CommonDateRes fetchDtIntakeForIdStage(CommonStageIdReq commonStageIdReq) {
		Date intakedate = allegtnDao.fetchDtIntakeForIdStage(commonStageIdReq.getUlIdStage());
		//checking for Field Validation Exception Stage Id can not be null
		Optional<Date> intakeDtFromAllegtnDao = Optional.ofNullable(intakedate);
		CommonDateRes commonDateRes = new CommonDateRes();
		if(intakeDtFromAllegtnDao.isPresent()) {
			commonDateRes.setDate(intakeDtFromAllegtnDao.get());
		}else{
			commonDateRes.setDate(null);
		}
		log.info(ServiceConstants.TRANSACTION_ID + commonStageIdReq.getTransactionId());
		return commonDateRes;
	}

	public void updtVictimPerpRoles(AllegationDetailDto allegationDetail) {
		Integer AVVictimCnt = allegtnDao.getVictimCount(allegationDetail.getIdVictim(), allegationDetail.getIdStage(),
				allegationDetail.getIdAllegation());
		Integer APVictimCnt = 0;
		if (!TypeConvUtil.isNullOrEmpty(allegationDetail.getIdAllegedPerpetrator()))
			APVictimCnt = allegtnDao.getVictimCount(allegationDetail.getIdAllegedPerpetrator(),
					allegationDetail.getIdStage(), allegationDetail.getIdAllegation());

		Integer AVPerpCnt = allegtnDao.getPerpCount(allegationDetail.getIdVictim(), allegationDetail.getIdStage(),
				allegationDetail.getIdAllegation());
		Integer APPerpCnt = 0;
		if (!TypeConvUtil.isNullOrEmpty(allegationDetail.getIdAllegedPerpetrator()))
			APPerpCnt = allegtnDao.getPerpCount(allegationDetail.getIdAllegedPerpetrator(),
					allegationDetail.getIdStage(), allegationDetail.getIdAllegation());

		/*
		 * Determine victim's and perp's new roles, based on their remaining
		 * allegations: Named As New Role Victim & Perp VICTIM_PERP Victim Only
		 * ALLEGED_VICTIM Perp Only ALLEGED_PERP No Remaining NO_ROLE
		 * Allegations
		 */

		/* Victim Role */

		if ((AVVictimCnt > 0) && (AVPerpCnt > 0))

			allegationDetail.setCdStagePersRole2(ServiceConstants.AV_PERP);

		if ((AVVictimCnt == 0) && (AVPerpCnt > 0))
			allegationDetail.setCdStagePersRole2(ServiceConstants.ALLEG_PERP);

		if ((AVVictimCnt > 0) && (AVPerpCnt == 0))
			allegationDetail.setCdStagePersRole2(ServiceConstants.ALLEG_VICTIM);

		if ((AVVictimCnt == 0) && (AVPerpCnt == 0))
			allegationDetail.setCdStagePersRole2(ServiceConstants.NO_ROLE);

		/* Perp Role */
		if ((APVictimCnt > 0) && (APPerpCnt > 0))
			allegationDetail.setCdStagePersRole(ServiceConstants.AV_PERP);

		if ((APVictimCnt == 0) && (APPerpCnt > 0))
			allegationDetail.setCdStagePersRole(ServiceConstants.ALLEG_PERP);

		if ((APVictimCnt > 0) && (APPerpCnt == 0))
			allegationDetail.setCdStagePersRole(ServiceConstants.ALLEG_VICTIM);

		if ((APVictimCnt == 0) && (APPerpCnt == 0))
			allegationDetail.setCdStagePersRole(ServiceConstants.NO_ROLE);
		/// * Update victim's and perp's role in STAGE_PERSON_LINK. */
		stagePersonLinkDao.updateStagePersonLink(allegationDetail);
	}

	private void allegationEvent(AllegationAUDReq allegationAUDReq, String operation) {
		if(allegationAUDReq.getAllegationDetail().getIdEvent() == null) {
			Event event = new Event();
			event.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
			event.setCdEventType(ServiceConstants.ALLEGATION_EVENT_TYPE);
			event.setTxtEventDescr(allegationAUDReq.getAllegationDetail().getEventDescr());
			event.setDtEventOccurred(new Date());
			event.setDtLastUpdate(new Date());
			event.setDtEventCreated(new Date());
			Stage stage = stageDao.getStageEntityById(allegationAUDReq.getAllegationDetail().getIdStage());
			event.setStage(stage);
			event.setIdCase(allegationAUDReq.getAllegationDetail().getIdCase());
			if(!ObjectUtils.isEmpty(allegationAUDReq.getAllegationDetail().getIdCreatedPerson())) {
				Person person = personDao.getPersonByPersonId(allegationAUDReq.getAllegationDetail().getIdCreatedPerson());
				event.setPerson(person);
			}
			Long idEvent = eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_ADD);
			allegationAUDReq.getAllegationDetail().setIdEvent(idEvent);
		} else if(operation.equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			Event event = eventDao.getEventById(allegationAUDReq.getAllegationDetail().getIdEvent());
			if(event != null) {
				event.setDtEventModified(new Date());
				event.setDtLastUpdate(new Date());
				event.setTxtEventDescr(event.getTxtEventDescr().replace("added", "deleted"));
				eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_UPDATE);
			}
		} else {
			Event event = eventDao.getEventById(allegationAUDReq.getAllegationDetail().getIdEvent());
			if(event != null) {
				event.setDtEventModified(new Date());
				event.setDtLastUpdate(new Date());
				eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_UPDATE);
			}
		}
	}
	@Override
	public boolean getValidAllegations(Long idCase){
		return allegtnDao.getValidAllegations(idCase);
	}


	/**
	 * Method help to call the DB and check the allegation problem count
	 *
	 * @param idAllegation selected allegation id
	 * @return set data into res and return
	 */
	@Override
	public CommonCountRes getAllegationProblemCount(Long idAllegation) {
		int count = allegtnDao.getAllegationProblemCount(idAllegation);
		CommonCountRes res = new CommonCountRes();
		res.setPerfTmTotQty(count);
		return res;
	}

	/**
	 * Method helps to call the Allegation dao
	 *
	 * @param idStage selected stage id
	 * @param idAllegation selected allegation id for delete the record
	 */
	@Override
	public void deleteSPSourcesForAllegationRecord(Long idStage, Long idAllegation) {
		allegtnDao.deleteSPSourceForAllegationByAllegationId(idStage, idAllegation);
	}

	/**
	 * Method Name: getEvent Method Description: This method is called for
	 * finding out Event Id for given Stage and taskcode
	 *
	 * @param idStage
	 * @param emrTaskCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<EventDto> getEventIds(Long idStage, String emrTaskCode) {
		List<EventDto> eventDtoList = new ArrayList<>();
		List<Long> idEvents = new ArrayList<>();
		eventDtoList = eventDao.getEventsByStageIDAndTaskCode(idStage,emrTaskCode);
        return eventDtoList;

	}
}
