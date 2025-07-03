package us.tx.state.dfps.service.handwriting.dao;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.HWKeyEventDto;
import us.tx.state.dfps.service.handwriting.dto.HandWritingDto;
import us.tx.state.dfps.service.handwriting.dto.HandWritingValueDto;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * Dao Interface for functions required for implementing handwriting
 * functionality
 *
 */
public interface HandWritingDao {

	/**
	 * This method is used to update Hand Written Data.
	 * 
	 * @param handWritingDto
	 * @return long
	 */
	public long updateHandWrittenData(HandWritingDto handWritingDto);

	/**
	 * This method is used to update Hand Written data with new Key and Event
	 * 
	 * @param hwKeyEventDto
	 * @return long
	 */
	public long updateHandwritingKeyAndEventWithNew(HWKeyEventDto hwKeyEventDto);

	/**
	 * This method will update the handwrittenData with the new key and
	 * fieldName
	 * 
	 * @param hwKeyEventDto
	 * @return int
	 */
	public int updateHandwritingKeyWithNew(HWKeyEventDto hwKeyEventDto);

	/**
	 * This method will retrieve the
	 * 
	 * @param idWrittenData
	 * @return int
	 */
	public int updateHandwrittenFieldViewed(String idWrittenData);

	/**
	 * This method is used to update the cdNotesType and binTranslatedNotes for
	 * the given dataKey.
	 * 
	 * @param handWritingDto
	 * @return int
	 */
	public int saveHandwrttenData(HandWritingDto handWritingDto);

	/**
	 * This method is used to delete Hand Written data for the corresponding
	 * dataKey
	 * 
	 * @param handWritingDto
	 * @return long
	 */
	public long deleteHandwrittenData(HandWritingDto handWritingDto);

	/**
	 * This method is used to delete all HandWrittenData for the corresponding
	 * EventId
	 * 
	 * @param handWritingDto
	 * @return long
	 */
	public long deleteHandwrittenDataForEvent(HandWritingDto handWritingDto);

	/**
	 * This method is used to delete the list of Hand Written datas
	 * 
	 * @param string
	 * @return long
	 */
	public long deleteUsedHandwrittenData(String string);

	/**
	 * Method Name: fetchEventStageInfo Method Description: Fetches
	 * miscellaneous Event information and some stage information based on an
	 * Event
	 * 
	 * @param eventId
	 * @return HandWritingValueDto
	 */
	public HandWritingValueDto fetchEventStageInfo(Long eventId);

	/**
	 * Method Name: fetchHandwritableFieldList Method Description: This method
	 * fetches the list of fields on which handwriting need to be provided This
	 * method is used only for fetching handwritable fields in forms
	 * 
	 * @param sDocType
	 * @return List<HandWritingValueDto>
	 */
	public List<HandWritingValueDto> fetchHandwritableFieldList(String sDocType);

	/**
	 * Method Name: fetchHandwrittenDataForEvent Method Description: This method
	 * fetches handwritten data for an event
	 * 
	 * @param eventId
	 * @param bFetchImage
	 * @return List<HandWritingValueDto>
	 */
	public List<HandWritingValueDto> fetchHandwrittenDataForEvent(Long eventId, boolean bFetchImage);


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

	public long getEventforContact(HandWritingDto handWritingDto);

	public void updateFamilyPlanNarrToSignatures(Long idEvent, Long idFamilyPlanNarr);

}
