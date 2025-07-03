package us.tx.state.dfps.service.contacts.service;

import us.tx.state.dfps.service.common.request.KinshipNotificationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description :MONTHLY
 * EVALUATION FORM Tuxedo Service :CSUB84S Mar 28, 2018- 9:48:14 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface KinshipNotificationService {
	/**
	 * Method Name: getKinshipNotificationDetails Method Description: Fetch the
	 * Kinship Notification Details
	 * 
	 * @param kinshipNotificationReq
	 * @return KinshipNotificationDto @
	 */
	public PreFillDataServiceDto getKinshipNotificationDetails(KinshipNotificationReq kinshipNotificationReq);

}
