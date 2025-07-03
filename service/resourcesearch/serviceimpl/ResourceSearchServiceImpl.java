package us.tx.state.dfps.service.resourcesearch.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.ResourceSearchRes;
import us.tx.state.dfps.service.common.util.ResourceSearchComparatorUtil;
import us.tx.state.dfps.service.common.util.ResourceUtil;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.resource.dto.ContractPeriodDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.resource.dto.ResourceSearchResultDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;
import us.tx.state.dfps.service.resourcesearch.dao.ResourceSearchDao;
import us.tx.state.dfps.service.resourcesearch.service.ResourceSearchService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * implements the methods declared in ResourceSearchService Interface> Oct 31,
 * 2017- 6:24:57 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ResourceSearchServiceImpl implements ResourceSearchService {

	@Autowired
	private ResourceSearchDao resourceSearchDao;
	
	@Autowired
	private CapsResourceDao capsResourceDao;

	@Autowired
	private ContractDao contractDao;

	private static final Logger log = Logger.getLogger(ResourceSearchServiceImpl.class);

	/**
	 * Method Name: getResourceDetails Method Description: This method gets the
	 * details of the resource based on the resourceId.
	 * 
	 * @param resourceId
	 * @return ResourceSearchValueDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ResourceSearchResultDto getResourceDetails(String resourceId) {
		ResourceSearchResultDto resourceSearchValueDto = new ResourceSearchResultDto();
		try {
			resourceSearchValueDto = resourceSearchDao.getResourceDetails(resourceId);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return resourceSearchValueDto;

	}

	/**
	 * Method Name: getResourceDetailsUsingIdPlcmtEvent Method Description: This
	 * method retrieves Resource Details using Placement Event Id.
	 * 
	 * @param idPlcmtEvent
	 * @return ResourceValueBeanDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ResourceValueBeanDto getResourceDetailsUsingIdPlcmtEvent(Long idPlcmtEvent) {
		ResourceValueBeanDto resourceValueBeanDto = new ResourceValueBeanDto();
		try {
			resourceValueBeanDto = resourceSearchDao.getResourceDetailsUsingIdPlcmtEvent(idPlcmtEvent);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return resourceValueBeanDto;

	}

	/**
	 * Method Name: getChildResources Method Description: This method retrieves
	 * the Child Resources of the given type for the Parent Resource.
	 * 
	 * @param resourceId
	 * @param cdRsrcLinkType
	 * @return List<ResourceValueBeanDto> @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<ResourceValueBeanDto> getChildResources(Long resourceId, String cdRsrcLinkType) {
		List<ResourceValueBeanDto> resourceValueBeanDtoList = new ArrayList<ResourceValueBeanDto>();
		try {
			resourceValueBeanDtoList = resourceSearchDao.getChildResources(resourceId, cdRsrcLinkType);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return resourceValueBeanDtoList;
	}

	/**
	 * Method Name: selectResourceDetails Method Description: This method
	 * retrieves Resource Details using idResource
	 * 
	 * @param idResource
	 * @return ResourceValueBeanDto @
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ResourceValueBeanDto selectResourceDetails(Long idResource) {
		ResourceValueBeanDto resourceValueBeanDto = new ResourceValueBeanDto();
		try {
			resourceValueBeanDto = resourceSearchDao.selectResourceDetails(idResource);
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return resourceValueBeanDto;

	}

	/**
	 * Method Name: searchResources Method Description: Search for Resources
	 * given the specified parameters.
	 * 
	 * @param resourceSearchValueDto
	 * @return resourceSearchRes @
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ResourceSearchRes searchResources(ResourceSearchResultDto resourceSearchValueDto) {
		ResourceSearchRes resourceSearchRes = new ResourceSearchRes();
		String stage = resourceSearchValueDto.getStage();
		boolean isAddResource = resourceSearchValueDto.getAddResource();
		List<ResourceSearchResultDto> resourceSearchResultDtoList = resourceSearchDao
				.executeSearch(resourceSearchValueDto);
		// Move the code to create the response in ServiceImpl
		SortedSet<ResourceSearchResultDto> sortedResourceSet = new TreeSet<ResourceSearchResultDto>(
				new ResourceSearchComparatorUtil());
		List<ResourceSearchResultDto> searchValueDtoList = new ArrayList<ResourceSearchResultDto>();
		List<String> parentResIdList = new ArrayList<String>();
		// Checking for 999 records instead of 1000 to throw Too Many Rows
		// exception,
		// because rownum<1000 has been added to query to avoid additional data
		// to be retrieved and sorted for performance reasons.
		if (resourceSearchResultDtoList.size() > ServiceConstants.MAX_SIZE) { // artf225721 remove " >= " to allow 999 rows
			ErrorDto error = new ErrorDto();
			error.setErrorMsg(ServiceConstants.TOO_MANY_RECORD_EXCEPTION);
			resourceSearchRes.setErrorDto(error);
		} else {
			for (ResourceSearchResultDto searchDto : resourceSearchResultDtoList) {
				Integer tmpResId = Integer.valueOf(searchDto.getIdentificationNum());
				ResourceSearchResultDto newResourceSearchValueDto = new ResourceSearchResultDto();
				newResourceSearchValueDto.setEffectiveDate(resourceSearchValueDto.getEffectiveDate());
				newResourceSearchValueDto.setSearchResName(resourceSearchValueDto.getResourceName());
				newResourceSearchValueDto.setSearchResAddr1(resourceSearchValueDto.getStreetAddress());
				Boolean isIntakeReq = ResourceUtil.isIntakeRequest();
				Boolean isIntakeLawEnfReq = ResourceUtil.isIntakeLawEnfRequest();
				newResourceSearchValueDto.setResourceName(searchDto.getResourceName());
				newResourceSearchValueDto.setIdentificationNum(searchDto.getIdentificationNum());
				newResourceSearchValueDto.setResourceInactive(searchDto.getResourceInactive());
				newResourceSearchValueDto.setResourceTypes(searchDto.getResourceTypes());
				newResourceSearchValueDto.setFacilityType(searchDto.getFacilityType());
				newResourceSearchValueDto.setStreetAddress(searchDto.getStreetAddress());
				newResourceSearchValueDto.setNameCity(StringUtils.isEmpty(searchDto.getStreetAddress2())
						? ServiceConstants.EMPTY_STR : searchDto.getStreetAddress2().toUpperCase());
				newResourceSearchValueDto.setNameCounty(searchDto.getNameCounty());
				newResourceSearchValueDto.setPhoneNumber(searchDto.getPhoneNumber());
				newResourceSearchValueDto.setPhoneExtension(searchDto.getPhoneExtension());
				newResourceSearchValueDto.setSchoolDistrict(searchDto.getSchoolDistrict());
				newResourceSearchValueDto.setAddResource(resourceSearchValueDto.getAddResource());
				newResourceSearchValueDto.setRtbStatus(searchDto.getRtbStatus());
				newResourceSearchValueDto.setIndFictiveCaregiver(searchDto.getIndFictiveCaregiver());
				newResourceSearchValueDto.setIndRelativeCaregiver(searchDto.getIndRelativeCaregiver());
				newResourceSearchValueDto.setStreetAddress2Actuall(searchDto.getStreetAddress2Actuall());
				newResourceSearchValueDto.setStateName(searchDto.getStateName());
				newResourceSearchValueDto.setZipCode(StringUtils.isEmpty(searchDto.getZipCode()) ? ServiceConstants.EMPTY_STR
						: searchDto.getZipCode().substring(0, 5));
				if(searchDto.getZipCode()!=null && searchDto.getZipCode().length()>6) {
					newResourceSearchValueDto.setZipCodeSuffix((searchDto.getZipCode().substring(6, 10)));
				}
				if ((isIntakeLawEnfReq == ServiceConstants.FALSEVAL
						&& (StringUtil.isValid(resourceSearchValueDto.getSearchResName())
								|| StringUtil.isValid(resourceSearchValueDto.getSearchResAddr1())))
						&& !isAddResource) {
					newResourceSearchValueDto.setResourceScore(searchDto.getResourceScore());
				}
				if (!TypeConvUtil.isNullOrEmpty(searchDto.getRsrcContracted())) {
					newResourceSearchValueDto.setRsrcContracted(searchDto.getRsrcContracted());
				}
				if (!TypeConvUtil.isNullOrEmpty(searchDto.getInvJurisdiction())) {
					newResourceSearchValueDto.setInvJurisdiction(searchDto.getInvJurisdiction());
				}

				if (isIntakeReq) {
					if (!TypeConvUtil.isNullOrEmpty(searchDto.getParentResourceId())) {
						newResourceSearchValueDto.setParentResourceId(searchDto.getParentResourceId());
					}
					if (!TypeConvUtil.isNullOrEmpty(searchDto.getParentResourceType())) {
						newResourceSearchValueDto.setParentResourceType(searchDto.getParentResourceType());
					}
				}

				Boolean isDuplicate = ServiceConstants.FALSEVAL;
				for (ResourceSearchResultDto sortedResource : sortedResourceSet) {
					if ((Integer.parseInt(sortedResource.getIdentificationNum())) == tmpResId.intValue()) {
						isDuplicate = ServiceConstants.TRUEVAL;
						break;
					}
				}
				if (isDuplicate == ServiceConstants.FALSEVAL) {
					sortedResourceSet.add(newResourceSearchValueDto);
					if (ServiceConstants.CODE_02.equals(newResourceSearchValueDto.getParentResourceType())) {
						parentResIdList.add(newResourceSearchValueDto.getParentResourceId());
					}
				}
			}
			// Gets Parent resource info from the database in case of intake
			// request,
			// and populates ResourceSearchValueBeans inside the list.
			sortedResourceSet = resourceSearchDao.populateParentResInfo(sortedResourceSet, parentResIdList);
			sortedResourceSet.stream().forEach(sortedResource -> {
				searchValueDtoList.add(sortedResource);
			});

			if (!StringUtils.isEmpty(stage)) {
				// if the searchValueDtoList is null then do not do the search
				// for association of Id_person with Kin stage
				if (!ObjectUtils.isEmpty(searchValueDtoList)) {
					// get the resourceIds and pass the same for
					// getResourceStageAsso method
					List<String> resourceIds = new ArrayList<String>();
					searchValueDtoList.stream().forEach(resourceSearchResultDto -> {
						resourceIds.add(resourceSearchResultDto.getIdentificationNum());
					});

					List<ResourceSearchResultDto> newResourceSearchValueDtoList = resourceSearchDao
							.getResourceStageAsso(resourceSearchValueDto, resourceIds);

					// Iterate through both the list to determine if the
					// Identification Num in both the list
					// are the Same. If they are the same , then set the
					// attribute 'isassociated' to true
					// else set the attribute to false.

					for (ResourceSearchResultDto resValueDto : searchValueDtoList) {
						newResourceSearchValueDtoList.stream().forEach(newResValueDto -> {
							if (resValueDto.getIdentificationNum().trim()
									.equals(newResValueDto.getIdentificationNum().trim())) {
								resValueDto.setIsAssociate(ServiceConstants.TRUE);
							} else {
								resValueDto.setIsAssociate(ServiceConstants.FALSE);
							}
						});
					}
				}
			}
			resourceSearchRes.setResourceSearchResultDtoLst(searchValueDtoList);
		}
		return resourceSearchRes;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.resourcesearch.service.ResourceSearchService#getResourceDtls(java.lang.Long)
	 */
	@Override
	public ResourceDto getResourceDtls(Long idResource) {
		ResourceDto resourceDto = new ResourceDto();
				CapsResource capsResource  = new CapsResource();
		 capsResource = capsResourceDao.getCapsResourceById(idResource);
		if(!ObjectUtils.isEmpty(capsResource) && !ObjectUtils.isEmpty(capsResource.getCdRsrcSchDist())){
			//artf212958 : passing county code to get school dist code
			String schDistName = capsResourceDao.getSchDistName(capsResource.getCdRsrcSchDist(),capsResource.getCdRsrcCnty());
			resourceDto.setCdRsrcSchDist(schDistName);	
		}
		return resourceDto;
	}

	@Override
	public ResourceSearchRes getVendorDetails(List<ResourceSearchResultDto> result) {

		ResourceSearchRes resourceSearchRes = new ResourceSearchRes();
		List<ResourceSearchResultDto> list = new ArrayList<>();
		for(ResourceSearchResultDto resourceSearchResultDto : result){
			String vendorId = resourceSearchDao.getVendorDetails(resourceSearchResultDto.getIdentificationNum());
			resourceSearchResultDto.setVendorId(vendorId);
			list.add(resourceSearchResultDto);
		}
		resourceSearchRes.setResourceSearchResultDtoLst(list);
		return resourceSearchRes;

	}

	@Override
	public Map<Long, ContractPeriodDto> getContractDetails(List<String> idFacilities) {
		Map<Long, ContractPeriodDto> returnObj = new HashMap<Long, ContractPeriodDto>();
		for(String idFacility : idFacilities){
			ContractPeriodDto contractPeriodDto = contractDao.getLatestCPForResourceId(idFacility);
			if(contractPeriodDto!=null){
				returnObj.put(Long.valueOf(idFacility),contractPeriodDto);
			}
		}
		return returnObj;
	}
	@Override
	public List<Long> getFcl02Resources(List<String> idFacilities){
		return resourceSearchDao.getFcl02Resources(idFacilities);
	}

}
