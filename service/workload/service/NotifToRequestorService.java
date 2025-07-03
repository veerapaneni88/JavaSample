package us.tx.state.dfps.service.workload.service;

import us.tx.state.dfps.service.common.request.NotifToRequestorReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for CCFC46S Mar 7, 2018- 11:12:34 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface NotifToRequestorService {

	/**
	 * Method Name: getRequestor Method Description: Service method to make DAO
	 * calls and gather data
	 * 
	 * @param notifToRequestorReq
	 * @return PreFillDataServiceDto @
	 */
	public CommonFormRes getRequestor(NotifToRequestorReq notifToRequestorReq);

}
