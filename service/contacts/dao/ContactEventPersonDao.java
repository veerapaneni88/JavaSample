package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.ContactDetailsOutDto;
import us.tx.state.dfps.xmlstructs.outputstructs.StageProgramDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactEventPersonDao Aug 2, 2018- 6:39:12 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ContactEventPersonDao {

	/**
	 * 
	 * Method Name: getContactDetails Method Description: This DAM is a QUERY
	 * join for the CONTACT, EVENT and PERSON tables. See the SELECT statement
	 * for details. ( It is functionally specific to the Contact Detail window.)
	 * 
	 * @param contactDetailsOutDto
	 * @return StageProgramDto
	 */
	public StageProgramDto getContactDetails(ContactDetailsOutDto contactDetailsOutDto);

}