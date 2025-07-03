package us.tx.state.dfps.service.workload.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.common.web.bean.AddressDetailBean;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ApprovalStatusReq;
import us.tx.state.dfps.service.common.request.AssignSaveGroupReq;
import us.tx.state.dfps.service.common.request.AssignWorkloadReq;
import us.tx.state.dfps.service.common.request.CaseMergeUpdateReq;
import us.tx.state.dfps.service.common.request.CaseTodoReq;
import us.tx.state.dfps.service.common.request.CitizenStatusFormReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.DisplaySijsStatusFormReq;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.InvAlertDueDatesReq;
import us.tx.state.dfps.service.common.request.KinApprovalReq;
import us.tx.state.dfps.service.common.request.MrefStatusFormReq;
import us.tx.state.dfps.service.common.request.OnCallCountyReq;
import us.tx.state.dfps.service.common.request.PreDisplayPriorityClosureReq;
import us.tx.state.dfps.service.common.request.PriorityClosureReq;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.common.request.RejectionApprovalDtoReq;
import us.tx.state.dfps.service.common.request.RetrvCountyReq;
import us.tx.state.dfps.service.common.request.SearchAssignReq;
import us.tx.state.dfps.service.common.request.SearchStageReq;
import us.tx.state.dfps.service.common.request.ToDoStageReq;
import us.tx.state.dfps.service.common.request.ToDoUtilityReq;
import us.tx.state.dfps.service.common.request.TodoListAUDReq;
import us.tx.state.dfps.service.common.request.TodoListDelReq;
import us.tx.state.dfps.service.common.request.TodoListReq;
import us.tx.state.dfps.service.common.request.TodoReq;
import us.tx.state.dfps.service.common.request.TodoUpdateReq;
import us.tx.state.dfps.service.common.response.ARExtensionRes;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.common.response.ApproverStatusRes;
import us.tx.state.dfps.service.common.response.AssignSaveGroupRes;
import us.tx.state.dfps.service.common.response.AssignWorkloadRes;
import us.tx.state.dfps.service.common.response.CaseMergeUpdateRes;
import us.tx.state.dfps.service.common.response.CaseTodoRes;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.DisplaySijsStatusFormRes;
import us.tx.state.dfps.service.common.response.EventDetailRes;
import us.tx.state.dfps.service.common.response.IDListRes;
import us.tx.state.dfps.service.common.response.InvAlertDueDatesRes;
import us.tx.state.dfps.service.common.response.KinApprovalRes;
import us.tx.state.dfps.service.common.response.OnCallCountyRes;
import us.tx.state.dfps.service.common.response.PreDisplayPriorityClosureRes;
import us.tx.state.dfps.service.common.response.PriorityClosureRes;
import us.tx.state.dfps.service.common.response.PriorityClosureSaveRes;
import us.tx.state.dfps.service.common.response.RetrvCountyRes;
import us.tx.state.dfps.service.common.response.SearchAssignRes;
import us.tx.state.dfps.service.common.response.SearchStageRes;
import us.tx.state.dfps.service.common.response.TodoListAUDRes;
import us.tx.state.dfps.service.common.response.TodoListDelRes;
import us.tx.state.dfps.service.common.response.TodoListRes;
import us.tx.state.dfps.service.common.response.TodoRes;
import us.tx.state.dfps.service.common.response.TodoSelectStageRes;
import us.tx.state.dfps.service.common.response.TodoSupervisorRes;
import us.tx.state.dfps.service.common.response.TodoUpdateRes;
import us.tx.state.dfps.service.common.response.UnitSummaryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.heightenedmonitoring.service.HeightenedMonitoringService;
import us.tx.state.dfps.service.legal.dto.LegalActionRtrvOutDto;
import us.tx.state.dfps.service.legal.service.LegalActionSaveService;
import us.tx.state.dfps.service.oncall.service.OnCallService;
import us.tx.state.dfps.service.workload.dto.*;
import us.tx.state.dfps.service.workload.service.AssignWorkloadService;
import us.tx.state.dfps.service.workload.service.CaseTodoService;
import us.tx.state.dfps.service.workload.service.ToDoService;
import us.tx.state.dfps.service.workload.service.UnitSummaryService;
import us.tx.state.dfps.service.workload.service.WorkloadService;
import us.tx.state.dfps.web.security.user.UserRolesEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN11S Class
 * Description: WorkloadController will have all operation which are mapped to
 * Workload module. Mar 23, 2017 - 3:18:29 PM
 * *  ***********  Change History *********************************
 *Oct 15, 2019  mullar2  artf128805 : FCL changes for showing notification section in priority closure for RCI intakes
 */
@RestController
@Api(tags = { "identity" })
@RequestMapping("/workload")
public class WorkloadController {

	@Autowired
	AssignWorkloadService assignWorkloadService;

	@Autowired
	CaseTodoService caseTodoService;

	@Autowired
	WorkloadService workloadService;

	@Autowired
	UnitSummaryService unitSummaryService;

	@Autowired
	ToDoService toDoService;

	@Autowired
	OnCallService onCallService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private LegalActionSaveService legalActionSaveService;

	@Autowired
	private HeightenedMonitoringService heightenedMonitoringService;

	private static final Logger log = Logger.getLogger(WorkloadController.class);

