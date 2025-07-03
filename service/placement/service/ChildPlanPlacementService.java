package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PlanPlacement info of child's service Jan 27, 2018- 1:26:14 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ChildPlanPlacementService {
	/**
	 * 
	 * Method Name: getChildPlanPlacement Method Description: This method is
	 * used for retrieving the PlacementInfo of Child's Service Plan
	 * 
	 * @param idEvent
	 * @param idStage
	 * @return ChildPlanPlacementDto @
	 */
	public PreFillDataServiceDto getChildPlanPlacement(Long idEvent, Long idStage);
}
