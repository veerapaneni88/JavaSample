/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 23, 2017- 11:03:49 AM
 *© 2017 Texas Department of Family and Protective Services
 */
package us.tx.state.dfps.service.approval.serviceimpl;

import static us.tx.state.dfps.service.common.CodesConstant.CCIACLOS_107;
import static us.tx.state.dfps.service.common.CodesConstant.CCONPAY_URT;
import static us.tx.state.dfps.service.common.CodesConstant.CCONPAY_VUR;
import static us.tx.state.dfps.service.common.CodesConstant.CCONPROG_CPS;
import static us.tx.state.dfps.service.common.CodesConstant.CCONUNIT_DA2;
import static us.tx.state.dfps.service.common.CodesConstant.CCONUNIT_ONE;
import static us.tx.state.dfps.service.common.CodesConstant.CCRUTC_83;
import static us.tx.state.dfps.service.common.CodesConstant.CFAHMSTA_070;
import static us.tx.state.dfps.service.common.CodesConstant.CFAHMSTA_080;
import static us.tx.state.dfps.service.common.CodesConstant.CKNELGST_APRV;
import static us.tx.state.dfps.service.common.CodesConstant.CKNELGST_CTOR;
import static us.tx.state.dfps.service.common.CodesConstant.CKNPYELG_CTOR;
import static us.tx.state.dfps.service.common.CodesConstant.CKNPYELG_ELIG;
import static us.tx.state.dfps.service.common.CodesConstant.CKNPYELG_NELG;
import static us.tx.state.dfps.service.common.CodesConstant.CPLLAFRM_DD;
import static us.tx.state.dfps.service.common.CodesConstant.CPLLAFRM_DQ;
import static us.tx.state.dfps.service.common.CodesConstant.CRELDATE_SEP_2018_IMPACT;
import static us.tx.state.dfps.service.common.CodesConstant.FORMAT_MMDDYYYY;
import static us.tx.state.dfps.service.common.CodesConstant.MAX_UNITS_68O;
import static us.tx.state.dfps.service.common.CodesConstant.MAX_UNITS_68P;
import static us.tx.state.dfps.service.common.CodesConstant.MIN_UNITS_68O;
import static us.tx.state.dfps.service.common.CodesConstant.NON_TANF_MONTHS_68P;
import static us.tx.state.dfps.service.common.CodesConstant.TANF_MONTHS_68O;
import static us.tx.state.dfps.service.common.CodesConstant.TANF_MONTHS_68P;
import static us.tx.state.dfps.service.common.CodesConstant.TASK_CLOSE_KIN_HOME;
import static us.tx.state.dfps.service.common.CodesConstant.TASK_MNTN_KIN_HOME;
import static us.tx.state.dfps.service.common.CodesConstant.TASK_MNTN_KIN_MONTHLY_EXTN;

import static us.tx.state.dfps.service.common.ServiceConstants.ALT_RESPONSE_STAGE_CLOSED;
import static us.tx.state.dfps.service.common.ServiceConstants.APS_INV_TO_SVC_TASK;
import static us.tx.state.dfps.service.common.ServiceConstants.AR_CLOSURE_SAFETY_ASSESSMENT_INDICATOR;
import static us.tx.state.dfps.service.common.ServiceConstants.AR_INITIAL_SAFETY_ASSESSMENT_INDICATOR;
import static us.tx.state.dfps.service.common.ServiceConstants.AR_SERVICE_AUTH_TASK_CODE;
import static us.tx.state.dfps.service.common.ServiceConstants.A_R_STAGE;
import static us.tx.state.dfps.service.common.ServiceConstants.BEGIN_INDEX;
import static us.tx.state.dfps.service.common.ServiceConstants.BOOLEAN_FALSE;
import static us.tx.state.dfps.service.common.ServiceConstants.CARRY_OVER_EVENT_TEXT;
import static us.tx.state.dfps.service.common.ServiceConstants.CASE_CLOSED;
import static us.tx.state.dfps.service.common.ServiceConstants.CASE_NAME_MAX_LENGTH;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_010;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_020;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_030;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_040;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_050;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_060;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_070;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_080;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_090;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_100;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_110;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_120;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_130;
import static us.tx.state.dfps.service.common.ServiceConstants.CCLOSAR_140;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_64;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_65;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_66;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_68;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_69;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_70;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_72;
import static us.tx.state.dfps.service.common.ServiceConstants.CCRSKRIC_97;
import static us.tx.state.dfps.service.common.ServiceConstants.CD_FILE_OFFICE_TYPE_PRS;
import static us.tx.state.dfps.service.common.ServiceConstants.CD_TASK_PAD_AA_RECERT;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVNTTYP_AUT;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVNTTYP_CAS;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVNTTYP_CCL;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVNTTYP_STG;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVTSTAT_APRV;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVTSTAT_COMP;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVTSTAT_NEW;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVTSTAT_PEND;
import static us.tx.state.dfps.service.common.ServiceConstants.CLEGSTAT_090;
import static us.tx.state.dfps.service.common.ServiceConstants.CLEGSTAT_100;
import static us.tx.state.dfps.service.common.ServiceConstants.CLEGSTAT_120;
import static us.tx.state.dfps.service.common.ServiceConstants.CLEGSTAT_150;
import static us.tx.state.dfps.service.common.ServiceConstants.CPGRMS_CCL;
import static us.tx.state.dfps.service.common.ServiceConstants.CPGRMS_CPS;
import static us.tx.state.dfps.service.common.ServiceConstants.CPGRMS_RCL;
import static us.tx.state.dfps.service.common.ServiceConstants.CPLMNTYP_090;
import static us.tx.state.dfps.service.common.ServiceConstants.CRECRETN_TYPE_ARR;
import static us.tx.state.dfps.service.common.ServiceConstants.CRELDATE;
import static us.tx.state.dfps.service.common.ServiceConstants.CRELVICT_RC;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_AP;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_AR;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_HP;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_NO;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_PR;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_SE;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_UK;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_VC;
import static us.tx.state.dfps.service.common.ServiceConstants.CROLEALL_VP;
import static us.tx.state.dfps.service.common.ServiceConstants.CRPTRINT_OV;
import static us.tx.state.dfps.service.common.ServiceConstants.CRSRCLOS_45;
import static us.tx.state.dfps.service.common.ServiceConstants.CRSRCLOS_55;
import static us.tx.state.dfps.service.common.ServiceConstants.CSDMDCSN_SAFEWITHPLAN;
import static us.tx.state.dfps.service.common.ServiceConstants.CSDMDCSN_UNSAFE;
import static us.tx.state.dfps.service.common.ServiceConstants.CSDMRLVL_HIGH;
import static us.tx.state.dfps.service.common.ServiceConstants.CSDMRLVL_VERYHIGH;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTAGES_ADO;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTAGES_AR;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTAGES_FPR;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTAGES_FRE;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTAGES_FSU;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTAGES_INV;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTAGES_SUB;
import static us.tx.state.dfps.service.common.ServiceConstants.CSTATE_TX;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68O;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68P;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68Q;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68R;
import static us.tx.state.dfps.service.common.ServiceConstants.EMPTY_STRING;
import static us.tx.state.dfps.service.common.ServiceConstants.END_INDEX;
import static us.tx.state.dfps.service.common.ServiceConstants.FALSEVAL;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_AR_CLOSURE_LONG;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_AR_CLOSURE_SHORT;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_FPR_CLOSURE_LONG;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_FPR_CLOSURE_SHORT;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_INV_CLOSURE_LONG;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_INV_CLOSURE_SHORT;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_SDM_RA_APPROVED_LONG;
import static us.tx.state.dfps.service.common.ServiceConstants.FBSS_SDM_RA_APPROVED_SHORT;
import static us.tx.state.dfps.service.common.ServiceConstants.FPR_SERVICE_AUTH_TASK_CODE;
import static us.tx.state.dfps.service.common.ServiceConstants.GENERIC_END_DATE;
import static us.tx.state.dfps.service.common.ServiceConstants.HOME_PEND_CLOSED_STATUS;
import static us.tx.state.dfps.service.common.ServiceConstants.ID_TODO_INFO_ADO_ADPT_SUB_90_DAYS_PRIOR;
import static us.tx.state.dfps.service.common.ServiceConstants.ID_TODO_INFO_PAD_ADPT_SUB;
import static us.tx.state.dfps.service.common.ServiceConstants.ID_TODO_INFO_PAD_ADPT_SUB_90_DAYS_PRIOR;
import static us.tx.state.dfps.service.common.ServiceConstants.INDICATOR_YES1;
import static us.tx.state.dfps.service.common.ServiceConstants.INVESTIGATION_CONCLUSION_TEXT;
import static us.tx.state.dfps.service.common.ServiceConstants.INV_CONCLUSION_TASK_CODE;
import static us.tx.state.dfps.service.common.ServiceConstants.INV_SERVICE_AUTH_TASK_CODE;
import static us.tx.state.dfps.service.common.ServiceConstants.KIN_HOME_APPRV_COMPLETE_MSG;
import static us.tx.state.dfps.service.common.ServiceConstants.KIN_HOME_STATUS_APPROVED;
import static us.tx.state.dfps.service.common.ServiceConstants.KIN_HOME_STATUS_COMP;
import static us.tx.state.dfps.service.common.ServiceConstants.MAX_VICTIM_AGE_CNCLSN;
import static us.tx.state.dfps.service.common.ServiceConstants.NO;
import static us.tx.state.dfps.service.common.ServiceConstants.PERSELIG_PRG_OPEN_B;
import static us.tx.state.dfps.service.common.ServiceConstants.PERSELIG_PRG_OPEN_C;
import static us.tx.state.dfps.service.common.ServiceConstants.PERSELIG_PRG_START_S;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_ADD;
import static us.tx.state.dfps.service.common.ServiceConstants.STAGE_PRIOR;
import static us.tx.state.dfps.service.common.ServiceConstants.SVC_CD_TASK_CONTACT_AOC;
import static us.tx.state.dfps.service.common.ServiceConstants.SVC_CD_TASK_CONTACT_SRR_APS;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_7490;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CCLINVCLOSEAPP;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CCLINVCLOSURE;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CODE_DAYCARE_APP_AR;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CODE_DAYCARE_APP_FPR;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CODE_DAYCARE_APP_FRE;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CODE_DAYCARE_APP_FSU;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CODE_DAYCARE_APP_INV;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CODE_DAYCARE_APP_SUB;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_CPSINVCLOSURE;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_RCLINVCLOSEAPP;
import static us.tx.state.dfps.service.common.ServiceConstants.TASK_RCLINVCLOSURE;
import static us.tx.state.dfps.service.common.ServiceConstants.TRUEVAL;
import static us.tx.state.dfps.service.common.ServiceConstants.TRUE_VALUE;
import static us.tx.state.dfps.service.common.ServiceConstants.UNABLE_TO_COMPLETE;
import static us.tx.state.dfps.service.common.ServiceConstants.Y;
import static us.tx.state.dfps.service.common.ServiceConstants.YES;
import static us.tx.state.dfps.service.common.ServiceConstants.ZERO_VAL;
import static us.tx.state.dfps.service.common.ServiceConstants.Zero;
import static us.tx.state.dfps.service.common.ServiceConstants.Zero_INT;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.domain.SvcAuthEventLink;
import us.tx.state.dfps.common.dto.BaseAddRiskAssmtValueDto;
import us.tx.state.dfps.common.dto.StageClosureValueDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.StringHelper;
import us.tx.state.dfps.service.SDM.dao.SdmReunificationAsmntDao;
import us.tx.state.dfps.service.admin.dao.AdminWorkerDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.StageRegionDao;
import us.tx.state.dfps.service.admin.dao.TodoCreateDao;
import us.tx.state.dfps.service.admin.dto.AdminWorkerInpDto;
import us.tx.state.dfps.service.admin.dto.AdminWorkerOutpDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EventStageDiDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusDetailDto;
import us.tx.state.dfps.service.admin.dto.PostDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.casepackage.dto.MergedIntakeARStageDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusDao;
import us.tx.state.dfps.service.approval.service.ApprovalStatusService;
import us.tx.state.dfps.service.arreport.dao.ArReportDao;
import us.tx.state.dfps.service.arstageprog.dao.ArStageProgDao;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.casepackage.dao.CaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseFileManagementDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.PcspListPlacmtDao;
import us.tx.state.dfps.service.casepackage.dao.RecordsRetentionDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dao.SituationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.casepackage.service.RecordsRetentionService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.IncomingDetailDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApprovalPersonReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.KinRejectApprovalReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.ApprovalPersonRes;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao;
import us.tx.state.dfps.service.investigation.dao.AllegtnDao;
import us.tx.state.dfps.service.kin.dto.*;
import us.tx.state.dfps.service.kinapproval.service.KinApprovalService;
import us.tx.state.dfps.service.kinhomeinfo.dao.KinHomeInfoDao;
import us.tx.state.dfps.service.kinhomeinfo.service.KinHomeInfoService;
import us.tx.state.dfps.service.kinpayment.dao.KinshipPaymentDao;
import us.tx.state.dfps.service.legal.dao.ToDoEventDao;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.ResourceServiceDao;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityValueDto;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;
import us.tx.state.dfps.service.servicauthform.service.ServiceAuthFormService;
import us.tx.state.dfps.service.stageprogression.service.StageProgressionService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WebsvcFormTransDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonPrintDto;
import us.tx.state.dfps.service.workload.dto.ApproveApprovalDto;
import us.tx.state.dfps.service.workload.dto.ApproversDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.SVCAuthDetailDto;
import us.tx.state.dfps.service.workload.dto.SecondaryApprovalDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;
import us.tx.state.dfps.xmlstructs.inputstructs.SynchronizationServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 23, 2017- 11:03:49 AM © 2017 Texas Department of
 * Family and Protective Services
 * 05/11/2020  ochumd     artf149010 - Removal of Old Business Rules for CS
 */
@Service
@Transactional
public class ApprovalStatusServiceImpl implements ApprovalStatusService {

	public static final long KINSHIP_AUTOMATED_SYSTEM_ID = 999999985L;
	public static final String CSVATYPE_INI = "INI";
	public static final String CSVATYPE_ONT = "ONT";
	public static final String CINVUTYP_DA2 = "DA2";

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Autowired
	ApprovalStatusDao approvalStatusDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	AllegtnDao allegationDao;

	@Autowired
	CaseSummaryDao caseSummaryDao;

	@Autowired
	ToDoEventDao toDoEventDao;

	@Autowired
	TodoCreateDao todoCreateDao;

	@Autowired
	AdminWorkerDao adminWorkerDao;

	@Autowired
	ArReportDao arReportDao;

	@Autowired
	ArStageProgDao arStageProgDao;

	@Autowired
	PostEventService postEventService;

	@Autowired
	StageProgressionService stageProgressionService;

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	SvcAuthDetailDao svcAuthDetailDao;

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	@Autowired
	IncomingDetailDao incomingDetailDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	SituationDao situationDao;

	@Autowired
	CaseDao caseDao;

	@Autowired
	TodoDao todoDao;

	@Autowired
	LegalStatusDao legalStatusDao;

	@Autowired
	PlacementDao placementDao;

	@Autowired
	SdmReunificationAsmntDao sdmReunificationDao;

	@Autowired
	RecordsRetentionService recordsRetentionService;

	@Autowired
	AlertService alertService;

	@Autowired
	StageRegionDao stageRegionDao;

	@Autowired
	ServiceAuthFormService serviceAuthFormService;

	@Autowired
	PcspListPlacmtDao pcspListPlacmtDao;

	@Autowired
	ArHelperDao arHelperDao;

	@Autowired
	CodesDao codesDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	KinHomeInfoDao kinHomeInfoDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	KinshipPaymentDao kinshipPaymentDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	ResourceServiceDao resourceServiceDao;

	@Autowired
	CaseFileManagementDao caseFileManagementDao;

	@Autowired
	StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	RecordsRetentionDao recordsRetentionDao;

	@Autowired
	KinHomeInfoService kinHomeInfoService;

	@Autowired
	KinApprovalService kinApprovalService;
	
	@Autowired
	WebsvcFormTransDao websvcFormTransDao;

	private static final Logger log = Logger.getLogger(ApprovalStatusServiceImpl.class);

	public static final String TODO_ALERT = "A";

	/**
	 * Method Name: saveApproval Method Description:This method is used to save
	 * the assign staff to print closure notifications in approval status page
	 *
	 * @param approvalPersonReq
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ApprovalPersonRes saveApproval(ApprovalPersonReq approvalPersonReq) {
		ApprovalPersonRes approvalPersonRes = new ApprovalPersonRes();
		log.info("TransactionId :" + approvalPersonReq.getTransactionId());
		ApprovalPersonPrintDto approvalPersonDto = approvalStatusDao
				.saveApproval(approvalPersonReq.getApprovalPersonDto());
		if (approvalPersonDto.getIdEventPersLink() == null)
			approvalPersonRes.setStatusMsg("Record all ready  exist");
		approvalPersonRes.setApprovalPersonDto(approvalPersonDto);
		return approvalPersonRes;
	}

	/**
	 * Method Name: getApproval Method Description:This method is to retrieve
	 * the name of assign staff to print in approval status page
	 *
	 * @param approvalPersonReq
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ApprovalPersonRes getApproval(ApprovalPersonReq approvalPersonReq) {
		ApprovalPersonRes approvalPersonRes = new ApprovalPersonRes();
		ApprovalPersonPrintDto approvalPersonDto = approvalStatusDao
				.getApproval(approvalPersonReq.getApprovalPersonDto());
		approvalPersonRes.setApprovalPersonDto(approvalPersonDto);
		return approvalPersonRes;
	}

	/**
	 * The Method returns if the Second level Approval is required for CPS
	 * Investigation Conclusion Approval.
	 *
	 * @param secondaryApprovalDto
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean isSecondLevelApproverRequiredForCPSINV(SecondaryApprovalDto secondaryApprovalDto) {
		// Long idCase = new Long(secondaryApprovalDto.getIdCase());
		Long idStage = new Long(secondaryApprovalDto.getIdStage());

		boolean finalResponse = false;

		//Secondary approval required for new Admin Code -INV over 60 days
		if(CCIACLOS_107.equalsIgnoreCase(secondaryApprovalDto.getCdStageReasonClose())){
			finalResponse=true;
			return finalResponse;
		}

		// Determine if there is a recommendation at Stage closure that an FPR
		// or CVS stage be open.
		boolean openStageBeyondInv = false;
		if (CRSRCLOS_45.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CRSRCLOS_55.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_64.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_65.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_66.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_68.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_69.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_70.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_72.equals(secondaryApprovalDto.getCdStageReasonClose())
				|| CCRSKRIC_97.equals(secondaryApprovalDto.getCdStageReasonClose())) {
			// there is a recommendation for CVS or FBSS stage, so no need for
			// secondary approval
			openStageBeyondInv = true;
		}

		// Check open stages in the case. If there is a open stage in ADO, FRE,
		// FPR, FSU, SUB stage
		// then case should not be sent to secondary approval
		// Below check is removed as open stage beyond investigation is being
		// checked as part of new rules for CSS.
		/*
		 * if (!openStageBeyondInv) { List<StageDto> openStages =
		 * stageDao.getOpenStageByIdCase(idCase); openStageBeyondInv =
		 * openStages.stream() .anyMatch(stage -> (stage != null &&
		 * (stage.getCdStage().equals(CSTAGES_ADO) ||
		 * stage.getCdStage().equals(CSTAGES_FRE) ||
		 * stage.getCdStage().equals(CSTAGES_FPR) ||
		 * stage.getCdStage().equals(CSTAGES_FSU) ||
		 * stage.getCdStage().equals(CSTAGES_SUB)))); }
		 */

