package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeTaskOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Interface for fetching Event Details Aug 4, 2017- 11:50:24 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EventStageTypeTaskDao {

	/**
	 * 
	 * Method Name: getEventDtls Method Description: This method will get data
	 * from EVENT table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventStageTypeTaskOutDto> @
	 */
	public List<EventStageTypeTaskOutDto> getEventDtls(EventStageTypeTaskInDto pInputDataRec);
}
