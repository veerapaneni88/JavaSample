package us.tx.state.dfps.service.legal.dao;

import java.util.List;

import us.tx.state.dfps.service.legal.dto.TodoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * ToDoEventDao Sep 7, 2017- 4:40:27 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ToDoEventDao {

	/**
	 * Method Name: fetchToDoListForEvent Method Description: This method Calls
	 * the DAO to fetch the information from Event.
	 * 
	 * @param idEvent
	 * @return List @
	 */
	public List fetchToDoListForEvent(long idEvent);

	/**
	 * Method Name: fetchOpenTodoForStage Method Description:Fetch the IdTodo of
	 * the open Next review Todo task for the stage. It should have only one
	 * Todo in the list.
	 * 
	 * @param idStage
	 * @return List<TodoDto>
	 */
	public List<TodoDto> fetchOpenTodoForStage(Long idStage);

}
