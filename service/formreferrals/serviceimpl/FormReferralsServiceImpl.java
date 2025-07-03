package us.tx.state.dfps.service.formreferrals.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CourtesyFormReferrals;
import us.tx.state.dfps.common.domain.CourtesyReferralIntrvw;
import us.tx.state.dfps.common.domain.FbssReferrals;
import us.tx.state.dfps.common.domain.FormsReferrals;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.QuickFind;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.AudDiligentSearchReq;
import us.tx.state.dfps.service.common.request.CourtesyFormReq;
import us.tx.state.dfps.service.common.request.DiligentSearchRtrvReq;
import us.tx.state.dfps.service.common.request.FormReferralsReq;
import us.tx.state.dfps.service.common.request.OnLoadDlgntHdrReq;
import us.tx.state.dfps.service.common.response.AudDiligentSearchRes;
import us.tx.state.dfps.service.common.response.CourtesyFormRes;
import us.tx.state.dfps.service.common.response.DiligentSearchRtrvRes;
import us.tx.state.dfps.service.common.response.FormReferralsRes;
import us.tx.state.dfps.service.common.response.OnLoadDlgntHdrRes;
import us.tx.state.dfps.service.common.response.PrsnListRtrvForDlgntSrchRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.formreferrals.dao.FormReferralsDao;
import us.tx.state.dfps.service.formreferrals.dto.CaseWorkerDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.CourtesyFormReferlDto;
import us.tx.state.dfps.service.formreferrals.dto.CourtesyInterviewDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchChildDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.DlgntSrchHdrDto;
import us.tx.state.dfps.service.formreferrals.dto.FbssReferralsDto;
import us.tx.state.dfps.service.formreferrals.dto.FormReferralsDto;
import us.tx.state.dfps.service.formreferrals.dto.OnLoadDlgntHdrDto;
import us.tx.state.dfps.service.formreferrals.dto.QuickFindDto;
import us.tx.state.dfps.service.formreferrals.dto.QuickFindPersonDto;
import us.tx.state.dfps.service.formreferrals.dto.RequesterDtlDto;
import us.tx.state.dfps.service.formreferrals.dto.SupervisorDtlDto;
import us.tx.state.dfps.service.formreferrals.service.FormReferralsService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FbssReferralPrefillData;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.workload.dto.EventDto;
/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jul 7, 2017- 2:26:44 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
public class FormReferralsServiceImpl implements FormReferralsService {

	@Autowired
	FormReferralsDao formReferralsDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	PersonListService personListService;

	//[artf151021] UC 561 FBSS Referral Printable Form
	@Autowired
	private FbssReferralPrefillData fbssReferralPrefillData;

	@Autowired
	EventDao eventDao;
	
	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	ApprovalCommonService approvalCommonService;

	@Autowired
	CaseSummaryDao caseSummaryDao;
	@Autowired
	StageDao stageDao;
	private static final Logger log = Logger.getLogger(FormReferralsServiceImpl.class);

