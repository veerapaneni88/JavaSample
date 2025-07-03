package us.tx.state.dfps.service.casepackage.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.casemergesplit.dto.CaseMergeSplitSaveDto;
import us.tx.state.dfps.common.domain.Workload;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.LEAgencySearchDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefListDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.dto.ServAuthRetrieveDto;
import us.tx.state.dfps.service.casepackage.dto.ToDoDetailDto;
import us.tx.state.dfps.service.casepackage.service.CapsCaseService;
import us.tx.state.dfps.service.casepackage.service.CaseFileManagementService;
import us.tx.state.dfps.service.casepackage.service.CaseHistoryService;
import us.tx.state.dfps.service.casepackage.service.CaseMergeService;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.casepackage.service.LEAgencySearchService;
import us.tx.state.dfps.service.casepackage.service.LawEnfrcmntNotifctnService;
import us.tx.state.dfps.service.casepackage.service.RecordsRetentionService;
import us.tx.state.dfps.service.casepackage.service.ServAuthRetrieveService;
import us.tx.state.dfps.service.casepackage.service.SpecialHandlingAudService;
import us.tx.state.dfps.service.casepackage.service.SpecialHandlingService;
import us.tx.state.dfps.service.common.request.ActedUponAssgnReq;
import us.tx.state.dfps.service.common.request.CFMgmntReq;
import us.tx.state.dfps.service.common.request.CaseFileManagementReq;
import us.tx.state.dfps.service.common.request.CaseFileMgmtReq;
import us.tx.state.dfps.service.common.request.CaseFileMgtReq;
import us.tx.state.dfps.service.common.request.CaseMergeSplitSaveReq;
import us.tx.state.dfps.service.common.request.CaseMergeSplitValidateReq;
import us.tx.state.dfps.service.common.request.CaseMergeSplitValidateRes;
import us.tx.state.dfps.service.common.request.CaseMergeValidationReq;
import us.tx.state.dfps.service.common.request.CasePersonListReq;
import us.tx.state.dfps.service.common.request.CaseSearchInputReq;
import us.tx.state.dfps.service.common.request.CaseSummaryReq;
import us.tx.state.dfps.service.common.request.ClosedCaseImageAccessReq;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CpsInvCnclsnReq;
import us.tx.state.dfps.service.common.request.GetSSCCUserAndRefReq;
import us.tx.state.dfps.service.common.request.HasSSCCReferralReq;
import us.tx.state.dfps.service.common.request.LEAgencySearchReq;
import us.tx.state.dfps.service.common.request.LawEnfrcmntNotifctnReq;
import us.tx.state.dfps.service.common.request.NytdReq;
import us.tx.state.dfps.service.common.request.RecordRetentionDataSaveReq;
import us.tx.state.dfps.service.common.request.RecordsRetentionReq;
import us.tx.state.dfps.service.common.request.RecordsRetentionRtrvReq;
import us.tx.state.dfps.service.common.request.RetrvCaseHistoryReq;
import us.tx.state.dfps.service.common.request.RetrvCaseHistoryRes;
import us.tx.state.dfps.service.common.request.RetrvCaseMergeReq;
import us.tx.state.dfps.service.common.request.SSCCRefListKeyReq;
import us.tx.state.dfps.service.common.request.SSCCRefListReq;
import us.tx.state.dfps.service.common.request.SSCCReferralReq;
import us.tx.state.dfps.service.common.request.ServAuthRetrieveReq;
import us.tx.state.dfps.service.common.request.SpecialHandlingAudReq;
import us.tx.state.dfps.service.common.request.SpecialHandlingReq;
import us.tx.state.dfps.service.common.request.StageClosureRtrvReq;
import us.tx.state.dfps.service.common.request.WorkingNarrativeReq;
import us.tx.state.dfps.service.common.request.WorkloadRequest;
import us.tx.state.dfps.service.common.response.ActedUponAssgnRes;
import us.tx.state.dfps.service.common.response.CFMgmntRes;
import us.tx.state.dfps.service.common.response.CaseFileMgmtSaveRes;
import us.tx.state.dfps.service.common.response.CaseFileMgtRes;
import us.tx.state.dfps.service.common.response.CaseMergeUpdateRes;
import us.tx.state.dfps.service.common.response.CaseMergeValidationRes;
import us.tx.state.dfps.service.common.response.CaseSearchRes;
import us.tx.state.dfps.service.common.response.CaseSummaryRes;
import us.tx.state.dfps.service.common.response.ClosedCaseImageAccessRes;
import us.tx.state.dfps.service.common.response.CommonDateRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.CpsInvCnclsnRes;
import us.tx.state.dfps.service.common.response.GetSSCCUserAndRefRes;
import us.tx.state.dfps.service.common.response.HasSSCCReferralRes;
import us.tx.state.dfps.service.common.response.IDListRes;
import us.tx.state.dfps.service.common.response.LEAgencySearchRes;
import us.tx.state.dfps.service.common.response.LawEnfrcmntNotifctnRes;
import us.tx.state.dfps.service.common.response.ListObjectRes;
import us.tx.state.dfps.service.common.response.ListSelectStageRes;
import us.tx.state.dfps.service.common.response.NotificationFileRes;
import us.tx.state.dfps.service.common.response.NytdRes;
import us.tx.state.dfps.service.common.response.RecordRetentionSaveRes;
import us.tx.state.dfps.service.common.response.RecordsRetentionRtrvRes;
import us.tx.state.dfps.service.common.response.RetrvCaseMergeRes;
import us.tx.state.dfps.service.common.response.SSCCRefListKeyRes;
import us.tx.state.dfps.service.common.response.SSCCReferralRes;
import us.tx.state.dfps.service.common.response.SaveUnitRes;
import us.tx.state.dfps.service.common.response.ServAuthRetrieveRes;
import us.tx.state.dfps.service.common.response.SpecialHandlingAudRes;
import us.tx.state.dfps.service.common.response.SpecialHandlingRes;
import us.tx.state.dfps.service.common.response.StageRes;
import us.tx.state.dfps.service.common.response.WorkingNarrativeRes;
import us.tx.state.dfps.service.common.response.StageInfoRes;
import us.tx.state.dfps.service.common.response.WorkloadResponse;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.common.dto.LawEnforcementAgencyInfo;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.intake.dto.IncomingDetailDto;
import us.tx.state.dfps.service.legalstatus.service.LegalStatusService;
import us.tx.state.dfps.service.person.service.PersonListService;
import us.tx.state.dfps.service.sscc.service.SSCCRefService;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.ReopenStageDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.service.WorkloadService;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN37S
 * Class Description:CasePackage controller class will have all operation which
 * are related to case and relevant page to case. Apr 26, 2017 - 3:27:56 PM4
 * 07/05/2021 kurmav Artifact artf190991 : Hide PCSP tab for FAD and KIN stages
 *
 */
@RestController
@Api(tags = { "referral", "identity" })
@RequestMapping("/case")
public class CasePackageController {

	@Autowired
	CaseMergeService caseMergeService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CaseSummaryService caseSummaryService;

	@Autowired
	ServAuthRetrieveService servAuthRetrieveService;

	@Autowired
	SpecialHandlingService specialHandlingService;

	@Autowired
	CaseFileManagementService caseFileManagementService;

	@Autowired
	CapsCaseService capsCaseService;

	@Autowired
	SSCCRefService sSCCRefService;

	@Autowired
	RecordsRetentionService recordsRetentionService;

	@Autowired
	EventService eventService;

	@Autowired
	CaseHistoryService caseHistoryService;

	@Autowired
	CommonService commonService;

	@Autowired
	LEAgencySearchService leAgencySearchService;

	@Autowired
	LawEnfrcmntNotifctnService lawEnfrcmntNotifctnService;

	@Autowired
	PersonListService personListService;

	@Autowired
	SpecialHandlingAudService specialHandlingAudService;

	@Autowired
	LegalStatusService legalStatusService;

	@Autowired
	WorkloadService workloadService;

	private static final Logger log = Logger.getLogger(CasePackageController.class);
	public static final String LAW_ENFORCEMENT_NOTIFICATION = "leNotification";
	public static final String LAW_ENFORCEMENT_INFORMATION = "leInformation";

