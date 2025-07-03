package us.tx.state.dfps.service.resourcedetail.service;

import java.util.List;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailOutDto;
import us.tx.state.dfps.service.resource.detail.dto.SchoolDistrictReq;
import us.tx.state.dfps.service.resource.dto.SchoolDistrictDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for resource details service Jan 30, 2018- 12:02:19 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ResourceDetailService {

	/**
	 * 
	 * Method Name: saveResourceDetails Dam: Cres04s Method Description: This
	 * method is used for add, update and delete operations of resource details
	 * page
	 * 
	 * @param resourceDetailInDto
	 * @return resourceDetailOutDto @
	 */
	public ResourceDetailOutDto saveResourceDetails(ResourceDetailInDto resourceDetailInDto);

	/**
	 * This is a list service that retrieves all of the school districts for a
	 * particular county passed from the Resource Address window
	 *
	 * @param schoolDistrictReq
	 * @return schoolDistrictRes
	 * @throws InvalidRequestException
	 * @
	 */
	public List<SchoolDistrictDto> getSchooldistrict(SchoolDistrictReq schoolDistrictReq);

	public Long getResourceIdByStageId(Long stageId);
}
