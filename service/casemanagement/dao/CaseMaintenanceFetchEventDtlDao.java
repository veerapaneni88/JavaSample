package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.EventRtrvTaskInDto;
import us.tx.state.dfps.service.casepackage.dto.EventRtrvTaskOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceFetchEventDtlDao Feb 7, 2018- 5:45:02 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceFetchEventDtlDao {
	public void fetchEventDtl(EventRtrvTaskInDto eventRtrvTaskInDto, EventRtrvTaskOutDto eventRtrvTaskOutDto);

}
