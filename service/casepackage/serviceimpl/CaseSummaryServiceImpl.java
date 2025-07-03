package us.tx.state.dfps.service.casepackage.serviceimpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.casemanagement.dto.ProviderPlacementDto;
import us.tx.state.dfps.common.domain.CgNotifFileUpload;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.casemanagement.dto.NotificationFileDto;
import us.tx.state.dfps.common.domain.CgNotifFileUploadDtl;
import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.caregiver.service.CaregiverNotificationService;
import us.tx.state.dfps.service.casemanagement.dao.PCSPPlcmntDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CaseStageSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.CaseSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.MuleEsbResponseDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.dto.ToDoDetailDto;
import us.tx.state.dfps.service.casepackage.service.CaseSummaryService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.IncomingDetailDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.intake.dto.IncomingDetailDto;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.*;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import us.tx.state.dfps.service.common.ServiceConstants;
/**
 * 07/05/2021 kurmav Artifact artf190991 : Hide PCSP tab for FAD and KIN stages
 */
@Service
@Transactional
//@Import({ContactSearchListBusinessDelegate.class,AllegationListBusinessDelegate.class, CacheAdapter.class})
public class CaseSummaryServiceImpl implements CaseSummaryService {

	@Autowired
	private CaseSummaryDao caseSummaryDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private IncomingDetailDao incomingDetailDao;

	@Autowired
	private CloseStageCaseService closeStageCaseService;

	@Autowired
	CloseOpenStageService closeOpenStageService;

	@Autowired
	PostEventService postEventService;

	@Autowired
	TodoDao todoDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	StageWorkloadDao stageWorkloadDao;


	@Autowired
	MobileUtil mobileUtil;

	@Autowired
	EmployeeDao employeeDao;


	@Autowired
	private CodesDao codesDao;


	@Autowired
	PCSPPlcmntDao pCSPPlcmntDao;

	@Autowired
    CaregiverNotificationService caregiverNotificationService;

	private static final Logger log = Logger.getLogger(CaseSummaryServiceImpl.class);

	public CaseSummaryServiceImpl() {
		// Default Constructor
	}


	/**
	 * This method will take stage id as a parameter and check if the stage has the start date before the APS Release Date.
	 * If the stage start date is prior to the APS Release Date, it returns true, else false.
	 *
	 * @param stageStartDate
	 * @return
	 */
	@Override
	public Boolean checkPreSingleStageByStartDate(Date stageStartDate) {
		Date relDate = codesDao.getAppRelDate(ServiceConstants.CRELDATE_NOV_2020_APS);
		return !ObjectUtils.isEmpty(stageStartDate) && DateUtils.isBefore(stageStartDate, relDate);
	}

