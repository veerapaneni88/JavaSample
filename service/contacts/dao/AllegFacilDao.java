package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.AllegationFacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilAllegPersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AllegFacilDao Aug 2, 2018- 6:29:27 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface AllegFacilDao {
	/**
	 * 
	 * Method Name: getAllegationFacilAllegPerson Method Description: This
	 * method fetches the date from FACIL_ALLEG
	 * 
	 * @param allegationFacilAllegPersonDto
	 * @return FacilAllegPersonDto
	 * @throws DataNotFoundException
	 */
	public FacilAllegPersonDto getAllegationFacilAllegPerson(
			AllegationFacilAllegPersonDto allegationFacilAllegPersonDto);

}
