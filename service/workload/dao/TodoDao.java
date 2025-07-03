package us.tx.state.dfps.service.workload.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.request.CaseTodoReq;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.common.request.ToDoUtilityReq;
import us.tx.state.dfps.service.common.request.TodoReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.dto.TodoInfoCommonDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN11S Class
 * Description: Todo DAO Interface Mar 23, 2017 - 3:55:39 PM
 */

public interface TodoDao {

	/**
	 * 
	 * Method Description: This Method will retrieve all the Todos for a certain
	 * person and a time period. Dam Name: CCMN17D
	 * 
	 * @param todoReq
	 * @return List<TodoDto> , DataNotFoundException
	 */
	public List<TodoDto> getTodoDetails(TodoReq todoReq) throws DataNotFoundException;

	/**
	 * 
	 * Method Description: Method is implemented in TodoDaoImpl to get Todo
	 * details Service Name: CCMN13S
	 * 
	 * @param archIPStruct
	 * @param ldIdTodo
	 * @return TodoDto
	 * @throws Exception
	 */
	public TodoDto getTodoDtlsById(Long ldIdTodo);

	/**
	 * 
	 * Method Description: This Method is used to retrieve todo List based on
	 * ID_Todo. Service Name: CCMN19S and CCMN35S DAM Name - CCMN43D
	 * 
	 * @param todoDto
	 * @param archIPStruct
	 * @return TodoDto
	 */
	public TodoDto todoAUD(TodoDto todoDto, ServiceReqHeaderDto serviceReqHeaderDto);

	/**
	 * 
	 * Method Description: Method is implemented in TodoDaoImpl to get Todo
	 * details Service Name: CCMN13S
	 * 
	 * @param ldIdTodo
	 * @return Todo
	 * @throws Exception
	 */
	public String todoDeleteById(List<Long> ldIdTodo);

	/**
	 * 
	 * Method Description: The purpose of this dam (ccmn42dQUERYdam) is to
	 * retrieve records for display in the ListBox of the 'Case To Do
	 * List'window (ccmn31w.win). The NM_PERSON_FULL is retrieved from the
	 * PERSON table for the ID_TODO_PERS_ASSIGNED. The initials (First, Middle,
	 * Last) of the person are extracted from the NM_PERSON_FULL's that are
	 * retrieved (via the initials42 function). Associated with a particular
	 * case (ID_TODO_CASE), and with a DT_TODO_DUE that falls between the dates
	 * passed into this DAM (From Date: pInputDataRec->dtDtTodoDue[0], and TO
	 * Date: pInputDataRec->dtDtTodoDue[1]). and with ( (T.CD_TODO_TYPE = 'A')
	 * OR (T.DT_TODO_COMPLETED IS NULL) ). The retrieved records are ORDERed BY
	 * ASSIGNED, DATE, or CREATOR, based on the
	 * pInputDataRec->ArchInputStruct.cReqFuncCd value. Service Name: CCMN12S
	 * DAM: CCMN42D
	 * 
	 * @param caseTodoReq
	 * @return List<TodoDto>
	 * 
	 */

	List<TodoDto> getTodoList(CaseTodoReq caseTodoReq);

	/**
	 * 
	 * Method Description:getTodoBypersonId
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * 
	 */

	public List<Long> getTodoBypersonId(Long personId) throws DataNotFoundException;
	
	
	
	/**
	 * 
	 * Method Description: legacy service name - CCMN25s DAM Name : CCMN99D
	 * 
	 * @param idPerson
	 * @param personByIdTodoPersAssigned
	 * @return String
	 * 
	 */
	public List<TodoDto> getTodoUpdateDel(Long idPerson, Long personByIdTodoPersAssigned, Long uIdStage,
			Date todoCompleteddt);

	/**
	 * Method Description:This dam was written specifically for intake so that
	 * the service will be able to find the LE Notification todo. The only
	 * information available will be the cd task and the id stage
	 * 
	 * Service Name: CCMN88S, DAM Name: CINT58D
	 * 
	 * @param idTodoStage
	 * @param cdTodTask
	 * @return
	 * @throws DataNotFoundException
	 * 
	 */
	public List<TodoDto> getTodoByTodoStageIdTask(Long idTodoStage, String cdTodTask) throws DataNotFoundException;

	/**
	 * This DAM will perform a full row retrieval from the TODO_INFO table.
	 * 
	 * Service Name: CSUB40U, DAM Name: CSES08D
	 * 
	 * @param cdTodoInfo
	 * @return
	 * 
	 */
	public TodoInfoCommonDto getTodoInfoByTodoInfo(String cdTodoInfo);

