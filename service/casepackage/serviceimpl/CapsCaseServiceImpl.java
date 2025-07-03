package us.tx.state.dfps.service.casepackage.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.service.UnitService;
import us.tx.state.dfps.service.casepackage.dao.CFMgnmtListDao;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementAUDDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileMgmtLocationDetailDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileMgmtUnitDetailFetchDao;
import us.tx.state.dfps.service.casepackage.dao.CaseMergeCustomDao;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionAUDDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkUpdIndEmpNewDao;
import us.tx.state.dfps.service.casepackage.dto.CFMgmntDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseSearchDto;
import us.tx.state.dfps.service.casepackage.dto.CapsEmailDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementOutDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtLocatioOutDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtLocationInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtUnitDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileMgmtUnitDetailOutDto;
import us.tx.state.dfps.service.casepackage.dto.CaseMergeDetailDto;
import us.tx.state.dfps.service.casepackage.dto.CasePersonListDto;
import us.tx.state.dfps.service.casepackage.dto.CasePersonListFormDto;
import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.service.casepackage.dto.DesignatedContactDto;
import us.tx.state.dfps.service.casepackage.dto.NytdYouthDto;
import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataInDto;
import us.tx.state.dfps.service.casepackage.dto.RecordRetentionDataOutDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdIndEmpNewInDto;
import us.tx.state.dfps.service.casepackage.dto.SurveyHistoryDto;
import us.tx.state.dfps.service.casepackage.service.CapsCaseService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.WorkingNarrativeDao;
import us.tx.state.dfps.service.common.request.ActedUponAssgnReq;
import us.tx.state.dfps.service.common.request.CFMgmntReq;
import us.tx.state.dfps.service.common.request.CaseFileMgmtReq;
import us.tx.state.dfps.service.common.request.CasePersonListReq;
import us.tx.state.dfps.service.common.request.CaseSearchInputReq;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.NytdReq;
import us.tx.state.dfps.service.common.request.RecordRetentionDataSaveReq;
import us.tx.state.dfps.service.common.request.WorkingNarrativeReq;
import us.tx.state.dfps.service.common.response.ActedUponAssgnRes;
import us.tx.state.dfps.service.common.response.CFMgmntRes;
import us.tx.state.dfps.service.common.response.CaseFileMgmtSaveRes;
import us.tx.state.dfps.service.common.response.CaseSearchRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.EmployeeMailRes;
import us.tx.state.dfps.service.common.response.NytdRes;
import us.tx.state.dfps.service.common.response.RecordRetentionSaveRes;
import us.tx.state.dfps.service.common.response.WorkingNarrativeRes;
import us.tx.state.dfps.common.dto.LawEnforcementAgencyInfo;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.OutlookUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.exception.TooManyRowsReturnException;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CasePersonListFormPrefillData;
import us.tx.state.dfps.service.outlook.AppointmentDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN20S Class
 * Description: This class is doing service Implementation for CaseManageService
 * Mar 24, 2017 - 7:50:07 PM
 */
@Service
@Transactional
public class CapsCaseServiceImpl implements CapsCaseService {
	@Autowired
	MessageSource messageSource;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	UnitService unitService;

	@Autowired
	CaseMergeCustomDao caseMergeCustomDao;

	@Autowired
	WorkingNarrativeDao workingNarrativeDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	CasePersonListFormPrefillData casePersonListFormPrefillData;

	// Ccmnc0d
	@Autowired
	CaseFileMgmtUnitDetailFetchDao caseFileMgmtUnitDetailFetchDao;

	// Ccmna5d
	@Autowired
	CaseFileMgmtLocationDetailDao caseFileMgmtLocationDetailDao;

	// Caud76d
	@Autowired
	CaseFileManagementAUDDao caseFileManagementAUDDao;

	// Ccmn52d
	@Autowired
	StagePersonLinkUpdIndEmpNewDao stagePersonLinkUpdIndEmpNewDao;

	@Autowired
	CFMgnmtListDao cFMgmntListDao;

	// Caud75d
	@Autowired
	RecordsRetentionAUDDao recordsRetentionAUDDao;

	@Autowired
	OutlookUtil outlookUtil;

	@Autowired
	FamilyPlanDao familyPlanDao;

	@Autowired
	StageDao stageDao;

	private static final Logger log = Logger.getLogger(CapsCaseServiceImpl.class);
	private static final String TR_ID_STR = "TransactionId :";
	public static final ResourceBundle EMAIL_CONFIG_BUNDLE = ResourceBundle.getBundle("EmailConfig");
	public static final String CVSREMOVAL_EMAIL_CONFIG_BASE = "CVSRemoval.emailId.";
	private static final String FPOS_EVALUATION_DUE_FOR = "FPOS Evaluation due for ";
	public static final String EXCEPTION_STRING_ONE = "Exception Occured while ";
	public static final String FSNA = "Subsequent FSNA Due ";
	public static final String FOR = " for ";

