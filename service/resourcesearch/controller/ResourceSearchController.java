package us.tx.state.dfps.service.resourcesearch.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ResourceReq;
import us.tx.state.dfps.service.common.request.ResourceSearchResultReq;
import us.tx.state.dfps.service.common.response.ResourceRes;
import us.tx.state.dfps.service.common.response.ResourceSearchRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.resource.dto.ContractPeriodDto;
import us.tx.state.dfps.service.resource.dto.ResourceSearchResultDto;
import us.tx.state.dfps.service.resourcesearch.service.ResourceSearchService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:<ResourceSearchController for ResourceSearchBean> Oct 31, 2017-
 * 6:25:50 PM © 2017 Texas Department of Family and Protective Services
 */
@Api(tags = { "resourcesearch"})
@RestController
@RequestMapping("/resourceSearch")
public class ResourceSearchController {

	@Autowired
	private ResourceSearchService resourceSearchService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger(ResourceSearchController.class);

	/**
	 * Method Name: getResourceDetails Method Description: This method gets the
	 * details of the resource based on the resourceId.
	 * 
	 * @param resourceSearchNewReq
	 * @return resourceSearchRes
	 */
	@RequestMapping(value = "/getResourceDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceSearchRes getResourceDetails(
			@RequestBody ResourceSearchResultReq resourceSearchNewReq) {
		LOG.debug("Entering method getResourceDetails in ResourceSearchController");
		if (TypeConvUtil.isNullOrEmpty(resourceSearchNewReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.resourceId.mandatory", null, Locale.US));
		}
		ResourceSearchRes resourceSearchRes = new ResourceSearchRes();
		resourceSearchRes.setResourceSearchValueDto(
				resourceSearchService.getResourceDetails(resourceSearchNewReq.getIdResource().toString()));
		LOG.info("TransactionId :" + resourceSearchNewReq.getTransactionId());
		LOG.debug("Exiting method getResourceDetails in ResourceSearchController");
		return resourceSearchRes;
	}

	/**
	 * Method Name: getResourceDetailsUsingIdPlcmtEvent Method Description: This
	 * method retrieves Resource Details using Placement Event Id.
	 * 
	 * @param resourceSearchNewReq
	 * @return resourceSearchRes
	 */
	@RequestMapping(value = "/getResourceDetailsUsingIdPlcmtEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceSearchRes getResourceDetailsUsingIdPlcmtEvent(
			@RequestBody ResourceSearchResultReq resourceSearchNewReq) {
		LOG.debug("Entering method getResourceDetailsUsingIdPlcmtEvent in ResourceSearchController");
		if (TypeConvUtil.isNullOrEmpty(resourceSearchNewReq.getIdPlcmtEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.idPlcmtEvent.mandatory", null, Locale.US));
		}
		ResourceSearchRes resourceSearchRes = new ResourceSearchRes();
		resourceSearchRes.setResourceValueBeanDto(
				resourceSearchService.getResourceDetailsUsingIdPlcmtEvent(resourceSearchNewReq.getIdPlcmtEvent()));
		LOG.info("TransactionId :" + resourceSearchNewReq.getTransactionId());
		LOG.debug("Exiting method getResourceDetailsUsingIdPlcmtEvent in ResourceSearchController");
		return resourceSearchRes;
	}

	/**
	 * Method Name: getChildResources Method Description: This method retrieves
	 * the Child Resources of the given type for the Parent Resource.
	 * 
	 * @param resourceSearchNewReq
	 * @return resourceSearchRes
	 */
	@RequestMapping(value = "/getChildResources", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceSearchRes getChildResources(
			@RequestBody ResourceSearchResultReq resourceSearchNewReq) {
		LOG.debug("Entering method getChildResources in ResourceSearchController");
		if (TypeConvUtil.isNullOrEmpty(resourceSearchNewReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.resourceId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(resourceSearchNewReq.getCdRsrcLinkType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.cdRsrcLinkType.mandatory", null, Locale.US));
		}
		ResourceSearchRes resourceSearchRes = new ResourceSearchRes();
		resourceSearchRes.setResourceValueBeanDtoList(resourceSearchService
				.getChildResources(resourceSearchNewReq.getIdResource(), resourceSearchNewReq.getCdRsrcLinkType()));
		LOG.info("TransactionId :" + resourceSearchNewReq.getTransactionId());
		LOG.debug("Exiting method getChildResources in ResourceSearchController");
		return resourceSearchRes;
	}

	/**
	 * Method Name: selectResourceDetails Method Description: This method
	 * retrieves Resource Details using idResource
	 * 
	 * @param resourceSearchNewReq
	 * @return resourceSearchRes
	 */
	@RequestMapping(value = "/selectResourceDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceSearchRes selectResourceDetails(
			@RequestBody ResourceSearchResultReq resourceSearchNewReq) {
		LOG.debug("Entering method selectResourceDetails in ResourceSearchController");
		if (TypeConvUtil.isNullOrEmpty(resourceSearchNewReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.resourceId.mandatory", null, Locale.US));
		}
		ResourceSearchRes resourceSearchRes = new ResourceSearchRes();
		resourceSearchRes.setResourceValueBeanDto(
				resourceSearchService.selectResourceDetails(resourceSearchNewReq.getIdResource()));
		LOG.info("TransactionId :" + resourceSearchNewReq.getTransactionId());
		LOG.debug("Exiting method selectResourceDetails in ResourceSearchController");
		return resourceSearchRes;
	}

	/**
	 * Method Name: searchResources Method Description: Search for Resources
	 * given the specified parameters.
	 * 
	 * @param resourceSearchNewReq
	 * @return resourceSearchRes
	 */
	@ApiOperation(value="Search Resources", tags = { "resourcesearch"} )
	@RequestMapping(value = "/searchResources", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceSearchRes searchResources(@RequestBody ResourceSearchResultReq resourceSearchNewReq) {
		LOG.debug("Entering method searchResources in ResourceSearchController");
		if (TypeConvUtil.isNullOrEmpty(resourceSearchNewReq.getResourceSearchValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.ResourceSearchValueDto.mandatory", null, Locale.US));
		}

		ResourceSearchRes resourceSearchRes = resourceSearchService
				.searchResources(resourceSearchNewReq.getResourceSearchValueDto());
		LOG.info("TransactionId :" + resourceSearchNewReq.getTransactionId());
		LOG.debug("Exiting method searchResources in ResourceSearchController");
		return resourceSearchRes;
	}
	
	/**
	 * Method Name: getResourceDetails Method Description: This method gets the
	 * details of the resource based on the resourceId and district name.
	 * 
	 * @param resourceSearchNewReq
	 * @return resourceSearchRes
	 */
	@RequestMapping(value = "/getResourceDlts", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceRes getResourceDtls(
			@RequestBody ResourceReq resourceReq) {
		LOG.debug("Entering method getResourceDetails in ResourceSearchController");
		if (TypeConvUtil.isNullOrEmpty(resourceReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.resourceId.mandatory", null, Locale.US));
		}
		ResourceRes resourceRes = new ResourceRes();
		resourceRes.setResourceDto(
				resourceSearchService.getResourceDtls(resourceReq.getIdResource()));
		LOG.info("TransactionId :" + resourceReq.getTransactionId());
		LOG.debug("Exiting method getResourceDetails in ResourceSearchController");
		return resourceRes;
	}

	/**
	 * Method Name: searchVendor Method Description: Search for Resources
	 * given the specified parameters.
	 *
	 * @param ResourceSearchResultDto
	 * @return resourceSearchRes
	 */
	@ApiOperation(value = "ResourceSearch", tags = { "resourcesearch" })
	@RequestMapping(value = "/searchVendor", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceSearchRes searchVendor(@RequestBody List<ResourceSearchResultDto> result) {
		LOG.debug("Entering method searchResources in ResourceSearchController");
		if (result==null) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.ResourceSearchValueDto.mandatory", null, Locale.US));
		}
		ResourceSearchRes resourceSearchRes = resourceSearchService.getVendorDetails(result);


		return resourceSearchRes;
	}

	/**
	 * Method Name: getContractInfo Method Description: Search for contact period by resource
	 * given the specified parameters.
	 *
	 * @param idFacilities
	 * @return Map<Long, ContractPeriodDto>
	 */
	@ApiOperation(value = "ResourceSearch", tags = { "resourcesearch" })
	@RequestMapping(value = "/getContractInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Map<Long, ContractPeriodDto> getContractInfo(@RequestBody  List<String> idFacilities) {
		LOG.debug("Entering method searchResources in ResourceSearchController");
		if (idFacilities==null) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.ResourceSearchValueDto.mandatory", null, Locale.US));
		}
		return resourceSearchService.getContractDetails(idFacilities);
	}

	/**
	 * Method Name: getContractInfo Method Description: Search for contact period by resource
	 * given the specified parameters.
	 *
	 *
	 */
	@ApiOperation(value = "ResourceSearch", tags = { "resourcesearch" })
	@RequestMapping(value = "/getFcl02Resources", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public List<Long> getFcl02Resources(@RequestBody List<String> idFacilities) {
		LOG.debug("Entering method getFcl02Resources in ResourceSearchController");
		return resourceSearchService.getFcl02Resources(idFacilities);
	}
}