	/**
	 * 
	 * Method Description: Method for Priority Closure Save/Close. This
	 * retrieves Count of LE notifications.
	 * 
	 * 
	 * @param PriorityClosureSaveReq
	 *            Todo
	 * @return
	 * 
	 * 		Tuxedo Service Name: CINT21S
	 */
	public Long getTodobyStageTask(PriorityClosureSaveReq priorityClosureSaveReq);

	/**
	 * 
	 * Method Description: Method for Priority Closure Save/Close. This Updates
	 * Todo table.
	 * 
	 * 
	 * @param PriorityClosureSaveReq
	 *            Todo
	 * @return
	 * 
	 * 		Tuxedo Service Name: CINT21S
	 */

	public void updateTodoDate(PriorityClosureSaveReq priorityClosureSaveReq, Todo todo);

	/**
	 * 
	 * Method Description: Method for get the Contact Count for Priority
	 * Closure.
	 * 
	 * 
	 * @param PriorityClosureSaveReq
	 * @return Long count
	 * 
	 *         Tuxedo Service Name: CINT21S
	 */
	public Long getContactCount(PriorityClosureSaveReq priorityClosureSaveReq);

	/**
	 * This dam is used by the stage progression CloseOpenStage function
	 * (ccmn03u) to retrieve two ID's (ID_EVENT and ID_TODO) and three
	 * timestamps (one from the EVENT table (ID_EVENT), one from the CONTACT
	 * table (ID_EVENT), and one from the TODO table (ID_TODO)) for a particular
	 * ID_STAGE (the old stage). There could be none, or sets of these five
	 * numbers returned (two ID's and three timestamps). If there are multiple
	 * sets, it is for CD_CONTACT_TYPE = 'CMST', 'CS45' or 'CS60'.
	 * 
	 * Service Name : CCMN03U, DAM Name : CCMNH0D
	 * 
	 * @param idStage
	 * @return
	 * 
	 */
	public List<TodoDto> getTodoByStageId(Long idStage);

	public Todo getTodoDetailById(Long idTodo);

	public List<TodoDto> fetchToDoListForEvent(Long idEvent);

	/**
	 * Method Description: This service will returns a the event and todo id's
	 * for an approval given an event from the stage
	 * 
	 * @param ToDoUtilityReq
	 * @return ToDoUtilityRes
	 * 
	 */
	public TodoDto getToDoIdForApproval(ToDoUtilityReq toDoUtilityReq);

	/**
	 * Delete all todos for the caseid passed
	 * 
	 * @param caseID
	 * @return
	 */
	public String toDoDeleteByCaseID(long caseID);

	/**
	 * 
	 * Method Name: getPrintTaskExistsSql Method Description: this method is
	 * used to get check if the print task exsts for the logged in person for a
	 * given case , satge and event .
	 * 
	 * @param idCase
	 * @param idStage
	 * @param idEvent
	 * @return
	 * 
	 */
	public CommonBooleanRes getPrintTaskExistsSql(Long idCase, Long idStage, Long idEvent);

	/**
	 * Method Name: getPersonFullName Method Description: This method returns
	 * full name for given person ID. EJB Name : ToDoBean.java
	 * 
	 * @param idPerson
	 * @return String
	 */
	public String getPersonFullName(Long idPerson);

	/**
	 * Method Name: selectToDosForEvent Method Description:This function
	 * retrieves Open Tasks / Alerts of the given Type for the given Event.
	 * 
	 * @param idContactEvt
	 * @return List<TodoDto>
	 * @throws DataNotFoundException
	 */
	public List<TodoDto> selectToDosForEvent(Long idContactEvt) throws DataNotFoundException;

	/**
	 * Method Name: updateToDosDao Method Description: Manual Stage Progression
	 * INT to A-R This function updates Todo Table. EJB Name : ToDoBean.java
	 * 
	 * @param toDosDto
	 * @return long[]
	 * 
	 */
	public long[] updateToDosDao(List<TodoDto> toDosDto);

	/**
	 * Method Name: selectToDosForStage Method Description: This function
	 * retrieves Open Tasks / Alerts of the given Type for the given Stage. If
	 * cdTodoInfoList is null, returns all Todos/Alerts for the Stage. EJB Name
	 * : ToDoBean.java
	 * 
	 * @param idInvStage
	 * @param cdTodoInfoList
	 * @return List<TodoDto>
	 */
	public List<TodoDto> selectToDosForStage(Long idInvStage, List<String> cdTodoInfoList);

	/**
	 * Method Name: updateTodo Method Description: This method update TODO
	 * Table. EJB Name : ToDoBean.java
	 * 
	 * @param todoLists
	 * @return long
	 */
	public Long updateTodo(TodoDto todoLists);