	/**
	 *
	 * Method Description: This Method is designed to retrieve case information as
	 * well as a list of stages associated with that case. It receives ID CASE. It
	 * returns data from the CASE, PERSON, PERSON PHONE, STAGE, STAGE PERSON LINK
	 * tables.
	 *
	 * @param rtvCaseSummaryReq
	 * @return caseSummaryRes @ Tuxedo Service Name:CCMN37S
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CaseSummaryRes getCaseSummary(CaseSummaryReq rtvCaseSummaryReq) {
		CaseSummaryRes caseSummaryRes = new CaseSummaryRes();
		List<CaseStageSummaryDto> caseStageInfo = new ArrayList<>();
		Boolean FL = Boolean.FALSE;
		if (!ObjectUtils.isEmpty(rtvCaseSummaryReq.getIdCase()) && rtvCaseSummaryReq.getIdCase() != 0l) {
			CaseSummaryDto caseSummary = new CaseSummaryDto();
			caseSummary = caseSummaryDao.getCaseInfo(rtvCaseSummaryReq.getIdCase());
			if (null == rtvCaseSummaryReq.getCdStageProgram()) {
				rtvCaseSummaryReq.setCdStageProgram(ServiceConstants.CPGRMS_CPS);
			}
			if (rtvCaseSummaryReq.getCdStageProgram().equalsIgnoreCase(ServiceConstants.APS_INVST_DETAIL)) {
				caseStageInfo = caseSummaryDao.getCaseStageAPSInfo(rtvCaseSummaryReq.getIdCase());
			} else if (rtvCaseSummaryReq.getCdStageProgram().equalsIgnoreCase(ServiceConstants.LICENSING_CCL)
					|| rtvCaseSummaryReq.getCdStageProgram().equalsIgnoreCase(ServiceConstants.LICENSING_RCL)) {
				caseStageInfo = caseSummaryDao.getCaseStageCCLInfo(rtvCaseSummaryReq.getIdCase());
			} else if (rtvCaseSummaryReq.getCdStageProgram().equalsIgnoreCase(ServiceConstants.FACILITY_INVST_DTL)) {
				caseStageInfo = caseSummaryDao.getCaseStageDTLInfo(rtvCaseSummaryReq.getIdCase());
			} else {
				caseStageInfo = caseSummaryDao.getCaseStageCPSInfo(rtvCaseSummaryReq.getIdCase());
			}
			for (CaseStageSummaryDto caseStage : caseStageInfo) {
				caseStage.setPreSingleStage(checkPreSingleStageByStartDate(caseStage.getDtStageStart()));
				List<CaseStageSummaryDto> personPhone = caseSummaryDao
						.getCaseStagePhonePersonInfo(caseStage.getIdPerson());
				//Defect#12619 only show primary active business phone numbers
				if (CollectionUtils.isNotEmpty(personPhone)) {
					caseStage.setNmPersonFull(personPhone.get(0).getNmPersonFull());
					caseStage.setNbrPhone(personPhone.get(0).getNbrPhone());
					boolean validNoEndDate = (TypeConvUtil.isNullOrEmpty(personPhone.get(0).getEndDate())) ? true : false;
					boolean noEndDate = false;
					if (!TypeConvUtil.isNullOrEmpty(personPhone.get(0).getEndDate())) {
						String endDatStr = DateUtils.stringDate(personPhone.get(0).getEndDate());
						noEndDate = ServiceConstants.MAX_DATE_LOWERSTRING.equals(endDatStr) ? true : false;
				}
					if ((ServiceConstants.INV_Stage).equalsIgnoreCase(caseStage.getCdStage())) {
						Long idPriorStage = caseSummaryDao.getStageMergeInfo(caseStage.getIdStage());
						caseStage.setIdPriorStage(idPriorStage);
					} else {
						caseStage.setIdPriorStage(ServiceConstants.Zero_Value);
					}
					if ((ServiceConstants.INT_Stage).equalsIgnoreCase(caseStage.getCdStage())) {
						CaseStageSummaryDto dtIncomingCall = caseSummaryDao.getIncomingDetail(caseStage.getIdStage());
						if (TypeConvUtil.isNullOrEmpty(dtIncomingCall)) {
							caseStage.setDtIncomingCall(caseStage.getDtStageStart());
						} else {
							caseStage.setDtIncomingCall(dtIncomingCall.getDtIncomingCall());
							if (null != dtIncomingCall.getTmIncmgCall()) {
								caseStage.setTmIncmgCall(dtIncomingCall.getTmIncmgCall());
							}
						}
					} else {
						caseStage.setDtIncomingCall(null);
						caseStage.setTmIncmgCall(ServiceConstants.NULL_VALUE);
					}
					if (!(ServiceConstants.Close_To_Merge).equalsIgnoreCase(caseStage.getCdStageReasonClosed())) {
						caseStage.setIndStageMerged(ServiceConstants.Stage_Merge);
					} else {
						caseStage.setIndStageMerged(ServiceConstants.Stage_Not_Merge);
					}
					Date DtStageClose = caseStage.getDtStageClose();
					if (!ObjectUtils.isEmpty(DtStageClose)
							&& ((ServiceConstants.NULL_JAVA_DATE_DATE.compareTo(DtStageClose) == 0)
							|| (ServiceConstants.NULL_MIN_JAVA_DATE_DATE.compareTo(DtStageClose) == 0))) {
						caseStage.setDtStageClose(null);
					}
					//Checking for stage is checked out to MPS or not
					checkForMPSCheckoutDetails(caseStage);

				}
				// Get list of cases for RCL and CCL stages in the request
				List<Long> rcciMrefCandidateIdList = new ArrayList<>();
				if (rtvCaseSummaryReq.getCdStageProgram().equalsIgnoreCase(ServiceConstants.LICENSING_CCL)
						|| rtvCaseSummaryReq.getCdStageProgram().equalsIgnoreCase(ServiceConstants.LICENSING_RCL)) {
					rcciMrefCandidateIdList.add(rtvCaseSummaryReq.getIdCase());
				}

				// do query to find RCCI Mref data for those stages' cases
				if (!ObjectUtils.isEmpty(rcciMrefCandidateIdList)) {
					List<RcciMrefDto> mrefDtoList = stageWorkloadDao.getRcciMrefDataByCaseList(rcciMrefCandidateIdList);

					// apply RCCI Mref data to result. Since we searched by case, all stages have the same case.
					if (!ObjectUtils.isEmpty(mrefDtoList)) {
						caseStageInfo.stream().forEach(currWkld -> {
							RcciMrefDto currMref = mrefDtoList.get(0);
							if (currMref != null && ServiceConstants.INV_Stage.equals(currWkld.getCdStage())) {
								currWkld.setRcciMrefCount(applyRcciMrefThresholds(currMref.getRcciMrefCnt(),
										currWkld.getCdStageProgram(),
										currMref.getNbrRsrcFacilCapacity(),
										currMref.getCdRsrcFacilType()));
								currWkld.setNbrRsrcFacilCapacity(currMref.getNbrRsrcFacilCapacity());
								currWkld.setCdRsrcFacilType(currMref.getCdRsrcFacilType());
							}
						});
					}
				}
				if (!CollectionUtils.isEmpty(caseStageInfo)) {
					caseSummaryRes.setCaseStageSummaryDto(caseStageInfo);
			}
				if (!mobileUtil.isMPSEnvironment()) {
					FL = caseSummaryDao.checkChildFt(rtvCaseSummaryReq.getIdCase());
					if (!TypeConvUtil.isNullOrEmpty(caseSummary)) {
						if (FL == Boolean.TRUE) {
							caseSummary.setIndChildFatality("Y");
						} else {
							caseSummary.setIndChildFatality("N");
						}
					}
				}
				caseSummaryRes.setCaseSummaryDto(caseSummary);
			}
		}
			caseSummaryRes.setTransactionId(rtvCaseSummaryReq.getTransactionId());
			log.info("TransactionId :" + rtvCaseSummaryReq.getTransactionId());
			return caseSummaryRes;

	}


	/**
	 * 
	 * Method Description: This method will check to see if there is a AFC stage in
	 * Pending Approval Status for a case.
	 * 
	 * @paramulIdCase - Case ID
	 * @returnBoolean Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public CommonHelperRes getAFCPendingStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (caseSummaryDao.getAFCPendingCount(commonHelperReq.getIdCase()) > 0) {
			commonHelperRes.setAFCPendingStatus(Boolean.TRUE);
		} else {
			commonHelperRes.setAFCPendingStatus(Boolean.FALSE);
		}
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Description: Method to determine if Stage is Valid for SDM Risk
	 * Assessment.
	 * 
	 * @paramulIdStage - Stage ID
	 * @returnBoolean Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public CommonHelperRes isSDMInvRiskAssmt(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		Long idStage = commonHelperReq.getIdStage();
		Date dtStageStart = null;
		if (null != commonHelperReq.getDtStageStart()) {
			dtStageStart = commonHelperReq.getDtStageStart();
		}
		if (null == dtStageStart) {
			SelectStageDto selectStageDto = caseSummaryDao.getStage(idStage, ServiceConstants.STAGE_CURRENT);
			//Defect 13000, Included the A-R stage as well. Because this functionality is applicable to A-R also.
			if (null != selectStageDto.getCdStageProgram()
					&& selectStageDto.getCdStageProgram().equals(ServiceConstants.CPGRMS_CPS)
					&& null != selectStageDto.getCdStage()
					&& (selectStageDto.getCdStage().equals(ServiceConstants.CSTAGES_INV)
					|| selectStageDto.getCdStage().equals(ServiceConstants.CSTAGES_AR))) {
				dtStageStart = selectStageDto.getDtStartDate();
			} else
				commonHelperRes.setIsSDMInvRiskAssmt(Boolean.FALSE);
		}
		if (null != dtStageStart) {
			if (dtStageStart.after(ServiceConstants.AUG_15_IMPACT_DATE)) {
				commonHelperRes.setIsSDMInvRiskAssmt(Boolean.TRUE);
			} else {
				if (caseSummaryDao.sdmEventExists(commonHelperReq.getIdStage()) > 0) {
					commonHelperRes.setIsSDMInvRiskAssmt(Boolean.TRUE);
				} else {
					commonHelperRes.setIsSDMInvRiskAssmt(Boolean.FALSE);
				}
			}
		}
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Description: This function returns true if the case is currently
	 * checked out Checked out (OT or AI) for the given Stage Id.
	 * 
	 * @paramulIdStage - Stage ID
	 * @returnBoolean Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public CommonHelperRes getCaseCheckoutStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (caseSummaryDao.getCaseCheckoutPerson(commonHelperReq.getIdStage()) > 0) {
			commonHelperRes.setCaseCheckoutStatus(Boolean.TRUE);
		} else {
			commonHelperRes.setCaseCheckoutStatus(Boolean.FALSE);
		}
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Description: Returns details for the stage prior to the given stage as
	 * indicated by the STAGE_LINK table.
	 * 
	 * @paramidStage - Stage ID
	 * @returnSelectStageDto Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public SelectStageDto getPriorStage(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getStage(commonHelperReq.getIdStage(), ServiceConstants.STAGE_PRIOR);
	}

	/**
	 * 
	 * Method Description: Returns details for the stage prior to the given stage as
	 * indicated by the STAGE_LINK table.
	 * 
	 * @paramidStage - Stage ID
	 * @returnSelectStageDto Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public SelectStageDto getLaterStage(CommonHelperReq commonHelperReq) {
		// Note - For CPS, when the A-R stage id is passed, and it has FPR & INV open then this method throws error
		return caseSummaryDao.getStage(commonHelperReq.getIdStage(), ServiceConstants.STAGE_LATER);
	}

	/**
	 * 
	 * Method Description: Returns information about a stage..
	 * 
	 * @paramidStage - Stage ID
	 * @returnSelectStageDto Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public SelectStageDto getStage(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getStage(commonHelperReq.getIdStage(), ServiceConstants.STAGE_CURRENT);
	}

	/**
	 * 
	 * Method Description: Method will fetch the last update date for the passed
	 * Entity Class
	 * 
	 * @paramentityClass - The Entity Class to fetch
	 * @paramprimaryKey - Primary Key for the Entity Class
	 * @paramentityID - value for the Primary Key
	 * @returnDate - Last Update Date of the Entity and Primary Key Passed Tuxedo
	 *             Service Name: NA
	 */
	@Override
	@Transactional
	public CommonHelperRes getLastUpdateDate(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setLastUpdateDate(caseSummaryDao.getLastUpdateDate(commonHelperReq.getEntityClass(),
				commonHelperReq.getPrimaryKey(), commonHelperReq.getEntityID()));
		return commonHelperRes;
	}

