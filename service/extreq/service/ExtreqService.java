package us.tx.state.dfps.service.extreq.service;

import us.tx.state.dfps.service.common.request.ExtreqReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSVC27s
 * Extension Request Mar 15, 2018- 11:00:13 AM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface ExtreqService {

	/**
	 * Method Name: getExtreq Method Description: CSVC27s Extension Request
	 * populates form extreq
	 * 
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getExtreq(ExtreqReq extreqReq);

}
