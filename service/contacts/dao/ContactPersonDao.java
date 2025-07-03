package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactPersonDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactPersonDetailsDoDto;

public interface ContactPersonDao {
	/**
	 * 
	 * Method Name: getPersonDetailsForEvent Method Description:gets the person
	 * name & id
	 * 
	 * @param csys12di
	 * @return
	 * @throws DataNotFoundException
	 */
	public ContactPersonDetailsDoDto getPersonDetailsForEvent(ContactPersonDiDto csys12di);

}
