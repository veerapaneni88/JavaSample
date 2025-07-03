package us.tx.state.dfps.service.casemanagement.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casemanagement.service.ArHelperService;
import us.tx.state.dfps.service.common.request.ArInvCnclsnReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CriminalHistoryReq;
import us.tx.state.dfps.service.common.response.ArHelperRes;
import us.tx.state.dfps.service.common.response.ArInvCnclsnRes;
import us.tx.state.dfps.service.common.response.ArValidationRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CriminalHistoryRes;
import us.tx.state.dfps.service.common.response.PersonDtlRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Service Name: ArHelper
 * Description: Helper methods for AR Conclusion page load and save Jun 30, 2017
 * - 1:17:59 PM
 * 
 * @author PONNAB
 *
 */

@RestController
@RequestMapping("/arhelper")
public class ArHelperController {

	@Autowired
	ArHelperService arHelperService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventService eventService;

	private static final Logger log = Logger.getLogger(ArHelperController.class);

	/**
	 * Method Description: Method to check if risk is indicated.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/isriskindicated", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes searchUnitListDtls(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isRiskIndicated(commonHelperReq.getIdCase());
	}

	/**
	 * Method Description: Method to check if approval has been submitted.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/hasbeensubmittedforapproval", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes hasBeenSubmittedForApproval(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.hasBeenSubmittedForApproval(commonHelperReq.getIdEvent());

	}

	/**
	 * Method Description: Method to check if legal action exists.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/islegalactionexists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isLegalActionExists(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isLegalActionExists(commonHelperReq.getIdCase());
	}

	/**
	 * Method Description: Method to get contact purpose.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/getContactPurposeFamAssmtRScheduling", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes getContactPurposeFamAssmtRScheduling(
			@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.getContactPurposeFamAssmtRScheduling(commonHelperReq.getIdCase());
	}

	/**
	 * Method Description: Method to check if legal action is pending.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */

	@RequestMapping(value = "/isLegalActionPending", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isLegalActionPending(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isLegalActionPending(commonHelperReq.getIdStage());
	}

	/**
	 * Method Description: Method to check if initial safety assessment is
	 * rejected or pending.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */

	@RequestMapping(value = "/isInitialSftyAssmntRejectedOrPending", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isInitialSftyAssmntRejectedOrPending(
			@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isInitialSftyAssmntRejectedOrPending(commonHelperReq.getIdStage());
	}

	/**
	 * Method Description: Method to check if pending day care approval exists.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */

	@RequestMapping(value = "/isPendingDayCareApprvalExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isPendingDayCareApprvalExists(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isPendingDayCareApprvalExists(commonHelperReq.getIdStage());
	}

	/**
	 * Method Description: Method to check if pending service authorization
	 * approval exists.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/isPendingSeviceAuthApprvalExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isPendingSeviceAuthApprvalExists(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isPendingSeviceAuthApprvalExists(commonHelperReq.getIdStage());
	}

	/**
	 * Method Description: Method to check if Extension request pending.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/isExtensionRequestPending", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isExtensionRequestPending(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isExtensionRequestPending(commonHelperReq.getIdStage());
	}

	/**
	 * Method Description: Method to check if more reference child exist.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/isMoreReferenceChildExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isMoreReferenceChildExists(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isMoreReferenceChildExists(commonHelperReq.getIdStage());
	}

	/**
	 * Method Description: Method to get safety assessment info.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/getSafetyAssessmentInfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes getSafetyAssessmentInfo(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isRiskIndicated(commonHelperReq.getIdCase());
	}

	/**
	 * Method Description: Method to get event by stage and event type.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */

	@RequestMapping(value = "/getEventByStageAndEventType", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes getEventByStageAndEventType(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.getEventByStageAndEventType(commonHelperReq.getIdStage(),
				commonHelperReq.getEventType());
	}

	/**
	 * Method Description: Method to check get person characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/getPersonCharacteristics", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PersonDtlRes getPersonCharacteristics(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.getPersonCharacteristics(commonHelperReq.getIdStage());
	}

	/**
	 * Method Description: Method to check get person characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/isPendingIntlSafetyAssmtApprvalExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isPendingIntlSafetyAssmtApprvalExists(
			@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.isPendingIntlSafetyAssmtApprvalExists(commonHelperReq.getIdStage());

	}

	/**
	 * Method Description: Method to check get person characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/getOutcome", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes getOutcome(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.getOutcome(commonHelperReq.getIdStage());

	}

	/**
	 * Method Description: Method to check get idAssessmentHousehold
	 * characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/getIdAssessmentHousehold", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes getIdAssessmentHousehold(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.getIdAssessmentHousehold(commonHelperReq.getIdEvent());

	}

	/**
	 * Method Description: Method to check get idAssessmentHousehold
	 * characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/updIdAssessmentHousehold", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes updIdAssessmentHousehold(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.updIdAssessmentHousehold(commonHelperReq);

	}

	/**
	 * Method Description: Method to get ArConclusionDetail characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/getArConclusionDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArInvCnclsnRes getArConclusionDetail(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.getArInvConclusionDetail(commonHelperReq);
	}

	/**
	 * Method Description: Method to save ArConclusionDetail characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/saveArConclusionDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArInvCnclsnRes saveArConclusionDetail(@RequestBody ArInvCnclsnReq arInvCnclsnReq) {

		log.info("TransactionId :" + arInvCnclsnReq.getTransactionId());
		return arHelperService.saveArInvConclusionDetail(arInvCnclsnReq);
	}

	/**
	 * Method Description: Method to save Validate submit.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/validateArSubmit", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ArValidationRes validateArSubmit(@RequestBody CommonHelperReq commonHelperReq) {

		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return arHelperService.getArInvConclusionValidation(commonHelperReq);
	}

	/**
	 * Method Description: Method to to PrintClosure Notification checked
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 * 
	 * @
	 */
	@RequestMapping(value = "/getPrintNotificationFlag", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getPrintNotificationFlag(@RequestBody CommonHelperReq commonHelperReq) {

		return arHelperService.getPrintNotificationFlag(commonHelperReq);
	}

	/**
	 * Method Description: Method to to get Actual conclusion Event ID
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 * 
	 * @
	 */
	@RequestMapping(value = "/getConclusionEventId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getConclusionEventId(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getSzCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.taskCode.mandatory", null, Locale.US));
		}

		return arHelperService.getConclusionEventId(commonHelperReq);
	}

	/**
	 * Method Name: checkCrimHistPerson Method Description: This method returns
	 * true if the criminal history action is null, else will return false
	 * 
	 * @param InvalidRequestException
	 */
	@RequestMapping(value = "/checkCrimHistPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CriminalHistoryRes checkCrimHistPerson(@RequestBody CriminalHistoryReq criminalHistoryReq) {
		if (TypeConvUtil.isNullOrEmpty(criminalHistoryReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource
					.getMessage("criminalHistoryReq.checkCrimHistPerson.getIdPerson.mandatory", null, Locale.US));
		}
		CriminalHistoryRes criminalHistoryRes = new CriminalHistoryRes();
		criminalHistoryRes.setCriminalHistoryPersonCheckResult(
				arHelperService.checkCrimHistPerson(criminalHistoryReq.getIdPerson()));
		return criminalHistoryRes;

	}

	/**
	 * Method Name: selectARConclusion Method Description: This method returns
	 * the AR Conclusion Details
	 * 
	 * @param ArInvCnclsnReq
	 * @return ArInvCnclsnRes
	 */
	@RequestMapping(value = "/selectARConclusion", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ArInvCnclsnRes selectARConclusion(@RequestBody ArInvCnclsnReq arInvCnclsnReq) {

		ArInvCnclsnRes arInvCnclsnRes = new ArInvCnclsnRes();
		if (!TypeConvUtil.isNullOrEmpty(arInvCnclsnReq.getIdStage())) {
			arInvCnclsnRes.setArInvCnclsnDto(arHelperService.selectARConclusion(arInvCnclsnReq.getIdStage()));
		} else if (!TypeConvUtil.isNullOrEmpty(arInvCnclsnReq.getArInvCnclsnDto().getIdStage())) {
			arInvCnclsnRes.setArInvCnclsnDto(
					arHelperService.selectARConclusion(arInvCnclsnReq.getArInvCnclsnDto().getIdStage()));
		} else {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return arInvCnclsnRes;
	}

	/**
	 * Method Name: updateARConclusion Method Description: This method updates
	 * the ARConclusion Details Updates/Inserts AREaEligibility Details
	 * 
	 * @param ArInvCnclsnReq
	 * @return Long
	 */
	@RequestMapping(value = "/updateARConclusion", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ArInvCnclsnRes updateARConclusion(@RequestBody ArInvCnclsnReq arInvCnclsnReq) {
		ArInvCnclsnRes arInvCnclsnRes = new ArInvCnclsnRes();
		if (!TypeConvUtil.isNullOrEmpty(arInvCnclsnReq.getArInvCnclsnDto())) {
			arHelperService.updateARConclusion(arInvCnclsnReq.getArInvCnclsnDto());
		}
		arInvCnclsnRes.setArInvCnclsnDto(arInvCnclsnReq.getArInvCnclsnDto());
		return arInvCnclsnRes;
	}

	/**
	 * Method Name: getArEaEligibilityDetails Method Description: This method
	 * retrieves the ArEaEligibilityDetails
	 * 
	 * @param ArInvCnclsnReq
	 * @return Long
	 */

	@RequestMapping(value = "/getArEaEligibilityDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArInvCnclsnRes getArEaEligibilityDetails(@RequestBody ArInvCnclsnReq arInvCnclsnReq) {

		ArInvCnclsnRes arInvCnclsnRes = new ArInvCnclsnRes();
		if (!TypeConvUtil.isNullOrEmpty(arInvCnclsnReq.getArInvCnclsnDto())) {

			if (TypeConvUtil.isNullOrEmpty(arInvCnclsnReq.getArInvCnclsnDto().getIdEvent())) {
				throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
			}
			arInvCnclsnRes
					.setArInvCnclsnDto(arHelperService.getArEaEligibilityDetails(arInvCnclsnReq.getArInvCnclsnDto()));

		}

		return arInvCnclsnRes;
	}

	/**
	 * Method Name: invalidateApprovalStatus Method Description: Retrieve the
	 * Approval Id's and Delete the To-Do/Approval Event
	 * 
	 * @param ArInvCnclsnReq
	 */

	@RequestMapping(value = "/invalidateAppralStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArInvCnclsnRes invalidateAppralStatus(@RequestBody ArInvCnclsnReq arInvCnclsnReq) {

		ArInvCnclsnRes arInvCnclsnRes = new ArInvCnclsnRes();
		if (!TypeConvUtil.isNullOrEmpty(arInvCnclsnReq.getArInvCnclsnDto())) {

			if (TypeConvUtil.isNullOrEmpty(arInvCnclsnReq.getArInvCnclsnDto().getIdEvent())) {
				throw new InvalidRequestException(
						messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
			}
			arHelperService.invalidateApprovalStatus(arInvCnclsnReq.getArInvCnclsnDto());

		}

		arInvCnclsnRes.setArInvCnclsnDto(arInvCnclsnReq.getArInvCnclsnDto());
		return arInvCnclsnRes;
	}
	
	
	
	@RequestMapping(value = "/validationForSexualVctmizationQues", headers = {
    "Accept=application/json" }, method = RequestMethod.POST)
    public  CommonHelperRes validationForSexualVctmizationQues(@RequestBody CommonHelperReq commonHelperReq) {
    
	    CommonHelperRes response =new CommonHelperRes();
	    boolean result=arHelperService.validationForSexualVctmizationQues(commonHelperReq.getIdStage());
	    response.setResult(result);
	    return response;
    }

	/**
	 * Method Name: isFbssReferralApproved
	 * Method Desc: Checks with the FBSS Referral is approved in the case if idHouseHoldPerson not null then
	 * selected for house hold else for any house hold
	 *
	 * @param commonHelperReq
	 * @return
	 */
	// artf156988  FR 24.11.4.28 Validate Approved FBSS Referral
	@RequestMapping(value = "/isFbssReferralApproved", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ArHelperRes isFbssReferralApproved(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}

		ArHelperRes arHelperRes = new ArHelperRes();
		arHelperRes.setEventDtoList(arHelperService.isFbssReferralApproved(commonHelperReq.getIdCase(),commonHelperReq.getTaskCodes()));
		return arHelperRes;
	}

}