	/**
	 * Method Name: getSupervisorForStage Method Description: This function
	 * returns the Supervisor for the given Stage. EJB Name : ToDoBean.java
	 * 
	 * @param idStage
	 * @return long
	 */
	public long getSupervisorForStage(long idStage);

	/**
	 * Method Name: isTodoExists Method Description: This method checks if
	 * Active Todo Exists for the given user.
	 * 
	 * @param idSvcAuthEvent
	 * @param cdTodoSaTwcTrnsUpdate
	 * @param userId
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isTodoExists(Long idSvcAuthEvent, String cdTodoSaTwcTrnsUpdate, Long userId);

	/**
	 * Method Name: deleteTodosForStage Method Description: This function
	 * deletes Tasks / Alerts of the given Type for the given Stage. EJB Name :
	 * ToDoBean.java
	 * 
	 * @param idStage
	 * @param cdTodoInfoList
	 * @return long
	 * 
	 */
	public long deleteTodosForStage(long idStage, List<String> todoInfoList);

	/**
	 * MethodName: getStage MethodDescription: get stage for given idStage
	 * 
	 * @param idStage
	 * @return
	 */
	public Stage getStage(long idStage);

	/**
	 * MethodName: isCPSSDMInvSafetyAssmt MethodDescription:
	 * 
	 * @param DtStageStart
	 * @return
	 */
	public boolean isCPSSDMInvSafetyAssmt(Date DtStageStart);

	/**
	 * MethodName:getIncomingCallDateForIntake MethodDescription:
	 * 
	 * @param stageId
	 * @return
	 */
	public Date getIncomingCallDateForIntake(long stageId);

	/**
	 * MethodName: deleteIncompleteTodos MethodDescription: This method deletes
	 * all the records from TO DO-table for a stage id and DT_TODO_COMPLETED is
	 * null
	 * 
	 * @param idStage
	 *            the stage identifier
	 */
	public long deleteIncompleteTodos(int idFromStage);

	/**
	 * Method Name: deleteTodosForACase Method Description: This method deletes
	 * all the records from TO DO-table for a case. Usually called when a case
	 * is closed
	 * 
	 * @param idCase
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteTodosForACase(Long idCase) throws DataNotFoundException;

	/**
	 * Method isTodoCcL Method DEscription: This method gives the count of
	 * records with the task code 2560 and returns true if the count is zero
	 * else returns false.
	 * 
	 * @param idStage
	 *            return boolean
	 */

	public Boolean isTodoCCL(Long idStage) throws DataNotFoundException;

	/**
	 * Method Name: deleteTodosForAEvent Method Description: This method deletes
	 * all the records from TO DO-table for a event. Usually called when
	 * approval event is deleted
	 * 
	 * @param idEvent
	 * @return Long
	 * 
	 */
	public Long deleteTodosForAEvent(Long idEvent);

	public Long deleteTodoListByEventId(Long idEvent);

	public Long deleteTodoListByEventIdAndTaskCode(Long idEvent);

	/**
	 * Method Name: updateToDoEvent Method Description: This method will update
	 * event on the todo table
	 * 
	 * @param cdTask
	 * @param idStage
	 * @param idEvent
	 */
	public void updateToDoEvent(String cdTask, Long idStage, Long idEvent);
	
	public void deleteTodo(Long idToDo) ;
	
	/**
	 * Method Name: updateToDoTaskDue Method Description:This method will update
	 * the todoTaskdue date for ScheduledCourtDate filed value in legal action
	 * screen. Added this method for warranty defect 11779
	 * 
	 * @param idTodo
	 * @param toDoTaskDue
	 */
	public void updateToDoTaskDue(Long idTodo, Date toDoTaskDue);

	public void completeToDoStageClosure(Long idStage);

	public List<TodoDto> getRCIAlertTodoDetails(TodoReq todoReq);

	public long[] updateAlertToDos(List<TodoDto> toDosDto);

	public Long findSupervisorAlert(Long idWorkerAlert);

	public String deleteRCIAlerts(long stageId);

	public void enhanceTodoListWithExtensions(List<TodoDto> toDosDto);

	/**
	 * Deletes alerts based stage and cd_task
	 * @param stageId
	 * @param taskCode
	 * @return
	 */
	public String deleteAlertsByStageAndTask(Long stageId,String taskCode);

	public List<TodoDto> getTasksByStageIdTask(Long idTodoStage, String cdTodTask) throws DataNotFoundException;


	public List<TodoDto> getTasksByCaseAndTask(Long idCase, String taskCode);

	public void deleteAlertsByCaseAndTask(Long idCase, String taskCode);

	public void deleteTodosByTaskAndCase(Long idCase, String taskCode);

	boolean checkForGdnRefAprvByPersonId(Long idPerson);

    boolean isCaseClosureApprover(Long idPerson);

	public void saveorUpdate(Todo todo);
}
