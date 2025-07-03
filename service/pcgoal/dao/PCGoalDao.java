package us.tx.state.dfps.service.pcgoal.dao;

import java.util.List;

import us.tx.state.dfps.service.pcgoal.dto.PglDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: dao class
 * for CCMN56S to populate form CCMN0100 Mar 5, 2018- 11:45:30 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PCGoalDao {

	/**
	 * Method Name: getPglDetailsDto Method Description: Retrieves
	 * PERM_GOAL_LOOKUP static table data
	 * 
	 * @return List<PglDetailDto>
	 * @throws DataNotFoundException
	 */
	public List<PglDetailDto> getPglDetailsDto();

}