package us.tx.state.dfps.service.fce.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.EligibilityDeterminationFceReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EligibilityDeterminationFceRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.service.EligibilityDeterminationService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * is used for Adoption Assistance and FosterCare Eligibility Determination
 * Controller Mar 15, 2018- 10:25:50 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/eligibilityDetermination")
public class EligibilityDeterminationController {

	@Autowired
	private EligibilityDeterminationService eligibilityDeterminationService;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Method Name: fetchEligDetermination Method Description: This method is
	 * used for reads the EligibilityDetermination details
	 * 
	 * @param CommonHelperReq
	 * @return EligibilityDeterminationFceRes
	 */
	@RequestMapping(value = "/fetchEligDetermination", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EligibilityDeterminationFceRes fetchEligDetermination(
			@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getUserId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.userId.mandatory", null, Locale.US));
		}
		EligibilityDeterminationFceRes commonHelperRes = new EligibilityDeterminationFceRes();
		commonHelperRes.setTransactionId(commonHelperReq.getTransactionId());
		commonHelperRes.setEligibilityDeterminationFceDto(eligibilityDeterminationService.fetchEligDetermination(
				commonHelperReq.getIdStage(), commonHelperReq.getIdEvent(), Long.valueOf(commonHelperReq.getUserId())));
		return commonHelperRes;
	}