	/**
	 * Method Name: formReferralsList Method Description: Method to poulate
	 * Forms list.
	 * 
	 * @param formReq
	 * @return formReferralsRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes getFormReferralsList(FormReferralsReq formReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		log.info("TransactionId :" + formReq.getTransactionId());
		formReferralsRes = formReferralsDao.formReferralsList(formReq);
		return formReferralsRes;
	}

	/**
	 * Method Name: formReferralsDelete Method Description: Method to delete a
	 * record in Forms_Referrals table.
	 * 
	 * @param formReq
	 * @return formReferralsRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes formReferralsDelete(FormReferralsReq formReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		formReferralsDao.formReferralsDelete(formReq);
		log.info("TransactionId :" + formReq.getTransactionId());
		return formReferralsRes;
	}

	/**
	 * Method Name: getCourtesyReferralDetail Method Description: Method to
	 * retrieve data from Courtesy referral and interview
	 * 
	 * @param courtesyFormReq
	 * @return courtesyFormRes
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CourtesyFormRes getCourtesyReferralDetail(CourtesyFormReq courtesyFormReq) {
		CourtesyFormRes courtesyFormRes = new CourtesyFormRes();
		courtesyFormRes.setCourtesyFormReferlDto(formReferralsDao
				.getCourtesyReferralDetail(courtesyFormReq.getCourtesyFormReferlDto().getIdFormsReferrals()));
		courtesyFormRes.setCourtesyInterviewList(formReferralsDao
				.getCourtesyReferralInterview(courtesyFormReq.getCourtesyFormReferlDto().getIdFormsReferrals()));
		return courtesyFormRes;
	}

	/**
	 * Method Name: saveCourtesyReferralDetail Method Description: Method to
	 * save Courtesy referral.
	 * 
	 * @param formReferralsReq
	 * @return courtesyFormRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CourtesyFormRes saveCourtesyReferralDetail(FormReferralsReq formReferralsReq) {
		CourtesyFormReferrals courtesyFormReferrals = new CourtesyFormReferrals();
		String formType = null;
		BeanUtils.copyProperties(formReferralsReq.getCourtesyFormReferlDto(), courtesyFormReferrals);
		courtesyFormReferrals.setDtCreated(new Date());
		courtesyFormReferrals.setDtLastUpdate(new Date());
		FormsReferrals formsReferrals = new FormsReferrals();
		formsReferrals.setIdFormsReferrals(formReferralsReq.getCourtesyFormReferlDto().getIdFormsReferrals());
		// Save Courtesy referrals
		courtesyFormReferrals.setIdFormsReferrals(formsReferrals);
		// Save Forms_Referrals
		formsReferrals.setDtCreated(new Date());
		formsReferrals.setDtLastUpdate(new Date());
		if (!TypeConvUtil.isNullOrEmpty(formReferralsReq.getIdStage()))
			formsReferrals.setIdStage(formReferralsReq.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(formReferralsReq.getCourtesyFormReferlDto().getCdRqstType())) {
			if (ServiceConstants.CRTSY.equalsIgnoreCase(formReferralsReq.getCourtesyFormReferlDto().getCdRqstType())) {
				formType = ServiceConstants.COURTESY_DTL;
			} else if (ServiceConstants.TRNFR
					.equalsIgnoreCase(formReferralsReq.getCourtesyFormReferlDto().getCdRqstType())) {
				formType = ServiceConstants.CASE_TRANSFER;
			}
			formsReferrals.setCdFormType(formType);
		}
		if (!TypeConvUtil.isNullOrEmpty(formReferralsReq.getCourtesyFormReferlDto().getIdLastUpdatePerson()))
			formsReferrals.setIdLastUpdatePerson(formReferralsReq.getCourtesyFormReferlDto().getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(formReferralsReq.getCourtesyFormReferlDto().getIdCreatedPerson()))
			formsReferrals.setIdCreatedPerson(formReferralsReq.getCourtesyFormReferlDto().getIdCreatedPerson());
		if (!TypeConvUtil.isNullOrEmpty(formReferralsReq.getCourtesyFormReferlDto().getIdFormsReferrals()))
			formsReferrals.setIdFormsReferrals(formReferralsReq.getCourtesyFormReferlDto().getIdFormsReferrals());
		String description = ServiceConstants.COURTESY_DESC;
		String desc1 = ServiceConstants.COURTESY_DESC;
		String desc2 = ServiceConstants.TRANSFER_DESC;
		if (ServiceConstants.CASE_COURTESY
				.equalsIgnoreCase(formReferralsReq.getCourtesyFormReferlDto().getCdRqstType()))
			description = desc1;
		else if (ServiceConstants.CASE_TRANSFER
				.equalsIgnoreCase(formReferralsReq.getCourtesyFormReferlDto().getCdRqstType()))
			description = desc2;
		Long idEvent = formReferralsDao.postEvent(formReferralsReq, description);
		formsReferrals.setIdEvent(idEvent);
		// formReferralsDao.formReferralsSave(formsReferrals);
		Long idFormReferrals = formReferralsDao.formReferralsSave(formsReferrals);
		formsReferrals.setIdFormsReferrals(idFormReferrals);
		// Calendar date conversion
		Date cntctNeeded = new Date();
		if (!TypeConvUtil.isNullOrEmpty(formReferralsReq.getCourtesyFormReferlDto().getDtCntctNeeded())) {
			cntctNeeded = formReferralsReq.getCourtesyFormReferlDto().getDtCntctNeeded();
			Calendar cal = Calendar.getInstance();
			cal.setTime(cntctNeeded);
			cntctNeeded = cal.getTime();
		}
		Date beginDate = new Date();
		if (!TypeConvUtil.isNullOrEmpty(formReferralsReq.getCourtesyFormReferlDto().getDtRqst())) {
			beginDate = formReferralsReq.getCourtesyFormReferlDto().getDtRqst();
			Calendar cal = Calendar.getInstance();
			cal.setTime(beginDate);
			beginDate = cal.getTime();
		}
		courtesyFormReferrals.setDtCntctNeeded(cntctNeeded);
		courtesyFormReferrals.setDtRqst(beginDate);
		// Save Courtesy referrals
		courtesyFormReferrals.setIdFormsReferrals(formsReferrals);
		courtesyFormReferrals = formReferralsDao.saveCourtesyReferralDetail(courtesyFormReferrals);
		CourtesyFormReferlDto courtesyFormReferlDto = new CourtesyFormReferlDto();
		BeanUtils.copyProperties(courtesyFormReferrals, courtesyFormReferlDto);
		CourtesyFormRes courtesyFormRes = new CourtesyFormRes();
		courtesyFormReferlDto.setIdCourtesyFormReferrals(courtesyFormReferrals.getIdCourtesyFormReferrals());
		courtesyFormReferlDto.setIdFormsReferrals(idFormReferrals);
		courtesyFormRes.setCourtesyFormReferlDto(courtesyFormReferlDto);
		return courtesyFormRes;
	}

	/**
	 * Method Name: getQuickFind Method Description:Method to retrieve record
	 * from quick_find table.
	 * 
	 * @param formReq
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes getQuickFind(FormReferralsReq formReq) {
		FormReferralsRes formRes = new FormReferralsRes();
		QuickFindDto quickFindDto = new QuickFindDto();
		FormReferralsDto formReferralsDto = new FormReferralsDto();
		log.info("TransactionId :" + formReq.getTransactionId());
		FormsReferrals formReferralsentity = formReferralsDao.getFormReferrals(formReq);
		Set<QuickFind> qucikFindSet = formReferralsentity.getQuickFindCollection();
		QuickFind entity = new ArrayList<>(qucikFindSet).get(0);
		List<QuickFindDto> QuickFndLst = new ArrayList<>();
		List<FormReferralsDto> FormReferralsDtoLst = new ArrayList<>();
		if (entity != null) {
			quickFindDto.setIdPerson(entity.getIdSbjctPerson());
			quickFindDto.setIdCreatedPerson(entity.getIdCreatedPerson());
			quickFindDto.setIdCase(entity.getIdCase());
			quickFindDto.setCurrentAddr(entity.getAddrStLn1());
			quickFindDto.setCurrentApt(entity.getAddrStLn2());
			quickFindDto.setCurrentCity(entity.getAddrCity());
			quickFindDto.setCurrentCounty(entity.getCdCnty());
			quickFindDto.setCurrentPhn(entity.getNbrPhone());
			quickFindDto.setCurrentState(entity.getCdState());
			quickFindDto.setCurrentZip(entity.getAddrZip());
			quickFindDto.setDob(entity.getCdSbjctDob());
			quickFindDto.setIdQuickFind(entity.getIdQuickFind());
			quickFindDto.setIndQuickEnd(entity.getIndQuickEnd());
			quickFindDto.setLookingFor(entity.getTxtLookingFor());
			quickFindDto.setOther(entity.getTxtOther());
			quickFindDto.setRequestorCounty(entity.getCdRqstrCnty());
			quickFindDto.setRequestorName(entity.getNmRqstr());
			quickFindDto.setRequestorRegion(entity.getCdRqstrRegion());
			quickFindDto.setSsn(entity.getCdSbjctSsn());
			quickFindDto.setSubjectAliase(entity.getTxtSbjctAlias());
			// retrieve subject name from person entity
			if (!TypeConvUtil.isNullOrEmpty(entity.getIdSbjctPerson())) {
				Person person = personDao.getPerson(entity.getIdSbjctPerson());
				quickFindDto.setSubjectName(person.getNmPersonFull());
			}
			quickFindDto.setSubjectRelationship(entity.getTxtSbjctRel());
			quickFindDto.setIndOther(entity.getIndOther());
			quickFindDto.setIndAccounting(entity.getIndAcctng());
			quickFindDto.setIndAps(entity.getIndAps());
			quickFindDto.setIndCcl(entity.getIndCcl());
			quickFindDto.setIndCps(entity.getIndCps());
			quickFindDto.setIndCpsNytd(entity.getIndNytd());
			quickFindDto.setIndEmr(entity.getIndEmr());
			quickFindDto.setIndLegal(entity.getIndLegal());
			formReferralsDto.setFormType(entity.getIdFormsReferrals().getCdFormType());
			formReferralsDto.setIdEvent(entity.getIdFormsReferrals().getIdEvent());
			formReferralsDto.setIdStage(entity.getIdFormsReferrals().getIdStage());
			formReferralsDto.setIdFormsReferrals(entity.getIdFormsReferrals().getIdFormsReferrals());
		}
		QuickFndLst.add(quickFindDto);
		FormReferralsDtoLst.add(formReferralsDto);
		formRes.setQuickFindList(QuickFndLst);
		formRes.setFormReferralsList(FormReferralsDtoLst);
		return formRes;
	}

	/**
	 * Method Name: quickFindSave Method Description:Method to save record in
	 * quick_find table.
	 * 
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes saveQuickFind(FormReferralsReq formReq) {
		FormReferralsRes formRes = new FormReferralsRes();
		log.info("TransactionId :" + formReq.getTransactionId());
		FormsReferrals entity = formReferralsDao.saveQuickFind(formReq);
		QuickFindDto quickFindDto = new QuickFindDto();
		FormReferralsDto formReferralsDto = new FormReferralsDto();
		List<QuickFindDto> QuickFndLst = new ArrayList<>();
		List<FormReferralsDto> FormReferralsDtoLst = new ArrayList<>();
		QuickFind quickFind = (QuickFind) new ArrayList(entity.getQuickFindCollection()).get(0);
		if (quickFind != null) {
			quickFindDto.setIdPerson(quickFind.getIdCreatedPerson());
			quickFindDto.setCurrentAddr(quickFind.getAddrStLn1());
			quickFindDto.setCurrentApt(quickFind.getAddrStLn2());
			quickFindDto.setCurrentCity(quickFind.getAddrCity());
			quickFindDto.setCurrentCounty(quickFind.getCdCnty());
			quickFindDto.setCurrentPhn(quickFind.getNbrPhone());
			quickFindDto.setCurrentState(quickFind.getCdState());
			quickFindDto.setCurrentZip(quickFind.getAddrZip());
			quickFindDto.setDob(quickFind.getCdSbjctDob());
			quickFindDto.setIdQuickFind(quickFind.getIdQuickFind());
			quickFindDto.setIndQuickEnd(quickFind.getIndQuickEnd());
			quickFindDto.setLookingFor(quickFind.getTxtLookingFor());
			quickFindDto.setOther(quickFind.getTxtOther());
			quickFindDto.setRequestorCounty(quickFind.getCdRqstrCnty());
			quickFindDto.setRequestorName(quickFind.getNmRqstr());
			quickFindDto.setRequestorRegion(quickFind.getCdRqstrRegion());
			quickFindDto.setSsn(quickFind.getCdSbjctSsn());
			quickFindDto.setSubjectAliase(quickFind.getTxtSbjctAlias());
			quickFindDto.setSubjectName(quickFind.getNmSbjct());
			quickFindDto.setSubjectRelationship(quickFind.getTxtSbjctRel());
			quickFindDto.setIndOther(quickFind.getIndOther());
			formReferralsDto.setFormType(entity.getCdFormType());
			formReferralsDto.setDtLastUpdate(entity.getDtLastUpdate());
			formReferralsDto.setIdEvent(entity.getIdEvent());
			formReferralsDto.setIdStage(entity.getIdStage());
			formReferralsDto.setIdFormsReferrals(entity.getIdFormsReferrals());
		}
		QuickFndLst.add(quickFindDto);
		FormReferralsDtoLst.add(formReferralsDto);
		formRes.setQuickFindList(QuickFndLst);
		formRes.setFormReferralsList(FormReferralsDtoLst);
		return formRes;
	}

	/**
	 * Method Name: getFbssReferrals Method Description:Method to retrieve
	 * record from fbss_referrals table.
	 * 
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes getFbssReferrals(FormReferralsReq formReq) {
		FormReferralsRes formRes = new FormReferralsRes();
		FbssReferralsDto fbssReferralsDto = new FbssReferralsDto();
		FormReferralsDto formReferralsDto = new FormReferralsDto();
		log.info("TransactionId :" + formReq.getTransactionId());
		FormsReferrals formReferrals = formReferralsDao.getFormReferrals(formReq);
		FbssReferrals entity = new ArrayList<>(formReferrals.getFbssReferralsCollection()).get(0);
		List<FbssReferralsDto> fbssLst = new ArrayList<>();
		List<FormReferralsDto> formReferralsDtoLst = new ArrayList<>();
		if (entity != null) {
			fbssReferralsDto.setAddrCity(entity.getAddrCity());
			fbssReferralsDto.setAddrSt1(entity.getAddrStLn1());
			fbssReferralsDto.setAddrSt2(entity.getAddrStLn2());
			fbssReferralsDto.setAddrZip(entity.getAddrZip());
			fbssReferralsDto.setBarriersFamily(entity.getIndFmlyBarriers());
			fbssReferralsDto.setCaseWorker(entity.getNmCswrkr());
			fbssReferralsDto.setCaseWorkerPhn(entity.getNbrCswrkrPhone());
			fbssReferralsDto.setChildPcsp(entity.getIndChildPcsp());
			fbssReferralsDto.setContactFamily(entity.getTxtCntctFmly());
			fbssReferralsDto.setCourtOrder(entity.getIndCourtOrdr());
			fbssReferralsDto.setDtCourtHearing(entity.getDtCourtHearng());
			fbssReferralsDto.setDtCreated(entity.getDtCreated());
			fbssReferralsDto.setDtReferred(entity.getDtRefrdToFbss());
			fbssReferralsDto.setDtUpdatedOn(entity.getDtLastUpdate());
			fbssReferralsDto.setFamilyAccept(entity.getIndFmlyAccptsFbss());
			fbssReferralsDto.setIdCreatedPerson(entity.getIdCreatedPerson());
			fbssReferralsDto.setIdFbssReferrals(entity.getIdFbssReferrals());
			fbssReferralsDto.setIdLastUpdatePerson(entity.getIdLastUpdatePerson());
			fbssReferralsDto.setNonResident(entity.getIndAbsntPrnt());
			fbssReferralsDto.setPendingLeAction(entity.getIndPndngLeActns());
			fbssReferralsDto.setReferredLang(entity.getTxtPrfrdLang());
			fbssReferralsDto.setReferringCnty(entity.getCdRfrngCnty());
			fbssReferralsDto.setReferringUnit(entity.getCdRfrngUnit());
			fbssReferralsDto.setSpecialChildern(entity.getIndSpeclNeeds());
			fbssReferralsDto.setSupervisor(entity.getNmSuprvsr());
			fbssReferralsDto.setSupervisorPhn(entity.getNbrSupvsrPhone());
			fbssReferralsDto.setBarriersFamilyStr(entity.getTxtFmlyBarriers());
			fbssReferralsDto.setTxtComments(entity.getTxtAdtnlCmnts());
			fbssReferralsDto.setDangerIndicator(entity.getTxtDngrs());
			fbssReferralsDto.setFamilyAcceptStr(entity.getTxtFmlyAccptsFbss());
			fbssReferralsDto.setNonResidentStr(entity.getTxtAbsntPrnt());
			fbssReferralsDto.setParentProtective(entity.getTxtPrntPrtctvActns());
			fbssReferralsDto.setParentProtectiveTarget(entity.getTxtPrntDmnshdCapcty());
			fbssReferralsDto.setPendingLeActionStr(entity.getTxtPndngLeActns());
			fbssReferralsDto.setSpecialChildernStr(entity.getTxtSpeclNeeds());
			fbssReferralsDto.setWorkerSafetyStr(entity.getTxtWrkrSfty());
			fbssReferralsDto.setWorkerSafety(entity.getIndWrkrSfty());
			fbssReferralsDto.setIdPerson(entity.getIdPerson());
			formReferralsDto.setFormType(entity.getIdFormsReferrals().getCdFormType());
			formReferralsDto.setIdEvent(entity.getIdFormsReferrals().getIdEvent());
			String cdEventStatus = eventDao.getEventStatus(entity.getIdFormsReferrals().getIdEvent());
			formReferralsDto.setEventStatus(cdEventStatus);
			formReferralsDto.setIdStage(entity.getIdFormsReferrals().getIdStage());
			formReferralsDto.setIdFormsReferrals(entity.getIdFormsReferrals().getIdFormsReferrals());
			//PPM#46797 artf150671 : Changes to fetch the new variables that are added as per the concurrent stage project
			fbssReferralsDto.setIdCpsSA(entity.getIdCpsSA());
			fbssReferralsDto.setCourtReportUpld(entity.getCourtReportUpld());
			fbssReferralsDto.setFtmHeld(entity.getFtmHeld());
			fbssReferralsDto.setAutoAssign(entity.getAutoAssign());
		}
		fbssLst.add(fbssReferralsDto);
		formReferralsDtoLst.add(formReferralsDto);
		formRes.setFbssReferralsList(fbssLst);
		formRes.setFormReferralsList(formReferralsDtoLst);
		return formRes;
	}

	/**
	 * Method Name: fbssSave Method Description:Method to save record in
	 * fbss_referrals table.
	 * 
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes saveFBSS(FormReferralsReq formReq) {
		FormReferralsRes formRes = new FormReferralsRes();
		log.info("TransactionId :" + formReq.getTransactionId());
		//for invalidating the approval in case event is PEND and user not navigating from Task
		Long eventId = formReq.getFormReferralsList().get(0).getIdEvent();
		Consumer<Long> invalidateEvent = idEvent -> {
			ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
			ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
			approvalCommonInDto.setIdEvent(idEvent);
			// Call Service to invalidate approval
			approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);
		};
		if (!ObjectUtils.isEmpty(eventId) && eventId > ServiceConstants.ZERO ) {
			String cdEventStatus = eventDao.getEventStatus(eventId);
			if (!formReq.getFbssReferralsList().get(0).isApprovalMode() && ServiceConstants.EVENT_STATUS_PEND.equals(cdEventStatus)) {
				invalidateEvent.accept(eventId);
			}
		}

		// for invalidating the INV/A-R Conclusion approval in case it is PEND
		String cdTask = ServiceConstants.CSTAGES_INV.equalsIgnoreCase(formReq.getFormReferralsList().get(0).getStage()) ? ServiceConstants.INV_CONCLUSION_TASK_CODE  : ServiceConstants.AR_CONCLUSION_TASK_CODE;
			List<EventDto> eventDtoList = eventDao.getEventByStageIDAndTaskCode(formReq.getFormReferralsList().get(0).getIdStage(), cdTask);
			if (!CollectionUtils.isEmpty(eventDtoList)){
				eventDtoList.stream().filter(eventDto-> ServiceConstants.EVENTSTATUS_PENDING.equalsIgnoreCase(eventDto.getCdEventStatus())).forEach(pendeventDto->{
					invalidateEvent.accept(pendeventDto.getIdEvent());
				});
			}

		FormsReferrals entity = formReferralsDao.saveFBSS(formReq);
		FbssReferralsDto fbssReferralsDto = new FbssReferralsDto();
		FormReferralsDto formReferralsDto = new FormReferralsDto();
		List<FbssReferralsDto> fbssReferralsLst = new ArrayList<>();
		List<FormReferralsDto> formReferralsDtoLst = new ArrayList<>();
		FbssReferrals fbssReferrals = (FbssReferrals) new ArrayList(entity.getFbssReferralsCollection()).get(0);
		if (fbssReferrals != null) {
			fbssReferralsDto.setAddrCity(fbssReferrals.getAddrCity());
			fbssReferralsDto.setAddrSt1(fbssReferrals.getAddrStLn1());
			fbssReferralsDto.setAddrSt2(fbssReferrals.getAddrStLn2());
			fbssReferralsDto.setAddrZip(fbssReferrals.getAddrZip());
			fbssReferralsDto.setBarriersFamily(fbssReferrals.getIndFmlyBarriers());
			fbssReferralsDto.setCaseWorker(fbssReferrals.getNmCswrkr());
			fbssReferralsDto.setCaseWorkerPhn(fbssReferrals.getNbrCswrkrPhone());
			fbssReferralsDto.setChildPcsp(fbssReferrals.getIndChildPcsp());
			fbssReferralsDto.setContactFamily(fbssReferrals.getTxtCntctFmly());
			fbssReferralsDto.setCourtOrder(fbssReferrals.getIndCourtOrdr());
			fbssReferralsDto.setDtCourtHearing(fbssReferrals.getDtCourtHearng());
			fbssReferralsDto.setDtCreated(fbssReferrals.getDtCreated());
			fbssReferralsDto.setDtReferred(fbssReferrals.getDtRefrdToFbss());
			fbssReferralsDto.setDtUpdatedOn(fbssReferrals.getDtLastUpdate());
			fbssReferralsDto.setFamilyAccept(fbssReferrals.getIndFmlyAccptsFbss());
			fbssReferralsDto.setIdCreatedPerson(fbssReferrals.getIdCreatedPerson());
			fbssReferralsDto.setIdFbssReferrals(fbssReferrals.getIdFbssReferrals());
			fbssReferralsDto.setIdLastUpdatePerson(fbssReferrals.getIdLastUpdatePerson());
			fbssReferralsDto.setNonResident(fbssReferrals.getIndAbsntPrnt());
			fbssReferralsDto.setPendingLeAction(fbssReferrals.getIndPndngLeActns());
			fbssReferralsDto.setReferredLang(fbssReferrals.getTxtPrfrdLang());
			fbssReferralsDto.setReferringCnty(fbssReferrals.getCdRfrngCnty());
			fbssReferralsDto.setReferringUnit(fbssReferrals.getCdRfrngUnit());
			fbssReferralsDto.setSpecialChildern(fbssReferrals.getIndSpeclNeeds());
			fbssReferralsDto.setSupervisor(fbssReferrals.getNmSuprvsr());
			fbssReferralsDto.setSupervisorPhn(fbssReferrals.getNbrSupvsrPhone());
			fbssReferralsDto.setBarriersFamilyStr(fbssReferrals.getTxtFmlyBarriers());
			fbssReferralsDto.setTxtComments(fbssReferrals.getTxtAdtnlCmnts());
			fbssReferralsDto.setDangerIndicator(fbssReferrals.getTxtDngrs());
			fbssReferralsDto.setFamilyAcceptStr(fbssReferrals.getTxtFmlyAccptsFbss());
			fbssReferralsDto.setNonResidentStr(fbssReferrals.getTxtAbsntPrnt());
			fbssReferralsDto.setParentProtective(fbssReferrals.getTxtPrntPrtctvActns());
			fbssReferralsDto.setParentProtectiveTarget(fbssReferrals.getTxtPrntDmnshdCapcty());
			fbssReferralsDto.setPendingLeActionStr(fbssReferrals.getTxtPndngLeActns());
			fbssReferralsDto.setSpecialChildernStr(fbssReferrals.getTxtSpeclNeeds());
			fbssReferralsDto.setWorkerSafetyStr(fbssReferrals.getTxtWrkrSfty());
			fbssReferralsDto.setWorkerSafety(fbssReferrals.getIndWrkrSfty());
			fbssReferralsDto.setIdPerson(fbssReferrals.getIdPerson());
			//PPM#46797 artf150671 : Changes to fetch the new variables that are added as per the concurrent stage project
			fbssReferralsDto.setIdCpsSA(fbssReferrals.getIdCpsSA());
			fbssReferralsDto.setCourtReportUpld(fbssReferrals.getCourtReportUpld());
			fbssReferralsDto.setFtmHeld(fbssReferrals.getFtmHeld());
			fbssReferralsDto.setAutoAssign(fbssReferrals.getAutoAssign());
			//end
			formReferralsDto.setFormType(entity.getCdFormType());
			formReferralsDto.setDtLastUpdate(entity.getDtLastUpdate());
			formReferralsDto.setIdEvent(entity.getIdEvent());
			formReferralsDto.setIdStage(entity.getIdStage());
			formReferralsDto.setIdFormsReferrals(entity.getIdFormsReferrals());
		}
		fbssReferralsLst.add(fbssReferralsDto);
		formReferralsDtoLst.add(formReferralsDto);
		formRes.setFbssReferralsList(fbssReferralsLst);
		formRes.setFormReferralsList(formReferralsDtoLst);
		return formRes;
	}

	/**
	 * Method Name: quickFindDelete Method Description:Method to delete record
	 * from quick_find table.
	 * 
	 * @param formReq
	 * @return @
	 */
	@Override
	@Transactional
	public FormReferralsRes deleteQuickFind(FormReferralsReq formReq) {
		String msg = formReferralsDao.deleteQuickFind(formReq);
		FormReferralsRes res = new FormReferralsRes();
		res.setStatusMsg(msg);
		return res;
	}

