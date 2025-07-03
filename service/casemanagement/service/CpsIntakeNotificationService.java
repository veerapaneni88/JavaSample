package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.common.request.CpsIntakeNotificationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * Name:CpsIntakeNotificationService Class Description:This service is used to
 * launch the CPS Intake Notification forms Oct 30, 2017- 3:27:12 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface CpsIntakeNotificationService {

	/**
	 * Method Name:getCpsIntkNotificnLawEnfrcemntReport Method Description:This
	 * service produces the data required for the CPS Intake notification law
	 * enforcement report
	 * 
	 * @param cpsIntakeNotificationReq
	 * @return
	 */
	PreFillDataServiceDto getCpsIntkNotificnLawEnfrcemntReport(CpsIntakeNotificationReq cpsIntakeNotificationReq);
}