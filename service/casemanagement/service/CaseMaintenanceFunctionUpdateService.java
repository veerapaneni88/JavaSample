package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.casepackage.dto.CheckStageEventInDto;
import us.tx.state.dfps.service.casepackage.dto.CheckStageEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFunctionUpdateService Feb 7, 2018- 5:56:18 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceFunctionUpdateService {
	public void checkStageEventStatus(CheckStageEventInDto checkStageEventInDto,
			CheckStageEventOutDto checkStageEventOutDto);

	public void retrieveStage(CheckStageEventInDto checkStageEventInDto, CheckStageEventOutDto checkStageEventOutDto,
			boolean pbStageIsClosed);

	public CheckStageEventOutDto updateFunctionalTable(CheckStageEventInDto checkStageEventInDto);

}
