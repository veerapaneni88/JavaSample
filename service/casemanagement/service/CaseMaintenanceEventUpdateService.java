package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.casepackage.dto.PostEventInDto;
import us.tx.state.dfps.service.casepackage.dto.PostEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceEventUpdateService Feb 7, 2018- 5:56:27 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CaseMaintenanceEventUpdateService {
	public void postEvent(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto);

	public void fetchEvent(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto);

	public void updateEvent(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto);

	public void updateEventPersonLink(PostEventInDto postEventInDto, PostEventOutDto postEventOutDto);

	public PostEventOutDto eventAUD(PostEventInDto postEventInDto);

}
