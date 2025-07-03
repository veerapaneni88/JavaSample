package us.tx.state.dfps.service.prt.controller;

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
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.PRTActPlanFollowUpRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.prt.service.PRTActPlanFollowUpService;
import us.tx.state.dfps.service.prt.service.PRTService;
import us.tx.state.dfps.service.subcare.dto.PRTActPlanFollowUpDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to handle the service request for retrieve/save of PRT Follow-Up
 * details. Mar 28, 2018- 3:21:39 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/prtActPlanFollowUp")
public class PRTActPlanFollowUpController {

	private static final String COMMON_ID_CASE_MANDATORY = "common.idCase.mandatory";
	private static final String COMMON_TABLE_NAME_MANDATORY = "common.tableName.mandatory";
	private static final String COMMON_ID_EVENT_MANDATORY = "common.idEvent.mandatory";

	@Autowired
	MessageSource messageSource;

	@Autowired
	PRTActPlanFollowUpService prtActPlanFollowUpService;

	@Autowired
	PRTService prtService;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PRTActPlanFollowUpServiceLog");

	/**
	 * Method Name: fetchActPlanFollowUp Method Description:This method is used
	 * to fetch the PRT Follow-Up details. This method calls the service
	 * implementation which checks if it is a existing follow-up or a new one.
	 * 
	 * @param prtActplanFollowUpReq
	 * @return prtActPlanFollowUpRes
	 */
	@RequestMapping(value = "/fetchactplanfollowup", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActPlanFollowUpRes fetchActPlanFollowUp(
			@RequestBody PRTActplanFollowUpReq prtActplanFollowUpReq) {

		if (TypeConvUtil.isNullOrEmpty(prtActplanFollowUpReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(prtActplanFollowUpReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}

		PRTActPlanFollowUpRes prtActPlanFollowUpRes = new PRTActPlanFollowUpRes();
		// Calling the service to get the follow-up details.
		prtActPlanFollowUpRes.setPrtActPlanFollowUpValueDto(
				prtActPlanFollowUpService.fetchActPlanFollowUp(prtActplanFollowUpReq.getIdActplnFollowupEvent(),
						prtActplanFollowUpReq.getIdStage(), prtActplanFollowUpReq.getIdCase()));
		/*
		 * Calling the service to get the list of options to be excluded from
		 * the Follow-Up type drop-down.
		 */
		prtActPlanFollowUpRes.setFollowUpTypeExcludeOptions(prtActPlanFollowUpService
				.getFollowUpTypeExcludeOptions(prtActPlanFollowUpRes.getPrtActPlanFollowUpValueDto()));
		// returning the response from service call.
		return prtActPlanFollowUpRes;

	}

	/**
	 * This method retrieves all PRT Action Plan FollowUp From (PRTCFU).
	 * 
	 * @param idActplnFollowupEvent
	 * @param idStage
	 * @param idCase
	 * 
	 * @return PRTActPlanFollowUpRes
	 */

	@RequestMapping(value = "/displayActPlanFollowUpFrom", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes displayActPlanFollowUpFrom(
			@RequestBody PRTActplanFollowUpReq prtActplanFollowUpReq) {
		log.info("idStage :" + prtActplanFollowUpReq.getIdStage());

		if (TypeConvUtil.isNullOrEmpty(prtActplanFollowUpReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(prtActplanFollowUpReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(
				prtActPlanFollowUpService.displayActPlanFollowUpFrom(prtActplanFollowUpReq.getIdActplnFollowupEvent(),
						prtActplanFollowUpReq.getIdStage(), prtActplanFollowUpReq.getIdCase())));
		log.info("TransactionId :" + prtActplanFollowUpReq.getTransactionId());

		return commonFormRes;

	}

	/**
	 * Method Name: validate Method Description:This method is used to check if
	 * a new PRT Follow-Up can be added. This method calls the service
	 * implementation which checks the business rules if a new PRT Follow-Up can
	 * be added.The service implementation will return error list if any.
	 * 
	 * @param prtActplanFollowUpReq
	 * @return prtActPlanFollowUpRes
	 */
	@RequestMapping(value = "/validate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActPlanFollowUpRes validate(@RequestBody PRTActplanFollowUpReq prtActplanFollowUpReq) {
		PRTActPlanFollowUpRes prtActPlanFollowUpRes = new PRTActPlanFollowUpRes();
		// Calling the service implementation to validate a new Follow-Up.
		prtActPlanFollowUpRes.setErrorList(prtActPlanFollowUpService.validateAddNewFollowUp(prtActplanFollowUpReq));
		// returning the response from the REST service call
		return prtActPlanFollowUpRes;
	}

	/**
	 * Method Name: getPermStatus Method Description:This method is used to get
	 * the Perm Status Lookup definitions list.
	 * 
	 * @param prtActplanFollowUpReq
	 * @return prtActPlanFollowUpRes
	 */
	@RequestMapping(value = "/getPermStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActPlanFollowUpRes getPermStatus(@RequestBody PRTActplanFollowUpReq prtActplanFollowUpReq) {
		PRTActPlanFollowUpRes prtActPlanFollowUpRes = new PRTActPlanFollowUpRes();
		/*
		 * Calling the service implementation to get the Perm Status Lookup
		 * definitions list.
		 */
		prtActPlanFollowUpRes.setPermStatusLookUpList(prtService.getPermStatus());
		// returning the response from the REST service call
		return prtActPlanFollowUpRes;
	}

	/**
	 * Method Name: deletePRTStrategy Method Description:This method is used to
	 * delete a strategy from the PRT Follow-Up. This method calls the service
	 * implementation which will delete the strategy and the corresponding
	 * tables.
	 * 
	 * @param prtActplanFollowUpReq
	 * @return prtActPlanFollowUpRes
	 */
	@RequestMapping(value = "/deletePRTStrategy", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActPlanFollowUpRes deletePRTStrategy(
			@RequestBody PRTActplanFollowUpReq prtActplanFollowUpReq) {
		PRTActPlanFollowUpRes prtActPlanFollowUpRes = new PRTActPlanFollowUpRes();
		// Calling the service implementation to delete the strategy.
		prtService.deletePRTStrategy(prtActplanFollowUpReq);
		// returning the response from the REST service call
		return prtActPlanFollowUpRes;
	}

	/**
	 * Method Name: savePRTFollowUp Method Description:This method is used to
	 * save the PRT Follow-Up details.
	 * 
	 * @param prtActplanFollowUpReq
	 * @return prtActPlanFollowUpRes
	 */
	@RequestMapping(value = "/savePRTFollowUp", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActPlanFollowUpRes savePRTFollowUp(
			@RequestBody PRTActplanFollowUpReq prtActplanFollowUpReq) {
		PRTActPlanFollowUpRes prtActPlanFollowUpRes = new PRTActPlanFollowUpRes();
		/*
		 * Calling the service implementation to save the PRT Follow-Up and get
		 * the idEvent after the save/update event.
		 */
		prtActPlanFollowUpRes.setIdEvent(
				prtActPlanFollowUpService.savePRTFollowUp(prtActplanFollowUpReq.getPrtActPlanFollowUpDto()));
		// returning the response from the REST service call
		return prtActPlanFollowUpRes;
	}

	/**
	 * Method Name: getFollowUpDetails Method Description:This method is used to
	 * get the PRT Follow-Up details pertaining to a particular Follow-Up type.
	 * 
	 * @param prtActplanFollowUpReq
	 * @return prtActPlanFollowUpRes
	 */
	@RequestMapping(value = "/getFollowUpDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActPlanFollowUpRes getFollowUpDetails(
			@RequestBody PRTActplanFollowUpReq prtActplanFollowUpReq) {
		PRTActPlanFollowUpRes prtActPlanFollowUpRes = new PRTActPlanFollowUpRes();
		/*
		 * Calling the service implementation to the PRT Follow-Up details for a
		 * particular follow-Up.
		 */
		PRTActPlanFollowUpDto prtActPlanFollowUpDto = prtActPlanFollowUpService
				.getFollowUpDetails(prtActplanFollowUpReq.getPrtActPlanFollowUpDto());
		prtActPlanFollowUpRes.setPrtActPlanFollowUpValueDto(prtActPlanFollowUpDto);
		// returning the response from the REST service call
		return prtActPlanFollowUpRes;
	}

	/**
	 * 
	 * Method Name: getTimeStamp Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/getTimeStampFU", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getTimeStamp(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_CASE_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getTableName())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_TABLE_NAME_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_EVENT_MANDATORY, null, Locale.US));
		}
		commonHelperRes.setDtLastUpdate(prtActPlanFollowUpService.getTimeStamp(commonHelperReq));
		return commonHelperRes;
	}
}