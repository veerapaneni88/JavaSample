package us.tx.state.dfps.service.childplanrtrv.dao;

public interface ChildPlanNarrativeDao {
	/**
	 * Method Name: insertChildPlanNarrative
	 *
	 * Method Description: Retrieve all Child Plan Narrative given an
	 * ID_CHILD_PLAN_EVENT (old) and insert them into the appropriate table
	 * (determined by the CP_PLAN_TYPE passed in) with the new
	 * ID_CHILD_PLAN_EVENT passed in.
	 * 
	 * @param cdCspPlanType
	 * @param idChildPlanEvent
	 * @param idChildPlanEventNew
	 */
	public void insertChildPlanNarrative(String cdCspPlanType, Long idChildPlanEvent, Long idChildPlanEventNew);

	/**
	 * Method Name: deleteChildPlanNarrative
	 *
	 * Method Description: Delete all records (for a given ID_EVENT) from tables
	 * derived from the difference between 2 Plan Types
	 * 
	 * @param cdCspPlanTypeOld
	 * @param cdCspPlanTypeNew
	 * @param idChildPlanEventNew
	 */
	public void deleteChildPlanNarrative(String cdCspPlanTypeOld, String cdCspPlanTypeNew, Long idChildPlanEventNew);

}
