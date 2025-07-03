package us.tx.state.dfps.service.eventutility.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:EventUtilityDao interface for implementing EventUtility
 * functionality Sep 7, 2017- 10:54:39 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EventUtilityDao {

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update CD_EVENT_STATUS with provided events
	 * 
	 * @param events
	 * @return long @
	 */
	public long updateEventStatus(List<Event> events);

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update CD_EVENT_STATUS with provided events and status
	 * 
	 * @param events
	 * @param status
	 * @return long @
	 */
	public long updateEventStatus(List<Event> events, String status);

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update CD_EVENT_STATUS with provided idEvent and cdEventStatus status
	 * 
	 * @param idEvent
	 * @param cdEventStatus
	 * @return long @
	 */
	public long updateEventStatus(Long idEvent, String cdEventStatus);

	/**
	 * Method Name: eventExists Method Description: This method is used to check
	 * if the event exists for the provided Event bean
	 * 
	 * @param eventBean
	 * @return boolean @
	 */
	public boolean eventExists(EventValueDto eventBean);

	/**
	 * Method Name: getINVConclusionStatus Method Description: This method
	 * Retrieve the Status of the Investigation Conclusion Event
	 * 
	 * @param idCase
	 * @return String @
	 */
	public String getINVConclusionStatus(long idCase);
}
