package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.casemergesplit.dto.CaseMergeSplitSaveDto;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseMerge;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.UnitAccessDto;
import us.tx.state.dfps.service.admin.dao.CaseMergeToDao;
import us.tx.state.dfps.service.admin.dto.CaseMergeToInDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeToOutDto;
import us.tx.state.dfps.service.admin.dto.StageEventDto;
import us.tx.state.dfps.service.admin.service.UnitService;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseMergeCustomDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CaseMergeDetailDto;
import us.tx.state.dfps.service.casepackage.dto.CaseStageSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.PersonMergeDto;
import us.tx.state.dfps.service.casepackage.service.CaseMergeService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.IncomingDetailDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CaseMergeSplitSaveReq;
import us.tx.state.dfps.service.common.request.CaseMergeSplitValidateReq;
import us.tx.state.dfps.service.common.request.CaseMergeSplitValidateRes;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.RetrvCaseMergeReq;
import us.tx.state.dfps.service.common.response.CaseMergeUpdateRes;
import us.tx.state.dfps.service.common.response.EventDetailRes;
import us.tx.state.dfps.service.common.response.RetrvCaseMergeRes;
import us.tx.state.dfps.service.common.service.CommonService;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.StageSearchEventDao;
import us.tx.state.dfps.service.eventutility.dao.EventUtilityDao;
import us.tx.state.dfps.service.stage.dto.PrimaryWorker;
import us.tx.state.dfps.service.workload.dao.CaseMergeUpdateDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.CaseMergeUpdateDto;
import us.tx.state.dfps.service.workload.dto.EventStageDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.WorkloadService;

@Service
@Transactional
public class CaseMergeServiceImpl implements CaseMergeService {

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	CaseMergeCustomDao caseMergeCustomDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	CommonService commonService;

	@Autowired
	CaseMergeUpdateDao caseMergeUpdateDao;

	@Autowired
	IncomingDetailDao incomingDetailDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;

	@Autowired
	UnitService unitService;

	@Autowired
	CaseMergeToDao caseMergeToDao;

	@Autowired
	TodoDao todoDao;

	@Autowired
	EventUtilityDao eventUtilityDao;

	@Autowired
	StageSearchEventDao stageSearchEventDao;

	@Autowired
	private WorkloadService workloadService;

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;
	private static final Logger log = Logger.getLogger(CaseMergeServiceImpl.class);

	public static final long MSG_CASE_APRV_PENDING = 55175;
	public static final long MSG_MERGE_BY_INTAKE_DATE = 55144;
	public static final long MSG_CIRCULAR_MERGE_NOT_ALLOWED = 55067;
	public static final long MSG_CFC_CMS_MAX_MERGES = 55204;
	public static final long MSG_CASES_CANNOT_BE_MERGED = 55552;
	public static final String REC_DSTRY_DATE = "recDstryDate";
	public static final long MSG_CRSR_STAGE = 55176;
	public static final long MSG_CFC_NO_MERGE_ACCESS = 8263;
	public static final long MSG_CFC_MERGE_PEND = 8266;
	public static final long MSG_CFC_POST_ADOPT_STG = 8240;
	public static final long MSG_CASE_MRG_STAGE_TYPE = 55210;
	public static final long MSG_MRG_BOTH_CASES_OPEN = 55212;
	public static final long MSG_CASE_MRG_OPEN_STAGE = 55211;
	public static final long MSG_CFC_ALREADY_PEND = 8273;
	public static final long MSG_CFC_CMS_PROGRAM = 8241;
	public static final long MSG_CFC_AFC_CLSD = 55987;
	public static final long MSG_CFC_CMS_APS_PRN = 8249;
	public static final long MSG_CFC_CMS_LE_NOT = 8246;
	public static final long MSG_CFC_CMS_OPEN_INT = 55167;
	public static final long MSG_CFC_CMS_SA = 8245;
	public static final long MSG_CFC_CMS_NO_MRG = 8247;
	public static final long MSG_CFC_CMS_FAD = 8243;
	public static final long MSG_CFC_CMS_KIN = 55341;
	public static final long MSG_CFC_CMS_APS = 8265;
	public static final long MSG_CFC_CMS_INT_OPN = 8244;
	public static final long MSG_CFC_CMS_PRN = 8242;
	public static final long MSG_CFC_CMS_DUP_STG = 8250;
	public static final long MSG_INTAKE_DATE_MESSAGE = 55205;
	public static final long MSG_CFC_CMS_FSNA_CVS = 56924;
	public static final long MSG_CFC_CMS_FSNA_FBSS = 56925;
	public static final long MSG_CFC_CMS_SDM_SFTY = 56926;
	public static final long MSG_CFC_CMS_RISKREASSMNT_NOT_APRV = 56989;
	public static final long MSG_CFC_CMS_REUNIFCTIONASSMNT_NOT_APRV = 56990;
	public static final long MSG_CFC_FROM_ID_INV = 8262;
	public static final long MSG_CFC_CASE_INVALID = 8371;
	public static final String ID_CASE_MERGE_TO = "ID_CASE_MERGE_TO";
	public static final String ID_CASE_MERGE_FROM = "ID_CASE_MERGE_FROM";

	/**
	 * Method Name:getCaseMerges Method Description: This service will get all
	 * merged cases detail
	 * 
	 * @param getCaseMerges
	 * @return RetrvCaseMergeRes
	 */
	@Override
	@Transactional
	public RetrvCaseMergeRes getCaseMerges(RetrvCaseMergeReq retrvCaseMergeReq) {
		String startsWithToken_MergeTo = "ID_CASE_MERGE_TO";
		RetrvCaseMergeRes retrvCaseMergeRes = new RetrvCaseMergeRes();
		retrvCaseMergeRes.setDtTodaysDate(null);
		Long inputCaseid = retrvCaseMergeReq.getIdCase();
		// get all case detail passing the case ID
		CapsCase capsCase = caseMergeCustomDao.getCapsCaseByid(retrvCaseMergeReq.getIdCase());
		retrvCaseMergeRes.setCdCaseProgram(capsCase.getCdCaseProgram());
		if (ObjectUtils.isEmpty(capsCase.getDtCaseClosed())) {
			retrvCaseMergeRes.setIndToCaseCld(Boolean.FALSE);
		} else {
			retrvCaseMergeRes.setIndToCaseCld(Boolean.TRUE);
		}
		// Select of all merged cases for one case (MERGED_TO cases).
		List<CaseMergeDetailDto> caseMergeDetailToList = caseMergeCustomDao.getCaseMergeDetails(inputCaseid,
				startsWithToken_MergeTo);
		if (CollectionUtils.isEmpty(caseMergeDetailToList)) {
			startsWithToken_MergeTo = "ID_CASE_MERGE_FROM";
			caseMergeDetailToList = caseMergeCustomDao.getCaseMergeDetails(inputCaseid,
					startsWithToken_MergeTo);
		}
		List<CaseMergeDetailDto> caseMergeUniqueItemsList = new ArrayList<CaseMergeDetailDto>();
		// to get only unique items to caseMergeUniqueItemsList
		for (CaseMergeDetailDto caseMergeDetail : caseMergeDetailToList) {
			if (!caseMergeUniqueItemsList.stream()
					.filter(caseMerge -> !ObjectUtils.isEmpty(caseMergeDetail.getIdCaseMergeFrom())
							&& !ObjectUtils.isEmpty(caseMerge.getIdCaseMergeFrom())
							&& caseMergeDetail.getIdCaseMergeFrom().compareTo(caseMerge.getIdCaseMergeFrom()) == 0
							&& !ObjectUtils.isEmpty(caseMergeDetail.getIdCaseMergeTo())
							&& !ObjectUtils.isEmpty(caseMerge.getIdCaseMergeTo())
							&& caseMergeDetail.getIdCaseMergeTo().compareTo(caseMerge.getIdCaseMergeTo()) == 0
							&& ((!ObjectUtils.isEmpty(caseMergeDetail.getIndCaseMergeInv())
									&& !ObjectUtils.isEmpty(caseMerge.getIndCaseMergeInv())
									&& caseMergeDetail.getIndCaseMergeInv().equals(caseMerge.getIndCaseMergeInv()))
									|| (ObjectUtils.isEmpty(caseMergeDetail.getIndCaseMergeInv())
											&& ObjectUtils.isEmpty(caseMerge.getIndCaseMergeInv())))
							&& caseMergeDetail.getDtCaseMerge().compareTo(caseMerge.getDtCaseMerge()) == 0)
					.findAny().isPresent()) {

				caseMergeUniqueItemsList.add(caseMergeDetail);
			}
		}

		// Add the caseMergeUniqueItemsList to the response
		retrvCaseMergeRes.setRowCaseMergeDetail(caseMergeUniqueItemsList);
		if (retrvCaseMergeReq.getIndMergeAccess().equals(ServiceConstants.STRING_IND_Y)
				&& !retrvCaseMergeRes.getIndToCaseCld()) {
			List<PrimaryWorker> primaryWorkerList = stageDao.getPrimaryCaseWorker(inputCaseid);
			/*
			 ** Copy Yes to the IndMergeAccess if one of the idperson from dam
			 * output matches with the service idperson
			 */
			Iterator<PrimaryWorker> primaryWorkerIterator = primaryWorkerList.iterator();
			while (primaryWorkerIterator.hasNext()) {
				PrimaryWorker pw = primaryWorkerIterator.next();
				if (retrvCaseMergeReq.getIdPerson().equals(pw.getUidPerson())) {
					// Call to check whether logged in user has unit access ,
					// method should return true or false
					UnitAccessDto unitAccessDto = new UnitAccessDto();
					unitAccessDto.setIdUnit(pw.getUidUnit());
					List<Long> personList = new ArrayList<>();
					personList.add(pw.getUidPerson());
					unitAccessDto.setIdPersonList(personList);
					Boolean hassUnitAccess = commonService.checkUnitAccess(unitAccessDto);
					if (hassUnitAccess) {
						retrvCaseMergeRes.setIndMergeAccess(ServiceConstants.Y);
					} else {
						retrvCaseMergeRes.setIndMergeAccess(ServiceConstants.N);
					}
				}
			}
		}
		retrvCaseMergeRes.setTransactionId(retrvCaseMergeReq.getTransactionId());
		return retrvCaseMergeRes;
	}

