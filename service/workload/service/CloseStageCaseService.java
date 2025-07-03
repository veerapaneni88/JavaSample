package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.common.dto.CloseStageCaseInputDto;
import us.tx.state.dfps.common.dto.CloseStageCaseOutputDto;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN02U Class
 * Description: CloseStageCase Common Function Apr 10, 2017 - 10:51:33 AM
 */

public interface CloseStageCaseService {

	/**
	 * This shared library function provides the necessary updates required to
	 * close a stage. If a case and a situation are associated with the stage,
	 * and there are no other open stages associated with the case, the
	 * situation and the case are also closed.
	 * 
	 * Service Name : CCMN02U
	 * 
	 * @param closeStageCaseInputDto
	 * @return @
	 */
	public CloseStageCaseOutputDto closeStageCase(CloseStageCaseInputDto closeStageCaseInputDto);
}
