package us.tx.state.dfps.service.childplanrtrv.dao;

import us.tx.state.dfps.service.childplan.dto.ChildPlanEventDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * FetchPlanDao May 8, 2018- 1:16:56 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface FetchPlanDao {
	/**
	 * 
	 * Method Name: queryCses19 Method Description: Queries the Child Plan table
	 * to get the info
	 * 
	 * @param childPlnRtDto
	 * @return ChildPlanEventDto
	 */
	public ChildPlanEventDto getChildPlanEvent(Long idChildPlanEvent);
}