	/**
	 * This service will retrieve all past merges for a given case,as well as
	 * case specific information for the given case. If the necessary security
	 * requirements have not already been met, the service will also check if
	 * the logged in user is either assigned to the given case as a primary
	 * worker or is in the unit hiearchy of a primary worker for the case. This
	 * service method is to get the details required for case merge screen
	 * Method Description: legacy service name - CCFC39S
	 * 
	 * @param caseMergeRetvIn
	 * @return CaseMergeRetrvOut
	 */
	@RequestMapping(value = "/getcasemerge", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RetrvCaseMergeRes getCaseMerges(@RequestBody RetrvCaseMergeReq retrvCaseMergeReq) {
		if (TypeConvUtil.isNullOrEmpty(retrvCaseMergeReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(retrvCaseMergeReq.getIndMergeAccess())) {
			throw new InvalidRequestException(
					messageSource.getMessage("casemerge.csysindmergeaccess.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(retrvCaseMergeReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		RetrvCaseMergeRes retrvCaseMergeRes = caseMergeService.getCaseMerges(retrvCaseMergeReq);
		log.info("TransactionId :" + retrvCaseMergeReq.getTransactionId());
		return retrvCaseMergeRes;
	}

	/**
	 * 
	 * Method Description: This service is designed to retrieve case information
	 * as well as a list of stages associated with that case. It receives ID
	 * CASE. It returns data from the CASE, PERSON, PERSON PHONE, STAGE, STAGE
	 * PERSON LINK tables. Service Name: CCMN37S
	 * 
	 * @param rtvCaseSummaryReq
	 * @return rtvCaseSummaryRes
	 */
	// CCMN37S
	@RequestMapping(value = "/casesummary", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CaseSummaryRes getCaseSummary(@RequestBody CaseSummaryReq rtvCaseSummaryReq) {
		CaseSummaryRes rtvCaseSummaryRes = new CaseSummaryRes();
		if (TypeConvUtil.isNullOrEmpty(rtvCaseSummaryReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		try {
			rtvCaseSummaryRes = caseSummaryService.getCaseSummary(rtvCaseSummaryReq);
		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("casepackage.caseSummary.data", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		}
		log.info("TransactionId :" + rtvCaseSummaryReq.getTransactionId());
		return rtvCaseSummaryRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieval the Service Authorization
	 * APS Detail window based on the input request. Service Name : CCON24S
	 * 
	 * @param servAuthRetrieveReq
	 * @return ServAuthRetrieveRes
	 * @,InvalidRequestException,DataNotFoundException
	 */
	@RequestMapping(value = "/getServAuthDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ServAuthRetrieveRes getAuthDtls(@RequestBody ServAuthRetrieveReq servAuthRetrieveReq) {
		if (null == servAuthRetrieveReq.getUlIdSvcAuth()) {
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.getAuthDtls.serviceAuthID", null, Locale.US));
		}
		List<ServAuthRetrieveDto> authList = null;
		ServAuthRetrieveRes servAuthRetrieveRes = new ServAuthRetrieveRes();
		authList = servAuthRetrieveService.getAuthDetails(servAuthRetrieveReq);
		servAuthRetrieveRes.setServAuthRetrieveDtoList(authList);
		servAuthRetrieveRes.setTransactionId(servAuthRetrieveReq.getTransactionId());
		log.info("TransactionId :" + servAuthRetrieveReq.getTransactionId());
		return servAuthRetrieveRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve either all or only the
	 * valid rows for a given person id depending upon whether the invalid SYS
	 * IND VALID ONLY indicator is set. Service Name: CCMN81S
	 * 
	 * @param specialHandlingReq
	 * @return specialHandlingRes
	 */
	@RequestMapping(value = "/getSpclHndlngDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SpecialHandlingRes getSpclHndlng(@RequestBody SpecialHandlingReq specialHandlingReq) {
		if (TypeConvUtil.isNullOrEmpty(specialHandlingReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		SpecialHandlingRes specialHandlingRes = new SpecialHandlingRes();
		CapsCaseDto caseList = specialHandlingService.getSpclHndlng(specialHandlingReq);
		specialHandlingRes.setCapsCaseDto(caseList);
		specialHandlingRes.setTransactionId(specialHandlingReq.getTransactionId());
		log.info("TransactionId :" + specialHandlingReq.getTransactionId());
		return specialHandlingRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve all columns for an Id Case
	 * from the CASE FILE MANAGEMENT table. There will be one row for a
	 * specified Id Case. It will retrieve a full row from both the OFFICE and
	 * UNIT tables with the ID OFFICE and ID UNIT respectively. The service will
	 * also retrieve a full row from the CAPS CASE table to get the closure date
	 * for the Case. Finally, it will check to see if the person who entered the
	 * window is the primary worker. Service Name: CCFC21S
	 *
	 * @param caseFileMgtReq
	 * @return caseFileMgtRes
	 */
	@RequestMapping(value = "/getCaseFileMgt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CaseFileMgtRes getCaseFileDtls(@RequestBody CaseFileMgtReq caseFileMgtReq) {
		if (TypeConvUtil.isNullOrEmpty(caseFileMgtReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(caseFileMgtReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		CaseFileMgtRes caseFileMgtRes = new CaseFileMgtRes();
		CaseFileManagementDto caseFileList = caseFileManagementService.getCaseFileManagementRtrv(caseFileMgtReq);
		caseFileMgtRes.setCaseFileMgtDto(caseFileList);
		caseFileMgtRes.setTransactionId(caseFileMgtReq.getTransactionId());
		log.info("TransactionId :" + caseFileMgtReq.getTransactionId());
		return caseFileMgtRes;
	}

	/**
	 * 
	 * Method Name: searchCase Method Description: searchCase Service Name -
	 * CCMN20S
	 * 
	 * @param caseSearchReq
	 * @return
	 */
	@RequestMapping(value = "/searchList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CaseSearchRes searchCase(@RequestBody CaseSearchInputReq caseSearchReq) {
		log.info("TransactionId :" + caseSearchReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(caseSearchReq)) {
			throw new InvalidRequestException(
					messageSource.getMessage("searchCase.caseSearchReq.mandatory", null, Locale.US));
		}
		return capsCaseService.caseSearch(caseSearchReq);
	}

	/**
	 * Get the List of Nytd Youth History records.
	 * 
	 * Nytd Service for case summary
	 * 
	 * @param nytdReq
	 * @return NytdRes
	 */
	@RequestMapping(value = "/retrieveNytdYouthHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public NytdRes retrieveNytdYouthHistory(@RequestBody NytdReq nytdReq) {
		log.info("TransactionId :" + nytdReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(nytdReq.getNytdYouthID())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return capsCaseService.retrieveNytdYouthHistory(nytdReq);
	}

	/**
	 * getAFCPendingStatus: The getAFCPendingStatus method was added as a part
	 * of SIR 23966 (MPS Phase III Lockdown Changes) to determine if any AFC
	 * stage approval event is currently in PROC status. This is necessary
	 * because of the possibility of multiple stage approval submissions in AFC.
	 * 
	 * Service Name - NA (Util Method getAFCPendingStatus)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 */
	@RequestMapping(value = "/getAFCPendingStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getAFCPendingStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getAFCPendingStatus(commonHelperReq);
	}

	/**
	 * isSDMInvRiskAssmt : Method to determine if Stage is Valid for SDM Risk
	 * Assessment.
	 * 
	 * Service Name - NA (Util Method isSDMInvRiskAssmt)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 */
	@RequestMapping(value = "/isSDMInvRiskAssmt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes isSDMInvRiskAssmt(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.isSDMInvRiskAssmt(commonHelperReq);
	}

	/**
	 * getCaseCheckoutStatus: This function returns true if the case is
	 * currently checked out Checked out (OT or AI) for the given Stage Id.
	 * 
	 * Service Name - NA (Util Method getCaseCheckoutStatus)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 */
	@RequestMapping(value = "/getCaseCheckoutStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getCaseCheckoutStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getCaseCheckoutStatus(commonHelperReq);
	}

	/**
	 * getPriorStage: Returns details for the stage prior to the given stage as
	 * indicated by the STAGE_LINK table.
	 * 
	 * Service Name - NA (Util Method getPriorStage)
	 * 
	 * @paramcommonHelperReq
	 * @returnSelectStageDto
	 */
	@RequestMapping(value = "/getPriorStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SelectStageDto getPriorStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getPriorStage(commonHelperReq);
	}

	/**
	 * getStage: Returns information about a stage.
	 * 
	 * Service Name - NA (Util Method getCaseCheckoutStatus)
	 * 
	 * @paramcommonHelperReq
	 * @returnSelectStageDto
	 */
	@ApiOperation(value = "Get Stage Details", tags = { "referral" })
	@RequestMapping(value = "/getStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SelectStageDto getStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getStage(commonHelperReq);
	}

	/**
	 * Method Name: ssccRefListKey Method Description: Invoke the SSCC Referral
	 * EJB to process logic to display the SSCC Referral section and the Add
	 * button on the Case Summary page.
	 * 
	 * @param sSCCRefListKeyReq
	 * @return SSCCRefListKeyRes
	 */
	@ApiOperation(value = "Get referral list list", tags = { "referral" })
	@RequestMapping(value = "/ssccRefListKey", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCRefListKeyRes ssccRefListKey(@RequestBody SSCCRefListKeyReq sSCCRefListKeyReq) {
		log.info("TransactionId :" + sSCCRefListKeyReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(sSCCRefListKeyReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(sSCCRefListKeyReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return sSCCRefService.getSSCCRefListKey(sSCCRefListKeyReq);
	}

	/**
	 * Method Name:hasSSCCReferralService Method Description: Returns true if
	 * there is at least one SSCC Referral for given stage
	 * 
	 * @param hasSSCCReferralReq
	 * @return HasSSCCReferralRes
	 */
	@RequestMapping(value = "/hasSSCCReferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public HasSSCCReferralRes hasSSCCReferralService(@RequestBody HasSSCCReferralReq hasSSCCReferralReq) {
		log.info("TransactionId :" + hasSSCCReferralReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(hasSSCCReferralReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return sSCCRefService.hasSSCCReferral(hasSSCCReferralReq);
	}

	/**
	 * Method Description: This method returns if all questions have been
	 * answered. Service Name: CpsInvCnclsn
	 * 
	 * @param cpsInvCnclsnReq
	 * @return CpsInvCnclsnRes
	 */
	@RequestMapping(value = "/cpsInvCnclsn", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CpsInvCnclsnRes getCpsInvCnclsn(@RequestBody CpsInvCnclsnReq cpsInvCnclsnReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getQuesAnsrd(cpsInvCnclsnReq);
	}

	/**
	 * This service will retrieve all columns for an ID Case from the RECORDS
	 * RETENTION table. There will be one row for a specified ID Case.
	 * Additionally, it will retrieve a full row from the CAPS CASE table to get
	 * the closure date for case. It calls DAMS: CCMNC5D - CASE SMP and CSES56D
	 * - REC RETN RTRV.
	 * 
	 * Service Name - CCFC19S
	 * 
	 * @param recordsRetentionRtrvReq
	 * @return RecordsRetentionRtrvRes
	 */
	@RequestMapping(value = "/recordsRetentionRtrv", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public RecordsRetentionRtrvRes recordsRetentionRtrvService(
			@RequestBody RecordsRetentionRtrvReq recordsRetentionRtrvReq) {
		log.info("TransactionId :" + recordsRetentionRtrvReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(recordsRetentionRtrvReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return recordsRetentionService.recordsRetentionRtrv(recordsRetentionRtrvReq);
	}

	/**
	 * Method Description: This method inserts closed case image access audit
	 * data into CASE_IMAGE_API_AUDIT table. Service Name: ClosedCaseImageAccess
	 * 
	 * @param closedCaseImageAccessReq
	 * @return ClosedCaseImageAccessRes
	 */
	@RequestMapping(value = "/closedCaseImageAccess", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ClosedCaseImageAccessRes insertClosedCaseAuditData(
			@RequestBody ClosedCaseImageAccessReq closedCaseImageAccessReq) {
		if (TypeConvUtil.isNullOrEmpty(closedCaseImageAccessReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(closedCaseImageAccessReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return caseSummaryService.insertApiAuditRecord(closedCaseImageAccessReq);
	}

	/**
	 * Method Description: Method to determine if user has access to modify PCSP
	 * page.
	 * 
	 * Service Name - NA (Util Method hasPCSPAccess)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 */
	@RequestMapping(value = "/hasPCSPAccess", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes hasPCSPAccess(@RequestBody CommonHelperReq commonHelperReq) {
		return caseSummaryService.hasPCSPAccess(commonHelperReq);
	}

	/**
	 * Method Description: Method to look at the case to determine if the given
	 * user has access to any open stage..
	 * 
	 * Service Name - NA (Util Method hasStageAccessToAnyOpenStage)
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/hasStageAccessToAnyOpenStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes hasStageAccessToAnyOpenStage(@RequestBody CommonHelperReq commonHelperReq) {
		return caseSummaryService.hasStageAccessToAnyStage(commonHelperReq);
	}

	/**
	 * Method Description: Method to look at the case to determine if the given
	 * user has access to any closed stage..
	 * 
	 * Service Name - NA (Util Method hasStageAccessToAnyClosedStage)
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/hasStageAccessToAnyClosedStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes hasStageAccessToAnyClosedStage(@RequestBody CommonHelperReq commonHelperReq) {
		return caseSummaryService.hasStageAccessToAnyStage(commonHelperReq);
	}

	/**
	 * getCaseCheckoutPerson: This function retuns the Primary (PR) or Seconday
	 * (SE) Worker that Checked out (OT or AI) the given Stage Id.
	 * 
	 * Service Name - NA (Util Method getCaseCheckoutPerson)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 */
	@RequestMapping(value = "/getCaseCheckoutPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getCaseCheckoutPerson(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getCaseCheckoutPerson(commonHelperReq);
	}

	/**
	 * getApprovalToDo: Returns a the event and todo id's for an approval given
	 * an event from the stage.
	 * 
	 * Service Name - NA (Util Method getApprovalToDo)
	 * 
	 * @paramcommonHelperReq
	 * @returnToDoDetailsDto
	 * 
	 */
	@RequestMapping(value = "/getApprovalToDo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ToDoDetailDto getApprovalToDo(@RequestBody CommonHelperReq commonHelperReq) {
		ToDoDetailDto toDoDetailDto = new ToDoDetailDto();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		toDoDetailDto = caseSummaryService.getApprovalToDo(commonHelperReq);
		if (TypeConvUtil.isNullOrEmpty(toDoDetailDto)) {
			toDoDetailDto = new ToDoDetailDto();
		}
		return toDoDetailDto;
	}

	/**
	 * getToDo: Returns the To Do Details for the passed To Do ID.
	 * 
	 * Service Name - NA (Util Method getApprovalToDo)
	 * 
	 * @paramcommonHelperReq
	 * @returnToDoDetailsDto
	 * 
	 */
	@RequestMapping(value = "/getToDo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ToDoDetailDto getToDo(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdToDo())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approvalstatus.todoid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getToDo(commonHelperReq);
	}

	/**
	 * getPendingEventTasks: Returns a set of tasks associated with events not
	 * in COMP or APRV status for a particular stage.
	 * 
	 * Service Name - NA (Util Method getApprovalToDo)
	 * 
	 * @paramcommonHelperReq
	 * @returnListObjectRes
	 * 
	 */
	@RequestMapping(value = "/getPendingEventTasks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ListObjectRes getPendingEventTasks(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getPendingEventTasks(commonHelperReq);
	}

	/**
	 * getStageByTypeAndPriorStage: Returns details for the stage of the given
	 * type, if one exists, that originated from the given stage id. (USAGE:
	 * This method was written for SIR 16114 to find the FSU stage that most
	 * closely precedes the FRE stage with the given start date.)
	 * 
	 * Service Name - NA (Util Method getApprovalToDo)
	 * 
	 * @paramcommonHelperReq
	 * @returnSelectStageDto
	 * 
	 */
	@RequestMapping(value = "/getStageByTypeAndPriorStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SelectStageDto getStageByTypeAndPriorStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageType.mandatory", null, Locale.US));
		}
		return caseSummaryService.getStageByTypeAndPriorStage(commonHelperReq);
	}

	/**
	 * getEventByStageAndEventType: Returns the most recent event id, event
	 * status, task code and timestamp for the given stage and event type.
	 * 
	 * Service Name - NA (Util Method getApprovalToDo)
	 * 
	 * @paramcommonHelperReq
	 * @returnEventDto
	 * 
	 */
	@RequestMapping(value = "/getEventByStageAndEventType", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public EventDto getEventByStageAndEventType(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getEventType())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventType.mandatory", null, Locale.US));
		}
		return caseSummaryService.getEventByStageAndEventType(commonHelperReq);
	}

	/**
	 * hasAccessToCase: Looks at the case to determine if the given user has
	 * access to any stage. Use this version of the method if you want to test
	 * access for the current user. The following items are checked: primary
	 * worker assigned to stage, one of the four secondary workers assigned to
	 * the stage, the supervisor of any of the above, the designee of any of the
	 * above supervisors
	 * 
	 * Service Name - NA (Util Method getApprovalToDo)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 * 
	 */
	@RequestMapping(value = "/hasAccessToCase", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes hasAccessToCase(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return caseSummaryService.hasAccessToCase(commonHelperReq);
	}

	/**
	 * getAllStages: Returns a list of All stages for a given case; they are
	 * sorted by stage code, then stage name.
	 * 
	 * Service Name - NA (Util Method getAllStages)
	 * 
	 * @paramcommonHelperReq
	 * @returnListObjectRes
	 * 
	 */
	@RequestMapping(value = "/getAllStages", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ListSelectStageRes getAllStages(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		ListSelectStageRes listSelectStageRes = new ListSelectStageRes();
		listSelectStageRes.setSelectStageDto(
				new ArrayList<SelectStageDto>(eventService.getOpenStages(commonHelperReq.getIdCase(), null)));
		return listSelectStageRes;
	}

	/**
	 * Method Description: This method will retrieve locating information.
	 * Service Name:CFMgmntList
	 * 
	 * @param caseFileMgtReq
	 * @return CaseFileMgtRes @
	 */
	@RequestMapping(value = "/cFMgmtList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CaseFileMgtRes getCFMgmtList(@RequestBody CaseFileMgtReq caseFileMgtReq) {
		if (TypeConvUtil.isNullOrEmpty(caseFileMgtReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseFileManagementService.getCFMList(caseFileMgtReq);
	}

	/**
	 * Method-Description: This method returns a date value when a stage is
	 * closed.
	 * 
	 * Service Name - NA (Util Method dtStageClosed)
	 * 
	 * @paramcommonHelperReq(Stage id)
	 * @returnDate
	 */
	@RequestMapping(value = "/dtStageClosed", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes dtStageClosed(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.dtStageClosed(commonHelperReq);
	}

	/**
	 * This service will add/update all columns for an Id Case from the RECORDS
	 * RETENTION table. It will call DAM:CAUD75D
	 * 
	 * @param RecordsRetentionReq
	 * @return @
	 */
	@RequestMapping(value = "/manageRecordsRetention", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SaveUnitRes manageRecordsRetention(@RequestBody RecordsRetentionReq recordsRetentionReq) {
		log.info("Functioncal code in RecordsRetentionReq :" + recordsRetentionReq.getReqFuncCd());
		if (StringUtils.isBlank(recordsRetentionReq.getCdRecRtnRetenType()))
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.manage.retention.type", null, Locale.US));
		if (StringUtils.isBlank(recordsRetentionReq.getTxtRecRtnDstryDtRsn()))
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.manage.retention.reasontxt", null, Locale.US));
		SaveUnitRes saveUnitRes = new SaveUnitRes();
		String msg = caseFileManagementService.manageRecordsRetention(recordsRetentionReq);
		saveUnitRes.setActionResult("Successfull" + msg);
		return saveUnitRes;
	}

	/**
	 * This service will save all columns for an IdCase to the CASE FILE
	 * MANAGEMENT table. There will be one row for a specified IdCase.
	 * Furthermore, it will check to see if the MailCode/Region/Program
	 * specified exists as well as the Unit/Region/Program exists. Additionally,
	 * it will retrieve a full row from the CAPS CASE table to get the closure
	 * date for the Case.
	 */
	@RequestMapping(value = "/manageCaseFileManagement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SaveUnitRes manageCaseFileManagement(@RequestBody CaseFileManagementReq caseFileManagementReq) {
		log.info("Functioncal code in RecordsRetentionReq :" + caseFileManagementReq.getReqFuncCd());
		validateCriteria(caseFileManagementReq);
		SaveUnitRes saveUnitRes = new SaveUnitRes();
		String msg = caseFileManagementService.manageCaseFileManagement(caseFileManagementReq);
		saveUnitRes.setActionResult("Successfull" + msg);
		return saveUnitRes;
	}

	/**
	 * This service will save case merges and case splits to the database with
	 * the pending flag. The actual DB updates will be done in a batch process.
	 */
	@RequestMapping(value = "/manageCaseMergeSplit", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CaseMergeUpdateRes manageCaseMergeSplit(@RequestBody CaseMergeSplitSaveReq caseMergeSplitSaveReq) {
		if (TypeConvUtil.isNullOrEmpty(caseMergeSplitSaveReq)
				|| CollectionUtils.isEmpty(caseMergeSplitSaveReq.getCaseMergeSplitDetailList())) {
			throw new InvalidRequestException(messageSource.getMessage("person.nameHistory.data", null, Locale.US));
		}
		for (CaseMergeSplitSaveDto caseMergeSplitSaveDto : caseMergeSplitSaveReq.getCaseMergeSplitDetailList()) {
			if (TypeConvUtil.isNullOrEmpty(caseMergeSplitSaveDto.getIdCaseMergeFrom())) {
				throw new InvalidRequestException(
						messageSource.getMessage("casepackage.merge.casefrom", null, Locale.US));
			}
			if (TypeConvUtil.isNullOrEmpty(caseMergeSplitSaveDto.getIdCaseMergeTo())) {
				throw new InvalidRequestException(
						messageSource.getMessage("casepackage.merge.caseto", null, Locale.US));
			}
		}
		return caseMergeService.manageCaseMerge(caseMergeSplitSaveReq);
	}

	/**
	 * This Service will verify that the ID case passed to it is an existing
	 * case that has not previously been a Merge From case. The service will
	 * also verify that the ID case passed is not pending another merge. If the
	 * security requirement have not been met, the service will also verify that
	 * the logged in user is authorized to perform the merge. Finally, a series
	 * of edit checks will be run to see if cases are eligible to be merged.
	 * 
	 * @param caseMergeReq
	 * @return
	 */
	@RequestMapping(value = "/verify", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CaseMergeSplitValidateRes verifyCaseForMerge(
			@RequestBody CaseMergeSplitValidateReq caseMergeSplitValidateReq) {
		if (null == caseMergeSplitValidateReq.getIdCaseMergeFrom()) {
			throw new InvalidRequestException(messageSource.getMessage("stage.fromStage.mandatory", null, Locale.US));
		}
		if (null == caseMergeSplitValidateReq.getIdCaseMergeTo()) {
			throw new InvalidRequestException(messageSource.getMessage("stage.toStage.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + caseMergeSplitValidateReq.getTransactionId());
		return caseMergeService.verifyCaseMerge(caseMergeSplitValidateReq);
	}

	protected void validateCriteria(CaseFileManagementReq caseFileManagementReq) {
		if (null == caseFileManagementReq) {
			throw new InvalidRequestException(messageSource.getMessage("person.nameHistory.data", null, Locale.US));
		}
		if (StringUtils.isBlank(caseFileManagementReq.getCdCaseProgram())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.unitProgram.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(caseFileManagementReq.getCdOfficeRegion())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.unitRegion.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(caseFileManagementReq.getNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("unit.nbrUnit.mandatory", null, Locale.US));
		}
		if (StringUtils.isBlank(caseFileManagementReq.getAddrMailCode())) {
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.manage.addressmail", null, Locale.US));
		}
		// //if (caseFileManagementReq.getEFuncCode()== EFuncCode.FUNC_CD_UPDATE
		// || caseFileManagementReq.getEFuncCode() == EFuncCode.FUNC_CD_DELETE)
		// {
		// if(caseFileManagementReq.getUlIdCase() == null)
		// throw new
		// InvalidRequestException(messageSource.getMessage("employee.idPerson.mandatory",
		// null, Locale.US));
		// }
	}

	/**
	 * Method Name: caseMergeValidation Method Description: This method will
	 * Handles business functions required for SHIELD case merge validations
	 * 
	 * @param caseMergeValidationReq
	 * @return CaseMergeValidationRes
	 */
	@RequestMapping(value = "/caseMergeValidation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CaseMergeValidationRes caseMergeValidation(@RequestBody CaseMergeValidationReq caseMergeValidationReq) {
		if (null == caseMergeValidationReq.getFromStage()) {
			throw new InvalidRequestException(messageSource.getMessage("stage.fromStage.mandatory", null, Locale.US));
		}
		if (null == caseMergeValidationReq.getToStage()) {
			throw new InvalidRequestException(messageSource.getMessage("stage.toStage.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + caseMergeValidationReq.getTransactionId());
		return caseSummaryService.getCaseMrgValidation(caseMergeValidationReq);
	}

	/**
	 * Method-Description:This method returns all SUB stages in the case
	 * 
	 * @paramulIdCase
	 * @returnListOfStageId
	 */
	@RequestMapping(value = "/getAllSUBStages", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getAllSUBStages(@RequestBody CommonHelperReq commonHelperReq) {
		if (null == commonHelperReq.getIdCase()) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getAllSUBStages(commonHelperReq);
	}

	/**
	 * Method-Description:This method returns all FSU stages in the case
	 * 
	 * @param ulIdCase
	 * @return ListOfStageId @ InvalidRequestException
	 */
	@RequestMapping(value = "/getOpenFSUStages", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getOpenFSUStages(@RequestBody CommonHelperReq commonHelperReq) {
		if (null == commonHelperReq.getIdCase()) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getOpenFSUStages(commonHelperReq);
	}

	/**
	 * Method-Description:This method returns all FRE stages in the case
	 * 
	 * @param ulIdCase
	 * @return ListOfStageId @ InvalidRequestException
	 */
	@RequestMapping(value = "/getOpenFREStages", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getOpenFREStages(@RequestBody CommonHelperReq commonHelperReq) {
		if (null == commonHelperReq.getIdCase()) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getOpenFREStages(commonHelperReq);
	}

	/**
	 * Method-Description:This method checks if the passed CVS stage is
	 * currently checked out to the MPS Mobile device. The indicator for checked
	 * out cases is the CD_MOBILE_STATUS column on the Workload table.
	 * 
	 * @param ulIdStage
	 * @return Boolean -- true or False @ InvalidRequestException
	 */
	@RequestMapping(value = "/getCaseStageCheckoutStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getCaseStageCheckoutStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (null == commonHelperReq.getIdStage()) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getCaseStageCheckoutStatus(commonHelperReq);
	}

	/**
	 * Check if Case has an active SSCC Referral SSCC_REFERRAL.ID_CASE =
	 * <FROM:Case> AND SSCC_REFERRAL.CD_STATUS = 40 (Active)
	 *
	 * Method returns true if there is at least one active SSCC referral for
	 * this case
	 * 
	 * @param hasSSCCReferralReq
	 * @return HasSSCCReferralRes
	 */
	@RequestMapping(value = "/hasActiveSSCCReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public HasSSCCReferralRes hasActiveSSCCReferral(@RequestBody HasSSCCReferralReq hasSSCCReferralReq) {
		log.info("TransactionId :" + hasSSCCReferralReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(hasSSCCReferralReq.getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return sSCCRefService.hasActiveSSCCReferral(hasSSCCReferralReq);
	}

	/**
	 *
	 * Method returns information from Case table using idCase.
	 * 
	 * @param commonHelperReq
	 * @return CapsCaseDto
	 */
	@ApiOperation(value = "Get Case Details", tags = { "referral" })
	@RequestMapping(value = "/getCaseInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CapsCaseDto getCaseInfo(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return capsCaseService.getCaseInfo(commonHelperReq);
	}

	/**
	 * Method Description:Save Working Narrative is to save or update Narrative
	 * about case or stage for contact Service Name: Save Working Narrative
	 * 
	 * @param workingNarrativeReq
	 * @return workingNarrativeRes @
	 */
	// SaveWorkingNarrative
	@RequestMapping(value = "/saveWorkingNarrative", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public WorkingNarrativeRes saveWorkingNarrative(@RequestBody WorkingNarrativeReq workingNarrativeReq) {
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeReq.getWorkingNarrativeDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		WorkingNarrativeRes workingNarrativeRes = new WorkingNarrativeRes();
		workingNarrativeRes = capsCaseService.saveWorkingNarrative(workingNarrativeReq);
		if (null == workingNarrativeRes.getMessage()) {
			throw new DataNotFoundException(messageSource.getMessage("person.personlist.data", null, Locale.US));
		}
		log.info("TransactionId :" + workingNarrativeReq.getTransactionId());
		return workingNarrativeRes;
	}

	/**
	 * 
	 * Method Name: getCaseHistory Method Description: Retrives the Case History
	 * Details for the passed Case Id
	 * 
	 * @param retrvCaseHistoryReq
	 * @return RetrvCaseHistoryRes
	 */
	@RequestMapping(value = "/getCaseHistory", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RetrvCaseHistoryRes getCaseHistory(@RequestBody RetrvCaseHistoryReq retrvCaseHistoryReq) {
		if (TypeConvUtil.isNullOrEmpty(retrvCaseHistoryReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseHistoryService.getCaseHistory(retrvCaseHistoryReq);
	}

	/**
	 *
	 * This service returns information from Resource table using ResourceName,
	 * ResourceCounty or ResourceCity or ResourceId.
	 * Search with Resource Id is used in Law Enforcement Information section on Case Summary Page.
	 * @param LEAgencySearchReq
	 * @return LEAgencySearchRes
	 */
	@RequestMapping(value = "/getLEAgency", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LEAgencySearchRes getLEAgency(@RequestBody LEAgencySearchReq leAgencySearchReq) {
		if(LAW_ENFORCEMENT_NOTIFICATION.equalsIgnoreCase(leAgencySearchReq.getSearchType()) && (TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getNmResource())
					&& TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getCdResourceCnty())
					&& TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getAddrResourceCity()))) {
				throw new InvalidRequestException(
						messageSource.getMessage("casepackage.lEAgencySearch.mandatory", null, Locale.US));
		}else if(LAW_ENFORCEMENT_INFORMATION.equalsIgnoreCase(leAgencySearchReq.getSearchType()) && TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getSearchResourceName())
				&& TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getCdResourceCnty())
				&& TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getSearchIdResource())
				&& TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getAddrResourceCity())) {
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.lEAgencyInfoSearch.mandatory", null, Locale.US));
		}
		LEAgencySearchRes lEAgencySearchRes = leAgencySearchService.getLawEnforcementAgencyList(leAgencySearchReq);
		if (TypeConvUtil.isNullOrEmpty(lEAgencySearchRes.getElAgencySearchDtoList())) {
			throw new DataNotFoundException(messageSource.getMessage("case.workingNarrative.data", null, Locale.US));
		}
		return lEAgencySearchRes;
	}

	/**
	 * 
	 * Method Description: Get Working Narrative is to Fetch Narrative about
	 * case for contact Service Name: Fetch Working Narrative
	 * 
	 * @param workingNarrativeReq
	 * @return workingNarrativeRes
	 */
	@RequestMapping(value = "/getWorkingNarrative", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public WorkingNarrativeRes getWorkingNarrative(@RequestBody CommonHelperReq workingNarrativeReq) {
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		WorkingNarrativeRes workingNarrativeRes = new WorkingNarrativeRes();
		workingNarrativeRes = capsCaseService.getWorkingNarrative(workingNarrativeReq);
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeRes.getContactNarrativeDto())) {
			workingNarrativeRes = null;
		}
		log.info("TransactionId :" + workingNarrativeReq.getTransactionId());
		return workingNarrativeRes;
	}

	/**
	 * 
	 * Method Description: Fetch Narrative of old Contact and copy it to new
	 * Contact Service Name: createNewNarrativeUsing
	 * 
	 * @param workingNarrativeReq
	 * @return workingNarrativeRes
	 */
	@RequestMapping(value = "/contactDetailNarrativeNewUsing", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public WorkingNarrativeRes createNewNarrativeUsing(@RequestBody WorkingNarrativeReq workingNarrativeReq) {
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeReq.getIdEventOld())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeReq.getIdEventNew())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		WorkingNarrativeRes workingNarrativeRes = new WorkingNarrativeRes();
		workingNarrativeRes = capsCaseService.createNewNarrativeUsing(workingNarrativeReq);

		log.info("TransactionId :" + workingNarrativeReq.getTransactionId());
		return workingNarrativeRes;
	}

	@RequestMapping(value = "/getStageIdList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getStageIdList(@RequestBody CommonHelperReq workingNarrativeReq) {
		CommonHelperRes stageIdList = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(workingNarrativeReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		stageIdList = capsCaseService.getStageIdsList(workingNarrativeReq);

		return stageIdList;

	}

	/**
	 * This service will add a new notification with a valid Resource ID and
	 * Stage ID for the LAW_ENFRCMNT_NOTIFCTN table.
	 * 
	 * @param LawEnfrcmntNotifctnReq
	 * @return LawEnfrcmntNotifctnRes @
	 */
	@RequestMapping(value = "/saveLawEnfrcmntNotifctn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LawEnfrcmntNotifctnRes saveLawEnfrcmntNotifctn(@RequestBody LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq) {
		log.info("Functioncal code in lawEnfrcmntNotifctnReq :" + lawEnfrcmntNotifctnReq.getReqFuncCd());
		if (TypeConvUtil.isNullOrEmpty(lawEnfrcmntNotifctnReq.getLawEnfrcmntNotifctnDto().getIdResource())
				&& TypeConvUtil.isNullOrEmpty(lawEnfrcmntNotifctnReq.getLawEnfrcmntNotifctnDto().getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.saveLEN.mandatory", null, Locale.US));
		LawEnfrcmntNotifctnRes lawEnfrcmntNotifctnRes = new LawEnfrcmntNotifctnRes();
		lawEnfrcmntNotifctnRes = lawEnfrcmntNotifctnService.saveLawEnforcementNotifcn(lawEnfrcmntNotifctnReq);
		return lawEnfrcmntNotifctnRes;
	}

	/**
	 *
	 * This service returns information from LAW_ENFRCMNT_NOTIFCTN table using
	 * stage ID
	 * 
	 * @param LawEnfrcmntNotifctnReq
	 * @return LawEnfrcmntNotifctnRes
	 */
	@RequestMapping(value = "/getlawEnfrcmntNotifctnList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public LawEnfrcmntNotifctnRes getlawEnfrcmntNotifctnList(
			@RequestBody LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq) {
		log.info("Functioncal code in lawEnfrcmntNotifctnReq :" + lawEnfrcmntNotifctnReq.getReqFuncCd());
		if (TypeConvUtil.isNullOrEmpty(lawEnfrcmntNotifctnReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.getLENList.mandatory", null, Locale.US));
		LawEnfrcmntNotifctnRes lawEnfrcmntNotifctnRes = new LawEnfrcmntNotifctnRes();
		lawEnfrcmntNotifctnRes = lawEnfrcmntNotifctnService.getLawEnforcementNotifctnList(lawEnfrcmntNotifctnReq);
		if (lawEnfrcmntNotifctnRes.getLawEnfrcmntNotifctnDto().size() <= 0) {
			lawEnfrcmntNotifctnRes.setLawEnfrcmntNotifctnDto(null);
		}
		return lawEnfrcmntNotifctnRes;
	}

	/**
	 *
	 * This service returns resource email from Resource email table using
	 * idResource.
	 * 
	 * @param LEAgencySearchReq
	 * @return LEAgencySearchRes
	 */
	@RequestMapping(value = "/getResourceEmail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public LEAgencySearchRes getResourceEmail(@RequestBody LEAgencySearchReq leAgencySearchReq) {
		if (TypeConvUtil.isNullOrEmpty(leAgencySearchReq.getIdResource())) {
			throw new InvalidRequestException(
					messageSource.getMessage("casepackage.lEAgencySearch.mandatory", null, Locale.US));
		}
		List<String> emailList = leAgencySearchService.getResourceEmail(leAgencySearchReq);
		List<LEAgencySearchDto> lEAgencySearchDtoList = new ArrayList<>();
		LEAgencySearchDto lEAgencySearchDto = new LEAgencySearchDto();
		lEAgencySearchDto.setEmailList(emailList);
		lEAgencySearchDtoList.add(lEAgencySearchDto);
		LEAgencySearchRes lEAgencySearchRes = new LEAgencySearchRes();
		lEAgencySearchRes.setElAgencySearchDtoList(lEAgencySearchDtoList);
		return lEAgencySearchRes;
	}

	/**
	 * 
	 * Method Description: Returns a list of CaseUtility.Event objects for the
	 * given EventIdStruct_ARRAY; the events are passed in this object because
	 * this method is used in ToDoConversation with data from ToDoDetailDB
	 * Service Name: CaseUtility.Event objects for the given array of events
	 * 
	 * @param eventIdList
	 * @return workingNarrativeRes
	 */
	@RequestMapping(value = "/getEvents", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getEvents(@RequestBody CommonEventIdReq eventIdList) {
		if (TypeConvUtil.isNullOrEmpty(eventIdList.getIdEvents()) || 0 == eventIdList.getIdEvents().size()) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CommonHelperRes events = new CommonHelperRes();
		events = capsCaseService.getEvents(eventIdList);
		if (TypeConvUtil.isNullOrEmpty(events) || CollectionUtils.isEmpty(events.getEventDtls())) {
			events = new CommonHelperRes();
		}
		log.info("TransactionId :" + eventIdList.getTransactionId());
		return events;
	}

	/**
	 * getPriorStage: Returns details for the stage Later to the given stage as
	 * indicated by the STAGE_LINK table.
	 * 
	 * Service Name - NA (Util Method getPriorStage)
	 * 
	 * @paramcommonHelperReq
	 * @returnSelectStageDto
	 */
	@RequestMapping(value = "/getLaterStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SelectStageDto getLaterStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getLaterStage(commonHelperReq);
	}

	/**
	 * Method Description: This method retrieves the incoming detail for a stage
	 * id Service Name: Case Summary
	 * 
	 * @param commonHelperReq
	 * @return IncomingDetailDto
	 */
	@RequestMapping(value = "/getIncomingDetailByStageId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public IncomingDetailDto getIncomingDetailByStageId(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getIncomingDetailByStageId(commonHelperReq);
	}

	/**
	 * 
	 * Method Name: getOldestIntakeStageByCaseId Method Description: Retrieve
	 * the Intake Stage Details for the passed Case Id
	 * 
	 * @param commonHelperReq
	 * @return StageDto
	 */
	@RequestMapping(value = "/getOldestIntakeStageByCaseId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public StageDto getOldestIntakeStageByCaseId(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getOldestIntakeStageByCaseId(commonHelperReq);
	}

	/**
	 * 
	 * Method Name: updateStageCloseReason Method Description: Method used to
	 * close the case and stage for new close reason
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/updateStageCloseReason", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes updateStageCloseReason(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.updateStageCloseReason(commonHelperReq);
	}

	@RequestMapping(value = "/getPrimaryCaseworkerForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getPrimaryCaseworkerForStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return personListService.getPrimaryCaseworkerForStage(commonHelperReq.getIdStage());
	}

	@RequestMapping(value = "/getRegionByCounty", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes getRegionByCounty(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdCounty())) {
			throw new InvalidRequestException(messageSource.getMessage("common.countyCode.mandatory", null, Locale.US));
		}
		CommonStringRes response = new CommonStringRes();
		response.setCommonRes(caseSummaryService.getRegionByCounty(commonHelperReq.getCdCounty()));
		return response;
	}

	/**
	 * Tuxedo Service Name: CPER01S. Method Description: This method is used to
	 * retrieve Case Person List form by passing IdCase as input request
	 * 
	 * @param CasePersonListReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/getCasePersonListForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCasePersonListForm(@RequestBody CasePersonListReq casePersonListReq) {

		if (TypeConvUtil.isNullOrEmpty(casePersonListReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(capsCaseService.getCasePersonListForm(casePersonListReq)));
		log.info("TransactionId :" + casePersonListReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * @param ssccReq
	 * @return GetSSCCUserAndRefRes
	 */
	@RequestMapping(value = "/getSSCCUsrAndRefByStgAndRefType", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public GetSSCCUserAndRefRes GetSSCCUsrAndRefByStgAndRefType(@RequestBody GetSSCCUserAndRefReq ssccReq) {
		GetSSCCUserAndRefRes userAndRefList = new GetSSCCUserAndRefRes();

		userAndRefList.setisUserSSCC(sSCCRefService.isUserSSCC(ssccReq.getIdPerson()));
		List<SSCCRefDto> refList = null;

		refList = sSCCRefService.GetActiveSSCCReferral(ssccReq.getIdStage(),
				us.tx.state.dfps.service.common.ServiceConstants.PLACEMENT_REFERAAL);

		if (!ObjectUtils.isEmpty(refList)) {
			SSCCRefListDto referranceList = new SSCCRefListDto();
			referranceList.setSsccReferralForCaseList(refList);
			userAndRefList.setsSCCRefListDto(referranceList);
		}

		return userAndRefList;
	}

	/**
	 * Method Name: fetchSSCCReferralListForCase Method Description:Fetches the
	 * list of SSCC Referrals for the case and sets it into the
	 * SSCCRefListValueBean
	 * 
	 * @param ssccRefListReq
	 * @return SSCCRefListKeyRes
	 */
	@ApiOperation(value = "Get referral list", tags = { "referral" })
	@RequestMapping(value = "/fetchSSCCReferralListForCase", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCRefListKeyRes fetchSSCCReferralListForCase(@RequestBody SSCCRefListReq ssccRefListReq) {

		log.debug("TransactionId :" + ssccRefListReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(ssccRefListReq.getSsccRefListDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("case.ssccRefListDto.mandatory", null, Locale.US));
		}

		SSCCRefListKeyRes ssccRefListKeyRes = new SSCCRefListKeyRes();
		ssccRefListKeyRes
				.setsSCCRefListDto(sSCCRefService.fetchSSCCReferralListForCase(ssccRefListReq.getSsccRefListDto()));
		return ssccRefListKeyRes;
	}

	/**
	 * Method Name:caseFileMgmtSave Method Description:This service will save
	 * all columns for an IdCase to the CASE FILE MANAGEMENT table. There will
	 * be one row for a specified IdCase. Furthermore, it will check to see if
	 * the MailCode/Region/Program specified exists as well as the
	 * Unit/Region/Program exists. Additionally, it will retrieve a full row
	 * from the CAPS CASE table to get the closure date for the Case. TUX-
	 * CCFC22S
	 * 
	 * @param caseFileMgmtReq
	 * @return caseFileMgmtSaveRes
	 */
	@RequestMapping(value = "/caseFileMgmtSave", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CaseFileMgmtSaveRes caseFileMgmtSave(@RequestBody CaseFileMgmtReq caseFileMgmtReq) {
		log.debug("Entering method caseFileMgmtSave in CasePackageController");
		CaseFileMgmtSaveRes caseFileMgmtSaveRes = capsCaseService.caseFileMgmtSave(caseFileMgmtReq);
		log.debug("Exiting method caseFileMgmtSave in CasePackageController");
		return caseFileMgmtSaveRes;
	}

	/**
	 * Method Name: saveSpecialHandling Method Description:This window while
	 * perform AUD operations for the special handling window (CCMN73W). It will
	 * update part of one row in the CAPS CASE table. TUX: CCMN82S
	 * 
	 * @param specialHandlingAudReq
	 * @return specialHandlingAudRes
	 */
	@RequestMapping(value = "/saveSpecialHandling", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SpecialHandlingAudRes saveSpecialHandling(
			@RequestBody SpecialHandlingAudReq specialHandlingAudReq) {
		log.debug("Entering method saveSpecialHandling in CasePackageController");
		SpecialHandlingAudRes specialHandlingAudRes = specialHandlingAudService
				.specialHandlingAud(specialHandlingAudReq);
		log.debug("Exiting method saveSpecialHandling in CasePackageController");
		return specialHandlingAudRes;
	}

	/**
	 * Method Name: updateStagePersEmpNew Method Description:Updates the IND
	 * STAGE PERS EMP NEW field in the STAGE PERSON LINK table for a certain
	 * person and a certain STAGE.
	 *
	 * @param actedUponAssgnReq
	 * @return actedUponAssgnRes
	 */
	@RequestMapping(value = "/updateStagePersEmpNew", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ActedUponAssgnRes updateStagePersEmpNew(@RequestBody ActedUponAssgnReq actedUponAssgnReq) {
		log.debug("Entering method updateStagePersEmpNew in CasePackageController");
		ActedUponAssgnRes actedUponAssgnRes = capsCaseService.actedUponAssign(actedUponAssgnReq);
		log.debug("Exiting method updateStagePersEmpNew in CasePackageController");
		return actedUponAssgnRes;
	}

	/**
	 * 
	 * Method Name: getCFMgmntInfo Method Description:Retrieve the Locating
	 * Information
	 * 
	 * @param cfMgmntReq
	 * @return CFMgmntRes
	 */
	@RequestMapping(value = "/getCFMgmntInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CFMgmntRes getCFMgmntInfo(@RequestBody final CFMgmntReq cfMgmntReq) {

		if (TypeConvUtil.isNullOrEmpty(cfMgmntReq.getCfMgmntDto().getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		log.debug("Transaction Id :" + cfMgmntReq.getTransactionId());
		return capsCaseService.getCFMgmntInfo(cfMgmntReq);
	}

	/**
	 * Method Name: saveRecordsRetention Method Description:save
	 * RecordsRetention data in case summary TUX - CCFC20S
	 * 
	 * @param recordRetentionDataSaveReq
	 * @return RecordRetentionSaveRes
	 */
	@RequestMapping(value = "/saveRecordsRetention", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RecordRetentionSaveRes saveRecordsRetention(
			@RequestBody RecordRetentionDataSaveReq recordRetentionDataSaveReq) {
		log.debug("Entering method saveRecordsRetention in CasePackageController");
		RecordRetentionSaveRes recordRetentionSaveRes = capsCaseService.saveRecordRetention(recordRetentionDataSaveReq);
		log.debug("Exiting method saveRecordsRetention in CasePackageController");
		return recordRetentionSaveRes;
	}

	/**
	 * Method Name: saveIntakeNotesOrReopnInv Method Description:This Service is
	 * for updating intake notes and reopen INV reasons for CCL Program ADS
	 * Changes on Case summary page
	 * 
	 * @param stageClosureRtrvReq
	 * @return stageRes @
	 */
	@RequestMapping(value = "/saveIntakeNotesOrReopnInv", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageRes saveIntakeNotesOrReopnInv(@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method saveIntakeNotes in CasePackageController");
		StageRes stageRes = caseSummaryService.saveIntakeNotesOrReopnInv(stageClosureRtrvReq);
		log.debug("Exiting method saveIntakeNotes in CasePackageController");
		return stageRes;
	}

	/**
	 * MethodName: updateSSCCReferral MethodDescription: Method invokes the
	 * Helper update methods based on the SSCC Referral Type and Referral status
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@RequestMapping(value = "/updatessccreferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCReferralRes updateSSCCReferral(@RequestBody SSCCReferralReq ssccReferralReq){
		if (TypeConvUtil.isNullOrEmpty(ssccReferralReq.getSsccRefDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.SSCCReferral.DataNotFound", null, Locale.US));
		}
		sSCCRefService.updateSSCCReferralDetail(ssccReferralReq.getSsccRefDto(), ssccReferralReq.getUserId());
		log.info("TransactionId :" + ssccReferralReq.getTransactionId());
		return null;
	}

	/**
	 * Method Name: getIntakeNotesOrReopnInv Method Description:This Service is
	 * for fetching intake notes and reopen INV reasons for CCL Program ADS
	 * Changes on Case summary page
	 * 
	 * @param stageClosureRtrvReq
	 * @return stageRes @
	 */
	@RequestMapping(value = "/getIntakeNotesOrReopnInv", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageRes getIntakeNotesOrReopnInv(@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method getIntakeNotesAndReopnInv in CasePackageController");
		StageRes stageRes = caseSummaryService.getIntakeNotesOrReopnInv(stageClosureRtrvReq);
		log.debug("Exiting method getIntakeNotesAndReopnInv in CasePackageController");
		return stageRes;
	}

	/**
	 * Method Name: hasSSCCReferralForStage Method Description: Returns true if
	 * there is at least one SSCC Referral for given stage
	 * 
	 * @param hasSSCCReferralReq
	 * @return
	 */
	@RequestMapping(value = "/hasSSCCReferralForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  HasSSCCReferralRes hasSSCCReferralForStage(@RequestBody HasSSCCReferralReq hasSSCCReferralReq){

		if (TypeConvUtil.isNullOrEmpty(hasSSCCReferralReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(hasSSCCReferralReq.getCdRefType())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdRefType.mandatory", null, Locale.US));
		}
		HasSSCCReferralRes hasSSCCReferralRes = new HasSSCCReferralRes();
		hasSSCCReferralRes.setTransactionId(hasSSCCReferralReq.getTransactionId());
		hasSSCCReferralRes.setHasSSCCReferralForStage(sSCCRefService
				.hasSSCCReferralForStage(hasSSCCReferralReq.getIdStage(), hasSSCCReferralReq.getCdRefType()));
		return hasSSCCReferralRes;
	}

	/**
	 * Gets the legal status for child. UIDS 2.3.3.5 - Remove a child from home
	 * - Income and Expenditures
	 *
	 * @param commonHelperReq
	 *            the common helper req
	 * @return the legal status for child
	 */
	// UIDS 2.3.3.5 - Remove a child from home - Income and Expenditures
	@RequestMapping(value = "/checkLegalStatusForChild", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getLegalStatusForChild(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();

		if (ObjectUtils.isEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (legalStatusService.getLegalStatusForChild(commonHelperReq.getIdPerson(), commonHelperReq.getIdCase()) > 0)
			commonHelperRes.setResult(Boolean.TRUE);
		else
			commonHelperRes.setResult(Boolean.FALSE);

		return commonHelperRes;
	}

	/**
	 * Gets the recent legal region for child.
	 *
	 * @param commonHelperReq
	 *            the common helper req
	 * @return the recent legal region for child
	 */
	@RequestMapping(value = "/getLegalRegionForChild", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getRecentLegalRegionForChild(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();

		if (ObjectUtils.isEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		commonHelperRes
				.setChildLegalRegion(legalStatusService.getRecentLegalRegionForChild(commonHelperReq.getIdPerson()));

		return commonHelperRes;
	}

	/**
	 * Method Name: getIntIntakeDate Method Description: This method is used to
	 * get Int Intake Date
	 * 
	 * @param commonHelperReq
	 * @return CommonDateRes
	 */
	@RequestMapping(value = "/getIntIntakeDate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonDateRes getIntIntakeDate(@RequestBody CommonHelperReq commonHelperReq) {
		CommonDateRes commonDateRes = new CommonDateRes();

		if (ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		commonDateRes.setDate(caseSummaryService.getIntIntakeDate(commonHelperReq.getIdStage()));

		return commonDateRes;
	}
	
	/**
	 * 
	 * Method Name: getIntakeStageIdForSelectedStage Method Description: Method to fetch
	 * the Intake Stage Id for the passed stage ID
	 * 
	 * @param commonHelperReq
	 * @return StageDto
	 */
	@RequestMapping(value = "/getIntStgForselctdStg", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getIntakeStageIdForSelectedStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getIntakeStageIdForSelectedStage(commonHelperReq);
	}
	
	@RequestMapping(value = "/reopenStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes reopenStage(@RequestBody ReopenStageDto reopenStageDto) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		caseSummaryService.reopenStage(reopenStageDto);
		commonHelperRes.setResult(true);
		return commonHelperRes;
	}
	
	/**
	 * Method Name: getPrimaryChildIdByIdStage
	 * Method Description: Method to fetch Primary child Id(PersonID) for the passed stage ID
	 * 
	 * @param commonHelperReq
	 * @return commonHelperRes
	 * FCL Artifact ID: artf128756
	 */
	@RequestMapping(value = "/getPrimaryChildIdByIdStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getPrimaryChildIdByIdStage(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		commonHelperRes.setIdPrimaryChild(caseSummaryService.getPrimaryChildIdByIdStage(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	// add 	public List<CaseMergeUpdateDto> searchAllMergedCases(Long idCaseMerge) {
	@RequestMapping(value = "/getNeubusCaseList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getNeubusCaseList(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		commonHelperRes.setMergedCaseList(caseMergeService.getNeubusCaseList(commonHelperReq.getIdCase()));
		return commonHelperRes;
	}

	/**
	 * Method Name: getStageByIdCase
	 * Method Description: Method to fetch stage information by case ID
	 *
	 * @param commonHelperReq
	 * @return StageInfoRes
	 * Artifact ID: artf190991
	 */
	@ApiOperation(value = "Get stage details based on the caseId", tags = { "heightenedMonitoring" })
	@RequestMapping(value = "/getStageByIdCase", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public StageInfoRes getStageByIdCase(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return caseSummaryService.getStageByIdCase(commonHelperReq.getIdCase());
	}


	@RequestMapping(value = "/getPcspPlacementId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getPcspPlacementId(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventId.mandatory", null, Locale.US));
		}
		return caseSummaryService.getPcspPlacementId(commonHelperReq.getIdEvent());
	}


	/**
	 * Method Name: saveAgencyInfo
	 * Method Description: Method to save LE Involvement Agency Info for a case in INT, INV stages.
	 * @param agencyInfo
	 * @return
	 */
	@RequestMapping(value = "/saveAgencyInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes saveAgencyInfo(
			@RequestBody LawEnforcementAgencyInfo agencyInfo) {
		log.debug("Entering method saveSpecialHandling in CasePackageController");
		return capsCaseService.saveLEInvolvmentAgencyInfo(agencyInfo);
	}

	@RequestMapping(value = "/getworkloadHasLoginUserForStageAndCase", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public WorkloadResponse getWorkloadHasLoginUserForStageAndCase(@RequestBody WorkloadRequest workloadRequest) {
		if (TypeConvUtil.isNullOrEmpty(workloadRequest.getStageId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(workloadRequest.getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(workloadRequest.getLoginUserId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idUser.mandatory", null, Locale.US));
		}
		return workloadService.getWorkloadHasLoginUserForStageAndCase(workloadRequest.getStageId(),
				workloadRequest.getCaseId(), workloadRequest.getLoginUserId());
	}

	@RequestMapping(value = "/hasApproverTask", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes hasApproverTask(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getUserID())) {
			throw new InvalidRequestException(messageSource.getMessage("common.userid.mandatory", null, Locale.US));
		}

		return eventService.hasApproverTask(commonHelperReq.getIdCase(), commonHelperReq.getUserID());
	}

	/**
	 * Method Name: getCaseSensitiveByIdCase
	 * Method Description: Method to check case-sensitive information by case ID
	 *
	 * @param idCase
	 * @return boolean
	 *
	 */
	@ApiOperation(value = "Get case-sensitive based on the caseId", tags = { "contacts", "identity" })
	@RequestMapping(value = "/getCaseSensitiveByIdCase", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public boolean getCaseSensitiveByIdCase(@RequestBody Long idCase) {
		return caseSummaryService.getCaseSensitiveByIdCase(idCase);
	}

    @RequestMapping(value = "/getAcknowledgeFileList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public NotificationFileRes getAcknowledgeFileList(@RequestBody CaseSummaryReq caseSummaryReq) {

        if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getIdCase())) {
            throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
        }

        NotificationFileRes retVal = caseSummaryService.getAcknowledgeFileList(caseSummaryReq.getIdCase());
        return retVal;
    }

	@RequestMapping(value = "/acknowledgementRequestUpload", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public NotificationFileRes acknowledgementRequestUpload(@RequestBody CaseSummaryReq caseSummaryReq) {
		if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
        if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getLoginUserId())) {
            throw new InvalidRequestException(messageSource.getMessage("common.idUser.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getEncodedFileData())) {
            throw new InvalidRequestException(messageSource.getMessage("common.fileData.mandatory", null, Locale.US));
        }
		return caseSummaryService.acknowledgementRequestUpload(caseSummaryReq);
	}

	@RequestMapping(value = "/acknowledgementRequestSend", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public NotificationFileRes acknowledgementRequestSend(@RequestBody CaseSummaryReq caseSummaryReq) {
        if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getIdCgNotifFileUploadDtl())) {
            throw new InvalidRequestException(messageSource.getMessage("common.idCgNotifFileUploadDtl.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getLoginUserId())) {
            throw new InvalidRequestException(messageSource.getMessage("common.idUser.mandatory", null, Locale.US));
        }
		return caseSummaryService.acknowledgementRequestSend(caseSummaryReq);
	}

    @RequestMapping(value = "/acknowledgementRequestDownload", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public NotificationFileRes acknowledgementRequestDownload(@RequestBody CaseSummaryReq caseSummaryReq) {
        if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getIdCgNotifFileUpload())) {
            throw new InvalidRequestException(messageSource.getMessage("common.idCgNotifFileUpload.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(caseSummaryReq.getIdCgNotifFileUploadDtl())) {
            throw new InvalidRequestException(messageSource.getMessage("common.idCgNotifFileUploadDtl.mandatory", null, Locale.US));
        }
        return caseSummaryService.acknowledgementRequestDownload(caseSummaryReq);
    }
}
