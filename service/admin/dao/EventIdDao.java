package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Fetches the Event Details using EventID Aug 5, 2017- 7:12:20 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EventIdDao {

	/**
	 * Method Name: getEventDetailList Method Description: This Method Fetches
	 * the Event Details using Event Id. ccmn45dQUERYdam
	 * 
	 * @param pInputDataRec
	 * @return List<EventIdOutDto> @
	 */
	public List<EventIdOutDto> getEventDetailList(EventIdInDto pCCMN45DInputRec);
}
