/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for External Organization Search Data access
 * Layer.
 *Jul 9, 2018- 3:44:53 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.externalorg.dao;

import java.util.List;

import us.tx.state.dfps.common.externalorg.dto.ExternalOrgDto;
import us.tx.state.dfps.common.externalorg.dto.ExternalOrgSearchParamDto;

public interface ExternalOrgSearchDao {

	/**
	 * Method Name: searchExternalOrg Method Description: Implementation method
	 * for External Organization search for the given criteria.
	 * 
	 * @param searchParam
	 * @return searchResult
	 */
	List<ExternalOrgDto> searchExternalOrg(ExternalOrgSearchParamDto searchParam);

}