	/**
	 * Method Name: fbssDelete Method Description:Method to delete record from
	 * the fbss_referrals table.
	 * 
	 * @param formReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes deleteFBSS(FormReferralsReq formReq) {
		String msg1 = formReferralsDao.deleteQuickFind(formReq);
		FormReferralsRes res = new FormReferralsRes();
		res.setStatusMsg(msg1);
		return res;
	}

	/**
	 * Method Name: saveCourtesyReferralIntrvw Method Description: Method to
	 * save a record in courtesy referral interview table.
	 * 
	 * 
	 * @param formReferralsReq
	 * @return courtesyFormRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CourtesyFormRes saveCourtesyReferralIntrvw(FormReferralsReq formReferralsReq) {
		CourtesyReferralIntrvw courtesyReferralIntrvw = new CourtesyReferralIntrvw();
		CourtesyFormRes courtesyFormRes = new CourtesyFormRes();
		CourtesyInterviewDto courtesyInterviewDto = new CourtesyInterviewDto();
		BeanUtils.copyProperties(formReferralsReq.getCourtesyInterviewDto(), courtesyReferralIntrvw);
		courtesyReferralIntrvw.setDtCreated(formReferralsReq.getCourtesyInterviewDto().getDtCreated());
		CourtesyFormReferrals courtesyFormReferrals = new CourtesyFormReferrals();
		courtesyFormReferrals
				.setIdCourtesyFormReferrals(formReferralsReq.getCourtesyInterviewDto().getIdCourtesyFormReferrals());
		courtesyReferralIntrvw.setIdCourtesyFormReferrals(courtesyFormReferrals);
		courtesyReferralIntrvw = formReferralsDao.saveCourtesyReferralIntrvwDetail(courtesyReferralIntrvw);
		BeanUtils.copyProperties(courtesyReferralIntrvw, courtesyInterviewDto);
		courtesyFormRes.setCourtesyInterviewDto(courtesyInterviewDto);
		return courtesyFormRes;
	}

	/**
	 * Method Name: CourtesyReferralDelete Method Description: Method to delete
	 * a record in Forms_Referrals table.
	 * 
	 * @param formReq
	 * @return formReferralsRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes courtesyReferralDelete(CourtesyFormReq courtesyFormReq) {
		FormReferralsRes formReferralsRes = new FormReferralsRes();
		FormReferralsReq formReferralsReq = new FormReferralsReq();
		formReferralsReq.setIdFormReferral(courtesyFormReq.getCourtesyFormReferlDto().getIdFormsReferrals());
		formReferralsDao.formReferralsDelete(formReferralsReq);
		//artf220413 : Set event id to the request form in web and add a code to delete the event on service using event id.
		eventDao.deleteEventById(courtesyFormReq.getIdEvent());
		log.info("TransactionId :" + courtesyFormReq.getTransactionId());
		return formReferralsRes;
	}

	/**
	 * Method Name: getQuickFindPerson Method Description:This method is used to
	 * fetch the quch_find page values onload.
	 * 
	 * @param formReq
	 * @return @
	 */
	@Transactional
	public FormReferralsRes getQuickFindPerson(FormReferralsReq formReq) {
		return formReferralsDao.getQuickFindPerson(formReq);
	}

