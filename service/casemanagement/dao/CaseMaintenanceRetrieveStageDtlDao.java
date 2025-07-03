package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RetrieveStageInDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceRetrieveStageDtlDao Feb 7, 2018- 5:45:23 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceRetrieveStageDtlDao {
	public void fetchStageDtl(RetrieveStageInDto retrieveStageInDto, RetrieveStageOutDto retrieveStageOutDto);

}
