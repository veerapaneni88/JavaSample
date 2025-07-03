package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.DeleteTodoDto;

public interface TodoDeleteDao {
	/**
	 * 
	 * Method Name: deleteTodo Method Description:delete Todo by idtodoevent
	 * 
	 * @param deleteTodoDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long deleteTodo(DeleteTodoDto deleteTodoDto);
}
