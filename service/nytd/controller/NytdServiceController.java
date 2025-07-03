package us.tx.state.dfps.service.nytd.controller;

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
import us.tx.state.dfps.service.common.request.NytdPopulationSearchReq;
import us.tx.state.dfps.service.common.request.NytdReportPeriodReq;
import us.tx.state.dfps.service.common.request.NytdReq;
import us.tx.state.dfps.service.common.request.NytdYouthHistoryReq;
import us.tx.state.dfps.service.common.response.NytdPopulationSearchRes;
import us.tx.state.dfps.service.common.response.NytdReportPeriodRes;
import us.tx.state.dfps.service.common.response.NytdRes;
import us.tx.state.dfps.service.common.response.NytdYouthHistoryRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.nytd.service.NytdService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * implements the methods implemented in Legacy Nytd EJB Apr 30, 2018- 4:21:06
 * PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/nytd")
public class NytdServiceController {
	private static final String DT_SEARCH_MANDATORY = "dtSearch.mandatory";
	private static final String PERSON_ID_PERSON_MANDATORY = "person.idPerson.mandatory";
	private static final String TRANSACTION_ID = "TransactionId :";
	private static final String PERSON_ID_STAGE_MANDATORY = "person.stageid.mandatory";

	@Autowired
	MessageSource messageSource;

	@Autowired
	NytdService nytdService;

	private static final Logger log = Logger.getLogger(NytdServiceController.class);

