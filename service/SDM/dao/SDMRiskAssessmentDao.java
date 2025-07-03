package us.tx.state.dfps.service.SDM.dao;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.SDMRiskAssessmentRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentAnswerDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssmtSecondaryFollowupDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;

public interface SDMRiskAssessmentDao {

	/**
	 * @param eventDto
	 * @return
	 */
	public long updateEventStatus(EventValueDto eventValueDto);

	/**
	 * @param sdmRiskAssessmentDto
	 * @return
	 */
	public long addAssessmentCompletedDate(SDMRiskAssessmentDto sdmRiskAssessmentDto);

	/**
	 * @param sDMRiskAssessmentdto
	 * @return @
	 */
	public SDMRiskAssessmentDto addRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	/**
	 * @param SDMRiskAssessmentAnswerDB
	 * @return
	 */
	public long addRiskResponse(SDMRiskAssessmentAnswerDto answerDB);

	/**
	 * @param SDMRiskAssessmentDto
	 * @return
	 */
	public long updateScoresAndRiskLevels(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	/**
	 * @param SDMRiskAssessmentAnswerDB
	 * @return
	 */
	public long updateRiskResponse(SDMRiskAssessmentAnswerDto answerDB);

	/**
	 * @param SDMRiskAssessmentFollowupDB
	 * @return
	 */
	public long addRiskFollowupResponse(SDMRiskAssessmentFollowupDto followupDB);

	/**
	 * @param SDMRiskAssmtSecondaryFollowupDB
	 * @return
	 */
	public long addRiskSecondFollowupResponse(SDMRiskAssmtSecondaryFollowupDto secFollowupDB);

	/**
	 * @param SDMRiskAssmtSecondaryFollowupDB
	 * @return
	 */
	public long updateRiskSecondFollowupResponse(SDMRiskAssmtSecondaryFollowupDto secFollowupDB);

	/**
	 * @param SDMRiskAssessmentFollowupDB
	 * @return
	 */
	public long updateRiskFollowupResponse(SDMRiskAssessmentFollowupDto followupDB);

	/**
	 * @param stageValueBean
	 * @param stageId
	 * @return
	 */
	public long updatePrimarySecondaryCaretaker(StagePersonValueDto stageValueBean, long stageId);

	String deleteRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	SDMRiskAssessmentDto getHouseholdName(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	/**
	 * Gets primary and secondary caregiver names for a risk assessment
	 * @param idAsmntCpsRa
	 * @return
	 */
	SDMRiskAssessmentDto getCaregiverNames(int idAsmntCpsRa);

	SDMRiskAssessmentRes saveHouseholdDtl(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	/**
	 * Method Name: getExistingHRAForHousehold Method Description:
	 * 
	 * @param retrieveHouseHoldForCaseReq
	 * @return
	 */
	public SDMRiskAssessmentRes getExistingRAForHousehold(SDMRiskAssessmentDto retrieveHouseHoldForCaseReq);

	/**
	 * 
	 * Method Name: queryRiskAssessment Method Description:Returns Risk
	 * Assessment DataBean based on Risk Assessment Event Id and Stage Id It
	 * pulls back the questions, answers, followups, secondary followups and
	 * responses
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return
	 */
	public SDMRiskAssessmentDto queryRiskAssessment(Long idEvent, Long idStage);

	/**
	 * 
	 * Method Name: getStageIdList Method Description:
	 * 
	 * @param idStage
	 * @param idPrimary
	 * @param idSecondary
	 * @return
	 */
	public Map<String, List<IntakeAllegationDto>> getStageIdList(Long idStage, Long idPrimary, Long idSecondary);

	/**
	 * 
	 * Method Name: getCareGiverHistoryList Method Description:List of Primary
	 * parent/caregiver who has a history of abuse or neglect as a child
	 * 
	 * @param idStage
	 * @param idPerson
	 * @return
	 */
	public List<IntakeAllegationDto> getCareGiverHistoryList(Long idStage, Long idPerson);

	/**
	 * 
	 * Method Name: getPriorNeglectList Method Description: List of StageDB with
	 * prior neglect history
	 * 
	 * @param stageId
	 * @return
	 */
	public List<IntakeAllegationDto> getPriorNeglectList(Long stageId);

	/**
	 * 
	 * Method Name: getPriorAbuseList Method Description: List of stageDB with
	 * prior abuse history
	 * 
	 * @param idStage
	 * @return
	 */
	public List<IntakeAllegationDto> getPriorAbuseList(Long idStage);

	/**
	 * Method Name: getRiskAssessment. Method Description: This method gets the
	 * cpa_ra record for input idStage
	 * 
	 * @param idStage
	 * @return SDMRiskAssessmentDto
	 */
	public SDMRiskAssessmentDto getRiskAssessment(Long idStage);

	/**
	 * Method Name: queryRiskAssessmentDtls. Method Description: This service is
	 * to retrieve if Risk Assessment details exists and the event status of
	 * Risk Assessment.
	 * 
	 * @param riskAssmtValueDto
	 * @return SDMRiskAssessmentDto
	 */
	public SDMRiskAssessmentDto queryRiskAssessmentExists(SDMRiskAssessmentDto sdmRiskAssessmentDto);

	/**
	 * Method Name: getPersonDetails Method Description:Retrieves the list of
	 * person names from the PERSON table
	 * 
	 * @param sdmRiskAssessmentDBDto
	 * @param ulIdStage
	 * @return SDMRiskAssessmentDto
	 */
	public SDMRiskAssessmentDto getPersonDetails(SDMRiskAssessmentDto sdmRiskAssessmentDto, Long idStage);

	/**
	 * Method Name:queryPageData Method Description:Returns Risk Assessment
	 * Databean to build a New Risk Assessment form in Impact.
	 * 
	 * @param idStage
	 * @return SDMRiskAssessmentDto
	 */
	public SDMRiskAssessmentDto queryPageData(Long idStage);

	/**
	 * Method Name:getPrimaryCreGivrHistoryCount Method Description:Returns
	 * primary CareGiver History Count
	 * 
	 * @param idStage,idPrimaryCaregiver
	 * @return List<StagePersonValueDto>
	 */
	public List<StagePersonValueDto> getPrimaryCreGivrHistoryCount(Long idPrimaryCaregiver, Long idStage);

	/**
	 * Method Name:getSecondaryCreGivrHistoryCount Method Description:Returns
	 * secondary CareGiver History Count
	 * 
	 * @param idStage,idSecondaryCaregiver
	 * @return List<StagePersonValueDto>
	 */
	public List<StagePersonValueDto> getSecondaryCreGivrHistoryCount(Long idSecondaryCaregiver, Long idStage);

	/**
	 * Method Name:retrieveSafetyAssmtData Method Description : This method is
	 * called from display method in SafetyAssmtConversation if the page has
	 * been previously saved. It retrives back all the responses
	 * 
	 * @param safetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	public SafetyAssessmentRes retrieveSafetyAssmtData(SafetyAssessmentReq safetyAssessmentReq);

	/**
	 * Method Name:getsubStageOpen Method Description:returns boolean value for
	 * subStage
	 * 
	 * @param idCase
	 * @return boolean
	 */
	public boolean getsubStageOpen(Long idCase);

	/**
	 * Method Name:getCurrentEventStatus Method Description:returns the current
	 * event Status
	 * 
	 * @param idStage
	 * @param idCase
	 * @return String
	 */
	public String getCurrentEventStatus(Long idStage, Long idCase);

	public long updateRiskAsmnt(SDMRiskAssessmentDto sDMRiskAssessmentdto);
	

     /**
 	 * Method Name:getStageHouseholdSDMRAEvent Method Description:returns the event id of
     * Risk Assessment for the household identified for the stage closure.
     *
 	 * @param idStage
 	 * @return Long
 	 */
	
 	public Long getStageHouseholdSDMRAEvent(long idStage); // Defect # 9159 Code changes for the right Risk Assessment to be pulled

	
}
