package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.EventUpdateInDto;

public interface EventUpdateDao {
	/**
	 * 
	 * Method Name: updateEvent Method Description:Updates event table.
	 * 
	 * @param eventUpdateInDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long updateEvent(EventUpdateInDto eventUpdateInDto);

}