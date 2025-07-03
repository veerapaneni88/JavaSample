package us.tx.state.dfps.service.eventutility.service;

import java.util.List;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Used to
 * update the status of events. Sep 6, 2017- 6:47:42 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface EventUtilityService {

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update CD_EVENT_STATUS with provided events
	 * 
	 * @param events
	 * @return long @
	 */
	public Long updateEventStatus(List<Event> events);

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update CD_EVENT_STATUS with provided events and status
	 * 
	 * @param events
	 * @param status
	 * @return long @
	 */
	public Long updateEventStatus(List<Event> events, String status);

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update CD_EVENT_STATUS with provided idEvent and cdEventStatus status
	 * 
	 * @param idEvent
	 * @param cdEventStatus
	 * @return long @
	 */
	public Long updateEventStatus(Long idEvent, String cdEventStatus);

	/**
	 * Method Name: invalidatePendingStageClosure Method Description: This
	 * method is used to update the status to COMP provided input as
	 * stageClosureEventId
	 * 
	 * @param stageClosureEventId
	 * @return long @
	 */
	public Long invalidatePendingStageClosure(Long stageClosureEventId);

	/**
	 * Method Name: fetchEventInfo Method Description: Fetches the event info
	 * for the IdEvent.
	 * 
	 * @param idEvent
	 * @return EventValueDto @
	 */
	public EventValueDto fetchEventInfo(long idEvent);

	/**
	 * Method Name: eventExists Method Description: This method is used to check
	 * if the event exists for the provided Event bean
	 * 
	 * @param eventBean
	 * @return boolean @
	 */
	public boolean eventExists(EventValueDto eventBean);

}