	/**
	 * 
	 * Method Description: This Method is used to retrieve Assigned Workload to
	 * worker by giving person_id in request object(AssignWorkloadReq) Service
	 * Name: CCMN14S
	 * 
	 * @param assignWorkloadReq
	 * @return assignWorkloadRes
	 */
	@ApiOperation(value = "Get workload", tags = { "identity" })
	@RequestMapping(value = "/getAssignWorkloadDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public AssignWorkloadRes getAssignWorkloadDtls(@RequestBody AssignWorkloadReq assignWorkloadReq) {
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getUlIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzCdUnitProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.unitProgram.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzCdUnitRegion())) {
			throw new InvalidRequestException(messageSource.getMessage("common.unitRegion.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("common.nbrUnit.mandatory", null, Locale.US));
		}
		AssignWorkloadRes assignWorkloadRes = assignWorkloadService.getAssignWorkloadDetails(assignWorkloadReq);
		assignWorkloadRes.setTransactionId(assignWorkloadReq.getTransactionId());
		log.info("TransactionId :" + assignWorkloadReq.getTransactionId());
		return assignWorkloadRes;
	}

	/**
	 * 
	 * Method Description: This Method is to get response through service layer
	 * by giving person_id in request object(AssignWorkloadReq)
	 * 
	 * @param assignWorkloadReq
	 * @return todoRes
	 */
	// CCMN11S
	@ApiOperation(value = "Get todo list", tags = { "identity" })
	@RequestMapping(value = "/getTodoDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TodoRes getTodoDtls(@RequestBody TodoReq todoReq) {
		if (null == todoReq.getUlIdTodoPersAssigned()) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (null == todoReq.getToDate()) {
			throw new InvalidRequestException(messageSource.getMessage("common.dateTo.mandatory", null, Locale.US));
		}
		List<TodoDto> todoList = null;
		TodoRes todoRes = new TodoRes();
		todoList = workloadService.getTodoDetails(todoReq);
		todoRes.setTodoDtoList(todoList);
		/*
		 * if (todoRes.getTodoDtoList().size() == ServiceConstants.ZERO_VAL) {
		 * throw new DataNotFoundException(messageSource.getMessage(
		 * "common.data.emptyset", null, Locale.US));
		 * 
		 * }
		 */
		log.info("TransactionId :" + todoReq.getTransactionId());
		return todoRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * case and a time period. Service Name: CCMN12S
	 * 
	 * @param caseTodoReq
	 * @return CaseTodoRes
	 */
	@RequestMapping(value = "/getTodoList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CaseTodoRes getTodoList(@RequestBody CaseTodoReq caseTodoReq) {
		if (TypeConvUtil.isNullOrEmpty(caseTodoReq.getDtTo())) {
			throw new InvalidRequestException(messageSource.getMessage("common.dateTo.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(caseTodoReq.getUlIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		CaseTodoRes caseTodoRes = new CaseTodoRes();
		List<TodoDto> caseTodoList = caseTodoService.getTodoList(caseTodoReq);
		caseTodoRes.setTodoDtoList(caseTodoList);
		caseTodoRes.setTransactionId(caseTodoReq.getTransactionId());
		log.info("TransactionId :" + caseTodoReq.getTransactionId());
		return caseTodoRes;
	}

	/**
	 * 
	 * Method Description: This method is used to retrieve the users supervisor,
	 * NM STAGE, NM TASK, TASK DUE DT, PRIMARY WORKER of STAGE and idTodo based
	 * on the input request function indicator. Service Name: CCMN13S
	 * 
	 * @param toCaseStructvar
	 * @return todoListRes
	 */
	@ApiOperation(value = "Get Staff todo Info", tags = { "identity" })
	@RequestMapping(value = "/getstafftodolist", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TodoListRes getStaffToDoList(@RequestBody TodoListReq todoListReq) {
		if ((todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEXT_APPROVE)
				|| todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEW_APPROVE)
				|| todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.TODO_MODE_NEW_FCE_TODO))) {
			if (TypeConvUtil.isNullOrEmpty(todoListReq.getSzCdUnitProgram()))
				throw new InvalidRequestException(
						messageSource.getMessage("stafftodolist.szCdUnitProgram.mandatory", null, Locale.US));
			if (TypeConvUtil.isNullOrEmpty(todoListReq.getSzCdUnitRegion()))
				throw new InvalidRequestException(
						messageSource.getMessage("stafftodolist.szCdUnitRegion.mandatory", null, Locale.US));
			if (TypeConvUtil.isNullOrEmpty(todoListReq.getSzNbrUnit()))
				throw new InvalidRequestException(
						messageSource.getMessage("stafftodolist.szNbrUnit.mandatory", null, Locale.US));
			if (TypeConvUtil.isNullOrEmpty(todoListReq.getUlIdStage()))
				throw new InvalidRequestException(
						messageSource.getMessage("stafftodolist.ulIdStage.mandatory", null, Locale.US));
		} else if (todoListReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_ASSGIN)) {
			if (TypeConvUtil.isNullOrEmpty(todoListReq.getUlIdStage()))
				throw new InvalidRequestException(
						messageSource.getMessage("stafftodolist.ulIdStage.mandatory", null, Locale.US));
		} else {
			if (TypeConvUtil.isNullOrEmpty(todoListReq.getLdIdTodo()))
				throw new InvalidRequestException(
						messageSource.getMessage("stafftodolist.ldIdTodo.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + todoListReq.getTransactionId());
		return workloadService.StaffToDoList(todoListReq);
	}

	/**
	 * 
	 * Method Description: This service will delete all rows from the _TODO
	 * table for the idTodo given as input. This service was created to allow
	 * BLOCK DELETE of TODOs on the Staff Todo List window to be saved. Service
	 * Name: CCMN97S
	 * 
	 * @param todoListDelReq
	 * @return TodoListDelRes
	 */
	@RequestMapping(value = "/getstafftodolistDel", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public TodoListDelRes getstafftodolistDel(@RequestBody TodoListDelReq todoListDelReq) {
		if (todoListDelReq.getLdIdTodo().size() == ServiceConstants.ZERO_VAL) {
			throw new InvalidRequestException(
					messageSource.getMessage("stafftodolist.UlIdTodo.mandatory", null, Locale.US));
		}
		List<Long> todoIdList =  todoListDelReq.getLdIdTodo();
		List<TodoDto> todoDtoList = workloadService.getTodo(todoIdList);
		log.info("TransactionId :" + todoListDelReq.getTransactionId());
		TodoListDelRes res = workloadService.StaffToDoListDelete(todoListDelReq);
		
		if(ServiceConstants.SUCCESS.equalsIgnoreCase(res.getReturnMsg())){
		workloadService.updateStage(todoDtoList);
		}
		return res; 
	}

	/**
	 * 
	 * Method Description: This service will Add/Update to the _TODO table, Add
	 * to the Approval, Approval Event Link table, Event table, and Approvers
	 * table based on the input request function indicator Service Name: CCMN19S
	 * 
	 * @param todoListAUDReq
	 * @return TodoListAUDRes
	 */
	@RequestMapping(value = "/getstafftodolistAUD", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public TodoListAUDRes getstafftodolistAUD(@RequestBody TodoListAUDReq todoListAUDReq) {
		if (todoListAUDReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEW_APPROVE)) {
			if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getApproversDto().getIdTodo()))
				// messageSource.getMessage("staffToDoListAUD.approversidTodo.mandatory",
				// null,
				// Locale.US));
				if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoEvent()))
				throw new InvalidRequestException(messageSource.getMessage("staffToDoListAUD.todoidevent.mandatory", null, Locale.US));
			if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoCase()))
				throw new InvalidRequestException(
						messageSource.getMessage("staffToDoListAUD.todoidcase.mandatory ", null, Locale.US));
			if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoStage()))
				throw new InvalidRequestException(
						messageSource.getMessage("staffToDoListAUD.todoidstage.mandatory", null, Locale.US));
		} else if (todoListAUDReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_NEXT_APPROVE)) {
			if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoCase()))
				throw new InvalidRequestException(
						messageSource.getMessage("staffToDoListAUD.todoidcase.mandatory ", null, Locale.US));
			if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodoStage()))
				throw new InvalidRequestException(
						messageSource.getMessage("staffToDoListAUD.todoidstage.mandatory", null, Locale.US));
		} else if (todoListAUDReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)) {
			if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodo()))
				throw new InvalidRequestException(
						messageSource.getMessage("staffToDoListAUD.todoid.mandatory", null, Locale.US));
		} else if (todoListAUDReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)) {
			if (TypeConvUtil.isNullOrEmpty(todoListAUDReq.getTodoDto().getIdTodo()))
				throw new InvalidRequestException(
						messageSource.getMessage("staffToDoListAUD.todoid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + todoListAUDReq.getTransactionId());
		return workloadService.staffToDoListAUD(todoListAUDReq);
	}

	/**
	 * 
	 * Method Description: This Method will perform the event details search and
	 * populate the records based on the input request. Service Name: CCMN33S
	 * 
	 * @param eventReq
	 * @return EventDetailRes
	 * 
	 */
	@ApiOperation(value = "Get event list", tags = { "placements" })
	@RequestMapping(value = "/retrieveevent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EventDetailRes getEventDtls(@RequestBody EventReq eventReq) {
		if (eventReq.getUlIdCase() == null && eventReq.getUlIdStage() == null && eventReq.getUlIdPerson() == null
				&& eventReq.getUlIdEvent() == null) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.personcasestage.mandatory", null, Locale.US));
		}
		EventDetailRes eventDetailRes;
		eventDetailRes = workloadService.getEventDetails(eventReq);

		if (null == eventDetailRes.getEventSearchDto()) {
			throw new DataNotFoundException(messageSource.getMessage("common.data.emptyset", null, Locale.US));
		}
		return eventDetailRes;
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
	@RequestMapping(value = "/getAssignSaveGroup", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AssignSaveGroupRes getAssignSaveGroup(@RequestBody AssignSaveGroupReq assignSaveGroupReq) {
		if (assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.INTAKE_NON_APS)
				|| assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.INTAKE_NON_INT)
				|| assignSaveGroupReq.getReqFuncCd().equalsIgnoreCase(ServiceConstants.NON_INT_FAD)) {
			for (int i = 0; i < assignSaveGroupReq.getAssignSaveGroupDto().size(); i++) {
				if (TypeConvUtil.isNullOrEmpty(assignSaveGroupReq.getUlIdPerson()))
					throw new InvalidRequestException(
							messageSource.getMessage("getAssignSaveGroup.personid.mandatory", null, Locale.US));
				if (TypeConvUtil.isNullOrEmpty(assignSaveGroupReq.getAssignSaveGroupDto().get(i).getIdPerson()))
					throw new InvalidRequestException(messageSource
							.getMessage("getAssignSaveGroup.assignGrpDto.personid.mandatory", null, Locale.US));
			}
		}
		log.info("TransactionId :" + assignSaveGroupReq.getTransactionId());
		AssignSaveGroupRes assignSaveGroupRes = new AssignSaveGroupRes();
		assignSaveGroupRes = workloadService.assignSaveGroup(assignSaveGroupReq);
		// Calling a send email function to send the appointment whenever a FPR
		// stage is getting re-assigned to some other case worker.
		if (!CollectionUtils.isEmpty(assignSaveGroupRes.getIdApproverMap())) {
			workloadService.sendEmail(assignSaveGroupRes.getIdApproverMap(), assignSaveGroupReq.getHostName());
		}
		// UIDS - 2.6.1.1 - Assign Removal of Case - Legal Action and Outcome
		// if List of idEvents not empty then send email
		if (!CollectionUtils.isEmpty(assignSaveGroupRes.getIdEvents())) {
			for (LegalActionRtrvOutDto legalActionRtrvOutDto : assignSaveGroupRes.getIdEvents())
				//Warranty Defect#12114 - Issue fixed to avoid dupilcate outlook calender invite
				legalActionSaveService.fetchEmployeeEmail(assignSaveGroupReq,legalActionRtrvOutDto.getIdLegalActEvent(),
						legalActionRtrvOutDto.getDtScheduledCourtDate(), assignSaveGroupReq.getHostName(),
						legalActionRtrvOutDto.getCdLegalActActnSubtype(),
						assignSaveGroupReq.getAssignSaveGroupDto().get(0).getIdCase(), Boolean.TRUE);
		}
		return assignSaveGroupRes;
	}

	/**
	 * 
	 * Method Description: This Method will retrieve counties linked to a Texas
	 * city. Service Name: CCMN87S
	 * 
	 * @param searchRetrieveCountyReq
	 * @return RetrvCountyRes
	 */
	@RequestMapping(value = "/reqCity", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RetrvCountyRes getCountyList(@RequestBody RetrvCountyReq searchRetrieveCountyReq) {
		if (searchRetrieveCountyReq.getSzAddrCity() == null) {
			throw new InvalidRequestException(messageSource.getMessage("city.szAddrCity.mandatory", null, Locale.US));
		}
		RetrvCountyRes searchRetrieveCountyRes = workloadService.getCountyList(searchRetrieveCountyReq);
		log.info("TransactionId :" + searchRetrieveCountyReq.getTransactionId());
		return searchRetrieveCountyRes;
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
	@RequestMapping(value = "/searchStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SearchStageRes searchStageByIds(@RequestBody SearchStageReq searchStageReq) {
		try {
			log.info("TransactionId :" + searchStageReq.getTransactionId());
			return workloadService.getSearchStageList(searchStageReq);
		} catch (DataNotFoundException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(
					messageSource.getMessage("common.data.emptyset", null, Locale.US));
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}
	}

	@RequestMapping(value = "/saveRejectionApproval", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes saveRejectionApproval(@RequestBody RejectionApprovalDtoReq rejectionApprovalDtoReq) {

		if (!ObjectUtils.isEmpty(rejectionApprovalDtoReq.getRejectApprovalDto())) {
			workloadService.saveRejectionApproval(rejectionApprovalDtoReq.getRejectApprovalDto());
		}
		return new CommonBooleanRes();
	}

	/**
	 * 
	 * Method Description:recieve list of case object to be either deleted or
	 * added into CASE_MERGE table based on action indicator CCFC41S
	 * 
	 * @param caseMergeUpdateReq
	 * @return CaseMergeUpdateRes
	 */
	@RequestMapping(value = "/updateMergeCase", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CaseMergeUpdateRes updateCaseMerge(@RequestBody CaseMergeUpdateReq caseMergeUpdateReq){
		List<CaseMergeUpdateDto> caseList = caseMergeUpdateReq.getUpdateinput();
		for (CaseMergeUpdateDto caseMergeUpdateDto : caseList) {
			if (caseMergeUpdateDto.getTsLastUpdate() == null) {
				throw new DataNotFoundException(
						messageSource.getMessage("caseMerge.dtLastUpdate.mandatory", null, Locale.US));
			}
			if (caseMergeUpdateDto.getIdCaseMerge() == null) {
				throw new DataNotFoundException(
						messageSource.getMessage("caseMerge.IdCaseMerge.mandatory", null, Locale.US));
			}
			if (caseMergeUpdateDto.getIdCaseMergeFrom() == null) {
				throw new DataNotFoundException(
						messageSource.getMessage("caseMerge.IdCaseMergeFrom.mandatory", null, Locale.US));
			}
			if (caseMergeUpdateDto.getIdCaseMergeTo() == null) {
				throw new DataNotFoundException(
						messageSource.getMessage("caseMerge.IdCaseMergeTo.mandatory", null, Locale.US));
			}
		}
		log.info("TransactionId :" + caseMergeUpdateReq.getTransactionId());
		return workloadService.CaseMergeUpdate(caseMergeUpdateReq);
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
	@RequestMapping(value = "/getApprovalStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ApprovalStatusRes getApprovalStatusDtls(@RequestBody ApprovalStatusReq approvalStatusReq){
		if (TypeConvUtil.isNullOrEmpty(approvalStatusReq.getUlIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approvalstatus.eventid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + approvalStatusReq.getTransactionId());
		return workloadService.fetchApprovalStatusDtls(approvalStatusReq);
	}

	/**
	 * 
	 * Method Description: This service will retrieve information needed in
	 * order to assign an employee to a stage Tuxedo Service Name:
	 * 
	 * @param searchAssignReq
	 * @return searchAssignRes
	 */
	@RequestMapping(value = "/getAssign", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SearchAssignRes getAssignEmpStage(@RequestBody SearchAssignReq searchAssignReq) {
		SearchAssignRes searchAssignRes = new SearchAssignRes();
		if (TypeConvUtil.isNullOrEmpty(searchAssignReq.getIdUnit())
				&& TypeConvUtil.isNullOrEmpty(searchAssignReq.getCdOnCallCounty())
				&& TypeConvUtil.isNullOrEmpty(searchAssignReq.getCdOnCallProgram())
				&& TypeConvUtil.isNullOrEmpty(searchAssignReq.getIdStageList())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		try {
			searchAssignRes = workloadService.getAssignEmpStage(searchAssignReq);
		} catch (DataNotFoundException e) {
			DataNotFoundException dataNotFoundException = new DataNotFoundException(
					messageSource.getMessage("workload.assign.data", null, Locale.US));
			dataNotFoundException.initCause(e);
			throw dataNotFoundException;
		}
		log.info("TransactionId :" + searchAssignRes.getTransactionId());
		return searchAssignRes;
	}

	/**
	 * getInvCnclPendingStages: Returns CPS stageIds that has pending
	 * investigation conclusions.
	 * 
	 * Service Name - NA (Util Method getInvCnclPendingStages)
	 * 
	 * @param utilInputDto
	 * @return List<Long>
	 */
	@RequestMapping(value = "/getInvCnclPendingStages", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getInvCnclPendingStages(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdWorker())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idWorker.mandatory", null, Locale.US));
		}
		return workloadService.getInvCnclPendingStages(commonHelperReq);
	}

	/**
	 * getCheckedOutStages: Returns an array of stage ID's representing the
	 * subset of a passed array of stage ID's that are currently checked out to
	 * MPS
	 * 
	 * Service Name - NA (Util Method getCheckedOutStages)
	 * 
	 * @param utilInputDto
	 * @return List<Long>
	 */
	@RequestMapping(value = "/getCheckedOutStages", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getCheckedOutStages(@RequestBody CommonHelperReq commonHelperReq) {
		return workloadService.getCheckedOutStages(commonHelperReq);
	}

	/**
	 * getStagesWithARasPriorStage: Optimize workload page query to improve page
	 * loading times. Returns the list of Stage Ids that has A-R as a prior
	 * stage
	 * 
	 * Service Name - NA (Util Method getStagesWithARasPriorStage)
	 * 
	 * @param utilInputDto
	 * @return Set<Long>
	 */
	@RequestMapping(value = "/getStagesWithARasPriorStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getStagesWithARasPriorStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdWorker())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idWorker.mandatory", null, Locale.US));
		}
		return workloadService.getStagesWithARasPriorStage(commonHelperReq);
	}

	/**
	 * getARPendingStages: Returns list of AR stages that requires worker
	 * attention
	 * 
	 * Service Name - NA (Util Method getARPendingStages)
	 * 
	 * @param utilInputDto
	 * @return List<Long>
	 */
	@RequestMapping(value = "/getARPendingStages", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getARPendingStages(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdWorker())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idWorker.mandatory", null, Locale.US));
		}
		return workloadService.getARPendingStages(commonHelperReq);
	}

	/**
	 * 
	 * Method Description:Predisplay service for CINT15W Priority/Closure
	 * window. Tuxedo Service Name:CINT99S
	 * 
	 * @param preDisplayPriorityClosureReq
	 * @return preDisplayPriorityClosureRes
	 */
	@RequestMapping(value = "/predisplaypriorityclosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PreDisplayPriorityClosureRes getPreDisplayPriorityClosure(
			@RequestBody PreDisplayPriorityClosureReq preDisplayPriorityClosureReq) {
		PreDisplayPriorityClosureRes preDisplayPriorityClosureRes = new PreDisplayPriorityClosureRes();
		if (TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureReq.getIdStage())
				&& TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureReq.getCdEventType())
				&& TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureReq.getCdEventStatus())
				&& TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureReq.getTxtEventDescr())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		preDisplayPriorityClosureRes = workloadService.getPreDisplayPriorityClosure(preDisplayPriorityClosureReq);
		if (TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureRes)) {
			throw new DataNotFoundException(
					messageSource.getMessage("workload.PreDisplayPriorityClosure.data", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureRes.getpCStageRow())) {
			throw new DataNotFoundException(
					messageSource.getMessage("workload.PreDisplayPriorityClosure.stageData", null, Locale.US));
		}
		log.info("TransactionId :" + preDisplayPriorityClosureRes.getTransactionId());
		return preDisplayPriorityClosureRes;
	}

	/**
	 *
	 *
	 * Method Description: This Method is to retrieve for priority closure
	 * screen. Service Name:Priority Closure Ejb Service Tuxedo Dam Name:
	 * PriortyClosureDAO
	 * 
	 * @param priorityClosureEjbReq
	 * @return priorityClosureEjbRes @
	 */
	@RequestMapping(value = "/priorityclosure", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PriorityClosureRes getIdEventPersonInfo(@RequestBody PriorityClosureReq priorityClosureEjbReq) {
		PriorityClosureRes priorityClosureEjbRes = new PriorityClosureRes();
		if (null == (priorityClosureEjbReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		priorityClosureEjbRes = workloadService.getIdEventPersonInfo(priorityClosureEjbReq);
		if (null == (priorityClosureEjbRes.getPriorityClosureEjbDto())) {
			priorityClosureEjbRes.setPriorityClosureEjbDto(null);
			// throw new
			// DataNotFoundException(messageSource.getMessage("workload.PriorityClosure.data",
			// null, Locale.US));
		}
		log.info("TransactionId :" + priorityClosureEjbRes.getTransactionId());
		return priorityClosureEjbRes;
	}

	/**
	 *
	 *
	 * Method Description: This Method is used to check if the Stage is closed
	 * or not
	 * 
	 * @param StageId
	 * @return Boolean True or False @
	 */
	@RequestMapping(value = "/isStageClosed", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes isStageClosed(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		commonHelperRes = workloadService.isStageClosed(commonHelperReq);
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
	@RequestMapping(value = "/priorityclosuresave", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public PriorityClosureSaveRes priorityClosureSave(@RequestBody PriorityClosureSaveReq priorityClosureSaveReq) {
		PriorityClosureSaveRes priorityClosureSaveRes = new PriorityClosureSaveRes();
		if (null == (priorityClosureSaveReq.getPriorityClosureDto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		priorityClosureSaveRes = workloadService.savePriority(priorityClosureSaveReq);
		log.info("TransactionId :" + priorityClosureSaveRes.getTransactionId());
		return priorityClosureSaveRes;
	}

	/**
	 * Returns Extension request Event object for the given AR stage.
	 * 
	 * @param idStage
	 * @return Eventid and Event status
	 */
	@RequestMapping(value = "/getARExtensionRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ARExtensionRes getARExtensionRequest(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		return workloadService.getARExtensionRequest(commonHelperReq);
	}

	/**
	 * Method Description:Returns cd mobile Status given a Workload Case Id to
	 * it.
	 * 
	 * @param idWkldCase
	 * @return String cd_mobile_status
	 */
	@RequestMapping(value = "/getCdMobileStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getCdMobileStatus(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idWkldCase.mandatory", null, Locale.US));
		}
		commonHelperRes = workloadService.getCdMobileStatus(commonHelperReq);
		return commonHelperRes;
	}

	/**
	 * Method-Description:Returns the role of a person in a stage, provided the
	 * case is in that person's workload. If the person does not have a role, or
	 * the stage is no in their workload, will return empty string (""). Should
	 * always return PRIMARY ("PR") or SECONDARY ("SE").
	 *
	 * @param ulIdPerson
	 * @param ulIdStage
	 * @return the role of the person in that stage in their workload.
	 */
	@RequestMapping(value = "/getRoleInWorkloadStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getRoleInWorkloadStage(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes = workloadService.getRoleInWorkloadStage(commonHelperReq);
		return commonHelperRes;
	}

	/**
	 * Method-Description:This method will fetch all active stages for a person
	 * 
	 * @param PersonID
	 * @return List of all active Stage Id(s)
	 */
	@RequestMapping(value = "/getActiveStagesForPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public IDListRes getActiveStagesForPerson(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idPerson.mandatory", null, Locale.US));
		}
		return workloadService.getActiveStagesForPerson(commonHelperReq);
	}

	/**
	 * Method-Description:Check Configuration table ONLINE_PARAMETERS, if Tlets
	 * Check needs to be Enabled or Disabled
	 * 
	 * @return Boolean -- true or false @
	 */
	@RequestMapping(value = "/disableTletsCheck", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes disableTletsCheck() {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes = workloadService.disableTletsCheck();
		return commonHelperRes;
	}

	/**
	 * Method Description: This method will implemented on case summary screen.
	 * Service Name: KinApproval
	 * 
	 * @param kinApprovalReq
	 * @return KinApprovalRes
	 */
	@RequestMapping(value = "/kinApproval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public KinApprovalRes checkStageCaseID(@RequestBody KinApprovalReq kinApprovalReq) {
		if (TypeConvUtil.isNullOrEmpty(kinApprovalReq.getIdCaseFrom())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return workloadService.checkStageCaseID(kinApprovalReq);
	}

	/**
	 * 
	 * Method Description: This method is called, when a user searches for a
	 * unit. The user profile is used to populate the search parameters
	 * therefore no service is called here.
	 * 
	 * @param AssignWorkloadReq
	 * @return AssignWorkloadReq
	 * @, InvalidRequestException Tuxedo Service Name: CCMN29S
	 */
	@RequestMapping(value = "/getUnitSummary", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AssignWorkloadReq getUnitSummaryDtls(@RequestBody AssignWorkloadReq assignWorkloadReq) {
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzCdUnitProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.unitProgram.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzCdUnitRegion())) {
			throw new InvalidRequestException(messageSource.getMessage("common.unitRegion.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("common.nbrUnit.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + assignWorkloadReq.getTransactionId());
		return unitSummaryService.getUnitSummaryDtls(assignWorkloadReq);
	}

	/**
	 * 
	 * Method Description: This method Searches the database for program -
	 * region - unit combination that match the specified search criteria.
	 * 
	 * @param AssignWorkloadReq
	 * @return UnitSummaryRes
	 * @, InvalidRequestException Tuxedo Service Name: CCMN29S
	 */
	@RequestMapping(value = "/searchUnitSummary", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public UnitSummaryRes searchUnitSummary(HttpServletRequest request,@RequestBody AssignWorkloadReq assignWorkloadReq) {
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzCdUnitProgram())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.unitProgram.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzCdUnitRegion())) {
			throw new InvalidRequestException(messageSource.getMessage("common.unitRegion.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(assignWorkloadReq.getSzNbrUnit())) {
			throw new InvalidRequestException(messageSource.getMessage("common.nbrUnit.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + assignWorkloadReq.getTransactionId());
		return unitSummaryService.searchUnitSummary(assignWorkloadReq);
	}

	/**
	 * Method Description: This method is to find if the contact with the
	 * purpose of initial already exist.
	 * 
	 * @paramidStage
	 * @returnBoolean -- true or False
	 */
	@RequestMapping(value = "/getContactPurposeStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getContactPurposeStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return workloadService.getContactPurposeStatus(commonHelperReq);
	}

	/**
	 * Method Description: This method is to find if there is an approved
	 * contact for the given case id and contact type.
	 * 
	 * @paramidCaseContactType
	 * @returnBoolean -- true or False
	 */
	@RequestMapping(value = "/isAprvContactInCase", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes isAprvContactInCase(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdContactType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.contactType.mandatory", null, Locale.US));
		}
		commonHelperRes = workloadService.isAprvContactInCase(commonHelperReq);
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
	@RequestMapping(value = "/getCheckedOutPersonStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getCheckedOutPersonStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson2())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return workloadService.getCheckedOutPersonStatus(commonHelperReq);
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
	@RequestMapping(value = "/getApproversStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ApproverStatusRes getApproversStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return workloadService.getApproversStatus(commonHelperReq);
	}

	@RequestMapping(value = "/getPrimaryWorkerIdForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getPrimaryWorkerIdForStage(@RequestBody CommonHelperReq commonHelperReq) {
		return workloadService.getPrimaryWorkerIdForStage(commonHelperReq);
	}

	/**
	 * Method Name: getAssignedWorkersForStage Method Description: This method
	 * will fetch the list of Primary and Secondary workers assigned to a Stage
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/getAssignedWorkersForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getAssignedWorkersForStage(@RequestBody CommonHelperReq commonHelperReq) {
		List<Long> idAssignedWorkers = new ArrayList<>();
		idAssignedWorkers = workloadService.getAssignedWorkersForStage(commonHelperReq.getIdStage());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setPersonIds(idAssignedWorkers);
		return commonHelperRes;
	}

	/**
	 * AR - Stage progression from AR - FPR - message not displayed This method
	 * will fetch the overall disposition
	 * 
	 * @param idCase
	 *            the stage identifier
	 * @return String disposition code @
	 */
	@RequestMapping(value = "/getARStageOverallDisposition", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getARStageOverallDisposition(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return workloadService.getARStageOverallDisposition(commonHelperReq);
	}

	/**
	 * Method Description: This service will retrieve the Risk Assessment
	 * Details as a List for the particular Case.
	 * 
	 * @param eventReq
	 * @return EventDetailRes @
	 */
	@RequestMapping(value = "/sdmAssmntList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EventDetailRes getSDMAssmntList(@RequestBody EventReq eventReq) {
		if (TypeConvUtil.isNullOrEmpty(eventReq.getUlIdCase()))
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(eventReq.getSzCdTask()))
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		EventDetailRes eventDetailRes = workloadService.getSDMAssmntListDtls(eventReq);
		return eventDetailRes;
	}

	/**
	 * Method Description: This service will returns a the event and todo id's
	 * for an approval given an event from the stage
	 * 
	 * @param ToDoUtilityReq
	 * @return ToDoUtilityRes @
	 */
	@RequestMapping(value = "/toDoIdForApproval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getToDoIdForApproval(@RequestBody ToDoUtilityReq toDoUtilityReq) {
		if (TypeConvUtil.isNullOrEmpty(toDoUtilityReq.getIdToDo()))
			throw new InvalidRequestException(
					messageSource.getMessage("toDoIdForApproval.toDoUtilityReq.idToDo.mandatory", null, Locale.US));
		TodoDto toDoDto = workloadService.getToDoIdForApproval(toDoUtilityReq);
		CommonHelperRes resp = new CommonHelperRes();
		resp.setToDoDto(toDoDto);
		return resp;
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
	@RequestMapping(value = "/fetchIdEventForIdAprEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes fetchIdEventForIdAprEvent(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return workloadService.fetchIdEventForIdAprEvent(commonHelperReq);
	}

	@RequestMapping(value = "/printTaskExists", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes printTaskExists(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return workloadService.getPrintTaskExists(commonHelperReq);
	}

	@ApiOperation(value = "Get todo task details", tags = { "identity" })
	@RequestMapping(value = "/getTodoDto", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getTodoDto(@RequestBody ToDoUtilityReq toDoUtilityReq) {
		CommonHelperRes todoRes = workloadService.getTodoDtlById(Long.valueOf(toDoUtilityReq.getIdToDo()));
		return todoRes;
	}

	/**
	 * Method Name: selectToDosForStage Method Description: This function
	 * retrieves Open Tasks / Alerts of the given Type for the given Stage. If
	 * cdTodoInfoList is null, returns all Todos/Alerts for the Stage. EJB Name
	 * : ToDoBean.java
	 * 
	 * @param toDoStageReq
	 * @return TodoSelectStageRes
	 */
	@RequestMapping(value = "/selectToDosForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  TodoSelectStageRes selectToDosForStage(@RequestBody ToDoStageReq toDoStageReq) {

		log.info("WorkloadController.TodoSelectStageRes()");

		if (TypeConvUtil.isNullOrEmpty(toDoStageReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.idstage.mandatory", null, Locale.US));
		}

		/*
		 * if (TypeConvUtil.isNullOrEmpty(toDoStageReq.getCdTodoInfoList())) {
		 * throw new InvalidRequestException(
		 * messageSource.getMessage("todo.cdtodoinfolist.mandatory", null,
		 * Locale.US)); }
		 */

		TodoSelectStageRes todoSelectStageRes = new TodoSelectStageRes();
		todoSelectStageRes.setTodosDto(
				(toDoService.selectToDosForStage(toDoStageReq.getIdStage(), toDoStageReq.getCdTodoInfoList())));

		return todoSelectStageRes;

	}

	/**
	 * Method Name: getSupervisorForStage Method Description: This function
	 * returns the Supervisor for the given Stage. EJB Name : ToDoBean.java
	 * 
	 * @param toDoStageReq
	 * @return TodoSupervisorRes
	 */
	@RequestMapping(value = "/getSupervisorForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  TodoSupervisorRes getSupervisorForStage(@RequestBody ToDoStageReq toDoStageReq) {

		TodoSupervisorRes todoSupervisorRes = new TodoSupervisorRes();
		log.info("TransactionId :" + toDoStageReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(toDoStageReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.idStage.mandatory", null, Locale.US));
		}

		todoSupervisorRes.setIdSupervisor(toDoService.getSupervisorForStage(toDoStageReq.getIdStage()));

		return todoSupervisorRes;
	}

	/**
	 * Method Name: updateTodo Method Description: This method update _TODO
	 * Table. EJB Name : ToDoBean.java
	 * 
	 * @param todoUpdateReq
	 * @return TodoUpdateRes
	 */
	@RequestMapping(value = "/updateTodo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  TodoUpdateRes updateTodo(@RequestBody TodoUpdateReq todoUpdateReq) {

		log.info("TransactionId :" + todoUpdateReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(todoUpdateReq.getTodoDto())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.todoDto.mandatory", null, Locale.US));
		}

		TodoUpdateRes toDoRes = new TodoUpdateRes();
		toDoRes.setTodoDto(toDoService.updateToDo(todoUpdateReq.getTodoDto()));
		return toDoRes;

	}

	@RequestMapping(value = "/deleteTodoByEventId/{idEvent}", headers = {"Accept=application/json"}, method = RequestMethod.DELETE)
	public void deleteTodoByEventId(@PathVariable(value = "idEvent") Long idEvent) {
		toDoService.deleteToDoByEvent(idEvent);
	}

	@RequestMapping(value = "/deleteTodoByEventIdAndTask/{idEvent}", headers = {"Accept=application/json"}, method = RequestMethod.DELETE)
	public void deleteTodoListByEventIdAndTaskCode(@PathVariable(value = "idEvent") Long idEvent) {
		toDoService.deleteTodoListByEventIdAndTaskCode(idEvent);
	}

  @RequestMapping(
      value = "/deleteTodoByIdTodo/{idTodo}",
      headers = {"Accept=application/json"},
      method = RequestMethod.DELETE)
  public void deleteTodoByIdTodo(@PathVariable(value = "idTodo") Long idTodo) {
    toDoService.deleteTodoByIdTodo(idTodo);
  }

	@RequestMapping(value = "/isCaseAssignedToPerson/{idPerson}/{idCase}", produces = "application/json", method = RequestMethod.GET)
	public Boolean isCaseAssignedToPerson(@PathVariable(value = "idPerson") Long idPerson,
										  @PathVariable(value = "idCase") Long idCase) {
		return workloadService.isCaseAssignedToPerson(idPerson, idCase);
	}

	/**
	 *
	 * Method Name: RtrvOnCallNbr Method Description:This service retrieves data
	 * for On Call List window's county list box.
	 *
	 * @param onCallCountyReq
	 * @return OnCallCountyRes
	 */
	@RequestMapping(value = "/rtrvOnCallCounty", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  OnCallCountyRes getCountyForOnCall(@RequestBody OnCallCountyReq onCallCountyReq) {

		OnCallCountyRes onCallCountyRes = new OnCallCountyRes();

		if (TypeConvUtil.isNullOrEmpty(onCallCountyReq.getCdRegion())) {
			throw new InvalidRequestException(
					messageSource.getMessage("onCall.cdOnCallRegion.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(onCallCountyReq.getIdOnCall())) {
			throw new InvalidRequestException(messageSource.getMessage("assign.idoncall.empty", null, Locale.US));
		}

		onCallCountyRes = onCallService.rtrvOnCallCountyDtl(onCallCountyReq);
		log.info("TransactionId :" + onCallCountyReq.getTransactionId());
		return onCallCountyRes;

	}

	/**
	 * Tuxedo Service Name: CINV69S. Method Description: This method is used to
	 * retrieve M-Ref Status form by passing IdPerson as input request
	 * 
	 * @param MrefStatusFormReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/getAssignWorkload", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getAssignWorkload(@RequestBody MrefStatusFormReq mrefStatusFormReq) {

		if (TypeConvUtil.isNullOrEmpty(mrefStatusFormReq.getIdPerson()))
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(workloadService.getMrefStatusForm(mrefStatusFormReq)));
		log.info("TransactionId :" + mrefStatusFormReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * Tuxedo Service Name: CINV88S. Method Description: This method is used to
	 * retrieve SIJS Status form by passing
	 * IdPerson,CdRegion,CdPersonCitizenship as input request
	 * 
	 * @param MrefStatusFormReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/getSIJSForm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getSijsForm(@RequestBody CitizenStatusFormReq citizenStatusFormReq) {

		if (TypeConvUtil.isNullOrEmpty(citizenStatusFormReq.getIdPerson()))
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));

		if (TypeConvUtil.isNullOrEmpty(citizenStatusFormReq.getCdRegion()))
			throw new InvalidRequestException(
					messageSource.getMessage("workloadcontroller.cdRegion.mandatory", null, Locale.US));

		if (TypeConvUtil.isNullOrEmpty(citizenStatusFormReq.getCdPersonCitizenship()))
			throw new InvalidRequestException(
					messageSource.getMessage("workloadcontroller.cdCitizenship.mandatory", null, Locale.US));
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(workloadService.getSijsForm(citizenStatusFormReq)));
		log.info("TransactionId :" + citizenStatusFormReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * Method Description: This method is used to return Flag which enables
	 * Select Options for SIJS Status dropdown in Assigned Workload Page
	 * 
	 * @param DisplaySijsStatusFormReq
	 * @return DisplaySijsStatusFormRes
	 */
	@RequestMapping(value = "/displaySijsStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public DisplaySijsStatusFormRes displaySijsStatusForm(
			@RequestBody DisplaySijsStatusFormReq displaySijsStatusFormReq) {

		if (TypeConvUtil.isNullOrEmpty(displaySijsStatusFormReq.getIdPerson()))
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));

		boolean displaySijsStatusFormFlag;

		DisplaySijsStatusFormRes displaySijsStatusFormRes = new DisplaySijsStatusFormRes();
		displaySijsStatusFormFlag = workloadService.getSijsStatus(displaySijsStatusFormReq.getIdPerson());
		displaySijsStatusFormRes.setDisplayStatus(displaySijsStatusFormFlag);
		log.info("TransactionId :" + displaySijsStatusFormReq.getTransactionId());
		return displaySijsStatusFormRes;
	}

	/**
	 * Method Name: endDateInvAlerts Method Description: This function will be
	 * called by the Approval of Investigation conclusion. It will end date all
	 * the Alerts/Tasks for the workers and supervisors reminding them to
	 * complete Investigation conclusion. EJB Name : ToDoBean.java
	 * 
	 * @param todoEndDateReq
	 * @return TodoEndDateRes
	 * 
	 */
	@RequestMapping(value = "/endDateInvAlerts", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  TodoRes endDateInvAlerts(@RequestBody TodoReq todoEndDateReq) {

		if (TypeConvUtil.isNullOrEmpty(todoEndDateReq.getIdInvStage())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.idInvStage.mandatory", null, Locale.US));
		}

		toDoService.endDateInvAlerts(todoEndDateReq.getIdInvStage());
		TodoRes todoEndDateRes = new TodoRes();
		todoEndDateRes.setIdInvStage(todoEndDateReq.getIdInvStage());

		return todoEndDateRes;

	}

	/**
	 * Method Name: changeInvAlertDueDates Method Description: This function
	 * changes the Due date for Caseworker and Supervisor Alerts if the
	 * Extension Request is Approved EJB Name : ToDoBean.java
	 * 
	 * @param investigationAlertReq
	 * @return InvAlertDueDatesRes
	 * 
	 */
	@RequestMapping(value = "/changeInvAlertDueDates", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  InvAlertDueDatesRes changeInvAlertDueDates(
			@RequestBody InvAlertDueDatesReq invAlertDueDatesReq){
		InvAlertDueDatesRes investigationAlertRes = new InvAlertDueDatesRes();

		if (TypeConvUtil.isNullOrEmpty(invAlertDueDatesReq.getIdInvStage())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.idInvStage.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(invAlertDueDatesReq.getApprovalLen())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.approvalLen.mandatory", null, Locale.US));
		}

		toDoService.changeInvAlertDueDates(invAlertDueDatesReq.getIdInvStage(), invAlertDueDatesReq.getApprovalLen());
		investigationAlertRes.setIdInvStage(invAlertDueDatesReq.getIdInvStage());
		investigationAlertRes.setApprovalLen(invAlertDueDatesReq.getApprovalLen());
		return investigationAlertRes;

	}

	/**
	 * 
	 * Method Name: getToDoCount Method Description:This method is used to count
	 * the tasks in the Staff To Do List with the taskode 2560 and return true
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/getToDoCount", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes getToDoCount(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonBooleanRes commonBooleanRes = new CommonBooleanRes();
		commonBooleanRes.setExists(workloadService.getToDoCount(commonHelperReq.getIdStage()));
		return commonBooleanRes;

	}

	/**
	 * 
	 * Method Name: assignSecondary Method Description: This method is used to
	 * perform secondary assignment for LPS supervisor in case of placement out
	 * of region true
	 * 
	 * @param commonHelperReq
	 * @return AssignSaveGroupRes
	 */
	@RequestMapping(value = "/assignSecondary", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public AssignSaveGroupRes assignSecondary(@RequestBody CommonHelperReq commonHelperReq) {
		return workloadService.assignSecondary(commonHelperReq.getIdCase(), commonHelperReq.getIdStage(),
				commonHelperReq.getIdPerson(), new Long(commonHelperReq.getIdUser()));
	}
	
	@RequestMapping(value = "/stagePersRole", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes stagePersRole(@RequestBody CommonHelperReq commonHelperReq) {
		String persRole = workloadService.getStagePersRole(commonHelperReq.getIdStage(), commonHelperReq.getIdPerson());
		CommonHelperRes res = new CommonHelperRes();
		res.setCdStagePersRole(persRole);
		return res;
	}
	

	/**
	 *Method Name:	fetchRevisedVictimDetails
	 *Method Description:Fetch the revised victim details
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	@RequestMapping(value = "/fetchRevisedVictimDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public PreDisplayPriorityClosureRes fetchRevisedVictimDetails(@RequestBody PreDisplayPriorityClosureReq preDisplayPriorityClosureReq)  {
        PreDisplayPriorityClosureRes preDisplayPriorityClosureRes = new PreDisplayPriorityClosureRes();
        if (TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        preDisplayPriorityClosureRes = workloadService.fetchRevisedVictimDetails(preDisplayPriorityClosureReq);
        if (TypeConvUtil.isNullOrEmpty(preDisplayPriorityClosureRes)) {
            throw new DataNotFoundException(
                    messageSource.getMessage("workload.PreDisplayPriorityClosure.data", null, Locale.US));
        }

        log.info("TransactionId :" + preDisplayPriorityClosureRes.getTransactionId());
        return preDisplayPriorityClosureRes;
    }
	
	/**
	 *Method Name:	fetchRevisedChildrenInFacility
	 *Method Description:This method is called to update the children in facility  details after the resource search in priority closure page for RCI intake
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	@RequestMapping(value = "/fetchRevisedChildrenInFacility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public PreDisplayPriorityClosureRes fetchRevisedChildrenInFacility(@RequestBody PreDisplayPriorityClosureReq preDisplayPriorityClosureReq)  {  
	    PreDisplayPriorityClosureRes preDisplayPriorityClosureRes= workloadService.fetchRevisedChildrenInFacility(preDisplayPriorityClosureReq);
	    //fetch the latest facility children details
	    if(!ObjectUtils.isEmpty(preDisplayPriorityClosureReq.getIdVictimNotificationRsrc())) {	   
	        IntakeNotfFacilityDto facilityDto=preDisplayPriorityClosureRes.getPriorityClosureDto().getFacilityDetails();
	        facilityDto.setChildrenInFacilityList(workloadService.fetchLatestChildrenInFacility(preDisplayPriorityClosureReq.getIdVictimNotificationRsrc()));
	        preDisplayPriorityClosureRes.getPriorityClosureDto().setFacilityDetails(facilityDto);
	    }
	    return preDisplayPriorityClosureRes;
    }

	/**
	 *Method Name:	getRevisedChildrenInFacility
	 *Method Description:This method is called to get the children in facility  details after the validate License Info in priority closure page for RCI intake
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	@RequestMapping(value = "/getRevisedChildrenInFacility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PreDisplayPriorityClosureRes getRevisedChildrenInFacility(@RequestBody PreDisplayPriorityClosureReq preDisplayPriorityClosureReq)  {
		PreDisplayPriorityClosureRes preDisplayPriorityClosureRes= workloadService.getRevisedChildrenInFacility(preDisplayPriorityClosureReq);
		return preDisplayPriorityClosureRes;
	}

	/**
	 *
	 * Method Description: This Method is to get response through service layer
	 * by giving person_id in request object(AssignWorkloadReq)
	 *
	 * @param assignWorkloadReq
	 * @return todoRes
	 */

	@ApiOperation(value = "Get alert popup todo list", tags = { "identity" })
	@RequestMapping(value = "/getAlertTodoDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TodoRes getAlertTodoDtls(@RequestBody TodoReq todoReq) {
		if (null == todoReq.getUlIdTodoPersAssigned()) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(todoReq.getIdStage())) {

			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (null == todoReq.getIdCase()) {
			throw new InvalidRequestException(messageSource.getMessage("common.IdCase.mandatory", null, Locale.US));
		}

		List<TodoDto> todoList = null;
		TodoRes todoRes = new TodoRes();
		todoList = workloadService.getRCIAlertTodoDetailsByCaseAndStage(todoReq);
		todoRes.setTodoDtoList(todoList);

		log.info("TransactionId :" + todoReq.getTransactionId());
		return todoRes;
	}


	/**
	 * Method Name: updateTodos Method Description: Manual Stage Progression INT
	 * to A-R This function updates To do Table. EJB Name : To DoBean.java
	 *
	 * @param todoReq
	 * @return ToDosRes
	 */
	@ApiOperation(value = "update alert popup todo list", tags = { "identity" })
	@RequestMapping(value = "/updateAlertTodo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  TodoRes updateAlertTodos(@RequestBody TodoReq todoReq) {
		TodoRes toDosRes = new TodoRes();
		toDosRes.setResult(toDoService.updateAlertToDos(todoReq.getTodoDto()));
		toDosRes.setTodoDtoList(todoReq.getTodoDto());
		return toDosRes;
	}

	@RequestMapping(value = "/getTodoCaseDueDtList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TodoRes getTodoCaseDueDtList(@RequestBody TodoReq caseTodoReq) {
		List<TodoDto> paramList = caseTodoReq.getTodoDto();
		toDoService.getTodoCaseDueDtList(paramList);

		TodoRes retVal = new TodoRes();
		retVal.setTodoDtoList(paramList);
		return retVal;
	}

	/**
	 *  This method will return true if person has the permissions to approve the Guardianship Referral.
	 *
	 * @param commonHelperReq request from controller
	 * @return
	 */
	@RequestMapping(value = "/checkGuardianshipApprove", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes checkGuardianshipApprove(@RequestBody CommonHelperReq commonHelperReq){
		CommonBooleanRes  commonBooleanRes = new CommonBooleanRes();
		commonBooleanRes.setExists(toDoService.checkGudApprovels(commonHelperReq.getIdPerson()));
		return commonBooleanRes;
	}

	/**
	 * Method helps to get the REST request from Page Api and process the request and
	 * response with data Assigned dto data
	 *
	 * @param commonHelperReq common help request
	 * @return returns todolistResponse bean with Assigned dto data
	 */
	@RequestMapping(value = "/getEMRProgramAdmin", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public TodoListRes getEMRProgramAdmin(@RequestBody CommonHelperReq commonHelperReq){
		TodoListRes todoListRes = new TodoListRes();

		if(CodesConstant.APRV_APS_INV_EMR.equals(commonHelperReq.getSzCdTask())){
			AssignedDto assignedDto = workloadService.findEMRProgramAdmin(commonHelperReq.getIdUser(), "MNTN_EMR_APRV");
			todoListRes.setAssignedDto(assignedDto);
		}else if(CodesConstant.APRV_APS_SVC_AUTH.equals(commonHelperReq.getSzCdTask())) {
			double svcAuthAmt = heightenedMonitoringService.getSvcAuthAmount(commonHelperReq.getIdEvent());
			double authAmt = Double.valueOf("750");
			if(Double.compare(svcAuthAmt, authAmt) > 0) {
				AssignedDto assignedDto = workloadService.findEMRProgramAdmin(commonHelperReq.getIdUser(), "APS_SA_APRV");
				todoListRes.setAssignedDto(assignedDto);
			}
		}
		return todoListRes;
	}

	@RequestMapping(value = "/checkCaseClosureApprover", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes checkCaseClosureApprover(@RequestBody CommonHelperReq commonHelperReq){
		CommonBooleanRes  commonBooleanRes = new CommonBooleanRes();
		commonBooleanRes.setExists(toDoService.checkCaseClosureApprover(commonHelperReq.getIdPerson()));
		return commonBooleanRes;
	}

	@RequestMapping(value = "/getApprovalPersonRole", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes getApprovalPersonRole(@RequestBody CommonHelperReq commonHelperReq){
		CommonBooleanRes  commonBooleanRes = new CommonBooleanRes();
		commonBooleanRes.setExists(workloadService.getExecStaffSecurityForEMR(commonHelperReq.getIdPerson(), "MNTN_EMR_APRV"));
		return commonBooleanRes;
	}

	@RequestMapping(value = "/getValidatedAddressApi", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public AddressDetailBean getValidatedAddressApi(@RequestBody String guid) {
		return workloadService.getValidatedAddressApi(guid.replace("\"",""));
	}

	@RequestMapping(value = "/saveValidatedAddressSSCCApi", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public CommonBooleanRes saveValidatedAddressApi(@RequestBody AddressDetailBean addressDetailBean) {
		CommonBooleanRes  commonBooleanRes = new CommonBooleanRes();
		commonBooleanRes.setExists(workloadService.saveValidatedAddressApi(addressDetailBean));
		return commonBooleanRes;
	}

}