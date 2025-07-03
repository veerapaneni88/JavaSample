package us.tx.state.dfps.service.childfatality.service;

import us.tx.state.dfps.service.common.request.ChildFatalityReq;
import us.tx.state.dfps.service.common.response.ChildFatalityRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:The
 * Interface ChildFatalityService. Aug 20, 2017- 5:14:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */

public interface ChildFatalityService {

	/**
	 * Search child.
	 *
	 * @param childFatalityReq
	 *            the child fatality req
	 * @return the child fatality res
	 */
	public ChildFatalityRes searchChild(ChildFatalityReq childFatalityReq);

}
