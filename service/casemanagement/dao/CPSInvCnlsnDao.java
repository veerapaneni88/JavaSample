package us.tx.state.dfps.service.casemanagement.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.tx.state.dfps.common.domain.Allegation;
import us.tx.state.dfps.common.domain.SubstanceTracker;
import us.tx.state.dfps.service.common.request.CpsInvNoticesClosureReq;
import us.tx.state.dfps.service.common.response.CpsInvNoticesRes;
import us.tx.state.dfps.service.common.response.CpsInvSubstancesRes;

public interface CPSInvCnlsnDao {


	/**
	 *
	 * Method Description:This Method is used to save/update the information of all
	 * the substances for a given person ID and stage ID.
	 *
	 * @param List<SubstanceTracker>
	 * @param String operationName - this can be Insert, Update and Delete
	 * @return @CpsInvSubstancesRes
	 */
	public CpsInvSubstancesRes updateSubstanceTracker(List<SubstanceTracker> substanceTrackerList, String operation);


	/**
	 *
	 * Method Description:This Method is used to retireve the information of all
	 * 	 * the substances for a given person ID and stage ID from the Substance_Tracker table
	 *
	 * @param Long idPerson
	 * @param Long idStage
	 * @param String isParent
	 * @return List<SubstanceTracker></SubstanceTracker>
	 */
	public List<SubstanceTracker> getExsitingSubstanceTrackersbyStageIDPersonID(Long idPerson, Long idStage, String isParent);


	/**
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId.
	 * 
	 * @param stageId
	 * @param stageProgram
	 * @param CommonHelperReq(eventId)
	 */

	CpsInvNoticesRes getClosureNotices(Long eventId, Long stageId, String stageProgram);

	/**
	 * 
	 * Method Description: This method is used to save/update/delete the
	 * information of closure notices.
	 * 
	 * @param cpsInvNoticesClosureReq
	 * @return CpsInvNoticesRes(successMessage String)
	 */
	CpsInvNoticesRes saveClosureNotices(CpsInvNoticesClosureReq cpsInvNoticesClosureReq);

	Long updateChildSexLaborTrafficking(Long idCase);

	/**
	 * Method Description:
	 * 
	 * @param idEvent
	 * @param idPerson
	 * @return
	 */
	Boolean getSchoolPersonnelInitialRole(Long idEvent, Long idPerson);

	/**
	 * 
	 * Method Name: getCompletedAssementCount Method Description: This method is
	 * used to check if completed assessments exists after release date .
	 * 
	 * @param idCase
	 * @return
	 */
	Boolean getCompletedAssessmentCount(Long idCase);

	/**
	 * Method Name: savePrintPerson Method Description: Method Saves the
	 * Selected Printer Person against the Conclusion Event ID.
	 * 
	 * @param idEvent
	 * @param idPrintPerson
	 * @param idApprover
	 * @return
	 */
	Boolean savePrintPerson(Long idEvent, Long idPrintPerson, Long idApprover);

	/**
	 * Method Name: getPrintPerson Method Description: Method fetches the Saved
	 * Print Person for Conclusion Event.
	 * 
	 * @param idEvent
	 * @param idApprover
	 * @return
	 */
	Long getPrintPerson(Long idEvent, Long idApprover);

	/**
	 * Method Name: getPersonIdInFDTC Method Description:The method returns the
	 * list of person id/name that have legal actions of type FDTC for a given
	 * case
	 * 
	 * @param caseId
	 * @return HashMap
	 * 
	 */
	public HashMap<Long, String> getPersonIdInFDTC(Long caseId);

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description:Returns the most
	 * recent FDTC Subtype and Outcome date for a Person in a given case id
	 * 
	 * @param personId
	 * @return HashMap
	 */
	public HashMap<String, String> getMostRecentFDTCSubtype(Long personId);

	/**
	 * Method Name: fetchAllegQuestionAnswers Method Description:This method
	 * gets ID, DOB, and DOD for victim on an allegation.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean fetchAllegQuestionAnswers(Long idStage);

	/**
	 * Method Name: fetchAllegQuestionYAnswers Method Description:This method
	 * returns if all questions have been answered.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean fetchAllegQuestionYAnswers(Long idStage);

	/**
	 * Method Name: validateDispAndCFQuestion Method Description:This method
	 * returns true if all the answers to the question match the severity.
	 * 
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean validateDispAndCFQuestion(Long idStage);

	/**
	 * Method Name: validateRlqshCstdyAndPrsnCharQuestion Method
	 * Description:This method checks the relinquish custody question and person
	 * char for SB44.
	 * 
	 * @param idStage
	 * @return List<Long>
	 */
	public List<Long> validateRlqshCstdyAndPrsnCharQuestion(Long idStage);

