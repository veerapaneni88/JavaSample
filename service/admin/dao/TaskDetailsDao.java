package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.TaskdiDto;
import us.tx.state.dfps.service.admin.dto.TaskdoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Ccmn82dDao
 * Aug 11, 2017- 1:38:10 AM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface TaskDetailsDao {
	public List<TaskdoDto> getTaskDtls(TaskdiDto pInputDataRec);

}