	/**
	 * getCaseByInput
	 * 
	 * Service Name- CCMN20S, DAM- CCMN13D
	 * 
	 * @param caseSearchReq
	 * @return
	 */
	@Override
	public List<CapsCaseSearchDto> getCaseByInput(CaseSearchInputReq caseSearchReq,ErrorDto errorDto) {
		List<CapsCaseSearchDto> capsCaseSearchDtoList = new ArrayList<>();
		Long countCase = 0L;
		boolean bLastNameOnly = true;
		String nmCaseLast = ServiceConstants.EMPTY_STRING;
		if (caseSearchReq != null) {
			if (!ObjectUtils.isEmpty(caseSearchReq.getIdPersonList())) {
				if (!ObjectUtils.isEmpty(caseSearchReq.getIdPersonList().get(0))
						&& caseSearchReq.getIdPersonList().get(0) != 0) {
					bLastNameOnly = false;
				}
			}
			if (!ObjectUtils.isEmpty(caseSearchReq.getIdCase())) {
				bLastNameOnly = false;
			}
			if (!ObjectUtils.isEmpty(caseSearchReq.getNmCase())) {
				if (caseSearchReq.getNmCase().substring(caseSearchReq.getNmCase().length() - 1)
						.equals(ServiceConstants.COMMA)) {
					nmCaseLast = caseSearchReq.getNmCase().substring(0, caseSearchReq.getNmCase().length() - 1);
				} else {
					bLastNameOnly = false;
				}
			}
			if (!ObjectUtils.isEmpty(caseSearchReq.getCdCaseProgram())) {
				bLastNameOnly = false;
			}
			if (!ObjectUtils.isEmpty(caseSearchReq.getCdCaseRegion())) {
				bLastNameOnly = false;
			}
			if (!ObjectUtils.isEmpty(caseSearchReq.getCdCaseCounty())) {
				bLastNameOnly = false;
			}
			if (!ObjectUtils.isEmpty(caseSearchReq.getAddrMailCodeCity())) {
				bLastNameOnly = false;
			}
			if (bLastNameOnly) {
				countCase = capsCaseDao.getCaseNumberByNmCaseLast(nmCaseLast);
			}
			if (countCase > ServiceConstants.MAX_SEARCH_ROWS) {
				errorDto.setErrorCode(ServiceConstants.MSG_TOO_MANY_ROWS_RETURNED);
			} else {
				capsCaseSearchDtoList = capsCaseDao.getCaseByInput(caseSearchReq);
			}
		}
		if (null != caseSearchReq) {
			log.info(TR_ID_STR + caseSearchReq.getTransactionId());
		}
		return capsCaseSearchDtoList;
	}

