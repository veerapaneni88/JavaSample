package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.ContactRtrvInDto;
import us.tx.state.dfps.service.admin.dto.ContactRtrvOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for ContactRtrvDao Sep 27, 2017- 11:10:57 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ContactRtrvDao {

	/**
	 * Method Name: getFirstEREVContactDateForStage Method Description:Performs
	 * a Select Count to determine if any Request for Review Contacts have been
	 * recorded.
	 * 
	 * @param contactRtrvInDto
	 * @return ContactRtrvOutDto
	 * @throws DataNotFoundException
	 */
	public ContactRtrvOutDto getFirstEREVContactDateForStage(ContactRtrvInDto contactRtrvInDto)
			throws DataNotFoundException;

}
