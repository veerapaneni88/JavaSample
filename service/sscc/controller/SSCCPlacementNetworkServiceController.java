package us.tx.state.dfps.service.sscc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.admin.dto.SSCCPlcmntRsrcLinkMCDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDetailDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCPlacementNetworkResourceDto;
import us.tx.state.dfps.service.common.request.SSCCPlacementNetworkReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.SSCCPlacementNetworkRes;
import us.tx.state.dfps.service.sscc.service.SSCCPlacementNetworkService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SSCCPlacementNetworkServiceController Aug 31, 2018- 5:35:24 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/placementNetwork")
public class SSCCPlacementNetworkServiceController {

	@Autowired
	SSCCPlacementNetworkService ssccPlacementNetworkService;

	/**
	 * Method Name: getSsccPlacementNetworkList
	 *
	 * Method Description: This method is the service controller method which
	 * gets SSCC Placement Network List.
	 *
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/getSsccPlacementNetworkList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes getSsccPlacementNetworkList(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();
		ssccPlacementNetworkRes.setSsccPlacementNetworkList(
				ssccPlacementNetworkService.getSsccPlacementNetworkList(ssccPlacementNetworkReq.getIdResourceSscc()));
		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: getResourceDetailsAddMode
	 *
	 * Method Description: This method is called when user clicks on Validate
	 * button on Placement Network Details page. After the Successful validation
	 * this method will be called and resource details will be displayed on the
	 * Resource Header page.
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/getResourceDetailsAddMode", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes getResourceDetailsAddMode(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {

		SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto = ssccPlacementNetworkService
				.getResourceDetailsAddMode(ssccPlacementNetworkReq.getPlacementNetworkResourceDto());
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();
		ssccPlacementNetworkRes.setSsccPlacementNetworkResourceDto(ssccPlacementNetworkResourceDto);
		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: getSsccPlacementNetworkDetails
	 *
	 * Method Description: This method is the service controller method which
	 * gets SSCC Placement Network Details
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/getSsccPlacementNetworkDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes getSsccPlacementNetworkDetails(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		SSCCPlacementNetworkResourceDto ssccPlacementNetworkResourceDto = ssccPlacementNetworkService
				.getSsccPlacementNetworkDetails(ssccPlacementNetworkReq.getPlacementNetworkResourceDto());
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();
		ssccPlacementNetworkRes.setSsccPlacementNetworkResourceDto(ssccPlacementNetworkResourceDto);
		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: addResourceToSSCCPlacementNetwork
	 *
	 * Method Description: This method is is the service controller method which
	 * adds Resource To SSCC Placement Network
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/addResourceToSSCCPlacementNetwork", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes addResourceToSSCCPlacementNetwork(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {

		ssccPlacementNetworkService.insertSsccPlcmtRsrcLink(ssccPlacementNetworkReq.getPlacementNetworkResourceDto(),
				ssccPlacementNetworkReq.getIdResourceSscc());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(Boolean.TRUE);
		return commonHelperRes;
	}

	/**
	 * Method Name: addAgencyHomeToSSCCPlacementNetwork
	 *
	 * Method Description: This method is is the service controller method which
	 * adds Agency Home To SSCC Placement Network
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/addAgencyHomeToSSCCPlacementNetwork", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes addAgencyHomeToSSCCPlacementNetwork(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		ssccPlacementNetworkService.insertAgencyHomeSsccPlcmtRsrcLink(
				ssccPlacementNetworkReq.getPlacementNetworkResourceDto(), ssccPlacementNetworkReq.getIdResourceSscc());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(Boolean.TRUE);
		return commonHelperRes;
	}

	/**
	 * Method Name: updateAgencyHomeToSSCCPlacementNetwork
	 *
	 * Method Description: This method is is the service controller method which
	 * updates Agency Home To SSCC Placement Network
	 *
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/updateAgencyHomeToSSCCPlacementNetwork", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes updateAgencyHomeToSSCCPlacementNetwork(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		ssccPlacementNetworkService
				.updateAgencyHomeSsccPlcmtRsrcLink(ssccPlacementNetworkReq.getPlacementNetworkResourceDto());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(Boolean.TRUE);
		return commonHelperRes;
	}

	/**
	 * Method Name: updateResourceToSSCCPlacementNetwork
	 * 
	 * Method Description: This method is the service controller method which
	 * updates Resource to SSCC Placement Network
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/updateResourceToSSCCPlacementNetwork", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes updateResourceToSSCCPlacementNetwork(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		ssccPlacementNetworkService.updateSsccPlcmtRsrcLink(ssccPlacementNetworkReq.getPlacementNetworkResourceDto());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(Boolean.TRUE);
		return commonHelperRes;
	}

	/**
	 * Method Name: addMCToSSCCPlacementNetwork
	 *
	 * Method Description: This method is the service controller method which
	 * adds a SSCCPlcmntRsrcLinkMC record
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/addMCToSSCCPlacementNetwork", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes addMCToSSCCPlacementNetwork(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		ssccPlacementNetworkService.insertSsccPlcmntRsrcLinkMC(ssccPlacementNetworkReq.getPlcmntRsrcLinkMCDto());
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(Boolean.TRUE);
		return commonHelperRes;
	}

	/**
	 * Method Name: updateMCToSSCCPlacementNetwork
	 *
	 * Method Description: This method is the service controller method which
	 * updates a SSCCPlcmntRsrcLinkMC record
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/updateMCToSSCCPlacementNetwork", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes updateMCToSSCCPlacementNetwork(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {

		ssccPlacementNetworkService.updateSsccPlcmntRsrcLinkMC(ssccPlacementNetworkReq.getPlcmntRsrcLinkMCDto());
		CommonHelperRes response = new CommonHelperRes();
		response.setResult(Boolean.TRUE);
		return response;
	}

	/**
	 * Method Name: removeMCFromSSCCPlacementNetwork
	 *
	 * Method Description: This method is the service controller method which
	 * removes a SSCCPlcmntRsrcLinkMC record
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/removeMCFromSSCCPlacementNetwork", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes removeMCFromSSCCPlacementNetwork(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {

		ssccPlacementNetworkService.removeSsccPlcmntRsrcLinkMC(ssccPlacementNetworkReq.getPlcmntRsrcLinkMCDto());
		CommonHelperRes response = new CommonHelperRes();
		response.setResult(Boolean.TRUE);
		return response;
	}
	/**
	 * Method Name: getPlcmntRsrcLinkMCById
	 *
	 * Method Description: This method retrieves the SSCCPlcmntRsrcLinkMC record
	 * based on the primary key
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/getPlcmntRsrcLinkMCById", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes getPlcmntRsrcLinkMCById(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {

		SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto = ssccPlacementNetworkService.getSSCCPlcmntRsrcLinkMCById(
				ssccPlacementNetworkReq.getPlcmntRsrcLinkMCDto().getIdSSCCPlcmtRsrcLinkMC());
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();
		ssccPlacementNetworkRes.setSsccPlcmntRsrcLinkMCDto(ssccPlcmntRsrcLinkMCDto);
		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: getRsrcMedCnsntrByRsrcPrsn
	 *
	 * Method Description: This method returns the active count of medical
	 * consenter records for a CPA or other facility med consenter person
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/getRsrcMedCnsntrByRsrcPrsn", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes getRsrcMedCnsntrByRsrcPrsn(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {

		SSCCPlcmntRsrcLinkMCDto ssccPlcmntRsrcLinkMCDto = ssccPlacementNetworkReq.getPlcmntRsrcLinkMCDto();
		ssccPlcmntRsrcLinkMCDto = ssccPlacementNetworkService.getRsrcMedCnsntrByRsrcPrsn(ssccPlcmntRsrcLinkMCDto);
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();
		ssccPlacementNetworkRes.setSsccPlcmntRsrcLinkMCDto(ssccPlcmntRsrcLinkMCDto);
		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: isAgncyHmActvInOtherRsrc
	 *
	 * Method Description: This method is the service controller method which
	 * checks if the Inactive Agency Hm in under another Active CPA
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/isAgncyHmActvInOtherRsrc", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes isAgncyHmActvInOtherRsrc(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();
		ssccPlacementNetworkRes.setIndAgncyHmActvInOtherRsrc(ssccPlacementNetworkService.isAgncyHmActvInOtherRsrc(
				ssccPlacementNetworkReq.getPlacementNetworkResourceDto().getSsccPlacementNetworkResourceDetailDto()));
		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: getSsccResourceHeaderDetails
	 *
	 * Method Description: This method is service controller method for getting
	 * SSCCParameterDto.
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/getSsccResourceHeaderDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes getSsccResourceHeaderDetails(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();

		ssccPlacementNetworkRes.setSsccParameterDto(
				ssccPlacementNetworkService.getSsccResourceHeaderDetails(ssccPlacementNetworkReq.getIdResourceSscc()));
		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: getCPANetworkStatus
	 *
	 * Method Description: This method is the service controller method which to
	 * get CPA Network Status
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/getCPANetworkStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes getCPANetworkStatus(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();

		ssccPlacementNetworkRes.setCpaNetworkStatus(ssccPlacementNetworkService.getCPANetworkStatus(
				ssccPlacementNetworkReq.getPlacementNetworkResourceDto().getSsccPlacementNetworkResourceDetailDto()
						.getIdRsrcCpa(),
				ssccPlacementNetworkReq.getPlacementNetworkResourceDto().getSsccPlacementNetworkResourceDetailDto()
						.getIdRsrcSscc()));

		return ssccPlacementNetworkRes;
	}

	/**
	 * Method Name: hasPlacementOpen
	 *
	 * Method Description: This method is the service controller method which
	 * checks whether Resource has Placement open or ended.
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/hasPlacementOpen", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes hasPlacementOpen(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		SSCCPlacementNetworkResourceDetailDto resourceDetailDto = ssccPlacementNetworkReq
				.getPlacementNetworkResourceDto().getSsccPlacementNetworkResourceDetailDto();
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(ssccPlacementNetworkService.hasPlacementOpen(resourceDetailDto.getIdResource(),
				resourceDetailDto.getIdRsrcSscc(), resourceDetailDto.getCdPlcmtRsrcLinkType(),
				resourceDetailDto.getDtStart()));
		return commonHelperRes;
	}

	/**
	 * Method Name: validateResourceId
	 *
	 * Method Description: This method is the service controller method which
	 * validates resource id
	 * 
	 * @param ssccPlacementNetworkReq
	 * @return SSCCPlacementNetworkRes
	 */
	@RequestMapping(value = "/validateResourceId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlacementNetworkRes validateResourceId(
			@RequestBody SSCCPlacementNetworkReq ssccPlacementNetworkReq) {
		SSCCPlacementNetworkRes ssccPlacementNetworkRes = new SSCCPlacementNetworkRes();
		ssccPlacementNetworkRes.setSsccPlacementNetworkValidationDto(ssccPlacementNetworkService.validateResourceId(
				ssccPlacementNetworkReq.getPlacementNetworkResourceDto().getSsccPlacementNetworkResourceDetailDto()));
		return ssccPlacementNetworkRes;
	}
}
