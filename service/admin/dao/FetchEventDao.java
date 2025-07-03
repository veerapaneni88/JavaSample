package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventResultDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Fetches the Event Details using EventID Aug 5, 2017- 7:12:20 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FetchEventDao {

	/**
	 * 
	 * Method Name: fetchEventDao Method Description: Method to fetch Event
	 * Details using EventID.
	 * 
	 * @param FetchEventDto
	 * @return List<FetchEventResultDto>
	 * @throws DataNotFoundException
	 */
	public List<FetchEventResultDto> fetchEventDao(FetchEventDto fetchEventDto);

}
