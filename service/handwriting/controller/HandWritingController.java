package us.tx.state.dfps.service.handwriting.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.FetchHandWritingReq;
import us.tx.state.dfps.service.common.request.FetchHandWritingStageReq;
import us.tx.state.dfps.service.common.request.FetchLinkedNotesForStagesReq;
import us.tx.state.dfps.service.common.request.HWKeyEventReq;
import us.tx.state.dfps.service.common.request.HandWritingReq;
import us.tx.state.dfps.service.common.request.HandWrittenFieldViewedReq;
import us.tx.state.dfps.service.common.request.HandWrittenUsedReq;
import us.tx.state.dfps.service.common.response.FetchCaseIdForStageIdRes;
import us.tx.state.dfps.service.common.response.FetchHandWritingRes;
import us.tx.state.dfps.service.common.response.FetchStageInfoRes;
import us.tx.state.dfps.service.common.response.FetchStageRes;
import us.tx.state.dfps.service.common.response.HandWritingRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.handwriting.service.HandWritingService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:HandwritingController implementation for functions required for
 * implementing handwriting functionality June 19, 2018- 11:31:29 AM © 2018
 * Texas Department of Family and Protective Services
 */

@RestController
@RequestMapping("/handwriting")
public class HandWritingController {

	@Autowired
	HandWritingService handWritingService;

	@Autowired
	MessageSource messageSource;