	/**
	 * 
	 * Method Name: getDiligentSearchRtrv Method Description: This service will
	 * retrieve data for Diligent Search screen.
	 * 
	 * @param diligentSearchRtrvReq
	 * @return DiligentSearchRtrvRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public DiligentSearchRtrvRes getDiligentSearchRtrv(DiligentSearchRtrvReq diligentSearchRtrvReq) {
		DiligentSearchRtrvRes diligentSearchRtrvRes = new DiligentSearchRtrvRes();
		DlgntSrchHdrDto dlgntSrchHdrDto = new DlgntSrchHdrDto();
		List<DlgntSrchDtlDto> dlgntSrchDtlDtoList = new ArrayList<>();
		List<DlgntSrchChildDtlDto> dlgntSrchChildDtlDtoList = new ArrayList<>();
		List<DlgntSrchChildDtlDto> dlgntSrchChildDtlList = new ArrayList<>();
		dlgntSrchHdrDto = formReferralsDao.getDiligentSearchHeader(diligentSearchRtrvReq.getIdFormsReferrals());
		diligentSearchRtrvRes.setDlgntSrchHdrDto(dlgntSrchHdrDto);
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto)) {
			Long idDlgntSrchHdr = dlgntSrchHdrDto.getIdDlgntSrchHdr();
			dlgntSrchDtlDtoList = formReferralsDao.getDiligentSearchDetail(idDlgntSrchHdr);
			diligentSearchRtrvRes.setDlgntSrchDtlDtoList(dlgntSrchDtlDtoList);
		}
if (!TypeConvUtil.isNullOrEmpty(dlgntSrchDtlDtoList)) {
			List<Long> idSearchDtl = dlgntSrchDtlDtoList.stream().filter(
					o -> !ServiceConstants.RELATIONSHIP_TO_CHILD_MOTHER.equalsIgnoreCase(o.getReltnshpToChild()))
					.map(DlgntSrchDtlDto::getIdDlgntSrchDtl).collect(Collectors.toList());
			for (Long searchid : idSearchDtl) {
				dlgntSrchChildDtlDtoList = formReferralsDao.getDiligentSearchChildDetails(searchid);
				dlgntSrchChildDtlList.addAll(dlgntSrchChildDtlDtoList);
			}
			diligentSearchRtrvRes.setDlgntSrchChildDtlDtoList(dlgntSrchChildDtlList);
		}
		log.info("TransactionId :" + diligentSearchRtrvReq.getTransactionId());
		return diligentSearchRtrvRes;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AudDiligentSearchRes saveAndUpdateDiligentSearch(AudDiligentSearchReq audDiligentSearchReq) {
		AudDiligentSearchRes audDiligentSearchRes = new AudDiligentSearchRes();
		audDiligentSearchRes = formReferralsDao.saveAndUpdateDiligentSearch(audDiligentSearchReq);
		log.info("TransactionId :" + audDiligentSearchReq.getTransactionId());
		return audDiligentSearchRes;
	}

	/**
	 * 
	 * Method Name: getPersonList Method Description: This service will retrieve
	 * list for persons from STAGE, STAGE_PERSON_LINK and PERSON tables.
	 * 
	 * @param diligentSearchRtrvReq
	 * @return PrsnListRtrvForDlgntSrchRes @
	 */
	public PrsnListRtrvForDlgntSrchRes getPersonList(DiligentSearchRtrvReq diligentSearchRtrvReq) {
		PrsnListRtrvForDlgntSrchRes prsnListRtrvForDlgntSrchRes = new PrsnListRtrvForDlgntSrchRes();
		List<DlgntSrchDtlDto> dlgntSrchDtlDtoList = new ArrayList<>();
		dlgntSrchDtlDtoList = formReferralsDao.getPersonDtlByStageId(diligentSearchRtrvReq.getIdStage());
		if (!ObjectUtils.isEmpty(dlgntSrchDtlDtoList)) {
			prsnListRtrvForDlgntSrchRes.setDlgntSrchDtlDtoList(dlgntSrchDtlDtoList);
		}
		log.info("TransactionId :" + diligentSearchRtrvReq.getTransactionId());
		return prsnListRtrvForDlgntSrchRes;
	}

