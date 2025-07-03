package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventLastUpdatedInDto;
import us.tx.state.dfps.service.admin.dto.EventLastUpdatedoDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao for
 * EventLastUpdateDao Sep 8, 2017- 4:06:24 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface EventLastUpdateDao {
	/**
	 * 
	 * Method Name:getEventDtls Method Description: This method fetches event
	 * details for given event id
	 *
	 * @param pInputDataRec
	 * @return List<EventLastUpdatedoDto> @
	 */
	public List<EventLastUpdatedoDto> getEventDtls(EventLastUpdatedInDto pInputDataRec);

	/**
	 * Method Name: getDtLastUpdateForEvent Method Description: This method
	 * fetches event details for given last update date
	 * 
	 * @param eventLastUpdatediDto
	 * @return EventLastUpdatedoDto @
	 */
	public EventLastUpdatedoDto getDtLastUpdateForEvent(EventLastUpdatedInDto eventLastUpdatediDto);

}