	/**
	 * Method Name: getRlnqshAnswrdYVictimIds Method Description: This method
	 * returns list of victimIds whose relinquish question is answered yes.
	 * 
	 * @param idStage
	 * @return List<Long>
	 */
	public List<Long> getRlnqshAnswrdYVictimIds(Long idStage);

	/**
	 * Method Name: isEventStatusNew Method Description:This method returns TRUE
	 * if there is at least one event status as 'NEW'
	 * 
	 * @param idCase
	 * @return Boolean
	 */
	public Boolean isEventStatusNew(Long idCase);

	/**
	 * Method Name: isChildSexLaborTrafficking Method Description:This method
	 * returns TRUE if the case has answered Child Sex/Labor Trafficking
	 * question in the current stage or there is Allegation of Child Sex/Labor
	 * Trafficking
	 * 
	 * @param idCase
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean isChildSexLaborTrafficking(Long idCase, Long idStage);

	/**
	 * Method Name: rlnqushQuestionAnsweredY Method Description:This method
	 * returns true if atleast one of the relinquish allegation cstdy is
	 * answered yes.
	 * 
	 * @param idStage
	 * @param ulIdVictim
	 * @return Boolean
	 */
	public Boolean rlnqushQuestionAnsweredY(Long idStage, Long ulIdVictim);

	/**
	 * Method Name: isRlnqshAnswrd Method Description:This method returns true
	 * if relinquish question is not answered.
	 * 
	 * @param idStage
	 * @param idCase
	 * @return Boolean
	 */
	public Boolean isRlnqshAnswrd(Long idStage, Long idCase);

	/**
	 * Method Name: isDispositionMissing Method Description:Retrieve true if any
	 * of allegation(s) for the input stage is missing a disposition
	 * 
	 * @param stageId
	 * @return Boolean
	 */
	public Boolean isDispositionMissing(Long stageId);

	/**
	 * Method Name: getInitialSDMSafetyAssmntCount Method Description:Returns
	 * the initial safety assessment count.
	 * 
	 * @param stageId
	 * @return Long
	 */
	public Long getInitialSDMSafetyAssmntCount(Long stageId);

	/**
	 * Method Name: getPriorARInitialSafetyAssmntCount Method Description:
	 * Returns the AR initial Safety Assessment count.
	 * 
	 * @param stageId
	 * @return Long
	 */
	public Long getPriorARInitialSafetyAssmntCount(Long stageId);
	
	/**
	 * Method Name: getPriorARInitialSafetyAssmntCountNew Method Description:
	 * Returns the AR initial Safety Assessment count for new assessments.
	 * 
	 * @param stageId
	 * @return Long
	 */
	public Long getPriorARInitialSafetyAssmntCountNew(Long stageId);

	/**
	 * Method Name: getInCompSDMSafetyAssmntCount Method Description: Returns
	 * the Incomplete Safety Sssessment count.
	 * 
	 * @param stageId
	 * @return Long
	 */
	public Long getInCompSDMSafetyAssmntCount(Long stageId);

	/**
	 * Method Name: getRemovalRecordCount Method Description: Returns count of
	 * removal indicated on the Safety Assessment.
	 * 
	 * @param stageId
	 * @return Long
	 */
	public Long getRemovalRecordCount(Long stageId);

	/**
	 * Method Name: getPcspCount Method Description:Returns count of pcsp
	 * indicated on the Safety Assessment.
	 * 
	 * @param stageId
	 * @return Long
	 */
	public Long getPcspCount(Long stageId);

	/**
	 * Method Name: getSafetyDecisionAndIdCps Method Description:Returns safety
	 * decision indicated on the Safety Assessment.
	 * 
	 * @param stageId
	 * @return Map<Long, String>
	 */
	public Map<Long, String> getSafetyDecisionAndIdCps(Long stageId);

	/**
	 * Method Name: pcspSelectedCount Method Description:Returns safety decision
	 * indicated on the Safety Assessment.
	 * 
	 * @param cpsSaId
	 * @return Long
	 */
	public Long pcspSelectedCount(Long cpsSaId);

