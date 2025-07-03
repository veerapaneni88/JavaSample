package us.tx.state.dfps.service.childplan.service;

import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for ChildPlanService Nov 9, 2017- 10:45:00 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ChildServicePlanFormService {

	/**
	 * 
	 * Method Name:getChildServicesPlan Method Description: The method provides
	 * detailed case plans for providing services to children in substitute care
	 * and their families
	 * 
	 * @param childPlanReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getChildServicesPlan(ChildPlanReq childPlanReq);

}
