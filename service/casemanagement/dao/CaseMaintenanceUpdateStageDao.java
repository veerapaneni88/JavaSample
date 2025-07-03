package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.StagePersonLinkUpdateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateStageDao Feb 7, 2018- 5:46:48 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceUpdateStageDao {
	public void updateStagePersonLink(StagePersonLinkUpdateInDto stagePersonLinkUpdateInDto,
			StagePersonLinkUpdateOutDto stagePersonLinkUpdateOutDto);

}
