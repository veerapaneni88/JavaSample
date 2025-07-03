package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdInDto;
import us.tx.state.dfps.service.admin.dto.EventStagePersonLinkInsUpdOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Interface for fetching event details Aug 7, 2017- 3:44:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EventStagePersonLinkInsUpdDao {

	/**
	 * 
	 * Method Name: getEventAndStatusDtls Method Description: This method will
	 * dynamically retrieves data from event and stage table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventStagePersonLinkInsUpdOutDto> @
	 */
	public List<EventStagePersonLinkInsUpdOutDto> getEventAndStatusDtls(EventStagePersonLinkInsUpdInDto pInputDataRec);
}
