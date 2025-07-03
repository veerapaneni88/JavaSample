package us.tx.state.dfps.service.familyplan.dao;

import java.util.List;

import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.familyTree.bean.FamilyPlanGoalValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to query the Family Plan Goal Oct 30, 2017- 3:02:27 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FamilyPlanGoalDao {

	/**
	 * Method Name: queryFGDMFamilyGoal Method Description: This method is used
	 * to query the Family Plan Goal
	 *
	 * @param idCase
	 * @param idEvent
	 * @return List<FamilyPlanGoalValueDto>
	 */
	public List<FamilyPlanGoalValueDto> queryFGDMGoal(Long idCase, Long idEvent);

	/**
	 * Method Name: getAreaOfConcernList Method Description: This method returns
	 * a list of Area of Concern for a family plan goal
	 *
	 * @param idFamilyPlanGoal
	 * @return List<String>
	 */
	public List<String> getAreaOfConcernList(Long idFamilyPlanGoal);

	/**
	 * Method Name: deleteFGDMGoal Method Description: This method is used to
	 * delete a family plan goal from FAMILY_PLAN_TABLE, also entry in the
	 * FAM_PLN_AOC_GOAL_LINK and FAM_PLN_TASK_GOAL_LINK is deleted.
	 * 
	 * @param familyPlanGoalValueDto
	 * @return Long
	 * 
	 */
	public Long deleteFGDMGoal(FamilyPlanGoalValueDto familyPlanGoalValueDto);

	/**
	 * Method Name: saveOrUpdateFamilyPlanGoal Method Description: save the
	 * family goals with goal txt and area of concerns associated with a
	 * particular Family Plan event.
	 * 
	 * @param familyPlanGoalValueDtos
	 * @param idEvent
	 * @param idCase
	 * @return String
	 */
	FamilyPlanRes saveOrUpdateFamilyPlanGoal(List<FamilyPlanGoalValueDto> familyPlanGoalValueDtos, Long idEvent,
			Long idCase);

	/**
	 * Method Name: deleteFamPlanGoal Method Description:
	 * 
	 * @param idGoal
	 * @return
	 */
	String deleteFamPlanGoal(Long idGoal);

}
