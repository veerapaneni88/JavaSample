package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.EventPersonLinkDeleteDto;

public interface DeleteEventPersonLinkDao {
	/**
	 * 
	 * Method Name: deleteEventPersonLink Method Description: This DAM deletes
	 * all rows from the EVENT_PERSON_LINK table based on an ID-EVENT.
	 * 
	 * @param eventPersonLinkDeleteDto
	 * @return long @
	 */
	public long deleteEventPersonLink(EventPersonLinkDeleteDto eventPersonLinkDeleteDto);

}
