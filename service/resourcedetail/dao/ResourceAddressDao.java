package us.tx.state.dfps.service.resourcedetail.dao;

import java.util.List;

import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resource.dto.SchoolDistrictDetailsDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Resource Address table insert, update and delete operations Feb 2, 2018-
 * 1:45:13 PM © 2017 Texas Department of Family and Protective Services
 */
public interface ResourceAddressDao {
	/**
	 * 
	 * Method Name: saveResourceAddress Method Description: This method used for
	 * resource address insert,update and delete operations using
	 * resourceDetailInDto request
	 * 
	 * @param resourceDetailInDto
	 *            Dam : CRES25D
	 * @return resourceId @
	 */
	public Long saveResourceAddress(ResourceDetailInDto resourceDetailInDto);

	/**
	 * This is a list service that retrieves all of the school districts for a
	 * particular county passed from the Resource Address window
	 *
	 * @param schDistTxCounty
	 * @return schoolDistrictRes
	 */

	public List<SchoolDistrictDetailsDto> getSchoolDistrict(String schDistTxCounty);
}
