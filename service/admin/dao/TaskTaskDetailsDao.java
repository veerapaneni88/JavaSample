package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.TaskInDto;
import us.tx.state.dfps.service.admin.dto.TaskOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for TaskTaskDetailsDaoImpl Aug 11, 2017- 1:38:10 AM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface TaskTaskDetailsDao {

	/**
	 * 
	 * Method Name: getTaskDtls Method Description: This method will get data
	 * from TASK table.
	 * 
	 * @param pInputDataRec
	 * @return List<TaskOutDto> @
	 */
	public List<TaskOutDto> getTaskDtls(TaskInDto pInputDataRec);
}
