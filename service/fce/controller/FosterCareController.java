package us.tx.state.dfps.service.fce.controller;

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
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.FosterCareReviewReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EventUtilityRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.service.FosterCareReviewService;
import us.tx.state.dfps.service.fostercarereview.dto.FosterCareReviewDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Services
 * for Fc Review Page Feb 14, 2018- 2:43:43 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("/fosterCareReview")
public class FosterCareController {

	@Autowired
	private FosterCareReviewService fosterCareReviewService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(FosterCareController.class);

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010 Method Description:Return
	 * true if the foster care review was created on or after Oct 1st 2010 FCON
	 * change date for Extended foster care.
	 * 
	 * @param eventUtilityReq
	 * @return EventUtilityRes
	 */
	@RequestMapping(value = "/isFCReviewCreatedOnAfter1stOct2010", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventUtilityRes isFCReviewCreatedOnAfter1stOct2010(
			@RequestBody CommonEventIdReq commonEventIdReq) {
		log.debug("Entering method isFCReviewCreatedOnAfter1stOct2010 in FosterCareReviewController");
		if (TypeConvUtil.isNullOrEmpty(commonEventIdReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("eventUtilityReq.idEvent.mandatory", null, Locale.US));
		}
		EventUtilityRes eventUtilityRes = new EventUtilityRes();
		eventUtilityRes.setTransactionId(commonEventIdReq.getTransactionId());
		log.info("TransactionId :" + commonEventIdReq.getTransactionId());
		eventUtilityRes.setFosterExists(
				fosterCareReviewService.isFCReviewCreatedOnAfter1stOct2010(commonEventIdReq.getIdEvent()));
		log.debug("Exiting method isFCReviewCreatedOnAfter1stOct2010 in FosterCareReviewController");
		return eventUtilityRes;
	}

	/**
	 * Method Name: isFCReviewCreatedOnAfter1stOct2010ByIdFceReview Method
	 * Description: Return true if the foster care review was created on or
	 * after Oct 1st 2010 FCON change date for Extended foster care.
	 * 
	 * @param fosterCareReviewReq
	 * @return EventUtilityRes
	 */
	@RequestMapping(value = "/isFCReviewCreatedOnAfter1stOct2010ByIdFceReview", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventUtilityRes isFCReviewCreatedOnAfter1stOct2010ByIdFceReview(
			@RequestBody FosterCareReviewReq fosterCareReviewReq) {
		log.debug("Entering method isFCReviewCreatedOnAfter1stOct2010ByIdFceReview in FosterCareReviewController");
		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getIdFceReview())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fosterCareReviewReq.idFceReview.mandatory", null, Locale.US));
		}
		EventUtilityRes eventUtilityRes = new EventUtilityRes();
		eventUtilityRes.setTransactionId(fosterCareReviewReq.getTransactionId());
		log.info("TransactionId :" + fosterCareReviewReq.getTransactionId());
		eventUtilityRes.setFosterExistsById(fosterCareReviewService
				.isFCReviewCreatedOnAfter1stOct2010ByIdFceReview(fosterCareReviewReq.getIdFceReview()));
		log.debug("Exiting method isFCReviewCreatedOnAfter1stOct2010ByIdFceReview in FosterCareReviewController");
		return eventUtilityRes;
	}

