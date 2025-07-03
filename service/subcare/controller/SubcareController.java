package us.tx.state.dfps.service.subcare.controller;

import java.util.LinkedList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.common.exception.PersonNotFoundException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CvsFaHomeReq;
import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.common.request.ResourceReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CvsFaHomeRes;
import us.tx.state.dfps.service.common.response.ResourceRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.subcare.dto.ResourceRtbExceptionDto;
import us.tx.state.dfps.service.subcare.service.CapsResourceService;
import us.tx.state.dfps.service.subcare.service.CvsFaHomeService;
import us.tx.state.dfps.service.subcare.service.SubcareCaseService;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CVSFaHome
 * Class Description: Controller class for services related to Subcare. Apr
 * 20,2017 - 4:29:18 PM
 */

@RestController
@RequestMapping("/subcare")
public class SubcareController {

	@Autowired
	CvsFaHomeService cvsFaHomeService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	CapsResourceService capsResourceService;

	@Autowired
	SubcareCaseService subcareCaseService;

	/**
	 * 
	 * Method Description: Method to retrieve Person details to populate the CVS
	 * Home window. This method is also called in the save and update
	 * functionalities. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	@RequestMapping(value = "/getcvsfahome", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CvsFaHomeRes getCvsFaHomelDetails(@RequestBody CvsFaHomeReq cvsFaHomeReq) {

		CvsFaHomeRes cvsFaHomeRes = new CvsFaHomeRes();

		cvsFaHomeRes = cvsFaHomeService.getCvsFaHomeDetails(cvsFaHomeReq);
		cvsFaHomeRes.setReturnMsg(ServiceConstants.SUCCESS);

		return cvsFaHomeRes;
	}

	@ExceptionHandler(PersonNotFoundException.class)
	public CvsFaHomeRes InvalidRequestExceptionHandler() {

		CvsFaHomeRes cvsFaHomeRes = new CvsFaHomeRes();
		cvsFaHomeRes.setReturnMsg("Person_not_Found");
		return cvsFaHomeRes;

	}

	/**
	 * 
	 * Method Description: Method to save person details in the CVS Home window.
	 * This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */

	@RequestMapping(value = "/savecvsfahome", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CvsFaHomeRes saveCvsFaHome(@RequestBody CvsFaHomeReq cvsFaHomeReq) {

		return cvsFaHomeService.saveCvsFaHome(cvsFaHomeReq);
	}

	/**
	 * 
	 * Method Description: Method to update person details in the CVS Home
	 * window. This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */

	@RequestMapping(value = "/updatecvsfahome", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CvsFaHomeRes updateCvsFaHome(@RequestBody CvsFaHomeReq cvsFaHomeReq) {

		CvsFaHomeRes cvsFaHomeRes = new CvsFaHomeRes();

		cvsFaHomeRes = cvsFaHomeService.updateCvsFaHome(cvsFaHomeReq);

		return cvsFaHomeRes;
	}

	/**
	 * 
	 * Method Description: Method to update person details in the CVS Home
	 * window. This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param resourceReq
	 * @return ResourceRes @
	 */

	@RequestMapping(value = "/getresourcedtl", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ResourceRes getResourceDtl(@RequestBody ResourceReq resourceReq) {
		if (TypeConvUtil.isNullOrEmpty(resourceReq)) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(resourceReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		return capsResourceService.getResourceDtl(resourceReq);

	}

	/**
	 * 
	 * Method Description:Populates form csc40o00, which Populates Subcare Case
	 * Management Tool
	 * 
	 * @param facilityInvSumReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getsubcaretool", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getSubcareCase(@RequestBody FacilityInvSumReq facilityInvSumReq) {
		if (ObjectUtils.isEmpty(facilityInvSumReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("facilityInvSumReq.idStage.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(subcareCaseService.getSubcareCase(facilityInvSumReq)));
		return commonFormRes;
	}

	@RequestMapping(value = "/checkResourceRtbException", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ResourceRes checkResourceRtbException(@RequestBody ResourceRtbExceptionDto resourceReq) {
		ResourceRtbExceptionDto rawResourceResult = capsResourceService.checkResourceRtbException(resourceReq);

		// reusing ResourceRes creates a extra overhead, but is done because if we create a dedicated response,
		// it will only get used once.
		ResourceRes retVal = new ResourceRes();
		retVal.setResourceDto(new ResourceDto());
		retVal.getResourceDto().setRtbExceptionList(new LinkedList<ResourceRtbExceptionDto>());
		retVal.getResourceDto().getRtbExceptionList().add(rawResourceResult);

		return retVal;

	}
}
