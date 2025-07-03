package us.tx.state.dfps.service.admin.dao;

import java.util.Date;

import us.tx.state.dfps.service.admin.dto.FetchEventStatusdiDto;
import us.tx.state.dfps.service.admin.dto.FetchEventStatusdoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:DAO Interface for fetching event details
 *
 * Aug 7, 2017- 3:44:41 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface FetchEventStatusDao {

	/**
	 * 
	 * Method Name: searchEvents Method Description: The searchEvents method
	 * returns CCMN87DO search events information.
	 * 
	 * @param fetchEventStatusdiDto
	 * @return FetchEventStatusdoDto
	 */
	public FetchEventStatusdoDto searchEvents(FetchEventStatusdiDto fetchEventStatusdiDto);

	/**
	 * 
	 * Method Name: searchEventsWithPagination Method Description: The
	 * searchEventsWithPagination method returns CCMN87DO search events
	 * information with pagination details.
	 * 
	 * @param fetchEventStatusdiDto
	 * @return FetchEventStatusdoDto
	 */
	public FetchEventStatusdoDto searchEventsWithPagination(FetchEventStatusdiDto fetchEventStatusdiDto);

	/**
	 * 
	 * Method Name: getRemovalDate Method Description: Gets Conservatorship
	 * removal date
	 * 
	 * @return Date
	 * @param ulIdEvent
	 */
	public Date getRemovalDate(Integer uIdEvent);

}