	/**
	 * caseSearch Service Name- CCMN20S
	 * 
	 * @param caseSearchReq
	 * @return
	 */
	@Override
	@Transactional
	public CaseSearchRes caseSearch(CaseSearchInputReq caseSearchReq) {
		CaseSearchRes caseSearchRes = new CaseSearchRes();
		List<CapsCaseSearchDto> capsCaseSearchDtoList = new ArrayList<>();
		List<CaseMergeDetailDto> caseMergeDetailDtoList = new ArrayList<>();
		List<String> stageTypes = new ArrayList<>();
		StageDto stageDto = new StageDto();
		Long idPerson = caseSearchReq.getIdPersonList().get(0);
		if (!ObjectUtils.isEmpty(caseSearchReq.getIdCase())) {
			// CMSC56D
			Long caseId = capsCaseDao.getCaseIdFromCloseCaseMerge(caseSearchReq.getIdCase());
			if (!ObjectUtils.isEmpty(caseId) && caseId != 0) {
				caseSearchReq.setIdCase(caseId);
			}
		}
		// CCMN13D
		ErrorDto errorDto = new ErrorDto();
		capsCaseSearchDtoList = this.getCaseByInput(caseSearchReq,errorDto);
		if (!(ObjectUtils.isEmpty(errorDto) && errorDto.getErrorCode() != 0)) {
			caseSearchRes.setErrorDto(errorDto);
		}
		if (!CollectionUtils.isEmpty(capsCaseSearchDtoList)) {
			for (CapsCaseSearchDto capsCaseSearchDto : capsCaseSearchDtoList) {
				if (!ObjectUtils.isEmpty(idPerson)) {
					// CCMNI5D
					stageTypes = capsCaseDao.getStageTypeByCasePersonId(capsCaseSearchDto.getIdCase(), idPerson);
					if (!ObjectUtils.isEmpty(stageTypes)) {
						if (stageTypes.size() > 0) {
							capsCaseSearchDto.setCdStagePersType(stageTypes.get(0));
						}
					}
				}

				List<CapsCaseSearchDto> caseSearchDtoList = new ArrayList<>();
				caseSearchDtoList = capsCaseDao.getCasebyStage(capsCaseSearchDto.getIdStage(),
						ServiceConstants.PRIMARY_ROLE_STAGE_OPEN, ServiceConstants.PRIMARY_ROLE_STAGE_CLOSED);
				if (!CollectionUtils.isEmpty(caseSearchDtoList)) {
					for (CapsCaseSearchDto caseSearchDto : caseSearchDtoList) {
						capsCaseSearchDto.setScrWorkerPrim(caseSearchDto.getNmPersonFull());
					}
				}
				// CCMNI6D
				stageDto = capsCaseDao.getMaxStageByCaseId(capsCaseSearchDto.getIdCase());
				if (!ObjectUtils.isEmpty(stageDto)) {
					if (!ObjectUtils.isEmpty(stageDto.getCdStage())) {
						capsCaseSearchDto.setIdStage(stageDto.getIdStage());
						capsCaseSearchDto.setCdStage(stageDto.getCdStage());
						capsCaseSearchDto.setScrWorkerPrim(stageDto.getNmPersonFull());
						capsCaseSearchDto.setCdStageType(stageDto.getCdStageType());
					}
				}
				//Added null condition check for cdCaseProgram - Warranty defect 12224
				if (!ObjectUtils.isEmpty(capsCaseSearchDto.getCdCaseProgram())
						&& capsCaseSearchDto.getCdCaseProgram().equals(ServiceConstants.CPGRMS_CPS)) {
					// CLSCB7D
					Long capsCaseId = capsCaseDao.getCaseIdByStageOverall(capsCaseSearchDto.getIdCase());
					if (!ObjectUtils.isEmpty(capsCaseId) && capsCaseId > ServiceConstants.ZERO_VAL) {
						capsCaseSearchDto.setScrIndCaseUTC(ServiceConstants.STRING_IND_Y);
					} else {
						capsCaseSearchDto.setScrIndCaseUTC(ServiceConstants.STRING_IND_N);
					}
				}
				if (!ObjectUtils.isEmpty(capsCaseSearchDto.getIdUnit())) {
					int uCntSvc = ServiceConstants.CNTSVC;
					int uCntFnc = ServiceConstants.CNTFNC;
					if (!ObjectUtils.isEmpty(caseSearchReq.getIdPersonList())) {
						while (uCntSvc < caseSearchReq.getIdPersonList().size()
								&& !caseSearchReq.getIdPersonList().get(uCntSvc).equals(ServiceConstants.ZERO_VAL)) {
							caseSearchReq.getIdPersonList().set(uCntFnc, caseSearchReq.getIdPersonList().get(uCntSvc));
							uCntSvc++;
							uCntFnc++;
						}
					}
					// CCMN04U
					if (unitService.unitAccess(capsCaseSearchDto.getIdUnit(), ServiceConstants.EMPTY_STRING,
							ServiceConstants.EMPTY_STRING, ServiceConstants.EMPTY_STRING,
							caseSearchReq.getIdPersonList())) {
						capsCaseSearchDto.setScrIndEmpStageAssign(ServiceConstants.STRING_IND_Y);
					} else {
						capsCaseSearchDto.setScrIndEmpStageAssign(ServiceConstants.STRING_IND_N);
					}
				} else {
					capsCaseSearchDto.setScrIndEmpStageAssign(ServiceConstants.STRING_IND_N);
				}
				// CLSC68D
				caseMergeDetailDtoList = caseMergeCustomDao.getCaseMergeByIdCaseMergeTo(capsCaseSearchDto.getIdCase());
				String pcScrIndStageMerged = ServiceConstants.EMPTY_STRING;
				if (!ObjectUtils.isEmpty(caseMergeDetailDtoList)) {
					for (CaseMergeDetailDto caseMergeDetailDto : caseMergeDetailDtoList) {
						if (!pcScrIndStageMerged.equals(ServiceConstants.STRING_IND_Y)) {
							if (null == caseMergeDetailDto.getIndCaseMergePending()
									|| caseMergeDetailDto.getIndCaseMergePending().equals(ServiceConstants.STRING_IND_N)
											&& caseMergeDetailDto.getDtCaseMergeSplit() == null) {
								pcScrIndStageMerged = ServiceConstants.STRING_IND_Y;
							}
						}
					}
					caseSearchRes.setRowQty(Integer.toString(capsCaseSearchDtoList.size()));

				} else {
					pcScrIndStageMerged = ServiceConstants.STRING_IND_N;
				}
				capsCaseSearchDto.setScrIndStageMerged(pcScrIndStageMerged);
			}
			if (capsCaseSearchDtoList.size() > caseSearchReq.getPageNbr() * caseSearchReq.getPageSizeNbr()) {
				caseSearchRes.setMoreDataInd(ServiceConstants.STRING_IND_Y);
			} else {
				caseSearchRes.setMoreDataInd(ServiceConstants.STRING_IND_N);
			}
		}
		setStageTypeForSVCAndClosedCases(capsCaseSearchDtoList);
		caseSearchRes.setCaseSearchDtoList(capsCaseSearchDtoList);
		return caseSearchRes;
	}

