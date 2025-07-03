package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.UpdateToDoDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for calling UpdateToDoDaoImpl. Aug 4, 2017- 12:14:46 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface UpdateToDoDao {
	/**
	 * 
	 * Method Name: updateTODOEvent Method Description:This method updates ToDo
	 * Completed Date by calling Cinv43dDaoImpl
	 * 
	 * @param updateToDoDto
	 */
	public String updateTODOEvent(UpdateToDoDto updateToDoDto);

	/**
	 * Method Name: completeTodo Method Description:Updates the
	 * DateToDoCompleted field to current date
	 * 
	 * @param updateToDoDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long completeTodo(UpdateToDoDto updateToDoDto);

	public void completeTodoEvent(Long idEvent);

}
