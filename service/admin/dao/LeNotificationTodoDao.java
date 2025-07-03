/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Feb 22, 2018- 11:21:39 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.LeNotificationTodoDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 22, 2018- 11:21:39 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface LeNotificationTodoDao {

	/**
	 * 
	 * Method Name: getTodoByIdStageAndTask Method Description:This method will
	 * get TODO table by Stage Id and Task.
	 * 
	 * @param pInputDataRec
	 * @return List<Cint58doDto>
	 * @throws DataNotFoundException
	 */
	public List<LeNotificationTodoDto> getTodoByIdStageAndTask(LeNotificationTodoDto leNotificationTodoDto);

}