	/**
	 * 
	 * Method Name: fetchCaseIdForStageId Method Description:This method fetches
	 * the Case Id for a Stage Id
	 * 
	 * @param fetchHandWritingStageReq
	 * @return FetchCaseIdForStageIdRes
	 */
	@RequestMapping(value = "/fetchCaseIdForStageId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FetchCaseIdForStageIdRes fetchCaseIdForStageId(
			@RequestBody FetchHandWritingStageReq fetchHandWritingStageReq) {
		FetchCaseIdForStageIdRes fetchCaseIdForStageIdRes = new FetchCaseIdForStageIdRes();
		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingStageReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchStageName.IdStage.mandatory", null, Locale.US));
		}
		fetchCaseIdForStageIdRes
				.setResult(handWritingService.fetchCaseIdForStageId(fetchHandWritingStageReq.getIdStage()));
		return fetchCaseIdForStageIdRes;
	}

	/**
	 * 
	 * Method Name: fetchStageInfo Method Description:This method fetches the
	 * stage information for the Handwriting signature
	 * 
	 * @param fetchHandWritingStageReq
	 * @return FetchStageInfoRes
	 */
	@RequestMapping(value = "/fetchStageInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FetchStageInfoRes fetchStageInfo(
			@RequestBody FetchHandWritingStageReq fetchHandWritingStageReq) {

		FetchStageInfoRes fetchStageInfoRes = new FetchStageInfoRes();
		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingStageReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchStageName.IdStage.mandatory", null, Locale.US));
		}
		fetchStageInfoRes.setResult(handWritingService.fetchStageInfo(fetchHandWritingStageReq.getIdStage()));
		return fetchStageInfoRes;
	}

	/**
	 * 
	 * Method Name: fetchStageName Method Description:This method fetches the
	 * stage name information
	 * 
	 * @param fetchHandWritingStageReq
	 * @return FetchStageRes
	 */
	@RequestMapping(value = "/fetchStageName", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FetchStageRes fetchStageName(@RequestBody FetchHandWritingStageReq fetchHandWritingStageReq) {

		FetchStageRes fetchStageRes = new FetchStageRes();
		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingStageReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchStageName.IdStage.mandatory", null, Locale.US));
		}
		fetchStageRes.setResult(handWritingService.fetchStageName(fetchHandWritingStageReq.getIdStage()));
		return fetchStageRes;
	}

	/**
	 * 
	 * Method Name: fetchLinkedNotesForStages Method Description:This method is
	 * called at the time of stage Check-in when we want to check if handwritten
	 * notes linked to pages exist.So we fetch only notes which are linked to
	 * pages
	 * 
	 * @param fetchLinkedNotesForStagesReq
	 * @return FetchHandWritingRes
	 */
	@RequestMapping(value = "/fetchLinkedNotesForStages", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FetchHandWritingRes fetchLinkedNotesForStages(
			@RequestBody FetchLinkedNotesForStagesReq fetchLinkedNotesForStagesReq) {

		FetchHandWritingRes fetchHandWritingRes = new FetchHandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(fetchLinkedNotesForStagesReq.getStageList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchLinkedNotesForStages.StageList.mandatory", null, Locale.US));
		}
		fetchHandWritingRes
				.setResults(handWritingService.fetchLinkedNotesForStages(fetchLinkedNotesForStagesReq.getStageList()));
		return fetchHandWritingRes;
	}

	/**
	 * 
	 * Method Name: fetchHandwrittenDataForEvent Method Description:This method
	 * fetches handwritten data for an event
	 * 
	 * @param fetchHandWritingStageReq
	 * @return FetchHandWritingRes
	 */
	@RequestMapping(value = "/fetchHandwrittenDataForEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FetchHandWritingRes fetchHandwrittenDataForEvent(
			@RequestBody FetchHandWritingStageReq fetchHandWritingStageReq) {

		FetchHandWritingRes fetchHandWritingRes = new FetchHandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingStageReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchHandwrittenDataForStage.IdEvent.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingStageReq.getBfetchImage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchHandwrittenDataForStage.BfetchImage.mandatory", null, Locale.US));
		}
		fetchHandWritingRes.setResults(handWritingService.fetchHandwrittenDataForEvent(
				fetchHandWritingStageReq.getIdEvent(), fetchHandWritingStageReq.getBfetchImage()));
		return fetchHandWritingRes;
	}

	/**
	 * 
	 * Method Name: fetchHandwrittenDataForKey Method Description:This method
	 * fetches handwritten data for a key value
	 * 
	 * @param fetchHandWritingStageReq
	 * @return FetchHandWritingRes
	 */
	@RequestMapping(value = "/fetchHandwrittenDataForKey", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FetchHandWritingRes fetchHandwrittenDataForKey(
			@RequestBody FetchHandWritingStageReq fetchHandWritingStageReq) {

		FetchHandWritingRes fetchHandWritingRes = new FetchHandWritingRes();

		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingStageReq.getDataKey())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchHandwrittenDataForStage.IdCase.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingStageReq.getBfetchImage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchHandwrittenDataForStage.BfetchImage.mandatory", null, Locale.US));
		}
		fetchHandWritingRes.setResults(handWritingService.fetchHandwrittenDataForKey(
				fetchHandWritingStageReq.getDataKey(), fetchHandWritingStageReq.getBfetchImage()));
		return fetchHandWritingRes;
	}

	/**
	 * 
	 * Method Name: updateHandWrittenData Method Description:This method is used
	 * to update Hand Written Data
	 * 
	 * @param handWritingReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/hwUpdate", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes updateHandWrittenData(@RequestBody HandWritingReq handWritingReq) {

		HandWritingRes handWritingRes = new HandWritingRes();
		handWritingRes = handWritingService.updateHandWrittenData(handWritingReq.getHandWritingDto());
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: fetchDetails Method Description:Method to get the
	 * handwritten details
	 * 
	 * @param handWritingReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/fetchDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes fetchDetails(@RequestBody HandWritingReq handWritingReq) {

		HandWritingRes handWritingRes = new HandWritingRes();
		handWritingRes = handWritingService.updateHandWrittenData(handWritingReq.getHandWritingDto());
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: fetchHwFieldListForForm Method Description:This method
	 * fetches handwriting fields for a form. These fields are configured in a
	 * table HANDWRITING_FIELDS. For normal pages, we dont maintain the list of
	 * handwritable fields in the table.This method fetches the handwritable
	 * fields and checks if handwritten data exist for that field, then loads
	 * information about handwritten data
	 * 
	 * @param fetchHandWritingReq
	 * @return FetchHandWritingRes
	 */
	@RequestMapping(value = "/fetchHwFieldListForForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FetchHandWritingRes fetchHwFieldListForForm(
			@RequestBody FetchHandWritingReq fetchHandWritingReq) {

		FetchHandWritingRes fetchHandWritingRes = new FetchHandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingReq.getSdocType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchHandWriting.sdocType.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(fetchHandWritingReq.getEventId())) {
			throw new InvalidRequestException(
					messageSource.getMessage("fetchHandWriting.eventId.mandatory", null, Locale.US));
		}
		fetchHandWritingRes.setResults(handWritingService.fetchHwFieldListForForm(fetchHandWritingReq.getSdocType(),
				fetchHandWritingReq.getEventId()));
		return fetchHandWritingRes;
	}

	/**
	 * 
	 * Method Name: updateHandwritingKeyAndEventWithNew Method Description:This
	 * method is used to update Hand Written data with new Key and Event In this
	 * case we update dynamically generated Key to an actual key
	 * 
	 * @param hwKeyEventReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/updateHWKeyEvt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes updateHandwritingKeyAndEventWithNew(@RequestBody HWKeyEventReq hwKeyEventReq) {
		HandWritingRes handWritingRes = new HandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(hwKeyEventReq.getHwKeyEventDto())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		}
		handWritingRes = handWritingService.updateHandwritingKeyAndEventWithNew(hwKeyEventReq.getHwKeyEventDto());
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: updateHandwritingKeyWithNew Method Description:Method used
	 * to call when a handwriting note is actually connected to a field. In this
	 * case we update dynamically generated Key to an actual key the field name
	 * can also be updated with new field name
	 * 
	 * @param hwKeyEventReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/updateHWKey", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes updateHandwritingKeyWithNew(@RequestBody HWKeyEventReq hwKeyEventReq) {
		HandWritingRes handWritingRes = new HandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(hwKeyEventReq.getHwKeyEventDto())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		}
		handWritingRes = handWritingService.updateHandwritingKeyWithNew(hwKeyEventReq.getHwKeyEventDto());
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: updateHandwrittenFieldViewed Method Description:This method
	 * is used to update Hand Written Data
	 * 
	 * @param fieldViewedReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/updateHWFld", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes updateHandwrittenFieldViewed(
			@RequestBody HandWrittenFieldViewedReq fieldViewedReq) {

		HandWritingRes handWritingRes = new HandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(fieldViewedReq)) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		}
		handWritingRes = handWritingService.updateHandwrittenFieldViewed(fieldViewedReq);
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: saveHandwrttenData Method Description:This method is used to
	 * update the cdNotesType and binTranslatedNotes for the given dataKey of
	 * HandWrittenData
	 * 
	 * @param HandWritingReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/saveHWriting", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes saveHandwrttenData(@RequestBody HandWritingReq hwDB) {

		HandWritingRes handWritingRes = new HandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(hwDB.getHandWritingDto())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		}
		handWritingRes = handWritingService.saveHandwrttenData(hwDB.getHandWritingDto());
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: deleteHandwrittenData Method Description:This method deletes
	 * the handwritten data for the given key
	 * 
	 * @param handWritingReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/deleteHWriting", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes deleteHandwrittenData(@RequestBody HandWritingReq handWritingReq) {

		HandWritingRes handWritingRes = new HandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(handWritingReq.getHandWritingDto())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		}
		handWritingRes = handWritingService.deleteHandwrittenData(handWritingReq.getHandWritingDto());
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: deleteHandwrittenDataForEvent Method Description:This method
	 * deletes all the handwritten data for a given event
	 * 
	 * @param handWritingReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/deleteHWritingEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes deleteHandwrittenDataForEvent(@RequestBody HandWritingReq handWritingReq) {
		HandWritingRes handWritingRes = new HandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(handWritingReq.getHandWritingDto())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		}
		handWritingRes = handWritingService.deleteHandwrittenDataForEvent(handWritingReq.getHandWritingDto());
		return handWritingRes;

	}

	/**
	 * 
	 * Method Name: deleteUsedHandwrittenData Method Description:This method
	 * deletes the handwritten data which has been translated and used This
	 * input parameter is comma separated list of Keys to be deleted
	 * 
	 * @param handWrittenUsedReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/deleteUsedHWriting", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  HandWritingRes deleteUsedHandwrittenData(@RequestBody HandWrittenUsedReq handWrittenUsedReq) {
		HandWritingRes handWritingRes = new HandWritingRes();
		if (TypeConvUtil.isNullOrEmpty(handWrittenUsedReq.getsKeyValue())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		}
		handWritingRes = handWritingService.deleteUsedHandwrittenData(handWrittenUsedReq.getsKeyValue());
		return handWritingRes;

	}
	
	@RequestMapping(value = "/updateFamilyPlanNarrToSignatures", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public HandWritingRes updateFamilyPlanNarrToSignatures(@RequestBody HandWritingReq handWritingReq) {
		handWritingService.updateFamilyPlanNarrToSignatures(handWritingReq.getIdEvent(), handWritingReq.getIdFamilyPlanNarr());
		HandWritingRes handWritingRes = new HandWritingRes();
		handWritingRes.setUpdateResult(1);
		return handWritingRes;
	}
}
