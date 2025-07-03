package us.tx.state.dfps.service.resourcedetail.dao;

import us.tx.state.dfps.common.domain.ResourceEmail;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Resource Email table insert, update and delete operations Call CAUDL3D
 * Feb 2, 2018- 1:38:05 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface ResourceEmailInsUpdDelDao {
	/**
	 * 
	 * Method Name: handleResourceEmail Method Description: This method used for
	 * resource email insert,update and delete operations using
	 * resourceDetailInDto request
	 * 
	 * @param actionType
	 * @param resourceEmail
	 * @return @
	 */
	public Long handleResourceEmail(String actionType, ResourceEmail resourceEmail);
}
