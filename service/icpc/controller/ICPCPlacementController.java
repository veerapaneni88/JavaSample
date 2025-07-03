
package us.tx.state.dfps.service.icpc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import us.tx.state.dfps.common.domain.IcpcEventLink;
import us.tx.state.dfps.common.domain.IcpcRequest;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ICPCEmailDocumentReq;
import us.tx.state.dfps.service.common.request.ICPCEmailLogReq;
import us.tx.state.dfps.service.common.request.ICPCPlacementReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.FTRelationshipRes;
import us.tx.state.dfps.service.common.response.ICPCDocumentRes;
import us.tx.state.dfps.service.common.response.ICPCPlacementRes;
import us.tx.state.dfps.service.common.response.ListTransmissionRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.icpc.dto.*;
import us.tx.state.dfps.service.icpc.service.ICPCPlacementService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Main
 * Services Controller for ICPC Page Services Aug 3, 2018- 4:21:26 PM © 2017
 * Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/icpcPlacement")
public class ICPCPlacementController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ICPCPlacementService icpcPlacementService;

	/**
	 * 
	 * Method Name: getSummaryInfo Method Description: Gets the ICPC Summary
	 * Info details
	 * 
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/getSummaryInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes getSummaryInfo(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		ICPCPlacementRequestDto icpcPlacementRequestDto = new ICPCPlacementRequestDto();
		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();

		if (TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("icpc.placement.input.idcase.mandatory", null, Locale.US));
		}
		icpcPlacementRequestDto = icpcPlacementService.getSummaryInfo(icpcPlacementReq.getIdCase());
		icpcPlacementRes.setIcpcPlacementRequestDto(icpcPlacementRequestDto);

		return icpcPlacementRes;
	}

	/**
	 * 
	 * Method Name: saveICPCSummary Method Description:Save the ICPC Summary
	 * Details
	 * 
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/saveICPCSummary", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes saveICPCSummary(@RequestBody ICPCPlacementReq icpcPlacementReq) {
		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		if (TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getIcpcPlacementRequestDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input1.mandatory", null, Locale.US));
		}
		icpcPlacementService.saveICPCSummary(icpcPlacementReq.getIcpcPlacementRequestDto());
		return icpcPlacementRes;
	}

	/**
	 * Method Name: getTransmittalInfo Method Description: get transmittal
	 * Details
	 * 
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/getTransmittalInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes getTransmittalInfo(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		ICPCTransmittalDto icpcTransmittalDto = new ICPCTransmittalDto();
		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		icpcTransmittalDto = icpcPlacementService.getTransmittalInfo(icpcPlacementReq.getIdTransmittal(),
				icpcPlacementReq.getIdICPCRequest(), icpcPlacementReq.getIdStage());

		icpcPlacementRes.setTransmittalInfo(icpcTransmittalDto);
		return icpcPlacementRes;
	}

	/**
	 * Method Name: getPlacementStatusInfo Method Description: get ICPC status
	 * report detail information for display
	 * 
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/getPlacementStatusInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes getPlacementStatusInfo(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		ICPCPlacementStatusDto iCPCPlacementStatusDto = new ICPCPlacementStatusDto();
		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		iCPCPlacementStatusDto = icpcPlacementService.getPlacementStatusInfo(icpcPlacementReq.getIdEvent());
		IcpcEventLink icpcEventLink = icpcPlacementService.getIcpcEventLinkInfo(icpcPlacementReq.getIdEvent());
		Map<Long, String> icpcRequest = icpcPlacementService.getIcpcRequestInfo(iCPCPlacementStatusDto.getIdICPCRequest());
		iCPCPlacementStatusDto.setIcpcRequest(icpcRequest);
		iCPCPlacementStatusDto.setIdCreatedPerson(icpcEventLink.getIdCreatedPerson());
		iCPCPlacementStatusDto.setNeiceCaseId(icpcEventLink.getIdNeiceCase());
		icpcPlacementRes.setIcpcPlacementStatusDto(iCPCPlacementStatusDto);
		return icpcPlacementRes;
	}

	/**
	 * Method Name: saveTransmittal Method Description: This method saves the
	 * transmittal detail page details.
	 * 
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/saveTransmittal", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes saveTransmittal(@RequestBody ICPCPlacementReq icpcPlacementReq) {
		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		Long idIcpcTransmittal;
		idIcpcTransmittal = icpcPlacementService.saveTransmittal(icpcPlacementReq.getTransmittalDto());
		icpcPlacementRes.setIdICPCTransmittal(idIcpcTransmittal);
		return icpcPlacementRes;
	}

	/**
	 * 
	 * Method Name: getICPCPlacementRequestDetail Method Description: get all
	 * ICPC placement request detail
	 * 
	 * @param ICPCPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/getICPCPlacementRequestDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes getICPCPlacementRequestDetail(
			@RequestBody ICPCPlacementReq icpcPlacementReq) {

		if (ObjectUtils.isEmpty(icpcPlacementReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(icpcPlacementReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(icpcPlacementReq.getEligibility())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.eligibility.mandatory", null, Locale.US));
		}

		ICPCPlacementRequestDto icpcPlacementStatusDto = icpcPlacementService.getICPCPlacementRequestDetail(
				icpcPlacementReq.getIdEvent(), icpcPlacementReq.getIdStage(), icpcPlacementReq.getEligibility());

		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		icpcPlacementRes.setIcpcPlacementRequestDto(icpcPlacementStatusDto);

		return icpcPlacementRes;
	}

	/**
	 * Method Name: deleteTransmittal Method Description: Delete the transmittal
	 * details to the database from Placement Request Details page
	 * 
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/deleteTransmittal", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  void deleteTransmittal(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		if (TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getIdICPCTransmittal())) {
			throw new InvalidRequestException(
					messageSource.getMessage("icpc.placement.input.idicpctransmittal.mandatory", null, Locale.US));
		}
		icpcPlacementService.deleteICPCTransmittal(icpcPlacementReq.getIdICPCTransmittal());

	}

	/**
	 * 
	 * Method Name: savePlacementRequest Method Description: Saves all ICPC
	 * Placement Request information
	 * 
	 * @param ICPCPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/savePlacementRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes savePlacementRequest(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		if (ObjectUtils.isEmpty(icpcPlacementReq.getIcpcPlacementRequestDBDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input1.mandatory", null, Locale.US));
		}
		icpcPlacementRes.setIcpcPlacementRequestDto(
				icpcPlacementService.savePlacementRequest(icpcPlacementReq.getIcpcPlacementRequestDBDto()));
		return icpcPlacementRes;
	}

	/**
	 * 
	 * Method Name: updatePlacementRequest Method Description:updates all ICPC
	 * Placement Request information
	 * 
	 * @param ICPCPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/updatePlacementRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes updatePlacementRequest(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		if (ObjectUtils.isEmpty(icpcPlacementReq.getIcpcPlacementRequestDBDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input1.mandatory", null, Locale.US));
		}
		List<ICPCAgencyDto> legacyEventsList = icpcPlacementService
				.updatePlacementRequest(icpcPlacementReq.getIcpcPlacementRequestDBDto());

		icpcPlacementRes.setLegacyEventsList(legacyEventsList);

		return icpcPlacementRes;
	}

	// ICPC Placement Request Selector Controller

	/**
	 * Method Name: getPlacementSelector Method Description: This method
	 * generates the list for PEND and APRV Status which has a Placement 100-A
	 * Record
	 *
	 * 
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */

	@RequestMapping(value = "/getPlacementSelector", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes getPlacementSelector(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();

		if (TypeConvUtil.isNullOrEmpty(icpcPlacementReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("icpc.placement.input.idStage.mandatory", null, Locale.US));
		}

		icpcPlacementRes = icpcPlacementService.getListPlacementRequest(icpcPlacementReq);
		return icpcPlacementRes;
	}

	/**
	 * 
	 * Method Name: getIcpcDocument Method Description: get the document details
	 * for the given idICPCDocuments for Interstate Compact Placement Request
	 * Detail-Email Detail (ADS)page.
	 *
	 * @param icpcEmailDocumentReq
	 * @return ICPCDocumentRes
	 */
	@RequestMapping(value = "/getIcpcDocumentDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ICPCDocumentRes getIcpcDocument(@RequestBody ICPCEmailDocumentReq icpcEmailDocumentReq){
		ICPCDocumentRes icpcDocumentRes = new ICPCDocumentRes();
		List<ICPCDocumentDto> icpcDocumentDtos = new ArrayList<ICPCDocumentDto>();
		icpcDocumentDtos = icpcPlacementService.getIcpcDocument(icpcEmailDocumentReq.getIdICPCDocuments());
		icpcDocumentRes.setIcpcDocumentDtos(icpcDocumentDtos);
		return icpcDocumentRes;
	}

	/**
	 * 
	 * Method Name: getIcpcDocument Method Description: this method is to save
	 * ICPC placement status report detail page
	 *
	 * @param icpcEmailDocumentReq
	 * @return ICPCDocumentRes
	 */
	@RequestMapping(value = "/savePlacementStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ICPCPlacementRes savePlacementStatus(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		return icpcPlacementService.savePlacementStatus(icpcPlacementReq);
	}

	/**
	 * 
	 * Method Name: getIcpcDocument Method Description: this method is to update
	 * ICPC placement status report detail page
	 *
	 * @param icpcEmailDocumentReq
	 * @return ICPCDocumentRes
	 */
	@RequestMapping(value = "/updatePlacementStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ICPCPlacementRes updatePlacementStatus(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		return icpcPlacementService.updatePlacementStatus(icpcPlacementReq);
	}

	/**
	 * 
	 * Method Name: updateIcpcEmailDtlLog Method Description: This method is to
	 * update ICPC placement Email detail Log tables(ICPC_EMAIL_LOG,
	 * ICPC_EMAIL_DOC_LOG) when the email has been sent successfully.
	 *
	 * @param icpcEmailLogReq
	 * @return CommonStringRes
	 */
	@RequestMapping(value = "/updateIcpcEmailDtlLog", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonStringRes updateIcpcEmailDtlLog(@RequestBody ICPCEmailLogReq icpcEmailLogReq) {

		return icpcPlacementService.updateIcpcEmailDtlLog(icpcEmailLogReq);
	}

	/**
	 * 
	 * Method Name: getPersonRelation Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */

	@RequestMapping(value = "/getPersonRelation", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public FTRelationshipRes getPersonRelation(@RequestBody CommonHelperReq commonHelperReq) {

		return icpcPlacementService.getPersonRelation(commonHelperReq);
	}

	/**
	 * Method Name: getRecentAprvEvent Method Description: This method is used
	 * to retrieve most recent approved CPS idEvent.
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/getRecentAprvEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getRecentAprvEvent(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIdEvent(icpcPlacementService.getRecentAprvEvent(commonHelperReq.getIdStage()));
		return commonHelperRes;
	}

	/**
	 * 
	 * Method Name: deletePlacementRequest Method Description: Deletes all ICPC
	 * Placement Request information
	 * 
	 * @param ICPCPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/deletePlacementRequest", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ICPCPlacementRes deletePlacementRequest(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		ICPCPlacementRes icpcPlacementRes = new ICPCPlacementRes();
		if (ObjectUtils.isEmpty(icpcPlacementReq.getIcpcPlacementRequestDBDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input1.mandatory", null, Locale.US));
		}
		Long idEvent = icpcPlacementService.deletePlacementRequest(icpcPlacementReq.getIcpcPlacementRequestDBDto());
		icpcPlacementRes.setIdEvent(idEvent);
		return icpcPlacementRes;
	}

	@GetMapping(value = "/getTransmissionLst/{idStage}")
	public ListTransmissionRes getTransmissionLst(@PathVariable(value = "idStage") Long idStage) {

		if (ObjectUtils.isEmpty(idStage)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return icpcPlacementService.getTransmissionLst(idStage);
	}

	@GetMapping(value = "/getTransmissionChildLst/{idTransmission}")
	public ListTransmissionRes getTransmissionChildLst(
			@PathVariable(value = "idTransmission") Long idTransmission) {

		if (ObjectUtils.isEmpty(idTransmission)) {
			throw new InvalidRequestException(messageSource.getMessage("transmissionlst.noTransmissionId", null, Locale.US));
		}

		return icpcPlacementService.getTransmissionChildLst(idTransmission);
	}

	@GetMapping(value = "/getTransmissionAttachments/{idTransmission}/{idPersonNeice}")
	public ListTransmissionRes getTransmissionAttachments(
			@PathVariable(value = "idTransmission") Long idTransmission, @PathVariable(value = "idPersonNeice") Long idPersonNeice) {

		if (ObjectUtils.isEmpty(idTransmission)) {
			throw new InvalidRequestException(messageSource.getMessage("transmissionlst.noTransmissionId", null, Locale.US));
		}

		if (ObjectUtils.isEmpty(idPersonNeice)) {
			throw new InvalidRequestException(messageSource.getMessage("transmissionlst.idPersonNeice", null, Locale.US));
		}

		return icpcPlacementService.getTransmissionAttachments(idTransmission,idPersonNeice);
	}

	@GetMapping(value = "/getTransmissionAttachment/{idAttachment}")
	public ListTransmissionRes getTransmissionAttachment(
			@PathVariable(value = "idAttachment") Long idAttachment) {

		if (ObjectUtils.isEmpty(idAttachment)) {
			throw new InvalidRequestException(messageSource.getMessage("transmissionlst.noAttachmentId", null, Locale.US));
		}

		return icpcPlacementService.getTransmissionAttachment(idAttachment);
	}

	@RequestMapping(value = "/saveNeiceTransmission", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void saveNeiceTransmission(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		icpcPlacementService.saveNeiceTransmission(icpcPlacementReq);
	}

	@RequestMapping(value = "/getSendingAgencyInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.GET)
	public ListTransmissionRes getSendingAgencyInfo(@RequestParam(name = "stateCode", required = false) String stateCode) {

		return icpcPlacementService.getSendingAgencyInfo(stateCode);
	}

	/**
	 * Method Name: validateCreateTransmittal
	 * Method Description: Retrieve the Placement Status Details and Document Details for Create
	 * Transmittal Validation
	 *
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/validate-transmittal", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public ICPCPlacementRes validateCreateTransmittal(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		return icpcPlacementService.validateCreateTransmittal(icpcPlacementReq.getTransmittalDto());
	}

	/**
	 * Method Name: saveTransmittalDocs
	 * Method Description: Save the Transmittal selected documents for Transmission
	 *
	 * @param icpcPlacementReq
	 * @return ICPCPlacementRes
	 */
	@RequestMapping(value = "/save-docs", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public void saveTransmittalDocs(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		if (ObjectUtils.isEmpty(icpcPlacementReq.getTransmittalDto()) || ObjectUtils.isEmpty(
				icpcPlacementReq.getTransmittalDto()
						.getIdICPCTransmittal())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input1.mandatory", null, Locale.US));
		}

		icpcPlacementService.saveTransmittalDocs(icpcPlacementReq.getTransmittalDto());
	}

	/**
	 * Method Name: saveTransmittalStatus
	 * Method Description: Save the Transmittal Status received from MuleSoft
	 *
	 * @param icpcPlacementReq
	 */
	@RequestMapping(value = "/save-status", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public void saveCreateTransmittal(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		if (ObjectUtils.isEmpty(icpcPlacementReq.getTransmittalDto()) || ObjectUtils.isEmpty(
				icpcPlacementReq.getTransmittalDto()
						.getIdICPCTransmittal())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input1.mandatory", null, Locale.US));
		}

		icpcPlacementService.saveTransmittalStatus(icpcPlacementReq.getTransmittalDto());
	}

	/**
	 * Method Name: saveTransmittalStatus
	 * Method Description: Save the Transmittal Status received from MuleSoft
	 *
	 * @param icpcPlacementReq
	 */
	@RequestMapping(value = "/save-other", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public void saveTransmittalMode(@RequestBody ICPCPlacementReq icpcPlacementReq) {

		if (ObjectUtils.isEmpty(icpcPlacementReq.getTransmittalDto()) || ObjectUtils.isEmpty(
				icpcPlacementReq.getTransmittalDto()
						.getIdICPCTransmittal())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input1.mandatory", null, Locale.US));
		}

		icpcPlacementService.saveTransmittalOtherMode(icpcPlacementReq.getTransmittalDto());
	}

}