	/**
	 * This service will save case merges and case splits to the database with
	 * the pending flag. The actual DB updates will be done in a batch process.
	 * 
	 * @param caseMergeUpdateReq
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	public CaseMergeUpdateRes manageCaseMerge(CaseMergeSplitSaveReq caseMergeSplitSaveReq) {
		CaseMergeUpdateDto dto = null;
		CaseMergeUpdateRes caseMergeUpdateRes = new CaseMergeUpdateRes();
		caseMergeUpdateRes.setMsg("");
		caseMergeUpdateRes.setMoreDataInd(ServiceConstants.STRING_IND_Y);
		for (CaseMergeSplitSaveDto caseMergeUpdateDto : caseMergeSplitSaveReq.getCaseMergeSplitDetailList()) {
			if (caseMergeUpdateDto.getCdDataAction().equalsIgnoreCase(ServiceConstants.VOID_MERGE)) {
				caseMergeUpdateDto.setIndCaseMergePending(ServiceConstants.MERGE);
			}

			switch (caseMergeUpdateDto.getCdDataAction()) {
			case ServiceConstants.MERGE:
				caseMergeUpdateDto.setIndCaseMergePending(ServiceConstants.MERGE);
				dto = caseMergeUpdateDao.saveCaseMerge(
						createTransientCaseMerge(caseMergeUpdateDto));
				caseMergeUpdateRes.setUpdateoutput(caseMergeUpdateDao.searchAllMergedCases(dto.getIdCaseMerge()));
				caseMergeUpdateRes.setMsg("Casemerge saved .. ");
				break;
			case ServiceConstants.SPLIT:
				caseMergeUpdateDto.setIndCaseMergePending(ServiceConstants.SPLIT);
				dto = caseMergeUpdateDao.saveCaseMerge(
						createTransientCaseMerge(caseMergeUpdateDto));
				caseMergeUpdateRes.setUpdateoutput(caseMergeUpdateDao.searchAllMergedCases(dto.getIdCaseMerge()));
				caseMergeUpdateRes.setMsg("Casemerge saved .. ");
				break;
			default:
				dto = caseMergeUpdateDao.deleteCaseMerge(
						createTransientCaseMerge(caseMergeUpdateDto));
				caseMergeUpdateRes.setUpdateoutput(caseMergeUpdateDao.searchAllMergedCases(dto.getIdCaseMerge()));
				caseMergeUpdateRes.setMsg("Casemerge purged .. ");
				break;
			}
		}
		caseMergeUpdateRes.setMoreDataInd(ServiceConstants.STRING_IND_Y);
		return caseMergeUpdateRes;
	}

	protected CaseMerge createTransientCaseMerge(CaseMergeSplitSaveDto caseMergeSplitSaveDto) {
		CaseMerge caseMerge = new CaseMerge();
		caseMerge.setDtCaseMerge(caseMergeSplitSaveDto.getDtCaseMerge());
		caseMerge.setDtCaseMergeSplit(caseMergeSplitSaveDto.getDtCaseSplit());
		caseMerge.setIdCaseMerge(caseMergeSplitSaveDto.getIdCaseMerge());
		caseMerge.setDtLastUpdate(new Date());
		caseMerge.setIndCaseMergePending(caseMergeSplitSaveDto.getIndCaseMergePending().charAt(0));
		CapsCase capsCaseByFrom = new CapsCase();
		capsCaseByFrom.setIdCase(caseMergeSplitSaveDto.getIdCaseMergeFrom());
		caseMerge.setCapsCaseByIdCaseMergeFrom(capsCaseByFrom);
		if (null != caseMergeSplitSaveDto.getIdCaseMergePersonMerge()) {
			Person personMrg = new Person();
			personMrg.setIdPerson(caseMergeSplitSaveDto.getIdCaseMergePersonMerge());
			caseMerge.setPersonByIdCaseMergePersMrg(personMrg);
		}
		if (null != caseMergeSplitSaveDto.getIdCaseMergePersonSplit()) {
			Person personSplit = new Person();
			personSplit.setIdPerson(caseMergeSplitSaveDto.getIdCaseMergePersonSplit());
			caseMerge.setPersonByIdCaseMergePersSplit(personSplit);
		}
		CapsCase capsCaseByTo = new CapsCase();
		capsCaseByTo.setIdCase(caseMergeSplitSaveDto.getIdCaseMergeTo());
		caseMerge.setCapsCaseByIdCaseMergeTo(capsCaseByTo);
		if (StringUtils.isNotBlank(caseMergeSplitSaveDto.getIndCaseMergeInv()))
			caseMerge.setIndCaseMergeInvalid(caseMergeSplitSaveDto.getIndCaseMergeInv().charAt(0));
		if (StringUtils.isNotBlank(caseMergeSplitSaveDto.getIndCaseMergePending()))
			caseMerge.setIndCaseMergePending(caseMergeSplitSaveDto.getIndCaseMergePending().charAt(0));
		return caseMerge;
	}

	/**
	 * checkForReverseMerge Check For Reverse Merge - Circular Case Merge is not
	 * allowed
	 * 
	 * @param idCaseMergeFrom
	 * @param idCaseMergeTo
	 * @return boolean
	 */
	protected boolean checkForReverseMerge(final Long idCaseMergeFrom, final Long idCaseMergeTo) {
		boolean circularMerge = false;
		List<CaseMergeUpdateDto> mergeDtos = caseMergeUpdateDao.checkForReverseMerge(idCaseMergeTo);
		for (CaseMergeUpdateDto caseMergeUpdateDto : mergeDtos) {
			if (caseMergeUpdateDto.getIdCaseMergeFrom().equals(idCaseMergeTo)
					&& caseMergeUpdateDto.getIdCaseMergeTo().equals(idCaseMergeFrom)) {
				circularMerge = true;
				break;
			}
		}
		return circularMerge;
	}

