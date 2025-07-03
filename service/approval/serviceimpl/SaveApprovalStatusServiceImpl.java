/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *
 *June 4, 2018- 10:59:00 AM
 *© 2017 Texas Department of Family and Protective Services 
 */

package us.tx.state.dfps.service.approval.serviceimpl;



import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.approval.dto.ApprovalStatusFacilityIndicatorDto;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyReq;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyRes;
import us.tx.state.dfps.approval.dto.AprvlStatusUpdateCapsResourceReq;
import us.tx.state.dfps.approval.dto.ClosePlacementReq;
import us.tx.state.dfps.approval.dto.ContractVersionAudReq;
import us.tx.state.dfps.approval.dto.CreateStageProgressionEventReq;
import us.tx.state.dfps.approval.dto.GetPrimaryWorkerDto;
import us.tx.state.dfps.approval.dto.GetPrimaryWorkerReq;
import us.tx.state.dfps.approval.dto.ResourceHistoryCountReq;
import us.tx.state.dfps.approval.dto.ResourceHistoryCountRes;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageReq;
import us.tx.state.dfps.approval.dto.UpdateFacilityLocReq;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.common.web.ApprovalConstants;
import us.tx.state.dfps.common.web.WebConstants;
import us.tx.state.dfps.service.admin.dao.ApprovalEventDao;
import us.tx.state.dfps.service.admin.dao.ApproverUpdateDao;
import us.tx.state.dfps.service.admin.dao.EventAdminDao;
import us.tx.state.dfps.service.admin.dao.EventInsUpdDelDao;
import us.tx.state.dfps.service.admin.dao.FacilityLocDao;
import us.tx.state.dfps.service.admin.dao.PersonInfoDao;
import us.tx.state.dfps.service.admin.dao.PersonStagePersonLinkTypeDao;
import us.tx.state.dfps.service.admin.dao.PlacementActPlannedDao;
import us.tx.state.dfps.service.admin.dao.StageLinkStageDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkStTypeRoleDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dao.TaskDetailsDao;
import us.tx.state.dfps.service.admin.dao.UpdateEventDao;
import us.tx.state.dfps.service.admin.dto.ApprovalUpdateDto;
import us.tx.state.dfps.service.admin.dto.ContractCountyInDto;
import us.tx.state.dfps.service.admin.dto.ContractCountyOutDto;
import us.tx.state.dfps.service.admin.dto.ContractOutputDto;
import us.tx.state.dfps.service.admin.dto.ContractServiceInDto;
import us.tx.state.dfps.service.admin.dto.ContractServiceOutDto;
import us.tx.state.dfps.service.admin.dto.ContractVersionInDto;
import us.tx.state.dfps.service.admin.dto.ContractVersionOutDto;
import us.tx.state.dfps.service.admin.dto.EmpUnitDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.admin.dto.EventDataInputDto;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocDto;
import us.tx.state.dfps.service.admin.dto.FacilityLocInDto;
import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeInDto;
import us.tx.state.dfps.service.admin.dto.PersonStagePersonLinkTypeOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.admin.dto.StageLinkStageInDto;
import us.tx.state.dfps.service.admin.dto.StageLinkStageOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkStTypeRoleOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateOutDto;
import us.tx.state.dfps.service.admin.dto.UpdateEventiDto;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusDao;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusResourceHistyDao;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusUpdateCapsResourceDao;
import us.tx.state.dfps.service.approval.dao.AprvlGetPrimaryWorkerDao;
import us.tx.state.dfps.service.approval.dao.AprvlStatusGetNmStageDao;
import us.tx.state.dfps.service.approval.dao.ClosePlacementDao;
import us.tx.state.dfps.service.approval.dao.ContractVersionAudDao;
import us.tx.state.dfps.service.approval.dao.CreateStageProgressionEventDao;
import us.tx.state.dfps.service.approval.dao.ResourceHistoryCountDao;
import us.tx.state.dfps.service.approval.dao.UpdateFacilityLocDao;
import us.tx.state.dfps.service.approval.service.SaveApprovalStatusService;
import us.tx.state.dfps.service.arstageprog.dao.ArStageProgDao;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dto.CapsEmailDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.casepackage.service.CapsCaseService;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAUDEvtDetailDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEventDto;
import us.tx.state.dfps.service.childplanenhancements.dao.ChildPlanInsUpdDelDao;
import us.tx.state.dfps.service.childplanrtrv.dao.FetchPlanDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.dao.TaskDao;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.conservatorship.service.ConservatorshipService;
import us.tx.state.dfps.service.cpsinvstsummary.dao.CpsInvstSummaryDao;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.formreferrals.dto.FbssReferralsDto;
import us.tx.state.dfps.service.formreferrals.service.FormReferralsService;
import us.tx.state.dfps.service.heightenedmonitoring.service.HeightenedMonitoringService;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dto.CpsInvstDetailDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.oncall.service.OnCallService;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEligibilityDao;
import us.tx.state.dfps.service.person.dao.RetrieveCapsResourceDao;
import us.tx.state.dfps.service.person.dto.PersonEligibilityDto;
import us.tx.state.dfps.service.person.dto.RtrvRsrcByStageInDto;
import us.tx.state.dfps.service.person.dto.RtrvRsrcByStageOutDto;
import us.tx.state.dfps.service.person.dto.SupervisorDto;
import us.tx.state.dfps.service.placement.dao.ContractCountyDao;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.placement.dao.ContractServiceDao;
import us.tx.state.dfps.service.placement.dao.ContractVersionDao;
import us.tx.state.dfps.service.placement.dao.PersonIdDtlsDao;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodInDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resource.dto.ResourceHistoryAuditInDto;
import us.tx.state.dfps.service.resourcehistory.dao.ResourceHistoryDao;
import us.tx.state.dfps.service.resourcehistoryaudit.dao.ResourceHistoryAuditDao;
import us.tx.state.dfps.service.servicauthform.service.ServiceAuthFormService;
import us.tx.state.dfps.service.stageprogression.service.StageProgressionService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.CapsResourceDto;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcontractor.dao.CcntyregDao;
import us.tx.state.dfps.service.subcontractor.dto.CcntyregDto;
import us.tx.state.dfps.service.workload.dao.ApprovalEventLinkDao;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkEventDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.RejectApprovalDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.dto.TaskDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

@Service
@Transactional
public class SaveApprovalStatusServiceImpl implements SaveApprovalStatusService {

	private static final String APPROVAL = "approval";
	private static final String CHILD_PLAN_APPROVAL_TASK_CODE = "7180";
	private static final Logger LOGGER = Logger.getLogger(SaveApprovalStatusServiceImpl.class);

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private CaseFileManagementDao caseFileManagementDao;

	@Autowired
	CloseOpenStageService closeOpenStageService;

	@Autowired
	CloseStageCaseService closeStageCaseService;

	@Autowired
	CommonToDoFunctionService commonToDoFunctionService;

	@Autowired
	EventService eventService;

	@Autowired
	TodoDao todoDao;

	@Autowired
	UpdateEventDao updateEventDao;

	@Autowired
	ApprovalEventDao approvalEventDao;

	@Autowired
	ApproverUpdateDao approverUpdateDao;

	@Autowired
	ApprovalStatusDao approvalStatusDao;

	@Autowired
	EventInsUpdDelDao eventInsUpdDelDao;

	@Autowired
	EventAdminDao eventAdminDao;

	@Autowired
	CpsInvstSummaryDao cpsInvstSummaryDao;

	@Autowired
	PlacementActPlannedDao placementActPlannedDao;

	@Autowired
	StagePersonLinkStTypeRoleDao stagePersonLinkStTypeRoleDao;

	@Autowired
	PcaDao pcaDao;

	@Autowired
	PersonIdDtlsDao personIdDtlsDao;

	@Autowired
	TaskDao taskDao;

	@Autowired
	TaskDetailsDao taskDetailsDao;

	@Autowired
	PersonInfoDao personInfoDao;

	@Autowired
	ApprovalStatusResourceHistyDao approvalStatusResourceHistyDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	ApprovalStatusUpdateCapsResourceDao approvalStatusUpdateCapsResourceDao;

	@Autowired
	AprvlStatusGetNmStageDao aprvlStatusGetNmStageDao;

	@Autowired
	CapsCaseDao capsCaseDao;

	@Autowired
	ArStageProgDao arStageProgDao;

	@Autowired
	StageLinkStageDao stageLinkStageDao;

	@Autowired
	PersonStagePersonLinkTypeDao personStagePersonLinkTypeDao;

	@Autowired
	PersonEligibilityDao personEligibilityDao;

	@Autowired
	ClosePlacementDao closePlacementDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	FetchPlanDao fetchPlanDao;

	@Autowired
	ChildPlanInsUpdDelDao childPlanInsUpdDelDao;

	@Autowired
	SvcAuthEventLinkDao svcAuthEventLinkDao;

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	ResourceHistoryCountDao resourceHistoryCountDao;

	@Autowired
	ApprovalEventLinkDao approvalEventLinkDao;

	@Autowired
	RetrieveCapsResourceDao retrieveCapsResourceDao;

	@Autowired
	FacilityLocDao facilityLocDao;

	@Autowired
	PlacementDao placementDao;

	@Autowired
	ContractVersionDao contractVersionDao;

	@Autowired
	ContractServiceDao contractServiceDao;

	@Autowired
	ContractCountyDao contractCountyDao;

	@Autowired
	UnitDao unitDao;

	@Autowired
	CcntyregDao ccntyregDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	ContractVersionAudDao contractVersionAudDao;

	@Autowired
	UpdateFacilityLocDao updateFacilityLocDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	AprvlGetPrimaryWorkerDao aprvlGetPrimaryWorkerDao;

	@Autowired
	CreateStageProgressionEventDao createStageProgressionEventDao;

	@Autowired
	ResourceHistoryAuditDao resourceHistoryAuditDao;

	@Autowired
	CapsCaseService capsCaseService;
	@Autowired
	ConservatorshipService conservatorshipService;

	@Autowired
	FamilyPlanDao familyPlanDao;
	
	@Autowired
	LicensingInvstDtlDao licensingInvstDtlDao;

	@Autowired
	ResourceHistoryDao resourceHistoryDao;

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	StageProgressionService stageProgressionService;

	@Autowired
	ServiceAuthFormService serviceAuthFormService;

	@Autowired
	FormReferralsService formReferralsService;

	@Autowired
	OnCallService onCallService;

	@Autowired
	HeightenedMonitoringService heightenedMonitoringService;

	public static final String TODO_ALERT = "A";
	public static final String EVENT_DESCR_END = " End ";
	public static final String tempCdPlcmtRemovalRsn = ServiceConstants.AS_OF_STGCLOSURE;
	public static final Integer NBR_OF_HOME_TYPE = 7;

	public static final Integer NBR_SVC_CODE_SIXTY_A = 1;
	public static final Integer NBR_SVC_CODE_SIXTY_AB = 2;
	public static final Integer NBR_SVC_CODE_SIXTY_ABC = 3;
	public static final Integer NBR_SVC_CODE_SIXTY_ABCD = 4;

	// contract AUD
	public static final String MSG_CON_CLOSURE_AFTER_EFF = "8059";
	public static final Integer THIRTY_DAYS = 30;
	public static final Integer firstRecord = 0; // FIRST_REC
	public static final String dayHours = "DA2";
	/** DAY_24_HOURS */
	public static final String unitRatePaymentType = "URT";
	/** UNIT_RATE_PAYMENT_TYPE */
	public static final String EVENT_DESC_HOME_CLOSED = "Home Closed.";
	public static final String EVENT_DESC_HOME_APPROVED= "Changed home status to Approved- Active.";
	public static final String EXCEPTION_STRING_ONE = "Exception Occured while ";
	public static final String EXCEPTION_STRING_TWO = " of Class ";

	/**
	 * Method Name: saveApvlStsService Method Description: Saves information
	 * change on Approval Window (CCMN35S). Updates the status of all related
	 * events to the Approval. Sends out appropriate To-Do notifications.
	 * 
	 * Legacy Service Name: CCMN35S
	 * 
	 * @param saveApprovalStatusReq
	 *            - passed from ApprovalStatusController
	 * @return approvalStatusRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ApprovalStatusRes saveApprovalStatus(SaveApprovalStatusReq saveApprovalStatusReq) {
		LOGGER.info("Entering saveApprovalStatus() method in SaveApprovalStatusImpl.");

		Boolean indFosterContractExists = Boolean.FALSE; /**
															 * Indicates if that
															 * the foster
															 * contract exists
															 */
		Boolean indAdoptContractExists = Boolean.FALSE; /**
														 * Indicates if that the
														 * foster contract
														 * exists
														 */
		Boolean indUpdateFosterContract = Boolean.FALSE; /**
															 * Indicates if that
															 * the foster
															 * contract exists
															 */
		Boolean indUpdateAdoptContract = Boolean.FALSE; /**
														 * Indicates if that the
														 * foster contract
														 * exists
														 */
		Boolean indPCAContractExists = Boolean.FALSE; /**
														 * Indicates if that the
														 * PCA contract exists
														 */
		Boolean indUpdatePCAContract = Boolean.FALSE; /**
														 * Indicates if that the
														 * PCA contract exists
														 */
		Boolean indGroupHomeIndicator = Boolean.FALSE;
		Boolean categoryHomeType = Boolean.FALSE;
		Boolean relFictiveKinHome = Boolean.FALSE;
		Boolean eaSub = Boolean.FALSE; // bEA_Sub
		Boolean eAEligible = Boolean.FALSE; // bEA_Eligible
		Boolean eaTwelveMonths = Boolean.FALSE;// bEA_12Months eaTwelveMonths
		Boolean eaFound = Boolean.FALSE;// bEA_Found
		Boolean indOpenDADDPlcmt = Boolean.FALSE;
		Integer counterP = 0;
		Long idEvent = 0l;
		Long ONE = 1L;
		Long TWO = 2L;
		Byte ONE_BYTE = 1;
		Integer ONE_INT = 1;
		Integer usCountyRow;
		String countyFromResource = ""; /** county from resource */
		String regionFromResource = ""; /** resource region */
		String sCreateContract = ServiceConstants.EMPTY_STR; /**
																 * Indicates the
																 * type of
																 * contract to
																 * be created.
																 */
		String sUpdateContract = ServiceConstants.EMPTY_STR; /**
																 * Indicates the
																 * type of
																 * contract to
																 * be update.
																 */
		String EVENT_PLACE_HOLDER = "{NM_EVENT}";
		String APR_WORKER = "New approval determination logged {NM_EVENT}. Outstanding requests exist.";
		String APR_DESIGNEE = "Designee Logged Approval {NM_EVENT} has been approved. Outstanding requests exist.";
		String REJ_WORKER = "Rejection determination logged for {NM_EVENT}. Other approval requests invalidated.";
		String REJ_DESIGNEE = "Designee Logged Rejection {NM_EVENT} has been approved. Other requests invalidated.";
		String COM_WORKER = "Approval Complete: {NM_EVENT}.";
		String COM_DESIGNEE = "Designee Logged Approval {NM_EVENT} has been approved";
		Integer rowCounter;
		Integer rowCounterTwo;
		Long tempIdRsrcAddress = ServiceConstants.ZERO;
		Long idTempContract = ServiceConstants.ZERO;
		Integer adoptiveOrFoster;
		Long tempIdPlcmtEvent = 0l;
		Long idResource = ServiceConstants.ZERO;
		Boolean foster = Boolean.TRUE;
		Boolean change = Boolean.TRUE;
		Boolean testBool = Boolean.FALSE;
		StringBuilder endDateString = new StringBuilder();

		SimpleDateFormat dateFormatter = new SimpleDateFormat();

		Date MAX_DATE = new Date(Long.MAX_VALUE);

		List<ContractOutputDto> contractOutputDtoList = new ArrayList<>();
		List<String> ADOPTIVE_OR_FOSTER_LIST = new ArrayList<>();
		List<ContractOutputDto> contractOutDtoForCloseHomeList = new ArrayList<>();

		/** R1/R2 Impact */

		/** Counter for contracts returned from database */
		PlacementActPlannedOutDto placementActPlannedOutDto = new PlacementActPlannedOutDto();
		
