/**
 * 
 */
package us.tx.state.dfps.service.subcare.service;

import us.tx.state.dfps.service.common.request.ResourceReq;
import us.tx.state.dfps.service.common.response.ResourceRes;
import us.tx.state.dfps.service.subcare.dto.ResourceRtbExceptionDto;

/**
 * @author OGAHJ
 *
 */
public interface CapsResourceService {

	/**
	 * 
	 * Method Name:getResourceDtl (Ejb service Name- CRES03S ) Description:
	 * Retrieval service called within predisplay of Rsrc Dtl window. Tables
	 * hit: Caps Resource, Rsrc Categories, Rsrc School District, Rsrc Address,
	 * Phone and Rsrc Link.
	 * 
	 * @param resourceServiceReq
	 * @return ResourceServiceRes @
	 */

	public ResourceRes getResourceDtl(ResourceReq resourceReq);

	public ResourceRtbExceptionDto checkResourceRtbException(ResourceRtbExceptionDto newException);
}
