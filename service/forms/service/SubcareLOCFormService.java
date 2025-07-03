/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 19, 2018- 4:11:05 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.service;

import us.tx.state.dfps.service.common.request.SubcareLOCFormReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 19, 2018- 4:11:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SubcareLOCFormService {

	/**
	 * 
	 * Method Name: getSubcareLOCAuthReqReport Method Description: service
	 * implementation for CSUB44S
	 * 
	 * @param placementFormReq
	 * @return @
	 */
	PreFillDataServiceDto getSubcareLOCAuthReqReport(SubcareLOCFormReq placementFormReq);

}
