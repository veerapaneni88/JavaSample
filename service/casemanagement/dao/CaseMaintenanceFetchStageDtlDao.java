package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.RtrvStageInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchStageDtlDao Feb 7, 2018- 5:45:11 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceFetchStageDtlDao {
	public void fetchStageDtl(RtrvStageInDto rtrvStageInDto, RtrvStageOutDto rtrvStageOutDto);

}
