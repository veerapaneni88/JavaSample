package us.tx.state.dfps.service.SDM.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentResponseDto;

/**
 * Dao Interface for functions required for implementing SDM Safety Assessment
 * functionality
 *
 */
public interface SDMSafetyAssessmentDao {

	/**
	 * Method to Delete all safety Assessment records from CpsSaFollowupResponse
	 * and add records in CpsSaResponse if there are safety assessment responses
	 * including follow ups
	 * 
	 * @param safetyAssessmentDB
	 * @return
	 */
	long deleteSafetyAssmtDetails(SDMSafetyAssessmentDto safetyAssessmentDB);

	/**
	 * Updates Safety Assessment in CPS_SA table.
	 * 
	 * @param safetyAssessmentDB
	 * @return
	 */
	long updateSDMSafetyAssessment(SDMSafetyAssessmentDto safetyAssessmentDB);

	/**
	 * Updates Safety Assessment Response record in the CPS_SA_RESPONSE table.
	 * 
	 * @param safetyAssessmentDB
	 * @return
	 */
	long updateSDMSafetyAssessmentResponse(SDMSafetyAssessmentResponseDto safetyAssessmentDB);

	/**
	 * Updates Safety Assessment Followup record in the CPS_SA_FOLLOWUP_RESPONSE
	 * table.
	 * 
	 * @param safetyAssessmentDB
	 * @return
	 */
	long updateFollowupResponse(SDMSafetyAssessmentFollowupDto safetyAssessmentDB);

	/**
	 * Inserts record into the Safety Assessment(CPS_SA) table.
	 * 
	 * @param safetyAssessmentDB
	 * @return @
	 */
	long addSafetyAssessment(SDMSafetyAssessmentDto safetyAssessmentDB);

	/**
	 * Deletes record from Event_Person_Link table.
	 * 
	 * @param tobeDeletedPersonId
	 * @param eventId
	 * @return
	 */
	long deleteEventPersonLink(Integer tobeDeletedPersonId, int eventId);

	/**
	 * Inserts record into the EVENT_PERSON_LINK table.
	 * 
	 * @param tobeAddedPersonId
	 * @param eventId
	 * @param caseId
	 * @return
	 */
	long addEventPersonLink(Integer tobeAddedPersonId, int eventId, int caseId);

	SDMSafetyAssessmentDto getSDMSafetyAssessment(Long idEvent, Long idStage);

	Boolean isSftAsmntInProcStatusAvail(Long idStage, String cdStage);

	Boolean isNewSftAsmntInProcStatusAvail(Long idStage, Long idPerson, String cdStage, Long idCpsSA);

	SDMSafetyAssessmentDto getQueryPageData(Long idStage, Boolean assmntWithHouseHold);

	String getAsmtTypHoHold(CommonHelperReq commonHelperReq, Long priorStageID);

	SDMSafetyAssessmentDto completeAssessment(SDMSafetyAssessmentDto safetyAssessmentDB, UserProfileDto userProfileDB);

	void undoCompleteAssessment(SDMSafetyAssessmentDto safetyAssessmentDB);

	/**
	 * 
	 * Method Name: getSafetyAssmentList Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	SDMSafetyAssessmentDto getSafetyAssmentList(Long idEvent);

	/**
	 * 
	 * Method Name: geteventPersonLink Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	List<Long> getEventPersonLink(Long idEvent);

	/**
	 * 
	 * Method Name: getSafetyAssmentType Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	String getSafetyAssmentType(Long idEvent);

	/**
	 * 
	 * Method Name: getSafetyAssmentResp Method Description: This method is used
	 * to retrieve the Safety Assessment Response by passing idCpsSa as input
	 * request.
	 * 
	 * @param idStage
	 * @return
	 */
	List<SDMSafetyAssessmentResponseDto> getSafetyAssmentResp(int idCpsSa);

	/**
	 * 
	 * Method Name: getSDMFollowUpQuestions Method Description: This method is
	 * used to retrieve the SDM FollowUp Questions by passing idCpsSa as input
	 * request.
	 * 
	 * @param idStage
	 * @return
	 */
	List<SDMSafetyAssessmentFollowupDto> getSDMFollowUpQuestions(int idCpsSa);

	/**
	 * Method Name: getPersonAssessedInSDMSafety Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	List<Long> getPersonAssessedInSDMSafety(Long idStage);
	
	/**
	 * 
	 * Method Name: getLatestSafetyAssessmentEvent Method Description:Gets
	 * latest safety assessment event id
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getLatestSafetyAssessmentEvent(Long idStage);
	/**
	 * 
	 *Method Name:	getLatestAssessmentDateInStage
	 *Method Description:This method is used to get the latest Assessment date.
	 *@param idStage
	 *@param idEvent
	 *@return
	 */
	public Date getLatestAssessmentDateInStage(Long idStage, Long idEvent);
	
	/**
	 * 
	 *Method Name:	getLatestAssessmentPersonAssessedList
	 *Method Description:This method is used to get the list of person Assessed 
	 *@param idStage
	 *@param idPerson
	 *@param idCpsRa
	 *@return
	 */
	public List<Long> getLatestAssessmentPersonAssessedList(Long idCase, Long idPerson,Long idEvent);
	/**
	 * 
	 *Method Name:	getAsmtTypHouseHoldForAssessedPerson
	 *Method Description:This method is used to check if some household is selected as a person assessed.
	 *@param idStage
	 *@param idCpsSa
	 *@param idPersonList
	 *@return
	 */
	public String getAsmtTypHouseHoldForAssessedPerson(Long idStage, Long idCpsSa,List<Long> idPersonList);

	/**
	 * Method Name: getChildrenCareGiverAssessedInSDMSafety Method Description: The method
	 * fetches the list of Persons assessed in SDM Safety Assessment
	 * for the Passed INV Stage.
	 * 
	 * @param idStage
	 * @return idPersonAssessedList
	 */
	public List<Long> getChildrenCareGiverAssessedInSDMSafety(Long idStage);
	
	public String checkAsmtTypHoHold(CommonHelperReq commonHelperReq, Long priorStageID);
}
