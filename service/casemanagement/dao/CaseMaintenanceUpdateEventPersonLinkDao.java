package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.EventPersonLinkUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.EventPersonLinkUpdateOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceUpdateEventPersonLinkDao Feb 7, 2018- 5:46:40 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceUpdateEventPersonLinkDao {
	public void updateEventPersonLink(EventPersonLinkUpdateInDto eventPersonLinkUpdateInDto,
			EventPersonLinkUpdateOutDto eventPersonLinkUpdateOutDto);

}
