package us.tx.state.dfps.service.childplan.service;

import us.tx.state.dfps.common.dto.ChildPlanParticipantDto;
import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.common.response.ChildPlanParticipRes;
import us.tx.state.dfps.service.common.response.ChildPlanRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ChildPlanService May 16, 2018- 12:25:25 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface ChildPlanService {

	/**
	 * Method Name: queryChildPlan Method Description:
	 * 
	 * @param childPlanBean
	 * @return ChildPlanRes @
	 */
	public ChildPlanRes getChildPlan(ChildPlanReq childPlanBean);

	/**
	 * 
	 * Method Name: queryPlanTypeCode Method Description: Query the plan type
	 * code for the given child plan.
	 * 
	 * @param caseId
	 * @param eventId
	 * @return String @
	 */
	public String getPlanTypeCode(Long caseId, Long eventId);

	/**
	 * 
	 * Method Name: checkIfEventIsLegacy Method Description: Queries a row from
	 * the EVENT_PLAN_LINK for the given event id to determine whether or not
	 * the event is a legacy event--one created before the initial launch of
	 * IMPACT.
	 * 
	 * @param eventId
	 * @return Boolean @
	 */
	public Boolean checkIfEventIsLegacy(Long eventId);

	/**
	 * Method Name: deleteChildPlan Method Description:This Method is used to
	 * delete the child plan based The event id of the child plan.
	 * 
	 * @param idEvent
	 * @param ChildPlanLegacyDto
	 */
	public void deleteChildPlan(ChildPlanReq childPlanReq);
	/**
	 * Method Name: deleteChildPlanByEvent Method Description:This Method is used to
	 * delete the child plan based The event id of the child plan.
	 *
	 * @param childPlanReq
	 */
	public void deleteChildPlanByEvent(ChildPlanReq childPlanReq);
	/**
	 * Method Name: childPlanNewUsingSave Method Description: This method is
	 * used to save Child Plan after New Using
	 * 
	 * @param childPlanReq
	 * @return ChildPlanRes
	 */
	public ChildPlanRes childPlanNewUsingSave(ChildPlanReq childPlanReq);

	/**
	 * Method Name: childPlanSave Method Description: This method is used to
	 * Save child Plan
	 * 
	 * @param childPlanReq
	 * @return ChildPlanRes
	 */
	public ChildPlanRes childPlanSave(ChildPlanReq childPlanReq);

	/**
	 * Method Name: deleteChildPlan Method Description:.
	 * 
	 * 
	 * @param childPlanParticipDto
	 */
	public ChildPlanParticipRes saveChildPlanParticip(ChildPlanParticipantDto childPlanParticipDto);

	/**
	 * Method Name: deleteChildPlanParticip Method Description:This Method is
	 * used to delete the child plan Participation
	 * 
	 * 
	 * @param childPlanParticipDto
	 */

	public String deleteChildPlanParticip(ChildPlanParticipantDto childPlanParticipDto);

	/**
	 * Method Name: staffSearchResultInformation Method Description:
	 * 
	 * 
	 * @param childPlanParticipDto
	 */

	public ChildPlanParticipRes staffSearchResultInformation(Long idStage);

	/**
	 * Method Name: fetchChildPlanParticipant Method Description:
	 * 
	 * 
	 * @param childPlanParticipDto
	 */

	public ChildPlanParticipRes fetchChildPlanParticipant(Long idEvent, Long idChildPlanPart);

	/**
	 * Method Name: saveChildPlan Method Description: This method is used to
	 * saveChildPlan
	 * 
	 * @param childPlanReq
	 * @return ChildPlanRes
	 */
	public ChildPlanRes saveChildPlan(ChildPlanReq childPlanReq);

	public ChildPlanParticipantDto fetchSsccChildPlanParticipant(Long idSsccChildPlanParticip);

}