	/**
	 * Method Name: Method Description: Get the List of Nytd Youth History
	 * records.
	 * 
	 * @param nytdReq
	 * @return NytdRes
	 */
	@Override
	@Transactional
	public NytdRes retrieveNytdYouthHistory(NytdReq nytdReq) {
		NytdRes nytdEjbRes = new NytdRes();
		List<NytdYouthDto> nytdYouthDtoList = new ArrayList<>();
		List<DesignatedContactDto> designatedContactDtoList = new ArrayList<>();
		if (!TypeConvUtil.isNullOrEmpty(nytdReq)) {
			if (!TypeConvUtil.isNullOrEmpty(nytdReq.getNytdYouthID())) {
				nytdYouthDtoList = capsCaseDao.queryNytdYouth(nytdReq.getNytdYouthID());
				designatedContactDtoList = capsCaseDao.queryNytdYouthDesignatedContacts(nytdReq.getNytdYouthID());
			}
			if (!TypeConvUtil.isNullOrEmpty(nytdYouthDtoList)) {
				if (nytdYouthDtoList.size() > 0) {
					if (!TypeConvUtil.isNullOrEmpty(nytdYouthDtoList.get(0))) {
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDtoList.get(0).getStageName())) {
							nytdEjbRes.setStageName(nytdYouthDtoList.get(0).getStageName());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDtoList.get(0).getIdStage())) {
							nytdEjbRes.setStageID(nytdYouthDtoList.get(0).getIdStage());
						}
					}
					for (NytdYouthDto nytdYouthDto : nytdYouthDtoList) {
						SurveyHistoryDto surveyHistoryDto = new SurveyHistoryDto();
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getIdNytdHeader())) {
							surveyHistoryDto.setIdNytdHeader(nytdYouthDto.getIdNytdHeader());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getSurveyType())) {
							surveyHistoryDto.setSurveyType(nytdYouthDto.getSurveyType());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getSurveyStatus())) {
							surveyHistoryDto.setSurveyStatus(nytdYouthDto.getSurveyStatus());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getCdSurveyStatus())) {
							surveyHistoryDto.setCdSurveyStatus(nytdYouthDto.getCdSurveyStatus());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getDtSurveyDue())) {
							surveyHistoryDto.setDtSurveyDue(nytdYouthDto.getDtSurveyDue());
							surveyHistoryDto.setSurveyDueDate(DateUtils.dateString(nytdYouthDto.getDtSurveyDue()));
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getDtSurveyCompletion())) {
							surveyHistoryDto.setDtSurveyCompletion(nytdYouthDto.getDtSurveyCompletion());
							surveyHistoryDto.setSurveyCompletionDate(
									DateUtils.dateString(nytdYouthDto.getDtSurveyCompletion()));
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getDtPeriodStart())) {
							surveyHistoryDto.setDtPeriodStart(nytdYouthDto.getDtPeriodStart());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getDtPeriodEnd())) {
							surveyHistoryDto.setDtPeriodEnd(nytdYouthDto.getDtPeriodEnd());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getOutcomeReportingStatus())) {
							surveyHistoryDto.setOutcomeReportingStatus(nytdYouthDto.getOutcomeReportingStatus());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getOutcomeReportingStatusStr())) {
							surveyHistoryDto.setOutcomeReportingStatusStr(nytdYouthDto.getOutcomeReportingStatusStr());
						}
						if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getDtPeriodStart())
								&& !TypeConvUtil.isNullOrEmpty(nytdYouthDto.getDtPeriodEnd())) {
							surveyHistoryDto.setNytdReportingPeriod(DateUtils.getStandardNytdFormattedPeriod(
									nytdYouthDto.getDtPeriodStart(), nytdYouthDto.getDtPeriodEnd()));
						}
						if (!TypeConvUtil.isNullOrEmpty(surveyHistoryDto.getSurveyType())) {
							if (surveyHistoryDto.getSurveyType().equals(ServiceConstants.NYTD_YOUTH_BASE)) {
								nytdEjbRes.setBase(surveyHistoryDto);
								if (!TypeConvUtil.isNullOrEmpty(nytdYouthDto.getFollowUpDetermined())) {
									nytdEjbRes.setFollowUpDetermined(nytdYouthDto.getFollowUpDetermined());
								}
							}
							if (surveyHistoryDto.getSurveyType().equals(ServiceConstants.NYTD_YOUTH_19)) {
								nytdEjbRes.setFollowup19(surveyHistoryDto);
							}
							if (surveyHistoryDto.getSurveyType().equals(ServiceConstants.NYTD_YOUTH_21)) {
								nytdEjbRes.setFollowup21(surveyHistoryDto);
							}
						}
					}
				}
			}
			if (!TypeConvUtil.isNullOrEmpty(designatedContactDtoList)) {
				nytdEjbRes.setDesignatedContacts(designatedContactDtoList);
			}
		}
		nytdEjbRes.setTransactionId(nytdReq.getTransactionId());
		log.info(TR_ID_STR + nytdReq.getTransactionId());
		return nytdEjbRes;
	}

	/**
	 * Method returns information from Case table using idCase.
	 * 
	 * @param NytdEjbReq
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CapsCaseDto getCaseInfo(CommonHelperReq commonHelperReq) {
		return capsCaseDao.getCaseDetails(commonHelperReq.getIdCase());
	}

	/**
	 * Method Description:This service will save working narrative for a stage
	 * based on idEvent generated during contact initiation. Service Name: Save
	 * Working Narrative
	 * 
	 * @param workingNarrativeReq
	 * @return workingNarrativeRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public WorkingNarrativeRes saveWorkingNarrative(WorkingNarrativeReq workingNarrativeReq) {
		WorkingNarrativeRes workingNarrativeRes = new WorkingNarrativeRes();
		workingNarrativeRes = workingNarrativeDao.saveWorkingNarrative(workingNarrativeReq);
		workingNarrativeRes.setMessage(ServiceConstants.SUCCESS);
		return workingNarrativeRes;
	}

	/**
	 * Method Description: This service will get working narrative for a case
	 * based on idEvent generated during contact initiation. Service Name: Fetch
	 * Working Narrative
	 * 
	 * @param workingNarrativeReq
	 * @return workingNarrativeRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public WorkingNarrativeRes getWorkingNarrative(CommonHelperReq workingNarrativeReq) {
		List<ContactNarrativeDto> contactNarrativeDto = new ArrayList<>();
		WorkingNarrativeRes workingNarrativeRes = new WorkingNarrativeRes();
		CommonHelperRes idStages = this.getStageIdsList(workingNarrativeReq);
		List<Long> idStageList = new ArrayList<>();
		idStageList.addAll(idStages.getStageIdList());
		contactNarrativeDto = workingNarrativeDao.fetchContactForNarrative(workingNarrativeReq.getIdCase(),
				idStageList);
		List<CaseMergeDetailDto> caseMergeByIdCaseMergeTo = caseMergeCustomDao
				.getCaseMergeByIdCaseMergeTo(workingNarrativeReq.getIdCase());
		List<Long> idMergeStage = caseMergeByIdCaseMergeTo.stream().map(CaseMergeDetailDto::getIdCaseMergeStageFrom)
				.collect(Collectors.toList());
		if (null != idMergeStage) {
			for (ContactNarrativeDto stageId : contactNarrativeDto) {
				for (CaseMergeDetailDto mergeStageId : caseMergeByIdCaseMergeTo) {
					if (null != mergeStageId) {
						int bFound = (stageId.getIdContactStage()).compareTo(mergeStageId.getIdCaseMergeStageFrom());
						if (0 == bFound && (!TypeConvUtil.isNullOrEmpty(mergeStageId.getDtStageClose()))) {
							stageId.setIndMergeStage(ServiceConstants.STRING_IND_Y);
							break;
						} else {
							stageId.setIndMergeStage(ServiceConstants.STRING_IND_N);
						}
					}
				}
				if (null != stageId.getDtStageClose()) {
					stageId.setIndStageClose(ServiceConstants.STRING_IND_Y);
				} else {
					stageId.setIndStageClose(ServiceConstants.STRING_IND_N);
				}
			}
		}
		workingNarrativeRes.setContactNarrativeDto(contactNarrativeDto);
		return workingNarrativeRes;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public CommonHelperRes getEvents(CommonEventIdReq eventIdList) {
		CommonHelperRes res = new CommonHelperRes();
		List<EventDto> events = capsCaseDao.getEvents(eventIdList);
		if (!TypeConvUtil.isNullOrEmpty(events) && events.size() > 0) {
			res.setEventDtls(events);
		}
		return res;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public CommonHelperRes getStageIdsList(CommonHelperReq workingNarrativeReq) {
		List<Long> arStageIds = new ArrayList<Long>();
		Map<Long, String> priorStageList = new HashMap<>();
		List<Long> stageIds = workingNarrativeDao.spIdStage(workingNarrativeReq.getIdCase(),
				workingNarrativeReq.getIdStage(), workingNarrativeReq.getCdStage());
		stageIds.add(workingNarrativeReq.getIdStage());
		if (!StringUtils.isEmpty(stageIds) && stageIds.size() > 0) {
			priorStageList = workingNarrativeDao.getPriorStageList(stageIds);
		}
		Map<Long, String> arPriorStageList = new HashMap<>();
		if (!StringUtils.isEmpty(priorStageList.entrySet())) {
			for (Map.Entry<Long, String> idPriorStage : priorStageList.entrySet()) {
				if (ServiceConstants.A_R_STAGE.equalsIgnoreCase(idPriorStage.getValue())) {
					// call proc
					arStageIds = workingNarrativeDao.spIdStage(workingNarrativeReq.getIdCase(), idPriorStage.getKey(),
							idPriorStage.getValue());
					if (!StringUtils.isEmpty(arStageIds) && arStageIds.size() > 0) {
						arPriorStageList = workingNarrativeDao.getPriorStageList(arStageIds);
					}
				}
			}
		}
		Set<Long> stageIdList = new HashSet<>();
		stageIdList.add(workingNarrativeReq.getIdStage());
		stageIdList.addAll(stageIds);
		if (!StringUtils.isEmpty(priorStageList.keySet())) {
			stageIdList.addAll(priorStageList.keySet());
		}
		stageIdList.addAll(arStageIds);
		if (!StringUtils.isEmpty(arPriorStageList.keySet())) {
			stageIdList.addAll(arPriorStageList.keySet());
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setStageIdList(stageIdList);
		return commonHelperRes;

	}

	/**
	 * Method Description: This method is used to retrieve the information for
	 * Case Person List form by passing IdCase as input request
	 * 
	 * 
	 * @param casePersonListReq
	 * @return PreFillDataServiceDto
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getCasePersonListForm(CasePersonListReq casePersonListReq) {

		CasePersonListFormDto casePersonListFormDto = new CasePersonListFormDto();

		// Call CSEC02D to return the generic case information needed for all
		// forms
		GenericCaseInfoDto genericCaseInfoDto = new GenericCaseInfoDto();
		genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(casePersonListReq.getIdStage());
		casePersonListFormDto.setGenericCaseInfoDto(genericCaseInfoDto);

		// Call CLSCGDD to return the Case Person List
		List<CasePersonListDto> casePersonListDtolist = null;
		casePersonListDtolist = capsCaseDao.getCasePersonListDtls(genericCaseInfoDto.getIdCase());
		casePersonListFormDto.setCasePersonListDto(casePersonListDtolist);

		return casePersonListFormPrefillData.returnPrefillData(casePersonListFormDto);

	}

	/**
	 * Method Name: caseFileMgmtSave Method Description: This method is used to
	 * save caseFileMgmt detail section in case summary
	 * 
	 * @param caseFileMgmtReq
	 * @return @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaseFileMgmtSaveRes caseFileMgmtSave(CaseFileMgmtReq caseFileMgmtReq) {
		log.debug("Entering method caseFileMgmtSave in CapsCaseServiceImpl");
		CaseFileMgmtSaveRes caseFileMgmtSaveRes = new CaseFileMgmtSaveRes();
		long idOffice = 0;
		long idUnit = 0;
		CaseFileManagementInDto caseFileManagementInDto = new CaseFileManagementInDto();
		CaseFileManagementOutDto caseFileManagementOutDto = new CaseFileManagementOutDto();
		CaseFileMgmtUnitDetailInDto caseFileMgmtUnitDetailInDto = new CaseFileMgmtUnitDetailInDto();
		CaseFileMgmtUnitDetailOutDto caseFileMgmtUnitDetailOutDto = new CaseFileMgmtUnitDetailOutDto();
		CaseFileMgmtLocationInDto caseFileMgmtLocationInDto = new CaseFileMgmtLocationInDto();
		CaseFileMgmtLocatioOutDto caseFileMgmtLocatioOutDto = new CaseFileMgmtLocatioOutDto();
		if (ServiceConstants.PRS.equals(caseFileMgmtReq.getCdCaseFileOfficeType())) {
			caseFileMgmtUnitDetailInDto.setCdUnitRegion(caseFileMgmtReq.getCdOfficeRegion());
			caseFileMgmtUnitDetailInDto.setNbrUnit(caseFileMgmtReq.getNbrUnit());
			caseFileMgmtUnitDetailInDto.setCdUnitProgram(caseFileMgmtReq.getCdCaseProgram());
			if (caseFileMgmtReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_AFC)) {
				caseFileMgmtUnitDetailInDto.setCdUnitProgram(ServiceConstants.CPGRMS_APS);
			}
			if (caseFileMgmtReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_CCL)
					|| (caseFileMgmtReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_RCL))) {
				caseFileMgmtUnitDetailInDto.setCdUnitProgram(ServiceConstants.CPGRMS_LIC);
			}
			caseFileMgmtUnitDetailOutDto = caseFileMgmtUnitDetailFetchDao
					.caseFileMgmtUnitDetailFetch(caseFileMgmtUnitDetailInDto);
			if (caseFileMgmtUnitDetailOutDto != null) {
				// pServiceStatus.setSeverity(FND_SEVERITY_OK);
				// pServiceStatus.setExplan_code(FND_SUCCESS);
				idUnit = caseFileMgmtUnitDetailOutDto.getIdUnit();

				caseFileMgmtLocationInDto.setCdOfficeRegion(caseFileMgmtReq.getCdOfficeRegion());
				caseFileMgmtLocationInDto.setAddrMailCode(caseFileMgmtReq.getAddrMailCode());
				caseFileMgmtLocationInDto.setCdOfficeProgram(caseFileMgmtReq.getCdCaseProgram());
				if (caseFileMgmtReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_AFC)) {
					caseFileMgmtLocationInDto.setCdOfficeProgram(ServiceConstants.CPGRMS_APS);
				}

				if (caseFileMgmtReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_CCL)
						|| (caseFileMgmtReq.getCdCaseProgram().equalsIgnoreCase(ServiceConstants.CPGRMS_RCL))) {
					caseFileMgmtLocationInDto.setCdOfficeProgram(ServiceConstants.CPGRMS_LIC);
				}

				caseFileMgmtLocatioOutDto = caseFileMgmtLocationDetailDao
						.caseFileMgmtLocationDetail(caseFileMgmtLocationInDto);
				if (caseFileMgmtLocatioOutDto != null) {
					idOffice = caseFileMgmtLocatioOutDto.getIdOffice();
				}

			}
		}

		if (caseFileMgmtSaveRes != null) {
			caseFileManagementInDto.setReqFuncCd(caseFileMgmtReq.getReqFuncCd());
			caseFileManagementInDto.setIdCase(caseFileMgmtReq.getIdCase());
			if (ServiceConstants.PRS.equals(caseFileMgmtReq.getCdCaseFileOfficeType())) {
				caseFileManagementInDto.setIdOffice(idOffice);
				caseFileManagementInDto.setIdUnit(idUnit);
			}
			caseFileManagementInDto.setAddrCaseFileCity(caseFileMgmtReq.getAddrCaseFileCity());
			caseFileManagementInDto.setAddrCaseFileStLn1(caseFileMgmtReq.getAddrCaseFileStLn1());
			caseFileManagementInDto.setAddrCaseFileStLn2(caseFileMgmtReq.getAddrCaseFileStLn2());
			caseFileManagementInDto.setCdCaseFileOfficeType(caseFileMgmtReq.getCdCaseFileOfficeType());
			caseFileManagementInDto.setDtCaseFileArchCompl(caseFileMgmtReq.getDtCaseFileArchCompl());
			caseFileManagementInDto.setDtCaseFileArchElig(caseFileMgmtReq.getDtCaseFileArchElig());
			caseFileManagementInDto.setNmCaseFileOffice(caseFileMgmtReq.getNmCaseFileOffice());
			caseFileManagementInDto.setTxtCaseFileLocateInfo(caseFileMgmtReq.getTxtCaseFileLocateInfo());
			caseFileManagementInDto.setTxtAddSkpTrnInfo2(caseFileMgmtReq.getTxtAddSkpTrnInfo2());
			caseFileManagementInDto.setTsLastUpdate(caseFileMgmtReq.getTsLastUpdate());
			CommonHelperRes commonHelperRes = caseFileManagementAUDDao.caseFileManagementAUD(caseFileManagementInDto,
					caseFileManagementOutDto);
			if (!ObjectUtils.isEmpty(commonHelperRes) && !ObjectUtils.isEmpty(commonHelperRes.getErrorDto())) {
				caseFileMgmtSaveRes.setErrorDto(commonHelperRes.getErrorDto());
			}
		}

		return caseFileMgmtSaveRes;
	}

	/**
	 * Method Name: actedUponAssign Method Description: Service method to update
	 * the STAGE PERSON LINK table.
	 * 
	 * @param actedUponAssgnReq
	 * @return ActedUponAssgnRes @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ActedUponAssgnRes actedUponAssign(ActedUponAssgnReq actedUponAssgnReq) {
		log.debug("Entering method actedUponAssign in CapsCaseServiceImpl");
		ActedUponAssgnRes actedUponAssgnRes = new ActedUponAssgnRes();
		if (actedUponAssgnReq.getIdStage() != 0) {
			StagePersonLinkUpdIndEmpNewInDto stagePersonLinkUpdIndEmpNewInDto = new StagePersonLinkUpdIndEmpNewInDto();
			stagePersonLinkUpdIndEmpNewInDto.setIdPerson(actedUponAssgnReq.getIdPerson());
			stagePersonLinkUpdIndEmpNewInDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			stagePersonLinkUpdIndEmpNewInDto.setIdStage(actedUponAssgnReq.getIdStage());
			stagePersonLinkUpdIndEmpNewDao.updateEmployeeIndicator(stagePersonLinkUpdIndEmpNewInDto);
		}
		log.debug("Exiting method actedUponAssign in CapsCaseServiceImpl");
		return actedUponAssgnRes;
	}

	/**
	 * 
	 * Method Name: getCFMgmntInfo Method Description: Retrieve the Locating
	 * Information for CaseFile mgmt
	 * 
	 * @param cFMgmntReq
	 * @return CFMgmntRes @
	 */
	@Override
	@Transactional
	public CFMgmntRes getCFMgmntInfo(final CFMgmntReq cFMgmntReq) {
		CFMgmntRes cfMgmntRes = new CFMgmntRes();
		CFMgmntDto cfMgmntDto = new CFMgmntDto();
		cfMgmntRes = cFMgmntListDao.getCFMgmntInfo(cFMgmntReq);

		if (!TypeConvUtil.isNullOrEmpty(cfMgmntDto)) {
			cfMgmntRes.getCfMgmntDto().setSkpTrnInfoHashMap(cFMgmntListDao.getSkpTrnInfo(cFMgmntReq));

		} else {
			cfMgmntRes.getCfMgmntDto().setSkpTrnInfoHashMap(cFMgmntListDao.getSkpTrnInfo(cFMgmntReq));
		}

		// cfMgmntRes.setCfMgmntDto(cfMgmntDto);

		log.debug("in getCFMgmntInfo method" + cfMgmntRes);
		return cfMgmntRes;
	}

	/**
	 * Method Name: saveRecordRetention Method Description: save RecordRetention
	 * data
	 * 
	 * @param recordRetentionDataSaveReq
	 * @return RecordRetentionSaveRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RecordRetentionSaveRes saveRecordRetention(RecordRetentionDataSaveReq recordRetentionDataSaveReq) {
		log.debug("Entering method saveRecordRetention in CapsCaseServiceImpl");
		RecordRetentionSaveRes recordRetentionSaveRes = new RecordRetentionSaveRes();
		RecordRetentionDataInDto recordRetentionDataInDto = new RecordRetentionDataInDto();
		RecordRetentionDataOutDto recordRetentionDataOutDto = new RecordRetentionDataOutDto();

		recordRetentionDataInDto.setReqFuncCd(recordRetentionDataSaveReq.getReqFuncCd());
		recordRetentionDataInDto.setCdRecRtnRetenType(recordRetentionDataSaveReq.getCdRecRtnRetenType());
		recordRetentionDataInDto.setTxtRecRtnDstryDtRsn(recordRetentionDataSaveReq.getTxtRecRtnDstryDtRsn());
		recordRetentionDataInDto.setIdCase(recordRetentionDataSaveReq.getIdCase());
		recordRetentionDataInDto.setDtRecRtnDstryActual(recordRetentionDataSaveReq.getDtRecRtnDstryActual());
		recordRetentionDataInDto.setDtRecRtnDstryElig(recordRetentionDataSaveReq.getDtRecRtnDstryElig());
		recordRetentionDataInDto.setTsLastUpdate(recordRetentionDataSaveReq.getTsLastUpdate());
		recordsRetentionAUDDao.recordsRetentionAUD(recordRetentionDataInDto, recordRetentionDataOutDto);

		log.debug("Exiting method saveRecordRetention in CapsCaseServiceImpl");
		return recordRetentionSaveRes;
	}

	/**
	 * 
	 * Method Description: Fetch Narrative of old Contact and copy it to new
	 * Contact Service Name: createNewNarrativeUsing
	 * 
	 * @param workingNarrativeReq
	 * @return workingNarrativeRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public WorkingNarrativeRes createNewNarrativeUsing(WorkingNarrativeReq workingNarrativeReq) {

		return workingNarrativeDao.saveNewUsingWorkingNarrative(workingNarrativeReq);
	}

	/**
	 * @Override
	 * 
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String sendEmployeeEmail(Long idEvent, CapsEmailDto capsEmailDto, String hostName) {
		EmployeeMailRes employeeMailRes = new EmployeeMailRes();
		String message = ServiceConstants.EMPTY_STRING;
		String cdStage = capsEmailDto.getCdStage();
		/* Calling the DAO method to get the email DTO list */
		List<EmailDetailsDto> emailDetailsDtoList = familyPlanDao.fetchEmployeeEmail(idEvent, cdStage);
		/* If the List id not empty then call the send appointment function. */
		if (!ObjectUtils.isEmpty(emailDetailsDtoList)) {
			employeeMailRes.setReciepentDetailList(emailDetailsDtoList);
			message = sendAppointment(employeeMailRes, capsEmailDto, hostName);
		}
		/*
		 * Calling the send appointment method to generate and send the
		 * appointment to the users
		 */

		return message;
	}

	/**
	 * 
	 * Method Name: sendAppointment Method Description: This Method is used for
	 * Send The Appointment Invite
	 * 
	 * @param employeeMailRes
	 * @param capsEmailDto
	 * @param hostName
	 * @return
	 */
	private String sendAppointment(EmployeeMailRes employeeMailRes, CapsEmailDto capsEmailDto, String hostName) {
		String message = ServiceConstants.OUTLOOK_SUCCESS;
		/* Creating a Appointment DTO for to be sent to outlook util. */
		AppointmentDto appointmentDto = new AppointmentDto();
		appointmentDto.setAppointmentBody(ServiceConstants.EMPTY_STRING);
		Calendar cal = Calendar.getInstance();
		/*
		 * Now use review date to calculate the invite time.
		 */
		cal.setTime(capsEmailDto.getDtNxtReviewDate());
		/*
		 * Subtracting 30 day's to the review date
		 */
		cal.add(Calendar.DATE, ServiceConstants.OUTLOOK_DAYS_MINUS_30);
		Date startDate = cal.getTime();
		/*
		 * Below logic is to generate the end time of the day
		 */
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, ServiceConstants.OUTLOOK_TIME_HOURS_23);
		cal.set(Calendar.MINUTE, ServiceConstants.OUTLOOK_TIME_59);
		cal.set(Calendar.SECOND, ServiceConstants.OUTLOOK_TIME_59);
		cal.set(Calendar.MILLISECOND, ServiceConstants.OUTLOOK_TIME_59);
		Date endDate = cal.getTime();
		appointmentDto.setDtStartDate(startDate);
		appointmentDto.setDtEndDate(endDate);
		appointmentDto.setIndAllDayEvent(Boolean.TRUE);
		if (ServiceConstants.FSU_FRE.equals(capsEmailDto.getCdStage())
				|| ServiceConstants.CSTAGES_FSU.equals(capsEmailDto.getCdStage())
				|| ServiceConstants.CSTAGES_FRE.equals(capsEmailDto.getCdStage())) {
			appointmentDto.setRemainderMins(10080);
		}

		if (ServiceConstants.CSTAGES_FPR.equals(capsEmailDto.getCdStage())) {
			appointmentDto.setAppointmentSubject(FPOS_EVALUATION_DUE_FOR + capsEmailDto.getNmParticipant());
		} else if (ServiceConstants.FSU_FRE.equals(capsEmailDto.getCdStage())
				|| ServiceConstants.CSTAGES_FSU.equals(capsEmailDto.getCdStage())
				|| ServiceConstants.CSTAGES_FRE.equals(capsEmailDto.getCdStage())) {
			appointmentDto.setAppointmentSubject(
					FSNA + DateUtils.stringDt(startDate) + FOR + capsEmailDto.getNmParticipant());
		}
		/*
		 * Below loop is used to generate the multiple appointment DTO to be
		 * sent to outlook UTIL to generate and trigger the appointment for
		 * multiple users
		 */

		for (EmailDetailsDto emailDetailsDto : employeeMailRes.getReciepentDetailList()) {
			List<String> emailAddress = new ArrayList<>();
			/*
			 * Below condition is added to prevent the mail triggering to actual
			 * user during multiple testing phases
			 */
			if (ServiceConstants.PROD.equals(hostName)) {
				emailAddress.add(emailDetailsDto.getEmailAddress());
			}
			/*
			 * Remove below condition if test email address needs to be removed
			 */
			else {
				String emailProperty = CVSREMOVAL_EMAIL_CONFIG_BASE + hostName;
				emailAddress.add(EMAIL_CONFIG_BUNDLE.getString(emailProperty));
			}

			appointmentDto.setReceiverEmailAddress(emailAddress);

		}
		/*
		 * Calling the outlook utility method to send the appointment meeting
		 * also we are calling the outlookExchange service method to get the the
		 * Exchange service passing the service account userName and Password to
		 * get the service.
		 */
		try{
			message = outlookUtil.sendAppointment(appointmentDto, outlookUtil.getOutlookExchangeService(null, null));
		}catch (Exception e) {
			message = ServiceConstants.OUTLOOK_FAILURE;
		}
		
		return message;
	}

	/**
	 * Method Name: saveLEInvolvmentAgencyInfo Method Description: save LE Involvement Agency Info
	 * @param agencyInfo
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonStringRes saveLEInvolvmentAgencyInfo(LawEnforcementAgencyInfo agencyInfo) {
		log.debug("Entering method saveLEAgencyInfo in CapsCaseServiceImpl");
		CommonStringRes commonStringRes = new CommonStringRes();
		capsCaseDao.updateLEInvolvmentAgencyInfo(agencyInfo);
		log.debug("Exiting method saveLEAgencyInfo in CapsCaseServiceImpl");
		commonStringRes.setCommonRes(ServiceConstants.SAVE_SUCCESS);
		return commonStringRes;
	}

	/**
	 * Method helps to get the Stage Type only for closed cases and SVC stage types
	 *
	 * @param capsCaseSearchDtoList - case search list data
	 */
	private void setStageTypeForSVCAndClosedCases(List<CapsCaseSearchDto> capsCaseSearchDtoList) {
		//artf268253- Obtain the stage type for SVC stages that are closed cases, allowing the hyperlink to be enabled when worker search the case.
		capsCaseSearchDtoList.stream().filter(a -> CodesConstant.CSTAGES_SVC.equalsIgnoreCase(a.getCdStage()) && null != a.getDtCaseClosed()).forEach(a -> {
			StageDto s = stageDao.getStageById(a.getIdStage());
			a.setCdStageType(s.getCdStageType());
		});
	}

}
