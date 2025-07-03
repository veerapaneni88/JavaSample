package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventPersonLinkInsUpdDelInDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Ccmn68dDao
 * Aug 6, 2017- 7:44:58 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface EventPersonLinkInsUpdDelDao {

	/**
	 * 
	 * Method Name: updateEventPersonLink Method Description: This method will
	 * perform add, update and delete on Event Person Link table.
	 * 
	 * @param iCcmn68diDto
	 * @throws DataNotFoundException
	 */
	public void updateEventPersonLink(EventPersonLinkInsUpdDelInDto iCcmn68diDto);
}
