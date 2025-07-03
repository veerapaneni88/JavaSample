package us.tx.state.dfps.service.admin.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.admin.dao.AllegationDtlsDao;
import us.tx.state.dfps.service.admin.dao.AllegationStageCaseDao;
import us.tx.state.dfps.service.admin.dao.CaseMergeFromDao;
import us.tx.state.dfps.service.admin.dao.CaseMergeToDao;
import us.tx.state.dfps.service.admin.dao.ContactSearchDtContactOccDao;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailEventDao;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailStageIdDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.EventStagePersonLinkInsUpdDao;
import us.tx.state.dfps.service.admin.dao.RiskAssmtNarrOrIraDao;
import us.tx.state.dfps.service.admin.dao.StageLinkIncomeDetailDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkPersonStgTypeDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkStTypeRoleDao;
import us.tx.state.dfps.service.admin.dao.StageRegionDao;
import us.tx.state.dfps.service.admin.dto.AllegationDtlsInDto;
import us.tx.state.dfps.service.admin.dto.AllegationDtlsOutDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseOutDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeFromInDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeFromOutDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeToInDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeToOutDto;
import us.tx.state.dfps.service.admin.dto.ContactInDto;
import us.tx.state.dfps.service.admin.dto.ContactOutDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclSelectiDto;
import us.tx.state.dfps.service.admin.dto.CpsInvConclSelectoDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailEventInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailEventOutDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;
import us.tx.state.dfps.service.admin.dto.RiskAssessmentFactorDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraInDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraOutDto;
import us.tx.state.dfps.service.admin.dto.StageLinkIncomeDetailInDto;
import us.tx.state.dfps.service.admin.dto.StageLinkIncomeDetailOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkPersonStgTypeOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleOutDto;
import us.tx.state.dfps.service.admin.dto.StageRegionInDto;
import us.tx.state.dfps.service.admin.dto.StageRegionOutDto;
import us.tx.state.dfps.service.admin.service.CpsInvConclSelectService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.dao.SrvreferralslDao;
import us.tx.state.dfps.service.riskassessment.dao.RiskAssessmentDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:Service Impl for fetching event details
 *
 * Aug 6, 2017- 2:15:45 PM Â© 2017 Texas Department of Family and Protective
 * Services
 */
