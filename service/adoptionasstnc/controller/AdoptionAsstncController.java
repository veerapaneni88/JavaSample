package us.tx.state.dfps.service.adoptionasstnc.controller;

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
import us.tx.state.dfps.service.adoptionasstnc.service.AdoptionAsstncService;
import us.tx.state.dfps.service.common.request.AdoptionAsstncEventReq;
import us.tx.state.dfps.service.common.request.AdoptionAsstncFetchReq;
import us.tx.state.dfps.service.common.request.AdoptionAsstncReq;
import us.tx.state.dfps.service.common.response.AdoptionAsstncFetchRes;
import us.tx.state.dfps.service.common.response.AdoptionAsstncRecordRes;
import us.tx.state.dfps.service.common.response.AdoptionAsstncRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * executes the method declared in the AdoptionAsstnc. Oct 30, 2017- 1:59:37 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/AdoptionAsstnc")
public class AdoptionAsstncController {

	@Autowired
	private AdoptionAsstncService adoptionAsstncService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-AdoptionAsstncControllerLog");

	/**
	 * Method Name: getNonRecurringAdoptionAsstncCeiling Method
	 * Description:Determines the maximum one-time payment amount for a
	 * non-recurring adoption assistance payment.
	 * 
	 * @return AdoptionAsstncRes
	 */
	@RequestMapping(value = "/getNonRecurringAdoptionAsstncCeiling", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRes getNonRecurringAdoptionAsstncCeiling() {
		LOG.debug("Entering method getNonRecurringAdoptionAsstncCeiling in AdoptionAsstncController");
		AdoptionAsstncRes adoptionAsstncRes = new AdoptionAsstncRes();
		adoptionAsstncRes.setAdoptionAsstncCeiling(adoptionAsstncService.getNonRecurringAdoptionAsstncCeiling());
		LOG.debug("Exiting method getNonRecurringAdoptionAsstncCeiling in AdoptionAsstncController");
		return adoptionAsstncRes;
	}

	/**
	 * Method Name: getRecurringAdoptionAsstncCeiling Method Description:
	 * Determines the monthly adoption assistance ceiling using the person id of
	 * the child being placed into adoption and the "effective" adoption
	 * assistance start date, which is either the start date of the adoption
	 * assistance record being added/updated or the start date of the earliest
	 * adoption assistance record for the person/resource combination, if others
	 * exist.
	 * 
	 * @param adoptionAsstncReq
	 * @return AdoptionAsstncRes
	 */
	@RequestMapping(value = "/getRecurringAdoptionAsstncCeiling", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRes getRecurringAdoptionAsstncCeiling(
			@RequestBody AdoptionAsstncReq adoptionAsstncReq) {
		LOG.debug("Entering method getRecurringAdoptionAsstncCeiling in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncReq.getAdoptionAsstncDto())) {
			throw new InvalidRequestException(messageSource.getMessage("AdoptionAsstncDto.mandatory", null, Locale.US));
		}
		AdoptionAsstncRes adoptionAsstncRes = new AdoptionAsstncRes();
		adoptionAsstncRes.setAdoptionAsstncCeiling(
				adoptionAsstncService.getRecurringAdoptionAsstncCeiling(adoptionAsstncReq.getAdoptionAsstncDto()));
		adoptionAsstncRes.setTransactionId(adoptionAsstncReq.getTransactionId());
		LOG.debug("Exiting method getRecurringAdoptionAsstncCeiling in AdoptionAsstncController");
		return adoptionAsstncRes;
	}

	/**
	 * Method Name: getValidationErrors Method Description:Validates the
	 * specified adoption assistance amount based upon the adoption assistance
	 * type and the "effective" adoption assistance start date, which is either
	 * the start date of the adoption assistance record being added/updated or
	 * the start date of the earliest adoption assistance record for the
	 * person/resource combination, if others exist.
	 * 
	 * @param adoptionAsstncReq
	 * @return AdoptionAsstncRes
	 */
	@RequestMapping(value = "/getValidationErrors", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRes getValidationErrors(@RequestBody AdoptionAsstncReq adoptionAsstncReq) {
		LOG.debug("Entering method getValidationErrors in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncReq.getAdoptionAsstncDto())) {
			throw new InvalidRequestException(messageSource.getMessage("AdoptionAsstncDto.mandatory", null, Locale.US));
		}
		AdoptionAsstncRes adoptionAsstncRes = new AdoptionAsstncRes();
		adoptionAsstncRes
				.setErrorMessage(adoptionAsstncService.getValidationErrors(adoptionAsstncReq.getAdoptionAsstncDto()));
		adoptionAsstncRes.setTransactionId(adoptionAsstncReq.getTransactionId());
		LOG.debug("Exiting method getValidationErrors in AdoptionAsstncController");
		return adoptionAsstncRes;
	}

	/**
	 * Method Name: getAlocWithGreatestStartDate Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) record with the greatest start date
	 * for the given person id.
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRes
	 */
	@RequestMapping(value = "/getAlocWithGreatestStartDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRes getAlocWithGreatestStartDate(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method getAlocWithGreatestStartDate in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		AdoptionAsstncRes adoptionAsstncRes = new AdoptionAsstncRes();
		adoptionAsstncRes.setAlocWithGreatestStartDate(
				adoptionAsstncService.getAlocWithGreatestStartDate(adoptionAsstncFetchReq.getPersonId()));
		adoptionAsstncRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method getAlocWithGreatestStartDate in AdoptionAsstncController");
		return adoptionAsstncRes;
	}

	/**
	 * Method Name: getPlacementWithGreatestStartDate Method
	 * Description:Retrieves the Placement record with the greatest start date
	 * for the given person id.
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRes
	 */
	@RequestMapping(value = "/getPlacementWithGreatestStartDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRes getPlacementWithGreatestStartDate(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method getPlacementWithGreatestStartDate in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getResourceId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("kinPlacementInfo.resourceId.mandatory", null, Locale.US));
		}
		AdoptionAsstncRes adoptionAsstncRes = new AdoptionAsstncRes();
		adoptionAsstncRes.setPlacementWithGreatestStartDate(adoptionAsstncService.getPlacementWithGreatestStartDate(
				adoptionAsstncFetchReq.getPersonId(), adoptionAsstncFetchReq.getResourceId()));
		adoptionAsstncRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method getPlacementWithGreatestStartDate in AdoptionAsstncController");
		return adoptionAsstncRes;
	}

	/**
	 * Method Name: adoptionPlacementDate Method Description: Retrieves the most
	 * recent ADO Placement start date for the given person id
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRes
	 */
	@RequestMapping(value = "/adoptionPlacementDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRes adoptionPlacementDate(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method adoptionPlacementDate in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		AdoptionAsstncRes adoptionAsstncRes = new AdoptionAsstncRes();
		adoptionAsstncRes.setAdoptionPlacementDate(
				adoptionAsstncService.adoptionPlacementDate(adoptionAsstncFetchReq.getPersonId()));
		adoptionAsstncRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method adoptionPlacementDate in AdoptionAsstncController");
		return adoptionAsstncRes;
	}

	/**
	 * Method Name: fetchLatestAdptAsstncRecord Method Description:Retrieves the
	 * Adoption Subsidy details
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncFetchRes
	 */
	@RequestMapping(value = "/fetchLatestAdptAsstncRecord", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncFetchRes fetchLatestAdptAsstncRecord(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method fetchLatestAdptAsstncRecord in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		AdoptionAsstncFetchRes adoptionAsstncFetchRes = new AdoptionAsstncFetchRes();
		adoptionAsstncFetchRes
				.setIdAdptSub(adoptionAsstncService.fetchLatestAdptAsstncRecord(adoptionAsstncFetchReq.getPersonId()));
		adoptionAsstncFetchRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method fetchLatestAdptAsstncRecord in AdoptionAsstncController");
		return adoptionAsstncFetchRes;
	}

	/**
	 * Method Name: fetchAdptAsstncDetail Method Description: Retrieves the
	 * Adoption Subsidy details
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncFetchRes
	 */
	@RequestMapping(value = "/fetchAdptAsstncDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncFetchRes fetchAdptAsstncDetail(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method fetchAdptAsstncDetail in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getIdAdptSub())) {
			throw new InvalidRequestException(messageSource.getMessage("IdAdptSub.mandatory", null, Locale.US));
		}
		AdoptionAsstncFetchRes adoptionAsstncFetchRes = new AdoptionAsstncFetchRes();
		adoptionAsstncFetchRes.setAdoptionAsstncDto(
				adoptionAsstncService.fetchAdptAsstncDetail(adoptionAsstncFetchReq.getIdAdptSub()));
		adoptionAsstncFetchRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method fetchAdptAsstncDetail in AdoptionAsstncController");
		return adoptionAsstncFetchRes;
	}

	/**
	 * Method Name: fetchAllAdptAsstncRecord Method Description:Fetches the all
	 * the adoption assistance record for the given person id if one exists.
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRecordRes
	 */
	@RequestMapping(value = "/fetchAllAdptAsstncRecord", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRecordRes fetchAllAdptAsstncRecord(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method fetchAllAdptAsstncRecord in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		AdoptionAsstncRecordRes adoptionAsstncRecordRes = new AdoptionAsstncRecordRes();
		adoptionAsstncRecordRes.setAdoptionAsstncDtoList(
				adoptionAsstncService.fetchAllAdptAsstncRecord(adoptionAsstncFetchReq.getPersonId()));
		adoptionAsstncRecordRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method fetchAllAdptAsstncRecord in AdoptionAsstncController");
		return adoptionAsstncRecordRes;
	}

	/**
	 * Method Name: isAdptAsstncCreatedPostAugRollout Method
	 * Description:Determines if the the Adoption Subsidy is created Pre/Post
	 * Aug 22 2010 rollout.
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncFetchRes
	 */
	@RequestMapping(value = "/isAdptAsstncCreatedPostAugRollout", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncFetchRes isAdptAsstncCreatedPostAugRollout(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method isAdptAsstncCreatedPostAugRollout in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getIdAdptSub())) {
			throw new InvalidRequestException(messageSource.getMessage("IdAdptSub.mandatory", null, Locale.US));
		}
		AdoptionAsstncFetchRes adoptionAsstncFetchRes = new AdoptionAsstncFetchRes();
		adoptionAsstncFetchRes.setIsCreatedPostRollOut(
				adoptionAsstncService.isAdptAsstncCreatedPostAugRollout(adoptionAsstncFetchReq.getIdAdptSub()));
		adoptionAsstncFetchRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method isAdptAsstncCreatedPostAugRollout in AdoptionAsstncController");
		return adoptionAsstncFetchRes;
	}

	/**
	 * Method Name: getRecentAdoptPlcmStartDate Method Description:Fetches the
	 * Adoptive Placement Start date for Resource and Child Combination if one
	 * exists.
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRes
	 */
	@RequestMapping(value = "/getRecentAdoptPlcmStartDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRes getRecentAdoptPlcmStartDate(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method getRecentAdoptPlcmStartDate in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getResourceId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("kinPlacementInfo.resourceId.mandatory", null, Locale.US));
		}
		AdoptionAsstncRes adoptionAsstncRes = new AdoptionAsstncRes();
		adoptionAsstncRes.setAdoptionPlacementDate(adoptionAsstncService.getRecentAdoptPlcmStartDate(
				adoptionAsstncFetchReq.getPersonId(), adoptionAsstncFetchReq.getResourceId()));
		adoptionAsstncRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method getRecentAdoptPlcmStartDate in AdoptionAsstncController");
		return adoptionAsstncRes;
	}

	/**
	 * Method Name: findEligibilityOrPrimayWorkerForStage Method
	 * Description:This function return if Eligibility Specialist that is
	 * assigned to stage. If None assigned returns Primary worker for Stage.
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRecordRes
	 */
	@RequestMapping(value = "/findEligibilityOrPrimayWorkerForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRecordRes findEligibilityOrPrimayWorkerForStage(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method findEligibilityOrPrimayWorkerForStage in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stafftodolist.ulIdStage.mandatory", null, Locale.US));
		}
		AdoptionAsstncRecordRes adoptionAsstncRecordRes = new AdoptionAsstncRecordRes();
		adoptionAsstncRecordRes.setIdWorker(
				adoptionAsstncService.findEligibilityOrPrimayWorkerForStage(adoptionAsstncFetchReq.getIdStage()));
		adoptionAsstncRecordRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method findEligibilityOrPrimayWorkerForStage in AdoptionAsstncController");
		return adoptionAsstncRecordRes;
	}

	/**
	 * Method Name: getAlocOnAdptAssistAgrmntSignDt Method Description:Retrieves
	 * the Authorized Level of Care (ALOC) on the day when Adoption Assist
	 * Agreement was Signed
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRecordRes
	 */
	@RequestMapping(value = "/getAlocOnAdptAssistAgrmntSignDt", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRecordRes getAlocOnAdptAssistAgrmntSignDt(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method getAlocOnAdptAssistAgrmntSignDt in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getDtAdptAsstAgreement())) {
			throw new InvalidRequestException(
					messageSource.getMessage("DtAdptAsstAgreement.mandatory", null, Locale.US));
		}
		AdoptionAsstncRecordRes adoptionAsstncRecordRes = new AdoptionAsstncRecordRes();
		adoptionAsstncRecordRes.setCdPLOCChild(adoptionAsstncService.getAlocOnAdptAssistAgrmntSignDt(
				adoptionAsstncFetchReq.getPersonId(), adoptionAsstncFetchReq.getDtAdptAsstAgreement()));
		adoptionAsstncRecordRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method getAlocOnAdptAssistAgrmntSignDt in AdoptionAsstncController");
		return adoptionAsstncRecordRes;
	}

	/**
	 * Method Name: isAdptSubsidyCreatedOnAfterAppl Method Description:Returns
	 * true if the Subsidy was created on or after the Adopt Assistance
	 * Application was created
	 * 
	 * @param adoptionAsstncEventReq
	 * @return AdoptionAsstncRecordRes
	 */
	@RequestMapping(value = "/isAdptSubsidyCreatedOnAfterAppl", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRecordRes isAdptSubsidyCreatedOnAfterAppl(
			@RequestBody AdoptionAsstncEventReq adoptionAsstncEventReq) {
		LOG.debug("Entering method isAdptSubsidyCreatedOnAfterAppl in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncEventReq.getIdEventSubsidy())) {
			throw new InvalidRequestException(messageSource.getMessage("IdEventSubsidy.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncEventReq.getIdEventApplication())) {
			throw new InvalidRequestException(
					messageSource.getMessage("IdEventApplication.mandatory", null, Locale.US));
		}
		AdoptionAsstncRecordRes adoptionAsstncRecordRes = new AdoptionAsstncRecordRes();
		adoptionAsstncRecordRes.setIsAdptSubsidyCreated(adoptionAsstncService.isAdptSubsidyCreatedOnAfterAppl(
				adoptionAsstncEventReq.getIdEventSubsidy(), adoptionAsstncEventReq.getIdEventApplication()));
		adoptionAsstncRecordRes.setTransactionId(adoptionAsstncEventReq.getTransactionId());
		LOG.debug("Exiting method isAdptSubsidyCreatedOnAfterAppl in AdoptionAsstncController");
		return adoptionAsstncRecordRes;
	}

	/**
	 * Method Name: isAdptSubsidyEnded Method Description:Returns false if any
	 * of the adoption subsidies(for that stage) is not ended.
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRecordRes
	 */
	@RequestMapping(value = "/isAdptSubsidyEnded", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRecordRes isAdptSubsidyEnded(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method adoptionPlacementDate in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("stafftodolist.ulIdStage.mandatory", null, Locale.US));
		}
		AdoptionAsstncRecordRes adoptionAsstncRecordRes = new AdoptionAsstncRecordRes();
		adoptionAsstncRecordRes
				.setIsAdptSubsidyEnded(adoptionAsstncService.isAdptSubsidyEnded(adoptionAsstncFetchReq.getIdStage()));
		adoptionAsstncRecordRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method adoptionPlacementDate in AdoptionAsstncController");
		return adoptionAsstncRecordRes;
	}

	/**
	 * Method Name: getAdptPlcmtInfo Method Description:Retrieves the Adoptive
	 * Placement informations
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncFetchRes
	 */
	@RequestMapping(value = "/getAdptPlcmtInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncFetchRes getAdptPlcmtInfo(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method getAdptPlcmtInfo in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getIdAdptSub())) {
			throw new InvalidRequestException(messageSource.getMessage("IdAdptSub.mandatory", null, Locale.US));
		}
		AdoptionAsstncFetchRes adoptionAsstncFetchRes = new AdoptionAsstncFetchRes();
		adoptionAsstncFetchRes
				.setAdoptionAsstncDto(adoptionAsstncService.getAdptPlcmtInfo(adoptionAsstncFetchReq.getIdAdptSub()));
		adoptionAsstncFetchRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method getAdptPlcmtInfo in AdoptionAsstncController");
		return adoptionAsstncFetchRes;
	}

	/**
	 * Method Name: fetchActiveAdpList Method Description:Get Person Active ADP
	 * Eligibilities for input person id
	 * 
	 * @param adoptionAsstncFetchReq
	 * @return AdoptionAsstncRecordRes
	 */
	@RequestMapping(value = "/fetchActiveAdpList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  AdoptionAsstncRecordRes fetchActiveAdpList(
			@RequestBody AdoptionAsstncFetchReq adoptionAsstncFetchReq) {
		LOG.debug("Entering method fetchActiveAdpList in AdoptionAsstncController");
		if (TypeConvUtil.isNullOrEmpty(adoptionAsstncFetchReq.getPersonId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		AdoptionAsstncRecordRes adoptionAsstncRecordRes = new AdoptionAsstncRecordRes();
		adoptionAsstncRecordRes.setAdoptionAsstncDtoList(
				adoptionAsstncService.fetchActiveAdpList(adoptionAsstncFetchReq.getPersonId()));
		adoptionAsstncRecordRes.setTransactionId(adoptionAsstncFetchReq.getTransactionId());
		LOG.debug("Exiting method fetchActiveAdpList in AdoptionAsstncController");
		return adoptionAsstncRecordRes;
	}
}
