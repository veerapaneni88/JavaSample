package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceSelectStageDao Feb 7, 2018- 5:45:38 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceSelectStageDao {
	public void selectStage(StageRtrvInDto stageRtrvInDto, StageRtrvOutDto stageRtrvOutDto);

}
