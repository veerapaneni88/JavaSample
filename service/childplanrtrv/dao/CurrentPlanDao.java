package us.tx.state.dfps.service.childplanrtrv.dao;

import us.tx.state.dfps.service.childplan.dto.ChildPlanLegacyDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This is the
 * interface for CurrentPlanDaoImpl Oct 11, 2017- 12:30:58 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CurrentPlanDao {
	/**
	 * 
	 * Method Name: queryCdyn09 Method Description:This methods returns the list
	 * of topics applicable for current selected plan
	 * 
	 * @param childPlanDetailsDto
	 * @return ChildPlnDto
	 * @throws DataNotFoundException
	 */
	public void getCurrentlySelectedPlans(Long idChildPlanEvent, String cdCspPlanType,
			ChildPlanLegacyDto childPlanLegacyDto);
}
