package us.tx.state.dfps.service.SDM.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.SDM.service.SDMRiskAssessmentService;
import us.tx.state.dfps.service.SDM.service.SdmRiskAssessmentFormService;
import us.tx.state.dfps.service.common.request.SDMRiskAssessmentReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.SDMRiskAssessmentRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyAssessmentDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Name:SDMRiskAssessmentBean
 * Class Description: SDMRiskAssessmentController class will have all operation
 * which are related to SDM Risk assessment Process.
 *
 */

@RestController
@RequestMapping("/SDMRiskAssessment")
public class SDMRiskAssessmentController {

	@Autowired
	SDMRiskAssessmentService sDMRiskAssessmentService;

	@Autowired
	SdmRiskAssessmentFormService sdmRiskAssessmentFormService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(SDMRiskAssessmentController.class);

	/**
	 * To save the SDMRiskAssessmentDB to the CPS_RA table
	 * 
	 * @param sDMRiskAssessmentReq
	 * @return SDMRiskAssessmentRes
	 */
	@RequestMapping(value = "/saveRiskAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes saveRiskAssessment(
			@RequestBody SDMRiskAssessmentReq sDMRiskAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentReq.getsDMRiskAssessmentdto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sDMRiskAssessment.SDMRiskAssessmentDto.mandatory", null, Locale.US));
		}

