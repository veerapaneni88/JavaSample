package us.tx.state.dfps.service.SDM.dao;

import java.util.List;

import us.tx.state.dfps.riskandsafetyassmt.dto.SafetyFactorEvalDto;
import us.tx.state.dfps.riskandsafetyassmt.dto.SafteyEvalResDto;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyAssessmentDto;

public interface SafetyEvalDao {

	public List<SafetyFactorEvalDto> getSafetyEvalFactors(Long idEvent);

	public SafteyEvalResDto getSafetyEval(Long idEvent);

	/**
	 * Method Name: getRecentSafetyEval Method Description: cinva2d - This DAM
	 * determines whether the ID EVENT Passed in corresponds to the most recent
	 * Safety Eval
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return idEvent
	 */

	public Long getRecentSafetyEval(Long idEvent, Long idStage);

	/**
	 * Method Name: getCurrentEventId Method Description:Fetches back an eventId
	 * for a given combination of caseId and stageId.
	 * 
	 * @param idStage
	 * @param idCase
	 * @return
	 */
	public Long getCurrentEventId(Long idStage, Long idCase);

	/**
	 * Method Name: retrieveSafetyAssessmentData Method Description:If an
	 * event-id exists for a given combination of stageid and caseid, then this
	 * method is called.This retrieves the Safety assessment data.
	 * 
	 * @param idStage
	 * @param idEvent
	 * @param idCase
	 * @return
	 */
	public SafetyAssessmentDto retrieveSafetyAssessmentData(Long idStage, Long idEvent, Long idCase);

	/**
	 * Method Name: getCurrentEventStatus Method Description:Returns back a
	 * string containing current Event status
	 * 
	 * @param idStage
	 * @param idCase
	 * @return
	 */
	public String getCurrentEventStatus(Long idStage, Long idCase);

	/**
	 * Method Name: getSubStageOpen Method Description:Returns true if SubStage
	 * is Open else returns false
	 * 
	 * @param idCase
	 * @return
	 */
	public boolean getSubStageOpen(Long idCase);

	/**
	 * Method Name: getQueryPgData Method Description:Retrieve the data needed
	 * to build the Safety Assessment page.
	 * 
	 * @param safetyAssessmentReq
	 * @return
	 */
	public SafetyAssessmentRes getQueryPgData(SafetyAssessmentReq safetyAssessmentReq);

}
