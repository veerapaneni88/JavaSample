package us.tx.state.dfps.service.stageutility.controller;

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
import us.tx.state.dfps.service.common.request.StageProgressionReq;
import us.tx.state.dfps.service.common.response.ListRes;
import us.tx.state.dfps.service.common.response.StagePersonRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.stageutility.service.StageUtilityService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * has functions to access Stage related information. Oct 12, 2017- 2:53:11 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/StageUtility")
public class StageUtilityController {

	@Autowired
	private StageUtilityService stageUtilityService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger(StageUtilityController.class);

	/**
	 * Method Name: retrieveStageInfo Method Description: This method retrieves
	 * information from Stage table using idStage.
	 * 
	 * @param stageProgressionReq
	 * @return StageRes
	 *//*
		 * @RequestMapping(value = "/retrieveStageInfo", headers = {
		 * "Accept=application/json" }, method = RequestMethod.POST)
		 * public  StageRes retrieveStageInfo(@RequestBody
		 * StageProgressionReq stageProgressionReq) { LOG.
		 * debug("Entering method retrieveStageInfo in StageUtilityController");
		 * if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdStage())) {
		 * throw new InvalidRequestException(
		 * messageSource.getMessage("stageProgressionReq.idStage.mandatory",
		 * null, Locale.US)); } StageRes stageRes = new StageRes();
		 * stageRes.setTransactionId(stageProgressionReq.getTransactionId());
		 * LOG.info("TransactionId :" + stageProgressionReq.getTransactionId());
		 * stageRes.setStageValueBeanDto(stageUtilityService.retrieveStageInfo((
		 * long) stageProgressionReq.getIdStage()));
		 * 
		 * LOG.
		 * debug("Exiting method retrieveStageInfo in StageUtilityController");
		 * return stageRes; }
		 */

	/**
	 * Method Name: findPrimaryChildForStage Method Description: This method
	 * returns Primary Child for the Stage.
	 * 
	 * @param stageProgressionReq
	 * @return StagePersonRes
	 */
	@RequestMapping(value = "/findPrimaryChildForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StagePersonRes findPrimaryChildForStage(@RequestBody StageProgressionReq stageProgressionReq) {
		LOG.debug("Entering method findPrimaryChildForStage in StageUtilityController");
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.idStage.mandatory", null, Locale.US));
		}
		StagePersonRes stagePersonRes = new StagePersonRes();
		stagePersonRes.setTransactionId(stageProgressionReq.getTransactionId());
		LOG.info("TransactionId :" + stageProgressionReq.getTransactionId());

		stagePersonRes
				.setIdPerson(stageUtilityService.findPrimaryChildForStage((long) stageProgressionReq.getIdStage()));
		LOG.debug("Exiting method findPrimaryChildForStage in StageUtilityController");
		return stagePersonRes;
	}

	/**
	 * Method Name: findWorkersForStage Method Description: This method returns
	 * the primary and secondary workers assigned to the stage with the given
	 * security profiles. If the security profile list parameter is empty or
	 * null, it returns all the primary and secondary workers assigned to the
	 * stage.
	 * 
	 * @param stageProgressionReq
	 * @return ListRes
	 */
	@RequestMapping(value = "/findWorkersForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ListRes findWorkersForStage(@RequestBody StageProgressionReq stageProgressionReq) {
		LOG.debug("Entering method findWorkersForStage in StageUtilityController");
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getSecProfiles())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.secProfiles.mandatory", null, Locale.US));
		}
		ListRes listRes = new ListRes();
		listRes.setTransactionId(stageProgressionReq.getTransactionId());
		LOG.info("TransactionId :" + stageProgressionReq.getTransactionId());
		listRes.setWorkersList(stageUtilityService.findWorkersForStage((long) stageProgressionReq.getIdStage(),
				stageProgressionReq.getSecProfiles()));
		LOG.debug("Exiting method findWorkersForStage in StageUtilityController");
		return listRes;
	}

	/**
	 * Method Name: fetchReasonForDeathMissing Method Description: This method
	 * gets count of persons on stage with DOD but no death code, for use in
	 * validation of INV stage closures.
	 * 
	 * @param stageProgressionReq
	 * @return StagePersonRes
	 */
	@RequestMapping(value = "/fetchReasonForDeathMissing", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StagePersonRes fetchReasonForDeathMissing(
			@RequestBody StageProgressionReq stageProgressionReq) {
		LOG.debug("Entering method fetchReasonForDeathMissing in StageUtilityController");
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.idStage.mandatory", null, Locale.US));
		}
		StagePersonRes stagePersonRes = new StagePersonRes();
		stagePersonRes.setTransactionId(stageProgressionReq.getTransactionId());
		LOG.info("TransactionId :" + stageProgressionReq.getTransactionId());
		stagePersonRes.setReasonMissingCount(
				stageUtilityService.fetchReasonForDeathMissing((long) stageProgressionReq.getIdStage()));
		LOG.debug("Exiting method fetchReasonForDeathMissing in StageUtilityController");
		return stagePersonRes;
	}

	/**
	 * Method Name: getCheckedOutStagesForPerson Method Description: This method
	 * Fetches the list of stages which has this person and are checkout to MPS
	 * 
	 * @param stageProgressionReq
	 * @return ListRes
	 */
	@RequestMapping(value = "/getCheckedOutStagesForPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ListRes getCheckedOutStagesForPerson(@RequestBody StageProgressionReq stageProgressionReq) {
		LOG.debug("Entering method getCheckedOutStagesForPerson in StageUtilityController");
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.idPerson.mandatory", null, Locale.US));
		}
		ListRes listRes = new ListRes();
		listRes.setTransactionId(stageProgressionReq.getTransactionId());
		LOG.info("TransactionId :" + stageProgressionReq.getTransactionId());
		listRes.setStagePersonValueDtos(
				stageUtilityService.getCheckedOutStagesForPerson(stageProgressionReq.getIdPerson()));
		LOG.debug("Exiting method getCheckedOutStagesForPerson in StageUtilityController");
		return listRes;
	}

	/**
	 * Method Name: isPrimaryChildInOpenStage Method Description: This method is
	 * to check if a person in Primary Child in open stage
	 * 
	 * @param stageProgressionReq
	 * @return StagePersonRes
	 */
	@RequestMapping(value = "/isPrimaryChildInOpenStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StagePersonRes isPrimaryChildInOpenStage(
			@RequestBody StageProgressionReq stageProgressionReq) {
		LOG.debug("Entering method isPrimaryChildInOpenStage in StageUtilityController");
		if (TypeConvUtil.isNullOrEmpty(stageProgressionReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stageProgressionReq.idPerson.mandatory", null, Locale.US));
		}
		StagePersonRes stagePersonRes = new StagePersonRes();
		stagePersonRes.setTransactionId(stageProgressionReq.getTransactionId());
		LOG.info("TransactionId :" + stageProgressionReq.getTransactionId());
		stagePersonRes
				.setIsPrimaryChild(stageUtilityService.isPrimaryChildInOpenStage(stageProgressionReq.getIdPerson()));
		LOG.debug("Exiting method isPrimaryChildInOpenStage in StageUtilityController");
		return stagePersonRes;
	}
}
