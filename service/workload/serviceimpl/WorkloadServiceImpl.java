package us.tx.state.dfps.service.workload.serviceimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.PLMT_TSK;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gov.texas.dfps.api.notification.client.NotificationApi;
import gov.texas.dfps.api.notification.client.model.Event;
import gov.texas.dfps.api.notification.client.model.EventAction;
import gov.texas.dfps.api.notification.client.model.EventObject;
import microsoft.exchange.webservices.data.core.ExchangeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.common.web.bean.AddressDetailBean;
import us.tx.state.dfps.phoneticsearch.IIRHelper.FormattingHelper;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casemanagement.dao.FetchIncomingFacilityDao;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.*;
import us.tx.state.dfps.service.casepackage.service.CapsCaseService;
import us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDtlDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.*;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FTFamilyTreeUtil;
import us.tx.state.dfps.service.common.util.OutlookUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDao;
import us.tx.state.dfps.service.conservatorship.service.ConservatorshipService;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.AssignWorkloadPrefillData;
import us.tx.state.dfps.service.forms.util.SijsStatusFormPrefillData;
import us.tx.state.dfps.service.fsna.dao.FSNADao;
import us.tx.state.dfps.service.heightenedmonitoring.service.HeightenedMonitoringService;
import us.tx.state.dfps.service.intake.dao.IncomingDao;
import us.tx.state.dfps.service.investigation.dao.CpsInvstDetailDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstSumDao;
import us.tx.state.dfps.service.investigation.dto.ClassFacilityDto;
import us.tx.state.dfps.service.legal.dao.LeglActnModificationDao;
import us.tx.state.dfps.service.legal.dto.LegalActionRtrvOutDto;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.lookup.dto.CodeAttributes;
import us.tx.state.dfps.service.lookup.service.LookupService;
import us.tx.state.dfps.service.outlook.AppointmentDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dao.TemporaryAbsenceDao;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.workload.dao.*;
import us.tx.state.dfps.service.workload.dto.ApprovalDto;
import us.tx.state.dfps.service.workload.dto.AssignmentGroupDto;
import us.tx.state.dfps.service.workload.dto.*;
import us.tx.state.dfps.service.workload.service.CloseStageCaseService;
import us.tx.state.dfps.service.workload.service.WorkloadService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN13S
 * Class Description: This class is doing service Implementation for
 * StaffToDoListService Mar 21, 2017 - 2:34:34 PM
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 *  ***********  Change History *********************************
 *Oct 15, 2019  mullar2  artf128805 : FCL changes for showing notification section in priority closure for RCI intakes
 * 06/11/2021   kanakas  artf128837 :  Dev - dev task for FAD stage navigation
 */
@Service
@Transactional
public class WorkloadServiceImpl implements WorkloadService {

	public static final ResourceBundle emailConfigBundle = ResourceBundle.getBundle("EmailConfig");
	private static final String FPR = "FPR";
	private static final String SAFEWITHPLAN = "SAFEWITHPLAN";
	private static final String UNSAFE = "UNSAFE";

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private StageDao stageDao;

	@Autowired
	private EventService eventService;

	@Autowired
	private IncomingDetailDao incomingDetailDao;

	@Autowired
	private TodoDao todoDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private ApprovalEventLinkDao approvalEventLinkDao;

	@Autowired
	private ApprovalEventDelDao approvalEventDelDao;

	@Autowired
	private EventDao eventDao;

	@Autowired
	private ApprovalDao approvalDao;

	@Autowired
	private ApproversDao approversDao;

	@Autowired
	private FamilyAssmtDao familyAssmtDao;

	@Autowired
	private CheckStageEventStatusService checkStageEventStatus;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private CapsCaseDao capsCaseDao;

	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	private CityDao retrieveCountyDao;

	@Autowired
	private CaseMergeUpdateDao caseMergeUpdateDao;

	@Autowired
	private WebsvcFormTransDao websvcFormTransDao;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private ApprovalRejectionDao approvalRejectionDao;

	@Autowired
	private StageProgDao stageProgDao;

	@Autowired
	private AssignDao assignDao;

	@Autowired
	private IncomingDao incomingDao;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AssignWorkloadPrefillData assignWorkloadPrefillData;

	@Autowired
	private AssignWorkloadDao assignWorkloadDao;

	@Autowired
	private CloseStageCaseService closeStageCaseService;

	@Autowired
	private SijsStatusFormPrefillData sijsStatusFormPrefillData;

	@Autowired
	private FamilyPlanDao familyPlanDao;

	@Autowired
	private ChildPlanBeanDao childPlanBeanDao;

	@Autowired
	ConservatorshipService conservatorshipService;

	@Autowired
	private CapsCaseService capsCaseService;

	@Autowired
	private LeglActnModificationDao leglActnModificationDao;


	@Autowired
	private CaseSummaryDao caseSummaryDao;

	@Autowired
	CnsrvtrshpRemovalDao cnsrvtrshpRemovalDao;

	@Autowired
	OutlookUtil outlookUtil;

	@Autowired
	FSNADao fsnaDao;

	@Autowired
	private LegalStatusDao legalStatusDao;

	@Autowired
	CpsInvstDetailDao cpsInvstDetailDao;

	@Autowired
	PriorityClosureDao priorityClosureDao;

	@Autowired
	LookupService lookupService;

	@Autowired
	NotificationApi notificationApi;

	@Autowired
	HeightenedMonitoringService heightenedMonitoringService;

	@Autowired
	TemporaryAbsenceDao temporaryAbsenceDao;

	@Autowired
	private FetchIncomingFacilityDao fetchIncomingFacilityDao;

	@Autowired
	private CpsIntakeReportDao cpsIntakeReportDao;

	@Autowired
	LicensingInvstSumDao licensingInvstSumDao;

	@Autowired
	LicensingInvstDtlDao licensingInvstDtlDao;


	private static final Logger log = Logger.getLogger(WorkloadServiceImpl.class);

	private static final String PROD_HOST_NAME = "PROD";

	private static final String MM_DD_YYYY_FORMAT = "MM/d/uuuu";

	private static final String[] ignoreProperties = { "idCase", "idStage" };

	public WorkloadServiceImpl() {

	}

	/**
	 * Service Name: CCMN13S Method Description: This method is used to cThis
	 * service will retrieve the user's supervisor if the ReqFuncCd is
	 * REQ_FUNC_CD_APPROVAL, or it will get the NM STAGE, NM TASK, TASK DUE DT,
	 * PRIMARY WORKER of STAGE if the ReqFuncCd is REQ_FUNC_CD_ASSIGN, or it
	 * will get the information related to the ID_TODO specified.
	 *
	 * @param todoCaseStru
	 * @return todoListRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public TodoListRes StaffToDoList(TodoListReq todoListReq) {
		TodoListRes todoListRes = new TodoListRes();
		// Long cdTask = Long.valueOf(todoListReq.getCdTask());
		if ((todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEXT_APPROVE)
				|| todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEW_APPROVE)
				|| todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.TODO_MODE_NEW_FCE_TODO))) {
			IncomingDetail incomingDto = new IncomingDetail();
			CreatedDto createdDto = new CreatedDto();
			//PPM 60692-artf179777-Start-To Do page changes
			AssignedDto assignedDto = fetchAssignedDto(todoListReq);
			//PPM 60692-artf179777-End
			TaskDto taskDto = taskDao.getTaskDetails(todoListReq.getSzCdTask());
			StageDto stageDto = stageDao.getStageById(todoListReq.getUlIdStage());
			boolean cdStageInd = TypeConvUtil.isNullOrEmpty(stageDto.getCdStage());
			if (!cdStageInd && stageDto.getCdStage() != ServiceConstants.CDSTAGE_IR
					&& stageDto.getCdStage() != ServiceConstants.CDSTAGE_SPC) {
				if (stageDto.getCdStage().equalsIgnoreCase(ServiceConstants.CDSTAGE_INT)) {
					incomingDto = incomingDetailDao.getincomingDetailbyId(todoListReq.getUlIdStage());
				}
				StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(todoListReq.getUlIdStage(),
						ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
				if (!TypeConvUtil.isNullOrEmpty(stagePersonDto.getNmPersonFull())) {
					stageDto.setNmStage(stagePersonDto.getNmStage());
					stageDto.setIdTodoPersWorker(stagePersonDto.getIdTodoPersWorker());
					createdDto.setNmPersonFull(stagePersonDto.getNmPersonFull());
				} else {
					if (incomingDto.getCdIncmgStatus().equalsIgnoreCase(ServiceConstants.MARKED_DELETION)) {
						stageDto.setNmStage(stagePersonDto.getNmStage());
					}
				}
			}
			todoListRes.setAssignedDto(assignedDto);
			todoListRes.setTaskDto(taskDto);
			todoListRes.setStageDto(stageDto);
			todoListRes.setCreatedDto(createdDto);
		} else if (todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_ASSGIN)) {
			IncomingDetail incomingDto = new IncomingDetail();
			if (!TypeConvUtil.isNullOrEmpty(todoListReq.getSzCdTask())) {
				TaskDto taskDto = taskDao.getTaskDetails(todoListReq.getSzCdTask());
				todoListRes.setTaskDto(taskDto);
			}
			StageDto stageDto = stageDao.getStageById(todoListReq.getUlIdStage());
			CreatedDto createdDto = new CreatedDto();
			boolean cdStageInd = TypeConvUtil.isNullOrEmpty(stageDto.getCdStage());
			if (!cdStageInd && stageDto.getCdStage() != ServiceConstants.CDSTAGE_IR
					&& stageDto.getCdStage() != ServiceConstants.CDSTAGE_SPC) {
				if (stageDto.getCdStage() != ServiceConstants.CDSTAGE_INT) {
					incomingDto = incomingDetailDao.getincomingDetailbyId(todoListReq.getUlIdStage());
				}
				StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(todoListReq.getUlIdStage(),
						ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
				if (!TypeConvUtil.isNullOrEmpty(stagePersonDto.getNmPersonFull())) {
					stageDto.setNmStage(stagePersonDto.getNmStage());
					stageDto.setIdTodoPersWorker(stagePersonDto.getIdTodoPersWorker());
					createdDto.setNmPersonFull(stagePersonDto.getNmPersonFull());
				} else {
					if (incomingDto.getCdIncmgStatus().equalsIgnoreCase(ServiceConstants.MARKED_DELETION)) {
						stageDto.setNmStage(stagePersonDto.getNmStage());
					}
				}
			}
			todoListRes.setStageDto(stageDto);
			todoListRes.setCreatedDto(createdDto);
		} else {
			TaskDto taskDto = new TaskDto();
			StageDto stageDto = new StageDto();
			TodoInfoDto todoInfoDto = new TodoInfoDto();
			// StagePersonDto stagePersonDto = new StagePersonDto();
			IncomingDetail incomingDto = new IncomingDetail();
			CreatedDto createdDto = new CreatedDto();
			AssignedDto assignedDto = new AssignedDto();
			boolean todoInd = true;
			TodoDto todoDtoDtls = todoDao.getTodoDtlsById(todoListReq.getLdIdTodo());
			if (!ObjectUtils.isEmpty(todoDtoDtls)) {
				todoInd = TypeConvUtil.isNullOrEmpty(todoDtoDtls.getIdTodo());
				if (!todoInd) {
					todoInfoDto.setCdTodoType(todoDtoDtls.getCdTodoType());
					todoInfoDto.setIdTodo(todoDtoDtls.getIdTodo());
					todoInfoDto.setDtTodoDue(todoDtoDtls.getDtTodoDue());
					todoInfoDto.setIdCase(todoDtoDtls.getIdTodoCase());
					todoInfoDto.setIdEvent(todoDtoDtls.getIdTodoEvent());
					todoInfoDto.setDtTodoCompleted(todoDtoDtls.getDtTodoCompleted());
					todoInfoDto.setTodoDesc(todoDtoDtls.getTodoDesc());
					todoInfoDto.setTodoLongDesc(todoDtoDtls.getTodoLongDesc());
					todoListRes.setTsLastUpdate(todoDtoDtls.getDtLastUpdate());
				}
				boolean cdTaskInd = TypeConvUtil.isNullOrEmpty(todoListReq.getSzCdTask());
				if (!cdTaskInd) {
					taskDto = taskDao.getTaskDetails(todoListReq.getSzCdTask());
					if (!ObjectUtils.isEmpty(taskDto)) {
						taskDto.setCdTask(todoDtoDtls.getCdTodoTask());
						taskDto.setDtTaskDue(todoDtoDtls.getDtTodoTaskDue());
					}
				}
				boolean idStageInd = TypeConvUtil.isNullOrEmpty(todoListReq.getUlIdStage());
				if (!idStageInd) {
					stageDto = stageDao.getStageById(todoListReq.getUlIdStage());
					stageDto.setIdStage(todoDtoDtls.getIdTodoStage());
					boolean cdStageInd = TypeConvUtil.isNullOrEmpty(stageDto.getCdStage());
					if (!cdStageInd && stageDto.getCdStage() != ServiceConstants.CDSTAGE_IR
							&& stageDto.getCdStage() != ServiceConstants.CDSTAGE_SPC) {
						if (stageDto.getCdStage() != ServiceConstants.CDSTAGE_INT) {
							incomingDto = incomingDetailDao.getincomingDetailbyId(todoListReq.getUlIdStage());
						}
						StagePersonDto stagePersonDtls = stageDao.getStagePersonLinkDetails(todoListReq.getUlIdStage(),
								ServiceConstants.PRIMARY_ROLE_STAGE_OPEN);
						if (!TypeConvUtil.isNullOrEmpty(stagePersonDtls.getNmPersonFull())) {
							stageDto.setNmStage(stagePersonDtls.getNmStage());
							stageDto.setNmPersonFull(stagePersonDtls.getNmPersonFull());
							stageDto.setIdTodoPersWorker(stagePersonDtls.getIdTodoPersWorker());
							createdDto.setNmPersonFull(stagePersonDtls.getNmPersonFull());
						} else {
							if (incomingDto.getCdIncmgStatus().equalsIgnoreCase(ServiceConstants.MARKED_DELETION)) {
								stageDto.setNmStage(stagePersonDtls.getNmStage());
							}
						}
					}
				}
				boolean todoPersonIDInd = TypeConvUtil.isNullOrEmpty(todoDtoDtls.getIdTodoPersCreator());
				PersonDto personDto = null;
				if (!todoPersonIDInd) {
					personDto = personDao.getPersonById(todoDtoDtls.getIdTodoPersCreator());
					if (!TypeConvUtil.isNullOrEmpty(personDto.getIdPerson())) {
						createdDto.setNmPersonFull(personDto.getNmPersonFull());
					}
				} else {
					createdDto.setNmPersonFull(ServiceConstants.SYSTEM);
				}
				if (!TypeConvUtil.isNullOrEmpty(todoDtoDtls.getIdTodoPersAssigned())) {
					personDto = personDao.getPersonById(todoDtoDtls.getIdTodoPersAssigned());
					assignedDto.setNmPersonFull(personDto.getNmPersonFull());
				}
				createdDto.setIdTodoPersCreator(todoDtoDtls.getIdTodoPersCreator());
				createdDto.setDtTodoCreated(todoDtoDtls.getDtTodoCreated());
				assignedDto.setIdTodoPersAssigned(todoDtoDtls.getIdTodoPersAssigned());
				todoListRes.setAssignedDto(assignedDto);
				todoListRes.setTaskDto(taskDto);
				todoListRes.setStageDto(stageDto);
				todoListRes.setTodoInfoDto(todoInfoDto);
				todoListRes.setCreatedDto(createdDto);
			}
		}
		todoListRes.setTransactionId(todoListReq.getTransactionId());
		log.info("TransactionId :" + todoListReq.getTransactionId());
		return todoListRes;
	}

	/**
	 * PPM 60692-artf179777 - To Do page changes
	 * Method Description: This method fetches AssignedDto of Regional Director for given Region if the task code is
	 * 9917 else fetches the details from UnitDao
	 *
	 * @param todoListReq
	 * @return
	 */
	private AssignedDto fetchAssignedDto(TodoListReq todoListReq) {
		AssignedDto assignedDto = null;

		if (ServiceConstants.HMM_SERV_REF_TASK.equals(todoListReq.getSzCdTask())
				|| ServiceConstants.HMM_SERV_REF_TASK_APRV.equals(todoListReq.getSzCdTask()) ) {

			boolean isSecondary = false;
			if (ServiceConstants.REQ_IND_NEXT_APPROVE.equals(todoListReq.getReqFuncCd())) {
				isSecondary = true;
			}
			assignedDto = heightenedMonitoringService.fetchApproverForHMToDo(todoListReq.getLegalRegion(),
					isSecondary);
		} else {
			assignedDto = unitDao.getPersonDdetails(todoListReq);
		}

		return assignedDto;
	}

	/**
	 * Service Name: CCMN97s Method Description: This service will delete all
	 * rows from the _TODO table for the idTodo given as input. This service was
	 * created to allow BLOCK DELETE of _TODOs on the Staff Todo List window to
	 * be saved.
	 *
	 * @param todoListDelReq
	 * @return TodoListDelRes @
	 */

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoListDelRes StaffToDoListDelete(TodoListDelReq todoListDelReq) {
		TodoListDelRes todoListDelRes = new TodoListDelRes();
		String returnMsg = "";

		returnMsg = todoDao.todoDeleteById(todoListDelReq.getLdIdTodo());
		todoListDelRes.setReturnMsg(returnMsg);
		todoListDelRes.setTransactionId(todoListDelReq.getTransactionId());
		log.info("TransactionId :" + todoListDelReq.getTransactionId());

		return todoListDelRes;
	}

	/**
	 * to update the stage RCI indicator
	 * artf129782: Licensing Investigation Conclusion
	 * @param todoListDelReq
	 * @return TodoListDelRes @
	 */

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateStage(List<TodoDto> todoDtoList) {

		stageDao.updateRciIndicator(todoDtoList);
	}

	/**
	 *
	 */
	public List<TodoDto> getTodo(List<Long> ldIdTodo){
		List<TodoDto> todoDtoList = new ArrayList();
		for (int i = 0; i < ldIdTodo.size(); i++) {
			TodoDto todoDtoDtls = todoDao.getTodoDtlsById(ldIdTodo.get(i));
			todoDtoList.add(todoDtoDtls);
		}
		return todoDtoList;
	}

