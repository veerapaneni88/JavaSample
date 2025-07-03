package us.tx.state.dfps.service.arfamilynotification.service;

import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for AR Conclusion Reporter Notification May 8, 2018- 4:30:37
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface ArConcRepNotifService {

	/**
	 * Method Name: getReporterNotif Method Description: Gets the prefill string
	 * for the ARRENOT/S forms
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	public PreFillDataServiceDto getReporterNotif(PopulateFormReq req);

}
