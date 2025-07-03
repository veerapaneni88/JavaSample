package us.tx.state.dfps.service.resourcedetail.controller;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.response.ResourceDetailRes;
import us.tx.state.dfps.service.common.response.SchoolDistrictRes;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailOutDto;
import us.tx.state.dfps.service.resource.detail.dto.SchoolDistrictReq;
import us.tx.state.dfps.service.resource.dto.SchoolDistrictDto;
import us.tx.state.dfps.service.resourcedetail.service.ResourceDetailService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Formats
 * input record and updates the Phone, Address, CAPS Resource and Category
 * tables. If the Resource is of type hotline, then no address records are
 * required. © 2017 Texas Department of Family and Protective Cres04s
 */
@RestController
@RequestMapping("/resourceDetail")
public class ResourceDetailController {

	private static final Logger log = Logger.getLogger(ResourceDetailController.class);

	@Autowired
	private ResourceDetailService resourceDetailService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * Method Name: handleResourceDetails Method Description:Formats input
	 * record and updates the Phone, Address, Email, CAPS Resource and Category
	 * tables. If the Resource is of type hotline, then no address records are
	 * required.
	 * 
	 * @param resourceDetailInDto
	 * @return resourceDetailRes
	 */
	@RequestMapping(value = "/handleResourceDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ResourceDetailRes handleResourceDetails(@RequestBody ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method handleResourceDetails in ResourceDetailController");
		ResourceDetailRes resourceDetailRes = new ResourceDetailRes();
		/*
		 * if (ObjectUtils.isEmpty(resourceDetailInDto.getIdResource())) { throw
		 * new InvalidRequestException(messageSource.getMessage(
		 * "caps.resource.resourceId", null, Locale.US)); }
		 */
		ResourceDetailOutDto resourceDetailOutDto = resourceDetailService.saveResourceDetails(resourceDetailInDto);
		resourceDetailRes.setResourceDetailOutDto(resourceDetailOutDto);
		log.debug("Exiting method handleResourceDetails in ResourceDetailController");
		return resourceDetailRes;
	}

	/**
	 * This is a list service that retrieves all of the school districts for a
	 * particular county passed from the Resource Address window
	 *
	 * @param schoolDistrictReq
	 * @return schoolDistrictRes
	 */
	@RequestMapping(value = "/schoolDistrict", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SchoolDistrictRes getSchoolDistrict(@RequestBody SchoolDistrictReq schoolDistrictReq) {
		log.debug("Entering method getSchoolDistrict in ResourceDetailController");
		if (TypeConvUtil.isNullOrEmpty(schoolDistrictReq.getScrAddrGenericAddrCnty())) {
			throw new InvalidRequestException(
					messageSource.getMessage("schoolDistrictReq.scrAddrGenericAddrCnty.mandatory", null, Locale.US));
		}
		List<SchoolDistrictDto> schoolDistrictDtoList = resourceDetailService.getSchooldistrict(schoolDistrictReq);
		SchoolDistrictRes schoolDistrictRes = new SchoolDistrictRes();
		schoolDistrictRes.setSchoolDistrictDto(schoolDistrictDtoList);
		log.debug("Exiting method getSchoolDistrict in ResourceDetailController");
		return schoolDistrictRes;
	}

	@GetMapping(value = "/stage/{idStage}")
	public Long getResourceIdByStageId(@PathVariable(value = "idStage") Long idStage) {
		log.debug("Entering method getResourceIdByStageId in ResourceDetailController");
		return resourceDetailService.getResourceIdByStageId(idStage);
	}

}