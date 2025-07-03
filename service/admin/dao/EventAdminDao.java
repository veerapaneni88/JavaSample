package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.service.admin.dto.EventDataInputDto;
import us.tx.state.dfps.service.admin.dto.EventDataOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the interface for Ccmn46dDaoImpl Aug 6, 2017- 7:44:02 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EventAdminDao {
	/**
	 * DAM Name: CCMN46D DAM Description: This DAM performs adds, Update and
	 * Delete on EVENT table
	 * 
	 * 
	 * @param inputEvent
	 * @return
	 */
	EventDataOutputDto postEvent(EventDataInputDto inputEvent);

}