		SDMRiskAssessmentDto sDMRiskAssessmentdto = sDMRiskAssessmentReq.getsDMRiskAssessmentdto();
		return sDMRiskAssessmentService.saveRiskAssessment(sDMRiskAssessmentdto);

	}

	/**
	 * Complete Risk Assessment form.
	 *
	 * @param sDMRiskAssessmentReq
	 * @return SDMRiskAssessmentRes
	 */

	@RequestMapping(value = "/completeAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes getCompleteAssessment(
			@RequestBody SDMRiskAssessmentReq sDMRiskAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentReq.getsDMRiskAssessmentdto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("completeAssessment.SdmRiskAssessmentDto.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentReq.getEventValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("completeAssessment.EventDto.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + sDMRiskAssessmentReq.getTransactionId());

		sDMRiskAssessmentService.completeAssessment(sDMRiskAssessmentReq);
		SDMRiskAssessmentRes sdmRiskAssessmentRes = new SDMRiskAssessmentRes();
		sdmRiskAssessmentRes.setEventValueDto(sDMRiskAssessmentReq.getEventValueDto());

		return sdmRiskAssessmentRes;

	}

	/**
	 * To delete the SDMRiskAssessmentDB to the CPS_RA table
	 * 
	 * @param sDMRiskAssessmentReq
	 * @return SDMRiskAssessmentRes
	 */
	@RequestMapping(value = "/deletRiskAssessment", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes deleteRiskAssessment(
			@RequestBody SDMRiskAssessmentReq sDMRiskAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentReq.getsDMRiskAssessmentdto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sDMRiskAssessment.SDMRiskAssessmentDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentReq.getsDMRiskAssessmentdto().getId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sDMRiskAssessment.idCpsRa.mandatory", null, Locale.US));
		}

		SDMRiskAssessmentDto sDMRiskAssessmentdto = sDMRiskAssessmentReq.getsDMRiskAssessmentdto();
		log.info("TransactionId :" + sDMRiskAssessmentReq.getTransactionId());
		return sDMRiskAssessmentService.deleteRiskAssessment(sDMRiskAssessmentdto);

	}

	/**
	 * To fetch the SDMRiskAssessmentDB HouseHold Name
	 * 
	 * @param sDMRiskAssHouseholdReq
	 * @return SDMRiskAssessmentRes
	 */
	@RequestMapping(value = "/getHouseholdName", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes getHouseholdName(
			@RequestBody SDMRiskAssessmentReq sDMRiskAssHouseholdReq) {

		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto().getId())
				|| 0 == sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto().getId()) {
			throw new InvalidRequestException(
					messageSource.getMessage("sDMRiskAssessment.idCpsRa.mandatory", null, Locale.US));
		}
		SDMRiskAssessmentDto sDMRiskAssessmentdto = sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto();
		log.info("TransactionId :" + sDMRiskAssHouseholdReq.getTransactionId());
		return sDMRiskAssessmentService.getHouseholdName(sDMRiskAssessmentdto);

	}

	/**
	 * To save the SDMRiskAssessmentDB HouseHold Dtl
	 * 
	 * @param sDMRiskAssHouseholdReq
	 * @return SDMRiskAssessmentRes
	 */
	@RequestMapping(value = "/saveHouseholdName", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes saveHouseholdDtl(
			@RequestBody SDMRiskAssessmentReq sDMRiskAssHouseholdReq) {

		SDMRiskAssessmentRes sDMHouseholdSaveRes = new SDMRiskAssessmentRes();
		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto().getId())
				&& TypeConvUtil.isNullOrEmpty(sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto().getIdHouseHoldPerson())
				&& (TypeConvUtil.isNullOrEmpty(sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto().getLoggedInUser())
						|| 0 == sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto().getLoggedInUser())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		sDMHouseholdSaveRes = sDMRiskAssessmentService
				.saveHouseholdDtl(sDMRiskAssHouseholdReq.getsDMRiskAssessmentdto());
		log.info("TransactionId :" + sDMRiskAssHouseholdReq.getTransactionId());
		return sDMHouseholdSaveRes;
	}

	@RequestMapping(value = "/getExistingRAForHousehold", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes getExistingRAForHousehold(
			@RequestBody SDMRiskAssessmentReq retrieveHouseHoldForCaseReq) {
		SDMRiskAssessmentRes response = new SDMRiskAssessmentRes();
		if (TypeConvUtil.isNullOrEmpty(retrieveHouseHoldForCaseReq.getsDMRiskAssessmentdto().getCaseId())
				|| TypeConvUtil
						.isNullOrEmpty(retrieveHouseHoldForCaseReq.getsDMRiskAssessmentdto().getIdHouseHoldPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		response = sDMRiskAssessmentService
				.getExistingRAForHousehold(retrieveHouseHoldForCaseReq.getsDMRiskAssessmentdto());

		log.info("TransactionId :" + retrieveHouseHoldForCaseReq.getTransactionId());
		return response;
	}

	@RequestMapping(value = "/getsdmriskassmtinfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getSdmRiskAssmtInfo(@RequestBody SDMRiskAssessmentReq sDMRiskAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(sdmRiskAssessmentFormService.getSdmRiskAssmtFormInfo(sDMRiskAssessmentReq)));
		log.info("TransactionId :" + sDMRiskAssessmentReq.getTransactionId());

		return commonFormRes;
	}

	@RequestMapping(value = "/getRiskAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes getRiskAssessment(
			@RequestBody SDMRiskAssessmentReq sdmRiskAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(sdmRiskAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return sDMRiskAssessmentService.getRiskAssesment(sdmRiskAssessmentReq.getIdStage());
	}

	@RequestMapping(value = "/queryRiskAssessmentExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes queryRiskAssessmentExists(
			@RequestBody SDMRiskAssessmentReq sdmRiskAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(sdmRiskAssessmentReq.getsDMRiskAssessmentdto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(sdmRiskAssessmentReq.getsDMRiskAssessmentdto().getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return sDMRiskAssessmentService.queryRiskAssessmentExists(sdmRiskAssessmentReq.getsDMRiskAssessmentdto());
	}

	/**
	 * Method Name:getSDMRiskAssessment Method Description:To get a
	 * riskAssessment details with eventId and stageId
	 * 
	 * @param sdmRiskAssessmentReq
	 * @return SDMRiskAssessmentRes
	 */
	@RequestMapping(value = "/getSDMRiskAssessment", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes getSDMRiskAssessment(
			@RequestBody SDMRiskAssessmentReq sdmRiskAssessmentReq) {
		log.info("TransactionId :" + sdmRiskAssessmentReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(sdmRiskAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(sdmRiskAssessmentReq.getEventValueDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		SDMRiskAssessmentRes sdmRiskAssessmentRes = new SDMRiskAssessmentRes();
		sdmRiskAssessmentRes.setsDMRiskAssessmentdto(sDMRiskAssessmentService.getSDMRiskAssessment(
				sdmRiskAssessmentReq.getIdStage(), sdmRiskAssessmentReq.getEventValueDto().getIdEvent()));

		return sdmRiskAssessmentRes;
	}

	/**
	 * Method Name:queryPageData Method Description:To pull the questions,
	 * answers, followups and secondary follow ups when the assessment is new
	 * 
	 * @param sdmRiskAssessmentReq
	 * @return SDMRiskAssessmentRes
	 */
	@RequestMapping(value = "/queryPageData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes queryPageData(@RequestBody SDMRiskAssessmentReq sdmRiskAssessmentReq) {
		log.info("TransactionId :" + sdmRiskAssessmentReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(sdmRiskAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		SDMRiskAssessmentRes sdmRiskAssessmentRes = new SDMRiskAssessmentRes();
		sdmRiskAssessmentRes
				.setsDMRiskAssessmentdto(sDMRiskAssessmentService.queryPageData(sdmRiskAssessmentReq.getIdStage()));
		return sdmRiskAssessmentRes;
	}

	/**
	 * Method Name: getPrimaryCreGivrHistoryCount Method Description:To get a
	 * Primary Caregiver History with care giver Id and stageId
	 * 
	 * @param sDMRiskAssmtReq
	 * @return SDMRiskAssmtRes
	 */
	@RequestMapping(value = "/getPrimaryCreGivrHistoryCount", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes getPrimaryCreGivrHistoryCount(
			@RequestBody SDMRiskAssessmentReq sDMRiskAssmtReq) {

		log.info("TransactionId :" + sDMRiskAssmtReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssmtReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssmtReq.getsDMRiskAssessmentdto().getIdPrimaryCaregiver())) {
			throw new InvalidRequestException(
					messageSource.getMessage("SDMRiskAssessment.primaryCareGiver.mandatory", null, Locale.US));
		}

		SDMRiskAssessmentRes sDMRiskAssmtRes = new SDMRiskAssessmentRes();
		sDMRiskAssmtRes.setUpdateResult(sDMRiskAssessmentService.getPrimaryCreGivrHistoryCount(
				sDMRiskAssmtReq.getsDMRiskAssessmentdto().getIdPrimaryCaregiver(), sDMRiskAssmtReq.getIdStage()));
		return sDMRiskAssmtRes;

	}

	/**
	 * Method Name: getSecondaryCreGivrHistoryCount Method Description:To get a
	 * Secondary Caregiver History with care giver Id and stageId
	 * 
	 * @param sDMRiskAssmtReq
	 * @return SDMRiskAssmtRes
	 */
	@RequestMapping(value = "/getSecondaryCreGivrHistoryCount", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskAssessmentRes getSecondaryCreGivrHistoryCount(
			@RequestBody SDMRiskAssessmentReq sDMRiskAssmtReq) {

		log.info("TransactionId :" + sDMRiskAssmtReq.getTransactionId());

		if (TypeConvUtil.isNullOrEmpty(sDMRiskAssmtReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(sDMRiskAssmtReq.getsDMRiskAssessmentdto().getIdSecondaryCaregiver())) {
			throw new InvalidRequestException(
					messageSource.getMessage("SDMRiskAssessment.secondaryCareGiverId.mandatory", null, Locale.US));
		}

		SDMRiskAssessmentRes sDMRiskAssmtRes = new SDMRiskAssessmentRes();
		sDMRiskAssmtRes.setUpdateResult(sDMRiskAssessmentService.getSecondaryCreGivrHistoryCount(
				sDMRiskAssmtReq.getsDMRiskAssessmentdto().getIdSecondaryCaregiver(), sDMRiskAssmtReq.getIdStage()));
		return sDMRiskAssmtRes;

	}

	/**
	 * Method Name:retrieveSafetyAssmtData Method Description:This method is
	 * called from display method in SafetyAssmtConversation if the page has
	 * been previously saved. It retrives back all the responses
	 * 
	 * @param SafetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	public  SafetyAssessmentRes retrieveSafetyAssmtData(
			@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
		safetyAssessmentRes = sDMRiskAssessmentService.retrieveSafetyAssmtData(safetyAssessmentReq);

		return safetyAssessmentRes;
	}

	/**
	 * Method Name:getSubStageOpen Method Description:Returns if a Sub Stage is
	 * open, with a given StageId
	 * 
	 * @param safetyAssessmentDto
	 * @return boolean
	 */
	public  SafetyAssessmentRes getSubStageOpen(@RequestBody SafetyAssessmentDto safetyAssessmentDto) {
		SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();

		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentDto.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		boolean subStageOpen = (sDMRiskAssessmentService.getsubStageOpen(safetyAssessmentDto.getIdCase()));
		safetyAssessmentRes.setSubStageOpen(subStageOpen);
		return safetyAssessmentRes;
	}

	/**
	 * Method Name:getCurrentEventStatus Method Description:Returns back a
	 * string containing current Event status
	 * 
	 * @param safetyAssessmentDto
	 * @return SafetyAssessmentRes
	 */
	public  SafetyAssessmentRes getCurrentEventStatus(
			@RequestBody SafetyAssessmentDto safetyAssessmentDto) {

		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentDto.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentDto.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessmentRes = new SafetyAssessmentRes();
		String eventStatus = sDMRiskAssessmentService.getCurrentEventStatus(safetyAssessmentDto.getIdStage(),
				safetyAssessmentDto.getIdCase());
		safetyAssessmentRes.setEventStatus(eventStatus);
		return safetyAssessmentRes;
	}

}
