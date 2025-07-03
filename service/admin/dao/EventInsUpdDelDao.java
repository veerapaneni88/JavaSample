package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EventInsUpdDelOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Ccmn46dDao
 * Aug 6, 2017- 7:44:02 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface EventInsUpdDelDao {

	/**
	 * 
	 * Method Name: ccmn46dAUDdam Method Description: This method will perform
	 * Add, Update and Delete on Event.
	 * 
	 * @param iCcmn46diDto
	 * @return EventInsUpdDelOutDto
	 */
	EventInsUpdDelOutDto ccmn46dAUDdam(EventInsUpdDelInDto iCcmn46diDto);

	/**
	 * 
	 * Method Name: checkEventByLastUpdate Method Description: Method will check
	 * event by event id and dtLastUpdate
	 * 
	 * @param iCcmn46diDto
	 * @return
	 */
	public Event checkEventByLastUpdate(EventInsUpdDelInDto iCcmn46diDto);
}