	/**
	 * Method Name: isEntryLevelLegalStatusPresent Method Description: Check if
	 * Entry level Legal Status present for the child
	 * 
	 * @param fosterCareReviewReq
	 * @return EventUtilityRes
	 */
	@RequestMapping(value = "/isEntryLevelLegalStatusPresent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  EventUtilityRes isEntryLevelLegalStatusPresent(
			@RequestBody FosterCareReviewReq fosterCareReviewReq) {
		log.debug("Entering method isEntryLevelLegalStatusPresent in FosterCareReviewController");
		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getFosterCareReviewDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fosterCareReviewReq.fosterCareReviewDto.mandatory", null, Locale.US));
		}
		EventUtilityRes eventUtilityRes = new EventUtilityRes();
		eventUtilityRes.setTransactionId(fosterCareReviewReq.getTransactionId());
		log.info("TransactionId :" + fosterCareReviewReq.getTransactionId());
		eventUtilityRes.setLegalStatusPresent(
				fosterCareReviewService.isEntryLevelLegalStatusPresent(fosterCareReviewReq.getFosterCareReviewDto()));
		log.debug("Exiting method isEntryLevelLegalStatusPresent in FosterCareReviewController");
		return eventUtilityRes;
	}

	/**
	 * Method Name: save Method Description: save data in bean; change
	 * eventStatus to PROC if it's true
	 * 
	 * @param fosterCareReviewReq
	 */
	@RequestMapping(value = "/save", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void save(@RequestBody FosterCareReviewReq fosterCareReviewReq) {

		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getFosterCareReviewDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fosterCareReviewReq.fosterCareReviewDto.mandatory", null, Locale.US));
		}

		fosterCareReviewService.save(fosterCareReviewReq);
	}

	/**
	 * Method Name: submit Method Description:check to make sure the application
	 * is "complete" before creating a todo to give the eligibility specialist
	 * 
	 * @param fosterCareReviewReq
	 */
	@RequestMapping(value = "/submit", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void submit(@RequestBody FosterCareReviewReq fosterCareReviewReq) {

		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getFosterCareReviewDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fosterCareReviewReq.fosterCareReviewDto.mandatory", null, Locale.US));
		}
		fosterCareReviewService.submit(fosterCareReviewReq);
	}

	/**
	 * Method Name: updateSystemDerivedParentalDeprivation Method Description:
	 * This is executed in a separate transaction so we ensure we have all the
	 * latest information when we calculate system-derived parental deprivation
	 * 
	 * @param commonEventIdReq
	 */
	@RequestMapping(value = "/updateSystemDerivedParentalDeprivation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void updateSystemDerivedParentalDeprivation(@RequestBody CommonEventIdReq commonEventIdReq) {

		if (TypeConvUtil.isNullOrEmpty(commonEventIdReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		fosterCareReviewService.updateSystemDerivedParentalDeprivation(commonEventIdReq);
	}

	/**
	 * Method Name: closeEligibility Method Description: Prematurely close the
	 * eligibility
	 * 
	 * @param fosterCareReviewReq
	 */
	@RequestMapping(value = "/closeEligibility", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void closeEligibility(@RequestBody FosterCareReviewReq fosterCareReviewReq) {

		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getFosterCareReviewDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fosterCareReviewReq.fosterCareReviewDto.mandatory", null, Locale.US));
		}
		fosterCareReviewService.closeEligibility(fosterCareReviewReq);
	}

	/**
	 * Method Name: confirm Method Description: confirm the eligibility and
	 * close the review
	 * 
	 * @param fosterCareReviewReq
	 * @return
	 */
	@RequestMapping(value = "/confirm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes confirm(@RequestBody FosterCareReviewReq fosterCareReviewReq) {

		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getFosterCareReviewDto())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		return fosterCareReviewService.confirm(fosterCareReviewReq);
	}

	/**
	 * Method Name: readFosterCareReview Method Description: Read data for
	 * FosterCareReviewBean; sync data with the rest of the system
	 * 
	 * @param fosterCareReviewReq
	 * @return
	 */
	@RequestMapping(value = "/getFosterCareReview", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FosterCareReviewDto readFosterCareReview(
			@RequestBody FosterCareReviewReq fosterCareReviewReq) {

		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getIdLastUpdatePerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.lastUpdatePersonId.mandatory", null, Locale.US));
		}
		Boolean isNewUsing = !ObjectUtils.isEmpty(fosterCareReviewReq.getReqFuncCd())
				&& ServiceConstants.NEW_USING_STRING.equals(fosterCareReviewReq.getReqFuncCd());
		return fosterCareReviewService.read(fosterCareReviewReq.getIdStage(), fosterCareReviewReq.getIdEvent(),
				fosterCareReviewReq.getIdLastUpdatePerson(), isNewUsing);
	}

	/**
	 * Method Name: determineEligibility Method Description: calculate
	 * eligibility/reasons not eligible
	 * 
	 * @param fosterCareReviewReq
	 */
	@RequestMapping(value = "/determineEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public FosterCareReviewDto determineEligibility(@RequestBody FosterCareReviewReq fosterCareReviewReq) {

		if (TypeConvUtil.isNullOrEmpty(fosterCareReviewReq.getFosterCareReviewDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fosterCareReviewReq.fosterCareReviewDto.mandatory", null, Locale.US));
		}
		return fosterCareReviewService.determineEligibility(fosterCareReviewReq.getFosterCareReviewDto());
	}

	/**
	 * Method Name: enableFosterGoupMessage Method Description: This method
	 * tells weather to show fostercare message or not
	 * 
	 * @return
	 */
	@RequestMapping(value = "/enableFosterGoupMessage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes enableFosterGoupMessage() {

		return fosterCareReviewService.enableFosterGoupMessage();
	}
}
