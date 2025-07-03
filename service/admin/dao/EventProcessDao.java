package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventInputDto;
import us.tx.state.dfps.service.admin.dto.EventOutputDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:EventProcessDao Aug 6, 2017- 7:44:02 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface EventProcessDao {
	/**
	 * 
	 * Method Name: ccmn46dAUDdam Method Description:This method is
	 * update,delete and insert in event table.
	 * 
	 * @param eventInputDto
	 * @return EventOutputDto
	 * @throws DataNotFoundException
	 */
	public EventOutputDto ccmn46dAUDdam(EventInputDto eventInputDto);

	/**
	 * 
	 * Method Name: updateEvent Method Description:this method updates event
	 * table
	 * 
	 * @param eventInputDto
	 * @throws DataNotFoundException
	 */
	public void updateEvent(EventInputDto eventInputDto);

	/**
	 * 
	 * Method Name: deleteEvent Method Description:this method deletes the event
	 * .
	 * 
	 * @param eventInputDto
	 * @throws DataNotFoundException
	 */
	public void deleteEvent(EventInputDto eventInputDto);

	/**
	 * 
	 * Method Name: saveEvent Method Description:This method insert into event
	 * table.
	 * 
	 * @param eventInputDto
	 * @return
	 * @throws DataNotFoundException
	 */
	public Long saveEvent(EventInputDto eventInputDto);
}
