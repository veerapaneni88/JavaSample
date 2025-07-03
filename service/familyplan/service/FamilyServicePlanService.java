package us.tx.state.dfps.service.familyplan.service;

import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method CSVC08S May 2, 2018- 4:28:18 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FamilyServicePlanService {

	/**
	 * Method Name: getServicePlan Method Description: Makes dao calls and
	 * returns prefill string for Family Service Plan
	 * 
	 * @param req
	 * @return
	 */
	public PreFillDataServiceDto getServicePlan(CommonApplicationReq req);

}
