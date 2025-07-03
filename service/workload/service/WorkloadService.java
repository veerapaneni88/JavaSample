package us.tx.state.dfps.service.workload.service;

import java.util.List;
import java.util.TreeMap;

import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.common.web.bean.AddressDetailBean;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.common.request.ApprovalStatusReq;
import us.tx.state.dfps.service.common.request.AssignSaveGroupReq;
import us.tx.state.dfps.service.common.request.CaseMergeUpdateReq;
import us.tx.state.dfps.service.common.request.CitizenStatusFormReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.KinApprovalReq;
import us.tx.state.dfps.service.common.request.MrefStatusFormReq;
import us.tx.state.dfps.service.common.request.PreDisplayPriorityClosureReq;
import us.tx.state.dfps.service.common.request.PriorityClosureReq;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.common.request.RetrvCountyReq;
import us.tx.state.dfps.service.common.request.SearchAssignReq;
import us.tx.state.dfps.service.common.request.SearchStageReq;
import us.tx.state.dfps.service.common.request.ToDoUtilityReq;
import us.tx.state.dfps.service.common.request.TodoListAUDReq;
import us.tx.state.dfps.service.common.request.TodoListDelReq;
import us.tx.state.dfps.service.common.request.TodoListReq;
import us.tx.state.dfps.service.common.request.TodoReq;
import us.tx.state.dfps.service.common.response.ARExtensionRes;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.common.response.ApproverStatusRes;
import us.tx.state.dfps.service.common.response.AssignSaveGroupRes;
import us.tx.state.dfps.service.common.response.CaseMergeUpdateRes;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EventDetailRes;
import us.tx.state.dfps.service.common.response.IDListRes;
import us.tx.state.dfps.service.common.response.KinApprovalRes;
import us.tx.state.dfps.service.common.response.PreDisplayPriorityClosureRes;
import us.tx.state.dfps.service.common.response.PriorityClosureRes;
import us.tx.state.dfps.service.common.response.PriorityClosureSaveRes;
import us.tx.state.dfps.service.common.response.RetrvCountyRes;
import us.tx.state.dfps.service.common.response.SearchAssignRes;
import us.tx.state.dfps.service.common.response.SearchStageRes;
import us.tx.state.dfps.service.common.response.TodoListAUDRes;
import us.tx.state.dfps.service.common.response.TodoListDelRes;
import us.tx.state.dfps.service.common.response.TodoListRes;
import us.tx.state.dfps.service.common.response.WorkloadResponse;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.*;

/**
 * ImpactWebServices - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN13S
 * Class Description: This class is use for retrieving Staff Todo List Mar 21,
 * 2017 - 12:44:36 PM
 */
public interface WorkloadService {

	/**
	 * Service Name: CCMN13S Method Description: This method is used to cThis
	 * service will retrieve the user's supervisor if the ReqFuncCd is
	 * REQ_FUNC_CD_APPROVAL, or it will get the NM STAGE, NM TASK, TASK DUE DT,
	 * PRIMARY WORKER of STAGE if the ReqFuncCd is REQ_FUNC_CD_ASSIGN, or it
	 * will get the information related to the ID TODO specified.
	 * 
	 * @param todoCaseStruct
	 * @param archIPStruct
	 * @return todoListRes
	 */
	TodoListRes StaffToDoList(TodoListReq todoCaseStruct);

	/**
	 * Service Name: CCMN97s Method Description: This service will delete all
	 * rows from the TODO table for the idTodo given as input. This service was
	 * created to allow BLOCK DELETE of TODOs on the Staff Todo List window to
	 * be saved.
	 * 
	 * @param todoListDelReq
	 * @param archIPStruct
	 * @return TodoListDelRes
	 */
	TodoListDelRes StaffToDoListDelete(TodoListDelReq todoListDelReq);

	/**
	 * Service Name: CCMN19s Method Description: This service will Add/Update to
	 * the TODO table, Add to the Approval, Approval Event Link table, Event
	 * table, and Approvers table based on the input request function indicator
	 * 
	 * @param todoListAUDReq
	 * @param archInputDto
	 * @return todoListAUDRes
	 */
	TodoListAUDRes staffToDoListAUD(TodoListAUDReq todoListAUDReq);

	/**
	 * 
	 * Method Description: This Method is implemented in
	 * StaffToDoListServiceImpl. Service Name: CCMN25s
	 * 
	 * @param assignSaveGroupReq
	 * @param archIPStruct
	 * @return TodoListAUDRes
	 */
	AssignSaveGroupRes assignSaveGroup(AssignSaveGroupReq assignSaveGroupReq);

	/**
	 * 
	 * Method Description: This Method will perform the event details search and
	 * populate the records based on the input request. Service Name: CCMN33S
	 * 
	 * @param eventReq
	 * @return EventDetailRes @
	 */

