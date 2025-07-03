package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactStageDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactStageDoDto;

import java.util.Date;

public interface ContactOccuredDao {
	/**
	 * 
	 * Method Name: csys15dQueryDao Method Description:fetches the date contact
	 * occurred from contact
	 * 
	 * @param csys15di
	 * @return
	 * @throws DataNotFoundException
	 */
	public ContactStageDoDto csys15dQueryDao(ContactStageDiDto csys15di);
	public ContactStageDoDto consys15dQueryDao(ContactStageDiDto csys15di);

}
