package us.tx.state.dfps.service.handwriting.service;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.HWKeyEventDto;
import us.tx.state.dfps.service.common.request.HandWrittenFieldViewedReq;
import us.tx.state.dfps.service.common.response.HandWritingRes;
import us.tx.state.dfps.service.handwriting.dto.HandWritingDto;
import us.tx.state.dfps.service.handwriting.dto.HandWritingValueDto;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * Service Interface for functions required for implementing handwriting
 * functionality
 *
 */
public interface HandWritingService {

	/**
	 * This method is used to update Hand Written Data.
	 * 
	 * @param handWritingDto
	 * @return HandWritingRes
	 */
	public HandWritingRes updateHandWrittenData(HandWritingDto handWritingDto);

	/**
	 * This method is used to update Hand Written data with new Key and Event
	 * 
	 * @param hwKeyEventDto
	 * @return HandWritingRes
	 */
	public HandWritingRes updateHandwritingKeyAndEventWithNew(HWKeyEventDto hwKeyEventDto);

	/**
	 * 
	 * Method Description: This method is used to Update the new handWrittenId
	 * and fieldName.
	 * 
	 * @param hwKeyEventDto
	 * @return HandWritingRes
	 */
	public HandWritingRes updateHandwritingKeyWithNew(HWKeyEventDto hwKeyEventDto);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param fieldViewedReq
	 * @return HandWritingRes
	 */
	public HandWritingRes updateHandwrittenFieldViewed(HandWrittenFieldViewedReq fieldViewedReq);

	/**
	 * 
	 * Method Description: This method is used to update the cdNotesType and
	 * binTranslatedNotes for the given dataKey.
	 * 
	 * @param handWritingDto
	 * @return HandWritingRes
	 */
	public HandWritingRes saveHandwrttenData(HandWritingDto handWritingDto);

	/**
	 * This method is used to delete Hand Written data for the corresponding
	 * dataKey
	 * 
	 * @param handWritingDto
	 * @return HandWritingRes
	 */
	public HandWritingRes deleteHandwrittenData(HandWritingDto handWritingDto);

	/**
	 * This method is used to delete all HandWrittendata for the corresponding
	 * EventId
	 * 
	 * @param handWritingDto
	 * @return HandWritingRes
	 */
	public HandWritingRes deleteHandwrittenDataForEvent(HandWritingDto handWritingDto);

	/**
	 * This method is used to delete Hand Written data for the corresponding
	 * keys which is in the form of String separated by commas
	 * 
	 * @param getsKeyValue
	 * @return HandWritingRes
	 */
	public HandWritingRes deleteUsedHandwrittenData(String getsKeyValue);

	/**
	 * Method Name: fetchHwFieldListForForm Method Description: This method
	 * fetches handwriting fields for a form. These fields are configured in a
	 * table HANDWRITING_FIELDS. For normal pages, we dont maintain the list of
	 * handwritable fields in the table. It is part of the page coding
	 * 
	 * This method fetches the handwritable fields and checks if handwritten
	 * data exist for that field, then loads information about handwritten data
	 * also
	 * 
	 * @param getsDocType
	 * @param eventId
	 * @return List<HandWritingValueDto>
	 */
	public List<HandWritingValueDto> fetchHwFieldListForForm(String getsDocType, Long eventId);


	/**
	 * Method Name: fetchHandwrittenDataForEvent Method Description: This method
	 * fetches handwritten data for an event
	 * 
	 * @param idEvent
	 * @param bfetchImage
	 * @return List<HandWritingValueDto>
	 */
	public List<HandWritingValueDto> fetchHandwrittenDataForEvent(Long idEvent, Boolean bfetchImage);

	/**
	 * Method Name: fetchHandwrittenDataForKey Method Description: This method
	 * fetches handwritten data for a key value
	 * 
	 * @param dataKey
	 * @param bfetchImage
	 * @return List<HandWritingValueDto>
	 */
	public List<HandWritingValueDto> fetchHandwrittenDataForKey(String dataKey, Boolean bfetchImage);

	/**
	 * Method Name: fetchLinkedNotesForStages Method Description: This method is
	 * called at the time of stage Check-in when we want to check if handwritten
	 * notes linked to pages exist. So we fetch only notes which are linked to
	 * pages
	 * 
	 * @param stageList
	 * @return List<HandWritingValueDto>
	 */
	public List<HandWritingValueDto> fetchLinkedNotesForStages(List<String> stageList);

	/**
	 * Method Name: fetchStageName Method Description: This method fetches the
	 * stage information
	 * 
	 * @param idStage
	 * @return String
	 */
	public String fetchStageName(Long idStage);

	/**
	 * Method Name: fetchStageInfo Method Description: This method fetches the
	 * stage information
	 * 
	 * @param idStage
	 * @return StageDBDto
	 */
	public StageDBDto fetchStageInfo(Long idStage);

	/**
	 * Method Name: fetchCaseIdForStageId Method Description: This method
	 * fetches the Case Id for a Stage Id
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long fetchCaseIdForStageId(Long idStage);

	public void updateFamilyPlanNarrToSignatures(Long idEvent, Long idFamilyPlanNarr);

}