	EventDetailRes getEventDetails(EventReq eventReq);

	/**
	 * 
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * person and a time period. Service Name: CCMN11S
	 * 
	 * @param todoReq
	 * @return List<TodoDto> @
	 */
	List<TodoDto> getTodoDetails(TodoReq todoReq);

	/**
	 * 
	 * Method Description: This Method will retrieve counties linked to a Texas
	 * city. Service Name: CCMN87S
	 * 
	 * @param searchRetrieveCountyReq
	 * @return RetrvCountyRes @
	 */
	RetrvCountyRes getCountyList(RetrvCountyReq searchRetrieveCountyReq);

	/**
	 * 
	 * Method Description: This Method will retrieve all ID_STAGES given a case
	 * or a person. Service Name: CCMN32S
	 * 
	 * @param searchStageReq
	 * @return SearchStageRes
	 * @, InvalidRequestException
	 */
	SearchStageRes getSearchStageList(SearchStageReq searchStageReq);

	/**
	 * 
	 * Method Description:call CaseSummaryPageServiceImple method to work on
	 * CASE_MERGE as action required
	 * 
	 * @param caseMergeUpdateReq
	 * @return CaseMergeUpdateRes
	 */
	CaseMergeUpdateRes CaseMergeUpdate(CaseMergeUpdateReq caseMergeUpdateReq);

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
	ApprovalStatusRes fetchApprovalStatusDtls(ApprovalStatusReq approvalStatusReq);

	/**
	 * 
	 * Method Description: THis Method is used to retrieve Assign page Details.
	 * Tuxedo Service Name:CCMN80S
	 * 
	 * @param assignReq
	 */
	SearchAssignRes getAssignEmpStage(SearchAssignReq searchassignReq);

	/**
	 * 
	 * Method Description: Returns CPS stageIds that has pending investigation
	 * conclusions.
	 *
	 * @param idWorker
	 * @return IDListRes
	 */

