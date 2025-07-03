/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 9, 2018- 2:37:19 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.sscc.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SSCCOptCircumListReq;
import us.tx.state.dfps.service.common.request.SSCCPlcmtOptCircumReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.SSCCOptCircumListRes;
import us.tx.state.dfps.service.common.response.SSCCPlcmtMedCnsntrRtrvRes;
import us.tx.state.dfps.service.common.response.SSCCPlcmtOptCircumRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto;
import us.tx.state.dfps.service.sscc.service.SSCCOptCircumListService;
import us.tx.state.dfps.service.sscc.service.SSCCPlcmtOptCircumService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * contains all the service calls to SSCCOptCircumListService and
 * SSCCOptCircumService Aug 9, 2018- 2:37:19 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/ssccOptCircumSvcController")
public class SSCCOptCircumSvcController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SSCCOptCircumListService ssccOptCircumListService;

	@Autowired
	SSCCPlcmtOptCircumService ssccPlcmtOptCircumService;

	private static final Logger log = Logger.getLogger(SSCCOptCircumSvcController.class);

	/**
	 * Method Name: fetchSSCCOptCircumList Method Description: This is the
	 * service method called for the display of SSCC Placement Options and
	 * Circumstances List
	 * 
	 * @param commonStageIdReq
	 * @return SSCCOptCircumListRes
	 */
	@RequestMapping(value = "/fetchSSCCOptCircumList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCOptCircumListRes fetchSSCCOptCircumList(
			@RequestBody SSCCOptCircumListReq ssccOptCircumListReq) {
		log.debug("Entering method fetchSSCCOptCircumList in SSCCOptCircumSvcController");

		if (TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getUserDto())
				&& TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getUserDto().getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idUser.mandatory", null, Locale.US));
		}
		SSCCOptCircumListRes ssccOptCircumListRes = new SSCCOptCircumListRes();
		SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto = new SSCCPlcmtOptCircumDto();
		ssccPlcmtOptCircumDto.setIdStage(ssccOptCircumListReq.getIdStage());
		ssccPlcmtOptCircumDto.setIdUser(ssccOptCircumListReq.getUserDto().getIdUser());
		// service call that update the ssccPlcmtOptCircumDto with page mode and
		// user role
		ssccPlcmtOptCircumService.setUpSSCCPlcmtOptCircum(ssccPlcmtOptCircumDto);
		ssccOptCircumListRes.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumDto);
		// service call to fetch the list of options and circumstances
		ssccOptCircumListRes.setSsccPlcmtOptCircumListDto(
				ssccOptCircumListService.fetchSSCCOptCircumList(ssccOptCircumListReq.getIdStage()));
		log.debug("Exiting method fetchSSCCOptCircumList in SSCCOptCircumSvcController");
		return ssccOptCircumListRes;
	}

	/**
	 * Method Name: fetchSSCCOptCircumDetails Method Description: Rest
	 * Controller Method for Fetching SSCCPlcmtnOptCircum Details.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return SSCCPlcmtOptCircumRes
	 */
	@RequestMapping(value = "/fetchSSCCOptCircumDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes fetchSSCCOptCircumDetails(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method fetchSSCCOptCircumDetails of SSCCOptCircumSvcController");
		if (TypeConvUtil.isNullOrEmpty(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(
				ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto().getSsccPlcmtHeaderDto().getIdSSCCPlcmtHeader())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCPlcmntHeader.mandatory", null, Locale.US));
		}
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		response.setSsccPlcmtOptCircumDto(
				ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto()));
		log.info("Exiting method fetchSSCCOptCircumDetails of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: rescindFromList Method Description: This is the service
	 * method that handles the request to rescind an SSCC option/circumstance
	 * from the List page.
	 * 
	 * @param ssccOptCircumListReq
	 * @return SSCCOptCircumListRes
	 */
	@RequestMapping(value = "/rescindFromList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCOptCircumListRes rescindFromList(@RequestBody SSCCOptCircumListReq ssccOptCircumListReq) {
		log.debug("Entering method rescindFromList in SSCCOptCircumSvcController");

		// if (TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getIdStage())) {
		// throw new
		// InvalidRequestException(messageSource.getMessage("common.idStage.mandatory",
		// null, Locale.US));
		// }
		// if (TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getUserDto())
		// &&
		// TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getUserDto().getIdUser()))
		// {
		// throw new
		// InvalidRequestException(messageSource.getMessage("common.idUser.mandatory",
		// null, Locale.US));
		// }
		if (TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getSsccPlcmtOptCircumDto())
				&& TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getSsccPlcmtOptCircumDto().getSsccPlcmtHeaderDto())
				&& TypeConvUtil.isNullOrEmpty(ssccOptCircumListReq.getSsccPlcmtOptCircumDto().getSsccPlcmtHeaderDto()
						.getIdSSCCPlcmtHeader())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCPlcmntHeader.mandatory", null, Locale.US));
		}
		SSCCOptCircumListRes ssccOptCircumListRes = new SSCCOptCircumListRes();
		SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto = ssccOptCircumListReq.getSsccPlcmtOptCircumDto();
		ssccPlcmtOptCircumDto = ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCircumDto);
		// rescind and fetch the revised list of options and circumstances
		ssccOptCircumListService.rescind(ssccPlcmtOptCircumDto);
		ssccOptCircumListRes.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumDto);
		log.debug("Exiting method rescindFromList in SSCCOptCircumSvcController");
		return ssccOptCircumListRes;
	}

	/**
	 * Method Name: saveSSCCOptCircumDetails Method Description: This method
	 * calls the save service for the SSCC Option and Circumstance Details
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/saveSSCCPlcmtOptCircum", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes saveSSCCOptCircumDetails(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		if (TypeConvUtil.isNullOrEmpty(
				ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto().getSsccPlcmtHeaderDto().getIdSSCCReferral())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCReferral.mandatory", null, Locale.US));
		}
		SSCCPlcmtOptCircumRes resonse = new SSCCPlcmtOptCircumRes();
		resonse.setSsccPlcmtOptCircumDto(
				ssccPlcmtOptCircumService.save(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto()));
		return resonse;
	}

	/**
	 * Method Name: saveAndContinueSSCCPlcmtOptCircum Method Description: Rest
	 * Controller Method for Save and Continue of SSCCPlcmtOptCircum.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return SSCCPlcmtOptCircumRes
	 */
	@RequestMapping(value = "/saveAndContinueSSCCPlcmtOptCircum", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes saveAndContinueSSCCPlcmtOptCircum(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method saveAndContinueSSCCPlcmtOptCircum of SSCCOptCircumSvcController");
		if (TypeConvUtil.isNullOrEmpty(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto().getIdActiveRef())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCReferral.mandatory", null, Locale.US));
		}
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		response.setSsccPlcmtOptCircumDto(
				ssccPlcmtOptCircumService.saveAndContinue(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto()));
		log.info("Returning Response saveAndContinueSSCCPlcmtOptCircum of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: saveOnValidate Method Description: Rest controller method to
	 * handle saveOnValidate Request.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/saveOnValidate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes saveOnValidate(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method saveOnValidate of SSCCOptCircumSvcController");
		if (TypeConvUtil.isNullOrEmpty(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto().getIdActiveRef())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCReferral.mandatory", null, Locale.US));
		}
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto = ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto();
		// Check if service need to calculate the Error Warning.
		boolean checkErrorWarn = ServiceConstants.VALIDATE.equalsIgnoreCase(ssccPlcmtOptCircumDto.getBtnClicked())
				|| ServiceConstants.REVALIDATE.equalsIgnoreCase(ssccPlcmtOptCircumDto.getBtnClicked());
		ssccPlcmtOptCircumDto = ssccPlcmtOptCircumService
				.saveOnValidate(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto(), checkErrorWarn);
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumDto);
		// Set the Timeline Dto before returning response.
		ssccPlcmtOptCircumDto.setSsccTimelineList(ssccPlcmtOptCircumService.fetchSSCCTimeLine(ssccPlcmtOptCircumDto));
		log.info("Returning Response saveOnValidate of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: setResource Method Description: Rest Controller method for
	 * Seting the SSCCPlcmtOptCircum Bean with Selectd Resource.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/setResourceToSSCCPlcmtOpt", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes setResource(@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method saveAndContinueSSCCPlcmtOptCircum of SSCCOptCircumSvcController");
		if (TypeConvUtil.isNullOrEmpty(ssccPlcmtOptCircumReq.getSelectedResource())) {
			throw new InvalidRequestException(messageSource.getMessage("caps.resource.resourceID", null, Locale.US));
		}
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.setResource(
				ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto(), ssccPlcmtOptCircumReq.getSelectedResource()));
		log.info("Returning Response saveAndContinueSSCCPlcmtOptCircum of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: retrieveMedCnsntrDtl Method Description: Rest Controller
	 * method for Fetching the Details of Selected Med Concenter.
	 * 
	 * @param ssccPlcmtOptCirumReq
	 * @return response
	 */
	@RequestMapping(value = "/retrieveMedCnsntrDtl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtMedCnsntrRtrvRes retrieveMedCnsntrDtl(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCirumReq) {
		log.info("Entering method retrieveMedCnsntrDtl of SSCCOptCircumSvcController");
		SSCCPlcmtMedCnsntrRtrvRes response = new SSCCPlcmtMedCnsntrRtrvRes();
		response.setSsccPlcmtMedCnsntrDto(ssccPlcmtOptCircumService.setSelTypeAndData(
				ssccPlcmtOptCirumReq.getSsccPlcmtMedCnsntrDto(), ssccPlcmtOptCirumReq.getSsccPlcmtOptCircumDto()));
		log.info("Returing response retrieveMedCnsntrDtl of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * 
	 * Method Name: cancelAndUpdateSSCCPlcmtOptCircum Method Description: Rest
	 * Controller method to delete the placement information.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/cancelAndRestartSSCCPlcmtOptCircum", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes cancelAndUpdateSSCCPlcmtOptCircum(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method cancelAndUpdateSSCCPlcmtOptCircum of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		response.setSsccPlcmtOptCircumDto(
				ssccPlcmtOptCircumService.cancelAndRestart(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto()));
		log.info("Returning Response saveAndContinueSSCCPlcmtOptCircum of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: approveWithOutSave Method Description: Rest Controller
	 * method to approveWithoutSave to Placement Page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/approveWithOutSave", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes approveWithOutSave(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method approveWithOutSave of SSCCOptCircumSvcController");
		if (TypeConvUtil.isNullOrEmpty(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto().getIdActiveRef())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCReferral.mandatory", null, Locale.US));
		}
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto = ssccPlcmtOptCircumService
				.approveWithOutSave(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Set the timeline Dto before returning response.
		ssccPlcmtOptCircumDto.setSsccTimelineList(ssccPlcmtOptCircumService.fetchSSCCTimeLine(ssccPlcmtOptCircumDto));
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumDto);
		log.info("Returning Response approveWithOutSave of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: reject Method Description: Rest Controller method to handle
	 * reject on SSCCPlacement Opt Circum Page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/reject", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes reject(@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method reject of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto = ssccPlcmtOptCircumService
				.reject(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		ssccPlcmtOptCircumDto.setSsccTimelineList(ssccPlcmtOptCircumService.fetchSSCCTimeLine(ssccPlcmtOptCircumDto));
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumDto);
		log.info("Returning Response of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: saveToPlaceInfo Method Description: Rest Controller Method
	 * to handle SaveToPlaceInfo on SSCC Placment OptCircum page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/saveToPlaceInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes saveToPlaceInfo(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method saveToPlaceInfo of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// Call Save To Place Info Service.
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.saveToPlaceInfo(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Fetch the updated Records and return.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning saveToPlaceInfo of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: checkNarrativeExists Method Description: Rest Controller
	 * Method to Handle checkNarrativeExists Service Req.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/checkNarrativeExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes checkNarrativeExists(@RequestBody CommonHelperReq ssccPlcmtOptCircumReq) {
		log.info("Entering method checkNarrativeExists of SSCCOptCircumSvcController");
		CommonBooleanRes response = new CommonBooleanRes();
		response.setExists(ssccPlcmtOptCircumService.checkNarrativeExist(ssccPlcmtOptCircumReq.getIdHeader(),
				ssccPlcmtOptCircumReq.getNbrVersion()));
		log.info("Returning checkNarrativeExists of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: rescind Method Description: rest Controller Method to handle
	 * rescind for SSCCPlcmtOption Page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/rescind", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes rescind(@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method rescind of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// Call Rescind service
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.rescind(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Fetch the updated Records and return.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning rescind of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: setPerson Method Description: Rest Controller method to set
	 * the selected Person to SSCCPlcmtOptCircumstance.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/setPersonToSSCCOptCircum", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes setPerson(@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method setPerson of SSCCOptCircumSvcController");
		if (TypeConvUtil.isNullOrEmpty(ssccPlcmtOptCircumReq.getSelectedPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.setPerson(
				ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto(), ssccPlcmtOptCircumReq.getSelectedPerson()));
		log.info("Returning Response setPerson of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: saveAndTransmit Method Description: Rest controller method
	 * to for handling the Save and Transmit functionality on SSCC Plcmt Opt
	 * Circum page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/saveAndTransmit", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes saveAndTransmit(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method saveAndTransmit of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto = ssccPlcmtOptCircumService
				.saveAndTransmit(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Before returning set the Timelines.
		ssccPlcmtOptCircumDto.setSsccTimelineList(ssccPlcmtOptCircumService.fetchSSCCTimeLine(ssccPlcmtOptCircumDto));
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumDto);
		log.info("Returning Response saveAndTransmit of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: updateAndNotify Method Description: Rest Controller method
	 * to Handle Update and Notify Request on SSCC Plcmt OptCircum page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/updateAndNotify", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes updateAndNotify(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method updateAndNotify of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// Call Save update Notify Service
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.updateAndNotify(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Fetch the updated Records and return.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning updateAndNotify of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: setUpSectionToEdit Method Description: Rest Controller
	 * method to Handle the Edit Request.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/setUpSectionToEdit", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes setUpSectionToEdit(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method setUpSectionToEdit of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// Call setupSection to Edit service
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.setUpSectionToEdit(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Fetch the updated Records and return.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning setUpSectionToEdit of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: cancelUpdate Method Description: Rest controller method for
	 * Cancel Update service of SSCC Plcmt Opt Circum page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/cancelUpdate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes cancelUpdate(@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method setUpSectionToEdit of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// Call cancel update service. Delete the current version records.
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.cancelUpdate(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Fetch the Previous version
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning setUpSectionToEdit  of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: acknowledgeOnly Method Description: Rest controller method
	 * to update the status on Acknowledge by DFPS user.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/acknowledgeOnly", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes acknowledgeOnly(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method acknowledgeOnly of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// call Acknowledge only service method to update the header status.
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.acknowledgeOnly(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Read the updated values and return back.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning acknowledgeOnly of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: approveWithMod Method Description: Rest Controller method to
	 * Call ApproveWithMod Service. The service is called on click of
	 * Approve/ApproveWithMod button ON SSCCPlcmtOptCircum page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/approveWithMod", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes approveWithMod(
			@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method approveWithMod of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// call ApproveWithMod service method to update the header status.
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.approveWithMod(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Read the updated values and return back.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning approveWithMod of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: finalizeEval Method Description: Rest Controller method to
	 * Call the Finalize Evaluation Service from SSCCPlcmtOptCircum page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/finalizeEval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes finalizeEval(@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method finalizeEval of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// call finalize Evaluation service method to update the header status
		// and Timline records.
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.finalizeEval(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Read the updated values and return back.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning finalizeEval of SSCCOptCircumSvcController");
		return response;
	}

	/**
	 * Method Name: cancelEval Method Description: Rest Controller method to
	 * Call Cancel Evaluation Service for SSCCPlcmtOptCircum page.
	 * 
	 * @param ssccPlcmtOptCircumReq
	 * @return
	 */
	@RequestMapping(value = "/cancelEval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCPlcmtOptCircumRes cancelEval(@RequestBody SSCCPlcmtOptCircumReq ssccPlcmtOptCircumReq) {
		log.info("Entering method cancelEval of SSCCOptCircumSvcController");
		SSCCPlcmtOptCircumRes response = new SSCCPlcmtOptCircumRes();
		// call finalize Evaluation service method to update the header status
		// and Timline records.
		SSCCPlcmtOptCircumDto ssccPlcmtOptCirumDto = ssccPlcmtOptCircumService
				.cancelEval(ssccPlcmtOptCircumReq.getSsccPlcmtOptCircumDto());
		// Read the updated values and return back.
		response.setSsccPlcmtOptCircumDto(ssccPlcmtOptCircumService.readSSCCPlcmtOptCirum(ssccPlcmtOptCirumDto));
		log.info("Returning cancelEval of SSCCOptCircumSvcController");
		return response;
	}
}
