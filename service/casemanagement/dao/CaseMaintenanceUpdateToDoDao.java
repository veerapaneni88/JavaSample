package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.CompleteToDoInDto;
import us.tx.state.dfps.service.casepackage.dto.CompleteToDoOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateToDoDao Feb 7, 2018- 5:46:58 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CaseMaintenanceUpdateToDoDao {
	public void updateTodo(CompleteToDoInDto completeToDoInDto, CompleteToDoOutDto completeToDoOutDto);

}
