
package us.tx.state.dfps.service.arfamilynotification.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: <Service
 * interface class for taking request from ARCnclnFamilyNotifController> Apr 5,
 * 2018- 11:27:43 AM © 2017 Texas Department of Family and Protective Services
 */
public interface ARCnclnFamilyNotifService {

	/**
	 * Service Name: arfanot Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then populate
	 * Arfanot Spanish form on person detail/contact detail page
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getARCnclnFamilyNotif(PopulateFormReq populateFormReq);

	/**
	 * Service Name: arsafna Method Description: This service will get forms
	 * populated by receiving populateFormReq from controller, then populate
	 * arsafna form on SDM Safety Assessment page
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getARSafetyFamilyAssmtForm(PopulateFormReq populateFormReq);

}
