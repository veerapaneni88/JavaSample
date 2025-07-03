package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.CpsInvstDetailInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.EmergencyAssistInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.EventUpdEventStatusDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.CpsEmergencyResponseDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EventUpdEventStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusOutDto;
import us.tx.state.dfps.service.admin.dto.SaveEmergAssistiDto;
import us.tx.state.dfps.service.admin.dto.SaveEmergAssistoDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.SaveEmergAssistService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * saves emergency assistance details Aug 9, 2017- 2:39:11 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Service
@Transactional
public class SaveEmergAssistServiceImpl implements SaveEmergAssistService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageEventStatusCommonService objCcmn06uService;

	@Autowired
	ApprovalCommonService objCcmn05uService;

	@Autowired
	PostEventStageStatusService objCcmn01uService;

	@Autowired
	TodoUpdDtTodoCompletedDao objCinv43dDao;

	@Autowired
	EventUpdEventStatusDao objCcmn62dDao;

	@Autowired
	EmergencyAssistInsUpdDelDao objCinv16dDao;

	@Autowired
	CpsInvstDetailInsUpdDelDao objCinv12dDao;

	private static final Logger log = Logger.getLogger(SaveEmergAssistServiceImpl.class);

	/**
	 * 
	 * Method Name: callSaveEmergAssistService Method Description: This method
	 * saves emergency assistance details
	 * 
	 * @param liAssistiDto
	 * @return SaveEmergAssistoDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SaveEmergAssistoDto callSaveEmergAssistService(SaveEmergAssistiDto saveEmergAssistiDto) {
		log.debug("Entering method callSaveEmergAssistService in SaveEmergAssistServiceImpl");
		SaveEmergAssistiDto pInputMsg = saveEmergAssistiDto;
		SaveEmergAssistoDto pOutputMsg = new SaveEmergAssistoDto();
		String RetVal = ServiceConstants.EMPTY_STRING;
		boolean isAdd = TypeConvUtil.isNullOrEmpty(pInputMsg.getRowCcmn01UG00().getIdEvent());
		StageTaskInDto pCCMN06UInputRec = processCheckStatus(pInputMsg, isAdd);
		RetVal = objCcmn06uService.checkStageEventStatus(pCCMN06UInputRec);
		if (!isAdd) {
			pOutputMsg.setIdEvent(pInputMsg.getRowCcmn01UG00().getIdEvent());
		}
		if (!RetVal.isEmpty() && RetVal.equalsIgnoreCase(ServiceConstants.ARC_SUCCESS)) {
			RetVal = ServiceConstants.FND_SUCCESS;
		} else {
			RetVal = ServiceConstants.FND_FAIL;
		}
		boolean isEventIdZero = TypeConvUtil.isNullOrEmpty(pInputMsg.getIdEvent());
		if (ServiceConstants.FND_SUCCESS.equals(RetVal)) {
			PostEventStageStatusInDto ccmn01uiDto = processccmn01Service(pInputMsg);
			PostEventStageStatusOutDto postEventStageStatusOutDto = objCcmn01uService
					.callPostEventStageStatusService(ccmn01uiDto);
			pOutputMsg.setIdEvent(postEventStageStatusOutDto.getIdEvent());
			if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getSysNbrReserved1()) && !pInputMsg.getSysNbrReserved1()) {
				if (!isEventIdZero) {
					ApprovalCommonInDto ccmn05uiDto = new ApprovalCommonInDto();
					ccmn05uiDto.setSysNbrReserved1(pInputMsg.getSysNbrReserved1());
					if (pInputMsg.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
						ccmn05uiDto.setIdEvent(pInputMsg.getRowCcmn01UG00().getIdEvent());
					} else {
						EventUpdEventStatusInDto pCCMN62DInputRec = processUpdateEvent(pInputMsg);
						objCcmn62dDao.updateEvent(pCCMN62DInputRec);
						ccmn05uiDto.setIdEvent(pInputMsg.getIdEvent());
					}
					objCcmn05uService.callCcmn05uService(ccmn05uiDto);
				}
				if (pInputMsg.getRowCcmn01UG00().getIdEvent() != 0
						&& !TypeConvUtil.isNullOrEmpty(pInputMsg.getRowCcmn01UG00().getCdEventStatus())
						&& !pInputMsg.getRowCcmn01UG00().getCdEventStatus()
								.equalsIgnoreCase(ServiceConstants.EVENT_STATUS_COMPLETE)) {
					TodoUpdDtTodoCompletedInDto pCINV43DInputRec = processUpdateTODOEvent(pInputMsg);
					objCinv43dDao.updateTODOEvent(pCINV43DInputRec);
				}
			}
			if (pInputMsg.getRowCinv12SIG00() != null && !pInputMsg.getRowCinv12SIG00().isEmpty()) {
				List<CpsEmergencyResponseDto> rowCinv12SIG00List = pInputMsg.getRowCinv12SIG00();
				for (CpsEmergencyResponseDto rowCinv12SIG00 : rowCinv12SIG00List) {
					EmergencyAssistInsUpdDelInDto pCINV16DInputRec = new EmergencyAssistInsUpdDelInDto();
					EmergencyAssistInsUpdDelInDto cinv16diDto = processCinv16d(rowCinv12SIG00, pCINV16DInputRec,
							pInputMsg, pOutputMsg);
					objCinv16dDao.cuEmergencyAssistanceDtls(cinv16diDto);
				}
			}
			CpsInvstDetailInsUpdDelInDto pCINV12DInputRec = processCinv12(pInputMsg);
			switch (pCINV12DInputRec.getReqFuncCd()) {
			case ServiceConstants.REQ_FUNC_CD_ADD:
				Long idEvent = objCinv12dDao.getNewCpsInvstId();
				pCINV12DInputRec.setIdEvent(idEvent);
				pCINV12DInputRec.setTsLastUpdate(pInputMsg.getRowCcmn01UG00().getTsLastUpdate());
				objCinv12dDao.saveCpsInvstDetail(pCINV12DInputRec);
				break;
			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				objCinv12dDao.updateCpsInvstDetail(pCINV12DInputRec);
				break;
			case ServiceConstants.REQ_FUNC_CD_DELETE:
				break;
			}
			/*
			 * if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getUlIdEvent())) {
			 * pOutputMsg.setUlIdEvent(pInputMsg.getUlIdEvent()); }
			 */
		}
		log.debug("Exiting method callSaveEmergAssistService in SaveEmergAssistServiceImpl");
		return pOutputMsg;
	}

	/**
	 * 
	 * Method Name: processccmn01Service Method Description: This method maps
	 * Ccmn01uiDto with SaveEmergAssistiDto.
	 * 
	 * @param pInputMsg
	 * @return PostEventStageStatusInDto
	 */
	private PostEventStageStatusInDto processccmn01Service(SaveEmergAssistiDto pInputMsg) {
		PostEventStageStatusInDto ccmn01uiDto = new PostEventStageStatusInDto();
		ccmn01uiDto.setIdEvent(pInputMsg.getRowCcmn01UG00().getIdEvent());
		ccmn01uiDto.setIdStage(pInputMsg.getRowCcmn01UG00().getIdStage());
		ccmn01uiDto.setIdPerson(pInputMsg.getRowCcmn01UG00().getIdPerson());
		ccmn01uiDto.setCdTask(pInputMsg.getRowCcmn01UG00().getCdTask());
		ccmn01uiDto.setCdEventType(pInputMsg.getRowCcmn01UG00().getCdEventType());
		ccmn01uiDto.setDtEventLastUpdate(pInputMsg.getRowCcmn01UG00().getTsLastUpdate());
		ccmn01uiDto.setDtEventOccurred(pInputMsg.getRowCcmn01UG00().getDtEventOccurred());
		ccmn01uiDto.setEventDescr(pInputMsg.getRowCcmn01UG00().getEventDescr());
		ccmn01uiDto.setCdEventStatus(pInputMsg.getRowCcmn01UG00().getCdEventStatus());
		ccmn01uiDto.setReqFuncCd(pInputMsg.getReqFuncCd());
		ccmn01uiDto.setCdReqFunction(pInputMsg.getReqFuncCd());
		ccmn01uiDto.setCdScrDataAction(pInputMsg.getReqFuncCd());
		return ccmn01uiDto;
	}

	/**
	 * 
	 * Method Name: processCinv12 Method Description: This method maps
	 * Cinv12diDto with List<SaveEmergAssistiDto>
	 * 
	 * @param liAssistiDto
	 * @return CpsInvstDetailInsUpdDelInDto
	 */
	private CpsInvstDetailInsUpdDelInDto processCinv12(SaveEmergAssistiDto assistiDto) {
		// SaveEmergAssistiDto assistiDto =
		// liAssistiDto.get(ServiceConstants.Zero);
		CpsInvstDetailInsUpdDelInDto pCINV12DInputRec = new CpsInvstDetailInsUpdDelInDto();
		pCINV12DInputRec.setCdCpsInvstDtlFamIncm(assistiDto.getRowCinv12SiG01().getCdCpsInvstDtlFamIncm());
		pCINV12DInputRec.setCdCpsOverallDisptn(assistiDto.getRowCinv12SiG01().getCdCpsOverallDisptn());
		pCINV12DInputRec.setDtCPSInvstDtlAssigned(assistiDto.getRowCinv12SiG01().getDtCPSInvstDtlAssigned());
		pCINV12DInputRec.setDtCPSInvstDtlBegun(assistiDto.getRowCinv12SiG01().getDtCPSInvstDtlBegun());
		pCINV12DInputRec.setDtCpsInvstDtlComplt(assistiDto.getRowCinv12SiG01().getDtCpsInvstDtlComplt());
		pCINV12DInputRec.setDtCPSInvstDtlIntake(assistiDto.getRowCinv12SiG01().getDtCPSInvstDtlIntake());
		pCINV12DInputRec.setIdEvent(assistiDto.getRowCinv12SiG01().getIdEvent());
		pCINV12DInputRec.setIdStage(assistiDto.getRowCinv12SiG01().getIdStage());
		pCINV12DInputRec.setIndCpsInvstSafetyPln(assistiDto.getRowCinv12SiG01().getIndCpsInvstSafetyPln());
		pCINV12DInputRec.setIndCpsInvstDtlRaNa(assistiDto.getRowCinv12SiG01().getIndCpsInvstDtlRaNa());
		// Setting CpsInvestigation Form attributes.
		/*
		 * pCINV12DInputRec.setSzCdCpsInvstCpsLeJointContact(assistiDto.
		 * getRowCinv12SiG01().getRowCINV10DOG00().
		 * getCdCpsInvstCpsLeJointContact());
		 * pCINV12DInputRec.setSzCdVictimTaped(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getSzCdVictimTaped());
		 * pCINV12DInputRec.setBIndParentGivenGuide(assistiDto.getRowCinv12SiG01
		 * ().getRowCINV10DOG00().getIndParentGivenGuide());
		 * pCINV12DInputRec.setSzTxtVictimPhoto(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getSzTxtVictimPhoto());
		 * pCINV12DInputRec.setSzTxtRsnOpenServices(assistiDto.getRowCinv12SiG01
		 * ().getRowCINV10DOG00().getSzTxtRsnOpenServices());
		 * pCINV12DInputRec.setBIndMultiPersFound(assistiDto.getRowCinv12SiG01()
		 * .getRowCINV10DOG00().getIndMultiPersFound());
		 * pCINV12DInputRec.setCIndCpsInvstAbbrv(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndCpsInvstAbbrv());
		 * pCINV12DInputRec.setCIndMeth(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndMeth());
		 * pCINV12DInputRec.setSzTxtRsnInvClosed(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getSzTxtRsnInvClosed());
		 * pCINV12DInputRec.setBIndFTMOffered(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndFTMOffered());
		 * pCINV12DInputRec.setBIndAbsentParent(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndAbsentParent());
		 * pCINV12DInputRec.setBIndCpsInvstCpsLeJointContact(assistiDto.
		 * getRowCinv12SiG01().getRowCINV10DOG00().
		 * getIndCpsInvstCpsLeJointContact());
		 * pCINV12DInputRec.setSzCdVictimPhoto(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getSzCdVictimPhoto());
		 * pCINV12DInputRec.setBIndReqOrders(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndReqOrders());
		 * pCINV12DInputRec.setBIndMultiPersMerged(assistiDto.getRowCinv12SiG01(
		 * ).getRowCINV10DOG00().getIndMultiPersMerged());
		 * pCINV12DInputRec.setSzTxtAbsentParent(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getSzTxtAbsentParent());
		 * pCINV12DInputRec.setSzTxtRsnOvrllDisptn(assistiDto.getRowCinv12SiG01(
		 * ).getRowCINV10DOG00().getSzTxtRsnOvrllDisptn());
		 * pCINV12DInputRec.setSzTxtCpsInvstCpsLeJointContact(assistiDto.
		 * getRowCinv12SiG01().getRowCINV10DOG00().
		 * getSzTxtCpsInvstCpsLeJointContact());
		 * pCINV12DInputRec.setBIndVictimTaped(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndVictimTaped());
		 * pCINV12DInputRec.setBIndParentNotify(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndParentNotify());
		 * pCINV12DInputRec.setBIndVictimPhoto(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndVictimPhoto());
		 * pCINV12DInputRec.setBIndChildSexTraffic(assistiDto.getRowCinv12SiG01(
		 * ).getRowCINV10DOG00().getIndChildSexTraffic());
		 * pCINV12DInputRec.setBIndFTMOccurred(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIndFTMOccurred());
		 * pCINV12DInputRec.setSzTxtVictimTaped(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getSzTxtVictimTaped());
		 * pCINV12DInputRec.setBIndChildLaborTraffic(assistiDto.
		 * getRowCinv12SiG01().getRowCINV10DOG00().getIndChildLaborTraffic());
		 * pCINV12DInputRec.setIdHouseHold(assistiDto.getRowCinv12SiG01().
		 * getRowCINV10DOG00().getIdHouseHold());
		 */
		// pCINV12DInputRec.setBIndCpsInvstEaConcl(ServiceConstants.STRING_IND_Y);
		pCINV12DInputRec.setTsLastUpdate(assistiDto.getRowCinv12SiG01().getTsLastUpdate());
		if (assistiDto.getRowCinv12SIG00() != null && !assistiDto.getRowCinv12SIG00().isEmpty()) {
			List<CpsEmergencyResponseDto> rowCinv12SIG00List = assistiDto.getRowCinv12SIG00();
			for (CpsEmergencyResponseDto rowCinv12SIG00 : rowCinv12SIG00List) {
				if (!ServiceConstants.STRING_IND_Y.equals(rowCinv12SIG00.getIndEaResponse())) {
					pCINV12DInputRec.setIndCpsInvstEaConcl(ServiceConstants.STRING_IND_N);
					break;
				}
			}
		}
		if (!TypeConvUtil.isNullOrEmpty(assistiDto.getRowCinv12SiG01().getCdCpsInvstDtlFamIncm()) && assistiDto
				.getRowCinv12SiG01().getCdCpsInvstDtlFamIncm().equalsIgnoreCase(ServiceConstants.CEAFINCM_I5)) {
			pCINV12DInputRec.setIndCpsInvstEaConcl(ServiceConstants.STRING_IND_N);
		}
		if (!TypeConvUtil.isNullOrEmpty(assistiDto.getRowCinv12SiG01().getCdCpsInvstDtlFamIncm()) && assistiDto
				.getRowCinv12SiG01().getCdCpsInvstDtlFamIncm().equalsIgnoreCase(ServiceConstants.CEAFINCM_I6)) {
			pCINV12DInputRec.setIndCpsInvstEaConcl(ServiceConstants.STRING_IND_N);
		}
		pCINV12DInputRec.setIndCpsInvstAbbrv(assistiDto.getRowCinv12SiG01().getIndCpsInvstAbbrv());
		pCINV12DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		return pCINV12DInputRec;
	}

	/**
	 * 
	 * Method Name: processCinv16d Method Description: This method maps
	 * Cinv16diDto with List<SaveEmergAssistiDto>
	 * 
	 * @param liAssistiDto
	 * @param pCINV16DInputRec
	 * @param saveEmergAssistiDto
	 * @return EmergencyAssistInsUpdDelInDto
	 */
	private EmergencyAssistInsUpdDelInDto processCinv16d(CpsEmergencyResponseDto rowcinv12sig00Dto,
			EmergencyAssistInsUpdDelInDto pCINV16DInputRec, SaveEmergAssistiDto saveEmergAssistiDto,
			SaveEmergAssistoDto pOutputMsg) {
		if (saveEmergAssistiDto.getReqFuncCd().equals(ServiceConstants.REQ_FUNC_CD_ADD)) {
			rowcinv12sig00Dto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
		}
		/*
		 * if (!TypeConvUtil.isNullOrEmpty(saveEmergAssistiDto.getUlIdStage()))
		 * { rowcinv12sig00Dto.setSzCdScrDataAction(ServiceConstants.
		 * REQ_FUNC_CD_ADD); }
		 */
		pCINV16DInputRec.setCdEaQuestion(rowcinv12sig00Dto.getCdEaQuestion());
		if (rowcinv12sig00Dto.getCdScrDataAction().equals(ServiceConstants.REQ_FUNC_CD_UPDATE)
				|| rowcinv12sig00Dto.getCdScrDataAction().equals(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			pCINV16DInputRec.setIdEmergencyAssist(rowcinv12sig00Dto.getIdEmergencyAssist());
		} else {
			pCINV16DInputRec.setIdEmergencyAssist(Long.valueOf(ServiceConstants.LongZero));
		}
		pCINV16DInputRec.setIndEaResponse(rowcinv12sig00Dto.getIndEaResponse());
		pCINV16DInputRec.setIdEvent(pOutputMsg.getIdEvent());
		// pCINV16DInputRec.setTsLastUpdate(rowcinv12sig00Dto.getTsLastUpdate());
		pCINV16DInputRec.setTsLastUpdate(new Date());
		pCINV16DInputRec.setCdReqFunction(rowcinv12sig00Dto.getCdScrDataAction());
		return pCINV16DInputRec;
	}

	/**
	 * 
	 * Method Name: processUpdateTODOEvent Method Description: This method maps
	 * Cinv43diDto with SaveEmergAssistiDto
	 * 
	 * @param pInputMsg
	 * @return TodoUpdDtTodoCompletedInDto
	 */
	private TodoUpdDtTodoCompletedInDto processUpdateTODOEvent(SaveEmergAssistiDto pInputMsg) {
		TodoUpdDtTodoCompletedInDto pCINV43DInputRec = new TodoUpdDtTodoCompletedInDto();
		pCINV43DInputRec.setIdEvent(pInputMsg.getRowCcmn01UG00().getIdEvent());
		return pCINV43DInputRec;
	}

	/**
	 * 
	 * Method Name: processEvent Method Description: This method maps
	 * Ccmn62diDto with SaveEmergAssistiDto
	 * 
	 * @param pInputMsg
	 * @return EventUpdEventStatusInDto
	 */
	private EventUpdEventStatusInDto processUpdateEvent(SaveEmergAssistiDto pInputMsg) {
		EventUpdEventStatusInDto pCCMN62DInputRec = new EventUpdEventStatusInDto();
		pCCMN62DInputRec.setIdEvent(pInputMsg.getIdEvent());
		pCCMN62DInputRec.setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
		pCCMN62DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		return pCCMN62DInputRec;
	}

	/**
	 * 
	 * Method Name: processEvent Method Description: This method maps
	 * Ccmn06uiDto with SaveEmergAssistiDto
	 * 
	 * @param pInputMsg
	 * @param isAdd
	 * @return StageTaskInDto
	 */
	private StageTaskInDto processCheckStatus(SaveEmergAssistiDto pInputMsg, boolean isAdd) {
		StageTaskInDto pCCMN06UInputRec = new StageTaskInDto();
		if (isAdd) {
			pInputMsg.getRowCcmn01UG00().setDtEventOccurred(new Date());
			pInputMsg.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		} else {
			pInputMsg.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			// pInputMsg.setcReqFuncCd(pInputMsg.getRowCcmn01UG00().getcReqFuncCode());
		}
		pCCMN06UInputRec.setReqFuncCd(pInputMsg.getReqFuncCd());
		pCCMN06UInputRec.setIdStage(pInputMsg.getRowCcmn01UG00().getIdStage());
		pCCMN06UInputRec.setCdTask(pInputMsg.getRowCcmn01UG00().getCdTask());
		return pCCMN06UInputRec;
	}
}
