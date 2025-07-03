package us.tx.state.dfps.service.childplanparticipant.service;

import us.tx.state.dfps.service.childplan.dto.ChildPlanLegacyDto;
import us.tx.state.dfps.service.common.request.ChildPlanParticipantInReq;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Implements
 * services related to ChildPlan. Implementation for Csub36sBean.java Oct 10,
 * 2017- 3:08:03 PM © 2017 Texas Department of Family and Protective Services
 */
public interface ChildPlanParticipantService {

	/**
	 * Method Name: childPlanParticipantFetch Method Description: This method is
	 * used to get the child plan participants
	 * 
	 * @param idChildPlanEvent
	 * @return void
	 */
	public void fetchChildPlanParticipants(Long idChildPlanEvent, ChildPlanLegacyDto childPlanLegacyDto);

	/**
	 * Calls methods for inserting and updating Child Plan participant table
	 * 
	 * @param caud27di
	 * @
	 */
	public Long saveOrUpdateChildPlanParticip(ChildPlanParticipantInReq childPlanParticipantInReq);

	/**
	 * Calls methods for deleting Child Plan participant table
	 * 
	 * @param caud27di
	 * @
	 */
	public String deleteChildPlanParticip(ChildPlanParticipantInReq childPlanParticipantInReq);
}
