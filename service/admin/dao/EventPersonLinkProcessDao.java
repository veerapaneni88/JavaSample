package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventLinkInDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Ccmn68dDao
 * Aug 6, 2017- 7:44:58 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface EventPersonLinkProcessDao {
	/**
	 * ccmn68dAUDdam
	 * 
	 * @param iCcmn68diDto
	 * @throws DataNotFoundException
	 */
	public void updateEventPersonLink(EventLinkInDto iCcmn68diDto);

	/**
	 * 
	 * Method Name: ccmn68dAUDdam Method Description:this method does
	 * insert,update and delete.
	 * 
	 * @param eventLinkInDto
	 * @return long
	 * @throws DataNotFoundException
	 */
	public long ccmn68dAUDdam(EventLinkInDto eventLinkInDto);

	/**
	 * deleteEventPersonLink
	 * 
	 * @param iCcmn68diDto
	 */
	public void deleteEventPersonLink(EventLinkInDto iCcmn68diDto);

	/**
	 * saveEventPersonLink
	 * 
	 * @param iCcmn68diDto
	 */
	public void saveEventPersonLink(EventLinkInDto iCcmn68diDto);

}
