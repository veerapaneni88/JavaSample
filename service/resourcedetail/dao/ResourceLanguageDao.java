package us.tx.state.dfps.service.resourcedetail.dao;

import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Resource Language table insert, update and delete operations Feb 2, 2018-
 * 1:41:53 PM © 2017 Texas Department of Family and Protective Services
 */
public interface ResourceLanguageDao {
	/**
	 * 
	 * Method Name: saveResourceLanguage Method Description: This method used
	 * for resource language insert,update and delete operations using
	 * resourceDetailInDto request Dam : CAUDL4D
	 * 
	 * @param resourceDetailInDto
	 * @return resourceId @
	 */
	public Long saveResourceLanguage(ResourceDetailInDto resourceDetailInDto);
}
