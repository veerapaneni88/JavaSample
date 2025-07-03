package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.ContactFieldDiDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ContactFieldDoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactFieldDao Nov 1, 2017- 5:23:49 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface ContactFieldDao {

	/**
	 * Method Name: getContactDetails Method Description:This populates a number
	 * of fields about the contact. If it gets a SQL_NOT_FOUND error, it will
	 * throw it.
	 * 
	 * @param contactFieldDiDto
	 * @return ContactFieldDoDto
	 */
	public ContactFieldDoDto getContactDetails(ContactFieldDiDto contactFieldDiDto);
}
