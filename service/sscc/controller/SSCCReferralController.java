package us.tx.state.dfps.service.sscc.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.common.request.SSCCReferralReq;
import us.tx.state.dfps.service.common.response.SSCCReferralRes;
import us.tx.state.dfps.service.sscc.service.SSCCRefService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * Controller for SSCC Referral Detail page Aug 15, 2018- 10:57:28 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Api(tags = { "referral" })
@RestController
@RequestMapping("/SSCCRefDetail")
public class SSCCReferralController {

	/** The sscc ref service. */
	@Autowired
	SSCCRefService ssccRefService;

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getReferralDetails Method Description: This method is used
	 * to fetch details for the existing sscc referral.
	 *
	 * @param ssccReferralReq
	 *            the sscc referral req
	 * @return the referral details
	 */
	@ApiOperation(value = "Get sscc referral details", tags = { "referral" })
	@RequestMapping(value = "/getReferralDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes getReferralDetails(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.fetchReferralById(ssccReferralReq.getIdSSCCReferral(), ssccReferralReq.getUserId(),
				ssccReferralReq.isFixerRight());
	}

	/**
	 * Method Name: getNewReferral Method Description: This method fetches the
	 * details when user tries to add sscc referral.
	 *
	 * @param ssccReferralReq
	 *            the sscc referral req
	 * @return the new referral
	 */

	@ApiOperation(value = "Get new referral", tags = { "referral" })
	@RequestMapping(value = "/getNewReferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes getNewReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idCase.mandatory", null, Locale.US));
		return ssccRefService.fetchSSCCRefHeaderDataForNewReferral(ssccReferralReq);
	}

	/**
	 * 
	 * Method Name: saveRefHeader Method Description: This method validates and
	 * saves the sscc referral header information
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@ApiOperation(value = "Get referral header", tags = { "referral" })
	@RequestMapping(value = "/saveRefHeader", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes saveRefHeader(@RequestBody SSCCReferralReq ssccReferralReq) {
		return ssccRefService.saveSSCCReferralHeader(ssccReferralReq.getSsccRefDto());
	}

	/**
	 * 
	 * Method Name: deleteRefHeader Method Description: This method deletes the
	 * sscc referral header information
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@RequestMapping(value = "/deleteRefHeader", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes deleteRefHeader(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.idSSCCReferral.mandatory", null, Locale.US));
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		Long idSSCCReferral = ssccRefService.deleteSSCCReferralHeader(ssccReferralReq.getIdSSCCReferral());
		ssccReferralRes.setIdSSCCReferral(idSSCCReferral);
		return ssccReferralRes;
	}

	/**
	 * 
	 * Method Name: saveAndTransmitReferral Method Description: This method
	 * calls the service when user clicks on save and transmit button.
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@ApiOperation(value = "Save and transmit referral", tags = { "referral" })
	@RequestMapping(value = "/saveAndTransmitReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes saveAndTransmitReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.saveAndTransmitSSCCReferral(ssccReferralReq.getSsccRefDto(),
				ssccReferralReq.getSsccListDto(), ssccReferralReq.getUserId());
	}

	/**
	 * 
	 * Method Name: finalizeSSCCReferralDischarge Method Description: Method is
	 * invoked when user clicks on the Finalize Discharge button on the SSCC
	 * Referral Detail page
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@ApiOperation(value = "Finalizing referral discharge", tags = { "referral" })
	@RequestMapping(value = "/finalizeSSCCReferralDischarge", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes finalizeSSCCReferralDischarge(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.finalizeSSCCReferralDischarge(ssccReferralReq.getSsccRefDto(),
				ssccReferralReq.getUserId());
	}

	/**
	 * Method Name:undoDischargeSSCCReferral. Method Description:Method is
	 * invoked when SSCC Fixer clicks on Undo Discharge button
	 * 
	 * @param SSCCRefDto
	 *            ssccRefDto
	 * @param SSCCReferralRes
	 *
	 */
	@RequestMapping(value = "/undoDischargeSSCCReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes undoDischargeSSCCReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.undoDischargeSSCCReferral(ssccReferralReq.getSsccRefDto(), ssccReferralReq.getUserId());
	}

	/**
	 * 
	 * Method Name: updateAndNotifySSCCReferral Method Description: This method
	 * calls the service business when user clicks on notify and update button.
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@ApiOperation(value = "Update and notify sscc referral", tags = { "referral" })	
	@RequestMapping(value = "/updateAndNotifySSCCReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes updateAndNotifySSCCReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.updateAndNotifySSCCReferral(ssccReferralReq.getSsccRefDto(), ssccReferralReq.getUserId());
	}

	/**
	 * 
	 * Method Name: acknowledgeSSCCReferral Method Description: This method
	 * calls the service business when user clicks on acknowledge button.
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@ApiOperation(value = "Acknowledge sscc referral", tags = { "referral" })	
	@RequestMapping(value = "/acknowledgeSSCCReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes acknowledgeSSCCReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.acknowledgeSSCCReferral(ssccReferralReq.getSsccRefDto(), ssccReferralReq.getUserId());
	}

	/**
	 * 
	 * Method Name: acknowledgeDischargeSSCCReferral Method Description: This
	 * method calls the service business when user clicks on acknowledge
	 * Discharge button.
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@RequestMapping(value = "/acknowledgeDischargeSSCCReferral", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes acknowledgeDischargeSSCCReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.acknowledgeDischargeSSCCReferral(ssccReferralReq.getSsccRefDto(),
				ssccReferralReq.getUserId());
	}

	/**
	 * 
	 * Method Name: deleteSSCCReferral Method Description: This method calls the
	 * service business when user clicks on delete button.
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@RequestMapping(value = "/deleteSSCCReferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes deleteSSCCReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.deleteSSCCReferral(ssccReferralReq.getSsccRefDto());
	}

	/**
	 * 
	 * Method Name: addSSCCRefFamilyPerson Method Description: This method is
	 * used when the user tries to add person
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@ApiOperation(value = "Add person to sscc family referral", tags = { "referral" })
	@RequestMapping(value = "/addSSCCRefFamilyPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes addSSCCRefFamilyPerson(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.addOrRemoveSSCCRefFamilyPerson(ssccReferralReq.getSsccRefDto(),
				ssccReferralReq.getUserId(), false);
	}

	/**
	 * 
	 * Method Name: removeSSCCRefFamilyPerson Method Description: This method is
	 * used when user tries to remove the person from family referral
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@ApiOperation(value = "Delete person from sscc family referral", tags = { "referral" })
	@RequestMapping(value = "/removeSSCCRefFamilyPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes removeSSCCRefFamilyPerson(@RequestBody SSCCReferralReq ssccReferralReq) {
		if (ObjectUtils.isEmpty(ssccReferralReq.getSsccRefDto().getIdSSCCReferral()))
			throw new InvalidRequestException(
					messageSource.getMessage("ssccReferralReq.ssccRefDto.idSSCCReferral.mandatory", null, Locale.US));
		return ssccRefService.addOrRemoveSSCCRefFamilyPerson(ssccReferralReq.getSsccRefDto(),
				ssccReferralReq.getUserId(), true);
	}
	
	/**
	 * 
	 * Method Name: removeSSCCRefFamilyPerson Method Description: This method is
	 * used when user tries to remove the person from family referral
	 * 
	 * @param ssccReferralReq
	 * @return
	 */
	@RequestMapping(value = "/activeSSCCRefBystage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes getSSCCRefByStageId(@RequestBody SSCCReferralReq ssccReferralReq) {
		
		return ssccRefService.retrieveSSCCRefByStageId(ssccReferralReq.getSsccRefDto().getIdStage());
	}

	/**
	 * code added for artf231094
	 * @param ssccReferralReq
	 * @return
	 */
	@RequestMapping(value = "/fetchSSCCRefCount", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SSCCReferralRes fetchSSCCRefCount(@RequestBody SSCCReferralReq ssccReferralReq) {
		return ssccRefService.getSSCCRefCount(ssccReferralReq.getSsccRefDto());
	}
	
}
