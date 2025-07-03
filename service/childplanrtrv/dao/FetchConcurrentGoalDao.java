
package us.tx.state.dfps.service.childplanrtrv.dao;

import java.util.List;

import us.tx.state.dfps.service.childplan.dto.ConcurrentGoalDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * interface for FetchConcurrentGoalDaoImpl Oct 11, 2017- 12:35:30 PM © 2017
 * Texas Department of Family and Protective Services
 */
public interface FetchConcurrentGoalDao {

	/**
	 * Method Name: getConcurrentGoals Method Description: This method is used
	 * to getConcurrentGoals
	 * 
	 * @param idChildPlanEvent
	 * @return List<ConcurrentGoalDto>
	 */
	public List<ConcurrentGoalDto> getConcurrentGoals(Long idChildPlanEvent);
}
