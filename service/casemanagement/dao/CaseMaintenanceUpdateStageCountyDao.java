package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.StageCountyUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.StageCountyUpdateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateStageCountyDao Feb 7, 2018- 5:46:44 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceUpdateStageCountyDao {
	public void updateStageCounty(StageCountyUpdateInDto stageCountyUpdateInDto,
			StageCountyUpdateOutDto stageCountyUpdateOutDto);

}
