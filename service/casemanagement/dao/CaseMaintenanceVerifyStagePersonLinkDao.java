package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkRtrvOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceVerifyStagePersonLinkDao Feb 7, 2018- 5:47:02 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceVerifyStagePersonLinkDao {
	public void verifyStagePersonLink(StagePersonLinkRtrvInDto stagePersonLinkRtrvInDto,
			StagePersonLinkRtrvOutDto stagePersonLinkRtrvOutDto);

}
