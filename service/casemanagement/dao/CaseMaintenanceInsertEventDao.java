package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.casepackage.dto.UpdateEventInDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceInsertEventDao Feb 7, 2018- 5:45:19 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceInsertEventDao {
	public void updateEvent(UpdateEventInDto updateEventInDto, UpdateEventOutDto updateEventOutDto);

}