@Service
@Transactional
public class CpsInvConclSelectServiceImpl implements CpsInvConclSelectService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CpsInvstDetailEventDao objCinv10dDao;

	@Autowired
	CpsInvstDetailStageIdDao objCinv95dDao;

	@Autowired
	EventIdDao objCcmn45dDao;

	@Autowired
	ContactSearchDtContactOccDao objCsys22dDao;

	@Autowired
	RiskAssessmentDao objCinv14dDao;

	@Autowired
	EventStagePersonLinkInsUpdDao objCcmn87dDao;

	@Autowired
	RiskAssmtNarrOrIraDao objCsys13dDao;

	@Autowired
	StageRegionDao objCint40dDao;

	@Autowired
	StageLinkIncomeDetailDao objClsc84dDao;

	@Autowired
	AllegationDtlsDao objCinvd3dDao;

	@Autowired
	AllegationStageCaseDao objCinvg1dDao;

	@Autowired
	StagePersonLinkStTypeRoleDao objCint20dDao;

	@Autowired
	StagePersonLinkPersonStgTypeDao objClsc18dDao;

	@Autowired
	CaseMergeToDao objClsc68dDao;

	@Autowired
	CaseMergeFromDao objClsc67dDao;

	@Autowired
	SrvreferralslDao objSrvreferralslDao;

	private static final Logger log = Logger.getLogger(CpsInvConclSelectServiceImpl.class);

	/**
	 * 
	 * Method Name: callCpsInvConclSelectService Method Description: This
	 * service is used in the Predisplay callback of window CINV06W - CPS INV
	 * CONCLUSION. It retrieves all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return CpsInvConclSelectoDto @
	 */
	@SuppressWarnings("unused")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CpsInvConclSelectoDto callCpsInvConclSelectService(CpsInvConclSelectiDto pInputMsg) {
		log.debug("Entering method callCpsInvConclSelectService in CpsInvConclSelectServiceImpl");
		boolean bCasePending = false;
		// (pOutputMsg.setDtWCDDtSystemDate)(null);
		List<CpsInvstDetailEventOutDto> cinv10doDtos = null;
		List<CpsInvstDetailStageIdOutDto> cinv95doDtos = null;
		List<StageRegionOutDto> cint40doDtos = null;
		CpsInvConclSelectoDto pOutputMsg = new CpsInvConclSelectoDto();
		if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getIdEvent())) {
			cinv10doDtos = CallCINV10D(pInputMsg, pOutputMsg);
		} else {
			cinv95doDtos = CallCINV95D(pInputMsg, pOutputMsg);
		}
		/* cinv95doDtos = CallCINV95D(pInputMsg, pOutputMsg); */
		if (cinv10doDtos != null) {
			for (CpsInvstDetailEventOutDto cinv10doDto : cinv10doDtos) {
				if (!TypeConvUtil.isNullOrEmpty(cinv10doDto)) {
					if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getIdEvent())) {
						CallCCMN45D(pInputMsg, pOutputMsg);
					}
				}
			}
		}
		if (cinv95doDtos != null) {
			for (CpsInvstDetailStageIdOutDto cinv95doDto : cinv95doDtos) {
				if (!TypeConvUtil.isNullOrEmpty(cinv95doDto)) {
					if (!TypeConvUtil.isNullOrEmpty(pInputMsg.getIdEvent())) {
						CallCCMN45D(pInputMsg, pOutputMsg);
					}
				}
			}
		}
		CallCSYS22D(pInputMsg, pOutputMsg);
		CallCINV14D(pInputMsg, pOutputMsg);
		if (ServiceConstants.ZERO_VAL != pOutputMsg.getIdEvent()) {
			CallCSYS13D(pOutputMsg, pOutputMsg, pInputMsg.getUserId());
		}
		cint40doDtos = CallCINT40D(pInputMsg, pOutputMsg);
		for (StageRegionOutDto cint40doDto : cint40doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(cint40doDto)) {
				CallCheckCasePending(cint40doDto.getIdCase(), bCasePending);
			}
		}
		CallCLSC84D(pInputMsg, pOutputMsg);
		CallCINT20D(pInputMsg);
		CallCLSC18D(pInputMsg, pOutputMsg);
		CallCINVD3D(pInputMsg, pOutputMsg);
		CallCINVG1D(pInputMsg, pOutputMsg);
		log.debug("Exiting method callCpsInvConclSelectService in CpsInvConclSelectServiceImpl");
		return pOutputMsg;
	}

	/**
	 * 
	 * Method Name: callCpsInvConclSelectService Method Description: This
	 * service is used in the Predisplay callback of window CINV06W - CPS INV
	 * CONCLUSION. It retrieves all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<CpsInvstDetailEventOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<CpsInvstDetailEventOutDto> CallCINV10D(CpsInvConclSelectiDto pInputMsg,
			CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCINV10D in CpsInvConclSelectServiceImpl");
		CpsInvstDetailEventInDto pCINV10DInputRec = new CpsInvstDetailEventInDto();
		pCINV10DInputRec.setIdEvent(pInputMsg.getIdEvent());
		List<CpsInvstDetailEventOutDto> cinv10doDtos = objCinv10dDao.getCPSInvestmentDtl(pCINV10DInputRec);
		// Fetching CpsInvestDtlBegun from the first contact added
		mapInvDtlToOp(pInputMsg, pOutputMsg, cinv10doDtos);
		log.debug("Exiting method CallCINV10D in CpsInvConclSelectServiceImpl");
		return cinv10doDtos;
	}

	/**
	 * 
	 * Method Name: callCpsInvConclSelectService Method Description: This
	 * service is used in the Predisplay callback of window CINV06W - CPS INV
	 * CONCLUSION. It retrieves all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @param pOutputMsg
	 * @
	 */
	private void mapInvDtlToOp(CpsInvConclSelectiDto pInputMsg, CpsInvConclSelectoDto pOutputMsg,
			List<CpsInvstDetailEventOutDto> cinv10doDtos) {
		for (CpsInvstDetailEventOutDto cinv10doDto : cinv10doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(cinv10doDto)) {
				pOutputMsg.setIdEvent(pInputMsg.getIdEvent());
				pOutputMsg.setDtCPSInvstDtlAssigned(cinv10doDto.getDtCPSInvstDtlAssigned());
				pOutputMsg.setDtCPSInvstDtlBegun(cinv10doDto.getDtCPSInvstDtlBegun());
				pOutputMsg.setDtCpsInvstDtlComplt(cinv10doDto.getDtCpsInvstDtlComplt());
				pOutputMsg.setCdCpsOverallDisptn(cinv10doDto.getCdCpsOverallDisptn());
				pOutputMsg.setIndCpsInvstSafetyPln(cinv10doDto.getIndCpsInvstSafetyPln());
				pOutputMsg.setIndCpsInvstDtlRaNa(cinv10doDto.getIndCpsInvstDtlRaNa());
				pOutputMsg.setCdCpsInvstDtlFamIncm(cinv10doDto.getCdCpsInvstDtlFamIncm());
				pOutputMsg.setIndCpsInvstEaConcl(cinv10doDto.getIndCpsInvstEaConcl());
				pOutputMsg.setIndCpsInvstAbbrv(cinv10doDto.getIndCpsInvstAbbrv());
				pOutputMsg.setIndCpsInvstCpsLeJointContact(cinv10doDto.getIndCpsInvstCpsLeJointContact());
				pOutputMsg.setCdCpsInvstCpsLeJointContact(cinv10doDto.getCdCpsInvstCpsLeJointContact());
				pOutputMsg.setCpsInvstCpsLeJointContact(cinv10doDto.getCpsInvstCpsLeJointContact());
				pOutputMsg.setIndVictimTaped(cinv10doDto.getIndVictimTaped());
				pOutputMsg.setCdVictimTaped(cinv10doDto.getCdVictimTaped());
				pOutputMsg.setVictimTaped(cinv10doDto.getVictimTaped());
				pOutputMsg.setIndMeth(cinv10doDto.getIndMeth());
				pOutputMsg.setIndSubstancePrnt(cinv10doDto.getIndSubstancePrnt());
				pOutputMsg.setIndSubstanceChild(cinv10doDto.getIndSubstanceChild());
				pOutputMsg.setIdStage(cinv10doDto.getIdStage());
				pOutputMsg.setIndVictimPhoto(cinv10doDto.getIndVictimPhoto());
				pOutputMsg.setCdVictimPhoto(cinv10doDto.getCdVictimPhoto());
				pOutputMsg.setVictimPhoto(cinv10doDto.getVictimPhoto());
				pOutputMsg.setIndParentGivenGuide(cinv10doDto.getIndParentGivenGuide());
				pOutputMsg.setIndParentNotify(cinv10doDto.getIndParentNotify());
				pOutputMsg.setIndMultiPersFound(cinv10doDto.getIndMultiPersFound());
				pOutputMsg.setIndMultiPersMerged(cinv10doDto.getIndMultiPersMerged());
				pOutputMsg.setIndFTMOffered(cinv10doDto.getIndFTMOffered());
				pOutputMsg.setIndFTMOccurred(cinv10doDto.getIndFTMOccurred());
				pOutputMsg.setIndReqOrders(cinv10doDto.getIndReqOrders());
				pOutputMsg.setRsnOvrllDisptn(cinv10doDto.getRsnOvrllDisptn());
				pOutputMsg.setRsnOpenServices(cinv10doDto.getRsnOpenServices());
				pOutputMsg.setRsnInvClosed(cinv10doDto.getRsnInvClosed());
				pOutputMsg.setAbsentParent(cinv10doDto.getAbsentParent());
				pOutputMsg.setIndAbsentParent(cinv10doDto.getIndAbsentParent());
				pOutputMsg.setIndChildSexTraffic(cinv10doDto.getIndChildSexTraffic());
				pOutputMsg.setIndChildLaborTraffic(cinv10doDto.getIndChildLaborTraffic());
				pOutputMsg.setIndNoNoticeSelected(!TypeConvUtil.isNullOrEmpty(cinv10doDto.getIndNoNoticeSelected())
						? cinv10doDto.getIndNoNoticeSelected() : "N");
				pOutputMsg.setIndVrblWrtnNotifRights(cinv10doDto.getIndVrblWrtnNotifRights());
				pOutputMsg.setIndNotifRightsUpld(cinv10doDto.getIndNotifRightsUpld());
				if (null != cinv10doDto.getIdHouseHold()) {
					pOutputMsg.setIdHouseHold(cinv10doDto.getIdHouseHold());
				}
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCINV95D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<CpsInvstDetailStageIdOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<CpsInvstDetailStageIdOutDto> CallCINV95D(CpsInvConclSelectiDto pInputMsg,
			CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCINV95D in CpsInvConclSelectServiceImpl");
		CpsInvstDetailStageIdInDto pCINV95DInputRec = new CpsInvstDetailStageIdInDto();
		pCINV95DInputRec.setIdStage(pInputMsg.getIdStage());
		List<CpsInvstDetailStageIdOutDto> cinv95doDtos = objCinv95dDao.getInvstDtls(pCINV95DInputRec);
		mapInvDtlsToOp(pOutputMsg, cinv95doDtos);
		log.debug("Exiting method CallCINV95D in CpsInvConclSelectServiceImpl");
		return cinv95doDtos;
	}

	/**
	 * 
	 * Method Name: mapInvDtlsToOp Method Description: This service is used in
	 * the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param pOutputMsg
	 * @param cinv95doDtos
	 */
	private void mapInvDtlsToOp(CpsInvConclSelectoDto pOutputMsg, List<CpsInvstDetailStageIdOutDto> cinv95doDtos) {
		for (CpsInvstDetailStageIdOutDto cinv95doDto : cinv95doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(cinv95doDto)) {
				pOutputMsg.setIdEvent(cinv95doDto.getIdEvent());
				pOutputMsg.setDtCPSInvstDtlIntake(cinv95doDto.getDtCPSInvstDtlIntake());
				pOutputMsg.setDtCPSInvstDtlAssigned(cinv95doDto.getDtCPSInvstDtlAssigned());
				pOutputMsg.setDtCPSInvstDtlBegun(cinv95doDto.getDtCPSInvstDtlBegun());
				pOutputMsg.setDtCpsInvstDtlComplt(cinv95doDto.getDtCpsInvstDtlComplt());
				pOutputMsg.setCdCpsOverallDisptn(cinv95doDto.getCdCpsOverallDisptn());
				pOutputMsg.setIndCpsInvstSafetyPln(cinv95doDto.getIndCpsInvstSafetyPln());
				pOutputMsg.setIndCpsInvstDtlRaNa(cinv95doDto.getIndCpsInvstDtlRaNa());
				pOutputMsg.setCdCpsInvstDtlFamIncm(cinv95doDto.getCdCpsInvstDtlFamIncm());
				pOutputMsg.setIndCpsInvstEaConcl(cinv95doDto.getIndCpsInvstEaConcl());
				pOutputMsg.setIndCpsInvstCpsLeJointContact(cinv95doDto.getIndCpsInvstCpsLeJointContact());
				pOutputMsg.setCdCpsInvstCpsLeJointContact(cinv95doDto.getCdCpsInvstCpsLeJointContact());
				pOutputMsg.setCpsInvstCpsLeJointContact(cinv95doDto.getCpsInvstCpsLeJointContact());
				pOutputMsg.setIndVictimTaped(cinv95doDto.getIndVictimTaped());
				pOutputMsg.setCdVictimTaped(cinv95doDto.getCdVictimTaped());
				pOutputMsg.setVictimTaped(cinv95doDto.getVictimTaped());
				pOutputMsg.setIndMeth(cinv95doDto.getIndMeth());
				pOutputMsg.setIndVictimPhoto(cinv95doDto.getIndVictimPhoto());
				pOutputMsg.setCdVictimPhoto(cinv95doDto.getCdVictimPhoto());
				pOutputMsg.setVictimPhoto(cinv95doDto.getVictimPhoto());
				pOutputMsg.setIndParentGivenGuide(cinv95doDto.getIndParentGivenGuide());
				pOutputMsg.setIndParentNotify(cinv95doDto.getIndParentNotify());
				pOutputMsg.setIndMultiPersFound(cinv95doDto.getIndMultiPersFound());
				pOutputMsg.setIndMultiPersMerged(cinv95doDto.getIndMultiPersMerged());
				pOutputMsg.setIndFTMOffered(cinv95doDto.getIndFTMOffered());
				pOutputMsg.setIndFTMOccurred(cinv95doDto.getIndFTMOccurred());
				pOutputMsg.setIndReqOrders(cinv95doDto.getIndReqOrders());
				pOutputMsg.setRsnOvrllDisptn(cinv95doDto.getRsnOvrllDisptn());
				pOutputMsg.setRsnOpenServices(cinv95doDto.getRsnOpenServices());
				pOutputMsg.setRsnInvClosed(cinv95doDto.getRsnInvClosed());
				pOutputMsg.setAbsentParent(cinv95doDto.getAbsentParent());
				pOutputMsg.setIndAbsentParent(cinv95doDto.getIndAbsentParent());
				pOutputMsg.setIndChildSexTraffic(cinv95doDto.getIndChildSexTraffic());
				pOutputMsg.setIndChildLaborTraffic(cinv95doDto.getIndChildLaborTraffic());
				pOutputMsg.setIdStage(cinv95doDto.getIdStage());
				pOutputMsg.setTsLastUpdate(cinv95doDto.getTsLastUpdate());
				pOutputMsg.setIdHouseHold(cinv95doDto.getIdHouseHold());
				pOutputMsg.setIndSubstancePrnt(cinv95doDto.getIndSubstancePrnt());
				pOutputMsg.setIndSubstanceChild(cinv95doDto.getIndSubstanceChild());
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCCMN45D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<EventIdOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<EventIdOutDto> CallCCMN45D(CpsInvConclSelectiDto pInputMsg, CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCCMN45D in CpsInvConclSelectServiceImpl");
		EventIdInDto pCCMN45DInputRec = new EventIdInDto();
		pCCMN45DInputRec.setIdEvent(pInputMsg.getIdEvent());
		List<EventIdOutDto> ccmn45doDtos = objCcmn45dDao.getEventDetailList(pCCMN45DInputRec);
		mapEventDtlsToOp(pOutputMsg, ccmn45doDtos);
		log.debug("Exiting method CallCCMN45D in CpsInvConclSelectServiceImpl");
		return ccmn45doDtos;
	}

	/**
	 * 
	 * Method Name: mapEventDtlsToOp Method Description: This service is used in
	 * the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param pOutputMsg
	 * @param ccmn45doDtos
	 */
	private void mapEventDtlsToOp(CpsInvConclSelectoDto pOutputMsg, List<EventIdOutDto> ccmn45doDtos) {
		for (EventIdOutDto ccmn45doDto : ccmn45doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(ccmn45doDto)) {
				pOutputMsg.setCdEventStatus(ccmn45doDto.getCdEventStatus());
				pOutputMsg.setIdEvent(ccmn45doDto.getIdEvent());
				pOutputMsg.setIdStage(ccmn45doDto.getIdStage());
				pOutputMsg.setCdEventType(ccmn45doDto.getCdEventType());
				pOutputMsg.setIdPerson(ccmn45doDto.getIdPerson());
				pOutputMsg.setCdTask(ccmn45doDto.getCdTask());
				pOutputMsg.setEventDescr(ccmn45doDto.getEventDescr());
				pOutputMsg.setDtEventOccurred(ccmn45doDto.getDtEventOccurred());
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCSYS22D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<ContactOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<ContactOutDto> CallCSYS22D(CpsInvConclSelectiDto pInputMsg, CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCSYS22D in CpsInvConclSelectServiceImpl");
		ContactInDto pCSYS22DInputRec = new ContactInDto();
		pCSYS22DInputRec.setIdStage(pInputMsg.getIdStage());
		List<ContactOutDto> csys22doDtos = objCsys22dDao.getDateOccured(pCSYS22DInputRec);
		mapDateDtlsToOp(pOutputMsg, csys22doDtos);
		log.debug("Exiting method CallCSYS22D in CpsInvConclSelectServiceImpl");
		return csys22doDtos;
	}

	/**
	 * 
	 * Method Name: mapDateDtlsToOp Method Description: This service is used in
	 * the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param pOutputMsg
	 * @param csys22doDtos
	 */
	private void mapDateDtlsToOp(CpsInvConclSelectoDto pOutputMsg, List<ContactOutDto> csys22doDtos) {
		for (ContactOutDto csys22doDto : csys22doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(csys22doDto.getDtDTContactOccurred())) {
				pOutputMsg.setDtCPSInvstDtlBegun(csys22doDto.getDtDTContactOccurred());
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCINV14D Method Description:
	 * 
	 * @param pInputMsg
	 * @return List<RiskAssessmentFactorOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<RiskAssessmentFactorDto> CallCINV14D(CpsInvConclSelectiDto pInputMsg,
			CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCINV14D in CpsInvConclSelectServiceImpl");
		EventStagePersonLinkInsUpdInDto pCCMN87DInputRec = new EventStagePersonLinkInsUpdInDto();
		List<RiskAssessmentFactorDto> cinv14doDtos = objCinv14dDao.getRiskAssessmentFactorDtls(pInputMsg.getIdStage());
		mapRiskAssessDtls(pOutputMsg, cinv14doDtos);
		if (!TypeConvUtil.isNullOrEmpty(pOutputMsg.getIdEvent())) {
			pCCMN87DInputRec.setIdStage(pOutputMsg.getIdStage());
			pCCMN87DInputRec.setCdEventType(ServiceConstants.ASSESS_EVENT_TYPE);
			pCCMN87DInputRec.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
			List<EventStagePersonLinkInsUpdOutDto> ccmn87doDtos = objCcmn87dDao.getEventAndStatusDtls(pCCMN87DInputRec);
			mapRiskFindDtls(ccmn87doDtos, pOutputMsg);
		}
		log.debug("Exiting method CallCINV14D in CpsInvConclSelectServiceImpl");
		return cinv14doDtos;
	}

	/**
	 * 
	 * Method Name: mapRiskFindDtls Method Description: This service is used in
	 * the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param ccmn87doDtos
	 * @param pOutputMsg
	 */
	private void mapRiskFindDtls(List<EventStagePersonLinkInsUpdOutDto> ccmn87doDtos,
			CpsInvConclSelectoDto pOutputMsg) {
		for (EventStagePersonLinkInsUpdOutDto ccmn87doDto : ccmn87doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(ccmn87doDto)) {
				if (null != ccmn87doDto.getCdTask() && null != ccmn87doDto.getCdEventStatus()) {
					if (((!ServiceConstants.RISK_ASSMNT_TASK.equalsIgnoreCase(ccmn87doDto.getCdTask()))
							|| (!ServiceConstants.IRA_ASSMT_TASK.equalsIgnoreCase(ccmn87doDto.getCdTask())))
							&& (!((!ccmn87doDto.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENT_COMPLETE))
									|| (!ccmn87doDto.getCdEventStatus()
											.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_PENDING))
									|| (!ccmn87doDto.getCdEventStatus()
											.equalsIgnoreCase(ServiceConstants.EVENTSTATUS_APPROVE))))) {
						pOutputMsg.setCdRiskAssmtRiskFind(ServiceConstants.NULL_VALUE);
						break;
					}
				}
			}
		}
	}

	/**
	 * 
	 * Method Name: mapRiskAssessDtls Method Description: This service is used
	 * in the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param pOutputMsg
	 * @param cinv14doDtos
	 */
	private void mapRiskAssessDtls(CpsInvConclSelectoDto pOutputMsg, List<RiskAssessmentFactorDto> cinv14doDtos) {
		for (RiskAssessmentFactorDto cinv14doDto : cinv14doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(cinv14doDto)) {
				pOutputMsg.setIdEvent(cinv14doDto.getIdEvent());
				pOutputMsg.setIdStage(cinv14doDto.getIdStage());
				pOutputMsg.setCdRiskAssmtPurpose(cinv14doDto.getCdRiskAssmtPurpose());
				pOutputMsg.setCdRiskAssmtRiskFind(cinv14doDto.getCdRiskAssmtRiskFind());
				pOutputMsg.setCdRiskAssmtApAccess(cinv14doDto.getCdRiskAssmtApAccess());
				pOutputMsg.setIndRiskAssmtIntranet(cinv14doDto.getIndRiskAssmtIntranet());
				pOutputMsg.setCdRiskFactorCateg(cinv14doDto.getCdRiskFactorCateg());
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCSYS13D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pOutputMsg
	 * @param pOutputMsg1
	 * @param userId
	 * @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void CallCSYS13D(CpsInvConclSelectoDto pOutputMsg, CpsInvConclSelectoDto pOutputMsg1, String userId) {
		log.debug("Entering method CallCSYS13D in CpsInvConclSelectServiceImpl");
		RiskAssmtNarrOrIraInDto pCSYS13DInputRec = new RiskAssmtNarrOrIraInDto();
		pCSYS13DInputRec.setIdEvent(pOutputMsg.getIdEvent());
		if ((ServiceConstants.YES.equals(pOutputMsg1.getIndRiskAssmtIntranet()))
				|| (ServiceConstants.MERGE.equals(pOutputMsg1.getIndRiskAssmtIntranet()))) {
			pCSYS13DInputRec.setSysTxtTablename(ServiceConstants.IRA_NARRATIVE);
		} else {
			pCSYS13DInputRec.setSysTxtTablename(ServiceConstants.RISK_ASSMT_NARR);
		}
		List<RiskAssmtNarrOrIraOutDto> eventDtls = objCsys13dDao.getEventDtls(pCSYS13DInputRec);
		if (!TypeConvUtil.isNullOrEmpty(eventDtls)) {
			pOutputMsg1.setScrTxtNarrStatus(ServiceConstants.TXT_NARR_EXISTS);
		} else {
			pOutputMsg1.setScrTxtNarrStatus(ServiceConstants.EMPTY_STRING);
		}
		log.debug("Exiting method CallCSYS13D in CpsInvConclSelectServiceImpl");
	}

	/**
	 * 
	 * Method Name: CallCINT40D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<StageRegionOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StageRegionOutDto> CallCINT40D(CpsInvConclSelectiDto pInputMsg, CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCINT40D in CpsInvConclSelectServiceImpl");
		StageRegionInDto pCINT40DInputRec = new StageRegionInDto();
		pCINT40DInputRec.setIdStage(pInputMsg.getIdStage());
		List<StageRegionOutDto> cint40doDtos = objCint40dDao.getStageDtls(pCINT40DInputRec);
		// cint40doDtos.CpsInvConclSelectoDto pOutputMsg = new
		// CpsInvConclSelectoDto();
		mapStageDtlsToOp(pOutputMsg, cint40doDtos);
		log.debug("Exiting method CallCINT40D in CpsInvConclSelectServiceImpl");
		return cint40doDtos;
	}

	/**
	 * 
	 * Method Name: mapStageDtlsToOp Method Description: This service is used in
	 * the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param pOutputMsg
	 * @param cint40doDtos
	 */
	private void mapStageDtlsToOp(CpsInvConclSelectoDto pOutputMsg, List<StageRegionOutDto> cint40doDtos) {
		for (StageRegionOutDto cint40doDto : cint40doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(cint40doDto)) {
				pOutputMsg.setIdStage(cint40doDto.getIdStage());
				pOutputMsg.setTmSysTmStageClose(cint40doDto.getTmSysTmStageClose());
				pOutputMsg.setTmSysTmStageStart(cint40doDto.getTmSysTmStageStart());
				pOutputMsg.setIdUnit(cint40doDto.getIdUnit());
				pOutputMsg.setIndStageClose(cint40doDto.getIndStageClose());
				pOutputMsg.setNmStage(cint40doDto.getNmStage());
				pOutputMsg.setCdStage(cint40doDto.getCdStage());
				pOutputMsg.setCdStageClassification(cint40doDto.getCdStageClassification());
				pOutputMsg.setCdStageCnty(cint40doDto.getCdStageCnty());
				pOutputMsg.setCdStageCurrPriority(cint40doDto.getCdStageCurrPriority());
				pOutputMsg.setCdStageInitialPriority(cint40doDto.getCdStageInitialPriority());
				pOutputMsg.setCdStageProgram(cint40doDto.getCdStageProgram());
				pOutputMsg.setCdStageReasonClosed(cint40doDto.getCdStageReasonClosed());
				pOutputMsg.setDtStageClose(cint40doDto.getDtStageClose());
				pOutputMsg.setDtStageStart(cint40doDto.getDtStageStart());
				pOutputMsg.setIdCase(cint40doDto.getIdCase());
				pOutputMsg.setIdSituation(cint40doDto.getIdSituation());
				pOutputMsg.setStageClosureCmnts(cint40doDto.getStageClosureCmnts());
				pOutputMsg.setStagePriorityCmnts(cint40doDto.getStagePriorityCmnts());
				pOutputMsg.setCdStageRegion(cint40doDto.getCdStageRegion());
				pOutputMsg.setCdStageRsnPriorityChgd(cint40doDto.getCdStageRsnPriorityChgd());
				pOutputMsg.setCdStageType(cint40doDto.getCdStageType());
				pOutputMsg.setTsLastUpdate(cint40doDto.getTsLastUpdate());
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCLSC84D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<StageLinkIncomeDetailOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StageLinkIncomeDetailOutDto> CallCLSC84D(CpsInvConclSelectiDto pInputMsg,
			CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCLSC84D in CpsInvConclSelectServiceImpl");
		StageLinkIncomeDetailInDto pCLSC84DInputRec = new StageLinkIncomeDetailInDto();
		pCLSC84DInputRec.setIdStage(pInputMsg.getIdStage());
		List<StageLinkIncomeDetailOutDto> clsc84doDtos = objClsc84dDao.stageIncomingDtls(pCLSC84DInputRec);
		mapInvtIntakeDtls(pOutputMsg, clsc84doDtos);
		log.debug("Exiting method CallCLSC84D in CpsInvConclSelectServiceImpl");
		return clsc84doDtos;
	}

	/**
	 * 
	 * Method Name: mapInvtIntakeDtls Method Description: This service is used
	 * in the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param pOutputMsg
	 * @param clsc84doDtos
	 */
	private void mapInvtIntakeDtls(CpsInvConclSelectoDto pOutputMsg, List<StageLinkIncomeDetailOutDto> clsc84doDtos) {
		for (StageLinkIncomeDetailOutDto clsc84doDto : clsc84doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(clsc84doDto)) {
				pOutputMsg.setDtCPSInvstDtlIntake(clsc84doDto.getDtIncomingCall());
				pOutputMsg.setIdPriorStage(clsc84doDto.getIdPriorStage());
				pOutputMsg.setIndIncmgSuspMeth(clsc84doDto.getIndIncmgSuspMeth());
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCINVD3D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<AllegationDtlsOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<AllegationDtlsOutDto>
	CallCINVD3D(CpsInvConclSelectiDto pInputMsg, CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCINVD3D in CpsInvConclSelectServiceImpl");
		AllegationDtlsInDto pCINVD3DInputRec = new AllegationDtlsInDto();
		pOutputMsg.setIndPhabSxabAllegExist(ServiceConstants.AR_NO);
		pCINVD3DInputRec.setIdStage(pInputMsg.getIdStage());
		List<AllegationDtlsOutDto> allegationDtlsOutDtos = objCinvd3dDao.getAllegationDtls(pCINVD3DInputRec);
		for (AllegationDtlsOutDto allegationDtlsOutDto : allegationDtlsOutDtos) {
			if (!TypeConvUtil.isNullOrEmpty(allegationDtlsOutDto)) {
				if (!TypeConvUtil.isNullOrEmpty(allegationDtlsOutDto.getSysNbrIdAllgtn())) {
					pOutputMsg.setIndPhabSxabAllegExist(ServiceConstants.YES);
				}
			}
		}
		log.debug("Exiting method CallCINVD3D in CpsInvConclSelectServiceImpl");
		return allegationDtlsOutDtos;
	}

	/**
	 * 
	 * Method Name: CallCINVG1D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<AllegationStageCaseOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<AllegationStageCaseOutDto> CallCINVG1D(CpsInvConclSelectiDto pInputMsg,
			CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCINVG1D in CpsInvConclSelectServiceImpl");
		AllegationStageCaseInDto pCINVG1DInputRec = new AllegationStageCaseInDto();
		pOutputMsg.setIndLbtrSxtrAllegExist(ServiceConstants.AR_NO);
		pCINVG1DInputRec.setIdStage(pInputMsg.getIdStage());
		List<AllegationStageCaseOutDto> cinvg1doDtos = objCinvg1dDao.caseExistDtls(pCINVG1DInputRec);
		for (AllegationStageCaseOutDto cinvg1doDto : cinvg1doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(cinvg1doDto)) {
				pOutputMsg.setIndLbtrSxtrAllegExist(cinvg1doDto.getIndChildSexLaborTrafficExists());
			}
		}
		log.debug("Exiting method CallCINVG1D in CpsInvConclSelectServiceImpl");
		return cinvg1doDtos;
	}

	/**
	 * 
	 * Method Name: CallCINT20D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<StagePersonLinkStTypeRoleOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StagePersonLinkStTypeRoleOutDto> CallCINT20D(CpsInvConclSelectiDto pInputMsg) {
		log.debug("Entering method CallCINT20D in CpsInvConclSelectServiceImpl");
		StagePersonLinkStTypeRoleInDto pCINT20DInputRec = new StagePersonLinkStTypeRoleInDto();
		pCINT20DInputRec.setIdStage(pInputMsg.getIdStage());
		pCINT20DInputRec.setCdStagePersRole(ServiceConstants.STAGE_PERS_ROLE_UK);
		pCINT20DInputRec.setCdStagePersType(ServiceConstants.PERSON_ROLE_PRINCIPAL);
		List<StagePersonLinkStTypeRoleOutDto> cint20doDtos = objCint20dDao.stagePersonDtls(pCINT20DInputRec);
		for (StagePersonLinkStTypeRoleOutDto cint20doDto : cint20doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(cint20doDto)) {
			}
		}
		log.debug("Exiting method CallCINT20D in CpsInvConclSelectServiceImpl");
		return cint20doDtos;
	}

	/**
	 * 
	 * Method Name: CallCLSC18D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pInputMsg
	 * @return List<StagePersonLinkPersonStgTypeOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StagePersonLinkPersonStgTypeOutDto> CallCLSC18D(CpsInvConclSelectiDto pInputMsg,
			CpsInvConclSelectoDto pOutputMsg) {
		log.debug("Entering method CallCLSC18D in CpsInvConclSelectServiceImpl");
		StagePersonLinkPersonStgTypeInDto pCLSC18DInputRec = new StagePersonLinkPersonStgTypeInDto();
		pOutputMsg.setIndPrnUk(ServiceConstants.AR_NO);
		pCLSC18DInputRec.setCdStagePersType(ServiceConstants.PERSON_ROLE_PRINCIPAL);
		pCLSC18DInputRec.setIdStage(pInputMsg.getIdStage());
		List<StagePersonLinkPersonStgTypeOutDto> clsc18doDtos = objClsc18dDao.getPersonDtls(pCLSC18DInputRec);
		mapPersonDtls(clsc18doDtos, pOutputMsg);
		log.debug("Exiting method CallCLSC18D in CpsInvConclSelectServiceImpl");
		return clsc18doDtos;
	}

	/**
	 * 
	 * Method Name: mapPersonDtls Method Description: This service is used in
	 * the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param clsc18doDtos
	 * @param pOutputMsg
	 */
	private void mapPersonDtls(List<StagePersonLinkPersonStgTypeOutDto> clsc18doDtos,
			CpsInvConclSelectoDto pOutputMsg) {
		boolean bUnknownName;
		for (StagePersonLinkPersonStgTypeOutDto clsc18doDto : clsc18doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(clsc18doDto)) {
				bUnknownName = false;
				if ((TypeConvUtil.isNullOrEmpty(clsc18doDto.getNmPersonFirst()))
						&& (TypeConvUtil.isNullOrEmpty(clsc18doDto.getNmPersonLast()))) {
					bUnknownName = true;
				}
				if ((ServiceConstants.REQ_FUNC_CD_UPDATE.equals(clsc18doDto.getPersonSex())
						|| ServiceConstants.YES.equals(clsc18doDto.getIndPersonDobApprox())) && true != bUnknownName) {
					pOutputMsg.setIndPrnUk(ServiceConstants.YES);
					break;
				}
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCheckCasePending Method Description: This service is
	 * used in the Predisplay callback of window CINV06W - CPS INV CONCLUSION.
	 * It retrieves all the values necessary to populate window.
	 * 
	 * @param ulIdCaseMerge
	 * @param pbCasePending
	 * @
	 */
	@SuppressWarnings("unused")
	public void CallCheckCasePending(long ulIdCaseMerge, boolean pbCasePending) {
		log.debug("Entering method CallCheckCasePending in CpsInvConclSelectServiceImpl");
		boolean bLocalCasePending = false;
		CaseMergeToInDto idCaseToInput = new CaseMergeToInDto();
		CaseMergeToOutDto idCaseToOutput = new CaseMergeToOutDto();
		CaseMergeFromInDto idCaseFromInput = new CaseMergeFromInDto();
		CaseMergeFromOutDto idCaseFromOutput = new CaseMergeFromOutDto();
		CpsInvConclSelectoDto pOutputMsg = new CpsInvConclSelectoDto();
		if ((TypeConvUtil.isNullOrEmpty(idCaseFromInput)) || (TypeConvUtil.isNullOrEmpty(idCaseFromOutput))
				|| (TypeConvUtil.isNullOrEmpty(idCaseToInput)) || (TypeConvUtil.isNullOrEmpty(idCaseToOutput))) {
		}
		bLocalCasePending = pbCasePending;
		idCaseToInput.setIdCaseMergeTo(ulIdCaseMerge);
		CallCLSC68D(idCaseToInput, bLocalCasePending);
		if (!bLocalCasePending) {
			idCaseFromInput.setIdCaseMergeFrom(ulIdCaseMerge);
			CallCLSC67D(idCaseFromInput, bLocalCasePending);
		}
		if (bLocalCasePending) {
			pOutputMsg.setIndCaseMergePending(ServiceConstants.YES);
		} else {
			pOutputMsg.setIndCaseMergePending(ServiceConstants.AR_NO);
		}
		log.debug("Exiting method CallCheckCasePending in CpsInvConclSelectServiceImpl");
	}

	/**
	 * 
	 * Method Name: CallCLSC68D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pbCLSC68DInputRec
	 * @param pbCasePending
	 * @return List<CaseMergeToOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<CaseMergeToOutDto> CallCLSC68D(CaseMergeToInDto pbCLSC68DInputRec, boolean pbCasePending) {
		log.debug("Entering method CallCLSC68D in CpsInvConclSelectServiceImpl");
		CaseMergeToInDto pCLSC68DInputRec = new CaseMergeToInDto();
		pCLSC68DInputRec.setIdCaseMergeTo(pbCLSC68DInputRec.getIdCaseMergeTo());
		List<CaseMergeToOutDto> clsc68doDtos = objClsc68dDao.getCaseMergeDtls(pCLSC68DInputRec);
		mapPendingStatus68(clsc68doDtos);
		log.debug("Exiting method CallCLSC68D in CpsInvConclSelectServiceImpl");
		return clsc68doDtos;
	}

	/**
	 * 
	 * Method Name: mapPendingStatus68 Method Description: This service is used
	 * in the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param clsc68doDtos
	 */
	@SuppressWarnings("unused")
	private void mapPendingStatus68(List<CaseMergeToOutDto> clsc68doDtos) {
		boolean pbCasePending = false;
		for (CaseMergeToOutDto clsc68doDto : clsc68doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(clsc68doDto)
					&& !TypeConvUtil.isNullOrEmpty(clsc68doDto.getIndCaseMergePending())) {
				if (clsc68doDto.getIndCaseMergePending().equals(ServiceConstants.MERGE)) {
					pbCasePending = true;
				}
			}
		}
	}

	/**
	 * 
	 * Method Name: CallCLSC67D Method Description: This service is used in the
	 * Predisplay callback of window CINV06W - CPS INV CONCLUSION. It retrieves
	 * all the values necessary to populate window.
	 * 
	 * @param pbCLSC67DInputRec
	 * @param pbCasePending
	 * @return List<CaseMergeFromOutDto> @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<CaseMergeFromOutDto> CallCLSC67D(CaseMergeFromInDto pbCLSC67DInputRec, boolean pbCasePending) {
		log.debug("Entering method CallCLSC67D in CpsInvConclSelectServiceImpl");
		CaseMergeFromInDto pCLSC67DInputRec = new CaseMergeFromInDto();
		pCLSC67DInputRec.setIdCaseMergeFrom(pbCLSC67DInputRec.getIdCaseMergeFrom());
		List<CaseMergeFromOutDto> clsc67doDtos = objClsc67dDao.getCaseMergeDtls(pCLSC67DInputRec);
		mapPendingStatus67(clsc67doDtos);
		log.debug("Exiting method CallCLSC67D in CpsInvConclSelectServiceImpl");
		return clsc67doDtos;
	}

	/**
	 * 
	 * Method Name: mapPendingStatus67 Method Description: This service is used
	 * in the Predisplay callback of window CINV06W - CPS INV CONCLUSION. It
	 * retrieves all the values necessary to populate window.
	 * 
	 * @param clsc67doDtos
	 */
	@SuppressWarnings("unused")
	private void mapPendingStatus67(List<CaseMergeFromOutDto> clsc67doDtos) {
		boolean pbCasePending;
		for (CaseMergeFromOutDto clsc67doDto : clsc67doDtos) {
			if (!TypeConvUtil.isNullOrEmpty(clsc67doDto)) {
				if (!TypeConvUtil.isNullOrEmpty(clsc67doDto.getIndCaseMergePending())
						&& clsc67doDto.getIndCaseMergePending().equals(ServiceConstants.MERGE)) {
					pbCasePending = true;
				}
			}
		}
	}
}
