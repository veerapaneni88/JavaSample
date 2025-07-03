package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusInDto;
import us.tx.state.dfps.service.admin.dto.EventStageTypeStatusOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface to fetch event status> Aug 8, 2017- 4:21:59 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EventStageTypeStatusDao {

	/**
	 * 
	 * Method Name: getEventStatus Method Description: This method will get
	 * Event Status from Event.
	 * 
	 * @param pInputDataRec
	 * @return List<EventStageTypeStatusOutDto> @
	 */
	public List<EventStageTypeStatusOutDto> getEventStatus(EventStageTypeStatusInDto pInputDataRec);

	/**
	 * Method Name: getEventStatusForDayCare Method Description:
	 * 
	 * @param pInputDataRec
	 * @param eventType
	 * @param eventStatus
	 * @return
	 */
	List<EventStageTypeStatusOutDto> getEventStatusForDayCare(EventStageTypeStatusInDto pInputDataRec, String eventType,
			String eventStatus);
}