	/**
	 * Method Name: setNewPersonView Method Description: Set the # in the record
	 * that identifies this record as not viewed.
	 * 
	 * @param nytdReq
	 * @return NytdRes
	 * 
	 */
	@RequestMapping(value = "/setNewPersonView", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  NytdRes setNewPersonView(@RequestBody NytdReq nytdReq) {
		NytdRes response = new NytdRes();
		response.setUpdateResponse(nytdService.setNewPersonView(nytdReq.getNytdID(), nytdReq.getIdAssignedPerson()));
		return response;
	}

	/**
	 * Method Name: getSaveNytdYouthOutcomeReportingStatus Method Description:
	 * Save the Reporting Status for this youth record.
	 * 
	 * @param nytdReq
	 * @return response
	 */
	@RequestMapping(value = "/saveNytdYouthOutcomeReportingStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NytdRes getSaveNytdYouthOutcomeReportingStatus(@RequestBody NytdReq nytdReq) {
		NytdRes response = new NytdRes();
		response.setUpdateResponse(nytdService.saveNytdYouthOutcomeReportingStatus(nytdReq.getNytdSurveyHeaderId(),
				nytdReq.getReportingStatus()));
		return response;

	}

	/**
	 * Method Name: getSaveSurveyAppAuthInfo Method Description: This method
	 * saves Authentication Info record into impact_rqst_nytd@nytd table.
	 * 
	 * @param nytdReq
	 * @return response
	 */
	@RequestMapping(value = "/saveSurveyAppAuthInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NytdRes saveSurveyAppAuthInfo(@RequestBody NytdReq nytdReq) {
		NytdRes response = new NytdRes();
		response.setUpdateResponse(nytdService.saveSurveyAppAuthInfo(nytdReq.getIdStaff()));
		return response;
	}

	/**
	 * Method Name: retrieveNytdYouthHistory Method Description: Get the List of
	 * Nytd Youth History records.
	 * 
	 * @param nytdRetriveReq
	 * @return nytdRetriveRes
	 */
	@RequestMapping(value = "/retrieveNytdYouthHistory", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NytdYouthHistoryRes retrieveNytdYouthHistory(@RequestBody NytdYouthHistoryReq nytdRetriveReq) {
		if (TypeConvUtil.isNullOrEmpty(nytdRetriveReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(PERSON_ID_STAGE_MANDATORY, null, Locale.US));
		}
		NytdYouthHistoryRes nytdRetriveRes = new NytdYouthHistoryRes();
		nytdRetriveRes.setTransactionId(nytdRetriveReq.getTransactionId());
		log.debug(TRANSACTION_ID + nytdRetriveReq.getTransactionId());
		nytdRetriveRes.setNytdYouthHistoryDto(nytdService.retrieveNytdYouthHistory(nytdRetriveReq.getIdStage()));
		return nytdRetriveRes;

	}

	/**
	 * Method Name: retrieveNytdYouthContactInfo Method Description:Get
	 * YouthContactInfoValueBean containing the info related to NYTD youth and
	 * youth designated contact primary and other current information.
	 * 
	 * @param nytdRetriveReq
	 * @return nytdFetchRes
	 */
	@RequestMapping(value = "/retrieveNytdYouthContactInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NytdYouthHistoryRes retrieveNytdYouthContactInfo(
			@RequestBody NytdYouthHistoryReq nytdRetriveReq) {
		if (TypeConvUtil.isNullOrEmpty(nytdRetriveReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(PERSON_ID_STAGE_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(nytdRetriveReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(PERSON_ID_PERSON_MANDATORY, null, Locale.US));
		}
		NytdYouthHistoryRes nytdFetchRes = new NytdYouthHistoryRes();
		nytdFetchRes.setTransactionId(nytdRetriveReq.getTransactionId());
		log.debug(TRANSACTION_ID + nytdRetriveReq.getTransactionId());
		nytdFetchRes.setNytdYouthContactInfoDto(
				nytdService.retrieveNytdYouthContactInfo(nytdRetriveReq.getIdStage(), nytdRetriveReq.getIdPerson()));
		return nytdFetchRes;

	}

	/**
	 * Method Name: getNytdReportingPeriod Method Description: Get the NYTD
	 * Reporting Period as a ValueBean for an input date
	 * 
	 * @param nytdReportPeriodReq
	 * @return nytdReportPeriodRes
	 */
	@RequestMapping(value = "/getNytdReportingPeriod", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NytdReportPeriodRes getNytdReportingPeriod(
			@RequestBody NytdReportPeriodReq nytdReportPeriodReq) {
		if (TypeConvUtil.isNullOrEmpty(nytdReportPeriodReq.getDtNytdReportPeriod())) {
			throw new InvalidRequestException(messageSource.getMessage(DT_SEARCH_MANDATORY, null, Locale.US));
		}
		NytdReportPeriodRes nytdReportPeriodRes = new NytdReportPeriodRes();
		nytdReportPeriodRes.setTransactionId(nytdReportPeriodReq.getTransactionId());
		log.debug(TRANSACTION_ID + nytdReportPeriodReq.getTransactionId());
		nytdReportPeriodRes.setNytdReportPeriodDto(
				nytdService.getNytdReportingPeriod(nytdReportPeriodReq.getDtNytdReportPeriod()));
		return nytdReportPeriodRes;

	}

	/**
	 * Method Name: getCurrentReportingPeriod Method Description: Get the NYTD
	 * currentReporting Period as a ValueBean for an input date
	 * 
	 * @param nytdReportPeriodReq
	 * @return nytdReportPeriodRes
	 */

	@RequestMapping(value = "/getCurrentReportingPeriod", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NytdReportPeriodRes getCurrentReportingPeriod(
			@RequestBody NytdReportPeriodReq nytdReportPeriodReq) {
		NytdReportPeriodRes nytdReportPeriodRes = new NytdReportPeriodRes();
		nytdReportPeriodRes.setTransactionId(nytdReportPeriodReq.getTransactionId());
		log.debug(TRANSACTION_ID + nytdReportPeriodReq.getTransactionId());
		nytdReportPeriodRes.setNytdReportPeriodDto(nytdService.getCurrNytdReportingPeriod());
		return nytdReportPeriodRes;

	}

	/**
	 * Method Name: searchNytdPopulation Method Description: Get the NYTD
	 * Population based on Input Criteria
	 * 
	 * @param nytdSearchValueDto
	 * @return NytdPopulationSearchRes
	 */
	@RequestMapping(value = "/searchNytdPopulation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  NytdPopulationSearchRes searchNytdPopulation(
			@RequestBody NytdPopulationSearchReq nytdPopulationSearchReq) {
		NytdPopulationSearchRes result = new NytdPopulationSearchRes();
		result.setNytdSearchResultList(
				nytdService.searchNytdPopulation(nytdPopulationSearchReq.getNytdSearchValueDto()));
		return result;
	}

}
