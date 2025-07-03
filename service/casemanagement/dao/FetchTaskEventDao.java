package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.GetEventInDto;
import us.tx.state.dfps.service.casepackage.dto.GetEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FetchTaskEventDao Feb 7, 2018- 5:47:12 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface FetchTaskEventDao {
	public void fetchTaskEvent(GetEventInDto getEventInDto, GetEventOutDto getEventOutDto);

}
