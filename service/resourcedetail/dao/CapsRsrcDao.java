package us.tx.state.dfps.service.resourcedetail.dao;

import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for insert or update CapsResource table Call CRES16D Feb 2, 2018- 1:32:51 PM
 * © 2017 Texas Department of Family and Protective Services
 */
public interface CapsRsrcDao {
	/**
	 * 
	 * Method Name: saveCapsResource Method Description: This method used for
	 * caps resource insert,update and delete operations using
	 * resourceDetailInDto request
	 * 
	 * @param resourceDetailInDto
	 * @return resourceId @
	 */
	public Long saveCapsResource(ResourceDetailInDto resourceDetailInDto);

	/**
	 *
	 * Method Name: fetchParentFacilityNumber Method Description: This method used for
	 * fetch the facility number for childs/subcontracting  resources with no facilty number from parent resource   using
	 * parent resource ID
	 *
	 * @param ResourceID
	 * @return facilityNumber @
	 */
	public Long fetchParentFacilityNumber(Long ResourceID);
}
