package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.service.common.request.CpsIntakeNotificationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * Name:CpsLicenseLawEnforcementService Class
 * description:CpsLicenseLawEnforcementService Class Description:This service is
 * used to launch the CPS Intake license law enforcement forms Oct 30, 2017-
 * 3:27:12 PM © 2017 Texas Department of Family and Protective Services
 */
public interface CpsLicenseLawEnforcementService {

	/**
	 * Method Name:getCpsIntkLicenseLawReport Method description:This service
	 * produces the data required for the CPS Intake license law enforcement
	 * report.
	 * 
	 * @param cpsIntakeNotificationReq
	 * @return PreFillDataServiceDto
	 */
	PreFillDataServiceDto getCpsIntkLicenseLawReport(CpsIntakeNotificationReq cpsIntakeNotificationReq);

}
