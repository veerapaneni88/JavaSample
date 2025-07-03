/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jan 11, 2018- 3:50:06 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.populateletter.service;

import us.tx.state.dfps.service.common.request.PopulateLetterReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV63S Jan
 * 11, 2018- 3:50:06 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface LetterReporterService {

	/**
	 * Method Name: PopulateLetter Method Description:
	 * 
	 * @param populateLetterReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto populateLetter(PopulateLetterReq populateLetterReq, boolean spanish);
}
