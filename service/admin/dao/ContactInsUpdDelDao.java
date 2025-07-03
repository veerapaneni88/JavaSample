package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.ContactInsUpdDelInDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Csys07dDao
 * Aug 8, 2017- 6:17:12 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ContactInsUpdDelDao {

	/**
	 * 
	 * Method Name: updateContactAndContactNarrative Method Description: This
	 * method will perform UPDATE on CONTACT and NARRATIVE.
	 * 
	 * @param pInputDataRec
	 * @throws DataNotFoundException
	 */
	public void updateContactAndContactNarrative(ContactInsUpdDelInDto pInputDataRec);
}
