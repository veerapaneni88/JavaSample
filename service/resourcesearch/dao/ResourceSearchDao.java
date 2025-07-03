package us.tx.state.dfps.service.resourcesearch.dao;

import java.util.List;
import java.util.SortedSet;

import us.tx.state.dfps.service.resource.dto.ResourceSearchResultDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;
import us.tx.state.dfps.service.subcare.dto.ResourceRtbExceptionDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is the interface for ResourceSearchDaoImpl> Oct 31, 2017- 6:44:30 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface ResourceSearchDao {

	/**
	 * Method Name: getResourceDetails Method Description: This method gets the
	 * details of the resource based on the resourceId.
	 * 
	 * @param resourceId
	 * @return ResourceSearchValueDto
	 */
	public ResourceSearchResultDto getResourceDetails(String resourceId);

	/**
	 * Method Name: getResourceDetailsUsingIdPlcmtEvent Method Description: This
	 * method retrieves Resource Details using Placement Event Id.
	 * 
	 * @param idPlcmtEvent
	 * @return ResourceValueBeanDto
	 */
	public ResourceValueBeanDto getResourceDetailsUsingIdPlcmtEvent(Long idPlcmtEvent);

	/**
	 * Method Name: getChildResources Method Description: This method retrieves
	 * the Child Resources of the given type for the Parent Resource.
	 * 
	 * @param resourceId
	 * @param cdRsrcLinkType
	 * @return List<ResourceValueBeanDto>
	 */
	public List<ResourceValueBeanDto> getChildResources(Long resourceId, String cdRsrcLinkType);

	/**
	 * Method Name: selectResourceDetails Method Description: This method
	 * retrieves Resource Details using idResource
	 * 
	 * @param idResource
	 * @return ResourceValueBeanDto
	 */
	public ResourceValueBeanDto selectResourceDetails(Long idResource);

	/**
	 * Method Name: executeSearch Method Description: Search for a resource
	 * based on any number of input parameters specified in the input
	 * 
	 * @param resourceSearchDBDto
	 * @return PaginationResultDto
	 */
	public List<ResourceSearchResultDto> executeSearch(ResourceSearchResultDto resourceSearchValueDto);

	/**
	 * Method Name: getResourceStageAsso Method Description: Gets the Resource
	 * ids values for the given stage
	 * 
	 * @param resourceSearchValueDto
	 * @param resourceIds
	 * @return PaginationResultDto
	 */
	public List<ResourceSearchResultDto> getResourceStageAsso(ResourceSearchResultDto resourceSearchValueDto,
			List<String> resourceIds);

	/**
	 * Method Name: populateParentResInfo Method Description:
	 * 
	 * @param sortedResourceSet
	 * @param parentResIdList
	 * @return SortedSet<ResourceSearchResultDto>
	 */
	public SortedSet<ResourceSearchResultDto> populateParentResInfo(
			SortedSet<ResourceSearchResultDto> sortedResourceSet, List<String> parentResIdList);

	// artf187193 BR 1.2 Manual Override of RTB Indicator
	public List<ResourceRtbExceptionDto> findResourceRtbExceptions(Long resourceId);

	public List<ResourceRtbExceptionDto> findResourceRtbExceptionById(Long idResourceRtbException);

	public Long addResourceRtbException(ResourceRtbExceptionDto rtbException);

	public Long updateResourceRtbException(ResourceRtbExceptionDto rtbException);

	public String getVendorDetails(String resourceIds);

	public List<Long> getFcl02Resources(List<String> idFacilities);
}
