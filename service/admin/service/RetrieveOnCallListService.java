package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.RetrieveOnCallListiDto;
import us.tx.state.dfps.service.common.response.RetrieveOnCallListRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:RetrieveOnCallListService Aug 16, 2017- 8:05:52 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface RetrieveOnCallListService {
	/**
	 * callRetrieveOnCallListService Method Description: This Method retrieves a
	 * full row of the ON_CALL table based on dynamic input.
	 * 
	 * @param pInputMsg
	 * @return @
	 */
	public RetrieveOnCallListRes callRetrieveOnCallListService(RetrieveOnCallListiDto pInputMsg);
}