	/**
	 * findAnyStageTypeForReg to check whether either Case Merge To or Case
	 * Merge From is of CRSR stage type
	 * 
	 * @param idCaseMergeFrom
	 * @param idCaseMergeTo
	 * @return boolean
	 */
	protected boolean findAnyStageTypeForReg(final Long idCaseMergeFrom, final Long idCaseMergeTo) {
		boolean checkedCRSR = false;
		List<StageDto> fromStageCaseDtos = stageDao.getStageEntityByCaseId(idCaseMergeFrom,
				StageDao.ORDERBYCLAUSE.BYSITUATIONSTARTDT);
		checkedCRSR = fromStageCaseDtos.stream().filter(dto -> dto.getCdStage().equals(ServiceConstants.C_REG)
				|| dto.getCdStage().equals(ServiceConstants.C_GUA)).findAny().isPresent();
		if (!checkedCRSR) {
			List<StageDto> toStageCaseDtos = stageDao.getStageEntityByCaseId(idCaseMergeTo,
					StageDao.ORDERBYCLAUSE.BYSITUATIONSTARTDT);
			checkedCRSR = toStageCaseDtos.stream().filter(dto -> dto.getCdStage().equals(ServiceConstants.C_REG)
					|| dto.getCdStage().equals(ServiceConstants.C_GUA)).findAny().isPresent();
		}
		return checkedCRSR;
	}

	protected List<StageDto> filterStageCloseDate(List<StageDto> stageDtos) {
		return stageDtos.stream().filter(stageDto -> (stageDto.getDtStageClose() != null)).collect(Collectors.toList());
	}

