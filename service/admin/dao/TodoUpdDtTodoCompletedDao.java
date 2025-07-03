package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cinv43dDao
 * Aug 4, 2017- 12:14:46 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface TodoUpdDtTodoCompletedDao {

	/**
	 * 
	 * Method Name: updateTODOEvent Method Description: This method will update
	 * DT_TODO_COMPLETED for TODO table.
	 * 
	 * @param pInputDataRec
	 * @
	 */
	public void updateTODOEvent(TodoUpdDtTodoCompletedInDto pInputDataRec);
}