	/**
	 * Method Name: deletedlgtSearch Method Description:Method to delete record
	 * from the
	 * DLGNT_SRCH_HDR,DLGNT_SRCH_DTL,DLGNT_SRCH_CHILD_DTL,FORMS_REFERRALS table.
	 * 
	 * @param formReq
	 * @return @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes deletedlgtSearch(FormReferralsReq formReq) {
		String msg2 = formReferralsDao.deletedlgtSearch(formReq);
		FormReferralsRes res1 = new FormReferralsRes();
		res1.setStatusMsg(msg2);
		return res1;
	}

	/**
	 * 
	 * Method Name: getDlgntSrchHdrByStageId Method Description: This Service
	 * will retrieve Case Worker, Requester and Supervisor info for Diligent
	 * Search Header.
	 * 
	 * @param onLoadDlgntHdrReq
	 * @return OnLoadDlgntHdrRes
	 * @throws InvalidRequestException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public OnLoadDlgntHdrRes getDlgntSrchHdrByStageId(OnLoadDlgntHdrReq onLoadDlgntHdrReq) {
		OnLoadDlgntHdrRes loadDlgntHdrRes = new OnLoadDlgntHdrRes();
		DlgntSrchHdrDto dlgntSrchHdrDto = new DlgntSrchHdrDto();
		SupervisorDtlDto supervisorDtlDto = new SupervisorDtlDto();
		dlgntSrchHdrDto = formReferralsDao.getCaseWorkerDtl(onLoadDlgntHdrReq.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto)) {
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdCase())) {
				loadDlgntHdrRes.setIdCase(dlgntSrchHdrDto.getIdCase());
			}
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getRegn())) {
				loadDlgntHdrRes.setTxtRegn(dlgntSrchHdrDto.getRegn());
			}
			Long idSupervisor;
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdWrkrPerson())) {
				idSupervisor = formReferralsDao.getSupervisorId(dlgntSrchHdrDto.getIdWrkrPerson());
				OnLoadDlgntHdrDto onLoadDlgntHdrDtoSupr = new OnLoadDlgntHdrDto();
				if (!TypeConvUtil.isNullOrEmpty(idSupervisor)) {
					onLoadDlgntHdrDtoSupr = formReferralsDao.getDlgntHdrByStageId(idSupervisor);
					supervisorDtlDto.setIdSuprvsrPerson(idSupervisor);
					if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoSupr)) {
						if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoSupr.getNmPersonFull())) {
							supervisorDtlDto.setNmPersonFull(onLoadDlgntHdrDtoSupr.getNmPersonFull());
						}
						if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoSupr.getEmail())) {
							supervisorDtlDto.setEmailSuprvsr(onLoadDlgntHdrDtoSupr.getEmail());
						}
						if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoSupr.getCdMail())) {
							supervisorDtlDto.setMailCodeSuprvsr(onLoadDlgntHdrDtoSupr.getCdMail());
						}
						if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoSupr.getPhone())) {
							supervisorDtlDto.setPhoneSuprvsr(onLoadDlgntHdrDtoSupr.getPhone());
						}
					}
				}
			}
		}
		loadDlgntHdrRes.setSupervisorDtlDto(supervisorDtlDto);
		OnLoadDlgntHdrDto onLoadDlgntHdrDto = new OnLoadDlgntHdrDto();
		RequesterDtlDto requesterDtlDto = new RequesterDtlDto();
		onLoadDlgntHdrDto = formReferralsDao.getDlgntHdrByStageId(onLoadDlgntHdrReq.getIdRqstrPerson());
		if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDto)) {
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrReq.getIdRqstrPerson())) {
				requesterDtlDto.setIdRqstrPerson(onLoadDlgntHdrReq.getIdRqstrPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDto.getNmPersonFull())) {
				requesterDtlDto.setNmRqstrFull(onLoadDlgntHdrDto.getNmPersonFull());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDto.getEmail())) {
				requesterDtlDto.setEmailRqstr(onLoadDlgntHdrDto.getEmail());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDto.getCdMail())) {
				requesterDtlDto.setMailCodeRqstr(onLoadDlgntHdrDto.getCdMail());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDto.getPhone())) {
				requesterDtlDto.setRostrPhone(onLoadDlgntHdrDto.getPhone());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDto.getUnitRole())) {
				requesterDtlDto.setUnitRole(onLoadDlgntHdrDto.getUnitRole());
			}
		}
		loadDlgntHdrRes.setRequesterDtlDto(requesterDtlDto);
		OnLoadDlgntHdrDto onLoadDlgntHdrDtoWrkr = new OnLoadDlgntHdrDto();
		CaseWorkerDtlDto caseWorkerDtlDto = new CaseWorkerDtlDto();
		onLoadDlgntHdrDtoWrkr = formReferralsDao.getDlgntHdrByStageId(dlgntSrchHdrDto.getIdWrkrPerson());
		if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoWrkr)) {
			if (!TypeConvUtil.isNullOrEmpty(dlgntSrchHdrDto.getIdWrkrPerson())) {
				caseWorkerDtlDto.setIdWrkrPerson(dlgntSrchHdrDto.getIdWrkrPerson());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoWrkr.getNmPersonFull())) {
				caseWorkerDtlDto.setNmWrkrFull(onLoadDlgntHdrDtoWrkr.getNmPersonFull());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoWrkr.getEmail())) {
				caseWorkerDtlDto.setEmail(onLoadDlgntHdrDtoWrkr.getEmail());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoWrkr.getCdMail())) {
				caseWorkerDtlDto.setMailCodeWrkr(onLoadDlgntHdrDtoWrkr.getCdMail());
			}
			if (!TypeConvUtil.isNullOrEmpty(onLoadDlgntHdrDtoWrkr.getPhone())) {
				caseWorkerDtlDto.setPhoneWrkr(onLoadDlgntHdrDtoWrkr.getPhone());
			}
		}
		loadDlgntHdrRes.setCaseWorkerDtlDto(caseWorkerDtlDto);
		log.info("TransactionId :" + onLoadDlgntHdrReq.getTransactionId());
		return loadDlgntHdrRes;
	}

	/**
	 * 
	 * Method Name: getCaseWorkerCounty Method Description: This method
	 * retrieves case worker's information by passing idPerson.
	 * 
	 * @param idPerson
	 * @return caseWorkerDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaseWorkerDtlDto getCaseWorkerCounty(Long idPerson) {
		CaseWorkerDtlDto caseWorkerDtlDto = new CaseWorkerDtlDto();
		caseWorkerDtlDto = formReferralsDao.getCaseWorkerCounty(idPerson);
		return caseWorkerDtlDto;
	}

	/**
	 * PPM#46797 artf150671
	 * Method Name: getHouseHoldDetails
	 * Method Description: This method retrieves household SDM safety assessment, and address details.
	 *
	 * @param idStage
	 * @return List<QuickFindPersonDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<QuickFindPersonDto> getHouseHoldDetails(Long idStage) {
		List<PersonListDto> personList = personDao.getPersonListByIdStage(idStage, ServiceConstants.STAFF_TYPE).stream()
				.filter(o -> ServiceConstants.PRINCIPAL.equals(o.getStagePersType())).collect(Collectors.toList());
		SelectStageDto selectStageDto=caseSummaryDao.getStage(idStage, ServiceConstants.STAGE_CURRENT);
		List<Long> householdIds = personList.stream().map( PersonListDto::getIdPerson).collect(Collectors.toList());
		return processHouseHoldDetails(formReferralsDao.getHouseHoldDetails(householdIds,selectStageDto.getIdCase()));
	}
	public List<QuickFindPersonDto> processHouseHoldDetails(List<QuickFindPersonDto> householdList){
		Map<Long,List<QuickFindPersonDto>> personByid=  householdList!= null ? householdList.stream().collect(Collectors.groupingBy(QuickFindPersonDto::getIdPerson)) : new HashMap<>();
		List<QuickFindPersonDto> houseHoldDetailsList =new ArrayList<>();
		Map<String,List<String>> followUp=new HashMap<>();
		personByid.forEach( (k,v)->{
			QuickFindPersonDto personDto=v.get(0);
			followUp.clear();
			List<String> dangerIndicators=v.stream().filter(dto -> "SA2".equalsIgnoreCase(dto.getSection())).map(dangerdto->{
				if(20 == dangerdto.getQuestionNbr()){
					return dangerdto.getOtherdesc();
				}else if(7==dangerdto.getQuestionNbr()){
					List<String> followuplist=null;
					if(followUp.get(dangerdto.getSelectedTxt()) != null) {
						followuplist=followUp.get(dangerdto.getSelectedTxt());
					} else{
						followuplist=new ArrayList<>();
					}
					followuplist.add(dangerdto.getFollowupquestiontext());
					followUp.put(dangerdto.getSelectedTxt(),followuplist);
					return null;
				}else {
					return dangerdto.getSelectedTxt();
				}
			}).filter(val-> val !=null).collect(Collectors.toList());
			if(followUp.size()>0){
				String followupresponse=followUp.values().stream().findFirst().get().stream().collect(Collectors.joining("<br>"));
				dangerIndicators.add(followUp.keySet().stream().findAny().get()+"<br>"+followupresponse);
			}
			List<String> protectiveActions=v.stream().filter(dto -> "SA3".equalsIgnoreCase(dto.getSection())).map(dangerdto->{
				if(32==dangerdto.getQuestionNbr()){
					return dangerdto.getOtherdesc();
				}else {
					return dangerdto.getSelectedTxt();
				}
			}).collect(Collectors.toList());
			personDto.setDangerIndicators(dangerIndicators);
			personDto.setProtectiveActions(protectiveActions);
			houseHoldDetailsList.add(personDto);
		});
		return houseHoldDetailsList;
	}
	/**
	 * PPM#46797 artf150671
	 * Method Name: getHouseHoldDetailsBySA
	 * Method Description: This method retrieves household SDM safety assessment, and address details BY idcpssa.
	 *
	 * @param idStage,idcpssa
	 * @return List<QuickFindPersonDto>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<QuickFindPersonDto> getHouseHoldDetailsBySA(Long idcpssa) {		;
		return processHouseHoldDetails(formReferralsDao.getHouseHoldDetailsBySA(idcpssa));
	}

	/**
	 * PPM#46797 artf150671
	 * Method Name: valdiateSaveAndSubmit
	 * Method Description: This method valdiates on SaveAndSubmit.
	 *
	 * @param idStage
	 * @param indApprovalFlow
	 * @param idCpsSa
	 * @param idPersonHouseHold
	 * @param idApproval
	 * @return int
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String validateSaveAndSubmit(Long idStage, String indApprovalFlow, Long idCpsSa, Long idPersonHouseHold,
									 Long idApproval) {

		// For Approval Flow, retrieves the CPS SA ID and Household Person ID from FBSS based on Approval ID
		if ("Y".equals(indApprovalFlow)) {

			Long idFormReferrals =  getFormReferralByApprovalId(idApproval);

			FormReferralsReq formReq = new FormReferralsReq();
			formReq.setIdFormReferral(idFormReferrals);

			FormsReferrals formReferrals = formReferralsDao.getFormReferrals(formReq);
			FbssReferrals fbssReferrals = new ArrayList<>(formReferrals.getFbssReferralsCollection()).get(0);

			idCpsSa = fbssReferrals.getIdCpsSA();
			idPersonHouseHold = fbssReferrals.getIdPerson();
		}

		return formReferralsDao.validateSaveAndSubmit(idStage, indApprovalFlow, idCpsSa, idPersonHouseHold);
	}

	/**
	 * artf151569
	 * Method Name: getFormReferralByApprovalId
	 * Method Description: This method retrieves the Form Referral Id based on the Approval ID.
	 *
	 * @param idApproval
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getFormReferralByApprovalId(Long idApproval) {
		return formReferralsDao.getFormsReferralIdByApproval(idApproval);
	}

	/**
	 * PPM#46797 artf150671
	 * Method Name: getFBSSReferralsForFPR
	 * Method Description:  Method to retrieve
	 * 	 * FBSS referral details
	 * @param idFPRStage
	 * @return FormReferralsDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsDto getFBSSReferralsForFPR(Long idFPRStage) {
		SelectStageDto stageDto=caseSummaryDao.getStage(idFPRStage, ServiceConstants.STAGE_PRIOR);
		return formReferralsDao.getFBSSReferralsForFPR(stageDto.getIdStage());
	}

	/**
	 * artf151021
	 * Method Name: getFbssReferralForm Method Description: This method
	 * retrieves fbss referral info by passing referral id.
	 *
	 * @param FormReferralsReq
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getFbssReferralForm(FormReferralsReq formReq) {
		FormReferralsRes formReferralsRes = getFbssReferralInfo(formReq);
		return fbssReferralPrefillData.returnPrefillData(formReferralsRes);
	}


	/**
	 * Method Name: getFbssReferrals Method Description:Method to retrieve
	 * record from fbss_referrals table.
	 *
	 * @param formReq
	 * @return
	 * @ @throws
	 *       InvalidRequestException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public FormReferralsRes getFbssReferralInfo(FormReferralsReq formReq) {
		FormReferralsRes formRes = new FormReferralsRes();
		FbssReferralsDto fbssReferralsDto = new FbssReferralsDto();
		FormReferralsDto formReferralsDto = new FormReferralsDto();
		EventDto eventDto = null;
		List<QuickFindPersonDto> indicatorsDto = null;
		CapsCaseDto capsCaseDto = null;
		log.info("TransactionId :" + formReq.getTransactionId());
		FormsReferrals formReferrals = formReferralsDao.getFormReferralsByEvent(formReq);
		FbssReferrals entity = null;
		//code to retrieve event status
		if(null != formReferrals && formReferrals.getIdEvent() > 0){
			eventDto = eventDao.getEventByid(formReferrals.getIdEvent());
		}
		
		if(null != eventDto){
			capsCaseDto = capsCaseDao.getCapsCaseByid(eventDto.getIdCase());
		}
		
		if(null != formReferrals.getFbssReferralsCollection() && formReferrals.getFbssReferralsCollection().size() > 0){
			entity = new ArrayList<>(formReferrals.getFbssReferralsCollection()).get(0);
		}
		List<FbssReferralsDto> fbssLst = new ArrayList<>();
		List<FormReferralsDto> formReferralsDtoLst = new ArrayList<>();
		if (null != entity) {			
			if(null != eventDto && Arrays.asList(ServiceConstants.CEVTSTAT_APRV,ServiceConstants.CEVTSTAT_PEND).contains(eventDto.getCdEventStatus()) ){
				fbssReferralsDto.setAddrCity(entity.getAddrCity());
				fbssReferralsDto.setAddrSt1(entity.getAddrStLn1());
				fbssReferralsDto.setAddrSt2(entity.getAddrStLn2());
				fbssReferralsDto.setAddrZip(entity.getAddrZip());
				fbssReferralsDto.setReferredLang(entity.getTxtPrfrdLang());
				indicatorsDto = getHouseHoldDetailsBySA(entity.getIdCpsSA());
			}else {
				indicatorsDto = getHouseHoldDetails(formReferrals.getIdStage());
				Long personIdVal = entity.getIdPerson();
				List<QuickFindPersonDto> quickFindPersonLst = indicatorsDto.stream()                
		                .filter(personId -> personIdVal.equals(personId.getIdPerson()))
		                .collect(Collectors.toList());
				if(null != quickFindPersonLst && quickFindPersonLst.size() > 0){
						fbssReferralsDto.setAddrCity(quickFindPersonLst.get(0).getCurrentCity());
						fbssReferralsDto.setAddrSt1(quickFindPersonLst.get(0).getCurrentAddr());
						fbssReferralsDto.setAddrSt2(quickFindPersonLst.get(0).getCurrentApt());
						fbssReferralsDto.setAddrZip(quickFindPersonLst.get(0).getCurrentZip());
						fbssReferralsDto.setReferredLang(quickFindPersonLst.get(0).getLang());
				}
			}
			if(null != capsCaseDto){
				fbssReferralsDto.setNmCase(capsCaseDto.getNmCase());
			}
			if(null != eventDto){
				fbssReferralsDto.setIdCase(eventDto.getIdCase());
			}
			fbssReferralsDto.setBarriersFamily(entity.getIndFmlyBarriers());
			fbssReferralsDto.setCaseWorker(entity.getNmCswrkr());
			fbssReferralsDto.setCaseWorkerPhn(entity.getNbrCswrkrPhone());
			fbssReferralsDto.setChildPcsp(entity.getIndChildPcsp());
			fbssReferralsDto.setContactFamily(entity.getTxtCntctFmly());
			fbssReferralsDto.setCourtOrder(entity.getIndCourtOrdr());
			fbssReferralsDto.setDtCourtHearing(entity.getDtCourtHearng());
			fbssReferralsDto.setDtCreated(entity.getDtCreated());
			fbssReferralsDto.setDtReferred(entity.getDtRefrdToFbss());
			fbssReferralsDto.setDtUpdatedOn(entity.getDtLastUpdate());
			fbssReferralsDto.setFamilyAccept(entity.getIndFmlyAccptsFbss());
			fbssReferralsDto.setIdCreatedPerson(entity.getIdCreatedPerson());
			fbssReferralsDto.setIdFbssReferrals(entity.getIdFbssReferrals());
			fbssReferralsDto.setIdLastUpdatePerson(entity.getIdLastUpdatePerson());
			fbssReferralsDto.setNonResident(entity.getIndAbsntPrnt());
			fbssReferralsDto.setPendingLeAction(entity.getIndPndngLeActns());
			fbssReferralsDto.setReferringCnty(entity.getCdRfrngCnty());
			fbssReferralsDto.setReferringUnit(entity.getCdRfrngUnit());
			fbssReferralsDto.setSpecialChildern(entity.getIndSpeclNeeds());
			fbssReferralsDto.setSupervisor(entity.getNmSuprvsr());
			fbssReferralsDto.setSupervisorPhn(entity.getNbrSupvsrPhone());
			fbssReferralsDto.setBarriersFamilyStr(entity.getTxtFmlyBarriers());
			fbssReferralsDto.setTxtComments(entity.getTxtAdtnlCmnts());
			fbssReferralsDto.setDangerIndicator(entity.getTxtDngrs());
			fbssReferralsDto.setFamilyAcceptStr(entity.getTxtFmlyAccptsFbss());
			fbssReferralsDto.setNonResidentStr(entity.getTxtAbsntPrnt());
			fbssReferralsDto.setParentProtective(entity.getTxtPrntPrtctvActns());
			fbssReferralsDto.setParentProtectiveTarget(entity.getTxtPrntDmnshdCapcty());
			fbssReferralsDto.setPendingLeActionStr(entity.getTxtPndngLeActns());
			fbssReferralsDto.setSpecialChildernStr(entity.getTxtSpeclNeeds());
			fbssReferralsDto.setWorkerSafetyStr(entity.getTxtWrkrSfty());
			fbssReferralsDto.setWorkerSafety(entity.getIndWrkrSfty());
			fbssReferralsDto.setIdPerson(entity.getIdPerson());
			fbssReferralsDto.setCourtReportUpld(entity.getCourtReportUpld());
			fbssReferralsDto.setFtmHeld(entity.getFtmHeld());
			formReferralsDto.setFormType(entity.getIdFormsReferrals().getCdFormType());
			formReferralsDto.setIdEvent(entity.getIdFormsReferrals().getIdEvent());
			formReferralsDto.setIdStage(entity.getIdFormsReferrals().getIdStage());
			formReferralsDto.setIdFormsReferrals(entity.getIdFormsReferrals().getIdFormsReferrals());
		}
		
		fbssLst.add(fbssReferralsDto);
		formReferralsDtoLst.add(formReferralsDto);
		formRes.setFbssReferralsList(fbssLst);
		formRes.setFormReferralsList(formReferralsDtoLst);
		formRes.setQuickFindPersonList(indicatorsDto);
		
		return formRes;
	}

	@Override
	public DiligentSearchRtrvRes getDiligentSearchHdrId(DiligentSearchRtrvReq diligentSearchRtrvReq) {
		DiligentSearchRtrvRes diligentSearchRtrvRes = new DiligentSearchRtrvRes();
		DlgntSrchHdrDto dlgntSrchHdrDto = new DlgntSrchHdrDto();
		List<DlgntSrchDtlDto> dlgntSrchDtlDtoList = new ArrayList<>();
		List<DlgntSrchChildDtlDto> dlgntSrchChildDtlDtoList = new ArrayList<>();
		List<DlgntSrchChildDtlDto> dlgntSrchChildDtlList = new ArrayList<>();
		dlgntSrchHdrDto = formReferralsDao.getDiligentSearchId(diligentSearchRtrvReq.getIdFormsReferrals());
		diligentSearchRtrvRes.setDlgntSrchHdrDto(dlgntSrchHdrDto);
		return  diligentSearchRtrvRes;
	}

}
