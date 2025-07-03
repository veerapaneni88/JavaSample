package us.tx.state.dfps.service.pcaeligdetermination.controller;

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
import us.tx.state.dfps.service.common.request.PcaApplAndDetermReq;
import us.tx.state.dfps.service.common.request.PcaEligDeterminationReq;
import us.tx.state.dfps.service.common.response.PcaApplAndDetermRes;
import us.tx.state.dfps.service.common.response.PcaEligDeterminationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.pcaeligdetermination.service.PcaEligDeterminationService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PcaEligDeterminationController Oct 16, 2017- 12:09:51 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/pcaEligDetermination")
public class PcaEligDeterminationController {

	@Autowired
	private PcaEligDeterminationService pcaEligDeterminationService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(PcaEligDeterminationController.class);

	/**
	 * Method Name: fetchEligibilityDetermination Method Description:This method
	 * returns Pca Eligibility Determination Record using Application Event.
	 * 
	 * @param pcaAppDetermReq
	 * @return PcaApplAndDetermRes
	 */
	@RequestMapping(value = "/fetchEligibilityDetermination", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaApplAndDetermRes fetchEligibilityDetermination(
			@RequestBody PcaApplAndDetermReq pcaAppDetermReq) {
		log.debug("Entering method fetchEligibilityDetermination in PcaEligDeterminationController");
		if (TypeConvUtil.isNullOrEmpty(pcaAppDetermReq.getIdAppEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("PcaEligDeterminationController.idAppEvent.mandatory", null, Locale.US));
		}
		PcaApplAndDetermRes pcaApplAndDetermRes = new PcaApplAndDetermRes();
		pcaApplAndDetermRes.setPcaApplAndDetermDBDto(
				pcaEligDeterminationService.fetchEligibilityDetermination(pcaAppDetermReq.getIdAppEvent()));
		log.debug("Exiting method fetchEligibilityDetermination in PcaEligDeterminationController");
		return pcaApplAndDetermRes;
	}

	/**
	 * Method Name: saveEligibilityDetermination Method Description:his method
	 * saves(updates) Pca Eligibility Determination information. Empty
	 * Eligibility Determination Record will be created when the worker submits
	 * Pca Application. So this function needs to handle updates only.
	 * 
	 * @param pcaAppDetermReq
	 * @return PcaApplAndDetermRes
	 */
	@RequestMapping(value = "/saveEligibilityDetermination", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaApplAndDetermRes saveEligibilityDetermination(
			@RequestBody PcaApplAndDetermReq pcaAppDetermReq) {
		log.debug("Entering method saveEligibilityDetermination in PcaEligDeterminationController");
		if (TypeConvUtil.isNullOrEmpty(pcaAppDetermReq.getPcaApplAndDetermDBDto())) {
			throw new InvalidRequestException(messageSource
					.getMessage("PcaEligDeterminationController.pcaApplAndDetermDBDto.mandatory", null, Locale.US));
		}
		PcaApplAndDetermRes pcaApplAndDetermRes = new PcaApplAndDetermRes();
		pcaApplAndDetermRes.setUpdatedResult(
				pcaEligDeterminationService.saveEligibilityDetermination(pcaAppDetermReq.getPcaApplAndDetermDBDto()));
		log.debug("Exiting method saveEligibilityDetermination in PcaEligDeterminationController");
		return pcaApplAndDetermRes;
	}

	/**
	 * Method Name: determinePrelimEligibility Method Description:This method
	 * determines Preliminary Eligibility and the saves the Determination to
	 * database.
	 * 
	 * @param pcaAppDetermReq
	 * @return PcaApplAndDetermRes
	 */
	@RequestMapping(value = "/determinePrelimEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaApplAndDetermRes determinePrelimEligibility(
			@RequestBody PcaApplAndDetermReq pcaAppDetermReq){
		log.debug("Entering method determinePrelimEligibility in PcaEligDeterminationController");
		if (TypeConvUtil.isNullOrEmpty(pcaAppDetermReq.getPcaApplAndDetermDBDto())) {
			throw new InvalidRequestException(messageSource
					.getMessage("PcaEligDeterminationController.pcaApplAndDetermDBDto.mandatory", null, Locale.US));
		}
		PcaApplAndDetermRes pcaApplAndDetermRes = new PcaApplAndDetermRes();
		pcaApplAndDetermRes.setUpdatedResult(
				pcaEligDeterminationService.determinePrelimEligibility(pcaAppDetermReq.getPcaApplAndDetermDBDto()));
		log.debug("Exiting method determinePrelimEligibility in PcaEligDeterminationController");
		return pcaApplAndDetermRes;
	}

	/**
	 * Method Name: determineFinalEligibility Method Description:This method
	 * determines Final Eligibility and the saves the determination to database.
	 * 
	 * @param pcaAppDetermReq
	 * @return PcaApplAndDetermRes
	 */
	@RequestMapping(value = "/determineFinalEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaApplAndDetermRes determineFinalEligibility(@RequestBody PcaApplAndDetermReq pcaAppDetermReq){
		log.debug("Entering method determineFinalEligibility in PcaEligDeterminationController");
		if (TypeConvUtil.isNullOrEmpty(pcaAppDetermReq.getPcaApplAndDetermDBDto())) {
			throw new InvalidRequestException(messageSource
					.getMessage("PcaEligDeterminationController.pcaApplAndDetermDBDto.mandatory", null, Locale.US));
		}
		PcaApplAndDetermRes pcaApplAndDetermRes = new PcaApplAndDetermRes();
		pcaApplAndDetermRes.setUpdatedResult(
				pcaEligDeterminationService.determineFinalEligibility(pcaAppDetermReq.getPcaApplAndDetermDBDto()));
		log.debug("Exiting method determineFinalEligibility in PcaEligDeterminationController");
		return pcaApplAndDetermRes;
	}

	/**
	 * Method Name: selectDetermFromIdPcaApp Method Description:This method
	 * returns Eligibility Determination Record for the given Application Id
	 * 
	 * @param pcaEligDeterminationReq
	 * @return PcaEligDeterminationRes
	 */
	@RequestMapping(value = "/selectDetermFromIdPcaApp", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcaEligDeterminationRes selectDetermFromIdPcaApp(
			@RequestBody PcaEligDeterminationReq pcaEligDeterminationReq) {
		log.debug("Entering method selectDetermFromIdPcaApp in PcaEligDeterminationController");
		if (TypeConvUtil.isNullOrEmpty(pcaEligDeterminationReq.getIdPcaEligApplication())) {
			throw new InvalidRequestException(messageSource
					.getMessage("PcaEligDeterminationController.idPcaEligApplication.mandatory", null, Locale.US));
		}
		PcaEligDeterminationRes pcaEligDeterminationRes = new PcaEligDeterminationRes();
		pcaEligDeterminationRes.setPcaEligDeterminationDto(pcaEligDeterminationService
				.selectDetermFromIdPcaApp(pcaEligDeterminationReq.getIdPcaEligApplication()));
		log.debug("Exiting method selectDetermFromIdPcaApp in PcaEligDeterminationController");
		return pcaEligDeterminationRes;
	}
}
