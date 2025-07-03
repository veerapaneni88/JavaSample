package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.SaveStageInDto;
import us.tx.state.dfps.service.casepackage.dto.SaveStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateStageNameDao Feb 7, 2018- 5:46:53 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceUpdateStageNameDao {
	public void saveStage(SaveStageInDto saveStageInDto, SaveStageOutDto saveStageOutDto);

}
