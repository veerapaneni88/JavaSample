package us.tx.state.dfps.service.workload.service;

import java.text.ParseException;
import java.util.List;

import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Implements
 * the methods in TodoBean Sep 6, 2017- 7:43:26 PM © 2017 Texas Department of
 * Family and Protective Services
 * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 * 
 */
public interface ToDoService {

	/**
	 * Method Name: updateToDo Method Description: This method update TODO
	 * Table.
	 * 
	 * @param todoDto
	 * @return TodoDto @
	 */
	public TodoDto updateToDo(TodoDto todoDto);

	/**
	 * Method Name: getSupervisorForStage Method Description: This function
	 * returns the Supervisor for the given Stage.
	 * 
	 * @param idStage
	 * @return long @
	 */
	public long getSupervisorForStage(long idStage);

	/**
	 * Method Name: selectToDosForStage Method Description: This function
	 * retrieves Open Tasks / Alerts of the given Type for the given Stage. If
	 * cdTodoInfoList is null, returns all Todos/Alerts for the Stage.
	 * 
	 * @param idStage
	 * @param cdTodoInfoList
	 * @return List<TodoDto> @
	 */
	public List<TodoDto> selectToDosForStage(long idStage, List<String> cdTodoInfoList);

	/**
	 * Method Name: updateToDos Method Description: Manual Stage Progression INT
	 * to A-R This function updates Todo Table.
	 *
	 * @param toDosDto
	 * @return long[]
	 */
	public long[] updateToDos(List<TodoDto> toDosDto);

	public Long deleteToDoByEvent(Long idEvent);

	public Long deleteTodoListByEventIdAndTaskCode(Long idEvent);

  public void deleteTodoByIdTodo(Long idTodo);

	/**
	 * Method Name: deleteToDosForStage Method Description: This function
	 * deletes Tasks / Alerts of the given Type for the given Stage.
	 *
	 * @param idStage
	 * @param cdTodoInfoList
	 * @return long
	 */
	public long deleteToDosForStage(long idStage, List<String> todoInfoList);

	/**
	 * Method Name: endDateInvAlerts Method Description: This function will be
	 * called by the Approval of Investigation conclusion. It will end date all
	 * the Alerts/Tasks for the workers and supervisors reminding them to
	 * complete Investigation conclusion.
	 * 
	 * @param idInvStage
	 * @return long
	 */
	public long endDateInvAlerts(long idInvStage);

	/**
	 * Method Name: createInvestigationAlerts Method Description: This function
	 * creates 30-Day / 37-Day INV Caseworker Alerts.
	 * 
	 * @param idCase
	 * @param idIntakeStage
	 * @param idLoggedInWorker
	 * @return long
	 * @throws ParseException
	 */
	public long createInvestigationAlerts(long idCase, long idIntakeStage, long idLoggedInWorker);
	
	/**
     * Method Name: createRCLAlert Method Description: Creates alerts for Child Sexual Aggression, trafficking, sexual behavior, Alleged Aggressor of Child Sexual Aggression
     * for the sub worker and the supervisor  
     * added for artf129782
     * @param createRCLAlertReq
     * @return long
     * 
     */
	public long createRCLAlerts(long idStage, long idEvent,long assignedTo, String description, long idCreatedPerson);

	/**
	 * Method Name: changeInvAlertDueDates Method Description: This function
	 * changes the Due date for Caseworker and Supervisor Alerts if the
	 * Extension Request is Approved
	 * 
	 * @param idInvStage
	 * @param approvalLen
	 * @return long
	 * 
	 */
	public Long changeInvAlertDueDates(Long idInvStage, String approvalLen);

	/**
	 * Method Name: formatUserInitials Method Description:
	 * 
	 * @param personFullName
	 * @return
	 */
	public String formatUserInitials(String personFullName);

	public long[] updateAlertToDos(List<TodoDto> toDosDto);

	public void getTodoCaseDueDtList(List<TodoDto> toDosDto);
	public long createAlert(TodoDto todoDto);

    boolean checkGudApprovels(Long idPerson);

	boolean checkCaseClosureApprover(Long idPerson);
}
