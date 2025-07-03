package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.GetTaskInDto;
import us.tx.state.dfps.service.casepackage.dto.GetTaskOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchTaskDao Feb 7, 2018- 5:45:15 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CaseMaintenanceFetchTaskDao {
	public void fetchTaskDtl(GetTaskInDto getTaskInDto, GetTaskOutDto getTaskOutDto);

}
