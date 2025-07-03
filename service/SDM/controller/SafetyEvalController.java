package us.tx.state.dfps.service.SDM.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.SDM.service.SafetyEvalService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service for
 * the Child Safety Eval Form on Safety and Risk Assessment Page. May 9, 2018-
 * 10:28:21 AM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/childsafetyeval")
public class SafetyEvalController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SafetyEvalService safetyEvalService;

	/**
	 * 
	 * Method Description:This method is used to retrieve the information Child
	 * Safety Evaluation letter by passing idStage and idEvent as input request
	 * 
	 * @param CommonHelperReq
	 *            Service name :CINV13S
	 * @return CommonFormRes
	 */

	@RequestMapping(value = "/getsafetyevaldetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getSafetyEvalDetails(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(safetyEvalService.getSafetyEvalDetails(commonHelperReq)));

		return commonFormRes;

	}

	/**
	 * Method name :getCurrentEventId Method Description:This method fetches the
	 * current Event Id
	 * 
	 * @param SafetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@RequestMapping(value = "/getCurrentEventId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes getCurrentEventId(@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessmentRes = safetyEvalService.getCurrentEventId(safetyAssessmentReq.getIdStage(),
				safetyAssessmentReq.getIdCase());
		return safetyAssessmentRes;
	}

	/**
	 * Method name :getQueryPgData Method Description:Retrieve the data needed
	 * to build the Safety Assessment page.
	 * 
	 * @param SafetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@RequestMapping(value = "/getQueryPgData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes getQueryPgData(@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		SafetyAssessmentRes safetyAssessmentRes = safetyEvalService.getQueryPgData(safetyAssessmentReq);
		return safetyAssessmentRes;
	}

	/**
	 * Method name :retrieveSafetyAssmtData Method Description:This method
	 * fetches the safety assessment data
	 * 
	 * @param SafetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@RequestMapping(value = "/retrieveSafetyAssmtData", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes retrieveSafetyAssmtData(
			@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getSafetyAssessmentDto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getSafetyAssessmentDto().getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getSafetyAssessmentDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessmentRes = safetyEvalService.retrieveSafetyAssmtData(
				safetyAssessmentReq.getSafetyAssessmentDto().getIdStage(),
				safetyAssessmentReq.getSafetyAssessmentDto().getIdEvent(),
				safetyAssessmentReq.getSafetyAssessmentDto().getIdCase());
		return safetyAssessmentRes;
	}

	/**
	 * Method name :getCurrentEventStatus Method Description:This method fetches
	 * the current Event status
	 * 
	 * @param SafetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@RequestMapping(value = "/getCurrentEventStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes getCurrentEventStatus(
			@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessmentRes = safetyEvalService
				.getCurrentEventStatus(safetyAssessmentReq.getIdStage(), safetyAssessmentReq.getIdCase());
		return safetyAssessmentRes;
	}

	/**
	 * Method name :getSubStageOpen Method Description:Returns if SubStage is
	 * Open or Close
	 * 
	 * @param SafetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@RequestMapping(value = "/getSubStageOpen", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes getSubStageOpen(@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getSafetyAssessmentDto().getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessmentRes = safetyEvalService
				.getSubStageOpen(safetyAssessmentReq.getSafetyAssessmentDto().getIdCase());
		return safetyAssessmentRes;
	}

}
