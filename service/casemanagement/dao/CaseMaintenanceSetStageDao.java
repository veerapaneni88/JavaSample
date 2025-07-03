package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.UpdateStagePersonLinkInDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateStagePersonLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceSetStageDao Feb 7, 2018- 5:45:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CaseMaintenanceSetStageDao {
	public void setStage(UpdateStagePersonLinkInDto updateStagePersonLinkInDto,
			UpdateStagePersonLinkOutDto updateStagePersonLinkOutDto);

}