	/**
	 * Service Name: CCMN19s Method Description: This service will Add/Update to
	 * the _TODO table, Add to the Approval, Approval Event Link table, Event
	 * table, and Approvers table based on the input request function indicator
	 *
	 * @param todoListAUDReq
	 * @return todoListAUDRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoListAUDRes staffToDoListAUD(TodoListAUDReq todoListAUDReq) {
		TodoListAUDRes todoListAUDRes = new TodoListAUDRes();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(todoListAUDReq.getReqFuncCd());

		String RetVal = "";
		if (todoListAUDReq.getTodoDto().getCdTodoType().equalsIgnoreCase(ServiceConstants.TODO_ACTIONS_TASK)
				&& !serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
			InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
			inCheckStageEventStatusDto.setCdTask(todoListAUDReq.getTodoDto().getCdTodoTask());
			inCheckStageEventStatusDto.setIdStage(todoListAUDReq.getTodoDto().getIdTodoStage());
			inCheckStageEventStatusDto.setCdReqFunction(todoListAUDReq.getReqFuncCd());
			Boolean eventStageStatus = checkStageEventStatus.chkStgEventStatus(inCheckStageEventStatusDto);
			if (eventStageStatus) {
				RetVal = ServiceConstants.SUCCESS;
			} else {
				ErrorDto error = new ErrorDto();
				error.setErrorCode(8164);
				todoListAUDRes.setErrorDto(error);
				RetVal = ServiceConstants.FAIL;
			}
		} else {
			RetVal = ServiceConstants.SUCCESS;
		}
		if (RetVal.equalsIgnoreCase(ServiceConstants.SUCCESS)) {
			Long approversEventId = ServiceConstants.ZERO_VAL;
			if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEW_APPROVE)) {
				Long idApproval = 0l;
				int approvalIND = ServiceConstants.APPROVAL_INDICATOR;
				ApprovalDto approvalDtoEvn = new ApprovalDto();
				ApproversDto approversDtoEvn = new ApproversDto();
				TodoDto todoDtoEvn = new TodoDto();
				for (int i = 0; i < todoListAUDReq.getUidEvent().size(); i++) {
					ApprovalEventLinkDto approvalevenLinkDto = approvalEventLinkDao
							.getApprovalEventLinkID(todoListAUDReq.getUidEvent().get(i));
					if (!TypeConvUtil.isNullOrEmpty(approvalevenLinkDto)) {
						idApproval = approvalevenLinkDto.getIdApproval();
					}
					if (!TypeConvUtil.isNullOrEmpty(idApproval)) {
						List<Object> toRemove = new ArrayList<Object>();
						for (int j = 0; j < todoListAUDReq.getUidEvent().size(); j++) {
							if (todoListAUDReq.getUidEvent().get(j).equals(idApproval)) {
								toRemove.add(todoListAUDReq.getUidEvent().get(j));
							}
						}
						toRemove.clear();
						List<ApproversDto> approversDto = approversDao.getApproversPersonDetails(idApproval);
						for (int k = 0; k < approversDto.size(); k++) {
							if (!TypeConvUtil.isNullOrEmpty(approversDto.get(k).getCdApproversStatus())) {
								if (approversDto.get(k).getCdApproversStatus()
										.equalsIgnoreCase(ServiceConstants.APPROVAL_REJECT)
										|| approversDto.get(k).getCdApproversStatus()
										.equalsIgnoreCase(ServiceConstants.APPROVAL_INVALID)) {
									approvalIND = ServiceConstants.Zero;
								}
							}
						}
						if (approvalIND == ServiceConstants.APPROVAL_INDICATOR) {
							idApproval = 0l;
						}
					}
					if (!TypeConvUtil.isNullOrEmpty(idApproval) && approvalevenLinkDto != null) {
						ServiceReqHeaderDto apprvEventDel = new ServiceReqHeaderDto();
						apprvEventDel.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
						String appvalEvnDelMsg = approvalEventDelDao.getApprovalEventrecordDel(apprvEventDel,
								approvalevenLinkDto.getIdApproval(), todoListAUDReq.getTodoDto().getIdTodo(),
								todoListAUDReq.getUidEvent());
						log.info("Approval Event Deletion Record " + appvalEvnDelMsg);
					}
				}
				ServiceReqHeaderDto eventAdd = new ServiceReqHeaderDto();
				eventAdd.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				EventDto eventDto = setValuesToEventDto(todoListAUDReq, serviceReqHeaderDto);
				EventDto idEvent = eventDao.eventAUDFunc(eventAdd, eventDto);
				if (!TypeConvUtil.isNullOrEmpty(idEvent.getIdEvent())) {
					approvalDtoEvn.setIdApproval(idEvent.getIdEvent());
					approversEventId = idEvent.getIdEvent();
					todoDtoEvn.setIdTodoEvent(idEvent.getIdEvent());
					todoListAUDReq.getTodoDto().setIdTodoEvent(idEvent.getIdEvent());
					todoListAUDReq.getApproversDto().setId_Approval(idEvent.getIdEvent());
				}
				String updEvnStatusMsg = updateEventStatus(todoListAUDReq);
				log.info("Updated Event Message " + updEvnStatusMsg);
				ServiceReqHeaderDto reqIndAdd = new ServiceReqHeaderDto();
				reqIndAdd.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				String approvalAUDMsg = approvalDao.getApprovalAUD(reqIndAdd, todoListAUDReq.getApprovalDto(),
						idEvent.getIdEvent());
				log.info("ApprovalAUD Message " + approvalAUDMsg);
				ServiceReqHeaderDto reqIndDel = new ServiceReqHeaderDto();
				reqIndDel.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
				String appvalEvnLinkDelMsg = approvalEventLinkAUD(todoListAUDReq, reqIndDel, approvalDtoEvn);
				String appvalEvnLinkAddMsg = approvalEventLinkAUD(todoListAUDReq, reqIndAdd, approvalDtoEvn);
				log.info("ApprovalEventLinkDelMessage : " + appvalEvnLinkDelMsg + " ApprovalEventLinkAddMessage "
						+ appvalEvnLinkAddMsg);
				TodoDto todoDto = setValuesToTodoDto(todoListAUDReq, serviceReqHeaderDto);
				TodoDto todoDtoid = todoDao.todoAUD(todoDto, reqIndAdd);
				todoListAUDReq.getApproversDto().setIdTodo(todoDtoid.getIdTodo());
				approversDtoEvn.setId_Person(todoListAUDReq.getApproversDto().getId_Person());
				approversDtoEvn.setIdTodo(todoListAUDReq.getApproversDto().getIdTodo());
				approversDtoEvn.setIndApproversHistorical(todoListAUDReq.getApproversDto().getIndApproversHistorical());
				approversDtoEvn
						.setDtApproversDetermination(todoListAUDReq.getApproversDto().getDtApproversDetermination());
				approversDtoEvn.setDtApproversRequested(todoListAUDReq.getApproversDto().getDtApproversRequested());
				approversDtoEvn.setId_Approval(approversEventId);
				approversDtoEvn.setApproversCmnts(todoListAUDReq.getApproversDto().getApproversCmnts());
				approversDao.getApproversAUD(reqIndAdd, approversDtoEvn);
				todoListAUDRes.setIdTodo(todoDtoid.getIdTodo());
			} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEXT_APPROVE)) {
				ServiceReqHeaderDto reqIndAdd = new ServiceReqHeaderDto();
				reqIndAdd.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				TodoDto todoDtoDtls = setValuesToTodoDto(todoListAUDReq, serviceReqHeaderDto);
				TodoDto todoDtoid = todoDao.todoAUD(todoDtoDtls, reqIndAdd);
				todoListAUDReq.getApproversDto().setIdTodo(todoDtoid.getIdTodo());
				ApproversDto approversDtoEvn = new ApproversDto();
				approversDtoEvn.setId_Person(todoListAUDReq.getApproversDto().getId_Person());
				approversDtoEvn.setIdTodo(todoListAUDReq.getApproversDto().getIdTodo());
				approversDtoEvn.setIndApproversHistorical(todoListAUDReq.getApproversDto().getIndApproversHistorical());
				approversDtoEvn
						.setDtApproversDetermination(todoListAUDReq.getApproversDto().getDtApproversDetermination());
				approversDtoEvn.setDtApproversRequested(todoListAUDReq.getApproversDto().getDtApproversRequested());
				approversDtoEvn.setId_Approval(todoListAUDReq.getApproversDto().getId_Approval());
				approversDtoEvn.setApproversCmnts(todoListAUDReq.getApproversDto().getApproversCmnts());
				String appvrsAUDMsg = approversDao.getApproversAUD(reqIndAdd, approversDtoEvn);
				log.info("ApprovalAUD Message " + appvrsAUDMsg);
				if (!todoListAUDReq.getSysIndTaskNew().equalsIgnoreCase(ServiceConstants.APPROVAL_FLAG)) {
					ServiceReqHeaderDto reqIndUpd = new ServiceReqHeaderDto();
					reqIndUpd.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
					String evnUpdTimStmpMsg = eventDao.getEventUpdateTimeStamp(reqIndUpd, todoListAUDReq);
					log.info("EventUpdTimeStamp Message " + evnUpdTimStmpMsg);
					todoListAUDRes.setIdTodo(todoDtoDtls.getIdTodo());
				}
				todoListAUDRes.setIdTodo(todoDtoid.getIdTodo());
			} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
				ServiceReqHeaderDto reqIndUpd = new ServiceReqHeaderDto();
				reqIndUpd.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
				TodoDto todoDtoDtls = setValuesToTodoDto(todoListAUDReq, serviceReqHeaderDto);
				// added as part of artf251970 : Reassigned task issue. when task is reassigned and task has pending approval,update the approver in Approvers table.
				updatePendingApprovalforTask(todoListAUDReq);
				TodoDto todoDtoid = todoDao.todoAUD(todoDtoDtls, reqIndUpd);
				todoListAUDRes.setIdTodo(todoDtoid.getIdTodo());
			} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
				ServiceReqHeaderDto reqIndDel = new ServiceReqHeaderDto();
				reqIndDel.setReqFuncCd(ServiceConstants.REQ_IND_AUD_DELETE);
				TodoDto todoDtoid = todoDao.todoAUD(todoListAUDReq.getTodoDto(), reqIndDel);
				todoListAUDRes.setIdTodo(todoDtoid.getIdTodo());
			} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_ASSGIN)) {
				TaskDto taskDto = new TaskDto();
				if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getCdTodoTask())) {
					taskDto = taskDao.getTaskDetails(todoListAUDReq.getTodoDto().getCdTodoTask());
				}
				EventDto eventDto = setValuesToEventDto(todoListAUDReq, serviceReqHeaderDto);
				eventDto.setCdEventType(taskDto.getCdTaskEventType());
				ServiceReqHeaderDto eventAdd = new ServiceReqHeaderDto();
				eventAdd.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				EventDto idEvent = eventDao.eventAUDFunc(eventAdd, eventDto);
				log.info("Output Event AUD Function " + idEvent.getIdEvent());
				if (!ServiceConstants.FAMILY_ASSESSMENT_TASK
						.equalsIgnoreCase(todoListAUDReq.getTodoDto().getCdTodoTask())) {
					ServiceReqHeaderDto serviceReqHeaderDtoFam = new ServiceReqHeaderDto();
					serviceReqHeaderDtoFam.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
					String famAssmtAUDMsg = familyAssmtDao.getFamilyAssmtAUD(serviceReqHeaderDtoFam,
							todoListAUDReq.getEventDto().getIdEvent(), todoListAUDReq.getTodoDto().getIdTodoStage());
					log.info("Output Family Assessment AUD " + famAssmtAUDMsg);
				}
				TodoDto todoDtoDtls = setValuesToTodoDto(todoListAUDReq, serviceReqHeaderDto);
				ServiceReqHeaderDto serviceReqHeaderDtoTod0 = new ServiceReqHeaderDto();
				serviceReqHeaderDtoTod0.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				TodoDto todoDtoid = todoDao.todoAUD(todoDtoDtls, serviceReqHeaderDtoTod0);
				todoListAUDRes.setIdTodo(todoDtoid.getIdTodo());
			} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.TODO_MODE_NEW_FCE_TODO)) {

				updateEventStatus(todoListAUDReq);

				TodoDto todoDtoDtls = setValuesToTodoDto(todoListAUDReq, serviceReqHeaderDto);
				ServiceReqHeaderDto serviceReqHeaderDtoTod0 = new ServiceReqHeaderDto();
				serviceReqHeaderDtoTod0.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				TodoDto todoDtoid = todoDao.todoAUD(todoDtoDtls, serviceReqHeaderDtoTod0);
				todoListAUDRes.setIdTodo(todoDtoid.getIdTodo());
			}

			else {
				EventDto eventDto = new EventDto();
				if (null == todoListAUDReq.getTodoDto().getIdTodoEvent()
						|| todoListAUDReq.getTodoDto().getIdTodoEvent().equals(ServiceConstants.ZERO_VAL)) {
					TaskDto taskDto = new TaskDto();
					if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getCdTodoTask())) {
						taskDto = taskDao.getTaskDetails(todoListAUDReq.getTodoDto().getCdTodoTask());
					}
					if (null!=taskDto && !TypeConvUtil.isNullOrEmpty(taskDto.getCdTaskEventType())) {
						if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoCase())) {
							eventDto.setIdCase(todoListAUDReq.getTodoDto().getIdTodoCase());
						}
						eventDto.setCdEventType(taskDto.getCdTaskEventType());
						eventDto.setCdTask(todoListAUDReq.getTodoDto().getCdTodoTask());
						eventDto.setIdCase(todoListAUDReq.getTodoDto().getIdTodoCase());
						eventDto.setEventDescr(todoListAUDReq.getTodoDto().getTodoDesc());
						eventDto.setDtEventOccurred(todoListAUDReq.getTodoDto().getDtTodoCreated());
						eventDto.setIdStage(todoListAUDReq.getTodoDto().getIdTodoStage());
						eventDto.setIdPerson(todoListAUDReq.getTodoDto().getIdTodoPersCreator());
						eventDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_NEW);
						eventDto.setDtEventModified(new Date());
						eventDto.setDtEventCreated(new Date());
						eventDto.setDtLastUpdate(new Date());
						ServiceReqHeaderDto eventAdd = new ServiceReqHeaderDto();
						eventAdd.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
						eventDto = eventDao.eventAUDFunc(eventAdd, eventDto);
						//artf122129-Updates the ID_EVENT in CPS_INVST_DETAIL table for the stage to connect the event with the conclusion page.
						if(todoListAUDReq.getTodoDto().getCdTodoTask().equals(ServiceConstants.INV_CONCLUSION_TASK_CODE)){
							List<CpsInvstDetail> cpsInvstDetailList=cpsInvstDetailDao.getCpsInvstDetailbyParentId(todoListAUDReq.getTodoDto().getIdTodoStage());
							if(cpsInvstDetailList.size()>0){
								CpsInvstDetail cpsInvstDetail=cpsInvstDetailList.get(0);
								cpsInvstDetail.setIdEvent(eventDto.getIdEvent());
								cpsInvstDetailDao.updtCpsInvstDetail(cpsInvstDetail,ServiceConstants.REQ_FUNC_CD_UPDATE);
							}
						}
					}
				}
				TodoDto todoDtoDtls = setValuesToTodoDto(todoListAUDReq, serviceReqHeaderDto);
				ServiceReqHeaderDto serviceReqHeaderDtoTod0 = new ServiceReqHeaderDto();
				serviceReqHeaderDtoTod0.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
				if (null == todoListAUDReq.getTodoDto().getIdTodoEvent()
						|| todoListAUDReq.getTodoDto().getIdTodoEvent().equals(ServiceConstants.ZERO_VAL)) {
					if (null != eventDto.getIdEvent()) {
						todoDtoDtls.setIdTodoEvent(eventDto.getIdEvent());
					}
				}
				TodoDto todoDtoid = todoDao.todoAUD(todoDtoDtls, serviceReqHeaderDtoTod0);
				todoListAUDRes.setIdTodo(todoDtoid.getIdTodo());
			}
		}
		log.info("TransactionId :" + todoListAUDReq.getTransactionId());
		return todoListAUDRes;
	}

	/**
	 * when task is re-assinged and has pending approval task,
	 * update the approvers to new approver.
	 * @param todoListAUDReq
	 */
	private void updatePendingApprovalforTask(TodoListAUDReq todoListAUDReq) {

		if(!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoEvent()) && !TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoPersAssigned()) &&
				Long.parseLong(todoListAUDReq.getUserId()) != todoListAUDReq.getTodoDto().getIdTodoPersAssigned()){
			EventDto eventDto = eventDao.getEventByid(todoListAUDReq.getTodoDto().getIdTodoEvent());
			if(!TypeConvUtil.isNullOrEmpty(eventDto) && eventDto.getCdEventType().equalsIgnoreCase(ServiceConstants.APPROVAL_EVENT_TYPE)
					&& eventDto.getCdEventStatus().equalsIgnoreCase(ServiceConstants.EVENTSTATUS_PROCESS)){
				approversDao.updateApproversbyIdToDo(todoListAUDReq.getTodoDto().getIdTodoEvent(), todoListAUDReq.getTodoDto().getIdTodo(), todoListAUDReq.getTodoDto().getIdTodoPersAssigned());
			}
		}
		;
	}

	/**
	 *
	 * Method Description: This Method is to get response through service layer
	 * by giving required input in request object(TodoListDelReq) Service Name:
	 * CCMN19S
	 *
	 * @param assignSaveGroupReq
	 * @return AssignSaveGroupRes
	 */
	@Override
	public AssignSaveGroupRes assignSaveGroup(AssignSaveGroupReq assignSaveGroupReq) {
		AssignSaveGroupRes assignSaveGroupRes = new AssignSaveGroupRes();
		TreeMap<Long, String> idApproverMap = new TreeMap<Long, String>();
		PostEventOPDto postEventOPDto = new PostEventOPDto();
		String RetVal = "";
		String saveStatus = "";
		StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
		// for (int i = 0; i < assignSaveGroupReq.getUlPageSizeNbr(); i++) {
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setCdTask(ServiceConstants.NULL_VALUE);
		if (assignSaveGroupReq.getAssignSaveGroupDto().size() > 0) {
			for (AssignSaveGroupDto assignSaveGroupDto : assignSaveGroupReq.getAssignSaveGroupDto()) {
				inCheckStageEventStatusDto.setIdStage(assignSaveGroupDto.getIdStage());
				if (!TypeConvUtil.isNullOrEmpty(assignSaveGroupReq.getReqFuncCd())) {
					inCheckStageEventStatusDto.setCdReqFunction(assignSaveGroupReq.getReqFuncCd());
				}
				Boolean eventStageStatus = checkStageEventStatus.chkStgEventStatus(inCheckStageEventStatusDto);
				if (eventStageStatus) {
					RetVal = ServiceConstants.SUCCESS;
				} else {
					RetVal = ServiceConstants.FAIL;
				}
			}
		}
		// }
		if (RetVal.equalsIgnoreCase(ServiceConstants.SUCCESS)) {
			if (assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.INTAKE_NON_APS)
					|| assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.INTAKE_NON_INT)
					|| assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.NON_INT_FAD)) {
				postEventOPDto = postEvent(assignSaveGroupReq, 0, ServiceConstants.REQ_IND_AUD_UPDATE);
				if (!TypeConvUtil.isNullOrEmpty(postEventOPDto.getIdEvent())) {
					saveStatus = ServiceConstants.SUCCESS;
				}
			} else if (assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.NON_INT_FUL_SVC)
					|| assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.INTAKE_FULL_SVC)) {
				ServiceReqHeaderDto serviceReqHeaderDtos = new ServiceReqHeaderDto();
				Date date = new Date();
				for (int i = 0; i < assignSaveGroupReq.getAssignSaveGroupDto().size(); i++) {
					TreeMap<Long, String> idEventMap = new TreeMap<Long, String>();
					List<LegalActionRtrvOutDto> idEventList = null;
					StageDto stageDetail = stageDao
							.getStageById(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage());
					stagePersonLinkDto.setCdStagePersType(ServiceConstants.STG_PERSONLINK_STAFF);
					stagePersonLinkDto.setCdStagePersSearchInd(ServiceConstants.STR_ZERO_VAL);
					stagePersonLinkDto.setStagePersNotes(ServiceConstants.EMPTY_STRING);
					stagePersonLinkDto.setDtStagePersLink(date);
					stagePersonLinkDto.setCdStagePersRelInt(ServiceConstants.EMPTY_STRING);
					stagePersonLinkDto.setIndStagePersReporter(ServiceConstants.STR_ZERO_VAL);
					stagePersonLinkDto.setIndStagePersInLaw(ServiceConstants.STR_ZERO_VAL);
					stagePersonLinkDto.setIndStagePersEmpNew(ServiceConstants.IND_STGPER_EMPNEW);
					stagePersonLinkDto
							.setDtLastUpdate(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getTsLastUpdate());
					stagePersonLinkDto.setIdStage(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage());
					stagePersonLinkDto.setIdCase(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase());
					stagePersonLinkDto.setIdPerson(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson());
					stagePersonLinkDto
							.setIdStagePersonLink(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStagePerson());
					stagePersonLinkDto
							.setCdStagePersRole(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdStagePersRole());
					serviceReqHeaderDtos
							.setReqFuncCd(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction());
					String retMsg = stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDtos);
					log.info("Output StagePersonLinkAud Message " + retMsg);
					if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
							.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)) {
						ServiceReqHeaderDto todoArchIpDto = new ServiceReqHeaderDto();
						postEventOPDto = postEvent(assignSaveGroupReq, i, ServiceConstants.REQ_IND_AUD_ADD);
						TodoDto todoDtoDtls = setValuesToTodoDtoAssign(assignSaveGroupReq, i);
						todoArchIpDto.setReqFuncCd(todoDtoDtls.getReqFuncCd());
						TodoDto todoDtoId = todoDao.todoAUD(todoDtoDtls, todoArchIpDto);
						if (!TypeConvUtil.isNullOrEmpty(todoDtoId.getIdTodo())) {
							saveStatus = ServiceConstants.SUCCESS;
						}
						if (Stream.of(FPR, ServiceConstants.CSTAGES_FSU, ServiceConstants.CSTAGES_FRE)
								.anyMatch(stageDetail.getCdStage()::equalsIgnoreCase)) {
							idEventMap = workLoadDao.getLatestChildPlanEvent(
									assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase(),
									stageDetail.getCdStage());
						}
						// UIDS - 2.6.1.1 - Assign Removal of Case - Legal
						// Action and Outcome
						// get the List of idEvents for the given idCase
						idEventList = leglActnModificationDao.fetchLegalActionEventIds(
								assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase());
						// ADS Change for triggering calendar events for initial
						// FSNA creation secondary
						// worker reassignment for FRE and FSU stage
						if (ServiceConstants.CSTAGES_FSU.equalsIgnoreCase(stageDetail.getCdStage())
								|| ServiceConstants.CSTAGES_FRE.equalsIgnoreCase(stageDetail.getCdStage())) {
							createCalendarEventForReassignment(stageDetail.getIdStage(),
									assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson(),
									assignSaveGroupReq.getHostName(), ServiceConstants.YES, ServiceConstants.YES);
						}
					} else if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
							.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
						Long idPerson = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson();
						Long personByIdTodoPersAssigned = assignSaveGroupReq.getAssignSaveGroupDto().get(i)
								.getIdPerson();
						Long stageId = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage();
						Date todoCompleteddt = ServiceConstants.NULL_VALDAT;
						postEventOPDto = postEvent(assignSaveGroupReq, i, ServiceConstants.REQ_IND_AUD_DELETE);
						List<TodoDto> todoDto = todoDao.getTodoUpdateDel(idPerson, personByIdTodoPersAssigned, stageId,
								todoCompleteddt);
						if (!TypeConvUtil.isNullOrEmpty(todoDto)) {
							saveStatus = ServiceConstants.SUCCESS;
						}
					} else if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
							.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
						if (ServiceConstants.PRIMARY_ROLE_STAGE_OPEN.equalsIgnoreCase(
								assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdStagePersRole())) {
							ServiceReqHeaderDto todoArchIpDtoAdd = new ServiceReqHeaderDto();
							postEventOPDto = postEvent(assignSaveGroupReq, i, ServiceConstants.UNASSIGNED);
							postEventOPDto = postEvent(assignSaveGroupReq, i, ServiceConstants.REQ_IND_AUD_UPDATE);
							TodoDto todoDtoDtls = setValuesToTodoDtoAssign(assignSaveGroupReq, i);
							todoArchIpDtoAdd.setReqFuncCd(todoDtoDtls.getReqFuncCd());
							TodoDto todoDtoId = todoDao.todoAUD(todoDtoDtls, todoArchIpDtoAdd);
							log.info("TODO AUD output " + todoDtoId.getIdTodo());
							Long idPerson = 0l;
							Long personByIdTodoPersAssigned = 0l;
							if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
									.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
								idPerson = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson();
								personByIdTodoPersAssigned = assignSaveGroupReq.getAssignSaveGroupDto().get(i)
										.getIdPerson();
							} else if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
									.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
								idPerson = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson();
								personByIdTodoPersAssigned = assignSaveGroupReq.getAssignSaveGroupDto().get(i)
										.getSysIdPriorPerson();
							}
							Long stageId = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage();
							Date todoCompleteddt = ServiceConstants.NULL_VALDAT;
							List<TodoDto> todoDto = todoDao.getTodoUpdateDel(idPerson, personByIdTodoPersAssigned,
									stageId, todoCompleteddt);
							log.info("Deleted TODO Records " + todoDto.size());
							UnitDto unitDto = unitDao
									.getUnitDtlsById(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdUnit());
							assignSaveGroupReq.getAssignSaveGroupDto().get(i)
									.setSysIdPriorPerson(unitDto.getIdPerson());
							if (!(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson()
									.equals(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson()))) {
								ServiceReqHeaderDto todoArchIpDtoS = new ServiceReqHeaderDto();
								// todoArchIpDtoS.setcReqFuncCd(assignSaveGroupReq.getcReqFuncCd());
								TodoDto todoDtoSup = new TodoDto();
								Date dateSup = new Date();
								todoDtoSup.setCdTodoTask(ServiceConstants.EMPTY_STRING);
								todoDtoSup.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
								todoDtoSup.setDtTodoCompleted(dateSup);
								todoDtoSup.setDtTodoCreated(dateSup);
								todoDtoSup.setDtTodoDue(dateSup);
								todoDtoSup.setCdTodoTask(ServiceConstants.EMPTY_STRING);
								todoDtoSup.setIdTodoCase(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase());
								todoDtoSup.setIdTodoPersCreator(assignSaveGroupReq.getUlIdPerson());
								todoDtoSup
										.setIdTodoStage(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage());
								todoDtoSup.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
								if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
										.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)) {
									todoDtoSup.setTodoDesc(ServiceConstants.SECONDARY_ASSIGN);
									todoDtoSup.setIdTodoPersAssigned(
											assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson());
									todoDtoSup.setIdTodoPersWorker(
											assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson());
								} else if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
										.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
									todoDtoSup.setIdTodoPersWorker(
											assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson());
									String txtEvnDescp = ServiceConstants.TXT_TODO_DESC;
									todoDtoSup.setTodoDesc(txtEvnDescp.concat(
											(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getNmPersonFull())));
									todoDtoSup.setIdTodoPersAssigned(
											assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson());
								}
								// TodoDto todoDtoSup =
								// setValuesToTodoDtoAssign(assignSaveGroupReqDup,
								// i);
								todoArchIpDtoS.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
								TodoDto todoDtoIdDtls = todoDao.todoAUD(todoDtoSup, todoArchIpDtoS);
								log.info("Output TODO AUD " + todoDtoIdDtls.getIdTodo());
							}
							ServiceReqHeaderDto serviceReqHeaderDtoEmp = new ServiceReqHeaderDto();
							serviceReqHeaderDtoEmp.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
							String empRtnMsg = employeeDao.getEmployeeUpdate(
									assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson(),
									serviceReqHeaderDtoEmp);
							log.info("Update Employ Message " + empRtnMsg);
							UnitDto unitDtoReg = unitDao
									.getUnitDtlsById(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdUnit());
							Long idStage = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage();
							Long idUnit = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdUnit();
							String cdStageRegion = unitDtoReg.getCdUnitRegion();
							String stageRtnMsg = stageDao.getStageupdate(idStage, idUnit, cdStageRegion);
							log.info("Update Stage Message " + stageRtnMsg);
							if (!ObjectUtils.isEmpty((assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase()))) {
								Long idCase = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase();
								String cdCaseRegion = unitDtoReg.getCdUnitRegion();
								String capCaseRtnMsg = capsCaseDao.getCapCaseUpdate(idCase, cdCaseRegion);
								log.info("Update Case Message " + capCaseRtnMsg);
							}
							saveStatus = ServiceConstants.SUCCESS;
							if (FPR.equalsIgnoreCase(stageDetail.getCdStage())
									|| ServiceConstants.CSTAGES_FSU.equalsIgnoreCase(stageDetail.getCdStage())
									|| ServiceConstants.CSTAGES_FRE.equalsIgnoreCase(stageDetail.getCdStage())) {
								idEventMap = workLoadDao.getLatestChildPlanEvent(
										assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase(),
										stageDetail.getCdStage());
							}
							// UIDS - 2.6.1.1 - Assign Removal of Case - Legal
							// Action and Outcome
							// get the List of idEvents for the given idCase
							idEventList = leglActnModificationDao.fetchLegalActionEventIds(
									assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase());
							// ADS Change for triggering calendar events for
							// initial FSNA creation primary
							// worker reassignment for FRE and FSU stage
							if (ServiceConstants.CSTAGES_FSU.equalsIgnoreCase(stageDetail.getCdStage())
									|| ServiceConstants.CSTAGES_FRE.equalsIgnoreCase(stageDetail.getCdStage())) {
								createCalendarEventForReassignment(stageDetail.getIdStage(),
										assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson(),
										assignSaveGroupReq.getHostName(), ServiceConstants.YES, ServiceConstants.YES);
							}
						}
					}
					// Create CPOS initial /Review reminders for CPS primary and
					// secondary worker assignment changes
					if ((ServiceConstants.CPGRMS_CPS
							.equals(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdStageProgram()))
							&& ServiceConstants.CSTAGES_SUB
							.equals(stageDetail.getCdStage())
							&& (ServiceConstants.REQ_IND_AUD_ADD.equalsIgnoreCase(
							assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()))
							|| (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
							.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE))) {
						// retrieve all child plan events if the event status is
						// APRV need to create CPOS review reminder for other
						// status need to create initial review
						List<ChildPlanOfServiceDtlDto> childPlans = childPlanBeanDao.getChildPlanEvents(
								assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage(),
								assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase());

						for (ChildPlanOfServiceDtlDto childPlan : childPlans) {
							Date toDay = new Date();
							Calendar cal = Calendar.getInstance();
							//Added the null condition for DtCVSRemoval - Warranty defect 12162
							if (!ObjectUtils.isEmpty(childPlan.getDtCVSRemoval())) {
								cal.setTime(childPlan.getDtCVSRemoval());
							}
							cal.add(Calendar.DATE, 45);
							Calendar currentCalDate = Calendar.getInstance();
							if (ServiceConstants.CHILD_PLAN_INITIAL.equals(childPlan.getCdCspPlanType())
									&& !ServiceConstants.EVENTSTATUS_APPROVE.equals(childPlan.getCdEventStatus())
									&& cal.after(currentCalDate)) {
								// send COPS Initial reminder
								sendReminder(childPlan.getIdChildPlanEvent(), assignSaveGroupReq.getTransactionId(),
										childPlan.getDtCVSRemoval(), assignSaveGroupReq.getHostName(), false);
							}

							else if (ServiceConstants.REVIEW.equals(childPlan.getCdCspPlanType())
									&& ServiceConstants.EVENTSTATUS_APPROVE.equals(childPlan.getCdEventStatus())
									&& childPlan.getDtCspNextReview().after(toDay)) {
								// send COPS Review reminder
								sendReminder(childPlan.getIdChildPlanEvent(), assignSaveGroupReq.getTransactionId(),
										childPlan.getDtCspNextReview(), assignSaveGroupReq.getHostName(), true);
							}

						}
					}
					if (!ObjectUtils.isEmpty(idEventMap)) {
						idApproverMap.putAll(idEventMap);
					}
					// UIDS - 2.6.1.1 - Assign Removal of Case - Legal Action
					// and Outcome
					// set the List of idEvents with other details in the
					// assignSaveGroupRes to send
					// email
					assignSaveGroupRes.setIdEvents(idEventList);
				}
			} else if (assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.INTAKE_APS)) {
				postEventOPDto = postEvent(assignSaveGroupReq, 0, ServiceConstants.REQ_IND_AUD_UPDATE);
				for (int i = 0; i < assignSaveGroupReq.getAssignSaveGroupDto().size(); i++) {
					if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
							.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
						assignSaveGroupReq.setReqFuncCd("");
						ServiceReqHeaderDto serviceReqHeaderDtoUpd = new ServiceReqHeaderDto();
						TodoDto todoDtoDtls = setValuesToTodoDtoAssign(assignSaveGroupReq, i);
						serviceReqHeaderDtoUpd.setReqFuncCd(todoDtoDtls.getReqFuncCd());
						TodoDto todoDtoId = todoDao.todoAUD(todoDtoDtls, serviceReqHeaderDtoUpd);
						log.info("Update TODO Message " + todoDtoId.getIdTodo());
						UnitDto unitDto = unitDao
								.getUnitDtlsById(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdUnit());
						if (!(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson()
								.equals(unitDto.getIdPerson()))) {
							assignSaveGroupReq.setReqFuncCd(ServiceConstants.SUPERVISOR);
							ServiceReqHeaderDto serviceReqHeaderDtoSp = new ServiceReqHeaderDto();
							TodoDto todoDtoSup = setValuesToTodoDtoAssign(assignSaveGroupReq, i);
							serviceReqHeaderDtoSp.setReqFuncCd(todoDtoSup.getReqFuncCd());
							todoDtoSup.setIdTodoPersAssigned(
									assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson());
							TodoDto todoDtoIdDt = todoDao.todoAUD(todoDtoSup, serviceReqHeaderDtoSp);
							log.info("Update TODO Message " + todoDtoIdDt.getIdTodo());
						}
						ServiceReqHeaderDto serviceReqHeaderDtoEmp = new ServiceReqHeaderDto();
						serviceReqHeaderDtoEmp.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
						String empRtnMsg = employeeDao.getEmployeeUpdate(
								assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson(),
								serviceReqHeaderDtoEmp);
						log.info("Update Employee Message " + empRtnMsg);
						UnitDto unitDtoReg = unitDao
								.getUnitDtlsById(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdUnit());
						Long idStage = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage();
						Long idUnit = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdUnit();
						String cdStageRegion = unitDtoReg.getCdUnitRegion();
						String stageRtnMsg = stageDao.getStageupdate(idStage, idUnit, cdStageRegion);
						log.info("Update Stage Message " + stageRtnMsg);
						if (!ObjectUtils.isEmpty((assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase()))) {
							Long idCase = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase();
							String cdCaseRegion = unitDtoReg.getCdUnitRegion();
							String capCaseRtnMsg = capsCaseDao.getCapCaseUpdate(idCase, cdCaseRegion);
							log.info("Update Case Message " + capCaseRtnMsg);
						}
						saveStatus = ServiceConstants.SUCCESS;
					}
				}
			}
		}
		//Code to create notification for the workers when a referral exists
		if (assignSaveGroupReq.getStageIds().size() > 0) {
			for (Long idStage : assignSaveGroupReq.getStageIds()) {
				if (!ObjectUtils.isEmpty(assignSaveGroupReq.getSsccReferralIdmap())) {
					Long idSSCCReferral = assignSaveGroupReq.getSsccReferralIdmap().get(idStage);
					if (null != idSSCCReferral && idSSCCReferral > 0) {
						createEventForNotificationOnReferral(idStage, idSSCCReferral, assignSaveGroupReq.getPersonCreated());
					}
				}
			}
		}


		assignSaveGroupRes.setSaveStatus(saveStatus);
		assignSaveGroupRes.setIdApproverMap(idApproverMap);
		log.info("TransactionId :" + assignSaveGroupReq.getTransactionId());
		return assignSaveGroupRes;
	}

	private void createEventForNotificationOnReferral(Long idStage, Long idSSSCCReferral, Long personCreated) {
		Event event = new Event();
		String personsAssignedToStage = getAssignedPersonsIdForStage(idStage);
		try{
			event.setObjectId(idSSSCCReferral);
			event.setObject(EventObject.SSCC_REFERRAL);
			event.setPersonsId(personsAssignedToStage);
			event.setAction(EventAction.CREATED);
			event.setCreatedPersonId(personCreated);
			event.setLastUpdatePersonId(personCreated);
			notificationApi.createEvent(event);
			log.info("Created Notification event for Referral Id: "+idSSSCCReferral+ " assigned to "+personsAssignedToStage);
		} catch (Exception e) {
			log.fatal(e.getMessage() + " for Referral Id : "+idSSSCCReferral+ " assigned to "+personsAssignedToStage );
		}

	}

	private String getAssignedPersonsIdForStage(Long stageId) {
		String personsIds = null;
		List<AssignmentGroupDto> assignmentGroupDto = new ArrayList<AssignmentGroupDto>();
		try {
			assignmentGroupDto = assignDao.getAssignmentgroup(stageId);
		} catch (ServiceLayerException e) {
			log.fatal(e.getMessage());
		}
		if(!CollectionUtils.sizeIsEmpty(assignmentGroupDto)){
			personsIds = assignmentGroupDto.stream().filter(assignmentGroup -> null != assignmentGroup.getExternalOrg() && assignmentGroup.getExternalOrg().equalsIgnoreCase("ESSC"))
					.map(assignmentGroup -> assignmentGroup.getIdPerson().toString()).collect(Collectors.joining(","));
		}
		return personsIds;
	}

	// this method will send request to create outlook calendar reminder
	private void sendReminder(Long idChildPlanEvent, String transactionId, Date dtCVSRemoval, String hostName,
							  boolean indReview) {
		CommonHelperReq commonHelperReq = new CommonHelperReq();
		List<Long> idEventList = new ArrayList<Long>();
		idEventList.add(idChildPlanEvent);
		commonHelperReq.setIdEventList(idEventList);
		commonHelperReq.setTransactionId(transactionId);
		commonHelperReq.setDtCVSRemoval(dtCVSRemoval);
		commonHelperReq.setHostName(hostName);
		commonHelperReq.setIndReview(indReview);
		conservatorshipService.fetchEmployeeEmail(commonHelperReq);
	}

	public TodoDto setValuesToTodoDto(TodoListAUDReq todoListAUDReq, ServiceReqHeaderDto serviceReqHeaderDto) {
		TodoDto todoDtoVal = new TodoDto();
		todoDtoVal.setCdTodoTask(todoListAUDReq.getTodoDto().getCdTodoTask());
		todoDtoVal.setCdTodoType(todoListAUDReq.getTodoDto().getCdTodoType());
		todoDtoVal.setDtTodoCompleted(todoListAUDReq.getTodoDto().getDtTodoCompleted());
		Date dtTodoCreated = new Date();
		Date dtTodoDue = null;
		Date dtTodoTaskDue = null;
		if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getDtTodoCreated())) {
			dtTodoCreated = todoListAUDReq.getTodoDto().getDtTodoCreated();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtTodoCreated);
			// cal.add(Calendar.DATE, 1);
			dtTodoCreated = cal.getTime();
		}
		todoDtoVal.setDtTodoCreated(dtTodoCreated);
		if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getDtTodoDue())) {
			dtTodoDue = todoListAUDReq.getTodoDto().getDtTodoDue();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtTodoDue);
			// cal.add(Calendar.DATE, 1);
			dtTodoDue = cal.getTime();
		}
		if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getDtTodoTaskDue())) {
			dtTodoTaskDue = todoListAUDReq.getTodoDto().getDtTodoTaskDue();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtTodoTaskDue);
			// cal.add(Calendar.DATE, 1);
			dtTodoTaskDue = cal.getTime();
		}
		todoDtoVal.setDtTodoDue(dtTodoDue);
		todoDtoVal.setDtTodoTaskDue(dtTodoTaskDue);
		todoDtoVal.setDtLastUpdate(todoListAUDReq.getTodoDto().getDtLastUpdate());
		todoDtoVal.setIdTodoCase(todoListAUDReq.getTodoDto().getIdTodoCase());
		todoDtoVal.setIdTodoPersAssigned(todoListAUDReq.getTodoDto().getIdTodoPersAssigned());
		todoDtoVal.setIdTodoPersCreator(todoListAUDReq.getTodoDto().getIdTodoPersCreator());
		todoDtoVal.setIdTodoPersWorker(todoListAUDReq.getTodoDto().getIdTodoPersWorker());
		todoDtoVal.setIdTodoStage(todoListAUDReq.getTodoDto().getIdTodoStage());
		todoDtoVal.setIdTodo(todoListAUDReq.getTodoDto().getIdTodo());
		todoDtoVal.setTodoDesc(todoListAUDReq.getTodoDto().getTodoDesc());
		todoDtoVal.setTodoLongDesc(todoListAUDReq.getTodoDto().getTodoLongDesc());
		if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEXT_APPROVE)) {
			todoDtoVal.setIdTodoEvent(todoListAUDReq.getApproversDto().getId_Approval());
		} else {
			todoDtoVal.setIdTodoEvent(todoListAUDReq.getTodoDto().getIdTodoEvent());
		}
		if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEW_APPROVE)
				|| serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEXT_APPROVE)) {
			todoDtoVal.setIdTodoPersCreator(null);
		} else {
			todoDtoVal.setIdTodoPersCreator(todoListAUDReq.getTodoDto().getIdTodoPersCreator());
		}
		log.info("TransactionId :" + todoListAUDReq.getTransactionId());
		return todoDtoVal;
	}

	public EventDto setValuesToEventDto(TodoListAUDReq todoListAUDReq, ServiceReqHeaderDto serviceReqHeaderDto) {
		EventDto eventDto = new EventDto();
		Date dtevnent = new Date();
		Date eventOccured = new Date();
		if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEW_APPROVE)) {
			eventDto.setCdEventType(ServiceConstants.EVENT_TYPE_APPRV);
			eventDto.setCdTask(todoListAUDReq.getTodoDto().getCdTodoTask());
			//artf197007 : Allowing user to enter 128 char and saving only 120 char.
			String actualToDoDesc = todoListAUDReq.getTodoDto().getTodoDesc();
			eventDto.setEventDescr(actualToDoDesc.length() > 120 ? actualToDoDesc.substring(0,120) : actualToDoDesc);
			if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getDtTodoCreated())) {
				eventOccured = todoListAUDReq.getTodoDto().getDtTodoCreated();
				Calendar cal = Calendar.getInstance();
				cal.setTime(eventOccured);
				// cal.add(Calendar.DATE, 1);
				eventOccured = cal.getTime();
			}
			eventDto.setDtEventOccurred(eventOccured);
			eventDto.setIdStage(todoListAUDReq.getEventDto().getIdStage());
			eventDto.setIdPerson(todoListAUDReq.getTodoDto().getIdTodoPersCreator());
			eventDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_PROCESS);
			eventDto.setDtEventModified(dtevnent);
			eventDto.setDtEventCreated(dtevnent);
			eventDto.setDtLastUpdate(dtevnent);
			if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoCase())) {
				eventDto.setIdCase(todoListAUDReq.getTodoDto().getIdTodoCase());
			}
		} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_ASSGIN)) {
			eventDto.setCdEventType(todoListAUDReq.getEventDto().getCdEventType());
			eventDto.setCdTask(todoListAUDReq.getTodoDto().getCdTodoTask());
			eventDto.setEventDescr(todoListAUDReq.getEventDto().getEventDescr());
			// ARC_UTLGetDateAndTime
			eventDto.setDtEventOccurred(todoListAUDReq.getTodoDto().getDtTodoCreated());
			eventDto.setIdStage(todoListAUDReq.getTodoDto().getIdTodoStage());
			eventDto.setIdPerson(todoListAUDReq.getTodoDto().getIdTodoPersCreator());
			eventDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_NEW);
			eventDto.setDtEventModified(dtevnent);
			eventDto.setDtEventCreated(dtevnent);
			eventDto.setDtLastUpdate(dtevnent);
			if (!TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoCase())) {
				eventDto.setIdCase(todoListAUDReq.getTodoDto().getIdTodoCase());
			}
		}
		log.info("TransactionId :" + todoListAUDReq.getTransactionId());
		return eventDto;
	}

	public String updateEventStatus(TodoListAUDReq todoListAUDReq) {
		EventDto eventDto = new EventDto();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
		String retMsg = ServiceConstants.EMPTY_STR;
		Date evnCreatedDate = new Date();
		for (int i = 0; i < todoListAUDReq.getUidEvent().size(); i++) {
			if (!todoListAUDReq.getUidEvent().get(i).equals(0l)) {
				eventDto = eventDao.getEventByid(todoListAUDReq.getUidEvent().get(i));
				if (!ObjectUtils.isEmpty(eventDto)) {
					eventDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_PENDING);
					if (ServiceConstants.TODO_MODE_NEW_FCE_TODO.equals(todoListAUDReq.getReqFuncCd())) {
						String eventDescr = EventUtil.getDescription(todoListAUDReq.getTodoDto().getCdTodoTask(),
								ServiceConstants.EVENTSTATUS_PENDING);
						eventDto.setEventDescr(eventDescr);

					}
					eventDto.setDtEventModified(evnCreatedDate);
					// eventDto.setDtEventCreated(evnCreatedDate);
					// if (i == 1) {
					if (!ServiceConstants.FAM_PLAN_TYPE_FPP
							.equalsIgnoreCase(todoListAUDReq.getTodoDto().getCdTodoType())) {
						eventDto.setIdEvent(todoListAUDReq.getUidEvent().get(i));
						retMsg = eventDao.getEventDetailsUpdate(serviceReqHeaderDto, eventDto);
					} /*
					 * else {
					 * eventDto.setIdEvent(todoListAUDReq.getUidEvent().
					 * get(i)); retMsg = eventDao.getEventDetailsUpdate(
					 * serviceReqHeaderDto, eventDto); }
					 */
					// }
				}
			}
		}

		log.info("TransactionId :" + todoListAUDReq.getTransactionId());
		return retMsg;
	}

	public String approvalEventLinkAUD(TodoListAUDReq todoListAUDReq, ServiceReqHeaderDto serviceReqHeaderDto,
									   ApprovalDto approvalDto) {
		ApprovalEventLinkDto approvalEventLinkDto = new ApprovalEventLinkDto();
		String retMsg = ServiceConstants.EMPTY_STR;
		approvalEventLinkDto.setIdApproval(approvalDto.getIdApproval());
		for (int i = 0; i < todoListAUDReq.getUidEvent().size(); i++) {
			if (!todoListAUDReq.getUidEvent().get(i).equals(0l)) {
				approvalEventLinkDto.setIdEvent(todoListAUDReq.getUidEvent().get(i));
				retMsg = approvalEventLinkDao.getApprovalEventLinkAUD(serviceReqHeaderDto, approvalEventLinkDto);
			}
		}
		log.info("TransactionId :" + todoListAUDReq.getTransactionId());
		return retMsg;
	}

	public PostEventOPDto postEvent(AssignSaveGroupReq assignSaveGroupReq, int i, String action) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		PostEventOPDto postEventOPDto = new PostEventOPDto();
		PostEventDto postEventDto = new PostEventDto();
		List<PostEventDto> postEventDtoList = new ArrayList<PostEventDto>();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		Date dtlastUpdate = new Date();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
		postEventIPDto.setCdEventType(ServiceConstants.EVENTSTATUS_ASSIGNMENT);
        //artf193346 : setting personcreated instead of the ulIdPerson
		postEventIPDto.setIdPerson(assignSaveGroupReq.getPersonCreated());
		postEventDto.setIdPerson(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson());
		String username = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getNmPersonFull();
		if (null != action && ServiceConstants.UNASSIGNED.equals(action)) {
			postEventDto.setIdPerson(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson());
			username = assignSaveGroupReq.getAssignSaveGroupDto().get(i).getNmPersonFullPriorPerson();
		}
		postEventIPDto.setIdStage(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage());
		postEventIPDto.setDtEventOccurred(dtlastUpdate);
		String txtEvnDescp = "";
		if (null != action && ServiceConstants.REQ_IND_AUD_UPDATE.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_PRIMARY;
		} else if (null != action && ServiceConstants.UNASSIGNED.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_PRI_UNASSIGN;
		} else if (null != action && ServiceConstants.REQ_IND_AUD_ADD.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_SECONDARY;
		} else if (null != action && ServiceConstants.REQ_IND_AUD_DELETE.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_SEC_UNASSIGN;
		}
		String txtEvntDesc = txtEvnDescp.concat(username);
		DateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
		String date = dateFormat.format(dtlastUpdate);
		dateFormat.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
		DateFormat timeFormat = new SimpleDateFormat(ServiceConstants.TIME_FORMAT);
		timeFormat.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
		String time = timeFormat.format(dtlastUpdate);
		txtEvntDesc = txtEvntDesc + ServiceConstants.SINGLE_WHITESPACE + date + ServiceConstants.AT + time;
		postEventIPDto.setEventDescr(txtEvntDesc);
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
		postEventDtoList.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventDtoList);
		postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
		log.info("TransactionId :" + assignSaveGroupReq.getTransactionId());
		return postEventOPDto;
	}

	public TodoDto setValuesToTodoDtoAssign(AssignSaveGroupReq assignSaveGroupReq, int i) {
		TodoDto todoDtoVal = new TodoDto();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		date = cal.getTime();
		todoDtoVal.setCdTodoTask(ServiceConstants.EMPTY_STRING);
		todoDtoVal.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
		todoDtoVal.setDtTodoCompleted(date);
		todoDtoVal.setDtTodoCreated(date);
		todoDtoVal.setDtTodoDue(date);
		// todoDtoVal.setCdTodoTask(ServiceConstants.EMPTY_STRING);
		todoDtoVal.setIdTodoCase(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdCase());
		todoDtoVal.setIdTodoPersCreator(assignSaveGroupReq.getUlIdPerson());
		todoDtoVal.setIdTodoStage(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdStage());
		todoDtoVal.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
		if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
				.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)) {
			todoDtoVal.setTodoDesc(ServiceConstants.SECONDARY_ASSIGN);
			todoDtoVal.setIdTodoPersAssigned(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson());
			todoDtoVal.setIdTodoPersWorker(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson());
		} else if (assignSaveGroupReq.getAssignSaveGroupDto().get(i).getCdScrDataAction()
				.equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
			todoDtoVal.setIdTodoPersWorker(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson());
			if (assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.SUPERVISOR)) {
				String txtEvnDescp = ServiceConstants.TXT_TODO_DESC;
				todoDtoVal.setTodoDesc(
						txtEvnDescp.concat((assignSaveGroupReq.getAssignSaveGroupDto().get(i).getNmPersonFull())));
				todoDtoVal
						.setIdTodoPersAssigned(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getSysIdPriorPerson());
			} else {
				todoDtoVal.setTodoDesc(ServiceConstants.PRIMARY_ASSIGN);
				todoDtoVal.setIdTodoPersAssigned(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson());
			}
		}
		todoDtoVal.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		log.info("TransactionId :" + assignSaveGroupReq.getTransactionId());
		return todoDtoVal;
	}

	/**
	 *
	 * Method Description: This Method will perform the event details search and
	 * populate the records based on the input request. Service Name: CCMN33S
	 *
	 * DAM: CCMN87D,CCMN85D,CCMN82D
	 *
	 * @param eventReq
	 * @return EventDetailRes @
	 */
	@Transactional
	public EventDetailRes getEventDetails(EventReq eventReq) {
		EventDetailRes eventDetailRes = new EventDetailRes();
		eventDetailRes.setbIndFilteredSensitiveEvents(ServiceConstants.STRING_IND_N);
		if (TypeConvUtil.isNullOrEmpty(eventReq.getUlIdStage()) && null != eventReq.getEventType()
				&& !TypeConvUtil.isNullOrEmpty(eventReq.getUlIdCase())
				&& TypeConvUtil.isNullOrEmpty(eventReq.getUlIdPerson())) {
			List<EventListDto> caseList = eventDao.getCaseDetailList(eventReq);
			eventDetailRes.setEventSearchDto(caseList);
		}
		if (TypeConvUtil.isNullOrEmpty(eventReq.getUlIdStage()) && !TypeConvUtil.isNullOrEmpty(eventReq.getUlIdCase())
				&& TypeConvUtil.isNullOrEmpty(eventReq.getUlIdPerson())) {
			List<EventListDto> caseList = eventDao.getCaseDetailList(eventReq);
			eventDetailRes.setEventSearchDto(caseList);
		}
		if (TypeConvUtil.isNullOrEmpty(eventReq.getUlIdStage()) && null != eventReq.getStageType()
				&& !TypeConvUtil.isNullOrEmpty(eventReq.getUlIdCase())
				&& TypeConvUtil.isNullOrEmpty(eventReq.getUlIdPerson())) {
			List<EventListDto> caseList = eventDao.getCaseDetailList(eventReq);
			eventDetailRes.setEventSearchDto(caseList);
		}

		if (!TypeConvUtil.isNullOrEmpty(eventReq.getUlIdStage())
				&& !TypeConvUtil.isNullOrEmpty(eventReq.getUlIdCase())) {
			if (PLMT_TSK.equalsIgnoreCase(eventReq.getSzCdTask())) {
				eventDetailRes.setActiveTAsCount(temporaryAbsenceDao.getActiveTemporaryAbsencesForActivePlacements(eventReq.getUlIdStage()));
			}
			List<EventListDto> eventdetails = eventDao.getEventDetails(eventReq);
			eventDetailRes.setEventSearchDto(eventdetails);
		}
		if (!TypeConvUtil.isNullOrEmpty(eventReq.getUlIdStage())
				&& ServiceConstants.REQ_FUNC_REMOVAL.equalsIgnoreCase(eventReq.getReqFuncCd())) {
			List<EventListDto> eventdetails = eventDao.getEventListbyEventType(eventReq,
					ServiceConstants.REMOVAL_EVENT);
			eventDetailRes.setEventSearchDto(eventdetails);
		}
		if (!TypeConvUtil.isNullOrEmpty(eventReq.getUlIdStage())
				&& (eventReq.getUlIdCase() == ServiceConstants.LONG_ZERO_VAL)) {
			List<EventListDto> eventdetails = eventDao.getEventCaseDetails(eventReq);
			eventDetailRes.setEventSearchDto(eventdetails);
		}
		if (!TypeConvUtil.isNullOrEmpty(eventReq.getUlIdPerson())) {
			List<EventListDto> eventdetails = eventDao.getEventDetails(eventReq);
			eventDetailRes.setEventSearchDto(eventdetails);
		}
		if (TypeConvUtil.isNullOrEmpty(eventReq.getUlIdStage()) && !TypeConvUtil.isNullOrEmpty(eventReq.getUlIdCase())
				&& !TypeConvUtil.isNullOrEmpty(eventReq.getUlIdEventPerson())) {
			List<EventListDto> caseList = eventDao.getCaseDetailList(eventReq);
			eventDetailRes.setEventSearchDto(caseList);
		}
		/*eventDetailRes.setbIndFilteredSensitiveEvents(ServiceConstants.STR_ZERO_VAL);
		if (eventReq.getbIndCaseSensitive() != null) {
			if (eventReq.getbIndCaseSensitive().equalsIgnoreCase(ServiceConstants.STRING_IND_N)) {
				eventDetailRes.setbIndFilteredSensitiveEvents(ServiceConstants.STRING_IND_Y);
			}
		}*/
		if (!TypeConvUtil.isNullOrEmpty(eventReq.getUlIdEvent())
				&& !org.springframework.util.CollectionUtils.isEmpty(eventDetailRes.getEventSearchDto())) {
			List<EventListDto> eventdetails = eventDetailRes.getEventSearchDto().stream()
					.filter(eventDto -> eventDto.getIdEvent().equals(eventReq.getUlIdEvent()))
					.collect(Collectors.toList());
			eventDetailRes.setEventSearchDto(eventdetails);
		}

		// The below code has been added for setting the NonCPSStageProgram
		// Indicator in the Event List Dto
		// Check if the Case ID has been passed in the Req, if yes, take it from
		// the request, else use the stage id to fetch the Case ID
		Long idCase = 0l;
		if (!ObjectUtils.isEmpty(eventReq.getUlIdCase())) {
			idCase = eventReq.getUlIdCase();
		} else {
			if (!ObjectUtils.isEmpty(eventReq.getUlIdStage())) {
				idCase = eventDao.getIdCaseForStage(eventReq.getUlIdStage());
			}
		}
		// Once we have the Case ID, check to see if the case has stages with
		// more than one stage program type.
		if (!ObjectUtils.isEmpty(idCase) && idCase > 0) {
			Boolean moreThanOneStageProgram = eventDao.checkIfMultipleStagePrograms(idCase);
			if (moreThanOneStageProgram) {
				Map<Long, String> stageProgramDetails = eventDao.getStagePrograms(idCase);
				eventDetailRes.getEventSearchDto().forEach(e -> {
					if (!ServiceConstants.CAPS_PROG_CPS.equals(stageProgramDetails.get(e.getIdStage()))
							|| ServiceConstants.CAPS_PROG_PRS.equals(stageProgramDetails.get(e.getIdStage()))) {
						e.setNonCpsStageProgram(true);
					}
				});
			} else {
				String cdCaseProgram = eventDao.getCaseProgram(idCase);
				if (!StringUtils.isEmpty(cdCaseProgram)) {
					eventDetailRes.getEventSearchDto().forEach(e -> {
						if (!ServiceConstants.CAPS_PROG_CPS.equals(cdCaseProgram)
								|| ServiceConstants.CAPS_PROG_PRS.equals(cdCaseProgram)) {
							e.setNonCpsStageProgram(true);
						}
					});
				}
			}
		}
		// For Family Plan, also fetch Primary Participant
		eventDetailRes.getEventSearchDto().stream().forEach(eventListDto -> {
			if (!ObjectUtils.isEmpty(eventListDto.getCdTask()) && Arrays
					.stream(ServiceConstants.FAMILY_PLAN_LEGACY_TASK_LIST).anyMatch(eventListDto.getCdTask()::equals)) {
				eventListDto.setLegacyFamilyPlan(true);
			}
			if (!ObjectUtils.isEmpty(eventListDto.getCdTask()) && Arrays.stream(ServiceConstants.FAMILY_PLAN_TASK_LIST)
					.anyMatch(eventListDto.getCdTask()::equals)) {
				String familyPlanVersion = familyPlanDao.getFamilyPlanVersion(eventListDto.getIdEvent());
				eventListDto.setFamilyPlanVersion(familyPlanVersion);

				if (!ObjectUtils.isEmpty(familyPlanVersion) && ServiceConstants.THREE.equals(familyPlanVersion)) {
					StringBuilder desc = new StringBuilder();
					desc.append(ServiceConstants.FAMILY_PLAN);
					PersonValueDto personValueDto = familyPlanDao.getFirstParticipant(eventListDto.getIdEvent(), true);
					if (!ObjectUtils.isEmpty(personValueDto)) {
						desc.append(ServiceConstants.HYPHEN);
						String name = FTFamilyTreeUtil.formatPersonName(personValueDto.getFullName(),
								personValueDto.getNmSuffix(), personValueDto.getPersonId().intValue());
						desc.append(name);
					}
					eventListDto.setEventDescr(desc.toString());
				} else {
					eventListDto.setLegacyRecord(true);
				}
			} else if (!ObjectUtils.isEmpty(eventListDto.getCdTask()) && Arrays
					.stream(ServiceConstants.FAMILY_PLAN_EVAL_TASK_LIST).anyMatch(eventListDto.getCdTask()::equals)) {
				String familyPlanVersion = familyPlanDao.getFamilyPlanEvalVersion(eventListDto.getIdEvent());
				eventListDto.setFamilyPlanVersion(familyPlanVersion);

				if (!ObjectUtils.isEmpty(familyPlanVersion) && ServiceConstants.THREE.equals(familyPlanVersion)) {
					StringBuilder desc = new StringBuilder();
					desc.append(ServiceConstants.FAMILY_PLAN_EVAL);
					PersonValueDto personValueDto = familyPlanDao.getFirstParticipant(eventListDto.getIdEvent(), false);
					if (!ObjectUtils.isEmpty(personValueDto)) {
						desc.append(ServiceConstants.HYPHEN);
						String name = FTFamilyTreeUtil.formatPersonName(personValueDto.getFullName(),
								personValueDto.getNmSuffix(), personValueDto.getPersonId().intValue());
						desc.append(name);
					}
					eventListDto.setEventDescr(desc.toString());
				} else {
					eventListDto.setLegacyRecord(true);
				}
			}
			//for PPM 65209
			if ("9999".equals(eventListDto.getCdTask())) {
				eventListDto.setIdPlacementTa(getPlacementTa(eventListDto.getIdEvent()));
			}
		});

		if (ServiceConstants.FRE_LEGAL_TASK.equals(eventReq.getSzCdTask())
				|| ServiceConstants.INV_LEGAL_TASK.equals(eventReq.getSzCdTask())
				|| ServiceConstants.FSU_LEGAL_TASK.equals(eventReq.getSzCdTask())
				|| ServiceConstants.FPR_LEGAL_TASK.equals(eventReq.getSzCdTask())
				|| ServiceConstants.SUB_LEGAL_TASK.equals(eventReq.getSzCdTask())) {
			// For Legal Status List, to populate "Person" field #7725 defect
			eventDetailRes.getEventSearchDto().forEach(eventListDto -> {
				String personFullName = legalStatusDao.getPersonFullName(eventListDto.getIdEvent());
				eventListDto.setScrPersonNameEvent(personFullName);
			});
		}

		// Setting the isTPRView indicator to be used in the JSP for enabling
		// the type hyper link when SEC_SVC_LVL_REV security attribute is set
		eventDetailRes.getEventSearchDto().forEach(eventDto -> {
			if (eventDto.getCdEventType().equalsIgnoreCase(CodesConstant.CEVNTTYP_PLA)
					|| eventDto.getCdEventType().equalsIgnoreCase(CodesConstant.CEVNTTYP_LOC)) {
				eventDto.setIsTPRView(false);
			} else {
				eventDto.setIsTPRView(true);
			}
		});

		// For intrim stage disable hyperlink defect# 10619
		eventDetailRes.getEventSearchDto().forEach(eventListDto -> {
			if (!ObjectUtils.isEmpty(eventListDto.getCdStage())) {
				eventListDto.setShowHyperLink(disabledStage(eventListDto.getCdStage(), eventListDto.getCdStageProgram()));
			}
		});

		// for showing sensitive case information message defect# 10619
		eventDetailRes.getEventSearchDto().forEach(eventListDto -> {
			if (!ObjectUtils.isEmpty(eventListDto.getIdCase())) {
				CapsCaseDto capsCaseDto = capsCaseDao.getCaseDetails(eventListDto.getIdCase());
				if (!ObjectUtils.isEmpty(capsCaseDto.getIndCaseSensitive())
						&& capsCaseDto.getIndCaseSensitive().equalsIgnoreCase(ServiceConstants.STRING_IND_Y)) {
					eventDetailRes.setbIndFilteredSensitiveEvents(ServiceConstants.STRING_IND_Y);
				}
			}
		});


		if(!ObjectUtils.isEmpty(eventReq.getUlIdPerson())){
			Boolean sensitiveCase = eventDao.checkIfPersonBelongToSensitiveCase(eventReq.getUlIdPerson());
			if(sensitiveCase){
				eventDetailRes.setbIndFilteredSensitiveEvents(ServiceConstants.STRING_IND_Y);
			}
		}
		log.info("TransactionId :" + eventReq.getTransactionId());
		return eventDetailRes;
	}

	//for PPM 65209
	/**
	 * for Temporary Absence events get idPlaementTA based on idEvent
	 * @param idEvent
	 * @return
	 */
	private Long getPlacementTa(Long idEvent) {
		return temporaryAbsenceDao.getPlacementTaByEventId(idEvent);
	}

	/**
	 *
	 *Method Name:	disabledStage
	 *Method Description:This method is used to disable the hyperLink for event based on stage and stage program
	 *@param cdStage
	 *@param cdStageProgram
	 *@return
	 */
	private boolean disabledStage(String cdStage, String cdStageProgram) {
		if (!ObjectUtils.isEmpty(cdStage) && !ObjectUtils.isEmpty(cdStageProgram)
				&& ((cdStageProgram.equals(CodesConstant.CPGRMS_CPS) && (cdStage.equals(CodesConstant.CSTAGES_INT)
						|| cdStage.equals(CodesConstant.CSTAGES_INV) || cdStage.equals(CodesConstant.CSTAGES_FAD)
						|| cdStage.equals(CodesConstant.CSTAGES_KIN)
				|| cdStage.equals(CodesConstant.CSTAGES_AR) || cdStage.equals(CodesConstant.CSTAGES_SUB)
				|| cdStage.equals(CodesConstant.CSTAGES_PAL) || cdStage.equals(CodesConstant.CSTAGES_FSU)
				|| cdStage.equals(CodesConstant.CSTAGES_FRE) || cdStage.equals(CodesConstant.CSTAGES_FPR)))
						|| cdStage.equals(CodesConstant.CSTAGES_PAL) || cdStage.equals(CodesConstant.CSTAGES_FSU)
				|| (cdStageProgram.equals(CodesConstant.CPGRMS_APS) //INV, SVC , ARI STAGES as per Stephen
				&& (cdStage.equals(CodesConstant.CSTAGES_INV)
				|| cdStage.equals(CodesConstant.CSTAGES_SVC)
				|| cdStage.equals(CodesConstant.CSTAGES_ARI)
//				|| cdStage.equals(CodesConstant.CSTAGES_ARF)
		        || cdStage.equals(CodesConstant.CSTAGES_AOC)))
				|| (cdStageProgram
				.equals(CodesConstant.CPGRMS_AFC)
				&& (cdStage.equals(CodesConstant.CSTAGES_INT)
				|| cdStage.equals(CodesConstant.CSTAGES_INV)))
				|| (cdStageProgram
				.equals(CodesConstant.CPGRMS_CCL)
				&& (cdStage.equals(CodesConstant.CSTAGES_INT)
				|| cdStage.equals(CodesConstant.CSTAGES_INV)))
				|| (cdStageProgram
				.equals(CodesConstant.CPGRMS_RCL)
				&& (cdStage.equals(CodesConstant.CSTAGES_INT)
				|| cdStage.equals(CodesConstant.CSTAGES_INV)))
				|| (cdStageProgram.equals(CodesConstant.CPHYSCND_PRS)
				&& (cdStage.equals(CodesConstant.CSTAGES_INT)
				|| cdStage.equals(CodesConstant.CSTAGES_IR))
				|| (cdStage.equals(CodesConstant.CSTAGES_ARI))
				|| (cdStage.equals(CodesConstant.CSTAGES_ARF)) ))) {
			return false;
		} else
			return true;
	}

	/**
	 *
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * person and a time period. Service Name: CCMN11S
	 *
	 * @param todoReq
	 * @return List<TodoDto> @
	 */
	@Transactional
	public List<TodoDto> getTodoDetails(TodoReq todoReq) {
		List<TodoDto> todoServiceOutput = new ArrayList<TodoDto>();
		todoServiceOutput = todoDao.getTodoDetails(todoReq);
		log.info("TransactionId :" + todoReq.getTransactionId());
		return todoServiceOutput;
	}

	/**
	 *
	 * Method Description:call service to retrieve county code list based on
	 * input city name
	 *
	 * @param searchRetrieveCountyReq
	 * @return searchRetrieveCountyRes @
	 */
	@Override
	@Transactional
	public RetrvCountyRes getCountyList(RetrvCountyReq searchRetrieveCountyReq) {
		RetrvCountyRes searchRetrieveCountyRes = new RetrvCountyRes();
		List<CityCountyDto> retrieveCountyOutputs = new ArrayList<CityCountyDto>();
		String szAddrCity = searchRetrieveCountyReq.getSzAddrCity();
		List<CityDto> outputCounty = new ArrayList<CityDto>();
		outputCounty = retrieveCountyDao.getCountyList(szAddrCity);
		for (CityDto countyCode : outputCounty) {
			retrieveCountyOutputs.add(this.getRetrieveCountyOutput(countyCode.getCdCounty(), searchRetrieveCountyReq));
		}
		searchRetrieveCountyRes.setRetrieveOutput(retrieveCountyOutputs);
		searchRetrieveCountyRes.setTransactionId(searchRetrieveCountyReq.getTransactionId());
		log.info("TransactionId :" + searchRetrieveCountyReq.getTransactionId());
		return searchRetrieveCountyRes;
	}

	/**
	 *
	 * Method Description:combine search result with inputs to form the output
	 *
	 * @param szSysCode1CntyCode
	 * @param searchRetrieveCountyReq
	 * @return retrieveCountyDto @
	 */
	private CityCountyDto getRetrieveCountyOutput(String szSysCode1CntyCode, RetrvCountyReq searchRetrieveCountyReq) {
		CityCountyDto retrieveCountyDto = new CityCountyDto();
		if (szSysCode1CntyCode != null) {
			retrieveCountyDto.setAddrZip(searchRetrieveCountyReq.getlAddrZip());
			retrieveCountyDto.setAddrCity(searchRetrieveCountyReq.getSzAddrCity());
			retrieveCountyDto.setCdSysCode1Cnty(szSysCode1CntyCode);
			retrieveCountyDto.setAddrPersAddrStLn1(searchRetrieveCountyReq.getSzAddrPersAddrStLn1());
			retrieveCountyDto.setAddrPersAddrStLn2(searchRetrieveCountyReq.getSzAddrPersAddrStLn2());
		}
		log.info("TransactionId :" + searchRetrieveCountyReq.getTransactionId());
		return retrieveCountyDto;
	}

	/**
	 *
	 * Method Description: This Method will retrieve all ID_STAGES given a case
	 * or a person. Service Name: CCMN32S
	 *
	 * @param searchStageReq
	 * @return SearchStageRes
	 * @, InvalidRequestException
	 */
	@Override
	@Transactional
	public SearchStageRes getSearchStageList(SearchStageReq searchStageReq) {
		SearchStageRes searchStageRes = new SearchStageRes();
		List<StageIdDto> outputs = new ArrayList<StageIdDto>();
		if (searchStageReq.getCapsCase() != null) {
			List<StageIdDto> stageListByCase = this.searchStageByCaseId(searchStageReq);
			if (stageListByCase != null && !stageListByCase.isEmpty()) {
				outputs.addAll(stageListByCase);
			} else {
				throw new InvalidRequestException(
						messageSource.getMessage("stage.not.found.attributes", null, Locale.US));
			}
		}
		if (searchStageReq.getIdPerson() != null) {
			List<StageIdDto> stageListByPerson = this.searchStageByPersonId(searchStageReq);
			if (stageListByPerson != null && !stageListByPerson.isEmpty()) {
				outputs.addAll(stageListByPerson);
			} else {
				throw new InvalidRequestException(
						messageSource.getMessage("stagepersonlink.not.found.attributes", null, Locale.US));
			}
		}
		searchStageRes.setStageList(outputs);
		searchStageRes.setTransactionId(searchStageReq.getTransactionId());
		log.info("TransactionId :" + searchStageReq.getTransactionId());
		return searchStageRes;
	}

	/**
	 *
	 * Method Description:call dao for dam-CCMNA2D Service Name: CCMN32S
	 *
	 * @param req
	 * @return List<StageIdDto>
	 */
	public List<StageIdDto> searchStageByPersonId(SearchStageReq req) {
		Long personId = req.getIdPerson();
		List<StageIdDto> stageOutputByPersonId = null;
		if (personId == null) {
			throw new InvalidRequestException(
					messageSource.getMessage("stagepersonlink.idPerson.mandatory", null, Locale.US));
		} else {
			stageOutputByPersonId = stagePersonLinkDao.getStageIdList(personId);
		}
		log.info("TransactionId :" + req.getTransactionId());
		return stageOutputByPersonId;
	}

	/**
	 *
	 * Method Description: Call dao for dam-CCMN86D Service Name: CCMN32S
	 *
	 * @param req
	 * @return List<StageIdDto>
	 */
	public List<StageIdDto> searchStageByCaseId(SearchStageReq req) {
		Long caseId = req.getCapsCase();
		List<StageIdDto> stageOutputByCaseId = null;
		if (caseId == null) {
			throw new InvalidRequestException(messageSource.getMessage("stage.capsCase.mandatory", null, Locale.US));
		} else {
			stageOutputByCaseId = stageDao.searchStageByCaseId(caseId);
		}
		log.info("TransactionId :" + req.getTransactionId());
		return stageOutputByCaseId;
	}

	/**
	 *
	 *
	 * Method Description:perform different operations upon request indicated by
	 * SzCdScrDataAction indicator
	 *
	 * @param caseMergeUpdateReq
	 * @return CaseMergeUpdateRes
	 */
	@Override
	@Transactional
	public CaseMergeUpdateRes CaseMergeUpdate(CaseMergeUpdateReq caseMergeUpdateReq){
		CaseMergeUpdateRes caseMergeUpdateRes = new CaseMergeUpdateRes();
		OutputMessageDto outputMessageDto = new OutputMessageDto();
		List<CaseMergeUpdateDto> updateCaseList = caseMergeUpdateReq.getUpdateinput();
		String MERGE = ServiceConstants.MERGE;
		String VOID_MERGE = ServiceConstants.VOID_MERGE;
		String SPLIT = ServiceConstants.SPLIT;
		// String VOID_SPLIT=ServiceConstants.VOID_SPLIT;
		for (CaseMergeUpdateDto caseMergeUpdateDto : updateCaseList) {
			if (caseMergeUpdateDto.getCdScrDataAction().equals(MERGE)
					|| caseMergeUpdateDto.getCdScrDataAction().equals(VOID_MERGE)) {
				caseMergeUpdateDto.setIndCaseMergePending(MERGE);
			} else {
				caseMergeUpdateDto.setIndCaseMergePending(SPLIT);
			}
			if (caseMergeUpdateDto.getCdScrDataAction().equals(MERGE)
					|| caseMergeUpdateDto.getCdScrDataAction().equals(SPLIT)) {
				Long caseId = this.saveCaseMerge(caseMergeUpdateDto).getIdCaseMerge();
				Long caseMergeToId = caseMergeUpdateDto.getIdCaseMergeTo();
				if (caseId == null) {
					caseMergeUpdateRes.setMsg(outputMessageDto.getErrorMsg());
					break;
				} else {
					List<CaseMergeUpdateDto> caseList = caseMergeUpdateDao.searchAllMergedCases(caseMergeToId);
					if (caseList == null || caseList.isEmpty()) {
						caseMergeUpdateRes.setMsg(outputMessageDto.getLoopMsg());
						break;
					}
				}
				// verify connect by loop and break if loop
			} else {
				Long caseId = this.deleteCaseMerge(caseMergeUpdateDto).getIdCaseMerge();
				if (caseId == null) {
					caseMergeUpdateRes.setMsg(outputMessageDto.getErrorMsg());
					break;
				}
			}
		}
		if (caseMergeUpdateRes.getMsg() == null) {
			caseMergeUpdateRes.setMsg(outputMessageDto.getSuccessMsg());
		}
		caseMergeUpdateRes.setTransactionId(caseMergeUpdateReq.getTransactionId());
		log.info("TransactionId :" + caseMergeUpdateReq.getTransactionId());
		return caseMergeUpdateRes;
	}

	/**
	 *
	 * Method Description:call dao to insert case as requested
	 *
	 * @param caseDto
	 * @return CaseMergeUpdateDto
	 */
	public CaseMergeUpdateDto saveCaseMerge(CaseMergeUpdateDto caseDto){
		CaseMerge saveCase = new CaseMerge();
		CaseMergeUpdateDto caseSaved = new CaseMergeUpdateDto();
		String format = ServiceConstants.SIMPLE_DATE_FORMAT;
		if (caseDto.getIdCaseMerge() != null) {
			saveCase.setIdCaseMerge(caseDto.getIdCaseMerge());
		}
		if (caseDto.getIdCaseMergeTo() != null) {
			CapsCase caseMergeTo = new CapsCase();
			caseMergeTo.setIdCase(caseDto.getIdCaseMergeTo());
			saveCase.setCapsCaseByIdCaseMergeTo(caseMergeTo);
		}
		if (caseDto.getIdCaseMergeFrom() != null) {
			CapsCase caseMergeFrom = new CapsCase();
			caseMergeFrom.setIdCase(caseDto.getIdCaseMergeFrom());
			saveCase.setCapsCaseByIdCaseMergeFrom(caseMergeFrom);
		}
		if (caseDto.getIdCaseMergePersMrg() != null) {
			Person personMerge = new Person();
			personMerge.setIdPerson(caseDto.getIdCaseMergePersMrg());
		}
		if (caseDto.getIdCaseMergePersSplit() != null) {
			Person personSplit = new Person();
			personSplit.setIdPerson(caseDto.getIdCaseMergePersSplit());
		}
		if (caseDto.getIndCaseMergeInv() != null) {
			saveCase.setIndCaseMergeInvalid(caseDto.getIndCaseMergeInv().charAt(0));
		}
		if (caseDto.getIndCaseMergePending() != null) {
			saveCase.setIndCaseMergePending(caseDto.getIndCaseMergePending().charAt(0));
		}
		try {
			if (caseDto.getTsLastUpdate() != null) {
				DateFormat df = new SimpleDateFormat(format);
				Date lastUpdate;
				lastUpdate = df.parse(caseDto.getTsLastUpdate());
				saveCase.setDtLastUpdate(lastUpdate);
			}
			if (caseDto.getDtCaseMerge() != null) {
				DateFormat df = new SimpleDateFormat(format);
				Date dtCaseMerge;
				dtCaseMerge = df.parse(caseDto.getDateCaseMerge());
				saveCase.setDtCaseMerge(dtCaseMerge);
			}
			if (caseDto.getDtCaseMergeSplit() != null) {
				DateFormat df = new SimpleDateFormat(format);
				Date dtCaseMergeSplit;
				dtCaseMergeSplit = df.parse(caseDto.getDtCaseMergeSplit());
				saveCase.setDtCaseMerge(dtCaseMergeSplit);
			}
		} catch (ParseException e) {
			throw new ServiceLayerException(e.getMessage());
		}
		caseSaved = caseMergeUpdateDao.saveCaseMerge(saveCase);
		return caseSaved;
	}

	/**
	 *
	 *
	 * Method Description:call Dao to delete case
	 *
	 * @param caseDto
	 * @return CaseMergeUpdateDto
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public CaseMergeUpdateDto deleteCaseMerge(CaseMergeUpdateDto caseDto){
		String format = ServiceConstants.SIMPLE_DATE_FORMAT;
		CaseMerge deleteCase = new CaseMerge();
		CaseMergeUpdateDto caseDeleted = new CaseMergeUpdateDto();
		try {
			if (caseDto.getTsLastUpdate() != null) {
				DateFormat df = new SimpleDateFormat(format);
				Date lastUpdate;
				lastUpdate = df.parse(caseDto.getTsLastUpdate());
				deleteCase.setDtLastUpdate(lastUpdate);
			}
		} catch (ParseException e) {
			throw new ServiceLayerException(e.getMessage());
		}
		if (caseDto.getIdCaseMerge() != null) {
			deleteCase.setIdCaseMerge(caseDto.getIdCaseMerge());
		}
		if (caseDto.getIdCaseMergeTo() != null) {
			CapsCase caseMergeTo = new CapsCase();
			caseMergeTo.setIdCase(caseDto.getIdCaseMergeTo());
			deleteCase.setCapsCaseByIdCaseMergeTo(caseMergeTo);
		}
		if (caseDto.getIdCaseMergeFrom() != null) {
			CapsCase caseMergeFrom = new CapsCase();
			caseMergeFrom.setIdCase(caseDto.getIdCaseMergeFrom());
			deleteCase.setCapsCaseByIdCaseMergeFrom(caseMergeFrom);
		}
		caseDeleted = caseMergeUpdateDao.deleteCaseMerge(deleteCase);
		return caseDeleted;
	}

	/**
	 *
	 * Method Description:This Method Retrieves data from Approval Related
	 * tables to support the population of the Approval Status window and WCD.
	 * The service will be given either an ID_TODO, ID_EVENT or ID_APPROVAL.
	 * Service Name: CCMN34S
	 *
	 * @param approvalStatusReq
	 * @return ApprovalStatusRes
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public ApprovalStatusRes fetchApprovalStatusDtls(ApprovalStatusReq approvalStatusReq){
		ApprovalStatusRes approvalStatusRes = new ApprovalStatusRes();
		Long scenario;
		boolean bSprog = false;
		if (!TypeConvUtil.isNullOrEmpty(approvalStatusReq.getLdIdTodo())) {
			scenario = ServiceConstants.APRV_MODIFY;
			approvalStatusRes.setUlIdApproval(approversDao.approversSearchApproval(approvalStatusReq.getLdIdTodo()));
			List<WebsvcFormTransSearchDto> websvcFormTransSearchDtoList = websvcFormTransDao
					.websvcFormTransSearch(approvalStatusReq.getUlIdEvent());
			approvalStatusRes.setWebsvcFormTransList(websvcFormTransSearchDtoList);
		} else {
			scenario = ServiceConstants.APRV_BROWSE;
			ApprovalEventLinkDto approvalEventLinkDto = approvalEventLinkDao
					.getApprovalEventLinkID(approvalStatusReq.getUlIdEvent());
			if (null != approvalEventLinkDto) {
				approvalStatusRes.setUlIdApproval((!TypeConvUtil.isNullOrEmpty(approvalEventLinkDto.getIdApproval())
						? approvalEventLinkDto.getIdApproval() : approvalStatusReq.getUlIdEvent()));
			} else {
				approvalStatusRes.setUlIdApproval(approvalStatusReq.getUlIdEvent());
			}
		}
		if (approvalStatusRes.getUlIdApproval() == null) {
			throw new ServiceLayerException(ServiceConstants.REQUEST_INVALID);
		}
		List<ApproversDto> approversDtoList = approversDao
				.getApproversPersonDetails(approvalStatusRes.getUlIdApproval());
		approvalStatusRes.setApproversDtoList(approversDtoList);
		for (ApproversDto approversDto : approversDtoList) {
			if (approversDto.getCdApproversStatus().equals(ServiceConstants.APRV_REJECT)
					|| approversDto.getCdApproversStatus().equals(ServiceConstants.APRV_INVALID)) {
				scenario = ServiceConstants.APRV_BROWSE;
				break;
			}
		}
		List<ApprovalEventLinkEventDto> approvalEventLinkEventDtoList = new ArrayList<ApprovalEventLinkEventDto>();
		EventStageSearchDto eventStageSearchDto = new EventStageSearchDto();
		if (scenario.equals(ServiceConstants.APRV_MODIFY)) {
			approvalEventLinkEventDtoList = approvalEventLinkDao
					.approvalEventLinkSearchByApprovalId(approvalStatusRes.getUlIdApproval());
			approvalStatusRes.setUlRowQty2(approvalEventLinkEventDtoList.size());
			if (approvalEventLinkEventDtoList.size() == ServiceConstants.ROWNUM1) {
				TaskDto taskDto = taskDao.getTaskDetails(approvalEventLinkEventDtoList.get(0).getCdTask());
				approvalStatusRes.setSzCdTaskTopWindow(taskDto.getCdTaskListWindow());
			}
			eventStageSearchDto = stageDao.stageEventSearchById(approvalStatusRes.getUlIdApproval());
			if (null == eventStageSearchDto && approvalEventLinkEventDtoList != null
					&& !approvalEventLinkEventDtoList.isEmpty()) {
				eventStageSearchDto = stageDao.stageEventSearchById(approvalEventLinkEventDtoList.get(0).getIdEvent());
			}
			if (eventStageSearchDto == null) {
				throw new ServiceLayerException(ServiceConstants.REQUEST_INVALID);
			}
			approvalStatusRes.setSzNmStage(eventStageSearchDto.getNmStage());
			approvalStatusRes.setUlIdCase(eventStageSearchDto.getIdCase());
			approvalStatusRes.setUlIdStage(eventStageSearchDto.getIdStage());
			approvalStatusRes.setSzCdStage(eventStageSearchDto.getCdStage());
			approvalStatusRes.setUlIdPerson(
					setInputValuesWrkLdStgPersLink(eventStageSearchDto, eventStageSearchDto.getCdStage()));
			if (approvalStatusRes.getUlIdPerson() != null && approvalStatusRes.getUlIdPerson() > 0) {
				EmployeePersPhNameDto employeePersPhNameDto = employeeDao
						.searchPersonPhoneName(approvalStatusRes.getUlIdPerson());
				if (TypeConvUtil.isNullOrEmpty(employeePersPhNameDto.getNmNameMiddle())) {
					String nmPersonFull = employeePersPhNameDto.getNmNameLast().concat(",")
							.concat(employeePersPhNameDto.getNmNameFirst());
					approvalStatusRes.setSzNmPersonFull(nmPersonFull);
				} else {
					String nmPersonFull = employeePersPhNameDto.getNmNameLast().concat(",")
							.concat(employeePersPhNameDto.getNmNameFirst()).concat(" ")
							.concat(employeePersPhNameDto.getNmNameMiddle());
					approvalStatusRes.setSzNmPersonFull(nmPersonFull);
				}
			}
			EventDto eventDto = eventDao.getEventByid(approvalStatusRes.getUlIdApproval());
			if (null == eventDto) {
				eventDto = eventDao.getEventByid(approvalEventLinkEventDtoList.get(0).getIdEvent());
			}
			approvalStatusRes.setEventDto(eventDto);
		}
		ApprovalPersonSearchDto approvalPersonSearchDto = approvalDao
				.approvalPersonSearchbyId(approvalStatusRes.getUlIdApproval());
		if (!ObjectUtils.isEmpty(approvalPersonSearchDto)) {
			approvalStatusRes.setUlIdPerson(approvalPersonSearchDto.getIdPerson());
			approvalStatusRes.setSzNmPersonFull(approvalPersonSearchDto.getNmPersonFull());
			approvalStatusRes.setSzTxtApprovalTopic(approvalPersonSearchDto.getApprovalTopic());
		}

		List<ApprovalRejectionPersonDto> approvalRejectionPersonDtoList = approvalRejectionDao
				.approvalRejectionPersonSearch(
						(!TypeConvUtil.isNullOrEmpty(approvalStatusReq.getUlIdCase()) ? approvalStatusReq.getUlIdCase()
								: eventStageSearchDto.getIdCase()),
						(!TypeConvUtil.isNullOrEmpty(approvalStatusReq.getUlIdStage())
								? approvalStatusReq.getUlIdStage() : eventStageSearchDto.getIdStage()));
		approvalStatusRes.setApprovalRejectionPersonDtoList(approvalRejectionPersonDtoList);
		AprvlStageProgDto aprvlStageProgDto = new AprvlStageProgDto();
		aprvlStageProgDto.setWCDCdStageProgressMode(ServiceConstants.SP_NOT_APPL);
		if (approvalStatusRes.getUlRowQty2() != null) {
			for (int i = 0; i < approvalStatusRes.getUlRowQty2(); i++) {
				if ((ServiceConstants.CLOSE_TASKS).contains(approvalEventLinkEventDtoList.get(i).getCdTask())) {
					bSprog = true;
				}
			}
		}
		List<StageProgDto> stageProgDto = new ArrayList<StageProgDto>();
		if (bSprog) {
			stageProgDto = stageProgDao.getStgProgroession(eventStageSearchDto.getCdStage(),
					eventStageSearchDto.getCdStageProgram(), eventStageSearchDto.getCdStageReasonClosed());

			// artf151569: Updates the Stage Prog List for CPS (A-R & INV) when the Approval created the FPR Release date
			if (!ObjectUtils.isEmpty(approvalStatusRes.getUlIdApproval()) && ServiceConstants.CD_STAGE_CPS
					.equals(eventStageSearchDto.getCdStageProgram()) && Arrays.asList(
					ServiceConstants.CSTAGES_AR, ServiceConstants.CSTAGES_INV).contains(eventStageSearchDto.getCdStage())
					&& ServiceConstants.FPR_STAGE_PROG_TABLE.containsColumn(eventStageSearchDto.getCdStageReasonClosed())) {

				hasAppEventCreatedBeforeFBSSReferral(approvalStatusRes.getUlIdApproval(), stageProgDto);
			}
		}
		approvalStatusRes.setApprovalEventLinkEventDtoList(approvalEventLinkEventDtoList);
		aprvlStageProgDto.setCdStageProgram(eventStageSearchDto.getCdStageProgram());
		aprvlStageProgDto.setCdStageReasonClosed(eventStageSearchDto.getCdStageReasonClosed());
		for (int i = 0; i < stageProgDto.size(); i++) {
			if (!TypeConvUtil.isNullOrEmpty(stageProgDto.get(0).getIdStageProg())) {
				aprvlStageProgDto.setCdStageOpen(stageProgDto.get(0).getCdStageProgOpen());
				if (ServiceConstants.IND_STGPROG_CLOSE.equalsIgnoreCase(stageProgDto.get(0).getIndStageProgClose())) {
					aprvlStageProgDto.setWCDCdStageProgressMode(ServiceConstants.SP_AUTOMATIC);
				} else {
					aprvlStageProgDto.setWCDCdStageProgressMode(ServiceConstants.SP_CASECLOSE);
				}
			} else {
				aprvlStageProgDto.setWCDCdStageProgressMode(ServiceConstants.SP_MANUAL);
			}
		}
		approvalStatusRes.setAprvlStageProgDto(aprvlStageProgDto);
		approvalStatusRes.setTransactionId(approvalStatusReq.getTransactionId());

		// This condition is added to get the version of family plan if the task is family plan approval
		if (!ObjectUtils.isEmpty(approvalStatusRes.getApprovalEventLinkEventDtoList())
				&& !approvalStatusRes.getApprovalEventLinkEventDtoList().isEmpty()
				&& !ObjectUtils.isEmpty(approvalStatusRes.getApprovalEventLinkEventDtoList().get(0).getCdTask())) {
			if (Arrays.stream(ServiceConstants.FAMILY_PLAN_TASK_LIST)
					.anyMatch(approvalStatusRes.getApprovalEventLinkEventDtoList().get(0).getCdTask()::equals)) {
				approvalStatusRes.setFamilyPlanVersion(familyPlanDao.getFamilyPlanVersion(
						approvalStatusRes.getApprovalEventLinkEventDtoList().get(0).getIdEvent()));
			} else if (Arrays.stream(ServiceConstants.FAMILY_PLAN_EVAL_TASK_LIST)
					.anyMatch(approvalStatusRes.getApprovalEventLinkEventDtoList().get(0).getCdTask()::equals)) {
				approvalStatusRes.setFamilyPlanVersion(familyPlanDao.getFamilyPlanEvalVersion(
						approvalStatusRes.getApprovalEventLinkEventDtoList().get(0).getIdEvent()));
			}
		}
		log.info("TransactionId :" + approvalStatusReq.getTransactionId());
		return approvalStatusRes;
	}

	private Long setInputValuesWrkLdStgPersLink(EventStageSearchDto eventStageSearchDto, String cdStage) {
		Long idPerson = 0l;
		String cdStgPersRole = "";
		if (ServiceConstants.CLOSE_STAGE_YES.equalsIgnoreCase(eventStageSearchDto.getIndStageClose())) {
			if ((ServiceConstants.HIST_PRIMARY_TASKSTATUS).contains(eventStageSearchDto.getCdTask())) {
				if (!TypeConvUtil.isNullOrEmpty(eventStageSearchDto.getDtStageClose())) {
					cdStgPersRole = ServiceConstants.HIST_PRIM_WORKER;
				} else {
					cdStgPersRole = ServiceConstants.PRIMARY_WORKER;
				}
			}
		} else {
			cdStgPersRole = ServiceConstants.PRIMARY_WORKER;
		}
		if (ServiceConstants.CLOSE_STAGE_YES.equalsIgnoreCase(eventStageSearchDto.getIndStageClose())
				&& cdStage.equalsIgnoreCase(ServiceConstants.INT_Stage)
				&& !TypeConvUtil.isNullOrEmpty(eventStageSearchDto.getDtStageClose())) {
			cdStgPersRole = ServiceConstants.HIST_PRIM_WORKER;
		}
		if (cdStgPersRole.equals(ServiceConstants.PRIMARY_WORKER)
				|| cdStgPersRole.equals(ServiceConstants.SECONDARY_WORKER)) {
			idPerson = workLoadDao.getPersonIdByRole(eventStageSearchDto.getIdStage(), cdStgPersRole);
		} else {
			idPerson = stagePersonLinkDao.getPersonIdByRole(eventStageSearchDto.getIdStage(), cdStgPersRole);
		}
		return idPerson;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: hasAppEventCreatedBeforeFBSSReferral
	 * Method Description: This method is used to check whether the current stage approval has been created before the
	 * FPR Release, and there is no approval FBSS Referral event. If found, then uses the static table to update the
	 * StageProgression list.
	 *
	 * @param idApproval
	 * @param stageProgDtos
	 * @return
	 */
	@Override
	public void hasAppEventCreatedBeforeFBSSReferral(Long idApproval, List<StageProgDto> stageProgDtos) {

		Predicate<StageProgDto> predicate = dto -> ServiceConstants.FPR_STAGE_PROG_TABLE
				.containsRow(dto.getCdStageProgStage()) && ServiceConstants.FPR_STAGE_PROG_TABLE
				.containsColumn(dto.getCdStageProgRsnClose()) && ServiceConstants.CD_STAGE_CPS
				.equals(dto.getCdStageProgProgram());

		if (!CollectionUtils.isEmpty(stageProgDtos)) {
			if (workLoadDao.hasAppEventExistsBeforeFBSSRef(idApproval)) {
				// Approval has been created before the FPR Release date, uses the Static table to update the Stage Progression list
				stageProgDtos.stream().filter(predicate).findFirst()
						.ifPresent(dto -> {
							dto.setCdStageProgOpen(ServiceConstants.FPR_STAGE_PROG_TABLE.get(dto.getCdStageProgStage(),
									dto.getCdStageProgRsnClose()));
							dto.setIndStageProgClose(ServiceConstants.IND_STGPROG_CLOSE);
						});
			} else {
				// Approval has been created after the FPR Release date, setting the IndStageProgClose as 0 for Stage close only
				stageProgDtos.stream().filter(predicate).findFirst()
						.ifPresent(dto -> {
							dto.setIndStageProgClose(ServiceConstants.STAGE_PROG_0);
						});
			}
		}
	}

	/**
	 * Artifact ID: artf140443
	 *Method Name:	isCaseAssignedToPerson
	 *Method Description:checks if a case is assigned to the case worker
	 *@param idPerson
	 *@param idCase
	 *@return
	 */
	@Override
	public boolean isCaseAssignedToPerson(Long idPerson, Long idCase) {
		return workLoadDao.isCaseAssignedToPerson(idPerson, idCase);
	}

	/**
	 *
	 *
	 * Method Description:This service will retrieve information needed in order
	 * to assign an employee to a stage Tuxedo Service Name:CCMN80S
	 *
	 * @param searchAssignReq
	 * @return searchAssignRes @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public SearchAssignRes getAssignEmpStage(SearchAssignReq searchAssignReq) {
		SearchAssignRes searchAssignRes = new SearchAssignRes();
		List<AvailStaffGroupDto> availStaffGroupDtoList = new ArrayList<>();
		List<AvailStaffGroupDto> availStaffGroupList = new ArrayList<>();
		/* determine view mode */
		if (searchAssignReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.FULL_UNITS_VIEW)) {
			List<AvailStaffGroupDto> availStaffGroupDto = assignDao.getAvailStaffInfo(searchAssignReq.getIdUnit());
			searchAssignRes.setAvailStaffGroupDto(availStaffGroupDto);
		} else /* determine onCall mode */
			if (searchAssignReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.ON_CALL_VIEW)) {
				/* Current Date */
				Calendar myDate = Calendar.getInstance();
				OnCallDto onCallTest = assignDao.getOnCallId(searchAssignReq.getCdOnCallProgram(),
						searchAssignReq.getCdOnCallCounty(), myDate.getTime());
				if (!TypeConvUtil.isNullOrEmpty(onCallTest)) {
					availStaffGroupDtoList = assignDao.getOnCallEmp(onCallTest.getIdOnCall());
					if (!TypeConvUtil.isNullOrEmpty(availStaffGroupDtoList)) {
						availStaffGroupList.addAll(availStaffGroupDtoList);
					}
				}
				// myDate.set(2017, 01, 12, 17, 00, 00);//need to change
				Date reportDate = DateUtils.retrieveOnCallId(myDate).getTime();
				OnCallDto onCall = assignDao.getOnCallId(searchAssignReq.getCdOnCallProgram(),
						searchAssignReq.getCdOnCallCounty(), reportDate);
				if (!TypeConvUtil.isNullOrEmpty(onCall)) {
					List<AvailStaffGroupDto> availStaffGroupDto = assignDao.getOnCallEmp(onCall.getIdOnCall());
					if (!TypeConvUtil.isNullOrEmpty(availStaffGroupDto)) {
						for (AvailStaffGroupDto availStaffGroup : availStaffGroupDto) {
							if (availStaffGroup.getCdEmpOnCallDesig().equalsIgnoreCase(ServiceConstants.ROUTER)
									&& availStaffGroupList.stream().noneMatch(dto ->
									dto.getIdPerson().equals(availStaffGroup.getIdPerson()))) {
								availStaffGroupList.add(availStaffGroup);
								break;
							}
						}
					}
				}
				searchAssignRes.setAvailStaffGroupDto(availStaffGroupList);
			}

		/*
		 ** determine if Assign Processing is required (indicated by 1 or more
		 * IdStage(s) being passed)
		 */
		if (!CollectionUtils.sizeIsEmpty(searchAssignReq.getIdStageList())) {
			/*
			 ** determine Assign Mode ( if only one stage id - regular, more than
			 * 1 stage id - block transfer)
			 */
			searchAssignReq.getIdStageList().stream().forEach(item -> {
				List<AssignmentGroupDto> assignmentGroupDto = new ArrayList<AssignmentGroupDto>();
				try {
					assignmentGroupDto = assignDao.getAssignmentgroup(item);
				} catch (ServiceLayerException e) {
					log.fatal(e.getMessage());
				}
				Boolean bIndMREF = Boolean.FALSE;
				bIndMREF = !TypeConvUtil.isNullOrEmpty(assignmentGroupDto.get(0).getDtMultiRef()) ? true : false;
				if (bIndMREF == true) {
					Boolean indCSSReviewContact = Boolean.TRUE;
					try {
						indCSSReviewContact = assignDao.getIndCSSReviewContact(item);
					} catch (ServiceLayerException e) {
						log.fatal(e.getMessage());
					}
					returnAssignmentGroupDto(assignmentGroupDto, indCSSReviewContact);
				}
				searchAssignRes.getAssignmentGroupDto().addAll(assignmentGroupDto);
			});
			searchAssignRes.setIdUnit(searchAssignRes.getAssignmentGroupDto().get(0).getIdUnit());
			searchAssignRes.setNmCase(searchAssignRes.getAssignmentGroupDto().get(0).getNmCase());
		}
		searchAssignRes.setTransactionId(searchAssignReq.getTransactionId());
		log.info("TransactionId :" + searchAssignReq.getTransactionId());
		return searchAssignRes;
	}

	Stream<AssignmentGroupDto> returnAssignmentGroupDto(List<AssignmentGroupDto> assignmentGroupDtos,
														final boolean indCSSReviewContact) {
		return (Stream<AssignmentGroupDto>) assignmentGroupDtos.stream().map(assignmentGroupDto -> {
			assignmentGroupDto.setIndCSSReviewContact(String.valueOf(indCSSReviewContact));
			return assignmentGroupDto;
		});
	}

	/**
	 *
	 *
	 * Method Description: Tuxedo Service Name:CINT99S Tuxedo Dam Name: CINT40S,
	 * CINV34S, CINT73D
	 *
	 * @param preDisplayPriorityClosureReq
	 * @return preDisplayPriorityClosureRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreDisplayPriorityClosureRes getPreDisplayPriorityClosure(
			PreDisplayPriorityClosureReq preDisplayPriorityClosureReq) {
		PreDisplayPriorityClosureRes preDisplayPriorityClosureRes = new PreDisplayPriorityClosureRes();
		// Call CINT40D
		StageDto pCStageRow = stageDao.getStageById(preDisplayPriorityClosureReq.getIdStage());
		if (ServiceConstants.CCL.equals(preDisplayPriorityClosureReq.getCdStageProgram())) {
			List<PriorityHistoryDto> priorityHistory = workLoadDao
					.getPriorityTracking(preDisplayPriorityClosureReq.getIdStage());
			preDisplayPriorityClosureRes.setPriorityHistory(priorityHistory);
		}
		if (ServiceConstants.CCL.equals(preDisplayPriorityClosureReq.getCdStageProgram())
				|| ServiceConstants.CPGRMS_RCL.equals(preDisplayPriorityClosureReq.getCdStageProgram())) {
			preDisplayPriorityClosureRes.setPriorityClosureLicensingDto(getLicensingInformation(preDisplayPriorityClosureReq,pCStageRow.getIdCase()));
		}
		preDisplayPriorityClosureRes.setpCStageRow(pCStageRow);
		//artf128805 : FCL changes :fetch the notification section only when the program is RCL	and after the FCL REL date
		if (isFCLCheck(preDisplayPriorityClosureReq.getCdStageProgram(),pCStageRow.getDtStageStart(),preDisplayPriorityClosureReq.getFclRelDate())) {
			PriorityClosureDto priorityClosureDto=new PriorityClosureDto();
			//fetch intake date
			priorityClosureDto.setIntakeDate(priorityClosureDao.getIntakeDate(preDisplayPriorityClosureReq.getIdStage()));
			//default load data fetch
			// call to dao to fetch the data for victims
			List<IntakeNotfChildDto> victimNotificationList=priorityClosureDao.getNotifiedVictimNotificationDetails(preDisplayPriorityClosureReq.getIdStage());
			IntakeNotfFacilityDto notifFacilityDto=new IntakeNotfFacilityDto();
			List<IntakeNotfChildDto> facilityChildrenDetailsList=null;
			long intakeStageId=preDisplayPriorityClosureReq.getIdStage();
			ResourceDto facilityDetails=null;
			if(CollectionUtils.isEmpty(victimNotificationList)){
				// Default load
				victimNotificationList=priorityClosureDao.getDefaultVictimNotificationDetails(intakeStageId);
				// Get the OV person id
				Long oldestVictimId=victimNotificationList.stream().filter(dto-> CodesConstant.CRELVICT_OV.equalsIgnoreCase(dto.getCdStagePersRelInt())).findFirst().get().getIdPerson();
				// call to fetch the facility id details and the children
				facilityDetails =priorityClosureDao.getDefaultNotificationFacilityDetails(oldestVictimId);
				if(!ObjectUtils.isEmpty(facilityDetails))
					facilityChildrenDetailsList=priorityClosureDao.getDefaultFacilityChildrenDetails(facilityDetails.getIdResource());

			}else if(victimNotificationList.stream().anyMatch(intakeNotifDto-> StringUtils.isBlank(intakeNotifDto.getCdNotificationStatus())))
			{ //atleast once saved but notify not clicked
				List<IntakeNotfChildDto> defaultVictimNotificationList=priorityClosureDao.getDefaultVictimNotificationDetails(intakeStageId);
				victimNotificationList.forEach(savedVictimDto-> {
					//update the details for the victim section to get the latest details that has changed after the save
					IntakeNotfChildDto defaultVictimNotifDto=defaultVictimNotificationList.stream().filter(defaultVictimdto-> defaultVictimdto.getIdPerson().equals(savedVictimDto.getIdPerson())).findFirst().get();
					savedVictimDto.setNmPersonFull(defaultVictimNotifDto.getNmPersonFull());
					savedVictimDto.setCdLegalStatStatus(defaultVictimNotifDto.getCdLegalStatStatus());
					savedVictimDto.setTxtLegalStatus(defaultVictimNotifDto.getTxtLegalStatus());
					savedVictimDto.setIdWorkerPerson(defaultVictimNotifDto.getIdWorkerPerson());
					savedVictimDto.setNmWorkerPerson(defaultVictimNotifDto.getNmWorkerPerson());
					savedVictimDto.setIdSupervisor(defaultVictimNotifDto.getIdSupervisor());
					savedVictimDto.setNmSupervisor(defaultVictimNotifDto.getNmSupervisor());
					if(savedVictimDto.getIdPersonRevised() !=null && savedVictimDto.getIdSubStage() !=null ) {
						//update the corrected section legal status and victim and worker names
						IntakeNotfChildDto latestVictimDetails=priorityClosureDao.getLatestVictimDetails(savedVictimDto.getIdPersonRevised(),savedVictimDto.getIdSubStage());

						savedVictimDto.setNmPersonFullRevised(latestVictimDetails.getNmPersonFullRevised());
						savedVictimDto.setCdLegalStatStatusRevised(latestVictimDetails.getCdLegalStatStatusRevised());
						savedVictimDto.setTxtLegalStatusRevised(latestVictimDetails.getTxtLegalStatusRevised());
						savedVictimDto.setIdWorkerRevised(latestVictimDetails.getIdWorkerRevised());
						savedVictimDto.setNmWorkerRevised(latestVictimDetails.getNmWorkerRevised());
						savedVictimDto.setIdSupervisorRevised(latestVictimDetails.getIdSupervisorRevised());
						savedVictimDto.setNmSupervisorRevised(latestVictimDetails.getNmSupervisorRevised());
						savedVictimDto.setIdCase(latestVictimDetails.getIdCase());
					}else {
						savedVictimDto.setIdCase(defaultVictimNotifDto.getIdCase());
					}
				});

				// call to fetch the facility id details and the children
				IntakeNotfFacilityDto savedFacilityDetails=priorityClosureDao.getNotifiedFacilityDetails(intakeStageId);
				if(!ObjectUtils.isEmpty(savedFacilityDetails))
				{
					facilityDetails =priorityClosureDao.getSavedFacilityDetails(savedFacilityDetails.getFacilityDetail().getIdResource());
					//merge the latest details with saved detail primary key
					facilityDetails.setIdVictimNotificationRsrc(savedFacilityDetails.getFacilityDetail().getIdVictimNotificationRsrc());
					facilityDetails.setDtLastUpdate(savedFacilityDetails.getFacilityDetail().getDtLastUpdate());

					// List<IntakeNotfChildDto> savedFacilityChildrenDetailsList=priorityClosureDao.getNotifiedFacilityChildrenDetails(facilityDetails.getIdVictimNotificationResource());
					facilityChildrenDetailsList=priorityClosureDao.getDefaultFacilityChildrenDetails(facilityDetails.getIdResource());
					List<IntakeNotfChildDto> savedFacilityChildrenDetailsList=savedFacilityDetails.getChildrenInFacilityList();

					facilityChildrenDetailsList.forEach(defaultChildDto-> {
						//update the details for the nonvictim section to get the saved primary key and last update date
						Optional<IntakeNotfChildDto> savedChildrenDtoOptional=savedFacilityChildrenDetailsList.stream().filter(savedChildDto-> savedChildDto.getIdPerson().equals(defaultChildDto.getIdPerson())).findFirst();
						//Changes done to fix PROD defect 14173 (PPM#60465)
						if(savedChildrenDtoOptional.isPresent()) {
							defaultChildDto.setIdVctmNotifctnRsrcChild(savedChildrenDtoOptional.get().getIdVctmNotifctnRsrcChild());
							defaultChildDto.setDtLastUpdate(savedChildrenDtoOptional.get().getDtLastUpdate());
						}
					});
				}

			}else {
				// Notify clicked
				// call to fetch the facility id details and the children
				IntakeNotfFacilityDto savedFacilityDetails=priorityClosureDao.getNotifiedFacilityDetails(intakeStageId);
				if(!ObjectUtils.isEmpty(savedFacilityDetails))
				{
					facilityDetails =savedFacilityDetails.getFacilityDetail();
					facilityChildrenDetailsList=savedFacilityDetails.getChildrenInFacilityList();
				}
			}

			priorityClosureDto.setVictimNotificationList(victimNotificationList);
			if(!ObjectUtils.isEmpty(facilityDetails) && !ObjectUtils.isEmpty(facilityDetails.getIdResource())) {
				notifFacilityDto.setFacilityDetail(facilityDetails);
			}
			notifFacilityDto.setChildrenInFacilityList(facilityChildrenDetailsList);
			priorityClosureDto.setFacilityDetails(notifFacilityDto);
			preDisplayPriorityClosureRes.setPriorityClosureDto(priorityClosureDto);

		}

		if (!TypeConvUtil.isNullOrEmpty(pCStageRow)) {
			// Call CINV34D
			List<PersonListDto> personList = personDao.getPersonListByIdStage(preDisplayPriorityClosureReq.getIdStage(),
					ServiceConstants.STAFF_TYPE);
			List<PersonListDto> stagePersReporter = personList.stream()
					.filter(p -> p.getIndStagePersReporter().equalsIgnoreCase(ServiceConstants.STRING_IND_Y))
					.collect(Collectors.toList());
			preDisplayPriorityClosureRes.setNoOfReporters(CollectionUtils.size(stagePersReporter));
			if (stagePersReporter != null && CollectionUtils.size(stagePersReporter) > 0) {
				for (PersonListDto p : stagePersReporter) {
					// PersonListDto p = stagePersReporter.iterator().next();
					String personFullName = (!StringUtils.isBlank(p.getPersonSuffix()))
							? (String.format("%s%s%s", p.getPersonFull(), ServiceConstants.SINGLE_WHITESPACE, p.getPersonSuffix()))
							: (String.format("%s", p.getPersonFull()));
					preDisplayPriorityClosureRes.setNmPersonFull(personFullName);
					preDisplayPriorityClosureRes.setIdPerson(p.getIdPerson());
					String txtDesc = String.format("%s%s", preDisplayPriorityClosureReq.getTxtEventDescr(),
							personFullName);
					// Call CINT73D
					List<Long> idEvent = eventDao.getIdEvent(preDisplayPriorityClosureReq.getIdStage(),
							preDisplayPriorityClosureReq.getCdEventType(),
							preDisplayPriorityClosureReq.getCdEventStatus(), txtDesc);
					if (idEvent == null || CollectionUtils.size(idEvent) == 0) {
						// check without suffix
						personFullName = String.format("%s", p.getPersonFull());
						preDisplayPriorityClosureRes.setNmPersonFull(personFullName);
						preDisplayPriorityClosureRes.setIdPerson(p.getIdPerson());
						txtDesc = String.format("%s%s", preDisplayPriorityClosureReq.getTxtEventDescr(),
								personFullName);
						// Call CINT73D
						idEvent = eventDao.getIdEvent(preDisplayPriorityClosureReq.getIdStage(),
								preDisplayPriorityClosureReq.getCdEventType(),
								preDisplayPriorityClosureReq.getCdEventStatus(), txtDesc);
						if (idEvent == null || CollectionUtils.size(idEvent) == 0) {
							preDisplayPriorityClosureRes.setSysIndGeneric(ServiceConstants.STRING_IND_N);
							break;
						} else {
							preDisplayPriorityClosureRes.setSysIndGeneric(ServiceConstants.STRING_IND_Y);
						}
					} else {
						preDisplayPriorityClosureRes.setSysIndGeneric(ServiceConstants.STRING_IND_Y);
					}

				}
			} else {
				preDisplayPriorityClosureRes.setNmPersonFull(ServiceConstants.NULL_VALUE);
				preDisplayPriorityClosureRes.setIdPerson(ServiceConstants.NULL_VAL);
			}

		}

		preDisplayPriorityClosureRes.setDtGenericSysdate(new Date());
		log.info("TransactionId :" + preDisplayPriorityClosureRes.getTransactionId());
		return preDisplayPriorityClosureRes;
	}

	public PriorityClosureLicensingDto getLicensingInformation(PreDisplayPriorityClosureReq preDisplayPriorityClosureReq, Long idCase){
		// Retrieve licensing information based on stage id.
		PriorityClosureLicensingDto priorityClosureLicensingDto = priorityClosureDao
				.getIntakeLicensingDetails(preDisplayPriorityClosureReq.getIdStage());
		// If the licensing information is not present in PriorityClosure table , get the information
		// from incoming_facility, facility and caps_resource table.
		if (ObjectUtils.isEmpty(priorityClosureLicensingDto)) {
			priorityClosureLicensingDto = new PriorityClosureLicensingDto();
			RetreiveIncomingFacilityInputDto inputDto = new RetreiveIncomingFacilityInputDto();
			RetreiveIncomingFacilityOutputDto outputDto = new RetreiveIncomingFacilityOutputDto();
			inputDto.setIdStage(preDisplayPriorityClosureReq.getIdStage());
			// Get the operation resource id from incoming_facility table.
			fetchIncomingFacilityDao.fetchIncomingFacility(inputDto, outputDto);
			if (!ObjectUtils.isEmpty(idCase)) {
				AgencyHomeInfoDto resourceInfoDto = null;
				AgencyHomeInfoDto agencyHomeInfoDto = null;
				if (!ObjectUtils.isEmpty(outputDto) && !ObjectUtils.isEmpty(outputDto.getIdResource()) && 0L < outputDto.getIdResource()) {
					// Get the Operation information from facility and caps_resource table using the resource id
					resourceInfoDto = cpsIntakeReportDao.getResourceInfoDto(outputDto.getIdResource());
					if(!ObjectUtils.isEmpty(resourceInfoDto)){
						populateOperationInformation(priorityClosureLicensingDto,resourceInfoDto, outputDto.getCdIncmgFacilType());
					}
				}
				// Get the Agency Home's facility id from INTAKE_REPORT
				ClassIntakeDto intakeDto = cpsIntakeReportDao.getIntakeClassData(idCase);
				// Get the agency home info if the operation facility is "Child Placing Agency"
				if (!ObjectUtils.isEmpty(intakeDto) && !ObjectUtils.isEmpty(intakeDto.getFacilityId())
						&& !ObjectUtils.isEmpty(outputDto) && CodesConstant.CFACTYP2_60.equalsIgnoreCase(outputDto.getCdIncmgFacilType())) {
					agencyHomeInfoDto = cpsIntakeReportDao.getAgencyHomeInfoDto(intakeDto.getFacilityId());
					if(!ObjectUtils.isEmpty(agencyHomeInfoDto)){
						populateAgencyHomeInformation(priorityClosureLicensingDto, agencyHomeInfoDto);
					}
				}
			}
		}
		return priorityClosureLicensingDto;
	}

	private void populateOperationInformation(PriorityClosureLicensingDto priorityClosureLicensingDto,AgencyHomeInfoDto resourceInfoDto, String facilityType){
		priorityClosureLicensingDto.setIdResource(resourceInfoDto.getResourceId());
		priorityClosureLicensingDto.setNmResource(resourceInfoDto.getFacilityName());
		priorityClosureLicensingDto.setCdRsrcFacilType(facilityType);
		if(!ObjectUtils.isEmpty(resourceInfoDto.getFacilityNumber())) {
			priorityClosureLicensingDto.setClassFacilType(getClassFacilityType(resourceInfoDto.getFacilityNumber().intValue(), null));
		}
		priorityClosureLicensingDto.setNbrAcclaim(!ObjectUtils.isEmpty(resourceInfoDto.getFacilityNumber())?resourceInfoDto.getFacilityNumber().intValue():null);
		priorityClosureLicensingDto.setNbrAgency(!ObjectUtils.isEmpty(resourceInfoDto.getAgencyNumber())? resourceInfoDto.getAgencyNumber().intValue():null);
		priorityClosureLicensingDto.setNbrBranch(!ObjectUtils.isEmpty(resourceInfoDto.getBranchNumber())? resourceInfoDto.getBranchNumber().shortValue():null);
	}

	private void populateAgencyHomeInformation(PriorityClosureLicensingDto priorityClosureLicensingDto,AgencyHomeInfoDto agencyHomeInfoDto) {
		priorityClosureLicensingDto.setIdAffilResource(agencyHomeInfoDto.getResourceId());
		priorityClosureLicensingDto.setNmAffilResource(agencyHomeInfoDto.getFacilityName());
		priorityClosureLicensingDto.setCdAffilRsrcFacilType(agencyHomeInfoDto.getResourceFacilityType());
		if(!ObjectUtils.isEmpty(agencyHomeInfoDto.getFacilityNumber())){
			priorityClosureLicensingDto.setClassAffilFacilType(getClassFacilityType(agencyHomeInfoDto.getFacilityNumber().intValue(), ServiceConstants.A));
		}
		priorityClosureLicensingDto.setNbrAffilAcclaim(!ObjectUtils.isEmpty( agencyHomeInfoDto.getFacilityNumber() )? agencyHomeInfoDto.getFacilityNumber().intValue():null);
		priorityClosureLicensingDto.setNbrAffilAgency(!ObjectUtils.isEmpty(agencyHomeInfoDto.getAgencyNumber() )?agencyHomeInfoDto.getAgencyNumber().intValue():null);
		priorityClosureLicensingDto.setNbrAffilBranch(!ObjectUtils.isEmpty(agencyHomeInfoDto.getBranchNumber())? agencyHomeInfoDto.getBranchNumber().shortValue():null);
	}

		private String getClassFacilityType(Integer facilityNumber, String indAgencyHome){
		String classFacilityType="";
		List<ClassFacilityDto> classFacilityDtoList = licensingInvstDtlDao.getClassFacilityView(
				facilityNumber, null, null, indAgencyHome);
		if (!classFacilityDtoList.isEmpty() && !ObjectUtils.isEmpty(classFacilityDtoList.get(0))) {
			classFacilityType = classFacilityDtoList.get(0).getDescFacilityType();
		}
		return classFacilityType;
	}

	/**
	 *
	 *
	 * Method Description: This Method is to retrieve ideventperson, job class,
	 * personname and employee class for priority closure screen. Service
	 * Name:Priority Closure Ejb Service Tuxedo Dam Name: PriortyClosureDAO
	 *
	 * @param priorityClosureEjbReq
	 * @return priorityClosureEjbRes @
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PriorityClosureRes getIdEventPersonInfo(PriorityClosureReq priorityClosureEjbReq) {
		PriorityClosureRes priorityClosureEjbRes = new PriorityClosureRes();
		priorityClosureEjbRes
				.setPriorityClosureEjbDto(workLoadDao.getIdEventPersonInfo(priorityClosureEjbReq.getIdStage()));
		return priorityClosureEjbRes;
	}

	/**
	 *
	 * Method Description: Returns CPS stageIds that has pending investigation
	 * conclusions.
	 *
	 * @param commonHelperReq
	 * @return IDListRes @ Tuxedo Service Name: NA
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public IDListRes getInvCnclPendingStages(CommonHelperReq commonHelperReq) {
		IDListRes invCnclPendingStages = new IDListRes();
		invCnclPendingStages.setIdList(workLoadDao.getInvCnclPendingStages(commonHelperReq.getIdWorker()));
		return invCnclPendingStages;
	}

	/**
	 *
	 * Method Description: Returns an array of stage ID's representing the
	 * subset of a passed array of stage ID's that are currently checked out to
	 * MPS.
	 *
	 * @param commonHelperReq
	 * @return IDListRes @ Tuxedo Service Name: NA
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public IDListRes getCheckedOutStages(CommonHelperReq commonHelperReq) {
		IDListRes checkedOutStages = new IDListRes();
		checkedOutStages.setIdList(workLoadDao.getCheckedOutStages(commonHelperReq.getStageIDs()));
		return checkedOutStages;
	}

	/**
	 *
	 * Method Description: Optimize workload page query to improve page loading
	 * times. Returns the list of Stage Ids that has A-R as a prior stage
	 *
	 * @param commonHelperReq
	 * @return IDListRes @ Tuxedo Service Name: NA
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public IDListRes getStagesWithARasPriorStage(CommonHelperReq commonHelperReq) {
		IDListRes stagesWithARasPendingStage = new IDListRes();
		stagesWithARasPendingStage.setIdList(workLoadDao.getStagesWithARasPriorStage(commonHelperReq.getIdWorker()));
		return stagesWithARasPendingStage;
	}

	/**
	 *
	 * Method Description: Returns list of AR stages that requires worker
	 * attention
	 *
	 * @param commonHelperReq
	 * @return IDListRes @ Tuxedo Service Name: NA
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public IDListRes getARPendingStages(CommonHelperReq commonHelperReq) {
		IDListRes arPendingStagesRes = new IDListRes();
		Long holdStageID;
		List<Long> arPendingStages = new ArrayList<Long>();
		ARPendingStagesDto arPendingStagesDto = new ARPendingStagesDto();
		List<ARPendingStagesDto> arPendingStagesList = new ArrayList<ARPendingStagesDto>();
		arPendingStagesList = workLoadDao.getARPendingStages(commonHelperReq.getIdWorker());
		for (ARPendingStagesDto arPendingStagesDtoLoop : arPendingStagesList) {
			holdStageID = arPendingStagesDtoLoop.getIdWkldStage();
			arPendingStagesDto = workLoadDao.getARExtensionRequest(holdStageID);
			if (null != arPendingStagesDto && !"APRV".equals(arPendingStagesDto.getCdEventStatus())) {
				arPendingStages.add(holdStageID);
			}
		}
		arPendingStagesRes.setIdList(arPendingStages);
		return arPendingStagesRes;
	}

	/**
	 * Method Description:This method is used to check if the Stage is closed or
	 * not.
	 *
	 * @param stage
	 *            id
	 * @return Boolean true or false @
	 */
	@Override
	public CommonHelperRes isStageClosed(CommonHelperReq caseSummaryReq) {
		Boolean isStageClosed = Boolean.FALSE;
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		StageDto stagedto = stageDao.getStageById(caseSummaryReq.getIdStage());
		stagedto.getDtStageClose();
		if (null != stagedto.getDtStageClose()) {
			isStageClosed = Boolean.TRUE;
		}
		commonHelperRes.setIsStageClosed(isStageClosed);
		return commonHelperRes;
	}

	/**
	 *
	 * Method Description: Method for Priority Closure Save/Close. This Updates
	 * _TODO, STAGE, Incoming Detail, and Creates Event.
	 *
	 * @param PriorityClosureSaveReq
	 * @return PriorityClosureSaveRes @ Tuxedo Service Name: CINT21S
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PriorityClosureSaveRes savePriority(PriorityClosureSaveReq priorityClosureSaveReq) {
		PriorityClosureSaveRes priorityClosureSaveRes = new PriorityClosureSaveRes();
		Long todoCount = 0l;
		Long contactCount = 0l;
		String isIntakeFormallyScreened = priorityClosureSaveReq.getPriorityClosureDto().getIndIntakeFormallyScreened();
		boolean errorFlag = false;
		if (ServiceConstants.CCL.equals(priorityClosureSaveReq.getPriorityClosureDto().getCdStageProgram()) ||
				ServiceConstants.CPGRMS_RCL.equals(priorityClosureSaveReq.getPriorityClosureDto().getCdStageProgram())) {
			PriorityClosure priorityClosure = priorityClosureDao.getPriorityClosureLicensingDetails(priorityClosureSaveReq.getPriorityClosureDto().getIdStage());
			priorityClosure = populatePriorityClosureLicensingDetails(priorityClosureSaveReq, priorityClosure);
			priorityClosureDao.savePriorityClosureLicensingDetails(priorityClosure);
		}
		if (ServiceConstants.CCL.equals(priorityClosureSaveReq.getPriorityClosureDto().getCdStageProgram())) {
			workLoadDao.savePriorityTracking(priorityClosureSaveReq);
		}
		if (priorityClosureSaveReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.CLOSE)) {
			todoCount = todoDao.getTodobyStageTask(priorityClosureSaveReq);
			contactCount = todoDao.getContactCount(priorityClosureSaveReq);
			Boolean isCrsrStage = priorityClosureSaveReq.getPriorityClosureDto().getIsCrsrStage();
			if (!priorityClosureSaveReq.getPriorityClosureDto().getCdStage().equals(ServiceConstants.INR_STAGE)
					&& !isCrsrStage) {
				if (todoCount > ServiceConstants.ZERO_VAL) {
					priorityClosureSaveRes.setbIndFormalScreenContactCreated("MSG_INT_LE_NOTIF_REQUIRED");
					errorFlag = true;

				} else if (ServiceConstants.Y.equalsIgnoreCase(isIntakeFormallyScreened)
						&& contactCount == ServiceConstants.ZERO_VAL) {
					priorityClosureSaveRes.setbIndFormalScreenContactCreated("MSG_FORMAL_SCREENING_CONTACT_REQ");
					errorFlag = true;
				}
			}
		}
		if (!errorFlag) {
			if (!TypeConvUtil.isNullOrEmpty(priorityClosureSaveReq.getPriorityClosureDto().getIdStage())) {
				try {
					stageDao.updateStagePriority(priorityClosureSaveReq);
				} catch (DataLayerException e) {
					ErrorDto error = new ErrorDto();
					if (e.getMessage().endsWith(ServiceConstants.MSG_SYS_STAGE_CLOSED)) {
						error.setErrorMsg(ServiceConstants.MSG_SYS_STAGE_CLOSED);
						priorityClosureSaveRes.setErrorDto(error);
					}
					return priorityClosureSaveRes;
				}
			}
			// Incoming Detail
			Person person = new Person();
			person.setIdPerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());

			boolean isCrsrStage = priorityClosureSaveReq.getPriorityClosureDto().getIsCrsrStage().booleanValue();
			if (isCrsrStage
					|| priorityClosureSaveReq.getPriorityClosureDto().getCdStage().equals(ServiceConstants.INR_STAGE)) {
				// Incoming Detail
				IncomingDetail incomingDetail = new IncomingDetail();
				incomingDetail.setCdIncmgStatus(ServiceConstants.STATUS_CLOSED);
				incomingDetail.setIdStage(priorityClosureSaveReq.getPriorityClosureDto().getIdStage());
				incomingDao.updateIncomingDetail(incomingDetail, priorityClosureSaveReq);
			}
			// PD 73570: No Date for Screener TODO - It shouldn't insert record into TODO table when
			// priority changed to 'N' and is closed. Deleted the TODO insert code because it's not longer required

			if (!priorityClosureSaveReq.getPriorityClosureDto().getCdStage().equals(ServiceConstants.CSTAGES_IR)
					&& !priorityClosureSaveReq.getPriorityClosureDto().getIsCrsrStage()) {
				PostEventReq postEventReq = new PostEventReq();
				PostEventRes postEventRes = new PostEventRes();
				postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				postEventReq.setDtDtEventOccurred(new Date());
				postEventReq.setTsLastUpdate(new Date());
				if (priorityClosureSaveReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.SAVE)) {
					postEventReq.setSzCdEventType(priorityClosureSaveReq.getPriorityClosureDto().getCdEventType());
				} else {
					postEventReq.setSzCdEventType(ServiceConstants.CEVNTTYP_STG);
				}
				postEventReq.setSzCdEventStatus(ServiceConstants.EVENT_STAT_COMP);
				postEventReq.setUlIdStage(priorityClosureSaveReq.getPriorityClosureDto().getIdStage());
				if (!TypeConvUtil.isNullOrEmpty(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson()))
					postEventReq.setUlIdPerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());
				if (!TypeConvUtil.isNullOrEmpty(priorityClosureSaveReq.getPriorityClosureDto().getIdCase()))
					postEventReq.setUlIdCase(priorityClosureSaveReq.getPriorityClosureDto().getIdCase());
				if (!TypeConvUtil.isNullOrEmpty(priorityClosureSaveReq.getPriorityClosureDto().getEventDescr()))
					postEventReq.setSzTxtEventDescr(priorityClosureSaveReq.getPriorityClosureDto().getEventDescr());
				if (!ObjectUtils.isEmpty(postEventReq.getSzTxtEventDescr()))
					postEventRes = eventService.postEvent(postEventReq);
				priorityClosureSaveRes.setUlIdEvent(postEventRes.getUlIdEvent());
			}
			if (priorityClosureSaveReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.SAVE)) {
				priorityClosureSaveRes.setbIndFormalScreenContactCreated(ServiceConstants.SAVE_SUCCESS);
				//artf128805 : FCL changes : to save the notification section only for RCI intake and opened after the REL date
				if(isFCLCheck(priorityClosureSaveReq.getPriorityClosureDto().getCdStageProgram(), priorityClosureSaveReq.getPriorityClosureDto().getDtStageStart(),priorityClosureSaveReq.getPriorityClosureDto().getFclRelDate()))
					saveFCLNotificationDetails(priorityClosureSaveReq);
			} else {
				priorityClosureSaveRes.setbIndFormalScreenContactCreated(ServiceConstants.CLOSE_SUCCESS);
			}
		}
		if (priorityClosureSaveRes.getbIndFormalScreenContactCreated()
				.equalsIgnoreCase(ServiceConstants.CLOSE_SUCCESS)) {
			CloseStageCaseInputDto closeStageCaseInput = new CloseStageCaseInputDto();
			closeStageCaseInput.setCdStage(priorityClosureSaveReq.getPriorityClosureDto().getCdStage());
			closeStageCaseInput.setCdStageProgram(priorityClosureSaveReq.getPriorityClosureDto().getCdStageProgram());
			closeStageCaseInput
					.setCdStageReasonClosed(priorityClosureSaveReq.getPriorityClosureDto().getCdStageReasonClosed());
			closeStageCaseInput.setEventDescr(priorityClosureSaveReq.getPriorityClosureDto().getEventDescr());
			closeStageCaseInput.setIdStage(priorityClosureSaveReq.getPriorityClosureDto().getIdStage());
			closeStageCaseInput.setIdPerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());
			closeStageCaseInput.setReqFuncCd(priorityClosureSaveReq.getReqFuncCd());
			closeStageCaseInput.setIsCrsrStage(priorityClosureSaveReq.getPriorityClosureDto().getIsCrsrStage());
			closeStageCaseService.closeStageCase(closeStageCaseInput);
		}

		return priorityClosureSaveRes;
	}

	private PriorityClosure populatePriorityClosureLicensingDetails(PriorityClosureSaveReq priorityClosureSaveReq, PriorityClosure priorityClosure) {
		if(ObjectUtils.isEmpty(priorityClosure)){
			priorityClosure = new PriorityClosure();
			priorityClosure.setIdCreatedPerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());
			priorityClosure.setDtCreated(new Date());
			priorityClosure.setIdStage(priorityClosureSaveReq.getPriorityClosureDto().getIdStage());
			priorityClosure.setIdCase(priorityClosureSaveReq.getPriorityClosureDto().getIdCase());
		}
		BeanUtils.copyProperties(priorityClosureSaveReq.getPriorityClosureLicensingDto(), priorityClosure,ignoreProperties);
		priorityClosure.setDtLastUpdate(new Date());
		priorityClosure.setIdLastUpdatePerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());
		return priorityClosure;
	}

	/**
	 * Returns Extension request Event object for the given AR stage.
	 *
	 * @param idStage
	 *            in CommonHelperReq
	 * @return Event @
	 */
	@Override
	@Transactional
	public ARExtensionRes getARExtensionRequest(CommonHelperReq commonHelperReq) {
		ARExtensionRes arExtensionRes = new ARExtensionRes();
		ARPendingStagesDto arPendingStagesDto = new ARPendingStagesDto();
		arPendingStagesDto = workLoadDao.getARExtensionRequest(commonHelperReq.getIdStage());
		if (null != arPendingStagesDto) {
			arExtensionRes.setARExtensionDetail(arPendingStagesDto);
		}
		return arExtensionRes;
	}

	/**
	 * Method Description:Returns cd mobile Status given a Workload Case Id to
	 * it.
	 *
	 * @param idCase
	 *            in CommonHelperReq
	 * @return String @
	 */
	@Override
	@Transactional
	public CommonHelperRes getCdMobileStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setCdMobileStatus(workLoadDao.getCdMobileStatus(commonHelperReq.getIdCase()));
		return commonHelperRes;
	}

	/**
	 * Method-Description:Returns the role of a person in a stage, provided the
	 * case is in that person's workload. If the person does not have a role, or
	 * the stage is no in their workload, will return empty string (""). Should
	 * always return PRIMARY ("PR") or SECONDARY ("SE").
	 *
	 * @param IdPerson
	 * @param IdStage
	 * @return the role of the person in that stage in their workload. @
	 */
	@Override
	@Transactional
	public CommonHelperRes getRoleInWorkloadStage(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setRoleInWorkloadStage(
				workLoadDao.getRoleInWorkloadStage(commonHelperReq.getIdStage(), commonHelperReq.getIdPerson()));
		return commonHelperRes;
	}

	@Override
	public IDListRes getActiveStagesForPerson(CommonHelperReq commonHelperReq) {
		IDListRes idListRes = new IDListRes();
		idListRes.setIdList(workLoadDao.getActiveStagesForPerson(commonHelperReq.getIdPerson()));
		return idListRes;
	}

	/**
	 * Method-Description:Check Configuration table ONLINE_PARAMETERS, if Tlets
	 * Check needs to be Enabled or Disabled
	 *
	 * @return Boolean -- true or false @
	 */
	@Override
	public CommonHelperRes disableTletsCheck() {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setDisableTletsCheck(workLoadDao.disableTletsCheck());
		return commonHelperRes;
	}

	/**
	 * Method Description: This method will implement Workload Service. Service
	 * Name: KinApproval
	 *
	 * @param kinApprovalReq
	 * @return KinApprovalRes @
	 */
	public KinApprovalRes checkStageCaseID(KinApprovalReq kinApprovalReq) {
		KinApprovalRes kinApprovalRes = new KinApprovalRes();
		kinApprovalRes.setIsKinStage(approvalDao.checkStageCaseID(kinApprovalReq.getIdCaseFrom()));
		return kinApprovalRes;
	}

	/**
	 * Method Description: This method is to find if the contact with the
	 * purpose of initial already exist.
	 *
	 * @paramidStage
	 * @returnBoolean -- true or False
	 */
	@Override
	@Transactional
	public CommonHelperRes getContactPurposeStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setContactPurposeStatus(workLoadDao.getContactPurposeStatus(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}



	/**
	 * Method Description: This method is to find if there is an approved
	 * contact for the given case id.
	 *
	 * @paramidCaseContactType
	 * @returnBoolean -- true or False
	 */
	@Override
	@Transactional
	public CommonHelperRes isAprvContactInCase(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIsAprvContactInCase(
				workLoadDao.isAprvContactInCase(commonHelperReq.getIdCase(), commonHelperReq.getIdContactType()));
		return commonHelperRes;
	}

	/**
	 * Method Description: Returns an a boolean value based on whether or not
	 * either of the two passed person ID's is tied to a stage currently checked
	 * out to MPS
	 *
	 * @param idPerson1
	 *            and idPerson2
	 * @returnBoolean -- true or False
	 */
	@Override
	@Transactional
	public CommonHelperRes getCheckedOutPersonStatus(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setCheckedOutPersonStatus(
				workLoadDao.getCheckedOutPersonStatus(commonHelperReq.getIdPerson(), commonHelperReq.getIdPerson2()));
		return commonHelperRes;
	}

	/**
	 * Returns the last (using the fact that ID columns increment) approvers
	 * status record for the event. This will always be one of "APRV," "REJT,"
	 * "PEND," OR "INVD." If the event has not been submitted for approval, it
	 * will be null.
	 *
	 * @param ulIdEvent
	 *            The ID_EVENT of the event for which the Approvers status will
	 *            be returned.
	 * @return The approvers status for the particular event.
	 */
	@Override
	public ApproverStatusRes getApproversStatus(CommonHelperReq commonHelperReq) {
		return new ApproverStatusRes(approvalDao.getApproversStatus(commonHelperReq.getIdEvent()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.workload.service.WorkloadService#
	 * getPrimaryWorkerIdForStage(us.tx.state.dfps.service.common.request.
	 * CommonHelperReq)
	 */
	@Override
	public CommonHelperRes getPrimaryWorkerIdForStage(CommonHelperReq commonHelperReq) {
		return approvalDao.getPrimaryWorkerIdForStage(commonHelperReq);
	}

	/**
	 * Method Name: getAssignedWorkersForStage Method Description: Fetch the
	 * Primary and Secondary Workers assigned to the passed Stage ID
	 *
	 * @param idStage
	 * @return List<Long>
	 */
	@Override
	public List<Long> getAssignedWorkersForStage(Long idStage) {
		return workLoadDao.getAssignedWorkersForStage(idStage);
	}

	@Override
	public String getStagePersRole(Long idStage, Long idPerson) {
		return workLoadDao.getStagePersRole(idStage, idPerson);
	}


	/**
	 * AR - Stage progression from AR - FPR - message not displayed This method
	 * will fetch the overall disposition
	 *
	 * @param idCase
	 *            the stage identifier
	 * @return String disposition code @
	 */
	@Override
	@Transactional
	public CommonHelperRes getARStageOverallDisposition(CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setArStageOverallDisposition(
				approvalDao.getARStageOverallDisposition(commonHelperReq.getIdCase(), commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	/**
	 * Method Description: This Method will retrieve the Risk Assessment Details
	 * as a List for the particular Case by passing stageID, caseId and taskId
	 * as Input.
	 *
	 * @param eventReq
	 * @return EventDetailRes @
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public EventDetailRes getSDMAssmntListDtls(EventReq eventReq) {
		EventDetailRes eventDetailRes = new EventDetailRes();
		List<EventListDto> eventdetails = eventDao.getSDMAssmntList(eventReq);
		/*For FSU and FRE stages display the UNSAFE assessments from previous
		stages and;
		 For FPR display the Safe with Plan assessments from previous stages
		defect no 9869 */
		if (!ObjectUtils.isEmpty(eventdetails) && (ServiceConstants.CD_TASK_SA_FSU.equals(eventReq.getSzCdTask())
				|| ServiceConstants.CD_TASK_SA_FRE.equals(eventReq.getSzCdTask()))) {
			eventdetails.removeIf(eventDto -> eventDto.getIdStage().compareTo(eventReq.getUlIdStage()) != 0
					&& !UNSAFE.equalsIgnoreCase(eventDto.getCdSafetyDecision()));
		} else if (!ObjectUtils.isEmpty(eventdetails)
				&& (ServiceConstants.CD_TASK_SA_FPR.equals(eventReq.getSzCdTask()))) {
			eventdetails.removeIf(eventDto -> eventDto.getIdStage().compareTo(eventReq.getUlIdStage()) != 0
					&& !SAFEWITHPLAN.equalsIgnoreCase(eventDto.getCdSafetyDecision()));
		}
		eventDetailRes.setEventSearchDto(eventdetails);
		log.info("TransactionId :" + eventReq.getTransactionId());
		return eventDetailRes;
	}

	/**
	 * Method Description: This service will returns a the event and todo id's
	 * for an approval given an event from the stage
	 *
	 * @param ToDoUtilityReq
	 * @return ToDoUtilityRes @
	 */
	@Override
	@Transactional
	public TodoDto getToDoIdForApproval(ToDoUtilityReq toDoUtilityReq) {
		TodoDto toDoDto = new TodoDto();
		toDoDto = todoDao.getToDoIdForApproval(toDoUtilityReq);
		return toDoDto;
	}

	/**
	 * Returns idEvent for the given Approval Event.
	 *
	 * @param idAprvlEvent
	 *
	 * @return idEvent
	 *
	 * @ InvalidRequestException
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes fetchIdEventForIdAprEvent(CommonHelperReq commonHelperReq) {
		CommonHelperRes resp = new CommonHelperRes();
		resp.setIdEvent(approvalDao.fetchIdEventForIdAprEvent(commonHelperReq.getIdEvent()));
		return resp;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonBooleanRes getPrintTaskExists(CommonHelperReq commonHelperReq) {
		CommonBooleanRes resp = null;
		if (commonHelperReq != null && commonHelperReq.getIdCase() != null && commonHelperReq.getIdStage() != null
				&& commonHelperReq.getIdEvent() != null) {
			resp = todoDao.getPrintTaskExistsSql(commonHelperReq.getIdCase(), commonHelperReq.getIdStage(),
					commonHelperReq.getIdEvent());
		}
		if (!ObjectUtils.isEmpty(commonHelperReq.getTransactionId())) {
			log.info("TransactionId :" + commonHelperReq.getTransactionId());
		}
		return resp;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes getTodoDtlById(Long todoId) {
		CommonHelperRes resp = new CommonHelperRes();
		TodoDto todoDto = todoDao.getTodoDtlsById(todoId);
		resp.setToDoDto(todoDto);
		return resp;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public TodoDto getToDoById(Long idTodo) {
		TodoDto toDoDto = new TodoDto();
		toDoDto = todoDao.getTodoDtlsById(idTodo);
		return toDoDto;
	}

	/**
	 * Method Description: This method is used to retrieve the information for
	 * M-ref Status form by passing IdPerson as input request
	 *
	 *
	 * @param mrefStatusFormReq
	 * @return PreFillDataServiceDto @
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getMrefStatusForm(MrefStatusFormReq mrefStatusFormReq) {

		MrefStatusFormDto mrefStatusFormDto = new MrefStatusFormDto();

		List<MrefStatusFormDtlsDto> mrefStatusFormDtlslist = new ArrayList<MrefStatusFormDtlsDto>();

		// Call CSEC01D to retreive the Worker Name and Worker Id
		EmployeePersPhNameDto employeePersPhNameDto = employeeDao
				.searchPersonPhoneName(mrefStatusFormReq.getIdPerson());
		mrefStatusFormDto.setEmployeePersPhNameDto(employeePersPhNameDto);

		// Call CLSC0AD to retrieve the M-ref details for the personId
		List<MrefDtlsDto> mrefDtlslist = assignWorkloadDao.getMrefDtls(mrefStatusFormReq.getIdPerson(),
				ServiceConstants.YES_INDICATOR);



		// Declaration of list for CLSC0BD
		MrefSecondaryDtlsDto mrefSecondaryDtlsDto = new MrefSecondaryDtlsDto();

		List<MrefSecondaryDtlsDto> mrefSecondaryDtlslist = null;

		// Declaration of list for CLSC0CD
		MrefCssContactDtlsDto mrefCssContactDtlsDto = new MrefCssContactDtlsDto();

		List<MrefCssContactDtlsDto> mrefCssContactDtlslist = null;

		// Call CLSC0BD and CLSC0CD only if there are M-Ref details for the
		// personId
		if ((null != mrefDtlslist) && (mrefDtlslist.size() > ServiceConstants.Zero)) {
			for (MrefDtlsDto mrefDtlsDto : mrefDtlslist) {
				// Call CLSC0BD by passing the StageId and RoleCd as
				// 'SECONDARY_ASSIGN_STAFF'

				mrefSecondaryDtlslist = assignWorkloadDao.getMrefSecondaryDtls(mrefDtlsDto.getIdStage(),
						ServiceConstants.CD_ROLE);

				// Call CLSC0CD by passing the StageId and ContactPurpose in
				// 'CSS_REVIEW_FULL','CSS_REVIEW_OTHER','CSS_REVIEW_SCREENED'

				mrefCssContactDtlslist = assignWorkloadDao.getMrefCssContactDtls(mrefDtlsDto.getIdStage(),
						ServiceConstants.CD_CSS_REVIEW_FULL, ServiceConstants.CD_CSS_REVIEW_OTHER,
						ServiceConstants.CD_CSS_REVIEW_SCREENED);

				MrefStatusFormDtlsDto mrefStatusFormDtlsDto = new MrefStatusFormDtlsDto();

				// Assigning MRefDto
				mrefStatusFormDtlsDto.setMrefDtlsDto(mrefDtlsDto);

				// If secondary details are not present for StageId,
				// then assign default/null values
				// else assign secondary details returned by the query
				if (mrefSecondaryDtlslist.isEmpty()) {
					mrefSecondaryDtlsDto.setIdStage(mrefDtlsDto.getIdStage());
					mrefSecondaryDtlsDto.setCdJobClassDecode(ServiceConstants.EMPTY_STRING);
					mrefSecondaryDtlsDto.setDtSecondaryAssgnd(ServiceConstants.NULL_DATE_TYPE);
					mrefSecondaryDtlsDto.setDtSecondaryUnassgnd(ServiceConstants.NULL_DATE_TYPE);
					mrefSecondaryDtlsDto.setNmPersonFull(ServiceConstants.JOB_CLASS_DECODE);
					mrefSecondaryDtlslist.add(mrefSecondaryDtlsDto);
					mrefStatusFormDtlsDto.setMrefSecondaryDtlsDto(mrefSecondaryDtlslist);
				} else {
					mrefStatusFormDtlsDto.setMrefSecondaryDtlsDto(mrefSecondaryDtlslist);

				}

				// If contact details are not present for StageId,
				// then assign default/null values
				// else assign contact details returned by the query
				if (mrefCssContactDtlslist.isEmpty())

				{
					mrefCssContactDtlsDto.setCdContactPurposeDecode(ServiceConstants.EMPTY_STRING);
					mrefCssContactDtlsDto.setCdContactType(ServiceConstants.EMPTY_STRING);
					mrefCssContactDtlsDto.setIdContactStage(mrefDtlsDto.getIdStage());
					mrefCssContactDtlsDto.setDtContactOccured(ServiceConstants.NULL_DATE_TYPE);
					mrefCssContactDtlsDto.setNmPersonFull(ServiceConstants.PERSON_FULL_NAME);
					mrefCssContactDtlslist.add(mrefCssContactDtlsDto);
					mrefStatusFormDtlsDto.setMrefCssContactDtlsDto(mrefCssContactDtlslist);
				} else {
					mrefStatusFormDtlsDto.setMrefCssContactDtlsDto(mrefCssContactDtlslist);
				}

				mrefStatusFormDtlslist.add(mrefStatusFormDtlsDto);
			}
		}

		mrefStatusFormDto.setMrefStatusFormDtlsDto(mrefStatusFormDtlslist);

		return assignWorkloadPrefillData.returnPrefillData(mrefStatusFormDto);
	}

	/**
	 * Method Description: This method is used to retrieve the information for
	 * SIJS Status form by passing IdPerson,cdRegion,cdCitizenship as input
	 * request
	 *
	 *
	 * @param citizenStatusFormReq
	 * @return PreFillDataServiceDto @
	 */

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getSijsForm(CitizenStatusFormReq citizenStatusFormReq) {

		SijsStatusFormDto sijsStatusFormDto = new SijsStatusFormDto();

		List<SijsStatusFormDtlsDto> SijsStatusFormDtlslist = new ArrayList<SijsStatusFormDtlsDto>();

		// Call CSEC01D to retreive the Worker Name and Worker Id
		EmployeePersPhNameDto employeePersPhNameDto = employeeDao
				.searchPersonPhoneName(citizenStatusFormReq.getIdPerson());
		sijsStatusFormDto.setEmployeePersPhNameDto(employeePersPhNameDto);

		// Call CLSC1CD and CLSC1ED to get retrieve the SIJS Details
		List<SijsDtlsDto> sijsDtlslist = null;

		sijsDtlslist = assignWorkloadDao.getSijsDtls(citizenStatusFormReq.getIdPerson(),
				citizenStatusFormReq.getCdRegion(), citizenStatusFormReq.getCdPersonCitizenship());

		SijsSecondaryDtlsDto sijsSecondaryDtlsDto = new SijsSecondaryDtlsDto();

		// Call CLSC1CD,CLSC1DD,CLSS1AD,CLSCDCD only if there are M-Ref details
		// for the personId
		if ((null != sijsDtlslist) && (sijsDtlslist.size() > ServiceConstants.Zero)) {
			for (SijsDtlsDto sijsDtlsDto : sijsDtlslist) {
				SijsStatusFormDtlsDto sijsStatusFormDtlsDto = new SijsStatusFormDtlsDto();

				// Setting the SIJS Details
				sijsStatusFormDtlsDto.setSijsDtlsDto(sijsDtlsDto);

				// Call CLSC1DD to get the secondary assignee details

				List<SijsSecondaryDtlsDto> sijsSecondaryDtlslist = null;
				sijsSecondaryDtlslist = assignWorkloadDao.getSijsSecondaryDtls(sijsDtlsDto.getIdStage(),
						ServiceConstants.CD_ROLE);
				if (sijsSecondaryDtlslist.isEmpty()) {

					sijsSecondaryDtlsDto.setIdStage(sijsDtlsDto.getIdStage());
					sijsSecondaryDtlsDto.setCdJobClassDecode(ServiceConstants.EMPTY_STRING);
					sijsSecondaryDtlsDto.setDtAssgnd(ServiceConstants.NULL_DATE_TYPE);
					sijsSecondaryDtlsDto.setDtUnAssgnd(ServiceConstants.NULL_DATE_TYPE);
					sijsSecondaryDtlsDto.setNmPersonFull(ServiceConstants.JOB_CLASS_DECODE);
					sijsSecondaryDtlsDto.setNbrDaysAssigned(ServiceConstants.ZERO);
					sijsSecondaryDtlslist.add(sijsSecondaryDtlsDto);
					sijsStatusFormDtlsDto.setSijsSecondaryDtlsDto(sijsSecondaryDtlslist);

				} else {
					sijsStatusFormDtlsDto.setSijsSecondaryDtlsDto(sijsSecondaryDtlslist);
				}

				// Call CLSC1CD to get the Legal information
				SijsLegalDtlsDto sijsLegalDtlsDto = assignWorkloadDao.getSijsLegalDtls(sijsDtlsDto.getIdPerson());
				sijsStatusFormDtlsDto.setSijsLegalDtlsDto(sijsLegalDtlsDto);

				// Call CSES31D to get the Person Information
				PersonDtlDto personDtlDto = personDao.searchPersonDtlById(sijsDtlsDto.getIdPerson());
				sijsStatusFormDtlsDto.setPersonDtlDto(personDtlDto);

				// Call CLSS1AD TO get event Id's for the Contact Type
				List<SijsEventIdDtlsDto> sijsEventIdDtlslist = null;
				sijsEventIdDtlslist = assignWorkloadDao.getSijsEventIdDtls(sijsDtlsDto.getIdStage(),
						ServiceConstants.CONTACT_STAGE, ServiceConstants.CONTACT_PURPOSE);
				sijsStatusFormDtlsDto.setSijsEventIdDtlsDto(sijsEventIdDtlslist);

				// Call CLSS1AD TO get event Id's for the Contact Type
				List<SijsEventContactDtlsDto> sijsEventContactDtlslist = null;
				sijsEventContactDtlslist = assignWorkloadDao.getSijsEventContactDtls(sijsDtlsDto.getIdStage(),
						ServiceConstants.CONTACT_STAGE, ServiceConstants.CONTACT_PURPOSE);
				sijsStatusFormDtlsDto.setSijsEventContactDtlsDto(sijsEventContactDtlslist);

				SijsStatusFormDtlslist.add(sijsStatusFormDtlsDto);

			}

		}

		sijsStatusFormDto.setSijsStatusFormDtlsDto(SijsStatusFormDtlslist);

		return sijsStatusFormPrefillData.returnPrefillData(sijsStatusFormDto);
	}

	/**
	 * Method Description: This method is used to get the boolean to display the
	 * Select Options for SIJS Status form SIJS Status form by passing IdPerson
	 *
	 *
	 * @param idPerson
	 * @return Boolean
	 */

	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })

	public boolean getSijsStatus(Long idPerson) {

		boolean displayStatus = true;

		GetSijsStatusFormDto getSijsStatusFormDto = new GetSijsStatusFormDto();

		getSijsStatusFormDto = assignWorkloadDao.getSijsStatus(idPerson);

		if(!ObjectUtils.isEmpty(getSijsStatusFormDto) && ServiceConstants.ZERO.equals(getSijsStatusFormDto.getIdWkldStage())) {
			displayStatus = false;
		}

		return displayStatus;

	}

	/**
	 * Method Description: This method is used to get the count of Tasks with
	 * the Task Code 2560
	 *
	 * @param idStage
	 * @return Boolean
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean getToDoCount(Long idStage) {
		Boolean todoCount = todoDao.isTodoCCL(idStage);
		log.info("TransactionId :" + idStage);
		return todoCount;
	}

	/**
	 * Method Description: This method is used to save the incomplete CCL
	 * Rejection check box
	 *
	 * @param rejectApprovalDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void saveRejectionApproval(RejectApprovalDto rejectApprovalDto) {
		approvalRejectionDao.saveRejectionApproval(rejectApprovalDto);
		log.info("TransactionId :" + rejectApprovalDto);

	}

	/**
	 *
	 * Method Name: assignSecondary Method Description: Assign secondary worker
	 *
	 * @param idCase
	 * @param idStage
	 * @param idPerson
	 * @param idUser
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AssignSaveGroupRes assignSecondary(Long idCase, Long idStage, Long idPerson, Long idUser) {
		AssignSaveGroupRes response = new AssignSaveGroupRes();
		// Check if the stage already has the person as primary or secondary staff. If so than don't
		// create the new Secondary assignment to same person.
		List<StagePersonLinkDto> stagePersonList = stagePersonLinkDao.getStagePersonLinkByIdStage(idStage);
		if (!ObjectUtils.isEmpty(stagePersonList) && stagePersonList.stream().anyMatch(
				p -> CodesConstant.CPRSNALL_STF.equals(p.getCdStagePersType()) && idPerson.equals(p.getIdPerson()))) {
			// if the stage has already been assigned to the worker, no need
			// assign again.
			return response;
		}
		String reqFuncCode = ServiceConstants.REQ_IND_AUD_ADD;
		StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
		stagePersonLinkDto.setCdStagePersType(ServiceConstants.STG_PERSONLINK_STAFF);
		stagePersonLinkDto.setCdStagePersSearchInd(ServiceConstants.STR_ZERO_VAL);
		stagePersonLinkDto.setStagePersNotes(ServiceConstants.EMPTY_STRING);
		stagePersonLinkDto.setDtStagePersLink(new Date());
		stagePersonLinkDto.setCdStagePersRelInt(ServiceConstants.EMPTY_STRING);
		stagePersonLinkDto.setIndStagePersReporter(ServiceConstants.STR_ZERO_VAL);
		stagePersonLinkDto.setIndStagePersInLaw(ServiceConstants.STR_ZERO_VAL);
		stagePersonLinkDto.setIndStagePersEmpNew(ServiceConstants.IND_STGPER_EMPNEW);
		stagePersonLinkDto.setDtLastUpdate(new Date());
		stagePersonLinkDto.setIdStage(idStage);
		stagePersonLinkDto.setIdCase(idCase);
		stagePersonLinkDto.setIdPerson(idPerson);
		stagePersonLinkDto.setCdStagePersRole(CodesConstant.CSTFROLS_SE);
		ServiceReqHeaderDto serviceReqHeaderDtos = new ServiceReqHeaderDto();
		serviceReqHeaderDtos.setReqFuncCd(reqFuncCode);
		String retMsg = stagePersonLinkDao.getStagePersonLinkAUD(stagePersonLinkDto, serviceReqHeaderDtos);
		log.info("Output assignSecondary Message " + retMsg);
		ServiceReqHeaderDto todoArchIpDto = new ServiceReqHeaderDto();
		postEvent(idPerson, idUser, idStage, ServiceConstants.REQ_IND_AUD_ADD);
		TodoDto todoDtoDtls = setValuesToTodoDtoAssignForAdd(idCase, idUser, idStage, idPerson);
		todoArchIpDto.setReqFuncCd(todoDtoDtls.getReqFuncCd());
		TodoDto todoDto = todoDao.todoAUD(todoDtoDtls, todoArchIpDto);
		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodo())) {
			String saveStatus = ServiceConstants.SUCCESS;
			response.setSaveStatus(saveStatus);
			response.setIdToDo(todoDto.getIdTodo());
		}
		return response;

	}

	/**
	 *
	 * Method Name: postEvent Method Description: Post Event for assignSecondary
	 *
	 * @param idPerson
	 * @param idUser
	 * @param idStage
	 * @param action
	 * @return
	 */
	public PostEventOPDto postEvent(Long idPerson, Long idUser, Long idStage, String action) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		PostEventDto postEventDto = new PostEventDto();
		List<PostEventDto> postEventDtoList = new ArrayList<>();
		ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
		Date dtlastUpdate = new Date();
		serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		postEventIPDto.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
		postEventIPDto.setCdEventType(ServiceConstants.EVENTSTATUS_ASSIGNMENT);
		postEventIPDto.setIdPerson(idUser);
		postEventDto.setIdPerson(idPerson);
		Employee employee = employeeDao.getEmployeeByEmployeeId(idPerson);
		String username = FormattingHelper.formatFullName(employee.getNmEmployeeFirst(), employee.getNmEmployeeMiddle(),
				employee.getNmEmployeeLast());
		postEventIPDto.setIdStage(idStage);
		postEventIPDto.setDtEventOccurred(dtlastUpdate);
		String txtEvnDescp = "";
		if (null != action && ServiceConstants.REQ_IND_AUD_UPDATE.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_PRIMARY;
		} else if (null != action && ServiceConstants.UNASSIGNED.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_PRI_UNASSIGN;
		} else if (null != action && ServiceConstants.REQ_IND_AUD_ADD.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_SECONDARY;
		} else if (null != action && ServiceConstants.REQ_IND_AUD_DELETE.equals(action)) {
			txtEvnDescp = ServiceConstants.TXT_TODO_DESC_SEC_UNASSIGN;
		}
		String txtEvntDesc = txtEvnDescp.concat(username);
		DateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT);
		String date = dateFormat.format(dtlastUpdate);
		dateFormat.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
		DateFormat timeFormat = new SimpleDateFormat(ServiceConstants.TIME_FORMAT);
		timeFormat.setTimeZone(TimeZone.getTimeZone("CST6CDT"));
		String time = timeFormat.format(dtlastUpdate);
		txtEvntDesc = txtEvntDesc + ServiceConstants.SINGLE_WHITESPACE + date + ServiceConstants.AT + time;
		postEventIPDto.setEventDescr(txtEvntDesc);
		postEventDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
		postEventDtoList.add(postEventDto);
		postEventIPDto.setPostEventDto(postEventDtoList);
		return postEventService.checkPostEventStatus(postEventIPDto, serviceReqHeaderDto);
	}

	/**
	 *
	 * Method Name: setValuesToTodoDtoAssignForAdd Method Description: Create
	 * todoDto for assignSecondary.
	 *
	 * @param idCase
	 * @param idUser
	 * @param idStage
	 * @param idPerson
	 * @return
	 */
	public TodoDto setValuesToTodoDtoAssignForAdd(Long idCase, Long idUser, Long idStage, Long idPerson) {
		TodoDto todoDtoVal = new TodoDto();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		date = cal.getTime();
		todoDtoVal.setCdTodoTask(ServiceConstants.EMPTY_STRING);
		todoDtoVal.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
		todoDtoVal.setDtTodoCompleted(date);
		todoDtoVal.setDtTodoCreated(date);
		todoDtoVal.setDtTodoDue(date);
		todoDtoVal.setIdTodoCase(idCase);
		todoDtoVal.setIdTodoPersCreator(idUser);
		todoDtoVal.setIdTodoStage(idStage);
		todoDtoVal.setTodoLongDesc(ServiceConstants.EMPTY_STRING);
		todoDtoVal.setTodoDesc(ServiceConstants.SECONDARY_ASSIGN);
		todoDtoVal.setIdTodoPersAssigned(idPerson);
		Long idPrimary = approvalDao.getPrimaryWorkerIdForStage(idStage);
		todoDtoVal.setIdTodoPersWorker(idPrimary);
		todoDtoVal.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
		return todoDtoVal;
	}

	/**
	 *
	 * Method Name: sendEmail Method Description:This method is used to send the
	 * email appointment.
	 *
	 * @param idEventList
	 * @param hostName
	 */
	public void sendEmail(TreeMap<Long, String> idEventMap, String hostName) {
		idEventMap.forEach((idEvent, cdStage) -> {
			CapsEmailDto capsEmailDto = familyPlanDao.getFamilyPlanReviewDetail(idEvent, "assign");
			capsEmailDto.setCdStage(cdStage);

			/*
			 * Checking if the next review date is not empty and is after todays
			 * date, also checking for the id Participant as for legacy stages
			 * it will be null. and we need to send the appointment for the New
			 * Stages only.
			 */
			if (!ObjectUtils.isEmpty(capsEmailDto.getDtNxtReviewDate())
					&& capsEmailDto.getDtNxtReviewDate().after(new Date())
					&& !ObjectUtils.isEmpty(capsEmailDto.getNmParticipant())) {
				capsCaseService.sendEmployeeEmail(idEvent, capsEmailDto, hostName);
			}
		});
	}

	/**
	 * Method Name: createCalendarEventForReassignment Method Description:This
	 * method is used to create calendar events and remainder for creating
	 * initial FSNA for FRE and FSU stages.
	 *
	 * @param stageDetail
	 * @param assignSaveGroupDto
	 * @param hostName
	 */
	@Override
	public void createCalendarEventForReassignment(Long idStage, Long idPerson, String hostName, String indCurrentStage,
												   String indReassignment) {
		SelectStageDto selectStageDto = new SelectStageDto();
		List<String> emailAddressList = new ArrayList<String>();
		Long idPriorStage = idStage;
		SelectStageDto selectStageDtoCurrent = caseSummaryDao.getStage(idStage, ServiceConstants.CURRENT);

		if(ServiceConstants.INV_Stage.equalsIgnoreCase(selectStageDtoCurrent.getCdStage())){
			indCurrentStage = ServiceConstants.N;
		}
		if (ServiceConstants.YES.equals(indCurrentStage)) {
			idPriorStage = getPriorStage(idStage);
			selectStageDto = selectStageDtoCurrent;
		} else {
			selectStageDto = caseSummaryDao.getLaterFSUStage(idStage);
		}
		//Defect 14866, To not display error page to the end user on post request fail
		if(ObjectUtils.isEmpty(selectStageDto)){
			throw new DataNotFoundException(messageSource.getMessage("conservatorship.nofsustage", null, Locale.US));
		}
		// Get the removal date in the earliest conservatorship removal in that
		// stage
		Date dtRemoval = cnsrvtrshpRemovalDao.getRmvlDtForEarliestEvent(idPriorStage);
		// Check if there is any initial FSNA in the FSU and FRE stage.
		Long idEvent = null;
		if(!ObjectUtils.isEmpty(selectStageDto)){
			idEvent = fsnaDao.getMostRecentAprvInitialFsna(selectStageDto.getIdStage());
		}
		// Converting date to local date for simple date manipulations
		//Added the null condition for dtRemoval - Warranty defect 12162
		LocalDate localDtRemoval = null;
		if (!ObjectUtils.isEmpty(dtRemoval)) {
			localDtRemoval = dtRemoval.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			// Add 21 days to the removal date.
			localDtRemoval = localDtRemoval.plusDays(21);
		}

		/*
		 * check if new date (removal date +21) is after current date and there
		 * is no approved initial FSNA for the stage.
		 */
		//Added the null condition for localDtRemoval - Warranty defect 12162
		if (!ObjectUtils.isEmpty(localDtRemoval) && localDtRemoval.isAfter(LocalDate.now()) && ObjectUtils.isEmpty(idEvent)
				&& !ObjectUtils.isEmpty(selectStageDto)) {
			List<String> emailAndPasswordList = outlookUtil.getImpactEmailAndPassword();
			ExchangeService exchangeService = outlookUtil.getOutlookExchangeService(emailAndPasswordList.get(0),
					emailAndPasswordList.get(1));
			AppointmentDto appointmentDto = new AppointmentDto();
			String formatDate = DateTimeFormatter.ofPattern(MM_DD_YYYY_FORMAT).format(localDtRemoval);
			// Initial FSNA Due MM/DD/YYYY for Last Name, First Initial.

			appointmentDto
					.setAppointmentSubject("Initial FSNA Due " + formatDate + " for " + selectStageDto.getNmCase());
			appointmentDto.setIndAllDayEvent(Boolean.TRUE);
			String emailAddress = null;
			if (!PROD_HOST_NAME.equalsIgnoreCase(hostName)) {
				emailAddress = emailConfigBundle.getString("AssignCVSRemoval.emailId." + hostName);
				emailAddressList.add(emailAddress);
			} else {
				if (ServiceConstants.YES.equals(indReassignment)) {
					emailAddress = employeeDao.getEmployeeById(idPerson).getEmployeeEmailAddress();
					emailAddressList.add(emailAddress);
				} else {

					// get all the primary and secondary workers for FSU stage.
					emailAddressList = employeeDao.getEmployeeEmailAddressList(selectStageDto.getIdStage());

				}
			}
			appointmentDto.setReceiverEmailAddress(emailAddressList);
			/*
			 * check if the difference between (removal date+21) and current
			 * date is more than 7
			 */
			int numberOfDaysDiff = LocalDate.now().until(localDtRemoval).getDays();
			if (numberOfDaysDiff > 7) {
				Date dueDate = Date.from(localDtRemoval.atTime(0, 0).atZone(ZoneId.systemDefault()).toInstant());
				appointmentDto.setDtStartDate(dueDate);
				appointmentDto.setDtEndDate(dueDate);
				appointmentDto.setRemainderMins(10080);
			} else {
				// Create the calendar event and the remainder for today itself.
				Date dueDate = new Date();
				appointmentDto.setDtStartDate(dueDate);
				appointmentDto.setDtEndDate(dueDate);
				appointmentDto.setRemainderMins(0);
			}
			// Create calendar event and the remainder is sent to (due date -7)
			// days.
			outlookUtil.sendAppointment(appointmentDto, exchangeService);
		}

	}

	/**
	 * Method Name: getPriorStage Method Description:This method is used to get
	 * the prior stage of a FRE stage or FSU in which a conservatorship removal
	 * was done.
	 *
	 * @param idStage
	 *            - The id stage.
	 * @return idPriorStage - The id of the prior stage.
	 */
	private Long getPriorStage(Long idStage) {
		Long idPriorStage = null;
		// Calling the dao impl to get the prior stage details.
		SelectStageDto selectStageDto = caseSummaryDao.getStage(idStage, ServiceConstants.STAGE_PRIOR);
		idPriorStage = selectStageDto.getIdStage();
		// If the prior stage was FSU or SUB , then check the prior stage of
		// that stage
		if (CodesConstant.CSTAGES_FSU.equals(selectStageDto.getCdStage())
				|| CodesConstant.CSTAGES_SUB.equals(selectStageDto.getCdStage())) {
			SelectStageDto stageDto = caseSummaryDao.getStage(idPriorStage, ServiceConstants.STAGE_PRIOR);
			idPriorStage = stageDto.getIdStage();

		}
		// returning the prior stage id.
		return idPriorStage;
	}


	/**
	 *Method Name:	fetchRevisedVictimDetails
	 *Method Description:Fetch the revised victim details
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreDisplayPriorityClosureRes fetchRevisedVictimDetails(PreDisplayPriorityClosureReq preDisplayPriorityClosureReq) {
		PreDisplayPriorityClosureRes preDisplayPriorityClosureRes = new PreDisplayPriorityClosureRes();
		List<IntakeNotfChildDto> victimRevisedList=priorityClosureDao.fetchRevisedVictimDetails(preDisplayPriorityClosureReq.getIdRevisedPerson());
		PriorityClosureDto priorityClosureDto= new PriorityClosureDto();
		priorityClosureDto.setVictimNotificationList(victimRevisedList);
		preDisplayPriorityClosureRes.setPriorityClosureDto(priorityClosureDto);
		return preDisplayPriorityClosureRes;
	}

	/**
	 *Method Name:	isFCLCheck
	 *Method Description:To fetch the notification section only when the program is RCL
	 *@param cdStageProgram
	 *@param stageStartDate
	 *@return
	 */
	private boolean  isFCLCheck(String cdStageProgram,Date stageStartDate,Date relDate){
		//artf128805 : FCL changes :fetch the notification section only when the program is RCL

		return ServiceConstants.LICENSING_RCL.equalsIgnoreCase(cdStageProgram) && relDate.before(stageStartDate);

	}

	/**
	 *Method Name:	saveFCLNotificationDetails
	 *Method Description:To save/notify the changes for the notification section in the Priority Closure page for RCI intakes
	 *@param priorityClosureSaveReq
	 */
	private void saveFCLNotificationDetails(PriorityClosureSaveReq priorityClosureSaveReq){
		//save the details to three tables
		PriorityClosureDto priorityClosureDto= priorityClosureSaveReq.getPriorityClosureDto();
		List<IntakeNotfChildDto>  victimNotificationList=priorityClosureDto.getVictimNotificationList();
		IntakeNotfFacilityDto facilityDetails=priorityClosureDto.getFacilityDetails();
		if(priorityClosureDto.isNotifyClicked()){
			ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
			boolean screenedIn = !CodesConstant.CCPSPRTY_N.equalsIgnoreCase(priorityClosureDto.getCdStageCurrPriority()) ?   true : false;
			//Notify clicked create alerts,event,update notification status and date sent and take snapshot
			if(!ObjectUtils.isEmpty(victimNotificationList)) {
				victimNotificationList.forEach(dto ->{
					Object entity=priorityClosureDao.loadEntity(dto.getIdVictimNotification(),VictimNotification.class);
					VictimNotification victimNotificationEntity=entity!=null ? (VictimNotification)entity : new VictimNotification();

					BeanUtils.copyProperties(dto,victimNotificationEntity,"dtCreated","idCreatedPerson");
					victimNotificationEntity.setIdLastUpdatePerson(priorityClosureDto.getIdPerson());
					victimNotificationEntity.setDtLastUpdate(new Date());
					// setting as hibernate gives error when date is null, anyways this will be set by DB trigger
					if(ObjectUtils.isEmpty(entity)) {
						victimNotificationEntity.setDtCreated(new Date());
						victimNotificationEntity.setIdCreatedPerson(priorityClosureDto.getIdPerson());
					}
					if(dto.getIdSubStage() == null){
						victimNotificationEntity.setDtNotificationSent(null);
						victimNotificationEntity.setCdNotificationStatus(CodesConstant.NOTSTAT_N);
						victimNotificationEntity.setIndRevisedInfo("N");
					}else {
						victimNotificationEntity.setDtNotificationSent(new Date());
						victimNotificationEntity.setCdNotificationStatus(CodesConstant.NOTSTAT_Y);
						victimNotificationEntity.setIndRevisedInfo(dto.getIdWorkerRevised() !=null ? "Y"  : "N");
						// get corrected worker id or the existing system found worker id
						Long idWorker = dto.getIdWorkerRevised() !=null ? dto.getIdWorkerRevised() : dto.getIdWorkerPerson();
						Long idSupervisor = dto.getIdSupervisorRevised() !=null ? dto.getIdSupervisorRevised() : dto.getIdSupervisor();
						Long idChild = dto.getIdPersonRevised() !=null ? dto.getIdPersonRevised() : dto.getIdPerson();
						String nmPersonFull = !StringUtils.isBlank(dto.getNmPersonFullRevised()) ? dto.getNmPersonFullRevised() :dto.getNmPersonFull();
						// Create Victim Event
						Long idEvent= createFCLNotificationEvent(dto.getIdStage(),dto.getIdSubStage(),priorityClosureDto.getIdPerson(),idChild,dto.getIdCase(),priorityClosureDto.getIdCase(),screenedIn, true);
						//primary worker victim alert
						todoDao.todoAUD(populateToDo(dto.getIdCase(),priorityClosureDto.getIdCase(), idWorker,dto.getIdStage(), dto.getIdSubStage(),screenedIn, true,nmPersonFull,idEvent), serviceReqHeaderDto);
						//Supervisor victim alert
						todoDao.todoAUD(populateToDo(dto.getIdCase(),priorityClosureDto.getIdCase(), idSupervisor,dto.getIdStage(),dto.getIdSubStage(),screenedIn, true,nmPersonFull,idEvent), serviceReqHeaderDto);
						priorityClosureDao.updateRciIndicator(dto.getIdSubStage());
						victimNotificationEntity.setIdEvent(idEvent);
					}
					priorityClosureDao.saveVictimNotificationDetails(victimNotificationEntity);

					// Look for a ADO for each shild, and create alerts if needed.
					// getStagePersonFilterByAdoStage return sparsely populated object with only idStage, idCase, idPerson, nmPersonFull, and cdLegalStatStatus
					IntakeNotfChildDto stageCaseAndPersonOfAdoStage = stageDao.getStagePersonFilterByAdoStage(dto.getIdPerson());
					if(!Objects.isNull(stageCaseAndPersonOfAdoStage)) {
						Long logonUserId = new Long(priorityClosureDto.getIdPerson());
						VictimNotification victimNotificationAdo = new VictimNotification();
						victimNotificationAdo.setDtNotificationSent(new Date());
						victimNotificationAdo.setCdNotificationStatus(CodesConstant.NOTSTAT_Y);

						victimNotificationAdo.setIdPerson(dto.getIdPerson());

						victimNotificationAdo.setNmPersonFull(stageCaseAndPersonOfAdoStage.getNmPersonFull());
						victimNotificationAdo.setCdLegalStatStatus(stageCaseAndPersonOfAdoStage.getCdLegalStatStatus());
						// skip the *revised fields
						victimNotificationAdo.setIndRevisedInfo("N");

						victimNotificationAdo.setDtCreated(dto.getDtLastUpdate());
						victimNotificationAdo.setDtLastUpdate(dto.getDtLastUpdate());
						victimNotificationAdo.setIdStage(dto.getIdStage());
						victimNotificationAdo.setIdSubStage(stageCaseAndPersonOfAdoStage.getIdStage());
						victimNotificationAdo.setIdLastUpdatePerson(logonUserId);
						victimNotificationAdo.setIdCreatedPerson(logonUserId);

						// get worker and supervisor info
						IntakeNotfChildDto workerAndSupervisorForAdoStage = stageDao.getStagePersonFilterByPersTypeAndRole(stageCaseAndPersonOfAdoStage.getIdStage());
						victimNotificationAdo.setIdWorkerPerson(workerAndSupervisorForAdoStage.getIdWorkerPerson());
						victimNotificationAdo.setNmWorkerPerson(workerAndSupervisorForAdoStage.getNmWorkerPerson());
						victimNotificationAdo.setIdSupervisor(workerAndSupervisorForAdoStage.getIdSupervisor());
						victimNotificationAdo.setNmSupervisor(workerAndSupervisorForAdoStage.getNmSupervisor());

						Long idWorker = workerAndSupervisorForAdoStage.getIdWorkerPerson();
						Long idChild = stageCaseAndPersonOfAdoStage.getIdPerson();
						String nmPersonFull = stageCaseAndPersonOfAdoStage.getNmPersonFull();
						Long idSupervisor = workerAndSupervisorForAdoStage.getIdSupervisor();
						// Create Victim Event
						Long idEvent= createFCLNotificationEvent(dto.getIdStage(),stageCaseAndPersonOfAdoStage.getIdStage(),idWorker,
								idChild, stageCaseAndPersonOfAdoStage.getIdCase(), priorityClosureDto.getIdCase(), screenedIn, true);
						victimNotificationAdo.setIdEvent(idEvent);
						//primary worker victim alert
						todoDao.todoAUD(populateToDo(stageCaseAndPersonOfAdoStage.getIdCase(),priorityClosureDto.getIdCase(), idWorker,
								dto.getIdStage(), stageCaseAndPersonOfAdoStage.getIdStage(), screenedIn, true,nmPersonFull,idEvent), serviceReqHeaderDto);
						//Supervisor victim alert
						todoDao.todoAUD(populateToDo(stageCaseAndPersonOfAdoStage.getIdCase(),priorityClosureDto.getIdCase(), idSupervisor,
								dto.getIdStage(), stageCaseAndPersonOfAdoStage.getIdStage(), screenedIn, true,nmPersonFull,idEvent), serviceReqHeaderDto);
						priorityClosureDao.updateRciIndicator(stageCaseAndPersonOfAdoStage.getIdStage());
						priorityClosureDao.saveVictimNotificationDetails(victimNotificationAdo);
					}
				});
			}

			List<Long> victimIdList=victimNotificationList.stream().map(dto-> dto.getIdPersonRevised() !=null ? dto.getIdPersonRevised() : dto.getIdPerson()).collect(Collectors.toList());

			// non-victim notifications (other children in the facility)
			if(!ObjectUtils.isEmpty(facilityDetails) && !ObjectUtils.isEmpty(facilityDetails.getFacilityDetail()) && !ObjectUtils.isEmpty(facilityDetails.getFacilityDetail().getIdResource()) ){
				//check rsrc belongs to facility that require notification
				boolean sendNotificationToFacility=isNotificationNeededForFacility(facilityDetails.getFacilityDetail().getCdRsrcFacilType(),facilityDetails.getFacilitiesToBeNotifiedMap());
				Object entity=priorityClosureDao.loadEntity(facilityDetails.getFacilityDetail().getIdVictimNotificationRsrc(),VictimNotificationRsrc.class);
				VictimNotificationRsrc  victimNotificationRsrcEntity= entity!=null ? (VictimNotificationRsrc)entity : new  VictimNotificationRsrc();
				BeanUtils.copyProperties(facilityDetails.getFacilityDetail(),victimNotificationRsrcEntity,"dtCreated","idCreatedPerson");

				victimNotificationRsrcEntity.setIdStage(priorityClosureDto.getIdStage());
				victimNotificationRsrcEntity.setIdLastUpdatePerson(priorityClosureDto.getIdPerson());
				victimNotificationRsrcEntity.setDtLastUpdate(new Date());
				// setting as hibernate gives error when date is null, anyways this will be set by DB trigger
				if(ObjectUtils.isEmpty(entity)) {
					victimNotificationRsrcEntity.setDtCreated(new Date());
					victimNotificationRsrcEntity.setIdCreatedPerson(priorityClosureDto.getIdPerson());
				}
				Set<VctmNotifctnRsrcChild> savedVctmNotifctnRsrcChilds = entity != null ? ((VictimNotificationRsrc) entity).getVctmNotifctnRsrcChilds() : null;

				Set<VctmNotifctnRsrcChild> vctmNotifctnRsrcChilds =new HashSet<>();
				if(!ObjectUtils.isEmpty(facilityDetails.getChildrenInFacilityList())) {
					List<Long> vctmNotifctnRsrcChildIds =new ArrayList<Long>();
					facilityDetails.getChildrenInFacilityList().forEach(childrenDto->{
						VctmNotifctnRsrcChild childrenEntity = null;
						// setting as hibernate gives error when date is null, anyways this will be set by DB trigger
						if(ObjectUtils.isEmpty(savedVctmNotifctnRsrcChilds)) {
							childrenEntity=new VctmNotifctnRsrcChild();
							childrenEntity.setDtCreated(new Date());
							childrenEntity.setIdCreatedPerson(priorityClosureDto.getIdPerson());
						}else {
							Optional<VctmNotifctnRsrcChild> savedChildEntity=savedVctmNotifctnRsrcChilds.stream().filter(childEntity-> childEntity.getIdVctmNotifctnRsrcChild().equals(childrenDto.getIdVctmNotifctnRsrcChild())).findFirst();
							if(savedChildEntity.isPresent()) {
								childrenEntity=savedChildEntity.get();
								vctmNotifctnRsrcChildIds.add(childrenEntity.getIdVctmNotifctnRsrcChild());
							}else {
								childrenEntity=new VctmNotifctnRsrcChild();
								childrenEntity.setDtCreated(new Date());
								childrenEntity.setIdCreatedPerson(priorityClosureDto.getIdPerson());
							}
						}
						BeanUtils.copyProperties(childrenDto,childrenEntity,"dtCreated","idCreatedPerson");
						childrenEntity.setIdLastUpdatePerson(priorityClosureDto.getIdPerson());
						childrenEntity.setDtLastUpdate(new Date());
						childrenEntity.setIdSubStage(childrenDto.getIdSubStage());
						// Artifact: artf150949: ALM ID: 15013 - Set the Non-Victim Notification Status as 'N' when there is no open SUB stage available for child.
						if(!sendNotificationToFacility || ObjectUtils.isEmpty(childrenDto.getIdSubStage())){
							childrenEntity.setDtNotificationSent(null);
							childrenEntity.setCdNotificationStatus(CodesConstant.NOTSTAT_N);
						}else if(victimIdList.contains(childrenDto.getIdPerson())){
							childrenEntity.setDtNotificationSent(null);
							childrenEntity.setCdNotificationStatus(CodesConstant.NOTSTAT_NA);
						}else  {
							childrenEntity.setDtNotificationSent(new Date());
							childrenEntity.setCdNotificationStatus(CodesConstant.NOTSTAT_Y);
							//create non victim event
							Long idEvent= createFCLNotificationEvent(childrenDto.getIdStage(),childrenDto.getIdSubStage(),priorityClosureDto.getIdPerson(),childrenDto.getIdPerson(),childrenDto.getIdCase(),priorityClosureDto.getIdCase(),screenedIn, false);
							//primary worker non victim alert
							todoDao.todoAUD(populateToDo(childrenDto.getIdCase(),priorityClosureDto.getIdCase(), childrenDto.getIdWorkerPerson(),childrenDto.getIdStage(),childrenDto.getIdSubStage(),screenedIn, false,childrenDto.getNmPersonFull(),idEvent), serviceReqHeaderDto);
							//Supervisor non victim alert
							todoDao.todoAUD(populateToDo(childrenDto.getIdCase(),priorityClosureDto.getIdCase(), childrenDto.getIdSupervisor(),childrenDto.getIdStage(),childrenDto.getIdSubStage(),screenedIn, false,childrenDto.getNmPersonFull(),idEvent), serviceReqHeaderDto);
							priorityClosureDao.updateRciIndicator(childrenDto.getIdSubStage());
						}

						childrenEntity.setVictimNotificationRsrc(victimNotificationRsrcEntity);
						vctmNotifctnRsrcChilds.add(childrenEntity);

					});

					// Changes done to fix PROD defect 14173 (PPM#60465). The root cause of the
					// defect was that screener had earlier saved a set of children id’s in the
					// VICTIM_NOTIFICATION_RSRC_CHILD table (Note: We save only the children id’s in case
					// of save to pull the latest details till notify) .After few days when screener
					// loads the page , almost all the children in the facility have changed and are
					// different from what is present in the VICTIM_NOTIFICATION_RSRC_CHILD table. Due to
					// this  when we save and notify, new children id’s gets
					// saved as expected but the old children person id’s still exist in the table
					// and after notify they are visible in the front end with only their person
					// id’s .Hence the code below will delete them.
					if (!ObjectUtils.isEmpty(savedVctmNotifctnRsrcChilds)) {
						List<VctmNotifctnRsrcChild> savedVctmNotifctnRsrcChildsList = savedVctmNotifctnRsrcChilds.stream()
								.filter(childEntity -> !vctmNotifctnRsrcChildIds
										.contains(childEntity.getIdVctmNotifctnRsrcChild()))
								.collect(Collectors.toList());
						if (!ObjectUtils.isEmpty(savedVctmNotifctnRsrcChildsList)) {
							priorityClosureDao.deleteRsrcChildren(savedVctmNotifctnRsrcChildsList);
						}
					}

				}

				victimNotificationRsrcEntity.setVctmNotifctnRsrcChilds(vctmNotifctnRsrcChilds);
				priorityClosureDao.saveFacilityDetails(victimNotificationRsrcEntity);

				// TODO non-victims may need ADO alerts also.
			}
		}else{
			//save clicked , save only the person id's so to fetch other details in real time
			if(!ObjectUtils.isEmpty(victimNotificationList)) {
				victimNotificationList.forEach(dto ->{
					Object entity=priorityClosureDao.loadEntity(dto.getIdVictimNotification(),VictimNotification.class);
					VictimNotification victimNotificationEntity= entity!=null ? (VictimNotification)entity : new VictimNotification();
					victimNotificationEntity.setIdVictimNotification(dto.getIdVictimNotification());
					victimNotificationEntity.setIdPerson(dto.getIdPerson());
					victimNotificationEntity.setNmPersonFull(dto.getNmPersonFull());
					victimNotificationEntity.setIdPersonRevised(dto.getIdPersonRevised());
					victimNotificationEntity.setNmPersonFullRevised(dto.getNmPersonFullRevised());
					// victimNotificationEntity.setIdWorkerRevised(dto.getIdWorkerRevised());
					victimNotificationEntity.setIndRevisedInfo(dto.getIdPersonRevised() !=null ? "Y"  : "N");
					// victimNotificationEntity.setIdSupervisorRevised(dto.getIdSupervisorRevised());
					victimNotificationEntity.setIdStage(priorityClosureDto.getIdStage());
					victimNotificationEntity.setIdSubStage(dto.getIdSubStage());
					victimNotificationEntity.setIdLastUpdatePerson(priorityClosureDto.getIdPerson());
					victimNotificationEntity.setDtLastUpdate(new Date());
					// setting as hibernate gives error when date is null, anyways this will be set by DB trigger
					victimNotificationEntity.setDtCreated(entity!=null ? ((VictimNotification)entity).getDtCreated() : new Date());
					victimNotificationEntity.setIdCreatedPerson(entity!=null ? ((VictimNotification)entity).getIdCreatedPerson() : priorityClosureDto.getIdPerson());
					priorityClosureDao.saveVictimNotificationDetails(victimNotificationEntity);
				});
			}
			if(!ObjectUtils.isEmpty(facilityDetails) && !ObjectUtils.isEmpty(facilityDetails.getFacilityDetail()) && !ObjectUtils.isEmpty(facilityDetails.getFacilityDetail().getIdResource()) ){
				Object entity=priorityClosureDao.loadEntity(facilityDetails.getFacilityDetail().getIdVictimNotificationRsrc(),VictimNotificationRsrc.class);
				VictimNotificationRsrc  victimNotificationRsrcEntity=entity!=null ? (VictimNotificationRsrc)entity : new  VictimNotificationRsrc();
				victimNotificationRsrcEntity.setIdVictimNotificationRsrc(facilityDetails.getFacilityDetail().getIdVictimNotificationRsrc());
				victimNotificationRsrcEntity.setIdResource(facilityDetails.getFacilityDetail().getIdResource());
				victimNotificationRsrcEntity.setIdStage(priorityClosureDto.getIdStage());

				Set<VctmNotifctnRsrcChild> savedVctmNotifctnRsrcChilds = entity != null ? ((VictimNotificationRsrc) entity).getVctmNotifctnRsrcChilds() : null;
				Set<VctmNotifctnRsrcChild> vctmNotifctnRsrcChilds = new HashSet<>();
				if(!ObjectUtils.isEmpty(facilityDetails.getChildrenInFacilityList())) {
					facilityDetails.getChildrenInFacilityList().forEach(childrenDto->{
						VctmNotifctnRsrcChild childrenEntity =null;
						if(ObjectUtils.isEmpty(savedVctmNotifctnRsrcChilds)) {
							childrenEntity=new VctmNotifctnRsrcChild();
						}else {
							childrenEntity=savedVctmNotifctnRsrcChilds.stream().filter(childEntity-> childEntity.getIdVctmNotifctnRsrcChild().equals(childrenDto.getIdVctmNotifctnRsrcChild())).findFirst().orElse(new VctmNotifctnRsrcChild());
						}

						childrenEntity.setIdVctmNotifctnRsrcChild(childrenDto.getIdVctmNotifctnRsrcChild());
						childrenEntity.setIdPerson(childrenDto.getIdPerson());
						childrenEntity.setIdSubStage(childrenDto.getIdSubStage());
						childrenEntity.setVictimNotificationRsrc(victimNotificationRsrcEntity);
						childrenEntity.setIdLastUpdatePerson(priorityClosureDto.getIdPerson());
						childrenEntity.setDtLastUpdate(new Date());
						childrenEntity.setIdCreatedPerson(childrenEntity.getIdCreatedPerson() !=null ? childrenEntity.getIdCreatedPerson() : priorityClosureDto.getIdPerson());
						// setting as hibernate gives error when date is null, anyways this will be set by DB trigger
						childrenEntity.setDtCreated(childrenEntity.getDtCreated() !=null ? childrenEntity.getDtCreated() :new Date());
						vctmNotifctnRsrcChilds.add(childrenEntity);
					});
				}
				victimNotificationRsrcEntity.setVctmNotifctnRsrcChilds(vctmNotifctnRsrcChilds);
				// setting as hibernate gives error when date is null, anyways this will be set by DB trigger
				victimNotificationRsrcEntity.setIdLastUpdatePerson(priorityClosureDto.getIdPerson());
				victimNotificationRsrcEntity.setDtLastUpdate(new Date());
				victimNotificationRsrcEntity.setIdCreatedPerson(entity!=null ? ((VictimNotificationRsrc)entity).getIdCreatedPerson() : priorityClosureDto.getIdPerson());
				victimNotificationRsrcEntity.setDtCreated(entity!=null ? ((VictimNotificationRsrc)entity).getDtCreated(): new Date());

				priorityClosureDao.saveFacilityDetails(victimNotificationRsrcEntity);
			}
		}

	}

	/**
	 *Method Name:	populateToDo
	 *Method Description:Populate the FCL notification TO DO for task code 9907
	 *@param subStageCaseId
	 *@param intakeCaseId
	 *@param idToDoAssigned
	 *@param idStage
	 *@param screenedIn
	 *@param victim
	 *@param nmPersonFull
	 *@param idEvent
	 *@return
	 */
	private TodoDto populateToDo(Long subStageCaseId,Long intakeCaseId, Long idToDoAssigned,Long idStage,Long idSubStage,boolean screenedIn, boolean victim,String nmPersonFull,Long idEvent) {

		TodoDto todoDto= new TodoDto();
		todoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
		todoDto.setCdTodoTask(ServiceConstants.RCL_ALERT_TASK_CODE);
		todoDto.setDtTodoCompleted(new Date());
		todoDto.setDtTodoCreated(new Date());
		todoDto.setDtTodoDue(new Date());
		todoDto.setIdTodoCase(subStageCaseId);
		todoDto.setIdTodoPersCreator(null);
		todoDto.setIdTodoPersAssigned(idToDoAssigned);
		todoDto.setIdTodoStage(idSubStage);
		todoDto.setIdTodoEvent(idEvent);
		String stageName = idStage != null ? stageDao.getStageEntityById(idStage).getNmStage() : "";
		if(victim && screenedIn){
			todoDto.setTodoDesc(String.format(ServiceConstants.VICTIM_SCRNIN_ALERT_SHORT,nmPersonFull,intakeCaseId, stageName));
			todoDto.setTodoLongDesc(String.format(ServiceConstants.VICTIM_SCRNIN_ALERT_LONG,intakeCaseId, stageName));
		}else if(victim && !screenedIn) {
			todoDto.setTodoDesc(String.format(ServiceConstants.VICTIM_PN_ALERT_SHORT,nmPersonFull,intakeCaseId, stageName));
			todoDto.setTodoLongDesc(ServiceConstants.VICTIM_PN_ALERT_LONG);
		}else if(!victim && screenedIn) {
			todoDto.setTodoDesc(String.format(ServiceConstants.NON_VICTIM_SCRNIN_ALERT_SHORT,nmPersonFull,intakeCaseId, stageName));
			todoDto.setTodoLongDesc(String.format(ServiceConstants.NON_VICTIM_SCRNIN_ALERT_LONG,intakeCaseId, stageName));
		}else if (!victim && !screenedIn){
			todoDto.setTodoDesc(String.format(ServiceConstants.NON_VICTIM_PN_ALERT_SHORT,nmPersonFull,intakeCaseId, stageName));
			todoDto.setTodoLongDesc(ServiceConstants.VICTIM_PN_ALERT_LONG);
		}

		return todoDto;
	}

	/**
	 *Method Name:	createFCLNotificationEvent
	 *Method Description:To create events for FCL notifications for  the child's sub stage
	 *@param idStage
	 *@param idRclScreener
	 *@param idchild
	 *@param subStageCaseID
	 *@param intakeCaseId
	 *@param screenedIn
	 *@param victim
	 *@return
	 */
	private Long createFCLNotificationEvent(Long idStage,Long idSubStage,Long idRclScreener,Long idchild,Long subStageCaseID,Long intakeCaseId,boolean screenedIn, boolean victim){
		PostEventReq postEventReq = new PostEventReq();
		PostEventRes postEventRes = new PostEventRes();
		postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventReq.setDtDtEventOccurred(new Date());
		postEventReq.setTsLastUpdate(new Date());
		postEventReq.setSzCdEventType(CodesConstant.CEVNTTYP_NOT);
		postEventReq.setSzCdEventStatus(ServiceConstants.EVENT_STAT_COMP);
		postEventReq.setUlIdStage(idSubStage == null ? idStage : idSubStage);
		postEventReq.setUlIdPerson(idRclScreener);
		postEventReq.setUlIdCase(subStageCaseID);
		if(screenedIn){
			postEventReq.setSzTxtEventDescr(String.format(ServiceConstants.EVNT_SCRN_VICTIM_DESC,intakeCaseId, idStage != null ? stageDao.getStageEntityById(idStage).getNmStage() : ""));
		}else{
			postEventReq.setSzTxtEventDescr(String.format(ServiceConstants.EVNT_PN_VICTIM_DESC,intakeCaseId, idStage != null ? stageDao.getStageEntityById(idStage).getNmStage() : ""));
		}
		postEventRes = eventService.postEvent(postEventReq);
		return postEventRes.getUlIdEvent();
	}

	/**
	 *Method Name:	isNotificationNeededForFacility
	 *Method Description:Static list to fetch the facility id to be notified
	 *@param facilityType
	 *@return
	 */
	private boolean isNotificationNeededForFacility(String facilityType,Map<String,CodeAttributes> facilityMap){

		return StringUtils.isBlank(facilityType) ?  false : facilityMap.containsKey(facilityType);
	}


	/**
	 *Method Name:	fetchRevisedChildrenInFacility
	 *Method Description:This method is called to update the children in facility  details after the resource search in priority closure page for RCI intake
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED,  rollbackFor = {
			Exception.class })
	public PreDisplayPriorityClosureRes fetchRevisedChildrenInFacility(PreDisplayPriorityClosureReq preDisplayPriorityClosureReq) {
		PreDisplayPriorityClosureRes preDisplayPriorityClosureRes = new PreDisplayPriorityClosureRes();
		PriorityClosureDto priorityClosureDto=new PriorityClosureDto();
		IntakeNotfFacilityDto facilityDto=new IntakeNotfFacilityDto();
		// call to fetch the facility id details and the children
		Set<VctmNotifctnRsrcChild> vctmNotifctnRsrcChilds = new HashSet<>();
		List<IntakeNotfChildDto> defaultFacilityChildrenDetailsList=priorityClosureDao.getDefaultFacilityChildrenDetails(preDisplayPriorityClosureReq.getIdResource());
		if(null!=preDisplayPriorityClosureReq.getIdVictimNotificationRsrc()) {
			List<IntakeNotfChildDto> savedFacilityChildrenDetailsList=priorityClosureDao.getNotifiedFacilityChildrenDetails(preDisplayPriorityClosureReq.getIdVictimNotificationRsrc());
			if(ObjectUtils.isEmpty(defaultFacilityChildrenDetailsList) && !ObjectUtils.isEmpty(savedFacilityChildrenDetailsList)) {
				//new rsrc id detail are empty and old rsrc details need deletion
				priorityClosureDao.deleteChildrenDetails(preDisplayPriorityClosureReq.getIdVictimNotificationRsrc());
			}else if(!ObjectUtils.isEmpty(defaultFacilityChildrenDetailsList) && ObjectUtils.isEmpty(savedFacilityChildrenDetailsList)) {
				//new rsrc id detail are not empty and old rsrc details are empty, so need  to be saved with the default list
				vctmNotifctnRsrcChilds=prepareVctmNotifctnRsrcChildEntity(vctmNotifctnRsrcChilds, preDisplayPriorityClosureReq, defaultFacilityChildrenDetailsList);
			}else {
				//scenario when the saved list is different from the new list of children for new mapped resource
				List<Long> savedChildrenIdList= savedFacilityChildrenDetailsList.stream().map(dto->dto.getIdPerson()).collect(Collectors.toList());
				List<Long> defaultChildrenIdList= defaultFacilityChildrenDetailsList.stream().map(dto->dto.getIdPerson()).collect(Collectors.toList());
				if(!savedChildrenIdList.containsAll(defaultChildrenIdList)) {
					priorityClosureDao.deleteChildrenDetails(preDisplayPriorityClosureReq.getIdVictimNotificationRsrc());
					vctmNotifctnRsrcChilds=prepareVctmNotifctnRsrcChildEntity(vctmNotifctnRsrcChilds, preDisplayPriorityClosureReq, defaultFacilityChildrenDetailsList);
				}
			}

			VictimNotificationRsrc  victimNotificationRsrcEntity =(VictimNotificationRsrc) priorityClosureDao.loadEntity(preDisplayPriorityClosureReq.getIdVictimNotificationRsrc(),VictimNotificationRsrc.class);
			victimNotificationRsrcEntity.setIdResource(preDisplayPriorityClosureReq.getIdResource());
			victimNotificationRsrcEntity.setVctmNotifctnRsrcChilds(vctmNotifctnRsrcChilds);
			priorityClosureDao.saveFacilityDetails(victimNotificationRsrcEntity);
		}else {
			facilityDto.setChildrenInFacilityList(defaultFacilityChildrenDetailsList);
		}
		priorityClosureDto.setFacilityDetails(facilityDto);
		preDisplayPriorityClosureRes.setPriorityClosureDto(priorityClosureDto);

		return preDisplayPriorityClosureRes;
	}

	/**
	 *Method Name:	fetchRevisedChildrenInFacility
	 *Method Description:This method is called to get the children in facility  details
	 * after the LicenseINfo validation in priority closure page for RCI intake
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED,  rollbackFor = {
			Exception.class })
	public PreDisplayPriorityClosureRes getRevisedChildrenInFacility(PreDisplayPriorityClosureReq preDisplayPriorityClosureReq) {
		PreDisplayPriorityClosureRes preDisplayPriorityClosureRes = new PreDisplayPriorityClosureRes();
		PriorityClosureDto priorityClosureDto=new PriorityClosureDto();
		IntakeNotfFacilityDto facilityDto=new IntakeNotfFacilityDto();
		// call to fetch the facility id details and the children
		List<IntakeNotfChildDto> defaultFacilityChildrenDetailsList=priorityClosureDao.getDefaultFacilityChildrenDetails(preDisplayPriorityClosureReq.getIdResource());
		facilityDto.setChildrenInFacilityList(defaultFacilityChildrenDetailsList);
		priorityClosureDto.setFacilityDetails(facilityDto);
		preDisplayPriorityClosureRes.setPriorityClosureDto(priorityClosureDto);

		return preDisplayPriorityClosureRes;
	}

	private Set<VctmNotifctnRsrcChild> prepareVctmNotifctnRsrcChildEntity(Set<VctmNotifctnRsrcChild> vctmNotifctnRsrcChilds, PreDisplayPriorityClosureReq preDisplayPriorityClosureReq, List<IntakeNotfChildDto> defaultFacilityChildrenDetailsList){
		VictimNotificationRsrc  victimNotificationRsrcEntity=new  VictimNotificationRsrc();
		victimNotificationRsrcEntity.setIdVictimNotificationRsrc(preDisplayPriorityClosureReq.getIdVictimNotificationRsrc());
		defaultFacilityChildrenDetailsList.stream().forEach(childrenDto->{
			VctmNotifctnRsrcChild childrenEntity = new VctmNotifctnRsrcChild();
			BeanUtils.copyProperties(childrenDto,childrenEntity);
			childrenEntity.setVictimNotificationRsrc(victimNotificationRsrcEntity);
			childrenEntity.setIdLastUpdatePerson(preDisplayPriorityClosureReq.getIdUser());
			childrenEntity.setIdCreatedPerson(preDisplayPriorityClosureReq.getIdUser());
			// setting as hibernate gives error when date is null, anyways this will be set by DB trigger
			childrenEntity.setDtCreated(new Date());
			childrenEntity.setDtLastUpdate(new Date());
			vctmNotifctnRsrcChilds.add(childrenEntity);
		});
		return vctmNotifctnRsrcChilds;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<IntakeNotfChildDto> fetchLatestChildrenInFacility(Long  idVictimNotificationRsrc) {

		return priorityClosureDao.getNotifiedFacilityChildrenDetails(idVictimNotificationRsrc);
	}


	@Override
	public List<TodoDto> getRCIAlertTodoDetailsByCaseAndStage(TodoReq todoReq) {

		List<TodoDto> todoServiceOutput = todoDao.getRCIAlertTodoDetails(todoReq);
		log.info("TransactionId :" + todoReq.getTransactionId());
		return todoServiceOutput;

	}

	/**
	 * Creates Workload alerts for SUB and ADO stage case workers and supervisors, if the alert doesn't exists with
	 * priorstage combination
	 * @param stagePersonValueDto
	 * @param priorStage
	 * @param logonUserId
	 * @param screenedIn
	 */
	@Override
	public void processPersonMergeWorkloadAlerts(StagePersonValueDto stagePersonValueDto, SelectStageDto priorStage
			, Long logonUserId, boolean screenedIn) {
		processAlertsForaStage(stagePersonValueDto, priorStage, logonUserId, screenedIn, "SUB");
		processAlertsForaStage(stagePersonValueDto, priorStage, logonUserId, screenedIn, "ADO");
	}

	/**
	 * Checks if the stage type exists for the person and then calls sendWorkloadAlertsForStage for creating alerts.
	 * @param stagePersonValueDto
	 * @param priorStage
	 * @param logonUserId
	 * @param screenedIn
	 * @param cdStage
	 */
	public void processAlertsForaStage(StagePersonValueDto stagePersonValueDto, SelectStageDto priorStage, Long logonUserId
			, boolean screenedIn, String cdStage) {
		List<IntakeNotfChildDto> intakeNotfChildDtos = stageDao.getStagesForPCSelfByStageType(stagePersonValueDto.getIdPerson(), cdStage);
		intakeNotfChildDtos.forEach(intakeNotfChildDto -> {
			sendWorkloadAlertsForStage(intakeNotfChildDto, stagePersonValueDto, logonUserId, priorStage, screenedIn);
		});
	}

	/**
	 * Creates alert for the stage if the alert doesn't exist with priorstage id.
	 * @param intakeNotfChildDto
	 * @param stagePersonValueDto
	 * @param logonUserId
	 * @param priorStage
	 * @param screenedIn
	 */
	public void sendWorkloadAlertsForStage(IntakeNotfChildDto intakeNotfChildDto, StagePersonValueDto stagePersonValueDto,
										   Long logonUserId, SelectStageDto priorStage, boolean screenedIn) {
		// get worker and supervisor info
		IntakeNotfChildDto workerAndSupervisorForStage = stageDao.getStagePersonFilterByPersTypeAndRole(intakeNotfChildDto.getIdStage());

		boolean isAlertExistsForWorker = priorityClosureDao.checkIfAlertAlreadyExists(stagePersonValueDto.getIdPerson(),
				priorStage.getIdStage(), intakeNotfChildDto.getIdStage(), workerAndSupervisorForStage.getIdWorkerPerson(),
				false);
		//Supervisor alerts are not needed, commenting out the code if it is needed in the future
		/*boolean isAlertExistsForSupervisor = priorityClosureDao.checkIfAlertAlreadyExists(stagePersonValueDto.getIdPerson(),
				priorStage.getIdStage(), intakeNotfChildDto.getIdStage(), workerAndSupervisorForStage.getIdSupervisor(),
				true);*/
		if (!isAlertExistsForWorker) {
			ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
			serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);

			VictimNotification victimNotification = new VictimNotification();
			victimNotification.setDtNotificationSent(new Date());
			victimNotification.setCdNotificationStatus(CodesConstant.NOTSTAT_Y);

			victimNotification.setIdPerson(stagePersonValueDto.getIdPerson());

			victimNotification.setNmPersonFull(intakeNotfChildDto.getNmPersonFull());
			victimNotification.setCdLegalStatStatus(intakeNotfChildDto.getCdLegalStatStatus());
			// skip the *revised fields
			victimNotification.setIndRevisedInfo("N");

			victimNotification.setDtCreated(new Date());
			victimNotification.setDtLastUpdate(new Date());
			victimNotification.setIdStage(priorStage.getIdStage());
			victimNotification.setIdSubStage(intakeNotfChildDto.getIdStage());
			victimNotification.setIdLastUpdatePerson(logonUserId);
			victimNotification.setIdCreatedPerson(logonUserId);

			victimNotification.setIdWorkerPerson(workerAndSupervisorForStage.getIdWorkerPerson());
			victimNotification.setNmWorkerPerson(workerAndSupervisorForStage.getNmWorkerPerson());
			victimNotification.setIdSupervisor(workerAndSupervisorForStage.getIdSupervisor());
			victimNotification.setNmSupervisor(workerAndSupervisorForStage.getNmSupervisor());

			Long idWorker = workerAndSupervisorForStage.getIdWorkerPerson();
			Long idChild = intakeNotfChildDto.getIdPerson();
			String nmPersonFull = intakeNotfChildDto.getNmPersonFull();
			//Long idSupervisor = workerAndSupervisorForStage.getIdSupervisor();
			// Create Victim Event
			Long idEvent = createFCLNotificationEvent(stagePersonValueDto.getIdStage(), intakeNotfChildDto.getIdStage(), idWorker,
					idChild, intakeNotfChildDto.getIdCase(), stagePersonValueDto.getIdCase(), screenedIn, true);
			victimNotification.setIdEvent(idEvent);
			//primary worker victim alert
			todoDao.todoAUD(populateToDo(intakeNotfChildDto.getIdCase(), stagePersonValueDto.getIdCase(), idWorker,
					stagePersonValueDto.getIdStage(), intakeNotfChildDto.getIdStage(), screenedIn
					, true, nmPersonFull, idEvent), serviceReqHeaderDto);
			//Supervisor victim alert
			/* if (!isAlertExistsForSupervisor) {
				todoDao.todoAUD(populateToDo(intakeNotfChildDto.getIdCase(), stagePersonValueDto.getIdCase(), idSupervisor,
						stagePersonValueDto.getIdStage(), intakeNotfChildDto.getIdStage(), screenedIn
						, true, nmPersonFull, idEvent), serviceReqHeaderDto);
			}*/
			priorityClosureDao.updateRciIndicator(intakeNotfChildDto.getIdStage());
			priorityClosureDao.saveVictimNotificationDetails(victimNotification);
		}
	}

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public WorkloadResponse getWorkloadHasLoginUserForStageAndCase(Long stageId, Long caseId, Long loginUserId) {
		WorkloadResponse workloadResponse = new WorkloadResponse();
		workloadResponse.setWorkloadHasLoginUserForStageAndCase(
				workLoadDao.getWorkloadHasLoginUserForStageAndCase(stageId, caseId, loginUserId));
		return workloadResponse;
	}
	/**
	 * Service Implemation to communicate with DAO
	 *
	 * @param idUser - logged in user id
	 * @param securityRole - selected security role
	 * @return - return as assigned dto
	 */
	@Override
	public AssignedDto findEMRProgramAdmin(int idUser, String securityRole) {
		AssignedDto assignedDto = workLoadDao.findEMRProgramAdminByUseridAndSecurityRole(idUser, securityRole);
		return assignedDto;
	}


	@Override
	public boolean getExecStaffSecurityForEMR(Long idPerson, String securityRole) {
		return workLoadDao.getExecStaffSecurityForEMR(idPerson, securityRole);
	}

	@Override
	public boolean saveValidatedAddressApi(AddressDetailBean addressDetailBean) {
		return workLoadDao.insertValidatedAddressApi(addressDetailBean);
	}

	@Override
	public AddressDetailBean getValidatedAddressApi(String guid){
		return workLoadDao.getValidatedAddressApi(guid);
	}

}