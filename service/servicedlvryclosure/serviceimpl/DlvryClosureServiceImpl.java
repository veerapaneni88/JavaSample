package us.tx.state.dfps.service.servicedlvryclosure.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dao.CriminalHistoryRecordsCheckDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.StageRegionDao;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckOutDto;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.StageRegionInDto;
import us.tx.state.dfps.service.admin.dto.StageRegionOutDto;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.casepackage.service.PcspService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.request.ServiceDlvryClosureReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.servicedlvryclosure.dao.ServiceDlvryClosureDao;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureService;
import us.tx.state.dfps.service.servicedlvryclosure.service.DlvryClosureSubmitService;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.service.servicedlvryclosure.dto.ClosureNotificationLettersDto;
import us.tx.state.service.servicedlvryclosure.dto.RGPostEventDto;
import us.tx.state.service.servicedlvryclosure.dto.RGStageDto;
import us.tx.state.service.servicedlvryclosure.dto.ServiceDlvryClosureDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Service
 * Retrieves the Ccmn45dDao,Cint40dDao,Csvc21dDao,Csesc2dDao Dam Details Aug 23,
 * 2017- 5:04:42 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class DlvryClosureServiceImpl implements DlvryClosureService {

	@Autowired
	private EventIdDao eventIdDao;

	@Autowired
	private StageRegionDao stageRegionDao;

	@Autowired
	private CriminalHistoryRecordsCheckDao criminalHistoryRecordsCheckDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private PcspService pcspService;

	@Autowired
	private SrvreferralslDao srvrflDao;

	@Autowired
	PcspListPlacmtDao pcspDao;

	@Autowired
	CaseSummaryService caseSummaryService;

	@Autowired
	DlvryClosureSubmitService dlvryClosureSubmitService;

	@Autowired
	ServiceDlvryClosureDao serviceDlvryClosureDao;

	public static final String SVC_CD_EVENT_STATUS_PENDING = "PEND";

	/**
	 * @method name : dlvryClosureService
	 * @Method Description: This Retrieves the
	 *         Ccmn45dDao,Cint40dDao,Csvc21dDao,Csesc2dDao Dam Details.
	 * @param serviceDlvryClosureReq
	 * @return ServiceDlvryClosureDto
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceDlvryClosureDto dlvryClosureService(ServiceDlvryClosureReq serviceDlvryClosureReq) {

		long idStage = serviceDlvryClosureReq.getIdStage();
		long idEvent = 0;
		String cdStage = serviceDlvryClosureReq.getCdStage();

		String pageMode = serviceDlvryClosureReq.getPageMode();

		ServiceDlvryClosureDto serviceDlvryClosureDto = new ServiceDlvryClosureDto();

		List<EventDto> eventDto = eventDao.getEventByStageIDAndTaskCode(idStage, serviceDlvryClosureReq.getCdTask());
		if (CollectionUtils.isNotEmpty(eventDto)) {
			idEvent = eventDto.get(0).getIdEvent();
		}
		List<PcspDto> pcspList = pcspList(serviceDlvryClosureReq.getIdCase(), cdStage,
				serviceDlvryClosureReq.getCdStageProgram());

		serviceDlvryClosureDto.setPcspListDto(pcspList);

		RGStageDto rgStage = getStageDetails(idStage);
		serviceDlvryClosureDto.setRgStageDto(rgStage);

		if (idEvent != 0) {
			RGPostEventDto rgEvent = getEventDto(idEvent);

			serviceDlvryClosureDto.setRgPostEventDto(rgEvent);
			List<Long> chrPersonIdList = fetchcriminalHistoryRecordsPersonIdList(idStage);

			serviceDlvryClosureDto.setChrPersonIdList(chrPersonIdList);

		}
		if (!ObjectUtils.isEmpty(serviceDlvryClosureDto.getRgStageDto().getDtStageClose())) {
			pageMode = ServiceConstants.NEW;
		}
		Boolean approvalCps = pcspDao.setHasBeenSubmittedForApprovalCps(idEvent);
		serviceDlvryClosureDto.setIdEvent(idEvent);
		serviceDlvryClosureDto.setApprovalCps(approvalCps);
		CommonHelperReq commonHelperReq = new CommonHelperReq();
		commonHelperReq.setIdStage(idStage);
		boolean status = caseSummaryService.getCaseCheckoutStatus(commonHelperReq).getCaseCheckoutStatus();
		serviceDlvryClosureDto.setCheckedOutToMps(status);
		if (!ObjectUtils.isEmpty(approvalCps) && approvalCps && ServiceConstants.EDIT_M.equals(pageMode)
				&& !ObjectUtils.isEmpty(serviceDlvryClosureReq.getApprovalMode())
				&& !serviceDlvryClosureReq.getApprovalMode() && SVC_CD_EVENT_STATUS_PENDING
						.equalsIgnoreCase(serviceDlvryClosureDto.getRgPostEventDto().getCdEventStatus())) {
			serviceDlvryClosureDto.setInformationMsg(ServiceConstants.MSG_CMN_INVLD_APRVL);
			serviceDlvryClosureDto.setPopupMsg(ServiceConstants.MSG_CMN_INVLD_APRVL_POPUP);
		} else {
			if (status && !ObjectUtils.isEmpty(pageMode) && !ServiceConstants.PAGEMODE_NEW.equals(pageMode)
					&& !ObjectUtils.isEmpty(serviceDlvryClosureReq.getApprovalMode())) {
				serviceDlvryClosureDto.setInformationMsg(ServiceConstants.MSG_CASE_CHECKEDOUT);
			}
		}

		List<ClosureNotificationLettersDto> finalClosureList = serviceDlvryClosureDao
				.getClosureNotificationletters(serviceDlvryClosureDto.getRgStageDto().getIdStage());
		serviceDlvryClosureDto.setClosureNotificationlist(finalClosureList);
		serviceDlvryClosureDto.setPageMode(pageMode);
		
		if (!ObjectUtils.isEmpty(serviceDlvryClosureDto) && !ObjectUtils.isEmpty(serviceDlvryClosureDto.getRgStageDto())
				&& !ObjectUtils.isEmpty(serviceDlvryClosureDto.getRgStageDto().getIdCase())) {
			serviceDlvryClosureDto.setPchasOpenSUBStage(getPCHasOpenSUBStage(serviceDlvryClosureDto.getRgStageDto().getIdCase()));
		}

		return serviceDlvryClosureDto;
	}

	/**
	 * @method name : getStageDetails
	 * @Method Description: This Retrieves the Ccmn45dDao Dam Details.
	 * @param idStage
	 * @return RGStageDto
	 * 
	 */
	@Override
	public RGStageDto getStageDetails(long idStage) {
		StageRegionInDto stageRegionInDto = new StageRegionInDto();
		stageRegionInDto.setIdStage(idStage);
		RGStageDto rgStage = new RGStageDto();
		List<StageRegionOutDto> stageRegionListOutDto = stageRegionDao.getStageDtls(stageRegionInDto);
		if (!TypeConvUtil.isNullOrEmpty(stageRegionListOutDto)) {
			List<RGStageDto> rgStageListDto = new ArrayList<>();
			for (StageRegionOutDto stageRegionOutDto : stageRegionListOutDto) {
				RGStageDto rgStageDto = new RGStageDto();
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getIdStage())) {
					rgStageDto.setIdStage(stageRegionOutDto.getIdStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getTmSysTmStageClose())) {
					rgStageDto.setSysTmStageClose(stageRegionOutDto.getTmSysTmStageClose());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getTmSysTmStageStart())) {
					rgStageDto.setSysTmStageStart(stageRegionOutDto.getTmSysTmStageStart());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getIdUnit())) {
					rgStageDto.setIdUnit(stageRegionOutDto.getIdUnit());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getIndStageClose())) {
					rgStageDto.setIndStageClose(stageRegionOutDto.getIndStageClose());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getNmStage())) {
					rgStageDto.setNmStage(stageRegionOutDto.getNmStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStage())) {
					rgStageDto.setCdStage(stageRegionOutDto.getCdStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageClassification())) {
					rgStageDto.setCdStageClassification(stageRegionOutDto.getCdStageClassification());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageCnty())) {
					rgStageDto.setCdStageCnty(stageRegionOutDto.getCdStageCnty());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageCurrPriority())) {
					rgStageDto.setCdStageCurrPriority(stageRegionOutDto.getCdStageCurrPriority());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageInitialPriority())) {
					rgStageDto.setCdStageInitialPriority(stageRegionOutDto.getCdStageInitialPriority());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageProgram())) {
					rgStageDto.setCdStageProgram(stageRegionOutDto.getCdStageProgram());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageReasonClosed())) {
					rgStageDto.setCdStageReasonClosed(stageRegionOutDto.getCdStageReasonClosed());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getDtStageClose())) {
					rgStageDto.setDtStageClose(stageRegionOutDto.getDtStageClose());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getDtStageStart())) {
					rgStageDto.setDtStartDate(stageRegionOutDto.getDtStageStart());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getIdCase())) {
					rgStageDto.setIdCase(stageRegionOutDto.getIdCase());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getIdSituation())) {
					rgStageDto.setIdSituation(stageRegionOutDto.getIdSituation());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getStageClosureCmnts())) {
					rgStageDto.setStageClosureCmnts(stageRegionOutDto.getStageClosureCmnts());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getStagePriorityCmnts())) {
					rgStageDto.setStagePriorityCmnts(stageRegionOutDto.getStagePriorityCmnts());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageRegion())) {
					rgStageDto.setCdStageRegion(stageRegionOutDto.getCdStageRegion());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageRsnPriorityChgd())) {
					rgStageDto.setCdStageRsnPriorityChgd(stageRegionOutDto.getCdStageRsnPriorityChgd());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getCdStageType())) {
					rgStageDto.setCdStageType(stageRegionOutDto.getCdStageType());
				}
				if (!TypeConvUtil.isNullOrEmpty(stageRegionOutDto.getTsLastUpdate())) {
					rgStageDto.setLastUpdate(stageRegionOutDto.getTsLastUpdate());
				}
				rgStageListDto.add(rgStageDto);
			}
			if (!ObjectUtils.isEmpty(rgStageListDto)) {
				rgStage = rgStageListDto.get(0);
			}
		}

		return rgStage;
	}

	/**
	 * Method Name: getEventDto Method Description:This Retrieves the Ccmn45dDao
	 * Dam Details
	 * 
	 * @param idEvent
	 * @return RGPostEventDto
	 */
	private RGPostEventDto getEventDto(long idEvent) {
		EventIdInDto eventIdInDto = new EventIdInDto();
		eventIdInDto.setIdEvent(idEvent);
		RGPostEventDto rgPostEvent = new RGPostEventDto();
		List<EventIdOutDto> eventIdListOutDto = eventIdDao.getEventDetailList(eventIdInDto);
		if (!TypeConvUtil.isNullOrEmpty(eventIdListOutDto)) {
			List<RGPostEventDto> rgPostEventListDto = new ArrayList<>();
			for (EventIdOutDto eventIdOutDto : eventIdListOutDto) {
				RGPostEventDto rgPostEventDto = new RGPostEventDto();
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getCdTask())) {
					rgPostEventDto.setCdTask(eventIdOutDto.getCdTask());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getCdEventStatus())) {
					rgPostEventDto.setCdEventStatus(eventIdOutDto.getCdEventStatus());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getCdEventType())) {
					rgPostEventDto.setCdEventType(eventIdOutDto.getCdEventType());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getEventDescr())) {
					rgPostEventDto.setEventDescr(eventIdOutDto.getEventDescr());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getIdEvent())) {
					rgPostEventDto.setIdEvent(eventIdOutDto.getIdEvent());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getIdStage())) {
					rgPostEventDto.setIdStage(eventIdOutDto.getIdStage());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getIdPerson())) {
					rgPostEventDto.setIdPerson(eventIdOutDto.getIdPerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getDtEventOccurred())) {
					rgPostEventDto.setDtEventOccurred(eventIdOutDto.getDtEventOccurred());
				}
				if (!TypeConvUtil.isNullOrEmpty(eventIdOutDto.getTsLastUpdate())) {
					rgPostEventDto.setLastUpdate(eventIdOutDto.getTsLastUpdate());
				}
				rgPostEventListDto.add(rgPostEventDto);
			}
			rgPostEvent = rgPostEventListDto.get(0);
		}
		return rgPostEvent;
	}

	/**
	 * Method Name: fetchcriminalHistoryRecordsPersonIdList Method Description:
	 * This Retrieves the Ccmn45dDao Dam Details.
	 * 
	 * @param idStage
	 * @return List<Long>
	 */
	private List<Long> fetchcriminalHistoryRecordsPersonIdList(Long idStage) {

		CriminalHistoryRecordsCheckInDto criminalHistoryRecordsCheckInDto = new CriminalHistoryRecordsCheckInDto();
		criminalHistoryRecordsCheckInDto.setIdStage(idStage);
		List<CriminalHistoryRecordsCheckOutDto> criminalHistoryRecordsCheckListOutDto = criminalHistoryRecordsCheckDao
				.getCriminalCheckRecords(criminalHistoryRecordsCheckInDto);
		List<Long> personIdList = new ArrayList<>();
		if (!TypeConvUtil.isNullOrEmpty(criminalHistoryRecordsCheckListOutDto)) {
			for (CriminalHistoryRecordsCheckOutDto criminalHistoryRecordsCheckOutDto : criminalHistoryRecordsCheckListOutDto) {
				if (!TypeConvUtil.isNullOrEmpty(criminalHistoryRecordsCheckOutDto.getIdPerson())) {
					personIdList.add(criminalHistoryRecordsCheckOutDto.getIdPerson());
				}
			}
		}
		return personIdList;
	}

	private List<PcspDto> pcspList(long idCase, String cdStage, String stageProgram) {
		CommonHelperReq commonHelperReq = new CommonHelperReq();
		commonHelperReq.setIdCase(idCase);
		List<PcspDto> pcspList = new ArrayList<>();
		Boolean checkLegacyPCSP = pcspService.checkLegacyPCSP(commonHelperReq).getIsCheckLegacyPCSP();
		if (!ObjectUtils.isEmpty(checkLegacyPCSP) && checkLegacyPCSP && CodesConstant.CSTAGES_FPR.equals(cdStage)
				&& CodesConstant.CPGRMS_CPS.equals(stageProgram)) {
			PcspReq pcspReq = new PcspReq();
			pcspReq.setCdStage(cdStage);
			pcspReq.setCaseId(idCase);
			pcspList = srvrflDao.getPcspList(pcspReq);
		}
		return pcspList;
	}

	/**
	 * Method Name: getClosureNotificationLetter Method Description: This method
	 * is to retrieve all closure Notification records from Data base
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public List<ClosureNotificationLettersDto> getClosureNotificationLetter(Long idStage) {
		return serviceDlvryClosureDao.getClosureNotificationletters(idStage);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonBooleanRes getPrintTaskExists(CommonHelperReq commonHelperReq) {
		CommonBooleanRes resp = null;
		if (commonHelperReq != null && commonHelperReq.getIdCase() != null && commonHelperReq.getIdStage() != null
				&& commonHelperReq.getIdEvent() != null) {
			resp = serviceDlvryClosureDao.getPrintTaskExistsFPR(commonHelperReq.getIdCase(),
					commonHelperReq.getIdStage(), commonHelperReq.getIdEvent());
		}
		return resp;
	}

	/**
	 * Method Name: getPCHasOpenSUBStage Method Description: This method is to check
	 * if principal child has open SUB Stage on the respective case.
	 * 
	 * @param idCase
	 *            return boolean
	 */
	@Override
	public boolean getPCHasOpenSUBStage(Long idCase) {
		return serviceDlvryClosureDao.getPCHasOpenSUBStage(idCase);
	}

}