	/**
	 * Method Name: hasDOBChangedForCertPers Method Description: This method is
	 * used for Checks if certified persons DOB has changed
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/hasDOBChangedForCertPers", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes hasDOBChangedForCertPers(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdFceEligibility())) {
			throw new InvalidRequestException(messageSource.getMessage("IdFceEligibility.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setTransactionId(commonHelperReq.getTransactionId());
		commonHelperRes.setDobChangedForCertPers(
				eligibilityDeterminationService.hasDOBChangedForCertPers(commonHelperReq.getIdFceEligibility()));
		return commonHelperRes;
	}

	/**
	 * Method Name: confirmEligibility Method Description: This method is used
	 * for Confirms the Eligibility
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/confirmEligibility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes confirmEligibility(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getUserId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.userId.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes = eligibilityDeterminationService.confirmEligibility(commonHelperReq.getIdStage(),
				commonHelperReq.getIdEvent(), Long.valueOf(commonHelperReq.getUserId()));
		commonHelperRes.setTransactionId(commonHelperReq.getTransactionId());
		return commonHelperRes;
	}

	/**
	 * Method Name: determineEligibility Method Description: This method is used
	 * for Determines the Eligibility
	 * 
	 * @param eligibilityDeterminationFceReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/determineEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes determineEligibility(
			@RequestBody EligibilityDeterminationFceReq eligibilityDeterminationFceReq) {
		if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFceReq.getEligibilityDeterminationFceDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("eligibilityDeterminationFceDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(
				eligibilityDeterminationFceReq.getEligibilityDeterminationFceDto().getFceEligibilityDto())) {
			throw new InvalidRequestException(messageSource.getMessage("fceEligibilityDto.mandatory", null, Locale.US));
		}
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes = eligibilityDeterminationService
				.determineEligibility(eligibilityDeterminationFceReq.getEligibilityDeterminationFceDto());
		commonHelperRes.setTransactionId(eligibilityDeterminationFceReq.getTransactionId());
		return commonHelperRes;
	}

	/**
	 * Method Name: saveEligibility Method Description: This method is used for
	 * saves the eligibility determination details.
	 * 
	 * @param eligibilityDeterminationFceReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/saveEligibility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes saveEligibility(
			@RequestBody EligibilityDeterminationFceReq eligibilityDeterminationFceReq) {
		if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFceReq.getEligibilityDeterminationFceDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("EligibilityDeterminationFceDto.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(
				eligibilityDeterminationFceReq.getEligibilityDeterminationFceDto().getFceEligibilityDto())) {
			throw new InvalidRequestException(messageSource.getMessage("FceEligibilityDto.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFceReq.getEligibilityDeterminationFceDto()
				.getFceEligibilityDto().getIdFceEligibility())) {
			throw new InvalidRequestException(messageSource.getMessage("idFceEligibility.mandatory", null, Locale.US));
		}

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes = eligibilityDeterminationService
				.saveEligibility(eligibilityDeterminationFceReq.getEligibilityDeterminationFceDto());
		commonHelperRes.setTransactionId(eligibilityDeterminationFceReq.getTransactionId());
		return commonHelperRes;
	}

	/**
	 * Commented Code will be used for adaption assistance
	 */
	/*
	*//**
		 * Method Name: getEligDeterminationInfo Method Description: This method
		 * returns Adoption Assistance Determination information including -
		 * Event
		 * 
		 * @param eligibilityDeterminationFetchReq
		 * @return EligibilityDeterminationRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/getEligDeterminationInfo", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationRes
	 * getEligDeterminationInfo(
	 * 
	 * @RequestBody EligibilityDeterminationFetchReq
	 * eligibilityDeterminationFetchReq) { LOG.
	 * debug("Entering method getEligDeterminationInfo in EligibilityDeterminationController"
	 * ); if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.getIdStage()
	 * )) { throw new InvalidRequestException(messageSource.getMessage(
	 * "common.stageid.mandatory", null, Locale.US)); } if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.
	 * getIdAppEvent())) { throw new InvalidRequestException(
	 * messageSource.getMessage("pcaAppBckgnd.idAppEvent.mandatory", null,
	 * Locale.US)); } EligibilityDeterminationRes eligibilityDeterminationRes =
	 * new EligibilityDeterminationRes();
	 * eligibilityDeterminationRes.setTransactionId(
	 * eligibilityDeterminationFetchReq.getTransactionId());
	 * eligibilityDeterminationRes.setAaeApplAndDetermDBDto(
	 * eligibilityDeterminationService.getEligDeterminationInfo(
	 * eligibilityDeterminationFetchReq.getIdStage(),
	 * eligibilityDeterminationFetchReq.getIdAppEvent()));
	 * LOG.info("TransactionId :" +
	 * eligibilityDeterminationFetchReq.getTransactionId()); LOG.
	 * debug("Exiting method getEligDeterminationInfo in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationRes; }
	 * 
	 *//**
		 * Method Name: updateEventAndCreateEligDeterm Method Description: This
		 * method update the Event to PEND and will create a new Adoption
		 * Assistance Eligibility Determination This is called when the
		 * Application is submitted to Eligibility Specialist
		 * 
		 * @param eligibilityDeterminationFetchReq
		 * @return EligibilityDeterminationRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/updateEventAndCreateEligDeterm", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationRes
	 * updateEventAndCreateEligDeterm(
	 * 
	 * @RequestBody EligibilityDeterminationFetchReq
	 * eligibilityDeterminationFetchReq) { LOG.
	 * debug("Entering method updateEventAndCreateEligDeterm in EligibilityDeterminationController"
	 * ); if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.getIdEvent()
	 * )) { throw new InvalidRequestException(messageSource.getMessage(
	 * "common.eventid.mandatory", null, Locale.US)); } if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.
	 * getIdLastUpdatePerson())) { throw new InvalidRequestException(
	 * messageSource.getMessage("common.lastUpdatePersonId.mandatory", null,
	 * Locale.US)); } EligibilityDeterminationRes eligibilityDeterminationRes =
	 * new EligibilityDeterminationRes();
	 * eligibilityDeterminationRes.setTransactionId(
	 * eligibilityDeterminationFetchReq.getTransactionId());
	 * eligibilityDeterminationRes.setUpdateResult(
	 * eligibilityDeterminationService.updateEventAndCreateEligDeterm(
	 * eligibilityDeterminationFetchReq.getIdEvent(),
	 * eligibilityDeterminationFetchReq.getIdLastUpdatePerson()));
	 * LOG.info("TransactionId :" +
	 * eligibilityDeterminationFetchReq.getTransactionId()); LOG.
	 * debug("Exiting method updateEventAndCreateEligDeterm in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationRes; }
	 * 
	 *//**
		 * Method Name: saveEligDeterminationInfo Method Description: This
		 * method update Adoption Assistance Eligibility Determination and the
		 * event.
		 * 
		 * @param eligibilityDeterminationReq
		 * @return EligibilityDeterminationRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/saveEligDeterminationInfo", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationRes
	 * saveEligDeterminationInfo(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method saveEligDeterminationInfo in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } EligibilityDeterminationRes eligibilityDeterminationRes =
	 * new EligibilityDeterminationRes();
	 * eligibilityDeterminationRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); eligibilityDeterminationRes.setUpdateResult(
	 * eligibilityDeterminationService
	 * .saveEligDeterminationInfo(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId()); LOG.
	 * debug("Exiting method saveEligDeterminationInfo in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationRes; }
	 * 
	 *//**
		 * Method Name: saveEligDetermAndCompTodo Method Description:This method
		 * saves Adoption Assistance Determination and completes Adoption
		 * Assistance Eligibility Determination Todo as complete.
		 * 
		 * @param eligibilityDeterminationReq
		 * @return EligibilityDeterminationRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/saveEligDetermAndCompTodo", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationRes
	 * saveEligDetermAndCompTodo(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method saveEligDetermAndCompTodo in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } EligibilityDeterminationRes eligibilityDeterminationRes =
	 * new EligibilityDeterminationRes();
	 * eligibilityDeterminationRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); eligibilityDeterminationRes.setUpdateResult(
	 * eligibilityDeterminationService
	 * .saveEligDetermAndCompTodo(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId()); LOG.
	 * debug("Exiting method saveEligDetermAndCompTodo in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationRes; }
	 * 
	 *//**
		 * Method Name: saveEligDeterminationValueBean Method Description:This
		 * method saves AAE Determination value bean.
		 * 
		 * @param eligibilityDeterminationSaveReq
		 * @return EligibilityDeterminationRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/saveEligDeterminationValueBean", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationRes
	 * saveEligDeterminationValueBean(
	 * 
	 * @RequestBody EligibilityDeterminationSaveReq
	 * eligibilityDeterminationSaveReq) { LOG.
	 * debug("Entering method saveEligDeterminationDto in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationSaveReq.
	 * getEligibilityDeterminationDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("EligibilityDeterminationDto.mandatory", null,
	 * Locale.US)); } EligibilityDeterminationRes eligibilityDeterminationRes =
	 * new EligibilityDeterminationRes();
	 * eligibilityDeterminationRes.setTransactionId(
	 * eligibilityDeterminationSaveReq.getTransactionId());
	 * eligibilityDeterminationRes.setUpdateResult(
	 * eligibilityDeterminationService
	 * .saveEligDeterminationValueBean(eligibilityDeterminationSaveReq.
	 * getEligibilityDeterminationDto())); LOG.info("TransactionId :" +
	 * eligibilityDeterminationSaveReq.getTransactionId()); LOG.
	 * debug("Exiting method saveEligDeterminationDto in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationRes; }
	 * 
	 *//**
		 * Method Name: determinePrelimDetermin Method Description:This method
		 * determines Preliminary Determination and the saves Adoption
		 * Assistance Eligibility Determination
		 * 
		 * @param eligibilityDeterminationReq
		 * @return EligibilityDeterminationRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/determinePrelimDetermin", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationRes determinePrelimDetermin(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method determinePrelimDetermin in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } EligibilityDeterminationRes eligibilityDeterminationRes =
	 * new EligibilityDeterminationRes();
	 * eligibilityDeterminationRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); eligibilityDeterminationRes.setUpdateResult(
	 * eligibilityDeterminationService
	 * .determinePrelimDetermin(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId()); LOG.
	 * debug("Exiting method determinePrelimDetermin in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationRes; }
	 * 
	 *//**
		 * Method Name: calculatePrelimEligDeterm Method Description: This
		 * method returns preliminary eligibility determination value in form of
		 * AaeEligDetermMessgDto
		 * 
		 * @param eligibilityDeterminationReq
		 * @return AaeEligDetermMessgRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/calculatePrelimEligDeterm", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  AaeEligDetermMessgRes calculatePrelimEligDeterm(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method calculatePrelimEligDeterm in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeEligDetermMessgDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeEligDetermMessgDto.mandatory", null,
	 * Locale.US)); } AaeEligDetermMessgRes aaeEligDetermMessgRes = new
	 * AaeEligDetermMessgRes();
	 * aaeEligDetermMessgRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId());
	 * aaeEligDetermMessgRes.setAaeEligDetermMessgDto(
	 * eligibilityDeterminationService.calculatePrelimEligDeterm(
	 * eligibilityDeterminationReq.getAaeApplAndDetermDBDto(),
	 * eligibilityDeterminationReq.getAaeEligDetermMessgDto())); LOG.
	 * debug("Exiting method calculatePrelimEligDeterm in EligibilityDeterminationController"
	 * ); return aaeEligDetermMessgRes; }
	 * 
	 *//**
		 * Method Name: determineFinalDetermin Method Description:This method
		 * determines Final Determination and the saves AAE Determination and
		 * returns the determination message Dto
		 * 
		 * @param eligibilityDeterminationReq
		 * @return AaeEligDetermMessgRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/determineFinalDetermin", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  AaeEligDetermMessgRes determineFinalDetermin(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method determineFinalDetermin in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } AaeEligDetermMessgRes aaeEligDetermMessgRes = new
	 * AaeEligDetermMessgRes();
	 * aaeEligDetermMessgRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId());
	 * aaeEligDetermMessgRes.setAaeEligDetermMessgDto(
	 * eligibilityDeterminationService
	 * .determineFinalDetermin(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())); LOG.
	 * debug("Exiting method determineFinalDetermin in EligibilityDeterminationController"
	 * ); return aaeEligDetermMessgRes; }
	 * 
	 *//**
		 * Method Name: calculateFinalEligDeterm Method Description:This method
		 * returns final eligibility determination value in form of
		 * AaeEligDetermMessgValueBean.This will perform the
		 * calculatePrelimEligDeterm eligibility determination and based in that
		 * will perform the final eligibility determination.
		 * 
		 * @param eligibilityDeterminationReq
		 * @return AaeEligDetermMessgRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/calculateFinalEligDeterm", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  AaeEligDetermMessgRes calculateFinalEligDeterm(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method calculateFinalEligDeterm in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } AaeEligDetermMessgRes aaeEligDetermMessgRes = new
	 * AaeEligDetermMessgRes();
	 * aaeEligDetermMessgRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId());
	 * aaeEligDetermMessgRes.setAaeEligDetermMessgDto(
	 * eligibilityDeterminationService
	 * .calculateFinalEligDeterm(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())); LOG.
	 * debug("Exiting method calculateFinalEligDeterm in EligibilityDeterminationController"
	 * ); return aaeEligDetermMessgRes; }
	 * 
	 *//**
		 * Method Name: fetchFinalEligDetermOutcome Method Description:This
		 * method returns the Final Eligibility determination Messages in form
		 * of AaeEligDetermMessgValueBean. Is used for display only.
		 * 
		 * @param eligibilityDeterminationReq
		 * @return AaeEligDetermMessgRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/fetchFinalEligDetermOutcome", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  AaeEligDetermMessgRes fetchFinalEligDetermOutcome(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method fetchFinalEligDetermOutcome in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } AaeEligDetermMessgRes aaeEligDetermMessgRes = new
	 * AaeEligDetermMessgRes();
	 * aaeEligDetermMessgRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId());
	 * aaeEligDetermMessgRes.setAaeEligDetermMessgDto(
	 * eligibilityDeterminationService
	 * .fetchFinalEligDetermOutcome(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())); LOG.
	 * debug("Exiting method fetchFinalEligDetermOutcome in EligibilityDeterminationController"
	 * ); return aaeEligDetermMessgRes; }
	 * 
	 *//**
		 * Method Name: validateSiblAppl Method Description:This method
		 * determines if the sibling selected is an applicable child or not.
		 * 
		 * @param eligibilityDeterminationFetchReq
		 * @return AaeEligDetermMessgRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/validateSiblAppl", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  AaeEligDetermMessgRes validateSiblAppl(
	 * 
	 * @RequestBody EligibilityDeterminationFetchReq
	 * eligibilityDeterminationFetchReq) { LOG.
	 * debug("Entering method validateSiblAppl in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.
	 * getIdSiblingApplPerson())) { throw new InvalidRequestException(
	 * messageSource.getMessage("IdSiblingApplPerson.mandatory", null,
	 * Locale.US)); } if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.getIdStage()
	 * )) { throw new InvalidRequestException(messageSource.getMessage(
	 * "common.stageid.mandatory", null, Locale.US)); } AaeEligDetermMessgRes
	 * aaeEligDetermMessgRes = new AaeEligDetermMessgRes();
	 * aaeEligDetermMessgRes.setTransactionId(eligibilityDeterminationFetchReq.
	 * getTransactionId()); LOG.info("TransactionId :" +
	 * eligibilityDeterminationFetchReq.getTransactionId());
	 * aaeEligDetermMessgRes.setMessageList(eligibilityDeterminationService.
	 * validateSiblAppl(
	 * eligibilityDeterminationFetchReq.getIdSiblingApplPerson(),
	 * eligibilityDeterminationFetchReq.getIdStage())); LOG.
	 * debug("Exiting method validateSiblAppl in EligibilityDeterminationController"
	 * ); return aaeEligDetermMessgRes; }
	 * 
	 *//**
		 * Method Name: validateFinalEligDetem Method Description:This method
		 * checks for validation errors. 1. Checks if the Date of consummation
		 * exists 2. Active ADO placement exists 3. Sibling is placed in same
		 * resource as the child
		 * 
		 * @param eligibilityDeterminationReq
		 * @return AaeEligDetermMessgRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/validateFinalEligDetem", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  AaeEligDetermMessgRes validateFinalEligDetem(
	 * 
	 * @RequestBody EligibilityDeterminationReq eligibilityDeterminationReq) {
	 * LOG.
	 * debug("Entering method validateFinalEligDetem in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())) { throw new InvalidRequestException(
	 * messageSource.getMessage("AaeApplAndDetermDBDto.mandatory", null,
	 * Locale.US)); } AaeEligDetermMessgRes aaeEligDetermMessgRes = new
	 * AaeEligDetermMessgRes();
	 * aaeEligDetermMessgRes.setTransactionId(eligibilityDeterminationReq.
	 * getTransactionId()); LOG.info("TransactionId :" +
	 * eligibilityDeterminationReq.getTransactionId());
	 * aaeEligDetermMessgRes.setMessageList(eligibilityDeterminationService
	 * .validateFinalEligDetem(eligibilityDeterminationReq.
	 * getAaeApplAndDetermDBDto())); LOG.
	 * debug("Exiting method validateFinalEligDetem in EligibilityDeterminationController"
	 * ); return aaeEligDetermMessgRes; }
	 * 
	 *//**
		 * Method Name: completeEligDetermAssignedTodo Method Description: This
		 * method completes Adoption Assistance Eligibility Determination Todo
		 * if any
		 * 
		 * @param eligibilityDeterminationFetchReq
		 * @return EligibilityDeterminationRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/completeEligDetermAssignedTodo", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationRes
	 * completeEligDetermAssignedTodo(
	 * 
	 * @RequestBody EligibilityDeterminationFetchReq
	 * eligibilityDeterminationFetchReq) { LOG.
	 * debug("Entering method completeEligDetermAssignedTodo in EligibilityDeterminationController"
	 * ); if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.getIdEvent()
	 * )) { throw new InvalidRequestException(messageSource.getMessage(
	 * "common.eventid.mandatory", null, Locale.US)); }
	 * EligibilityDeterminationRes eligibilityDeterminationRes = new
	 * EligibilityDeterminationRes();
	 * eligibilityDeterminationRes.setTransactionId(
	 * eligibilityDeterminationFetchReq.getTransactionId());
	 * eligibilityDeterminationRes.setUpdateResult(
	 * eligibilityDeterminationService
	 * .completeEligDetermAssignedTodo(eligibilityDeterminationFetchReq.
	 * getIdEvent())); LOG.info("TransactionId :" +
	 * eligibilityDeterminationFetchReq.getTransactionId()); LOG.
	 * debug("Exiting method completeEligDetermAssignedTodo in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationRes; }
	 * 
	 *//**
		 * Method Name: isStepparentInHomeOfRemoval Method
		 * Description:isStepparentInHomeOfRemoval
		 * 
		 * @param eligibilityDeterminationFetchReq
		 * @return EligibilityDeterminationDBRes
		 * 
		 * @
		 */
	/*
	 * @RequestMapping(value = "/isStepparentInHomeOfRemoval", headers = {
	 * "Accept=application/json" }, method = RequestMethod.POST)
	 * public  EligibilityDeterminationFceRes
	 * isStepparentInHomeOfRemoval(
	 * 
	 * @RequestBody EligibilityDeterminationFetchReq
	 * eligibilityDeterminationFetchReq) { LOG.
	 * debug("Entering method completeEligDetermAssignedTodo in EligibilityDeterminationController"
	 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.
	 * getIdFceEligibility())) { throw new
	 * InvalidRequestException(messageSource.getMessage(
	 * "IdFceEligibility.mandatory", null, Locale.US)); } if
	 * (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFetchReq.getIdStage()
	 * )) { throw new InvalidRequestException(messageSource.getMessage(
	 * "common.stageid.mandatory", null, Locale.US)); }
	 * 
	 * EligibilityDeterminationFceRes eligibilityDeterminationDBRes = new
	 * EligibilityDeterminationFceRes();
	 * eligibilityDeterminationDBRes.setTransactionId(
	 * eligibilityDeterminationFetchReq.getTransactionId());
	 * eligibilityDeterminationDBRes.setIndPersonHmRemoval(
	 * eligibilityDeterminationService.isStepparentInHomeOfRemoval(
	 * eligibilityDeterminationFetchReq.getIdFceEligibility(),
	 * eligibilityDeterminationFetchReq.getIdStage()));
	 * LOG.info("TransactionId :" +
	 * eligibilityDeterminationFetchReq.getTransactionId()); LOG.
	 * debug("Exiting method completeEligDetermAssignedTodo in EligibilityDeterminationController"
	 * ); return eligibilityDeterminationDBRes; }
	 * 
	 *//**
		 * Method Name: getFceContext Method Description: Returns the FceContext
		 * 
		 * @param eligibilityDeterminationFceReq
		 * @return EligibilityFceContextRes
		 * 
		 * @
		 *//*
		 * @RequestMapping(value = "/getFceContext", headers = {
		 * "Accept=application/json" }, method = RequestMethod.POST)
		 * public  EligibilityFceContextRes getFceContext(
		 * 
		 * @RequestBody EligibilityDeterminationFceReq
		 * eligibilityDeterminationFceReq) { LOG.
		 * debug("Entering method completeEligDetermAssignedTodo in EligibilityDeterminationController"
		 * ); if (TypeConvUtil.isNullOrEmpty(eligibilityDeterminationFceReq.
		 * getEligibilityDeterminationFceDto())) { throw new
		 * InvalidRequestException(
		 * messageSource.getMessage("EligibilityDeterminationDBDto.mandatory",
		 * null, Locale.US)); }
		 * 
		 * EligibilityFceContextRes eligibilityFceContextRes = new
		 * EligibilityFceContextRes();
		 * eligibilityFceContextRes.setTransactionId(
		 * eligibilityDeterminationFceReq.getTransactionId());
		 * eligibilityFceContextRes.setFceContextDto(
		 * eligibilityDeterminationService
		 * .getFceContext(eligibilityDeterminationFceReq.
		 * getEligibilityDeterminationFceDto())); LOG.info("TransactionId :" +
		 * eligibilityDeterminationFceReq.getTransactionId()); LOG.
		 * debug("Exiting method completeEligDetermAssignedTodo in EligibilityDeterminationController"
		 * ); return eligibilityFceContextRes; }
		 */
}
