package us.tx.state.dfps.service.contacts.service;

import us.tx.state.dfps.xmlstructs.inputstructs.ContactAUDDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ChildContactDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * Interface for functions required for implementing ContactDetailSave
 * functionality Nov 8, 2017- 4:18:54 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ContactDetailSaveService {

	/**
	 * 
	 * Method Name: audContactDetailRecord Method Description:Save/Save And
	 * Submit Service for Contact Detail.(CSYS07S)
	 * 
	 * @param contactAUDDto
	 * @return ChildContactDto
	 */
	public ChildContactDto audContactDetailRecord(ContactAUDDto contactAUDDto);

}