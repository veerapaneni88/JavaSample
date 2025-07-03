package us.tx.state.dfps.service.fce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.EligibilitySummaryReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EligibilitySummaryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.EligibilitySummaryDto;
import us.tx.state.dfps.service.fce.service.EligibilitySummaryService;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.service.ServicePackageService;

import java.util.List;
import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * EligibilitySummaryServiceController Mar 12, 2018- 11:29:01 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/eligibiltySummary")
public class EligibilitySummaryServiceController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	EligibilitySummaryService eligibilitySummaryService;

	@Autowired
	ServicePackageService servicePackageService;

	/**
	 * Method Name: saveEligibilitySummary Method Description: This method is
	 * used to saveEligibilitySummary
	 * 
	 * @param eligibilitySummaryReq
	 * @return EligibilitySummaryRes
	 */
	@RequestMapping(value = "/saveEligibilitySummary", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EligibilitySummaryRes saveEligibilitySummary(
			@RequestBody EligibilitySummaryReq eligibilitySummaryReq) {

		EligibilitySummaryDto eligibilitySummary = eligibilitySummaryReq.getEligibilitySummaryDto();

		EligibilitySummaryRes response = new EligibilitySummaryRes();
		eligibilitySummaryService.save(eligibilitySummary);
		response.setResult(Boolean.TRUE);
		return response;
	}

	/**
	 * Method Name: deleteEligibilitySummary Method Description: This method is
	 * used to deleteEligibilitySummary
	 * 
	 * @param eligibilitySummaryReq
	 * @ @return EligibilitySummaryRes
	 */
	@RequestMapping(value = "/deleteEligibilitySummary", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EligibilitySummaryRes deleteEligibilitySummary(
			@RequestBody EligibilitySummaryReq eligibilitySummaryReq) {

		EligibilitySummaryDto eligibilitySummaryDto = eligibilitySummaryReq.getEligibilitySummaryDto();
		EligibilitySummaryRes response = new EligibilitySummaryRes();
		eligibilitySummaryService.delete(eligibilitySummaryDto);
		response.setResult(Boolean.TRUE);
		return response;

	}

	/**
	 * Method Name: getEligibilitySummary Method Description: This method is
	 * used to getEligibilitySummary
	 * 
	 * @param eligibilitySummaryReq
	 * @return EligibilitySummaryRes
	 */
	@RequestMapping(value = "/getEligibilitySummary", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EligibilitySummaryRes getEligibilitySummary(
			@RequestBody EligibilitySummaryReq eligibilitySummaryReq) {
		EligibilitySummaryRes eligibilitySummaryRes = eligibilitySummaryService.read(eligibilitySummaryReq.getIdStage(),
				eligibilitySummaryReq.getIdEvent(), eligibilitySummaryReq.getIdLastUpdatePerson());

		eligibilitySummaryRes.getEligibilitySummaryDto().getFceEligibilityDto()
				.setDtLastUpdate(eligibilitySummaryService.getFceEligibility(
						eligibilitySummaryRes.getEligibilitySummaryDto().getFceEligibilityDto().getIdFceEligibility())
						.getDtLastUpdate());

		return eligibilitySummaryRes;
	}

	/**
	 * Method Name: isAutoEligibility Method Description: This method is used to
	 * isAutoEligibility
	 * 
	 * @param idEvent
	 * @ @return CommonHelperRes
	 */
	@RequestMapping(value = "/isAutoEligibility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes isAutoEligibility(@RequestBody Long idEvent) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(eligibilitySummaryService.isAutoEligibility(idEvent));
		return commonHelperRes;
	}

	/**
	 * Method Name: getAdoProcessStatus Method Description: This method is used
	 * to getAdoProcessStatus
	 * 
	 * @param idStage
	 * @ @return CommonHelperRes
	 */
	@RequestMapping(value = "/getAdoProcessStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getAdoProcessStatus(@RequestBody Long idStage) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(eligibilitySummaryService.getAdoProcessStatus(idStage));
		return commonHelperRes;
	}

	/**
	 * Method Name: getEligibilityByStage Method Description: This method will
	 * fetches the eligibility records based on Stage and eligibilityIdPerson
	 *
	 * @param eligibilitySummaryReq
	 * @return EligibilitySummaryRes
	 */
	@RequestMapping(value = "/getEligibilityByStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EligibilitySummaryRes getEligibilityByStage(
			@RequestBody EligibilitySummaryReq eligibilitySummaryReq) {
		if (TypeConvUtil.isNullOrEmpty(eligibilitySummaryReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		EligibilitySummaryRes response  = eligibilitySummaryService.getEligibilityByStage(eligibilitySummaryReq.getIdStage());
		response.setResult(Boolean.TRUE);
		return response;
	}

}