	/**
	 * Method Description: This method returns if all questions have been answered.
	 * Service Name: CpsInvCnclsn
	 * 
	 * @param cpsInvCnclsnReq
	 * @return CpsInvCnclsnRes @
	 */
	@Transactional
	public CpsInvCnclsnRes getQuesAnsrd(CpsInvCnclsnReq cpsInvCnclsnReq) {
		CpsInvCnclsnRes cpsInvCnclsnRes = new CpsInvCnclsnRes();
		Boolean allegCount = Boolean.FALSE;
		int countAlleg = caseSummaryDao.getCountForCpsInvCnclsn(cpsInvCnclsnReq.getIdStage());
		if (countAlleg > 0) {
			allegCount = Boolean.TRUE;
		}
		cpsInvCnclsnRes.setQuestionAnsrd(allegCount);
		return cpsInvCnclsnRes;
	}

	/**
	 * Method Description: Method to determine if user has access to modify PCSP
	 * page.
	 * 
	 * Service Name - NA (Util Method hasPCSPAccess)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 * @throwsInvalidRequestException
	 * 
	 */
	@Override
	@Transactional
	public CommonHelperRes hasPCSPAccess(CommonHelperReq commonHelperReq) {
		boolean hasPCSPAccess = false;
		String cdStage = ServiceConstants.EMPTY_STRING;
		Long stageId = commonHelperReq.getIdStage();
		if (stageId > 0) {
			SelectStageDto stageDto = caseSummaryDao.getStage(commonHelperReq.getIdStage(), "Current");
			if (null != stageDto) {
				cdStage = stageDto.getCdStage();
				if (!TypeConvUtil.isNullOrEmpty(stageDto.getCdStageProgram()) && isPCSPAssessment(stageDto.getIdCase())
						&& ServiceConstants.ARCHITECTURE_CONS_N.equals(stageDto.getIndStageClose())
						&& !TypeConvUtil.isNullOrEmpty(cdStage)
						&& ServiceConstants.CPGRMS_CPS.equals(stageDto.getCdStageProgram())
						&& (cdStage.equals(ServiceConstants.CSTAGES_AR) || cdStage.equals(ServiceConstants.CSTAGES_INV)
								|| cdStage.equals(ServiceConstants.CSTAGES_FPR)
								|| cdStage.equals(ServiceConstants.CSTAGES_FSU)
								|| cdStage.equals(ServiceConstants.CSTAGES_FRE))) {
					hasPCSPAccess = true;
				}
			}
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setHasPCSPAccess(hasPCSPAccess);
		return commonHelperRes;
	}

	/**
	 *
	 * Method Description: Method to determine if PCSP tab should be displayed for a
	 * case. All open cases as of 3/20/2016 and cases opened after 3/20/2016 will
	 * have PCSP tab.
	 * 
	 * @param caseId
	 * @return boolean isPCSPAssessment
	 * @throws Exception
	 */
	@Transactional
	public Boolean isPCSPAssessment(Long caseId) {
		Boolean isPCSPAssessment = Boolean.FALSE;
		Date mar16RelDt = null;
		CaseSummaryDto caseDetails = caseSummaryDao.getCaseDetails(caseId);
		if (null != caseDetails && ServiceConstants.CPGRMS_CPS.equals(caseDetails.getCdCaseProgram())) {
			Date caseClosedDate = caseDetails.getDtCaseClosed();
			if (null == caseClosedDate)
				isPCSPAssessment = Boolean.TRUE;
			else {
				try {
					SimpleDateFormat formatter = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_ddMMYYYY);
					mar16RelDt = formatter.parse(ServiceConstants.mar16RelDt);
				} catch (ParseException e) {
					throw new ServiceLayerException("Exception Occured in CaseSummaryServiceImpl.isPCSPAssessment:",
							e.getMessage(), null);
				}
				if (caseClosedDate.before(mar16RelDt)) {
					isPCSPAssessment = Boolean.FALSE;
				} else {
					isPCSPAssessment = Boolean.TRUE;
				}
			}
		}
		return isPCSPAssessment;
	}

