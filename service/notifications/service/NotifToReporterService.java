package us.tx.state.dfps.service.notifications.service;

import us.tx.state.dfps.service.common.request.NotificationsReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Notification To Reporter, Tuxedo Service: cint32s May 25, 2018- 9:38:45 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface NotifToReporterService {
	/**
	 * 
	 * Method Name: getNotificationsService Method Description:This service will
	 * return the data needed for the form letter to reporter (cfin0900
	 * Notification to Reporter).
	 * 
	 * @param NotificationsReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getNotificationsService(NotificationsReq notificationsReq);

}