		if (!openStageBeyondInv) {
			// Find out the Victims for the Case and check if any victim is
			// under aged at the time of in take.
			List<Person> victims = allegationDao.getVictimsByIdStage(idStage);
			// Check if the Victim list is Empty, i.e. None of the below
			// condition will satisfy. So we can directly return considering the
			// CSS Review is not required.
			if (!ObjectUtils.isEmpty(victims)) {
				// Check for any open stage beyond investigation where the VC is
				// involved.
				List<Date> stageChildAgeList = new ArrayList<>();
				List<Long> victimIds = new ArrayList<>();
				victims.stream().forEach(victim -> {
					stageChildAgeList.add(victim.getDtPersonBirth());
					victimIds.add(victim.getIdPerson());
				});

				// Check If Overall Disposition for the Investigation is UTC
				boolean overallDispositionIsUTC = false;
				//artf149010 Removal CCRUTC_81 as part of removing Old Business Rules for CSS
				if (!openStageBeyondInv && (CCRUTC_83.equals(secondaryApprovalDto.getCdStageReasonClose()))) {
					overallDispositionIsUTC = true;
				}
				if (!overallDispositionIsUTC) {
					// Determine if the Victim Child is Under aged
					boolean hasUnderAgeChild = false;
					// SelectStageDto priorStage =
					// caseSummaryDao.getStage(idStage,
					// STAGE_PRIOR);
					Date dtIntakeStage = allegationDao.fetchDtIntakeForIdStage(idStage);
					// approvalStatusDao.getStageChildAgeList(idCase, idStage);
					hasUnderAgeChild = stageChildAgeList.stream().filter(Objects::nonNull).anyMatch(dob -> DateUtils
							.getPersonListAge(dob, dtIntakeStage) < SecondaryApprovalDto.MAX_VICTIM_AGE);

					if (!openStageBeyondInv && hasUnderAgeChild) {

						// Determine the final Risk Level for Stage
						boolean anyHighRiskAssesment = false;
						boolean safetyDecisionSwpUnsafe = false;
						List<String> finalRiskLevelList = approvalStatusDao.getFinalRiskLevelForStage(idStage);
						anyHighRiskAssesment = finalRiskLevelList.stream()
								.anyMatch(risk -> CSDMRLVL_HIGH.equalsIgnoreCase(risk)
										|| CSDMRLVL_VERYHIGH.equalsIgnoreCase(risk));
						if (!anyHighRiskAssesment) {
							String latestSafetyDecision = approvalStatusDao.getLatestSafetyDecision(idStage);
							safetyDecisionSwpUnsafe = CSDMDCSN_UNSAFE
									.equalsIgnoreCase(latestSafetyDecision)
									|| CSDMDCSN_SAFEWITHPLAN.equalsIgnoreCase(latestSafetyDecision);
						}

						if (anyHighRiskAssesment || safetyDecisionSwpUnsafe) {
							finalResponse = true;
						}
					} else {
						finalResponse = false;
					}
				} else {
					finalResponse = true;
				}
			}

		}
		return finalResponse;
	}

	/**
	 * Method Name: getDayCareApproval Method Description:This method determine
	 * where it is Day care Request Service Authorization Approval or Regular
	 * Approval
	 *
	 * @param idEvent
	 * @return Boolean
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean getDayCareApproval(Long idEvent) {
		return approvalStatusDao.getDayCareApproval(idEvent);
	}

	/**
	 *
	 * Method Name: getSSCCReferalForIdPersonDC Method Description: Get the
	 * Active Placement Referral for Day care Request
	 *
	 * @param idEvent
	 * @return Long
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getSSCCReferalForIdPersonDC(Long idEvent) {

		Long idSsccReferral = 0l;
		SsccReferral ssccReferral = new SsccReferral();
		ssccReferral = approvalStatusDao.getSSCCReferalForIdPersonDC(idEvent);
		if (!ObjectUtils.isEmpty(ssccReferral)) {
			idSsccReferral = ssccReferral.getIdSSCCReferral();
		}
		return idSsccReferral;
	}

	/**
	 * Method Name: updateSSCCReferral Method Description:This method sets
	 * SSCC_REFERRAL table with IND_LINKED_SVC_AUTH_DATA = 'Y',
	 * DT_LINKED_SVC_AUTH_DATA = SYSDATE for the given SSCC Referral Id. and
	 * sets SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'.
	 *
	 * @param idSSCCReferral
	 * @return Updated SsccReferral
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public SsccReferral updateSSCCReferral(Long idSSCCReferral) {
		return approvalStatusDao.updateSSCCReferral(idSSCCReferral);
	}

	/**
	 * Method Name: getSSCCReferralForIdPersonPALorSUB Method Description:This
	 * method gets SSCC Referral id for the person id in PAL or SUB stage.
	 *
	 * @param idEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long getSSCCReferralForIdPersonPALorSUB(Long idEvent) {
		return approvalStatusDao.getSSCCReferralForIdPersonPALorSUB(idEvent);
	}

	/**
	 * Method Name: updateSSCCList Method Description: Updates a row into the
	 * SSCC_LIST table
	 *
	 * @param idSSCCRererral
	 * @return SSCCListDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public SSCCListDto updateSSCCList(Long idSSCCRererral) {
		return approvalStatusDao.updateSSCCList(idSSCCRererral);
	}

	/**
	 * Method Name: getSSCCReferralFamilyForIdPerson Method Description: This
	 * method gets SSCC Referral Family id for the person id in NOT (SUB or PAL)
	 * stage.
	 *
	 * @param idEvent
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long getSSCCReferralFamilyForIdPerson(Long idEvent) {
		return approvalStatusDao.getSSCCReferralFamilyForIdPerson(idEvent);
	}

	/**
	 * Method Name: getVendorId MethodDescription: This method gets the Vendor
	 * Id for the given event id
	 *
	 * @param idEvent
	 * @return String
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public String getVendorId(Integer idEvent) {

		String vendorId = null;
		vendorId = approvalStatusDao.getVendorId(idEvent);
		return vendorId;
	}

	/**
	 * Method Name: isVendorIdExistsBatchParameters Method Description:This
	 * method checks whether vendor id exists or not in BATCH_SSCC_PARAMETERS
	 * table
	 *
	 * @param vid
	 * @return Boolean
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean isVendorIdExistsBatchParameters(String vid) {
		Boolean vidExists = FALSEVAL;
		vidExists = approvalStatusDao.isVendorIdExistsBatchParameters(vid);
		return vidExists;
	}

	/**
	 * MethodName: updateSSCCReferralFamily MethodDescription:This method sets
	 * SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'
	 *
	 * EJB Name : ServiceAuthBean.java
	 *
	 * @param idEvent
	 * @return long
	 *
	 */

	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long updateSSCCReferralFamily(long idEvent) {

		approvalStatusDao.updateSSCCReferralFamilyDao(idEvent);

		return idEvent;
	}

	/**
	 * Method Name: fetchTodoDtlAdptAssistNxtRecrtRevw Method Description:Fetch
	 * the Todo Details of the next recert open To do task for the stage.
	 *
	 * @param idStage
	 * @param cdEventTaskCode
	 * @return List<TodoDto>
	 *
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public List<TodoDto> fetchTodoDtlAdptAssistNxtRecrtRevw(Long idStage, String cdEventTaskCode) {

		List<TodoDto> todoDtoList = new ArrayList<TodoDto>();
		todoDtoList = approvalStatusDao.fetchOpenTodoForStage(idStage);

		for (TodoDto todo : todoDtoList) {

			if (!TypeConvUtil.isNullOrEmpty(todo) && !TypeConvUtil.isNullOrEmpty(todo.getIdTodo())
					&& !TypeConvUtil.isNullOrEmpty(todo.getCdTodoTask())
					&& cdEventTaskCode.equals(todo.getCdTodoTask())) {
				todoDtoList.add(todo);
				break;
			}
		}
		return todoDtoList;
	}

	/**
	 * artf112312 Placement changed and Home Info approved on same day produces incorrect SAD
	 * To term the service auth if the placement is closed
	 * @param homeInfoDto
	 */
	public void termServiceAuth(KinHomeInfoDto homeInfoDto) {
		if (!CollectionUtils.isEmpty(homeInfoDto.getKinChildVBList())) {
			homeInfoDto.getKinChildVBList().stream()
					.forEach(kinChildDto -> serviceAuthorizationDao.termOtherServiceAuthDetails(
					homeInfoDto, kinChildDto));
		}
	}
	/**
	 * Method Name: approveKinHome Method Description: This method is used to
	 * process kinship approval.
	 *
	 * @param approversDto
	 * @param eventDto
	 * @param appEventDto
	 * @param todoDto
	 * @return Integer
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long approveKinHome(ApproversDto approversDto, EventDto eventDto,
								  EventDto appEventDto, TodoDto todoDto, KinHomeInfoDto inputKinHomeInfoDto) {
		Integer updatedResult = 0;

		KinHomeInfoDto kinHomeInfoDto = kinHomeInfoService.getKinHomeInfo(inputKinHomeInfoDto.getIdHomeStage());
		kinHomeInfoDto.setUserId(inputKinHomeInfoDto.getUserId());
		kinHomeInfoDto.setPersonFullName(inputKinHomeInfoDto.getPersonFullName());

		updatedResult = processKinHomeApproval(approversDto, eventDto, appEventDto, todoDto, kinHomeInfoDto);

		if  (((TASK_MNTN_KIN_HOME.equals(todoDto.getCdTodoTask())) ||
				(TASK_CLOSE_KIN_HOME.equals(todoDto.getCdTodoTask()))) ) {
			kinApprovalService.doKinshipResourceApproval(kinHomeInfoDto);
		}

		return Long.valueOf(String.valueOf(updatedResult));
	}

	private Integer processKinHomeApproval(ApproversDto approversDto, EventDto eventDto,
								  EventDto appEventDto, TodoDto todoDto, KinHomeInfoDto kinHomeInfoDto) {
		Integer updatedResult = 0;
		boolean showNewPage = true;
		Long globalEventId = eventDto.getIdEvent();
		Long appEventId = appEventDto.getIdEvent();
		Long idCase = eventDto.getIdCase();

		StageValueBeanDto stageDto = stageDao.retrieveStageInfo(eventDto.getIdStage());

		boolean isHB4KinAssessment = isHB4KinAssessment(stageDto.getDtStageStart());

		updateApprovers(approversDto);
		updateEventStatus(globalEventId, KIN_HOME_STATUS_APPROVED);
		updateEventStatus(appEventId, KIN_HOME_STATUS_COMP);
		deleteTodoKin(todoDto);

		String caseName = getNmCaseKin(idCase);
		StringBuilder notificationMsg = new StringBuilder(KIN_HOME_APPRV_COMPLETE_MSG + "(");

		if (caseName != null)
			notificationMsg.append(caseName.length() > CASE_NAME_MAX_LENGTH
					? caseName.substring(BEGIN_INDEX.intValue(), END_INDEX.intValue())
					: caseName);
		notificationMsg.append(", ");
		notificationMsg.append(idCase);
		notificationMsg.append(")");
		todoDto.setTodoDesc(notificationMsg.toString());
		updatedResult = Integer.valueOf(String.valueOf(insertTodoKin(todoDto)));

		//45217 - HB4 Long Term Solution - to check if this has to go thorugh new hb4 logic
		if ((CFAHMSTA_080.equalsIgnoreCase(kinHomeInfoDto.getHomeStatus()) ||
				CFAHMSTA_070.equalsIgnoreCase(kinHomeInfoDto.getHomeStatus()))
				&& !isHB4KinAssessment) {
			showNewPage = false;
		} else {
			showNewPage = true;
		}

		// if this has to go through new hb4 logic, determine payment eligibility
		if (showNewPage && !TASK_MNTN_KIN_MONTHLY_EXTN.equalsIgnoreCase(todoDto.getCdTodoTask())) {
			determinePaymentEligibility(kinHomeInfoDto);
			// save the payment information
			savePaymentInfo(kinHomeInfoDto);
			// save the kin payment history
			saveOrUpdateKinPaymentHistory(kinHomeInfoDto);
			termServiceAuth(kinHomeInfoDto);
			// if the caregiver payment eligible, then create contact and
			// related
			if (CKNPYELG_ELIG.equalsIgnoreCase(kinHomeInfoDto.getKinCaregiverPaymentEligStatusCode())
					|| CKNPYELG_CTOR.equalsIgnoreCase(kinHomeInfoDto.getKinCaregiverPaymentEligStatusCode())) {
				Long contractId = automateContractRelated(kinHomeInfoDto);

				if (!HOME_PEND_CLOSED_STATUS.equals(kinHomeInfoDto.getHomeStatus())) {
					saveServiceAuth(kinHomeInfoDto, contractId, stageDto.getIdStage());
				}
				updateServiceAuth(kinHomeInfoDto);
			} else {
				updateServiceAuth(kinHomeInfoDto);
			}
		}

		return updatedResult;
	}

	public Integer rejectKinHome(KinRejectApprovalReq kinRejectApprovalReq) {
		int updatedResult = 0;

		if  (TASK_MNTN_KIN_HOME.equals(kinRejectApprovalReq.getCdTask())) {

			StageValueBeanDto stageDto = stageDao.retrieveStageInfo(kinRejectApprovalReq.getIdHomeStage());
			KinHomeInfoDto kinHomeInfoDto = kinHomeInfoService.getKinHomeInfo(kinRejectApprovalReq.getIdHomeStage());

			boolean isHB4KinAssessment = isHB4KinAssessment(stageDto.getDtStageStart());

			if (!((CFAHMSTA_080.equalsIgnoreCase(kinHomeInfoDto.getHomeStatus()) ||
					CFAHMSTA_070.equalsIgnoreCase(kinHomeInfoDto.getHomeStatus()))
					&& !isHB4KinAssessment)) {
				updatedResult = kinHomeInfoService.rejectKinHome(kinHomeInfoDto);
			}
		}

		return updatedResult;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long approveKinMonthlyPayment(KinHomeInfoDto kinHomeInfoDto, EventDto eventDto,
										 EventDto appEventDto, TodoDto todoDto) {
		Long updatedResult = 0L;
		Long caseId = 0L;
		Long idCaseFromSAEvent = 0L;
		Map<Long, Long> map = new HashMap<>();
		Long serviceAuthId = 0L;
		Long serviceAuthDltId = 0L;
		int lineItem = 0;
		KinMonthlyExtPaymentDto kinMonthlyExtPayment = new KinMonthlyExtPaymentDto();

		Long globalEventId = eventDto.getIdEvent();
		List<KinMonthlyExtPaymentDto> kinMonthlyExtPayments = kinshipPaymentDao.getMonthlyExtensionSql(globalEventId);
		Long resourceId = capsResourceDao.getCapsResourceByStageId(kinHomeInfoDto.getIdHomeStage());
		Long contractId = kinHomeInfoDao.getContractId(resourceId);
		if (!CollectionUtils.isEmpty(kinMonthlyExtPayments) && kinMonthlyExtPayments.size() > 0) {
			kinMonthlyExtPayment = kinMonthlyExtPayments.get(0);
		}
		Long childId = kinMonthlyExtPayment.getChildId();
		List<SVCAuthDetailDto> svcAuthDetails = serviceAuthorizationDao.getServiceAuthList(resourceId, childId);
		serviceAuthId = !CollectionUtils.isEmpty(svcAuthDetails) ? svcAuthDetails.get(0).getIdSvcAuth() : serviceAuthId;
		if (serviceAuthDltId == 0L) {
			caseId = kinHomeInfoDao.getCaseIdForHomeInfo(childId);
			Set<Long> serviceAuthSet = serviceAuthorizationDao.getSAHeaderIdSet(resourceId, contractId, caseId);

			Iterator<Long> it = serviceAuthSet.iterator();
			while (it.hasNext()) {
				Long sadId = Long.parseLong(it.next() + "");
				idCaseFromSAEvent = kinHomeInfoDao.getSAEventLinkCaseId(sadId);
				map.put(idCaseFromSAEvent, sadId);
			}
			if (!CollectionUtils.isEmpty(map) && map.containsKey(idCaseFromSAEvent)) {
				serviceAuthId = map.get(idCaseFromSAEvent);
				if (!kinHomeInfoDao.isStageOpen(serviceAuthId)) {
					serviceAuthId = 0L;
				}
			}
		}
		int numOfUnitsRemaining = (int) Math.round(DateHelper.daysDifference(kinMonthlyExtPayment.getEndDate(), kinMonthlyExtPayment.getStartDate())) + 1;
		Double ratefor68P = kinHomeInfoDao.getFosterCareRate(CSVCCODE_68P, kinMonthlyExtPayment.getStartDate());

		if(!ObjectUtils.isEmpty(contractId) && contractId != 0L) {
			 lineItem = kinHomeInfoDao.getContractLineItem(contractId, CSVCCODE_68P, kinMonthlyExtPayment.getStartDate());
		}
		if (serviceAuthId != 0L && contractId != 0L && lineItem != 0L) {
			serviceAuthDltId = serviceAuthorizationDao.insertMonthlyExtensionSADetail(kinMonthlyExtPayment, serviceAuthId, ratefor68P,
					numOfUnitsRemaining, lineItem);
		}
		if(serviceAuthDltId != 0L) {
			kinshipPaymentDao.updateMonthlyPayment(serviceAuthDltId, globalEventId);
		}
		updateEventStatus(globalEventId, KIN_HOME_STATUS_APPROVED);

		Long appEventId = appEventDto.getIdEvent();
		updateEventStatus(appEventId, KIN_HOME_STATUS_COMP);
		deleteTodoKin(todoDto);
		return updatedResult;
	}

	/**
	 * Method Name: updateApprovers Method Description: This method is used to
	 * update approvers details.
	 *
	 * @param approversDto
	 * @return Long
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateApprovers(ApproversDto approversDto) {

		Long updatedResult = 0L;
		updatedResult = approvalStatusDao.updateApproversSql(approversDto);
		return updatedResult;
	}

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update event status details.
	 *
	 * @param eventId
	 * @param eventStatus
	 * @return Long
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateEventStatus(Long eventId, String eventStatus) {
		Long updatedResult = 0L;

		updatedResult = approvalStatusDao.updateEventStatus(eventId, eventStatus);
		return updatedResult;
	}

	/**
	 * Method Name: deleteTodoKin Method Description: This method is used to
	 * delete to Do details.
	 *
	 * @param todoDto
	 * @return Long
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteTodoKin(TodoDto todoDto) {
		Long updatedResult = 0L;
		updatedResult = approvalStatusDao.deleteTodo(todoDto);
		return updatedResult;
	}

	/**
	 * Method Name: getNmCaseKin Method Description: This method is used to get
	 * case kin details.
	 *
	 * @param idCase
	 * @return String
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getNmCaseKin(Long idCase) {
		String nmCaseStr = new String();
		nmCaseStr = approvalStatusDao.getNmCase(idCase);
		return nmCaseStr;
	}

	/**
	 * Method Name: insertTodoKin Method Description: This method is used to
	 * insert to do details.
	 *
	 * @param todoDto
	 * @return Long
	 *
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long insertTodoKin(TodoDto todoDto) {
		Long updatedResult = 0L;
		updatedResult = approvalStatusDao.insertTodo(todoDto);
		return updatedResult;
	}

	/**
	 * Method Name:createTodoNxtRecertReview Method description: Creates a next
	 * recert To do in the newly opened PAD stage succeeding the ADO stage.
	 *
	 * @param adptAssistRecertTodoDetail
	 * @param idStage
	 * @return Long
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public Long createTodoNxtRecertReview(List<TodoDto> adptAssistRecertTodoDetail, Long idStage) {

		Long idStagePad = ZERO_VAL;

		Long idTodoInfo = ZERO_VAL;

		EventStageDiDto eventStageDiDto = new EventStageDiDto();

		for (TodoDto todoRow : adptAssistRecertTodoDetail) {

			if (!TypeConvUtil.isNullOrEmpty(todoRow.getIdTodo())
					&& !TypeConvUtil.isNullOrEmpty(todoRow.getCdTodoTask())) {

				if (!TypeConvUtil.isNullOrEmpty(todoRow.getCdTodoType())) {
					eventStageDiDto.setSzCdTodoType((String) todoRow.getCdTodoType());
				}
				eventStageDiDto.setSzCdTodoTask(CD_TASK_PAD_AA_RECERT);
				eventStageDiDto.setDtDtTodoCreated(new Date());
				eventStageDiDto.setDtDtTodoCompleted(new Date());

				if (!TypeConvUtil.isNullOrEmpty(todoRow.getDtTodoDue())) {
					eventStageDiDto.setDtDtTodoDue(DateUtils.toCastorDate((Date) todoRow.getDtTodoDue()));
				} else {
					eventStageDiDto.setDtDtTodoDue(new Date());
				}
				if (!TypeConvUtil.isNullOrEmpty(todoRow.getDtTodoTaskDue())) {
					eventStageDiDto.setDtDtTaskDue(DateUtils.toCastorDate((Date) todoRow.getDtTodoTaskDue()));
				} else {
					eventStageDiDto.setDtDtTaskDue(new Date());
				}
				if (!TypeConvUtil.isNullOrEmpty(todoRow.getIdTodoPersWorker())) {
					eventStageDiDto.setUlIdTodoPersWorker((todoRow.getIdTodoPersWorker()));
				} else {
					eventStageDiDto.setUlIdTodoPersWorker(ZERO_VAL);
				}
				if (!TypeConvUtil.isNullOrEmpty(todoRow.getTodoDesc())) {
					eventStageDiDto.setSzTxtTodoDesc((String) todoRow.getTodoDesc());
				} else {
					eventStageDiDto.setSzTxtTodoDesc("Adoption Assistance Recertification is Due ");
				}
				if (!TypeConvUtil.isNullOrEmpty(todoRow.getTodoLongDesc())) {
					eventStageDiDto.setTxtTodoLongDesc(todoRow.getTodoLongDesc());
				} else {
					eventStageDiDto.setTxtTodoLongDesc(EMPTY_STRING);
				}
				if (!TypeConvUtil.isNullOrEmpty(todoRow.getIdTodoInfo())) {
					idTodoInfo = (todoRow.getIdTodoInfo());
				}
				if (!TypeConvUtil.isNullOrEmpty(todoRow.getIdTodoPersCreator())) {
					eventStageDiDto.setUlIdTodoPersCreator(todoRow.getIdTodoPersCreator());
				}
				if (!TypeConvUtil.isNullOrEmpty(todoRow.getIdTodoEvent())) {
					eventStageDiDto.setUlIdEvent(todoRow.getIdTodoEvent());
				}
			}
		}

		if (isValid(eventStageDiDto.getSzCdTodoTask())) {
			idStagePad = approvalStatusDao.fetchPADStageIdForADO(idStage);
			if (!TypeConvUtil.isNullOrEmpty(idStagePad)) {
				eventStageDiDto.setUlIdCase(stageDao.retrieveStageInfo(idStagePad).getIdCase());
				eventStageDiDto.setUlIdTodoPersAssigned(getPrimaryWorkerId(idStagePad));
				eventStageDiDto.setUlIdStage(idStagePad);
				if (idTodoInfo == ID_TODO_INFO_ADO_ADPT_SUB_90_DAYS_PRIOR) {
					eventStageDiDto.setUlIdTodoInfo(ID_TODO_INFO_PAD_ADPT_SUB_90_DAYS_PRIOR);
				} else {
					eventStageDiDto.setUlIdTodoInfo(ID_TODO_INFO_PAD_ADPT_SUB);
				}
				ServiceInputDto serviceInputDto = new ServiceInputDto();
				serviceInputDto.setCreqFuncCd(REQ_FUNC_CD_ADD);
				eventStageDiDto.setServiceInputDto(serviceInputDto);
				todoCreateDao.audToDoImpact(eventStageDiDto);
			}
		}
		return idStagePad;
	}

	/**
	 * Method Name: isValid Method Description:Checks to see if a given string
	 * is valid. This includes checking that the string is not null or empty.
	 *
	 * @param value
	 * @return
	 */
	private Boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > Zero_INT);
	}

	/**
	 * Method Name: getPrimaryWorkerId Method Description: Calls the DAO to
	 * fetch Primary Worker
	 *
	 * @param idStage
	 * @return Long
	 *
	 */
	private Long getPrimaryWorkerId(Long idStage) {
		AdminWorkerInpDto adminWorkerInpDto = new AdminWorkerInpDto();
		adminWorkerInpDto.setIdStage(idStage);
		adminWorkerInpDto.setCdStagePersRole(CROLEALL_PR);

		AdminWorkerOutpDto adminWorkerOutpDto = adminWorkerDao.getPersonInRole(adminWorkerInpDto);

		return adminWorkerOutpDto.getIdTodoPersAssigned();
	}

	/**
	 *
	 * Method Name: processARConclusionStage Method Description: This method
	 * will look at the closure reason and decide if it needs to progress the
	 * case to INV or to close the AR Stage or progress to FPR
	 *
	 * @param idCase
	 * @param idFromStage
	 * @param idApprover
	 * @param idApproval
	 * @return String
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public String processARConclusionStage(int idCase, int idFromStage, int idApprover, Long idApproval) {

		String overallDisposition = EMPTY_STRING;
		String overallDispositionDesc = EMPTY_STRING;
		Long newStageID = ZERO_VAL;

		try {
			Long idStageFrom = Long.valueOf(idFromStage);
			// 01. First get the Conclusion table information
			ArInvCnclsnDto aRConclusionValueDto = arReportDao.selectARConclusion(idStageFrom);
			overallDisposition = aRConclusionValueDto.getCdAROverallDisposition();
			overallDispositionDesc = getOverallDispositionByCode(overallDisposition);

			if (!TypeConvUtil.isNullOrEmpty(aRConclusionValueDto)
					&& INDICATOR_YES1.equals(aRConclusionValueDto.getIndArEaEligible())) {
				createEaEligible(aRConclusionValueDto);
			}
			// 02. Find the user requesting stage progression
			Long requestingWorkerID = approvalStatusDao.getPrimaryWorkerIdForStage(Long.valueOf(idFromStage));
			// 03. Check the closure reason. Based on that call the appropriate
			// method
			if (CCLOSAR_060.equals(overallDispositionDesc)) {

				// FBSS Referral - createFPR calls only if APP event created before FBSS Referral release date
				if (workLoadDao.hasAppEventExistsBeforeFBSSRef(idApproval)) {
					newStageID = createFPRStage(idCase, idFromStage, requestingWorkerID.intValue(), overallDisposition,
							idApprover);
				} else {
					// Copy Open Service Auth to FPR when Reason Code is FPR/FBSS
					cpsCopyOpenServiceAuth((long) idFromStage, CSTAGES_AR, requestingWorkerID);
					//[artf172959] Defect: 16737 - AR closure reasons close case with open stage
					newStageID = closeARStage(idCase, Long.valueOf(idFromStage), requestingWorkerID.intValue(),
							overallDisposition, idApprover, isAnyStageOpenForCase(idCase));

				}

			} else if (CCLOSAR_070.equals(overallDispositionDesc)
					|| CCLOSAR_080.equals(overallDispositionDesc)
					|| CCLOSAR_090.equals(overallDispositionDesc)
					|| CCLOSAR_100.equals(overallDispositionDesc)) {
				newStageID = createINVStage(idCase, idFromStage, requestingWorkerID.intValue(), overallDisposition,
						idApprover);
			} else if (CCLOSAR_010.equals(overallDispositionDesc)
					|| CCLOSAR_020.equals(overallDispositionDesc)
					|| CCLOSAR_030.equals(overallDispositionDesc)
					|| CCLOSAR_040.equals(overallDispositionDesc)
					|| CCLOSAR_050.equals(overallDispositionDesc)) {
				newStageID = closeARStage(idCase, Long.valueOf(idFromStage), requestingWorkerID.intValue(),
						overallDisposition, idApprover, isAnyStageOpenForCase(idCase));

				// artf151569 - FBSS Referral - Generate Alert on A-R Closure
				//TODO:: Check for idUser instead of idApprover
				generateAlertIfStageOpen((long) idFromStage, CSTAGES_AR, (long) idCase,
						(long) idApprover, false);
			}
			// Added code for EJB Change
			//[artf172959] Defect: 16737 - AR closure reasons close case with open stage
			else if ((CCLOSAR_110.equals(overallDispositionDesc))
					|| (CCLOSAR_120.equals(overallDispositionDesc))
					|| (CCLOSAR_130.equals(overallDispositionDesc))
					|| (CCLOSAR_140.equals(overallDispositionDesc))) {
				newStageID = closeARStage(idCase, Long.valueOf(idFromStage), requestingWorkerID.intValue(),
						overallDisposition, idApprover, isAnyStageOpenForCase(idCase));

				// artf151569 - FBSS Referral - Generate Alert on A-R Closure
				//TODO:: Check for idUser instead of idApprover
				generateAlertIfStageOpen((long) idFromStage, CSTAGES_AR, (long) idCase,
						(long) idApprover, false);
			}
			postProcessing(newStageID, requestingWorkerID.intValue());
		} catch (DataNotFoundException de) {
			log.error(de.getMessage());
		}
		return overallDisposition;
	}

	private String getOverallDispositionByCode(String overallDisposition) {
		switch (overallDisposition) {
		case "010":
			return CCLOSAR_010;
		case "020":
			return CCLOSAR_020;
		case "030":
			return CCLOSAR_030;
		case "040":
			return CCLOSAR_040;
		case "050":
			return CCLOSAR_050;
		case "060":
			return CCLOSAR_060;
		case "070":
			return CCLOSAR_070;
		case "080":
			return CCLOSAR_080;
		case "090":
			return CCLOSAR_090;
		case "100":
			return CCLOSAR_100;
		case "110":
			return CCLOSAR_110;
		case "120":
			return CCLOSAR_120;
		case "130":
			return CCLOSAR_130;
		case "140":
			return CCLOSAR_140;
		default:
			return "";
		}
	}

	/**
	 * Method Name: isSecondaryApprovalRequired Method Description:This method
	 * is used to determine if there is a need for secondary approval
	 *
	 * @param secondaryApprovalDto
	 * @return boolean
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public boolean isSecondaryApprovalRequired(SecondaryApprovalDto secondaryApprovalDto) {
		log.info("ApprovalStatusServices.isSecondaryApprovalRequired()");
		if (isCPSSecondaryApprovalRequiredForSDM(secondaryApprovalDto) || isChildFatality(secondaryApprovalDto)) {
			log.info("ApprovalStatusServices.isSecondaryApprovalRequired() is returning true");
			return TRUE_VALUE;
		} else {

			// If any reason above call return false , we will make another call
			// for INV stages
			boolean isCPSINV = checkCPSInvStatus(secondaryApprovalDto);
			if (isCPSINV) {
				return isSecondLevelApproverRequiredForCPSINV(secondaryApprovalDto);
			}

			log.info("ApprovalStatusServices.isSecondaryApprovalRequired() is returning false");
			return FALSEVAL;
		}
	}

	/**
	 * Method Name: isCPSSecondaryApprovalRequiredForSDM Method Description:This
	 * method is used to determine if there is a need for secondary approval for
	 * SDM
	 *
	 * @param approvalDto
	 * @return boolean
	 */
	/*
	 * @Override
	 */ @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public boolean isCPSSecondaryApprovalRequiredForSDM(SecondaryApprovalDto approvalDto) {

		boolean isCPSINV = FALSEVAL;
		log.info("ApprovalStatusServices.isCPSSecondaryApprovalRequiredForSDM()");
		if (approvalDto.getIdCase() != 0 && approvalDto.getCdStage() != null && approvalDto.getCdStageProgram() != null
				&& approvalDto.getCdTaskCode() != null) {

			isCPSINV = checkCPSInvStatus(approvalDto);
			if ((TASK_CODE_DAYCARE_APP_INV.equals(approvalDto.getCdTaskCode())
					|| TASK_CODE_DAYCARE_APP_FPR.equals(approvalDto.getCdTaskCode())
					|| TASK_CODE_DAYCARE_APP_FRE.equals(approvalDto.getCdTaskCode())
					|| TASK_CODE_DAYCARE_APP_FSU.equals(approvalDto.getCdTaskCode())
					|| TASK_CODE_DAYCARE_APP_AR.equals(approvalDto.getCdTaskCode())
					|| TASK_CODE_DAYCARE_APP_SUB.equals(approvalDto.getCdTaskCode()))
					|| (SVC_CD_TASK_CONTACT_AOC.equals(approvalDto.getCdTaskCode())
							|| APS_INV_TO_SVC_TASK.equals(approvalDto.getCdTaskCode())
							|| SVC_CD_TASK_CONTACT_SRR_APS.equals(approvalDto.getCdTaskCode()))) {
				log.info("ApprovalStatusServices.isCPSSecondaryApprovalRequiredForSDM() is returning true value");
				return TRUE_VALUE;
			}

			// Check if Incoming approval is SDM Reunification Assessment
			// Approval. then evaluate the 2nd level
			// approver criteria.
			if (CSTAGES_FSU.equalsIgnoreCase(approvalDto.getCdStage())
					&& CCONPROG_CPS.equalsIgnoreCase(approvalDto.getCdStageProgram())
					&& TASK_7490.equals(approvalDto.getCdTaskCode())) {
				return isSecondLevelApproverReqdforReunificaiton(Long.valueOf(approvalDto.getIdEvent()));
			}

		}
		boolean hasMatchRecommendation = hasMatchRecommendation(approvalDto);

		List<String> opnStgList = approvalStatusDao.getOpenStagesForCase(Long.valueOf(approvalDto.getIdCase()));
		boolean bFBSSStageFound = FALSEVAL;

		for (String idStage : opnStgList) {
			bFBSSStageFound = hasbFBSSStageFound(idStage);
		}

		if ((!isCPSINV) || hasMatchRecommendation || bFBSSStageFound) {
			return FALSEVAL;
		}

		String szCdOverallDisposition = approvalStatusDao.queryOverallDispositionForInvst(approvalDto.getIdCase(),
				approvalDto.getIdStage());
		boolean isSeriousOverallDisposition;
		//artf149010 added CCRUTC_83 as part of removing Old Business Rules for CSS
		if(checkSeriousOverall(szCdOverallDisposition) && (CCRUTC_83.equals(approvalDto.getCdStageReasonClose()))) {
			isSeriousOverallDisposition = TRUEVAL;
		} else {
			isSeriousOverallDisposition = FALSEVAL;
		}

		boolean hasStageReviewedBySecondApprover = FALSEVAL;

		SecondaryApprovalDto secondaryApprovalViewDto = approvalStatusDao.getStageDetails(approvalDto.getIdStage());

		if (secondaryApprovalViewDto != null) {
			if (Y.equals(secondaryApprovalViewDto.getIndSecondApprover())) {
				hasStageReviewedBySecondApprover = true;
			}
			approvalDto.setDtStageStart(secondaryApprovalViewDto.getDtStageStart());
		}

		boolean hasUnderAgeChild = FALSEVAL;

		List<ApprovalCommonInDto> stageChildrenList = approvalStatusDao.queryStageChildrenDOB(approvalDto.getIdCase(),
				approvalDto.getIdStage());

		if (stageChildrenList != null) {
			hasUnderAgeChild = checkUnderAgeChild(approvalDto, hasUnderAgeChild, stageChildrenList);
		}
		if (isSeriousOverallDisposition == FALSEVAL
				|| hasStageReviewedBySecondApprover == TRUE_VALUE
				|| (hasUnderAgeChild == FALSEVAL
						|| approvalStatusDao.isSchoolInvestigation(approvalDto.getIdStage())
						|| approvalStatusDao.isSchoolPersonnelInvolved(approvalDto.getIdStage()))) {
			return FALSEVAL;
		}

		return TRUE_VALUE;
	}

	/**
	 * Method Name: isChildFatality Method Description:Find Whether
	 * ChildFatality present or not.
	 *
	 * @param approvalViewDto
	 * @return boolean
	 */
	private boolean isChildFatality(SecondaryApprovalDto approvalViewDto) {

		boolean isCPSCCLINV = FALSEVAL;
		log.info("ApprovalStatusServices.isChildFatality()");
		if (approvalViewDto.getIdCase() != 0 && approvalViewDto.getCdStage() != null
				&& approvalViewDto.getCdStageProgram() != null && approvalViewDto.getCdTaskCode() != null) {
			if ((CPGRMS_CPS.equals(approvalViewDto.getCdStageProgram())
					|| CPGRMS_CCL.equals(approvalViewDto.getCdStageProgram())
					|| CPGRMS_RCL.equals(approvalViewDto.getCdStageProgram()))
					&& CSTAGES_INV.equals(approvalViewDto.getCdStage())
					&& (TASK_CPSINVCLOSURE.equals(approvalViewDto.getCdTaskCode())
							|| INV_CONCLUSION_TASK_CODE.equals(approvalViewDto.getCdTaskCode())
							|| TASK_CCLINVCLOSURE.equals(approvalViewDto.getCdTaskCode())
							|| TASK_CCLINVCLOSEAPP.equals(approvalViewDto.getCdTaskCode())
							|| TASK_RCLINVCLOSURE.equals(approvalViewDto.getCdTaskCode())
							|| TASK_RCLINVCLOSEAPP.equals(approvalViewDto.getCdTaskCode()))) {
				isCPSCCLINV = TRUE_VALUE;
			}
		}
		if (isCPSCCLINV == FALSEVAL) {
			return FALSEVAL;
		}
		boolean childDied = approvalStatusDao.isChildDeath(approvalViewDto.getIdStage());

		if (childDied) {
			return TRUE_VALUE;
		} else {
			return FALSEVAL;
		}

	}

	/**
	 * Method Name: checkCPSInvStatus Method Description:We need to check
	 * secondary approval for CPS investigation stage only
	 *
	 * @param approvalDto
	 * @return boolean
	 */
	private boolean checkCPSInvStatus(SecondaryApprovalDto approvalDto) {
		boolean isCPSINV = false;
		log.info("ApprovalStatusServices.checkCPSInvStatus()");
		if (CPGRMS_CPS.equals(approvalDto.getCdStageProgram())
				&& CSTAGES_INV.equals(approvalDto.getCdStage())
				&& (TASK_CPSINVCLOSURE.equals(approvalDto.getCdTaskCode())
						|| INV_CONCLUSION_TASK_CODE.equals(approvalDto.getCdTaskCode()))) {
			log.info("ApprovalStatusServices.checkCPSInvStatus() is returning true value");
			isCPSINV = TRUEVAL;
		}
		log.info("ApprovalStatusServices.checkCPSInvStatus() is returning false value");
		return isCPSINV;
	}

	/**
	 * Method Name: hasMatchRecommendation Method Description:Checks the
	 * CdStageReasonClose
	 *
	 * @param approvalDto
	 * @return boolean
	 */
	private boolean hasMatchRecommendation(SecondaryApprovalDto approvalDto) {
		if (!TypeConvUtil.isNullOrEmpty(approvalDto.getCdStageReasonClose())) {
			log.info("ApprovalStatusServices.hasMatchRecommendation()");
			if (approvalDto.getCdStageReasonClose().equals(CRSRCLOS_45)
					|| CRSRCLOS_55.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_64.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_65.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_66.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_68.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_69.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_70.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_72.equals(approvalDto.getCdStageReasonClose())
					|| CCRSKRIC_97.equals(approvalDto.getCdStageReasonClose())) {
				return TRUEVAL;
			}
		}
		return FALSEVAL;
	}

	/**
	 * Method Name: hasbFBSSStageFound Method Description:Check whether Stage
	 * found or not
	 *
	 * @param Stage
	 * @return boolean
	 */
	private boolean hasbFBSSStageFound(String Stage) {
		log.info("ApprovalStatusServices.hasbFBSSStageFound()");
		if ((Stage != null) && (CSTAGES_ADO.equals(Stage) || CSTAGES_FRE.equals(Stage)
				|| CSTAGES_FPR.equals(Stage) || CSTAGES_FSU.equals(Stage)
				|| CSTAGES_SUB.equals(Stage))) {
			return TRUEVAL;
		}
		return FALSEVAL;
	}

	/**
	 * Method Name: checkSeriousOverall Method Description:check if overall
	 * disposition for stage is not "Ruled Out" or "Admin closure" // This
	 * indirectly means that there is a serious allegation for a principal in
	 * stage
	 *
	 * @param szCdOverallDisposition
	 * @return
	 */
	private boolean checkSeriousOverall(String szCdOverallDisposition) {
		boolean isSeriousOverallDisposition;
		//artf149010 added UNABLE_TO_COMPLETE check as part of removing Old Business Rules for CSS
		if (szCdOverallDisposition != null) {
			if ((UNABLE_TO_COMPLETE).equals(szCdOverallDisposition)) {
				isSeriousOverallDisposition = TRUEVAL;
			} else
				isSeriousOverallDisposition = FALSEVAL;
		} else {
			isSeriousOverallDisposition = FALSEVAL;
		}
		return isSeriousOverallDisposition;
	}

	/**
	 * Method Name: checkUnderAgeChild Method Description:determine if there is
	 * an underage child in the stage
	 *
	 * @param approvalViewDto
	 * @param hasUnderAgeChild
	 * @param stageChildrenList
	 * @return
	 */
	private boolean checkUnderAgeChild(SecondaryApprovalDto approvalViewDto, boolean hasUnderAgeChild,
			List<ApprovalCommonInDto> stageChildrenList) {
		for (ApprovalCommonInDto personDetail : stageChildrenList) {
			if (personDetail.getDtPersonBirth() != null) {
				Date priorStageStartDate = approvalStatusDao.getPriorStage(Long.valueOf(approvalViewDto.getIdStage()));
				//Modified the code to check the age with Max victim age as 4 - Warranty defect 11761
				if (getAge(personDetail.getDtPersonBirth(), priorStageStartDate) < MAX_VICTIM_AGE_CNCLSN) {
					hasUnderAgeChild = true;
					break;
				}
			}
		}
		return hasUnderAgeChild;
	}

	/**
	 * returns the present age.
	 *
	 * @param dateOfBirth
	 * @return boolean
	 *
	 */
	private static int getAge(Date dateOfBirth, Date date) {
		int personAge = Zero;
		Calendar fromDate = Calendar.getInstance();
		Calendar toDate = Calendar.getInstance();

		fromDate.setTime(dateOfBirth);
		toDate.setTime(date);

		if (dateOfBirth != null) {
			personAge = toDate.get(Calendar.YEAR) - fromDate.get(Calendar.YEAR);

			if ((fromDate.get(Calendar.MONTH) > toDate.get(Calendar.MONTH))
					|| ((fromDate.get(Calendar.MONTH) == toDate.get(Calendar.MONTH))
							&& (fromDate.get(Calendar.DAY_OF_MONTH) > toDate.get(Calendar.DAY_OF_MONTH)))) {
				personAge = personAge - 1;
			}

		}
		return personAge;
	}

	/**
	 * Method Name: getPendingApprovalCount Method Description:This function
	 * returns Count of the Pending Approvals for the given Approval Id.
	 *
	 * @param idApproval
	 * @return long
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long getPendingApprovalCount(long idApproval) {

		SecondaryApprovalDto approvalServiceDto = new SecondaryApprovalDto();
		approvalServiceDto.setUlIdApproval(idApproval);
		long pendingCount = ZERO_VAL;
		ApproveApprovalDto approveApprovalDto = approvalStatusDao.getCcmn56DO(approvalServiceDto);

		ApprovalCommonInDto approvalCommonInDto = approveApprovalDto.getRowCcmn56DoArrayDto();

		List<ApproversDto> ApproversDto = approvalCommonInDto.getRowCcmn56DoList();

		for (ApproversDto approverDtrmntnDto : ApproversDto) {
			if (CEVTSTAT_PEND.equals(approverDtrmntnDto.getCdApproversStatus())) {
				pendingCount++;
			}
		}

		return pendingCount;
	}

	/**
	 *
	 * Method Name: createEaEligible Method Description: Method to create a new
	 * person Eligibility Record
	 *
	 * @param arInvCnclsnDto
	 */
	@SuppressWarnings("unused")
	private void createEaEligible(ArInvCnclsnDto arInvCnclsnDto) {
		List<Integer> idPersonList = new ArrayList<>();
		idPersonList = arStageProgDao.getAllEligiblePrinciplesArStage(Long.valueOf(arInvCnclsnDto.getIdStage()));
		PersonEligibilityDto personEligibilityDto = null;
		if (null != idPersonList) {
			for (Integer idPerson : idPersonList) {
				personEligibilityDto = arStageProgDao.getPersonEligibilityRecord(idPerson);
				if (!TypeConvUtil.isNullOrEmpty(personEligibilityDto)) {
					if (PERSELIG_PRG_START_S.equals(personEligibilityDto.getCdPersEligPrgClosed())
							&& PERSELIG_PRG_START_S
									.equals(personEligibilityDto.getCdPersEligPrgOpen())) {
						personEligibilityDto.setCdPersEligPrgOpen(PERSELIG_PRG_OPEN_B);
					} else {
						personEligibilityDto.setCdPersEligPrgOpen(PERSELIG_PRG_OPEN_C);
					}
					PersonEligibilityValueDto personEligibilityValueDto = new PersonEligibilityValueDto();
					personEligibilityValueDto.setPersonEligibilityId(personEligibilityDto.getIdPersElig());
					arStageProgDao.updatePersonEligibility(personEligibilityValueDto);
				} else {

					Date dtPersEligStart = arStageProgDao
							.getEligibilityStartDate(Long.valueOf(arInvCnclsnDto.getIdStage()));

					arStageProgDao.createPersonEligibility(Long.valueOf(idPerson), dtPersEligStart);
				}
			}
		}
	}

	/**
	 * Method Name: createFPRStage Method Description: 01. Add a event stating
	 * the A-R Stage is closed 02. Delete To dos. Any to do that were not
	 * completed, i.e. DT_TO DO_COMPLETED is NULL. 03: New Stage Creation.
	 * Ensure no other stages are open. 04: Create a new Event for the new stage
	 * creation. i.e FPR 05. Close the existing A-R stage. This is moved to a
	 * later stage because of common code being used. 06: Change
	 * Type/ROLE/REL-INT. PR to HP. Delete SE 07. Service Authorization. Needs
	 * to be transferred to the FPR. Service Authorization is not stage
	 * specific. So it should show up in FPR automatically.
	 *
	 * @param idCase
	 * @param idFromStage
	 * @param idUser
	 * @param reasonCode
	 * @param idApprover
	 * @return
	 */
	private Long createFPRStage(int idCase, int idFromStage, int idUser, String reasonCode, int idApprover) {

		PostDto postDto = new PostDto();
		postDto.setIdStage(idFromStage);
		postDto.setCdEventType(CEVNTTYP_STG);
		postDto.setIdPerson(idUser);
		postDto.setCdTask(EMPTY_STRING);
		postDto.setReqFunctionCd(REQ_FUNC_CD_ADD);
		postDto.setTxtEventDescr(ALT_RESPONSE_STAGE_CLOSED);
		postDto.setCdEventStatus(CEVTSTAT_COMP);
		postDto.setDtDtEventOccurred((Calendar.getInstance()).getTime());
		postDto.setDtEventCreated((Calendar.getInstance()).getTime());

		ServiceInputDto archInputStructDto = new ServiceInputDto();
		archInputStructDto.setCreqFuncCd(REQ_FUNC_CD_ADD);
		postDto.setArchInputStructDto(archInputStructDto);

		SynchronizationServiceDto rowCcmn01UiG00 = new SynchronizationServiceDto();
		rowCcmn01UiG00.setIdEvent(postDto.getIdEvent());
		rowCcmn01UiG00.setIdStage(postDto.getIdStage());
		rowCcmn01UiG00.setIdPerson(postDto.getIdPerson());
		rowCcmn01UiG00.setCdTask(postDto.getCdTask());
		rowCcmn01UiG00.setCdEventType(postDto.getCdEventType());
		rowCcmn01UiG00.setEventDescr(postDto.getTxtEventDescr());
		rowCcmn01UiG00.setDtEventOccurred(postDto.getDtDtEventOccurred());
		rowCcmn01UiG00.setCdEventStatus(postDto.getCdEventStatus());
		rowCcmn01UiG00.setDtLastUpdate(postDto.getEventLastUpdate());

		postDto.setRowCcmn01UiG00(rowCcmn01UiG00);
		postEventService.PostEvent(postDto);

		todoDao.deleteIncompleteTodos(idFromStage);

		Long newStageId = 0l;

		StageValueBeanDto stageCreationValBean = new StageValueBeanDto();
		stageCreationValBean.setIdStage(Long.valueOf(idFromStage));
		// setCdStageType to setCdStage
		stageCreationValBean.setCdStage(CSTAGES_FPR);
		stageCreationValBean.setIdCreatedPerson(Long.valueOf(idUser));
		stageCreationValBean.setNmNewCase("");
		newStageId = stageProgressionService.createNewStage(stageCreationValBean);

		StageValueBeanDto stageDtoDto = new StageValueBeanDto();
		stageDtoDto.setDtStageClose((Calendar.getInstance()).getTime());
		stageDtoDto.setIdStage((long) idFromStage);
		stageDtoDto.setCdStageReasonClosed(reasonCode);
		arStageProgDao.closeStage(stageDtoDto);

		List<String> requestedRoles = new ArrayList<>();
		requestedRoles.add(CROLEALL_PR);
		requestedRoles.add(CROLEALL_SE);
		List<StagePersonValueDto> staffPersonList = stageProgDao.selectStagePersonLink(idFromStage, requestedRoles);
		for (StagePersonValueDto staffValueBean : staffPersonList) {
			if (CROLEALL_PR.equals(staffValueBean.getCdStagePersRole())) {
				staffValueBean.setCdStagePersRole(CROLEALL_HP);
				arStageProgDao.updateStagePersonLink(staffValueBean);
			} else if (CROLEALL_SE.equals(staffValueBean.getCdStagePersRole())) {
				stageDao.deleteStagePersonLink(staffValueBean);
			}
		}

		List<String> approverRelatedRoles = new ArrayList<>();
		approverRelatedRoles.add(CROLEALL_PR);
		List<StagePersonValueDto> currentPRList = stageProgDao.selectStagePersonLink(newStageId.intValue(),
				approverRelatedRoles);
		StagePersonValueDto iSPValueBean = currentPRList.get(0);
		arStageProgDao.updateStagePersonLink(iSPValueBean.getIdPerson(), Long.valueOf(idApprover),
				Long.valueOf(newStageId), Long.valueOf(idCase), CROLEALL_PR);

		List<EventPersonDto> eventPersons = new ArrayList<>();
		List<SvcAuthEventLinkInDto> existingSvcAuthEvents = svcAuthDetailDao.getServiceAuthorizationEventDetails(
				Long.valueOf(idCase), Long.valueOf(idFromStage), AR_SERVICE_AUTH_TASK_CODE);
		if (!ObjectUtils.isEmpty(existingSvcAuthEvents)) {
			for (SvcAuthEventLinkInDto serviceAuthEventLinkValueBean : existingSvcAuthEvents) {
				EventValueDto eventValueDto1 = new EventValueDto();
				eventValueDto1.setIdStage(newStageId);
				eventValueDto1.setCdEventType(CEVNTTYP_AUT);
				eventValueDto1.setIdCase(Long.valueOf(idCase));
				eventValueDto1.setIdPerson(Long.valueOf(idUser));
				eventValueDto1.setEventDescr(serviceAuthEventLinkValueBean.getTxtEventDescription());
				eventValueDto1.setCdEventStatus(CEVTSTAT_APRV);
				eventValueDto1.setCdEventTask(FPR_SERVICE_AUTH_TASK_CODE);
				int invServiceAuthEventID = arStageProgDao.createEvent(eventValueDto1);

				serviceAuthEventLinkValueBean.setSvcAuthEventId(Long.valueOf(invServiceAuthEventID));

				for (Long idPerson : serviceAuthEventLinkValueBean.getPersons()) {
					EventPersonDto eventPersonValueBean = new EventPersonDto();
					eventPersonValueBean.setIdpersonId(idPerson);
					eventPersonValueBean.setIdEvent(Long.valueOf(invServiceAuthEventID));
					eventPersonValueBean.setIdCase(Long.valueOf(idCase));
					eventPersons.add(eventPersonValueBean);
				}
			}

			serviceAuthorizationDao.insertIntoServiceAuthorizationEventLinks(existingSvcAuthEvents);
			serviceAuthorizationDao.insertIntoEventPersonLinks(eventPersons);
		}

		return newStageId;
	}

	/**
	 *
	 * Method Name: createINVStage Method Description:01. Update Incoming
	 * detail. IND_INCMG_INT_INV_RECLASS='Y'. What does this mean. Do we need to
	 * to do something similar for A-R to INV 02. Add a event stating the A-R
	 * Stage is closed 03. STAGE_PERSON_ LINK needs to be updated. PR is changed
	 * to HP. Done later due to programming logic 04. Delete To do s. Any todo
	 * that were not completed, i.e. DT_TO DO_COMPLETED is NULL. Additionally
	 * certain contact types cannot be deleted. CMST, C3MT, CS45 05: PERSON
	 * ELIGIBILITY. Are there any implications. 06: Any updating ON CASE CAPS?
	 * 07: New Stage Creation. Ensure no other stages are open. 08: Create a new
	 * Event for the new stage creation. i.e A-R 09. Close the existing A-R
	 * stage. This is moved from the first step to the 9 because of common code
	 * being used. 10: Change ROLE/REL-INT. PR to HP. Delete SE 11: Create a new
	 * To Do - This will call the INV Alerts. problem is it needs to be called
	 * outside after the stage is committed. 12: Move contacts if any ---
	 * Contacts are not moved. 13: APP ACCESS Audit log. This is created every
	 * time a contact is added. Not required 14: Any ADMIN Review implications -
	 * Not Applicable 15: Allegations - Do we do anything with Allegations 16:
	 * New entry into CPS_AR_CNCLS_DETAIL 16.1 We need to insert a Event for INV
	 * Conclusion to be used for INV details table 16.2 New entry into
	 * cps_invst_detail 17. Service Authorization. Needs to be transferred to
	 * the INV. SA is not stage specific. So it should show up in INV
	 * automatically. 18. PriorStage and Incoming Call date update to workload.
	 * Currently there is a trigger that does not work. 19. Create a INV Safety
	 * Assessment Record from a approved AR Safety Assessment and a associated
	 * approved event. Also Risk Assessment
	 *
	 * @param idCase
	 * @param idFromStage
	 * @param idUser
	 * @param reasonCode
	 * @param idApprover
	 * @return int
	 */
	private Long createINVStage(int idCase, int idFromStage, int idUser, String reasonCode, int idApprover) {

		//get all the AR and linked INT stages for the case.
		List<MergedIntakeARStageDto> linkedStages = caseSummaryDao.getLinkedIntakeARStages(idCase);
		Long nextARStageId = null;
		Long nextIntakeStageId = null;
		List<Long> intakeStages = new ArrayList<>();
		Long currentArStage = (long) idFromStage;
		List<Long> closedToMergeIntakeStages = null;

		if(linkedStages !=null && !linkedStages.isEmpty()) {
			List<MergedIntakeARStageDto> sortedArStages = linkedStages.stream().distinct()
					.sorted(Comparator.comparing(MergedIntakeARStageDto::getArStageId))
					.collect(Collectors.toList());

			//filter all stages  greater than the inputStageId
			List<MergedIntakeARStageDto> stagesAfterCurrentArstage = sortedArStages.stream()
					.filter(arId -> arId.getArStageId() > currentArStage).collect(Collectors.toList());

			closedToMergeIntakeStages = stagesAfterCurrentArstage.stream()
					.filter(x -> x.getStageReasonClosed() != null && ServiceConstants.STAGE_REASON_CLOSED.equals(x.getStageReasonClosed()))
					.map(MergedIntakeARStageDto::getIntakeStageId).collect(Collectors.toList());
		}

		if(closedToMergeIntakeStages != null && !closedToMergeIntakeStages.isEmpty()) {
			intakeStages = new ArrayList<>(closedToMergeIntakeStages);
		}

		SelectStageDto intakeStage = caseSummaryDao.getStage(Long.valueOf(idFromStage), STAGE_PRIOR);

		// Steps to create new stage

		// 01. Update Incoming detail. IND_INCMG_INT_INV_RECLASS='Y'. What does
		// this
		// mean. Do we need to to do something similar for A-R to INV

		// 02. Add a event stating the A-R Stage is closed

		PostDto postDto = new PostDto();
		postDto.setIdStage(idFromStage);
		postDto.setCdEventType(CEVNTTYP_STG);
		postDto.setIdPerson(idUser);
		postDto.setCdTask(EMPTY_STRING);
		postDto.setReqFunctionCd(REQ_FUNC_CD_ADD);
		postDto.setTxtEventDescr(ALT_RESPONSE_STAGE_CLOSED);
		postDto.setCdEventStatus(CEVTSTAT_COMP);
		postDto.setDtDtEventOccurred((Calendar.getInstance()).getTime());
		postDto.setDtEventCreated((Calendar.getInstance()).getTime());

		ServiceInputDto archInputStructDto = new ServiceInputDto();
		archInputStructDto.setCreqFuncCd(REQ_FUNC_CD_ADD);
		postDto.setArchInputStructDto(archInputStructDto);

		SynchronizationServiceDto rowCcmn01UiG00 = new SynchronizationServiceDto();
		rowCcmn01UiG00.setIdEvent(postDto.getIdEvent());
		rowCcmn01UiG00.setIdStage(postDto.getIdStage());
		rowCcmn01UiG00.setIdPerson(postDto.getIdPerson());
		rowCcmn01UiG00.setCdTask(postDto.getCdTask());
		rowCcmn01UiG00.setCdEventType(postDto.getCdEventType());
		rowCcmn01UiG00.setEventDescr(postDto.getTxtEventDescr());
		rowCcmn01UiG00.setDtEventOccurred(postDto.getDtDtEventOccurred());
		rowCcmn01UiG00.setCdEventStatus(postDto.getCdEventStatus());
		rowCcmn01UiG00.setDtLastUpdate(postDto.getEventLastUpdate());

		postDto.setRowCcmn01UiG00(rowCcmn01UiG00);

		postEventService.PostEvent(postDto);

		// 03. STAGE_PERSON_ LINK needs to be updated. PR is changed to HP. Done
		// later
		// due to programming logic

		// 04. Delete To dos. Any todo that were not completed, i.e.
		// DT_TODO_COMPLETED
		// is NULL.

		todoDao.deleteIncompleteTodos(idFromStage);

		// 05: PERSON ELIGIBILITY. Are there any implications.

		// 06: Any updating ON CASE CAPS? No changes will be done.

		// 07: New Stage Creation. Ensure no other stages are open
		// 08: Create a new Event for the new stage creation. i.e A-R

		StageValueBeanDto stageCreationValBean = new StageValueBeanDto();
		stageCreationValBean.setIdStage(Long.valueOf(idFromStage));
		stageCreationValBean.setCdStage(CSTAGES_INV);
		stageCreationValBean.setCdStageType(intakeStage.getCdStageType());
		stageCreationValBean.setIdCreatedPerson(Long.valueOf(idUser));
		stageCreationValBean.setNmNewCase("");
		Long newStageId = stageProgressionService.createNewStage(stageCreationValBean);

		// 09. Close the existing AR stage. Due to the need to reuse, we have to
		// create
		// a new case and then close the old

		StageClosureValueDto stageClosureValueDto = new StageClosureValueDto();
		stageClosureValueDto.setDtStageClosed((Calendar.getInstance()).getTime());
		stageClosureValueDto.setIdStage(idFromStage);
		stageClosureValueDto.setCdStageCloseRsn(reasonCode);
		arStageProgDao.closeStage(stageClosureValueDto);

		// 10. Any STAGE_PERSON_ LINK needs to be updated? PR needs to be to HP.
		// First old Stage Person Needs to be fetched for PR. ANd updated with
		// HP.
		// Delete SE
		List<String> requestedPRRoles = new ArrayList<>();
		requestedPRRoles.add(CROLEALL_PR);
		requestedPRRoles.add(CROLEALL_SE);

		// Steps to transform Stage Person Links from A-R to INV
		// a. All persons in AR will adopt the Type from the INTAKE
		// b. All persons in AR will adopt the Role from the INTAKE
		// c. All persons in AR will retain their new REL-INT. Exception:
		// Reference
		// Child will be changed to Oldest Victim
		// d. Any person in INT that does not currently exist in A-R needs to be
		// brought
		// back
		List<StagePersonValueDto> currentPRList = stageProgDao.selectStagePersonLink(idFromStage, requestedPRRoles);

		for (StagePersonValueDto staffValueBean : currentPRList) {
			if (CROLEALL_PR.equals(staffValueBean.getCdStagePersRole())) {
				staffValueBean.setCdStagePersRole(CROLEALL_HP);
				arStageProgDao.updateStagePersonLink(staffValueBean);
			} else if (CROLEALL_SE.equals(staffValueBean.getCdStagePersRole())) {
				stageDao.deleteStagePersonLink(staffValueBean);
			}
		}

		List<StagePersonValueDto> finalizedList = new ArrayList<>();

		List<String> intakeRoles = new ArrayList<>();
		intakeRoles.add(CROLEALL_AP);
		intakeRoles.add(CROLEALL_NO);
		intakeRoles.add(CROLEALL_VC);
		intakeRoles.add(CROLEALL_VP);
		intakeRoles.add(CROLEALL_UK);

		List<StagePersonValueDto> intStagePersonList = stageProgDao.selectStagePersonLink(newStageId.intValue(),
				intakeRoles);

		HashMap<Long, StagePersonValueDto> mapINTStagePersonMap = new HashMap<>();
		for (StagePersonValueDto inst : intStagePersonList) {
			mapINTStagePersonMap.put(inst.getIdPerson(), inst);
		}

		List<String> invRolesList = new ArrayList<>();
		invRolesList.add(CROLEALL_AR);
		invRolesList.add(CROLEALL_NO);
		invRolesList.add(CROLEALL_AP);
		invRolesList.add(CROLEALL_VC);
		invRolesList.add(CROLEALL_VP);
		invRolesList.add(CROLEALL_UK);
		List<StagePersonValueDto> invStagePersonList = stageProgDao.selectStagePersonLink(newStageId.intValue(),
				invRolesList);

		Map<Integer, Integer> mergedPersonsMap = arStageProgDao
				.fetchForwardPersonsForStagePersons(Long.valueOf(newStageId));

		for (StagePersonValueDto stagePersonValueDto : invStagePersonList) {
			StagePersonValueDto intPerson = mapINTStagePersonMap.get(stagePersonValueDto.getIdPerson());
			if (intPerson != null) {
				boolean ismodified = false;
				if (!stagePersonValueDto.getCdStagePersType().equals(intPerson.getCdStagePersType())) {
					stagePersonValueDto.setCdStagePersType(intPerson.getCdStagePersType());
					ismodified = true;
				}
				if (!stagePersonValueDto.getCdStagePersRole().equals(intPerson.getCdStagePersRole())) {
					stagePersonValueDto.setCdStagePersRole(intPerson.getCdStagePersRole());
					ismodified = true;
				}
				if ((!TypeConvUtil.isNullOrEmpty(stagePersonValueDto.getCdStagePersRelLong()))
						&& (CRELVICT_RC.equals(stagePersonValueDto.getCdStagePersRelLong()))) {
					stagePersonValueDto.setCdStagePersRelLong(CRPTRINT_OV);
					ismodified = true;
				}
				if (ismodified) {
					finalizedList.add(stagePersonValueDto);
				}
				mapINTStagePersonMap.remove(intPerson.getIdPerson());
			} else {
				if (mergedPersonsMap.containsKey(stagePersonValueDto.getIdPerson())) {
					int priorIdBeforeMerge = mergedPersonsMap.get(stagePersonValueDto.getIdPerson());
					StagePersonValueDto intPersonThatWasMerged = mapINTStagePersonMap.get(priorIdBeforeMerge);
					if (intPersonThatWasMerged != null) {
						boolean ismodified = false;
						if (!stagePersonValueDto.getCdStagePersType()
								.equals(intPersonThatWasMerged.getCdStagePersType())) {
							stagePersonValueDto.setCdStagePersType(intPersonThatWasMerged.getCdStagePersType());
							ismodified = true;
						}
						if (!stagePersonValueDto.getCdStagePersRole()
								.equals(intPersonThatWasMerged.getCdStagePersRole())) {
							stagePersonValueDto.setCdStagePersRole(intPersonThatWasMerged.getCdStagePersRole());
							ismodified = true;
						}
						if ((stagePersonValueDto.getCdStagePersRelLong() != null)
								&& (stagePersonValueDto.getCdStagePersRelLong().equals(CRELVICT_RC))) {
							stagePersonValueDto.setCdStagePersRelLong(CRPTRINT_OV);
							ismodified = true;
						}
						if (ismodified) {
							finalizedList.add(stagePersonValueDto);
						}
						mapINTStagePersonMap.remove(intPersonThatWasMerged.getIdPerson());
					} else {
						boolean ismodified = false;
						if ((stagePersonValueDto.getCdStagePersRelLong() != null)
								&& (stagePersonValueDto.getCdStagePersRelLong().equals(CRELVICT_RC))) {
							stagePersonValueDto.setCdStagePersRelLong(CRPTRINT_OV);
							ismodified = true;
						}
						if (stagePersonValueDto.getCdStagePersRole().equals(CROLEALL_AR)) {
							stagePersonValueDto.setCdStagePersRole(CROLEALL_NO);
							ismodified = true;
						}
						if (ismodified) {
							finalizedList.add(stagePersonValueDto);
						}
					}
				} else {
					boolean ismodified = false;
					if ((stagePersonValueDto.getCdStagePersRelLong() != null)
							&& (stagePersonValueDto.getCdStagePersRelLong().equals(CRELVICT_RC))) {
						stagePersonValueDto.setCdStagePersRelLong(CRPTRINT_OV);
						ismodified = true;
					}
					if (stagePersonValueDto.getCdStagePersRole().equals(CROLEALL_AR)) {
						stagePersonValueDto.setCdStagePersRole(CROLEALL_NO);
						ismodified = true;
					}
					if (ismodified) {
						finalizedList.add(stagePersonValueDto);
					}
				}
			}
		}

		stageProgDao.updateStagePersonLinks(finalizedList);

		for (Map.Entry<Long, StagePersonValueDto> entry : mapINTStagePersonMap.entrySet()) {
			StagePersonValueDto stagePersonValueDto = (StagePersonValueDto) entry.getValue();
			stagePersonValueDto.setIdStage(Long.valueOf(newStageId));
			Timestamp currentTimeStamp = new Timestamp(Calendar.getInstance().getTime().getTime());
			stagePersonValueDto.setDtStagePersLink(currentTimeStamp);
		}
		stageProgDao.insertIntoStagePersonLinks(new ArrayList<StagePersonValueDto>(mapINTStagePersonMap.values()));

		List<AllegationDto> existingAllegations = arStageProgDao.fetchIntakeAllegations(intakeStage.getIdStage());

		List<List<AllegationDto>> allegationList = new ArrayList<>();
		if(intakeStages != null && !intakeStages.isEmpty()) {
			for (Long intakeStg : intakeStages) {
				List<AllegationDto> allegFromIntake = arStageProgDao.fetchIntakeAllegations(intakeStg);
				allegationList.add(allegFromIntake);
			}
		}
		List<AllegationDto> allegationFromNextIntake = allegationList.stream().flatMap(List::stream).collect(Collectors.toList());
		//removing duplicate Intake allegations.
		List <AllegationDto> allAllegations = removeDuplicateIntakeAllegations(existingAllegations,allegationFromNextIntake);

		/*
		 * Following Sequence generation is take care at the Entity level
		 * using @GeneratedValue
		 *
		 * for(AllegationDto allegationValueBean :existingAllegations){
		 * allegationValueBean.setIdAllegation(SequenceUtil.getSequence(
		 * ServiceConstants .TABLENAME)); }
		 */

		arStageProgDao.createInvestigationAllegations(Long.valueOf(newStageId), Long.valueOf(idCase),allAllegations);

		EventValueDto eventValueDto = new EventValueDto();
		eventValueDto.setIdStage(newStageId);
		eventValueDto.setCdEventType(CEVNTTYP_CCL);
		eventValueDto.setIdCase(Long.valueOf(idCase));
		eventValueDto.setIdPerson(Long.valueOf(idUser));
		eventValueDto.setEventDescr(INVESTIGATION_CONCLUSION_TEXT);
		eventValueDto.setCdEventStatus(CEVTSTAT_NEW);
		eventValueDto.setCdEventTask(INV_CONCLUSION_TASK_CODE);
		Long idEvent = Long.valueOf(arStageProgDao.createEvent(eventValueDto));

		arStageProgDao.insertCPSInvestigationDetail(Long.valueOf(idCase), Long.valueOf(newStageId),
				Long.valueOf(idUser), intakeStage.getDtStartDate(), idEvent);

		List<EventPersonDto> eventPersons = new ArrayList<>();
		List<SvcAuthEventLinkInDto> existingSvcAuthEvents = svcAuthDetailDao.getServiceAuthorizationEventDetails(
				Long.valueOf(idCase), Long.valueOf(idFromStage), AR_SERVICE_AUTH_TASK_CODE);
		if (!ObjectUtils.isEmpty(existingSvcAuthEvents)) {
			for (SvcAuthEventLinkInDto serviceAuthEventLinkValueBean : existingSvcAuthEvents) {
				EventValueDto eventValueDto1 = new EventValueDto();
				eventValueDto1.setIdStage(newStageId);
				eventValueDto1.setCdEventType(CEVNTTYP_AUT);
				eventValueDto1.setIdCase(Long.valueOf(idCase));
				eventValueDto1.setIdPerson(Long.valueOf(idUser));
				eventValueDto1.setEventDescr(serviceAuthEventLinkValueBean.getTxtEventDescription());
				eventValueDto1.setCdEventStatus(CEVTSTAT_APRV);
				eventValueDto1.setCdEventTask(INV_SERVICE_AUTH_TASK_CODE);
				int invServiceAuthEventID = arStageProgDao.createEvent(eventValueDto1);
				serviceAuthEventLinkValueBean.setSvcAuthEventId(Long.valueOf(invServiceAuthEventID));
				for (Long idPerson : serviceAuthEventLinkValueBean.getPersons()) {
					EventPersonDto eventPersonValueBean = new EventPersonDto();
					eventPersonValueBean.setIdpersonId(idPerson);
					eventPersonValueBean.setIdEvent(Long.valueOf(invServiceAuthEventID));
					eventPersonValueBean.setIdCase(Long.valueOf(idCase));
					eventPersons.add(eventPersonValueBean);
				}
			}
			serviceAuthorizationDao.insertIntoServiceAuthorizationEventLinks(existingSvcAuthEvents);
			serviceAuthorizationDao.insertIntoEventPersonLinks(eventPersons);
		}

		// dto's IncomingdiDto, IncomingDoDto (Info)
		IncomingDetail incomingDetail = new IncomingDetail();
		incomingDetail.setIdStage(intakeStage.getIdStage());
		IncomingDetail incomingDoDto = incomingDetailDao.getincomingDetailbyId(intakeStage.getIdStage());

		if ((idFromStage > Zero) || (!TypeConvUtil.isNullOrEmpty(incomingDoDto.getDtIncomingCall()))) {
			arStageProgDao.updatePriorStageAndIncomingCallDate(Long.valueOf(newStageId), Long.valueOf(idCase),
					Long.valueOf(idFromStage), new java.sql.Timestamp(incomingDoDto.getDtIncomingCall().getTime()));
		}

		StageValueBeanDto stageDtoDto = stageDao.retrieveStageInfo(newStageId);

		if (!isCPSSDMInvSafetyAssmt(DateUtils.toCastorDate((stageDtoDto.getDtStageStart())))) {
			ARSafetyAssmtValueDto safetyValueBean = arReportDao.getARSafetyAssmt(idFromStage,
					AR_INITIAL_SAFETY_ASSESSMENT_INDICATOR, idUser);
			if ((!TypeConvUtil.isNullOrEmpty(safetyValueBean))
					&& (CEVTSTAT_APRV.equals(safetyValueBean.getCdEventStatus()))) {

				SafetyAssessmentReq safetyAssessmentReq = new SafetyAssessmentReq();
				safetyAssessmentReq.setIdCase(Long.valueOf(idCase));
				safetyAssessmentReq.setIdStage(Long.valueOf(newStageId));
				safetyAssessmentReq.setIdUser(Long.valueOf(idUser));
				safetyAssessmentReq.setEventDesc(CARRY_OVER_EVENT_TEXT);
				stageProgressionService.addSafetyAssmt(safetyAssessmentReq);

				stageProgDao.updateINVSafetyAssignmentWithARSafetyAssignment(
						Long.valueOf(safetyValueBean.getIdArSafetyAssmt()), Long.valueOf(idCase),
						Long.valueOf(newStageId));

				BaseAddRiskAssmtValueDto baseAddRiskAssmtValueDto = new BaseAddRiskAssmtValueDto();
				baseAddRiskAssmtValueDto.setIdStage(newStageId.intValue());
				baseAddRiskAssmtValueDto.setIdCase(idCase);
				baseAddRiskAssmtValueDto.setIdPerson(idUser);
				baseAddRiskAssmtValueDto.setIdUser(idUser);
				stageProgressionService.addRiskAssmt(baseAddRiskAssmtValueDto);
			}
		}

		stageCreationValBean.setIdCase(Long.valueOf(idCase));
		stageProgDao.updateStageLink(stageCreationValBean, newStageId);

		return newStageId;
	}

	/**
	 * Removes duplicate allegations based on victim id , victim name, allegation prep and allegation type.
	 * @param
	 * @param
	 * @return allAllegations
	 */
	private List<AllegationDto> removeDuplicateIntakeAllegations(List<AllegationDto>...allegationLists) {
		Set<String> uniqueAllegations = new HashSet<>();
		List<AllegationDto> allAllegations = new ArrayList<>();
		for (List<AllegationDto> listAllegDto : allegationLists) {
			if (listAllegDto != null) {
				for (AllegationDto intakeAllegations : listAllegDto) {
					String key = intakeAllegations.getIdVictim() + "-" +
							intakeAllegations.getNmVictim() + "-" +
							intakeAllegations.getIdAllegedPerpetrator() + "-" +
							intakeAllegations.getCdAllegType();
					if (uniqueAllegations.add(key)) {
						allAllegations.add(intakeAllegations);
					}
				}
			}
		}
		return  allAllegations;
	}

	private boolean isCPSSDMInvSafetyAssmt(Date DtStageStart) {
		boolean isSDMSafetyAssmt = false;
		Date march15RelDt = null;
		;
		try {
			march15RelDt = new SimpleDateFormat("mm/dd/yyyy").parse("03/29/2015");
		} catch (ParseException e) {
			log.debug(e.getMessage());
		}
		if (null != DtStageStart && DateUtils.isAfter(DtStageStart, march15RelDt)) {
			isSDMSafetyAssmt = true;
		}
		return isSDMSafetyAssmt;
	}

	/**
	 *
	 * Method Name: closeARStage Method Description: Close AR Stage
	 * functionality.
	 *
	 * 01. Close the AR Stage 02. Add a event stating the A-R Stage is closed
	 * 03. Change PR to HP. Delete SE 04. Determine if Person is involved in any
	 * active stages. Else mark inactive 05. Eligibility 06: update and close
	 * Situation 07. Close Case. update CASE CAPS 08. Case Close Event 09.
	 * Delete all To do from the case 10. Case File Management. Insert a record
	 * into CaseFileManagement table 11. Record Retention
	 *
	 * @param idCase
	 * @param idFromStage
	 * @param idUser
	 * @param reasonCode
	 * @param idApprover
	 * @param isAnyStageOpen
	 * @return
	 */
	private Long closeARStage(int idCase, Long idFromStage, int idUser, String reasonCode, int idApprover,
							  boolean isAnyStageOpen) {

		StageClosureValueDto stageClosureValueDto = new StageClosureValueDto();
		stageClosureValueDto.setDtStageClosed((Calendar.getInstance()).getTime());
		stageClosureValueDto.setIdStage(idFromStage.intValue());
		stageClosureValueDto.setCdStageCloseRsn(reasonCode);
		arStageProgDao.closeStage(stageClosureValueDto);

		PostDto postDto = new PostDto();
		postDto.setIdStage(idFromStage);
		postDto.setCdEventType(CEVNTTYP_STG);
		postDto.setIdPerson(idUser);
		postDto.setCdTask(EMPTY_STRING);
		postDto.setReqFunctionCd(REQ_FUNC_CD_ADD);
		postDto.setTxtEventDescr(ALT_RESPONSE_STAGE_CLOSED);
		postDto.setCdEventStatus(CEVTSTAT_COMP);
		postDto.setDtDtEventOccurred((Calendar.getInstance()).getTime());
		postDto.setDtEventCreated((Calendar.getInstance()).getTime());

		ServiceInputDto archInputStructDto = new ServiceInputDto();
		archInputStructDto.setCreqFuncCd(REQ_FUNC_CD_ADD);
		postDto.setArchInputStructDto(archInputStructDto);

		SynchronizationServiceDto rowCcmn01UiG00 = new SynchronizationServiceDto();
		rowCcmn01UiG00.setIdEvent(postDto.getIdEvent());
		rowCcmn01UiG00.setIdStage(postDto.getIdStage());
		rowCcmn01UiG00.setIdPerson(postDto.getIdPerson());
		rowCcmn01UiG00.setCdTask(postDto.getCdTask());
		rowCcmn01UiG00.setCdEventType(postDto.getCdEventType());
		rowCcmn01UiG00.setEventDescr(postDto.getTxtEventDescr());
		rowCcmn01UiG00.setDtEventOccurred(postDto.getDtDtEventOccurred());
		rowCcmn01UiG00.setCdEventStatus(postDto.getCdEventStatus());
		rowCcmn01UiG00.setDtLastUpdate(postDto.getEventLastUpdate());
		postDto.setRowCcmn01UiG00(rowCcmn01UiG00);

		postEventService.PostEvent(postDto);

		List<String> requestedRoles = new ArrayList<>();
		requestedRoles.add(CROLEALL_PR);
		requestedRoles.add(CROLEALL_SE);
		List<StagePersonValueDto> staffPersonList = stageProgDao.selectStagePersonLink(idFromStage.intValue(),
				requestedRoles);

		for (StagePersonValueDto staffValueBean : staffPersonList) {
			if (CROLEALL_PR.equals(staffValueBean.getCdStagePersRole())) {
				staffValueBean.setCdStagePersRole(CROLEALL_HP);
				arStageProgDao.updateStagePersonLink(staffValueBean);
			} else if (CROLEALL_SE.equals(staffValueBean.getCdStagePersRole())) {
				stageDao.deleteStagePersonLink(staffValueBean);
			}
		}

		List<String> arRolesList = new ArrayList<>();
		arRolesList.add(CROLEALL_AR);
		arRolesList.add(CROLEALL_NO);
		arRolesList.add(CROLEALL_AP);
		arRolesList.add(CROLEALL_VC);
		arRolesList.add(CROLEALL_VP);
		arRolesList.add(CROLEALL_UK);
		List<StagePersonValueDto> arStagePersonList = stageProgDao.selectStagePersonLink(idFromStage.intValue(),
				arRolesList);

		ArrayList<Long> inactiveList = new ArrayList<>();
		for (StagePersonValueDto arPersonValueBean : arStagePersonList) {
			List<Long> stageList = workLoadDao.getActiveStagesForPerson(arPersonValueBean.getIdPerson());
			if (!stageList.isEmpty() && (stageList.contains(Long.valueOf(idFromStage)))) {
				inactiveList.add(arPersonValueBean.getIdPerson());
			}
		}
		if (!ObjectUtils.isEmpty(inactiveList)) {
			personDao.makePersonsInactive(inactiveList);
		}

		// Close Case if no other stage open
		if (!isAnyStageOpen) {
			StageValueBeanDto stageDtoDto = stageDao.retrieveStageInfo(idFromStage);
			situationDao.closeSituation(stageDtoDto.getIdSituation());

			caseDao.closeCase(Long.valueOf(idCase));

			PostDto caseClosePostDto = new PostDto();
			caseClosePostDto.setIdStage(idFromStage);
			caseClosePostDto.setCdEventType(CEVNTTYP_CAS);
			caseClosePostDto.setIdPerson(idUser);
			caseClosePostDto.setCdTask(EMPTY_STRING);
			caseClosePostDto.setReqFunctionCd(REQ_FUNC_CD_ADD);
			caseClosePostDto.setTxtEventDescr(CASE_CLOSED);
			caseClosePostDto.setCdEventStatus(CEVTSTAT_COMP);
			caseClosePostDto.setDtDtEventOccurred((Calendar.getInstance()).getTime());
			caseClosePostDto.setDtEventCreated((Calendar.getInstance()).getTime());

			archInputStructDto = new ServiceInputDto();
			archInputStructDto.setCreqFuncCd(REQ_FUNC_CD_ADD);
			caseClosePostDto.setArchInputStructDto(archInputStructDto);

			rowCcmn01UiG00 = new SynchronizationServiceDto();
			rowCcmn01UiG00.setIdEvent(caseClosePostDto.getIdEvent());
			rowCcmn01UiG00.setIdStage(caseClosePostDto.getIdStage());
			rowCcmn01UiG00.setIdPerson(caseClosePostDto.getIdPerson());
			rowCcmn01UiG00.setCdTask(caseClosePostDto.getCdTask());
			rowCcmn01UiG00.setCdEventType(caseClosePostDto.getCdEventType());
			rowCcmn01UiG00.setEventDescr(caseClosePostDto.getTxtEventDescr());
			rowCcmn01UiG00.setDtEventOccurred(caseClosePostDto.getDtDtEventOccurred());
			rowCcmn01UiG00.setCdEventStatus(caseClosePostDto.getCdEventStatus());
			rowCcmn01UiG00.setDtLastUpdate(caseClosePostDto.getEventLastUpdate());
			caseClosePostDto.setRowCcmn01UiG00(rowCcmn01UiG00);
			postEventService.PostEvent(caseClosePostDto);

			todoDao.deleteTodosForACase(Long.valueOf(idCase));

			CaseFileManagementDto inputCaseFileMgmtValueBean = new CaseFileManagementDto();
			inputCaseFileMgmtValueBean.setIdCaseFileCase(Long.valueOf(idCase));
			CaseFileManagementDto caseFileManagementDto = approvalStatusDao
					.getSelectCaseFileManagement(inputCaseFileMgmtValueBean);
			if (TypeConvUtil.isNullOrEmpty(caseFileManagementDto)) {
				CaseFileManagementDto caseFileMgmtValueBean = new CaseFileManagementDto();
				caseFileMgmtValueBean.setIdCaseFileCase(Long.valueOf(idCase));
				caseFileMgmtValueBean.setIdOffice(employeeDao.getEmployeeOfficeIdentifier(Long.valueOf(idUser)));
				caseFileMgmtValueBean.setIdUnit(stageDtoDto.getIdUnit());
				caseFileMgmtValueBean.setCdCaseFileOfficeType(CD_FILE_OFFICE_TYPE_PRS);
				// In Dao layer we fetching case file address, city and all things
				// but
				// i found we r no where setting the these values. Need check in
				// Legacy
				approvalStatusDao.insertCaseFileManagement(caseFileMgmtValueBean);
			}

			Date finalRecordRetentionDate = arStageProgDao.fetchRecordRetentionDate();
			if (!TypeConvUtil.isNullOrEmpty(finalRecordRetentionDate)) {
				arStageProgDao.createRecordRetentionRecord(idCase, CRECRETN_TYPE_ARR,
						finalRecordRetentionDate, finalRecordRetentionDate);
			}
		}
		return idFromStage;
	}

	/**
	 * Method Name: postProcessing Method Description: Find if a Event exist for
	 * AR Closure Safety Assessment and then set it to COMP Status
	 *
	 * @param newStageID
	 * @param idUser
	 */
	private void postProcessing(Long newStageID, int idUser) {
		ARSafetyAssmtValueDto arSafetyAssmtValueDto = approvalStatusDao.getARSafetyAssmt(newStageID.intValue(),
				AR_CLOSURE_SAFETY_ASSESSMENT_INDICATOR, idUser);
		if (!TypeConvUtil.isNullOrEmpty(arSafetyAssmtValueDto)
				&& arSafetyAssmtValueDto.getIdEvent() > Zero) {
			approvalStatusDao.updateEventStatus(arSafetyAssmtValueDto.getIdEvent(), CEVTSTAT_COMP);
		}
	}

	/**
	 *
	 * Method Name: checkRegionChange Method Description: check if region for
	 * the child is changed
	 *
	 * @param idEvent
	 * @param idCase
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public String checkRegionChange(Long idEvent, Long idCase) {
		Placement placement = placementDao.retrievePlacementByEventId(idEvent);
		String placementRegion = null;
		if (!ObjectUtils.isEmpty(placement)) {
			Long idPerson = placement.getPersonByIdPlcmtChild().getIdPerson();
			LegalStatusDetailDto legalStatusDetailDto = legalStatusDao.getLatestLegalStatus(idPerson, idCase);
			if (!ObjectUtils.isEmpty(legalStatusDetailDto)) {
				String legalCounty = legalStatusDetailDto.getCdLegalStatCnty();
				String region = caseSummaryDao.getRegionByCounty(legalCounty);
				if (!ObjectUtils.isEmpty(placement.getAddrPlcmtCnty())) {
					// Compare placement region with legal status region. If it
					// is different, return placement region
					String regionForPlacement = caseSummaryDao.getRegionByCounty(placement.getAddrPlcmtCnty());
					if (!ObjectUtils.isEmpty(region) && !ObjectUtils.isEmpty(regionForPlacement)
							&& !region.equals(regionForPlacement))
						placementRegion = regionForPlacement;
				}

			}
		}

		return placementRegion;
	}

	/**
	 * Method Name: isSecondLevelApproverReqdforReunificaiton Method
	 * Description: Method returns if the 2nd level approver required for
	 * approving SDM Reunification Assessment.
	 *
	 * @param idEvent
	 * @return secondAprvrReqd
	 */
	private Boolean isSecondLevelApproverReqdforReunificaiton(Long idEvent) {
		Boolean secondAprvrReqd = Boolean.FALSE;
		// Check if any child for the Reunification has Rcommendation summary
		// Return Home, Then 2nd level approver is
		// Required for approving the SDM Reunification Assessment.
		secondAprvrReqd = sdmReunificationDao.anyChildWithRtnHome(idEvent);
		return secondAprvrReqd;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public Long getICPCApprovalLevel(Long idEvent) {
		return approvalStatusDao.getICPCApprovalLevel(idEvent);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<ApproversDto> getapproversdtoList(Long idApproval) {
		return approvalStatusDao.getapproversdtoList(idApproval);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public ApprovalStatusRes isApproverLoggedIn(Long idApproval) {
		return approvalStatusDao.isApproverLoggedIn(idApproval);
	}

	/**
	 * Method Name: getBoardEmail Method Description: This method retrieves
	 * board member's email addresses based on placement county.
	 *
	 * @param idCase,
	 * @param nmStage
	 * @return List<String>
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<String> getBoardEmail(Long idCase, String nmStage) {
		return approvalStatusDao.getBoardEmail(idCase, nmStage);
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: generateAlertIfStageOpen
	 * Method Description: This method is used to generate alerts for the open Prior or Progressed stage based on the
	 * given cdStage and idStage
	 *
	 * @param idStage
	 * @param cdStage
	 * @param idCase
	 * @param idUser
	 * @param isFbssSdmRa
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void generateAlertIfStageOpen(Long idStage, String cdStage, Long idCase, Long idUser, boolean isFbssSdmRa) {

		String shortDesc = null;
		String longDesc = null;

		List<Long> openStageIds = fetchCpsPriorOrProgressedStage(idStage, cdStage);

		if (!CollectionUtils.isEmpty(openStageIds)) {

			if (isFbssSdmRa) {
				// Retrieve the SDM Risk Reassessment
				SDMRiskReasmntDto sdmRiskReasmntDto = approvalStatusDao.retrieveFbssSdmRiskReassessment(idStage);

				if (!ObjectUtils.isEmpty(sdmRiskReasmntDto) && !ObjectUtils.isEmpty(sdmRiskReasmntDto.getIdHshldAssessed())) {
					// Retrieve the Household Person details
					PersonDto personDto = personDao.getPersonById(sdmRiskReasmntDto.getIdHshldAssessed());

					shortDesc = FBSS_SDM_RA_APPROVED_SHORT;
					longDesc = String.format(FBSS_SDM_RA_APPROVED_LONG, sdmRiskReasmntDto.getDtAsmnt(),
							personDto.getNmPersonFull(), sdmRiskReasmntDto.getCdRiskLevelDiscOvrride(),
							sdmRiskReasmntDto.getCdPlndActn());
				}
			} else {
				switch (cdStage) {
					case CSTAGES_AR:
						shortDesc = FBSS_AR_CLOSURE_SHORT;
						longDesc = FBSS_AR_CLOSURE_LONG;
						break;
					case CSTAGES_INV:
						shortDesc = FBSS_INV_CLOSURE_SHORT;
						longDesc = FBSS_INV_CLOSURE_LONG;
						break;
					default:
						shortDesc = FBSS_FPR_CLOSURE_SHORT;
						longDesc = FBSS_FPR_CLOSURE_LONG;
				}
			}

			if (!StringUtils.isEmpty(shortDesc) && !StringUtils.isEmpty(longDesc)) {
				// Generates the alerts to Primary & Secondary staff for open stages
				for (Long stageId : openStageIds) {
					List<Long> caseWorkerPersonIdList  = workLoadDao.getAssignedWorkersForStage(stageId);
					for (Long idPerson : caseWorkerPersonIdList) {
						alertService.createFbssAlert(stageId, idPerson, null, idCase, longDesc, shortDesc);
					}
				}
			}
		}
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: fetchCpsPriorOrProgressedStage
	 * Method Description: This method is used to fetch the CPS prior or progressed stage id's for the given stage
	 *
	 * @param idStage
	 * @param cdStage
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Long> fetchCpsPriorOrProgressedStage(Long idStage, String cdStage) {

		List<Long> openStageIds = null;
		List<StageDto> stageDTOs = approvalStatusDao.fetchCpsPriorOrProgressedStage(idStage, cdStage, false);

		if (!CollectionUtils.isEmpty(stageDTOs)) {

			// Predicate on Stage date closed
			Predicate<StageDto> datePredicate = stageDto -> !ObjectUtils.isEmpty(stageDto.getDtStageClose())
					&& GENERIC_END_DATE.compareTo(stageDto.getDtStageClose()) != 0;

			if (CSTAGES_FPR.equals(cdStage)) {
				// Filters all the closed AR stages, to check for progressed INV stage
				List<StageDto> filteredARStageDTOs = stageDTOs.stream().filter(stageDto -> CSTAGES_AR.
						equals(stageDto.getCdStage()) && datePredicate.test(stageDto))
						.collect(Collectors.toList());

				// To retrieve the INV stage if the Stage progressed
				if (!CollectionUtils.isEmpty(filteredARStageDTOs)) {
					filteredARStageDTOs.forEach(stageDto -> {

						List<StageDto> invStageDTOs = approvalStatusDao.
								fetchCpsPriorOrProgressedStage(stageDto.getIdStage(), CSTAGES_AR, true);

						if (!CollectionUtils.isEmpty(invStageDTOs)) {
							stageDTOs.addAll(invStageDTOs);
						}
					});
				}
			}

			// Filter only the open stages
			openStageIds = stageDTOs.stream().filter(datePredicate.negate()).map(StageDto::getIdStage)
					.distinct().collect(Collectors.toList());
		}

		return openStageIds;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: getCpsProgressedStageForSelectedStage
	 * Method Description: This method is used to retrieve the Progressed Stage ID
	 *
	 * @param idStage
	 * @param cdStage
	 * @param stageType
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getCpsProgressedStageForSelectedStage(Long idStage, String cdStage, String stageType) {

		Long idProgressedStage = null;
		boolean retrieveProgressedInv = false;
		if (CSTAGES_AR.equals(cdStage) && CSTAGES_INV.equals(stageType)) {
			retrieveProgressedInv = true;
		}

		List<StageDto> stageDTOs = approvalStatusDao.fetchCpsPriorOrProgressedStage(idStage, cdStage, retrieveProgressedInv);

		if (!CollectionUtils.isEmpty(stageDTOs)) {

			StageDto stageDto = stageDTOs.stream().filter(dto -> stageType.equals(dto.getCdStage())).findFirst()
					.orElse(null);
			idProgressedStage = !ObjectUtils.isEmpty(stageDto) ? stageDto.getIdStage() : null;
		}

		return idProgressedStage;
	}

	/**
	 * This method will update the EMR status in DB.
	 *
	 * @param emrStatus
	 * @param idStage
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public Long updateEmrStatusForSelectedStage(String emrStatus, Long idStage) {

		return approvalStatusDao.updateEmrStatus(emrStatus, idStage);
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: cpsCopyOpenServiceAuth
	 * Method Description: This method is used for CPS - INV/A-R to copy open Service Auth to FPR
	 *
	 * @param idStage
	 * @param cdStage
	 * @param idUser
	 * @return
	 */
	@Override
	@Transactional
	public void cpsCopyOpenServiceAuth(Long idStage, String cdStage, Long idUser) {

		List<StageDto> stageDTOs = approvalStatusDao.fetchCpsPriorOrProgressedStage(idStage, cdStage, false);

		if (!CollectionUtils.isEmpty(stageDTOs) && stageDTOs.stream()
				.anyMatch(dto -> CSTAGES_FPR.equals(dto.getCdStage()))) {

			StageDto fprStageDTO = stageDTOs.stream().filter(dto -> CSTAGES_FPR.equals(dto.getCdStage()))
					.findFirst().orElse(null);

			if (!ObjectUtils.isEmpty(fprStageDTO) && Arrays.asList(CSTAGES_INV,
					CSTAGES_AR).contains(cdStage)) {

				serviceAuthFormService.copyOpenServiceAuthToNewStage(idStage, fprStageDTO.getIdStage(),
						idUser, FPR_SERVICE_AUTH_TASK_CODE);
			}

		}
	}

	/**
	 * Artifact ID: artf164464
	 * Method Name: checkPCSPOpenOnApproval
	 * Method Description: This method checks if there is an open PCSP in the case and there are no other PCSP
	 * applicable stages (INV, A-R, FPR, FSU, FRE) open in the case
	 *
	 * @param idCase
	 * @param idStage
	 * @param cdStage
	 * @return
	 */
	@Override
	@Transactional
	public boolean checkPCSPOpenOnApproval(Long idCase, Long idStage, String cdStage) {

		boolean isPCSPOpen = false;
		boolean eligibleToCheckPcsp = true;

		// For A-R Stage, this validation only applies when using the ‘FPR/FBSS’ Closure Reason
		if (CSTAGES_AR.equals(cdStage)) {

			ArInvCnclsnDto arInvCnclsnDto = arHelperDao.selectARConclusion(idStage);

			if (!ObjectUtils.isEmpty(arInvCnclsnDto)
					&& !CCLOSAR_060.equals(arInvCnclsnDto.getCdAROverallDisposition())) {
				eligibleToCheckPcsp = false;
			}
		}

		// Retrieves if there is an open PCSP in the case and there are no other PCSP applicable stages
		if (eligibleToCheckPcsp) {
			isPCSPOpen = pcspListPlacmtDao.hasOpenPCSPlacement(idCase)
					&& !pcspListPlacmtDao.hasOtherStagesOpen(idCase, cdStage);
		}

		return isPCSPOpen;
	}
	//[artf172959] Defect: 16737 - AR closure reasons close case with open stage
	private boolean isAnyStageOpenForCase(int idCase) {
		List<String> openStageCodesForCase = approvalStatusDao.getOpenStagesForCase((long) idCase);
		boolean isAnyStageOpenForCase = false;
		if (!CollectionUtils.isEmpty(openStageCodesForCase)) {
			isAnyStageOpenForCase = openStageCodesForCase.stream().anyMatch(cdStage ->
					!A_R_STAGE.equals(cdStage));
		}
		return isAnyStageOpenForCase;
	}



	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteTodosByTaskAndCase(Long idCase, String taskCode) {

		todoDao.deleteTodosByTaskAndCase(idCase,taskCode);
	}



	/**
	 * <p>  Added by kanakas</p>
	 * <p> Method to determine if a case is using new hb4 method. </p>
	 * @param dtStageStart
	 * @return isHB4KinAssessment
	 */
	private boolean isHB4KinAssessment(Date dtStageStart) {
		List<CodeAttributes> allKinDateCodes = codesDao.getCodesTable(CRELDATE, CRELDATE_SEP_2018_IMPACT);

		Optional<CodeAttributes> optionalExistingKinDateCode = allKinDateCodes.stream()
				.filter(codeDto -> codeDto.getCode().equals(CRELDATE_SEP_2018_IMPACT)).findFirst();
		if (optionalExistingKinDateCode.isPresent()) {
			try {
				return !ObjectUtils.isEmpty(dtStageStart) &&
						dtStageStart.after(new SimpleDateFormat(FORMAT_MMDDYYYY).parse(optionalExistingKinDateCode.get().getDecode()));
			} catch (ParseException e) {
				log.error("Kin Assessment Date: {0}", e);
			}
		}
		return false;
	}

	/**
	 * SR 45217 - HB4 Long Term Solution
	 * @param homeInfoDto
	 */
	public void updateServiceAuth(KinHomeInfoDto homeInfoDto) {
		if (!ObjectUtils.isEmpty(homeInfoDto.getIdHomeResource())) {
			List <KinChildDto> childList = homeInfoDto.getKinChildVBList();
			if (CollectionUtils.isEmpty(childList)){
				childList = placementDao.getAllChildPlacementsId(homeInfoDto.getIdHomeResource());
			}

			if (!CollectionUtils.isEmpty(childList)) {
				childList.stream().forEach(kinChildDto -> {
					if (ObjectUtils.isEmpty(kinChildDto.getPaymentEligibilityStatus()) ||
							BOOLEAN_FALSE.equalsIgnoreCase(kinChildDto.getPaymentEligibilityStatus())) {
						Long contractId = contractDao.getIdContractForResourceId(homeInfoDto.getIdHomeResource());

						if (!ObjectUtils.isEmpty(contractId)) {
							serviceAuthorizationDao.updateServiceAuthDetails(homeInfoDto, contractId,
									CSVCCODE_68O, kinChildDto);
							serviceAuthorizationDao.updateServiceAuthDetails(homeInfoDto, contractId,
									CSVCCODE_68P, kinChildDto);
						}
					}
				});
			}
		}
	}

	/**
	 * SR 45217     HB4 Long Term Solution
	 * @param homeInfoDto
	 * @throws Exception
	 */
	public void determinePaymentEligibility(KinHomeInfoDto homeInfoDto) {
		List<KinChildDto> kinChildVBList = new ArrayList<KinChildDto>();

		boolean childEligibleCourtOrdered = false;
		String childEligible = null;
		String cdpaymentElig = null;
		boolean atleastOnechildElig = false;
		boolean atleastOneChildEligibleCourtOrdered = false;
		Date paymentEligEndDate = null;
		//artf112416 - Service Auths generating wrong dates and resource for multiple kids.
		boolean isCaregiverMeetsQual = false;

		String isCaregiverElig = homeInfoDto.getKinCaregiverEligStatusCode();
		String isCourtOrderedPayment = homeInfoDto.getIsPaymentCourtOrdered();
		String indIncomeQualification = homeInfoDto.getIsHouseholdMeetFPL();
		String hasBegunTraining = homeInfoDto.getHasBegunTraining();

		String indSignedAgreement = homeInfoDto.getResourceSignedAgreement();
		//artf130531 - Placement was closed then Home information page was approved--SAD was not system Termed
		List<Long> childIdList = kinHomeInfoDao.getChildrensPid(homeInfoDto.getIdHomeResource());

		for (Long idChild  : childIdList) {
			KinChildDto child =	placementDao.getPlacementInfo(idChild);
			//artf130775 - to remove null pointer
			if (!ObjectUtils.isEmpty(child)) {
				kinChildVBList.add(child);
			}
		}

		List <KinChildDto>  updatedKinChildVBList = new ArrayList();
		//artf112416
		if ((CKNELGST_APRV.equalsIgnoreCase(isCaregiverElig ) || CKNELGST_CTOR.equalsIgnoreCase(isCaregiverElig))
				&& (!ObjectUtils.isEmpty(indIncomeQualification) && YES.equalsIgnoreCase(indIncomeQualification) )
				&& (!ObjectUtils.isEmpty(indSignedAgreement) && (YES.equalsIgnoreCase(indSignedAgreement)))
				&& (!ObjectUtils.isEmpty(hasBegunTraining) && (YES.equalsIgnoreCase( hasBegunTraining)))) {
			isCaregiverMeetsQual = true;
		} else {
			isCaregiverMeetsQual = false;
		}
		int count = 0;

		for (KinChildDto childBean : kinChildVBList) {
			Date legalStatusStartDate = null;
			int age = 0;
			Long id = null;
			boolean childAlive = true;
			String legalStatus = null;
			boolean hasApprovedPlacement = false;
			if (childBean!= null) {
				id = childBean.getChildId();
			}
			hasApprovedPlacement = placementDao.getHasApprovedPlacementForAChild(homeInfoDto.getIdHomeResource(), childBean.getChildId());
			age = childBean.getAge();
			count++;
			KinChildDto kcValueBean = null;
			List<KinChildDto> legalStatusList = kinHomeInfoDao.getLegalStatusInfo(childBean.getChildId());
			if(!ObjectUtils.isEmpty(legalStatusList)) {
				kcValueBean = legalStatusList.get(0);
			}
			if(!ObjectUtils.isEmpty(kcValueBean)) {
				legalStatus = kcValueBean.getCdLegalStatus();

				if (CLEGSTAT_090.equalsIgnoreCase(legalStatus)
						|| CLEGSTAT_100.equalsIgnoreCase(legalStatus)
						|| CLEGSTAT_120.equalsIgnoreCase(legalStatus)
						|| CLEGSTAT_150.equalsIgnoreCase(legalStatus)) {
					legalStatusStartDate = kcValueBean.getLegalStatusStatDate();
				}
			}

			if (childBean.getDateOfDeath() != null) {
				childAlive = false;
			}
			//artf112416
			if (hasApprovedPlacement && childAlive && legalStatus != null &&
					(legalStatusStartDate == null)  && (YES.equalsIgnoreCase(isCourtOrderedPayment))
					&& (age < 18 ) && (CPLMNTYP_090.equalsIgnoreCase(childBean.getPlacementTypeCode()))
					&& (childBean.getPlacementEndDate() != null &&
					(DateHelper.isAfter(childBean.getPlacementEndDate(), new Date())))) {
				childEligible = YES;
				atleastOnechildElig = true;
				childEligibleCourtOrdered = true;
				atleastOneChildEligibleCourtOrdered = true;
				childBean.setPaymentEligibilityStatus(childEligible);
				childBean.setPaymentEligibilityStartDate(homeInfoDto.getCourtOrderedPaymentDate());
			} else if(hasApprovedPlacement &&  isCaregiverMeetsQual && childAlive
					&& legalStatus != null &&  (legalStatusStartDate == null )
					&& (age < 18) &&  (CPLMNTYP_090.equalsIgnoreCase(childBean.getPlacementTypeCode()))
					&& ( childBean.getPlacementEndDate() != null
					&& (DateHelper.isAfter(childBean.getPlacementEndDate(), new Date())))) {
				childEligible = YES;
				childBean.setPaymentEligibilityStatus(childEligible);
				atleastOnechildElig = true;
			} else {
				childEligible = NO;
				childBean.setPaymentEligibilityStatus(childEligible);
			}

			// to calculate payment elig start Date
			if (!childEligibleCourtOrdered && StringHelper.BOOLEAN_TRUE.equalsIgnoreCase(childEligible)) {
				childBean.setPaymentEligibilityStartDate(calculatePayEligStartDate(homeInfoDto,
						childBean.getPlacementStartDate()));
			}

			//if the child is not eligible for payment then calculate payment eligibility end date
			if (StringHelper.BOOLEAN_FALSE.equalsIgnoreCase(childEligible)) {
				//to calculate payment elig end Date

				if (legalStatusStartDate == null && childBean.getPlacementEndDate() != null
						&&  childBean.getPlacementEndDate() != DateHelper.MAX_JAVA_DATE) {
					paymentEligEndDate = childBean.getPlacementEndDate();
				} else if (childBean.getPlacementEndDate() != null
						&& !DateHelper.isAfterToday(childBean.getPlacementEndDate())
						&& DateHelper.isBefore(childBean.getPlacementEndDate(), legalStatusStartDate)) {
					paymentEligEndDate = childBean.getPlacementEndDate();
				} else {
					paymentEligEndDate = legalStatusStartDate;
				}

				if (paymentEligEndDate == null ) {
					paymentEligEndDate = new Date();
				}

				if (paymentEligEndDate != null &&
						DateHelper.isoFormat.format(paymentEligEndDate).equalsIgnoreCase(
								DateHelper.isoFormat.format(DateHelper.MAX_JAVA_DATE))){
					paymentEligEndDate = new Date();
				}
				childBean.setPaymentEligibilityEndDate(paymentEligEndDate);
			}
			childBean.setAge(age);
			childBean.setCdLegalStatus(legalStatus);
			//artf112416
			if(!ObjectUtils.isEmpty(kcValueBean)) {
				childBean.setLegalStatusStatDate(kcValueBean.getLegalStatusStatDate());
			}
			updatedKinChildVBList.add(childBean);
		}

		if (atleastOnechildElig  && atleastOneChildEligibleCourtOrdered) {
			cdpaymentElig =  CKNPYELG_CTOR;
			homeInfoDto.setKinCaregiverPaymentEligStatusCode(cdpaymentElig);
			homeInfoDto.setPaymentStartDate(homeInfoDto.getCourtOrderedPaymentDate());

		} else if (CKNELGST_APRV.equalsIgnoreCase(isCaregiverElig ) && atleastOnechildElig
				&& (indIncomeQualification != null &&  YES.equalsIgnoreCase(indIncomeQualification))
				&& (indSignedAgreement != null && (YES.equalsIgnoreCase(indSignedAgreement)))
				&& (hasBegunTraining != null && (YES.equalsIgnoreCase( hasBegunTraining) ))) {
			cdpaymentElig =  CKNPYELG_ELIG;
			homeInfoDto.setKinCaregiverPaymentEligStatusCode(cdpaymentElig);
			homeInfoDto.setPaymentStartDate(calculatePayEligStartDate(homeInfoDto, null));
		} else {
			cdpaymentElig =  CKNPYELG_NELG;
			homeInfoDto.setKinCaregiverPaymentEligStatusCode(cdpaymentElig);
			homeInfoDto.setPaymentStartDate(null);
		}

		homeInfoDto.setKinChildVBList(updatedKinChildVBList);
	}

	/**
	 * SR45217     HB4 Long Term Solution SQLs
	 * @param homeInfoDto
	 * @param contractId
	 * @param stageId
	 */
	public void saveServiceAuth(KinHomeInfoDto homeInfoDto, Long contractId, Long stageId){
		boolean isTanf = false;
		Long idEvent = null;
		double  ratefor68O = 0.0;
		double  ratefor68P = 0.0;
		int numUnitsUsedFor68O = 0;
		int numUnitsRequestedFor68O = 0;
		int numUnitsRemaining68O = 0;
		double numUnitsRequestedNew68ODouble = 0;
		int numUnitsRequestedNew68O = 0;
		int totalUnitsUsedfor68O = 0;
		double maxUnitsDouble = 0;
		int maxUnits = 0;
		int numUnitsUsedFor68P = 0;
		int numUnitsRequestedFor68P = 0;
		int totalUnitsUsedfor68P = 0;
		int totalUnitsRemaining68P = 0;

		int pendingUnits68O = 0;
		int pendingUnits68P = 0;
		int totalNumOfUnitsUsed = 0;

		Long idServiceauth = null;
		int totalMonthsFor68O = 0;
		int totalMonthsForNonTanf68P = 0;
		int totalMonthsForTanf68P = 0;

		Date startingDate = null;
		Long idCaseFromSAEvent = null;

		Date maxTermDate = null;
		Date paymentStartDateFromChild = null;
		Date paymentStartDate = null;
		String numMnthsFor68O = null;
		String numMnthsNonTanf68P = null;
		String numMnthsFor68P = null;
		int lineItem = 0;
		Long subStageId = null;
		Long caseId = null;
		boolean hasOpen68OExists = false;
		boolean hasOpen68PExists = false;
		boolean hasClosed68OExists = false;
		boolean hasClosed68PExists = false;
		boolean has68OPaidFull = false;

		ratefor68O = kinHomeInfoDao.getFosterCareRate(CSVCCODE_68O, homeInfoDto.getPaymentStartDate());
		ratefor68P = kinHomeInfoDao.getFosterCareRate(CSVCCODE_68P, homeInfoDto.getPaymentStartDate());
		numMnthsFor68O = kinHomeInfoDao.getKinshipConstants(TANF_MONTHS_68O);
		numMnthsNonTanf68P = kinHomeInfoDao.getKinshipConstants(NON_TANF_MONTHS_68P);
		numMnthsFor68P = kinHomeInfoDao.getKinshipConstants(TANF_MONTHS_68P);

		Long placementsAdultId = placementDao.getPlacementsAdultId(homeInfoDto.getIdHomeResource());

		/*
		 * artf251080 : Kinship Case will NOT Approve
		 * if placementsAdultId was not found, than get primary caregiver for the given stage id
		 */
		if(TypeConvUtil.isNullOrEmpty(placementsAdultId)){
			placementsAdultId = stagePersonLinkDao.getPrimaryCareGiverbyStage(stageId);
		}

		if (numMnthsFor68O != null) {
			totalMonthsFor68O = Integer.parseInt(numMnthsFor68O);
		}
		if (numMnthsFor68P != null) {
			totalMonthsForTanf68P = Integer.parseInt(numMnthsFor68P);
		}
		if (numMnthsNonTanf68P != null) {
			totalMonthsForNonTanf68P = Integer.parseInt(numMnthsNonTanf68P);
		}
		KinHomeInfoDto savedBean = kinHomeInfoService.getKinHomeInfo(homeInfoDto.getIdHomeStage());
		savedBean.setUserId(homeInfoDto.getUserId());

		//		service auth inserts logic starts
		List <KinChildDto> childList = homeInfoDto.getKinChildVBList();

		String livingArrangement = null;

		for (KinChildDto childBean  : childList) {
			numUnitsUsedFor68O = 0;
			numUnitsRequestedFor68O = 0;
			numUnitsRemaining68O = 0;
			numUnitsRequestedNew68ODouble = 0;
			totalUnitsUsedfor68O = 0;
			numUnitsUsedFor68P = 0;
			numUnitsRequestedFor68P = 0;
			totalUnitsUsedfor68P = 0;
			totalNumOfUnitsUsed = 0;

			if (StringHelper.BOOLEAN_TRUE.equalsIgnoreCase(childBean.getPaymentEligibilityStatus())) {

				livingArrangement = kinHomeInfoDao.getChildLivingArrangement(childBean.getChildId());
				paymentStartDateFromChild = kinHomeInfoDao.getPaymentStartDate(childBean.getPlacementEventId(), homeInfoDto.getIdHomeResource());

				if (CSTATE_TX.equalsIgnoreCase(savedBean.getResourceAddressState()) &&
						(CPLLAFRM_DD.equalsIgnoreCase(livingArrangement)
								|| CPLLAFRM_DQ.equalsIgnoreCase(livingArrangement) )) {
					isTanf = true;
				} else {
					isTanf = false;
				}

				subStageId = eventDao.getSubStageId(childBean.getPlacementEventId());
				caseId = eventDao.getCaseId(childBean.getPlacementEventId());

				if (subStageId == 0) {
					subStageId = stageId;
				}

				Date maxLegalStatusDate = legalStatusDao.getMaxLegalStatusDate(childBean.getChildId());
				KinChildDto childBeanLegalInfo = null;
				if (maxLegalStatusDate != null) {
					childBeanLegalInfo = legalStatusDao.getPlacementLegalStatusInfoWithDate(childBean.getChildId(), maxLegalStatusDate);
				} else {
					childBeanLegalInfo = placementDao.getPlacementLegalStatusInfo(childBean.getChildId());
				}

				/*get total units used for 68O which has term date before sysdate
				  artf112123 Svc Auth detail with excessive days added to already-paying caregivers*/
				if (childBeanLegalInfo.getLegalStatusStatDate() != null && childBean.getChildId() != null) {

					numUnitsUsedFor68O = serviceAuthorizationDao.getServiceAuthUnitsUsed(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(), CSVCCODE_68O, true);
					numUnitsRequestedFor68O = serviceAuthorizationDao.getServiceAuthUnitsRequested(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(), CSVCCODE_68O,false);
					numUnitsUsedFor68P = serviceAuthorizationDao.getServiceAuthUnitsUsed(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(), CSVCCODE_68P, true);
					numUnitsRequestedFor68P = serviceAuthorizationDao.getServiceAuthUnitsRequested(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(), CSVCCODE_68P, false);

					hasOpen68OExists = serviceAuthorizationDao.getServiceAuthDtlOpenExists(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(),  CSVCCODE_68O );
					hasOpen68PExists = serviceAuthorizationDao.getServiceAuthDtlOpenExists(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(),  CSVCCODE_68P );
					hasClosed68OExists = serviceAuthorizationDao.getServiceAuthDtlClosedExists(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(),  CSVCCODE_68O );
					hasClosed68PExists = serviceAuthorizationDao.getServiceAuthDtlClosedExists(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(),  CSVCCODE_68P );

					has68OPaidFull =   serviceAuthorizationDao.getIs68OPaidFull(childBean.getChildId(),
							childBeanLegalInfo.getLegalStatusStatDate(), homeInfoDto.getIdHomeResource());

					//get pending units
					pendingUnits68O = getPendingUnits(childBean, childBeanLegalInfo.getLegalStatusStatDate(), CSVCCODE_68O );

					pendingUnits68P = getPendingUnits(childBean, childBeanLegalInfo.getLegalStatusStatDate(), CSVCCODE_68P );

					if (pendingUnits68O < 0) {
						pendingUnits68O = 0;
					}
					if (pendingUnits68P < 0) {
						pendingUnits68P = 0;
					}
					//	total units for 68O
					if (hasOpen68OExists) {
						totalUnitsUsedfor68O = totalUnitsUsedfor68O + numUnitsRequestedFor68O;
					}
					// total units for 68O if the SAD is closed
					if (hasClosed68OExists) {
						totalUnitsUsedfor68O = totalUnitsUsedfor68O + numUnitsUsedFor68O;
						totalUnitsUsedfor68O  = totalUnitsUsedfor68O + pendingUnits68O;
					}
					//total units for 68P
					if (hasOpen68PExists) {
						totalUnitsUsedfor68P = totalUnitsUsedfor68P + numUnitsRequestedFor68P;
					}
					// total units for 68P if the SAD is closed
					if (hasClosed68PExists) {
						totalUnitsUsedfor68P = totalUnitsUsedfor68P + numUnitsUsedFor68P;
						totalUnitsUsedfor68P  = totalUnitsUsedfor68P + pendingUnits68P;
					}

					//  end of artf112123
					totalNumOfUnitsUsed = totalUnitsUsedfor68O + totalUnitsUsedfor68P;

					maxTermDate = kinHomeInfoDao.getMaxServiceAuthTermDate(childBean.getChildId(), childBeanLegalInfo.getLegalStatusStatDate());

				}
				if (maxTermDate != null && paymentStartDateFromChild != null) {
					if (DateHelper.isAfter(maxTermDate, paymentStartDateFromChild  )) {
						paymentStartDate = DateHelper.addToDate(maxTermDate,0,0,1);
					} else {
						paymentStartDate = paymentStartDateFromChild;
					}
				} else {
					paymentStartDate = paymentStartDateFromChild;
				}

				if (!ObjectUtils.isEmpty(paymentStartDate)) {
					Date endDateForCalc = DateHelper.addToDate(paymentStartDate,1,0,0 );
					maxUnitsDouble = DateHelper.daysDifference(endDateForCalc, paymentStartDate) ;
					maxUnits = (int)Math.round(maxUnitsDouble);
				}

				//if it is tanf, create 68O and 68P
				if (isTanf) {
					//artf112123
					if (totalUnitsUsedfor68O < MIN_UNITS_68O || !has68OPaidFull) {
						if (totalUnitsUsedfor68P > MAX_UNITS_68P ) {
							numUnitsRemaining68O = maxUnits - (totalUnitsUsedfor68P + totalUnitsUsedfor68O);
						} else {
							numUnitsRemaining68O = MAX_UNITS_68O - totalUnitsUsedfor68O;
						}
					}

					//end artf112123
					//lineItem = kinHomeInfoDao.getContractLineItem(contractId, CSVCCODE_68O);
					// we need to check if there is a SA header with 68O or 68P exists
					Set<Long> serviceAuthSet = serviceAuthorizationDao.getSAHeaderIdSet(
							homeInfoDto.getIdHomeResource(), contractId, caseId);
					HashMap<Long, Long> map = new HashMap<Long, Long>();

					//This means there is no service header, so need to create it
					if (CollectionUtils.isEmpty(serviceAuthSet)) {
						idEvent = eventDao.createSAEvent(homeInfoDto, childBean, subStageId);
						idServiceauth  = serviceAuthorizationDao.insertServcAuth(homeInfoDto, childBean, CSVCCODE_68O,
								contractId, savedBean , placementsAdultId);
						serviceAuthorizationDao.insertServcAuthEventLink(savedBean, idServiceauth, idEvent);
					} else {
						// there is service header
						Iterator<Long> it = serviceAuthSet.iterator();
						while (it.hasNext()) {
							Long sadId = it.next();
							idCaseFromSAEvent = kinHomeInfoDao.getSAEventLinkCaseId(sadId);
							map.put(idCaseFromSAEvent, sadId);
						}
						if (!CollectionUtils.isEmpty(map)) {
							if (map.containsKey(idCaseFromSAEvent)) {
								idServiceauth = map.get(idCaseFromSAEvent);
								if (!kinHomeInfoDao.isStageOpen(idServiceauth)) {
									idServiceauth = null;
								}
							}
						}

						if (ObjectUtils.isEmpty(idServiceauth)) {
							idEvent = eventDao.createSAEvent(homeInfoDto, childBean, subStageId);
							idServiceauth  = serviceAuthorizationDao.insertServcAuth(homeInfoDto, childBean,
									CSVCCODE_68O, contractId, savedBean,placementsAdultId);
							serviceAuthorizationDao.insertServcAuthEventLink(savedBean, idServiceauth, idEvent);
						}
					}
					//creating SAD logic
					//first create 68O
					if((totalUnitsUsedfor68O < MAX_UNITS_68O) && (totalNumOfUnitsUsed < maxUnits )
							&& (numUnitsRemaining68O > 0)) {

						lineItem = kinHomeInfoDao.getContractLineItem(contractId, CSVCCODE_68O, paymentStartDate);
						Date endDate = serviceAuthorizationDao.insertServiceAuthDtil(homeInfoDto, childBean.getChildId(),
								idServiceauth, CSVCCODE_68O,
								ratefor68O, paymentStartDate, numUnitsRemaining68O, totalMonthsFor68O, lineItem, true);
						numUnitsRequestedNew68ODouble  = DateHelper.daysDifference(endDate,paymentStartDate)+1;
						numUnitsRequestedNew68O = (int) Math.round(numUnitsRequestedNew68ODouble);
						startingDate   = DateHelper.addToDate(endDate,0,0,1);
					}

					totalUnitsRemaining68P = maxUnits - (totalNumOfUnitsUsed + numUnitsRequestedNew68O);

					if (totalUnitsUsedfor68P < MAX_UNITS_68P && (totalNumOfUnitsUsed + numUnitsRequestedNew68O) < maxUnits
							&& totalUnitsRemaining68P > 0) {
						if (ObjectUtils.isEmpty(startingDate)) {
							startingDate = paymentStartDate;

						}
						lineItem = kinHomeInfoDao.getContractLineItem(contractId, CSVCCODE_68P, startingDate);
						serviceAuthorizationDao.insertServiceAuthDtil(homeInfoDto, childBean.getChildId(),
								idServiceauth, CSVCCODE_68P,
								ratefor68P, startingDate, totalUnitsRemaining68P,
								totalMonthsForTanf68P, lineItem , true);
					}
				} else {

					// we need to check if there is a SA header WITH 68O or 68P exists
					Set<Long> serviceAuthSet = serviceAuthorizationDao.getSAHeaderIdSet(
							homeInfoDto.getIdHomeResource(), contractId, caseId);
					HashMap<Long, Long> map = new HashMap<Long, Long>();

					//This means there is no service header, so need to create it
					if (CollectionUtils.isEmpty(serviceAuthSet)) {
						idEvent = eventDao.createSAEvent(homeInfoDto, childBean, subStageId);
						ServiceAuthorization serviceAuthorization = serviceAuthorizationDao.populateServcAuth(
								homeInfoDto, childBean, CSVCCODE_68P, contractId, savedBean, placementsAdultId);
						serviceAuthorization = serviceAuthorizationDao.serviceAuthorizationSave(serviceAuthorization);
						idServiceauth = serviceAuthorization.getIdSvcAuth();
						SvcAuthEventLink svcAuthEventLink = new SvcAuthEventLink(idEvent, serviceAuthorization,
								new Date(), homeInfoDto.getIdHomeCase(), null);
						serviceAuthorizationDao.svcAuthEventLinkSave(svcAuthEventLink);
//						serviceAuthorizationDao.insertServcAuthEventLink(savedBean, idServiceauth, idEvent);
					} else {
						// if there is service header
						Iterator<Long> it = serviceAuthSet.iterator();
						while (it.hasNext()) {
							Long saId = it.next();
							idCaseFromSAEvent = kinHomeInfoDao.getSAEventLinkCaseId(saId);

							map.put(idCaseFromSAEvent,saId);
						}
						if (!CollectionUtils.isEmpty(map)) {
							if (map.containsKey(childBean.getCaseId())){
								idServiceauth = map.get(childBean.getCaseId());
							}
						}
						if (ObjectUtils.isEmpty(idServiceauth)) {
							idEvent = eventDao.createSAEvent(homeInfoDto, childBean, subStageId);
							ServiceAuthorization serviceAuthorization = serviceAuthorizationDao.populateServcAuth(
									homeInfoDto, childBean, CSVCCODE_68P, contractId, savedBean, placementsAdultId);
							serviceAuthorization = serviceAuthorizationDao.serviceAuthorizationSave(serviceAuthorization);
							idServiceauth = serviceAuthorization.getIdSvcAuth();
							SvcAuthEventLink svcAuthEventLink = new SvcAuthEventLink(idEvent, serviceAuthorization,
									new Date(), homeInfoDto.getIdHomeCase(), null);
							serviceAuthorizationDao.svcAuthEventLinkSave(svcAuthEventLink);
//							idServiceauth  = serviceAuthorizationDao.insertServcAuth(homeInfoDto, childBean,
//									CSVCCODE_68P, contractId, savedBean, placementsAdultId );
//							serviceAuthorizationDao.insertServcAuthEventLink(savedBean, idServiceauth, idEvent);
						}
					}

					totalUnitsRemaining68P = maxUnits - totalNumOfUnitsUsed;

					if (totalNumOfUnitsUsed < maxUnits ) {
						lineItem = kinHomeInfoDao.getContractLineItem(contractId, CSVCCODE_68P, paymentStartDate);
						serviceAuthorizationDao.insertServiceAuthDtil(homeInfoDto, childBean.getChildId(),
								idServiceauth, CSVCCODE_68P, ratefor68P, paymentStartDate,
								totalUnitsRemaining68P, totalMonthsForNonTanf68P, lineItem,false);
					}
				}
			}
			idServiceauth = null;
			maxTermDate = null;
		}
	}

	/**
	 * SR 45217 - HB4 Long Term Solution
	 *
	 * @param homeInfoDto
	 */
	public Long automateContractRelated(KinHomeInfoDto homeInfoDto) {
		Long contractId = null;

		boolean isResourceExists68O = false;
		boolean isResourceExists68P = false;
		boolean isResourceExists68Q = false;
		boolean isResourceExists68R = false;
		double  ratefor68O = 0.0;
		double  ratefor68P = 0.0;
		double  ratefor68Q = 0.0;
		double  ratefor68R = 0.0;

		Long resourceAddressId = null;

		String indTrain = kinHomeInfoDao.getKinTrainCompleted(homeInfoDto.getIdHomeStage());

		KinHomeInfoDto savedBean =  kinHomeInfoService.getKinHomeInfo(homeInfoDto.getIdHomeStage());
		savedBean.setUserId(homeInfoDto.getUserId());

		ratefor68O = kinHomeInfoDao.getFosterCareRate(CSVCCODE_68O, homeInfoDto.getPaymentStartDate());
		ratefor68P = kinHomeInfoDao.getFosterCareRate(CSVCCODE_68P, homeInfoDto.getPaymentStartDate());
		ratefor68Q = kinHomeInfoDao.getFosterCareRate(CSVCCODE_68Q, homeInfoDto.getPaymentStartDate());
		ratefor68R = kinHomeInfoDao.getFosterCareRate(CSVCCODE_68R, homeInfoDto.getPaymentStartDate());
		//first if the resource exists for this service
		isResourceExists68O = kinHomeInfoDao.getIsResourceExists(homeInfoDto.getIdHomeResource(), CSVCCODE_68O);
		isResourceExists68P = kinHomeInfoDao.getIsResourceExists(homeInfoDto.getIdHomeResource(), CSVCCODE_68P);
		isResourceExists68Q = kinHomeInfoDao.getIsResourceExists(homeInfoDto.getIdHomeResource(), CSVCCODE_68Q);
		isResourceExists68R = kinHomeInfoDao.getIsResourceExists(homeInfoDto.getIdHomeResource(), CSVCCODE_68R);

		if (!isResourceExists68O) {
			kinHomeInfoDao.addResourceService(homeInfoDto, CSVCCODE_68O, savedBean,   indTrain);
		}

		if(!isResourceExists68P){
			kinHomeInfoDao.addResourceService(homeInfoDto, CSVCCODE_68P, savedBean,  indTrain);
		}

		if (!isResourceExists68Q) {
			kinHomeInfoDao.addResourceService(homeInfoDto, CSVCCODE_68Q, savedBean,   indTrain);
		}

		if (!isResourceExists68R) {
			kinHomeInfoDao.addResourceService(homeInfoDto, CSVCCODE_68R, savedBean, indTrain);
		}

		//first check if the contract exists for the resource
		contractId = kinHomeInfoDao.getContractId(homeInfoDto.getIdHomeResource());

		if (ObjectUtils.isEmpty(contractId)) {

			resourceAddressId = kinHomeInfoDao.getResourceAddressId(homeInfoDto.getIdHomeResource());
			contractId = kinHomeInfoDao.addContract(homeInfoDto, savedBean, resourceAddressId);
			kinHomeInfoDao.addContractPeriod(homeInfoDto, contractId);
			kinHomeInfoDao.addContractVersion(homeInfoDto, contractId);

			kinHomeInfoDao.addContractService(homeInfoDto, contractId, savedBean, CCONPAY_URT, CCONUNIT_DA2, CSVCCODE_68O,1, ratefor68O );
			kinHomeInfoDao.addContractService(homeInfoDto, contractId, savedBean, CCONPAY_URT, CCONUNIT_DA2, CSVCCODE_68P,2, ratefor68P);
			kinHomeInfoDao.addContractService(homeInfoDto, contractId, savedBean, CCONPAY_VUR, CCONUNIT_ONE, CSVCCODE_68Q,3, ratefor68Q);
			kinHomeInfoDao.addContractService(homeInfoDto, contractId, savedBean, CCONPAY_VUR, CCONUNIT_ONE, CSVCCODE_68R,4, ratefor68R);

			kinHomeInfoDao.addContractCounty(homeInfoDto, contractId, savedBean, CSVCCODE_68O,1);
			kinHomeInfoDao.addContractCounty(homeInfoDto, contractId, savedBean, CSVCCODE_68P,2);
			kinHomeInfoDao.addContractCounty(homeInfoDto, contractId, savedBean, CSVCCODE_68Q,3);
			kinHomeInfoDao.addContractCounty(homeInfoDto, contractId, savedBean, CSVCCODE_68R,4);
		}

		return contractId;
	}

	/**
	 * SR  45217 - HB4 Long Term Solution
	 * To save Payment eligibility and child payment eligibility
	 * @param homeInfoDto
	 *
	 */
	public void savePaymentInfo(KinHomeInfoDto homeInfoDto) {
		kinHomeInfoDao.updateResourceForEligibility(homeInfoDto);
	}

	/**
	 * SR 45217 - HB4 Long Term Solution
	 * To save the kin caregiver payment eligibility info
	 * @param homeInfoDto
	 */
	public void saveOrUpdateKinPaymentHistory(KinHomeInfoDto homeInfoDto){
		String legalStatus = null;
		KinPaymentEligibilityDto savedPayBean = null;

		List <KinChildDto> childList = homeInfoDto.getKinChildVBList();

		if (CollectionUtils.isEmpty(childList)) {
			childList = placementDao.getAllChildPlacementsId(homeInfoDto.getIdHomeResource());
		}
		for (KinChildDto childBean : childList) {
			List<KinChildDto> KinChildList = kinHomeInfoDao.getLegalStatusInfo(childBean.getChildId());
			if (!ObjectUtils.isEmpty(KinChildList)) {
				KinChildDto kcValueBean = KinChildList.get(0);
				KinChildDto childAgeBean = placementDao.getPlacementChildInfo(childBean.getChildId(),
						homeInfoDto.getIdHomeResource());
				if (!ObjectUtils.isEmpty(kcValueBean)) {
					legalStatus = kcValueBean.getCdLegalStatus();
					childBean.setCdLegalStatus(legalStatus);
					childBean.setAge(childAgeBean.getAge());
					childBean.setPlacementEventId(childAgeBean.getPlacementEventId());

					savedPayBean = kinHomeInfoDao.getPaymentEligibilityHistory(homeInfoDto.getIdHomeResource(),
							childAgeBean.getPlacementEventId());
					kinHomeInfoDao.saveKinPaymentHistory(homeInfoDto, childBean, savedPayBean);
				}
			}
		}
	}

	/**
	 * SR45217     HB4 Long Term Solution
	 * @param childBean
	 * @param homeInfoDto
	 * @param savedPayBean
	 * @return
	 */
	private boolean isBeanChanged(KinChildDto childBean , KinHomeInfoDto homeInfoDto,
								  KinPaymentEligibilityDto savedPayBean){

		boolean isBeanChanged = false;
		boolean paymentChanged = false;
		boolean meetsFPLChanged = false;
		boolean paymentEligstatusChanged = false;
		boolean resourceSignedAgreementChanged = false;
		boolean HasBegunTrainingChanged = false;
		boolean ageChanged = false;
		boolean paymentDateChanged = false;

		if (savedPayBean == null) {
			isBeanChanged = true;
		} else if (savedPayBean != null) {
			if (homeInfoDto.getIsPaymentCourtOrdered() == null && savedPayBean.getIndCourtOrdedPayment() == null) {
				paymentChanged = false;
			} else if (homeInfoDto.getIsPaymentCourtOrdered() != null &&
					!homeInfoDto.getIsPaymentCourtOrdered().equalsIgnoreCase(savedPayBean.getIndCourtOrdedPayment())) {
				paymentChanged = true;
			}
			if (homeInfoDto.getIsHouseholdMeetFPL() == null && savedPayBean.getIndIncomeQual()== null) {
				meetsFPLChanged = false;
			} else if (homeInfoDto.getIsHouseholdMeetFPL() != null &&
					!homeInfoDto.getIsHouseholdMeetFPL().equalsIgnoreCase(savedPayBean.getIndIncomeQual())) {
				meetsFPLChanged = true;
			}
			if (childBean.getPaymentEligibilityStatus() == null && savedPayBean.getCdPaymentEligStatus() == null ) {
				paymentEligstatusChanged = false;
			} else if (childBean.getPaymentEligibilityStatus() != null
					&& !childBean.getPaymentEligibilityStatus().equalsIgnoreCase(savedPayBean.getCdPaymentEligStatus())) {
				paymentEligstatusChanged = true;
			}

			if (homeInfoDto.getResourceSignedAgreement() == null && savedPayBean.getIndSignedAgrmnt() == null ) {
				resourceSignedAgreementChanged = false;
			} else if (homeInfoDto.getResourceSignedAgreement() != null &&
					!homeInfoDto.getResourceSignedAgreement().equalsIgnoreCase(savedPayBean.getIndSignedAgrmnt())){
				resourceSignedAgreementChanged = true;
			}
			if (homeInfoDto.getHasBegunTraining() == null && savedPayBean.getIndBegunTraining() == null ) {
				HasBegunTrainingChanged = false;
			} else if (homeInfoDto.getHasBegunTraining()!= null &&
					!homeInfoDto.getHasBegunTraining().equalsIgnoreCase(savedPayBean.getIndBegunTraining())) {
				HasBegunTrainingChanged = true;
			}
			if (childBean.getAge() != savedPayBean.getChildAge()) {
				ageChanged = true;
			}

			if ((childBean.getPaymentEligibilityStartDate() == null && savedPayBean.getDtEligPaymntStart() != null) ||
					(childBean.getPaymentEligibilityStartDate() != null &&
							savedPayBean.getDtEligPaymntStart() == null)) {
				paymentDateChanged = true;
			} else if ((childBean.getPaymentEligibilityStartDate() != null &&
					savedPayBean.getDtEligPaymntStart() != null)) {
				if (DateHelper.daysDifference(childBean.getPaymentEligibilityStartDate(),
						savedPayBean.getDtEligPaymntStart()) > 0) {
					paymentDateChanged = true;
				}
			}
			if(ageChanged ||
					paymentChanged ||
					meetsFPLChanged  ||
					paymentEligstatusChanged ||
					resourceSignedAgreementChanged ||
					HasBegunTrainingChanged ||
					paymentDateChanged){
				isBeanChanged = true;
			}
		}

		return isBeanChanged;
	}

	/**
	 *
	 * @param childBean
	 * @param legalStatusStatDate
	 * @param serviceCode
	 * @return
	 */
	private int getPendingUnits(KinChildDto childBean, Date legalStatusStatDate, String serviceCode) {
		double pendingUnitsDouble = 0.0;
		int pendingUnits = 0;
		Date pendingTermDate = serviceAuthorizationDao.getSADPendingTermDate(childBean.getChildId(),
				legalStatusStatDate, serviceCode );
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int dayOfmonth = cal.get(Calendar.DAY_OF_MONTH);
		if(pendingTermDate != null && dayOfmonth < 2 ){
			Date firstDayOfLastMonth = getFirstDayOfMonth(-1);
			pendingUnitsDouble = DateHelper.daysDifference(pendingTermDate, firstDayOfLastMonth);
			pendingUnits = (int)Math.round(pendingUnitsDouble);
			pendingUnits = pendingUnits +1;

		}
		Date pendingTermDateCurrentMonth = serviceAuthorizationDao.getSADCurrentMonthTermDate(childBean.getChildId(),
				legalStatusStatDate, serviceCode) ;
		if(pendingTermDateCurrentMonth != null ){
			Date firstDayOfCurrentMonth = getFirstDayOfMonth(0);
			pendingUnitsDouble = (DateHelper.daysDifference(pendingTermDateCurrentMonth, firstDayOfCurrentMonth));
			pendingUnits = (int)Math.round(pendingUnitsDouble);
			pendingUnits = pendingUnits +1;
		}
		return pendingUnits;
	}

	private Date getFirstDayOfMonth(int month) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.set(Calendar.DATE, 1);
		aCalendar.add(Calendar.DAY_OF_MONTH, month);
		aCalendar.set(Calendar.DATE, 1);
		aCalendar.set(Calendar.HOUR, 0);
		aCalendar.set(Calendar.MINUTE, 0);
		aCalendar.set(Calendar.SECOND, 0);
		aCalendar.set(Calendar.MILLISECOND, 0);
		Date firstDateOfPreviousMonth = aCalendar.getTime();
		return firstDateOfPreviousMonth;
	}

	/**
	 * SR 45217     HB4 Long Term Solution SQLs
	 * homeInfoDto , placement date
	 * calculate payment Eligibilty  start date
	 */
	public Date calculatePayEligStartDate(KinHomeInfoDto homeInfoDto, Date placementDate) {

		Date calcPlacementDate = null;
		Date intermediateDate = null;
		Date paymentEligStartDate = null;

		if (ObjectUtils.isEmpty(placementDate)) {
			placementDate = kinHomeInfoDao.getMinPlacementDate(homeInfoDto.getIdHomeResource());
		}

		if (homeInfoDto.getCourtOrderedPaymentDate() != null
				&& (homeInfoDto.getIsPaymentCourtOrdered() != null
				&& StringHelper.EMPTY_STRING.equalsIgnoreCase(homeInfoDto.getIsPaymentCourtOrdered())
				&& !NO.equalsIgnoreCase(homeInfoDto.getIsPaymentCourtOrdered()))){

			paymentEligStartDate = homeInfoDto.getCourtOrderedPaymentDate();
		}
		if ((!ObjectUtils.isEmpty(homeInfoDto.getAssessmentApprovedDate())
				&& !DateHelper.isAfterToday(homeInfoDto.getAssessmentApprovedDate()))
				&& !ObjectUtils.isEmpty(homeInfoDto.getAgreementSignedDate())
				&& !DateHelper.isAfterToday(homeInfoDto.getAgreementSignedDate())
				&& !ObjectUtils.isEmpty(placementDate)) {
			if (!ObjectUtils.isEmpty(homeInfoDto.getCourtOrderedPlacementDate())
					&& !DateHelper.isAfterToday(homeInfoDto.getCourtOrderedPlacementDate())) {
				if (DateHelper.isAfter(placementDate, homeInfoDto.getCourtOrderedPlacementDate())) {
					calcPlacementDate = placementDate;
				} else {
					calcPlacementDate = homeInfoDto.getCourtOrderedPlacementDate();
				}
			} else {
				calcPlacementDate = placementDate;
			}

			if (DateHelper.isAfter(homeInfoDto.getAgreementSignedDate(), calcPlacementDate)) {
				intermediateDate = homeInfoDto.getAgreementSignedDate();
			} else {
				intermediateDate = calcPlacementDate;
			}

			if (DateHelper.isAfter(intermediateDate, homeInfoDto.getAssessmentApprovedDate())) {
				paymentEligStartDate =  intermediateDate;
			} else {
				paymentEligStartDate = homeInfoDto.getAssessmentApprovedDate();
			}
		}

		return paymentEligStartDate;
	}

	private ResourceServiceDto populateUpdateResourceService(ResourceServiceDto  resServiceDto ,
															 KinHomeInfoDto  kinHomeResource,
															 String indTrain ) {
		// this means that there exists a row in the Resource Service hence do an update
		// populate the retResServiceBean with the right values and call update

		resServiceDto .setIndKinshipHomeAssmnt(ServiceConstants.KIN_HOME_SET_IND_Y);
		resServiceDto .setIndKinshipTraining(indTrain);
		resServiceDto .setIndKinshipIncome(kinHomeResource.getResourceIncomeQualification());
		resServiceDto .setIndKinshipAgreement(kinHomeResource.getResourceManualGiven());

		return resServiceDto ;
	}

	private ResourceServiceDto  populateInsertResourceService(KinHomeInfoDto  kinHomeInfo, String indTrain) {
		ResourceServiceDto  resService = new ResourceServiceDto();
		resService.setCdRsrcSvcCnty(kinHomeInfo.getResourceAddressCounty());
		resService.setCdRsrcSvcState(kinHomeInfo.getResourceAddressState());
		resService.setCdRsrcSvcRegion(kinHomeInfo.getRegion());
		resService.setIndKinshipHomeAssmnt(ServiceConstants.KIN_HOME_SET_IND_Y);
		resService.setIndKinshipTraining(indTrain);
		resService.setIndKinshipIncome(kinHomeInfo.getResourceIncomeQualification());
		resService.setIndKinshipAgreement(kinHomeInfo.getResourceSignedAgreement());
		resService.setCdRsrcSvcCategRsrc(ServiceConstants.KIN_HOME_SET_RES_SERV_CAT);
		resService.setCdRsrcSvcProgram(ServiceConstants.KIN_HOME_SET_RES_SERV_PGRM);

		resService.setIndRsrcSvcCntyPartial(ServiceConstants.KIN_HOME_SET_IND_N);
		resService.setIndRsrcSvcIncomeBsed(ServiceConstants.KIN_HOME_SET_IND_N);
		resService.setIndRsrcSvcShowRow(ServiceConstants.KIN_HOME_SET_IND_Y);
		return resService;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public void saveHomeAssessmentApproval(CommonHelperReq commonHelperReq) {

		KinHomeAssessmentDto kinHomeAssessmentDto = getAssessmentOverAllStatusAndDate(commonHelperReq.getIdCase());
		kinHomeAssessmentDto.setNmPersonFull(commonHelperReq.getUserFullName());

		if(null!=kinHomeAssessmentDto){
			kinHomeInfoDao.updateCapsResource(kinHomeAssessmentDto, commonHelperReq.getIdStage());
		}
	}
	private KinHomeAssessmentDto getAssessmentOverAllStatusAndDate(Long caseId) {

		 KinHomeAssessmentDto kinHomeAssessmentDto = kinHomeInfoDao.getHomeAssessmentOverallStatusDate(caseId);

		 if (ServiceConstants.CASMSTS_APRV.equalsIgnoreCase(kinHomeAssessmentDto.getCdStatus())
				 || ServiceConstants.CASMSTS_APKS.equalsIgnoreCase(kinHomeAssessmentDto.getCdStatus())) {

			 KinHomeAssessmentDto declineStatusDto = kinHomeInfoDao.getDeclineStatusDate(caseId);

			 if (null != declineStatusDto && null != declineStatusDto.getDtStatusChanged() && declineStatusDto.getDtStatusChanged() != null) {
				 KinHomeAssessmentDto latestStatusDto = kinHomeInfoDao.getLatestStatusCode(caseId, declineStatusDto.getDtStatusChanged());
				 if (null != latestStatusDto) {
					 kinHomeAssessmentDto.setDtStatusChanged(latestStatusDto.getDtStatusChanged());
				 }
			 } else {
				 KinHomeAssessmentDto statusDateDto = kinHomeInfoDao.getStatusDate(caseId);
				 if (null != statusDateDto) {
					 kinHomeAssessmentDto.setDtStatusChanged(statusDateDto.getDtStatusChanged());
				 }
			 }
		 }
		 return kinHomeAssessmentDto;
	 }
}