	private StageDto getEarliestIntakeDate(List<StageDto> stageDtos) {

		List<StageDto> intakeStages = stageDtos.stream()
				.filter(stageDto -> StringUtils.equalsIgnoreCase(ServiceConstants.CSTAGES_INT, stageDto.getCdStage())
						&& null != stageDto.getDtStageCreated())
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(intakeStages)) {
			return null;
		}
		return intakeStages.stream().min((p1, p2) -> p1.getDtStageCreated().compareTo(p2.getDtStageCreated())).get();

	}

	/**
	 * isCaseUnderAnyStage
	 * 
	 * @param stageDtos
	 * @param cdStage
	 * @return boolean
	 */
	private boolean isCaseUnderAnyStage(List<StageDto> stageDtos, final String cdStage) {
		return stageDtos.stream().filter(stageDto -> StringUtils.equalsIgnoreCase(cdStage, stageDto.getCdStage())
				&& ObjectUtils.isEmpty(stageDto.getDtStageClose())).findAny().isPresent();
	}

	/**
	 * Method Description: This method is used to Case Merge Split request
	 * object
	 * 
	 * @param caseMergeReq
	 * @return CaseMergeSplitValidateRes
	 */
	@Override
	public CaseMergeSplitValidateRes verifyCaseMerge(CaseMergeSplitValidateReq caseMergeReq) {

		CaseMergeSplitValidateRes caseMergeRes = new CaseMergeSplitValidateRes();
		List<Long> errorMsgNumberList = new ArrayList<Long>();
		caseMergeRes.setErrorMsgNumberList(errorMsgNumberList);
		long nbrOfOpnStgCaseFrom = 0; /* number of open stages for case-from */
		long nbrOfOpnStgCaseTo = 0; /* number of open stages for case-to */
		String caseFromStage = "";
		String caseToStage = "";
		boolean isMergeFromInv = false;
		boolean isMergePend = false;
		boolean isAPSCases = false;
		boolean isCPSPrincipal = false;
		boolean isMTCRSR = true;
		boolean isSPCStage = false;
		boolean isFAD = false;
		boolean isKIN = false;
		boolean isOpenIntDlg = false;
		boolean isDupStage = false;
		boolean isMTAOC = false;
		boolean isMFAOC = false;
		// Call isPending to check if either cases is pending
		boolean isPending = isPending(caseMergeReq.getIdCaseMergeFrom(), caseMergeReq.getIdCaseMergeTo());
		if (isPending) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CASE_APRV_PENDING);
		}

		// Call switchCases to Compare Intake Date
		boolean switchCase = switchCases(caseMergeReq.getIdCaseMergeFrom(), caseMergeReq.getIdCaseMergeTo()
				);

		/* if CaseTo date is before CaseFrom, don't switch cases. */
		if (switchCase) {
			Long temp = caseMergeReq.getIdCaseMergeTo();
			caseMergeReq.setIdCaseMergeTo(caseMergeReq.getIdCaseMergeFrom());
			caseMergeReq.setIdCaseMergeFrom(temp);
			caseMergeRes.getErrorMsgNumberList().add(MSG_MERGE_BY_INTAKE_DATE);
		}
		// Check For Reverse Merge - Circular Case Merge is not allowed
		boolean isMergeNotAllowed = checkForReverseMerge(caseMergeReq.getIdCaseMergeFrom(),
				caseMergeReq.getIdCaseMergeTo());
		if (isMergeNotAllowed) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CIRCULAR_MERGE_NOT_ALLOWED);
		}
		// Check Merge Counts - Limit the number of level (generations) that a
		// case can be merged to 4
		Integer mergeCount = caseMergeUpdateDao.checkForMergeCounts(caseMergeReq.getIdCaseMergeFrom());
		if (mergeCount > 3) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_MAX_MERGES);
		}
		// Check whether both Case Merge To or Case Merge From retention
		// destruction date
		boolean isNotAllowMerge = checkForRecDstryDate(caseMergeReq.getIdCaseMergeFrom(),
				caseMergeReq.getIdCaseMergeTo());
		if (isNotAllowMerge) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CASES_CANNOT_BE_MERGED);
		}
		// Checking whether either Case Merge To or Case Merge From is of CRSR
		// stage type
		boolean checkedCRSR = findAnyStageTypeForReg(caseMergeReq.getIdCaseMergeFrom(),
				caseMergeReq.getIdCaseMergeTo());
		if (checkedCRSR) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CRSR_STAGE);
		}
		// Check if The Case SMP
		CapsCase capsCaseFrom = capsCaseDao.getCapsCaseEntityById(caseMergeReq.getIdCaseMergeFrom());
		// if no cas found then entered from id is invalid , we do not need to
		// check for other errors
		if (ObjectUtils.isEmpty(capsCaseFrom)) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_FROM_ID_INV);
			return caseMergeRes;
		}
		/*
		 ** If the Merge From case is closed set the Merge From case Closed
		 * indicator variable to TRUE, otherwise set it to false
		 */
		if (null != capsCaseFrom && !TypeConvUtil.isNullOrEmpty(capsCaseFrom.getDtCaseClosed())) {
			caseMergeRes.setIndFromCaseClosed(Boolean.TRUE);
		} else {
			caseMergeRes.setIndFromCaseClosed(Boolean.FALSE);
		}

		if (null != capsCaseFrom) {
			caseMergeRes.setNmCaseMergeFrom(capsCaseFrom.getNmCase());
		} else {
			caseMergeRes.setNmCaseMergeFrom(null);
		}

		List<CaseMergeUpdateDto> caseMergeFromUpdates = caseMergeUpdateDao
				.checkForReverseMerge(caseMergeReq.getIdCaseMergeFrom());
		/*
		 ** While more rows are left to process and rc is success, continue loop
		 * to check if any of the Merge From case is Pending to be merged, and
		 * if any of the Merge From case is to be Invalidated. If so than set
		 * the respective flags.
		 */
		Iterator<CaseMergeUpdateDto> iter = caseMergeFromUpdates.iterator();
		while (iter.hasNext() && ((!isMergeFromInv) || (!isMergePend))) {
			CaseMergeUpdateDto caseMergeFromUpdate = iter.next();
			/*
			 ** If the Case Merge Invalidation indicator is set to TRUE than
			 * check to see if the batch process has gone thru and Merge has
			 * taken place or not.
			 */
			if (ServiceConstants.STRING_IND_Y.equals(caseMergeFromUpdate.getIndCaseMergeInv())
					&& TypeConvUtil.isNullOrEmpty(caseMergeFromUpdate.getDtCaseMergeSplitDate())) {
				isMergeFromInv = true;
			}
			/*
			 ** If a case is pending to be merged than set the MergePend
			 * indicator to TRUE.
			 */
			if ((ServiceConstants.MERGE.equals(caseMergeFromUpdate.getIndCaseMergePending()))
					|| (ServiceConstants.SPLIT.equals(caseMergeFromUpdate.getIndCaseMergePending()))) {
				isMergePend = true;
			}
		}
		/*
		 ** If Merge To case is closed and Merge From case is open and
		 * MergeAccess is not allowed than check if the user is Primary worker
		 * or is in the unit hierarchy of Primary in Merge From case.
		 */
		if (!caseMergeReq.getIndMergeAccess().equalsIgnoreCase(ServiceConstants.STRING_IND_Y)
				&& !caseMergeRes.getIndFromCaseClosed()) {
			String indMergeAccess = hasMergeAccess(caseMergeReq.getIdCaseMergeFrom(), caseMergeReq.getIdPerson());
			caseMergeRes.setIndMergeAccess(indMergeAccess);
			if (!indMergeAccess.equalsIgnoreCase(ServiceConstants.STRING_IND_Y)) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_NO_MERGE_ACCESS);
			}
		}

		if (!caseMergeReq.getIndMergePend().equalsIgnoreCase(ServiceConstants.STRING_IND_Y)) {
			CaseMergeToInDto caseMergeToInDto = new CaseMergeToInDto();
			caseMergeToInDto.setIdCaseMergeTo(caseMergeReq.getIdCaseMergeFrom());
			List<CaseMergeToOutDto> caseMergeTolist = caseMergeToDao.getCaseMergeDtls(caseMergeToInDto);

			if (!isMergePend && caseMergeTolist.stream()
					.filter(dto -> !TypeConvUtil.isNullOrEmpty(dto.getIndCaseMergePending())
							&& (dto.getIndCaseMergePending().equalsIgnoreCase(ServiceConstants.MERGE)
									|| dto.getIndCaseMergePending().equalsIgnoreCase(ServiceConstants.SPLIT)))
					.findAny().isPresent()) {
				isMergePend = true;
			}
			if (isMergePend) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_MERGE_PEND);
			}
		}
		// For case merge to
		List<StageDto> stageDtolist = stageDao.getStageEntityByCaseId(caseMergeReq.getIdCaseMergeTo(),
				StageDao.ORDERBYCLAUSE.BYSTAGEID);
		if (!caseMergeReq.getIndPostAdopt().equalsIgnoreCase(ServiceConstants.STRING_IND_Y) && stageDtolist.stream()
				.filter(stageDto -> stageDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_PAD)).findAny()
				.isPresent()) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_POST_ADOPT_STG);
			caseMergeReq.setIndPostAdopt(ServiceConstants.STRING_IND_Y);
		}
			nbrOfOpnStgCaseTo = stageDtolist.stream()
					.filter(stageDto -> (TypeConvUtil.isNullOrEmpty(stageDto.getDtStageClose()) || ServiceConstants.GENERIC_END_DATE.equals(stageDto.getDtStageClose()))).count();

		for (StageDto stageDto : stageDtolist) {
			if(!ObjectUtils.isEmpty(stageDto.getIndStageClose()) && CodesConstant.N.equals(stageDto.getIndStageClose())
					 && ObjectUtils.isEmpty(stageDto.getDtStageClose())){
				caseToStage = stageDto.getCdStage();
			}
		}

		// For case merge from
		List<StageDto> stageDtoCaseMergeFromlist = stageDao.getStageEntityByCaseId(caseMergeReq.getIdCaseMergeFrom(),
				StageDao.ORDERBYCLAUSE.BYSTAGEID);
		// check if MSG_CFC_CASE_INVALID error is thrown if so , then return it
		// from here
		if (CollectionUtils.isEmpty(stageDtoCaseMergeFromlist)) {
			// we need to clear other errors , show only show the following
			// error as legacy was throwing exception
			// web side catching that exception.
			caseMergeRes.getErrorMsgNumberList().clear();
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CASE_INVALID);
			return caseMergeRes;// this should be return here as this error is
								// actaully sever error , we do not need to
								// check for other errors.
		}
		if (!caseMergeReq.getIndPostAdopt().equalsIgnoreCase(ServiceConstants.STRING_IND_Y) && stageDtoCaseMergeFromlist
				.stream().filter(stageDto -> stageDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_PAD))
				.findAny().isPresent()) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_POST_ADOPT_STG);
			caseMergeReq.setIndPostAdopt(ServiceConstants.STRING_IND_Y);
		}
		nbrOfOpnStgCaseFrom = stageDtoCaseMergeFromlist.stream()
					.filter(stageDto -> (TypeConvUtil.isNullOrEmpty(stageDto.getDtStageClose())) || (ServiceConstants.GENERIC_END_DATE.equals(stageDto.getDtStageClose()))).count();
		for (StageDto stageDto : stageDtoCaseMergeFromlist) {
			if(!ObjectUtils.isEmpty(stageDto.getIndStageClose()) && CodesConstant.N.equals(stageDto.getIndStageClose())
					&& ObjectUtils.isEmpty(stageDto.getDtStageClose())) {
				caseFromStage = stageDto.getCdStage();
			}
		}

		if (null != capsCaseFrom && caseMergeReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.STAGE_PROGRAM_APS)
				&& (capsCaseFrom.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.STAGE_PROGRAM_APS))) {

			if (nbrOfOpnStgCaseFrom == 1 && nbrOfOpnStgCaseTo == 1 && !caseFromStage.equalsIgnoreCase(caseToStage)) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CASE_MRG_STAGE_TYPE);
			} else if (nbrOfOpnStgCaseFrom == 0 || nbrOfOpnStgCaseTo == 0) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_MRG_BOTH_CASES_OPEN);
			} else if (nbrOfOpnStgCaseFrom > 1 || nbrOfOpnStgCaseTo > 1) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CASE_MRG_OPEN_STAGE);
			}
		}
		// CLSC66D
		List<PersonMergeDto> personMergeToIdList = caseMergeCustomDao
				.retrievRecentPersonId(caseMergeReq.getIdCaseMergeTo());
		List<PersonMergeDto> personMergeFromIdList = caseMergeCustomDao
				.retrievRecentPersonId(caseMergeReq.getIdCaseMergeFrom());
		if (isMergePend) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_ALREADY_PEND);
		}
		/*
		 ** The Merge To and Merge From cases must have the same program.
		 */
		if (null != capsCaseFrom && !caseMergeReq.getCdCaseProgram().equals(capsCaseFrom.getCdCaseProgram())) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_PROGRAM);
		}

		/*
		 * If the Merge To case Program is the same as Adult Protective Service
		 * or Adult Facility Care Than check if Merge From case is Closed or
		 * not.
		 */
		if (caseMergeReq.getCdCaseProgram().equals(ServiceConstants.STAGE_PROGRAM_APS)
				|| caseMergeReq.getCdCaseProgram().equals(ServiceConstants.STAGE_PROGRAM_AFC)) {
			/*
			 ** If the Merge From case is open and Merge To case Program is Adult
			 * Protective Service than set APSCase flag to True.
			 */
			if (caseMergeRes.getIndFromCaseClosed()
					&& caseMergeReq.getCdCaseProgram().equals(ServiceConstants.STAGE_PROGRAM_APS)) {
				isAPSCases = true;
			}
			/*
			 ** SIR 1005095 - If it's AFC and the Merge From case is closed,
			 * disallow merge
			 */

			if (ServiceConstants.STAGE_PROGRAM_AFC.equals(caseMergeReq.getCdCaseProgram())
					&& (caseMergeRes.getIndFromCaseClosed()
							|| ServiceConstants.STRING_IND_Y.equalsIgnoreCase(caseMergeReq.getIndToCaseClosed()))) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_AFC_CLSD);
			}
			/*
			 ** If a same Person is found in both the MergeTo and MergeFrom case
			 * which has a Stage Person Role of Person Role Victim or Person
			 * Role Client or Person Role Both than set the APSVictim flag to
			 * TRUE.
			 */
			boolean apsVictim = false;
			if (ServiceConstants.STAGE_PROGRAM_APS.equals(caseMergeReq.getCdCaseProgram())
					|| ServiceConstants.STAGE_PROGRAM_AFC.equals(caseMergeReq.getCdCaseProgram())) {
				for (PersonMergeDto personMergeToDto : personMergeToIdList) {
					if (personMergeToDto.getCdPersonRole().equalsIgnoreCase(ServiceConstants.PERSON_ROLE_VICTIM)
							|| personMergeToDto.getCdPersonRole().equalsIgnoreCase(ServiceConstants.PERSON_ROLE_BOTH)
							|| personMergeToDto.getCdPersonRole()
									.equalsIgnoreCase(ServiceConstants.PERSON_ROLE_CLIENT)) {
						if (apsVictim) {
							break;
						}
						for (PersonMergeDto personMergeFromDto : personMergeFromIdList) {
							if (personMergeFromDto.getIdPerson().equals(personMergeToDto.getIdPerson())
									&& (ServiceConstants.PERSON_ROLE_VICTIM
											.equalsIgnoreCase(personMergeFromDto.getCdPersonRole())
											|| ServiceConstants.PERSON_ROLE_BOTH
													.equalsIgnoreCase(personMergeFromDto.getCdPersonRole())
											|| ServiceConstants.PERSON_ROLE_CLIENT
													.equalsIgnoreCase(personMergeFromDto.getCdPersonRole()))) {
								apsVictim = true;
								break;
							}

						}
					}
				}
				if (!apsVictim) {
					caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_APS_PRN);
				}
			}

		}
		/*
		 ** Look for matching principals. If the MergeTo case Program is
		 * Licensing or Child Protective Service Than loop through
		 * MergeToPersonInfo and MergeFromPersonInfo. If same Person is found in
		 * both the cases than set CPSPrincipal flag to TRUE.
		 */
		if (caseMergeReq.getCdCaseProgram().equals(ServiceConstants.CAPS_PROG_CPS)
				|| caseMergeReq.getCdCaseProgram().equals(ServiceConstants.CAPS_PROG_CCL)
				|| caseMergeReq.getCdCaseProgram().equals(ServiceConstants.CAPS_PROG_RCL)) {
			for (PersonMergeDto personMergeToDto : personMergeToIdList) {

				if (personMergeFromIdList.stream().filter(
						personMergeFromDto -> personMergeFromDto.getIdPerson().equals(personMergeToDto.getIdPerson()))
						.findAny().isPresent()) {
					isCPSPrincipal = true;
				}

			}
		}
		/*
		 ** Look for Case Related Special Request Stage Type. CRSRs may be merged
		 * if both the Merge To and Merge From cases are CRSRs. If a CRSR is
		 * being merged into a case that is not a CRSR, the CRSR must be the
		 * Merge From case.
		 */
		if (stageDtolist.stream().filter(
				stagedto -> !stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.SPECIAL_REQUEST_STAGE_TYPE))
				.findAny().isPresent()) {
			// If any of the stages is not CRSR then the case is not CRSR.
			isMTCRSR = false;
		}
		// look for I&R or SPC stage and set the Flag.
		if (stageDtolist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_SPC)
						|| stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_IR))
				.findAny().isPresent()) {
			isSPCStage = true;
		}

		// look for FAD stage and set the Flag.
		if (stageDtolist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_FAD)).findAny()
				.isPresent()) {
			isFAD = true;
		}

		// look for KIN stage and set the Flag.
		if (stageDtolist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_KIN)).findAny()
				.isPresent()) {
			isKIN = true;
		}
		// look for open AOC stage and set the Flag.
		// *** Check this
		if (isAPSCases && (stageDtolist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_AOC)
						&& TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose()))).findAny().isPresent()) {
			isMTAOC = true;
		}

		/*
		 * look for open intake and set the Open Intake flag. There can be an
		 * Open Intake but there cannot be an Open Intake dialog for a Merge can
		 * take place.
		 */
		if (stageDtolist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INT)
						&& TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose()))
				.findAny().isPresent()) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_OPEN_INT);
		}

		// Look for Open Intake Dialog and set the flag.
		if (stageDtolist.stream()
				.filter(stagedto -> TypeConvUtil.isNullOrEmpty(stagedto.getCdStageReasonClosed())
						&& stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INT))
				.findAny().isPresent()) {
			isOpenIntDlg = true;
		}
		// ************Look for Open Duplicate Stages.*****************
		// Duplicate Stages for SUBCARE
		if (stageDtolist.stream()
				.filter(stagedto -> TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose())
						&& stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_SUB))
				.findAny().isPresent()) {
			isDupStage = isDuplicateStages(stageDtolist, personMergeToIdList, personMergeFromIdList,
					ServiceConstants.CSTAGES_SUB);
		}
		// Duplicate Stages for Post Adopt stage
		if (!isDupStage && stageDtolist.stream()
				.filter(stagedto -> TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose())
						&& stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_PAD))
				.findAny().isPresent()) {
			isDupStage = isDuplicateStages(stageDtolist, personMergeToIdList, personMergeFromIdList,
					ServiceConstants.CSTAGES_PAD);

		}
		// Duplicate Stages for Adopt stage
		if (!isDupStage && stageDtolist.stream()
				.filter(stagedto -> TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose())
						&& stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_ADO))
				.findAny().isPresent()) {
			isDupStage = isDuplicateStages(stageDtolist, personMergeToIdList, personMergeFromIdList,
					ServiceConstants.CSTAGES_ADO);

		}
		// Duplicate Stages for Prep Adult stage
		if (!isDupStage && stageDtolist.stream()
				.filter(stagedto -> TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose())
						&& stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_PAL))
				.findAny().isPresent()) {
			isDupStage = isDuplicateStages(stageDtolist, personMergeToIdList, personMergeFromIdList,
					ServiceConstants.CSTAGES_PAL);
		}
		/*
		 ** look for CRSR stage type. CRSRs may be merged if both the Merge To
		 * and Merge From cases are CRSRs. If a CRSR is being merged into a case
		 * that is not a CRSR, the CRSR must be the Merge From case. If any of
		 * the Merge From Stage Type is Case Related Special Request than set
		 * the flag to TRUE.
		 */

		/*
		 * look for I&R or SPC stage type. If any of the Merge From stage type
		 * is Info and Refferal or Special stage type set the SPCStage flag to
		 * TRUE.
		 */
		if (!isSPCStage && stageDtoCaseMergeFromlist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_IR)).findAny()
				.isPresent()) {
			isSPCStage = true;
		}

		/*
		 * look for FAD stage. If any of the Merge From Stage is FAD set the
		 * flag to TRUE.
		 */
		if (!isFAD && stageDtoCaseMergeFromlist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_FAD)).findAny()
				.isPresent()) {
			isFAD = true;
		}

		/*
		 * look for KIN stage. If any of the Merge From Stage is KIN set the
		 * flag to TRUE.
		 */
		if (!isKIN && stageDtoCaseMergeFromlist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_KIN)).findAny()
				.isPresent()) {
			isKIN = true;
		}

		/*
		 * look for open AOC stage(It only exists in APS Stage). If any of the
		 * Merge From Stage is Aging Out Child stage and that stage is still
		 * Open than set the AOC flag to TRUE.
		 */
		if (isAPSCases && stageDtoCaseMergeFromlist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_AOC)
						&& TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose()))
				.findAny().isPresent()) {
			isMFAOC = true;
		}
		if (stageDtoCaseMergeFromlist.stream()
				.filter(stagedto -> stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INT)
						&& TypeConvUtil.isNullOrEmpty(stagedto.getDtStageClose()))
				.findAny().isPresent()) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_OPEN_INT);
			for (StageDto StageFromDto : stageDtoCaseMergeFromlist) {
				if (StageFromDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INT)
						&& TypeConvUtil.isNullOrEmpty(StageFromDto.getDtStageClose())) {
					List<TodoDto> toDoRes = todoDao.getTodoByTodoStageIdTask(StageFromDto.getIdStage(),
							ServiceConstants.NOTIF_REPORTER_TASK_CODE);
					if (toDoRes.stream().filter(todo -> TypeConvUtil.isNullOrEmpty(todo.getDtTodoCompleted())).findAny()
							.isPresent()) {
						caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_LE_NOT);
					}
				}

			}
		}
		if (stageDtoCaseMergeFromlist.stream()
				.filter(stagedto -> TypeConvUtil.isNullOrEmpty(stagedto.getCdStageReasonClosed())
						&& stagedto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INT))
				.findAny().isPresent()) {
			isOpenIntDlg = true;
		}

		if (caseMergeReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_CPS)) {
			for (StageDto StageMergeToDto : stageDtolist) {
				if (StageMergeToDto.getCdStage().equalsIgnoreCase(ServiceConstants.INVEST_STAGE)) {
					for (StageDto StageMergeFromDto : stageDtoCaseMergeFromlist)

						if (((StageMergeFromDto.getCdStage().equalsIgnoreCase(ServiceConstants.INVEST_STAGE))
								|| (StageMergeFromDto.getCdStage().equalsIgnoreCase(ServiceConstants.CSTAGES_INT)))
								&& TypeConvUtil.isNullOrEmpty(StageMergeFromDto.getDtStageClose())
								&& StageMergeFromDto.getCdStageProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_CPS)
								&& !caseMergeRes.getIndFromCaseClosed()
								&& caseMergeReq.getIndToCaseClosed().equalsIgnoreCase(ServiceConstants.STRING_IND_N))

						{
							// Call DAM CLSC94D for merge from
							caseMergeRes.setCaseFromCclStatusCode(
									eventUtilityDao.getINVConclusionStatus(caseMergeReq.getIdCaseMergeFrom()));
							// Call DAM CLSC94D for merge to
							caseMergeRes.setCaseToCclStatusCode(
									eventUtilityDao.getINVConclusionStatus(caseMergeReq.getIdCaseMergeTo()));
						}
				}
			}
		}

		/*
		 * If Merge From Case is Open than look for open service Authorization.
		 * Note that all service Authorization must be closed in the Merge From
		 * case before the merge can take place.
		 */
		if (!caseMergeRes.getIndFromCaseClosed()) {
			// Call - CLSC60D
			List<Date> dtSvcAuthTermList = serviceAuthorizationDao.getSvcAuthDtTerm(caseMergeReq.getIdCaseMergeFrom());
			Date dtSvcAuthTerm;

			// Taking the latest date
			if (!dtSvcAuthTermList.isEmpty()) {
				dtSvcAuthTerm = dtSvcAuthTermList.get(0);
				for (Date currentDate : dtSvcAuthTermList) {
					if (dtSvcAuthTerm.before(currentDate)) {
						dtSvcAuthTerm = currentDate;
					}
				}
			} else {
				dtSvcAuthTerm = null;
			}
			if (!TypeConvUtil.isNullOrEmpty(dtSvcAuthTerm)) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_SA);
			}
		}

		if (isSPCStage) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_NO_MRG);
		}

		// FAD homes cannot be merged.
		if (isFAD) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_FAD);
		}
		// KIN homes cannot be merged.
		if (isKIN) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_KIN);
		}

		/*
		 ** Cannot merge an open APS case and an open APS case with an open AOC
		 * stage.
		 */
		if ((isMTAOC && !isMFAOC) || (!isMTAOC && isMFAOC)) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_APS);
		}
		if (isOpenIntDlg) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_INT_OPN);
		}

		/*
		 ** CPS - There must be at least one common principal in the Merge From
		 * and Merge To cases.
		 */
		if ((caseMergeReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_CPS)
				|| caseMergeReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_CCL)
				|| caseMergeReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_RCL))
				&& (!isCPSPrincipal)) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_PRN);
		}
		if (isDupStage) {
			caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_DUP_STG);

		}
		if (!ObjectUtils.isEmpty(caseMergeReq.getIdCaseMergeFrom())) {
			caseMergeRes = getAssesmentEventListByStages(caseMergeReq.getIdCaseMergeFrom(), caseMergeRes);
		}
		log.info("Case Merge Split Request Validated successfully");
		return caseMergeRes;
	}

	/**
	 * isDuplicateStages
	 *
	 * Loop thru both the MergeTo Person and MergeFrom Person. If same Person is
	 * found in both the cases which has a Person role of Primary Child and
	 * belongs to the same stage Than set the Duplicate flag to TRUE.
	 * 
	 * @param stageDtolist
	 * @param personMergeToIdList
	 * @param personMergeFromIdList
	 * @param stage
	 * @return boolean
	 */
	protected boolean isDuplicateStages(List<StageDto> stageDtolist, List<PersonMergeDto> personMergeToIdList,
			List<PersonMergeDto> personMergeFromIdList, String stage) {
		boolean isDupStage = false;
		for (StageDto stageMergeTo : stageDtolist) {
			for (PersonMergeDto personMergeToDto : personMergeToIdList) {
				if (stageMergeTo.getIdStage().equals(personMergeToDto.getIdStage())
						&& stageMergeTo.getCdStage().equalsIgnoreCase(stage)
						&& personMergeToDto.getCdPersonRole().equalsIgnoreCase(ServiceConstants.PRIMARY_CHILD)
						&& (personMergeFromIdList.stream()
								.filter(personMergeFromDto -> personMergeFromDto.getIdPerson()
										.equals(personMergeToDto.getIdPerson())
										&& personMergeFromDto.getIdStage().equals(personMergeToDto.getIdStage()))
								.findAny().isPresent())) {
					isDupStage = true;
					break;
				}
			}
		}
		return isDupStage;
	}

	/**
	 * hasMergeAccess
	 *
	 * If Merge To case is closed and Merge From case is open and MergeAccess is
	 * not allowed than check if the user is Primary worker or is in the unit
	 * hierarchy of Primary in Merge From case.
	 *
	 * 
	 * @param idCaseMergeFrom
	 * @param idPerson
	 * @return String
	 */
	protected String hasMergeAccess(final Long idCaseMergeFrom, final Long idPerson) {
		String indMergeAccess = ServiceConstants.STRING_IND_N;
		List<PrimaryWorker> primaryWorkers = stageDao.getPrimaryCaseWorker(idCaseMergeFrom);
		/*
		 ** check if MergeAccess is allowed or not. Once the MergeAccess
		 * Indicator is set to Yes the loop will exit, otherwise it will go thru
		 * all the rows returned.
		 */

		if (primaryWorkers.stream().filter(worker -> worker.getUidPerson() == idPerson).findAny().isPresent()) {
			indMergeAccess = ServiceConstants.STRING_IND_Y;
		} else {
			/*
			 ** Call Unit Access Function - to check if the person is in the unit
			 * hierarchy of primary in Merge From case.
			 */
			List<Long> idPersonList = new ArrayList<Long>();
			idPersonList.add(idPerson);
			for (PrimaryWorker worker : primaryWorkers) {
				if (unitService.unitAccess(worker.getUidUnit(), ServiceConstants.EMPTY_STRING,
						ServiceConstants.EMPTY_STRING, ServiceConstants.EMPTY_STRING, idPersonList)) {
					indMergeAccess = ServiceConstants.STRING_IND_Y;
					break;
				} else {
					indMergeAccess = ServiceConstants.STRING_IND_N;
				}
			}
		}
		return indMergeAccess;
	}

	/**
	 * checkForRecDstryDate This function will check whether the records
	 * retention destruction date of both Case Merge To or Case Merge From is in
	 * the future. Also compare the records retention destruction date of the
	 * first case with the date of case opened of other case
	 * 
	 * @param idCaseMergeFrom
	 * @param idCaseMergeTo
	 * @return boolean
	 */
	protected boolean checkForRecDstryDate(final Long idCaseMergeFrom, final Long idCaseMergeTo) {
		Map<String, Date> recDstyFromMap = caseMergeUpdateDao.checkForRecDstryDate(idCaseMergeFrom);
		Map<String, Date> recDstyToMap = caseMergeUpdateDao.checkForRecDstryDate(idCaseMergeTo);
		int lRCFrom = 1;
		int lRCTo = 1;
		boolean bDtDstryDtInvalid = false;
		boolean bDtCaseOpenedInvalid = false;
		/*
		 * Check if record retention destruction date of case merge from and
		 * case merge to are present
		 */
		if (!TypeConvUtil.isNullOrEmpty(recDstyFromMap)) {
			lRCFrom = recDstyFromMap.get(REC_DSTRY_DATE).compareTo(new Date());
			/*
			 * Compare the destruction date and date of case opened of both case
			 * merge from.
			 */
			CapsCaseDto toCapsCaseDto = capsCaseDao.getCaseDetails(idCaseMergeTo);
			/*
			 * Check to see if the case opened date of case merge to and
			 * destruction date of case merge from are present.
			 */
			if (!TypeConvUtil.isNullOrEmpty(toCapsCaseDto.getDtCaseOpened())
					&& !TypeConvUtil.isNullOrEmpty(recDstyFromMap.get(REC_DSTRY_DATE))) {
				/*
				 * If the destruction date of case merge from is prior or same
				 * as date of case opened of case merge to - set flag indicate
				 * the case opened date is invalid(bDtCaseOpenedInvalid = TRUE).
				 */
				int lRC1To = recDstyFromMap.get(REC_DSTRY_DATE).compareTo(toCapsCaseDto.getDtCaseOpened());
				if (lRC1To <= 0) {
					bDtCaseOpenedInvalid = true;
				}
			}
		}
		if (!TypeConvUtil.isNullOrEmpty(recDstyToMap)) {
			lRCTo = recDstyToMap.get(REC_DSTRY_DATE).compareTo(new Date());
			/*
			 * Compare the destruction date and date of case opened of both case
			 * merge to.
			 */
			CapsCaseDto fromCapsCaseDto = capsCaseDao.getCaseDetails(idCaseMergeFrom);

			/*
			 * Check to see if the case opened date of case merge from and
			 * destruction date of case merge to are present.
			 */
			if (!TypeConvUtil.isNullOrEmpty(fromCapsCaseDto.getDtCaseOpened())
					&& !TypeConvUtil.isNullOrEmpty(recDstyToMap.get(REC_DSTRY_DATE))) {
				/*
				 * If the destruction date of case merge to is prior or same as
				 * date of case opened of case merge from - set flag indicate
				 * the case opened date is invalid(bDtCaseOpenedInvalid = TRUE).
				 */
				int lRC1From = recDstyToMap.get(REC_DSTRY_DATE).compareTo(fromCapsCaseDto.getDtCaseOpened());
				if (lRC1From <= 0) {
					bDtCaseOpenedInvalid = true;
				}
			}
		}

		if (lRCFrom <= 0 && lRCTo <= 0) {
			bDtDstryDtInvalid = true;
		}

		/*
		 * Send error message if invalid destruction date and invalid case
		 * opened date.
		 */
		return (bDtDstryDtInvalid || bDtCaseOpenedInvalid);
	}

	/**
	 * switchCases this function checks if we have to switch cases
	 * 
	 * @param idCaseMergeFrom
	 * @param idCaseMergeTo
	 * @return boolean
	 */
	protected boolean switchCases(final Long idCaseMergeFrom, final Long idCaseMergeTo) {
		/*
		 * There must always be a from-case for case-merge. if there is no row
		 * back from, we let the validation proccess to throw the right error
		 * message for the user.
		 */
		boolean switchCase = false;
		List<StageDto> fromStageDtos = stageDao.getStageEntityByCaseId(idCaseMergeFrom,
				StageDao.ORDERBYCLAUSE.BYSTAGEID);
		List<StageDto> toStageDtos = stageDao.getStageEntityByCaseId(idCaseMergeTo, StageDao.ORDERBYCLAUSE.BYSTAGEID);
		StageDto fromStageDto = getEarliestIntakeDate(fromStageDtos);
		StageDto toStageDto = getEarliestIntakeDate(toStageDtos);
		boolean isCaseFromInvOpen = isCaseUnderAnyStage(fromStageDtos, ServiceConstants.CSTAGES_INV);
		boolean isCaseToInvOpen = isCaseUnderAnyStage(toStageDtos, ServiceConstants.CSTAGES_INV);
		boolean isCaseFromArOpen = isCaseUnderAnyStage(fromStageDtos, ServiceConstants.CSTAGES_AR);
		boolean isCaseToArOpen = isCaseUnderAnyStage(toStageDtos, ServiceConstants.CSTAGES_AR);

		/* do the comparison only if they both have Inv or both A-R open */
		if (((isCaseFromInvOpen && isCaseToInvOpen) || (isCaseFromArOpen && isCaseToArOpen))
				&& !TypeConvUtil.isNullOrEmpty(fromStageDto) && !TypeConvUtil.isNullOrEmpty(toStageDto)) {
			CaseStageSummaryDto fromSummaryDto = caseSummaryDao.getIncomingDetail(fromStageDto.getIdStage());
			CaseStageSummaryDto toSummaryDto = caseSummaryDao.getIncomingDetail(toStageDto.getIdStage());
			if (!TypeConvUtil.isNullOrEmpty(fromSummaryDto) && !TypeConvUtil.isNullOrEmpty(toSummaryDto)) {
				//Warranty Defect# 12213 - Added null check to avoid exception
				switchCase = (!ObjectUtils.isEmpty(toSummaryDto.getDtIncomingCall()) && !ObjectUtils.isEmpty(fromSummaryDto.getDtIncomingCall())) &&
						toSummaryDto.getDtIncomingCall().compareTo(fromSummaryDto.getDtIncomingCall()) > 0 ? true
						: false;
			}
		}

		return switchCase;
	}

	/**
	 * Method Description: This method is used to find if either to or from case
	 * is in pending state.
	 * 
	 * @param idCaseMergeFrom
	 * @param idCaseMergeTo
	 * @return boolean
	 */
	protected boolean isPending(final Long idCaseMergeFrom, final Long idCaseMergeTo) {
		boolean isPending = false;
		EventStageDto eventStageDto = caseMergeUpdateDao.searchCaseStage(idCaseMergeTo,
				ServiceConstants.EVENT_STATUS_PENDING, ServiceConstants.CAPS_PROG_CCL);
		if (TypeConvUtil.isNullOrEmpty(eventStageDto)) {
			EventStageDto eventStageFromDto = caseMergeUpdateDao.searchCaseStage(idCaseMergeFrom,
					ServiceConstants.EVENT_STATUS_PENDING, ServiceConstants.CAPS_PROG_CCL);
			if (!TypeConvUtil.isNullOrEmpty(eventStageFromDto)) {
				log.info("FROM Case is in pending state");
				isPending = true;
			}
		} else {
			log.info("TO Case is in pending state");
			isPending = true;
		}
		return isPending;
	}

	public Boolean getAssesmentStatusInOpenStages(Long idCase, String idUser, List<String> stageCodeList,
			String indAssesmentType) {
		log.info("Start - getAssesmentEventList method - caseMergeServiceImpl class");

		EventReq eventReq = new EventReq();
		eventReq.setbIndCaseSensitive(ServiceConstants.Y);
		eventReq.setReqFuncCd(ServiceConstants.UPDATE);
		eventReq.setPerfInd(ServiceConstants.Y);
		eventReq.setDataAcsInd(ServiceConstants.Y);
		eventReq.setPageNbr(1);
		eventReq.setPageSizeNbr(20);
		eventReq.setUserId(idUser);
		List<String> eventTypeList = new ArrayList<>();
		eventTypeList.add(ServiceConstants.CEVNTTYP_ASM);
		eventReq.setEventType(eventTypeList);
		eventReq.setStageType(stageCodeList);
		eventReq.setUlIdCase(idCase);
		eventReq.setUlIdEventPerson(0L);
		eventReq.setUlIdPerson(0L);
		Boolean status = Boolean.FALSE;
		EventDetailRes eventDetails = workloadService.getEventDetails(eventReq);
		/*
		 * for(EventListDto eventListDto:eventDetails.getEventSearchDto()){
		 * if(!eventListDto.getCdEventStatus().equalsIgnoreCase(ServiceConstants
		 * .APRV_STATUS_APPROVED)){ status = Boolean.TRUE; break; } }
		 */
		// Finding if any event has status as PEND or COMP or NEW then set
		// boolean to throw error message
		if (indAssesmentType.equalsIgnoreCase(ServiceConstants.SAFETY_ASSESMENT_CMS)
				&& eventDetails.getEventSearchDto().stream().filter(eventListDto -> !eventListDto.getCdEventStatus()
						.equalsIgnoreCase(ServiceConstants.EVENT_STATUS_COMP)).findAny().isPresent()) {
			status = Boolean.TRUE;
		}
		if (indAssesmentType
				.equalsIgnoreCase(
						ServiceConstants.CVS_FSNA_CMS)
				&& eventDetails.getEventSearchDto().stream().filter(eventListDto -> !eventListDto.getCdEventStatus()
						.equalsIgnoreCase(ServiceConstants.APRV_STATUS_APPROVED)).findAny().isPresent()) {
			status = Boolean.TRUE;
		}
		if (indAssesmentType.equalsIgnoreCase(ServiceConstants.FBSS_FSNA_CMS) && eventDetails.getEventSearchDto()
				.stream()
				.filter(eventListDto -> !eventListDto.getCdEventStatus()
						.equalsIgnoreCase(ServiceConstants.APRV_STATUS_APPROVED)
						|| !eventListDto.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENT_STATUS_COMP))
				.findAny().isPresent()) {
			status = Boolean.TRUE;
		}
		log.info("End - getAssesmentEventList method - caseMergeServiceImpl class");
		return status;

	}

	private CaseMergeSplitValidateRes getAssesmentEventListByStages(Long idCase,
			CaseMergeSplitValidateRes caseMergeRes) {
		log.info("Start - getAssesmentEventListByStages method - caseMergeServiceImpl class");
		List<StageEventDto> assessmentEventList = new ArrayList<>();
		StageEventDto stageEventDto = new StageEventDto();
		if (!ObjectUtils.isEmpty(idCase)) {
			stageEventDto.setIdCase(idCase);
			assessmentEventList = stageSearchEventDao.getAssessmentListByStageEvent(stageEventDto);
		}
		if (!CollectionUtils.isEmpty(assessmentEventList)) {
			if (assessmentEventList.stream().filter(
					assessmentEvent -> ((assessmentEvent.getCdTask().equalsIgnoreCase(ServiceConstants.TASK_7400)
							|| (assessmentEvent.getCdTask().equalsIgnoreCase(ServiceConstants.TASK_7410)))
							&& !assessmentEvent.getCdEventStatus()
									.equalsIgnoreCase(ServiceConstants.APRV_STATUS_APPROVED)))
					.findAny().isPresent()) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_FSNA_CVS);
			}
			if (assessmentEventList.stream().filter(
					assessmentEvent -> ((assessmentEvent.getCdTask().equalsIgnoreCase(ServiceConstants.TASK_7420))
							&& (!assessmentEvent.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENT_STATUS_COMP)
									&& !assessmentEvent.getCdEventStatus()
											.equalsIgnoreCase(ServiceConstants.APRV_STATUS_APPROVED))))
					.findAny().isPresent()) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_FSNA_FBSS);
			}
			if (assessmentEventList.stream().filter(
					assessmentEvent -> ((assessmentEvent.getCdTask().equalsIgnoreCase(ServiceConstants.TASK_7430)
							|| (assessmentEvent.getCdTask().equalsIgnoreCase(ServiceConstants.TASK_7440))
							|| (assessmentEvent.getCdTask().equalsIgnoreCase(ServiceConstants.TASK_7450)))
							&& !assessmentEvent.getCdEventStatus()
									.equalsIgnoreCase(ServiceConstants.EVENT_STATUS_COMP)))
					.findAny().isPresent()) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_SDM_SFTY);
			}
			// Error msg added for Risk ReAssessment
			if (assessmentEventList.stream().filter(assessmentEvent -> ((assessmentEvent.getCdTask()
					.equalsIgnoreCase(ServiceConstants.TASK_7470))
					&& !assessmentEvent.getCdEventStatus().equalsIgnoreCase(ServiceConstants.APRV_STATUS_APPROVED)))
					.findAny().isPresent()) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_RISKREASSMNT_NOT_APRV);
			}
			if (assessmentEventList.stream().filter(assessmentEvent -> ((assessmentEvent.getCdTask()
					.equalsIgnoreCase(ServiceConstants.TASK_7480))
					&& !assessmentEvent.getCdEventStatus().equalsIgnoreCase(ServiceConstants.APRV_STATUS_APPROVED)))
					.findAny().isPresent()) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_RISKREASSMNT_NOT_APRV);
			}
			if (assessmentEventList.stream().filter(assessmentEvent -> ((assessmentEvent.getCdTask()
					.equalsIgnoreCase(ServiceConstants.TASK_7490))
					&& !assessmentEvent.getCdEventStatus().equalsIgnoreCase(ServiceConstants.APRV_STATUS_APPROVED)))
					.findAny().isPresent()) {
				caseMergeRes.getErrorMsgNumberList().add(MSG_CFC_CMS_REUNIFCTIONASSMNT_NOT_APRV);
			}
		}
		log.info("End - getAssesmentEventListByStages method - caseMergeServiceImpl class");
		return caseMergeRes;

	}

	@Override
	public List<Long> getNeubusCaseList(Long idCase) {
		List<CaseMergeUpdateDto> caseMergeList = caseMergeUpdateDao.searchAllMergedCases(idCase);
		List<Long> mergeList  = new ArrayList<>();     //Contains merged case list from first scan
		List<Long> fromToIdsList  = new ArrayList<>(); //Contains final case list
		List<Long> tempList = new ArrayList<>();       //Contains temp case list
		List<Long> dependList  = new ArrayList<>();    //Contains dependency list before split found

		int splitOrPendMerge = 0;
		int totalRecCount = 0;

		//Generate merged case list and a running count of split/pending-merges ids.
		for (CaseMergeUpdateDto caseMergeRow : caseMergeList) {
			totalRecCount++;
			//Skip case ids that are split or in merge-pending
			if ( (("M".equalsIgnoreCase(caseMergeRow.getIndCaseMergePending())) ||
					((caseMergeRow.getIndCaseMergePending() == null || ("".equals(caseMergeRow.getIndCaseMergePending()))))
							&& (caseMergeRow.getDtCaseMergeSplitDate() != null)))
			{
				splitOrPendMerge++;
			}
			else
			{
				mergeList.add(caseMergeRow.getIdCaseMergeTo());
				mergeList.add(caseMergeRow.getIdCaseMergeFrom());
			}

		};
		//Following logic is executed only if list has splits or merge-pending
		if (splitOrPendMerge > 0)
		{
			splitOrPendMerge = 0;
			for (CaseMergeUpdateDto caseMergeRow : caseMergeList) {

				//If "to" id matches to main case id, include it in list. Otherwise, generate
				//a dependency list to be used in later processing.
				if (caseMergeRow.getIdCaseMergeTo().equals(idCase))
				{
					fromToIdsList.add(caseMergeRow.getIdCaseMergeTo());

					//Include "from" id if it is in merge or split-pending state
					if ( (("S".equalsIgnoreCase(caseMergeRow.getIndCaseMergePending())) && (caseMergeRow.getDtCaseMergeSplitDate() != null)) ||
							((caseMergeRow.getIndCaseMergePending() == null || ("".equals(caseMergeRow.getIndCaseMergePending())))
									&& (caseMergeRow.getDtCaseMergeSplitDate() == null)) )
					{
						//Build list of case ids directly merged into main case id
						fromToIdsList.add(caseMergeRow.getIdCaseMergeFrom());
					}
					else
					{
						splitOrPendMerge++;
					}
				}
				else
				{
					//Skip case ids that are in split or merge penging state
					if ( (("M".equalsIgnoreCase(caseMergeRow.getIndCaseMergePending()))) ||
							((caseMergeRow.getIndCaseMergePending() == null || ("".equals(caseMergeRow.getIndCaseMergePending())))
									&& (caseMergeRow.getDtCaseMergeSplitDate() != null)))
					{
						splitOrPendMerge++;
					}
					else
					{
						//Build list of "to" and "from" pairs not directly related to main case id
						dependList.add( caseMergeRow.getIdCaseMergeTo());
						dependList.add( caseMergeRow.getIdCaseMergeFrom());
					}
				}
			} //end of while

			if (splitOrPendMerge > 0)
			{
				if (!fromToIdsList.isEmpty())
				{
					HashSet h = new HashSet(fromToIdsList);
					fromToIdsList.clear();
					fromToIdsList.addAll(h);
				}

				//Scan dependList to find case ids merged into each id of fromToIdsList
				for (int i = 0; i < fromToIdsList.size();  i++ )
				{
					//Process dependency list of each case id merged into main case
					if (!fromToIdsList.get(i).equals(idCase))
					{
						tempList.clear();
						for (int j = 0; j < dependList.size();  j = j+2 )
						{
							//Build merged list
							if (fromToIdsList.get(i).equals(dependList.get(j)))
							{
								tempList.add(dependList.get(j + 1));
							}
						}
						fromToIdsList.addAll(tempList);
					}
				}
			}
		} //No splits found in original list
		else
		{
			fromToIdsList.addAll(mergeList);
		}

		if (!fromToIdsList.isEmpty())
		{
			HashSet h = new HashSet(fromToIdsList);
			fromToIdsList.clear();
			fromToIdsList.addAll(h);
		}

		return fromToIdsList;
	}

}
