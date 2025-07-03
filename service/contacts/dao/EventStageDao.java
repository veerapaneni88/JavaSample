package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.EventUpdateStatusDto;

public interface EventStageDao {
	/**
	 * 
	 * Method Name: updateEvent Method Description:update event table using
	 * stage link table.
	 * 
	 * @param eventUpdateStatusDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long updateEvent(EventUpdateStatusDto eventUpdateStatusDto);
}
