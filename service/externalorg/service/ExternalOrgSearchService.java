/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for External Organization Search Service.
 *Jul 9, 2018- 3:33:39 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.service;

import us.tx.state.dfps.common.externalorg.dto.ExternalOrgSearchParamDto;
import us.tx.state.dfps.service.common.response.ExternalOrgSearchRes;

public interface ExternalOrgSearchService {

	/**
	 * Method Name: executeOrganizationSearch Method Description: Search Service
	 * for External Organization Records.
	 * 
	 * @param searchParam
	 * @return response
	 */
	ExternalOrgSearchRes executeOrganizationSearch(ExternalOrgSearchParamDto searchParam);

}
