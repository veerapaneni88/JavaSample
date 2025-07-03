package us.tx.state.dfps.service.resourcedetail.dao;

import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Resource Phone table insert, update and delete operations Feb 2, 2018-
 * 1:36:57 PM © 2017 Texas Department of Family and Protective Services
 */
public interface ResourcePhoneDao {
	/**
	 * 
	 * Method Name: saveResourcePhone Method Description: This method used for
	 * resource phone insert,update and delete operations using
	 * resourceDetailInDto request Dam: CRES26D
	 * 
	 * @param resourceDetailInDto
	 * @return resourceId @
	 */
	public ResourceDetailOutDto saveResourcePhone(ResourceDetailInDto resourceDetailInDto);
}
