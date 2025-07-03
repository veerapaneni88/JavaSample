package us.tx.state.dfps.service.lonnotinv.service;

import us.tx.state.dfps.service.common.request.LONNotInvReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV87S
 * Letter of Notification - Not Investigated Mar 21, 2018- 3:26:07 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface LONNotInvService {

	/**
	 * Method Name: getLetter Method Description: Letter of Notification - Not
	 * Investigated - populating the letter
	 * 
	 * @param lONNotInvReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getLetter(LONNotInvReq lONNotInvReq);

}
