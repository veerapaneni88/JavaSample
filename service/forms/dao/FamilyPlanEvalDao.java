/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 7, 2018- 10:40:57 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.forms.dto.FamilyAssmtFactDto;
import us.tx.state.dfps.service.forms.dto.FamilyChildNameGaolDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvaItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanGoalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanParticipantsDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.person.dto.EventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 7, 2018- 10:40:57 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FamilyPlanEvalDao {

	/**
	 * 
	 * Method Name: getFamilyPlanTable Method Description:(CSVC41D) Queries
	 * FAMILY_PLAN table.
	 * 
	 * @param idEvent2
	 * @param idFamPlanEval
	 * 
	 * @param idFamilyPlanEvaluation
	 * @return @
	 */
	FamilyPlanDto getFamilyPlanTable(Long idEvent);

	/**
	 * 
	 * Method Name: getFamilyPlanEvalTable Method Description:(CSVC43D) Queries
	 * FAMILY_PLAN_EVAL table.
	 * 
	 * @param idEvent
	 * @return @
	 */
	FamilyPlanEvalDto getFamilyPlanEvalTable(Long idEvent);

	/**
	 * 
	 * Method Name: getFamilyPlanEvalItems Method Description:(CSVC40D) Queries
	 * the FAMILY_PLAN_EVAL_ITEM table to get the evaluation items.
	 * 
	 * @param idEvent
	 * @return @
	 */
	List<FamilyPlanEvaItemDto> getFamilyPlanEvalItems(Long idEvent);

	/**
	 * 
	 * Method Name: getDistinctPricipleNames Method Description:(CLSC23D) Simple
	 * retrieval of a list of distinct principal names from table NAME,
	 * FAMILY_ASSMT_FACTORS for a given ID_FAM_ASSMT_EVENT
	 * 
	 * @param sysdate
	 * @param effectiveDate
	 * 
	 * @return @
	 */
	List<FamilyAssmtFactDto> getDistinctPricipleNames(Long idSvcPlanEvent, Date effectiveDate);

	/**
	 * 
	 * Method Name: getFamilyChildNameGaol Method Description:(CLSCB5D) This
	 * will be used to query the EVENT_PERSON_LINK, PERSON, STAGE_PERSON_LINK,
	 * and STAGE tables to retrieve the name and goal of child in a family plan.
	 * 
	 * @return @
	 */
	List<FamilyChildNameGaolDto> getFamilyChildNameGaol(Long idEvent);

	/**
	 * 
	 * Method Name: getFamilyPlanParticipants Method Description:(CLSC24D)
	 * Retrieval of the list FAMILY PLAN Participants.
	 * 
	 * @param idSvcPlanEvent
	 * @param effectiveDate
	 * @param idEvent
	 * @return @
	 */
	List<FamilyPlanParticipantsDto> getFamilyPlanParticipants(Long idSvcPlanEvent, Date effectiveDate);

	/**
	 * 
	 * Method Name: getFamilyPlanGoals Method Description:(CSVC56D) Retrieval of
	 * FAMILY PLAN GOALS list. FAMILY_PLAN_GOAL.TXT_GOAL
	 * 
	 * @param idEvent
	 * @return @
	 */
	List<FamilyPlanGoalDto> getFamilyPlanGoals(Long idEvent);

	/**
	 * 
	 * Method Name: getFamilyPlanTasks Method Description:(CSVC52D) Queries the
	 * FAMILY_PLAN_TASK table.
	 * 
	 * @param idEvent
	 * @return @
	 */
	List<FamilyPlanTaskDto> getFamilyPlanTasks(Long idEvent, Long idSvcPlanEvent);

	/**
	 * 
	 * Method Name: getInitialConcernsPlan Method Description:(CSVC54D) Queries
	 * the FAMILY_PLAN_ITEM table to select Initial Concerns of a PLAN
	 * 
	 * @return @
	 */
	List<FamilyPlanItemDto> getInitialConcernsPlan(Long idEvent, String dtApr08Rollout);

	/**
	 * 
	 * Method Name: getFamDateComplete Method Description:(CSVC58D) Queries the
	 * FAMILY_PLAN_EVAL table to get the Dt Completed of every evaluation for a
	 * given family plan.
	 * 
	 * @param idEvent
	 * @return @
	 */
	List<FamilyPlanEvalDto> getFamDateComplete(Long idEvent);

	/**
	 * 
	 * Method Name: getIndicatartorVals Method Description:(CSESC8D) This DAM
	 * will select for indicator values for a family plan event created
	 * before/after 27Apr08.
	 * 
	 * @param dtEventOccurred
	 * @return @
	 */
	EventDto getIndicatartorVals(String dtApr08Rollout, String version3, String version2, Long idEvent);

	/**
	 * 
	 * Method Name: getDateCompletedEval Method Description:(CSVC53D) Queries
	 * the FAMILY_PLAN and the FAMILY_PLAN_EVAL tables to get the Dt Completed
	 * of the plan and every evaluation for a given family plan.
	 * 
	 * @param idEvent
	 * @param rollOut
	 * @return @
	 */
	List<FamilyPlanDto> getDateCompletedEval(Long idEvent, String dtApr08Rollout);

	/**
	 * 
	 * Method Name: getInitialConcerns Method Description:(CSVC55D) Queries the
	 * FAMILY_PLAN_ITEM table to select Initial Concerns of a PLAN and queries
	 * the FAMILY_PLAN and FAMILY_PLAN_EVAL tables for DT_COMPLETED which is
	 * passed to the form as DT_INITIALLY_ADDRESSED.
	 * 
	 * @param idEvent
	 * @param rollOut
	 * @return @
	 */
	List<FamilyPlanDto> getInitialConcerns(Long idEvent, String rollOut);

}