	/**
	 * Method Description: Method to look at the case to determine if the given user
	 * has access to any open or closed stage.
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 * @throwsInvalidRequestException
	 * 
	 */
	@Override
	@Transactional
	public CommonHelperRes hasStageAccessToAnyStage(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (null != commonHelperReq.getIndStageClose()) {
			commonHelperRes.setHasStageAccessToAnyClosedStage(caseSummaryDao.hasStageAccessToAnyStage(
					commonHelperReq.getIndStageClose(), commonHelperReq.getIdCase(), commonHelperReq.getUserID()));
		} else {
			commonHelperRes.setHasStageAccessToAnyOpenStage(caseSummaryDao.hasStageAccessToAnyStage(
					commonHelperReq.getIndStageClose(), commonHelperReq.getIdCase(), commonHelperReq.getUserID()));
		}
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Description: This function retuns the Primary (PR) or Seconday (SE)
	 * Worker that Checked out (OT or AI) the given Stage Id.
	 * 
	 * @paramulIdStage - Stage ID
	 * @returnLong Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public CommonHelperRes getCaseCheckoutPerson(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setCaseCheckoutPerson(caseSummaryDao.getCaseCheckoutPerson(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Description: Returns a the event and todo id's for an approval given
	 * an event from the stage.
	 * 
	 * @paramcommonHelperReq
	 * @returnToDoDetailDto Tuxedo Service Name: NA
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ToDoDetailDto getApprovalToDo(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getApprovalToDo(commonHelperReq.getIdEvent());
	}

	/**
	 * 
	 * Method Description: Returns the To Do Details for the passed To Do ID.
	 * 
	 * @paramcommonHelperReq
	 * @returnToDoDetailDto Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public ToDoDetailDto getToDo(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getToDo(commonHelperReq.getIdToDo());
	}

	/**
	 * 
	 * Method Description: Returns a set of tasks associated with events not in COMP
	 * or APRV status for a particular stage.
	 * 
	 * @paramcommonHelperReq
	 * @returnListObjectRes Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public ListObjectRes getPendingEventTasks(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getPendingEventTasks(commonHelperReq.getIdStage());
	}

	/**
	 * 
	 * Method Description: Returns details for the stage of the given type, if one
	 * exists, that originated from the given stage id. (USAGE: This method was
	 * written for SIR 16114 to find the FSU stage that most closely precedes the
	 * FRE stage with the given start date.)
	 * 
	 * @paramcommonHelperReq
	 * @returnSelectStageDto Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public SelectStageDto getStageByTypeAndPriorStage(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getStageByTypeAndPriorStage(commonHelperReq.getIdStage(), commonHelperReq.getCdStage());
	}

	/**
	 * 
	 * Method Description: Returns the most recent event id, event status, task code
	 * and timestamp for the given stage and event type.
	 * 
	 * @paramcommonHelperReq
	 * @returnEventDto Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public EventDto getEventByStageAndEventType(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getEventByStageAndEventType(commonHelperReq.getIdStage(), commonHelperReq.getEventType());
	}

	/**
	 * 
	 * Method Description: Looks at the case to determine if the given user has
	 * access to any stage. Use this version of the method if you want to test
	 * access for the current user. The following items are checked: primary worker
	 * assigned to stage, one of the four secondary workers assigned to the stage,
	 * the supervisor of any of the above, the designee of any of the above
	 * supervisors
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean Tuxedo Service Name: NA
	 */
	@Override
	@Transactional
	public CommonHelperRes hasAccessToCase(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setHasAccessToCase(
				caseSummaryDao.hasAccessToCase(commonHelperReq.getIdCase(), commonHelperReq.getIdPerson()));
		return commonHelperRes;
	}

	/**
	 * Method Description: This method inserts closed case image access audit data
	 * into CASE_IMAGE_API_AUDIT table. Service Name: ClosedCaseImageAccess
	 * 
	 * @paramclosedCaseImageAccessReq
	 * @returnClosedCaseImageAccessRes
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ClosedCaseImageAccessRes insertApiAuditRecord(ClosedCaseImageAccessReq closedCaseImageAccessReq) {
		ClosedCaseImageAccessRes closedCaseImageAccessRes = new ClosedCaseImageAccessRes();
		String rtnMsg = "";
		rtnMsg = caseSummaryDao.insertApiAuditRecord(closedCaseImageAccessReq.getIdCase(),
				closedCaseImageAccessReq.getIdPerson());
		closedCaseImageAccessRes.setReturnMsg(rtnMsg);
		return closedCaseImageAccessRes;
	}

	/**
	 * Method Name: getCaseMrgValidation Method Description: This method will do the
	 * case merge validation. Like, Fetch the open stage record, given a caseId.
	 * Returns all open stages in a case for given caseId. Check if both the cases
	 * are merge compatible, INV and SVC cases are handled differently. This method
	 * returns boolean value true if stage start date of both fromMergeCase and
	 * toMergeCase is after SHIELD effective date. evaluates for SHIELD Case Check
	 * if RORA is completed in both the cases Investigations with a different Final
	 * Risk Level cannot be merged, and compares the Final Risk Level of both the
	 * investigations A complete Safety Assessment is required before merging
	 * investigations, This function returns true if both the investigations have
	 * complete safety assessments If any Safety Reassessments exist, they must be
	 * completed This method checks if the Strengths and Needs Assessment is
	 * complete This method checks if all the Strengths and Needs Reassessments are
	 * complete. This method checks if an SNA exists for a given stage A completed
	 * Safety Assessment is no longer required before merging investigations,To
	 * check if servicePlan event exists in the to stage
	 * 
	 * @paramcaseMergeValidationReq
	 * @returnCaseMergeValidationRes
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaseMergeValidationRes getCaseMrgValidation(CaseMergeValidationReq caseMergeValidationReq) {
		CaseMergeValidationRes caseMergeValidationRes = new CaseMergeValidationRes();
		StageDto frmMrgStgDtl = new StageDto();
		StageDto toMrgStgDtl = new StageDto();
		Boolean isCareOmCase = Boolean.FALSE;
		Boolean isCaseFromCareOm = Boolean.FALSE;
		Boolean isCaseToCareOm = Boolean.FALSE;
		Boolean isCaseFromShield = Boolean.FALSE;
		Boolean isCaseToShield = Boolean.FALSE;
		Boolean isShieldCase = Boolean.FALSE;
		Boolean isCompatible = Boolean.FALSE;
		Boolean isComplete = Boolean.FALSE;
		Boolean isRoraComp = Boolean.FALSE;
		Boolean isRiskLevelEqual = Boolean.FALSE;
		Boolean isSafetyReasComp = Boolean.FALSE;
		Boolean isSafetAssmtComp = Boolean.FALSE;
		Boolean isSnaReasComp = Boolean.FALSE;
		String fromEventStatus = ServiceConstants.EMPTY_STRING;
		String toEventStatus = ServiceConstants.EMPTY_STRING;

		List<StageDto> fromMergeOpenStages = stageDao.getOpenStages(caseMergeValidationReq.getFromStage().getIdCase());
		List<StageDto> toMergeOpenStages = stageDao.getOpenStages(caseMergeValidationReq.getToStage().getIdCase());

		if (!fromMergeOpenStages.isEmpty()) {
			caseMergeValidationRes.setFromMrgOpenStages(fromMergeOpenStages);
		}

		if (!toMergeOpenStages.isEmpty()) {
			caseMergeValidationRes.setToMrgOpenStages(toMergeOpenStages);
		}
		// getting last stage from the list of open stages
		if (!fromMergeOpenStages.isEmpty()) {
			frmMrgStgDtl = fromMergeOpenStages.get(fromMergeOpenStages.size() - 1);
			caseMergeValidationRes.setFromMrgOpenStage(frmMrgStgDtl);
		}
		if (!toMergeOpenStages.isEmpty()) {
			toMrgStgDtl = toMergeOpenStages.get(toMergeOpenStages.size() - 1);
			caseMergeValidationRes.setToMrgOpenStage(toMrgStgDtl);
		}
		//
		Boolean isValidShieldCaseFrm = stageDao.isValidShieldCase(frmMrgStgDtl);
		Boolean isValidShieldCaseTo = stageDao.isValidShieldCase(toMrgStgDtl);
		if (!TypeConvUtil.isNullOrEmpty(frmMrgStgDtl) && !TypeConvUtil.isNullOrEmpty(toMrgStgDtl)) {
			isCaseFromShield = isValidShieldCaseFrm;
			isCaseToShield = isValidShieldCaseTo;
			if (isCaseFromShield && isCaseToShield) {
				isShieldCase = Boolean.TRUE;
			}
		}
		caseMergeValidationRes.setIsShieldCase(isShieldCase);
		if (!TypeConvUtil.isNullOrEmpty(frmMrgStgDtl) && !TypeConvUtil.isNullOrEmpty(toMrgStgDtl)) {
			isCaseFromCareOm = !isValidShieldCaseFrm;
			isCaseToCareOm = !isValidShieldCaseTo;
			if (isCaseFromCareOm && isCaseToCareOm) {
				isCareOmCase = Boolean.TRUE;
			}
		}
		caseMergeValidationRes.setisCareOmCase(isCareOmCase);
		if (!TypeConvUtil.isNullOrEmpty(frmMrgStgDtl) && !TypeConvUtil.isNullOrEmpty(toMrgStgDtl)) {
			if (ServiceConstants.CSTAGES_INV.equalsIgnoreCase(frmMrgStgDtl.getCdStage())
					&& ServiceConstants.CSTAGES_INV.equalsIgnoreCase(toMrgStgDtl.getCdStage())) {
				isCaseFromShield = isValidShieldCaseFrm;
				isCaseToShield = isValidShieldCaseTo;
				if ((isCaseFromShield && isCaseToShield) || (!isCaseFromShield && !isCaseToShield)) {
					isCompatible = Boolean.TRUE;
				}
			} else if (ServiceConstants.CSTAGES_SVC.equalsIgnoreCase(frmMrgStgDtl.getCdStage())
					&& ServiceConstants.CSTAGES_SVC.equalsIgnoreCase(toMrgStgDtl.getCdStage())) {
				if (!(ServiceConstants.CSTAGES_TYPE_REG.equals(frmMrgStgDtl.getCdStageType())
						|| ServiceConstants.CSTAGES_TYPE_GUA.equals(frmMrgStgDtl.getCdStageType()))
						&& (ServiceConstants.CSTAGES_TYPE_ICS.equals(toMrgStgDtl.getCdStageType())
								|| ServiceConstants.CSTAGES_TYPE_MNT.equals(toMrgStgDtl.getCdStageType()))
						|| (ServiceConstants.CSTAGES_TYPE_REG.equals(toMrgStgDtl.getCdStageType())
								|| ServiceConstants.CSTAGES_TYPE_GUA.equals(toMrgStgDtl.getCdStageType()))
								&& (ServiceConstants.CSTAGES_TYPE_ICS.equals(frmMrgStgDtl.getCdStageType())
										|| ServiceConstants.CSTAGES_TYPE_MNT.equals(frmMrgStgDtl.getCdStageType()))) {
					isCompatible = Boolean.TRUE;
				}
			}
		}
		caseMergeValidationRes.setIsMergeCompatible(isCompatible);
		Long fromSAEventCount = eventDao.getSafetyReasEventCount(frmMrgStgDtl.getIdStage());
		Long toSAEventCount = eventDao.getSafetyReasEventCount(toMrgStgDtl.getIdStage());
		if (fromSAEventCount < 0 || toSAEventCount < 0) {
			isSafetyReasComp = Boolean.TRUE;
		}
		caseMergeValidationRes.setIsSafetyReasComplete(isSafetyReasComp);
		// Added Missing IsSafetyAssmtComplete from legacy
		Boolean isFromSafetyAssmtComplete = Boolean.FALSE;// eventDao.isSafetyAssmtComplete(frmMrgStgDtl.getIdStage());
		Boolean isToSafetyAssmtComplete = Boolean.FALSE;// eventDao.isSafetyAssmtComplete(toMrgStgDtl.getIdStage());
		if (isFromSafetyAssmtComplete && isToSafetyAssmtComplete) {
			isSafetAssmtComp = Boolean.TRUE;
		}
		caseMergeValidationRes.setIsSafetyAssmtComplete(isSafetAssmtComp);
		Long fromSNAEventCount = eventDao.getSNAReasEventCount(frmMrgStgDtl.getIdStage());
		Long toSNAEventCount = eventDao.getSNAReasEventCount(toMrgStgDtl.getIdStage());
		Boolean isFromSNAExist = eventDao.isSNAexists(frmMrgStgDtl.getIdStage(), ServiceConstants.CD_ASSMT_TYPE_REAS);
		Boolean isToSNAExist = eventDao.isSNAexists(toMrgStgDtl.getIdStage(), ServiceConstants.CD_ASSMT_TYPE_REAS);
		if ((!isFromSNAExist && fromSNAEventCount < 0) || (!isToSNAExist && toSNAEventCount < 0)) {
			isSnaReasComp = Boolean.TRUE;
		}
		caseMergeValidationRes.setIsSnaReasComplete(isSnaReasComp);
		Boolean isFromSNAComp = eventDao.isSNAComplete(frmMrgStgDtl.getIdStage());
		Boolean isToSNAComp = eventDao.isSNAComplete(toMrgStgDtl.getIdStage());
		Boolean isFromSNAExists = eventDao.isSNAexists(frmMrgStgDtl.getIdStage(), ServiceConstants.CD_ASSMT_TYPE_INIT);
		Boolean isToSNAExists = eventDao.isSNAexists(toMrgStgDtl.getIdStage(), ServiceConstants.CD_ASSMT_TYPE_INIT);
		if ((!isFromSNAExists && isFromSNAComp) || (!isToSNAExists && isToSNAComp)) {
			isComplete = Boolean.TRUE;
		}
		caseMergeValidationRes.setIsSnaComplete(isComplete);
		Boolean isInterventionStarted = stageDao.isInterventionStarted(frmMrgStgDtl.getIdStage());
		caseMergeValidationRes.setIsToSafetyAsmtInterventionStarted(isInterventionStarted);
		Boolean isServicePlanExists = stageDao.isServicePlanExists(toMrgStgDtl.getIdStage());
		caseMergeValidationRes.setIsServicePlanExists(isServicePlanExists);
		Boolean isSec3Started = stageDao.isSec3Started(frmMrgStgDtl.getIdStage());
		caseMergeValidationRes.setIsFromSafetyAsmtSec3Started(isSec3Started);
		Boolean isInterventionSelected = stageDao.isInterventionSelected(frmMrgStgDtl.getIdStage());
		caseMergeValidationRes.setIsFromSafetyAmntIntrvntnSelected(isInterventionSelected);
		List<String> fromCaseRoraStatusList = eventDao.getRoraEventStatus(frmMrgStgDtl.getIdStage());
		List<String> toCaseRoraStatusList = eventDao.getRoraEventStatus(toMrgStgDtl.getIdStage());
		if (null != fromCaseRoraStatusList && !fromCaseRoraStatusList.isEmpty()) {
			fromEventStatus = fromCaseRoraStatusList.get(fromCaseRoraStatusList.size() - 1);
		}
		if (null != toCaseRoraStatusList && !toCaseRoraStatusList.isEmpty()) {
			toEventStatus = toCaseRoraStatusList.get(toCaseRoraStatusList.size() - 1);
		}
		if (ServiceConstants.CD_EVENT_STATUS_COMP.equalsIgnoreCase(fromEventStatus)
				&& ServiceConstants.CD_EVENT_STATUS_COMP.equalsIgnoreCase(toEventStatus)) {
			isRoraComp = Boolean.TRUE;
		}
		caseMergeValidationRes.setIsRoraComplete(isRoraComp);
		String fromCaseRiskLevel = eventDao.getFinalRiskLevel(frmMrgStgDtl.getIdStage());
		String toCaseRiskLevel = eventDao.getFinalRiskLevel(toMrgStgDtl.getIdStage());
		if ((!TypeConvUtil.isNullOrEmpty(toCaseRiskLevel)) && (!TypeConvUtil.isNullOrEmpty(fromCaseRiskLevel))) {
			isRiskLevelEqual = fromCaseRiskLevel.equals(toCaseRiskLevel) ? true : false;
		}
		caseMergeValidationRes.setisRoraRiskLevelEqual(isRiskLevelEqual);
		log.info("TransactionId :" + caseMergeValidationReq.getTransactionId());
		return caseMergeValidationRes;
	}

	/**
	 * Method-Description: This method returns a date value when a stage is closed.
	 * 
	 * Service Name - NA (Util Method dtStageClosed)
	 * 
	 * @paramcommonHelperReq(Stage id)
	 * @returnDate
	 * 
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes dtStageClosed(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		Date dtStageClose = null;
		StageDto stageDto = stageDao.getStageById(commonHelperReq.getIdStage());
		if (null != stageDto.getDtStageClose()) {
			dtStageClose = stageDto.getDtStageClose();
		}
		commonHelperRes.setDtStageClosed(dtStageClose);
		return commonHelperRes;
	}

	/**
	 * Method-Description:This method returns all SUB stages in the case
	 * 
	 * @param ulIdCase
	 * @return ListOfStageId
	 * @ @throws
	 *       InvalidRequestException
	 */
	@Override
	@Transactional
	public IDListRes getAllSUBStages(CommonHelperReq commonHelperReq) {
		IDListRes listRes = new IDListRes();
		List<StageDto> listStageId = new ArrayList<StageDto>();
		List<Long> stageIdList = new ArrayList<Long>();
		try {
			listStageId = stageDao.getStagesByType(commonHelperReq.getIdCase(), ServiceConstants.ALL_STAGES,
					ServiceConstants.CSTAGES_SUB);
			if (null != listStageId) {
				for (StageDto dd : listStageId) {
					stageIdList.add(dd.getIdStage());
				}
				listRes.setIdList(stageIdList);
			}
		} catch (InvalidRequestException e) {
			throw new ServiceLayerException("Exception Occured : CaseSummaryServiceImpl.getAllSUBStages : ",
					e.getMessage(), null);
		}
		return listRes;
	}

	/**
	 * Method-Description:This method returns all FSU stages in the case
	 * 
	 * @param ulIdCase
	 * @return ListOfStageId @ InvalidRequestException
	 */
	@Override
	@Transactional
	public IDListRes getOpenFSUStages(CommonHelperReq commonHelperReq) {
		IDListRes listRes = new IDListRes();
		List<StageDto> listStageId = new ArrayList<StageDto>();
		List<Long> stageIdList = new ArrayList<Long>();
		try {
			listStageId = stageDao.getStagesByType(commonHelperReq.getIdCase(), ServiceConstants.OPEN_STAGES,
					ServiceConstants.CSTAGES_FSU);
			if (null != listStageId) {
				for (StageDto dd : listStageId) {
					stageIdList.add(dd.getIdStage());
				}
				listRes.setIdList(stageIdList);
			}
		} catch (InvalidRequestException e) {
			throw new ServiceLayerException("Exception Occured : CaseSummaryServiceImpl.getOpenFSUStages : ",
					e.getMessage(), null);
		}
		return listRes;
	}

	/**
	 * Method-Description:This method returns all FRE stages in the case
	 * 
	 * @param ulIdCase
	 * @return ListOfStageId @ InvalidRequestException
	 */
	@Override
	@Transactional
	public IDListRes getOpenFREStages(CommonHelperReq commonHelperReq) {
		IDListRes listRes = new IDListRes();
		List<StageDto> listStageId = new ArrayList<StageDto>();
		List<Long> stageIdList = new ArrayList<Long>();
		try {
			listStageId = stageDao.getStagesByType(commonHelperReq.getIdCase(), ServiceConstants.OPEN_STAGES,
					ServiceConstants.CSTAGES_FRE);
			if (null != listStageId) {
				for (StageDto dd : listStageId) {
					stageIdList.add(dd.getIdStage());
				}
				listRes.setIdList(stageIdList);
			}
		} catch (InvalidRequestException e) {
			throw new ServiceLayerException("Exception Occured : CaseSummaryServiceImpl.getOpenFREStages : ",
					e.getMessage(), null);
		}
		return listRes;
	}

	@Override
	public CommonHelperRes getCaseStageCheckoutStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setCaseStageCheckoutStatus(stageDao.getCaseStageCheckoutStatus(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	@Override
	@Transactional
	public IncomingDetailDto getIncomingDetailByStageId(CommonHelperReq commonHelperReq) {
		IncomingDetailDto detailDto = new IncomingDetailDto();
		IncomingDetail incomingDetail = incomingDetailDao.getincomingDetailbyId(commonHelperReq.getIdStage());
		BeanUtils.copyProperties(incomingDetail, detailDto);
		return detailDto;
	}

	/**
	 * Method-Description:Retrieve Intake stage details for a Case
	 * 
	 * @param commonHelperReq
	 * @return StageDto @
	 */
	@Override
	@Transactional
	public StageDto getOldestIntakeStageByCaseId(CommonHelperReq commonHelperReq) {
		return caseSummaryDao.getOldestIntakeStageByCaseId(commonHelperReq.getIdCase());
	}

	/**
	 * Method-Description:Update case and stage to close for new reason
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes updateStageCloseReason(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		caseSummaryDao.updateStageCloseReason(commonHelperReq.getIdStage());
		CloseStageCaseInputDto closeStageCaseInput = new CloseStageCaseInputDto();
		closeStageCaseInput.setCdStage(commonHelperReq.getCdStage());
		closeStageCaseInput.setCdStageProgram(commonHelperReq.getStageProgType());
		closeStageCaseInput.setCdStageReasonClosed(commonHelperReq.getCdStageReasonClosed());
		closeStageCaseInput.setEventDescr(ServiceConstants.INV_CLOSE_REASON);
		closeStageCaseInput.setIdStage(commonHelperReq.getIdStage());
		closeStageCaseInput.setIdPerson(commonHelperReq.getIdPerson());
		closeStageCaseInput.setReqFuncCd(ServiceConstants.ADD);
		closeStageCaseInput.setIsCrsrStage(Boolean.FALSE);
		closeStageCaseService.closeStageCase(closeStageCaseInput);
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: getRegionByCounty Method Description:
	 * 
	 * @param county
	 * @ret
	 *
	 **/
	@Override
	public String getRegionByCounty(String county) {
		return caseSummaryDao.getRegionByCounty(county);
	}

	/**
	 * Method Name: saveIntakeNotesOrReopnInv Method Description:This Service is for
	 * updating intake notes and reopen INV reasons for CCL Program ADS Changes on
	 * Case summary page
	 * 
	 * @param stageClosureRtrvReq
	 * @return stageRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageRes saveIntakeNotesOrReopnInv(StageClosureRtrvReq stageClosureRtrvReq) {
		StageRes stageRes = new StageRes();
		if (stageClosureRtrvReq.getStageClosureDto().getStageDto().isIndStageReOpenInv()) {
			ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
			stageClosureRtrvReq.getPostEventIPDto().setTsLastUpdate(new Date());
			if (ObjectUtils.isEmpty(stageClosureRtrvReq.getPostEventIPDto().getIdEvent())
					|| 0L == stageClosureRtrvReq.getPostEventIPDto().getIdEvent()) {
				archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
			} else {
				archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
			}
			// Calling event service to create new event when ReOpen INV is
			// clicked
			postEventService.checkPostEventStatus(stageClosureRtrvReq.getPostEventIPDto(), archInputDto);
		}
		if ((ServiceConstants.Y)
				.equalsIgnoreCase(stageClosureRtrvReq.getStageClosureDto().getStageDto().getIndRevwdReadyAsgn())) {
			CnsrvtrshpRemovalAlertRes res = new CnsrvtrshpRemovalAlertRes();
			// Calling ToDO creation service when Reviewed and Ready to be
			// Assigned is clicked
			res.setStatusResponse(caseSummaryDao.getAlertForIntakeNotes(
					stageClosureRtrvReq.getToDoDto().getIdTodoCfStage(),
					stageClosureRtrvReq.getCommonDto().getNmStage(), stageClosureRtrvReq.getCommonDto().getIdCase()));
		}

		// Deleting the task created for the CCL Reviewer
		if (!ObjectUtils.isEmpty(stageClosureRtrvReq.getCommonDto().getIdTodo())
				&& stageClosureRtrvReq.getCommonDto().getIdTodo() > 0l) {
			todoDao.deleteTodo(stageClosureRtrvReq.getCommonDto().getIdTodo());
		}
		if (stageClosureRtrvReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
			// Calling Stage table update service
			closeOpenStageService.stageAUD(stageClosureRtrvReq.getStageClosureDto().getStageDto(),
					stageClosureRtrvReq.getReqFuncCd());
			stageRes.setMessage(ServiceConstants.SUCCESS);
		}
		return stageRes;
	}

	/**
	 * Method Name: getIntakeNotesOrReopnInv Method Description:This Service is for
	 * fetching intake notes and reopen INV reasons for CCL Program ADS Changes on
	 * Case summary page
	 * 
	 * @param stageClosureRtrvReq
	 * @return stageRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public StageRes getIntakeNotesOrReopnInv(StageClosureRtrvReq stageClosureRtrvReq) {
		StageRes stageRes = new StageRes();
		StageDto stageDto = stageDao.getStageById(stageClosureRtrvReq.getStageClosureDto().getStageDto().getIdStage());
		if (!ObjectUtils.isEmpty(stageDto)) {
			stageRes.setStageDto(stageDto);
		}
		return stageRes;
	}

	/**
	 * Method Name: getIntIntakeDate Method Description: This method is used to get
	 * Int Intake Date
	 * 
	 * @param idStage
	 * @return Date
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, readOnly = true)
	public Date getIntIntakeDate(Long idStage) {
		return caseSummaryDao.getIntIntakeDate(idStage);
	}

	/**
	 * Method Name: getIntakeStageIdForSelectedStage Method Description: Method to
	 * fetch the Intake Stage Id for the passed stage ID
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@Override
	@Transactional
	public CommonHelperRes getIntakeStageIdForSelectedStage(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIdIntakeStage(caseSummaryDao.getIntakeStageIdForSelectedStage(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void reopenStage(ReopenStageDto reopenStageDto) {
		closeOpenStageService.reopenClosedStage(reopenStageDto);
	}
	
	
	/**
	 * Method Name: getPrimaryChildIdByIdStage
	 * Description: Getting Person ID(Primary child Id) by passing Stage ID
	 * 
	 * @param commonHelperReq
	 * @return idPrimaryChild
	 * 
	 * © 2019 Texas Department of Family and Protective Services
	 * FCL Artifact ID: artf128756
	 **/
	@Override
	@Transactional
	public long getPrimaryChildIdByIdStage(long idStage) {
		long idPrimaryChild = 0;
		
		StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
		stagePersonLinkDto = stagePersonLinkDao.getPrimaryChildIdByIdStage(idStage);
		if (!ObjectUtils.isEmpty(stagePersonLinkDto)) {
			idPrimaryChild = stagePersonLinkDto.getIdPerson();
		}
			return idPrimaryChild;
	}

	// artf178637 this method should be exactly the same as ApsIntakeReportPrefillData.applyRcciMrefThresholds
	// it is duplicated because the shared common-* libraries don't allow for shared logic
	public static Integer applyRcciMrefThresholds(Integer rcciMrefCount, String cdProgramType, Integer nbrFacilCapacity, String cdFacilityType) {
		Integer retVal = rcciMrefCount;
		if (retVal != null) {
			if (CodesConstant.CPGRMS_RCL.equalsIgnoreCase(cdProgramType)
					|| CodesConstant.CPGRMS_CCL.equalsIgnoreCase(cdProgramType)) {
				if (cdFacilityType != null) {
					if ("64".equals(cdFacilityType)) {
						if (nbrFacilCapacity == null || nbrFacilCapacity <= 40) {
							if (rcciMrefCount < 11) {
								retVal = null;
							}
						} else if (nbrFacilCapacity <= 80) {
							if (rcciMrefCount < 31) {
								retVal = null;
							}
						} else {
							if (rcciMrefCount < 47) {
								retVal = null;
							}
						}
					} else if ("80".equals(cdFacilityType) || "67".equals(cdFacilityType)) {
						if (nbrFacilCapacity == null || nbrFacilCapacity <= 25) {
							if (rcciMrefCount < 7) {
								retVal = null;
							}
						} else if (nbrFacilCapacity <= 75) {
							if (rcciMrefCount < 12) {
								retVal = null;
							}
						} else {
							if (rcciMrefCount < 11) {
								retVal = null;
							}
						}
					} else { // the query only pulls data for GRO, RTC, or ES, but if that breaks we should hide MREF
						retVal = null;
					}
				} else { // if facility type is null, the this is not GRO, RTC, or ES, and is not eligible for MREF value.
					retVal = null;
				}
			} else { // program is not RCL or CCL. Ideally you wouldn't call this with the wrong program but whatevs.
				retVal = null;
			}
		}
		return retVal;
	}

	/**
	 * Method Name: getStageByIdCase
	 * Method Description: Method to fetch stage information for passed case ID
	 *
	 * @param idCase
	 * @return StageInfoRes
	 */
	@Override
	public StageInfoRes getStageByIdCase(Long idCase) {
		StageInfoRes stageInfoRes = new StageInfoRes();
		stageInfoRes.setStageDtoList(stageDao.getStageEntityByCaseId(idCase, StageDao.ORDERBYCLAUSE.BYSTAGEID));
		return stageInfoRes;
	}

	/**
	 *
	 * @param eventId
	 * @return String
	 */
	@Override
	public CommonHelperRes getPcspPlacementId(Long eventId) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		String plcmntId = pCSPPlcmntDao.getPlacementIdForEvent(eventId);
		commonHelperRes.setIdPcspPlcmnt(plcmntId);
		return commonHelperRes;
	}

	/**
	 * Method helps to check for is the stage is checkout to MPS or not.
	 * If stage is checked out to MPS. Getting worker details to display the name and worker id in case summary screen as attention message
	 *
	 * @param caseStage case stage summary dto
	 */

	private void checkForMPSCheckoutDetails(CaseStageSummaryDto caseStage) {
		if (caseStage.getIdStage() > 0) {
			CommonHelperReq commonHelperReq = new CommonHelperReq();
			commonHelperReq.setIdStage(caseStage.getIdStage());
			Long idPersonMpsCheckout =	caseSummaryDao.getCaseCheckoutPerson(caseStage.getIdStage());
			if (idPersonMpsCheckout > 0) {
				caseStage.setStageCheckoutStatus(true);
				Employee employee = employeeDao.getEmployeeByEmployeeId(idPersonMpsCheckout);
				caseStage.setMpsCheckoutPersonId(employee.getIdPerson());
				caseStage.setMpsCheckoutPersonName(employee.getNmEmployeeLast()
						+ "," + employee.getNmEmployeeFirst() + " " + employee.getNmEmployeeMiddle());
			}
		}
	}

	/**
	 * Method Name: getCaseSensitiveByIdCase
	 * Method Description: Method to fetch Case summary information for passed case ID
	 *
	 * @param idCase
	 * @return boolean
	 */
	@Override
	public boolean getCaseSensitiveByIdCase(Long idCase) {
		CaseSummaryDto caseSummaryDto = caseSummaryDao.getCaseInfo(idCase);
		return "Y".equals(caseSummaryDto.getIndCaseSensitive());
	}

    /**
     * Called during case summary page initial load.
     * @param idCase
     * @param idStage
     * @return
     */
    public NotificationFileRes getAcknowledgeFileList(Long idCase) {
        NotificationFileRes retVal = new NotificationFileRes();
		List<NotificationFileDto> fileList = null;
        List<CgNotifFileUpload> recordList = caseSummaryDao.getNeubusRecords(idCase);
		List<Long> recordIdList = recordList.stream().mapToLong(currRecord -> currRecord.getIdCgNotifFileUpload()).
				boxed().collect(Collectors.toList());

        if (!ObjectUtils.isEmpty(recordList)) {
			fileList = new LinkedList<>();
			List<CgNotifFileUploadDtl> rawResultList = caseSummaryDao.getNeubusFileList(recordIdList);

            // Convert from domain object to DTO
            for (CgNotifFileUploadDtl currResult : rawResultList) {
                NotificationFileDto temp = new NotificationFileDto();
				temp.setIdCgNotifFileUpload(currResult.getIdCgNotifFileUpload());
                temp.setIdCgNotifFileUploadDtl(currResult.getIdCgNotifFileUploadDtl());
                temp.setIdNuidNum(currResult.getIdNuidNum());
                temp.setTxtUploadFileNm(currResult.getTxtUploadFileNm());
                temp.setTxtComments(currResult.getTxtComments());
                temp.setCdDocType(currResult.getCdDocType());
                temp.setCdRequestStatus(currResult.getCdRequestStatus());
                temp.setDtCreated(currResult.getDtCreated());
                temp.setDtLastUpdate(currResult.getDtLastUpdate());
                temp.setIdCreatedPerson(currResult.getIdCreatedPerson());
                temp.setIdLastUpdatePerson(currResult.getIdLastUpdatePerson());
                temp.setDtAckRcvd(currResult.getDtAckRcvd());
                fileList.add(temp);
            }
        }

        retVal.setNotificationFileList(fileList);

        // add information regarding the current placement provider.
    	List<ProviderPlacementDto> placementData = caseSummaryDao.fetchCurrentPlacementInformation(idCase);

		retVal.setProviderPlacementDtoList(placementData);

        return retVal;
    }

    @Override
    public NotificationFileRes acknowledgementRequestDownload(CaseSummaryReq caseSummaryReq) {
        NotificationFileRes res = new NotificationFileRes();
        NotificationFileDto identifiers = caseSummaryDao.getNeubusIdentifiers(caseSummaryReq.getIdCgNotifFileUpload(), caseSummaryReq.getIdCgNotifFileUploadDtl());

        if (identifiers != null) {
            String docId = identifiers.getIdDocNum();
            String nuId = identifiers.getIdNuidNum();
            String fileName = identifiers.getTxtUploadFileNm();
            MuleEsbResponseDto reponse = caregiverNotificationService.muleEsbDownloadFile(nuId, docId);

            if (reponse.getErrorMessage() == null) {
                res.setFileDownloadData(reponse.getFileData());
                res.setFileName(fileName);
            } else {
                res.setErrorCode(reponse.getErrorCode());
                res.setErrorMessage(reponse.getErrorMessage());
            }
        } else {
            res = new NotificationFileRes();
            res.setErrorCode("500");
            res.setErrorMessage("Internal Error: No document found in DB with that ID.");
        }
		return res;
    }

	@Override
	public NotificationFileRes acknowledgementRequestUpload(CaseSummaryReq caseSummaryReq) {
		// This was a working stub, documents show MuleSoft will provide us the document id, but responses from the
		// sandbox instance provided indicate document id will be hidden. Sooo, this may or may not need a total
		// redesign down to the tables.

		// Neubus File creation
		MuleEsbResponseDto reponse = caregiverNotificationService.muleEsbUploadFile(caseSummaryReq);
		String newFileNuid = reponse.getNuid();
		String newRecordDocId = reponse.getDocumentId();

		Long newIdCgNotifFileUpload = null;
		NotificationFileRes res = new NotificationFileRes();
		if (reponse.getErrorMessage() == null) {
			Long exisgtingIdCgNotifFileUpload = caseSummaryDao.getNebusRecordById(newRecordDocId, caseSummaryReq.getIdCase(),
					caseSummaryReq.getIdStage(), caseSummaryReq.getLoginUserId());
			if (exisgtingIdCgNotifFileUpload == null) {
				newIdCgNotifFileUpload = caseSummaryDao.createNeubusRecord(newRecordDocId, caseSummaryReq.getIdCase(),
						caseSummaryReq.getIdStage(), caseSummaryReq.getLoginUserId());
			} else {
				newIdCgNotifFileUpload = exisgtingIdCgNotifFileUpload;
			}

			// you may notice a lack of null checking on placement id. it's not really possible to have a null placement
			// id since we checked for one before we created the record. If there is somehow a null placement id, there
			// is no recovery anyhow so just let it fail hard.
			List<ProviderPlacementDto> currPlacementList = caseSummaryDao.fetchCurrentPlacementInformation(caseSummaryReq.getIdCase());
			ProviderPlacementDto currPlacement = currPlacementList.stream().filter(placement -> placement.getStageId().equals(caseSummaryReq.getIdStage())).findFirst().get();
			Long newIdCgNotifFileUploadDtl = caseSummaryDao.createNeubusFile(newFileNuid, newIdCgNotifFileUpload,
					caseSummaryReq.getFilename(), caseSummaryReq.getCdDocType(), "NS",
					currPlacement.getChildName() + "-" + currPlacement.getResourceName(), currPlacement.getPlacementId(), caseSummaryReq.getLoginUserId());

			res.setIdCgNotifFileUpload(newIdCgNotifFileUpload);
			res.setIdCgNotifFileUploadDtl(newIdCgNotifFileUploadDtl);
		} else {
			res.setErrorCode(reponse.getErrorCode());
			res.setErrorMessage(reponse.getErrorMessage());
		}
		return res;
	}

	@Override
	public NotificationFileRes acknowledgementRequestSend(CaseSummaryReq caseSummaryReq) {
        CaregiverAckReq paramObject = caseSummaryDao.findAckRequestParameters(caseSummaryReq.getIdCgNotifFileUploadDtl());
        paramObject.setDocumentType(convertImpactDocTypeToMulesoft(paramObject.getDocumentType()));
		paramObject.setCaseWorkerId(String.valueOf(caseSummaryReq.getLoginUserId()));
        MuleEsbResponseDto ackReqRes = caregiverNotificationService.muleEsbSendAckRequest(paramObject);
        if (ackReqRes.getErrorMessage() == null) {
            caseSummaryDao.updateNeubusFile(caseSummaryReq.getIdCgNotifFileUploadDtl(), "SE", caseSummaryReq.getLoginUserId());
        } else {
            caseSummaryDao.updateNeubusFile(caseSummaryReq.getIdCgNotifFileUploadDtl(), "ND", caseSummaryReq.getLoginUserId());
        }
        NotificationFileRes retVal = new NotificationFileRes();
        retVal.setErrorCode(ackReqRes.getErrorCode());
        retVal.setErrorMessage(ackReqRes.getErrorMessage());
		return retVal;
	}

	// IMPACT values defined in CODES_TABLES where CODE_TYPE = 'CCDOCTYP', Mulesoft values from API Payload Schema-Send Acknowledgment-Salesforce-IMPACT.txt
    private String convertImpactDocTypeToMulesoft(String documentType) {
        String retVal = null;
        if (documentType != null) {
            switch (documentType) {
                case "AA" :
                    retVal = "AttachmentA";
                    break;
                case "PS" :
                    retVal = "2279";
                    break;
            }
        }
        return retVal;
    }

    private String convertMulesoftDocTypeToImpact(String documentType) {
        String retVal = null;
        if (documentType != null) {
            switch (documentType) {
                case "AttachmentA" :
                    retVal = "AA";
                    break;
                case "2279" :
                    retVal = "PS";
                    break;
            }
        }
        return retVal;
    }


}
