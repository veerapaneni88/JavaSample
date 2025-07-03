package us.tx.state.dfps.service.resourcesearch.service;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.service.common.response.ResourceSearchRes;
import us.tx.state.dfps.service.resource.dto.ContractPeriodDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resource.dto.ResourceSearchResultDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is the interface for ResourceSearchServiceImpl> Oct 31, 2017- 6:28:27 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface ResourceSearchService {

	/**
	 * Method Name: getResourceDetails Method Description: This method gets the
	 * details of the resource based on the resourceId.
	 * 
	 * @param resourceId
	 * @return ResourceSearchValueDto @
	 */
	public ResourceSearchResultDto getResourceDetails(String resourceId);

	/**
	 * Method Name: getResourceDetailsUsingIdPlcmtEvent Method Description: This
	 * method retrieves Resource Details using Placement Event Id.
	 * 
	 * @param idPlcmtEvent
	 * @return ResourceValueBeanDto @
	 */
	public ResourceValueBeanDto getResourceDetailsUsingIdPlcmtEvent(Long idPlcmtEvent);

	/**
	 * Method Name: getChildResources Method Description: This method retrieves
	 * the Child Resources of the given type for the Parent Resource.
	 * 
	 * @param resourceId
	 * @param cdRsrcLinkType
	 * @return List<ResourceValueBeanDto> @
	 */
	public List<ResourceValueBeanDto> getChildResources(Long resourceId, String cdRsrcLinkType);

	/**
	 * Method Name: selectResourceDetails Method Description: This method
	 * retrieves Resource Details using idResource
	 * 
	 * @param idResource
	 * @return ResourceValueBeanDto @
	 */
	public ResourceValueBeanDto selectResourceDetails(Long idResource);

	/**
	 * Method Name: searchResources Method Description: Search for Resources
	 * given the specified parameters.
	 * 
	 * @param resourceSearchValueDto
	 * @return PaginationResultDto @
	 */
	public ResourceSearchRes searchResources(ResourceSearchResultDto resourceSearchValueDto);

	/**
	 *Method Name:	getResourceDtls
	 *Method Description:
	 *@param idResource
	 *@return
	 */
	public ResourceDto getResourceDtls(Long idResource);

	public ResourceSearchRes getVendorDetails(List<ResourceSearchResultDto> result);

	public Map<Long, ContractPeriodDto> getContractDetails(List<String> idFacilities);

	public List<Long> getFcl02Resources(List<String> idFacilities);
}