	IDListRes getInvCnclPendingStages(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns an array of stage ID's representing the
	 * subset of a passed array of stage ID's that are currently checked out to
	 * MPS
	 *
	 * @param stageIds
	 * @return IDListRes
	 */

	IDListRes getCheckedOutStages(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Optimize workload page query to improve page loading
	 * times. Returns the list of Stage Ids that has A-R as a prior stage
	 *
	 * @param idWorker
	 * @return IDListRes
	 */

	IDListRes getStagesWithARasPriorStage(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns list of AR stages that requires worker
	 * attention
	 *
	 * @param idWorker
	 * @return IDListRes
	 */

	IDListRes getARPendingStages(CommonHelperReq commonHelperReq);

	/**
	 *
	 *
	 * Method Description: This method is used to get data for Pre Display
	 * priority closure by calling Dao query. Tuxedo Service Name:CINT99S Tuxedo
	 * Dam Name: CINT40S, CINV34S, CINT73D
	 * 
	 * @param preDisplayPriorityClosureReq
	 * @return preDisplayPriorityClosureRes
	 */
	PreDisplayPriorityClosureRes getPreDisplayPriorityClosure(
			PreDisplayPriorityClosureReq preDisplayPriorityClosureReq);

	/**
	 *
	 *
	 * Method Description: This Method is to retrieve ideventperson, job class,
	 * personname and employee class for priority closure screen. Service
	 * Name:Priority Closure Ejb Service Tuxedo Dam Name: PriortyClosureDAO
	 * 
	 * @param priorityClosureEjbReq
	 * @return priorityClosureEjbRes
	 */

	PriorityClosureRes getIdEventPersonInfo(PriorityClosureReq priorityClosureEjbReq);

	/**
	 * 
	 * Method Description: Method for Priority Closure Save/Close. This Updates
	 * TODO, STAGE, Incoming Detail, and Creates Event.
	 * 
	 * @param PriorityClosureSaveReq
	 * @return PriorityClosureSaveRes Tuxedo Service Name: CINT21S
	 */
	PriorityClosureSaveRes savePriority(PriorityClosureSaveReq priorityClosureSaveReq);

	/*
	 * Method Description: This Method is check if a Stage is closed or not
	 * 
	 * @param Stage id
	 * 
	 * @return Boolean true or false
	 */
	CommonHelperRes isStageClosed(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: This method will be implemented in
	 * WorkloadServiceImpl. Service Name: KinApproval
	 * 
	 * @param kinApprovalReq
	 * @return KinApprovalRes
	 */
	public KinApprovalRes checkStageCaseID(KinApprovalReq kinApprovalReq);

	/**
	 * Returns Extension request Event object for the given AR stage.
	 * 
	 * @param idStage
	 *            in CommonHelperReq
	 * @return Event
	 */
	ARExtensionRes getARExtensionRequest(CommonHelperReq commonHelperReq);

	/**
	 * Method Description:Returns cd mobile Status given a Workload Case Id to
	 * it.
	 * 
	 * @param idCase
	 *            in CommonHelperReq
	 * @return String
	 */
	CommonHelperRes getCdMobileStatus(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:Returns the role of a person in a stage, provided the
	 * case is in that person's workload. If the person does not have a role, or
	 * the stage is no in their workload, will return empty string (""). Should
	 * always return PRIMARY ("PR") or SECONDARY ("SE").
	 *
	 * @param IdPerson
	 * @param IdStage
	 * @return the role of the person in that stage in their workload.
	 */
	CommonHelperRes getRoleInWorkloadStage(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:This method will fetch all active stages for a person
	 * 
	 * @param PersonID
	 * @return List of all active Stage Id(s)
	 */
	IDListRes getActiveStagesForPerson(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:Check Configuration table ONLINE_PARAMETERS, if Tlets
	 * Check needs to be Enabled or Disabled
	 * 
	 * @return Boolean -- true or false
	 */
	CommonHelperRes disableTletsCheck();

	/**
	 * Method Description: This method is to find if the contact with the
	 * purpose of initial already exist.
	 * 
	 * @paramidStage
	 * @returnBoolean -- true or False
	 */

	CommonHelperRes getContactPurposeStatus(CommonHelperReq commonHelperReq);


	/**
	 * Method Description: This method is to find if there is an approved
	 * contact for the given case id.
	 * 
	 * @paramidCaseContactType
	 * @returnBoolean -- true or False
	 */
	CommonHelperRes isAprvContactInCase(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: Returns an a boolean value based on whether or not
	 * either of the two passed person ID's is tied to a stage currently checked
	 * out to MPS
	 * 
	 * @param idPerson1
	 *            and idPerson2
	 * @returnBoolean -- true or False
	 */
	CommonHelperRes getCheckedOutPersonStatus(CommonHelperReq commonHelperReq);

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
	ApproverStatusRes getApproversStatus(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: getPrimaryWorkerIdForStage Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	CommonHelperRes getPrimaryWorkerIdForStage(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: getAssignedWorkersForStage Method Description: Fetch the
	 * Primary and Secondary Workers assigned to the passed Stage ID
	 * 
	 * @param idStage
	 * @return List<Long>
	 */
	List<Long> getAssignedWorkersForStage(Long idStage);

	/**
	 * AR - Stage progression from AR - FPR - message not displayed This method
	 * will fetch the overall disposition
	 * 
	 * @param idCase
	 *            the stage identifier
	 * @return String disposition code
	 */
	CommonHelperRes getARStageOverallDisposition(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: This Method will retrieve the Risk Assessment Details
	 * as a List for the particular Case by passing stageID, caseId and taskId
	 * as Input.
	 * 
	 * @param eventReq
	 * @return EventDetailRes
	 */
	EventDetailRes getSDMAssmntListDtls(EventReq eventReq);

	/**
	 * Method Description: This service will returns a the event and todo id's
	 * for an approval given an event from the stage
	 * 
	 * @param ToDoUtilityReq
	 * @return ToDoUtilityRes
	 */
	TodoDto getToDoIdForApproval(ToDoUtilityReq toDoUtilityReq);

	/**
	 * Returns idEvent for the given Approval Event.
	 * 
	 * @param idAprvlEvent
	 * 
	 * @return idEvent
	 */
	CommonHelperRes fetchIdEventForIdAprEvent(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: getPrintTaskExists Method Description:Printtask method is
	 * used to verify is a print task exists for the logged in user based on the
	 * idcase , idsateg and idevent.
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	CommonBooleanRes getPrintTaskExists(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: getTodoDtlById Method Description:
	 * 
	 * @param todoId
	 * @return
	 */
	CommonHelperRes getTodoDtlById(Long todoId);

	/**
	 * Method Description: This method is used to retrieve the information for
	 * M-ref Status form by passing IdPerson as input request
	 * 
	 * 
	 * @param MrefStatusFormReq
	 * @return PreFillDataServiceDto
	 */
	PreFillDataServiceDto getMrefStatusForm(MrefStatusFormReq mrefStatusFormReq);

	/**
	 * Method Description: This method is used to retrieve the information for
	 * SIJS Status form by passing IdPerson,cdRegion,cdCitizenship as input
	 * request
	 * 
	 * 
	 * @param CitizenStatusFormReq
	 * @return PreFillDataServiceDto
	 */
	PreFillDataServiceDto getSijsForm(CitizenStatusFormReq citizenStatusFormReq);

	/**
	 * Method Description: This method is used to get the boolean to display the
	 * Select Options for SIJS Status form SIJS Status form by passing IdPerson
	 * 
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public boolean getSijsStatus(Long idPerson);

	/**
	 * Method Description: This method is used to get the count of records for
	 * the given idStage and task code
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean getToDoCount(Long idStage);

	/**
	 * Method Name: saveRejectionApproval Method Description:This method is used
	 * to save the incomplete CCL Rejection check box for CCL Program.
	 * 
	 * @param rejectApprovalDto
	 * @return
	 */
	public void saveRejectionApproval(RejectApprovalDto rejectApprovalDto);

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
	public AssignSaveGroupRes assignSecondary(Long idCase, Long idStage, Long idPerson, Long idUser);

	/**
	 * 
	 * Method Name: sendEmail Method Description:This method is used to send the
	 * email appointment.
	 * 
	 * @param treeMap
	 * @param hostName
	 */
	public void sendEmail(TreeMap<Long, String> treeMap, String hostName);

	/**
	 * Method Name: createCalendarEventForReassignment Method Description:
	 * 
	 * @param idStage
	 * @param idPerson
	 * @param hostName
	 * @param indCurrentStage
	 * @param indReassignment
	 */
	public void createCalendarEventForReassignment(Long idStage, Long idPerson, String hostName, String indCurrentStage,
			String indReassignment);
	
	/**
	 *Method Name:	fetchRevisedVictimDetails
	 *Method Description:To fetch the details for the revised victim id
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	PreDisplayPriorityClosureRes fetchRevisedVictimDetails(PreDisplayPriorityClosureReq preDisplayPriorityClosureReq);

	/**
	 * method to retrieve role based on idStage and idPerson
	 * @param idStage
	 * @param idPerson
	 * @return
	 */
	public String getStagePersRole(Long idStage, Long idPerson);

	/**
	 * Method Name: updateStage Method Description:
	 * 
	 * @param ldIdTodo
	 */
	public void updateStage(List<TodoDto> ldIdTodo);
	
	/**
    /**
     *Method Name:	fetchRevisedChildrenInFacility
     *Method Description:
     *@param preDisplayPriorityClosureReq
     *@return
     */
    PreDisplayPriorityClosureRes fetchRevisedChildrenInFacility(
            PreDisplayPriorityClosureReq preDisplayPriorityClosureReq);

	/**
	 /**
	 *Method Name:	getRevisedChildrenInFacility
	 *Method Description:
	 *@param preDisplayPriorityClosureReq
	 *@return
	 */
	PreDisplayPriorityClosureRes getRevisedChildrenInFacility(
			PreDisplayPriorityClosureReq preDisplayPriorityClosureReq);
	/* Method Name: getTodo Method Description:
	 * 
	 * @param ldIdTodo
	 */
	public List<TodoDto> getTodo(List<Long> ldIdTodo) ;
	
	/**
	 *Method Name:	fetchLatestChildrenInFacility
	 *Method Description:Fetch Latest Children details from the facility
	 *@param idVictimNotificationRsrc
	 *@return
	 */
	List<IntakeNotfChildDto> fetchLatestChildrenInFacility(Long  idVictimNotificationRsrc);

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
	public void hasAppEventCreatedBeforeFBSSReferral(Long idApproval, List<StageProgDto> stageProgDtos);

	/**
	 * Artifact ID: artf140443
	 *Method Name:	isCaseAssignedToPerson
	 *Method Description:checks if a case is assigned to the case worker
	 *@param idPerson
	 *@param idCase
	 *@return
	 */
	boolean isCaseAssignedToPerson(Long idPerson, Long idCase);

	/**
	 *
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * person and a time period. Service Name: CCMN11S
	 *
	 * @param todoReq
	 * @return List<TodoDto> @
	 */
	List<TodoDto> getRCIAlertTodoDetailsByCaseAndStage(TodoReq todoReq);


	/**
	 * Creates Workload alerts for SUB and ADO stage case workers and supervisors, if the alert doesn't exists with
	 * priorstage combination
	 * @param stagePersonValueDto
	 * @param priorStage
	 * @param logonUserId
	 * @param screenedIn
	 */
	public void processPersonMergeWorkloadAlerts(StagePersonValueDto stagePersonValueDto, SelectStageDto priorStage, Long logonUserId, boolean screenedIn);

	/**
	 * Populates the licensing information in priority closure section for intake stage
	 * @param preDisplayPriorityClosureReq
	 * @param idCase
	 * @return
	 */
	PriorityClosureLicensingDto getLicensingInformation(PreDisplayPriorityClosureReq preDisplayPriorityClosureReq, Long idCase);

	public WorkloadResponse getWorkloadHasLoginUserForStageAndCase(Long stageId, Long caseId, Long loginUserId);
	AssignedDto findEMRProgramAdmin(int idUser, String securityRole);

	boolean getExecStaffSecurityForEMR(Long idPerson, String role);

	public boolean saveValidatedAddressApi(AddressDetailBean addressDetailBean);

	public AddressDetailBean getValidatedAddressApi(String guid);
}