		Calendar calendarPlus100Years = Calendar.getInstance();
		calendarPlus100Years.add(Calendar.YEAR, 100);
		ApprovalStatusRes approvalStatusRes = new ApprovalStatusRes();
		try {
			String emailMessage = ServiceConstants.EMPTY_STRING;
			ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();

			/** This is done for avoiding the Null pointers below */
			if (ObjectUtils.isEmpty(saveApprovalStatusReq.getRejectApprovalDto())) {
				saveApprovalStatusReq.setRejectApprovalDto(new RejectApprovalDto());
			}

			/**
			 * Determine IdPlcmtChild by retrieving PRIMARY CHILD from
			 * Stage_Person_Link with ID_STAGE, CD_STAGE_PERS ROLE,
			 * CD_STAGE_PERS_TYPE DAM Name: CINT20D
			 */
			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getIdStage())) {
				List<StagePersonLinkStTypeRoleOutDto> stagePersonLinkStTypeRoleOutDtoList = stagePersonLinkStTypeRoleDao
						.stagePersonDtls(getStagePersonLinkStTypeRoleInDto(saveApprovalStatusReq,
								ServiceConstants.PERSON_ROLE_PRIM_CHILD, ServiceConstants.PERSON_TYPE_PRINCIPAL));

				if (!CollectionUtils.isEmpty(stagePersonLinkStTypeRoleOutDtoList)) {
					/**
					 * DAM Name: CSES34D to get the placement information and
					 * Check for open DA/DD PLACEMENT
					 */
					placementActPlannedOutDto = placementActPlannedDao
							.getPlacementRecord(stagePersonLinkStTypeRoleOutDtoList.get(0).getIdPerson());
					
					if (!ObjectUtils.isEmpty(placementActPlannedOutDto)){
						if ((ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd()) || 
								(!ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd()) 
										&& ServiceConstants.NULL_JAVA_DATE_DATE.compareTo(placementActPlannedOutDto.getDtPlcmtEnd()) == 0))
								&& (ServiceConstants.OPEN_NON_FPS_PAID_PLCMT.equals(placementActPlannedOutDto.getCdPlcmtType()))
								&& (!ServiceConstants.PRIV_AGCY_ADPT_HM.equals(placementActPlannedOutDto.getCdPlcmtLivArr()))
								) {
							tempIdPlcmtEvent = placementActPlannedOutDto.getIdPlcmtEvent();
							indOpenDADDPlcmt = Boolean.TRUE;
						}
						else if((ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd()) || 
								(!ObjectUtils.isEmpty(placementActPlannedOutDto.getDtPlcmtEnd()) 
										&& ServiceConstants.NULL_JAVA_DATE_DATE.compareTo(placementActPlannedOutDto.getDtPlcmtEnd()) == 0))
								&& !ServiceConstants.ADOPTIVE_PLACEMENT.equals(placementActPlannedOutDto.getCdPlcmtLivArr())
								&& !ServiceConstants.PRIV_AGCY_ADPT_HM .equals(placementActPlannedOutDto.getCdPlcmtLivArr())
								&& (ServiceConstants.OPEN_PLCMT_DA .equals(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| ServiceConstants.OPEN_PLCMT_DD.equals(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| ServiceConstants.OPEN_PLCMT_DG.equals(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| ServiceConstants.OPEN_PLCMT_DF.equals(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| ServiceConstants.OPEN_PLCMT_DQ.equals(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| ServiceConstants.OPEN_PLCMT_DR.equals(placementActPlannedOutDto.getCdPlcmtLivArr())
										|| ServiceConstants.OPEN_PLCMT_DE.equals(placementActPlannedOutDto.getCdPlcmtLivArr())
									)
								) {
							indOpenDADDPlcmt = Boolean.TRUE;
							tempIdPlcmtEvent = placementActPlannedOutDto.getIdPlcmtEvent();
						}
						else {
							indOpenDADDPlcmt = Boolean.FALSE;
						}
					}
					
				}
			}

			/**
			 * MHMR Enhancement for AFC Investigation "Waiting for
			 * Superintendent Comments". If the Investigation is approved, the
			 * Superintendent Notified indicator will be set to 'Y' (indicator
			 * selected). If the approval to_do is an "Approve In take Call", do
			 * not update superintendent notified ind.
			 * 
			 * The SuperInt Notification check box is being checked incorrectly
			 * when the Extent Req Form was approved. Updated if statement to
			 * exclude Extension Request.
			 */

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& ServiceConstants.STATUS_APPROVED
							.equals(saveApprovalStatusReq.getApproveApprovalDto().getCdApproversStatus())
					&& (ServiceConstants.AFC_STAGE
							.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageProgram()))
					&& !((ServiceConstants.APPROVE_INT_CALL
							.equals(saveApprovalStatusReq.getEventStatusDto().getCdTask()))
							|| (ServiceConstants.APPROVE_EXT_REQ
									.equals(saveApprovalStatusReq.getEventStatusDto().getCdTask()))))

			{
				/**
				 * DAM Name: CAUDC7D
				 * 
				 * to set the Superintendent Notified indicator on the Facility
				 * Investigation table. Will set the indicator to 'Y'.
				 */
				approvalStatusDao.updateFacilityIndicator(saveApprovalStatusReq);
			}

			/**
			 * Overall disposition need to be saved into Approval or approval
			 * rejection for CPS Investigation stage only Calling CINV95D to
			 * fetch the overall disposition
			 */
			if ((ServiceConstants.INV_Stage.equals(saveApprovalStatusReq.getCdStage()))
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getAprvlStageProgDto())
					&& (ServiceConstants.CPS_PROGRAM
							.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageProgram()))) {
				/**
				 * DAM Name: CINV95D
				 * 
				 * to fetch the overall disposition
				 */
				List<CpsInvstDetailDto> cpsInvstDetailDtoList = cpsInvstSummaryDao
						.getCpsInvstDetail(saveApprovalStatusReq.getIdStage());

				if (!ObjectUtils.isEmpty(cpsInvstDetailDtoList)) {
					if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto()))
						saveApprovalStatusReq.getApproveApprovalDto()
								.setCdOvrllDisptn(cpsInvstDetailDtoList.get(0).getCdCpsOverallDisptn());
					if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getRejectApprovalDto()))
						saveApprovalStatusReq.getRejectApprovalDto()
								.setCdOvrllDisptn(cpsInvstDetailDtoList.get(0).getCdCpsOverallDisptn());
				}

				/**
				 * DAM Name: CSES00D DAM Description:Fetch the employee
				 * information based on the ID_PERSON of Approver We need
				 * JobClass and EmpConfirmedHrmis for storing it into Approver
				 * or Approval rejection table
				 */
				Long idPerson = ServiceConstants.ZERO;
				if (ServiceConstants.WIN_APPROVE.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())
						|| ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {
					idPerson = Long.valueOf(saveApprovalStatusReq.getApproveApprovalDto().getIdPerson());

				} else if (ServiceConstants.WIN_REJECT.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {
					idPerson = Long.valueOf(saveApprovalStatusReq.getRejectApprovalDto().getIdRejector());
				}

				EmployeeDetailDto employeeDetailDto = approvalStatusDao.getEmployeeInfo(idPerson);

				saveApprovalStatusReq.getApproveApprovalDto().setCdJobClass(employeeDetailDto.getCdEmployeeClass());
				saveApprovalStatusReq.getRejectApprovalDto().setCdJobClass(employeeDetailDto.getCdEmployeeClass());
				saveApprovalStatusReq.getApproveApprovalDto()
						.setIndEmpConfirmedHrmis(employeeDetailDto.getIndEmpConfirmedHrmis());
				saveApprovalStatusReq.getRejectApprovalDto()
						.setIndEmpConfirmedHrmis(employeeDetailDto.getIndEmpConfirmedHrmis());

				// Null Checks
				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getAprvlStageProgDto())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())) {
					saveApprovalStatusReq.getApproveApprovalDto().setCdStageReasonClosed(
							saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed());
				}

				// Null Checks
				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getAprvlStageProgDto())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getRejectApprovalDto()))
					saveApprovalStatusReq.getRejectApprovalDto().setCdStageReasonClosed(
							saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed());

				if (ServiceConstants.INDICATOR_YES.equals(saveApprovalStatusReq.getIndSecondApprover())) {

					/**
					 * DAM NAME: CAUDK4D if this request is from secondary
					 * approver, then we need to store this information on the
					 * STAGE table in IND_SECOND_APPROVER. Once a stage has been
					 * reviewed by second approver, this stage would not require
					 * secondary approval, even if other conditions are met.
					 * 
					 * This DAM will update IndSecondApprover in STAGE table for
					 * a given IdStage
					 */
					approvalStatusDao.updateIndSecondApprover(saveApprovalStatusReq);
				}
			}

			/*** INVALIDATED ALREADY SCENERIO ***/
			if (ServiceConstants.WIN_INVALID.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {

				/** Set up To_do Action Structure */
				TodoDto todoDto = new TodoDto();
				todoDto.setIdTodo(saveApprovalStatusReq.getIdTodo());
				/** Force Delete w/ Max Time stamp Value */
				todoDto.setDtLastUpdate(MAX_DATE);
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);

				/**
				 * DAM NAME: CCMN43D Delete the To_do
				 */
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);
			}

			/*** SAVE APPROVAL DETERMINATION SCENERIO ***/
			else if (ServiceConstants.WIN_APPROVE.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {

				/**
				 * DAM NAME: CCMN46D
				 * 
				 * This dam update the txt_event_descr in teh placement event to
				 * reflect that the placement has been ended.
				 * 
				 */
				eventInsUpdDelDao.ccmn46dAUDdam(getEventInsUpdDelInDto(saveApprovalStatusReq));

				/** Save the Approval Determination */

				/**
				 * DAM Name : CCMN61D
				 * 
				 * Executes the AUD Type DAM that will update the changed
				 * Approvers row on the Approvers table for save approval
				 * determination scenario
				 */

				/**
				 * Dam Name: CCMN61D
				 * 
				 * This Dam is to Update the approvers tables based on the
				 * ID_APPROVERS
				 */
				approvalStatusDao.updateApprovers(getApprovalStatusFacilityIndicatorDto(saveApprovalStatusReq));

				/**
				 * This dam CCMN43D has switch case (3) 1.Insert 2.Update
				 * 3.Delete
				 * 
				 * In this case we are calling CCMN43D to delete the to_do
				 */
				TodoDto todoDto = new TodoDto();
				todoDto.setIdTodo(saveApprovalStatusReq.getIdTodo());
				todoDto.setDtLastUpdate(MAX_DATE);
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);

				/**
				 * DAM Name: CCMN43D
				 * 
				 * This Dam is to delete the to_do
				 */
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);

				/**
				 * DAM Name: CCMN82D.
				 * 
				 * This DAM is to Retrieve entire row from the TASK table based
				 * on the CdTask.
				 */
				TaskDto taskDto = taskDao.getTaskDetails(saveApprovalStatusReq.getCdTask());

				getSaveApprovalStatusReqForTodo(saveApprovalStatusReq, TODO_ALERT);
				saveApprovalStatusReq.setIdTodoPersAssigned(saveApprovalStatusReq.getIdPerson());
				saveApprovalStatusReq.setIdTodoPersWorker(saveApprovalStatusReq.getIdPerson());
				saveApprovalStatusReq.setTxtTodoDesc(APR_WORKER.replace(EVENT_PLACE_HOLDER, taskDto.getTaskDecode()));
				todoDto = initializeTodo(saveApprovalStatusReq);
				serviceReqHeaderDto.setReqFuncCd(todoDto.getReqFuncCd());

				/**
				 * Dam Name: CCMN43D This is to send Notification to the worker
				 */
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);

				/**
				 * Checking if Supervisor Notification Necessary
				 */
				if (ServiceConstants.INDICATOR_YES.equals(saveApprovalStatusReq.getIndDesigneeAprvl())) {

					/**
					 * Dam Name: CCMN60D
					 * 
					 * This DAM is used to retrieve an employee's supervisor
					 * name and ID from Person
					 */
					SupervisorDto supervisorDto = pcaDao.getSupervisorPersonId(saveApprovalStatusReq.getIdPerson());

					/**
					 * DAM Name: CCMN82D
					 * 
					 * This Dam will retrieve row from TASK table based on the
					 * cdTask.
					 */
					taskDto = taskDao.getTaskDetails(saveApprovalStatusReq.getCdTask());

					getSaveApprovalStatusReqForTodo(saveApprovalStatusReq, TODO_ALERT);
					saveApprovalStatusReq.setIdTodoPersAssigned(supervisorDto.getIdPerson());
					saveApprovalStatusReq.setIdTodoPersWorker(saveApprovalStatusReq.getIdPerson());
					saveApprovalStatusReq
							.setTxtTodoDesc(APR_DESIGNEE.replace(EVENT_PLACE_HOLDER, taskDto.getTaskDecode()));

					todoDto = initializeTodo(saveApprovalStatusReq);
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);

					/**
					 * Dam Name: CCMN43D This is to send Notification to the
					 * Supervisor
					 */
					todoDao.todoAUD(todoDto, serviceReqHeaderDto);
				}
			}

			/*** SAVE REJECTION DETERMINATION SCENARIO ***/

			/**
			 * This Block of Code getting saved, When the flag
			 * WcdCdAprvlWinaction is 'Y' Only
			 */
			else if (ServiceConstants.WIN_REJECT.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {

				/**
				 * Condition: Save Rejection to APPROVAL_REJECTION table
				 */
				if (ServiceConstants.YES.equals(saveApprovalStatusReq.getIndDispRejReason())) {

					/**
					 * DAM Name: CCMNI2D
					 * 
					 * This dam will save to the APPROVAL_REJECTION table and
					 * approval rejection information entered on the approval
					 * status page.
					 */
					approvalStatusDao.saveRejectionApproval(getRejectApprovalDto(saveApprovalStatusReq));
				}

				/**
				 * DAM Name: CCMN46D This Dam has Add, Update and Delete
				 * functions.
				 * 
				 * This particular point we are Updating the EVENT Status
				 * (Intervening Update Strategy)
				 */
				eventAdminDao.postEvent(getEventDataInputDto(saveApprovalStatusReq));

				/**
				 * Dam Name: CCMN61D
				 * 
				 * This Dam is to perform Save the Approval Determination
				 */
				approvalStatusDao.updateApprovers(getApprovalStatusFacilityIndicatorDto(saveApprovalStatusReq));

				/**
				 * DAM Name: CCMN88D
				 * 
				 * This DAM Sets the Approver status to invalid for all pending
				 * approvers for a given approval.
				 */
				approverUpdateDao.updateApproverStatus(getAprvlStatus(saveApprovalStatusReq));

				/**
				 * DAM Name: CCMN57D
				 * 
				 * For a given approval retrieves the related functional events
				 */
				List<ApprovalEventLinkEventDto> approvalEventLinkEventDtoList = approvalEventLinkDao
						.approvalEventLinkSearchByApprovalId(saveApprovalStatusReq.getIdApproval());

				/**
				 * Initialize and Populate Input Structure for DAM: Related
				 * Event
				 */
				UpdateEventiDto updateEventiDto = new UpdateEventiDto();

				/**
				 * Populate Input Structure: request function code in the input
				 * arch
				 */
				updateEventiDto.setcReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

				/**
				 * Update Loop for all related events stop if error encountered
				 * (This Block of code is moved from DAM [CCMN62D] to here)
				 */
				for (ApprovalEventLinkEventDto approvalEventOutputDto : approvalEventLinkEventDtoList) {

					/**
					 * Populate Input Structure for DAM: Related Event
					 */
					updateEventiDto.setUlIdEvent(approvalEventOutputDto.getIdEvent());

					/**
					 * Populate Input Structure: Set New Event Status for all
					 * related events (This Block of code is moved from DAM
					 * [CCMN62D] to here)
					 */
					if (ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {
						updateEventiDto.setSzCdEventStatus(ServiceConstants.EVT_APPROVED);
					} else {
						updateEventiDto.setSzCdEventStatus(ServiceConstants.EVT_COMPLETE);
						if (ServiceConstants.TASK_CCLINVCLOSEAPP.equals(saveApprovalStatusReq.getCdTask())
								&& ServiceConstants.Y
										.equals(saveApprovalStatusReq.getRejectApprovalDto().getIndInCompleteCCLRejection())
								&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getCdTask())
								&& ServiceConstants.TASK_CCLINVCLOSURE.equals(approvalEventOutputDto.getCdTask())) {
							licensingInvstDtlDao.saveLicensingInvDtlBasedOnDtComplt(approvalEventOutputDto.getIdEvent(),
									null);
							updateEventiDto.setSzCdEventStatus(ServiceConstants.PROCESS_EVENT_STATUS);
						}
					}
					
					/**
					 * DAM NAME : CCMN62D
					 * 
					 * Description: Loops through the returned list of ID EVENTS
					 * from CCMN57D executing the CCMN62D AUD type DAM to update
					 * the event status for each to either Completed or Approved
					 * based upon what was accomplished on the window.
					 */
					
					updateEventDao.updateEvent(updateEventiDto);
				}

				TodoDto todoDto = new TodoDto();
				todoDto.setIdTodo(saveApprovalStatusReq.getIdTodo());
				todoDto.setDtLastUpdate(MAX_DATE);
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);

				/**
				 * DAM Name - CCMN43D This Method is used to Delete to do List
				 * based on ID To do.
				 */
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);

				getSaveApprovalStatusReqForTodo(saveApprovalStatusReq, TODO_ALERT);
				saveApprovalStatusReq.setIdTodoPersAssigned(saveApprovalStatusReq.getIdPerson());
				saveApprovalStatusReq.setIdTodoPersWorker(saveApprovalStatusReq.getIdPerson());

				/**
				 * DAM Name: CCMN82D
				 * 
				 * This method is to Retrieves a complete single row from the
				 * Task table.
				 */
				TaskDto taskDto = taskDao.getTaskDetails(saveApprovalStatusReq.getCdTask());

				saveApprovalStatusReq.setTxtTodoDesc(REJ_WORKER.replace(EVENT_PLACE_HOLDER, taskDto.getTaskDecode()));

				todoDto = initializeTodo(saveApprovalStatusReq);
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);

				/**
				 * DAM Name - CCMN43D This Method is used send Notification to
				 * Worker.
				 */
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);

				/** Check if Supervisor Notification Necessary */
				if (ServiceConstants.INDICATOR_YES.equals(saveApprovalStatusReq.getIndDesigneeAprvl())) {

					/**
					 * DAM: CCMN60D Capture Supervisor's ID into locally
					 * declared 2nd parameter by calling
					 * 
					 */
					SupervisorDto supervisorDto = pcaDao.getSupervisorPersonId(saveApprovalStatusReq.getIdPerson());

					/**
					 * DAM Name: CCMN82D This method retrieves a complete row
					 * from the Task table to get Task decode from Task table.
					 */

					// Populate Task Code and Save into database
					taskDto = taskDao.getTaskDetails(saveApprovalStatusReq.getCdTask());
					saveApprovalStatusReq
							.setTxtTodoDesc(REJ_DESIGNEE.replace(EVENT_PLACE_HOLDER, taskDto.getTaskDecode()));
					getSaveApprovalStatusReqForTodo(saveApprovalStatusReq, TODO_ALERT);
					saveApprovalStatusReq.setIdTodoPersAssigned(supervisorDto.getIdPerson());
					saveApprovalStatusReq.setIdTodoPersWorker(saveApprovalStatusReq.getIdPerson());
					todoDto = initializeTodo(saveApprovalStatusReq);

					/**
					 * DAM Name - CCMN43D This Method is used send Notification
					 * to Supervisor.
					 */
					todoDao.todoAUD(todoDto, serviceReqHeaderDto);
				}

				/**
				 * If Approval was rejected and the event is FA Home retrieve
				 * the record from the Resource History table that has the most
				 * recent Date Effective and the FA Home Status is not Pending
				 * Approved or Pending Closed. Update the Resource record with
				 * the FA Home Status from the Resource History record.
				 */
				for (ApprovalEventLinkEventDto approvalEventOutputDto : approvalEventLinkEventDtoList) {

					/**
					 * For each Event in this list, check the Task Code to see
					 * if it is an FA Home event.
					 */
					/**
					 * Checking the task code whether it is equal to 8200 or not
					 */
					if ((ServiceConstants.FA_HOME_HME).equals(approvalEventOutputDto.getCdTask())) {

						/**
						 * DAM Name: CSEC38D This Dam will retrieve a row from
						 * the RESOURCE HISTORY table where the effective Date
						 * is the most recent date and FA home status is <> to
						 * 'PV' or 'SP'
						 */
						List<ApprovalStatusResourceHistyRes> apprStatResHistyResList = approvalStatusResourceHistyDao
								.fetchResourceHisty(getApprStatResHistyReq(saveApprovalStatusReq));

						for (ApprovalStatusResourceHistyRes approvalStatusResourceHistyRes : apprStatResHistyResList) {

							/**
							 * DAM Name: CRES04D
							 * 
							 * This DAM performs a full row select from the
							 * CAPS_RESOURCE table with ID_RESOURCE as input.
							 */
							ResourceDto resourceDto = capsResourceDao
									.getResourceById(approvalStatusResourceHistyRes.getIdResource());
							resourceDto.setNmRsrcLastUpdate(saveApprovalStatusReq.getUserFullName());

							/**
							 * DAM Name: CAUD52D
							 * 
							 * This dam will update rows from the Caps Resource
							 * table.
							 */
							approvalStatusUpdateCapsResourceDao.updateCapsResource(getupdateCapsResource(resourceDto));
						}
					}
				}
			}

			/*** SAVE COMPLETED APPROVAL DETERMINATION SCENERIO ***/

			else if (ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {

				// artf215108 - Added code to delete alerts after a sub stage is approved for closure
				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
						&& ServiceConstants.STATUS_APPROVED
						.equals(saveApprovalStatusReq.getApproveApprovalDto().getCdApproversStatus())
						&& (ServiceConstants.STAGE_CODE_SUB
						.equals(saveApprovalStatusReq.getCdStage()))
						&& (ServiceConstants.CLOSE_SUB_STAGE
						.equals(saveApprovalStatusReq.getCdTask()))) {
					todoDao.deleteRCIAlerts(saveApprovalStatusReq.getIdStage());
				}

				/**
				 * DAM NAME: CCMN46D
				 * 
				 * This dam update the Event Status (Intervening Update
				 * Strategy)
				 */
				eventInsUpdDelDao.ccmn46dAUDdam(getEventInsUpdDelInDto(saveApprovalStatusReq));

				/**
				 * DAM Name: CCMN61D
				 * 
				 * Executes the AUD Type DAM that will update the changed
				 * Approvers row on the Approvers table.(Save the Approval
				 * Determination)
				 */
				approvalStatusDao.updateApprovers(getApprovalStatusFacilityIndicatorDto(saveApprovalStatusReq));
				// SD 82809 - COMP Status.
				if(ServiceConstants.FSU_SVC_AUTH_TASK.equals(saveApprovalStatusReq.getCdTask())         ||
						ServiceConstants.FPR_SVC_AUTH_TASK.equals(saveApprovalStatusReq.getCdTask())    ||
						ServiceConstants.SUBCARE_SVC_AUTH_TASK.equals(saveApprovalStatusReq.getCdTask()) ||
						ServiceConstants.APS_SVC_AUTH_TASK.equals(saveApprovalStatusReq.getCdTask())      ||
						ServiceConstants.INV_SERVICE_AUTH_TASK_CODE.equals(saveApprovalStatusReq.getCdTask()) ||
				        ServiceConstants.SA_CPS_PAL.equals(saveApprovalStatusReq.getCdTask())  ||
				        ServiceConstants.AR_SERVICE_AUTH_TASK_CODE.equals(saveApprovalStatusReq.getCdTask()) )
				{
					approvalStatusDao.deleteAutEvent(saveApprovalStatusReq.getIdCase());
				}
				/**
				 * DAM Name: CCMN57D
				 * 
				 * For a given approval retrieves the related functional events
				 */
				List<ApprovalEventLinkEventDto> approvalEventLinkEventDtoList = approvalEventLinkDao
						.approvalEventLinkSearchByApprovalId(saveApprovalStatusReq.getIdApproval());

				for (ApprovalEventLinkEventDto approvalEventOutputDto : approvalEventLinkEventDtoList) {
					UpdateEventiDto updateEventiDto = new UpdateEventiDto();
					updateEventiDto.setUlIdEvent(approvalEventOutputDto.getIdEvent());
					if (ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {
						updateEventiDto.setSzCdEventStatus(ServiceConstants.EVT_APPROVED);
					} else {
						updateEventiDto.setSzCdEventStatus(ServiceConstants.EVT_COMPLETE);
					}
					updateEventiDto.setcReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

					/**
					 * DAM Name: CCMN62D
					 * 
					 * This Dam updates event status
					 */
					updateEventDao.updateEvent(updateEventiDto);
					//PPM 70054 - Set the placement Approved indicator in CHILD_BILL_OF_RIGHTS Table for initial bill of rights.
					if(!ObjectUtils.isEmpty(updateEventiDto) && !ObjectUtils.isEmpty(updateEventiDto.getSzCdEventStatus())
							&& ServiceConstants.EVT_APPROVED.equals(updateEventiDto.getSzCdEventStatus())){
						placementDao.setInitialBorDisableInd(updateEventiDto.getUlIdEvent());
					}
					//Added the code to delete the todo if the cdtask = 2330 for warranty defect 12191					
					List<TodoDto> todoDtoList = todoDao.fetchToDoListForEvent(approvalEventOutputDto.getIdEvent());
					if (!CollectionUtils.isEmpty(todoDtoList)) {
						for (TodoDto todoDto : todoDtoList) {
							if (todoDto.getIdTodo() != saveApprovalStatusReq.getIdTodo()
									&& ServiceConstants.INV_CONCLUSION_TASK_CODE
											.equalsIgnoreCase(todoDto.getCdTodoTask())) {
								todoDao.deleteTodo(todoDto.getIdTodo());
							}
						}
					}
				}
				TodoDto todoDto = new TodoDto();
				todoDto.setIdTodo(saveApprovalStatusReq.getIdTodo());
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
				/**
				 * DAM Name: CCMN43D
				 * 
				 * This Method Delets the to do.
				 */
				TodoDto todo = todoDao.getTodoDtlsById(saveApprovalStatusReq.getIdTodo());
				if (ObjectUtils.isEmpty(todo)) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
					approvalStatusRes.setErrorDto(errorDto);
					Long errorCode = 2046l;
					
					throw new ServiceLayerException("",errorCode,org.springframework.http.HttpStatus.OK);
				} 
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);

				/**
				 * DAM Name: CCMN82D Method Description: This method retrieves a
				 * complete row from the Task table to get Task decode from Task
				 * table.
				 */
				TaskDto taskDto = taskDao.getTaskDetails(saveApprovalStatusReq.getCdTask());
				getSaveApprovalStatusReqForTodo(saveApprovalStatusReq, TODO_ALERT);
				saveApprovalStatusReq.setIdTodoPersAssigned(saveApprovalStatusReq.getIdPerson());
				saveApprovalStatusReq.setIdTodoPersWorker(saveApprovalStatusReq.getIdPerson());
				saveApprovalStatusReq.setTxtTodoDesc(COM_WORKER.replace(EVENT_PLACE_HOLDER, taskDto.getTaskDecode()));
				todoDto = initializeTodo(saveApprovalStatusReq);

				if (ServiceConstants.DCR_TASK_CODE_SUB.equals(saveApprovalStatusReq.getCdTask())
						|| ServiceConstants.DCR_TASK_CODE_INV.equals(saveApprovalStatusReq.getCdTask())
						|| ServiceConstants.DCR_TASK_CODE_FRE.equals(saveApprovalStatusReq.getCdTask())
						|| ServiceConstants.DCR_TASK_CODE_FSU.equals(saveApprovalStatusReq.getCdTask())
						|| ServiceConstants.DCR_TASK_CODE_FPR.equals(saveApprovalStatusReq.getCdTask())) {
					StringBuilder todoDescStringBuilder = new StringBuilder();
					todoDescStringBuilder.append(todoDto.getTodoDesc());
					todoDescStringBuilder.append("(");
					SaveApprovalStatusNmStageReq saveApprovalStatusNmStageReq = new SaveApprovalStatusNmStageReq();
					saveApprovalStatusNmStageReq.setIdStage(saveApprovalStatusReq.getIdStage());

					/**
					 * DAM Name: CCMNJ7D
					 * 
					 * This method retrieves a stage name from the Stage table
					 * based on an ID_stage as an input
					 */
					todoDescStringBuilder.append(todoDto.getTodoDesc());
					todoDescStringBuilder
							.append((aprvlStatusGetNmStageDao.getNmStage(saveApprovalStatusNmStageReq)).getNmStage());
					todoDescStringBuilder.append(",");
					todoDescStringBuilder.append(saveApprovalStatusReq.getIdCase());
					todoDescStringBuilder.append(")");
					todoDto.setTodoDesc(todoDescStringBuilder.toString());
				} else if (todoDto.getIdTodoCase() > ServiceConstants.ZERO) {
					StringBuilder todoDescStringBuilder = new StringBuilder();
					todoDescStringBuilder.append(todoDto.getTodoDesc());

					/**
					 * DAM:CCMNC5D
					 * 
					 * Method Description: This Method will retire the ID CASE
					 * from the service and will return an entire row from the
					 * CAPS_CASE table. Case details.
					 */
					todoDescStringBuilder.append("(");
					todoDescStringBuilder.append(capsCaseDao.getCaseDetails(todoDto.getIdTodoCase()).getNmCase());
					todoDescStringBuilder.append(",");
					todoDescStringBuilder.append(todoDto.getIdTodoCase());
					todoDescStringBuilder.append(")");
					todoDto.setTodoDesc(todoDescStringBuilder.toString());
				}
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);

				/**
				 * DAM Name - CCMN43D This Method is used send Notification to
				 * Worker.
				 */
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);

				/** Check if Supervisor Notification Necessary */
				if (ServiceConstants.INDICATOR_YES.equals(saveApprovalStatusReq.getIndDesigneeAprvl())) {

					/**
					 * Dam Name: CCMN60D
					 * 
					 * Method Description: This method is used to retrieve an
					 * employee's supervisor name and ID from Person, Unit,
					 * Unit_Emp_Link tables by passing idPerson as input.
					 */
					SupervisorDto supervisorDto = pcaDao.getSupervisorPersonId(saveApprovalStatusReq.getIdPerson());

					/**
					 * DAM Name: CCMN82D
					 * 
					 * Method Description: This method retrieves a complete row
					 * from the Task table to get Task decode from Task table.
					 */
					taskDto = taskDao.getTaskDetails(saveApprovalStatusReq.getCdTask());

					getSaveApprovalStatusReqForTodo(saveApprovalStatusReq, TODO_ALERT);
					saveApprovalStatusReq.setIdTodoPersAssigned(supervisorDto.getIdPerson());
					saveApprovalStatusReq.setIdTodoPersWorker(saveApprovalStatusReq.getIdPerson());
					saveApprovalStatusReq
							.setTxtTodoDesc(COM_DESIGNEE.replace(EVENT_PLACE_HOLDER, taskDto.getTaskDecode()));
					todoDto = initializeTodo(saveApprovalStatusReq);
					serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);

					/**
					 * DAM Name - CCMN43D This Method is used send Notification
					 * to Supervisor.
					 */
					todoDao.todoAUD(todoDto, serviceReqHeaderDto);
				}

				/**
				 * Stage Progression / CaseStage Closure. Due to a completed
				 * approval and if stage progression mode indicates an
				 * applicable code, either progress the stage or close the case
				 * based on the mode and other AprvlStageProg structure
				 * information
				 */
				if (ServiceConstants.SP_MANUAL
						.equals(saveApprovalStatusReq.getAprvlStageProgDto().getWCDCdStageProgressMode())
						|| ServiceConstants.SP_AUTOMATIC
								.equals(saveApprovalStatusReq.getAprvlStageProgDto().getWCDCdStageProgressMode())) {

					/**
					 * Creating request object for CloseOpenStage
					 */
					CloseOpenStageInputDto closeOpenStageInputDto = new CloseOpenStageInputDto();
					closeOpenStageInputDto.setIdStage(saveApprovalStatusReq.getIdStage());
					closeOpenStageInputDto.setCdStage(saveApprovalStatusReq.getCdStage());
					closeOpenStageInputDto
							.setCdStageProgram(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageProgram());
					closeOpenStageInputDto
							.setCdStageOpen(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageOpen());
					closeOpenStageInputDto.setCdStageReasonClosed(
							saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed());

					/**
					 * Pass Id of the user who requested the authorization
					 */
					closeOpenStageInputDto.setIdPerson(saveApprovalStatusReq.getIdPerson());

					/**
					 * Calling close stage case for CRSR Reasons Closed as well
					 * as Adoption Disruption for ADO stages.
					 */
					if (ServiceConstants.ADOPTION_STAGE.equals(saveApprovalStatusReq.getCdStage())
							&& (ServiceConstants.ADO_CMTD
									.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
									|| ServiceConstants.UNAB_COMP.equals(
											saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
									|| ServiceConstants.REQ_WITH.equals(
											saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
									|| ServiceConstants.SVC_COMP.equals(
											saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed()))) {
						closeOpenStageInputDto.setSysIndSStgOpenOnly(ServiceConstants.INDICATOR_YES);
					}

					// artf151569
					closeOpenStageInputDto.setIdApproval(saveApprovalStatusReq.getIdApproval());

					/**
					 * Calling The CloseOpenStage Function of
					 * CloseOpenStageService (CCMN03U.Src)
					 */
					closeOpenStageService.closeOpenStage(closeOpenStageInputDto);
				} /** End If - Manual or Auto Stage Progression */

				else if (ServiceConstants.SP_CASECLOSE
						.equals(saveApprovalStatusReq.getAprvlStageProgDto().getWCDCdStageProgressMode())) {

					// FBSS Referral Changes - Added this method call to create/update the Person Eligibility
					if (ServiceConstants.CSTAGES_INV.equals(saveApprovalStatusReq.getCdStage()) &&
							ServiceConstants.FPR_STAGE_PROG_TABLE.containsColumn(saveApprovalStatusReq
									.getAprvlStageProgDto().getCdStageReasonClosed())) {
						closeOpenStageService.createOrUpdatePersonEligibility(saveApprovalStatusReq.getIdStage());
					}

					/**
					 * EA eligibility add/update for INVESTIGATIONS that have
					 * been progressed to SUBCARE and FSU
					 */

					eaSub = Boolean.FALSE;

					/**
					 * Confirm that the INV stage was progressed to SUB/FSU
					 * stages.
					 */

					StageLinkStageInDto stageLinkStageInDto = new StageLinkStageInDto();
					stageLinkStageInDto.setIdStage(saveApprovalStatusReq.getIdStage());

					/**
					 * DAM Name: CSECA8D
					 * 
					 * Retrieves a row if Investigation was progressed to
					 * SUB/FSU.
					 */
					List<StageLinkStageOutDto> stageLinkStageOutDtoList = stageLinkStageDao
							.getPriorStage(stageLinkStageInDto);
					if (!CollectionUtils.isEmpty(stageLinkStageOutDtoList)) {
						eaSub = Boolean.TRUE;
					}

					if (eaSub) {
						/* A */

						/**
						 * DAM Name: CSECA2D
						 * 
						 * Retrieves a row if EA Eligibility was determined
						 */
						//Updated for QC15352, [artf161012]. The SQL condition checks for CD_STAGE_PERS_TYPE!=Passed in Role.
						// Call the service to fetch all Non-Staff PRN persons from Stage Person Link and then filter PRN roles.
						List<PersonStagePersonLinkTypeOutDto> personStagePersonLinkTypeOutDtoList = personStagePersonLinkTypeDao
								.getPersonDetails(getPersonStagePersonLinkTypeInDto(saveApprovalStatusReq,
										ServiceConstants.CPRSNALL_STF));
						if (!CollectionUtils.isEmpty(personStagePersonLinkTypeOutDtoList)) {
							// Filter only the principal Person from the list.
							personStagePersonLinkTypeOutDtoList = personStagePersonLinkTypeOutDtoList.stream()
									.filter(p -> CodesConstant.CPRSNALL_PRN.equals(p.getCdStagePersType()))
									.collect(Collectors.toList());
						}

						// Defect 16792 , EA Eligibility is based on indCPSInvsDtlEaConcl in CPS_INVST_DETAIL
						String indCPSInvsDtlEaConcl = stageDao.getIndCPSInvsDtlEaConclByStageId(saveApprovalStatusReq.getIdStage());

						if (!CollectionUtils.isEmpty(personStagePersonLinkTypeOutDtoList) && ServiceConstants.Y.equalsIgnoreCase(indCPSInvsDtlEaConcl)) {
							eAEligible = Boolean.TRUE;
						}

						if (eAEligible) {/* B */
							for (PersonStagePersonLinkTypeOutDto personStagePersonLinkTypeOutDto : personStagePersonLinkTypeOutDtoList) {

								/**
								 * DAM Name: CSECA1D
								 * 
								 * Description: Get Person Eligibility List by
								 * Person ID and Eligibility Type
								 */
								List<PersonEligibility> personEligibilityDetailList = personEligibilityDao
										.getPersonEligibilityByIdPersonAndType(
												personStagePersonLinkTypeOutDto.getIdPerson(),
												ServiceConstants.PERS_ELIG_TYPE_EA);
								if (!CollectionUtils.isEmpty(personEligibilityDetailList)) {
									eaFound = Boolean.TRUE;
								} else //Added for QC15352
									eaFound = Boolean.FALSE;

								/**
								 * If an 'EA' PERSON_ELIGIBILITY record was
								 * found for the principal, determine whether
								 * the record is open (active) or not open
								 * (ended).
								 */
								if (eaFound) {
									/** D1 */ // pbeaFound = TRUE
									/**
									 * Retrieve the most recent 'EA'
									 * PERSON_ELIGIBILITY record for the
									 * principal, if one exists.
									 */
									PersonEligibility mostRecentEligibility = personEligibilityDetailList.get(0);
									//Modified for QC15352. Check for NULL_JAVA_DATE_DATE instead of null for DtPersEligEaDeny.
									if ((( new Date()).equals(mostRecentEligibility.getDtPersEligEnd()) || mostRecentEligibility.getDtPersEligEnd().after(new Date()))
											&& ServiceConstants.NULL_JAVA_DATE_DATE
													.equals(mostRecentEligibility.getDtPersEligEaDeny())) {
										/** E1 if EA is open */

										serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);

										PersonEligibilityDto personEligibilityDto = new PersonEligibilityDto();
										personEligibilityDto.setIdPersElig(mostRecentEligibility.getIdPersElig());
										/**
										 ** Since the EA record is open, if it
										 * was started by STARS and is open only
										 * by STARS, update
										 * CD_PERS_ELIG_PRG_OPEN to indicate
										 * that is now open by both STARS and
										 * IMPACT; otherwise update
										 * CD_PERS_ELIG_PRG_OPEN to indicate
										 * that is now open only by IMPACT
										 */
										if (ServiceConstants.PERSELIG_PRG_START_S
												.equals(mostRecentEligibility.getCdPersEligPrgStart())
												&& ServiceConstants.PERSELIG_PRG_OPEN_S
														.equals(mostRecentEligibility.getCdPersEligPrgOpen())) {
											personEligibilityDto
													.setCdPersEligPrgOpen(ServiceConstants.PERSELIG_PRG_OPEN_B);
										} else {
											personEligibilityDto
													.setCdPersEligPrgOpen(ServiceConstants.PERSELIG_PRG_OPEN_C);
										}
										serviceReqHeaderDto.setReqFuncCd(ServiceConstants.EA_UPDATE);

										/**
										 * DAM Name: CAUDC9D
										 * 
										 * Updates EA eligibility open or adds
										 * new EA eligiblity.
										 */
										personEligibilityDao.personEligibilityAUD(personEligibilityDto,
												serviceReqHeaderDto);
									} else {/** E2 if EA is closed */
										eaTwelveMonths = Boolean.FALSE;

										/**
										 * Add one year to the Start Date of the
										 * most recent EA record so that we can
										 * check whether or not one year has
										 * passed since this person's last
										 * eligibility period.
										 * "dtDtPersEligStart" will contain the
										 * Start Date of the most recent EA
										 * record. "dtDtPersEligEnd" will be
										 * returned and will contain the Start
										 * Date plus one year.
										 */

										/****** CSEC85D ******/
										Calendar newEligibilityDate = Calendar.getInstance();
										newEligibilityDate.setTime(mostRecentEligibility.getDtPersEligStart());
										if (newEligibilityDate.get(Calendar.YEAR) % 4 == ServiceConstants.Zero) {
											newEligibilityDate.add(Calendar.DATE, ServiceConstants.LEAP_YEAR_DAYS);
										} else {
											newEligibilityDate.add(Calendar.DATE, ServiceConstants.NORMAL_YEAR_DAYS);
										}

										/**
										 * Query the date that will be used as
										 * the Start Date if/when we write a new
										 * 'EA' PERSON_ELIGIBILITY record for
										 * this principal. We will use the
										 * earliest of the following dates: 1.)
										 * Earliest service auth from the INV
										 * stage, 2.) Date of conservatorship
										 * removal, or 3.) Date of stage closure
										 * of this INV stage.
										 */
										/****** CSECA3D ******/
										if (!TypeConvUtil.isNullOrEmpty(saveApprovalStatusReq.getIdStage())) {
											Date dtSvnAuthDtlBegin = personEligibilityDao
													.getSvcAuthDtlBeginByIdStage(saveApprovalStatusReq.getIdStage());

											/**
											 * Determine whether or not one year
											 * has passed between the Start Date
											 * of the person's most recent EA
											 * record and the Start Date that we
											 * would use for the person's new EA
											 * record if we were to write one.
											 * We can write the new record only
											 * if one year has passed.
											 */
											// Defect 15474, Logic should be if the existing EA date + 1 yr is before the expected new EA start date
											if (newEligibilityDate.getTime().before(dtSvnAuthDtlBegin)) {
												eaTwelveMonths = Boolean.TRUE;
											}
										}
									}
								} /** END: D1 */
								if ((!eaFound || eaTwelveMonths)
										&& !TypeConvUtil.isNullOrEmpty(saveApprovalStatusReq.getIdStage())) {

									/***********************
									 * Determine start date *
									 ************************/

									/**
									 * Query the date that will be used as
									 * the Start Date for the new 'EA'
									 * PERSON_ELIGIBILITY record for this
									 * principal. We will use the earliest
									 * of the following dates: 1.) Earliest
									 * service auth from the INV stage, 2.)
									 * Date of conservatorship removal, or
									 * 3.) Date of stage closure of this INV
									 * stage.
									 */

									/**
									 *
									 * Method Name:
									 * getSvcAuthDtlBeginByIdStage Method
									 * Description:This DAM returns the date
									 * that EA Eligibility should be
									 * initiated. In determining this date,
									 * the DAM grabs the date of all Service
									 * Auths in the Investigation stage, the
									 * date of the conservatorship removal,
									 * and the date of stage closure. It
									 * then picks the earliest of these
									 * dates as the date that EA eligibility
									 * begins.
									 *
									 * DAM Name: CSECA3D
									 *
									 * @param idStage
									 * @return
									 */
									Date dtSvnAuthDtlBegin = personEligibilityDao
											.getSvcAuthDtlBeginByIdStage(saveApprovalStatusReq.getIdStage());

									/*******************************
									 * Insert EA eligibility record *
									 *******************************/
									serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);

									PersonEligibilityDto personEligibilityDto = new PersonEligibilityDto();
									//Modified for QC15352. Set IdPersEligPerson instead of IdPersElig for adding new record.
									personEligibilityDto
											.setIdPersEligPerson(personStagePersonLinkTypeOutDto.getIdPerson());
									personEligibilityDto.setDtPersEligStart(dtSvnAuthDtlBegin);
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.EA_ADD);

									/**
									 *
									 * Method Name: personEligibilityAUD
									 * Method Description:This DAM is called
									 * by 3 services: CCMN35S, CCMN03U, and
									 * CCMN02U. The logic switches on
									 * 'ArchInputStruct.cReqFuncCd'. CCMN35s
									 * and CCMN03U will send in Case '1' or
									 * '2': Case 1: Insert new EA into
									 * PERSON_ELIGIBILITY Case 2: Update EA
									 * open code in PERSON_ELIGIBILITY
									 * CCMN02U will send in Case '3', '4',
									 * '5':\ Case 3: Update Open and Close
									 * code in PERSON ELIGIBILITY. Case 4:
									 * Update Deny date and Close code in
									 * PERSON ELIGIBILITY. Case 5: Update
									 * Deny date, Open code, and Close code
									 * in PERSON ELIGIBILITY.
									 *
									 * @param personEligibilityDto
									 * @param archInputDto
									 */
									personEligibilityDao.personEligibilityAUD(personEligibilityDto,
											serviceReqHeaderDto);

								}
							} /** END: C */
						} /** END: B */
					} /** END: A */
					
					//Once the user selects ‘Open to FBSS’ and hit ‘Save and Submit’, and it is approved, the FRE stage will be closed and progressed to ‘FPR’ stage.
					if(ApprovalConstants.TASK_FRESTAGECLOSURE.equals(saveApprovalStatusReq.getCdTask()) && WebConstants.FRE.equals(saveApprovalStatusReq.getCdStage()) 
							&& ApprovalConstants.OPEN_TO_FBSS_STAGE_CLOSED_CODE.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())) {
						
						Long requestingWorkerID = approvalStatusDao.getPrimaryWorkerIdForStage(saveApprovalStatusReq.getIdStage());
						
						processFREConclusionStage(saveApprovalStatusReq.getIdCase(), saveApprovalStatusReq.getIdStage(),
								requestingWorkerID, saveApprovalStatusReq.getIdApproval(), saveApprovalStatusReq.getIdApprover());
					}
					
					/**
					 * Initialize CloseStageCase Input and Output Structure
					 */
					CloseStageCaseInputDto closeStageCaseInputDto = new CloseStageCaseInputDto();

					/**
					 * get CloseStageCase Input Structure
					 */

					getCloseStageCaseInputDto(saveApprovalStatusReq, closeStageCaseInputDto);

					/**
					 * Service Name: closeStageCase service Description: This
					 * shared library function provides the necessary updates
					 * required to close a stage. If a case and a situation are
					 * associated with the stage, and there are no other open
					 * stages associated with the case, the situation and the
					 * case are also closed.
					 * 
					 * Service Name : CCMN02U
					 */
					CloseStageCaseOutputDto closeStageCaseOutputDto = closeStageCaseService
							.closeStageCase(closeStageCaseInputDto);

					todoDao.completeToDoStageClosure(saveApprovalStatusReq.getIdStage());
					
					if (!ObjectUtils.isEmpty(closeStageCaseOutputDto)) {
						/****************************************************************************
						 * If Stage successfully closed after approval, if SUB
						 * stage with appropriate stage closure reason, if last
						 * open placement in stage is DA or DD, CallCSUB87D to
						 * end last open DA/DD Placement. Also call CCMN45 and
						 * CCMN46.
						 ****************************************************************************/
//artf227805
						if (indOpenDADDPlcmt == Boolean.TRUE && (ServiceConstants.SUBCARE_STAGE
								.equals(saveApprovalStatusReq.getCdStage())
								&& (ServiceConstants.APPR_STAT_NON_SUIT
										.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
								|| ServiceConstants.APPR_STAT_EXIT_TO_TRIBE
								.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.APPR_STAT_PMC_TO_OTHER.equals(
												saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.APPR_STAT_CVS_NOT_OBTAINED.equals(
												saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.APPR_STAT_CHILD_RETURNED_HOME.equals(
												saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.APPR_STAT_CHILD_WITH_RELATIVES.equals(
												saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.APPR_STAT_EMANCIPATED.equals(
												saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.APPR_STAT_AGED_OUT.equals(
												saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.APPR_STAT_CHILD_DEATH.equals(saveApprovalStatusReq
												.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.SVC_COMP.equals(
										saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.UNAB_COMP.equals(
										saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
										|| ServiceConstants.REQ_WITH.equals(saveApprovalStatusReq
										.getAprvlStageProgDto().getCdStageReasonClosed())))) {

							ClosePlacementReq closePlacementReq = new ClosePlacementReq();
							closePlacementReq.setIdPlcmtEvent(tempIdPlcmtEvent);
							closePlacementReq.setScrDtCurrentDate(new Date());

							/**
							 * dtTodaysDate is set to null in legacy...but DB
							 * has Constraint "cannot update
							 * ("CAPS"."PLACEMENT"."DT_PLCMT_END") to NULL"
							 */
							
							closePlacementReq.setCdPlcmtRemovalRsn(tempCdPlcmtRemovalRsn);
							closePlacementReq
									.setIdLastUpdatePerson(saveApprovalStatusReq.getApproveApprovalDto().getIdPerson());

							/**
							 * DAM Name: CSUB87D
							 * 
							 * Description: Updates PLACEMENT table to close an
							 * open DA/DD placement by populating the placement
							 * closure reason and placement end date.
							 * 
							 * This DAM is called from Stage Closure Approval
							 * service CCMN35S.src, when the last placement is
							 * an open DA/DD placement, upon APPROVAL of SUB
							 * stage closure. Parameters in : ID_PLCMT_EVENT
							 */

							closePlacementDao.closePlacement(closePlacementReq);

							/**
							 * CCMN45D dam to retrieve PLACEMENT Event record so
							 * EVENT's txtEventDescr can be ended. The EVENT
							 * description must be ended whenever the placement
							 * is ended.
							 */
							if (!TypeConvUtil.isNullOrEmpty(tempIdPlcmtEvent)) {

								/**
								 * 
								 * Method Description: This Method will return
								 * the Event details from the Event Table for
								 * the given approval id (id event). Dam Name:
								 * CCMN45D
								 * 
								 * DAM Name: CCMN45D
								 * 
								 * @param idEvent
								 * @return EventDto
								 */
								EventDto eventDto = eventDao.getEventByid(tempIdPlcmtEvent);

								/**
								 * CCMN46D PLCMT dam will update placement EVENT
								 * record so EVENT's txtEventDescr can be
								 * updated/ended. The EVENT description must be
								 * ended whenever the placement is ended.
								 */

								Event event = new Event();
								event.setIdEvent(eventDto.getIdEvent());
								//Defect 11109 - Get the persistent object
								Person person = personDao.getPersonByPersonId(eventDto.getIdPerson());
								//person.setIdPerson(eventDto.getIdPerson());
								event.setPerson(person);
								//Defect 11109 - Get the persistent object
								Stage stage = stageDao.getStageEntityById(eventDto.getIdStage());
								event.setStage(stage); // Set persistent object
								event.setDtEventOccurred(eventDto.getDtEventOccurred());
								event.setDtLastUpdate(eventDto.getDtLastUpdate());
								event.setCdEventStatus(eventDto.getCdEventStatus());
								event.setCdTask(eventDto.getCdTask());
								event.setCdEventType(eventDto.getCdEventType());
								// Re-Build the Event Description to add " End
								// MM/DD/YYYY " after 'Act Start
								// MM/DD/YYYY'.
								endDateString.append(eventDto.getEventDescr());
								
								/**
								 * Build the Event Description EndDate string
								 * using Today's Date to event description
								 * ccmn46d
								 */
								
								
								dateFormatter.applyPattern(ServiceConstants.DATE_FORMAT_MMddyyyy);
								
								endDateString.insert(endDateString.indexOf("Start") + 17, EVENT_DESCR_END + dateFormatter.format(new Date()) + " ");
								
								event.setTxtEventDescr(endDateString.toString());
								/**
								 * DAM Name: CCMN46D
								 * 
								 * This method will update the EVENT table based
								 * on ID_EVENT
								 */
								eventDao.updateEvent(event, ServiceConstants.REQ_FUNC_CD_UPDATE);

							} /** end if */
						} /** end */
					} /** end switch after CloseStageCase */
				} /** End Else If - Auto Stage & Case Closure */

				/** END */

				/**
				 * Call close stage case for CRSR Reasons Closed as well as
				 * Adoption Disruption for ADO stages.
				 */
				if (ServiceConstants.SP_AUTOMATIC
						.equals(saveApprovalStatusReq.getAprvlStageProgDto().getWCDCdStageProgressMode())
						&& ServiceConstants.ADOPTION_STAGE.equals(saveApprovalStatusReq.getCdStage())
						&& (ServiceConstants.ADO_CMTD
								.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
								|| ServiceConstants.UNAB_COMP
										.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
								|| ServiceConstants.REQ_WITH
										.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
								|| ServiceConstants.SVC_COMP.equals(
										saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed()))) {

					/**
					 * Initialize CloseStageCase Input Structure
					 */
					CloseStageCaseInputDto closeStageCaseInputDto = new CloseStageCaseInputDto();
					/**
					 * get CloseStageCase Input Structure
					 */

					getCloseStageCaseInputDto(saveApprovalStatusReq, closeStageCaseInputDto);

					/**
					 * This shared library function provides the necessary
					 * updates required to close a stage. If a case and a
					 * situation are associated with the stage, and there are no
					 * other open stages associated with the case, the situation
					 * and the case are also closed.
					 * 
					 * Service Name : CCMN02U
					 * 
					 * @param closeStageCaseInputDto
					 * @return
					 */
					closeStageCaseService.closeStageCase(closeStageCaseInputDto);

				} /** end-if */
				/**
				 * End
				 */

				// artf151569 - FBSS Referral
				if (ServiceConstants.FBSS_REF_TASK_CODE_CPSINV.equals(saveApprovalStatusReq.getCdTask())
					|| ServiceConstants.FBSS_REF_TASK_CODE_CPSAR.equals(saveApprovalStatusReq.getCdTask())) {

					Long requestingWorkerID = approvalStatusDao.getPrimaryWorkerIdForStage(saveApprovalStatusReq.getIdStage());

					createFPRStage(saveApprovalStatusReq.getIdCase(), saveApprovalStatusReq.getIdStage(),
							requestingWorkerID, saveApprovalStatusReq.getIdApproval(), saveApprovalStatusReq.getIdApprover());
				}

				//PPM 60692-artf179778-Start-Approve page changes
				if (ServiceConstants.PLMT_TSK.equals(saveApprovalStatusReq.getCdTask())
						|| ServiceConstants.PLMT_TSK_APRV.equals(saveApprovalStatusReq.getCdTask())) {

					heightenedMonitoringService.populatePlacementEventToHmReq(saveApprovalStatusReq.getIdStage(),
							(long) saveApprovalStatusReq.getEventStatusDto().getIdEvent());
				}
				//PPM 60692-artf179778-End

				/**************************************************************************
				 ** For approval of Sub care, Service Authorization or FA Home, a
				 * standard set of To Do s must be created for the worker. Do
				 * not generate a To do if the Case is going to close.
				 **
				 **************************************************************************/

				for (ApprovalEventLinkEventDto approvalEventOutputDto : approvalEventLinkEventDtoList) {

					/**
					 * For each Event in this list, the Task Code must be
					 * checked to see if it maps to a Child Plan, Service
					 * Authorization or FA Home Event.
					 *
					 * Added a check for Adoption Plan Task Code.
					 *
					 */

					if ((!ServiceConstants.CHILD_PLAN_TASK_CODE_SUB.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.ADOPTION_PLAN.equals(approvalEventOutputDto.getCdTask()))
							&& (ServiceConstants.SP_CASECLOSE.equals(
									saveApprovalStatusReq.getAprvlStageProgDto().getWCDCdStageProgressMode()))) {

						/**
						 * Retrieve a functional record from the Child Plan
						 * table using IdEvent, in order to create ToDos for
						 * Child Plan events.
						 */

						/**
						 * DAM Name: CSES19D
						 * 
						 * Description: This DAM will perform a full row
						 * retrieval for a row in the CHILD PLAN table which has
						 * Id Event equal to host.
						 */
						ChildPlanEventDto childPlanEventDto = fetchPlanDao
								.getChildPlanEvent(approvalEventOutputDto.getIdEvent());

						if (!ObjectUtils.isEmpty(childPlanEventDto)) {
							MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
							TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
							if (!ServiceConstants.INIT_PLAN.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.INIT_PLAN_PAL.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.REVIEW.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.REVIEW_PAL.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.FAC_REVIEW.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.FAC_REVIEW_PAL.equals(childPlanEventDto.getCdCspPlanType())) {
								todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_SUB_DUE_6MOS);
							} else if (!ServiceConstants.ADOPT_PLAN.equals(childPlanEventDto.getCdCspPlanType())) {
								todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_ADO_DUE_6MOS);
							} else if (!ServiceConstants.INIT_PLAN_THER.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.INIT_PAL_THER.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.REVIEW_THER.equals(childPlanEventDto.getCdCspPlanType())
									|| !ServiceConstants.REVIEW_PAL_THER.equals(childPlanEventDto.getCdCspPlanType())) {
								todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_SUB_DUE_3MOS);
							}

							/**
							 * Added 'Next Review Date' field to the Child Plan
							 * Detail page, so pass this date to the
							 * TodoCommonFunction for create the to do for the
							 * next review. The to do date no longer will be
							 * calculated from SYSDATE.
							 */

							mergeSplitToDoDto.setDtTodoCfDueFrom(childPlanEventDto.getDtCspNextReview());
							mergeSplitToDoDto.setIdTodoCfPersAssgn(ServiceConstants.ZERO);
							mergeSplitToDoDto.setIdTodoCfPersCrea(
									Long.valueOf(saveApprovalStatusReq.getApproveApprovalDto().getIdPerson()));

							/**
							 * Pass 0 as EventId
							 */
							mergeSplitToDoDto.setIdTodoCfEvent(ServiceConstants.ZERO);
							mergeSplitToDoDto.setIdTodoCfStage(saveApprovalStatusReq.getIdStage());
							mergeSplitToDoDto.setIdTodoCfPersWkr(ServiceConstants.ZERO);
							todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);

							/** Calling to do Common Function */
							TodoCreateOutDto todoCreateOutDto = commonToDoFunctionService
									.TodoCommonFunction(todoCreateInDto);

							/** Update functional record */

							ChildPlanAUDEvtDetailDto childPlanAUDEvtDetailDto = new ChildPlanAUDEvtDetailDto();

							/**
							 * get Input Structure for DAM
							 */
							childPlanAUDEvtDetailDto.setIdChildPlanEvent(approvalEventOutputDto.getIdEvent());
							childPlanAUDEvtDetailDto.setDtLastUpdate(childPlanEventDto.getDtLastUpdate());
							childPlanAUDEvtDetailDto.setIdPerson(childPlanEventDto.getIdPerson());
							childPlanAUDEvtDetailDto.setCdCspPlanPermGoal(childPlanEventDto.getCdCspPlanPermGoal());
							childPlanAUDEvtDetailDto.setCdCspPlanType(childPlanEventDto.getCdCspPlanType());
							childPlanAUDEvtDetailDto.setDtCspPermGoalTarget(childPlanEventDto.getDtCspPermGoalTarget());
							childPlanAUDEvtDetailDto.setDtCspNextReview(childPlanEventDto.getDtCspNextReview());
							childPlanAUDEvtDetailDto.setDtLastUpdate(todoCreateOutDto.getDtDtTodoDue());
							childPlanAUDEvtDetailDto.setCspLengthOfStay(childPlanEventDto.getCspLengthOfStay());
							childPlanAUDEvtDetailDto.setCspDiscrepancy(childPlanEventDto.getCspDiscrepancy());
							childPlanAUDEvtDetailDto.setCspParticipComment(childPlanEventDto.getCspParticipComment());
							childPlanAUDEvtDetailDto
									.setIndParentsParticipated(childPlanEventDto.getIndParentsParticipated());
							childPlanAUDEvtDetailDto.setDtCspPlanCompleted(childPlanEventDto.getDtCspPlanCompleted());
							childPlanAUDEvtDetailDto
									.setDtInitlTransitionPlan(childPlanEventDto.getDtInitTransitionPlan());
							childPlanAUDEvtDetailDto.setInfoNotAvail(childPlanEventDto.getInfoNotAvail());
							childPlanAUDEvtDetailDto.setOtherAssmt(childPlanEventDto.getOtherAssmt());
							childPlanAUDEvtDetailDto.setIndNoConGoal(childPlanEventDto.getIndNoConGoal());
							childPlanAUDEvtDetailDto.setCdSsccPurpose(childPlanEventDto.getCdSsccPlanPurpose());

							/**
							 * get the request function code in the input
							 * architecture header
							 */
							ServiceInputDto serviceInputDto = new ServiceInputDto();
							serviceInputDto.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
							childPlanAUDEvtDetailDto.setArchInputStruct(serviceInputDto);

							/**
							 * DAM Name: CAUD25D
							 * 
							 * Description: This dam will add, update, & delete
							 * a row from the child plan table. In this case we
							 * are updating the child_Plan_table
							 */
							childPlanInsUpdDelDao.childPlanUpdateOrDelete(childPlanAUDEvtDetailDto);
						} /** end if Child Plan */

					}
					/**
					 * creating the alert for Child Plan Review Due
					 */
					if (ServiceConstants.CHILD_PLAN_TASK_CODE_SUB.equals(approvalEventOutputDto.getCdTask())) {

						ChildPlanEventDto childPlanEventDto = fetchPlanDao
								.getChildPlanEvent(approvalEventOutputDto.getIdEvent());

						TodoDto alertTodoDto = populateChildPlanReviewAlert(saveApprovalStatusReq);
						serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
						alertTodoDto.setDtTodoDue(new Date());
						alertTodoDto.setCdTodoTask(ServiceConstants.CHILD_PLAN_TASK_CODE_SUB);
						StringBuilder todoDescStringBuilder = new StringBuilder("Child Plan Review due for ");
						PersonDto personDto = personDao.getPersonById(childPlanEventDto.getIdPerson());
						//todoDescStringBuilder.append("( ");
						todoDescStringBuilder.append(personDto.getNmPersonFull());
						//todoDescStringBuilder.append(")");
						alertTodoDto.setTodoDesc(todoDescStringBuilder.toString());
						// assigning to the person , so that it can be viewed
						// only external user
						alertTodoDto.setIdTodoPersAssigned(childPlanEventDto.getIdPerson());
						// setting due Date 30 days prior to review date
						alertTodoDto.setDtTodoDue(DateUtils.addDays(childPlanEventDto.getDtCspNextReview(), -30));
						todoDao.todoAUD(alertTodoDto, serviceReqHeaderDto);
						/**
						 * creating the calendar appointment and reminder
						 */
						CommonHelperReq commonHelperReq = new CommonHelperReq();
						List<Long> events = new ArrayList<>();
						events.add(approvalEventOutputDto.getIdEvent());
						commonHelperReq.setIdEventList(events);
						commonHelperReq.setTransactionId(saveApprovalStatusReq.getTransactionId());
						commonHelperReq.setDtCVSRemoval(childPlanEventDto.getDtCspNextReview());
						commonHelperReq.setHostName(saveApprovalStatusReq.getHostName());
						commonHelperReq.setIndReview(true);
						conservatorshipService.fetchEmployeeEmail(commonHelperReq);

					}

					/******************************************************************
					 * First, APS will no longer receive their to dos from
					 * Approval Status - APS workers receive to dos from CCON19S
					 * - SvcAuthSave. Second, CPS to do text and date formatting
					 * will be corrected.
					 ******************************************************************/
					else if ((!ServiceConstants.SA_CPS_FAM_PRES.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.SA_CPS_INVEST.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.SA_CPS_AR.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.SA_CPS_ADOPT.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.SA_CPS_POST_ADOPT.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.SA_CPS_SUBCARE.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.SA_CPS_FAM_REUN.equals(approvalEventOutputDto.getCdTask())
							|| !ServiceConstants.SA_CPS_PAL.equals(approvalEventOutputDto.getCdTask()))
							&& ServiceConstants.SP_CASECLOSE
									.equals(saveApprovalStatusReq.getAprvlStageProgDto().getWCDCdStageProgressMode())) {

						SvcAuthEventLinkInDto svcAuthEventLink = new SvcAuthEventLinkInDto();
						svcAuthEventLink.setIdSvcAuthEvent(approvalEventOutputDto.getIdEvent());

						/**
						 * DAM Name: CSES24D Description: This DAM will retrieve
						 * a row from the SVC_AUTH_EVENT_LINK table based on ID
						 * EVENT.
						 */
						List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtoList = svcAuthEventLinkDao
								.getAuthEventLink(svcAuthEventLink);

						/**
						 * DAM Name : CLSS24D
						 * 
						 * This DAM selects a full row from the svc_auth_detail
						 * with id_svc_auth as input.
						 */
						// this check to ensure svc_auth event list exists
						if (CollectionUtils.isNotEmpty(svcAuthEventLinkOutDtoList)) {
							List<SVCAuthDetailDto> svcAuthDetailDtoList = serviceAuthorizationDao
									.getSVCAuthDetailDtoById(svcAuthEventLinkOutDtoList.get(0).getIdSvcAuth());
							for (SVCAuthDetailDto svcAuthDetailDt : svcAuthDetailDtoList) {

								/**
								 * DAM Name : CINV81D MetodDescription: This
								 * method will return retrieve Full Name by
								 * person id
								 */
								PersonDto personDto = personDao.getPersonById(svcAuthDetailDt.getIdPerson());

								TodoCreateInDto todoCreateInDto = new TodoCreateInDto();

								if (!ServiceConstants.SA_CPS_FAM_PRES.equals(approvalEventOutputDto.getCdTask())
										|| !ServiceConstants.SA_CPS_INVEST.equals(approvalEventOutputDto.getCdTask())
										|| !ServiceConstants.SA_CPS_AR.equals(approvalEventOutputDto.getCdTask())
										|| !ServiceConstants.SA_CPS_ADOPT.equals(approvalEventOutputDto.getCdTask())
										|| !ServiceConstants.SA_CPS_POST_ADOPT
												.equals(approvalEventOutputDto.getCdTask())
										|| !ServiceConstants.SA_CPS_SUBCARE.equals(approvalEventOutputDto.getCdTask())
										|| !ServiceConstants.SA_CPS_FAM_REUN.equals(approvalEventOutputDto.getCdTask())
										|| !ServiceConstants.SA_CPS_PAL.equals(approvalEventOutputDto.getCdTask())

								) {
									todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_SVC_AUTH_CPS);
								} /* end if */

								svcAuthDetailDt.setDtSvcAuthDtlShow(svcAuthDetailDt.getDtSvcAuthDtlTerm());

								/**
								 * Compare the Current Date to the DtlShowDate
								 */
								Long daysDiffernce = TimeUnit.DAYS.convert(
										new Date().getTime() - svcAuthDetailDt.getDtSvcAuthDtlShow().getTime(),
										TimeUnit.MILLISECONDS);
								/**
								 * If the current date is more than 30 days from
								 * the DtlShowDate, set the DtlShowDate to 30
								 * days before the TermDate. Otherwise, set the
								 * DtlShowDate to the current date
								 */

								if (daysDiffernce > THIRTY_DAYS) {
									/**
									 * Set the value of dtCPS add to 30 days
									 * less than the term date
									 */
									svcAuthDetailDt.setDtSvcAuthDtlShow(
											DateUtils.addDays(svcAuthDetailDt.getDtSvcAuthDtlShow(), -30));

								} else {
									svcAuthDetailDt.setDtSvcAuthDtlShow(new Date());
								} /* end if/else */

								todoCreateInDto.setDtSysDtTodoCfDueFrom(svcAuthDetailDt.getDtSvcAuthDtlShow());
								todoCreateInDto.setSysIdTodoCfPersAssgn(ServiceConstants.ZERO);
								todoCreateInDto.setSysIdTodoCfEvent(ServiceConstants.ZERO);
								todoCreateInDto.setSysIdTodoCfStage(saveApprovalStatusReq.getIdStage());
								todoCreateInDto.setSysIdTodoCfPersWkr(ServiceConstants.ZERO);

								/**
								 * re-format the TodoTxtDescr
								 */
								StringBuilder sysTxtTodoCfDescBuilder = new StringBuilder("Case ");
								sysTxtTodoCfDescBuilder.append(saveApprovalStatusReq.getIdCase());
								sysTxtTodoCfDescBuilder.append(" - SA ");
								sysTxtTodoCfDescBuilder.append(personDto.getNmPersonFull());
								sysTxtTodoCfDescBuilder.append(" - ");
								/**
								 * Get the decode value of the SvcAuthService
								 */
								sysTxtTodoCfDescBuilder.append(
										lookupDao.decode(CodesConstant.CSVCCODE, svcAuthDetailDt.getCdSvcAuthDtlSvc()));
								sysTxtTodoCfDescBuilder.append(" expires on ");
								sysTxtTodoCfDescBuilder.append("");

								todoCreateInDto.setSysTxtTodoCfDesc(sysTxtTodoCfDescBuilder.toString());
								todoCreateInDto.setMergeSplitToDoDto(new MergeSplitToDoDto());
								/**
								 * Calling todoCommonFunction
								 */
								commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
							} /** end for loop */
						} // end if for list check

					} /** end else if Service Auth */

					/*******************************************************************
					 **
					 *********************************************************************/
					else if (ServiceConstants.TASK_MNTN_LIC.equals(approvalEventOutputDto.getCdTask())) {
						if (ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {

							/**
							 * Dam Name: CMSC46D
							 * 
							 * DAM Description: This Dam will count the resource
							 * history based on the ID_RSHS_FA_HOME_STAGE and
							 * CD_RSHS_FA_HOME_STATUS
							 */
							ResourceHistoryCountRes resourceHistoryCountRes = resourceHistoryCountDao
									.resourceHistoryCount(getResourceHistoryCountReq(saveApprovalStatusReq));
							/**
							 * If there are no rows returned
							 */
							if (!ObjectUtils.isEmpty(resourceHistoryCountRes)
									&& ServiceConstants.ZERO.equals(resourceHistoryCountRes.getSysNbrGenericCntr())) {

								/** Create To do */
								TodoCreateInDto todoCreateInDto = new TodoCreateInDto();

								/**
								 * Copy properties from into Service Request
								 * Header DTO from saveApprovalStatusReq to
								 * todoCreateInDto
								 */

								BeanUtils.copyProperties(todoCreateInDto,
										cloneServiceReqHeaderDto(saveApprovalStatusReq));

								MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
								mergeSplitToDoDto.setCdTodoCf(ServiceConstants.TODO_FA_HOME);
								mergeSplitToDoDto.setDtTodoCfDueFrom(ServiceConstants.NULL_DATE_TYPE);
								mergeSplitToDoDto.setIdTodoCfPersCrea(saveApprovalStatusReq.getIdPerson());
								mergeSplitToDoDto.setIdTodoCfEvent(ServiceConstants.ZERO);
								mergeSplitToDoDto.setIdTodoCfStage(saveApprovalStatusReq.getIdStage());
								todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);

								/**
								 * Service Name: CSUB40U
								 */
								commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
							} /** close if */

							/**
							 * Only execute the following DAM call if the
							 * previous call was successful and Create an Event
							 */

							/**
							 * Populate DAM Input Structure
							 */
							PostEventReq postEventReq = new PostEventReq();
							postEventReq.setSzCdTask(ServiceConstants.TASK_MNTN_LIC);
							postEventReq.setTsLastUpdate(ServiceConstants.NULL_DATE_TYPE);
							postEventReq.setSzCdEventStatus(ServiceConstants.EVENT_STATUS_APRV);
							postEventReq.setSzCdEventType(CodesConstant.CEVNTTYP_HME);
							postEventReq.setDtDtEventOccurred(ServiceConstants.NULL_DATE_TYPE);
							postEventReq.setUlIdEvent(ServiceConstants.ZERO);
							postEventReq.setUlIdStage(saveApprovalStatusReq.getIdStage());
							postEventReq.setUlIdPerson(saveApprovalStatusReq.getIdPerson());
							postEventReq.setSzTxtEventDescr(EVENT_DESC_HOME_APPROVED);
							postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

							/**
							 * DAM Name: CCMN01U
							 * 
							 * Description: The purpose of this dam
							 * (ccmn01dQUERYdam) is to retrieve data for the
							 * Office Assignments ListBox on the Office Detail
							 * window. It retrieves the name, id and bjn for all
							 * employees within an office.
							 */
							PostEventRes postEventRes = eventService.postEvent(postEventReq);

							// Clone Service Input Header
							serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);

							/** CREATE AN APPROVAL LINK */
							ApprovalEventLinkDto approvalEventLinkDto = new ApprovalEventLinkDto();
							approvalEventLinkDto.setIdApproval(saveApprovalStatusReq.getIdApproval());
							approvalEventLinkDto.setIdEvent(postEventRes.getUlIdEvent());

							/**
							 * Populate the request function code in the input
							 * architecture header
							 */
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);

							/**
							 * DAM Name: CCMN91D Description: This Method is
							 * will Add several records and delete the records
							 * to the APPROVAL_EVENT_LINK table based on the
							 * input & request indicator
							 */
							approvalEventLinkDao.getApprovalEventLinkAUD(serviceReqHeaderDto, approvalEventLinkDto);

							CapsResourceDto capsResourceDto = new CapsResourceDto();
							capsResourceDto.setCdRsrcFaHomeStatus(ServiceConstants.HOME_APPRV_ACTIVE_STATUS);
							capsResourceDto.setCdRsrcStatus(ServiceConstants.RSRC_STAT_ACTIVE);
							capsResourceDto.setIdEvent(postEventRes.getUlIdEvent());
							capsResourceDto.setIdStage(saveApprovalStatusReq.getIdStage());
							capsResourceDto.setIndRsrcWriteHist(ServiceConstants.YES);
							capsResourceDto.setDtApproversDetermination(
									saveApprovalStatusReq.getApproveApprovalDto().getDtApproversDetermination());
							capsResourceDto.setIdApproval(saveApprovalStatusReq.getIdApproval());
							serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

							/**
							 * DAM Name: CAUDB3D
							 * 
							 * Description: Description: This DAM is used by the
							 * Approval Save screen to update specific columns
							 * on the CAPS RESOURCE table.
							 */
							capsResourceDao.updateCapsResourceAUD(capsResourceDto, serviceReqHeaderDto);

							RtrvRsrcByStageInDto rtrvRsrcByStageInDto = new RtrvRsrcByStageInDto();
							rtrvRsrcByStageInDto.setIdRsrcFaHomeStage(saveApprovalStatusReq.getIdStage());
							RtrvRsrcByStageOutDto rtrvRsrcByStageOutDto = new RtrvRsrcByStageOutDto();

							/**
							 * DAM Name: CSES41D Method Name: This Dam will
							 * retrieve a row from CAPS_RESORCE given ID_STAGE
							 */

							List<RtrvRsrcByStageOutDto> rtrvRsrcByStageOutDtoList = retrieveCapsResourceDao
									.retrieveCapsResource(rtrvRsrcByStageInDto, rtrvRsrcByStageOutDto);
							for (RtrvRsrcByStageOutDto rtrvRsrcByStageOutputDto : rtrvRsrcByStageOutDtoList) {

								/**
								 * Set local variable to DAM Output IdResource
								 */
								idResource = rtrvRsrcByStageOutputDto.getUlIdResource();
								/** Set adoptiveOrFoster based on home type */
								adoptiveOrFoster = 0;
								if (!ServiceConstants.SA_FOSTER.equals(rtrvRsrcByStageOutputDto.getSzCdRsrcCategory())
										|| !ServiceConstants.LEGAL_RISK
												.equals(rtrvRsrcByStageOutputDto.getSzCdRsrcCategory())) {
								}

								if (ServiceConstants.ADOPTIVE
										.equals(rtrvRsrcByStageOutputDto.getSzCdRsrcCategory())) {
									foster = Boolean.FALSE;
								} /** End */

								/**
								 * Different processing depending upon
								 * RsrcCategory
								 */
								/**
								 * If statement was wrongly checking for 0 !=
								 * strcmp. If the category is ADOPTIVE, then the
								 * FLOC will be end dated(as FLOC no longer
								 * applys to ADOPTIVE), else the FLOC process
								 * will be done.
								 */

								/**
								 * removed if condition to check SzCdRsrcCategory is equals to FOSTER_ADOPTIVE
								 * Artifact artf250900 : Dev only - Service Level Resource Fac page FAD home
								 * request approval process if the category is Adoptive execute the else condition- to end the hold
								 * and create a new line with Active status.
								 */
									/**
									 * If Category is not Adopt, retrieve the
									 * currently effective Facilility_LOC row,
									 * see if an HOLD statuses need to be
									 * updated to APPROVED. If so, call a DAM to
									 * end date the currently effective LOC row
									 * & insert the new LOC row.
									 */
									/**
									 * Populate Input Structure for DAM
									 */
									FacilityLocInDto facilityLocInDto = new FacilityLocInDto();
									facilityLocInDto.setIdResource(rtrvRsrcByStageOutputDto.getUlIdResource());
									facilityLocInDto.setPlcmtStartDate(new Date());

									/**
									 * DAM Name: CSEC25D
									 * 
									 * Description: This DAM will select a row
									 * from FACILITY_LOC that falls the given
									 * dates when passed an ID_RESOURCE
									 */
									List<FacilityLocDto> facilityLocDtoList = facilityLocDao
											.getFclityLocByResourceId(facilityLocInDto);
									List<FacilityLocDto> toUpdateFacilityLocDtoList = new ArrayList<>();
									for (FacilityLocDto facilityLocDto : facilityLocDtoList) {

										/**
										 * CAPS RESOURCE Save Processing
										 */
										if (ServiceConstants.FLOC_HOLD.equals(facilityLocDto.getCdFlocStatus1())) {
											facilityLocDto.setCdFlocStatus1(ServiceConstants.FLOC_ACTIVE);
											change = Boolean.TRUE;
										}
										if (ServiceConstants.FLOC_HOLD.equals(facilityLocDto.getCdFlocStatus2())) {
											facilityLocDto.setCdFlocStatus2(ServiceConstants.FLOC_ACTIVE);
											change = Boolean.TRUE;
										}
										if (ServiceConstants.FLOC_HOLD.equals(facilityLocDto.getCdFlocStatus3())) {
											facilityLocDto.setCdFlocStatus3(ServiceConstants.FLOC_ACTIVE);
											change = Boolean.TRUE;
										}
										if (ServiceConstants.FLOC_HOLD.equals(facilityLocDto.getCdFlocStatus4())) {
											facilityLocDto.setCdFlocStatus4(ServiceConstants.FLOC_ACTIVE);
											change = Boolean.TRUE;
										}
										if (ServiceConstants.FLOC_HOLD.equals(facilityLocDto.getCdFlocStatus5())) {
											facilityLocDto.setCdFlocStatus5(ServiceConstants.FLOC_ACTIVE);
											change = Boolean.TRUE;
										}
										if (ServiceConstants.FLOC_HOLD.equals(facilityLocDto.getCdFlocStatus6())) {
											facilityLocDto.setCdFlocStatus6(ServiceConstants.FLOC_ACTIVE);
											change = Boolean.TRUE;
										}

										if (change) {
											/**
											 * Populate Input Structure for DAM
											 */
											FacilityLocDto newFacilityLocDto = new FacilityLocDto();
											newFacilityLocDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
											newFacilityLocDto.setIdResource(facilityLocDto.getIdResource());
											newFacilityLocDto.setCdFlocStatus1(facilityLocDto.getCdFlocStatus1());
											newFacilityLocDto.setCdFlocStatus2(facilityLocDto.getCdFlocStatus2());
											newFacilityLocDto.setCdFlocStatus3(facilityLocDto.getCdFlocStatus3());
											newFacilityLocDto.setCdFlocStatus4(facilityLocDto.getCdFlocStatus4());
											newFacilityLocDto.setCdFlocStatus5(facilityLocDto.getCdFlocStatus5());
											newFacilityLocDto.setCdFlocStatus6(facilityLocDto.getCdFlocStatus6());
											newFacilityLocDto.setCdFlocStatus7(facilityLocDto.getCdFlocStatus7());
											newFacilityLocDto.setCdFlocStatus8(facilityLocDto.getCdFlocStatus8());
											newFacilityLocDto.setCdFlocStatus9(facilityLocDto.getCdFlocStatus9());
											newFacilityLocDto.setCdFlocStatus10(facilityLocDto.getCdFlocStatus10());
											newFacilityLocDto.setCdFlocStatus11(facilityLocDto.getCdFlocStatus11());
											newFacilityLocDto.setCdFlocStatus12(facilityLocDto.getCdFlocStatus12());
											newFacilityLocDto.setCdFlocStatus13(facilityLocDto.getCdFlocStatus13());
											newFacilityLocDto.setCdFlocStatus14(facilityLocDto.getCdFlocStatus14());
											newFacilityLocDto.setCdFlocStatus15(facilityLocDto.getCdFlocStatus15());
											newFacilityLocDto
													.setNbrFlocLevelsOfCare(facilityLocDto.getNbrFlocLevelsOfCare());
											newFacilityLocDto.setDtFlocEffect(new Date());
											/**
											 * End date to MAX_DATE instead of
											 * NULL_DATE.
											 */
											newFacilityLocDto.setDtFlocEnd(ServiceConstants.MAX_DATE);
											/** End */
											newFacilityLocDto.setIndFlocCancelHist(ServiceConstants.YES);
											toUpdateFacilityLocDtoList.add(newFacilityLocDto);
										} /** end if - (if bChange == TRUE) */
									} /** END Loop for facilityLocDtoList */

									/**
									 * If any of the above changes has taken
									 * place, call CAUD80D to update the end
									 * date of the previous row and insert the
									 * new row
									 */
									if (CollectionUtils.isNotEmpty(toUpdateFacilityLocDtoList)) {
										UpdateFacilityLocReq updateFacilityLocReq = new UpdateFacilityLocReq();
										updateFacilityLocReq.setIdResource(rtrvRsrcByStageOutputDto.getUlIdResource());
										updateFacilityLocReq.setDtFlocEnd(new Date());
										updateFacilityLocDao.updateFacilityLoc(updateFacilityLocReq);

										FacilityDetailSaveReq facilityDetailSaveReq = new FacilityDetailSaveReq();
										facilityDetailSaveReq.setFacilityLocList(toUpdateFacilityLocDtoList);
										FacilityDetailRes facilityDetailRes = new FacilityDetailRes();

										/**
										 * DAM Name: CAUD80D
										 * 
										 * Description: This dam will update Dt
										 * Floc End to current date where Dt
										 * Floc End = Max Date and will then add
										 * this row or delete rows from the
										 * Facility Loc table.
										 */
										facilityLocDao.updateFacilityLoc(facilityDetailSaveReq, facilityDetailRes);
									}
							} /**
								 * END Loop for rtrvRsrcByStageOutDtoList; END
								 * CSES41D processing
								 */
						}

						/**
						 * DAM Name: CRES04D
						 * 
						 * Description: This method performs AUD functionality
						 * on the FACILITY INVST DTL table. This DAM
						 */
						ResourceDto resourceDto = capsResourceDao.getResourceById(idResource);

						/**************************************************************************
						 ***************************************************************************
						 **
						 ** CONTRACT CREATION/MODIFICATION
						 **
						 ***************************************************************************
						 **************************************************************************/
						/**
						 * Begin contract creation/modification process if the
						 * save service is successful up to this point
						 */

						if (!TypeConvUtil.isNullOrEmpty(resourceDto)) {

							/** Set adoptiveOrFoster based on home type */
							ADOPTIVE_OR_FOSTER_LIST = getFosterHomeTypes(resourceDto);

							/** END */

							/************************************************************************
							 ** (BEGIN): Contracts existence determination. Is
							 * there an open foster and adoptive contract for
							 * the home?
							 ************************************************************************/

							/**************************************************************************
							 ** (BEGIN): CLSS67D - List retrieval of Contract
							 * rows for and id resource.
							 **************************************************************************/

							/**
							 * IdResource is retrieved in cses41d above
							 */
							/**
							 * DAM Name: CLSS67D
							 * 
							 * Description: Retrieves CONTRACT table based on
							 * Id_Resource.
							 */

							List<ContractDto> contractDtlList = placementDao.getContractDtl(idResource);
							/**
							 * Loop through all contract rows returned from the
							 * previous DAM
							 */
							for (ContractDto contractDto : contractDtlList) {
								ContractOutputDto contractOutputDto = new ContractOutputDto();
								contractOutputDto.setIdContract(contractDto.getIdContract());
								contractOutputDto.setTsLastUpdate(contractDto.getDtLastUpdate());
								contractOutputDto.setSysIndContractCurrent(ServiceConstants.N_CHAR);
								contractOutputDto.setIdCntrctManager(contractDto.getIdContractMngr());

								/**
								 * DAM Name: CSES80D
								 * 
								 * Method Name: getContractPeriodByIdContract
								 * MEthod Description: Retrieves CONTRACT based
								 * on id_contract from CONTRACT_PERIOD table
								 */
								List<ContractPeriodDto> contractPeriodList = placementDao
										.getContractPeriodByIdContract(contractOutputDto.getIdContract());

								for (ContractPeriodDto contractPeriod : contractPeriodList) {
									contractOutputDto.setDtCnperTerm(contractPeriod.getDtCnperTerm());
									contractOutputDto.setDtCnperClosure(contractPeriod.getDtCnperClosure());
									contractOutputDto.setDtCnperStart(contractPeriod.getDtCnperStart());
									contractOutputDto
											.setNbrCnperPeriod((int) contractPeriod.getIdContractPeriod());
									contractOutputDto.setSysTsLastUpdate2(contractPeriod.getDtLastUpdate());
									/**
									 * Changed "extend o" date processing to
									 * have == instead of =
									 */
									/**
									 * Potential for difference in dates being
									 * too large prevented using compare date
									 * and time. Instead, "extend" compare date
									 * processing is being used
									 */
									/**
									 * If year is greater OR If years are equal
									 * and month is greater OR If years and
									 * months are equal and day is greater OR If
									 * year, month and day are equal
									 */

									if (contractOutputDto.getDtCnperTerm().after(new Date())
											|| contractOutputDto.getDtCnperTerm() == new Date()) {
										testBool = Boolean.TRUE;
									}

									/**
									 * END "extend o" compare date processing
									 */
									if (testBool) {
										if (!ServiceConstants.CONTRACT_STATUS_CLOSED
												.equals(contractPeriod.getCdCnperStatus())) {
											contractOutputDto.setSysIndContractCurrent(ServiceConstants.CHAR_Y);
										} else {
											contractOutputDto.setSysIndContractCurrent(ServiceConstants.N_CHAR);
										}
									} else {
										contractOutputDto.setSysIndContractCurrent(ServiceConstants.N_CHAR);
									}
								} /** END LOOP: CSES80DO: contractPeriodList */

								/*******************************************************************
								 ** (END) CSES80D: Retrieve Contract Period table
								 * information
								 *******************************************************************/

								contractOutputDtoList.add(contractOutputDto);

							} /**
								 * end loop to run through all contracts returned
								 * from CLSS67D
								 */
							/** END for CLSS67D */

							/**************************************************************************
							 ** (END): CLSS67D - List retrieval of Contract rows
							 * for and id resource.
							 **************************************************************************/

							/**
							 * Loop through all contract rows returned from the
							 * previous DAMs
							 */
							if (CollectionUtils.isNotEmpty(contractDtlList)) {
								for (ContractOutputDto contractOutputDto : contractOutputDtoList) {
									if (!ObjectUtils.isEmpty(contractOutputDto.getSysIndContractCurrent()) &&
											ServiceConstants.CHAR_Y == contractOutputDto.getSysIndContractCurrent()) {
										/****************************************************************
										 ** (BEGIN): CSES81D - Contract Version
										 * retrieve for an idContract , contract
										 * period number, and version end date
										 * that is greater than the current
										 * date.
										 ****************************************************************/

										/** Set Input structure for CSES81D */
										ContractVersionInDto contractVersionInDto = new ContractVersionInDto();
										contractVersionInDto.setIdContract(contractOutputDto.getIdContract());
										contractVersionInDto
												.setNbrCnverPeriod(Long.valueOf(contractOutputDto.getNbrCnperPeriod()));

										/**
										 * DAM NAME: CSES81D
										 * 
										 * Method Description: retrieves a full
										 * row from CONTRACT VERSION given the
										 * ID_CONTRACT and NBR_CNVER_PERIOD.
										 */
										List<ContractVersionOutDto> contractVersionOutDtoList = contractVersionDao
												.getContractVersionByNbrCnverPeriod(contractVersionInDto);

										for (ContractVersionOutDto contractVersionOutDto : contractVersionOutDtoList) {
											contractOutputDto
													.setNbrCnverVersion(contractVersionOutDto.getNbrCnverVersion());
											contractOutputDto
													.setSysTsLastUpdate3(contractVersionOutDto.getTsLastUpdate());
										}

										/*********************************************************
										 * Retrieve contract service codes for
										 * the contract, period, and version
										 * passed to the DAM.
										 *********************************************************/

										ContractServiceInDto contactServiceInDto = new ContractServiceInDto();
										contactServiceInDto.setIdContract(contractOutputDto.getIdContract());

										/**
										 * DAM Name - CLSS13D
										 * 
										 * Method Description: retrieves a full
										 * row from CONTRACT_SERVICE given the
										 * ID_CONTRACT.
										 */
										List<ContractServiceOutDto> contractServiceOutDtoList = contractServiceDao
												.getContractService(contactServiceInDto);

										rowCounter = contractServiceOutDtoList.size();
										// rowCounterOne =
										// contractServiceOutDtoList.size();

										List<ContractServiceOutDto> tempContractServiceOutDtoList = new ArrayList<>();

										for (ContractServiceOutDto contractServiceOutDtl : contractServiceOutDtoList) {
											ContractServiceOutDto tempContractServiceOutDto = new ContractServiceOutDto();
											tempContractServiceOutDto
													.setSysTsLastUpdate5(contractServiceOutDtl.getDtLastUpdate());
											tempContractServiceOutDto
													.setNbrCnsvcVersion(contractServiceOutDtl.getNbrCnsvcVersion());
											tempContractServiceOutDto.setIdCnsvc(contractServiceOutDtl.getIdCnsvc());
											tempContractServiceOutDto
													.setNbrCnsvcLineItem(contractServiceOutDtl.getNbrCnsvcLineItem());
											tempContractServiceOutDto
													.setNbrCnsvcUnitRate(contractServiceOutDtl.getNbrCnsvcUnitRate());
											tempContractServiceOutDto.setAmtCnsvcUnitRateUsed(
													contractServiceOutDtl.getAmtCnsvcUnitRateUsed());
											tempContractServiceOutDto
													.setCdCnsvcUnitType(contractServiceOutDtl.getCdCnsvcUnitType());
											tempContractServiceOutDto
													.setCdCnsvcService(contractServiceOutDtl.getCdCnsvcService());

											/**
											 * Compare against the new F/A home
											 * codes
											 */

											if (ServiceConstants.CD_SERV_FOST_L1
													.equals(tempContractServiceOutDto.getCdCnsvcService())
													|| ServiceConstants.CD_SERV_FOST_L2
															.equals(tempContractServiceOutDto.getCdCnsvcService())
													|| ServiceConstants.CD_SERV_FOST_LEV_BASIC
															.equals(tempContractServiceOutDto.getCdCnsvcService())
													|| ServiceConstants.CD_SERV_FOST_LEV_MOD
															.equals(tempContractServiceOutDto.getCdCnsvcService())
													|| ServiceConstants.CD_SERV_FOST_LEV_SPEC
															.equals(tempContractServiceOutDto.getCdCnsvcService())
													|| ServiceConstants.CD_SERV_FOST_LEV_INT
															.equals(tempContractServiceOutDto.getCdCnsvcService())) {
												/**
												 * if service code is a foster
												 * code
												 */
												indFosterContractExists = Boolean.TRUE;
												indUpdateFosterContract = Boolean.TRUE;
											} /**
												 * end if service code is a foster
												 * code
												 */

											if (ServiceConstants.CD_SERV_ADP_SUB
													.equals(tempContractServiceOutDto.getCdCnsvcService())
													|| ServiceConstants.CD_SERV_ADP_NON_REC_SUB
															.equals(tempContractServiceOutDto.getCdCnsvcService())) { /**
																														 * if
																														 * service
																														 * code
																														 * is
																														 * a
																														 * adoptive
																														 * code
																														 */
												indAdoptContractExists = Boolean.TRUE;
												indUpdateAdoptContract = Boolean.TRUE;
											} /**
												 * end if service code is a
												 * adoptive code
												 */

											/**
											 * Added following block for PCA
											 * contract
											 */
											if (ServiceConstants.CD_SERV_PCA_SUB
													.equals(tempContractServiceOutDto.getCdCnsvcService())
													|| ServiceConstants.CD_SERV_PCA_NON_REC_SUB
															.equals(tempContractServiceOutDto.getCdCnsvcService())) {
												/**
												 * if service code is a adoptive
												 * code
												 */
												indPCAContractExists = Boolean.TRUE;
												indUpdatePCAContract = Boolean.TRUE;
											} /**
												 * end if service code is a PCA
												 * code
												 */

											tempContractServiceOutDtoList.add(tempContractServiceOutDto);
										}
										contractOutputDto.setContractServiceOutDtoList(tempContractServiceOutDtoList);
										/*********************************************************
										 * Retrieve contract service codes for
										 * the contract, period, and version
										 * passed to the DAM.
										 *********************************************************/

										/*********************************************************
										 * Retrieve contract service codes for
										 * the contract, period, and version
										 * passed to the DAM.
										 *********************************************************/
										if (rowCounter >= ServiceConstants.Zero) {
											List<ContractCountyOutDto> tempContractCountyOutDtoList = new ArrayList<>();

											/**
											 * prepare input structure for
											 * getContractCounty (CLSS68D)
											 */
											ContractCountyInDto contractCountyInDto = new ContractCountyInDto();
											contractCountyInDto.setIdContract(contractOutputDto.getIdContract());
											contractCountyInDto
													.setNbrCncntyPeriod(contractOutputDto.getNbrCnperPeriod());
											contractCountyInDto
													.setNbrCncntyVersion(contractOutputDto.getNbrCnverVersion());

											/**
											 * DAM NAME: CLSS68D
											 * 
											 * DAM Description: retrieves a full
											 * row from CONTRACT COUNTY given
											 * the ID_CONTRACT, NBR_CNVER_PERIOD
											 * and NBR_CNCNTY_VERSION.
											 */
											List<ContractCountyOutDto> contractCountyOutDtoList = contractCountyDao
													.getContractCounty(contractCountyInDto);

											rowCounter = contractCountyOutDtoList.size();

											/**
											 * Set fields in CFAD08SI to fields
											 * in CLSS68DO for each contract
											 * service row returned.
											 */

											/**
											 * Set variable to hold number of
											 * contract services for a contract
											 */
											contractOutputDto.setUlSysNbrGenericCntr(
													Long.valueOf(contractCountyOutDtoList.size()));
											for (ContractCountyOutDto contractCountyOutDto : contractCountyOutDtoList) {
												ContractCountyOutDto tempContractCountyOutDto = new ContractCountyOutDto();
												tempContractCountyOutDto.setCdCncnctyService(
														contractCountyOutDto.getCdCncnctyService());
												tempContractCountyOutDto
														.setSysTsLastUpdate4(contractCountyOutDto.getDtLastUpdate());
												tempContractCountyOutDto
														.setIdCncnty(contractCountyOutDto.getIdCncnty());
												tempContractCountyOutDto
														.setCdCncnctyCounty(contractCountyOutDto.getCdCncnctyCounty());
												tempContractCountyOutDto.setDtCncnctyEffective(
														contractCountyOutDto.getDtCncnctyEffective());
												/**
												 * The DtCncntyEnd was not being
												 * copied from the DAM output to
												 * the CFAD01 holding variable
												 */
												tempContractCountyOutDto
														.setDtCncnctyEnd(contractCountyOutDto.getDtCncnctyEnd());
												tempContractCountyOutDto
														.setNbrCncntyPeriod(contractCountyOutDto.getNbrCncntyPeriod());
												tempContractCountyOutDto.setNbrCncntyVersion(
														contractCountyOutDto.getNbrCncntyVersion());
												tempContractCountyOutDto.setNbrCncntyLineItem(
														contractCountyOutDto.getNbrCncntyLineItem());

												tempContractCountyOutDtoList.add(tempContractCountyOutDto);
											}
											contractOutputDto.setContractCountyOutDtoList(tempContractCountyOutDtoList);
										}
										/*********************************************************
										 ** (END): CLSS13D - Retrieve contract
										 * service codes for the contract,
										 * period, and version passed to the
										 * DAM.
										 *********************************************************/
										/****************************************************************
										 ** (END): CSES81D - Contract Version
										 * retrieve for an idContract , contract
										 * period number, and version end date
										 * that is greater than the current
										 * date.
										 ****************************************************************/
									} /** End if bIndConractCurrent is TRUE */
								} /** End for loop contractOutputDtoList */
							} /**
								 * End if contractDtlList NOT Empty (i.e.
								 * FND_SUCCESS)
								 */

							/************************************************************************
							 ** (END): Contracts existence determination. Is
							 * there an open foster and adoptive contract for
							 * the home?
							 ************************************************************************/
						} /**
							 * End if resourceDto FOUND for given idResource
							 * (i.e.FND_SUCCESS)
							 */

						/************************************************************************
						 ** (BEGIN): Rule creation of PCA contract when FAD home
						 * types does not have Relative or Active Kin
						 ************************************************************************/
						for (String hometype : ADOPTIVE_OR_FOSTER_LIST) {
							if (ServiceConstants.FOST_TYPE_RELATIVE.toString().equals(hometype)
									|| ServiceConstants.FOST_TYPE_FICTIVE_KIN.toString().equals(hometype)) {
								relFictiveKinHome = Boolean.TRUE;
								break;
							}
						}

						/**
						 * if it is not a relative or fictive kin type of home
						 * then we should not create or modify PCA contract
						 */
						if (relFictiveKinHome == Boolean.FALSE) {
							indPCAContractExists = Boolean.TRUE; // this will
																	// not
																	// create
																	// PCA
																	// contract
							indUpdatePCAContract = Boolean.FALSE; // this will
																	// not
																	// update
																	// PCA
																	// contract
						}

						/************************************************************************
						 ** (End): Rule creation of PCA contract when FAD home
						 * types does not have Relative or Fictive Kin
						 ************************************************************************/

						/************************************************************************
						 ** (BEGIN): Contract creation process if the contract
						 * does not already exist.
						 ************************************************************************/

						/*******************************************************************
						 ** (BEGIN) CINT20D: Retrieve Primary Worker from Stage
						 * Person Link.
						 *******************************************************************/

						/**
						 * DAM NAME: CINT20D Method Name: stagePersonDtls Method
						 * Description: This method will get data from Stage
						 * Person Link table.
						 * 
						 * @param pInputDataRec
						 * @return List<StagePersonLinkStTypeRoleOutDto>
						 */
						if (idResource > 0) { // since idResource not found we
												// do not need to create
												// Contracts
							List<StagePersonLinkStTypeRoleOutDto> primaryWorkerForStagePersonLinkList = stagePersonLinkStTypeRoleDao
									.stagePersonDtls(getStagePersonLinkStTypeRoleInDto(saveApprovalStatusReq,
											ServiceConstants.PERSON_STAGE_ROLE_PRIMARY,
											ServiceConstants.PERSON_TYPE_WORKER));

							for (StagePersonLinkStTypeRoleOutDto primaryWorkerForStagePersonLink : primaryWorkerForStagePersonLinkList) {
								if (CollectionUtils.isEmpty(contractOutputDtoList)) {
									// This could avoid most of the Nullpointers
									// for Contract related things below
									ContractOutputDto contractOutputDto = new ContractOutputDto();
									contractOutputDtoList.add(contractOutputDto);
									(contractOutputDtoList.get(firstRecord))
											.setIdCntrctManager(primaryWorkerForStagePersonLink.getIdPerson());
								}
							}

							/*******************************************************************
							 ** (END) CINT20D: Retrieve Primary Worker from Stage
							 * Person Link.
							 *******************************************************************/

							/**
							 * Add if FND_SUCCESS = RetVal check around this
							 * logic.
							 */

							/************************************************************************
							 ** (BEGIN): CRES13D Retrieve Resource Address
							 ************************************************************************/

							/**
							 * DAM Name: CRES13D This DAM retrieves all of the
							 * Address data given a resource ID.
							 */
							List<ResourceAddressDto> resourceAddressDtoList = capsResourceDao
									.getResourceAddress(idResource);

							for (ResourceAddressDto resourceAddressDto : resourceAddressDtoList) {
								/**
								 * Changed to business from primary and added
								 * VID validation
								 */
								/**
								 * Copy idResourceAddress if the address type if
								 * "business" and has a valid VID. (There can be
								 * more than one business address but only one
								 * with a VID.)
								 */
								if (ServiceConstants.RSRC_BUIS_ADDR.equals(resourceAddressDto.getCdRsrcAddrType())
										&& !ServiceConstants.STR_ZERO_VAL.equals(resourceAddressDto.getNbrRsrcAddrVid())) {
									tempIdRsrcAddress = resourceAddressDto.getIdRsrcAddress();
									break;
								}

							} /** if FND_SUCCESS == RetVal */

							/************************************************************************
							 ** (END): Retrieve Resource Address
							 ************************************************************************/

							/****************************************************************
							 ** (BEGIN): CSES41D - Retrieve Resource Info
							 ****************************************************************/
							/** Call CSES41D to get IdStage */
							RtrvRsrcByStageInDto rtrvRsrcByStageInputDto = new RtrvRsrcByStageInDto();
							rtrvRsrcByStageInputDto.setIdRsrcFaHomeStage(saveApprovalStatusReq.getIdStage());
							RtrvRsrcByStageOutDto rtrvRsrcByStageOutputDto = new RtrvRsrcByStageOutDto();

							/**
							 * DAM Name: CSES41D DAM Description: This Dam will
							 * retrieve a row from CAPS_RESORCE given ID_STAGE
							 */
							List<RtrvRsrcByStageOutDto> rtrvRsrcByStageOutputDtoList = retrieveCapsResourceDao
									.retrieveCapsResource(rtrvRsrcByStageInputDto, rtrvRsrcByStageOutputDto);

							for (RtrvRsrcByStageOutDto rtrvRsrcByStageDto : rtrvRsrcByStageOutputDtoList) {
								countyFromResource = rtrvRsrcByStageDto.getSzCdRsrcCnty();
							}

							/****************************************************************
							 ** (END): CSES41D - Resource info retrieve
							 ****************************************************************/

							/**
							 * Add if FND_SUCCESS = RetVal check around this
							 * logic.
							 */

							if (ServiceConstants.COUNTY_CD_OUT_OF_STATE.equals(countyFromResource)) {
								/********************************************************************
								 ** (BEGIN): CCMN39D - Retrieve Primary Worker's
								 * Region
								 ********************************************************************/

								/**
								 * DAM Name: CCMN39D DAM Description: This DAM
								 * receives an ID PERSON and a CD UNIT MEMBER
								 * ROLE and returns a full row from the UNIT
								 * table, a full row from the UNIT EMP LINK
								 * table and NM PERSON FULL from the PERSON
								 * table. The returned information applies to
								 * the unit to which the ID PERSON is assigned
								 * with the given CD UNIT MEMBER ROLE. The NM
								 * PERSON FULL is the name of the Unit Approver
								 * for that unit.
								 */
								List<EmpUnitDto> empUnitDtoList = unitDao.searchUnitAttributesByPersonId(
										contractOutputDtoList.get(firstRecord).getIdCntrctManager(),
										ServiceConstants.UNIT_MEMBER_IN_ASSIGNED);

								for (EmpUnitDto empUnitDto : empUnitDtoList) {
									if (empUnitDto.getCdUnitRegion().charAt(0) == '0') {
										/**
										 * checking to see if someone with a
										 * division number is trying to save. If
										 * not, then delete the leading zero in
										 * the region number
										 */

										regionFromResource = empUnitDto.getCdUnitRegion().substring(1);
									} else {
										/**
										 * If someone with a division number is
										 * trying to save then it will be
										 * defaulted to state office.
										 */
										regionFromResource = ServiceConstants.CAPS_UNIT_STATE_OFFICE;
									}
									/** END */
								}

								/********************************************************************
								 ** (END): CCMN39D - Retrieve Primary Worker's
								 * Region
								 ********************************************************************/
							} /* end if */
							else {

								/****************************************************************
								 ** (BEGIN): CSES82D - Region retrieval from
								 * Region/County table
								 ****************************************************************/
								/**
								 * Prepare DAM Input Structures
								 */
								CcntyregDto ccntyregDto = new CcntyregDto();
								ccntyregDto.setCode(countyFromResource);

								/**
								 * Method Name: getRegionFromCounty Method
								 * Description: Retrieves region based on
								 * the county.
								 * 
								 * DAM: CSES82D Service Name: CCMN35S
								 */

								regionFromResource = ccntyregDao.getRegionFromCounty(ccntyregDto);


								/****************************************************************
								 ** (END): CSES82D - Region/County retrieve
								 ****************************************************************/
							} /** END ELSE */ /**
												 * end if FND_SUCCESS == RetVal
												 */

							/**
							 * Only create new contracts if one does not exist
							 * for foster or adoptive contracts.
							 */

							while ((!indAdoptContractExists) || (!indFosterContractExists) || (!indPCAContractExists)) {
								if (!indAdoptContractExists) {
									indAdoptContractExists = Boolean.TRUE;
									sCreateContract = ServiceConstants.SA_ADOPTIVE;
								} /** end if */

								else if (!indFosterContractExists) {
									indFosterContractExists = Boolean.TRUE;
									sCreateContract = ServiceConstants.SA_FOSTER;
								} /** end if */

								else if (!indPCAContractExists) {
									indPCAContractExists = Boolean.TRUE;
									sCreateContract = ServiceConstants.SA_PCA_CONTRACT;
								} /** end if */

								/********************************* ADD ********************************/

								/**
								 * Add if FND_SUCCESS = RetVal check around this
								 * logic.
								 */

								/**********************************************************************
								 ** (BEGIN): CAUD01D CONTRACT AUD
								 ***********************************************************************/

								serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);

								/**
								 ** Populate DAM input structure
								 */

								/**
								 ** Set CAUD01DI ReqFuncCode to Modify
								 */
								serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
								ContractDto contractDto = new ContractDto();
								contractDto.setIdResourse(idResource);
								contractDto.setCdCntrctFuncType(ServiceConstants.STAGE_CD_FAD);
								contractDto.setCdCntrctProgramType(ServiceConstants.FA_PROGRAM);
								contractDto.setCdCntrctProcureType(ServiceConstants.PROVIDER_ENROLL);
								contractDto.setCdCntrctRegion(regionFromResource);
								contractDto.setIndCntrctBudgLimit(ServiceConstants.NO_LIMIT.toString());
								contractDto.setIdRsrcAddress(tempIdRsrcAddress);
								contractDto.setIdContractMngr(
										(contractOutputDtoList.get(firstRecord)).getIdCntrctManager());
								contractDto.setDtLastUpdate(new Date());
								/**
								 * Get the logged on users id to pass into the
								 * contract DAM
								 */
								contractDto.setIdContractWkr(saveApprovalStatusReq.getIdCntrctWkr());

								/**
								 * DAM NAME: CAUD01D. Method Name: contractAUD
								 * Method Description:This DAM is called by
								 * services: CCMN35S. Used to perform CRUD
								 * operation on Contract tables
								 */
								ContractDto resultContractDto = contractDao.contractAUD(contractDto, serviceReqHeaderDto);

								/*
								 * Pass returned contract into the temporary
								 * variable
								 */
								idTempContract = resultContractDto.getIdContract();

								/************************************************************************
								 ** END CAUD01D
								 ************************************************************************/
								/** end if FND_SUCCESS == RetVal */

								/**
								 * Add if FND_SUCCESS = RetVal check around this
								 * logic.
								 */

								/************************************************************************
								 ** BEGIN CAUD20D CONTRACT PERIOD
								 ************************************************************************/

								/**
								 * Populate the user name in the DAM Input
								 * architecture header by copying the service
								 * input header message architecture header to
								 * the DAM Input architecture header.
								 */
								serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);

								/**
								 * Initialize and populate DAM Input Structures
								 */
								ContractContractPeriodInDto contractPeriodInDto = new ContractContractPeriodInDto();
								contractPeriodInDto.setIdContract(idTempContract);
								/**
								 * Get the logged on users id to pass into the
								 * contract DAM
								 */
								contractPeriodInDto.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());
								contractPeriodInDto.setNbrCnperPeriod(ONE);
								/**
								 * Add 100 years to todays date
								 */
								contractPeriodInDto.setDtDtCnperClosure(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusYears(100).toInstant()));
								contractPeriodInDto.setDtDtCnperTerm(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusYears(100).toInstant()));
								contractPeriodInDto.setDtDtCnperStart(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
								contractPeriodInDto.setCdCnperStatus(ServiceConstants.CONTRACT_STATUS_ACTIVE);
								contractPeriodInDto.setIndCnperRenewal(ServiceConstants.NO_RENEWAL);
								contractPeriodInDto.setIndCnperSigned(ServiceConstants.SIGNED_YES);
								contractPeriodInDto.setLastUpdate(new Date());
								serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
								ContractPeriodAUDRes contractPeriodAUDRes = contractDao
										.contractPeriodAUD(contractPeriodInDto, serviceReqHeaderDto);

								if (Boolean.FALSE == contractPeriodAUDRes.getSysCdGenericReturnCode()) {
									throw new ServiceLayerException(
											"Exception Adding in ContractDaoImpl.contractPeriodAUD() with Error Code: "
													+ MSG_CON_CLOSURE_AFTER_EFF);
								} /** end of if */
								else {
									/*************************************************************
									 ** BEGIN CAUD15D CONTRACT VERSION
									 **************************************************************/
									/**
									 * CAUD15D - CONTRACT VERSION AUD
									 */
									/**
									 * Initialize DAM Input Structure
									 */
									ContractVersionAudReq contractVersionAudReq = new ContractVersionAudReq();
									/**
									 * Populate DAM Input Structure
									 */
									contractVersionAudReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									contractVersionAudReq.setIdContract(idTempContract);
									contractVersionAudReq.setNbrCnverPeriod(ONE);
									contractVersionAudReq.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());
									contractVersionAudReq.setNbrCnverVersion(ONE);
									contractVersionAudReq.setDtCnverCreate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
									contractVersionAudReq.setDtCnverEffective(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
									contractVersionAudReq.setLastUpdate(new Date());
									/**
									 * When creating a new contract, dtCnverEnd
									 * should be curr date plus 100 years
									 */
									contractVersionAudReq.setDtCnverEnd(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusYears(100).toInstant()));
									contractVersionAudReq.setIndCnverVerLock(ServiceConstants.SA_FND_YES);
									// Call CAUD15D
									contractVersionAudDao.contractVersionAud(contractVersionAudReq);

									/*************************************************************
									 ** END CAUD15D
									 **************************************************************/
								} /** end else */
								/********************************************************************
								 ** END CAUD20D
								 *********************************************************************/
								/** if FND_SUCCESS = RetVal */

								/**
								 * Added condition if FND_SUCCESS == RetVal to
								 * ensure this logic only occurrs if the
								 * previous retrieves and updates were
								 * successful.
								 */

								if (ServiceConstants.SA_FOSTER.equals(sCreateContract)) {
									/************************************************************************
									 ** BEGIN CAUD17D - CONTRACT SERVICE
									 ************************************************************************/
									/**
									 * Initialize DAM Input Structure
									 */
									ContractServiceInDto contractServiceInDto = new ContractServiceInDto();
									/**
									 * Populate DAM Input Structure
									 */
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									contractServiceInDto.setIdContract(idTempContract);
									contractServiceInDto.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());
									contractServiceInDto.setCnsvcPeriod(ONE_BYTE);
									contractServiceInDto.setCnsvcVersion(ONE);
									contractServiceInDto.setCnsvcUnitType(dayHours);
									contractServiceInDto.setCdCnsvcPaymentType(unitRatePaymentType);
									contractServiceInDto.setIndCnsvcNewRow(ServiceConstants.FND_NO);
									contractServiceInDto.setDtLastUpdate(new Date());
									counterP = 0;

									/**
									 * There are now 4 Service Codes for Foster
									 * Levels, only Loop through and save those
									 * codes
									 */
									while (counterP < NBR_SVC_CODE_SIXTY_ABCD) {
										contractServiceInDto.setCnsvcLineItem(Long.valueOf((counterP + 1)));

										switch (counterP) {
										case 0:
											contractServiceInDto
													.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_BASIC);
											contractServiceInDto
													.setCnsvcUnitRate(ServiceConstants.FOSTER_PAYMENT_LEV_BASIC);
											break;

										case 1:
											contractServiceInDto
													.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_MOD);
											contractServiceInDto
													.setCnsvcUnitRate(ServiceConstants.FOSTER_PAYMENT_LEV_MOD);
											break;

										case 2:
											contractServiceInDto
													.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_SPEC);
											contractServiceInDto
													.setCnsvcUnitRate(ServiceConstants.FOSTER_PAYMENT_LEV_SPEC);
											break;

										case 3:
											contractServiceInDto
													.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_INT);
											contractServiceInDto
													.setCnsvcUnitRate(null);
											break;

										default:
											break;

										}/** end switch */

										/**
										 * DAM Name: CAUD17D
										 ** 
										 ** Description: This method perform CRUD
										 * Operation on CONTRACT_SERVICE table
										 */
										contractServiceDao.contractServiceAUD(contractServiceInDto,
												serviceReqHeaderDto);

										counterP = (counterP + 1);
									} /** end while CounterP < 5 */
									/**************************************************************
									 ** END CAUD17D
									 ***************************************************************/
								}

								/**
								 * Added condition that FND_SUCCESS == RetVal to
								 * prevent processing if we have not
								 * successfully reached this point.
								 */

								if (ServiceConstants.SA_ADOPTIVE.equals(sCreateContract)) {

									/************************************************************************
									 ** BEGIN CAUD17D
									 ************************************************************************/

									/**
									 * Service AUD Processing for CAUD17D
									 */
									/**
									 * Initialize DAM Input Structure
									 */
									/**
									 * Populate DAM Input Structure
									 */
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									ContractServiceInDto contractServiceInDto = populateContractServiceInDto( saveApprovalStatusReq,
											 idTempContract,  ONE_BYTE,  ONE,
											 ONE,  ServiceConstants.CD_SERV_ADP_SUB, ServiceConstants.ADOPTION_SUBSIDY, ServiceConstants.SUBSIDY_PAYMENT );

									/**
									 * Method Name: contractServiceAUD DAM Name:
									 * CAUD17D
									 ** 
									 ** Method Description: This method perform
									 * CRUD Operation on CONTRACT_SERVICE table
									 */
									contractServiceDao.contractServiceAUD(contractServiceInDto, serviceReqHeaderDto);

									/**
									 * Initialize DAM Input Structure
									 */
									/**
									 * Populate DAM Input Structure
									 */
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									ContractServiceInDto contractServiceInDto1 = populateContractServiceInDto( saveApprovalStatusReq,
											idTempContract,  ONE_BYTE,  ONE, TWO,
											ServiceConstants.CD_SERV_ADP_NON_REC_SUB, ServiceConstants.ADOPTION_SUBSIDY, ServiceConstants.SUBSIDY_PAYMENT);
									/**
									 * Method Name: contractServiceAUD DAM Name:
									 * CAUD17D
									 **
									 ** Method Description: This method perform
									 * CRUD Operation on CONTRACT_SERVICE table
									 */
									contractServiceDao.contractServiceAUD(contractServiceInDto1, serviceReqHeaderDto);




									/**************************************************************
									 ** END CAUD17D
									 ***************************************************************/
								}

								/** Added following block for PCA */

								if (ServiceConstants.SA_PCA_CONTRACT.equals(sCreateContract)) {
									/************************************************************************
									 ** BEGIN CAUD17D
									 ************************************************************************/
									/**
									 * Service AUD Processing for CAUD17D
									 */
									/**
									 * Initialize DAM Input Structure
									 */
									/**
									 * Populate DAM Input Structure
									 */
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									ContractServiceInDto contractServiceInDto = populateContractServiceInDto( saveApprovalStatusReq,
											idTempContract,  ONE_BYTE,  ONE, ONE,
											ServiceConstants.CD_SERV_PCA_SUB, ServiceConstants.PCA_SUBSIDY_UNIT_TYPE, ServiceConstants.PCA_SUBSIDY_PAYMENT );

									/**
									 * DAM CAUD17D. The Contract Service AUD
									 * performs a full row insert to the
									 * Contract Service table.
									 */
									contractServiceDao.contractServiceAUD(contractServiceInDto, serviceReqHeaderDto);

									/**
									 * Initialize DAM Input Structure
									 */
									/**
									 * Populate DAM Input Structure
									 */
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
									ContractServiceInDto contractServiceInDto1 = populateContractServiceInDto( saveApprovalStatusReq,
											idTempContract,  ONE_BYTE,  ONE, TWO,
											ServiceConstants.CD_SERV_PCA_NON_REC_SUB, ServiceConstants.PCA_SUBSIDY_UNIT_TYPE, ServiceConstants.PCA_SUBSIDY_PAYMENT );

									/**
									 * DAM CAUD17D. The Contract Service AUD
									 * performs a full row insert to the
									 * Contract Service table.
									 */
									contractServiceDao.contractServiceAUD(contractServiceInDto1, serviceReqHeaderDto);
									/**************************************************************
									 ** END CAUD17D
									 ***************************************************************/
								}

								/**
								 * Added condition that FND_SUCCESS == RetVal.
								 * Previous calls must have been successful to
								 * perform this logic
								 */

								/**************************************************************
								 ** BEGIN CAUD08D - Contract County Insert
								 **************************************************************
								 * /* Initialize DAM Input Structure
								 */
								ContractCountyInDto contractCountyInDto = new ContractCountyInDto();

								/**
								 * Populate DAM Input Structure
								 */
								serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

								/**
								 * Get the logged on users id to pass into the
								 * contract DAM
								 */
								contractCountyInDto.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());

								/**
								 * Initialize current date to dtTempDate(today's
								 * date) and Add 100 years, no months and no
								 * years to dtCurrentDate
								 */
								contractCountyInDto.setDtCncntyEnd(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusYears(100).toInstant()));
								contractCountyInDto.setIdContract(idTempContract);
								contractCountyInDto.setIdResource(idResource);
								contractCountyInDto.setCdCncntyCounty(countyFromResource);
								contractCountyInDto.setDtCncntyEffective(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
								contractCountyInDto.setNbrCncntyPeriod(ServiceConstants.ONE_INT);
								contractCountyInDto.setNbrCncntyVersion(ServiceConstants.ONE_INT);
								contractCountyInDto.setDtLastUpdate(new Date());
								usCountyRow = 0;

								/** 19613 Basic homes now use only one code */
								adoptiveOrFoster = NBR_SVC_CODE_SIXTY_A;

								// need to set the maximum number of services to
								// be written
								// to the contract-county table
								if (ServiceConstants.SA_FOSTER == sCreateContract) {
									if (ObjectUtils.isEmpty(ADOPTIVE_OR_FOSTER_LIST)) {
										adoptiveOrFoster = 0;
									} else {
										/**
										 * loop through the rows to see if any
										 * of them are group homes, if they are
										 * set an indicator
										 */
										while (usCountyRow < NBR_OF_HOME_TYPE) {
											if (CollectionUtils.isNotEmpty(ADOPTIVE_OR_FOSTER_LIST) &&
													usCountyRow < ADOPTIVE_OR_FOSTER_LIST.size() &&
													ServiceConstants.FOST_TYPE_GROUP.toString()
													.equals(ADOPTIVE_OR_FOSTER_LIST.get(usCountyRow))) {
												indGroupHomeIndicator = Boolean.TRUE;
												break;
											}
											usCountyRow++;
										}

										usCountyRow = 0;
										while (usCountyRow < NBR_OF_HOME_TYPE) {
											if (CollectionUtils.isNotEmpty(ADOPTIVE_OR_FOSTER_LIST) &&
													usCountyRow < ADOPTIVE_OR_FOSTER_LIST.size() &&
													(ServiceConstants.FOST_TYPE_HABIL.toString()
													.equals(ADOPTIVE_OR_FOSTER_LIST.get(usCountyRow))
													|| ServiceConstants.FOST_TYPE_THER.toString()
															.equals(ADOPTIVE_OR_FOSTER_LIST.get(usCountyRow))
													|| ServiceConstants.FOST_TYPE_PRIM_MED.toString()
															.equals(ADOPTIVE_OR_FOSTER_LIST.get(usCountyRow)))) {

												/**
												 * if group home is false, save
												 * 63A-D else save 63A-C
												 */
												if (indGroupHomeIndicator == Boolean.FALSE) {
													/**
													 * 19613, 22390, 22485
													 * Habil, Ther, and Prim Med
													 * homes use two codes 63A,
													 * B, C & D
													 */
													adoptiveOrFoster = NBR_SVC_CODE_SIXTY_ABCD;
												} else {
													adoptiveOrFoster = NBR_SVC_CODE_SIXTY_ABC;

												}
												break;
											}
											usCountyRow++;
										}
									}
								} else if (ServiceConstants.SA_ADOPTIVE == sCreateContract) {
									adoptiveOrFoster = NBR_SVC_CODE_SIXTY_AB;
								} else if (ServiceConstants.SA_PCA_CONTRACT == sCreateContract) {
									adoptiveOrFoster = NBR_SVC_CODE_SIXTY_AB;
								} else {
									adoptiveOrFoster = 0;
								}

								usCountyRow = 0;

								/**
								 * County AUD processing CAUD08D
								 */

								while (usCountyRow < adoptiveOrFoster) {
									/* Save new codes */
									if (ServiceConstants.SA_FOSTER == sCreateContract && foster) {
										switch (usCountyRow) {
										case 0:
											contractCountyInDto
													.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_BASIC);
											break;

										case 1:
											contractCountyInDto
													.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_MOD);
											break;

										case 2:
											contractCountyInDto
													.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_SPEC);
											break;

										case 3:
											contractCountyInDto
													.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_INT);
											break;

										default:
											break;
										}/** end switch */

									} /** end if */

									if ((ServiceConstants.SA_ADOPTIVE == sCreateContract)
											&& (ServiceConstants.ZERO_SHORT == usCountyRow)) {
										contractCountyInDto.setCdCncntyService(ServiceConstants.CD_SERV_ADP_SUB);
									}

									if ((ServiceConstants.SA_ADOPTIVE == sCreateContract)
											&& (ServiceConstants.ONE_INT == usCountyRow)) {
										contractCountyInDto
												.setCdCncntyService(ServiceConstants.CD_SERV_ADP_NON_REC_SUB);
									}
									/** Add following section for PCA */
									if ((ServiceConstants.SA_PCA_CONTRACT == sCreateContract)
											&& (ServiceConstants.ZERO_SHORT == usCountyRow)) {
										contractCountyInDto.setCdCncntyService(ServiceConstants.CD_SERV_PCA_SUB);
									}

									if ((ServiceConstants.SA_PCA_CONTRACT == sCreateContract)
											&& (ServiceConstants.ONE_INT == usCountyRow)) {
										contractCountyInDto
												.setCdCncntyService(ServiceConstants.CD_SERV_PCA_NON_REC_SUB);
									}

									contractCountyInDto.setNbrCncntyLineItem(usCountyRow + ONE_INT);

									/**
									 * DAM Name: CAUD08D
									 *
									 * Method Description: This method perform
									 * CRUD Operation on CONTRACT_COUNTY table
									 */
									contractCountyDao.contractCountyAUD(contractCountyInDto, serviceReqHeaderDto);
									usCountyRow++;
								} /*
									 * end while for county AUD processing CAUD08D
									 */

								/*************************************************************
								 ** END CAUD08D
								 **************************************************************/
								/** End */
								/** end - if FND_SUCCESS == RetVal */
								/*****************************
								 * END ADD CODE
								 *****************************/
							}
							/**
							 * End while !bIndAdoptContractExists or
							 * !bIndFosterContractExists or
							 * !bIndPCAContractExists
							 */
							/*****************************
							 * END ADD CODE
							 *****************************/

							/*********************************************************************
							 ** (END): Contract creation process if the contract
							 * does not already exist.
							 *********************************************************************/
							/*********************************************************************
							 ** (BEGIN): Contract modification process if the
							 * contract already exists.
							 *********************************************************************/

							int m = 0;

							/**
							 * Only modify contracts when flags are TRUE
							 */
							while ((indUpdateAdoptContract) || (indUpdateFosterContract) || (indUpdatePCAContract)) {

								if (CollectionUtils.isNotEmpty(contractOutputDtoList) &&
										m < contractOutputDtoList.size() &&
										ServiceConstants.CHAR_Y == contractOutputDtoList.get(m)
										.getSysIndContractCurrent()) {
									if ((indUpdateAdoptContract)
											&& ((ServiceConstants.CD_SERV_ADP_SUB.equals(contractOutputDtoList.get(m)
													.getContractServiceOutDtoList().get(0).getCdCnsvcService()))
													|| (ServiceConstants.CD_SERV_ADP_NON_REC_SUB.equals(
															contractOutputDtoList.get(m).getContractServiceOutDtoList()
																	.get(0).getCdCnsvcService())))) {
										indUpdateAdoptContract = Boolean.FALSE;
										sUpdateContract = ServiceConstants.SA_ADOPTIVE;
									} /** end if */
									else if ((indUpdateFosterContract)
											&& (ServiceConstants.CD_SERV_FOST_L1.equals(contractOutputDtoList.get(m)
													.getContractServiceOutDtoList().get(0).getCdCnsvcService()))
											|| (ServiceConstants.CD_SERV_FOST_L2.equals(contractOutputDtoList.get(m)
													.getContractServiceOutDtoList().get(0).getCdCnsvcService()))
											|| (ServiceConstants.CD_SERV_FOST_LEV_BASIC.equals(contractOutputDtoList
													.get(m).getContractServiceOutDtoList().get(0).getCdCnsvcService()))
											|| (ServiceConstants.CD_SERV_FOST_LEV_MOD.equals(contractOutputDtoList
													.get(m).getContractServiceOutDtoList().get(0).getCdCnsvcService()))
											|| (ServiceConstants.CD_SERV_FOST_LEV_SPEC.equals(contractOutputDtoList
													.get(m).getContractServiceOutDtoList().get(0).getCdCnsvcService()))
											|| (ServiceConstants.CD_SERV_FOST_LEV_INT.equals(contractOutputDtoList
													.get(m).getContractServiceOutDtoList().get(0).getCdCnsvcService())))

									{
										indUpdateFosterContract = Boolean.FALSE;
										sUpdateContract = ServiceConstants.SA_FOSTER;
									} /** end if */
									/** Added following code for PCA Contract */
									else if (indUpdatePCAContract &&
											CollectionUtils.isNotEmpty(contractOutputDtoList) &&
											m < contractOutputDtoList.size() &&
											((ServiceConstants.CD_SERV_PCA_SUB.equals(contractOutputDtoList.get(m)
													.getContractServiceOutDtoList().get(0).getCdCnsvcService()))
													|| (ServiceConstants.CD_SERV_PCA_NON_REC_SUB.equals(
															contractOutputDtoList.get(m).getContractServiceOutDtoList()
																	.get(0).getCdCnsvcService())))) {
										indUpdatePCAContract = Boolean.FALSE;
										sUpdateContract = ServiceConstants.SA_PCA_CONTRACT;
									} /** end if */

									/**
									 * Update the contract table when approving
									 * the home. This is necessary if a prs home
									 * has been reopened and the region has
									 * changed before the home has been
									 * re-approved.
									 */

									/****************************************************************
									 ** (BEGIN): CAUD01D CONTRACT AUD
									 ****************************************************************/
									/**
									 * Populate the user name in the DAM Input
									 * architecture header by copying the
									 * service input header message architecture
									 * header to the DAM Input architecture
									 * header.
									 */
									serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);
									/**
									 * Initialize and Populate DAM input
									 * structure
									 */
									ContractDto contractDto = new ContractDto();
									contractDto.setIdContract(contractOutputDtoList.get(m).getIdContract());
									contractDto.setIdResourse(idResource);
									contractDto.setCdCntrctFuncType(ServiceConstants.STAGE_CD_FAD);
									contractDto.setCdCntrctProgramType(ServiceConstants.FA_PROGRAM);
									contractDto.setCdCntrctProcureType(ServiceConstants.PROVIDER_ENROLL);
									contractDto.setCdCntrctRegion(regionFromResource);
									contractDto.setIndCntrctBudgLimit(ServiceConstants.NO_LIMIT.toString());
									contractDto.setIdRsrcAddress(tempIdRsrcAddress);
									contractDto.setIdContractMngr(
											(contractOutputDtoList.get(firstRecord)).getIdCntrctManager());
									contractDto.setIdContractWkr(saveApprovalStatusReq.getIdCntrctWkr());
									contractDto.setDtLastUpdate(contractOutputDtoList.get(m).getTsLastUpdate());
									/**
									 * Set CAUD01DI ReqFuncCode to Modify
									 */
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

									/**
									 * DAM NAME: CAUD01D. Method Name:
									 * contractAUD Method Description:This DAM
									 * is called by services: CCMN35S. Used to
									 * perform CRUD operation on Contract tables
									 */
									contractDao.contractAUD(contractDto, serviceReqHeaderDto);

									/************************************************************************
									 ** END CAUD01D CONTRACT HEADER UPDATE
									 ************************************************************************/

									/**
									 * Added code to check if FND_SUCCESS ==
									 * RetVal
									 */
									/************************************************************************
									 ** BEGIN CAUD20D CONTRACT PERIOD
									 ************************************************************************/
									/**
									 * Populate the user name in the DAM Input
									 * architecture header by copying the
									 * service input header message architecture
									 * header to the DAM Input architecture
									 * header.
									 */
									serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);

									/**
									 * Initialize and populate DAM Input
									 * Structures
									 */
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
									ContractContractPeriodInDto contractPeriodInDto = new ContractContractPeriodInDto();

									contractPeriodInDto.setIdContract(contractOutputDtoList.get(m).getIdContract());
									/**
									 * Get the logged on users id to pass into
									 * the contract DAM
									 */
									contractPeriodInDto.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());
									contractPeriodInDto.setNbrCnperPeriod(
											Long.valueOf(contractOutputDtoList.get(m).getNbrCnperPeriod()));
									contractPeriodInDto
											.setDtDtCnperClosure(contractOutputDtoList.get(m).getDtCnperClosure());
									contractPeriodInDto.setDtDtCnperTerm(contractOutputDtoList.get(m).getDtCnperTerm());
									contractPeriodInDto
											.setDtDtCnperStart(contractOutputDtoList.get(m).getDtCnperStart());
									contractPeriodInDto.setCdCnperStatus(ServiceConstants.CONTRACT_STATUS_ACTIVE);
									contractPeriodInDto.setIndCnperRenewal(ServiceConstants.NO_RENEWAL);
									contractPeriodInDto.setIndCnperSigned(ServiceConstants.SIGNED_YES);
									/**
									 * DAM Name: CAUD20D. The Contract Period
									 * ELB DAM receives IdContract and performs
									 * an AUD on the indicated row. Delete: a
									 * stored procedure is called to perform a
									 * cascade delete for Contract Version,
									 * Contract Service and Contract County.
									 ** Add: Performs a full row insert into
									 * Contract Period Table Modify: Performs a
									 * full row update into Contract Period
									 * Table.
									 */
									contractDao.contractPeriodAUD(contractPeriodInDto, serviceReqHeaderDto);
									/***********************************************************
									 ** END CAUD20D
									 ************************************************************/
									/** end if - if FND_SUCCESS == RetVal */
									/**
									 * Called CheckCatHomeType function and
									 * Added CategoryHomeType == 0 condition to
									 * the if statement. We only want to create
									 * new version if Category and/or home type
									 * is changed
									 */
									categoryHomeType = checkCatHomeType(idResource);

									if (CollectionUtils.isNotEmpty(contractOutputDtoList) &&
											m < contractOutputDtoList.size() &&
											(ServiceConstants.CHAR_Y == contractOutputDtoList.get(m)
											.getSysIndContractCurrent())
											&& ServiceConstants.SA_FOSTER.equals(sUpdateContract) && categoryHomeType) {
										contractVerSerCnty(saveApprovalStatusReq,
												contractOutputDtoList.get(m).getIdContract(),
												contractOutputDtoList.get(m).getNbrCnperPeriod(), idResource,
												serviceReqHeaderDto);
									}
								} /** end if contract current */

								m++;
							}
							/**
							 * comment below actually is:end while Only modify
							 * contracts when flags are TRUE
							 */
							/**
							 * end while only create new contracts if one
							 * doesn't exist
							 */

							/** END OF CONTRACT UPDATE SEGMENT */

							/*********************************************************************
							 ** (END): Contract modification process if the
							 * contract already exists.
							 *********************************************************************/

							/**************************************************************************
							 ** END CONTRACT CREATION/MODIFICATION
							 **************************************************************************/
						}
					} /** close if maintain lic */

					/**
					 * Added else if to call CSUB40, to create a To do, when the
					 * task is re-evaluate and Approved
					 */
					else if (ServiceConstants.RE_EVALUATE.equals(approvalEventOutputDto.getCdTask())
							&& ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())) {
						/**
						 * Initialize and Populate Common function Input and
						 * Structure
						 */
						TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
						todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_FA_HOME);
						todoCreateInDto.setDtSysDtTodoCfDueFrom(ServiceConstants.NULL_DATE_TYPE);
						todoCreateInDto.setSysIdTodoCfPersCrea(saveApprovalStatusReq.getIdPerson());
						todoCreateInDto.setSysIdTodoCfEvent(ServiceConstants.ZERO);
						todoCreateInDto.setSysIdTodoCfStage(saveApprovalStatusReq.getIdStage());
						todoCreateInDto.setMergeSplitToDoDto(new MergeSplitToDoDto());
						/**
						 * Service Name: CSUB40
						 * 
						 * to create to do
						 */
						commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);

					} /** end else if - end */

					/**
					 * Combined the two if blocks below
					 */
					if ((ServiceConstants.TASK_CLOSE_HOME.equals(approvalEventOutputDto.getCdTask()))
							&& (ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction()))) {
						/**
						 * Populate the user name in the DAM Input architecture
						 * header by copying the service input header message
						 * architecture header to the DAM Input architecture
						 * header.
						 */
						serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);

						/**
						 * Initialize and Populate DAM Input Structure
						 */
						PostEventReq postEventReq = new PostEventReq();
						postEventReq.setSzCdTask(ServiceConstants.BLANK);
						postEventReq.setTsLastUpdate(ServiceConstants.NULL_DATE_TYPE);
						postEventReq.setSzCdEventStatus(ServiceConstants.EVENT_STATUS_APRV);
						postEventReq.setSzCdEventType(ServiceConstants.EVENT_TYPE_HOME);
						postEventReq.setDtDtEventOccurred(ServiceConstants.NULL_DATE_TYPE);
						postEventReq.setUlIdEvent(ServiceConstants.ZERO);
						postEventReq.setUlIdStage(saveApprovalStatusReq.getIdStage());
						postEventReq.setUlIdPerson(saveApprovalStatusReq.getIdPerson());
						postEventReq.setSzTxtEventDescr(EVENT_DESC_HOME_CLOSED);
						postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

						/**
						 * DAM Name: CCMN01D
						 * 
						 * Description: The purpose of this method is to
						 * retrieve data for the Office Assignments ListBox on
						 * the Office Detail window. It retrieves the name, id
						 * and bjn for all employees within an office.
						 */
						PostEventRes postEventRes = eventService.postEvent(postEventReq);
						idEvent = postEventRes.getUlIdEvent();

						/** CREATE AN APPROVAL LINK */

						/**
						 * Only create an approval link if the event was created
						 * successfully above
						 */

						/**
						 * Initialize and Populate DAM Input Structure
						 */

						ApprovalEventLinkDto approvalEventLinkDto = new ApprovalEventLinkDto();
						approvalEventLinkDto.setIdApproval(saveApprovalStatusReq.getIdApproval());
						approvalEventLinkDto.setIdEvent(postEventRes.getUlIdEvent());
						approvalEventLinkDto.setDtLastUpdate(postEventRes.getTsLastUpdate());

						/**
						 * Populate the request function code in the input
						 * architecture header
						 */
						serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);

						/**
						 * DAM Name: CCMN91D Method Description: This Method is
						 * will Add several records and delete the records to
						 * the APPROVAL_EVENT_LINK table based on the input &
						 * request indicator
						 */
						approvalEventLinkDao.getApprovalEventLinkAUD(serviceReqHeaderDto, approvalEventLinkDto);

						/** UPDATE CAPS_RESOURCE */

						/**
						 * Only call CAUDB3D if the previous DAM call is
						 * successful
						 */
						/**
						 * Initialize and Populate DAM Input Structure
						 */
						CapsResourceDto capsResourceDto = new CapsResourceDto();
						capsResourceDto.setCdRsrcFaHomeStatus(ServiceConstants.HOME_STATUS_CLOSED);
						capsResourceDto.setCdRsrcStatus(ServiceConstants.RSRC_STAT_INACTIVE);
						capsResourceDto.setIdEvent(idEvent);
						capsResourceDto.setIdStage(saveApprovalStatusReq.getIdStage());
						/**
						 * This write history indicator must be set to yes in
						 * order to write the record to the resource history
						 * table
						 */
						capsResourceDto.setIndRsrcWriteHist(ServiceConstants.YES);
						capsResourceDto.setDtApproversDetermination(
								saveApprovalStatusReq.getApproveApprovalDto().getDtApproversDetermination());
						capsResourceDto.setIdApproval(saveApprovalStatusReq.getIdApproval());
						/**
						 ** Populate the request function code in the input
						 * architecture header
						 */
						serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

						/**
						 * DAM Name: CAUDB3D Method Name: updateCapsResourceAUD
						 * Method Description: Description: This DAM is used by
						 * the Approval Save screen to update specific columns
						 * on the CAPS RESOURCE table. Service Name : CCMN35S;
						 */
						capsResourceDao.updateCapsResourceAUD(capsResourceDto, serviceReqHeaderDto);

						RtrvRsrcByStageInDto rtrvRsrcByStageInDto = new RtrvRsrcByStageInDto();
						rtrvRsrcByStageInDto.setIdRsrcFaHomeStage(saveApprovalStatusReq.getIdStage());
						RtrvRsrcByStageOutDto rtrvRsrcByStageOutDto = new RtrvRsrcByStageOutDto();

						/**
						 * DAM NAME: CSES41D DAM Description: This Dam will
						 * retrieve a row from CAPS_RESORCE givn ID_STAGE
						 */
						List<RtrvRsrcByStageOutDto> rtrvRsrcByStageOutDtoList = retrieveCapsResourceDao
								.retrieveCapsResource(rtrvRsrcByStageInDto, rtrvRsrcByStageOutDto);
						/**
						 * Set local variable to DAM Output IdResource
						 */
						for (RtrvRsrcByStageOutDto rtrvRsrcByStageOutputDto : rtrvRsrcByStageOutDtoList) {
							idResource = rtrvRsrcByStageOutputDto.getUlIdResource();
						}

						/**************************************************************************
						 **
						 ** BEGIN CONTRACT CREATION/MODIFICATION FOR CLOSE HOME
						 **
						 **************************************************************************/

						/**
						 * Begin contract creation/modification process if the
						 * save service is successful up to this point
						 */
						/************************************************************************
						 * (BEGIN): Contracts existance determination. Is there
						 * an open foster and adoptive or PCA contract for the
						 * home?
						 ************************************************************************/

						/**************************************************************************
						 ** (BEGIN): CLSS67D - List retrieval of Contract rows
						 * for and id resource.
						 **************************************************************************/

						/**
						 * DAM Name: CLSS67D Retrieves CONTRACT table based on
						 * Id_Resource.
						 */
						List<ContractDto> contractDtlList = placementDao.getContractDtl(idResource);

						/**
						 * Loop through all contract rows returned from the
						 * previous DAM
						 */

						for (ContractDto contractDto : contractDtlList) {
							ContractOutputDto contractOutputDto = new ContractOutputDto();
							contractOutputDto.setIdContract(contractDto.getIdContract());
							contractOutputDto.setTsLastUpdate(contractDto.getDtLastUpdate());

							/*******************************************************************
							 ** (BEGIN) CSES80D: Retrieve Contract Period table
							 * information
							 *******************************************************************/

							/**
							 * DAM Name: CSES80D Method Name:
							 * getContractPeriodByIdContract MEthod Description:
							 * Retrieves CONTRACT based on id_contract from
							 * CONTRACT_PERIOD table
							 *
							 */
							List<ContractPeriodDto> contractPeriodList = placementDao
									.getContractPeriodByIdContract(contractOutputDto.getIdContract());

							/**
							 * Set fields in CFAD08SO to fields in CSES80DO
							 */
							for (ContractPeriodDto contractPeriod : contractPeriodList) {
								contractOutputDto.setIdCntrctWkr(contractPeriod.getIdPerson());
								contractOutputDto.setDtCnperTerm(contractPeriod.getDtCnperTerm());
								contractOutputDto.setDtCnperClosure(contractPeriod.getDtCnperClosure());
								contractOutputDto.setDtCnperStart(contractPeriod.getDtCnperStart());
								contractOutputDto.setNbrCnperPeriod((int) contractPeriod.getIdContractPeriod());
								contractOutputDto.setSysTsLastUpdate2(contractPeriod.getDtLastUpdate());

								/**
								 * Potential for difference in dates being too
								 * large prevented using compare date and time.
								 * Instead, "extend" compare date processing is
								 * being used
								 */

								if (contractOutputDto.getDtCnperTerm().after(new Date())
										|| contractOutputDto.getDtCnperTerm() == new Date()) {
									testBool = Boolean.TRUE;
								}

								/**
								 * END "extendo" compare date processing
								 */
								if (testBool) {
									if (!ServiceConstants.CONTRACT_STATUS_CLOSED
											.equals(contractPeriod.getCdCnperStatus())) {
										contractOutputDto.setSysIndContractCurrent(ServiceConstants.CHAR_Y);
									} else {
										contractOutputDto.setSysIndContractCurrent(ServiceConstants.N_CHAR);
									}
								} else {
									contractOutputDto.setSysIndContractCurrent(ServiceConstants.N_CHAR);
								}

								/**
								 * Changed error processing of cses80d to allow
								 * sql-not-found because that is an acceptible
								 * condition. It should not "blow-up" at this
								 * point.
								 */

								/*******************************************************************
								 ** (END) CSES80D: Retrieve Contract Period table
								 * information
								 *******************************************************************/
							}
							contractOutDtoForCloseHomeList.add(contractOutputDto);
						} /**
							 * end loop to run through all contracts returned from
							 * CLSS67D
							 */
						/**************************************************************************
						 ** (END): CLSS67D - List retrieval of Contract rows for
						 * and id resource.
						 **************************************************************************/
						/**
						 * Loop through all contract rows returned from the
						 * previous DAMs
						 */

						for (ContractOutputDto contractOutputDto : contractOutDtoForCloseHomeList) {
							if (!ObjectUtils.isEmpty(contractOutputDto.getSysIndContractCurrent()) &&
									ServiceConstants.CHAR_Y == contractOutputDto.getSysIndContractCurrent()) {
								/****************************************************************
								 ** (BEGIN): CSES81D - Contract Version retrieve
								 * for an idContract , contract period number,
								 * and version end date that is greater than the
								 * current date.
								 ****************************************************************/

								/****************************************************************
								 ** (BEGIN): CSES81D - Contract Version retrieve
								 * for an idContract , contract period number,
								 * and version end date that is greater than the
								 * current date.
								 ****************************************************************/

								/** Set Input structure for CSES81D */
								ContractVersionInDto contractVersionInDto = new ContractVersionInDto();
								contractVersionInDto.setIdContract(contractOutputDto.getIdContract());
								contractVersionInDto
										.setNbrCnverPeriod(Long.valueOf(contractOutputDto.getNbrCnperPeriod()));
								/**
								 * DAM Name: CSES81D * Method Description:
								 * retrieves a full row from CONTRACT VERSION
								 * given the ID_CONTRACT and NBR_CNVER_PERIOD.
								 *
								 */
								List<ContractVersionOutDto> contractVersionOutDtoList = contractVersionDao
										.getContractVersionByNbrCnverPeriod(contractVersionInDto);

								for (ContractVersionOutDto contractVersionOutDto : contractVersionOutDtoList) {
									contractOutputDto.setNbrCnverVersion(contractVersionOutDto.getNbrCnverVersion());
									contractOutputDto.setSysTsLastUpdate3(contractVersionOutDto.getTsLastUpdate());
									contractOutputDto.setIdCnver(contractVersionOutDto.getIdCnver());
									contractOutputDto.setDtCnverCreate(contractVersionOutDto.getDtCnverCreate());
									contractOutputDto.setDtCnverEnd(contractVersionOutDto.getDtCnverEnd());
									contractOutputDto.setDtCnverEffective(contractVersionOutDto.getDtCnverEffective());
								}
								/*********************************************************
								 ** (BEGIN): CLSS13D - Retrieve contract service
								 * codes for the contract, period, and version
								 * passed to the DAM.
								 *********************************************************/

								/* Set input structure for CLSS13D */
								ContractServiceInDto contactServiceInDto = new ContractServiceInDto();
								contactServiceInDto.setIdContract(contractOutputDto.getIdContract());

								/**
								 * DAM Name - CLSS13D Method Description:
								 * retrieves a full row from CONTRACT_SERVICE
								 * given the ID_CONTRACT.
								 */
								List<ContractServiceOutDto> contractServiceOutDtoList = contractServiceDao
										.getContractService(contactServiceInDto);

								rowCounter = 0;
								// rowCounterOne =
								// contractServiceOutDtoList.size();

								List<ContractServiceOutDto> tempContractServiceOutDtoList = new ArrayList<>();
								for (ContractServiceOutDto contractServiceOutputDto : contractServiceOutDtoList) {

									ContractServiceOutDto tempContractServiceOutDto = new ContractServiceOutDto();
									tempContractServiceOutDto
											.setSysTsLastUpdate5(contractServiceOutputDto.getDtLastUpdate());
									tempContractServiceOutDto
											.setNbrCnsvcVersion(contractServiceOutputDto.getNbrCnsvcVersion());
									tempContractServiceOutDto.setIdCnsvc(contractServiceOutputDto.getIdCnsvc());
									tempContractServiceOutDto
											.setNbrCnsvcLineItem(contractServiceOutputDto.getNbrCnsvcLineItem());
									tempContractServiceOutDto
											.setNbrCnsvcUnitRate(contractServiceOutputDto.getNbrCnsvcUnitRate());
									tempContractServiceOutDto.setAmtCnsvcUnitRateUsed(
											contractServiceOutputDto.getAmtCnsvcUnitRateUsed());
									tempContractServiceOutDto
											.setCdCnsvcUnitType(contractServiceOutputDto.getCdCnsvcUnitType());
									tempContractServiceOutDto
											.setCdCnsvcService(contractServiceOutputDto.getCdCnsvcService());

									/**
									 * Change comparison of service returned
									 * from 60A-E to 63A-D Compare against the
									 * new F/A home codes
									 */
									if (ServiceConstants.CD_SERV_FOST_L1
											.equals(tempContractServiceOutDto.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_L2
													.equals(tempContractServiceOutDto.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_BASIC
													.equals(tempContractServiceOutDto.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_MOD
													.equals(tempContractServiceOutDto.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_SPEC
													.equals(tempContractServiceOutDto.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_INT
													.equals(tempContractServiceOutDto.getCdCnsvcService())) {
										/**
										 * if service code is a foster code
										 */
										indFosterContractExists = Boolean.TRUE;
										indUpdateFosterContract = Boolean.TRUE;
									} /**
										 * end if service code is a foster code
										 */

									if (ServiceConstants.CD_SERV_ADP_SUB
											.equals(tempContractServiceOutDto.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_ADP_NON_REC_SUB
													.equals(tempContractServiceOutDto.getCdCnsvcService())) {
										/**
										 * if service code is a adoptive code
										 */
										indAdoptContractExists = Boolean.TRUE;
										indUpdateAdoptContract = Boolean.TRUE;
									} /**
										 * end if service code is a adoptive code
										 */

									/**
									 * Added following block for PCA contract
									 */
									if (ServiceConstants.CD_SERV_PCA_SUB
											.equals(tempContractServiceOutDto.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_PCA_NON_REC_SUB
													.equals(tempContractServiceOutDto.getCdCnsvcService())) {
										/**
										 * if service code is a adoptive code
										 */
										indPCAContractExists = Boolean.TRUE;
										indUpdatePCAContract = Boolean.TRUE;
									} /** end if service code is a PCA code */

									tempContractServiceOutDtoList.add(tempContractServiceOutDto);

									/*********************************************************
									 ** (BEGIN): CLSS68D - Retrieve contract
									 * service codes for the contract, period,
									 * and version passed to the DAM.
									 *********************************************************/
									if (rowCounter >= ServiceConstants.Zero) {
										List<ContractCountyOutDto> tempContractCountyOutDtoList = new ArrayList<>();

										// prepare input structure for CLSS68D
										ContractCountyInDto contractCountyInDto = new ContractCountyInDto();
										contractCountyInDto.setIdContract(contractOutputDto.getIdContract());
										contractCountyInDto.setNbrCncntyPeriod(
												Integer.valueOf(contractOutputDto.getNbrCnperPeriod()));
										contractCountyInDto.setNbrCncntyVersion(
												Integer.valueOf(contractOutputDto.getNbrCnverVersion()));

										/**
										 * DAM NAME: CLSS68D Method Description:
										 * retrieves a full row from CONTRACT
										 * COUNTY given the ID_CONTRACT,
										 * NBR_CNVER_PERIOD and
										 * NBR_CNCNTY_VERSION.
										 */
										List<ContractCountyOutDto> contractCountyOutDtoList = contractCountyDao
												.getContractCounty(contractCountyInDto);

										rowCounter = contractCountyOutDtoList.size();

										/**
										 * Set fields in CFAD08SI to fields in
										 * CLSS68DO for each contract service
										 * row returned.
										 */

										/**
										 * Set variable to hold number of
										 * contract services for a contract
										 */
										contractOutputDto
												.setUlSysNbrGenericCntr(Long.valueOf(contractCountyOutDtoList.size()));
										for (ContractCountyOutDto contractCountyOutDto : contractCountyOutDtoList) {
											ContractCountyOutDto tempContractCountyOutDto = new ContractCountyOutDto();
											tempContractCountyOutDto
													.setCdCncnctyService(contractCountyOutDto.getCdCncnctyService());
											tempContractCountyOutDto
													.setSysTsLastUpdate4(contractCountyOutDto.getDtLastUpdate());
											tempContractCountyOutDto.setIdCncnty(contractCountyOutDto.getIdCncnty());
											tempContractCountyOutDto
													.setCdCncnctyCounty(contractCountyOutDto.getCdCncnctyCounty());
											tempContractCountyOutDto.setDtCncnctyEffective(
													contractCountyOutDto.getDtCncnctyEffective());
											/**
											 * The DtCncntyEnd was not being
											 * copied from the DAM output to the
											 * CFAD01 holding variable
											 */
											tempContractCountyOutDto
													.setDtCncnctyEnd(contractCountyOutDto.getDtCncnctyEnd());
											tempContractCountyOutDto
													.setNbrCncntyPeriod(contractCountyOutDto.getNbrCncntyPeriod());
											tempContractCountyOutDto
													.setNbrCncntyVersion(contractCountyOutDto.getNbrCncntyVersion());
											tempContractCountyOutDto
													.setNbrCncntyLineItem(contractCountyOutDto.getNbrCncntyLineItem());

											tempContractCountyOutDtoList.add(tempContractCountyOutDto);
										}
										contractOutputDto.setContractCountyOutDtoList(tempContractCountyOutDtoList);

									} /** end if tempSvcRowQty >= 0 */
									/*********************************************************
									 ** (END): CLSS68D - Retrieve contract
									 * service codes for the contract, period,
									 * and version passed to the DAM.
									 *********************************************************/

									contractOutputDto.setContractServiceOutDtoList(tempContractServiceOutDtoList);

									/*********************************************************
									 ** (END): CLSS13D - Retrieve contract
									 * service codes for the contract, period,
									 * and version passed to the DAM.
									 *********************************************************/

									/****************************************************************
									 ** (END): CSES81D - Contract Version
									 * retrieve for an idContract , contract
									 * period number, and version end date that
									 * is greater than the current date.
									 ****************************************************************/
								}
							} /** End if indConractCurrent is TRUE */
						} /** End for loop */

						/************************************************************************
						 ** (END): Contracts existence determination. Is there an
						 * open foster and adoptive contract for the home?
						 ************************************************************************/

						/************************************************************************
						 ** (BEGIN): Rule creation of PCA contract when FAD home
						 * types does not have Relative or Fictive Kin
						 ************************************************************************/

						for (RtrvRsrcByStageOutDto rtrvRsrcByStageOutputDto : rtrvRsrcByStageOutDtoList) {
							ADOPTIVE_OR_FOSTER_LIST = new ArrayList<>();
							ADOPTIVE_OR_FOSTER_LIST.add(rtrvRsrcByStageOutputDto.getcCdRsrcFaHomeType1());
							ADOPTIVE_OR_FOSTER_LIST.add(rtrvRsrcByStageOutputDto.getcCdRsrcFaHomeType2());
							ADOPTIVE_OR_FOSTER_LIST.add(rtrvRsrcByStageOutputDto.getcCdRsrcFaHomeType3());
							ADOPTIVE_OR_FOSTER_LIST.add(rtrvRsrcByStageOutputDto.getcCdRsrcFaHomeType4());
							ADOPTIVE_OR_FOSTER_LIST.add(rtrvRsrcByStageOutputDto.getcCdRsrcFaHomeType5());
							ADOPTIVE_OR_FOSTER_LIST.add(rtrvRsrcByStageOutputDto.getcCdRsrcFaHomeType6());
							ADOPTIVE_OR_FOSTER_LIST.add(rtrvRsrcByStageOutputDto.getcCdRsrcFaHomeType7());
						}
						int i = 0;
						while (i < NBR_OF_HOME_TYPE) {
							if (CollectionUtils.isNotEmpty(ADOPTIVE_OR_FOSTER_LIST) &&
									i < ADOPTIVE_OR_FOSTER_LIST.size() &&
									(ServiceConstants.FOST_TYPE_RELATIVE.toString().equals(ADOPTIVE_OR_FOSTER_LIST.get(i))
									|| ServiceConstants.FOST_TYPE_FICTIVE_KIN.toString()
											.equals(ADOPTIVE_OR_FOSTER_LIST.get(i)))) {
								relFictiveKinHome = Boolean.TRUE;
								break;
							}
							i++;
						}

						// if it is not a relative or fictive kin type of home
						// then we should not
						// create or modify PCA contract
						if (relFictiveKinHome == Boolean.FALSE) {
							indPCAContractExists = Boolean.TRUE; // this will
																	// not
																	// create
																	// PCA
																	// contract
							indUpdatePCAContract = Boolean.FALSE; // this will
																	// not
																	// update
																	// PCA
																	// contract
						}

						/************************************************************************
						 ** (End): Rule creation of PCA contract when FAD home
						 * types does not have Relative or Fictive Kin
						 ************************************************************************/

						/*****************************************************************
						 ** (BEGIN): Contract modification process if the
						 * contract already exists and a change has been made to
						 * the home's status
						 ******************************************************************/
						/************************
						 * UPDATE CODE
						 ****************************/
						/**
						 * Initialize contract counter
						 */
						int m = 0;
						/**
						 * Only modify contracts where flag is TRUE
						 */
						while ((indUpdateAdoptContract) || (indUpdateFosterContract) || indUpdatePCAContract) {
							if (CollectionUtils.isNotEmpty(contractOutDtoForCloseHomeList) &&
									m < contractOutDtoForCloseHomeList.size() &&
									Character.valueOf(ServiceConstants.CHAR_Y).equals(contractOutDtoForCloseHomeList.get(m)
									.getSysIndContractCurrent())) {

								if (indUpdateAdoptContract) {
									indUpdateAdoptContract = Boolean.FALSE;
									sUpdateContract = ServiceConstants.SA_ADOPTIVE;
								} /** end if */

								else if (indUpdateFosterContract) {
									indUpdateFosterContract = Boolean.FALSE;
									sUpdateContract = ServiceConstants.SA_FOSTER;
								} /** end if */
								/** add a block for PCA contract also */
								else if (indUpdatePCAContract) {
									indUpdatePCAContract = Boolean.FALSE;
									sUpdateContract = ServiceConstants.SA_PCA_CONTRACT;
								} /** end if */

								/************************************************************************
								 ** BEGIN CAUD20D CONTRACT PERIOD
								 ************************************************************************/

								/**
								 * Populate the username in the DAM Input
								 * architecture header by copying the service
								 * input header message architecture header to
								 * the DAM Input architecture header.
								 */
								serviceReqHeaderDto = cloneServiceReqHeaderDto(saveApprovalStatusReq);
								/**
								 * Initialize and populate DAM Input Structures
								 */
								serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
								ContractContractPeriodInDto contractPeriodInDto = new ContractContractPeriodInDto();

								ContractOutputDto mContractOutputDto = contractOutDtoForCloseHomeList.get(m);

								contractPeriodInDto.setIdContract(mContractOutputDto.getIdContract());
								/**
								 * Get the logged on users id to pass into the
								 * contract DAM
								 */
								contractPeriodInDto.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());

								/**
								 * The latest period for the contract needs to
								 * be updated. We cannot assume that there is
								 * only one period for a FA Home contract.
								 */
								contractPeriodInDto
										.setNbrCnperPeriod(Long.valueOf(mContractOutputDto.getNbrCnperPeriod()));

								/**
								 * Run loop four times to guarantee all contract
								 * services will be checked.
								 */

								List<ContractServiceOutDto> contractServiceOutputDtlList = mContractOutputDto
										.getContractServiceOutDtoList();
								for (ContractServiceOutDto contractServiceOutputDtl : contractServiceOutputDtlList) {
									/**
									 * If service codes are for a foster
									 * contract do foster processing
									 */
									/**
									 * Change comparison of service returned
									 * from 60A-E to 63A-D
									 */
									if (ServiceConstants.CD_SERV_FOST_L1
											.equals(contractServiceOutputDtl.getCdCnsvcService()) ||

											ServiceConstants.CD_SERV_FOST_L2
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_BASIC
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_MOD
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_SPEC
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_INT
													.equals(contractServiceOutputDtl.getCdCnsvcService())) {
										contractPeriodInDto.setCdCnperStatus(ServiceConstants.CONTRACT_STATUS_CLOSED);

										/**
										 * Compare today's date with Period
										 * Start date
										 */
										if (isSameDay(mContractOutputDto.getDtCnperStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
												new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
											/**
											 * If above function returns zero,
											 * the dates are the same and we
											 * need to add one day to current
											 * date before setting Term and
											 * Closure date to it. This will
											 * avoid an error on the window
											 * regarding start date can not
											 * equal term or closure date.
											 */
											contractPeriodInDto.setDtDtCnperTerm(DateUtils.addDays(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), 1));
											contractPeriodInDto.setDtDtCnperClosure(DateUtils.addDays(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), 1));
										} else {
											contractPeriodInDto.setDtDtCnperTerm(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
											contractPeriodInDto.setDtDtCnperClosure(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
										}

									} /** end if FOSTER service codes */

									/**
									 * If service codes are for an adoptive
									 * contract do foster processing
									 */
									if (ServiceConstants.CD_SERV_ADP_SUB
											.equals(contractServiceOutputDtl.getCdCnsvcService()) ||

											ServiceConstants.CD_SERV_ADP_NON_REC_SUB
													.equals(contractServiceOutputDtl.getCdCnsvcService())) {
										contractPeriodInDto
												.setCdCnperStatus(ServiceConstants.CONTRACT_STATUS_SERVICE_HOLD);
										contractPeriodInDto.setDtDtCnperTerm(mContractOutputDto.getDtCnperTerm());
										contractPeriodInDto.setDtDtCnperClosure(mContractOutputDto.getDtCnperClosure());

									}

									/**
									 * If service codes are for an PCA contract
									 * do PCA contract processing
									 */
									if (ServiceConstants.CD_SERV_PCA_SUB
											.equals(contractServiceOutputDtl.getCdCnsvcService()) ||

											ServiceConstants.CD_SERV_PCA_NON_REC_SUB
													.equals(contractServiceOutputDtl.getCdCnsvcService())) {
										contractPeriodInDto
												.setCdCnperStatus(ServiceConstants.CONTRACT_STATUS_SERVICE_HOLD);
										contractPeriodInDto.setDtDtCnperTerm(mContractOutputDto.getDtCnperTerm());
										contractPeriodInDto.setDtDtCnperClosure(mContractOutputDto.getDtCnperClosure());
									} /** end else if PCA service codes */
								} /**
									 * END loop four times to guarantee all
									 * contract services will be checked.
									 */

								contractPeriodInDto.setDtDtCnperStart(mContractOutputDto.getDtCnperStart());
								contractPeriodInDto.setIndCnperRenewal(ServiceConstants.NO_RENEWAL);
								contractPeriodInDto.setIndCnperSigned(ServiceConstants.SIGNED_YES);
								contractPeriodInDto.setLastUpdate(mContractOutputDto.getSysTsLastUpdate2());

								/**
								 ** DAM Name: CAUD20D. The Contract Period ELB
								 * DAM receives IdContract and performs an AUD
								 * on the indicated row. Delete: a stored
								 * procedure is called to perform a cascade
								 * delete for Contract Version, Contract Service
								 * and Contract County. Add: Performs a full row
								 * insert into Contract Period Table Modify:
								 * Performs a full row update into Contract
								 * Period Table.
								 */
								contractDao.contractPeriodAUD(contractPeriodInDto, serviceReqHeaderDto);

								/*************************************************************
								 ** BEGIN CAUD15D CONTRACT VERSION
								 **************************************************************/
								/**
								 * CAUD15D - CONTRACT VERSION AUD
								 */
								/**
								 * Initialize and populate DAM Input Structure
								 */
								ContractVersionAudReq contractVersionAudReq = new ContractVersionAudReq();
								/**
								 * Populate DAM Input Structure
								 */
								contractVersionAudReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
								contractVersionAudReq.setIdCnver(mContractOutputDto.getIdCnver());
								contractVersionAudReq.setIdContract(mContractOutputDto.getIdContract());
								contractVersionAudReq.setIdCntrctWkr(mContractOutputDto.getIdCntrctWkr());
								/**
								 * The lastest period and version for the
								 * contract needs to be updated. We cannot
								 * assume that there is only one period and one
								 * version for a FA Home contract.
								 */
								contractVersionAudReq
										.setNbrCnverPeriod(Long.valueOf(mContractOutputDto.getNbrCnperPeriod()));
								contractVersionAudReq
										.setNbrCnverVersion(Long.valueOf(mContractOutputDto.getNbrCnverVersion()));
								contractVersionAudReq.setIndCnverVerLock(ServiceConstants.SA_FND_YES);
								contractVersionAudReq.setDtCnverCreate(mContractOutputDto.getDtCnverCreate());
								contractVersionAudReq.setLastUpdate(mContractOutputDto.getSysTsLastUpdate3());
								contractVersionAudReq.setDtCnverEffective(mContractOutputDto.getDtCnverEffective());
								contractVersionAudReq.setLastUpdate(new Date());
								/**
								 * Run loop four times to guarantee all contract
								 * services will be checked.
								 */
								contractServiceOutputDtlList = mContractOutputDto.getContractServiceOutDtoList();
								for (ContractServiceOutDto contractServiceOutputDtl : contractServiceOutputDtlList) {
									/**
									 * If service codes are for a foster
									 * contract do foster processing
									 */
									/**
									 * Change comparison of service returned
									 * from 60A-E to 63A-D
									 */
									if (ServiceConstants.CD_SERV_FOST_L1
											.equals(contractServiceOutputDtl.getCdCnsvcService()) ||

											ServiceConstants.CD_SERV_FOST_L2
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_BASIC
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_MOD
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_SPEC
													.equals(contractServiceOutputDtl.getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_INT
													.equals(contractServiceOutputDtl.getCdCnsvcService())) {
										if (!ObjectUtils.isEmpty(contractPeriodInDto.getDtDtCnperClosure())) {
											contractVersionAudReq.setDtCnverEnd(contractPeriodInDto.getDtDtCnperClosure());
										} else {
											contractVersionAudReq.setDtCnverEnd(new Date());
										}
									} /** end if FOSTER service codes */

									/**
									 * If service codes are for an adoptive
									 * contract do foster processing
									 */
									if (ServiceConstants.CD_SERV_ADP_SUB
											.equals(contractServiceOutputDtl.getCdCnsvcService()) ||

											ServiceConstants.CD_SERV_ADP_NON_REC_SUB
													.equals(contractServiceOutputDtl.getCdCnsvcService())) {
										contractVersionAudReq.setDtCnverEnd(mContractOutputDto.getDtCnverEnd());

									} /** end else if ADOPTIVE */

									/**
									 * If service codes are for a PCA contract
									 * do PCA processing
									 */
									if (ServiceConstants.CD_SERV_PCA_SUB
											.equals(contractServiceOutputDtl.getCdCnsvcService()) ||

											ServiceConstants.CD_SERV_PCA_NON_REC_SUB
													.equals(contractServiceOutputDtl.getCdCnsvcService())) {
										contractVersionAudReq.setDtCnverEnd(mContractOutputDto.getDtCnverEnd());
									} /** end else if PCA */
								} /** end for loop */

								/* CONTRACT VERSION update */
								/**
								 * DAM Name: CAUD15D DAM Description: AUD on
								 * CONTRACT_VERSION table. DELETE: also delete
								 * CONTRACT_COUNTY and CONTRACT_SERVICE based on
								 * this combination (ID_CONTRACT,
								 * PERIOD,VERSION) (i.e., delete all LINE_ITEM)
								 * In this case we are updating the
								 * contractVersion
								 */
								contractVersionAudReq.setLastUpdate(new Date());
								contractVersionAudDao.contractVersionAud(contractVersionAudReq);

								/************************************************************
								 ** BEGIN CAUD08D CONTRACT COUNTY update. This is
								 * added because the contract county end date
								 * needs to be updated simultaneously with the
								 * contract_period and contract_county tables.
								 ************************************************************/
								/**
								 * Initialize and Populate DAM Input Structure
								 */
								ContractCountyInDto contractCountyInDto = new ContractCountyInDto();

								/**
								 * Run loop four times to guarantee all contract
								 * services will be checked.
								 */
								rowCounterTwo = 5;
								if (CollectionUtils.isNotEmpty(contractServiceOutputDtlList) &&
										(ServiceConstants.CD_SERV_ADP_SUB
										.equals(contractServiceOutputDtlList.get(0).getCdCnsvcService())
										|| ServiceConstants.CD_SERV_ADP_NON_REC_SUB
												.equals(contractServiceOutputDtlList.get(0).getCdCnsvcService()))) {
									rowCounterTwo = 2;

								} /** end else if ADOPTIVE */

								/**
								 ** If service codes are for a PCA contract do
								 * PCA processing
								 */
								if (CollectionUtils.isNotEmpty(contractServiceOutputDtlList) &&
										(ServiceConstants.CD_SERV_PCA_SUB
										.equals(contractServiceOutputDtlList.get(0).getCdCnsvcService())
										|| ServiceConstants.CD_SERV_PCA_NON_REC_SUB
												.equals(contractServiceOutputDtlList.get(0).getCdCnsvcService()))) {
									rowCounterTwo = 2;
								}

								/**
								 * end.also changed the loop counter for the
								 * following for loop from tempSvcRowQty to
								 * tempSvcRowQty2 and added another condition
								 */

								for (int j = 0; j < rowCounterTwo &&
										CollectionUtils.isNotEmpty(mContractOutputDto.getContractCountyOutDtoList()) &&
										j < mContractOutputDto.getContractCountyOutDtoList().size() &&
										!ObjectUtils.isEmpty(
												mContractOutputDto.getContractCountyOutDtoList().get(j)); j++) {
									serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
									contractCountyInDto.setIdContract(mContractOutputDto.getIdContract());
									contractCountyInDto.setIdCncnty(mContractOutputDto.getContractCountyOutDtoList().get(j).getIdCncnty());
									/**
									 * Get the logged on users id to pass into
									 * the contract DAM
									 */
									contractCountyInDto.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());
									contractCountyInDto.setIdResource(idResource);
									contractCountyInDto.setNbrCncntyPeriod(mContractOutputDto.getContractCountyOutDtoList().get(j).getNbrCncntyPeriod());
									contractCountyInDto.setNbrCncntyVersion(mContractOutputDto.getContractCountyOutDtoList().get(j).getNbrCncntyVersion());
									contractCountyInDto.setCdCncntyCounty(mContractOutputDto.getContractCountyOutDtoList().get(j).getCdCncnctyCounty());
									contractCountyInDto.setCdCncntyService(mContractOutputDto.getCdCncntyService());
									contractCountyInDto.setNbrCncntyLineItem(mContractOutputDto.getContractCountyOutDtoList().get(j).getNbrCncntyLineItem());
									contractCountyInDto.setDtCncntyEffective(mContractOutputDto.getContractCountyOutDtoList().get(j).getDtCncnctyEffective());
									contractCountyInDto.setDtLastUpdate(new Date());

									/**
									 * If service codes are for a foster
									 * contract do foster processing
									 */
									/**
									 * Change comparison of service returned /*
									 * from 60A-E to 63A-D
									 */
									if (ServiceConstants.CD_SERV_FOST_L1.equals(mContractOutputDto
											.getContractServiceOutDtoList().get(j).getCdCnsvcService()) ||

											ServiceConstants.CD_SERV_FOST_L2.equals(mContractOutputDto
													.getContractServiceOutDtoList().get(j).getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_BASIC.equals(mContractOutputDto
													.getContractServiceOutDtoList().get(j).getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_MOD.equals(mContractOutputDto
													.getContractServiceOutDtoList().get(j).getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_SPEC.equals(mContractOutputDto
													.getContractServiceOutDtoList().get(j).getCdCnsvcService())
											|| ServiceConstants.CD_SERV_FOST_LEV_INT.equals(mContractOutputDto
													.getContractServiceOutDtoList().get(j).getCdCnsvcService())) {
										if (!ObjectUtils.isEmpty(contractPeriodInDto.getDtDtCnperClosure())) {
											contractCountyInDto.setDtCncntyEnd(contractPeriodInDto.getDtDtCnperClosure());
										} else {
											contractCountyInDto.setDtCncntyEnd(new Date());
										}
									} /** end if FOSTER service codes */

									/**
									 * If service codes are for an adoptive
									 * contract do adoptive processing
									 */
									if (ServiceConstants.CD_SERV_ADP_SUB.equals(mContractOutputDto
											.getContractServiceOutDtoList().get(j).getCdCnsvcService())
											|| ServiceConstants.CD_SERV_ADP_NON_REC_SUB.equals(mContractOutputDto
													.getContractServiceOutDtoList().get(j).getCdCnsvcService())) {
										contractCountyInDto.setDtCncntyEnd(mContractOutputDto.getContractCountyOutDtoList().get(j).getDtCncnctyEnd());

									} /** end else if ADOPTIVE service codes */

									/**
									 * If service codes are for a PCA contract
									 * do PCA processing
									 */
									if (ServiceConstants.CD_SERV_PCA_SUB.equals(mContractOutputDto
											.getContractServiceOutDtoList().get(j).getCdCnsvcService())
											|| ServiceConstants.CD_SERV_PCA_NON_REC_SUB.equals(mContractOutputDto
													.getContractServiceOutDtoList().get(j).getCdCnsvcService())) {
										contractCountyInDto.setDtCncntyEnd(mContractOutputDto.getContractCountyOutDtoList().get(j).getDtCncnctyEnd());
									} /** end else if PCA service codes */

									/*
									 * DAM Name: CAUD08D Method Description:
									 * This method perform CRUD Operation on
									 * CONTRACT_COUNTY table
									 * 
									 */
									contractCountyDao.contractCountyAUD(contractCountyInDto, serviceReqHeaderDto);
								} /** end for loop */
								/************************************************************
								 ** END CAUD08D CONTRACT COUNTY update
								 ************************************************************/
								/***********************************************************
								 ** END CAUD20D
								 ************************************************************/
							} /** end if contract is current */
							/**
							 * Increment counter outside of current loop because
							 * we are keeping track of ALL contracts
							 */
							m++;
						}

						/*****************************************************************
						 ** (END): Contract modification process if the contract
						 * already exists and a change has been made to the
						 * home's status
						 ******************************************************************/

						/**************************************************************************
						 ***************************************************************************
						 **
						 ** END CONTRACT CREATION/MODIFICATION
						 **
						 ***************************************************************************
						 **************************************************************************/

						/**
						 * Different processing depending upon RsrcCategory
						 */

						for (RtrvRsrcByStageOutDto rtrvRsrcByStageOutputDto : rtrvRsrcByStageOutDtoList) {
							if (ServiceConstants.FA_CATG_ADOPT.equals(rtrvRsrcByStageOutputDto.getSzCdRsrcCategory())) {
								/**
								 * This logic is applicable to non-Adoptive
								 * homes. Adoptive homes do not have LOCs.
								 */
								/**
								 * Initialize & Populate Input Structure for DAM
								 */
								UpdateFacilityLocReq updateFacilityLocReq = new UpdateFacilityLocReq();
								updateFacilityLocReq.setIdResource(rtrvRsrcByStageOutputDto.getUlIdResource());

								// cReqFuncCd not required to set in this case
								// as the dao doesn't need that to
								// differentiate
								updateFacilityLocReq.setDtFlocEnd(ServiceConstants.NULL_DATE_TYPE);

								/**
								 * DAM Name : CAUDB4D
								 * 
								 * This service Used to end date a facility loc
								 * row
								 */
								updateFacilityLocDao.updateFacilityLoc(updateFacilityLocReq);
							} /** end if different processing */

							/** CALL STAGE PROGRESSION - CCMN06U */
							/**
							 * Only call Stage progression if the previous DAM
							 * call was successful
							 */
							/**
							 ** Initialize and populate CloseStageCase Input
							 * Structure
							 */
							CloseStageCaseInputDto closeStageCaseInputDto = new CloseStageCaseInputDto();

							closeStageCaseInputDto.setIdStage(saveApprovalStatusReq.getIdStage());
							closeStageCaseInputDto.setCdStage(saveApprovalStatusReq.getCdStage());
							closeStageCaseInputDto.setCdStageProgram(
									saveApprovalStatusReq.getAprvlStageProgDto().getCdStageProgram());
							closeStageCaseInputDto.setCdStageReasonClosed(
									!ObjectUtils.isEmpty(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed())
											? saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed()
									: capsResourceDto.getCdRsrcClosureRsn());
							closeStageCaseInputDto.setIdPerson(saveApprovalStatusReq.getIdPerson());
							closeStageCaseService.closeStageCase(closeStageCaseInputDto);

							createCaseFileManagement(saveApprovalStatusReq.getIdStage(),
									saveApprovalStatusReq.getIdCase(), saveApprovalStatusReq.getIdPerson());
						}
					} /** end if */

					/**
					 * Combined two if blocks
					 */
					if ((ServiceConstants.FA_HOME_RVF.equals(approvalEventOutputDto.getCdTask()))
							&& (ServiceConstants.WIN_APPROVE.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction()))) {
						/**
						 * Initialize and Populate Common function Input
						 * Structure
						 */
						TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
						todoCreateInDto.setSysCdTodoCf(ServiceConstants.TODO_UPDATE_STATUS);
						todoCreateInDto.setDtSysDtTodoCfDueFrom(ServiceConstants.NULL_DATE_TYPE);
						todoCreateInDto.setSysIdTodoCfPersCrea(saveApprovalStatusReq.getIdPerson());
						todoCreateInDto.setSysIdTodoCfEvent(idEvent);
						todoCreateInDto.setSysIdTodoCfStage(saveApprovalStatusReq.getIdStage());
						todoCreateInDto.setMergeSplitToDoDto(new MergeSplitToDoDto());

						/**
						 * Calling common to do function to generate to do
						 */
						commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
						/** close if: WcdCdAprvlWinaction=WIN_APPROVE check */
					} /** close if: CdTask=FA_HOME_RVF */

					/******************************************************************
					 **
					 ** END R1/R2 IMPACT FOR F/A HOME 1/23/96
					 **
					 *******************************************************************/
				} /** end for */
				/** */

				if (ServiceConstants.INV_Stage.equals(saveApprovalStatusReq.getCdStage())
						&& ServiceConstants.CPS_PROGRAM.equals(saveApprovalStatusReq.getCdStageProgram())
						&& ServiceConstants.WIN_COMPAPRV.equals(saveApprovalStatusReq.getWcdCdAprvlWinaction())
						&& ServiceConstants.APPROVE_INV_CNCLS.equals(saveApprovalStatusReq.getCdTask())) {

					/**
					 * Retrieve all the 'Principals' associated with the closed
					 * INV
					 */

					/**
					 * DAM Name : CCMNE4D
					 * 
					 * This DAM retrieves a list of person id's and their full
					 * names who are Principals in the stage given as input to
					 * the DAM NOTE: This DAM contains non-GENDAM generated code
					 * which would need to be copied if this DAM is re-GENDAM'd.
					 * 
					 */
					List<PersonDto> personDtoList = stageDao
							.getPersonFromPRNStageByIdStage(saveApprovalStatusReq.getIdStage());

					/**
					 * Retrieve the primary worker for all ADO stages each
					 * 'Principal' (returned from above DAM) is associated with
					 */
					for (PersonDto personDto : personDtoList) {

						/**
						 * Initialize and populate DAM input area
						 */
						GetPrimaryWorkerReq getPrimaryWorkerReq = new GetPrimaryWorkerReq();
						getPrimaryWorkerReq.setIdPerson(personDto.getIdPerson());

						/**
						 * DAM NAME: CCMNJ5D
						 * 
						 * Description: This DAM takes as input an ID PERSON and
						 * performs the following logic:
						 *
						 * 1- It searches the STAGE_PERSON_LINK table and
						 * retrieves all those stages where ID_PERSON =
						 * Input.ID_PERSON.
						 *
						 * 2- For each ID_STAGE retrieved in Step 1, it searches
						 * the STAGE table and verifies that the DT_STAGE_CLOSE
						 * = NULL and retrieves the ID_CASE associated with
						 * ID_STAGE, if any.
						 *
						 * 3- It searches the PERSON table to verify that
						 * CD_PERSON_STATUS = 'A' (active)
						 *
						 * 4- For every stage retrieved in Step 1, it searches
						 * the STAGE_PERSON_LINK table and retrieves the
						 * ID_PERSON where CD_STAGE_PERS_ROLE = "PR" (primary).
						 *
						 * NOTE: This DAM contains non-GENDAM code that would
						 * need to be copied if this DAM is re-GENDAM'd.
						 */
						List<GetPrimaryWorkerDto> primaryWorkerDtoList = aprvlGetPrimaryWorkerDao
								.getPrimaryWorkerIdByStage(getPrimaryWorkerReq).getGetPrimaryWorkerDtoList();

						/**
						 * DAM NAME: CCMNJ5D When retrieving records in DAM
						 * CCMNJ5D, if an error occurs, excluding SQL_NOT_FOUND,
						 * the function returns a general error to the calling
						 * Service. The function aborts processing and all
						 * changes to the database are rolled back. A general
						 * error message is displayed upon returning to the
						 * client.
						 */
						for (GetPrimaryWorkerDto primaryWorkerDto : primaryWorkerDtoList) {
							/**
							 * Create for primary worker
							 */
							CreateStageProgressionEventReq createStageProgressionEventReq = new CreateStageProgressionEventReq();

							StringBuilder todoDescBuilder = new StringBuilder();
							todoDescBuilder.append(personDto.getNmPersonFull());
							todoDescBuilder.append(" is involved in a closed INV Stage  ");
							todoDescBuilder.append(saveApprovalStatusReq.getIdStage());
							todoDescBuilder.append(" in Case ");
							todoDescBuilder.append(saveApprovalStatusReq.getIdCase());
							createStageProgressionEventReq.setTxtTodoDesc(todoDescBuilder.toString());

							createStageProgressionEventReq.setCdTodoType(ServiceConstants.ALERT_TODO);
							createStageProgressionEventReq.setIdTodoPersAssigned(primaryWorkerDto.getIdPerson());
							createStageProgressionEventReq.setIdTodoPersWorker(primaryWorkerDto.getIdPerson());
							createStageProgressionEventReq.setDtTodoCreated(new Date());
							createStageProgressionEventReq.setDtTodoDue(new Date());
							createStageProgressionEventReq.setIdCase(primaryWorkerDto.getIdCase());
							createStageProgressionEventReq.setIdStage(primaryWorkerDto.getIdStage());

							/**
							 * Only navigational tasks/to do's should have a
							 * Null completed date, so set the To do's completed
							 * date to today's date.
							 */
							createStageProgressionEventReq.setDtTodoCompleted(new Date());
							/**
							 * The DAM input structure is memset to NULL, but
							 * the Task due date should actually be set to
							 * NullDate.
							 */
							createStageProgressionEventReq.setDtTaskDue(ServiceConstants.NULL_DATE_TYPE);

							createStageProgressionEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);

							/**
							 * DAM Name: CCMNJ6D Method Name:
							 * createStageProgressionEvent Method Description:
							 * This DAM will ADD a record to the TO*DO table.
							 * Used to create a Task To*Do linked to a Stage
							 * Progression created event
							 */
							createStageProgressionEventDao.createStageProgressionEvent(createStageProgressionEventReq);
						} /** end of for primaryWorkerDtoList */
					} /** end of for INV Principals */
				} /** END: */
			} /*** END: SAVE COMPLETED APPROVAL DETERMINATION SCENERIO ***/
			/*********************************************************************
			 * Prepare output message to be returned and return
			 **********************************************************************/
			
			// Legacy does constructs an output message but its commented out.
			// Whoever, is calling this method. return them the expected results
			// as we have
			// reached the end of this method.
			// or since this a save method, we need to return VOID
			LOGGER.info("Exiting saveApprovalStatus() method in SaveApprovalStatusImpl.");

			// Calling the calendar invite for Family plan approval
			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getEventStatusDto())) {
				if (CHILD_PLAN_APPROVAL_TASK_CODE
						.equalsIgnoreCase(saveApprovalStatusReq.getEventStatusDto().getCdTask())) {
					CapsEmailDto capsEmailDto = familyPlanDao.getFamilyPlanReviewDetail(
							Long.valueOf(saveApprovalStatusReq.getEventStatusDto().getIdEvent()), APPROVAL);
					capsEmailDto.setCdStage(ServiceConstants.CSTAGES_FPR);
					/*
					 * Checking if the next review date is not empty and is
					 * after todays date, also checking for the id Participant
					 * as for legacy stages it will be null. and we need to send
					 * the appointment for the New Stages only.
					 */
					if (!ObjectUtils.isEmpty(capsEmailDto.getDtNxtReviewDate())
							&& capsEmailDto.getDtNxtReviewDate().after(new Date())
							&& !ObjectUtils.isEmpty(capsEmailDto.getNmParticipant())) {
							emailMessage = capsCaseService.sendEmployeeEmail(
								Long.valueOf(saveApprovalStatusReq.getEventStatusDto().getIdEvent()), capsEmailDto,
								saveApprovalStatusReq.getHostName());
					}
				} else
				// Calling the calendar invite for Family plan approval for FSU
				// or
				// FRE.
				if (ServiceConstants.CD_TASK_FSU_APPRV_FAM_PLAN
						.equalsIgnoreCase(saveApprovalStatusReq.getEventStatusDto().getCdTask())
						|| ServiceConstants.CD_TASK_FRE_APPRV_FAM_PLAN
								.equalsIgnoreCase(saveApprovalStatusReq.getEventStatusDto().getCdTask())) {
					CapsEmailDto capsEmailDto = familyPlanDao.getFamilyPlanReviewDetail(
							Long.valueOf(saveApprovalStatusReq.getEventStatusDto().getIdEvent()), APPROVAL);
					capsEmailDto.setCdStage(ServiceConstants.FSU_FRE);
					/*
					 * Checking if the next review date is not empty and is
					 * after todays date, also checking for the id Participant
					 * as for legacy stages it will be null. and we need to send
					 * the appointment for the New Stages only.
					 */
					if (!ObjectUtils.isEmpty(capsEmailDto.getDtNxtReviewDate())
							&& capsEmailDto.getDtNxtReviewDate().after(new Date())
							&& !ObjectUtils.isEmpty(capsEmailDto.getNmParticipant())) {
							emailMessage = capsCaseService.sendEmployeeEmail(
								Long.valueOf(saveApprovalStatusReq.getEventStatusDto().getIdEvent()), capsEmailDto,
								saveApprovalStatusReq.getHostName());
						}
					}
				}
			if(!StringUtils.isEmpty(emailMessage) && ServiceConstants.OUTLOOK_FAILURE.equalsIgnoreCase(emailMessage)){
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(1111);
				approvalStatusRes.setErrorDto(errorDto);
				LOGGER.debug("Email failed.");
			}
			return approvalStatusRes;
		} catch (IllegalAccessException | InvocationTargetException | ParseException exception) {
			LOGGER.debug("Exception Occurred in Save Approval Status Service.");
			throw new ServiceLayerException("Exception Occurred in Save Approval Status Service:" + exception);
		}
	}

	private List<String> getFosterHomeTypes(ResourceDto capsResource) {
		return Stream.of(capsResource.getCdRsrcFaHomeType1(), capsResource.getCdRsrcFaHomeType2(), capsResource.getCdRsrcFaHomeType3(),
						capsResource.getCdRsrcFaHomeType4(), capsResource.getCdRsrcFaHomeType5(), capsResource.getCdRsrcFaHomeType6(),
						capsResource.getCdRsrcFaHomeType7())
				.filter(homeType -> homeType != null && !homeType.isEmpty()).collect(Collectors.toList());

	}
	
	/**
	 * Method Name: processFREConclusionStage
	 * Method Description: Once the user selects ‘Open to FBSS’ and hit ‘Save and Submit’, 
	 * and it is approved, the FRE stage will be closed and progressed to ‘FPR’ stage.
	 * @param idCase
	 * @param idFromStage
	 * @param idUser
	 * @param idApproval
	 * @param idApprover
	 * @return void
	 */
	public void processFREConclusionStage(Long idCase, Long idFromStage, Long idUser, Long idApproval, Long idApprover) {
		
		Long newStageId = 0l;

		// Creates new FPR Stage and returns the stage ID
		StageValueBeanDto stageCreationValBean = new StageValueBeanDto();
		stageCreationValBean.setIdStage(idFromStage);
		stageCreationValBean.setCdStage(ServiceConstants.CSTAGES_FPR);
		stageCreationValBean.setIdCreatedPerson(idUser);
		stageCreationValBean.setNmNewCase("");
		newStageId = stageProgressionService.createNewStage(stageCreationValBean);
		
		//Update New Stage StageReasonClosed and StageClosureCmnts as null
		Stage stage = stageDao.getStageEntityById(newStageId);
		stage.setCdStageReasonClosed(null);
		stage.setTxtStageClosureCmnts(null);
		stageDao.updateStage(stage);

		List<String> approverRelatedRoles = new ArrayList<>();
		approverRelatedRoles.add(ServiceConstants.CROLEALL_PR);
		List<StagePersonValueDto> currentPRList = stageProgDao.selectStagePersonLink(newStageId.intValue(),
				approverRelatedRoles);
		StagePersonValueDto iSPValueBean = currentPRList.get(0);
		arStageProgDao.updateStagePersonLink(iSPValueBean.getIdPerson(), idApprover, newStageId, idCase,
				ServiceConstants.CROLEALL_PR);

		// Copies the open Service Auth to the new stage
		serviceAuthFormService.copyOpenServiceAuthToNewStage(idFromStage, newStageId, idUser,
				ServiceConstants.FPR_SERVICE_AUTH_TASK_CODE);
		
		arStageProgDao.updatePriorStageAndIncomingCallDate(Long.valueOf(newStageId), Long.valueOf(idCase),Long.valueOf(idFromStage), null);

		// Flush the data, to avoid the exception on Database trigger
		approvalStatusDao.sessionFlush();
		
		return ;
	}

	/**
	 * Method Name: populateContractServiceInDto
	 * Method Description: Populates ContractServiceInDto
	 * @param saveApprovalStatusReq
	 * @param idContract
	 * @param cnsvcPeriod
	 * @param cnsvcVersion
	 * @param cnsvcLineItem
	 * @param cnsvcService
	 * @param cnsvcUnitType
	 * @param cnsvcUnitRate
	 * @return ContractServiceInDto
	 */
	private ContractServiceInDto populateContractServiceInDto(SaveApprovalStatusReq saveApprovalStatusReq,
															  Long idContract, Byte cnsvcPeriod, Long cnsvcVersion,
															  Long cnsvcLineItem, String cnsvcService, String cnsvcUnitType, Double cnsvcUnitRate) {

		ContractServiceInDto contractServiceInDto = new ContractServiceInDto();
		contractServiceInDto.setIdContract(idContract);
		contractServiceInDto.setIdCntrctWkr(saveApprovalStatusReq.getIdCntrctWkr());
		contractServiceInDto.setCnsvcPeriod(cnsvcPeriod);
		contractServiceInDto.setCnsvcVersion(cnsvcVersion);
		contractServiceInDto.setCnsvcLineItem(cnsvcLineItem);
		contractServiceInDto.setCdCnsvcService(cnsvcService);
		contractServiceInDto.setCdCnsvcPaymentType(unitRatePaymentType);
		contractServiceInDto.setIndCnsvcNewRow(ServiceConstants.FND_NO);

		contractServiceInDto.setCnsvcUnitType(cnsvcUnitType);
		contractServiceInDto.setCnsvcUnitRate(cnsvcUnitRate);
		contractServiceInDto.setDtLastUpdate(new Date());

		return contractServiceInDto;
	}

	/**
	 * Method Name: getCloseStageCaseInputDto Method Description:
	 * 
	 * @param saveApprovalStatusReq
	 * @param closeStageCaseInputDto
	 */
	private void getCloseStageCaseInputDto(SaveApprovalStatusReq saveApprovalStatusReq,
			CloseStageCaseInputDto closeStageCaseInputDto) {
		closeStageCaseInputDto.setIdStage(saveApprovalStatusReq.getIdStage());
		closeStageCaseInputDto.setCdStage(saveApprovalStatusReq.getCdStage());
		closeStageCaseInputDto.setCdStageProgram(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageProgram());
		closeStageCaseInputDto
				.setCdStageReasonClosed(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageReasonClosed());
		closeStageCaseInputDto.setIdPerson(saveApprovalStatusReq.getIdPerson());
	}

	/**
	 * Method Name: checkCatHomeType; Method Description: Check to see if
	 * category and/or home type of a resource is changed.
	 * 
	 * @param idResource
	 * @return Boolean
	 */
	private Boolean checkCatHomeType(Long idResource) throws IllegalAccessException, InvocationTargetException {

		ResourceDto resourceDto = capsResourceDao.getResourceById(idResource);
		Boolean hasCategoryChanged = Boolean.FALSE;

		String categoryHomeType1 = resourceDto.getCdRsrcCategory();
		List<String> homeType1List = new ArrayList<>();
		homeType1List.add(resourceDto.getCdRsrcFaHomeType1());
		homeType1List.add(resourceDto.getCdRsrcFaHomeType2());
		homeType1List.add(resourceDto.getCdRsrcFaHomeType3());
		homeType1List.add(resourceDto.getCdRsrcFaHomeType4());
		homeType1List.add(resourceDto.getCdRsrcFaHomeType5());
		homeType1List.add(resourceDto.getCdRsrcFaHomeType6());
		homeType1List.add(resourceDto.getCdRsrcFaHomeType7());

		ResourceHistoryAuditInDto resourceHistoryAuditInDto = new ResourceHistoryAuditInDto();
		resourceHistoryAuditInDto.setIdAudResource(idResource);
		resourceHistoryAuditInDto.setCdRshsAudFaHomeStatus(ServiceConstants.HOME_STATUS_APVD_ACT);

		/**
		 * DAM NAME: CLSS82D * Method Name:
		 * getResourceHistoryAuditByIdRescAndHmeStatus Method Description: DAM
		 * NAME: CLSS82D; This DAM will select a full row from the Resource
		 * History Audit table given an Id_Resource & facility type.
		 */
		List<ResourceHistoryAudit> resourceHistoryList = resourceHistoryAuditDao.getResourceHistoryAuditByIdRescAndHmeStatus(resourceHistoryAuditInDto);

		/** we are interested in second record */

		if (CollectionUtils.isNotEmpty(resourceHistoryList) && !ObjectUtils.isEmpty(resourceHistoryList.get(1))) {
			String categoryHomeType2 = resourceHistoryList.get(1).getCdRshsAudCategory();
			List<String> homeType2List = new ArrayList<>();
			homeType2List.add(resourceHistoryList.get(1).getCdRshsAudFaHomeType1());
			homeType2List.add(resourceHistoryList.get(1).getCdRshsAudFaHomeType2());
			homeType2List.add(resourceHistoryList.get(1).getCdRshsAudFaHomeType3());
			homeType2List.add(resourceHistoryList.get(1).getCdRshsAudFaHomeType4());
			homeType2List.add(resourceHistoryList.get(1).getCdRshsAudFaHomeType5());
			homeType2List.add(resourceHistoryList.get(1).getCdRshsAudFaHomeType6());
			homeType2List.add(resourceHistoryList.get(1).getCdRshsAudFaHomeType7());

			if (!categoryHomeType1.equals(categoryHomeType2)
					|| !homeType1List.toString().contentEquals(homeType2List.toString())) {
				hasCategoryChanged = Boolean.TRUE;
			}
		}

		return hasCategoryChanged;
	}

	/** END CheckCatHomeType */

	/**
	 * Method Name: contractVerSerCnty
	 **
	 ** Method Description: Updates and/or Inserts into Contract_County,
	 ** Contract_version and Contract_Service tables. Dam Name: CSES01D, CAUD15D,
	 * CAUD17D
	 * 
	 * @param saveApprovalStatusReq
	 * @param idContract
	 * @param nbrCnperPeriod
	 * @param idResource
	 * @return Boolean
	 */
	private void contractVerSerCnty(SaveApprovalStatusReq saveApprovalStatusReq, Long idContract,
			Integer nbrCnperPeriod, Long idResource, ServiceReqHeaderDto serviceReqHeaderDto) {
		/**
		 * Declare local variables
		 */
		Long localNumberOfDays;
		Boolean bDeleteInsertContractCounty = Boolean.TRUE;
		int localAdoptOrFoster;
		Boolean bGroupHome2 = Boolean.FALSE;
		String TEMP_CONTRACT_COUNTY = ServiceConstants.EMPTY_STR;
		List<String> ADOPTIVE_OR_FOSTER_LIST = new ArrayList<>();
		List<ContractServiceOutDto> contractServiceOutDtoList = new ArrayList<>();
		/**
		 * Initialize DAM Input Structure for CAUD15D
		 */
		ContractVersionAudReq contractVersionAudReq = new ContractVersionAudReq();

		ContractVersionInDto contractVersionInDto = new ContractVersionInDto();
		contractVersionInDto.setIdContract(idContract);
		contractVersionInDto.setNbrCnverPeriod(Long.valueOf(nbrCnperPeriod));

		/**
		 * DAM Name : CSES01D Method Description: This DAM will receive ID
		 * CONTRACT and NBR CNPER PERIOD (of the previous period) and will
		 * return all columns for the latest contract version record in the
		 * CONTRACT VERSION table.
		 */
		ContractVersionOutDto contractVersionOutDto = contractVersionDao.getLatestContractVersion(contractVersionInDto);

		contractVersionAudReq.setIdContract(contractVersionOutDto.getIdContract());
		contractVersionAudReq.setIdCntrctWkr(contractVersionOutDto.getIdCntrctWkr());
		contractVersionAudReq.setNbrCnverPeriod(Long.valueOf(contractVersionOutDto.getNbrCnverPeriod()));
		contractVersionAudReq.setNbrCnverVersion(Long.valueOf(contractVersionOutDto.getNbrCnverVersion()));
		if(!ObjectUtils.isEmpty(contractVersionOutDto.getNbrCnverNoShowPct())) {
			contractVersionAudReq.setNbrCnverNoShowPct(contractVersionOutDto.getNbrCnverNoShowPct());
		}
		contractVersionAudReq.setIndCnverVerLock(contractVersionOutDto.getIndCnverVerLock() ? 'Y' : 'N');
		contractVersionAudReq.setTxtCnverComment(contractVersionOutDto.getTxtCnverComment());
		contractVersionAudReq.setIdCnver(contractVersionOutDto.getIdCnver());
		contractVersionAudReq.setScrTmGeneric1(contractVersionOutDto.getScrTmGeneric1());
		contractVersionAudReq.setDtCnverEnd(contractVersionOutDto.getDtCnverEnd());
		contractVersionAudReq.setScrTmGeneric2(contractVersionOutDto.getScrTmGeneric2());
		contractVersionAudReq.setDtCnverEffective(contractVersionOutDto.getDtCnverEffective());

		localNumberOfDays = TimeUnit.DAYS.convert(
				(new Date().getTime() - contractVersionAudReq.getDtCnverEffective().getTime()), TimeUnit.MILLISECONDS);

		/**
		 * if current date is only 2 days different from Eff date, then don't
		 * update contract_version and contract-county tables There are 1440
		 * Minutes in One Day
		 */
		if (localNumberOfDays <= 2)
			bDeleteInsertContractCounty = Boolean.FALSE;

		contractVersionAudReq.setDtCnverCreate(contractVersionOutDto.getDtCnverCreate());
		contractVersionAudReq.setScrTmGeneric3(contractVersionOutDto.getScrTmGeneric3());
		contractVersionAudReq.setLastUpdate(contractVersionOutDto.getTsLastUpdate());
		contractVersionAudReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);

		for (int i = 0; i < 2 && bDeleteInsertContractCounty; i++) {
			if (i == 1) {
				contractVersionAudReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				contractVersionAudReq.setIndCnverVerLock(ServiceConstants.SA_FND_YES);
				contractVersionAudReq.setDtCnverEnd(contractVersionOutDto.getDtCnverEnd());
				contractVersionAudReq.setDtCnverEffective(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
				contractVersionAudReq.setLastUpdate(new Date());
				contractVersionAudReq.setTxtCnverComment(ServiceConstants.BLANK);
				contractVersionAudReq.setNbrCnverVersion(Long.valueOf(contractVersionOutDto.getNbrCnverVersion()) + 1);
				contractVersionAudReq.setIdCnver(ServiceConstants.ZERO);
			}

			/**
			 * DAM Name: CAUD15D Method Name: ContractVersionAud Method
			 * Description: AUD on CONTRACT_VERSION table. DELETE: also delete
			 * CONTRACT_COUNTY and CONTRACT_SERVICE based on this combination
			 * (ID_CONTRACT, PERIOD,VERSION) (i.e., delete all LINE_ITEM)
			 */
			contractVersionAudDao.contractVersionAud(contractVersionAudReq);
		} /** end of for */

		if (bDeleteInsertContractCounty) {

			/** Set input structure for CLSS13D */
			ContractServiceInDto contactServiceInDto = new ContractServiceInDto();
			contactServiceInDto.setIdContract(idContract);

			/**
			 * DAM Name: CLSS13D Method Description: retrieves a full row from
			 * CONTRACT_SERVICE given the ID_CONTRACT.
			 */
			contractServiceOutDtoList = contractServiceDao.getContractService(contactServiceInDto);

		} /** end if bDeleteInsertContractCounty */

		if (bDeleteInsertContractCounty) {
			/**
			 * Initialize DAM Input Structure
			 */
			ContractServiceInDto contractServiceInDto = new ContractServiceInDto();
			ContractServiceOutDto firstContractServiceOutDto = contractServiceOutDtoList.get(0);
			/**
			 * Populate DAM Input Structure
			 */
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
			contractServiceInDto.setIdCnsvc(ServiceConstants.ZERO);
			for (Integer i = 0; i < NBR_SVC_CODE_SIXTY_ABCD && CollectionUtils.isNotEmpty(contractServiceOutDtoList); i++) {

				contractServiceInDto.setIdContract(firstContractServiceOutDto.getIdContract());
				contractServiceInDto.setIdCntrctWkr(firstContractServiceOutDto.getIdCntrctWkr());
				contractServiceInDto.setCnsvcVersion(Long.valueOf(firstContractServiceOutDto.getNbrCnsvcVersion()) + 1);
				contractServiceInDto.setCnsvcPeriod(firstContractServiceOutDto.getNbrCnsvcPeriod());
				contractServiceInDto.setCnsvcLineItem(Long.valueOf((i + 1)));

				switch (i) {
				case 0:
					contractServiceInDto.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_BASIC);
					contractServiceInDto.setCnsvcUnitRate(ServiceConstants.FOSTER_PAYMENT_LEV_BASIC);
					break;
				case 1:
					contractServiceInDto.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_MOD);
					contractServiceInDto.setCnsvcUnitRate(ServiceConstants.FOSTER_PAYMENT_LEV_MOD);
					break;
				case 2:
					contractServiceInDto.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_SPEC);
					contractServiceInDto.setCnsvcUnitRate(ServiceConstants.FOSTER_PAYMENT_LEV_SPEC);
					break;
				case 3:
					contractServiceInDto.setCdCnsvcService(ServiceConstants.CD_SERV_FOST_LEV_INT);
					contractServiceInDto.setCnsvcUnitRate(null);
					break;
				default:
					break;
				}/** end switch */

				contractServiceInDto.setCdCnsvcPaymentType(firstContractServiceOutDto.getCdCnsvcPaymentType());
				contractServiceInDto.setIndCnsvcNewRow(firstContractServiceOutDto.getIndCnsvcNewRow());
				contractServiceInDto.setCnsvcUnitType(firstContractServiceOutDto.getCdCnsvcUnitType());
				if (!ObjectUtils.isEmpty(firstContractServiceOutDto.getNbrCnsvcFedMatch())) {
					contractServiceInDto.setCnsvcFedMatch(firstContractServiceOutDto.getNbrCnsvcFedMatch().shortValue());
				}
				if (!ObjectUtils.isEmpty(firstContractServiceOutDto.getNbrCnsvcLocalMatch())) {
					contractServiceInDto.setCnsvcLocalMatch(firstContractServiceOutDto.getNbrCnsvcLocalMatch().shortValue());
				}
				contractServiceInDto.setAmtCnsvcAdminAllUsed(firstContractServiceOutDto.getAmtCnsvcAdminAllUsed());
				contractServiceInDto.setAmtCnsvcEquip(firstContractServiceOutDto.getAmtCnsvcEquip());
				contractServiceInDto.setAmtCnsvcEquipUsed(firstContractServiceOutDto.getAmtCnsvcEquipUsed());
				contractServiceInDto.setAmtCnsvcFrgBenft(firstContractServiceOutDto.getAmtCnsvcFrgBenft());
				contractServiceInDto.setAmtCnsvcFrgBenftUsed(firstContractServiceOutDto.getAmtCnsvcFrgBenftUsed());
				contractServiceInDto.setAmtCnsvcOffItemUsed(firstContractServiceOutDto.getAmtCnsvcOffItemUsed());
				contractServiceInDto.setAmtCnsvcOther(firstContractServiceOutDto.getAmtCnsvcOther());
				contractServiceInDto.setAmtCnsvcOtherUsed(firstContractServiceOutDto.getAmtCnsvcOtherUsed());
				contractServiceInDto.setAmtCnsvcSalary(firstContractServiceOutDto.getAmtCnsvcSalary());
				contractServiceInDto.setAmtCnsvcSalaryUsed(firstContractServiceOutDto.getAmtCnsvcSalaryUsed());
				contractServiceInDto.setAmtCnsvcSupply(firstContractServiceOutDto.getAmtCnsvcSupply());
				contractServiceInDto.setAmtCnsvcSupplyUsed(firstContractServiceOutDto.getAmtCnsvcSupplyUsed());
				contractServiceInDto.setAmtCnsvcTravel(firstContractServiceOutDto.getAmtCnsvcTravel());
				contractServiceInDto.setAmtCnsvcTravelUsed(firstContractServiceOutDto.getAmtCnsvcTravelUsed());
				contractServiceInDto.setAmtCnsvcUnitRate(firstContractServiceOutDto.getAmtCnsvcUnitRate());
				contractServiceInDto.setAmtCnsvcUnitRateUsed(firstContractServiceOutDto.getAmtCnsvcUnitRateUsed());
				contractServiceInDto.setDtLastUpdate(firstContractServiceOutDto.getDtLastUpdate());
				/**
				 ** DAM Name: CAUD17D. The Contract Service AUD performs a full
				 * row insert to the Contract Service table.
				 */
				contractServiceDao.contractServiceAUD(contractServiceInDto, serviceReqHeaderDto);

			} /** end of for */
		} /** end of if */

		/** Find out the levels of care of the resource */

		/**
		 * DAM Name: CRES04D This DAM performs AUD functionality on the FACILITY
		 * INVST DTL table. This DAM only inserts.
		 */
		ResourceDto resourceDto = capsResourceDao.getResourceById(idResource);

		localAdoptOrFoster = ServiceConstants.ZERO_SHORT;
		ADOPTIVE_OR_FOSTER_LIST = new ArrayList<>();
		if (!TypeConvUtil.isNullOrEmpty(resourceDto)
				&& !ServiceConstants.FA_CATG_ADOPT.equals(resourceDto.getCdRsrcCategory())) {

			ADOPTIVE_OR_FOSTER_LIST.add(resourceDto.getCdRsrcFaHomeType1());
			ADOPTIVE_OR_FOSTER_LIST.add(resourceDto.getCdRsrcFaHomeType2());
			ADOPTIVE_OR_FOSTER_LIST.add(resourceDto.getCdRsrcFaHomeType3());
			ADOPTIVE_OR_FOSTER_LIST.add(resourceDto.getCdRsrcFaHomeType4());
			ADOPTIVE_OR_FOSTER_LIST.add(resourceDto.getCdRsrcFaHomeType5());
			ADOPTIVE_OR_FOSTER_LIST.add(resourceDto.getCdRsrcFaHomeType6());
			ADOPTIVE_OR_FOSTER_LIST.add(resourceDto.getCdRsrcFaHomeType7());

			localAdoptOrFoster = NBR_SVC_CODE_SIXTY_A;

			if (ADOPTIVE_OR_FOSTER_LIST.get(0) == null) {
				localAdoptOrFoster = ServiceConstants.ZERO_SHORT;
			} else {

				/**
				 * loop through the rows to see if any of them are group homes,
				 * if they are set an indicator
				 */
				for (String adoptiveOrFosterVal : ADOPTIVE_OR_FOSTER_LIST) {
					if (ServiceConstants.FOST_TYPE_GROUP.toString().equals(adoptiveOrFosterVal)) {
						bGroupHome2 = Boolean.TRUE;
						break;
					}
				}

				for (String adoptiveOrFosterValue : ADOPTIVE_OR_FOSTER_LIST) {
					if (ServiceConstants.FOST_TYPE_HABIL.toString().equals(adoptiveOrFosterValue)
							|| ServiceConstants.FOST_TYPE_THER.toString().equals(adoptiveOrFosterValue)
							|| ServiceConstants.FOST_TYPE_PRIM_MED.toString().equals(adoptiveOrFosterValue)) {
						/**
						 * if group home is false, save 63A-D else save 63A-C
						 */
						if (!bGroupHome2) {
							localAdoptOrFoster = NBR_SVC_CODE_SIXTY_ABCD;
						} else {
							localAdoptOrFoster = NBR_SVC_CODE_SIXTY_ABC;
						}
						break;
					}
				}
			}
			TEMP_CONTRACT_COUNTY = resourceDto.getCdRsrcCnty();
		}

		// prepare input structure for CLSS68D
		ContractCountyInDto contractCountyInDto = new ContractCountyInDto();
		contractCountyInDto.setIdContract(idContract);
		contractCountyInDto.setNbrCncntyPeriod(Integer.valueOf(nbrCnperPeriod));
		contractCountyInDto.setNbrCncntyVersion(Integer.valueOf(contractVersionOutDto.getNbrCnverVersion()));

		/**
		 * DAM NAME: CLSS68D Method Description: retrieves a full row from
		 * CONTRACT COUNTY given the ID_CONTRACT, NBR_CNVER_PERIOD and
		 * NBR_CNCNTY_VERSION.
		 */
		List<ContractCountyOutDto> contractCountyOutDtoList = contractCountyDao.getContractCounty(contractCountyInDto);

		/** Update the selected records from previous DAM */
		if (!bDeleteInsertContractCounty && !CollectionUtils.isEmpty(contractCountyOutDtoList)) {
			List<Long> contractCountyIdList = contractCountyOutDtoList.stream().map(contractCountyOutDto -> contractCountyOutDto.getIdCncnty()).collect(Collectors.toList());
			contractCountyDao.deleteContractCounties(contractCountyIdList);
		} else {
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
			for (ContractCountyOutDto contractCountyOutDto : contractCountyOutDtoList) {
				contractCountyInDto = new ContractCountyInDto();
				contractCountyInDto.setIdCncnty(contractCountyOutDto.getIdCncnty());
				contractCountyInDto.setCdCncntyCounty(contractCountyOutDto.getCdCncnctyCounty());
				contractCountyInDto.setDtLastUpdate(contractCountyOutDto.getDtLastUpdate());
				contractCountyInDto.setIdContract(contractCountyOutDto.getIdContract());
				contractCountyInDto.setNbrCncntyPeriod(contractCountyOutDto.getNbrCncntyPeriod());
				contractCountyInDto.setIdCntrctWkr(contractCountyOutDto.getIdCntrctWkr());
				contractCountyInDto.setIdResource(contractCountyOutDto.getIdResource());
				contractCountyInDto.setCdCncntyService(contractCountyOutDto.getCdCncnctyService());
				contractCountyInDto.setTmScrTmGeneric1(contractCountyOutDto.getTmScrTmGeneric1());
				contractCountyInDto.setTmScrTmGeneric2(contractCountyOutDto.getTmScrTmGeneric2());
				contractCountyInDto.setNbrCncntyLineItem(contractCountyOutDto.getNbrCncntyLineItem());
				contractCountyInDto.setNbrCncntyVersion(contractCountyOutDto.getNbrCncntyVersion());
				contractCountyInDto.setDtCncntyEffective(contractCountyOutDto.getDtCncnctyEffective());
				contractCountyInDto.setDtCncntyEnd(contractCountyOutDto.getDtCncnctyEnd());

				/**
				 * DAM Name: CAUD08D Method Description: This method perform
				 * CRUD Operation on CONTRACT_COUNTY table
				 */
				contractCountyDao.contractCountyAUD(contractCountyInDto, serviceReqHeaderDto);
			} /** end of for */
		} /** end else */
		/** Insert new records into Contract_County */
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		contractCountyInDto = new ContractCountyInDto();
		contractCountyInDto.setIdCncnty(ServiceConstants.ZERO);
		if (!bDeleteInsertContractCounty) {
			contractCountyInDto.setNbrCncntyVersion(contractVersionOutDto.getNbrCnverVersion());
		} else {
			contractCountyInDto.setNbrCncntyVersion(contractVersionOutDto.getNbrCnverVersion() + 1);
		}
		contractCountyInDto.setCdCncntyCounty(TEMP_CONTRACT_COUNTY);
		contractCountyInDto.setDtLastUpdate(contractVersionOutDto.getTsLastUpdate());
		contractCountyInDto.setIdContract(contractVersionOutDto.getIdContract());
		contractCountyInDto.setNbrCncntyPeriod(contractVersionOutDto.getNbrCnverPeriod());
		contractCountyInDto.setIdCntrctWkr(contractVersionOutDto.getIdCntrctWkr());
		contractCountyInDto.setIdResource(idResource);
		contractCountyInDto.setTmScrTmGeneric1(contractVersionOutDto.getScrTmGeneric1());
		contractCountyInDto.setTmScrTmGeneric2(contractVersionOutDto.getScrTmGeneric2());
		contractCountyInDto.setDtCncntyEffective(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		contractCountyInDto.setDtCncntyEnd(contractVersionOutDto.getDtCnverEnd());
		for (Integer i = 0; i < localAdoptOrFoster; i++) {
			contractCountyInDto.setNbrCncntyLineItem((i + 1));
			switch (i) {
			case 0:
				contractCountyInDto.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_BASIC);
				break;

			case 1:
				contractCountyInDto.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_MOD);
				break;

			case 2:
				contractCountyInDto.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_SPEC);
				break;

			case 3:
				contractCountyInDto.setCdCncntyService(ServiceConstants.CD_SERV_FOST_LEV_INT);
				break;

			default:
				break;
			} /** end switch */

			/**
			 * DAM Name: CLSS68D This method perform CRUD Operation on
			 * CONTRACT_COUNTY table
			 */
			contractCountyDao.contractCountyAUD(contractCountyInDto, serviceReqHeaderDto);
		} /** end of for */
	}

	/**
	 * Method Name: getPersonStagePersonLinkTypeInDto; Method Description:
	 * Create a PersonStagePersonLinkTypeInDto using saveApprovalStatusReq for
	 * the given StagePersonType
	 * 
	 * @param saveApprovalStatusReq
	 * @param personType
	 * @return PersonStagePersonLinkTypeInDto
	 */

	private PersonStagePersonLinkTypeInDto getPersonStagePersonLinkTypeInDto(
			SaveApprovalStatusReq saveApprovalStatusReq, String personType) {

		/**
		 * Populate the username in the DAM input architecture header by copying
		 * the service input header message architecture header to the DAM Input
		 * architecture header
		 */
		PersonStagePersonLinkTypeInDto personStagePersonLinkTypeInDto = new PersonStagePersonLinkTypeInDto();
		personStagePersonLinkTypeInDto.setIdStage(Long.valueOf(saveApprovalStatusReq.getIdStage()));
		if (!ObjectUtils.isEmpty(personType)) {
			personStagePersonLinkTypeInDto.setCdStagePersType(personType);
		}
		return personStagePersonLinkTypeInDto;
	}

	/**
	 * Method Name: getStagePersonLinkStTypeRoleInDto; Method Description:
	 * Create a StagePersonLinkStTypeRoleInDto using saveApprovalStatusReq
	 * 
	 * @param saveApprovalStatusReq
	 * @return StagePersonLinkStTypeRoleInDto
	 */
	private StagePersonLinkStTypeRoleInDto getStagePersonLinkStTypeRoleInDto(
			SaveApprovalStatusReq saveApprovalStatusReq, String persRole, String persType) {
		StagePersonLinkStTypeRoleInDto stagePersonLinkStTypeRoleInDto = new StagePersonLinkStTypeRoleInDto();
		stagePersonLinkStTypeRoleInDto.setIdStage(Long.valueOf(saveApprovalStatusReq.getIdStage()));
		stagePersonLinkStTypeRoleInDto.setCdStagePersRole(persRole);
		stagePersonLinkStTypeRoleInDto.setCdStagePersType(persType);
		return stagePersonLinkStTypeRoleInDto;
	}

	/**
	 * Method Name: geteventInsUpdDelInDto; Method Description; this method will
	 * get required parameters for DAM CCMN46D
	 * 
	 * @param saveApprovalStatusReq
	 * @return EventInsUpdDelInDto
	 */
	private EventInsUpdDelInDto getEventInsUpdDelInDto(SaveApprovalStatusReq saveApprovalStatusReq) {
		EventInsUpdDelInDto eventInsUpdDelInDto = new EventInsUpdDelInDto();
		eventInsUpdDelInDto.setIdEvent(Long.valueOf(saveApprovalStatusReq.getEventStatusDto().getIdEvent()));
		eventInsUpdDelInDto.setIdPerson(Long.valueOf(saveApprovalStatusReq.getEventStatusDto().getIdPerson()));
		eventInsUpdDelInDto.setIdStage(Long.valueOf(saveApprovalStatusReq.getEventStatusDto().getIdStage()));
		eventInsUpdDelInDto.setDtEventOccurred(saveApprovalStatusReq.getEventStatusDto().getDtEventOccurred());
		eventInsUpdDelInDto.setDtEventModified(saveApprovalStatusReq.getDtEventModified());
		eventInsUpdDelInDto.setCdEventStatus(saveApprovalStatusReq.getEventStatusDto().getCdEventStatus());
		eventInsUpdDelInDto.setCdTask(saveApprovalStatusReq.getEventStatusDto().getCdTask());
		eventInsUpdDelInDto.setCdEventType(saveApprovalStatusReq.getEventStatusDto().getCdEventType());
		eventInsUpdDelInDto.setEventDescr(saveApprovalStatusReq.getEventDescr());
		eventInsUpdDelInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
		eventInsUpdDelInDto.setDtEventLastUpdate(saveApprovalStatusReq.getEventStatusDto().getTsLastUpdate());
		return eventInsUpdDelInDto;

	}

	/**
	 * Method Name: getApprovalStatusFacilityIndicatorDto Method Description:
	 * Create a ApprovalStatusFacilityIndicatorDto using saveApprovalStatusReq
	 * for DAM CCMN61D to Save the Approval Determination.
	 * 
	 * @param saveApprovalStatusReq
	 * @return ApprovalStatusFacilityIndicatorDto
	 */
	private ApprovalStatusFacilityIndicatorDto getApprovalStatusFacilityIndicatorDto(
			SaveApprovalStatusReq saveApprovalStatusReq) throws ParseException {
		ApprovalStatusFacilityIndicatorDto approvalStatusFacilityIndicatorDto = new ApprovalStatusFacilityIndicatorDto();

		if (!ObjectUtils.isEmpty(saveApprovalStatusReq)) {
			// Added Null Check
			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto()) && !ObjectUtils
					.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getDtApproversDetermination())) {
				approvalStatusFacilityIndicatorDto.setDtApproversDetermination(
						saveApprovalStatusReq.getApproveApprovalDto().getDtApproversDetermination());

				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getTmScrTmApprovalTime()))
					approvalStatusFacilityIndicatorDto.setScrTmApprovalTime(
							saveApprovalStatusReq.getApproveApprovalDto().getTmScrTmApprovalTime());

				String approvalDate = new SimpleDateFormat("MM/dd/yyyy")
						.format(saveApprovalStatusReq.getApproveApprovalDto().getDtApproversDetermination());

				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");

				Date dtApproversDetermination = format.parse(
						approvalDate + " " + saveApprovalStatusReq.getApproveApprovalDto().getTmScrTmApprovalTime());
				approvalStatusFacilityIndicatorDto.setDtApproversDetermination(dtApproversDetermination);

			}

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getIdPerson()))
				approvalStatusFacilityIndicatorDto
						.setIdPerson(saveApprovalStatusReq.getApproveApprovalDto().getIdPerson());

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getCdApproversStatus()))
				approvalStatusFacilityIndicatorDto
						.setCdApproversStatus(saveApprovalStatusReq.getApproveApprovalDto().getCdApproversStatus());

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getApproversComments()))
				approvalStatusFacilityIndicatorDto
						.setTxtApproversComments(saveApprovalStatusReq.getApproveApprovalDto().getApproversComments());

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getCdApprovalLength()))
				approvalStatusFacilityIndicatorDto
						.setCdApprovalLength(saveApprovalStatusReq.getApproveApprovalDto().getCdApprovalLength());

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getIndWithinWorkerControl()))
				approvalStatusFacilityIndicatorDto.setIndWithinWorkerControl(
						saveApprovalStatusReq.getApproveApprovalDto().getIndWithinWorkerControl());

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getIdApprovers()))
				approvalStatusFacilityIndicatorDto
						.setIdApprovers(saveApprovalStatusReq.getApproveApprovalDto().getIdApprovers());

			if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
					&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getTsLastUpdate()))
				approvalStatusFacilityIndicatorDto
						.setLastUpdate(saveApprovalStatusReq.getApproveApprovalDto().getTsLastUpdate());

			if (ServiceConstants.INV_Stage.equals(saveApprovalStatusReq.getCdStage()) && ServiceConstants.CPS_PROGRAM
					.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageProgram())) {

				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getCdOvrllDisptn()))
					approvalStatusFacilityIndicatorDto
							.setCdOvrllDisptn(saveApprovalStatusReq.getApproveApprovalDto().getCdOvrllDisptn());

				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getCdStageReasonClosed()))
					approvalStatusFacilityIndicatorDto.setCdStageReasonClosed(
							saveApprovalStatusReq.getApproveApprovalDto().getCdStageReasonClosed());

				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto())
						&& !ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getCdJobClass()))
					approvalStatusFacilityIndicatorDto
							.setCdJobClass(saveApprovalStatusReq.getApproveApprovalDto().getCdJobClass());

				if (!ObjectUtils.isEmpty(saveApprovalStatusReq.getApproveApprovalDto()) && !ObjectUtils
						.isEmpty(saveApprovalStatusReq.getApproveApprovalDto().getIndEmpConfirmedHrmis()))
					approvalStatusFacilityIndicatorDto.setIndEmpConfirmedHrmis(
							saveApprovalStatusReq.getApproveApprovalDto().getIndEmpConfirmedHrmis());

			}
			approvalStatusFacilityIndicatorDto.setDtDeterminationRecorded(new Date());

			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
			approvalStatusFacilityIndicatorDto.setScrDeterminationRecordedTime((dateFormat.format(new Date())));
		}
		return approvalStatusFacilityIndicatorDto;
	}

	/**
	 * Method Name: initializeTodo; Method Description: Create a new tododto
	 * using saveApprovalStatusReq
	 * 
	 * @param saveApprovalStatusReq
	 * @return TodoDto
	 */
	private TodoDto initializeTodo(SaveApprovalStatusReq saveApprovalStatusReq) {
		TodoDto todoDto = new TodoDto();
		todoDto.setReqFuncCd(saveApprovalStatusReq.getReqFuncCd());
		todoDto.setCdTodoType(saveApprovalStatusReq.getCdTodoType());
		todoDto.setDtTodoCreated(saveApprovalStatusReq.getdTodoCreated());
		todoDto.setDtTodoCompleted(saveApprovalStatusReq.getDtTodoCompleted());
		todoDto.setDtTodoDue(saveApprovalStatusReq.getDtTodoDue());
		todoDto.setDtTodoTaskDue(saveApprovalStatusReq.getDtTaskDue());
		todoDto.setIdTodoCase(saveApprovalStatusReq.getIdCase());
		todoDto.setIdTodoPersAssigned(saveApprovalStatusReq.getIdTodoPersAssigned());
		todoDto.setIdTodoPersWorker(saveApprovalStatusReq.getIdTodoPersWorker());
		todoDto.setIdTodoStage(saveApprovalStatusReq.getIdStage());
		todoDto.setCdTodoTask(saveApprovalStatusReq.getCdTask());
		todoDto.setTodoDesc(saveApprovalStatusReq.getTxtTodoDesc());
		todoDto.setIdTodoPersCreator(saveApprovalStatusReq.getIdTodoPersCreator());
		todoDto.setDtLastUpdate(new Date());
		return todoDto;
	}

	private TodoDto populateChildPlanReviewAlert(SaveApprovalStatusReq saveApprovalStatusReq) {
		TodoDto todoDto = new TodoDto();
		todoDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		todoDto.setCdTodoType(TODO_ALERT);
		todoDto.setDtTodoCreated(new Date());
		todoDto.setDtTodoCompleted(ServiceConstants.NULL_DATE_TYPE);
		todoDto.setDtTodoTaskDue(saveApprovalStatusReq.getDtTaskDue());
		todoDto.setIdTodoCase(saveApprovalStatusReq.getIdCase());
		todoDto.setIdTodoPersWorker(saveApprovalStatusReq.getIdTodoPersWorker());
		todoDto.setIdTodoStage(saveApprovalStatusReq.getIdStage());
		todoDto.setCdTodoTask(ServiceConstants.CHILD_PLAN_TASK_CODE_SUB);
		todoDto.setIdTodoPersCreator(saveApprovalStatusReq.getIdTodoPersCreator());
		todoDto.setDtLastUpdate(new Date());

		return todoDto;
	}

	/**
	 * Method Name: getEventDataInputDto; Method Description: Create a new
	 * EventDataInputDto using saveApprovalStatusReq for DAM CCMN46D
	 * 
	 * @param saveApprovalStatusReq
	 * @return EventDataInputDto
	 */
	private EventDataInputDto getEventDataInputDto(SaveApprovalStatusReq saveApprovalStatusReq) {
		EventDataInputDto eventDataInputDto = new EventDataInputDto();
		eventDataInputDto.setIdEvent(saveApprovalStatusReq.getEventStatusDto().getIdEvent());
		eventDataInputDto.setIdPerson(saveApprovalStatusReq.getEventStatusDto().getIdPerson());
		eventDataInputDto.setIdStage(saveApprovalStatusReq.getEventStatusDto().getIdStage());
		eventDataInputDto.setDtDtEventOccurred(saveApprovalStatusReq.getEventStatusDto().getDtEventOccurred());
		eventDataInputDto.setEventLastUpdate(saveApprovalStatusReq.getEventStatusDto().getTsLastUpdate());
		eventDataInputDto.setCdEventStatus(saveApprovalStatusReq.getEventStatusDto().getCdEventStatus());
		eventDataInputDto.setCdTask(saveApprovalStatusReq.getEventStatusDto().getCdTask());
		eventDataInputDto.setCdEventType(saveApprovalStatusReq.getEventStatusDto().getCdEventType());
		eventDataInputDto.setTxtEventDescr(saveApprovalStatusReq.getEventStatusDto().getEventDescr());
		eventDataInputDto.setReqFunctionCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		return eventDataInputDto;
	}

	/**
	 * Method Name: getRejectApprovalDto Method Description: Create a new
	 * RejectApprovalDto using saveApprovalStatusReq for DAM CCMNI2D
	 * 
	 * @param saveApprovalStatusReq
	 * @return RejectApprovalDto
	 */
	private RejectApprovalDto getRejectApprovalDto(SaveApprovalStatusReq saveApprovalStatusReq) {
		RejectApprovalDto rejectApprovalDtoToSave = saveApprovalStatusReq.getRejectApprovalDto();

		/**
		 * Don't set the following parameters only if cdStage is NOT "INV" and
		 * Program
		 */
		/** name is NOT "CPS". */
		if (!(ServiceConstants.INV_Stage.equals(saveApprovalStatusReq.getCdStage()) && ServiceConstants.CPS_PROGRAM
				.equals(saveApprovalStatusReq.getAprvlStageProgDto().getCdStageProgram()))) {
			rejectApprovalDtoToSave.setCdOvrllDisptn(ServiceConstants.BLANK);
			rejectApprovalDtoToSave.setCdStageReasonClosed(ServiceConstants.BLANK);
			rejectApprovalDtoToSave.setCdJobClass(ServiceConstants.BLANK);
			rejectApprovalDtoToSave.setIndEmpConfirmedHrmis(ServiceConstants.BLANK);
		}

		// Calendar.getInstance().getTime() changed to new Date()
		rejectApprovalDtoToSave.setDtDeterminationRecorded(new Date());
		return rejectApprovalDtoToSave;
	}

	/**
	 * Method Name: getAprvlStatus Method Description: Create a new
	 * ApprovalUpdateDto using saveApprovalStatusReq for DAM CCMN88D
	 * 
	 * @param saveApprovalStatusReq
	 * @return ApprovalUpdateDto
	 */
	private ApprovalUpdateDto getAprvlStatus(SaveApprovalStatusReq saveApprovalStatusReq) {
		ApprovalUpdateDto approvalUpdateDto = new ApprovalUpdateDto();
		approvalUpdateDto.setUlIdApproval(saveApprovalStatusReq.getIdApproval());
		approvalUpdateDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		return approvalUpdateDto;
	}

	/**
	 * Method Name: getApprStatResHistyReq Method Description: Create a new
	 * ApprovalStatusResourceHistyReq using saveApprovalStatusReq for DAM
	 * CSEC38D
	 * 
	 * @param saveApprovalStatusReq
	 * @return ApprovalStatusResourceHistyReq
	 */
	private ApprovalStatusResourceHistyReq getApprStatResHistyReq(SaveApprovalStatusReq saveApprovalStatusReq) {
		ApprovalStatusResourceHistyReq approvalStatusResourceHistyReq = new ApprovalStatusResourceHistyReq();
		approvalStatusResourceHistyReq.setIdRsrcFaHomeEvent(saveApprovalStatusReq.getIdEvent());
		return approvalStatusResourceHistyReq;
	}

	/**
	 * Method Name: getupdateCapsResource Method Description: Create a new
	 * AprvlStatusUpdateCapsResourceReq using saveApprovalStatusReq for DAM
	 * CAUD52D
	 * 
	 * @param resourceDto
	 * @return AprvlStatusUpdateCapsResourceReq
	 */
	private AprvlStatusUpdateCapsResourceReq getupdateCapsResource(ResourceDto resourceDto) {
		AprvlStatusUpdateCapsResourceReq aprvlStatusUpdateCapsResourceReq = new AprvlStatusUpdateCapsResourceReq();
		aprvlStatusUpdateCapsResourceReq.setIdResource(resourceDto.getIdResource());
		aprvlStatusUpdateCapsResourceReq.setCdRsrcStatus(resourceDto.getCdRsrcStatus());
		aprvlStatusUpdateCapsResourceReq.setCdRsrcFaHomeStatus(resourceDto.getCdRsrcFaHomeStatus());
		aprvlStatusUpdateCapsResourceReq.setLastUpdate((Calendar.getInstance().getTime()).toString());
		aprvlStatusUpdateCapsResourceReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
		aprvlStatusUpdateCapsResourceReq.setPersonFullname(resourceDto.getNmRsrcLastUpdate());
		return aprvlStatusUpdateCapsResourceReq;
	}

	/**
	 * Method Name: getResourceHistoryCountReq Method Description: Create a new
	 * ResourceHistoryCountReq using saveApprovalStatusReq for DAM CMSC46D
	 * 
	 * @param saveApprovalStatusReq
	 * @return ResourceHistoryCountReq
	 */
	private ResourceHistoryCountReq getResourceHistoryCountReq(SaveApprovalStatusReq saveApprovalStatusReq) {
		ResourceHistoryCountReq resourceHistoryCountReq = new ResourceHistoryCountReq();
		resourceHistoryCountReq.setIdRsrcFaHomeStage(saveApprovalStatusReq.getIdStage());
		resourceHistoryCountReq.setCdRshsFaHomeStatus(ServiceConstants.HOME_APPRV_STATUS);
		return resourceHistoryCountReq;
	}

	/**
	 * Method Name: getSaveApprovalStatusReqForTodo Method Description: Create a
	 * new saveApprovalStatusReq for DAM CCMN43D
	 * 
	 * @param saveApprovalStatusReq
	 * @param
	 */
	private void getSaveApprovalStatusReqForTodo(SaveApprovalStatusReq saveApprovalStatusReq, String TODO_ALERT) {
		saveApprovalStatusReq.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		saveApprovalStatusReq.setCdTodoType(TODO_ALERT);
		saveApprovalStatusReq.setdTodoCreated(new Date());
		saveApprovalStatusReq.setDtTodoCompleted(new Date());
		saveApprovalStatusReq.setDtTodoDue(new Date());
		saveApprovalStatusReq.setDtTaskDue(ServiceConstants.NULL_DATE_TYPE);
	}

	/**
	 * Populate the user name in the DAM Input architecture header by copying
	 * the service input header message architecture header to the DAM Input
	 * architecture header.
	 * 
	 * @param saveApprovalStatusReq
	 * @return ServiceReqHeaderDto
	 */
	private ServiceReqHeaderDto cloneServiceReqHeaderDto(SaveApprovalStatusReq saveApprovalStatusReq) {
		ServiceReqHeaderDto outputServiceReqHeaderDto = new ServiceReqHeaderDto();
		outputServiceReqHeaderDto.setReqFuncCd(saveApprovalStatusReq.getReqFuncCd());
		outputServiceReqHeaderDto.setPageNbr(saveApprovalStatusReq.getPageNbr());
		outputServiceReqHeaderDto.setPageSizeNbr(saveApprovalStatusReq.getPageSizeNbr());
		outputServiceReqHeaderDto.setUserId(saveApprovalStatusReq.getUserId());
		outputServiceReqHeaderDto.setUserType(saveApprovalStatusReq.getUserType());
		outputServiceReqHeaderDto.setTransactionId(saveApprovalStatusReq.getTransactionId());
		outputServiceReqHeaderDto.setTotalRecCount(saveApprovalStatusReq.getTotalRecCount());
		outputServiceReqHeaderDto.setSysNbrReserved1(saveApprovalStatusReq.getSysNbrReserved1());
		outputServiceReqHeaderDto.setPerfInd(saveApprovalStatusReq.getPerfInd());
		outputServiceReqHeaderDto.setDataAcsInd(saveApprovalStatusReq.getDataAcsInd());
		return outputServiceReqHeaderDto;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: createFPRStage
	 * Method Description: Creates a new FPR Stage from the INV/A-R, while keeping the existing stage open
	 *
	 * @param idCase
	 * @param idFromStage
	 * @param idUser
	 * @param idApprover
	 */
	private void createFPRStage(Long idCase, Long idFromStage, Long idUser, Long idApproval, Long idApprover) {

		Long newStageId = 0l;

		// Creates new FPR Stage and returns the stage ID
		StageValueBeanDto stageCreationValBean = new StageValueBeanDto();
		stageCreationValBean.setIdStage(idFromStage);
		stageCreationValBean.setCdStage(ServiceConstants.CSTAGES_FPR);
		stageCreationValBean.setIdCreatedPerson(idUser);
		stageCreationValBean.setNmNewCase("");
		// set Stage Type to REG for all except INV with stage type as CRSR
		stageCreationValBean.setCdStageType(ServiceConstants.STAGE_TYPE_REG);
		newStageId = stageProgressionService.createNewStage(stageCreationValBean);

		// Retrieve the Router Person from Router Schedule to assign
		Long idFormReferrals =  formReferralsService.getFormReferralByApprovalId(idApproval);
		Long newPerson = idApprover;

		FormReferralsReq formReq = new FormReferralsReq();
		formReq.setIdFormReferral(idFormReferrals);
		FormReferralsRes formReferralsRes = formReferralsService.getFbssReferrals(formReq);

		if (!ObjectUtils.isEmpty(formReferralsRes) && !CollectionUtils.isEmpty(formReferralsRes.getFbssReferralsList())) {

			FbssReferralsDto fbssReferralsDto = formReferralsRes.getFbssReferralsList().get(0);

			if (!ObjectUtils.isEmpty(fbssReferralsDto) && "Y".equals(fbssReferralsDto.getAutoAssign())) {

				OnCallSearchReq onCallSearchReq = new OnCallSearchReq();
				onCallSearchReq.setCdOnCallProgram("FBS");
				onCallSearchReq.setCdOnCallCounty(Arrays.asList(fbssReferralsDto.getReferringCnty()));

				Person routerPerson = onCallService.getRouterPersonOnCall(onCallSearchReq);

				if (!ObjectUtils.isEmpty(routerPerson) && !ObjectUtils.isEmpty(routerPerson.getIdPerson())) {
					newPerson = routerPerson.getIdPerson();
				}
			}
		}

		List<String> approverRelatedRoles = new ArrayList<>();
		approverRelatedRoles.add(ServiceConstants.CROLEALL_PR);
		List<StagePersonValueDto> currentPRList = stageProgDao.selectStagePersonLink(newStageId.intValue(),
				approverRelatedRoles);
		StagePersonValueDto iSPValueBean = currentPRList.get(0);
		arStageProgDao.updateStagePersonLink(iSPValueBean.getIdPerson(), newPerson, newStageId, idCase,
				ServiceConstants.CROLEALL_PR);

		// Copies the open Service Auth to the new stage
		serviceAuthFormService.copyOpenServiceAuthToNewStage(idFromStage, newStageId, idUser,
				ServiceConstants.FPR_SERVICE_AUTH_TASK_CODE);

		// Flush the data, to avoid the exception on Database trigger
		approvalStatusDao.sessionFlush();
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildPlanParticipantEmailRes getParticipants(SaveApprovalStatusReq saveApprovalStatusReq) {

		ChildPlanParticipantEmailRes resp = new ChildPlanParticipantEmailRes();
		List<ChildPlanParticipantDto> childPlanParticipDtos = childPlanInsUpdDelDao
				.getChildParticipants(saveApprovalStatusReq.getIdEvent());

		if (CollectionUtils.isNotEmpty(childPlanParticipDtos)) {
			Optional<ChildPlanParticipantDto> participtOptional = childPlanParticipDtos.stream()
					.filter(o -> !"EML".equalsIgnoreCase(o.getCdDstrbutnMthd())).findFirst();
			List<ChildPlanParticipantDto> emailParticipants = childPlanParticipDtos.stream()
					.filter(o -> "EML".equalsIgnoreCase(o.getCdDstrbutnMthd())).collect(Collectors.toList());
			List<ChildPlanParticipantDto> validEmailParticipants = new ArrayList<>();
			boolean isEmailValid = true;
			for (ChildPlanParticipantDto participant : emailParticipants) {
				if (checkEmail(participant.getEmail())) {
					validEmailParticipants.add(participant);
				} else {
					isEmailValid = false;
				}
			}
			if (participtOptional.isPresent() || !isEmailValid) {
				TodoDto todoDto = new TodoDto();
				/// then Send the alerts
				ChildPlanEventDto childPlanDto = fetchPlanDao.getChildPlanEvent(saveApprovalStatusReq.getIdEvent());
				getSaveApprovalStatusReqForTodo(saveApprovalStatusReq, "A");
				saveApprovalStatusReq.setIdTodoPersAssigned(saveApprovalStatusReq.getIdPerson());
				saveApprovalStatusReq.setIdTodoPersWorker(saveApprovalStatusReq.getIdPerson());
				todoDto = initializeTodo(saveApprovalStatusReq);
				ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
				serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				todoDto.setDtTodoDue(new Date());
				todoDto.setCdTodoTask(ServiceConstants.CHILD_PLAN_TASK_CODE_SUB);
				StringBuilder todoDescStringBuilder = new StringBuilder("Distribute Child Plan for ");
				PersonDto personDto = personDao.getPersonById(childPlanDto.getIdPerson());
				//todoDescStringBuilder.append("( ");
				todoDescStringBuilder.append(personDto.getNmPersonFull());
				//todoDescStringBuilder.append(")");
				todoDto.setTodoDesc(todoDescStringBuilder.toString());
				/**
				 * DAM Name - CCMN43D This Method is used send Notification to
				 * primary case worker.
				 */
				todoDao.todoAUD(todoDto, serviceReqHeaderDto);

			}

			// collecting all participants
			List<Long> childPlanParticipList = childPlanParticipDtos.stream().map((o -> o.getIdChildPlanPart()))
					.collect(Collectors.toList());
			// the above list
			resp.setChildPlanParticipDtos(validEmailParticipants);
			resp.setChildPlanParticipIds(childPlanParticipList);
		}
		return resp;
	}

	public boolean checkEmail(String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		String regex = "^[A-Za-z0-9_]+([-+.'][A-Za-z0-9_]+)*@[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*\\.[A-Za-z0-9_]+([-.][A-Za-z0-9_]+)*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ChildPlanParticipantEmailRes updateDateCopyProvided(CommonHelperReq commHelperReq) {

		ChildPlanParticipantEmailRes resp = new ChildPlanParticipantEmailRes();
		resp.setUpdated(childPlanInsUpdDelDao.updateDateCopyProvided(commHelperReq.getPersonIds()));
		return resp;
	}


	public boolean isSameDay(LocalDate localDate1, LocalDate localDate2) {
		return localDate1.isEqual(localDate2);
	}

	private void createCaseFileManagement(Long stageId, Long caseId, Long personId) {

		EmployeeDto employeeDto = personInfoDao.getSelectEmployee(personId);
		Long officeId = employeeDto.getIdOffice();
		Long idUnit = employeeDto.getIdEmpUnit();

		CaseFileManagement retCasefile = caseFileManagementDao.findCaseFileManagementById(caseId);

		if (retCasefile != null ) {
			retCasefile.setCdCaseFileOfficeType(CodesConstant.CASE_FILE_MGMT_TYPE);
			retCasefile.setIdCaseFileCase(retCasefile.getIdCaseFileCase());

			if (!TypeConvUtil.isNullOrEmpty(officeId)) {
				Office office = (Office) sessionFactory.getCurrentSession().get(Office.class, officeId);
				if (TypeConvUtil.isNullOrEmpty(office)) {
					throw new DataNotFoundException(
							messageSource.getMessage("record.not.found.office", null, Locale.US));
				}
				retCasefile.setOffice(office);
			}

			if (!TypeConvUtil.isNullOrEmpty(idUnit)) {
				Unit unit = (Unit) sessionFactory.getCurrentSession().get(Unit.class, idUnit);
				if (TypeConvUtil.isNullOrEmpty(unit)) {
					throw new DataNotFoundException(
							messageSource.getMessage("record.not.found.unit", null, Locale.US));
				}
				retCasefile.setUnit(unit);
			}

			retCasefile.setDtLastUpdate(new Date());
			caseFileManagementDao.updateCaseFileManagement(retCasefile);
		} else {
			CaseFileManagement csfilemgmt = new CaseFileManagement();
			csfilemgmt.setIdCaseFileCase(caseId);
			csfilemgmt.setAddrCaseFileCity(null);
			csfilemgmt.setAddrCaseFileStLn1(null);
			csfilemgmt.setAddrCaseFileStLn2(null);
			csfilemgmt.setCdCaseFileOfficeType(CodesConstant.CASE_FILE_MGMT_TYPE);
			csfilemgmt.setDtCaseFileArchCompl(null);
			csfilemgmt.setDtCaseFileArchElig(null);

			if (!TypeConvUtil.isNullOrEmpty(officeId)) {
				Office office = (Office) sessionFactory.getCurrentSession().get(Office.class, officeId);
				if (TypeConvUtil.isNullOrEmpty(office)) {
					throw new DataNotFoundException(
							messageSource.getMessage("record.not.found.office", null, Locale.US));
				}
				csfilemgmt.setOffice(office);
			}

			if (!TypeConvUtil.isNullOrEmpty(idUnit)) {
				Unit unit = (Unit) sessionFactory.getCurrentSession().get(Unit.class, idUnit);
				if (TypeConvUtil.isNullOrEmpty(unit)) {
					throw new DataNotFoundException(
							messageSource.getMessage("record.not.found.unit", null, Locale.US));
				}
				csfilemgmt.setUnit(unit);
			}
			CapsCase capsCase = capsCaseDao.getCapsCaseEntityById(caseId);
			csfilemgmt.setCapsCase(capsCase);
			csfilemgmt.setIdCaseFileCase(caseId);
			csfilemgmt.setTxtAddSkpTrn(null);
			csfilemgmt.setDtLastUpdate(new Date());
			caseFileManagementDao.insertCaseFileManagement(csfilemgmt);
		}
	}

}