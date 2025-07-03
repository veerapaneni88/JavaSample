package us.tx.state.dfps.service.notifications.service;

import us.tx.state.dfps.service.common.request.NotificationsReq;
import us.tx.state.dfps.service.common.response.NotificationsRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:NotificationsService Aug 10, 2017- 9:59:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface NotificationsService {

	/**
	 * 
	 * Method Name: callCint33sService Method Description:This service will
	 * update the contact table and return a contact if one occurred before.
	 * 
	 * @param objCint33siDto
	 * @return NotificationsOutDto @
	 */
	public NotificationsRes callNotificationsService(NotificationsReq notificationsReq);

}