	/**
	 * Method Name: cspInterventionCount Method Description:Returns safety
	 * decision indicated on the Safety Assessment.
	 * 
	 * @param stageId
	 * @return Long
	 */
	public Long cspInterventionCount(Long stageId);

	/**
	 * Method Name: sdmRiskAssmComp Method Description:Method to check if the
	 * Risk Assessment for the stage is completed
	 * 
	 * @param stageId
	 * @return Boolean
	 */
	public Boolean sdmRiskAssmComp(Long stageId);

	/**
	 * Method Name: getSevenDaySafetyAssmntCount
	 * 
	 * Method Description:Method to check if the Risk Assessment for the stage
	 * is completed
	 * 
	 * @param stageId
	 * @return Boolean
	 */
	public Long getSevenDaySafetyAssmntCount(Long stageId);

	/**
	 * Method Name: getSafetyDecisionAndIdCpsForHoushold Method Description:
	 * 
	 * @param stageId
	 * @param idHshldPrsn
	 * @return
	 */
	Map<Long, String> getSafetyDecisionAndIdCpsForHoushold(Long stageId, Long idHshldPrsn);

	/**
	 * Method Name: getPcspCountForHousehold Method Description:
	 * 
	 * @param stageId
	 * @return
	 */
	Long getPcspCountForCase(Long stageId, Long idCase);

	/**
	 * Method Name: getRemovalRecordCountForHousehold Method Description:
	 * 
	 * @param stageId
	 * @param idHshldPrsn
	 * @return
	 */
	Long getRemovalRecordCountForHousehold(Long stageId, Long idHshldPrsn);

	/**
	 * Method Name: sdmRiskAssmCompForHousehold Method Description: Method to
	 * check if the Risk Assessment for the stage is completed for the Household
	 * selected in Investigation Conclusion page.
	 * 
	 * @param stageId
	 * @param idHshldPrsn
	 * @return Boolean
	 */
	Boolean sdmRiskAssmCompForHousehold(Long stageId, Long idHshldPrsn);

	/**
	 * Method Name: checkTrfckngRecPend Method Description: Method to check if
	 * the Trafficking Record exists for Allegation in RTB/UTD.
	 * 
	 * @param idStage
	 * @return
	 */
	Allegation checkTrfckngRecPend(Long idStage);

	List<Allegation> checkTrfckngRecPendList(Long idStage);


	/**
	 * Method Name: checkSxHistIndForAllegs
	 * Method Description: This method will check if there is an SVH incident
	 * recorded for PRN(s) if any allegation(cd_alleg_disposition = 'RTB' and cd_alleg_type = 'SXAB') exists
	 * @param idStage
	 * @return boolean
	 */
  public boolean checkSxHistIndForAllegs(Long idStage);

	/**
	 * artf228543
	 * Method Name: hasAllegedPerpetratorWithAgeLessThanTen
	 * Method Description: This method checks if the given stage has any alleged perpetrator
	 * with age less than ten and disposition is not Admin Closure.
	 *
	 * @param idStage
	 * @param idCase
	 * @return boolean
	 */
  public boolean hasAllegedPerpetratorWithAgeLessThanTen (Long idStage, Long idCase) ;

	/**
	 * Method Name:updateSubstanceTracker
	 * Method Description: This method updates substance tracker table during person merge process.
	 * If the idClosedPerson has any records in substance tracker table, then they will be updated to idForwardPerson
	 *
	 * @param idStage         stage id of the where the data needs to be updated
	 * @param idClosedPerson  id of the person being closed
	 * @param idForwardPerson id of the person being forwarded
     */
	public void updateSubstanceTrackerForPersonMerge(Long idStage, Long idClosedPerson, Long idForwardPerson);


	/**
	 * Method Name:updateVictimOnClosureLetters
	 * Method Description: This method updates SchoolInvNotifctnVctm table during person merge process.
	 * If the idClosedPerson has any records in SchoolInvNotifctnVctm table, then they will be updated to idForwardPerson
	 *
	 * @param idStages        stage ids of the where the data needs to be updated
	 * @param idClosedPerson  id of the person being closed
	 * @param idForwardPerson id of the person being forwarded
	 */
	public void updateVictimOnClosureLetters(Set<Long> idStages, Long idPersonMergeWorker, Long idClosedPerson, Long idForwardPerson);

}
