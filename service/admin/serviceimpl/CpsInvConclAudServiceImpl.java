package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.StageInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.StagePersonUpdByRoleDao;
import us.tx.state.dfps.service.admin.dao.StageUnitDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclAudiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclAudoDto;
import us.tx.state.dfps.service.admin.dto.CpsInvServiceDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelOutDto;
import us.tx.state.dfps.service.admin.dto.PostEventStageStatusInDto;
import us.tx.state.dfps.service.admin.dto.SaveEmergAssistiDto;
import us.tx.state.dfps.service.admin.dto.StageInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonUpdByRoleInDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.StageUnitInDto;
import us.tx.state.dfps.service.admin.dto.StageUnitOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.CpsInvConclAudService;
import us.tx.state.dfps.service.admin.service.PostEventStageStatusService;
import us.tx.state.dfps.service.admin.service.SaveEmergAssistService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.casemanagement.service.CPSInvCnlsnService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CpsInvNoticesClosureReq;
import us.tx.state.dfps.service.common.request.CpsInvSubstanceReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.personlistbystage.dao.PersonListByStageDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 11, 2017- 1:45:22 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class CpsInvConclAudServiceImpl implements CpsInvConclAudService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageEventStatusCommonService objCcmn06uService;

	@Autowired
	ApprovalCommonService objCcmn05uService;

	@Autowired
	PostEventStageStatusService objCcmn01uService;

	@Autowired
	CpsInvstDetailInsUpdDelDao objCinv12dDao;

	@Autowired
	StageInsUpdDelDao objCsvc18dDao;

	@Autowired
	StageUnitDao objClsc59dDao;

	@Autowired
	StagePersonUpdByRoleDao objCaude6dDao;

	@Autowired
	SaveEmergAssistService objSaveEmergAssistService;

	@Autowired
	CPSInvCnlsnService cnlsnService;

	@Autowired
	private PersonListByStageDao personListByStageDao;


	private static final Logger log = Logger.getLogger(CpsInvConclAudServiceImpl.class);

	/**
	 * Method Name: saveCpsInvestigationDetails Method Description: Method Saves
	 * the Emergency Assistance Details, CPS Investigation Conclusion Details
	 * and Conclusion Notification Details in single Transaction.
	 * 
	 * @param cpsInvConclAudiDto
	 * @param saveEmergAsssistiDto
	 * @param cpsInvNoticeClosureReq
	 * @return CpsInvConclAudoDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CpsInvConclAudoDto saveCpsInvestigationDetails(CpsInvConclAudiDto cpsInvConclAudiDto,
			SaveEmergAssistiDto saveEmergAssistiDto, CpsInvNoticesClosureReq cpsInvNoticeClosureReq,
														  CpsInvSubstanceReq cpsInvSubstanceReq	) {

		if (!StringUtils.isEmpty(saveEmergAssistiDto.getIdEvent())) {
			objSaveEmergAssistService.callSaveEmergAssistService(saveEmergAssistiDto);
		}
		CpsInvConclAudoDto objCpsInvConclAudoDto = callCpsInvConclAudService(cpsInvConclAudiDto);
		if (ObjectUtils.isEmpty(objCpsInvConclAudoDto.getErrorDto())) {
			cnlsnService.saveClosureNotices(cpsInvNoticeClosureReq);
			cnlsnService.saveSubstances(cpsInvSubstanceReq);
	}
		return objCpsInvConclAudoDto;
	}

	/**
	 * 
	 * Method Name: callCpsInvConclAudService Method Description: This service
	 * updates information modified on the CPS Investigation Conclusion window.
	 * 
	 * @param pInputMsg
	 * @return CpsInvConclAudoDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CpsInvConclAudoDto callCpsInvConclAudService(CpsInvConclAudiDto pInputMsg) {
		log.debug("Entering method callCpsInvConclAudService in CpsInvConclAudServiceImpl");
		Date complete = new Date();
		CpsInvServiceDto rowcinv10dog00Dto = new CpsInvServiceDto();
		rowcinv10dog00Dto = pInputMsg.getRowCINV10DOG00();
		if (null != pInputMsg.getRowCINV10DOG00().getDtCpsInvstDtlComplt()) {
			complete = pInputMsg.getRowCINV10DOG00().getDtCpsInvstDtlComplt();
			Calendar cal = Calendar.getInstance();
			cal.setTime(complete);
			complete = cal.getTime();
			rowcinv10dog00Dto.setDtCpsInvstDtlComplt(complete);
			pInputMsg.setRowCINV10DOG00(rowcinv10dog00Dto);
		}
		CpsInvConclAudoDto pOutputMsg = new CpsInvConclAudoDto();
		String rc = ServiceConstants.FND_SUCCESS;
		StageTaskInDto pCCMN06UInputRec = new StageTaskInDto();
		pCCMN06UInputRec.setReqFuncCd(pInputMsg.getReqFuncCd());
		pCCMN06UInputRec.setIdStage(pInputMsg.getRowCCMN45DO().getIdStage());
		pCCMN06UInputRec.setCdTask(pInputMsg.getRowCCMN45DO().getCdTask());
		String ccmn06uService = objCcmn06uService.checkStageEventStatus(pCCMN06UInputRec);

		if (!TypeConvUtil.isNullOrEmpty(ccmn06uService)) {
			if (ccmn06uService.equalsIgnoreCase(ServiceConstants.ARC_SUCCESS)) {
				if (pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_1)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_2)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_3)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_4)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_5)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_6)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_REFUSAL_CLOSURE)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_MOVED_CLOSURE)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.WORKLOAD_CONSTR_CLOSURE)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.NONFAM_INV_CLOSURE)) {
					StageUnitInDto pCLSC59DInputRec = new StageUnitInDto();
					pCLSC59DInputRec.setIdCase(pInputMsg.getRowCINV14SOG00().getIdCase());
					List<StageUnitOutDto> clsc59doDtos = objClsc59dDao.getStageDetails(pCLSC59DInputRec);
					if (clsc59doDtos != null && clsc59doDtos.size() > 0) {
						rc = CallCLSC59D(pInputMsg, clsc59doDtos);
					}
				}
				if (rc.equalsIgnoreCase(ServiceConstants.FND_SUCCESS)) {
					if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getSysNbrReserved1())) {
						if (!pInputMsg.getSysNbrReserved1()) {
							if (pInputMsg.getRowCCMN45DO().getCdEventStatus()
									.equalsIgnoreCase(ServiceConstants.EVENT_STATUS_PENDING)) {
								ApprovalCommonInDto pInvdInput = new ApprovalCommonInDto();
								pInvdInput.setIdEvent(pInputMsg.getRowCCMN45DO().getIdEvent());
								pInvdInput.setSysNbrReserved1(pInputMsg.getSysNbrReserved1());
								objCcmn05uService.callCcmn05uService(pInvdInput);
							}
							PostEventStageStatusInDto ccmn01uiDto = CallCCMN01U(pInputMsg);
							objCcmn01uService.callPostEventStageStatusService(ccmn01uiDto);
						}
					}
					CpsInvstDetailInsUpdDelInDto cinv12diDto = CallCINV12D(pInputMsg);
					CpsInvstDetailInsUpdDelOutDto cinv12doDto = new CpsInvstDetailInsUpdDelOutDto();
					switch (cinv12diDto.getReqFuncCd()) {
					case ServiceConstants.REQ_FUNC_CD_ADD:
						Long idEvent = objCinv12dDao.getNewCpsInvstId();
						cinv12diDto.setIdEvent(idEvent);
						cinv12doDto = objCinv12dDao.saveCpsInvstDetail(cinv12diDto);
						break;
					case ServiceConstants.REQ_FUNC_CD_UPDATE:
						cinv12doDto = objCinv12dDao.updateCpsInvstDetail(cinv12diDto);
						break;
					case ServiceConstants.REQ_FUNC_CD_DELETE:
						break;
					}
					if (cinv12doDto != null) {
						StageInsUpdDelInDto pCSVC18DInputRec = CallCSVC18D(pInputMsg);
						switch (pCSVC18DInputRec.getReqFuncCd()) {
						case ServiceConstants.REQ_FUNC_CD_ADD:
							objCsvc18dDao.saveStageDetail(pCSVC18DInputRec);
							break;
						case ServiceConstants.REQ_FUNC_CD_UPDATE:
							objCsvc18dDao.updateStageDetail(pCSVC18DInputRec);
							if (!pCSVC18DInputRec.getCdStage()
									.equalsIgnoreCase(ServiceConstants.STAGE_TYPE_INVESTIGATION)
									&& !pCSVC18DInputRec.getCdStageReasonClosed()
									.equalsIgnoreCase(ServiceConstants.CD_INV_CLOSED_AND_RECLASS)) {
								objCsvc18dDao.updateIncomingDetail(pCSVC18DInputRec);
							}
							break;
						case ServiceConstants.REQ_FUNC_CD_DELETE:
							objCsvc18dDao.deleteStageDetails(pCSVC18DInputRec);
							break;
						}
					}
				} else {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(Integer.parseInt(rc));
					pOutputMsg.setErrorDto(errorDto);
				}
			}
		}
		if (pInputMsg.getIndChkd() != null
				&& pInputMsg.getIndChkd().equalsIgnoreCase(ServiceConstants.INDICATOR_YES1)) {
			StagePersonUpdByRoleInDto pCAUDE6DInputRec = new StagePersonUpdByRoleInDto();
			pCAUDE6DInputRec.setIdStage(pInputMsg.getRowCINV14SOG00().getIdStage());
			pCAUDE6DInputRec.setCdStagePersRole(ServiceConstants.PERSON_STAGE_ROLE_UK);
			pCAUDE6DInputRec.setCdStagePersType(ServiceConstants.PERSON_TYPE_PRN);
			pCAUDE6DInputRec.setCdStagePersRole2(ServiceConstants.NO_ROLE);
			try {
				objCaude6dDao.updateStagePersonDetails(pCAUDE6DInputRec);
			} catch (DataNotFoundException e) {
				log.debug(
						"Data Not found exception occured in updating the stage person link  table in  CpsInvConclAudServiceImpl");
			}
		}


		log.debug("Exiting method callCpsInvConclAudService in CpsInvConclAudServiceImpl");
		return pOutputMsg;
	}

	
	
	/**
	 * Updates the EVENT record by calling the PostEvent function. Post Event
	 * adds, updates, or deletes the Event and adds or deletes people from the
	 * Event Person Link table.
	 * 
	 * @param pInputMsg
	 * @return ccmn01uoDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public PostEventStageStatusInDto CallCCMN01U(CpsInvConclAudiDto pInputMsg) {
		log.debug("Entering method CallCCMN01U in CpsInvConclAudServiceImpl");
		PostEventStageStatusInDto pCCMN01UInputRec = new PostEventStageStatusInDto();
		if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getRowCINV10DOG00().getDtCPSInvstDtlIntake())
				&& !TypeConvUtil.isNullOrEmpty(pInputMsg.getRowCINV10DOG00().getDtCPSInvstDtlAssigned())
				&& !TypeConvUtil.isNullOrEmpty(pInputMsg.getRowCINV10DOG00().getDtCPSInvstDtlBegun())
				&& !TypeConvUtil.isNullOrEmpty(pInputMsg.getRowCINV10DOG00().getDtCpsInvstDtlComplt())
				&& (!StringUtils.isEmpty(pInputMsg.getRowCINV14DOG00().getCdRiskAssmtRiskFind())
						|| (pInputMsg.getRowCINV10DOG00().getIndCpsInvstDtlRaNa() == ServiceConstants.INDICATOR_YES))
				&& !StringUtils.isEmpty(pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed())) {
			if (pInputMsg.getRowCINV14SOG00().getCdStageType().equalsIgnoreCase(ServiceConstants.CRSR_STAGE)) {
				pInputMsg.getRowCCMN45DO().setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
			} else {
				if (!StringUtils.isEmpty(pInputMsg.getRowCINV10DOG00().getCdCpsOverallDisptn())) {
					pInputMsg.getRowCCMN45DO().setCdEventStatus(ServiceConstants.EVENT_STATUS_COMPLETE);
				} else {
					pInputMsg.getRowCCMN45DO().setCdEventStatus(ServiceConstants.EVENT_STATUS_PROGRESS);
				}
			}
		} else {
			pInputMsg.getRowCCMN45DO().setCdEventStatus(ServiceConstants.EVENT_STATUS_PROGRESS);
		}
		pCCMN01UInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		pCCMN01UInputRec.setIdEvent(pInputMsg.getRowCCMN45DO().getIdEvent());
		pCCMN01UInputRec.setIdStage(pInputMsg.getRowCCMN45DO().getIdStage());
		pCCMN01UInputRec.setIdPerson(pInputMsg.getRowCCMN45DO().getIdPerson());
		pCCMN01UInputRec.setCdTask(pInputMsg.getRowCCMN45DO().getCdTask());
		pCCMN01UInputRec.setCdEventType(pInputMsg.getRowCCMN45DO().getCdEventType());
		pCCMN01UInputRec.setDtEventOccurred(pInputMsg.getRowCCMN45DO().getDtEventOccurred());
		pCCMN01UInputRec.setEventDescr(pInputMsg.getRowCCMN45DO().getEventDescr());
		pCCMN01UInputRec.setCdEventStatus(pInputMsg.getRowCCMN45DO().getCdEventStatus());
		pCCMN01UInputRec.setDtEventLastUpdate(pInputMsg.getRowCCMN45DO().getTsLastUpdate());
		log.debug("Exiting method CallCCMN01U in CpsInvConclAudServiceImpl");
		return pCCMN01UInputRec;
	}

	/**
	 * This function creates a new record in the CPS INVST DETAIL table
	 * 
	 * @param pInputMsg
	 * @return cinv12doDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public CpsInvstDetailInsUpdDelInDto CallCINV12D(CpsInvConclAudiDto pInputMsg) {
		log.debug("Entering method CallCINV12D in CpsInvConclAudServiceImpl");
		CpsInvstDetailInsUpdDelInDto pCINV12DInputRec = new CpsInvstDetailInsUpdDelInDto();
		pCINV12DInputRec.setDtCPSInvstDtlAssigned(pInputMsg.getRowCINV10DOG00().getDtCPSInvstDtlAssigned());
		pCINV12DInputRec.setDtCPSInvstDtlBegun(pInputMsg.getRowCINV10DOG00().getDtCPSInvstDtlBegun());
		pCINV12DInputRec.setDtCpsInvstDtlComplt(pInputMsg.getRowCINV10DOG00().getDtCpsInvstDtlComplt());
		pCINV12DInputRec.setDtCPSInvstDtlIntake(pInputMsg.getRowCINV10DOG00().getDtCPSInvstDtlIntake());
		pCINV12DInputRec.setIdStage(pInputMsg.getRowCINV10DOG00().getIdStage());
		pCINV12DInputRec.setCdCpsInvstDtlFamIncm(pInputMsg.getRowCINV10DOG00().getCdCpsInvstDtlFamIncm());
		pCINV12DInputRec.setCdCpsOverallDisptn(pInputMsg.getRowCINV10DOG00().getCdCpsOverallDisptn());
		pCINV12DInputRec.setIndCpsInvstDtlRaNa(pInputMsg.getRowCINV10DOG00().getIndCpsInvstDtlRaNa());
		pCINV12DInputRec.setIndCpsInvstSafetyPln(pInputMsg.getRowCINV10DOG00().getIndCpsInvstSafetyPln());
		pCINV12DInputRec.setIndCpsInvstEaConcl(pInputMsg.getRowCINV10DOG00().getIndCpsInvstEaConcl());
		pCINV12DInputRec.setIndCpsInvstAbbrv(pInputMsg.getRowCINV10DOG00().getIndCpsInvstAbbrv());
		pCINV12DInputRec
		.setIndCpsInvstCpsLeJointContact(pInputMsg.getRowCINV10DOG00().getIndCpsInvstCpsLeJointContact());
		pCINV12DInputRec.setCdCpsInvstCpsLeJointContact(pInputMsg.getRowCINV10DOG00().getCdCpsInvstCpsLeJointContact());
		pCINV12DInputRec.setCpsInvstCpsLeJointContact(pInputMsg.getRowCINV10DOG00().getCpsInvstCpsLeJointContact());
		pCINV12DInputRec.setIndVictimTaped(pInputMsg.getRowCINV10DOG00().getIndVictimTaped());
		pCINV12DInputRec.setCdVictimTaped(pInputMsg.getRowCINV10DOG00().getCdVictimTaped());
		pCINV12DInputRec.setVictimTaped(pInputMsg.getRowCINV10DOG00().getVictimTaped());
		pCINV12DInputRec.setIndVictimPhoto(pInputMsg.getRowCINV10DOG00().getIndVictimPhoto());
		pCINV12DInputRec.setCdVictimPhoto(pInputMsg.getRowCINV10DOG00().getCdVictimPhoto());
		pCINV12DInputRec.setVictimPhoto(pInputMsg.getRowCINV10DOG00().getVictimPhoto());
		pCINV12DInputRec.setIndParentGivenGuide(pInputMsg.getRowCINV10DOG00().getIndParentGivenGuide());
		pCINV12DInputRec.setIndParentNotify(pInputMsg.getRowCINV10DOG00().getIndParentNotify());
		pCINV12DInputRec.setIndMultiPersFound(pInputMsg.getRowCINV10DOG00().getIndMultiPersFound());
		pCINV12DInputRec.setIndMultiPersMerged(pInputMsg.getRowCINV10DOG00().getIndMultiPersMerged());
		pCINV12DInputRec.setIndMeth(pInputMsg.getRowCINV10DOG00().getIndMeth());
		pCINV12DInputRec.setIndFTMOffered(pInputMsg.getRowCINV10DOG00().getIndFTMOffered());
		pCINV12DInputRec.setIndFTMOccurred(pInputMsg.getRowCINV10DOG00().getIndFTMOccurred());
		pCINV12DInputRec.setIndReqOrders(pInputMsg.getRowCINV10DOG00().getIndReqOrders());
		pCINV12DInputRec.setRsnOvrllDisptn(pInputMsg.getRowCINV10DOG00().getRsnOvrllDisptn());
		pCINV12DInputRec.setRsnOpenServices(pInputMsg.getRowCINV10DOG00().getRsnOpenServices());
		pCINV12DInputRec.setRsnInvClosed(pInputMsg.getRowCINV10DOG00().getRsnInvClosed());
		pCINV12DInputRec.setAbsentParent(pInputMsg.getRowCINV10DOG00().getAbsentParent());
		pCINV12DInputRec.setIndAbsentParent(pInputMsg.getRowCINV10DOG00().getIndAbsentParent());
		pCINV12DInputRec.setIndChildSexTraffic(pInputMsg.getRowCINV10DOG00().getIndChildSexTraffic());
		pCINV12DInputRec.setIndChildLaborTraffic(pInputMsg.getRowCINV10DOG00().getIndChildLaborTraffic());
		pCINV12DInputRec.setIdHouseHold(pInputMsg.getRowCINV10DOG00().getIdHouseHold());
		pCINV12DInputRec.setIdEvent(pInputMsg.getRowCINV10DOG00().getIdEvent());
		pCINV12DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		pCINV12DInputRec.setTsLastUpdate(pInputMsg.getRowCINV10DOG00().getTsLastUpdate());
		pCINV12DInputRec.setIndNoNoticeSelected(pInputMsg.getRowCINV10DOG00().getIndNoNoticeSelected());
		pCINV12DInputRec.setIndSubstancePrnt(pInputMsg.getRowCINV10DOG00().getIndSubstancePrnt());
		pCINV12DInputRec.setIndSubstanceChild(pInputMsg.getRowCINV10DOG00().getIndSubstanceChild());
		pCINV12DInputRec.setIndVrblWrtnNotifRights(pInputMsg.getRowCINV10DOG00().getIndVrblWrtnNotifRights());
		pCINV12DInputRec.setIndNotifRightsUpld(pInputMsg.getRowCINV10DOG00().getIndNotifRightsUpld());
		log.debug("Exiting method CallCINV12D in CpsInvConclAudServiceImpl");
		return pCINV12DInputRec;
	}

	/**
	 * Calls the DAM to update or add a row in the Stage table
	 * 
	 * @param pInputMsg
	 * @return csvc18doDto @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageInsUpdDelInDto CallCSVC18D(CpsInvConclAudiDto pInputMsg) {
		log.debug("Entering method CallCSVC18D in CpsInvConclAudServiceImpl");
		StageInsUpdDelInDto pCSVC18DInputRec = new StageInsUpdDelInDto();
		pCSVC18DInputRec.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		pCSVC18DInputRec.setIdStage(pInputMsg.getRowCINV14SOG00().getIdStage());
		pCSVC18DInputRec.setTmSysTmStageClose(pInputMsg.getRowCINV14SOG00().getTmSysTmStageClose());
		pCSVC18DInputRec.setTmSysTmStageStart(pInputMsg.getRowCINV14SOG00().getTmSysTmStageStart());
		pCSVC18DInputRec.setIdUnit(pInputMsg.getRowCINV14SOG00().getIdUnit());
		pCSVC18DInputRec.setIndStageClose(pInputMsg.getRowCINV14SOG00().getIndStageClose());
		pCSVC18DInputRec.setNmStage(pInputMsg.getRowCINV14SOG00().getNmStage());
		// pCSVC18DInputRec.setDtDtClientAdvised(pInputMsg.getRowCINV14SOG00().getDtClientAdvised());
		pCSVC18DInputRec.setCdStage(pInputMsg.getRowCINV14SOG00().getCdStage());
		pCSVC18DInputRec.setCdStageClassification(pInputMsg.getRowCINV14SOG00().getCdStageClassification());
		pCSVC18DInputRec.setCdStageCnty(pInputMsg.getRowCINV14SOG00().getCdStageCnty());
		pCSVC18DInputRec.setCdStageCurrPriority(pInputMsg.getRowCINV14SOG00().getCdStageCurrPriority());
		pCSVC18DInputRec.setCdStageInitialPriority(pInputMsg.getRowCINV14SOG00().getCdStageInitialPriority());
		pCSVC18DInputRec.setCdStageProgram(pInputMsg.getRowCINV14SOG00().getCdStageProgram());
		pCSVC18DInputRec.setCdStageReasonClosed(pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed());
		pCSVC18DInputRec.setDtStageClose(pInputMsg.getRowCINV14SOG00().getDtStageClose());
		pCSVC18DInputRec.setDtStageStart(pInputMsg.getRowCINV14SOG00().getDtStageStart());
		pCSVC18DInputRec.setIdCase(pInputMsg.getRowCINV14SOG00().getIdCase());
		pCSVC18DInputRec.setTsLastUpdate(pInputMsg.getRowCINV14SOG00().getTsLastUpdate());
		pCSVC18DInputRec.setIdSituation(pInputMsg.getRowCINV14SOG00().getIdSituation());
		pCSVC18DInputRec.setStageClosureCmnts(pInputMsg.getRowCINV14SOG00().getStageClosureCmnts());
		pCSVC18DInputRec.setStagePriorityCmnts(pInputMsg.getRowCINV14SOG00().getStagePriorityCmnts());
		pCSVC18DInputRec.setCdStageRegion(pInputMsg.getRowCINV14SOG00().getCdStageRegion());
		pCSVC18DInputRec.setCdStageRsnPriorityChgd(pInputMsg.getRowCINV14SOG00().getCdStageRsnPriorityChgd());
		pCSVC18DInputRec.setCdStageType(pInputMsg.getRowCINV14SOG00().getCdStageType());
		log.debug("Exiting method CallCSVC18D in CpsInvConclAudServiceImpl");
		return pCSVC18DInputRec;
	}

	/**
	 * Calls the DAM to retrieve all stages for a given case, then check for an
	 * existing FSU or SUB stage. If one exists, return the appropriate message
	 * based upon the closure reason.
	 *
	 * @param pInputMsg
	 * @return returnVal @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String CallCLSC59D(CpsInvConclAudiDto pInputMsg, List<StageUnitOutDto> clsc59doDtos) {
		log.debug("Entering method CallCLSC59D in CpsInvConclAudServiceImpl");
		long rc = 0;
		String returnVal = ServiceConstants.PROCESS_TUX_SQL_ERROR_TRANSACT;
		boolean bIndFoundSub = false;
		Date dtDtMostRecentIntake;
		if (clsc59doDtos != null && clsc59doDtos.size() > 0) {
			dtDtMostRecentIntake = null;
			for (StageUnitOutDto clsc59doDto : clsc59doDtos) {
				if (clsc59doDto.getCdStage().equalsIgnoreCase(ServiceConstants.INTAKE)) {
					if (TypeConvUtil.isNullOrEmpty(dtDtMostRecentIntake)) {
						dtDtMostRecentIntake = clsc59doDto.getDtStageClose();
					}
					rc = dtDtMostRecentIntake.compareTo(clsc59doDto.getDtStageStart());
					if (rc < 0) {
						dtDtMostRecentIntake = clsc59doDto.getDtStageStart();
					}
				}
			}
			for (StageUnitOutDto clsc59doDto : clsc59doDtos) {
				if (clsc59doDto.getCdStage().equalsIgnoreCase(ServiceConstants.SUBCARE)
						|| (clsc59doDto.getCdStage().equalsIgnoreCase(ServiceConstants.FAMILY_SUBCARE))) {
					if (ObjectUtils.isEmpty(clsc59doDto.getDtStageClose())
							|| dtDtMostRecentIntake.before(clsc59doDto.getDtStageClose())) {
						bIndFoundSub = true;
						break;
					}
				} else {
					returnVal = ServiceConstants.FND_SUCCESS;
				}
			}
			if (bIndFoundSub) {
				if (pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_1)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_2)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_3)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_4)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_5)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.FAM_PRES_CLOSURE_6)) {
					//artf165283 :Change INV subcare validations to allow Family Pres Recommended Action  PPM 46797
					//Message changed from MSG_FPR_NOT_FROM_INVEST as part of artifact for FBSS INV Concurrent Stage project
					returnVal = ServiceConstants.FND_SUCCESS;
				} else if ((pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.WORKLOAD_CONSTR_CLOSURE)
						|| pInputMsg.getRowCINV14SOG00().getCdStageReasonClosed()
						.equalsIgnoreCase(ServiceConstants.NONFAM_INV_CLOSURE))) {
					returnVal = ServiceConstants.MSG_USE_REMOVAL_SUB_CLOSURE_RSN;
				} else {
					returnVal = ServiceConstants.FND_SUCCESS;
				}
			} else {
				returnVal = ServiceConstants.FND_SUCCESS;
			}
		}
		log.debug("Exiting method CallCLSC59D in CpsInvConclAudServiceImpl");
		return returnVal;
	}
}
