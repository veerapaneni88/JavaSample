package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.UpdateEventiDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Demote the status of all previously captured approval related events to
 * Complete Aug 8, 2017- 10:40:28 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface UpdateEventDao {
	/**
	 * Method Name: updateEvent Method Description: This method updates event
	 * status
	 * 
	 * @param updateEventiDto
	 * @return no. of rows modified
	 */
	public Integer updateEvent(UpdateEventiDto updateEventiDto);
}
